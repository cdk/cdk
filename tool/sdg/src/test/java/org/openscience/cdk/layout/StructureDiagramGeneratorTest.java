/*  Copyright (C) 2003-2007  Christoph Steinbeck <steinbeck@users.sf.net>
 *                     2009  Mark Rijnbeek <markr@ebi.ac.uk>
 *                     2009  Mark Rijnbeek <mark_rynbeek@users.sf.net>
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
package org.openscience.cdk.layout;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.io.StringReader;

import javax.vecmath.Vector2d;

import org.hamcrest.number.OrderingComparison;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.MDLV2000Writer;
import org.openscience.cdk.io.Mol2Reader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 *  A set of test cases for the StructureDiagramGenerator
 *
 * @cdk.module test-sdg
 *
 *@author     steinbeck
 *@cdk.created    August 29, 2003
 */
public class StructureDiagramGeneratorTest extends CDKTestCase {

    boolean standAlone = false;

    /**
     * Called just before each test method.
     */
    @Before
    public void setUp() {}

    /**
     * Called just after each test method.
     */
    @After
    public void tearDown() {}

    public IAtomContainer generateCoordinates(IAtomContainer m) throws Exception {
        StructureDiagramGenerator sdg = new StructureDiagramGenerator();
        sdg.setUseTemplates(true);
        sdg.setMolecule(m);
        sdg.generateCoordinates(new Vector2d(0, 1));
        return sdg.getMolecule();
    }

    public void visualBugPMR() throws Exception {
        String filename = "data/cml/SL0016a.cml";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        IChemSequence seq = chemFile.getChemSequence(0);
        IChemModel model = seq.getChemModel(0);
        IAtomContainer mol = model.getMoleculeSet().getAtomContainer(0);
        //MoleculeViewer2D.display(mol, true, false, JFrame.DO_NOTHING_ON_CLOSE,"");

    }

    /**
     *  A unit test for JUnit
     *
     *@exception  Exception  thrown if something goes wrong
     *@cdk.bug 1670871
     */
    @Test(timeout = 5000)
    public void testBugLecture2007() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        //IAtomContainer mol = sp.parseSmiles("Oc1nc(nc2c(nn(c12)C)CCC)c3cc(ccc3(OCC))S(=O)(=O)N4CCN(C)CC4");
        IAtomContainer mol = sp.parseSmiles("O=C(N1CCN(CC1)CCCN(C)C)C3(C=2C=CC(=CC=2)C)(CCCCC3)");

        //IAtomContainer mol = sp.parseSmiles("C1CCC1CCCCCCCC1CC1");

