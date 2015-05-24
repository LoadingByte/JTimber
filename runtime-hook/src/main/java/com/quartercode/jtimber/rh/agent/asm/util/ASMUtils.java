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

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

/**
 * A utility class which contains some utility methods used by ASM transformers.
 */
public class ASMUtils {

    private static final String NODE_FQCN                 = "com.quartercode.jtimber.api.node.Node";

    private static final Type   FUNCS_CLASS               = Type.getObjectType("com/quartercode/jtimber/api/internal/RHConstFunctions");
    private static final Method FUNC_ADD_OR_REMOVE_PARENT = Method.getMethod("void addOrRemoveParent (java.lang.Object, " + NODE_FQCN + ", boolean)");
    private static final Method FUNC_TRY_WRAP             = Method.getMethod("java.lang.Object tryWrap (java.lang.Object, java.lang.Class, java.lang.Class)");

    /**
     * Generates the instructions to push a non-static field onto the stack and box it in case it is a primitive.
     * This is just a handy shortcut. However, it requires a {@link GeneratorAdapter} method visitor.
     *
     * @param mg The {@link MethodVisitor} that should be used to generate the instructions (must be a generator adapter).
     * @param classType The {@link Type} of the class which contains the field.
     * @param fieldName The name of the field.
     * @param fieldType The type of the field.
     */
    public static void generateGetField(GeneratorAdapter mg, Type classType, String fieldName, Type fieldType) {

        // Push the object stored in the field
        mg.loadThis();
        mg.getField(classType, fieldName, fieldType);

        // Box the object in case it is a primitive
        mg.box(fieldType);
    }

    /**
     * Generates the instructions to call the {@code addParent()} or {@code removeParent()} method on an object using {@code this} as the parent.
     * Note that an instanceof check is be executed beforehand in order to check whether the object is non-null and parent-aware.<br>
     * <br>
     * The object the operation should be performed on needs to be the topmost value on the stack when the generated instructions are entered.
     * The rest of the stack is ignored by the generated instructions.
     * Note that the generated instructions make sure to reconstruct the original stack at the end.
     *
     * @param mg The {@link MethodVisitor} that should be used to generate the instructions (must be a generator adapter).
     * @param add Whether the {@code addParent()} ({@code true}) or the {@code removeParent()} ({@code false}) method should be called.
     */
    public static void generateAddOrRemoveThisAsParent(GeneratorAdapter mg, boolean add) {

        // ----- Stack: [node]

        // Prepare the stack for the following function call
        mg.dup();
        mg.loadThis();
        mg.push(add);

        // ----- Stack: [node, node, this, add/remove]

        // Call the function which actually adds/removes the parent "this" to/from the node
        mg.invokeStatic(FUNCS_CLASS, FUNC_ADD_OR_REMOVE_PARENT);

        // ----- Stack: [node]
    }

    /**
     * Generates the instructions to wrap the top stack value in a wrapper of the given type and then put that wrapper on the top of the stack instead of the original object.
     * That is done by constructing a new wrapper using the original object as first and only constructor argument.
     * If the top stack value is {@code null}, nothing happens.
     * The rest of the stack is ignored by the generated instructions.
     *
     * @param mg The {@link MethodVisitor} that should be used to generate the instructions (must be a generator adapter).
     * @param wrapperType The {@link Type} of the wrapper that should be constructed.
     * @param wrapperConstructorArgType The type of the single argument of the wrapper constructor that should be called.
     *        Note that the top stack value must be an instance of this type.
     */
    public static void generateWrapperSubstitution(GeneratorAdapter mg, Type wrapperType, Type wrapperConstructorArgType) {

        // ----- Stack: [forWrapping]

        mg.push(wrapperType);
        mg.push(wrapperConstructorArgType);

        // ----- Stack: [forWrapping, wrapperType, wrapperConstructorArgType]

        // Call the function which actually adds/removes the parent "this" to/from the node
        mg.invokeStatic(FUNCS_CLASS, FUNC_TRY_WRAP);

        // ----- Stack: [forWrapping OR wrapper]
    }

    private ASMUtils() {

    }

}
