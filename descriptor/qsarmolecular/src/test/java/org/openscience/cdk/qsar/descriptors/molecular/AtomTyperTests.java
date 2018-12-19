package org.openscience.cdk.qsar.descriptors.molecular;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.descriptors.molecular.JPlogPDescriptor;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

public class AtomTyperTests {

	static SmilesParser parser = null;
	
	@Test
	public void testIsPolar() throws CDKException 
	{
		JPlogPDescriptor desc = new JPlogPDescriptor();
		assertTrue(desc.isPolar(new Atom("O")));
		assertTrue(desc.isPolar(new Atom("S")));
		assertTrue(desc.isPolar(new Atom("N")));
		assertTrue(desc.isPolar(new Atom("P")));
		assertFalse(desc.isPolar(new Atom("C")));
	}
	
	@Test
	public void testIsElectronWithdrawing() throws CDKException 
	{
		JPlogPDescriptor desc = new JPlogPDescriptor();
		assertTrue(desc.electronWithdrawing(new Atom("O")));
		assertTrue(desc.electronWithdrawing(new Atom("S")));
		assertTrue(desc.electronWithdrawing(new Atom("N")));
		assertTrue(desc.electronWithdrawing(new Atom("S")));
		assertTrue(desc.electronWithdrawing(new Atom("F")));
		assertTrue(desc.electronWithdrawing(new Atom("Cl")));
		assertTrue(desc.electronWithdrawing(new Atom("Br")));
		assertTrue(desc.electronWithdrawing(new Atom("I")));
		assertFalse(desc.electronWithdrawing(new Atom("C")));
	}
	
	@Test
	public void testNonHNeighbours() throws CDKException
	{
		JPlogPDescriptor desc = new JPlogPDescriptor();
		IAtomContainer molecule = parseSmiles("CC");
		IAtom atom = molecule.getAtom(1);
		assertEquals(1,desc.nonHNeighbours(atom));
		
		molecule = parseSmiles("C");
		atom = molecule.getAtom(0);
		assertEquals(0,desc.nonHNeighbours(atom));
		
		molecule = parseSmiles("C(C)C");
		atom = molecule.getAtom(0);
		assertEquals(2,desc.nonHNeighbours(atom));
		
		molecule = parseSmiles("C(C)(C)C");
		atom = molecule.getAtom(0);
		assertEquals(3,desc.nonHNeighbours(atom));
		
		molecule = parseSmiles("C(C)(C)(C)C");
		atom = molecule.getAtom(0);
		assertEquals(4,desc.nonHNeighbours(atom));
	}
	
	
	
	@Test
	public void testDoubleBondHetero() throws CDKException
	{
		JPlogPDescriptor desc = new JPlogPDescriptor();
		IAtomContainer molecule = parseSmiles("c1nnccc1");
		IAtom atom = molecule.getAtom(1);
		assertFalse(desc.doubleBondHetero(atom));
		
		molecule = parseSmiles("CC(=O)C");
		atom = molecule.getAtom(1);
		assertTrue(desc.doubleBondHetero(atom));
	}
	
	@Test
	public void testCarbonylConjugated() throws CDKException
	{
		JPlogPDescriptor desc = new JPlogPDescriptor();
		IAtomContainer molecule = parseSmiles("c1nnccc1");
		IAtom atom = molecule.getAtom(0);
		assertFalse(desc.carbonylConjugated(atom));
		
		molecule = parseSmiles("C=C=C");
		atom = molecule.getAtom(0);
		assertFalse(desc.carbonylConjugated(atom));
		
		molecule = parseSmiles("CC(=O)C");
		atom = molecule.getAtom(0);
		assertTrue(desc.carbonylConjugated(atom));
	}
	
	@Test
	public void testNextToAromatic() throws CDKException
	{
		JPlogPDescriptor desc = new JPlogPDescriptor();
		IAtomContainer molecule = parseSmiles("c1nnccc1");
		IAtom atom = molecule.getAtom(0);
		assertFalse(desc.nextToAromatic(atom));
		
		molecule = parseSmiles("C=C=C");
		atom = molecule.getAtom(0);
		assertFalse(desc.nextToAromatic(atom));
		
		molecule = parseSmiles("Nc1ccccc1");
		atom = molecule.getAtom(0);
		assertTrue(desc.nextToAromatic(atom));
	}
	
