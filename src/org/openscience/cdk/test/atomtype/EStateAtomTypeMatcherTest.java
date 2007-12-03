/* $Revision: 7691 $ $Author: egonw $ $Date: 2007-01-11 12:47:48 +0100 (Thu, 11 Jan 2007) $
 * 
 * Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
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
package org.openscience.cdk.test.atomtype;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.atomtype.EStateAtomTypeMatcher;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test-standard
 */
public class EStateAtomTypeMatcherTest extends CDKTestCase {
    
	EStateAtomTypeMatcher matcher = new EStateAtomTypeMatcher();
	IMolecule mol = null;
	
	public EStateAtomTypeMatcherTest(String name) {
        super(name);
    }
    
	public static Test suite() {
		return new TestSuite(EStateAtomTypeMatcherTest.class);
	}

	public void testEStateAtomTypeMatcher() {		
		assertNotNull(matcher);
	}
	
	IRingSet getRings()
	{
		IRingSet rs = null;		 
		try {
			AllRingsFinder arf = new AllRingsFinder();
			rs = arf.findAllRings(mol);
		} catch (Exception e) {
			System.out.println("Could not find all rings: " + e.getMessage());
		}
		return(rs);     
	}
	
	void testAtom(String expectedAtType, IAtom atom)
	{	
		assertEquals(expectedAtType, matcher.findMatchingAtomType(mol,atom).getAtomTypeName());
	}
	
	public void testSP3Atoms(){
		//Testing with CC(C)(C)CC 
		mol = new Molecule();
		IAtom a1 = mol.getBuilder().newAtom("C");
		mol.addAtom(a1);
		IAtom a2 = mol.getBuilder().newAtom("C");
		mol.addAtom(a2);
		IAtom a3 = mol.getBuilder().newAtom("C");
		mol.addAtom(a3);
		IAtom a4 = mol.getBuilder().newAtom("C");
		mol.addAtom(a4);
		IAtom a5 = mol.getBuilder().newAtom("C");
		mol.addAtom(a5);
		IAtom a6 = mol.getBuilder().newAtom("C");
		mol.addAtom(a6);
		IAtom a7 = mol.getBuilder().newAtom("H");
		mol.addAtom(a7);
		IAtom a8 = mol.getBuilder().newAtom("H");
		mol.addAtom(a8);
		IAtom a9 = mol.getBuilder().newAtom("H");
		mol.addAtom(a9);
		IAtom a10 = mol.getBuilder().newAtom("H");
		mol.addAtom(a10);
		IAtom a11 = mol.getBuilder().newAtom("H");
		mol.addAtom(a11);
		IAtom a12 = mol.getBuilder().newAtom("H");
		mol.addAtom(a12);
		IAtom a13 = mol.getBuilder().newAtom("H");
		mol.addAtom(a13);
		IAtom a14 = mol.getBuilder().newAtom("H");
		mol.addAtom(a14);
		IAtom a15 = mol.getBuilder().newAtom("H");
		mol.addAtom(a15);
		IAtom a16 = mol.getBuilder().newAtom("H");
		mol.addAtom(a16);
		IAtom a17 = mol.getBuilder().newAtom("H");
		mol.addAtom(a17);
		IAtom a18 = mol.getBuilder().newAtom("H");
		mol.addAtom(a18);
		IAtom a19 = mol.getBuilder().newAtom("H");
		mol.addAtom(a19);
		IAtom a20 = mol.getBuilder().newAtom("H");
		mol.addAtom(a20);
		IBond b1 = mol.getBuilder().newBond(a2, a1, 1.0);
		mol.addBond(b1);
		IBond b2 = mol.getBuilder().newBond(a3, a2, 1.0);
		mol.addBond(b2);
		IBond b3 = mol.getBuilder().newBond(a4, a2, 1.0);
		mol.addBond(b3);
		IBond b4 = mol.getBuilder().newBond(a5, a2, 1.0);
		mol.addBond(b4);
		IBond b5 = mol.getBuilder().newBond(a6, a5, 1.0);
		mol.addBond(b5);
		IBond b6 = mol.getBuilder().newBond(a1, a7, 1.0);
		mol.addBond(b6);
		IBond b7 = mol.getBuilder().newBond(a1, a8, 1.0);
		mol.addBond(b7);
		IBond b8 = mol.getBuilder().newBond(a1, a9, 1.0);
		mol.addBond(b8);
		IBond b9 = mol.getBuilder().newBond(a3, a10, 1.0);
		mol.addBond(b9);
		IBond b10 = mol.getBuilder().newBond(a3, a11, 1.0);
		mol.addBond(b10);
		IBond b11 = mol.getBuilder().newBond(a3, a12, 1.0);
		mol.addBond(b11);
		IBond b12 = mol.getBuilder().newBond(a4, a13, 1.0);
		mol.addBond(b12);
		IBond b13 = mol.getBuilder().newBond(a4, a14, 1.0);
		mol.addBond(b13);
		IBond b14 = mol.getBuilder().newBond(a4, a15, 1.0);
		mol.addBond(b14);
		IBond b15 = mol.getBuilder().newBond(a5, a16, 1.0);
		mol.addBond(b15);
		IBond b16 = mol.getBuilder().newBond(a5, a17, 1.0);
		mol.addBond(b16);
		IBond b17 = mol.getBuilder().newBond(a6, a18, 1.0);
		mol.addBond(b17);
		IBond b18 = mol.getBuilder().newBond(a6, a19, 1.0);
		mol.addBond(b18);
		IBond b19 = mol.getBuilder().newBond(a6, a20, 1.0);
		mol.addBond(b19);	
		
		matcher.setRingSet(getRings());
		testAtom("SsCH3",a1);
		testAtom("SssssC",a2);
		testAtom("SsCH3",a3);
		testAtom("SsCH3",a4);
		testAtom("SssCH2",a5);
		testAtom("SsCH3",a6);
		testAtom("SsH",a7);
		testAtom("SsH",a8);
	}
	
