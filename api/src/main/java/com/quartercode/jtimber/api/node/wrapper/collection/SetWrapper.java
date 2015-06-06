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

package com.quartercode.jtimber.api.node.wrapper.collection;

import java.util.Set;
import com.quartercode.jtimber.api.node.wrapper.Wrapper;

/**
 * A {@link Wrapper} around any class that implements the {@link Set} interface.
 * See {@link Wrapper} for more details on what wrappers are and what they are used for.<br>
 * <br>
 * Note that this wrapper also implements the set interface in order to allow it being used like any other set.
 * Also note that the wrapper extends the {@link CollectionWrapper} in order to inherit its functionality.
 *
 * @param <E> The type of elements in the wrapped set.
 * @see Set
 * @see Wrapper
 * @see CollectionWrapper
 */
public class SetWrapper<E> extends CollectionWrapper<E> implements Set<E> {

    /**
     * Creates a new {@link Set} {@link Wrapper} that wraps around the given set.
     *
     * @param wrapped The set the new set wrapper wraps around.
     */
    public SetWrapper(Set<E> wrapped) {

        super(wrapped);
    }

}