	@Test
	public void testGetPolarBondArray() throws CDKException
	{
		JPlogPDescriptor desc = new JPlogPDescriptor();
		IAtomContainer molecule = parseSmiles("c1nnccc1");
		IAtom atom = molecule.getAtom(0);
		assertEquals(1,desc.getPolarBondArray(atom)[1]);
		
		molecule = parseSmiles("CO");
		atom = molecule.getAtom(0);
		assertEquals(1,desc.getPolarBondArray(atom)[0]);
		
		molecule = parseSmiles("C=O");
		atom = molecule.getAtom(0);
		assertEquals(1,desc.getPolarBondArray(atom)[2]);
		
		molecule = parseSmiles("C#N");
		atom = molecule.getAtom(0);
		assertEquals(1,desc.getPolarBondArray(atom)[3]);
	}
	
	@Test
	public void testBoundTo() throws CDKException
	{
		JPlogPDescriptor desc = new JPlogPDescriptor();
		IAtomContainer molecule = parseSmiles("CO");
		IAtom atom = molecule.getAtom(0);
		assertTrue(desc.boundTo(atom,"O"));
		assertFalse(desc.boundTo(atom,"S"));
	}
	
	@Test
	public void testCheckAlphaCarbonyl() throws CDKException
	{
		JPlogPDescriptor desc = new JPlogPDescriptor();
		IAtomContainer molecule = parseSmiles("O=CN");
		IAtom atom = molecule.getAtom(0);
		assertTrue(desc.checkAlphaCarbonyl(atom,"N"));
		assertFalse(desc.checkAlphaCarbonyl(atom,"S"));
	}
	
	@Test
	public void testGetHydrogenSpecial() throws CDKException
	{
		JPlogPDescriptor desc = new JPlogPDescriptor();
		IAtomContainer molecule = parseSmiles("HO");
		IAtom atom = molecule.getAtom(0);
		assertEquals(50, desc.getHydrogenSpecial(atom)); // DD = 50
		
		molecule = parseSmiles("HCC=O");
		atom = molecule.getAtom(0);
		assertEquals(51, desc.getHydrogenSpecial(atom)); // DD = 51
		
		molecule = parseSmiles("HC");
		atom = molecule.getAtom(0);
		assertEquals(46, desc.getHydrogenSpecial(atom)); // DD = 46
		
		molecule = parseSmiles("HCF");
		atom = molecule.getAtom(0);
		assertEquals(47, desc.getHydrogenSpecial(atom)); // DD = 47
		
		molecule = parseSmiles("HC(F)F");
		atom = molecule.getAtom(0);
		assertEquals(48, desc.getHydrogenSpecial(atom)); // DD = 48
		
		molecule = parseSmiles("HC(F)(F)O");
		atom = molecule.getAtom(0);
		assertEquals(49, desc.getHydrogenSpecial(atom)); // DD = 49
		
		molecule = parseSmiles("HC=C");
		atom = molecule.getAtom(0);
		assertEquals(47, desc.getHydrogenSpecial(atom)); // DD = 47
		
		molecule = parseSmiles("HC(=C)O");
		atom = molecule.getAtom(0);
		assertEquals(48, desc.getHydrogenSpecial(atom)); // DD = 48
		
		molecule = parseSmiles("HC(=O)O");
		atom = molecule.getAtom(0);
		assertEquals(49, desc.getHydrogenSpecial(atom)); // DD = 49
		
		molecule = parseSmiles("HC#C");
		atom = molecule.getAtom(0);
		assertEquals(48, desc.getHydrogenSpecial(atom)); // DD = 48
		
		molecule = parseSmiles("HC#N");
		atom = molecule.getAtom(0);
		assertEquals(49, desc.getHydrogenSpecial(atom)); // DD = 49
	}
	
	@Test
	public void testGetDefaultSpecial() throws CDKException
	{
		JPlogPDescriptor desc = new JPlogPDescriptor();
		IAtomContainer molecule = parseSmiles("P(=O)(O)(O)C");
		IAtom atom = molecule.getAtom(0);
		assertEquals(3, desc.getDefaultSpecial(atom)); // DD = 03
		
		molecule = parseSmiles("o1cccc1");
		atom = molecule.getAtom(0);
		assertEquals(10, desc.getDefaultSpecial(atom)); // DD = 10
	}
	
