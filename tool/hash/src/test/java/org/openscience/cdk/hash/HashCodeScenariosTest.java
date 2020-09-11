/*
 * Copyright (c) 2013 John May <jwmay@users.sf.net>
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

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.AtomContainerAtomPermutor;
import org.openscience.cdk.graph.AtomContainerPermutor;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.stereo.DoubleBondStereochemistry;
import org.openscience.cdk.stereo.TetrahedralChirality;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openscience.cdk.interfaces.IDoubleBondStereochemistry.Conformation.OPPOSITE;
import static org.openscience.cdk.interfaces.IDoubleBondStereochemistry.Conformation.TOGETHER;

/**
 * This test class provides several scenario tests for the {@literal cdk-hash}
 * module.
 *
 * @author John May
 * @cdk.module test-hash
 */
public class HashCodeScenariosTest {

    /**
     * Two molecules with identical Racid identification numbers, these hash
     * codes should be different.
     */
    @Test
    public void figure2a() {

        List<IAtomContainer> mols = sdf("ihlenfeldt93-figure-2a.sdf", 2);

        IAtomContainer a = mols.get(0);
        IAtomContainer b = mols.get(1);

        MoleculeHashGenerator generator = new HashGeneratorMaker().elemental().depth(6).molecular();

        long aHash = generator.generate(a);
        long bHash = generator.generate(b);

        org.hamcrest.MatcherAssert.assertThat(nonEqMesg(a, b), aHash, is(not(bHash)));
    }

    /**
     * Two molecules with identical Racid identification numbers, these hash
     * codes should be different.
     */
    @Test
    public void figure2b() {

        List<IAtomContainer> mols = sdf("ihlenfeldt93-figure-2b.sdf", 2);

        IAtomContainer a = mols.get(0);
        IAtomContainer b = mols.get(1);

        MoleculeHashGenerator generator = new HashGeneratorMaker().elemental().depth(6).molecular();

        long aHash = generator.generate(a);
        long bHash = generator.generate(b);

        org.hamcrest.MatcherAssert.assertThat(nonEqMesg(a, b), aHash, is(not(bHash)));
    }

    /**
     * Two molecules with identical Racid identification numbers, these hash
     * codes should be different.
     */
    @Test
    public void figure2c() {
        List<IAtomContainer> mols = sdf("ihlenfeldt93-figure-2c.sdf", 2);

        IAtomContainer a = mols.get(0);
        IAtomContainer b = mols.get(1);

        MoleculeHashGenerator generator = new HashGeneratorMaker().elemental().depth(6).molecular();

        long aHash = generator.generate(a);
        long bHash = generator.generate(b);

        org.hamcrest.MatcherAssert.assertThat(nonEqMesg(a, b), aHash, is(not(bHash)));
    }

    /**
     * These two molecules from the original publication collide when using a
     * previous hash coding method (Bawden, 81). The hash codes should be
     * different using this method.
     */
    @Test
    public void figure3() {

        List<IAtomContainer> mols = sdf("ihlenfeldt93-figure-3.sdf", 2);

        IAtomContainer a = mols.get(0);
        IAtomContainer b = mols.get(1);

        MoleculeHashGenerator generator = new HashGeneratorMaker().elemental().depth(6).molecular();

        long aHash = generator.generate(a);
        long bHash = generator.generate(b);

        org.hamcrest.MatcherAssert.assertThat(nonEqMesg(a, b), aHash, is(not(bHash)));
    }

    /**
     * These two molecules have atoms experiencing uniform environments but
     * where the number of atoms between the molecules is different. This
     * demonstrates the size the molecule is considered when hashing.
     */
    @Test
    public void figure7() {

        List<IAtomContainer> mols = sdf("ihlenfeldt93-figure-7.sdf", 2);

        IAtomContainer a = mols.get(0);
        IAtomContainer b = mols.get(1);

        MoleculeHashGenerator generator = new HashGeneratorMaker().elemental().depth(6).molecular();

        long aHash = generator.generate(a);
        long bHash = generator.generate(b);

        org.hamcrest.MatcherAssert.assertThat(nonEqMesg(a, b), aHash, is(not(bHash)));
    }

    /**
     * These molecules are erroneous structures from a catalogue file, the
     * German names are the original names as they appear in the catalogue. The
     * hash code identifies that the two molecules are the same.
     */
    @Test
    public void figure10() {
        List<IAtomContainer> mols = sdf("ihlenfeldt93-figure-10.sdf", 2);

        IAtomContainer a = mols.get(0);
        IAtomContainer b = mols.get(1);

        MoleculeHashGenerator generator = new HashGeneratorMaker().elemental().depth(6).molecular();

        long aHash = generator.generate(a);
        long bHash = generator.generate(b);

        org.hamcrest.MatcherAssert.assertThat(eqMesg(a, b), aHash, is(bHash));
    }

    /**
     * This structure is an example where the Cahn-Ingold-Prelog (CIP) rules can
     * not discriminate two neighbours of chiral atom. Due to this, the CIP
     * rules are not used as an atom seed and instead a bootstrap method is
     * used. Please refer to the original article for the exact method.
     */
    @Test
    public void figure11() {

        List<IAtomContainer> mols = sdf("ihlenfeldt93-figure-11.sdf", 1);

        IAtomContainer molecule = mols.get(0);

        MoleculeHashGenerator basic = new HashGeneratorMaker().elemental().depth(8).molecular();
        MoleculeHashGenerator stereo = new HashGeneratorMaker().elemental().depth(8).chiral().molecular();

        long basicHash = basic.generate(molecule);
        long stereoHash = stereo.generate(molecule);

        assertThat(
                "If the stereo-centre was perceived then the basic hash should be different from the chiral hash code",
                basicHash, is(not(stereoHash)));
    }

