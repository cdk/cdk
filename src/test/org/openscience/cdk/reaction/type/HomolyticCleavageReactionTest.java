/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
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
package org.openscience.cdk.reaction.type;


import java.util.HashMap;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.SingleElectron;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.type.HomolyticCleavageReaction;
import org.openscience.cdk.reaction.ReactionProcessTest;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;

/**
 * TestSuite that runs a test for the HomolyticCleavageReactionTest.
 * Generalized Reaction: A=B => [A*]-[B*]
 *
 * @cdk.module test-reaction
 */
public class HomolyticCleavageReactionTest extends ReactionProcessTest {

	private final static IChemObjectBuilder builder = NoNotificationChemObjectBuilder.getInstance();
	private final static LonePairElectronChecker lpcheck = new LonePairElectronChecker();

	/**
	 *  The JUnit setup method
	 */
	 @BeforeClass public static void setUp() throws Exception {
	 	setReaction(HomolyticCleavageReaction.class);
	 }
	 
	 /**
	  *  The JUnit setup method
	  */
	 @Test public void testHomolyticCleavageReaction() throws Exception {
			IReactionProcess type = new HomolyticCleavageReaction();
			Assert.assertNotNull(type);
	 }
	 /**
		 * A unit test suite for JUnit. Reaction: propane.
		 * CC!-!C => C[C*] + [C*]
		 *           
		 * @cdk.inchi InChI=1/C3H8/c1-3-2/h3H2,1-2H3
		 * 
		 * @return    The test suite
		 */
		@Test public void testInitiate_IMoleculeSet_IMoleculeSet() throws Exception {
			//Smiles("CCC")
			IMolecule molecule = builder.newMolecule();
			molecule.addAtom(builder.newAtom("C"));
			molecule.addAtom(builder.newAtom("C"));
			molecule.addAtom(builder.newAtom("C"));
			molecule.addBond(0, 1, IBond.Order.SINGLE);
			molecule.addBond(1, 2, IBond.Order.SINGLE);
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addBond(0, 3, IBond.Order.SINGLE);
			molecule.addBond(0, 4, IBond.Order.SINGLE);
			molecule.addBond(0, 5, IBond.Order.SINGLE);
			molecule.addBond(1, 6, IBond.Order.SINGLE);
			molecule.addBond(1, 7, IBond.Order.SINGLE);
			molecule.addBond(2, 8, IBond.Order.SINGLE);
			molecule.addBond(2, 9, IBond.Order.SINGLE);
			molecule.addBond(2, 10, IBond.Order.SINGLE);
			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
			
			molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER,true);

	        IMoleculeSet setOfReactants = builder.newMoleculeSet();
	        setOfReactants.addMolecule(molecule);
			
			IReactionProcess type  = new HomolyticCleavageReaction(); 
			HashMap<String,Object> params = new HashMap<String,Object>();
			params.put("hasActiveCenter",Boolean.TRUE);;
	        type.setParameters(params);
	        
	        /* initiate */
			IReactionSet setOfReactions = type.initiate(setOfReactants, null);

	        Assert.assertEquals(1, setOfReactions.getReactionCount());
	        
	        // expected products 
	        
