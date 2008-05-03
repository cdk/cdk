/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 *  Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
package org.openscience.cdk.protein.data;

import java.util.Hashtable;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Monomer;
import org.openscience.cdk.Strand;
import org.openscience.cdk.interfaces.IBioPolymer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IPDBPolymer;
import org.openscience.cdk.protein.data.PDBAtom;
import org.openscience.cdk.protein.data.PDBMonomer;
import org.openscience.cdk.protein.data.PDBPolymer;
import org.openscience.cdk.NewCDKTestCase;

/**
 * Checks the functionality of the PDBPolymer class.
 *
 * @cdk.module test-data
 *
 * @see PDBPolymer
 */
public class PDBPolymerTest extends NewCDKTestCase {
	
	protected static IChemObjectBuilder builder;

    @BeforeClass public static void setUp() {
    	builder = DefaultChemObjectBuilder.getInstance();
    }

	@Test public void testPDBPolymer() {
		PDBPolymer pdbPolymer = new PDBPolymer();
		Assert.assertNotNull(pdbPolymer);
		Assert.assertEquals(pdbPolymer.getMonomerCount(), 0);
		
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
		PDBAtom oPDBAtom1 = new PDBAtom("C1");
		PDBAtom oPDBAtom2 = new PDBAtom("C2");
		PDBAtom oPDBAtom3 = new PDBAtom("C3");
		PDBAtom oPDBAtom4 = new PDBAtom("C4");
		PDBAtom oPDBAtom5 = new PDBAtom("C5");
		
		pdbPolymer.addAtom(oPDBAtom1);
		pdbPolymer.addAtom(oPDBAtom2, oStrand1);
		pdbPolymer.addAtom(oPDBAtom3, oMono1, oStrand1);
		pdbPolymer.addAtom(oPDBAtom4, oMono2, oStrand2);
		pdbPolymer.addAtom(oPDBAtom5, oMono3, oStrand2);
		Assert.assertNotNull(pdbPolymer.getAtom(0));
		Assert.assertNotNull(pdbPolymer.getAtom(1));
		Assert.assertNotNull(pdbPolymer.getAtom(2));
		Assert.assertNotNull(pdbPolymer.getAtom(3));
		Assert.assertNotNull(pdbPolymer.getAtom(4));
		Assert.assertEquals(oPDBAtom1, pdbPolymer.getAtom(0));
		Assert.assertEquals(oPDBAtom2, pdbPolymer.getAtom(1));
		Assert.assertEquals(oPDBAtom3, pdbPolymer.getAtom(2));
		Assert.assertEquals(oPDBAtom4, pdbPolymer.getAtom(3));
		Assert.assertEquals(oPDBAtom5, pdbPolymer.getAtom(4));

		Assert.assertNull(pdbPolymer.getMonomer("0815", "A"));
		Assert.assertNull(pdbPolymer.getMonomer("0815", "B"));
		Assert.assertNull(pdbPolymer.getMonomer("0815", ""));
		Assert.assertNull(pdbPolymer.getStrand(""));
		Assert.assertNotNull(pdbPolymer.getMonomer("TRP279", "A"));
		Assert.assertEquals(oMono1, pdbPolymer.getMonomer("TRP279", "A"));
		Assert.assertEquals(pdbPolymer.getMonomer("TRP279", "A").getAtomCount(), 1);
		Assert.assertNotNull(pdbPolymer.getMonomer("HOH", "B"));
		Assert.assertEquals(oMono2, pdbPolymer.getMonomer("HOH", "B"));
		Assert.assertEquals(pdbPolymer.getMonomer("HOH", "B").getAtomCount(), 1);
		Assert.assertEquals(pdbPolymer.getStrand("B").getAtomCount(), 2);
		Assert.assertEquals(pdbPolymer.getStrand("B").getMonomerCount(), 2);
		Assert.assertNull(pdbPolymer.getStrand("C"));
		Assert.assertNotNull(pdbPolymer.getStrand("B"));
	}
	
	@Test public void testGetStructures() {
		
	}
	
	@Test public void testAddStructure_IPDBStructure(){
		
	}
	
	@Test public void testGetMonomerNamesInSequentialOrder() {
		
	}
	
