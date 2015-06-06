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
import java.util.List;
import java.util.ListIterator;
import com.quartercode.jtimber.api.internal.observ.collection.ObservableList;
import com.quartercode.jtimber.api.node.wrapper.Wrapper;

/**
 * A {@link Wrapper} around any class that implements the {@link List} interface.
 * See {@link Wrapper} for more details on what wrappers are and what they are used for.<br>
 * <br>
 * Note that this wrapper also implements the list interface in order to allow it being used like any other list.
 * Also note that the wrapper extends the {@link CollectionWrapper} in order to inherit its functionality.
 *
 * @param <E> The type of elements in the wrapped list.
 * @see List
 * @see Wrapper
 * @see CollectionWrapper
 */
public class ListWrapper<E> extends CollectionWrapper<E> implements List<E> {

    private final ObservableList<E> wrapped;

    /**
     * Creates a new {@link List} {@link Wrapper} that wraps around the given list.
     *
     * @param wrapped The list the new list wrapper wraps around.
     */
    public ListWrapper(List<E> wrapped) {

        super(wrapped);

        this.wrapped = new ObservableList<>(wrapped);
        this.wrapped.setObserver(new CopyParentsCollectionObserver(this));
    }

    // ----- Delegates -----

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {

        return wrapped.addAll(index, c);
    }

    @Override
    public E get(int index) {

        return wrapped.get(index);
    }

    @Override
    public E set(int index, E element) {

        return wrapped.set(index, element);
    }

    @Override
    public void add(int index, E element) {

        wrapped.add(index, element);
    }

    @Override
    public E remove(int index) {

        return wrapped.remove(index);
    }

    @Override
    public int indexOf(Object o) {

        return wrapped.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {

        return wrapped.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {

        return wrapped.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {

        return wrapped.listIterator(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {

        return wrapped.subList(fromIndex, toIndex);
    }

}
