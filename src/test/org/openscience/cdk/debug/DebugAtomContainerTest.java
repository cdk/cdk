/* $Revision$ $Author$ $Date$    
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
 */
package org.openscience.cdk.debug;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerTest;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;

/**
 * Checks the functionality of the {@link DebugAtomContainer}.
 *
 * @cdk.module test-datadebug
 */
public class DebugAtomContainerTest extends IAtomContainerTest {

    @BeforeClass public static void setUp() {
        setChemObject(new DebugAtomContainer());
    }

    @Test public void testDebugAtomContainer_int_int_int_int() {
        // create an empty container with predefined
        // array lengths
        IAtomContainer ac = new DebugAtomContainer(5,6,1,2);
        
        Assert.assertEquals(0, ac.getAtomCount());
        Assert.assertEquals(0, ac.getElectronContainerCount());

        // test whether the ElectronContainer is correctly initialized
        ac.addBond(getBuilder().newBond(getBuilder().newAtom("C"), getBuilder().newAtom("C"), IBond.Order.DOUBLE));
        ac.addLonePair(getBuilder().newLonePair(getBuilder().newAtom("N")));
    }

    @Test public void testDebugAtomContainer() {
        // create an empty container with in the constructor defined array lengths
        IAtomContainer container = new DebugAtomContainer();
        
        Assert.assertEquals(0, container.getAtomCount());
        Assert.assertEquals(0, container.getBondCount());
        
        // test whether the ElectronContainer is correctly initialized
        container.addBond(getBuilder().newBond(getBuilder().newAtom("C"), getBuilder().newAtom("C"), IBond.Order.DOUBLE));
        container.addLonePair(getBuilder().newLonePair(getBuilder().newAtom("N")));
    }

    @Test public void testDebugAtomContainer_IAtomContainer() {
        IMolecule acetone = getBuilder().newMolecule();
        IAtom c1 = getBuilder().newAtom("C");
        IAtom c2 = getBuilder().newAtom("C");
        IAtom o = getBuilder().newAtom("O");
        IAtom c3 = getBuilder().newAtom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        IBond b1 = getBuilder().newBond(c1, c2, IBond.Order.SINGLE);
        IBond b2 = getBuilder().newBond(c1, o, IBond.Order.DOUBLE);
        IBond b3 = getBuilder().newBond(c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        IAtomContainer container = new DebugAtomContainer(acetone);
        Assert.assertEquals(4, container.getAtomCount());
        Assert.assertEquals(3, container.getBondCount());
    }
}
