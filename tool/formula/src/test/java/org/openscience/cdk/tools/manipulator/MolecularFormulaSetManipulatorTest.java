/* Copyright (C) 2007  Miguel Rojasch <miguelrojasch@users.sf.net>
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
package org.openscience.cdk.tools.manipulator;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.formula.MolecularFormula;
import org.openscience.cdk.formula.MolecularFormulaRange;
import org.openscience.cdk.formula.MolecularFormulaSet;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IMolecularFormulaSet;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

/**
 * Checks the functionality of the MolecularFormulaSetManipulator.
 *
 * @cdk.module test-formula
 */
public class MolecularFormulaSetManipulatorTest extends CDKTestCase {

    private final static IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();

    /**
     *  Constructor for the MolecularFormulaSetManipulatorTest object.
     *
     */
    public MolecularFormulaSetManipulatorTest() {

        super();
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testGetMaxOccurrenceElements_IMolecularFormulaSet() {
        IMolecularFormula mf1 = new MolecularFormula(); /* C4H12NO4 */
        mf1.addIsotope(builder.newInstance(IIsotope.class, "C"), 4);
        mf1.addIsotope(builder.newInstance(IIsotope.class, "H"), 12);
        mf1.addIsotope(builder.newInstance(IIsotope.class, "N"), 1);
        mf1.addIsotope(builder.newInstance(IIsotope.class, "O"), 4);

        IMolecularFormula mf2 = new MolecularFormula(); /* C7H20N4O2 */
        mf2.addIsotope(builder.newInstance(IIsotope.class, "C"), 7);
        mf2.addIsotope(builder.newInstance(IIsotope.class, "H"), 20);
        mf2.addIsotope(builder.newInstance(IIsotope.class, "N"), 4);
        mf2.addIsotope(builder.newInstance(IIsotope.class, "O"), 2);

        IMolecularFormula mf3 = new MolecularFormula(); /* C9H5O7 */
        mf3.addIsotope(builder.newInstance(IIsotope.class, "C"), 9);
        mf3.addIsotope(builder.newInstance(IIsotope.class, "H"), 5);
        mf3.addIsotope(builder.newInstance(IIsotope.class, "O"), 7);

        IMolecularFormulaSet mfSet = new MolecularFormulaSet();
        mfSet.addMolecularFormula(mf1);
        mfSet.addMolecularFormula(mf2);
        mfSet.addMolecularFormula(mf3);

        IMolecularFormula molecularFormula = MolecularFormulaSetManipulator.getMaxOccurrenceElements(mfSet);

        /* Result: C9H20N4O7 */

        Assert.assertEquals(40, MolecularFormulaManipulator.getAtomCount(molecularFormula));
        Assert.assertEquals(4, molecularFormula.getIsotopeCount());
        Assert.assertEquals(9, molecularFormula.getIsotopeCount(builder.newInstance(IIsotope.class, "C")));
        Assert.assertEquals(20, molecularFormula.getIsotopeCount(builder.newInstance(IIsotope.class, "H")));
        Assert.assertEquals(4, molecularFormula.getIsotopeCount(builder.newInstance(IIsotope.class, "N")));
        Assert.assertEquals(7, molecularFormula.getIsotopeCount(builder.newInstance(IIsotope.class, "O")));

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testGetMinOccurrenceElements_IMolecularFormulaSet() {
        IMolecularFormula mf1 = new MolecularFormula(); /* C4H12NO4 */
        mf1.addIsotope(builder.newInstance(IIsotope.class, "C"), 4);
        mf1.addIsotope(builder.newInstance(IIsotope.class, "H"), 12);
        mf1.addIsotope(builder.newInstance(IIsotope.class, "N"), 1);
        mf1.addIsotope(builder.newInstance(IIsotope.class, "O"), 4);

        IMolecularFormula mf2 = new MolecularFormula(); /* C7H20N4O2 */
        mf2.addIsotope(builder.newInstance(IIsotope.class, "C"), 7);
        mf2.addIsotope(builder.newInstance(IIsotope.class, "H"), 20);
        mf2.addIsotope(builder.newInstance(IIsotope.class, "N"), 4);
        mf2.addIsotope(builder.newInstance(IIsotope.class, "O"), 2);

        IMolecularFormula mf3 = new MolecularFormula(); /* C9H5O7 */
        mf3.addIsotope(builder.newInstance(IIsotope.class, "C"), 9);
        mf3.addIsotope(builder.newInstance(IIsotope.class, "H"), 5);
        mf3.addIsotope(builder.newInstance(IIsotope.class, "O"), 7);

        IMolecularFormulaSet mfSet = new MolecularFormulaSet();
        mfSet.addMolecularFormula(mf1);
        mfSet.addMolecularFormula(mf2);
        mfSet.addMolecularFormula(mf3);

        IMolecularFormula molecularFormula = MolecularFormulaSetManipulator.getMinOccurrenceElements(mfSet);

        /* Result: C4H5NO2 */

        Assert.assertEquals(12, MolecularFormulaManipulator.getAtomCount(molecularFormula));
        Assert.assertEquals(4, molecularFormula.getIsotopeCount());
        Assert.assertEquals(4, molecularFormula.getIsotopeCount(builder.newInstance(IIsotope.class, "C")));
        Assert.assertEquals(5, molecularFormula.getIsotopeCount(builder.newInstance(IIsotope.class, "H")));
        Assert.assertEquals(1, molecularFormula.getIsotopeCount(builder.newInstance(IIsotope.class, "N")));
        Assert.assertEquals(2, molecularFormula.getIsotopeCount(builder.newInstance(IIsotope.class, "O")));

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testRemove_IMolecularFormulaSet_IMolecularFormula_IMolecularFormula() {

        IMolecularFormula formulaMin = new MolecularFormula();
        formulaMin.addIsotope(builder.newInstance(IIsotope.class, "C"), 1);
        formulaMin.addIsotope(builder.newInstance(IIsotope.class, "H"), 1);
        formulaMin.addIsotope(builder.newInstance(IIsotope.class, "O"), 1);

        IMolecularFormula formulaMax = new MolecularFormula();
        formulaMax.addIsotope(builder.newInstance(IIsotope.class, "C"), 4);
        formulaMax.addIsotope(builder.newInstance(IIsotope.class, "H"), 12);
        formulaMax.addIsotope(builder.newInstance(IIsotope.class, "N"), 2);

        IMolecularFormula mf1 = new MolecularFormula();
        mf1.addIsotope(builder.newInstance(IIsotope.class, "C"), 3);
        mf1.addIsotope(builder.newInstance(IIsotope.class, "H"), 10);
        mf1.addIsotope(builder.newInstance(IIsotope.class, "N"), 1);

        IMolecularFormulaSet formulaSet = new MolecularFormulaSet();
        formulaSet.addMolecularFormula(mf1);

        IMolecularFormulaSet newMFSet = MolecularFormulaSetManipulator.remove(formulaSet, formulaMin, formulaMax);

        Assert.assertNull(newMFSet);

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testRemove_1() {

        IMolecularFormula formulaMin = new MolecularFormula();
        formulaMin.addIsotope(builder.newInstance(IIsotope.class, "C"), 1);
        formulaMin.addIsotope(builder.newInstance(IIsotope.class, "H"), 1);
        formulaMin.addIsotope(builder.newInstance(IIsotope.class, "N"), 1);

        IMolecularFormula formulaMax = new MolecularFormula();
        formulaMax.addIsotope(builder.newInstance(IIsotope.class, "C"), 4);
        formulaMax.addIsotope(builder.newInstance(IIsotope.class, "H"), 12);
        formulaMax.addIsotope(builder.newInstance(IIsotope.class, "N"), 2);

        IMolecularFormula mf1 = new MolecularFormula();
        mf1.addIsotope(builder.newInstance(IIsotope.class, "C"), 3);
        mf1.addIsotope(builder.newInstance(IIsotope.class, "H"), 10);
        mf1.addIsotope(builder.newInstance(IIsotope.class, "N"), 1);

        IMolecularFormula mf2 = new MolecularFormula();
        mf2.addIsotope(builder.newInstance(IIsotope.class, "C"), 1);
        mf2.addIsotope(builder.newInstance(IIsotope.class, "H"), 1);
        mf2.addIsotope(builder.newInstance(IIsotope.class, "N"), 1);

        IMolecularFormula mf3 = new MolecularFormula();
        mf3.addIsotope(builder.newInstance(IIsotope.class, "C"), 4);
        mf3.addIsotope(builder.newInstance(IIsotope.class, "H"), 12);
        mf3.addIsotope(builder.newInstance(IIsotope.class, "N"), 2);

        IMolecularFormula mf4 = new MolecularFormula();
        mf4.addIsotope(builder.newInstance(IIsotope.class, "C"), 7);
        mf4.addIsotope(builder.newInstance(IIsotope.class, "H"), 10);
        mf4.addIsotope(builder.newInstance(IIsotope.class, "N"), 1);

        IMolecularFormulaSet formulaSet = new MolecularFormulaSet();
        formulaSet.addMolecularFormula(mf1);
        formulaSet.addMolecularFormula(mf2);
        formulaSet.addMolecularFormula(mf3);
        formulaSet.addMolecularFormula(mf4);

        IMolecularFormulaSet newMFSet = MolecularFormulaSetManipulator.remove(formulaSet, formulaMin, formulaMax);
        /* the mf4 is excluded from the limits */

        Assert.assertEquals(3, newMFSet.size());
        Assert.assertEquals(MolecularFormulaManipulator.getString(mf1),
                MolecularFormulaManipulator.getString(newMFSet.getMolecularFormula(0)));
        Assert.assertEquals(MolecularFormulaManipulator.getString(mf2),
                MolecularFormulaManipulator.getString(newMFSet.getMolecularFormula(1)));
        Assert.assertEquals(MolecularFormulaManipulator.getString(mf3),
                MolecularFormulaManipulator.getString(newMFSet.getMolecularFormula(2)));

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testRemove_2() {

        IMolecularFormula formulaMin = new MolecularFormula();
        formulaMin.addIsotope(builder.newInstance(IIsotope.class, "C"), 1);
        formulaMin.addIsotope(builder.newInstance(IIsotope.class, "H"), 1);
        formulaMin.addIsotope(builder.newInstance(IIsotope.class, "N"), 1);

        IMolecularFormula formulaMax = new MolecularFormula();
        formulaMax.addIsotope(builder.newInstance(IIsotope.class, "C"), 4);
        formulaMax.addIsotope(builder.newInstance(IIsotope.class, "H"), 12);
        formulaMax.addIsotope(builder.newInstance(IIsotope.class, "N"), 2);

        IMolecularFormula mf1 = new MolecularFormula();
        mf1.addIsotope(builder.newInstance(IIsotope.class, "C"), 3);
        mf1.addIsotope(builder.newInstance(IIsotope.class, "H"), 10);
        mf1.addIsotope(builder.newInstance(IIsotope.class, "N"), 1);

        IMolecularFormula mf2 = new MolecularFormula();
        mf2.addIsotope(builder.newInstance(IIsotope.class, "C"), 1);
        mf2.addIsotope(builder.newInstance(IIsotope.class, "H"), 1);

        IMolecularFormulaSet formulaSet = new MolecularFormulaSet();
        formulaSet.addMolecularFormula(mf1);
        formulaSet.addMolecularFormula(mf2);

        IMolecularFormulaSet newMFSet = MolecularFormulaSetManipulator.remove(formulaSet, formulaMin, formulaMax);
        /* the mf2 is excluded from the limits. It doesn't contain N */

        Assert.assertEquals(1, newMFSet.size());
        Assert.assertEquals(MolecularFormulaManipulator.getString(mf1),
                MolecularFormulaManipulator.getString(newMFSet.getMolecularFormula(0)));

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testRemove_3() {

        IMolecularFormula formulaMin = new MolecularFormula();
        formulaMin.addIsotope(builder.newInstance(IIsotope.class, "C"), 1);
        formulaMin.addIsotope(builder.newInstance(IIsotope.class, "H"), 1);
        formulaMin.addIsotope(builder.newInstance(IIsotope.class, "N"), 1);

        IMolecularFormula formulaMax = new MolecularFormula();
        formulaMax.addIsotope(builder.newInstance(IIsotope.class, "C"), 4);
        formulaMax.addIsotope(builder.newInstance(IIsotope.class, "H"), 12);
        formulaMax.addIsotope(builder.newInstance(IIsotope.class, "N"), 2);

        IMolecularFormula mf1 = new MolecularFormula();
        mf1.addIsotope(builder.newInstance(IIsotope.class, "C"), 3);
        mf1.addIsotope(builder.newInstance(IIsotope.class, "H"), 10);
        mf1.addIsotope(builder.newInstance(IIsotope.class, "N"), 1);

        IMolecularFormula mf2 = new MolecularFormula();
        mf2.addIsotope(builder.newInstance(IIsotope.class, "C"), 1);
        mf2.addIsotope(builder.newInstance(IIsotope.class, "H"), 1);
        mf2.addIsotope(builder.newInstance(IIsotope.class, "O"), 1);

        IMolecularFormulaSet formulaSet = new MolecularFormulaSet();
        formulaSet.addMolecularFormula(mf1);
        formulaSet.addMolecularFormula(mf2);

        IMolecularFormulaSet newMFSet = MolecularFormulaSetManipulator.remove(formulaSet, formulaMin, formulaMax);
        /* the mf2 is excluded from the limits. It doesn't contain N */

        Assert.assertEquals(1, newMFSet.size());
        Assert.assertEquals(MolecularFormulaManipulator.getString(mf1),
                MolecularFormulaManipulator.getString(newMFSet.getMolecularFormula(0)));

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testRemove_IMolecularFormulaSet_MolecularFormulaRange() {

        MolecularFormulaRange formulaRange = new MolecularFormulaRange();
        formulaRange.addIsotope(builder.newInstance(IIsotope.class, "C"), 0, 4);
        formulaRange.addIsotope(builder.newInstance(IIsotope.class, "H"), 0, 12);
        formulaRange.addIsotope(builder.newInstance(IIsotope.class, "N"), 0, 2);

        IMolecularFormula mf2 = new MolecularFormula();
        mf2.addIsotope(builder.newInstance(IIsotope.class, "C"), 1);
        mf2.addIsotope(builder.newInstance(IIsotope.class, "H"), 11);
        mf2.addIsotope(builder.newInstance(IIsotope.class, "N"), 1);

        IMolecularFormula mf1 = new MolecularFormula();
        mf1.addIsotope(builder.newInstance(IIsotope.class, "C"), 3);
        mf1.addIsotope(builder.newInstance(IIsotope.class, "H"), 10);

        IMolecularFormulaSet formulaSet = new MolecularFormulaSet();
        formulaSet.addMolecularFormula(mf1);
        formulaSet.addMolecularFormula(mf2);

        IMolecularFormulaSet newMFSet = MolecularFormulaSetManipulator.remove(formulaSet, formulaRange);
        /* the mf2 is excluded from the limits. It doesn't contain N */

        Assert.assertEquals(2, newMFSet.size());

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testContains_IMolecularFormulaSet_IMolecularFormula() {
        IMolecularFormula mf1 = new MolecularFormula();
        mf1.addIsotope(builder.newInstance(IIsotope.class, "C"), 4);
        mf1.addIsotope(builder.newInstance(IIsotope.class, "H"), 12);
        mf1.addIsotope(builder.newInstance(IIsotope.class, "N"), 1);
        mf1.addIsotope(builder.newInstance(IIsotope.class, "O"), 4);

        IMolecularFormula mf3 = new MolecularFormula();
        mf3.addIsotope(builder.newInstance(IIsotope.class, "C"), 9);
        mf3.addIsotope(builder.newInstance(IIsotope.class, "H"), 5);
        mf3.addIsotope(builder.newInstance(IIsotope.class, "O"), 7);

        IMolecularFormulaSet formulaSet = new MolecularFormulaSet();
        formulaSet.addMolecularFormula(mf1);
        formulaSet.addMolecularFormula(mf3);

        IMolecularFormula mf2 = new MolecularFormula();
        mf2.addIsotope(builder.newInstance(IIsotope.class, "C"), 4);
        mf2.addIsotope(builder.newInstance(IIsotope.class, "H"), 12);
        mf2.addIsotope(builder.newInstance(IIsotope.class, "N"), 1);
        mf2.addIsotope(builder.newInstance(IIsotope.class, "O"), 4);

        IMolecularFormula mf4 = new MolecularFormula();
        mf4.addIsotope(builder.newInstance(IIsotope.class, "C"), 4);
        IIsotope hyd = builder.newInstance(IIsotope.class, "H");
        hyd.setExactMass(2.0032342);
        mf4.addIsotope(hyd, 12);
        mf4.addIsotope(builder.newInstance(IIsotope.class, "N"), 1);
        mf4.addIsotope(builder.newInstance(IIsotope.class, "O"), 4);

        Assert.assertTrue(MolecularFormulaSetManipulator.contains(formulaSet, mf2));
        Assert.assertFalse(MolecularFormulaSetManipulator.contains(formulaSet, mf4));

    }

}
