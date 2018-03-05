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
import org.openscience.cdk.isomorphism.Pattern;
import org.openscience.cdk.isomorphism.VentoFoggia;
import org.openscience.cdk.isomorphism.matchers.smarts.SmartsMatchers;
import org.openscience.cdk.smiles.smarts.parser.SMARTSParser;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

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
public class MACCSFingerprinter extends AbstractFingerprinter implements IFingerprinter {

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
                    if (numAtoms > 1 && visitPart(visit, adjlist, 0, -1) < numAtoms)
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

    private static int findUnvisited(boolean[] array, int fromIndex) throws IndexOutOfBoundsException {
        if (fromIndex < 0 || fromIndex >= array.length) { throw new IndexOutOfBoundsException(); }

        for (int idx = fromIndex; idx < array.length; idx++) {
            if (! array[idx]) {
                return idx;
            }
        }
        return -1;
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
    public Map<String, Integer> getRawFingerprint(IAtomContainer container) throws CDKException {
        // For the counting fingerprints we need to consider how to deal with:
        // 1) Binary FPs that put molecules into a certain group, e.g. [Ac,Th,Pa,...] 0 | Actinides.
        //    ~~ a) We can count each atom separately. ~~
        //    ==> We stay with the binary version for the groups.
        // 2) Binary FPs that looking for one, two, etc occurrences:
        //    ==> Merge those fingerprints to a simple count of the corresponding structure.
        // 3) Rings
        //    ==> Rings are just simply counted.
        // 4) Fragments
        //    ==> Number of fragments is counted

        MaccsKey[] keys = keys(container.getBuilder());
        Map<String, Integer> fp = new HashMap<>(); // Tree maps impose an order of the inserted keys

        // init SMARTS invariants (connectivity, degree, etc)
        SmartsMatchers.prepare(container, false);

        final int numAtoms = container.getAtomCount();

        final GraphUtil.EdgeToBondMap bmap    = GraphUtil.EdgeToBondMap.withSpaceFor(container);
        final int[][]                 adjlist = GraphUtil.toAdjList(container, bmap);

        for (int i = 0; i < keys.length; i++) {
            final MaccsKey key     = keys[i];
            final Pattern  pattern = key.pattern;

            // We want to have fixed length MACCS counting fingerprints. So we add
            // for each SMARTS a (key, value = 0)-pair to the hash-map.
            // Some keys are repeated in the MACCS definition. We do not want to
            // override any existing value.
            if (! fp.containsKey(key.smarts)) {
                fp.put(key.smarts, 0);
            }

            switch (key.smarts) {
                // Aromatic ring (??). In the MACCS definitions (bit 124 / row 125) this is marked
                // with ["!*]. This is somehow strange? However, it is handled together with 6th rings.
                case "[!*]":
                    break;

                // isotopes
                case "[!0]":
                    // According to the MACCS definition this pattern checks whether a molecules
                    // is an isotope. In the counting fingerprints we can keep this property binary.
                    for (IAtom atom : container.atoms()) {
                        if (atom.getMassNumber() != null) {
                            fp.put(key.smarts, 1);
                            break;
                        }
                    }
                    break;

                // groups
                case "[!#1;!#6;!#7;!#8;!#9;!#14;!#15;!#16;!#17;!#35;!#53]":
                case "[#104,#105,#106,#107,#108,#109,#110,#111,#112]":
                case "[#5,Al,Ga,In,Tl]":
                case "[Ac,Th,Pa,U,Np,Pu,Am,Cm,Bk,Cf,Es,Fm,Md,No,Lr]":
                case "[Be,Mg,Ca,Sr,Ba,Ra]":
                case "[Cu,Zn,Ag,Cd,Au,Hg]":
                case "[Fe,Co,Ni,Ru,Rh,Pd,Os,Ir,Pt]":
                case "[Ge,As,as,Se,se,Sn,Sb,Te,Tl,Pb,Bi]":
                case "[La,Ce,Pr,Nd,Pm,Sm,Eu,Gd,Tb,Dy,Ho,Er,Tm,Yb,Lu]":
                case "[Li,Na,K,Rb,Cs,Fr]":
                case "[Sc,Ti,Y,Zr,Hf]":
                case "[V,Cr,Mn,Nb,Mo,Tc,Ta,W,Re]":
                case "[F,Cl,Br,I]":
                    if (pattern.matches(container)) {
                        fp.put(key.smarts, 1);
                    }
                    break;

                // ring counts
                case "[R]1@*@*@1": // 3M RING bit22
                case "[R]1@*@*@*@1": // 4M RING bit11
                case "[R]1@*@*@*@*@1": // 5M RING bit96
                case "[R]1@*@*@*@*@*@1": // 6M RING bit163, bit124
                case "[R]1@*@*@*@*@*@*@1": // 7M RING, bit19
                case "[R]1@*@*@*@*@*@*@*@1": // 8M RING, bit101
                    // handled separately
                    break;

                // fragments
                case "(*).(*)":
                    // bit 166 (*).(*) we can match this in SMARTS but it's faster to just
                    // count the number of components or in this case try to traverse the
                    // component, iff there are some atoms not visited we have more than
                    // one component
                    if (numAtoms > 1) {
                        boolean[] visited = new boolean[numAtoms];
                        int numComp = 0;
                        int beg     = 0;
                        do {
                            numComp++;
                            visitPart(visited, adjlist, beg, -1);
                            beg = findUnvisited(visited, 0);
                        } while (beg != -1);
                        fp.put(key.smarts, numComp);
                    }
                    else {
                        fp.put(key.smarts, numAtoms);
                    }
                    break;

                default:
                    // NOTE: 'countUnique' is unique in the sense of the set of involved atoms.
                    // TODO: Are there any fingerprint definitions for those we need to treat bonds uniquely?

                    if (fp.get(key.smarts) == 0) {
                        // If the key.count == 0, than in the binary fps we only check occurrence, if
                        // key.count == n != 0, than the binary fps checks, whether the substructure
                        // occurred at least n + 1 times. In counting version of the fingerprint we
                        // merge these two cases and simply count the how often a substructure occurred.
                        fp.put(key.smarts, pattern.matchAll(container).stereochemistry().countUnique());
                    }
                    break;
            }
        }

        // Ring counts

        // threshold=126, see AllRingsFinder.Threshold.PubChem_97
        if (numAtoms > 2) {
            AllCycles allcycles = new AllCycles(adjlist, Math.min(8, numAtoms), 126);

            // TODO: Are the cycles here unique?
            for (int[] path : allcycles.paths()) {
                String ringSmarts;
                // length is +1 as we repeat the closure vertex
                switch (path.length) {
                    case 4: // 3M bit22
                        ringSmarts = "[R]1@*@*@1";
                        break;
                    case 5: // 4M bit11
                        ringSmarts = "[R]1@*@*@*@1";
                        break;
                    case 6: // 5M bit96
                        ringSmarts = "[R]1@*@*@*@*@1";
                        break;
                    case 7: // 6M bit163->bit145, bit124 numArom > 1
                        ringSmarts = "[R]1@*@*@*@*@*@1";

                        // Handle aromatic rings
                        if (isAromPath(path, bmap)) {
                            // SMARTS taken from line 125 in the MACCS definition file.
                            fp.put("[!*]", fp.get("[!*]") + 1);
                        }
                        break;
                    case 8: // 7M bit19
                        ringSmarts = "[R]1@*@*@*@*@*@*@1";
                        break;
                    case 9: // 8M bit101
                        ringSmarts = "[R]1@*@*@*@*@*@*@*@1";
                        break;
                    default:
                        ringSmarts = "NULL";
                        break;
                }

                if (! ringSmarts.equals("NULL")) {
                    fp.put(ringSmarts, fp.get(ringSmarts) + 1);
                }
            }
        }

        return (fp);
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
        final Map<String, Integer> rawFingerprint = getRawFingerprint(container);
        final Map<Integer, String> mapInt2Key = new HashMap<>();

        // Key-set has fixed length of 157, i.e. the number of unique MACCS fps definitions.
        List<String> keys = new ArrayList<>(rawFingerprint.keySet());
        Collections.sort(keys);
        Integer idx = 0;
        for (String key : keys) { mapInt2Key.put(idx++, key); }

        return new ICountFingerprint() {
            @Override
            public long size() { return 157; }

            @Override
            public int numOfPopulatedbins() { return 157; }

            @Override
            public int getCount(int index) throws IndexOutOfBoundsException {
                if ((index < 0) || (index >= numOfPopulatedbins())) { throw new IndexOutOfBoundsException(); }
                return rawFingerprint.get(mapInt2Key.get(index));
            }

            @Override
            public int getHash(int index) { return index; }

            @Override
            public void merge(ICountFingerprint fp) {}

            @Override
            public void setBehaveAsBitFingerprint(boolean behaveAsBitFingerprint) {}

            @Override
            public boolean hasHash(int hash) { return true; }

            @Override
            public int getCountForHash(int hash) { return getCount(hash); }
        };
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
