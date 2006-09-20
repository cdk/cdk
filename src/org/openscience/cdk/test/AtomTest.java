/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2002-2006  The Chemistry Development Kit (CDK) project
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

import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

/**
 * Checks the functionality of the AtomTypeFactory
 *
 * @cdk.module test-data
 */
public class AtomTest extends CDKTestCase {

	protected IChemObjectBuilder builder;
	
    public AtomTest(String name) {
        super(name);
    }

    public void setUp() {
    	builder = DefaultChemObjectBuilder.getInstance();
    }

    public static Test suite() {
        return new TestSuite(AtomTest.class);
    }

    /**
     * Method to test the Atom(String symbol) method.
     */
    public void testAtom() {
        IAtom a = builder.newAtom();
        assertNotNull(a);
    }

    public void testAtom_IElement() {
    	IElement element = builder.newElement();
        IAtom a = builder.newAtom(element);
        assertNotNull(a);
    }

    /**
     * Method to test the Atom(String symbol) method.
     */
    public void testAtom_String() {
        IAtom a = builder.newAtom("C");
        assertEquals("C", a.getSymbol());
        assertNull(a.getPoint2d());
        assertNull(a.getPoint3d());
        assertNull(a.getFractionalPoint3d());
    }

    /**
     * Method to test the Atom(String symbol, javax.vecmath.Point3d point3D) method.
     */
    public void testAtom_String_Point3d() {
        Point3d point3d = new Point3d(1.0, 2.0, 3.0);

        IAtom a = builder.newAtom("C", point3d);
        assertEquals("C", a.getSymbol());
        assertEquals(point3d, a.getPoint3d());
        assertNull(a.getPoint2d());
        assertNull(a.getFractionalPoint3d());
    }

    /**
     * Method to test the Atom(String symbol, javax.vecmath.Point3d point3D) method.
     */
    public void testAtom_String_Point2d() {
        Point2d point2d = new Point2d(1.0, 2.0);

        IAtom a = builder.newAtom("C", point2d);
        assertEquals("C", a.getSymbol());
        assertEquals(point2d, a.getPoint2d());
        assertNull(a.getPoint3d());
        assertNull(a.getFractionalPoint3d());
    }

    /**
     * Method to test the get/setCharge() methods.
     */
    public void testSetCharge_double() {
        double charge = 0.15;

        IAtom a = builder.newAtom("C");
        a.setCharge(charge);
        assertEquals(charge, a.getCharge(), 0.001);
    }
    public void testGetCharge() {
        testSetCharge_double();
    }

    /**
     * Method to test the get/setHydrogenCount() methods.
     */
    public void testSetHydrogenCount_int() {
        int count = 1;

        IAtom a = builder.newAtom("C");
        a.setHydrogenCount(count);
        assertEquals(count, a.getHydrogenCount());
    }
    public void testGetHydrogenCount() {
        testSetHydrogenCount_int();
    }

    /**
     * Method to test the setFractional3D() methods.
     */
    public void testSetFractionalPoint3d_Point3d() {
        IAtom a = builder.newAtom("C");
        a.setFractionalPoint3d(new Point3d(0.5, 0.5, 0.5));
        Point3d fract = a.getFractionalPoint3d();
        assertNotNull(fract);
        assertEquals(0.5, fract.x, 0.001);
        assertEquals(0.5, fract.y, 0.001);
        assertEquals(0.5, fract.z, 0.001);
    }
    public void testGetFractionalPoint3d() {
        testSetFractionalPoint3d_Point3d();
    }
    
    public void testSetFractX3d_double() {
        IAtom a = builder.newAtom("C");
        a.setFractX3d(0.5);
        Point3d point3d = a.getFractionalPoint3d();
        assertEquals(0.5, point3d.x, 0.001);
    }

    public void testSetFractY3d_double() {
        IAtom a = builder.newAtom("C");
        a.setFractY3d(0.5);
        Point3d point3d = a.getFractionalPoint3d();
        assertEquals(0.5, point3d.y, 0.001);
    }

    public void testSetFractZ3d_double() {
        IAtom a = builder.newAtom("C");
        a.setFractZ3d(0.5);
        Point3d point3d = a.getFractionalPoint3d();
        assertEquals(0.5, point3d.z, 0.001);
    }

    /**
     * Method to test the set[XYZ]3D() methods.
     */
    public void testSetX3d_double() {
        IAtom a = builder.newAtom("C");
        a.setX3d(1.0);

        assertNotNull(a.getPoint3d());
        assertEquals(1.0, a.getPoint3d().x, 0.001);
    }
    public void testSetY3d_double() {
        IAtom a = builder.newAtom("C");
        a.setY3d(2.0);

        assertNotNull(a.getPoint3d());
        assertEquals(2.0, a.getPoint3d().y, 0.001);
    }
    public void testSetZ3d_double() {
        IAtom a = builder.newAtom("C");
        a.setZ3d(3.0);

        assertNotNull(a.getPoint3d());
        assertEquals(3.0, a.getPoint3d().z, 0.001);
    }
    
    public void testGetPoint3d() {
        Point3d point3d = new Point3d(1.0, 2.0, 3.0);
        
        IAtom a = builder.newAtom("C", point3d);
        assertNotNull(a.getPoint3d());
        assertEquals(point3d, a.getPoint3d(), 0.001);
    }
    public void testSetPoint3d_Point3d() {
        Point3d point3d = new Point3d(1.0, 2.0, 3.0);
        
        IAtom a = builder.newAtom("C");
        a.setPoint3d(point3d);
        assertEquals(point3d, a.getPoint3d());
    }
    
