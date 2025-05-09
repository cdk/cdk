/*
 * Copyright (c) 2013. John May <jwmay@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.hash;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author John May
 * @author Uli Fechner
 */
class BasicMoleculeHashGeneratorTest {

    @Test
    void testConstruct_Null() {
        Assertions.assertThrows(NullPointerException.class,
                                () -> {
                                    new BasicMoleculeHashGenerator(null);
                                });
    }

    @Test
    void testConstruct_NullPRNG() {
        Assertions.assertThrows(NullPointerException.class,
                                () -> {
                                    new BasicMoleculeHashGenerator(mock(AtomHashGenerator.class), null);
                                });
    }

    @Test
    void testGenerate() {

        AtomHashGenerator atomGenerator = mock(AtomHashGenerator.class);
        Pseudorandom prng = mock(Pseudorandom.class);
        IAtomContainer container = mock(IAtomContainer.class);

        MoleculeHashGenerator generator = new BasicMoleculeHashGenerator(atomGenerator, prng);

        when(atomGenerator.generate(container)).thenReturn(new long[]{1, 1, 1, 1});
        when(prng.next(1L)).thenReturn(1L);

        long hashCode = generator.generate(container);

        verify(atomGenerator, times(1)).generate(container);
        verify(prng, times(3)).next(1L);

        verifyNoMoreInteractions(atomGenerator, container, prng);

        long expected = 2147483647L ^ 1L ^ 1L ^ 1L ^ 1L;

        assertThat(hashCode, is(expected));

    }

    @Test
    void testGenerate_Rotation() {

        AtomHashGenerator atomGenerator = mock(AtomHashGenerator.class);
        Xorshift xorshift = new Xorshift();
        IAtomContainer container = mock(IAtomContainer.class);

        MoleculeHashGenerator generator = new BasicMoleculeHashGenerator(atomGenerator, new Xorshift());

        when(atomGenerator.generate(container)).thenReturn(new long[]{5L, 5L, 5L, 5L});

        long hashCode = generator.generate(container);

        verify(atomGenerator, times(1)).generate(container);

        verifyNoMoreInteractions(atomGenerator, container);

        long expected = 2147483647L ^ 5L ^ xorshift.next(5L) ^ xorshift.next(xorshift.next(5L))
                ^ xorshift.next(xorshift.next(xorshift.next(5L)));

        assertThat(hashCode, is(expected));

    }

    // The following test cases assert that the hashes of arbitrarily selected molecules
    // are equal to an expected value. This makes it more likely to automatically pick up
    // any unintentional changes to the computation of the hash values.
    @Test
    void testGenerate_bicyclohexyl() throws IOException {
        // arrange
        IAtomContainer atomContainer = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom atom1 = new Atom(6);
        IAtom atom2 = new Atom(6);
        IAtom atom3 = new Atom(6);
        IAtom atom4 = new Atom(6);
        IAtom atom5 = new Atom(6);
        IAtom atom6 = new Atom(6);
        IAtom atom7 = new Atom(6);
        IAtom atom8 = new Atom(6);
        IAtom atom9 = new Atom(6);
        IAtom atom10 = new Atom(6);
        IAtom atom11 = new Atom(6);
        IAtom atom12 = new Atom(6);
        IBond bond1 = new Bond(atom1, atom2, IBond.Order.SINGLE);
        IBond bond2 = new Bond(atom2, atom3, IBond.Order.SINGLE);
        IBond bond3 = new Bond(atom3, atom4, IBond.Order.SINGLE);
        IBond bond4 = new Bond(atom4, atom5, IBond.Order.SINGLE);
        IBond bond5 = new Bond(atom5, atom6, IBond.Order.SINGLE);
        IBond bond6 = new Bond(atom6, atom1, IBond.Order.SINGLE);
        IBond bond7 = new Bond(atom1, atom7, IBond.Order.SINGLE);
        IBond bond8 = new Bond(atom7, atom8, IBond.Order.SINGLE);
        IBond bond9 = new Bond(atom8, atom9, IBond.Order.SINGLE);
        IBond bond10 = new Bond(atom9, atom10, IBond.Order.SINGLE);
        IBond bond11 = new Bond(atom10, atom11, IBond.Order.SINGLE);
        IBond bond12 = new Bond(atom11, atom12, IBond.Order.SINGLE);
        IBond bond13 = new Bond(atom12, atom7, IBond.Order.SINGLE);
        atomContainer.addAtom(atom1);
        atomContainer.addAtom(atom2);
        atomContainer.addAtom(atom3);
        atomContainer.addAtom(atom4);
        atomContainer.addAtom(atom5);
        atomContainer.addAtom(atom6);
        atomContainer.addAtom(atom7);
        atomContainer.addAtom(atom8);
        atomContainer.addAtom(atom9);
        atomContainer.addAtom(atom10);
        atomContainer.addAtom(atom11);
        atomContainer.addAtom(atom12);
        atomContainer.addBond(bond1);
        atomContainer.addBond(bond2);
        atomContainer.addBond(bond3);
        atomContainer.addBond(bond4);
        atomContainer.addBond(bond5);
        atomContainer.addBond(bond6);
        atomContainer.addBond(bond7);
        atomContainer.addBond(bond8);
        atomContainer.addBond(bond9);
        atomContainer.addBond(bond10);
        atomContainer.addBond(bond11);
        atomContainer.addBond(bond12);
        atomContainer.addBond(bond13);

        Isotopes.getInstance().configureAtoms(atomContainer);
        MoleculeHashGenerator generator = new HashGeneratorMaker().depth(12).elemental().isotopic().orbital().charged().chiral().molecular();

        // act & assert
        assertThat(generator.generate(atomContainer), is(-3175716729819999889L));
    }

