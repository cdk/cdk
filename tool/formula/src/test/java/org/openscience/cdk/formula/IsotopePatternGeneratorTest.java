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

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;

/**
 * Checks the functionality of the IsotopePatternGenerator.
 *
 * @cdk.module test-formula
 *
 * @author         Miguel Rojas
 * @cdk.created    2007-03-01
 */
public class IsotopePatternGeneratorTest extends CDKTestCase {

    private final static IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();

    /**
     *  Constructor for the IsotopePatternGeneratorTest object
     *
     */
    public IsotopePatternGeneratorTest() {
        super();
    }

    /**
     * A unit test for JUnit.
     *
     * @return    Description of the Return Value
     */
    @Test
    public void testIsotopePatternGenerator() {
        IsotopePatternGenerator isotopeGe = new IsotopePatternGenerator();
        Assert.assertNotNull(isotopeGe);
    }

    /**
     * A unit test for JUnit.
     *
     * @return    Description of the Return Value
     */
    @Test
    public void testIsotopePatternGenerator_double() {
        IsotopePatternGenerator isotopeGe = new IsotopePatternGenerator();
        Assert.assertNotNull(isotopeGe);
    }

    /**
     * A unit test for JUnit:
     *
     * @return    Description of the Return Value
     */
    @Test
    public void testGetIsotopes_IMolecularFormula() {
        IMolecularFormula molFor = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("C41H79N8O3P1", builder);
        IsotopePatternGenerator isotopeGe = new IsotopePatternGenerator(.1);
        IsotopePattern isos = isotopeGe.getIsotopes(molFor);
        Assert.assertEquals(2, isos.getNumberOfIsotopes(), 0.001);
    }

    /**
     * A unit test for JUnit:
     *
     * @return    Description of the Return Value
     */
    @Test
    public void testGetIsotopes_IMolecularFormula_withoutONE() {
        IMolecularFormula molFor = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("C41H79N8O3P", builder);
        IsotopePatternGenerator isotopeGe = new IsotopePatternGenerator(.01);
        IsotopePattern isos = isotopeGe.getIsotopes(molFor);
        Assert.assertEquals(6, isos.getNumberOfIsotopes(), 0.001);
    }

    /**
     * A unit test for JUnit: Isotopes of the Bromine.
     *
     * @return    Description of the Return Value
     */
    @Test
    public void testGetIsotopes1() {
        IMolecularFormula molFor = new MolecularFormula();
        molFor.addIsotope(builder.newInstance(IIsotope.class, "Br"));
        molFor.addIsotope(builder.newInstance(IIsotope.class, "Br"));

        IsotopePatternGenerator isotopeGe = new IsotopePatternGenerator(.1);
        IsotopePattern isoPattern = isotopeGe.getIsotopes(molFor);

        Assert.assertEquals(3, isoPattern.getNumberOfIsotopes());

    }

    /**
     * A unit test for JUnit: Isotopes of the Bromine.
     *
     * @return    Description of the Return Value
     */
    @Test
    public void testCalculateIsotopesAllBromine() {
        // RESULTS ACCORDING PAGE: http://www2.sisweb.com/mstools/isotope.htm
        double[] massResults = {157.836669, 159.834630, 161.832580};
        double[] abundResults = {.512, 1.00, .487};

        IMolecularFormula molFor = new MolecularFormula();
        molFor.addIsotope(builder.newInstance(IIsotope.class, "Br"));
        molFor.addIsotope(builder.newInstance(IIsotope.class, "Br"));

        IsotopePatternGenerator isotopeGe = new IsotopePatternGenerator(.1);
        IsotopePattern isoPattern = isotopeGe.getIsotopes(molFor);

        Assert.assertEquals(3, isoPattern.getNumberOfIsotopes());

        Assert.assertEquals(massResults[0], isoPattern.getIsotope(0).getMass(), 0.01);
        Assert.assertEquals(massResults[1], isoPattern.getIsotope(1).getMass(), 0.01);
        Assert.assertEquals(massResults[2], isoPattern.getIsotope(2).getMass(), 0.01);

        Assert.assertEquals(abundResults[0], isoPattern.getIsotope(0).getIntensity(), 0.01);
        Assert.assertEquals(abundResults[1], isoPattern.getIsotope(1).getIntensity(), 0.01);
        Assert.assertEquals(abundResults[2], isoPattern.getIsotope(2).getIntensity(), 0.01);

    }

    /**
     * A unit test for JUnit: Isotopes of the Iodemethylidyne.
     *
     * @return    Description of the Return Value
     */
    @Test
    public void testCalculateIsotopesIodemethylidyne() {
        // RESULTS ACCORDING PAGE: http://www2.sisweb.com/mstools/isotope.htm
        double[] massResults = {138.904480, 139.907839};
        double[] abundResults = {1.00, .011};

        IMolecularFormula molFor = new MolecularFormula();
        molFor.addIsotope(builder.newInstance(IIsotope.class, "C"));
        molFor.addIsotope(builder.newInstance(IIsotope.class, "I"));

        Assert.assertEquals(2, molFor.getIsotopeCount());

        IsotopePatternGenerator isotopeGe = new IsotopePatternGenerator(.01);
        IsotopePattern isoPattern = isotopeGe.getIsotopes(molFor);

        Assert.assertEquals(2, isoPattern.getNumberOfIsotopes());

        Assert.assertEquals(massResults[0], isoPattern.getIsotope(0).getMass(), 0.01);
        Assert.assertEquals(massResults[1], isoPattern.getIsotope(1).getMass(), 0.01);

        Assert.assertEquals(abundResults[0], isoPattern.getIsotope(0).getIntensity(), 0.01);
        Assert.assertEquals(abundResults[1], isoPattern.getIsotope(1).getIntensity(), 0.01);

    }

