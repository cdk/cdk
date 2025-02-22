package uk.ac.ebi.beam;

/** @author John May */
final class BondBasedConfiguration {

    static Configuration.DoubleBond configurationOf(Graph g,
                                                    int x, int u, int v, int y) {

        Edge e = g.edge(u, v);

        if (e.bond() != Bond.DOUBLE)
            throw new IllegalArgumentException("atoms u,v are not labelled as a double bond");

        Edge e1 = g.edge(u, x);
        Edge e2 = g.edge(v, y);

        Bond b1 = e1.bond(u);
        Bond b2 = e2.bond(v);

        if (b1 == Bond.IMPLICIT || b1 == Bond.SINGLE)
            return Configuration.DoubleBond.UNSPECIFIED;
        if (b2 == Bond.IMPLICIT || b2 == Bond.SINGLE)
            return Configuration.DoubleBond.UNSPECIFIED;

        return b1 == b2 ? Configuration.DoubleBond.TOGETHER
                        : Configuration.DoubleBond.OPPOSITE;
    }

}
