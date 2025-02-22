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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

/** @author John May */
public class EdgeTest {

    @Test public void either() throws Exception {
        assertThat(new Edge(2, 3, Bond.IMPLICIT).either(), is(2));
        assertThat(new Edge(3, 2, Bond.IMPLICIT).either(), is(3));
    }

    @Test public void other() throws Exception {
        assertThat(new Edge(2, 3, Bond.IMPLICIT).other(2), is(3));
        assertThat(new Edge(2, 3, Bond.IMPLICIT).other(3), is(2));
        assertThat(new Edge(3, 2, Bond.IMPLICIT).other(2), is(3));
        assertThat(new Edge(3, 2, Bond.IMPLICIT).other(3), is(2));
    }

    @Ignore("no longer thrown")
    public void invalidEndpoint() throws Exception {
        new Edge(2, 3, Bond.IMPLICIT).other(1);
    }

    @Test public void bond() throws Exception {
        assertThat(new Edge(2, 3, Bond.SINGLE).bond(), is(Bond.SINGLE));
        assertThat(new Edge(2, 3, Bond.UP).bond(), is(Bond.UP));
        assertThat(new Edge(2, 3, Bond.DOWN).bond(), is(Bond.DOWN));
    }

    @Test public void relativeBond() throws Exception {
        assertThat(new Edge(2, 3, Bond.SINGLE).bond(2), is(Bond.SINGLE));
        assertThat(new Edge(2, 3, Bond.SINGLE).bond(3), is(Bond.SINGLE));
        assertThat(new Edge(2, 3, Bond.UP).bond(2), is(Bond.UP));
        assertThat(new Edge(2, 3, Bond.UP).bond(3), is(Bond.DOWN));
        assertThat(new Edge(2, 3, Bond.DOWN).bond(2), is(Bond.DOWN));
        assertThat(new Edge(2, 3, Bond.DOWN).bond(3), is(Bond.UP));
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidRelativeBond() throws Exception {
        new Edge(2, 3, Bond.IMPLICIT).bond(1);
    }

    @Test public void undirectedHashCode() {
        assertThat(new Edge(0, 1, Bond.IMPLICIT).hashCode(),
                   is(new Edge(1, 0, Bond.IMPLICIT).hashCode()));
    }

    @Test public void directedHashCode() {
        assertThat(new Edge(0, 1, Bond.UP).hashCode(),
                   is(new Edge(1, 0, Bond.DOWN).hashCode()));
        assertThat(new Edge(0, 1, Bond.UP).hashCode(),
                   is(new Edge(1, 0, Bond.UP).hashCode()));
    }

    @Test public void undirectedEquality() {
        assertThat(new Edge(0, 1, Bond.IMPLICIT),
                   is(new Edge(0, 1, Bond.IMPLICIT)));
        assertThat(new Edge(0, 1, Bond.IMPLICIT),
                   is(new Edge(1, 0, Bond.IMPLICIT)));
    }

    @Test public void undirectedInequality() {
        assertThat(new Edge(0, 1, Bond.SINGLE),
                   is(not(new Edge(0, 1, Bond.DOUBLE))));
        assertThat(new Edge(0, 1, Bond.DOUBLE),
                   is(not(new Edge(1, 0, Bond.SINGLE))));
    }

    @Test public void directedEquality() {
        assertThat(new Edge(0, 1, Bond.UP),
                   is(new Edge(0, 1, Bond.UP)));
        assertThat(new Edge(0, 1, Bond.UP),
                   is(new Edge(1, 0, Bond.DOWN)));
        assertThat(new Edge(1, 0, Bond.DOWN),
                   is(new Edge(0, 1, Bond.UP)));
        assertThat(new Edge(1, 0, Bond.DOWN),
                   is(new Edge(1, 0, Bond.DOWN)));
    }

    @Test public void directedInequality() {
        assertThat(new Edge(0, 1, Bond.UP),
                   is(not(new Edge(0, 1, Bond.DOWN))));
        assertThat(new Edge(0, 1, Bond.UP),
                   is(not(new Edge(1, 0, Bond.UP))));
        assertThat(new Edge(1, 0, Bond.UP),
                   is(not(new Edge(0, 1, Bond.UP))));
        assertThat(new Edge(1, 0, Bond.DOWN),
                   is(not(new Edge(1, 0, Bond.UP))));
    }
}
