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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectedComponents;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.isomorphism.Pattern;
import org.openscience.cdk.isomorphism.Ullmann;
import org.openscience.cdk.isomorphism.matchers.smarts.SmartsMatchers;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.smiles.smarts.parser.SMARTSParser;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

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

        for (int i = 0; i < keys.length; i++) {
            final MaccsKey key     = keys[i];
            final Pattern  pattern = key.pattern;

            switch (key.smarts) {
                case "[!*]":
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

        // at this point we have skipped the entries whose pattern is "?"
        // (bits 1,44,125,166) so let try and do those features by hand

        // bit 125 aromatic ring count > 1
        // bit 101 a ring with more than 8 members
        AllRingsFinder ringFinder = new AllRingsFinder();
        IRingSet rings = ringFinder.findAllRings(container);
        int ringCount = 0;
        for (int i = 0; i < rings.getAtomContainerCount(); i++) {
            IAtomContainer ring = rings.getAtomContainer(i);
            boolean allAromatic = true;
            if (ringCount < 2) { // already found enough aromatic rings
                for (IBond bond : ring.bonds()) {
                    if (!bond.getFlag(CDKConstants.ISAROMATIC)) {
                        allAromatic = false;
                        break;
                    }
                }
            }
            if (allAromatic) ringCount++;
            if (ringCount > 1) fp.set(124);
            if (ring.getAtomCount() >= 8) fp.set(100);
        }

        // bit 166 (*).(*) we can match this in SMARTS but it's faster to just
        // count the number of component
        ConnectedComponents cc = new ConnectedComponents(GraphUtil.toAdjList(container));
        if (cc.nComponents() > 1) fp.set(165);

        return new BitSetFingerprint(fp);
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Integer> getRawFingerprint(IAtomContainer iAtomContainer) throws CDKException {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @Override
    public int getSize() {
        if (keys != null)
            return keys.length;
        else
            return 0; // throw exception when keys aren't loaded?
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
        return Ullmann.findSubstructure(SMARTSParser.parse(smarts, builder));
    }
}
