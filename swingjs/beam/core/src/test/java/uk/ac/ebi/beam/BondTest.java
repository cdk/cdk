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
import static uk.ac.ebi.beam.Bond.AROMATIC;
import static uk.ac.ebi.beam.Bond.DOT;
import static uk.ac.ebi.beam.Bond.DOUBLE;
import static uk.ac.ebi.beam.Bond.DOWN;
import static uk.ac.ebi.beam.Bond.IMPLICIT;
import static uk.ac.ebi.beam.Bond.QUADRUPLE;
import static uk.ac.ebi.beam.Bond.SINGLE;
import static uk.ac.ebi.beam.Bond.TRIPLE;
import static uk.ac.ebi.beam.Bond.UP;

/** @author John May */
public class BondTest {

    @Test public void dotElectrons() throws Exception {
        assertThat(DOT.order(), is(0));
    }

    @Test public void singleElectrons() throws Exception {
        assertThat(SINGLE.order(), is(1));
    }

    @Test public void doubleElectrons() throws Exception {
        assertThat(DOUBLE.order(), is(2));
    }

    @Test public void tripleElectrons() throws Exception {
        assertThat(TRIPLE.order(), is(3));
    }

    @Test public void quadrupleElectrons() throws Exception {
        assertThat(QUADRUPLE.order(), is(4));
    }

    @Test public void aromaticElectrons() throws Exception {
        assertThat(AROMATIC.order(), is(1));
    }

    @Test public void upElectrons() throws Exception {
        assertThat(UP.order(), is(1));
    }

    @Test public void downElectrons() throws Exception {
        assertThat(DOWN.order(), is(1));
    }

    @Test public void dotInverse() throws Exception {
        assertThat(DOT.inverse(), is(DOT));
    }

    @Test public void singleInverse() throws Exception {
        assertThat(SINGLE.inverse(), is(SINGLE));
    }

    @Test public void doubleInverse() throws Exception {
        assertThat(DOUBLE.inverse(), is(DOUBLE));
    }

    @Test public void tripleInverse() throws Exception {
        assertThat(TRIPLE.inverse(), is(TRIPLE));
    }

    @Test public void quadrupleInverse() throws Exception {
        assertThat(QUADRUPLE.inverse(), is(QUADRUPLE));
    }

    @Test public void aromaticInverse() throws Exception {
        assertThat(AROMATIC.inverse(), is(AROMATIC));
    }

    @Test public void upInverse() throws Exception {
        assertThat(UP.inverse(), is(DOWN));
    }

    @Test public void downInverse() throws Exception {
        assertThat(DOWN.inverse(), is(UP));
    }

    @Test public void implicitInverse() throws Exception {
        assertThat(IMPLICIT.inverse(), is(IMPLICIT));
    }

    @Test public void dotSymbol() throws Exception {
        assertThat(DOT.token(), is("."));
    }

    @Test public void singleSymbol() throws Exception {
        assertThat(SINGLE.token(), is("-"));
    }

    @Test public void doubleSymbol() throws Exception {
        assertThat(DOUBLE.token(), is("="));
    }

    @Test public void tripleSymbol() throws Exception {
        assertThat(TRIPLE.token(), is("#"));
    }

    @Test public void quadrupleSymbol() throws Exception {
        assertThat(QUADRUPLE.token(), is("$"));
    }

    @Test public void aromaticSymbol() throws Exception {
        assertThat(AROMATIC.token(), is(":"));
    }

    @Test public void upSymbol() throws Exception {
        assertThat(UP.token(), is("/"));
    }

    @Test public void downSymbol() throws Exception {
        assertThat(DOWN.token(), is("\\"));
    }

    @Test public void implicitSymbol() throws Exception {
        assertThat(IMPLICIT.token(), is(""));
    }
}
