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

/**
 * Any {@link ParentAware} objects stored in {@link Node} fields annotated with this annotation don't know they are referenced by the annotated field.
 * For example, if a node object N holds a reference to a parent-aware object PA in an annotated field, PA doesn't have N in its parent list.
 * However, N has PA in its child list.
 * Note that the annotation doesn't change anything else.
 */
@Target ({ ElementType.FIELD })
@Retention (RetentionPolicy.CLASS)
public @interface Weak {

}
