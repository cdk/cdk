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

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static uk.ac.ebi.beam.Bond.AROMATIC;
import static uk.ac.ebi.beam.Bond.DOUBLE;
import static uk.ac.ebi.beam.Bond.IMPLICIT;
import static uk.ac.ebi.beam.Bond.SINGLE;

/** @author John May */
public class ImplicitToExplicitTest {

    @Test public void cycloHexane() throws Exception {
        Graph g = new Graph(6);
        g.addAtom(AtomImpl.AliphaticSubset.Carbon);
        g.addAtom(AtomImpl.AliphaticSubset.Carbon);
        g.addAtom(AtomImpl.AliphaticSubset.Carbon);
        g.addAtom(AtomImpl.AliphaticSubset.Carbon);
        g.addAtom(AtomImpl.AliphaticSubset.Carbon);
        g.addAtom(AtomImpl.AliphaticSubset.Carbon);
        g.addEdge(new Edge(0, 1, IMPLICIT));
        g.addEdge(new Edge(1, 2, IMPLICIT));
        g.addEdge(new Edge(2, 3, IMPLICIT));
        g.addEdge(new Edge(3, 4, IMPLICIT));
        g.addEdge(new Edge(4, 5, IMPLICIT));
        g.addEdge(new Edge(5, 0, IMPLICIT));

        Graph h = new ImplicitToExplicit().apply(g);

        Assert.assertThat(g, CoreMatchers.is(CoreMatchers.not(CoreMatchers
                                                                      .sameInstance(h))));

        for (int u = 0; u < h.order(); u++) {
            for (Edge e : h.edges(u)) {
                Assert.assertThat(e.bond(), CoreMatchers.is(SINGLE));
            }
        }
    }

    @Test public void aromaticBenzene() throws Exception {
        Graph g = new Graph(6);
        g.addAtom(AtomImpl.AromaticSubset.Carbon);
        g.addAtom(AtomImpl.AromaticSubset.Carbon);
        g.addAtom(AtomImpl.AromaticSubset.Carbon);
        g.addAtom(AtomImpl.AromaticSubset.Carbon);
        g.addAtom(AtomImpl.AromaticSubset.Carbon);
        g.addAtom(AtomImpl.AromaticSubset.Carbon);
        g.addEdge(new Edge(0, 1, IMPLICIT));
        g.addEdge(new Edge(1, 2, IMPLICIT));
        g.addEdge(new Edge(2, 3, IMPLICIT));
        g.addEdge(new Edge(3, 4, IMPLICIT));
        g.addEdge(new Edge(4, 5, IMPLICIT));
        g.addEdge(new Edge(5, 0, IMPLICIT));

        Graph h = new ImplicitToExplicit().apply(g);

        Assert.assertThat(g, CoreMatchers.is(CoreMatchers.not(CoreMatchers
                                                                      .sameInstance(h))));

        for (int u = 0; u < h.order(); u++) {
            for (Edge e : h.edges(u)) {
                Assert.assertThat(e.bond(), CoreMatchers.is(AROMATIC));
            }
        }
    }

