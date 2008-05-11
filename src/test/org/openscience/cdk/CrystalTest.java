/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2002-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk;

import javax.vecmath.Vector3d;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.ICrystal;

/**
 * Checks the functionality of the Crystal.
 *
 * @cdk.module test-data
 */
public class CrystalTest extends NewCDKTestCase {

	protected static IChemObjectBuilder builder;
	
    @BeforeClass public static void setUp() {
    	builder = DefaultChemObjectBuilder.getInstance();
    }

    @Test public void testCrystal() {
        ICrystal crystal = builder.newCrystal();
        Assert.assertNotNull(crystal);
        Assert.assertEquals(0, crystal.getAtomCount());
        Assert.assertEquals(0, crystal.getBondCount());
    }
    
    @Test public void testCrystal_IAtomContainer() {
        IAtomContainer acetone = builder.newAtomContainer();
        IAtom c1 = builder.newAtom("C");
        IAtom c2 = builder.newAtom("C");
        IAtom o = builder.newAtom("O");
        IAtom c3 = builder.newAtom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        IBond b1 = builder.newBond(c1, c2, IBond.Order.SINGLE);
        IBond b2 = builder.newBond(c1, o, IBond.Order.DOUBLE);
        IBond b3 = builder.newBond(c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        ICrystal crystal = builder.newCrystal(acetone);
        Assert.assertNotNull(crystal);
        Assert.assertEquals(4, crystal.getAtomCount());
        Assert.assertEquals(3, crystal.getBondCount());
    }
    
    @Test public void testAdd_IAtomContainer() {
        IAtomContainer acetone = builder.newAtomContainer();
        IAtom c1 = builder.newAtom("C");
        IAtom c2 = builder.newAtom("C");
        IAtom o = builder.newAtom("O");
        IAtom c3 = builder.newAtom("C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        IBond b1 = builder.newBond(c1, c2, IBond.Order.SINGLE);
        IBond b2 = builder.newBond(c1, o, IBond.Order.DOUBLE);
        IBond b3 = builder.newBond(c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);
        
        ICrystal crystal = builder.newCrystal();
        crystal.add(acetone);
        Assert.assertEquals(4, crystal.getAtomCount());
        Assert.assertEquals(3, crystal.getBondCount());
    }
    
    @Test public void testAddAtom_IAtom() {
        IAtom c1 = builder.newAtom("C");
        ICrystal crystal = builder.newCrystal();
        crystal.addAtom(c1);
        Assert.assertEquals(1, crystal.getAtomCount());
    }

    @Test public void testSetA_Vector3d() {
        ICrystal crystal = builder.newCrystal();
        
        crystal.setA(new Vector3d(1.0, 2.0, 3.0));
        Vector3d a = crystal.getA();
        Assert.assertEquals(1.0, a.x, 0.001);
        Assert.assertEquals(2.0, a.y, 0.001);
        Assert.assertEquals(3.0, a.z, 0.001);
    }
    
    @Test public void testGetA() {
        ICrystal crystal = builder.newCrystal();
        
        crystal.setA(new Vector3d(1.0, 2.0, 3.0));
        Vector3d a = crystal.getA();
        Assert.assertNotNull(a);
    }
    
    @Test public void testGetB() {
        ICrystal crystal = builder.newCrystal();
        
        crystal.setB(new Vector3d(1.0, 2.0, 3.0));
        Vector3d a = crystal.getB();
	Assert.assertNotNull(a);
    }
    
    @Test public void testGetC() {
        ICrystal crystal = builder.newCrystal();
        
        crystal.setC(new Vector3d(1.0, 2.0, 3.0));
        Vector3d a = crystal.getC();
	Assert.assertNotNull(a);
    }
    
    @Test public void testSetB_Vector3d() {
        ICrystal crystal = builder.newCrystal();
        
        crystal.setB(new Vector3d(1.0, 2.0, 3.0));
        Vector3d b = crystal.getB();
        Assert.assertEquals(1.0, b.x, 0.001);
        Assert.assertEquals(2.0, b.y, 0.001);
        Assert.assertEquals(3.0, b.z, 0.001);
    }
    
    @Test public void testSetC_Vector3d() {
        ICrystal crystal = builder.newCrystal();
        
        crystal.setC(new Vector3d(1.0, 2.0, 3.0));
        Vector3d c = crystal.getC();
        Assert.assertEquals(1.0, c.x, 0.001);
        Assert.assertEquals(2.0, c.y, 0.001);
        Assert.assertEquals(3.0, c.z, 0.001);
    }
    
    @Test public void testSetSpaceGroup_String() {
        ICrystal crystal = builder.newCrystal();
        String spacegroup = "P 2_1 2_1 2_1";
        crystal.setSpaceGroup(spacegroup);
        Assert.assertEquals(spacegroup, crystal.getSpaceGroup());
    }

    @Test public void testGetSpaceGroup() {
        ICrystal crystal = builder.newCrystal();
        String spacegroup = "P 2_1 2_1 2_1";
        crystal.setSpaceGroup(spacegroup);
        Assert.assertNotNull(crystal.getSpaceGroup());
        Assert.assertEquals(spacegroup, crystal.getSpaceGroup());
    }

    @Test public void testSetZ_Integer() {
        ICrystal crystal = builder.newCrystal();
        int z = 2;
        crystal.setZ(z);
        Assert.assertEquals(z, crystal.getZ());
    }
    @Test public void testGetZ() {
        testSetZ_Integer();
    }

    /**
     * Method to test whether the class complies with RFC #9.
     */
    @Test public void testToString() {
        ICrystal crystal = builder.newCrystal();
        String description = crystal.toString();
        for (int i=0; i< description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }

    @Test public void testClone() throws Exception {
        ICrystal crystal = builder.newCrystal();
        Object clone = crystal.clone();
        Assert.assertTrue(clone instanceof ICrystal);
    }

    @Test public void testClone_Axes() throws Exception {
        ICrystal crystal1 = builder.newCrystal();
        Vector3d axes = new Vector3d(1.0, 2.0, 3.0);
        crystal1.setA(axes);
        ICrystal crystal2 = (ICrystal)crystal1.clone();

        // test cloning of axes
        crystal1.getA().x = 5.0;
        Assert.assertEquals(1.0, crystal2.getA().x, 0.001);
    }
    
    @Test public void testSetZeroAxes() {
        ICrystal crystal = builder.newCrystal();
        
        crystal.setA(new Vector3d(1.0, 2.0, 3.0));
        Vector3d a = crystal.getA();
        Assert.assertNotNull(a);
    }
    
    
}
