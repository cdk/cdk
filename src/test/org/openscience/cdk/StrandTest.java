/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk;

import java.util.Hashtable;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
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
 * @cdk.module test-data
 */
public class StrandTest extends CDKTestCase {
	
	protected static IChemObjectBuilder builder;
	
    @BeforeClass public static void setUp() {
       	builder = DefaultChemObjectBuilder.getInstance();
    }

	@Test public void testStrand() {
		IStrand oStrand = builder.newStrand();
		Assert.assertNotNull(oStrand);
		Assert.assertEquals(oStrand.getMonomerCount(), 0);

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
		Assert.assertNotNull(oStrand.getAtom(0));
		Assert.assertNotNull(oStrand.getAtom(1));
		Assert.assertNotNull(oStrand.getAtom(2));
		Assert.assertNotNull(oStrand.getAtom(3));
		Assert.assertNotNull(oStrand.getAtom(4));
		Assert.assertEquals(oAtom1, oStrand.getAtom(0));
		Assert.assertEquals(oAtom2, oStrand.getAtom(1));
		Assert.assertEquals(oAtom3, oStrand.getAtom(2));
		Assert.assertEquals(oAtom4, oStrand.getAtom(3));
		Assert.assertEquals(oAtom5, oStrand.getAtom(4));

		Assert.assertNull(oStrand.getMonomer("0815"));
		Assert.assertNotNull(oStrand.getMonomer(""));
		Assert.assertNotNull(oStrand.getMonomer("TRP279"));
		Assert.assertEquals(oMono1, oStrand.getMonomer("TRP279"));
		Assert.assertEquals(oStrand.getMonomer("TRP279").getAtomCount(), 1);
		Assert.assertNotNull(oStrand.getMonomer("HOH"));
		Assert.assertEquals(oMono2, oStrand.getMonomer("HOH"));
		Assert.assertEquals(oStrand.getMonomer("HOH").getAtomCount(), 1);
		Assert.assertEquals(oStrand.getMonomer("").getAtomCount(), 2);
		Assert.assertEquals(oStrand.getAtomCount(), 5);
		Assert.assertEquals(oStrand.getMonomerCount(), 3);
	}
	
	@Test public void testGetStrandName()	{
		IStrand oStrand = builder.newStrand();
		oStrand.setStrandName("A");
		
		Assert.assertEquals("A", oStrand.getStrandName());
	}
	
	@Test public void testGetStrandType()	{
		IStrand oStrand = builder.newStrand();
		oStrand.setStrandType("DNA");
		
		Assert.assertEquals("DNA", oStrand.getStrandType());
	}
	
	/** The methods above effectively test SetStrandName and
	 * SetStrandType as well, but I include SetStrandName and
	 * SetStrandType explicitly as well (for concinstency).  
	 */
	
	@Test public void testSetStrandName_String()	{
		IStrand oStrand = builder.newStrand();
		oStrand.setStrandName("A");
		
		Assert.assertEquals("A", oStrand.getStrandName());
	}
	
	@Test public void testSetStrandType_String()	{
		IStrand oStrand = builder.newStrand();
		oStrand.setStrandType("DNA");
		
		Assert.assertEquals("DNA", oStrand.getStrandType());
	}
	
	@Test public void testAddAtom_IAtom() {
		IStrand oStrand = builder.newStrand();
		IAtom oAtom1 = builder.newAtom("C1");
		IAtom oAtom2 = builder.newAtom("C2");
		oStrand.addAtom(oAtom1);
		oStrand.addAtom(oAtom2);

		Assert.assertEquals(2, oStrand.getAtomCount());
	}
    
	@Test public void testAddAtom_IAtom_IMonomer() {
		IStrand oStrand = builder.newStrand();
		IMonomer oMono1 = builder.newMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		IAtom oAtom1 = builder.newAtom("C1");
		IAtom oAtom2 = builder.newAtom("C2");
		IAtom oAtom3 = builder.newAtom("C3");
		oStrand.addAtom(oAtom1);
		oStrand.addAtom(oAtom2);
		oStrand.addAtom(oAtom3, oMono1);

		Assert.assertEquals(2, oStrand.getMonomer("").getAtomCount());
		Assert.assertEquals(1, oStrand.getMonomer("TRP279").getAtomCount());
	}
	
	@Test public void testGetMonomerCount() {
		IStrand oStrand = builder.newStrand();
		IMonomer oMono1 = builder.newMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		IMonomer oMono2 = builder.newMonomer();
		oMono2.setMonomerName(new String("HOH"));
		IAtom oAtom2 = builder.newAtom("C2");
		IAtom oAtom3 = builder.newAtom("C3");
		oStrand.addAtom(oAtom2, oMono1);
		oStrand.addAtom(oAtom3, oMono2);

		Assert.assertEquals(2, oStrand.getMonomerCount());
	}
	 
