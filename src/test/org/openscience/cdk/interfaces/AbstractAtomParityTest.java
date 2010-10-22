/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.interfaces;

import org.junit.Assert;
import org.junit.Test;

/**
 * Checks the functionality of {@link IAtomParity} implementations.
 *
 * @cdk.module test-interfaces
 *
 * @see org.openscience.cdk.AtomParity
 */
public abstract class AbstractAtomParityTest extends AbstractCDKObjectTest {

    private static IChemObjectBuilder builder;

    public static IChemObjectBuilder getNewBuilder() {
        return builder;
    }

    public static void setBuilder(IChemObjectBuilder builder ) {
        AbstractAtomParityTest.builder = builder;
    }

    @Test public void testGetAtom() {
        IAtom carbon = getNewBuilder().newInstance(IAtom.class,"C");
        carbon.setID("central");
        IAtom carbon1 = getNewBuilder().newInstance(IAtom.class,"C");
        carbon1.setID("c1");
        IAtom carbon2 = getNewBuilder().newInstance(IAtom.class,"C");
        carbon2.setID("c2");
        IAtom carbon3 = getNewBuilder().newInstance(IAtom.class,"C");
        carbon3.setID("c3");
        IAtom carbon4 = getNewBuilder().newInstance(IAtom.class,"C");
        carbon4.setID("c4");
        int parityInt = 1;
        IAtomParity parity = getNewBuilder().newInstance(IAtomParity.class, carbon, carbon1, carbon2, carbon3, carbon4, parityInt);
        Assert.assertEquals(carbon, parity.getAtom());
    }
    
    @Test public void testGetSurroundingAtoms() {
        IAtom carbon = getNewBuilder().newInstance(IAtom.class,"C");
        carbon.setID("central");
        IAtom carbon1 = getNewBuilder().newInstance(IAtom.class,"C");
        carbon1.setID("c1");
        IAtom carbon2 = getNewBuilder().newInstance(IAtom.class,"C");
        carbon2.setID("c2");
        IAtom carbon3 = getNewBuilder().newInstance(IAtom.class,"C");
        carbon3.setID("c3");
        IAtom carbon4 = getNewBuilder().newInstance(IAtom.class,"C");
        carbon4.setID("c4");
        int parityInt = 1;
        IAtomParity parity = getNewBuilder().newInstance(IAtomParity.class, carbon, carbon1, carbon2, carbon3, carbon4, parityInt);
        org.openscience.cdk.interfaces.IAtom[] neighbors = parity.getSurroundingAtoms();
        Assert.assertEquals(4, neighbors.length);
        Assert.assertEquals(carbon1, neighbors[0]);
        Assert.assertEquals(carbon2, neighbors[1]);
        Assert.assertEquals(carbon3, neighbors[2]);
        Assert.assertEquals(carbon4, neighbors[3]);
    }
    
    @Test public void testGetParity() {
        IAtom carbon = getNewBuilder().newInstance(IAtom.class,"C");
        carbon.setID("central");
        IAtom carbon1 = getNewBuilder().newInstance(IAtom.class,"C");
        carbon1.setID("c1");
        IAtom carbon2 = getNewBuilder().newInstance(IAtom.class,"C");
        carbon2.setID("c2");
        IAtom carbon3 = getNewBuilder().newInstance(IAtom.class,"C");
        carbon3.setID("c3");
        IAtom carbon4 = getNewBuilder().newInstance(IAtom.class,"C");
        carbon4.setID("c4");
        int parityInt = 1;
        IAtomParity parity = getNewBuilder().newInstance(IAtomParity.class, carbon, carbon1, carbon2, carbon3, carbon4, parityInt);
        Assert.assertEquals(parityInt, parity.getParity());
    }
    
    /** Test for RFC #9 */
    @Test public void testToString() {
        IAtom carbon = getNewBuilder().newInstance(IAtom.class,"C");
        carbon.setID("central");
        IAtom carbon1 = getNewBuilder().newInstance(IAtom.class,"C");
        carbon1.setID("c1");
        IAtom carbon2 = getNewBuilder().newInstance(IAtom.class,"C");
        carbon2.setID("c2");
        IAtom carbon3 = getNewBuilder().newInstance(IAtom.class,"C");
        carbon3.setID("c3");
        IAtom carbon4 = getNewBuilder().newInstance(IAtom.class,"C");
        carbon4.setID("c4");
        int parityInt = 1;
        IAtomParity parity = getNewBuilder().newInstance(IAtomParity.class, carbon, carbon1, carbon2, carbon3, carbon4, parityInt);
        String description = parity.toString();
        for (int i=0; i< description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }

}
