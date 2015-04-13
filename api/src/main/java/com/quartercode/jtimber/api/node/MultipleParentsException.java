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
 * This exception is thrown when the {@link ParentAware#getSingleParent()} method is called although the parent-aware object has multiple parents.
 * Since this exception indicates a conceptual error, it is a {@link RuntimeException}.
 */
public class MultipleParentsException extends RuntimeException {

    private static final long    serialVersionUID = 9200758242915690131L;

    private final ParentAware<?> object;

    /**
     * Creates a new multiple parents exception.
     * 
     * @param object The {@link ParentAware} object the {@link ParentAware#getSingleParent()} method was called on although the object has multiple parents.
     */
    public MultipleParentsException(ParentAware<?> object) {

        this.object = object;
    }

    /**
     * Creates a new multiple parents exception with a detail message that describes the error.
     * 
     * @param object The {@link ParentAware} object the {@link ParentAware#getSingleParent()} method was called on although the object has multiple parents.
     * @param message A detail message which describes the error that occurred.
     */
    public MultipleParentsException(ParentAware<?> object, String message) {

        super(message);

        this.object = object;
    }

    /**
     * Creates a new multiple parents exception with the exception that caused the error.
     * 
     * @param object The {@link ParentAware} object the {@link ParentAware#getSingleParent()} method was called on although the object has multiple parents.
     * @param cause The exception which caused the error in the first place.
     */
    public MultipleParentsException(ParentAware<?> object, Throwable cause) {

        super(cause);

        this.object = object;
    }

    /**
     * Creates a new multiple parents exception with a detail message that describes the error and the exception that caused it.
     * 
     * @param object The {@link ParentAware} object the {@link ParentAware#getSingleParent()} method was called on although the object has multiple parents.
     * @param message A detail message which describes the error that occurred.
     * @param cause The exception which caused the error in the first place.
     */
    public MultipleParentsException(ParentAware<?> object, String message, Throwable cause) {

        super(message, cause);

        this.object = object;
    }

    /**
     * Returns the {@link ParentAware} object the {@link ParentAware#getSingleParent()} method was called on although the object has multiple parents.
     * 
     * @return The affected parent-aware object.
     */
    public ParentAware<?> getObject() {

        return object;
    }

}
