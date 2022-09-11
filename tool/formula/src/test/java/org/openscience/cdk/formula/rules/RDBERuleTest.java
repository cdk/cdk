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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

/**
 * @cdk.module test-formula
 */
class RDBERuleTest extends FormulaRuleTest {

    private static IChemObjectBuilder builder;

    /**
    *  The JUnit setup method
    */
    @BeforeAll
    static void setUp() throws Exception {
        builder = DefaultChemObjectBuilder.getInstance();
        setRule(RDBERule.class);
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    void testRDBERule() throws Exception {

        IRule rule = new RDBERule();
        Assertions.assertNotNull(rule);

    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    void testDefault() throws Exception {

        IRule rule = new RDBERule();
        Object[] objects = rule.getParameters();
        Assertions.assertEquals(2, objects.length);

        double min = (Double) objects[0];
        double max = (Double) objects[1];
        Assertions.assertEquals(-0.5, min, 0.00001);
        Assertions.assertEquals(30, max, 0.00001);

    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    void testSetParameters() throws Exception {

        IRule rule = new RDBERule();
        Object[] params = new Object[2];

        params[0] = 0.0;
        params[1] = 10.0;
        rule.setParameters(params);

        Object[] objects = rule.getParameters();
        Assertions.assertEquals(2, objects.length);

        double min = (Double) objects[0];
        double max = (Double) objects[1];
        Assertions.assertEquals(0.0, min, 0.00001);
        Assertions.assertEquals(10.0, max, 0.00001);

    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    void testDefaultValidFalse() throws Exception {

        IRule rule = new RDBERule();

        IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("C2H4", builder);

        Assertions.assertEquals(1.0, rule.validate(formula), 0.0001);
    }

    /**
     * A unit test suite for JUnit.C2H11N4O4
     *
     *
     */
    @Test
    void testDefaultValidFalse_SetParam() throws Exception {

        RDBERule rule = new RDBERule();

        IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("CH2F10S2", builder);

        List<Double> value = rule.getRDBEValue(formula);
        Assertions.assertEquals(6, value.size(), 0.0001);
        Assertions.assertEquals(-4.0, value.get(0), 0.0001);
        Assertions.assertEquals(-3.0, value.get(1), 0.0001);
        Assertions.assertEquals(-2.0, value.get(2), 0.0001);
        Assertions.assertEquals(-2.0, value.get(3), 0.0001);
        Assertions.assertEquals(-1.0, value.get(4), 0.0001);
        Assertions.assertEquals(0.0, value.get(5), 0.0001);

        Assertions.assertEquals(1.0, rule.validate(formula), 0.0001);
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    void testDefaultValidTrue() throws Exception {

        IRule rule = new RDBERule();

        IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("C1H4", builder);
        formula.setCharge(0);

        Assertions.assertEquals(1.0, rule.validate(formula), 0.0001);
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    void testGetRDBEValue_IMolecularFormula() throws Exception {

        RDBERule rule = new RDBERule();

        IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("C2H4", builder);

        Assertions.assertEquals(1.0, rule.getRDBEValue(formula).get(0), 0.0001);
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    void testValidate_IMolecularFormula_double() throws Exception {

        RDBERule rule = new RDBERule();

        IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("C2H4", builder);

        Assertions.assertTrue(rule.validate(formula, 2.0));
    }

    /**
     * A unit test suite for JUnit.C3H8O3S2
     *
     *
     */
    @Test
    void test1() throws Exception {

        RDBERule rule = new RDBERule();

        IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("C3H8O3S2", builder);

        List<Double> value = rule.getRDBEValue(formula);
        Assertions.assertEquals(6, value.size(), 0.0001);
        Assertions.assertEquals(0.0, value.get(0), 0.0001);
        Assertions.assertEquals(1.0, rule.validate(formula), 0.0001);
    }

    /**
     * A unit test suite for JUnit.C4H8O3S1
     *
     *
     */
    @Test
    void test2() throws Exception {

        RDBERule rule = new RDBERule();

        IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("C4H8O3S1", builder);

        Assertions.assertEquals(1.0, rule.validate(formula), 0.0001);
    }

    /**
     * A unit test suite for JUnit.NH4+
     *
     *
     */
    @Test
    void testAnticipatedIonState_1() throws Exception {

        RDBERule rule = new RDBERule();

        IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("NH4", builder);
        formula.setCharge(1);

        Assertions.assertEquals(1.0, rule.validate(formula), 0.0001);
    }

    /**
     * A unit test suite for JUnit.NH4+
     *
     *
     */
    @Test
    void testAnticipatedIonState_2() throws Exception {

        RDBERule rule = new RDBERule();

        IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("NH4", builder);

        Assertions.assertEquals(0.0, rule.validate(formula), 0.0001);
    }

    /**
     * A unit test suite for JUnit for lipid PC.
     *
     * @cdk.bug 2322906
     *
     *
     */
    @Test
    void testPCCharged() throws Exception {

        RDBERule rule = new RDBERule();

        IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("C42H85NO8P", builder);
        formula.setCharge(1);

        Assertions.assertEquals(1.0, rule.validate(formula), 0.0001);
    }

    /**
     * A unit test suite for JUnit for B.
     *
     *
     */
    @Test
    void testB() throws Exception {

        RDBERule rule = new RDBERule();

        IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("C6H9BNO2", builder);
        formula.setCharge(1);

        Assertions.assertEquals(1.0, rule.validate(formula), 0.0001);
    }
}