    /**
     * This scenario demonstrates how stereo-chemistry encoding is invariant
     * under permutation. A simple molecule 'bromo(chloro)fluoromethane' is
     * permuted to all 120 possible atom orderings. It is checked that the (R)-
     * configuration  and (S)- configuration values are invariant
     */
    @Test
    public void figure12() {
        List<IAtomContainer> mols = sdf("ihlenfeldt93-figure-12.sdf", 2);

        MoleculeHashGenerator stereo = new HashGeneratorMaker().elemental().depth(1).chiral().molecular();

        Set<Long> sHashes = new HashSet<Long>();
        Set<Long> rHashes = new HashSet<Long>();

        AtomContainerAtomPermutor rpermutor = new AtomContainerAtomPermutor(mols.get(0));
        AtomContainerAtomPermutor spermutor = new AtomContainerAtomPermutor(mols.get(1));

        while (rpermutor.hasNext() && spermutor.hasNext()) {
            IAtomContainer r = rpermutor.next();
            IAtomContainer s = spermutor.next();
            sHashes.add(stereo.generate(s));
            rHashes.add(stereo.generate(r));
        }
        org.hamcrest.MatcherAssert.assertThat("all (S)-bromo(chloro)fluoromethane permutation produce a single hash code", sHashes.size(),
                CoreMatchers.is(1));
        org.hamcrest.MatcherAssert.assertThat("all (R)-bromo(chloro)fluoromethane permutation produce a single hash code", rHashes.size(),
                CoreMatchers.is(1));
        sHashes.addAll(rHashes);
        org.hamcrest.MatcherAssert.assertThat(sHashes.size(), CoreMatchers.is(2));
    }

    /**
     * This molecule has a tetrahedral stereo-centre depends on the
     * configuration of two double bonds. Swapping the double bond configuration
     * inverts the tetrahedral stereo-centre (R/S) and produces different hash
     * codes.
     */
    @Test
    public void figure13a() {

        List<IAtomContainer> mols = sdf("ihlenfeldt93-figure-13a.sdf", 2);

        IAtomContainer a = mols.get(0);
        IAtomContainer b = mols.get(1);

        MoleculeHashGenerator stereo = new HashGeneratorMaker().elemental().depth(8).chiral().molecular();
        long aHash = stereo.generate(a);
        long bHash = stereo.generate(b);

        assertThat(nonEqMesg(a, b), aHash, is(not(bHash)));
    }

    /**
     * This molecule has double bond stereo chemistry defined only by
     * differences in the configurations of it's substituents. The two
     * configurations the bond can take (Z/E) and should produce different hash
     * codes.
     */
    @Test
    public void figure13b() {
        List<IAtomContainer> mols = sdf("ihlenfeldt93-figure-13b.sdf", 2);

        IAtomContainer a = mols.get(0);
        IAtomContainer b = mols.get(1);

        MoleculeHashGenerator stereo = new HashGeneratorMaker().elemental().depth(8).chiral().molecular();

        assertThat(nonEqMesg(a, b), stereo.generate(a), is(not(stereo.generate(b))));
    }

    /**
     * These two structures were found in the original publication as duplicates
     * in the catalogue of the CHIRON program. The article notes the second name
     * is likely incorrect but that this is how it appears in the catalogue. The
     * two molecules are in fact the same and generate the same hash code.
     */
    @Test
    public void figure14() {

        List<IAtomContainer> mols = sdf("ihlenfeldt93-figure-14.sdf", 2);

        IAtomContainer a = mols.get(0);
        IAtomContainer b = mols.get(1);

        MoleculeHashGenerator generator = new HashGeneratorMaker().elemental().depth(6).molecular();

        long aHash = generator.generate(a);
        long bHash = generator.generate(b);

        org.hamcrest.MatcherAssert.assertThat(eqMesg(a, b), aHash, is(bHash));
    }

    /**
     * These two compounds are connected differently but produce the same basic
     * hash code. In order to discriminate them we must use the perturbed hash
     * code.
     */
    @Test
    public void figure15() {

        List<IAtomContainer> mols = sdf("ihlenfeldt93-figure-15.sdf", 2);

        IAtomContainer a = mols.get(0);
        IAtomContainer b = mols.get(1);

        MoleculeHashGenerator generator = new HashGeneratorMaker().elemental().depth(6).molecular();
        long aHash = generator.generate(a);
        long bHash = generator.generate(b);

        org.hamcrest.MatcherAssert.assertThat(eqMesg(a, b), aHash, is(bHash));

        MoleculeHashGenerator perturbed = new HashGeneratorMaker().elemental().depth(6).perturbed().molecular();
        aHash = perturbed.generate(a);
        bHash = perturbed.generate(b);
        org.hamcrest.MatcherAssert.assertThat(nonEqMesg(a, b), aHash, is(not(bHash)));
    }

    /**
     * The molecules cubane and cuneane have the same number of atoms all of
     * which experience the same environment in the first sphere. Using a
     * non-perturbed hash code, these will hash to the same value. The perturbed
     * hash code, allows us to discriminate them.
     */
    @Test
    public void figure16a() {

        List<IAtomContainer> mols = sdf("ihlenfeldt93-figure-16a.sdf", 2);

        IAtomContainer a = mols.get(0);
        IAtomContainer b = mols.get(1);

        MoleculeHashGenerator nonperturbed = new HashGeneratorMaker().elemental().depth(6).molecular();
        MoleculeHashGenerator perturbed = new HashGeneratorMaker().elemental().depth(6).perturbed().molecular();

        long aHash = nonperturbed.generate(a);
        long bHash = nonperturbed.generate(b);
        org.hamcrest.MatcherAssert.assertThat(eqMesg(a, b), aHash, is(bHash));

        aHash = perturbed.generate(a);
        bHash = perturbed.generate(b);
        org.hamcrest.MatcherAssert.assertThat(nonEqMesg(a, b), aHash, is(not(bHash)));

        AtomHashGenerator perturbedAtomic = new HashGeneratorMaker().elemental().depth(3).perturbed().atomic();
        long[] aHashes = perturbedAtomic.generate(a);
        long[] bHashes = perturbedAtomic.generate(b);

        assertThat("cubane has 1 equiavelnt class", toSet(aHashes).size(), is(1));
        assertThat("cubane has 3 equiavelnt classes", toSet(bHashes).size(), is(3));
    }

    private Set<Long> toSet(long[] xs) {
        Set<Long> set = new HashSet<Long>();
        for (long x : xs) {
            set.add(x);
        }
        return set;
    }

