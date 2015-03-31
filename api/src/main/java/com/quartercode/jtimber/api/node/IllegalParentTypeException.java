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

/**
 * This exception is thrown when a disallowed "parent" {@link Node} references a {@link ParentAware} object.
 */
public class IllegalParentTypeException extends RuntimeException {

    private static final long    serialVersionUID = -2894391058589444998L;

    private final ParentAware<?> object;
    private final Node<?>        parent;

    /**
     * Creates a new illegal parent type exception.
     * 
     * @param object The {@link ParentAware} object that was referenced by the disallowed given "parent" {@link Node}.
     * @param parent The disallowed "parent" node that referenced the given parent-aware object.
     */
    public IllegalParentTypeException(ParentAware<?> object, Node<?> parent) {

        this.object = object;
        this.parent = parent;
    }

    /**
     * Creates a new illegal parent type exception with a detail message that describes the error.
     * 
     * @param object The {@link ParentAware} object that was referenced by the disallowed given "parent" {@link Node}.
     * @param parent The disallowed "parent" node that referenced the given parent-aware object.
     * @param message A detail message which describes the error that occurred.
     */
    public IllegalParentTypeException(ParentAware<?> object, Node<?> parent, String message) {

        super(message);

        this.object = object;
        this.parent = parent;
    }

    /**
     * Creates a new illegal parent type exception with the exception that caused the error.
     * 
     * @param object The {@link ParentAware} object that was referenced by the disallowed given "parent" {@link Node}.
     * @param parent The disallowed "parent" node that referenced the given parent-aware object.
     * @param cause The exception which caused the error in the first place.
     */
    public IllegalParentTypeException(ParentAware<?> object, Node<?> parent, Throwable cause) {

        super(cause);

        this.object = object;
        this.parent = parent;
    }

    /**
     * Creates a new illegal parent type exception with a detail message that describes the error and the exception that caused it.
     * 
     * @param object The {@link ParentAware} object that was referenced by the disallowed given "parent" {@link Node}.
     * @param parent The disallowed "parent" node that referenced the given parent-aware object.
     * @param message A detail message which describes the error that occurred.
     * @param cause The exception which caused the error in the first place.
     */
    public IllegalParentTypeException(ParentAware<?> object, Node<?> parent, String message, Throwable cause) {

        super(message, cause);

        this.object = object;
        this.parent = parent;
    }

    /**
     * Returns the {@link ParentAware} object that was referenced by the disallowed "parent" {@link Node}.
     * Its {@link ParentAware#addParent(Node)} method probably threw this exception.
     * 
     * @return The affected parent-aware object.
     */
    public ParentAware<?> getObject() {

        return object;
    }

    /**
     * Returns the disallowed "parent" {@link Node} which referenced the {@link ParentAware} returned by {@link #getObject()}.
     * 
     * @return The disallowed "parent" node.
     */
    public Node<?> getParent() {

        return parent;
    }

}