    /**
     * Method to test the set[XY]2D() methods.
     */
    public void testSetX2d_double() {

        IAtom a = builder.newAtom("C");
        a.setX2d(1.0);

        assertNotNull(a.getPoint2d());
        assertEquals(1.0, a.getPoint2d().x, 0.001);
    }
    public void testSetY2d_double() {

        IAtom a = builder.newAtom("C");
        a.setY2d(2.0);

        assertNotNull(a.getPoint2d());
        assertEquals(2.0, a.getPoint2d().y, 0.001);
    }
    
    public void testGetPoint2d() {
        Point2d point2d = new Point2d(1.0, 2.0);
        
        IAtom a = builder.newAtom("C", point2d);
        assertNotNull(a.getPoint2d());
        assertEquals(point2d.x, a.getPoint2d().x, 0.001);
        assertEquals(point2d.y, a.getPoint2d().y, 0.001);
    }
    public void testSetPoint2d_Point2d() {
        Point2d point2d = new Point2d(1.0, 2.0);
        
        IAtom a = builder.newAtom("C");
        a.setPoint2d(point2d);
        assertEquals(point2d, a.getPoint2d());
    }

    /**
     * Method to test the get/setHydrogenCount() methods.
     */
    public void testSetStereoParity_int() {
        int parity = CDKConstants.STEREO_ATOM_PARITY_PLUS;

        IAtom a = builder.newAtom("C");
        a.setStereoParity(parity);
        assertEquals(parity, a.getStereoParity());
    }
    public void testGetStereoParity() {
        testSetStereoParity_int();
    }
    
    /**
     * Method to test the compare() method.
     */
    public void testCompare_Object() {
        IAtom someAtom = builder.newAtom("C");
        if (someAtom instanceof org.openscience.cdk.Atom) {
        	org.openscience.cdk.Atom atom = (org.openscience.cdk.Atom)someAtom;
        	assertTrue(atom.compare(atom));
        	IAtom hydrogen = builder.newAtom("H");
        	assertFalse(atom.compare(hydrogen));
        	assertFalse(atom.compare("C"));
        }
    }
    
    /**
     * Method to test the clone() method
     */
    public void testClone() throws Exception {
        IAtom atom = builder.newAtom("C");
        Object clone = atom.clone();
        assertTrue(clone instanceof IAtom);
    }
    
    /**
     * Method to test the clone() method
     */
    public void testClone_Point2d() throws Exception {
        IAtom atom = builder.newAtom("C");
        atom.setPoint2d(new Point2d(2, 3));
        IAtom clone = (IAtom)atom.clone();

        // test cloning
        atom.setX2d(5);
        assertEquals(clone.getPoint2d().x, 2.0, 0.001);
    }

    /**
     * Method to test the clone() method
     */
    public void testClone_Point3d() throws Exception {
        IAtom atom = builder.newAtom("C");
        atom.setPoint3d(new Point3d(2, 3, 4));
        IAtom clone = (IAtom)atom.clone();

        // test cloning
        atom.setX3d(5);
        assertEquals(clone.getPoint3d().x, 2.0, 0.001);
    }

    /**
     * Method to test the clone() method
     */
    public void testClone_FractionalPoint3d() throws Exception {
        IAtom atom = builder.newAtom("C");
        atom.setFractionalPoint3d(new Point3d(2, 3, 4));
        IAtom clone = (IAtom)atom.clone();

        // test cloning
        atom.setFractX3d(5);
        assertEquals(clone.getFractionalPoint3d().x, 2.0, 0.001);
    }

    /**
     * Method to test the clone() method
     */
    public void testClone_HydrogenCount() throws Exception {
        IAtom atom = builder.newAtom("C");
        atom.setHydrogenCount(3);
        IAtom clone = (IAtom)atom.clone();

        // test cloning
        atom.setHydrogenCount(4);
        assertEquals(3, clone.getHydrogenCount());
    }

    /**
     * Method to test the clone() method
     */
    public void testClone_StereoParity() throws Exception {
        IAtom atom = builder.newAtom("C");
        atom.setStereoParity(3);
        IAtom clone = (IAtom)atom.clone();

        // test cloning
        atom.setStereoParity(4);
        assertEquals(3, clone.getStereoParity());
    }

    /**
     * Method to test the clone() method
     */
    public void testClone_Charge() throws Exception {
        IAtom atom = builder.newAtom("C");
        atom.setCharge(1.0);
        IAtom clone = (IAtom)atom.clone();

        // test cloning
        atom.setCharge(5.0);
        assertEquals(1.0, clone.getCharge(), 0.001);
    }

    /**
     * Method to test wether the class complies with RFC #9.
     */
    public void testToString() {
        IAtom atom = builder.newAtom("C");
        String description = atom.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue('\n' != description.charAt(i));
            assertTrue('\r' != description.charAt(i));
        }
    }

    /**
     * Checks that the default charge is set to NaN
     */
    public void testDefaultChargeValue() {
        IAtom atom = builder.newAtom("C");
        assertEquals(CDKConstants.UNSET, atom.getCharge(), 0.00000001);
    }
}
