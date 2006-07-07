package org.openscience.cdk.test.tools;


import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.SingleElectron;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.ISetOfAtomContainers;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.StructureResonanceGenerator;
import org.openscience.cdk.tools.LonePairElectronChecker;

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
        adder.addImplicitHydrogensToSatisfyValency(molecule);
        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
        lpcheck.newSaturate(molecule);
		
        IAtom atom =  molecule.getAtomAt(2);
        molecule.addElectronContainer(new SingleElectron(atom));
        atom.setFormalCharge(1);
        ILonePair[] selectron = molecule.getLonePairs(atom);
		molecule.removeElectronContainer(selectron[0]);

		StructureResonanceGenerator gRI = new StructureResonanceGenerator(true,true,true,false,false);
		ISetOfAtomContainers setOfMolecules = gRI.getAllStructures(molecule);

		Assert.assertEquals(8,setOfMolecules.getAtomContainerCount());
       
		/*1*/
        Molecule molecule1 = (new SmilesParser()).parseSmiles("C[C+](O)C=O");
        adder = new HydrogenAdder();
        adder.addImplicitHydrogensToSatisfyValency(molecule1);
        lpcheck.newSaturate(molecule1);
        IAtom atom1 =  molecule1.getAtomAt(2);
        molecule1.addElectronContainer(new SingleElectron(atom1));
        atom1.setHydrogenCount(0);

		QueryAtomContainer qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(molecule1);
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(setOfMolecules.getAtomContainer(1),qAC));
		
		/*2*/
		Molecule molecule2 = (new SmilesParser()).parseSmiles("CC(O)=CO");
		adder = new HydrogenAdder();
		adder.addImplicitHydrogensToSatisfyValency(molecule2);
		lpcheck.newSaturate(molecule2);
		IAtom atom2a =  molecule2.getAtomAt(2);
		molecule2.addElectronContainer(new SingleElectron(atom2a));
		atom2a.setHydrogenCount(0);
		IAtom atom2b =  molecule2.getAtomAt(4);
		atom2b.setHydrogenCount(0);
		atom2b.setFormalCharge(1);

		qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(molecule2);
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(setOfMolecules.getAtomContainer(3),qAC));
		
		/*3*/
		Molecule molecule3 = (new SmilesParser()).parseSmiles("CC(=O)CO");
		adder = new HydrogenAdder();
		adder.addImplicitHydrogensToSatisfyValency(molecule3);
		lpcheck.newSaturate(molecule3);
		IAtom atom3a =  molecule3.getAtomAt(3);
		molecule3.addElectronContainer(new SingleElectron(atom3a));
		atom3a.setHydrogenCount(1);
		IAtom atom3b =  molecule3.getAtomAt(4);
		atom3b.setHydrogenCount(0);
		atom3b.setFormalCharge(1);

		qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(molecule3);
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(setOfMolecules.getAtomContainer(5),qAC));
		
		/*4*/
		Molecule molecule4 = (new SmilesParser()).parseSmiles("CC(=O)C=O");
		adder = new HydrogenAdder();
		adder.addImplicitHydrogensToSatisfyValency(molecule4);
		lpcheck.newSaturate(molecule4);
		IAtom atom4 =  molecule4.getAtomAt(4);
		molecule4.addElectronContainer(new SingleElectron(atom4));	
		selectron = molecule4.getLonePairs(atom4);
		molecule4.removeElectronContainer(selectron[0]);
		atom4.setFormalCharge(1);
		

		qAC = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(molecule4);
		Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(setOfMolecules.getAtomContainer(7),qAC));
	}
	/**
	 * A unit test suite for JUnit: Resonance CC(=[O*+])C=O <=> CC(=O)C=[O*+]
	 *
	 * @return    The test suite
	 */
	public void testGetStructures1() throws ClassNotFoundException, CDKException, java.lang.Exception {
		Molecule molecule = (new SmilesParser()).parseSmiles("CC(=O)C=O");
	    HydrogenAdder adder = new HydrogenAdder();
        adder.addImplicitHydrogensToSatisfyValency(molecule);
        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
        lpcheck.newSaturate(molecule);
		
        IAtom atom =  molecule.getAtomAt(2);
        molecule.addElectronContainer(new SingleElectron(atom));
        atom.setFormalCharge(1);
        ILonePair[] selectron = molecule.getLonePairs(atom);
		molecule.removeElectronContainer(selectron[0]);

		StructureResonanceGenerator gRI = new StructureResonanceGenerator(true,true,true,false,false);
		ISetOfAtomContainers setOfMolecules = gRI.getStructures(molecule);

		Assert.assertEquals(2,setOfMolecules.getAtomContainerCount());
		
		Molecule molecule1 = (new SmilesParser()).parseSmiles("CC(=O)C=O");
		adder = new HydrogenAdder();
		adder.addImplicitHydrogensToSatisfyValency(molecule1);
		lpcheck.newSaturate(molecule1);
		IAtom atom1 =  molecule1.getAtomAt(4);
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
        adder.addImplicitHydrogensToSatisfyValency(molecule);
        LonePairElectronChecker lpcheck = new LonePairElectronChecker();
        lpcheck.newSaturate(molecule);
        
        IAtom atom =  molecule.getAtomAt(3);
        molecule.addElectronContainer(new SingleElectron(atom));
        atom.setFormalCharge(1);
        ILonePair[] selectron = molecule.getLonePairs(atom);
		molecule.removeElectronContainer(selectron[0]);

		StructureResonanceGenerator gRI = new StructureResonanceGenerator(true,true,true,false,false);
		ISetOfAtomContainers setOfMolecules = gRI.getStructures(molecule);

		Assert.assertEquals(2,setOfMolecules.getAtomContainerCount());
		
		Molecule molecule1 = (new SmilesParser()).parseSmiles("CCC(=O)C(C)=O");
		adder = new HydrogenAdder();
		adder.addImplicitHydrogensToSatisfyValency(molecule1);
		lpcheck.newSaturate(molecule1);

        IAtom atom1 =  molecule1.getAtomAt(6);
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
	    HydrogenAdder adder = new HydrogenAdder();
        adder.addImplicitHydrogensToSatisfyValency(molecule);
        
        molecule.getAtomAt(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBondAt(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtomAt(2).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getBondAt(2).setFlag(CDKConstants.REACTIVE_CENTER,true);
		molecule.getAtomAt(3).setFlag(CDKConstants.REACTIVE_CENTER,true);
		
        StructureResonanceGenerator gRI = new StructureResonanceGenerator(true,true,true,true,true);
		ISetOfAtomContainers setOfMolecules = gRI.getStructures(molecule);
        
		Assert.assertEquals(2,setOfMolecules.getAtomContainerCount());

        Molecule molecule2 = (new SmilesParser()).parseSmiles("C-[C+]-C=C-C-C=C-C=C");
        adder.addImplicitHydrogensToSatisfyValency(molecule2);
        
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
	    HydrogenAdder adder = new HydrogenAdder();
        adder.addImplicitHydrogensToSatisfyValency(molecule);

        ISetOfAtomContainers setOfMolecules = gR.getStructures(molecule);
        
		Assert.assertEquals(2,setOfMolecules.getAtomContainerCount());

        Molecule molecule2 = (new SmilesParser()).parseSmiles("C=C-[C-]-C");
        adder.addImplicitHydrogensToSatisfyValency(molecule2);
        
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
        
        ISetOfAtomContainers setOfMolecules = gR.getAllStructures(molecule);
        
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
        
        ISetOfAtomContainers setOfMolecules = gR.getAllStructures(molecule);
        
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
        
        ISetOfAtomContainers setOfMolecules = gR.getAllStructures(molecule);
        
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
        
        ISetOfAtomContainers setOfMolecules = gR.getAllStructures(molecule);
        
		Assert.assertEquals(12,setOfMolecules.getAtomContainerCount());
	}
}
