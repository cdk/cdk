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
import org.openscience.cdk.Atom;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
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
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.type.RadicalSiteInitiationHReaction;
import org.openscience.cdk.reaction.ReactionProcessTest;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;

/**
 * TestSuite that runs a test for the RadicalSiteInitiationHReactionTest.
 * Generalized Reaction: [A*]-B-H => A=B + [H*].
 *
 * @cdk.module test-reaction
 */
public class RadicalSiteInitiationHReactionTest extends ReactionProcessTest {

	private final static  IChemObjectBuilder builder = NoNotificationChemObjectBuilder.getInstance();
	/**
	 *  The JUnit setup method
	 */
	 @BeforeClass public static void setUp() throws Exception {
	 	setReaction(RadicalSiteInitiationHReaction.class);
	 }
	 
	 /**
	  *  The JUnit setup method
	  */
	 @Test public void testRadicalSiteInitiationHReaction() throws Exception {
			IReactionProcess type = new RadicalSiteInitiationHReaction();
			Assert.assertNotNull(type);
	 }
	/**
	 * A unit test suite for JUnit. Reaction: [C*]([H])([H])C([H])([H])[H] => C=C +[H*]
	 * Automatic search of the center active.
	 *
	 * @return    The test suite
	 */
	@Test public void testInitiate_IMoleculeSet_IMoleculeSet() throws Exception {
		IReactionProcess type = new RadicalSiteInitiationHReaction();
		
		/*[C*]-C-C*/
		IMolecule molecule = getMolecule();
		
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		setOfReactants.addMolecule(molecule);

		/* initiate */
		makeSureAtomTypesAreRecognized(molecule);
		
        HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.FALSE);;
        type.setParameters(params);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        Assert.assertEquals(3, setOfReactions.getReactionCount());
        Assert.assertEquals(2, setOfReactions.getReaction(0).getProductCount());

        
        IMolecule product1 = setOfReactions.getReaction(0).getProducts().getMolecule(0);
		
        /*C=C*/
        IMolecule molecule1 = getMolecule1();

        IQueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product1);
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule1,queryAtom));
		
		IMolecule product2 = setOfReactions.getReaction(0).getProducts().getMolecule(1);
		
        /*[H*]*/
		IMolecule molecule2 = builder.newMolecule();
		molecule2.addAtom(new Atom("H"));
        molecule2.addSingleElectron(new SingleElectron(molecule2.getAtom(0)));

        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product2);
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule2,queryAtom));
       
	}
	
	/**
	 * A unit test suite for JUnit. Reaction: [C*]([H])([H])C([H])([H])[H] => C=C +[H*]
	 * Automatic search of the center active.
	 *
	 * @return    The test suite
	 */
	@Test public void testManuallyCentreActive() throws Exception {
		IReactionProcess type = new RadicalSiteInitiationHReaction();
		
		/*[C*]-C-C*/
		IMolecule molecule = getMolecule();
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		setOfReactants.addMolecule(molecule);

		/* initiate */
		makeSureAtomTypesAreRecognized(molecule);

		/*manually put the reactive center*/
		molecule.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(3).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(4).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(2).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(3).setFlag(CDKConstants.REACTIVE_CENTER,true);
		
        HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
        type.setParameters(params);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(2, setOfReactions.getReaction(0).getProductCount());
        
        IMolecule product1 = setOfReactions.getReaction(0).getProducts().getMolecule(0);
		
        /*C=C*/
        IMolecule molecule1 = getMolecule1();

        IQueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product1);
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule1,queryAtom));
		
		IMolecule product2 = setOfReactions.getReaction(0).getProducts().getMolecule(1);
		
        /*[H*]*/
		IMolecule molecule2 = builder.newMolecule();
		molecule2.addAtom(new Atom("H"));
        molecule2.addSingleElectron(new SingleElectron(molecule2.getAtom(0)));

        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product2);
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule2,queryAtom));
       
	}
	/**
	 * A unit test suite for JUnit. 
	 * 
	 * @return    The test suite
	 */
	@Test public void testCentreActive() throws Exception {
		IReactionProcess type  = new RadicalSiteInitiationHReaction();

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
IReactionProcess type = new RadicalSiteInitiationHReaction();
		
		/*[C*]-C-C*/
		IMolecule molecule = getMolecule();
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		setOfReactants.addMolecule(molecule);

		/* initiate */
		makeSureAtomTypesAreRecognized(molecule);

		/*manually put the reactive center*/
		molecule.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(3).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(4).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(2).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(3).setFlag(CDKConstants.REACTIVE_CENTER,true);
		
        HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
        type.setParameters(params);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        IMolecule reactant = setOfReactions.getReaction(0).getReactants().getMolecule(0);
		Assert.assertTrue(molecule.getAtom(0).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(reactant.getAtom(0).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(molecule.getAtom(3).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(reactant.getAtom(3).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(molecule.getAtom(4).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(reactant.getAtom(4).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(molecule.getBond(2).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(reactant.getBond(2).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(molecule.getBond(3).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(reactant.getBond(3).getFlag(CDKConstants.REACTIVE_CENTER));
	}
	/**
	 * A unit test suite for JUnit.
	 *  
	 * @return    The test suite
	 */
	@Test public void testMapping() throws Exception {
IReactionProcess type = new RadicalSiteInitiationHReaction();
		
		/*[C*]-C-C*/
		IMolecule molecule = getMolecule();
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		setOfReactants.addMolecule(molecule);

		/* initiate */
		makeSureAtomTypesAreRecognized(molecule);

		/*manually put the reactive center*/
		molecule.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(3).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(4).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(2).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(3).setFlag(CDKConstants.REACTIVE_CENTER,true);
		
        HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
        type.setParameters(params);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        IMolecule product = setOfReactions.getReaction(0).getProducts().getMolecule(0);

        Assert.assertEquals(4,setOfReactions.getReaction(0).getMappingCount());
        
		Assert.assertEquals(4,setOfReactions.getReaction(0).getMappingCount());
		IAtom mappedProductA1 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtom(0));
		Assert.assertEquals(mappedProductA1, product.getAtom(0));
        IBond mappedProductB1 = (IBond)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getBond(2));
        Assert.assertEquals(mappedProductB1, product.getBond(2));
        IAtom mappedProductA2 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtom(0));
        Assert.assertEquals(mappedProductA2, product.getAtom(0));
        IAtom mappedProductA3 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtom(3));
        Assert.assertEquals(mappedProductA3, product.getAtom(3));
		
	}
	/**
	 * Get the Molecule 
	 * 
	 * @return The IMolecule
	 */
	private IMolecule getMolecule() {
		IMolecule molecule = builder.newMolecule();
		molecule.addAtom(builder.newAtom("C"));
		molecule.addAtom(builder.newAtom("H"));
		molecule.addBond(0, 1, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("H"));
		molecule.addBond(0, 2, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(0, 3, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("H"));
		molecule.addBond(3, 4, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("H"));
		molecule.addBond(3, 5, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("H"));
		molecule.addBond(3, 6, IBond.Order.SINGLE);
		
		IAtom atom =  molecule.getAtom(0);
        molecule.addSingleElectron(new SingleElectron(atom));
        return molecule;
	}
	/**
	 * Get the Molecule 1
	 * @return The IMolecule
	 */
	private IMolecule getMolecule1() {
		IMolecule molecule1 = builder.newMolecule();
        molecule1.addAtom(builder.newAtom("C"));
        molecule1.addAtom(builder.newAtom("H"));
        molecule1.addBond(0, 1, IBond.Order.SINGLE);
        molecule1.addAtom(builder.newAtom("H"));
        molecule1.addBond(0, 2, IBond.Order.SINGLE);
        molecule1.addAtom(builder.newAtom("C"));
        molecule1.addBond(0, 3, IBond.Order.DOUBLE);
        molecule1.addAtom(builder.newAtom("H"));
        molecule1.addBond(3, 4, IBond.Order.SINGLE);
        molecule1.addAtom(builder.newAtom("H"));
        molecule1.addBond(3, 5, IBond.Order.SINGLE);
		return molecule1;
	}
	
	/**
	 * Test to recognize if a IMolecule matcher correctly the CDKAtomTypes.
	 * 
	 * @param molecule          The IMolecule to analyze
	 * @throws CDKException
	 */
	private void makeSureAtomTypesAreRecognized(IMolecule molecule) throws CDKException {

		Iterator<IAtom> atoms = molecule.atoms().iterator();
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
