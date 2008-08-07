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
package org.openscience.cdk.fingerprint;

import java.io.InputStream;
import java.util.BitSet;
import java.util.List;

import javax.vecmath.Point2d;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.ringsearch.RingPartitioner;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.templates.MoleculeFactory;

/**
 * @cdk.module test-fingerprint
 */
public class ExtendedFingerprinterTest extends NewCDKTestCase {
	
	public ExtendedFingerprinterTest() {
		super();
	}


	@Test public void testExtendedFingerprinter() throws java.lang.Exception {
		IFingerprinter fingerprinter = new ExtendedFingerprinter();
		Assert.assertNotNull(fingerprinter);
	}
	
	@Test public void testGetFingerprint_IAtomContainer() throws java.lang.Exception {
		IFingerprinter fingerprinter = new ExtendedFingerprinter();
		Assert.assertNotNull(fingerprinter);
		
		Molecule mol = MoleculeFactory.makeIndole();
		BitSet bs = fingerprinter.getFingerprint(mol);
		Molecule frag1 = MoleculeFactory.makePyrrole();
		BitSet bs1 = fingerprinter.getFingerprint(frag1);
		Assert.assertTrue(FingerprinterTool.isSubset(bs, bs1));
		Assert.assertFalse(FingerprinterTool.isSubset(bs1, bs));
	}
	
	@Test
    public void testGetFingerprint_IAtomContainer_IRingSet_List() throws java.lang.Exception {
		ExtendedFingerprinter fingerprinter = new ExtendedFingerprinter();
		Assert.assertNotNull(fingerprinter);
		
		Molecule mol = MoleculeFactory.makeIndole();
		IRingSet rs=new SSSRFinder(mol).findSSSR();
		List rslist=RingPartitioner.partitionRings(rs);
		BitSet bs = fingerprinter.getFingerprint(mol,rs, rslist);
		Molecule frag1 = MoleculeFactory.makePyrrole();
		BitSet bs1 = fingerprinter.getFingerprint(frag1);
		Assert.assertTrue(FingerprinterTool.isSubset(bs, bs1));
		Assert.assertFalse(FingerprinterTool.isSubset(bs1, bs));
	}
	
	
	@Test public void testGetSize() throws java.lang.Exception {
		IFingerprinter fingerprinter = new ExtendedFingerprinter(512);
		Assert.assertNotNull(fingerprinter);
		Assert.assertEquals(512, fingerprinter.getSize());
	}

	@Test public void testExtendedFingerprinter_int() throws java.lang.Exception {
		IFingerprinter fingerprinter = new ExtendedFingerprinter(512);
		Assert.assertNotNull(fingerprinter);
		
		Molecule mol = MoleculeFactory.makeIndole();
		BitSet bs = fingerprinter.getFingerprint(mol);
		Molecule frag1 = MoleculeFactory.makePyrrole();
		BitSet bs1 = fingerprinter.getFingerprint(frag1);
		Assert.assertTrue(FingerprinterTool.isSubset(bs, bs1));
		Assert.assertFalse(FingerprinterTool.isSubset(bs1, bs));
	}
	
	@Test public void testExtendedFingerprinter_int_int() throws java.lang.Exception {
		IFingerprinter fingerprinter = new ExtendedFingerprinter(512,7);
		Assert.assertNotNull(fingerprinter);
		
		Molecule mol = MoleculeFactory.makeIndole();
		BitSet bs = fingerprinter.getFingerprint(mol);
		Molecule frag1 = MoleculeFactory.makePyrrole();
		BitSet bs1 = fingerprinter.getFingerprint(frag1);
		Assert.assertTrue(FingerprinterTool.isSubset(bs, bs1));
		Assert.assertFalse(FingerprinterTool.isSubset(bs1, bs));
	}
	
