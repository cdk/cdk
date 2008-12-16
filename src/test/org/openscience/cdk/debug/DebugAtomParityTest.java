/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.debug;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.AtomParity;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomParity;
import org.openscience.cdk.interfaces.IAtomParityTest;

/**
 * Checks the functionality of the {@link DebugAtomParity}.
 *
 * @cdk.module test-datadebug
 */
public class DebugAtomParityTest extends IAtomParityTest {

    @BeforeClass public static void setUp() {
        setBuilder(DebugChemObjectBuilder.getInstance());
    }

    @Test public void testCorrectInstance() {
    	IAtomParity parity = getBuilder().newAtomParity(getBuilder().newAtom(), getBuilder().newAtom(), getBuilder().newAtom(), getBuilder().newAtom(), getBuilder().newAtom(), 1); 
    	Assert.assertTrue(
    		"Object not instance of DebugAtomParity, but: " + parity.getClass().getName(),
    		parity instanceof DebugAtomParity
    	);
    }

    @Test public void testDebugAtomParity_IAtom_IAtom_IAtom_IAtom_IAtom_int() {
        IAtom carbon = getBuilder().newAtom("C");
        carbon.setID("central");
        IAtom carbon1 = getBuilder().newAtom("C");
        carbon1.setID("c1");
        IAtom carbon2 = getBuilder().newAtom("C");
        carbon2.setID("c2");
        IAtom carbon3 = getBuilder().newAtom("C");
        carbon3.setID("c3");
        IAtom carbon4 = getBuilder().newAtom("C");
        carbon4.setID("c4");
        int parityInt = 1;
        AtomParity parity = new DebugAtomParity(carbon, carbon1, carbon2, carbon3, carbon4, parityInt);
        Assert.assertNotNull(parity);
    }
}
