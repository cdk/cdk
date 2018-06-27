/*
 * Copyright (c) 2013 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.graph.invariant;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * An implementation based on the canon algorithm {@cdk.cite WEI89}. The
 * algorithm uses an initial set of of invariants which are assigned a rank.
 * Equivalent ranks are then shattered using an unambiguous function (in this
 * case, the product of primes of adjacent ranks). Once no more equivalent ranks
 * can be shattered ties are artificially broken and rank shattering continues.
 * Unlike the original description rank stability is not maintained reducing
 * the number of values to rank at each stage to only those which are equivalent.
 * 
 *
 * The initial set of invariants is basic and are - <i>
 * "sufficient for the purpose of obtaining unique notation for simple SMILES,
 *  but it is not necessarily a “complete” set. No “perfect” set of invariants
 *  is known that will distinguish all possible graph asymmetries. However,
 *  for any given set of structures, a set of invariants can be devised to
 *  provide the necessary discrimination"</i> {@cdk.cite WEI89}. As such this
 *  producer should not be considered a complete canonical labelled but in
 *  practice performs well. For a more accurate and computationally expensive
 *  labelling, please using the {@link InChINumbersTools}.
 *
 * <blockquote><pre>
 * IAtomContainer m = ...;
 * int[][]        g = GraphUtil.toAdjList(m);
 *
 * // obtain canon labelling
 * long[] labels = Canon.label(m, g);
 *
 * // obtain symmetry classes
 * long[] labels = Canon.symmetry(m, g);
 * </pre></blockquote>
 *
 * @author John May
 * @cdk.module standard
 * @cdk.githash
 */
public final class Canon {

    private static final int N_PRIMES = 10000;
    /**
     * Graph, adjacency list representation.
     */
    private final int[][] g;

    /**
     * Storage of canon labelling and symmetry classes.
     */
    private final long[] labelling, symmetry;

    /** Only compute the symmetry classes. */
    private boolean symOnly = false;

    /**
     * Create a canon labelling for the graph (g) with the specified
     * invariants.
     *
     * @param g         a graph (adjacency list representation)
     * @param hydrogens binary vector of terminal hydrogens
     * @param partition an initial partition of the vertices
     */
    private Canon(int[][] g, long[] partition, boolean[] hydrogens, boolean symOnly) {
        this.g = g;
        this.symOnly = symOnly;
        labelling = partition.clone();
        symmetry = refine(labelling, hydrogens);
    }

    /**
     * Compute the canonical labels for the provided structure. The labelling
     * does not consider isomer information or stereochemistry. The current
     * implementation does not fully distinguish all structure topologies
     * but in practise performs well in the majority of cases. A complete
     * canonical labelling can be obtained using the {@link InChINumbersTools}
     * but is computationally much more expensive.
     *
     * @param container structure
     * @param g         adjacency list graph representation
     * @return the canonical labelling
     * @see EquivalentClassPartitioner
     * @see InChINumbersTools
     */
    public static long[] label(IAtomContainer container, int[][] g) {
        return label(container, g, basicInvariants(container, g));
    }

    /**
     * Compute the canonical labels for the provided structure. The labelling
     * does not consider isomer information or stereochemistry. This method
     * allows provision of a custom array of initial initial.
     *
     * 
     * The current
     * implementation does not fully distinguish all structure topologies
     * but in practise performs well in the majority of cases. A complete
     * canonical labelling can be obtained using the {@link InChINumbersTools}
     * but is computationally much more expensive.
     *
     * @param container  structure
     * @param g          adjacency list graph representation
     * @param initial    initial seed invariants
     * @return the canonical labelling
     * @see EquivalentClassPartitioner
     * @see InChINumbersTools
     */
    public static long[] label(IAtomContainer container, int[][] g, long[] initial) {
        if (initial.length != g.length)
            throw new IllegalArgumentException("number of initial != number of atoms");
        return new Canon(g, initial, terminalHydrogens(container, g), false).labelling;
    }

    /**
     * Compute the canonical labels for the provided structure. The initial
     * labelling is seed-ed with the provided atom comparator <code>cmp</code>
     * allowing arbitary properties to be distinguished or ignored.
     *
     * @param container  structure
     * @param g          adjacency list graph representation
     * @param cmp        comparator to compare atoms
     * @return the canonical labelling
     */
    public static long[] label(IAtomContainer    container,
                               int[][]           g,
                               Comparator<IAtom> cmp) {
        if (g.length == 0)
            return new long[0];
        IAtom[] atoms = AtomContainerManipulator.getAtomArray(container);
        Arrays.sort(atoms, cmp);
        long[] initial = new long[atoms.length];
        long   part    = 1;
        initial[container.indexOf(atoms[0])] = part;
        for (int i=1; i<atoms.length; i++) {
            if (cmp.compare(atoms[i], atoms[i-1]) != 0)
                ++part;
            initial[container.indexOf(atoms[i])] = part;
        }
        return label(container, g, initial);
    }

