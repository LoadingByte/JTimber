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

package com.quartercode.jtimber.rh.agent.asm.util;

import static org.objectweb.asm.Opcodes.ASM5;
import java.util.Collection;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Type;

/**
 * A {@link FieldVisitor} which adds the names of all fields with a certain annotation to a given {@link Collection}.
 */
public class AnnotatedFieldRecorder extends FieldVisitor {

    private final String             fieldName;
    private final String             annotationDesc;
    private final Collection<String> fields;

    /**
     * Creates a new annotated field recorder.
     * 
     * @param fv The field visitor to which this visitor must delegate method calls. May be {@code null}.
     * @param fieldName The name of the field the visitor should process.
     * @param annotation The annotation {@link Type} the visitor should look out for.
     *        If the processed fields has this annotation, its name is added to the given fields {@link Collection}.
     * @param fields The collection which stores all fields annotated with the given annotation.
     */
    public AnnotatedFieldRecorder(FieldVisitor fv, String fieldName, Type annotation, Collection<String> fields) {

        super(ASM5, fv);

        this.fieldName = fieldName;
        annotationDesc = annotation.getDescriptor();
        this.fields = fields;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {

        if (desc.equals(annotationDesc)) {
            fields.add(fieldName);
        }

        return super.visitAnnotation(desc, visible);
    }

}
