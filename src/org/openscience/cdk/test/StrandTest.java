/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.test;

import java.util.Hashtable;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Monomer;
import org.openscience.cdk.Strand;

/**
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 * @author     Martin Eklund <martin.eklund@farmbio.uu.se>
 * @cdk.module test
 */
public class StrandTest extends TestCase {
	
    public StrandTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(StrandTest.class);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(new TestSuite(StrandTest.class));
	}

	public void testStrand() {
		Strand oStrand = new Strand();
		assertNotNull(oStrand);
		assertEquals(oStrand.getMonomerCount(), 0);

		Monomer oMono1 = new Monomer();
		oMono1.setMonomerName(new String("TRP279"));
		Monomer oMono2 = new Monomer();
		oMono2.setMonomerName(new String("HOH"));
		Monomer oMono3 = new Monomer();
		oMono3.setMonomerName(new String("GLYA16"));
		Atom oAtom1 = new Atom("C1");
		Atom oAtom2 = new Atom("C2");
		Atom oAtom3 = new Atom("C3");
		Atom oAtom4 = new Atom("C4");
		Atom oAtom5 = new Atom("C5");
		
		oStrand.addAtom(oAtom1);
		oStrand.addAtom(oAtom2);
		oStrand.addAtom(oAtom3, oMono1);
		oStrand.addAtom(oAtom4, oMono2);
		oStrand.addAtom(oAtom5, oMono3);
		assertNotNull(oStrand.getAtomAt(0));
		assertNotNull(oStrand.getAtomAt(1));
		assertNotNull(oStrand.getAtomAt(2));
		assertNotNull(oStrand.getAtomAt(3));
		assertNotNull(oStrand.getAtomAt(4));
		assertEquals(oAtom1, oStrand.getAtomAt(0));
		assertEquals(oAtom2, oStrand.getAtomAt(1));
		assertEquals(oAtom3, oStrand.getAtomAt(2));
		assertEquals(oAtom4, oStrand.getAtomAt(3));
		assertEquals(oAtom5, oStrand.getAtomAt(4));

		assertNull(oStrand.getMonomer("0815"));
		assertNotNull(oStrand.getMonomer(""));
		assertNotNull(oStrand.getMonomer("TRP279"));
		assertEquals(oMono1, oStrand.getMonomer("TRP279"));
		assertEquals(oStrand.getMonomer("TRP279").getAtomCount(), 1);
		assertNotNull(oStrand.getMonomer("HOH"));
		assertEquals(oMono2, oStrand.getMonomer("HOH"));
		assertEquals(oStrand.getMonomer("HOH").getAtomCount(), 1);
		assertEquals(oStrand.getMonomer("").getAtomCount(), 2);
		assertEquals(oStrand.getAtomCount(), 5);
		assertEquals(oStrand.getMonomerCount(), 3);
	}
	
	public void testGetStrandName()	{
		Strand oStrand = new Strand();
		oStrand.setStrandName("A");
		
		assertEquals("A", oStrand.getStrandName());
	}
	
	public void testGetStrandType()	{
		Strand oStrand = new Strand();
		oStrand.setStrandType("DNA");
		
		assertEquals("DNA", oStrand.getStrandType());
	}
	
	/** The methods above effectively test SetStrandName and
	 * SetStrandType as well, but I include SetStrandName and
	 * SetStrandType explicitly as well (for concinstency).  
	 */
	
	public void testSetStrandName_String()	{
		Strand oStrand = new Strand();
		oStrand.setStrandName("A");
		
		assertEquals("A", oStrand.getStrandName());
	}
	
	public void testSetStrandType_String()	{
		Strand oStrand = new Strand();
		oStrand.setStrandType("DNA");
		
		assertEquals("DNA", oStrand.getStrandType());
	}
	
	public void testAddAtom_Atom() {
		Strand oStrand = new Strand();
		Atom oAtom1 = new Atom("C1");
		Atom oAtom2 = new Atom("C2");
		oStrand.addAtom(oAtom1);
		oStrand.addAtom(oAtom2);

		assertEquals(2, oStrand.getAtomCount());
	}
    
	public void testAddAtom_Atom_Monomer() {
		Strand oStrand = new Strand();
		Monomer oMono1 = new Monomer();
		oMono1.setMonomerName(new String("TRP279"));
		Atom oAtom1 = new Atom("C1");
		Atom oAtom2 = new Atom("C2");
		oStrand.addAtom(oAtom1);
		oStrand.addAtom(oAtom2);
		oStrand.addAtom(oAtom2, oMono1);

		assertEquals(2, oStrand.getMonomer("").getAtomCount());
		assertEquals(1, oStrand.getMonomer("TRP279").getAtomCount());
	}
	
	public void testGetMonomerCount() {
		Strand oStrand = new Strand();
		Monomer oMono1 = new Monomer();
		oMono1.setMonomerName(new String("TRP279"));
		Monomer oMono2 = new Monomer();
		oMono2.setMonomerName(new String("HOH"));
		Atom oAtom2 = new Atom("C2");
		Atom oAtom3 = new Atom("C3");
		oStrand.addAtom(oAtom2, oMono1);
		oStrand.addAtom(oAtom3, oMono2);

		assertEquals(2, oStrand.getMonomerCount());
	}
	 
	public void testGetMonomer_String() {
		Strand oStrand = new Strand();
		Monomer oMono1 = new Monomer();
		oMono1.setMonomerName(new String("TRP279"));
		Monomer oMono2 = new Monomer();
		oMono2.setMonomerName(new String("HOH"));
		Atom oAtom2 = new Atom("C2");
		Atom oAtom3 = new Atom("C3");
		oStrand.addAtom(oAtom2, oMono1);
		oStrand.addAtom(oAtom3, oMono2);

		assertEquals(oMono1, oStrand.getMonomer("TRP279"));
		assertEquals(oMono2, oStrand.getMonomer("HOH"));
		assertNull(oStrand.getMonomer("TEST"));
	}
	
	public void testGetMonomerNames() {
		Strand oStrand = new Strand();
		Monomer oMono1 = new Monomer();
		oMono1.setMonomerName(new String("TRP279"));
		Monomer oMono2 = new Monomer();
		oMono2.setMonomerName(new String("HOH"));
		Atom oAtom2 = new Atom("C2");
		Atom oAtom3 = new Atom("C3");
		oStrand.addAtom(oAtom2, oMono1);
		oStrand.addAtom(oAtom3, oMono2);
		Hashtable monomers = new Hashtable();
		Monomer oMon = new Monomer();
		oMon.setMonomerName("");
		oMon.setMonomerType("UNKNOWN");
		monomers.put("", oMon);
		monomers.put("TRP279", oMono1);
		monomers.put("HOH", oMono2);
		
		assertEquals(monomers.keySet(), oStrand.getMonomerNames());
/*
		assertEquals(3, oStrand.getMonomerNames().size());
		assertTrue(oStrand.getMonomerNames().contains(oMono1.getMonomerName()));
		assertTrue(oStrand.getMonomerNames().contains(oMono2.getMonomerName()));
*/		
	}
	
	public void testRemoveMonomer_String()	{
		Strand oStrand = new Strand();
		Monomer oMono1 = new Monomer();
		oMono1.setMonomerName(new String("TRP279"));
		Atom oAtom1 = new Atom("C1");
		oStrand.addAtom(oAtom1, oMono1);
		
		Hashtable strands = new Hashtable();
		strands.putAll(oStrand.getMonomers());
		assertTrue(strands.contains(oMono1));
		oStrand.removeMonomer("TRP279");
		strands.clear();
		strands.putAll(oStrand.getMonomers());
		assertFalse(strands.contains(oMono1));
	}
	
	public void testGetMonomers()	{
		Strand oStrand = new Strand();
		Monomer oMono1 = new Monomer();
		oMono1.setMonomerName(new String("TRP279"));
		Monomer oMono2 = new Monomer();
		oMono2.setMonomerName(new String("HOH"));
		Atom oAtom2 = new Atom("C2");
		Atom oAtom3 = new Atom("C3");
		oStrand.addAtom(oAtom2, oMono1);
		oStrand.addAtom(oAtom3, oMono2);
		Hashtable monomers = new Hashtable();
		Monomer oMon = new Monomer();
		oMon.setMonomerName("");
		oMon.setMonomerType("UNKNOWN");
		monomers.put("", oMon);
		monomers.put("TRP279", oMono1);
		monomers.put("HOH", oMono2);
		
		assertEquals(monomers.keySet(), oStrand.getMonomerNames());
	}

    /**
     * Method to test wether the class complies with RFC #9.
     */
    public void testToString() {
        Strand oStrand = new Strand();
		Monomer oMono1 = new Monomer();
		oMono1.setMonomerName(new String("TRP279"));
		Monomer oMono2 = new Monomer();
		oMono2.setMonomerName(new String("HOH"));
		Atom oAtom2 = new Atom("C2");
		Atom oAtom3 = new Atom("C3");
		oStrand.addAtom(oAtom2, oMono1);
		oStrand.addAtom(oAtom3, oMono2);
		Hashtable monomers = new Hashtable();
		Monomer oMon = new Monomer();
		oMon.setMonomerName("");
		oMon.setMonomerType("UNKNOWN");
		monomers.put("", oMon);
		monomers.put("TRP279", oMono1);
		monomers.put("HOH", oMono2);
        String description = oStrand.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue('\n' != description.charAt(i));
            assertTrue('\r' != description.charAt(i));
        }
    }
}
