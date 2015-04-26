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

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.IADD;
import java.util.Map.Entry;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;
import com.quartercode.jtimber.rh.agent.asm.ClassMetadata;
import com.quartercode.jtimber.rh.agent.asm.MetadataAwareClassVisitor;
import com.quartercode.jtimber.rh.agent.asm.util.ASMUtils;

/**
 * The {@link ClassVisitor} which adds so called "child accessors" to nodes in order to make the children of such nodes (their attributes) available through a convenient method.
 * Note that it transforms all classes that are fed into it.
 * Therefore, only node classes should be sent through it.
 */
public final class InsertChildAccessorsClassTransformer extends MetadataAwareClassVisitor {

    private static final Method DEFAULT_CONSTRUCTOR              = Method.getMethod("void <init> ()");
    private static final Type   ARRAY_LIST_CLASS                 = Type.getObjectType("java/util/ArrayList");

    private static final Type   FUNCS_CLASS                      = Type.getObjectType("com/quartercode/jtimber/api/internal/RHConstFunctions");
    private static final Method FUNC_ADD_ACTUAL_CHILDREN_TO_LIST = Method.getMethod("void addActualChildrenToList (java.util.List, java.lang.Object)");
    private static final Method FUNC_COUNT_ACTUAL_CHILDREN       = Method.getMethod("int countActualChildren (java.lang.Object)");

    private static final Method GET_CHILDREN_METHOD              = Method.getMethod("java.util.List getChildren ()");
    private static final Method GET_CHILD_COUNT_METHOD           = Method.getMethod("int getChildCount ()");

    /**
     * Creates a new insert child accessors class transformer.
     * 
     * @param cv The class visitor to which this visitor delegates method calls. May be {@code null}.
     * @param metadata The {@link ClassMetadata} object the transformer uses to retrieve metadata about the processed class.
     */
    public InsertChildAccessorsClassTransformer(ClassVisitor cv, ClassMetadata metadata) {

        super(cv, metadata);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

        // Remove the method if it will be added later on (when the visitor reaches the end of the class)
        if (name.equals("getChildren") || name.equals("getChildCount")) {
            return null;
        } else {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
    }

    @Override
    public void visitEnd() {

        // Add the getChildren() method
        generateGetChildrenMethod();

        // Add the getChildCount() method
        generateGetChildCountMethod();

        super.visitEnd();
    }

    private void generateGetChildrenMethod() {

        GeneratorAdapter mg = new GeneratorAdapter(ACC_PUBLIC, GET_CHILDREN_METHOD, null, null, cv);

        // Create the list; if the superclass is a node, call the getChildren() method on the superclass and use the result as the list
        // The leave the list at the bottom of the stack
        if (metadata.isSuperclassNode) {
            mg.loadThis();
            mg.invokeConstructor(metadata.superclassType, GET_CHILDREN_METHOD);
        } else {
            mg.newInstance(ARRAY_LIST_CLASS);
            mg.dup();
            mg.invokeConstructor(ARRAY_LIST_CLASS, DEFAULT_CONSTRUCTOR);
        }

        // ----- Stack: [list]

        // Add all field values to the list
        for (Entry<String, Type> field : metadata.fields.entrySet()) {
            String fieldName = field.getKey();
            Type fieldType = field.getValue();

            // Duplicate the list; the duplication is necessary because the following invocation of the add() method on the list
            // will "consume" this reference
            mg.dup();

            // ----- Stack: [list, list]

            // Push the current field value
            ASMUtils.generateGetField(mg, metadata.classType, fieldName, fieldType);

            // ----- Stack: [list, list, fieldValue]

            // Add the field object to the list; the called static method executes some checks and handles wrappers
            mg.invokeStatic(FUNCS_CLASS, FUNC_ADD_ACTUAL_CHILDREN_TO_LIST);

            // ----- Stack: [list]
        }

        // Return the list (which is at the bottom of the stack)
        mg.returnValue();

        mg.endMethod();
    }

    private void generateGetChildCountMethod() {

        GeneratorAdapter mg = new GeneratorAdapter(ACC_PUBLIC, GET_CHILD_COUNT_METHOD, null, null, cv);

        // Push the initial counter; if the superclass is a node, call the getChildCount() method on the superclass and use the result as the initial counter
        if (metadata.isSuperclassNode) {
            mg.loadThis();
            mg.invokeConstructor(metadata.superclassType, GET_CHILD_COUNT_METHOD);
        } else {
            mg.push(0);
        }

        // ----- Stack: [counter]

        // Increment the counter for all fields which are not null
        for (Entry<String, Type> field : metadata.fields.entrySet()) {
            String fieldName = field.getKey();
            Type fieldType = field.getValue();

            // Push the current field value
            ASMUtils.generateGetField(mg, metadata.classType, fieldName, fieldType);

            // ----- Stack: [counter, fieldValue]

            // Calculate the amount of children the current field value represents; the called static method executes some checks and handles wrappers
            // For example, null counts as 0 while a wrapper might represent multiple children
            mg.invokeStatic(FUNCS_CLASS, FUNC_COUNT_ACTUAL_CHILDREN);

            // ----- Stack: [counter, specificFieldCount]

            // Add the calculated amount of children to the counter
            mg.visitInsn(IADD);

            // ----- Stack: [counter]
        }

        // Return the counter
        mg.returnValue();

        mg.endMethod();
    }

}
