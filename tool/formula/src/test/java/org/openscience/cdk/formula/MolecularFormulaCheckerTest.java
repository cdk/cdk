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

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.formula.rules.ChargeRule;
import org.openscience.cdk.formula.rules.IRule;
import org.openscience.cdk.formula.rules.MMElementRule;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

/**
 * Checks the functionality of the MolecularFormulaChecker.
 *
 * @cdk.module test-formula
 */
public class MolecularFormulaCheckerTest extends CDKTestCase {

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
            e.printStackTrace();
        }
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testMolecularFormulaChecker_List() {

        Assert.assertNotNull(new MolecularFormulaChecker(new ArrayList<IRule>()));
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testGetRules() {

        MolecularFormulaChecker MFChecker = new MolecularFormulaChecker(new ArrayList<IRule>());

        Assert.assertNotNull(MFChecker.getRules());
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testIsValidSum_IMolecularFormula() {

        IMolecularFormula formula = new MolecularFormula();
        formula.addIsotope(ifac.getMajorIsotope("C"), 1);
        formula.addIsotope(ifac.getMajorIsotope("H"), 100);

        List<IRule> rules = new ArrayList<IRule>();
        rules.add(new MMElementRule());

        MolecularFormulaChecker MFChecker = new MolecularFormulaChecker(rules);

        Assert.assertEquals(0.0, MFChecker.isValidSum(formula), 0.001);

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testIsValid_NOT() {

        IMolecularFormula formula = new MolecularFormula();
        formula.addIsotope(ifac.getMajorIsotope("C"), 1);
        formula.addIsotope(ifac.getMajorIsotope("H"), 100);

        List<IRule> rules = new ArrayList<IRule>();
        rules.add(new MMElementRule());

        MolecularFormulaChecker MFChecker = new MolecularFormulaChecker(rules);

        IMolecularFormula formulaWith = MFChecker.isValid(formula);

        Assert.assertEquals(0.0, formulaWith.getProperty((new MMElementRule()).getClass()));

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testIsValid_IMolecularFormula() {

        IMolecularFormula formula = new MolecularFormula();
        formula.addIsotope(ifac.getMajorIsotope("C"), 1);
        formula.addIsotope(ifac.getMajorIsotope("H"), 100);
        formula.setCharge(0);

        List<IRule> rules = new ArrayList<IRule>();
        rules.add(new MMElementRule());
        rules.add(new ChargeRule());

        MolecularFormulaChecker MFChecker = new MolecularFormulaChecker(rules);

        Assert.assertEquals(0.0, MFChecker.isValidSum(formula), 0.001);

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testIsValid_NOT_2Rules() {

        IMolecularFormula formula = new MolecularFormula();
        formula.addIsotope(ifac.getMajorIsotope("C"), 1);
        formula.addIsotope(ifac.getMajorIsotope("H"), 100);
        formula.setCharge(0);

        List<IRule> rules = new ArrayList<IRule>();
        rules.add(new MMElementRule());
        rules.add(new ChargeRule());

        MolecularFormulaChecker MFChecker = new MolecularFormulaChecker(rules);

        IMolecularFormula formulaWith = MFChecker.isValid(formula);

        Assert.assertEquals(0.0, formulaWith.getProperty((new MMElementRule()).getClass()));
        Assert.assertEquals(1.0, formulaWith.getProperty((new ChargeRule()).getClass()));

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testIsValidSum_True_2Rules() {

        IMolecularFormula formula = new MolecularFormula();
        formula.addIsotope(ifac.getMajorIsotope("C"), 1);
        formula.addIsotope(ifac.getMajorIsotope("H"), 4);
        formula.setCharge(0);

        List<IRule> rules = new ArrayList<IRule>();
        rules.add(new MMElementRule());
        rules.add(new ChargeRule());

        MolecularFormulaChecker MFChecker = new MolecularFormulaChecker(rules);

        Assert.assertEquals(1.0, MFChecker.isValidSum(formula), 0.001);

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testIsValid_True_2Rules() {

        IMolecularFormula formula = new MolecularFormula();
        formula.addIsotope(ifac.getMajorIsotope("C"), 1);
        formula.addIsotope(ifac.getMajorIsotope("H"), 4);
        formula.setCharge(0);

        List<IRule> rules = new ArrayList<IRule>();
        rules.add(new MMElementRule());
        rules.add(new ChargeRule());

        MolecularFormulaChecker MFChecker = new MolecularFormulaChecker(rules);

        IMolecularFormula formulaWith = MFChecker.isValid(formula);

        Assert.assertEquals(1.0, formulaWith.getProperty((new MMElementRule()).getClass()));
        Assert.assertEquals(1.0, formulaWith.getProperty((new ChargeRule()).getClass()));

    }
}