	@Test public void testGetMonomerCount() {
		PDBPolymer pdbPolymer = new PDBPolymer();
		Assert.assertEquals(0, pdbPolymer.getMonomerCount());
		
		Strand oStrand1 = new Strand();
		oStrand1.setStrandName("A");
		Strand oStrand2 = new Strand();
		oStrand2.setStrandName("B");
		Monomer oMono1 = new Monomer();
		oMono1.setMonomerName(new String("TRP279"));
		Monomer oMono2 = new Monomer();
		oMono2.setMonomerName(new String("HOH"));
		PDBAtom oPDBAtom1 = new PDBAtom("C1");
		PDBAtom oPDBAtom2 = new PDBAtom("C2");
		PDBAtom oPDBAtom3 = new PDBAtom("C3");
		pdbPolymer.addAtom(oPDBAtom1);
		pdbPolymer.addAtom(oPDBAtom2, oMono1, oStrand1);
		pdbPolymer.addAtom(oPDBAtom3, oMono2, oStrand2);
		Assert.assertNotNull(pdbPolymer.getAtom(0));
		Assert.assertNotNull(pdbPolymer.getAtom(1));
		Assert.assertNotNull(pdbPolymer.getAtom(2));
		Assert.assertEquals(oPDBAtom1, pdbPolymer.getAtom(0));
		Assert.assertEquals(oPDBAtom2, pdbPolymer.getAtom(1));
		Assert.assertEquals(oPDBAtom3, pdbPolymer.getAtom(2));

		Assert.assertEquals(2, pdbPolymer.getMonomerCount());
	}
	
	@Test public void testGetMonomerNames() {
		PDBPolymer pdbPolymer = new PDBPolymer();
		Assert.assertEquals(0, pdbPolymer.getMonomerNames().size());
		
		Strand oStrand1 = new Strand();
		oStrand1.setStrandName("A");
		Strand oStrand2 = new Strand();
		oStrand2.setStrandName("B");
		Monomer oMono1 = new Monomer();
		oMono1.setMonomerName(new String("TRP279"));
		Monomer oMono2 = new Monomer();
		oMono2.setMonomerName(new String("HOH"));
		PDBAtom oPDBAtom1 = new PDBAtom("C1");
		PDBAtom oPDBAtom2 = new PDBAtom("C2");
		PDBAtom oPDBAtom3 = new PDBAtom("C3");
		pdbPolymer.addAtom(oPDBAtom1);
		pdbPolymer.addAtom(oPDBAtom2, oMono1, oStrand1);
		pdbPolymer.addAtom(oPDBAtom3, oMono2, oStrand2);
		Assert.assertNotNull(pdbPolymer.getAtom(0));
		Assert.assertNotNull(pdbPolymer.getAtom(1));
		Assert.assertNotNull(pdbPolymer.getAtom(2));
		Assert.assertEquals(oPDBAtom1, pdbPolymer.getAtom(0));
		Assert.assertEquals(oPDBAtom2, pdbPolymer.getAtom(1));
		Assert.assertEquals(oPDBAtom3, pdbPolymer.getAtom(2));

		Assert.assertEquals(3, pdbPolymer.getMonomerNames().size());
		Assert.assertTrue(pdbPolymer.getMonomerNames().contains(oMono1.getMonomerName()));
		Assert.assertTrue(pdbPolymer.getMonomerNames().contains(oMono2.getMonomerName()));
	}
	
	@Test public void testGetMonomer_String_String() {
		PDBPolymer pdbPolymer = new PDBPolymer();
		
		Strand oStrand1 = new Strand();
		oStrand1.setStrandName("A");
		Strand oStrand2 = new Strand();
		oStrand2.setStrandName("B");
		Monomer oMono1 = new Monomer();
		oMono1.setMonomerName(new String("TRP279"));
		Monomer oMono2 = new Monomer();
		oMono2.setMonomerName(new String("HOH"));
		PDBAtom oPDBAtom1 = new PDBAtom("C1");
		PDBAtom oPDBAtom2 = new PDBAtom("C2");
		PDBAtom oPDBAtom3 = new PDBAtom("C3");
		pdbPolymer.addAtom(oPDBAtom1, oMono1, oStrand1);
		pdbPolymer.addAtom(oPDBAtom2, oMono1, oStrand1);
		pdbPolymer.addAtom(oPDBAtom3, oMono2, oStrand2);

		Assert.assertEquals(oMono1, pdbPolymer.getMonomer("TRP279", "A"));
		Assert.assertEquals(oMono2, pdbPolymer.getMonomer("HOH", "B"));
	}
    
