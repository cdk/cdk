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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
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
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.ReactionProcessTest;
import org.openscience.cdk.reaction.type.parameters.IParameterReact;
import org.openscience.cdk.reaction.type.parameters.SetReactionCenter;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;

/**
 * TestSuite that runs a test for the HeterolyticCleavageSBReactionTest.
 * Generalized Reaction: A-B => |[A-] +[B+] // [A+] + |[B-]. Depending of the bond order
 * the bond will be removed or simply the order decreased. 
 *
 * @cdk.module test-reaction
 */
public class HeterolyticCleavageSBReactionTest extends ReactionProcessTest {

	private final LonePairElectronChecker lpcheck = new LonePairElectronChecker();
	private IChemObjectBuilder builder = NoNotificationChemObjectBuilder.getInstance();
	/**
	 *  The JUnit setup method
	 */
	public  HeterolyticCleavageSBReactionTest()  throws Exception {
			setReaction(HeterolyticCleavageSBReaction.class);
	 }
	 
	 /**
	  *  The JUnit setup method
	  */
	 @Test public void testHeterolyticCleavageSBReaction() throws Exception {
			IReactionProcess type = new HeterolyticCleavageSBReaction();
			Assert.assertNotNull(type);
	 }
	 /**
	 * A unit test suite for JUnit. Reaction: propane.
	 * CC!-!C => C[C+] + [C-]
	 *           C[C-] + [C+]
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

        IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
        setOfReactants.addMolecule(molecule);
		
		IReactionProcess type  = new HeterolyticCleavageSBReaction(); 
		List<IParameterReact> paramList = new ArrayList<IParameterReact>();
	    IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);
        
        /* initiate */
		IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(2, setOfReactions.getReactionCount());
        
        // expected products 
        
        //Smiles("C[C+]")
		IMolecule expected1 = builder.newMolecule();
		expected1.addAtom(builder.newAtom("C"));
		expected1.addAtom(builder.newAtom("C"));
		expected1.getAtom(1).setFormalCharge(+1);
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
		
		//Smiles("[C-]")
		IMolecule expected2 = builder.newMolecule();
		expected2.addAtom(builder.newAtom("C"));
		expected2.getAtom(0).setFormalCharge(-1);
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
        
