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

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

/**
 * @cdk.module test-standard
 */
public class DescriptorValueTest extends CDKTestCase {

    public DescriptorValueTest() {
        super();
    }

    private final static String DESC_REF         = "bla";
    private final static String DESC_IMPL_TITLE  = "bla2";
    private final static String DESC_IMPL_VENDOR = "bla3";
    private final static String DESC_IMPL_ID     = "bla4";

    @Test
    public void testDescriptorValue_DescriptorSpecification_arrayString_arrayObject_IDescriptorResult_arrayString() {
        DescriptorSpecification spec = new DescriptorSpecification(DESC_REF, DESC_IMPL_TITLE, DESC_IMPL_ID,
                DESC_IMPL_VENDOR);
        DescriptorValue value = new DescriptorValue(spec, new String[0], new Object[0], new DoubleResult(0.7),
                new String[]{"bla"});
        Assert.assertNotNull(value);
    }

    @Test
    public void testGetValue() {
        DescriptorSpecification spec = new DescriptorSpecification(DESC_REF, DESC_IMPL_TITLE, DESC_IMPL_ID,
                DESC_IMPL_VENDOR);
        DoubleResult doubleVal = new DoubleResult(0.7);
        DescriptorValue value = new DescriptorValue(spec, new String[0], new Object[0], doubleVal, new String[]{"bla"});
        Assert.assertEquals(doubleVal, value.getValue());
    }

    @Test
    public void testGetSpecification() {
        DescriptorSpecification spec = new DescriptorSpecification(DESC_REF, DESC_IMPL_TITLE, DESC_IMPL_ID,
                DESC_IMPL_VENDOR);
        DoubleResult doubleVal = new DoubleResult(0.7);
        DescriptorValue value = new DescriptorValue(spec, new String[0], new Object[0], doubleVal, new String[]{"bla"});
        Assert.assertEquals(spec, value.getSpecification());
    }

    @Test
    public void testGetParameters() {
        DescriptorSpecification spec = new DescriptorSpecification(DESC_REF, DESC_IMPL_TITLE, DESC_IMPL_ID,
                DESC_IMPL_VENDOR);
        DoubleResult doubleVal = new DoubleResult(0.7);
        DescriptorValue value = new DescriptorValue(spec, new String[0], new Object[0], doubleVal, new String[]{"bla"});
        Assert.assertEquals(0, value.getParameters().length);
    }

    @Test
    public void testGetParameterNames() {
        DescriptorSpecification spec = new DescriptorSpecification(DESC_REF, DESC_IMPL_TITLE, DESC_IMPL_ID,
                DESC_IMPL_VENDOR);
        DoubleResult doubleVal = new DoubleResult(0.7);
        DescriptorValue value = new DescriptorValue(spec, new String[0], new Object[0], doubleVal, new String[]{"bla"});
        Assert.assertEquals(0, value.getParameterNames().length);
    }

    @Test
    public void testGetNames() {
        DescriptorSpecification spec = new DescriptorSpecification(DESC_REF, DESC_IMPL_TITLE, DESC_IMPL_ID,
                DESC_IMPL_VENDOR);
        DoubleResult doubleVal = new DoubleResult(0.7);
        DoubleArrayResult doubleVals = new DoubleArrayResult();
        doubleVals.add(Double.valueOf(0.1));
        doubleVals.add(Double.valueOf(0.2));
        DescriptorValue value;
        value = new DescriptorValue(spec, new String[0], new Object[0], doubleVal, new String[]{"bla"});
        Assert.assertEquals(1, value.getNames().length);
        value = new DescriptorValue(spec, new String[0], new Object[0], doubleVal, new String[]{ });
        Assert.assertEquals(1, value.getNames().length);
        value = new DescriptorValue(spec, new String[0], new Object[0], doubleVal, null);
        Assert.assertEquals(1, value.getNames().length);
        value = new DescriptorValue(spec, new String[0], new Object[0], doubleVals, null);
        Assert.assertEquals(2, value.getNames().length);
    }

    @Test
    public void testGetException() {
        DescriptorSpecification spec = new DescriptorSpecification(DESC_REF, DESC_IMPL_TITLE, DESC_IMPL_ID,
                DESC_IMPL_VENDOR);
        DoubleResult doubleVal = new DoubleResult(0.7);
        DescriptorValue value = new DescriptorValue(spec, new String[0], new Object[0], doubleVal, new String[]{"bla"},
                new CDKException("A test exception"));
        org.hamcrest.MatcherAssert.assertThat(value.getException(), is(instanceOf(CDKException.class)));
    }
}
