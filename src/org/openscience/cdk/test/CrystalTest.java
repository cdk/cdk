/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2002-2004  The Chemistry Development Kit (CDK) project
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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.Crystal;

/**
 * Checks the funcitonality of the Crystal.
 *
 * @cdkPackage test
 */
public class CrystalTest extends TestCase {

    public CrystalTest(String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        return new TestSuite(CrystalTest.class);
    }

    public void testSetA() {
        Crystal crystal = new Crystal();
        
        crystal.setA(1.0, 2.0, 3.0);
        double[] a = crystal.getA();
        assertEquals(1.0, a[0], 0.001);
        assertEquals(2.0, a[1], 0.001);
        assertEquals(3.0, a[2], 0.001);
    }
    
    public void testSetB() {
        Crystal crystal = new Crystal();
        
        crystal.setB(1.0, 2.0, 3.0);
        double[] b = crystal.getB();
        assertEquals(1.0, b[0], 0.001);
        assertEquals(2.0, b[1], 0.001);
        assertEquals(3.0, b[2], 0.001);
    }
    
    public void testSetC() {
        Crystal crystal = new Crystal();
        
        crystal.setC(1.0, 2.0, 3.0);
        double[] c = crystal.getC();
        assertEquals(1.0, c[0], 0.001);
        assertEquals(2.0, c[1], 0.001);
        assertEquals(3.0, c[2], 0.001);
    }
    
    public void testSetSpaceGroup() {
        Crystal crystal = new Crystal();
        String spacegroup = "P 2_1 2_1 2_1";
        crystal.setSpaceGroup(spacegroup);
        assertEquals(spacegroup, crystal.getSpaceGroup());
    }

    public void testSetZ() {
        Crystal crystal = new Crystal();
        int z = 2;
        crystal.setZ(z);
        assertEquals(z, crystal.getZ());
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
}