    /**
     * A unit test for JUnit: Isotopes of the n-Carbone.
     *
     * @return    Description of the Return Value
     */
    @Test
    public void testCalculateIsotopesnCarbono() {
        // RESULTS ACCORDING PAGE: http://www2.sisweb.com/mstools/isotope.htm
        double[] massResults = {120.000000, 121.003360, 122.006709};
        double[] abundResults = {1.00, .108, 0.005};

        IMolecularFormula molFor = new MolecularFormula();
        molFor.addIsotope(builder.newInstance(IIsotope.class, "C"), 10);

        IsotopePatternGenerator isotopeGe = new IsotopePatternGenerator(0.0010);
        IsotopePattern isoPattern = isotopeGe.getIsotopes(molFor);

        Assert.assertEquals(3, isoPattern.getNumberOfIsotopes());

        Assert.assertEquals(massResults[0], isoPattern.getIsotope(0).getMass(), 0.01);
        Assert.assertEquals(massResults[1], isoPattern.getIsotope(1).getMass(), 0.01);
        Assert.assertEquals(massResults[2], isoPattern.getIsotope(2).getMass(), 0.01);

        Assert.assertEquals(abundResults[0], isoPattern.getIsotope(0).getIntensity(), 0.01);
        Assert.assertEquals(abundResults[1], isoPattern.getIsotope(1).getIntensity(), 0.01);
        Assert.assertEquals(abundResults[2], isoPattern.getIsotope(2).getIntensity(), 0.01);

    }

    /**
     * A unit test for JUnit.
     *
     * @return    Description of the Return Value
     */
    @Test
    public void testCalculateIsotopesOrthinine() {
        // RESULTS ACCORDING PAGE: http://www2.sisweb.com/mstools/isotope.htm
        double[] massResults = {133.097720, 134.094750, 134.101079, 134.103990, 135.101959, 135.104430};
        double[] abundResults = {1.00, .006, .054, 0.002, 0.004, 0.001};

        IMolecularFormula molFor = new MolecularFormula();
        molFor.addIsotope(builder.newInstance(IIsotope.class, "C"), 5);
        molFor.addIsotope(builder.newInstance(IIsotope.class, "H"), 13);
        molFor.addIsotope(builder.newInstance(IIsotope.class, "N"), 2);
        molFor.addIsotope(builder.newInstance(IIsotope.class, "O"), 2);

        IsotopePatternGenerator isotopeGe = new IsotopePatternGenerator(0.0010);
        IsotopePattern isoPattern = isotopeGe.getIsotopes(molFor);

        Assert.assertEquals(6, isoPattern.getNumberOfIsotopes());

        Assert.assertEquals(massResults[0], isoPattern.getIsotope(0).getMass(), 0.01);
        Assert.assertEquals(massResults[1], isoPattern.getIsotope(1).getMass(), 0.01);
        Assert.assertEquals(massResults[2], isoPattern.getIsotope(2).getMass(), 0.01);
        Assert.assertEquals(massResults[3], isoPattern.getIsotope(3).getMass(), 0.01);
        Assert.assertEquals(massResults[4], isoPattern.getIsotope(4).getMass(), 0.01);
        Assert.assertEquals(massResults[5], isoPattern.getIsotope(5).getMass(), 0.01);

        Assert.assertEquals(abundResults[0], isoPattern.getIsotope(0).getIntensity(), 0.01);
        Assert.assertEquals(abundResults[1], isoPattern.getIsotope(1).getIntensity(), 0.01);
        Assert.assertEquals(abundResults[2], isoPattern.getIsotope(2).getIntensity(), 0.01);
        Assert.assertEquals(abundResults[3], isoPattern.getIsotope(3).getIntensity(), 0.01);
        Assert.assertEquals(abundResults[4], isoPattern.getIsotope(4).getIntensity(), 0.01);
        Assert.assertEquals(abundResults[5], isoPattern.getIsotope(5).getIntensity(), 0.01);

    }

    /**
     * @cdk.bug 3273205
     */
    @Test
    public void testCalculateIsotopesMn() {

        IMolecularFormula molFor = new MolecularFormula();
        molFor.addIsotope(builder.newInstance(IIsotope.class, "Mn"), 1);

        IsotopePatternGenerator isotopeGe = new IsotopePatternGenerator(0.001);
        IsotopePattern isoPattern = isotopeGe.getIsotopes(molFor);

        Assert.assertEquals(1, isoPattern.getNumberOfIsotopes());

    }
    
