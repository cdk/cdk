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
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.ISetOfReactions;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.type.DisplacementChargeFromDonorReaction;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;


/**
 * TestSuite that runs a test for the DisplacementChargeFromDonorReactionTest.
 * Generalized Reaction: X-A=B => [X+]=A-B[-].
 *
 * @cdk.module test-reaction
 */
public class DisplacementChargeFromDonorReactionTest extends CDKTestCase {
	
	private IReactionProcess type;

	/**
	 * Constructror of the DisplacementChargeFromDonorReactionTest object
	 *
	 */
	public  DisplacementChargeFromDonorReactionTest() {
		type  = new DisplacementChargeFromDonorReaction();
	}
    
	public static Test suite() {
		return new TestSuite(DisplacementChargeFromDonorReactionTest.class);
	}
	/**
	 * A unit test suite for JUnit. Reaction: O-C=C => [O+]=C-[C-]
	 * Automatic sarch of the centre active.
	 *
	 * @return    The test suite
	 */
	public void testAutomaticSearchCentreActiveEthenol() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newSetOfMolecules();
		
		/*O-C=C*/
		Molecule molecule = (new SmilesParser()).parseSmiles("O-C=C");
	    HydrogenAdder adder = new HydrogenAdder();
        adder.addImplicitHydrogensToSatisfyValency(molecule);
        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(molecule);
		setOfReactants.addMolecule(molecule);
		
		/*automatic search of the centre active*/
        Object[] params = {Boolean.FALSE};
        type.setParameters(params);
        
        /* iniciate */
        ISetOfReactions setOfReactions = type.initiate(setOfReactants, null);
        
        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        IMolecule product = setOfReactions.getReaction(0).getProducts().getMolecule(0);
        
        /*[O+]=C-[C+]*/
		Molecule molecule2 = (new SmilesParser()).parseSmiles("[O+]=C-[C-]");
	    adder = new HydrogenAdder();
        adder.addImplicitHydrogensToSatisfyValency(molecule2);
        lpcheck.newSaturate(molecule2);
		setOfReactants.addMolecule(molecule2);
		
        QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product);
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule2,qAC));
	}
	/**
	 * A unit test suite for JUnit. Reaction: O-C=C => [O+]=C-[C-]
	 * Manually put of the centre active.
	 *
	 * @return    The test suite
	 */
	public void testManuallyPutCentreActiveEthenol() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newSetOfMolecules();
		/*O-C=C*/
		Molecule molecule = (new SmilesParser()).parseSmiles("O-C=C");
	    HydrogenAdder adder = new HydrogenAdder();
        adder.addImplicitHydrogensToSatisfyValency(molecule);
        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(molecule);
		setOfReactants.addMolecule(molecule);
		
		/*manually put the centre active*/
		molecule.getAtomAt(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtomAt(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtomAt(2).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBondAt(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBondAt(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
		
        Object[] params = {Boolean.TRUE};
        type.setParameters(params);
        
        /* iniciate */
        ISetOfReactions setOfReactions = type.initiate(setOfReactants, null);
        
        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        
        		
	}
	/**
	 * A unit test suite for JUnit. Reaction: O-C=C => [O+]=C-[C-]
	 * Test of mapped between the reactant and product. Only is mapped the centre active.
	 *
	 * @return    The test suite
	 */
	public void testMappingEthenol() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newSetOfMolecules();
		/*O-C=C*/
		Molecule molecule = (new SmilesParser()).parseSmiles("O-C=C");
	    HydrogenAdder adder = new HydrogenAdder();
        adder.addImplicitHydrogensToSatisfyValency(molecule);
        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(molecule);
		setOfReactants.addMolecule(molecule);
		
		/*automatic search of the centre active*/
        Object[] params = {Boolean.FALSE};
        type.setParameters(params);
        
        /* iniciate */
        ISetOfReactions setOfReactions = type.initiate(setOfReactants, null);
        
        IMolecule product = setOfReactions.getReaction(0).getProducts().getMolecule(0);

        Assert.assertEquals(5,setOfReactions.getReaction(0).getMappings().length);
        IAtom mappedProductA1 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtomAt(0));
        assertEquals(mappedProductA1, product.getAtomAt(0));
        IBond mappedProductB1 = (IBond)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getBondAt(0));
        assertEquals(mappedProductB1, product.getBondAt(0));
        IAtom mappedProductA2 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtomAt(1));
        assertEquals(mappedProductA2, product.getAtomAt(1));
	}
	/**
	 * A unit test suite for JUnit. Reaction: C=N-C=C => {(no possible) C=[N+]=C-[C-]}
	 * Reaction: C=N-C=C => 
	 * Test of mapped between the reactant and product. Only is mapped the centre active.
	 *
	 * @return    The test suite
	 */
	public void testN() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newSetOfMolecules();
		/*O-C=C*/
		Molecule molecule = (new SmilesParser()).parseSmiles("C=N-C=C");
	    HydrogenAdder adder = new HydrogenAdder();
        adder.addImplicitHydrogensToSatisfyValency(molecule);
        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
		lpcheck.newSaturate(molecule);
		setOfReactants.addMolecule(molecule);
		
		/*automatic search of the centre active*/
        Object[] params = {Boolean.FALSE};
        type.setParameters(params);
        
        /* iniciate */
        ISetOfReactions setOfReactions = type.initiate(setOfReactants, null);
        
        
        Assert.assertEquals(0, setOfReactions.getReactionCount());

        
        
	}
}