    @Test
    void testGenerate_bicyclopentadienyl() throws IOException {
        // arrange
        IAtomContainer atomContainer = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom atom1 = new Atom(6);
        IAtom atom2 = new Atom(6);
        IAtom atom3 = new Atom(6);
        IAtom atom4 = new Atom(6);
        IAtom atom5 = new Atom(6);
        IAtom atom6 = new Atom(6);
        IAtom atom7 = new Atom(6);
        IAtom atom8 = new Atom(6);
        IAtom atom9 = new Atom(6);
        IAtom atom10 = new Atom(6);
        IBond bond1 = new Bond(atom1, atom2, IBond.Order.SINGLE);
        IBond bond2 = new Bond(atom2, atom3, IBond.Order.SINGLE);
        IBond bond3 = new Bond(atom3, atom4, IBond.Order.DOUBLE);
        IBond bond4 = new Bond(atom4, atom5, IBond.Order.SINGLE);
        IBond bond5 = new Bond(atom5, atom1, IBond.Order.DOUBLE);
        IBond bond6 = new Bond(atom1, atom6, IBond.Order.SINGLE);
        IBond bond7 = new Bond(atom6, atom7, IBond.Order.DOUBLE);
        IBond bond8 = new Bond(atom7, atom8, IBond.Order.SINGLE);
        IBond bond9 = new Bond(atom8, atom9, IBond.Order.DOUBLE);
        IBond bond10 = new Bond(atom9, atom10, IBond.Order.SINGLE);
        IBond bond11 = new Bond(atom10, atom6, IBond.Order.SINGLE);
        atomContainer.addAtom(atom1);
        atomContainer.addAtom(atom2);
        atomContainer.addAtom(atom3);
        atomContainer.addAtom(atom4);
        atomContainer.addAtom(atom5);
        atomContainer.addAtom(atom6);
        atomContainer.addAtom(atom7);
        atomContainer.addAtom(atom8);
        atomContainer.addAtom(atom9);
        atomContainer.addAtom(atom10);
        atomContainer.addBond(bond1);
        atomContainer.addBond(bond2);
        atomContainer.addBond(bond3);
        atomContainer.addBond(bond4);
        atomContainer.addBond(bond5);
        atomContainer.addBond(bond6);
        atomContainer.addBond(bond7);
        atomContainer.addBond(bond8);
        atomContainer.addBond(bond9);
        atomContainer.addBond(bond10);
        atomContainer.addBond(bond11);

        Isotopes.getInstance().configureAtoms(atomContainer);
        MoleculeHashGenerator generator = new HashGeneratorMaker().depth(12).elemental().isotopic().orbital().charged().chiral().molecular();

        // act & assert
        assertThat(generator.generate(atomContainer), is(-6031161513447867509L));
    }

