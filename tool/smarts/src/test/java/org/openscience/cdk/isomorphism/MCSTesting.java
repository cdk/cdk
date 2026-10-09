/* Copyright (C) 2026  Syed Asad Rahman <s9asad@gmail.com>
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
 */
package org.openscience.cdk.isomorphism;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import org.junit.jupiter.api.Assertions;
import org.openscience.cdk.AtomRef;
import org.openscience.cdk.BondRef;
import org.openscience.cdk.exception.Intractable;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import org.openscience.cdk.silent.Atom;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smarts.Smarts;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.stereo.DoubleBondStereochemistry;
import org.openscience.cdk.stereo.TetrahedralChirality;

/**
 * Helpers shared by the MCS tests, and a brute force search over all atom
 * mappings to check the searches against. A mapping is compared as the
 * string {@link Arrays#toString(int[])}, and results as sets of these, never
 * by their order.
 *
 * @author Syed Asad Rahman
 */
final class MCSTesting {

    // the group of a random configuration, see withConfigurations
    private static final int[] GROUPS = {0, 0, 0, IStereoElement.GRP_RAC1, IStereoElement.GRP_RAC2,
                                         IStereoElement.GRP_REL1};

    /** The amino acids other than glycine, by one-letter code, and their side chains, for {@link #peptide}. */
    private static final String   RESIDUES    = "AVLIFYWSTCMNQDEKRH";
    private static final String[] SIDE_CHAINS = {"C", "C(C)C", "CC(C)C", "C(C)CC", "Cc1ccccc1", "Cc1ccc(O)cc1",
            "Cc1c[nH]c2ccccc12", "CO", "C(C)O", "CS", "CCSC", "CC(N)=O", "CCC(N)=O", "CC(=O)O", "CCC(=O)O", "CCCCN",
            "CCCNC(=N)N", "Cc1c[nH]cn1"};

    private MCSTesting() {
    }

    static IAtomContainer smi(String smiles) throws InvalidSmilesException {
        return new SmilesParser(SilentChemObjectBuilder.getInstance()).parseSmiles(smiles);
    }

    static IAtomContainer smarts(String smarts) {
        IAtomContainer query = SilentChemObjectBuilder.getInstance().newAtomContainer();
        Assertions.assertTrue(Smarts.parse(query, smarts), smarts);
        return query;
    }

    /** A SMILES chain of {@code count} carbon atoms. */
    static String carbons(int count) {
        char[] chain = new char[count];
        Arrays.fill(chain, 'C');
        return new String(chain);
    }

    /** A linear peptide from its one-letter sequence, without stereochemistry. */
    static String peptide(String sequence) {
        StringBuilder smiles = new StringBuilder();
        for (char residue : sequence.toCharArray()) {
            smiles.append("NC");
            if (residue != 'G') {
                smiles.append('(').append(SIDE_CHAINS[RESIDUES.indexOf(residue)]).append(')');
            }
            smiles.append("C(=O)");
        }
        return smiles.append('O').toString();
    }

    /** The number of mapped query atoms. */
    static int atoms(int[] mapping) {
        int count = 0;
        for (int x : mapping) {
            if (x >= 0) {
                count++;
            }
        }
        return count;
    }

    /**
     * The query bonds mapped onto target bonds they match: a query bond by its
     * expression, any other with {@code orders} by the default rule.
     */
    static int commonBonds(IAtomContainer query, IAtomContainer target, int[] mapping, boolean orders) {
        int count = 0;
        for (IBond bond : query.bonds()) {
            if (common(bond, image(query, target, mapping, bond), orders)) {
                count++;
            }
        }
        return count;
    }

