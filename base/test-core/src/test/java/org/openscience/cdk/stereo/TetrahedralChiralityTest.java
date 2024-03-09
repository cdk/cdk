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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;

/**
 * @cdk.module test-core
 */
class TetrahedralChiralityTest extends CDKTestCase {

    private static IAtomContainer molecule;
    private static IAtom[]        ligands;

    @BeforeAll
    static void setup() throws Exception {
        molecule = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        molecule.addAtom(new Atom("Cl"));
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("Br"));
        molecule.addAtom(new Atom("I"));
        molecule.addAtom(new Atom("H"));
        molecule.addBond(0, 1, Order.SINGLE);
        molecule.addBond(1, 2, Order.SINGLE);
        molecule.addBond(1, 3, Order.SINGLE);
        molecule.addBond(1, 4, Order.SINGLE);
        ligands = new IAtom[]{molecule.getAtom(4), molecule.getAtom(3), molecule.getAtom(2), molecule.getAtom(0)};
    }

    @Test
    void testTetrahedralChirality_IAtom_arrayIAtom_ITetrahedralChirality_Stereo() {
        TetrahedralChirality chirality = new TetrahedralChirality(molecule.getAtom(1), ligands, Stereo.CLOCKWISE);
        Assertions.assertNotNull(chirality);
    }

    @Test
    void testBuilder() {
        TetrahedralChirality chirality = new TetrahedralChirality(molecule.getAtom(1), ligands, Stereo.CLOCKWISE);
        chirality.setBuilder(DefaultChemObjectBuilder.getInstance());
        Assertions.assertEquals(DefaultChemObjectBuilder.getInstance(), chirality.getBuilder());
    }

    @Test
    void testGetChiralAtom() {
        TetrahedralChirality chirality = new TetrahedralChirality(molecule.getAtom(1), ligands, Stereo.CLOCKWISE);
        Assertions.assertNotNull(chirality);
        Assertions.assertEquals(molecule.getAtom(1), chirality.getChiralAtom());
    }

    @Test
    void testGetStereo() {
        TetrahedralChirality chirality = new TetrahedralChirality(molecule.getAtom(1), ligands, Stereo.CLOCKWISE);
        Assertions.assertNotNull(chirality);
        Assertions.assertEquals(molecule.getAtom(1), chirality.getChiralAtom());
        for (int i = 0; i < ligands.length; i++) {
            Assertions.assertEquals(ligands[i], chirality.getLigands()[i]);
        }
        Assertions.assertEquals(Stereo.CLOCKWISE, chirality.getStereo());
    }

    @Test
    void testGetLigands() {
        TetrahedralChirality chirality = new TetrahedralChirality(molecule.getAtom(1), ligands, Stereo.CLOCKWISE);
        Assertions.assertNotNull(chirality);
        for (int i = 0; i < ligands.length; i++) {
            Assertions.assertEquals(ligands[i], chirality.getLigands()[i]);
        }
    }

    @Test
    void testMap_Map_Map() throws CloneNotSupportedException {

        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();

        IAtom c1 = builder.newInstance(IAtom.class, "C");
        IAtom o2 = builder.newInstance(IAtom.class, "O");
        IAtom n3 = builder.newInstance(IAtom.class, "N");
        IAtom c4 = builder.newInstance(IAtom.class, "C");
        IAtom h5 = builder.newInstance(IAtom.class, "H");

        // new stereo element
        ITetrahedralChirality original = new TetrahedralChirality(c1, new IAtom[]{o2, n3, c4, h5}, Stereo.CLOCKWISE);

        // clone the atoms and place in a map
        Map<IAtom, IAtom> mapping = new HashMap<>();
        IAtom c1clone = c1.clone();
        mapping.put(c1, c1clone);
        IAtom o2clone = o2.clone();
        mapping.put(o2, o2clone);
        IAtom n3clone = n3.clone();
        mapping.put(n3, n3clone);
        IAtom c4clone = c4.clone();
        mapping.put(c4, c4clone);
        IAtom h5clone = h5.clone();
        mapping.put(h5, h5clone);

        // map the existing element a new element
        ITetrahedralChirality mapped = original.map(mapping, Collections.EMPTY_MAP);

        org.hamcrest.MatcherAssert.assertThat("mapped chiral atom was the same as the original", mapped.getChiralAtom(),
                is(not(sameInstance(original.getChiralAtom()))));
        org.hamcrest.MatcherAssert.assertThat("mapped chiral atom was not the clone", mapped.getChiralAtom(), is(sameInstance(c1clone)));

        IAtom[] originalLigands = original.getLigands();
        IAtom[] mappedLigands = mapped.getLigands();

        org.hamcrest.MatcherAssert.assertThat("first ligand was te same as the original", mappedLigands[0],
                is(not(sameInstance(originalLigands[0]))));
        org.hamcrest.MatcherAssert.assertThat("first mapped ligand was not the clone", mappedLigands[0], is(sameInstance(o2clone)));
        org.hamcrest.MatcherAssert.assertThat("second ligand was te same as the original", mappedLigands[1],
                is(not(sameInstance(originalLigands[1]))));
        org.hamcrest.MatcherAssert.assertThat("second mapped ligand was not the clone", mappedLigands[1], is(sameInstance(n3clone)));
        org.hamcrest.MatcherAssert.assertThat("third ligand was te same as the original", mappedLigands[2],
                is(not(sameInstance(originalLigands[2]))));
        org.hamcrest.MatcherAssert.assertThat("third mapped ligand was not the clone", mappedLigands[2], is(sameInstance(c4clone)));
        org.hamcrest.MatcherAssert.assertThat("forth ligand was te same as the original", mappedLigands[3],
                is(not(sameInstance(originalLigands[3]))));
        org.hamcrest.MatcherAssert.assertThat("forth mapped ligand was not the clone", mappedLigands[3], is(sameInstance(h5clone)));

        org.hamcrest.MatcherAssert.assertThat("stereo was not mapped", mapped.getStereo(), is(original.getStereo()));

    }

    @Test
    void testMap_Null_Map() throws CloneNotSupportedException {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> {
                                    IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();

                                    IAtom c1 = builder.newInstance(IAtom.class, "C");
                                    IAtom o2 = builder.newInstance(IAtom.class, "O");
                                    IAtom n3 = builder.newInstance(IAtom.class, "N");
                                    IAtom c4 = builder.newInstance(IAtom.class, "C");
                                    IAtom h5 = builder.newInstance(IAtom.class, "H");

                                    // new stereo element
                                    ITetrahedralChirality original = new TetrahedralChirality(c1, new IAtom[]{o2, n3, c4, h5}, Stereo.CLOCKWISE);

                                    // map the existing element a new element - should through an IllegalArgumentException
                                    ITetrahedralChirality mapped = original.map(null, Collections.EMPTY_MAP);
                                });
    }

    @Test
    void testMap_Map_Map_EmptyMapping() throws CloneNotSupportedException {

        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();

        IAtom c1 = builder.newInstance(IAtom.class, "C");
        IAtom o2 = builder.newInstance(IAtom.class, "O");
        IAtom n3 = builder.newInstance(IAtom.class, "N");
        IAtom c4 = builder.newInstance(IAtom.class, "C");
        IAtom h5 = builder.newInstance(IAtom.class, "H");

        // new stereo element
        ITetrahedralChirality original = new TetrahedralChirality(c1, new IAtom[]{o2, n3, c4, h5}, Stereo.CLOCKWISE);

        // map the existing element a new element - should through an IllegalArgumentException
        ITetrahedralChirality mapped = original.map(Collections.EMPTY_MAP, Collections.EMPTY_MAP);

        org.hamcrest.MatcherAssert.assertThat(mapped, is(sameInstance(original)));
    }

    @Test
    void contains() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();

        IAtom c1 = builder.newInstance(IAtom.class, "C");
        IAtom o2 = builder.newInstance(IAtom.class, "O");
        IAtom n3 = builder.newInstance(IAtom.class, "N");
        IAtom c4 = builder.newInstance(IAtom.class, "C");
        IAtom h5 = builder.newInstance(IAtom.class, "H");

        // new stereo element
        ITetrahedralChirality element = new TetrahedralChirality(c1, new IAtom[]{o2, n3, c4, h5}, Stereo.CLOCKWISE);

        Assertions.assertTrue(element.contains(c1));
        Assertions.assertTrue(element.contains(o2));
        Assertions.assertTrue(element.contains(n3));
        Assertions.assertTrue(element.contains(c4));
        Assertions.assertTrue(element.contains(h5));

        Assertions.assertFalse(element.contains(builder.newInstance(IAtom.class)));
        Assertions.assertFalse(element.contains(null));
    }

    @Test
    void testToString() {
        TetrahedralChirality chirality = new TetrahedralChirality(molecule.getAtom(1), ligands, Stereo.CLOCKWISE);
        String stringRepr = chirality.toString();
        Assertions.assertNotSame(0, stringRepr.length());
        Assertions.assertFalse(stringRepr.contains("\n"));
    }
}
