/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.interfaces;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.interfaces.ISingleElectron;

/**
 * Checks the functionality of {@link org.openscience.cdk.interfaces.ISingleElectron} implementations.
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
