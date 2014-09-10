/* Copyright (C) 2007-2008  Egon Willighagen <egonw@users.sf.net>
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
import org.junit.Test;
import org.openscience.cdk.Isotope;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.formula.MolecularFormula;
import org.openscience.cdk.interfaces.IMolecularFormula;

/**
 * Tests for formula restriction rules.
 *
 * @cdk.module test-formula
 */
public abstract class FormulaRuleTest extends CDKTestCase {

    protected static IRule rule;

    public static void setRule(Class ruleClass) throws Exception {
        if (FormulaRuleTest.rule == null) {
            Object rule = (Object) ruleClass.newInstance();
            if (!(rule instanceof IRule)) {
                throw new CDKException("The passed rule class must be a IRule");
            }
            FormulaRuleTest.rule = (IRule) rule;
        }
    }

    /**
     * Makes sure that the extending class has set the super.rule.
     * Each extending class should have this bit of code (JUnit4 formalism):
     * <pre>
     * @Before public static void setUp() {
     *   // Pass a Class, not an Object!
     *   setRule(SomeDescriptor.class);
     * }
     *
     * <p>The unit tests in the extending class may use this instance, but
     * are not required.
     *
     * </pre>
     */
    @Test
    public void testHasSetSuperDotRule() {
        Assert.assertNotNull("The extending class must set the super.rule in its setUp() method.", rule);
    }

    @Test
    public void testGetParameters() {
        Object[] params = rule.getParameters();
        //		FIXME: the next would be nice, but not currently agreed-upon policy
        //		assertNotNull(
        //			"The method getParameters() must return a non-null value, possible a zero length Object[] array",
        //			paramNames
        //		);
        //		FIXME: so instead:
        if (params == null) params = new Object[0];
        for (int i = 0; i < params.length; i++) {
            Assert.assertNotNull("A parameter default must not be null.", params[i]);
        }
    }

    @Test
    public void testSetParameters_arrayObject() throws Exception {
        Object[] defaultParams = rule.getParameters();
        rule.setParameters(defaultParams);
    }

    @Test
    public void testValidate_IMolecularFormula() throws Exception {
        IMolecularFormula mf = new MolecularFormula();
        mf.addIsotope(new Isotope("C", 13));
        mf.addIsotope(new Isotope("H", 2), 4);
        rule.validate(new MolecularFormula());

        // can it handle an empty MF?
        rule.validate(new MolecularFormula());
    }

}
