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

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** @author John May */
public class GraphTest {

    @Test public void addAtoms() {
        Graph g = new Graph(5);
        assertThat(g.addAtom(mock(Atom.class)), is(0));
        assertThat(g.addAtom(mock(Atom.class)), is(1));
        assertThat(g.addAtom(mock(Atom.class)), is(2));
        assertThat(g.addAtom(mock(Atom.class)), is(3));
        assertThat(g.addAtom(mock(Atom.class)), is(4));
    }

    @Test public void addAtomsResize() {
        Graph g = new Graph(2);
        assertThat(g.addAtom(mock(Atom.class)), is(0));
        assertThat(g.addAtom(mock(Atom.class)), is(1));
        assertThat(g.addAtom(mock(Atom.class)), is(2));
        assertThat(g.addAtom(mock(Atom.class)), is(3));
        assertThat(g.addAtom(mock(Atom.class)), is(4));
    }

    @Test public void atomAccess() {
        Atom[] atoms = new Atom[]{
                mock(Atom.class),
                mock(Atom.class),
                mock(Atom.class),
                mock(Atom.class)
        };
        Graph g = new Graph(5);
        for (Atom a : atoms)
            g.addAtom(a);
        assertThat(g.atom(0), is(atoms[0]));
        assertThat(g.atom(1), is(atoms[1]));
        assertThat(g.atom(2), is(atoms[2]));
        assertThat(g.atom(3), is(atoms[3]));
    }

    @Test public void testOrder() {
        Graph g = new Graph(5);
        assertThat(g.order(), is(0));
        g.addAtom(mock(Atom.class));
        assertThat(g.order(), is(1));
        g.addAtom(mock(Atom.class));
        assertThat(g.order(), is(2));
        g.addAtom(mock(Atom.class));
        assertThat(g.order(), is(3));
        g.addAtom(mock(Atom.class));
        assertThat(g.order(), is(4));
        g.addAtom(mock(Atom.class));
        assertThat(g.order(), is(5));
    }

    @Test public void testSize() {
        Graph g = new Graph(5);
        g.addAtom(mock(Atom.class));
        g.addAtom(mock(Atom.class));
        g.addAtom(mock(Atom.class));

        Edge e1 = new Edge(0, 1, Bond.IMPLICIT);
        Edge e2 = new Edge(0, 1, Bond.IMPLICIT);

        assertThat(g.size(), is(0));
        g.addEdge(e1);
        assertThat(g.size(), is(1));
        g.addEdge(e2);
        assertThat(g.size(), is(2));
    }

    @Test public void testEdges() {
        Graph g = new Graph(5);
        g.addAtom(mock(Atom.class));
        g.addAtom(mock(Atom.class));
        g.addAtom(mock(Atom.class));
        g.addEdge(new Edge(0, 1, Bond.IMPLICIT));
        g.addEdge(new Edge(1, 2, Bond.IMPLICIT));
        assertThat(g.edges(0).size(), is(1));
        assertThat(g.edges(0), hasItem(new Edge(0, 1, Bond.IMPLICIT)));
        assertThat(g.edges(1).size(), is(2));
        assertThat(g.edges(1), hasItems(new Edge(0, 1, Bond.IMPLICIT),
                                        new Edge(1, 0, Bond.IMPLICIT)));
    }

    @Test public void testEdgesResize() {
        Graph g = new Graph(2);
        g.addAtom(mock(Atom.class));
        g.addAtom(mock(Atom.class));
        g.addAtom(mock(Atom.class));
        g.addEdge(new Edge(0, 1, Bond.IMPLICIT));
        g.addEdge(new Edge(1, 2, Bond.IMPLICIT));
        assertThat(g.edges(0).size(), is(1));
        assertThat(g.edges(0), hasItem(new Edge(0, 1, Bond.IMPLICIT)));
        assertThat(g.edges(1).size(), is(2));
        assertThat(g.edges(1), hasItems(new Edge(0, 1, Bond.IMPLICIT),
                                        new Edge(1, 0, Bond.IMPLICIT)));
    }

