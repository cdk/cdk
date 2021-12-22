/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
 * Checks the functionality of {@link ILonePair} implementations.
 *
 * @see org.openscience.cdk.LonePair
 *
 * @cdk.module test-interfaces
 */
public abstract class AbstractLonePairTest extends AbstractElectronContainerTest {

    @Test
    public void testSetAtom_IAtom() {
        ILonePair lp = (ILonePair) newChemObject();
        IAtom atom = lp.getBuilder().newInstance(IAtom.class, "N");
        lp.setAtom(atom);
        Assert.assertEquals(atom, lp.getAtom());
    }

    @Test
    public void testGetAtom() {
        ILonePair lp = (ILonePair) newChemObject();
        IAtom atom = lp.getBuilder().newInstance(IAtom.class, "N");
        Assert.assertNull(lp.getAtom());
        lp.setAtom(atom);
        Assert.assertEquals(atom, lp.getAtom());
    }

    @Test
    @Override
    public void testGetElectronCount() {
        ILonePair lp = (ILonePair) newChemObject();
        Assert.assertEquals(2, lp.getElectronCount().intValue());

        lp = lp.getBuilder().newInstance(ILonePair.class, lp.getBuilder().newInstance(IAtom.class, "N"));
        Assert.assertEquals(2, lp.getElectronCount().intValue());
    }

    @Test
    public void testContains_IAtom() {
        ILonePair lp = (ILonePair) newChemObject();
        IAtom atom = lp.getBuilder().newInstance(IAtom.class, "N");
        lp.setAtom(atom);
        Assert.assertTrue(lp.contains(atom));
    }

    @Test
    @Override
    public void testClone() throws Exception {
        ILonePair lp = (ILonePair) newChemObject();
        Object clone = lp.clone();
        Assert.assertTrue(clone instanceof ILonePair);
    }

    @Test
    public void testClone_IAtom() throws Exception {
        ILonePair lp = (ILonePair) newChemObject();
        IAtom atom = lp.getBuilder().newInstance(IAtom.class, "N");
        lp.setAtom(atom);

        // test cloning of atom
        ILonePair clone = (ILonePair) lp.clone();
        Assert.assertNotSame(atom, clone.getAtom());
    }

    /** Test for RFC #9 */
    @Test
    @Override
    public void testToString() {
        ILonePair lp = (ILonePair) newChemObject();
        String description = lp.toString();
        for (int i = 0; i < description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }

    /**
     * The electron count of an LP is always exactly 2.
     */
    @Test
    @Override
    public void testSetElectronCount_Integer() {
        IElectronContainer ec = (IElectronContainer) newChemObject();
        ec.setElectronCount(3);
        Assert.assertEquals(2, ec.getElectronCount().intValue());
        ec.setElectronCount(null);
        Assert.assertEquals(2, ec.getElectronCount().intValue());
    }

}
