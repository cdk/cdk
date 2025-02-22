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

import java.io.IOException;

/**
 * An exception thrown when parsing malformed SMILES.
 *
 * @author John May
 */
final class InvalidSmilesException extends IOException {

    InvalidSmilesException(String message, CharBuffer buffer) {
        this(message, buffer, 0);
    }
    
    InvalidSmilesException(String message, CharBuffer buffer, int offset) {
        super(message + display(buffer, offset));
    }

    InvalidSmilesException(String message) {
        super(message);
    }

    /**
     * Displays the character buffer and marks on the next line the current
     * position in the buffer.
     *
     * <blockquote><pre>
     * invalid bracket atom:
     * C[CCCC
     *    ^
     * </pre></blockquote>
     *
     * @param buffer a character buffer
     * @return a 3 line string showing the buffer and it's current position
     */
    static String display(final CharBuffer buffer, int offset) {
        StringBuilder sb = new StringBuilder();
        sb.append('\n');
        sb.append(buffer);
        sb.append('\n');
        for (int i = 1; i < (buffer.position() + offset); i++)
            sb.append(' ');
        sb.append('^');
        return sb.toString();
    }

    static String display(final CharBuffer buffer, int offset, int offset2) {
        StringBuilder sb = new StringBuilder();
        sb.append('\n');
        sb.append(buffer);
        sb.append('\n');
        for (int i = 1; i < buffer.length(); i++) {
            if (i == buffer.position+offset || i == buffer.position+offset2)
                sb.append('^');
            else
                sb.append(' ');
        }
        return sb.toString();
    }

    /**
     * Utility for invalid bracket atom error.
     *
     * @param buffer the current buffer
     * @return the invalid smiles exception with buffer information
     */
    static InvalidSmilesException invalidBracketAtom(CharBuffer buffer) {
        return new InvalidSmilesException("Invalid bracket atom, [ <isotope>? <symbol> <chiral>? <hcount>? <charge>? <class>? ], SMILES may be truncated:",
                                          buffer);
    }

}
