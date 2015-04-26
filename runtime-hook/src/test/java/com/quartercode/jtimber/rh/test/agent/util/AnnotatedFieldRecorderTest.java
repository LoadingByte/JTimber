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

package com.quartercode.jtimber.rh.test.agent.util;

import static org.junit.Assert.assertArrayEquals;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.objectweb.asm.Type;
import com.quartercode.jtimber.rh.agent.asm.util.AnnotatedFieldRecorder;

public class AnnotatedFieldRecorderTest {

    private static final Type ANNOTATION_1_TYPE = Type.getObjectType("test/TestAnnotation1");
    private static final Type ANNOTATION_2_TYPE = Type.getObjectType("test/TestAnnotation2");
    private static final Type ANNOTATION_3_TYPE = Type.getObjectType("test/TestAnnotation3");

    @Test
    public void test() {

        List<String> list = new ArrayList<>();
        list.add("field1");
        list.add("field3");

        AnnotatedFieldRecorder fv = new AnnotatedFieldRecorder(null, "field2", ANNOTATION_2_TYPE, list);

        assertArrayEquals("Field list before recorder visit", new String[] { "field1", "field3" }, list.toArray());

        fv.visitAnnotation(ANNOTATION_1_TYPE.getDescriptor(), true);
        fv.visitAnnotation(ANNOTATION_2_TYPE.getDescriptor(), true);
        fv.visitAnnotation(ANNOTATION_3_TYPE.getDescriptor(), true);

        assertArrayEquals("Field list after recorder visit", new String[] { "field1", "field3", "field2" }, list.toArray());
    }

}