	public void testSP2Atoms(){
		//Test with C=CC=N 
		mol = new Molecule();
		IAtom a1 = mol.getBuilder().newAtom("C");
		mol.addAtom(a1);
		IAtom a2 = mol.getBuilder().newAtom("C");
		mol.addAtom(a2);
		IAtom a3 = mol.getBuilder().newAtom("C");
		mol.addAtom(a3);
		IAtom a4 = mol.getBuilder().newAtom("N");
		mol.addAtom(a4);
		IAtom a5 = mol.getBuilder().newAtom("H");
		mol.addAtom(a5);
		IAtom a6 = mol.getBuilder().newAtom("H");
		mol.addAtom(a6);
		IAtom a7 = mol.getBuilder().newAtom("H");
		mol.addAtom(a7);
		IAtom a8 = mol.getBuilder().newAtom("H");
		mol.addAtom(a8);
		IAtom a9 = mol.getBuilder().newAtom("H");
		mol.addAtom(a9);
		IBond b1 = mol.getBuilder().newBond(a2, a1, 2.0);
		mol.addBond(b1);
		IBond b2 = mol.getBuilder().newBond(a3, a2, 1.0);
		mol.addBond(b2);
		IBond b3 = mol.getBuilder().newBond(a4, a3, 2.0);
		mol.addBond(b3);
		IBond b4 = mol.getBuilder().newBond(a1, a5, 1.0);
		mol.addBond(b4);
		IBond b5 = mol.getBuilder().newBond(a1, a6, 1.0);
		mol.addBond(b5);
		IBond b6 = mol.getBuilder().newBond(a2, a7, 1.0);
		mol.addBond(b6);
		IBond b7 = mol.getBuilder().newBond(a3, a8, 1.0);
		mol.addBond(b7);
		IBond b8 = mol.getBuilder().newBond(a4, a9, 1.0);
		mol.addBond(b8);
		
		matcher.setRingSet(getRings());
		testAtom("SdCH2",a1);
		testAtom("SdsCH",a2);
		testAtom("SdsCH",a3);
		testAtom("SdNH",a4);	
		testAtom("SsH",a9);
	}
	
