/* $RCSfile$
 * $Author: egonw $    
 * $Date: 2006-09-20 10:48:23 +0000 (Wed, 20 Sep 2006) $    
 * $Revision: 6963 $
 * 
 *  Copyright (C) 2004-2006  Miguel Rojas <miguel.rojas@uni-koeln.de>
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

package org.openscience.cdk.test.protein.data;

import java.util.Hashtable;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Monomer;
import org.openscience.cdk.Strand;
import org.openscience.cdk.interfaces.IBioPolymer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IPDBPolymer;
import org.openscience.cdk.protein.data.PDBPolymer;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Checks the functionality of the PDBPolymer class.
 *
 * @cdk.module test-data
 *
 * @see PDBPolymer
 */
public class PDBPolymerTest extends CDKTestCase {
	
	protected IChemObjectBuilder builder;

	public PDBPolymerTest(String name) {
        super(name);
    }

    public void setUp() {
    	builder = DefaultChemObjectBuilder.getInstance();
    }

    public static Test suite() {
        return new TestSuite(PDBPolymerTest.class);
    }
    
	public void testPDBPolymer() {
		PDBPolymer pdbPolymer = new PDBPolymer();
		assertNotNull(pdbPolymer);
		assertEquals(pdbPolymer.getMonomerCount(), 0);
		
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
		
		pdbPolymer.addAtom(oAtom1);
		pdbPolymer.addAtom(oAtom2, oStrand1);
		pdbPolymer.addAtom(oAtom3, oMono1, oStrand1);
		pdbPolymer.addAtom(oAtom4, oMono2, oStrand2);
		pdbPolymer.addAtom(oAtom5, oMono3, oStrand2);
		assertNotNull(pdbPolymer.getAtom(0));
		assertNotNull(pdbPolymer.getAtom(1));
		assertNotNull(pdbPolymer.getAtom(2));
		assertNotNull(pdbPolymer.getAtom(3));
		assertNotNull(pdbPolymer.getAtom(4));
		assertEquals(oAtom1, pdbPolymer.getAtom(0));
		assertEquals(oAtom2, pdbPolymer.getAtom(1));
		assertEquals(oAtom3, pdbPolymer.getAtom(2));
		assertEquals(oAtom4, pdbPolymer.getAtom(3));
		assertEquals(oAtom5, pdbPolymer.getAtom(4));

		assertNull(pdbPolymer.getMonomer("0815", "A"));
		assertNull(pdbPolymer.getMonomer("0815", "B"));
		assertNull(pdbPolymer.getMonomer("0815", ""));
		assertNull(pdbPolymer.getStrand(""));
		assertNotNull(pdbPolymer.getMonomer("TRP279", "A"));
		assertEquals(oMono1, pdbPolymer.getMonomer("TRP279", "A"));
		assertEquals(pdbPolymer.getMonomer("TRP279", "A").getAtomCount(), 1);
		assertNotNull(pdbPolymer.getMonomer("HOH", "B"));
		assertEquals(oMono2, pdbPolymer.getMonomer("HOH", "B"));
		assertEquals(pdbPolymer.getMonomer("HOH", "B").getAtomCount(), 1);
		assertEquals(pdbPolymer.getStrand("B").getAtomCount(), 2);
		assertEquals(pdbPolymer.getStrand("B").getMonomerCount(), 2);
		assertNull(pdbPolymer.getStrand("C"));
		assertNotNull(pdbPolymer.getStrand("B"));
	}
	
	public void testGetMonomerCount() {
		PDBPolymer pdbPolymer = new PDBPolymer();
		assertEquals(0, pdbPolymer.getMonomerCount());
		
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
		pdbPolymer.addAtom(oAtom1);
		pdbPolymer.addAtom(oAtom2, oMono1, oStrand1);
		pdbPolymer.addAtom(oAtom3, oMono2, oStrand2);
		assertNotNull(pdbPolymer.getAtom(0));
		assertNotNull(pdbPolymer.getAtom(1));
		assertNotNull(pdbPolymer.getAtom(2));
		assertEquals(oAtom1, pdbPolymer.getAtom(0));
		assertEquals(oAtom2, pdbPolymer.getAtom(1));
		assertEquals(oAtom3, pdbPolymer.getAtom(2));

		assertEquals(2, pdbPolymer.getMonomerCount());
	}
	
	public void testGetMonomerNames() {
		PDBPolymer pdbPolymer = new PDBPolymer();
		assertEquals(0, pdbPolymer.getMonomerNames().size());
		
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
		pdbPolymer.addAtom(oAtom1);
		pdbPolymer.addAtom(oAtom2, oMono1, oStrand1);
		pdbPolymer.addAtom(oAtom3, oMono2, oStrand2);
		assertNotNull(pdbPolymer.getAtom(0));
		assertNotNull(pdbPolymer.getAtom(1));
		assertNotNull(pdbPolymer.getAtom(2));
		assertEquals(oAtom1, pdbPolymer.getAtom(0));
		assertEquals(oAtom2, pdbPolymer.getAtom(1));
		assertEquals(oAtom3, pdbPolymer.getAtom(2));

		assertEquals(3, pdbPolymer.getMonomerNames().size());
		assertTrue(pdbPolymer.getMonomerNames().contains(oMono1.getMonomerName()));
		assertTrue(pdbPolymer.getMonomerNames().contains(oMono2.getMonomerName()));
	}
	
	public void testGetMonomer_String_String() {
		PDBPolymer pdbPolymer = new PDBPolymer();
		
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
		pdbPolymer.addAtom(oAtom1, oMono1, oStrand1);
		pdbPolymer.addAtom(oAtom2, oMono1, oStrand1);
		pdbPolymer.addAtom(oAtom3, oMono2, oStrand2);

		assertEquals(oMono1, pdbPolymer.getMonomer("TRP279", "A"));
		assertEquals(oMono2, pdbPolymer.getMonomer("HOH", "B"));
	}
    
