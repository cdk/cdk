/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
 * 
 */

package org.openscience.cdk.test;

import java.util.Hashtable;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.BioPolymer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Monomer;
import org.openscience.cdk.Strand;
import org.openscience.cdk.interfaces.IBioPolymer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * Checks the functionality of the BioPolymer class.
 *
 * @cdk.module test-data
 *
 * @see org.openscience.cdk.BioPolymer
 */
public class BioPolymerTest extends CDKTestCase {
	
	protected IChemObjectBuilder builder;

	public BioPolymerTest(String name) {
        super(name);
    }

    public void setUp() {
    	builder = DefaultChemObjectBuilder.getInstance();
    }

    public static Test suite() {
        return new TestSuite(BioPolymerTest.class);
    }
    
	public void testBioPolymer() {
		BioPolymer oBioPolymer = new BioPolymer();
		assertNotNull(oBioPolymer);
		assertEquals(oBioPolymer.getMonomerCount(), 0);
		
		Strand oStrand1 = new Strand();
		oStrand1.setStrandName("A");
		Strand oStrand2 = new Strand();
		oStrand2.setStrandName("B");
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
		
		oBioPolymer.addAtom(oAtom1);
		oBioPolymer.addAtom(oAtom2, oStrand1);
		oBioPolymer.addAtom(oAtom3, oMono1, oStrand1);
		oBioPolymer.addAtom(oAtom4, oMono2, oStrand2);
		oBioPolymer.addAtom(oAtom5, oMono3, oStrand2);
		assertNotNull(oBioPolymer.getAtom(0));
		assertNotNull(oBioPolymer.getAtom(1));
		assertNotNull(oBioPolymer.getAtom(2));
		assertNotNull(oBioPolymer.getAtom(3));
		assertNotNull(oBioPolymer.getAtom(4));
		assertEquals(oAtom1, oBioPolymer.getAtom(0));
		assertEquals(oAtom2, oBioPolymer.getAtom(1));
		assertEquals(oAtom3, oBioPolymer.getAtom(2));
		assertEquals(oAtom4, oBioPolymer.getAtom(3));
		assertEquals(oAtom5, oBioPolymer.getAtom(4));

		assertNull(oBioPolymer.getMonomer("0815", "A"));
		assertNull(oBioPolymer.getMonomer("0815", "B"));
		assertNull(oBioPolymer.getMonomer("0815", ""));
		assertNull(oBioPolymer.getStrand(""));
		assertNotNull(oBioPolymer.getMonomer("TRP279", "A"));
		assertEquals(oMono1, oBioPolymer.getMonomer("TRP279", "A"));
		assertEquals(oBioPolymer.getMonomer("TRP279", "A").getAtomCount(), 1);
		assertNotNull(oBioPolymer.getMonomer("HOH", "B"));
		assertEquals(oMono2, oBioPolymer.getMonomer("HOH", "B"));
		assertEquals(oBioPolymer.getMonomer("HOH", "B").getAtomCount(), 1);
		assertEquals(oBioPolymer.getStrand("B").getAtomCount(), 2);
		assertEquals(oBioPolymer.getStrand("B").getMonomerCount(), 2);
		assertNull(oBioPolymer.getStrand("C"));
		assertNotNull(oBioPolymer.getStrand("B"));
	}
	
	public void testGetMonomerCount() {
		BioPolymer oBioPolymer = new BioPolymer();
		assertEquals(0, oBioPolymer.getMonomerCount());
		
		Strand oStrand1 = new Strand();
		oStrand1.setStrandName("A");
		Strand oStrand2 = new Strand();
		oStrand2.setStrandName("B");
		Monomer oMono1 = new Monomer();
		oMono1.setMonomerName(new String("TRP279"));
		Monomer oMono2 = new Monomer();
		oMono2.setMonomerName(new String("HOH"));
		Atom oAtom1 = new Atom("C1");
		Atom oAtom2 = new Atom("C2");
		Atom oAtom3 = new Atom("C3");
		oBioPolymer.addAtom(oAtom1);
		oBioPolymer.addAtom(oAtom2, oMono1, oStrand1);
		oBioPolymer.addAtom(oAtom3, oMono2, oStrand2);
		assertNotNull(oBioPolymer.getAtom(0));
		assertNotNull(oBioPolymer.getAtom(1));
		assertNotNull(oBioPolymer.getAtom(2));
		assertEquals(oAtom1, oBioPolymer.getAtom(0));
		assertEquals(oAtom2, oBioPolymer.getAtom(1));
		assertEquals(oAtom3, oBioPolymer.getAtom(2));

		assertEquals(2, oBioPolymer.getMonomerCount());
	}
	
