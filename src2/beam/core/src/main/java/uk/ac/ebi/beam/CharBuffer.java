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
 * A character buffer with utilities for sequential processing of characters.
 *
 * @author John May
 */
final class CharBuffer {

    /** Characters stored in a fixed size array. */
    public char[] cs;

    /** Current position. */
    public int position;

    /**
     * Internal constructor
     *
     * @param cs array of characters
     */
    private CharBuffer(char[] cs) {
        this.cs = cs;
    }

    /**
     * Determine if there are any characters remaining in the buffer. There are
     * no characters remaining when the position has reached the end of the
     * array.
     *
     * @return the position has reached the end of the array
     */
    boolean hasRemaining() {
        return position < cs.length;
    }

    /**
     * The current position in the buffer.
     *
     * @return current position
     */
    int position() {
        return position;
    }

    /**
     * Access the next character in the buffer and progress the position.
     *
     * @return the next character
     * @see #next()
     */
    char get() {
        return cs[position++];
    }

    /**
     * Access the the next character in the buffer without progressing the
     * position.
     *
     * @return the next character
     * @see #get()
     */
    char next() {
        return cs[position];
    }

    /**
     * Determine if the next character is a digit. The buffer is first checked
     * to ensure there are characters remaining.
     *
     * @return whether there is a 'next' character and it is a digit.
     */
    boolean nextIsDigit() {
        return hasRemaining() && isDigit(next());
    }

    /**
     * Access the next character as a digit, the buffer position will progress.
     * No check is made that there are characters remaining and the next
     * character is a digit.
     *
     * @return the next character in the buffer as a digit.
     * @see #nextIsDigit()
     */
    int getAsDigit() {
        return toDigit(get());
    }

    /**
     * Access the next character as a digit, the buffer position does not
     * progress. No check is made that there are characters remaining and the
     * next character is a digit.
     *
     * @return the next character in the buffer as a digit.
     * @see #nextIsDigit()
     */
    int nextAsDigit() {
        return toDigit(next());
    }

    /**
     * Determine if the next character is {@literal c}.
     *
     * @param c test if the next character is
     * @return whether there are characters remaining and the
     */
    boolean nextIs(char c) {
        return hasRemaining() && cs[position] == c;
    }

    /**
     * Progress the buffer if the next character is the same as the provided
     * character.
     *
     * @param c a character
     * @return whether the buffer progressed and the character matched
     */
    boolean getIf(final char c) {
        if (hasRemaining() && nextIs(c)) {
            position++;
            return true;
        }
        return false;
    }

    /**
     * Get a sequence of digits from the buffer as a positive integer.  The
     * buffer is progressed until the end of the number. If the characters do
     * not represent a number then -1 is returned and the buffer is not
     * progressed.
     *
     * @return the number read, < 0 if no number read
     */
    int getNumber() {
        if (!nextIsDigit())
            return -1;
        int num = getAsDigit();
        while (nextIsDigit())
            num = (num * 10) + getAsDigit();
        return num;
    }

    /**
     * Get a sequence of specified digits from the buffer as a positive integer.
     * The buffer is progressed until the end of the number. If the characters do
     * not represent a number then -1 is returned and the buffer is not
     * progressed.
     *
     * @param nDigits the number of digits to read
     *
     * @return the number read, < 0 if no number read
     */
    int getNumber(int nDigits) {
        if (!nextIsDigit())
            return -1;
        int num = getAsDigit();
        while (--nDigits > 0 && nextIsDigit())
            num = (num * 10) + getAsDigit();
        return num;
    }

    /**
     * Obtain the string of characters 'from' - 'to' the specified indices.
     *
     * @param from start index
     * @param to   end index
     * @return the string between the indices
     */
    String substr(int from, int to) {
        return new String(Arrays.copyOfRange(cs, from, to));
    }

    /**
     * The number of characters in the buffer.
     *
     * @return length of the buffer
     */
    int length() {
        return cs.length;
    }

    /**
     * Determine if the specified character 'c' is a digit (0-9).
     *
     * @param c a character
     * @return the character is a digit
     */
    static boolean isDigit(char c) {
        // Character.isDigit allows 'any' unicode digit, we don't need that
        return c >= '0' && c <= '9';
    }

    /**
     * Convert the specified character to the corresponding integral digit.
     * Note, no check is made as to whether the character is actually a digit
     * which should be performed with {@link #isDigit(char)}.
     *
     * @param c a character
     * @return the digit for character
     * @see #isDigit(char)
     */
    static int toDigit(char c) {
        return c - '0';
    }

    /**
     * Create a buffer from a string.
     *
     * @param str string
     * @return new char buffer
     */
    static CharBuffer fromString(String str) {
        return new CharBuffer(str.toCharArray());
    }

    /** @inheritDoc */
    @Override public String toString() {
        return new String(cs);
    }
}
