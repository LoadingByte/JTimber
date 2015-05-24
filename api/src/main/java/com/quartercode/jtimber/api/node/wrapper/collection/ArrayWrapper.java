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

import java.util.Arrays;
import java.util.List;
import javax.xml.bind.annotation.XmlTransient;
import com.quartercode.jtimber.api.node.Node;
import com.quartercode.jtimber.api.node.ParentAware;
import com.quartercode.jtimber.api.node.wrapper.AbstractWrapper;
import com.quartercode.jtimber.api.node.wrapper.Wrapper;

/**
 * A {@link Wrapper} around a one-dimensional array of any type.
 * See {@link Wrapper} for more details on what wrappers are and what they are used for.<br>
 * <br>
 * <b>Important note:</b> This wrapper doesn't support JAXB persistence (yet).
 * If you need that feature, use collections and the {@link CollectionWrapper} (or subclasses for lists etc.) instead.
 *
 * @param <E> The type of elements in the wrapped one-dimensional array.
 * @see Wrapper
 */
@XmlTransient
public class ArrayWrapper<E> extends AbstractWrapper {

    private final E[] wrapped;

    /**
     * Creates a new array {@link Wrapper} that wraps around the given one-dimensional array.
     *
     * @param wrapped The one-dimensional array the new array wrapper wraps around.
     */
    public ArrayWrapper(E[] wrapped) {

        // Sadly, this cannot be avoided
        super(wrapped = wrapped.clone());

        this.wrapped = wrapped;
    }

    // ----- Wrapper Methods -----

    @Override
    public List<Object> getActualChildren() {

        return Arrays.asList((Object[]) wrapped);
    }

    // ----- ParentAware Overrides -----

    @Override
    public void addParent(Node<?> parent) {

        super.addParent(parent);

        for (E element : wrapped) {
            if (element instanceof ParentAware) {
                ((ParentAware<?>) element).addParent(parent);
            }
        }
    }

    @Override
    public void removeParent(Node<?> parent) {

        super.removeParent(parent);

        for (E element : wrapped) {
            if (element instanceof ParentAware) {
                ((ParentAware<?>) element).removeParent(parent);
            }
        }
    }

    // ----- Custom Methods -----

    /**
     * Returns the length (or the size) of the one-dimensional array.
     *
     * @return The length of the array.
     */
    public int length() {

        return wrapped.length;
    }

    /**
     * Returns the element which is stored at the given index inside the one-dimensional array.
     *
     * @param index The index at which the element for return is stored.
     * @return The element which is stored at the given index.
     */
    public E get(int index) {

        return wrapped[index];
    }

    /**
     * Sets the element which should be stored at the given index inside the one-dimensional array.
     *
     * @param index The index at which the element should be stored.
     * @param value The element which should be stored at the given index.
     */
    public void set(int index, E value) {

        E oldValue = wrapped[index];

        // Change the parents of the affected elements
        for (Node<?> parent : getParents()) {
            if (oldValue instanceof ParentAware) {
                ((ParentAware<?>) oldValue).removeParent(parent);
            }

            if (value instanceof ParentAware) {
                ((ParentAware<?>) value).addParent(parent);
            }
        }

        wrapped[index] = value;
    }

    /**
     * Returns a clone of the wrapped one-dimensional array.
     * This method internally calls the {@link Object#clone() clone} method on the wrapped array.
     *
     * @return A clone of the wrapped array.
     */
    public E[] cloneArray() {

        return wrapped.clone();
    }

}
