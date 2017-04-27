/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
 *                    2011  Thorsten Flügel <thorsten.fluegel@tu-dortmund.de>
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
package org.openscience.cdk.graph.invariant;

import com.google.common.primitives.Ints;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

/**
 * Compute the extended connectivity values (Morgan Numbers) {@cdk.cite MOR65}.
 * The tool does not produce the lexicographic smallest labelling on the graph
 * and should not be used as a robust canonical labelling tool. To canonical
 * label a graph please use {@link InChINumbersTools} or {@link
 * CanonicalLabeler}. To determine equivalent classes of atoms please use {@link
 * HuLuIndexTool} or one of the discrete refines available in the 'cdk-group'
 * module.
 *
 * @author shk3
 * @cdk.module standard
 * @cdk.githash
 * @cdk.created 2003-06-30
 * @cdk.keyword Morgan number
 * @see InChINumbersTools
 * @see CanonicalLabeler
 * @see HuLuIndexTool
 */
public class MorganNumbersTools {

    /** Default size of adjacency lists. */
    private static final int INITIAL_DEGREE = 4;

    /**
     * Makes an array containing the morgan numbers of the atoms of
     * atomContainer. These number are the extended connectivity values and not
     * the lexicographic smallest labelling on the graph.
     *
     * @param molecule the molecule to analyse.
     * @return The morgan numbers value.
     */
    public static long[] getMorganNumbers(IAtomContainer molecule) {

        int order = molecule.getAtomCount();

        long[] currentInvariants = new long[order];
        long[] previousInvariants = new long[order];

        int[][] graph = new int[order][INITIAL_DEGREE];
        int[] degree = new int[order];

        // which atoms are the non-hydrogens.
        int[] nonHydrogens = new int[order];

        for (int v = 0; v < order; v++)
            nonHydrogens[v] = "H".equals(molecule.getAtom(v).getSymbol()) ? 0 : 1;

        // build the graph and initialise the current connectivity
        // value to the number of connected non-hydrogens
        for (IBond bond : molecule.bonds()) {
            int u = molecule.indexOf(bond.getBeg());
            int v = molecule.indexOf(bond.getEnd());
            graph[u] = Ints.ensureCapacity(graph[u], degree[u] + 1, INITIAL_DEGREE);
            graph[v] = Ints.ensureCapacity(graph[v], degree[v] + 1, INITIAL_DEGREE);
            graph[u][degree[u]++] = v;
            graph[v][degree[v]++] = u;
            currentInvariants[u] += nonHydrogens[v];
            currentInvariants[v] += nonHydrogens[u];
        }

        // iteratively sum the connectivity values for each vertex
        for (int i = 0; i < order; i++) {
            System.arraycopy(currentInvariants, 0, previousInvariants, 0, order);
            for (int u = 0; u < order; u++) {
                currentInvariants[u] = 0;

                // for each of the vertices adjacent to 'u' sum their
                // previous connectivity value
                int[] neighbors = graph[u];
                for (int j = 0; j < degree[u]; j++) {
                    int v = neighbors[j];
                    currentInvariants[u] += previousInvariants[v] * nonHydrogens[v];
                }
            }
        }
        return currentInvariants;
    }

    /**
     * Makes an array containing the morgan numbers+element symbol of the atoms
     * of {@code atomContainer}. This method puts the element symbol before the
     * morgan number, useful for finding out how many different rests are
     * connected to an atom.
     *
     * @param atomContainer The atomContainer to analyse.
     * @return The morgan numbers value.
     */
    public static String[] getMorganNumbersWithElementSymbol(IAtomContainer atomContainer) {
        long[] morgannumbers = getMorganNumbers(atomContainer);
        String[] morgannumberswithelement = new String[morgannumbers.length];
        for (int i = 0; i < morgannumbers.length; i++) {
            morgannumberswithelement[i] = atomContainer.getAtom(i).getSymbol() + "-" + morgannumbers[i];
        }
        return (morgannumberswithelement);
    }
}