    /**
     * The score of a mapping, atoms &lt;&lt; 32 | common bonds, or -1 unless its
     * common bonds connect its atoms; with {@code minBonds} above 0 common
     * bonds &lt;&lt; 32 | atoms, or -1 unless the common bonds form fragments of
     * at least {@code minBonds} bonds that every mapped atom is in. Bonds are
     * common as for {@link #commonBonds}.
     */
    static long score(IAtomContainer query, IAtomContainer target, int[] mapping, boolean orders, int minBonds) {
        // the parts the common bonds join the mapped atoms into, and the common bonds of each
        int[] part = new int[mapping.length];
        int[] partBonds = new int[mapping.length];
        int atoms = atoms(mapping);
        int parts = atoms;
        int common = 0;
        for (int i = 0; i < part.length; i++) {
            part[i] = i;
        }
        for (IBond bond : query.bonds()) {
            if (common(bond, image(query, target, mapping, bond), orders)) {
                int a = find(part, query.indexOf(bond.getBegin()));
                int b = find(part, query.indexOf(bond.getEnd()));
                if (a != b) {
                    part[b] = a;
                    partBonds[a] += partBonds[b];
                    parts--;
                }
                partBonds[a]++;
                common++;
            }
        }
        for (int i = 0; i < mapping.length; i++) {
            if (mapping[i] >= 0 && (minBonds == 0 ? parts > 1 : partBonds[find(part, i)] < minBonds)) {
                return -1;
            }
        }
        return atoms == 0 ? -1 : minBonds == 0 ? (long) atoms << 32 | common : (long) common << 32 | atoms;
    }

    private static int find(int[] part, int i) {
        while (part[i] != i) {
            i = part[i] = part[part[i]];
        }
        return i;
    }

    /** The default bond rule: both aromatic, or both not aromatic with the same order. */
    static boolean bondsMatch(IBond a, IBond b) {
        if (a.isAromatic() || b.isAromatic()) {
            return a.isAromatic() && b.isAromatic();
        }
        return a.getOrder() == b.getOrder();
    }

    static Set<String> keys(Collection<int[]> mappings) {
        Set<String> keys = new HashSet<>();
        for (int[] mapping : mappings) {
            keys.add(Arrays.toString(mapping));
        }
        return keys;
    }

    /**
     * Checks that a mapping has one entry per query atom, is one to one, maps
     * atoms that match, a query atom by its expression and any other by its
     * symbol, and that the common bonds connect the mapped atoms.
     */
    static void assertValid(IAtomContainer query, IAtomContainer target, int[] mapping, boolean orders) {
        Assertions.assertEquals(query.getAtomCount(), mapping.length, "one entry per query atom");
        Set<Integer> used = new HashSet<>();
        List<Integer> queue = new ArrayList<>();
        for (int i = 0; i < mapping.length; i++) {
            if (mapping[i] >= 0) {
                Assertions.assertTrue(mapping[i] < target.getAtomCount(), "target index in range");
                Assertions.assertTrue(used.add(mapping[i]), "one to one");
                IAtom a = query.getAtom(i);
                IAtom b = target.getAtom(mapping[i]);
                Assertions.assertTrue(a instanceof IQueryAtom ? ((IQueryAtom) a).matches(b)
                                                              : Objects.equals(a.getSymbol(), b.getSymbol()),
                                      "atoms match");
                if (queue.isEmpty()) {
                    queue.add(i);
                }
            }
        }
        Set<Integer> seen = new HashSet<>(queue);
        for (int k = 0; k < queue.size(); k++) {
            IAtom atom = query.getAtom(queue.get(k));
            for (IBond bond : query.getConnectedBondsList(atom)) {
                IBond other = image(query, target, mapping, bond);
                int nbr = query.indexOf(bond.getOther(atom));
                if (common(bond, other, orders) && seen.add(nbr)) {
                    queue.add(nbr);
                }
            }
        }
        Assertions.assertEquals(used.size(), seen.size(), "connected by common bonds");
    }

    /** Puts the atoms and bonds in a random order, with their stereo; returns the old index of each new atom index. */
    static int[] shuffle(IAtomContainer container, Random random) {
        List<IAtom> atoms = new ArrayList<>();
        for (IAtom atom : container.atoms()) {
            atoms.add(AtomRef.deref(atom));
        }
        List<IBond> bonds = new ArrayList<>();
        for (IBond bond : container.bonds()) {
            bonds.add(BondRef.deref(bond));
        }
        List<IAtom> before = new ArrayList<>(atoms);
        List<IStereoElement<?, ?>> stereo = new ArrayList<>();
        container.stereoElements().forEach(stereo::add);
        Collections.shuffle(atoms, random);
        Collections.shuffle(bonds, random);
        container.setAtoms(atoms.toArray(new IAtom[0]));
        container.setBonds(bonds.toArray(new IBond[0]));
        // the stereo elements are dropped with the old atoms and bonds, and put back on the new ones
        container.setStereoElements(new ArrayList<>(stereo));
        int[] order = new int[atoms.size()];
        for (int i = 0; i < order.length; i++) {
            order[i] = before.indexOf(atoms.get(i));
        }
        return order;
    }

