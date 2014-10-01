/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
 * Checks the functionality of {@link ISingleElectron} implementations.
 *
 * @see org.openscience.cdk.SingleElectron
 *
 * @cdk.module test-interfaces
 */
public abstract class AbstractSingleElectronTest extends AbstractElectronContainerTest {

    @Test
    @Override
    public void testGetElectronCount() {
        ISingleElectron radical = (ISingleElectron) newChemObject();
        Assert.assertEquals(1, radical.getElectronCount().intValue());
    }

    @Test
    public void testContains_IAtom() {
        IChemObject object = newChemObject();
        IAtom atom = object.getBuilder().newInstance(IAtom.class, "N");
        ISingleElectron radical = object.getBuilder().newInstance(ISingleElectron.class, atom);
        Assert.assertTrue(radical.contains(atom));
    }

    @Test
    public void testSetAtom_IAtom() {
        ISingleElectron radical = (ISingleElectron) newChemObject();
        IAtom atom = radical.getBuilder().newInstance(IAtom.class, "N");
        Assert.assertNull(radical.getAtom());
        radical.setAtom(atom);
        Assert.assertEquals(atom, radical.getAtom());
    }

    @Test
    public void testGetAtom() {
        IChemObject object = newChemObject();
        IAtom atom = object.getBuilder().newInstance(IAtom.class, "N");
        ISingleElectron radical = object.getBuilder().newInstance(ISingleElectron.class, atom);
        Assert.assertEquals(atom, radical.getAtom());
    }

    @Test
    @Override
    public void testClone() throws Exception {
        ISingleElectron radical = (ISingleElectron) newChemObject();
        Object clone = radical.clone();
        Assert.assertNotNull(clone);
        Assert.assertTrue(clone instanceof ISingleElectron);
    }

    @Test
    public void testClone_IAtom() throws Exception {
        ISingleElectron radical = (ISingleElectron) newChemObject();
        IAtom atom = radical.getBuilder().newInstance(IAtom.class, "N");
        radical.setAtom(atom);

        // test cloning of atom
        ISingleElectron clone = (ISingleElectron) radical.clone();
        Assert.assertNotSame(atom, clone.getAtom());
    }

    /** Test for RFC #9 */
    @Test
    @Override
    public void testToString() {
        ISingleElectron radical = (ISingleElectron) newChemObject();
        String description = radical.toString();
        for (int i = 0; i < description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }

    /**
     * The electron count of a single electron is always exactly 1.
     */
    @Test
    @Override
    public void testSetElectronCount_Integer() {
        IElectronContainer ec = (IElectronContainer) newChemObject();
        ec.setElectronCount(3);
        Assert.assertEquals(1, ec.getElectronCount().intValue());
        ec.setElectronCount(null);
        Assert.assertEquals(1, ec.getElectronCount().intValue());
    }
}