    /**
     * A chlorinated cubane and cuneane can not be told apart by the basic hash
     * code. However using perturbed hash codes is is possible to tell them
     * apart as well as the 3 different chlorination locations on the cuneane
     */
    @Test
    public void figure16b() {

        List<IAtomContainer> mols = sdf("ihlenfeldt93-figure-16b.sdf", 4);

        IAtomContainer a = mols.get(0);
        IAtomContainer b = mols.get(1);
        IAtomContainer c = mols.get(2);
        IAtomContainer d = mols.get(3);

        MoleculeHashGenerator generator = new HashGeneratorMaker().elemental().depth(6).perturbed().molecular();

        long aHash = generator.generate(a);
        long bHash = generator.generate(b);
        long cHash = generator.generate(c);
        long dHash = generator.generate(d);

        org.hamcrest.MatcherAssert.assertThat(nonEqMesg(a, b), aHash, is(not(bHash)));
        org.hamcrest.MatcherAssert.assertThat(nonEqMesg(a, c), aHash, is(not(cHash)));
        org.hamcrest.MatcherAssert.assertThat(nonEqMesg(a, d), aHash, is(not(dHash)));
        org.hamcrest.MatcherAssert.assertThat(nonEqMesg(a, c), bHash, is(not(cHash)));
        org.hamcrest.MatcherAssert.assertThat(nonEqMesg(b, d), bHash, is(not(dHash)));
        org.hamcrest.MatcherAssert.assertThat(nonEqMesg(c, d), cHash, is(not(dHash)));

    }

    /**
     * This scenario demonstrates how the depth influences the hash code. These
     * two molecules differ only by length of their aliphatic chains. One  has
     * chains of length 10 and 11 and other of length 11 and 10 (connected the
     * other way). To tell these apart the depth must be large enough to
     * propagate  the environments from the ends of both chains.
     */
    @Test
    public void aminotetracosanone() {

        List<IAtomContainer> aminotetracosanones = sdf("aminotetracosanones.sdf", 2);

        IAtomContainer a = aminotetracosanones.get(0);
        IAtomContainer b = aminotetracosanones.get(1);

        for (int depth = 0; depth < 12; depth++) {
            MoleculeHashGenerator basic = new HashGeneratorMaker().elemental().depth(depth).molecular();
            long aHash = basic.generate(a);
            long bHash = basic.generate(b);

            if (depth < 7) {
                assertThat(eqMesg(a, b) + " at depth " + depth, aHash, is(bHash));
            } else {
                assertThat(nonEqMesg(a, b) + " at depth " + depth, aHash, is(not(bHash)));
            }
        }

    }

    /**
     * This test demonstrates that the nine stereo isomers of inositol can be
     * hashed to the same value or to different values (perturbed).
     *
     * @see <a href="http://en.wikipedia.org/wiki/Inositol#Isomers_and_structure">Inositol
     *      Isomers</a>
     */
    @Test
    public void inositols() {

        List<IAtomContainer> inositols = sdf("inositols.sdf", 9);

        // non-stereo non-perturbed hash generator
        MoleculeHashGenerator basic = new HashGeneratorMaker().elemental().depth(6).molecular();

        Set<Long> hashes = new HashSet<Long>(5);

        for (IAtomContainer inositol : inositols) {
            long hash = basic.generate(inositol);
            hashes.add(hash);
        }

        assertThat("all inositol isomers should hash to the same value", hashes.size(), is(1));

        // stereo non-perturbed hash generator
        MoleculeHashGenerator stereo = new HashGeneratorMaker().elemental().depth(6).chiral().molecular();
        hashes.clear();

        for (IAtomContainer inositol : inositols) {
            long hash = stereo.generate(inositol);
            hashes.add(hash);
        }

        assertThat("all inositol isomers should hash to the same value", hashes.size(), is(1));

        // stereo non-perturbed hash generator
        MoleculeHashGenerator perturbed = new HashGeneratorMaker().elemental().depth(6).chiral().perturbed()
                .molecular();
        hashes.clear();

        for (IAtomContainer inositol : inositols) {
            long hash = perturbed.generate(inositol);
            hashes.add(hash);
        }

        assertThat("all inositol isomers should hash to different values", hashes.size(), is(9));

    }

    @Test
    public void allenesWithImplicitHydrogens() {

        List<IAtomContainer> allenes = sdf("allene-implicit-h.sdf", 2);

        IAtomContainer mAllene = allenes.get(0);
        IAtomContainer pAllene = allenes.get(1);

        // non-stereo hash code
        MoleculeHashGenerator basic = new HashGeneratorMaker().elemental().depth(2).molecular();
        assertThat("(M) and (P) allene should hash the same when non-stereo", basic.generate(mAllene),
                is(basic.generate(pAllene)));

        MoleculeHashGenerator stereo = new HashGeneratorMaker().elemental().depth(2).chiral().molecular();

        assertThat("(M) and (P) allene should not hash the same when stereo", stereo.generate(mAllene),
                is(not(stereo.generate(pAllene))));

        // check the hashes are invariant under permutation
        long mAlleneReference = stereo.generate(mAllene);
        long pAlleneReference = stereo.generate(pAllene);

        AtomContainerPermutor mAllenePermutor = new AtomContainerAtomPermutor(mAllene);
        while (mAllenePermutor.hasNext()) {
            assertThat("(M)-allene was not invariant under permutation", stereo.generate(mAllenePermutor.next()),
                    is(mAlleneReference));
        }

        AtomContainerPermutor pAllenePermutor = new AtomContainerAtomPermutor(pAllene);
        while (pAllenePermutor.hasNext()) {
            assertThat("(P)-allene was not invariant under permutation", stereo.generate(pAllenePermutor.next()),
                    is(pAlleneReference));
        }
    }

