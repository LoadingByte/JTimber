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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.ref.WeakReference;

/**
 * {@link Node} fields, which are annotated with this annotation and contain a {@link ParentAware} object, null themselves as soon as a referenced
 * object has no more {@link ParentAware#getParents() parents}.
 * In order to avoid inconsistencies, the referenced object should not be revived (by adding new parents to it) after it has been dereferenced.
 * That should not be done because the object disposal is lazy and can only happen when the field is <i>accessed</i> (from within the node class).
 * Therefore, you should also watch out for possible memory leaks! Weak references are not {@link WeakReference weak for the garbage collector}.<br>
 * <br>
 * Moreover, any {@link ParentAware} objects stored in annotated node fields don't know they are referenced by the annotated field.
 * For example, if a node object N holds a reference to a parent-aware object PA in an annotated field, PA doesn't have N in its parent list.
 * However, N has PA in its child list.
 */
@Target ({ ElementType.FIELD })
@Retention (RetentionPolicy.CLASS)
public @interface Weak {

}
