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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The default implementation of the {@link ParentAware} interface.
 * It stores the parent collection using an {@link ArrayList}, held in a {@code transient} field.
 * 
 * @see ParentAware
 */
public class DefaultParentAware implements ParentAware {

    private final transient List<Node> parents             = new ArrayList<>();
    private final transient List<Node> parentsUnmodifiable = Collections.unmodifiableList(parents);

    @Override
    public List<Node> getParents() {

        return parentsUnmodifiable;
    }

    @Override
    public int getParentCount() {

        return parents.size();
    }

    @Override
    public void addParent(Node parent) {

        // Abort if the parent is null
        if (parent == null) {
            return;
        }

        parents.add(parent);
    }

    @Override
    public void removeParent(Node parent) {

        parents.remove(parent);
    }

}
