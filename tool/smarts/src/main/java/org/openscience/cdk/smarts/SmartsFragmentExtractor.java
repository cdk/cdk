/*
 * Copyright (c) 2016 John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.smarts;

import java.util.Arrays;
import java.util.Locale;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

/**
 * Utility class to create SMARTS that match part (substructure) of a molecule. SMARTS are generated
 * by providing the atom indexes. An example use cases is encoding features from a fingerprint.
 *
 * <p>The extractor has two modes. {@link #MODE_EXACT} (default) captures the element, valence,
 * hydrogen count, connectivity, and charge in the SMARTS atom expressions. The alternative mode,
 * {@link #MODE_JCOMPOUNDMAPPER}, only captures the element, non-zero charge, and peripheral bonds.
 * Although the later looks cleaner, the peripheral bonds intend to capture the connectivity of the
 * terminal atoms but since the valence is not bounded further substitution is still allowed. This
 * mirrors functionality from jCompoundMapper {@cdk.cite Hinselmann2011}.
 *
 * <p>The difference is easily demonstrated for methyl. Consider the compound of 2-methylpentane
 * {@code CC(C)CCC}, if we extract one of the methyl atoms depending on the mode we obtain {@code
 * [CH3v4X4+0]} or {@code C*}. The first of these patterns (obtained by {@link #MODE_EXACT}) matches
 * the compound in <b>three places</b> (the three methyl groups). The second matches <b>six</b>
 * times (every atom) because the substituion on the carbon is not locked. A further complication is
 * introduced by the inclusion of the peripheral atoms, for 1H-indole {@code [nH]1ccc2c1cccc2} we
 * can obtain the SMARTS {@code n(ccc(a)a)a} that doesn't match at all. This is because one of the
 * aromatic atoms ('a') needs to match the nitrogen.
 *
 * <p><b>Basic Usage:</b>
 *
 * <pre>{@code
 * IChemObjectBuilder      bldr      = SilentChemObjectBuilder.getInstance();
 * SmilesParser            smipar    = new SmilesParser(bldr);
 *
 * IAtomContainer          mol       = smipar.parseSmiles("[nH]1ccc2c1cccc2");
 * SmartsFragmentExtractor subsmarts = new SmartsFragmentExtractor(mol);
 *
 * // smarts=[nH1v3X3+0][cH1v4X3+0][cH1v4X3+0][cH0v4X3+0]
 * // hits  =1
 * String             smarts    = mol.generate(new int[]{0,1,3,4});
 *
 * subsmarts.setMode(MODE_JCOMPOUNDMAPPER);
 * // smarts=n(ccc(a)a)a
 * // hits  = 0 - one of the 'a' atoms needs to match the nitrogen
 * String             smarts    = mol.generate(new int[]{0,1,3,4});
 * }</pre>
 *
 * @author Nikolay Kochev
 * @author Nina Jeliazkova
 * @author John May
 */
public final class SmartsFragmentExtractor {

    /** Sets the mode of the extractor to produce SMARTS similar to JCompoundMapper. */
    public static final int MODE_JCOMPOUNDMAPPER = 1;

    /** Sets the mode of the extractor to produce exact SMARTS. */
    public static final int MODE_EXACT = 2;

    // molecule being selected over
    private final IAtomContainer mol;

    // fast-access mol graph data structures
    private final int[][] atomAdj, bondAdj;
    private final int[] deg;

    // SMARTS atom and bond expressions
    private final String[] aexpr;
    private final String[] bexpr;

    // SMARTS traversal/generation
    private final int[] avisit;
    private final int[] rbnds;
    private final int[] rnums;
    private int numVisit;

    // which mode should SMARTS be encoded in
    private int mode = MODE_EXACT;

