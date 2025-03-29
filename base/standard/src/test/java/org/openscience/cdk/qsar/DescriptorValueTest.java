/* Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.qsar;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;

class DescriptorValueTest {

    DescriptorValueTest() {
        super();
    }

    private final static String DESC_REF         = "bla";
    private final static String DESC_IMPL_TITLE  = "bla2";
    private final static String DESC_IMPL_VENDOR = "bla3";
    private final static String DESC_IMPL_ID     = "bla4";

    @Test
    void testDescriptorValue_DescriptorSpecification_arrayString_arrayObject_IDescriptorResult_arrayString() {
        DescriptorSpecification spec = new DescriptorSpecification(DESC_REF, DESC_IMPL_TITLE, DESC_IMPL_ID,
                DESC_IMPL_VENDOR);
        DescriptorValue value = new DescriptorValue(spec, new String[0], new Object[0], new DoubleResult(0.7),
                new String[]{"bla"});
        Assertions.assertNotNull(value);
    }

    @Test
    void testGetValue() {
        DescriptorSpecification spec = new DescriptorSpecification(DESC_REF, DESC_IMPL_TITLE, DESC_IMPL_ID,
                DESC_IMPL_VENDOR);
        DoubleResult doubleVal = new DoubleResult(0.7);
        DescriptorValue value = new DescriptorValue(spec, new String[0], new Object[0], doubleVal, new String[]{"bla"});
        Assertions.assertEquals(doubleVal, value.getValue());
    }

    @Test
    void testGetSpecification() {
        DescriptorSpecification spec = new DescriptorSpecification(DESC_REF, DESC_IMPL_TITLE, DESC_IMPL_ID,
                DESC_IMPL_VENDOR);
        DoubleResult doubleVal = new DoubleResult(0.7);
        DescriptorValue value = new DescriptorValue(spec, new String[0], new Object[0], doubleVal, new String[]{"bla"});
        Assertions.assertEquals(spec, value.getSpecification());
    }

    @Test
    void testGetParameters() {
        DescriptorSpecification spec = new DescriptorSpecification(DESC_REF, DESC_IMPL_TITLE, DESC_IMPL_ID,
                DESC_IMPL_VENDOR);
        DoubleResult doubleVal = new DoubleResult(0.7);
        DescriptorValue value = new DescriptorValue(spec, new String[0], new Object[0], doubleVal, new String[]{"bla"});
        Assertions.assertEquals(0, value.getParameters().length);
    }

    @Test
    void testGetParameterNames() {
        DescriptorSpecification spec = new DescriptorSpecification(DESC_REF, DESC_IMPL_TITLE, DESC_IMPL_ID,
                DESC_IMPL_VENDOR);
        DoubleResult doubleVal = new DoubleResult(0.7);
        DescriptorValue value = new DescriptorValue(spec, new String[0], new Object[0], doubleVal, new String[]{"bla"});
        Assertions.assertEquals(0, value.getParameterNames().length);
    }

    @Test
    void testGetNames() {
        DescriptorSpecification spec = new DescriptorSpecification(DESC_REF, DESC_IMPL_TITLE, DESC_IMPL_ID,
                DESC_IMPL_VENDOR);
        DoubleResult doubleVal = new DoubleResult(0.7);
        DoubleArrayResult doubleVals = new DoubleArrayResult();
        doubleVals.add(0.1);
        doubleVals.add(0.2);
        DescriptorValue value;
        value = new DescriptorValue(spec, new String[0], new Object[0], doubleVal, new String[]{"bla"});
        Assertions.assertEquals(1, value.getNames().length);
        value = new DescriptorValue(spec, new String[0], new Object[0], doubleVal, new String[]{ });
        Assertions.assertEquals(1, value.getNames().length);
        value = new DescriptorValue(spec, new String[0], new Object[0], doubleVal, null);
        Assertions.assertEquals(1, value.getNames().length);
        value = new DescriptorValue(spec, new String[0], new Object[0], doubleVals, null);
        Assertions.assertEquals(2, value.getNames().length);
    }

    @Test
    void testGetException() {
        DescriptorSpecification spec = new DescriptorSpecification(DESC_REF, DESC_IMPL_TITLE, DESC_IMPL_ID,
                DESC_IMPL_VENDOR);
        DoubleResult doubleVal = new DoubleResult(0.7);
        DescriptorValue value = new DescriptorValue(spec, new String[0], new Object[0], doubleVal, new String[]{"bla"},
                new CDKException("A test exception"));
        org.hamcrest.MatcherAssert.assertThat(value.getException(), is(instanceOf(CDKException.class)));
    }
}
