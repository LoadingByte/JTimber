/*
 * This file is part of JTimber.
 * Copyright (c) 2015 QuarterCode <http://www.quartercode.com/>
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

import java.util.Collection;

/**
 * A parent-aware object knows all {@link Node} that reference it in some way.
 * Those nodes are also called "parents".
 * The framework implements different ways to track these parents, including bytecode manipulation at runtime.<br>
 * <br>
 * Note that each node is parent-aware as well!
 * Also note that a default implementation of this interface is provided: {@link DefaultParentAware}.
 * 
 * @see DefaultParentAware
 * @see Node
 */
public interface ParentAware {

    /**
     * Returns all {@link Node}s that hold a reference to this parent-aware object in some way..
     * Those nodes are also called "parents" of this object.
     * 
     * @return All nodes that reference this object.
     */
    public Collection<Node> getParents();

    /**
     * Returns the amount of {@link Node}s that hold a reference to this parent-aware object in some way..
     * Those nodes are also called "parents" of this object.<br>
     * <br>
     * Note that the result of this method should be the same as the {@link Collection#size() size} of the {@link #getParents() parents collection}.
     * 
     * @return The amount of nodes that reference this object.
     */
    public int getParentCount();

    /**
     * <b>Internal</b> method for adding a parent {@link Node} to the {@link #getParents() parents collection}.
     * As a result of this method call the given node <b>must</b> be added to the parents collection (if it isn't {@code null}).
     * <b>Don't call this method if you don't have a reason to do it!</b>
     * 
     * @param parent The parent node to add to the parents collection.
     *        If this is {@code null}, nothing should happen. An exception should not be thrown.
     */
    public void addParent(Node parent);

    /**
     * <b>Internal</b> method for removing a parent {@link Node} from the {@link #getParents() parents collection}.
     * As a result of this method call the given node <b>must</b> be removed from the parents collection.
     * <b>Don't call this method if you don't have a reason to do it!</b>
     * 
     * @param parent The parent node to remove from the parents collection.
     *        If this is {@code null}, nothing should happen. An exception should not be thrown.
     */
    public void removeParent(Node parent);

}
