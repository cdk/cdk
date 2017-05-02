/* Copyright (C) 1997-2007  Christian Hoppe <chhoppe@users.sf.net>
 *                     2006  Mario Baseda
 *
 *  Contact: cdk-devel@list.sourceforge.net
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
package org.openscience.cdk.modeling.builder3d;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.SlowTest;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import static org.junit.Assert.assertNotNull;

/**
 *  Description of the Class
 *
 * @cdk.module test-builder3d
 *
 *@author     chhoppe
 *@cdk.created    2004-11-04
 */
public class ModelBuilder3DTest extends CDKTestCase {

    boolean standAlone = false;

    /**
     *  Sets the standAlone attribute
     *
     *@param  standAlone  The new standAlone value
     */
    public void setStandAlone(boolean standAlone) {
        this.standAlone = standAlone;
    }

    //  A unit test for JUnit with methylenfluoride\
    @Test
    public void testModelBuilder3D_CF() throws Exception {
        ModelBuilder3D mb3d = ModelBuilder3D.getInstance(DefaultChemObjectBuilder.getInstance());
        Point3d c_coord = new Point3d(1.392, 0.0, 0.0);
        Point3d f_coord = new Point3d(0.0, 0.0, 0.0);
        Point3d h1_coord = new Point3d(1.7439615035767404, 1.0558845107302222, 0.0);
        Point3d h2_coord = new Point3d(1.7439615035767404, -0.5279422553651107, 0.914422809754875);
        Point3d h3_coord = new Point3d(1.7439615035767402, -0.5279422553651113, -0.9144228097548747);

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CF");
        addExplicitHydrogens(mol);
        //mb3d.setTemplateHandler();
        mol = mb3d.generate3DCoordinates(mol, false);
        assertEquals(c_coord, mol.getAtom(0).getPoint3d(), 0.0001);
        assertEquals(f_coord, mol.getAtom(1).getPoint3d(), 0.0001);
        assertEquals(h1_coord, mol.getAtom(2).getPoint3d(), 0.0001);
        assertEquals(h2_coord, mol.getAtom(3).getPoint3d(), 0.0001);
        assertEquals(h3_coord, mol.getAtom(4).getPoint3d(), 0.0001);
        checkAverageBondLength(mol);
    }

    @Test
    public void testModelBuilder3D_CccccC() throws Exception {
        ModelBuilder3D mb3d = ModelBuilder3D.getInstance(DefaultChemObjectBuilder.getInstance());
        String smile = "CccccC";
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles(smile);
        addExplicitHydrogens(mol);
        mol = mb3d.generate3DCoordinates(mol, false);
        for (int i = 0; i < mol.getAtomCount(); i++) {
            assertNotNull(mol.getAtom(i).getPoint3d());
        }
        checkAverageBondLength(mol);
        //logger.debug("Layout molecule with SMILE: "+smile);
    }

    @Test
    public void testModelBuilder3D_c1ccccc1C0() throws Exception {
        Assume.assumeTrue(runSlowTests());

        ModelBuilder3D mb3d = ModelBuilder3D.getInstance(DefaultChemObjectBuilder.getInstance());
        String smile = "c1ccccc1C=O";
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles(smile);
        addExplicitHydrogens(mol);
        mb3d.generate3DCoordinates(mol, false);
        for (int i = 0; i < mol.getAtomCount(); i++) {
            assertNotNull(mol.getAtom(i).getPoint3d());
        }
        checkAverageBondLength(mol);
    }

    @Test
    @Category(SlowTest.class)
    public void testModelBuilder3D_Konstanz() throws Exception {
        Assume.assumeTrue(runSlowTests());

        ModelBuilder3D mb3d = ModelBuilder3D.getInstance(DefaultChemObjectBuilder.getInstance());
        String smile = "C12(-[H])-C3(-C(-[H])(-[H])-C(-C4(-C5(-C(-Cl)(-Cl)-C(-C-3-4-[H])(-Cl)-C(-Cl)(-[H])-C-5(-Cl)-[H])-Cl)-[H])(-[H])-C-2(-O-1)-[H])-[H]";
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles(smile);
        addExplicitHydrogens(mol);
        mol = mb3d.generate3DCoordinates(mol, false);
        for (int i = 0; i < mol.getAtomCount(); i++) {
            assertNotNull(mol.getAtom(i).getPoint3d());
        }
        checkAverageBondLength(mol);
    }

