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

import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.Locale;

import static org.junit.Assert.assertNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static uk.ac.ebi.beam.Element.Arsenic;
import static uk.ac.ebi.beam.Element.Boron;
import static uk.ac.ebi.beam.Element.Bromine;
import static uk.ac.ebi.beam.Element.Calcium;
import static uk.ac.ebi.beam.Element.Carbon;
import static uk.ac.ebi.beam.Element.Chlorine;
import static uk.ac.ebi.beam.Element.Fluorine;
import static uk.ac.ebi.beam.Element.Iodine;
import static uk.ac.ebi.beam.Element.Nitrogen;
import static uk.ac.ebi.beam.Element.Oxygen;
import static uk.ac.ebi.beam.Element.Phosphorus;
import static uk.ac.ebi.beam.Element.Selenium;
import static uk.ac.ebi.beam.Element.Sulfur;
import static uk.ac.ebi.beam.Element.Unknown;

/** @author John May */
public class ElementTest {

    @Test public void organicSymbols() throws Exception {
        assertThat(Element.ofSymbol("B"), is(Boron));
        assertThat(Element.ofSymbol("C"), is(Carbon));
        assertThat(Element.ofSymbol("N"), is(Nitrogen));
        assertThat(Element.ofSymbol("O"), is(Oxygen));
        assertThat(Element.ofSymbol("P"), is(Phosphorus));
        assertThat(Element.ofSymbol("S"), is(Sulfur));
        assertThat(Element.ofSymbol("F"), is(Fluorine));
        assertThat(Element.ofSymbol("Br"), is(Bromine));
        assertThat(Element.ofSymbol("Cl"), is(Chlorine));
        assertThat(Element.ofSymbol("I"), is(Iodine));
    }

    @Test public void aromaticSymbols() throws Exception {
        assertThat(Element.ofSymbol("b"), is(Boron));
        assertThat(Element.ofSymbol("c"), is(Carbon));
        assertThat(Element.ofSymbol("n"), is(Nitrogen));
        assertThat(Element.ofSymbol("o"), is(Oxygen));
        assertThat(Element.ofSymbol("p"), is(Phosphorus));
        assertThat(Element.ofSymbol("s"), is(Sulfur));
        assertThat(Element.ofSymbol("se"), is(Selenium));
        assertThat(Element.ofSymbol("as"), is(Arsenic));
    }

    @Test public void symbols() {
        for (Element e : Element.values()) {
            assertThat(Element.ofSymbol(e.symbol()), is(e));
        }
    }

    @Test public void invalidSymbol() {
        assertNull(Element.ofSymbol("J"));
    }

    @Test public void organic() {
        for (Element e : Arrays.asList(Boron,
                                       Carbon,
                                       Nitrogen,
                                       Oxygen,
                                       Phosphorus,
                                       Sulfur,
                                       Fluorine,
                                       Chlorine,
                                       Bromine,
                                       Iodine)) {
            assertTrue(e.organic());
        }
    }

    @Test public void aromatic() {
        for (Element e : Arrays.asList(Boron,
                                       Carbon,
                                       Nitrogen,
                                       Oxygen,
                                       Phosphorus,
                                       Sulfur,
                                       Selenium,
                                       Arsenic)) {
            assertTrue(e.aromatic());
        }
    }
    
    @Test public void verify() {
        for (Element e : Element.values()) {
            boolean valid = e.verify(0, 0);
        }
    }
    
    @Test public void ofNumber() {
        assertThat(Element.ofNumber(6), is(Element.Carbon));
        assertThat(Element.ofNumber(8), is(Element.Oxygen));
    }

    @Test
    public void read() {
        for (Element e : Element.values()) {
            if (e.aromatic())
                assertThat(Element.read(CharBuffer.fromString(e.symbol()
                                                               .toLowerCase(Locale.ENGLISH))), is(e));
            assertThat(Element.read(CharBuffer.fromString(e.symbol())), is(e));
        }
    }

    @Test
    public void readNone() {
        assertNull(Element.read(CharBuffer.fromString("")));
    }

    @Test
    public void readInvalidElement() {
        assertNull(Element.read(CharBuffer.fromString("J")));
    }
}
