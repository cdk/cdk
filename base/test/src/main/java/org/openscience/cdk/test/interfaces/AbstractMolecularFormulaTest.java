/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.interfaces;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Checks the functionality of {@link org.openscience.cdk.interfaces.IMolecularFormula} implementations.
 *
 * @cdk.module test-interfaces
 */
public abstract class AbstractMolecularFormulaTest extends CDKTestCase {

    private static IChemObjectBuilder builder;

    public static IChemObjectBuilder getBuilder() {
        return builder;
    }

    public static void setBuilder(IChemObjectBuilder builder) {
        AbstractMolecularFormulaTest.builder = builder;
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testGetIsotopeCount0() {

        IMolecularFormula mf = getBuilder().newInstance(IMolecularFormula.class);

        Assertions.assertEquals(0, mf.getIsotopeCount());
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testGetIsotopeCount() {

        IMolecularFormula mf = getBuilder().newInstance(IMolecularFormula.class);
        mf.addIsotope(getBuilder().newInstance(IIsotope.class, "C"));
        mf.addIsotope(getBuilder().newInstance(IIsotope.class, "H"));
        mf.addIsotope(getBuilder().newInstance(IIsotope.class, "H"));
        mf.addIsotope(getBuilder().newInstance(IIsotope.class, "H"));
        mf.addIsotope(getBuilder().newInstance(IIsotope.class, "H"));

        Assertions.assertEquals(2, mf.getIsotopeCount());
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testAddIsotope_IIsotope() {

        IMolecularFormula mf = getBuilder().newInstance(IMolecularFormula.class);
        mf.addIsotope(getBuilder().newInstance(IIsotope.class, "C"));
        mf.addIsotope(getBuilder().newInstance(IIsotope.class, "H"));

        IIsotope hy = getBuilder().newInstance(IIsotope.class, "C");
        hy.setNaturalAbundance(2.00342342);
        mf.addIsotope(hy);

        Assertions.assertEquals(3, mf.getIsotopeCount());
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testGetIsotopeCount_IIsotope() {
        IMolecularFormula mf = getBuilder().newInstance(IMolecularFormula.class);

        IIsotope carb = getBuilder().newInstance(IIsotope.class, "C");
        IIsotope flu = getBuilder().newInstance(IIsotope.class, "F");
        IIsotope h1 = getBuilder().newInstance(IIsotope.class, "H");
        IIsotope h2 = getBuilder().newInstance(IIsotope.class, "H");
        IIsotope h3 = getBuilder().newInstance(IIsotope.class, "H");
        mf.addIsotope(carb);
        mf.addIsotope(flu);
        mf.addIsotope(h1);
        mf.addIsotope(h2);
        mf.addIsotope(h3);

        Assertions.assertEquals(3, mf.getIsotopeCount());
        Assertions.assertEquals(1, mf.getIsotopeCount(carb));
        Assertions.assertEquals(1, mf.getIsotopeCount(flu));
        Assertions.assertEquals(3, mf.getIsotopeCount(h1));
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testGetIsotopeCount_IIsotope2() {
        IMolecularFormula mf = getBuilder().newInstance(IMolecularFormula.class);

        IIsotope carb = getBuilder().newInstance(IIsotope.class, "C");
        IIsotope flu = getBuilder().newInstance(IIsotope.class, "F");
        IIsotope h1 = getBuilder().newInstance(IIsotope.class, "H");
        mf.addIsotope(carb);
        mf.addIsotope(flu);
        mf.addIsotope(h1);
        mf.addIsotope(h1);
        mf.addIsotope(h1);

        Assertions.assertEquals(3, mf.getIsotopeCount());
        Assertions.assertEquals(1, mf.getIsotopeCount(carb));
        Assertions.assertEquals(1, mf.getIsotopeCount(flu));
        Assertions.assertEquals(3, mf.getIsotopeCount(h1));
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testAddIsotope_IIsotope_int() {
        IMolecularFormula mf = getBuilder().newInstance(IMolecularFormula.class);

        IIsotope carb = getBuilder().newInstance(IIsotope.class, "C");
        IIsotope flu = getBuilder().newInstance(IIsotope.class, "F");
        IIsotope h1 = getBuilder().newInstance(IIsotope.class, "H");
        mf.addIsotope(carb);
        mf.addIsotope(flu);
        mf.addIsotope(h1, 3);

        Assertions.assertEquals(3, mf.getIsotopeCount());
        Assertions.assertEquals(1, mf.getIsotopeCount(carb));
        Assertions.assertEquals(1, mf.getIsotopeCount(flu));
        Assertions.assertEquals(3, mf.getIsotopeCount(h1));
        // In a List the objects are not stored in the same order than called
        //        Assert.assertEquals("C", mf.getIsotope(0).getSymbol());
        //        Assert.assertEquals("F", mf.getIsotope(1).getSymbol());
        //        Assert.assertEquals("H", mf.getIsotope(2).getSymbol());
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testGetIsotope_Number_Clone() throws Exception {
        IMolecularFormula mf = getBuilder().newInstance(IMolecularFormula.class);

        IIsotope carb = getBuilder().newInstance(IIsotope.class, "C");
        IIsotope flu = getBuilder().newInstance(IIsotope.class, "F");
        IIsotope h1 = getBuilder().newInstance(IIsotope.class, "H");
        mf.addIsotope(carb);
        mf.addIsotope(flu);
        mf.addIsotope(h1, 3);

        Object clone = mf.clone();
        Assertions.assertTrue(clone instanceof IMolecularFormula);

        IMolecularFormula cloneFormula = (IMolecularFormula) clone;

        Assertions.assertEquals(1, cloneFormula.getIsotopeCount(carb));
        Assertions.assertEquals(1, cloneFormula.getIsotopeCount(flu));
        Assertions.assertEquals(3, cloneFormula.getIsotopeCount(h1));
        // In a List the objects are not stored in the same order than called
        //        Assert.assertEquals("C", cloneFormula.getIsotope(0).getSymbol());
        //        Assert.assertEquals("F", cloneFormula.getIsotope(1).getSymbol());
        //        Assert.assertEquals("H", cloneFormula.getIsotope(2).getSymbol());
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testGetIsotopeCount_IIsotope_Occurr() {
        IMolecularFormula mf = getBuilder().newInstance(IMolecularFormula.class);

        IIsotope carb = getBuilder().newInstance(IIsotope.class, "C");
        IIsotope flu = getBuilder().newInstance(IIsotope.class, "F");
        IIsotope h1 = getBuilder().newInstance(IIsotope.class, "H");
        mf.addIsotope(carb);
        mf.addIsotope(flu);
        mf.addIsotope(h1, 3);

        Assertions.assertEquals(3, mf.getIsotopeCount());
        Assertions.assertEquals(1, mf.getIsotopeCount(carb));
        Assertions.assertEquals(1, mf.getIsotopeCount(flu));
        Assertions.assertEquals(3, mf.getIsotopeCount(h1));
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testAdd_IMolecularFormula() {

        IMolecularFormula acetone = getBuilder().newInstance(IMolecularFormula.class);
        acetone.addIsotope(getBuilder().newInstance(IIsotope.class, "C"), 3);
        IIsotope oxig = getBuilder().newInstance(IIsotope.class, "O");
        acetone.addIsotope(oxig);

        Assertions.assertEquals(2, acetone.getIsotopeCount());

        IMolecularFormula water = getBuilder().newInstance(IMolecularFormula.class);
        water.addIsotope(getBuilder().newInstance(IIsotope.class, "H"), 2);
        water.addIsotope(oxig);
        acetone.add(water);

        Assertions.assertEquals(3, acetone.getIsotopeCount());

    }

    @Test
    public void testMolecularFormula_NullCharge() {
        IMolecularFormula mf = getBuilder().newInstance(IMolecularFormula.class);
        IMolecularFormula mf2 = getBuilder().newInstance(IMolecularFormula.class);
        mf2.setCharge(0);
        mf.add(mf2);
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testIsotopes() {

        IMolecularFormula mf = getBuilder().newInstance(IMolecularFormula.class);
        mf.addIsotope(getBuilder().newInstance(IIsotope.class, "C"));
        mf.addIsotope(getBuilder().newInstance(IIsotope.class, "F"));
        mf.addIsotope(getBuilder().newInstance(IIsotope.class, "H"), 3);

        Iterator<IIsotope> istoIter = mf.isotopes().iterator();
        int counter = 0;
        while (istoIter.hasNext()) {
            istoIter.next();
            counter++;
        }
        Assertions.assertEquals(3, counter);
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testContains_IIsotope() {
        IMolecularFormula mf = getBuilder().newInstance(IMolecularFormula.class);

        IIsotope carb = getBuilder().newInstance(IIsotope.class, "C");
        IIsotope h1 = getBuilder().newInstance(IIsotope.class, "H");
        IIsotope h2 = getBuilder().newInstance(IIsotope.class, "H");
        h2.setExactMass(2.0004);

        mf.addIsotope(carb);
        mf.addIsotope(h1);

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
    public void testInstance_IIsotope() {

        IMolecularFormula mf = getBuilder().newInstance(IMolecularFormula.class);

        IIsotope carb = getBuilder().newInstance(IIsotope.class, "C");
        IIsotope flu = getBuilder().newInstance(IIsotope.class, "F");
        IIsotope h1 = getBuilder().newInstance(IIsotope.class, "H");
        mf.addIsotope(carb);
        mf.addIsotope(flu);
        mf.addIsotope(h1, 3);

        Iterator<IIsotope> istoIter = mf.isotopes().iterator();
        Assertions.assertNotNull(istoIter);
        Assertions.assertTrue(istoIter.hasNext());
        IIsotope next = istoIter.next();
        Assertions.assertTrue(next instanceof IIsotope);
        //        Assert.assertEquals(carb, next);

        Assertions.assertTrue(istoIter.hasNext());
        next = istoIter.next();
        Assertions.assertTrue(next instanceof IIsotope);
        //        Assert.assertEquals(flu, next);

        Assertions.assertTrue(istoIter.hasNext());
        next = istoIter.next();
        Assertions.assertTrue(next instanceof IIsotope);
        //        Assert.assertEquals(h1, next);

        Assertions.assertFalse(istoIter.hasNext());
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testGetCharge() {

        IMolecularFormula mf = getBuilder().newInstance(IMolecularFormula.class);
        mf.setCharge(1);
        mf.addIsotope(getBuilder().newInstance(IAtom.class, "C"));
        mf.addIsotope(getBuilder().newInstance(IAtom.class, "F"));
        mf.addIsotope(getBuilder().newInstance(IAtom.class, "H"), 3);

        Assertions.assertEquals(3, mf.getIsotopeCount());
        Assertions.assertEquals(1.0, mf.getCharge(), 0.001);

    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testSetCharge_Double() {

        IMolecularFormula mf = getBuilder().newInstance(IMolecularFormula.class);
        Assertions.assertEquals(CDKConstants.UNSET, mf.getCharge());

        mf.setCharge(1);
        Assertions.assertEquals(1.0, mf.getCharge(), 0.001);

        mf.add(mf);
        Assertions.assertEquals(2.0, mf.getCharge(), 0.001);
    }

    @Test
    public void testSetCharge_Integer() {

        IMolecularFormula mf = getBuilder().newInstance(IMolecularFormula.class);
        mf.setCharge(1);
        mf.addIsotope(getBuilder().newInstance(IAtom.class, "C"));
        mf.addIsotope(getBuilder().newInstance(IAtom.class, "F"));
        mf.addIsotope(getBuilder().newInstance(IAtom.class, "H"), 3);

        Assertions.assertEquals(1.0, mf.getCharge(), 0.001);

    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testCharge_rest() {

        IMolecularFormula mf = getBuilder().newInstance(IMolecularFormula.class);
        Assertions.assertEquals(CDKConstants.UNSET, mf.getCharge());

        mf.setCharge(1);
        Assertions.assertEquals(1.0, mf.getCharge(), 0.001);

        IMolecularFormula mf2 = getBuilder().newInstance(IMolecularFormula.class);
        mf2.setCharge(-1);
        mf.add(mf2);
        Assertions.assertEquals(0.0, mf.getCharge(), 0.001);
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testRemoveIsotope_IIsotope() {

        IMolecularFormula mf = getBuilder().newInstance(IMolecularFormula.class);
        IIsotope carb = getBuilder().newInstance(IIsotope.class, "C");
        IIsotope flu = getBuilder().newInstance(IIsotope.class, "F");
        IIsotope h1 = getBuilder().newInstance(IIsotope.class, "H");
        mf.addIsotope(carb);
        mf.addIsotope(flu);
        mf.addIsotope(h1, 3);

        // remove the Fluorine
        mf.removeIsotope(flu);

        Assertions.assertEquals(2, mf.getIsotopeCount());

    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testRemoveAllIsotopes() {
        IMolecularFormula mf = getBuilder().newInstance(IMolecularFormula.class);
        IIsotope carb = getBuilder().newInstance(IIsotope.class, "C");
        IIsotope flu = getBuilder().newInstance(IIsotope.class, "F");
        IIsotope h1 = getBuilder().newInstance(IIsotope.class, "H");
        mf.addIsotope(carb);
        mf.addIsotope(flu);
        mf.addIsotope(h1, 3);

        // remove the Fluorine
        mf.removeAllIsotopes();

        Assertions.assertEquals(0, mf.getIsotopeCount());

    }

    /**
     * A unit test suite for JUnit. Only test whether the
     * MolecularFormula are correctly cloned.
     *
     *
    */
    @Test
    public void testClone() throws Exception {
        IMolecularFormula mf = getBuilder().newInstance(IMolecularFormula.class);
        mf.setCharge(1);
        Object clone = mf.clone();
        Assertions.assertTrue(clone instanceof IMolecularFormula);
        Assertions.assertEquals(mf.getIsotopeCount(), ((IMolecularFormula) clone).getIsotopeCount());
        Assertions.assertEquals(mf.getCharge(), ((IMolecularFormula) clone).getCharge());

    }

    /**
     * A unit test suite for JUnit. Only test whether
     * the MolecularFormula are correctly cloned.
    */
    @Test
    public void testClone_Isotopes() throws Exception {
        IMolecularFormula mf = getBuilder().newInstance(IMolecularFormula.class);
        IIsotope carb = getBuilder().newInstance(IIsotope.class, "C");
        IIsotope flu = getBuilder().newInstance(IIsotope.class, "F");
        IIsotope h1 = getBuilder().newInstance(IIsotope.class, "H");
        mf.addIsotope(carb);
        mf.addIsotope(flu);
        mf.addIsotope(h1, 3);

        Assertions.assertEquals(3, mf.getIsotopeCount());
        Assertions.assertEquals(1, mf.getIsotopeCount(carb));
        Assertions.assertEquals(1, mf.getIsotopeCount(flu));
        Assertions.assertEquals(3, mf.getIsotopeCount(h1));

        Object clone = mf.clone();
        Assertions.assertTrue(clone instanceof IMolecularFormula);
        Assertions.assertEquals(mf.getIsotopeCount(), ((IMolecularFormula) clone).getIsotopeCount());

        Assertions.assertEquals(3, ((IMolecularFormula) clone).getIsotopeCount());
    }

    /**
     * A unit test suite for JUnit.
    */
    @Test
    public void testSetProperty_Object_Object() throws Exception {
        IMolecularFormula mf = getBuilder().newInstance(IMolecularFormula.class);
        mf.setProperty("blabla", 2);
        Assertions.assertNotNull(mf.getProperty("blabla"));
    }

    /**
     * A unit test suite for JUnit.
    */
    @Test
    public void testRemoveProperty_Object() throws Exception {
        IMolecularFormula mf = getBuilder().newInstance(IMolecularFormula.class);
        String blabla = "blabla";
        double number = 2;
        mf.setProperty(blabla, number);
        Assertions.assertNotNull(mf.getProperty(blabla));

        mf.removeProperty("blabla");
        Assertions.assertNull(mf.getProperty(blabla));

    }

    /**
     * A unit test suite for JUnit.
    */
    @Test
    public void testGetProperty_Object() throws Exception {
        testSetProperty_Object_Object();

    }

    /**
     * A unit test suite for JUnit.
    */
    @Test
    public void testGetProperties() throws Exception {
        IMolecularFormula mf = getBuilder().newInstance(IMolecularFormula.class);
        mf.setProperty("blabla", 2);
        mf.setProperty("blabla3", 3);
        Assertions.assertEquals(2, mf.getProperties().size());
    }

    /**
     * A unit test suite for JUnit.
    */
    @Test
    public void testSetProperties_Map() throws Exception {
        testGetProperties();

    }

    @Test
    public void testGetBuilder() {
        IMolecularFormula add = getBuilder().newInstance(IMolecularFormula.class);
        IChemObjectBuilder builder = add.getBuilder();
        Assertions.assertNotNull(getBuilder());
        Assertions.assertEquals(getBuilder().getClass().getName(), builder.getClass().getName());
    }
}
