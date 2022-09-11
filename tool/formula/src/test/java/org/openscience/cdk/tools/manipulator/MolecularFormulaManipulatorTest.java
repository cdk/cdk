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
package org.openscience.cdk.tools.manipulator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Isotope;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.formula.MolecularFormula;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator.*;

/**
 * Checks the functionality of the MolecularFormulaManipulator.
 *
 * @cdk.module test-formula
 */
class MolecularFormulaManipulatorTest extends CDKTestCase {

    private final static IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    private IsotopeFactory                  ifac;

    /**
     *  Constructor for the MolecularFormulaManipulatorTest object.
     */
    MolecularFormulaManipulatorTest() {

        super();
        try {
            ifac = Isotopes.getInstance();
        } catch (IOException e) {
            LoggingToolFactory.createLoggingTool(MolecularFormulaManipulatorTest.class)
                              .warn("Unexpected Error:", e);
        }
    }

    /** Test atom and isotope count for methyl-group. */
    @Test
    void testGetAtomCount_IMolecularFormula() {

        IMolecularFormula formula = new MolecularFormula();
        formula.addIsotope(builder.newInstance(IIsotope.class, "C"));
        formula.addIsotope(builder.newInstance(IIsotope.class, "H"), 3);

        Assertions.assertEquals(2, formula.getIsotopeCount());

        Assertions.assertEquals(4, getAtomCount(formula));
    }

    /**
     * Test molecular formula's generated from IIsotopes, including hydrogen/deuterium handling.
     */
    @Test
    void testGetElementCount_IMolecularFormula_IElement() {
        IMolecularFormula formula = new MolecularFormula();
        IIsotope carb = builder.newInstance(IIsotope.class, "C");
        IIsotope flu = builder.newInstance(IIsotope.class, "F");
        IIsotope h1 = builder.newInstance(IIsotope.class, "H");
        IIsotope h2 = builder.newInstance(IIsotope.class, "H");
        h2.setExactMass(2.014101778);
        formula.addIsotope(carb, 2);
        formula.addIsotope(flu);
        formula.addIsotope(h1, 3);
        formula.addIsotope(h2, 4);

        Assertions.assertEquals(10, getAtomCount(formula));
        Assertions.assertEquals(4, formula.getIsotopeCount());
        Assertions.assertEquals(3, formula.getIsotopeCount(h1));
        Assertions.assertEquals(4, formula.getIsotopeCount(h2));

        Assertions.assertEquals(2, getElementCount(formula, builder.newInstance(IElement.class, carb)));
        Assertions.assertEquals(1, getElementCount(formula, builder.newInstance(IElement.class, flu)));
        Assertions.assertEquals(7, getElementCount(formula, builder.newInstance(IElement.class, h1)));
    }

    /**
     * Test getIsotopes for hydrogen/deuterium.
     */
    @Test
    void testGetIsotopes_IMolecularFormula_IElement() {
        IMolecularFormula formula = new MolecularFormula();
        IIsotope carb = builder.newInstance(IIsotope.class, "C");
        IIsotope flu = builder.newInstance(IIsotope.class, "F");
        IIsotope h1 = builder.newInstance(IIsotope.class, "H");
        IIsotope h2 = builder.newInstance(IIsotope.class, "H");
        h2.setExactMass(2.014101778);
        formula.addIsotope(carb, 1);
        formula.addIsotope(flu);
        formula.addIsotope(h1, 1);
        formula.addIsotope(h2, 2);

        List<IIsotope> isotopes = MolecularFormulaManipulator.getIsotopes(formula,
                builder.newInstance(IElement.class, "H"));
        Assertions.assertEquals(2, isotopes.size());
    }

    @Test
    void testContainsElement_IMolecularFormula_IElement() {
        IMolecularFormula formula = new MolecularFormula();
        IIsotope carb = builder.newInstance(IIsotope.class, "C");
        IIsotope flu = builder.newInstance(IIsotope.class, "F");
        IIsotope h1 = builder.newInstance(IIsotope.class, "H");
        IIsotope h2 = builder.newInstance(IIsotope.class, "H");
        h2.setExactMass(2.014101778);
        formula.addIsotope(carb, 1);
        formula.addIsotope(flu);
        formula.addIsotope(h1, 1);
        formula.addIsotope(h2, 2);

        Assertions.assertTrue(containsElement(formula, builder.newInstance(IElement.class, "C")));
        Assertions.assertTrue(containsElement(formula, builder.newInstance(IElement.class, "H")));
        Assertions.assertTrue(containsElement(formula, builder.newInstance(IElement.class, "F")));
    }

    @Test
    void testGetString_IMolecularFormula_Empty() {
        String stringMF = MolecularFormulaManipulator.getString(new MolecularFormula());
        Assertions.assertNotNull(stringMF);
        Assertions.assertEquals("", stringMF);
    }

    /** Test if formula re-ordering to a user-specified element order works */
    @Test
    void testGetString_IMolecularFormula_arrayString_boolean() {
        IMolecularFormula formula = new MolecularFormula();
        formula.addIsotope(builder.newInstance(IIsotope.class, "C"), 2);
        formula.addIsotope(builder.newInstance(IIsotope.class, "H"), 2);
        Assertions.assertEquals("C2H2", getString(formula));

        String[] newOrder = new String[2];
        newOrder[0] = "H";
        newOrder[1] = "C";

        Assertions.assertEquals("H2C2", getString(formula, newOrder, true));

    }

    /** Test if isotope-list re-ordering to a user-specified element order works */
    @Test
    void testPutInOrder_arrayString_IMolecularFormula() {
        IMolecularFormula formula = new MolecularFormula();
        formula.addIsotope(builder.newInstance(IIsotope.class, "C"), 2);
        formula.addIsotope(builder.newInstance(IIsotope.class, "H"), 2);

        String[] newOrder = new String[2];
        newOrder[0] = "H";
        newOrder[1] = "C";

        List<IIsotope> list = MolecularFormulaManipulator.putInOrder(newOrder, formula);
        Assertions.assertEquals("H", list.get(0).getSymbol());
        Assertions.assertEquals("C", list.get(1).getSymbol());

        newOrder = new String[2];
        newOrder[0] = "C";
        newOrder[1] = "H";

        list = MolecularFormulaManipulator.putInOrder(newOrder, formula);
        Assertions.assertEquals("C", list.get(0).getSymbol());
        Assertions.assertEquals("H", list.get(1).getSymbol());

    }

    @Test
    void testGetString__String_IMolecularFormula() {
        Assertions.assertNotNull(getMolecularFormula("C10H16", new MolecularFormula()));
        Assertions.assertNotNull(getMolecularFormula("C10H16", builder));
        IMolecularFormula formula = MolecularFormulaManipulator.getMolecularFormula("O3(Fe(O2))3", builder);
        Assertions.assertEquals(formula.getIsotopeCount(new Atom("Fe") ), 3);
        Assertions.assertEquals(formula.getIsotopeCount(new Atom("O") ), 9);
    }

    /** Test if formula-order is independent of isotope-insertion order */
    @Test
    void testGetString_IMolecularFormula() {
        IMolecularFormula mf1 = new MolecularFormula();
        mf1.addIsotope(builder.newInstance(IIsotope.class, "C"), 10);
        mf1.addIsotope(builder.newInstance(IIsotope.class, "H"), 16);

        Assertions.assertEquals("C10H16", getString(mf1));

        IMolecularFormula mf2 = new MolecularFormula();
        mf2.addIsotope(builder.newInstance(IAtom.class, "H"), 16);
        mf2.addIsotope(builder.newInstance(IAtom.class, "C"), 10);

        Assertions.assertEquals("C10H16", getString(mf2));

        Assertions.assertEquals(getString(mf2), getString(mf1));

    }

    /**
     * @cdk.bug 2276507
     */
    @Test
    void testBug2276507() {
        IMolecularFormula mf1 = new MolecularFormula();
        mf1.addIsotope(builder.newInstance(IIsotope.class, "C"), 1);
        mf1.addIsotope(builder.newInstance(IIsotope.class, "H"), 4);

        Assertions.assertEquals("CH4", getString(mf1));
    }

