package org.openscience.cdk.graph;

import java.util.BitSet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author maclean
 */
class PermutorTest {

    private int factorial(int n) {
        if (n <= 1) {
            return 1;
        } else {
            return n * factorial(n - 1);
        }
    }

    private int[] getIdentity(int size) {
        int[] identity = new int[size];
        for (int index = 0; index < size; index++) {
            identity[index] = index;
        }
        return identity;
    }

    private boolean arrayElementsDistinct(int[] array) {
        BitSet bitSet = new BitSet(array.length);
        for (int index = 0; index < array.length; index++) {
            if (bitSet.get(index)) {
                return false;
            } else {
                bitSet.set(index);
            }
        }
        return true;
    }

    @Test
    void constructorTest() {
        int size = 4;
        Permutor permutor = new Permutor(size);
        int[] current = permutor.getCurrentPermutation();
        Assertions.assertArrayEquals(getIdentity(size), current);
    }

    @Test
    void hasNextTest() {
        int size = 4;
        Permutor permutor = new Permutor(size);
        Assertions.assertTrue(permutor.hasNext());
    }

    @Test
    void setRankTest() {
        int size = 4;
        int[] reverse = new int[]{3, 2, 1, 0};
        Permutor permutor = new Permutor(size);
        // out of 4! = 24 permutations, numbered 0-23, this is the last
        permutor.setRank(23);
        Assertions.assertArrayEquals(reverse, permutor.getCurrentPermutation());
    }

    @Test
    void getRankTest() {
        int size = 4;
        int rank = 10;
        Permutor permutor = new Permutor(size);
        permutor.setRank(rank);
        Assertions.assertEquals(rank, permutor.getRank());
    }

    @Test
    void setPermutationTest() {
        int size = 4;
        int[] target = new int[]{3, 1, 0, 2};
        Permutor permutor = new Permutor(size);
        permutor.setPermutation(target);
        Assertions.assertArrayEquals(target, permutor.getCurrentPermutation());
    }

    @Test
    void countGeneratedPermutations() {
        int size = 4;
        Permutor permutor = new Permutor(size);
        int count = 1; // the identity permutation is not generated
        while (permutor.hasNext()) {
            permutor.getNextPermutation();
            count++;
        }
        Assertions.assertEquals(factorial(size), count);
    }

    @Test
    void getCurrentPermutationTest() {
        int size = 4;
        Permutor permutor = new Permutor(size);
        boolean allOk = true;
        while (permutor.hasNext()) {
            permutor.getNextPermutation();
            int[] current = permutor.getCurrentPermutation();
            if (arrayElementsDistinct(current)) {
                continue;
            } else {
                allOk = false;
                break;
            }
        }
        Assertions.assertTrue(allOk);
    }

    @Test
    void maxRankTest() {
        int size = 4;
        Permutor permutor = new Permutor(size);
        Assertions.assertEquals(factorial(size) - 1, permutor.calculateMaxRank());
    }

    @Test
    void getRandomNextTest() {
        int size = 4;
        Permutor permutor = new Permutor(size);
        int[] random = permutor.getRandomNextPermutation();
        Assertions.assertTrue(arrayElementsDistinct(random));
    }

}
