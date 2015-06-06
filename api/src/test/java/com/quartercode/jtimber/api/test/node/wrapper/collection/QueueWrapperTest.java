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

package com.quartercode.jtimber.api.test.node.wrapper.collection;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import java.util.LinkedList;
import java.util.Queue;
import org.junit.Before;
import org.junit.Test;
import com.quartercode.jtimber.api.node.DefaultNode;
import com.quartercode.jtimber.api.node.Node;
import com.quartercode.jtimber.api.node.wrapper.collection.QueueWrapper;

public class QueueWrapperTest {

    private final Node<?>               parent1 = new DefaultNode<>();
    private final Node<?>               parent2 = new DefaultNode<>();

    private final Queue<Node<?>>        queue   = new LinkedList<>();
    private final QueueWrapper<Node<?>> wrapper = new QueueWrapper<>(queue);

    private final Node<?>               elem1   = new DefaultNode<>();
    private final Node<?>               elem2   = new DefaultNode<>();
    private final Node<?>               elem3   = new DefaultNode<>();

    @Before
    public void setUp() {

        wrapper.addParent(parent1);
        wrapper.addParent(parent2);
    }

    @Test
    public void testOffer() {

        wrapper.offer(elem1);
        wrapper.offer(elem2);

        assertArrayEquals("Queue elements after modifications", new Node[] { elem1, elem2 }, queue.toArray());
        assertArrayEquals("Actual children of wrapper after modifications", new Object[] { elem1, elem2 }, wrapper.getActualChildren().toArray());

        assertArrayEquals("Parents of element 1 after modifications", new Node[] { parent1, parent2 }, elem1.getParents().toArray());
        assertArrayEquals("Parents of element 2 after modifications", new Node[] { parent1, parent2 }, elem2.getParents().toArray());
    }

    @Test
    public void testRemoveAndPoll() {

        wrapper.add(elem1);
        wrapper.add(elem2);
        wrapper.add(elem3);

        assertEquals("First polled element (using 'remove()')", elem1, wrapper.remove());
        assertEquals("Second polled element (using 'poll()')", elem2, wrapper.poll());

        assertArrayEquals("Queue elements after modifications", new Node[] { elem3 }, queue.toArray());
        assertArrayEquals("Actual children of wrapper after modifications", new Object[] { elem3 }, wrapper.getActualChildren().toArray());

        assertArrayEquals("Parents of element 1 after modifications", new Node[0], elem1.getParents().toArray());
        assertArrayEquals("Parents of element 2 after modifications", new Node[0], elem2.getParents().toArray());
        assertArrayEquals("Parents of element 3 after modifications", new Node[] { parent1, parent2 }, elem3.getParents().toArray());
    }

    // Basic delegates and methods covered by CollectionWrapper are not tested!

}
