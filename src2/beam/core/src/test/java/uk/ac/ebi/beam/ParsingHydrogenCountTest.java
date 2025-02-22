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
import static org.junit.Assert.assertTrue;

/**
 * Unit tests verify correct handling of hydrogen count for bracket atoms.
 *
 * @author John May
 */
public class ParsingHydrogenCountTest {

    @Test public void impliedHCount() {
        verify("H", 1);
    }

    @Test public void H0() {
        verify("H0", 0);
    }

    @Test public void H1() {
        verify("H1", 1);
    }

    @Test public void H2() {
        verify("H2", 2);
    }

    @Test public void H3() {
        verify("H3", 3);
    }

    @Test public void H4() {
        verify("H4", 4);
    }

    @Test public void H42() {
        verify("H42", 42);
    }

    @Test public void H101() {
        verify("H101", 101);
    }

    @Test public void noHCount() {
        CharBuffer buffer = CharBuffer.fromString("-1");
        assertThat(Parser.readHydrogens(buffer), is(0));
        assertTrue(buffer.nextIs('-'));
    }

    private void verify(String str, int count) {
        assertThat(Parser.readHydrogens(CharBuffer.fromString(str)), is(count));
    }
}
