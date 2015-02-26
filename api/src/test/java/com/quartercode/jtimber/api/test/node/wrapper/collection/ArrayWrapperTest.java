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

package com.quartercode.jtimber.api.test.node.wrapper.collection;

import static org.junit.Assert.assertArrayEquals;
import org.junit.Before;
import org.junit.Test;
import com.quartercode.jtimber.api.node.DefaultNode;
import com.quartercode.jtimber.api.node.Node;
import com.quartercode.jtimber.api.node.wrapper.collection.ArrayWrapper;

public class ArrayWrapperTest {

    private final Node               parent1 = new DefaultNode();
    private final Node               parent2 = new DefaultNode();

    private final Node[]             array   = new Node[2];
    private final ArrayWrapper<Node> wrapper = new ArrayWrapper<>(array);

    private final Node               elem1   = new DefaultNode();
    private final Node               elem2   = new DefaultNode();
    private final Node               elem3   = new DefaultNode();

    @Before
    public void setUp() {

        wrapper.addParent(parent1);
        wrapper.addParent(parent2);
    }

    @Test
    public void testSetWithIndex() {

        wrapper.set(0, elem1);
        wrapper.set(1, elem2);

        assertArrayEquals("Array elements after first round of modifications", new Node[] { elem1, elem2 }, array);

        assertArrayEquals("Parents of element 1 after modifications", new Node[] { parent1, parent2 }, elem1.getParents().toArray());
        assertArrayEquals("Parents of element 2 after modifications", new Node[] { parent1, parent2 }, elem2.getParents().toArray());
        assertArrayEquals("Parents of element 3 after modifications", new Node[0], elem3.getParents().toArray());

        wrapper.set(0, null);
        wrapper.set(1, elem3);

        assertArrayEquals("Array elements after second round of modifications", new Node[] { null, elem3 }, array);

        assertArrayEquals("Parents of element 1 after modifications", new Node[0], elem1.getParents().toArray());
        assertArrayEquals("Parents of element 2 after modifications", new Node[0], elem2.getParents().toArray());
        assertArrayEquals("Parents of element 3 after modifications", new Node[] { parent1, parent2 }, elem3.getParents().toArray());
    }

    // Basic delegates are not tested!

}