    @Test
    public void allenesWithExplicitHydrogens() {

        List<IAtomContainer> allenes = sdf("allene-explicit-h.sdf", 2);

        IAtomContainer mAllene = allenes.get(0);
        IAtomContainer pAllene = allenes.get(1);

        // non-stereo hash code
        MoleculeHashGenerator basic = new HashGeneratorMaker().elemental().depth(2).molecular();
        assertThat("(M) and (P) allene should hash the same when non-stereo", basic.generate(mAllene),
                is(basic.generate(pAllene)));

        MoleculeHashGenerator stereo = new HashGeneratorMaker().elemental().depth(2).chiral().molecular();

        assertThat("(M) and (P) allene should not hash the same when stereo", stereo.generate(mAllene),
                is(not(stereo.generate(pAllene))));

        // check the hashes are invariant under permutation
        long mAlleneReference = stereo.generate(mAllene);
        long pAlleneReference = stereo.generate(pAllene);

        AtomContainerPermutor mAllenePermutor = new AtomContainerAtomPermutor(mAllene);
        while (mAllenePermutor.hasNext()) {
            assertThat("(M)-allene was not invariant under permutation", stereo.generate(mAllenePermutor.next()),
                    is(mAlleneReference));
        }

        AtomContainerPermutor pAllenePermutor = new AtomContainerAtomPermutor(pAllene);
        while (pAllenePermutor.hasNext()) {
            assertThat("(P)-allene was not invariant under permutation", stereo.generate(pAllenePermutor.next()),
                    is(pAlleneReference));
        }
    }

    @Test
    public void allenes2Dand3D() {

        List<IAtomContainer> allenes2D = sdf("allene-explicit-h.sdf", 2);
        List<IAtomContainer> allenes3D = sdf("allene-explicit-3d-h.sdf", 2);

        IAtomContainer mAllene2D = allenes2D.get(0);
        IAtomContainer mAllene3D = allenes3D.get(0);
        IAtomContainer pAllene2D = allenes2D.get(1);
        IAtomContainer pAllene3D = allenes3D.get(1);

        // non-stereo hash code
        MoleculeHashGenerator basic = new HashGeneratorMaker().elemental().depth(2).molecular();
        assertThat("(M) and (P) allene (2D) should hash the same when non-stereo", basic.generate(mAllene2D),
                is(basic.generate(pAllene2D)));
        assertThat("(M) and (P) allene (3D) should hash the same when non-stereo", basic.generate(mAllene3D),
                is(basic.generate(pAllene3D)));
        assertThat("(M) allene should hash the same in 2D and 3D", basic.generate(mAllene2D),
                is(basic.generate(mAllene3D)));
        assertThat("(P) allene should hash the same in 2D and 3D", basic.generate(mAllene2D),
                is(basic.generate(mAllene3D)));

        MoleculeHashGenerator stereo = new HashGeneratorMaker().elemental().depth(2).chiral().molecular();

        assertThat("(M) and (P) allene should not hash the same when stereo", stereo.generate(mAllene2D),
                is(not(stereo.generate(pAllene2D))));
        assertThat("(M) and (P) allene (3D) should not hash the same when stereo", stereo.generate(mAllene3D),
                is(not(stereo.generate(pAllene3D))));

        assertThat("(M) allene should hash the same in 2D and 3D (stereo)", basic.generate(mAllene2D),
                is(basic.generate(mAllene3D)));
        assertThat("(P) allene should hash the same in 2D and 3D (stereo)", basic.generate(pAllene2D),
                is(basic.generate(pAllene3D)));
    }

    @Test
    public void allenesWithUnspecifiedConfiguration() {
        List<IAtomContainer> allenes = sdf("allene-implicit-h.sdf", 2);
        List<IAtomContainer> unspecified = sdf("allene-unspecified.sdf", 2);

        IAtomContainer mAllene = allenes.get(0);
        IAtomContainer pAllene = allenes.get(1);
        IAtomContainer unspecAllene1 = unspecified.get(0);
        IAtomContainer unspecAllene2 = unspecified.get(1);

        // non-stereo hash code
        MoleculeHashGenerator basic = new HashGeneratorMaker().elemental().depth(2).molecular();

        assertThat("(M) and (P) allene should hash the same when non-stereo", basic.generate(mAllene),
                is(basic.generate(pAllene)));
        assertThat("Unspecifed allene should be the same", basic.generate(mAllene), is(basic.generate(unspecAllene1)));
        assertThat("Unspecifed allene should be the same", basic.generate(mAllene), is(basic.generate(unspecAllene2)));

        MoleculeHashGenerator stereo = new HashGeneratorMaker().elemental().depth(2).chiral().molecular();
        assertThat("(M) and (P) allene should not hash the same when using stereo", stereo.generate(mAllene),
                is(not(stereo.generate(pAllene))));
        assertThat("Unspecifed allene should be the different", stereo.generate(mAllene),
                is(not(stereo.generate(unspecAllene1))));
        assertThat("Unspecifed allene should be the different", stereo.generate(mAllene),
                is(not(stereo.generate(unspecAllene2))));
        assertThat("Unspecifed allenes should be the same", stereo.generate(unspecAllene1),
                is(stereo.generate(unspecAllene2)));
    }

    @Test
    public void cumulenes() {

        List<IAtomContainer> cumulenes = sdf("cumulenes.sdf", 2);

        IAtomContainer eCumulene = cumulenes.get(0);
        IAtomContainer zCumulene = cumulenes.get(1);

        // non-stereo hash code
        MoleculeHashGenerator basic = new HashGeneratorMaker().elemental().depth(2).molecular();
        assertThat("(E) and (Z) cumulene should hash the same when non-stereo", basic.generate(eCumulene),
                is(basic.generate(zCumulene)));

        MoleculeHashGenerator stereo = new HashGeneratorMaker().elemental().depth(2).chiral().molecular();

        assertThat("(E) and (Z) cumulene should not hash the same when stereo", stereo.generate(eCumulene),
                is(not(stereo.generate(zCumulene))));
    }

    @Test
    public void suppressedHydrogens() {

        List<IAtomContainer> implicits = sdf("butan-2-ols.sdf", 2);
        List<IAtomContainer> explicits = sdf("butan-2-ols-explicit-hydrogens.sdf", 2);

        IAtomContainer implicit = implicits.get(0);
        IAtomContainer explicit = explicits.get(0);

        MoleculeHashGenerator unsuppressed = new HashGeneratorMaker().elemental().depth(4).molecular();
        assertThat(nonEqMesg(implicit, explicit), unsuppressed.generate(implicit),
                is(not(unsuppressed.generate(explicit))));

        MoleculeHashGenerator suppressed = new HashGeneratorMaker().elemental().depth(4).suppressHydrogens()
                .molecular();
        assertThat(eqMesg(implicit, explicit), suppressed.generate(implicit), is(suppressed.generate(explicit)));

    }