	public void testGetMonomerNames() {
		BioPolymer oBioPolymer = new BioPolymer();
		assertEquals(0, oBioPolymer.getMonomerNames().size());
		
		Strand oStrand1 = new Strand();
		oStrand1.setStrandName("A");
		Strand oStrand2 = new Strand();
		oStrand2.setStrandName("B");
		Monomer oMono1 = new Monomer();
		oMono1.setMonomerName(new String("TRP279"));
		Monomer oMono2 = new Monomer();
		oMono2.setMonomerName(new String("HOH"));
		Atom oAtom1 = new Atom("C1");
		Atom oAtom2 = new Atom("C2");
		Atom oAtom3 = new Atom("C3");
		oBioPolymer.addAtom(oAtom1);
		oBioPolymer.addAtom(oAtom2, oMono1, oStrand1);
		oBioPolymer.addAtom(oAtom3, oMono2, oStrand2);
		assertNotNull(oBioPolymer.getAtom(0));
		assertNotNull(oBioPolymer.getAtom(1));
		assertNotNull(oBioPolymer.getAtom(2));
		assertEquals(oAtom1, oBioPolymer.getAtom(0));
		assertEquals(oAtom2, oBioPolymer.getAtom(1));
		assertEquals(oAtom3, oBioPolymer.getAtom(2));

		assertEquals(3, oBioPolymer.getMonomerNames().size());
		assertTrue(oBioPolymer.getMonomerNames().contains(oMono1.getMonomerName()));
		assertTrue(oBioPolymer.getMonomerNames().contains(oMono2.getMonomerName()));
	}
	
	public void testGetMonomer_String_String() {
		BioPolymer oBioPolymer = new BioPolymer();
		
		Strand oStrand1 = new Strand();
		oStrand1.setStrandName("A");
		Strand oStrand2 = new Strand();
		oStrand2.setStrandName("B");
		Monomer oMono1 = new Monomer();
		oMono1.setMonomerName(new String("TRP279"));
		Monomer oMono2 = new Monomer();
		oMono2.setMonomerName(new String("HOH"));
		Atom oAtom1 = new Atom("C1");
		Atom oAtom2 = new Atom("C2");
		Atom oAtom3 = new Atom("C3");
		oBioPolymer.addAtom(oAtom1, oMono1, oStrand1);
		oBioPolymer.addAtom(oAtom2, oMono1, oStrand1);
		oBioPolymer.addAtom(oAtom3, oMono2, oStrand2);

		assertEquals(oMono1, oBioPolymer.getMonomer("TRP279", "A"));
		assertEquals(oMono2, oBioPolymer.getMonomer("HOH", "B"));
	}
    
	public void testAddAtom_IAtom() {
		BioPolymer oBioPolymer = new BioPolymer();
		
		Atom oAtom1 = new Atom("C1");
		Atom oAtom2 = new Atom("C2");
		oBioPolymer.addAtom(oAtom1);
		oBioPolymer.addAtom(oAtom2);

		assertEquals(2, oBioPolymer.getAtomCount());
	}
    
	public void testAddAtom_IAtom_IStrand() {
		BioPolymer oBioPolymer = new BioPolymer();
		Strand oStrand1 = new Strand();
		oStrand1.setStrandName("A");
		Monomer oMono1 = new Monomer();
		oMono1.setMonomerName(new String("TRP279"));
		Atom oAtom1 = new Atom("C1");
		Atom oAtom2 = new Atom("C2");
		Atom oAtom3 = new Atom("C3");
		oBioPolymer.addAtom(oAtom1, oStrand1);
		oBioPolymer.addAtom(oAtom2, oStrand1);
		oBioPolymer.addAtom(oAtom3, oMono1, oStrand1);

		assertEquals(2, oBioPolymer.getMonomer("", "A").getAtomCount());
		assertEquals(1, oBioPolymer.getMonomer("TRP279", "A").getAtomCount());
		assertEquals(3, oBioPolymer.getAtomCount());
	}
	
	public void testAddAtom_IAtom_IMonomer_IStrand()	{
		BioPolymer oBioPolymer = new BioPolymer();
		Strand oStrand1 = new Strand();
		oStrand1.setStrandName("A");
		Monomer oMono1 = new Monomer();
		oMono1.setMonomerName(new String("TRP279"));
		Atom oAtom1 = new Atom("C1");
		Atom oAtom2 = new Atom("C2");
		oBioPolymer.addAtom(oAtom1, oMono1, oStrand1);
		oBioPolymer.addAtom(oAtom2, oMono1, oStrand1);
		oBioPolymer.addAtom(oAtom1, null, oStrand1);
		
		assertEquals(2, oBioPolymer.getMonomer("TRP279", "A").getAtomCount());
		assertEquals(0, oBioPolymer.getMonomer("", "A").getAtomCount());
	}
	
