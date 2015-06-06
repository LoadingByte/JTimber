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

import java.util.Queue;
import com.quartercode.jtimber.api.node.wrapper.collection.CollectionWrapper;

/**
 * A class that wraps around any class that implements the {@link Queue} interface and adds observing functionalities for the different manipulating methods.
 * See {@link AbstractObservableCollection} for more details on what observable collections are and what they are used for.
 * Note that this class also implements the collection interface in order to allow it being used like any other collection.
 * Also note that the wrapper extends the {@link CollectionWrapper} in order to inherit its functionality.
 *
 * @param <E> The type of elements in the wrapped queue.
 * @see Queue
 * @see AbstractObservableCollection
 * @see ObservableCollection
 */
public class ObservableQueue<E> extends ObservableCollection<E> implements Queue<E> {

    private final Queue<E> wrapped;

    /**
     * Creates a new observable {@link Queue} that wraps around the given queue.
     *
     * @param wrapped The queue the new observable queue wraps around.
     */
    public ObservableQueue(Queue<E> wrapped) {

        super(wrapped);

        this.wrapped = wrapped;
    }

    @Override
    public boolean offer(E e) {

        boolean modified = wrapped.offer(e);

        if (modified) {
            getObserver().onAdd(e);
        }

        return modified;
    }

    @Override
    public E remove() {

        E element = wrapped.remove();
        getObserver().onRemove(element);
        return element;
    }

    @Override
    public E poll() {

        E element = wrapped.poll();
        getObserver().onRemove(element);
        return element;
    }

    // ----- Basic Delegates -----

    @Override
    public E element() {

        return wrapped.element();
    }

    @Override
    public E peek() {

        return wrapped.peek();
    }

}