    /**
     * Calculate isotopes for C10000 (failed in CDK 1.5.12).
     */
    @Test
    public void testCalculateIsotopesC10000() {
        IMolecularFormula molFor = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("C10000", builder);
        IsotopePatternGenerator isotopeGe = new IsotopePatternGenerator(.1);
        IsotopePattern isos = isotopeGe.getIsotopes(molFor);
        Assert.assertEquals(44, isos.getNumberOfIsotopes());
        for (int i = 0; i < isos.getNumberOfIsotopes(); i++)
            Assert.assertTrue(isos.getIsotope(i).getMass() > 120085);
    }
    
    /**
     * Calculate isotopes for C20H30Fe2P2S4Cl4 (in CDK 1.5.12, this call 
     * sometimes returns 34 and sometimes 35 isotopes, non-deterministically).
     */
    @Ignore("Non-deterministic test value is bad! This likely depends on our current isotope data which is also bad.")
    public void testCalculateIsotopesC20H30Fe2P2S4Cl4() {
        IMolecularFormula molFor = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("C20H30Fe2P2S4Cl4", builder);
        IsotopePatternGenerator isotopeGe = new IsotopePatternGenerator(.01);
        IsotopePattern isos = isotopeGe.getIsotopes(molFor);
        Assert.assertEquals(35, isos.getNumberOfIsotopes());
    }

    @Test
    public void testGeneratorSavesState() {
        IsotopePatternGenerator isogen = new IsotopePatternGenerator(.1);

        IMolecularFormula mf1 = MolecularFormulaManipulator.getMolecularFormula("C6H12O6", builder);
        IsotopePattern ip1 = isogen.getIsotopes(mf1);
        Assert.assertEquals(1, ip1.getNumberOfIsotopes());

        IMolecularFormula mf2 = MolecularFormulaManipulator.getMolecularFormula("C6H12O6", builder);
        IsotopePattern ip2 = isogen.getIsotopes(mf2);
        Assert.assertEquals(1, ip2.getNumberOfIsotopes());
    }

    @Test
    public void testGetIsotopes_IMolecularFormula_Charged() {
        IsotopePatternGenerator isogen = new IsotopePatternGenerator(.1);
        
        IMolecularFormula mfPositive = MolecularFormulaManipulator.getMolecularFormula("C6H11O6Na", builder);
        mfPositive.setCharge(1);
        IsotopePattern ip1 = isogen.getIsotopes(mfPositive);
        Assert.assertEquals(1, ip1.getNumberOfIsotopes());

        isogen = new IsotopePatternGenerator(.1);
        IMolecularFormula mfNeutral = MolecularFormulaManipulator.getMolecularFormula("C6H12O6Na", builder);
        mfNeutral.setCharge(0);
        IsotopePattern ip2 = isogen.getIsotopes(mfNeutral);
        Assert.assertEquals(1, ip2.getNumberOfIsotopes());

        Assert.assertNotEquals(ip1.getIsotope(0).getMass(), ip2.getIsotope(0).getMass());
    }

    @Test
    public void testGetIsotopes_IMolecularFormula_deprotonate() {
        IsotopePatternGenerator isogen = new IsotopePatternGenerator(.1);

        IMolecularFormula mf1 = MolecularFormulaManipulator.getMolecularFormula("C6H12O6", builder);
        MolecularFormulaManipulator.adjustProtonation(mf1, -1);
        IsotopePattern ip1 = isogen.getIsotopes(mf1);
        Assert.assertEquals(1, ip1.getNumberOfIsotopes());

        isogen = new IsotopePatternGenerator(.1);
        IMolecularFormula mf2 = MolecularFormulaManipulator.getMolecularFormula("C6H11O6", builder);
        IsotopePattern ip2 = isogen.getIsotopes(mf2);
        Assert.assertEquals(1, ip2.getNumberOfIsotopes());

        Assert.assertEquals(ip1.getIsotope(0).getMass(), ip2.getIsotope(0).getMass(), 0.001);
    }

    @Test
    public void testMultipleFormulasForAMass() {
        IMolecularFormula mf = MolecularFormulaManipulator.getMolecularFormula("C6Cl2", builder);
        IsotopePatternGenerator isogen = new IsotopePatternGenerator(0.1).setMinIntensity(0.01)
                                                                         .setMinResolution(0.01)
                                                                         .setStoreFormulas(true);
        IsotopePattern pattern = isogen.getIsotopes(mf);
        List<IsotopeContainer> isotopes = pattern.getIsotopes();
        org.hamcrest.MatcherAssert.assertThat(isotopes.get(0).getFormulas().size(), is(1));
        org.hamcrest.MatcherAssert.assertThat(isotopes.get(1).getFormulas().size(), is(1));
        org.hamcrest.MatcherAssert.assertThat(isotopes.get(2).getFormulas().size(), is(2));
        org.hamcrest.MatcherAssert.assertThat(isotopes.get(3).getFormulas().size(), is(2));
        org.hamcrest.MatcherAssert.assertThat(isotopes.get(4).getFormulas().size(), is(3));
    }

}
