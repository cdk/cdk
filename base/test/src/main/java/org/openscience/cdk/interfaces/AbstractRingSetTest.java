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
 *
 */
package org.openscience.cdk.interfaces;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

/**
 * Checks the functionality of {@link IRingSet} implementations.
 *
 * @cdk.module test-interfaces
 */
public abstract class AbstractRingSetTest extends AbstractAtomContainerSetTest {

    @Test
    public void testAdd_IRingSet() {
        IRingSet rs = (IRingSet) newChemObject();
        IRing r1 = rs.getBuilder().newInstance(IRing.class, 5, "C");
        IRing r2 = rs.getBuilder().newInstance(IRing.class, 3, "C");
        rs.addAtomContainer(r1);

        IRingSet rs2 = (IRingSet) newChemObject();
        rs2.addAtomContainer(r2);
        rs2.add(rs);

        Assert.assertEquals(1, rs.getAtomContainerCount());
        Assert.assertEquals(2, rs2.getAtomContainerCount());
    }

    @Test
    @Override
    public void testToString() {
        IRingSet ringset = (IRingSet) newChemObject();
        String description = ringset.toString();
        for (int i = 0; i < description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }

    @Test
    @Override
    public void testClone() throws CloneNotSupportedException {
        IRingSet ringset = (IRingSet) newChemObject();
        IRing ring = ringset.getBuilder().newInstance(IRing.class);
        ringset.addAtomContainer(ring);

        IRingSet clone = (IRingSet) ringset.clone();
        Assert.assertNotNull(clone);
        Assert.assertTrue(clone instanceof IRingSet);
        Assert.assertEquals(1, clone.getAtomContainerCount());
        Assert.assertNotSame(ring, clone.getAtomContainer(0));
    }

    @Test
    public void testContains_IAtom() {
        IRingSet ringset = (IRingSet) newChemObject();

        IAtom ring1Atom1 = ringset.getBuilder().newInstance(IAtom.class, "C"); // rather artificial molecule
        IAtom ring1Atom2 = ringset.getBuilder().newInstance(IAtom.class, "C");
        IAtom sharedAtom1 = ringset.getBuilder().newInstance(IAtom.class, "C");
        IAtom sharedAtom2 = ringset.getBuilder().newInstance(IAtom.class, "C");
        IAtom ring2Atom1 = ringset.getBuilder().newInstance(IAtom.class, "C");
        IAtom ring2Atom2 = ringset.getBuilder().newInstance(IAtom.class, "C");
        IBond ring1Bond1 = ringset.getBuilder().newInstance(IBond.class, ring1Atom1, ring1Atom2);
        IBond ring1Bond2 = ringset.getBuilder().newInstance(IBond.class, sharedAtom1, ring1Atom1);
        IBond ring1Bond3 = ringset.getBuilder().newInstance(IBond.class, sharedAtom2, ring1Atom2);
        IBond sharedBond = ringset.getBuilder().newInstance(IBond.class, sharedAtom1, sharedAtom2);
        IBond ring2Bond1 = ringset.getBuilder().newInstance(IBond.class, ring2Atom1, ring2Atom2);
        IBond ring2Bond2 = ringset.getBuilder().newInstance(IBond.class, sharedAtom1, ring2Atom1);
        IBond ring2Bond3 = ringset.getBuilder().newInstance(IBond.class, sharedAtom2, ring2Atom2);

        IRing ring1 = ringset.getBuilder().newInstance(IRing.class);
        ring1.addAtom(ring1Atom1);
        ring1.addAtom(ring1Atom2);
        ring1.addAtom(sharedAtom1);
        ring1.addAtom(sharedAtom2);
        ring1.addBond(ring1Bond1);
        ring1.addBond(ring1Bond2);
        ring1.addBond(ring1Bond3);
        ring1.addBond(sharedBond);
        IRing ring2 = ringset.getBuilder().newInstance(IRing.class);
        ring2.addAtom(ring2Atom1);
        ring2.addAtom(ring2Atom2);
        ring2.addAtom(sharedAtom1);
        ring2.addAtom(sharedAtom2);
        ring2.addBond(ring2Bond1);
        ring2.addBond(ring2Bond2);
        ring2.addBond(ring2Bond3);
        ring2.addBond(sharedBond);

        ringset.addAtomContainer(ring1);
        ringset.addAtomContainer(ring2);

        Assert.assertTrue(ringset.contains(ring1Atom1));
        Assert.assertTrue(ringset.contains(ring1Atom2));
        Assert.assertTrue(ringset.contains(sharedAtom1));
        Assert.assertTrue(ringset.contains(sharedAtom2));
        Assert.assertTrue(ringset.contains(ring2Atom1));
        Assert.assertTrue(ringset.contains(ring2Atom2));
    }

    @Test
    public void testContains_IAtomContainer() {
        IRingSet ringset = (IRingSet) newChemObject();

        IAtom ring1Atom1 = ringset.getBuilder().newInstance(IAtom.class, "C"); // rather artificial molecule
        IAtom ring1Atom2 = ringset.getBuilder().newInstance(IAtom.class, "C");
        IAtom sharedAtom1 = ringset.getBuilder().newInstance(IAtom.class, "C");
        IAtom sharedAtom2 = ringset.getBuilder().newInstance(IAtom.class, "C");
        IAtom ring2Atom1 = ringset.getBuilder().newInstance(IAtom.class, "C");
        IAtom ring2Atom2 = ringset.getBuilder().newInstance(IAtom.class, "C");
        IBond ring1Bond1 = ringset.getBuilder().newInstance(IBond.class, ring1Atom1, ring1Atom2);
        IBond ring1Bond2 = ringset.getBuilder().newInstance(IBond.class, sharedAtom1, ring1Atom1);
        IBond ring1Bond3 = ringset.getBuilder().newInstance(IBond.class, sharedAtom2, ring1Atom2);
        IBond sharedBond = ringset.getBuilder().newInstance(IBond.class, sharedAtom1, sharedAtom2);
        IBond ring2Bond1 = ringset.getBuilder().newInstance(IBond.class, ring2Atom1, ring2Atom2);
        IBond ring2Bond2 = ringset.getBuilder().newInstance(IBond.class, sharedAtom1, ring2Atom1);
        IBond ring2Bond3 = ringset.getBuilder().newInstance(IBond.class, sharedAtom2, ring2Atom2);

        IRing ring1 = ringset.getBuilder().newInstance(IRing.class);
        ring1.addAtom(ring1Atom1);
        ring1.addAtom(ring1Atom2);
        ring1.addAtom(sharedAtom1);
        ring1.addAtom(sharedAtom2);
        ring1.addBond(ring1Bond1);
        ring1.addBond(ring1Bond2);
        ring1.addBond(ring1Bond3);
        ring1.addBond(sharedBond);
        IRing ring2 = ringset.getBuilder().newInstance(IRing.class);
        ring2.addAtom(ring2Atom1);
        ring2.addAtom(ring2Atom2);
        ring2.addAtom(sharedAtom1);
        ring2.addAtom(sharedAtom2);
        ring2.addBond(ring2Bond1);
        ring2.addBond(ring2Bond2);
        ring2.addBond(ring2Bond3);
        ring2.addBond(sharedBond);

        ringset.addAtomContainer(ring1);
        ringset.addAtomContainer(ring2);

        Assert.assertTrue(ringset.contains(ring1));
        Assert.assertTrue(ringset.contains(ring2));
    }

    @Test
    public void testGetRings_IBond() {
        IRingSet ringset = (IRingSet) newChemObject();

        IAtom ring1Atom1 = ringset.getBuilder().newInstance(IAtom.class, "C"); // rather artificial molecule
        IAtom ring1Atom2 = ringset.getBuilder().newInstance(IAtom.class, "C");
        IAtom sharedAtom1 = ringset.getBuilder().newInstance(IAtom.class, "C");
        IAtom sharedAtom2 = ringset.getBuilder().newInstance(IAtom.class, "C");
        IAtom ring2Atom1 = ringset.getBuilder().newInstance(IAtom.class, "C");
        IAtom ring2Atom2 = ringset.getBuilder().newInstance(IAtom.class, "C");
        IBond ring1Bond1 = ringset.getBuilder().newInstance(IBond.class, ring1Atom1, ring1Atom2);
        IBond ring1Bond2 = ringset.getBuilder().newInstance(IBond.class, sharedAtom1, ring1Atom1);
        IBond ring1Bond3 = ringset.getBuilder().newInstance(IBond.class, sharedAtom2, ring1Atom2);
        IBond sharedBond = ringset.getBuilder().newInstance(IBond.class, sharedAtom1, sharedAtom2);
        IBond ring2Bond1 = ringset.getBuilder().newInstance(IBond.class, ring2Atom1, ring2Atom2);
        IBond ring2Bond2 = ringset.getBuilder().newInstance(IBond.class, sharedAtom1, ring2Atom1);
        IBond ring2Bond3 = ringset.getBuilder().newInstance(IBond.class, sharedAtom2, ring2Atom2);

        IRing ring1 = ringset.getBuilder().newInstance(IRing.class);
        ring1.addAtom(ring1Atom1);
        ring1.addAtom(ring1Atom2);
        ring1.addAtom(sharedAtom1);
        ring1.addAtom(sharedAtom2);
        ring1.addBond(ring1Bond1);
        ring1.addBond(ring1Bond2);
        ring1.addBond(ring1Bond3);
        ring1.addBond(sharedBond);
        IRing ring2 = ringset.getBuilder().newInstance(IRing.class);
        ring2.addAtom(ring2Atom1);
        ring2.addAtom(ring2Atom2);
        ring2.addAtom(sharedAtom1);
        ring2.addAtom(sharedAtom2);
        ring2.addBond(ring2Bond1);
        ring2.addBond(ring2Bond2);
        ring2.addBond(ring2Bond3);
        ring2.addBond(sharedBond);

        ringset.addAtomContainer(ring1);
        ringset.addAtomContainer(ring2);

        Assert.assertEquals(1, ringset.getRings(ring1Bond1).getAtomContainerCount());
        Assert.assertEquals(1, ringset.getRings(ring1Bond2).getAtomContainerCount());
        Assert.assertEquals(1, ringset.getRings(ring1Bond3).getAtomContainerCount());
        Assert.assertEquals(2, ringset.getRings(sharedBond).getAtomContainerCount());
        Assert.assertEquals(1, ringset.getRings(ring2Bond1).getAtomContainerCount());
        Assert.assertEquals(1, ringset.getRings(ring2Bond2).getAtomContainerCount());
        Assert.assertEquals(1, ringset.getRings(ring2Bond3).getAtomContainerCount());
    }

    @Test
    public void testGetRings_IAtom() {
        IRingSet ringset = (IRingSet) newChemObject();

        IAtom ring1Atom1 = ringset.getBuilder().newInstance(IAtom.class, "C"); // rather artificial molecule
        IAtom ring1Atom2 = ringset.getBuilder().newInstance(IAtom.class, "C");
        IAtom sharedAtom1 = ringset.getBuilder().newInstance(IAtom.class, "C");
        IAtom sharedAtom2 = ringset.getBuilder().newInstance(IAtom.class, "C");
        IAtom ring2Atom1 = ringset.getBuilder().newInstance(IAtom.class, "C");
        IAtom ring2Atom2 = ringset.getBuilder().newInstance(IAtom.class, "C");
        IBond ring1Bond1 = ringset.getBuilder().newInstance(IBond.class, ring1Atom1, ring1Atom2);
        IBond ring1Bond2 = ringset.getBuilder().newInstance(IBond.class, sharedAtom1, ring1Atom1);
        IBond ring1Bond3 = ringset.getBuilder().newInstance(IBond.class, sharedAtom2, ring1Atom2);
        IBond sharedBond = ringset.getBuilder().newInstance(IBond.class, sharedAtom1, sharedAtom2);
        IBond ring2Bond1 = ringset.getBuilder().newInstance(IBond.class, ring2Atom1, ring2Atom2);
        IBond ring2Bond2 = ringset.getBuilder().newInstance(IBond.class, sharedAtom1, ring2Atom1);
        IBond ring2Bond3 = ringset.getBuilder().newInstance(IBond.class, sharedAtom2, ring2Atom2);

        IRing ring1 = ringset.getBuilder().newInstance(IRing.class);
        ring1.addAtom(ring1Atom1);
        ring1.addAtom(ring1Atom2);
        ring1.addAtom(sharedAtom1);
        ring1.addAtom(sharedAtom2);
        ring1.addBond(ring1Bond1);
        ring1.addBond(ring1Bond2);
        ring1.addBond(ring1Bond3);
        ring1.addBond(sharedBond);
        IRing ring2 = ringset.getBuilder().newInstance(IRing.class);
        ring2.addAtom(ring2Atom1);
        ring2.addAtom(ring2Atom2);
        ring2.addAtom(sharedAtom1);
        ring2.addAtom(sharedAtom2);
        ring2.addBond(ring2Bond1);
        ring2.addBond(ring2Bond2);
        ring2.addBond(ring2Bond3);
        ring2.addBond(sharedBond);

        ringset.addAtomContainer(ring1);
        ringset.addAtomContainer(ring2);

        Assert.assertEquals(1, ringset.getRings(ring1Atom1).getAtomContainerCount());
        Assert.assertEquals(1, ringset.getRings(ring1Atom1).getAtomContainerCount());
        Assert.assertEquals(2, ringset.getRings(sharedAtom1).getAtomContainerCount());
        Assert.assertEquals(2, ringset.getRings(sharedAtom2).getAtomContainerCount());
        Assert.assertEquals(1, ringset.getRings(ring2Atom1).getAtomContainerCount());
        Assert.assertEquals(1, ringset.getRings(ring2Atom2).getAtomContainerCount());
    }

    @Test
    public void testGetConnectedRings_IRing() {
        IRingSet ringset = (IRingSet) newChemObject();

        IAtom ring1Atom1 = ringset.getBuilder().newInstance(IAtom.class, "C"); // rather artificial molecule
        IAtom ring1Atom2 = ringset.getBuilder().newInstance(IAtom.class, "C");
        IAtom sharedAtom1 = ringset.getBuilder().newInstance(IAtom.class, "C");
        IAtom sharedAtom2 = ringset.getBuilder().newInstance(IAtom.class, "C");
        IAtom ring2Atom1 = ringset.getBuilder().newInstance(IAtom.class, "C");
        IAtom ring2Atom2 = ringset.getBuilder().newInstance(IAtom.class, "C");
        IBond ring1Bond1 = ringset.getBuilder().newInstance(IBond.class, ring1Atom1, ring1Atom2);
        IBond ring1Bond2 = ringset.getBuilder().newInstance(IBond.class, sharedAtom1, ring1Atom1);
        IBond ring1Bond3 = ringset.getBuilder().newInstance(IBond.class, sharedAtom2, ring1Atom2);
        IBond sharedBond = ringset.getBuilder().newInstance(IBond.class, sharedAtom1, sharedAtom2);
        IBond ring2Bond1 = ringset.getBuilder().newInstance(IBond.class, ring2Atom1, ring2Atom2);
        IBond ring2Bond2 = ringset.getBuilder().newInstance(IBond.class, sharedAtom1, ring2Atom1);
        IBond ring2Bond3 = ringset.getBuilder().newInstance(IBond.class, sharedAtom2, ring2Atom2);

        IRing ring1 = ringset.getBuilder().newInstance(IRing.class);
        ring1.addAtom(ring1Atom1);
        ring1.addAtom(ring1Atom2);
        ring1.addAtom(sharedAtom1);
        ring1.addAtom(sharedAtom2);
        ring1.addBond(ring1Bond1);
        ring1.addBond(ring1Bond2);
        ring1.addBond(ring1Bond3);
        ring1.addBond(sharedBond);
        IRing ring2 = ringset.getBuilder().newInstance(IRing.class);
        ring2.addAtom(ring2Atom1);
        ring2.addAtom(ring2Atom2);
        ring2.addAtom(sharedAtom1);
        ring2.addAtom(sharedAtom2);
        ring2.addBond(ring2Bond1);
        ring2.addBond(ring2Bond2);
        ring2.addBond(ring2Bond3);
        ring2.addBond(sharedBond);

        ringset.addAtomContainer(ring1);
        ringset.addAtomContainer(ring2);

        Assert.assertEquals(1, ringset.getConnectedRings(ring2).getAtomContainerCount());
        Assert.assertEquals(1, ringset.getConnectedRings(ring1).getAtomContainerCount());
    }

    /**
     * Test for RingSetTest bug #1772613.
     * When using method getConnectedRings(...) of RingSet.java fused or bridged rings
     * returned a list of connected rings that contained duplicates.
     * Bug fix by Andreas Schueller <a.schueller@chemie.uni-frankfurt.de>
     * @cdk.bug 1772613
     */
    @Test
    public void testGetConnectedRingsBug1772613() throws Exception {
        // Build a bridged and fused norbomane like ring system
        // C1CCC2C(C1)C4CC2C3CCCCC34
        IRingSet ringSet = (IRingSet) newChemObject();
        IRing leftCyclohexane = ringSet.getBuilder().newInstance(IRing.class, 6, "C");
        IRing rightCyclopentane = ringSet.getBuilder().newInstance(IRing.class, 5, "C");

        IRing leftCyclopentane = ringSet.getBuilder().newInstance(IRing.class);
        IBond leftCyclohexane0RightCyclopentane4 = ringSet.getBuilder().newInstance(IBond.class,
                leftCyclohexane.getAtom(0), rightCyclopentane.getAtom(4));
        IBond leftCyclohexane1RightCyclopentane2 = ringSet.getBuilder().newInstance(IBond.class,
                leftCyclohexane.getAtom(1), rightCyclopentane.getAtom(2));
        leftCyclopentane.addAtom(leftCyclohexane.getAtom(0));
        leftCyclopentane.addAtom(leftCyclohexane.getAtom(1));
        leftCyclopentane.addAtom(rightCyclopentane.getAtom(2));
        leftCyclopentane.addAtom(rightCyclopentane.getAtom(3));
        leftCyclopentane.addAtom(rightCyclopentane.getAtom(4));
        leftCyclopentane.addBond(leftCyclohexane.getBond(leftCyclohexane.getAtom(0), leftCyclohexane.getAtom(1)));
        leftCyclopentane.addBond(leftCyclohexane1RightCyclopentane2);
        leftCyclopentane.addBond(rightCyclopentane.getBond(rightCyclopentane.getAtom(2), rightCyclopentane.getAtom(3)));
        leftCyclopentane.addBond(rightCyclopentane.getBond(rightCyclopentane.getAtom(3), rightCyclopentane.getAtom(4)));
        leftCyclopentane.addBond(leftCyclohexane0RightCyclopentane4);

        IRing rightCyclohexane = ringSet.getBuilder().newInstance(IRing.class);
        IAtom rightCyclohexaneAtom0 = ringSet.getBuilder().newInstance(IAtom.class, "C");
        IAtom rightCyclohexaneAtom1 = ringSet.getBuilder().newInstance(IAtom.class, "C");
        IAtom rightCyclohexaneAtom2 = ringSet.getBuilder().newInstance(IAtom.class, "C");
        IAtom rightCyclohexaneAtom5 = ringSet.getBuilder().newInstance(IAtom.class, "C");
        IBond rightCyclohexaneAtom0Atom1 = ringSet.getBuilder().newInstance(IBond.class, rightCyclohexaneAtom0,
                rightCyclohexaneAtom1);
        IBond rightCyclohexaneAtom1Atom2 = ringSet.getBuilder().newInstance(IBond.class, rightCyclohexaneAtom1,
                rightCyclohexaneAtom2);
        IBond rightCyclohexane2rightCyclopentane1 = ringSet.getBuilder().newInstance(IBond.class,
                rightCyclohexaneAtom2, rightCyclopentane.getAtom(1));
        IBond rightCyclohexane5rightCyclopentane0 = ringSet.getBuilder().newInstance(IBond.class,
                rightCyclohexaneAtom5, rightCyclopentane.getAtom(0));
        IBond rightCyclohexaneAtom0Atom5 = ringSet.getBuilder().newInstance(IBond.class, rightCyclohexaneAtom0,
                rightCyclohexaneAtom5);
        rightCyclohexane.addAtom(rightCyclohexaneAtom0);
        rightCyclohexane.addAtom(rightCyclohexaneAtom1);
        rightCyclohexane.addAtom(rightCyclohexaneAtom2);
        rightCyclohexane.addAtom(rightCyclopentane.getAtom(1));
        rightCyclohexane.addAtom(rightCyclopentane.getAtom(0));
        rightCyclohexane.addAtom(rightCyclohexaneAtom5);
        rightCyclohexane.addBond(rightCyclohexaneAtom0Atom1);
        rightCyclohexane.addBond(rightCyclohexaneAtom1Atom2);
        rightCyclohexane.addBond(rightCyclohexane2rightCyclopentane1);
        rightCyclohexane.addBond(rightCyclopentane.getBond(rightCyclopentane.getAtom(0), rightCyclopentane.getAtom(1)));
        rightCyclohexane.addBond(rightCyclohexane5rightCyclopentane0);
        rightCyclohexane.addBond(rightCyclohexaneAtom0Atom5);

        ringSet.addAtomContainer(leftCyclohexane);
        ringSet.addAtomContainer(leftCyclopentane);
        ringSet.addAtomContainer(rightCyclopentane);
        ringSet.addAtomContainer(rightCyclohexane);

        // Get connected rings
        IRingSet connectedRings = ringSet.getConnectedRings(leftCyclohexane);

        // Iterate over the connectedRings and fail if any duplicate is found
        List<IRing> foundRings = new ArrayList<IRing>();
        for (IAtomContainer container : connectedRings.atomContainers()) {
            IRing connectedRing = (IRing) container;
            if (foundRings.contains(connectedRing)) Assert.fail("The list of connected rings contains duplicates.");
            foundRings.add(connectedRing);
        }
    }

    @Test
    @Override
    public void testIsEmpty() {

        IRingSet ringSet = (IRingSet) newChemObject();

        org.hamcrest.MatcherAssert.assertThat("new ringset should be empty", ringSet.isEmpty(), is(true));

        ringSet.addAtomContainer(ringSet.getBuilder().newInstance(IAtomContainer.class));

        org.hamcrest.MatcherAssert.assertThat("ringset with an atom container should not be empty", ringSet.isEmpty(), is(not(true)));

        ringSet.removeAllAtomContainers();

        org.hamcrest.MatcherAssert.assertThat("ringset with removed atom containers should be empty", ringSet.isEmpty(), is(true));

    }

}