	@Test public void testAddAtom_IPDBAtom() {
		PDBPolymer pdbPolymer = new PDBPolymer();
		
		PDBAtom oPDBAtom1 = new PDBAtom("C1");
		PDBAtom oPDBAtom2 = new PDBAtom("C2");
		pdbPolymer.addAtom(oPDBAtom1);
		pdbPolymer.addAtom(oPDBAtom2);

		Assert.assertEquals(2, pdbPolymer.getAtomCount());
	}
    
	@Test public void testAddAtom_IPDBAtom_IStrand() {
		PDBPolymer pdbPolymer = new PDBPolymer();
		Strand oStrand1 = new Strand();
		oStrand1.setStrandName("A");
		PDBMonomer oMono1 = new PDBMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		PDBAtom oPDBAtom1 = new PDBAtom("C1");
		PDBAtom oPDBAtom2 = new PDBAtom("C2");
		PDBAtom oPDBAtom3 = new PDBAtom("C3");
		pdbPolymer.addAtom(oPDBAtom1, oStrand1);
		pdbPolymer.addAtom(oPDBAtom2, oStrand1);
		pdbPolymer.addAtom(oPDBAtom3, oMono1, oStrand1);

		Assert.assertEquals(2, pdbPolymer.getMonomer("", "A").getAtomCount());
		Assert.assertEquals(1, pdbPolymer.getMonomer("TRP279", "A").getAtomCount());
		Assert.assertEquals(3, pdbPolymer.getAtomCount());
	}
	
	@Test public void testAddAtom_IPDBAtom_IMonomer_IStrand()	{
		PDBPolymer pdbPolymer = new PDBPolymer();
		Strand oStrand1 = new Strand();
		oStrand1.setStrandName("A");
		PDBMonomer oMono1 = new PDBMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		PDBAtom oPDBAtom1 = new PDBAtom("C1");
		PDBAtom oPDBAtom2 = new PDBAtom("C2");
		PDBAtom oPDBAtom3 = new PDBAtom("C3");
		pdbPolymer.addAtom(oPDBAtom1, oStrand1);
		pdbPolymer.addAtom(oPDBAtom2, oStrand1);
		pdbPolymer.addAtom(oPDBAtom3, oMono1, oStrand1);

		Assert.assertEquals(2, pdbPolymer.getMonomer("", "A").getAtomCount());
		Assert.assertEquals(1, pdbPolymer.getMonomer("TRP279", "A").getAtomCount());
		Assert.assertEquals(3, pdbPolymer.getAtomCount());
	}

	
	@Test public void testAddAtom_IPDBAtom_IMonomer() {
		PDBPolymer pdbPolymer = new PDBPolymer();
		PDBMonomer oMono1 = new PDBMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		Strand oStrand1 = new Strand();
		oStrand1.setStrandName("A");
		PDBAtom oPDBAtom1 = new PDBAtom("C1");
		pdbPolymer.addAtom(oPDBAtom1, oMono1, oStrand1);

		Assert.assertEquals(1, pdbPolymer.getMonomer("TRP279", "A").getAtomCount());
	}
	
	@Test public void testGetStrandCount()	{
		PDBPolymer pdbPolymer = new PDBPolymer();
		Strand oStrand1 = new Strand();
		oStrand1.setStrandName("A");
		Monomer oMono1 = new Monomer();
		oMono1.setMonomerName(new String("TRP279"));
		PDBAtom oPDBAtom1 = new PDBAtom("C1");
		pdbPolymer.addAtom(oPDBAtom1, oMono1, oStrand1);

		Assert.assertEquals(1, pdbPolymer.getStrandCount());
	}
	
