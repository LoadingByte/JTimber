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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.quartercode.jtimber.api.internal.observ.collection.ObservableMap;
import com.quartercode.jtimber.api.node.Node;
import com.quartercode.jtimber.api.node.ParentAware;
import com.quartercode.jtimber.api.node.wrapper.AbstractWrapper;
import com.quartercode.jtimber.api.node.wrapper.Wrapper;

/**
 * A {@link Wrapper} around any class that implements the {@link Map} interface.
 * See {@link Wrapper} for more details on what wrappers are and what they are used for.<br>
 * <br>
 * Note that this wrapper also implements the map interface in order to allow it being used like any other map.
 * However, some limitations are imposed on some optional operations.
 * Firstly, the collections returned by {@link #keySet()} and {@link #values()} are not modifiable.
 * Secondly, it is disallowed (but sadly not enforced) to call {@link java.util.Map.Entry#setValue(Object)} on an entry from the {@link #entrySet() entry set}.
 *
 * @param <K> The type of keys maintained by the wrapped map.
 * @param <V> The type of mapped values in the wrapped map.
 * @see Map
 * @see Wrapper
 */
public class MapWrapper<K, V> extends AbstractWrapper implements Map<K, V> {

    private final ObservableMap<K, V> wrapped;

    /**
     * Creates a new {@link Map} {@link Wrapper} that wraps around the given map.
     *
     * @param wrapped The map the new map wrapper wraps around.
     */
    public MapWrapper(Map<K, V> wrapped) {

        super(wrapped);

        this.wrapped = new ObservableMap<>(wrapped);
        this.wrapped.setObserver(new CopyParentsCollectionObserver(this));
    }

    @Override
    public Collection<?> getActualChildren() {

        List<Object> actualChildren = new ArrayList<>();

        actualChildren.addAll(wrapped.keySet());
        actualChildren.addAll(wrapped.values());

        return actualChildren;
    }

    @Override
    public void addParent(Node<?> parent) {

        super.addParent(parent);

        for (Entry<K, V> entry : wrapped.entrySet()) {
            if (entry.getKey() instanceof ParentAware) {
                ((ParentAware<?>) entry.getKey()).addParent(parent);
            }
            if (entry.getValue() instanceof ParentAware) {
                ((ParentAware<?>) entry.getValue()).addParent(parent);
            }
        }
    }

    @Override
    public void removeParent(Node<?> parent) {

        super.removeParent(parent);

        for (Entry<K, V> entry : wrapped.entrySet()) {
            if (entry.getKey() instanceof ParentAware) {
                ((ParentAware<?>) entry.getKey()).removeParent(parent);
            }
            if (entry.getValue() instanceof ParentAware) {
                ((ParentAware<?>) entry.getValue()).removeParent(parent);
            }
        }
    }

    // ----- Delegates -----

    @Override
    public V put(K key, V value) {

        return wrapped.put(key, value);
    }

    @Override
    public V remove(Object key) {

        return wrapped.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

        wrapped.putAll(m);
    }

    @Override
    public void clear() {

        wrapped.clear();
    }

    @Override
    public Set<K> keySet() {

        return wrapped.keySet();
    }

    @Override
    public Collection<V> values() {

        return wrapped.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {

        return wrapped.entrySet();
    }

    @Override
    public int size() {

        return wrapped.size();
    }

    @Override
    public boolean isEmpty() {

        return wrapped.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {

        return wrapped.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {

        return wrapped.containsValue(value);
    }

    @Override
    public V get(Object key) {

        return wrapped.get(key);
    }

}
