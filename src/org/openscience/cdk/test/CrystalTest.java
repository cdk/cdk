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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 * 
 */
package org.openscience.cdk.test;

import javax.vecmath.Vector3d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.Bond;
import org.openscience.cdk.interfaces.Crystal;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.ChemObjectBuilder;

/**
 * Checks the functionality of the Crystal.
 *
 * @cdk.module test
 */
public class CrystalTest extends CDKTestCase {

	protected ChemObjectBuilder builder;
	
    public CrystalTest(String name) {
        super(name);
    }

    public void setUp() {
    	builder = DefaultChemObjectBuilder.getInstance();
    }

    public static Test suite() {
        return new TestSuite(CrystalTest.class);
    }

    public void testCrystal() {
        Crystal crystal = builder.newCrystal();
        assertNotNull(crystal);
        assertEquals(0, crystal.getAtomCount());
        assertEquals(0, crystal.getBondCount());
    }
    
    public void testCrystal_IAtomContainer() {
        IAtomContainer acetone = builder.newAtomContainer();
        IAtom c1 = builder.newAtom("C");
        IAtom c2 = builder.newAtom("C");
        IAtom o = builder.newAtom("O");
        IAtom c3 = builder.newAtom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = builder.newBond(c1, c2,1);
        Bond b2 = builder.newBond(c1, o, 2);
        Bond b3 = builder.newBond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        Crystal crystal = builder.newCrystal(acetone);
        assertNotNull(crystal);
        assertEquals(4, crystal.getAtomCount());
        assertEquals(3, crystal.getBondCount());
    }
    
    public void testAdd_IAtomContainer() {
        IAtomContainer acetone = builder.newAtomContainer();
        IAtom c1 = builder.newAtom("C");
        IAtom c2 = builder.newAtom("C");
        IAtom o = builder.newAtom("O");
        IAtom c3 = builder.newAtom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        Bond b1 = builder.newBond(c1, c2,1);
        Bond b2 = builder.newBond(c1, o, 2);
        Bond b3 = builder.newBond(c1, c3,1);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        Crystal crystal = builder.newCrystal();
        crystal.add(acetone);
        assertEquals(4, crystal.getAtomCount());
        assertEquals(3, crystal.getBondCount());
    }
    
    public void testAddAtom_IAtom() {
        IAtom c1 = builder.newAtom("C");
        Crystal crystal = builder.newCrystal();
        crystal.addAtom(c1);
        assertEquals(1, crystal.getAtomCount());
    }

    public void testSetA_Vector3d() {
        Crystal crystal = builder.newCrystal();
        
        crystal.setA(new Vector3d(1.0, 2.0, 3.0));
        Vector3d a = crystal.getA();
        assertEquals(1.0, a.x, 0.001);
        assertEquals(2.0, a.y, 0.001);
        assertEquals(3.0, a.z, 0.001);
    }
    
    public void testGetA() {
        Crystal crystal = builder.newCrystal();
        
        crystal.setA(new Vector3d(1.0, 2.0, 3.0));
        Vector3d a = crystal.getA();
        assertNotNull(a);
    }
    
    public void testGetB() {
        Crystal crystal = builder.newCrystal();
        
        crystal.setB(new Vector3d(1.0, 2.0, 3.0));
        Vector3d a = crystal.getB();
	assertNotNull(a);
    }
    
    public void testGetC() {
        Crystal crystal = builder.newCrystal();
        
        crystal.setC(new Vector3d(1.0, 2.0, 3.0));
        Vector3d a = crystal.getC();
	assertNotNull(a);
    }
    
    public void testSetB_Vector3d() {
        Crystal crystal = builder.newCrystal();
        
        crystal.setB(new Vector3d(1.0, 2.0, 3.0));
        Vector3d b = crystal.getB();
        assertEquals(1.0, b.x, 0.001);
        assertEquals(2.0, b.y, 0.001);
        assertEquals(3.0, b.z, 0.001);
    }
    
    public void testSetC_Vector3d() {
        Crystal crystal = builder.newCrystal();
        
        crystal.setC(new Vector3d(1.0, 2.0, 3.0));
        Vector3d c = crystal.getC();
        assertEquals(1.0, c.x, 0.001);
        assertEquals(2.0, c.y, 0.001);
        assertEquals(3.0, c.z, 0.001);
    }
    
    public void testSetSpaceGroup_String() {
        Crystal crystal = builder.newCrystal();
        String spacegroup = "P 2_1 2_1 2_1";
        crystal.setSpaceGroup(spacegroup);
        assertEquals(spacegroup, crystal.getSpaceGroup());
    }

    public void testGetSpaceGroup() {
        Crystal crystal = builder.newCrystal();
        String spacegroup = "P 2_1 2_1 2_1";
        crystal.setSpaceGroup(spacegroup);
        assertNotNull(crystal.getSpaceGroup());
        assertEquals(spacegroup, crystal.getSpaceGroup());
    }

    public void testSetZ_int() {
        Crystal crystal = builder.newCrystal();
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
        Crystal crystal = builder.newCrystal();
        String description = crystal.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }

    public void testClone() {
        Crystal crystal = builder.newCrystal();
        Object clone = crystal.clone();
        assertTrue(clone instanceof Crystal);
    }

    public void testClone_Axes() {
        Crystal crystal1 = builder.newCrystal();
        Vector3d axes = new Vector3d(1.0, 2.0, 3.0);
        crystal1.setA(axes);
        Crystal crystal2 = (Crystal)crystal1.clone();

        // test cloning of axes
        crystal1.getA().x = 5.0;
        assertEquals(1.0, crystal2.getA().x, 0.001);
    }
    
    public void testSetZeroAxes() {
        Crystal crystal = builder.newCrystal();
        
        crystal.setA(new Vector3d(1.0, 2.0, 3.0));
        Vector3d a = crystal.getA();
        assertNotNull(a);
    }
    
    
}