    @Test
    public void suppressedHydrogens_chiral() {

        List<IAtomContainer> implicits = sdf("butan-2-ols.sdf", 2);
        List<IAtomContainer> explicits = sdf("butan-2-ols-explicit-hydrogens.sdf", 2);

        IAtomContainer implicit = implicits.get(0);
        IAtomContainer explicit = explicits.get(0);

        MoleculeHashGenerator unsuppressed = new HashGeneratorMaker().elemental().depth(4).chiral().molecular();
        assertThat(nonEqMesg(implicit, explicit), unsuppressed.generate(implicit),
                is(not(unsuppressed.generate(explicit))));

        MoleculeHashGenerator suppressed = new HashGeneratorMaker().elemental().depth(4).chiral().suppressHydrogens()
                .molecular();
        assertThat(eqMesg(implicit, explicit), suppressed.generate(implicit), is(suppressed.generate(explicit)));

        // okay now let's do some permutations can check the hash codes are always the same
        AtomContainerPermutor implicitPermutor = new AtomContainerAtomPermutor(implicit);
        AtomContainerPermutor explicitPermutor = new AtomContainerAtomPermutor(explicit);

        while (implicitPermutor.hasNext() && explicitPermutor.hasNext()) {
            implicit = implicitPermutor.next();
            explicit = explicitPermutor.next();
            assertThat(eqMesg(implicit, explicit), suppressed.generate(implicit), is(suppressed.generate(explicit)));
        }

    }

    @Test
    public void inositols_suppressedHydrogens() {

        List<IAtomContainer> implicits = sdf("inositols.sdf", 9);
        List<IAtomContainer> explicits = sdf("inositols-explicit-hydrogens.sdf", 9);

        assertThat("different number of implicit and explicit structures", implicits.size(), is(explicits.size()));

        MoleculeHashGenerator unsuppressed = new HashGeneratorMaker().elemental().depth(4).perturbed().molecular();

        MoleculeHashGenerator suppressed = new HashGeneratorMaker().elemental().depth(4).suppressHydrogens()
                .perturbed().molecular();

        // check that for each inesitol the values are equal if we suppress the hydrogens
        for (int i = 0; i < implicits.size(); i++) {

            IAtomContainer implicit = implicits.get(i);
            IAtomContainer explicit = explicits.get(i);

            assertThat(nonEqMesg(implicit, explicit), unsuppressed.generate(implicit),
                    is(not(unsuppressed.generate(explicit))));

            assertThat(eqMesg(implicit, explicit), suppressed.generate(implicit), is(suppressed.generate(explicit)));

        }

    }

    @Test
    public void inositols_suppressedHydrogens_chiral() {

        List<IAtomContainer> implicits = sdf("inositols.sdf", 9);
        List<IAtomContainer> explicits = sdf("inositols-explicit-hydrogens.sdf", 9);

        assertThat("different number of implicit and explicit structures", implicits.size(), is(explicits.size()));

        // check that for different depth values - all the inositols will hash
        // differently or the same depending on whether or not we suppress the
        // explicit hydrogens
        for (int d = 0; d < 10; d++) {

            MoleculeHashGenerator unsuppressed = new HashGeneratorMaker().elemental().depth(d).chiral().perturbed()
                    .molecular();

            MoleculeHashGenerator suppressed = new HashGeneratorMaker().elemental().depth(d).chiral()
                    .suppressHydrogens().perturbed().molecular();
            for (int i = 0; i < implicits.size(); i++) {

                IAtomContainer implicit = implicits.get(i);
                IAtomContainer explicit = explicits.get(i);

                assertThat(nonEqMesg(implicit, explicit), unsuppressed.generate(implicit),
                        is(not(unsuppressed.generate(explicit))));

                assertThat(eqMesg(implicit, explicit), suppressed.generate(implicit), is(suppressed.generate(explicit)));

            }
        }
    }

    @Test
    public void suppressedHydrogens_dicholorethenes() {

        List<IAtomContainer> implicits = sdf("dichloroethenes.sdf", 2);
        List<IAtomContainer> explicits = sdf("dichloroethenes-explicit-hydrogens.sdf", 2);

        assertThat("different number of implicit and explicit structures", implicits.size(), is(explicits.size()));

        // check that for different depth values - all the dicholorethenes will hash
        // differently or the same depending on whether or not we suppress the
        // explicit hydrogens
        for (int d = 0; d < 4; d++) {

            MoleculeHashGenerator unsuppressed = new HashGeneratorMaker().elemental().depth(d).chiral().perturbed()
                    .molecular();

            MoleculeHashGenerator suppressed = new HashGeneratorMaker().elemental().depth(d).chiral()
                    .suppressHydrogens().perturbed().molecular();
            for (int i = 0; i < implicits.size(); i++) {

                IAtomContainer implicit = implicits.get(i);
                IAtomContainer explicit = explicits.get(i);

                assertThat(nonEqMesg(implicit, explicit), unsuppressed.generate(implicit),
                        is(not(unsuppressed.generate(explicit))));

                assertThat(eqMesg(implicit, explicit), suppressed.generate(implicit), is(suppressed.generate(explicit)));

            }
        }
    }

    @Test
    public void suppressedHydrogens_allenes() {

        List<IAtomContainer> implicits = sdf("allene-implicit-h.sdf", 2);
        List<IAtomContainer> explicits = sdf("allene-explicit-h.sdf", 2);

        assertThat("different number of implicit and explicit structures", implicits.size(), is(explicits.size()));

        // check that for different depth values - all the dicholorethenes will hash
        // differently or the same depending on whether or not we suppress the
        // explicit hydrogens
        for (int d = 0; d < 4; d++) {

            MoleculeHashGenerator unsuppressed = new HashGeneratorMaker().elemental().depth(d).chiral().perturbed()
                    .molecular();

            MoleculeHashGenerator suppressed = new HashGeneratorMaker().elemental().depth(d).chiral()
                    .suppressHydrogens().perturbed().molecular();
            for (int i = 0; i < implicits.size(); i++) {

                IAtomContainer implicit = implicits.get(i);
                IAtomContainer explicit = explicits.get(i);

                assertThat(nonEqMesg(implicit, explicit), unsuppressed.generate(implicit),
                        is(not(unsuppressed.generate(explicit))));

                assertThat(eqMesg(implicit, explicit), suppressed.generate(implicit), is(suppressed.generate(explicit)));

            }
        }
    }

