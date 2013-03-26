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
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.AtomContainerAtomPermutor;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.openscience.cdk.CDKConstants.TITLE;

/**
 * This test class provides several scenario tests for the {@literal cdk-hash}
 * module.
 *
 * @author John May
 * @cdk.module test-hash
 */
public class HashCodeScenarios {

    @BeforeClass
    public static void scenarioInfo() {
        newline();
        hrule();
        print("CDK Hash Code Scenarios - John May, 2013");
        hrule();
        newline();
    }

    @Test public void figure2a() {
        title("Figure 2a - Inlenfeldt and Gasteiger, 93");
        print("Two molecules with identical Racid identification numbers, these hash codes should be different.");
        newline();
        newline();

        List<IAtomContainer> mols = sdf("/data/hash/ihlenfeldt93-figure-2a.sdf", 2);

        IAtomContainer a = mols.get(0);
        IAtomContainer b = mols.get(1);

        MoleculeHashGenerator generator = new HashGeneratorMaker().elemental()
                                                                  .depth(6)
                                                                  .molecular();

        long aHash = generator.generate(a);
        long bHash = generator.generate(b);

        Assert.assertThat(aHash, is(not(bHash)));
        System.out.println(hex(aHash) + " ≠ " + hex(bHash));
    }

    @Test public void figure2b() {
        title("Figure 2b - Inlenfeldt and Gasteiger, 93");
        print("Two molecules with identical Racid identification numbers, these hash codes should be different.");
        newline();
        newline();

        List<IAtomContainer> mols = sdf("/data/hash/ihlenfeldt93-figure-2b.sdf", 2);

        IAtomContainer a = mols.get(0);
        IAtomContainer b = mols.get(1);

        MoleculeHashGenerator generator = new HashGeneratorMaker().elemental()
                                                                  .depth(6)
                                                                  .molecular();

        long aHash = generator.generate(a);
        long bHash = generator.generate(b);

        Assert.assertThat(aHash, is(not(bHash)));
        System.out.println(hex(aHash) + " ≠ " + hex(bHash));
    }

    @Test public void figure2c() {
        title("Figure 2c - Inlenfeldt and Gasteiger, 93");
        print("Two molecules with identical Racid identification numbers, these hash codes should be different.");
        newline();
        newline();

        List<IAtomContainer> mols = sdf("/data/hash/ihlenfeldt93-figure-2c.sdf", 2);

        IAtomContainer a = mols.get(0);
        IAtomContainer b = mols.get(1);

        MoleculeHashGenerator generator = new HashGeneratorMaker().elemental()
                                                                  .depth(6)
                                                                  .molecular();

        long aHash = generator.generate(a);
        long bHash = generator.generate(b);

        Assert.assertThat(aHash, is(not(bHash)));
        System.out.println(hex(aHash) + " ≠ " + hex(bHash));
    }

    @Test public void figure3() {
        title("Figure 3 - Inlenfeldt and Gasteiger, 93");
        print("These two molecules from the original publication collide when using a previous hash coding method (Bawden, 81). The hash codes should be different using this method.");
        newline();
        newline();

        List<IAtomContainer> mols = sdf("/data/hash/ihlenfeldt93-figure-3.sdf", 2);

        IAtomContainer a = mols.get(0);
        IAtomContainer b = mols.get(1);

        MoleculeHashGenerator generator = new HashGeneratorMaker().elemental()
                                                                  .depth(6)
                                                                  .molecular();

        long aHash = generator.generate(a);
        long bHash = generator.generate(b);

        Assert.assertThat(aHash, is(not(bHash)));
        System.out.println(hex(aHash) + " ≠ " + hex(bHash));
    }