	public void testGetStrandCount()	{
		BioPolymer oBioPolymer = new BioPolymer();
		Strand oStrand1 = new Strand();
		oStrand1.setStrandName("A");
		Monomer oMono1 = new Monomer();
		oMono1.setMonomerName(new String("TRP279"));
		Atom oAtom1 = new Atom("C1");
		oBioPolymer.addAtom(oAtom1, oMono1, oStrand1);

		assertEquals(1, oBioPolymer.getStrandCount());
	}
	
	public void testGetStrand_String()	{
		BioPolymer oBioPolymer = new BioPolymer();
		Strand oStrand1 = new Strand();
		oStrand1.setStrandName("A");
		Monomer oMono1 = new Monomer();
		oMono1.setMonomerName(new String("TRP279"));
		Atom oAtom1 = new Atom("C1");
		oBioPolymer.addAtom(oAtom1, oMono1, oStrand1);
		
		assertEquals(oStrand1, oBioPolymer.getStrand("A"));
	}
	
	public void testGetStrandNames()	{
		BioPolymer oBioPolymer = new BioPolymer();
		Strand oStrand1 = new Strand();
		Strand oStrand2 = new Strand();
		oStrand1.setStrandName("A");
		oStrand2.setStrandName("B");
		Monomer oMono1 = new Monomer();
		oMono1.setMonomerName(new String("TRP279"));
		Monomer oMono2 = new Monomer();
		oMono2.setMonomerName(new String("GLY123"));
		Atom oAtom1 = new Atom("C1");
		Atom oAtom2 = new Atom("C2");
		oBioPolymer.addAtom(oAtom1, oMono1, oStrand1);
		oBioPolymer.addAtom(oAtom2, oMono2, oStrand2);
		Hashtable strands = new Hashtable();
		strands = new Hashtable();
		strands.put("A", oStrand1);
		strands.put("B", oStrand2);
		
		assertEquals(strands.keySet(), oBioPolymer.getStrandNames());
	}
	
	public void testRemoveStrand_String()	{
		BioPolymer oBioPolymer = new BioPolymer();
		Strand oStrand1 = new Strand();
		oStrand1.setStrandName("A");
		Monomer oMono1 = new Monomer();
		oMono1.setMonomerName(new String("TRP279"));
		Atom oAtom1 = new Atom("C1");
		oBioPolymer.addAtom(oAtom1, oMono1, oStrand1);
		
		assertTrue(oBioPolymer.getStrandNames().contains(oStrand1.getStrandName()));
		assertEquals(1, oBioPolymer.getAtomCount());
		oBioPolymer.removeStrand("A");
		assertFalse(oBioPolymer.getStrandNames().contains(oStrand1.getStrandName()));
		assertEquals(0, oBioPolymer.getAtomCount());
	}
	
	public void testGetStrands()	{
		BioPolymer oBioPolymer = new BioPolymer();
		Strand oStrand1 = new Strand();
		Strand oStrand2 = new Strand();
		oStrand1.setStrandName("A");
		oStrand2.setStrandName("B");
		Monomer oMono1 = new Monomer();
		oMono1.setMonomerName(new String("TRP279"));
		Monomer oMono2 = new Monomer();
		oMono2.setMonomerName(new String("GLY123"));
		Atom oAtom1 = new Atom("C1");
		Atom oAtom2 = new Atom("C2");
		oBioPolymer.addAtom(oAtom1, oMono1, oStrand1);
		oBioPolymer.addAtom(oAtom2, oMono2, oStrand2);
		Hashtable strands = new Hashtable();
		strands = new Hashtable();
		strands.put("A", oStrand1);
		strands.put("B", oStrand2);
		
		assertEquals(strands, oBioPolymer.getStrands());
	}
    
    /**
     * Method to test wether the class complies with RFC #9.
     */
    public void testToString() {
        BioPolymer bp = new BioPolymer();
        String description = bp.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue('\n' != description.charAt(i));
            assertTrue('\r' != description.charAt(i));
        }
    }
    
    /**
     * Method to test the clone() method
     */
    public void testClone() throws Exception {
    	IBioPolymer polymer = builder.newBioPolymer();
        Object clone = polymer.clone();
        assertTrue(clone instanceof IBioPolymer);
    }

}
