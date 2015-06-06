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

import com.quartercode.jtimber.api.internal.observ.collection.CollectionObserver;
import com.quartercode.jtimber.api.node.Node;
import com.quartercode.jtimber.api.node.ParentAware;
import com.quartercode.jtimber.api.node.wrapper.Wrapper;

/**
 * A {@link CollectionObserver} that copies the parents of a certain {@link ParentAware} object into each parent-aware element that is added to or removed from the observed collection.
 * It is used by {@link Wrapper}s like the {@link CollectionWrapper}.
 */
public class CopyParentsCollectionObserver implements CollectionObserver {

    private final ParentAware<Node<?>> source;

    /**
     * Creates a new copy parents collection observer.
     *
     * @param source The {@link ParentAware} object whose parents should be copied into each parent-aware element that is added to or removed from the observed collection.
     */
    public CopyParentsCollectionObserver(ParentAware<Node<?>> source) {

        this.source = source;
    }

    @Override
    public void onAdd(Object object) {

        if (object instanceof ParentAware) {
            for (Node<?> parent : source.getParents()) {
                ((ParentAware<?>) object).addParent(parent);
            }
        }
    }

    @Override
    public void onRemove(Object object) {

        if (object instanceof ParentAware) {
            for (Node<?> parent : source.getParents()) {
                ((ParentAware<?>) object).removeParent(parent);
            }
        }
    }

}
