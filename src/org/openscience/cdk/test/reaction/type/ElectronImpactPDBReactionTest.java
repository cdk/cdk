package org.openscience.cdk.test.reaction.type;


import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.ISetOfMolecules;
import org.openscience.cdk.interfaces.ISetOfReactions;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.type.ElectronImpactPDBReaction;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;

/**
 * TestSuite that runs a test for the ElectronImpactPDBReactionTest.
 *
 * @cdk.module test-extra
 */
 
public class ElectronImpactPDBReactionTest extends CDKTestCase {
	
	public  ElectronImpactPDBReactionTest() {}
    
	public static Test suite() {
		return new TestSuite(ElectronImpactPDBReactionTest.class);
	}
	/**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
	public void test1_5_Hexen_3_one() throws ClassNotFoundException, CDKException, java.lang.Exception {
		/* ionize >C=C< , set the reactive center*/
		Molecule reactant = (new SmilesParser()).parseSmiles("C=CCC(=O)CC");
		
		IBond[] bonds = reactant.getBonds();
		for(int i = 0 ; i < bonds.length ; i++){
			IAtom[] atoms = bonds[i].getAtoms();
			if(bonds[i].getOrder() == 2 &&
					atoms[0].getSymbol().equals("C")&&
					atoms[1].getSymbol().equals("C")){
				bonds[i].setFlag(CDKConstants.REACTIVE_CENTER,true);
			}
		}
		
		ISetOfMolecules setOfReactants = DefaultChemObjectBuilder.getInstance().newSetOfMolecules();
		setOfReactants.addMolecule(reactant);
		
		IReactionProcess type  = new ElectronImpactPDBReaction();
        Object[] params = {true};
        type.setParameters(params);
        ISetOfReactions setOfReactions = type.initiate(setOfReactants, null);
        
        Assert.assertEquals(1, setOfReactions.getReactionCount());

        IMolecule molecule = setOfReactions.getReaction(0).getProducts().getMolecule(0);
        Assert.assertEquals(1, molecule.getAtomAt(1).getFormalCharge());
        Assert.assertEquals(1, molecule.getSingleElectronSum(molecule.getAtomAt(0)));

        molecule = setOfReactions.getReaction(0).getProducts().getMolecule(1);
        Assert.assertEquals(1, molecule.getAtomAt(0).getFormalCharge());
        Assert.assertEquals(1, molecule.getSingleElectronSum(molecule.getAtomAt(1)));
		
	}
	/**
	 *  A unit test for JUnit
	 *
	 *@return    Description of the Return Value
	 */
	public void test_Propene() throws ClassNotFoundException, CDKException, java.lang.Exception {
		/* ionize all possible double bonds */
		Molecule reactant = (new SmilesParser()).parseSmiles("C=CC");
		
		
		ISetOfMolecules setOfReactants = DefaultChemObjectBuilder.getInstance().newSetOfMolecules();
		setOfReactants.addMolecule(reactant);
		
		IReactionProcess type  = new ElectronImpactPDBReaction();
//        Object[] params = {false};
//        type.setParameters(params);
		ISetOfReactions setOfReactions = type.initiate(setOfReactants, null);
        
		Assert.assertEquals(1, setOfReactions.getReactionCount());

        
        IMolecule molecule = setOfReactions.getReaction(0).getProducts().getMolecule(0);
        Assert.assertEquals(1, molecule.getAtomAt(1).getFormalCharge());
        Assert.assertEquals(1, molecule.getSingleElectronSum(molecule.getAtomAt(0)));

        molecule = setOfReactions.getReaction(0).getProducts().getMolecule(1);
        Assert.assertEquals(1, molecule.getAtomAt(0).getFormalCharge());
        Assert.assertEquals(1, molecule.getSingleElectronSum(molecule.getAtomAt(1)));
		
	}
}
