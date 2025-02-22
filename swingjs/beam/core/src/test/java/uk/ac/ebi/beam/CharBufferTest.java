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
public class CharBufferTest {

    @Test public void emptyBufferHasNoneRemaining() throws Exception {
        CharBuffer buffer = CharBuffer.fromString("");
        assertFalse(buffer.hasRemaining());
    }

    @Test public void nonEmptyBufferHasRemaining() throws Exception {
        CharBuffer buffer = CharBuffer.fromString("-");
        assertTrue(buffer.hasRemaining());
    }

    @Test public void endOfBufferHasNoneRemaining() throws Exception {
        CharBuffer buffer = CharBuffer.fromString("-");
        assertTrue(buffer.hasRemaining());
        buffer.get();
        assertFalse(buffer.hasRemaining());
    }

    @Test public void position() {
        assertThat(CharBuffer.fromString("").position(), is(0));
        CharBuffer buffer = CharBuffer.fromString("...");
        assertThat(buffer.position(), is(0));
        assertThat(buffer.get(), is('.'));
        assertThat(buffer.position(), is(1));
        assertThat(buffer.get(), is('.'));
        assertThat(buffer.position(), is(2));
        assertThat(buffer.get(), is('.'));
        assertThat(buffer.position(), is(3));
    }

    @Test public void length() {
        assertThat(CharBuffer.fromString("").length(), is(0));
        assertThat(CharBuffer.fromString(".").length(), is(1));
        assertThat(CharBuffer.fromString("..").length(), is(2));
        assertThat(CharBuffer.fromString("...").length(), is(3));
    }

    @Test public void getProgressesPosition() {
        CharBuffer buffer = CharBuffer.fromString("abcd");
        assertThat(buffer.get(), is('a'));
        assertThat(buffer.get(), is('b'));
        assertThat(buffer.get(), is('c'));
        assertThat(buffer.get(), is('d'));
    }

    @Test public void nextDoesNotProgressPosition() {
        CharBuffer buffer = CharBuffer.fromString("abcd");
        assertThat(buffer.next(), is('a'));
        assertThat(buffer.position(), is(0));
        assertThat(buffer.next(), is('a'));
        assertThat(buffer.position(), is(0));
        assertThat(buffer.next(), is('a'));
        assertThat(buffer.position(), is(0));
        assertThat(buffer.next(), is('a'));
        assertThat(buffer.position(), is(0));
        buffer.get();
        assertThat(buffer.position(), is(1));
        assertThat(buffer.next(), is('b'));
        assertThat(buffer.position(), is(1));
        assertThat(buffer.next(), is('b'));
    }

    @Test public void isDigit() {
        for (char c = '0'; c <= '9'; c++)
            assertTrue(CharBuffer.isDigit(c));
        for (char c = 'a'; c <= 'z'; c++)
            assertFalse(CharBuffer.isDigit(c));
        for (char c = 'A'; c <= 'Z'; c++)
            assertFalse(CharBuffer.isDigit(c));
    }

    @Test public void toDigit() {
        assertThat(CharBuffer.toDigit('0'), is(0));
        assertThat(CharBuffer.toDigit('1'), is(1));
        assertThat(CharBuffer.toDigit('2'), is(2));
        assertThat(CharBuffer.toDigit('3'), is(3));
        assertThat(CharBuffer.toDigit('4'), is(4));
        assertThat(CharBuffer.toDigit('5'), is(5));
        assertThat(CharBuffer.toDigit('6'), is(6));
        assertThat(CharBuffer.toDigit('7'), is(7));
        assertThat(CharBuffer.toDigit('8'), is(8));
        assertThat(CharBuffer.toDigit('9'), is(9));
    }

    @Test public void nextIsDigit() {
        CharBuffer buffer = CharBuffer.fromString("c1");
        assertFalse(buffer.nextIsDigit());
        assertThat(buffer.get(), is('c'));
        assertTrue(buffer.nextIsDigit());
        assertThat(buffer.get(), is('1'));
        assertFalse(buffer.nextIsDigit());
    }

