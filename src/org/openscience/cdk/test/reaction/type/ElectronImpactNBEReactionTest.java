/* $RCSfile$
 * $Author: miguelrojasch $
 * $Date: 2006-05-11 14:25:07 +0200 (Do, 11 Mai 2006) $
 * $Revision: 6221 $
 *
 *  Copyright (C) 2004-2006  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.type.ElectronImpactNBEReaction;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LonePairElectronChecker;

/**
 * TestSuite that runs a test for the ElectronImpactNBEReactionTest.
 *
 * @cdk.module test-reaction
 */
 
public class ElectronImpactNBEReactionTest extends CDKTestCase {
	
	public  ElectronImpactNBEReactionTest() {}
    
	public static Test suite() {
		return new TestSuite(ElectronImpactNBEReactionTest.class);
	}
	/**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
	public void test1_5_Hexen_3_one() throws ClassNotFoundException, CDKException, java.lang.Exception {
		/* ionize(>C=O): C=CCC(=O)CC -> C=CCC(=O*)CC , set the reactive center*/
		Molecule reactant = (new SmilesParser()).parseSmiles("C=CCC(=O)CC");
		LonePairElectronChecker lpeCheck = new LonePairElectronChecker();
		lpeCheck.newSaturate(reactant);
		
		IAtom[] atoms = reactant.getAtoms();
		for(int i = 0 ; i < atoms.length ; i++){
			if(reactant.getLonePairs(atoms[i]).length > 0){
				atoms[i].setFlag(CDKConstants.REACTIVE_CENTER,true);
			}
		}
		
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		setOfReactants.addMolecule(reactant);
		
		IReactionProcess type  = new ElectronImpactNBEReaction();
        Object[] params = {Boolean.TRUE};
        type.setParameters(params);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        IMolecule molecule = setOfReactions.getReaction(0).getProducts().getMolecule(0);
        Assert.assertEquals(1, molecule.getAtom(4).getFormalCharge());
        Assert.assertEquals(1, molecule.getSingleElectronSum(molecule.getAtom(4)));
        
        Assert.assertEquals(1,setOfReactions.getReaction(0).getMappings().length);
        
		
	}
	/**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
	public void test2_5_Hexen_3_one() throws ClassNotFoundException, CDKException, java.lang.Exception {
		/* ionize(>C=O): C=CCC(=O)CC -> C=CCC(=O*)CC, without setting the reactive center*/
		Molecule reactant = (new SmilesParser()).parseSmiles("C=CCC(=O)CC");
		LonePairElectronChecker lpeCheck = new LonePairElectronChecker();
		lpeCheck.newSaturate(reactant);
		
		
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		setOfReactants.addMolecule(reactant);
		
		IReactionProcess type  = new ElectronImpactNBEReaction();
//        Object[] params = {false};
//        type.setParameters(params);
		IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
		Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        IMolecule molecule = setOfReactions.getReaction(0).getProducts().getMolecule(0);
        Assert.assertEquals(1, molecule.getAtom(4).getFormalCharge());
        Assert.assertEquals(1, molecule.getSingleElectronSum(molecule.getAtom(4)));
		
	}
}
