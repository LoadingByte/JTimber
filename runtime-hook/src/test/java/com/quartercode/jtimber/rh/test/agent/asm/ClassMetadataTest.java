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

package com.quartercode.jtimber.rh.test.agent.asm;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.Test;
import com.quartercode.jtimber.rh.agent.asm.ClassMetadata;

public class ClassMetadataTest {

    @Test
    public void testGetNonWeakFields() {

        ClassMetadata metadata = new ClassMetadata();

        // Add some fields
        for (int counter = 1; counter <= 5; counter++) {
            metadata.fields.put("field" + counter, null);
        }

        // Add some weak fields
        metadata.weakFields.add("field2");
        metadata.weakFields.add("field5");

        // Check the non-weak fields
        assertEquals(new HashSet<>(Arrays.asList("field1", "field3", "field4")), metadata.getNonWeakFields());
    }

}
