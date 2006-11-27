/* $RCSfile$
 * $Author: miguelrojasch $
 * $Date: 2006-05-11 14:25:07 +0200 (Do, 11 Mai 2006) $
 * $Revision: 6221 $
 *
 *  Copyright (C) 2004-2006  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
package org.openscience.cdk.test.reaction.type;


import java.util.Iterator;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.type.CleavageBondMultiReaction;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.ReactionSetManipulator;

/**
 * TestSuite that runs a test for the CleavageBondMultiReactionTest.
 * 
 * @cdk.module test-reaction
 */
public class CleavageBondMultiReactionTest extends CDKTestCase {
	
	private IReactionProcess type;

	/**
	 * Constructror of the CleavageBondMultiReactionTest object
	 *
	 */
	public  CleavageBondMultiReactionTest() {
		type  = new CleavageBondMultiReaction();
	}
    
	public static Test suite() {
		return new TestSuite(CleavageBondMultiReactionTest.class);
	}

	/**
//	 * A unit test suite for JUnit. Reaction: 
//	 * CCc1ccc(C=O)cc1  =>  C+ Cc1ccc(C=O)cc1
//	 * CCc1ccc(C=O)cc1  =>  CC + c1ccc(C=O)cc1 
//	 * Automatic sarch of the centre active.
//	 *
//	 * @return    The test suite
//	 */
//	public void test1() throws ClassNotFoundException, CDKException, java.lang.Exception {
//		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
//		
//		Molecule molecule = (new SmilesParser()).parseSmiles("CCc1ccc(C=O)cc1");
//        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
//		lpcheck.newSaturate(molecule);
//		
//		molecule.getBond(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
//		molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
//		molecule.getBond(5).setFlag(CDKConstants.REACTIVE_CENTER,true);
//		setOfReactants.addMolecule(molecule);
//		
//		/*has active center*/
//        Object[] params = {Boolean.TRUE};
//        type.setParameters(params);
//        
//        /* iniciate */
//        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
//        
//        Assert.assertEquals(3, setOfReactions.getReactionCount());
//        IAtomContainerSet acS = ReactionSetManipulator.getAllMolecules(setOfReactions);
//        Assert.assertEquals(9,acS.getAtomContainerCount());
//        
//        
//	}
//	/**
//	 * A unit test suite for JUnit. Reaction: 
//	 * O=Cc1ccccc1  =>  O=C + c1ccccc1 
//	 * Automatic sarch of the centre active.
//	 *
//	 * @return    The test suite
//	 */
//	public void test2() throws ClassNotFoundException, CDKException, java.lang.Exception {
//		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
//		
//		Molecule molecule = (new SmilesParser()).parseSmiles("O=Cc1ccccc1");
//		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
//		lpcheck.newSaturate(molecule);
//		
//		molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
//		setOfReactants.addMolecule(molecule);
//		
//		/*has active center*/
//        Object[] params = {Boolean.TRUE};
//        type.setParameters(params);
//        
//        /* iniciate */
//        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
//        
//        Assert.assertEquals(1, setOfReactions.getReactionCount());
//        Assert.assertEquals(2, setOfReactions.getReaction(0).getProductCount());
//
//        IMolecule product = setOfReactions.getReaction(0).getProducts().getMolecule(0);
//		
//        /*C=O*/
//		Molecule molecule2 = (new SmilesParser()).parseSmiles("C=O");
//        QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product);
//		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule2,qAC));
//		
//		product = setOfReactions.getReaction(0).getProducts().getMolecule(1);
//		/*c1ccccc1*/
//		Molecule molecule3 = (new SmilesParser()).parseSmiles("c1ccccc1");
//		
//        qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product);
//		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule3,qAC));
//		
//	}
	/**
	 * A unit test suite for JUnit. Reaction: 
	 *
	 * @return    The test suite
	 */
	public void test3() throws ClassNotFoundException, Exception, java.lang.Exception {
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		
		Molecule molecule = (new SmilesParser()).parseSmiles("CCc1cccc(CC)c1N(COC)C(=O)CCl");
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(molecule);
		
		molecule.getBond(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(6).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(7).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(10).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(11).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(12).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(13).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(14).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(16).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(17).setFlag(CDKConstants.REACTIVE_CENTER,true);
		setOfReactants.addMolecule(molecule);
		
		/*has active center*/
        Object[] params = {Boolean.TRUE};
        type.setParameters(params);
        
        /* iniciate */
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        Assert.assertEquals(28, setOfReactions.getReactionCount());
        IAtomContainerSet acS = ReactionSetManipulator.getAllMolecules(setOfReactions);
        Assert.assertEquals(62,acS.getAtomContainerCount());
        
		
	}
}
