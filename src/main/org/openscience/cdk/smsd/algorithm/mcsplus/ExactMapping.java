/**
 *
 * Copyright (C) 2006-2010  Syed Asad Rahman {asad@ebi.ac.uk}
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
import org.openscience.cdk.annotations.TestClass;

/**
 * This class handles MCS between two identical molecules.
 * Hence they generate am MCS where all atoms are mapped.
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 */

@TestClass("org.openscience.cdk.smsd.SMSDBondSensitiveTest")
public class ExactMapping {

    /**
     * 
     * Extract atom mapping from the cliques and stores it in a List
     * @param comp_graph_nodes
     * @param clique_List_org
     * @return 
     */
    private static List<Integer> extractCliqueMapping(List<Integer> comp_graph_nodes, List<Integer> clique_List_org) {

        List<Integer> clique_mapping = new ArrayList<Integer>();
        List<Integer> clique_List = new ArrayList<Integer>(clique_List_org);
        int clique_siz = clique_List.size();
        int vec_size = comp_graph_nodes.size();
        //System.out.println("VEC  SIZE " + vec_size);
        for (int a = 0; a < clique_siz; a++) {
            for (int b = 0; b < vec_size; b += 3) {
                if (clique_List.get(a) == comp_graph_nodes.get(b + 2)) {
                    clique_mapping.add(comp_graph_nodes.get(b));
                    clique_mapping.add(comp_graph_nodes.get(b + 1));
                }
            }
        }

        return clique_mapping;
    }

    //extract atom mapping from the clique List and print it on the screen
    /**
     * 
     * @param _mappings
     * @param comp_graph_nodes
     * @param clique_List_org
     * @return
     */
    protected static List<List<Integer>> extractMapping(List<List<Integer>> _mappings, List<Integer> comp_graph_nodes,
            List<Integer> clique_List_org) {

        try {

            List<Integer> clique_List = extractCliqueMapping(comp_graph_nodes, clique_List_org);
            _mappings.add(clique_List);

        } catch (Exception e) {
            System.err.println("Error in FinalMapping List: " + e.getCause());
            e.printStackTrace();
            System.exit(1);
        }
        return _mappings;
    }
}
