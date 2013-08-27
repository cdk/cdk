package org.openscience.cdk.hash;

import org.junit.Test;

import java.util.BitSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author John May
 * @cdk.module test-hash
 */
public class SuppressedTest {

    @Test
    public void none() throws Exception {
        Suppressed suppressed = Suppressed.none();
        for (int i = 0; i < 1000; i++) {
            assertFalse(suppressed.contains(i));
        }
    }

    @Test
    public void bitset() throws Exception {
        BitSet set = new BitSet();
        set.set(2);
        set.set(3);
        set.set(5);
        set.set(7);
        set.set(11);
        set.set(42);
        Suppressed suppressed = Suppressed.fromBitSet(set);

        assertTrue(suppressed.contains(2));
        assertTrue(suppressed.contains(3));
        assertTrue(suppressed.contains(5));
        assertTrue(suppressed.contains(7));
        assertTrue(suppressed.contains(11));
        assertTrue(suppressed.contains(42));

        assertFalse(suppressed.contains(0));
        assertFalse(suppressed.contains(1));
        assertFalse(suppressed.contains(4));
        assertFalse(suppressed.contains(6));
        assertFalse(suppressed.contains(8));
        assertFalse(suppressed.contains(9));
        assertFalse(suppressed.contains(10));
        assertFalse(suppressed.contains(12));
        assertFalse(suppressed.contains(13));
        assertFalse(suppressed.contains(14));

    }
}
