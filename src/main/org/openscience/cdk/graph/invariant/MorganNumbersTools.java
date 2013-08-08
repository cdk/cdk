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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

/**
 * Compute the extended connectivity values (Morgan Numbers) {@cdk.cite MOR65}.
 * The tool does not produce the lexicographic smallest labelling on the graph
 * and should be used as a robust canonical labelling tool.
 * To canonical label a graph please use {@link InChINumbersTools} or
 * {@link CanonicalLabeler}.
 * To determine equivalent classes of atoms please use {@link HuLuIndexTool}
 * or one of the discrete refines available in the 'cdk-group' module.
 *
 * @cdk.module  standard
 * @cdk.githash
 *
 * @author      shk3
 * @cdk.created 2003-06-30
 * @cdk.keyword Morgan number
 * @see InChINumbersTools
 * @see CanonicalLabeler
 * @see HuLuIndexTool
 */
@TestClass("org.openscience.cdk.graph.invariant.MorganNumbersToolsTest")
public class MorganNumbersTools {

  /**
   * Makes an array containing the morgan numbers of the atoms of atomContainer.
   * These number are the extended connectivity values and not the lexicographic
   * smallest labelling on the graph.
   *
   * @param  m  The atomContainer to analyse.
   * @return The morgan numbers value.
   */
  @TestMethod("testGetMorganNumbers_IAtomContainer")
  public static long[] getMorganNumbers(IAtomContainer m) {

        // order of the graph, |V|
        int ord = m.getAtomCount();

        // current and previous connectivity values
        long[] curr = new long[ord];
		long[] prev = new long[ord];

        // adjacent vertices, degree of each vertex
        int[][] g   = new int[ord][4];
        int[]   deg = new int[ord];

        // which atoms are the 'heavys' (hs) - non-hydrogens.
        int[]   hs  = new int[ord];

		for (int f = 0; f < ord; f++)
            hs[f] = "H".equals(m.getAtom(f).getSymbol()) ? 0 : 1;

        // build the graph (g) and initialise the current connectivity
        // value to the number of connected non-hydrogens
        for (IBond bond : m.bonds()) {
            int u = m.getAtomNumber(bond.getAtom(0));
            int v = m.getAtomNumber(bond.getAtom(1));
            g[u] = Ints.ensureCapacity(g[u], deg[u] + 1, 4);
            g[v] = Ints.ensureCapacity(g[v], deg[v] + 1, 4);
            g[u][deg[u]++] = v;
            g[v][deg[v]++] = u;
            curr[u] += hs[v];
            curr[v] += hs[u];
        }

        // iteratively sum the connectivity values for each vertex
		for (int e = 0; e < ord; e++) {
            System.arraycopy(curr, 0, prev, 0, ord);
			for (int u = 0; u < ord; u++) {
				curr[u] = 0;

                // for each of the vertices (vs) adjacent to 'u' sum their
                // previous connectivity value
                int[] vs = g[u];
				for (int j = 0; j < deg[u]; j++) {
                    int v = vs[j];
					curr[u] += prev[v] * hs[v];
				}
			}
		}
		return curr;
  }


  /**
  * Makes an array containing the morgan numbers+element symbol of the atoms of
  * atomContainer. This method puts the element symbol before the morgan
  * number, useful for finding out how many different rests are connected to an
  * atom.
  *
  * @param atomContainer The atomContainer to analyse.
  * @return The morgan numbers value.
  */
  @TestMethod("testPhenylamine")
  public static String[] getMorganNumbersWithElementSymbol(IAtomContainer atomContainer) {
    long[] morgannumbers = getMorganNumbers(atomContainer);
    String[] morgannumberswithelement = new String[morgannumbers.length];
    for (int i = 0; i < morgannumbers.length; i++) {
      morgannumberswithelement[i] = atomContainer.getAtom(i).getSymbol() + "-" + morgannumbers[i];
    }
    return (morgannumberswithelement);
  }
}

