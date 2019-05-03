/* Copyright (C) 2014  Tomas Pluskal <plusik@gmail.com>
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
package org.openscience.cdk.formula;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IMolecularFormulaSet;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

/**
 * Checks the functionality of the MolecularFormulaGenerator.
 *
 * @cdk.module test-formula
 */
public class MolecularFormulaGeneratorTest extends CDKTestCase {

    private final IChemObjectBuilder builder = SilentChemObjectBuilder
            .getInstance();

    /**
     * Test the getNextFormula() method
     */
    @Test
    public void testGetNextFormula() throws Exception {

        IsotopeFactory ifac = Isotopes.getInstance();
        IIsotope c = ifac.getMajorIsotope("C");
        IIsotope h = ifac.getMajorIsotope("H");
        IIsotope n = ifac.getMajorIsotope("N");
        IIsotope o = ifac.getMajorIsotope("O");

        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(c, 0, 10);
        mfRange.addIsotope(h, 0, 10);
        mfRange.addIsotope(o, 0, 10);
        mfRange.addIsotope(n, 0, 10);

        double minMass = 100.0;
        double maxMass = 100.05;

        MolecularFormulaGenerator gen = new MolecularFormulaGenerator(builder,
                minMass, maxMass, mfRange);
        IMolecularFormula f = gen.getNextFormula();
        Assert.assertNotNull(f);

    }

    /**
     * Test the getAllFormulas() method
     */
    @Test
    public void testGetAllFormulas() throws Exception {

        IsotopeFactory ifac = Isotopes.getInstance();
        IIsotope c = ifac.getMajorIsotope("C");
        IIsotope h = ifac.getMajorIsotope("H");
        IIsotope n = ifac.getMajorIsotope("N");
        IIsotope o = ifac.getMajorIsotope("O");

        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(c, 0, 10);
        mfRange.addIsotope(h, 0, 10);
        mfRange.addIsotope(o, 0, 10);
        mfRange.addIsotope(n, 0, 10);

        double minMass = 100.0;
        double maxMass = 100.05;

        MolecularFormulaGenerator gen = new MolecularFormulaGenerator(builder,
                minMass, maxMass, mfRange);
        IMolecularFormulaSet mfSet = gen.getAllFormulas();

        Assert.assertNotNull(mfSet);
        Assert.assertNotEquals(0, mfSet.size());
    }

    /**
     * Test the getFinishedPercentage() method
     */
    @Test
    public void testGetFinishedPercentage() throws Exception {

        IsotopeFactory ifac = Isotopes.getInstance();
        IIsotope c = ifac.getMajorIsotope("C");
        IIsotope h = ifac.getMajorIsotope("H");
        IIsotope n = ifac.getMajorIsotope("N");
        IIsotope o = ifac.getMajorIsotope("O");

        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(c, 0, 10);
        mfRange.addIsotope(h, 0, 10);
        mfRange.addIsotope(o, 0, 10);
        mfRange.addIsotope(n, 0, 10);

        double minMass = 100.0;
        double maxMass = 100.05;

        MolecularFormulaGenerator gen = new MolecularFormulaGenerator(builder,
                minMass, maxMass, mfRange);

        double finishedPerc, lastFinishedPerc = 0d;

        // The initial value must be 0
        finishedPerc = gen.getFinishedPercentage();
        Assert.assertEquals(0d, finishedPerc, 0.0001);

        // The value must increase after each generated formula
        while (gen.getNextFormula() != null) {
            finishedPerc = gen.getFinishedPercentage();
            Assert.assertTrue(finishedPerc > lastFinishedPerc);
            lastFinishedPerc = finishedPerc;
        }

        // The final value must be 1
        finishedPerc = gen.getFinishedPercentage();
        Assert.assertEquals(1d, finishedPerc, 0.0001);

    }