    @Test
    public void xtestModelBuilder3D_Konstanz2() throws Exception {
        Assume.assumeTrue(runSlowTests());

        ModelBuilder3D mb3d = ModelBuilder3D.getInstance(DefaultChemObjectBuilder.getInstance());
        String smile = "c1(:c(:c(:c(-[H]):c(-Cl):c:1-[H])-[H])-[H])-[H]";
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles(smile);
        addExplicitHydrogens(mol);
        mol = mb3d.generate3DCoordinates(mol, false);
        for (int i = 0; i < mol.getAtomCount(); i++) {
            assertNotNull(mol.getAtom(i).getPoint3d());
        }
        checkAverageBondLength(mol);
    }

    @Test
    public void testModelBuilder3D_C1CCCCCCC1CC() throws Exception {
        Assume.assumeTrue(runSlowTests());

        ModelBuilder3D mb3d = ModelBuilder3D.getInstance(DefaultChemObjectBuilder.getInstance());
        String smile = "C1CCCCCCC1CC";
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles(smile);
        addExplicitHydrogens(mol);
        mol = mb3d.generate3DCoordinates(mol, false);
        for (int i = 0; i < mol.getAtomCount(); i++) {
            assertNotNull(mol.getAtom(i).getPoint3d());
        }
        checkAverageBondLength(mol);
    }

    /**
     * Bug #1610997 says the modelbuilder does not work if 2d coordinates exist before - we test this here
     *
     * @cdk.bug 1610997
     */
    @Test
    public void testModelBuilder3D_CCCCCCCCCC_with2d() throws Exception {
        Assume.assumeTrue(runSlowTests());

        ModelBuilder3D mb3d = ModelBuilder3D.getInstance(DefaultChemObjectBuilder.getInstance());
        String smile = "CCCCCCCCCC";
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles(smile);
        for (int i = 0; i < mol.getAtomCount(); i++) {
            mol.getAtom(i).setPoint2d(new Point2d(1, 1));
        }
        addExplicitHydrogens(mol);
        mol = mb3d.generate3DCoordinates(mol, false);
        for (int i = 0; i < mol.getAtomCount(); i++) {
            assertNotNull(mol.getAtom(i).getPoint3d());
        }
        checkAverageBondLength(mol);
    }

    /**
     * @cdk.bug 1315823
     */
    @Test
    @Category(SlowTest.class)
    public void testModelBuilder3D_232() throws Exception {
        Assume.assumeTrue(runSlowTests());

        ModelBuilder3D mb3d = ModelBuilder3D.getInstance(DefaultChemObjectBuilder.getInstance());
        String filename = "data/mdl/allmol232.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        reader.close();
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        IAtomContainer ac = new AtomContainer(containersList.get(0));
        addExplicitHydrogens(ac);
        ac = mb3d.generate3DCoordinates(ac, false);
        assertNotNull(ac.getAtom(0).getPoint3d());
        checkAverageBondLength(ac);
    }

    public static void checkAverageBondLength(IAtomContainer ac) {
        double avlength = GeometryUtil.getBondLengthAverage3D(ac);
        for (int i = 0; i < ac.getBondCount(); i++) {
            double distance = ac.getBond(i).getBegin().getPoint3d().distance(ac.getBond(i).getEnd().getPoint3d());
            Assert.assertTrue("Unreasonable bond length (" + distance + ") for bond " + i, distance >= avlength / 2
                    && distance <= avlength * 2);
        }
    }

