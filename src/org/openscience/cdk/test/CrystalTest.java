/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2002-2003  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.*;
import java.util.*;
import junit.framework.*;
import javax.vecmath.*;

/**
 * Checks the funcitonality of the Crystal.
 */
public class CrystalTest extends TestCase {

    public CrystalTest(String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        return new TestSuite(CrystalTest.class);
    }

    public void testSetABC() {
        Crystal crystal = new Crystal();
        
        crystal.setA(1.0, 2.0, 3.0);
        double[] a = crystal.getA();
        assertTrue(1.0 == a[0]);
        assertTrue(2.0 == a[1]);
        assertTrue(3.0 == a[2]);
        
        crystal.setB(1.0, 2.0, 3.0);
        double[] b = crystal.getB();
        assertTrue(1.0 == b[0]);
        assertTrue(2.0 == b[1]);
        assertTrue(3.0 == b[2]);

        crystal.setC(1.0, 2.0, 3.0);
        double[] c = crystal.getC();
        assertTrue(1.0 == c[0]);
        assertTrue(2.0 == c[1]);
        assertTrue(3.0 == c[2]);
    }
    
    public void testSetSpaceGroup() {
        Crystal crystal = new Crystal();
        crystal.setSpaceGroup("P 2_1 2_1 2_1");
        assertEquals("P 2_1 2_1 2_1", crystal.getSpaceGroup());
        
        assertEquals(4, crystal.getZ());
    }

    public void testGetP1Cell() {
        Crystal crystal = new Crystal();
        crystal.addAtom(new Atom("C"));
        crystal.addAtom(new Atom("C"));
        crystal.addAtom(new Atom("C"));
        
        crystal.setSpaceGroup("P 2_1 2_1 2_1");
        Crystal p1 = crystal.getP1Cell();
        assertEquals(12, p1.getAtomCount());
    }

}
