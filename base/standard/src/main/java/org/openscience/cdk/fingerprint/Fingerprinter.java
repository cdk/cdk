/* Copyright (C) 2002-2007  Christoph Steinbeck <steinbeck@users.sf.net>
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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.periodictable.PeriodicTable;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/**
 *  Generates a fingerprint for a given AtomContainer. Fingerprints are
 *  one-dimensional bit arrays, where bits are set according to a the
 *  occurrence of a particular structural feature (See for example the
 *  Daylight inc. theory manual for more information). Fingerprints allow for
 *  a fast screening step to exclude candidates for a substructure search in a
 *  database. They are also a means for determining the similarity of chemical
 *  structures. <p>
 *
 *  A fingerprint is generated for an AtomContainer with this code: <pre>
 *   Molecule molecule = new Molecule();
 *   IFingerprinter fingerprinter = new Fingerprinter();
 *   IBitFingerprint fingerprint = fingerprinter.getBitFingerprint(molecule);
 *   fingerprint.size(); // returns 1024 by default
 *   fingerprint.length(); // returns the highest set bit
 * </pre> <p>
 *
 *  The FingerPrinter assumes that hydrogens are explicitly given! Furthermore,
 *  if pseudo atoms or atoms with malformed symbols are present, their atomic
 *  number is taken as one more than the last element currently supported in
 *  {@link org.openscience.cdk.tools.periodictable.PeriodicTable}.
 *
 *  <font color="#FF0000">Warning: The aromaticity detection for this
 *  FingerPrinter relies on AllRingsFinder, which is known to take very long
 *  for some molecules with many cycles or special cyclic topologies. Thus,
 *  the AllRingsFinder has a built-in timeout of 5 seconds after which it
 *  aborts and throws an Exception. If you want your SMILES generated at any
 *  expense, you need to create your own AllRingsFinder, set the timeout to a
 *  higher value, and assign it to this FingerPrinter. In the vast majority of
 *  cases, however, the defaults will be fine. </font> <p>
 *
 *  <font color="#FF0000">Another Warning : The daylight manual says:
 *  "Fingerprints are not so definite: if a fingerprint indicates a pattern is
 *  missing then it certainly is, but it can only indicate a pattern's presence
 *  with some probability." In the case of very small molecules, the
 *  probability that you get the same fingerprint for different molecules is
 *  high. </font>
 *  </p>
 *
 * @author         steinbeck
 * @cdk.created    2002-02-24
 * @cdk.keyword    fingerprint
 * @cdk.keyword    similarity
 * @cdk.module     standard
 * @cdk.githash
 */
public class Fingerprinter extends AbstractFingerprinter implements IFingerprinter {

    /** Throw an exception if too many paths (per atom) are generated. */
    private final static int                 DEFAULT_PATH_LIMIT   = 1500;

    /** The default length of created fingerprints. */
    public final static int                  DEFAULT_SIZE         = 1024;
    /** The default search depth used to create the fingerprints. */
    public final static int                  DEFAULT_SEARCH_DEPTH = 8;

    private int                              size;
    private int                              searchDepth;
    private int                              pathLimit = DEFAULT_PATH_LIMIT;

    static int                               debugCounter         = 0;


    private static ILoggingTool              logger               = LoggingToolFactory
                                                                          .createLoggingTool(Fingerprinter.class);



    /**
     * Creates a fingerprint generator of length <code>DEFAULT_SIZE</code>
     * and with a search depth of <code>DEFAULT_SEARCH_DEPTH</code>.
     */
    public Fingerprinter() {
        this(DEFAULT_SIZE, DEFAULT_SEARCH_DEPTH);
    }

