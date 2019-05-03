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
import org.openscience.cdk.formula.MolecularFormula;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * @cdk.module test-formula
 */
public class ChargeRuleTest extends FormulaRuleTest {

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
    public void testChargeRule() throws ClassNotFoundException, CDKException, Exception {

        IRule rule = new ChargeRule();
        Assert.assertNotNull(rule);

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testDefault() throws ClassNotFoundException, CDKException, Exception {

        IRule rule = new ChargeRule();
        Object[] objects = rule.getParameters();
        Assert.assertEquals(1, objects.length);

        double charge = (Double) objects[0];
        Assert.assertEquals(0.0, charge, 0.00001);

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testSetParameters() throws ClassNotFoundException, CDKException, Exception {

        IRule rule = new ChargeRule();

        Object[] params = new Object[1];

        params[0] = -1.0;
        rule.setParameters(params);

        Object[] objects = rule.getParameters();
        Assert.assertEquals(1, objects.length);

        double charge = (Double) objects[0];
        Assert.assertEquals(-1.0, charge, 0.00001);

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testDefaultValidFalse() throws ClassNotFoundException, CDKException, Exception {

        IRule rule = new ChargeRule();

        IMolecularFormula formula = new MolecularFormula();
        formula.addIsotope(builder.newInstance(IIsotope.class, "C"), 2);
        formula.addIsotope(builder.newInstance(IIsotope.class, "H"), 200);
        formula.setCharge(1);

        Assert.assertEquals(0.0, rule.validate(formula), 0.0001);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testDefaultValidFalse_SetParam() throws ClassNotFoundException, CDKException, Exception {

        IRule rule = new ChargeRule();

        Object[] params = new Object[1];
        IMolecularFormula formula = new MolecularFormula();
        formula.addIsotope(builder.newInstance(IIsotope.class, "C"), 2);
        formula.addIsotope(builder.newInstance(IIsotope.class, "H"), 200);
        formula.setCharge(1);

        params[0] = -1.0;
        rule.setParameters(params);

        Assert.assertEquals(0.0, rule.validate(formula), 0.0001);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testDefaultValidTrue() throws ClassNotFoundException, CDKException, Exception {

        IRule rule = new ChargeRule();

        IMolecularFormula formula = new MolecularFormula();
        formula.addIsotope(builder.newInstance(IIsotope.class, "C"), 2);
        formula.addIsotope(builder.newInstance(IIsotope.class, "H"), 6);
        formula.setCharge(0);

        Assert.assertEquals(1.0, rule.validate(formula), 0.0001);
    }

}
