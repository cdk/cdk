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
package org.openscience.cdk.interfaces;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

/**
 * Checks the functionality of {@link IAdductFormula} implementations.
 *
 * @cdk.module test-interfaces
 */
public abstract class AbstractAdductFormulaTest extends AbstractMolecularFormulaSetTest {

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    @Override
    public void testSize() {
        IAdductFormula mfS = getBuilder().newInstance(IAdductFormula.class);
        Assert.assertEquals(0, mfS.size());
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testAddIMolecularFormula() {
        IAdductFormula mfS = getBuilder().newInstance(IAdductFormula.class);
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));

        Assert.assertEquals(3, mfS.size());
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    @Override
    public void testAdd_IMolecularFormulaSet() {
        IAdductFormula adduct = getBuilder().newInstance(IAdductFormula.class);
        IMolecularFormulaSet mfSet = getBuilder().newInstance(IMolecularFormulaSet.class);
        mfSet.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfSet.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfSet.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        adduct.add(mfSet);

        Assert.assertEquals(3, adduct.size());
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    @Override
    public void testIterator() {
        IAdductFormula mfS = getBuilder().newInstance(IAdductFormula.class);
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));

        Assert.assertEquals(3, mfS.size());
        Iterator<IMolecularFormula> iter = mfS.molecularFormulas().iterator();
        int count = 0;
        while (iter.hasNext()) {
            iter.next();
            ++count;
            iter.remove();
        }
        Assert.assertEquals(0, mfS.size());
        Assert.assertEquals(3, count);
        Assert.assertFalse(iter.hasNext());
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    @Override
    public void testMolecularFormulas() {
        IAdductFormula mfS = getBuilder().newInstance(IAdductFormula.class);
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));

        Assert.assertEquals(3, mfS.size());
        int count = 0;
        for (IMolecularFormula formula : mfS.molecularFormulas()) {
            ++count;
            Assert.assertNotNull(formula);
        }
        Assert.assertEquals(3, count);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testAdd_IAdductFormula() {
        IAdductFormula mfS = getBuilder().newInstance(IAdductFormula.class);
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));

        IAdductFormula tested = getBuilder().newInstance(IAdductFormula.class);
        Assert.assertEquals(0, tested.size());
        tested.add(mfS);
        Assert.assertEquals(3, tested.size());
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    @Override
    public void testGetMolecularFormula_int() {
        IAdductFormula mfS = getBuilder().newInstance(IAdductFormula.class);
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));

        Assert.assertNotNull(mfS.getMolecularFormula(2)); // third molecule should exist
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    @Override
    public void testAddMolecularFormula_IMolecularFormula() {
        IAdductFormula mfS = getBuilder().newInstance(IAdductFormula.class);
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));

        Assert.assertEquals(5, mfS.size());

        // now test it to make sure it properly grows the array
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));

        Assert.assertEquals(7, mfS.size());
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testGetMolecularFormulas_int() {
        IAdductFormula mfS = getBuilder().newInstance(IAdductFormula.class);

        Assert.assertEquals(0, mfS.size());

        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));

        Assert.assertEquals(3, mfS.size());
        Assert.assertNotNull(mfS.getMolecularFormula(0));
        Assert.assertNotNull(mfS.getMolecularFormula(1));
        Assert.assertNotNull(mfS.getMolecularFormula(2));
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testContains_IIsotope() {
        IAdductFormula add = getBuilder().newInstance(IAdductFormula.class);

        IMolecularFormula mf = getBuilder().newInstance(IMolecularFormula.class);
        IIsotope carb = getBuilder().newInstance(IIsotope.class, "C");
        IIsotope h1 = getBuilder().newInstance(IIsotope.class, "H");
        IIsotope h2 = getBuilder().newInstance(IIsotope.class, "H");
        h2.setExactMass(2.00055);

        mf.addIsotope(carb);
        mf.addIsotope(h1);

        add.addMolecularFormula(mf);

        Assert.assertTrue(mf.contains(carb));
        Assert.assertTrue(mf.contains(h1));
        Assert.assertFalse(mf.contains(h2));
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    @Override
    public void testContains_IMolecularFormula() {
        IAdductFormula add = getBuilder().newInstance(IAdductFormula.class);

        IMolecularFormula mf = getBuilder().newInstance(IMolecularFormula.class);
        IIsotope carb = getBuilder().newInstance(IIsotope.class, "C");
        IIsotope h1 = getBuilder().newInstance(IIsotope.class, "H");
        IIsotope h2 = getBuilder().newInstance(IIsotope.class, "H");
        h2.setExactMass(2.00055);

        mf.addIsotope(carb);
        mf.addIsotope(h1);

        add.addMolecularFormula(mf);

        Assert.assertTrue(add.contains(mf));
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testGetCharge() {

        IAdductFormula add = getBuilder().newInstance(IAdductFormula.class);
        IMolecularFormula mf1 = getBuilder().newInstance(IMolecularFormula.class);
        mf1.setCharge(1);
        add.addMolecularFormula(mf1);

        Assert.assertEquals(1.0, add.getCharge(), 0.01);

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testSetCharge_Integer() {
        testGetCharge();

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    @Override
    public void testClone() throws Exception {
        IAdductFormula mfS = getBuilder().newInstance(IAdductFormula.class);
        Object clone = mfS.clone();
        Assert.assertTrue(clone instanceof IAdductFormula);
        Assert.assertNotSame(mfS, clone);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    @Override
    public void testRemoveMolecularFormula_IMolecularFormula() {
        IAdductFormula mfS = getBuilder().newInstance(IAdductFormula.class);
        IMolecularFormula mf1 = getBuilder().newInstance(IMolecularFormula.class);
        IMolecularFormula mf2 = getBuilder().newInstance(IMolecularFormula.class);
        mfS.addMolecularFormula(mf1);
        mfS.addMolecularFormula(mf2);
        mfS.removeMolecularFormula(mf1);
        Assert.assertEquals(1, mfS.size());
        Assert.assertEquals(mf2, mfS.getMolecularFormula(0));
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    @Override
    public void testRemoveAllMolecularFormulas() {
        IAdductFormula mfS = getBuilder().newInstance(IAdductFormula.class);
        IMolecularFormula mf1 = getBuilder().newInstance(IMolecularFormula.class);
        IMolecularFormula mf2 = getBuilder().newInstance(IMolecularFormula.class);
        mfS.addMolecularFormula(mf1);
        mfS.addMolecularFormula(mf2);

        Assert.assertEquals(2, mfS.size());
        mfS.removeAllMolecularFormulas();
        Assert.assertEquals(0, mfS.size());
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    @Override
    public void testRemoveMolecularFormula_int() {
        IAdductFormula mfS = getBuilder().newInstance(IAdductFormula.class);
        IMolecularFormula mf1 = getBuilder().newInstance(IMolecularFormula.class);
        IMolecularFormula mf2 = getBuilder().newInstance(IMolecularFormula.class);
        mfS.addMolecularFormula(mf1);
        mfS.addMolecularFormula(mf2);
        mfS.removeMolecularFormula(0);
        Assert.assertEquals(1, mfS.size());
        Assert.assertEquals(mf2, mfS.getMolecularFormula(0));
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    @Override
    public void testReplaceMolecularFormula_int_IMolecularFormula() {
        IAdductFormula mfS = getBuilder().newInstance(IAdductFormula.class);
        IMolecularFormula mf1 = getBuilder().newInstance(IMolecularFormula.class);
        IMolecularFormula mf2 = getBuilder().newInstance(IMolecularFormula.class);
        IMolecularFormula mf3 = getBuilder().newInstance(IMolecularFormula.class);
        mfS.addMolecularFormula(mf1);
        mfS.addMolecularFormula(mf2);
        Assert.assertEquals(mf2, mfS.getMolecularFormula(1));
        mfS.removeMolecularFormula(1);
        mfS.addMolecularFormula(mf3);
        Assert.assertEquals(mf3, mfS.getMolecularFormula(1));
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testGetIsotopeCount() {

        IAdductFormula add = getBuilder().newInstance(IAdductFormula.class);
        Assert.assertEquals(0, add.getIsotopeCount());

        IMolecularFormula formula = getBuilder().newInstance(IMolecularFormula.class);
        formula.addIsotope(getBuilder().newInstance(IIsotope.class, "C"));
        formula.addIsotope(getBuilder().newInstance(IIsotope.class, "H"), 4);

        add.addMolecularFormula(formula);

        Assert.assertEquals(2, add.getIsotopeCount());
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testIsotopes() {
        IAdductFormula add = getBuilder().newInstance(IAdductFormula.class);

        IMolecularFormula formula1 = getBuilder().newInstance(IMolecularFormula.class);
        formula1.addIsotope(getBuilder().newInstance(IIsotope.class, "C"));
        formula1.addIsotope(getBuilder().newInstance(IIsotope.class, "H"), 4);

        IMolecularFormula formula2 = getBuilder().newInstance(IMolecularFormula.class);
        formula2.addIsotope(getBuilder().newInstance(IIsotope.class, "F"));

        add.addMolecularFormula(formula1);
        add.addMolecularFormula(formula2);

        int count = 0;
        Iterator<IIsotope> it = add.isotopes().iterator();
        while (it.hasNext()) {
            it.next();
            ++count;
        }
        Assert.assertEquals(3, count);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testGetIsotopeCount_Sum() {

        IAdductFormula add = getBuilder().newInstance(IAdductFormula.class);
        Assert.assertEquals(0, add.getIsotopeCount());

        IMolecularFormula adduct1 = getBuilder().newInstance(IMolecularFormula.class);
        adduct1.addIsotope(getBuilder().newInstance(IIsotope.class, "C"));
        IIsotope h = getBuilder().newInstance(IIsotope.class, "H");
        adduct1.addIsotope(h, 4);
        add.addMolecularFormula(adduct1);

        IMolecularFormula formula = getBuilder().newInstance(IMolecularFormula.class);
        formula.addIsotope(h);
        add.addMolecularFormula(adduct1);

        Assert.assertEquals(2, add.getIsotopeCount());
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testGetIsotopeCount_IIsotope() {

        IAdductFormula add = getBuilder().newInstance(IAdductFormula.class);
        Assert.assertEquals(0, add.getIsotopeCount());

        IMolecularFormula formula = getBuilder().newInstance(IMolecularFormula.class);
        IIsotope C = getBuilder().newInstance(IIsotope.class, "C");
        formula.addIsotope(C);
        IIsotope h = getBuilder().newInstance(IIsotope.class, "H");
        formula.addIsotope(h, 4);

        add.addMolecularFormula(formula);

        Assert.assertEquals(2, formula.getIsotopeCount());
        Assert.assertEquals(2, add.getIsotopeCount());
        Assert.assertEquals(1, add.getIsotopeCount(C));
        Assert.assertEquals(4, add.getIsotopeCount(h));
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testGetIsotopeCount_Sum_Isotope() {

        IAdductFormula add = getBuilder().newInstance(IAdductFormula.class);
        Assert.assertEquals(0, add.getIsotopeCount());

        IMolecularFormula adduct1 = getBuilder().newInstance(IMolecularFormula.class);
        IIsotope C = getBuilder().newInstance(IIsotope.class, "C");
        adduct1.addIsotope(C);
        IIsotope h = getBuilder().newInstance(IIsotope.class, "H");
        adduct1.addIsotope(h, 4);
        add.addMolecularFormula(adduct1);

        IMolecularFormula adduct2 = getBuilder().newInstance(IMolecularFormula.class);
        adduct2.addIsotope(h);
        add.addMolecularFormula(adduct2);

        Assert.assertEquals(1, add.getIsotopeCount(C));
        Assert.assertEquals(5, add.getIsotopeCount(h));
    }

    @Test
    @Override
    public void testGetBuilder() {
        IAdductFormula add = getBuilder().newInstance(IAdductFormula.class);
        IChemObjectBuilder builder = add.getBuilder();
        Assert.assertNotNull(builder);
        Assert.assertEquals(getBuilder().getClass().getName(), builder.getClass().getName());
    }
}
