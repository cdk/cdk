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
package org.openscience.cdk.smsd;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.silent.Atom;
import org.openscience.cdk.silent.AtomContainer;

/**
 * Small test graphs, and a brute force search over all atom mappings for
 * their connected maximum common substructures, to check the MCS searches
 * against.
 *
 * @author Syed Asad Rahman
 */
public final class BruteForceMCS {

    private BruteForceMCS() {
    }

    /**
     * Every connected common substructure mapping with the most atoms, then
     * the most common bonds. Atoms match by symbol; with {@code bonds}, bonds
     * match if both are aromatic, or both are not aromatic and have the same
     * order. A mapping is
     * written as {@link Arrays#toString(int[])} of the target atom index of
     * each query atom, -1 if unmapped. If no atom matches, the only mapping
     * maps no atom.
     *
     * @param query  the query
     * @param target the target
     * @param bonds  whether bond orders must match
     * @return the maximum mappings
     */
    public static Set<String> mappings(IAtomContainer query, IAtomContainer target, boolean bonds) {
        return new Search(query, target, bonds).mappings();
    }

    /**
     * A complete graph of carbon atoms and single bonds.
     *
     * @param size the number of atoms
     * @return the graph
     */
    public static IAtomContainer completeGraph(int size) {
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
     * A random connected graph of C, N and O atoms: a random tree plus about
     * a quarter of the other atom pairs bonded.
     *
     * @param random the random numbers
     * @param size   the number of atoms
     * @param orders whether to use double bonds as well as single bonds
     * @return the graph
     */
    public static IAtomContainer randomGraph(Random random, int size, boolean orders) {
        String[] elements = {"C", "C", "N", "O"};
        IAtomContainer graph = new AtomContainer();
        for (int i = 0; i < size; i++) {
            graph.addAtom(new Atom(elements[random.nextInt(elements.length)]));
            if (i > 0) {
                graph.addBond(random.nextInt(i), i, order(random, orders));
            }
        }
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                if (graph.getBond(graph.getAtom(i), graph.getAtom(j)) == null && random.nextDouble() < 0.25) {
                    graph.addBond(i, j, order(random, orders));
                }
            }
        }
        return graph;
    }

    private static IBond.Order order(Random random, boolean orders) {
        return orders && random.nextBoolean() ? IBond.Order.DOUBLE : IBond.Order.SINGLE;
    }

    private static final class Search {

        private final IAtomContainer query;
        private final IAtomContainer target;
        private final boolean        bonds;
        private final int[]          map;
        private final boolean[]      used;
        private final Set<String>    best      = new HashSet<>();
        private int                  bestAtoms = -1;
        private int                  bestBonds = -1;

        Search(IAtomContainer query, IAtomContainer target, boolean bonds) {
            this.query = query;
            this.target = target;
            this.bonds = bonds;
            this.map = new int[query.getAtomCount()];
            this.used = new boolean[target.getAtomCount()];
            Arrays.fill(map, -1);
        }

        Set<String> mappings() {
            search(0, 0);
            return best;
        }

        private void search(int i, int atoms) {
            if (atoms + map.length - i < bestAtoms) {
                return;
            }
            if (i == map.length) {
                save(atoms);
                return;
            }
            map[i] = -1;
            search(i + 1, atoms);
            for (int j = 0; j < used.length; j++) {
                if (!used[j] && query.getAtom(i).getSymbol().equals(target.getAtom(j).getSymbol())) {
                    map[i] = j;
                    used[j] = true;
                    search(i + 1, atoms + 1);
                    used[j] = false;
                }
            }
        }

        private int countCommonBonds() {
            int common = 0;
            for (int i = 0; i < map.length; i++) {
                for (int j = i + 1; map[i] >= 0 && j < map.length; j++) {
                    if (map[j] >= 0 && commonBond(i, j)) {
                        common++;
                    }
                }
            }
            return common;
        }

        private boolean commonBond(int i, int j) {
            IBond qb = query.getBond(query.getAtom(i), query.getAtom(j));
            IBond tb = target.getBond(target.getAtom(map[i]), target.getAtom(map[j]));
            if (qb == null || tb == null) {
                return false;
            }
            return !bonds || (qb.isAromatic() && tb.isAromatic())
                    || (!qb.isAromatic() && !tb.isAromatic() && qb.getOrder() == tb.getOrder());
        }

        private void save(int atoms) {
            // the mapped atoms must be connected by common bonds
            boolean[] seen = new boolean[map.length];
            int reached = 0;
            for (int i = 0; i < map.length && reached == 0; i++) {
                if (map[i] >= 0) {
                    seen[i] = true;
                    reached = 1;
                }
            }
            for (boolean grown = true; grown;) {
                grown = false;
                for (int i = 0; i < map.length; i++) {
                    for (int j = 0; seen[i] && j < map.length; j++) {
                        if (map[j] >= 0 && !seen[j] && commonBond(i, j)) {
                            seen[j] = true;
                            reached++;
                            grown = true;
                        }
                    }
                }
            }
            if (reached != atoms) {
                return;
            }
            int common = countCommonBonds();
            if (atoms > bestAtoms || (atoms == bestAtoms && common > bestBonds)) {
                bestAtoms = atoms;
                bestBonds = common;
                best.clear();
            }
            if (atoms == bestAtoms && common == bestBonds) {
                best.add(Arrays.toString(map));
            }
        }
    }
}
