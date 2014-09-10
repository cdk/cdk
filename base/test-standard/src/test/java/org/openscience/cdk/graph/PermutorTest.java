package org.openscience.cdk.graph;

import java.util.BitSet;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author maclean
 * @cdk.module test-standard
 */
public class PermutorTest {

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
    public void constructorTest() {
        int size = 4;
        Permutor permutor = new Permutor(size);
        int[] current = permutor.getCurrentPermutation();
        Assert.assertArrayEquals(getIdentity(size), current);
    }

    @Test
    public void hasNextTest() {
        int size = 4;
        Permutor permutor = new Permutor(size);
        Assert.assertTrue(permutor.hasNext());
    }

    @Test
    public void setRankTest() {
        int size = 4;
        int[] reverse = new int[]{3, 2, 1, 0};
        Permutor permutor = new Permutor(size);
        // out of 4! = 24 permutations, numbered 0-23, this is the last
        permutor.setRank(23);
        Assert.assertArrayEquals(reverse, permutor.getCurrentPermutation());
    }

    @Test
    public void getRankTest() {
        int size = 4;
        int rank = 10;
        Permutor permutor = new Permutor(size);
        permutor.setRank(rank);
        Assert.assertEquals(rank, permutor.getRank());
    }

    @Test
    public void setPermutationTest() {
        int size = 4;
        int[] target = new int[]{3, 1, 0, 2};
        Permutor permutor = new Permutor(size);
        permutor.setPermutation(target);
        Assert.assertArrayEquals(target, permutor.getCurrentPermutation());
    }

    @Test
    public void countGeneratedPermutations() {
        int size = 4;
        Permutor permutor = new Permutor(size);
        int count = 1; // the identity permutation is not generated
        while (permutor.hasNext()) {
            permutor.getNextPermutation();
            count++;
        }
        Assert.assertEquals(factorial(size), count);
    }

    @Test
    public void getCurrentPermutationTest() {
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
        Assert.assertTrue(allOk);
    }

    @Test
    public void maxRankTest() {
        int size = 4;
        Permutor permutor = new Permutor(size);
        Assert.assertEquals(factorial(size) - 1, permutor.calculateMaxRank());
    }

    @Test
    public void getRandomNextTest() {
        int size = 4;
        Permutor permutor = new Permutor(size);
        int[] random = permutor.getRandomNextPermutation();
        Assert.assertTrue(arrayElementsDistinct(random));
    }

}