    @Test public void figure7() {
        title("Figure 7 - Inlenfeldt and Gasteiger, 93");
        print("These two molecules have atoms experiencing uniform environments but where the number" +
                      " of atoms between the molecules is different. This demonstrates the size the molecule is considered when" +
                      " hashing.");
        newline();
        List<IAtomContainer> mols = sdf("/data/hash/ihlenfeldt93-figure-7.sdf", 2);

        IAtomContainer a = mols.get(0);
        IAtomContainer b = mols.get(1);

        MoleculeHashGenerator generator = new HashGeneratorMaker().elemental()
                                                                  .depth(6)
                                                                  .molecular();

        long aHash = generator.generate(a);
        long bHash = generator.generate(b);

        Assert.assertThat(aHash, is(not(bHash)));
        System.out.println(hex(aHash) + " ≠ " + hex(bHash));
    }

    @Test public void figure10() {
        title("Figure 10 - Inlenfeldt and Gasteiger, 93");
        print("These molecules are erroneous structures from a catalogue file, the German" +
                      " names are the original names as they appear in the catalogue. The hash" +
                      " code identifies that the two molecules are the same.");
        newline();
        newline();

        List<IAtomContainer> mols = sdf("/data/hash/ihlenfeldt93-figure-10.sdf", 2);

        IAtomContainer a = mols.get(0);
        IAtomContainer b = mols.get(1);

        MoleculeHashGenerator generator = new HashGeneratorMaker().elemental()
                                                                  .depth(6)
                                                                  .molecular();

        long aHash = generator.generate(a);
        long bHash = generator.generate(b);

        Assert.assertThat(aHash, is(bHash));
        System.out.println(info(a, aHash));
        System.out.println(info(b, bHash));
    }


    @Test public void figure11() {
        title("Figure 11 - Inlenfeldt and Gasteiger, 93");
        print("This structure is an example where the Cahn-Ingold-Prelog (CIP) rules" +
                      " can not discriminate two neighbours of chiral atom. Due to this, the CIP rules" +
                      " are not used as an atom seed and instead a bootstrap method is used." +
                      " Please refer to the original article for the exact method.");
        newline();
        newline();


        List<IAtomContainer> mols = sdf("/data/hash/ihlenfeldt93-figure-11.sdf", 1);

        IAtomContainer molecule = mols.get(0);

        MoleculeHashGenerator basic = new HashGeneratorMaker().elemental()
                                                              .depth(8)
                                                              .molecular();
        MoleculeHashGenerator stereo = new HashGeneratorMaker().elemental()
                                                               .depth(8)
                                                               .chiral()
                                                               .molecular();

        long basicHash = basic.generate(molecule);
        long stereoHash = stereo.generate(molecule);

        print("If the stereo-centre was perceived then the basic hash should be different from the chiral hash code:");
        System.out.println("\t basic hash code: " + hex(basicHash));
        System.out.println("\tchiral hash code: " + hex(stereoHash));
        System.out.println();

        assertThat(basicHash, is(not(stereoHash)));
    }

    @Test public void figure12() {
        List<IAtomContainer> mols = sdf("/data/hash/ihlenfeldt93-figure-12.sdf", 2);

        title("Scenario - invariant stereo-chemistry");
        print("This scenario demonstrates how stereo-chemistry encoding is" +
                      " invariant under permutation. A simple molecule 'bromo(chloro)fluoromethane' is" +
                      " permuted to all 120 possible atom orderings. It is checked that the (R)- configuration" +
                      " and (S)- configuration values are invariant.");
        print("Note: 32-bit hash codes shown for clarity.");
        newline();
        newline();

        MoleculeHashGenerator stereo = new HashGeneratorMaker().elemental()
                                                               .depth(1)
                                                               .chiral()
                                                               .molecular();


        Set<Long> sHashes = new HashSet<Long>();
        Set<Long> rHashes = new HashSet<Long>();

        AtomContainerAtomPermutor rpermutor = new AtomContainerAtomPermutor(mols.get(0));
        AtomContainerAtomPermutor spermutor = new AtomContainerAtomPermutor(mols.get(1));

        System.out.println("            Order\t  (R)-form\t  (S)-form");

        while (rpermutor.hasNext() && spermutor.hasNext()) {
            IAtomContainer r = rpermutor.next();
            IAtomContainer s = spermutor.next();
            System.out.println(atomOrder(r) + "\t" + hex32(stereo.generate(r)) + "\t" + hex32(stereo.generate(s)));
            sHashes.add(stereo.generate(s));
            rHashes.add(stereo.generate(r));
        }
        Assert.assertThat(sHashes.size(), CoreMatchers.is(1));
        Assert.assertThat(rHashes.size(), CoreMatchers.is(1));
        sHashes.addAll(rHashes);
        Assert.assertThat(sHashes.size(), CoreMatchers.is(2));
    }

