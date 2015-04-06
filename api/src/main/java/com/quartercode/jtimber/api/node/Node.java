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
import com.quartercode.jtimber.api.node.wrapper.Wrapper;
import com.quartercode.jtimber.api.node.wrapper.collection.ArrayWrapper;
import com.quartercode.jtimber.api.node.wrapper.collection.CollectionWrapper;

/**
 * Each tree node of any object tree which uses this framework must implement this interface.
 * That allows the node to be referenced as parent by other {@link ParentAware} objects.
 * For implementing that functionality, all <b>direct</b> references from a node to parent-aware objects are tracked using bytecode generated at runtime (arrays are not considered).
 * Additionally, the {@link Wrapper} mechanism is used to track references through arrays (see {@link ArrayWrapper}), collections (see {@link CollectionWrapper}), etc.<br>
 * <br>
 * Note that each node is parent-aware as well! That means that nodes also known the nodes that reference them (alias their parents).
 * Also note that a default implementation of this interface is provided: {@link DefaultNode}.<br>
 * <br>
 * Additionally, each node provides the {@link #getChildren()} method which returns all non-null attribute values of the node class.
 * Moreover, the {@link #getChildCount()} method returns just the amount of non-null attributes and is a lot faster.
 * Note that {@link Wrapper}s are properly resolved by those methods.
 * 
 * @param <P> The type of {@link Node}s that are able to be parents of this node.
 *        Note that all parents are verified against this type at runtime.
 *        Only parent nodes which are a compatible with this type are allowed.
 */
public interface Node<P extends Node<?>> extends ParentAware<P> {

    /**
     * Returns the values of all non-null attributes this class and all superclasses have.
     * Note that all objects and primitives are included in the returned list.
     * 
     * @return All non-null attributes of this class and all superclasses.
     */
    public List<Object> getChildren();

    /**
     * Returns the amount of non-null attributes this class and all superclasses have.
     * Note that all object and primitive attributes are included for the final result.<br>
     * <br>
     * Note that the result of this method should be the same as the {@link List#size() size} of the {@link #getChildren() children list}.
     * However, this method is probably is faster.
     * 
     * @return The amount of non-null attributes this class and all superclasses have.
     */
    public int getChildCount();

}