	@Test public void testGetMonomer_String() {
		IStrand oStrand = builder.newStrand();
		IMonomer oMono1 = builder.newMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		IMonomer oMono2 = builder.newMonomer();
		oMono2.setMonomerName(new String("HOH"));
		IAtom oAtom2 = builder.newAtom("C2");
		IAtom oAtom3 = builder.newAtom("C3");
		oStrand.addAtom(oAtom2, oMono1);
		oStrand.addAtom(oAtom3, oMono2);

		Assert.assertEquals(oMono1, oStrand.getMonomer("TRP279"));
		Assert.assertEquals(oMono2, oStrand.getMonomer("HOH"));
		Assert.assertNull(oStrand.getMonomer("TEST"));
	}
	
	@Test public void testGetMonomerNames() {
		IStrand oStrand = builder.newStrand();
		IMonomer oMono1 = builder.newMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		IMonomer oMono2 = builder.newMonomer();
		oMono2.setMonomerName(new String("HOH"));
		IAtom oAtom2 = builder.newAtom("C2");
		IAtom oAtom3 = builder.newAtom("C3");
		oStrand.addAtom(oAtom2, oMono1);
		oStrand.addAtom(oAtom3, oMono2);
		Map monomers = new Hashtable();
		IMonomer oMon = builder.newMonomer();
		oMon.setMonomerName("");
		oMon.setMonomerType("UNKNOWN");
		monomers.put("", oMon);
		monomers.put("TRP279", oMono1);
		monomers.put("HOH", oMono2);
		
		Assert.assertEquals(monomers.keySet(), oStrand.getMonomerNames());
/*
		Assert.assertEquals(3, oStrand.getMonomerNames().size());
		Assert.assertTrue(oStrand.getMonomerNames().contains(oMono1.getMonomerName()));
		Assert.assertTrue(oStrand.getMonomerNames().contains(oMono2.getMonomerName()));
*/		
	}
	
	@Test public void testRemoveMonomer_String()	{
		IStrand oStrand = builder.newStrand();
		IMonomer oMono1 = builder.newMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		IAtom oAtom1 = builder.newAtom("C1");
		oStrand.addAtom(oAtom1, oMono1);		
		Assert.assertTrue(oStrand.getMonomerNames().contains(oMono1.getMonomerName()));
		Assert.assertEquals(1, oStrand.getAtomCount());
		oStrand.removeMonomer("TRP279");
		Assert.assertFalse(oStrand.getMonomerNames().contains(oMono1.getMonomerName()));
		Assert.assertEquals(0, oStrand.getAtomCount());
	}
	
	@Test public void testGetMonomers()	{
		IStrand oStrand = builder.newStrand();
		IMonomer oMono1 = builder.newMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		IMonomer oMono2 = builder.newMonomer();
		oMono2.setMonomerName(new String("HOH"));
		IAtom oAtom2 = builder.newAtom("C2");
		IAtom oAtom3 = builder.newAtom("C3");
		oStrand.addAtom(oAtom2, oMono1);
		oStrand.addAtom(oAtom3, oMono2);
		Map monomers = new Hashtable();
		IMonomer oMon = builder.newMonomer();
		oMon.setMonomerName("");
		oMon.setMonomerType("UNKNOWN");
		monomers.put("", oMon);
		monomers.put("TRP279", oMono1);
		monomers.put("HOH", oMono2);
		
		Assert.assertEquals(monomers.keySet(), oStrand.getMonomerNames());
	}

    /**
     * Method to test whether the class complies with RFC #9.
     */
    @Test public void testToString() {
        IStrand oStrand = builder.newStrand();
		IMonomer oMono1 = builder.newMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		IMonomer oMono2 = builder.newMonomer();
		oMono2.setMonomerName(new String("HOH"));
		IAtom oAtom2 = builder.newAtom("C2");
		IAtom oAtom3 = builder.newAtom("C3");
		oStrand.addAtom(oAtom2, oMono1);
		oStrand.addAtom(oAtom3, oMono2);
		Map monomers = new Hashtable();
		IMonomer oMon = builder.newMonomer();
		oMon.setMonomerName("");
		oMon.setMonomerType("UNKNOWN");
		monomers.put("", oMon);
		monomers.put("TRP279", oMono1);
		monomers.put("HOH", oMono2);
        String description = oStrand.toString();
        for (int i=0; i< description.length(); i++) {
            Assert.assertTrue('\n' != description.charAt(i));
            Assert.assertTrue('\r' != description.charAt(i));
        }
    }
    
    /**
     * Method to test the clone() method
     */
    @Test public void testClone() throws Exception {
    	IStrand strand = builder.newStrand();
        Object clone = strand.clone();
        Assert.assertTrue(clone instanceof IStrand);
    }
    
}