    public Fingerprinter(int size) {
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
    public Fingerprinter(int size, int searchDepth) {
        this.size = size;
        this.searchDepth = searchDepth;

    }

    /**
     * Generates a fingerprint of the default size for the given AtomContainer.
     *
     * @param container The AtomContainer for which a Fingerprint is generated
     * @param ringFinder An instance of
     *                   {@link org.openscience.cdk.ringsearch.AllRingsFinder}
     * @exception CDKException if there is a timeout in ring or aromaticity
     *                         perception
     * @return A {@link BitSet} representing the fingerprint
     */
    public IBitFingerprint getBitFingerprint(IAtomContainer container, AllRingsFinder ringFinder) throws CDKException {
        int position = -1;
        logger.debug("Entering Fingerprinter");
        logger.debug("Starting Aromaticity Detection");
        long before = System.currentTimeMillis();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(container);
        Aromaticity.cdkLegacy().apply(container);
        long after = System.currentTimeMillis();
        logger.debug("time for aromaticity calculation: " + (after - before) + " milliseconds");
        logger.debug("Finished Aromaticity Detection");
        BitSet bitSet = new BitSet(size);
        encodePaths(container, searchDepth, bitSet, size);
        return new BitSetFingerprint(bitSet);
    }

    /**
     * Generates a fingerprint of the default size for the given AtomContainer.
     *
     *@param container The AtomContainer for which a Fingerprint is generated
     */
    @Override
    public IBitFingerprint getBitFingerprint(IAtomContainer container) throws CDKException {
        return getBitFingerprint(container, null);
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Integer> getRawFingerprint(IAtomContainer iAtomContainer) throws CDKException {
        throw new UnsupportedOperationException();
    }

    private String encodePath(IAtomContainer mol, Map<IAtom, Map<IAtom, IBond>> cache, List<IAtom> path) {
        StringBuffer sb = new StringBuffer();
        IAtom x = path.get(0);

        sb.append(getAtomSymbol(x));
        for (int i = 1; i < path.size(); i++) {
            final IAtom[] y = {path.get(i)};
            Map<IAtom, IBond> m = cache.get(x);
            final IBond[] b = {m != null ? m.get(y[0]) : null};
            if (b[0] == null) {
                b[0] = mol.getBond(x, y[0]);
                cache.put(x, new HashMap<IAtom, IBond>() {

                    {
                        put(y[0], b[0]);
                    }
                });
            }
            sb.append(getBondSymbol(b[0]));
            sb.append(getAtomSymbol(y[0]));
            x = y[0];
        }

        // we store the lexicographically lower one of the
        // string and its reverse
        StringBuffer revForm = new StringBuffer(sb);
        revForm.reverse();
        if (sb.toString().compareTo(revForm.toString()) <= 0)
            return revForm.toString();
        else
            return sb.toString();
    }

    /**
     * Get all paths of lengths 0 to the specified length.
     *
     * This method will find all paths up to length N starting from each
     * atom in the molecule and return the unique set of such paths.
     *
     * @param container The molecule to search
     * @param searchDepth The maximum path length desired
     * @return A Map of path strings, keyed on themselves
     * @deprecated Use {@link #encodePaths(IAtomContainer, int, BitSet, int)}
     */
    @Deprecated
    protected int[] findPathes(IAtomContainer container, int searchDepth) throws CDKException {

        Set<Integer> hashes = new HashSet<>();

        Map<IAtom, Map<IAtom, IBond>> cache = new HashMap<IAtom, Map<IAtom, IBond>>();

        for (IAtom startAtom : container.atoms()) {
            List<List<IAtom>> p = PathTools.getLimitedPathsOfLengthUpto(container, startAtom, searchDepth, pathLimit);
            for (List<IAtom> path : p) {
                hashes.add(encodePath(container, cache, path).hashCode());
            }
        }

        int   pos = 0;
        int[] result = new int[hashes.size()];
        for (Integer hash : hashes)
            result[pos++] = hash;

        return result;
    }

    protected void encodePaths(IAtomContainer container, int searchDepth, BitSet fp, int size) throws CDKException {

        Random rand = new Random();

        Map<IAtom, Map<IAtom, IBond>> cache = new HashMap<IAtom, Map<IAtom, IBond>>();

        for (IAtom startAtom : container.atoms()) {
            List<List<IAtom>> p = PathTools.getLimitedPathsOfLengthUpto(container, startAtom, searchDepth, pathLimit);
            for (List<IAtom> path : p) {
                int x = encodePath(container, cache, path).hashCode();
                rand.setSeed(x);
                // XXX: fp.set(x % size); would work just as well but would encode a
                //      different bit
                fp.set(rand.nextInt(size));
            }
        }
    }

    private String getAtomSymbol(IAtom atom) {
        if (atom instanceof IPseudoAtom ||
            atom.getAtomicNumber() == null ||
            atom.getAtomicNumber() == 0) {
            return "*";
        } else {
            // XXX: backwards compatibility
            // This is completely random, I believe the intention is because
            // paths were reversed with string manipulation to de-duplicate
            // (only the lowest lexicographically is stored) however this
            // doesn't work with multiple atom symbols:
            // e.g. Fe-C => C-eF vs C-Fe => eF-C
            // A dirty hack is to replace "common" symbols with single letter
            // equivalents so the reversing is less wrong
            switch (atom.getAtomicNumber()) {
                case 17: // Cl
                    return "X";
                case 35: // Br
                    return "Z";
                case 14: // Si
                    return "Y";
                case 33: // As
                    return "D";
                case 3: // Li
                    return "L";
                case 34: // Se
                    return "E";
                case 11:  // Na
                    return "G";
                case 20:  // Ca
                    return "J";
                case 13:  // Al
                    return "A";
            }
            return atom.getSymbol();
        }
    }

    /**
     *  Gets the bondSymbol attribute of the Fingerprinter class
     *
     *@param  bond  Description of the Parameter
     *@return       The bondSymbol value
     */
    protected String getBondSymbol(IBond bond) {
        String bondSymbol = "";
        if (bond.getFlag(CDKConstants.ISAROMATIC)) {
            bondSymbol = ":";
        } else if (bond.getOrder() == IBond.Order.SINGLE) {
            bondSymbol = "-";
        } else if (bond.getOrder() == IBond.Order.DOUBLE) {
            bondSymbol = "=";
        } else if (bond.getOrder() == IBond.Order.TRIPLE) {
            bondSymbol = "#";
        }
        return bondSymbol;
    }

    public void setPathLimit(int limit) {
        this.pathLimit = limit;
    }

    public int getSearchDepth() {
        return searchDepth;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public ICountFingerprint getCountFingerprint(IAtomContainer container) throws CDKException {
        throw new UnsupportedOperationException();
    }

}
