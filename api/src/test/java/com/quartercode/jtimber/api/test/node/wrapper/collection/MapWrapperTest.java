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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import com.quartercode.jtimber.api.node.DefaultNode;
import com.quartercode.jtimber.api.node.Node;
import com.quartercode.jtimber.api.node.wrapper.collection.MapWrapper;

public class MapWrapperTest {

    private static Node<?>[] mapToArray(Map<Node<?>, Node<?>> map) {

        Node<?>[] array = new Node[map.size() * 2];

        int index = 0;
        for (Entry<Node<?>, Node<?>> entry : map.entrySet()) {
            array[index] = entry.getKey();
            array[index + 1] = entry.getValue();
            index += 2;
        }

        return array;
    }

    private final Node<?>                      parent1 = new DefaultNode<>();
    private final Node<?>                      parent2 = new DefaultNode<>();

    private final Map<Node<?>, Node<?>>        map     = new LinkedHashMap<>();
    private final MapWrapper<Node<?>, Node<?>> wrapper = new MapWrapper<>(map);

    private final Node<?>                      elem1   = new DefaultNode<>();
    private final Node<?>                      elem2   = new DefaultNode<>();
    private final Node<?>                      elem3   = new DefaultNode<>();

    @Before
    public void setUp() {

        wrapper.addParent(parent1);
        wrapper.addParent(parent2);
    }

    @Test
    public void testAddParent() {

        wrapper.put(elem1, elem2);
        assertArrayEquals("Parents of element 1 before addition of a third parent", new Node[] { parent1, parent2 }, elem1.getParents().toArray());
        assertArrayEquals("Parents of element 2 before addition of a third parent", new Node[] { parent1, parent2 }, elem2.getParents().toArray());

        Node<?> parent3 = new DefaultNode<>();
        wrapper.addParent(parent3);
        assertArrayEquals("Parents of element 1 after addition of a third parent", new Node[] { parent1, parent2, parent3 }, elem1.getParents().toArray());
        assertArrayEquals("Parents of element 2 after addition of a third parent", new Node[] { parent1, parent2, parent3 }, elem2.getParents().toArray());
    }

    @Test
    public void testRemoveParent() {

        wrapper.put(elem1, elem2);
        assertArrayEquals("Parents of element 1 before removal of a parent", new Node[] { parent1, parent2 }, elem1.getParents().toArray());
        assertArrayEquals("Parents of element 2 before removal of a parent", new Node[] { parent1, parent2 }, elem2.getParents().toArray());

        wrapper.removeParent(parent2);
        assertArrayEquals("Parents of element 1 after addition of a parent", new Node[] { parent1 }, elem1.getParents().toArray());
        assertArrayEquals("Parents of element 2 after addition of a parent", new Node[] { parent1 }, elem2.getParents().toArray());
    }

    @Test
    public void testPut() {

        wrapper.put(elem1, elem2);
        wrapper.put(elem3, elem2);

        assertArrayEquals("Map elements after modifications", new Node[] { elem1, elem2, elem3, elem2 }, mapToArray(map));
        // The internal algorithm for getActual() children first adds all keys and then all values to the output list
        assertArrayEquals("Actual children of wrapper after modifications", new Object[] { elem1, elem3, elem2, elem2 }, wrapper.getActualChildren().toArray());

        assertArrayEquals("Parents of element 1 after modifications", new Node[] { parent1, parent2 }, elem1.getParents().toArray());
        assertArrayEquals("Parents of element 2 after modifications", new Node[] { parent1, parent2, parent1, parent2 }, elem2.getParents().toArray());
        assertArrayEquals("Parents of element 3 after modifications", new Node[] { parent1, parent2 }, elem3.getParents().toArray());
    }

    @Test
    public void testRemove() {

        wrapper.put(elem1, elem2);
        wrapper.put(elem3, elem2);
        wrapper.remove(elem1);

        assertArrayEquals("Map elements after modifications", new Node[] { elem3, elem2 }, mapToArray(map));
        assertArrayEquals("Actual children of wrapper after modifications", new Object[] { elem3, elem2 }, wrapper.getActualChildren().toArray());

        assertArrayEquals("Parents of element 1 after modifications", new Node[0], elem1.getParents().toArray());
        assertArrayEquals("Parents of element 2 after modifications", new Node[] { parent1, parent2 }, elem2.getParents().toArray());
        assertArrayEquals("Parents of element 3 after modifications", new Node[] { parent1, parent2 }, elem3.getParents().toArray());
    }

    @Test
    public void testPutAll() {

        Map<Node<?>, Node<?>> sourceMap = new LinkedHashMap<>();
        sourceMap.put(elem1, elem2);
        sourceMap.put(elem3, elem2);
        wrapper.putAll(sourceMap);

        assertArrayEquals("Map elements after modifications", new Node[] { elem1, elem2, elem3, elem2 }, mapToArray(map));
        // The internal algorithm for getActual() children first adds all keys and then all values to the output list
        assertArrayEquals("Actual children of wrapper after modifications", new Object[] { elem1, elem3, elem2, elem2 }, wrapper.getActualChildren().toArray());

        assertArrayEquals("Parents of element 1 after modifications", new Node[] { parent1, parent2 }, elem1.getParents().toArray());
        assertArrayEquals("Parents of element 2 after modifications", new Node[] { parent1, parent2, parent1, parent2 }, elem2.getParents().toArray());
        assertArrayEquals("Parents of element 3 after modifications", new Node[] { parent1, parent2 }, elem3.getParents().toArray());
    }

    @Test
    public void testClear() {

        wrapper.put(elem1, elem2);
        wrapper.put(elem3, elem2);
        wrapper.clear();

        assertArrayEquals("Map elements after modifications", new Node[0], mapToArray(map));
        assertArrayEquals("Actual children of wrapper after modifications", new Object[0], wrapper.getActualChildren().toArray());

        assertArrayEquals("Parents of element 1 after modifications", new Node[0], elem1.getParents().toArray());
        assertArrayEquals("Parents of element 2 after modifications", new Node[0], elem2.getParents().toArray());
        assertArrayEquals("Parents of element 3 after modifications", new Node[0], elem3.getParents().toArray());
    }

    @Test (expected = UnsupportedOperationException.class)
    public void testKeySet() {

        wrapper.put(elem1, elem2);
        wrapper.keySet().remove(elem1);
    }

    @Test (expected = UnsupportedOperationException.class)
    public void testValues() {

        wrapper.put(elem1, elem2);
        wrapper.values().remove(elem2);
    }

    @Test
    public void testEntrySet() {

        wrapper.put(elem1, elem2);
        wrapper.put(elem3, elem2);

        Set<Entry<Node<?>, Node<?>>> entrySet = wrapper.entrySet();
        entrySet.remove(entrySet.iterator().next());

        assertArrayEquals("Regular map elements after modifications", new Node[] { elem3, elem2 }, mapToArray(map));
        assertArrayEquals("Actual children of wrapper after modifications", new Object[] { elem3, elem2 }, wrapper.getActualChildren().toArray());

        assertArrayEquals("Parents of element 1 after modifications", new Node[0], elem1.getParents().toArray());
        assertArrayEquals("Parents of element 2 after modifications", new Node[] { parent1, parent2 }, elem2.getParents().toArray());
        assertArrayEquals("Parents of element 3 after modifications", new Node[] { parent1, parent2 }, elem3.getParents().toArray());
    }

    // Basic delegates are not tested!

}
