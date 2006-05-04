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
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.type.DisplacementChargeReaction;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;

/**
 * TestSuite that runs a test for the DisplacementChargeReactionTest.
 * Generalized Reaction: A=B => [A-]-B[+] or A=B => [A+]-B[-].
 *
 * @cdk.module test-reaction
 */
public class DisplacementChargeReactionTest extends CDKTestCase {
	
	private IReactionProcess type;

	/**
	 * Constructror of the DisplacementChargeReactionTest object
	 *
	 */
	public  DisplacementChargeReactionTest() {
		type  = new DisplacementChargeReaction();
	}
    
	public static Test suite() {
		return new TestSuite(DisplacementChargeReactionTest.class);
	}
	/**
	 * A unit test suite for JUnit. Reaction: C=C => [C+]-[C-] + [C-]-[C+]
	 * Automatic sarch of the centre active.
	 *
	 * @return    The test suite
	 */
	public void testAutomaticSearchCentreActiveExample1() throws ClassNotFoundException, CDKException, java.lang.Exception {
		ISetOfMolecules setOfReactants = DefaultChemObjectBuilder.getInstance().newSetOfMolecules();
		
		/*C=C*/
		Molecule molecule = (new SmilesParser()).parseSmiles("C=C");
	    HydrogenAdder adder = new HydrogenAdder();
        adder.addImplicitHydrogensToSatisfyValency(molecule);
		setOfReactants.addMolecule(molecule);
		
		/*automatic search of the centre active*/
        Object[] params = {Boolean.FALSE};
        type.setParameters(params);
        
        /* iniciate */
        ISetOfReactions setOfReactions = type.initiate(setOfReactants, null);
        
        Assert.assertEquals(2, setOfReactions.getReactionCount());
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());
        Assert.assertEquals(1, setOfReactions.getReaction(1).getProductCount());

        IMolecule product = setOfReactions.getReaction(0).getProducts().getMolecule(0);
        
        /*[C+]-[C-]*/
		Molecule molecule2 = (new SmilesParser()).parseSmiles("[C+]-[C-]");
	    adder = new HydrogenAdder();
        adder.addImplicitHydrogensToSatisfyValency(molecule2);
		setOfReactants.addMolecule(molecule2);
		
		/*[C-]-[C+]*/
		Molecule molecule3 = (new SmilesParser()).parseSmiles("[C-]-[C+]");
	    adder = new HydrogenAdder();
        adder.addImplicitHydrogensToSatisfyValency(molecule3);
		setOfReactants.addMolecule(molecule3);
        
        QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product);
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule2,qAC));
	}
	/**
	 * A unit test suite for JUnit. Reaction: C=C => [C+]-[C-] + [C-]-[C+]
	 * Manually put of the centre active.
	 *
	 * @return    The test suite
	 */
	public void testManuallyPutCentreActiveExample1() throws ClassNotFoundException, CDKException, java.lang.Exception {
		ISetOfMolecules setOfReactants = DefaultChemObjectBuilder.getInstance().newSetOfMolecules();
		/*C=C*/
		Molecule molecule = (new SmilesParser()).parseSmiles("C=C");
	    HydrogenAdder adder = new HydrogenAdder();
        adder.addImplicitHydrogensToSatisfyValency(molecule);
		setOfReactants.addMolecule(molecule);
		
		/*manually put the centre active*/
		molecule.getAtomAt(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtomAt(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBondAt(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		
        Object[] params = {Boolean.TRUE};
        type.setParameters(params);
        
        /* iniciate */
        ISetOfReactions setOfReactions = type.initiate(setOfReactants, null);
        
        Assert.assertEquals(2, setOfReactions.getReactionCount());
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());
        Assert.assertEquals(1, setOfReactions.getReaction(1).getProductCount());

        
        		
	}
	/**
	 * A unit test suite for JUnit. Reaction: C=C => [C+]-[C-] + [C-]-[C+]
	 * Test of mapped between the reactant and product. Only is mapped the centre active.
	 *
	 * @return    The test suite
	 */
	public void testMappingExample1() throws ClassNotFoundException, CDKException, java.lang.Exception {
		ISetOfMolecules setOfReactants = DefaultChemObjectBuilder.getInstance().newSetOfMolecules();
		/*C=C*/
		Molecule molecule = (new SmilesParser()).parseSmiles("C=C");
	    HydrogenAdder adder = new HydrogenAdder();
        adder.addImplicitHydrogensToSatisfyValency(molecule);
		setOfReactants.addMolecule(molecule);
		
		/*automatic search of the centre active*/
        Object[] params = {Boolean.FALSE};
        type.setParameters(params);
        
        /* iniciate */
        ISetOfReactions setOfReactions = type.initiate(setOfReactants, null);
        
        IMolecule product = setOfReactions.getReaction(0).getProducts().getMolecule(0);

        Assert.assertEquals(3,setOfReactions.getReaction(0).getMappings().length);
        IAtom mappedProductA1 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtomAt(0));
        assertEquals(mappedProductA1, product.getAtomAt(0));
        IBond mappedProductB1 = (IBond)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getBondAt(0));
        assertEquals(mappedProductB1, product.getBondAt(0));
        IAtom mappedProductA2 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtomAt(1));
        assertEquals(mappedProductA2, product.getAtomAt(1));
	}
	
}
