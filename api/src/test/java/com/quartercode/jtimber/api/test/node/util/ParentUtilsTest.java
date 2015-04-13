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

package com.quartercode.jtimber.api.test.node.util;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import com.quartercode.jtimber.api.node.DefaultNode;
import com.quartercode.jtimber.api.node.DefaultParentAware;
import com.quartercode.jtimber.api.node.Node;
import com.quartercode.jtimber.api.node.ParentAware;
import com.quartercode.jtimber.api.node.util.ParentUtils;

public class ParentUtilsTest {

    @Test
    public void testGetFirstParentOfType() {

        TestNode target1 = new TestNode();
        TestNode target2 = new TestNode();

        // Build 1
        ParentAware<?> start = new DefaultParentAware<>();

        start.addParent(new DefaultNode<>());

        // Test 1

        assertNull("First parent of type for non-existent type", ParentUtils.getFirstParentOfType(TestNode.class, start));

        // Build 2

        Node<?> branch1 = new DefaultNode<>();
        start.addParent(branch1);
        branch1.addParent(new DefaultNode<>());
        branch1.addParent(target1);

        // Test 2

        assertTrue("First parent of type single-existent type is wrong", ParentUtils.getFirstParentOfType(TestNode.class, start) == target1);

        // Build 3

        Node<?> branch2 = new DefaultNode<>();
        start.addParent(branch2);
        branch2.addParent(target2);

        // Test 3

        assertTrue("First parent of type for double-existent type is wrong", ParentUtils.getFirstParentOfType(TestNode.class, start) == target1);
    }

    private static class TestNode extends DefaultNode<Node<?>> {

    }

}
