/* Copyright (C) 2006-2007  The Chemistry Development Kit Project
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
 */
package org.openscience.cdk.tools.manipulator;

import java.util.List;
import java.util.Vector;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.templates.TestMoleculeFactory;

/**
 * @cdk.module test-standard
 */
public class RingSetManipulatorTest extends CDKTestCase {

    protected IChemObjectBuilder builder;

    private IRingSet             ringset        = null;
    private IAtom                ring1Atom1     = null;
    private IAtom                ring1Atom3     = null;
    private IAtom                ring2Atom3     = null;
    private IBond                bondRing2Ring3 = null;
    private IRing                ring2          = null;
    private IRing                ring3          = null;

    @Before
    public void setUp() {
        builder = DefaultChemObjectBuilder.getInstance();
        ringset = builder.newInstance(IRingSet.class);
        ring1Atom1 = builder.newInstance(IAtom.class, "C"); // rather artificial molecule
        IAtom ring1Atom2 = builder.newInstance(IAtom.class, "C");
        ring1Atom3 = builder.newInstance(IAtom.class, "C");
        IAtom ring2Atom1 = builder.newInstance(IAtom.class, "C");
        IAtom ring2Atom2 = builder.newInstance(IAtom.class, "C");
        ring2Atom3 = builder.newInstance(IAtom.class, "C");
        IAtom ring3Atom3 = builder.newInstance(IAtom.class, "C");
        IAtom ring3Atom4 = builder.newInstance(IAtom.class, "C");

        IAtom ring4Atom1 = builder.newInstance(IAtom.class, "C");
        IAtom ring4Atom2 = builder.newInstance(IAtom.class, "C");

        IBond ring1Bond1 = builder.newInstance(IBond.class, ring1Atom1, ring1Atom2);
        IBond ring1Bond2 = builder.newInstance(IBond.class, ring1Atom2, ring1Atom3);
        IBond ring1Bond3 = builder.newInstance(IBond.class, ring1Atom3, ring1Atom1);
        bondRing2Ring3 = builder.newInstance(IBond.class, ring2Atom1, ring2Atom2);
        IBond ring2Bond2 = builder.newInstance(IBond.class, ring2Atom2, ring2Atom3);
        IBond ring2Bond3 = builder.newInstance(IBond.class, ring2Atom3, ring2Atom1, IBond.Order.DOUBLE);
        IBond ring3Bond2 = builder.newInstance(IBond.class, ring2Atom2, ring3Atom3);
        IBond bondRing3Ring4 = builder.newInstance(IBond.class, ring3Atom3, ring3Atom4);
        IBond ring3Bond4 = builder.newInstance(IBond.class, ring3Atom4, ring2Atom1);
        IBond ring4Bond1 = builder.newInstance(IBond.class, ring4Atom1, ring4Atom2);
        IBond ring4Bond2 = builder.newInstance(IBond.class, ring4Atom2, ring3Atom3);
        IBond ring4Bond3 = builder.newInstance(IBond.class, ring3Atom4, ring4Atom1);

        IRing ring1 = builder.newInstance(IRing.class);
        ring1.addAtom(ring1Atom1);
        ring1.addAtom(ring1Atom2);
        ring1.addAtom(ring1Atom3);
        ring1.addBond(ring1Bond1);
        ring1.addBond(ring1Bond2);
        ring1.addBond(ring1Bond3);

        ring2 = builder.newInstance(IRing.class);
        ring2.addAtom(ring2Atom1);
        ring2.addAtom(ring2Atom2);
        ring2.addAtom(ring2Atom3);
        ring2.addBond(bondRing2Ring3);
        ring2.addBond(ring2Bond2);
        ring2.addBond(ring2Bond3);

        ring3 = builder.newInstance(IRing.class);
        ring3.addAtom(ring2Atom1);
        ring3.addAtom(ring2Atom2);
        ring3.addAtom(ring3Atom3);
        ring3.addAtom(ring3Atom4);
        ring3.addBond(bondRing2Ring3);
        ring3.addBond(ring3Bond2);
        ring3.addBond(bondRing3Ring4);
        ring3.addBond(ring3Bond4);

        IRing ring4 = builder.newInstance(IRing.class);
        ring4.addAtom(ring4Atom1);
        ring4.addAtom(ring4Atom2);
        ring4.addAtom(ring3Atom3);
        ring4.addAtom(ring3Atom4);
        ring4.addBond(bondRing3Ring4);
        ring4.addBond(ring4Bond1);
        ring4.addBond(ring4Bond2);
        ring4.addBond(ring4Bond3);

        ringset.addAtomContainer(ring1);
        ringset.addAtomContainer(ring2);
        ringset.addAtomContainer(ring3);
        ringset.addAtomContainer(ring4);
    }

    @Test
    public void testIsSameRing_IRingSet_IAtom_IAtom() {
        Assert.assertTrue(RingSetManipulator.isSameRing(ringset, ring1Atom1, ring1Atom3));
        Assert.assertFalse(RingSetManipulator.isSameRing(ringset, ring1Atom1, ring2Atom3));
    }