    @Test
    void testGenerate_molecule1() throws IOException {
        // arrange
        IAtomContainer atomContainer = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom atom1 = new Atom(7);
        IAtom atom2 = new Atom(6);
        IAtom atom3 = new Atom(6);
        IAtom atom4 = new Atom(6);
        IAtom atom5 = new Atom(53);
        IAtom atom6 = new Atom(6);
        IAtom atom7 = new Atom(6);
        IAtom atom8 = new Atom(6);
        IAtom atom9 = new Atom(6);
        IAtom atom10 = new Atom(6);
        IAtom atom11 = new Atom(6);
        IAtom atom12 = new Atom(53);
        IAtom atom13 = new Atom(6);
        IAtom atom14 = new Atom(6);
        IBond bond1 = new Bond(atom1, atom2, IBond.Order.SINGLE);
        IBond bond2 = new Bond(atom2, atom3, IBond.Order.SINGLE);
        IBond bond3 = new Bond(atom3, atom4, IBond.Order.SINGLE);
        IBond bond4 = new Bond(atom4, atom5, IBond.Order.SINGLE);
        IBond bond5 = new Bond(atom4, atom6, IBond.Order.SINGLE);
        IBond bond6 = new Bond(atom6, atom7, IBond.Order.SINGLE);
        IBond bond7 = new Bond(atom7, atom1, IBond.Order.SINGLE);
        IBond bond8 = new Bond(atom1, atom8, IBond.Order.SINGLE);
        IBond bond9 = new Bond(atom8, atom9, IBond.Order.SINGLE);
        IBond bond10 = new Bond(atom9, atom10, IBond.Order.DOUBLE);
        IBond bond11 = new Bond(atom10, atom11, IBond.Order.SINGLE);
        IBond bond12 = new Bond(atom11, atom12, IBond.Order.SINGLE);
        IBond bond13 = new Bond(atom11, atom13, IBond.Order.DOUBLE);
        IBond bond14 = new Bond(atom13, atom14, IBond.Order.SINGLE);
        IBond bond15 = new Bond(atom14, atom8, IBond.Order.DOUBLE);
        atomContainer.addAtom(atom1);
        atomContainer.addAtom(atom2);
        atomContainer.addAtom(atom3);
        atomContainer.addAtom(atom4);
        atomContainer.addAtom(atom5);
        atomContainer.addAtom(atom6);
        atomContainer.addAtom(atom7);
        atomContainer.addAtom(atom8);
        atomContainer.addAtom(atom9);
        atomContainer.addAtom(atom10);
        atomContainer.addAtom(atom11);
        atomContainer.addAtom(atom12);
        atomContainer.addAtom(atom13);
        atomContainer.addAtom(atom14);
        atomContainer.addBond(bond1);
        atomContainer.addBond(bond2);
        atomContainer.addBond(bond3);
        atomContainer.addBond(bond4);
        atomContainer.addBond(bond5);
        atomContainer.addBond(bond6);
        atomContainer.addBond(bond7);
        atomContainer.addBond(bond8);
        atomContainer.addBond(bond9);
        atomContainer.addBond(bond10);
        atomContainer.addBond(bond11);
        atomContainer.addBond(bond12);
        atomContainer.addBond(bond13);
        atomContainer.addBond(bond14);
        atomContainer.addBond(bond15);

        Isotopes.getInstance().configureAtoms(atomContainer);
        MoleculeHashGenerator generator = new HashGeneratorMaker().depth(12).elemental().isotopic().orbital().charged().chiral().molecular();

        // act & assert
        assertThat(generator.generate(atomContainer), is(3048684266681880198L));
    }