	/*
	 * this test only works with allringsfinder in fingerprinter
	 * shk3 2008-8-7: With the change of the extended fingerprinter in r11932, this works by default 
	 */
	@Test public void testDifferentRingFinders()throws Exception{
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
		Assert.assertTrue(FingerprinterTool.isSubset(bs1, bs));
		Assert.assertFalse(FingerprinterTool.isSubset(bs, bs1));
	}
	/*
	 * this tests if a system with three single rings is not found (it should not) if looking
	 * for a system with three condensed rings using the fingerprint
	 */
	@Test public void testCondensedSingle()throws Exception{
		  IMolecule molcondensed = new Molecule();
		  IAtom a1 = molcondensed.getBuilder().newAtom("C");
		  a1.setPoint2d(new Point2d(421.99999999999994, 860.0));  molcondensed.addAtom(a1);
		  IAtom a2 = molcondensed.getBuilder().newAtom("C");
		  a2.setPoint2d(new Point2d(390.8230854637602, 878.0));  molcondensed.addAtom(a2);
		  IAtom a3 = molcondensed.getBuilder().newAtom("C");
		  a3.setPoint2d(new Point2d(390.8230854637602, 914.0));  molcondensed.addAtom(a3);
		  IAtom a4 = molcondensed.getBuilder().newAtom("C");
		  a4.setPoint2d(new Point2d(422.0, 932.0));  molcondensed.addAtom(a4);
		  IAtom a5 = molcondensed.getBuilder().newAtom("C");
		  a5.setPoint2d(new Point2d(453.1769145362398, 914.0));  molcondensed.addAtom(a5);
		  IAtom a6 = molcondensed.getBuilder().newAtom("C");
		  a6.setPoint2d(new Point2d(453.1769145362398, 878.0));  molcondensed.addAtom(a6);
		  IAtom a7 = molcondensed.getBuilder().newAtom("C");
		  a7.setPoint2d(new Point2d(484.3538290724796, 860.0));  molcondensed.addAtom(a7);
		  IAtom a8 = molcondensed.getBuilder().newAtom("C");
		  a8.setPoint2d(new Point2d(515.5307436087194, 878.0));  molcondensed.addAtom(a8);
		  IAtom a9 = molcondensed.getBuilder().newAtom("C");
		  a9.setPoint2d(new Point2d(515.5307436087194, 914.0));  molcondensed.addAtom(a9);
		  IAtom a10 = molcondensed.getBuilder().newAtom("C");
		  a10.setPoint2d(new Point2d(484.3538290724796, 932.0));  molcondensed.addAtom(a10);
		  IAtom a11 = molcondensed.getBuilder().newAtom("C");
		  a11.setPoint2d(new Point2d(546.7076581449592, 932.0));  molcondensed.addAtom(a11);
		  IAtom a12 = molcondensed.getBuilder().newAtom("C");
		  a12.setPoint2d(new Point2d(577.884572681199, 914.0));  molcondensed.addAtom(a12);
		  IAtom a13 = molcondensed.getBuilder().newAtom("C");
		  a13.setPoint2d(new Point2d(577.884572681199, 878.0));  molcondensed.addAtom(a13);
		  IAtom a14 = molcondensed.getBuilder().newAtom("C");
		  a14.setPoint2d(new Point2d(546.7076581449592, 860.0));  molcondensed.addAtom(a14);
		  IAtom a15 = molcondensed.getBuilder().newAtom("C");
		  a15.setPoint2d(new Point2d(359.6461709275204, 860.0));  molcondensed.addAtom(a15);
		  IAtom a16 = molcondensed.getBuilder().newAtom("C");
		  a16.setPoint2d(new Point2d(609.0614872174388, 860.0));  molcondensed.addAtom(a16);
		  IBond b1 = molcondensed.getBuilder().newBond(a1, a2, IBond.Order.SINGLE);
		  molcondensed.addBond(b1);
		  IBond b2 = molcondensed.getBuilder().newBond(a2, a3, IBond.Order.SINGLE);
		  molcondensed.addBond(b2);
		  IBond b3 = molcondensed.getBuilder().newBond(a3, a4, IBond.Order.SINGLE);
		  molcondensed.addBond(b3);
		  IBond b4 = molcondensed.getBuilder().newBond(a4, a5, IBond.Order.SINGLE);
		  molcondensed.addBond(b4);
		  IBond b5 = molcondensed.getBuilder().newBond(a5, a6, IBond.Order.SINGLE);
		  molcondensed.addBond(b5);
		  IBond b6 = molcondensed.getBuilder().newBond(a6, a1, IBond.Order.SINGLE);
		  molcondensed.addBond(b6);
		  IBond b7 = molcondensed.getBuilder().newBond(a6, a7, IBond.Order.SINGLE);
		  molcondensed.addBond(b7);
		  IBond b8 = molcondensed.getBuilder().newBond(a7, a8, IBond.Order.SINGLE);
		  molcondensed.addBond(b8);
		  IBond b9 = molcondensed.getBuilder().newBond(a8, a9, IBond.Order.SINGLE);
		  molcondensed.addBond(b9);
		  IBond b10 = molcondensed.getBuilder().newBond(a9, a10, IBond.Order.SINGLE);
		  molcondensed.addBond(b10);
		  IBond b11 = molcondensed.getBuilder().newBond(a10, a5, IBond.Order.SINGLE);
		  molcondensed.addBond(b11);
		  IBond b12 = molcondensed.getBuilder().newBond(a9, a11, IBond.Order.SINGLE);
		  molcondensed.addBond(b12);
		  IBond b13 = molcondensed.getBuilder().newBond(a11, a12, IBond.Order.SINGLE);
		  molcondensed.addBond(b13);
		  IBond b14 = molcondensed.getBuilder().newBond(a12, a13, IBond.Order.SINGLE);
		  molcondensed.addBond(b14);
		  IBond b15 = molcondensed.getBuilder().newBond(a13, a14, IBond.Order.SINGLE);
		  molcondensed.addBond(b15);
		  IBond b16 = molcondensed.getBuilder().newBond(a14, a8, IBond.Order.SINGLE);
		  molcondensed.addBond(b16);
		  IBond b17 = molcondensed.getBuilder().newBond(a2, a15, IBond.Order.SINGLE);
		  molcondensed.addBond(b17);
		  IBond b18 = molcondensed.getBuilder().newBond(a13, a16, IBond.Order.SINGLE);
		  molcondensed.addBond(b18);
		  
		  IMolecule molsingle = new Molecule();
		  IAtom a1s = molsingle.getBuilder().newAtom("C");
		  a1s.setPoint2d(new Point2d(421.99999999999994, 860.0));  molsingle.addAtom(a1s);
		  IAtom a2s = molsingle.getBuilder().newAtom("C");
		  a2s.setPoint2d(new Point2d(390.8230854637602, 878.0));  molsingle.addAtom(a2s);
		  IAtom a6s = molsingle.getBuilder().newAtom("C");
		  a6s.setPoint2d(new Point2d(453.1769145362398, 878.0));  molsingle.addAtom(a6s);
		  IAtom a3s = molsingle.getBuilder().newAtom("C");
		  a3s.setPoint2d(new Point2d(390.8230854637602, 914.0));  molsingle.addAtom(a3s);
		  IAtom a15s = molsingle.getBuilder().newAtom("C");
		  a15s.setPoint2d(new Point2d(359.6461709275204, 860.0));  molsingle.addAtom(a15s);
		  IAtom a5s = molsingle.getBuilder().newAtom("C");
		  a5s.setPoint2d(new Point2d(453.1769145362398, 914.0));  molsingle.addAtom(a5s);
		  IAtom a7s = molsingle.getBuilder().newAtom("C");
		  a7s.setPoint2d(new Point2d(492.8230854637602, 881.0));  molsingle.addAtom(a7s);
		  IAtom a4s = molsingle.getBuilder().newAtom("C");
		  a4s.setPoint2d(new Point2d(422.0, 932.0));  molsingle.addAtom(a4s);
		  IAtom a8s = molsingle.getBuilder().newAtom("C");
		  a8s.setPoint2d(new Point2d(524.0, 863.0));  molsingle.addAtom(a8s);
		  IAtom a9s = molsingle.getBuilder().newAtom("C");
		  a9s.setPoint2d(new Point2d(492.8230854637602, 917.0));  molsingle.addAtom(a9s);
		  IAtom a10s = molsingle.getBuilder().newAtom("C");
		  a10s.setPoint2d(new Point2d(555.1769145362398, 881.0));  molsingle.addAtom(a10s);
		  IAtom a11s = molsingle.getBuilder().newAtom("C");
		  a11s.setPoint2d(new Point2d(524.0, 935.0));  molsingle.addAtom(a11s);
		  IAtom a12s = molsingle.getBuilder().newAtom("C");
		  a12s.setPoint2d(new Point2d(555.1769145362398, 917.0));  molsingle.addAtom(a12s);
		  IAtom a13s = molsingle.getBuilder().newAtom("C");
		  a13s.setPoint2d(new Point2d(592.8230854637602, 889.0));  molsingle.addAtom(a13s);
		  IAtom a14s = molsingle.getBuilder().newAtom("C");
		  a14s.setPoint2d(new Point2d(624.0, 871.0));  molsingle.addAtom(a14s);
		  IAtom a16s = molsingle.getBuilder().newAtom("C");
		  a16s.setPoint2d(new Point2d(592.8230854637602, 925.0));  molsingle.addAtom(a16s);
		  IAtom a17s = molsingle.getBuilder().newAtom("C");
		  a17s.setPoint2d(new Point2d(655.1769145362398, 889.0));  molsingle.addAtom(a17s);
		  IAtom a18s = molsingle.getBuilder().newAtom("C");
		  a18s.setPoint2d(new Point2d(624.0, 943.0));  molsingle.addAtom(a18s);
		  IAtom a19s = molsingle.getBuilder().newAtom("C");
		  a19s.setPoint2d(new Point2d(655.1769145362398, 925.0));  molsingle.addAtom(a19s);
		  IAtom a20s = molsingle.getBuilder().newAtom("C");
		  a20s.setPoint2d(new Point2d(686.3538290724796, 871.0));  molsingle.addAtom(a20s);
		  IBond b1s = molsingle.getBuilder().newBond(a1s, a2s, IBond.Order.SINGLE);
		  molsingle.addBond(b1s);
		  IBond b6s = molsingle.getBuilder().newBond(a6s, a1s, IBond.Order.SINGLE);
		  molsingle.addBond(b6s);
		  IBond b2s = molsingle.getBuilder().newBond(a2s, a3s, IBond.Order.SINGLE);
		  molsingle.addBond(b2s);
		  IBond b17s = molsingle.getBuilder().newBond(a2s, a15s, IBond.Order.SINGLE);
		  molsingle.addBond(b17s);
		  IBond b5s = molsingle.getBuilder().newBond(a5s, a6s, IBond.Order.SINGLE);
		  molsingle.addBond(b5s);
		  IBond b7s = molsingle.getBuilder().newBond(a6s, a7s,IBond.Order.SINGLE);
		  molsingle.addBond(b7s);
		  IBond b3s = molsingle.getBuilder().newBond(a3s, a4s, IBond.Order.SINGLE);
		  molsingle.addBond(b3s);
		  IBond b4s = molsingle.getBuilder().newBond(a4s, a5s, IBond.Order.SINGLE);
		  molsingle.addBond(b4s);
		  IBond b8s = molsingle.getBuilder().newBond(a8s, a7s, IBond.Order.SINGLE);
		  molsingle.addBond(b8s);
		  IBond b9s = molsingle.getBuilder().newBond(a7s, a9s, IBond.Order.SINGLE);
		  molsingle.addBond(b9s);
		  IBond b10s = molsingle.getBuilder().newBond(a10s, a8s, IBond.Order.SINGLE);
		  molsingle.addBond(b10s);
		  IBond b11s = molsingle.getBuilder().newBond(a9s, a11s, IBond.Order.SINGLE);
		  molsingle.addBond(b11s);
		  IBond b12s = molsingle.getBuilder().newBond(a12s, a10s, IBond.Order.SINGLE);
		  molsingle.addBond(b12s);
		  IBond b13s = molsingle.getBuilder().newBond(a10s, a13s,IBond.Order.SINGLE);
		  molsingle.addBond(b13s);
		  IBond b14s = molsingle.getBuilder().newBond(a11s, a12s, IBond.Order.SINGLE);
		  molsingle.addBond(b14s);
		  IBond b15s = molsingle.getBuilder().newBond(a14s, a13s, IBond.Order.SINGLE);
		  molsingle.addBond(b15s);
		  IBond b16s = molsingle.getBuilder().newBond(a13s, a16s, IBond.Order.SINGLE);
		  molsingle.addBond(b16s);
		  IBond b18s = molsingle.getBuilder().newBond(a17s, a14s, IBond.Order.SINGLE);
		  molsingle.addBond(b18s);
		  IBond b19s = molsingle.getBuilder().newBond(a16s, a18s, IBond.Order.SINGLE);
		  molsingle.addBond(b19s);
		  IBond b20s = molsingle.getBuilder().newBond(a19s, a17s, IBond.Order.SINGLE);
		  molsingle.addBond(b20s);
		  IBond b21s = molsingle.getBuilder().newBond(a18s, a19s, IBond.Order.SINGLE);
		  molsingle.addBond(b21s);
		  IBond b22s = molsingle.getBuilder().newBond(a17s, a20s, IBond.Order.SINGLE);
		  molsingle.addBond(b22s);
		  
		  IFingerprinter fingerprinter = new ExtendedFingerprinter();
		  BitSet bs1 = fingerprinter.getFingerprint(molsingle);
		  BitSet bs2 = fingerprinter.getFingerprint(molcondensed);
		  
		  Assert.assertFalse(FingerprinterTool.isSubset(bs1, bs2));
		  Assert.assertTrue(FingerprinterTool.isSubset(bs2, bs1));

	}	
	
	/*
	 * The power of the extended fingerprinter could not distinguish these before the change in r11932
	 */
	@Test public void testChebi() throws java.lang.Exception
	{
		Molecule searchmol = null;
		Molecule findmol = null;
		String filename = "data/mdl/chebisearch.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins);
		searchmol = (Molecule) reader.read((ChemObject) new Molecule());
		filename = "data/mdl/chebifind.mol";
		ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		reader = new MDLV2000Reader(ins);
		findmol = (Molecule) reader.read((ChemObject) new Molecule());
		IFingerprinter fingerprinter = new ExtendedFingerprinter();
		BitSet superBS = fingerprinter.getFingerprint(findmol);
		BitSet subBS = fingerprinter.getFingerprint(searchmol);
		boolean isSubset = FingerprinterTool.isSubset(superBS, subBS);
		boolean isSubset2 = FingerprinterTool.isSubset(subBS, superBS);
		Assert.assertFalse(isSubset);
		Assert.assertFalse(isSubset2);
	}
}

