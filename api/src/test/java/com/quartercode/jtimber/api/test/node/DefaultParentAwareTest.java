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

package com.quartercode.jtimber.api.test.node;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.quartercode.jtimber.api.node.DefaultNode;
import com.quartercode.jtimber.api.node.DefaultParentAware;
import com.quartercode.jtimber.api.node.IllegalParentTypeException;
import com.quartercode.jtimber.api.node.MultipleParentsException;
import com.quartercode.jtimber.api.node.Node;

public class DefaultParentAwareTest {

    @Test
    public void testGetSingleParent() {

        PA1 pa = new PA1();
        Node1 node = new Node1();

        assertEquals("Single parent when PA has no parents", null, pa.getSingleParent());

        pa.addParent(node);
        assertEquals("Single parent when PA has one parent", node, pa.getSingleParent());

        pa.addParent(node);
        pa.addParent(node);
        pa.addParent(node);
        assertEquals("Single parent when PA has four identical parents", node, pa.getSingleParent());
    }

    @Test (expected = MultipleParentsException.class)
    public void testGetSingleParentException() {

        PA1 pa = new PA1();
        pa.addParent(new Node1());
        pa.addParent(new Node2());

        // Expect exception
        pa.getSingleParent();
    }

    @Test
    public void testAddNullParent() {

        PA1 pa = new PA1();
        pa.addParent(null); // Expect no exception

        assertEquals("A null parent has been added", pa.getParentCount(), 0);
    }

    @Test (expected = IllegalParentTypeException.class)
    public void testAddDisallowedParent() {

        PA2 pa = new PA2();

        Node1 node1 = new Node1();
        pa.addParent(node1);

        assertArrayEquals("Parents of the parent-aware object after an allowed node has been added", new Node[] { node1 }, pa.getParents().toArray());

        pa.addParent(new Node2()); // Expect exception
    }

    @Test
    public void testRemoveNullParent() {

        PA1 pa = new PA1();
        pa.removeParent(null); // Expect no exception
    }

    private static class PA1 extends DefaultParentAware<Node<?>> {

    }

    private static class PA2 extends DefaultParentAware<Node1> {

    }

    private static class Node1 extends DefaultNode<Node<?>> {

    }

    private static class Node2 extends DefaultNode<Node<?>> {

    }

}
