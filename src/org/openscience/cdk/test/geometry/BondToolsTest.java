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

import java.io.InputStream;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.geometry.BondTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.XYZReader;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test-standard
 */
public class BondToolsTest extends CDKTestCase {

    public BondToolsTest(String name) {
        super(name);
    }
    
	public static Test suite() {
		return new TestSuite(BondToolsTest.class);
	}
	
	
	public void testIsValidDoubleBondConfiguration_IAtomContainer_IBond(){
		try{
			String filename = "data/mdl/testdoublebondconfig.mol";
		    InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		    MDLV2000Reader reader = new MDLV2000Reader(ins);
	        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
	        IMolecule mol=chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
	        assertTrue(BondTools.isValidDoubleBondConfiguration(mol, mol.getBond(0)));
	        assertFalse(BondTools.isValidDoubleBondConfiguration(mol, mol.getBond(1)));
	        assertFalse(BondTools.isValidDoubleBondConfiguration(mol, mol.getBond(2)));
	        assertFalse(BondTools.isValidDoubleBondConfiguration(mol, mol.getBond(3)));
	        assertFalse(BondTools.isValidDoubleBondConfiguration(mol, mol.getBond(4)));
	        assertFalse(BondTools.isValidDoubleBondConfiguration(mol, mol.getBond(5)));
	        assertFalse(BondTools.isValidDoubleBondConfiguration(mol, mol.getBond(6)));
	        assertFalse(BondTools.isValidDoubleBondConfiguration(mol, mol.getBond(7)));
		} catch (Exception exc) {
			exc.printStackTrace();
			fail(exc.getMessage());
		}		
	}

	public void testIsCisTrans_IAtom_IAtom_IAtom_IAtom_IAtomContainer(){
		try{
			String filename = "data/mdl/testdoublebondconfig.mol";
		    InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		    MDLV2000Reader reader = new MDLV2000Reader(ins);
	        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
	        IMolecule mol=chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
	        assertFalse(BondTools.isCisTrans(mol.getAtom(2),mol.getAtom(0),mol.getAtom(1),mol.getAtom(4),mol));
		} catch (Exception exc) {
			exc.printStackTrace();
			fail(exc.getMessage());
		}		
	}

	public void testIsLeft_IAtom_IAtom_IAtom(){
		try{
			String filename = "data/mdl/testdoublebondconfig.mol";
		    InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		    MDLV2000Reader reader = new MDLV2000Reader(ins);
	        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
	        IMolecule mol=chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
	        assertFalse(BondTools.isLeft(mol.getAtom(1),mol.getAtom(0),mol.getAtom(2)));
		} catch (Exception exc) {
			exc.printStackTrace();
			fail(exc.getMessage());
		}		
	}

	public void testGiveAngleBothMethods_IAtom_IAtom_IAtom_boolean(){
		try{
			String filename = "data/mdl/testdoublebondconfig.mol";
		    InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		    MDLV2000Reader reader = new MDLV2000Reader(ins);
	        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
	        IMolecule mol=chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
	        assertEquals(2.0943946986086157,BondTools.giveAngleBothMethods(mol.getAtom(0),mol.getAtom(2),mol.getAtom(3),true),0.2);
	        assertEquals(2.0943946986086157,BondTools.giveAngleBothMethods(mol.getAtom(0),mol.getAtom(2),mol.getAtom(3),false),0.2);
		} catch (Exception exc) {
			exc.printStackTrace();
			fail(exc.getMessage());
		}		
	}


	/**
	 * Make sure the the rebonding is working.
	 */
	public void testCloseEnoughToBond_IAtom_IAtom_double() throws Exception {
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
		assertTrue(BondTools.closeEnoughToBond(mol.getAtom(0),mol.getAtom(1),1));
		assertFalse(BondTools.closeEnoughToBond(mol.getAtom(0),mol.getAtom(8),1));
	}

	public void testGiveAngleBothMethods_Point2d_Point2d_Point2d_boolean(){
		try{
			String filename = "data/mdl/testdoublebondconfig.mol";
		    InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		    MDLV2000Reader reader = new MDLV2000Reader(ins);
	        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
	        IMolecule mol=chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
	        assertEquals(2.0943946986086157,BondTools.giveAngleBothMethods(mol.getAtom(0).getPoint2d(),mol.getAtom(2).getPoint2d(),mol.getAtom(3).getPoint2d(),true),0.2);
	        assertEquals(2.0943946986086157,BondTools.giveAngleBothMethods(mol.getAtom(0).getPoint2d(),mol.getAtom(2).getPoint2d(),mol.getAtom(3).getPoint2d(),false),0.2);
		} catch (Exception exc) {
			exc.printStackTrace();
			fail(exc.getMessage());
		}		
	}

