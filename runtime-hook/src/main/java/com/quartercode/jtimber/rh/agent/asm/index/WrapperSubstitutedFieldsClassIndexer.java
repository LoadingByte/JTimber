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

package com.quartercode.jtimber.rh.agent.asm.index;

import static org.objectweb.asm.Opcodes.ASM5;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Type;
import com.quartercode.jtimber.rh.agent.asm.ClassMetadata;
import com.quartercode.jtimber.rh.agent.asm.MetadataAwareClassVisitor;

/**
 * The class indexer which indexes the {@link ClassMetadata#wrapperSubstitutedFields fields annotated with @@SubstituteWithWrapper} of the processed class.
 */
public final class WrapperSubstitutedFieldsClassIndexer extends MetadataAwareClassVisitor {

    private static final Type SWW_CLASS         = Type.getObjectType("com/quartercode/jtimber/api/node/wrapper/SubstituteWithWrapper");
    private static final Type SWW_DEFAULT_CLASS = Type.getObjectType(SWW_CLASS.getInternalName() + "$Default");

    /**
     * Creates a new wrapper substituted fields class indexer.
     * 
     * @param cv The class visitor to which this visitor delegates method calls. May be {@code null}.
     * @param metadata The {@link ClassMetadata} object the indexed metadata should be stored in.
     */
    public WrapperSubstitutedFieldsClassIndexer(ClassVisitor cv, ClassMetadata metadata) {

        super(cv, metadata);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {

        // Return a FieldVisitorImpl
        FieldVisitor fv = super.visitField(access, name, desc, signature, value);
        return new FieldVisitorImpl(fv, name);
    }

    /*
     * This field visitor "invokes" the AnnotationVisitorImpl with a possibly found "@SubstituteWithWrapper" annotation.
     */
    private final class FieldVisitorImpl extends FieldVisitor {

        private final String fieldName;

        private FieldVisitorImpl(FieldVisitor fv, String fieldName) {

            super(ASM5, fv);

            this.fieldName = fieldName;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {

            AnnotationVisitor av = super.visitAnnotation(desc, visible);
            if (desc.equals(SWW_CLASS.getDescriptor())) {
                av = new AnnotationVisitorImpl(av, fieldName);
            }
            return av;
        }

    }

    /*
     * This annotation visitor expects "@SubstituteWithWrapper" annotations, parses them and adds their data to the list "fieldsForWrapperSubstitution".
     */
    private final class AnnotationVisitorImpl extends AnnotationVisitor {

        private final String fieldName;

        private Type         wrapperType;
        private Type         wrapperConstructorArgType;

        private AnnotationVisitorImpl(AnnotationVisitor av, String fieldName) {

            super(ASM5, av);

            this.fieldName = fieldName;
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

            metadata.wrapperSubstitutedFields.put(fieldName, Pair.of(wrapperType, wrapperConstructorArgType));
        }

    }

}
