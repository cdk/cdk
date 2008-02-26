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
import org.openscience.cdk.LonePair;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.type.CarbonylEliminationReaction;
import org.openscience.cdk.reaction.ReactionProcessTest;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;

/**
 * TestSuite that runs a test for the CarbonylEliminationReactionTest.
 * Generalized Reaction: RC-C#[O+] => R[C] + |C#[O+]
 *
 * @cdk.module test-reaction
 */
public class CarbonylEliminationReactionTest extends ReactionProcessTest {

	private final static  IChemObjectBuilder builder = NoNotificationChemObjectBuilder.getInstance();
	/**
	 *  The JUnit setup method
	 */
	 @BeforeClass public static void setUp() throws Exception {
	 	setReaction(CarbonylEliminationReaction.class);
	 }
	/**
	 * A unit test suite for JUnit. Reaction: C-C#[O+] => [C+] + [|C-]#[O+]
	 * Automatic looking for active center.
	 *
	 * @return    The test suite
	 */
	@Test public void testAutomaticSearchCentreActiveExample1() throws Exception {
        
		IReactionProcess type  = new CarbonylEliminationReaction();
		/*[C*]-C-C*/
		IMolecule molecule = builder.newMolecule();//Smiles("C-C#[O+]")
		molecule.addAtom(builder.newAtom("C"));
		molecule.addAtom(builder.newAtom("H"));
		molecule.addBond(0, 1, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("H"));
		molecule.addBond(0, 2, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("H"));
		molecule.addBond(0, 3, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(0, 4, IBond.Order.SINGLE);
		IAtom oxy = builder.newAtom("O");
		oxy.setFormalCharge(1);
		molecule.addAtom(oxy);
		molecule.addBond(4, 5, IBond.Order.TRIPLE);
		
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
        lpcheck.saturate(molecule);
		makeSureAtomTypesAreRecognized(molecule);
        
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		setOfReactants.addMolecule(molecule);
		
		/* initiate */
        Object[] params = {Boolean.FALSE};
        type.setParameters(params);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(2, setOfReactions.getReaction(0).getProductCount());

        
        IMolecule product1 = setOfReactions.getReaction(0).getProducts().getMolecule(0);
      
        //Smiles("[C+]");
        IMolecule molecule2 = builder.newMolecule();//Smiles("[C+]");
		IAtom carb = builder.newAtom("C");
		carb.setFormalCharge(1);
		molecule2.addAtom(carb);
		molecule2.addAtom(builder.newAtom("H"));
		molecule2.addBond(0, 1, IBond.Order.SINGLE);
		molecule2.addAtom(builder.newAtom("H"));
		molecule2.addBond(0, 2, IBond.Order.SINGLE);
		molecule2.addAtom(builder.newAtom("H"));
		molecule2.addBond(0, 3, IBond.Order.SINGLE);
		
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule2,product1));
		
		IMolecule product2 = setOfReactions.getReaction(0).getProducts().getMolecule(1);
		