    /**
     * Compute the symmetry classes for the provided structure. There are known
     * examples where symmetry is incorrectly found. The {@link
     * EquivalentClassPartitioner} gives more accurate symmetry perception but
     * this method is very quick and in practise successfully portions the
     * majority of chemical structures.
     *
     * @param container structure
     * @param g         adjacency list graph representation
     * @return symmetry classes
     * @see EquivalentClassPartitioner
     */
    public static long[] symmetry(IAtomContainer container, int[][] g) {
        return new Canon(g, basicInvariants(container, g), terminalHydrogens(container, g), true).symmetry;
    }

    /**
     * Internal - refine invariants to a canonical labelling and
     * symmetry classes.
     *
     * @param invariants the invariants to refine (canonical labelling gets
     *                   written here)
     * @param hydrogens  binary vector of terminal hydrogens
     * @return the symmetry classes
     */
    private long[] refine(long[] invariants, boolean[] hydrogens) {

        int ord = g.length;

        InvariantRanker ranker = new InvariantRanker(ord);

        // current/next vertices, these only hold the vertices which are
        // equivalent
        int[] currVs = new int[ord];
        int[] nextVs = new int[ord];

        // fill with identity (also set number of non-unique)
        int nnu = ord;
        for (int i = 0; i < ord; i++)
            currVs[i] = i;

        long[] prev = invariants;
        long[] curr = Arrays.copyOf(invariants, ord);

        // initially all labels are 1, the input invariants are then used to
        // refine this coarse partition
        Arrays.fill(prev, 1L);

        // number of ranks
        int n = 0, m = 0;

        // storage of symmetry classes
        long[] symmetry = null;

        while (n < ord) {

            // refine the initial invariants using product of primes from
            // adjacent ranks
            while ((n = ranker.rank(currVs, nextVs, nnu, curr, prev)) > m && n < ord) {
                nnu = 0;
                for (int i = 0; i < ord && nextVs[i] >= 0; i++) {
                    int v = nextVs[i];
                    currVs[nnu++] = v;
                    curr[v] = hydrogens[v] ? prev[v] : primeProduct(g[v], prev, hydrogens);
                }
                m = n;
            }

            if (symmetry == null) {

                // After symmetry classes have been found without hydrogens we add
                // back in the hydrogens and assign ranks. We don't refine the
                // partition until the next time round the while loop to avoid
                // artificially splitting due to hydrogen representation, for example
                // the two hydrogens are equivalent in this SMILES for ethane '[H]CC'
                for (int i = 0; i < g.length; i++) {
                    if (hydrogens[i]) {
                        curr[i] = prev[g[i][0]];
                        hydrogens[i] = false;
                    }
                }
                n = ranker.rank(currVs, nextVs, nnu, curr, prev);
                symmetry = Arrays.copyOf(prev, ord);

                // Update the buffer of non-unique vertices as hydrogens next
                // to discrete heavy atoms are also discrete (and removed from
                // 'nextVs' during ranking.
                nnu = 0;
                for (int i = 0; i < ord && nextVs[i] >= 0; i++) {
                    currVs[nnu++] = nextVs[i];
                }
            }

            // partition is discrete or only symmetry classes are needed
            if (symOnly || n == ord) return symmetry;

            // artificially split the lowest cell, we perturb the value
            // of all vertices with equivalent rank to the lowest non-unique
            // vertex
            int lo = nextVs[0];
            for (int i = 1; i < ord && nextVs[i] >= 0 && prev[nextVs[i]] == prev[lo]; i++)
                prev[nextVs[i]]++;

            // could also swap but this is cleaner
            System.arraycopy(nextVs, 0, currVs, 0, nnu);
        }

        return symmetry;
    }

    /**
     * Compute the prime product of the values (ranks) for the given
     * adjacent neighbors (ws).
     *
     * @param ws    indices (adjacent neighbors)
     * @param ranks invariant ranks
     * @return the prime product
     */
    private long primeProduct(int[] ws, long[] ranks, boolean[] hydrogens) {
        long prod = 1;
        for (int w : ws) {
            if (!hydrogens[w]) {
                prod *= PRIMES[(int) ranks[w]];
            }
        }
        return prod;
    }

