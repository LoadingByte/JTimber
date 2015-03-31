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

import java.util.Collection;
import java.util.Iterator;
import com.quartercode.jtimber.api.node.Node;
import com.quartercode.jtimber.api.node.ParentAware;
import com.quartercode.jtimber.api.node.wrapper.Wrapper;

/**
 * A {@link Wrapper} around any class that implements the {@link Collection} interface.
 * See {@link Wrapper} for more details on what wrappers are and what they are used for.<br>
 * <br>
 * Note that this wrapper also implements the collection interface in order to allow it being used like any other collection.
 * 
 * @param <E> The type of elements in the wrapped collection.
 * @see Collection
 * @see Wrapper
 */
public class CollectionWrapper<E extends ParentAware<?>> extends Wrapper implements Collection<E> {

    private final Collection<E> wrapped;

    /**
     * Creates a new {@link Collection} {@link Wrapper} that wraps around the given collection.
     * 
     * @param wrapped The collection the new collection wrapper wraps around.
     */
    public CollectionWrapper(Collection<E> wrapped) {

        super(wrapped);

        this.wrapped = wrapped;
    }

    // ----- ParentAware Overrides -----

    @Override
    public void addParent(Node<?> parent) {

        super.addParent(parent);

        for (E element : wrapped) {
            if (element != null) {
                element.addParent(parent);
            }
        }
    }

    @Override
    public void removeParent(Node<?> parent) {

        super.removeParent(parent);

        for (E element : wrapped) {
            if (element != null) {
                element.removeParent(parent);
            }
        }
    }

    // ----- Event Methods -----

    /**
     * Internal method that should be called whenever a {@link ParentAware} object is added to the wrapped {@link Collection}.
     * It adjusts the parents of that new element by adding the parents of this wrapper.
     * 
     * @param element The element that is added to the underlying collection.
     */
    protected void addElement(ParentAware<?> element) {

        if (element != null) {
            for (Node<?> parent : getParents()) {
                element.addParent(parent);
            }
        }
    }

    /**
     * Internal method that should be called whenever a {@link ParentAware} object is removed from the wrapped {@link Collection}.
     * It adjusts the parents of that element by removing the parents of this wrapper.
     * 
     * @param element The element that is removed from the underlying collection.
     */
    protected void removeElement(ParentAware<?> element) {

        if (element != null) {
            for (Node<?> parent : getParents()) {
                element.removeParent(parent);
            }
        }
    }

    // ----- Collection Overrides -----

    @Override
    public Iterator<E> iterator() {

        return new IteratorWrapper(wrapped.iterator());
    }

    @Override
    public boolean add(E e) {

        boolean modified = wrapped.add(e);

        if (modified) {
            addElement(e);
        }

        return modified;
    }

    @Override
    public boolean remove(Object o) {

        boolean modified = wrapped.remove(o);

        if (modified && o instanceof ParentAware) {
            removeElement((ParentAware<?>) o);
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
            removeElement(element);
        }

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
     * An internal {@link Iterator} implementation that wraps around a real iterator and adjusts the parents of removed elements.
     * 
     * @see CollectionWrapper
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

            // If the setting was successful (no exception has been thrown), change the parents of the removed element
            removeElement(currentElement);
        }

    }

}
