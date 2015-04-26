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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.Type;

/**
 * A data object which stores metadata about a certain java bytecode class.
 * Note that this object is typically filled by ASM indexers.
 * Afterwards, ASM transformers can use the data to modify classes.
 */
public class ClassMetadata {

    // ----- Types -----

    /**
     * The {@link Type} of the represented class.
     */
    public Type                                classType;

    /**
     * The {@link Type} of the superclass of the represented class.
     */
    public Type                                superclassType;

    /**
     * Whether the {@link #superclassType superclass} of the represented class is a node.
     */
    public boolean                             isSuperclassNode;

    // ----- Fields -----

    /**
     * The names of all fields of the represented class, along with their {@link Type}s.
     */
    public final Map<String, Type>             fields                   = new HashMap<>();

    /**
     * The names of all {@link #fields} of the represented class that are annotated with the {@code @Weak} annotation.
     */
    public final Set<String>                   weakFields               = new HashSet<>();

    /**
     * The names of all {@link #fields} of the represented class that are annotated with the {@code @SubstituteWithWrapper} annotation.
     * Note that the two parameters of the annotation ({@code value} and {@code wrapperConstructorArg}) are stored in {@link Pair}s mapped to the field names.
     * Note that the {@code wrapperConstructorArg} parameter might be {@code null} if no one is set explicitly.
     */
    public final Map<String, Pair<Type, Type>> wrapperSubstitutedFields = new HashMap<>();

    /**
     * Returns the names of all {@link #fields} of the represented class that are not {@link #weakFields weak} (annotated with the {@code @Weak} annotation).
     * 
     * @return The names of all non-weak fields of the represented class.
     */
    public Set<String> getNonWeakFields() {

        Set<String> result = new HashSet<>(fields.keySet());
        result.removeAll(weakFields);
        return result;
    }

}
