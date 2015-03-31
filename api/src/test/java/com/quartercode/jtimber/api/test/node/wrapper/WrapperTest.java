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

package com.quartercode.jtimber.api.test.node.wrapper;

import static org.junit.Assert.*;
import org.junit.Test;
import com.quartercode.jtimber.api.node.wrapper.Wrapper;

public class WrapperTest {

    @Test
    public void testHashCode() {

        Object object = new Object();
        Wrapper wrapper = new Wrapper(object);

        assertEquals("Hash code of wrapper", object.hashCode(), wrapper.hashCode());
    }

    @Test
    public void testEqualsObject() {

        Object object1 = new Object();
        Object object2 = new Object();
        Wrapper wrapper1 = new Wrapper(object1);
        Wrapper wrapper2 = new Wrapper(object2);

        assertTrue("Test setup error: Object 1 isn't equal to itself for some reason", object1.equals(object1));
        assertTrue("Test setup error: Object 2 isn't equal to itself for some reason", object2.equals(object2));
        assertFalse("Test setup error: Two different objects are equal for some reason", object1.equals(object2));

        assertFalse("Test setup error: Wrapper 1 is equal to an object it's not storing for some reason", wrapper1.equals(object2));
        assertFalse("Test setup error: Wrapper 2 is equal to an object it's not storing for some reason", wrapper2.equals(object1));

        assertTrue("Wrapper 1 isn't equal to itself", wrapper1.equals(wrapper1));
        assertTrue("Wrapper 2 isn't equal to itself", wrapper2.equals(wrapper2));
        assertFalse("Wrappers 1 and 2 are equal although the shouldn't", wrapper1.equals(wrapper2));

        assertTrue("Wrapper 1 isn't equal to the object it's storing", wrapper1.equals(object1));
        assertTrue("Wrapper 2 isn't equal to the object it's storing", wrapper2.equals(object2));

        assertTrue("Wrappers aren't equal although they wrap around the same object ", wrapper1.equals(new Wrapper(object1)));
    }

    @Test
    public void testToString() {

        Object object = new Object();
        Wrapper wrapper = new Wrapper(object);

        assertEquals("String representation of wrapper", object.toString(), wrapper.toString());
    }

}
