/* $Revision$ $Author$ $Date$    
 * 
 * Copyright (C) 1997-2007  Egon Willighagen <egonw@users.sf.net>
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

import java.io.InputStream;
import java.math.BigInteger;
import java.util.BitSet;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.applications.swing.MoleculeViewer2D;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;

/**
 * @cdk.module test-standard
 */
public class FingerprinterTest extends CDKTestCase
{
	
	boolean standAlone = false;
	private static LoggingTool logger = new LoggingTool(FingerprinterTest.class);
	
	public FingerprinterTest(String name)
	{
		super(name);
	}

	
	public static Test suite() {
		return new TestSuite(FingerprinterTest.class);
	}

	/**
	 * @cdk.bug 706786
	 */
	public void testBug706786() throws java.lang.Exception
	{
		Molecule superstructure = null;
		Molecule substructure = null;
		/* We make a specifically substituted chromane here 
		 * as well as the pure chromane skeleton, which should
		 * be a substructure of the first.
		 */
		String filename = "data/mdl/bug706786-1.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLReader reader = new MDLReader(ins);
		superstructure = (Molecule) reader.read((ChemObject) new Molecule());
		filename = "data/mdl/bug706786-2.mol";
		ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		reader = new MDLReader(ins);
		substructure = (Molecule) reader.read((ChemObject) new Molecule());
		/* now we've read the two chromanes and we are going to check now
		 * whether the latter is likely to be a substructure of the first by
		 * using the fingerprinter.
		*/
		
		Fingerprinter fingerprinter = new Fingerprinter();
		
		BitSet superBS = fingerprinter.getFingerprint(superstructure);
		BitSet subBS = fingerprinter.getFingerprint(substructure);
		boolean isSubset = Fingerprinter.isSubset(superBS, subBS);

		if (standAlone)
		{
			System.out.println("BitString superstructure: " + superBS);
			System.out.println("BitString substructure: " + subBS);
			System.out.println("isSubset? " + isSubset);
		}
		assertTrue(isSubset);
	}
	
	/**
	 * @cdk.bug 853254
	 */
	public void testBug853254() throws java.lang.Exception
	{
		Molecule superstructure = null;
		Molecule substructure = null;
		/* We make a specifically substituted chromane here 
		 * as well as the pure chromane skeleton, which should
		 * be a substructure of the first.
		 */
		String filename = "data/mdl/bug853254-2.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLReader reader = new MDLReader(ins);
		superstructure = (Molecule) reader.read((ChemObject) new Molecule());
		//MoleculeViewer2D.display(superstructure, false, true);		
		filename = "data/mdl/bug853254-1.mol";
		ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		reader = new MDLReader(ins);
		substructure = (Molecule) reader.read((ChemObject) new Molecule());
		//MoleculeViewer2D.display(substructure, false, true);		
		/* now we've read the two and we are going to check now
		 * whether the latter is likely to be a substructure of the first by
		 * using the fingerprinter.
		*/
		
		Fingerprinter fingerprinter = new Fingerprinter();
		
		BitSet superBS = fingerprinter.getFingerprint(superstructure);
		BitSet subBS = fingerprinter.getFingerprint(substructure);
		boolean isSubset = Fingerprinter.isSubset(superBS, subBS);

		if (standAlone)
		{
			System.out.println("BitString superstructure: " + superBS);
			System.out.println("BitString substructure: " + subBS);
			System.out.println("isSubset? " + isSubset);
		}
		//Fingerprinter.listDifferences(superBS, subBS);
		assertTrue(isSubset);
	}
	
	
	