    @Test public void testEdgesIterable() {
        Graph g = new Graph(2);
        g.addAtom(mock(Atom.class));
        g.addAtom(mock(Atom.class));
        g.addAtom(mock(Atom.class));
        g.addEdge(new Edge(0, 1, Bond.IMPLICIT));
        g.addEdge(new Edge(1, 2, Bond.IMPLICIT));

        Iterable<Edge> es = g.edges();
        Iterator<Edge> it = es.iterator();
        assertTrue(it.hasNext());
        assertThat(it.next(), is(new Edge(0, 1, Bond.IMPLICIT)));
        assertTrue(it.hasNext());
        assertThat(it.next(), is(new Edge(1, 2, Bond.IMPLICIT)));
        assertFalse(it.hasNext());
    }


    @Test public void testDegree() {
        Graph g = new Graph(5);
        g.addAtom(mock(Atom.class));
        g.addAtom(mock(Atom.class));
        g.addAtom(mock(Atom.class));
        g.addEdge(new Edge(0, 1, Bond.IMPLICIT));
        g.addEdge(new Edge(1, 2, Bond.IMPLICIT));
        assertThat(g.degree(0), is(1));
        assertThat(g.degree(1), is(2));
    }

    @Test public void adjacent() {
        Graph g = new Graph(5);
        g.addAtom(mock(Atom.class));
        g.addAtom(mock(Atom.class));
        g.addAtom(mock(Atom.class));
        g.addEdge(new Edge(0, 1, Bond.IMPLICIT));
        g.addEdge(new Edge(1, 2, Bond.IMPLICIT));
        assertTrue(g.adjacent(0, 1));
        assertTrue(g.adjacent(1, 2));
        assertFalse(g.adjacent(0, 2));
    }

