package uk.ac.ebi.beam;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static uk.ac.ebi.beam.Configuration.AL1;
import static uk.ac.ebi.beam.Configuration.AL2;
import static uk.ac.ebi.beam.Configuration.ANTI_CLOCKWISE;
import static uk.ac.ebi.beam.Configuration.CLOCKWISE;
import static uk.ac.ebi.beam.Configuration.TH1;
import static uk.ac.ebi.beam.Configuration.TH2;

/** @author John May */
public class TopologyTest {

    @Test
    public void unknown() throws Exception {
        assertThat(Topology.unknown()
                           .configuration(),
                   is(Configuration.UNKNOWN));
    }

    @Test(expected = IllegalArgumentException.class)
    public void unknownAtom() throws Exception {
        Topology.unknown()
                .atom();
    }

    @Test
    public void unknownTransform() {
        assertThat(Topology.unknown().transform(new int[0]),
                   is(sameInstance(Topology.unknown())));
    }

    @Test
    public void unknownOrderBy() {
        assertThat(Topology.unknown().orderBy(new int[0]),
                   is(sameInstance(Topology.unknown())));
    }

    @Test
    public void permutationParity() {
        assertThat(Topology.parity(new int[]{0, 1, 2, 3},
                                   new int[]{0, 1, 2, 3}), is(1));   // even
        assertThat(Topology.parity(new int[]{0, 1, 2, 3},
                                   new int[]{0, 1, 3, 2}), is(-1));  // swap 2,3 = odd
        assertThat(Topology.parity(new int[]{0, 1, 2, 3},
                                   new int[]{1, 0, 3, 2}), is(1));   // swap 0,1 = even
        assertThat(Topology.parity(new int[]{0, 1, 2, 3},
                                   new int[]{2, 0, 3, 1}), is(-1));  // swap 0,3 = odd
    }

    @Test
    public void sort() {
        int[] org = new int[]{1, 2, 3, 4};
        assertThat(Topology.sort(org, new int[]{0, 1, 2, 3, 4}),
                   is(not(sameInstance(org))));
        assertThat(Topology.sort(org, new int[]{0, 1, 2, 3, 4}),
                   is(new int[]{1, 2, 3, 4}));
        assertThat(Topology.sort(org, new int[]{0, 2, 1, 3, 4}),
                   is(new int[]{2, 1, 3, 4}));
        // non-sequential
        assertThat(Topology.sort(org, new int[]{0, 2, 1, 7, 4}),
                   is(new int[]{2, 1, 4, 3}));
    }

    @Test public void tetrahedralAtom() {
        Topology t1 = Topology.tetrahedral(1, new int[]{0, 2, 3, 4}, TH1);
        assertThat(t1.atom(), is(1));
    }

    @Test public void tetrahedralOrderBy() {
        // test shows the first example of tetrahedral configuration from the
        // OpenSMILES specification

        // N=1, Br=2, O=3, C=4
        Topology t1 = Topology.tetrahedral(0, new int[]{1, 2, 3, 4}, TH1);

        // N, Br, O, C
        assertThat(t1.orderBy(new int[]{0, 1, 2, 4, 3})
                     .configuration(), is(TH2));
        // O, Br, C, N
        assertThat(t1.orderBy(new int[]{0, 4, 2, 1, 3})
                     .configuration(), is(TH1));
        // C, Br, N, O
        assertThat(t1.orderBy(new int[]{0, 3, 2, 4, 1})
                     .configuration(), is(TH1));
        // C, Br, O, N
        assertThat(t1.orderBy(new int[]{0, 4, 2, 3, 1})
                     .configuration(), is(TH2));
        // Br, O, N, C
        assertThat(t1.orderBy(new int[]{0, 3, 1, 2, 4})
                     .configuration(), is(TH1));
        // Br, C, O, N
        assertThat(t1.orderBy(new int[]{0, 4, 1, 3, 2})
                     .configuration(), is(TH1));
        // Br, N, C, O
        assertThat(t1.orderBy(new int[]{0, 2, 1, 4, 3})
                     .configuration(), is(TH1));
        // Br, N, O, C
        assertThat(t1.orderBy(new int[]{0, 2, 1, 3, 4})
                     .configuration(), is(TH2));
    }

