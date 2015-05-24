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

package com.quartercode.jtimber.api.node.wrapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import com.quartercode.jtimber.api.node.Node;
import com.quartercode.jtimber.api.node.wrapper.collection.ListWrapper;

/**
 * {@link Node} fields annotated with this annotation are marked as being {@link Wrapper}s of a certain specified type.
 * However, the type of the field must be a supertype/interface of both the specified wrapper type and the type of the wrapped object.
 * For example, {@link ArrayList} (wrapped object) and {@link ListWrapper} (wrapper) both have the same supertype {@link List}.
 * Therefore, if the field is of type {@code List}, this annotation is allowed with the {@link #value() specified wrapper} {@link ListWrapper}.<br>
 * <br>
 * This information is primarily used for recreating wrappers after JAXB unmarshalling.
 * After that process has completed, an algorithm iterates through all node fields with this annotation.
 * For each field, the {@link #value() specified wrapper} is created with the unmarshalled field value as the first and only constructor argument.
 * Afterwards, the field value is set to the newly created wrapper (which wraps around the old field value).<br>
 * By default, the algorithm assumes that the type of the field is the type of the wrapper constructor argument.
 * However, that must not always be the case.
 * Therefore, the optional {@link #wrapperConstructorArg()} annotation property controls the type of the wrapper constructor argument.
 */
@Target ({ ElementType.FIELD })
@Retention (RetentionPolicy.CLASS)
public @interface SubstituteWithWrapper {

    /**
     * The exact type of the {@link Wrapper} stored in the annotated field.
     * It is used for creating new wrappers after JAXB unmarshalling.
     * See {@link SubstituteWithWrapper} for more information.
     *
     * @return The type of the wrapped stored in the annotated field.
     */
    Class<? extends Wrapper> value ();

    /**
     * By default, the algorithm executed after JAXB unmarshalling (see {@link SubstituteWithWrapper}) assumes that the type of the annotated field
     * is the type of the first and only wrapper constructor argument (of the {@link #value() specified wrapper}).
     * However, that must not always be the case.
     * Therefore, this optional annotation property controls the type of the wrapper constructor argument.
     *
     * @return The type of the first and only wrapper constructor argument.
     */
    Class<?> wrapperConstructorArg () default Default.class;

    static class Default {

    }

}
