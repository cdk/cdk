/*
 * $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CKD) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 * 
 */

package org.openscience.cdk.test.fingerprint;


import org.openscience.cdk.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.fingerprint.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.templates.*;
import org.openscience.cdk.layout.*;

import javax.swing.JFrame;
import java.io.*;
import java.util.*;
import junit.framework.*;

public class FingerprinterTest extends TestCase
{
	
	boolean standAlone = false;
	
	public FingerprinterTest(String name)
	{
		super(name);
	}

	
	public static Test suite() {
		return new TestSuite(FingerprinterTest.class);
	}

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
		MDLReader reader = new MDLReader(new InputStreamReader(ins));
		superstructure = (Molecule) reader.read((ChemObject) new Molecule());
		filename = "data/mdl/bug706786-2.mol";
		ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		reader = new MDLReader(new InputStreamReader(ins));
		substructure = (Molecule) reader.read((ChemObject) new Molecule());
		/* now we've read the two chromanes and we are going to check now
		 * whether the latter is likely to be a substructure of the first by
		 * using the fingerprinter.
		*/
		
		BitSet superBS = Fingerprinter.getFingerprint(superstructure);
		BitSet subBS = Fingerprinter.getFingerprint(substructure);
		boolean isSubset = Fingerprinter.isSubset(superBS, subBS);

		if (standAlone)
		{
			System.out.println("BitString superstructure: " + superBS);
			System.out.println("BitString substructure: " + subBS);
			System.out.println("isSubset? " + isSubset);
		}
		assertTrue(isSubset);
	}
	
	/** This is a test for bug [ 771485 ] Problems with different aromaticity concepts */
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
		MDLReader reader = new MDLReader(new InputStreamReader(ins));
		structure1 = (Molecule) reader.read((ChemObject) new Molecule());
		filename = "data/mdl/bug771485-2.mol";
		ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		reader = new MDLReader(new InputStreamReader(ins));
		structure2 = (Molecule) reader.read((ChemObject) new Molecule());
		/* now we've read the two chromanes and we are going to check now
		 * whether the latter is likely to be a substructure of the first by
		 * using the fingerprinter.
		*/
		
		BitSet superBS = Fingerprinter.getFingerprint(structure1);
		BitSet subBS = Fingerprinter.getFingerprint(structure2);
		boolean isSubset = Fingerprinter.isSubset(superBS, subBS);
		MoleculeViewer2D.display(structure1, false);
		MoleculeViewer2D.display(structure1, false);

		if (standAlone)
		{
			System.out.println("BitString 1: " + superBS);
			System.out.println("BitString 2: " + subBS);
			System.out.println("isSubset? " + isSubset);
		}
		assertTrue(isSubset);
	}


	
	
	public void testFingerprinter() throws java.lang.Exception
	{
		Molecule mol = MoleculeFactory.makeIndole();
		BitSet bs = Fingerprinter.getFingerprint(mol);
		Molecule frag1 = MoleculeFactory.makePyrrole();
		BitSet bs1 = Fingerprinter.getFingerprint(frag1);
		if (!standAlone) assertTrue(Fingerprinter.isSubset(bs, bs1));
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

	public static void main(String[] args)
	{
		try{
			FingerprinterTest fpt = new FingerprinterTest("FingerprinterTest");
			fpt.standAlone = true;
			//fpt.testFingerprinter();	
			//fpt.testBug706786();
			fpt.testBug771485();
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
	}
}