    @Test public void implicitToExplicit_tetrahedral() {

        // N[C@]([H])(C)C(=O)O
        Graph g = new Graph(7);
        g.addAtom(AtomImpl.AliphaticSubset.Nitrogen);
        g.addAtom(new AtomImpl.BracketAtom(Element.Carbon, 0, 0));
        g.addAtom(AtomImpl.EXPLICIT_HYDROGEN);
        g.addAtom(AtomImpl.AliphaticSubset.Carbon);
        g.addAtom(AtomImpl.AliphaticSubset.Carbon);
        g.addAtom(AtomImpl.AliphaticSubset.Oxygen);
        g.addAtom(AtomImpl.AliphaticSubset.Oxygen);

        g.addEdge(new Edge(0, 1, Bond.IMPLICIT));
        g.addEdge(new Edge(1, 2, Bond.IMPLICIT));
        g.addEdge(new Edge(1, 3, Bond.IMPLICIT));
        g.addEdge(new Edge(1, 4, Bond.IMPLICIT));
        g.addEdge(new Edge(4, 5, Bond.DOUBLE));
        g.addEdge(new Edge(4, 6, Bond.IMPLICIT));

        assertThat(Topology.toExplicit(g, 1, ANTI_CLOCKWISE), is(TH1));
        assertThat(Topology.toExplicit(g, 1, CLOCKWISE), is(TH2));
    }

    @Test public void implicitToExplicit_tetrahedralImplicitH() {

        // N[C@]([H])(C)C(=O)O
        Graph g = new Graph(7);
        g.addAtom(AtomImpl.AliphaticSubset.Nitrogen);
        g.addAtom(new AtomImpl.BracketAtom(Element.Carbon, 1, 0));
        g.addAtom(AtomImpl.AliphaticSubset.Carbon);
        g.addAtom(AtomImpl.AliphaticSubset.Carbon);
        g.addAtom(AtomImpl.AliphaticSubset.Oxygen);
        g.addAtom(AtomImpl.AliphaticSubset.Oxygen);

        g.addEdge(new Edge(0, 1, Bond.IMPLICIT));
        g.addEdge(new Edge(1, 2, Bond.IMPLICIT));
        g.addEdge(new Edge(1, 3, Bond.IMPLICIT));
        g.addEdge(new Edge(3, 4, Bond.DOUBLE));
        g.addEdge(new Edge(3, 5, Bond.IMPLICIT));

        assertThat(Topology.toExplicit(g, 1, ANTI_CLOCKWISE), is(TH1));
        assertThat(Topology.toExplicit(g, 1, CLOCKWISE), is(TH2));
    }

    @Test public void implicitToExplicit_sulfoxide() {
        // C[S@](CC)=O
        Graph g = new Graph(5);
        g.addAtom(AtomImpl.AliphaticSubset.Carbon);
        g.addAtom(new AtomImpl.BracketAtom(Element.Sulfur, 0, 0));
        g.addAtom(AtomImpl.AliphaticSubset.Carbon);
        g.addAtom(AtomImpl.AliphaticSubset.Carbon);
        g.addAtom(AtomImpl.AliphaticSubset.Oxygen);

        g.addEdge(new Edge(0, 1, Bond.IMPLICIT));
        g.addEdge(new Edge(1, 2, Bond.IMPLICIT));
        g.addEdge(new Edge(1, 4, Bond.DOUBLE));
        g.addEdge(new Edge(2, 3, Bond.IMPLICIT));

        assertThat(Topology.toExplicit(g, 1, ANTI_CLOCKWISE), is(TH1));
        assertThat(Topology.toExplicit(g, 1, CLOCKWISE), is(TH2));
    }
    
    // CCCCC[P@@]1CCC[C@H]1[C@@H]2CCCP2CCCCC CID 59836513
    @Test public void implicitToExplicit_phosphorus() throws Exception {
        // C[P@@](CC)O
        Graph g = new Graph(5);
        g.addAtom(AtomImpl.AliphaticSubset.Carbon);
        g.addAtom(new AtomImpl.BracketAtom(Element.Phosphorus, 0, 0));
        g.addAtom(AtomImpl.AliphaticSubset.Carbon);
        g.addAtom(AtomImpl.AliphaticSubset.Carbon);
        g.addAtom(AtomImpl.AliphaticSubset.Oxygen);

        g.addEdge(new Edge(0, 1, Bond.IMPLICIT));
        g.addEdge(new Edge(1, 2, Bond.IMPLICIT));
        g.addEdge(new Edge(1, 4, Bond.IMPLICIT));
        g.addEdge(new Edge(2, 3, Bond.IMPLICIT));
        
        assertThat(Topology.toExplicit(g, 1, ANTI_CLOCKWISE), is(TH1));
        assertThat(Topology.toExplicit(g, 1, CLOCKWISE), is(TH2));
    }

