/* 
 * $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2001  The JChemPaint project
 * 
 * Contact: steinbeck@ice.mpg.de
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

package org.openscience.cdk.test;


import org.openscience.cdk.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.fingerprint.*;
import org.openscience.cdk.io.*;
import java.io.*;
import java.util.*;
import junit.framework.*;

public class FingerprinterTest extends TestCase
{
	
	public FingerprinterTest(String name)
	{
		super(name);
	}

	
	public static Test suite() {
		return new TestSuite(FingerprinterTest.class);
	}

	public  void testFingerprinter()
	{
		Molecule mol = MoleculeFactory.makeAlphaPinene();
		BitSet bs = Fingerprinter.getFingerprint(mol);
		Molecule frag1 = makeFragment1();
		Molecule frag2 = makeFragment2();
		Molecule frag3 = makeFragment3();
		BitSet bs1 = Fingerprinter.getFingerprint(frag1);
		BitSet bs2 = Fingerprinter.getFingerprint(frag2);
		BitSet bs3 = Fingerprinter.getFingerprint(frag3);
		assert(Fingerprinter.isSubset(bs, bs1));
		assert(!Fingerprinter.isSubset(bs, bs2));
		assert(!Fingerprinter.isSubset(bs, bs3));
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
		FingerprinterTest fpt = new FingerprinterTest("FingerprinterTest");
		fpt.testFingerprinter();
	}
}