	/**
	 * Problems with different aromaticity concepts.
	 * 
	 * @cdk.bug 771485
	 */
	public void testBug771485() throws java.lang.Exception
	{
		Molecule structure1 = null;
		Molecule structure2 = null;
		/* We make a specifically substituted chromane here 
		 * as well as the pure chromane skeleton, which should
		 * be a substructure of the first.
		 */
		String filename = "data/mdl/bug771485-1.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLReader reader = new MDLReader(ins);
		structure1 = (Molecule) reader.read((ChemObject) new Molecule());
		filename = "data/mdl/bug771485-2.mol";
		ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		reader = new MDLReader(ins);
		structure2 = (Molecule) reader.read((ChemObject) new Molecule());
		/* now we've read the two chromanes and we are going to check now
		 * whether the latter is likely to be a substructure of the first by
		 * using the fingerprinter.
		*/
		
		Fingerprinter fingerprinter = new Fingerprinter();
		
		BitSet superBS = fingerprinter.getFingerprint(structure2);
		BitSet subBS = fingerprinter.getFingerprint(structure1);
		boolean isSubset = Fingerprinter.isSubset(superBS, subBS);
		if (standAlone)
		{
			MoleculeViewer2D.display(structure1, false);
			MoleculeViewer2D.display(structure1, false);

			System.out.println("BitString 1: " + superBS);
			System.out.println("BitString 2: " + subBS);
			System.out.println("isSubset? " + isSubset);
		}
		assertTrue(isSubset);
	}

	/**
	 * Fingerprint not subset.
	 * 
	 * @cdk.bug 934819
	 */
	public void testBug934819() throws java.lang.Exception
	{
		Molecule superstructure = null;
		Molecule substructure = null;
		/* We make a specifically substituted chromane here 
		 * as well as the pure chromane skeleton, which should
		 * be a substructure of the first.
		 */
		String filename = "data/mdl/bug934819-1.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLReader reader = new MDLReader(ins);
		substructure = (Molecule) reader.read((ChemObject) new Molecule());
		filename = "data/mdl/bug934819-2.mol";
		ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		reader = new MDLReader(ins);
		superstructure = (Molecule) reader.read((ChemObject) new Molecule());
		/* now we've read the two molecules and we are going to check now
		 * whether the latter is likely to be a substructure of the first by
		 * using the fingerprinter.
		*/
		
		Fingerprinter fingerprinter = new Fingerprinter();
		
		BitSet superBS = fingerprinter.getFingerprint(superstructure);
		BitSet subBS = fingerprinter.getFingerprint(substructure);
		boolean isSubset = Fingerprinter.isSubset(superBS, subBS);
		if (standAlone)
		{
			//MoleculeViewer2D.display(superstructure, false);
			//MoleculeViewer2D.display(substructure, false);

			logger.debug("BitString 1: " + superBS);
			logger.debug("BitString 2: " + subBS);
			logger.debug("isSubset? " + isSubset);
		}
		assertTrue(isSubset);
	}


	/**
	 * Fingerprinter gives different fingerprints for same molecule.
	 * 
	 * @cdk.bug 931608
	 * @cdk.bug 934819
	 */
	public void testBug931608() throws java.lang.Exception
	{
		Molecule structure1 = null;
		Molecule structure2 = null;
		/* We make a specifically substituted chromane here 
		 * as well as the pure chromane skeleton, which should
		 * be a substructure of the first.
		 */
		String filename = "data/mdl/bug931608-1.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLReader reader = new MDLReader(ins);
		structure1 = (Molecule) reader.read((ChemObject) new Molecule());
		filename = "data/mdl/bug931608-2.mol";
		ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		reader = new MDLReader(ins);
		structure2 = (Molecule) reader.read((ChemObject) new Molecule());
		/* now we've read the two molecules and we are going to check now
		 * whether the two give the same bitstring.
		*/
		
		Fingerprinter fingerprinter = new Fingerprinter();
		
		BitSet bs1 = fingerprinter.getFingerprint(structure1);
		BitSet bs2 = fingerprinter.getFingerprint(structure2);
		// now we do the boolean XOR on the two bitsets, leading
		// to a bitset that has all the bits set to "true" which differ
		// between the two original bitsets
		bs1.xor(bs2);
		// cardinality gives us the number of "true" bits in the 
		// result of the XOR operation.
		int cardinality = bs1.cardinality();
		if (standAlone)
		{
			//MoleculeViewer2D.display(structure1, false);
			//MoleculeViewer2D.display(structure1, false);

			logger.debug("differing bits: " + bs1);
			logger.debug("number of differing bits: " + cardinality);
		}
		assertEquals(0, cardinality);
	}