    @Test public void kekuleBenzene() throws Exception {
        Graph g = new Graph(6);
        g.addAtom(AtomImpl.AliphaticSubset.Carbon);
        g.addAtom(AtomImpl.AliphaticSubset.Carbon);
        g.addAtom(AtomImpl.AliphaticSubset.Carbon);
        g.addAtom(AtomImpl.AliphaticSubset.Carbon);
        g.addAtom(AtomImpl.AliphaticSubset.Carbon);
        g.addAtom(AtomImpl.AliphaticSubset.Carbon);
        g.addEdge(new Edge(0, 1, IMPLICIT));
        g.addEdge(new Edge(1, 2, DOUBLE));
        g.addEdge(new Edge(2, 3, IMPLICIT));
        g.addEdge(new Edge(3, 4, DOUBLE));
        g.addEdge(new Edge(4, 5, IMPLICIT));
        g.addEdge(new Edge(5, 0, DOUBLE));

        Graph h = new ImplicitToExplicit().apply(g);

        Assert.assertThat(g, CoreMatchers.is(CoreMatchers.not(CoreMatchers
                                                                      .sameInstance(h))));


        Assert.assertThat(h.edges(0), CoreMatchers
                .hasItems(new Edge(0, 1, SINGLE),
                          new Edge(0, 5, DOUBLE)));
        Assert.assertThat(h.edges(1), CoreMatchers
                .hasItems(new Edge(1, 0, SINGLE),
                          new Edge(1, 2, DOUBLE)));
        Assert.assertThat(h.edges(2), CoreMatchers
                .hasItems(new Edge(2, 1, DOUBLE),
                          new Edge(2, 3, SINGLE)));
        Assert.assertThat(h.edges(3), CoreMatchers
                .hasItems(new Edge(3, 2, SINGLE),
                          new Edge(3, 4, DOUBLE)));
        Assert.assertThat(h.edges(4), CoreMatchers
                .hasItems(new Edge(4, 3, DOUBLE),
                          new Edge(4, 5, SINGLE)));
        Assert.assertThat(h.edges(5), CoreMatchers
                .hasItems(new Edge(5, 0, DOUBLE),
                          new Edge(5, 4, SINGLE)));
    }

    @Test public void aromaticType() {
        Atom a = Mockito.mock(Atom.class);
        Atom b = Mockito.mock(Atom.class);
        Mockito.when(a.aromatic()).thenReturn(true);
        Mockito.when(b.aromatic()).thenReturn(true);
        Assert.assertThat(ImplicitToExplicit.type(a, b), CoreMatchers
                .is(Bond.AROMATIC));
    }

    @Test public void singleType() {
        Atom a = Mockito.mock(Atom.class);
        Atom b = Mockito.mock(Atom.class);

        Mockito.when(a.aromatic()).thenReturn(true);
        Mockito.when(b.aromatic()).thenReturn(false);
        Assert.assertThat(ImplicitToExplicit.type(a, b), CoreMatchers
                .is(Bond.SINGLE));

        Mockito.when(a.aromatic()).thenReturn(false);
        Mockito.when(b.aromatic()).thenReturn(true);
        Assert.assertThat(ImplicitToExplicit.type(a, b), CoreMatchers
                .is(Bond.SINGLE));

        Mockito.when(a.aromatic()).thenReturn(false);
        Mockito.when(b.aromatic()).thenReturn(false);
        Assert.assertThat(ImplicitToExplicit.type(a, b), CoreMatchers
                .is(Bond.SINGLE));
    }

    @Test public void toExplicitEdge_NonImplicitIdentity() {
        Graph g = new Graph(0);
        for (Bond b : Bond.values()) {
            if (b != IMPLICIT) {
                Edge e = new Edge(0, 1, SINGLE);
                Assert.assertThat(ImplicitToExplicit
                                          .toExplicitEdge(g, e), CoreMatchers
                                          .is(CoreMatchers.sameInstance(e)));
            }
        }
    }

    @Test public void toExplicitEdge() {
        Graph g = new Graph(2);

        Atom u = Mockito.mock(Atom.class);
        Atom v = Mockito.mock(Atom.class);

        Mockito.when(u.aromatic()).thenReturn(false);
        Mockito.when(v.aromatic()).thenReturn(false);

        g.addAtom(u);
        g.addAtom(v);

        Edge e = new Edge(0, 1, IMPLICIT);
        Assert.assertThat(ImplicitToExplicit.toExplicitEdge(g, e),
                          CoreMatchers.is(CoreMatchers.not(CoreMatchers
                                                                   .sameInstance(e))));
        Assert.assertThat(ImplicitToExplicit.toExplicitEdge(g, e),
                          CoreMatchers.is(new Edge(0, 1, SINGLE)));


    }
}
