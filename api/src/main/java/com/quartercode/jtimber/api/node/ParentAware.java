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

import java.util.List;

/**
 * A parent-aware object knows all {@link Node}s that reference it in some way.
 * Those nodes are also called "parents".
 * The framework implements different ways to track these parents, including bytecode manipulation at runtime.<br>
 * <br>
 * Note that each node is parent-aware as well!
 * Also note that a default implementation of this interface is provided: {@link DefaultParentAware}.
 * 
 * @param <P> The type of {@link Node}s that are able to be parents of this parent-aware object.
 *        Note that all parents are verified against this type at runtime.
 *        Only parent nodes which are a compatible with this type are allowed.
 * 
 * @see DefaultParentAware
 * @see Node
 */
public interface ParentAware<P extends Node<?>> {

    /**
     * Returns all {@link Node}s that hold a reference to this parent-aware object in some way.
     * Those nodes are also called "parents" of this object.
     * If a parent node references this object multiple times, the returned list contains that parent node multiple times as well.
     * 
     * @return All nodes that reference this object.
     */
    public List<P> getParents();

    /**
     * Returns the one and only {@link Node} that holds a reference to this parent-aware object in some way.
     * That single node is also called the "parent" of this object.
     * However, this method throws a {@link MultipleParentsException} if this object is referenced by multiple nodes.
     * Note that the exception is not thrown if the same object references this object multiple times (e.g. through multiple fields).
     * 
     * @return The one and only node that references this object.
     * @throws MultipleParentsException
     */
    public P getSingleParent();

    /**
     * Returns the amount of {@link Node}s that hold a reference to this parent-aware object in some way.
     * Those nodes are also called "parents" of this object.<br>
     * <br>
     * Note that the result of this method should be the same as the {@link List#size() size} of the {@link #getParents() parents list}.
     * However, this method probably is faster.
     * 
     * @return The amount of nodes that reference this object.
     */
    public int getParentCount();

    /**
     * <b>Internal</b> method for adding a parent {@link Node} to the {@link #getParents() parents list}.
     * As a result of this method call the given node <b>must</b> be added to the parents list (if it isn't {@code null} or disallowed).
     * The node must even be added if it already exists.
     * <b>Don't call this method if you don't have a reason to do it!</b><br>
     * <br>
     * Note that this method enforces the type limit imposed by the generic type parameter {@code <P>}.
     * All nodes which are not compatible (same type or subtype) with that generic type parameter cause an {@link IllegalParentTypeException} to be thrown.
     * However, this method still accepts all nodes in order to make the implementation of wrappers easier.
     * 
     * @param parent The parent node to add to the parents list.
     *        If this is {@code null}, nothing should happen. An exception should not be thrown.
     * @throws IllegalParentTypeException
     */
    public void addParent(Node<?> parent);

    /**
     * <b>Internal</b> method for removing a parent {@link Node} from the {@link #getParents() parents list}.
     * As a result of this method call the given node <b>must</b> be removed from the parents list (if it isn't {@code null}).
     * The node must only be removed once; if it exists twice in the list, only one entry should be removed.
     * <b>Don't call this method if you don't have a reason to do it!</b>
     * 
     * @param parent The parent node to remove from the parents list.
     *        If this is {@code null}, nothing should happen. An exception should not be thrown.
     */
    public void removeParent(Node<?> parent);

}