    /**
     * Test setOne parameter for {@link MolecularFormulaManipulator#getString(IMolecularFormula, boolean)}
     */
    @Test
    void testGetString_IMolecularFormula_boolean() {
        IMolecularFormula mf1 = new MolecularFormula();
        mf1.addIsotope(builder.newInstance(IIsotope.class, "C"), 1);
        mf1.addIsotope(builder.newInstance(IIsotope.class, "H"), 4);

        Assertions.assertEquals("C1H4", getString(mf1, true));
    }

    /** Test if formulae group elements when not inserted simultaneously */
    @Test
    void testGetString_Isotopes() {
        IMolecularFormula mf1 = new MolecularFormula();
        mf1.addIsotope(builder.newInstance(IIsotope.class, "C", 12), 9);
        mf1.addIsotope(builder.newInstance(IIsotope.class, "C", 13), 1);
        mf1.addIsotope(builder.newInstance(IIsotope.class, "H"), 16);

        Assertions.assertEquals("C10H16", getString(mf1));
    }

    @Test
    void testGetMolecularFormula_String_IChemObjectBuilder() {
        IMolecularFormula molecularFormula = MolecularFormulaManipulator.getMolecularFormula("C10H16", builder);

        Assertions.assertEquals(26, getAtomCount(molecularFormula));
        Assertions.assertEquals(2, molecularFormula.getIsotopeCount());

    }

    /** Test formula summing */
    @Test
    void testGetMolecularFormula_String_IMolecularFormula() {

        IMolecularFormula mf1 = new MolecularFormula();
        mf1.addIsotope(builder.newInstance(IIsotope.class, "C"), 10);
        mf1.addIsotope(builder.newInstance(IIsotope.class, "H"), 16);

        Assertions.assertEquals(26, getAtomCount(mf1));
        Assertions.assertEquals(2, mf1.getIsotopeCount());

        IMolecularFormula mf2 = MolecularFormulaManipulator.getMolecularFormula("C11H17", mf1);

        Assertions.assertEquals(54, getAtomCount(mf2));
        Assertions.assertEquals(2, mf2.getIsotopeCount());
    }

    /** Test formula mass calculation */
    @Test
    void testGetMajorIsotopeMolecularFormula_String_IChemObjectBuilder() throws Exception {
        IMolecularFormula mf2 = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("C11H17", builder);

        Assertions.assertEquals(28, getAtomCount(mf2));
        Assertions.assertEquals(2, mf2.getIsotopeCount());
        IIsotope carbon = Isotopes.getInstance().getMajorIsotope("C");
        IIsotope hydrogen = Isotopes.getInstance().getMajorIsotope("H");
        double totalMass = carbon.getExactMass() * 11;
        totalMass += hydrogen.getExactMass() * 17;
        Assertions.assertEquals(totalMass, getTotalExactMass(mf2), 0.0000001);
    }

    /** test @link {@link MolecularFormulaManipulator#removeElement(IMolecularFormula, IElement)} */
    @Test
    void testRemoveElement_IMolecularFormula_IElement() {
        IMolecularFormula formula = new MolecularFormula();
        formula.addIsotope(builder.newInstance(IIsotope.class, "C"), 1);
        IIsotope fl = builder.newInstance(IIsotope.class, "F");
        IIsotope hy2 = builder.newInstance(IIsotope.class, "H");
        IIsotope hy1 = builder.newInstance(IIsotope.class, "H");
        hy2.setExactMass(2.014101778);
        formula.addIsotope(fl, 1);
        formula.addIsotope(hy1, 2);
        formula.addIsotope(hy2, 1);

        Assertions.assertEquals(4, formula.getIsotopeCount());

        formula = MolecularFormulaManipulator.removeElement(formula, builder.newInstance(IElement.class, "F"));

        Assertions.assertEquals(3, formula.getIsotopeCount());
        Assertions.assertEquals(4, getAtomCount(formula));

        formula = MolecularFormulaManipulator.removeElement(formula, builder.newInstance(IElement.class, "H"));

        Assertions.assertEquals(1, getAtomCount(formula));
        Assertions.assertEquals(1, formula.getIsotopeCount());

    }

    /**
     * Test total Exact Mass.
     */
    @Test
    void testGetTotalExactMass_IMolecularFormula() throws Exception {

        IMolecularFormula formula = new MolecularFormula();
        IIsotope carb = builder.newInstance(IIsotope.class, "C");
        carb.setExactMass(12.00);
        IIsotope cl = builder.newInstance(IIsotope.class, "Cl");
        cl.setExactMass(34.96885268);

        formula.addIsotope(carb);
        formula.addIsotope(cl);

        double totalExactMass = MolecularFormulaManipulator.getTotalExactMass(formula);

        Assertions.assertEquals(46.96885268, totalExactMass, 0.000001);
    }

    /**
     * Test total Exact Mass.
     *
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws CDKException
     */
    @Test
    void testGetTotalExactMassWithCharge_IMolecularFormula() throws Exception {

        IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("CH5O", builder);

        double totalExactMass = MolecularFormulaManipulator.getTotalExactMass(formula);
        Assertions.assertEquals(33.034040, totalExactMass, 0.0001);

        formula.setCharge(1);
        double totalExactMass2 = MolecularFormulaManipulator.getTotalExactMass(formula);
        Assertions.assertEquals(33.03349, totalExactMass2, 0.0001);
    }

    /**
     * Test total Exact Mass.
     *
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws CDKException
     */
    @Test
    void testGetTotalExactMassWithChargeNeg_IMolecularFormula() throws IOException, ClassNotFoundException {

        IMolecularFormula formula = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("H2PO4", builder);
        formula.setCharge(-1);
        double totalExactMass2 = MolecularFormulaManipulator.getTotalExactMass(formula);
        Assertions.assertEquals(96.96961875390926, totalExactMass2, 0.0001);
    }

    @Test
    void testGetNaturalExactMass_IMolecularFormula() throws Exception {
        IMolecularFormula formula = new MolecularFormula();
        formula.addIsotope(builder.newInstance(IIsotope.class, "C"));
        formula.addIsotope(builder.newInstance(IIsotope.class, "Cl"));

        double expectedMass = 0.0;
        expectedMass += Isotopes.getInstance().getNaturalMass(builder.newInstance(IElement.class, "C"));
        expectedMass += Isotopes.getInstance().getNaturalMass(builder.newInstance(IElement.class, "Cl"));

        double totalExactMass = MolecularFormulaManipulator.getNaturalExactMass(formula);
        Assertions.assertEquals(expectedMass, totalExactMass, 0.000001);
    }

    @Test
    void testGetTotalMassNumber_IMolecularFormula() throws Exception {
        IMolecularFormula formula = new MolecularFormula();
        formula.addIsotope(builder.newInstance(IIsotope.class, "C"));
        formula.addIsotope(builder.newInstance(IIsotope.class, "O"));

        double totalExactMass = MolecularFormulaManipulator.getTotalMassNumber(formula);
        Assertions.assertEquals(28, totalExactMass, 0.000001);
    }

    @Test
    void testGetMajorIsotopeMass_IMolecularFormula() throws Exception {
        IMolecularFormula formula = new MolecularFormula();
        formula.addIsotope(builder.newInstance(IIsotope.class, "C"));
        formula.addIsotope(builder.newInstance(IIsotope.class, "H"), 4);

        double expectedMass = 0.0;
        expectedMass += Isotopes.getInstance().getMajorIsotope("C").getExactMass();
        expectedMass += 4.0 * Isotopes.getInstance().getMajorIsotope("H").getExactMass();

        double totalExactMass = MolecularFormulaManipulator.getMajorIsotopeMass(formula);
        Assertions.assertEquals(expectedMass, totalExactMass, 0.000001);
    }

