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
import org.openscience.cdk.CDKTestCase;

/**
 * Checks the functionality of {@link IMolecularFormulaSet} implementations.
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
     * @return    The test suite
     */
    @Test
    public void testSize() {
        IMolecularFormulaSet mfS = getBuilder().newInstance(IMolecularFormulaSet.class);
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        Assert.assertEquals(1, mfS.size());
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testAdd_IMolecularFormula() {
        IMolecularFormulaSet mfS = getBuilder().newInstance(IMolecularFormulaSet.class);
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
    public void testIterator() {
        IMolecularFormulaSet mfS = getBuilder().newInstance(IMolecularFormulaSet.class);
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
    public void testMolecularFormulas() {
        IMolecularFormulaSet mfS = getBuilder().newInstance(IMolecularFormulaSet.class);
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
    public void testAdd_IMolecularFormulaSet() {
        IMolecularFormulaSet mfS = getBuilder().newInstance(IMolecularFormulaSet.class);
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));

        IMolecularFormulaSet tested = getBuilder().newInstance(IMolecularFormulaSet.class);
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
    public void testGetMolecularFormula_int() {
        IMolecularFormulaSet mfS = getBuilder().newInstance(IMolecularFormulaSet.class);
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));
        mfS.addMolecularFormula(getBuilder().newInstance(IMolecularFormula.class));

        Assert.assertNotNull(mfS.getMolecularFormula(2)); // third molecule should exist
        //        Assert.assertNull(mfS.getMolecularFormula(3)); // fourth molecule must not exist
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testAddMolecularFormula_IMolecularFormula() {
        IMolecularFormulaSet mfS = getBuilder().newInstance(IMolecularFormulaSet.class);
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
    public void testGetMolecularFormulas() {
        IMolecularFormulaSet mfS = getBuilder().newInstance(IMolecularFormulaSet.class);

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

        Assert.assertTrue(mfS.contains(mf));
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testClone() throws Exception {
        IMolecularFormulaSet mfS = getBuilder().newInstance(IMolecularFormulaSet.class);
        Object clone = mfS.clone();
        Assert.assertTrue(clone instanceof IMolecularFormulaSet);
        Assert.assertNotSame(mfS, clone);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
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
        Assert.assertTrue(clone instanceof IMolecularFormulaSet);
        Assert.assertNotSame(mfS, clone);
        Assert.assertEquals(mfS.size(), ((IMolecularFormulaSet) clone).size());
        Assert.assertEquals(mfS.getMolecularFormula(0).getIsotopeCount(), ((IMolecularFormulaSet) clone)
                .getMolecularFormula(0).getIsotopeCount());
        Assert.assertEquals(mfS.getMolecularFormula(1).getIsotopeCount(), ((IMolecularFormulaSet) clone)
                .getMolecularFormula(1).getIsotopeCount());
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testRemoveMolecularFormula_IMolecularFormula() {
        IMolecularFormulaSet mfS = getBuilder().newInstance(IMolecularFormulaSet.class);
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
    public void testRemoveAllMolecularFormulas() {
        IMolecularFormulaSet mfS = getBuilder().newInstance(IMolecularFormulaSet.class);
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
    public void testRemoveMolecularFormula_int() {
        IMolecularFormulaSet mfS = getBuilder().newInstance(IMolecularFormulaSet.class);
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
    public void testReplaceMolecularFormula_int_IMolecularFormula() {
        IMolecularFormulaSet mfS = getBuilder().newInstance(IMolecularFormulaSet.class);
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

    @Test
    public void testGetBuilder() {
        IMolecularFormulaSet add = getBuilder().newInstance(IMolecularFormulaSet.class);
        IChemObjectBuilder builder = add.getBuilder();
        Assert.assertNotNull(builder);
        Assert.assertEquals(getBuilder().getClass().getName(), builder.getClass().getName());
    }
}