	public void testSPAtoms(){
		//Testing with  C#CCC#N 
		mol = new Molecule();
		IAtom a1 = mol.getBuilder().newAtom("C");
		mol.addAtom(a1);
		IAtom a2 = mol.getBuilder().newAtom("C");
		mol.addAtom(a2);
		IAtom a3 = mol.getBuilder().newAtom("C");
		mol.addAtom(a3);
		IAtom a4 = mol.getBuilder().newAtom("C");
		mol.addAtom(a4);
		IAtom a5 = mol.getBuilder().newAtom("N");
		mol.addAtom(a5);
		IAtom a6 = mol.getBuilder().newAtom("H");
		mol.addAtom(a6);
		IAtom a7 = mol.getBuilder().newAtom("H");
		mol.addAtom(a7);
		IAtom a8 = mol.getBuilder().newAtom("H");
		mol.addAtom(a8);
		IBond b1 = mol.getBuilder().newBond(a2, a1, 3.0);
		mol.addBond(b1);
		IBond b2 = mol.getBuilder().newBond(a3, a2, 1.0);
		mol.addBond(b2);
		IBond b3 = mol.getBuilder().newBond(a4, a3, 1.0);
		mol.addBond(b3);
		IBond b4 = mol.getBuilder().newBond(a5, a4, 3.0);
		mol.addBond(b4);
		IBond b5 = mol.getBuilder().newBond(a1, a6, 1.0);
		mol.addBond(b5);
		IBond b6 = mol.getBuilder().newBond(a3, a7, 1.0);
		mol.addBond(b6);
		IBond b7 = mol.getBuilder().newBond(a3, a8, 1.0);
		mol.addBond(b7);
		
		matcher.setRingSet(getRings());
		testAtom("StCH",a1);
		testAtom("StsC",a2);
		testAtom("SssCH2",a3);
		testAtom("StsC",a4);
		testAtom("StN",a5);		
	}
	
	public void testAromaticAtoms(){
		//Testing with C1=CN=CC=C1C		
		mol = new Molecule();
		IAtom a1 = mol.getBuilder().newAtom("C");
		a1.setFlag(CDKConstants.ISAROMATIC,true);
		mol.addAtom(a1);
		IAtom a2 = mol.getBuilder().newAtom("C");
		a2.setFlag(CDKConstants.ISAROMATIC,true);
		mol.addAtom(a2);
		IAtom a3 = mol.getBuilder().newAtom("N");
		a3.setFlag(CDKConstants.ISAROMATIC,true);
		mol.addAtom(a3);
		IAtom a4 = mol.getBuilder().newAtom("C");
		a4.setFlag(CDKConstants.ISAROMATIC,true);
		mol.addAtom(a4);
		IAtom a5 = mol.getBuilder().newAtom("C");
		a5.setFlag(CDKConstants.ISAROMATIC,true);
		mol.addAtom(a5);
		IAtom a6 = mol.getBuilder().newAtom("C");
		a6.setFlag(CDKConstants.ISAROMATIC,true);
		mol.addAtom(a6);
		IAtom a7 = mol.getBuilder().newAtom("C");
		mol.addAtom(a7);
		IAtom a8 = mol.getBuilder().newAtom("H");
		mol.addAtom(a8);
		IAtom a9 = mol.getBuilder().newAtom("H");
		mol.addAtom(a9);
		IAtom a10 = mol.getBuilder().newAtom("H");
		mol.addAtom(a10);
		IAtom a11 = mol.getBuilder().newAtom("H");
		mol.addAtom(a11);
		IAtom a12 = mol.getBuilder().newAtom("H");
		mol.addAtom(a12);
		IAtom a13 = mol.getBuilder().newAtom("H");
		mol.addAtom(a13);
		IAtom a14 = mol.getBuilder().newAtom("H");
		mol.addAtom(a14);
		IBond b1 = mol.getBuilder().newBond(a2, a1, 2.0);
		mol.addBond(b1);
		IBond b2 = mol.getBuilder().newBond(a3, a2, 1.0);
		mol.addBond(b2);
		IBond b3 = mol.getBuilder().newBond(a4, a3, 2.0);
		mol.addBond(b3);
		IBond b4 = mol.getBuilder().newBond(a5, a4, 1.0);
		mol.addBond(b4);
		IBond b5 = mol.getBuilder().newBond(a6, a5, 2.0);
		mol.addBond(b5);
		IBond b6 = mol.getBuilder().newBond(a6, a1, 1.0);
		mol.addBond(b6);
		IBond b7 = mol.getBuilder().newBond(a7, a6, 1.0);
		mol.addBond(b7);
		IBond b8 = mol.getBuilder().newBond(a1, a8, 1.0);
		mol.addBond(b8);
		IBond b9 = mol.getBuilder().newBond(a2, a9, 1.0);
		mol.addBond(b9);
		IBond b10 = mol.getBuilder().newBond(a5, a10, 1.0);
		mol.addBond(b10);
		IBond b11 = mol.getBuilder().newBond(a7, a11, 1.0);
		mol.addBond(b11);
		IBond b12 = mol.getBuilder().newBond(a7, a12, 1.0);
		mol.addBond(b12);
		IBond b13 = mol.getBuilder().newBond(a7, a13, 1.0);
		mol.addBond(b13);
		IBond b14 = mol.getBuilder().newBond(a4, a14, 1.0);
		mol.addBond(b14);		
		
		matcher.setRingSet(getRings());		
		testAtom("SaaCH",a1);
		testAtom("SaaCH",a2);
		testAtom("SaaN",a3);
		testAtom("SaaCH",a4);
		testAtom("SaaCH",a5);
		testAtom("SsaaC",a6);
		testAtom("SsCH3",a7);
	}
	
