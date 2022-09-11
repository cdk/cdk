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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.formula.MolecularFormula;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * @cdk.module test-formula
 */
class MMElementRuleTest extends FormulaRuleTest {

    private static IChemObjectBuilder builder;

    /**
    *  The JUnit setup method
    */
    @BeforeAll
    static void setUp() throws Exception {
        builder = DefaultChemObjectBuilder.getInstance();
        setRule(MMElementRule.class);
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    void testMMElementRule() throws Exception {

        IRule rule = new MMElementRule();
        Assertions.assertNotNull(rule);

    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    void testDefault() throws Exception {

        IRule rule = new MMElementRule();
        Object[] objects = rule.getParameters();

        Assertions.assertSame(MMElementRule.Database.WILEY, objects[0]);
        Assertions.assertSame(MMElementRule.RangeMass.Minus500, objects[1]);

    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    void testSetParameters() throws Exception {

        IRule rule = new MMElementRule();

        Object[] params = new Object[2];

        params[0] = MMElementRule.Database.DNP;
        params[1] = MMElementRule.RangeMass.Minus1000;

        rule.setParameters(params);
        Object[] objects = rule.getParameters();

        Assertions.assertSame(MMElementRule.Database.DNP, objects[0]);
        Assertions.assertSame(MMElementRule.RangeMass.Minus1000, objects[1]);
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    void testDefaultValidFalse() throws Exception {

        IRule rule = new MMElementRule();

        IMolecularFormula formula = new MolecularFormula();
        formula.addIsotope(builder.newInstance(IIsotope.class, "C"), 2);
        formula.addIsotope(builder.newInstance(IIsotope.class, "H"), 200);

        Assertions.assertEquals(0.0, rule.validate(formula), 0.0001);
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    void testDefaultValidTrue() throws Exception {

        IRule rule = new MMElementRule();

        IMolecularFormula formula = new MolecularFormula();
        formula.addIsotope(builder.newInstance(IIsotope.class, "C"), 2);
        formula.addIsotope(builder.newInstance(IIsotope.class, "H"), 6);

        Assertions.assertEquals(1.0, rule.validate(formula), 0.0001);
    }
}