    /**
     * Test the cancel() method called from another thread. This test must
     * finish in 1000 ms.
     */
    @Test(timeout = 1000)
    public void testCancel() throws Exception {

        IsotopeFactory ifac = Isotopes.getInstance();
        IIsotope c = ifac.getMajorIsotope("C");
        IIsotope h = ifac.getMajorIsotope("H");
        IIsotope n = ifac.getMajorIsotope("N");
        IIsotope o = ifac.getMajorIsotope("O");
        IIsotope p = ifac.getMajorIsotope("P");
        IIsotope s = ifac.getMajorIsotope("S");

        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(c, 0, 1000);
        mfRange.addIsotope(h, 0, 1000);
        mfRange.addIsotope(o, 0, 1000);
        mfRange.addIsotope(n, 0, 1000);
        mfRange.addIsotope(p, 0, 1000);
        mfRange.addIsotope(s, 0, 1000);

        double minMass = 100000.0;
        double maxMass = 100000.001;

        final MolecularFormulaGenerator gen = new MolecularFormulaGenerator(
                builder, minMass, maxMass, mfRange);

        Runnable cancelThread = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                gen.cancel();
            }
        };
        new Thread(cancelThread).run();

        // We will get stuck in the next method call until the cancel thread
        // calls the cancel() method
        gen.getAllFormulas();

        // Next getNextFormula() call should return null
        IMolecularFormula f = gen.getNextFormula();
        Assert.assertNull(f);
    }

    /**
     * Test empty molecular formula range
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEmptyMFRange() throws Exception {
        new MolecularFormulaGenerator(builder, 0, 100,
                new MolecularFormulaRange());
    }

    /**
     * Test negative mass
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNegativeMass() throws Exception {

        IsotopeFactory ifac = Isotopes.getInstance();
        IIsotope c = ifac.getMajorIsotope("C");

        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(c, 0, 100);
        new MolecularFormulaGenerator(builder, -20, -10,
                new MolecularFormulaRange());
    }

    /**
     * Test if the generator respects minimal element counts
     *
     */
    @Test
    public void testMinCounts() throws Exception {

        IsotopeFactory ifac = Isotopes.getInstance();
        IIsotope c = ifac.getMajorIsotope("C");
        IIsotope h = ifac.getMajorIsotope("H");
        IIsotope n = ifac.getMajorIsotope("N");
        IIsotope o = ifac.getMajorIsotope("O");

        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(c, 5, 20);
        mfRange.addIsotope(h, 5, 20);
        mfRange.addIsotope(o, 5, 20);
        mfRange.addIsotope(n, 5, 20);

        // The minimal formula MF=C5H5O5N5 MW=215.0290682825
        double minMass = 100;
        double maxMass = 250;

        MolecularFormulaGenerator gen = new MolecularFormulaGenerator(builder,
                minMass, maxMass, mfRange);
        IMolecularFormulaSet mfSet = gen.getAllFormulas();

        // Check that all element counts in the formula are >= 5
        for (IMolecularFormula f : mfSet.molecularFormulas()) {
            for (IIsotope i : f.isotopes()) {
                int count = f.getIsotopeCount(i);
                Assert.assertTrue(count >= 5);
            }
        }

    }

    /**
     * Test if the generator respects maximal element counts
     *
     */
    @Test
    public void testMaxCounts() throws Exception {

        IsotopeFactory ifac = Isotopes.getInstance();
        IIsotope c = ifac.getMajorIsotope("C");
        IIsotope h = ifac.getMajorIsotope("H");
        IIsotope n = ifac.getMajorIsotope("N");
        IIsotope o = ifac.getMajorIsotope("O");

        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(c, 3, 7);
        mfRange.addIsotope(h, 3, 7);
        mfRange.addIsotope(o, 3, 7);
        mfRange.addIsotope(n, 3, 7);

        // The maximal formula MF=C7H7O7N7 MW=301.0406955954
        double minMass = 250;
        double maxMass = 400;

        MolecularFormulaGenerator gen = new MolecularFormulaGenerator(builder,
                minMass, maxMass, mfRange);
        IMolecularFormulaSet mfSet = gen.getAllFormulas();

        // Check that all element counts in the formula are <= 7
        for (IMolecularFormula f : mfSet.molecularFormulas()) {
            for (IIsotope i : f.isotopes()) {
                int count = f.getIsotopeCount(i);
                Assert.assertTrue(count <= 7);
            }
        }
    }

    /**
     * Test to find a single carbon.
     */
    @Test
    public void testSingleCarbon() throws Exception {

        IsotopeFactory ifac = Isotopes.getInstance();
        IIsotope c = ifac.getMajorIsotope("C");

        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(c, 0, 100);

        double minMass = 5;
        double maxMass = 15;

        MolecularFormulaGenerator gen = new MolecularFormulaGenerator(builder,
                minMass, maxMass, mfRange);
        IMolecularFormulaSet mfSet = gen.getAllFormulas();

        Assert.assertNotNull(mfSet);
        Assert.assertEquals(1, mfSet.size());
        Assert.assertEquals("C", MolecularFormulaManipulator.getString(mfSet
                .getMolecularFormula(0)));
    }

    /**
     * Test to find MF=C10000, MW=120000.0 using only carbons.
     */
    @Test
    public void testCarbons() throws Exception {

        IsotopeFactory ifac = Isotopes.getInstance();
        IIsotope c = ifac.getMajorIsotope("C");

        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(c, 0, 100000);

        double minMass = 120000.0 - 1;
        double maxMass = 120000.0 + 1;

        MolecularFormulaGenerator gen = new MolecularFormulaGenerator(builder,
                minMass, maxMass, mfRange);
        IMolecularFormulaSet mfSet = gen.getAllFormulas();

        Assert.assertNotNull(mfSet);
        Assert.assertEquals(1, mfSet.size());
        Assert.assertEquals("C10000", MolecularFormulaManipulator
                .getString(mfSet.getMolecularFormula(0)));
    }

    /**
     * Test to find H2O in a range of 1-20.
     */
    @Test
    public void testWater() throws Exception {

        IsotopeFactory ifac = Isotopes.getInstance();
        IIsotope c = ifac.getMajorIsotope("C");
        IIsotope h = ifac.getMajorIsotope("H");
        IIsotope n = ifac.getMajorIsotope("N");
        IIsotope o = ifac.getMajorIsotope("O");
        IIsotope p = ifac.getMajorIsotope("P");
        IIsotope s = ifac.getMajorIsotope("S");

        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(c, 0, 10);
        mfRange.addIsotope(h, 0, 10);
        mfRange.addIsotope(o, 0, 10);
        mfRange.addIsotope(n, 0, 10);
        mfRange.addIsotope(p, 0, 10);
        mfRange.addIsotope(s, 0, 10);

        double minMass = 1;
        double maxMass = 20;

        MolecularFormulaGenerator gen = new MolecularFormulaGenerator(builder,
                minMass, maxMass, mfRange);
        IMolecularFormulaSet mfSet = gen.getAllFormulas();

        Assert.assertNotNull(mfSet);

        boolean found = false;
        for (IMolecularFormula formula : mfSet.molecularFormulas()) {
            String mf = MolecularFormulaManipulator.getString(formula);
            if (mf.equals("H2O")) {
                found = true;
                break;
            }
        }
        Assert.assertTrue("The molecular formula H2O should be found", found);
    }

    /**
     * MolecularFormulaGenerator should use full enumeration method when smallest element has large weight
     */
    @Test
    public void testUseFullEnumerationWhenNoHydrogen() throws Exception {
        IsotopeFactory ifac = Isotopes.getInstance();
        IIsotope c = ifac.getMajorIsotope("C");
        IIsotope n = ifac.getMajorIsotope("N");
        IIsotope o = ifac.getMajorIsotope("O");

        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(c, 0, 50);
        mfRange.addIsotope(o, 0, 30);
        mfRange.addIsotope(n, 0, 10);

        MolecularFormulaGenerator generator = new MolecularFormulaGenerator(builder, 1023.000, 1023.002, mfRange);
        Assert.assertTrue("generator implementation should be instance of FullEnumerationFormulaGenerator", generator.formulaGenerator instanceof FullEnumerationFormulaGenerator);
    }

    /**
     * MolecularFormulaGenerator should use full enumeration method when the mass deviation is very large (i.e. as
     * large as the smallest weight)
     */
    @Test
    public void testUseFullEnumerationWhenSuperLargeMassDeviation() throws Exception {
        IsotopeFactory ifac = Isotopes.getInstance();
        IIsotope c = ifac.getMajorIsotope("C");
        IIsotope h = ifac.getMajorIsotope("H");
        IIsotope n = ifac.getMajorIsotope("N");
        IIsotope o = ifac.getMajorIsotope("O");

        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(c, 0, 20);
        mfRange.addIsotope(h, 0, 30);
        mfRange.addIsotope(o, 0, 15);
        mfRange.addIsotope(n, 0, 10);

        MolecularFormulaGenerator generator = new MolecularFormulaGenerator(builder, 13, 14, mfRange);
        Assert.assertTrue("generator implementation should be instance of FullEnumerationFormulaGenerator", generator.formulaGenerator instanceof FullEnumerationFormulaGenerator);
    }


    /**
     * MolecularFormulaGenerator should use full enumeration method when mass to decompose is too large to encode
     * it as 32 bit integer with default blowup factor
     */
    @Test
    public void testUseFullEnumerationWhenExceedIntegerSpace() throws Exception {
        IsotopeFactory ifac = Isotopes.getInstance();
        IIsotope c = ifac.getMajorIsotope("C");
        IIsotope h = ifac.getMajorIsotope("H");
        IIsotope n = ifac.getMajorIsotope("N");
        IIsotope o = ifac.getMajorIsotope("O");

        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(c, 0, 20);
        mfRange.addIsotope(h, 0, 30);
        mfRange.addIsotope(o, 0, 15);
        mfRange.addIsotope(n, 0, 10);

        MolecularFormulaGenerator generator = new MolecularFormulaGenerator(builder, 1300000, 1300000.1, mfRange);
        Assert.assertTrue("generator implementation should be instance of FullEnumerationFormulaGenerator", generator.formulaGenerator instanceof FullEnumerationFormulaGenerator);
    }


    /**
     * MolecularFormulaGenerator should use Round Robin when using proper input
     */
    @Test
    public void testUseRoundRobinWheneverPossible() throws Exception {
        IsotopeFactory ifac = Isotopes.getInstance();
        IIsotope c = ifac.getMajorIsotope("C");
        IIsotope h = ifac.getMajorIsotope("H");
        IIsotope n = ifac.getMajorIsotope("N");
        IIsotope o = ifac.getMajorIsotope("O");

        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(c, 0, 20);
        mfRange.addIsotope(h, 0, 30);
        mfRange.addIsotope(o, 0, 15);
        mfRange.addIsotope(n, 0, 10);

        MolecularFormulaGenerator generator = new MolecularFormulaGenerator(builder, 230.002, 230.004, mfRange);
        Assert.assertTrue("generator implementation should be instance of RoundRobinFormulaGenerator", generator.formulaGenerator instanceof RoundRobinFormulaGenerator);
    }

    /**
     * Test to find MF=C5H11N2O, MW=115.08714
     */
    @Test
    public void testSmallMass() throws Exception {

        IsotopeFactory ifac = Isotopes.getInstance();
        IIsotope c = ifac.getMajorIsotope("C");
        IIsotope h = ifac.getMajorIsotope("H");
        IIsotope n = ifac.getMajorIsotope("N");
        IIsotope o = ifac.getMajorIsotope("O");

        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(c, 0, 20);
        mfRange.addIsotope(h, 0, 30);
        mfRange.addIsotope(o, 0, 15);
        mfRange.addIsotope(n, 0, 10);

        double minMass = 115.08714 - 0.0001;
        double maxMass = 115.08714 + 0.0001;

        MolecularFormulaGenerator gen = new MolecularFormulaGenerator(builder,
                minMass, maxMass, mfRange);
        IMolecularFormulaSet mfSet = gen.getAllFormulas();

        Assert.assertNotNull(mfSet);
        Assert.assertEquals(1, mfSet.size());
        Assert.assertEquals("C5H11N2O", MolecularFormulaManipulator
                .getString(mfSet.getMolecularFormula(0)));
    }

    /**
     * Test to find pentacarboxyporphyrin, MF=C37H38N4O10 MW=698.25879
     * 
     */
    @Test
    public void testMiddleMass() throws Exception {

        IsotopeFactory ifac = Isotopes.getInstance();
        IIsotope c = ifac.getMajorIsotope("C");
        IIsotope h = ifac.getMajorIsotope("H");
        IIsotope n = ifac.getMajorIsotope("N");
        IIsotope o = ifac.getMajorIsotope("O");

        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(c, 0, 50);
        mfRange.addIsotope(h, 0, 100);
        mfRange.addIsotope(o, 0, 30);
        mfRange.addIsotope(n, 0, 10);

        double minMass = 698.25879 - 0.0001;
        double maxMass = 698.25879 + 0.0001;

        MolecularFormulaGenerator gen = new MolecularFormulaGenerator(builder,
                minMass, maxMass, mfRange);
        IMolecularFormulaSet mfSet = gen.getAllFormulas();

        Assert.assertNotNull(mfSet);
        Assert.assertEquals(1, mfSet.size());
        Assert.assertEquals("C37H38N4O10", MolecularFormulaManipulator
                .getString(mfSet.getMolecularFormula(0)));
    }

    /**
     * Test to find ubiquitin: MF=C374H623N103O116S MW=8445.573784
     *
     */
    @Test
    public void testHighMass() throws Exception {

        IsotopeFactory ifac = Isotopes.getInstance();
        IIsotope c = ifac.getMajorIsotope("C");
        IIsotope h = ifac.getMajorIsotope("H");
        IIsotope n = ifac.getMajorIsotope("N");
        IIsotope o = ifac.getMajorIsotope("O");
        IIsotope s = ifac.getMajorIsotope("S");

        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(c, 350, 400);
        mfRange.addIsotope(h, 620, 650);
        mfRange.addIsotope(o, 100, 150);
        mfRange.addIsotope(n, 100, 150);
        mfRange.addIsotope(s, 0, 10);

        double minMass = 8445.573784 - 0.00001;
        double maxMass = 8445.573784 + 0.00001;

        MolecularFormulaGenerator gen = new MolecularFormulaGenerator(builder,
                minMass, maxMass, mfRange);
        IMolecularFormulaSet mfSet = gen.getAllFormulas();

        Assert.assertNotNull(mfSet);
        Assert.assertEquals(1, mfSet.size());
        Assert.assertEquals("C374H623N103O116S", MolecularFormulaManipulator
                .getString(mfSet.getMolecularFormula(0)));


        //////////////////
    }

    /**
     * 
     *
     * Test if formula MF=C4H11NO4 MW=137.06881 is found in mass range
     * 137-137.2.
     *
     */
    @Test
    public void testFormulaFoundInRange() throws Exception {
        IsotopeFactory ifac = Isotopes.getInstance();
        IIsotope c = ifac.getMajorIsotope("C");
        IIsotope h = ifac.getMajorIsotope("H");
        IIsotope n = ifac.getMajorIsotope("N");
        IIsotope o = ifac.getMajorIsotope("O");

        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(c, 1, 50);
        mfRange.addIsotope(h, 1, 100);
        mfRange.addIsotope(o, 1, 50);
        mfRange.addIsotope(n, 1, 50);

        double minMass = 137.0;
        double maxMass = 137.2;

        MolecularFormulaGenerator gen = new MolecularFormulaGenerator(builder,
                minMass, maxMass, mfRange);
        IMolecularFormulaSet mfSet = gen.getAllFormulas();

        Assert.assertEquals(48, mfSet.size());
        boolean found = false;
        for (IMolecularFormula formula : mfSet.molecularFormulas()) {
            String mf = MolecularFormulaManipulator.getString(formula);
            if (mf.equals("C4H11NO4")) {
                found = true;
                break;
            }
        }
        Assert.assertTrue("The molecular formula C4H11NO4 should be found",
                found);
    }

    /**
     * Test if formula MF=C11H10NO2 MW=188.07115 is found in mass range 187-189.
     *
     */
    @Test
    public void testFormulaFoundInRange2() throws Exception {

        IsotopeFactory ifac = Isotopes.getInstance();
        IIsotope c = ifac.getMajorIsotope("C");
        IIsotope h = ifac.getMajorIsotope("H");
        IIsotope n = ifac.getMajorIsotope("N");
        IIsotope o = ifac.getMajorIsotope("O");

        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(c, 1, 50);
        mfRange.addIsotope(h, 1, 100);
        mfRange.addIsotope(o, 1, 50);
        mfRange.addIsotope(n, 1, 50);

        double minMass = 187;
        double maxMass = 189;

        MolecularFormulaGenerator gen = new MolecularFormulaGenerator(builder,
                minMass, maxMass, mfRange);
        IMolecularFormulaSet mfSet = gen.getAllFormulas();

        Assert.assertEquals(528, mfSet.size());
        boolean found = false;
        for (IMolecularFormula formula : mfSet.molecularFormulas()) {
            String mf = MolecularFormulaManipulator.getString(formula);
            if (mf.equals("C11H10NO2")) {
                found = true;
                break;
            }
        }
        Assert.assertTrue("The molecular formula C11H10NO2 should be found",
                found);
    }

    /**
     * Test if formula with 7 different elements is found in a narrow mass
     * range. MF=C8H9Cl3NO2PS MW=318.915719
     *
     */
    @Test
    public void testCompoundWith7Elements() throws Exception {

        IsotopeFactory ifac = Isotopes.getInstance();
        IIsotope c = ifac.getMajorIsotope("C");
        IIsotope h = ifac.getMajorIsotope("H");
        IIsotope n = ifac.getMajorIsotope("N");
        IIsotope o = ifac.getMajorIsotope("O");
        IIsotope s = ifac.getMajorIsotope("S");
        IIsotope p = ifac.getMajorIsotope("P");
        IIsotope cl = ifac.getMajorIsotope("Cl");

        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(c, 7, 9);
        mfRange.addIsotope(h, 8, 10);
        mfRange.addIsotope(o, 1, 3);
        mfRange.addIsotope(n, 0, 2);
        mfRange.addIsotope(s, 0, 2);
        mfRange.addIsotope(p, 0, 2);
        mfRange.addIsotope(cl, 2, 4);

        double minMass = 318.915719 - 0.0001;
        double maxMass = 318.915719 + 0.0001;

        MolecularFormulaGenerator gen = new MolecularFormulaGenerator(builder,
                minMass, maxMass, mfRange);
        IMolecularFormulaSet mfSet = gen.getAllFormulas();

        Assert.assertNotNull(mfSet);
        Assert.assertEquals(1, mfSet.size());
        Assert.assertEquals("C8H9Cl3NO2PS", MolecularFormulaManipulator
                .getString(mfSet.getMolecularFormula(0)));

    }

    /**
     * Test if C13 isotope-containing formula is found. MF=C(^12)3C(^13)H5
     */
    @Test
    public void testDifferentIsotopes() throws Exception {

        IsotopeFactory ifac = Isotopes.getInstance();
        IIsotope c = ifac.getMajorIsotope("C");
        IIsotope[] carbons = ifac.getIsotopes("C");
        IIsotope c13 = carbons[5]; // 13
        IIsotope h = ifac.getMajorIsotope("H");

        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(c, 0, 11);
        mfRange.addIsotope(c13, 0, 10);
        mfRange.addIsotope(h, 0, 10);

        double minMass = 54.04193 - 0.001;
        double maxMass = 54.04193 + 0.001;

        MolecularFormulaGenerator gen = new MolecularFormulaGenerator(builder,
                minMass, maxMass, mfRange);
        IMolecularFormulaSet mfSet = gen.getAllFormulas();

        Assert.assertNotNull(mfSet);
        Assert.assertEquals(1, mfSet.size());

        IMolecularFormula trueFormula = new MolecularFormula(); // C3CH5
        trueFormula.addIsotope(c, 3);
        trueFormula.addIsotope(c13, 1);
        trueFormula.addIsotope(h, 5);

        Assert.assertEquals(trueFormula.getIsotopeCount(), mfSet
                .getMolecularFormula(0).getIsotopeCount());
        Assert.assertEquals(trueFormula.getIsotopeCount(c), mfSet
                .getMolecularFormula(0).getIsotopeCount(c));
        Assert.assertEquals(trueFormula.getIsotopeCount(c13), mfSet
                .getMolecularFormula(0).getIsotopeCount(c13));

    }

    /**
     * Test if formula MF=C7H15N2O4 MW=191.10318 is found properly if we fix the
     * element counts
     */
    @Test
    public void testFixedElementCounts() throws Exception {

        IsotopeFactory ifac = Isotopes.getInstance();
        IIsotope c = ifac.getMajorIsotope("C");
        IIsotope h = ifac.getMajorIsotope("H");
        IIsotope n = ifac.getMajorIsotope("N");
        IIsotope o = ifac.getMajorIsotope("O");

        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(c, 7, 7);
        mfRange.addIsotope(h, 15, 15);
        mfRange.addIsotope(o, 4, 4);
        mfRange.addIsotope(n, 2, 2);

        double massMin = 10d;
        double massMax = 1000d;
        MolecularFormulaGenerator gen = new MolecularFormulaGenerator(builder,
                massMin, massMax, mfRange);

        IMolecularFormulaSet mfSet = gen.getAllFormulas();

        Assert.assertNotNull(mfSet);
        Assert.assertEquals(1, mfSet.size());
        Assert.assertEquals("C7H15N2O4", MolecularFormulaManipulator
                .getString(mfSet.getMolecularFormula(0)));

    }

    /**
     * Test if zero results are returned in case the target mass range is too
     * high
     */
    @Test
    public void testMassRangeTooHigh() throws Exception {

        IsotopeFactory ifac = Isotopes.getInstance();
        IIsotope c = ifac.getMajorIsotope("C");
        IIsotope h = ifac.getMajorIsotope("H");
        IIsotope n = ifac.getMajorIsotope("N");
        IIsotope o = ifac.getMajorIsotope("O");

        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(c, 0, 10);
        mfRange.addIsotope(h, 0, 10);
        mfRange.addIsotope(o, 0, 10);
        mfRange.addIsotope(n, 0, 10);

        double massMin = 1000d;
        double massMax = 2000d;
        MolecularFormulaGenerator gen = new MolecularFormulaGenerator(builder,
                massMin, massMax, mfRange);

        IMolecularFormulaSet mfSet = gen.getAllFormulas();

        Assert.assertNotNull(mfSet);
        Assert.assertEquals(0, mfSet.size());

    }

    /**
     * Test if zero results are returned in case the target mass range is too
     * low
     */
    @Test
    public void testMassRangeTooLow() throws Exception {

        IsotopeFactory ifac = Isotopes.getInstance();
        IIsotope c = ifac.getMajorIsotope("C");
        IIsotope h = ifac.getMajorIsotope("H");
        IIsotope n = ifac.getMajorIsotope("N");
        IIsotope o = ifac.getMajorIsotope("O");

        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(c, 100, 200);
        mfRange.addIsotope(h, 100, 200);
        mfRange.addIsotope(o, 100, 200);
        mfRange.addIsotope(n, 100, 200);

        double massMin = 50d;
        double massMax = 100d;
        MolecularFormulaGenerator gen = new MolecularFormulaGenerator(builder,
                massMin, massMax, mfRange);

        IMolecularFormulaSet mfSet = gen.getAllFormulas();
        Assert.assertNotNull(mfSet);
        Assert.assertEquals(0, mfSet.size());

    }

}
