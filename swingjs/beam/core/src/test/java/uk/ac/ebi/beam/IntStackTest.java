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

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/** @author John May */
public class IntStackTest {

    @Test public void push() throws Exception {
        IntStack stack = new IntStack(4);
        stack.push(1);
        assertThat(stack.peek(), is(1));
        assertThat(stack.size(), is(1));
        stack.push(2);
        assertThat(stack.peek(), is(2));
        assertThat(stack.size(), is(2));
        stack.push(4);
        assertThat(stack.peek(), is(4));
        assertThat(stack.size(), is(3));
    }

    @Test public void pushWithResize() throws Exception {
        IntStack stack = new IntStack(1);
        stack.push(1);
        assertThat(stack.peek(), is(1));
        assertThat(stack.size(), is(1));
        stack.push(2);
        assertThat(stack.peek(), is(2));
        assertThat(stack.size(), is(2));
        stack.push(4);
        assertThat(stack.peek(), is(4));
        assertThat(stack.size(), is(3));
    }

    @Test public void pushDuplicate() throws Exception {
        IntStack stack = new IntStack(4);
        stack.push(1);
        assertThat(stack.peek(), is(1));
        assertThat(stack.size(), is(1));
        stack.push(stack.peek());
        assertThat(stack.peek(), is(1));
        assertThat(stack.size(), is(2));
        stack.push(stack.peek());
        assertThat(stack.peek(), is(1));
        assertThat(stack.size(), is(3));
    }

    @Test public void pop() throws Exception {
        IntStack stack = new IntStack(4);
        stack.push(1);
        stack.push(2);
        stack.push(3);
        assertThat(stack.pop(), is(3));
        assertThat(stack.pop(), is(2));
        assertThat(stack.pop(), is(1));
    }

    @Test public void popWithResize() throws Exception {
        IntStack stack = new IntStack(1);
        stack.push(1);
        stack.push(2);
        stack.push(3);
        assertThat(stack.pop(), is(3));
        assertThat(stack.pop(), is(2));
        assertThat(stack.pop(), is(1));
    }

    @Test public void empty() throws Exception {
        assertTrue(new IntStack(4).empty());
    }

    @Test public void nonEmpty() throws Exception {
        IntStack stack = new IntStack(4);
        assertTrue(stack.empty());
        stack.push(1);
        assertFalse(stack.empty());
        stack.pop();
        assertTrue(stack.empty());
    }

    @Test public void size() throws Exception {
        assertThat(new IntStack(4).size(), is(0));
    }

    @Test public void clear() throws Exception {
        IntStack stack = new IntStack(1);
        stack.push(1);
        assertThat(stack.peek(), is(1));
        assertThat(stack.size(), is(1));
        stack.push(2);
        assertThat(stack.peek(), is(2));
        assertThat(stack.size(), is(2));
        stack.push(4);
        assertThat(stack.peek(), is(4));
        assertThat(stack.size(), is(3));
        stack.clear();
        assertThat(stack.size(), is(0));
        stack.push(4);
        assertThat(stack.peek(), is(4));
        assertThat(stack.size(), is(1));
        stack.push(8);
        assertThat(stack.peek(), is(8));
        assertThat(stack.size(), is(2));
        stack.push(9);
        assertThat(stack.peek(), is(9));
        assertThat(stack.size(), is(3));
    }
}
