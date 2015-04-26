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

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Set;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import com.quartercode.jtimber.rh.agent.asm.index.ClassTypesClassIndexer;
import com.quartercode.jtimber.rh.agent.asm.index.FieldsClassIndexer;
import com.quartercode.jtimber.rh.agent.asm.index.WeakFieldsClassIndexer;
import com.quartercode.jtimber.rh.agent.asm.index.WrapperSubstitutedFieldsClassIndexer;
import com.quartercode.jtimber.rh.agent.asm.transform.InsertChildAccessorsClassTransformer;
import com.quartercode.jtimber.rh.agent.asm.transform.InsertJAXBTweaksClassTransformer;
import com.quartercode.jtimber.rh.agent.asm.transform.InsertParentWatcherClassTransformer;
import com.quartercode.jtimber.rh.agent.asm.transform.InsertWeakRefWatcherClassTransformer;

/**
 * The {@link ClassFileTransformer} which manipulates the bytecode of newly loaded classes using some hard-coded ASM transformers.
 * That bytecode manipulator adds extra bytecode for enabling JTimber functions.
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
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        // Only transform actual nodes because only they are able to be parents and have children
        if (!nodeIndex.contains(className)) {
            return classfileBuffer;
        }

        // Reader and writer
        ClassReader reader = new ClassReader(classfileBuffer);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        // Collect metadata about the class and then call the transformers
        ClassMetadata metadata = new ClassMetadata();
        reader.accept(createIndexerChain(metadata), ClassReader.SKIP_FRAMES);
        reader.accept(createTransformerChain(writer, metadata), ClassReader.SKIP_FRAMES);

        return writer.toByteArray();
    }

    private ClassVisitor createIndexerChain(ClassMetadata metadata) {

        // The indexer shouldn't execute any changes; therefore, the result isn't stored anywhere and the target is null
        ClassVisitor next = null;

        next = new WrapperSubstitutedFieldsClassIndexer(next, metadata);
        next = new WeakFieldsClassIndexer(next, metadata);
        next = new FieldsClassIndexer(next, metadata);
        next = new ClassTypesClassIndexer(next, metadata, nodeIndex);

        return next;
    }

    private ClassVisitor createTransformerChain(ClassVisitor target, ClassMetadata metadata) {

        ClassVisitor next = target;

        // Order requirements:
        // - InsertWeakRefWatcherClassTransformer after all transformers that add/remove/manipulate GETFIELD instructions
        // - InsertJAXBTweaksClassTransformer after InsertParentWatcherClassTransformer because there shouldn't be any parent watchers in the afterUnmarshal() method
        next = new InsertWeakRefWatcherClassTransformer(next, metadata);
        next = new InsertJAXBTweaksClassTransformer(next, metadata);
        next = new InsertChildAccessorsClassTransformer(next, metadata);
        next = new InsertParentWatcherClassTransformer(next, metadata);

        return next;
    }

}
