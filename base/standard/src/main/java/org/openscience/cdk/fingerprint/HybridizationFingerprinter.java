/* Copyright (C) 2002-2007  Christoph Steinbeck <steinbeck@users.sf.net>
 *               2009-2011  Egon Willighagen <egonw@users.sf.net>
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

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType.Hybridization;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.periodictable.PeriodicTable;

/**
 * Generates a fingerprint for a given {@link IAtomContainer}. Fingerprints are
 * one-dimensional bit arrays, where bits are set according to a the occurrence
 * of a particular structural feature (See for example the Daylight inc. theory
 * manual for more information). Fingerprints allow for a fast screening step to
 * exclude candidates for a substructure search in a database. They are also a
 * means for determining the similarity of chemical structures.
 *
 * <p>A fingerprint is generated for an AtomContainer with this code:</p><pre>
 *   Molecule molecule = new Molecule();
 *   IFingerprinter fingerprinter =
 *     new HybridizationFingerprinter();
 *   BitSet fingerprint = fingerprinter.getFingerprint(molecule);
 *   fingerprint.size(); // returns 1024 by default
 *   fingerprint.length(); // returns the highest set bit
 * </pre>
 *
 * <p>The FingerPrinter assumes that hydrogens are explicitly given!
 * Furthermore, if pseudo atoms or atoms with malformed symbols are present,
 * their atomic number is taken as one more than the last element currently
 * supported in {@link PeriodicTable}.
 *
 * <p>Unlike the {@link Fingerprinter}, this fingerprinter does not take into
 * account aromaticity. Instead, it takes into account SP2
 * {@link Hybridization}.
 *
 * @cdk.keyword    fingerprint
 * @cdk.keyword    similarity
 * @cdk.module     standard
 * @cdk.githash
 */
public class HybridizationFingerprinter extends Fingerprinter implements IFingerprinter {

    /**
     * Creates a fingerprint generator of length <code>DEFAULT_SIZE</code>
     * and with a search depth of <code>DEFAULT_SEARCH_DEPTH</code>.
     */
    public HybridizationFingerprinter() {
        this(DEFAULT_SIZE, DEFAULT_SEARCH_DEPTH);
    }

    public HybridizationFingerprinter(int size) {
        this(size, DEFAULT_SEARCH_DEPTH);
    }

    /**
     * Constructs a fingerprint generator that creates fingerprints of
     * the given size, using a generation algorithm with the given search
     * depth.
     *
     * @param  size        The desired size of the fingerprint
     * @param  searchDepth The desired depth of search
     */
    public HybridizationFingerprinter(int size, int searchDepth) {
        super(size, searchDepth);
    }

    /**
     * Gets the bond Symbol attribute of the Fingerprinter class.
     *
     * @return       The bondSymbol value
     */
    protected String getBondSymbol(IBond bond) {
        String bondSymbol = "";
        if (bond.getOrder() == IBond.Order.SINGLE) {
            if (isSP2Bond(bond)) {
                bondSymbol = ":";
            } else {
                bondSymbol = "-";
            }
        } else if (bond.getOrder() == IBond.Order.DOUBLE) {
            if (isSP2Bond(bond)) {
                bondSymbol = ":";
            } else {
                bondSymbol = "=";
            }
        } else if (bond.getOrder() == IBond.Order.TRIPLE) {
            bondSymbol = "#";
        } else if (bond.getOrder() == IBond.Order.QUADRUPLE) {
            bondSymbol = "*";
        }
        return bondSymbol;
    }

    /**
     * Returns true if the bond binds two atoms, and both atoms are SP2.
     */
    private boolean isSP2Bond(IBond bond) {
        return bond.getAtomCount() == 2 && bond.getBegin().getHybridization() == Hybridization.SP2
               && bond.getEnd().getHybridization() == Hybridization.SP2;
    }
}