    /**
     * Test total Exact Mass. It is necessary to have added the
     * corresponding isotope before to calculate the exact mass.
     *
     */
    @Test
    void testBug_1944604() throws Exception {

        IMolecularFormula formula = new MolecularFormula();
        IIsotope carb = builder.newInstance(IIsotope.class, "C");

        formula.addIsotope(carb);

        Assertions.assertEquals("C1", getString(formula, true));

        double totalExactMass = MolecularFormulaManipulator.getTotalExactMass(formula);

        Assertions.assertEquals(12.0, totalExactMass, 0.000001);
    }

    /**
     * Test total natural abundance.
     */
    @Test
    void testGetTotalNaturalAbundance_IMolecularFormula() throws Exception {

        IMolecularFormula formula = new MolecularFormula();
        IIsotope carb = builder.newInstance(IIsotope.class, "C");
        carb.setNaturalAbundance(98.93);
        IIsotope cl = builder.newInstance(IIsotope.class, "Cl");
        cl.setNaturalAbundance(75.78);
        formula.addIsotope(carb);
        formula.addIsotope(cl);

        double totalAbudance = MolecularFormulaManipulator.getTotalNaturalAbundance(formula);

        Assertions.assertEquals(0.74969154, totalAbudance, 0.000001);
    }

    /**
     * Test total natural abundance.
     */
    @Test
    void testGetTotalNaturalAbundance_IMolecularFormula2() throws Exception {

        IMolecularFormula formula1 = new MolecularFormula();
        IIsotope br1 = builder.newInstance(IIsotope.class, "Br");
        br1.setNaturalAbundance(49.31);
        IIsotope br2 = builder.newInstance(IIsotope.class, "Br");
        br2.setNaturalAbundance(50.69);
        formula1.addIsotope(br1);
        formula1.addIsotope(br2);

        Assertions.assertEquals(2, formula1.getIsotopeCount(), 0.000001);
        double totalAbudance = MolecularFormulaManipulator.getTotalNaturalAbundance(formula1);
        Assertions.assertEquals(0.24995235, totalAbudance, 0.000001);
    }

    @Test
    void testGetTotalNaturalAbundance_IMolecularFormula3() throws Exception {
        IMolecularFormula formula2 = new MolecularFormula();
        IIsotope br1 = builder.newInstance(IIsotope.class, "Br");
        br1.setNaturalAbundance(50.69);
        IIsotope br2 = builder.newInstance(IIsotope.class, "Br");
        br2.setNaturalAbundance(50.69);
        formula2.addIsotope(br1);
        formula2.addIsotope(br2);

        Assertions.assertEquals(1, formula2.getIsotopeCount(), 0.000001);
        double totalAbudance = MolecularFormulaManipulator.getTotalNaturalAbundance(formula2);

        Assertions.assertEquals(0.25694761, totalAbudance, 0.000001);
    }

    @Test
    void testGetTotalNaturalAbundance_IMolecularFormula4() throws Exception {
        IMolecularFormula formula2 = new MolecularFormula();
        IIsotope br1 = builder.newInstance(IIsotope.class, "Br");
        br1.setNaturalAbundance(50.69);
        formula2.addIsotope(br1);
        formula2.addIsotope(br1);

        Assertions.assertEquals(1, formula2.getIsotopeCount());
        double totalAbudance = MolecularFormulaManipulator.getTotalNaturalAbundance(formula2);

        Assertions.assertEquals(0.25694761, totalAbudance, 0.000001);
    }

    /** Test Double-Bond-Equivalent (DBE) calculation */
    @Test
    void testGetDBE_IMolecularFormula() throws Exception {
        IMolecularFormula formula = new MolecularFormula();
        formula.addIsotope(builder.newInstance(IIsotope.class, "C"), 10);
        formula.addIsotope(builder.newInstance(IIsotope.class, "H"), 22);

        Assertions.assertEquals(0.0, getDBE(formula), 0.01);

        formula = new MolecularFormula();
        formula.addIsotope(builder.newInstance(IIsotope.class, "C"), 10);
        formula.addIsotope(builder.newInstance(IIsotope.class, "H"), 16);

        Assertions.assertEquals(3.0, getDBE(formula), 0.01);

        formula = new MolecularFormula();
        formula.addIsotope(builder.newInstance(IIsotope.class, "C"), 10);
        formula.addIsotope(builder.newInstance(IIsotope.class, "H"), 16);
        formula.addIsotope(builder.newInstance(IIsotope.class, "O"));

        Assertions.assertEquals(3.0, getDBE(formula), 0.01);

        formula = new MolecularFormula();
        formula.addIsotope(builder.newInstance(IIsotope.class, "C"), 10);
        formula.addIsotope(builder.newInstance(IIsotope.class, "H"), 19);
        formula.addIsotope(builder.newInstance(IIsotope.class, "N"));

        Assertions.assertEquals(2.0, getDBE(formula), 0.01);

    }

    @Test
    void testGetHTML_IMolecularFormula() {
        MolecularFormula formula = new MolecularFormula();
        formula.addIsotope(builder.newInstance(IIsotope.class, "C"), 8);
        formula.addIsotope(builder.newInstance(IIsotope.class, "H"), 10);
        formula.addIsotope(builder.newInstance(IIsotope.class, "Cl"), 2);
        formula.addIsotope(builder.newInstance(IIsotope.class, "O"), 2);

        Assertions.assertEquals("C<sub>8</sub>H<sub>10</sub>Cl<sub>2</sub>O<sub>2</sub>", getHTML(formula));
    }

    @Test
    void htmlFormulaDoesNotAddSubscriptForSingleElements() {
        MolecularFormula formula = new MolecularFormula();
        formula.addIsotope(builder.newInstance(IIsotope.class, "C"), 1);
        formula.addIsotope(builder.newInstance(IIsotope.class, "H"), 4);

        Assertions.assertEquals("CH<sub>4</sub>", getHTML(formula));
    }

    @Test
    void testGetHTML_IMolecularFormula_boolean_boolean() {
        MolecularFormula formula = new MolecularFormula();
        formula.addIsotope(builder.newInstance(IIsotope.class, "C"), 10);

        Assertions.assertEquals("C<sub>10</sub>", getHTML(formula, true, false));
        formula.setCharge(1);
        Assertions.assertEquals("C<sub>10</sub><sup>+</sup>", getHTML(formula, true, false));
        formula.setCharge(formula.getCharge() - 2);
        Assertions.assertEquals("C<sub>10</sub><sup>–</sup>", getHTML(formula, true, false));
    }

    @Test
    void nullIsotopeLabels() {
        MolecularFormula formula = new MolecularFormula();
        formula.addIsotope(builder.newInstance(IIsotope.class, "C"), 10);

        Assertions.assertEquals("C<sub>10</sub>", getHTML(formula, true, false));
        formula.setCharge(1);
        Assertions.assertEquals("C<sub>10</sub><sup>+</sup>", getHTML(formula, true, true));
        formula.setCharge(formula.getCharge() - 2);
        Assertions.assertEquals("C<sub>10</sub><sup>–</sup>", getHTML(formula, true, true));
    }

    @Test
    void testGetHTML_IMolecularFormula_arrayString_boolean_boolean() {
        IMolecularFormula formula = new MolecularFormula();
        formula.addIsotope(builder.newInstance(IIsotope.class, "C"), 2);
        formula.addIsotope(builder.newInstance(IIsotope.class, "H"), 2);

        String[] newOrder = new String[2];
        newOrder[0] = "H";
        newOrder[1] = "C";

        Assertions.assertEquals("H<sub>2</sub>C<sub>2</sub>", getHTML(formula, newOrder, false, false));
    }

    @Test
    void testGetHTML_IMolecularFormulaWithIsotope() {
        MolecularFormula formula = new MolecularFormula();
        formula.addIsotope(ifac.getMajorIsotope("C"), 2);
        formula.addIsotope(ifac.getMajorIsotope("H"), 6);
        Assertions.assertEquals("<sup>12</sup>C<sub>2</sub><sup>1</sup>H<sub>6</sub>", getHTML(formula, false, true));
    }

