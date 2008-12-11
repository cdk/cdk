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
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.BioPolymerTest;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IBioPolymer;
import org.openscience.cdk.interfaces.IMonomer;
import org.openscience.cdk.interfaces.IPDBAtom;
import org.openscience.cdk.interfaces.IPDBMonomer;
import org.openscience.cdk.interfaces.IPDBPolymer;
import org.openscience.cdk.interfaces.IStrand;

/**
 * Checks the functionality of the PDBPolymer class.
 *
 * @cdk.module test-data
 *
 * @see PDBPolymer
 */
public class PDBPolymerTest extends BioPolymerTest {
	
    @BeforeClass public static void setUp() {
        setBuilder(DefaultChemObjectBuilder.getInstance());
    }

	@Test public void testPDBPolymer() {
		IPDBPolymer pdbPolymer = getBuilder().newPDBPolymer();
		Assert.assertNotNull(pdbPolymer);
		Assert.assertEquals(pdbPolymer.getMonomerCount(), 0);
		
		IStrand oStrand1 = getBuilder().newStrand();
		oStrand1.setStrandName("A");
		IStrand oStrand2 = getBuilder().newStrand();
		oStrand2.setStrandName("B");
		IMonomer oMono1 = getBuilder().newMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		IMonomer oMono2 = getBuilder().newMonomer();
		oMono2.setMonomerName(new String("HOH"));
		IMonomer oMono3 = getBuilder().newMonomer();
		oMono3.setMonomerName(new String("GLYA16"));
		IPDBAtom oPDBAtom1 = getBuilder().newPDBAtom("C1");
		IPDBAtom oPDBAtom2 = getBuilder().newPDBAtom("C2");
		IPDBAtom oPDBAtom3 = getBuilder().newPDBAtom("C3");
		IPDBAtom oPDBAtom4 = getBuilder().newPDBAtom("C4");
		IPDBAtom oPDBAtom5 = getBuilder().newPDBAtom("C5");
		
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
		IPDBPolymer pdbPolymer = getBuilder().newPDBPolymer();
		Assert.assertEquals(0, pdbPolymer.getMonomerCount());
		
		IStrand oStrand1 = getBuilder().newStrand();
		oStrand1.setStrandName("A");
		IStrand oStrand2 = getBuilder().newStrand();
		oStrand2.setStrandName("B");
		IMonomer oMono1 = getBuilder().newMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		IMonomer oMono2 = getBuilder().newMonomer();
		oMono2.setMonomerName(new String("HOH"));
		IPDBAtom oPDBAtom1 = getBuilder().newPDBAtom("C1");
		IPDBAtom oPDBAtom2 = getBuilder().newPDBAtom("C2");
		IPDBAtom oPDBAtom3 = getBuilder().newPDBAtom("C3");
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
		IPDBPolymer pdbPolymer = getBuilder().newPDBPolymer();
		Assert.assertEquals(0, pdbPolymer.getMonomerNames().size());
		
		IStrand oStrand1 = getBuilder().newStrand();
		oStrand1.setStrandName("A");
		IStrand oStrand2 = getBuilder().newStrand();
		oStrand2.setStrandName("B");
		IMonomer oMono1 = getBuilder().newMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		IMonomer oMono2 = getBuilder().newMonomer();
		oMono2.setMonomerName(new String("HOH"));
		IPDBAtom oPDBAtom1 = getBuilder().newPDBAtom("C1");
		IPDBAtom oPDBAtom2 = getBuilder().newPDBAtom("C2");
		IPDBAtom oPDBAtom3 = getBuilder().newPDBAtom("C3");
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
		IPDBPolymer pdbPolymer = getBuilder().newPDBPolymer();
		
		IStrand oStrand1 = getBuilder().newStrand();
		oStrand1.setStrandName("A");
		IStrand oStrand2 = getBuilder().newStrand();
		oStrand2.setStrandName("B");
		IMonomer oMono1 = getBuilder().newMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		IMonomer oMono2 = getBuilder().newMonomer();
		oMono2.setMonomerName(new String("HOH"));
		IPDBAtom oPDBAtom1 = getBuilder().newPDBAtom("C1");
		IPDBAtom oPDBAtom2 = getBuilder().newPDBAtom("C2");
		IPDBAtom oPDBAtom3 = getBuilder().newPDBAtom("C3");
		pdbPolymer.addAtom(oPDBAtom1, oMono1, oStrand1);
		pdbPolymer.addAtom(oPDBAtom2, oMono1, oStrand1);
		pdbPolymer.addAtom(oPDBAtom3, oMono2, oStrand2);

		Assert.assertEquals(oMono1, pdbPolymer.getMonomer("TRP279", "A"));
		Assert.assertEquals(oMono2, pdbPolymer.getMonomer("HOH", "B"));
	}
    
