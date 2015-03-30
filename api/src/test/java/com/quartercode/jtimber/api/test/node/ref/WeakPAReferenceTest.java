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

package com.quartercode.jtimber.api.test.node.ref;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import com.quartercode.jtimber.api.node.ParentAware;
import com.quartercode.jtimber.api.node.ref.WeakPAReference;

public class WeakPAReferenceTest {

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    @Test
    public void test() {

        final ParentAware<?> pa = context.mock(ParentAware.class);

        // @formatter:off
        context.checking(new Expectations() {{

            Sequence parentCountSequence = context.sequence("parentCountSequence");

            exactly(3).of(pa).getParentCount(); inSequence(parentCountSequence);
                will(returnValue(1));
            oneOf(pa).getParentCount(); inSequence(parentCountSequence);
                will(returnValue(0));
            allowing(pa).getParentCount(); inSequence(parentCountSequence);
                will(returnValue(1));

        }});
        // @formatter:on

        WeakPAReference<?> reference = new WeakPAReference<>(pa);

        for (int counter = 0; counter < 3; counter++) {
            assertEquals("Weakly referenced pa", pa, reference.get());
        }

        assertNull("Weakly referenced node hasn't been nulled although its parent count is 0", reference.get());

        for (int counter = 0; counter < 3; counter++) {
            assertNull("Weakly referenced node has been revived although it has been removed", reference.get());
        }
    }

}
