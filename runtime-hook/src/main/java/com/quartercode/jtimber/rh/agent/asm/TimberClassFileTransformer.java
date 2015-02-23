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

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Set;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

/**
 * The {@link ClassFileTransformer} which manipulates the bytecode of newly loaded classes using the {@link InsertParentWatcherClassAdapter}.
 * That bytecode manipulator adds extra bytecode for tracking the parents of parent-aware objects.
 * 
 * @see InsertParentWatcherClassAdapter
 */
public class TimberClassFileTransformer implements ClassFileTransformer {

    private final Set<String> nodeIndex;

    /**
     * Creates a new timber class file transformer that only transforms nodes.
     * Which classes are nodes should be listed in the given node index (containing the internal names of node classes).
     * 
     * @param nodeIndex The index that marks which classes are nodes and should therefore be transformed.
     */
    public TimberClassFileTransformer(Set<String> nodeIndex) {

        this.nodeIndex = nodeIndex;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] bytes) throws IllegalClassFormatException {

        // Only transform actual nodes because only they are able to be parents
        if (!nodeIndex.contains(className)) {
            return bytes;
        }

        // Writer
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        // Transformer
        InsertParentWatcherClassAdapter transformer = new InsertParentWatcherClassAdapter(writer);

        // Reader
        ClassReader reader = new ClassReader(bytes);
        reader.accept(transformer, 0);

        return writer.toByteArray();
    }

}
