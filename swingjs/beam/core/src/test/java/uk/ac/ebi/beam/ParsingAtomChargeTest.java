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
 * Unit tests verify correct handling of charge for bracket atoms.
 *
 * @author John May
 */
public class ParsingAtomChargeTest {

    @Test public void implicitPlusOne() {
        verify("+", +1);
    }

    @Test public void implicitPlusTwo() {
        verify("++", +2);
    }

    @Test public void implicitPlusThree() {
        verify("+++", +3);
    }

    @Test public void implicitPlusFour() {
        verify("++++", +4);
    }

    @Test public void implicitMinusOne() {
        verify("-", -1);
    }

    @Test public void implicitMinusTwo() {
        verify("--", -2);
    }

    @Test public void implicitMinusThree() {
        verify("---", -3);
    }

    @Test public void implicitMinusFour() {
        verify("----", -4);
    }

    @Test public void plusOne() {
        verify("+1", +1);
    }

    @Test public void plusTwo() {
        verify("+2", +2);
    }

    @Test public void minusOne() {
        verify("-1", -1);
    }

    @Test public void minusTwo() {
        verify("-2", -2);
    }

    @Test public void noCharge() {
        CharBuffer buffer = CharBuffer.fromString(":");
        assertThat(Parser.readCharge(buffer), is(0));
        assertTrue(buffer.nextIs(':'));
    }

    // really bad form but parsed okay
    @Test public void minusPlusOne() {
        verify("-+1", 0);
    }

    // really bad form but parsed okay
    @Test public void plusPlusMinusOne() {
        verify("++-1", +1);
    }

    // really bad form but parsed okay
    @Test public void minusMinusPlusOne() {
        verify("--+1", -1);
    }

    // really bad form but parsed okay
    @Test public void plusMinusOne() {
        verify("+-1", 0);
    }

    // really bad form but parsed okay
    @Test public void plusPlusOne() {
        verify("++1", 2);
    }

    // really bad form but parsed okay
    @Test public void plusPlusTwo() {
        verify("++2", 3);
    }

    // An implementation is required to accept charges in the range -15 to +15
    @Test public void rangeCheck() {
        for (int i = -15; i <= 15; i++)
            verify((i > 0 ? "+" : "") + Integer.toString(i), i);
    }

    private void verify(String str, int charge) {
        assertThat(Parser.readCharge(CharBuffer.fromString(str)), is(charge));
    }
}
