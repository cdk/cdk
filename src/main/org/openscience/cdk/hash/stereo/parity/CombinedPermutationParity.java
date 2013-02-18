package org.openscience.cdk.hash.stereo.parity;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;

/**
 * Combine two permutation parities into one.
 *
 * @author John May
 * @cdk.module hash
 */
@TestClass("org.openscience.cdk.hash.stereo.parity.CombinedPermutationParityTest")
public final class CombinedPermutationParity implements PermutationParity {

    private final PermutationParity left;
    private final PermutationParity right;

    /**
     * Combines the left and right parity into a single parity. This parity is
     * the product of the two separate parities.
     *
     * @param left  either parity
     * @param right other parity
     */
    public CombinedPermutationParity(PermutationParity left, PermutationParity right) {
        this.left = left;
        this.right = right;
    }

    /**
     * @inheritDoc
     */
    @TestMethod("testParity")
    @Override public int parity(long[] current) {
        return left.parity(current) * right.parity(current);
    }
}

