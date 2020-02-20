/* Copyright (c) 2014 Collaborative Drug Discovery, Inc. <alex@collaborativedrug.com>
 *
 * Implemented by Alex M. Clark, produced by Collaborative Drug Discovery, Inc.
 * Made available to the CDK community under the terms of the GNU LGPL.
 *
 *    http://collaborativedrug.com
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

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.CRC32;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IStereoElement;


/**
 *  <p>Circular fingerprints: for generating fingerprints that are functionally equivalent to ECFP-2/4/6 and FCFP-2/4/6
 *  fingerprints, which are partially described by Rogers et al. {@cdk.cite Rogers2010}.
 *
 *  <p>While the literature describes the method in detail, it does not disclose either the hashing technique for converting
 *  lists of integers into 32-bit codes, nor does it describe the scheme used to classify the atom types for creating
 *  the FCFP-class of descriptors. For this reason, the fingerprints that are created are not binary compatible with
 *  the reference implementation. They do, however, achieve effectively equal performance for modelling purposes.</p>
 *
 *  <p>The resulting fingerprint bits are presented as a list of unique bits, each with a 32-bit hashcode; typically there
 *  are no more than a hundred or so unique bit hashcodes per molecule. These identifiers can be folded into a smaller
 *  array of bits, such that they can be represented as a single long binary number, which is often more convenient.</p>
 *
 *	<p>The  integer hashing is done using the CRC32 algorithm, using the Java CRC32 class, which is the same
 *	formula/parameters as used by PNG files, and described in:</p>
 *
 *		<a href="http://www.w3.org/TR/PNG/#D-CRCAppendix">http://www.w3.org/TR/PNG/#D-CRCAppendix</a>
 *
 *	<p>Implicit vs. explicit hydrogens are handled, i.e. it doesn't matter whether the incoming molecule is hydrogen
 *	suppressed or not.</p>
 *
 *  <p>Implementation note: many of the algorithms involved in the generation of fingerprints (e.g. aromaticity, atom
 *  typing) have been coded up explicitly for use by this class, rather than making use of comparable functionality
 *  elsewhere in the CDK. This is to ensure that the CDK implementation of the algorithm is strictly equal to other
 *  implementations: dependencies on CDK functionality that could be modified or improved in the future would break
 *  binary compatibility with formerly identical implementations on other platforms.</p>
 *
 *  <p>For the FCFP class of fingerprints, atom typing is done using a scheme similar to that described by
 *  Green et al {@cdk.cite Green1994}.</p>
 *  
 *  <p>The fingerprints and their uses have been described in Clark et al. {@cdk.cite Clark2014}.
 *
 * <br/>
 * <b>
 * Important! this fingerprint can not be used for substructure screening.
 * </b>
 *
 * @author         am.clark
 * @cdk.created    2014-01-01
 * @cdk.keyword    fingerprint
 * @cdk.keyword    similarity
 * @cdk.module     standard
 * @cdk.githash
 */
public class CircularFingerprinter extends AbstractFingerprinter implements IFingerprinter {

    // ------------ constants ------------

    // identity by literal atom environment
    public static final int CLASS_ECFP0 = 1;
    public static final int CLASS_ECFP2 = 2;
    public static final int CLASS_ECFP4 = 3;
    public static final int CLASS_ECFP6 = 4;
    // identity by functional character of the atom
    public static final int CLASS_FCFP0 = 5;
    public static final int CLASS_FCFP2 = 6;
    public static final int CLASS_FCFP4 = 7;
    public static final int CLASS_FCFP6 = 8;

    public static final class FP {

        public int   hashCode;
        public int   iteration;
        public int[] atoms;

        public FP(int hashCode, int iteration, int[] atoms) {
            this.hashCode = hashCode;
            this.iteration = iteration;
            this.atoms = atoms;
        }
    }
    

    // ------------ private members ------------

    private final int      ATOMCLASS_ECFP = 1;
    private final int      ATOMCLASS_FCFP = 2;

    private IAtomContainer mol;
    private final int      length;

    private int[]          identity;
    private boolean[]      resolvedChiral;
    private int[][]        atomGroup;
    private CRC32          crc            = new CRC32();        // recycled for each CRC calculation
    private ArrayList<FP>  fplist         = new ArrayList<FP>();

    // summary information about the molecule, for quick access
    private boolean[]      amask;                               // true for all heavy atoms, i.e. hydrogens and non-elements are excluded
    private int[]          hcount;                              // total hydrogen count, including explicit and implicit hydrogens
    private int[][]        atomAdj, bondAdj;                    // precalculated adjacencies, including only those qualifying with 'amask'
    private int[]          ringBlock;                           // ring block identifier; 0=not in a ring
    private int[][]        smallRings;                          // all rings of size 3 through 7
    private int[]          bondOrder;                           // numeric bond order for easy reference
    private boolean[]      atomArom, bondArom;                  // aromaticity precalculated
    private int[][]        tetra;                               // tetrahedral rubric, a precursor to chirality

    // stored information for bio-typing; only defined for FCFP-class fingerprints
    private boolean[]      maskDon, maskAcc, maskPos, maskNeg, maskAro, maskHal; // functional property flags
    private int[]          bondSum;                                             // sum of bond orders for each atom (including explicit H's)
    private boolean[]      hasDouble;                                           // true if an atom has any double bonds
    private boolean[]      aliphatic;                                           // true for carbon atoms with only sigma bonds
    private boolean[]      isOxide;                                             // true if the atom has a double bond to oxygen
    private boolean[]      lonePair;                                            // true if the atom is N,O,S with octet valence and at least one lone pair
    private boolean[]      tetrazole;                                           // special flag for being in a tetrazole (C1=NN=NN1) ring

    // ------------ options -------------------
    private int     classType, atomClass;
    private boolean optPerceiveStereo = false;

    // ------------ public methods ------------

    /**
     * Default constructor: uses the ECFP6 type.
     */
    public CircularFingerprinter() {
        this(CLASS_ECFP6);
    }

    /**
     * Specific constructor: initializes with descriptor class type, one of ECFP_{p} or FCFP_{p}, where ECFP is
     * for the extended-connectivity fingerprints, FCFP is for the functional class version, and {p} is the
     * path diameter, and may be 0, 2, 4 or 6.
     *
     * @param classType one of CLASS_ECFP{n} or CLASS_FCFP{n}
     */
    public CircularFingerprinter(int classType) {
        this(classType, 1024);
    }

