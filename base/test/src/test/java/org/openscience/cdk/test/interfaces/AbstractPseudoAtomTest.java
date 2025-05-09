/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.interfaces;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IPseudoAtom;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

/**
 * Checks the functionality of {@link org.openscience.cdk.interfaces.IPseudoAtom} implementations.
 *
 */
public abstract class AbstractPseudoAtomTest extends AbstractAtomTest {

    @Test
    public void testGetLabel() {
        String label = "Arg255";
        IPseudoAtom a = (IPseudoAtom) newChemObject();
        a.setLabel(label);
        Assertions.assertEquals(label, a.getLabel());
    }

    @Test
    public void testSetLabel_String() {
        String label = "Arg255";
        IPseudoAtom atom = (IPseudoAtom) newChemObject();
        atom.setLabel(label);
        String label2 = "His66";
        atom.setLabel(label2);
        Assertions.assertEquals(label2, atom.getLabel());
    }

    @Test
    @Override
    public void testGetFormalCharge() {
        IPseudoAtom atom = (IPseudoAtom) newChemObject();
        Assertions.assertEquals(0, atom.getFormalCharge().intValue());
    }

    @Test
    @Override
    public void testSetFormalCharge_Integer() {
        IPseudoAtom atom = (IPseudoAtom) newChemObject();
        atom.setFormalCharge(+5);
        Assertions.assertEquals(+5, atom.getFormalCharge().intValue());
    }

    @Test
    public void testSetHydrogenCount_Integer() {
        IPseudoAtom atom = (IPseudoAtom) newChemObject();
        atom.setImplicitHydrogenCount(+5);
        Assertions.assertEquals(5, atom.getImplicitHydrogenCount().intValue());
    }

    @Test
    @Override
    public void testSetCharge_Double() {
        IPseudoAtom atom = (IPseudoAtom) newChemObject();
        atom.setCharge(0.78);
        Assertions.assertEquals(0.78, atom.getCharge(), 0.001);
    }

    @Test
    @Override
    public void testSetExactMass_Double() {
        IPseudoAtom atom = (IPseudoAtom) newChemObject();
        atom.setExactMass(12.001);
        Assertions.assertEquals(12.001, atom.getExactMass(), 0.001);
    }

    @Test
    @Override
    public void testSetStereoParity_Integer() {
        IPseudoAtom atom = (IPseudoAtom) newChemObject();
        atom.setStereoParity(-1);
        Assertions.assertEquals(0, atom.getStereoParity().intValue());
    }

    @Test
    public void testPseudoAtom_IAtom() {
        IChemObject object = newChemObject();
        IAtom atom = object.getBuilder().newInstance(IAtom.class, "C");
        Point3d fract = new Point3d(0.5, 0.5, 0.5);
        Point3d threeD = new Point3d(0.5, 0.5, 0.5);
        Point2d twoD = new Point2d(0.5, 0.5);
        atom.setFractionalPoint3d(fract);
        atom.setPoint3d(threeD);
        atom.setPoint2d(twoD);

        IPseudoAtom a = object.getBuilder().newInstance(IPseudoAtom.class, atom);
        assertEquals(fract, a.getFractionalPoint3d(), 0.0001);
        assertEquals(threeD, a.getPoint3d(), 0.0001);
        assertEquals(twoD, a.getPoint2d(), 0.0001);
    }

    /**
     * Method to test the clone() method
     */
    @Test
    @Override
    public void testClone() throws Exception {
        IPseudoAtom atom = (IPseudoAtom) newChemObject();
        Object clone = atom.clone();
        Assertions.assertTrue(clone instanceof IPseudoAtom);
    }

    /**
     * Method to test whether the class complies with RFC #9.
     */
    @Test
    @Override
    public void testToString() {
        IAtom atom = (IPseudoAtom) newChemObject();
        String description = atom.toString();
        for (int i = 0; i < description.length(); i++) {
            Assertions.assertTrue(description.charAt(i) != '\n');
            Assertions.assertTrue(description.charAt(i) != '\r');
        }
    }

    /**
     * Test for bug #1778479 "MDLWriter writes empty PseudoAtom label string".
     * We decided to let the pseudo atoms have a default label of '*'.
     *
     * Author: Andreas Schueller &lt;a.schueller@chemie.uni-frankfurt.de&gt;
     *
     * @cdk.bug 1778479
     */
    @Test
    public void testBug1778479DefaultLabel() {
        IPseudoAtom atom = (IPseudoAtom) newChemObject();
        Assertions.assertNotNull(atom.getLabel(), "Test for PseudoAtom's default label");
        Assertions.assertEquals("*", atom.getLabel(), "Test for PseudoAtom's default label");
    }

    /**
     * Overwrite the method in {@link AbstractAtomTest} to always
     * expect zero hydrogen counts.
     */
    @Test
    @Override
    public void testClone_HydrogenCount() throws Exception {
        IAtom atom = (IAtom) newChemObject();
        atom.setImplicitHydrogenCount(3);
        IAtom clone = atom.clone();

        // test cloning
        atom.setImplicitHydrogenCount(4);
        Assertions.assertEquals(3, clone.getImplicitHydrogenCount().intValue());
    }

    /**
     * Overwrite the method in {@link AbstractAtomTest} to always
     * expect zero hydrogen counts.
     */
    @Test
    public void testGetHydrogenCount() {
        // expect zero by definition
        IAtom a = (IAtom) newChemObject();
        Assertions.assertNull(a.getImplicitHydrogenCount());
        a.setImplicitHydrogenCount(5);
        Assertions.assertEquals(5, a.getImplicitHydrogenCount().intValue());
        a.setImplicitHydrogenCount(null);
        Assertions.assertNull(a.getImplicitHydrogenCount());
    }

    /**
     * Overwrite the method in {@link AbstractAtomTypeTest} to always
     * expect zero stereo parity.
     */
    @Test
    @Override
    public void testClone_StereoParity() throws Exception {
        IAtom atom = (IAtom) newChemObject();
        atom.setStereoParity(3);
        IAtom clone = atom.clone();

        // test cloning
        atom.setStereoParity(4);
        Assertions.assertEquals(0, clone.getStereoParity().intValue());
    }

    @Test
    public void testPseudoAtomCharges() {
        String label = "charged patom";
        IPseudoAtom a = (IPseudoAtom) newChemObject();
        a.setLabel(label);
        a.setFormalCharge(-1);
        Assertions.assertNotNull(a);
        Assertions.assertNotNull(a.getFormalCharge());
        Assertions.assertEquals(-1, a.getFormalCharge().intValue());
    }
}
