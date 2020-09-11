/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.smiles;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.SlowTest;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.aromaticity.ElectronDonation;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.AtomContainerAtomPermutor;
import org.openscience.cdk.graph.AtomContainerBondPermutor;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.stereo.DoubleBondStereochemistry;
import org.openscience.cdk.stereo.TetrahedralChirality;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import javax.vecmath.Point2d;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author         steinbeck
 * @cdk.created    2004-02-09
 * @cdk.module     test-smiles
 */
public class SmilesGeneratorTest extends CDKTestCase {

    /**
     *  A unit test for JUnit
     */
    @Test
    public void testSmilesGenerator() throws Exception {
        IAtomContainer mol2 = TestMoleculeFactory.makeAlphaPinene();
        SmilesGenerator sg = new SmilesGenerator();
        addImplicitHydrogens(mol2);
        String smiles2 = sg.create(mol2);
        Assert.assertNotNull(smiles2);
        Assert.assertEquals("C1(=CCC2CC1C2(C)C)C", smiles2);
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    public void testEthylPropylPhenantren() throws Exception {
        IAtomContainer mol1 = TestMoleculeFactory.makeEthylPropylPhenantren();
        SmilesGenerator sg = new SmilesGenerator();
        fixCarbonHCount(mol1);
        String smiles1 = sg.create(mol1);
        Assert.assertNotNull(smiles1);
        Assert.assertEquals("C=1C=CC(=C2C=CC3=C(C12)C=CC(=C3)CCC)CC", smiles1);
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    public void testPropylCycloPropane() throws Exception {
        IAtomContainer mol1 = TestMoleculeFactory.makePropylCycloPropane();
        SmilesGenerator sg = new SmilesGenerator();
        fixCarbonHCount(mol1);
        String smiles1 = sg.create(mol1);
        Assert.assertNotNull(smiles1);
        Assert.assertEquals("C1CC1CCC", smiles1);
    }

    /**
     *  A unit test for JUnit
     *
     */
    @Test
    public void testAlanin() throws Exception {
        IAtomContainer mol1 = new AtomContainer();
        SmilesGenerator sg = SmilesGenerator.isomeric();
        mol1.addAtom(new Atom("N", new Point2d(1, 0)));
        // 1
        mol1.addAtom(new Atom("C", new Point2d(1, 2)));
        // 2
        mol1.addAtom(new Atom("F", new Point2d(1, 2)));
        // 3
        mol1.addAtom(new Atom("C", new Point2d(0, 0)));
        // 4
        mol1.addAtom(new Atom("C", new Point2d(1, 4)));
        // 5
        mol1.addAtom(new Atom("O", new Point2d(1, 5)));
        // 6
        mol1.addAtom(new Atom("O", new Point2d(1, 6)));
        // 7
        mol1.addBond(0, 1, IBond.Order.SINGLE);
        // 1
        mol1.addBond(1, 2, IBond.Order.SINGLE, IBond.Stereo.UP);
        // 2
        mol1.addBond(1, 3, IBond.Order.SINGLE, IBond.Stereo.DOWN);
        // 3
        mol1.addBond(1, 4, IBond.Order.SINGLE);
        // 4
        mol1.addBond(4, 5, IBond.Order.SINGLE);
        // 5
        mol1.addBond(4, 6, IBond.Order.DOUBLE);
        // 6
        // hydrogens in-lined from hydrogen adder/placer
        mol1.addAtom(new Atom("H", new Point2d(0.13, -0.50)));
        mol1.addBond(0, 7, IBond.Order.SINGLE);
        mol1.addAtom(new Atom("H", new Point2d(1.87, -0.50)));
        mol1.addBond(0, 8, IBond.Order.SINGLE);
        mol1.addAtom(new Atom("H", new Point2d(-0.89, 0.45)));
        mol1.addBond(3, 9, IBond.Order.SINGLE);
        mol1.addAtom(new Atom("H", new Point2d(-0.45, -0.89)));
        mol1.addBond(3, 10, IBond.Order.SINGLE);
        mol1.addAtom(new Atom("H", new Point2d(0.89, -0.45)));
        mol1.addBond(3, 11, IBond.Order.SINGLE);
        mol1.addAtom(new Atom("H", new Point2d(1.00, 6.00)));
        mol1.addBond(5, 12, IBond.Order.SINGLE);
        addImplicitHydrogens(mol1);

        IsotopeFactory ifac = Isotopes.getInstance();
        ifac.configureAtoms(mol1);

        define(mol1, anticlockwise(mol1, 1, 0, 2, 3, 4));

        String smiles1 = sg.create(mol1);
        Assert.assertNotNull(smiles1);

        Assert.assertEquals("N([C@](F)(C([H])([H])[H])C(O[H])=O)([H])[H]", smiles1);

        define(mol1, clockwise(mol1, 1, 0, 2, 3, 4));

        smiles1 = sg.create(mol1);
        Assert.assertNotNull(smiles1);
        Assert.assertEquals("N([C@@](F)(C([H])([H])[H])C(O[H])=O)([H])[H]", smiles1);
    }

    /**
     *  A unit test for JUnit
     *
     *@exception  Exception  Description of the Exception
     */
    @Test
    public void testCisResorcinol() throws Exception {
        IAtomContainer mol1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        SmilesGenerator sg = SmilesGenerator.isomeric();
        mol1.addAtom(new Atom("O", new Point2d(3, 1)));
        // 1
        mol1.addAtom(new Atom("H", new Point2d(2, 0)));
        // 2
        mol1.addAtom(new Atom("C", new Point2d(2, 1)));
        // 3
        mol1.addAtom(new Atom("C", new Point2d(1, 1)));
        // 4
        mol1.addAtom(new Atom("C", new Point2d(1, 4)));
        // 5
        mol1.addAtom(new Atom("C", new Point2d(1, 5)));
        // 6
        mol1.addAtom(new Atom("C", new Point2d(1, 2)));
        // 7
        mol1.addAtom(new Atom("C", new Point2d(2, 2)));
        // 1
        mol1.addAtom(new Atom("O", new Point2d(3, 2)));
        // 2
        mol1.addAtom(new Atom("H", new Point2d(2, 3)));
        // 3
        mol1.addBond(0, 2, IBond.Order.SINGLE, IBond.Stereo.DOWN);
        // 1
        mol1.addBond(1, 2, IBond.Order.SINGLE, IBond.Stereo.UP);
        // 2
        mol1.addBond(2, 3, IBond.Order.SINGLE);
        // 3
        mol1.addBond(3, 4, IBond.Order.SINGLE);
        // 4
        mol1.addBond(4, 5, IBond.Order.SINGLE);
        // 5
        mol1.addBond(5, 6, IBond.Order.SINGLE);
        // 6
        mol1.addBond(6, 7, IBond.Order.SINGLE);
        // 3
        mol1.addBond(7, 8, IBond.Order.SINGLE, IBond.Stereo.UP);
        // 4
        mol1.addBond(7, 9, IBond.Order.SINGLE, IBond.Stereo.DOWN);
        // 5
        mol1.addBond(7, 2, IBond.Order.SINGLE);
        // 6

        // hydrogens in-lined from hydrogen adder/placer
        mol1.addAtom(new Atom("H", new Point2d(4.00, 1.00)));
        mol1.addBond(0, 10, IBond.Order.SINGLE);
        mol1.addAtom(new Atom("H", new Point2d(0.00, 1.00)));
        mol1.addBond(3, 11, IBond.Order.SINGLE);
        mol1.addAtom(new Atom("H", new Point2d(1.00, 0.00)));
        mol1.addBond(3, 12, IBond.Order.SINGLE);
        mol1.addAtom(new Atom("H", new Point2d(0.13, 4.50)));
        mol1.addBond(4, 13, IBond.Order.SINGLE);
        mol1.addAtom(new Atom("H", new Point2d(0.13, 3.50)));
        mol1.addBond(4, 14, IBond.Order.SINGLE);
        mol1.addAtom(new Atom("H", new Point2d(1.87, 5.50)));
        mol1.addBond(5, 15, IBond.Order.SINGLE);
        mol1.addAtom(new Atom("H", new Point2d(0.13, 5.50)));
        mol1.addBond(5, 16, IBond.Order.SINGLE);
        mol1.addAtom(new Atom("H", new Point2d(0.00, 2.00)));
        mol1.addBond(6, 17, IBond.Order.SINGLE);
        mol1.addAtom(new Atom("H", new Point2d(1.00, 1.00)));
        mol1.addBond(6, 18, IBond.Order.SINGLE);
        mol1.addAtom(new Atom("H", new Point2d(4.00, 2.00)));
        mol1.addBond(8, 19, IBond.Order.SINGLE);

        addImplicitHydrogens(mol1);
        IsotopeFactory ifac = Isotopes.getInstance();

        ifac.configureAtoms(mol1);
        define(mol1, clockwise(mol1, 2, 0, 1, 3, 7), clockwise(mol1, 7, 2, 6, 8, 9));
        String smiles1 = sg.create(mol1);
        Assert.assertNotNull(smiles1);
        Assert.assertEquals("O([C@@]1([H])C(C(C(C([C@]1(O[H])[H])([H])[H])([H])[H])([H])[H])([H])[H])[H]", smiles1);
        mol1 = AtomContainerManipulator.removeHydrogens(mol1);
        smiles1 = sg.create(mol1);
        Assert.assertNotNull(smiles1);
        Assert.assertEquals("O[C@H]1CCCC[C@H]1O", smiles1);
    }

    /**
     *  A unit test for JUnit
     *
     *@exception  Exception  Description of the Exception
     */
    @Test
    public void testCisTransDecalin() throws Exception {
        IAtomContainer mol1 = new AtomContainer();
        SmilesGenerator sg = SmilesGenerator.isomeric();

        mol1.addAtom(new Atom("H", new Point2d(0, 3))); // 0
        mol1.addAtom(new Atom("C", new Point2d(0, 1))); // 1
        mol1.addAtom(new Atom("C", new Point2d(0, -1))); // 2
        mol1.addAtom(new Atom("H", new Point2d(0, -3))); // 3

        mol1.addAtom(new Atom("C", new Point2d(1.5, 2))); // 4
        mol1.addAtom(new Atom("C", new Point2d(3, 1))); // 5
        mol1.addAtom(new Atom("C", new Point2d(3, -1))); // 6
        mol1.addAtom(new Atom("C", new Point2d(1.5, -2))); // 7

        mol1.addAtom(new Atom("C", new Point2d(-1.5, 2))); // 8
        mol1.addAtom(new Atom("C", new Point2d(-3, 1))); // 9
        mol1.addAtom(new Atom("C", new Point2d(-3, -1))); // 10
        mol1.addAtom(new Atom("C", new Point2d(-1.5, -2))); // 11

        mol1.addBond(1, 0, IBond.Order.SINGLE, IBond.Stereo.DOWN);
        mol1.addBond(1, 2, IBond.Order.SINGLE);
        mol1.addBond(2, 3, IBond.Order.SINGLE, IBond.Stereo.DOWN);

        mol1.addBond(1, 4, IBond.Order.SINGLE);
        mol1.addBond(4, 5, IBond.Order.SINGLE);
        mol1.addBond(5, 6, IBond.Order.SINGLE);
        mol1.addBond(6, 7, IBond.Order.SINGLE);
        mol1.addBond(7, 2, IBond.Order.SINGLE);

        mol1.addBond(1, 8, IBond.Order.SINGLE);
        mol1.addBond(8, 9, IBond.Order.SINGLE);
        mol1.addBond(9, 10, IBond.Order.SINGLE);
        mol1.addBond(10, 11, IBond.Order.SINGLE);
        mol1.addBond(11, 2, IBond.Order.SINGLE);

        // hydrogens in-lined from hydrogen adder/placer
        mol1.addAtom(new Atom("H", new Point2d(2.16, 2.75)));
        mol1.addBond(4, 12, IBond.Order.SINGLE);
        mol1.addAtom(new Atom("H", new Point2d(0.84, 2.75)));
        mol1.addBond(4, 13, IBond.Order.SINGLE);
        mol1.addAtom(new Atom("H", new Point2d(3.98, 0.81)));
        mol1.addBond(5, 14, IBond.Order.SINGLE);
        mol1.addAtom(new Atom("H", new Point2d(3.38, 1.92)));
        mol1.addBond(5, 15, IBond.Order.SINGLE);
        mol1.addAtom(new Atom("H", new Point2d(3.38, -1.92)));
        mol1.addBond(6, 16, IBond.Order.SINGLE);
        mol1.addAtom(new Atom("H", new Point2d(3.98, -0.81)));
        mol1.addBond(6, 17, IBond.Order.SINGLE);
        mol1.addAtom(new Atom("H", new Point2d(0.84, -2.75)));
        mol1.addBond(7, 18, IBond.Order.SINGLE);
        mol1.addAtom(new Atom("H", new Point2d(2.16, -2.75)));
        mol1.addBond(7, 19, IBond.Order.SINGLE);
        mol1.addAtom(new Atom("H", new Point2d(-0.84, 2.75)));
        mol1.addBond(8, 20, IBond.Order.SINGLE);
        mol1.addAtom(new Atom("H", new Point2d(-2.16, 2.75)));
        mol1.addBond(8, 21, IBond.Order.SINGLE);
        mol1.addAtom(new Atom("H", new Point2d(-3.38, 1.92)));
        mol1.addBond(9, 22, IBond.Order.SINGLE);
        mol1.addAtom(new Atom("H", new Point2d(-3.98, 0.81)));
        mol1.addBond(9, 23, IBond.Order.SINGLE);
        mol1.addAtom(new Atom("H", new Point2d(-3.98, -0.81)));
        mol1.addBond(10, 24, IBond.Order.SINGLE);
        mol1.addAtom(new Atom("H", new Point2d(-3.38, -1.92)));
        mol1.addBond(10, 25, IBond.Order.SINGLE);
        mol1.addAtom(new Atom("H", new Point2d(-2.16, -2.75)));
        mol1.addBond(11, 26, IBond.Order.SINGLE);
        mol1.addAtom(new Atom("H", new Point2d(-0.84, -2.75)));
        mol1.addBond(11, 27, IBond.Order.SINGLE);
        addImplicitHydrogens(mol1);
        IsotopeFactory ifac = Isotopes.getInstance();
        ifac.configureAtoms(mol1);
        define(mol1, clockwise(mol1, 1, 0, 2, 4, 8), clockwise(mol1, 2, 1, 3, 7, 1));
        String smiles1 = sg.create(mol1);
        Assert.assertNotNull(smiles1);
        Assert.assertEquals(
                "[H][C@@]12[C@@]([H])(C(C(C(C1([H])[H])([H])[H])([H])[H])([H])[H])C(C(C(C2([H])[H])([H])[H])([H])[H])([H])[H]",
                smiles1);
        define(mol1, clockwise(mol1, 1, 0, 2, 4, 8), anticlockwise(mol1, 2, 1, 3, 7, 1));
        String smiles3 = sg.create(mol1);
        org.hamcrest.MatcherAssert.assertThat(smiles1, is(not(smiles3)));
    }

    /**
     *  A unit test for JUnit
     *
     *@exception  Exception  Description of the Exception
     */
    @Test
    public void testDoubleBondConfiguration() throws Exception {
        IAtomContainer mol1 = new AtomContainer();
        SmilesGenerator sg = SmilesGenerator.isomeric();
        mol1.addAtom(new Atom("S", new Point2d(0, 0)));
        // 1
        mol1.addAtom(new Atom("C", new Point2d(1, 1)));
        // 2
        mol1.addAtom(new Atom("F", new Point2d(2, 0)));
        // 3
        mol1.addAtom(new Atom("C", new Point2d(1, 2)));
        // 4
        mol1.addAtom(new Atom("F", new Point2d(2, 3)));
        // 5
        mol1.addAtom(new Atom("S", new Point2d(0, 3)));
        // 1

        mol1.addBond(0, 1, IBond.Order.SINGLE);
        // 1
        mol1.addBond(1, 2, IBond.Order.SINGLE);
        // 2
        mol1.addBond(1, 3, IBond.Order.DOUBLE);
        // 3
        mol1.addBond(3, 4, IBond.Order.SINGLE);
        // 4
        mol1.addBond(3, 5, IBond.Order.SINGLE);
        // 4
        addImplicitHydrogens(mol1);
        IsotopeFactory ifac = Isotopes.getInstance();
        ifac.configureAtoms(mol1);

        mol1.setStereoElements(new ArrayList<IStereoElement>()); // clear existing
        mol1.addStereoElement(new DoubleBondStereochemistry(mol1.getBond(2), new IBond[]{mol1.getBond(1),
                mol1.getBond(3)}, IDoubleBondStereochemistry.Conformation.OPPOSITE));
        String smiles1 = sg.create(mol1);
        Assert.assertNotNull(smiles1);
        Assert.assertEquals("S\\C(\\F)=C(/F)\\S", smiles1);

        mol1.setStereoElements(new ArrayList<IStereoElement>()); // clear existing
        mol1.addStereoElement(new DoubleBondStereochemistry(mol1.getBond(2), new IBond[]{mol1.getBond(1),
                mol1.getBond(3)}, IDoubleBondStereochemistry.Conformation.TOGETHER));

        smiles1 = sg.create(mol1);
        Assert.assertNotNull(smiles1);
        Assert.assertEquals("S\\C(\\F)=C(\\F)/S", smiles1);

        // hydrogens in-lined from hydrogen adder/placer
        mol1.addAtom(new Atom("H", new Point2d(-0.71, -0.71)));
        mol1.getAtom(0).setImplicitHydrogenCount(0);
        mol1.getAtom(mol1.getAtomCount() - 1).setImplicitHydrogenCount(0);
        mol1.addBond(0, 6, IBond.Order.SINGLE);
        mol1.addAtom(new Atom("H", new Point2d(2.71, 3.71)));
        mol1.getAtom(5).setImplicitHydrogenCount(0);
        mol1.getAtom(mol1.getAtomCount() - 1).setImplicitHydrogenCount(0);
        mol1.addBond(5, 7, IBond.Order.SINGLE);

        mol1.setStereoElements(new ArrayList<IStereoElement>()); // clear existing
        mol1.addStereoElement(new DoubleBondStereochemistry(mol1.getBond(2), new IBond[]{mol1.getBond(0),
                mol1.getBond(3)}, IDoubleBondStereochemistry.Conformation.OPPOSITE));

        smiles1 = sg.create(mol1);
        Assert.assertNotNull(smiles1);
        Assert.assertEquals("S(/C(/F)=C(/F)\\S[H])[H]", smiles1);

        mol1.setStereoElements(new ArrayList<IStereoElement>()); // clear existing
        mol1.addStereoElement(new DoubleBondStereochemistry(mol1.getBond(2), new IBond[]{mol1.getBond(0),
                mol1.getBond(3)}, IDoubleBondStereochemistry.Conformation.TOGETHER));

        smiles1 = sg.create(mol1);
        Assert.assertNotNull(smiles1);
        Assert.assertEquals("S(/C(/F)=C(\\F)/S[H])[H]", smiles1);
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    public void testPartitioning() throws Exception {
        String smiles = "";
        IAtomContainer molecule = new AtomContainer();
        SmilesGenerator sg = new SmilesGenerator();
        Atom sodium = new Atom("Na");
        sodium.setFormalCharge(+1);
        Atom hydroxyl = new Atom("O");
        hydroxyl.setImplicitHydrogenCount(1);
        hydroxyl.setFormalCharge(-1);
        molecule.addAtom(sodium);
        molecule.addAtom(hydroxyl);
        addImplicitHydrogens(molecule);
        smiles = sg.create(molecule);
        Assert.assertTrue(smiles.indexOf(".") != -1);
    }

    /**
     * @cdk.bug 791091
     */
    @Test
    public void testBug791091() throws Exception {
        String smiles = "";
        IAtomContainer molecule = new AtomContainer();
        SmilesGenerator sg = new SmilesGenerator();
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("N"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addBond(1, 2, IBond.Order.SINGLE);
        molecule.addBond(2, 4, IBond.Order.SINGLE);
        molecule.addBond(4, 0, IBond.Order.SINGLE);
        molecule.addBond(4, 3, IBond.Order.SINGLE);
        fixCarbonHCount(molecule);
        smiles = sg.create(molecule);
        Assert.assertEquals("C1CCN1C", smiles);
    }

    /**
     * @cdk.bug 590236
     */
    @Test
    public void testBug590236() throws Exception {
        String smiles = "";
        IAtomContainer molecule = new AtomContainer();
        SmilesGenerator sg = SmilesGenerator.isomeric();
        molecule.addAtom(new Atom("C"));
        Atom carbon2 = new Atom("C");
        carbon2.setMassNumber(13);
        molecule.addAtom(carbon2);
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        fixCarbonHCount(molecule);
        smiles = sg.create(molecule);
        Assert.assertEquals("C[13CH3]", smiles);
    }

    /**
     * A bug reported for JChemPaint.
     *
     * @cdk.bug 956923
     */
    @Test
    public void testSFBug956923_aromatic() throws Exception {
        String smiles = "";
        IAtomContainer molecule = new AtomContainer();
        SmilesGenerator sg = new SmilesGenerator().aromatic();
        Atom sp2CarbonWithOneHydrogen = new Atom("C");
        sp2CarbonWithOneHydrogen.setHybridization(IAtomType.Hybridization.SP2);
        sp2CarbonWithOneHydrogen.setImplicitHydrogenCount(1);
        molecule.addAtom(sp2CarbonWithOneHydrogen);
        molecule.addAtom((Atom) sp2CarbonWithOneHydrogen.clone());
        molecule.addAtom((Atom) sp2CarbonWithOneHydrogen.clone());
        molecule.addAtom((Atom) sp2CarbonWithOneHydrogen.clone());
        molecule.addAtom((Atom) sp2CarbonWithOneHydrogen.clone());
        molecule.addAtom((Atom) sp2CarbonWithOneHydrogen.clone());
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addBond(1, 2, IBond.Order.SINGLE);
        molecule.addBond(2, 3, IBond.Order.SINGLE);
        molecule.addBond(3, 4, IBond.Order.SINGLE);
        molecule.addBond(4, 5, IBond.Order.SINGLE);
        molecule.addBond(5, 0, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        Aromaticity.cdkLegacy().apply(molecule);
        smiles = sg.create(molecule);
        Assert.assertEquals("c1ccccc1", smiles);
    }

    @Test
    public void testSFBug956923_nonAromatic() throws Exception {
        String smiles = "";
        IAtomContainer molecule = new AtomContainer();
        SmilesGenerator sg = new SmilesGenerator();
        Atom sp2CarbonWithOneHydrogen = new Atom("C");
        sp2CarbonWithOneHydrogen.setHybridization(IAtomType.Hybridization.SP2);
        sp2CarbonWithOneHydrogen.setImplicitHydrogenCount(1);
        molecule.addAtom(sp2CarbonWithOneHydrogen);
        molecule.addAtom((Atom) sp2CarbonWithOneHydrogen.clone());
        molecule.addAtom((Atom) sp2CarbonWithOneHydrogen.clone());
        molecule.addAtom((Atom) sp2CarbonWithOneHydrogen.clone());
        molecule.addAtom((Atom) sp2CarbonWithOneHydrogen.clone());
        molecule.addAtom((Atom) sp2CarbonWithOneHydrogen.clone());
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addBond(1, 2, IBond.Order.SINGLE);
        molecule.addBond(2, 3, IBond.Order.SINGLE);
        molecule.addBond(3, 4, IBond.Order.SINGLE);
        molecule.addBond(4, 5, IBond.Order.SINGLE);
        molecule.addBond(5, 0, IBond.Order.SINGLE);
        smiles = sg.create(molecule);
        Assert.assertEquals("[CH]1[CH][CH][CH][CH][CH]1", smiles);
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    public void testAtomPermutation() throws Exception {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("S"));
        mol.addAtom(new Atom("O"));
        mol.addAtom(new Atom("O"));
        mol.addAtom(new Atom("O"));
        mol.addAtom(new Atom("O"));
        mol.addBond(0, 1, IBond.Order.DOUBLE);
        mol.addBond(0, 2, IBond.Order.DOUBLE);
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addBond(0, 4, IBond.Order.SINGLE);
        mol.getAtom(3).setImplicitHydrogenCount(1);
        mol.getAtom(4).setImplicitHydrogenCount(1);
        addImplicitHydrogens(mol);
        AtomContainerAtomPermutor acap = new AtomContainerAtomPermutor(mol);
        SmilesGenerator sg = SmilesGenerator.unique();
        String smiles = "";
        String oldSmiles = sg.create(mol);
        while (acap.hasNext()) {
            smiles = sg.create(new AtomContainer((AtomContainer) acap.next()));
            //logger.debug(smiles);
            Assert.assertEquals(oldSmiles, smiles);
        }

    }

    /**
     *  A unit test for JUnit
     */
    @Test
    public void testBondPermutation() throws Exception {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("S"));
        mol.addAtom(new Atom("O"));
        mol.addAtom(new Atom("O"));
        mol.addAtom(new Atom("O"));
        mol.addAtom(new Atom("O"));
        mol.addBond(0, 1, IBond.Order.DOUBLE);
        mol.addBond(0, 2, IBond.Order.DOUBLE);
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addBond(0, 4, IBond.Order.SINGLE);
        mol.getAtom(3).setImplicitHydrogenCount(1);
        mol.getAtom(4).setImplicitHydrogenCount(1);
        addImplicitHydrogens(mol);
        AtomContainerBondPermutor acbp = new AtomContainerBondPermutor(mol);
        SmilesGenerator sg = SmilesGenerator.unique();
        String smiles = "";
        String oldSmiles = sg.create(mol);
        while (acbp.hasNext()) {
            smiles = sg.create(new AtomContainer((AtomContainer) acbp.next()));
            //logger.debug(smiles);
            Assert.assertEquals(oldSmiles, smiles);
        }

    }

    private void fixCarbonHCount(IAtomContainer mol) {
        /*
         * the following line are just a quick fix for this particluar
         * carbon-only molecule until we have a proper hydrogen count
         * configurator
         */
        double bondCount = 0;
        org.openscience.cdk.interfaces.IAtom atom;
        for (int f = 0; f < mol.getAtomCount(); f++) {
            atom = mol.getAtom(f);
            bondCount = mol.getBondOrderSum(atom);
            int correction = (int) bondCount - (atom.getCharge() != null ? atom.getCharge().intValue() : 0);
            if (atom.getSymbol().equals("C")) {
                atom.setImplicitHydrogenCount(4 - correction);
            } else if (atom.getSymbol().equals("N")) {
                atom.setImplicitHydrogenCount(3 - correction);
            }
        }
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    public void testPseudoAtom() throws Exception {
        IAtom atom = new PseudoAtom("Star");
        SmilesGenerator sg = new SmilesGenerator(SmiFlavor.Generic);
        String smiles = "";
        IAtomContainer molecule = new AtomContainer();
        molecule.addAtom(atom);
        addImplicitHydrogens(molecule);
        smiles = sg.create(molecule);
        Assert.assertEquals("*", smiles);
    }

    /**
     *  Test generation of a reaction SMILES. I know, it's a stupid alchemic
     *  reaction, but it serves its purpose.
     */
    @Test
    public void testReactionSMILES() throws Exception {
        Reaction reaction = new Reaction();
        AtomContainer methane = new AtomContainer();
        methane.addAtom(new Atom("C"));
        reaction.addReactant(methane);
        IAtomContainer magic = new AtomContainer();
        magic.addAtom(new PseudoAtom("magic"));
        reaction.addAgent(magic);
        IAtomContainer gold = new AtomContainer();
        gold.addAtom(new Atom("Au"));
        reaction.addProduct(gold);

        methane.getAtom(0).setImplicitHydrogenCount(4);
        gold.getAtom(0).setImplicitHydrogenCount(0);

        SmilesGenerator sg = new SmilesGenerator(SmiFlavor.Generic);
        String smiles = sg.create(reaction);
        //logger.debug("Generated SMILES: " + smiles);
        Assert.assertEquals("C>*>[Au]", smiles);
    }

    /**
     *  Test generation of a D and L alanin.
     */
    @Test
    public void testAlaSMILES() throws Exception {
        String filename = "data/mdl/l-ala.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        IAtomContainer mol1 = reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class));
        filename = "data/mdl/d-ala.mol";
        ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        reader = new MDLV2000Reader(ins, Mode.STRICT);
        IAtomContainer mol2 = reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class));
        SmilesGenerator sg = SmilesGenerator.isomeric();

        define(mol1, anticlockwise(mol1, 1, 0, 2, 3, 6));
        define(mol2, clockwise(mol2, 1, 0, 2, 3, 6));

        String smiles1 = sg.create(mol1);
        String smiles2 = sg.create(mol2);
        org.hamcrest.MatcherAssert.assertThat(smiles1, is(not(smiles2)));
    }

    /**
     *  Test some sugars
     */
    @Test
    public void testSugarSMILES() throws Exception {
        String filename = "data/mdl/D-mannose.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        IAtomContainer mol1 = reader.read(new AtomContainer());
        filename = "data/mdl/D+-glucose.mol";
        ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        reader = new MDLV2000Reader(ins, Mode.STRICT);
        IAtomContainer mol2 = reader.read(new AtomContainer());
        SmilesGenerator sg = SmilesGenerator.isomeric();

        define(mol1, anticlockwise(mol1, 0, 0, 1, 5, 9), anticlockwise(mol1, 1, 1, 0, 2, 8),
                clockwise(mol1, 2, 2, 1, 3, 6), anticlockwise(mol1, 5, 5, 0, 4, 10));
        define(mol2, anticlockwise(mol2, 0, 0, 1, 5, 9), anticlockwise(mol2, 1, 1, 0, 2, 8),
                clockwise(mol2, 2, 2, 1, 3, 6), clockwise(mol2, 5, 5, 0, 4, 10), anticlockwise(mol2, 4, 4, 3, 5, 11));

        String smiles1 = sg.create(mol1);
        String smiles2 = sg.create(mol2);
        org.hamcrest.MatcherAssert.assertThat(smiles1, is(not(smiles2)));
    }

    /**
     *  Test for some rings where the double bond is broken
     */
    @Test
    public void testCycloOctan() throws Exception {
        String filename = "data/mdl/cyclooctan.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        IAtomContainer mol1 = reader.read(new AtomContainer());
        SmilesGenerator sg = new SmilesGenerator();
        String moleculeSmile = sg.create(mol1);
        Assert.assertEquals("C\\1=C\\CCCCCC1", moleculeSmile);
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    public void testCycloOcten() throws Exception {
        String filename = "data/mdl/cycloocten.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        IAtomContainer mol1 = reader.read(new AtomContainer());
        SmilesGenerator sg = new SmilesGenerator();
        String moleculeSmile = sg.create(mol1);
        Assert.assertEquals("C1/C=C\\CCCCC1", moleculeSmile);
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    public void testCycloOctadien() throws Exception {
        String filename = "data/mdl/cyclooctadien.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        IAtomContainer mol1 = reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class));
        SmilesGenerator sg = new SmilesGenerator();
        String moleculeSmile = sg.create(mol1);
        Assert.assertEquals("C=1\\CC/C=C\\CC/C1", moleculeSmile);
    }

    /**
     * @cdk.bug 1089770
     */
    @Test
    public void testSFBug1089770_1() throws Exception {
        String filename = "data/mdl/bug1089770-1.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        IAtomContainer mol1 = reader.read(new AtomContainer());
        SmilesGenerator sg = new SmilesGenerator();
        String moleculeSmile = sg.create(mol1);
        //logger.debug(filename + " -> " + moleculeSmile);
        Assert.assertEquals("C1CCC2=C(C1)CCC2", moleculeSmile);
    }

    /**
     * @cdk.bug 1089770
     * @see <a href="https://sourceforge.net/p/cdk/bugs/242/">CDK Bug 1089770</a>
     */
    @Test
    public void testSFBug1089770_2() throws Exception {
        String filename = "data/mdl/bug1089770-2.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        IAtomContainer mol1 = reader.read(new AtomContainer());
        SmilesGenerator sg = new SmilesGenerator();
        String moleculeSmile = sg.create(mol1);
        //logger.debug(filename + " -> " + moleculeSmile);
        Assert.assertEquals("C=1\\CC/C=C\\CC/C1", moleculeSmile);
    }

    /**
     * @cdk.bug 1014344
     */
    @Category(SlowTest.class)
    // MDL -> CML (slow) -> SMILES round tripping
    @Test
    public void testSFBug1014344() throws Exception {
        String filename = "data/mdl/bug1014344-1.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLReader reader = new MDLReader(ins, Mode.STRICT);
        IAtomContainer mol1 = reader.read(new AtomContainer());
        addImplicitHydrogens(mol1);
        SmilesGenerator sg = new SmilesGenerator();
        String molSmiles = sg.create(mol1);
        StringWriter output = new StringWriter();
        CMLWriter cmlWriter = new CMLWriter(output);
        cmlWriter.write(mol1);
        CMLReader cmlreader = new CMLReader(new ByteArrayInputStream(output.toString().getBytes()));
        IAtomContainer mol2 = ((IChemFile) cmlreader.read(new ChemFile())).getChemSequence(0).getChemModel(0)
                .getMoleculeSet().getAtomContainer(0);
        addImplicitHydrogens(mol2);
        String cmlSmiles = sg.create(new AtomContainer(mol2));
        Assert.assertEquals(molSmiles, cmlSmiles);
    }

    /**
     * @cdk.bug 1014344
     */
    @Test
    public void testTest() throws Exception {
        String filename_cml = "data/mdl/9554-with-exp-hyd.mol";
        String filename_mol = "data/mdl/9553-with-exp-hyd.mol";
        InputStream ins1 = this.getClass().getClassLoader().getResourceAsStream(filename_cml);
        InputStream ins2 = this.getClass().getClassLoader().getResourceAsStream(filename_mol);
        MDLV2000Reader reader1 = new MDLV2000Reader(ins1, Mode.STRICT);
        IAtomContainer mol1 = reader1.read(new AtomContainer());

        MDLV2000Reader reader2 = new MDLV2000Reader(ins2, Mode.STRICT);
        IAtomContainer mol2 = reader2.read(new AtomContainer());

        SmilesGenerator sg = SmilesGenerator.isomeric();

        define(mol1, clockwise(mol1, 0, 1, 5, 12, 13), clockwise(mol1, 1, 0, 2, 6, 12),
                clockwise(mol1, 2, 1, 3, 9, 10), clockwise(mol1, 5, 0, 4, 11, 18));
        define(mol2, clockwise(mol2, 0, 1, 5, 12, 13), clockwise(mol2, 1, 0, 2, 6, 12),
                anticlockwise(mol2, 2, 1, 3, 9, 10), clockwise(mol2, 5, 0, 4, 11, 18));

        String moleculeSmile1 = sg.create(mol1);
        String moleculeSmile2 = sg.create(mol2);
        org.hamcrest.MatcherAssert.assertThat(moleculeSmile1, is(not(moleculeSmile2)));
    }

    /**
     * @cdk.bug 1535055
     */
    @Test
    public void testSFBug1535055() throws Exception {
        String filename_cml = "data/cml/test1.cml";
        InputStream ins1 = this.getClass().getClassLoader().getResourceAsStream(filename_cml);
        CMLReader reader1 = new CMLReader(ins1);
        IChemFile chemFile = (IChemFile) reader1.read(new ChemFile());
        Assert.assertNotNull(chemFile);
        IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);
        IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);
        IAtomContainer mol1 = model.getMoleculeSet().getAtomContainer(0);
        Assert.assertNotNull(mol1);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol1);
        addImplicitHydrogens(mol1);
        Assert.assertTrue(Aromaticity.cdkLegacy().apply(mol1));

        SmilesGenerator sg = new SmilesGenerator().aromatic();

        String mol1SMILES = sg.create(mol1);
        Assert.assertTrue(mol1SMILES.contains("nH"));
    }

    /**
     * @cdk.bug 1014344
     */
    @Test
    public void testSFBug1014344_1() throws Exception {
        String filename_cml = "data/cml/bug1014344-1.cml";
        String filename_mol = "data/mdl/bug1014344-1.mol";
        InputStream ins1 = this.getClass().getClassLoader().getResourceAsStream(filename_cml);
        InputStream ins2 = this.getClass().getClassLoader().getResourceAsStream(filename_mol);
        CMLReader reader1 = new CMLReader(ins1);
        IChemFile chemFile = (IChemFile) reader1.read(new ChemFile());
        IChemSequence seq = chemFile.getChemSequence(0);
        IChemModel model = seq.getChemModel(0);
        IAtomContainer mol1 = model.getMoleculeSet().getAtomContainer(0);

        MDLReader reader2 = new MDLReader(ins2);
        IAtomContainer mol2 = reader2.read(new AtomContainer());

        addImplicitHydrogens(mol1);
        addImplicitHydrogens(mol2);

        SmilesGenerator sg = new SmilesGenerator();

        String moleculeSmile1 = sg.create(mol1);
        //		logger.debug(filename_cml + " -> " + moleculeSmile1);
        String moleculeSmile2 = sg.create(mol2);
        //		logger.debug(filename_mol + " -> " + moleculeSmile2);
        Assert.assertEquals(moleculeSmile1, moleculeSmile2);
    }

    /**
     * @cdk.bug 1875946
     */
    @Test
    public void testPreservingFormalCharge() throws Exception {
        AtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom(Elements.OXYGEN));
        mol.getAtom(0).setFormalCharge(-1);
        mol.addAtom(new Atom(Elements.CARBON));
        mol.addBond(0, 1, IBond.Order.SINGLE);
        addImplicitHydrogens(mol);
        SmilesGenerator generator = new SmilesGenerator();
        generator.create(new AtomContainer(mol));
        Assert.assertEquals(-1, mol.getAtom(0).getFormalCharge().intValue());
        // mmm, that does not reproduce the bug findings yet :(
    }

    @Test
    public void testIndole() throws Exception {
        IAtomContainer mol = TestMoleculeFactory.makeIndole();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addImplicitHydrogens(mol);
        Aromaticity.cdkLegacy().apply(mol);

        SmilesGenerator smilesGenerator = new SmilesGenerator().aromatic();
        String smiles = smilesGenerator.create(mol);
        Assert.assertTrue(smiles.indexOf("[nH]") >= 0);
    }

    @Test
    public void testPyrrole() throws Exception {
        IAtomContainer mol = TestMoleculeFactory.makePyrrole();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addImplicitHydrogens(mol);
        Aromaticity.cdkLegacy().apply(mol);
        SmilesGenerator smilesGenerator = new SmilesGenerator().aromatic();
        String smiles = smilesGenerator.create(mol);
        Assert.assertTrue(smiles.indexOf("[nH]") >= 0);
    }

    /**
     * @cdk.bug 1300
     */
    @Test
    public void testDoubleBracketProblem() throws Exception {
        IAtomContainer mol = TestMoleculeFactory.makePyrrole();
        mol.getAtom(1).setFormalCharge(-1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        addImplicitHydrogens(mol);
        SmilesGenerator smilesGenerator = new SmilesGenerator().aromatic();
        String smiles = smilesGenerator.create(mol);
        Assert.assertFalse(smiles.contains("[[nH]-]"));
    }

    /**
     * @cdk.bug 1300
     */
    @Test
    public void testHydrogenOnChargedNitrogen() throws Exception {
        IAtomContainer mol = TestMoleculeFactory.makePyrrole();
        mol.getAtom(1).setFormalCharge(-1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        addImplicitHydrogens(mol);
        Aromaticity.cdkLegacy().apply(mol);

        SmilesGenerator smilesGenerator = new SmilesGenerator().aromatic();
        String smiles = smilesGenerator.create(mol);
        Assert.assertTrue(smiles.contains("[n-]"));
    }

    /**
     * @cdk.bug 545
     */
    @Test
    public void testTimeOut() throws Exception {
        String filename = "data/mdl/24763.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        ChemFile chemFile = reader.read(new ChemFile());
        reader.close();
        Assert.assertNotNull(chemFile);
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertEquals(1, containersList.size());
        IAtomContainer container = containersList.get(0);
        SmilesGenerator smilesGenerator = new SmilesGenerator();
        Assert.assertNotNull(smilesGenerator.create(container));
    }

    /**
     * @cdk.bug 2051597
     */
    @Test
    public void testSFBug2051597() throws Exception {
        String smiles = "c1(c2ccc(c8ccccc8)cc2)" + "c(c3ccc(c9ccccc9)cc3)" + "c(c4ccc(c%10ccccc%10)cc4)"
                + "c(c5ccc(c%11ccccc%11)cc5)" + "c(c6ccc(c%12ccccc%12)cc6)" + "c1(c7ccc(c%13ccccc%13)cc7)";
        SmilesParser smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer cdkMol = smilesParser.parseSmiles(smiles);
        SmilesGenerator smilesGenerator = new SmilesGenerator();
        String genSmiles = smilesGenerator.create(cdkMol);

        // check that we have the appropriate ring closure symbols
        Assert.assertTrue("There were'nt any % ring closures in the output", genSmiles.indexOf("%") >= 0);
        Assert.assertTrue(genSmiles.indexOf("%10") >= 0);
        Assert.assertTrue(genSmiles.indexOf("%11") >= 0);
        Assert.assertTrue(genSmiles.indexOf("%12") >= 0);
        Assert.assertTrue(genSmiles.indexOf("%13") >= 0);

        // check that we can read in the SMILES we got
        IAtomContainer cdkRoundTripMol = smilesParser.parseSmiles(genSmiles);
        Assert.assertNotNull(cdkRoundTripMol);
    }

    /**
     * @cdk.bug 2596061
     */
    @Test
    public void testRoundTripPseudoAtom() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        String smiles = "[12*H2-]";
        IAtomContainer mol = sp.parseSmiles(smiles);
        SmilesGenerator smilesGenerator = SmilesGenerator.isomeric();
        String genSmiles = smilesGenerator.create(mol);
        Assert.assertEquals(smiles, genSmiles);
    }

    /**
     * @cdk.bug 2781199
     */
    @Test
    public void testBug2781199() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        String smiles = "n1ncn(c1)CC";
        IAtomContainer mol = sp.parseSmiles(smiles);
        SmilesGenerator smilesGenerator = new SmilesGenerator().aromatic();
        String genSmiles = smilesGenerator.create(mol);
        Assert.assertTrue("Generated SMILES should not have explicit H: " + genSmiles, genSmiles.indexOf("H") == -1);
    }

    /**
     * @cdk.bug 2898032
     */
    @Test
    public void testCanSmiWithoutConfiguredAtoms() throws CDKException, IOException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        String s1 = "OC(=O)C(Br)(Cl)N";
        String s2 = "ClC(Br)(N)C(=O)O";

        IAtomContainer m1 = sp.parseSmiles(s1);
        IAtomContainer m2 = sp.parseSmiles(s2);

        SmilesGenerator sg = SmilesGenerator.unique();
        String o1 = sg.create(m1);
        String o2 = sg.create(m2);

        Assert.assertTrue("The two canonical SMILES should match", o1.equals(o2));
    }

