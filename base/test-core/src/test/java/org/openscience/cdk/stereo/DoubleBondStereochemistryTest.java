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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry.Conformation;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;

class DoubleBondStereochemistryTest extends CDKTestCase {

    private static IAtomContainer molecule;
    private static IBond[]        ligands;

    /**
     * This method creates <i>E</i>-but-2-ene.
     */
    @BeforeAll
    static void setup() {
        molecule = DefaultChemObjectBuilder.getInstance().newAtomContainer();
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
    @Test
    void testConstructor_TooManyBonds() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> {
                                    IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
                                    IBond b1 = builder.newBond();
                                    IBond b2 = builder.newBond();
                                    IBond b3 = builder.newBond();
                                    new DoubleBondStereochemistry(builder.newInstance(IBond.class), new IBond[]{b1, b2, b3}, Conformation.OPPOSITE);
                                });
    }

    @Test
    void testConstructor() {
        DoubleBondStereochemistry stereo = new DoubleBondStereochemistry(molecule.getBond(1), ligands,
                Conformation.OPPOSITE);
        Assertions.assertNotNull(stereo);
    }

    @Test
    void testBuilder() {
        DoubleBondStereochemistry stereo = new DoubleBondStereochemistry(molecule.getBond(1), ligands,
                Conformation.OPPOSITE);
        stereo.setBuilder(DefaultChemObjectBuilder.getInstance());
        Assertions.assertEquals(DefaultChemObjectBuilder.getInstance(), stereo.getBuilder());
    }

    @Test
    void testGetStereoBond() {
        DoubleBondStereochemistry stereo = new DoubleBondStereochemistry(molecule.getBond(1), ligands,
                Conformation.OPPOSITE);
        Assertions.assertNotNull(stereo);
        Assertions.assertEquals(molecule.getBond(1), stereo.getStereoBond());
    }

    @Test
    void testGetStereo() {
        DoubleBondStereochemistry stereo = new DoubleBondStereochemistry(molecule.getBond(1), ligands,
                Conformation.OPPOSITE);
        Assertions.assertNotNull(stereo);
        Assertions.assertEquals(Conformation.OPPOSITE, stereo.getStereo());
    }

    @Test
    void testGetBonds() {
        DoubleBondStereochemistry stereo = new DoubleBondStereochemistry(molecule.getBond(1), ligands,
                Conformation.OPPOSITE);
        Assertions.assertNotNull(stereo);
        for (int i = 0; i < ligands.length; i++) {
            Assertions.assertEquals(ligands[i], stereo.getBonds()[i]);
        }
    }

    @Test
    void contains() {
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

        Assertions.assertTrue(element.contains(c1));
        Assertions.assertTrue(element.contains(c2));
        Assertions.assertTrue(element.contains(o3));
        Assertions.assertTrue(element.contains(o4));

        Assertions.assertFalse(element.contains(builder.newInstance(IAtom.class)));
        Assertions.assertFalse(element.contains(null));
    }

    @Test
    void testMap_Map_Map() throws CloneNotSupportedException {

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
        Map<IBond, IBond> mapping = new HashMap<>();
        IBond c1c2clone = c1c2.clone();
        mapping.put(c1c2, c1c2clone);
        IBond c1o3clone = c1o3.clone();
        mapping.put(c1o3, c1o3clone);
        IBond c2o4clone = c2o4.clone();
        mapping.put(c2o4, c2o4clone);

        // map the existing element a new element
        IDoubleBondStereochemistry mapped = original.map(Collections.<IAtom,IAtom>emptyMap(), mapping);

        org.hamcrest.MatcherAssert.assertThat("mapped chiral atom was the same as the original", mapped.getStereoBond(),
                is(not(sameInstance(original.getStereoBond()))));
        org.hamcrest.MatcherAssert.assertThat("mapped chiral atom was not the clone", mapped.getStereoBond(), is(sameInstance(c1c2clone)));

        IBond[] originalBonds = original.getBonds();
        IBond[] mappedBonds = mapped.getBonds();

        org.hamcrest.MatcherAssert.assertThat("first bond was the same as the original", mappedBonds[0],
                is(not(sameInstance(originalBonds[0]))));
        org.hamcrest.MatcherAssert.assertThat("first mapped bond was not the clone", mappedBonds[0], is(sameInstance(c2o4clone)));
        org.hamcrest.MatcherAssert.assertThat("second bond was the same as the original", mappedBonds[1],
                is(not(sameInstance(originalBonds[1]))));
        org.hamcrest.MatcherAssert.assertThat("second mapped bond was not the clone", mappedBonds[1], is(sameInstance(c1o3clone)));

        org.hamcrest.MatcherAssert.assertThat("stereo was not mapped", mapped.getStereo(), is(original.getStereo()));

    }

    @Test
    void testMap_Null_Map() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> {
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
                                    IDoubleBondStereochemistry mapped = original.map(Collections.<IAtom,IAtom>emptyMap(), null);
                                });
    }

    @Test
    void testMap_Map_Map_EmptyMapping() {

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
        IDoubleBondStereochemistry mapped = original.map(Collections.<IAtom,IAtom>emptyMap(), Collections.<IBond,IBond>emptyMap());

        org.hamcrest.MatcherAssert.assertThat(mapped, is(sameInstance(original)));
    }

    @Test
    void testToString() {
        DoubleBondStereochemistry stereo = new DoubleBondStereochemistry(molecule.getBond(1), ligands,
                Conformation.OPPOSITE);
        String stringRepr = stereo.toString();
        Assertions.assertNotSame(0, stringRepr.length());
        Assertions.assertFalse(stringRepr.contains("\n"));
    }

    @Test
    void testMapStrictCarrierSwapNecessary() throws CDKException {
        // arrange
        SmilesParser smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        final IAtomContainer molecule1 = smilesParser.parseSmiles("CCC\\C(Cl)=C(/C)Cl");
        final IAtomContainer molecule2 = smilesParser.parseSmiles("ClC(C)=C(Cl)CCC");

        Map<IChemObject,IChemObject> chemObjectMap = new HashMap<>();
        chemObjectMap.put(molecule1.getBond(2), molecule2.getBond(4)); // carrier
        chemObjectMap.put(molecule1.getBond(4), molecule2.getBond(2)); // focus
        chemObjectMap.put(molecule1.getBond(5), molecule2.getBond(1)); // carrier
        IStereoElement<?,?> stereoElement = molecule1.stereoElements().iterator().next();

        // act
        IStereoElement<?,?> newStereoElement = stereoElement.mapStrict(chemObjectMap);

        // assert
        DoubleBondStereochemistry newDoubleBondStereochemistry = (DoubleBondStereochemistry) newStereoElement;
        Assertions.assertTrue(
                newDoubleBondStereochemistry.getFocus().getBegin() == newDoubleBondStereochemistry.getCarriers().get(0).getBegin() ||
                        newDoubleBondStereochemistry.getFocus().getBegin() == newDoubleBondStereochemistry.getCarriers().get(0).getEnd()
        );
        Assertions.assertTrue(
                newDoubleBondStereochemistry.getFocus().getEnd() == newDoubleBondStereochemistry.getCarriers().get(1).getBegin() ||
                        newDoubleBondStereochemistry.getFocus().getEnd() == newDoubleBondStereochemistry.getCarriers().get(1).getEnd()
        );
    }

    @Test
    void testMapStrictCarrierSwapNotNecessary() throws CDKException {
        // arrange
        SmilesParser smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        final IAtomContainer molecule1 = smilesParser.parseSmiles("CCC\\C(Cl)=C(/C)Cl");
        final IAtomContainer molecule2 = smilesParser.parseSmiles("CCCC(Cl)=C(/C)Cl");

        Map<IChemObject,IChemObject> chemObjectMap = new HashMap<>();
        chemObjectMap.put(molecule1.getBond(2), molecule2.getBond(2)); // carrier
        chemObjectMap.put(molecule1.getBond(4), molecule2.getBond(4)); // focus
        chemObjectMap.put(molecule1.getBond(5), molecule2.getBond(5)); // carrier
        IStereoElement<?,?> stereoElement = molecule1.stereoElements().iterator().next();

        // act
        IStereoElement<?,?> newStereoElement = stereoElement.mapStrict(chemObjectMap);

        // assert
        DoubleBondStereochemistry newDoubleBondStereochemistry = (DoubleBondStereochemistry) newStereoElement;
        Assertions.assertTrue(
                newDoubleBondStereochemistry.getFocus().getBegin() == newDoubleBondStereochemistry.getCarriers().get(0).getBegin() ||
                        newDoubleBondStereochemistry.getFocus().getBegin() == newDoubleBondStereochemistry.getCarriers().get(0).getEnd()
        );
        Assertions.assertTrue(
                newDoubleBondStereochemistry.getFocus().getEnd() == newDoubleBondStereochemistry.getCarriers().get(1).getBegin() ||
                        newDoubleBondStereochemistry.getFocus().getEnd() == newDoubleBondStereochemistry.getCarriers().get(1).getEnd()
        );
    }

}