	@Test public void testAddAtom_IPDBAtom() {
		IPDBPolymer pdbPolymer = getBuilder().newPDBPolymer();
		
		IPDBAtom oPDBAtom1 = getBuilder().newPDBAtom("C1");
		IPDBAtom oPDBAtom2 = getBuilder().newPDBAtom("C2");
		pdbPolymer.addAtom(oPDBAtom1);
		pdbPolymer.addAtom(oPDBAtom2);

		Assert.assertEquals(2, pdbPolymer.getAtomCount());
	}
    
	@Test public void testAddAtom_IPDBAtom_IStrand() {
		IPDBPolymer pdbPolymer = getBuilder().newPDBPolymer();
		IStrand oStrand1 = getBuilder().newStrand();
		oStrand1.setStrandName("A");
		IPDBMonomer oMono1 = getBuilder().newPDBMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		IPDBAtom oPDBAtom1 = getBuilder().newPDBAtom("C1");
		IPDBAtom oPDBAtom2 = getBuilder().newPDBAtom("C2");
		IPDBAtom oPDBAtom3 = getBuilder().newPDBAtom("C3");
		pdbPolymer.addAtom(oPDBAtom1, oStrand1);
		pdbPolymer.addAtom(oPDBAtom2, oStrand1);
		pdbPolymer.addAtom(oPDBAtom3, oMono1, oStrand1);

		Assert.assertEquals(2, pdbPolymer.getMonomer("", "A").getAtomCount());
		Assert.assertEquals(1, pdbPolymer.getMonomer("TRP279", "A").getAtomCount());
		Assert.assertEquals(3, pdbPolymer.getAtomCount());
	}
	
	@Test public void testAddAtom_IPDBAtom_IMonomer_IStrand()	{
		IPDBPolymer pdbPolymer = getBuilder().newPDBPolymer();
		IStrand oStrand1 = getBuilder().newStrand();
		oStrand1.setStrandName("A");
		IPDBMonomer oMono1 = getBuilder().newPDBMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		IPDBAtom oPDBAtom1 = getBuilder().newPDBAtom("C1");
		IPDBAtom oPDBAtom2 = getBuilder().newPDBAtom("C2");
		IPDBAtom oPDBAtom3 = getBuilder().newPDBAtom("C3");
		pdbPolymer.addAtom(oPDBAtom1, oStrand1);
		pdbPolymer.addAtom(oPDBAtom2, oStrand1);
		pdbPolymer.addAtom(oPDBAtom3, oMono1, oStrand1);

		Assert.assertEquals(2, pdbPolymer.getMonomer("", "A").getAtomCount());
		Assert.assertEquals(1, pdbPolymer.getMonomer("TRP279", "A").getAtomCount());
		Assert.assertEquals(3, pdbPolymer.getAtomCount());
	}

	
	@Test public void testAddAtom_IPDBAtom_IMonomer() {
		IPDBPolymer pdbPolymer = getBuilder().newPDBPolymer();
		IPDBMonomer oMono1 = getBuilder().newPDBMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		IStrand oStrand1 = getBuilder().newStrand();
		oStrand1.setStrandName("A");
		IPDBAtom oPDBAtom1 = getBuilder().newPDBAtom("C1");
		pdbPolymer.addAtom(oPDBAtom1, oMono1, oStrand1);

		Assert.assertEquals(1, pdbPolymer.getMonomer("TRP279", "A").getAtomCount());
	}
	
	@Test public void testGetStrandCount()	{
		IPDBPolymer pdbPolymer = getBuilder().newPDBPolymer();
		IStrand oStrand1 = getBuilder().newStrand();
		oStrand1.setStrandName("A");
		IMonomer oMono1 = getBuilder().newMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		IPDBAtom oPDBAtom1 = getBuilder().newPDBAtom("C1");
		pdbPolymer.addAtom(oPDBAtom1, oMono1, oStrand1);

		Assert.assertEquals(1, pdbPolymer.getStrandCount());
	}
	
