/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.stereo;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo;

/**
 * @cdk.module test-data
 */
public class TetrahedralChiralityTest extends CDKTestCase {

    private static IMolecule molecule;
    private static IAtom[] ligands;

    @BeforeClass
    public static void setup() throws Exception {
        molecule = new Molecule();
        molecule.addAtom(new Atom("Cl"));
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("Br"));
        molecule.addAtom(new Atom("I"));
        molecule.addAtom(new Atom("H"));
        molecule.addBond(0, 1, Order.SINGLE);
        molecule.addBond(1, 2, Order.SINGLE);
        molecule.addBond(1, 3, Order.SINGLE);
        molecule.addBond(1, 4, Order.SINGLE);
        ligands = new IAtom[] {
            molecule.getAtom(4),
            molecule.getAtom(3),
            molecule.getAtom(2),
            molecule.getAtom(0)
        };
    }

    @Test
    public void testTetrahedralChirality_IAtom_arrayIAtom_ITetrahedralChirality_Stereo() {
        TetrahedralChirality chirality = new TetrahedralChirality(
            molecule.getAtom(1), ligands, Stereo.CLOCKWISE
        );
        Assert.assertNotNull(chirality);
    }

    @Test
    public void testGetBuilder() {
        TetrahedralChirality chirality = new TetrahedralChirality(
            molecule.getAtom(1), ligands, Stereo.CLOCKWISE
        );
        Assert.assertEquals(
            DefaultChemObjectBuilder.getInstance(),
            chirality.getBuilder()
        );
    }

    @Test
    public void testGetChiralAtom() {
        TetrahedralChirality chirality = new TetrahedralChirality(
            molecule.getAtom(1), ligands, Stereo.CLOCKWISE
        );
        Assert.assertNotNull(chirality);
        Assert.assertEquals(molecule.getAtom(1), chirality.getChiralAtom());
    }

    @Test
    public void testGetStereo() {
        TetrahedralChirality chirality = new TetrahedralChirality(
            molecule.getAtom(1), ligands, Stereo.CLOCKWISE
        );
        Assert.assertNotNull(chirality);
        Assert.assertEquals(molecule.getAtom(1), chirality.getChiralAtom());
        for (int i=0; i<ligands.length; i++) {
            Assert.assertEquals(ligands[i], chirality.getLigands()[i]);
        }
        Assert.assertEquals(TetrahedralChirality.Stereo.CLOCKWISE, chirality.getStereo());
    }

    @Test
    public void testGetLigands() {
        TetrahedralChirality chirality = new TetrahedralChirality(
            molecule.getAtom(1), ligands, Stereo.CLOCKWISE
        );
        Assert.assertNotNull(chirality);
        for (int i=0; i<ligands.length; i++) {
            Assert.assertEquals(ligands[i], chirality.getLigands()[i]);
        }
    }

    @Test
    public void testToString() {
        TetrahedralChirality chirality = new TetrahedralChirality(
            molecule.getAtom(1), ligands, Stereo.CLOCKWISE
        );
        String stringRepr = chirality.toString();
        Assert.assertNotSame(0, stringRepr.length());
        Assert.assertFalse(stringRepr.contains("\n"));
    }
}