        /*[C*]*/
		molecule2 = builder.newMolecule();//Smiles("[C-]#[O+]");
		carb = builder.newAtom("C");
		carb.setFormalCharge(-1);
		molecule2.addLonePair(new LonePair(carb));
		molecule2.addAtom(carb);
		oxy = builder.newAtom("O");
		oxy.setFormalCharge(1);
		molecule2.addAtom(oxy);
		molecule2.addBond(0, 1, IBond.Order.TRIPLE);
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule2);
		lpcheck.saturate(molecule2);
		makeSureAtomTypesAreRecognized(molecule2);
		
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule2,product2));
       
	}
	/**
	 * A unit test suite for JUnit. Reaction: C-C#[O+] => [C+] + [|C-]#[O+]
	 * Automatic looking for active center.
	 *
	 * @return    The test suite
	 */
	@Test public void testManuallyPCentreActiveExample1() throws Exception {
        
		IReactionProcess type  = new CarbonylEliminationReaction();
		/*[C*]-C-C*/
		IMolecule molecule = builder.newMolecule();//Smiles("C-C#[O+]")
		molecule.addAtom(builder.newAtom("C"));
		molecule.addAtom(builder.newAtom("H"));
		molecule.addBond(0, 1, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("H"));
		molecule.addBond(0, 2, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("H"));
		molecule.addBond(0, 3, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(0, 4, IBond.Order.SINGLE);
		IAtom oxy = builder.newAtom("O");
		oxy.setFormalCharge(1);
		molecule.addAtom(oxy);
		molecule.addBond(4, 5, IBond.Order.TRIPLE);
		
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
        lpcheck.saturate(molecule);
		makeSureAtomTypesAreRecognized(molecule);
        
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		setOfReactants.addMolecule(molecule);
		
		/* initiate */
		/*manually put the reactive center*/
		molecule.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(4).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(5).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(3).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(4).setFlag(CDKConstants.REACTIVE_CENTER,true);
		
        Object[] params = {Boolean.TRUE};
        type.setParameters(params);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(2, setOfReactions.getReaction(0).getProductCount());

        
        IMolecule product1 = setOfReactions.getReaction(0).getProducts().getMolecule(0);
      
        //Smiles("[C+]");
        IMolecule molecule2 = builder.newMolecule();//Smiles("[C+]");
		IAtom carb = builder.newAtom("C");
		carb.setFormalCharge(1);
		molecule2.addAtom(carb);
		molecule2.addAtom(builder.newAtom("H"));
		molecule2.addBond(0, 1, IBond.Order.SINGLE);
		molecule2.addAtom(builder.newAtom("H"));
		molecule2.addBond(0, 2, IBond.Order.SINGLE);
		molecule2.addAtom(builder.newAtom("H"));
		molecule2.addBond(0, 3, IBond.Order.SINGLE);
		
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule2,product1));
		
		IMolecule product2 = setOfReactions.getReaction(0).getProducts().getMolecule(1);
		
        /*[C*]*/
		molecule2 = builder.newMolecule();//Smiles("[C-]#[O+]");
		carb = builder.newAtom("C");
		carb.setFormalCharge(-1);
		molecule2.addLonePair(new LonePair(carb));
		molecule2.addAtom(carb);
		oxy = builder.newAtom("O");
		oxy.setFormalCharge(1);
		molecule2.addAtom(oxy);
		molecule2.addBond(0, 1, IBond.Order.TRIPLE);
		
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule2,product2));
       
	}
	/**
	 * A unit test suite for JUnit. Reaction: C-C#[O+] => [C+] + [|C-]#[O+]
	 * Automatic looking for active center.
	 *
	 * @return    The test suite
	 */
	@Test public void testMappingExample1() throws Exception {
        
		IReactionProcess type  = new CarbonylEliminationReaction();
		/*[C*]-C-C*/
		IMolecule molecule = builder.newMolecule();//Smiles("C-C#[O+]")
		molecule.addAtom(builder.newAtom("C"));
		molecule.addAtom(builder.newAtom("H"));
		molecule.addBond(0, 1, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("H"));
		molecule.addBond(0, 2, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("H"));
		molecule.addBond(0, 3, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(0, 4, IBond.Order.SINGLE);
		IAtom oxy = builder.newAtom("O");
		oxy.setFormalCharge(1);
		molecule.addAtom(oxy);
		molecule.addBond(4, 5, IBond.Order.TRIPLE);
		
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
		LonePairElectronChecker lpcheck = new LonePairElectronChecker();
        lpcheck.saturate(molecule);
		makeSureAtomTypesAreRecognized(molecule);
        
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		setOfReactants.addMolecule(molecule);
		
		/* initiate */
        Object[] params = {Boolean.FALSE};
        type.setParameters(params);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        IMolecule product1 = setOfReactions.getReaction(0).getProducts().getMolecule(0);
		IMolecule product2 = setOfReactions.getReaction(0).getProducts().getMolecule(1);

		Assert.assertEquals(2,setOfReactions.getReaction(0).getMappingCount());
		IAtom mappedProductA1 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtom(0));
		Assert.assertEquals(mappedProductA1, product1.getAtom(0));
        IAtom mappedProductA2 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtom(4));
        Assert.assertEquals(mappedProductA2, product2.getAtom(0));
        
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
