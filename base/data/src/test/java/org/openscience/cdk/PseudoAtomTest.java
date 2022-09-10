/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.interfaces.AbstractPseudoAtomTest;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IPseudoAtom;

import org.junit.jupiter.api.BeforeAll;

/**
 * Checks the functionality of the AtomTypeFactory
 *
 * @cdk.module test-data
 */
public class PseudoAtomTest extends AbstractPseudoAtomTest {

    @BeforeAll
    public static void setUp() {
        setTestObjectBuilder(PseudoAtom::new);
    }

    @Test
    public void testPseudoAtom() {
        IPseudoAtom a = new PseudoAtom();
        Assertions.assertEquals("R", a.getSymbol());
        Assertions.assertNull(a.getPoint3d());
        Assertions.assertNull(a.getPoint2d());
        Assertions.assertNull(a.getFractionalPoint3d());
    }

    @Test
    public void testPseudoAtom_IElement() {
        IElement element = newChemObject().getBuilder().newInstance(IElement.class);
        IPseudoAtom a = new PseudoAtom(element);
        Assertions.assertEquals("R", a.getSymbol());
        Assertions.assertNull(a.getPoint3d());
        Assertions.assertNull(a.getPoint2d());
        Assertions.assertNull(a.getFractionalPoint3d());
    }

    @Test
    public void testPseudoAtom_String() {
        String label = "Arg255";
        IPseudoAtom a = new PseudoAtom(label);
        Assertions.assertEquals("R", a.getSymbol());
        Assertions.assertEquals(label, a.getLabel());
        Assertions.assertNull(a.getPoint3d());
        Assertions.assertNull(a.getPoint2d());
        Assertions.assertNull(a.getFractionalPoint3d());
    }

    @Test
    public void testPseudoAtom_String_Point2d() {
        Point2d point = new Point2d(1.0, 2.0);
        String label = "Arg255";
        IPseudoAtom a = new PseudoAtom(label, point);
        Assertions.assertEquals("R", a.getSymbol());
        Assertions.assertEquals(label, a.getLabel());
        Assertions.assertEquals(point, a.getPoint2d());
        Assertions.assertNull(a.getPoint3d());
        Assertions.assertNull(a.getFractionalPoint3d());
    }

    @Test
    public void testPseudoAtom_String_Point3d() {
        Point3d point = new Point3d(1.0, 2.0, 3.0);
        String label = "Arg255";
        IPseudoAtom a = new PseudoAtom(label, point);
        Assertions.assertEquals("R", a.getSymbol());
        Assertions.assertEquals(label, a.getLabel());
        Assertions.assertEquals(point, a.getPoint3d());
        Assertions.assertNull(a.getPoint2d());
        Assertions.assertNull(a.getFractionalPoint3d());
    }
}
