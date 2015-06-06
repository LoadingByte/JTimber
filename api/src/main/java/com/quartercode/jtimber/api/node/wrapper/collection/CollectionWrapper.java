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
import java.util.Collections;
import java.util.Iterator;
import com.quartercode.jtimber.api.internal.observ.collection.ObservableCollection;
import com.quartercode.jtimber.api.node.Node;
import com.quartercode.jtimber.api.node.ParentAware;
import com.quartercode.jtimber.api.node.wrapper.AbstractWrapper;
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
public class CollectionWrapper<E> extends AbstractWrapper implements Collection<E> {

    private final ObservableCollection<E> wrapped;

    /**
     * Creates a new {@link Collection} {@link Wrapper} that wraps around the given collection.
     *
     * @param wrapped The collection the new collection wrapper wraps around.
     */
    public CollectionWrapper(Collection<E> wrapped) {

        super(wrapped);

        this.wrapped = new ObservableCollection<>(wrapped);
        this.wrapped.setObserver(new CopyParentsCollectionObserver(this));
    }

    @Override
    public Collection<?> getActualChildren() {

        return Collections.unmodifiableCollection(wrapped);
    }

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

    // ----- Delegates -----

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
    public Iterator<E> iterator() {

        return wrapped.iterator();
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
    public boolean add(E e) {

        return wrapped.add(e);
    }

    @Override
    public boolean remove(Object o) {

        return wrapped.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {

        return wrapped.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {

        return wrapped.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {

        return wrapped.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {

        return wrapped.retainAll(c);
    }

    @Override
    public void clear() {

        wrapped.clear();
    }

}
