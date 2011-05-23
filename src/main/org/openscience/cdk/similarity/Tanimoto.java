/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.similarity;


import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;

import java.util.BitSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 *  Calculates the Tanimoto coefficient for a given pair of two 
 *  fingerprint bitsets or real valued feature vectors.
 *
 *  The Tanimoto coefficient is one way to 
 *  quantitatively measure the "distance" or similarity of 
 *  two chemical structures. 
 *
 *  <p>You can use the FingerPrinter class to retrieve two fingerprint bitsets.
 *  We assume that you have two structures stored in cdk.Molecule objects.
 *  A tanimoto coefficient can then be calculated like:
 *  <pre>
 *   BitSet fingerprint1 = Fingerprinter.getBitFingerprint(molecule1);
 *   BitSet fingerprint2 = Fingerprinter.getBitFingerprint(molecule2);
 *   float tanimoto_coefficient = Tanimoto.calculate(fingerprint1, fingerprint2);
 *  </pre>
 *
 *  <p>The FingerPrinter assumes that hydrogens are explicitely given, if this 
 *  is desired! 
 *  <p>Note that the continuous Tanimoto coefficient does not lead to a metric space
 *
 *@author         steinbeck
 * @cdk.githash
 *@cdk.created    2005-10-19
 *@cdk.keyword    jaccard
 *@cdk.keyword    similarity, tanimoto
 * @cdk.module fingerprint
 */
@TestClass("org.openscience.cdk.similarity.TanimotoTest")
public class Tanimoto 
{

    private Tanimoto() {
    }
    
    /**
     * Evaluates Tanimoto coefficient for two bit sets.
     * <p>
     * @param bitset1 A bitset (such as a fingerprint) for the first molecule
     * @param bitset2 A bitset (such as a fingerprint) for the second molecule
     * @return The Tanimoto coefficient
     * @throws org.openscience.cdk.exception.CDKException  if bitsets are not of the same length
     */
    @TestMethod("testTanimoto1,testTanimoto2")
    public static float calculate(BitSet bitset1, BitSet bitset2) throws CDKException
    {
        float _bitset1_cardinality = bitset1.cardinality();
        float _bitset2_cardinality = bitset2.cardinality();
        if (bitset1.size() != bitset2.size()) {
            throw new CDKException("Bitsets must have the same bit length");
        }
        BitSet one_and_two = (BitSet)bitset1.clone();
        one_and_two.and(bitset2);
        float _common_bit_count = one_and_two.cardinality();
        return _common_bit_count/(_bitset1_cardinality + _bitset2_cardinality - _common_bit_count);
    }
    
    /**
     * Evaluates the continuous Tanimoto coefficient for two real valued vectors.
     * <p>
     * @param features1 The first feature vector
     * @param features2 The second feature vector
     * @return The continuous Tanimoto coefficient
     * @throws org.openscience.cdk.exception.CDKException  if the features are not of the same length
     */
    @TestMethod("testTanimoto3")
    public static float calculate(double[] features1, double[] features2) throws CDKException {

        if (features1.length != features2.length) {
            throw new CDKException("Features vectors must be of the same length");
        }

        int n = features1.length;
        double ab = 0.0;
        double a2 = 0.0;
        double b2 = 0.0;

        for (int i = 0; i < n; i++) {
            ab += features1[i] * features2[i];
            a2 += features1[i]*features1[i];
            b2 += features2[i]*features2[i];
        }
        return (float)ab/(float)(a2+b2-ab);
    }

    /**
     * Evaluate continuous Tanimoto coefficient for two feature,count fingerprint representations.
     * <p>
     * Note that feature/count type fingerprints may not be of the same length.
     * 
     * @param features1 The first feature map
     * @param features2 The second feature map
     * @return The Tanimoto coefficient
     */
    @TestMethod("testTanimoto4")
    public static float calculate(Map<String, Integer> features1, Map<String, Integer> features2) {
        Set<String> common = new TreeSet<String>(features1.keySet());
        common.retainAll(features2.keySet());
        double xy = 0., x = 0., y = 0.;
        for (String s : common) {
            int c1 = features1.get(s), c2 = features2.get(s);
            xy += Math.max(c1, c2);
        }
        for (Integer c : features1.values()) {
            x += c;
        }
        for (Integer c : features2.values()) {
            y += c;
        }
        return (float) (xy / (x + y - xy));
    }
}
