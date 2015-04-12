/*
 * This file is part of JTimber.
 * Copyright (c) 2015 QuarterCode <http://quartercode.com/>
 *
 * JTimber is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JTimber is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JTimber. If not, see <http://www.gnu.org/licenses/>.
 */

package com.quartercode.jtimber.rh.agent.asm;

import static org.objectweb.asm.Opcodes.*;
import java.util.HashSet;
import java.util.Set;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import com.quartercode.jtimber.rh.agent.util.AnnotatedFieldRecorder;

/**
 * The {@link ClassVisitor} which adds so called "@Weak watchers" to nodes in order to implement to weak field discarding if the field's parent count is {@code 0}.
 * Note that it transforms all classes that are fed into it.
 * Therefore, only node classes should be sent through it.
 */
public final class InsertWeakRefWatcherClassAdapter extends CommonBaseClassAdapter {

    private static final Type   WEAK_CLASS         = Type.getObjectType("com/quartercode/jtimber/api/node/Weak");
    private static final String PARENT_AWARE_CLASS = "com/quartercode/jtimber/api/node/ParentAware";

    private final Set<String>   weakFields         = new HashSet<>();

    /**
     * Creates a new insert weak reference watcher class adapter.
     * 
     * @param cv The class visitor to which this visitor delegates method calls. May be {@code null}.
     */
    public InsertWeakRefWatcherClassAdapter(ClassVisitor cv) {

        super(cv);
    }

    @Override
    public FieldVisitor visitField(int access, final String name, String desc, String signature, Object value) {

        // Return an AnnotatedFieldRecorder which adds all fields annotated with "@Weak" to the "weakFields" set
        FieldVisitor fv = super.visitField(access, name, desc, signature, value);
        if (fv != null) {
            fv = new AnnotatedFieldRecorder(fv, name, WEAK_CLASS, weakFields);
        }
        return fv;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

        // Return an InsertWeakRefWatcherMethodAdapter
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (mv != null) {
            mv = new InsertWeakRefWatcherMethodAdapter(mv);
        }
        return mv;
    }

    /*
     * The method adapter internally used by the InsertWeakRefWatcherClassAdapter.
     * It adds weak reference watcher instructions around each GETFIELD instruction that accesses a @Weak field.
     */
    private final class InsertWeakRefWatcherMethodAdapter extends MethodVisitor {

        private InsertWeakRefWatcherMethodAdapter(MethodVisitor mv) {

            super(ASM5, mv);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String desc) {

            /*
             * If this is a GETFIELD instruction, the field is weak (@Weak annotation), the field references an object and the field is located in this class,
             * add the weak reference watcher code around the instruction.
             * Note that the instructions inside this block make sure to reconstruct the original stack.
             */
            if (opcode == GETFIELD && weakFields.contains(name) && Type.getType(desc).getSort() == Type.OBJECT && owner.equals(classType.getInternalName())) {
                /*
                 * If a parent-aware object is already present in the field, remove "this" from its parents.
                 */

                // Skip the if-block if the condition below isn't true
                Label endIf = new Label();

                // Push the object from the accessed field
                super.visitVarInsn(ALOAD, 0);
                super.visitFieldInsn(GETFIELD, owner, name, desc);

                // ----- Stack: [field]

                // Skip to the end if the object isn't parent-aware
                super.visitTypeInsn(INSTANCEOF, PARENT_AWARE_CLASS);
                super.visitJumpInsn(IFEQ, endIf);

                // Push the object from the accessed field once again
                super.visitVarInsn(ALOAD, 0);
                super.visitFieldInsn(GETFIELD, owner, name, desc);

                // ----- Stack: [field]

                // Skip to the end if the object's parent count isn't 0
                // super.visitTypeInsn(CHECKCAST, "com/quartercode/jtimber/api/node/ParentAware");
                super.visitMethodInsn(INVOKEINTERFACE, PARENT_AWARE_CLASS, "getParentCount", "()I", true);
                super.visitJumpInsn(IFNE, endIf);

                /* if (object instanceof ParentAware && ((ParentAware) object).getParentCount() == 0) */
                {
                    // Store "null" in the field
                    super.visitVarInsn(ALOAD, 0);
                    super.visitInsn(ACONST_NULL);
                    // ----- Stack: [this, null]
                    super.visitFieldInsn(PUTFIELD, owner, name, desc);
                }

                super.visitLabel(endIf);

                // ----- Stack: []
            }

            // Write the actual field instruction by calling the next visitor
            super.visitFieldInsn(opcode, owner, name, desc);

        }

    }

}
