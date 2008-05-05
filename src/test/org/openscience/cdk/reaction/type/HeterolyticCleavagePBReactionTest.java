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
import org.openscience.cdk.reaction.ReactionProcessTest;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;

/**
 * TestSuite that runs a test for the HeterolyticCleavagePBReactionTest.
 * Generalized Reaction: A=B => |[A-]-[B+] + [A+]-|[B-]. Depending of the bond order
 * the bond will be removed or simply the order decreased. 
 *
 * @cdk.module test-reaction
 */
public class HeterolyticCleavagePBReactionTest extends ReactionProcessTest {

	private final static IChemObjectBuilder builder = NoNotificationChemObjectBuilder.getInstance();
	private final static LonePairElectronChecker lpcheck = new LonePairElectronChecker();
	/**
	 *  The JUnit setup method
	 */
	 @BeforeClass public static void setUp() throws Exception {
	 	setReaction(HeterolyticCleavagePBReaction.class);
	 }
	 
	 /**
	  *  The JUnit setup method
	  */
	 @Test public void testHeterolyticCleavagePBReaction() throws Exception {
			IReactionProcess type = new HeterolyticCleavagePBReaction();
			Assert.assertNotNull(type);
	 }
	/**
	 * A unit test suite for JUnit. Reaction: Propene.
	 * CC!=!C => C[C+][C-]
	 *           C[C-][C+]
	 *
	 * @cdk.inchi InChI=1/C3H6/c1-3-2/h3H,1H2,2H3
	 * 
	 * @return    The test suite
	 */
	@Test public void testInitiate_IMoleculeSet_IMoleculeSet() throws Exception {
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

        IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
        setOfReactants.addMolecule(molecule);
		
		IReactionProcess type  = new HeterolyticCleavagePBReaction(); 
		HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
        type.setParameters(params);
        
        /* initiate */
		IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(2, setOfReactions.getReactionCount());

        // expected products 
        
        //Smiles("C[C+][C-]")
		IMolecule expected1 = builder.newMolecule();
		expected1.addAtom(builder.newAtom("C"));
		expected1.addAtom(builder.newAtom("C"));
		expected1.getAtom(1).setFormalCharge(+1);
		expected1.addAtom(builder.newAtom("C"));
		expected1.getAtom(2).setFormalCharge(-1);
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

        //Smiles("C[C-][C+]")
		expected1.getAtom(1).setFormalCharge(-1);
		expected1.getAtom(2).setFormalCharge(+1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
		lpcheck.saturate(expected1);
        product1 = setOfReactions.getReaction(1).getProducts().getMolecule(0);
        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product1,queryAtom));
        
	}
	/**
	 * A unit test suite for JUnit. Reaction:.
	 * C[C+]!=!C => C[C+][C-]
	 *           C[C-][C+]
	 * 
	 * @return    The test suite
	 */
	@Test public void testCspChargeDoubleB() throws Exception {
		
	}
	/**
	 * A unit test suite for JUnit. Reaction: Allene.
	 * C=C!=!C => C=[C+][C-]
	 *            C=[C-][C+]
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

        IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
        setOfReactants.addMolecule(molecule);
		
		IReactionProcess type  = new HeterolyticCleavagePBReaction(); 
		HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
        type.setParameters(params);
        
        /* initiate */
		IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(2, setOfReactions.getReactionCount());
        
        // expected products 
        
        //Smiles("C=[C+][C-]")
        IMolecule expected1 = builder.newMolecule();
        expected1.addAtom(builder.newAtom("C"));
        expected1.addAtom(builder.newAtom("C"));
		expected1.getAtom(1).setFormalCharge(+1);
        expected1.addAtom(builder.newAtom("C"));
		expected1.getAtom(2).setFormalCharge(-1);
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

        //Smiles("C=[C-][C+]")
		expected1.getAtom(1).setFormalCharge(-1);
		expected1.getAtom(2).setFormalCharge(+1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
		lpcheck.saturate(expected1);
        product1 = setOfReactions.getReaction(1).getProducts().getMolecule(0);
        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product1,expected1));
	}
	/**
	 * A unit test suite for JUnit. Reaction: Propyne.
	 * CC#C => C[C+]=[C-]
	 *         C[C-]=[C+]
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

        IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
        setOfReactants.addMolecule(molecule);
		
		IReactionProcess type  = new HeterolyticCleavagePBReaction(); 
		HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
        type.setParameters(params);
        
        /* initiate */
		IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(2, setOfReactions.getReactionCount());
        
        // expected products 
        
        //Smiles("C[C+]=[C-]")
        IMolecule expected1 = builder.newMolecule();
        expected1.addAtom(builder.newAtom("C"));
        expected1.addAtom(builder.newAtom("C"));
		expected1.getAtom(1).setFormalCharge(+1);
        expected1.addAtom(builder.newAtom("C"));
		expected1.getAtom(2).setFormalCharge(-1);
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

        //Smiles("C[C-]=[C+]")
		expected1.getAtom(1).setFormalCharge(-1);
		expected1.getAtom(2).setFormalCharge(+1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
		lpcheck.saturate(expected1);
        product1 = setOfReactions.getReaction(1).getProducts().getMolecule(0);
        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product1,queryAtom));
	}

	/**
	 * A unit test suite for JUnit. Reaction: N-methylmethanimine.
	 * CN!=!C => C[N-]-[C+]
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

        IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
        setOfReactants.addMolecule(molecule);
		
		IReactionProcess type  = new HeterolyticCleavagePBReaction(); 
		HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
        type.setParameters(params);
        
        /* initiate */
		IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(1, setOfReactions.getReactionCount());
        
        // expected products 

        //Smiles("C[N-]-[C+]")
        IMolecule expected1 = builder.newMolecule();
        expected1.addAtom(builder.newAtom("C"));
        expected1.addAtom(builder.newAtom("N"));
		expected1.getAtom(1).setFormalCharge(-1);
        expected1.addAtom(builder.newAtom("C"));
		expected1.getAtom(2).setFormalCharge(+1);
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
	 * A unit test suite for JUnit. Reaction: formonitrile.
	 * N!#!C => [N-]=[C+]
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

        IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
        setOfReactants.addMolecule(molecule);
		
		IReactionProcess type  = new HeterolyticCleavagePBReaction(); 
		HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
        type.setParameters(params);
        
        /* initiate */
		IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(1, setOfReactions.getReactionCount());
        
        // expected products 

        //Smiles("[N-]=[C+]")
        IMolecule expected1 = builder.newMolecule();
        expected1.addAtom(builder.newAtom("N"));
		expected1.getAtom(0).setFormalCharge(-1);
        expected1.addAtom(builder.newAtom("C"));
		expected1.getAtom(1).setFormalCharge(+1);
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
	 * A unit test suite for JUnit. Reaction: formaldehyde.
	 * O!=!C => [O-][C+]
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

        IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
        setOfReactants.addMolecule(molecule);
		
		IReactionProcess type  = new HeterolyticCleavagePBReaction(); 
		HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
        type.setParameters(params);
        
        /* initiate */
		IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(1, setOfReactions.getReactionCount());
        
        // expected products 

        //Smiles("[O-][C+]")
        IMolecule expected1 = builder.newMolecule();
        expected1.addAtom(builder.newAtom("O"));
		expected1.getAtom(0).setFormalCharge(-1);
        expected1.addAtom(builder.newAtom("C"));
		expected1.getAtom(1).setFormalCharge(+1);
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
	 * A unit test suite for JUnit. 
	 * 
	 * @return    The test suite
	 */
	@Test public void testCentreActive() throws Exception {
		IReactionProcess type  = new HeterolyticCleavagePBReaction();

		HashMap<String,Object> params = type.getParameters();
		Assert.assertTrue(params.get("hasActiveCenter") instanceof Boolean);
		Assert.assertFalse((Boolean)params.get("hasActiveCenter"));

        params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);
        type.setParameters(params);
		Assert.assertTrue((Boolean)params.get("hasActiveCenter"));
	}
	/**
	 * A unit test suite for JUnit. Reaction: C=O => [C+]-[O-]
	 * Manually put of the reactive center.
	 *
	 * @cdk.inchi InChI=1/CH2O/c1-2/h1H2
	 * 
	 * @return    The test suite
	 */
	@Test public void testCDKConstants_REACTIVE_CENTER() throws Exception {
		IReactionProcess type  = new HeterolyticCleavagePBReaction();
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
	 * A unit test suite for JUnit. Reaction: C=O => [C+]-[O-] + [C-]-[O+]
	 * Test of mapped between the reactant and product. Only is mapped the reactive center.
	 * 
	 * @cdk.inchi InChI=1/CH2O/c1-2/h1H2
	 *  
	 * @return    The test suite
	 */
	@Test public void testMapping() throws Exception {
		IReactionProcess type  = new HeterolyticCleavagePBReaction();
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
	}/**
	 * A unit test suite for JUnit. Reaction: 
	 * C(H)(H)=O => [C+](H)(H)-[O-] + [C+](H)=O +  
	 * Automatic search of the reactive atoms and bonds.
	 *
	 * @cdk.inchi InChI=1/CH2O/c1-2/h1H2
	 * 
	 * @return    The test suite
	 */
	@Test public void testBB_AutomaticSearchCentreActiveFormaldehyde() throws Exception {
		IReactionProcess type  = new HeterolyticCleavagePBReaction();
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		
		//Smiles("C(H)(H)=O")
		IMolecule molecule = builder.newMolecule();
		molecule.addAtom(builder.newAtom("C"));
		molecule.addAtom(builder.newAtom("O"));
		molecule.addBond(0, 1, IBond.Order.DOUBLE);
		addExplicitHydrogens(molecule);
        
		Assert.assertEquals(4, molecule.getAtomCount());

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
		lpcheck.saturate(molecule);
		Assert.assertEquals(2, molecule.getLonePairCount());
		Assert.assertEquals(molecule.getAtom(1), molecule.getLonePair(0).getAtom());
		Assert.assertEquals(molecule.getAtom(1), molecule.getLonePair(1).getAtom());
		setOfReactants.addMolecule(molecule);
		
		/*automatic search of the reactive atoms and bonds */
        HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.FALSE);;
        type.setParameters(params);
        
        /* initiate */
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
		makeSureAtomTypesAreRecognized(molecule);
		
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        Assert.assertEquals(1, setOfReactions.getReactionCount());
        
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        IMolecule product = setOfReactions.getReaction(0).getProducts().getMolecule(0);
        //Smiles("[C+](H)(H)-[O-]");
        IMolecule molecule11 = builder.newMolecule();
        IAtom carbon = builder.newAtom("C");
        carbon.setFormalCharge(1);
        molecule11.addAtom(carbon);
        IAtom oxyg = builder.newAtom("O");
        oxyg.setFormalCharge(-1);
        molecule11.addAtom(oxyg);
        molecule11.addBond(0, 1, IBond.Order.SINGLE);
        molecule11.addAtom(new Atom("H"));
        molecule11.addAtom(new Atom("H"));
        molecule11.addBond(0, 2, IBond.Order.SINGLE);
        molecule11.addBond(0, 3, IBond.Order.SINGLE);
	    
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule11,product));
		
	}
}
