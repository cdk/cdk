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
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.SingleElectron;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.ReactionProcessTest;
import org.openscience.cdk.reaction.type.parameters.IParameterReact;
import org.openscience.cdk.reaction.type.parameters.SetReactionCenter;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;

/**
 * TestSuite that runs a test for the RearrangementRadicalReactionTest.
 * Generalized Reaction: [A*+]-B-C => [A+]=B + [c*].
 *
 * @cdk.module test-reaction
 */
public class RadicalChargeSiteInitiationReactionTest extends ReactionProcessTest {

	private IChemObjectBuilder builder = NoNotificationChemObjectBuilder.getInstance();
	/**
	 *  The JUnit setup method
	 */
	public  RadicalChargeSiteInitiationReactionTest()  throws Exception {
			setReaction(RadicalChargeSiteInitiationReaction.class);
	 }
	 
	 /**
	  *  The JUnit setup method
	  */
	 @Test public void testRadicalChargeSiteInitiationReaction() throws Exception {
			IReactionProcess type = new RadicalChargeSiteInitiationReaction();
			Assert.assertNotNull(type);
	 }
	/**
	 * A unit test suite for JUnit. Reaction: [O+*]C([H])([H])C([H])([H])([H]) => [O+]=C([H])([H]) +[C*]([H])([H])([H])
	 * Automatic search of the center active.
	 *
	 * @return    The test suite
	 */
	@Test public void testInitiate_IMoleculeSet_IMoleculeSet() throws Exception {
        IReactionProcess type = new RadicalChargeSiteInitiationReaction();
		
		IMoleculeSet setOfReactants = getExampleReactants();

		/* initiate */
		
        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
	    IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.FALSE);
        paramList.add(param);
        type.setParameterList(paramList);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(2, setOfReactions.getReaction(0).getProductCount());

        
        IMolecule product1 = setOfReactions.getReaction(0).getProducts().getMolecule(0);
		
        IMolecule molecule1 = getExpectedProducts().getMolecule(0);
        
        QueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(molecule1);
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(product1,queryAtom));
		
		IMolecule product2 = setOfReactions.getReaction(0).getProducts().getMolecule(1);
		
        IMolecule molecule2 = getExpectedProducts().getMolecule(1);


        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product2);
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule2,queryAtom));
       
	}
	/**
	 * A unit test suite for JUnit.
	 * 
	 * @return    The test suite
	 */
	@Test public void testCDKConstants_REACTIVE_CENTER() throws Exception {
		IReactionProcess type  = new RadicalChargeSiteInitiationReaction();
		
		IMoleculeSet setOfReactants = getExampleReactants();
        IMolecule molecule = setOfReactants.getMolecule(0);
		
		/*manually put the reactive center*/
		molecule.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
		
		List<IParameterReact> paramList = new ArrayList<IParameterReact>();
	    IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);
        
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
		IReactionProcess type = new RadicalChargeSiteInitiationReaction();
		
		IMoleculeSet setOfReactants = getExampleReactants();
        IMolecule molecule = setOfReactants.getMolecule(0);

		molecule.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
		
		List<IParameterReact> paramList = new ArrayList<IParameterReact>();
		IParameterReact param = new SetReactionCenter();
	    param.setParameter(Boolean.TRUE);
	    paramList.add(param);
	    type.setParameterList(paramList);
		/* initiate */
		
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        IMolecule product1 = setOfReactions.getReaction(0).getProducts().getMolecule(0);
        IMolecule product2 = setOfReactions.getReaction(0).getProducts().getMolecule(1);

        Assert.assertEquals(9,setOfReactions.getReaction(0).getMappingCount());
        IAtom mappedProductA1 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtom(1));
		Assert.assertEquals(mappedProductA1, product1.getAtom(1));
        IAtom mappedProductA2 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtom(2));
        Assert.assertEquals(mappedProductA2, product2.getAtom(0));
        IAtom mappedProductA3 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtom(0));
        Assert.assertEquals(mappedProductA3, product1.getAtom(0));
        
	}
	/**
	 * Get the IMolecule
	 * 
	 * @return The IMoleculeSet
	 */
	private IMoleculeSet getExampleReactants() {
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newInstance(IMoleculeSet.class);
		IMolecule molecule = builder.newInstance(IMolecule.class);
		molecule.addAtom(builder.newInstance(IAtom.class,"O"));
		molecule.addAtom(builder.newInstance(IAtom.class,"C"));
		molecule.addBond(0, 1, IBond.Order.SINGLE);
		molecule.addAtom(builder.newInstance(IAtom.class,"C"));
		molecule.addBond(1, 2, IBond.Order.SINGLE);
		molecule.addAtom(builder.newInstance(IAtom.class,"H"));
		molecule.addBond(1, 3, IBond.Order.SINGLE);
		molecule.addAtom(builder.newInstance(IAtom.class,"H"));
		molecule.addBond(1, 4, IBond.Order.SINGLE);
		molecule.addAtom(builder.newInstance(IAtom.class,"H"));
		molecule.addBond(2, 5, IBond.Order.SINGLE);
		molecule.addAtom(builder.newInstance(IAtom.class,"H"));
		molecule.addBond(2, 6, IBond.Order.SINGLE);
		molecule.addAtom(builder.newInstance(IAtom.class,"H"));
		molecule.addBond(2, 7, IBond.Order.SINGLE);
		molecule.addAtom(builder.newInstance(IAtom.class,"H"));
		molecule.addBond(0, 8, IBond.Order.SINGLE);
		
		IAtom atom =  molecule.getAtom(0);
		atom.setFormalCharge(1);
        molecule.addSingleElectron(new SingleElectron(atom));
        
        setOfReactants.addMolecule(molecule);
		return setOfReactants;
	}
	/**
	 * Get the expected set of molecules.
	 * 
	 * @return The IMoleculeSet
	 */
	private IMoleculeSet getExpectedProducts() {
		IMoleculeSet setOfProducts = builder.newInstance(IMoleculeSet.class);
		IMolecule molecule1 = builder.newInstance(IMolecule.class);
		molecule1.addAtom(builder.newInstance(IAtom.class,"O"));
		molecule1.addAtom(builder.newInstance(IAtom.class,"C"));
		molecule1.addBond(0, 1, IBond.Order.DOUBLE);
		molecule1.addAtom(builder.newInstance(IAtom.class,"H"));
		molecule1.addBond(1, 2, IBond.Order.SINGLE);
		molecule1.addAtom(builder.newInstance(IAtom.class,"H"));
		molecule1.addBond(1, 3, IBond.Order.SINGLE);
		molecule1.getAtom(0).setFormalCharge(1);
		molecule1.addAtom(builder.newInstance(IAtom.class,"H"));
		molecule1.addBond(0, 4, IBond.Order.SINGLE);
        setOfProducts.addMolecule(molecule1);
        
        IMolecule molecule2 = builder.newInstance(IMolecule.class);
		molecule2.addAtom(builder.newInstance(IAtom.class,"C"));
		molecule2.addAtom(builder.newInstance(IAtom.class,"H"));
		molecule2.addBond(0, 1, IBond.Order.SINGLE);
		molecule2.addAtom(builder.newInstance(IAtom.class,"H"));
		molecule2.addBond(0, 2, IBond.Order.SINGLE);
		molecule2.addAtom(builder.newInstance(IAtom.class,"H"));
		molecule2.addBond(0, 3, IBond.Order.SINGLE);
        molecule2.addSingleElectron(new SingleElectron(molecule2.getAtom(0)));
        setOfProducts.addMolecule(molecule2);
        
        return setOfProducts;
	}
}
