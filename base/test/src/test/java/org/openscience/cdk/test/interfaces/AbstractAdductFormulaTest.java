/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.interfaces;

import java.util.Iterator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAdductFormula;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IMolecularFormulaSet;

/**
 * Checks the functionality of {@link org.openscience.cdk.interfaces.IAdductFormula} implementations.
 *
 * @cdk.module test-interfaces
 */
public abstract class AbstractAdductFormulaTest extends AbstractMolecularFormulaSetTest {

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    @Override
    public void testSize() {
        IAdductFormula mfS = getBuilder().newInstance(IAdductFormula.class);
        Assertions.assertEquals(0, mfS.size());
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testAddIMolecularFormula() {
        IAdductFormula mfS = getBuilder().newInstance(IAdductFormula.class);
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));

        Assertions.assertEquals(3, mfS.size());
    }

    /**
     * A unit test suite for JUnit.
     *
     *
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

        Assertions.assertEquals(3, adduct.size());
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    @Override
    public void testIterator() {
        IAdductFormula mfS = getBuilder().newInstance(IAdductFormula.class);
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));

        Assertions.assertEquals(3, mfS.size());
        Iterator<IMolecularFormula> iter = mfS.molecularFormulas().iterator();
        int count = 0;
        while (iter.hasNext()) {
            iter.next();
            ++count;
            iter.remove();
        }
        Assertions.assertEquals(0, mfS.size());
        Assertions.assertEquals(3, count);
        Assertions.assertFalse(iter.hasNext());
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    @Override
    public void testMolecularFormulas() {
        IAdductFormula mfS = getBuilder().newInstance(IAdductFormula.class);
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));

        Assertions.assertEquals(3, mfS.size());
        int count = 0;
        for (IMolecularFormula formula : mfS.molecularFormulas()) {
            ++count;
            Assertions.assertNotNull(formula);
        }
        Assertions.assertEquals(3, count);
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testAdd_IAdductFormula() {
        IAdductFormula mfS = getBuilder().newInstance(IAdductFormula.class);
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));

        IAdductFormula tested = getBuilder().newInstance(IAdductFormula.class);
        Assertions.assertEquals(0, tested.size());
        tested.add(mfS);
        Assertions.assertEquals(3, tested.size());
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    @Override
    public void testGetMolecularFormula_int() {
        IAdductFormula mfS = getBuilder().newInstance(IAdductFormula.class);
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));

        Assertions.assertNotNull(mfS.getMolecularFormula(2)); // third molecule should exist
    }

    /**
     * A unit test suite for JUnit.
     *
     *
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

        Assertions.assertEquals(5, mfS.size());

        // now test it to make sure it properly grows the array
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));

        Assertions.assertEquals(7, mfS.size());
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testGetMolecularFormulas_int() {
        IAdductFormula mfS = getBuilder().newInstance(IAdductFormula.class);

        Assertions.assertEquals(0, mfS.size());

        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));

        Assertions.assertEquals(3, mfS.size());
        Assertions.assertNotNull(mfS.getMolecularFormula(0));
        Assertions.assertNotNull(mfS.getMolecularFormula(1));
        Assertions.assertNotNull(mfS.getMolecularFormula(2));
    }

    /**
     * A unit test suite for JUnit.
     *
     *
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

        Assertions.assertTrue(mf.contains(carb));
        Assertions.assertTrue(mf.contains(h1));
        Assertions.assertFalse(mf.contains(h2));
    }

    /**
     * A unit test suite for JUnit.
     *
     *
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

        Assertions.assertTrue(add.contains(mf));
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testGetCharge() {

        IAdductFormula add = getBuilder().newInstance(IAdductFormula.class);
        IMolecularFormula mf1 = getBuilder().newInstance(IMolecularFormula.class);
        mf1.setCharge(1);
        add.addMolecularFormula(mf1);

        Assertions.assertEquals(1.0, add.getCharge(), 0.01);

    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testSetCharge_Integer() {
        testGetCharge();

    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    @Override
    public void testClone() throws Exception {
        IAdductFormula mfS = getBuilder().newInstance(IAdductFormula.class);
        Object clone = mfS.clone();
        Assertions.assertTrue(clone instanceof IAdductFormula);
        Assertions.assertNotSame(mfS, clone);
    }

    /**
     * A unit test suite for JUnit.
     *
     *
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
        Assertions.assertEquals(1, mfS.size());
        Assertions.assertEquals(mf2, mfS.getMolecularFormula(0));
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    @Override
    public void testRemoveAllMolecularFormulas() {
        IAdductFormula mfS = getBuilder().newInstance(IAdductFormula.class);
        IMolecularFormula mf1 = getBuilder().newInstance(IMolecularFormula.class);
        IMolecularFormula mf2 = getBuilder().newInstance(IMolecularFormula.class);
        mfS.addMolecularFormula(mf1);
        mfS.addMolecularFormula(mf2);

        Assertions.assertEquals(2, mfS.size());
        mfS.removeAllMolecularFormulas();
        Assertions.assertEquals(0, mfS.size());
    }

    /**
     * A unit test suite for JUnit.
     *
     *
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
        Assertions.assertEquals(1, mfS.size());
        Assertions.assertEquals(mf2, mfS.getMolecularFormula(0));
    }

    /**
     * A unit test suite for JUnit.
     *
     *
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
        Assertions.assertEquals(mf2, mfS.getMolecularFormula(1));
        mfS.removeMolecularFormula(1);
        mfS.addMolecularFormula(mf3);
        Assertions.assertEquals(mf3, mfS.getMolecularFormula(1));
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testGetIsotopeCount() {

        IAdductFormula add = getBuilder().newInstance(IAdductFormula.class);
        Assertions.assertEquals(0, add.getIsotopeCount());

        IMolecularFormula formula = getBuilder().newInstance(IMolecularFormula.class);
        formula.addIsotope(getBuilder().newInstance(IIsotope.class, "C"));
        formula.addIsotope(getBuilder().newInstance(IIsotope.class, "H"), 4);

        add.addMolecularFormula(formula);

        Assertions.assertEquals(2, add.getIsotopeCount());
    }

    /**
     * A unit test suite for JUnit.
     *
     *
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
        for (IIsotope iIsotope : add.isotopes()) {
            ++count;
        }
        Assertions.assertEquals(3, count);
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testGetIsotopeCount_Sum() {

        IAdductFormula add = getBuilder().newInstance(IAdductFormula.class);
        Assertions.assertEquals(0, add.getIsotopeCount());

        IMolecularFormula adduct1 = getBuilder().newInstance(IMolecularFormula.class);
        adduct1.addIsotope(getBuilder().newInstance(IIsotope.class, "C"));
        IIsotope h = getBuilder().newInstance(IIsotope.class, "H");
        adduct1.addIsotope(h, 4);
        add.addMolecularFormula(adduct1);

        IMolecularFormula formula = getBuilder().newInstance(IMolecularFormula.class);
        formula.addIsotope(h);
        add.addMolecularFormula(adduct1);

        Assertions.assertEquals(2, add.getIsotopeCount());
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testGetIsotopeCount_IIsotope() {

        IAdductFormula add = getBuilder().newInstance(IAdductFormula.class);
        Assertions.assertEquals(0, add.getIsotopeCount());

        IMolecularFormula formula = getBuilder().newInstance(IMolecularFormula.class);
        IIsotope C = getBuilder().newInstance(IIsotope.class, "C");
        formula.addIsotope(C);
        IIsotope h = getBuilder().newInstance(IIsotope.class, "H");
        formula.addIsotope(h, 4);

        add.addMolecularFormula(formula);

        Assertions.assertEquals(2, formula.getIsotopeCount());
        Assertions.assertEquals(2, add.getIsotopeCount());
        Assertions.assertEquals(1, add.getIsotopeCount(C));
        Assertions.assertEquals(4, add.getIsotopeCount(h));
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testGetIsotopeCount_Sum_Isotope() {

        IAdductFormula add = getBuilder().newInstance(IAdductFormula.class);
        Assertions.assertEquals(0, add.getIsotopeCount());

        IMolecularFormula adduct1 = getBuilder().newInstance(IMolecularFormula.class);
        IIsotope C = getBuilder().newInstance(IIsotope.class, "C");
        adduct1.addIsotope(C);
        IIsotope h = getBuilder().newInstance(IIsotope.class, "H");
        adduct1.addIsotope(h, 4);
        add.addMolecularFormula(adduct1);

        IMolecularFormula adduct2 = getBuilder().newInstance(IMolecularFormula.class);
        adduct2.addIsotope(h);
        add.addMolecularFormula(adduct2);

        Assertions.assertEquals(1, add.getIsotopeCount(C));
        Assertions.assertEquals(5, add.getIsotopeCount(h));
    }

    @Test
    @Override
    public void testGetBuilder() {
        IAdductFormula add = getBuilder().newInstance(IAdductFormula.class);
        IChemObjectBuilder builder = add.getBuilder();
        Assertions.assertNotNull(builder);
        Assertions.assertEquals(getBuilder().getClass().getName(), builder.getClass().getName());
    }
}
