/* Copyright (C) 2000-2007  Christoph Steinbeck 
 *               2001-2007,2009  Egon Willighagen 
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.modeling.builder3d;


import static junit.framework.Assert.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.HOSECodeGenerator;

/**
 * Checks the functionality of {@link #ForceFieldConfigurator}.
 * 
 * @author danielszisz
 * @version 01/04/2012
 * @module test-modelbuilder3d
 * 
 */
public class ForceFieldConfiguratorTest  {

	ForceFieldConfigurator forceFieldConfigurator = new ForceFieldConfigurator();
	
	/**
	 * @cdk.bug : ArrayIndexOutOfBoundsException because of wrong for loop
	*/
	@Test
	public void testCheckForceFieldType_String() {
		assertEquals(2, forceFieldConfigurator.getFfTypes().length);
		String validForceFieldType = "mm2";
		String invalidForceFieldType = "mmff2001";
		assertTrue(forceFieldConfigurator.checkForceFieldType(validForceFieldType));
		assertFalse(forceFieldConfigurator.checkForceFieldType(invalidForceFieldType));
		
	}
	
	@Test
	public void testSetForceFieldConfigurator_String() throws CDKException {
		String forceFieldName = "mmff94";
		forceFieldConfigurator.setForceFieldConfigurator(forceFieldName);
		List<IAtomType> mmff94AtomTypes = forceFieldConfigurator.getAtomTypes();
		assertNotNull(mmff94AtomTypes);
		IAtomType atomtype0 = mmff94AtomTypes.get(0);
		assertEquals("C", atomtype0.getAtomTypeName());
		IAtomType atomtype1 = mmff94AtomTypes.get(1);
		assertEquals("Csp2", atomtype1.getAtomTypeName());
		
		forceFieldName = "mm2";
		forceFieldConfigurator.setForceFieldConfigurator(forceFieldName);
		List<IAtomType> mm2AtomTypes = forceFieldConfigurator.getAtomTypes();
		assertNotNull(mm2AtomTypes);
		IAtomType atomtype2 = mm2AtomTypes.get(2);
		assertEquals("C=", atomtype2.getAtomTypeName());
		IAtomType atomtype3 = mm2AtomTypes.get(3);
		assertEquals("Csp", atomtype3.getAtomTypeName());
		
	}
	
	@Test
	public void testSetMM2Parameters() throws CDKException {
		forceFieldConfigurator.setMM2Parameters();
		assertNotNull(forceFieldConfigurator.getParameterSet());
		List<IAtomType> atomtypeList = forceFieldConfigurator.getAtomTypes();
		IAtomType atomtype1 = atomtypeList.get(1);
		assertEquals("Csp2", atomtype1.getAtomTypeName());
		assertEquals(6, (int) atomtype1.getAtomicNumber());
		assertEquals(12, (int) atomtype1.getMassNumber());
	}
	
	@Test
	public void testSetMMFF94Parameters() throws Exception {
		forceFieldConfigurator.setMMFF94Parameters();
		assertNotNull(forceFieldConfigurator.getParameterSet());
		List<IAtomType> atomtypeList = forceFieldConfigurator.getAtomTypes();
		IAtomType atomtype4 = atomtypeList.get(4);
		assertEquals("CO2M", atomtype4.getAtomTypeName());
		assertEquals(6, (int) atomtype4.getAtomicNumber());
		assertEquals(4, (int) atomtype4.getFormalNeighbourCount());
		assertEquals(12, (int) atomtype4.getMassNumber());
		
	}
	
	@Test
	public void testRemoveAromaticityFlagsFromHoseCode_String() {
		String hosecode1 = "***HO*SE*CODE***";
		String cleanHoseCode = forceFieldConfigurator.removeAromaticityFlagsFromHoseCode(hosecode1);
		assertEquals("HOSECODE",cleanHoseCode);
		String hosecode2 = "HOSECODE";
		cleanHoseCode = forceFieldConfigurator.removeAromaticityFlagsFromHoseCode(hosecode2);
		assertEquals("HOSECODE", cleanHoseCode);
		
	}
	
