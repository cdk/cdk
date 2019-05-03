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
package org.openscience.cdk.qsar.result;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.CDKTestCase;

/**
 * @cdk.module test-standard
 */
public class DoubleResultTest extends CDKTestCase {

    public DoubleResultTest() {
        super();
    }

    @Test
    public void testDoubleResult_double() {
        DoubleResult result = new DoubleResult(5.0);
        Assert.assertNotNull(result);
    }

    @Test
    public void testToString() {
        DoubleResult result = new DoubleResult(5.0);
        Assert.assertEquals("5.0", result.toString());
    }

    @Test
    public void testDoubleValue() {
        DoubleResult result = new DoubleResult(5);
        Assert.assertEquals(5.0, result.doubleValue(), 0.000001);
    }

    @Test
    public void testLength() {
        DoubleResult result = new DoubleResult(5);
        Assert.assertEquals(1, result.length());
    }
}
