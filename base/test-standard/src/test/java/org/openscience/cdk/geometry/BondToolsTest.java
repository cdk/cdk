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
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
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
 * @cdk.module test-standard
 */
public class BondToolsTest extends CDKTestCase {

    public BondToolsTest() {
        super();
    }

    @Test
    public void testIsValidDoubleBondConfiguration_IAtomContainer_IBond() throws Exception {
        String filename = "data/mdl/testdoublebondconfig.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        IAtomContainer mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        Assert.assertTrue(BondTools.isValidDoubleBondConfiguration(mol, mol.getBond(0)));
        Assert.assertFalse(BondTools.isValidDoubleBondConfiguration(mol, mol.getBond(1)));
        Assert.assertFalse(BondTools.isValidDoubleBondConfiguration(mol, mol.getBond(2)));
        Assert.assertFalse(BondTools.isValidDoubleBondConfiguration(mol, mol.getBond(3)));
        Assert.assertFalse(BondTools.isValidDoubleBondConfiguration(mol, mol.getBond(4)));
        Assert.assertFalse(BondTools.isValidDoubleBondConfiguration(mol, mol.getBond(5)));
        Assert.assertFalse(BondTools.isValidDoubleBondConfiguration(mol, mol.getBond(6)));
        Assert.assertFalse(BondTools.isValidDoubleBondConfiguration(mol, mol.getBond(7)));
    }

    @Test
    public void testIsCisTrans_IAtom_IAtom_IAtom_IAtom_IAtomContainer() throws Exception {
        String filename = "data/mdl/testdoublebondconfig.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        IAtomContainer mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        Assert.assertFalse(BondTools.isCisTrans(mol.getAtom(2), mol.getAtom(0), mol.getAtom(1), mol.getAtom(4), mol));
    }

    @Test
    public void testIsLeft_IAtom_IAtom_IAtom() throws Exception {
        String filename = "data/mdl/testdoublebondconfig.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        IAtomContainer mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        Assert.assertFalse(BondTools.isLeft(mol.getAtom(1), mol.getAtom(0), mol.getAtom(2)));
    }

