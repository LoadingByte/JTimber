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

package com.quartercode.jtimber.rh.agent.asm.transform;

import static org.objectweb.asm.Opcodes.ASM5;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import com.quartercode.jtimber.rh.agent.asm.ClassMetadata;
import com.quartercode.jtimber.rh.agent.asm.MetadataAwareClassVisitor;
import com.quartercode.jtimber.rh.agent.asm.util.ASMUtils;

/**
 * The {@link ClassVisitor} which adds so called "wrapper substitutions" to nodes in order to wrap all field values, which are annotated with {@code @SubstituteWithWrapper},
 * into the specified wrapper.
 * Therefore, no specific wrapper creation is necessary (e.g. {@code new ListWrapper(...)} can be omitted).
 * Note that it transforms all classes that are fed into it.
 * Therefore, only node classes should be sent through it.
 */
public final class InsertWrapperSubstitutionClassTransformer extends MetadataAwareClassVisitor {

    /**
     * Creates a new insert wrapper substitution class transformer.
     * 
     * @param cv The class visitor to which this visitor delegates method calls. May be {@code null}.
     * @param metadata The {@link ClassMetadata} object the transformer uses to retrieve metadata about the processed class.
     */
    public InsertWrapperSubstitutionClassTransformer(ClassVisitor cv, ClassMetadata metadata) {

        super(cv, metadata);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

        // Return an InsertWrapperSubstitutionMethodAdapter
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (mv != null) {
            mv = new InsertWrapperSubstitutionMethodAdapter(mv, access, name, desc);
        }
        return mv;
    }

    /*
     * The method adapter internally used by the InsertWrapperSubstitutionClassTransformer.
     * It adds wrapper substitution instructions around each PUTFIELD instruction.
     */
    private final class InsertWrapperSubstitutionMethodAdapter extends GeneratorAdapter {

        private InsertWrapperSubstitutionMethodAdapter(MethodVisitor mv, int access, String name, String desc) {

            super(ASM5, mv, access, name, desc);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String desc) {

            /*
             * If this is a PUTFIELD instruction, the field has a @SubstituteWithWrapper annotation, the field references an object and the field is located in this class,
             * add the wrapper substitution code around the instruction.
             * Note that the instructions inside this block make sure to reconstruct the original stack.
             */
            if (opcode == PUTFIELD && metadata.wrapperSubstitutedFields.containsKey(name) && Type.getType(desc).getSort() == Type.OBJECT && owner.equals(metadata.classType.getInternalName())) {
                Pair<Type, Type> wsField = metadata.wrapperSubstitutedFields.get(name);
                Type wrapperType = wsField.getLeft();
                // Null means "default"; in that case, the field type is used as the type of the first argument of the wrapper constructor
                Type wrapperConstructorArgType = wsField.getRight() == null ? metadata.fields.get(name) : wsField.getRight();

                // Substitute the top stack value for the PUTFIELD instruction with a wrapper
                ASMUtils.generateWrapperSubstitution(this, wrapperType, wrapperConstructorArgType);
            }

            // Write the actual field instruction by calling the next visitor
            super.visitFieldInsn(opcode, owner, name, desc);
        }

    }

}
