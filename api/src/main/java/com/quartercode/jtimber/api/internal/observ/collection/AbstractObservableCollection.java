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

package com.quartercode.jtimber.api.internal.observ.collection;

/**
 * The base class for implementations that wrap around a certain collection (might also be a map) and inform a set {@link CollectionObserver} whenever an element is added or removed.
 * For example, when an element is added to an {@link ObservableList}, the element is added to the wrapped list and the {@link CollectionObserver#onAdd(Object)} method is called.<br>
 * <br>
 * Note that this class is just a base class and does nothing on its own (apart from delegating calls to {@link #hashCode()}, {@link #equals(Object)} and {@link #toString()}).
 * For any actual functionality, you must use specific implementations like {@link ObservableCollection} or {@link ObservableList}.
 */
public abstract class AbstractObservableCollection {

    private final Object       wrapped;
    private CollectionObserver observer;

    /**
     * Creates a new abstract observable collection that wraps around the given object.
     *
     * @param wrapped The object the new abstract observable collection wraps around.
     */
    protected AbstractObservableCollection(Object wrapped) {

        this.wrapped = wrapped;
    }

    /**
     * Returns the {@link CollectionObserver} which is informed every time the wrapped collection changes.
     *
     * @return The collection observer that listens.
     */
    public CollectionObserver getObserver() {

        return observer;
    }

    /**
     * Changes the {@link CollectionObserver} which is informed every time the wrapped collection changes.
     * Note that this method should be called immediately after the construction of the observable collection object with a non-null observer.
     * Sadly, the observer setting cannot be part of the constructor itself due to the internal constructor implementation in bytecode.<br>
     * <br>
     * Also note that it is possible to overwrite a previously set collection observer by calling this method again.
     *
     * @param observer The new {@link CollectionObserver} that should listen.
     */
    public void setObserver(CollectionObserver observer) {

        this.observer = observer;
    }

    @Override
    public int hashCode() {

        return wrapped.hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        return wrapped.equals(obj instanceof AbstractObservableCollection ? ((AbstractObservableCollection) obj).wrapped : obj);
    }

    @Override
    public String toString() {

        return wrapped.toString();
    }

}
