/* Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.geometry;

import java.io.InputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.XYZReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

/**
 */
class BondToolsTest extends CDKTestCase {

    BondToolsTest() {
        super();
    }

    @Test
    void testIsValidDoubleBondConfiguration_IAtomContainer_IBond() throws Exception {
        String filename = "testdoublebondconfig.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        IAtomContainer mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        Assertions.assertTrue(BondTools.isValidDoubleBondConfiguration(mol, mol.getBond(0)));
        Assertions.assertFalse(BondTools.isValidDoubleBondConfiguration(mol, mol.getBond(1)));
        Assertions.assertFalse(BondTools.isValidDoubleBondConfiguration(mol, mol.getBond(2)));
        Assertions.assertFalse(BondTools.isValidDoubleBondConfiguration(mol, mol.getBond(3)));
        Assertions.assertFalse(BondTools.isValidDoubleBondConfiguration(mol, mol.getBond(4)));
        Assertions.assertFalse(BondTools.isValidDoubleBondConfiguration(mol, mol.getBond(5)));
        Assertions.assertFalse(BondTools.isValidDoubleBondConfiguration(mol, mol.getBond(6)));
        Assertions.assertFalse(BondTools.isValidDoubleBondConfiguration(mol, mol.getBond(7)));
    }

    @Test
    void testIsCisTrans_IAtom_IAtom_IAtom_IAtom_IAtomContainer() throws Exception {
        String filename = "testdoublebondconfig.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        IAtomContainer mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        Assertions.assertFalse(BondTools.isCisTrans(mol.getAtom(2), mol.getAtom(0), mol.getAtom(1), mol.getAtom(4), mol));
    }

    @Test
    void testIsLeft_IAtom_IAtom_IAtom() throws Exception {
        String filename = "testdoublebondconfig.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        IAtomContainer mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        Assertions.assertFalse(BondTools.isLeft(mol.getAtom(1), mol.getAtom(0), mol.getAtom(2)));
    }

    @Test
    void testGiveAngleBothMethods_IAtom_IAtom_IAtom_boolean() throws Exception {
        String filename = "testdoublebondconfig.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        IAtomContainer mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        Assertions.assertEquals(2.0943946986086157, BondTools.giveAngleBothMethods(mol.getAtom(0), mol.getAtom(2), mol.getAtom(3), true), 0.2);
        Assertions.assertEquals(2.0943946986086157, BondTools.giveAngleBothMethods(mol.getAtom(0), mol.getAtom(2), mol.getAtom(3), false), 0.2);
    }