    @Test public void figure13a() {
        title("Figure 13a - Inlenfeldt and Gasteiger, 93");
        print("This molecule has a tetrahedral stereo-centre depends on the configuration of two double bonds. Swapping" +
                      " the double bond configuration inverts the tetrahedral stereo-centre (R/S) and produces different hash codes.");
        newline();
        newline();

        List<IAtomContainer> mols = sdf("/data/hash/ihlenfeldt93-figure-13a.sdf", 2);

        IAtomContainer a = mols.get(0);
        IAtomContainer b = mols.get(1);

        MoleculeHashGenerator stereo = new HashGeneratorMaker().elemental()
                                                               .depth(8)
                                                               .chiral()
                                                               .molecular();
        long aHash = stereo.generate(a);
        long bHash = stereo.generate(b);

        print(info(a, aHash));
        print(info(b, bHash));
        assertThat(aHash, is(not(bHash)));
    }

    @Test public void figure13b() {
        title("Figure 13b - Inlenfeldt and Gasteiger, 93");
        print("This molecule has double bond stereo chemistry defined only by differences in the configurations of it's substituents. The" +
                      " two configurations the bond can take (Z/E) and should produce different hash codes.");
        newline();
        newline();

        List<IAtomContainer> mols = sdf("/data/hash/ihlenfeldt93-figure-13b.sdf", 2);

        IAtomContainer a = mols.get(0);
        IAtomContainer b = mols.get(1);

        MoleculeHashGenerator stereo = new HashGeneratorMaker().elemental()
                                                               .depth(8)
                                                               .chiral()
                                                               .molecular();

        print(info(a, stereo.generate(a)));
        print(info(b, stereo.generate(b)));
        assertThat(stereo.generate(a), is(not(stereo.generate(b))));
    }

    @Test public void figure14() {
        title("Figure 14 - Inlenfeldt and Gasteiger, 93");
        print("These two structures were found in the original publication as duplicates in the catalogue of the CHIRON program. The article notes the" +
                      " second name is likely incorrect but that this is how it appears in the catalogue. The two molecules are in fact the same and" +
                      " generate the same hash code.");
        newline();
        newline();

        List<IAtomContainer> mols = sdf("/data/hash/ihlenfeldt93-figure-14.sdf", 2);

        IAtomContainer a = mols.get(0);
        IAtomContainer b = mols.get(1);

        MoleculeHashGenerator generator = new HashGeneratorMaker().elemental()
                                                                  .depth(6)
                                                                  .molecular();

        long aHash = generator.generate(a);
        long bHash = generator.generate(b);

        Assert.assertThat(aHash, is(bHash));
        System.out.println(info(a, aHash));
        System.out.println(info(b, bHash));
    }

