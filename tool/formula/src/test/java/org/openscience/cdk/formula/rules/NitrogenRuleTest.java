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
public class NitrogenRuleTest extends FormulaRuleTest {

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
    public void testNitrogenRule() throws ClassNotFoundException, CDKException, Exception {

        IRule rule = new NitrogenRule();
        Assert.assertNotNull(rule);

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testDefault() throws ClassNotFoundException, CDKException, Exception {

        IRule rule = new NitrogenRule();
        Object[] objects = rule.getParameters();
        Assert.assertNull(objects);

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testSetParameters() throws ClassNotFoundException, CDKException, Exception {

        IRule rule = new NitrogenRule();
        rule.setParameters(null);

        Object[] objects = rule.getParameters();
        Assert.assertNull(objects);

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testDefaultValidFalse() throws ClassNotFoundException, CDKException, Exception {

        IRule rule = new NitrogenRule();

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

        IRule rule = new NitrogenRule();

        IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("C2H11N4O4", builder);
        formula.setCharge(1);

        Assert.assertEquals(1.0, rule.validate(formula), 0.0001);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testDefaultValidTrue() throws ClassNotFoundException, CDKException, Exception {

        IRule rule = new NitrogenRule();

        IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("C4H13N1O5", builder);
        formula.setCharge(0);

        Assert.assertEquals(1.0, rule.validate(formula), 0.0001);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testC45H75NO15() throws ClassNotFoundException, CDKException, Exception {

        IRule rule = new NitrogenRule();

        IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("C45H75NO15", builder);
        formula.setCharge(0);

        Assert.assertEquals(1.0, rule.validate(formula), 0.0001);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testC45H71N7O10() throws ClassNotFoundException, CDKException, Exception {

        IRule rule = new NitrogenRule();

        IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("C45H71N7O10", builder);
        formula.setCharge(0);

        Assert.assertEquals(1.0, rule.validate(formula), 0.0001);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testC49H75NO12() throws ClassNotFoundException, CDKException, Exception {

        IRule rule = new NitrogenRule();

        IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("C49H75NO12", builder);
        formula.setCharge(0);

        Assert.assertEquals(1.0, rule.validate(formula), 0.0001);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testC50H95NO10() throws ClassNotFoundException, CDKException, Exception {

        IRule rule = new NitrogenRule();

        IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("C50H95NO10", builder);
        formula.setCharge(0);

        Assert.assertEquals(1.0, rule.validate(formula), 0.0001);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testC47H75N5O10() throws ClassNotFoundException, CDKException, Exception {

        IRule rule = new NitrogenRule();

        IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("C47H75N5O10", builder);
        formula.setCharge(0);

        Assert.assertEquals(1.0, rule.validate(formula), 0.0001);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testC36H42N2O23() throws ClassNotFoundException, CDKException, Exception {

        IRule rule = new NitrogenRule();

        IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("C36H42N2O23", builder);
        formula.setCharge(0);

        Assert.assertEquals(1.0, rule.validate(formula), 0.0001);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testN() throws ClassNotFoundException, CDKException, Exception {

        IRule rule = new NitrogenRule();

        IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("NH3", builder);
        formula.setCharge(0);

        Assert.assertEquals(1.0, rule.validate(formula), 0.0001);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testNPlus() throws ClassNotFoundException, CDKException, Exception {

        IRule rule = new NitrogenRule();

        IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("NH4", builder);
        formula.setCharge(1);

        Assert.assertEquals(1.0, rule.validate(formula), 0.0001);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testNominalMass() throws ClassNotFoundException, CDKException, Exception {

        IRule rule = new NitrogenRule();

        IMolecularFormula formula = MolecularFormulaManipulator.getMolecularFormula("C25H53NO7P", builder);
        formula.setCharge(1);

        Assert.assertEquals(1.0, rule.validate(formula), 0.0001);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testDoubleCharge() throws ClassNotFoundException, CDKException, Exception {

        IRule rule = new NitrogenRule();

        IMolecularFormula formula = MolecularFormulaManipulator.getMolecularFormula("C22H34N2S2", builder);
        formula.setCharge(2);

        Assert.assertEquals(1.0, rule.validate(formula), 0.0001);
    }

    /**
     * A unit test suite for JUnit. Compounds like Fe, Co, Hg, Pt, As.C40H46FeN6O8S2
     *
     * @return    The test suite
     */
    @Test
    public void testWithFe() throws ClassNotFoundException, CDKException, Exception {

        IRule rule = new NitrogenRule();

        IMolecularFormula formula = MolecularFormulaManipulator.getMolecularFormula("C40H46FeN6O8S2", builder);
        formula.setCharge(2);

        Assert.assertEquals(1.0, rule.validate(formula), 0.0001);
    }

    /**
     * A unit test suite for JUnit. Compounds like Fe, Co, Hg, Pt, As.C40H46FeN6O8S2
     *
     * @return    The test suite
     */
    @Test
    public void testWithCo() throws ClassNotFoundException, CDKException, Exception {

        IRule rule = new NitrogenRule();

        IMolecularFormula formula = MolecularFormulaManipulator.getMolecularFormula("C43H50CoN4O16", builder);
        formula.setCharge(0);

        Assert.assertEquals(1.0, rule.validate(formula), 0.0001);
    }
}
