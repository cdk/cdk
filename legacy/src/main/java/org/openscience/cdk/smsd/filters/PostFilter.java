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
package org.openscience.cdk.smsd.filters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.LinkedHashSet;
import java.util.Set;
import org.openscience.cdk.smsd.helper.FinalMappings;

/**
 * Removes duplicate index mappings, preserving their first-occurrence order.
 * No stereochemical, ring, fragment-size or bond-energy scoring is performed.
 * Results are also stored in the calling thread's {@link FinalMappings}.
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class PostFilter {

    /** Creates the legacy mapping-deduplication helper. */
    public PostFilter() {
    }

    /**
     * Deduplicates flat source-index/target-index pair lists.
     * The input's outer list is cleared after successful storage; a null or
     * empty list clears the thread-local result store. Each pair list must have
     * even length and nonnull integer keys. Repeated source keys in one list
     * use their final value, as in {@link java.util.Map#put(Object, Object)}.
     *
     * @param mappings mutable outer list of pair lists, or null for no mappings
     * @return an independent, mutable snapshot of the unique index mappings
     * @throws NullPointerException if a nonempty input contains a null mapping
     *         or a null source index
     * @throws IndexOutOfBoundsException if a pair list has odd length
     * @throws UnsupportedOperationException if the nonempty outer list cannot
     *         be cleared; the result store has already been updated in this case
     */
    public static List<Map<Integer, Integer>> filter(List<List<Integer>> mappings) {
        FinalMappings finalMappings = FinalMappings.getInstance();
        if (mappings != null && !mappings.isEmpty()) {
            finalMappings.set(removeRedundantMapping(mappings));
            mappings.clear();
        } else {
            finalMappings.set(new ArrayList<>());
        }
        return finalMappings.getFinalMapping();
    }

    /**
     *
     * @param mappingOrg
     * @return
     */
    private static List<Map<Integer, Integer>> removeRedundantMapping(List<List<Integer>> mappingOrg) {
        Set<Map<Integer, Integer>> unique = new LinkedHashSet<>();
        for (List<Integer> mapping : mappingOrg) {
            unique.add(getMappingMapFromList(mapping));
        }
        return new ArrayList<>(unique);
    }

    private static Map<Integer, Integer> getMappingMapFromList(List<Integer> list) {
        Map<Integer, Integer> newMap = new TreeMap<>();
        for (int index = 0; index < list.size(); index += 2) {
            newMap.put(list.get(index), list.get(index + 1));
        }
        return newMap;
    }
}
