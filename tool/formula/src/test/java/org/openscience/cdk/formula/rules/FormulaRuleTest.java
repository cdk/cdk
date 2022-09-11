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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Isotope;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.formula.MolecularFormula;
import org.openscience.cdk.interfaces.IMolecularFormula;

/**
 * Tests for formula restriction rules.
 *
 * @cdk.module test-formula
 */
abstract class FormulaRuleTest extends CDKTestCase {

    private static IRule rule;
    private static Class<? extends IRule> ruleClass;

    static void setRule(Class<? extends IRule> ruleClass) throws Exception {
        FormulaRuleTest.ruleClass = ruleClass;
        FormulaRuleTest.rule = getRule();
    }
    
    private static IRule getRule() throws Exception {
        return ruleClass.newInstance();
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
    void testHasSetSuperDotRule() {
        Assertions.assertNotNull(rule, "The extending class must set the super.rule in its setUp() method.");
    }

    @Test
    void testValidate_IMolecularFormula() throws Exception {
        IRule rule = getRule();
    	
        IMolecularFormula mf = new MolecularFormula();
        mf.addIsotope(new Isotope("C", 13));
        mf.addIsotope(new Isotope("H", 2), 4);
        rule.validate(mf);

        // can it handle an empty MF?
        rule.validate(new MolecularFormula());
    }
}