        //Smiles("C[C-]")
		expected1.getAtom(1).setFormalCharge(-1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
        product1 = setOfReactions.getReaction(1).getProducts().getMolecule(0);
        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product1,queryAtom));
		
		//Smiles("[C+]")
		expected2.getAtom(0).setFormalCharge(+1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected2);
        product2 = setOfReactions.getReaction(1).getProducts().getMolecule(1);
        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected2);
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product2,queryAtom));
	}
	/**
	 * A unit test suite for JUnit. Reaction: .
	 * C[C+]!-!C => CC + [C+]
	 * 
	 * @return    The test suite
	 */
	@Test public void testCsp2ChargeSingleB() throws Exception {
		
	}
	/**
	 * A unit test suite for JUnit. Reaction: Propene.
	 * C=C!-!C => C=[C+] + [C-]
	 *            C=[C-] + [C+]
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

        IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
        setOfReactants.addMolecule(molecule);
		
		IReactionProcess type  = new HeterolyticCleavageSBReaction(); 
		List<IParameterReact> paramList = new ArrayList<IParameterReact>();
	    IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);
        
        /* initiate */
		IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(2, setOfReactions.getReactionCount());
        
        // expected products 
        
        //Smiles("C=[C+]")
		IMolecule expected1 = builder.newMolecule();
		expected1.addAtom(builder.newAtom("C"));
		expected1.addAtom(builder.newAtom("C"));
		expected1.getAtom(1).setFormalCharge(+1);
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
		
		//Smiles("[C-]")
		IMolecule expected2 = builder.newMolecule();
		expected2.addAtom(builder.newAtom("C"));
		expected2.getAtom(0).setFormalCharge(-1);
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
        
        //Smiles("C=[C-]")
		expected1.getAtom(1).setFormalCharge(-1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
		lpcheck.saturate(expected1);
        product1 = setOfReactions.getReaction(1).getProducts().getMolecule(0);
        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product1,queryAtom));
		
		//Smiles("[C+]")
		expected2.getAtom(0).setFormalCharge(+1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected2);

        product2 = setOfReactions.getReaction(1).getProducts().getMolecule(1);
        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected2);
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product2,queryAtom));
        
	}
	/**
	 * A unit test suite for JUnit. Reaction: .
	 * C=[C+]!-!C => C=C + [C+]
	 * 
	 * @return    The test suite
	 */
	@Test public void testCspChargeSingleB() throws Exception {
		
	}
	/**
	 * A unit test suite for JUnit. Reaction: Propyne.
	 * C#C!-!C => C#[C+] + [C-]
	 *            C#[C-] + [C+]
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

        IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
        setOfReactants.addMolecule(molecule);
		
		IReactionProcess type  = new HeterolyticCleavageSBReaction(); 
		List<IParameterReact> paramList = new ArrayList<IParameterReact>();
	    IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);
        
        /* initiate */
		IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(2, setOfReactions.getReactionCount());
       
        // expected products 
        
        //Smiles("C#[C-]")
		IMolecule expected1 = builder.newMolecule();
		expected1.addAtom(builder.newAtom("C"));
		expected1.addAtom(builder.newAtom("C"));
		expected1.getAtom(1).setFormalCharge(-1);
		expected1.addBond(0, 1, IBond.Order.TRIPLE);
		expected1.addAtom(builder.newAtom("H"));
		expected1.addBond(0, 2, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
        IMolecule product1 = setOfReactions.getReaction(1).getProducts().getMolecule(0);
        QueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product1,queryAtom));
		
		//Smiles("[C+]")
		IMolecule expected2 = builder.newMolecule();
		expected2.addAtom(builder.newAtom("C"));
		expected2.getAtom(0).setFormalCharge(+1);
		expected2.addAtom(builder.newAtom("H"));
		expected2.addAtom(builder.newAtom("H"));
		expected2.addAtom(builder.newAtom("H"));
		expected2.addBond(0, 1, IBond.Order.SINGLE);
		expected2.addBond(0, 2, IBond.Order.SINGLE);
		expected2.addBond(0, 3, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected2);
		lpcheck.saturate(expected2);
        IMolecule product2 = setOfReactions.getReaction(1).getProducts().getMolecule(1);
        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected2);
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product2,queryAtom));
        
        //Smiles("C#[C+]")
		expected1.getAtom(1).setFormalCharge(+1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
		lpcheck.saturate(expected1);
        product1 = setOfReactions.getReaction(0).getProducts().getMolecule(0);
        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product1,queryAtom));
		
		//Smiles("[C-]")
		expected2.getAtom(0).setFormalCharge(-1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected2);
        product2 = setOfReactions.getReaction(0).getProducts().getMolecule(1);
        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected2);
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product2,queryAtom));
        
	}

	/**
	 * A unit test suite for JUnit. Reaction: dimethylamine.
	 * CN!-!C => C[N-] + [C+]
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

        IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
        setOfReactants.addMolecule(molecule);
		
		IReactionProcess type  = new HeterolyticCleavageSBReaction(); 
		List<IParameterReact> paramList = new ArrayList<IParameterReact>();
	    IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);
        
        /* initiate */
		IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(1, setOfReactions.getReactionCount());
        
        // expected products 
        
        //Smiles("C[N-]")
        IMolecule expected1 = builder.newMolecule();
        expected1.addAtom(builder.newAtom("C"));
        expected1.addAtom(builder.newAtom("N"));
        expected1.getAtom(1).setFormalCharge(-1);
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
		
		
        //Smiles("[C+]")
        IMolecule expected2 = builder.newMolecule();
        expected2.addAtom(builder.newAtom("C"));
        expected2.getAtom(0).setFormalCharge(+1);
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
	 * A unit test suite for JUnit. Reaction: N-methylmethanimine.
	 * C=N!-!C =>[C+] +  C=[N-]
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

        IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
        setOfReactants.addMolecule(molecule);
		
		IReactionProcess type  = new HeterolyticCleavageSBReaction(); 
		List<IParameterReact> paramList = new ArrayList<IParameterReact>();
	    IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);
        
        /* initiate */
		IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(1, setOfReactions.getReactionCount());
        
        // expected products 

        //Smiles("[C+]")
        IMolecule expected1 = builder.newMolecule();
        expected1.addAtom(builder.newAtom("C"));
        expected1.getAtom(0).setFormalCharge(+1);
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
        
        //Smiles("C=[N-]")
		IMolecule expected2 = builder.newMolecule();
		expected2.addAtom(builder.newAtom("C"));
		expected2.addAtom(builder.newAtom("N"));
		expected2.getAtom(1).setFormalCharge(-1);
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
	 * A unit test suite for JUnit. Reaction:  methoxymethane.
	 * CO!-!C =>  C[O-] + [C+]
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

        IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
        setOfReactants.addMolecule(molecule);
		
		IReactionProcess type  = new HeterolyticCleavageSBReaction(); 
		List<IParameterReact> paramList = new ArrayList<IParameterReact>();
	    IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);
        
        /* initiate */
		IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(1, setOfReactions.getReactionCount());
        
        // expected products 

        //Smiles("C[O-]")
        IMolecule expected1 = builder.newMolecule();
        expected1.addAtom(builder.newAtom("C"));
		expected1.addAtom(builder.newAtom("O"));
		expected1.getAtom(1).setFormalCharge(-1);
		expected1.addBond(0, 1, IBond.Order.SINGLE);
		expected1.addAtom(builder.newAtom("H"));
		expected1.addAtom(builder.newAtom("H"));
		expected1.addAtom(builder.newAtom("H"));
		expected1.addBond(0, 2, IBond.Order.SINGLE);
		expected1.addBond(0, 3, IBond.Order.SINGLE);
		expected1.addBond(0, 4, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
		lpcheck.saturate(molecule);
        IMolecule product1 = setOfReactions.getReaction(0).getProducts().getMolecule(0);
        QueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product1,queryAtom));
		
        //Smiles("[C+]")
        IMolecule expected2 = builder.newMolecule();
        expected2.addAtom(builder.newAtom("C"));
        expected2.getAtom(0).setFormalCharge(+1);
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
	 * A unit test suite for JUnit. Reaction: fluoromethane.
	 * F!-!C => [F-] + [C+]
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

        IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
        setOfReactants.addMolecule(molecule);
		
		IReactionProcess type  = new HeterolyticCleavageSBReaction(); 
		List<IParameterReact> paramList = new ArrayList<IParameterReact>();
	    IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);
        
        /* initiate */
		IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(1, setOfReactions.getReactionCount());
        

        //Smiles("[F-]")
        IMolecule expected1 = builder.newMolecule();
        expected1.addAtom(builder.newAtom("F"));
        expected1.getAtom(0).setFormalCharge(-1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
		lpcheck.saturate(expected1);
        IMolecule product1 = setOfReactions.getReaction(0).getProducts().getMolecule(0);
        QueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product1,queryAtom));
        
        //Smiles("[C+]")
        IMolecule expected2 = builder.newMolecule();
        expected2.addAtom(builder.newAtom("C"));
        expected2.getAtom(0).setFormalCharge(+1);
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
	 * A unit test suite for JUnit. Reaction: C-O => [C+] + [O-]
	 * Manually put of the reactive center.
	 *
	 * @cdk.inchi  InChI=1/CH4O/c1-2/h2H,1H3 
	 * 
	 * @return    The test suite
	 */
	@Test public void testCDKConstants_REACTIVE_CENTER() throws Exception {
		IReactionProcess type  = new HeterolyticCleavageSBReaction();
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();

		/*CO*/
		IMolecule molecule = builder.newMolecule();//Smiles("CO")
		molecule.addAtom(builder.newAtom("C"));
		molecule.addAtom(builder.newAtom("O"));
		molecule.addBond(0, 1, IBond.Order.SINGLE);
		addExplicitHydrogens(molecule);
		
	    AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
		lpcheck.saturate(molecule);
		setOfReactants.addMolecule(molecule);
		
		/*manually put the reactive center*/
		molecule.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		
        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
	    IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);
        
        /* initiate */
		makeSureAtomTypesAreRecognized(molecule);
		
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(2, setOfReactions.getReaction(0).getProductCount());

        IMolecule reactant = setOfReactions.getReaction(0).getReactants().getMolecule(0);
		Assert.assertTrue(molecule.getAtom(0).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(reactant.getAtom(0).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(molecule.getAtom(1).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(reactant.getAtom(1).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(molecule.getBond(0).getFlag(CDKConstants.REACTIVE_CENTER));
		Assert.assertTrue(reactant.getBond(0).getFlag(CDKConstants.REACTIVE_CENTER));
	}

	/**
	 * A unit test suite for JUnit. Reaction: C-O => [C+] + [O-]
	 * Test of mapped between the reactant and product. Only is mapped the reactive center.
	 * 
	 * @cdk.inchi  InChI=1/CH4O/c1-2/h2H,1H3 
	 *  
	 * @return    The test suite
	 */
	@Test public void testMapping() throws Exception {
		IReactionProcess type  = new HeterolyticCleavageSBReaction();
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		
		/*C=O*/
		IMolecule molecule = builder.newMolecule();//Smiles("C=O")
		molecule.addAtom(builder.newAtom("C"));
		molecule.addAtom(builder.newAtom("O"));
		molecule.addBond(0, 1, IBond.Order.SINGLE);
		addExplicitHydrogens(molecule);
		
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
		lpcheck.saturate(molecule);
		setOfReactants.addMolecule(molecule);
		
		/*automatic search of the center active*/
        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
	    IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.FALSE);
        paramList.add(param);
        type.setParameterList(paramList);
        
        /* initiate */
		makeSureAtomTypesAreRecognized(molecule);
		
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        IMolecule product1 = setOfReactions.getReaction(0).getProducts().getMolecule(0);
        IMolecule product2 = setOfReactions.getReaction(0).getProducts().getMolecule(1);

        Assert.assertEquals(2,setOfReactions.getReaction(0).getMappingCount());
        IAtom mappedProductA1 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtom(0));
        Assert.assertEquals(mappedProductA1, product1.getAtom(0));
        IAtom mappedProductA2 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtom(1));
        Assert.assertEquals(mappedProductA2, product2.getAtom(0));
             
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