    /**
     * @cdk.bug 2898032
     */
    @Test
    public void testCanSmiWithConfiguredAtoms() throws CDKException, IOException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        String s1 = "OC(=O)C(Br)(Cl)N";
        String s2 = "ClC(Br)(N)C(=O)O";

        IAtomContainer m1 = sp.parseSmiles(s1);
        IAtomContainer m2 = sp.parseSmiles(s2);

        IsotopeFactory fact = Isotopes.getInstance();
        fact.configureAtoms(m1);
        fact.configureAtoms(m2);

        SmilesGenerator sg = SmilesGenerator.unique();
        String o1 = sg.create(m1);
        String o2 = sg.create(m2);

        Assert.assertTrue("The two canonical SMILES should match", o1.equals(o2));
    }

    /**
     * @cdk.bug 3040273
     */
    @Test
    public void testBug3040273() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        String testSmiles = "C1(C(C(C(C(C1Br)Br)Br)Br)Br)Br";
        IAtomContainer mol = sp.parseSmiles(testSmiles);
        IsotopeFactory fact = Isotopes.getInstance();
        fact.configureAtoms(mol);
        SmilesGenerator sg = new SmilesGenerator();
        String smiles = sg.create((IAtomContainer) mol);
        IAtomContainer mol2 = sp.parseSmiles(smiles);
        Assert.assertTrue(new UniversalIsomorphismTester().isIsomorph(mol, mol2));
    }

    @Test
    public void testCreateSMILESWithoutCheckForMultipleMolecules_withDetectAromaticity() throws Exception {
        IAtomContainer benzene = TestMoleculeFactory.makeBenzene();
        addImplicitHydrogens(benzene);
        SmilesGenerator sg = new SmilesGenerator();
        String smileswithoutaromaticity = sg.create(benzene);
        Assert.assertEquals("C=1C=CC=CC1", smileswithoutaromaticity);
    }

    @Test
    public void testCreateSMILESWithoutCheckForMultipleMolecules_withoutDetectAromaticity() throws Exception {
        IAtomContainer benzene = TestMoleculeFactory.makeBenzene();
        addImplicitHydrogens(benzene);
        SmilesGenerator sg = new SmilesGenerator().aromatic();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(benzene);
        Aromaticity.cdkLegacy().apply(benzene);
        String smileswitharomaticity = sg.create(benzene);
        Assert.assertEquals("c1ccccc1", smileswitharomaticity);
    }

    @Test
    public void outputOrder() throws Exception {
        IAtomContainer adenine = TestMoleculeFactory.makeAdenine();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(adenine);
        CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance()).addImplicitHydrogens(adenine);

        SmilesGenerator sg = SmilesGenerator.generic();
        int[] order = new int[adenine.getAtomCount()];

        String smi = sg.create(adenine, order);
        String[] at = new String[adenine.getAtomCount()];

        for (int i = 0; i < at.length; i++) {
            at[order[i]] = adenine.getAtom(i).getAtomTypeName();
        }

        // read in the SMILES
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer adenine2 = sp.parseSmiles(smi);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(adenine2);
        CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance()).addImplicitHydrogens(adenine2);

        // check atom types
        for (int i = 0; i < adenine2.getAtomCount(); i++) {
            assertThat(at[i], is(adenine2.getAtom(i).getAtomTypeName()));
        }
    }

    @Test
    public void outputCanOrder() throws Exception {
        IAtomContainer adenine = TestMoleculeFactory.makeAdenine();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(adenine);
        CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance()).addImplicitHydrogens(adenine);

        SmilesGenerator sg = SmilesGenerator.unique();
        int[] order = new int[adenine.getAtomCount()];

        String smi = sg.create(adenine, order);
        String[] at = new String[adenine.getAtomCount()];

        for (int i = 0; i < at.length; i++) {
            at[order[i]] = adenine.getAtom(i).getAtomTypeName();
        }

        // read in the SMILES
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer adenine2 = sp.parseSmiles(smi);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(adenine2);
        CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance()).addImplicitHydrogens(adenine2);

        // check atom types
        for (int i = 0; i < adenine2.getAtomCount(); i++) {
            assertThat(at[i], is(adenine2.getAtom(i).getAtomTypeName()));
        }
    }

    @Test
    public void atomClasses() throws Exception {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        IAtomContainer ethanol = new SmilesParser(bldr).parseSmiles("C[CH2:6]O");
        assertThat(SmilesGenerator.generic().create(ethanol), is("CCO"));
        assertThat(SmilesGenerator.generic().withAtomClasses().create(ethanol), is("C[CH2:6]O"));
    }

    /**
     * @cdk.bug 328
     */
    @Test
    public void bug328() throws Exception {
        assertThat(canon("[H]c2c([H])c(c1c(nc(n1([H]))C(F)(F)F)c2Cl)Cl"),
                   is(canon("Clc1ccc(Cl)c2[nH]c([nH0]c21)C(F)(F)F")));
    }

    @Test(expected = CDKException.class)
    public void warnOnBadInput() throws Exception {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        smipar.kekulise(false);
        IAtomContainer mol = smipar.parseSmiles("c1ccccc1");
        System.err.println(SmilesGenerator.isomeric().create(mol));
    }

    /**
     * @see https://tech.knime.org/forum/cdk/buggy-behavior-of-molecule-to-cdk-node
     */
    @Test
    public void assignDbStereo() throws Exception {
        String in = "C(/N)=C\\C=C\\1/N=C1";
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles(in);
        Assert.assertEquals("C(\\N)=C/C=C/1N=C1", SmilesGenerator.isomeric().create(mol));
    }

    @Test public void canonicalReactions() throws Exception {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IReaction r1 = smipar.parseReactionSmiles("CC(C)C1=CC=CC=C1.C(CC(=O)Cl)CCl>[Al+3].[Cl-].[Cl-].[Cl-].C(Cl)Cl>CC(C)C1=CC=C(C=C1)C(=O)CCCCl");
        IReaction r2 = smipar.parseReactionSmiles("C(CC(=O)Cl)CCl.CC(C)C1=CC=CC=C1>[Al+3].[Cl-].[Cl-].[Cl-].C(Cl)Cl>CC(C)C1=CC=C(C=C1)C(=O)CCCCl");
        IReaction r3 = smipar.parseReactionSmiles("CC(C)C1=CC=CC=C1.C(CC(=O)Cl)CCl>C(Cl)Cl.[Al+3].[Cl-].[Cl-].[Cl-]>CC(C)C1=CC=C(C=C1)C(=O)CCCCl");
        SmilesGenerator smigen = new SmilesGenerator(SmiFlavor.Canonical);
        assertThat(smigen.create(r1), is(smigen.create(r2)));
        assertThat(smigen.create(r2), is(smigen.create(r3)));
    }

    @Test(expected = IllegalStateException.class)
    public void inconsistentAromaticState() throws Exception {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles("c1ccccc1");
        for (IAtom atom : mol.atoms())
            atom.setIsAromatic(false);
        SmilesGenerator smigen = new SmilesGenerator(SmiFlavor.UseAromaticSymbols);
        smigen.create(mol);
    }

    @Test
    public void strictIsotopes() throws CDKException {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles("[12CH3]C");
        assertThat(new SmilesGenerator(SmiFlavor.AtomicMassStrict).create(mol), is("[12CH3]C"));
    }

    @Test
    public void isotopes() throws CDKException {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles("[12CH3]C");
        assertThat(new SmilesGenerator(SmiFlavor.AtomicMass).create(mol), is("[12CH3]C"));
    }

    @Test
    public void cyclobutene() throws CDKException {
        SmilesParser   smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol    = smipar.parseSmiles("C1(C)=C(c2ccccc2)C=C1.C1(C)C(c2ccccc2)=CC=1");
        // by default we generate SMILES that allows all double bonds to move
        // and remove differences just because of kekule assignment. This matches
        // InChI behavior
        assertThat(new SmilesGenerator(SmiFlavor.Canonical).create(mol),
                   is("C=1C=CC(=CC1)C=2C=CC2C.C=1C=CC(=CC1)C=2C=CC2C"));
        // this might not be desirable in some cases, so if UseAromaticSymbols
        // is set, double bonds are in rings are not allowed to move
        assertThat(new SmilesGenerator(SmiFlavor.Canonical + SmiFlavor.UseAromaticSymbols).create(mol),
                   is("c1ccc(cc1)C=2C=CC2C.c1ccc(cc1)C2=CC=C2C"));
    }

    @Test
    public void roundTripExtendedCisTrans() throws CDKException {
        SmilesParser   smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol    = smipar.parseSmiles("C/C=C=C=C/C");
        assertThat(new SmilesGenerator(SmiFlavor.Stereo).create(mol),
                   is("C/C=C=C=C/C"));
        for (IStereoElement se : mol.stereoElements()) {
            se.setConfigOrder(se.getConfigOrder() ^ 0x3); // flip
        }
        assertThat(new SmilesGenerator(SmiFlavor.Stereo).create(mol),
                   is("C/C=C=C=C\\C"));
    }

    @Test
    public void canonAtomMaps() throws CDKException {
        SmilesParser   smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol    = smipar.parseSmiles("[*:2]C(CC[*:3])[*:1]");
        assertThat(new SmilesGenerator(SmiFlavor.Canonical|SmiFlavor.AtomAtomMap).create(mol),
                   is("[*:1]C([*:2])CC[*:3]"));
        IAtomContainer mol2    = smipar.parseSmiles("[*:2]C(CC[*:1])[*:2]");
        assertThat(new SmilesGenerator(SmiFlavor.Canonical|SmiFlavor.AtomAtomMap).create(mol2),
                   is("[*:1]CCC([*:2])[*:2]"));
    }

    @Test
    public void canonAtomMapsRenumber() throws CDKException {
        SmilesParser   smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol    = smipar.parseSmiles("[*:2]C(CC[*:3])[*:1]");
        assertThat(new SmilesGenerator(SmiFlavor.Canonical|SmiFlavor.AtomAtomMapRenumber).create(mol),
                   is("[*:1]CCC([*:2])[*:3]"));
        IAtomContainer mol2    = smipar.parseSmiles("[*:3]C(CC[*:1])[*:2]");
        assertThat(new SmilesGenerator(SmiFlavor.Canonical|SmiFlavor.AtomAtomMapRenumber).create(mol2),
                   is("[*:1]CCC([*:2])[*:3]"));
    }

    static ITetrahedralChirality anticlockwise(IAtomContainer container, int central, int a1, int a2, int a3, int a4) {
        return new TetrahedralChirality(container.getAtom(central), new IAtom[]{container.getAtom(a1),
                                                                                container.getAtom(a2), container.getAtom(a3), container.getAtom(a4)},
                                        ITetrahedralChirality.Stereo.ANTI_CLOCKWISE);
    }

    static ITetrahedralChirality clockwise(IAtomContainer container, int central, int a1, int a2, int a3, int a4) {
        return new TetrahedralChirality(container.getAtom(central), new IAtom[]{container.getAtom(a1),
                                                                                container.getAtom(a2), container.getAtom(a3), container.getAtom(a4)},
                                        ITetrahedralChirality.Stereo.CLOCKWISE);
    }

    static void define(IAtomContainer container, IStereoElement... elements) {
        container.setStereoElements(Arrays.<IStereoElement>asList(elements));
    }

    static String canon(String smi) throws Exception {
        final IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        final SmilesParser smipar = new SmilesParser(bldr);
        final IAtomContainer container = smipar.parseSmiles(smi);
        AtomContainerManipulator.suppressHydrogens(container);
        Aromaticity arom = new Aromaticity(ElectronDonation.daylight(),
                                           Cycles.all());
        arom.apply(container);
        return SmilesGenerator.unique().create(container);
    }
}
