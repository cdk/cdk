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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.test;

import java.util.Hashtable;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMonomer;
import org.openscience.cdk.interfaces.IStrand;

/**
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 * @author     Martin Eklund <martin.eklund@farmbio.uu.se>
 * @cdk.module test
 */
public class StrandTest extends TestCase {
	
	protected IChemObjectBuilder builder;
	
    public StrandTest(String name) {
		super(name);
	}

    public void setUp() {
       	builder = DefaultChemObjectBuilder.getInstance();
    }

    public static Test suite() {
		return new TestSuite(StrandTest.class);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(new TestSuite(StrandTest.class));
	}

	public void testStrand() {
		IStrand oStrand = builder.newStrand();
		assertNotNull(oStrand);
		assertEquals(oStrand.getMonomerCount(), 0);

		IMonomer oMono1 = builder.newMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		IMonomer oMono2 = builder.newMonomer();
		oMono2.setMonomerName(new String("HOH"));
		IMonomer oMono3 = builder.newMonomer();
		oMono3.setMonomerName(new String("GLYA16"));
		IAtom oAtom1 = builder.newAtom("C1");
		IAtom oAtom2 = builder.newAtom("C2");
		IAtom oAtom3 = builder.newAtom("C3");
		IAtom oAtom4 = builder.newAtom("C4");
		IAtom oAtom5 = builder.newAtom("C5");
		
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
		IStrand oStrand = builder.newStrand();
		oStrand.setStrandName("A");
		
		assertEquals("A", oStrand.getStrandName());
	}
	
	public void testGetStrandType()	{
		IStrand oStrand = builder.newStrand();
		oStrand.setStrandType("DNA");
		
		assertEquals("DNA", oStrand.getStrandType());
	}
	
	/** The methods above effectively test SetStrandName and
	 * SetStrandType as well, but I include SetStrandName and
	 * SetStrandType explicitly as well (for concinstency).  
	 */
	
	public void testSetStrandName_String()	{
		IStrand oStrand = builder.newStrand();
		oStrand.setStrandName("A");
		
		assertEquals("A", oStrand.getStrandName());
	}
	
	public void testSetStrandType_String()	{
		IStrand oStrand = builder.newStrand();
		oStrand.setStrandType("DNA");
		
		assertEquals("DNA", oStrand.getStrandType());
	}
	
	public void testAddAtom_IAtom() {
		IStrand oStrand = builder.newStrand();
		IAtom oAtom1 = builder.newAtom("C1");
		IAtom oAtom2 = builder.newAtom("C2");
		oStrand.addAtom(oAtom1);
		oStrand.addAtom(oAtom2);

		assertEquals(2, oStrand.getAtomCount());
	}
    
	public void testAddAtom_IAtom_IMonomer() {
		IStrand oStrand = builder.newStrand();
		IMonomer oMono1 = builder.newMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		IAtom oAtom1 = builder.newAtom("C1");
		IAtom oAtom2 = builder.newAtom("C2");
		IAtom oAtom3 = builder.newAtom("C3");
		oStrand.addAtom(oAtom1);
		oStrand.addAtom(oAtom2);
		oStrand.addAtom(oAtom3, oMono1);

		assertEquals(2, oStrand.getMonomer("").getAtomCount());
		assertEquals(1, oStrand.getMonomer("TRP279").getAtomCount());
	}
	
	public void testGetMonomerCount() {
		IStrand oStrand = builder.newStrand();
		IMonomer oMono1 = builder.newMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		IMonomer oMono2 = builder.newMonomer();
		oMono2.setMonomerName(new String("HOH"));
		IAtom oAtom2 = builder.newAtom("C2");
		IAtom oAtom3 = builder.newAtom("C3");
		oStrand.addAtom(oAtom2, oMono1);
		oStrand.addAtom(oAtom3, oMono2);

		assertEquals(2, oStrand.getMonomerCount());
	}
	 
	public void testGetMonomer_String() {
		IStrand oStrand = builder.newStrand();
		IMonomer oMono1 = builder.newMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		IMonomer oMono2 = builder.newMonomer();
		oMono2.setMonomerName(new String("HOH"));
		IAtom oAtom2 = builder.newAtom("C2");
		IAtom oAtom3 = builder.newAtom("C3");
		oStrand.addAtom(oAtom2, oMono1);
		oStrand.addAtom(oAtom3, oMono2);

		assertEquals(oMono1, oStrand.getMonomer("TRP279"));
		assertEquals(oMono2, oStrand.getMonomer("HOH"));
		assertNull(oStrand.getMonomer("TEST"));
	}
	
	public void testGetMonomerNames() {
		IStrand oStrand = builder.newStrand();
		IMonomer oMono1 = builder.newMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		IMonomer oMono2 = builder.newMonomer();
		oMono2.setMonomerName(new String("HOH"));
		IAtom oAtom2 = builder.newAtom("C2");
		IAtom oAtom3 = builder.newAtom("C3");
		oStrand.addAtom(oAtom2, oMono1);
		oStrand.addAtom(oAtom3, oMono2);
		Hashtable monomers = new Hashtable();
		IMonomer oMon = builder.newMonomer();
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
		IStrand oStrand = builder.newStrand();
		IMonomer oMono1 = builder.newMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		IAtom oAtom1 = builder.newAtom("C1");
		oStrand.addAtom(oAtom1, oMono1);		
		assertTrue(oStrand.getMonomerNames().contains(oMono1.getMonomerName()));
		assertEquals(1, oStrand.getAtomCount());
		oStrand.removeMonomer("TRP279");
		assertFalse(oStrand.getMonomerNames().contains(oMono1.getMonomerName()));
		assertEquals(0, oStrand.getAtomCount());
	}
	
	public void testGetMonomers()	{
		IStrand oStrand = builder.newStrand();
		IMonomer oMono1 = builder.newMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		IMonomer oMono2 = builder.newMonomer();
		oMono2.setMonomerName(new String("HOH"));
		IAtom oAtom2 = builder.newAtom("C2");
		IAtom oAtom3 = builder.newAtom("C3");
		oStrand.addAtom(oAtom2, oMono1);
		oStrand.addAtom(oAtom3, oMono2);
		Hashtable monomers = new Hashtable();
		IMonomer oMon = builder.newMonomer();
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
        IStrand oStrand = builder.newStrand();
		IMonomer oMono1 = builder.newMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		IMonomer oMono2 = builder.newMonomer();
		oMono2.setMonomerName(new String("HOH"));
		IAtom oAtom2 = builder.newAtom("C2");
		IAtom oAtom3 = builder.newAtom("C3");
		oStrand.addAtom(oAtom2, oMono1);
		oStrand.addAtom(oAtom3, oMono2);
		Hashtable monomers = new Hashtable();
		IMonomer oMon = builder.newMonomer();
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
