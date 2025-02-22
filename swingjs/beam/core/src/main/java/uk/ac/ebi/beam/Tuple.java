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

/**
 * A simple utility class for storing two primitive integers.
 *
 * @author John May
 */
final class Tuple {

    private final int fst, snd;

    private Tuple(int fst, int snd) {
        this.fst = fst;
        this.snd = snd;
    }

    /**
     * Access the first value of the tuple.
     *
     * @return value
     */
    int first() {
        return fst;
    }

    /**
     * Access the second value of the tuple.
     *
     * @return value
     */
    int second() {
        return snd;
    }

    /** @inheritDoc */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple tuple = (Tuple) o;

        if (fst != tuple.fst) return false;
        if (snd != tuple.snd) return false;

        return true;
    }

    /** @inheritDoc */
    @Override
    public int hashCode() {
        int result = fst;
        result = 31 * result + snd;
        return result;
    }

    /** @inheritDoc */
    @Override public String toString() {
        return "{" + fst + ", " + snd + "}";
    }

    /**
     * Create a new tuple for the provided values.
     *
     * @param fst a value
     * @param snd another value
     * @return a tuple of the two values
     */
    static Tuple of(int fst, int snd) {
        return new Tuple(fst, snd);
    }
}