    @Test
    public void testModelBuilder3D_231() throws Exception {
        Assume.assumeTrue(runSlowTests());

        ModelBuilder3D mb3d = ModelBuilder3D.getInstance(DefaultChemObjectBuilder.getInstance());
        String filename = "data/mdl/allmol231.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        reader.close();
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        IAtomContainer ac = new AtomContainer(containersList.get(0));
        addExplicitHydrogens(ac);
        ac = mb3d.generate3DCoordinates(ac, false);
        for (int i = 0; i < ac.getAtomCount(); i++) {
            assertNotNull(ac.getAtom(i).getPoint3d());
        }
        checkAverageBondLength(ac);
    }

    /**
     * Test for SF bug #1309731.
     * @cdk.bug 1309731
     */
    @Test
    public void testModelBuilder3D_keepChemObjectIDs() throws Exception {
        ModelBuilder3D mb3d = ModelBuilder3D.getInstance(DefaultChemObjectBuilder.getInstance());

        IAtomContainer methanol = new AtomContainer();
        IChemObjectBuilder builder = methanol.getBuilder();

        IAtom carbon1 = builder.newInstance(IAtom.class, "C");
        carbon1.setID("carbon1");
        methanol.addAtom(carbon1);
        for (int i = 0; i < 3; i++) {
            IAtom hydrogen = builder.newInstance(IAtom.class, "H");
            methanol.addAtom(hydrogen);
            methanol.addBond(builder.newInstance(IBond.class, carbon1, hydrogen, IBond.Order.SINGLE));
        }
        IAtom oxygen1 = builder.newInstance(IAtom.class, "O");
        oxygen1.setID("oxygen1");
        methanol.addAtom(oxygen1);
        methanol.addBond(builder.newInstance(IBond.class, carbon1, oxygen1, IBond.Order.SINGLE));
        IAtom hydrogen = builder.newInstance(IAtom.class, "H");
        methanol.addAtom(hydrogen);
        methanol.addBond(builder.newInstance(IBond.class, hydrogen, oxygen1, IBond.Order.SINGLE));

        Assert.assertEquals(6, methanol.getAtomCount());
        Assert.assertEquals(5, methanol.getBondCount());

        mb3d.generate3DCoordinates(methanol, false);

        checkAverageBondLength(methanol);
        Assert.assertEquals("carbon1", carbon1.getID());
        Assert.assertEquals("oxygen1", oxygen1.getID());
    }

    /*
     * this is a test contributed by mario baseda / see bug #1610997
     * @cdk.bug 1610997
     */
    @Test
    public void testModel3D_bug_1610997() throws Exception {
        Assume.assumeTrue(runSlowTests());

        boolean notCalculatedResults = false;
        List<IAtomContainer> inputList = new ArrayList<IAtomContainer>();

        ////////////////////////////////////////////////////////////////////////////////////////////
        //generate the input molecules. This are molecules without x, y, z coordinats

        String[] smiles = new String[]{"CC", "OCC", "O(C)CCC", "c1ccccc1", "C(=C)=C", "OCC=CCc1ccccc1(C=C)",
                "O(CC=C)CCN", "CCCCCCCCCCCCCCC", "OCC=CCO", "NCCCCN"};
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer[] atomContainer = new IAtomContainer[smiles.length];
        for (int i = 0; i < smiles.length; i++) {
            atomContainer[i] = sp.parseSmiles(smiles[i]);

            inputList.add(atomContainer[i]);
        }
        ///////////////////////////////////////////////////////////////////////////////////////////
        // Generate 2D coordinates for the input molecules with the Structure Diagram Generator

        StructureDiagramGenerator str;
        List<IAtomContainer> resultList = new ArrayList<IAtomContainer>();
        for (Iterator<IAtomContainer> iter = inputList.iterator(); iter.hasNext();) {
            IAtomContainer molecules = iter.next();
            str = new StructureDiagramGenerator();
            str.setMolecule((IAtomContainer) molecules);
            str.generateCoordinates();
            resultList.add(str.getMolecule());
        }
        inputList = resultList;

        /////////////////////////////////////////////////////////////////////////////////////////////
        // Delete x and y coordinates

        for (Iterator<IAtomContainer> iter = inputList.iterator(); iter.hasNext();) {
            IAtomContainer molecules = iter.next();
            for (Iterator<IAtom> atom = molecules.atoms().iterator(); atom.hasNext();) {
                IAtom last = atom.next();
                last.setPoint2d(null);
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////
        // Test for the method Model3DBuildersWithMM2ForceField
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();

        ModelBuilder3D mb3d = ModelBuilder3D.getInstance(DefaultChemObjectBuilder.getInstance());
        for (int i = 0; i < inputList.size(); i++) {
            // shallow copy
            IAtomContainer mol = builder.newInstance(IAtomContainer.class, inputList.get(i));
            try {
                mol = mb3d.generate3DCoordinates(mol, false);
                for (IAtom a : mol.atoms())
                    assertNotNull(smiles[0] + " has unplaced atom", a.getPoint3d());
                checkAverageBondLength(mol);
            } catch (CDKException | CloneNotSupportedException | IOException e) {
                StringWriter stackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTrace));
                Assert.fail("3D coordinated could not be generator for " + smiles[i] + ": " + stackTrace);
            }
        }
    }

