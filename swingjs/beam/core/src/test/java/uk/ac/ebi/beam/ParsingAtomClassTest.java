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
import static org.junit.Assert.assertThat;

/**
 * Unit tests verify correct handling of atom class in bracket atoms.
 *
 * @author John May
 */
public class ParsingAtomClassTest {

    @Test(expected = InvalidSmilesException.class)
    public void invalid() throws Exception {
        verify(":", 0);
    }

    @Test public void none() throws Exception {
        verify("", 0);
    }

    @Test public void none2() throws Exception {
        verify("]", 0);
    }

    @Test public void zero() throws Exception {
        verify(":0", 0);
    }

    @Test public void one() throws Exception {
        verify(":1", 1);
    }

    @Test public void two() throws Exception {
        verify(":2", 2);
    }

    @Test public void fortyTwo() throws Exception {
        verify(":42", 42);
    }

    @Test public void fivePadded() throws Exception {
        verify(":005", 5);
    }

    @Test public void fortyTwoPadded() throws Exception {
        verify(":042", 42);
    }

    private void verify(String str, int atomClass) throws
                                                   InvalidSmilesException {
        assertThat(Parser.readClass(CharBuffer.fromString(str)), is(atomClass));
    }
}