	@Test
	public void testGetFluorineSpecial() throws CDKException
	{
		JPlogPDescriptor desc = new JPlogPDescriptor();
		IAtomContainer molecule = parseSmiles("FS");
		IAtom atom = molecule.getAtom(0);
		assertEquals(8, desc.getFluorineSpecial(atom)); // DD = 08
		
		molecule = parseSmiles("FB");
		atom = molecule.getAtom(0);
		assertEquals(9, desc.getFluorineSpecial(atom)); // DD = 09
		
		molecule = parseSmiles("FI");
		atom = molecule.getAtom(0);
		assertEquals(1, desc.getFluorineSpecial(atom)); // DD = 01
		
		molecule = parseSmiles("FC#C");
		atom = molecule.getAtom(0);
		assertEquals(2, desc.getFluorineSpecial(atom)); // DD = 02
		
		molecule = parseSmiles("FC=C");
		atom = molecule.getAtom(0);
		assertEquals(3, desc.getFluorineSpecial(atom)); // DD = 03
		
		molecule = parseSmiles("FC(C)(C)C");
		atom = molecule.getAtom(0);
		assertEquals(5, desc.getFluorineSpecial(atom)); // DD = 05
		
		molecule = parseSmiles("FC(F)(F)F");
		atom = molecule.getAtom(0);
		assertEquals(7, desc.getFluorineSpecial(atom)); // DD = 07
		
		molecule = parseSmiles("F(F)(F)F");
		atom = molecule.getAtom(0);
		assertEquals(99, desc.getFluorineSpecial(atom)); // DD = 99 Nonsense Fluorine
	}
	
	@Test
	public void testGetOxygenSpecial() throws CDKException
	{
		JPlogPDescriptor desc = new JPlogPDescriptor();
		IAtomContainer molecule = parseSmiles("ON");
		IAtom atom = molecule.getAtom(0);
		assertEquals(1, desc.getOxygenSpecial(atom)); // DD = 01
		
		molecule = parseSmiles("OS");
		atom = molecule.getAtom(0);
		assertEquals(2, desc.getOxygenSpecial(atom)); // DD = 02

		molecule = parseSmiles("OC");
		atom = molecule.getAtom(0);
		assertEquals(3, desc.getOxygenSpecial(atom)); // DD = 03

		molecule = parseSmiles("o1cccc1");
		atom = molecule.getAtom(0);
		assertEquals(8, desc.getOxygenSpecial(atom)); // DD = 08

		molecule = parseSmiles("O=N");
		atom = molecule.getAtom(0);
		assertEquals(4, desc.getOxygenSpecial(atom)); // DD = 04

		molecule = parseSmiles("O=S");
		atom = molecule.getAtom(0);
		assertEquals(5, desc.getOxygenSpecial(atom)); // DD = 05

		molecule = parseSmiles("O=CO");
		atom = molecule.getAtom(0);
		assertEquals(6, desc.getOxygenSpecial(atom)); // DD = 06

		molecule = parseSmiles("O=CN");
		atom = molecule.getAtom(0);
		assertEquals(9, desc.getOxygenSpecial(atom)); // DD = 09

		molecule = parseSmiles("O=CS");
		atom = molecule.getAtom(0);
		assertEquals(10, desc.getOxygenSpecial(atom)); // DD = 10

		molecule = parseSmiles("O=CC");
		atom = molecule.getAtom(0);
		assertEquals(7, desc.getOxygenSpecial(atom)); // DD = 07
	}
	
	@Test
	public void testGetNitrogenSpecial() throws CDKException
	{
		JPlogPDescriptor desc = new JPlogPDescriptor();
		IAtomContainer molecule = parseSmiles("[N+](C)(C)(C)C");
		IAtom atom = molecule.getAtom(0);
		assertEquals(9, desc.getNitrogenSpecial(atom)); // DD = 09
		
		molecule = parseSmiles("Nc1ccccc1");
		atom = molecule.getAtom(0);
		assertEquals(1, desc.getNitrogenSpecial(atom)); // DD = 01

		molecule = parseSmiles("NC=O");
		atom = molecule.getAtom(0);
		assertEquals(2, desc.getNitrogenSpecial(atom)); // DD = 02

		molecule = parseSmiles("[N+](=O)([O-])C");
		atom = molecule.getAtom(0);
		assertEquals(10, desc.getNitrogenSpecial(atom)); // DD = 10
		
		molecule = parseSmiles("NO");
		atom = molecule.getAtom(0);
		assertEquals(3, desc.getNitrogenSpecial(atom)); // DD = 03

		molecule = parseSmiles("NC");
		atom = molecule.getAtom(0);
		assertEquals(4, desc.getNitrogenSpecial(atom)); // DD = 04

		molecule = parseSmiles("n1ccccc1");
		atom = molecule.getAtom(0);
		assertEquals(5, desc.getNitrogenSpecial(atom)); // DD = 05

		molecule = parseSmiles("N=O");
		atom = molecule.getAtom(0);
		assertEquals(6, desc.getNitrogenSpecial(atom)); // DD = 06

		molecule = parseSmiles("N=C");
		atom = molecule.getAtom(0);
		assertEquals(7, desc.getNitrogenSpecial(atom)); // DD = 07

		molecule = parseSmiles("N#C");
		atom = molecule.getAtom(0);
		assertEquals(8, desc.getNitrogenSpecial(atom)); // DD = 08
	}
	