    /**
     * Generate the initial invariants for each atom in the {@code container}.
     * The labels use the invariants described in {@cdk.cite WEI89}. 
     *
     * The bits in the low 32-bits are: {@code 0000000000xxxxXXXXeeeeeeescchhhh}
     * where:
     * <ul>
     *     <li>0: padding</li>
     *     <li>x: number of connections</li>
     *     <li>X: number of non-hydrogens bonds</li>
     *     <li>e: atomic number</li>
     *     <li>s: sign of charge</li>
     *     <li>c: absolute charge</li>
     *     <li>h: number of attached hydrogens</li>
     * </ul>
     *
     * <b>Important: These invariants are <i>basic</i> and there are known
     * examples they don't distinguish. One trivial example to consider is
     * {@code [O]C=O} where both oxygens have no hydrogens and a single
     * connection but the atoms are not equivalent. Including a better
     * initial partition is more expensive</b>
     *
     * @param container an atom container to generate labels for
     * @param graph     graph representation (adjacency list)
     * @return initial invariants
     * @throws NullPointerException an atom had unset atomic number, hydrogen
     *                              count or formal charge
     */
    public static long[] basicInvariants(IAtomContainer container, int[][] graph) {

        long[] labels = new long[graph.length];

        for (int v = 0; v < graph.length; v++) {
            IAtom atom = container.getAtom(v);

            int deg = graph[v].length;
            int impH = implH(atom);
            int expH = 0;
            int elem = atomicNumber(atom);
            int chg = charge(atom);

            // count non-suppressed (explicit) hydrogens
            for (int w : graph[v])
                if (atomicNumber(container.getAtom(w)) == 1) expH++;

            long label = 0; // connectivity (first in)
            label |= deg + impH & 0xf;
            label <<= 4; // connectivity (heavy) <= 15 (4 bits)
            label |= deg - expH & 0xf;
            label <<= 7; // atomic number <= 127 (7 bits)
            label |= elem & 0x7f;
            label <<= 1; // charge sign == 1 (1 bit)
            label |= chg >> 31 & 0x1;
            label <<= 2; // charge <= 3 (2 bits)
            label |= Math.abs(chg) & 0x3;
            label <<= 4; // hydrogen count <= 15 (4 bits)
            label |= impH + expH & 0xf;

            labels[v] = label;
        }
        return labels;
    }

    /**
     * Access atomic number of atom defaulting to 0 for pseudo atoms.
     *
     * @param atom an atom
     * @return the atomic number
     * @throws NullPointerException the atom was non-pseudo at did not have an
     *                              atomic number
     */
    private static int atomicNumber(IAtom atom) {
        Integer elem = atom.getAtomicNumber();
        if (elem != null) return elem;
        if (atom instanceof IPseudoAtom) return 0;
        throw new NullPointerException("a non-pseudoatom had unset atomic number");
    }

    /**
     * Access implicit hydrogen count of the atom defaulting to 0 for pseudo
     * atoms.
     *
     * @param atom an atom
     * @return the implicit hydrogen count
     * @throws NullPointerException the atom was non-pseudo at did not have an
     *                              implicit hydrogen count
     */
    private static int implH(IAtom atom) {
        Integer h = atom.getImplicitHydrogenCount();
        if (h != null) return h;
        if (atom instanceof IPseudoAtom) return 0;
        throw new NullPointerException("a non-pseudoatom had unset hydrogen count");
    }

    /**
     * Access formal charge of an atom defaulting to 0 if undefined.
     *
     * @param atom an atom
     * @return the formal charge
     */
    private static int charge(IAtom atom) {
        Integer charge = atom.getFormalCharge();
        if (charge != null) return charge;
        return 0;
    }

    /**
     * Locate explicit hydrogens that are attached to exactly one other atom.
     *
     * @param ac a structure
     * @return binary set of terminal hydrogens
     */
    static boolean[] terminalHydrogens(final IAtomContainer ac, final int[][] g) {

        final boolean[] hydrogens = new boolean[ac.getAtomCount()];

        // we specifically don't check for null atomic number, this must be set.
        // if not, something major is wrong
        for (int i = 0; i < ac.getAtomCount(); i++) {
            IAtom atom = ac.getAtom(i);
            hydrogens[i] = atom.getAtomicNumber() == 1 &&
                           atom.getMassNumber() == null &&
                           g[i].length == 1;
        }

        return hydrogens;
    }

    /**
     * The first 10,000 primes.
     */
    private static final int[] PRIMES = loadPrimes();

    private static int[] loadPrimes() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Canon.class.getResourceAsStream("primes.dat")))) {
            int[] primes = new int[N_PRIMES];
            int i = 0;
            String line = null;
            while ((line = br.readLine()) != null) {
                primes[i++] = Integer.parseInt(line);
            }
            assert i == N_PRIMES;
            return primes;
        } catch (NumberFormatException | IOException e) {
            System.err.println("Critical - could not load primes table for canonical labelling!");
            return new int[0];
        }
    }
}