	public void testIsTetrahedral_IAtomContainer_IAtom_boolean(){
		try{
			String filename = "data/mdl/tetrahedral_1.mol";
		    InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		    MDLV2000Reader reader = new MDLV2000Reader(ins);
	        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
	        IMolecule mol=chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
	        assertEquals(BondTools.isTetrahedral(mol,mol.getAtom(0),true),1);
	        assertEquals(BondTools.isTetrahedral(mol,mol.getAtom(1),true),0);
			filename = "data/mdl/tetrahedral_1_lazy.mol";
		    ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		    reader = new MDLV2000Reader(ins);
	        chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
	        mol=chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
	        assertEquals(BondTools.isTetrahedral(mol,mol.getAtom(0),true),0);
	        assertEquals(BondTools.isTetrahedral(mol,mol.getAtom(0),false),3);
		} catch (Exception exc) {
			exc.printStackTrace();
			fail(exc.getMessage());
		}		
	}

	public void testIsTrigonalBipyramidalOrOctahedral_IAtomContainer_IAtom(){
		try{
			String filename = "data/mdl/trigonal_bipyramidal.mol";
		    InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		    MDLV2000Reader reader = new MDLV2000Reader(ins);
	        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
	        IMolecule mol=chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
	        assertEquals(BondTools.isTrigonalBipyramidalOrOctahedral(mol,mol.getAtom(0)),1);
	        assertEquals(BondTools.isTrigonalBipyramidalOrOctahedral(mol,mol.getAtom(1)),0);
		} catch (Exception exc) {
			exc.printStackTrace();
			fail(exc.getMessage());
		}		
	}

	public void testIsStereo_IAtomContainer_IAtom(){
		try{
			String filename = "data/mdl/trigonal_bipyramidal.mol";
		    InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		    MDLV2000Reader reader = new MDLV2000Reader(ins);
	        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
	        IMolecule mol=chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
	        assertTrue(BondTools.isStereo(mol,mol.getAtom(0)));
	        assertFalse(BondTools.isStereo(mol,mol.getAtom(1)));
		} catch (Exception exc) {
			exc.printStackTrace();
			fail(exc.getMessage());
		}		
	}

	public void testIsSquarePlanar_IAtomContainer_IAtom(){
		try{
			String filename = "data/mdl/squareplanar.mol";
		    InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		    MDLV2000Reader reader = new MDLV2000Reader(ins);
	        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
	        IMolecule mol=chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
	        assertTrue(BondTools.isSquarePlanar(mol,mol.getAtom(0)));
	        assertFalse(BondTools.isSquarePlanar(mol,mol.getAtom(1)));
		} catch (Exception exc) {
			exc.printStackTrace();
			fail(exc.getMessage());
		}		
	}
	
	public void testStereosAreOpposite_IAtomContainer_IAtom(){
		try{
			String filename = "data/mdl/squareplanar.mol";
		    InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		    MDLV2000Reader reader = new MDLV2000Reader(ins);
	        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
	        IMolecule mol=chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
	        assertFalse(BondTools.stereosAreOpposite(mol,mol.getAtom(0)));
			filename = "data/mdl/tetrahedral_with_four_wedges.mol";
		    ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		    reader = new MDLV2000Reader(ins);
	        chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
	        mol=chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
	        assertTrue(BondTools.stereosAreOpposite(mol,mol.getAtom(0)));
		} catch (Exception exc) {
			exc.printStackTrace();
			fail(exc.getMessage());
		}		
	}

	public void testMakeUpDownBonds_IAtomContainer(){
		try{
			String filename = "data/mdl/tetrahedral_2_lazy.mol";
		    InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		    MDLV2000Reader reader = new MDLV2000Reader(ins);
	        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
	        IMolecule mol=chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
	        BondTools.makeUpDownBonds(mol);
	        assertEquals(-1,mol.getBond(3).getStereo());
		} catch (Exception exc) {
			exc.printStackTrace();
			fail(exc.getMessage());
		}		
	}

	public void testGiveAngle_IAtom_IAtom_IAtom(){
		try{
			String filename = "data/mdl/testdoublebondconfig.mol";
		    InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		    MDLV2000Reader reader = new MDLV2000Reader(ins);
	        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
	        IMolecule mol=chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
	        assertEquals(2.0943946986086157,BondTools.giveAngle(mol.getAtom(0),mol.getAtom(2),mol.getAtom(3)),0.2);
		} catch (Exception exc) {
			exc.printStackTrace();
			fail(exc.getMessage());
		}		
	}

	public void testGiveAngleFromMiddle_IAtom_IAtom_IAtom(){
		try{
			String filename = "data/mdl/testdoublebondconfig.mol";
		    InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		    MDLV2000Reader reader = new MDLV2000Reader(ins);
	        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
	        IMolecule mol=chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
	        assertEquals(2.0943946986086157,BondTools.giveAngleFromMiddle(mol.getAtom(0),mol.getAtom(2),mol.getAtom(3)),0.2);
		} catch (Exception exc) {
			exc.printStackTrace();
			fail(exc.getMessage());
		}		
	}
}