    @Test
    public void edge() {
        Graph g = new Graph(5);
        g.addAtom(mock(Atom.class));
        g.addAtom(mock(Atom.class));
        g.addAtom(mock(Atom.class));
        g.addEdge(new Edge(0, 1, Bond.IMPLICIT));
        g.addEdge(new Edge(1, 2, Bond.IMPLICIT));
        assertThat(g.edge(0, 1), is(new Edge(0, 1, Bond.IMPLICIT)));
        assertThat(g.edge(1, 2), is(new Edge(1, 2, Bond.IMPLICIT)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void edgeNone() {
        Graph g = new Graph(5);
        g.addAtom(mock(Atom.class));
        g.addAtom(mock(Atom.class));
        g.addAtom(mock(Atom.class));
        g.addEdge(new Edge(0, 1, Bond.IMPLICIT));
        g.addEdge(new Edge(1, 2, Bond.IMPLICIT));
        g.edge(0, 2);
    }
    
    @Test public void addTopology() {
        Topology t = mock(Topology.class);
        when(t.atom()).thenReturn(5);
        Graph g = new Graph(6);
        g.addTopology(t);
        assertThat(g.topologyOf(5), is(t));
    }

    @Test public void addUnknownTopology() {
        Topology t = Topology.unknown();
        Graph g = new Graph(5);
        g.addTopology(t); // don't fail
        assertThat(g.order(), is(0));
        assertThat(g.size(), is(0));
    }

    @Test public void defaultTopology() {
        Graph g = new Graph(5);
        assertThat(g.topologyOf(4), is(Topology.unknown()));
    }

    @Test public void clear() {
        Graph g = new Graph(2);
        g.addAtom(mock(Atom.class));
        g.addAtom(mock(Atom.class));
        g.addAtom(mock(Atom.class));
        g.addEdge(new Edge(0, 1, Bond.IMPLICIT));
        g.addEdge(new Edge(1, 2, Bond.IMPLICIT));
        assertThat(g.order(), is(3));
        assertThat(g.size(), is(2));
        assertThat(g.edges(0).size(), is(1));
        assertThat(g.edges(0), hasItem(new Edge(0, 1, Bond.IMPLICIT)));
        assertThat(g.edges(1).size(), is(2));
        assertThat(g.edges(1), hasItems(new Edge(0, 1, Bond.IMPLICIT),
                                        new Edge(1, 0, Bond.IMPLICIT)));
        g.clear();
        assertThat(g.order(), is(0));
        assertThat(g.size(), is(0));
    }

    @Test public void permute() {
        Graph g = new Graph(2);
        g.addAtom(mock(Atom.class));
        g.addAtom(mock(Atom.class));
        g.addAtom(mock(Atom.class));
        g.addAtom(mock(Atom.class));
        g.addEdge(new Edge(0, 1, Bond.IMPLICIT));
        g.addEdge(new Edge(1, 2, Bond.IMPLICIT));
        g.addEdge(new Edge(2, 3, Bond.IMPLICIT));

        assertThat(g.degree(0), is(1));
        assertThat(g.degree(1), is(2));
        assertThat(g.degree(2), is(2));
        assertThat(g.degree(3), is(1));

        Graph h = g.permute(new int[]{1, 0, 3, 2});
        assertThat(h.degree(0), is(2));
        assertThat(h.degree(1), is(1));
        assertThat(h.degree(2), is(1));
        assertThat(h.degree(3), is(2));
        assertThat(g.atom(0), is(h.atom(1)));
        assertThat(g.atom(1), is(h.atom(0)));
        assertThat(g.atom(2), is(h.atom(3)));
        assertThat(g.atom(3), is(h.atom(2)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidPermutation() {
        Graph g = new Graph(2);
        g.addAtom(mock(Atom.class));
        g.addAtom(mock(Atom.class));
        g.addAtom(mock(Atom.class));
        g.addAtom(mock(Atom.class));
        g.addEdge(new Edge(0, 1, Bond.IMPLICIT));
        g.addEdge(new Edge(1, 2, Bond.IMPLICIT));
        g.addEdge(new Edge(2, 3, Bond.IMPLICIT));
        g.permute(new int[2]);
    }

    @Test public void sort() {
        Graph g = new Graph(2);
        g.addAtom(mock(Atom.class));
        g.addAtom(mock(Atom.class));
        g.addAtom(mock(Atom.class));
        g.addAtom(mock(Atom.class));
        g.addEdge(new Edge(3, 2, Bond.IMPLICIT));
        g.addEdge(new Edge(1, 2, Bond.IMPLICIT));
        g.addEdge(new Edge(0, 3, Bond.IMPLICIT));
        g.addEdge(new Edge(0, 1, Bond.IMPLICIT));
        assertThat(g.edges(0), CoreMatchers
                .<List<Edge>>is(Arrays.asList(new Edge(0, 3, Bond.IMPLICIT),
                                              new Edge(0, 1, Bond.IMPLICIT))));
        assertThat(g.edges(1), CoreMatchers
                .<List<Edge>>is(Arrays.asList(new Edge(1, 2, Bond.IMPLICIT),
                                              new Edge(1, 0, Bond.IMPLICIT))));
        assertThat(g.edges(2), CoreMatchers
                .<List<Edge>>is(Arrays.asList(new Edge(2, 3, Bond.IMPLICIT),
                                              new Edge(2, 1, Bond.IMPLICIT))));
        assertThat(g.edges(3), CoreMatchers
                .<List<Edge>>is(Arrays.asList(new Edge(3, 2, Bond.IMPLICIT),
                                              new Edge(3, 0, Bond.IMPLICIT))));
        g.sort(new Graph.CanOrderFirst());
        assertThat(g.edges(0), CoreMatchers
                .<List<Edge>>is(Arrays.asList(new Edge(0, 1, Bond.IMPLICIT),
                                              new Edge(0, 3, Bond.IMPLICIT))));
        assertThat(g.edges(1), CoreMatchers
                .<List<Edge>>is(Arrays.asList(new Edge(1, 0, Bond.IMPLICIT),
                                              new Edge(1, 2, Bond.IMPLICIT))));
        assertThat(g.edges(2), CoreMatchers
                .<List<Edge>>is(Arrays.asList(new Edge(2, 1, Bond.IMPLICIT),
                                              new Edge(2, 3, Bond.IMPLICIT))));
        assertThat(g.edges(3), CoreMatchers
                .<List<Edge>>is(Arrays.asList(new Edge(3, 0, Bond.IMPLICIT),
                                              new Edge(3, 2, Bond.IMPLICIT))));
    }

    @Test public void atoms() {
        Graph g = new Graph(20);
        g.addAtom(mock(Atom.class));
        g.addAtom(mock(Atom.class));
        g.addAtom(mock(Atom.class));
        g.addAtom(mock(Atom.class));
        Iterable<Atom> atoms = g.atoms();
        Iterator<Atom> it    = atoms.iterator();
        assertTrue(it.hasNext());
        assertNotNull(it.next());
        assertTrue(it.hasNext());
        assertNotNull(it.next());
        assertTrue(it.hasNext());
        assertNotNull(it.next());
        assertTrue(it.hasNext());
        assertNotNull(it.next());
        assertFalse(it.hasNext());
    }

    @Test public void configurationOf() throws Exception {
        Graph g = Graph.fromSmiles("O[C@]12CCCC[C@@]1(O)CCCC2");

        Assert.assertThat(g.configurationOf(1), is(Configuration.TH1));
        Assert.assertThat(g.configurationOf(6), is(Configuration.TH1));
    }

    @Test public void configurationOf_myoInositol() throws Exception {
        Graph g = Graph
                .fromSmiles("O[C@@H]1[C@H](O)[C@H](O)[C@H](O)[C@H](O)[C@@H]1O");

        Assert.assertThat(g.configurationOf(1), is(Configuration.TH1));
        Assert.assertThat(g.configurationOf(2), is(Configuration.TH1));
        Assert.assertThat(g.configurationOf(4), is(Configuration.TH1));
        Assert.assertThat(g.configurationOf(6), is(Configuration.TH1));
        Assert.assertThat(g.configurationOf(8), is(Configuration.TH1));
        Assert.assertThat(g.configurationOf(10), is(Configuration.TH2));
    }

    @Test public void myoInositol_neighbors() throws Exception {
        Graph g = Graph
                .fromSmiles("O[C@@H]1[C@H](O)[C@H](O)[C@H](O)[C@H](O)[C@@H]1O");

        Assert.assertThat(g.neighbors(1), is(new int[]{0, 2, 10}));
        Assert.assertThat(g.neighbors(2), is(new int[]{1, 3, 4}));
        Assert.assertThat(g.neighbors(4), is(new int[]{2, 5, 6}));
        Assert.assertThat(g.neighbors(6), is(new int[]{4, 7, 8}));
        Assert.assertThat(g.neighbors(8), is(new int[]{6, 9, 10}));
        Assert.assertThat(g.neighbors(10), is(new int[]{1, 8, 11}));
    }
    
    @Test public void implHCount() throws Exception{
        Graph g = Graph.fromSmiles("C1NC=C[C]=C1");
        assertThat(g.implHCount(0), is(2));
        assertThat(g.implHCount(1), is(1));
        assertThat(g.implHCount(2), is(1));
        assertThat(g.implHCount(3), is(1));
        assertThat(g.implHCount(4), is(0));
        assertThat(g.implHCount(5), is(1));
    }
    
    @Test public void implHCount_nonExpH() throws Exception{
        Graph g = Graph.fromSmiles("C([H])([H])1NC=C[C]=C1");
        assertThat(g.implHCount(0), is(0)); // 2 exp hs
        assertThat(g.implHCount(1), is(0)); // [H]
        assertThat(g.implHCount(2), is(0)); // [H]
        assertThat(g.implHCount(3), is(1)); 
        assertThat(g.implHCount(4), is(1)); 
        assertThat(g.implHCount(5), is(1));
        assertThat(g.implHCount(6), is(0));
        assertThat(g.implHCount(7), is(1));
    }
    
    @Test public void outputOrder() throws Exception {
        Graph g = GraphBuilder.create(5)
                              .add(Element.Carbon, 3)
                              .add(Element.Carbon, 1)
                              .add(Element.Carbon, 2)
                              .add(Element.Carbon, 3)
                              .add(Element.Carbon, 2)
                              .add(Element.Carbon, 3)
                              .add(0, 1)
                              .add(1, 2)
                              .add(1, 3)
                              .add(2, 4)
                              .add(4, 5)
                              .build();
        g.sort(new Graph.CanOrderFirst());
        int[] visited = new int[g.order()];
        assertThat(g.toSmiles(visited), is("CC(CCC)C"));
        assertThat(visited, is(new int[]{0, 1, 2, 5, 3, 4}));
    }
    
    @Test public void resonate() throws Exception {
        // two different resonance forms with the same
        // ordering
        Graph g = Graph.fromSmiles("C1=CC2=CC=CC2=C1");
        Graph h = Graph.fromSmiles("C=1C=C2C=CC=C2C=1");
        // produce different SMILES
        assertThat(g.toSmiles(), is("C1=CC2=CC=CC2=C1"));
        assertThat(h.toSmiles(), is("C=1C=C2C=CC=C2C1"));
        // but once resonate we get the same SMILES 
        assertThat(g.resonate().toSmiles(), is(h.resonate().toSmiles()));
    }


    // ensures we don't loose the carbonyl
    @Test public void nitrogen_5v() throws Exception {
        Graph g = Graph.fromSmiles("O=N1=CC=CC=C1");
        Graph h = Graph.fromSmiles("O=N=1C=CC=CC1");
        // produce different SMILES
        assertThat(g.toSmiles(), is("O=N1=CC=CC=C1"));
        assertThat(h.toSmiles(), is("O=N=1C=CC=CC1"));
        // but once resonate we get the same SMILES 
        assertThat(g.resonate().toSmiles(), is(h.resonate().toSmiles()));
    }
    
    // ensures we don't loose the allene
    @Test public void allene() throws Exception {
        Graph g = Graph.fromSmiles("C1=CC=C=CC=C1");
        assertThat(g.toSmiles(), is("C1=CC=C=CC=C1"));
        assertThat(g.resonate().toSmiles(), is("C1=CC=C=CC=C1"));
    }
    
    @Test public void sortH() throws Exception {
        Graph g = Graph.fromSmiles("C(C(C)[H])[H]");
        g.sort(new Graph.VisitHydrogenFirst());
        assertThat(g.toSmiles(), is("C([H])C([H])C"));
    }

    @Test public void sortHIsotopes() throws Exception {
        Graph g = Graph.fromSmiles("C([3H])([2H])[H]");
        g.sort(new Graph.VisitHydrogenFirst());
        assertThat(g.toSmiles(), is("C([H])([2H])[3H]"));
    }
    
    @Test public void hiBondOrderFirst() throws Exception {
        Graph g = Graph.fromSmiles("C=1C=CC=CC1");
        g.sort(new Graph.VisitHighOrderFirst());
        assertThat(g.toSmiles(), is("C1=CC=CC=C1"));
    }

    @Test public void hiBondOrderFirst2() throws Exception {
        Graph g = Graph.fromSmiles("P(=C)#N");
        g.sort(new Graph.VisitHighOrderFirst());
        assertThat(g.toSmiles(), is("P(#N)=C"));
    }
    
    @Test public void stableSort() throws Exception {
        Graph g = Graph.fromSmiles("C=1(C(=C(C(=C(C1[H])[H])[H])[H])[H])[H]");
        g.sort(new Graph.VisitHighOrderFirst());
        g.sort(new Graph.VisitHydrogenFirst());
        assertThat(g.toSmiles(), is("C1([H])=C([H])C([H])=C([H])C([H])=C1[H]"));
    }
    
    @Test public void CHEMBL1215012() throws Exception {
        Graph g = Graph.fromSmiles("[Na+].[Na+].CC(C)c1c(O)c(O)c(\\C=N\\[C@H]2[C@H]3SC(C)(C)[C@@H](N3C2=O)C(=O)[O-])c4C(=O)C(=C(C)C(=O)c14)C5=C(C)C(=O)c6c(C(C)C)c(O)c(O)c(\\C=N\\[C@H]7[C@H]8SC(C)(C)[C@@H](N8C7=O)C(=O)[O-])c6C5=O CHEMBL1215012");
        Assert.assertNotNull(g);
    }

    @Test public void nitgrogenStereochemistry() throws Exception {
        assertThat(Graph.fromSmiles("C1C[N@@]2CC[C@H]1C2").toSmiles(),
                   containsString("N@"));
    }

    @Test public void implicitChiralClasses() throws Exception {
        assertThat(Graph.fromSmiles("C1C[N@1]2CC[C@H]1C2").toSmiles(),
                   containsString("C1C[N@]2CC[C@H]1C2"));
        assertThat(Graph.fromSmiles("C1C[N@2]2CC[C@H]1C2").toSmiles(),
                   containsString("C1C[N@@]2CC[C@H]1C2"));
    }

    @Test public void cisplatin() throws IOException {
        Graph g = Graph.fromSmiles("[NH3][Pt@SP1]([NH3])(Cl)Cl");
        assertThat(g.topologyOf(1).type(),
                   is(Configuration.Type.SquarePlanar));
        assertThat(g.toSmiles(), is("[NH3][Pt@SP1]([NH3])(Cl)Cl"));
        assertThat(g.permute(new int[]{0, 1, 2, 4, 3}).toSmiles(),
                   is("[NH3][Pt@SP3]([NH3])(Cl)Cl"));
    }

    @Test public void trigonalBipyramidal() throws IOException {
        Graph g = Graph.fromSmiles("S[As@TB1](F)(Cl)(Br)N");
        assertThat(g.topologyOf(1).type(),
                   is(Configuration.Type.TrigonalBipyramidal));
        assertThat(g.toSmiles(), is("S[As@](F)(Cl)(Br)N"));
        assertThat(g.permute(new int[]{0, 1, 2, 4, 3, 5}).toSmiles(),
                   is("S[As@@](F)(Br)(Cl)N"));
    }

    @Test public void trigonalBipyramidal2() throws IOException {
        Graph g = Graph.fromSmiles("S[As@TB2](F)(Cl)(Br)N");
        assertThat(g.topologyOf(1).type(),
                   is(Configuration.Type.TrigonalBipyramidal));
        assertThat(g.toSmiles(), is("S[As@@](F)(Cl)(Br)N"));
        assertThat(g.permute(new int[]{0, 1, 2, 4, 3, 5}).toSmiles(),
                   is("S[As@](F)(Br)(Cl)N"));
    }

    @Test public void trigonalBipyramidal15() throws IOException {
        Graph g = Graph.fromSmiles("F[As@TB15](Cl)(S)(Br)N");
        assertThat(g.topologyOf(1).type(),
                   is(Configuration.Type.TrigonalBipyramidal));
        assertThat(g.toSmiles(), is("F[As@TB15](Cl)(S)(Br)N"));
        assertThat(g.permute(new int[]{0, 1, 2, 4, 3, 5}).toSmiles(),
                   is("F[As@TB17](Cl)(Br)(S)N"));
    }

    @Test public void octahedral1() throws IOException {
        Graph g = Graph.fromSmiles("C[Co@](F)(Cl)(Br)(I)S");
        assertThat(g.topologyOf(1).type(),
                   is(Configuration.Type.Octahedral));
        assertThat(g.toSmiles(), is("C[Co@](F)(Cl)(Br)(I)S"));
        assertThat(g.permute(new int[]{0, 1, 2, 4, 3, 5, 6}).toSmiles(),
                   is("C[Co@OH8](F)(Br)(Cl)(I)S"));
    }

    @Test public void extendedTetrahedralRingClosures() throws InvalidSmilesException {
        Graph g = Graph.fromSmiles("[CH3:3]1.[CH3:1]C(=[C@@]=[CH:2]1)[CH2:4]C");
        Assert.assertThat(g.topologyOf(3).configuration(), is(Configuration.AL2));
    }

    @Test public void testDegenerateOctahedral1() throws Exception {
        assertThat(Graph.fromSmiles("N[Co@OH1]N").toSmiles(),
                   containsString("N[Co@OH1]N"));
    }

    @Test public void testDegenerateOctahedral2() throws IOException {
        Graph g = Graph.fromSmiles("O=[V@OH25](Cl)(Cl)(F)F");
        assertThat(g.toSmiles(),
                   containsString("O=[V@OH25](Cl)(Cl)(F)F"));
    }

    @Test public void testDegenerateOctahedral3() throws IOException {
        Graph g = Graph.fromSmiles("O=[V@OH25]12(F)F.Cl1.Cl2");
        assertThat(g.toSmiles(),
                   containsString("O=[V@OH25](F)(F)(Cl)Cl"));
    }

    @Test public void nofail() throws IOException {
        Graph g = Graph.fromSmiles("CCCO[P@H]1(OC[C@@H]2[C@@H](O1)[C@@]([C@@H](O2)n3cnc4c3nc(nc4OCC)N)(C)F)O CHEMBL1630021");
        assertThat(g.toSmiles(),
                   CoreMatchers.is("CCCO[PH]1(OC[C@@H]2[C@@H](O1)[C@@]([C@@H](O2)n3cnc4c3nc(nc4OCC)N)(C)F)O"));
    }


}