	@Test public void testGetStrand_String()	{
		IPDBPolymer pdbPolymer = getBuilder().newPDBPolymer();
		IStrand oStrand1 = getBuilder().newStrand();
		oStrand1.setStrandName("A");
		IMonomer oMono1 = getBuilder().newMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		IPDBAtom oPDBAtom1 = getBuilder().newPDBAtom("C1");
		pdbPolymer.addAtom(oPDBAtom1, oMono1, oStrand1);
		
		Assert.assertEquals(oStrand1, pdbPolymer.getStrand("A"));
	}
	
	@Test public void testGetStrandNames()	{
		IPDBPolymer pdbPolymer = getBuilder().newPDBPolymer();
		IStrand oStrand1 = getBuilder().newStrand();
		IStrand oStrand2 = getBuilder().newStrand();
		oStrand1.setStrandName("A");
		oStrand2.setStrandName("B");
		IMonomer oMono1 = getBuilder().newMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		IMonomer oMono2 = getBuilder().newMonomer();
		oMono2.setMonomerName(new String("GLY123"));
		IPDBAtom oPDBAtom1 = getBuilder().newPDBAtom("C1");
		IPDBAtom oPDBAtom2 = getBuilder().newPDBAtom("C2");
		pdbPolymer.addAtom(oPDBAtom1, oMono1, oStrand1);
		pdbPolymer.addAtom(oPDBAtom2, oMono2, oStrand2);
		Map<String,IStrand> strands = new Hashtable<String,IStrand>();
		strands.put("A", oStrand1);
		strands.put("B", oStrand2);
		
		Assert.assertEquals(strands.keySet(), pdbPolymer.getStrandNames());
	}
	
	@Test public void testRemoveStrand_String()	{
		IPDBPolymer pdbPolymer = getBuilder().newPDBPolymer();
		IStrand oStrand1 = getBuilder().newStrand();
		oStrand1.setStrandName("A");
		IMonomer oMono1 = getBuilder().newMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		IPDBAtom oPDBAtom1 = getBuilder().newPDBAtom("C1");
		pdbPolymer.addAtom(oPDBAtom1, oMono1, oStrand1);
		
		Assert.assertTrue(pdbPolymer.getStrandNames().contains(oStrand1.getStrandName()));
		Assert.assertEquals(1, pdbPolymer.getAtomCount());
		pdbPolymer.removeStrand("A");
		Assert.assertFalse(pdbPolymer.getStrandNames().contains(oStrand1.getStrandName()));
		Assert.assertEquals(0, pdbPolymer.getAtomCount());
	}
	
	@Test public void testGetStrands()	{
		IPDBPolymer pdbPolymer = getBuilder().newPDBPolymer();
		IStrand oStrand1 = getBuilder().newStrand();
		IStrand oStrand2 = getBuilder().newStrand();
		oStrand1.setStrandName("A");
		oStrand2.setStrandName("B");
		IMonomer oMono1 = getBuilder().newMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		IMonomer oMono2 = getBuilder().newMonomer();
		oMono2.setMonomerName(new String("GLY123"));
		IPDBAtom oPDBAtom1 = getBuilder().newPDBAtom("C1");
		IPDBAtom oPDBAtom2 = getBuilder().newPDBAtom("C2");
		pdbPolymer.addAtom(oPDBAtom1, oMono1, oStrand1);
		pdbPolymer.addAtom(oPDBAtom2, oMono2, oStrand2);
		Map<String,IStrand> strands = new Hashtable<String,IStrand>();
		strands.put("A", oStrand1);
		strands.put("B", oStrand2);
		
		Assert.assertEquals(strands, pdbPolymer.getStrands());
	}
    
    /**
     * Method to test whether the class complies with RFC #9.
     */
    @Test public void testToString() {
    	IPDBPolymer pdbPolymer = getBuilder().newPDBPolymer();
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
    	IPDBPolymer polymer = getBuilder().newPDBPolymer();
        Object clone = polymer.clone();
        Assert.assertTrue(clone instanceof IBioPolymer);
    }

}
