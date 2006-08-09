package org.openscience.cdk.test.tools;


import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.SingleElectron;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.StructureResonanceGenerator;

/**
* TestSuite that runs all QSAR tests.
*
* @cdk.module test-extra
*/
public class StructureResonanceGeneratorTest  extends CDKTestCase
{

	private StructureResonanceGenerator gR;

	/**
	 *  Constructor for the StructureResonanceGeneratorTest object
	 *
	 *@param  name  Description of the Parameter
	 */
	public StructureResonanceGeneratorTest(String name)
	{
		super(name);
	}

    /**
    *  The JUnit setup method
    */
    public void setUp() {
        try {
        	gR = new StructureResonanceGenerator();
        } catch (Exception e) {
            fail();
        }
    }

	/**
	 * A unit test suite for JUnit
	 *
	 * @return    The test suite
	 */
    public static Test suite() {
        TestSuite suite = new TestSuite(StructureResonanceGeneratorTest.class);
        return suite;
	}
    
    /**
	 * <p>A unit test suite for JUnit: Resonance - CC(=[O*+])C=O</p>
	 * <p>CC(=[O*+])C=O <=> C[C+]([O*])C=O <=> CC([O*])=CO <=> CC(=O)[C*][O+] <=> CC(=O)C=[O*+]</p>
	 *
	 * @return    The test suite
	 */
	public void testGetAllStructures() throws ClassNotFoundException, CDKException, java.lang.Exception {
		Molecule molecule = (new SmilesParser()).parseSmiles("CC(=O)C=O");
	    HydrogenAdder adder = new HydrogenAdder();
        adder.addExplicitHydrogensToSatisfyValency(molecule);
        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
        lpcheck.newSaturate(molecule);
		
        IAtom atom =  molecule.getAtom(2);
        molecule.addElectronContainer(new SingleElectron(atom));
        atom.setFormalCharge(1);
        ILonePair[] selectron = molecule.getLonePairs(atom);
		molecule.removeElectronContainer(selectron[0]);

		StructureResonanceGenerator gRI = new StructureResonanceGenerator(true,true,true,true,false,false);
		IAtomContainerSet setOfMolecules = gRI.getAllStructures(molecule);

		Assert.assertEquals(8,setOfMolecules.getAtomContainerCount());
		
		/*1*/
        Molecule molecule1 = (new SmilesParser()).parseSmiles("C[C+](O)C=O");
        for(int i = 0; i < 4; i++)
			molecule1.addAtom(new Atom("H"));
		molecule1.addBond(0, 5, 1);
	    molecule1.addBond(0, 6, 1);
	    molecule1.addBond(0, 7, 1);
	    molecule1.addBond(3, 8, 1);
        lpcheck.newSaturate(molecule1);
        IAtom atom1 =  molecule1.getAtom(2);
        molecule1.addElectronContainer(new SingleElectron(atom1));
        QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(molecule1);
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(setOfMolecules.getAtomContainer(1),qAC));
		
//		/*2*/
//		Molecule molecule2 = (new SmilesParser()).parseSmiles("CC(O)=CO");
//		for(int i = 0; i < 4; i++)
//			molecule2.addAtom(new Atom("H"));
//		molecule2.addBond(0, 5, 1);
//	    molecule2.addBond(0, 6, 1);
//	    molecule2.addBond(0, 7, 1);
//	    molecule2.addBond(3, 8, 1);
//        lpcheck.newSaturate(molecule2);
//		IAtom atom2a =  molecule2.getAtom(2);
//		molecule2.addElectronContainer(new SingleElectron(atom2a));
//
//		IAtom atom2b =  molecule2.getAtom(4);
//		atom2b.setHydrogenCount(0);
//		atom2b.setFormalCharge(1);
//		
//		qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(molecule2);
//		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(setOfMolecules.getAtomContainer(3),qAC));
	}
	/**
	 * A unit test suite for JUnit: Resonance CC(=[O*+])C=O <=> CC(=O)C=[O*+]
	 *
	 * @return    The test suite
	 */
	public void testGetStructures1() throws ClassNotFoundException, CDKException, java.lang.Exception {
		Molecule molecule = (new SmilesParser()).parseSmiles("CC(=O)C=O");
	    HydrogenAdder adder = new HydrogenAdder();
        adder.addExplicitHydrogensToSatisfyValency(molecule);
        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
        lpcheck.newSaturate(molecule);
		
        IAtom atom =  molecule.getAtom(2);
        molecule.addElectronContainer(new SingleElectron(atom));
        atom.setFormalCharge(1);
        ILonePair[] selectron = molecule.getLonePairs(atom);
		molecule.removeElectronContainer(selectron[0]);

		StructureResonanceGenerator gRI = new StructureResonanceGenerator();
		IAtomContainerSet setOfMolecules = gRI.getStructures(molecule);

		Assert.assertEquals(2,setOfMolecules.getAtomContainerCount());
		
		Molecule molecule1 = (new SmilesParser()).parseSmiles("CC(=O)C=O");
		adder = new HydrogenAdder();
		adder.addExplicitHydrogensToSatisfyValency(molecule1);
		lpcheck.newSaturate(molecule1);
		IAtom atom1 =  molecule1.getAtom(4);
		molecule1.addElectronContainer(new SingleElectron(atom1));	
		selectron = molecule1.getLonePairs(atom1);
		molecule1.removeElectronContainer(selectron[0]);
		atom1.setFormalCharge(1);
		

		QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(molecule1);
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(setOfMolecules.getAtomContainer(1),qAC));
	
	}	
	/**
	 * A unit test suite for JUnit: Resonance CCC(=[O*+])C(C)=O <=> CCC(=O)C(C)=[O*+]
	 *
	 * @return    The test suite
	 */
	public void testGetStructures2() throws ClassNotFoundException, CDKException, java.lang.Exception {
		Molecule molecule = (new SmilesParser()).parseSmiles("CCC(=O)C(C)=O");
	    HydrogenAdder adder = new HydrogenAdder();
        adder.addExplicitHydrogensToSatisfyValency(molecule);
        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
        lpcheck.newSaturate(molecule);
        
        IAtom atom =  molecule.getAtom(3);
        molecule.addElectronContainer(new SingleElectron(atom));
        atom.setFormalCharge(1);
        ILonePair[] selectron = molecule.getLonePairs(atom);
		molecule.removeElectronContainer(selectron[0]);

		StructureResonanceGenerator gRI = new StructureResonanceGenerator();
		IAtomContainerSet setOfMolecules = gRI.getStructures(molecule);

		Assert.assertEquals(2,setOfMolecules.getAtomContainerCount());
		
		Molecule molecule1 = (new SmilesParser()).parseSmiles("CCC(=O)C(C)=O");
		adder = new HydrogenAdder();
		adder.addExplicitHydrogensToSatisfyValency(molecule1);
		lpcheck.newSaturate(molecule1);

        IAtom atom1 =  molecule1.getAtom(6);
        molecule1.addElectronContainer(new SingleElectron(atom1));
        atom1.setFormalCharge(1);
        selectron = molecule1.getLonePairs(atom1);
		molecule1.removeElectronContainer(selectron[0]);

		QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(molecule1);
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(setOfMolecules.getAtomContainer(1),qAC));
	
	}
	/**
	 * A unit test suite for JUnit: Resonance C-C=C-[C+]-C-C=C-[C+] <=> C-[C+]-C=C-C-C=C-[C+]
	 *
	 * @return    The test suite
	 */
	public void testFlagActiveCenter1() throws ClassNotFoundException, CDKException, java.lang.Exception {
		Molecule molecule = (new SmilesParser()).parseSmiles("CC=C[C+]-C-C=C-C=C");
        
        molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBond(2).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtom(3).setFlag(CDKConstants.REACTIVE_CENTER,true);
		
        StructureResonanceGenerator gRI = new StructureResonanceGenerator();
		IAtomContainerSet setOfMolecules = gRI.getStructures(molecule);
        
		Assert.assertEquals(2,setOfMolecules.getAtomContainerCount());

        Molecule molecule2 = (new SmilesParser()).parseSmiles("C-[C+]-C=C-C-C=C-C=C");
        
        QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(molecule2);
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(setOfMolecules.getAtomContainer(1),qAC));
	}
	/**
	 * A unit test suite for JUnit: Resonance C-C=C-[C-] <=> C=C-[C-]-C
	 *
	 * @return    The test suite
	 */
	public void testFlagActiveCenter2() throws ClassNotFoundException, CDKException, java.lang.Exception {
		Molecule molecule = (new SmilesParser()).parseSmiles("C-C=C-[C-]");

        IAtomContainerSet setOfMolecules = gR.getStructures(molecule);
        
		Assert.assertEquals(2,setOfMolecules.getAtomContainerCount());

        Molecule molecule2 = (new SmilesParser()).parseSmiles("C=C-[C-]-C");
        
        QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(molecule2);
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(setOfMolecules.getAtomContainer(1),qAC));
	}
	/**
	 * A unit test suite for JUnit: Resonance Formic acid  C(=O)O <=> [C-](-[O+])O <=> [C+](-[O-])O <=> C([O-])=[O+]
	 *
	 * @return    The test suite
	 */
	public void testResonanceFormicAcid() throws ClassNotFoundException, CDKException, java.lang.Exception {
		Molecule molecule = (new SmilesParser()).parseSmiles("C(=O)O");
	    HydrogenAdder adder = new HydrogenAdder();
        adder.addImplicitHydrogensToSatisfyValency(molecule);
        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
        lpcheck.newSaturate(molecule);
        
        IAtomContainerSet setOfMolecules = gR.getAllStructures(molecule);
        
		Assert.assertEquals(3,setOfMolecules.getAtomContainerCount());

//        Molecule molecule1 = (new SmilesParser()).parseSmiles("[C-](-[O+])O");
//        adder.addImplicitHydrogensToSatisfyValency(molecule1);
//        lpcheck.newSaturate(molecule1);
//        QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(molecule1);
//        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(setOfMolecules.getAtomContainer(1),qAC));

		Molecule molecule2 = (new SmilesParser()).parseSmiles("[C+](-[O-])O");
        adder.addImplicitHydrogensToSatisfyValency(molecule2);
        lpcheck.newSaturate(molecule2);
        QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(molecule2);
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(setOfMolecules.getAtomContainer(1),qAC));
        
        Molecule molecule3 = (new SmilesParser()).parseSmiles("C([O-])=[O+]");
        adder.addImplicitHydrogensToSatisfyValency(molecule3);
        lpcheck.newSaturate(molecule3);
        
        qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(molecule3);
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(setOfMolecules.getAtomContainer(2),qAC));
	}

	/**
	 * A unit test suite for JUnit: Resonance Formic acid  F-C=C <=> [F+]=C-[C-]
	 *
	 * @return    The test suite
	 */
	public void testResonanceFluoroethylene() throws ClassNotFoundException, CDKException, java.lang.Exception {
		Molecule molecule = (new SmilesParser()).parseSmiles("F-C=C");
	    HydrogenAdder adder = new HydrogenAdder();
        adder.addImplicitHydrogensToSatisfyValency(molecule);
        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
        lpcheck.newSaturate(molecule);
        
        IAtomContainerSet setOfMolecules = gR.getAllStructures(molecule);
        
		Assert.assertEquals(2,setOfMolecules.getAtomContainerCount());

        Molecule molecule1 = (new SmilesParser()).parseSmiles("[F+]=C-[C-]");
        adder.addImplicitHydrogensToSatisfyValency(molecule1);
        lpcheck.newSaturate(molecule1);
        QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(molecule1);
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(setOfMolecules.getAtomContainer(1),qAC));
	}
	/**
	 * A unit test suite for JUnit: Resonance Fluorobenzene  Fc1ccccc1 <=> ...
	 *
	 * @return    The test suite
	 */
	public void testResonanceFluorobenzene() throws ClassNotFoundException, CDKException, java.lang.Exception {
		Molecule molecule = (new SmilesParser()).parseSmiles("Fc1ccccc1");
	    HydrogenAdder adder = new HydrogenAdder();
        adder.addImplicitHydrogensToSatisfyValency(molecule);
        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
        lpcheck.newSaturate(molecule);
        
        StructureResonanceGenerator gRI = new StructureResonanceGenerator();
		IAtomContainerSet setOfMolecules = gRI.getAllStructures(molecule);
        
		Assert.assertEquals(4,setOfMolecules.getAtomContainerCount());

        Molecule molecule1 = (new SmilesParser()).parseSmiles("[F+]=C1C=CC=C[C-]1");
        adder.addImplicitHydrogensToSatisfyValency(molecule1);
        lpcheck.newSaturate(molecule1);
        QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(molecule1);
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(setOfMolecules.getAtomContainer(1),qAC));
        
        Molecule molecule2 = (new SmilesParser()).parseSmiles("[F+]=C1-C=C-[C-]-C=C1");
        adder.addImplicitHydrogensToSatisfyValency(molecule2);
        lpcheck.newSaturate(molecule2);
        qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(molecule2);
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(setOfMolecules.getAtomContainer(2),qAC));
	}
	/**
	 * A unit test suite for JUnit: Resonance   n1ccccc1 <=> ...
	 *
	 * @return    The test suite
	 */
	public void test_n1ccccc1() throws ClassNotFoundException, CDKException, java.lang.Exception {
		Molecule molecule = (new SmilesParser()).parseSmiles("n1ccccc1");
	    HydrogenAdder adder = new HydrogenAdder();
        adder.addImplicitHydrogensToSatisfyValency(molecule);
        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
        lpcheck.newSaturate(molecule);
        
        StructureResonanceGenerator gRI = new StructureResonanceGenerator();
		 IAtomContainerSet setOfMolecules = gRI.getAllStructures(molecule);
        
		Assert.assertEquals(10,setOfMolecules.getAtomContainerCount());
	}
}