    @Test public void implicitToExplicit_allene() {

        // OC(Cl)=[C@]=C(C)F
        Graph g = new Graph(7);
        g.addAtom(AtomImpl.AliphaticSubset.Oxygen);
        g.addAtom(AtomImpl.AliphaticSubset.Carbon);
        g.addAtom(AtomImpl.AliphaticSubset.Chlorine);
        g.addAtom(new AtomImpl.BracketAtom(Element.Carbon, 0, 0));
        g.addAtom(AtomImpl.AliphaticSubset.Carbon);
        g.addAtom(AtomImpl.AliphaticSubset.Carbon);
        g.addAtom(AtomImpl.AliphaticSubset.Fluorine);

        g.addEdge(new Edge(0, 1, Bond.IMPLICIT));
        g.addEdge(new Edge(1, 2, Bond.IMPLICIT));
        g.addEdge(new Edge(1, 3, Bond.DOUBLE));
        g.addEdge(new Edge(3, 4, Bond.DOUBLE));
        g.addEdge(new Edge(4, 5, Bond.IMPLICIT));
        g.addEdge(new Edge(5, 6, Bond.IMPLICIT));

        assertThat(Topology.toExplicit(g, 3, ANTI_CLOCKWISE), is(AL1));
        assertThat(Topology.toExplicit(g, 3, CLOCKWISE), is(AL2));
    }

    @Test public void implicitToExplicit_trigonalBipyramidal() {
        // O=C[As@](F)(Cl)(Br)S
        Graph g = new Graph(7);
        g.addAtom(AtomImpl.AliphaticSubset.Oxygen);
        g.addAtom(AtomImpl.AliphaticSubset.Carbon);
        g.addAtom(new AtomImpl.BracketAtom(Element.Arsenic, 0, 0));
        g.addAtom(AtomImpl.AliphaticSubset.Fluorine);
        g.addAtom(AtomImpl.AliphaticSubset.Chlorine);
        g.addAtom(AtomImpl.AliphaticSubset.Bromine);
        g.addAtom(AtomImpl.AliphaticSubset.Sulfur);

        g.addEdge(new Edge(0, 1, Bond.DOUBLE));
        g.addEdge(new Edge(1, 2, Bond.IMPLICIT));
        g.addEdge(new Edge(2, 3, Bond.IMPLICIT));
        g.addEdge(new Edge(2, 4, Bond.IMPLICIT));
        g.addEdge(new Edge(2, 5, Bond.IMPLICIT));
        g.addEdge(new Edge(2, 6, Bond.IMPLICIT));

        assertThat(Topology.toExplicit(g, 2, ANTI_CLOCKWISE),
                   is(Configuration.TB1));
        assertThat(Topology.toExplicit(g, 2, CLOCKWISE),
                   is(Configuration.TB2));
    }

    @Test public void implicitToExplicit_octahedral() {
        // S[Co@@](F)(Cl)(Br)(I)C=O
        Graph g = new Graph(8);
        g.addAtom(AtomImpl.AliphaticSubset.Sulfur);
        g.addAtom(new AtomImpl.BracketAtom(Element.Cobalt, 0, 0));
        g.addAtom(AtomImpl.AliphaticSubset.Fluorine);
        g.addAtom(AtomImpl.AliphaticSubset.Chlorine);
        g.addAtom(AtomImpl.AliphaticSubset.Bromine);
        g.addAtom(AtomImpl.AliphaticSubset.Iodine);
        g.addAtom(AtomImpl.AliphaticSubset.Carbon);
        g.addAtom(AtomImpl.AliphaticSubset.Oxygen);

        g.addEdge(new Edge(0, 1, Bond.IMPLICIT));
        g.addEdge(new Edge(1, 2, Bond.IMPLICIT));
        g.addEdge(new Edge(1, 3, Bond.IMPLICIT));
        g.addEdge(new Edge(1, 4, Bond.IMPLICIT));
        g.addEdge(new Edge(1, 5, Bond.IMPLICIT));
        g.addEdge(new Edge(1, 6, Bond.IMPLICIT));
        g.addEdge(new Edge(6, 7, Bond.DOUBLE));

        assertThat(Topology.toExplicit(g, 1, ANTI_CLOCKWISE),
                   is(Configuration.OH1));
        assertThat(Topology.toExplicit(g, 1, CLOCKWISE),
                   is(Configuration.OH2));
    }