	/**
	 * @cdk.bug  #3515122:  N atom type instead of NC=O
	 */
	@Test
	public void testConfigureMMFF94BasedAtom_IAtom_String_boolean_hydroxyurea() throws CDKException {
		String husmi = "NC(=O)NO";
		IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
		SmilesParser parser = new SmilesParser(builder);
		IAtomContainer hu = parser.parseSmiles(husmi);
		ForceFieldConfigurator ffc = new ForceFieldConfigurator();
		ffc.setForceFieldConfigurator("mmff94");
		IAtom N1= hu.getAtom(0);
		IAtom N2 = hu.getAtom(3);
		ffc.configureAtom(N1, 
				new HOSECodeGenerator().getHOSECode(hu, N1, 3),
				false);
		ffc.configureAtom(N2, 
				new HOSECodeGenerator().getHOSECode(hu, N2, 3),
				false);
		assertEquals("NC=O", N1.getAtomTypeName());
		assertEquals("N2OX", N2.getAtomTypeName());
			
	}
	
	@Test
	public void testConfigureMMFF94BasedAtom_IAtom_String_boolean_propanamide() throws CDKException {
		String pasmi = "NC(=O)CC";
		IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
		SmilesParser parser = new SmilesParser(builder);
		IAtomContainer pa = parser.parseSmiles(pasmi);
		ForceFieldConfigurator ffc = new ForceFieldConfigurator();
		ffc.setForceFieldConfigurator("mmff94");
		IAtom amideN = pa.getAtom(0);
		ffc.configureMMFF94BasedAtom(amideN, 
				new HOSECodeGenerator().getHOSECode(pa, amideN, 3), 
				false);
	    assertEquals("NC=O", amideN.getAtomTypeName());	
		
	}
	
	/**
	 * @cdk.bug  #3515122 : mmff94 atomtype N instead of NC=O
	 */
	@Test
	public void testConfigureMMFF94BasedAtom_IAtom_String_boolean_urea() throws CDKException {
		String usmi = "NC(N)=O";
		IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
		SmilesParser parser = new SmilesParser(builder);
		IAtomContainer urea = parser.parseSmiles(usmi);
		ForceFieldConfigurator ffc = new ForceFieldConfigurator();
		ffc.setForceFieldConfigurator("mmff94");
		IAtom amideN = urea.getAtom(0);
		ffc.configureMMFF94BasedAtom(amideN, 
				new HOSECodeGenerator().getHOSECode(urea, amideN, 3), 
				false);
//		System.err.println(amideN.getAtomTypeName());
		assertEquals("NC=O", amideN.getAtomTypeName());
		
	}
		
	/**
	 * @cdk.bug : bad atom types
	 */
	@Test 
	public void testAssignAtomTyps_test4_hydroxyurea() throws CDKException {
		String smiles = "C(=O)(NO)N";
	    String[] originalAtomTypes = {"C.sp2","O.sp2","N.amide","O.sp3","N.amide"}; 
        String[] expectedAtomTypes = {"C=","O=","NC=O","O","N2OX"};
    	IAtomContainer molecule = null;
    	String[] ffAtomTypes=null;
    	
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
		SmilesParser smilesParser = new SmilesParser(builder);
		molecule = smilesParser.parseSmiles(smiles);
		ffAtomTypes = new String[molecule.getAtomCount()];
		
		for(int i=0; i<molecule.getAtomCount(); i++) {
			assertEquals(originalAtomTypes[i],molecule.getAtom(i).getAtomTypeName());	
		     }
		forceFieldConfigurator.setForceFieldConfigurator("mmff94");	
		IRingSet moleculeRingSet = forceFieldConfigurator.assignAtomTyps(molecule);
		//no rings
		assertEquals(0, moleculeRingSet.getAtomContainerCount());
		for(int i=0; i< molecule.getAtomCount(); i++) {
			IAtom mmff94atom = molecule.getAtom(i);
			assertTrue(mmff94atom.getFlag(CDKConstants.ISALIPHATIC));
			ffAtomTypes[i] = mmff94atom.getAtomTypeName();
		 }
		 assertEquals(expectedAtomTypes, ffAtomTypes);
		
	}

}
