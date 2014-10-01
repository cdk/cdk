/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
 * Checks the functionality of {@link IAtomContainer} implementations.
 *
 * @cdk.module test-interfaces
 */
public abstract class AbstractMoleculeTest extends AbstractAtomContainerTest {

    @Test
    @Override
    public void testClone() throws Exception {
        IAtomContainer molecule = (IAtomContainer) newChemObject();
        Object clone = molecule.clone();
        Assert.assertTrue(clone instanceof IAtomContainer);
        Assert.assertNotSame(molecule, clone);
    }

    /** Test for RFC #9 */
    @Test
    @Override
    public void testToString() {
        IAtomContainer m = (IAtomContainer) newChemObject();
        String description = m.toString();
        for (int i = 0; i < description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }

    @Test
    public void testGetLonePairCount_Molecule() {
        IAtomContainer acetone = (IAtomContainer) newChemObject();
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class, "C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class, "C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class, "O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class, "C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class, c1, c2, IBond.Order.SINGLE);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class, c1, o, IBond.Order.DOUBLE);
        IBond b3 = acetone.getBuilder().newInstance(IBond.class, c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);

        // add lone pairs on oxygen
        ILonePair lp1 = acetone.getBuilder().newInstance(ILonePair.class, o);
        ILonePair lp2 = acetone.getBuilder().newInstance(ILonePair.class, o);
        acetone.addLonePair(lp1);
        acetone.addLonePair(lp2);

        Assert.assertEquals(2, acetone.getLonePairCount());
    }
}
