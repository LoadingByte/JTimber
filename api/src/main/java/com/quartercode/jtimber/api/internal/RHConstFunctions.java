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

package com.quartercode.jtimber.api.internal;

import java.util.List;
import com.quartercode.jtimber.api.node.wrapper.Wrapper;

/**
 * This internal class contains some functions used by the code injected by the runtime hook.
 * These methods are extracted into real java code in order to make their behavior more transparent and maintainable.
 * Otherwise, the runtime hook would have to manually inject their functionality into the bytecode, making it more difficult to understand.
 */
public class RHConstFunctions {

    // ----- Children -----

    /**
     * Adds the given child object to the given list (order of parameters is chosen for maximum efficiency).
     * If the child object is {@code null}, nothing is added to the list.
     * If the child object is a {@link Wrapper}, the {@link Wrapper#getActualChildren() actual children} of that wrapper are added to the list.
     * Moreover, if the actual children of the wrapper are also wrappers, those wrappers are resolved as well.
     * Actually, this continues recursively; no wrappers and only actual children will ever be added to the list.
     * 
     * @param list The list the child object should be added to.
     * @param child The child object to add to the given list.
     */
    public static void addActualChildrenToList(List<Object> list, Object child) {

        if (child != null) {
            if (child instanceof Wrapper) {
                for (Object actualChild : ((Wrapper) child).getActualChildren()) {
                    addActualChildrenToList(list, actualChild);
                }
            } else {
                list.add(child);
            }
        }
    }

    /**
     * Counts the amount of actual children represented by the given child object.
     * If the child object is {@code null}, {@code 0} is returned.
     * If the child object is any other object that a {@link Wrapper}, {@code 1} is returned.
     * If the child object is a wrapper, the actual children are resolved (as described in {@link #addActualChildrenToList(List, Object)}) and their count is returned.
     * 
     * @param child The child which represents the returned amount of actual children.
     * @return The amount of actual children represented by the given child object.
     */
    public static int countActualChildren(Object child) {

        if (child == null) {
            return 0;
        } else if (child instanceof Wrapper) {
            int count = 0;

            for (Object actualChild : ((Wrapper) child).getActualChildren()) {
                count += countActualChildren(actualChild);
            }

            return count;
        } else {
            return 1;
        }
    }

    private RHConstFunctions() {

    }

}
