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
 *
 */
package org.openscience.cdk.formula;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

/**
 * Checks the functionality of the MolecularFormulaRange.
 *
 * @cdk.module test-formula
 *
 * @see MolecularFormula
 */
public class MolecularFormulaRangeTest extends CDKTestCase {

    private final static IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();

    /**
     *  Constructor for the MolecularFormulaRangeTest object.
     *
     */
    public MolecularFormulaRangeTest() {
        super();
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testMolecularFormulaRange() {

        MolecularFormulaRange mfRange = new MolecularFormulaRange();

        Assert.assertNotNull(mfRange);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testGetIsotopeCount() {

        MolecularFormulaRange mfRange = new MolecularFormulaRange();

        Assert.assertEquals(0, mfRange.getIsotopeCount());
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testAddIsotope_IIsotope_int_int() {

        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(builder.newInstance(IIsotope.class, "C"), 0, 10);
        mfRange.addIsotope(builder.newInstance(IIsotope.class, "H"), 0, 10);

        Assert.assertEquals(2, mfRange.getIsotopeCount());
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testAddIsotope2() {

        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(builder.newInstance(IIsotope.class, "C"), 0, 10);
        mfRange.addIsotope(builder.newInstance(IIsotope.class, "H"), 0, 10);

        IIsotope hy = builder.newInstance(IIsotope.class, "C");
        hy.setNaturalAbundance(2.00342342);
        mfRange.addIsotope(hy, 0, 10);

        Assert.assertEquals(3, mfRange.getIsotopeCount());
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testGetIsotopeCountMax_IIsotope() {
        MolecularFormulaRange mfRange = new MolecularFormulaRange();

        IIsotope carb = builder.newInstance(IIsotope.class, "C");
        IIsotope h1 = builder.newInstance(IIsotope.class, "H");
        mfRange.addIsotope(carb, 0, 10);
        mfRange.addIsotope(h1, 0, 10);

        Assert.assertEquals(2, mfRange.getIsotopeCount());
        Assert.assertEquals(10, mfRange.getIsotopeCountMax(carb));
        Assert.assertEquals(10, mfRange.getIsotopeCountMax(h1));
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testGetIsotopeCountMin_IIsotope() {
        MolecularFormulaRange mfRange = new MolecularFormulaRange();

        IIsotope carb = builder.newInstance(IIsotope.class, "C");
        IIsotope h1 = builder.newInstance(IIsotope.class, "H");
        IIsotope flu = builder.newInstance(IIsotope.class, "F");
        mfRange.addIsotope(carb, 0, 10);
        mfRange.addIsotope(h1, 0, 10);

        Assert.assertEquals(2, mfRange.getIsotopeCount());
        Assert.assertEquals(0, mfRange.getIsotopeCountMin(carb));
        Assert.assertEquals(0, mfRange.getIsotopeCountMin(h1));
        Assert.assertEquals(-1, mfRange.getIsotopeCountMin(flu));
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testGetIsotopeCountMin_IIsotope2() {
        MolecularFormulaRange mfRange = new MolecularFormulaRange();

        IIsotope carb = builder.newInstance(IIsotope.class, "C");
        IIsotope h1 = builder.newInstance(IIsotope.class, "H");
        mfRange.addIsotope(carb, 0, 10);
        mfRange.addIsotope(h1, 0, 10);

        mfRange.addIsotope(carb, 5, 10);
        mfRange.addIsotope(h1, 5, 10);

        Assert.assertEquals(2, mfRange.getIsotopeCount());
        Assert.assertEquals(5, mfRange.getIsotopeCountMin(carb));
        Assert.assertEquals(5, mfRange.getIsotopeCountMin(h1));

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testGetIsotopeCountMin_IIsotope3() {
        MolecularFormulaRange mfRange = new MolecularFormulaRange();

        IIsotope carb1 = builder.newInstance(IIsotope.class, "C");
        IIsotope h1 = builder.newInstance(IIsotope.class, "H");

        IIsotope carb2 = builder.newInstance(IIsotope.class, "C");
        IIsotope h2 = builder.newInstance(IIsotope.class, "H");

        mfRange.addIsotope(carb1, 0, 10);
        mfRange.addIsotope(h1, 0, 10);

        mfRange.addIsotope(carb2, 5, 10);
        mfRange.addIsotope(h2, 5, 10);

        Assert.assertEquals(2, mfRange.getIsotopeCount());
        Assert.assertEquals(5, mfRange.getIsotopeCountMin(carb1));
        Assert.assertEquals(5, mfRange.getIsotopeCountMin(h1));
        Assert.assertEquals(5, mfRange.getIsotopeCountMin(carb2));
        Assert.assertEquals(5, mfRange.getIsotopeCountMin(h2));

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testGetIsotopeCountMin_IIsotope4() {
        MolecularFormulaRange mfRange = new MolecularFormulaRange();

        IIsotope carb1 = builder.newInstance(IIsotope.class, "C");
        IIsotope h1 = builder.newInstance(IIsotope.class, "H");

        IIsotope carb2 = builder.newInstance(IIsotope.class, "C");
        carb2.setNaturalAbundance(13.0876689);
        IIsotope h2 = builder.newInstance(IIsotope.class, "H");
        h2.setNaturalAbundance(2.0968768);

        mfRange.addIsotope(carb1, 0, 10);
        mfRange.addIsotope(h1, 0, 10);

        mfRange.addIsotope(carb2, 5, 10);
        mfRange.addIsotope(h2, 5, 10);

        Assert.assertEquals(4, mfRange.getIsotopeCount());
        Assert.assertEquals(0, mfRange.getIsotopeCountMin(carb1));
        Assert.assertEquals(0, mfRange.getIsotopeCountMin(h1));
        Assert.assertEquals(5, mfRange.getIsotopeCountMin(carb2));
        Assert.assertEquals(5, mfRange.getIsotopeCountMin(h2));

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testIsotopes() {

        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        mfRange.addIsotope(builder.newInstance(IIsotope.class, "C"), 0, 10);
        mfRange.addIsotope(builder.newInstance(IIsotope.class, "F"), 0, 10);

        Iterator<IIsotope> istoIter = mfRange.isotopes().iterator();
        int counter = 0;
        while (istoIter.hasNext()) {
            istoIter.next();
            counter++;
        }
        Assert.assertEquals(2, counter);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testContains_IIsotope() {
        MolecularFormulaRange mfRange = new MolecularFormulaRange();

        IIsotope carb = builder.newInstance(IIsotope.class, "C");
        IIsotope cl = builder.newInstance(IIsotope.class, "Cl");
        IIsotope h1 = builder.newInstance(IIsotope.class, "H");
        IIsotope h2 = builder.newInstance(IIsotope.class, "H");
        h2.setExactMass(2.0004);

        mfRange.addIsotope(carb, 0, 10);
        mfRange.addIsotope(h1, 0, 10);

        Assert.assertTrue(mfRange.contains(carb));
        Assert.assertFalse(mfRange.contains(cl));
        Assert.assertTrue(mfRange.contains(h1));
        Assert.assertFalse(mfRange.contains(h2));
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    public void testRemoveIsotope_IIsotope() {

        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        IIsotope carb = builder.newInstance(IIsotope.class, "C");
        IIsotope flu = builder.newInstance(IIsotope.class, "F");
        IIsotope h1 = builder.newInstance(IIsotope.class, "H");
        mfRange.addIsotope(carb, 0, 10);
        mfRange.addIsotope(flu, 0, 10);
        mfRange.addIsotope(h1, 0, 10);

        // remove the Fluorine
        mfRange.removeIsotope(flu);

        Assert.assertEquals(2, mfRange.getIsotopeCount());
        Assert.assertEquals(0, mfRange.getIsotopeCountMin(carb));
        Assert.assertEquals(-1, mfRange.getIsotopeCountMin(flu));

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testRemoveAllIsotopes() {
        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        IIsotope carb = builder.newInstance(IIsotope.class, "C");
        IIsotope flu = builder.newInstance(IIsotope.class, "F");
        IIsotope h1 = builder.newInstance(IIsotope.class, "H");
        mfRange.addIsotope(carb, 0, 10);
        mfRange.addIsotope(flu, 0, 10);
        mfRange.addIsotope(h1, 0, 10);

        // remove the Fluorine
        mfRange.removeAllIsotopes();

        Assert.assertEquals(0, mfRange.getIsotopeCount());
        Assert.assertEquals(-1, mfRange.getIsotopeCountMin(carb));
        Assert.assertEquals(-1, mfRange.getIsotopeCountMin(h1));
        Assert.assertEquals(-1, mfRange.getIsotopeCountMin(flu));

    }

    /**
     * A unit test suite for JUnit. Only test whether the
     * MolecularFormula are correctly cloned.
     *
     * @return    The test suite
    */
    @Test
    public void testClone() throws Exception {
        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        Object clone = mfRange.clone();
        Assert.assertTrue(clone instanceof MolecularFormulaRange);
        Assert.assertEquals(mfRange.getIsotopeCount(), ((MolecularFormulaRange) clone).getIsotopeCount());

    }

    /**
     * A unit test suite for JUnit. Only test whether
     * the MolecularFormula are correctly cloned.
    */
    @Test
    public void testClone_Isotopes() throws Exception {
        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        IIsotope carb = builder.newInstance(IIsotope.class, "C");
        IIsotope flu = builder.newInstance(IIsotope.class, "F");
        IIsotope h1 = builder.newInstance(IIsotope.class, "H");
        mfRange.addIsotope(carb, 0, 5);
        mfRange.addIsotope(flu, 2, 8);
        mfRange.addIsotope(h1, 4, 10);

        Assert.assertEquals(3, mfRange.getIsotopeCount());
        Assert.assertEquals(0, mfRange.getIsotopeCountMin(carb));
        Assert.assertEquals(2, mfRange.getIsotopeCountMin(flu));
        Assert.assertEquals(4, mfRange.getIsotopeCountMin(h1));
        Assert.assertEquals(5, mfRange.getIsotopeCountMax(carb));
        Assert.assertEquals(8, mfRange.getIsotopeCountMax(flu));
        Assert.assertEquals(10, mfRange.getIsotopeCountMax(h1));

        Object clone = mfRange.clone();
        Assert.assertTrue(clone instanceof MolecularFormulaRange);
        Assert.assertEquals(mfRange.getIsotopeCount(), ((MolecularFormulaRange) clone).getIsotopeCount());

        Assert.assertEquals(3, ((MolecularFormulaRange) clone).getIsotopeCount());

        Assert.assertEquals(3, ((MolecularFormulaRange) clone).getIsotopeCount());
        Assert.assertEquals(0, ((MolecularFormulaRange) clone).getIsotopeCountMin(carb));
        Assert.assertEquals(2, ((MolecularFormulaRange) clone).getIsotopeCountMin(flu));
        Assert.assertEquals(4, ((MolecularFormulaRange) clone).getIsotopeCountMin(h1));
        Assert.assertEquals(5, ((MolecularFormulaRange) clone).getIsotopeCountMax(carb));
        Assert.assertEquals(8, ((MolecularFormulaRange) clone).getIsotopeCountMax(flu));
        Assert.assertEquals(10, ((MolecularFormulaRange) clone).getIsotopeCountMax(h1));
    }
    
    /**
     * Test what happens when null isotope is added to MF range.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testNull() throws Exception {
        MolecularFormulaRange mfRange = new MolecularFormulaRange();
        IIsotope carb = builder.newInstance(IIsotope.class, "C");
        IIsotope nul = null;
        mfRange.addIsotope(carb, 2, 5);
        mfRange.addIsotope(nul, 3, 7);
    }
    
}