    @Test
    public void testGiveAngleBothMethods_IAtom_IAtom_IAtom_boolean() throws Exception {
        String filename = "data/mdl/testdoublebondconfig.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        IAtomContainer mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        Assert.assertEquals(2.0943946986086157,
                BondTools.giveAngleBothMethods(mol.getAtom(0), mol.getAtom(2), mol.getAtom(3), true), 0.2);
        Assert.assertEquals(2.0943946986086157,
                BondTools.giveAngleBothMethods(mol.getAtom(0), mol.getAtom(2), mol.getAtom(3), false), 0.2);
    }

    /**
     * Make sure the the rebonding is working.
     */
    @Test
    public void testCloseEnoughToBond_IAtom_IAtom_double() throws Exception {
        String filename = "data/xyz/viagra.xyz";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        XYZReader reader = new XYZReader(ins);
        AtomTypeFactory atf = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/jmol_atomtypes.txt",
                SilentChemObjectBuilder.getInstance());
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        IAtomContainer mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        Iterator<IAtom> atoms = mol.atoms().iterator();
        while (atoms.hasNext()) {
            atf.configure(atoms.next());
        }
        Assert.assertTrue(BondTools.closeEnoughToBond(mol.getAtom(0), mol.getAtom(1), 1));
        Assert.assertFalse(BondTools.closeEnoughToBond(mol.getAtom(0), mol.getAtom(8), 1));
    }

    @Test
    public void testGiveAngleBothMethods_Point2d_Point2d_Point2d_boolean() throws Exception {
        String filename = "data/mdl/testdoublebondconfig.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        IAtomContainer mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        Assert.assertEquals(2.0943946986086157, BondTools.giveAngleBothMethods(mol.getAtom(0).getPoint2d(), mol
                .getAtom(2).getPoint2d(), mol.getAtom(3).getPoint2d(), true), 0.2);
        Assert.assertEquals(2.0943946986086157, BondTools.giveAngleBothMethods(mol.getAtom(0).getPoint2d(), mol
                .getAtom(2).getPoint2d(), mol.getAtom(3).getPoint2d(), false), 0.2);
    }

    @Test
    public void testIsTetrahedral_IAtomContainer_IAtom_boolean() throws Exception {
        String filename = "data/mdl/tetrahedral_1.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        IAtomContainer mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        Assert.assertEquals(BondTools.isTetrahedral(mol, mol.getAtom(0), true), 1);
        Assert.assertEquals(BondTools.isTetrahedral(mol, mol.getAtom(1), true), 0);
        filename = "data/mdl/tetrahedral_1_lazy.mol";
        ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        reader = new MDLV2000Reader(ins);
        chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        Assert.assertEquals(BondTools.isTetrahedral(mol, mol.getAtom(0), true), 0);
        Assert.assertEquals(BondTools.isTetrahedral(mol, mol.getAtom(0), false), 3);
    }

    @Test
    public void testIsTrigonalBipyramidalOrOctahedral_IAtomContainer_IAtom() throws Exception {
        String filename = "data/mdl/trigonal_bipyramidal.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        IAtomContainer mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        Assert.assertEquals(BondTools.isTrigonalBipyramidalOrOctahedral(mol, mol.getAtom(0)), 1);
        Assert.assertEquals(BondTools.isTrigonalBipyramidalOrOctahedral(mol, mol.getAtom(1)), 0);
    }

    @Test
    public void testIsStereo_IAtomContainer_IAtom() throws Exception {
        String filename = "data/mdl/trigonal_bipyramidal.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        IAtomContainer mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        Assert.assertTrue(BondTools.isStereo(mol, mol.getAtom(0)));
        Assert.assertFalse(BondTools.isStereo(mol, mol.getAtom(1)));
    }

    @Test
    public void testIsStereo_IAtomContainer_IAtom_forinvalid() throws Exception {
        String filename = "data/mdl/trigonal_bipyramidal.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        IAtomContainer mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        for (int i = 1; i < 6; i++) {
            mol.getAtom(i).setSymbol("C");
        }
        Assert.assertFalse(BondTools.isStereo(mol, mol.getAtom(0)));
        Assert.assertFalse(BondTools.isStereo(mol, mol.getAtom(1)));
    }

    @Test
    public void testIsSquarePlanar_IAtomContainer_IAtom() throws Exception {
        String filename = "data/mdl/squareplanar.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        IAtomContainer mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        Assert.assertTrue(BondTools.isSquarePlanar(mol, mol.getAtom(0)));
        Assert.assertFalse(BondTools.isSquarePlanar(mol, mol.getAtom(1)));
    }

    @Test
    public void testStereosAreOpposite_IAtomContainer_IAtom() throws Exception {
        String filename = "data/mdl/squareplanar.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        IAtomContainer mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        Assert.assertFalse(BondTools.stereosAreOpposite(mol, mol.getAtom(0)));
        filename = "data/mdl/tetrahedral_with_four_wedges.mol";
        ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        reader = new MDLV2000Reader(ins);
        chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        Assert.assertTrue(BondTools.stereosAreOpposite(mol, mol.getAtom(0)));
    }

    @Test
    public void testMakeUpDownBonds_IAtomContainer() throws Exception {
        String filename = "data/mdl/tetrahedral_2_lazy.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        IAtomContainer mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        BondTools.makeUpDownBonds(mol);
        Assert.assertEquals(IBond.Stereo.DOWN, mol.getBond(3).getStereo());
    }

    @Test
    public void testGiveAngle_IAtom_IAtom_IAtom() throws Exception {
        String filename = "data/mdl/testdoublebondconfig.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        IAtomContainer mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        Assert.assertEquals(2.0943946986086157, BondTools.giveAngle(mol.getAtom(0), mol.getAtom(2), mol.getAtom(3)),
                0.2);
    }

    @Test
    public void testGiveAngleFromMiddle_IAtom_IAtom_IAtom() throws Exception {
        String filename = "data/mdl/testdoublebondconfig.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        IAtomContainer mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        Assert.assertEquals(2.0943946986086157,
                BondTools.giveAngleFromMiddle(mol.getAtom(0), mol.getAtom(2), mol.getAtom(3)), 0.2);
    }

    /**
     * @cdk.bug 2831420
     */
    @Test
    public void testBug2831420() throws Exception {
        String filename = "data/mdl/bug2831420.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        IAtomContainer mol = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        Assert.assertTrue(BondTools.isStereo(mol, mol.getAtom(5)));
    }
}