    @Test
    public void butan2ol_UsingStereoElement() {

        // C[CH](O)CC
        IAtomContainer butan2ol = new AtomContainer();
        butan2ol.addAtom(new Atom("C"));
        butan2ol.addAtom(new Atom("C"));
        butan2ol.addAtom(new Atom("O"));
        butan2ol.addAtom(new Atom("C"));
        butan2ol.addAtom(new Atom("C"));
        butan2ol.addBond(0, 1, IBond.Order.SINGLE);
        butan2ol.addBond(1, 2, IBond.Order.SINGLE);
        butan2ol.addBond(1, 3, IBond.Order.SINGLE);
        butan2ol.addBond(3, 4, IBond.Order.SINGLE);

        MoleculeHashGenerator generator = new HashGeneratorMaker().elemental().depth(4).chiral().molecular();

        long achiral = generator.generate(butan2ol);

        // C[C@@H](O)CC (2R)-butan-2-ol
        butan2ol.addStereoElement(new TetrahedralChirality(butan2ol.getAtom(1), new IAtom[]{butan2ol.getAtom(0),
                butan2ol.getAtom(1), // represents implicit H
                butan2ol.getAtom(2), butan2ol.getAtom(3),}, ITetrahedralChirality.Stereo.CLOCKWISE));

        long rConfiguration = generator.generate(butan2ol);

        // C[C@H](O)CC  (2S)-butan-2-ol
        butan2ol.setStereoElements(new ArrayList<IStereoElement>(1));
        butan2ol.addStereoElement(new TetrahedralChirality(butan2ol.getAtom(1), new IAtom[]{butan2ol.getAtom(0),
                butan2ol.getAtom(1), // represents implicit H
                butan2ol.getAtom(2), butan2ol.getAtom(3),}, ITetrahedralChirality.Stereo.ANTI_CLOCKWISE));

        long sConfiguration = generator.generate(butan2ol);

        // first check we have 3 different values
        assertThat(rConfiguration, is(not(sConfiguration)));
        assertThat(rConfiguration, is(not(achiral)));
        assertThat(sConfiguration, is(not(achiral)));

        // load the ones with 2D coordinates to check we match them
        List<IAtomContainer> butan2ols = sdf("butan-2-ols.sdf", 2);

        // first is 'R'
        assertThat(rConfiguration, is(generator.generate(butan2ols.get(0))));
        // second is 'S'
        assertThat(sConfiguration, is(generator.generate(butan2ols.get(1))));

        // okay now let's move around the atoms in the stereo element

        // [C@H](C)(O)CC (2R)-butan-2-ol
        butan2ol.setStereoElements(new ArrayList<IStereoElement>(1));
        butan2ol.addStereoElement(new TetrahedralChirality(butan2ol.getAtom(1), new IAtom[]{butan2ol.getAtom(1), // represents implicit H
                butan2ol.getAtom(0), butan2ol.getAtom(2), butan2ol.getAtom(3),},
                ITetrahedralChirality.Stereo.ANTI_CLOCKWISE));

        // check 'R' configuration was encoded
        assertThat(generator.generate(butan2ol), is(generator.generate(butan2ols.get(0))));

        // [C@@H](C)(O)CC (2S)-butan-2-ol
        butan2ol.setStereoElements(new ArrayList<IStereoElement>(1));
        butan2ol.addStereoElement(new TetrahedralChirality(butan2ol.getAtom(1), new IAtom[]{butan2ol.getAtom(1), // represents implicit H
                butan2ol.getAtom(0), butan2ol.getAtom(2), butan2ol.getAtom(3),}, ITetrahedralChirality.Stereo.CLOCKWISE));

        // check 'S' configuration was encoded
        assertThat(generator.generate(butan2ol), is(generator.generate(butan2ols.get(1))));

    }

    @Test
    public void dichloroethenes_stereoElements() {

        // CLC=CCL
        IAtomContainer dichloroethene = new AtomContainer();
        dichloroethene.addAtom(new Atom("Cl"));
        dichloroethene.addAtom(new Atom("C"));
        dichloroethene.addAtom(new Atom("C"));
        dichloroethene.addAtom(new Atom("Cl"));
        dichloroethene.addBond(0, 1, IBond.Order.SINGLE);
        dichloroethene.addBond(1, 2, IBond.Order.DOUBLE);
        dichloroethene.addBond(2, 3, IBond.Order.SINGLE);

        MoleculeHashGenerator generator = new HashGeneratorMaker().elemental().depth(4).chiral().molecular();

        // set E configuration
        dichloroethene.setStereoElements(new ArrayList<IStereoElement>());
        dichloroethene.addStereoElement(new DoubleBondStereochemistry(dichloroethene.getBond(1), new IBond[]{
                dichloroethene.getBond(0), dichloroethene.getBond(2)}, OPPOSITE));

        long eConfiguration = generator.generate(dichloroethene);

        // set Z configuration
        dichloroethene.setStereoElements(new ArrayList<IStereoElement>());
        dichloroethene.addStereoElement(new DoubleBondStereochemistry(dichloroethene.getBond(1), new IBond[]{
                dichloroethene.getBond(0), dichloroethene.getBond(2)}, TOGETHER));
        long zConfiguration = generator.generate(dichloroethene);

        // (E) and (Z) 2D geometry
        List<IAtomContainer> dichloroethenes2D = sdf("dichloroethenes.sdf", 2);

        assertThat(eConfiguration, is(generator.generate(dichloroethenes2D.get(0))));
        assertThat(zConfiguration, is(generator.generate(dichloroethenes2D.get(1))));
    }

