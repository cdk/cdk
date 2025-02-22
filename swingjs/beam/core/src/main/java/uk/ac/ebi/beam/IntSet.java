/*
 * Copyright (c) 2013, European Bioinformatics Institute (EMBL-EBI)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of the FreeBSD Project.
 */

package uk.ac.ebi.beam;

import java.util.BitSet;

/**
 * Abstraction allows simple definitions of integer sets. Generally for this
 * library we are dealing with small bounded integer ranges (vertices of a
 * graph) which are most efficiently represented as a binary set. For
 * convenience the {@link #allOf(int, int...)} method can be used to construct a
 * binary set from varargs.
 *
 * @author John May
 */
abstract class IntSet {

    /**
     * Determine if value 'x' is a member of this set.
     *
     * @param x a value to
     * @return x is included in this set.
     */
    abstract boolean contains(int x);

    /**
     * The universe is a set which includes every int value.
     *
     * @return int set with every item
     */
    static IntSet universe() {
        return UNIVERSE;
    }

    /**
     * The empty set is a set which includes no int values.
     *
     * @return int set with no items
     */
    static IntSet empty() {
        return complement(universe());
    }

    /**
     * Convenience method to create a set with the specified contents.
     *
     * <blockquote> IntSet.allOf(0, 2, 5); // a set with 0,2 and 5
     * </blockquote>
     *
     * @param x  a value
     * @param xs more values
     * @return int set with specified items
     */
    static IntSet allOf(int x, int... xs) {
        BitSet s = new BitSet();
        s.set(x);
        for (int v : xs)
            s.set(v);
        return new BinarySet(s);
    }

    /**
     * Convenience method to create a set without the specified contents.
     *
     * <blockquote> IntSet.noneOf(0, 2, 5); // a set with all but 0,2 and 5
     * </blockquote>
     *
     * @param x  a value
     * @param xs more values
     * @return int set without the specified items
     */
    static IntSet noneOf(int x, int... xs) {
        return complement(allOf(x, xs));
    }

    /**
     * Create an set from a BitSet.
     *
     * @param s bitset
     * @return int set which uses the bit set to test for membership
     */
    static IntSet fromBitSet(BitSet s) {
        return new BinarySet((BitSet) s.clone());
    }

    /**
     * Make a complement of the specified set.
     *
     * @param set a set
     * @return complement of the set
     */
    private static IntSet complement(IntSet set) {
        return new Complement(set);
    }

    /** An integer set based on the contents of a bit set. */
    private static final class BinarySet extends IntSet {
        private final BitSet s;

        private BinarySet(BitSet s) {
            this.s = s;
        }

        @Override boolean contains(int x) {
            return s.get(x);
        }
    }

    /** Complement of a set - invert any membership of the provided 'delegate' */
    private static final class Complement extends IntSet {
        private final IntSet delegate;

        private Complement(IntSet delegate) {
            this.delegate = delegate;
        }

        @Override boolean contains(int x) {
            return !delegate.contains(x);
        }
    }

    /** The universe - every object is a member of the set. */
    private static final IntSet UNIVERSE = new IntSet() {
        @Override boolean contains(int x) {
            return true;
        }
    };
}
