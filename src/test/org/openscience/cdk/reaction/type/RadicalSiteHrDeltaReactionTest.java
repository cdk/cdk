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
import org.openscience.cdk.reaction.type.HomolyticCleavageReaction;
import org.openscience.cdk.reaction.type.RadicalSiteHrDeltaReaction;
import org.openscience.cdk.reaction.ReactionProcessTest;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;

/**
 * TestSuite that runs a test for the RadicalSiteHrDeltaReactionTest.
 * Generalized Reaction: [A*]-(C)_4-C5[H] => A([H])-(C_4)-[C5*].
 *
 * @cdk.module test-reaction
 */
public class RadicalSiteHrDeltaReactionTest extends ReactionProcessTest {

	private final static  IChemObjectBuilder builder = NoNotificationChemObjectBuilder.getInstance();
	/**
	 *  The JUnit setup method
	 */
	 @BeforeClass public static void setUp() throws Exception {
	 	setReaction(RadicalSiteHrDeltaReaction.class);
	 }
	/**
	 * A unit test suite for JUnit. Reaction: C([H])([H])([H])C([H])([H])C(=O)C([H])([H])C([H])C([H])[H]
	 * Automatic search of the center active. hexan-3-one
	 *           
	 * @cdk.inchi InChI=1/C6H12O/c1-3-5-6(7)4-2/h3-5H2,1-2H3
	 *
	 * @return    The test suite
	 */
	@Test public void testAutomaticSearchCentreActive() throws Exception {
		IReactionProcess type = new RadicalSiteHrDeltaReaction();
	
		IMolecule molecule = getHexan3one();
		
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		setOfReactants.addMolecule(molecule);

		/* initiate */
		
        Object[] params = {Boolean.FALSE};
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
	 * A unit test suite for JUnit. Reaction: C([H])([H])([H])C([H])([H])C(=O)C([H])([H])C([H])C([H])[H]
	 * Automatic search of the center active. hexan-3-one
	 *           
	 * @cdk.inchi InChI=1/C6H12O/c1-3-5-6(7)4-2/h3-5H2,1-2H3
	 *
	 * @return    The test suite
	 */
	@Test public void testManuallyCentreActive() throws Exception {
		IReactionProcess type = new RadicalSiteHrDeltaReaction();
	
		IMolecule molecule = getHexan3one();
		
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		setOfReactants.addMolecule(molecule);

		molecule.getAtom(6).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(7).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(6).setFlag(CDKConstants.REACTIVE_CENTER,true);
		
        Object[] params = {Boolean.TRUE};
        type.setParameters(params);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());
       
        IMolecule product = setOfReactions.getReaction(0).getProducts().getMolecule(0);
        IMolecule molecule2 = getProduct1();
        
        IQueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product);
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule2,queryAtom));
	}
	/**
	 * create the compound Hexan-3-one.
	 * 
	 * @cdk.inchi InChI=1/C6H12O/c1-3-5-6(7)4-2/h3-5H2,1-2H3
	 * 
	 * @return The IMolecule
	 * @throws Exception
	 */
	private IMolecule getHexan3one() throws Exception {
		IMolecule molecule = builder.newMolecule();
		molecule.addAtom(builder.newAtom("C"));
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(0, 1, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(1, 2, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("O"));
		molecule.addBond(2, 3, IBond.Order.DOUBLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(2, 4, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(4, 5, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.getAtom(6).setFormalCharge(1);
		molecule.addBond(5, 6, IBond.Order.SINGLE);
		addExplicitHydrogens(molecule);
		molecule.getAtom(6).setFormalCharge(0);
		molecule.addSingleElectron(new SingleElectron(molecule.getAtom(6)));
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
		molecule2.addAtom(builder.newAtom("O"));
		molecule2.addBond(2, 3, IBond.Order.DOUBLE);
		molecule2.addAtom(builder.newAtom("C"));
		molecule2.addBond(2, 4, IBond.Order.SINGLE);
		molecule2.addAtom(builder.newAtom("C"));
		molecule2.addBond(4, 5, IBond.Order.SINGLE);
		molecule2.addAtom(builder.newAtom("C"));
		molecule2.addBond(5, 6, IBond.Order.SINGLE);
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
		IReactionProcess type  = new HomolyticCleavageReaction();

		Object[] object = type.getParameters();
		Assert.assertFalse(((Boolean) object[0]).booleanValue());
		 
		Object[] params = {Boolean.TRUE};
        type.setParameters(params);
		Assert.assertTrue(((Boolean) params[0]).booleanValue());
        
	}
	/**
	 * A unit test suite for JUnit.
	 * 
	 * @return    The test suite
	 */
	@Test public void testCDKConstants_REACTIVE_CENTER() throws Exception {
		IReactionProcess type  = new RadicalSiteHrDeltaReaction();
		IMoleculeSet setOfReactants = builder.newMoleculeSet();

		IMolecule molecule = getHexan3one();
		
		/*manually put the reactive center*/
		molecule.getAtom(6).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(7).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(6).setFlag(CDKConstants.REACTIVE_CENTER,true);
		
		setOfReactants.addMolecule(molecule);
		Object[] params = {Boolean.TRUE};
        type.setParameters(params);
        
        /* initiate */
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        IMolecule reactant = setOfReactions.getReaction(0).getReactants().getMolecule(0);
		Assert.assertTrue(molecule.getAtom(6).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(reactant.getAtom(6).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(molecule.getAtom(0).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(reactant.getAtom(0).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(molecule.getAtom(7).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(reactant.getAtom(7).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(molecule.getBond(6).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(reactant.getBond(6).getFlag(CDKConstants.REACTIVE_CENTER));
	}

	/**
	 * A unit test suite for JUnit.
	 *  
	 * @return    The test suite
	 */
	@Test public void testMapping() throws Exception {
		IReactionProcess type  = new RadicalSiteHrDeltaReaction();
		IMoleculeSet setOfReactants = builder.newMoleculeSet();

		IMolecule molecule = getHexan3one();
		
		setOfReactants.addMolecule(molecule);
		
		/*automatic search of the center active*/
        Object[] params = {Boolean.FALSE};
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
        IAtom mappedProductA3 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtom(7));
        Assert.assertEquals(mappedProductA3, product.getAtom(7));
        IBond mappedProductB1 = (IBond)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getBond(6));
        Assert.assertEquals(mappedProductB1, product.getBond(16));        
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
