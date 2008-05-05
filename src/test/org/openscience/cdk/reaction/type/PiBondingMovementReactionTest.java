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
import org.openscience.cdk.DefaultChemObjectBuilder;
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
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.ReactionProcessTest;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * TestSuite that runs a test for the PiBondingMovemetReactionTest.
 * Generalized Reaction: C1=C(C)-C(C)=C-C=C1 -> C1(C)=C(C)-C=C-C=C1.
 *
 * FIXME: REACT: The tests fail if I don't put the smiles, strange
 * 
 * @cdk.module test-reaction
 */
public class PiBondingMovementReactionTest extends ReactionProcessTest {

	private final static  IChemObjectBuilder builder = NoNotificationChemObjectBuilder.getInstance();
	/**
	 *  The JUnit setup method
	 */
	 @BeforeClass public static void setUp() throws Exception {
	 	setReaction(PiBondingMovementReaction.class);
	 }
	 
	 /**
	  *  The JUnit setup method
	  */
	 @Test public void testPiBondingMovementReaction() throws Exception {
			IReactionProcess type = new PiBondingMovementReaction();
			Assert.assertNotNull(type);
	 }
	 /**
		 * A unit test suite for JUnit with benzene. 
		 * Reaction:  C1=CC=CC=C1 -> C1(C)=C(C)-C=C-C=C1
		 * Automatic search of the center active.
		 * 
		 * InChI=1/C6H6/c1-2-4-6-5-3-1/h1-6H
		 *
		 * @return    The test suite
		 */
		@Test public void testInitiate_IMoleculeSet_IMoleculeSet() throws Exception {
	        IReactionProcess type = new PiBondingMovementReaction();
			// C1=C(C)-C(C)=C-C=C1
			IMolecule molecule = builder.newMolecule();
			molecule.addAtom(builder.newAtom("C"));
			molecule.addAtom(builder.newAtom("C"));
			molecule.addBond(0, 1, IBond.Order.DOUBLE);
			molecule.addAtom(builder.newAtom("C"));
			molecule.addBond(1, 2, IBond.Order.SINGLE);
			molecule.addAtom(builder.newAtom("C"));
			molecule.addBond(2, 3, IBond.Order.DOUBLE);
			molecule.addAtom(builder.newAtom("C"));
			molecule.addBond(3, 4, IBond.Order.SINGLE);
			molecule.addAtom(builder.newAtom("C"));
			molecule.addBond(4, 5, IBond.Order.DOUBLE);
			molecule.addBond(5, 0, IBond.Order.SINGLE);
			
			addExplicitHydrogens(molecule);
			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
			makeSureAtomTypesAreRecognized(molecule);
	        
			IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
			setOfReactants.addMolecule(molecule);

			/* initiate */
			HashMap<String,Object> params = new HashMap<String,Object>();
			params.put("hasActiveCenter",Boolean.FALSE);
			type.setParameters(params);
	        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
	        
	        Assert.assertEquals(1, setOfReactions.getReactionCount());
	        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());
	        
	        IMolecule product2 = setOfReactions.getReaction(0).getProducts().getMolecule(0);
	        
