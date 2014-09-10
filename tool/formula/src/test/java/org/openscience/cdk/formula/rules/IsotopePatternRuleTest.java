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

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.formula.MolecularFormula;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecularFormula;

/**
 * @cdk.module test-formula
 */
public class IsotopePatternRuleTest extends FormulaRuleTest {

    private static IChemObjectBuilder builder;
    private static IsotopeFactory     ifac;

    /**
     *  The JUnit setup method
     */
    @BeforeClass
    public static void setUp() throws Exception {
        builder = DefaultChemObjectBuilder.getInstance();
        ifac = Isotopes.getInstance();
        setRule(IsotopePatternRule.class);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testIsotopePatternRule() throws Exception {

        IRule rule = new IsotopePatternRule();
        Assert.assertNotNull(rule);

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testDefault() throws Exception {

        IRule rule = new IsotopePatternRule();
        Object[] objects = rule.getParameters();

        Assert.assertNull(objects[0]);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testSetParameters() throws Exception {

        IRule rule = new IsotopePatternRule();

        Object[] params = new Object[2];

        params[0] = new ArrayList<Double[]>();
        params[1] = 0.0001;
        rule.setParameters(params);

        Object[] objects = rule.getParameters();

        Assert.assertNotNull(objects[0]);
        Assert.assertEquals(2, objects.length);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testValid_Bromine() throws Exception {

        List<double[]> spectrum = new ArrayList<double[]>();
        spectrum.add(new double[]{157.8367, 51.399});
        spectrum.add(new double[]{159.8346, 100.00});
        spectrum.add(new double[]{161.8326, 48.639});

        IRule rule = new IsotopePatternRule();
        Object[] params = new Object[2];
        params[0] = spectrum;
        params[1] = 0.001;
        rule.setParameters(params);

        IMolecularFormula formula = new MolecularFormula();
        formula.addIsotope(ifac.getMajorIsotope("C"), 2);
        formula.addIsotope(ifac.getMajorIsotope("Br"), 2);
        formula.setCharge(0);

        Assert.assertEquals(0.0, rule.validate(formula), 0.0001);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testDefaultValidTrue() throws Exception {

        IMolecularFormula formula = new MolecularFormula();
        formula.addIsotope(ifac.getMajorIsotope("C"), 5);
        formula.addIsotope(ifac.getMajorIsotope("H"), 13);
        formula.addIsotope(ifac.getMajorIsotope("N"), 2);
        formula.addIsotope(ifac.getMajorIsotope("O"), 2);
        formula.setCharge(0);

        /** experimental results*/

        List<double[]> spectrum = new ArrayList<double[]>();
        spectrum.add(new double[]{133.0977, 100.00});
        spectrum.add(new double[]{134.09475, 0.6});
        spectrum.add(new double[]{134.1010, 5.4});

        IRule rule = new IsotopePatternRule();
        Object[] params = new Object[2];
        params[0] = spectrum;
        params[1] = 0.001;
        rule.setParameters(params);

        Assert.assertEquals(0.9433, rule.validate(formula), 0.001);
    }

}
