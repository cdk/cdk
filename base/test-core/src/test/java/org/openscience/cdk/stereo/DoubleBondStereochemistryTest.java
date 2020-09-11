/* Copyright (C) 2012  Egon Willighagen <egonw@users.sf.net>
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
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry;
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry.Conformation;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @cdk.module test-core
 */
public class DoubleBondStereochemistryTest extends CDKTestCase {

    private static IAtomContainer molecule;
    private static IBond[]        ligands;

    /**
     * This method creates <i>E</i>-but-2-ene.
     */
    @BeforeClass
    public static void setup() throws Exception {
        molecule = new AtomContainer();
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("C"));
        molecule.addBond(0, 1, Order.SINGLE);
        molecule.addBond(1, 2, Order.DOUBLE);
        molecule.addBond(2, 3, Order.SINGLE);
        ligands = new IBond[]{molecule.getBond(0), molecule.getBond(2)};
    }

    /**
     * Unit test ensures an exception is thrown if more the two elements are
     * passed to the constructor. When IDoubleBondStereoChemistry.getBonds()
     * is invoked the fixed size array is copied to an array of size 2. If
     * more then 2 bonds are given they would be truncated.
     *
     * @cdk.bug 1273
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_TooManyBonds() {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IBond b1 = builder.newBond();
        IBond b2 = builder.newBond();
        IBond b3 = builder.newBond();
        new DoubleBondStereochemistry(builder.newInstance(IBond.class), new IBond[]{b1,b2,b3}, Conformation.OPPOSITE);
    }

    @Test
    public void testConstructor() {
        DoubleBondStereochemistry stereo = new DoubleBondStereochemistry(molecule.getBond(1), ligands,
                Conformation.OPPOSITE);
        Assert.assertNotNull(stereo);
    }

    @Test
    public void testBuilder() {
        DoubleBondStereochemistry stereo = new DoubleBondStereochemistry(molecule.getBond(1), ligands,
                Conformation.OPPOSITE);
        stereo.setBuilder(DefaultChemObjectBuilder.getInstance());
        Assert.assertEquals(DefaultChemObjectBuilder.getInstance(), stereo.getBuilder());
    }

    @Test
    public void testGetStereoBond() {
        DoubleBondStereochemistry stereo = new DoubleBondStereochemistry(molecule.getBond(1), ligands,
                Conformation.OPPOSITE);
        Assert.assertNotNull(stereo);
        Assert.assertEquals(molecule.getBond(1), stereo.getStereoBond());
    }

    @Test
    public void testGetStereo() {
        DoubleBondStereochemistry stereo = new DoubleBondStereochemistry(molecule.getBond(1), ligands,
                Conformation.OPPOSITE);
        Assert.assertNotNull(stereo);
        Assert.assertEquals(Conformation.OPPOSITE, stereo.getStereo());
    }

    @Test
    public void testGetBonds() {
        DoubleBondStereochemistry stereo = new DoubleBondStereochemistry(molecule.getBond(1), ligands,
                Conformation.OPPOSITE);
        Assert.assertNotNull(stereo);
        for (int i = 0; i < ligands.length; i++) {
            Assert.assertEquals(ligands[i], stereo.getBonds()[i]);
        }
    }

    @Test
    public void contains() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();

        IAtom c1 = builder.newInstance(IAtom.class, "C");
        IAtom c2 = builder.newInstance(IAtom.class, "C");
        IAtom o3 = builder.newInstance(IAtom.class, "O");
        IAtom o4 = builder.newInstance(IAtom.class, "O");

        IBond c1c2 = builder.newInstance(IBond.class, c1, c2, Order.DOUBLE);
        IBond c1o3 = builder.newInstance(IBond.class, c1, o3, Order.SINGLE);
        IBond c2o4 = builder.newInstance(IBond.class, c2, o4, Order.SINGLE);

        // new stereo element
        DoubleBondStereochemistry element = new DoubleBondStereochemistry(c1c2, new IBond[]{c1o3, c2o4},
                Conformation.OPPOSITE);

        assertTrue(element.contains(c1));
        assertTrue(element.contains(c2));
        assertTrue(element.contains(o3));
        assertTrue(element.contains(o4));

        assertFalse(element.contains(builder.newInstance(IAtom.class)));
        assertFalse(element.contains(null));
    }

    @Test
    public void testMap_Map_Map() throws CloneNotSupportedException {

        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();

        IAtom c1 = builder.newInstance(IAtom.class, "C");
        IAtom c2 = builder.newInstance(IAtom.class, "C");
        IAtom o3 = builder.newInstance(IAtom.class, "O");
        IAtom o4 = builder.newInstance(IAtom.class, "O");

        IBond c1c2 = builder.newInstance(IBond.class, c1, c2, Order.DOUBLE);
        IBond c1o3 = builder.newInstance(IBond.class, c1, o3, Order.SINGLE);
        IBond c2o4 = builder.newInstance(IBond.class, c2, o4, Order.SINGLE);

        // new stereo element
        DoubleBondStereochemistry original = new DoubleBondStereochemistry(c1c2, new IBond[]{c1o3, c2o4},
                Conformation.OPPOSITE);

        // clone the atoms and place in a map
        Map<IBond, IBond> mapping = new HashMap<IBond, IBond>();
        IBond c1c2clone = (IBond) c1c2.clone();
        mapping.put(c1c2, c1c2clone);
        IBond c1o3clone = (IBond) c1o3.clone();
        mapping.put(c1o3, c1o3clone);
        IBond c2o4clone = (IBond) c2o4.clone();
        mapping.put(c2o4, c2o4clone);

        // map the existing element a new element
        IDoubleBondStereochemistry mapped = original.map(Collections.EMPTY_MAP, mapping);

        org.hamcrest.MatcherAssert.assertThat("mapped chiral atom was the same as the original", mapped.getStereoBond(),
                is(not(sameInstance(original.getStereoBond()))));
        org.hamcrest.MatcherAssert.assertThat("mapped chiral atom was not the clone", mapped.getStereoBond(), is(sameInstance(c1c2clone)));

        IBond[] originalBonds = original.getBonds();
        IBond[] mappedBonds = mapped.getBonds();

        org.hamcrest.MatcherAssert.assertThat("first bond was te same as the original", mappedBonds[0],
                is(not(sameInstance(originalBonds[0]))));
        org.hamcrest.MatcherAssert.assertThat("first mapped bond was not the clone", mappedBonds[0], is(sameInstance(c1o3clone)));
        org.hamcrest.MatcherAssert.assertThat("second bond was te same as the original", mappedBonds[1],
                is(not(sameInstance(originalBonds[1]))));
        org.hamcrest.MatcherAssert.assertThat("second mapped bond was not the clone", mappedBonds[1], is(sameInstance(c2o4clone)));

        org.hamcrest.MatcherAssert.assertThat("stereo was not mapped", mapped.getStereo(), is(original.getStereo()));

    }

    @Test(expected = IllegalArgumentException.class)
    public void testMap_Null_Map() throws CloneNotSupportedException {

        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();

        IAtom c1 = builder.newInstance(IAtom.class, "C");
        IAtom c2 = builder.newInstance(IAtom.class, "C");
        IAtom o3 = builder.newInstance(IAtom.class, "O");
        IAtom o4 = builder.newInstance(IAtom.class, "O");

        IBond c1c2 = builder.newInstance(IBond.class, c1, c2, Order.DOUBLE);
        IBond c1o3 = builder.newInstance(IBond.class, c1, o3, Order.SINGLE);
        IBond c2o4 = builder.newInstance(IBond.class, c2, o4, Order.SINGLE);

        // new stereo element
        IDoubleBondStereochemistry original = new DoubleBondStereochemistry(c1c2, new IBond[]{c1o3, c2o4},
                Conformation.OPPOSITE);

        // map the existing element a new element - should through an IllegalArgumentException
        IDoubleBondStereochemistry mapped = original.map(Collections.EMPTY_MAP, null);
    }

    @Test
    public void testMap_Map_Map_EmptyMapping() throws CloneNotSupportedException {

        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();

        IAtom c1 = builder.newInstance(IAtom.class, "C");
        IAtom c2 = builder.newInstance(IAtom.class, "C");
        IAtom o3 = builder.newInstance(IAtom.class, "O");
        IAtom o4 = builder.newInstance(IAtom.class, "O");

        IBond c1c2 = builder.newInstance(IBond.class, c1, c2, Order.DOUBLE);
        IBond c1o3 = builder.newInstance(IBond.class, c1, o3, Order.SINGLE);
        IBond c2o4 = builder.newInstance(IBond.class, c2, o4, Order.SINGLE);

        // new stereo element
        IDoubleBondStereochemistry original = new DoubleBondStereochemistry(c1c2, new IBond[]{c1o3, c2o4},
                Conformation.OPPOSITE);

        // map the existing element a new element - should through an IllegalArgumentException
        IDoubleBondStereochemistry mapped = original.map(Collections.EMPTY_MAP, Collections.EMPTY_MAP);

        org.hamcrest.MatcherAssert.assertThat(mapped, is(sameInstance(original)));
    }

    @Test
    public void testToString() {
        DoubleBondStereochemistry stereo = new DoubleBondStereochemistry(molecule.getBond(1), ligands,
                Conformation.OPPOSITE);
        String stringRepr = stereo.toString();
        Assert.assertNotSame(0, stringRepr.length());
        assertFalse(stringRepr.contains("\n"));
    }
}
