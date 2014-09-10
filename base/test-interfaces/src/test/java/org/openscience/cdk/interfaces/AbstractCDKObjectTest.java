/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.interfaces;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;

/**
 * Tests the functionality of {@link ICDKObject} implementations.
 *
 * @cdk.module  test-interfaces
 * @cdk.created 2010-10-22
 */
public abstract class AbstractCDKObjectTest extends CDKTestCase {

    private static ITestObjectBuilder builder;

    /**
     * Sets the {@link ITestObjectBuilder} that constructs new test objects with
     * {@link #newChemObject()}.
     *
     * @param builder ITestChemObject that instantiates new test objects
     */
    public static void setTestObjectBuilder(ITestObjectBuilder builder) {
        AbstractCDKObjectTest.builder = builder;
    }

    public static IChemObject newChemObject() {
        return AbstractCDKObjectTest.builder.newTestObject();
    }

    @Test
    public void testGetBuilder() {
        IChemObject chemObject = newChemObject();
        Object object = chemObject.getBuilder();
        Assert.assertNotNull(object);
        Assert.assertTrue(object instanceof IChemObjectBuilder);
    }
}
