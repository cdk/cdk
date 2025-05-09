/* Copyright (C) 2002-2007  The Chemistry Development Kit (CDK) project
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.test.interfaces.AbstractAtomTest;
import org.openscience.cdk.interfaces.IElement;

/**
 * Checks the functionality of the AtomTypeFactory
 *
 */
class AtomTest extends AbstractAtomTest {

    @BeforeAll
    static void setUp() {
        setTestObjectBuilder(Atom::new);
    }

    /**
     * Method to test the Atom(String symbol) method.
     */
    @Test
    void testAtom() {
        IAtom a = new Atom();
        Assertions.assertNotNull(a);
    }

    @Test
    void testAtom_IElement() {
        IElement element = newChemObject().getBuilder().newInstance(IElement.class);
        IAtom a = new Atom(element);
        Assertions.assertNotNull(a);
    }

    /**
     * Method to test the Atom(String symbol) method.
     */
    @Test
    void testAtom_String() {
        IAtom a = new Atom("C");
        Assertions.assertEquals("C", a.getSymbol());
        Assertions.assertNull(a.getPoint2d());
        Assertions.assertNull(a.getPoint3d());
        Assertions.assertNull(a.getFractionalPoint3d());
    }

    @Test
    void testAtom_NH4plus_direct() {
        IAtom a = new Atom(7, 4, +1);
        Assertions.assertEquals("N", a.getSymbol());
        Assertions.assertEquals((Integer) 7, a.getAtomicNumber());
        Assertions.assertEquals((Integer) 4, a.getImplicitHydrogenCount());
        Assertions.assertEquals((Integer) 1, a.getFormalCharge());
        Assertions.assertNull(a.getPoint2d());
        Assertions.assertNull(a.getPoint3d());
        Assertions.assertNull(a.getFractionalPoint3d());
    }

    @Test
    void testAtom_CH3_direct() {
        IAtom a = new Atom(6, 3);
        Assertions.assertEquals("C", a.getSymbol());
        Assertions.assertEquals((Integer) 6, a.getAtomicNumber());
        Assertions.assertEquals((Integer) 3, a.getImplicitHydrogenCount());
        Assertions.assertEquals((Integer) 0, a.getFormalCharge());
        Assertions.assertNull(a.getPoint2d());
        Assertions.assertNull(a.getPoint3d());
        Assertions.assertNull(a.getFractionalPoint3d());
    }

    @Test
    void testAtom_Cl_direct() {
        IAtom a = new Atom(17);
        Assertions.assertEquals("Cl", a.getSymbol());
        Assertions.assertEquals((Integer) 17, a.getAtomicNumber());
        Assertions.assertEquals((Integer) 0, a.getImplicitHydrogenCount());
        Assertions.assertEquals((Integer) 0, a.getFormalCharge());
        Assertions.assertNull(a.getPoint2d());
        Assertions.assertNull(a.getPoint3d());
        Assertions.assertNull(a.getFractionalPoint3d());
    }

    @Test
    void testAtom_NH4plus() {
        IAtom a = new Atom("NH4+");
        Assertions.assertEquals("N", a.getSymbol());
        Assertions.assertEquals((Integer) 7, a.getAtomicNumber());
        Assertions.assertEquals((Integer) 4, a.getImplicitHydrogenCount());
        Assertions.assertEquals((Integer) 1, a.getFormalCharge());
        Assertions.assertNull(a.getPoint2d());
        Assertions.assertNull(a.getPoint3d());
        Assertions.assertNull(a.getFractionalPoint3d());
    }

    @Test
    void testAtom_Ominus() {
        IAtom a = new Atom("O-");
        Assertions.assertEquals("O", a.getSymbol());
        Assertions.assertEquals((Integer) 8, a.getAtomicNumber());
        Assertions.assertEquals(null, a.getImplicitHydrogenCount());
        Assertions.assertEquals(Integer.valueOf(-1), a.getFormalCharge());
        Assertions.assertNull(a.getPoint2d());
        Assertions.assertNull(a.getPoint3d());
        Assertions.assertNull(a.getFractionalPoint3d());
    }

    @Test
    void testAtom_Ca2plus() {
        IAtom a = new Atom("Ca+2");
        Assertions.assertEquals("Ca", a.getSymbol());
        Assertions.assertEquals((Integer) 20, a.getAtomicNumber());
        Assertions.assertEquals(null, a.getImplicitHydrogenCount());
        Assertions.assertEquals(Integer.valueOf(+2), a.getFormalCharge());
        Assertions.assertNull(a.getPoint2d());
        Assertions.assertNull(a.getPoint3d());
        Assertions.assertNull(a.getFractionalPoint3d());
    }

    @Test
    void testAtom_13CH3() {
        IAtom a = new Atom("13CH3");
        Assertions.assertEquals("C", a.getSymbol());
        Assertions.assertEquals((Integer) 13, a.getMassNumber());
        Assertions.assertEquals((Integer) 6, a.getAtomicNumber());
        Assertions.assertEquals((Integer) 3, a.getImplicitHydrogenCount());
        Assertions.assertEquals((Integer) 0, a.getFormalCharge());
        Assertions.assertNull(a.getPoint2d());
        Assertions.assertNull(a.getPoint3d());
        Assertions.assertNull(a.getFractionalPoint3d());
    }


    /**
     * Method to test the Atom(String symbol, javax.vecmath.Point3d point3D) method.
     */
    @Test
    void testAtom_String_Point3d() {
        Point3d point3d = new Point3d(1.0, 2.0, 3.0);

        IAtom a = new Atom("C", point3d);
        Assertions.assertEquals("C", a.getSymbol());
        Assertions.assertEquals(point3d, a.getPoint3d());
        Assertions.assertNull(a.getPoint2d());
        Assertions.assertNull(a.getFractionalPoint3d());
    }

    /**
     * Method to test the Atom(String symbol, javax.vecmath.Point3d point3D) method.
     */
    @Test
    void testAtom_String_Point2d() {
        Point2d point2d = new Point2d(1.0, 2.0);

        IAtom a = new Atom("C", point2d);
        Assertions.assertEquals("C", a.getSymbol());
        Assertions.assertEquals(point2d, a.getPoint2d());
        Assertions.assertNull(a.getPoint3d());
        Assertions.assertNull(a.getFractionalPoint3d());
    }

    /**
     * Method to test the compare() method.
     */
    @Test
    @Override
    public void testCompare_Object() {
        IAtom someAtom = new Atom("C");
        if (someAtom instanceof org.openscience.cdk.Atom) {
            org.openscience.cdk.Atom atom = (org.openscience.cdk.Atom) someAtom;
            Assertions.assertTrue(atom.compare(atom));
            IAtom hydrogen = someAtom.getBuilder().newInstance(IAtom.class, "H");
            Assertions.assertFalse(atom.compare(hydrogen));
            Assertions.assertFalse(atom.compare("C"));
        }
    }

    @Test
    void testNewAtomImplicitHydrogenCount() {
        Assertions.assertNull(new Atom("C").getImplicitHydrogenCount());
        Assertions.assertNull(new Atom("*").getImplicitHydrogenCount());
        Assertions.assertNull(new Atom("H").getImplicitHydrogenCount());
        Assertions.assertNull(new Atom("D").getImplicitHydrogenCount());
        Assertions.assertNull(new Atom("T").getImplicitHydrogenCount());
    }
}
