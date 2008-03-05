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
import org.openscience.cdk.reaction.type.RadicalSiteHrBetaReaction;
import org.openscience.cdk.reaction.ReactionProcessTest;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;

/**
 * TestSuite that runs a test for the RadicalSiteHrBetaReaction.
 *
 * @cdk.module test-reaction
 */
public class RadicalSiteHrBetaReactionTest extends ReactionProcessTest {

	private final static  IChemObjectBuilder builder = NoNotificationChemObjectBuilder.getInstance();
	/**
	 *  The JUnit setup method
	 */
	 @BeforeClass public static void setUp() throws Exception {
	 	setReaction(RadicalSiteHrBetaReaction.class);
	 }
	/**
	 * A unit test suite for JUnit. Reaction: 
	 *
	 * @return    The test suite
	 */
	@Test public void testAutomaticSearchCentreActiveExample1() throws Exception {
		IReactionProcess type = new RadicalSiteHrBetaReaction();
		
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
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        IMolecule product = setOfReactions.getReaction(0).getProducts().getMolecule(0);
        IMolecule molecule2 = getProduct1();
        
        IQueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product);
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule2,queryAtom));
        
	}
	/**
	 * create the compound
	 * 
	 * @return The IMolecule
	 * @throws Exception
	 */
	private IMolecule getMolecule() throws Exception {

		IMolecule molecule = builder.newMolecule();
		molecule.addAtom(builder.newAtom("C"));
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(0, 1, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(1, 2, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.getAtom(3).setFormalCharge(1);
		molecule.addBond(2, 3, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(3, 4, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(4, 5, IBond.Order.SINGLE);
		addExplicitHydrogens(molecule);

		molecule.getAtom(3).setFormalCharge(0);
		molecule.addSingleElectron(new SingleElectron(molecule.getAtom(3)));
		
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
		makeSureAtomTypesAreRecognized(molecule);
		return molecule;
	}
	/**
	 * create the compound
	 * 
	 * @return The IMolecule
	 * @throws Exception 
	 */
	private IMolecule getProduct1() throws Exception {
		IMolecule molecule2 = builder.newMolecule();
		molecule2.addAtom(builder.newAtom("C"));
		molecule2.getAtom(0).setFormalCharge(1);
		molecule2.addAtom(builder.newAtom("C"));
		molecule2.addBond(0, 1, IBond.Order.SINGLE);
		molecule2.addAtom(builder.newAtom("C"));
		molecule2.addBond(1, 2, IBond.Order.SINGLE);
		molecule2.addAtom(builder.newAtom("C"));
		molecule2.addBond(2, 3, IBond.Order.SINGLE);
		molecule2.addAtom(builder.newAtom("C"));
		molecule2.addBond(3, 4, IBond.Order.SINGLE);
		molecule2.addAtom(builder.newAtom("C"));
		molecule2.addBond(4, 5, IBond.Order.SINGLE);
		addExplicitHydrogens(molecule2);

		molecule2.getAtom(0).setFormalCharge(0);
        molecule2.addSingleElectron(new SingleElectron(molecule2.getAtom(0)));
        
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule2);
		makeSureAtomTypesAreRecognized(molecule2);
		return molecule2;
	}
	/**
	 * A unit test suite for JUnit. 
	 * 
	 * @return    The test suite
	 */
	@Test public void testCentreActive() throws Exception {
		IReactionProcess type  = new RadicalSiteHrBetaReaction();

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
		IReactionProcess type  = new RadicalSiteHrBetaReaction();
		IMoleculeSet setOfReactants = builder.newMoleculeSet();

		IMolecule molecule = getMolecule();
		
		/*manually put the reactive center*/
		molecule.getAtom(3).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(6).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(5).setFlag(CDKConstants.REACTIVE_CENTER,true);
		
		setOfReactants.addMolecule(molecule);
		HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
        type.setParameters(params);
        
        /* initiate */
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        IMolecule reactant = setOfReactions.getReaction(0).getReactants().getMolecule(0);
		Assert.assertTrue(molecule.getAtom(6).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(reactant.getAtom(6).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(molecule.getAtom(0).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(reactant.getAtom(0).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(molecule.getAtom(3).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(reactant.getAtom(3).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(molecule.getBond(5).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(reactant.getBond(5).getFlag(CDKConstants.REACTIVE_CENTER));
	}

	/**
	 * A unit test suite for JUnit.
	 *  
	 * @return    The test suite
	 */
	@Test public void testMapping() throws Exception {
		IReactionProcess type  = new RadicalSiteHrBetaReaction();
		IMoleculeSet setOfReactants = builder.newMoleculeSet();

		IMolecule molecule = getMolecule();
		
		setOfReactants.addMolecule(molecule);
		
		/*automatic search of the center active*/
        HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.FALSE);;
        type.setParameters(params);
        
        /* initiate */
		makeSureAtomTypesAreRecognized(molecule);
		
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        IMolecule product = setOfReactions.getReaction(0).getProducts().getMolecule(0);

        Assert.assertEquals(4,setOfReactions.getReaction(0).getMappingCount());
        IAtom mappedProductA1 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtom(0));
        Assert.assertEquals(mappedProductA1, product.getAtom(0));
        IAtom mappedProductA2 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtom(6));
        Assert.assertEquals(mappedProductA2, product.getAtom(6));
        IAtom mappedProductA3 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtom(3));
        Assert.assertEquals(mappedProductA3, product.getAtom(3));
        IBond mappedProductB1 = (IBond)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getBond(5));
        Assert.assertEquals(mappedProductB1, product.getBond(17));        
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