	public void testNaphthalene(){
		//Testing with C1=CC2C=CC=CC=2C=C1		
		mol = new Molecule();
		IAtom a1 = mol.getBuilder().newAtom("C");
		a1.setFlag(CDKConstants.ISAROMATIC,true);
		mol.addAtom(a1);
		IAtom a2 = mol.getBuilder().newAtom("C");
		a2.setFlag(CDKConstants.ISAROMATIC,true);
		mol.addAtom(a2);
		IAtom a3 = mol.getBuilder().newAtom("C");
		a3.setFlag(CDKConstants.ISAROMATIC,true);
		mol.addAtom(a3);
		IAtom a4 = mol.getBuilder().newAtom("C");
		a4.setFlag(CDKConstants.ISAROMATIC,true);
		mol.addAtom(a4);
		IAtom a5 = mol.getBuilder().newAtom("C");
		a5.setFlag(CDKConstants.ISAROMATIC,true);
		mol.addAtom(a5);		
		IAtom a6 = mol.getBuilder().newAtom("C");
		a6.setFlag(CDKConstants.ISAROMATIC,true);
		mol.addAtom(a6);
		IAtom a7 = mol.getBuilder().newAtom("C");
		a7.setFlag(CDKConstants.ISAROMATIC,true);
		mol.addAtom(a7);
		IAtom a8 = mol.getBuilder().newAtom("C");
		a8.setFlag(CDKConstants.ISAROMATIC,true);
		mol.addAtom(a8);
		IAtom a9 = mol.getBuilder().newAtom("C");
		a9.setFlag(CDKConstants.ISAROMATIC,true);
		mol.addAtom(a9);
		IAtom a10 = mol.getBuilder().newAtom("C");
		a10.setFlag(CDKConstants.ISAROMATIC,true);
		mol.addAtom(a10);
		IAtom a11 = mol.getBuilder().newAtom("H");
		mol.addAtom(a11);
		IAtom a12 = mol.getBuilder().newAtom("H");
		mol.addAtom(a12);
		IAtom a13 = mol.getBuilder().newAtom("H");
		mol.addAtom(a13);
		IAtom a14 = mol.getBuilder().newAtom("H");
		mol.addAtom(a14);
		IAtom a15 = mol.getBuilder().newAtom("H");
		mol.addAtom(a15);
		IAtom a16 = mol.getBuilder().newAtom("H");
		mol.addAtom(a16);
		IAtom a17 = mol.getBuilder().newAtom("H");
		mol.addAtom(a17);
		IAtom a18 = mol.getBuilder().newAtom("H");
		mol.addAtom(a18);
		IBond b1 = mol.getBuilder().newBond(a2, a1, 2.0);
		mol.addBond(b1);
		IBond b2 = mol.getBuilder().newBond(a3, a2, 1.0);
		mol.addBond(b2);
		IBond b3 = mol.getBuilder().newBond(a4, a3, 1.0);
		mol.addBond(b3);
		IBond b4 = mol.getBuilder().newBond(a5, a4, 2.0);
		mol.addBond(b4);
		IBond b5 = mol.getBuilder().newBond(a6, a5, 1.0);
		mol.addBond(b5);
		IBond b6 = mol.getBuilder().newBond(a7, a6, 2.0);
		mol.addBond(b6);
		IBond b7 = mol.getBuilder().newBond(a8, a7, 1.0);
		mol.addBond(b7);
		IBond b8 = mol.getBuilder().newBond(a8, a3, 2.0);
		mol.addBond(b8);
		IBond b9 = mol.getBuilder().newBond(a9, a8, 1.0);
		mol.addBond(b9);
		IBond b10 = mol.getBuilder().newBond(a10, a9, 2.0);
		mol.addBond(b10);
		IBond b11 = mol.getBuilder().newBond(a10, a1, 1.0);
		mol.addBond(b11);
		IBond b12 = mol.getBuilder().newBond(a1, a11, 1.0);
		mol.addBond(b12);
		IBond b13 = mol.getBuilder().newBond(a2, a12, 1.0);
		mol.addBond(b13);
		IBond b14 = mol.getBuilder().newBond(a10, a13, 1.0);
		mol.addBond(b14);
		IBond b15 = mol.getBuilder().newBond(a9, a14, 1.0);
		mol.addBond(b15);
		IBond b16 = mol.getBuilder().newBond(a4, a15, 1.0);
		mol.addBond(b16);
		IBond b17 = mol.getBuilder().newBond(a5, a16, 1.0);
		mol.addBond(b17);
		IBond b18 = mol.getBuilder().newBond(a7, a17, 1.0);
		mol.addBond(b18);
		IBond b19 = mol.getBuilder().newBond(a6, a18, 1.0);
		mol.addBond(b19);
		
		matcher.setRingSet(getRings());
		testAtom("SaaCH",a1);
		testAtom("SaaCH",a2);
		testAtom("SaaaC",a3);
		testAtom("SaaCH",a4);
		testAtom("SaaCH",a5);
		testAtom("SaaCH",a6);
		testAtom("SaaCH",a7);
		testAtom("SaaaC",a8);
		testAtom("SaaCH",a9);
		testAtom("SaaCH",a10);
	}	
	