    @Test
    void testGetHTML_IMolecularFormulaWithIsotopeAndCharge() {
        MolecularFormula formula = new MolecularFormula();
        formula.addIsotope(ifac.getMajorIsotope("C"), 2);
        formula.addIsotope(ifac.getMajorIsotope("H"), 6);
        formula.setCharge(1);
        Assertions.assertEquals("<sup>12</sup>C<sub>2</sub><sup>1</sup>H<sub>6</sub><sup>+</sup>", getHTML(formula, true, true));
    }

    @Test
    void testGetMolecularFormula_IAtomContainer() {
        IAtomContainer ac = builder.newInstance(IAtomContainer.class);
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addAtom(builder.newInstance(IAtom.class, "H"));
        ac.addAtom(builder.newInstance(IAtom.class, "H"));
        ac.addAtom(builder.newInstance(IAtom.class, "H"));
        ac.addAtom(builder.newInstance(IAtom.class, "H"));

        IMolecularFormula mf1 = MolecularFormulaManipulator.getMolecularFormula(ac);

        IMolecularFormula mf2 = new MolecularFormula();
        mf2.addIsotope(builder.newInstance(IIsotope.class, "C"), 2);
        mf2.addIsotope(builder.newInstance(IIsotope.class, "H"), 4);

        Assertions.assertEquals(getAtomCount(mf2), getAtomCount(mf1));
        Assertions.assertEquals(mf2.getIsotopeCount(), mf1.getIsotopeCount());
        IElement elemC = builder.newInstance(IElement.class, "C");
        IElement elemH = builder.newInstance(IElement.class, "H");
        Assertions.assertEquals(mf2.getIsotopeCount(builder.newInstance(IIsotope.class, elemC)), mf1.getIsotopeCount(builder.newInstance(IIsotope.class, elemC)));
        Assertions.assertEquals(mf2.getIsotopeCount(builder.newInstance(IIsotope.class, elemH)), mf1.getIsotopeCount(builder.newInstance(IIsotope.class, elemH)));
        Assertions.assertEquals(getElementCount(mf2, elemC), getElementCount(mf1, elemC));
        Assertions.assertEquals(getElementCount(mf2, elemH), getElementCount(mf1, elemH));

    }

    @Test
    void testGetMolecularFormula_IAtomNullCharge() {
        IAtomContainer ac = builder.newInstance(IAtomContainer.class);
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.getAtom(0).setFormalCharge((Integer) CDKConstants.UNSET);
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addAtom(builder.newInstance(IAtom.class, "H"));
        ac.addAtom(builder.newInstance(IAtom.class, "H"));
        ac.addAtom(builder.newInstance(IAtom.class, "H"));
        ac.addAtom(builder.newInstance(IAtom.class, "H"));

        IMolecularFormula mf1 = MolecularFormulaManipulator.getMolecularFormula(ac);
        Assertions.assertNotNull(mf1);
    }

    @Test
    void testGetMolecularFormula_IAtomContainer_withCharge() {
        IAtomContainer ac = builder.newInstance(IAtomContainer.class);
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.getAtom(0).setFormalCharge(1);
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addAtom(builder.newInstance(IAtom.class, "H"));
        ac.addAtom(builder.newInstance(IAtom.class, "H"));
        ac.addAtom(builder.newInstance(IAtom.class, "H"));

        IMolecularFormula mf1 = MolecularFormulaManipulator.getMolecularFormula(ac);

        Assertions.assertEquals(1, mf1.getCharge(), 0.000);
    }

    @Test
    void testGetMolecularFormula_IAtomContainer_IMolecularFormula() {
        IAtomContainer ac = builder.newInstance(IAtomContainer.class);
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addAtom(builder.newInstance(IAtom.class, "H"));
        ac.addAtom(builder.newInstance(IAtom.class, "H"));
        ac.addAtom(builder.newInstance(IAtom.class, "H"));
        ac.addAtom(builder.newInstance(IAtom.class, "H"));

        IMolecularFormula mf1 = MolecularFormulaManipulator.getMolecularFormula(ac, new MolecularFormula());

        IMolecularFormula mf2 = new MolecularFormula();
        mf2.addIsotope(builder.newInstance(IIsotope.class, "C"), 2);
        mf2.addIsotope(builder.newInstance(IIsotope.class, "H"), 4);

        Assertions.assertEquals(getAtomCount(mf2), getAtomCount(mf1));
        Assertions.assertEquals(mf2.getIsotopeCount(), mf1.getIsotopeCount());
        IElement elemC = builder.newInstance(IElement.class, "C");
        IElement elemH = builder.newInstance(IElement.class, "H");
        Assertions.assertEquals(mf2.getIsotopeCount(builder.newInstance(IIsotope.class, elemC)), mf1.getIsotopeCount(builder.newInstance(IIsotope.class, elemC)));
        Assertions.assertEquals(mf2.getIsotopeCount(builder.newInstance(IIsotope.class, elemH)), mf1.getIsotopeCount(builder.newInstance(IIsotope.class, elemH)));
        Assertions.assertEquals(getElementCount(mf2, elemC), getElementCount(mf1, elemC));
        Assertions.assertEquals(getElementCount(mf2, elemH), getElementCount(mf1, elemH));

    }

    @Test
    void testGetMolecularFormula_IAtomContainerIMolecularFormula_2() {
        IAtomContainer ac = builder.newInstance(IAtomContainer.class);
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addAtom(builder.newInstance(IAtom.class, "H"));
        ac.addAtom(builder.newInstance(IAtom.class, "H"));
        ac.addAtom(builder.newInstance(IAtom.class, "H"));
        ac.addAtom(builder.newInstance(IAtom.class, "H"));

        IMolecularFormula mf0 = new MolecularFormula();
        mf0.addIsotope(builder.newInstance(IIsotope.class, "C"), 2);
        mf0.addIsotope(builder.newInstance(IIsotope.class, "H"), 5);

        IMolecularFormula mf1 = MolecularFormulaManipulator.getMolecularFormula(ac, mf0);

        IMolecularFormula mf2 = new MolecularFormula();
        mf2.addIsotope(builder.newInstance(IIsotope.class, "C"), 4);
        mf2.addIsotope(builder.newInstance(IIsotope.class, "H"), 9);

        Assertions.assertEquals(getAtomCount(mf2), getAtomCount(mf1));
        Assertions.assertEquals(mf2.getIsotopeCount(), mf1.getIsotopeCount());
        IElement elemC = builder.newInstance(IElement.class, "C");
        IElement elemH = builder.newInstance(IElement.class, "H");
        Assertions.assertEquals(mf2.getIsotopeCount(builder.newInstance(IIsotope.class, elemC)), mf1.getIsotopeCount(builder.newInstance(IIsotope.class, elemC)));
        Assertions.assertEquals(mf2.getIsotopeCount(builder.newInstance(IIsotope.class, elemH)), mf1.getIsotopeCount(builder.newInstance(IIsotope.class, elemH)));
        Assertions.assertEquals(getElementCount(mf2, elemC), getElementCount(mf1, elemC));
        Assertions.assertEquals(getElementCount(mf2, elemH), getElementCount(mf1, elemH));

    }

    @Test
    void testGetAtomContainer_IMolecularFormula() {

        IMolecularFormula mf2 = new MolecularFormula();
        mf2.addIsotope(builder.newInstance(IIsotope.class, "C"), 2);
        mf2.addIsotope(builder.newInstance(IIsotope.class, "H"), 4);

        IAtomContainer ac = MolecularFormulaManipulator.getAtomContainer(mf2);

        Assertions.assertEquals(6, ac.getAtomCount());

    }

    @Test
    void testGetAtomContainer_IMolecularFormula_IAtomContainer() {

        IMolecularFormula mf2 = new MolecularFormula();
        mf2.addIsotope(builder.newInstance(IIsotope.class, "C"), 2);
        mf2.addIsotope(builder.newInstance(IIsotope.class, "H"), 4);

        IAtomContainer ac = MolecularFormulaManipulator
                .getAtomContainer(mf2, builder.newInstance(IAtomContainer.class));

        Assertions.assertEquals(6, ac.getAtomCount());

    }

