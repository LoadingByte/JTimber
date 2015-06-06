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

import java.util.Collection;
import java.util.Iterator;

/**
 * A class that wraps around any class that implements the {@link Collection} interface and adds observing functionalities for the different manipulating methods.
 * See {@link AbstractObservableCollection} for more details on what observable collections are and what they are used for.
 * Note that this class also implements the collection interface in order to allow it being used like any other collection.
 *
 * @param <E> The type of elements in the wrapped collection.
 * @see Collection
 * @see AbstractObservableCollection
 */
public class ObservableCollection<E> extends AbstractObservableCollection implements Collection<E> {

    private final Collection<E> wrapped;

    /**
     * Creates a new observable {@link Collection} that wraps around the given collection.
     *
     * @param wrapped The collection the new observable collection wraps around.
     */
    public ObservableCollection(Collection<E> wrapped) {

        super(wrapped);

        this.wrapped = wrapped;
    }

    @Override
    public Iterator<E> iterator() {

        return new IteratorWrapper(wrapped.iterator());
    }

    @Override
    public boolean add(E e) {

        boolean modified = wrapped.add(e);

        if (modified) {
            getObserver().onAdd(e);
        }

        return modified;
    }

    @Override
    public boolean remove(Object o) {

        boolean modified = wrapped.remove(o);

        if (modified) {
            getObserver().onRemove(o);
        }

        return modified;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {

        boolean modified = false;

        for (E addedElement : c) {
            if (add(addedElement)) {
                modified = true;
            }
        }

        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {

        boolean modified = false;

        for (Object removedElement : c) {
            if (remove(removedElement)) {
                modified = true;
            }
        }

        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {

        boolean modified = false;

        Iterator<E> it = iterator();
        while (it.hasNext()) {
            if (!c.contains(it.next())) {
                // This change is observed by the iterator wrapper
                it.remove();
                modified = true;
            }
        }

        return modified;
    }

    @Override
    public void clear() {

        for (E element : wrapped) {
            getObserver().onRemove(element);
        }

        // Actualy clear the collection
        wrapped.clear();
    }

    // ----- Basic Delegates -----

    @Override
    public int size() {

        return wrapped.size();
    }

    @Override
    public boolean isEmpty() {

        return wrapped.isEmpty();
    }

    @Override
    public boolean contains(Object o) {

        return wrapped.contains(o);
    }

    @Override
    public Object[] toArray() {

        return wrapped.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {

        return wrapped.toArray(a);
    }

    @Override
    public boolean containsAll(Collection<?> c) {

        return wrapped.containsAll(c);
    }

    /**
     * An internal {@link Iterator} implementation that wraps around a real iterator and observes the {@link #remove() removal} of elements.
     *
     * @see ObservableCollection
     */
    protected class IteratorWrapper implements Iterator<E> {

        private final Iterator<E> wrapped;

        /**
         * The most recent element provided to the user of the {@link Iterator}.
         */
        protected E               currentElement;

        /**
         * Creates a new iterator wrapper that wraps around the given {@link Iterator}.
         *
         * @param wrapped The iterator the new iterator wrapper wraps around.
         */
        protected IteratorWrapper(Iterator<E> wrapped) {

            this.wrapped = wrapped;
        }

        @Override
        public boolean hasNext() {

            return wrapped.hasNext();
        }

        @Override
        public E next() {

            currentElement = wrapped.next();
            return currentElement;
        }

        @Override
        public void remove() {

            wrapped.remove();

            // If the setting was successful (no exception has been thrown), inform the observer about the removed element
            getObserver().onRemove(currentElement);
        }

    }

}
