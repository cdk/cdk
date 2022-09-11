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
package org.openscience.cdk.formula;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.formula.rules.ChargeRule;
import org.openscience.cdk.formula.rules.IRule;
import org.openscience.cdk.formula.rules.MMElementRule;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Checks the functionality of the MolecularFormulaChecker.
 *
 * @cdk.module test-formula
 */
class MolecularFormulaCheckerTest extends CDKTestCase {

    private final static IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    private IsotopeFactory                  ifac;

    /**
     *  Constructor for the MolecularFormulaCheckerTest object.
     *
     */
    public MolecularFormulaCheckerTest() {

        super();
        try {
            ifac = Isotopes.getInstance();
        } catch (IOException e) {
            LoggingToolFactory.createLoggingTool(MolecularFormulaCheckerTest.class)
                              .warn("Unexpected Error:", e);
        }
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    void testMolecularFormulaChecker_List() {

        Assertions.assertNotNull(new MolecularFormulaChecker(new ArrayList<>()));
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    void testGetRules() {

        MolecularFormulaChecker MFChecker = new MolecularFormulaChecker(new ArrayList<>());

        Assertions.assertNotNull(MFChecker.getRules());
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    void testIsValidSum_IMolecularFormula() {

        IMolecularFormula formula = new MolecularFormula();
        formula.addIsotope(ifac.getMajorIsotope("C"), 1);
        formula.addIsotope(ifac.getMajorIsotope("H"), 100);

        List<IRule> rules = new ArrayList<>();
        rules.add(new MMElementRule());

        MolecularFormulaChecker MFChecker = new MolecularFormulaChecker(rules);

        Assertions.assertEquals(0.0, MFChecker.isValidSum(formula), 0.001);

    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    void testIsValid_NOT() {

        IMolecularFormula formula = new MolecularFormula();
        formula.addIsotope(ifac.getMajorIsotope("C"), 1);
        formula.addIsotope(ifac.getMajorIsotope("H"), 100);

        List<IRule> rules = new ArrayList<>();
        rules.add(new MMElementRule());

        MolecularFormulaChecker MFChecker = new MolecularFormulaChecker(rules);

        IMolecularFormula formulaWith = MFChecker.isValid(formula);

        Assertions.assertEquals(0.0d, formulaWith.getProperty((new MMElementRule()).getClass()), 0.01);

    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    void testIsValid_IMolecularFormula() {

        IMolecularFormula formula = new MolecularFormula();
        formula.addIsotope(ifac.getMajorIsotope("C"), 1);
        formula.addIsotope(ifac.getMajorIsotope("H"), 100);
        formula.setCharge(0);

        List<IRule> rules = new ArrayList<>();
        rules.add(new MMElementRule());
        rules.add(new ChargeRule());

        MolecularFormulaChecker MFChecker = new MolecularFormulaChecker(rules);

        Assertions.assertEquals(0.0, MFChecker.isValidSum(formula), 0.001);

    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    void testIsValid_NOT_2Rules() {

        IMolecularFormula formula = new MolecularFormula();
        formula.addIsotope(ifac.getMajorIsotope("C"), 1);
        formula.addIsotope(ifac.getMajorIsotope("H"), 100);
        formula.setCharge(0);

        List<IRule> rules = new ArrayList<>();
        rules.add(new MMElementRule());
        rules.add(new ChargeRule());

        MolecularFormulaChecker MFChecker = new MolecularFormulaChecker(rules);

        IMolecularFormula formulaWith = MFChecker.isValid(formula);

        Assertions.assertEquals(0.0, formulaWith.getProperty((new MMElementRule()).getClass()), 0.01);
        Assertions.assertEquals(1.0, formulaWith.getProperty((new ChargeRule()).getClass()), 0.01);

    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    void testIsValidSum_True_2Rules() {

        IMolecularFormula formula = new MolecularFormula();
        formula.addIsotope(ifac.getMajorIsotope("C"), 1);
        formula.addIsotope(ifac.getMajorIsotope("H"), 4);
        formula.setCharge(0);

        List<IRule> rules = new ArrayList<>();
        rules.add(new MMElementRule());
        rules.add(new ChargeRule());

        MolecularFormulaChecker MFChecker = new MolecularFormulaChecker(rules);

        Assertions.assertEquals(1.0, MFChecker.isValidSum(formula), 0.001);

    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    void testIsValid_True_2Rules() {

        IMolecularFormula formula = new MolecularFormula();
        formula.addIsotope(ifac.getMajorIsotope("C"), 1);
        formula.addIsotope(ifac.getMajorIsotope("H"), 4);
        formula.setCharge(0);

        List<IRule> rules = new ArrayList<>();
        rules.add(new MMElementRule());
        rules.add(new ChargeRule());

        MolecularFormulaChecker MFChecker = new MolecularFormulaChecker(rules);

        IMolecularFormula formulaWith = MFChecker.isValid(formula);

        Assertions.assertEquals(1.0, formulaWith.getProperty((new MMElementRule()).getClass()), 0.01);
        Assertions.assertEquals(1.0, formulaWith.getProperty((new ChargeRule()).getClass()), 0.01);

    }
}
