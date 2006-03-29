/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2003-2006  The Chemistry Development Kit (CDK) project
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

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * Checks the functionality of the AtomTypeFactory
 *
 * @cdk.module test
 */
public class PseudoAtomTest extends CDKTestCase {

	protected IChemObjectBuilder builder;
	
    public PseudoAtomTest(String name) {
        super(name);
    }

    public void setUp() {
       	builder = DefaultChemObjectBuilder.getInstance();
    }

    public static Test suite() {
        return new TestSuite(PseudoAtomTest.class);
    }

    public void testPseudoAtom() {
        IPseudoAtom a = builder.newPseudoAtom();
        assertEquals("R", a.getSymbol());
        assertNull(a.getPoint3d());
        assertNull(a.getPoint2d());
        assertNull(a.getFractionalPoint3d());
    }
    
    public void testPseudoAtom_String() {
        String label = "Arg255";
        IPseudoAtom a = builder.newPseudoAtom(label);
        assertEquals("R", a.getSymbol());
        assertEquals(label, a.getLabel());
        assertNull(a.getPoint3d());
        assertNull(a.getPoint2d());
        assertNull(a.getFractionalPoint3d());
    }

    public void testPseudoAtom_String_Point2d() {
        Point2d point = new Point2d(1.0, 2.0);
        String label = "Arg255";
        IPseudoAtom a = builder.newPseudoAtom(label, point);
        assertEquals("R", a.getSymbol());
        assertEquals(label, a.getLabel());
        assertEquals(point, a.getPoint2d());
        assertNull(a.getPoint3d());
        assertNull(a.getFractionalPoint3d());
    }

    public void testPseudoAtom_String_Point3d() {
        Point3d point = new Point3d(1.0, 2.0, 3.0);
        String label = "Arg255";
        IPseudoAtom a = builder.newPseudoAtom(label, point);
        assertEquals("R", a.getSymbol());
        assertEquals(label, a.getLabel());
        assertEquals(point, a.getPoint3d());
        assertNull(a.getPoint2d());
        assertNull(a.getFractionalPoint3d());
    }

    public void testGetLabel() {
        String label = "Arg255";
        IPseudoAtom a = builder.newPseudoAtom(label);
        assertEquals(label, a.getLabel());
    }

    public void testSetLabel_String() {
        String label = "Arg255";
        IPseudoAtom atom = builder.newPseudoAtom(label);
        String label2 = "His66";
        atom.setLabel(label2);
        assertEquals(label2, atom.getLabel());
    }

    public void testGetFormalCharge() {
        IPseudoAtom atom = builder.newPseudoAtom("Whatever");
        assertEquals(0, atom.getFormalCharge());
    }

    public void testSetFormalCharge_int() {
        IPseudoAtom atom = builder.newPseudoAtom("Whatever");
        atom.setFormalCharge(+5);
        assertEquals(0, atom.getFormalCharge());
    }

    public void testSetHydrogenCount_int() {
        IPseudoAtom atom = builder.newPseudoAtom("Whatever");
        atom.setHydrogenCount(+5);
        assertEquals(0, atom.getHydrogenCount());
    }

    public void testSetCharge_double() {
        IPseudoAtom atom = builder.newPseudoAtom("Whatever");
        atom.setCharge(0.78);
        assertEquals(0.0, atom.getCharge(), 0.001);
    }

    public void testSetExactMass_double() {
        IPseudoAtom atom = builder.newPseudoAtom("Whatever");
        atom.setExactMass(12.001);
        assertEquals(0.0, atom.getExactMass(), 0.001);
    }

    public void testSetStereoParity_int() {
        IPseudoAtom atom = builder.newPseudoAtom("Whatever");
        atom.setStereoParity(-1);
        assertEquals(0, atom.getStereoParity());
    }

    public void testPseudoAtom_IAtom() {
        IAtom atom = builder.newAtom("C");
        Point3d fract = new Point3d(0.5, 0.5, 0.5);
        Point3d threeD = new Point3d(0.5, 0.5, 0.5);
        Point2d twoD = new Point2d(0.5, 0.5);
        atom.setFractionalPoint3d(fract);
        atom.setPoint3d(threeD);
        atom.setPoint2d(twoD);
        
        IPseudoAtom a = builder.newPseudoAtom(atom);
        assertEquals(fract, a.getFractionalPoint3d());
        assertEquals(threeD, a.getPoint3d());
        assertEquals(twoD, a.getPoint2d());
    }

    /**
     * Method to test wether the class complies with RFC #9.
     */
    public void testToString() {
        IAtom atom = builder.newPseudoAtom("R");
        String description = atom.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }
}
