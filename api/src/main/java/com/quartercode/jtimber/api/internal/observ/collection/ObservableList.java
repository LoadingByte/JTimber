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
import java.util.List;
import java.util.ListIterator;
import com.quartercode.jtimber.api.node.wrapper.collection.CollectionWrapper;

/**
 * A class that wraps around any class that implements the {@link List} interface and adds observing functionalities for the different manipulating methods.
 * See {@link AbstractObservableCollection} for more details on what observable collections are and what they are used for.
 * Note that this class also implements the collection interface in order to allow it being used like any other collection.
 * Also note that the wrapper extends the {@link CollectionWrapper} in order to inherit its functionality.
 *
 * @param <E> The type of elements in the wrapped list.
 * @see List
 * @see AbstractObservableCollection
 * @see ObservableCollection
 */
public class ObservableList<E> extends ObservableCollection<E> implements List<E> {

    private final List<E> wrapped;

    /**
     * Creates a new observable {@link List} that wraps around the given list.
     *
     * @param wrapped The list the new observable list wraps around.
     */
    public ObservableList(List<E> wrapped) {

        super(wrapped);

        this.wrapped = wrapped;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {

        int indexCounter = index;
        for (E element : c) {
            add(indexCounter, element);
            indexCounter++;
        }

        return !c.isEmpty();
    }

    @Override
    public E set(int index, E element) {

        E old = wrapped.set(index, element);

        // If the setting was successful (no exception has been thrown), inform the observer about the affected elements
        getObserver().onRemove(old);
        getObserver().onAdd(element);

        return old;
    }

    @Override
    public void add(int index, E element) {

        wrapped.add(index, element);

        // If the setting was successful (no exception has been thrown), inform the observer about the added element
        getObserver().onAdd(element);
    }

    @Override
    public E remove(int index) {

        E old = wrapped.remove(index);

        // If the setting was successful (no exception has been thrown), inform the observer about the removed element
        getObserver().onRemove(old);

        return old;
    }

    @Override
    public ListIterator<E> listIterator() {

        return new ListIteratorWrapper(wrapped.listIterator());
    }

    @Override
    public ListIterator<E> listIterator(int index) {

        return new ListIteratorWrapper(wrapped.listIterator(index));
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {

        ObservableList<E> subList = new ObservableList<>(wrapped.subList(fromIndex, toIndex));
        subList.setObserver(getObserver());
        return subList;
    }

    // ----- Basic Delegates -----

    @Override
    public E get(int index) {

        return wrapped.get(index);
    }

    @Override
    public int indexOf(Object o) {

        return wrapped.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {

        return wrapped.lastIndexOf(o);
    }

    /**
     * An internal {@link ListIterator} implementation that wraps around a real list iterator and observes the addition and removal of elements.
     * It extends the {@link ObservableCollection.IteratorWrapper} in order to inherit its basic functionality.
     *
     * @see ObservableList
     */
    protected class ListIteratorWrapper extends IteratorWrapper implements ListIterator<E> {

        private final ListIterator<E> wrapped;

        /**
         * Creates a new list iterator wrapper that wraps around the given {@link ListIterator}.
         *
         * @param wrapped The list iterator the new list iterator wrapper wraps around.
         */
        protected ListIteratorWrapper(ListIterator<E> wrapped) {

            super(wrapped);

            this.wrapped = wrapped;
        }

        @Override
        public boolean hasPrevious() {

            return wrapped.hasPrevious();
        }

        @Override
        public E previous() {

            currentElement = wrapped.previous();
            return currentElement;
        }

        @Override
        public int nextIndex() {

            return wrapped.nextIndex();
        }

        @Override
        public int previousIndex() {

            return wrapped.previousIndex();
        }

        @Override
        public void set(E e) {

            wrapped.set(e);

            // If the setting was successful (no exception has been thrown), inform the observer about the affected elements
            getObserver().onRemove(currentElement);
            getObserver().onAdd(e);
        }

        @Override
        public void add(E e) {

            wrapped.add(e);

            // If the setting was successful (no exception has been thrown), inform the observer about the added element
            getObserver().onAdd(e);
        }

    }

}
