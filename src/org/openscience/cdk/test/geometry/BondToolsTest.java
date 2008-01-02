/* $Revision: 7691 $ $Author: egonw $ $Date: 2007-01-11 12:47:48 +0100 (Thu, 11 Jan 2007) $
 * 
 * Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.test.geometry;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.geometry.BondTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.XYZReader;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.test.NewCDKTestCase;

import java.io.InputStream;
import java.util.Iterator;

/**
 * @cdk.module test-standard
 */
public class BondToolsTest extends NewCDKTestCase {

    public BondToolsTest() {
        super();
    }


    @Test
    public void testIsValidDoubleBondConfiguration_IAtomContainer_IBond() throws Exception {
		String filename = "data/mdl/testdoublebondconfig.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins);
		ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
		IMolecule mol=chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
		Assert.assertTrue(BondTools.isValidDoubleBondConfiguration(mol, mol.getBond(0)));
		Assert.assertFalse(BondTools.isValidDoubleBondConfiguration(mol, mol.getBond(1)));
		Assert.assertFalse(BondTools.isValidDoubleBondConfiguration(mol, mol.getBond(2)));
		Assert.assertFalse(BondTools.isValidDoubleBondConfiguration(mol, mol.getBond(3)));
		Assert.assertFalse(BondTools.isValidDoubleBondConfiguration(mol, mol.getBond(4)));
		Assert.assertFalse(BondTools.isValidDoubleBondConfiguration(mol, mol.getBond(5)));
		Assert.assertFalse(BondTools.isValidDoubleBondConfiguration(mol, mol.getBond(6)));
		Assert.assertFalse(BondTools.isValidDoubleBondConfiguration(mol, mol.getBond(7)));
	}

	@Test public void testIsCisTrans_IAtom_IAtom_IAtom_IAtom_IAtomContainer() throws Exception {
		String filename = "data/mdl/testdoublebondconfig.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins);
		ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
		IMolecule mol=chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
		Assert.assertFalse(BondTools.isCisTrans(mol.getAtom(2),mol.getAtom(0),mol.getAtom(1),mol.getAtom(4),mol));
	}

	@Test public void testIsLeft_IAtom_IAtom_IAtom() throws Exception {
		String filename = "data/mdl/testdoublebondconfig.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins);
		ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
		IMolecule mol=chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
		Assert.assertFalse(BondTools.isLeft(mol.getAtom(1),mol.getAtom(0),mol.getAtom(2)));
	}

	@Test public void testGiveAngleBothMethods_IAtom_IAtom_IAtom_boolean() throws Exception {
		String filename = "data/mdl/testdoublebondconfig.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins);
		ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
		IMolecule mol=chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
		Assert.assertEquals(2.0943946986086157,BondTools.giveAngleBothMethods(mol.getAtom(0),mol.getAtom(2),mol.getAtom(3),true),0.2);
		Assert.assertEquals(2.0943946986086157,BondTools.giveAngleBothMethods(mol.getAtom(0),mol.getAtom(2),mol.getAtom(3),false),0.2);
	}


	/**
	 * Make sure the the rebonding is working.
	 */
	@Test public void testCloseEnoughToBond_IAtom_IAtom_double() throws Exception {
		String filename = "data/xyz/viagra.xyz";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		XYZReader reader = new XYZReader(ins);
		AtomTypeFactory atf = AtomTypeFactory.getInstance(
				 "org/openscience/cdk/config/data/jmol_atomtypes.txt",
			     NoNotificationChemObjectBuilder.getInstance()
		);
		ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
		IMolecule mol=chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
		Iterator atoms = mol.atoms();
		while (atoms.hasNext()) {
			atf.configure((IAtom)atoms.next());
		}
		Assert.assertTrue(BondTools.closeEnoughToBond(mol.getAtom(0),mol.getAtom(1),1));
		Assert.assertFalse(BondTools.closeEnoughToBond(mol.getAtom(0),mol.getAtom(8),1));
	}

