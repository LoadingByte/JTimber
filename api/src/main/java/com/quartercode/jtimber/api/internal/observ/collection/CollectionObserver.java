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

import java.util.Map;

/**
 * A collection observer is informed whenever an object is added to or removed from an {@link AbstractObservableCollection observable collection}.
 * Therefore, it must provide two callback methods that can be invoked by the observable collection.
 *
 * @see AbstractObservableCollection
 */
public interface CollectionObserver {

    /**
     * This callback is invoked <b>after</b> a new object has been added to the {@link AbstractObservableCollection observable collection} this class is assigned to.
     * In case of a {@link Map}, both keys and values are sent through this method when they are added.
     *
     * @param object The object that is added to the underlying collection.
     */
    public void onAdd(Object object);

    /**
     * This callback is invoked <b>after</b> an object has been removed from the {@link AbstractObservableCollection observable collection} this class is assigned to.
     * In case of a {@link Map}, both keys and values are sent through this method when they are remove.
     *
     * @param object The object that is removed from the underlying collection.
     */
    public void onRemove(Object object);

}
