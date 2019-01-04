/* Copyright (C) 2018  Jeffrey Plante (Lhasa Limited)  <Jeffrey.Plante@lhasalimited.org>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
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
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

public class AtomTyperTests {

	static SmilesParser parser = null;
	
	@Test
	public void testIsPolar() throws CDKException 
	{
		JPlogPDescriptor desc = new JPlogPDescriptor();
		assertTrue(desc.jplogp.isPolar(new Atom("O")));
		assertTrue(desc.jplogp.isPolar(new Atom("S")));
		assertTrue(desc.jplogp.isPolar(new Atom("N")));
		assertTrue(desc.jplogp.isPolar(new Atom("P")));
		assertFalse(desc.jplogp.isPolar(new Atom("C")));
	}
	
	@Test
	public void testIsElectronWithdrawing() throws CDKException 
	{
		JPlogPDescriptor desc = new JPlogPDescriptor();
		assertTrue(desc.jplogp.electronWithdrawing(new Atom("O")));
		assertTrue(desc.jplogp.electronWithdrawing(new Atom("S")));
		assertTrue(desc.jplogp.electronWithdrawing(new Atom("N")));
		assertTrue(desc.jplogp.electronWithdrawing(new Atom("S")));
		assertTrue(desc.jplogp.electronWithdrawing(new Atom("F")));
		assertTrue(desc.jplogp.electronWithdrawing(new Atom("Cl")));
		assertTrue(desc.jplogp.electronWithdrawing(new Atom("Br")));
		assertTrue(desc.jplogp.electronWithdrawing(new Atom("I")));
		assertFalse(desc.jplogp.electronWithdrawing(new Atom("C")));
	}
	
	@Test
	public void testNonHNeighbours() throws CDKException
	{
		JPlogPDescriptor desc = new JPlogPDescriptor();
		IAtomContainer molecule = parseSmiles("CC");
		IAtom atom = molecule.getAtom(1);
		assertEquals(1,desc.jplogp.nonHNeighbours(atom));
		
		molecule = parseSmiles("C");
		atom = molecule.getAtom(0);
		assertEquals(0,desc.jplogp.nonHNeighbours(atom));
		
		molecule = parseSmiles("C(C)C");
		atom = molecule.getAtom(0);
		assertEquals(2,desc.jplogp.nonHNeighbours(atom));
		
		molecule = parseSmiles("C(C)(C)C");
		atom = molecule.getAtom(0);
		assertEquals(3,desc.jplogp.nonHNeighbours(atom));
		
		molecule = parseSmiles("C(C)(C)(C)C");
		atom = molecule.getAtom(0);
		assertEquals(4,desc.jplogp.nonHNeighbours(atom));
	}
	
	
	
	@Test
	public void testDoubleBondHetero() throws CDKException
	{
		JPlogPDescriptor desc = new JPlogPDescriptor();
		IAtomContainer molecule = parseSmiles("c1nnccc1");
		IAtom atom = molecule.getAtom(1);
		assertFalse(desc.jplogp.doubleBondHetero(atom));
		
		molecule = parseSmiles("CC(=O)C");
		atom = molecule.getAtom(1);
		assertTrue(desc.jplogp.doubleBondHetero(atom));
	}
	
	@Test
	public void testCarbonylConjugated() throws CDKException
	{
		JPlogPDescriptor desc = new JPlogPDescriptor();
		IAtomContainer molecule = parseSmiles("c1nnccc1");
		IAtom atom = molecule.getAtom(0);
		assertFalse(desc.jplogp.carbonylConjugated(atom));
		
		molecule = parseSmiles("C=C=C");
		atom = molecule.getAtom(0);
		assertFalse(desc.jplogp.carbonylConjugated(atom));
		
		molecule = parseSmiles("CC(=O)C");
		atom = molecule.getAtom(0);
		assertTrue(desc.jplogp.carbonylConjugated(atom));
	}
	
	@Test
	public void testNextToAromatic() throws CDKException
	{
		JPlogPDescriptor desc = new JPlogPDescriptor();
		IAtomContainer molecule = parseSmiles("c1nnccc1");
		IAtom atom = molecule.getAtom(0);
		assertFalse(desc.jplogp.nextToAromatic(atom));
		
		molecule = parseSmiles("C=C=C");
		atom = molecule.getAtom(0);
		assertFalse(desc.jplogp.nextToAromatic(atom));
		
		molecule = parseSmiles("Nc1ccccc1");
		atom = molecule.getAtom(0);
		assertTrue(desc.jplogp.nextToAromatic(atom));
	}
	
	@Test
	public void testGetPolarBondArray() throws CDKException
	{
		JPlogPDescriptor desc = new JPlogPDescriptor();
		IAtomContainer molecule = parseSmiles("c1nnccc1");
		IAtom atom = molecule.getAtom(0);
		assertEquals(1,desc.jplogp.getPolarBondArray(atom)[1]);
		
		molecule = parseSmiles("CO");
		atom = molecule.getAtom(0);
		assertEquals(1,desc.jplogp.getPolarBondArray(atom)[0]);
		
		molecule = parseSmiles("C=O");
		atom = molecule.getAtom(0);
		assertEquals(1,desc.jplogp.getPolarBondArray(atom)[2]);
		
		molecule = parseSmiles("C#N");
		atom = molecule.getAtom(0);
		assertEquals(1,desc.jplogp.getPolarBondArray(atom)[3]);
	}
	
	@Test
	public void testBoundTo() throws CDKException
	{
		JPlogPDescriptor desc = new JPlogPDescriptor();
		IAtomContainer molecule = parseSmiles("CO");
		IAtom atom = molecule.getAtom(0);
		assertTrue(desc.jplogp.boundTo(atom,"O"));
		assertFalse(desc.jplogp.boundTo(atom,"S"));
	}
	
	@Test
	public void testCheckAlphaCarbonyl() throws CDKException
	{
		JPlogPDescriptor desc = new JPlogPDescriptor();
		IAtomContainer molecule = parseSmiles("O=CN");
		IAtom atom = molecule.getAtom(0);
		assertTrue(desc.jplogp.checkAlphaCarbonyl(atom,"N"));
		assertFalse(desc.jplogp.checkAlphaCarbonyl(atom,"S"));
	}
	
	@Test
	public void testGetHydrogenSpecial() throws CDKException
	{
		JPlogPDescriptor desc = new JPlogPDescriptor();
		IAtomContainer molecule = parseSmiles("HO");
		IAtom atom = molecule.getAtom(0);
		assertEquals(50, desc.jplogp.getHydrogenSpecial(atom)); // DD = 50
		
		molecule = parseSmiles("HCC=O");
		atom = molecule.getAtom(0);
		assertEquals(51, desc.jplogp.getHydrogenSpecial(atom)); // DD = 51
		
		molecule = parseSmiles("HC");
		atom = molecule.getAtom(0);
		assertEquals(46, desc.jplogp.getHydrogenSpecial(atom)); // DD = 46
		
		molecule = parseSmiles("HCF");
		atom = molecule.getAtom(0);
		assertEquals(47, desc.jplogp.getHydrogenSpecial(atom)); // DD = 47
		
		molecule = parseSmiles("HC(F)F");
		atom = molecule.getAtom(0);
		assertEquals(48, desc.jplogp.getHydrogenSpecial(atom)); // DD = 48
		
		molecule = parseSmiles("HC(F)(F)O");
		atom = molecule.getAtom(0);
		assertEquals(49, desc.jplogp.getHydrogenSpecial(atom)); // DD = 49
		
		molecule = parseSmiles("HC=C");
		atom = molecule.getAtom(0);
		assertEquals(47, desc.jplogp.getHydrogenSpecial(atom)); // DD = 47
		
		molecule = parseSmiles("HC(=C)O");
		atom = molecule.getAtom(0);
		assertEquals(48, desc.jplogp.getHydrogenSpecial(atom)); // DD = 48
		
		molecule = parseSmiles("HC(=O)O");
		atom = molecule.getAtom(0);
		assertEquals(49, desc.jplogp.getHydrogenSpecial(atom)); // DD = 49
		
		molecule = parseSmiles("HC#C");
		atom = molecule.getAtom(0);
		assertEquals(48, desc.jplogp.getHydrogenSpecial(atom)); // DD = 48
		
		molecule = parseSmiles("HC#N");
		atom = molecule.getAtom(0);
		assertEquals(49, desc.jplogp.getHydrogenSpecial(atom)); // DD = 49
	}
	
	@Test
	public void testGetDefaultSpecial() throws CDKException
	{
		JPlogPDescriptor desc = new JPlogPDescriptor();
		IAtomContainer molecule = parseSmiles("P(=O)(O)(O)C");
		IAtom atom = molecule.getAtom(0);
		assertEquals(3, desc.jplogp.getDefaultSpecial(atom)); // DD = 03
		
		molecule = parseSmiles("o1cccc1");
		atom = molecule.getAtom(0);
		assertEquals(10, desc.jplogp.getDefaultSpecial(atom)); // DD = 10
	}
	
	@Test
	public void testGetFluorineSpecial() throws CDKException
	{
		JPlogPDescriptor desc = new JPlogPDescriptor();
		IAtomContainer molecule = parseSmiles("FS");
		IAtom atom = molecule.getAtom(0);
		assertEquals(8, desc.jplogp.getFluorineSpecial(atom)); // DD = 08
		
		molecule = parseSmiles("FB");
		atom = molecule.getAtom(0);
		assertEquals(9, desc.jplogp.getFluorineSpecial(atom)); // DD = 09
		
		molecule = parseSmiles("FI");
		atom = molecule.getAtom(0);
		assertEquals(1, desc.jplogp.getFluorineSpecial(atom)); // DD = 01
		
		molecule = parseSmiles("FC#C");
		atom = molecule.getAtom(0);
		assertEquals(2, desc.jplogp.getFluorineSpecial(atom)); // DD = 02
		
		molecule = parseSmiles("FC=C");
		atom = molecule.getAtom(0);
		assertEquals(3, desc.jplogp.getFluorineSpecial(atom)); // DD = 03
		
		molecule = parseSmiles("FC(C)(C)C");
		atom = molecule.getAtom(0);
		assertEquals(5, desc.jplogp.getFluorineSpecial(atom)); // DD = 05
		
		molecule = parseSmiles("FC(F)(F)F");
		atom = molecule.getAtom(0);
		assertEquals(7, desc.jplogp.getFluorineSpecial(atom)); // DD = 07
		
		molecule = parseSmiles("F(F)(F)F");
		atom = molecule.getAtom(0);
		assertEquals(99, desc.jplogp.getFluorineSpecial(atom)); // DD = 99 Nonsense Fluorine
	}
	
	@Test
	public void testGetOxygenSpecial() throws CDKException
	{
		JPlogPDescriptor desc = new JPlogPDescriptor();
		IAtomContainer molecule = parseSmiles("ON");
		IAtom atom = molecule.getAtom(0);
		assertEquals(1, desc.jplogp.getOxygenSpecial(atom)); // DD = 01
		
		molecule = parseSmiles("OS");
		atom = molecule.getAtom(0);
		assertEquals(2, desc.jplogp.getOxygenSpecial(atom)); // DD = 02

		molecule = parseSmiles("OC");
		atom = molecule.getAtom(0);
		assertEquals(3, desc.jplogp.getOxygenSpecial(atom)); // DD = 03

		molecule = parseSmiles("o1cccc1");
		atom = molecule.getAtom(0);
		assertEquals(8, desc.jplogp.getOxygenSpecial(atom)); // DD = 08

		molecule = parseSmiles("O=N");
		atom = molecule.getAtom(0);
		assertEquals(4, desc.jplogp.getOxygenSpecial(atom)); // DD = 04

		molecule = parseSmiles("O=S");
		atom = molecule.getAtom(0);
		assertEquals(5, desc.jplogp.getOxygenSpecial(atom)); // DD = 05

		molecule = parseSmiles("O=CO");
		atom = molecule.getAtom(0);
		assertEquals(6, desc.jplogp.getOxygenSpecial(atom)); // DD = 06

		molecule = parseSmiles("O=CN");
		atom = molecule.getAtom(0);
		assertEquals(9, desc.jplogp.getOxygenSpecial(atom)); // DD = 09

		molecule = parseSmiles("O=CS");
		atom = molecule.getAtom(0);
		assertEquals(10, desc.jplogp.getOxygenSpecial(atom)); // DD = 10

		molecule = parseSmiles("O=CC");
		atom = molecule.getAtom(0);
		assertEquals(7, desc.jplogp.getOxygenSpecial(atom)); // DD = 07
	}
	
	@Test
	public void testGetNitrogenSpecial() throws CDKException
	{
		JPlogPDescriptor desc = new JPlogPDescriptor();
		IAtomContainer molecule = parseSmiles("[N+](C)(C)(C)C");
		IAtom atom = molecule.getAtom(0);
		assertEquals(9, desc.jplogp.getNitrogenSpecial(atom)); // DD = 09
		
		molecule = parseSmiles("Nc1ccccc1");
		atom = molecule.getAtom(0);
		assertEquals(1, desc.jplogp.getNitrogenSpecial(atom)); // DD = 01

		molecule = parseSmiles("NC=O");
		atom = molecule.getAtom(0);
		assertEquals(2, desc.jplogp.getNitrogenSpecial(atom)); // DD = 02

		molecule = parseSmiles("[N+](=O)([O-])C");
		atom = molecule.getAtom(0);
		assertEquals(10, desc.jplogp.getNitrogenSpecial(atom)); // DD = 10
		
		molecule = parseSmiles("NO");
		atom = molecule.getAtom(0);
		assertEquals(3, desc.jplogp.getNitrogenSpecial(atom)); // DD = 03

		molecule = parseSmiles("NC");
		atom = molecule.getAtom(0);
		assertEquals(4, desc.jplogp.getNitrogenSpecial(atom)); // DD = 04

		molecule = parseSmiles("n1ccccc1");
		atom = molecule.getAtom(0);
		assertEquals(5, desc.jplogp.getNitrogenSpecial(atom)); // DD = 05

		molecule = parseSmiles("N=O");
		atom = molecule.getAtom(0);
		assertEquals(6, desc.jplogp.getNitrogenSpecial(atom)); // DD = 06

		molecule = parseSmiles("N=C");
		atom = molecule.getAtom(0);
		assertEquals(7, desc.jplogp.getNitrogenSpecial(atom)); // DD = 07

		molecule = parseSmiles("N#C");
		atom = molecule.getAtom(0);
		assertEquals(8, desc.jplogp.getNitrogenSpecial(atom)); // DD = 08
	}
	
	@Test
	public void testGetCarbonSpecial() throws CDKException
	{
		JPlogPDescriptor desc = new JPlogPDescriptor();
		IAtomContainer molecule = parseSmiles("CC");
		IAtom atom = molecule.getAtom(0);
		assertEquals(2, desc.jplogp.getCarbonSpecial(atom)); // DD = 02
		
		molecule = parseSmiles("CO");
		atom = molecule.getAtom(0);
		assertEquals(3, desc.jplogp.getCarbonSpecial(atom)); // DD = 03
		
		molecule = parseSmiles("c1ncccc1");
		atom = molecule.getAtom(0);
		assertEquals(11, desc.jplogp.getCarbonSpecial(atom)); // DD = 11
		
		molecule = parseSmiles("c1(O)ccccc1");
		atom = molecule.getAtom(0);
		assertEquals(5, desc.jplogp.getCarbonSpecial(atom)); // DD = 05
		
		molecule = parseSmiles("c1(O)ncccc1");
		atom = molecule.getAtom(0);
		assertEquals(13, desc.jplogp.getCarbonSpecial(atom)); // DD = 13
		
		molecule = parseSmiles("c1ccccc1");
		atom = molecule.getAtom(0);
		assertEquals(4, desc.jplogp.getCarbonSpecial(atom)); // DD = 04
		
		molecule = parseSmiles("C=O");
		atom = molecule.getAtom(0);
		assertEquals(7, desc.jplogp.getCarbonSpecial(atom)); // DD = 07
		
		molecule = parseSmiles("C(=C)O");
		atom = molecule.getAtom(0);
		assertEquals(8, desc.jplogp.getCarbonSpecial(atom)); // DD = 08
		
		molecule = parseSmiles("C(=O)O");
		atom = molecule.getAtom(0);
		assertEquals(14, desc.jplogp.getCarbonSpecial(atom)); // DD = 14
		
		molecule = parseSmiles("C=C");
		atom = molecule.getAtom(0);
		assertEquals(6, desc.jplogp.getCarbonSpecial(atom)); // DD = 06
		
		molecule = parseSmiles("C#N");
		atom = molecule.getAtom(0);
		assertEquals(12, desc.jplogp.getCarbonSpecial(atom)); // DD = 12
		
		molecule = parseSmiles("C(O)#C");
		atom = molecule.getAtom(0);
		assertEquals(10, desc.jplogp.getCarbonSpecial(atom)); // DD = 10
		
		molecule = parseSmiles("C(O)#N");
		atom = molecule.getAtom(0);
		assertEquals(15, desc.jplogp.getCarbonSpecial(atom)); // DD = 15
		
		molecule = parseSmiles("C#C");
		atom = molecule.getAtom(0);
		assertEquals(9, desc.jplogp.getCarbonSpecial(atom)); // DD = 09
	}
	
	@Test
	public void testGetNumMoreElectronegativeThanCarbon() throws CDKException
	{
		JPlogPDescriptor desc = new JPlogPDescriptor();
		IAtomContainer molecule = parseSmiles("c1ncccc1");
		Aromaticity.cdkLegacy().apply(molecule);
		IAtom atom = molecule.getAtom(0);
		assertEquals(2.0,desc.jplogp.getNumMoreElectronegativethanCarbon(atom),0.1);
		
		molecule = parseSmiles("CO");
		atom = molecule.getAtom(0);
		assertEquals(1.0,desc.jplogp.getNumMoreElectronegativethanCarbon(atom),0.1);
		
		molecule = parseSmiles("C=O");
		atom = molecule.getAtom(0);
		assertEquals(2.0,desc.jplogp.getNumMoreElectronegativethanCarbon(atom),0.1);
		
		molecule = parseSmiles("C#N");
		atom = molecule.getAtom(0);
		assertEquals(3.0,desc.jplogp.getNumMoreElectronegativethanCarbon(atom),0.1);
	}
	
	
	@Test
	public void testDefaultSpecial() throws CDKException 
	{
		IAtomContainer molecule = parseSmiles("P(=O)(O)(O)O");
		JPlogPDescriptor desc = new JPlogPDescriptor();
		Map<Integer, Integer> holo = desc.jplogp.getMappedHologram(molecule);
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
