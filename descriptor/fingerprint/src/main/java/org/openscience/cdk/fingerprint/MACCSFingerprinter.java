/* Copyright (C) 2008 Rajarshi Guha <rajarshi@users.sourceforge.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.fingerprint;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.AllCycles;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.isomorphism.Pattern;
import org.openscience.cdk.isomorphism.VentoFoggia;
import org.openscience.cdk.isomorphism.matchers.smarts.SmartsMatchers;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.smiles.smarts.parser.SMARTSParser;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

/**
 * This fingerprinter generates 166 bit MACCS keys.
 * 
 * The SMARTS patterns for each of the features was taken from
 * <a href="http://www.rdkit.org"> RDKit</a>. However given that there is no
 * official and explicit listing of the original key definitions, the results
 * of this implementation may differ from others.
 *
 * This class assumes that aromaticity perception, atom typing and adding of
 * implicit hydrogens have been performed prior to generating the fingerprint.
 *
 * <b>Note</b> Currently bits 1 and 44 are completely ignored since the RDKit
 * defs do not provide a definition and I can't find an official description
 * of them.
 *
 * <b>Warning - MACCS substructure keys cannot be used for substructure
 * filtering. It is possible for some keys to match substructures and not match
 * the superstructures. Some keys check for hydrogen counts which may not be
 * preserved in a superstructure.</b>
 *
 * @author Rajarshi Guha
 * @cdk.created 2008-07-23
 * @cdk.keyword fingerprint
 * @cdk.keyword similarity
 * @cdk.module  fingerprint
 * @cdk.githash
 */
public class MACCSFingerprinter implements IFingerprinter {

    private static ILoggingTool logger          = LoggingToolFactory.createLoggingTool(MACCSFingerprinter.class);

    private static final String KEY_DEFINITIONS = "data/maccs.txt";

    private volatile MaccsKey[] keys            = null;

    public MACCSFingerprinter() {}

