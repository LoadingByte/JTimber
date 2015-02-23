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

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import org.junit.Before;
import org.junit.Test;
import com.quartercode.jtimber.api.node.DefaultNode;
import com.quartercode.jtimber.api.node.Node;
import com.quartercode.jtimber.api.node.wrapper.collection.CollectionWrapper;

public class CollectionWrapperTest {

    private final Node                    parent1    = new DefaultNode();
    private final Node                    parent2    = new DefaultNode();

    private final Collection<Node>        collection = new ArrayList<>();
    private final CollectionWrapper<Node> wrapper    = new CollectionWrapper<>(collection);

    private final Node                    elem1      = new DefaultNode();
    private final Node                    elem2      = new DefaultNode();
    private final Node                    elem3      = new DefaultNode();

    @Before
    public void setUp() {

        wrapper.addParent(parent1);
        wrapper.addParent(parent2);
    }

    @Test
    public void testAddParent() {

        wrapper.add(elem1);
        assertArrayEquals("Parents of element 1 before addition of a third parent", new Node[] { parent1, parent2 }, elem1.getParents().toArray());

        Node parent3 = new DefaultNode();
        wrapper.addParent(parent3);
        assertArrayEquals("Parents of element 1 after addition of a third parent", new Node[] { parent1, parent2, parent3 }, elem1.getParents().toArray());
    }

    @Test
    public void testRemoveParent() {

        wrapper.add(elem1);
        assertArrayEquals("Parents of element 1 before removal of a parent", new Node[] { parent1, parent2 }, elem1.getParents().toArray());

        wrapper.removeParent(parent2);
        assertArrayEquals("Parents of element 1 after addition of a parent", new Node[] { parent1 }, elem1.getParents().toArray());
    }

    @Test
    public void testIterator() {

        wrapper.add(elem1);
        wrapper.add(elem2);

        Iterator<Node> it1 = wrapper.iterator();
        assertTrue(it1.hasNext());
        assertEquals(elem1, it1.next());
        assertTrue(it1.hasNext());
        assertEquals(elem2, it1.next());
        assertFalse(it1.hasNext());

        assertArrayEquals("Collection elements after first iteration", new Node[] { elem1, elem2 }, collection.toArray());

        assertArrayEquals("Parents of element 1 after first iteration", new Node[] { parent1, parent2 }, elem1.getParents().toArray());
        assertArrayEquals("Parents of element 2 after first iteration", new Node[] { parent1, parent2 }, elem2.getParents().toArray());

        Iterator<Node> it2 = wrapper.iterator();
        it2.next();
        it2.remove();

        assertArrayEquals("Collection elements after second iteration", new Node[] { elem2 }, collection.toArray());

        assertArrayEquals("Parents of element 1 after second iteration", new Node[0], elem1.getParents().toArray());
        assertArrayEquals("Parents of element 2 after second iteration", new Node[] { parent1, parent2 }, elem2.getParents().toArray());
    }

    @Test
    public void testAdd() {

        wrapper.add(elem1);
        wrapper.add(elem2);

        assertArrayEquals("Collection elements after modifications", new Node[] { elem1, elem2 }, collection.toArray());

        assertArrayEquals("Parents of element 1 after modifications", new Node[] { parent1, parent2 }, elem1.getParents().toArray());
        assertArrayEquals("Parents of element 2 after modifications", new Node[] { parent1, parent2 }, elem2.getParents().toArray());
    }

    @Test
    public void testRemove() {

        wrapper.add(elem1);
        wrapper.add(elem2);
        wrapper.remove(elem2);

        assertArrayEquals("Collection elements after modifications", new Node[] { elem1 }, collection.toArray());

        assertArrayEquals("Parents of element 1 after modifications", new Node[] { parent1, parent2 }, elem1.getParents().toArray());
        assertArrayEquals("Parents of element 2 after modifications", new Node[0], elem2.getParents().toArray());
    }

    @Test
    public void testAddAll() {

        wrapper.addAll(Arrays.asList(elem1, elem2));

        assertArrayEquals("Collection elements after modifications", new Node[] { elem1, elem2 }, collection.toArray());

        assertArrayEquals("Parents of element 1 after modifications", new Node[] { parent1, parent2 }, elem1.getParents().toArray());
        assertArrayEquals("Parents of element 2 after modifications", new Node[] { parent1, parent2 }, elem2.getParents().toArray());
    }

    @Test
    public void testRemoveAll() {

        wrapper.add(elem1);
        wrapper.add(elem2);
        wrapper.add(elem3);
        wrapper.removeAll(Arrays.asList(elem2, elem3));

        assertArrayEquals("Collection elements after modifications", new Node[] { elem1 }, collection.toArray());

        assertArrayEquals("Parents of element 1 after modifications", new Node[] { parent1, parent2 }, elem1.getParents().toArray());
        assertArrayEquals("Parents of element 2 after modifications", new Node[0], elem2.getParents().toArray());
        assertArrayEquals("Parents of element 3 after modifications", new Node[0], elem3.getParents().toArray());
    }

    @Test
    public void testRetainAll() {

        wrapper.add(elem1);
        wrapper.add(elem2);
        wrapper.add(elem3);
        wrapper.retainAll(Arrays.asList(elem2, elem3));

        assertArrayEquals("Collection elements after modifications", new Node[] { elem2, elem3 }, collection.toArray());

        assertArrayEquals("Parents of element 1 after modifications", new Node[0], elem1.getParents().toArray());
        assertArrayEquals("Parents of element 2 after modifications", new Node[] { parent1, parent2 }, elem2.getParents().toArray());
        assertArrayEquals("Parents of element 3 after modifications", new Node[] { parent1, parent2 }, elem3.getParents().toArray());
    }

    @Test
    public void testClear() {

        wrapper.add(elem1);
        wrapper.add(elem2);
        wrapper.clear();

        assertArrayEquals("Collection elements after modifications", new Node[0], collection.toArray());

        assertArrayEquals("Parents of element 1 after modifications", new Node[0], elem1.getParents().toArray());
        assertArrayEquals("Parents of element 2 after modifications", new Node[0], elem2.getParents().toArray());
    }

    // Basic delegates are not tested!

}
