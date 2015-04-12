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

package com.quartercode.jtimber.api.test.internal;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.quartercode.jtimber.api.internal.RHConstFunctions;
import com.quartercode.jtimber.api.node.wrapper.collection.ArrayWrapper;

@RunWith (Parameterized.class)
public class RHConstFunctionsTest {

    @Parameters
    public static Collection<Object[]> data() {

        List<Object[]> data = new ArrayList<>();

        // Simple tests
        data.add(new Object[] { null, new Object[0] });
        data.add(new Object[] { "string", new Object[] { "string" } });

        String[] stringArray1 = { "string1", "string2", "string3" };
        String[] stringArray2 = { "string4", "string5", "string6" };

        // Single wrapper test
        data.add(new Object[] { new ArrayWrapper<>(stringArray1), stringArray1 });
        // Recursive wrappers test
        data.add(new Object[] { new ArrayWrapper<>(concat(stringArray1, new ArrayWrapper<>(stringArray2))), concat(stringArray1, (Object[]) stringArray2) });

        return data;
    }

    private static Object[] concat(Object[] a, Object... b) {

        int aLength = a.length;
        int bLength = b.length;

        Object[] c = new Object[aLength + bLength];
        System.arraycopy(a, 0, c, 0, aLength);
        System.arraycopy(b, 0, c, aLength, bLength);

        return c;
    }

    private final Object   child;
    private final Object[] expectedActualChildren;

    public RHConstFunctionsTest(Object child, Object[] expectedActualChildren) {

        this.child = child;
        this.expectedActualChildren = expectedActualChildren;
    }

    @Test
    public void testAddActualChildrenToList() {

        List<Object> list = new ArrayList<>();
        RHConstFunctions.addActualChildrenToList(list, child);

        assertArrayEquals("Actual children", expectedActualChildren, list.toArray());
    }

    @Test
    public void testCountActualChildren() {

        int count = RHConstFunctions.countActualChildren(child);

        assertEquals("Actual children count", expectedActualChildren.length, count);
    }

}
