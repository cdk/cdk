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

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.hash.stereo.StereoEncoderFactory;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openscience.cdk.interfaces.IBond.Order.SINGLE;

/**
 * @author John May
 * @cdk.module test-hash
 */
public class PerturbedAtomHashGeneratorTest {

    @Test
    public void testGenerate() throws Exception {

        IAtomContainer m1 = cyclopentylcyclopentane();
        IAtomContainer m2 = decahydronaphthalene();

        SeedGenerator seeding = new SeedGenerator(BasicAtomEncoder.ATOMIC_NUMBER);
        Pseudorandom pseudorandom = new Xorshift();

        MoleculeHashGenerator basic = new BasicMoleculeHashGenerator(new BasicAtomHashGenerator(seeding, pseudorandom,
                8));
        MoleculeHashGenerator perturb = new BasicMoleculeHashGenerator(new PerturbedAtomHashGenerator(seeding,
                new BasicAtomHashGenerator(seeding, pseudorandom, 8), pseudorandom, StereoEncoderFactory.EMPTY,
                new MinimumEquivalentCyclicSet(), AtomSuppression.unsuppressed()));
        // basic encoding should say these are the same
        assertThat(basic.generate(m1), is(basic.generate(m2)));

        // perturbed encoding should differentiate them
        assertThat(perturb.generate(m1), is(not(perturb.generate(m2))));

    }

    @Test
    public void testCombine() throws Exception {
        Xorshift prng = new Xorshift();
        PerturbedAtomHashGenerator generator = new PerturbedAtomHashGenerator(new SeedGenerator(
                BasicAtomEncoder.ATOMIC_NUMBER), new BasicAtomHashGenerator(new SeedGenerator(
                BasicAtomEncoder.ATOMIC_NUMBER), prng, 8), prng, StereoEncoderFactory.EMPTY,
                new MinimumEquivalentCyclicSet(), AtomSuppression.unsuppressed());
        long[][] perturbed = new long[][]{{1, 2, 3, 4}, {1, 1, 1, 1}, {1, 2, 2, 4}, {2, 2, 2, 2},};

        long _0 = 1 ^ 2 ^ 3 ^ 4;
        long _1 = 1 ^ prng.next(1) ^ prng.next(prng.next(1)) ^ prng.next(prng.next(prng.next(1)));
        long _2 = 1 ^ 2 ^ prng.next(2) ^ 4;
        long _3 = 2 ^ prng.next(2) ^ prng.next(prng.next(2)) ^ prng.next(prng.next(prng.next(2)));

        long[] values = generator.combine(perturbed);
        Assert.assertArrayEquals(values, new long[]{_0, _1, _2, _3});
    }

    public IAtomContainer cyclopentylcyclopentane() {
        IAtom[] atoms = new IAtom[]{new Atom("C"), new Atom("C"), new Atom("C"), new Atom("C"), new Atom("C"),
                new Atom("C"), new Atom("C"), new Atom("C"), new Atom("C"), new Atom("C"),};
        IBond[] bonds = new IBond[]{new Bond(atoms[0], atoms[1], SINGLE), new Bond(atoms[0], atoms[4], SINGLE),
                new Bond(atoms[1], atoms[2], SINGLE), new Bond(atoms[2], atoms[3], SINGLE),
                new Bond(atoms[3], atoms[4], SINGLE), new Bond(atoms[5], atoms[6], SINGLE),
                new Bond(atoms[5], atoms[9], SINGLE), new Bond(atoms[6], atoms[7], SINGLE),
                new Bond(atoms[7], atoms[8], SINGLE), new Bond(atoms[8], atoms[9], SINGLE),
                new Bond(atoms[8], atoms[0], SINGLE),};
        IAtomContainer mol = new AtomContainer(0, 0, 0, 0);
        mol.setAtoms(atoms);
        mol.setBonds(bonds);
        return mol;
    }

    /**
     * @cdk.inchi InChI=1S/C10H18/c1-2-6-10-8-4-3-7-9(10)5-1/h9-10H,1-8H2
     */
    public IAtomContainer decahydronaphthalene() {
        IAtom[] atoms = new IAtom[]{new Atom("C"), new Atom("C"), new Atom("C"), new Atom("C"), new Atom("C"),
                new Atom("C"), new Atom("C"), new Atom("C"), new Atom("C"), new Atom("C"),};
        IBond[] bonds = new IBond[]{new Bond(atoms[0], atoms[1], SINGLE), new Bond(atoms[0], atoms[5], SINGLE),
                new Bond(atoms[1], atoms[2], SINGLE), new Bond(atoms[2], atoms[3], SINGLE),
                new Bond(atoms[3], atoms[4], SINGLE), new Bond(atoms[6], atoms[5], SINGLE),
                new Bond(atoms[5], atoms[4], SINGLE), new Bond(atoms[4], atoms[7], SINGLE),
                new Bond(atoms[6], atoms[9], SINGLE), new Bond(atoms[7], atoms[8], SINGLE),
                new Bond(atoms[8], atoms[9], SINGLE),};
        IAtomContainer mol = new AtomContainer(0, 0, 0, 0);
        mol.setAtoms(atoms);
        mol.setBonds(bonds);
        return mol;
    }

}