	@Test
	public void testGetCarbonSpecial() throws CDKException
	{
		JPlogPDescriptor desc = new JPlogPDescriptor();
		IAtomContainer molecule = parseSmiles("CC");
		IAtom atom = molecule.getAtom(0);
		assertEquals(2, desc.getCarbonSpecial(atom)); // DD = 02
		
		molecule = parseSmiles("CO");
		atom = molecule.getAtom(0);
		assertEquals(3, desc.getCarbonSpecial(atom)); // DD = 03
		
		molecule = parseSmiles("c1ncccc1");
		atom = molecule.getAtom(0);
		assertEquals(11, desc.getCarbonSpecial(atom)); // DD = 11
		
		molecule = parseSmiles("c1(O)ccccc1");
		atom = molecule.getAtom(0);
		assertEquals(5, desc.getCarbonSpecial(atom)); // DD = 05
		
		molecule = parseSmiles("c1(O)ncccc1");
		atom = molecule.getAtom(0);
		assertEquals(13, desc.getCarbonSpecial(atom)); // DD = 13
		
		molecule = parseSmiles("c1ccccc1");
		atom = molecule.getAtom(0);
		assertEquals(4, desc.getCarbonSpecial(atom)); // DD = 04
		
		molecule = parseSmiles("C=O");
		atom = molecule.getAtom(0);
		assertEquals(7, desc.getCarbonSpecial(atom)); // DD = 07
		
		molecule = parseSmiles("C(=C)O");
		atom = molecule.getAtom(0);
		assertEquals(8, desc.getCarbonSpecial(atom)); // DD = 08
		
		molecule = parseSmiles("C(=O)O");
		atom = molecule.getAtom(0);
		assertEquals(14, desc.getCarbonSpecial(atom)); // DD = 14
		
		molecule = parseSmiles("C=C");
		atom = molecule.getAtom(0);
		assertEquals(6, desc.getCarbonSpecial(atom)); // DD = 06
		
		molecule = parseSmiles("C#N");
		atom = molecule.getAtom(0);
		assertEquals(12, desc.getCarbonSpecial(atom)); // DD = 12
		
		molecule = parseSmiles("C(O)#C");
		atom = molecule.getAtom(0);
		assertEquals(10, desc.getCarbonSpecial(atom)); // DD = 10
		
		molecule = parseSmiles("C(O)#N");
		atom = molecule.getAtom(0);
		assertEquals(15, desc.getCarbonSpecial(atom)); // DD = 15
		
		molecule = parseSmiles("C#C");
		atom = molecule.getAtom(0);
		assertEquals(9, desc.getCarbonSpecial(atom)); // DD = 09
	}
	
	@Test
	public void testGetNumMoreElectronegativeThanCarbon() throws CDKException
	{
		JPlogPDescriptor desc = new JPlogPDescriptor();
		IAtomContainer molecule = parseSmiles("c1ncccc1");
		Aromaticity.cdkLegacy().apply(molecule);
		IAtom atom = molecule.getAtom(0);
		assertEquals(2.0,desc.getNumMoreElectronegativethanCarbon(atom),0.1);
		
		molecule = parseSmiles("CO");
		atom = molecule.getAtom(0);
		assertEquals(1.0,desc.getNumMoreElectronegativethanCarbon(atom),0.1);
		
		molecule = parseSmiles("C=O");
		atom = molecule.getAtom(0);
		assertEquals(2.0,desc.getNumMoreElectronegativethanCarbon(atom),0.1);
		
		molecule = parseSmiles("C#N");
		atom = molecule.getAtom(0);
		assertEquals(3.0,desc.getNumMoreElectronegativethanCarbon(atom),0.1);
	}
	
	
	@Test
	public void testDefaultSpecial() throws CDKException 
	{
		IAtomContainer molecule = parseSmiles("P(=O)(O)(O)O");
		JPlogPDescriptor desc = new JPlogPDescriptor();
		Map<Integer, Integer> holo = desc.getMappedHologram(molecule);
		assertEquals(1, holo.get(115404).intValue());

	}
	
	private static IAtomContainer parseSmiles(String smiles) throws CDKException {
		parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IAtomContainer molecule = parser.parseSmiles(smiles);
		AtomContainerManipulator.percieveAtomTypesAndConfigureUnsetProperties(molecule);
		AtomContainerManipulator.convertImplicitToExplicitHydrogens(molecule);
		Aromaticity.cdkLegacy().apply(molecule);
		return molecule;
	}

}