        IAtomContainer ac = generateCoordinates(mol);
        //		MoleculeViewer2D.display(new AtomContainer(ac), false);
        assertTrue(GeometryUtil.has2DCoordinates(ac));
    }

    /**
     *  A unit test for JUnit
     */
    @Test(timeout = 5000)
    public void testAlphaPinene() throws Exception {
        IAtomContainer m = TestMoleculeFactory.makeAlphaPinene();
        IAtomContainer ac = generateCoordinates(m);
        assertTrue(GeometryUtil.has2DCoordinates(ac));
    }

    @Test(timeout = 5000)
    public void testBridgedHydrogen() throws Exception {
        IAtomContainer inputStructure = new AtomContainer();
        IAtom carbon1 = new Atom("C");
        IAtom carbon2 = new Atom("C");
        IAtom bridgingHydrogen = new Atom("H");
        inputStructure.addAtom(carbon1);
        inputStructure.addAtom(bridgingHydrogen);
        inputStructure.addAtom(carbon2);
        inputStructure.addBond(0, 1, IBond.Order.SINGLE);
        inputStructure.addBond(1, 2, IBond.Order.SINGLE);
        StructureDiagramGenerator sdg = new StructureDiagramGenerator();
        sdg.setUseTemplates(true);
        sdg.setMolecule(inputStructure);
        sdg.generateExperimentalCoordinates(new Vector2d(0, 1));
        IAtomContainer structureWithCoordinates = sdg.getMolecule();
        System.out.println(structureWithCoordinates.getAtomCount());
        assertTrue(GeometryUtil.has2DCoordinates(structureWithCoordinates));
    }

    /**
     *  A unit test for JUnit
     */
    @Test(timeout = 5000)
    public void testBiphenyl() throws Exception {
        IAtomContainer m = TestMoleculeFactory.makeBiphenyl();
        IAtomContainer ac = generateCoordinates(m);
        assertTrue(GeometryUtil.has2DCoordinates(ac));
    }

    /**
     *  A unit test for JUnit
     */
    @Test(timeout = 5000)
    public void test4x3CondensedRings() throws Exception {
        IAtomContainer m = TestMoleculeFactory.make4x3CondensedRings();
        IAtomContainer ac = generateCoordinates(m);
        assertTrue(GeometryUtil.has2DCoordinates(ac));
    }

    /**
     *  A unit test for JUnit
     */
    @Test(timeout = 5000)
    public void testPhenylEthylBenzene() throws Exception {
        IAtomContainer m = TestMoleculeFactory.makePhenylEthylBenzene();
        IAtomContainer ac = generateCoordinates(m);
        assertTrue(GeometryUtil.has2DCoordinates(ac));
    }

    /**
     *  A unit test for JUnit
     */
    @Test(timeout = 5000)
    public void testSpiroRings() throws Exception {
        IAtomContainer m = TestMoleculeFactory.makeSpiroRings();
        IAtomContainer ac = generateCoordinates(m);
        assertTrue(GeometryUtil.has2DCoordinates(ac));
    }

    /**
     *  A unit test for JUnit
     */
    @Test(timeout = 5000)
    public void testMethylDecaline() throws Exception {
        IAtomContainer m = TestMoleculeFactory.makeMethylDecaline();
        IAtomContainer ac = generateCoordinates(m);
        assertTrue(GeometryUtil.has2DCoordinates(ac));
    }

    /**
     *  A unit test for JUnit
     */
    @Test(timeout = 5000)
    public void testBranchedAliphatic() throws Exception {
        IAtomContainer m = TestMoleculeFactory.makeBranchedAliphatic();
        IAtomContainer ac = generateCoordinates(m);
        assertTrue(GeometryUtil.has2DCoordinates(ac));
    }

    /**
     *  A unit test for JUnit
     */
    @Test(timeout = 5000)
    public void testDiamantane() throws Exception {
        IAtomContainer m = TestMoleculeFactory.makeDiamantane();
        IAtomContainer ac = generateCoordinates(m);
        assertTrue(GeometryUtil.has2DCoordinates(ac));
    }

    /**
     *  A unit test for JUnit
     *
     *@exception  Exception  thrown if something goes wrong
     *@cdk.bug 1670871
     */
    @Test(timeout = 5000)
    public void testBug1670871() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CC(=O)OC1C=CC(SC23CC4CC(CC(C4)C2)C3)N(C1SC56CC7CC(CC(C7)C5)C6)C(C)=O");
        IAtomContainer ac = generateCoordinates(mol);
        //MoleculeViewer2D.display(new AtomContainer(ac), false);
        assertTrue(GeometryUtil.has2DCoordinates(ac));
    }

    /**
     *  A unit test for JUnit
     */
    @Test(timeout = 5000)
    public void testEthylCyclohexane() throws Exception {
        IAtomContainer m = TestMoleculeFactory.makeEthylCyclohexane();
        IAtomContainer ac = generateCoordinates(m);
        assertTrue(GeometryUtil.has2DCoordinates(ac));
    }

    /**
     *  A unit test for JUnit
     */
    @Test(timeout = 5000)
    public void testBicycloRings() throws Exception {
        IAtomContainer m = TestMoleculeFactory.makeBicycloRings();
        IAtomContainer ac = generateCoordinates(m);
        assertTrue(GeometryUtil.has2DCoordinates(ac));
    }

    /**
     *  A unit test for JUnit
     */
    public IAtomContainer makeJhao3() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C=C1C2=CC13(CC23)");
        return mol;
    }

    /**
     *  A unit test for JUnit
     */
    public IAtomContainer makeJhao4() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCC3C1CC23(CC12)");
        return mol;
    }

    /**
     *  A unit test for JUnit
     */
    @Test(timeout = 5000)
    public void testBenzene() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("c1ccccc1");
        IAtomContainer ac = generateCoordinates(mol);
        assertTrue(GeometryUtil.has2DCoordinates(ac));
    }

    /**
     * @cdk.bug 780545
     */
    @Test(timeout = 5000)
    public void testBug780545() throws Exception {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C"));
        IAtomContainer ac = generateCoordinates(mol);
        assertTrue(GeometryUtil.has2DCoordinates(ac));
    }

    /**
     * @cdk.bug 1598409
     */
    @Test(timeout = 5000)
    public void testBug1598409() throws Exception {
        String smiles = "c1(:c(:c2-C(-c3:c(-C(=O)-c:2:c(:c:1-[H])-[H]):c(:c(:c(:c:3-[H])-[H])-N(-[H])-[H])-[H])=O)-[H])-[H]";
        SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer cdkMol = parser.parseSmiles(smiles);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(cdkMol);
        Aromaticity.cdkLegacy().apply(cdkMol);
        new StructureDiagramGenerator(cdkMol).generateCoordinates();
    }

    /**
     * @cdk.bug 1572062
     */
    @Test(timeout = 5000)
    public void testBug1572062() throws Exception {
        String filename = "data/mdl/sdg_test.mol";

        //		set up molecule reader
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        ISimpleChemObjectReader molReader = new MDLV2000Reader(ins, Mode.STRICT);

        //		read molecule
        IAtomContainer molecule = molReader.read(DefaultChemObjectBuilder.getInstance().newInstance(
                IAtomContainer.class));

        //		rebuild 2D coordinates
        StructureDiagramGenerator structureDiagramGenerator = new StructureDiagramGenerator();
        for (int i = 0; i < 10; i++) {
            structureDiagramGenerator.setMolecule(molecule);
            structureDiagramGenerator.generateCoordinates();
        }

    }

    /**
     * @cdk.bug 884993
     */
    @Test(timeout = 5000)
    public void testBug884993() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp
                .parseSmiles("[N+](=O)([O-])C1=C(O)C(=CC(=C1)[N+](=O)[O-])[N+](=O)[O-].C23N(CCCC2)CCCC3");
        try {
            IAtomContainer ac = generateCoordinates(mol);
            assertTrue(GeometryUtil.has2DCoordinates(ac));
            fail("This should have thrown a 'Molecule not connected' exception.");
        } catch (Exception exc) {
            // OK, an exception should have been thrown
            if (!(exc.toString().indexOf("Molecule not connected") >= 0)) fail();
        }
    }

    /**
     * Test for bug #1677912 "SDG JUnit test hangs"
     * The SMILES parsing takes hence a longer timeout.
     * @cdk.bug 1677912
     */
    @Test(timeout = 10000)
    public void testBug1677912SDGHangs() throws Exception {
        // Parse the SMILES
        String smiles = "[NH](-[CH]1-[CH]2-[CH2]-[CH]3-[CH2]-[CH]-1-[CH2]-[CH](-[CH2]-2)-[CH2]-3)-C(=O)-C(=O)-[CH2]-c1:n:c(:c(:[cH]:c:1-C(=O)-O-[CH3])-C(=O)-O-[CH3])-[CH2]-C(=O)-C(=O)-[NH]-[CH]1-[CH]2-[CH2]-[CH]3-[CH2]-[CH]-1-[CH2]-[CH](-[CH2]-2)-[CH2]-3";
        SmilesParser smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer molecule = smilesParser.parseSmiles(smiles);

        // Generate 2D coordinates
        StructureDiagramGenerator structureDiagramGenerator = new StructureDiagramGenerator();
        structureDiagramGenerator.setMolecule(molecule);
        structureDiagramGenerator.generateCoordinates();

        // Test completed, no timeout occured
    }

    /**
     * @cdk.bug 1714794
     */
    @Test(timeout = 5000)
    public void testBug1714794() throws Exception {
        String problematicMol2AsSmiles = "N1c2c(c3c(c4c(c(c3O)C)OC(OC=CC(C(C(C(C(C(C(C(C=CC=C(C1=O)C)C)O)C)O)C)OC(=O)C)C)OC)(C4=O)C)c(c2C=NN(C12CC3CC(C1)CC(C2)C3)C)O)O";
        SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer cdkMol = parser.parseSmiles(problematicMol2AsSmiles);
        new StructureDiagramGenerator(cdkMol).generateCoordinates();
        assertTrue(GeometryUtil.has2DCoordinates(cdkMol));

        String problematicMol2 = "@<TRIPOS>MOLECULE\n" + "mol_197219.smi\n" + " 129 135 0 0 0\n" + "SMALL\n"
                + "GASTEIGER\n" + "Energy = 0\n" + "\n" + "@<TRIPOS>ATOM\n"
                + "      1 N1          0.0000    0.0000    0.0000 N.am    1  <1>        -0.2782\n"
                + "      2 H1          0.0000    0.0000    0.0000 H       1  <1>         0.1552\n"
                + "      3 C1          0.0000    0.0000    0.0000 C.ar    1  <1>         0.0886\n"
                + "      4 C2          0.0000    0.0000    0.0000 C.ar    1  <1>         0.1500\n"
                + "      5 C3          0.0000    0.0000    0.0000 C.ar    1  <1>         0.0714\n"
                + "      6 C4          0.0000    0.0000    0.0000 C.ar    1  <1>         0.0456\n"
                + "      7 C5          0.0000    0.0000    0.0000 C.ar    1  <1>         0.0788\n"
                + "      8 C6          0.0000    0.0000    0.0000 C.ar    1  <1>         0.1435\n"
                + "      9 C7          0.0000    0.0000    0.0000 C.ar    1  <1>         0.0342\n"
                + "     10 C8          0.0000    0.0000    0.0000 C.ar    1  <1>         0.1346\n"
                + "     11 O1          0.0000    0.0000    0.0000 O.3     1  <1>        -0.5057\n"
                + "     12 H2          0.0000    0.0000    0.0000 H       1  <1>         0.2922\n"
                + "     13 C9          0.0000    0.0000    0.0000 C.3     1  <1>        -0.0327\n"
                + "     14 H3          0.0000    0.0000    0.0000 H       1  <1>         0.0280\n"
                + "     15 H4          0.0000    0.0000    0.0000 H       1  <1>         0.0280\n"
                + "     16 H5          0.0000    0.0000    0.0000 H       1  <1>         0.0280\n"
                + "     17 O2          0.0000    0.0000    0.0000 O.3     1  <1>        -0.4436\n"
                + "     18 C10         0.0000    0.0000    0.0000 C.3     1  <1>         0.3143\n"
                + "     19 O3          0.0000    0.0000    0.0000 O.2     1  <1>        -0.4528\n"
                + "     20 C11         0.0000    0.0000    0.0000 C.2     1  <1>         0.0882\n"
                + "     21 H6          0.0000    0.0000    0.0000 H       1  <1>         0.1022\n"
                + "     22 C12         0.0000    0.0000    0.0000 C.2     1  <1>        -0.0208\n"
                + "     23 H7          0.0000    0.0000    0.0000 H       1  <1>         0.0628\n"
                + "     24 C13         0.0000    0.0000    0.0000 C.3     1  <1>         0.0854\n"
                + "     25 H8          0.0000    0.0000    0.0000 H       1  <1>         0.0645\n"
                + "     26 C14         0.0000    0.0000    0.0000 C.3     1  <1>         0.0236\n"
                + "     27 H9          0.0000    0.0000    0.0000 H       1  <1>         0.0362\n"
                + "     28 C15         0.0000    0.0000    0.0000 C.3     1  <1>         0.1131\n"
                + "     29 H10         0.0000    0.0000    0.0000 H       1  <1>         0.0741\n"
                + "     30 C16         0.0000    0.0000    0.0000 C.3     1  <1>         0.0200\n"
                + "     31 H11         0.0000    0.0000    0.0000 H       1  <1>         0.0359\n"
                + "     32 C17         0.0000    0.0000    0.0000 C.3     1  <1>         0.0661\n"
                + "     33 H12         0.0000    0.0000    0.0000 H       1  <1>         0.0600\n"
                + "     34 C18         0.0000    0.0000    0.0000 C.3     1  <1>         0.0091\n"
                + "     35 H13         0.0000    0.0000    0.0000 H       1  <1>         0.0348\n"
                + "     36 C19         0.0000    0.0000    0.0000 C.3     1  <1>         0.0661\n"
                + "     37 H14         0.0000    0.0000    0.0000 H       1  <1>         0.0602\n"
                + "     38 C20         0.0000    0.0000    0.0000 C.3     1  <1>         0.0009\n"
                + "     39 H15         0.0000    0.0000    0.0000 H       1  <1>         0.0365\n"
                + "     40 C21         0.0000    0.0000    0.0000 C.2     1  <1>        -0.0787\n"
                + "     41 H16         0.0000    0.0000    0.0000 H       1  <1>         0.0576\n"
                + "     42 C22         0.0000    0.0000    0.0000 C.2     1  <1>        -0.0649\n"
                + "     43 H17         0.0000    0.0000    0.0000 H       1  <1>         0.0615\n"
                + "     44 C23         0.0000    0.0000    0.0000 C.2     1  <1>        -0.0542\n"
                + "     45 H18         0.0000    0.0000    0.0000 H       1  <1>         0.0622\n"
                + "     46 C24         0.0000    0.0000    0.0000 C.2     1  <1>         0.0115\n"
                + "     47 C25         0.0000    0.0000    0.0000 C.2     1  <1>         0.2441\n"
                + "     48 O4          0.0000    0.0000    0.0000 O.2     1  <1>        -0.2702\n"
                + "     49 C26         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0348\n"
                + "     50 H19         0.0000    0.0000    0.0000 H       1  <1>         0.0279\n"
                + "     51 H20         0.0000    0.0000    0.0000 H       1  <1>         0.0279\n"
                + "     52 H21         0.0000    0.0000    0.0000 H       1  <1>         0.0279\n"
                + "     53 C27         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0566\n"
                + "     54 H22         0.0000    0.0000    0.0000 H       1  <1>         0.0236\n"
                + "     55 H23         0.0000    0.0000    0.0000 H       1  <1>         0.0236\n"
                + "     56 H24         0.0000    0.0000    0.0000 H       1  <1>         0.0236\n"
                + "     57 O5          0.0000    0.0000    0.0000 O.3     1  <1>        -0.3909\n"
                + "     58 H25         0.0000    0.0000    0.0000 H       1  <1>         0.2098\n"
                + "     59 C28         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0577\n"
                + "     60 H26         0.0000    0.0000    0.0000 H       1  <1>         0.0234\n"
                + "     61 H27         0.0000    0.0000    0.0000 H       1  <1>         0.0234\n"
                + "     62 H28         0.0000    0.0000    0.0000 H       1  <1>         0.0234\n"
                + "     63 O6          0.0000    0.0000    0.0000 O.3     1  <1>        -0.3910\n"
                + "     64 H29         0.0000    0.0000    0.0000 H       1  <1>         0.2098\n"
                + "     65 C29         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0567\n"
                + "     66 H30         0.0000    0.0000    0.0000 H       1  <1>         0.0234\n"
                + "     67 H31         0.0000    0.0000    0.0000 H       1  <1>         0.0234\n"
                + "     68 H32         0.0000    0.0000    0.0000 H       1  <1>         0.0234\n"
                + "     69 O7          0.0000    0.0000    0.0000 O.3     1  <1>        -0.4608\n"
                + "     70 C30         0.0000    0.0000    0.0000 C.2     1  <1>         0.3042\n"
                + "     71 O8          0.0000    0.0000    0.0000 O.2     1  <1>        -0.2512\n"
                + "     72 C31         0.0000    0.0000    0.0000 C.3     1  <1>         0.0332\n"
                + "     73 H33         0.0000    0.0000    0.0000 H       1  <1>         0.0342\n"
                + "     74 H34         0.0000    0.0000    0.0000 H       1  <1>         0.0342\n"
                + "     75 H35         0.0000    0.0000    0.0000 H       1  <1>         0.0342\n"
                + "     76 C32         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0564\n"
                + "     77 H36         0.0000    0.0000    0.0000 H       1  <1>         0.0234\n"
                + "     78 H37         0.0000    0.0000    0.0000 H       1  <1>         0.0234\n"
                + "     79 H38         0.0000    0.0000    0.0000 H       1  <1>         0.0234\n"
                + "     80 O9          0.0000    0.0000    0.0000 O.3     1  <1>        -0.3753\n"
                + "     81 C33         0.0000    0.0000    0.0000 C.3     1  <1>         0.0372\n"
                + "     82 H39         0.0000    0.0000    0.0000 H       1  <1>         0.0524\n"
                + "     83 H40         0.0000    0.0000    0.0000 H       1  <1>         0.0524\n"
                + "     84 H41         0.0000    0.0000    0.0000 H       1  <1>         0.0524\n"
                + "     85 C34         0.0000    0.0000    0.0000 C.2     1  <1>         0.2505\n"
                + "     86 O10         0.0000    0.0000    0.0000 O.2     1  <1>        -0.2836\n"
                + "     87 C35         0.0000    0.0000    0.0000 C.3     1  <1>         0.0210\n"
                + "     88 H42         0.0000    0.0000    0.0000 H       1  <1>         0.0309\n"
                + "     89 H43         0.0000    0.0000    0.0000 H       1  <1>         0.0309\n"
                + "     90 H44         0.0000    0.0000    0.0000 H       1  <1>         0.0309\n"
                + "     91 C36         0.0000    0.0000    0.0000 C.ar    1  <1>         0.1361\n"
                + "     92 C37         0.0000    0.0000    0.0000 C.ar    1  <1>         0.0613\n"
                + "     93 C38         0.0000    0.0000    0.0000 C.2     1  <1>         0.0580\n"
                + "     94 H45         0.0000    0.0000    0.0000 H       1  <1>         0.0853\n"
                + "     95 N2          0.0000    0.0000    0.0000 N.2     1  <1>        -0.1915\n"
                + "     96 N3          0.0000    0.0000    0.0000 N.pl3   1  <1>        -0.2525\n"
                + "     97 C39         0.0000    0.0000    0.0000 C.3     1  <1>         0.0525\n"
                + "     98 C40         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0271\n"
                + "     99 H46         0.0000    0.0000    0.0000 H       1  <1>         0.0289\n"
                + "    100 H47         0.0000    0.0000    0.0000 H       1  <1>         0.0289\n"
                + "    101 C41         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0385\n"
                + "    102 H48         0.0000    0.0000    0.0000 H       1  <1>         0.0302\n"
                + "    103 C42         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0472\n"
                + "    104 H49         0.0000    0.0000    0.0000 H       1  <1>         0.0271\n"
                + "    105 H50         0.0000    0.0000    0.0000 H       1  <1>         0.0271\n"
                + "    106 C43         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0385\n"
                + "    107 H51         0.0000    0.0000    0.0000 H       1  <1>         0.0302\n"
                + "    108 C44         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0271\n"
                + "    109 H52         0.0000    0.0000    0.0000 H       1  <1>         0.0289\n"
                + "    110 H53         0.0000    0.0000    0.0000 H       1  <1>         0.0289\n"
                + "    111 C45         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0472\n"
                + "    112 H54         0.0000    0.0000    0.0000 H       1  <1>         0.0271\n"
                + "    113 H55         0.0000    0.0000    0.0000 H       1  <1>         0.0271\n"
                + "    114 C46         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0385\n"
                + "    115 H56         0.0000    0.0000    0.0000 H       1  <1>         0.0302\n"
                + "    116 C47         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0271\n"
                + "    117 H57         0.0000    0.0000    0.0000 H       1  <1>         0.0289\n"
                + "    118 H58         0.0000    0.0000    0.0000 H       1  <1>         0.0289\n"
                + "    119 C48         0.0000    0.0000    0.0000 C.3     1  <1>        -0.0472\n"
                + "    120 H59         0.0000    0.0000    0.0000 H       1  <1>         0.0271\n"
                + "    121 H60         0.0000    0.0000    0.0000 H       1  <1>         0.0271\n"
                + "    122 C49         0.0000    0.0000    0.0000 C.3     1  <1>         0.0189\n"
                + "    123 H61         0.0000    0.0000    0.0000 H       1  <1>         0.0444\n"
                + "    124 H62         0.0000    0.0000    0.0000 H       1  <1>         0.0444\n"
                + "    125 H63         0.0000    0.0000    0.0000 H       1  <1>         0.0444\n"
                + "    126 O11         0.0000    0.0000    0.0000 O.3     1  <1>        -0.5054\n"
                + "    127 H64         0.0000    0.0000    0.0000 H       1  <1>         0.2922\n"
                + "    128 O12         0.0000    0.0000    0.0000 O.3     1  <1>        -0.5042\n"
                + "    129 H65         0.0000    0.0000    0.0000 H       1  <1>         0.2923\n" + "@<TRIPOS>BOND\n"
                + "     1     1     2    1\n" + "     2     1     3    1\n" + "     3     3     4   ar\n"
                + "     4     4     5   ar\n" + "     5     5     6   ar\n" + "     6     6     7   ar\n"
                + "     7     7     8   ar\n" + "     8     8     9   ar\n" + "     9     9    10   ar\n"
                + "    10     5    10   ar\n" + "    11    10    11    1\n" + "    12    11    12    1\n"
                + "    13     9    13    1\n" + "    14    13    14    1\n" + "    15    13    15    1\n"
                + "    16    13    16    1\n" + "    17     8    17    1\n" + "    18    17    18    1\n"
                + "    19    18    19    1\n" + "    20    19    20    1\n" + "    21    20    21    1\n"
                + "    22    20    22    2\n" + "    23    22    23    1\n" + "    24    22    24    1\n"
                + "    25    24    25    1\n" + "    26    24    26    1\n" + "    27    26    27    1\n"
                + "    28    26    28    1\n" + "    29    28    29    1\n" + "    30    28    30    1\n"
                + "    31    30    31    1\n" + "    32    30    32    1\n" + "    33    32    33    1\n"
                + "    34    32    34    1\n" + "    35    34    35    1\n" + "    36    34    36    1\n"
                + "    37    36    37    1\n" + "    38    36    38    1\n" + "    39    38    39    1\n"
                + "    40    38    40    1\n" + "    41    40    41    1\n" + "    42    40    42    2\n"
                + "    43    42    43    1\n" + "    44    42    44    1\n" + "    45    44    45    1\n"
                + "    46    44    46    2\n" + "    47    46    47    1\n" + "    48     1    47   am\n"
                + "    49    47    48    2\n" + "    50    46    49    1\n" + "    51    49    50    1\n"
                + "    52    49    51    1\n" + "    53    49    52    1\n" + "    54    38    53    1\n"
                + "    55    53    54    1\n" + "    56    53    55    1\n" + "    57    53    56    1\n"
                + "    58    36    57    1\n" + "    59    57    58    1\n" + "    60    34    59    1\n"
                + "    61    59    60    1\n" + "    62    59    61    1\n" + "    63    59    62    1\n"
                + "    64    32    63    1\n" + "    65    63    64    1\n" + "    66    30    65    1\n"
                + "    67    65    66    1\n" + "    68    65    67    1\n" + "    69    65    68    1\n"
                + "    70    28    69    1\n" + "    71    69    70    1\n" + "    72    70    71    2\n"
                + "    73    70    72    1\n" + "    74    72    73    1\n" + "    75    72    74    1\n"
                + "    76    72    75    1\n" + "    77    26    76    1\n" + "    78    76    77    1\n"
                + "    79    76    78    1\n" + "    80    76    79    1\n" + "    81    24    80    1\n"
                + "    82    80    81    1\n" + "    83    81    82    1\n" + "    84    81    83    1\n"
                + "    85    81    84    1\n" + "    86    18    85    1\n" + "    87     7    85    1\n"
                + "    88    85    86    2\n" + "    89    18    87    1\n" + "    90    87    88    1\n"
                + "    91    87    89    1\n" + "    92    87    90    1\n" + "    93     6    91   ar\n"
                + "    94    91    92   ar\n" + "    95     3    92   ar\n" + "    96    92    93    1\n"
                + "    97    93    94    1\n" + "    98    93    95    2\n" + "    99    95    96    1\n"
                + "   100    96    97    1\n" + "   101    97    98    1\n" + "   102    98    99    1\n"
                + "   103    98   100    1\n" + "   104    98   101    1\n" + "   105   101   102    1\n"
                + "   106   101   103    1\n" + "   107   103   104    1\n" + "   108   103   105    1\n"
                + "   109   103   106    1\n" + "   110   106   107    1\n" + "   111   106   108    1\n"
                + "   112   108   109    1\n" + "   113   108   110    1\n" + "   114    97   108    1\n"
                + "   115   106   111    1\n" + "   116   111   112    1\n" + "   117   111   113    1\n"
                + "   118   111   114    1\n" + "   119   114   115    1\n" + "   120   114   116    1\n"
                + "   121   116   117    1\n" + "   122   116   118    1\n" + "   123    97   116    1\n"
                + "   124   114   119    1\n" + "   125   119   120    1\n" + "   126   119   121    1\n"
                + "   127   101   119    1\n" + "   128    96   122    1\n" + "   129   122   123    1\n"
                + "   130   122   124    1\n" + "   131   122   125    1\n" + "   132    91   126    1\n"
                + "   133   126   127    1\n" + "   134     4   128    1\n" + "   135   128   129    1\n";
        Mol2Reader r = new Mol2Reader(new StringReader(problematicMol2));
        IChemModel model = (IChemModel) r.read(SilentChemObjectBuilder.getInstance().newInstance(IChemModel.class));
        final IAtomContainer mol = model.getMoleculeSet().getAtomContainer(0);
        final IAtomContainer clone = (IAtomContainer) mol.clone();
        new StructureDiagramGenerator(mol.getBuilder().newInstance(IAtomContainer.class, clone)).generateCoordinates();
        assertTrue(GeometryUtil.has2DCoordinates(clone));
    }

    IAtomContainer makeTetraMethylCycloButane() {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5
        mol.addAtom(new Atom("C")); // 6
        mol.addAtom(new Atom("C")); // 7
        mol.addAtom(new Atom("C")); // 8

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.SINGLE); // 3
        mol.addBond(3, 0, IBond.Order.SINGLE); // 4
        mol.addBond(0, 4, IBond.Order.SINGLE); // 5
        mol.addBond(1, 5, IBond.Order.SINGLE); // 6
        mol.addBond(2, 6, IBond.Order.SINGLE); // 7
        mol.addBond(3, 7, IBond.Order.SINGLE); // 8
        return mol;
    }

    IAtomContainer makeJhao1() {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5
        mol.addAtom(new Atom("C")); // 6
        mol.addAtom(new Atom("C")); // 7
        mol.addAtom(new Atom("O")); // 8
        mol.addAtom(new Atom("C")); // 9

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(0, 3, IBond.Order.SINGLE); // 2
        mol.addBond(0, 4, IBond.Order.SINGLE); // 3
        mol.addBond(0, 7, IBond.Order.SINGLE); // 4
        mol.addBond(1, 4, IBond.Order.SINGLE); // 5
        mol.addBond(1, 5, IBond.Order.SINGLE); // 6
        mol.addBond(1, 6, IBond.Order.SINGLE); // 7
        mol.addBond(2, 3, IBond.Order.SINGLE); // 8
        mol.addBond(2, 5, IBond.Order.SINGLE); // 9
        mol.addBond(2, 6, IBond.Order.SINGLE); // 10
        mol.addBond(2, 7, IBond.Order.SINGLE); // 11
        mol.addBond(3, 8, IBond.Order.SINGLE); // 12
        return mol;
    }

    IAtomContainer makeJhao2() {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5
        mol.addAtom(new Atom("C")); // 6
        mol.addAtom(new Atom("C")); // 7
        mol.addAtom(new Atom("O")); // 8
        mol.addAtom(new Atom("C")); // 9

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(0, 3, IBond.Order.SINGLE); // 2
        mol.addBond(0, 4, IBond.Order.SINGLE); // 3
        mol.addBond(0, 7, IBond.Order.SINGLE); // 4
        mol.addBond(1, 5, IBond.Order.SINGLE); // 5
        mol.addBond(1, 6, IBond.Order.SINGLE); // 6
        mol.addBond(1, 7, IBond.Order.SINGLE); // 7
        mol.addBond(2, 3, IBond.Order.SINGLE); // 8
        mol.addBond(2, 4, IBond.Order.SINGLE); // 9
        mol.addBond(2, 5, IBond.Order.SINGLE); // 10
        mol.addBond(2, 6, IBond.Order.SINGLE); // 11
        mol.addBond(3, 8, IBond.Order.SINGLE); // 12
        return mol;
    }

    /**
     * @cdk.bug 1750968
     */
    public IAtomContainer makeBug1750968() throws Exception {
        String filename = "data/mdl/bug_1750968.mol";

        //		set up molecule reader
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        ISimpleChemObjectReader molReader = new MDLReader(ins, Mode.STRICT);

        //		read molecule
        return molReader.read(DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class));
    }

    /**
     * Test for StructureDiagramGenerator bug #1772609 "NPE with bridged rings in SDG/RingPlacer".
     * In method RingPlacer.placeBridgedRing(...) it could happen, that not all atoms of an unplaced
     * ring were selected for placing. Thus, those atoms later lacked 2D coordinates (they were null)
     * and the RingPlacer crashed with a NullPointerException such as:
     *
     * java.lang.NullPointerException
     *   at javax.vecmath.Tuple2d.<init>(Tuple2d.java:66)
     *   at javax.vecmath.Vector2d.<init>(Vector2d.java:74)
     *   at org.openscience.cdk.layout.RingPlacer.placeFusedRing(RingPlacer.java:379)
     *   at org.openscience.cdk.layout.RingPlacer.placeRing(RingPlacer.java:99)
     *   at org.openscience.cdk.layout.RingPlacer.placeConnectedRings(RingPlacer.java:663)
     *   at org.openscience.cdk.layout.StructureDiagramGenerator.layoutRingSet(StructureDiagramGenerator.java:516)
     *   at org.openscience.cdk.layout.StructureDiagramGenerator.generateCoordinates(StructureDiagramGenerator.java:379)
     *   at org.openscience.cdk.layout.StructureDiagramGenerator.generateCoordinates(StructureDiagramGenerator.java:445)
     *
     * Author: Andreas Schueller <a.schueller@chemie.uni-frankfurt.de>
     * @cdk.bug 1772609
     */
    @Test(timeout = 5000)
    public void testNPEWithBridgedRingsBug1772609() throws Exception {
        // set up molecule reader
        String filename = "data/mdl/bug1772609.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        ISimpleChemObjectReader molReader = new MDLV2000Reader(ins, Mode.STRICT);

        // read molecule
        IAtomContainer molecule = (IAtomContainer) molReader.read(DefaultChemObjectBuilder.getInstance().newInstance(
                IAtomContainer.class));

        // rebuild 2D coordinates
        // repeat this 10 times since the bug does only occur by chance
        StructureDiagramGenerator structureDiagramGenerator = new StructureDiagramGenerator();
        try {
            for (int i = 0; i < 10; i++) {
                structureDiagramGenerator.setMolecule(molecule);
                structureDiagramGenerator.generateCoordinates();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Test failed trying to layout bridged ring systems.");
        }
    }

    /**
     * Test for bug #1784850 "SDG hangs in infinite loop".
     * Fixed by correcting the safteyCounter check.
     *
     * Author: Andreas Schueller <a.schueller@chemie.uni-frankfurt.de>
     * @exception Exception is thown if something goes wrong
     * @cdk.bug 1784850
     */
    @Test(timeout = 5000)
    public void testBug1784850InfiniteLoop() throws Exception {
        // set up molecule reader
        String filename = "data/mdl/bug1784850.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        ISimpleChemObjectReader molReader = new MDLV2000Reader(ins, Mode.STRICT);

        // read molecule
        IAtomContainer molecule = molReader.read(DefaultChemObjectBuilder.getInstance().newInstance(
                IAtomContainer.class));

        // rebuild 2D coordinates
        StructureDiagramGenerator structureDiagramGenerator = new StructureDiagramGenerator();
        structureDiagramGenerator.setMolecule(molecule);
        structureDiagramGenerator.generateCoordinates();

        // test completed, no timeout occured
    }

    /**
    * For the SMILES compound below (the largest molecule in Chembl) a
    * handful of atoms had invalid (NaN) Double coordinates.
    *
    * @throws Exception if the test failed
    * @cdk.bug 2842445
    */
    @Test(timeout = 5000)
    public void testBug2843445NaNCoords() throws Exception {

        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        String smiles = "CCCC[C@H](NC(=O)[C@H](CCC(O)=O)NC(=O)[C@@H](NC(=O)[C@@H](CCCC)NC"
                + "(=O)[C@H](CC(N)=O)NC(=O)[C@H](CCC\\N=C(\\N)N)NC(=O)[C@H](CC(C)C)NC"
                + "(=O)[C@H](CC(C)C)NC(=O)[C@H](CC1=CNC=N1)NC(=O)[C@H](CC1=CC=CC=C1"
                + ")NC(=O)[C@@H](NC(=O)[C@H](CC(C)C)NC(=O)[C@H](CC(O)=O)NC(=O)[C@@H"
                + "](NC(=O)[C@H](CO)NC(=O)[C@@H](NC(=O)[C@@H]1CCCN1C(=O)[C@@H]1CCCN"
                + "1C(=O)[C@H](CC(O)=O)NC(=O)[C@H](CC(O)=O)NC(=O)[C@@H](N)CC(N)=O)["
                + "C@@H](C)CC)[C@@H](C)CC)[C@@H](C)O)[C@@H](C)CC)C(=O)N[C@@H](C)C(="
                + "O)N[C@@H](CCC\\N=C(\\N)N)C(=O)N[C@@H]([C@@H](C)CC)C(=O)N[C@@H](CCC"
                + "(O)=O)C(=O)N[C@@H](CC(N)=O)C(=O)N[C@@H](CCC(=O)OC)C(=O)N[C@@H](C"
                + "CC\\N=C(\\N)N)C(=O)N[C@@H](CCC(O)=O)C(=O)N[C@@H](CCC(O)=O)C(=O)N[C"
                + "@@H](C)C(=O)NCC(=O)N[C@@H](CCCCN)C(=O)N[C@@H](CC(N)=O)C(=O)N[C@@"
                + "H](CCC\\N=C(\\N)N)C(=O)N[C@@H](CCCCN)C(=O)N[C@@H](CC1=CC=C(O)C=C1)"
                + "C(=O)N[C@@H](CC(C)C)C(=O)N[C@@H](CC(O)=O)C(=O)N[C@@H](CCC(O)=O)C" + "(=O)N[C@@H](C(C)C)C(N)=O";
        IAtomContainer mol = sp.parseSmiles(smiles);

        StructureDiagramGenerator sdg = new StructureDiagramGenerator();
        sdg.setMolecule(mol);
        sdg.generateCoordinates(new Vector2d(0, 1));
        mol = sdg.getMolecule();

        int invalidCoordCount = 0;
        for (IAtom atom : mol.atoms()) {
            if (Double.isNaN(atom.getPoint2d().x) || Double.isNaN(atom.getPoint2d().y)) {
                invalidCoordCount++;
            }
        }
        Assert.assertEquals("No 2d coordinates should be NaN", 0, invalidCoordCount);
    }

    /**
     * The following SMILES compound gets null cordinates.
     *
     * @throws Exception if the test failed
     * @cdk.bug 1234
     */
    @Test(timeout = 5000, expected = CDKException.class)
    public void testBug1234() throws Exception {

        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        String smiles = "C1C1";

        IAtomContainer mol = sp.parseSmiles(smiles);
        StructureDiagramGenerator sdg = new StructureDiagramGenerator();
        sdg.setMolecule(mol);
        sdg.generateCoordinates(new Vector2d(0, 1));
        mol = sdg.getMolecule();

        int invalidCoordCount = 0;
        for (IAtom atom : mol.atoms()) {
            if (atom.getPoint2d() == null) {
                invalidCoordCount++;
            }
        }
        Assert.assertEquals("No 2d coordinates should be null", 0, invalidCoordCount);

    }

    /**
     * Tests case where calling generateExperimentalCoordinates threw an NPE.
     *
     * @cdk.bug 1269
     */
    @Test(timeout = 5000)
    public void testBug1269() throws Exception {

        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        String smiles = "O=C(O)[C@H](N)C"; // L-alanine, but any [C@H] will do
        IAtomContainer mol = sp.parseSmiles(smiles);

        StructureDiagramGenerator sdg = new StructureDiagramGenerator();
        sdg.setMolecule(mol);
        sdg.generateExperimentalCoordinates(new Vector2d(0, 1));
    }

    /**
     * Does the SDG handle non-connected molecules?
     *
     * @cdk.bug 1279
     */
    @Test(timeout = 5000, expected = Exception.class)
    public void testBug1279() throws Exception {

        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        String smiles = "[NH4+].CP(=O)(O)CCC(N)C(=O)[O-]";

        IAtomContainer mol = sp.parseSmiles(smiles);

        StructureDiagramGenerator sdg = new StructureDiagramGenerator();
        sdg.setMolecule(mol);
        sdg.generateCoordinates(new Vector2d(0, 1));
    }

    @Test public void alleneWithImplHDoesNotCauseNPE() throws Exception {

        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        String smiles = "CC=[C@]=CC";

        IAtomContainer mol = sp.parseSmiles(smiles);

        StructureDiagramGenerator sdg = new StructureDiagramGenerator();
        sdg.setMolecule(mol, false);
        sdg.generateCoordinates();
    }

    @Test
    public void pyrroleWithIdentityTemplate() throws Exception {

        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        String smiles = "C1=CNC=C1";

        StructureDiagramGenerator generator = new StructureDiagramGenerator();
        generator.setUseIdentityTemplates(true);
        generator.setUseTemplates(false);

        IAtomContainer mol = sp.parseSmiles(smiles);

        generator.setMolecule(mol, false);
        generator.generateCoordinates();

        IAtom nitrogen = mol.getAtom(2);

        // nitrogen is lowest point
        assertThat(nitrogen.getPoint2d().y, lessThan(mol.getAtom(0).getPoint2d().y));
        assertThat(nitrogen.getPoint2d().y, lessThan(mol.getAtom(1).getPoint2d().y));
        assertThat(nitrogen.getPoint2d().y, lessThan(mol.getAtom(3).getPoint2d().y));
        assertThat(nitrogen.getPoint2d().y, lessThan(mol.getAtom(4).getPoint2d().y));
    }

    @Test
    public void pyrroleWithoutIdentityTemplate() throws Exception {

        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        String smiles = "C1=CNC=C1";

        StructureDiagramGenerator generator = new StructureDiagramGenerator();
        generator.setUseIdentityTemplates(false);
        generator.setUseTemplates(false);

        IAtomContainer mol = sp.parseSmiles(smiles);

        generator.setMolecule(mol, false);
        generator.generateCoordinates();

        double minY = Double.MAX_VALUE;
        int i = -1;

        // note if the SDG changes the nitrogen might be at
        // the bottom by chance when generated ab initio
        for (int j = 0; j < mol.getAtomCount(); j++) {
            IAtom atom = mol.getAtom(j);
            if (atom.getPoint2d().y < minY) {
                minY = atom.getPoint2d().y;
                i = j;
            }
        }

        // N is at index 2
        assertThat(i, not(2));
    }

}