    public MACCSFingerprinter(IChemObjectBuilder builder) {
        try {
            keys = readKeyDef(builder);
        } catch (IOException e) {
            logger.debug(e);
        } catch (CDKException e) {
            logger.debug(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public IBitFingerprint getBitFingerprint(IAtomContainer container) throws CDKException {

        MaccsKey[] keys = keys(container.getBuilder());
        BitSet fp = new BitSet(keys.length);

        // init SMARTS invariants (connectivity, degree, etc)
        SmartsMatchers.prepare(container, false);

        final int numAtoms = container.getAtomCount();


        final GraphUtil.EdgeToBondMap bmap    = GraphUtil.EdgeToBondMap.withSpaceFor(container);
        final int[][]                 adjlist = GraphUtil.toAdjList(container, bmap);

        for (int i = 0; i < keys.length; i++) {
            final MaccsKey key     = keys[i];
            final Pattern  pattern = key.pattern;

            switch (key.smarts) {
                case "[!*]":
                    break;
                case "[!0]":
                    for (IAtom atom : container.atoms()) {
                        if (atom.getMassNumber() != null) {
                            fp.set(i);
                            break;
                        }
                    }
                    break;

                // ring bits
                case "[R]1@*@*@1": // 3M RING bit22
                case "[R]1@*@*@*@1": // 4M RING bit11
                case "[R]1@*@*@*@*@1": // 5M RING bit96
                case "[R]1@*@*@*@*@*@1": // 6M RING bit163, x2=bit145
                case "[R]1@*@*@*@*@*@*@1": // 7M RING, bit19
                case "[R]1@*@*@*@*@*@*@*@1": // 8M RING, bit101
                    // handled separately
                    break;

                case "(*).(*)":
                    // bit 166 (*).(*) we can match this in SMARTS but it's faster to just
                    // count the number of components or in this case try to traverse the
                    // component, iff there are some atoms not visited we have more than
                    // one component
                    boolean[] visit = new boolean[numAtoms];
                    if (visitPart(visit, adjlist, 0, -1) < numAtoms)
                        fp.set(165);
                    break;

                default:
                    if (key.count == 0) {
                        if (pattern.matches(container))
                            fp.set(i);
                    } else {
                        // check if there are at least 'count' unique hits, key.count = 0
                        // means find at least one match hence we add 1 to out limit
                        if (pattern.matchAll(container).uniqueAtoms().atLeast(key.count + 1))
                            fp.set(i);
                    }
                    break;
            }
        }

        // Ring Bits

        // threshold=126, see AllRingsFinder.Threshold.PubChem_97
        if (numAtoms > 2) {
            AllCycles allcycles = new AllCycles(adjlist,
                                                Math.min(8, numAtoms),
                                                126);
            int numArom = 0;
            for (int[] path : allcycles.paths()) {
                // length is +1 as we repeat the closure vertex
                switch (path.length) {
                    case 4: // 3M bit22
                        fp.set(21);
                        break;
                    case 5: // 4M bit11
                        fp.set(10);
                        break;
                    case 6: // 5M bit96
                        fp.set(95);
                        break;
                    case 7: // 6M bit163->bit145, bit124 numArom > 1

                        if (numArom < 2) {
                            if (isAromPath(path, bmap)) {
                                numArom++;
                                if (numArom == 2)
                                    fp.set(124);
                            }
                        }

                        if (fp.get(162)) {
                            fp.set(144); // >0
                        } else {
                            fp.set(162); // >1
                        }
                        break;
                    case 8: // 7M bit19
                        fp.set(18);
                        break;
                    case 9: // 8M bit101
                        fp.set(100);
                        break;
                }
            }
        }

        return new BitSetFingerprint(fp);
    }

    private static int visitPart(boolean[] visit, int[][] g, int beg, int prev) {
        visit[beg] = true;
        int visited = 1;
        for (int end : g[beg]) {
            if (end != prev && !visit[end])
                visited += visitPart(visit, g, end, beg);
        }
        return visited;
    }

    private static boolean isAromPath(int[] path, GraphUtil.EdgeToBondMap bmap) {
        int end = path.length - 1;
        for (int i = 0; i < end; i++) {
            if (!bmap.get(path[i], path[i+1]).isAromatic())
                return false;
        }
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Integer> getRawFingerprint(IAtomContainer iAtomContainer) throws CDKException {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public int getSize() {
        return 166;
    }

    private MaccsKey[] readKeyDef(final IChemObjectBuilder builder) throws IOException, CDKException {
        List<MaccsKey> keys = new ArrayList<MaccsKey>(166);
        BufferedReader reader = new BufferedReader(new InputStreamReader(getClass()
                .getResourceAsStream(KEY_DEFINITIONS)));

        // now process the keys
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.charAt(0) == '#') continue;
            String data = line.substring(0, line.indexOf('|')).trim();
            String[] toks = data.split("\\s");

            keys.add(new MaccsKey(toks[1], createPattern(toks[1], builder), Integer.parseInt(toks[2])));
        }
        if (keys.size() != 166) throw new CDKException("Found " + keys.size() + " keys during setup. Should be 166");
        return keys.toArray(new MaccsKey[166]);
    }

    private class MaccsKey {

        private String  smarts;
        private int     count;
        private Pattern pattern;

        private MaccsKey(String smarts, Pattern pattern, int count) {
            this.smarts = smarts;
            this.pattern = pattern;
            this.count = count;
        }

        public String getSmarts() {
            return smarts;
        }

        public int getCount() {
            return count;
        }
    }

    /** {@inheritDoc} */
    @Override
    public ICountFingerprint getCountFingerprint(IAtomContainer container) throws CDKException {
        throw new UnsupportedOperationException();
    }

    private final Object lock = new Object();

    /**
     * Access MACCS keys definitions.
     *
     * @return array of MACCS keys.
     * @throws CDKException maccs keys could not be loaded
     */
    private MaccsKey[] keys(final IChemObjectBuilder builder) throws CDKException {
        MaccsKey[] result = keys;
        if (result == null) {
            synchronized (lock) {
                result = keys;
                if (result == null) {
                    try {
                        keys = result = readKeyDef(builder);
                    } catch (IOException e) {
                        throw new CDKException("could not read MACCS definitions", e);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Create a pattern for the provided SMARTS - if the SMARTS is '?' a pattern
     * is not created.
     *
     * @param smarts  a smarts pattern
     * @param builder chem object builder
     * @return the pattern to match
     */
    private Pattern createPattern(String smarts, IChemObjectBuilder builder) {
        if (smarts.equals("[!0]")) return null; // FIXME can't be parsed by our SMARTS Grammar ATM
        return VentoFoggia.findSubstructure(SMARTSParser.parse(smarts, builder));
    }
}