	@Test public void testGetStrand_String()	{
		PDBPolymer pdbPolymer = new PDBPolymer();
		Strand oStrand1 = new Strand();
		oStrand1.setStrandName("A");
		Monomer oMono1 = new Monomer();
		oMono1.setMonomerName(new String("TRP279"));
		PDBAtom oPDBAtom1 = new PDBAtom("C1");
		pdbPolymer.addAtom(oPDBAtom1, oMono1, oStrand1);
		
		Assert.assertEquals(oStrand1, pdbPolymer.getStrand("A"));
	}
	
	@Test public void testGetStrandNames()	{
		PDBPolymer pdbPolymer = new PDBPolymer();
		Strand oStrand1 = new Strand();
		Strand oStrand2 = new Strand();
		oStrand1.setStrandName("A");
		oStrand2.setStrandName("B");
		Monomer oMono1 = new Monomer();
		oMono1.setMonomerName(new String("TRP279"));
		Monomer oMono2 = new Monomer();
		oMono2.setMonomerName(new String("GLY123"));
		PDBAtom oPDBAtom1 = new PDBAtom("C1");
		PDBAtom oPDBAtom2 = new PDBAtom("C2");
		pdbPolymer.addAtom(oPDBAtom1, oMono1, oStrand1);
		pdbPolymer.addAtom(oPDBAtom2, oMono2, oStrand2);
		Hashtable strands = new Hashtable();
		strands = new Hashtable();
		strands.put("A", oStrand1);
		strands.put("B", oStrand2);
		
		Assert.assertEquals(strands.keySet(), pdbPolymer.getStrandNames());
	}
	
	@Test public void testRemoveStrand_String()	{
		PDBPolymer pdbPolymer = new PDBPolymer();
		Strand oStrand1 = new Strand();
		oStrand1.setStrandName("A");
		Monomer oMono1 = new Monomer();
		oMono1.setMonomerName(new String("TRP279"));
		PDBAtom oPDBAtom1 = new PDBAtom("C1");
		pdbPolymer.addAtom(oPDBAtom1, oMono1, oStrand1);
		
		Assert.assertTrue(pdbPolymer.getStrandNames().contains(oStrand1.getStrandName()));
		Assert.assertEquals(1, pdbPolymer.getAtomCount());
		pdbPolymer.removeStrand("A");
		Assert.assertFalse(pdbPolymer.getStrandNames().contains(oStrand1.getStrandName()));
		Assert.assertEquals(0, pdbPolymer.getAtomCount());
	}
	
	@Test public void testGetStrands()	{
		PDBPolymer pdbPolymer = new PDBPolymer();
		Strand oStrand1 = new Strand();
		Strand oStrand2 = new Strand();
		oStrand1.setStrandName("A");
		oStrand2.setStrandName("B");
		Monomer oMono1 = new Monomer();
		oMono1.setMonomerName(new String("TRP279"));
		Monomer oMono2 = new Monomer();
		oMono2.setMonomerName(new String("GLY123"));
		PDBAtom oPDBAtom1 = new PDBAtom("C1");
		PDBAtom oPDBAtom2 = new PDBAtom("C2");
		pdbPolymer.addAtom(oPDBAtom1, oMono1, oStrand1);
		pdbPolymer.addAtom(oPDBAtom2, oMono2, oStrand2);
		Hashtable strands = new Hashtable();
		strands = new Hashtable();
		strands.put("A", oStrand1);
		strands.put("B", oStrand2);
		
		Assert.assertEquals(strands, pdbPolymer.getStrands());
	}
    
    /**
     * Method to test whether the class complies with RFC #9.
     */
    @Test public void testToString() {
    	PDBPolymer pdbPolymer = new PDBPolymer();
        String description = pdbPolymer.toString();
        for (int i=0; i< description.length(); i++) {
            Assert.assertTrue('\n' != description.charAt(i));
            Assert.assertTrue('\r' != description.charAt(i));
        }
    }
    
    /**
     * Method to test the clone() method
     */
    @Test public void testClone() throws Exception {
    	IPDBPolymer polymer = builder.newPDBPolymer();
        Object clone = polymer.clone();
        Assert.assertTrue(clone instanceof IBioPolymer);
    }

}