	public void testFingerprinter() throws java.lang.Exception
	{
		Fingerprinter fingerprinter = new Fingerprinter();
		
		Molecule mol = MoleculeFactory.makeIndole();
		BitSet bs = fingerprinter.getFingerprint(mol);
		Molecule frag1 = MoleculeFactory.makePyrrole();
		BitSet bs1 = fingerprinter.getFingerprint(frag1);
		assertTrue(Fingerprinter.isSubset(bs, bs1));
	}
	
	public void testFingerprinterArguments() throws java.lang.Exception
	{
		Fingerprinter fingerprinter = new Fingerprinter(1024,7);
		
		Molecule mol = MoleculeFactory.makeIndole();
		BitSet bs = fingerprinter.getFingerprint(mol);
		Molecule frag1 = MoleculeFactory.makePyrrole();
		BitSet bs1 = fingerprinter.getFingerprint(frag1);
		assertTrue(Fingerprinter.isSubset(bs, bs1));
	}
	
	public static Molecule makeFragment1()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 0
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4
		mol.addAtom(new Atom("C")); // 5
		mol.addAtom(new Atom("C")); // 6
				
		mol.addBond(0, 1, 1); // 1
		mol.addBond(0, 2, 1); // 2
		mol.addBond(0, 3, 1); // 3
		mol.addBond(0, 4, 1); // 4
		mol.addBond(3, 5, 1); // 5
		mol.addBond(5, 6, 2); // 6
		return mol;
	}


	public static Molecule makeFragment4()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 0
		mol.addAtom(new Atom("C")); // 1
				
		mol.addBond(0, 1, 1); // 1
		return mol;
	}

	public static Molecule makeFragment2()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 0
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("S")); // 3
		mol.addAtom(new Atom("O")); // 4
		mol.addAtom(new Atom("C")); // 5
		mol.addAtom(new Atom("C")); // 6
				
		mol.addBond(0, 1, 2); // 1
		mol.addBond(0, 2, 1); // 2
		mol.addBond(0, 3, 1); // 3
		mol.addBond(0, 4, 1); // 4
		mol.addBond(3, 5, 1); // 5
		mol.addBond(5, 6, 2); // 6
		mol.addBond(5, 6, 2); // 7
		return mol;
	}
	
	public static Molecule makeFragment3()
	{
		Molecule mol = new Molecule();
		mol.addAtom(new Atom("C")); // 0
		mol.addAtom(new Atom("C")); // 1
		mol.addAtom(new Atom("C")); // 2
		mol.addAtom(new Atom("C")); // 3
		mol.addAtom(new Atom("C")); // 4
		mol.addAtom(new Atom("C")); // 5
		mol.addAtom(new Atom("C")); // 6
				
		mol.addBond(0, 1, 1); // 1
		mol.addBond(0, 2, 1); // 2
		mol.addBond(0, 3, 1); // 3
		mol.addBond(0, 4, 1); // 4
		mol.addBond(3, 5, 2); // 5
		mol.addBond(5, 6, 1); // 6
		return mol;
	}

	public static void main(String[] args) throws Exception
	{
		BigInteger bi=new BigInteger("0");
		bi=bi.add(BigInteger.valueOf((long) Math.pow(2, 63)));
		System.err.println(bi.toString());
		bi=bi.add(BigInteger.valueOf((long) Math.pow(2, 0)));
		System.err.println(bi.toString());
		FingerprinterTest fpt = new FingerprinterTest("FingerprinterTest");
		fpt.standAlone = true;
		//fpt.testFingerprinter();
		//fpt.testFingerprinterArguments();
		//fpt.testBug706786();
		//fpt.testBug771485();
		//fpt.testBug853254();
		//fpt.testBug931608();
		fpt.testBug934819();
	}
}