    /*
     * @cdk.bug 1241421
     */
    @Test
    @Category(SlowTest.class)
    public void testModelBuilder3D_bug_1241421() throws Exception {
        ModelBuilder3D mb3d = ModelBuilder3D.getInstance(DefaultChemObjectBuilder.getInstance());
        String filename = "data/mdl/bug1241421.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        reader.close();
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        IAtomContainer ac = new AtomContainer(containersList.get(0));
        ac = mb3d.generate3DCoordinates(ac, false);
        checkAverageBondLength(ac);
    }

    @Test
    @Category(SlowTest.class)
    public void testModelBuilder3D_reserpine() throws Exception {
        ModelBuilder3D mb3d = ModelBuilder3D.getInstance(DefaultChemObjectBuilder.getInstance());
        String filename = "data/mdl/reserpine.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        reader.close();
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        IAtomContainer ac = new AtomContainer(containersList.get(0));
        ac = mb3d.generate3DCoordinates(ac, false);
        for (int i = 0; i < ac.getAtomCount(); i++) {
            assertNotNull(ac.getAtom(i).getPoint3d());
        }
        checkAverageBondLength(ac);
    }

    @Test
    public void testAlkanes() throws CDKException, IOException, CloneNotSupportedException {
        String smiles1 = "CCCCCCCCCCCCCCCCCC";
        String smiles2 = "CCCCCC(CCCC)CCCC";
        SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer nonBranchedAlkane = parser.parseSmiles(smiles1);
        IAtomContainer branchedAlkane = parser.parseSmiles(smiles2);
        ModelBuilder3D.getInstance(DefaultChemObjectBuilder.getInstance()).generate3DCoordinates(nonBranchedAlkane,
                false);
        ModelBuilder3D.getInstance(DefaultChemObjectBuilder.getInstance()).generate3DCoordinates(branchedAlkane, false);
    }
    
    @Test
    public void hydrogenAsFirstAtomInMethane() throws Exception {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer methane = smipar.parseSmiles("[H]C([H])([H])[H]");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(methane);
        ModelBuilder3D mb3d = ModelBuilder3D.getInstance(SilentChemObjectBuilder.getInstance());
        mb3d.generate3DCoordinates(methane, false);
        for (IAtom atom : methane.atoms())
            assertNotNull(atom.getPoint3d());
    }

    @Test
    public void hydrogenAsFirstAtomInEthane() throws Exception {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer ethane = smipar.parseSmiles("[H]C([H])([H])C([H])([H])[H]");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(ethane);
        ModelBuilder3D mb3d = ModelBuilder3D.getInstance(SilentChemObjectBuilder.getInstance());
        mb3d.generate3DCoordinates(ethane, false);
        for (IAtom atom : ethane.atoms())
            assertNotNull(atom.getPoint3d());
    }       

}
