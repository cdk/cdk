  package org.openscience.cdk.test.reaction.type;


import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
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
import org.openscience.cdk.tools.HydrogenAdder;

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
		Molecule molecule = (new SmilesParser()).parseSmiles("[C+]CC");
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
		Molecule molecule2 = (new SmilesParser()).parseSmiles("C=CC");
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(molecule2);
		
        QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product);
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule2,qAC));
		
		product = setOfReactions.getReaction(0).getProducts().getMolecule(1);
        /*[H+]*/
		molecule2 = (new SmilesParser()).parseSmiles("[H+]");
        
        qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product);
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule2,qAC));
	}

}
