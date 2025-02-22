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

import java.util.Arrays;

/**
 * A lightweight stack data structure for primitive 'int' types. For general
 * purpose programming {@link java.util.ArrayDeque} is preferred (note {@link
 * java.util.Stack} is synchronised).
 *
 * @author John May
 * @see java.util.ArrayDeque
 */
final class IntStack {

    /** Storage of values. */
    private int[] xs;

    /** Number of items in the stack */
    private int n;

    /**
     * Create a new stack with specified initial capacity.
     *
     * @param n capacity of the stack
     */
    IntStack(final int n) {
        this.xs = new int[n];
    }

    /**
     * Push the value {@literal x} on to the stack.
     *
     * @param x value to push
     */
    void push(final int x) {
        if (n == xs.length)
            xs = Arrays.copyOf(xs, xs.length * 2);
        xs[n++] = x;
    }

    /**
     * Access and remove the value on the top of the stack. No check is made as
     * to whether the stack is empty.
     *
     * @return value on top of the stack
     */
    int pop() {
        return xs[--n];
    }

    /**
     * Access the value on top of the stack without removing it. No check is
     * made as to whether the stack is empty.
     *
     * @return the last value added
     */
    int peek() {
        return xs[n - 1];
    }

    /**
     * Determine if there are any items on the stack.
     *
     * @return whether the stack is empty
     */
    boolean empty() {
        return n == 0;
    }

    /**
     * Number of items on the stack.
     *
     * @return size
     */
    public int size() {
        return n;
    }

    /** Remove all values from the stack. */
    public void clear() {
        n = 0;
    }
}
