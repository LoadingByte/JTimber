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
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A class that wraps around any class that implements the {@link Map} interface and adds observing functionalities for the different manipulating methods.
 * See {@link AbstractObservableCollection} for more details on what observable collections are and what they are used for.
 * Note that this class also implements the map interface in order to allow it being used like any other map.<br>
 * <br>
 * However, some limitations are imposed on some optional operations.
 * Firstly, the collections returned by {@link #keySet()} and {@link #values()} are not modifiable.
 * Secondly, it is disallowed (but sadly not enforced) to call {@link java.util.Map.Entry#setValue(Object)} on an entry from the {@link #entrySet() entry set}.
 *
 * @param <K> The type of keys maintained by the wrapped map.
 * @param <V> The type of mapped values in the wrapped map.
 * @see Map
 * @see AbstractObservableCollection
 */
public class ObservableMap<K, V> extends AbstractObservableCollection implements Map<K, V> {

    private final Map<K, V> wrapped;

    /**
     * Creates a new observable {@link Map} that wraps around the given map.
     *
     * @param wrapped The map the new observable map wraps around.
     */
    public ObservableMap(Map<K, V> wrapped) {

        super(wrapped);

        this.wrapped = wrapped;
    }

    @Override
    public V put(K key, V value) {

        boolean isKeyNew = !containsKey(key);

        V oldValue = wrapped.put(key, value);

        // If the putting was successful (no exception has been thrown), inform the observer about the affected elements
        if (isKeyNew) {
            getObserver().onAdd(key);
        }
        getObserver().onRemove(oldValue);
        getObserver().onAdd(value);

        return oldValue;
    }

    @Override
    public V remove(Object key) {

        if (containsKey(key)) {
            V oldValue = wrapped.remove(key);

            // If the removal was successful (no exception has been thrown), inform the observer about the removed elements
            getObserver().onRemove(key);
            getObserver().onRemove(oldValue);
            return oldValue;
        } else {
            return null;
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

        for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {

        Set<Entry<K, V>> entrySet = new HashSet<>(wrapped.entrySet());

        // Actually clear the map
        wrapped.clear();

        // If the clearing was successful (no exception has been thrown), inform the observer about the removed elements
        for (Entry<K, V> entry : entrySet) {
            getObserver().onRemove(entry.getKey());
            getObserver().onRemove(entry.getValue());
        }
    }

    @Override
    public Set<K> keySet() {

        return Collections.unmodifiableSet(wrapped.keySet());
    }

    @Override
    public Collection<V> values() {

        return Collections.unmodifiableCollection(wrapped.values());
    }

    /*
     * Note that this method does not take care of catching any call to Entry.setValue() because that would be to difficult.
     * Therefore, that call should not be made.
     */
    @Override
    public Set<Entry<K, V>> entrySet() {

        ObservableSet<Entry<K, V>> entrySet = new ObservableSet<>(wrapped.entrySet());

        entrySet.setObserver(new CollectionObserver() {

            @Override
            public void onAdd(Object object) {

                // Not needed since element addition is disallowed in entry sets
            }

            @Override
            public void onRemove(Object object) {

                Entry<?, ?> entry = (Entry<?, ?>) object;
                getObserver().onRemove(entry.getKey());
                getObserver().onRemove(entry.getValue());
            }

        });

        return entrySet;
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
