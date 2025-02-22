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
 * Unit tests for bracket atoms. Examples are lifted from the specification.
 *
 * @author John May
 */
public class ParsingBracketAtomTest {

    @Test public void uranium() throws InvalidSmilesException {
        assertThat(parse("[U]"), is(atom(Element.Uranium)));
    }

    @Test public void lead() throws InvalidSmilesException {
        assertThat(parse("[Pb]"), is(atom(Element.Lead)));
    }

    @Test public void helium() throws InvalidSmilesException {
        assertThat(parse("[He]"), is(atom(Element.Helium)));
    }

    @Test public void unknown() throws InvalidSmilesException {
        assertThat(parse("[*]"), is(atom(Element.Unknown)));
    }

    @Test public void identical() throws InvalidSmilesException {
        assertThat(parse("[C]"), is(parse("[CH0]")));
    }

    @Test public void identical2() throws InvalidSmilesException {
        assertThat(parse("[CH]"), is(parse("[CH1]")));
    }

    @Test public void methane() throws InvalidSmilesException {
        assertThat(parse("[CH4]"), is(atom(Element.Carbon, 4)));
    }

    @Test public void hydrochloricAcid() throws InvalidSmilesException {
        assertThat(parse("[ClH]"), is(atom(Element.Chlorine, 1)));
    }

    @Test public void hydrochloricAcid1() throws InvalidSmilesException {
        assertThat(parse("[ClH1]"), is(atom(Element.Chlorine, 1)));
    }

    @Test public void chlorineAnion() throws InvalidSmilesException {
        assertThat(parse("[Cl-]"), is(atom(Element.Chlorine, 0, -1)));
    }

    @Test public void hydroxylAnion() throws InvalidSmilesException {
        assertThat(parse("[OH1-]"), is(atom(Element.Oxygen, 1, -1)));
    }

    @Test public void hydroxylAnionAlt() throws InvalidSmilesException {
        assertThat(parse("[OH-1]"), is(atom(Element.Oxygen, 1, -1)));
    }

    @Test public void copperCation() throws InvalidSmilesException {
        assertThat(parse("[Cu+2]"), is(atom(Element.Copper, 0, +2)));
    }

    @Test public void copperCationAlt() throws InvalidSmilesException {
        assertThat(parse("[Cu++]"), is(atom(Element.Copper, 0, +2)));
    }

    @Test public void methaneIsotope() throws InvalidSmilesException {
        assertThat(parse("[13CH4]"), is(atom(13, Element.Carbon, 4, 0)));
    }

    @Test public void deuteriumIon() throws InvalidSmilesException {
        assertThat(parse("[2H+]"), is(atom(2, Element.Hydrogen, 0, +1)));
    }

    @Test public void uranium238Atom() throws InvalidSmilesException {
        assertThat(parse("[238U]"), is(atom(238, Element.Uranium, 0, 0)));
    }

    // An isotope is interpreted as a number, so that [2H], [02H] and [002H] all mean deuterium.
    @Test public void isotopePadding() throws InvalidSmilesException {
        assertThat(parse("[2H]"), is(parse("[02H]")));
        assertThat(parse("[2H]"), is(parse("[002H]")));
        assertThat(parse("[2H]"), is(parse("[0002H]")));
    }

    @Test public void chlorine36() throws InvalidSmilesException {
        assertThat(parse("[36Cl]"), is(atom(36, Element.Chlorine, 0, 0)));
    }

    // A general-purpose SMILES parser must accept at least three digits for the isotope and values from 0 to 999.
    @Test public void rangeCheck() throws InvalidSmilesException {
        for (int i = 0; i < 999; i++) {
            assertThat(parse("[" + Integer
                    .toString(i) + "C]"), is(atom(i, Element.Carbon, 0, 0)));
        }
    }

    @Test public void methaneAtomClassIs2() throws InvalidSmilesException {
        assertThat(parse("[CH4:2]").atomClass(), is(2));
    }

    private Atom parse(String str) throws InvalidSmilesException {
        CharBuffer buffer = CharBuffer.fromString(str);
        return new Parser(buffer, false).molecule().atom(0);
    }

    private Atom atom(Element e) {
        return new AtomImpl.BracketAtom(e, 0, 0);
    }

    private Atom atom(Element e, int hCount) {
        return new AtomImpl.BracketAtom(e, hCount, 0);
    }

    private Atom atom(Element e, int hCount, int charge) {
        return new AtomImpl.BracketAtom(e, hCount, charge);
    }

    private Atom atom(int isotope, Element e, int hCount, int charge) {
        return new AtomImpl.BracketAtom(isotope, e, hCount, charge, 0, false);
    }
}