    @Test public void figure15() {
        title("Figure 15 - Inlenfeldt and Gasteiger, 93");

        List<IAtomContainer> mols = sdf("/data/hash/ihlenfeldt93-figure-15.sdf", 2);

        IAtomContainer a = mols.get(0);
        IAtomContainer b = mols.get(1);

        MoleculeHashGenerator generator = new HashGeneratorMaker().elemental()
                                                                  .depth(6)
                                                                  .molecular();
        print("These two compounds are connected differently but produce the same basic hash code.");
        newline();
        newline();

        long aHash = generator.generate(a);
        long bHash = generator.generate(b);

        print(info(a, aHash));
        print(info(b, bHash));
        Assert.assertThat(aHash, is(bHash));


        MoleculeHashGenerator perturbed = new HashGeneratorMaker().elemental()
                                                                  .depth(6)
                                                                  .perturbed()
                                                                  .molecular();
        aHash = perturbed.generate(a);
        bHash = perturbed.generate(b);
        newline();
        print("In order to discriminate them we must use the perturbed hash code.");
        newline();
        newline();
        print(info(a, aHash));
        print(info(b, bHash));
        Assert.assertThat(aHash, is(not(bHash)));

    }

    @Test public void figure16a() {
        title("Figure 16a - Inlenfeldt and Gasteiger, 93");
        print("The molecules cubane and cuneane have the same number of atoms all of which experience the same" +
                      " environment in the first sphere.");
        newline();
        newline();

        List<IAtomContainer> mols = sdf("/data/hash/ihlenfeldt93-figure-16a.sdf", 2);

        IAtomContainer a = mols.get(0);
        IAtomContainer b = mols.get(1);

        MoleculeHashGenerator nonperturbed = new HashGeneratorMaker()
                .elemental()
                .depth(6)
                .molecular();
        MoleculeHashGenerator perturbed = new HashGeneratorMaker().elemental()
                                                                  .depth(6)
                                                                  .perturbed()
                                                                  .molecular();

        print("Using a non-perturbed hash code, these will hash to the same value.");
        newline(); newline();

        long aHash = nonperturbed.generate(a);
        long bHash = nonperturbed.generate(b);
        System.out.println(info(a, aHash));
        System.out.println(info(b, bHash));
        Assert.assertThat(aHash, is(bHash));

        print("The perturbed hash code, allows us to discriminate them.");
        newline();

        aHash = perturbed.generate(a);
        bHash = perturbed.generate(b);
        System.out.println(info(a, aHash));
        System.out.println(info(b, bHash));
        Assert.assertThat(aHash, is(not(bHash)));


        AtomHashGenerator perturbedAtomic = new HashGeneratorMaker().elemental()
                                                                    .depth(3)
                                                                    .perturbed()
                                                                    .atomic();
        print("The atoms of cubane are all equivalent, we can tell this using an atomic hash code");
        print("Note: 32-bit atom hash codes shown for clarity.");
        newline();
        long[] aHashes = perturbedAtomic.generate(a);
        long[] bHashes = perturbedAtomic.generate(b);
        print(info(a, aHashes));
        newline();
        print("The atoms of cuneane are split into three equivalent classes");
        print(info(b, bHashes));
        newline();

        // cubane has 1 equivalent class
        assertThat(toSet(aHashes).size(), is(1));
        // cuneane has 3 equivalent class
        assertThat(toSet(bHashes).size(), is(3));
    }

    private Set<Long> toSet(long[] xs){
        Set<Long> set = new HashSet<Long>();
        for(long x : xs){
            set.add(x);
        }
        return set;
    }

