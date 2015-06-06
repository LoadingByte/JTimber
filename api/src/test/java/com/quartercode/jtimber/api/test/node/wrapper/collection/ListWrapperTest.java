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

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import org.junit.Before;
import org.junit.Test;
import com.quartercode.jtimber.api.node.DefaultNode;
import com.quartercode.jtimber.api.node.Node;
import com.quartercode.jtimber.api.node.wrapper.collection.ListWrapper;

public class ListWrapperTest {

    private final Node<?>              parent1 = new DefaultNode<>();
    private final Node<?>              parent2 = new DefaultNode<>();

    private final List<Node<?>>        list    = new ArrayList<>();
    private final ListWrapper<Node<?>> wrapper = new ListWrapper<>(list);

    private final Node<?>              elem1   = new DefaultNode<>();
    private final Node<?>              elem2   = new DefaultNode<>();
    private final Node<?>              elem3   = new DefaultNode<>();

    @Before
    public void setUp() {

        wrapper.addParent(parent1);
        wrapper.addParent(parent2);
    }

    @Test
    public void testAddAllWithIndex() {

        wrapper.add(elem1);
        wrapper.addAll(0, Arrays.asList(elem2, elem3));

        assertArrayEquals("List elements after modifications", new Node[] { elem2, elem3, elem1 }, list.toArray());
        assertArrayEquals("Actual children of wrapper after modifications", new Object[] { elem2, elem3, elem1 }, wrapper.getActualChildren().toArray());

        assertArrayEquals("Parents of element 1 after modifications", new Node[] { parent1, parent2 }, elem1.getParents().toArray());
        assertArrayEquals("Parents of element 2 after modifications", new Node[] { parent1, parent2 }, elem2.getParents().toArray());
        assertArrayEquals("Parents of element 3 after modifications", new Node[] { parent1, parent2 }, elem3.getParents().toArray());
    }

    @Test
    public void testSetWithIndex() {

        wrapper.add(elem1);
        wrapper.add(elem2);
        wrapper.set(1, elem3);

        assertArrayEquals("List elements after modifications", new Node[] { elem1, elem3 }, list.toArray());
        assertArrayEquals("Actual children of wrapper after modifications", new Object[] { elem1, elem3 }, wrapper.getActualChildren().toArray());

        assertArrayEquals("Parents of element 1 after modifications", new Node[] { parent1, parent2 }, elem1.getParents().toArray());
        assertArrayEquals("Parents of element 2 after modifications", new Node[0], elem2.getParents().toArray());
        assertArrayEquals("Parents of element 3 after modifications", new Node[] { parent1, parent2 }, elem3.getParents().toArray());
    }

    @Test
    public void testAddWithIndex() {

        wrapper.add(elem1);
        wrapper.add(0, elem2);

        assertArrayEquals("List elements after modifications", new Node[] { elem2, elem1 }, list.toArray());
        assertArrayEquals("Actual children of wrapper after modifications", new Object[] { elem2, elem1 }, wrapper.getActualChildren().toArray());

        assertArrayEquals("Parents of element 1 after modifications", new Node[] { parent1, parent2 }, elem1.getParents().toArray());
        assertArrayEquals("Parents of element 2 after modifications", new Node[] { parent1, parent2 }, elem2.getParents().toArray());
    }

    @Test
    public void testRemoveWithIndex() {

        wrapper.add(elem1);
        wrapper.add(elem2);
        wrapper.remove(1);

        assertArrayEquals("List elements after modifications", new Node[] { elem1 }, list.toArray());
        assertArrayEquals("Actual children of wrapper after modifications", new Object[] { elem1 }, wrapper.getActualChildren().toArray());

        assertArrayEquals("Parents of element 1 after modifications", new Node[] { parent1, parent2 }, elem1.getParents().toArray());
        assertArrayEquals("Parents of element 2 after modifications", new Node[0], elem2.getParents().toArray());
    }

    @Test
    public void testListIterator() {

        wrapper.add(elem1);
        wrapper.add(elem2);

        // Only next iteration
        ListIterator<Node<?>> it1 = wrapper.listIterator();
        assertEquals(0, it1.nextIndex());
        assertTrue(it1.hasNext());
        assertEquals(elem1, it1.next());
        assertEquals(1, it1.nextIndex());
        assertTrue(it1.hasNext());
        assertEquals(elem2, it1.next());
        assertEquals(2, it1.nextIndex());
        assertFalse(it1.hasNext());

        // Next and previous iteration
        ListIterator<Node<?>> it2 = wrapper.listIterator();
        it2.next();
        it2.next();
        assertEquals(2, it2.nextIndex());
        assertEquals(1, it2.previousIndex());
        assertTrue(it2.hasPrevious());
        assertEquals(elem2, it2.previous());
        assertEquals(1, it2.nextIndex());
        assertEquals(0, it2.previousIndex());
        assertTrue(it2.hasNext());
        assertEquals(elem2, it2.next());
        assertEquals(2, it2.nextIndex());
        assertEquals(1, it2.previousIndex());
        assertFalse(it2.hasNext());

        assertArrayEquals("List elements after the first two iterations", new Node[] { elem1, elem2 }, list.toArray());
        assertArrayEquals("Actual children of wrapper after the first two iterations", new Object[] { elem1, elem2 }, wrapper.getActualChildren().toArray());

        assertArrayEquals("Parents of element 1 after the first two iterations", new Node[] { parent1, parent2 }, elem1.getParents().toArray());
        assertArrayEquals("Parents of element 2 after the first two iterations", new Node[] { parent1, parent2 }, elem2.getParents().toArray());

        // Iteration with modifications
        ListIterator<Node<?>> it3 = wrapper.listIterator();
        it3.next();
        assertEquals(1, it3.nextIndex());
        it3.remove(); // Remove elem1
        assertEquals(0, it3.nextIndex()); // Shifted because of the removal
        it3.next();
        it3.set(elem1); // Replace elem2 with elem1
        it3.add(elem3); // Add elem3 at the end

        assertArrayEquals("List elements after third iteration", new Node[] { elem1, elem3 }, list.toArray());
        assertArrayEquals("Actual children of wrapper after third iteration", new Object[] { elem1, elem3 }, wrapper.getActualChildren().toArray());

        assertArrayEquals("Parents of element 1 after third iteration", new Node[] { parent1, parent2 }, elem1.getParents().toArray());
        assertArrayEquals("Parents of element 2 after third iteration", new Node[0], elem2.getParents().toArray());
        assertArrayEquals("Parents of element 3 after third iteration", new Node[] { parent1, parent2 }, elem3.getParents().toArray());
    }

    // listIterator(int) with starting index is not tested because it is really similar to listIterator()

    @Test (expected = UnsupportedOperationException.class)
    public void testSubList() {

        wrapper.subList(0, 0);
    }

    // Basic delegates and methods covered by CollectionWrapper are not tested!

}
