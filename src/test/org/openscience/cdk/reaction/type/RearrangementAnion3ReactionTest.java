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
package org.openscience.cdk.test.reaction.type;


import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.LonePair;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.type.RearrangementAnion3Reaction;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;

/**
 * TestSuite that runs a test for the RearrangementAnion3ReactionTest.
 * Generalized Reaction: [A-]=B => A=[B-].
 *
 * @cdk.module test-reaction
 */
public class RearrangementAnion3ReactionTest extends CDKTestCase {
	
	private IReactionProcess type;
	/**
	 * Constructor of the RearrangementAnion3ReactionTest object 
	 *
	 */
	public  RearrangementAnion3ReactionTest() {
		type  = new RearrangementAnion3Reaction();
	}
    
	public static Test suite() {
		return new TestSuite(RearrangementAnion3ReactionTest.class);
	}
	/**
	 * A unit test suite for JUnit. Reaction: [C-]=C-C => C=[C-]-C
	 * Automatic sarch of the centre active.
	 *
	 * @return    The test suite
	 */
	public void testAutomaticSearchCentreActiveExample1() throws ClassNotFoundException, CDKException, java.lang.Exception {

		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		/*[C-]=C-C*/
		IMolecule molecule = getMolecule1();
		molecule.addLonePair(new LonePair(molecule.getAtom(0)));
		setOfReactants.addMolecule(molecule);
		
        Object[] params = {Boolean.FALSE};
        type.setParameters(params);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        
        
        IMolecule product = setOfReactions.getReaction(0).getProducts().getMolecule(0);
        Assert.assertEquals(-1, product.getAtom(1).getFormalCharge().intValue());
        
        /*C=[C-]-C*/
		IMolecule molecule2 = getMolecule2();
		
        QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product);
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule2,qAC));
        
        Assert.assertEquals(3,setOfReactions.getReaction(0).getMappingCount());
        
        IAtom mappedProduct = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtom(1));
        assertEquals(mappedProduct, product.getAtom(1));
	}
	/**
	 * A unit test suite for JUnit. Reaction: [C-]=C-C => C=[C-]-C
	 * Manually put of the centre active.
	 *
	 * @return    The test suite
	 */
	public void testManuallyPutCentreActiveExample1() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		/*[C-]=C-C*/
		IMolecule molecule = getMolecule1();
		molecule.addLonePair(new LonePair(molecule.getAtom(0)));
		setOfReactants.addMolecule(molecule);
		
		/*manually put the centre active*/
		molecule.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
		
        Object[] params = {Boolean.TRUE};
        type.setParameters(params);
        
        /* iniciate */
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        IMolecule product = setOfReactions.getReaction(0).getProducts().getMolecule(0);
        
        /*C=[C-]-C*/
        IMolecule molecule2 = getMolecule2();
        
        QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product);
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule2,qAC));
        
        		
	}
	/**
	 * A unit test suite for JUnit. Reaction: [C-]=C-C => C=[C-]-C
	 * Test of mapped between the reactant and product. Only is mapped the centre active.
	 *
	 * @return    The test suite
	 */
	public void testMappingExample1() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		/*[C-]=C-C*/
		IMolecule molecule = getMolecule1();
		molecule.addLonePair(new LonePair(molecule.getAtom(0)));
		setOfReactants.addMolecule(molecule);
		
		/*automatic search of the centre active*/
        Object[] params = {Boolean.FALSE};
        type.setParameters(params);
        
        /* iniciate */
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        IMolecule product = setOfReactions.getReaction(0).getProducts().getMolecule(0);

        Assert.assertEquals(3,setOfReactions.getReaction(0).getMappingCount());
        
        IAtom mappedProductA1 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtom(0));
        assertEquals(mappedProductA1, product.getAtom(0));
        mappedProductA1 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtom(1));
        assertEquals(mappedProductA1, product.getAtom(1));
        IBond mappedProductB1 = (IBond)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getBond(0));
        assertEquals(mappedProductB1, product.getBond(0));
        
	}
	/**
	 * get the molecule 1: [C-]=C-C
	 * 
	 * @return The IMolecule
	 */
	private IMolecule getMolecule1()throws ClassNotFoundException, CDKException, java.lang.Exception {
		IMolecule molecule = (new SmilesParser(org.openscience.cdk.DefaultChemObjectBuilder.getInstance())).parseSmiles("[C-]=C-C");
        addImplicitHydrogens(molecule);
        return molecule;
	}
	/**
	 * get the molecule 2: C=[C-]-C
	 * 
	 * @return The IMolecule
	 */
	private IMolecule getMolecule2()throws ClassNotFoundException, CDKException, java.lang.Exception {
		IMolecule molecule = (new SmilesParser(org.openscience.cdk.DefaultChemObjectBuilder.getInstance())).parseSmiles("C=[C-]-C");
        addImplicitHydrogens(molecule);
        return molecule;
	}
}
