/*
 * $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
        assertTrue(null == a.getPoint2D());
        assertTrue(null == a.getPoint3D());
    }

    /**
     * Method to test the Atom(String symbol, javax.vecmath.Point3d point3D) method.
     */
    public void testAtom2() {
        Point3d point3d = new Point3d(1.0, 2.0, 3.0);

        Atom a = new Atom("C", point3d);
        assertEquals("C", a.getSymbol());
        assertEquals(point3d, a.getPoint3D());
        assertTrue(null == a.getPoint2D());
    }

    /**
     * Method to test the Atom(String symbol, javax.vecmath.Point3d point3D) method.
     */
    public void testAtom3() {
        Point2d point2d = new Point2d(1.0, 2.0);

        Atom a = new Atom("C", point2d);
        assertEquals("C", a.getSymbol());
        assertEquals(point2d, a.getPoint2D());
        assertTrue(null == a.getPoint3D());
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
     * Method to test the set/get3D() methods.
     */
    public void testSet3D() {

        Atom a = new Atom("C");
        a.setX3D(1.0);
        a.setY3D(2.0);
        a.setZ3D(3.0);

        assertTrue(a.getPoint3D() != null);
    }
}
