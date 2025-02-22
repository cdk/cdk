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

/** @author John May */
public class ConfigurationTest {

    @Test public void tetrahedralShorthand() throws Exception {
        assertThat(Configuration.TH1
                                .shorthand(), is(Configuration.ANTI_CLOCKWISE));
        assertThat(Configuration.TH2.shorthand(), is(Configuration.CLOCKWISE));
    }

    @Test public void tetrahedralType() throws Exception {
        assertThat(Configuration.TH1
                                .type(), is(Configuration.Type.Tetrahedral));
        assertThat(Configuration.TH2
                                .type(), is(Configuration.Type.Tetrahedral));
    }

    @Test public void read() throws Exception {
        for (Configuration config : Configuration.values()) {
            assertThat(Configuration.read(CharBuffer
                                                  .fromString(config.symbol())),
                       is(config));
        }
    }

    @Test public void readNone() throws Exception {
        assertThat(Configuration.read(CharBuffer
                                              .fromString("]")),
                   is(Configuration.UNKNOWN));
    }

    @Test public void readNone1() throws Exception {
        assertThat(Configuration.read(CharBuffer
                                              .fromString("")),
                   is(Configuration.UNKNOWN));
    }

    @Test(expected = InvalidSmilesException.class)
    public void noTHNumber() throws InvalidSmilesException {
        Configuration.read(CharBuffer.fromString("@TH"));
    }

    @Test(expected = InvalidSmilesException.class)
    public void invalidTHNumber() throws InvalidSmilesException {
        Configuration.read(CharBuffer.fromString("@TH5"));
    }

    @Test(expected = InvalidSmilesException.class)
    public void noSPNumber() throws InvalidSmilesException {
        Configuration.read(CharBuffer.fromString("@SP"));
    }

    @Test(expected = InvalidSmilesException.class)
    public void invalidSPNumber() throws InvalidSmilesException {
        Configuration.read(CharBuffer.fromString("@SP4"));
    }

    @Test(expected = InvalidSmilesException.class)
    public void noALNumber() throws InvalidSmilesException {
        Configuration.read(CharBuffer.fromString("@AL"));
    }

    @Test(expected = InvalidSmilesException.class)
    public void invalidALNumber() throws InvalidSmilesException {
        Configuration.read(CharBuffer.fromString("@AL3"));
    }

    @Test(expected = InvalidSmilesException.class)
    public void noTBNumber() throws InvalidSmilesException {
        Configuration.read(CharBuffer.fromString("@TB"));
    }

    @Test(expected = InvalidSmilesException.class)
    public void invalidLoTBNumber() throws InvalidSmilesException {
        Configuration.read(CharBuffer.fromString("@TB0"));
    }

    @Test(expected = InvalidSmilesException.class)
    public void invalidHiTBNumber() throws InvalidSmilesException {
        Configuration.read(CharBuffer.fromString("@TB21"));
    }

    @Test(expected = InvalidSmilesException.class)
    public void noOHNumber() throws InvalidSmilesException {
        Configuration.read(CharBuffer.fromString("@OH"));
    }

    @Test(expected = InvalidSmilesException.class)
    public void invalidLoOHNumber() throws InvalidSmilesException {
        Configuration.read(CharBuffer.fromString("@OH0"));
    }

    @Test(expected = InvalidSmilesException.class)
    public void invalidHiOHNumber() throws InvalidSmilesException {
        Configuration.read(CharBuffer.fromString("@OH31"));
    }

    @Test public void antiClockwise() throws InvalidSmilesException {
        assertThat(Configuration.read(CharBuffer.fromString("@H")),
                   is(Configuration.ANTI_CLOCKWISE));
    }

    @Test(expected = InvalidSmilesException.class)
    public void incompleteTHorTB() throws InvalidSmilesException {
        Configuration.read(CharBuffer.fromString("@T"));
    }

    @Test(expected = InvalidSmilesException.class)
    public void incompleteSP() throws InvalidSmilesException {
        Configuration.read(CharBuffer.fromString("@S"));
    }

    @Test(expected = InvalidSmilesException.class)
    public void incompleteOH() throws InvalidSmilesException {
        Configuration.read(CharBuffer.fromString("@O"));
    }

    @Test(expected = InvalidSmilesException.class)
    public void incompleteAL() throws InvalidSmilesException {
        Configuration.read(CharBuffer.fromString("@A"));
    }

}
