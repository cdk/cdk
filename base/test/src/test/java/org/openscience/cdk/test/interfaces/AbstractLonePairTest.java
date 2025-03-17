/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.interfaces;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.interfaces.ILonePair;

/**
 * Checks the functionality of {@link org.openscience.cdk.interfaces.ILonePair} implementations.
 *
 * @see org.openscience.cdk.LonePair
 *
 */
public abstract class AbstractLonePairTest extends AbstractElectronContainerTest {

    @Test
    public void testSetAtom_IAtom() {
        ILonePair lp = (ILonePair) newChemObject();
        IAtom atom = lp.getBuilder().newInstance(IAtom.class, "N");
        lp.setAtom(atom);
        Assertions.assertEquals(atom, lp.getAtom());
    }

    @Test
    public void testGetAtom() {
        ILonePair lp = (ILonePair) newChemObject();
        IAtom atom = lp.getBuilder().newInstance(IAtom.class, "N");
        Assertions.assertNull(lp.getAtom());
        lp.setAtom(atom);
        Assertions.assertEquals(atom, lp.getAtom());
    }

    @Test
    @Override
    public void testGetElectronCount() {
        ILonePair lp = (ILonePair) newChemObject();
        Assertions.assertEquals(2, lp.getElectronCount().intValue());

        lp = lp.getBuilder().newInstance(ILonePair.class, lp.getBuilder().newInstance(IAtom.class, "N"));
        Assertions.assertEquals(2, lp.getElectronCount().intValue());
    }

    @Test
    public void testContains_IAtom() {
        ILonePair lp = (ILonePair) newChemObject();
        IAtom atom = lp.getBuilder().newInstance(IAtom.class, "N");
        lp.setAtom(atom);
        Assertions.assertTrue(lp.contains(atom));
    }

    @Test
    @Override
    public void testClone() throws Exception {
        ILonePair lp = (ILonePair) newChemObject();
        Object clone = lp.clone();
        Assertions.assertTrue(clone instanceof ILonePair);
    }

    @Test
    public void testClone_IAtom() throws Exception {
        ILonePair lp = (ILonePair) newChemObject();
        IAtom atom = lp.getBuilder().newInstance(IAtom.class, "N");
        lp.setAtom(atom);

        // test cloning of atom
        ILonePair clone = (ILonePair) lp.clone();
        Assertions.assertNotSame(atom, clone.getAtom());
    }

    /** Test for RFC #9 */
    @Test
    @Override
    public void testToString() {
        ILonePair lp = (ILonePair) newChemObject();
        String description = lp.toString();
        for (int i = 0; i < description.length(); i++) {
            Assertions.assertTrue(description.charAt(i) != '\n');
            Assertions.assertTrue(description.charAt(i) != '\r');
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
        Assertions.assertEquals(2, ec.getElectronCount().intValue());
        ec.setElectronCount(null);
        Assertions.assertEquals(2, ec.getElectronCount().intValue());
    }

}