	@Test public void testGiveAngleBothMethods_Point2d_Point2d_Point2d_boolean() throws Exception {
		String filename = "data/mdl/testdoublebondconfig.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins);
		ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
		IMolecule mol=chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
		Assert.assertEquals(2.0943946986086157,BondTools.giveAngleBothMethods(mol.getAtom(0).getPoint2d(),mol.getAtom(2).getPoint2d(),mol.getAtom(3).getPoint2d(),true),0.2);
		Assert.assertEquals(2.0943946986086157,BondTools.giveAngleBothMethods(mol.getAtom(0).getPoint2d(),mol.getAtom(2).getPoint2d(),mol.getAtom(3).getPoint2d(),false),0.2);
	}

	@Test public void testIsTetrahedral_IAtomContainer_IAtom_boolean() throws Exception {
		String filename = "data/mdl/tetrahedral_1.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins);
		ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
		IMolecule mol=chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
		Assert.assertEquals(BondTools.isTetrahedral(mol,mol.getAtom(0),true),1);
		Assert.assertEquals(BondTools.isTetrahedral(mol,mol.getAtom(1),true),0);
		filename = "data/mdl/tetrahedral_1_lazy.mol";
		ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		reader = new MDLV2000Reader(ins);
		chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
		mol=chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
		Assert.assertEquals(BondTools.isTetrahedral(mol,mol.getAtom(0),true),0);
		Assert.assertEquals(BondTools.isTetrahedral(mol,mol.getAtom(0),false),3);
	}

	@Test public void testIsTrigonalBipyramidalOrOctahedral_IAtomContainer_IAtom() throws Exception {
		String filename = "data/mdl/trigonal_bipyramidal.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins);
		ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
		IMolecule mol=chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
		Assert.assertEquals(BondTools.isTrigonalBipyramidalOrOctahedral(mol,mol.getAtom(0)),1);
		Assert.assertEquals(BondTools.isTrigonalBipyramidalOrOctahedral(mol,mol.getAtom(1)),0);
	}

	@Test public void testIsStereo_IAtomContainer_IAtom() throws Exception {
		String filename = "data/mdl/trigonal_bipyramidal.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins);
		ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
		IMolecule mol=chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
		Assert.assertTrue(BondTools.isStereo(mol,mol.getAtom(0)));
		Assert.assertFalse(BondTools.isStereo(mol,mol.getAtom(1)));
	}

	@Test public void testIsSquarePlanar_IAtomContainer_IAtom() throws Exception {
		String filename = "data/mdl/squareplanar.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins);
		ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
		IMolecule mol=chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
		Assert.assertTrue(BondTools.isSquarePlanar(mol,mol.getAtom(0)));
		Assert.assertFalse(BondTools.isSquarePlanar(mol,mol.getAtom(1)));
	}
	
	@Test public void testStereosAreOpposite_IAtomContainer_IAtom() throws Exception {
		String filename = "data/mdl/squareplanar.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins);
		ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
		IMolecule mol=chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
		Assert.assertFalse(BondTools.stereosAreOpposite(mol,mol.getAtom(0)));
		filename = "data/mdl/tetrahedral_with_four_wedges.mol";
		ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		reader = new MDLV2000Reader(ins);
		chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
		mol=chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
		Assert.assertTrue(BondTools.stereosAreOpposite(mol,mol.getAtom(0)));
	}

	@Test public void testMakeUpDownBonds_IAtomContainer() throws Exception {
		String filename = "data/mdl/tetrahedral_2_lazy.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins);
		ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
		IMolecule mol=chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
		BondTools.makeUpDownBonds(mol);
		Assert.assertEquals(-1,mol.getBond(3).getStereo());
	}

	@Test public void testGiveAngle_IAtom_IAtom_IAtom() throws Exception {
		String filename = "data/mdl/testdoublebondconfig.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins);
		ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
		IMolecule mol=chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
		Assert.assertEquals(2.0943946986086157,BondTools.giveAngle(mol.getAtom(0),mol.getAtom(2),mol.getAtom(3)),0.2);
	}

	@Test public void testGiveAngleFromMiddle_IAtom_IAtom_IAtom() throws Exception {
		String filename = "data/mdl/testdoublebondconfig.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins);
		ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
		IMolecule mol=chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
		Assert.assertEquals(2.0943946986086157,BondTools.giveAngleFromMiddle(mol.getAtom(0),mol.getAtom(2),mol.getAtom(3)),0.2);
	}
}


