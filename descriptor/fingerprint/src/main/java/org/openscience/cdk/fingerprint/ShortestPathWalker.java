/*
 * Copyright (C) 2012   Syed Asad Rahman <asad@ebi.ac.uk>
 *               2013   John May         <jwmay@users.sf.net>
 *
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
package org.openscience.cdk.fingerprint;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.graph.AllPairsShortestPaths;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

/**
 *
 * @author Syed Asad Rahman (2012)
 * @author John May (2013)
 * @cdk.keyword fingerprint
 * @cdk.keyword similarity
 * @cdk.module fingerprint
 * @cdk.githash
 *
 */
public final class ShortestPathWalker {

    /* container which is being traversed */
    private final IAtomContainer container;

    /* set of encoded atom paths */
    private final Set<String>    paths;

    /* maximum number of shortest paths, when there is more then one path */
    private static final int     MAX_SHORTEST_PATHS = 5;

    /**
     * Create a new shortest path walker for a given container.
     * @param container the molecule to encode the shortest paths
     */
    public ShortestPathWalker(IAtomContainer container) {
        this.container = container;
        this.paths = Collections.unmodifiableSet(traverse());
    }

    /**
     * Access a set of all shortest paths.
     * @return the paths
     */
    public Set<String> paths() {
        return Collections.unmodifiableSet(paths);
    }

    /**
     * Traverse all-pairs of shortest-paths within a chemical graph.
     */
    private Set<String> traverse() {

        Set<String> paths = new TreeSet<>();

        // All-Pairs Shortest-Paths (APSP)
        AllPairsShortestPaths apsp = new AllPairsShortestPaths(container);

        for (int i = 0, n = container.getAtomCount(); i < n; i++) {

            paths.add(toAtomPattern(container.getAtom(i)));

            // only do the comparison for i,j then reverse the path for j,i
            for (int j = i + 1; j < n; j++) {

                int nPaths = apsp.from(i).nPathsTo(j);

                // only encode when there is a manageable number of paths
                if (nPaths > 0 && nPaths < MAX_SHORTEST_PATHS) {

                    for (int[] path : apsp.from(i).pathsTo(j)) {
                        paths.add(encode(path));
                        paths.add(encode(reverse(path)));
                    }

                }

            }
        }

        return paths;

    }

    /**
     * Reverse an array of integers.
     *
     * @param src array to reverse
     * @return reversed copy of <i>src</i>
     */
    private int[] reverse(int[] src) {
        int[] dest = Arrays.copyOf(src, src.length);
        int left = 0;
        int right = src.length - 1;

        while (left < right) {
            // swap the values at the left and right indices
            dest[left] = src[right];
            dest[right] = src[left];

            // move the left and right index pointers in toward the center
            left++;
            right--;
        }
        return dest;
    }

    /**
     * Encode the provided path of atoms to a string.
     *
     * @param path inclusive array of vertex indices
     * @return encoded path
     */
    private String encode(int[] path) {
        StringBuilder sb = new StringBuilder(path.length * 3);
        for (int i = 0, n = path.length - 1; i <= n; i++) {
            IAtom atom = container.getAtom(path[i]);
            sb.append(toAtomPattern(atom));
            // if we are not at the last index, add the connecting bond
            if (i < n) {
                IBond bond = container.getBond(container.getAtom(path[i]), container.getAtom(path[i + 1]));
                sb.append(getBondSymbol(bond));
            }
        }
        return sb.toString();
    }

    /**
     * Convert an atom to a string representation. Currently this method just
     * returns the symbol but in future may include other properties, such as, stereo
     * descriptor and charge.
     * @param atom The atom to encode
     * @return encoded atom
     */
    private String toAtomPattern(IAtom atom) {
        return atom.getSymbol();
    }

    /**
     * Gets the bondSymbol attribute of the HashedFingerprinter class
     *
     * @param bond Description of the Parameter
     * @return The bondSymbol value
     *]\     */
    private char getBondSymbol(IBond bond) {
        if (isSP2Bond(bond)) {
            return '@';
        } else {
            switch (bond.getOrder()) {
                case SINGLE:
                    return '1';
                case DOUBLE:
                    return '2';
                case TRIPLE:
                    return '3';
                case QUADRUPLE:
                    return '4';
                default:
                    return '5';
            }
        }
    }

    /**
     * Returns true if the bond binds two atoms, and both atoms are SP2 in a ring system.
     */
    private boolean isSP2Bond(IBond bond) {
        return bond.getFlag(CDKConstants.ISAROMATIC);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public String toString() {
        int n = this.paths.size();
        String[] paths = this.paths.toArray(new String[n]);
        StringBuilder sb = new StringBuilder(n * 5);

        for (int i = 0, last = n - 1; i < n; i++) {
            sb.append(paths[i]);
            if (i != last) sb.append("->");
        }

        return sb.toString();
    }
}
