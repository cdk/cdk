/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2001-2005  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test;

import java.util.Hashtable;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IMonomer;
import org.openscience.cdk.interfaces.IPolymer;
import org.openscience.cdk.interfaces.IStrand;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * TestCase for the Polymer class.
 *
 * @author      Edgar Luttmann <edgar@uni-paderborn.de>
 * @author      Martin Eklund <martin.eklund@farmbio.uu.se>
 * @cdk.created 2001-08-09
 * @cdk.module  test
 */
public class PolymerTest extends TestCase {

	protected IChemObjectBuilder builder;
	
	public PolymerTest(String name) {
		super(name);
	}

    public void setUp() {
       	builder = DefaultChemObjectBuilder.getInstance();
    }

    public static Test suite() {
		return new TestSuite(PolymerTest.class);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(new TestSuite(PolymerTest.class));
	}

	public void testPolymer() {
		IPolymer oPolymer = builder.newPolymer();
		assertNotNull(oPolymer);
		assertEquals(oPolymer.getMonomerCount(), 0);
	}
	
	public void testAddAtom_IAtom() {
		IPolymer oPolymer = builder.newPolymer();
		
		IAtom oAtom1 = builder.newAtom("C1");
		IAtom oAtom2 = builder.newAtom("C2");
		oPolymer.addAtom(oAtom1);
		oPolymer.addAtom(oAtom2);

		assertEquals(2, oPolymer.getAtomCount());
		assertEquals(0, oPolymer.getMonomerCount());
	}
    
	public void testAddAtom_IAtom_IMonomer() {
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
		assertNotNull(oPolymer.getAtomAt(0));
		assertNotNull(oPolymer.getAtomAt(1));
		assertNotNull(oPolymer.getAtomAt(2));
		assertEquals(oAtom1, oPolymer.getAtomAt(0));
		assertEquals(oAtom2, oPolymer.getAtomAt(1));
		assertEquals(oAtom3, oPolymer.getAtomAt(2));
		assertEquals(3, oPolymer.getAtomCount());
		assertEquals(1, oPolymer.getMonomer("TRP279").getAtomCount());
		assertEquals(1, oPolymer.getMonomerCount());

		assertNotNull(oPolymer.getMonomer("TRP279"));
		assertEquals(oMono1, oPolymer.getMonomer("TRP279"));
	}
	
	public void testGetMonomerCount() {
		IPolymer oPolymer = builder.newPolymer();
		assertEquals(0, oPolymer.getMonomerCount());
		
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
		
		assertEquals(3, oPolymer.getAtomCount());	
		assertEquals(2, oPolymer.getMonomerCount());
	}
	
	public void testGetMonomer_String() {
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

		assertEquals(oMono1, oPolymer.getMonomer("TRP279"));
		assertEquals(oMono2, oPolymer.getMonomer("HOH"));
		assertNull(oPolymer.getMonomer("Mek"));
	}
	
	public void testGetMonomerNames() {
		IPolymer oPolymer = builder.newPolymer();
		assertEquals(0, oPolymer.getMonomerNames().size());
		
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
		Hashtable monomers = new Hashtable();
		IMonomer oMon = builder.newMonomer();
		monomers.put("TRP279", oMono1);
		monomers.put("HOH", oMono2);

		assertEquals(2, oPolymer.getMonomerNames().size());
		assertTrue(oPolymer.getMonomerNames().contains(oMono1.getMonomerName()));
		assertTrue(oPolymer.getMonomerNames().contains(oMono2.getMonomerName()));
		assertEquals(monomers.keySet(), oPolymer.getMonomerNames());
	}
	
	public void testRemoveMonomer_String()	{
		IPolymer oPolymer = builder.newPolymer();
		IMonomer oMono1 = builder.newMonomer();
		oMono1.setMonomerName(new String("TRP279"));
		IAtom oAtom1 = builder.newAtom("C1");
		oPolymer.addAtom(oAtom1, oMono1);
		assertTrue(oPolymer.getMonomerNames().contains(oMono1.getMonomerName()));
		assertEquals(1, oPolymer.getAtomCount());
		
		oPolymer.removeMonomer("TRP279");
		assertFalse(oPolymer.getMonomerNames().contains(oMono1.getMonomerName()));
		assertEquals(0, oPolymer.getAtomCount());
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
		monomers.put("TRP279", oMono1);
		monomers.put("HOH", oMono2);
        String description = oStrand.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue('\n' != description.charAt(i));
            assertTrue('\r' != description.charAt(i));
        }
    }
}
