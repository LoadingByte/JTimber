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

import com.quartercode.jtimber.api.node.DefaultParentAware;
import com.quartercode.jtimber.api.node.Node;
import com.quartercode.jtimber.api.node.wrapper.collection.ArrayWrapper;
import com.quartercode.jtimber.api.node.wrapper.collection.CollectionWrapper;
import com.quartercode.jtimber.api.node.wrapper.collection.ListWrapper;

/**
 * An abstract base implementation of the {@link Wrapper} interface (see that interface for more information on what wrappers are).
 * Note that this class is just a base class and does nothing on its own (apart from delegating calls to {@link #hashCode()}, {@link #equals(Object)} and {@link #toString()}).
 * For any actual parent-caretaking functionality, you must use specific wrapper implementations like {@link ArrayWrapper}, {@link CollectionWrapper}, or {@link ListWrapper}.
 *
 * @see Wrapper
 */
public abstract class AbstractWrapper extends DefaultParentAware<Node<?>> implements Wrapper {

    private final Object wrapped;

    /**
     * Creates a new abstract wrapper that wraps around the given object and only delegates calls to {@link #hashCode()}, {@link #equals(Object)} and {@link #toString()} to the wrapped object.
     *
     * @param wrapped The object the new abstract wrapper wraps around.
     */
    protected AbstractWrapper(Object wrapped) {

        this.wrapped = wrapped;
    }

    @Override
    public Object getInternallyWrapped() {

        return wrapped;
    }

    @Override
    public int hashCode() {

        return wrapped.hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        return wrapped.equals(obj instanceof Wrapper ? ((Wrapper) obj).getInternallyWrapped() : obj);
    }

    @Override
    public String toString() {

        return wrapped.toString();
    }

}
