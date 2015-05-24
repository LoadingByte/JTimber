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

package com.quartercode.jtimber.api.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.jodah.typetools.TypeResolver;

/**
 * The default implementation of the {@link ParentAware} interface.
 * It stores the parent collection using an {@link ArrayList}, held in a {@code transient} field.
 *
 * @param <P> The type of {@link Node}s that are able to be parents of this parent-aware object.
 *        Note that all parents are verified against this type at runtime.
 *        Only parent nodes which are a compatible with this type are allowed.
 *
 * @see ParentAware
 */
public class DefaultParentAware<P extends Node<?>> implements ParentAware<P> {

    private final transient List<P> parents             = new ArrayList<>();
    private final transient List<P> parentsUnmodifiable = Collections.unmodifiableList(parents);

    @Override
    public List<P> getParents() {

        return parentsUnmodifiable;
    }

    @Override
    public P getSingleParent() {

        if (parents.isEmpty()) {
            // If there are no parents, return null
            return null;
        } else {
            P firstParent = parents.get(0);

            if (parents.size() == 1) {
                // If there is only one parent, return that parent
                return firstParent;
            } else {
                // If there are multiple parents, check whether all parents are identical to the first parent in the list
                // In other words: Check whether one single node references this object multiple times
                for (int index = 1; index < parents.size(); index++) {
                    if (parents.get(index) != firstParent) {
                        // If that isn't the case, throw an exception
                        throw new MultipleParentsException(this, "Cannot use getSingleParent() on instance of '" + getClass().getName() + "' with " + parents.size() + " parents");
                    }
                }

                // If that is the case, return the first parent (all parents are identical)
                return firstParent;
            }
        }
    }

    @Override
    public int getParentCount() {

        return parents.size();
    }

    @SuppressWarnings ("unchecked")
    @Override
    public void addParent(Node<?> parent) {

        if (parent != null) {
            Class<?> allowedParentClass = TypeResolver.resolveRawArgument(ParentAware.class, getClass());
            boolean allowedParent = allowedParentClass.isAssignableFrom(parent.getClass());

            if (allowedParent || allowedParentClass == TypeResolver.Unknown.class) {
                // This unchecked cast cannot be avoided; however, the check above should have filtered out any disallowed parent
                parents.add((P) parent);
            } else {
                throw new IllegalParentTypeException(this, parent, "Nodes of type '" + parent.getClass().getName() + "' are not allowed to reference parent-aware objects of type '" + getClass().getName() + "',"
                        + "with latter only accepting '" + allowedParentClass.getName() + "' parents");
            }
        }
    }

    @Override
    public void removeParent(Node<?> parent) {

        parents.remove(parent);
    }

}
