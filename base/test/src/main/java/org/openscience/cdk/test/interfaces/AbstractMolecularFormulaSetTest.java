/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.interfaces;

import java.util.Iterator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IMolecularFormulaSet;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Checks the functionality of {@link org.openscience.cdk.interfaces.IMolecularFormulaSet} implementations.
 *
 * @cdk.module test-interfaces
 */
public abstract class AbstractMolecularFormulaSetTest extends CDKTestCase {

    private static IChemObjectBuilder builder;

    public static IChemObjectBuilder getBuilder() {
        return builder;
    }

    public static void setBuilder(IChemObjectBuilder builder) {
        AbstractMolecularFormulaSetTest.builder = builder;
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testSize() {
        IMolecularFormulaSet mfS = getBuilder().newInstance(IMolecularFormulaSet.class);
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        Assertions.assertEquals(1, mfS.size());
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testAdd_IMolecularFormula() {
        IMolecularFormulaSet mfS = getBuilder().newInstance(IMolecularFormulaSet.class);
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
    public void testIterator() {
        IMolecularFormulaSet mfS = getBuilder().newInstance(IMolecularFormulaSet.class);
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
    public void testMolecularFormulas() {
        IMolecularFormulaSet mfS = getBuilder().newInstance(IMolecularFormulaSet.class);
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
    public void testAdd_IMolecularFormulaSet() {
        IMolecularFormulaSet mfS = getBuilder().newInstance(IMolecularFormulaSet.class);
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));

        IMolecularFormulaSet tested = getBuilder().newInstance(IMolecularFormulaSet.class);
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
    public void testGetMolecularFormula_int() {
        IMolecularFormulaSet mfS = getBuilder().newInstance(IMolecularFormulaSet.class);
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));

        Assertions.assertNotNull(mfS.getMolecularFormula(2)); // third molecule should exist
        //        Assert.assertNull(mfS.getMolecularFormula(3)); // fourth molecule must not exist
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testAddMolecularFormula_IMolecularFormula() {
        IMolecularFormulaSet mfS = getBuilder().newInstance(IMolecularFormulaSet.class);
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
    public void testGetMolecularFormulas() {
        IMolecularFormulaSet mfS = getBuilder().newInstance(IMolecularFormulaSet.class);

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
    public void testContains_IMolecularFormula() {
        IMolecularFormulaSet mfS = getBuilder().newInstance(IMolecularFormulaSet.class);

        IMolecularFormula mf = getBuilder().newInstance(IMolecularFormula.class);
        IIsotope carb = builder.newInstance(IIsotope.class, "C");
        IIsotope h1 = builder.newInstance(IIsotope.class, "H");
        IIsotope h2 = builder.newInstance(IIsotope.class, "H");
        h2.setExactMass(2.00055);

        mf.addIsotope(carb);
        mf.addIsotope(h1);

        mfS.addMolecularFormula(mf);

        Assertions.assertTrue(mfS.contains(mf));
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testClone() throws Exception {
        IMolecularFormulaSet mfS = getBuilder().newInstance(IMolecularFormulaSet.class);
        Object clone = mfS.clone();
        Assertions.assertTrue(clone instanceof IMolecularFormulaSet);
        Assertions.assertNotSame(mfS, clone);
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testClone_IMolecualrFormula() throws Exception {
        IMolecularFormulaSet mfS = getBuilder().newInstance(IMolecularFormulaSet.class);
        IMolecularFormula mf1 = getBuilder().newInstance(IMolecularFormula.class);
        IIsotope carb = builder.newInstance(IIsotope.class, "C");
        IIsotope flu = builder.newInstance(IIsotope.class, "F");
        IIsotope h1 = builder.newInstance(IIsotope.class, "H");
        mf1.addIsotope(carb);
        mf1.addIsotope(flu);
        mf1.addIsotope(h1, 3);
        mfS.addMolecularFormula(mf1);

        IMolecularFormula mf2 = getBuilder().newInstance(IMolecularFormula.class);
        IIsotope carb2 = builder.newInstance(IIsotope.class, "C");
        IIsotope iode = builder.newInstance(IIsotope.class, "I");
        IIsotope h2 = builder.newInstance(IIsotope.class, "H");
        mf2.addIsotope(carb2);
        mf2.addIsotope(iode, 2);
        mf2.addIsotope(h2, 2);
        mfS.addMolecularFormula(mf2);

        Object clone = mfS.clone();
        Assertions.assertTrue(clone instanceof IMolecularFormulaSet);
        Assertions.assertNotSame(mfS, clone);
        Assertions.assertEquals(mfS.size(), ((IMolecularFormulaSet) clone).size());
        Assertions.assertEquals(mfS.getMolecularFormula(0).getIsotopeCount(), ((IMolecularFormulaSet) clone)
                .getMolecularFormula(0).getIsotopeCount());
        Assertions.assertEquals(mfS.getMolecularFormula(1).getIsotopeCount(), ((IMolecularFormulaSet) clone)
                .getMolecularFormula(1).getIsotopeCount());
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testRemoveMolecularFormula_IMolecularFormula() {
        IMolecularFormulaSet mfS = getBuilder().newInstance(IMolecularFormulaSet.class);
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
    public void testRemoveAllMolecularFormulas() {
        IMolecularFormulaSet mfS = getBuilder().newInstance(IMolecularFormulaSet.class);
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
    public void testRemoveMolecularFormula_int() {
        IMolecularFormulaSet mfS = getBuilder().newInstance(IMolecularFormulaSet.class);
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
    public void testReplaceMolecularFormula_int_IMolecularFormula() {
        IMolecularFormulaSet mfS = getBuilder().newInstance(IMolecularFormulaSet.class);
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

    @Test
    public void testGetBuilder() {
        IMolecularFormulaSet add = getBuilder().newInstance(IMolecularFormulaSet.class);
        IChemObjectBuilder builder = add.getBuilder();
        Assertions.assertNotNull(builder);
        Assertions.assertEquals(getBuilder().getClass().getName(), builder.getClass().getName());
    }
}