	        //Smiles("C[C*]")
			IMolecule expected1 = builder.newMolecule();
			expected1.addAtom(builder.newAtom("C"));
			expected1.addAtom(builder.newAtom("C"));
			expected1.addSingleElectron(builder.newSingleElectron(expected1.getAtom(1)));
			expected1.addBond(0, 1, IBond.Order.SINGLE);
			expected1.addAtom(builder.newAtom("H"));
			expected1.addAtom(builder.newAtom("H"));
			expected1.addAtom(builder.newAtom("H"));
			expected1.addAtom(builder.newAtom("H"));
			expected1.addAtom(builder.newAtom("H"));
			expected1.addBond(0, 2, IBond.Order.SINGLE);
			expected1.addBond(0, 3, IBond.Order.SINGLE);
			expected1.addBond(0, 4, IBond.Order.SINGLE);
			expected1.addBond(1, 5, IBond.Order.SINGLE);
			expected1.addBond(1, 6, IBond.Order.SINGLE);;
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
	        IMolecule product1 = setOfReactions.getReaction(0).getProducts().getMolecule(0);
	        IQueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
	        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product1,queryAtom));
			
			//Smiles("[C*]")
			IMolecule expected2 = builder.newMolecule();
			expected2.addAtom(builder.newAtom("C"));
			expected2.addSingleElectron(builder.newSingleElectron(expected2.getAtom(0)));
			expected2.addAtom(builder.newAtom("H"));
			expected2.addAtom(builder.newAtom("H"));
			expected2.addAtom(builder.newAtom("H"));
			expected2.addBond(0, 1, IBond.Order.SINGLE);
			expected2.addBond(0, 2, IBond.Order.SINGLE);
			expected2.addBond(0, 3, IBond.Order.SINGLE);
			
			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected2);
			lpcheck.saturate(expected2);
			IMolecule product2 = setOfReactions.getReaction(0).getProducts().getMolecule(1);
	        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected2);
			Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product2,queryAtom));
		}
		/**
		 * A unit test suite for JUnit. Reaction: Propene.
		 * C=C!-!C => C=[C*] + [C*]
		 *
		 * @cdk.inchi  InChI=1/C3H6/c1-3-2/h3H,1H2,2H3
		 * 
		 * @return    The test suite
		 */
		@Test public void testCsp2SingleB() throws Exception {
			//Smiles("C=CC")
			IMolecule molecule = builder.newMolecule();
			molecule.addAtom(builder.newAtom("C"));
			molecule.addAtom(builder.newAtom("C"));
			molecule.addAtom(builder.newAtom("C"));
			molecule.addBond(0, 1, IBond.Order.DOUBLE);
			molecule.addBond(1, 2, IBond.Order.SINGLE);
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addBond(0, 3, IBond.Order.SINGLE);
			molecule.addBond(0, 4, IBond.Order.SINGLE);
			molecule.addBond(1, 5, IBond.Order.SINGLE);
			molecule.addBond(2, 6, IBond.Order.SINGLE);
			molecule.addBond(2, 7, IBond.Order.SINGLE);
			molecule.addBond(2, 8, IBond.Order.SINGLE);
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
			
			molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER,true);

	        IMoleculeSet setOfReactants = builder.newMoleculeSet();
	        setOfReactants.addMolecule(molecule);
			
			IReactionProcess type  = new HomolyticCleavageReaction(); 
			HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
	        type.setParameters(params);
	        
	        /* initiate */
			IReactionSet setOfReactions = type.initiate(setOfReactants, null);

	        Assert.assertEquals(1, setOfReactions.getReactionCount());
	        
	        // expected products 
	        
	        //Smiles("C=[C*]")
			IMolecule expected1 = builder.newMolecule();
			expected1.addAtom(builder.newAtom("C"));
			expected1.addAtom(builder.newAtom("C"));
			expected1.addSingleElectron(builder.newSingleElectron(expected1.getAtom(1)));
			expected1.addBond(0, 1, IBond.Order.DOUBLE);
			expected1.addAtom(builder.newAtom("H"));
			expected1.addAtom(builder.newAtom("H"));
			expected1.addAtom(builder.newAtom("H"));
			expected1.addBond(0, 2, IBond.Order.SINGLE);
			expected1.addBond(0, 3, IBond.Order.SINGLE);
			expected1.addBond(1, 4, IBond.Order.SINGLE);
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
	        IMolecule product1 = setOfReactions.getReaction(0).getProducts().getMolecule(0);
	        IQueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
	        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product1,queryAtom));
			
			//Smiles("[C*]")
			IMolecule expected2 = builder.newMolecule();
			expected2.addAtom(builder.newAtom("C"));
			expected2.addSingleElectron(builder.newSingleElectron(expected2.getAtom(0)));
			expected2.addAtom(builder.newAtom("H"));
			expected2.addAtom(builder.newAtom("H"));
			expected2.addAtom(builder.newAtom("H"));
			expected2.addBond(0, 1, IBond.Order.SINGLE);
			expected2.addBond(0, 2, IBond.Order.SINGLE);
			expected2.addBond(0, 3, IBond.Order.SINGLE);
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected2);
			lpcheck.saturate(expected2);

	        IMolecule product2 = setOfReactions.getReaction(0).getProducts().getMolecule(1);
	        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected2);
	        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product2,queryAtom));
	        
		}
		/**
		 * A unit test suite for JUnit. Reaction: Propyne.
		 * C#C!-!C => C#[C*] + [C*]
		 *
		 * @cdk.inchi InChI=1/C3H4/c1-3-2/h1H,2H3
		 * 
		 * @return    The test suite
		 */
		@Test public void testCspSingleB() throws Exception {
			//Smiles("C#CC")
			IMolecule molecule = builder.newMolecule();
			molecule.addAtom(builder.newAtom("C"));
			molecule.addAtom(builder.newAtom("C"));
			molecule.addAtom(builder.newAtom("C"));
			molecule.addBond(0, 1, IBond.Order.TRIPLE);
			molecule.addBond(1, 2, IBond.Order.SINGLE);
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addBond(0, 3, IBond.Order.SINGLE);
			molecule.addBond(2, 4, IBond.Order.SINGLE);
			molecule.addBond(2, 5, IBond.Order.SINGLE);
			molecule.addBond(2, 6, IBond.Order.SINGLE);
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
	        
			molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER,true);

	        IMoleculeSet setOfReactants = builder.newMoleculeSet();
	        setOfReactants.addMolecule(molecule);
			
			IReactionProcess type  = new HomolyticCleavageReaction(); 
			HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
	        type.setParameters(params);
	        
	        /* initiate */
			IReactionSet setOfReactions = type.initiate(setOfReactants, null);

	        Assert.assertEquals(1, setOfReactions.getReactionCount());
	       
	        // expected products 
	        
	        //Smiles("C#[C*]")
			IMolecule expected1 = builder.newMolecule();
			expected1.addAtom(builder.newAtom("C"));
			expected1.addAtom(builder.newAtom("C"));
			expected1.addSingleElectron(builder.newSingleElectron(expected1.getAtom(1)));
			expected1.addBond(0, 1, IBond.Order.TRIPLE);
			expected1.addAtom(builder.newAtom("H"));
			expected1.addBond(0, 2, IBond.Order.SINGLE);
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
	        IMolecule product1 = setOfReactions.getReaction(0).getProducts().getMolecule(0);
	        QueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
	        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product1,queryAtom));
			
			//Smiles("[C*]")
			IMolecule expected2 = builder.newMolecule();
			expected2.addAtom(builder.newAtom("C"));
			expected2.addSingleElectron(builder.newSingleElectron(expected2.getAtom(0)));
			expected2.addAtom(builder.newAtom("H"));
			expected2.addAtom(builder.newAtom("H"));
			expected2.addAtom(builder.newAtom("H"));
			expected2.addBond(0, 1, IBond.Order.SINGLE);
			expected2.addBond(0, 2, IBond.Order.SINGLE);
			expected2.addBond(0, 3, IBond.Order.SINGLE);
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected2);
			lpcheck.saturate(expected2);
	        IMolecule product2 = setOfReactions.getReaction(0).getProducts().getMolecule(1);
	        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected2);
	        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product2,queryAtom));
	        
		}
		/**
		 * A unit test suite for JUnit. Reaction: Propene.
		 * CC!=!C => C[C*][C*]
		 *
		 * @cdk.inchi InChI=1/C3H6/c1-3-2/h3H,1H2,2H3
		 * 
		 * @return    The test suite
		 */
		@Test public void testCsp2DoubleB() throws Exception {
			//Smiles("CC=C")
			IMolecule molecule = builder.newMolecule();
			molecule.addAtom(builder.newAtom("C"));
			molecule.addAtom(builder.newAtom("C"));
			molecule.addAtom(builder.newAtom("C"));
			molecule.addBond(0, 1, IBond.Order.SINGLE);
			molecule.addBond(1, 2, IBond.Order.DOUBLE);
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addBond(0, 3, IBond.Order.SINGLE);
			molecule.addBond(0, 4, IBond.Order.SINGLE);
			molecule.addBond(0, 5, IBond.Order.SINGLE);
			molecule.addBond(1, 6, IBond.Order.SINGLE);
			molecule.addBond(2, 7, IBond.Order.SINGLE);
			molecule.addBond(2, 8, IBond.Order.SINGLE);
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
			
			molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER,true);

	        IMoleculeSet setOfReactants = builder.newMoleculeSet();
	        setOfReactants.addMolecule(molecule);
			
			IReactionProcess type  = new HomolyticCleavageReaction(); 
			HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
	        type.setParameters(params);
	        
	        /* initiate */
			IReactionSet setOfReactions = type.initiate(setOfReactants, null);

	        Assert.assertEquals(1, setOfReactions.getReactionCount());

	        // expected products 
	        
	        //Smiles("C[C*][C*]")
			IMolecule expected1 = builder.newMolecule();
			expected1.addAtom(builder.newAtom("C"));
			expected1.addAtom(builder.newAtom("C"));
	        expected1.addSingleElectron(builder.newSingleElectron(expected1.getAtom(1)));
			expected1.addAtom(builder.newAtom("C"));
	        expected1.addSingleElectron(builder.newSingleElectron(expected1.getAtom(2)));
			expected1.addBond(0, 1, IBond.Order.SINGLE);
			expected1.addBond(1, 2, IBond.Order.SINGLE);
			expected1.addAtom(builder.newAtom("H"));
			expected1.addAtom(builder.newAtom("H"));
			expected1.addAtom(builder.newAtom("H"));
			expected1.addAtom(builder.newAtom("H"));
			expected1.addAtom(builder.newAtom("H"));
			expected1.addAtom(builder.newAtom("H"));
			expected1.addBond(0, 3, IBond.Order.SINGLE);
			expected1.addBond(0, 4, IBond.Order.SINGLE);
			expected1.addBond(0, 5, IBond.Order.SINGLE);
			expected1.addBond(1, 6, IBond.Order.SINGLE);
			expected1.addBond(2, 7, IBond.Order.SINGLE);
			expected1.addBond(2, 8, IBond.Order.SINGLE);
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
			lpcheck.saturate(expected1);
	        IMolecule product1 = setOfReactions.getReaction(0).getProducts().getMolecule(0);
	        QueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
	        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product1,queryAtom));
	        
		}
		/**
		 * A unit test suite for JUnit. Reaction: Allene.
		 * C=C!=!C => C=[C*][C*]
		 *
		 * @cdk.inchi InChI=1/C3H4/c1-3-2/h1-2H2
		 * 
		 * @return    The test suite
		 */
		@Test public void testCspDoubleB() throws Exception {
			//Smiles("C=C=C")
			IMolecule molecule = builder.newMolecule();
			molecule.addAtom(builder.newAtom("C"));
			molecule.addAtom(builder.newAtom("C"));
			molecule.addAtom(builder.newAtom("C"));
			molecule.addBond(0, 1, IBond.Order.DOUBLE);
			molecule.addBond(1, 2, IBond.Order.DOUBLE);
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addBond(0, 3, IBond.Order.SINGLE);
			molecule.addBond(0, 4, IBond.Order.SINGLE);
			molecule.addBond(2, 5, IBond.Order.SINGLE);
			molecule.addBond(2, 6, IBond.Order.SINGLE);
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
			
			molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER,true);

	        IMoleculeSet setOfReactants = builder.newMoleculeSet();
	        setOfReactants.addMolecule(molecule);
			
			IReactionProcess type  = new HomolyticCleavageReaction(); 
			HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
	        type.setParameters(params);
	        
	        /* initiate */
			IReactionSet setOfReactions = type.initiate(setOfReactants, null);

	        Assert.assertEquals(1, setOfReactions.getReactionCount());
	        
	        // expected products 
	        
	        //Smiles("C=[C*][C*]")
	        IMolecule expected1 = builder.newMolecule();
	        expected1.addAtom(builder.newAtom("C"));
	        expected1.addAtom(builder.newAtom("C"));
	        expected1.addSingleElectron(builder.newSingleElectron(expected1.getAtom(1)));
	        expected1.addAtom(builder.newAtom("C"));
	        expected1.addSingleElectron(builder.newSingleElectron(expected1.getAtom(2)));
	        expected1.addBond(0, 1, IBond.Order.DOUBLE);
	        expected1.addBond(1, 2, IBond.Order.SINGLE);
	        expected1.addAtom(builder.newAtom("H"));
	        expected1.addAtom(builder.newAtom("H"));
	        expected1.addAtom(builder.newAtom("H"));
	        expected1.addAtom(builder.newAtom("H"));
	        expected1.addBond(0, 3, IBond.Order.SINGLE);
	        expected1.addBond(0, 4, IBond.Order.SINGLE);
	        expected1.addBond(2, 5, IBond.Order.SINGLE);
	        expected1.addBond(2, 6, IBond.Order.SINGLE);
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
			lpcheck.saturate(expected1);
	        IMolecule product1 = setOfReactions.getReaction(0).getProducts().getMolecule(0);
	        QueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
	        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product1,queryAtom));

		}
		/**
		 * A unit test suite for JUnit. Reaction: Propyne.
		 * CC#C => C[C*]=[C*]
		 *         
		 * @cdk.inchi InChI=1/C3H4/c1-3-2/h1H,2H3
		 * 
		 * @return    The test suite
		 */
		@Test public void testCspTripleB() throws Exception {
			//Smiles("CC#C")
			IMolecule molecule = builder.newMolecule();
			molecule.addAtom(builder.newAtom("C"));
			molecule.addAtom(builder.newAtom("C"));
			molecule.addAtom(builder.newAtom("C"));
			molecule.addBond(0, 1, IBond.Order.SINGLE);
			molecule.addBond(1, 2, IBond.Order.TRIPLE);
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addBond(0, 3, IBond.Order.SINGLE);
			molecule.addBond(0, 4, IBond.Order.SINGLE);
			molecule.addBond(0, 5, IBond.Order.SINGLE);
			molecule.addBond(2, 6, IBond.Order.SINGLE);
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
			
			molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER,true);

	        IMoleculeSet setOfReactants = builder.newMoleculeSet();
	        setOfReactants.addMolecule(molecule);
			
			IReactionProcess type  = new HomolyticCleavageReaction(); 
			HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
	        type.setParameters(params);
	        
	        /* initiate */
			IReactionSet setOfReactions = type.initiate(setOfReactants, null);

	        Assert.assertEquals(1, setOfReactions.getReactionCount());
	        
	        // expected products 
	        
	        //Smiles("C[C*]=[C*]")
	        IMolecule expected1 = builder.newMolecule();
	        expected1.addAtom(builder.newAtom("C"));
	        expected1.addAtom(builder.newAtom("C"));
	        expected1.addSingleElectron(builder.newSingleElectron(expected1.getAtom(1)));
	        expected1.addAtom(builder.newAtom("C"));
	        expected1.addSingleElectron(builder.newSingleElectron(expected1.getAtom(2)));
	        expected1.addBond(0, 1, IBond.Order.SINGLE);
	        expected1.addBond(1, 2, IBond.Order.DOUBLE);
	        expected1.addAtom(builder.newAtom("H"));
	        expected1.addAtom(builder.newAtom("H"));
	        expected1.addAtom(builder.newAtom("H"));
	        expected1.addAtom(builder.newAtom("H"));
	        expected1.addBond(0, 3, IBond.Order.SINGLE);
	        expected1.addBond(0, 4, IBond.Order.SINGLE);
	        expected1.addBond(0, 5, IBond.Order.SINGLE);
	        expected1.addBond(2, 6, IBond.Order.SINGLE);
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
	        IMolecule product1 = setOfReactions.getReaction(0).getProducts().getMolecule(0);
	        QueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
	        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product1,queryAtom));

		}

		/**
		 * A unit test suite for JUnit. Reaction: dimethylamine.
		 * CN!-!C => C[N*] + [C*]
		 *
		 * @cdk.inchi  InChI=1/C2H7N/c1-3-2/h3H,1-2H3
		 * 
		 * @return    The test suite
		 */
		@Test public void testNsp3SingleB() throws Exception {
			//Smiles("CNC")
			IMolecule molecule = builder.newMolecule();
			molecule.addAtom(builder.newAtom("C"));
			molecule.addAtom(builder.newAtom("N"));
			molecule.addAtom(builder.newAtom("C"));
			molecule.addBond(0, 1, IBond.Order.SINGLE);
			molecule.addBond(1, 2, IBond.Order.SINGLE);
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addBond(0, 3, IBond.Order.SINGLE);
			molecule.addBond(0, 4, IBond.Order.SINGLE);
			molecule.addBond(0, 5, IBond.Order.SINGLE);
			molecule.addBond(1, 6, IBond.Order.SINGLE);
			molecule.addBond(2, 7, IBond.Order.SINGLE);
			molecule.addBond(2, 8, IBond.Order.SINGLE);
			molecule.addBond(2, 9, IBond.Order.SINGLE);
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
			lpcheck.saturate(molecule);
			
			molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER,true);

	        IMoleculeSet setOfReactants = builder.newMoleculeSet();
	        setOfReactants.addMolecule(molecule);
			
			IReactionProcess type  = new HomolyticCleavageReaction(); 
			HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
	        type.setParameters(params);
	        
	        /* initiate */
			IReactionSet setOfReactions = type.initiate(setOfReactants, null);

	        Assert.assertEquals(1, setOfReactions.getReactionCount());
	        
	        // expected products 
	        
	        //Smiles("C[N*]")
	        IMolecule expected1 = builder.newMolecule();
	        expected1.addAtom(builder.newAtom("C"));
	        expected1.addAtom(builder.newAtom("N"));
	        expected1.addSingleElectron(builder.newSingleElectron(expected1.getAtom(1)));
	        expected1.addBond(0, 1, IBond.Order.SINGLE);
	        expected1.addAtom(builder.newAtom("H"));
	        expected1.addAtom(builder.newAtom("H"));
	        expected1.addAtom(builder.newAtom("H"));
	        expected1.addAtom(builder.newAtom("H"));
	        expected1.addBond(0, 2, IBond.Order.SINGLE);
	        expected1.addBond(0, 3, IBond.Order.SINGLE);
	        expected1.addBond(0, 4, IBond.Order.SINGLE);
	        expected1.addBond(1, 5, IBond.Order.SINGLE);
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
			lpcheck.saturate(expected1);
	        IMolecule product1 = setOfReactions.getReaction(0).getProducts().getMolecule(0);
	        QueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
	        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product1,queryAtom));
			
			
	        //Smiles("[C*]")
	        IMolecule expected2 = builder.newMolecule();
	        expected2.addAtom(builder.newAtom("C"));
	        expected2.addSingleElectron(builder.newSingleElectron(expected2.getAtom(0)));
	        expected2.addAtom(builder.newAtom("H"));
	        expected2.addAtom(builder.newAtom("H"));
	        expected2.addAtom(builder.newAtom("H"));
	        expected2.addBond(0, 1, IBond.Order.SINGLE);
	        expected2.addBond(0, 2, IBond.Order.SINGLE);
	        expected2.addBond(0, 3, IBond.Order.SINGLE);
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected2);
	        IMolecule product2 = setOfReactions.getReaction(0).getProducts().getMolecule(1);
	        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected2);
	        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product2,queryAtom));
	        
		}
		/**
		 * A unit test suite for JUnit. Reaction:.
		 * C[N+]!-!C => C[N*+] + [C*]
		 * 
		 * @return    The test suite
		 */
		@Test public void testNsp3ChargeSingleB() throws Exception {
			//Smiles("C[N+]C")
			IMolecule molecule = builder.newMolecule();
			molecule.addAtom(builder.newAtom("C"));
			molecule.addAtom(builder.newAtom("N"));
			molecule.getAtom(1).setFormalCharge(+1);
			molecule.addAtom(builder.newAtom("C"));
			molecule.addBond(0, 1, IBond.Order.SINGLE);
			molecule.addBond(1, 2, IBond.Order.SINGLE);
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addBond(0, 3, IBond.Order.SINGLE);
			molecule.addBond(0, 4, IBond.Order.SINGLE);
			molecule.addBond(0, 5, IBond.Order.SINGLE);
			molecule.addBond(1, 6, IBond.Order.SINGLE);
			molecule.addBond(1, 7, IBond.Order.SINGLE);
			molecule.addBond(2, 8, IBond.Order.SINGLE);
			molecule.addBond(2, 9, IBond.Order.SINGLE);
			molecule.addBond(2, 10, IBond.Order.SINGLE);
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
			lpcheck.saturate(molecule);
			
			molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER,true);

	        IMoleculeSet setOfReactants = builder.newMoleculeSet();
	        setOfReactants.addMolecule(molecule);
			
			IReactionProcess type  = new HomolyticCleavageReaction(); 
			HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
	        type.setParameters(params);
	        
	        /* initiate */
			IReactionSet setOfReactions = type.initiate(setOfReactants, null);

	        Assert.assertEquals(0, setOfReactions.getReactionCount());
	        
		}
		/**
		 * A unit test suite for JUnit. Reaction: N-methylmethanimine.
		 * C=N!-!C =>[C*] +  C=[N*]
		 *
		 * @cdk.inchi InChI=1/C2H5N/c1-3-2/h1H2,2H3
		 * 
		 * @return    The test suite
		 */
		@Test public void testNsp2SingleB() throws Exception {
			//Smiles("C=NC")
			IMolecule molecule = builder.newMolecule();
			molecule.addAtom(builder.newAtom("C"));
			molecule.addAtom(builder.newAtom("N"));
			molecule.addAtom(builder.newAtom("C"));
			molecule.addBond(0, 1, IBond.Order.DOUBLE);
			molecule.addBond(1, 2, IBond.Order.SINGLE);
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addBond(0, 3, IBond.Order.SINGLE);
			molecule.addBond(0, 4, IBond.Order.SINGLE);
			molecule.addBond(2, 5, IBond.Order.SINGLE);
			molecule.addBond(2, 6, IBond.Order.SINGLE);
			molecule.addBond(2, 7, IBond.Order.SINGLE);
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
			lpcheck.saturate(molecule);
			
			molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER,true);

	        IMoleculeSet setOfReactants = builder.newMoleculeSet();
	        setOfReactants.addMolecule(molecule);
			
			IReactionProcess type  = new HomolyticCleavageReaction(); 
			HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
	        type.setParameters(params);
	        
	        /* initiate */
			IReactionSet setOfReactions = type.initiate(setOfReactants, null);

	        Assert.assertEquals(1, setOfReactions.getReactionCount());
	        
	        // expected products 

	        //Smiles("[C*]")
	        IMolecule expected1 = builder.newMolecule();
	        expected1.addAtom(builder.newAtom("C"));
	        expected1.addSingleElectron(builder.newSingleElectron(expected1.getAtom(0)));
	        expected1.addAtom(builder.newAtom("H"));
	        expected1.addAtom(builder.newAtom("H"));
	        expected1.addAtom(builder.newAtom("H"));
	        expected1.addBond(0, 1, IBond.Order.SINGLE);
	        expected1.addBond(0, 2, IBond.Order.SINGLE);
	        expected1.addBond(0, 3, IBond.Order.SINGLE);
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
	        IMolecule product1 = setOfReactions.getReaction(0).getProducts().getMolecule(1);
	        QueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
	        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product1,queryAtom));
	        
	        //Smiles("C=[N*]")
			IMolecule expected2 = builder.newMolecule();
			expected2.addAtom(builder.newAtom("C"));
			expected2.addAtom(builder.newAtom("N"));
			expected2.addSingleElectron(builder.newSingleElectron(expected2.getAtom(1)));
			expected2.addBond(0, 1, IBond.Order.DOUBLE);
			expected2.addAtom(builder.newAtom("H"));
			expected2.addAtom(builder.newAtom("H"));
			expected2.addBond(0, 2, IBond.Order.SINGLE);
			expected2.addBond(0, 3, IBond.Order.SINGLE);
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected2);
			lpcheck.saturate(expected2);
	        IMolecule product2 = setOfReactions.getReaction(0).getProducts().getMolecule(0);
	        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected2);
	        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product2,queryAtom));
	        
		}
		/**
		 * A unit test suite for JUnit. Reaction:.
		 * C=[N+]!-!C => C=[N*+] + [C*]
		 * 
		 * @return    The test suite
		 */
		@Test public void testNsp2ChargeSingleB() throws Exception {
			//Smiles("C=[N+]C")
			IMolecule molecule = builder.newMolecule();
			molecule.addAtom(builder.newAtom("C"));
			molecule.addAtom(builder.newAtom("N"));
			molecule.getAtom(1).setFormalCharge(1);
			molecule.addAtom(builder.newAtom("C"));
			molecule.addBond(0, 1, IBond.Order.DOUBLE);
			molecule.addBond(1, 2, IBond.Order.SINGLE);
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addBond(0, 3, IBond.Order.SINGLE);
			molecule.addBond(0, 4, IBond.Order.SINGLE);
			molecule.addBond(1, 5, IBond.Order.SINGLE);
			molecule.addBond(2, 6, IBond.Order.SINGLE);
			molecule.addBond(2, 7, IBond.Order.SINGLE);
			molecule.addBond(2, 8, IBond.Order.SINGLE);
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
			lpcheck.saturate(molecule);
			
			molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER,true);

	        IMoleculeSet setOfReactants = builder.newMoleculeSet();
	        setOfReactants.addMolecule(molecule);
			
			IReactionProcess type  = new HomolyticCleavageReaction(); 
			HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
	        type.setParameters(params);
	        
	        /* initiate */
			IReactionSet setOfReactions = type.initiate(setOfReactants, null);

	        Assert.assertEquals(0, setOfReactions.getReactionCount());
	        
	        
		}
		/**
		 * A unit test suite for JUnit. Reaction: N-methylmethanimine.
		 * CN!=!C => C[N*]-[C*]
		 *
		 * @cdk.inchi InChI=1/C2H5N/c1-3-2/h1H2,2H3
		 * 
		 * @return    The test suite
		 */
		@Test public void testNsp2DoubleB() throws Exception {
			//Smiles("CN=C")
			IMolecule molecule = builder.newMolecule();
			molecule.addAtom(builder.newAtom("C"));
			molecule.addAtom(builder.newAtom("N"));
			molecule.addAtom(builder.newAtom("C"));
			molecule.addBond(0, 1, IBond.Order.SINGLE);
			molecule.addBond(1, 2, IBond.Order.DOUBLE);
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addBond(0, 3, IBond.Order.SINGLE);
			molecule.addBond(0, 4, IBond.Order.SINGLE);
			molecule.addBond(0, 5, IBond.Order.SINGLE);
			molecule.addBond(2, 6, IBond.Order.SINGLE);
			molecule.addBond(2, 7, IBond.Order.SINGLE);
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
			lpcheck.saturate(molecule);
			
			molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER,true);

	        IMoleculeSet setOfReactants = builder.newMoleculeSet();
	        setOfReactants.addMolecule(molecule);
			
			IReactionProcess type  = new HomolyticCleavageReaction(); 
			HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
	        type.setParameters(params);
	        
	        /* initiate */
			IReactionSet setOfReactions = type.initiate(setOfReactants, null);

	        Assert.assertEquals(1, setOfReactions.getReactionCount());
	        
	        // expected products 

	        //Smiles("C[N*]-[C*]")
	        IMolecule expected1 = builder.newMolecule();
	        expected1.addAtom(builder.newAtom("C"));
	        expected1.addAtom(builder.newAtom("N"));
			expected1.addSingleElectron(builder.newSingleElectron(expected1.getAtom(1)));
	        expected1.addAtom(builder.newAtom("C"));
			expected1.addSingleElectron(builder.newSingleElectron(expected1.getAtom(2)));
	        expected1.addBond(0, 1, IBond.Order.SINGLE);
	        expected1.addBond(1, 2, IBond.Order.SINGLE);
	        expected1.addAtom(builder.newAtom("H"));
	        expected1.addAtom(builder.newAtom("H"));
	        expected1.addAtom(builder.newAtom("H"));
	        expected1.addAtom(builder.newAtom("H"));
	        expected1.addAtom(builder.newAtom("H"));
	        expected1.addBond(0, 3, IBond.Order.SINGLE);
	        expected1.addBond(0, 4, IBond.Order.SINGLE);
	        expected1.addBond(0, 5, IBond.Order.SINGLE);
	        expected1.addBond(2, 6, IBond.Order.SINGLE);
	        expected1.addBond(2, 7, IBond.Order.SINGLE);
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
			lpcheck.saturate(expected1);
	        IMolecule product1 = setOfReactions.getReaction(0).getProducts().getMolecule(0);
	        QueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
	        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product1,queryAtom));
		}
		/**
		 * A unit test suite for JUnit. Reaction: .
		 * C[N+]!=!C => C[N*+]-[C*]
		 * 
		 * @return    The test suite
		 */
		@Test public void testNsp2ChargeDoubleB() throws Exception {
			//Smiles("C[N+]=C")
			IMolecule molecule = builder.newMolecule();
			molecule.addAtom(builder.newAtom("C"));
			molecule.addAtom(builder.newAtom("N"));
			molecule.getAtom(1).setFormalCharge(+1);
			molecule.addAtom(builder.newAtom("C"));
			molecule.addBond(0, 1, IBond.Order.SINGLE);
			molecule.addBond(1, 2, IBond.Order.DOUBLE);
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addBond(0, 3, IBond.Order.SINGLE);
			molecule.addBond(0, 4, IBond.Order.SINGLE);
			molecule.addBond(0, 5, IBond.Order.SINGLE);
			molecule.addBond(1, 6, IBond.Order.SINGLE);
			molecule.addBond(2, 7, IBond.Order.SINGLE);
			molecule.addBond(2, 8, IBond.Order.SINGLE);
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
			lpcheck.saturate(molecule);
			
			molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER,true);

	        IMoleculeSet setOfReactants = builder.newMoleculeSet();
	        setOfReactants.addMolecule(molecule);
			
			IReactionProcess type  = new HomolyticCleavageReaction(); 
			HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
	        type.setParameters(params);
	        
	        /* initiate */
			IReactionSet setOfReactions = type.initiate(setOfReactants, null);

	        Assert.assertEquals(0, setOfReactions.getReactionCount());
	        
		}
		/**
		 * A unit test suite for JUnit. Reaction: formonitrile.
		 * N!#!C => [N*]=[C*]
		 *
		 * @cdk.inchi InChI=1/CHN/c1-2/h1H
		 * 
		 * @return    The test suite
		 */
		@Test public void testNspTripleB() throws Exception {
			//Smiles("N#C")
			IMolecule molecule = builder.newMolecule();
			molecule.addAtom(builder.newAtom("N"));
			molecule.addAtom(builder.newAtom("C"));
			molecule.addBond(0, 1, IBond.Order.TRIPLE);
			molecule.addAtom(builder.newAtom("H"));
			molecule.addBond(1, 2, IBond.Order.SINGLE);
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
			lpcheck.saturate(molecule);
			
			molecule.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getBond(0).setFlag(CDKConstants.REACTIVE_CENTER,true);

	        IMoleculeSet setOfReactants = builder.newMoleculeSet();
	        setOfReactants.addMolecule(molecule);
			
			IReactionProcess type  = new HomolyticCleavageReaction(); 
			HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
	        type.setParameters(params);
	        
	        /* initiate */
			IReactionSet setOfReactions = type.initiate(setOfReactants, null);

	        Assert.assertEquals(1, setOfReactions.getReactionCount());
	        
	        // expected products 

	        //Smiles("[N*]=[C*]")
	        IMolecule expected1 = builder.newMolecule();
	        expected1.addAtom(builder.newAtom("N"));
			expected1.addSingleElectron(builder.newSingleElectron(expected1.getAtom(0)));
	        expected1.addAtom(builder.newAtom("C"));
			expected1.addSingleElectron(builder.newSingleElectron(expected1.getAtom(1)));
	        expected1.addBond(0, 1, IBond.Order.DOUBLE);
	        expected1.addAtom(builder.newAtom("H"));
	        expected1.addBond(1, 2, IBond.Order.SINGLE);
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
			lpcheck.saturate(expected1);
	        IMolecule product1 = setOfReactions.getReaction(0).getProducts().getMolecule(0);
	        QueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
	        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product1,queryAtom));
		}
		/**
		 * A unit test suite for JUnit. Reaction:.
		 * [N+]!#!C => [N*+]=[C*]
		 * 
		 * @return    The test suite
		 */
		@Test public void testNspChargeTripleB() throws Exception {
			//Smiles("[N+]#C")
			IMolecule molecule = builder.newMolecule();
			molecule.addAtom(builder.newAtom("N"));
			molecule.addAtom(builder.newAtom("C"));
			molecule.getAtom(0).setFormalCharge(+1);
			molecule.addBond(0, 1, IBond.Order.TRIPLE);
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addBond(0, 2, IBond.Order.SINGLE);
			molecule.addBond(1, 3, IBond.Order.SINGLE);
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
			lpcheck.saturate(molecule);
			
			molecule.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getBond(0).setFlag(CDKConstants.REACTIVE_CENTER,true);

	        IMoleculeSet setOfReactants = builder.newMoleculeSet();
	        setOfReactants.addMolecule(molecule);
			
			IReactionProcess type  = new HomolyticCleavageReaction(); 
			HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
	        type.setParameters(params);
	        
	        /* initiate */
			IReactionSet setOfReactions = type.initiate(setOfReactants, null);

	        Assert.assertEquals(0, setOfReactions.getReactionCount());
	        			
		}
		/**
		 * A unit test suite for JUnit. Reaction:  methoxymethane.
		 * CO!-!C =>  C[O*] + [C*]
		 *           
		 * @cdk.inchi InChI=1/C2H6O/c1-3-2/h1-2H3
		 * 
		 * @return    The test suite
		 */
		@Test public void testOsp2SingleB() throws Exception {
			//Smiles("COC")
			IMolecule molecule = builder.newMolecule();
			molecule.addAtom(builder.newAtom("C"));
			molecule.addAtom(builder.newAtom("O"));
			molecule.addAtom(builder.newAtom("C"));
			molecule.addBond(0, 1, IBond.Order.SINGLE);
			molecule.addBond(1, 2, IBond.Order.SINGLE);
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addBond(0, 3, IBond.Order.SINGLE);
			molecule.addBond(0, 4, IBond.Order.SINGLE);
			molecule.addBond(0, 5, IBond.Order.SINGLE);
			molecule.addBond(2, 6, IBond.Order.SINGLE);
			molecule.addBond(2, 7, IBond.Order.SINGLE);
			molecule.addBond(2, 8, IBond.Order.SINGLE);
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
			lpcheck.saturate(molecule);
			
			molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER,true);

	        IMoleculeSet setOfReactants = builder.newMoleculeSet();
	        setOfReactants.addMolecule(molecule);
			
			IReactionProcess type  = new HomolyticCleavageReaction(); 
			HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
	        type.setParameters(params);
	        
	        /* initiate */
			IReactionSet setOfReactions = type.initiate(setOfReactants, null);

	        Assert.assertEquals(1, setOfReactions.getReactionCount());
	        
	        // expected products 

	        //Smiles("C[O*]")
	        IMolecule expected1 = builder.newMolecule();
	        expected1.addAtom(builder.newAtom("C"));
			expected1.addAtom(builder.newAtom("O"));
			expected1.addSingleElectron(builder.newSingleElectron(expected1.getAtom(1)));
			expected1.addBond(0, 1, IBond.Order.SINGLE);
			expected1.addAtom(builder.newAtom("H"));
			expected1.addAtom(builder.newAtom("H"));
			expected1.addAtom(builder.newAtom("H"));
			expected1.addBond(0, 2, IBond.Order.SINGLE);
			expected1.addBond(0, 3, IBond.Order.SINGLE);
			expected1.addBond(0, 4, IBond.Order.SINGLE);
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
			lpcheck.saturate(expected1);
	        IMolecule product1 = setOfReactions.getReaction(0).getProducts().getMolecule(0);
	        QueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
	        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product1,queryAtom));
			
	        //Smiles("[C*]")
	        IMolecule expected2 = builder.newMolecule();
	        expected2.addAtom(builder.newAtom("C"));
	        expected2.addSingleElectron(builder.newSingleElectron(expected2.getAtom(0)));
	        expected2.addAtom(builder.newAtom("H"));
	        expected2.addAtom(builder.newAtom("H"));
	        expected2.addAtom(builder.newAtom("H"));
	        expected2.addBond(0, 1, IBond.Order.SINGLE);
	        expected2.addBond(0, 2, IBond.Order.SINGLE);
	        expected2.addBond(0, 3, IBond.Order.SINGLE);
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected2);
	        IMolecule product2 = setOfReactions.getReaction(0).getProducts().getMolecule(1);
	        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected2);
	        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product2,queryAtom));
		}
		/**
		 * A unit test suite for JUnit. Reaction:  methoxymethane.
		 * C[O+]!-!C =>  C[O*+] + [C*]
		 * 
		 * @cdk.inchi InChI=1/C2H6O/c1-3-2/h1-2H3
		 * 
		 * @return    The test suite
		 */
		@Test public void testOsp2ChargeSingleB() throws Exception {
			//Smiles("C[O+]C")
			IMolecule molecule = builder.newMolecule();
			molecule.addAtom(builder.newAtom("C"));
			molecule.addAtom(builder.newAtom("O"));
			molecule.getAtom(1).setFormalCharge(+1);
			molecule.addAtom(builder.newAtom("C"));
			molecule.addBond(0, 1, IBond.Order.SINGLE);
			molecule.addBond(1, 2, IBond.Order.SINGLE);
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addBond(0, 3, IBond.Order.SINGLE);
			molecule.addBond(0, 4, IBond.Order.SINGLE);
			molecule.addBond(0, 5, IBond.Order.SINGLE);
			molecule.addBond(1, 6, IBond.Order.SINGLE);
			molecule.addBond(2, 7, IBond.Order.SINGLE);
			molecule.addBond(2, 8, IBond.Order.SINGLE);
			molecule.addBond(2, 9, IBond.Order.SINGLE);
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
			lpcheck.saturate(molecule);
			
			molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER,true);

	        IMoleculeSet setOfReactants = builder.newMoleculeSet();
	        setOfReactants.addMolecule(molecule);
			
			IReactionProcess type  = new HomolyticCleavageReaction(); 
			HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
	        type.setParameters(params);
	        
	        /* initiate */
			IReactionSet setOfReactions = type.initiate(setOfReactants, null);

	        Assert.assertEquals(0, setOfReactions.getReactionCount());
	        
		}
		/**
		 * A unit test suite for JUnit. Reaction: formaldehyde.
		 * O!=!C => [O*][C*]
		 *
		 * @cdk.inchi  InChI=1/CH2O/c1-2/h1H2
		 * 
		 * @return    The test suite
		 */
		@Test public void testOspDoubleB() throws Exception {
			//Smiles("O=C")
			IMolecule molecule = builder.newMolecule();
			molecule.addAtom(builder.newAtom("O"));
			molecule.addAtom(builder.newAtom("C"));
			molecule.addBond(0, 1, IBond.Order.DOUBLE);
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addBond(1, 2, IBond.Order.SINGLE);
			molecule.addBond(1, 3, IBond.Order.SINGLE);
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
			lpcheck.saturate(molecule);
			
			molecule.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getBond(0).setFlag(CDKConstants.REACTIVE_CENTER,true);

	        IMoleculeSet setOfReactants = builder.newMoleculeSet();
	        setOfReactants.addMolecule(molecule);
			
			IReactionProcess type  = new HomolyticCleavageReaction(); 
			HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
	        type.setParameters(params);
	        
	        /* initiate */
			IReactionSet setOfReactions = type.initiate(setOfReactants, null);

	        Assert.assertEquals(1, setOfReactions.getReactionCount());
	        
	        // expected products 

	        //Smiles("[O*][C*]")
	        IMolecule expected1 = builder.newMolecule();
	        expected1.addAtom(builder.newAtom("O"));
	        expected1.addSingleElectron(builder.newSingleElectron(expected1.getAtom(0)));
	        expected1.addAtom(builder.newAtom("C"));
	        expected1.addSingleElectron(builder.newSingleElectron(expected1.getAtom(1)));
	        expected1.addBond(0, 1, IBond.Order.SINGLE);
	        expected1.addAtom(builder.newAtom("H"));
	        expected1.addAtom(builder.newAtom("H"));
	        expected1.addBond(1, 2, IBond.Order.SINGLE);
	        expected1.addBond(1, 3, IBond.Order.SINGLE);
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
			lpcheck.saturate(expected1);
	        IMolecule product1 = setOfReactions.getReaction(0).getProducts().getMolecule(0);
	        QueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
	        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product1,queryAtom));
		}
		/**
		 * A unit test suite for JUnit. Reaction: .
		 * [O+]!=!C => [O*][C*+]
		 * 
		 * @return    The test suite
		 */
		@Test public void testOspChargeDoubleB() throws Exception {
			//Smiles("[O+]=C")
			IMolecule molecule = builder.newMolecule();
			molecule.addAtom(builder.newAtom("O"));
			molecule.getAtom(0).setFormalCharge(+1);
			molecule.addAtom(builder.newAtom("C"));
			molecule.addBond(0, 1, IBond.Order.DOUBLE);
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addBond(0, 2, IBond.Order.SINGLE);
			molecule.addBond(1, 3, IBond.Order.SINGLE);
			molecule.addBond(1, 4, IBond.Order.SINGLE);
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
			lpcheck.saturate(molecule);
			
			molecule.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getBond(0).setFlag(CDKConstants.REACTIVE_CENTER,true);

	        IMoleculeSet setOfReactants = builder.newMoleculeSet();
	        setOfReactants.addMolecule(molecule);
			
			IReactionProcess type  = new HomolyticCleavageReaction(); 
			HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
	        type.setParameters(params);
	        
	        /* initiate */
			IReactionSet setOfReactions = type.initiate(setOfReactants, null);

	        Assert.assertEquals(0, setOfReactions.getReactionCount());
	    }
		/**
		 * A unit test suite for JUnit. Reaction: fluoromethane.
		 * F!-!C => [F*] + [C*]
		 *
		 * @cdk.inchi InChI=1/CH3F/c1-2/h1H3
		 * 
		 * @return    The test suite
		 */
		@Test public void testFspSingleB() throws Exception {
			//Smiles("FC")
			IMolecule molecule = builder.newMolecule();
			molecule.addAtom(builder.newAtom("F"));
			molecule.addAtom(builder.newAtom("C"));
			molecule.addBond(0, 1, IBond.Order.SINGLE);
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addAtom(builder.newAtom("H"));
			molecule.addBond(1, 2, IBond.Order.SINGLE);
			molecule.addBond(1, 3, IBond.Order.SINGLE);
			molecule.addBond(1, 4, IBond.Order.SINGLE);
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
			lpcheck.saturate(molecule);
			
			molecule.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getBond(0).setFlag(CDKConstants.REACTIVE_CENTER,true);

	        IMoleculeSet setOfReactants = builder.newMoleculeSet();
	        setOfReactants.addMolecule(molecule);
			
			IReactionProcess type  = new HomolyticCleavageReaction(); 
			HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
	        type.setParameters(params);
	        
	        /* initiate */
			IReactionSet setOfReactions = type.initiate(setOfReactants, null);

	        Assert.assertEquals(1, setOfReactions.getReactionCount());
	        

	        //Smiles("[F*]")
	        IMolecule expected1 = builder.newMolecule();
	        expected1.addAtom(builder.newAtom("F"));
	        expected1.addSingleElectron(builder.newSingleElectron(expected1.getAtom(0)));
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
			lpcheck.saturate(expected1);
	        IMolecule product1 = setOfReactions.getReaction(0).getProducts().getMolecule(0);
	        QueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
	        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product1,queryAtom));
	        
	        //Smiles("[C*]")
	        IMolecule expected2 = builder.newMolecule();
	        expected2.addAtom(builder.newAtom("C"));
	        expected2.addSingleElectron(builder.newSingleElectron(expected2.getAtom(0)));
	        expected2.addAtom(builder.newAtom("H"));
	        expected2.addAtom(builder.newAtom("H"));
	        expected2.addAtom(builder.newAtom("H"));
	        expected2.addBond(0, 1, IBond.Order.SINGLE);
	        expected2.addBond(0, 2, IBond.Order.SINGLE);
	        expected2.addBond(0, 3, IBond.Order.SINGLE);
	        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected2);
	        IMolecule product2 = setOfReactions.getReaction(0).getProducts().getMolecule(1);
	        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected2);
	        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product2,queryAtom));
		}
		/**
		 * A unit test suite for JUnit. 
		 * 
		 * @return    The test suite
		 */
		@Test public void testCentreActive() throws Exception {
			IReactionProcess type  = new HomolyticCleavageReaction();

			HashMap<String,Object> params = type.getParameters();
			Assert.assertTrue(params.get("hasActiveCenter") instanceof Boolean);
			Assert.assertFalse((Boolean)params.get("hasActiveCenter"));

	        params = new HashMap<String,Object>();
	        params.put("hasActiveCenter",Boolean.TRUE);
	        type.setParameters(params);
			Assert.assertTrue((Boolean)params.get("hasActiveCenter"));
		}
		/**
		 * A unit test suite for JUnit.
		 * 
		 * @return    The test suite
		 */
		@Test public void testCDKConstants_REACTIVE_CENTER() throws Exception {
			IReactionProcess type  = new HomolyticCleavageReaction();
			IMoleculeSet setOfReactants = builder.newMoleculeSet();

			/*C=O*/
			IMolecule molecule = builder.newMolecule();//Smiles("C=O")
			molecule.addAtom(builder.newAtom("C"));
			molecule.addAtom(builder.newAtom("O"));
			molecule.addBond(0, 1, IBond.Order.DOUBLE);
			addExplicitHydrogens(molecule);
			
		    AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
			lpcheck.saturate(molecule);
			setOfReactants.addMolecule(molecule);
			
			/*manually put the reactive center*/
			molecule.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
			molecule.getBond(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
			
	        HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
	        type.setParameters(params);
	        
	        /* initiate */
			makeSureAtomTypesAreRecognized(molecule);
			
	        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

	        Assert.assertEquals(1, setOfReactions.getReactionCount());
	        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

	        IMolecule reactant = setOfReactions.getReaction(0).getReactants().getMolecule(0);
			Assert.assertTrue(molecule.getAtom(0).getFlag(CDKConstants.REACTIVE_CENTER));
			Assert.assertTrue(reactant.getAtom(0).getFlag(CDKConstants.REACTIVE_CENTER));
			Assert.assertTrue(molecule.getAtom(1).getFlag(CDKConstants.REACTIVE_CENTER));
			Assert.assertTrue(reactant.getAtom(1).getFlag(CDKConstants.REACTIVE_CENTER));
			Assert.assertTrue(molecule.getBond(0).getFlag(CDKConstants.REACTIVE_CENTER));
			Assert.assertTrue(reactant.getBond(0).getFlag(CDKConstants.REACTIVE_CENTER));
		}

		/**
		 * A unit test suite for JUnit.
		 *  
		 * @return    The test suite
		 */
		@Test public void testMapping() throws Exception {
			IReactionProcess type  = new HomolyticCleavageReaction();
			IMoleculeSet setOfReactants = builder.newMoleculeSet();
			
			/*C=O*/
			IMolecule molecule = builder.newMolecule();//Smiles("C=O")
			molecule.addAtom(builder.newAtom("C"));
			molecule.addAtom(builder.newAtom("O"));
			molecule.addBond(0, 1, IBond.Order.DOUBLE);
			addExplicitHydrogens(molecule);
			
			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
			lpcheck.saturate(molecule);
			setOfReactants.addMolecule(molecule);
			
			/*automatic search of the center active*/
	        HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.FALSE);;
	        type.setParameters(params);
	        
	        /* initiate */
			makeSureAtomTypesAreRecognized(molecule);
			
	        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
	        
	        IMolecule product = setOfReactions.getReaction(0).getProducts().getMolecule(0);

	        Assert.assertEquals(3,setOfReactions.getReaction(0).getMappingCount());
	        IAtom mappedProductA1 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtom(0));
	        Assert.assertEquals(mappedProductA1, product.getAtom(0));
	        IAtom mappedProductA2 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtom(1));
	        Assert.assertEquals(mappedProductA2, product.getAtom(1));
	        IBond mappedProductB1 = (IBond)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getBond(0));
	        Assert.assertEquals(mappedProductB1, product.getBond(0));        
		}
	/**
	 * A unit test suite for JUnit. Reaction: Ethylbenzaldehyde.
	 * CCc1ccc(C=O)cc1  =>  C+ Cc1ccc(C=O)cc1
	 * CCc1ccc(C=O)cc1  =>  CC + c1ccc(C=O)cc1 
	 * Automatic looking for active center.
	 * 
	 * @cdk.inchi InChI=1/C9H10O/c1-2-8-3-5-9(7-10)6-4-8/h3-7H,2H2,1H3
	 *
	 * @return    The test suite
	 */
	@Test public void testEthylbenzaldehydeManual() throws Exception {
		IReactionProcess type  = new HomolyticCleavageReaction();
		IMoleculeSet setOfReactants = builder.newMoleculeSet();
		
		//smiles("CCc1ccc(C=O)cc1")
		IMolecule molecule = builder.newMolecule();
		molecule.addAtom(builder.newAtom("C"));
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(0, 1, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(1, 2, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(2, 3, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(3, 4, IBond.Order.DOUBLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(4, 5, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(5, 6, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("O"));
		molecule.addBond(6, 7, IBond.Order.DOUBLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(5, 8, IBond.Order.DOUBLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(8, 9, IBond.Order.SINGLE);
		molecule.addBond(9, 2, IBond.Order.DOUBLE);
		addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.saturate(molecule);
		

		molecule.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
		
		setOfReactants.addMolecule(molecule);
		
		/*has active center*/
        HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
        type.setParameters(params);
        
        /* initiate */
		makeSureAtomTypesAreRecognized(molecule);
		
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        Assert.assertEquals(2, setOfReactions.getReactionCount());
        Assert.assertEquals(2, setOfReactions.getReaction(0).getProductCount());
        Assert.assertEquals(2, setOfReactions.getReaction(1).getProductCount());

        IMolecule product = setOfReactions.getReaction(0).getProducts().getMolecule(0);
        /*C*/
        IMolecule molecule2 = builder.newMolecule();
        molecule2.addAtom(builder.newAtom("C"));
        molecule2.addAtom(builder.newAtom("H"));
        molecule2.addBond(0, 1, IBond.Order.SINGLE);
        molecule2.addAtom(builder.newAtom("H"));
        molecule2.addBond(0, 2, IBond.Order.SINGLE);
        molecule2.addAtom(builder.newAtom("H"));
        molecule2.addBond(0, 3, IBond.Order.SINGLE);
        molecule2.addSingleElectron(new SingleElectron(molecule2.getAtom(0)));
		
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule2,product));
		
		product = setOfReactions.getReaction(0).getProducts().getMolecule(1);
		/*Cc1ccc(C=O)cc1*/
		IMolecule molecule3 = builder.newMolecule();
		molecule3.addAtom(builder.newAtom("C"));
		molecule3.getAtom(0).setFormalCharge(1);
		molecule3.addAtom(builder.newAtom("C"));
		molecule3.addBond(0, 1, IBond.Order.SINGLE);
		molecule3.addAtom(builder.newAtom("C"));
		molecule3.addBond(1, 2, IBond.Order.SINGLE);
		molecule3.addAtom(builder.newAtom("C"));
		molecule3.addBond(2, 3, IBond.Order.DOUBLE);
		molecule3.addAtom(builder.newAtom("C"));
		molecule3.addBond(3, 4, IBond.Order.SINGLE);
		molecule3.addAtom(builder.newAtom("C"));
		molecule3.addBond(4, 5, IBond.Order.SINGLE);
		molecule3.addAtom(builder.newAtom("O"));
		molecule3.addBond(5, 6, IBond.Order.DOUBLE);
		molecule3.addAtom(builder.newAtom("C"));
		molecule3.addBond(4, 7, IBond.Order.DOUBLE);
		molecule3.addAtom(builder.newAtom("C"));
		molecule3.addBond(7, 8, IBond.Order.SINGLE);
		molecule3.addBond(8, 1, IBond.Order.DOUBLE);
		addExplicitHydrogens(molecule3);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule3);
        molecule3.addSingleElectron(new SingleElectron(molecule3.getAtom(0)));
		molecule3.getAtom(0).setFormalCharge(0);

		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule3,product));
		
		product = setOfReactions.getReaction(1).getProducts().getMolecule(0);
        /*CC*/
		molecule2 = builder.newMolecule();
		molecule2.addAtom(builder.newAtom("C"));
		molecule2.addAtom(builder.newAtom("C"));
		molecule2.addBond(0, 1, IBond.Order.SINGLE);
		molecule2.getAtom(0).setFormalCharge(1);
		addExplicitHydrogens(molecule2);
		molecule2.getAtom(0).setFormalCharge(0);
		molecule2.addSingleElectron(new SingleElectron(molecule2.getAtom(0)));

        
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule2,product));

		product = setOfReactions.getReaction(1).getProducts().getMolecule(1);
		/*c1ccc(C=O)cc1*/
		molecule3 = builder.newMolecule();
		molecule3.addAtom(builder.newAtom("C"));
		molecule3.addAtom(builder.newAtom("C"));
		molecule3.addBond(0, 1, IBond.Order.SINGLE);
		molecule3.addAtom(builder.newAtom("C"));
		molecule3.addBond(1, 2, IBond.Order.DOUBLE);
		molecule3.addAtom(builder.newAtom("C"));
		molecule3.addBond(2, 3, IBond.Order.SINGLE);
		molecule3.addAtom(builder.newAtom("C"));
		molecule3.addBond(3, 4, IBond.Order.SINGLE);
		molecule3.addAtom(builder.newAtom("O"));
		molecule3.addBond(4, 5, IBond.Order.DOUBLE);
		molecule3.addAtom(builder.newAtom("C"));
		molecule3.addBond(3, 6, IBond.Order.DOUBLE);
		molecule3.addAtom(builder.newAtom("C"));
		molecule3.addBond(6, 7, IBond.Order.SINGLE);
		molecule3.addBond(7, 0, IBond.Order.DOUBLE);

		molecule3.getAtom(0).setFormalCharge(1);
		addExplicitHydrogens(molecule3);
		molecule3.getAtom(0).setFormalCharge(0);
		molecule3.addSingleElectron(new SingleElectron(molecule3.getAtom(0)));
        
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule3);
		molecule3.addSingleElectron(new SingleElectron(molecule3.getAtom(0)));
		
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule3,product));
		
		
	}
	/**
	 * A unit test suite for JUnit. Reaction: Ethylbenzaldehyde.
	 * CCc1ccc(C=O)cc1  =>  C+ Cc1ccc(C=O)cc1
	 * CCc1ccc(C=O)cc1  =>  CC + c1ccc(C=O)cc1 
	 * Automatic looking for active center.
	 * 
	 * @cdk.inchi InChI=1/C9H10O/c1-2-8-3-5-9(7-10)6-4-8/h3-7H,2H2,1H3
	 * 
	 * @see       #testEthylbenzaldehydeManual()
	 * @return    The test suite
	 */
	@Test public void testEthylbenzaldehydeMapping() throws Exception {
		IReactionProcess type  = new HomolyticCleavageReaction();
		IMoleculeSet setOfReactants = builder.newMoleculeSet();
		
		//smiles("CCc1ccc(C=O)cc1")
		IMolecule molecule = builder.newMolecule();
		molecule.addAtom(builder.newAtom("C"));
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(0, 1, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(1, 2, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(2, 3, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(3, 4, IBond.Order.DOUBLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(4, 5, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(5, 6, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("O"));
		molecule.addBond(6, 7, IBond.Order.DOUBLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(5, 8, IBond.Order.DOUBLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(8, 9, IBond.Order.SINGLE);
		molecule.addBond(9, 2, IBond.Order.DOUBLE);
		addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
		
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.saturate(molecule);

		molecule.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
		
		setOfReactants.addMolecule(molecule);
		
		/*has active center*/
        HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
        type.setParameters(params);
        
        /* initiate */
		makeSureAtomTypesAreRecognized(molecule);
		
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        IMolecule product11 = setOfReactions.getReaction(0).getProducts().getMolecule(0);
        IMolecule product12 = setOfReactions.getReaction(0).getProducts().getMolecule(1);
		
		Assert.assertEquals(2,setOfReactions.getReaction(0).getMappingCount());
        IAtom mappedProductA1 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtom(0));
        Assert.assertEquals(mappedProductA1, product11.getAtom(0));
        IAtom mappedProductA2 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtom(1));
        Assert.assertEquals(mappedProductA2, product12.getAtom(0));
        
		IMolecule product21 = setOfReactions.getReaction(1).getProducts().getMolecule(0);
        IMolecule product22 = setOfReactions.getReaction(1).getProducts().getMolecule(1);
				
		Assert.assertEquals(2,setOfReactions.getReaction(0).getMappingCount());
        mappedProductA1 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(1), molecule.getAtom(1));
        Assert.assertEquals(mappedProductA1, product21.getAtom(1));
        mappedProductA2 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(1), molecule.getAtom(2));
        Assert.assertEquals(mappedProductA2, product22.getAtom(0));
        
	}
	/**
	 * Test to recognize if a IMolecule matcher correctly the CDKAtomTypes.
	 * 
	 * @param molecule          The IMolecule to analyze
	 * @throws CDKException
	 */
	private void makeSureAtomTypesAreRecognized(IMolecule molecule) throws CDKException {

		Iterator<IAtom> atoms = molecule.atoms();
		CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(molecule.getBuilder());
		while (atoms.hasNext()) {
				IAtom nextAtom = atoms.next();
				Assert.assertNotNull(
					"Missing atom type for: " + nextAtom, 
					matcher.findMatchingAtomType(molecule, nextAtom)
				);
		}
	}
}
