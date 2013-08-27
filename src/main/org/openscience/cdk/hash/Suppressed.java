package org.openscience.cdk.hash;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;

import java.util.BitSet;

/**
 * Defines a structure which indicates whether a vertex (int id) is suppressed
 * when computing an atomic/molecular hash code.
 *
 * @author John May
 * @cdk.module hash
 */
@TestClass("org.openscience.cdk.hash.SupressedTest")
abstract class Suppressed {

    /**
     * Is the vertex 'i' contained in the vertices which should be suppressed.
     *
     * @param i vertex index
     * @return the vertex is supressed
     */
    abstract boolean contains(int i);

    /**
     * The total number of suppressed vertices.
     *
     * @return number of suppressed vertices 0 .. |V|
     */
    abstract int count();

    /**
     * Access which vertices are suppressed as a fixed-size array.
     * 
     * @return the suppressed vertices
     */
    abstract int[] toArray();

    /** Default 'empty' implementation always returns false. */
    private static final class Empty extends Suppressed {
        /** @inheritDoc */
        @Override boolean contains(int i) {
            return false;
        }

        /** @inheritDoc */
        @Override int count() {
            return 0;
        }

        /** @inheritDoc */
        @Override int[] toArray() {
            return new int[0];
        }
    }

    /**
     * Implementation where the suppressed vertices are indicated with a
     * BitSet.
     */
    private static final class SuppressedBitSet extends Suppressed {

        /** Bits indicate suppressed vertices. */
        private final BitSet set;

        /**
         * Create a new suppressed instance with the specified vertices
         * suppressed.
         *
         * @param set bits indicates suppressed
         */
        private SuppressedBitSet(BitSet set) {
            this.set = set;
        }

        /** @inheritDoc */
        @Override boolean contains(int i) {
            return set.get(i);
        }

        /** @inheritDoc */
        @Override int count() {
            return set.cardinality();
        }
        
        /** @inheritDoc */        
        @Override int[] toArray() {
            int[] xs = new int[count()];
            int   n  = 0;
            for (int i = set.nextSetBit(0); i >= 0; i = set.nextSetBit(i + 1)) {
                xs[n++] = i;
            }
            return xs;
        }
    }

    /** default implementation. */
    private static final Empty empty = new Empty();

    /**
     * Access a suppressed implementation where no vertices are suppressed.
     *
     * @return implementation where all vertices are unsuppressed
     */
    @TestMethod("none")
    static Suppressed none() {
        return empty;
    }

    /**
     * Create a suppressed implementation for the provided BitSet.
     *
     * @param set bits indicated suppressed vertices
     * @return implementation using the BitSet to lookup suppressed vertices
     */
    @TestMethod("bitset")
    static Suppressed fromBitSet(BitSet set) {
        return new SuppressedBitSet(set);
    }
}
