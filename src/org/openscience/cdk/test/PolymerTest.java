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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  */
package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Monomer;
import org.openscience.cdk.Polymer;

/**
 * TestCase for the Polymer class.
 *
 * @author      Edgar Luttmann <edgar@uni-paderborn.de>
 * @cdk.created 2001-08-09
 * @cdk.module  test
 */
public class PolymerTest extends TestCase {

	public PolymerTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(PolymerTest.class);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(new TestSuite(PolymerTest.class));
	}

	public void testPolymer() {
		Polymer oPolymer = new Polymer();
		assertNotNull(oPolymer);
		assertEquals(oPolymer.getMonomerCount(), 0);
		
		Monomer oMono1 = new Monomer();
		oMono1.setMonomerName(new String("TRP279"));
		Monomer oMono2 = new Monomer();
		oMono2.setMonomerName(new String("HOH"));
		Atom oAtom1 = new Atom("C1");
		Atom oAtom2 = new Atom("C2");
		Atom oAtom3 = new Atom("C3");
		oPolymer.addAtom(oAtom1);
		oPolymer.addAtom(oAtom2, oMono1);
		oPolymer.addAtom(oAtom3, oMono2);
		assertNotNull(oPolymer.getAtomAt(0));
		assertNotNull(oPolymer.getAtomAt(1));
		assertNotNull(oPolymer.getAtomAt(2));
		assertEquals(oAtom1, oPolymer.getAtomAt(0));
		assertEquals(oAtom2, oPolymer.getAtomAt(1));
		assertEquals(oAtom3, oPolymer.getAtomAt(2));

		assertNull(oPolymer.getMonomer("0815"));
		assertNotNull(oPolymer.getMonomer("TRP279"));
		assertEquals(oMono1, oPolymer.getMonomer("TRP279"));
		assertEquals(oPolymer.getMonomer("TRP279").getAtomCount(), 1);
		assertNotNull(oPolymer.getMonomer("HOH"));
		assertEquals(oMono2, oPolymer.getMonomer("HOH"));
		assertEquals(oPolymer.getMonomer("HOH").getAtomCount(), 1);
	}
	
	public void testGetMonomerCount() {
		Polymer oPolymer = new Polymer();
		assertEquals(0, oPolymer.getMonomerCount()); // there is a default monomer
		
		Monomer oMono1 = new Monomer();
		oMono1.setMonomerName(new String("TRP279"));
		Monomer oMono2 = new Monomer();
		oMono2.setMonomerName(new String("HOH"));
		Atom oAtom1 = new Atom("C1");
		Atom oAtom2 = new Atom("C2");
		Atom oAtom3 = new Atom("C3");
		oPolymer.addAtom(oAtom1);
		oPolymer.addAtom(oAtom2, oMono1);
		oPolymer.addAtom(oAtom3, oMono2);
		assertNotNull(oPolymer.getAtomAt(0));
		assertNotNull(oPolymer.getAtomAt(1));
		assertNotNull(oPolymer.getAtomAt(2));
		assertEquals(oAtom1, oPolymer.getAtomAt(0));
		assertEquals(oAtom2, oPolymer.getAtomAt(1));
		assertEquals(oAtom3, oPolymer.getAtomAt(2));

		assertEquals(2, oPolymer.getMonomerCount());
	}
	
	public void testGetMonomerNames() {
		Polymer oPolymer = new Polymer();
		assertEquals(1, oPolymer.getMonomerNames().size()); // there is a default monomer
		
		Monomer oMono1 = new Monomer();
		oMono1.setMonomerName(new String("TRP279"));
		Monomer oMono2 = new Monomer();
		oMono2.setMonomerName(new String("HOH"));
		Atom oAtom1 = new Atom("C1");
		Atom oAtom2 = new Atom("C2");
		Atom oAtom3 = new Atom("C3");
		oPolymer.addAtom(oAtom1);
		oPolymer.addAtom(oAtom2, oMono1);
		oPolymer.addAtom(oAtom3, oMono2);
		assertNotNull(oPolymer.getAtomAt(0));
		assertNotNull(oPolymer.getAtomAt(1));
		assertNotNull(oPolymer.getAtomAt(2));
		assertEquals(oAtom1, oPolymer.getAtomAt(0));
		assertEquals(oAtom2, oPolymer.getAtomAt(1));
		assertEquals(oAtom3, oPolymer.getAtomAt(2));

		assertEquals(3, oPolymer.getMonomerNames().size());
		assertTrue(oPolymer.getMonomerNames().contains(oMono1.getMonomerName()));
		assertTrue(oPolymer.getMonomerNames().contains(oMono2.getMonomerName()));
	}
	
	public void testGetMonomer_String() {
		Polymer oPolymer = new Polymer();
		
		Monomer oMono1 = new Monomer();
		oMono1.setMonomerName(new String("TRP279"));
		Monomer oMono2 = new Monomer();
		oMono2.setMonomerName(new String("HOH"));
		Atom oAtom1 = new Atom("C1");
		Atom oAtom2 = new Atom("C2");
		Atom oAtom3 = new Atom("C3");
		oPolymer.addAtom(oAtom1, oMono1);
		oPolymer.addAtom(oAtom2, oMono1);
		oPolymer.addAtom(oAtom3, oMono2);

		assertEquals(oMono1, oPolymer.getMonomer("TRP279"));
		assertEquals(oMono2, oPolymer.getMonomer("HOH"));
	}
    
	public void testAddAtom_Atom() {
		Polymer oPolymer = new Polymer();
		
		Atom oAtom1 = new Atom("C1");
		Atom oAtom2 = new Atom("C2");
		oPolymer.addAtom(oAtom1);
		oPolymer.addAtom(oAtom2);

		assertEquals(2, oPolymer.getMonomer("").getAtomCount());
	}
    
	public void testAddAtom_Atom_Monomer() {
		Polymer oPolymer = new Polymer();
		
		Monomer oMono1 = new Monomer();
		oMono1.setMonomerName(new String("TRP279"));
		Atom oAtom1 = new Atom("C1");
		Atom oAtom2 = new Atom("C2");
		Atom oAtom3 = new Atom("C3");
		oPolymer.addAtom(oAtom1);
		oPolymer.addAtom(oAtom2, oMono1);
		oPolymer.addAtom(oAtom3, oMono1);

		assertEquals(1, oPolymer.getMonomer("").getAtomCount());
		assertEquals(2, oPolymer.getMonomer("TRP279").getAtomCount());
	}
}