    @Test
    void testGenerate_molecule2() throws IOException {
        // arrange
        IAtomContainer atomContainer = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom atom1 = new Atom(7);
        IAtom atom2 = new Atom(6);
        IAtom atom3 = new Atom(6);
        IAtom atom4 = new Atom(6);
        IAtom atom5 = new Atom(6);
        IAtom atom6 = new Atom(6);
        IAtom atom7 = new Atom(6);
        IAtom atom8 = new Atom(6);
        IAtom atom9 = new Atom(9);
        IAtom atom10 = new Atom(6);
        IAtom atom11 = new Atom(9);
        IAtom atom12 = new Atom(6);
        IAtom atom13 = new Atom(53);
        IAtom atom14 = new Atom(6);
        IAtom atom15 = new Atom(9);
        IAtom atom16 = new Atom(6);
        IAtom atom17 = new Atom(9);
        IBond bond1 = new Bond(atom1, atom2, IBond.Order.SINGLE);
        IBond bond2 = new Bond(atom2, atom3, IBond.Order.SINGLE);
        IBond bond3 = new Bond(atom3, atom4, IBond.Order.SINGLE);
        IBond bond4 = new Bond(atom4, atom5, IBond.Order.SINGLE);
        IBond bond5 = new Bond(atom5, atom6, IBond.Order.SINGLE);
        IBond bond6 = new Bond(atom6, atom1, IBond.Order.SINGLE);
        IBond bond7 = new Bond(atom1, atom7, IBond.Order.SINGLE);
        IBond bond8 = new Bond(atom7, atom8, IBond.Order.SINGLE);
        IBond bond9 = new Bond(atom8, atom9, IBond.Order.SINGLE);
        IBond bond10 = new Bond(atom8, atom10, IBond.Order.DOUBLE);
        IBond bond11 = new Bond(atom10, atom11, IBond.Order.SINGLE);
        IBond bond12 = new Bond(atom10, atom12, IBond.Order.SINGLE);
        IBond bond13 = new Bond(atom12, atom13, IBond.Order.SINGLE);
        IBond bond14 = new Bond(atom12, atom14, IBond.Order.DOUBLE);
        IBond bond15 = new Bond(atom14, atom15, IBond.Order.SINGLE);
        IBond bond16 = new Bond(atom14, atom16, IBond.Order.SINGLE);
        IBond bond17 = new Bond(atom16, atom17, IBond.Order.SINGLE);
        IBond bond18 = new Bond(atom16, atom7, IBond.Order.DOUBLE);
        atomContainer.addAtom(atom1);
        atomContainer.addAtom(atom2);
        atomContainer.addAtom(atom3);
        atomContainer.addAtom(atom4);
        atomContainer.addAtom(atom5);
        atomContainer.addAtom(atom6);
        atomContainer.addAtom(atom7);
        atomContainer.addAtom(atom8);
        atomContainer.addAtom(atom9);
        atomContainer.addAtom(atom10);
        atomContainer.addAtom(atom11);
        atomContainer.addAtom(atom12);
        atomContainer.addAtom(atom13);
        atomContainer.addAtom(atom14);
        atomContainer.addAtom(atom15);
        atomContainer.addAtom(atom16);
        atomContainer.addAtom(atom17);
        atomContainer.addBond(bond1);
        atomContainer.addBond(bond2);
        atomContainer.addBond(bond3);
        atomContainer.addBond(bond4);
        atomContainer.addBond(bond5);
        atomContainer.addBond(bond6);
        atomContainer.addBond(bond7);
        atomContainer.addBond(bond8);
        atomContainer.addBond(bond9);
        atomContainer.addBond(bond10);
        atomContainer.addBond(bond11);
        atomContainer.addBond(bond12);
        atomContainer.addBond(bond13);
        atomContainer.addBond(bond14);
        atomContainer.addBond(bond15);
        atomContainer.addBond(bond16);
        atomContainer.addBond(bond17);
        atomContainer.addBond(bond18);

        Isotopes.getInstance().configureAtoms(atomContainer);
        MoleculeHashGenerator generator = new HashGeneratorMaker().depth(12).elemental().isotopic().orbital().charged().chiral().molecular();

        // act & assert
        assertThat(generator.generate(atomContainer), is(-8993185380539911649L));
    }

