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
 */
package org.openscience.cdk.interfaces;

import org.junit.Assert;
import org.junit.Test;
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
        Assert.assertEquals(80.0, i.getNaturalAbundance(), 0.001);
    }

    @Test
    public void testGetNaturalAbundance() {
        testSetNaturalAbundance_Double();
    }

    @Test
    public void testSetExactMass_Double() {
        IIsotope i = (IIsotope) newChemObject();
        i.setExactMass(12.03);
        Assert.assertEquals(12.03, i.getExactMass(), 0.001);
    }

    @Test
    public void testGetExactMass() {
        testSetExactMass_Double();
    }

    @Test
    public void testSetMassNumber_Integer() {
        IIsotope i = (IIsotope) newChemObject();
        i.setMassNumber(2);
        Assert.assertEquals(2, i.getMassNumber().intValue());
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
        Assert.assertTrue(clone instanceof IIsotope);

        // test that everything has been cloned properly
        String diff = IsotopeDiff.diff(iso, (IIsotope) clone);
        Assert.assertNotNull(diff);
        Assert.assertEquals(0, diff.length());
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
        Assert.assertEquals(1.0, clone.getExactMass(), 0.001);
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
        Assert.assertEquals(1.0, clone.getNaturalAbundance(), 0.001);
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
        Assert.assertEquals(12, clone.getMassNumber().intValue());
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
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }

}