    @Test
    void testGetAtomContainer_String_IChemObjectBuilder() {
        String mf = "C2H4";
        IAtomContainer atomContainer = MolecularFormulaManipulator.getAtomContainer(mf,
                DefaultChemObjectBuilder.getInstance());
        Assertions.assertEquals(6, atomContainer.getAtomCount());
    }

    /**
     * @cdk.bug 1296
     */
    @Test
    void testGetAtomContainer_AddsAtomicNumbers() {
        IMolecularFormula mf2 = new MolecularFormula();
        mf2.addIsotope(builder.newInstance(IIsotope.class, "C"), 2);
        mf2.addIsotope(builder.newInstance(IIsotope.class, "H"), 4);
        IAtomContainer ac = MolecularFormulaManipulator
                .getAtomContainer(mf2, builder.newInstance(IAtomContainer.class));
        Assertions.assertEquals(6, ac.getAtomCount());
        Assertions.assertNotNull(ac.getAtom(0).getAtomicNumber());
        for (IAtom atom : ac.atoms()) {
            if (atom.getAtomicNumber() == IElement.C)
                Assertions.assertEquals(6, atom.getAtomicNumber().intValue());
            else if (atom.getAtomicNumber() == IElement.H)
                Assertions.assertEquals(1, atom.getAtomicNumber().intValue());
            else
                Assertions.fail("Unexcepted element: " + atom.getSymbol());
        }
    }

    @Test
    void testMolecularFormulaIAtomContainer_to_IAtomContainer2() {
        IAtomContainer ac = builder.newInstance(IAtomContainer.class);
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addAtom(builder.newInstance(IAtom.class, "C"));
        ac.addAtom(builder.newInstance(IAtom.class, "H"));
        ac.addAtom(builder.newInstance(IAtom.class, "H"));
        ac.addAtom(builder.newInstance(IAtom.class, "H"));
        ac.addAtom(builder.newInstance(IAtom.class, "H"));

        IMolecularFormula mf2 = new MolecularFormula();
        mf2.addIsotope(builder.newInstance(IIsotope.class, "C"), 2);
        mf2.addIsotope(builder.newInstance(IIsotope.class, "H"), 4);

        IAtomContainer ac2 = MolecularFormulaManipulator.getAtomContainer(mf2,
                builder.newInstance(IAtomContainer.class));

        Assertions.assertEquals(ac2.getAtomCount(), ac2.getAtomCount());
        Assertions.assertEquals(ac2.getAtom(0).getSymbol(), ac2.getAtom(0).getSymbol());
        Assertions.assertEquals(ac2.getAtom(5).getSymbol(), ac2.getAtom(5).getSymbol());

    }

    @Test
    void testElements_IMolecularFormula() {

        IMolecularFormula formula = new MolecularFormula();
        formula.addIsotope(builder.newInstance(IIsotope.class, "C"), 1);
        formula.addIsotope(builder.newInstance(IIsotope.class, "H"), 2);

        IIsotope br1 = builder.newInstance(IIsotope.class, "Br");
        br1.setNaturalAbundance(50.69);
        formula.addIsotope(br1);
        IIsotope br2 = builder.newInstance(IIsotope.class, "Br");
        br2.setNaturalAbundance(50.69);
        formula.addIsotope(br2);

        List<IElement> elements = MolecularFormulaManipulator.elements(formula);

        Assertions.assertEquals(5, getAtomCount(formula));
        Assertions.assertEquals(3, elements.size());
    }

    @Test
    void testCompare_Charge() {

        IMolecularFormula formula1 = new MolecularFormula();
        formula1.addIsotope(builder.newInstance(IIsotope.class, "C"), 1);
        formula1.addIsotope(builder.newInstance(IIsotope.class, "H"), 2);

        IMolecularFormula formula2 = new MolecularFormula();
        formula2.addIsotope(builder.newInstance(IIsotope.class, "C"), 1);
        formula2.addIsotope(builder.newInstance(IIsotope.class, "H"), 2);

        IMolecularFormula formula3 = new MolecularFormula();
        formula3.addIsotope(builder.newInstance(IIsotope.class, "C"), 1);
        formula3.addIsotope(builder.newInstance(IIsotope.class, "H"), 2);
        formula3.setCharge(0);

        Assertions.assertTrue(compare(formula1, formula2));
        Assertions.assertFalse(compare(formula1, formula3));

    }

    @Test
    void testCompare_NumberIsotope() {

        IMolecularFormula formula1 = new MolecularFormula();
        formula1.addIsotope(builder.newInstance(IIsotope.class, "C"), 1);
        formula1.addIsotope(builder.newInstance(IIsotope.class, "H"), 2);

        IMolecularFormula formula2 = new MolecularFormula();
        formula2.addIsotope(builder.newInstance(IIsotope.class, "C"), 1);
        formula2.addIsotope(builder.newInstance(IIsotope.class, "H"), 2);

        IMolecularFormula formula3 = new MolecularFormula();
        formula3.addIsotope(builder.newInstance(IIsotope.class, "C"), 1);
        formula3.addIsotope(builder.newInstance(IIsotope.class, "H"), 3);

        Assertions.assertTrue(compare(formula1, formula2));
        Assertions.assertFalse(compare(formula1, formula3));

    }

    @Test
    void testCompare_IMolecularFormula_IMolecularFormula() {

        IMolecularFormula formula1 = new MolecularFormula();
        formula1.addIsotope(builder.newInstance(IIsotope.class, "C"), 1);
        formula1.addIsotope(builder.newInstance(IIsotope.class, "H"), 2);

        IMolecularFormula formula2 = new MolecularFormula();
        formula2.addIsotope(builder.newInstance(IIsotope.class, "C"), 1);
        formula2.addIsotope(builder.newInstance(IIsotope.class, "H"), 2);

        IMolecularFormula formula3 = new MolecularFormula();
        formula3.addIsotope(builder.newInstance(IIsotope.class, "C"), 1);
        IIsotope hyd = builder.newInstance(IIsotope.class, "H");
        hyd.setExactMass(2.002334234);
        formula3.addIsotope(hyd, 2);

        Assertions.assertTrue(compare(formula1, formula2));
        Assertions.assertFalse(compare(formula1, formula3));

    }

    @Test
    void testGetHeavyElements_IMolecularFormula() {
        IMolecularFormula formula = new MolecularFormula();
        formula.addIsotope(builder.newInstance(IIsotope.class, "C"), 10);
        formula.addIsotope(builder.newInstance(IIsotope.class, "H"), 16);
        Assertions.assertEquals(1, getHeavyElements(formula).size());
    }

    @Test
    void testGetHeavyElements_IMolecularFormula_2() {
        IMolecularFormula formula = MolecularFormulaManipulator.getMolecularFormula("CH3OH", builder);
        Assertions.assertEquals(2, getHeavyElements(formula).size());
    }

    /**
     * Test if the elements-ordered-by-probability are in the expected order.
     */
    @Test
    void testGenerateOrderEle() {
        String[] listElements = new String[]{
                // Elements of life
                "C", "H", "O", "N", "Si", "P", "S", "F", "Cl",

                "Br", "I", "Sn", "B", "Pb", "Tl", "Ba", "In", "Pd", "Pt", "Os", "Ag", "Zr", "Se", "Zn", "Cu", "Ni",
                "Co", "Fe", "Cr", "Ti", "Ca", "K", "Al", "Mg", "Na", "Ce", "Hg", "Au", "Ir", "Re", "W", "Ta", "Hf",
                "Lu", "Yb", "Tm", "Er", "Ho", "Dy", "Tb", "Gd", "Eu", "Sm", "Pm", "Nd", "Pr", "La", "Cs", "Xe", "Te",
                "Sb", "Cd", "Rh", "Ru", "Tc", "Mo", "Nb", "Y", "Sr", "Rb", "Kr", "As", "Ge", "Ga", "Mn", "V", "Sc",
                "Ar", "Ne", "He", "Be", "Li",

                // rest of periodic table, in atom-number order.
                "Bi", "Po", "At", "Rn",
                // row-7 elements (including f-block)
                "Fr", "Ra", "Ac", "Th", "Pa", "U", "Np", "Pu", "Am", "Cm", "Bk", "Cf", "Es", "Fm", "Md", "No", "Lr",
                "Rf", "Db", "Sg", "Bh", "Hs", "Mt", "Ds", "Rg", "Cn",

                // The "odd one out": an unspecified R-group
                "R"};

        String[] arrayGenerated = MolecularFormulaManipulator.generateOrderEle();
        List<String> listGenerated = Arrays.asList(arrayGenerated);
        Assertions.assertEquals(113, listGenerated.size());

        for (String element : listElements) {
            Assertions.assertTrue(listGenerated.contains(element), "Element missing from generateOrderEle: " + element);
        }
    }

