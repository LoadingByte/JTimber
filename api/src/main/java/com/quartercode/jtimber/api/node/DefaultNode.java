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
 * The default implementation of the {@link Node} interface.
 * The child accessors are overridden for each implementation of {@link Node} (not only DefaultNode) at runtime.
 * Therefore, this class just provides empty dummy methods which do nothing (because they will be removed at runtime).
 * For the implementation of {@link ParentAware} functionality, it just extends the {@link DefaultParentAware} implementation.
 *
 * @param <P> The type of {@link Node}s that are able to be parents of this node.
 *        Note that all parents are verified against this type at runtime.
 *        Only parent nodes which are a compatible with this type are allowed.
 *
 * @see Node
 * @see DefaultParentAware
 */
public class DefaultNode<P extends Node<?>> extends DefaultParentAware<P> implements Node<P> {

    /*
     * The following two methods are just dummies and will be overridden at runtime.
     */

    @Override
    public List<Object> getChildren() {

        return null;
    }

    @Override
    public int getChildCount() {

        return 0;
    }

}