    /**
     * Tests demonstrates encoding of stereo specific hash codes (double bond)
     * using stereo-elements. The hash codes of the molecule with stereo
     * elements should match those we perceive using 2D coordinates (explicit
     * hydrogens)
     */
    @Test
    public void dichloroethenes_stereoElements_explicitH() {

        // CLC=CCL
        IAtomContainer dichloroethene = new AtomContainer();
        dichloroethene.addAtom(new Atom("Cl")); // Cl1
        dichloroethene.addAtom(new Atom("C")); // C2
        dichloroethene.addAtom(new Atom("C")); // C3
        dichloroethene.addAtom(new Atom("Cl")); // CL4
        dichloroethene.addAtom(new Atom("H")); // H5
        dichloroethene.addAtom(new Atom("H")); // H6
        dichloroethene.addBond(0, 1, IBond.Order.SINGLE); // CL1-C2   0
        dichloroethene.addBond(1, 2, IBond.Order.DOUBLE); // C2-C3    1
        dichloroethene.addBond(2, 3, IBond.Order.SINGLE); // CL2-C3   2
        dichloroethene.addBond(1, 4, IBond.Order.SINGLE); // C2-H5    3
        dichloroethene.addBond(2, 5, IBond.Order.SINGLE); // C3-H6    4

        MoleculeHashGenerator generator = new HashGeneratorMaker().elemental().depth(4).chiral().molecular();

        Set<Long> eConfigurations = new HashSet<Long>();
        Set<Long> zConfigurations = new HashSet<Long>();

        // set E configurations - we can specify using the C-CL bonds or the C-H
        // bonds so there are four possible combinations it's easiest to think
        // about with SMILES. Depending on which atoms we use the configuration
        // may be together or opposite but represent the same configuration (E)-
        // in this case. There are actually 8 ways in SMILES due to having two
        // planar embeddings but these four demonstrate what we're testing here:
        //
        // Cl/C([H])=C([H])/Cl
        // ClC(/[H])=C([H])/Cl
        // ClC(/[H])=C(\[H])Cl
        // Cl/C([H])=C(\[H])Cl
        dichloroethene.setStereoElements(new ArrayList<IStereoElement>());
        dichloroethene.addStereoElement(new DoubleBondStereochemistry(dichloroethene.getBond(1), new IBond[]{
                dichloroethene.getBond(0), // CL1-C2
                dichloroethene.getBond(2)}, // CL4-C3
                OPPOSITE));
        eConfigurations.add(generator.generate(dichloroethene));

        dichloroethene.setStereoElements(new ArrayList<IStereoElement>());
        dichloroethene.addStereoElement(new DoubleBondStereochemistry(dichloroethene.getBond(1), new IBond[]{
                dichloroethene.getBond(3), // C2-H5
                dichloroethene.getBond(2)}, TOGETHER));
        eConfigurations.add(generator.generate(dichloroethene));

        dichloroethene.setStereoElements(new ArrayList<IStereoElement>());
        dichloroethene.addStereoElement(new DoubleBondStereochemistry(dichloroethene.getBond(1), new IBond[]{
                dichloroethene.getBond(3), // C2-H5
                dichloroethene.getBond(4)}, // C3-H6
                OPPOSITE));
        eConfigurations.add(generator.generate(dichloroethene));

        dichloroethene.setStereoElements(new ArrayList<IStereoElement>());
        dichloroethene.addStereoElement(new DoubleBondStereochemistry(dichloroethene.getBond(1), new IBond[]{
                dichloroethene.getBond(0), // CL1-C2
                dichloroethene.getBond(4)}, // C3-H6
                TOGETHER));
        eConfigurations.add(generator.generate(dichloroethene));

        // set Z configurations - we can specify using the C-CL bonds or the
        // C-H bonds so there are four possible combinations
        dichloroethene.setStereoElements(new ArrayList<IStereoElement>());
        dichloroethene.addStereoElement(new DoubleBondStereochemistry(dichloroethene.getBond(1), new IBond[]{
                dichloroethene.getBond(0), dichloroethene.getBond(2)}, TOGETHER));
        zConfigurations.add(generator.generate(dichloroethene));

        dichloroethene.setStereoElements(new ArrayList<IStereoElement>());
        dichloroethene.addStereoElement(new DoubleBondStereochemistry(dichloroethene.getBond(1), new IBond[]{
                dichloroethene.getBond(3), dichloroethene.getBond(2)}, OPPOSITE));
        zConfigurations.add(generator.generate(dichloroethene));

        dichloroethene.setStereoElements(new ArrayList<IStereoElement>());
        dichloroethene.addStereoElement(new DoubleBondStereochemistry(dichloroethene.getBond(1), new IBond[]{
                dichloroethene.getBond(3), dichloroethene.getBond(4)}, TOGETHER));
        zConfigurations.add(generator.generate(dichloroethene));

        dichloroethene.setStereoElements(new ArrayList<IStereoElement>());
        dichloroethene.addStereoElement(new DoubleBondStereochemistry(dichloroethene.getBond(1), new IBond[]{
                dichloroethene.getBond(0), dichloroethene.getBond(4)}, OPPOSITE));
        zConfigurations.add(generator.generate(dichloroethene));

        // (E) and (Z) using 2D geometry (explicit hydrogens)
        List<IAtomContainer> dichloroethenes2D = sdf("dichloroethenes-explicit-hydrogens.sdf", 2);

        assertThat(eConfigurations.size(), is(1));
        assertThat(eConfigurations, hasItem(generator.generate(dichloroethenes2D.get(0))));

        assertThat(zConfigurations.size(), is(1));
        assertThat(zConfigurations, hasItem(generator.generate(dichloroethenes2D.get(1))));
    }

