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
import java.util.Objects;

/**
 * This class handles MCS between two identical molecules.
 * Hence they generate am MCS where all atoms are mapped.
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class ExactMapping {

    /**
     *
     * Extract atom mapping from the cliques and stores it in a List
     * @param compGraphNodes
     * @param cliqueListOrg
     */
    private static List<Integer> extractCliqueMapping(List<Integer> compGraphNodes, List<Integer> cliqueListOrg) {

        List<Integer> cliqueMapping = new ArrayList<>();
        List<Integer> cliqueList = new ArrayList<>(cliqueListOrg);
        int cliqueSize = cliqueList.size();
        int vecSize = compGraphNodes.size();
        //        System.out.println("VEC  SIZE " + vec_size);
        for (Integer integer : cliqueList) {
            for (int b = 0; b < vecSize; b += 3) {
                if (Objects.equals(integer, compGraphNodes.get(b + 2))) {
                    cliqueMapping.add(compGraphNodes.get(b));
                    cliqueMapping.add(compGraphNodes.get(b + 1));
                }
            }
        }

        return cliqueMapping;
    }

    //extract atom mapping from the clique List and print it on the screen
    /**
     *
     * @param mappings
     * @param compGraphNodes
     * @param cliqueListOrg
     * @return mappings
     */
    public static List<List<Integer>> extractMapping(List<List<Integer>> mappings, List<Integer> compGraphNodes,
            List<Integer> cliqueListOrg) {
        try {
            List<Integer> cliqueList = extractCliqueMapping(compGraphNodes, cliqueListOrg);
            mappings.add(cliqueList);
        } catch (Exception e) {
            System.err.println("Error in FinalMapping List: " + e.getCause());
            System.exit(1);
        }
        return mappings;
    }
}