    /** A mapping of shuffled molecules in the atom order of the originals, see {@link #shuffle}. */
    static int[] unshuffle(int[] mapping, int[] queryOrder, int[] targetOrder) {
        int[] original = new int[mapping.length];
        for (int i = 0; i < mapping.length; i++) {
            original[queryOrder[i]] = mapping[i] < 0 ? -1 : targetOrder[mapping[i]];
        }
        return original;
    }

    /** The mapping the other way round, from a molecule of {@code size} atoms. */
    static int[] inverse(int[] mapping, int size) {
        int[] inverse = new int[size];
        Arrays.fill(inverse, -1);
        for (int i = 0; i < mapping.length; i++) {
            if (mapping[i] >= 0) {
                inverse[mapping[i]] = i;
            }
        }
        return inverse;
    }

    static Set<String> inverses(Collection<int[]> mappings, int size) {
        Set<String> keys = new HashSet<>();
        for (int[] mapping : mappings) {
            keys.add(Arrays.toString(inverse(mapping, size)));
        }
        return keys;
    }

    /** Everything a search could change about a molecule, to check that it is left alone. */
    static String snapshot(IAtomContainer mol) {
        StringBuilder sb = new StringBuilder();
        for (IAtom atom : mol.atoms()) {
            sb.append(atom.getSymbol()).append(',').append(atom.getAtomicNumber()).append(',')
              .append(atom.getFormalCharge()).append(',').append(atom.getMassNumber()).append(',')
              .append(atom.getImplicitHydrogenCount()).append(',').append(Arrays.toString(atom.getFlags()))
              .append(',').append(atom.getProperties()).append(';');
        }
        for (IBond bond : mol.bonds()) {
            sb.append(mol.indexOf(bond.getBegin())).append('-').append(mol.indexOf(bond.getEnd())).append(',')
              .append(bond.getOrder()).append(',').append(Arrays.toString(bond.getFlags())).append(',')
              .append(bond.getProperties()).append(';');
        }
        mol.stereoElements().forEach(se -> sb.append(se.getConfig()).append(',').append(se.getGroupInfo()).append(';'));
        return sb.append(mol.getProperties()).toString();
    }

    /**
     * Every connected common substructure mapping with the most atoms, then
     * the most common bonds, by a brute force search; empty if no atom
     * matches. Atoms match by symbol; with {@code bonds}, bonds match by the
     * default rule, see {@link #bondsMatch}.
     */
    static Set<String> exact(IAtomContainer query, IAtomContainer target, boolean bonds) {
        return bruteForce(query, target, bonds, false);
    }

    /**
     * As {@link #exact}, with {@code rings} as {@link MCS#withCompleteRings()}
     * defines it: ring atoms and bonds pair only with ring ones, and each
     * mapped ring atom and common ring bond lies in a relevant ring whose bonds
     * are all common, on both sides; the rings are found from all cycles.
     */
    static Set<String> bruteForce(IAtomContainer query, IAtomContainer target, boolean bonds, boolean rings) {
        return bruteForce(query, target, bonds, rings, false);
    }

    /**
     * As {@link #bruteForce(IAtomContainer, IAtomContainer, boolean, boolean)};
     * with {@code stereo}, of those {@link #stereoOk}.
     */
    static Set<String> bruteForce(IAtomContainer query, IAtomContainer target, boolean bonds, boolean rings,
                                  boolean stereo) {
        return bruteForce(query, target, bonds, rings, stereo, 0);
    }

    /**
     * As {@link #bruteForce(IAtomContainer, IAtomContainer, boolean, boolean, boolean)};
     * with {@code minBonds} above 0 the maximum common edge subgraphs instead, as
     * {@link MCS#withDisconnected(int)} defines them: every mapping with the most
     * common bonds, then the most atoms, whose common bonds form fragments of at
     * least {@code minBonds} bonds each, which every mapped atom is in.
     */
    static Set<String> bruteForce(IAtomContainer query, IAtomContainer target, boolean bonds, boolean rings,
                                  boolean stereo, int minBonds) {
        return new BruteForce(query, target, bonds, rings, stereo, minBonds).mappings();
    }

