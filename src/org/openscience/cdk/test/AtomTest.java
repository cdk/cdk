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

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.CDKConstants;

/**
 * Checks the functionality of the AtomTypeFactory
 *
 * @cdk.module test
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
        assertNull(a.getPoint2d());
        assertNull(a.getPoint3d());
        assertNull(a.getFractionalPoint3d());
    }

    /**
     * Method to test the Atom(String symbol, javax.vecmath.Point3d point3D) method.
     */
    public void testAtom2() {
        Point3d point3d = new Point3d(1.0, 2.0, 3.0);

        Atom a = new Atom("C", point3d);
        assertEquals("C", a.getSymbol());
        assertEquals(point3d, a.getPoint3d());
        assertNull(a.getPoint2d());
        assertNull(a.getFractionalPoint3d());
    }

    /**
     * Method to test the Atom(String symbol, javax.vecmath.Point3d point3D) method.
     */
    public void testAtom3() {
        Point2d point2d = new Point2d(1.0, 2.0);

        Atom a = new Atom("C", point2d);
        assertEquals("C", a.getSymbol());
        assertEquals(point2d, a.getPoint2d());
        assertNull(a.getPoint3d());
        assertNull(a.getFractionalPoint3d());
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
        a.setFractionalPoint3d(new Point3d(0.5, 0.5, 0.5));
        Point3d fract = a.getFractionalPoint3d();
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
        a.setX3d(1.0);
        a.setY3d(2.0);
        a.setZ3d(3.0);

        assertTrue(a.getPoint3d() != null);
    }
    
    /**
     * Method to test the get[XYZ]3D() methods.
     */
    public void testGet3D() {
        Point3d point3d = new Point3d(1.0, 2.0, 3.0);
        
        Atom a = new Atom("C", point3d);
        assertTrue(point3d.x == a.getX3d());
        assertTrue(point3d.y == a.getY3d());
        assertTrue(point3d.z == a.getZ3d());
    }

    /**
     * Method to test the set[XY]2D() methods.
     */
    public void testSet2D() {

        Atom a = new Atom("C");
        a.setX2d(1.0);
        a.setY2d(2.0);

        assertTrue(a.getPoint2d() != null);
    }
    
    /**
     * Method to test the get[XY]2D() methods.
     */
    public void testGet2D() {
        Point2d point2d = new Point2d(1.0, 2.0);
        
        Atom a = new Atom("C", point2d);
        assertTrue(point2d.x == a.getX2d());
        assertTrue(point2d.y == a.getY2d());
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
        atom.setPoint2d(new Point2d(2, 3));
        atom.setPoint3d(new Point3d(2, 3, 4));
        atom.setFractionalPoint3d(new Point3d(2, 3, 4));
        Object clone = atom.clone();
        assertTrue(clone instanceof Atom);
        Atom copy = (Atom)clone;
        assertTrue(atom.compare(copy));
        
        // make sure the Point2d fields have been cloned too
        atom.setX2d(5);
        assertEquals(copy.getX2d(), 2.0, 0.001);
        atom.setX3d(5);
        assertEquals(copy.getX3d(), 2.0, 0.001);
        atom.setFractX3d(5);
        assertEquals(copy.getFractX3d(), 2.0, 0.001);
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