    @Test public void figure16b() {
        title("Figure 16a - Inlenfeldt and Gasteiger, 93");
        print("A chlorinated cubane and cuneane can not be told apart by the basic hash code. However using perturbed hash codes" +
                      " is is possible to tell them apart as well as the 3 different chlorination locations on the cuneane");
        newline();
        newline();
        List<IAtomContainer> mols = sdf("/data/hash/ihlenfeldt93-figure-16b.sdf", 4);

        IAtomContainer a = mols.get(0);
        IAtomContainer b = mols.get(1);
        IAtomContainer c = mols.get(2);
        IAtomContainer d = mols.get(3);

        MoleculeHashGenerator generator = new HashGeneratorMaker().elemental()
                                                                  .depth(6)
                                                                  .perturbed()
                                                                  .molecular();

        long aHash = generator.generate(a);
        long bHash = generator.generate(b);
        long cHash = generator.generate(c);
        long dHash = generator.generate(d);

        Assert.assertThat(aHash, is(not(bHash)));
        Assert.assertThat(aHash, is(not(cHash)));
        Assert.assertThat(aHash, is(not(dHash)));
        Assert.assertThat(bHash, is(not(cHash)));
        Assert.assertThat(bHash, is(not(dHash)));
        Assert.assertThat(cHash, is(not(dHash)));

        System.out.println(info(a, aHash));
        System.out.println(info(b, bHash));
        System.out.println(info(c, cHash));
        System.out.println(info(d, dHash));
    }


