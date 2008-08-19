/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2001-2007  The Chemistry Development Kit (CDK) project
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
 *  */
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
import org.openscience.cdk.interfaces.IPolymer;
import org.openscience.cdk.interfaces.IStrand;

/**
 * TestCase for the Polymer class.
 *
 * @author      Edgar Luttmann <edgar@uni-paderborn.de>
 * @author      Martin Eklund <martin.eklund@farmbio.uu.se>
 * @cdk.created 2001-08-09
 * @cdk.module  test-data
 */
public class PolymerTest extends NewCDKTestCase {

	protected static IChemObjectBuilder builder;
	
    @BeforeClass public static void setUp() {
       	builder = DefaultChemObjectBuilder.getInstance();
    }

	@Test public void testPolymer() {
		IPolymer oPolymer = builder.newPolymer();
		Assert.assertNotNull(oPolymer);
		Assert.assertEquals(oPolymer.getMonomerCount(), 0);
	}
	
	@Test public void testAddAtom_IAtom() {
		IPolymer oPolymer = builder.newPolymer();
		
		IAtom oAtom1 = builder.newAtom("C1");
		IAtom oAtom2 = builder.newAtom("C2");
		oPolymer.addAtom(oAtom1);
		oPolymer.addAtom(oAtom2);

		Assert.assertEquals(2, oPolymer.getAtomCount());
		Assert.assertEquals(0, oPolymer.getMonomerCount());
	}
    
	@Test public void testAddAtom_IAtom_IMonomer() {
		IPolymer oPolymer = builder.newPolymer();
		IMonomer oMono1 = builder.newMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		IMonomer oMono2 = null;
		IAtom oAtom1 = builder.newAtom("C1");
		IAtom oAtom2 = builder.newAtom("C2");
		IAtom oAtom3 = builder.newAtom("C3");
		
		oPolymer.addAtom(oAtom1);
		oPolymer.addAtom(oAtom2, oMono1);
		oPolymer.addAtom(oAtom3, oMono2);
		Assert.assertNotNull(oPolymer.getAtom(0));
		Assert.assertNotNull(oPolymer.getAtom(1));
		Assert.assertNotNull(oPolymer.getAtom(2));
		Assert.assertEquals(oAtom1, oPolymer.getAtom(0));
		Assert.assertEquals(oAtom2, oPolymer.getAtom(1));
		Assert.assertEquals(oAtom3, oPolymer.getAtom(2));
		Assert.assertEquals(3, oPolymer.getAtomCount());
		Assert.assertEquals(1, oPolymer.getMonomer("TRP279").getAtomCount());
		Assert.assertEquals(1, oPolymer.getMonomerCount());

		Assert.assertNotNull(oPolymer.getMonomer("TRP279"));
		Assert.assertEquals(oMono1, oPolymer.getMonomer("TRP279"));
	}
	
	@Test public void testGetMonomerCount() {
		IPolymer oPolymer = builder.newPolymer();
		Assert.assertEquals(0, oPolymer.getMonomerCount());
		
		IMonomer oMono1 = builder.newMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		IMonomer oMono2 = builder.newMonomer();
		oMono2.setMonomerName(new String("HOH"));
		IAtom oAtom1 = builder.newAtom("C1");
		IAtom oAtom2 = builder.newAtom("C2");
		IAtom oAtom3 = builder.newAtom("C3");
		oPolymer.addAtom(oAtom1);
		oPolymer.addAtom(oAtom2, oMono1);
		oPolymer.addAtom(oAtom3, oMono2);
		
		Assert.assertEquals(3, oPolymer.getAtomCount());	
		Assert.assertEquals(2, oPolymer.getMonomerCount());
	}
	
	@Test public void testGetMonomer_String() {
		IPolymer oPolymer = builder.newPolymer();
		
		IMonomer oMono1 = builder.newMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		IMonomer oMono2 = builder.newMonomer();
		oMono2.setMonomerName(new String("HOH"));
		IAtom oAtom1 = builder.newAtom("C1");
		IAtom oAtom2 = builder.newAtom("C2");
		IAtom oAtom3 = builder.newAtom("C3");
		oPolymer.addAtom(oAtom1, oMono1);
		oPolymer.addAtom(oAtom2, oMono1);
		oPolymer.addAtom(oAtom3, oMono2);

		Assert.assertEquals(oMono1, oPolymer.getMonomer("TRP279"));
		Assert.assertEquals(oMono2, oPolymer.getMonomer("HOH"));
		Assert.assertNull(oPolymer.getMonomer("Mek"));
	}
	
	@Test public void testGetMonomerNames() {
		IPolymer oPolymer = builder.newPolymer();
		Assert.assertEquals(0, oPolymer.getMonomerNames().size());
		
		IMonomer oMono1 = builder.newMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		IMonomer oMono2 = builder.newMonomer();
		oMono2.setMonomerName(new String("HOH"));
		IAtom oAtom1 = builder.newAtom("C1");
		IAtom oAtom2 = builder.newAtom("C2");
		IAtom oAtom3 = builder.newAtom("C3");
		oPolymer.addAtom(oAtom1);
		oPolymer.addAtom(oAtom2, oMono1);
		oPolymer.addAtom(oAtom3, oMono2);
		Map monomers = new Hashtable();
		//IMonomer oMon = builder.newMonomer();
		monomers.put("TRP279", oMono1);
		monomers.put("HOH", oMono2);

		Assert.assertEquals(2, oPolymer.getMonomerNames().size());
		Assert.assertTrue(oPolymer.getMonomerNames().contains(oMono1.getMonomerName()));
		Assert.assertTrue(oPolymer.getMonomerNames().contains(oMono2.getMonomerName()));
		Assert.assertEquals(monomers.keySet(), oPolymer.getMonomerNames());
	}
	
	@Test public void testRemoveMonomer_String()	{
		IPolymer oPolymer = builder.newPolymer();
		IMonomer oMono1 = builder.newMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		IAtom oAtom1 = builder.newAtom("C1");
		oPolymer.addAtom(oAtom1, oMono1);
		Assert.assertTrue(oPolymer.getMonomerNames().contains(oMono1.getMonomerName()));
		Assert.assertEquals(1, oPolymer.getAtomCount());
		
		oPolymer.removeMonomer("TRP279");
		Assert.assertFalse(oPolymer.getMonomerNames().contains(oMono1.getMonomerName()));
		Assert.assertEquals(0, oPolymer.getAtomCount());
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
    	IPolymer polymer = builder.newPolymer();
        Object clone = polymer.clone();
        Assert.assertTrue(clone instanceof IPolymer);
    }

}
