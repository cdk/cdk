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
package org.openscience.cdk.formula.rules;

import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

/**
 * @cdk.module test-formula
 */
public class RDBERuleTest extends FormulaRuleTest {

    private static IChemObjectBuilder builder;

    /**
    *  The JUnit setup method
    */
    @BeforeClass
    public static void setUp() throws Exception {
        builder = DefaultChemObjectBuilder.getInstance();
        setRule(ChargeRule.class);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testRDBERule() throws ClassNotFoundException, CDKException, Exception {

        IRule rule = new RDBERule();
        Assert.assertNotNull(rule);

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testDefault() throws ClassNotFoundException, CDKException, Exception {

        IRule rule = new RDBERule();
        Object[] objects = rule.getParameters();
        Assert.assertEquals(2, objects.length);

        double min = (Double) objects[0];
        double max = (Double) objects[1];
        Assert.assertEquals(-0.5, min, 0.00001);
        Assert.assertEquals(30, max, 0.00001);

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testSetParameters() throws ClassNotFoundException, CDKException, Exception {

        IRule rule = new RDBERule();
        Object[] params = new Object[2];

        params[0] = 0.0;
        params[1] = 10.0;
        rule.setParameters(params);

        Object[] objects = rule.getParameters();
        Assert.assertEquals(2, objects.length);

        double min = (Double) objects[0];
        double max = (Double) objects[1];
        Assert.assertEquals(0.0, min, 0.00001);
        Assert.assertEquals(10.0, max, 0.00001);

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testDefaultValidFalse() throws ClassNotFoundException, CDKException, Exception {

        IRule rule = new RDBERule();

        IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("C2H4", builder);

        Assert.assertEquals(1.0, rule.validate(formula), 0.0001);
    }

    /**
     * A unit test suite for JUnit.C2H11N4O4
     *
     * @return    The test suite
     */
    @Test
    public void testDefaultValidFalse_SetParam() throws ClassNotFoundException, CDKException, Exception {

        RDBERule rule = new RDBERule();

        IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("CH2F10S2", builder);

        List<Double> value = rule.getRDBEValue(formula);
        Assert.assertEquals(6, value.size(), 0.0001);
        Assert.assertEquals(-4.0, value.get(0), 0.0001);
        Assert.assertEquals(-3.0, value.get(1), 0.0001);
        Assert.assertEquals(-2.0, value.get(2), 0.0001);
        Assert.assertEquals(-2.0, value.get(3), 0.0001);
        Assert.assertEquals(-1.0, value.get(4), 0.0001);
        Assert.assertEquals(0.0, value.get(5), 0.0001);

        Assert.assertEquals(1.0, rule.validate(formula), 0.0001);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testDefaultValidTrue() throws ClassNotFoundException, CDKException, Exception {

        IRule rule = new RDBERule();

        IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("C1H4", builder);
        formula.setCharge(0);

        Assert.assertEquals(1.0, rule.validate(formula), 0.0001);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testGetRDBEValue_IMolecularFormula() throws ClassNotFoundException, CDKException, Exception {

        RDBERule rule = new RDBERule();

        IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("C2H4", builder);

        Assert.assertEquals(1.0, rule.getRDBEValue(formula).get(0), 0.0001);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testValidate_IMolecularFormula_double() throws ClassNotFoundException, CDKException, Exception {

        RDBERule rule = new RDBERule();

        IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("C2H4", builder);

        Assert.assertTrue(rule.validate(formula, 2.0));
    }

    /**
     * A unit test suite for JUnit.C3H8O3S2
     *
     * @return    The test suite
     */
    @Test
    public void test1() throws ClassNotFoundException, CDKException, Exception {

        RDBERule rule = new RDBERule();

        IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("C3H8O3S2", builder);

        List<Double> value = rule.getRDBEValue(formula);
        Assert.assertEquals(6, value.size(), 0.0001);
        Assert.assertEquals(0.0, value.get(0), 0.0001);
        Assert.assertEquals(1.0, rule.validate(formula), 0.0001);
    }

    /**
     * A unit test suite for JUnit.C4H8O3S1
     *
     * @return    The test suite
     */
    @Test
    public void test2() throws ClassNotFoundException, CDKException, Exception {

        RDBERule rule = new RDBERule();

        IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("C4H8O3S1", builder);

        Assert.assertEquals(1.0, rule.validate(formula), 0.0001);
    }

    /**
     * A unit test suite for JUnit.NH4+
     *
     * @return    The test suite
     */
    @Test
    public void testAnticipatedIonState_1() throws ClassNotFoundException, CDKException, Exception {

        RDBERule rule = new RDBERule();

        IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("NH4", builder);
        formula.setCharge(1);

        Assert.assertEquals(1.0, rule.validate(formula), 0.0001);
    }

    /**
     * A unit test suite for JUnit.NH4+
     *
     * @return    The test suite
     */
    @Test
    public void testAnticipatedIonState_2() throws ClassNotFoundException, CDKException, Exception {

        RDBERule rule = new RDBERule();

        IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("NH4", builder);

        Assert.assertEquals(0.0, rule.validate(formula), 0.0001);
    }

    /**
     * A unit test suite for JUnit for lipid PC.
     *
     * @cdk.bug 2322906
     *
     * @return    The test suite
     */
    @Test
    public void testPCCharged() throws ClassNotFoundException, CDKException, Exception {

        RDBERule rule = new RDBERule();

        IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("C42H85NO8P", builder);
        formula.setCharge(1);

        Assert.assertEquals(1.0, rule.validate(formula), 0.0001);
    }

    /**
     * A unit test suite for JUnit for B.
     *
     * @return    The test suite
     */
    @Test
    public void testB() throws ClassNotFoundException, CDKException, Exception {

        RDBERule rule = new RDBERule();

        IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("C6H9BNO2", builder);
        formula.setCharge(1);

        Assert.assertEquals(1.0, rule.validate(formula), 0.0001);
    }
}
