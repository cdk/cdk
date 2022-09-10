/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.interfaces;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.ILonePair;

/**
 * Checks the functionality of {@link org.openscience.cdk.interfaces.IAtomContainer} implementations.
 *
 * @cdk.module test-interfaces
 */
public abstract class AbstractMoleculeTest extends AbstractAtomContainerTest {

    @Test
    @Override
    public void testClone() throws Exception {
        IAtomContainer molecule = (IAtomContainer) newChemObject();
        Object clone = molecule.clone();
        Assertions.assertTrue(clone instanceof IAtomContainer);
        Assertions.assertNotSame(molecule, clone);
    }

    /** Test for RFC #9 */
    @Test
    @Override
    public void testToString() {
        IAtomContainer m = (IAtomContainer) newChemObject();
        String description = m.toString();
        for (int i = 0; i < description.length(); i++) {
            Assertions.assertTrue(description.charAt(i) != '\n');
            Assertions.assertTrue(description.charAt(i) != '\r');
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

        Assertions.assertEquals(2, acetone.getLonePairCount());
    }
}