    @Test public void getAsDigit() {
        CharBuffer buffer = CharBuffer.fromString("c1");
        assertFalse(buffer.nextIsDigit());
        assertThat(buffer.get(), is('c'));
        assertTrue(buffer.nextIsDigit());
        assertThat(buffer.getAsDigit(), is(1));
        assertFalse(buffer.nextIsDigit());
    }

    @Test public void nextAsDigit() {
        CharBuffer buffer = CharBuffer.fromString("c1");
        assertFalse(buffer.nextIsDigit());
        assertThat(buffer.get(), is('c'));
        assertTrue(buffer.nextIsDigit());
        assertThat(buffer.nextAsDigit(), is(1));
        assertTrue(buffer.nextIsDigit());
        assertThat(buffer.getAsDigit(), is(1));
        assertFalse(buffer.nextIsDigit());
    }

    @Test public void nextIsEmpty() {
        assertFalse(CharBuffer.fromString("").nextIs('?'));
    }

    @Test public void nextIs() {
        CharBuffer buffer = CharBuffer.fromString("[C@H]");

        assertFalse(buffer.nextIs('C'));
        assertFalse(buffer.nextIs('@'));
        assertFalse(buffer.nextIs('H'));
        assertFalse(buffer.nextIs(']'));
        assertTrue(buffer.nextIs('['));
        assertThat(buffer.get(), is('['));

        assertFalse(buffer.nextIs('['));
        assertFalse(buffer.nextIs('@'));
        assertFalse(buffer.nextIs('H'));
        assertFalse(buffer.nextIs(']'));
        assertTrue(buffer.nextIs('C'));
        assertThat(buffer.get(), is('C'));

        assertFalse(buffer.nextIs('['));
        assertFalse(buffer.nextIs('C'));
        assertFalse(buffer.nextIs('H'));
        assertFalse(buffer.nextIs(']'));
        assertTrue(buffer.nextIs('@'));
        assertThat(buffer.get(), is('@'));

        assertFalse(buffer.nextIs('['));
        assertFalse(buffer.nextIs('C'));
        assertFalse(buffer.nextIs('@'));
        assertFalse(buffer.nextIs(']'));
        assertTrue(buffer.nextIs('H'));
        assertThat(buffer.get(), is('H'));

        assertFalse(buffer.nextIs('['));
        assertFalse(buffer.nextIs('C'));
        assertFalse(buffer.nextIs('@'));
        assertFalse(buffer.nextIs('H'));
        assertTrue(buffer.nextIs(']'));
        assertThat(buffer.get(), is(']'));
    }

    @Test public void getSingleDigitNumber() {
        assertThat(CharBuffer.fromString("1").getNumber(), is(1));
        CharBuffer buffer = CharBuffer.fromString("2C");
        assertThat(buffer.getNumber(), is(2));
        assertThat(buffer.next(), is('C'));
    }

    @Test public void getTwoDigitNumber() {
        assertThat(CharBuffer.fromString("12").getNumber(), is(12));
        CharBuffer buffer = CharBuffer.fromString("20C");
        assertThat(buffer.getNumber(), is(20));
        assertThat(buffer.next(), is('C'));
    }

    @Test public void getThreeDigitNumber() {
        assertThat(CharBuffer.fromString("123").getNumber(), is(123));
        CharBuffer buffer = CharBuffer.fromString("212C");
        assertThat(buffer.getNumber(), is(212));
        assertThat(buffer.next(), is('C'));
    }

    @Test public void getThreeDigitNumber_2DigitsOnly() {
        assertThat(CharBuffer.fromString("123").getNumber(2), is(12));
    }

    @Test public void getNumberWithLeadingZeros() {
        assertThat(CharBuffer.fromString("0002").getNumber(), is(2));
        CharBuffer buffer = CharBuffer.fromString("002H");
        assertThat(buffer.getNumber(), is(2));
        assertThat(buffer.next(), is('H'));
    }

    @Test public void nonNumber() {
        CharBuffer buffer = CharBuffer.fromString("H3");
        assertThat(buffer.getNumber(), is(-1));
        assertThat(buffer.next(), is('H'));
    }
}
