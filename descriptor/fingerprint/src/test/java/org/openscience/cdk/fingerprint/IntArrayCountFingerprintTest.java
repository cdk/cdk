package org.openscience.cdk.fingerprint;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class IntArrayCountFingerprintTest {

    @Test
    void testMerge() {
        IntArrayCountFingerprint fp1 = new IntArrayCountFingerprint(new HashMap<String, Integer>() {

            {
                put("A", 1);
                put("B", 2);
                put("C", 3);
            }
        });
        IntArrayCountFingerprint fp2 = new IntArrayCountFingerprint(new HashMap<String, Integer>() {

            {
                put("A", 1);
                put("E", 2);
                put("F", 3);
            }
        });

        Map<Integer, Integer> hashCounts = new HashMap<>();
        for (int i = 0; i < fp1.numOfPopulatedbins(); i++) {
            hashCounts.put(fp1.getHash(i), fp1.getCount(i));
        }
        for (int i = 0; i < fp2.numOfPopulatedbins(); i++) {
            int hash = fp2.getHash(i);
            Integer count = hashCounts.get(hash);
            if (count == null) {
                count = 0;
            }
            hashCounts.put(hash, count + fp2.getCount(i));
        }

        fp1.merge(fp2);

        Assertions.assertEquals(fp1.numOfPopulatedbins(), hashCounts.size());

        for (int i = 0; i < fp1.numOfPopulatedbins(); i++) {
            Integer hash = fp1.getHash(i);
            Integer count = fp1.getCount(i);
            Assertions.assertTrue(hashCounts.containsKey(hash));
            Assertions.assertEquals(count, hashCounts.get(hash));
        }

        int Aindex = Arrays.binarySearch(fp1.hitHashes, "A".hashCode());
        Assertions.assertTrue(Aindex >= 0, "A should be in the fingerprint");
        Assertions.assertEquals(fp1.numOfHits[Aindex], 2);
        int Cindex = Arrays.binarySearch(fp1.hitHashes, "C".hashCode());
        Assertions.assertTrue(Cindex >= 0, "C should be in the fingerprint");
        Assertions.assertEquals(fp1.numOfHits[Cindex], 3);
    }

}
