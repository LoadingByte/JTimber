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

package com.quartercode.jtimber.api.node.util;

import java.util.List;
import com.quartercode.jtimber.api.node.Node;
import com.quartercode.jtimber.api.node.ParentAware;

/**
 * This utility class contains some methods related to {@link ParentAware#getParents() parents} of {@link ParentAware} objects.
 * 
 * @see ParentAware
 */
public class ParentUtils {

    /**
     * Walks up the parent tree and returns the first {@link Node} of the given type it finds.
     * Firstly, this method checks whether any of the direct parents of the given {@link ParentAware} object is an instance of the given type.
     * If that search is not successful, it calls this method recursively for each parent of the given parent-aware object.
     * The first one which finds a node of the requested type is returned as the result.
     * If absolutely no matching node can be found, {@code null} is returned.
     * 
     * @param type The node type to search for.
     *        The returned node must be an instance of this type.
     * @param object The parent-aware object the search should be started on.
     * @return The first found node of the given type it, or {@code null} if no matching node can be found.
     */
    @SuppressWarnings ("unchecked")
    public static <T extends Node<?>> T getFirstParentOfType(Class<T> type, ParentAware<? extends Node<?>> object) {

        List<? extends Node<?>> parents = object.getParents();

        for (Node<?> parent : parents) {
            if (type.isInstance(parent)) {
                return (T) parent;
            }
        }

        for (Node<?> parent : parents) {
            T foundParent = getFirstParentOfType(type, parent);

            if (foundParent != null) {
                return foundParent;
            }
        }

        return null;
    }

    private ParentUtils() {

    }

}
