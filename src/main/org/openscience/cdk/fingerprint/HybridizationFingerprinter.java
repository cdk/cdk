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

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType.Hybridization;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.periodictable.PeriodicTable;

/**
 * Generates a fingerprint for a given {@link IAtomContainer}. Fingerprints are
 * one-dimensional bit arrays, where bits are set according to a the occurrence
 * of a particular structural feature (See for example the Daylight inc. theory
 * manual for more information). Fingerprints allow for a fast screening step to
 * exclude candidates for a substructure search in a database. They are also a
 * means for determining the similarity of chemical structures.
 *
 * <p>A fingerprint is generated for an AtomContainer with this code:<pre>
 *   Molecule molecule = new Molecule();
 *   IFingerprinter fingerprinter =
 *     new HybridizationFingerprinter();
 *   BitSet fingerprint = fingerprinter.getFingerprint(molecule);
 *   fingerprint.size(); // returns 1024 by default
 *   fingerprint.length(); // returns the highest set bit
 * </pre></p>
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
@TestClass("org.openscience.cdk.fingerprint.HybridizationFingerprinterTest")
public class HybridizationFingerprinter implements IFingerprinter {

	/** The default length of created fingerprints. */
	public final static int DEFAULT_SIZE = 1024;
	/** The default search depth used to create the fingerprints. */
	public final static int DEFAULT_SEARCH_DEPTH = 8;
	
	private int size;
	private int searchDepth;

	static int debugCounter = 0;

	private static final Map<String, String> queryReplace 
	        = new HashMap<String, String>() {
        
	    private static final long serialVersionUID = 1L;
        
	    {
	        put("Cl", "X");    put("Br", "Z");
	        put("Si", "Y");    put("As", "D");
	        put("Li", "L");    put("Se", "E");
	        put("Na", "G");    put("Ca", "J");
	        put("Al", "A");
	    }
	};
	
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
		this.size = size;
		this.searchDepth = searchDepth;

    }

	/**
	 * Generates a fingerprint of the default size for the given AtomContainer.
	 *
	 * @param  container The {@link IAtomContainer} for which a fingerprint is
	 *                   generated.
	 */
    @TestMethod("testGetFingerprint_IAtomContainer")
    public BitSet getFingerprint(IAtomContainer container) throws CDKException {
        BitSet bitSet = new BitSet(size);

        try {
            IAtomContainer clonedContainer = (IAtomContainer)container.clone();
            AtomContainerManipulator.
                percieveAtomTypesAndConfigureUnsetProperties(clonedContainer);
            int[] hashes = findPathes(clonedContainer, searchDepth);
            for (int hash : hashes) {
                bitSet.set(new Random(hash).nextInt(size));
            }
        } catch (CloneNotSupportedException exception) {
            throw new CDKException(
                "Exception while cloning the input: " + exception.getMessage(),
                exception
            );
        }

        return bitSet;
	}

    /**
     * Get all paths of lengths 0 to the specified length.
     * This method will find all paths up to length N starting from each
     * atom in the molecule and return the unique set of such paths.
     *
     * @param  container    The molecule to search
     * @param  searchDepth  The maximum path length desired
     * @return              A Map of path strings, keyed on themselves
     */
    protected int[] findPathes(IAtomContainer container, int searchDepth) {

        List<StringBuffer> allPaths = new ArrayList<StringBuffer>();

        Map<IAtom,Map<IAtom, IBond>> cache 
            = new HashMap<IAtom, Map<IAtom,IBond>>();
        
        for (IAtom startAtom : container.atoms()) {
                List<List<IAtom>> p = PathTools.getPathsOfLengthUpto(
                    container, startAtom, searchDepth
                );
                for (List<IAtom> path : p) {
                    StringBuffer sb = new StringBuffer();
                    IAtom x = path.get(0);

                    // TODO if we ever get more than 255 elements, this will
                    // fail maybe we should use 0 for pseudo atoms and
                    // malformed symbols?
                    if (x instanceof IPseudoAtom)
                        sb.append('0');
                    else {
                        Integer atnum = PeriodicTable.getAtomicNumber(x.getSymbol());
                        if (atnum != null) sb.append((char) atnum.intValue());
                        else sb.append('0');
                    }

                    for (int i = 1; i < path.size(); i++) {
                        final IAtom[] y = {path.get(i)};
                        Map<IAtom, IBond> m = cache.get( x );
                        final IBond[] b = { m != null ? m.get( y[0] ) : null };
                        if ( b[0] == null ) {
                            b[0] = container.getBond(x, y[0]);
                            cache.put(
                                x, 
                                new HashMap<IAtom, IBond>() {
                                  static final long serialVersionUID = 1L;
                                  { put(y[0], b[0]); }
                                }
                            );
                        }
                        sb.append(getBondSymbol(b[0]));
                        sb.append(convertSymbol(y[0].getSymbol()));
                        x = y[0];
                    }

                    // we store the lexicographically lower one of the
                    // string and its reverse
                    StringBuffer revForm = new StringBuffer(sb);
                    revForm.reverse();
                    if (sb.toString().compareTo(revForm.toString()) <= 0)
                        allPaths.add(sb);
                    else allPaths.add(revForm);
                }
        }
        // now lets clean stuff up
        Set<String> cleanPath = new HashSet<String>();
        for (StringBuffer s : allPaths) {
            if (cleanPath.contains( s.toString() )) continue;
            String s2 = s.reverse().toString();
            if (cleanPath.contains(s2)) continue;
            cleanPath.add(s2);
        }

        // convert paths to hashes
        int[] hashes = new int[cleanPath.size()];
        int i= 0;
        for (String s: cleanPath) hashes[i++] = s.hashCode();

        return hashes;
    }

    /**
     * Maps two character element symbols unto unique single character
     * equivalents.
     */
    private String convertSymbol(String symbol) {
        String returnSymbol = queryReplace.get( symbol );
        return returnSymbol == null ? symbol
                                    : returnSymbol;
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
        if (bond.getAtomCount() == 2 &&
            bond.getAtom(0).getHybridization() == Hybridization.SP2 &&
            bond.getAtom(1).getHybridization() == Hybridization.SP2)
            return true;
        return false;
    }

    @TestMethod("testGetSearchDepth")
    public int getSearchDepth() {
		return searchDepth;
	}

    @TestMethod("testGetSize")
	public int getSize() {
		return size;
	}

    /**
     * {@inheritDoc}
     */
    public Map<String, Integer> getRawFingerprint(IAtomContainer container) {
        throw new UnsupportedOperationException();
    }

	@Override
	public IBitFingerprint getBitFingerprint(IAtomContainer container)
			throws CDKException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICountFingerprint getCountFingerprint(IAtomContainer container)
			throws CDKException {
		// TODO Auto-generated method stub
		return null;
	}

}

