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

import java.util.Set;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Type;
import com.quartercode.jtimber.rh.agent.asm.ClassMetadata;
import com.quartercode.jtimber.rh.agent.asm.MetadataAwareClassVisitor;

/**
 * The class indexer which indexes the following class metadata:
 * 
 * <ul>
 * <li>{@link ClassMetadata#classType The type of the processed class}</li>
 * <li>{@link ClassMetadata#superclassType The type of the superclass of the processed class}</li>
 * <li>{@link ClassMetadata#isSuperclassNode Whether the superclass of the processed class is a node}</li>
 * </ul>
 */
public final class ClassTypesClassIndexer extends MetadataAwareClassVisitor {

    private final Set<String> nodeIndex;

    /**
     * Creates a new class types class indexer.
     * 
     * @param cv The class visitor to which this visitor delegates method calls. May be {@code null}.
     * @param metadata The {@link ClassMetadata} object the indexed metadata should be stored in.
     * @param nodeIndex The index that marks which classes are nodes.
     */
    public ClassTypesClassIndexer(ClassVisitor cv, ClassMetadata metadata, Set<String> nodeIndex) {

        super(cv, metadata);

        this.nodeIndex = nodeIndex;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {

        metadata.classType = Type.getObjectType(name);
        metadata.superclassType = Type.getObjectType(superName);
        metadata.isSuperclassNode = nodeIndex.contains(superName);

        super.visit(version, access, name, signature, superName, interfaces);
    }

}