    /**
     * Every maximum common edge subgraph mapping, see
     * {@link #bruteForce(IAtomContainer, IAtomContainer, boolean, boolean, boolean, int)},
     * without complete rings or stereochemistry; empty if there is none.
     */
    static Set<String> bruteForceEdges(IAtomContainer query, IAtomContainer target, boolean bonds, int minBonds) {
        return bruteForce(query, target, bonds, false, false, minBonds);
    }

    /** Whether a mapping maps rings whole, see {@link #bruteForce}. */
    static boolean complete(IAtomContainer query, IAtomContainer target, int[] mapping, boolean bonds) {
        BruteForce check = new BruteForce(query, target, bonds, true, false, 0);
        System.arraycopy(mapping, 0, check.map, 0, mapping.length);
        return check.complete();
    }

    /**
     * Whether a mapping keeps the configuration of each tetrahedral centre and
     * double bond that both molecules specify, where the mapped atoms fix it,
     * as {@link MCS#withStereochemistry()} describes; worked out from the
     * stereo elements, with a union-find with parity over the groups.
     */
    static boolean stereoOk(IAtomContainer query, IAtomContainer target, int[] mapping) {
        Map<String, String> up = new HashMap<>();
        Map<String, Integer> odd = new HashMap<>();
        for (IStereoElement<?, ?> e : query.stereoElements()) {
            for (IStereoElement<?, ?> f : target.stereoElements()) {
                int flip = e.getConfigClass() != f.getConfigClass() ? -1
                         : e.getConfigClass() == IStereoElement.TH ? centre(query, target, mapping, e, f)
                         : e.getConfigClass() == IStereoElement.CT ? doubleBond(query, target, mapping, e, f) : -1;
                if (flip < 0) {
                    continue;
                }
                // the groups of the two are inverted, or not, so that they agree
                flip ^= e.getConfigOrder() == f.getConfigOrder() ? 0 : 1;
                String a = e.getGroupInfo() == 0 ? "absolute" : "query " + e.getGroupInfo();
                String b = f.getGroupInfo() == 0 ? "absolute" : "target " + f.getGroupInfo();
                for (; up.containsKey(a); a = up.get(a)) {
                    flip ^= odd.get(a);
                }
                for (; up.containsKey(b); b = up.get(b)) {
                    flip ^= odd.get(b);
                }
                if (a.equals(b) && flip != 0) {
                    return false;
                } else if (!a.equals(b)) {
                    up.put(a, b);
                    odd.put(a, flip);
                }
            }
        }
        return true;
    }

