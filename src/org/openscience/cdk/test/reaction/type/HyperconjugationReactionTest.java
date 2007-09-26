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

import org.openscience.cdk.Atom;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.type.HyperconjugationReaction;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;

/**
 * TestSuite that runs a test for the HyperconjugationReactionTest.
 * Generalized Reaction: [C1+]-C2 => C1=C2 + [H+].
 *
 * @cdk.module test-reaction
 */
public class HyperconjugationReactionTest extends CDKTestCase {
	
	private IReactionProcess type;

	/**
	 * Constructror of the HyperconjugationReactionTest object
	 *
	 */
	public  HyperconjugationReactionTest() {
		type  = new HyperconjugationReaction();
	}
    
	public static Test suite() {
		return new TestSuite(HyperconjugationReactionTest.class);
	}

	/**
	 * A unit test suite for JUnit. Reaction: [C+]-CC => C=CC + [H+]
	 * Automatic sarch of the centre active.
	 *
	 * @return    The test suite
	 */
	public void testBB_AutomaticSearchCentreActiveFormaldehyde() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		
		/*[C+]CC*/
		IMolecule molecule = (new SmilesParser(org.openscience.cdk.DefaultChemObjectBuilder.getInstance())).parseSmiles("[C+]CC");
		for(int i = 0; i < 7 ; i++)
			molecule.addAtom(new Atom("H"));
	    molecule.addBond(0, 3, 1);
	    molecule.addBond(0, 4, 1);
	    molecule.addBond(1, 5, 1);
	    molecule.addBond(1, 6, 1);
	    molecule.addBond(2, 7, 1);
	    molecule.addBond(2, 8, 1);
	    molecule.addBond(2, 9, 1);
		setOfReactants.addMolecule(molecule);
		
		/*automatic search of the centre active*/
        Object[] params = {Boolean.FALSE};
        type.setParameters(params);
        
        /* iniciate */
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(2, setOfReactions.getReaction(0).getProductCount());

        IMolecule product = setOfReactions.getReaction(0).getProducts().getMolecule(0);
        /*C=CC*/
		IMolecule molecule2 = (new SmilesParser(org.openscience.cdk.DefaultChemObjectBuilder.getInstance())).parseSmiles("C=CC");
		addExplicitHydrogens(molecule2);
		
        QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product);
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule2,qAC));
		
		product = setOfReactions.getReaction(0).getProducts().getMolecule(1);
        /*[H+]*/
		molecule2 = (new SmilesParser(org.openscience.cdk.DefaultChemObjectBuilder.getInstance())).parseSmiles("[H+]");
        
        qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product);
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule2,qAC));
	}

}