    /**
     * TODO: REACT: Introduce method
     *
     * @cdk.bug 2672696
     */
    @Test
    void testGetHillString_IMolecularFormula() {
        IMolecularFormula formula = MolecularFormulaManipulator.getMolecularFormula("CH3OH", builder);
        String listGenerated = MolecularFormulaManipulator.getHillString(formula);
        Assertions.assertEquals("CH4O", listGenerated);

        formula = MolecularFormulaManipulator.getMolecularFormula("CH3CH2Br", builder);
        listGenerated = MolecularFormulaManipulator.getHillString(formula);
        Assertions.assertEquals("C2H5Br", listGenerated);

        formula = MolecularFormulaManipulator.getMolecularFormula("HCl", builder);
        listGenerated = MolecularFormulaManipulator.getHillString(formula);
        Assertions.assertEquals("ClH", listGenerated);

        formula = MolecularFormulaManipulator.getMolecularFormula("HBr", builder);
        listGenerated = MolecularFormulaManipulator.getHillString(formula);
        Assertions.assertEquals("BrH", listGenerated);
    }

    /**
     * Tests that an atom which has not be configured with isotope information,
     * provides the correct exact mass.
     * @cdk.bug 1944604
     */
    @Test
    void testSingleAtomFromSmiles() throws CDKException {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C"));

        // previously performed inside SmilesParser
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        CDKHydrogenAdder.getInstance(DefaultChemObjectBuilder.getInstance()).addImplicitHydrogens(mol);

        IMolecularFormula mf = MolecularFormulaManipulator.getMolecularFormula(mol);
        double exactMass = MolecularFormulaManipulator.getTotalExactMass(mf);
        Assertions.assertEquals(16.0313, exactMass, 0.0001);
    }

    @Test
    void testSingleAtom() {
        String formula = "CH4";
        IMolecularFormula mf = MolecularFormulaManipulator.getMolecularFormula(formula, builder);
        Assertions.assertEquals(1, getIsotopes(mf, mf.getBuilder().newInstance(IElement.class, "C")).size());
    }

    @Test
    void testSimplifyMolecularFormula_String() {
        String formula = "C1H41.H2O";
        String simplifyMF = MolecularFormulaManipulator.simplifyMolecularFormula(formula);
        Assertions.assertEquals("C1H43O", simplifyMF);
    }

    @Test
    void testSimplifyMolecularFormula_String2() {
        String formula = "CH41.H2O";
        String simplifyMF = MolecularFormulaManipulator.simplifyMolecularFormula(formula);
        Assertions.assertEquals("CH43O", simplifyMF);
    }

    @Test
    void testSimplifygetMF() {
        String formula = "CH4.H2O";
        IMolecularFormula formula1 = new MolecularFormula();
        formula1.addIsotope(builder.newInstance(IIsotope.class, "C"), 1);
        formula1.addIsotope(builder.newInstance(IIsotope.class, "H"), 6);
        formula1.addIsotope(builder.newInstance(IIsotope.class, "O"), 1);
        IMolecularFormula ff = MolecularFormulaManipulator.getMolecularFormula(formula, builder);
        Assertions.assertTrue(compare(formula1,
                                      getMolecularFormula(formula, builder)));
        Assertions.assertEquals("CH6O", getString(ff));
    }

    @Test
    void testSpace() {
        String formula = "C17H21NO. C7H6O3";
        String simplifyMF = MolecularFormulaManipulator.simplifyMolecularFormula(formula);
        Assertions.assertEquals("C24H27NO4", simplifyMF);
    }

    /** Test molecule simplification */
    @Test
    void test0() {
        String formula = "Fe.(C6H11O7)3";
        String simplifyMF = MolecularFormulaManipulator.simplifyMolecularFormula(formula);
        Assertions.assertEquals("FeC18H33O21", simplifyMF);
    }

    /** Test molecule simplification */
    @Test
    void test1() {
        String formula = "(C6H11O7)3.Fe";
        String simplifyMF = MolecularFormulaManipulator.simplifyMolecularFormula(formula);
        Assertions.assertEquals("C18H33O21Fe", simplifyMF);
    }

    /** Test molecule simplification */
    @Test
    void test2() {
        String formula = "C14H14N2.2HCl";
        String simplifyMF = MolecularFormulaManipulator.simplifyMolecularFormula(formula);
        Assertions.assertEquals("C14H16N2Cl2", simplifyMF);
    }

    /** Test molecule simplification */
    @Test
    void test3() {
        String formula = "(C27H33N3O8)2.2HNO3.3H2O";
        String simplifyMF = MolecularFormulaManipulator.simplifyMolecularFormula(formula);
        Assertions.assertEquals("C54H74N8O25", simplifyMF);
    }

    /** Test if formula-comparison is simplify-independant */
    @Test
    void test4() {
        String formula = "(C27H33N3O8)2.2HNO3.3H2O";
        IMolecularFormula formula1 = new MolecularFormula();
        formula1.addIsotope(builder.newInstance(IIsotope.class, "C"), 54);
        formula1.addIsotope(builder.newInstance(IIsotope.class, "H"), 74);
        formula1.addIsotope(builder.newInstance(IIsotope.class, "O"), 25);
        formula1.addIsotope(builder.newInstance(IIsotope.class, "N"), 8);
        IMolecularFormula ff = MolecularFormulaManipulator.getMolecularFormula(formula, builder);
        Assertions.assertTrue(compare(formula1,
                                      getMolecularFormula(formula, builder)));
        Assertions.assertEquals("C54H74N8O25", getString(ff));
    }

    /** Test if formula-comparison is simplify-independant */
    @Test
    void test5() {
        String formula = "[SO3]2-";
        IMolecularFormula formula1 = new MolecularFormula();
        formula1.addIsotope(builder.newInstance(IIsotope.class, "S"), 1);
        formula1.addIsotope(builder.newInstance(IIsotope.class, "O"), 3);
        formula1.setCharge(-2);
        IMolecularFormula ff = MolecularFormulaManipulator.getMolecularFormula(formula, builder);
        Assertions.assertTrue(compare(formula1,
                                      getMolecularFormula(formula, builder)));
        Assertions.assertEquals("[O3S]2-", getString(ff));
        Assertions.assertEquals(-2, ff.getCharge(), 0.00001);
    }

    /** Test if formula-comparison is simplify-independant */
    @Test
    void test6() {
        String formula = "(CH3)2";
        IMolecularFormula formula1 = new MolecularFormula();
        formula1.addIsotope(builder.newInstance(IIsotope.class, "C"), 2);
        formula1.addIsotope(builder.newInstance(IIsotope.class, "H"), 6);
        IMolecularFormula ff = MolecularFormulaManipulator.getMolecularFormula(formula, builder);
        Assertions.assertTrue(compare(formula1,
                                      getMolecularFormula(formula, builder)));
        Assertions.assertEquals("C2H6", getString(ff));
    }

    /** Test if formula-comparison is simplify-independant */
    @Test
    void testWithH_Initial() {
        String formula = "HC5H11NO2H";
        IMolecularFormula formula1 = new MolecularFormula();
        formula1.addIsotope(builder.newInstance(IIsotope.class, "C"), 5);
        formula1.addIsotope(builder.newInstance(IIsotope.class, "H"), 13);
        formula1.addIsotope(builder.newInstance(IIsotope.class, "N"), 1);
        formula1.addIsotope(builder.newInstance(IIsotope.class, "O"), 2);
        IMolecularFormula ff = MolecularFormulaManipulator.getMolecularFormula(formula, builder);
        Assertions.assertTrue(compare(formula1,
                                      getMolecularFormula(formula, builder)));
        Assertions.assertEquals("C5H13NO2", getString(ff));
    }

