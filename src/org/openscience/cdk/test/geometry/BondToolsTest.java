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

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.geometry.BondTools;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLV2000Reader;
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
}


