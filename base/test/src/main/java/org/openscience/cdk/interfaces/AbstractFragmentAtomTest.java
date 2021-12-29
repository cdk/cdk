/* Copyright (C) 2002-2007  Egon Willighagen <egonw@users.sf.net>
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
 * Checks the functionality of {@link IFragmentAtom} implementations.
 *
 * @cdk.module test-interfaces
 */
public abstract class AbstractFragmentAtomTest extends AbstractPseudoAtomTest {

    @Test
    public void testGetFragment() {
        IFragmentAtom a = (IFragmentAtom) newChemObject();
        // make sure that we start with a not-null, but empty container
        Assert.assertNotNull(a.getFragment());
        Assert.assertEquals(0, a.getFragment().getAtomCount());
        Assert.assertEquals(0, a.getFragment().getBondCount());
    }

    @Test
    public void testIsExpanded() {
        IFragmentAtom a = (IFragmentAtom) newChemObject();
        Assert.assertNotNull(a);
        Assert.assertFalse(a.isExpanded()); // test the default state
    }

    @Test
    public void testSetExpanded_boolean() {
        IFragmentAtom a = (IFragmentAtom) newChemObject();
        Assert.assertNotNull(a);
        a.setExpanded(true);
        Assert.assertTrue(a.isExpanded());
        a.setExpanded(false);
        Assert.assertFalse(a.isExpanded());
    }

    @Test
    public void testSetFragment_IAtomContainer() {
        IFragmentAtom a = (IFragmentAtom) newChemObject();
        Assert.assertNotNull(a);
        IAtomContainer container = a.getBuilder().newInstance(IAtomContainer.class);
        container.addAtom(a.getBuilder().newInstance(IAtom.class, "N"));
        container.addAtom(a.getBuilder().newInstance(IAtom.class, "C"));
        container.addBond(0, 1, IBond.Order.TRIPLE);
        a.setFragment(container);
        Assert.assertEquals(container, a.getFragment());
    }

    @Test
    @Override
    public void testGetExactMass() {
        IFragmentAtom a = (IFragmentAtom) newChemObject();
        Assert.assertNotNull(a);
        IAtomContainer container = a.getBuilder().newInstance(IAtomContainer.class);
        container.addAtom(a.getBuilder().newInstance(IAtom.class, "N"));
        container.getAtom(0).setExactMass(5.5);
        container.addAtom(a.getBuilder().newInstance(IAtom.class, "C"));
        container.getAtom(1).setExactMass(3.5);
        container.addBond(0, 1, IBond.Order.TRIPLE);
        a.setFragment(container);
        Assert.assertEquals(9.0, a.getExactMass(), 0.0001);
    }

    /** Test for RFC #9 */
    @Test
    @Override
    public void testToString() {
        IFragmentAtom bond = (IFragmentAtom) newChemObject();
        String description = bond.toString();
        for (int i = 0; i < description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }

    /**
     * Overwrites the {@link AbstractPseudoAtomTest} version.
     */
    @Test(expected = IllegalAccessError.class)
    @Override
    public void testSetExactMass_Double() {
        IPseudoAtom atom = (IPseudoAtom) newChemObject();
        atom.setExactMass(12.001);
    }

    @Test
    @Override
    public void testClone_ExactMass() throws Exception {
        // do not test this, as the exact mass is a implicit
        // property calculated from the fragment
    }

}
