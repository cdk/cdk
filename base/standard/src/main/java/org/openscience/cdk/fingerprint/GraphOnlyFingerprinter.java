/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.fingerprint;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import java.util.BitSet;

/**
 * Specialized version of the {@link Fingerprinter} which does not take bond orders
 * into account.
 *
 * @author         egonw
 * @cdk.created    2007-01-11
 * @cdk.keyword    fingerprint
 * @cdk.keyword    similarity
 * @cdk.module     standard
 * @cdk.githash
 *
 * @see            org.openscience.cdk.fingerprint.Fingerprinter
 */
public class GraphOnlyFingerprinter extends Fingerprinter {

    /**
     * Creates a fingerprint generator of length <code>defaultSize</code>
     * and with a search depth of <code>defaultSearchDepth</code>.
     */
    public GraphOnlyFingerprinter() {
        super(DEFAULT_SIZE, DEFAULT_SEARCH_DEPTH);
    }

    public GraphOnlyFingerprinter(int size) {
        super(size, DEFAULT_SEARCH_DEPTH);
    }

    public GraphOnlyFingerprinter(int size, int searchDepth) {
        super(size, searchDepth);
    }

    /**
     * Gets the bondSymbol attribute of the Fingerprinter class. Because we do
     * not consider bond orders to be important, we just return "";
     *
     * @param  bond  Description of the Parameter
     * @return       The bondSymbol value
     */
    @Override
    protected String getBondSymbol(IBond bond) {
        return "";
    }

    public BitSet getBitFingerprint(IAtomContainer container, int size) throws Exception {
        BitSet bitSet = new BitSet(size);
        encodePaths(container, super.getSearchDepth(), bitSet, size);
        return bitSet;
    }
}