	public void testChargedAtoms(){
		//Testing with C[N+]		
		mol = new Molecule();		
		IAtom a1 = mol.getBuilder().newAtom("C");
		mol.addAtom(a1);
		IAtom a2 = mol.getBuilder().newAtom("N");
		a2.setFormalCharge(+1);
		mol.addAtom(a2);
		IAtom a3 = mol.getBuilder().newAtom("H");
		mol.addAtom(a3);
		IAtom a4 = mol.getBuilder().newAtom("H");
		mol.addAtom(a4);
		IAtom a5 = mol.getBuilder().newAtom("H");
		mol.addAtom(a5);
		IAtom a6 = mol.getBuilder().newAtom("H");
		mol.addAtom(a6);
		IAtom a7 = mol.getBuilder().newAtom("H");
		mol.addAtom(a7);
		IAtom a8 = mol.getBuilder().newAtom("H");
		mol.addAtom(a8);
		IBond b1 = mol.getBuilder().newBond(a2, a1, 1.0);
		mol.addBond(b1);
		IBond b2 = mol.getBuilder().newBond(a1, a3, 1.0);
		mol.addBond(b2);
		IBond b3 = mol.getBuilder().newBond(a1, a4, 1.0);
		mol.addBond(b3);
		IBond b4 = mol.getBuilder().newBond(a1, a5, 1.0);
		mol.addBond(b4);
		IBond b5 = mol.getBuilder().newBond(a2, a6, 1.0);
		mol.addBond(b5);
		IBond b6 = mol.getBuilder().newBond(a2, a7, 1.0);
		mol.addBond(b6);
		IBond b7 = mol.getBuilder().newBond(a2, a8, 1.0);
		mol.addBond(b7);
		
		matcher.setRingSet(getRings());
		testAtom("SsCH3",a1);
		testAtom("SsNpH3",a2);
	}
	
	public void testNaCl(){
		//Testing with [Na+].[Cl-]		
		mol = new Molecule();		
		IAtom a1 = mol.getBuilder().newAtom("Na");
		a1.setFormalCharge(+1);
		mol.addAtom(a1);
		IAtom a2 = mol.getBuilder().newAtom("Cl");
		a2.setFormalCharge(-1);		
		mol.addAtom(a2);
		
		matcher.setRingSet(getRings());
		testAtom("SNap",a1);
		testAtom("SClm",a2);
		
		//Testing with different presentation - [Na]Cl 		
		mol = new Molecule();		
		a1 = mol.getBuilder().newAtom("Na");		
		mol.addAtom(a1);
		a2 = mol.getBuilder().newAtom("Cl");				
		mol.addAtom(a2);
		IBond b1 = mol.getBuilder().newBond(a2, a1, 1.0);
		mol.addBond(b1);
		
		matcher.setRingSet(getRings());
		testAtom("SsNa",a1);
		testAtom("SsCl",a2);
	}	
}


