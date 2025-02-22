/*
 * Copyright (c) 2015. John May
 */

package uk.ac.ebi.beam;

import joptsimple.OptionSet;

import java.io.IOException;

/**
 * Anonymise SMILES input to all '*' atoms. Bond orders can optionally be emitted.
 */
public final class Anonymise extends FunctorCmdLnModule {

    private final Atom UNKN_ATOM = AtomImpl.AliphaticSubset.Any;

    public Anonymise() {
        super("anon");
        super.optparser.accepts("bo", "keep bond orders");
    }

    @Override
    Functor createFunctor(OptionSet optionSet) {
        final boolean bondorders = optionSet.has("bo");
        return new Functor() {
            @Override
            String map(String str) throws IOException {

                Graph g = Graph.fromSmiles(str);

                if (bondorders)
                    g = g.kekule();

                final GraphBuilder gb = GraphBuilder.create(g.order());

                for (int v = 0; v < g.order(); v++) {
                    gb.add(UNKN_ATOM);
                    for (Edge e : g.edges(v)) {
                        if (e.other(v) < v) {
                            if (bondorders)
                                gb.add(new Edge(v, e.other(v), bondForOrder(e.bond().order())));
                            else
                                gb.add(new Edge(v, e.other(v), Bond.IMPLICIT));
                        }
                    }
                }

                return gb.build().toSmiles() + suffixedId(str);
            }
        };
    }

    private Bond bondForOrder(int ord) {
        switch (ord) {
            case 1:
                return Bond.IMPLICIT;
            case 2:
                return Bond.DOUBLE;
            case 3:
                return Bond.TRIPLE;
            case 4:
                return Bond.QUADRUPLE;
            default:
                return Bond.IMPLICIT;
        }
    }
}
