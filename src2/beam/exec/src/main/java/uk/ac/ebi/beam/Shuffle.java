/*
 * Copyright (c) 2015. NextMove Software Ltd
 */

package uk.ac.ebi.beam;

import joptsimple.OptionSet;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class Shuffle extends PipingCmdLnModule {

    public Shuffle() {
        super("shuf");
        optparser.accepts("n", "try to produce 'n' random SMILES for each input")
                 .withRequiredArg()
                 .ofType(Integer.class)
                 .defaultsTo(10);
        optparser.accepts("m", "max number of shuffles")
                 .withRequiredArg()
                 .ofType(Integer.class)
                 .defaultsTo(500);
    }

    @Override
    void process(BufferedReader brdr, BufferedWriter bwtr, InputCounter counter, OptionSet optset) throws IOException {

        final int num = (Integer) optset.valueOf("n");
        final int max = (Integer) optset.valueOf("m");
        final boolean progress = !optset.has("prog-off");

        String line;
        int cnt = 0, gencnt = 0;
        while ((line = brdr.readLine()) != null) {
            try {
                final String id = suffixedId(line);
                for (String str : generate(line, num, max)) {
                    bwtr.write(str + id);
                    bwtr.newLine();
                    ++gencnt;
                }
                if (progress && ++cnt % 2500 == 0)
                    report("%d => %d", cnt, gencnt);
            } catch (InvalidSmilesException e) {
                System.err.println(e.getMessage());
            }
        }
        if (progress)
            report("%d => %d\n", cnt, gencnt);
    }

    private Set<String> generate(String str, int n, int m) throws IOException {

        final Set<String> smis = new HashSet<String>();

        Graph g = Graph.fromSmiles(str);

        while (m-- > 0 && smis.size() < n) {
            smis.add(g.toSmiles());
            g = Functions.randomise(g);
        }

        return smis;
    }
}
