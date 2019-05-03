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

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;

/**
 * Checks the functionality of the Association class.
 *
 * @cdk.module test-extra
 *
 * @see org.openscience.cdk.Association
 */
public class AssociationTest extends CDKTestCase {

    @Test
    public void testAssociation() {
        Association association = new Association();
        Assert.assertEquals(0, association.getElectronCount().intValue());
        Assert.assertEquals(0, association.getAtomCount());
    }

    @Test
    public void testAssociation_IAtom_IAtom() {
        Association association = new Association(new Atom("C"), new Atom("C"));
        Assert.assertEquals(0, association.getElectronCount().intValue());
        Assert.assertEquals(2, association.getAtomCount());
    }

    /** Test for RFC #9 */
    @Test
    public void testToString() {
        Association association = new Association();
        String description = association.toString();
        for (int i = 0; i < description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }

    @Test
    public void testToStringWithAtoms() {
        Association association = new Association(new Atom("C"), new Atom("C"));
        String description = association.toString();
        Assert.assertTrue(description.contains(","));
    }

    @Test
    public void testContains() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");

        Association association = new Association(c, o);

        Assert.assertTrue(association.contains(c));
        Assert.assertTrue(association.contains(o));
    }

    @Test
    public void testGetAtomCount() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");

        Association association = new Association(c, o);

        Assert.assertEquals(2, association.getAtomCount());
    }

    @Test
    public void testGetAtoms() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");

        Association association = new Association(c, o);

        IAtom[] atoms = association.getAtoms();
        Assert.assertEquals(2, atoms.length);
        Assert.assertNotNull(atoms[0]);
        Assert.assertNotNull(atoms[1]);
    }

    @Test
    public void testSetAtoms() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");
        Association association = new Association();
        association.setAtoms(new IAtom[]{c, o});

        Assert.assertTrue(association.contains(c));
        Assert.assertTrue(association.contains(o));
    }

    @Test
    public void testSetAtomAt() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");
        Atom n = new Atom("N");
        Association association = new Association(c, o);
        association.setAtomAt(n, 1);

        Assert.assertTrue(association.contains(c));
        Assert.assertTrue(association.contains(n));
        Assert.assertFalse(association.contains(o));
    }

    @Test
    public void testGetAtomAt() {
        Atom c = new Atom("C");
        Atom o = new Atom("O");
        Atom n = new Atom("N");
        Association association = new Association(c, o);

        Assert.assertEquals(c, association.getAtomAt(0));
        Assert.assertEquals(o, association.getAtomAt(1));

        association.setAtomAt(n, 0);
        Assert.assertEquals(n, association.getAtomAt(0));
    }

    @Test
    public void testGetElectronCount() {
        Association association = new Association();
        Assert.assertEquals(0, association.getElectronCount(), 0.00001);
    }
}
