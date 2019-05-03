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
import org.openscience.cdk.formula.MolecularFormula;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * @cdk.module test-formula
 */
public class ToleranceRangeRuleTest extends FormulaRuleTest {

    private static IChemObjectBuilder builder;

    /**
    *  The JUnit setup method
    */
    @BeforeClass
    public static void setUp() throws Exception {
        builder = DefaultChemObjectBuilder.getInstance();
        setRule(ToleranceRangeRule.class);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testToleranceRangeRule() throws Exception {

        IRule rule = new ToleranceRangeRule();
        Assert.assertNotNull(rule);

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testDefault() throws Exception {

        IRule rule = new ToleranceRangeRule();
        Object[] objects = rule.getParameters();
        Assert.assertEquals(2, objects.length);

        double mass = (Double) objects[0];
        Assert.assertEquals(0.0, mass, 0.00001);
        double tolerance = (Double) objects[1];
        Assert.assertEquals(0.05, tolerance, 0.00001);

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testSetParameters() throws Exception {

        IRule rule = new ToleranceRangeRule();

        Object[] params = new Object[2];
        params[0] = 133.0;
        params[1] = 0.00005;
        rule.setParameters(params);

        Object[] objects = rule.getParameters();

        Assert.assertEquals(2, objects.length);

        double mass = (Double) objects[0];
        Assert.assertEquals(133.0, mass, 0.00001);
        double tolerance = (Double) objects[1];
        Assert.assertEquals(0.00005, tolerance, 0.00001);

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testDefaultValidFalse() throws Exception {

        IRule rule = new ToleranceRangeRule();

        IMolecularFormula formula = new MolecularFormula();
        IIsotope carb = builder.newInstance(IIsotope.class, "C");
        carb.setExactMass(12.00);
        IIsotope cl = builder.newInstance(IIsotope.class, "Cl");
        cl.setExactMass(34.96885268);
        formula.addIsotope(carb);
        formula.addIsotope(cl);

        Assert.assertEquals(0.0, rule.validate(formula), 0.0001);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testDefaultValidFalse_SetParam() throws Exception {

        IRule rule = new ToleranceRangeRule();

        IMolecularFormula formula = new MolecularFormula();
        IIsotope carb = builder.newInstance(IIsotope.class, "C");
        carb.setExactMass(12.00);
        IIsotope cl = builder.newInstance(IIsotope.class, "Cl");
        cl.setExactMass(34.96885268);
        formula.addIsotope(carb);
        formula.addIsotope(cl);

        Object[] params = new Object[2];
        params[0] = 46.0; // real -> 46.96885268
        params[1] = 0.00005;
        rule.setParameters(params);

        Assert.assertEquals(0.0, rule.validate(formula), 0.0001);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testDefaultValidTrue() throws Exception {

        IRule rule = new ToleranceRangeRule();

        IMolecularFormula formula = new MolecularFormula();
        IIsotope carb = builder.newInstance(IIsotope.class, "C");
        carb.setExactMass(12.00);
        IIsotope cl = builder.newInstance(IIsotope.class, "Cl");
        cl.setExactMass(34.96885268);
        formula.addIsotope(carb);
        formula.addIsotope(cl);

        Object[] params = new Object[2];
        params[0] = 46.96885268;
        params[1] = 0.00005;
        rule.setParameters(params);

        Assert.assertEquals(1.0, rule.validate(formula), 0.0001);
    }

}
