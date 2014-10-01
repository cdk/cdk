/* Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
 *
 */
package org.openscience.cdk.interfaces;

import org.junit.Assert;
import org.junit.Test;

/**
 * Checks the functionality of {@link IPDBMonomer} implementations.
 *
 * @cdk.module test-interfaces
 */
public abstract class AbstractPDBMonomerTest extends AbstractMonomerTest {

    @Test
    public void testSetICode_String() {
        IPDBMonomer monomer = (IPDBMonomer) newChemObject();
        monomer.setICode(null);
        Assert.assertNull(monomer.getICode());
    }

    @Test
    public void testGetICode() {
        IPDBMonomer monomer = (IPDBMonomer) newChemObject();
        Assert.assertNull(monomer.getICode());
        monomer.setICode("iCode");
        Assert.assertNotNull(monomer.getICode());
        Assert.assertEquals("iCode", monomer.getICode());
    }

    @Test
    public void testSetChainID_String() {
        IPDBMonomer monomer = (IPDBMonomer) newChemObject();
        monomer.setChainID(null);
        Assert.assertNull(monomer.getChainID());
    }

    @Test
    public void testGetChainID() {
        IPDBMonomer monomer = (IPDBMonomer) newChemObject();
        Assert.assertNull(monomer.getChainID());
        monomer.setChainID("chainA");
        Assert.assertNotNull(monomer.getChainID());
        Assert.assertEquals("chainA", monomer.getChainID());
    }

    @Test
    public void testSetResSeq_String() {
        IPDBMonomer monomer = (IPDBMonomer) newChemObject();
        monomer.setResSeq(null);
        Assert.assertNull(monomer.getResSeq());
    }

    @Test
    public void testGetResSeq() {
        IPDBMonomer monomer = (IPDBMonomer) newChemObject();
        Assert.assertNull(monomer.getResSeq());
        monomer.setResSeq("reqSeq");
        Assert.assertNotNull(monomer.getResSeq());
        Assert.assertEquals("reqSeq", monomer.getResSeq());
    }

    @Test
    @Override
    public void testToString() {
        IPDBMonomer monomer = (IPDBMonomer) newChemObject();
        String description = monomer.toString();
        for (int i = 0; i < description.length(); i++) {
            Assert.assertTrue('\n' != description.charAt(i));
            Assert.assertTrue('\r' != description.charAt(i));
        }
    }

}
