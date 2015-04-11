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

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Type;

/**
 * A simple data class which represents a java bytecode field.
 * This class is used by the ASM {@link ClassVisitor}s of the runtime hook.
 */
public class Field {

    private final String name;
    private final Type   type;

    /**
     * Creates a new field with the given name and value {@link Type}.
     * 
     * @param name The name of the represented field.
     * @param type The value type of the represented field.
     */
    public Field(String name, Type type) {

        this.name = name;
        this.type = type;
    }

    /**
     * Returns the name of the represented java bytecode field.
     * 
     * @return The name of the field.
     */
    public String getName() {

        return name;
    }

    /**
     * Returns the value {@link Type} of the represented java bytecode field.
     * 
     * @return The value type of the field.
     */
    public Type getType() {

        return type;
    }

}
