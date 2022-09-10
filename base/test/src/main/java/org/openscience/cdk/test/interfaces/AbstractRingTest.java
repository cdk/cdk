/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.interfaces;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.tools.manipulator.BondManipulator;

/**
 * Checks the functionality of {@link org.openscience.cdk.interfaces.IRing} implementations.
 *
 * @cdk.module test-interfaces
 */
public abstract class AbstractRingTest extends AbstractAtomContainerTest {

    @Test
    public void testGetBondOrderSum() {
        IChemObject object = newChemObject();
        IRing r = object.getBuilder().newInstance(IRing.class, 5, "C");
        Assert.assertEquals(5, r.getBondOrderSum());

        BondManipulator.increaseBondOrder(r.getBond(0));
        Assert.assertEquals(6, r.getBondOrderSum());

        BondManipulator.increaseBondOrder(r.getBond(0));
        Assert.assertEquals(7, r.getBondOrderSum());

        BondManipulator.increaseBondOrder(r.getBond(4));
        Assert.assertEquals(8, r.getBondOrderSum());
    }

    @Test
    public void testGetRingSize() {
        IChemObject object = newChemObject();
        IRing r = object.getBuilder().newInstance(IRing.class, 5, "C");
        Assert.assertEquals(5, r.getRingSize());
    }

    @Test
    public void testGetNextBond_IBond_IAtom() {
        IRing ring = (IRing) newChemObject();
        IAtom c1 = ring.getBuilder().newInstance(IAtom.class, "C");
        IAtom c2 = ring.getBuilder().newInstance(IAtom.class, "C");
        IAtom c3 = ring.getBuilder().newInstance(IAtom.class, "C");
        IBond b1 = ring.getBuilder().newInstance(IBond.class, c1, c2, IBond.Order.SINGLE);
        IBond b2 = ring.getBuilder().newInstance(IBond.class, c3, c2, IBond.Order.SINGLE);
        IBond b3 = ring.getBuilder().newInstance(IBond.class, c1, c3, IBond.Order.SINGLE);
        ring.addAtom(c1);
        ring.addAtom(c2);
        ring.addAtom(c3);
        ring.addBond(b1);
        ring.addBond(b2);
        ring.addBond(b3);

        Assert.assertEquals(b1, ring.getNextBond(b2, c2));
        Assert.assertEquals(b1, ring.getNextBond(b3, c1));
        Assert.assertEquals(b2, ring.getNextBond(b1, c2));
        Assert.assertEquals(b2, ring.getNextBond(b3, c3));
        Assert.assertEquals(b3, ring.getNextBond(b1, c1));
        Assert.assertEquals(b3, ring.getNextBond(b2, c3));
    }

    @Test
    @Override
    public void testToString() {
        IChemObject object = newChemObject();
        IRing r = object.getBuilder().newInstance(IRing.class, 5, "C");
        String description = r.toString();
        for (int i = 0; i < description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }
}
