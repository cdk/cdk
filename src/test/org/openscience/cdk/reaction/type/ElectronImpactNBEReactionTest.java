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
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.type.ElectronImpactNBEReaction;
import org.openscience.cdk.reaction.ReactionProcessTest;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;

/**
 * TestSuite that runs a test for the ElectronImpactNBEReactionTest.
 *
 * @cdk.module test-reaction
 */
 
public class ElectronImpactNBEReactionTest extends ReactionProcessTest {

	private final static IChemObjectBuilder builder = NoNotificationChemObjectBuilder.getInstance();
	private final static LonePairElectronChecker lpcheck = new LonePairElectronChecker();
	/**
	 *  The JUnit setup method
	 */
	 @BeforeClass public static void setUp() throws Exception {
	 	setReaction(ElectronImpactNBEReaction.class);
	 }
	 
	/**
	 *  A unit test for JUnit with the compound 2_5_Hexen_3_one.
	 *
	 * @return    Description of the Return Value
	 */
	@Test public void testManual_Set_Active_Atom() throws Exception {
		/* ionize(>C=O): C=CCC(=O)CC -> C=CCC(=O*)CC , set the reactive center*/
		
		IMolecule reactant = builder.newMolecule();//Smiles("C=CCC(=O)CC")
		reactant.addAtom(builder.newAtom("C"));
		reactant.addAtom(builder.newAtom("C"));
		reactant.addAtom(builder.newAtom("C"));
		reactant.addAtom(builder.newAtom("C"));
		reactant.addAtom(builder.newAtom("O"));
		reactant.addAtom(builder.newAtom("C"));
		reactant.addAtom(builder.newAtom("C"));
		reactant.addBond(0, 1, IBond.Order.DOUBLE);
		reactant.addBond(1, 2, IBond.Order.SINGLE);
		reactant.addBond(2, 3, IBond.Order.SINGLE);
		reactant.addBond(3, 4, IBond.Order.DOUBLE);
		reactant.addBond(3, 5, IBond.Order.SINGLE);
		reactant.addBond(5, 6, IBond.Order.SINGLE);
		addExplicitHydrogens(reactant);
	    AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(reactant);
	    lpcheck.saturate(reactant);
		
		Iterator<IAtom> atoms = reactant.atoms();
		while (atoms.hasNext()) {
			IAtom atom = (IAtom)atoms.next();
			if(reactant.getConnectedLonePairsCount(atom) > 0){
				atom.setFlag(CDKConstants.REACTIVE_CENTER,true);
			}
		}
		
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		setOfReactants.addMolecule(reactant);
		
		/* initiate */
		makeSureAtomTypesAreRecognized(reactant);
		
		IReactionProcess type  = new ElectronImpactNBEReaction();
        Object[] params = {Boolean.TRUE};
        type.setParameters(params);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        IMolecule molecule = setOfReactions.getReaction(0).getProducts().getMolecule(0);
        Assert.assertEquals(1, molecule.getAtom(4).getFormalCharge().intValue());
        Assert.assertEquals(1, molecule.getConnectedSingleElectronsCount(molecule.getAtom(4)));
        
        Assert.assertTrue(setOfReactions.getReaction(0).mappings().hasNext());
        
		
	}
	/**
	 *  A unit test for JUnit with the compound 2_5_Hexen_3_one.
	 *
	 * @return    Description of the Return Value
	 */
	@Test public void testAutomatic_Set_Active_Atom() throws Exception {
		/* ionize(>C=O): C=CCC(=O)CC -> C=CCC(=O*)CC, without setting the reactive center*/
		IMolecule reactant = builder.newMolecule();//Smiles("C=CCC(=O)CC")
		reactant.addAtom(builder.newAtom("C"));
		reactant.addAtom(builder.newAtom("C"));
		reactant.addAtom(builder.newAtom("C"));
		reactant.addAtom(builder.newAtom("C"));
		reactant.addAtom(builder.newAtom("O"));
		reactant.addAtom(builder.newAtom("C"));
		reactant.addAtom(builder.newAtom("C"));
		reactant.addBond(0, 1, IBond.Order.DOUBLE);
		reactant.addBond(1, 2, IBond.Order.SINGLE);
		reactant.addBond(2, 3, IBond.Order.SINGLE);
		reactant.addBond(3, 4, IBond.Order.DOUBLE);
		reactant.addBond(3, 5, IBond.Order.SINGLE);
		reactant.addBond(5, 6, IBond.Order.SINGLE);
		addExplicitHydrogens(reactant);
	    AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(reactant);
	    lpcheck.saturate(reactant);
		
		
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		setOfReactants.addMolecule(reactant);

		/* initiate */
		makeSureAtomTypesAreRecognized(reactant);
		
		IReactionProcess type  = new ElectronImpactNBEReaction();
		IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
		Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        IMolecule molecule = setOfReactions.getReaction(0).getProducts().getMolecule(0);
        Assert.assertEquals(1, molecule.getAtom(4).getFormalCharge().intValue());
        Assert.assertEquals(1, molecule.getConnectedSingleElectronsCount(molecule.getAtom(4)));
		
	}
	/**
	 * A unit test suite for JUnit. Reaction: methanamine.
	 * C-!N! => C[N*+]
	 *
	 * @cdk.inchi  InChI=1/CH5N/c1-2/h2H2,1H3
	 * 
	 * @return    The test suite
	 */
	@Test public void testNsp3SingleB() throws Exception {
		//Smiles("CN")
		IMolecule molecule = builder.newMolecule();
		molecule.addAtom(builder.newAtom("C"));
		molecule.addAtom(builder.newAtom("N"));
		molecule.addBond(0, 1, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("H"));
		molecule.addAtom(builder.newAtom("H"));
		molecule.addAtom(builder.newAtom("H"));
		molecule.addAtom(builder.newAtom("H"));
		molecule.addAtom(builder.newAtom("H"));
		molecule.addBond(0, 2, IBond.Order.SINGLE);
		molecule.addBond(0, 3, IBond.Order.SINGLE);
		molecule.addBond(0, 4, IBond.Order.SINGLE);
		molecule.addBond(1, 5, IBond.Order.SINGLE);
		molecule.addBond(1, 6, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
		lpcheck.saturate(molecule);
		
		molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);

       IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
       setOfReactants.addMolecule(molecule);
		
		IReactionProcess type  = new ElectronImpactNBEReaction(); 
		Object[] params = {Boolean.TRUE};
       type.setParameters(params);
       
       /* initiate */
		IReactionSet setOfReactions = type.initiate(setOfReactants, null);

       Assert.assertEquals(1, setOfReactions.getReactionCount());
       
       // expected products 
       
       //Smiles("C[N*+]")
       IMolecule expected1 = builder.newMolecule();
       expected1.addAtom(builder.newAtom("C"));
       expected1.addAtom(builder.newAtom("N"));
	   expected1.getAtom(1).setFormalCharge(+1);
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
	   expected1.addBond(1, 6, IBond.Order.SINGLE);
       AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
       lpcheck.saturate(expected1);
       
       IMolecule product1 = setOfReactions.getReaction(0).getProducts().getMolecule(0);
       QueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
       Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product1,queryAtom));
		
	}
	/**
	 * A unit test suite for JUnit. Reaction: Methanimine.
	 * C=!N! => C=[N*+]
	 *
	 * @cdk.inchi  InChI=1/CH3N/c1-2/h2H,1H2
	 * 
	 * @return    The test suite
	 */
	@Test public void testNsp2SingleB() throws Exception {
		//Smiles("C=N")
		IMolecule molecule = builder.newMolecule();
		molecule.addAtom(builder.newAtom("N"));
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

       IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
       setOfReactants.addMolecule(molecule);
		
		IReactionProcess type  = new ElectronImpactNBEReaction(); 
		Object[] params = {Boolean.TRUE};
       type.setParameters(params);
       
       /* initiate */
		IReactionSet setOfReactions = type.initiate(setOfReactants, null);

       Assert.assertEquals(1, setOfReactions.getReactionCount());
       
       // expected products 

       //Smiles("[N*+]=C")
       IMolecule expected1 = builder.newMolecule();
       expected1.addAtom(builder.newAtom("N"));
       expected1.getAtom(0).setFormalCharge(1);
       expected1.addSingleElectron(builder.newSingleElectron(expected1.getAtom(0)));
       expected1.addAtom(builder.newAtom("C"));
       expected1.addBond(0, 1, IBond.Order.DOUBLE);
       expected1.addAtom(builder.newAtom("H"));
       expected1.addAtom(builder.newAtom("H"));
       expected1.addAtom(builder.newAtom("H"));
       expected1.addBond(0, 2, IBond.Order.SINGLE);
       expected1.addBond(1, 3, IBond.Order.SINGLE);
       expected1.addBond(1, 4, IBond.Order.SINGLE);
       AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
       lpcheck.saturate(expected1);
       
       IMolecule product1 = setOfReactions.getReaction(0).getProducts().getMolecule(0);
       QueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
       Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product1,queryAtom));
		
	}
	/**
	 * A unit test suite for JUnit. Reaction: fluoromethane.
	 * F!-!C => [F*+]C
	 *
	 * @cdk.inchi InChI=1/CH3F/c1-2/h1H3
	 * 
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

       IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
       setOfReactants.addMolecule(molecule);
		
		IReactionProcess type  = new ElectronImpactNBEReaction(); 
		Object[] params = {Boolean.TRUE};
       type.setParameters(params);
       
       /* initiate */
		IReactionSet setOfReactions = type.initiate(setOfReactants, null);

       Assert.assertEquals(1, setOfReactions.getReactionCount());
       
       // expected products 
       
       //Smiles("[F*+]C")
       IMolecule expected1 = builder.newMolecule();
       expected1.addAtom(builder.newAtom("F"));
       expected1.getAtom(0).setFormalCharge(1);
       expected1.addSingleElectron(builder.newSingleElectron(expected1.getAtom(0)));
       expected1.addAtom(builder.newAtom("C"));
       expected1.addBond(0, 1, IBond.Order.SINGLE);
       expected1.addAtom(builder.newAtom("H"));
       expected1.addAtom(builder.newAtom("H"));
       expected1.addAtom(builder.newAtom("H"));
       expected1.addBond(1, 2, IBond.Order.SINGLE);
       expected1.addBond(1, 3, IBond.Order.SINGLE);
       expected1.addBond(1, 4, IBond.Order.SINGLE);
       AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
		lpcheck.saturate(expected1);
       
       IMolecule product1 = setOfReactions.getReaction(0).getProducts().getMolecule(0);
       QueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
       Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product1,queryAtom));
		
	}
	/**
	 * A unit test suite for JUnit. 
	 * 
	 * @return    The test suite
	 */
	@Test public void testCentreActive() throws Exception {
		IReactionProcess type  = new ElectronImpactNBEReaction();

		Object[] object = type.getParameters();
		Assert.assertFalse(((Boolean) object[0]).booleanValue());
		 
		Object[] params = {Boolean.TRUE};
        type.setParameters(params);
		Assert.assertTrue(((Boolean) params[0]).booleanValue());
        
	}
	/**
	 * A unit test suite for JUnit. Reaction: C=O => C=[O*+]
	 * Manually put of the reactive center.
	 *
	 * @cdk.inchi InChI=1/CH2O/c1-2/h1H2
	 * 
	 * @return    The test suite
	 */
	@Test public void testCDKConstants_REACTIVE_CENTER() throws Exception {
		IReactionProcess type  = new ElectronImpactNBEReaction();
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();

		/*C=O*/
		IMolecule molecule = builder.newMolecule();//Smiles("C=O")
		molecule.addAtom(builder.newAtom("C"));
		molecule.addAtom(builder.newAtom("O"));
		molecule.addBond(0, 1, IBond.Order.DOUBLE);
		molecule.addAtom(builder.newAtom("H"));
		molecule.addAtom(builder.newAtom("H"));
		molecule.addBond(0, 2, IBond.Order.SINGLE);
		molecule.addBond(0, 3, IBond.Order.SINGLE);
		
	    AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
		lpcheck.saturate(molecule);
		setOfReactants.addMolecule(molecule);
		
		/*manually put the reactive center*/
		molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
		
        Object[] params = {Boolean.TRUE};
        type.setParameters(params);
        
        /* initiate */
		makeSureAtomTypesAreRecognized(molecule);
		
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        IMolecule reactant = setOfReactions.getReaction(0).getReactants().getMolecule(0);
		Assert.assertTrue(molecule.getAtom(1).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(reactant.getAtom(1).getFlag(CDKConstants.REACTIVE_CENTER));
	}

	/**
	 * A unit test suite for JUnit. Reaction: C=O => C=[O*+]
	 * Manually put of the reactive center.
	 *
	 * @cdk.inchi InChI=1/CH2O/c1-2/h1H2
	 *  
	 * @return    The test suite
	 */
	@Test public void testMapping() throws Exception {
		IReactionProcess type  = new ElectronImpactNBEReaction();
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		
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
        Object[] params = {Boolean.FALSE};
        type.setParameters(params);
        
        /* initiate */
		makeSureAtomTypesAreRecognized(molecule);
		
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        IMolecule product = setOfReactions.getReaction(0).getProducts().getMolecule(0);

        Assert.assertEquals(1,setOfReactions.getReaction(0).getMappingCount());
        IAtom mappedProductA1 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtom(1));
        Assert.assertEquals(mappedProductA1, product.getAtom(1));
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