    @Test public void implicitToExplicit_unknown() {
        assertThat(Topology.toExplicit(new Graph(0), 0, Configuration.UNKNOWN),
                   is(Configuration.UNKNOWN));
    }

    @Test public void implicitToExplicit_th1_th2() {
        assertThat(Topology.toExplicit(new Graph(0), 0, Configuration.TH1),
                   is(Configuration.TH1));
        assertThat(Topology.toExplicit(new Graph(0), 0, Configuration.TH2),
                   is(Configuration.TH2));
    }

    @Test public void implicitToExplicit_al1_al2() {
        assertThat(Topology.toExplicit(new Graph(0), 0, Configuration.AL1),
                   is(Configuration.AL1));
        assertThat(Topology.toExplicit(new Graph(0), 0, Configuration.AL2),
                   is(Configuration.AL2));
    }

    @Test public void implicitToExplicit_tbs() {
        for (Configuration c : Configuration.values()) {
            if (c.type() == Configuration.Type.TrigonalBipyramidal)
                assertThat(Topology.toExplicit(new Graph(0), 0, c),
                           is(c));
        }
    }

    @Test public void implicitToExplicit_ohs() {
        for (Configuration c : Configuration.values()) {
            if (c.type() == Configuration.Type.Octahedral)
                assertThat(Topology.toExplicit(new Graph(0), 0, c),
                           is(c));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_AntiClockwise() {
        Topology.create(0, new int[0], Collections
                .<Edge>emptyList(), Configuration.ANTI_CLOCKWISE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_Clockwise() {
        Topology.create(0, new int[0], Collections
                .<Edge>emptyList(), Configuration.CLOCKWISE);
    }

    @Test
    public void create_tb() {
        int[] vs = new int[]{1, 2, 3, 4, 5};
        List<Edge> es = Arrays.asList(new Edge(0, 1, Bond.IMPLICIT),
                                      new Edge(0, 2, Bond.IMPLICIT),
                                      new Edge(0, 3, Bond.IMPLICIT),
                                      new Edge(0, 4, Bond.IMPLICIT),
                                      new Edge(0, 5, Bond.IMPLICIT));
        Topology t = Topology.create(0, vs, es, Configuration.TB5);
        assertThat(t.configuration(), is(Configuration.TB5));
        assertThat(t.atom(), is(0));
    }

    @Test
    public void create_sp() {
        int[] vs = new int[]{1, 2, 3, 4};
        List<Edge> es = Arrays.asList(new Edge(0, 1, Bond.IMPLICIT),
                                      new Edge(0, 2, Bond.IMPLICIT),
                                      new Edge(0, 3, Bond.IMPLICIT),
                                      new Edge(0, 4, Bond.IMPLICIT));
        Topology t = Topology.create(0, vs, es, Configuration.SP1);
        assertThat(t.configuration(), is(Configuration.SP1));
        assertThat(t.atom(), is(0));
    }

    @Test
    public void create_oh() {
        int[] vs = new int[]{1, 2, 3, 4, 5, 6};
        List<Edge> es = Arrays.asList(new Edge(0, 1, Bond.IMPLICIT),
                                      new Edge(0, 2, Bond.IMPLICIT),
                                      new Edge(0, 3, Bond.IMPLICIT),
                                      new Edge(0, 4, Bond.IMPLICIT),
                                      new Edge(0, 5, Bond.IMPLICIT),
                                      new Edge(0, 6, Bond.IMPLICIT));
        Topology t = Topology.create(0, vs, es, Configuration.OH1);
        assertThat(t.configuration(), is(Configuration.OH1));
        assertThat(t.atom(), is(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_al() {
        assertThat(Topology.create(0, new int[0], Collections
                .<Edge>emptyList(), Configuration.AL1), is(Topology.unknown()));
    }

    @Test public void create_th() {
        int[] vs = new int[]{1, 2, 3, 4};
        List<Edge> es = Arrays.asList(new Edge(0, 1, Bond.IMPLICIT),
                                      new Edge(0, 2, Bond.IMPLICIT),
                                      new Edge(0, 3, Bond.IMPLICIT),
                                      new Edge(0, 4, Bond.IMPLICIT));
        Topology t = Topology.create(0, vs, es, TH1);
        assertThat(t.configuration(), is(TH1));
        assertThat(t.atom(), is(0));
    }

}
