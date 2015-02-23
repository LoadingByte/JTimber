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

package com.quartercode.jtimber.api.node.wrapper;

import java.util.Collection;
import com.quartercode.jtimber.api.node.DefaultParentAware;
import com.quartercode.jtimber.api.node.ParentAware;
import com.quartercode.jtimber.api.node.wrapper.collection.CollectionWrapper;
import com.quartercode.jtimber.api.node.wrapper.collection.ListWrapper;

/**
 * A wrapper allows a {@link ParentAware} object to know its parents, although the two levels are separated by another object, e.g. a {@link Collection}.
 * For example, the {@link CollectionWrapper} tracks the parents of parent-aware objects which are located inside a collection.
 * That means that the parents of the objects inside the collection are the objects which reference that collection.<br>
 * <br>
 * Internally, the wrapper does its job by being parent-aware itself and setting the parents of all the parent-aware objects it is sees through
 * the separation layer (e.g. the collection) to its own parents.
 * Continuing the collection example (this time with lists), the user just needs to wrap his lists into list wrappers:
 * 
 * <pre>
 * private List&lt;SomeParentAwareObject&gt; list = new ListWrapper<>(new ArrayList&lt;SomeParentAwareObject&gt;());
 * </pre>
 * 
 * The user doesn't notice any difference and uses the list like any other list.
 * Internally, however, the list wrapper takes care of adjusting the parents of all parent-aware objects stored in the list.<br>
 * <br>
 * Note that this class is just a base class and does nothing on its own (apart from delegating {@link #hashCode()}, {@link #equals(Object)} and {@link #toString()} calls).
 * For any actual parent-caretaking functionality, you must use specific wrappers like {@link CollectionWrapper} or {@link ListWrapper}.
 * 
 * @see CollectionWrapper
 * @see ListWrapper
 */
public class Wrapper extends DefaultParentAware {

    private final Object wrapped;

    /**
     * Creates a new generic wrapper that wraps around the given object and only delegates {@link #hashCode()}, {@link #equals(Object)} and {@link #toString()} calls
     * to the wrapped object.
     * 
     * @param wrapped The object the new generic wrapper wraps around.
     */
    public Wrapper(Object wrapped) {

        this.wrapped = wrapped;
    }

    /**
     * Returns the {@link Object#hashCode() hash code} of the wrapped object.
     * 
     * @return The hash code of the wrapped object.
     */
    @Override
    public int hashCode() {

        return wrapped.hashCode();
    }

    /**
     * Returns whether the given object {@link Object#equals(Object) equals} this object.
     * If the given object is a wrapper as well, the object wrapped by it is used for the equality check.
     * 
     * @param obj The object which should be compared with this object.
     * @return Whether the given object is equal to this object.
     */
    @Override
    public boolean equals(Object obj) {

        return wrapped.equals(obj instanceof Wrapper ? ((Wrapper) obj).wrapped : obj);
    }

    /**
     * Returns the {@link Object#hashCode() string representation} of the wrapped object.
     * 
     * @return The string representation of the wrapped object.
     */
    @Override
    public String toString() {

        return wrapped.toString();
    }

}
