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
import org.openscience.cdk.reaction.ReactionProcessTest;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;

/**
 * TestSuite that runs a test for the RadicalSiteInitiationHReactionTest.
 * Generalized Reaction: [A*+]-B-H => [A+]=B + [H*].
 *
 * @cdk.module test-reaction
 */
public class RadicalChargeSiteInitiationHReactionTest extends ReactionProcessTest {

	private final static  IChemObjectBuilder builder = NoNotificationChemObjectBuilder.getInstance();
	/**
	 *  The JUnit setup method
	 */
	 @BeforeClass public static void setUp() throws Exception {
	 	setReaction(RadicalChargeSiteInitiationHReaction.class);
	 }
	/**
	 * A unit test suite for JUnit. Reaction: [O+*]C([H])([H])C([H])([H])([H]) => [O+]=C([H])C([H])([H])([H]) +[H*]
	 * Automatic search of the center active.
	 *
	 * @return    The test suite
	 */
	@Test public void testAutomaticCentreActive() throws Exception {
		IReactionProcess type = new RadicalChargeSiteInitiationHReaction();
		
		/*[O*+]-C-C*/
		IMolecule molecule = getMolecule();
		
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		setOfReactants.addMolecule(molecule);

		/* initiate */
		makeSureAtomTypesAreRecognized(molecule);
		
        HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.FALSE);;
        type.setParameters(params);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        Assert.assertEquals(2, setOfReactions.getReactionCount());
        Assert.assertEquals(2, setOfReactions.getReaction(0).getProductCount());

        
        IMolecule product1 = setOfReactions.getReaction(0).getProducts().getMolecule(0);
		
        /*[O+]=CC*/
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
	 * A unit test suite for JUnit. Reaction: [O+*]C([H])([H])C([H])([H])([H]) => [O+]=C([H])C([H])([H])([H]) +[H*]
	 * Automatic search of the center active.
	 *
	 * @return    The test suite
	 */
	@Test public void testManuallyCentreActive() throws Exception {
		IReactionProcess type = new RadicalChargeSiteInitiationHReaction();

		/*[O*+]-C-C*/
		IMolecule molecule = getMolecule();
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		setOfReactants.addMolecule(molecule);

		/* initiate */
		makeSureAtomTypesAreRecognized(molecule);

		/*manually put the reactive center*/
		molecule.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(3).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(2).setFlag(CDKConstants.REACTIVE_CENTER,true);
		
        HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
        type.setParameters(params);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(2, setOfReactions.getReaction(0).getProductCount());
        
        IMolecule product1 = setOfReactions.getReaction(0).getProducts().getMolecule(0);

        /*[O+]=CC*/
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
		IReactionProcess type  = new RadicalChargeSiteInitiationHReaction();

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
		IReactionProcess type = new RadicalChargeSiteInitiationHReaction();

		/*[O*+]-C-C*/
		IMolecule molecule = getMolecule();
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		setOfReactants.addMolecule(molecule);

		/* initiate */
		makeSureAtomTypesAreRecognized(molecule);

		/*manually put the reactive center*/
		molecule.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(3).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(2).setFlag(CDKConstants.REACTIVE_CENTER,true);
		
        HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
        type.setParameters(params);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        IMolecule reactant = setOfReactions.getReaction(0).getReactants().getMolecule(0);
		Assert.assertTrue(molecule.getAtom(0).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(reactant.getAtom(0).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(molecule.getAtom(1).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(reactant.getAtom(1).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(molecule.getAtom(3).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(reactant.getAtom(3).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(molecule.getBond(0).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(reactant.getBond(0).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(molecule.getBond(2).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(reactant.getBond(2).getFlag(CDKConstants.REACTIVE_CENTER));
	}
	/**
	 * A unit test suite for JUnit.
	 *  
	 * @return    The test suite
	 */
	@Test public void testMapping() throws Exception {
		IReactionProcess type = new RadicalChargeSiteInitiationHReaction();

		/*[O*+]-C-C*/
		IMolecule molecule = getMolecule();
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		setOfReactants.addMolecule(molecule);

		/* initiate */
		makeSureAtomTypesAreRecognized(molecule);

		/*manually put the reactive center*/
		molecule.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(3).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(2).setFlag(CDKConstants.REACTIVE_CENTER,true);
		
        HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
        type.setParameters(params);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        IMolecule product = setOfReactions.getReaction(0).getProducts().getMolecule(0);
        IMolecule product2 = setOfReactions.getReaction(0).getProducts().getMolecule(1);

        Assert.assertEquals(4,setOfReactions.getReaction(0).getMappingCount());
        
		Assert.assertEquals(4,setOfReactions.getReaction(0).getMappingCount());
		IAtom mappedProductA1 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtom(0));
		Assert.assertEquals(mappedProductA1, product.getAtom(0));
        IBond mappedProductB1 = (IBond)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getBond(0));
        Assert.assertEquals(mappedProductB1, product.getBond(0));
        IAtom mappedProductA2 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtom(1));
        Assert.assertEquals(mappedProductA2, product.getAtom(1));
        IAtom mappedProductA3 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtom(3));
        Assert.assertEquals(mappedProductA3, product2.getAtom(0));
		
	}
	/**
	 * Get the Molecule 
	 * 
	 * @return The IMolecule
	 */
	private IMolecule getMolecule() {
		IMolecule molecule = builder.newMolecule();
		molecule.addAtom(builder.newAtom("O"));
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(0, 1, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(1, 2, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("H"));
		molecule.addBond(1, 3, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("H"));
		molecule.addBond(1, 4, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("H"));
		molecule.addBond(2, 5, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("H"));
		molecule.addBond(2, 6, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("H"));
		molecule.addBond(2, 7, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("H"));
		molecule.addBond(0, 8, IBond.Order.SINGLE);
		
		IAtom atom =  molecule.getAtom(0);
		atom.setFormalCharge(1);
        molecule.addSingleElectron(new SingleElectron(atom));
        return molecule;
	}
	/**
	 * Get the Molecule 1
	 * @return The IMolecule
	 */
	private IMolecule getMolecule1() {

		IMolecule molecule = builder.newMolecule();
		molecule.addAtom(builder.newAtom("O"));
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(0, 1, IBond.Order.DOUBLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(1, 2, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("H"));
		molecule.addBond(1, 3, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("H"));
		molecule.addBond(2, 4, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("H"));
		molecule.addBond(2, 5, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("H"));
		molecule.addBond(2, 6, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("H"));
		molecule.addBond(0, 7, IBond.Order.SINGLE);
		molecule.getAtom(0).setFormalCharge(1);
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
