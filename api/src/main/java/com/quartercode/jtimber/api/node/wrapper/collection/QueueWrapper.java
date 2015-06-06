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

import java.util.Queue;
import com.quartercode.jtimber.api.internal.observ.collection.ObservableQueue;
import com.quartercode.jtimber.api.node.wrapper.Wrapper;

/**
 * A {@link Wrapper} around any class that implements the {@link Queue} interface.
 * See {@link Wrapper} for more details on what wrappers are and what they are used for.<br>
 * <br>
 * Note that this wrapper also implements the queue interface in order to allow it being used like any other queue.
 * Also note that the wrapper extends the {@link CollectionWrapper} in order to inherit its functionality.
 *
 * @param <E> The type of elements in the wrapped queue.
 * @see Queue
 * @see Wrapper
 * @see CollectionWrapper
 */
public class QueueWrapper<E> extends CollectionWrapper<E> implements Queue<E> {

    private final ObservableQueue<E> wrapped;

    /**
     * Creates a new {@link Queue} {@link Wrapper} that wraps around the given queue.
     *
     * @param wrapped The queue the new list wrapper wraps around.
     */
    public QueueWrapper(Queue<E> wrapped) {

        super(wrapped);

        this.wrapped = new ObservableQueue<>(wrapped);
        this.wrapped.setObserver(new CopyParentsCollectionObserver(this));
    }

    // ----- Delegates -----

    @Override
    public boolean offer(E e) {

        return wrapped.offer(e);
    }

    @Override
    public E remove() {

        return wrapped.remove();
    }

    @Override
    public E poll() {

        return wrapped.poll();
    }

    @Override
    public E element() {

        return wrapped.element();
    }

    @Override
    public E peek() {

        return wrapped.peek();
    }

}
