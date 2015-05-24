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

import static org.objectweb.asm.Opcodes.ASM5;
import org.objectweb.asm.ClassVisitor;

/**
 * A {@link ClassVisitor} which references a {@link ClassMetadata} object for the processed class.
 * That object can either be used to store or to retrieve metdata about the class.
 */
public abstract class MetadataAwareClassVisitor extends ClassVisitor {

    /**
     * The {@link ClassMetadata} object for the processed class.
     * This object can either be used to store or to retrieve metdata about the class.
     */
    protected final ClassMetadata metadata;

    /**
     * Creates a new metadata-aware class visitor.
     *
     * @param cv The class visitor to which this visitor delegates method calls. May be {@code null}.
     * @param metadata The {@link ClassMetadata} object for the processed class.
     *        It can either be used to store or to retrieve metdata about the class.
     */
    protected MetadataAwareClassVisitor(ClassVisitor cv, ClassMetadata metadata) {

        super(ASM5, cv);

        this.metadata = metadata;
    }

}
