/* Copyright (C) 2010  Rajarshi Guha <rajarshi.guha@gmail.com>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
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
package org.openscience.cdk.similarity;


import java.util.Map;
import java.util.TreeSet;

/**
 * A class to evaluate the similarity between two LINGO's as described in {@cdk.cite Vidal2005}.
 *
 * The similarity calculation is a variant of the Tanimoto coefficient and hence its
 * value ranges from 0 to 1
 *
 * @author Rajarshi Guha
 * @cdk.githash
 * @cdk.keyword lingo
 * @cdk.keyword similarity, tanimoto
 * @cdk.module fingerprint
 */
public class LingoSimilarity {

    private LingoSimilarity() {}

    /**
     * Evaluate the LINGO similarity between two key,value sty;e fingerprints.
     *
     * The value will range from 0.0 to 1.0.
     *
     * @param features1
     * @param features2
     * @return similarity
     */
    public static float calculate(Map<String, Integer> features1, Map<String, Integer> features2) {
        TreeSet<String> keys = new TreeSet<String>(features1.keySet());
        keys.addAll(features2.keySet());

        float sum = 0.0f;
        for (String key : keys) {
            Integer c1 = features1.get(key);
            Integer c2 = features2.get(key);

            c1 = c1 == null ? 0 : c1;
            c2 = c2 == null ? 0 : c2;
            sum += 1.0 - Math.abs(c1 - c2) / (c1 + c2);
        }

        return sum / keys.size();
    }
}
