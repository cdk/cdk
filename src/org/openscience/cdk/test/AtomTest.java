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
import org.openscience.cdk.tools.*;
import java.util.*;
import junit.framework.*;
import javax.vecmath.*;

/**
 * Checks the functionality of the AtomTypeFactory
 */
public class AtomTest extends TestCase {

    public AtomTest(String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        return new TestSuite(AtomTest.class);
    }

    /**
     * Method to test the Atom(String symbol) method.
     */
    public void testAtom() {
        Atom a = new Atom("C");
        assertEquals("C", a.getSymbol());
        assertNull(a.getPoint2D());
        assertNull(a.getPoint3D());
        assertNull(a.getFractionalPoint3D());
    }

    /**
     * Method to test the Atom(String symbol, javax.vecmath.Point3d point3D) method.
     */
    public void testAtom2() {
        Point3d point3d = new Point3d(1.0, 2.0, 3.0);

        Atom a = new Atom("C", point3d);
        assertEquals("C", a.getSymbol());
        assertEquals(point3d, a.getPoint3D());
        assertNull(a.getPoint2D());
        assertNull(a.getFractionalPoint3D());
    }

    /**
     * Method to test the Atom(String symbol, javax.vecmath.Point3d point3D) method.
     */
    public void testAtom3() {
        Point2d point2d = new Point2d(1.0, 2.0);

        Atom a = new Atom("C", point2d);
        assertEquals("C", a.getSymbol());
        assertEquals(point2d, a.getPoint2D());
        assertNull(a.getPoint3D());
        assertNull(a.getFractionalPoint3D());
    }

    /**
     * Method to test the get/setCharge() methods.
     */
    public void testSetCharge() {
        double charge = 0.15;

        Atom a = new Atom("C");
        a.setCharge(charge);
        assertTrue(charge == a.getCharge());
    }

    /**
     * Method to test the get/setFormalCharge() methods.
     */
    public void testSetFormalCharge() {
        int charge = 1;

        Atom a = new Atom("C");
        a.setFormalCharge(charge);
        assertEquals(charge, a.getFormalCharge());
    }

    /**
     * Method to test the get/setHydrogenCount() methods.
     */
    public void testSetHydrogenCount() {
        int count = 1;

        Atom a = new Atom("C");
        a.setHydrogenCount(count);
        assertEquals(count, a.getHydrogenCount());
    }

    /**
     * Method to test the set[XYZ]3D() methods.
     */
    public void testSetFractional3D() {
        Atom a = new Atom("C");
        a.setFractionalPoint3D(new Point3d(0.5, 0.5, 0.5));
        Point3d fract = a.getFractionalPoint3D();
        assertNotNull(fract);
        assertEquals(0.5, fract.x, 0.001);
        assertEquals(0.5, fract.y, 0.001);
        assertEquals(0.5, fract.z, 0.001);
    }
    
    /**
     * Method to test the set[XYZ]3D() methods.
     */
    public void testSet3D() {

        Atom a = new Atom("C");
        a.setX3D(1.0);
        a.setY3D(2.0);
        a.setZ3D(3.0);

        assertTrue(a.getPoint3D() != null);
    }
    
    /**
     * Method to test the get[XYZ]3D() methods.
     */
    public void testGet3D() {
        Point3d point3d = new Point3d(1.0, 2.0, 3.0);
        
        Atom a = new Atom("C", point3d);
        assertTrue(point3d.x == a.getX3D());
        assertTrue(point3d.y == a.getY3D());
        assertTrue(point3d.z == a.getZ3D());
    }

    /**
     * Method to test the set[XY]2D() methods.
     */
    public void testSet2D() {

        Atom a = new Atom("C");
        a.setX2D(1.0);
        a.setY2D(2.0);

        assertTrue(a.getPoint2D() != null);
    }
    
    /**
     * Method to test the get[XY]2D() methods.
     */
    public void testGet2D() {
        Point2d point2d = new Point2d(1.0, 2.0);
        
        Atom a = new Atom("C", point2d);
        assertTrue(point2d.x == a.getX2D());
        assertTrue(point2d.y == a.getY2D());
    }

    /**
     * Method to test the get/setHydrogenCount() methods.
     */
    public void testSetStereoParity() {
        int parity = CDKConstants.STEREO_ATOM_PARITY_PLUS;

        Atom a = new Atom("C");
        a.setStereoParity(parity);
        assertEquals(parity, a.getStereoParity());
    }
    
    /**
     * Method to test the compare() method.
     */
    public void testCompare() {
        Atom atom = new Atom("C");
        assertTrue(atom.compare(atom));
        Atom hydrogen = new Atom("H");
        assertTrue(!atom.compare(hydrogen));
        assertTrue(!atom.compare("C"));
    }
    
    /**
     * Method to test the clone() method
     */
    public void testClone() {
        Atom atom = new Atom("C");
        Object clone = atom.clone();
        assertTrue(clone instanceof Atom);
        Atom copy = (Atom)clone;
        assertTrue(atom.compare(copy));
    }
    
    /**
     * Method to test wether the class complies with RFC #9.
     */
    public void testToString() {
        Atom atom = new Atom("C");
        String description = atom.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }
}
