/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IPseudoAtom;

/**
 * Checks the functionality of the AtomTypeFactory
 *
 * @cdk.module test-data
 */
<<<<<<< HEAD:src/test/org/openscience/cdk/PseudoAtomTest.java
public class PseudoAtomTest extends AtomTest {
=======
public class PseudoAtomTest extends CDKTestCase {
>>>>>>> bbc19522071c1b78697779bddcd7509e9314667e:src/test/org/openscience/cdk/PseudoAtomTest.java

	protected static IChemObjectBuilder builder;
	
    @BeforeClass public static void setUp() {
       	builder = DefaultChemObjectBuilder.getInstance();
    }

    @Test public void testPseudoAtom() {
        IPseudoAtom a = builder.newPseudoAtom();
        Assert.assertEquals("R", a.getSymbol());
        Assert.assertNull(a.getPoint3d());
        Assert.assertNull(a.getPoint2d());
        Assert.assertNull(a.getFractionalPoint3d());
    }
    
    @Test public void testPseudoAtom_IElement() {
    	IElement element = builder.newElement();
        IPseudoAtom a = builder.newPseudoAtom(element);
        Assert.assertEquals("R", a.getSymbol());
        Assert.assertNull(a.getPoint3d());
        Assert.assertNull(a.getPoint2d());
        Assert.assertNull(a.getFractionalPoint3d());
    }
    
    @Test public void testPseudoAtom_String() {
        String label = "Arg255";
        IPseudoAtom a = builder.newPseudoAtom(label);
        Assert.assertEquals("R", a.getSymbol());
        Assert.assertEquals(label, a.getLabel());
        Assert.assertNull(a.getPoint3d());
        Assert.assertNull(a.getPoint2d());
        Assert.assertNull(a.getFractionalPoint3d());
    }

    @Test public void testPseudoAtom_String_Point2d() {
        Point2d point = new Point2d(1.0, 2.0);
        String label = "Arg255";
        IPseudoAtom a = builder.newPseudoAtom(label, point);
        Assert.assertEquals("R", a.getSymbol());
        Assert.assertEquals(label, a.getLabel());
        Assert.assertEquals(point, a.getPoint2d());
        Assert.assertNull(a.getPoint3d());
        Assert.assertNull(a.getFractionalPoint3d());
    }

    @Test public void testPseudoAtom_String_Point3d() {
        Point3d point = new Point3d(1.0, 2.0, 3.0);
        String label = "Arg255";
        IPseudoAtom a = builder.newPseudoAtom(label, point);
        Assert.assertEquals("R", a.getSymbol());
        Assert.assertEquals(label, a.getLabel());
        Assert.assertEquals(point, a.getPoint3d());
        Assert.assertNull(a.getPoint2d());
        Assert.assertNull(a.getFractionalPoint3d());
    }

    @Test public void testGetLabel() {
        String label = "Arg255";
        IPseudoAtom a = builder.newPseudoAtom(label);
        Assert.assertEquals(label, a.getLabel());
    }

    @Test public void testSetLabel_String() {
        String label = "Arg255";
        IPseudoAtom atom = builder.newPseudoAtom(label);
        String label2 = "His66";
        atom.setLabel(label2);
        Assert.assertEquals(label2, atom.getLabel());
    }

    @Test public void testGetFormalCharge() {
        IPseudoAtom atom = builder.newPseudoAtom("Whatever");
        Assert.assertEquals(0, atom.getFormalCharge().intValue());
    }

    @Test public void testSetFormalCharge_Integer() {
        IPseudoAtom atom = builder.newPseudoAtom("Whatever");
        atom.setFormalCharge(+5);
        Assert.assertEquals(0, atom.getFormalCharge().intValue());
    }

    @Test public void testSetHydrogenCount_Integer() {
        IPseudoAtom atom = builder.newPseudoAtom("Whatever");
        atom.setHydrogenCount(+5);
        Assert.assertEquals(0, atom.getHydrogenCount().intValue());
    }

    @Test public void testSetCharge_Double() {
        IPseudoAtom atom = builder.newPseudoAtom("Whatever");
        atom.setCharge(0.78);
        Assert.assertEquals(0.0, atom.getCharge(), 0.001);
    }

    @Test public void testSetExactMass_Double() {
        IPseudoAtom atom = builder.newPseudoAtom("Whatever");
        atom.setExactMass(12.001);
        Assert.assertEquals(0.0, atom.getExactMass(), 0.001);
    }

    @Test public void testSetStereoParity_Integer() {
        IPseudoAtom atom = builder.newPseudoAtom("Whatever");
        atom.setStereoParity(-1);
        Assert.assertEquals(0, atom.getStereoParity().intValue());
    }

    @Test public void testPseudoAtom_IAtom() {
        IAtom atom = builder.newAtom("C");
        Point3d fract = new Point3d(0.5, 0.5, 0.5);
        Point3d threeD = new Point3d(0.5, 0.5, 0.5);
        Point2d twoD = new Point2d(0.5, 0.5);
        atom.setFractionalPoint3d(fract);
        atom.setPoint3d(threeD);
        atom.setPoint2d(twoD);
        
        IPseudoAtom a = builder.newPseudoAtom(atom);
        assertEquals(fract, a.getFractionalPoint3d(), 0.0001);
        assertEquals(threeD, a.getPoint3d(), 0.0001);
        assertEquals(twoD, a.getPoint2d(), 0.0001);
    }

    /**
     * Method to test the clone() method
     */
    @Test public void testClone() throws Exception {
        IAtom atom = builder.newPseudoAtom("C");
        Object clone = atom.clone();
        Assert.assertTrue(clone instanceof IPseudoAtom);
    }
    
    /**
     * Method to test whether the class complies with RFC #9.
     */
    @Test public void testToString() {
        IAtom atom = builder.newPseudoAtom("R");
        String description = atom.toString();
        for (int i=0; i< description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }
    
    /**
     * Test for bug #1778479 "MDLWriter writes empty PseudoAtom label string".
     * We decided to let the pseudo atoms have a default label of '*'.
     *
     * Author: Andreas Schueller <a.schueller@chemie.uni-frankfurt.de>
     * 
     * @cdk.bug 1778479
     */
    @Test public void testBug1778479DefaultLabel() {
        IPseudoAtom atom = builder.newPseudoAtom();
        Assert.assertNotNull("Test for PseudoAtom's default label", atom.getLabel());
        Assert.assertEquals("Test for PseudoAtom's default label", "*", atom.getLabel());
    }
}
