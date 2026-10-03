/**
 *
 * Copyright (C) 2006-2010  Syed Asad Rahman <asad@ebi.ac.uk>
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
package org.openscience.cdk.smsd.algorithm.mcsplus;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Converts compatibility-graph clique node IDs to source/target index pairs.
 * This helper does not search for a clique or establish chemical compatibility.
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class ExactMapping {

    /** Creates the legacy clique-mapping helper. */
    public ExactMapping() {
    }

    /**
     *
     * Extract atom mapping from the cliques and stores it in a List
     * @param compGraphNodes
     * @param cliqueListOrg
     */
    private static List<Integer> extractCliqueMapping(List<Integer> compGraphNodes, List<Integer> cliqueListOrg) {

        Map<Integer, Integer> nodeOffsets = new HashMap<>();
        for (int i = 0; i < compGraphNodes.size(); i += 3) {
            nodeOffsets.put(compGraphNodes.get(i + 2), i);
        }
        List<Integer> cliqueMapping = new ArrayList<>(cliqueListOrg.size() * 2);
        for (Integer node : cliqueListOrg) {
            Integer offset = nodeOffsets.get(node);
            if (offset == null) {
                throw new IllegalArgumentException("Clique node is absent from the compatibility graph: " + node);
            }
            cliqueMapping.add(compGraphNodes.get(offset));
            cliqueMapping.add(compGraphNodes.get(offset + 1));
        }

        return cliqueMapping;
    }

    /**
     * Appends the clique's mapping to the supplied output list.
     * Compatibility nodes are flat source-index/target-index/node-ID triples.
     * The resulting mapping is a new flat source-index/target-index pair list
     * in clique order. Input collections and index compatibility are trusted;
     * no equality of the original molecules is required or checked.
     *
     * @param mappings mutable output list receiving the new mapping
     * @param compGraphNodes compatibility-node triples with unique node IDs
     * @param cliqueListOrg clique node IDs to extract
     * @return the same output list after appending one mapping
     * @throws IllegalArgumentException if a clique ID is absent from the graph
     * @throws NullPointerException if an input list is null
     * @throws IndexOutOfBoundsException if graph triples are incomplete
     */
    public static List<List<Integer>> extractMapping(List<List<Integer>> mappings, List<Integer> compGraphNodes,
            List<Integer> cliqueListOrg) {
        mappings.add(extractCliqueMapping(compGraphNodes, cliqueListOrg));
        return mappings;
    }
}
