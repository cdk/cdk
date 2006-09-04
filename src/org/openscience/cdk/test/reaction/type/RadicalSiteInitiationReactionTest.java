package org.openscience.cdk.test.reaction.type;


import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
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
import org.openscience.cdk.reaction.type.RadicalSiteInitiationReaction;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;

/**
 * TestSuite that runs a test for the RearrangementRadical2ReactionTest.
 * Generalized Reaction: [A*]-B-C => A=B + [c*].
 *
 * @cdk.module test-reaction
 */
public class RadicalSiteInitiationReactionTest extends CDKTestCase {
	
	private IReactionProcess type;
	/**
	 * Constructror of the RadicalSiteInitiationReactionTest object
	 *
	 */
	public  RadicalSiteInitiationReactionTest() {
		type  = new RadicalSiteInitiationReaction();
	}
    
	public static Test suite() {
		return new TestSuite(RadicalSiteInitiationReactionTest.class);
	}
	/**
	 * A unit test suite for JUnit. Reaction: [C*]-C-C => C=C +[C*]
	 * Automatic sarch of the centre active.
	 *
	 * @return    The test suite
	 */
	public void testAutomaticSearchCentreActiveExample1() throws ClassNotFoundException, CDKException, java.lang.Exception {
        
		/*[C*]-C-C*/
		Molecule molecule = (new SmilesParser()).parseSmiles("[C+]-C-C");
	    HydrogenAdder adder = new HydrogenAdder("org.openscience.cdk.tools.ValencyChecker");
        adder.addImplicitHydrogensToSatisfyValency(molecule);
        IAtom atom =  molecule.getAtom(0);
        molecule.addElectronContainer(new SingleElectron(atom));
        atom.setFormalCharge(0);
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		setOfReactants.addMolecule(molecule);
		
        Object[] params = {Boolean.FALSE};
        type.setParameters(params);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(2, setOfReactions.getReaction(0).getProductCount());

        
        IMolecule product = setOfReactions.getReaction(0).getProducts().getMolecule(0);
        SmilesGenerator sg = new SmilesGenerator(product.getBuilder());
		
        /*C=C*/
        Molecule molecule2 = (new SmilesParser()).parseSmiles("C=C");
        adder.addImplicitHydrogensToSatisfyValency(molecule2);
        QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product);
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule2,qAC));
		
		product = setOfReactions.getReaction(0).getProducts().getMolecule(1);
		
        /*[C*]*/
		molecule2 = (new SmilesParser()).parseSmiles("[C+]");
        adder.addImplicitHydrogensToSatisfyValency(molecule);
        atom =  molecule2.getAtom(0);
        molecule2.addElectronContainer(new SingleElectron(atom));
        atom.setFormalCharge(0);
        qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product);
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule2,qAC));
        
       
	}
}