    /**
     * Specific constructor: initializes with descriptor class type, one of ECFP_{p} or FCFP_{p}, where ECFP is
     * for the extended-connectivity fingerprints, FCFP is for the functional class version, and {p} is the
     * path diameter, and may be 0, 2, 4 or 6.
     *
     * @param classType one of CLASS_ECFP{n} or CLASS_FCFP{n}
     * @param len size of folded (binary) fingerprint                  
     */
    public CircularFingerprinter(int classType, int len) {
        if (classType < 1 || classType > 8)
            throw new IllegalArgumentException("Invalid classType specified: " + classType);
        this.classType = classType;
        this.length = len;
    }

    /**
     * Sets whether stereochemistry should be re-perceived from 2D/3D
     * coordinates. By default stereochemistry encoded as {@link IStereoElement}s
     * are used.
     *
     * @param val perceived from 2D
     */
    public void setPerceiveStereo(boolean val) {
        this.optPerceiveStereo = val;
    }

    @Override
    protected List<Map.Entry<String, String>> getParameters() {
        String type = null;
        switch (classType) {
            case CLASS_ECFP0: type = "ECFP0"; break;
            case CLASS_ECFP2: type = "ECFP2"; break;
            case CLASS_ECFP4: type = "ECFP4"; break;
            case CLASS_ECFP6: type = "ECFP6"; break;
            case CLASS_FCFP0: type = "FCFP0"; break;
            case CLASS_FCFP2: type = "FCFP2"; break;
            case CLASS_FCFP4: type = "FCFP4"; break;
            case CLASS_FCFP6: type = "FCFP6"; break;
        }
        return Arrays.<Map.Entry<String, String>>asList(
            new AbstractMap.SimpleImmutableEntry<>("classType", type),
            new AbstractMap.SimpleImmutableEntry<>("perceiveStereochemistry",
                                                   Boolean.toString(optPerceiveStereo))
        );
    }

    /**
     * Calculates the fingerprints for the given {@link IAtomContainer}, and stores them for subsequent retrieval.
     *
     * @param mol chemical structure; all nodes should be known legitimate elements
     */
    public void calculate(IAtomContainer mol) throws CDKException {
        this.mol = mol;
        fplist.clear();
        atomClass = classType <= CLASS_ECFP6 ? ATOMCLASS_ECFP : ATOMCLASS_FCFP;

        excavateMolecule();
        if (atomClass == ATOMCLASS_FCFP) calculateBioTypes();

        final int na = mol.getAtomCount();
        identity = new int[na];
        resolvedChiral = new boolean[na];
        atomGroup = new int[na][];

        for (int n = 0; n < na; n++)
            if (amask[n]) {
                if (atomClass == ATOMCLASS_ECFP)
                    identity[n] = initialIdentityECFP(n);
                else
                    // atomClass==ATOMCLASS_FCFP
                    identity[n] = initialIdentityFCFP(n);
                atomGroup[n] = new int[]{n};
                fplist.add(new FP(identity[n], 0, atomGroup[n]));
            }

        int niter = classType == CLASS_ECFP2 || classType == CLASS_FCFP2 ? 1 : classType == CLASS_ECFP4
                || classType == CLASS_FCFP4 ? 2 : classType == CLASS_ECFP6 || classType == CLASS_FCFP6 ? 3 : 0;

        // iterate outward
        for (int iter = 1; iter <= niter; iter++) {
            final int[] newident = new int[na];
            for (int n = 0; n < na; n++)
                if (amask[n]) newident[n] = circularIterate(iter, n);
            identity = newident;

            for (int n = 0; n < na; n++)
                if (amask[n]) {
                    atomGroup[n] = growAtoms(atomGroup[n]);
                    considerNewFP(new FP(identity[n], iter, atomGroup[n]));
                }
        }
    }

    /**
     * Returns the number of fingerprints generated.
     *
     * @return total number of unique fingerprint hashes generated
     * */
    public int getFPCount() {
        return fplist.size();
    }

    /**
     * Returns the requested fingerprint.
     *
     * @param N index of fingerprint (0-based)
     * @return instance of a fingerprint hash
     * */
    public FP getFP(int N) {
        return fplist.get(N);
    }

    /**
     * Calculates the circular fingerprint for the given {@link IAtomContainer}, and <b>folds</b> the result into a single bitset
     * (see getSize()).
     *
     * @param  mol IAtomContainer for which the fingerprint should be calculated.
     * @return the fingerprint
     */
    @Override
    public IBitFingerprint getBitFingerprint(IAtomContainer mol) throws CDKException {
        calculate(mol);
        final BitSet bits = new BitSet(length);
        for (int n = 0; n < fplist.size(); n++) {
            int i = fplist.get(n).hashCode;
            long b = i >= 0 ? i : ((i & 0x7FFFFFFF) | (1L << 31));
            bits.set((int) (b % length));
        }
        return new BitSetFingerprint(bits);
    }

    /**
     * Calculates the circular fingerprint for the given {@link IAtomContainer}, and returns a datastructure that enumerates all
     * of the fingerprints, and their counts (i.e. does <b>not</b> fold them into a bitmask).
     *
     * @param  mol IAtomContainer for which the fingerprint should be calculated.
     * @return the count fingerprint
     */
    @Override
    public ICountFingerprint getCountFingerprint(IAtomContainer mol) throws CDKException {
        calculate(mol);

        // extract a convenient {hash:count} datastructure
        final Map<Integer, Integer> map = new TreeMap<Integer, Integer>();
        for (FP fp : fplist) {
            if (map.containsKey(fp.hashCode))
                map.put(fp.hashCode, map.get(fp.hashCode) + 1);
            else
                map.put(fp.hashCode, 1);
        }
        final int sz = map.size();
        final int[] hash = new int[sz], count = new int[sz];
        int n = 0;
        for (int h : map.keySet()) {
            hash[n] = h;
            count[n++] = map.get(h);
        }

        // implement a custom instance that provides a window directly into the summary content
        return new ICountFingerprint() {

            @Override
            public long size() {
                return 4294967296l;
            }

            @Override
            public int numOfPopulatedbins() {
                return sz;
            }

            @Override
            public int getCount(int index) {
                return count[index];
            }

            @Override
            public int getHash(int index) {
                return hash[index];
            }

            @Override
            public void merge(ICountFingerprint fp) {}

            @Override
            public void setBehaveAsBitFingerprint(boolean behaveAsBitFingerprint) {}

            @Override
            public boolean hasHash(int hash) {
                return map.containsKey(hash);
            }

            @Override
            public int getCountForHash(int hash) {
                return map.containsKey(hash) ? map.get(hash) : 0;
            }
        };
    }

