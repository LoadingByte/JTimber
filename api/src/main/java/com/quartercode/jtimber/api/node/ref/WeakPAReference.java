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

package com.quartercode.jtimber.api.node.ref;

import java.lang.ref.WeakReference;
import com.quartercode.jtimber.api.node.ParentAware;

/**
 * An immutable reference to a {@link ParentAware} object that nulls itself as soon as the referenced object has no more {@link ParentAware#getParents() parents}.
 * In order to avoid inconsistencies, the referenced object should not be revived (by adding new parents to it) after it has been dereferenced.
 * Note that an object which holds a weak PA reference doesn't count as a parent of the referenced parent-aware object.<br>
 * <br>
 * Also note that the referenced object is stored using a {@link WeakReference}.
 * That means that the object can be garbage collected, even if the parent count is wrong for some reason.
 * 
 * @param <T> The exact type of {@link ParentAware} object referenced by the weak PA reference.
 */
public class WeakPAReference<T extends ParentAware> {

    private final WeakReference<T> reference;

    /**
     * Creates a new weak PA reference which references the given {@link ParentAware} object.
     * See {@link WeakPAReference} for more information on what a weak PA reference is.
     * Note that a weak PA reference is immutable; because of that, the referenced PA cannot be changed after the reference has been constructed.
     * 
     * @param pa The parent-aware object which should be referenced by the new weak PA reference.
     */
    public WeakPAReference(T pa) {

        reference = new WeakReference<>(pa);
    }

    /**
     * Returns the referenced {@link ParentAware} object, or {@code null} if the object has no more {@link ParentAware#getParents() parents}.
     * See {@link WeakPAReference} for more information on what a weak PA reference is.
     * 
     * @return The referenced parent-aware object, or {@code null} if it has been dereferenced.
     */
    public T get() {

        T pa = reference.get();

        if (pa != null && pa.getParentCount() == 0) {
            reference.clear();
            return null;
        } else {
            return pa;
        }
    }

}
