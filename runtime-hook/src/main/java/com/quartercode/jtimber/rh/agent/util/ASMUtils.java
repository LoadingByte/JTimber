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

package com.quartercode.jtimber.rh.agent.util;

import static org.objectweb.asm.Opcodes.*;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

/**
 * A utility class which contains some utility methods used by ASM transformers.
 */
public class ASMUtils {

    private static final String API_PCKG           = "com/quartercode/jtimber/api";
    private static final String PARENT_AWARE_CLASS = API_PCKG + "/node/ParentAware";
    private static final String NODE_DESC          = "L" + API_PCKG + "/node/Node;";

    /**
     * Generates the instructions to push a non-static field onto the stack and box it in case it is a primitive.
     * This is just a handy shortcut. However, it requires a {@link GeneratorAdapter} method visitor.
     * 
     * @param mg The method visitor that should be used to generate the instructions (must be a generator adapter).
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
     * 
     * @param mv The {@link MethodVisitor} that should be used to generate the instructions.
     * @param methodName The name of the method to call ({@code "addParent"} or {@code "removeParent"}).
     */
    public static void generateAddOrRemoveThisAsParent(MethodVisitor mv, String methodName) {

        // Skip the if-block if the condition below isn't true
        Label endIf = new Label();
        mv.visitInsn(DUP);
        mv.visitTypeInsn(INSTANCEOF, PARENT_AWARE_CLASS);
        mv.visitJumpInsn(IFEQ, endIf);

        /* if (object instanceof ParentAware) */
        {
            // Push a copy of the value because the parent watcher method will be invoked on it
            mv.visitInsn(DUP);

            // Push "this" because it will be used as the first argument for the following method call
            mv.visitVarInsn(ALOAD, 0);

            // Invoke the parent watcher method on the value (which implements the "ParentAware" interface) using "this" as the first argument
            mv.visitMethodInsn(INVOKEINTERFACE, PARENT_AWARE_CLASS, methodName, "(" + NODE_DESC + ")V", true);
        }

        mv.visitLabel(endIf);
    }

    private ASMUtils() {

    }

}