    @Test
    void testGenerate_molecule3() throws IOException {
        // arrange
        IAtomContainer atomContainer = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom atom1 = new Atom(7);
        IAtom atom2 = new Atom(6);
        IAtom atom3 = new Atom(6);
        IAtom atom4 = new Atom(6);
        IAtom atom5 = new Atom(6);
        IAtom atom6 = new Atom(6);
        IAtom atom7 = new Atom(6);
        IAtom atom8 = new Atom(6);
        IAtom atom9 = new Atom(35);
        IAtom atom10 = new Atom(6);
        IAtom atom11 = new Atom(6);
        IAtom atom12 = new Atom(17);
        IAtom atom13 = new Atom(16);
        IBond bond1 = new Bond(atom1, atom2, IBond.Order.SINGLE);
        IBond bond2 = new Bond(atom2, atom3, IBond.Order.SINGLE);
        IBond bond3 = new Bond(atom3, atom4, IBond.Order.SINGLE);
        IBond bond4 = new Bond(atom4, atom5, IBond.Order.SINGLE);
        IBond bond5 = new Bond(atom5, atom6, IBond.Order.SINGLE);
        IBond bond6 = new Bond(atom6, atom1, IBond.Order.SINGLE);
        IBond bond7 = new Bond(atom1, atom7, IBond.Order.SINGLE);
        IBond bond8 = new Bond(atom7, atom8, IBond.Order.DOUBLE);
        IBond bond9 = new Bond(atom8, atom9, IBond.Order.SINGLE);
        IBond bond10 = new Bond(atom8, atom10, IBond.Order.SINGLE);
        IBond bond11 = new Bond(atom10, atom11, IBond.Order.DOUBLE);
        IBond bond12 = new Bond(atom11, atom12, IBond.Order.SINGLE);
        IBond bond13 = new Bond(atom11, atom13, IBond.Order.SINGLE);
        IBond bond14 = new Bond(atom13, atom7, IBond.Order.SINGLE);
        atomContainer.addAtom(atom1);
        atomContainer.addAtom(atom2);
        atomContainer.addAtom(atom3);
        atomContainer.addAtom(atom4);
        atomContainer.addAtom(atom5);
        atomContainer.addAtom(atom6);
        atomContainer.addAtom(atom7);
        atomContainer.addAtom(atom8);
        atomContainer.addAtom(atom9);
        atomContainer.addAtom(atom10);
        atomContainer.addAtom(atom11);
        atomContainer.addAtom(atom12);
        atomContainer.addAtom(atom13);
        atomContainer.addBond(bond1);
        atomContainer.addBond(bond2);
        atomContainer.addBond(bond3);
        atomContainer.addBond(bond4);
        atomContainer.addBond(bond5);
        atomContainer.addBond(bond6);
        atomContainer.addBond(bond7);
        atomContainer.addBond(bond8);
        atomContainer.addBond(bond9);
        atomContainer.addBond(bond10);
        atomContainer.addBond(bond11);
        atomContainer.addBond(bond12);
        atomContainer.addBond(bond13);
        atomContainer.addBond(bond14);

        Isotopes.getInstance().configureAtoms(atomContainer);
        MoleculeHashGenerator generator = new HashGeneratorMaker().depth(12).elemental().isotopic().orbital().charged().chiral().molecular();

        // act & assert
        assertThat(generator.generate(atomContainer), is(-6491860395059540140L));
    }

