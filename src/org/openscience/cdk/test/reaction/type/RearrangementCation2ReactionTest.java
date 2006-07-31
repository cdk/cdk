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
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.type.RearrangementCation2Reaction;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;


/**
 * TestSuite that runs a test for the RearrangementCation2ReactionTest.
 * Generalized Reaction: [A+]-B=C => A=B-[c+].
 *
 * @cdk.module test-reaction
 */
public class RearrangementCation2ReactionTest extends CDKTestCase {

	private IReactionProcess type;

	/**
	 * Constructror of the RearrangementCation2ReactionTest object
	 *
	 */
	public  RearrangementCation2ReactionTest() {
		type  = new RearrangementCation2Reaction();
	}
    
	public static Test suite() {
		return new TestSuite(RearrangementCation2ReactionTest.class);
	}
	/**
	 * A unit test suite for JUnit. Reaction: [C+]-C=C-C => C=C-[C+]-C
	 * Automatic sarch of the centre active.
	 *
	 * @return    The test suite
	 */
	public void testAutomaticSearchCentreActiveExample1() throws ClassNotFoundException, CDKException, java.lang.Exception {
		
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		/*[C+]-C=C-C*/
		IMolecule molecule = getMolecule1();
		setOfReactants.addMolecule(molecule);
		
		Object[] params = {Boolean.FALSE};
        type.setParameters(params);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        
        
        
        IMolecule product = setOfReactions.getReaction(0).getProducts().getMolecule(0);
        Assert.assertEquals(1, product.getAtom(2).getFormalCharge());
        Assert.assertEquals(0, product.getLonePairCount(molecule.getAtom(1)));
        
        /*C=C-[C+]-C*/
        IMolecule molecule2 = getMolecule2();
        QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product);
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule2,qAC));
        
       
	}
	/**
	 * A unit test suite for JUnit. Reaction: [C+]-C=C-C => C=C-[C+]-C
	 * Manually put of the centre active.
	 *
	 * @return    The test suite
	 */
	public void testManuallyPutCentreActiveExample1() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		/*[C+]-C=C-C*/
		IMolecule molecule = getMolecule1();
		setOfReactants.addMolecule(molecule);
		
		/*manually put the centre active*/
		molecule.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER,true);

		
        Object[] params = {Boolean.TRUE};
        type.setParameters(params);
        
        /* iniciate */
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        IMolecule product = setOfReactions.getReaction(0).getProducts().getMolecule(0);
        
        /*C=C-[C+]-C*/
        IMolecule molecule2 = getMolecule2();
        
        QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product);
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(molecule2,qAC));
	}
	/**
	 * A unit test suite for JUnit. Reaction: [C+]-C=C-C => C=C-[C+]-C
	 * Test of mapped between the reactant and product. Only is mapped the centre active.
	 *
	 * @return    The test suite
	 */
	public void testMappingExample1() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		/*[C+]-C=C-C*/
		IMolecule molecule = getMolecule1();
		setOfReactants.addMolecule(molecule);
		
		/*automatic search of the centre active*/
        Object[] params = {Boolean.FALSE};
        type.setParameters(params);
        
        /* iniciate */
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        IMolecule product = setOfReactions.getReaction(0).getProducts().getMolecule(0);

        Assert.assertEquals(5,setOfReactions.getReaction(0).getMappings().length);
        
        IAtom mappedProductA1 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtom(0));
        assertEquals(mappedProductA1, product.getAtom(0));
        IBond mappedProductB1 = (IBond)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getBond(0));
        assertEquals(mappedProductB1, product.getBond(0));
        mappedProductA1 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtom(1));
        assertEquals(mappedProductA1, product.getAtom(1));
        mappedProductB1 = (IBond)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getBond(1));
        assertEquals(mappedProductB1, product.getBond(1));
        mappedProductA1 = (IAtom)ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0), molecule.getAtom(2));
        assertEquals(mappedProductA1, product.getAtom(2));
	}
	/**
	 * get the molecule 1: [C+]-C=C-C
	 * 
	 * @return The IMolecule
	 */
	private IMolecule getMolecule1()throws ClassNotFoundException, CDKException, java.lang.Exception {
		Molecule molecule = (new SmilesParser()).parseSmiles("[C+]-C=C-C");
	    HydrogenAdder adder = new HydrogenAdder();
        adder.addImplicitHydrogensToSatisfyValency(molecule);
		
        return molecule;
	}
	/**
	 * get the molecule 2: C=C-[C+]-C
	 * 
	 * @return The IMolecule
	 */
	private IMolecule getMolecule2()throws ClassNotFoundException, CDKException, java.lang.Exception {
		Molecule molecule = (new SmilesParser()).parseSmiles("C=C-[C+]-C");
		HydrogenAdder adder = new HydrogenAdder();
        adder.addImplicitHydrogensToSatisfyValency(molecule);
        return molecule;
	}
}
