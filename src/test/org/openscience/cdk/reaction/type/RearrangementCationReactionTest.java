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
import org.openscience.cdk.reaction.type.RearrangementCationReaction;
import org.openscience.cdk.reaction.ReactionProcessTest;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;


/**
 * TestSuite that runs a test for the RearrangementCationReactionTest.
 * Generalized Reaction: [A+]-B=C => A=B-[c+].
 *
 * @cdk.module test-reaction
 */
public class RearrangementCationReactionTest extends ReactionProcessTest {

	private final static  IChemObjectBuilder builder = NoNotificationChemObjectBuilder.getInstance();

	/**
	 *  The JUnit setup method
	 */
	@BeforeClass public static void setUp() throws Exception {
	 	setReaction(RearrangementCationReaction.class);
	}
	 
	 /**
	  *  The JUnit setup method
	  */
	 @Test public void testRearrangementCationReaction() throws Exception {
			IReactionProcess type = new RearrangementCationReaction();
			Assert.assertNotNull(type);
	 }
	
	/**
	 * A unit test suite for JUnit. Reaction: [C+]-C=C-C => C=C-[C+]-C
	 * Automatic search of the center active.
	 *
	 * @return    The test suite
	 */
	@Test public void testInitiate_IMoleculeSet_IMoleculeSet() throws Exception {
		IReactionProcess type = new RearrangementCationReaction();
		
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		/*[C+]-C=C-C*/
		IMolecule molecule = getMolecule1();
		setOfReactants.addMolecule(molecule);

		/* initiate */
		
		HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.FALSE);;
        type.setParameters(params);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        
        IMolecule product = setOfReactions.getReaction(0).getProducts().getMolecule(0);
        Assert.assertEquals(1, product.getAtom(2).getFormalCharge().intValue());
        Assert.assertEquals(0, product.getConnectedLonePairsCount(molecule.getAtom(1)));
        
        /*C=C-[C+]-C*/
        IMolecule molecule2 = getMolecule2();
        IQueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product);
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule2,queryAtom));
        
        
       
	}
	/**
	 * A unit test suite for JUnit. Reaction: [C+]-C=C-C => C=C-[C+]-C
	 * Manually put of the center active.
	 *
	 * @return    The test suite
	 */
	@Test public void testManuallyCentreActive() throws Exception {
		IReactionProcess type = new RearrangementCationReaction();
		
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		/*[C+]-C=C-C*/
		IMolecule molecule = getMolecule1();
		setOfReactants.addMolecule(molecule);
		
		/*manually put the center active*/
		molecule.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER,true);

		
        HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
        type.setParameters(params);
        
		/* initiate */
		
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        IMolecule product = setOfReactions.getReaction(0).getProducts().getMolecule(0);
        
        /*C=C-[C+]-C*/
        IMolecule molecule2 = getMolecule2();
        
        IQueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product);
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule2,queryAtom));
        
	}
	/**
	 * A unit test suite for JUnit. 
	 * 
	 * @return    The test suite
	 */
	@Test public void testCentreActive() throws Exception {
		IReactionProcess type  = new RearrangementCationReaction();

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
		IReactionProcess type  = new RearrangementCationReaction();
		IMoleculeSet setOfReactants = builder.newMoleculeSet();

		IMolecule molecule = getMolecule1();
		
		/*manually put the reactive center*/
		molecule.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
		
		setOfReactants.addMolecule(molecule);
		HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
        type.setParameters(params);
        
        /* initiate */
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        IMolecule reactant = setOfReactions.getReaction(0).getReactants().getMolecule(0);
		Assert.assertTrue(molecule.getAtom(0).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(reactant.getAtom(0).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(molecule.getAtom(1).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(reactant.getAtom(1).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(molecule.getAtom(2).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(reactant.getAtom(2).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(molecule.getBond(0).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(reactant.getBond(0).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(molecule.getBond(1).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(reactant.getBond(1).getFlag(CDKConstants.REACTIVE_CENTER));
	}

	/**
	 * A unit test suite for JUnit.
	 *  
	 * @return    The test suite
	 */
	@Test public void testMapping() throws Exception {
		IReactionProcess type = new RearrangementCationReaction();
		
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		/*[C+]-C=C-C*/
		IMolecule molecule = getMolecule1();
		setOfReactants.addMolecule(molecule);
		
		/*automatic search of the center active*/
        HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.FALSE);;
        type.setParameters(params);
        
		/* initiate */
		
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        IMolecule product = setOfReactions.getReaction(0).getProducts().getMolecule(0);

        Assert.assertEquals(5,setOfReactions.getReaction(0).getMappingCount());
        
        IAtom mappedProductA1 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtom(0));
        Assert.assertEquals(mappedProductA1, product.getAtom(0));
        IBond mappedProductB1 = (IBond)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getBond(0));
        Assert.assertEquals(mappedProductB1, product.getBond(0));
        mappedProductA1 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtom(1));
        Assert.assertEquals(mappedProductA1, product.getAtom(1));
        mappedProductB1 = (IBond)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getBond(1));
        Assert.assertEquals(mappedProductB1, product.getBond(1));
        mappedProductA1 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtom(2));
        Assert.assertEquals(mappedProductA1, product.getAtom(2)); 
	}
	/**
	 * Test to recognize if this IMolecule_1 matches correctly into the CDKAtomTypes.
	 * @throws Exception 
	 * @throws ClassNotFoundException 
	 * @throws CDKException 
	 */
	@Test public void testAtomTypesMolecule1() throws CDKException, ClassNotFoundException, Exception{
		IMolecule moleculeTest = getMolecule1();
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(moleculeTest);
		makeSureAtomTypesAreRecognized(moleculeTest);
		
	}

	/**
	 * Test to recognize if this IMolecule_2 matches correctly into the CDKAtomTypes.
	 * @throws Exception 
	 * @throws ClassNotFoundException 
	 * @throws CDKException 
	 */
	@Test public void testAtomTypesMolecule2() throws CDKException, ClassNotFoundException, Exception{
		IMolecule moleculeTest = getMolecule2();
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(moleculeTest);
		makeSureAtomTypesAreRecognized(moleculeTest);
		
	}
	/**
	 * get the molecule 1: [C+]-C=C-C
	 * 
	 * @return The IMolecule
	 */
	private IMolecule getMolecule1()throws Exception {
		IMolecule molecule = builder.newMolecule();
		molecule.addAtom(builder.newAtom("C"));
		molecule.getAtom(0).setFormalCharge(1);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(0, 1, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(1, 2, IBond.Order.DOUBLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(2, 3, IBond.Order.SINGLE);
		addExplicitHydrogens(molecule);
		
        return molecule;
	}
	/**
	 * get the molecule 2: C=C-[C+]-C
	 * 
	 * @return The IMolecule
	 */
	private IMolecule getMolecule2()throws Exception {
		IMolecule molecule = builder.newMolecule();
		molecule.addAtom(builder.newAtom("C"));
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(0, 1, IBond.Order.DOUBLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.getAtom(2).setFormalCharge(1);
		molecule.addBond(1, 2, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(2, 3, IBond.Order.SINGLE);
		addExplicitHydrogens(molecule);
        return molecule;
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
