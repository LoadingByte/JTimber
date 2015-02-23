/*
 * This file is part of JTimber.
 * Copyright (c) 2015 QuarterCode <http://www.quartercode.com/>
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
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * The {@link ClassVisitor} which adds so called "parent watchers" to nodes in order to track the parents of parent-aware objects.
 * Note that it transforms all classes that are fed into it.
 * Therefore, only node classes should be sent through it.
 */
public final class InsertParentWatcherClassAdapter extends ClassVisitor {

    /**
     * Creates a new insert parent watcher class adapter.
     * 
     * @param cv The class visitor to which this visitor delegates method calls. May be {@code null}.
     */
    public InsertParentWatcherClassAdapter(ClassVisitor cv) {

        super(ASM5, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

        // Return an InsertParentWatcherClassAdapter
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (mv != null) {
            mv = new InsertParentWatcherMethodAdapter(mv);
        }
        return mv;
    }

    /*
     * The method adapter internally used by the InsertParentWatcherClassAdapter.
     */
    private final class InsertParentWatcherMethodAdapter extends MethodVisitor {

        private static final String PARENT_AWARE_CLASS         = "com/quartercode/jtimber/api/node/ParentAware";
        private static final String PARENT_WATCHER_METHOD_DESC = "(Lcom/quartercode/jtimber/api/node/Node;)V";

        private InsertParentWatcherMethodAdapter(MethodVisitor mv) {

            super(ASM5, mv);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String desc) {

            if (opcode == PUTFIELD) {
                // Write a remove parent instruction to consider any old parent-aware object stored in the field
                visitParentWatcherMethodCall(owner, name, desc, "removeParent");
            }

            // Write the actual field instruction by calling the next visitor
            super.visitFieldInsn(opcode, owner, name, desc);

            if (opcode == PUTFIELD) {
                // Write an add parent instruction to consider any new parent-aware object stored in the field
                visitParentWatcherMethodCall(owner, name, desc, "addParent");
            }
        }

        /*
         * Inserts the bytecode for adding/removing the currently processed node class to/from the parents list of the described field.
         * Additional or removal can be selected by passing "addParent" or "removeParent" as the last argument.
         */
        private void visitParentWatcherMethodCall(String fieldOwner, String fieldName, String fieldDesc, String methodName) {

            Type fieldType = Type.getType(fieldDesc);

            // If the accessed field contains objects and not primitives
            if (fieldType.getSort() == Type.OBJECT) {
                // Push the accessed field by reading it from "this"
                super.visitVarInsn(Opcodes.ALOAD, 0);
                super.visitFieldInsn(GETFIELD, fieldOwner, fieldName, fieldDesc);

                // Test whether the accessed field is not null and parent-aware
                super.visitTypeInsn(INSTANCEOF, PARENT_AWARE_CLASS);

                // Jump over the parent watcher method call if the accessed field is null or not parent-aware
                Label end = new Label();
                super.visitJumpInsn(IFEQ, end);

                // Parent watcher method call
                {
                    // Push the accessed field by reading it from "this" and cast it to the "ParentAware" interface
                    super.visitVarInsn(Opcodes.ALOAD, 0);
                    super.visitFieldInsn(GETFIELD, fieldOwner, fieldName, fieldDesc);
                    super.visitTypeInsn(CHECKCAST, PARENT_AWARE_CLASS);

                    // Push "this" because it will be used as the first argument
                    super.visitVarInsn(Opcodes.ALOAD, 0);

                    // Invoke the method on the accessed field using "this" as the first argument
                    super.visitMethodInsn(INVOKEINTERFACE, PARENT_AWARE_CLASS, methodName, PARENT_WATCHER_METHOD_DESC, true);
                }

                // Marks the end of the parent watcher method call
                super.visitLabel(end);
            }
        }

    }

}
