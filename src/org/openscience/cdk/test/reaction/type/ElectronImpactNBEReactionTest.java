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
import org.openscience.cdk.interfaces.ISetOfMolecules;
import org.openscience.cdk.interfaces.ISetOfReactions;
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
		
		ISetOfMolecules setOfReactants = DefaultChemObjectBuilder.getInstance().newSetOfMolecules();
		setOfReactants.addMolecule(reactant);
		
		IReactionProcess type  = new ElectronImpactNBEReaction();
        Object[] params = {Boolean.TRUE};
        type.setParameters(params);
        ISetOfReactions setOfReactions = type.initiate(setOfReactants, null);
        
        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        IMolecule molecule = setOfReactions.getReaction(0).getProducts().getMolecule(0);
        Assert.assertEquals(0, molecule.getAtomAt(4).getFormalCharge());
        Assert.assertEquals(1, molecule.getSingleElectronSum(molecule.getAtomAt(4)));
        
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
		
		
		ISetOfMolecules setOfReactants = DefaultChemObjectBuilder.getInstance().newSetOfMolecules();
		setOfReactants.addMolecule(reactant);
		
		IReactionProcess type  = new ElectronImpactNBEReaction();
//        Object[] params = {false};
//        type.setParameters(params);
		ISetOfReactions setOfReactions = type.initiate(setOfReactants, null);
        
		Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        IMolecule molecule = setOfReactions.getReaction(0).getProducts().getMolecule(0);
        Assert.assertEquals(0, molecule.getAtomAt(4).getFormalCharge());
        Assert.assertEquals(1, molecule.getSingleElectronSum(molecule.getAtomAt(4)));
		
	}
}
