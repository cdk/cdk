/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2006  The Chemistry Development Kit (CKD) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
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
package org.openscience.cdk.test.fingerprint;

import java.util.BitSet;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.fingerprint.IFingerprinter;
import org.openscience.cdk.fingerprint.StandardSubstructureSets;
import org.openscience.cdk.fingerprint.SubstructureFingerprinter;
import org.openscience.cdk.interfaces.IAminoAcid;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.ISetOfAtomContainers;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.AminoAcids;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.manipulator.AminoAcidManipulator;

/**
 * @cdk.module test
 */
public class SubstructureFingerprinterTest extends CDKTestCase {
	
	public SubstructureFingerprinterTest(String name) {
		super(name);
	}
	
	public static Test suite() {
		return new TestSuite(SubstructureFingerprinterTest.class);
	}

	public void testFunctionalGroups() {
		BitSet bitset = null;
		ISetOfAtomContainers set = null;
		try {
			set = StandardSubstructureSets.getFunctionalGroupSubstructureSet();
			
			IFingerprinter printer = new SubstructureFingerprinter(set);
			IMolecule pinene = MoleculeFactory.makeAlphaPinene();
			bitset = printer.getFingerprint(pinene);
		} catch (Exception exception) {
			exception.printStackTrace();
			fail(exception.getMessage());
		}
		
		assertNotNull(set);
		assertNotNull(bitset);
		// none of the funtional groups is found in this molecule
		for (int i=0; i<set.getAtomContainerCount(); i++) {
			assertFalse(bitset.get(i));
		}
	}
	
	public void testFunctionalGroups_matchAll() {
		BitSet bitset = null;
		ISetOfAtomContainers set = null;
		try {
			set = StandardSubstructureSets.getFunctionalGroupSubstructureSet();
			
			IFingerprinter printer = new SubstructureFingerprinter(set);
			IMolecule matchesAll = new SmilesParser().parseSmiles("C(C(=O)O)C(N([H])[H])C(O[H])C(COC)C(C(=O)[H])C(S(=O)(=O)O)C(P(=O)(=O)O)");
			bitset = printer.getFingerprint(matchesAll);
			System.out.println("BitSet: " + bitset);
		} catch (Exception exception) {
			exception.printStackTrace();
			fail(exception.getMessage());
		}
		
		assertNotNull(set);
		assertNotNull(bitset);
		// all funtional groups are found in this molecule
		assertEquals(set.getAtomContainerCount(), bitset.cardinality());
	}
	
	public void testAminoAcids() {
		BitSet bitset = null;
		ISetOfAtomContainers set = null;
		try {
			set = StandardSubstructureSets.getFunctionalGroupSubstructureSet();
				
		    IAminoAcid[] aas = AminoAcids.createAAs();
			IFingerprinter printer = new SubstructureFingerprinter(set);

			assertNotNull(set);

			// test wether all molecules have an amine and carboxylic acid group
			for (int i=0; i<aas.length; i++) {
				AminoAcidManipulator.addAcidicOxygen(aas[i]);
				IMolecule aminoAcid = aas[i].getBuilder().newMolecule(aas[i]);
				HydrogenAdder hAdder = new HydrogenAdder();
				hAdder.addExplicitHydrogensToSatisfyValency(aminoAcid);
				
				assertNotNull(aminoAcid);
				bitset = printer.getFingerprint(aminoAcid);
				assertNotNull(bitset);
				System.out.println("AA: " + aas[i].getProperty(AminoAcids.RESIDUE_NAME));
				System.out.println(" -> " + bitset);
				assertTrue(bitset.get(0)); // carboxylic acid group
				if (!aas[i].getProperty(AminoAcids.RESIDUE_NAME).equals("PRO"))
					assertTrue(bitset.get(1)); // amine group
			}			
		} catch (Exception exception) {
			exception.printStackTrace();
			fail(exception.getMessage());
		}
		
	}
}