    @Test
    void testGenerate_molecule4() throws IOException {
        // arrange
        IAtomContainer atomContainer = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom atom1 = new Atom(17);
        IAtom atom2 = new Atom(6);
        IAtom atom3 = new Atom(7);
        atom3.setCharge(+1.0);
        IAtom atom4 = new Atom(6);
        IAtom atom5 = new Atom(6);
        IAtom atom6 = new Atom(7);
        atom6.setCharge(+1.0);
        IAtom atom7 = new Atom(6);
        IAtom atom8 = new Atom(6);
        IAtom atom9 = new Atom(6);
        IAtom atom10 = new Atom(6);
        IAtom atom11 = new Atom(6);
        IAtom atom12 = new Atom(6);
        IAtom atom13 = new Atom(6);
        IAtom atom14 = new Atom(9);
        IAtom atom15 = new Atom(6);
        IAtom atom16 = new Atom(8);
        IAtom atom17 = new Atom(6);
        IAtom atom18 = new Atom(6);
        IAtom atom19 = new Atom(6);
        IBond bond1 = new Bond(atom1, atom2, IBond.Order.SINGLE);
        IBond bond2 = new Bond(atom2, atom3, IBond.Order.SINGLE);
        IBond bond3 = new Bond(atom3, atom4, IBond.Order.SINGLE);
        IBond bond4 = new Bond(atom4, atom5, IBond.Order.SINGLE);
        IBond bond5 = new Bond(atom5, atom6, IBond.Order.SINGLE);
        IBond bond6 = new Bond(atom6, atom7, IBond.Order.SINGLE);
        IBond bond7 = new Bond(atom7, atom8, IBond.Order.SINGLE);
        IBond bond8 = new Bond(atom8, atom3, IBond.Order.SINGLE);
        IBond bond9 = new Bond(atom3, atom9, IBond.Order.SINGLE);
        IBond bond10 = new Bond(atom9, atom10, IBond.Order.SINGLE);
        IBond bond11 = new Bond(atom10, atom6, IBond.Order.SINGLE);
        IBond bond12 = new Bond(atom6, atom11, IBond.Order.SINGLE);
        IBond bond13 = new Bond(atom11, atom12, IBond.Order.DOUBLE);
        IBond bond14 = new Bond(atom12, atom13, IBond.Order.SINGLE);
        IBond bond15 = new Bond(atom13, atom14, IBond.Order.SINGLE);
        IBond bond16 = new Bond(atom13, atom15, IBond.Order.DOUBLE);
        IBond bond17 = new Bond(atom15, atom16, IBond.Order.SINGLE);
        IBond bond18 = new Bond(atom16, atom17, IBond.Order.SINGLE);
        IBond bond19 = new Bond(atom15, atom18, IBond.Order.SINGLE);
        IBond bond20 = new Bond(atom18, atom19, IBond.Order.DOUBLE);
        IBond bond21 = new Bond(atom19, atom11, IBond.Order.SINGLE);
        atomContainer.addAtom(atom1);
        atomContainer.addAtom(atom2);
        atomContainer.addAtom(atom3);
        atomContainer.addAtom(atom4);
        atomContainer.addAtom(atom5);
        atomContainer.addAtom(atom6);
        atomContainer.addAtom(atom7);
        atomContainer.addAtom(atom8);
        atomContainer.addAtom(atom9);
        atomContainer.addAtom(atom10);
        atomContainer.addAtom(atom11);
        atomContainer.addAtom(atom12);
        atomContainer.addAtom(atom13);
        atomContainer.addAtom(atom14);
        atomContainer.addAtom(atom15);
        atomContainer.addAtom(atom16);
        atomContainer.addAtom(atom17);
        atomContainer.addAtom(atom18);
        atomContainer.addAtom(atom19);
        atomContainer.addBond(bond1);
        atomContainer.addBond(bond2);
        atomContainer.addBond(bond3);
        atomContainer.addBond(bond4);
        atomContainer.addBond(bond5);
        atomContainer.addBond(bond6);
        atomContainer.addBond(bond7);
        atomContainer.addBond(bond8);
        atomContainer.addBond(bond9);
        atomContainer.addBond(bond10);
        atomContainer.addBond(bond11);
        atomContainer.addBond(bond12);
        atomContainer.addBond(bond13);
        atomContainer.addBond(bond14);
        atomContainer.addBond(bond15);
        atomContainer.addBond(bond16);
        atomContainer.addBond(bond17);
        atomContainer.addBond(bond18);
        atomContainer.addBond(bond19);
        atomContainer.addBond(bond20);
        atomContainer.addBond(bond21);

        Isotopes.getInstance().configureAtoms(atomContainer);
        MoleculeHashGenerator generator = new HashGeneratorMaker().depth(12).elemental().isotopic().orbital().charged().chiral().molecular();

        // act & assert
        assertThat(generator.generate(atomContainer), is(3013689150770495490L));
    }
}