    private String atomOrder(IAtomContainer mol) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        Iterator<IAtom> it = mol.atoms().iterator();
        while (it.hasNext()) {
            sb.append(it.next().getSymbol());
            if(it.hasNext())
                sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * This test demonstrates the importance of choosing the correct depth. The
     * two molecules only differ in chain length.
     */
    @Test public void aminotetracosanone() {

        List<IAtomContainer> aminotetracosanones = sdf("/data/hash/aminotetracosanones.sdf", 2);

        title("Scenario - varying depth");
        print("This scenario demonstrates how the depth influences the hash code. These " +
                      "two molecules differ only by length of their aliphatic chains. One" +
                      " has chains of length 10 and 11 and other of length 11 and 10 (connected" +
                      " the other way). To tell these apart the depth must be large enough to propagate" +
                      " the environments from the ends of both chains.");
        newline();
        newline();

        IAtomContainer a = aminotetracosanones.get(0);
        IAtomContainer b = aminotetracosanones.get(1);

        print("The two molecules are " + a.getProperty(TITLE) + " and " + b
                .getProperty(TITLE)
                      + ". Below, their hash codes are shown at increasing depth.");
        newline();

        for (int depth = 0; depth < 12; depth++) {
            MoleculeHashGenerator basic = new HashGeneratorMaker().elemental()
                                                                  .depth(depth)
                                                                  .molecular();
            long aHash = basic.generate(a);
            long bHash = basic.generate(b);

            if (depth < 7) {
                print("At depth " + depth + ": " + hex(aHash) + " = " + hex(bHash));
                assertThat(aHash, is(bHash));
            } else {
                print("At depth " + depth + ": " + hex(aHash) + " ≠ " + hex(bHash));
                assertThat(aHash, is(not(bHash)));
            }
        }

    }

    /**
     * This test demonstrates that the nine stereo isomers of inositol can be
     * hashed to the same value or to different values.
     *
     * @see <a href="http://en.wikipedia.org/wiki/Inositol#Isomers_and_structure">Inositol
     *      Isomers</a>
     */
    @Test public void inositols() {

        List<IAtomContainer> inositols = sdf("/data/hash/inositols.sdf", 9);

        title("Scenario - inositols isomers");
        print("There are nine stereo-isomers of inositol, using a basic hash " +
                      "generator they will all produce the same value.");
        newline();
        newline();

        // non-stereo non-perturbed hash generator
        MoleculeHashGenerator basic = new HashGeneratorMaker().elemental()
                                                              .depth(6)
                                                              .molecular();

        Set<Long> hashes = new HashSet<Long>(5);

        for (IAtomContainer inositol : inositols) {
            long hash = basic.generate(inositol);
            System.out.println(info(inositol, hash));
            hashes.add(hash);
        }

        assertThat("all isomers should hash to the same value",
                   hashes.size(), is(1));

        // stereo non-perturbed hash generator
        MoleculeHashGenerator stereo = new HashGeneratorMaker().elemental()
                                                               .depth(6)
                                                               .chiral()
                                                               .molecular();
        hashes.clear();

        print("When we using 'chiral()' hash codes we still get the same values.");
        newline();
        for (IAtomContainer inositol : inositols) {
            long hash = stereo.generate(inositol);
            System.out.println(info(inositol, hash));
            hashes.add(hash);
        }

        assertThat("all isomers should hash to the same value",
                   hashes.size(), is(1));

        // stereo non-perturbed hash generator
        MoleculeHashGenerator perturbed = new HashGeneratorMaker().elemental()
                                                                  .depth(6)
                                                                  .chiral()
                                                                  .perturbed()
                                                                  .molecular();
        hashes.clear();

        print("The six chiral carbons of inositol experience uniform environments." +
                      " To tell the 9 isomers apart be must systematically 'perturb()' the " +
                      "hash codes.");
        newline();
        for (IAtomContainer inositol : inositols) {
            long hash = perturbed.generate(inositol);
            System.out.println(info(inositol, hash));
            hashes.add(hash);
        }

        assertThat("all isomers should hash to different values",
                   hashes.size(), is(9));

    }

    /**
     * Print a 64-bit long as a hex value and pads with leading 0s.
     *
     * @param hash a hex value
     * @return hex string of a hex value
     */
    private String hex(long hash) {
        String hex = Long.toHexString(hash);
        String prefix = "0x0000000000000000";
        int n = prefix.length() - hex.length();
        return prefix.substring(0, n) + hex;
    }

    private String hex32(long hash) {
        String hex = Integer.toHexString(Long.valueOf(hash).hashCode());
        String prefix = "0x00000000";
        int n = prefix.length() - hex.length();
        return prefix.substring(0, n) + hex;
    }

    private String hex32(long[] hashes) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < hashes.length; i++) {
            sb.append(hex32(hashes[i]));
            if ((i + 1) < hashes.length) {
                sb.append(", ");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    private static void title(String title) {
        newline();
        newline();
        print(title);
        hrule();
        newline();
    }

    private static void print(String info) {
        System.out.println(wrap(info, 76));
    }

    private static String wrap(String sentence, int target) {
        String[] words = sentence.split("(?:\\t+|\\n+|\\s+)");

        StringBuilder sb = new StringBuilder(sentence.length() + 20);
        int lineLength = 0;
        for (String word : words) {
            if (lineLength == 0) {
                sb.append(word);
                lineLength = word.length();
            } else if (lineLength + 1 + word.length() < target) {
                sb.append(" ").append(word);
                lineLength += word.length() + 1;
            } else {
                lineLength = word.length();
                sb.append(System.getProperty("line.separator")).append(word);
            }
        }

        return sb.toString();
    }

    private static void hrule() {
        print("---------------------------------------------------------------------------");
    }

    private static void newline() {
        print(System.getProperty("line.separator"));
    }

    /**
     * Print formatted molecule title (padded) and the hash value.
     *
     * @param mol  molecule with a title
     * @param hash a hash value
     * @return formatted info for printing
     */
    private String info(IAtomContainer mol, long hash) {
        String title = mol.getProperty(TITLE);
        if (title.length() > 30)
            return title + ":\n    " + hex(hash);
        else
            return String.format("%30s: ", title, toString()) + hex(hash);
    }

    private String info(IAtomContainer mol, long[] hashes) {
        String title = mol.getProperty(TITLE);
        if (title.length() > 30)
            return title + ":\n    " + hex32(hashes);
        else
            return String.format("%30s: ", title, toString()) + hex32(hashes);
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
                AtomContainerManipulator
                        .percieveAtomTypesAndConfigureAtoms(mol);
                structures.add(mol);
            } catch (CDKException e) {
                System.err.println(e.getMessage());
            }
        }

        // help identify if the SDF reader messed up
        assertThat("unexpected number of structures",
                   structures.size(),
                   is(exp));

        return structures;
    }

}
