/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.interfaces;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.tools.diff.IsotopeDiff;

/**
 * Checks the functionality of the Isotope class.
 *
 * @cdk.module test-interfaces
 *
 * @see org.openscience.cdk.Isotope
 */
public abstract class AbstractIsotopeTest extends AbstractElementTest {

    @Test
    public void testSetNaturalAbundance_Double() {
        IIsotope i = (IIsotope) newChemObject();
        i.setNaturalAbundance(80.0);
        Assertions.assertEquals(80.0, i.getNaturalAbundance(), 0.001);
    }

    @Test
    public void testGetNaturalAbundance() {
        testSetNaturalAbundance_Double();
    }

    @Test
    public void testSetExactMass_Double() {
        IIsotope i = (IIsotope) newChemObject();
        i.setExactMass(12.03);
        Assertions.assertEquals(12.03, i.getExactMass(), 0.001);
    }

    @Test
    public void testGetExactMass() {
        testSetExactMass_Double();
    }

    @Test
    public void testSetMassNumber_Integer() {
        IIsotope i = (IIsotope) newChemObject();
        i.setMassNumber(2);
        Assertions.assertEquals(2, i.getMassNumber().intValue());
    }

    @Test
    public void testGetMassNumber() {
        testSetMassNumber_Integer();
    }

    /**
     * Method to test the clone() method
     */
    @Test
    @Override
    public void testClone() throws Exception {
        IIsotope iso = (IIsotope) newChemObject();
        Object clone = iso.clone();
        Assertions.assertTrue(clone instanceof IIsotope);

        // test that everything has been cloned properly
        String diff = IsotopeDiff.diff(iso, (IIsotope) clone);
        Assertions.assertNotNull(diff);
        Assertions.assertEquals(0, diff.length());
    }

    /**
     * Method to test the clone() method
     */
    @Test
    public void testClone_ExactMass() throws Exception {
        IIsotope iso = (IIsotope) newChemObject();
        iso.setExactMass(1.0);
        IIsotope clone = (IIsotope) iso.clone();

        // test cloning of exact mass
        iso.setExactMass(2.0);
        Assertions.assertEquals(1.0, clone.getExactMass(), 0.001);
    }

    /**
     * Method to test the clone() method
     */
    @Test
    public void testClone_NaturalAbundance() throws Exception {
        IIsotope iso = (IIsotope) newChemObject();
        iso.setNaturalAbundance(1.0);
        IIsotope clone = (IIsotope) iso.clone();

        // test cloning of exact mass
        iso.setNaturalAbundance(2.0);
        Assertions.assertEquals(1.0, clone.getNaturalAbundance(), 0.001);
    }

    /**
     * Method to test the clone() method
     */
    @Test
    public void testClone_MassNumber() throws Exception {
        IIsotope iso = (IIsotope) newChemObject();
        iso.setMassNumber(12);
        IIsotope clone = (IIsotope) iso.clone();

        // test cloning of exact mass
        iso.setMassNumber(13);
        Assertions.assertEquals(12, clone.getMassNumber().intValue());
    }

    /**
     * Method to test whether the class complies with RFC #9.
     */
    @Test
    @Override
    public void testToString() {
        IIsotope iso = (IIsotope) newChemObject();
        String description = iso.toString();
        for (int i = 0; i < description.length(); i++) {
            Assertions.assertTrue(description.charAt(i) != '\n');
            Assertions.assertTrue(description.charAt(i) != '\r');
        }
    }

}
