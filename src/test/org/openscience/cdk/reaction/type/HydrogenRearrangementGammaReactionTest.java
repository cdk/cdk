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

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.SingleElectron;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.type.HydrogenRearrangementGammaReaction;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;

/**
 * TestSuite that runs a test for the RearrangementRadical3ReactionTest.
 * Generalized Reaction: [C*]-C1-C2-C3[H] => C([H])-C1-C2-[C3*].
 *
 * @cdk.module test-reaction
 */
public class HydrogenRearrangementGammaReactionTest extends CDKTestCase {
	
	private IReactionProcess type;
	/**
	 * Constructror of the HydrogenRearrangementGammaReactionTest object
	 *
	 */
	public  HydrogenRearrangementGammaReactionTest() {
		type  = new HydrogenRearrangementGammaReaction();
	}
    
	public static Test suite() {
		return new TestSuite(HydrogenRearrangementGammaReactionTest.class);
	}
	/**
	 * A unit test suite for JUnit. Reaction: [A*]-C1-C2-C3[H] => A([H])-C1-C2-[C3*]
	 * Automatic sarch of the centre active.
	 *
	 * @return    The test suite
	 */
	public void testAutomaticSearchCentreActiveExample1() throws ClassNotFoundException, CDKException, java.lang.Exception {
        
		/*[C*]=C-C-C-C*/
        IMolecule molecule = (new SmilesParser(org.openscience.cdk.DefaultChemObjectBuilder.getInstance())).parseSmiles("C([H])([H])C([H])([H])C([H])([H])C([H])([H])C([H])([H])C([H])([H])[H]");
        molecule.addSingleElectron(new SingleElectron(molecule.getAtom(0)));
        
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		setOfReactants.addMolecule(molecule);
		
        Object[] params = {Boolean.FALSE};
        type.setParameters(params);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        
        
        IMolecule product = setOfReactions.getReaction(0).getProducts().getMolecule(0);
        /*|C-[C*]-C*/
        IMolecule molecule2 = (new SmilesParser(org.openscience.cdk.DefaultChemObjectBuilder.getInstance())).parseSmiles("C([H])([H])([H])C([H])([H])C([H])([H])C([H])([H])C([H])C([H])([H])[H]");
        molecule2.addSingleElectron(new SingleElectron(molecule2.getAtom(12)));
        
        QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product);
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule2,qAC));
        
        Assert.assertEquals(3,setOfReactions.getReaction(0).getMappingCount());
        
        IAtom mappedProduct = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtom(0));
        assertEquals(mappedProduct, product.getAtom(0));
	}

}