    // centre e on centre f at its image: the parity of the permutation that pairs their carriers, the one left on
    // each side with each other, once three mapped ones pair up; -1 if not
    private static int centre(IAtomContainer query, IAtomContainer target, int[] mapping, IStereoElement<?, ?> e,
                              IStereoElement<?, ?> f) {
        int u = query.indexOf((IAtom) e.getFocus()), paired = 0, left = 0 + 1 + 2 + 3, inversions = 0;
        int[] perm = new int[4];
        for (int i = 0; i < 4; i++) {
            int c = mapping[query.indexOf((IAtom) e.getCarriers().get(i))];
            perm[i] = c < 0 || c == mapping[u] ? -1 : f.getCarriers().indexOf(target.getAtom(c));
            paired += perm[i] < 0 ? 0 : 1;
            left -= Math.max(perm[i], 0);
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < i; j++) {
                inversions += (perm[j] < 0 ? left : perm[j]) > (perm[i] < 0 ? left : perm[i]) ? 1 : 0;
            }
        }
        return mapping[u] != target.indexOf((IAtom) f.getFocus()) || paired < 3 ? -1 : inversions % 2;
    }

    // double bond e on double bond f between the images of its ends: whether the mapped neighbours that fix it are the
    // stored ones at an odd number of the four ends; -1 if it is not fixed
    private static int doubleBond(IAtomContainer query, IAtomContainer target, int[] mapping, IStereoElement<?, ?> e,
                                  IStereoElement<?, ?> f) {
        IBond bond = (IBond) e.getFocus(), image = (IBond) f.getFocus();
        int flip = 0;
        for (IAtom end : new IAtom[]{bond.getBegin(), bond.getEnd()}) {
            int v = mapping[query.indexOf(end)], x = -1;
            for (IAtom nbr : query.getConnectedAtomsList(end)) {
                int y = mapping[query.indexOf(nbr)];
                if (y >= 0 && v >= 0 && nbr != bond.getOther(end)
                    && target.getBond(target.getAtom(v), target.getAtom(y)) != null) {
                    x = query.indexOf(nbr);
                }
            }
            if (x < 0 || v != target.indexOf(image.getBegin()) && v != target.indexOf(image.getEnd())) {
                return -1;
            }
            flip ^= (x == stored(query, e, end)) == (mapping[x] == stored(target, f, target.getAtom(v))) ? 0 : 1;
        }
        return flip;
    }

    // the stored neighbour of an end of a double bond element: the other atom of its carrier bond there
    private static int stored(IAtomContainer mol, IStereoElement<?, ?> e, IAtom end) {
        for (Object carrier : e.getCarriers()) {
            if (((IBond) carrier).getOther(end) != null) {
                return mol.indexOf(((IBond) carrier).getOther(end));
            }
        }
        return -1;
    }

    /**
     * The molecule with a configuration at random on each atom of three or
     * four bonds and each double bond whose ends have one or two other bonds,
     * a sixth each in two racemic groups and one relative group.
     */
    static IAtomContainer withConfigurations(IAtomContainer mol, Random random) {
        for (IAtom atom : mol.atoms()) {
            List<IAtom> carriers = new ArrayList<>(mol.getConnectedAtomsList(atom));
            if (carriers.size() == 3) {
                carriers.add(atom); // an implicit hydrogen, put in place by the shuffle
            }
            if (carriers.size() == 4) {
                Collections.shuffle(carriers, random);
                addStereo(mol, new TetrahedralChirality(atom, carriers.toArray(new IAtom[0]), 1 + random.nextInt(2)),
                          random);
            }
        }
        for (IBond bond : mol.bonds()) {
            List<IBond> begin = new ArrayList<>(mol.getConnectedBondsList(bond.getBegin()));
            List<IBond> end = new ArrayList<>(mol.getConnectedBondsList(bond.getEnd()));
            begin.remove(bond);
            end.remove(bond);
            if (bond.getOrder() == IBond.Order.DOUBLE && !begin.isEmpty() && !end.isEmpty() && begin.size() <= 2
                && end.size() <= 2) {
                IBond[] carriers = {begin.get(random.nextInt(begin.size())), end.get(random.nextInt(end.size()))};
                addStereo(mol, new DoubleBondStereochemistry(bond, carriers, 1 + random.nextInt(2)), random);
            }
        }
        return mol;
    }

    private static void addStereo(IAtomContainer mol, IStereoElement<?, ?> se, Random random) {
        se.setGroupInfo(GROUPS[random.nextInt(GROUPS.length)]);
        mol.addStereoElement(se);
    }

    /**
     * Checks limits around the number of maximum mappings {@code exact}, and
     * 1000: as many mappings as asked for, each once, the first ones of a
     * larger limit, though that is not promised, and match the first of all.
     */
    static void assertLimits(MCS mcs, IAtomContainer target, Set<String> exact) throws Intractable {
        int size = exact.size();
        List<int[]> all = mcs.matchAll(target, size + 1);
        Assertions.assertEquals(exact, keys(all));
        Assertions.assertEquals(size, all.size(), "each once");
        for (int limit : new int[]{1, 2, size - 1, size, 1000}) {
            if (limit > 0) {
                assertFirst(all, mcs.matchAll(target, limit), Math.min(limit, size));
            }
        }
        assertFirst(all, mcs.matchAll(target), Math.min(1000, size));
        Assertions.assertArrayEquals(size == 0 ? new int[0] : all.get(0), mcs.match(target), "match");
    }

    // the mappings are the first ones of all, as many as given
    private static void assertFirst(List<int[]> all, List<int[]> mappings, int size) {
        Assertions.assertEquals(size, mappings.size(), "as many as asked for");
        for (int i = 0; i < size; i++) {
            Assertions.assertArrayEquals(all.get(i), mappings.get(i), "the first ones of a larger limit");
        }
    }

    /**
     * The 64 graphs on four carbon atoms, disconnected ones too, new objects
     * on each call: atom pair k of (0, 1), (0, 2), (0, 3), (1, 2), (1, 3),
     * (2, 3) is bonded where bit k of the graph's number is set, by a double
     * bond where k is odd.
     */
    static List<IAtomContainer> graphsOnFourAtoms() {
        int[][] pairs = {{0, 1}, {0, 2}, {0, 3}, {1, 2}, {1, 3}, {2, 3}};
        List<IAtomContainer> graphs = new ArrayList<>();
        for (int mask = 0; mask < 1 << pairs.length; mask++) {
            IAtomContainer mol = new AtomContainer();
            for (int i = 0; i < 4; i++) {
                mol.addAtom(new Atom("C"));
            }
            for (int k = 0; k < pairs.length; k++) {
                if ((mask & 1 << k) != 0) {
                    mol.addBond(pairs[k][0], pairs[k][1], k % 2 == 0 ? IBond.Order.SINGLE : IBond.Order.DOUBLE);
                }
            }
            graphs.add(mol);
        }
        return graphs;
    }

    // a complete graph of carbon atoms and single bonds
    static IAtomContainer completeGraph(int size) {
        IAtomContainer graph = new AtomContainer();
        for (int i = 0; i < size; i++) {
            graph.addAtom(new Atom("C"));
        }
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                graph.addBond(i, j, IBond.Order.SINGLE);
            }
        }
        return graph;
    }

    /**
     * A random connected graph: a uniformly random spanning tree, decoded
     * from a random Pruefer sequence, plus each other atom pair bonded with
     * probability {@code density}. Atoms are drawn from {@code elements}, a
     * third of the bonds are double, and each ring bond is aromatic with
     * probability 1/2.
     */
    static IAtomContainer randomGraph(Random random, int n, double density, String... elements) {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        for (int i = 0; i < n; i++) {
            mol.addAtom(new Atom(elements[random.nextInt(elements.length)]));
        }
        if (n > 1) {
            int[] code = new int[n - 2];
            int[] degree = new int[n];
            Arrays.fill(degree, 1);
            for (int i = 0; i < code.length; i++) {
                code[i] = random.nextInt(n);
                degree[code[i]]++;
            }
            for (int x : code) {
                int leaf = 0;
                while (degree[leaf] != 1) {
                    leaf++;
                }
                mol.addBond(leaf, x, order(random));
                degree[leaf]--;
                degree[x]--;
            }
            int u = -1;
            for (int i = 0; i < n; i++) {
                if (degree[i] == 1) {
                    if (u < 0) {
                        u = i;
                    } else {
                        mol.addBond(u, i, order(random));
                    }
                }
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (mol.getBond(mol.getAtom(i), mol.getAtom(j)) == null && random.nextDouble() < density) {
                    mol.addBond(i, j, order(random));
                }
            }
        }
        Cycles.markRingAtomsAndBonds(mol);
        for (IBond bond : mol.bonds()) {
            if (bond.isInRing() && random.nextBoolean()) {
                setAromatic(bond);
            }
        }
        return mol;
    }

    static void setAromatic(IBond bond) {
        bond.setIsAromatic(true);
        bond.getBegin().setIsAromatic(true);
        bond.getEnd().setIsAromatic(true);
    }

    private static IBond.Order order(Random random) {
        return random.nextInt(3) == 0 ? IBond.Order.DOUBLE : IBond.Order.SINGLE;
    }

    // whether a query bond and its image, if any, are a common bond, see commonBonds
    private static boolean common(IBond bond, IBond image, boolean orders) {
        if (image == null) {
            return false;
        }
        if (bond instanceof IQueryBond) {
            return ((IQueryBond) bond).matches(image);
        }
        return !orders || bondsMatch(bond, image);
    }

    // the bonds of each relevant ring, and all ring bonds into ringBonds: the simple cycles, each a ring unless it is
    // a sum of shorter ones, by elimination over GF(2)
    private static List<BitSet> relevantRings(IAtomContainer mol, BitSet ringBonds) {
        Set<BitSet> cycles = new HashSet<>();
        for (int s = 0; s < mol.getAtomCount(); s++) {
            cycles(mol, s, s, new BitSet(), new BitSet(), cycles);
        }
        List<BitSet> sorted = new ArrayList<>(cycles);
        sorted.sort(Comparator.comparingInt(BitSet::cardinality));
        List<BitSet> relevant = new ArrayList<>();
        // the sums of the shorter cycles, a row for each lowest bond, the key
        TreeMap<Integer, BitSet> basis = new TreeMap<>();
        for (int i = 0, j = 0; i < sorted.size(); i++) {
            for (; sorted.get(j).cardinality() < sorted.get(i).cardinality(); j++) {
                BitSet rest = reduce(basis, sorted.get(j));
                if (!rest.isEmpty()) {
                    basis.put(rest.nextSetBit(0), rest);
                }
            }
            ringBonds.or(sorted.get(i));
            if (!reduce(basis, sorted.get(i)).isEmpty()) {
                relevant.add(sorted.get(i));
            }
        }
        return relevant;
    }

    // adds the simple cycles through atom s and atoms numbered higher, as sets of bonds, that follow the path to a
    private static void cycles(IAtomContainer mol, int s, int a, BitSet atoms, BitSet bonds, Set<BitSet> cycles) {
        atoms.set(a);
        for (IBond bond : mol.getConnectedBondsList(mol.getAtom(a))) {
            int b = mol.indexOf(bond.getOther(mol.getAtom(a)));
            int e = mol.indexOf(bond);
            if (!bonds.get(e) && (b == s || b > s && !atoms.get(b))) {
                bonds.set(e);
                if (b == s) {
                    cycles.add((BitSet) bonds.clone());
                } else {
                    cycles(mol, s, b, atoms, bonds, cycles);
                }
                bonds.clear(e);
            }
        }
        atoms.clear(a);
    }

    // the cycle less each row of the basis whose key it has, by increasing key: empty if it is a sum of the rows
    private static BitSet reduce(TreeMap<Integer, BitSet> basis, BitSet cycle) {
        BitSet rest = (BitSet) cycle.clone();
        for (Map.Entry<Integer, BitSet> row : basis.entrySet()) {
            if (rest.get(row.getKey())) {
                rest.xor(row.getValue());
            }
        }
        return rest;
    }

    // whether each ring atom of atoms and each ring bond of common lies in a ring whose bonds are all common
    private static boolean whole(IAtomContainer mol, List<BitSet> rings, BitSet ringBonds, BitSet atoms,
                                 BitSet common) {
        BitSet covered = new BitSet();
        for (BitSet ring : rings) {
            BitSet outside = (BitSet) ring.clone();
            outside.andNot(common);
            if (outside.isEmpty()) {
                covered.or(ring);
            }
        }
        for (int a = atoms.nextSetBit(0); a >= 0; a = atoms.nextSetBit(a + 1)) {
            if (hasBond(mol, ringBonds, a) && !hasBond(mol, covered, a)) {
                return false;
            }
        }
        BitSet uncovered = (BitSet) common.clone();
        uncovered.and(ringBonds);
        uncovered.andNot(covered);
        return uncovered.isEmpty();
    }

    // whether the atom has one of the bonds
    private static boolean hasBond(IAtomContainer mol, BitSet bonds, int atom) {
        for (IBond bond : mol.getConnectedBondsList(mol.getAtom(atom))) {
            if (bonds.get(mol.indexOf(bond))) {
                return true;
            }
        }
        return false;
    }

    private static IBond image(IAtomContainer query, IAtomContainer target, int[] mapping, IBond bond) {
        int a = mapping[query.indexOf(bond.getBegin())];
        int b = mapping[query.indexOf(bond.getEnd())];
        return a >= 0 && b >= 0 ? target.getBond(target.getAtom(a), target.getAtom(b)) : null;
    }

    private static final class BruteForce {

        private final IAtomContainer query;
        private final IAtomContainer target;
        private final boolean        bonds;
        private final boolean        stereo;
        // the fewest common bonds of a fragment, 0 for a connected mapping
        private final int            minBonds;
        private final int[]          map;
        private final boolean[]      used;
        private final Set<String>    best      = new HashSet<>();
        // atoms << 32 | common bonds, or bonds first with minBonds
        private long                 bestScore = -1;
        // with complete rings, the relevant rings of each molecule as sets of bonds, else null, and its ring bonds
        private final List<BitSet>   qRings, tRings;
        private final BitSet         qRingBonds = new BitSet(), tRingBonds = new BitSet();

        BruteForce(IAtomContainer query, IAtomContainer target, boolean bonds, boolean rings, boolean stereo,
                   int minBonds) {
            this.query = query;
            this.target = target;
            this.bonds = bonds;
            this.stereo = stereo;
            this.minBonds = minBonds;
            this.map = new int[query.getAtomCount()];
            this.used = new boolean[target.getAtomCount()];
            Arrays.fill(map, -1);
            this.qRings = rings ? relevantRings(query, qRingBonds) : null;
            this.tRings = rings ? relevantRings(target, tRingBonds) : null;
        }

        Set<String> mappings() {
            search(0, 0);
            return best;
        }

        // map query atom i to nothing, then to each free target atom of the same symbol; atoms first, stop when
        // too few are left
        private void search(int i, int atoms) {
            if (minBonds == 0 && atoms + map.length - i < (int) (bestScore >>> 32)) {
                return;
            }
            if (i == map.length) {
                save(atoms);
                return;
            }
            map[i] = -1;
            search(i + 1, atoms);
            for (int j = 0; j < used.length; j++) {
                if (!used[j] && query.getAtom(i).getSymbol().equals(target.getAtom(j).getSymbol())
                    && (qRings == null || hasBond(query, qRingBonds, i) == hasBond(target, tRingBonds, j))) {
                    map[i] = j;
                    used[j] = true;
                    search(i + 1, atoms + 1);
                    used[j] = false;
                }
            }
        }

        private boolean commonBond(int i, int j) {
            IBond qb = query.getBond(query.getAtom(i), query.getAtom(j));
            IBond tb = target.getBond(target.getAtom(map[i]), target.getAtom(map[j]));
            return qb != null && tb != null && (!bonds || bondsMatch(qb, tb))
                   && (qRings == null || qRingBonds.get(query.indexOf(qb)) == tRingBonds.get(target.indexOf(tb)));
        }

        // whether the rings are whole on both sides, see bruteForce
        private boolean complete() {
            BitSet qAtoms = new BitSet(), tAtoms = new BitSet(), qCommon = new BitSet(), tCommon = new BitSet();
            for (int i = 0; i < map.length; i++) {
                for (int j = i + 1; map[i] >= 0 && j < map.length; j++) {
                    if (map[j] >= 0 && commonBond(i, j)) {
                        qCommon.set(query.indexOf(query.getBond(query.getAtom(i), query.getAtom(j))));
                        tCommon.set(target.indexOf(target.getBond(target.getAtom(map[i]), target.getAtom(map[j]))));
                    }
                }
                if (map[i] >= 0) {
                    qAtoms.set(i);
                    tAtoms.set(map[i]);
                }
            }
            return whole(query, qRings, qRingBonds, qAtoms, qCommon)
                   && whole(target, tRings, tRingBonds, tAtoms, tCommon);
        }

        private void save(int atoms) {
            if (atoms == 0) {
                return;
            }
            // the parts the common bonds join the mapped atoms into, and the common bonds of each
            int[] part = new int[map.length];
            int[] partBonds = new int[map.length];
            int parts = atoms;
            int common = 0;
            for (int i = 0; i < map.length; i++) {
                part[i] = i;
            }
            for (int i = 0; i < map.length; i++) {
                for (int j = i + 1; map[i] >= 0 && j < map.length; j++) {
                    if (map[j] >= 0 && commonBond(i, j)) {
                        int a = find(part, i);
                        int b = find(part, j);
                        if (a != b) {
                            part[b] = a;
                            partBonds[a] += partBonds[b];
                            parts--;
                        }
                        partBonds[a]++;
                        common++;
                    }
                }
            }
            // the mapped atoms must be connected by common bonds, or with minBonds each be in a fragment of that
            // many
            for (int i = 0; i < map.length; i++) {
                if (map[i] >= 0 && (minBonds == 0 ? parts > 1 : partBonds[find(part, i)] < minBonds)) {
                    return;
                }
            }
            long score = minBonds == 0 ? (long) atoms << 32 | common : (long) common << 32 | atoms;
            if (score < bestScore || qRings != null && !complete() || stereo && !stereoOk(query, target, map)) {
                return;
            }
            if (score > bestScore) {
                bestScore = score;
                best.clear();
            }
            best.add(Arrays.toString(map));
        }
    }
}
