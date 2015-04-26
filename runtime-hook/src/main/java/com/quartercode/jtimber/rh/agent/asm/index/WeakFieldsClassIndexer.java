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

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Type;
import com.quartercode.jtimber.rh.agent.asm.ClassMetadata;
import com.quartercode.jtimber.rh.agent.asm.MetadataAwareClassVisitor;
import com.quartercode.jtimber.rh.agent.asm.util.AnnotatedFieldRecorder;

/**
 * The class indexer which indexes the {@link ClassMetadata#weakFields fields annotated with @Weak} of the processed class.
 */
public final class WeakFieldsClassIndexer extends MetadataAwareClassVisitor {

    private static final Type WEAK_CLASS = Type.getObjectType("com/quartercode/jtimber/api/node/Weak");

    /**
     * Creates a new weak fields class indexer.
     * 
     * @param cv The class visitor to which this visitor delegates method calls. May be {@code null}.
     * @param metadata The {@link ClassMetadata} object the indexed metadata should be stored in.
     */
    public WeakFieldsClassIndexer(ClassVisitor cv, ClassMetadata metadata) {

        super(cv, metadata);
    }

    @Override
    public FieldVisitor visitField(int access, final String name, String desc, String signature, Object value) {

        // Return an AnnotatedFieldRecorder which adds all fields annotated with "@Weak" to the "weakFields" set
        FieldVisitor fv = super.visitField(access, name, desc, signature, value);
        return new AnnotatedFieldRecorder(fv, name, WEAK_CLASS, metadata.weakFields);
    }

}