    /**
     * Create a new instance over the provided molecule.
     *
     * @param mol molecule
     */
    public SmartsFragmentExtractor(IAtomContainer mol) {
        this.mol = mol;

        final int numAtoms = mol.getAtomCount();
        final int numBonds = mol.getBondCount();

        // build fast access
        this.deg = new int[numAtoms];
        this.atomAdj = new int[numAtoms][4];
        this.bondAdj = new int[numAtoms][4];
        this.aexpr = new String[numAtoms];
        this.bexpr = new String[numBonds];
        this.avisit = new int[numAtoms];
        this.rbnds = new int[numBonds];
        this.rnums = new int[100]; // max 99 in SMILES/SMARTS

        // index adjacency information and bond expressions for quick
        // reference and traversal
        for (int bondIdx = 0; bondIdx < numBonds; bondIdx++) {
            IBond bond = mol.getBond(bondIdx);
            IAtom beg = bond.getBegin();
            IAtom end = bond.getEnd();
            int begIdx = mol.indexOf(beg);
            int endIdx = mol.indexOf(end);
            this.bexpr[bondIdx] = encodeBondExpr(bondIdx, begIdx, endIdx);

            // make sufficient space
            if (deg[begIdx] == atomAdj[begIdx].length) {
                atomAdj[begIdx] = Arrays.copyOf(atomAdj[begIdx], deg[begIdx] + 2);
                bondAdj[begIdx] = Arrays.copyOf(bondAdj[begIdx], deg[begIdx] + 2);
            }
            if (deg[endIdx] == atomAdj[endIdx].length) {
                atomAdj[endIdx] = Arrays.copyOf(atomAdj[endIdx], deg[endIdx] + 2);
                bondAdj[endIdx] = Arrays.copyOf(bondAdj[endIdx], deg[endIdx] + 2);
            }

            atomAdj[begIdx][deg[begIdx]] = endIdx;
            bondAdj[begIdx][deg[begIdx]] = bondIdx;
            atomAdj[endIdx][deg[endIdx]] = begIdx;
            bondAdj[endIdx][deg[endIdx]] = bondIdx;

            deg[begIdx]++;
            deg[endIdx]++;
        }

        // pre-generate atom expressions
        for (int atomIdx = 0; atomIdx < numAtoms; atomIdx++)
            this.aexpr[atomIdx] = encodeAtomExpr(atomIdx);
    }

    /**
     * Set the mode of SMARTS substructure selection
     *
     * @param mode the mode
     */
    public void setMode(int mode) {
        // check arg
        switch (mode) {
            case MODE_EXACT:
            case MODE_JCOMPOUNDMAPPER:
                break;
            default:
                throw new IllegalArgumentException("Invalid mode specified!");
        }
        this.mode = mode;

        // re-gen atom expressions
        int numAtoms = mol.getAtomCount();
        for (int atomIdx = 0; atomIdx < numAtoms; atomIdx++)
            this.aexpr[atomIdx] = encodeAtomExpr(atomIdx);
    }

    /**
     * Generate a SMARTS for the substructure formed of the provided atoms.
     *
     * @param atomIdxs atom indexes
     * @return SMARTS, null if an empty array is passed
     */
    public String generate(int[] atomIdxs) {

        if (atomIdxs == null) throw new NullPointerException("No atom indexes provided");
        if (atomIdxs.length == 0) return null; // makes sense?

        // special case
        if (atomIdxs.length == 1 && mode == MODE_EXACT) return aexpr[atomIdxs[0]];

        // initialize traversal information
        Arrays.fill(rbnds, 0);
        Arrays.fill(avisit, 0);
        for (int atmIdx : atomIdxs) avisit[atmIdx] = -1;

        // first visit marks ring information
        numVisit = 1;
        for (int atomIdx : atomIdxs) {
            if (avisit[atomIdx] < 0) markRings(atomIdx, -1);
        }

        // reset visit flags and generate
        numVisit = 1;
        for (int atmIdx : atomIdxs) avisit[atmIdx] = -1;

        // second pass builds the expression
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < atomIdxs.length; i++) {
            if (avisit[atomIdxs[i]] < 0) {
                if (i > 0) sb.append('.');
                encodeExpr(atomIdxs[i], -1, sb);
            }
        }