    /**
     * Invalid: it is not appropriate to convert the integer hash codes into strings.
     */
    @Override
    public Map<String, Integer> getRawFingerprint(IAtomContainer mol) throws CDKException {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the extent of the folded fingerprints.
     *
     * @return the size of the fingerprint
     */
    @Override
    public int getSize() {
        return length;
    }

    // ------------ private methods ------------

    // calculates an integer number that stores the bit-packed identity of the given atom
    private int initialIdentityECFP(int aidx) {
        /*
         * Atom properties from the source reference: (1) number of heavy atom
         * neighbours (2) atom degree: valence minus # hydrogens (3) atomic
         * number (4) atomic mass (5) atom charge (6) number of hydrogen
         * neighbours (7) whether the atom is in a ring
         */

        IAtom atom = mol.getAtom(aidx);

        int nheavy = atomAdj[aidx].length, nhydr = hcount[aidx];
        int atno = atom.getAtomicNumber();

        final int[] ELEMENT_BONDING = {0, 1, 0, 1, 2, 3, 4, 3, 2, 1, 0, 1, 2, 3, 4, 3, 2, 1, 0, 1, 2, 3, 4, 5, 6, 7, 8,
                9, 10, 11, 12, 3, 4, 3, 2, 1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 3, 4, 3, 2, 1, 0, 1, 2, 4, 4,
                4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 3, 4, 5, 6, 7, 8, 1, 1, 4, 4, 4,
                4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};

        int degree = (atno > 0 && atno < ELEMENT_BONDING.length ? ELEMENT_BONDING[atno] : 0) - nhydr;
        int chg = atom.getFormalCharge();
        int inring = ringBlock[aidx] > 0 ? 1 : 0;

        crc.reset();
        crc.update((nheavy << 4) | degree);
        crc.update(atno);
        crc.update(chg + 0x80);
        crc.update((nhydr << 4) | inring);
        return (int) crc.getValue();
    }

    private int initialIdentityFCFP(int aidx) {
        return (maskDon[aidx] ? 0x01 : 0) | (maskAcc[aidx] ? 0x02 : 0) | (maskPos[aidx] ? 0x04 : 0)
                | (maskNeg[aidx] ? 0x08 : 0) | (atomArom[aidx] ? 0x10 : 0) | // strictly bond aromaticity more accurate rendition
                (maskHal[aidx] ? 0x20 : 0);
    }

    // takes the current identity values
    private int circularIterate(int iter, int atom) {
        final int[] adj = atomAdj[atom], adjb = bondAdj[atom];

        // build out a sequence, formulated as
        //     {iteration,original#, adj0-bondorder,adj0-identity, ..., [chiral?]}
        final int[] seq = new int[2 + 2 * adj.length];
        seq[0] = iter;
        seq[1] = identity[atom];
        for (int n = 0; n < adj.length; n++) {
            seq[2 * n + 2] = bondArom[adjb[n]] ? 0xF : bondOrder[adjb[n]];
            seq[2 * n + 3] = identity[adj[n]];
        }

        // now sort the adjacencies by bond order first, then identity second
        int p = 0;
        while (p < adj.length - 1) {
            int i = 2 + 2 * p;
            if (seq[i] > seq[i + 2] || (seq[i] == seq[i + 2] && seq[i + 1] > seq[i + 3])) {
                int sw = seq[i];
                seq[i] = seq[i + 2];
                seq[i + 2] = sw;
                sw = seq[i + 1];
                seq[i + 1] = seq[i + 3];
                seq[i + 3] = sw;
                if (p > 0) p--;
            } else
                p++;
        }

        // roll it up into a hash code
        crc.reset();
        for (int n = 0; n < seq.length; n += 2) {
            crc.update(seq[n]);
            final int v = seq[n + 1];
            crc.update(v >>> 24);
            crc.update((v >> 16) & 0xFF);
            crc.update((v >> 8) & 0xFF);
            crc.update(v & 0xFF);
        }

        // chirality flag: one chance to resolve it
        if (!resolvedChiral[atom] && tetra[atom] != null) {
            final int[] ru = tetra[atom];
            final int[] par = {ru[0] < 0 ? 0 : identity[ru[0]], ru[1] < 0 ? 0 : identity[ru[1]],
                    ru[2] < 0 ? 0 : identity[ru[2]], ru[3] < 0 ? 0 : identity[ru[3]]};
            if (par[0] != par[1] && par[0] != par[2] && par[0] != par[3] && par[1] != par[2] && par[1] != par[3]
                    && par[2] != par[3]) {
                int rp = 0;
                if (par[0] < par[1]) rp++;
                if (par[0] < par[2]) rp++;
                if (par[0] < par[3]) rp++;
                if (par[1] < par[2]) rp++;
                if (par[1] < par[3]) rp++;
                if (par[2] < par[3]) rp++;

                // add 1 or 2 to the end of the list, depending on the parity
                crc.update((rp & 1) + 1);
                resolvedChiral[atom] = true;
            }
        }

        return (int) crc.getValue();
    }

    // takes a set of atom indices and adds all atoms that are adjacent to at least one of them; the resulting list of
    // atom indices is sorted
    private int[] growAtoms(int[] atoms) {
        final int na = mol.getAtomCount();
        boolean[] mask = new boolean[na];
        for (int n = 0; n < atoms.length; n++) {
            mask[atoms[n]] = true;
            int[] adj = atomAdj[atoms[n]];
            for (int i = 0; i < adj.length; i++)
                mask[adj[i]] = true;
        }
        int sz = 0;
        for (int n = 0; n < na; n++)
            if (mask[n]) sz++;
        int[] newList = new int[sz];
        for (int n = na - 1; n >= 0; n--)
            if (mask[n]) newList[--sz] = n;
        return newList;
    }

    // consider adding a new fingerprint: if it's a duplicate with regard to the atom list, either replace the match or
    // discard it
    private void considerNewFP(FP newFP) {
        //wr("CONSIDER:"+newFP.iteration+",hash="+newFP.hashCode); //foo
        int hit = -1;
        FP fp = null;
        for (int n = 0; n < fplist.size(); n++) {
            fp = fplist.get(n);
            boolean equal = fp.atoms.length == newFP.atoms.length;
            for (int i = fp.atoms.length - 1; equal && i >= 0; i--)
                if (fp.atoms[i] != newFP.atoms[i]) equal = false;
            if (equal) {
                hit = n;
                break;
            }
        }
        if (hit < 0) {
            fplist.add(newFP);
            return;
        }

        // if the preexisting fingerprint is from an earlier iteration, or has a lower hashcode, discard
        if (fp.iteration < newFP.iteration || fp.hashCode < newFP.hashCode) return;
        fplist.set(hit, newFP);
    }

    // ------------ molecule analysis: cached cheminformatics ------------

    // summarize preliminary information about the molecular structure, to make sure the rest all goes quickly
    private void excavateMolecule() {
        final int na = mol.getAtomCount(), nb = mol.getBondCount();

        // create the mask of heavy atoms (amask) and the adjacency graphs, index-based, that are used to traverse
        // the heavy part of the graph
        amask = new boolean[na];
        for (int n = 0; n < na; n++)
            amask[n] = mol.getAtom(n).getAtomicNumber() > 1; // true for heavy elements
        atomAdj = new int[na][];
        bondAdj = new int[na][];
        bondOrder = new int[nb];
        hcount = new int[na];
        for (int n = 0; n < mol.getBondCount(); n++) {
            IBond bond = mol.getBond(n);
            if (bond.getAtomCount() != 2) continue;
            int a1 = mol.indexOf(bond.getBegin()), a2 = mol.indexOf(bond.getEnd());
            if (amask[a1] && amask[a2]) {
                atomAdj[a1] = appendInteger(atomAdj[a1], a2);
                bondAdj[a1] = appendInteger(bondAdj[a1], n);
                atomAdj[a2] = appendInteger(atomAdj[a2], a1);
                bondAdj[a2] = appendInteger(bondAdj[a2], n);
                if (bond.getOrder() == IBond.Order.SINGLE)
                    bondOrder[n] = 1;
                else if (bond.getOrder() == IBond.Order.DOUBLE)
                    bondOrder[n] = 2;
                else if (bond.getOrder() == IBond.Order.TRIPLE)
                    bondOrder[n] = 3;
                else if (bond.getOrder() == IBond.Order.QUADRUPLE) bondOrder[n] = 4;
                // (look for zero-order bonds later on)
            } else {
                if (!amask[a1]) hcount[a2]++;
                if (!amask[a2]) hcount[a1]++;
            }
        }
        for (int n = 0; n < na; n++)
            if (amask[n] && atomAdj[n] == null) {
                atomAdj[n] = new int[0];
                bondAdj[n] = atomAdj[n];
            }

        // calculate implicit hydrogens, using a very conservative formula
        final String[] HYVALENCE_EL = {"C", "N", "O", "S", "P"};
        final int[] HYVALENCE_VAL = {4, 3, 2, 2, 3};
        for (int n = 0; n < na; n++) {
            IAtom atom = mol.getAtom(n);
            String el = atom.getSymbol();
            int hy = 0;
            for (int i = 0; i < HYVALENCE_EL.length; i++)
                if (el.equals(HYVALENCE_EL[i])) {
                    hy = HYVALENCE_VAL[i];
                    break;
                }
            if (hy == 0) continue;
            int ch = atom.getFormalCharge();
            if (el.equals("C")) ch = -Math.abs(ch);
            final int unpaired = 0; // (not current available, maybe introduce later)
            hy += ch - unpaired;
            // (needs to include actual H's) for (int i=0;i<bondAdj[n].length;i++) hy-=bondOrder[bondAdj[n][i]];
            for (IBond bond : mol.getConnectedBondsList(atom)) {
                if (bond.getOrder() == IBond.Order.SINGLE)
                    hy -= 1;
                else if (bond.getOrder() == IBond.Order.DOUBLE)
                    hy -= 2;
                else if (bond.getOrder() == IBond.Order.TRIPLE)
                    hy -= 3;
                else if (bond.getOrder() == IBond.Order.QUADRUPLE) hy -= 4;
                // (look for zero-bonds later on)
            }
            hcount[n] += Math.max(0, hy);
        }

        markRingBlocks();

        ArrayList<int[]> rings = new ArrayList<int[]>();
        for (int rsz = 3; rsz <= 7; rsz++) {
            int[] path = new int[rsz];
            for (int n = 0; n < na; n++)
                if (ringBlock[n] > 0) {
                    path[0] = n;
                    recursiveRingFind(path, 1, rsz, ringBlock[n], rings);
                }
        }
        smallRings = rings.toArray(new int[rings.size()][]);

        detectStrictAromaticity();

        tetra = new int[na][];
        if (optPerceiveStereo) {
            for (int n = 0; n < na; n++)
                tetra[n] = rubricTetrahedral(n);
        } else {
            rubricTetrahedralsCdk();
        }
    }

    // assign a ring block ID to each atom (0=not in ring)
    private void markRingBlocks() {
        final int na = mol.getAtomCount();
        ringBlock = new int[na];

        boolean visited[] = new boolean[na];
        for (int n = 0; n < na; n++)
            visited[n] = !amask[n]; // skip hydrogens

        int path[] = new int[na + 1], plen = 0;
        while (true) {
            int last, current;

            if (plen == 0) // find an element of a new component to visit
            {
                last = -1;
                for (current = 0; current < na && visited[current]; current++) {
                }
                if (current >= na) break;
            } else {
                last = path[plen - 1];
                current = -1;
                for (int n = 0; n < atomAdj[last].length; n++)
                    if (!visited[atomAdj[last][n]]) {
                        current = atomAdj[last][n];
                        break;
                    }
            }

            if (current >= 0 && plen >= 2) // path is at least 2 items long, so look for any not-previous visited neighbours
            {
                int back = path[plen - 1];
                for (int n = 0; n < atomAdj[current].length; n++) {
                    int join = atomAdj[current][n];
                    if (join != back && visited[join]) {
                        path[plen] = current;
                        for (int i = plen; i == plen || path[i + 1] != join; i--) {
                            int id = ringBlock[path[i]];
                            if (id == 0)
                                ringBlock[path[i]] = last;
                            else if (id != last) {
                                for (int j = 0; j < na; j++)
                                    if (ringBlock[j] == id) ringBlock[j] = last;
                            }
                        }
                    }
                }
            }
            if (current >= 0) // can mark the new one as visited
            {
                visited[current] = true;
                path[plen++] = current;
            } else // otherwise, found nothing and must rewind the path
            {
                plen--;
            }
        }

        // the ring ID's are not necessarily consecutive, so reassign them to 0=none, 1..NBlocks
        int nextID = 0;
        for (int i = 0; i < na; i++)
            if (ringBlock[i] > 0) {
                nextID--;
                for (int j = na - 1; j >= i; j--)
                    if (ringBlock[j] == ringBlock[i]) ringBlock[j] = nextID;
            }
        for (int i = 0; i < na; i++)
            ringBlock[i] = -ringBlock[i];
    }

    // hunt for ring recursively: start with a partially defined path, and go exploring
    private void recursiveRingFind(int[] path, int psize, int capacity, int rblk, ArrayList<int[]> rings) {
        // not enough atoms yet, so look for new possibilities
        if (psize < capacity) {
            int last = path[psize - 1];
            for (int n = 0; n < atomAdj[last].length; n++) {
                int adj = atomAdj[last][n];
                if (ringBlock[adj] != rblk) continue;
                boolean fnd = false;
                for (int i = 0; i < psize; i++)
                    if (path[i] == adj) {
                        fnd = true;
                        break;
                    }
                if (!fnd) {
                    int newPath[] = new int[capacity];
                    for (int i = 0; i < psize; i++)
                        newPath[i] = path[i];
                    newPath[psize] = adj;
                    recursiveRingFind(newPath, psize + 1, capacity, rblk, rings);
                }
            }
            return;
        }

        // path is full, so make sure it eats its tail
        int last = path[psize - 1];
        boolean fnd = false;
        for (int n = 0; n < atomAdj[last].length; n++)
            if (atomAdj[last][n] == path[0]) {
                fnd = true;
                break;
            }
        if (!fnd) return;

        // make sure every element in the path has exactly 2 neighbours within the path; otherwise it is spanning a bridge, which
        // is an undesirable ring definition
        for (int n = 0; n < path.length; n++) {
            int count = 0, p = path[n];
            for (int i = 0; i < atomAdj[p].length; i++)
                for (int j = 0; j < path.length; j++)
                    if (atomAdj[p][i] == path[j]) {
                        count++;
                        break;
                    }
            if (count != 2) return; // invalid
        }

        // reorder the array (there are 2N possible ordered permutations) then look for duplicates
        int first = 0;
        for (int n = 1; n < psize; n++)
            if (path[n] < path[first]) first = n;
        int fm = (first - 1 + psize) % psize, fp = (first + 1) % psize;
        boolean flip = path[fm] < path[fp];
        if (first != 0 || flip) {
            int newPath[] = new int[psize];
            for (int n = 0; n < psize; n++)
                newPath[n] = path[(first + (flip ? psize - n : n)) % psize];
            path = newPath;
        }

        for (int n = 0; n < rings.size(); n++) {
            int[] look = rings.get(n);
            boolean same = true;
            for (int i = 0; i < psize; i++)
                if (look[i] != path[i]) {
                    same = false;
                    break;
                }
            if (same) return;
        }

        rings.add(path);
    }

    // aromaticity detection: uses a very narrowly defined algorithm, which detects 6-membered rings with alternating double bonds;
    // rings that are chained together (e.g. anthracene) will also be detected by the extended followup; note that this will NOT mark
    // rings such as thiophene, imidazolium, porphyrins, etc.: these systems will be left in their original single/double bond form
    private void detectStrictAromaticity() {
        final int na = mol.getAtomCount(), nb = mol.getBondCount();
        atomArom = new boolean[na];
        bondArom = new boolean[nb];

        if (smallRings.length == 0) return;

        boolean[] piAtom = new boolean[na];
        for (int n = 0; n < nb; n++)
            if (bondOrder[n] == 2) {
                IBond bond = mol.getBond(n);
                piAtom[mol.indexOf(bond.getBegin())] = true;
                piAtom[mol.indexOf(bond.getEnd())] = true;
            }

        ArrayList<int[]> maybe = new ArrayList<int[]>(); // rings which may yet be aromatic
        for (int[] r : smallRings)
            if (r.length == 6) {
                boolean consider = true;
                for (int n = 0; n < 6; n++) {
                    final int a = r[n];
                    if (!piAtom[a]) {
                        consider = false;
                        break;
                    }
                    int b = findBond(a, r[n == 5 ? 0 : n + 1]);
                    if (bondOrder[b] != 1 && bondOrder[b] != 2) {
                        consider = false;
                        break;
                    }
                }
                if (consider) maybe.add(r);
            }

        // keep classifying rings as aromatic until no change; this needs to be done iteratively, for the benefit of highly
        // embedded ring systems, that can't be classified as aromatic until it is known that their neighbours obviously are
        while (true) {
            boolean anyChange = false;

            for (int n = maybe.size() - 1; n >= 0; n--) {
                int[] r = maybe.get(n);
                boolean phase1 = true, phase2 = true; // has to go 121212 or 212121; already arom=either is OK
                for (int i = 0; i < 6; i++) {
                    int b = findBond(r[i], r[i == 5 ? 0 : i + 1]);
                    if (bondArom[b]) continue; // valid for either phase
                    phase1 = phase1 && bondOrder[b] == (2 - (i & 1));
                    phase2 = phase2 && bondOrder[b] == (1 + (i & 1));
                }
                if (!phase1 && !phase2) continue;

                // the ring is deemed aromatic: mark the flags and remove from the maybe list
                for (int i = 0; i < r.length; i++) {
                    atomArom[r[i]] = true;
                    bondArom[findBond(r[i], r[i == 5 ? 0 : i + 1])] = true;
                }
                maybe.remove(n);
                anyChange = true;
            }

            if (!anyChange) break;
        }
    }

    // tetrahedral 'rubric': for any sp3 atom that has stereo defined
    // in the CDK's object model.
    private void rubricTetrahedralsCdk() {
        for (IStereoElement se : mol.stereoElements()) {
            if (se.getConfigClass() == IStereoElement.Tetrahedral) {
                @SuppressWarnings("unchecked") final IStereoElement<IAtom, IAtom> th =
                    (IStereoElement<IAtom, IAtom>) se;
                final IAtom focus = th.getFocus();
                final List<IAtom> carriers = th.getCarriers();
                int[]             adj      = new int[4];

                for (int i = 0; i < 4; i++) {
                    if (focus.equals(carriers.get(i)))
                        adj[i] = -1; // impl H
                    else
                        adj[i] = mol.indexOf(carriers.get(i));
                }
                switch (th.getConfigOrder()) {
                    case IStereoElement.LEFT:
                        int i = adj[2];
                        adj[2] = adj[3];
                        adj[3] = i;
                        tetra[mol.indexOf(focus)] = adj;
                        break;
                    case IStereoElement.RIGHT:
                        tetra[mol.indexOf(focus)] = adj;
                        break;
                    default:
                }
            }
        }
    }

    // tetrahedral 'rubric': for any sp3 atom that has enough neighbours and appropriate wedge bond/3D geometry information,
    // build up a list of neighbours in a certain permutation order; the resulting array of size 4 can have a total of
    // 24 permutations; there are two groups of 12 that can be mapped onto each other by tetrahedral rotations, hence this
    // is a partioning technique for chirality; it can be thought of as all but the last step of determination of chiral
    // parity, except that the raw information is required for the circular fingerprint chirality resolution; note that this
    // does not consider the possibility of lone-pair chirality (e.g. sp3 phosphorus)
    private int[] rubricTetrahedral(int aidx) {
        if (hcount[aidx] > 1) return null;

        // make sure the atom has an acceptable environment
        IAtom atom = mol.getAtom(aidx);
        final int[] ELEMENT_BLOCKS = {0, 1, 2, 1, 1, 2, 2, 2, 2, 2, 2, 1, 1, 2, 2, 2, 2, 2, 2, 1, 1, 3, 3, 3, 3, 3, 3,
                3, 3, 3, 3, 2, 2, 2, 2, 2, 2, 1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 2, 2, 1, 1, 4, 4, 4, 4,
                4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 2, 2, 1, 1, 4, 4, 4, 4, 4, 4,
                4, 4, 4, 4, 4, 4, 4, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3};
        final int atno = atom.getAtomicNumber();
        if (atno <= 1 || atno >= ELEMENT_BLOCKS.length) return null;
        if (ELEMENT_BLOCKS[atno] != 2 /* p-block */) return null;

        final int adjc = atomAdj[aidx].length, hc = hcount[aidx];
        if (!(adjc == 3 && hc == 1) && !(adjc == 4 && hc == 0)) return null;

        // must have 3D coordinates or a wedge bond to qualify
        boolean wedgeOr3D = false;
        Point3d a3d = atom.getPoint3d();
        for (int n = 0; n < adjc; n++) {
            IBond.Stereo stereo = mol.getBond(bondAdj[aidx][n]).getStereo();
            if (stereo == IBond.Stereo.UP || stereo == IBond.Stereo.DOWN) {
                wedgeOr3D = true;
                break;
            }
            if (stereo == IBond.Stereo.UP_OR_DOWN) return null; // squiggly line: definitely not
            Point3d o3d = atom.getPoint3d();
            if (a3d != null && o3d != null && a3d.z != o3d.z) {
                wedgeOr3D = true;
                break;
            }
        }
        if (!wedgeOr3D) return null;

        // fill in existing positions, including "fake" Z coordinate if wedges are being used
        Point2d a2d = atom.getPoint2d();

        // for safety in case the bond type (bond stereo) is set but no coords are
        if (a2d == null && a3d == null) return null;

        final float x0 = a3d != null ? (float) a3d.x : (float) a2d.x;
        final float y0 = a3d != null ? (float) a3d.y : (float) a2d.y;
        final float z0 = a3d != null ? (float) a3d.z : 0;
        final float[] xp = new float[]{0, 0, 0, 0};
        final float[] yp = new float[]{0, 0, 0, 0};
        final float[] zp = new float[]{0, 0, 0, 0};
        int numShort = 0;
        for (int n = 0; n < adjc; n++) {
            IAtom other = mol.getAtom(atomAdj[aidx][n]);
            IBond bond = mol.getBond(bondAdj[aidx][n]);
            Point3d o3d = other.getPoint3d();
            Point2d o2d = other.getPoint2d();
            if (o3d != null) {
                xp[n] = (float) (o3d.x - x0);
                yp[n] = (float) (o3d.y - y0);
                zp[n] = (float) (o3d.z - z0);
            } else if (o2d != null) {
                IBond.Stereo stereo = bond.getStereo();
                xp[n] = (float) (o2d.x - x0);
                yp[n] = (float) (o2d.y - y0);
                zp[n] = other.equals(bond.getBegin()) ? 0 : stereo == IBond.Stereo.UP ? 1 : stereo == IBond.Stereo.DOWN ? -1
                                                                                                                   : 0;
            } else {
                return null; // no 2D coordinates on some atom
            }

            final float dx = xp[n] - x0, dy = yp[n] - y0, dz = zp[n] - z0;
            final float dsq = dx * dx + dy * dy + dz * dz;
            if (dsq < 0.01f * 0.01f) {
                numShort++;
                if (numShort > 1) return null; // second one's the dealbreaker
            }
        }

        // build an implicit H if necessary
        int[] adj = atomAdj[aidx];
        if (adjc == 3) {
            adj = appendInteger(adj, -1);
            xp[3] = 0;
            yp[3] = 0;
            zp[3] = 0;
        }

        // make the call on permutational parity
        float one = 0, two = 0;
        for (int i = 1; i <= 6; i++) {
            int a = 0, b = 0;
            if (i == 1) {
                a = 1;
                b = 2;
            } else if (i == 2) {
                a = 2;
                b = 3;
            } else if (i == 3) {
                a = 3;
                b = 1;
            } else if (i == 4) {
                a = 2;
                b = 1;
            } else if (i == 5) {
                a = 3;
                b = 2;
            } else if (i == 6) {
                a = 1;
                b = 3;
            }
            float xx = yp[a] * zp[b] - yp[b] * zp[a] - xp[0];
            float yy = zp[a] * xp[b] - zp[b] * xp[a] - yp[0];
            float zz = xp[a] * yp[b] - xp[b] * yp[a] - zp[0];
            if (i <= 3)
                one += xx * xx + yy * yy + zz * zz;
            else
                two += xx * xx + yy * yy + zz * zz;
        }

        if (two > one) {
            int i = adj[2];
            adj[2] = adj[3];
            adj[3] = i;
        }
        return adj;
    }

    // biotypes: when generating FCFP-type descriptors, atoms are initially labelled according to their functional
    // capabilities, that being defined by centers of biological interactions, such as hydrogen bonding and electrostatics
    private void calculateBioTypes() {
        final int na = mol.getAtomCount(), nb = mol.getBondCount();

        maskDon = new boolean[na];
        maskAcc = new boolean[na];
        maskPos = new boolean[na];
        maskNeg = new boolean[na];
        maskAro = new boolean[na];
        maskHal = new boolean[na];

        aliphatic = new boolean[na];
        bondSum = new int[na];
        for (int n = 0; n < na; n++)
            if (amask[n]) {
                aliphatic[n] = mol.getAtom(n).getSymbol().equals("C");
                bondSum[n] = hcount[n];
            }

        hasDouble = new boolean[na];
        isOxide = new boolean[na];
        for (int n = 0; n < nb; n++) {
            IBond bond = mol.getBond(n);
            if (bond.getAtomCount() != 2) continue;
            int a1 = mol.indexOf(bond.getBegin()), a2 = mol.indexOf(bond.getEnd()), o = bondOrder[n];
            if (!amask[a1] || !amask[a2]) continue;
            bondSum[a1] += o;
            bondSum[a2] += o;
            if (o == 2) {
                hasDouble[a1] = true;
                hasDouble[a2] = true;
                if (mol.getAtom(a1).getSymbol().equals("O")) isOxide[a2] = true;
                if (mol.getAtom(a2).getSymbol().equals("O")) isOxide[a1] = true;
            }
            if (o != 1) {
                aliphatic[a1] = false;
                aliphatic[a2] = false;
            }
        }

        lonePair = new boolean[na];
        for (int n = 0; n < na; n++) {
            IAtom atom = mol.getAtom(n);
            String el = atom.getSymbol();
            int valence = el.equals("N") ? 3 : el.equals("O") || el.equals("S") ? 2 : 0;
            if (valence > 0 && bondSum[n] + atom.getFormalCharge() <= valence) lonePair[n] = true;
        }

        // preprocess small rings
        tetrazole = new boolean[na];
        for (int[] r : smallRings)
            if (r.length >= 5 && r.length <= 7) {
                considerBioTypeAromaticity(r);
                if (r.length == 5) considerBioTypeTetrazole(r);
            }

        // calculate each remaining property
        for (int n = 0; n < na; n++)
            if (amask[n]) {
                maskDon[n] = determineDonor(n);
                maskAcc[n] = determineAcceptor(n);
                maskPos[n] = determinePositive(n);
                maskNeg[n] = determineNegative(n);
                maskHal[n] = determineHalide(n);
            }
    }

    // if the given ring is aromatic, mark the atoms accordingly: note that this "biotype" definition of aromaticity is
    // different to the one used in the rest of this class: any ring of size 5 to 7 that has a lone pair or pi bond on every
    // atom is labelled as aromatic, because the concept required is physical behaviour, i.e. ring current and effect on
    // neighbouring functional groups, rather than disambiguating conjugational equivalence
    private void considerBioTypeAromaticity(final int[] ring) {
        final int rsz = ring.length;
        int countDouble = 0;
        for (int n = 0; n < rsz; n++) {
            final int a = ring[n];
            if (hasDouble[a]) {
                countDouble++;
                continue;
            }
            if (!lonePair[a]) return;
        }
        if (countDouble < rsz - 2) return;
        for (int n = 0; n < rsz; n++)
            maskAro[ring[n]] = true;
    }

    // if the given ring is a tetrazole, mark the aroms accordingly; must be ring size length 5; it's possible to fool the
    // tetrazole test with a non-sane/invalid molecule
    private void considerBioTypeTetrazole(final int[] ring) {
        int countC = 0, countN = 0, ndbl = 0;
        for (int n = 0; n < 5; n++) {
            IAtom atom = mol.getAtom(ring[n]);
            if (atom.getFormalCharge() != 0) return;
            String el = atom.getSymbol();
            if (el.equals("C"))
                countC++;
            else if (el.equals("N")) countN++;
            if (bondOrder[findBond(ring[n], ring[n == 4 ? 0 : n + 1])] == 2) ndbl++;
        }
        if (countC != 1 || countN != 4 || ndbl != 2) return;
        for (int n = 0; n < 5; n++)
            if (mol.getAtom(ring[n]).getSymbol().equals("N")) tetrazole[ring[n]] = true;
    }

    // hydrogen bond donor
    private boolean determineDonor(int aidx) {
        // must have a hydrogen atom, either implicit or explicit
        if (hcount[aidx] == 0) return false;

        IAtom atom = mol.getAtom(aidx);
        final String el = atom.getSymbol();
        if (el.equals("N") || el.equals("O")) {
            // tetrazoles do not donate
            if (tetrazole[aidx]) return false;

            // see if any of the neighbours is an oxide of some sort; this is grounds for disqualification, with the exception
            // of amides, which are consider nonacidic
            for (int n = 0; n < atomAdj[aidx].length; n++)
                if (isOxide[atomAdj[aidx][n]]) {
                    if (!mol.getAtom(atomAdj[aidx][n]).getSymbol().equals("C") || !el.equals("N")) return false;
                }
            return true;
        } else if (el.equals("S")) {
            // any kind of adjacent double bond disqualifies -SH
            for (int n = 0; n < atomAdj[aidx].length; n++)
                if (hasDouble[atomAdj[aidx][n]]) return false;
            return true;
        } else if (el.equals("C")) {
            // terminal alkynes qualify
            for (int n = 0; n < bondAdj[aidx].length; n++)
                if (bondOrderBioType(bondAdj[aidx][n]) == 3) return true;
            return false;
        }

        return false;
    }

    // hydrogen bond acceptor
    private boolean determineAcceptor(int aidx) {
        IAtom atom = mol.getAtom(aidx);

        // must have an N,O,S lonepair and nonpositive charge for starters
        if (!lonePair[aidx] || mol.getAtom(aidx).getFormalCharge() > 0) return false;

        // basic nitrogens do not qualify
        if (atom.getSymbol().equals("N")) {
            boolean basic = true;
            for (int n = 0; n < atomAdj[aidx].length; n++)
                if (!aliphatic[atomAdj[aidx][n]]) {
                    basic = false;
                    break;
                }
            if (basic) return false;
        }

        return true;
    }

    // positive charge centre
    private boolean determinePositive(int aidx) {
        IAtom atom = mol.getAtom(aidx);

        // consider formal ionic charge first
        final int chg = atom.getFormalCharge();
        if (chg < 0) return false;
        if (chg > 0) {
            for (int n = 0; n < atomAdj[aidx].length; n++)
                if (mol.getAtom(atomAdj[aidx][n]).getFormalCharge() < 0) return false;
            return true;
        }
        final String el = atom.getSymbol();

        if (el.equals("N")) {
            // basic amines, i.e. aliphatic neighbours
            boolean basic = true;
            for (int n = 0; n < atomAdj[aidx].length; n++)
                if (!aliphatic[atomAdj[aidx][n]]) {
                    basic = false;
                    break;
                }
            if (basic) return true;

            // imines with N=C-N motif: the carbon atom must be bonded to at least one amine, and both other substituents
            // have to be without double bonds, i.e. R-N=C(R)NR2 or R-N=C(NR2)NR2 (R=not hydrogen)
            if (hasDouble[aidx] && hcount[aidx] == 0) {
                int other = -1;
                for (int n = 0; n < atomAdj[aidx].length; n++)
                    if (bondOrderBioType(bondAdj[aidx][n]) == 2) {
                        other = atomAdj[aidx][n];
                        break;
                    }
                if (other >= 0) {
                    int amines = 0;
                    for (int n = 0; n < atomAdj[other].length; n++) {
                        final int a = atomAdj[other][n];
                        if (a == aidx) continue;
                        if (hasDouble[a]) {
                            amines = 0;
                            break;
                        }
                        final String ael = mol.getAtom(a).getSymbol();
                        if (ael.equals("N")) {
                            if (hcount[a] > 0) {
                                amines = 0;
                                break;
                            }
                            amines++;
                        } else if (!ael.equals("C")) {
                            amines = 0;
                            break;
                        }
                    }
                    if (amines > 0) return true;
                }
            }
        } else if (el.equals("C")) {
            // carbon-centred charge if imine & H-containing amine present, i.e. =NR and -N[H]R both
            boolean imine = false, amine = false;
            for (int n = 0; n < atomAdj[aidx].length; n++) {
                final int a = atomAdj[aidx][n];
                if (tetrazole[a]) {
                    imine = false;
                    amine = false;
                    break;
                }
                if (!mol.getAtom(a).getSymbol().equals("N")) continue;
                if (bondOrderBioType(bondAdj[aidx][n]) == 2)
                    imine = true;
                else if (hcount[a] == 1) amine = true;
            }
            if (imine && amine) return true;
        }

        return false;
    }

    // negative charge centre
    private boolean determineNegative(int aidx) {
        IAtom atom = mol.getAtom(aidx);

        // consider formal ionic charge first
        final int chg = atom.getFormalCharge();
        if (chg > 0) return false;
        if (chg < 0) {
            for (int n = 0; n < atomAdj[aidx].length; n++)
                if (mol.getAtom(atomAdj[aidx][n]).getFormalCharge() > 0) return false;
            return true;
        }

        final String el = atom.getSymbol();

        // tetrazole nitrogens get negative charges
        if (tetrazole[aidx] && el.equals("N")) return true;

        // centres with an oxide and an -OH group qualify as negative
        if (isOxide[aidx] && (el.equals("C") || el.equals("S") || el.equals("P"))) {
            for (int n = 0; n < atomAdj[aidx].length; n++)
                if (bondOrderBioType(bondAdj[aidx][n]) == 1) {
                    final int a = atomAdj[aidx][n];
                    if (mol.getAtom(a).getSymbol().equals("O") && hcount[a] > 0) return true;
                }
        }

        return false;
    }

    // halide
    private boolean determineHalide(int aidx) {
        final String el = mol.getAtom(aidx).getSymbol();
        return el.equals("F") || el.equals("Cl") || el.equals("Br") || el.equals("I");
    }

    // returns either the bond order in the molecule, or -1 if the atoms are both labelled as aromatic
    private int bondOrderBioType(int bidx) {
        IBond bond = mol.getBond(bidx);
        if (bond.getAtomCount() != 2) return 0;
        final int a1 = mol.indexOf(bond.getBegin()), a2 = mol.indexOf(bond.getEnd());
        if (maskAro[a1] && maskAro[a2]) return -1;
        return bondOrder[bidx];
    }

    // convenience: appending to an int array
    private int[] appendInteger(int[] a, int v) {
        if (a == null || a.length == 0) return new int[]{v};
        int[] b = new int[a.length + 1];
        for (int n = a.length - 1; n >= 0; n--)
            b[n] = a[n];
        b[a.length] = v;
        return b;
    }

    // convenience: scans the atom adjacency to grab the bond index
    private int findBond(int a1, int a2) {
        for (int n = atomAdj[a1].length - 1; n >= 0; n--)
            if (atomAdj[a1][n] == a2) return bondAdj[a1][n];
        return -1;
    }
    
    /*
     * for debugging convenience: revive if necessary private void wr(String
     * str) {System.out.println(str);} private String arrayStr(int[] val) { if
     * (val==null) return "null"; if (val.length==0) return "{}"; String
     * str=String.valueOf(val[0]); for (int n=1;n<val.length;n++)
     * str+=","+val[n]; return "{"+str+"}"; } private String arrayStr(float[]
     * val) { if (val==null) return "null"; if (val.length==0) return "{}";
     * String str=String.valueOf(val[0]); for (int n=1;n<val.length;n++)
     * str+=","+val[n]; return "{"+str+"}"; } private String arrayStr(boolean[]
     * val) { if (val==null) return "null"; if (val.length==0) return "{}";
     * String str=""; for (int n=0;n<val.length;n++) str+=val[n] ? "1" : "0";
     * return "{"+str+"}"; }
     */
}
