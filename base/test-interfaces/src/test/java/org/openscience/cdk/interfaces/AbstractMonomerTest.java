/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
 *  */
package org.openscience.cdk.interfaces;

import org.junit.Assert;
import org.junit.Test;

/**
 * TestCase for {@link IMonomer} implementations.
 *
 * @cdk.module test-interfaces
 *
 * @author Edgar Luttman &lt;edgar@uni-paderborn.de&gt;
 * @cdk.created 2001-08-09
 */
public abstract class AbstractMonomerTest extends AbstractAtomContainerTest {

    @Test
    public void testSetMonomerName_String() {
        IMonomer m = (IMonomer) newChemObject();
        m.setMonomerName(new String("TRP279"));
        Assert.assertEquals(new String("TRP279"), m.getMonomerName());
    }

    @Test
    public void testGetMonomerName() {
        testSetMonomerName_String();
    }

    @Test
    public void testSetMonomerType_String() {
        IMonomer oMonomer = (IMonomer) newChemObject();
        oMonomer.setMonomerType(new String("TRP"));
        Assert.assertEquals(new String("TRP"), oMonomer.getMonomerType());
    }

    @Test
    public void testGetMonomerType() {
        testSetMonomerType_String();
    }

    /**
     * Method to test whether the class complies with RFC #9.
     */
    @Test
    @Override
    public void testToString() {
        IMonomer oMonomer = (IMonomer) newChemObject();
        oMonomer.setMonomerType(new String("TRP"));
        String description = oMonomer.toString();
        for (int i = 0; i < description.length(); i++) {
            Assert.assertTrue('\n' != description.charAt(i));
            Assert.assertTrue('\r' != description.charAt(i));
        }
    }

    @Test
    @Override
    public void testClone() throws Exception {
        IMonomer oMonomer = (IMonomer) newChemObject();
        Object clone = oMonomer.clone();
        Assert.assertTrue(clone instanceof IMonomer);
        Assert.assertNotSame(oMonomer, clone);
    }
}
