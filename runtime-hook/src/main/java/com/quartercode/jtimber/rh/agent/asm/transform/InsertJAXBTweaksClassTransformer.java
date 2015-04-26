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

import static org.objectweb.asm.Opcodes.*;
import java.util.Map.Entry;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;
import com.quartercode.jtimber.rh.agent.asm.ClassMetadata;
import com.quartercode.jtimber.rh.agent.asm.MetadataAwareClassVisitor;
import com.quartercode.jtimber.rh.agent.asm.util.ASMUtils;

/**
 * The {@link ClassVisitor} which tweaks all node classes in order to properly support JAXB persistence.
 * The visitor adds or modifies the {@code afterUnmarshal()} method in order for it to execute the following actions:
 * 
 * <ol>
 * <li>Wrap all field values, which are annotated with {@code @SubstituteWithWrapper}, into the specified wrapper and set the fields to that wrapper.</li>
 * <li>Call the {@code addParent()} method (with {@code this} as the first argument) on all fields which are parent-aware.</li>
 * </ol>
 * 
 * Note that the class visitor transforms all classes that are fed into it.
 * Therefore, only node classes should be sent through it.
 */
public final class InsertJAXBTweaksClassTransformer extends MetadataAwareClassVisitor {

    private static final Method AFTER_UNMARSHAL_METHOD       = Method.getMethod("void afterUnmarshal (javax.xml.bind.Unmarshaller, java.lang.Object)");
    private static final Method ADDED_AFTER_UNMARSHAL_METHOD = Method.getMethod("void afterUnmarshal_jtimber (javax.xml.bind.Unmarshaller, java.lang.Object)");

    private boolean             containsCustomAfterUnmarshalMethod;

    /**
     * Creates a new insert JAXB tweaks class transformer.
     * 
     * @param cv The class visitor to which this visitor delegates method calls. May be {@code null}.
     * @param metadata The {@link ClassMetadata} object the transformer uses to retrieve metadata about the processed class.
     */
    public InsertJAXBTweaksClassTransformer(ClassVisitor cv, ClassMetadata metadata) {

        super(cv, metadata);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

        // If the method is called afterUnmarshal(), insert a call to another method afterUnmarshal_jtimber() as the first instruction and set containsCustomAfterUnmarshalMethod to true
        // Otherwise, just delegate the method to the next class visitor
        if (name.endsWith("afterUnmarshal")) {
            containsCustomAfterUnmarshalMethod = true;

            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (mv != null) {
                mv = new MethodVisitor(ASM5, mv) {

                    @Override
                    public void visitCode() {

                        super.visitCode();

                        // Push this
                        super.visitVarInsn(ALOAD, 0);
                        // Push both arguments
                        super.visitVarInsn(ALOAD, 1);
                        super.visitVarInsn(ALOAD, 2);
                        // Invoke the method
                        super.visitMethodInsn(INVOKEVIRTUAL, metadata.classType.getInternalName(), ADDED_AFTER_UNMARSHAL_METHOD.getName(), ADDED_AFTER_UNMARSHAL_METHOD.getDescriptor(), false);
                    }

                };
            }
            return mv;
        } else {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
    }

    @Override
    public void visitEnd() {

        /*
         * Add the afterUnmarshal() method.
         */

        if (!metadata.getNonWeakFields().isEmpty()) {
            // If the method is necessary ...
            if (containsCustomAfterUnmarshalMethod) {
                // ... and the class already contains afterUnmarshal(), generate the method as afterUnmarshal_jtimber()
                // Note that the new method will be called from the old afterUnmarshal() method using a hook added in visitMethod()
                generateAfterUnmarshalMethod(ADDED_AFTER_UNMARSHAL_METHOD);
            } else {
                // ... and the class doesn't yet contain afterUnmarshal(), generate the method regularly as afterUnmarshal()
                generateAfterUnmarshalMethod(AFTER_UNMARSHAL_METHOD);
            }
        } else if (containsCustomAfterUnmarshalMethod) {
            // If the method is unnecessary, but a call to the method afterUnmarshal_jtimber() has already been added, generate that method as an empty dummy
            generateEmptyAddedAfterUnmarshalMethod();
        }

        super.visitEnd();
    }

    private void generateAfterUnmarshalMethod(Method method) {

        GeneratorAdapter mg = new GeneratorAdapter(ACC_PUBLIC, method, null, null, cv);

        /*
         * Iterate through all fields annotated with "SubstituteWithWrapper" and replace their current value with their current value wrapped inside a wrapper.
         * This section first reads the current field value, then creates a new wrapper which wraps around that field value, and finally sets the field to the wrapper.
         */
        for (Entry<String, Pair<Type, Type>> field : metadata.wrapperSubstitutedFields.entrySet()) {
            String fieldName = field.getKey();
            Type fieldType = metadata.fields.get(fieldName);
            Type wrapperType = field.getValue().getLeft();
            // Null means "default"; in that case, the field type is used as the type of the first argument of the wrapper constructor
            Type wrapperConstructorArgType = field.getValue().getRight() == null ? fieldType : field.getValue().getRight();

            // This "this" reference will be used by the PUTFIELD instruction later on
            mg.loadThis();

            // ----- Stack: [this]

            // Retrieve the current field value (may be null)
            ASMUtils.generateGetField(mg, metadata.classType, fieldName, fieldType);

            // ----- Stack: [this, fieldValue]

            // Substitute the field value with a wrapper if it is not null (null check is done by the ASMUtils method)
            ASMUtils.generateWrapperSubstitution(mg, wrapperType, wrapperConstructorArgType);

            // ----- Stack: [this, wrapper]

            // Store the new wrapper (or null) in the field the value has been retrieved from before
            // The substitution is complete
            mg.putField(metadata.classType, fieldName, fieldType);

            // ----- Stack: []
        }

        /*
         * Iterate through all fields.
         * For each field, call the addParent() method with "this" as parent if the current field value is parent-aware.
         */
        for (String fieldName : metadata.getNonWeakFields()) {
            Type fieldType = metadata.fields.get(fieldName);

            ASMUtils.generateGetField(mg, metadata.classType, fieldName, fieldType);

            // ----- Stack: [fieldValue]

            ASMUtils.generateAddOrRemoveThisAsParent(mg, "addParent");
        }

        // End the method
        mg.returnValue();
        mg.endMethod();
    }

    private void generateEmptyAddedAfterUnmarshalMethod() {

        GeneratorAdapter mg = new GeneratorAdapter(ACC_PUBLIC, ADDED_AFTER_UNMARSHAL_METHOD, null, null, cv);
        mg.returnValue();
        mg.endMethod();
    }

}
