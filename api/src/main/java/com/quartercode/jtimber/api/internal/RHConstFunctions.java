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
import com.quartercode.jtimber.api.node.Node;
import com.quartercode.jtimber.api.node.ParentAware;
import com.quartercode.jtimber.api.node.wrapper.Wrapper;

/**
 * This internal class contains some functions used by the code injected by the runtime hook.
 * These methods are extracted into real java code in order to make their behavior more transparent and maintainable.
 * Otherwise, the runtime hook would have to manually inject their functionality into the bytecode, making it more difficult to understand.
 */
public class RHConstFunctions {

    // ----- Parents -----

    /**
     * Adds the given parent {@link Node} to or removes it from the given child object <b>if</b> the child object is not {@code null} and {@link ParentAware}.
     * Otherwise, nothing happens.
     * 
     * @param child The child object the given parent node should be added to or removed from.
     * @param parent The parent node which should be added to or removed from the given child object.
     * @param add Whether the given parent node should be added to ({@code true}) or the removed from ({@code false}) the given child object.
     */
    public static void addOrRemoveParent(Object child, Node<?> parent, boolean add) {

        if (child instanceof ParentAware) {
            if (add) {
                ((ParentAware<?>) child).addParent(parent);
            } else {
                ((ParentAware<?>) child).removeParent(parent);
            }
        }
    }

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

    // ----- Wrappers -----

    /**
     * Tries to wrap the given original object in a new wrapper of the given type.
     * That is done by constructing a new wrapper using the original object as first and only constructor argument.
     * 
     * @param forWrapping The object that should be wrapped.
     * @param wrapperType The type of the wrapper that should be constructed.
     * @param wrapperConstructorArgType The type of the single argument of the wrapper constructor that should be called.
     *        Note that the given {@code forWrapping} object must be an instance of this type.
     *        Otherwise, the object is returned unchanged.
     * @return The new wrapper around the given {@code forWrapping} object.
     *         If the given original object is not compatible with the wrapper, the object is returned unchanged.
     */
    public static Object tryWrap(Object forWrapping, Class<? extends Wrapper> wrapperType, Class<?> wrapperConstructorArgType) {

        // If the object to be wrapped is null or not an instance of the specified wrapper constructor arg type, return the original object without wrapping it
        if (!wrapperConstructorArgType.isInstance(forWrapping)) {
            return forWrapping;
        }

        // Otherwise, try to create and return a new wrapper that wraps around the given object
        try {
            return wrapperType.getConstructor(wrapperConstructorArgType).newInstance(forWrapping);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Cannot find constructor 'public " + wrapperType.getSimpleName() + " (" + wrapperConstructorArgType.getName() + ")'"
                    + " in wrapper '" + wrapperType.getName() + "'.\n"
                    + "Try to specify a custom wrapper constructor argument type in the according @SubstituteWithWrapper annotation"
                    + " (you can find the affected field by looking at the second stack trace entry).\n"
                    + "For example, for 'ListWrapper' the argument type would be 'java.util.List'.", e);
        } catch (Exception e) {
            throw new RuntimeException("Error while trying to create new instance of wrapper '" + wrapperType.getName() + "'"
                    + " with constructor arg '" + forWrapping + "' (is of type '" + forWrapping.getClass().getName() + "'"
                    + ", minimum type is '" + wrapperConstructorArgType.getName() + "')", e);
        }
    }

    private RHConstFunctions() {

    }

}
