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
 * Also note that a default implementation of this interface is provided: {@link DefaultNode}.
 * 
 * @param <P> The type of {@link Node}s that are able to be parents of this node.
 *        Note that all parents are verified against this type at runtime.
 *        Only parent nodes which are a compatible with this type are allowed.
 */
public interface Node<P extends Node<?>> extends ParentAware<P> {

}
