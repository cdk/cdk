/* $Revision: 7657 $ $Author: egonw $ $Date: 2007-01-06 11:30:46 +0100 (Sat, 06 Jan 2007) $    
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

import java.util.BitSet;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.fingerprint.ExtendedFingerprinter;
import org.openscience.cdk.fingerprint.FingerprinterTool;
import org.openscience.cdk.fingerprint.IFingerprinter;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test-standard
 */
public class ExtendedFingerprinterTest extends CDKTestCase {
	
	public ExtendedFingerprinterTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(ExtendedFingerprinterTest.class);
	}

	public void testExtendedFingerprinter() throws java.lang.Exception {
		IFingerprinter fingerprinter = new ExtendedFingerprinter();
		assertNotNull(fingerprinter);
	}
	
	public void testGetFingerprint_IAtomContainer() throws java.lang.Exception {
		IFingerprinter fingerprinter = new ExtendedFingerprinter();
		assertNotNull(fingerprinter);
		
		Molecule mol = MoleculeFactory.makeIndole();
		BitSet bs = fingerprinter.getFingerprint(mol);
		Molecule frag1 = MoleculeFactory.makePyrrole();
		BitSet bs1 = fingerprinter.getFingerprint(frag1);
		assertTrue(FingerprinterTool.isSubset(bs, bs1));
	}
	
	public void testGetFingerprint_IAtomContainer_IRingSet() throws java.lang.Exception {
		ExtendedFingerprinter fingerprinter = new ExtendedFingerprinter();
		assertNotNull(fingerprinter);
		
		Molecule mol = MoleculeFactory.makeIndole();
		IRingSet ringset=new AllRingsFinder().findAllRings(mol);
		BitSet bs = fingerprinter.getFingerprint(mol,ringset);
		Molecule frag1 = MoleculeFactory.makePyrrole();
		BitSet bs1 = fingerprinter.getFingerprint(frag1);
		assertTrue(FingerprinterTool.isSubset(bs, bs1));
	}
	
	
	public void testGetSize() throws java.lang.Exception {
		IFingerprinter fingerprinter = new ExtendedFingerprinter(512);
		assertNotNull(fingerprinter);
		assertEquals(512, fingerprinter.getSize());
	}

	public void testExtendedFingerprinter_int() throws java.lang.Exception {
		IFingerprinter fingerprinter = new ExtendedFingerprinter(512);
		assertNotNull(fingerprinter);
		
		Molecule mol = MoleculeFactory.makeIndole();
		BitSet bs = fingerprinter.getFingerprint(mol);
		Molecule frag1 = MoleculeFactory.makePyrrole();
		BitSet bs1 = fingerprinter.getFingerprint(frag1);
		assertTrue(FingerprinterTool.isSubset(bs, bs1));
	}
	
	public void testExtendedFingerprinter_int_int() throws java.lang.Exception {
		IFingerprinter fingerprinter = new ExtendedFingerprinter(512,7);
		assertNotNull(fingerprinter);
		
		Molecule mol = MoleculeFactory.makeIndole();
		BitSet bs = fingerprinter.getFingerprint(mol);
		Molecule frag1 = MoleculeFactory.makePyrrole();
		BitSet bs1 = fingerprinter.getFingerprint(frag1);
		assertTrue(FingerprinterTool.isSubset(bs, bs1));
	}
	
	/*
	 * this test only works with allringsfinder in fingerprinter
	 */
	public void testDifferentRingFinders()throws Exception{
		IFingerprinter fingerprinter = new ExtendedFingerprinter();
		Molecule ac1=new Molecule();
		Atom atom1=new Atom("C");
		Atom atom2=new Atom("C");
		Atom atom3=new Atom("C");
		Atom atom4=new Atom("C");
		Atom atom5=new Atom("C");
		Atom atom6=new Atom("C");
		ac1.addAtom(atom1);
		ac1.addAtom(atom2);
		ac1.addAtom(atom3);
		ac1.addAtom(atom4);
		ac1.addAtom(atom5);
		ac1.addAtom(atom6);
		Bond bond1=new Bond(atom1,atom2);
		Bond bond2=new Bond(atom2,atom3);
		Bond bond3=new Bond(atom3,atom4);
		Bond bond4=new Bond(atom4,atom5);
		Bond bond5=new Bond(atom5,atom6);
		Bond bond6=new Bond(atom6,atom1);
		ac1.addBond(bond1);
		ac1.addBond(bond2);
		ac1.addBond(bond3);
		ac1.addBond(bond4);
		ac1.addBond(bond5);
		ac1.addBond(bond6);
		Molecule ac2=new Molecule();
		ac2.addAtom(atom1);
		ac2.addAtom(atom2);
		ac2.addAtom(atom3);
		ac2.addAtom(atom4);
		ac2.addAtom(atom5);
		ac2.addAtom(atom6);
		Bond bond7=new Bond(atom3,atom1);
		ac2.addBond(bond1);
		ac2.addBond(bond2);
		ac2.addBond(bond3);
		ac2.addBond(bond4);
		ac2.addBond(bond5);
		ac2.addBond(bond6);
		ac2.addBond(bond7);
		BitSet bs = fingerprinter.getFingerprint(ac1);
		BitSet bs1 = fingerprinter.getFingerprint(ac2);
		assertTrue(FingerprinterTool.isSubset(bs1, bs));	
		
	}
	
}