    /**
     * @cdk.bug 3071473
     */
    @Test
    void testFromMol() throws Exception {
        String filename = "formulatest.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assertions.assertNotNull(chemFile);
        List<IAtomContainer> mols = ChemFileManipulator.getAllAtomContainers(chemFile);
        IAtomContainer mol = mols.get(0);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        CDKHydrogenAdder ha = CDKHydrogenAdder.getInstance(DefaultChemObjectBuilder.getInstance());
        ha.addImplicitHydrogens(mol);
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);

        IMolecularFormula molecularFormula = MolecularFormulaManipulator.getMolecularFormula(mol);
        String formula2 = MolecularFormulaManipulator.getString(molecularFormula);
        Assertions.assertTrue(formula2.equals("C35H64N3O21P3S"));
    }

    /**
     * @cdk.bug 3340660
     */
    @Test
    void testHelium() {
        IAtomContainer helium = new AtomContainer();
        helium.addAtom(new Atom("He"));

        IMolecularFormula formula = MolecularFormulaManipulator.getMolecularFormula(helium);
        Assertions.assertNotNull(formula);
        Assertions.assertEquals("He", getString(formula));
    }

    /**
     * @cdk.bug 3340660
     */
    @Test
    void testAmericum() {
        IAtomContainer helium = new AtomContainer();
        helium.addAtom(new Atom("Am"));

        IMolecularFormula formula = MolecularFormulaManipulator.getMolecularFormula(helium);
        Assertions.assertNotNull(formula);
        Assertions.assertEquals("Am", getString(formula));
    }

    /**
     * @cdk.bug 2983334
     */
    @Test
    void testImplicitH() throws Exception {

        CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());

        IAtomContainer mol = TestMoleculeFactory.makeBenzene();

        IMolecularFormula f = MolecularFormulaManipulator.getMolecularFormula(mol);
        Assertions.assertEquals("C6", getString(f));

        Assertions.assertEquals(6, mol.getAtomCount());
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        adder.addImplicitHydrogens(mol);
        Assertions.assertEquals(6, mol.getAtomCount());
        f = MolecularFormulaManipulator.getMolecularFormula(mol);
        Assertions.assertEquals("C6H6", getString(f));

    }

    @Test
    void noNullPointerExceptionForExactMassOfRGroups() throws Exception {
        IMolecularFormula formula = new MolecularFormula();
        formula.addIsotope(new Isotope("C"));
        formula.addIsotope(new Isotope("H"), 3);
        formula.addIsotope(new Isotope("R"));
        assertThat(MolecularFormulaManipulator.getTotalExactMass(formula),
                   closeTo(15.0234, 0.01));
    }

    @Test
    void noNullPointerExceptionForMassOfRGroups() throws Exception {
        IMolecularFormula formula = new MolecularFormula();
        formula.addIsotope(new Isotope("C"));
        formula.addIsotope(new Isotope("H"), 3);
        formula.addIsotope(new Isotope("R"));
        assertThat(MolecularFormulaManipulator.getTotalMassNumber(formula),
                   closeTo(15.0, 0.01));
    }

    @Test
    void noNullPointerExceptionForMajorMassOfRGroups() throws Exception {
        IMolecularFormula formula = new MolecularFormula();
        formula.addIsotope(new Isotope("C"));
        formula.addIsotope(new Isotope("H"), 3);
        formula.addIsotope(new Isotope("R"));
        assertThat(MolecularFormulaManipulator.getMajorIsotopeMass(formula),
                   closeTo(15.0234, 0.01));
    }

    @Test
    void noNullPointerForStaticIsotopes() throws Exception {
        Isotopes is = Isotopes.getInstance();
        IIsotope carbon = is.getMajorIsotope("C");
        MolecularFormula mf = new MolecularFormula();
        mf.addIsotope(carbon, 10);
        MolecularFormulaManipulator.getNaturalExactMass(mf);
    }

    @Test
    void acceptMinusAsInput() throws Exception {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        IMolecularFormula mf = MolecularFormulaManipulator.getMolecularFormula("[PO4]3–",
                                                                               bldr);
        assertThat(mf.getCharge(), is(-3));
    }

    @Test
    void deprotonatePhenol() throws Exception {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        IMolecularFormula mf = MolecularFormulaManipulator.getMolecularFormula("C6H6O", bldr);
        Assertions.assertTrue(adjustProtonation(mf, -1));
        assertThat(MolecularFormulaManipulator.getString(mf), is("[C6H5O]-"));
        assertThat(mf.getCharge(), is(-1));
    }

    @Test
    void protonatePhenolate() throws Exception {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        IMolecularFormula mf = MolecularFormulaManipulator.getMolecularFormula("[C6H5O]-", bldr);
        Assertions.assertTrue(adjustProtonation(mf, +1));
        assertThat(MolecularFormulaManipulator.getString(mf), is("C6H6O"));
        assertThat(mf.getCharge(), is(0));
        assertThat(mf.getIsotopeCount(), is(3));
    }

    @Test
    void protonatePhenolateMajorIsotopes() throws Exception {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        IMolecularFormula mf = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("[C6H5O]-", bldr);
        Assertions.assertTrue(adjustProtonation(mf, +1));
        assertThat(MolecularFormulaManipulator.getString(mf), is("C6H6O"));
        assertThat(mf.getCharge(), is(0));
        assertThat(mf.getIsotopeCount(), is(3));
    }

    @Test
    void deprontateHCl() throws Exception {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        IMolecularFormula mf = MolecularFormulaManipulator.getMolecularFormula("HCl", bldr);
        Assertions.assertTrue(adjustProtonation(mf, -1));
        assertThat(MolecularFormulaManipulator.getString(mf), is("[Cl]-"));
        assertThat(mf.getCharge(), is(-1));
        assertThat(mf.getIsotopeCount(), is(1));
    }

    @Test
    void prontateChloride() throws Exception {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        IMolecularFormula mf = MolecularFormulaManipulator.getMolecularFormula("[Cl]-", bldr);
        Assertions.assertTrue(adjustProtonation(mf, +1));
        assertThat(MolecularFormulaManipulator.getString(mf), is("ClH"));
        assertThat(mf.getCharge(), is(0));
        assertThat(mf.getIsotopeCount(), is(2));
    }

    @Test
    void deprontateChloride() throws Exception {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        IMolecularFormula mf = MolecularFormulaManipulator.getMolecularFormula("[Cl]-", bldr);
        Assertions.assertFalse(adjustProtonation(mf, -1));
    }

    @Test
    void protonateDeuteratedPhenolate() throws Exception {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        IMolecularFormula mf = bldr.newInstance(IMolecularFormula.class);
        // [C6DH4O]- (parser not good enough ATM so need to create like this)
        IIsotope deuterium = Isotopes.getInstance().getIsotope("H", 2);
        IIsotope hydrogen = Isotopes.getInstance().getMajorIsotope(1);
        mf.addIsotope(deuterium, 1);
        mf.addIsotope(hydrogen, 4);
        mf.addIsotope(Isotopes.getInstance().getMajorIsotope(6), 6);
        mf.addIsotope(Isotopes.getInstance().getMajorIsotope(8), 1);
        mf.setCharge(-1);
        Assertions.assertTrue(adjustProtonation(mf, +1));
        assertThat(MolecularFormulaManipulator.getString(mf), is("C6H6O"));
        assertThat(mf.getCharge(), is(0));
        assertThat(mf.getIsotopeCount(), is(4));
        assertThat(mf.getIsotopeCount(deuterium), is(1));
        assertThat(mf.getIsotopeCount(hydrogen), is(5));
    }

    @Test
    void testMassNumberDisplay() throws Exception {
        IsotopeFactory ifac = Isotopes.getInstance();
        IIsotope br81 = ifac.getIsotope("Br", 81);

        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        IMolecularFormula mf = bldr.newInstance(IMolecularFormula.class);

        mf.addIsotope(new Atom("C"), 7);
        mf.addIsotope(new Atom("O"), 3);
        mf.addIsotope(new Atom("H"), 3);
        mf.addIsotope(new Atom("Br"), 1);
        mf.addIsotope(ifac.getIsotope("Br", 81), 1);

        assertThat(MolecularFormulaManipulator.getString(mf, false, false), is("C7H3Br2O3"));
        assertThat(MolecularFormulaManipulator.getString(mf, false, true), is("C7H3Br[81]BrO3"));
    }

    @Test
    void largeNumberOfAtoms() {
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        assertThat(getAtomCount(getMolecularFormula("C9999999", builder)),
                is(9999999));
    }

    @Test
    void tooLargeNumberOfAtoms() {
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        Assertions.assertThrows(NumberFormatException.class,
                                () -> {
                                    getAtomCount(getMolecularFormula("C10000000", builder)); // should throw
                                });
    }

    @Test
    void testMassNumberDisplayWithDefinedIsotopes() throws Exception {
        IsotopeFactory ifac = Isotopes.getInstance();

        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        IMolecularFormula mf = bldr.newInstance(IMolecularFormula.class);

        mf.addIsotope(ifac.getMajorIsotope("C"), 7);
        mf.addIsotope(ifac.getMajorIsotope("O"), 3);
        mf.addIsotope(ifac.getMajorIsotope("H"), 3);
        mf.addIsotope(ifac.getMajorIsotope("Br"), 1);
        mf.addIsotope(ifac.getIsotope("Br", 81), 1);
        Isotopes.clearMajorIsotopes(mf);
        assertThat(MolecularFormulaManipulator.getString(mf, false, false), is("C7H3Br2O3"));
        assertThat(MolecularFormulaManipulator.getString(mf, false, true), is("C7H3Br[81]BrO3"));
    }

    @Test
    void parseMFMass() throws Exception {
        String str = "C7H3[81]BrBrO3";
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        IMolecularFormula mf = MolecularFormulaManipulator.getMolecularFormula(str, builder);
        assertThat(MolecularFormulaManipulator.getString(mf, false, true),
            is("C7H3Br[81]BrO3"));
    }

    @Test
    void testRoundTripCharge() {
        String f = "[C3H7]+";
        IMolecularFormula m =
                MolecularFormulaManipulator.getMolecularFormula(f,
                                                                SilentChemObjectBuilder.getInstance());
        assertThat(MolecularFormulaManipulator.getString(m), is("[C3H7]+"));
    }

    @Test
    void getMostAbundant() {
        IMolecularFormula mf = new MolecularFormula();
        mf.addIsotope(new Atom("C"), 6);
        mf.addIsotope(new Atom("Br"), 6);
        IMolecularFormula mamf = MolecularFormulaManipulator.getMostAbundant(mf);
        assertThat(MolecularFormulaManipulator.getString(mamf, false, true),
                   is("[12]C6[79]Br3[81]Br3"));
    }


    private static void assertMass(String str, double expMass, int flav) {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        IMolecularFormula mf =
            MolecularFormulaManipulator.getMolecularFormula(str, bldr);
        double act = MolecularFormulaManipulator.getMass(mf, flav);
        assertThat(act, is(closeTo(expMass, 0.01)));
    }

    @Test
    void C6Br6() {
        assertMass("C6Br6", 551.485, MolWeight);
        assertMass("C6Br6", 545.510, MonoIsotopic);
        assertMass("C6Br6", 551.503, MostAbundant);
        assertMass("[12]C4[13]C2Br6", 553.427, MolWeight);
        assertMass("[12]C4[13]C2Br6", 547.516, MonoIsotopic);
        assertMass("[12]C4[13]C2Br6", 553.510, MostAbundant);
    }

    // Iron has 4 stable isotopes, 54 @ 5.85%, 56 @ 91.57%, 57 @ 2.12%, and
    // 58 @ 0.28%. Given 100 iron's we expected ~6 @ 54, ~92 @ 56 and 2 @ 57
    @Test
    void getMostAbundantFe100() {
        IMolecularFormula mf = new MolecularFormula();
        mf.addIsotope(new Atom("Fe"), 100);
        IMolecularFormula mamf = MolecularFormulaManipulator.getMostAbundant(mf);
        assertThat(MolecularFormulaManipulator.getString(mamf, false, true),
                   is("[54]Fe6[56]Fe92[57]Fe2"));
    }

    @Test
    void getMassCranbin() {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        IMolecularFormula mf =
                MolecularFormulaManipulator.getMolecularFormula("C202H315N55O64S6",
                                                                bldr);
        org.hamcrest.MatcherAssert.assertThat(MolecularFormulaManipulator.getMass(mf, MolWeight),
                          closeTo(4730.397, 0.001));
        org.hamcrest.MatcherAssert.assertThat(MolecularFormulaManipulator.getMass(mf, MolWeightIgnoreSpecified),
                          closeTo(4730.397, 0.001));
        org.hamcrest.MatcherAssert.assertThat(MolecularFormulaManipulator.getMass(mf, MonoIsotopic),
                          closeTo(4727.140, 0.001));
        org.hamcrest.MatcherAssert.assertThat(MolecularFormulaManipulator.getMass(mf, MostAbundant),
                          closeTo(4729.147, 0.001));
    }

    @Test
    void getMassCranbinSpecIsotopes() {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        IMolecularFormula mf =
                MolecularFormulaManipulator.getMolecularFormula("[12]C200[13]C2[1]H315[14]N55[16]O64[32]S6",
                                                               bldr);
        org.hamcrest.MatcherAssert.assertThat(MolecularFormulaManipulator.getMass(mf, MolWeight),
                          closeTo(4729.147, 0.001));
        org.hamcrest.MatcherAssert.assertThat(MolecularFormulaManipulator.getMass(mf, MolWeightIgnoreSpecified),
                          closeTo(4730.397, 0.001));
        org.hamcrest.MatcherAssert.assertThat(MolecularFormulaManipulator.getMass(mf, MonoIsotopic),
                          closeTo(4729.147, 0.001));
        org.hamcrest.MatcherAssert.assertThat(MolecularFormulaManipulator.getMass(mf, MostAbundant),
                          closeTo(4729.147, 0.001));
    }

    @Test
    void getMassCranbinMixedSpecIsotopes() {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        IMolecularFormula mf =
                MolecularFormulaManipulator.getMolecularFormula("C200[13]C2H315N55O64S6",
                                                                bldr);
        org.hamcrest.MatcherAssert.assertThat(MolecularFormulaManipulator.getMass(mf, MolWeight),
                          closeTo(4732.382, 0.001));
        org.hamcrest.MatcherAssert.assertThat(MolecularFormulaManipulator.getMass(mf, MolWeightIgnoreSpecified),
                          closeTo(4730.397, 0.001));
        org.hamcrest.MatcherAssert.assertThat(MolecularFormulaManipulator.getMass(mf, MonoIsotopic),
                          closeTo(4729.147, 0.001));
        org.hamcrest.MatcherAssert.assertThat(MolecularFormulaManipulator.getMass(mf, MostAbundant),
                          closeTo(4731.154, 0.001));
    }

    void roundtrip(String mfStr, String expected) {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        IMolecularFormula mf =
            MolecularFormulaManipulator.getMolecularFormula(mfStr, bldr);
        String actual = getString(mf, false, true);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testIsotopeAndChargeParsing() {
        // proffered input
        roundtrip("[[2]H2]-", "[[2]H2]-");
        // missing outer brackets, isotope+element in square brackets
        roundtrip("[[2H]2]-", "[[2]H2]-");
        roundtrip("[[2H]2]+", "[[2]H2]+"); // [2H]2+ is ambiguous
        // missing outer brackets, isotope in square brackets
        roundtrip("[2]H2-", "[[2]H2]-");
        roundtrip("[2]H2+", "[[2]H2]+");
        // +2 => 2+ with brackets
        roundtrip("[2H]2+2", "[[2]H2]2+");
        roundtrip("H+", "[H]+");
    }
}