	public void testAddAtom_IAtom() {
		PDBPolymer pdbPolymer = new PDBPolymer();
		
		Atom oAtom1 = new Atom("C1");
		Atom oAtom2 = new Atom("C2");
		pdbPolymer.addAtom(oAtom1);
		pdbPolymer.addAtom(oAtom2);

		assertEquals(2, pdbPolymer.getAtomCount());
	}
    
	public void testAddAtom_IAtom_IStrand() {
		PDBPolymer pdbPolymer = new PDBPolymer();
		Strand oStrand1 = new Strand();
		oStrand1.setStrandName("A");
		Monomer oMono1 = new Monomer();
		oMono1.setMonomerName(new String("TRP279"));
		Atom oAtom1 = new Atom("C1");
		Atom oAtom2 = new Atom("C2");
		Atom oAtom3 = new Atom("C3");
		pdbPolymer.addAtom(oAtom1, oStrand1);
		pdbPolymer.addAtom(oAtom2, oStrand1);
		pdbPolymer.addAtom(oAtom3, oMono1, oStrand1);

		assertEquals(2, pdbPolymer.getMonomer("", "A").getAtomCount());
		assertEquals(1, pdbPolymer.getMonomer("TRP279", "A").getAtomCount());
		assertEquals(3, pdbPolymer.getAtomCount());
	}
	
	public void testAddAtom_IAtom_IMonomer_IStrand()	{
		PDBPolymer pdbPolymer = new PDBPolymer();
		Strand oStrand1 = new Strand();
		oStrand1.setStrandName("A");
		Monomer oMono1 = new Monomer();
		oMono1.setMonomerName(new String("TRP279"));
		Atom oAtom1 = new Atom("C1");
		Atom oAtom2 = new Atom("C2");
		pdbPolymer.addAtom(oAtom1, oMono1, oStrand1);
		pdbPolymer.addAtom(oAtom2, oMono1, oStrand1);
		pdbPolymer.addAtom(oAtom1, null, oStrand1);
		
		assertEquals(2, pdbPolymer.getMonomer("TRP279", "A").getAtomCount());
		assertEquals(0, pdbPolymer.getMonomer("", "A").getAtomCount());
	}
	
	public void testGetStrandCount()	{
		PDBPolymer pdbPolymer = new PDBPolymer();
		Strand oStrand1 = new Strand();
		oStrand1.setStrandName("A");
		Monomer oMono1 = new Monomer();
		oMono1.setMonomerName(new String("TRP279"));
		Atom oAtom1 = new Atom("C1");
		pdbPolymer.addAtom(oAtom1, oMono1, oStrand1);

		assertEquals(1, pdbPolymer.getStrandCount());
	}
	
	public void testGetStrand_String()	{
		PDBPolymer pdbPolymer = new PDBPolymer();
		Strand oStrand1 = new Strand();
		oStrand1.setStrandName("A");
		Monomer oMono1 = new Monomer();
		oMono1.setMonomerName(new String("TRP279"));
		Atom oAtom1 = new Atom("C1");
		pdbPolymer.addAtom(oAtom1, oMono1, oStrand1);
		
		assertEquals(oStrand1, pdbPolymer.getStrand("A"));
	}
	
	public void testGetStrandNames()	{
		PDBPolymer pdbPolymer = new PDBPolymer();
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
		pdbPolymer.addAtom(oAtom1, oMono1, oStrand1);
		pdbPolymer.addAtom(oAtom2, oMono2, oStrand2);
		Hashtable strands = new Hashtable();
		strands = new Hashtable();
		strands.put("A", oStrand1);
		strands.put("B", oStrand2);
		
		assertEquals(strands.keySet(), pdbPolymer.getStrandNames());
	}
	
	public void testRemoveStrand_String()	{
		PDBPolymer pdbPolymer = new PDBPolymer();
		Strand oStrand1 = new Strand();
		oStrand1.setStrandName("A");
		Monomer oMono1 = new Monomer();
		oMono1.setMonomerName(new String("TRP279"));
		Atom oAtom1 = new Atom("C1");
		pdbPolymer.addAtom(oAtom1, oMono1, oStrand1);
		
		assertTrue(pdbPolymer.getStrandNames().contains(oStrand1.getStrandName()));
		assertEquals(1, pdbPolymer.getAtomCount());
		pdbPolymer.removeStrand("A");
		assertFalse(pdbPolymer.getStrandNames().contains(oStrand1.getStrandName()));
		assertEquals(0, pdbPolymer.getAtomCount());
	}
	
	public void testGetStrands()	{
		PDBPolymer pdbPolymer = new PDBPolymer();
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
		pdbPolymer.addAtom(oAtom1, oMono1, oStrand1);
		pdbPolymer.addAtom(oAtom2, oMono2, oStrand2);
		Hashtable strands = new Hashtable();
		strands = new Hashtable();
		strands.put("A", oStrand1);
		strands.put("B", oStrand2);
		
		assertEquals(strands, pdbPolymer.getStrands());
	}
    
    /**
     * Method to test wether the class complies with RFC #9.
     */
    public void testToString() {
    	PDBPolymer pdbPolymer = new PDBPolymer();
        String description = pdbPolymer.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue('\n' != description.charAt(i));
            assertTrue('\r' != description.charAt(i));
        }
    }
    
    /**
     * Method to test the clone() method
     */
    public void testClone() throws Exception {
    	IPDBPolymer polymer = builder.newPDBPolymer();
        Object clone = polymer.clone();
        assertTrue(clone instanceof IBioPolymer);
    }

}