    @Test
    public void testRingAlreadyInSet_IRing_IRingSet() {
        IRing r1 = builder.newInstance(IRing.class, 5, "C");
        IRing r2 = builder.newInstance(IRing.class, 3, "C");

        IRingSet rs = builder.newInstance(IRingSet.class);
        Assert.assertFalse(RingSetManipulator.ringAlreadyInSet(r1, rs));
        Assert.assertFalse(RingSetManipulator.ringAlreadyInSet(r2, rs));

        rs.addAtomContainer(r1);
        Assert.assertTrue(RingSetManipulator.ringAlreadyInSet(r1, rs));
        Assert.assertFalse(RingSetManipulator.ringAlreadyInSet(r2, rs));

        rs.addAtomContainer(r2);
        Assert.assertTrue(RingSetManipulator.ringAlreadyInSet(r1, rs));
        Assert.assertTrue(RingSetManipulator.ringAlreadyInSet(r2, rs));
    }

    @Test
    public void testGetAllAtomContainers_IRingSet() {
        IRingSet rs = builder.newInstance(IRingSet.class);
        rs.addAtomContainer(builder.newInstance(IRing.class));
        rs.addAtomContainer(builder.newInstance(IRing.class));
        List<IAtomContainer> list = RingSetManipulator.getAllAtomContainers(rs);
        Assert.assertEquals(2, list.size());
    }

    @Test
    public void testGetAtomCount_IRingSet() {
        IRingSet rs = builder.newInstance(IRingSet.class);
        IAtomContainer ac1 = builder.newInstance(IRing.class);
        ac1.addAtom(builder.newInstance(IAtom.class, "O"));
        rs.addAtomContainer(ac1);
        IAtomContainer ac2 = builder.newInstance(IRing.class);
        ac2.addAtom(builder.newInstance(IAtom.class, "C"));
        ac2.addAtom(builder.newInstance(IAtom.class, "C"));
        ac2.addBond(0, 1, IBond.Order.DOUBLE);
        rs.addAtomContainer(ac2);
        Assert.assertEquals(3, RingSetManipulator.getAtomCount(rs));
        Assert.assertEquals(1, RingSetManipulator.getBondCount(rs));
    }

    @Test
    public void testGetHeaviestRing_IRingSet_IBond() {
        IRing ring = RingSetManipulator.getHeaviestRing(ringset, bondRing2Ring3);
        Assert.assertEquals(ring2, ring);
    }

    @Test
    public void testGetMostComplexRing_IRingSet() {
        IRing ring = RingSetManipulator.getMostComplexRing(ringset);
        Assert.assertEquals(ring3, ring);
    }

    @Test
    public void testSort_IRingSet() {
        RingSetManipulator.sort(ringset);
        Assert.assertEquals(4, ringset.getAtomContainerCount());
        int currentSize = ringset.getAtomContainer(0).getAtomCount();
        for (int i = 1; i < ringset.getAtomContainerCount(); ++i) {
            Assert.assertTrue(ringset.getAtomContainer(i).getAtomCount() >= currentSize);
            currentSize = ringset.getAtomContainer(i).getAtomCount();
        }
    }

    @Test
    public void testGetBondCount() throws Exception {
        IAtomContainer mol = TestMoleculeFactory.makeAdenine();
        AllRingsFinder arf = new AllRingsFinder();
        IRingSet ringSet = arf.findAllRings(mol);
        Assert.assertEquals(3, ringSet.getAtomContainerCount());
        Assert.assertEquals(20, RingSetManipulator.getBondCount(ringSet));

        mol = TestMoleculeFactory.makeBiphenyl();
        ringSet = arf.findAllRings(mol);
        Assert.assertEquals(2, ringSet.getAtomContainerCount());
        Assert.assertEquals(12, RingSetManipulator.getBondCount(ringSet));
    }

    @Test
    public void markAromatic() throws Exception {
        IAtomContainer mol = TestMoleculeFactory.makeBiphenyl();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);

        AllRingsFinder arf = new AllRingsFinder();
        IRingSet ringSet = arf.findAllRings(mol);
        Assert.assertEquals(2, ringSet.getAtomContainerCount());

        RingSetManipulator.markAromaticRings(ringSet);
        for (int i = 0; i < ringSet.getAtomContainerCount(); i++) {
            IRing ring = (IRing) ringSet.getAtomContainer(i);
            Assert.assertTrue(ring.getFlag(CDKConstants.ISAROMATIC));
        }
    }

    @Test
    public void testGetAllInOneContainer_IRingSet() {
        IAtomContainer ac = RingSetManipulator.getAllInOneContainer(ringset);
        Assert.assertEquals(10, ac.getAtomCount());
    }

    @Test
    public void testGetLargestRingSet_List_IRingSet() throws Exception {
        List<IRingSet> list = new Vector<IRingSet>();
        list.add(ringset);
        IAtomContainer mol = TestMoleculeFactory.makeBiphenyl();

        AllRingsFinder arf = new AllRingsFinder();
        IRingSet ringSet = arf.findAllRings(mol);
        list.add(ringSet);
        Assert.assertEquals(2, RingSetManipulator.getLargestRingSet(list).getAtomContainerCount());
    }
}
