/* Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
package org.openscience.cdk.protein.data;

import javax.vecmath.Point3d;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.interfaces.AbstractPDBAtomTest;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IPDBAtom;
import org.openscience.cdk.test.interfaces.ITestObjectBuilder;

/**
 * Checks the functionality of the {@link PDBAtom} class.
 *
 * @cdk.module test-data
 */
class PDBAtomTest extends AbstractPDBAtomTest {

    @BeforeAll
    static void setUp() {
        setTestObjectBuilder(new ITestObjectBuilder() {

            @Override
            public IChemObject newTestObject() {
                return new PDBAtom("C");
            }
        });
    }

    @Test
    void testPDBAtom_IElement() {
        IElement element = newChemObject().getBuilder().newInstance(IElement.class);
        IAtom a = new PDBAtom(element);
        Assertions.assertNotNull(a);
    }

    /**
     * Method to test the Atom(String symbol) method.
     */
    @Test
    void testPDBAtom_String() {
        IPDBAtom a = new PDBAtom("C");
        Assertions.assertEquals("C", a.getSymbol());
        Assertions.assertNull(a.getPoint2d());
        Assertions.assertNull(a.getPoint3d());
        Assertions.assertNull(a.getFractionalPoint3d());
    }

    /**
     * Method to test the Atom(String symbol, javax.vecmath.Point3d point3D) method.
     */
    @Test
    void testPDBAtom_String_Point3d() {
        Point3d point3d = new Point3d(1.0, 2.0, 3.0);

        IPDBAtom a = new PDBAtom("C", point3d);
        Assertions.assertEquals("C", a.getSymbol());
        Assertions.assertEquals(point3d, a.getPoint3d());
        Assertions.assertNull(a.getPoint2d());
        Assertions.assertNull(a.getFractionalPoint3d());
    }

    /**
     * Method to test the compare() method.
     */
    @Test
    @Override
    public void testCompare_Object() {
        IPDBAtom someAtom = new PDBAtom("C");
        if (someAtom instanceof org.openscience.cdk.Atom) {
            org.openscience.cdk.Atom atom = (org.openscience.cdk.Atom) someAtom;
            Assertions.assertTrue(atom.compare(atom));
            IAtom hydrogen = atom.getBuilder().newInstance(IAtom.class, "H");
            Assertions.assertFalse(atom.compare(hydrogen));
            Assertions.assertFalse(atom.compare("C"));
        }
    }

}