        return sb.toString();
    }

    /**
     * Recursively marks ring closures (back edges) in the {@link #rbnds} array in a depth first
     * order.
     *
     * @param idx atom index
     * @param bprev previous bond
     */
    private void markRings(int idx, int bprev) {
        avisit[idx] = numVisit++;
        final int d = deg[idx];
        for (int j = 0; j < d; j++) {
            int nbr = atomAdj[idx][j];
            int bidx = bondAdj[idx][j];
            if (avisit[nbr] == 0 || bidx == bprev) continue; // ignored
            else if (avisit[nbr] < 0) markRings(nbr, bidx);
            else if (avisit[nbr] < avisit[idx]) rbnds[bidx] = -1; // ring closure
        }
    }

    /**
     * Recursively encodes a SMARTS expression into the provides string builder.
     *
     * @param idx atom index
     * @param bprev previous bond
     * @param sb destition to write SMARTS to
     */
    private void encodeExpr(int idx, int bprev, StringBuilder sb) {
        avisit[idx] = numVisit++;
        sb.append(aexpr[idx]);
        final int d = deg[idx];

        int remain = d;
        for (int j = 0; j < d; j++) {
            int nbr = atomAdj[idx][j];
            int bidx = bondAdj[idx][j];

            // ring open/close
            if (rbnds[bidx] < 0) {
                // open
                final int rnum = chooseRingNumber();
                if (rnum > 9) sb.append('%');
                sb.append(rnum);
                rbnds[bidx] = rnum;
            } else if (rbnds[bidx] > 0) {
                // close
                final int rnum = rbnds[bidx];
                releaseRingNumber(rnum);
                if (rnum > 9) sb.append('%');
                sb.append(rnum);
            }

            if (mode == MODE_EXACT && avisit[nbr] == 0 || bidx == bprev || rbnds[bidx] != 0)
                remain--;
        }

        for (int j = 0; j < d; j++) {
            int nbr = atomAdj[idx][j];
            int bidx = bondAdj[idx][j];
            if (mode == MODE_EXACT && avisit[nbr] == 0 || bidx == bprev || rbnds[bidx] != 0)
                continue; // ignored
            remain--;
            if (avisit[nbr] == 0) {
                // peripheral bond
                if (remain > 0) sb.append('(');
                sb.append(bexpr[bidx]);
                sb.append(mol.getAtom(nbr).isAromatic() ? 'a' : '*');
                if (remain > 0) sb.append(')');
            } else {
                if (remain > 0) sb.append('(');
                sb.append(bexpr[bidx]);
                encodeExpr(nbr, bidx, sb);
                if (remain > 0) sb.append(')');
            }
        }
    }

    /**
     * Select the lowest ring number for use in SMARTS.
     *
     * @return ring number
     * @throws IllegalStateException all ring numbers are used
     */
    private int chooseRingNumber() {
        for (int i = 1; i < rnums.length; i++) {
            if (rnums[i] == 0) {
                rnums[i] = 1;
                return i;
            }
        }
        throw new IllegalStateException("No more ring numbers available!");
    }

    /**
     * Releases a ring number allowing it to be reused.
     *
     * @param rnum ring number
     */
    private void releaseRingNumber(int rnum) {
        rnums[rnum] = 0;
    }

    /**
     * Encodes the atom at index (atmIdx) to a SMARTS expression that matches itself.
     *
     * @param atmIdx atom index
     * @return SMARTS atom expression
     */
    private String encodeAtomExpr(int atmIdx) {
        final IAtom atom = mol.getAtom(atmIdx);

        boolean complex = mode == MODE_EXACT;

        StringBuilder sb = new StringBuilder();

        switch (atom.getAtomicNumber()) {
            case 0: // *
                sb.append('*');
                break;
            case 5: // B
            case 6: // C
            case 7: // N
            case 8: // O
            case 15: // P
            case 16: // S
            case 9: // F
            case 17: // Cl
            case 35: // Br
            case 53: // I
                sb.append(
                        atom.isAromatic()
                                ? atom.getSymbol().toLowerCase(Locale.ROOT)
                                : atom.getSymbol());
                break;
            default:
                complex = true;
                sb.append(
                        atom.isAromatic()
                                ? atom.getSymbol().toLowerCase(Locale.ROOT)
                                : atom.getSymbol());
                break;
        }

        if (mode == MODE_EXACT) {

            int hcount = atom.getImplicitHydrogenCount();
            int valence = hcount;
            int connections = hcount;

            int atmDeg = this.deg[atmIdx];
            for (int i = 0; i < atmDeg; i++) {
                IBond bond = mol.getBond(bondAdj[atmIdx][i]);
                IAtom nbr = bond.getOther(atom);
                if (nbr.getAtomicNumber() != null && nbr.getAtomicNumber() == 1) hcount++;
                int bord = bond.getOrder() != null ? bond.getOrder().numeric() : 0;
                if (bord == 0)
                    throw new IllegalArgumentException(
                            "Molecule had unsupported zero-order or unset bonds!");
                valence += bord;
                connections++;
            }

            sb.append('H').append(hcount);
            sb.append('v').append(valence);
            sb.append('X').append(connections);
        }

        Integer chg = atom.getFormalCharge();
        if (chg == null) chg = 0;

        if (chg <= -1 || chg >= +1) {
            if (chg >= 0) sb.append('+');
            else sb.append('-');
            int abs = Math.abs(chg);
            if (abs > 1) sb.append(abs);
            complex = true;
        } else if (mode == MODE_EXACT) {
            sb.append("+0");
        }

        return complex ? '[' + sb.toString() + ']' : sb.toString();
    }

    /**
     * Encodes the bond at index (bondIdx) to a SMARTS expression that matches itself.
     *
     * @param bondIdx bond index
     * @param beg atom index of first atom
     * @param end atom index of second atom
     * @return SMARTS bond expression
     */
    private String encodeBondExpr(int bondIdx, int beg, int end) {
        IBond bond = mol.getBond(bondIdx);
        if (bond.getOrder() == null) return "";

        boolean bArom = bond.isAromatic();
        boolean aArom = mol.getAtom(beg).isAromatic() && mol.getAtom(end).isAromatic();
        switch (bond.getOrder()) {
            case SINGLE:
                if (bArom) {
                    return aArom ? "" : ":";
                } else {
                    return aArom ? "-" : "";
                }
            case DOUBLE:
                return bArom ? "" : "=";
            case TRIPLE:
                return "#";
            default:
                throw new IllegalArgumentException("Unsupported bond type: " + bond.getOrder());
        }
    }
}
