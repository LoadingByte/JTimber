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
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.tuple.Triple;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;
import com.quartercode.jtimber.rh.agent.util.ASMUtils;
import com.quartercode.jtimber.rh.agent.util.Field;

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
public final class InsertJAXBTweaksClassAdapter extends CommonBaseClassAdapter {

    private static final Type                     WEAK_CLASS                   = Type.getObjectType("com/quartercode/jtimber/api/node/Weak");
    private static final Type                     SWW_CLASS                    = Type.getObjectType("com/quartercode/jtimber/api/node/wrapper/SubstituteWithWrapper");
    private static final Type                     SWW_DEFAULT_CLASS            = Type.getObjectType(SWW_CLASS.getInternalName() + "$Default");

    private static final Method                   AFTER_UNMARSHAL_METHOD       = Method.getMethod("void afterUnmarshal (javax.xml.bind.Unmarshaller, java.lang.Object)");
    private static final Method                   ADDED_AFTER_UNMARSHAL_METHOD = Method.getMethod("void afterUnmarshal_jtimber (javax.xml.bind.Unmarshaller, java.lang.Object)");

    private boolean                               containsCustomAfterUnmarshalMethod;

    private final List<Field>                     fields                       = new ArrayList<>();
    private final List<Triple<Field, Type, Type>> fieldsForWrapperSubstitution = new ArrayList<>();

    /**
     * Creates a new insert JAXB tweaks class adapter.
     * 
     * @param cv The class visitor to which this visitor delegates method calls. May be {@code null}.
     */
    public InsertJAXBTweaksClassAdapter(ClassVisitor cv) {

        super(cv);
    }

    @Override
    public FieldVisitor visitField(int access, final String name, String desc, String signature, Object value) {

        // Create a representation of the field
        final Field field = new Field(name, Type.getType(desc));

        // Add the field to the "fields" list
        fields.add(field);

        // This annotation visitor expects "@SubstituteWithWrapper" annotations, parses them and adds their data to the list "fieldsForWrapperSubstitution"
        final class AnnotationVisitorImpl extends AnnotationVisitor {

            private Type wrapperType;
            private Type wrapperConstructorArgType;

            private AnnotationVisitorImpl(AnnotationVisitor av) {

                super(ASM5, av);
            }

            @Override
            public void visit(String name, Object value) {

                if (name.equals("value")) {
                    wrapperType = (Type) value;
                } else if (name.equals("wrapperConstructorArg")) {
                    wrapperConstructorArgType = (Type) value;

                    if (wrapperConstructorArgType.equals(SWW_DEFAULT_CLASS)) {
                        wrapperConstructorArgType = null;
                    }
                }

            }

            @Override
            public void visitEnd() {

                fieldsForWrapperSubstitution.add(Triple.of(field, wrapperType, wrapperConstructorArgType));
            }

        }

        // This field visitor does two things:
        // - It removes the field from the "fields" list if the "@Weak" annotation is present
        // - It "invokes" the AnnotationVisitorImpl with a possibly found "@SubstituteWithWrapper" annotation
        final class FieldVisitorImpl extends FieldVisitor {

            private FieldVisitorImpl(FieldVisitor fv) {

                super(ASM5, fv);
            }

            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {

                if (desc.equals(WEAK_CLASS.getDescriptor())) {
                    fields.remove(field);
                }

                AnnotationVisitor av = super.visitAnnotation(desc, visible);
                if (desc.equals(SWW_CLASS.getDescriptor()) && av != null) {
                    av = new AnnotationVisitorImpl(av);
                }
                return av;
            }

        }

        // Return a FieldVisitorImpl
        FieldVisitor fv = super.visitField(access, name, desc, signature, value);
        if (fv != null) {
            fv = new FieldVisitorImpl(fv);
        }
        return fv;
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
                        super.visitMethodInsn(INVOKEVIRTUAL, classType.getInternalName(), ADDED_AFTER_UNMARSHAL_METHOD.getName(), ADDED_AFTER_UNMARSHAL_METHOD.getDescriptor(), false);
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

        if (!fields.isEmpty()) {
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
        for (Triple<Field, Type, Type> field : fieldsForWrapperSubstitution) {
            String fieldName = field.getLeft().getName();
            Type fieldType = field.getLeft().getType();
            Type wrapperType = field.getMiddle();
            // Null means "default"; in that case, the field type is used as the type of the first argument of the wrapper constructor
            Type wrapperConstructorArgType = field.getRight() == null ? fieldType : field.getRight();

            // Note that this reference will be used for the PUTFIELD instruction later on
            mg.loadThis();

            // ----- Stack: [this]

            // Creates the wrapper using the current field value
            {
                // Create a new instance of the wrapper type and duplicate it for the constructor call later on
                mg.newInstance(wrapperType);
                mg.dup();

                // ----- Stack: [this, wrapper, wrapper]

                // Retrieve the current field value
                ASMUtils.generateGetField(mg, classType, fieldName, fieldType);

                // ----- Stack: [this, wrapper, wrapper, fieldValue]

                // Call the constructor of the new wrapper using the current field value as the first argument
                mg.invokeConstructor(wrapperType, Method.getMethod("void <init> (" + wrapperConstructorArgType.getClassName() + ")"));

                // ----- Stack: [this, wrapper]
            }

            // Store the new wrapper in the field the value has been retrieved from before
            // The substitution is complete
            mg.putField(classType, fieldName, fieldType);

            // ----- Stack: []
        }

        /*
         * Iterate through all fields.
         * For each field, call the addParent() method with "this" as parent if the current field value is parent-aware
         */
        for (Field field : fields) {
            ASMUtils.generateGetField(mg, classType, field.getName(), field.getType());

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
