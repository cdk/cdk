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
import org.openscience.cdk.interfaces.IFragmentAtom;
import org.openscience.cdk.interfaces.IPseudoAtom;

/**
 * Checks the functionality of {@link org.openscience.cdk.interfaces.IFragmentAtom} implementations.
 *
 * @cdk.module test-interfaces
 */
public abstract class AbstractFragmentAtomTest extends AbstractPseudoAtomTest {

    @Test
    public void testGetFragment() {
        IFragmentAtom a = (IFragmentAtom) newChemObject();
        // make sure that we start with a not-null, but empty container
        Assertions.assertNotNull(a.getFragment());
        Assertions.assertEquals(0, a.getFragment().getAtomCount());
        Assertions.assertEquals(0, a.getFragment().getBondCount());
    }

    @Test
    public void testIsExpanded() {
        IFragmentAtom a = (IFragmentAtom) newChemObject();
        Assertions.assertNotNull(a);
        Assertions.assertFalse(a.isExpanded()); // test the default state
    }

    @Test
    public void testSetExpanded_boolean() {
        IFragmentAtom a = (IFragmentAtom) newChemObject();
        Assertions.assertNotNull(a);
        a.setExpanded(true);
        Assertions.assertTrue(a.isExpanded());
        a.setExpanded(false);
        Assertions.assertFalse(a.isExpanded());
    }

    @Test
    public void testSetFragment_IAtomContainer() {
        IFragmentAtom a = (IFragmentAtom) newChemObject();
        Assertions.assertNotNull(a);
        IAtomContainer container = a.getBuilder().newInstance(IAtomContainer.class);
        container.addAtom(a.getBuilder().newInstance(IAtom.class, "N"));
        container.addAtom(a.getBuilder().newInstance(IAtom.class, "C"));
        container.addBond(0, 1, IBond.Order.TRIPLE);
        a.setFragment(container);
        Assertions.assertEquals(container, a.getFragment());
    }

    @Test
    @Override
    public void testGetExactMass() {
        IFragmentAtom a = (IFragmentAtom) newChemObject();
        Assertions.assertNotNull(a);
        IAtomContainer container = a.getBuilder().newInstance(IAtomContainer.class);
        container.addAtom(a.getBuilder().newInstance(IAtom.class, "N"));
        container.getAtom(0).setExactMass(5.5);
        container.addAtom(a.getBuilder().newInstance(IAtom.class, "C"));
        container.getAtom(1).setExactMass(3.5);
        container.addBond(0, 1, IBond.Order.TRIPLE);
        a.setFragment(container);
        Assertions.assertEquals(9.0, a.getExactMass(), 0.0001);
    }

    /** Test for RFC #9 */
    @Test
    @Override
    public void testToString() {
        IFragmentAtom bond = (IFragmentAtom) newChemObject();
        String description = bond.toString();
        for (int i = 0; i < description.length(); i++) {
            Assertions.assertTrue(description.charAt(i) != '\n');
            Assertions.assertTrue(description.charAt(i) != '\r');
        }
    }

    /**
     * Overwrites the {@link AbstractPseudoAtomTest} version.
     */
    @Test
    @Override
    public void testSetExactMass_Double() {
        IPseudoAtom atom = (IPseudoAtom) newChemObject();
        Assertions.assertThrows(IllegalAccessError.class,
                                () -> {
                                    atom.setExactMass(12.001);
                                });
    }

    @Test
    @Override
    public void testClone_ExactMass() throws Exception {
        // do not test this, as the exact mass is a implicit
        // property calculated from the fragment
    }

}
