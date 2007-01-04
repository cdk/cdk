/* $RCSfile$
 * $Author: miguelrojasch $
 * $Date: 2006-05-11 14:25:07 +0200 (Do, 11 Mai 2006) $
 * $Revision: 6221 $
 *
 *  Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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


import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.type.RearrangementAnion1Reaction;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;

/**
 * TestSuite that runs a test for the RearrangementAnion1ReactionTest.
 * Generalized Reaction: [A-]-B[+] => A=B.
 *
 * @cdk.module test-reaction
 */
public class RearrangementAnion1ReactionTest extends CDKTestCase {
	
	private IReactionProcess type;

	/**
	 * Constructror of the RearrangementAnion1ReactionTest object
	 *
	 */
	public  RearrangementAnion1ReactionTest() {
		type  = new RearrangementAnion1Reaction();
	}
    
	public static Test suite() {
		return new TestSuite(RearrangementAnion1ReactionTest.class);
	}
	/**
	 * A unit test suite for JUnit. Reaction: C-[C+]-[O-] => C-C=O
	 * Automatic sarch of the centre active.
	 *
	 * @return    The test suite
	 */
	public void testAutomaticSearchCentreActiveExample1() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		
		/*C-[C+]-[O-]*/
		IMolecule molecule = getMolecule1();
		setOfReactants.addMolecule(molecule);
		
		/*automatic search of the centre active*/
        Object[] params = {Boolean.FALSE};
        type.setParameters(params);
        
        /* iniciate */
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        IMolecule product = setOfReactions.getReaction(0).getProducts().getMolecule(0);
        
        /*C-C=O*/
        IMolecule molecule2 = getMolecule2();
        
        QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product);
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule2,qAC));
	}
	/**
	 * A unit test suite for JUnit. Reaction: C-[C+]-[O-] => C-C=O
	 * Manually put of the centre active.
	 *
	 * @return    The test suite
	 */
	public void testManuallyPutCentreActiveExample1() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		/*C-[C+]-[O-]*/
		IMolecule molecule = getMolecule1();
		setOfReactants.addMolecule(molecule);
		
		/*manually put the centre active*/
		molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
		
        Object[] params = {Boolean.TRUE};
        type.setParameters(params);
        
        /* iniciate */
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        IMolecule product = setOfReactions.getReaction(0).getProducts().getMolecule(0);
        
        /*C-C=O*/
        IMolecule molecule2 = getMolecule2();
        
        QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product);
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule2,qAC));
        
        		
	}
	/**
	 * A unit test suite for JUnit. Reaction: C-[C+]-[O-] => C-C=O
	 * Test of mapped between the reactant and product. Only is mapped the centre active.
	 *
	 * @return    The test suite
	 */
	public void testMappingExample1() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		/*C-[C+]-[O-]*/
		IMolecule molecule = getMolecule1();
		setOfReactants.addMolecule(molecule);
		
		/*automatic search of the centre active*/
        Object[] params = {Boolean.FALSE};
        type.setParameters(params);
        
        /* iniciate */
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        IMolecule product = setOfReactions.getReaction(0).getProducts().getMolecule(0);


        Assert.assertEquals(3,setOfReactions.getReaction(0).getMappingCount());
        IAtom mappedProductA1 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtom(1));
        assertEquals(mappedProductA1, product.getAtom(1));
        IBond mappedProductB1 = (IBond)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getBond(1));
        assertEquals(mappedProductB1, product.getBond(1));
        IAtom mappedProductA2 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtom(2));
        assertEquals(mappedProductA2, product.getAtom(2));
	}
	/**
	 * get the molecule 1: C-[C+]-[O-]
	 * 
	 * @return The IMolecule
	 */
	private IMolecule getMolecule1()throws ClassNotFoundException, CDKException, java.lang.Exception {
		IMolecule molecule = (new SmilesParser(org.openscience.cdk.DefaultChemObjectBuilder.getInstance())).parseSmiles("C-[C+]-[O-]");
	    HydrogenAdder adder = new HydrogenAdder();
        adder.addImplicitHydrogensToSatisfyValency(molecule);
        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
        lpcheck.newSaturate(molecule);
        return molecule;
	}
	/**
	 * get the molecule 2: C-C=O
	 * 
	 * @return The IMolecule
	 */
	private IMolecule getMolecule2()throws ClassNotFoundException, CDKException, java.lang.Exception {
		IMolecule molecule = (new SmilesParser(org.openscience.cdk.DefaultChemObjectBuilder.getInstance())).parseSmiles("C-C=O");
		HydrogenAdder adder = new HydrogenAdder();
        adder.addImplicitHydrogensToSatisfyValency(molecule);
        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
        lpcheck.newSaturate(molecule);
        return molecule;
	}
}