    /**
     * Tests demonstrates encoding of stereo specific hash codes (double bond)
     * using stereo-elements and suppressing the hydrogens. The hash codes
     * of the molecule with stereo elements should match those we perceive
     * using 2D coordinates (implicit hydrogens)
     */
    @Test
    public void dichloroethenes_stereoElements_explicitH_suppressed() {

        // CLC=CCL
        IAtomContainer dichloroethene = new AtomContainer();
        dichloroethene.addAtom(new Atom("Cl")); // Cl1
        dichloroethene.addAtom(new Atom("C")); // C2
        dichloroethene.addAtom(new Atom("C")); // C3
        dichloroethene.addAtom(new Atom("Cl")); // CL4
        dichloroethene.addAtom(new Atom("H")); // H5
        dichloroethene.addAtom(new Atom("H")); // H6
        dichloroethene.addBond(0, 1, IBond.Order.SINGLE); // CL1-C2   0
        dichloroethene.addBond(1, 2, IBond.Order.DOUBLE); // C2-C3    1
        dichloroethene.addBond(2, 3, IBond.Order.SINGLE); // CL2-C3   2
        dichloroethene.addBond(1, 4, IBond.Order.SINGLE); // C2-H5    3
        dichloroethene.addBond(2, 5, IBond.Order.SINGLE); // C3-H6    4

        MoleculeHashGenerator generator = new HashGeneratorMaker().elemental().depth(4).chiral().suppressHydrogens()
                .molecular();

        Set<Long> eConfigurations = new HashSet<Long>();
        Set<Long> zConfigurations = new HashSet<Long>();

        // set E configurations - we can specify using the C-CL bonds or the C-H
        // bonds so there are four possible combinations it's easiest to think
        // about with SMILES. Depending on which atoms we use the configuration
        // may be together or opposite but represent the same configuration (E)-
        // in this case. There are actually 8 ways in SMILES due to having two
        // planar embeddings but these four demonstrate what we're testing here:
        //
        // Cl/C([H])=C([H])/Cl
        // ClC(/[H])=C([H])/Cl
        // ClC(/[H])=C(\[H])Cl
        // Cl/C([H])=C(\[H])Cl
        dichloroethene.setStereoElements(new ArrayList<IStereoElement>());
        dichloroethene.addStereoElement(new DoubleBondStereochemistry(dichloroethene.getBond(1), new IBond[]{
                dichloroethene.getBond(0), // CL1-C2
                dichloroethene.getBond(2)}, // CL4-C3
                OPPOSITE));
        eConfigurations.add(generator.generate(dichloroethene));

        dichloroethene.setStereoElements(new ArrayList<IStereoElement>());
        dichloroethene.addStereoElement(new DoubleBondStereochemistry(dichloroethene.getBond(1), new IBond[]{
                dichloroethene.getBond(3), // C2-H5
                dichloroethene.getBond(2)}, TOGETHER));
        eConfigurations.add(generator.generate(dichloroethene));

        dichloroethene.setStereoElements(new ArrayList<IStereoElement>());
        dichloroethene.addStereoElement(new DoubleBondStereochemistry(dichloroethene.getBond(1), new IBond[]{
                dichloroethene.getBond(3), // C2-H5
                dichloroethene.getBond(4)}, // C3-H6
                OPPOSITE));
        eConfigurations.add(generator.generate(dichloroethene));

        dichloroethene.setStereoElements(new ArrayList<IStereoElement>());
        dichloroethene.addStereoElement(new DoubleBondStereochemistry(dichloroethene.getBond(1), new IBond[]{
                dichloroethene.getBond(0), // CL1-C2
                dichloroethene.getBond(4)}, // C3-H6
                TOGETHER));
        eConfigurations.add(generator.generate(dichloroethene));

        // set Z configurations - we can specify using the C-CL bonds or the
        // C-H bonds so there are four possible combinations
        dichloroethene.setStereoElements(new ArrayList<IStereoElement>());
        dichloroethene.addStereoElement(new DoubleBondStereochemistry(dichloroethene.getBond(1), new IBond[]{
                dichloroethene.getBond(0), dichloroethene.getBond(2)}, TOGETHER));
        zConfigurations.add(generator.generate(dichloroethene));

        dichloroethene.setStereoElements(new ArrayList<IStereoElement>());
        dichloroethene.addStereoElement(new DoubleBondStereochemistry(dichloroethene.getBond(1), new IBond[]{
                dichloroethene.getBond(3), dichloroethene.getBond(2)}, OPPOSITE));
        zConfigurations.add(generator.generate(dichloroethene));

        dichloroethene.setStereoElements(new ArrayList<IStereoElement>());
        dichloroethene.addStereoElement(new DoubleBondStereochemistry(dichloroethene.getBond(1), new IBond[]{
                dichloroethene.getBond(3), dichloroethene.getBond(4)}, TOGETHER));
        zConfigurations.add(generator.generate(dichloroethene));

        dichloroethene.setStereoElements(new ArrayList<IStereoElement>());
        dichloroethene.addStereoElement(new DoubleBondStereochemistry(dichloroethene.getBond(1), new IBond[]{
                dichloroethene.getBond(0), dichloroethene.getBond(4)}, OPPOSITE));
        zConfigurations.add(generator.generate(dichloroethene));

        // (E) and (Z) using 2D geometry (implicit hydrogens)
        List<IAtomContainer> dichloroethenes2D = sdf("dichloroethenes.sdf", 2);

        assertThat(eConfigurations.size(), is(1));
        assertThat(eConfigurations, hasItem(generator.generate(dichloroethenes2D.get(0))));

        assertThat(zConfigurations.size(), is(1));
        assertThat(zConfigurations, hasItem(generator.generate(dichloroethenes2D.get(1))));
    }

    private static String title(IAtomContainer mol) {
        return mol.getTitle();
    }

    private static String nonEqMesg(IAtomContainer a, IAtomContainer b) {
        return title(a) + " and " + title(b) + " should have different hash codes";
    }

    private static String eqMesg(IAtomContainer a, IAtomContainer b) {
        return title(a) + " and " + title(b) + " should have the same hash codes";
    }

    /**
     * Utility for loading SDFs into a List.
     *
     * @param path absolute path to SDF (classpath)
     * @param exp  expected number of structures
     * @return list of structures
     */
    private List<IAtomContainer> sdf(String path, int exp) {
        InputStream in = getClass().getResourceAsStream(path);

        assertNotNull(path + " could not be found in classpath", in);

        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IteratingSDFReader sdf = new IteratingSDFReader(in, builder, false);
        List<IAtomContainer> structures = new ArrayList<IAtomContainer>(exp);
        while (sdf.hasNext()) {
            IAtomContainer mol = sdf.next();
            try {
                AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
                structures.add(mol);
            } catch (CDKException e) {
                System.err.println(e.getMessage());
            }
        }
        try {
            sdf.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        // help identify if the SDF reader messed up
        assertThat("unexpected number of structures", structures.size(), is(exp));

        return structures;
    }

}