	        IQueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product2);
	        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule,queryAtom));
	       
		}
	/**
	 * A unit test suite for JUnit with 1,2-dimethylbenzene. 
	 * Reaction: C1=C(C)-C(C)=C-C=C1 -> C1(C)=C(C)-C=C-C=C1
	 * Automatic search of the center active.
	 * 
	 * InChI=1/C8H10/c1-7-5-3-4-6-8(7)2/h3-6H,1-2H3
	 *
	 * @return    The test suite
	 */
	@Test public void testAutomaticSearchCentreActiveExample1() throws Exception {
        IReactionProcess type = new PiBondingMovementReaction();
		// C1=C(C)-C(C)=C-C=C1
		IMolecule molecule = builder.newMolecule();
		molecule.addAtom(builder.newAtom("C"));
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(0, 1, IBond.Order.DOUBLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(1, 2, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(1, 3, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(3, 4, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(3, 5, IBond.Order.DOUBLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(5, 6, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(6, 7, IBond.Order.DOUBLE);
		molecule.addBond(7, 0, IBond.Order.SINGLE);
		
		addExplicitHydrogens(molecule);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
		makeSureAtomTypesAreRecognized(molecule);
        
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		setOfReactants.addMolecule(molecule);

		/* initiate */
		HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.FALSE);
		
        type.setParameters(params);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        
        IMolecule product2 = setOfReactions.getReaction(0).getProducts().getMolecule(0);
		
        //C1(C)=C(C)-C=C-C=C1
        IMolecule molecule2 = builder.newMolecule();
		molecule2.addAtom(builder.newAtom("C"));
		molecule2.addAtom(builder.newAtom("C"));
		molecule2.addBond(0, 1, IBond.Order.SINGLE);
		molecule2.addAtom(builder.newAtom("C"));
		molecule2.addBond(0, 2, IBond.Order.DOUBLE);
		molecule2.addAtom(builder.newAtom("C"));
		molecule2.addBond(2, 3, IBond.Order.SINGLE);
		molecule2.addAtom(builder.newAtom("C"));
		molecule2.addBond(2, 4, IBond.Order.SINGLE);
		molecule2.addAtom(builder.newAtom("C"));
		molecule2.addBond(4, 5, IBond.Order.DOUBLE);
		molecule2.addAtom(builder.newAtom("C"));
		molecule2.addBond(5, 6, IBond.Order.SINGLE);
		molecule2.addAtom(builder.newAtom("C"));
		molecule2.addBond(6, 7, IBond.Order.DOUBLE);
		molecule2.addBond(7, 0, IBond.Order.SINGLE);
		
		addExplicitHydrogens(molecule2);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule2);
		makeSureAtomTypesAreRecognized(molecule2);
		
		IQueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product2);
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule2,queryAtom));
       
	}
	/**
	 * A unit test suite for JUnit with 2-methylnaphthalene. 
	 * Reaction: C1=CC(=CC2=C1C=CC=C2)C 
	 * -> C1=CC(=CC2=CC=CC=C12)C + C1=C2C(=CC(=C1)C)C=CC=C2
	 * Automatic search of the center active.
	 *
	 * InChI=1/C11H10/c1-9-6-7-10-4-2-3-5-11(10)8-9/h2-8H,1H3
	 * 
	 * @return    The test suite
	 */
	@Test public void testDoubleRingConjugated() throws Exception {
        IReactionProcess type = new PiBondingMovementReaction();
        // C1=CC(=CC2=C1C=CC=C2)C 
        IMolecule molecule = getmethyilnaphthalene1();
		
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		setOfReactants.addMolecule(molecule);

		/* initiate */
		HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.FALSE);
		type.setParameters(params);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        Assert.assertEquals(2, setOfReactions.getReactionCount());
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        IMolecule product1 = setOfReactions.getReaction(0).getProducts().getMolecule(0);
        IMolecule molecule1 = getmethyilnaphthalene2();

		IQueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product1);
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule1,queryAtom));

        Assert.assertEquals(1, setOfReactions.getReaction(1).getProductCount());

        IMolecule product2 = setOfReactions.getReaction(1).getProducts().getMolecule(0);
        //C1=CC(=CC2=CC=CC=C12)C
        IMolecule molecule2 = builder.newMolecule();
		molecule2.addAtom(builder.newAtom("C"));
		molecule2.addAtom(builder.newAtom("C"));
		molecule2.addBond(0, 1, IBond.Order.SINGLE);
		molecule2.addAtom(builder.newAtom("C"));
		molecule2.addBond(1, 2, IBond.Order.SINGLE);
		molecule2.addAtom(builder.newAtom("C"));
		molecule2.addBond(2, 3, IBond.Order.DOUBLE);
		molecule2.addAtom(builder.newAtom("C"));
		molecule2.addBond(3, 4, IBond.Order.SINGLE);
		molecule2.addAtom(builder.newAtom("C"));
		molecule2.addBond(4, 5, IBond.Order.DOUBLE);
		molecule2.addAtom(builder.newAtom("C"));
		molecule2.addBond(5, 6, IBond.Order.SINGLE);
		molecule2.addAtom(builder.newAtom("C"));
		molecule2.addBond(6, 7, IBond.Order.DOUBLE);
		molecule2.addAtom(builder.newAtom("C"));
		molecule2.addBond(7, 8, IBond.Order.SINGLE);
		molecule2.addAtom(builder.newAtom("C"));
		molecule2.addBond(8, 9, IBond.Order.DOUBLE);
		molecule2.addAtom(builder.newAtom("C"));
		molecule2.addBond(9, 10, IBond.Order.SINGLE);
		molecule2.addBond(10, 1, IBond.Order.DOUBLE);
		molecule2.addBond(9, 4, IBond.Order.SINGLE);
		
		addExplicitHydrogens(molecule2);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule2);
		makeSureAtomTypesAreRecognized(molecule2);
		
		queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product2);
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule2,queryAtom));
       
	}
	/**
	 * A unit test suite for JUnit with 2-methylnaphthalene. 
	 * Reaction: C1=CC(=CC2=C1C=CC=C2)C 
	 * -> C1=CC(=CC2=CC=CC=C12)C + {NO => C1=C2C(=CC(=C1)C)C=CC=C2}
	 * 
	 * restricted the reaction center.
	 *
	 * InChI=1/C11H10/c1-9-6-7-10-4-2-3-5-11(10)8-9/h2-8H,1H3
	 * 
	 * @return    The test suite
	 */
	@Test public void testDoubleRingConjugated2() throws Exception {
        IReactionProcess type = new PiBondingMovementReaction();
        // C1=CC(=CC2=C1C=CC=C2)C 
        IMolecule molecule = getmethyilnaphthalene1();
		/*manually putting the reaction center*/
		molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(2).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(3).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(9).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(10).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(11).setFlag(CDKConstants.REACTIVE_CENTER,true);
		
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		setOfReactants.addMolecule(molecule);

		/* initiate */
        HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);
		type.setParameters(params);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        IMolecule product2 = setOfReactions.getReaction(0).getProducts().getMolecule(0);
        
        IMolecule molecule2 = getmethyilnaphthalene2();
        
        IQueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product2);
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule2,queryAtom));

	}
	/**
	 * Create one of the resonance for 2-methylnaphthalene.
	 * C1=CC(=CC2=C1C=CC=C2)C 
	 * 
	 * @return The IMolecule
	 * @throws Exception
	 */
	private IMolecule getmethyilnaphthalene1() throws Exception {
		// C{0}1=C{1}C{2}(=C{3}C{4}2=C{5}1C{6}=C{7}C{8}=C{9}2)C{10}
        // C1=CC(=CC2=C1C=CC=C2)C 
		IMolecule molecule = builder.newMolecule();
		molecule.addAtom(builder.newAtom("C"));
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(0, 1, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(1, 2, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(2, 3, IBond.Order.DOUBLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(3, 4, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(4, 5, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(5, 6, IBond.Order.DOUBLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(6, 7, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(7, 8, IBond.Order.DOUBLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(8, 9, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(9, 10, IBond.Order.SINGLE);
		molecule.addBond(10, 1, IBond.Order.DOUBLE);
		molecule.addBond(9, 4, IBond.Order.DOUBLE);
		
		addExplicitHydrogens(molecule);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
		makeSureAtomTypesAreRecognized(molecule);
		return molecule;
	}
	
	/**
	 * Create one of the resonance for 2-methylnaphthalene.
	 * C=1C=CC2=CC(=CC=C2(C=1))C
	 * 
	 * @return The IMolecule
	 * @throws Exception
	 */
	private IMolecule getmethyilnaphthalene2() throws Exception {

        //C=1C=CC2=CC(=CC=C2(C=1))C
		IMolecule molecule = builder.newMolecule();
		molecule.addAtom(builder.newAtom("C"));
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(0, 1, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(1, 2, IBond.Order.DOUBLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(2, 3, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(3, 4, IBond.Order.DOUBLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(4, 5, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(5, 6, IBond.Order.DOUBLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(6, 7, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(7, 8, IBond.Order.DOUBLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(8, 9, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(9, 10, IBond.Order.DOUBLE);
		molecule.addBond(10, 1, IBond.Order.SINGLE);
		molecule.addBond(9, 4, IBond.Order.SINGLE);
		
		addExplicitHydrogens(molecule);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
		makeSureAtomTypesAreRecognized(molecule);
		return molecule;
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
