/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2002-2005  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 * 
 */

package org.openscience.cdk.test;

import javax.vecmath.Vector3d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.Crystal;

/**
 * Checks the funcitonality of the Crystal.
 *
 * @cdk.module test
 */
public class CrystalTest extends CDKTestCase {

    public CrystalTest(String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        return new TestSuite(CrystalTest.class);
    }

    public void testCrystal() {
        Crystal crystal = new Crystal();
        assertNotNull(crystal);
        assertEquals(0, crystal.getAtomCount());
        assertEquals(0, crystal.getBondCount());
    }
    
    public void testCrystal_AtomContainer() {
        AtomContainer acetone = new AtomContainer();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        Crystal crystal = new Crystal(acetone);
        assertNotNull(crystal);
        assertEquals(4, crystal.getAtomCount());
        assertEquals(3, crystal.getBondCount());
    }
    
    public void testAdd_AtomContainer() {
        AtomContainer acetone = new AtomContainer();
        Atom c1 = new Atom("C");
        Atom c2 = new Atom("C");
        Atom o = new Atom("O");
        Atom c3 = new Atom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = new Bond(c1, c2,1);
        Bond b2 = new Bond(c1, o, 2);
        Bond b3 = new Bond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        Crystal crystal = new Crystal();
        crystal.add(acetone);
        assertEquals(4, crystal.getAtomCount());
        assertEquals(3, crystal.getBondCount());
    }
    
    public void testAddAtom_Atom() {
        Atom c1 = new Atom("C");
        Crystal crystal = new Crystal();
        crystal.addAtom(c1);
        assertEquals(1, crystal.getAtomCount());
    }

    public void testSetA_Vector3d() {
        Crystal crystal = new Crystal();
        
        crystal.setA(new Vector3d(1.0, 2.0, 3.0));
        Vector3d a = crystal.getA();
        assertEquals(1.0, a.x, 0.001);
        assertEquals(2.0, a.y, 0.001);
        assertEquals(3.0, a.z, 0.001);
    }
    
    public void testGetA() {
        Crystal crystal = new Crystal();
        
        crystal.setA(new Vector3d(1.0, 2.0, 3.0));
        Vector3d a = crystal.getA();
        assertNotNull(a);
    }
    
    public void testGetB() {
        Crystal crystal = new Crystal();
        
        crystal.setB(new Vector3d(1.0, 2.0, 3.0));
        Vector3d a = crystal.getB();
	assertNotNull(a);
    }
    
    public void testGetC() {
        Crystal crystal = new Crystal();
        
        crystal.setC(new Vector3d(1.0, 2.0, 3.0));
        Vector3d a = crystal.getC();
	assertNotNull(a);
    }
    
    public void testSetB_Vector3d() {
        Crystal crystal = new Crystal();
        
        crystal.setB(new Vector3d(1.0, 2.0, 3.0));
        Vector3d b = crystal.getB();
        assertEquals(1.0, b.x, 0.001);
        assertEquals(2.0, b.y, 0.001);
        assertEquals(3.0, b.z, 0.001);
    }
    
    public void testSetC_Vector3d() {
        Crystal crystal = new Crystal();
        
        crystal.setC(new Vector3d(1.0, 2.0, 3.0));
        Vector3d c = crystal.getC();
        assertEquals(1.0, c.x, 0.001);
        assertEquals(2.0, c.y, 0.001);
        assertEquals(3.0, c.z, 0.001);
    }
    
    public void testSetSpaceGroup_String() {
        Crystal crystal = new Crystal();
        String spacegroup = "P 2_1 2_1 2_1";
        crystal.setSpaceGroup(spacegroup);
        assertEquals(spacegroup, crystal.getSpaceGroup());
    }

    public void testGetSpaceGroup() {
        Crystal crystal = new Crystal();
        String spacegroup = "P 2_1 2_1 2_1";
        crystal.setSpaceGroup(spacegroup);
        assertNotNull(crystal.getSpaceGroup());
        assertEquals(spacegroup, crystal.getSpaceGroup());
    }

    public void testSetZ_int() {
        Crystal crystal = new Crystal();
        int z = 2;
        crystal.setZ(z);
        assertEquals(z, crystal.getZ());
    }
    public void testGetZ() {
        testSetZ_int();
    }

    /**
     * Method to test wether the class complies with RFC #9.
     */
    public void testToString() {
        Crystal crystal = new Crystal();
        String description = crystal.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }

    public void testClone() {
        Crystal crystal = new Crystal();
        Object clone = crystal.clone();
        assertTrue(clone instanceof Crystal);
    }

    public void testClone_Axes() {
        Crystal crystal1 = new Crystal();
        Vector3d axes = new Vector3d(1.0, 2.0, 3.0);
        crystal1.setA(axes);
        Crystal crystal2 = (Crystal)crystal1.clone();

        // test cloning of axes
        crystal1.getA().x = 5.0;
        assertEquals(1.0, crystal2.getA().x, 0.001);
    }
    
    public void testSetZeroAxes() {
        Crystal crystal = new Crystal();
        
        crystal.setA(new Vector3d(1.0, 2.0, 3.0));
        Vector3d a = crystal.getA();
        assertNotNull(a);
    }
    
    
}