    /**
     * Make sure the the rebonding is working.
     */
    @Test
    void testCloseEnoughToBond_IAtom_IAtom_double() throws Exception {
        String filename = "viagra.xyz";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        XYZReader reader = new XYZReader(ins);
        AtomTypeFactory atf = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/jmol_atomtypes.txt",
                SilentChemObjectBuilder.getInstance());
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        IAtomContainer mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        for (IAtom iAtom : mol.atoms()) {
            atf.configure(iAtom);
        }
        Assertions.assertTrue(BondTools.closeEnoughToBond(mol.getAtom(0), mol.getAtom(1), 1));
        Assertions.assertFalse(BondTools.closeEnoughToBond(mol.getAtom(0), mol.getAtom(8), 1));
    }

    @Test
    void testGiveAngleBothMethods_Point2d_Point2d_Point2d_boolean() throws Exception {
        String filename = "testdoublebondconfig.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        IAtomContainer mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        Assertions.assertEquals(2.0943946986086157, BondTools.giveAngleBothMethods(mol.getAtom(0).getPoint2d(), mol
                .getAtom(2).getPoint2d(), mol.getAtom(3).getPoint2d(), true), 0.2);
        Assertions.assertEquals(2.0943946986086157, BondTools.giveAngleBothMethods(mol.getAtom(0).getPoint2d(), mol
                .getAtom(2).getPoint2d(), mol.getAtom(3).getPoint2d(), false), 0.2);
    }

    @Test
    void testIsTetrahedral_IAtomContainer_IAtom_boolean() throws Exception {
        String filename = "tetrahedral_1.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        IAtomContainer mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        Assertions.assertEquals(BondTools.isTetrahedral(mol, mol.getAtom(0), true), 1);
        Assertions.assertEquals(BondTools.isTetrahedral(mol, mol.getAtom(1), true), 0);
        filename = "tetrahedral_1_lazy.mol";
        ins = this.getClass().getResourceAsStream(filename);
        reader = new MDLV2000Reader(ins);
        chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        Assertions.assertEquals(BondTools.isTetrahedral(mol, mol.getAtom(0), true), 0);
        Assertions.assertEquals(BondTools.isTetrahedral(mol, mol.getAtom(0), false), 3);
    }

    @Test
    void testIsTrigonalBipyramidalOrOctahedral_IAtomContainer_IAtom() throws Exception {
        String filename = "trigonal_bipyramidal.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        IAtomContainer mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        Assertions.assertEquals(BondTools.isTrigonalBipyramidalOrOctahedral(mol, mol.getAtom(0)), 1);
        Assertions.assertEquals(BondTools.isTrigonalBipyramidalOrOctahedral(mol, mol.getAtom(1)), 0);
    }

    @Test
    void testIsStereo_IAtomContainer_IAtom() throws Exception {
        String filename = "trigonal_bipyramidal.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        IAtomContainer mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        Assertions.assertTrue(BondTools.isStereo(mol, mol.getAtom(0)));
        Assertions.assertFalse(BondTools.isStereo(mol, mol.getAtom(1)));
    }

    @Test
    void testIsStereo_IAtomContainer_IAtom_forinvalid() throws Exception {
        String filename = "trigonal_bipyramidal.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        IAtomContainer mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        for (int i = 1; i < 6; i++) {
            mol.getAtom(i).setSymbol("C");
        }
        Assertions.assertFalse(BondTools.isStereo(mol, mol.getAtom(0)));
        Assertions.assertFalse(BondTools.isStereo(mol, mol.getAtom(1)));
    }

    @Test
    void testIsSquarePlanar_IAtomContainer_IAtom() throws Exception {
        String filename = "squareplanar.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        IAtomContainer mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        Assertions.assertTrue(BondTools.isSquarePlanar(mol, mol.getAtom(0)));
        Assertions.assertFalse(BondTools.isSquarePlanar(mol, mol.getAtom(1)));
    }

    @Test
    void testStereosAreOpposite_IAtomContainer_IAtom() throws Exception {
        String filename = "squareplanar.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        IAtomContainer mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        Assertions.assertFalse(BondTools.stereosAreOpposite(mol, mol.getAtom(0)));
        filename = "tetrahedral_with_four_wedges.mol";
        ins = this.getClass().getResourceAsStream(filename);
        reader = new MDLV2000Reader(ins);
        chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        Assertions.assertTrue(BondTools.stereosAreOpposite(mol, mol.getAtom(0)));
    }

    @Test
    void testMakeUpDownBonds_IAtomContainer() throws Exception {
        String filename = "tetrahedral_2_lazy.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        IAtomContainer mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        BondTools.makeUpDownBonds(mol);
        Assertions.assertEquals(IBond.Display.Down, mol.getBond(3).getDisplay());
    }

    @Test
    void testGiveAngle_IAtom_IAtom_IAtom() throws Exception {
        String filename = "testdoublebondconfig.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        IAtomContainer mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        Assertions.assertEquals(2.0943946986086157, BondTools.giveAngle(mol.getAtom(0), mol.getAtom(2), mol.getAtom(3)), 0.2);
    }

    @Test
    void testGiveAngleFromMiddle_IAtom_IAtom_IAtom() throws Exception {
        String filename = "testdoublebondconfig.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        IAtomContainer mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        Assertions.assertEquals(2.0943946986086157, BondTools.giveAngleFromMiddle(mol.getAtom(0), mol.getAtom(2), mol.getAtom(3)), 0.2);
    }

    /**
     * @cdk.bug 2831420
     */
    @Test
    void testBug2831420() throws Exception {
        String filename = "bug2831420.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        IAtomContainer mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        Assertions.assertTrue(BondTools.isStereo(mol, mol.getAtom(5)));
    }
}
