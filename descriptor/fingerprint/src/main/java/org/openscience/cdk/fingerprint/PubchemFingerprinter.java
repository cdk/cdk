/* Copyright (C) 2009  Rajarshi Guha <rajarshi.guha@gmail.com>
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
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.smarts.SmartsPattern;
import org.openscience.cdk.tools.periodictable.PeriodicTable;

import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Generates a Pubchem fingerprint for a molecule.
 * 
 * These fingerprints are described
 * <a href="ftp://ftp.ncbi.nlm.nih.gov/pubchem/specifications/pubchem_fingerprints.txt">
 * here</a> and are of the structural key type, of length 881. See
 * {@link org.openscience.cdk.fingerprint.Fingerprinter} for a
 * more detailed description of fingerprints in general. This implementation is
 * based on the public domain code made available by the NCGC
 * <a href="http://www.ncgc.nih.gov/pub/openhts/code/NCGC_PubChemFP.java.txt">
 * here</a>
 * 
 * 
 * A fingerprint is generated for an AtomContainer with this code: <pre>
 *   Molecule molecule = new Molecule();
 *   PubchemFingerprinter fprinter = new PubchemFingerprinter();
 *   BitSet fingerprint = fprinter.getBitFingerprint(molecule);
 *   fprinter.getSize(); // returns 881
 *   fingerprint.length(); // returns the highest set bit
 * </pre>
 * Note that the fingerprinter assumes that you have detected aromaticity and
 * atom types before evaluating the fingerprint. Also the fingerprinter
 * expects that explicit H's are present
 * 
 * Note that this fingerprint is not particularly fast, as it will perform
 * ring detection using {@link org.openscience.cdk.ringsearch.AllRingsFinder}
 * as well as multiple SMARTS queries.
 * 
 * Some SMARTS patterns have been modified from the original code, since they
 * were based on explicit H matching. As a result, we replace the explicit H's
 * with a query of the {@code #<N>&!H0} where {@code <N>} is the atomic number. Thus bit 344 was
 * originally {@code [#6](~[#6])([H])} but is written here as
 * {@code [#6&!H0]~[#6]}. In some cases, where the H count can be reduced
 * to single possibility we directly use that H count. An example is bit 35,
 * which was {@code [#6](~[#6])(~[#6])(~[#6])([H])} and is rewritten as
 * {@code [#6H1](~[#6])(~[#6])(~[#6])}.
 * 
 * <br/>
 * <b>Warning - this class is not thread-safe and uses stores intermediate steps
 * internally. Please use a separate instance of the class for each thread.</b>
 * <br/>
 * <b>
 * Important! this fingerprint can not be used for substructure screening.
 * </b>
 *
 * @author Rajarshi Guha
 * @cdk.keyword fingerprint
 * @cdk.keyword similarity
 * @cdk.module fingerprint
 * @cdk.githash
 * @cdk.threadnonsafe
 */
public class PubchemFingerprinter extends AbstractFingerprinter implements IFingerprinter {

    /**
     * Number of bits in this fingerprint.
     */
    public static final int FP_SIZE = 881;

    private byte[]          m_bits;

    private Map<String,SmartsPattern> cache = new HashMap<>();

    public PubchemFingerprinter(IChemObjectBuilder builder) {
        m_bits = new byte[(FP_SIZE + 7) >> 3];
    }

    /**
     * Calculate 881 bit Pubchem fingerprint for a molecule.
     * 
     * See
     * <a href="ftp://ftp.ncbi.nlm.nih.gov/pubchem/specifications/pubchem_fingerprints.txt">here</a>
     * for a description of each bit position.
     *
     * @param atomContainer the molecule to consider
     * @return the fingerprint
     * @throws CDKException if there is an error during substructure
     * searching or atom typing
     * @see #getFingerprintAsBytes()
     */
    @Override
    public IBitFingerprint getBitFingerprint(IAtomContainer atomContainer) throws CDKException {
        generateFp(atomContainer);
        BitSet fp = new BitSet(FP_SIZE);
        for (int i = 0; i < FP_SIZE; i++) {
            if (isBitOn(i)) fp.set(i);
        }
        return new BitSetFingerprint(fp);
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Integer> getRawFingerprint(IAtomContainer iAtomContainer) throws CDKException {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the size of the fingerprint.
     *
     * @return The bit length of the fingerprint
     */
    @Override
    public int getSize() {
        return FP_SIZE;
    }

    static class CountElements {

        int[] counts = new int[120];

        public CountElements(IAtomContainer m) {
            for (int i = 0; i < m.getAtomCount(); i++)
                ++counts[m.getAtom(i).getAtomicNumber()];
        }

        public int getCount(int atno) {
            return counts[atno];
        }

        public int getCount(String symb) {
            return counts[PeriodicTable.getAtomicNumber(symb)];
        }
    }

    static class CountRings {

        int[][]  sssr = {};
        IRingSet ringSet;

        public CountRings(IAtomContainer m) {
            ringSet = Cycles.sssr(m).toRingSet();
        }

        public int countAnyRing(int size) {
            int c = 0;
            for (IAtomContainer ring : ringSet.atomContainers()) {
                if (ring.getAtomCount() == size) c++;
            }
            return c;
        }

        private boolean isCarbonOnlyRing(IAtomContainer ring) {
            for (IAtom ringAtom : ring.atoms()) {
                if (!ringAtom.getSymbol().equals("C")) return false;
            }
            return true;
        }

        private boolean isRingSaturated(IAtomContainer ring) {
            for (IBond ringBond : ring.bonds()) {
                if (ringBond.getOrder() != IBond.Order.SINGLE || ringBond.getFlag(CDKConstants.ISAROMATIC)
                        || ringBond.getFlag(CDKConstants.SINGLE_OR_DOUBLE)) return false;
            }
            return true;
        }

        private boolean isRingUnsaturated(IAtomContainer ring) {
            return !isRingSaturated(ring);
        }

        private int countNitrogenInRing(IAtomContainer ring) {
            int c = 0;
            for (IAtom ringAtom : ring.atoms()) {
                if (ringAtom.getSymbol().equals("N")) c++;
            }
            return c;
        }

        private int countHeteroInRing(IAtomContainer ring) {
            int c = 0;
            for (IAtom ringAtom : ring.atoms()) {
                if (!ringAtom.getSymbol().equals("C") && !ringAtom.getSymbol().equals("H")) c++;
            }
            return c;
        }

        private boolean isAromaticRing(IAtomContainer ring) {
            for (IBond bond : ring.bonds())
                if (!bond.getFlag(CDKConstants.ISAROMATIC)) return false;
            return true;
        }

        public int countAromaticRing() {
            int c = 0;
            for (IAtomContainer ring : ringSet.atomContainers()) {
                if (isAromaticRing(ring)) c++;
            }
            return c;
        }

        public int countHeteroAromaticRing() {
            int c = 0;
            for (IAtomContainer ring : ringSet.atomContainers()) {
                if (!isCarbonOnlyRing(ring) && isAromaticRing(ring)) c++;
            }
            return c;
        }

        public int countSaturatedOrAromaticCarbonOnlyRing(int size) {
            int c = 0;
            for (IAtomContainer ring : ringSet.atomContainers()) {
                if (ring.getAtomCount() == size && isCarbonOnlyRing(ring)
                        && (isRingSaturated(ring) || isAromaticRing(ring))) c++;
            }
            return c;
        }

        public int countSaturatedOrAromaticNitrogenContainingRing(int size) {
            int c = 0;
            for (IAtomContainer ring : ringSet.atomContainers()) {
                if (ring.getAtomCount() == size && (isRingSaturated(ring) || isAromaticRing(ring))
                        && countNitrogenInRing(ring) > 0) ++c;
            }
            return c;
        }

        public int countSaturatedOrAromaticHeteroContainingRing(int size) {
            int c = 0;
            for (IAtomContainer ring : ringSet.atomContainers()) {
                if (ring.getAtomCount() == size && (isRingSaturated(ring) || isAromaticRing(ring))
                        && countHeteroInRing(ring) > 0) ++c;
            }
            return c;
        }

        public int countUnsaturatedCarbonOnlyRing(int size) {
            int c = 0;
            for (IAtomContainer ring : ringSet.atomContainers()) {
                if (ring.getAtomCount() == size && isRingUnsaturated(ring) && !isAromaticRing(ring)
                        && isCarbonOnlyRing(ring)) ++c;
            }
            return c;
        }

        public int countUnsaturatedNitrogenContainingRing(int size) {
            int c = 0;
            for (IAtomContainer ring : ringSet.atomContainers()) {
                if (ring.getAtomCount() == size && isRingUnsaturated(ring) && !isAromaticRing(ring)
                        && countNitrogenInRing(ring) > 0) ++c;
            }
            return c;
        }

        public int countUnsaturatedHeteroContainingRing(int size) {
            int c = 0;
            for (IAtomContainer ring : ringSet.atomContainers()) {
                if (ring.getAtomCount() == size && isRingUnsaturated(ring) && !isAromaticRing(ring)
                        && countHeteroInRing(ring) > 0) ++c;
            }
            return c;
        }
    }

    class CountSubstructures {

        private IAtomContainer mol;

        public CountSubstructures(IAtomContainer m) {
            mol = m;
        }

        public int countSubstructure(String smarts) throws CDKException {
            SmartsPattern ptrn = cache.get(smarts);
            if (ptrn == null) {
                ptrn = SmartsPattern.create(smarts);
                ptrn.setPrepare(false);
                cache.put(smarts, ptrn);
            }
            return ptrn.matchAll(mol).countUnique();
        }
    }

    private void _generateFp(byte[] fp, IAtomContainer mol) throws CDKException {
        SmartsPattern.prepare(mol);
        countElements(fp, mol);
        countRings(fp, mol);
        countSubstructures(fp, mol);
    }

    private void generateFp(IAtomContainer mol) throws CDKException {
        for (int i = 0; i < m_bits.length; ++i) {
            m_bits[i] = 0;
        }
        _generateFp(m_bits, mol);
    }

    private boolean isBitOn(int bit) {
        return (m_bits[bit >> 3] & MASK[bit % 8]) != 0;
    }

    /**
     * Returns the fingerprint generated for a molecule as a byte[].
     * 
     * Note that this should be immediately called after calling
     * {@link #getBitFingerprint(org.openscience.cdk.interfaces.IAtomContainer)}
     *
     * @return The fingerprint as a byte array
     * @see #getBitFingerprint(org.openscience.cdk.interfaces.IAtomContainer)
     */
    public byte[] getFingerprintAsBytes() {
        return m_bits;
    }

    /**
     * Returns a fingerprint from a Base64 encoded Pubchem fingerprint.
     *
     * @param enc The Base64 encoded fingerprint
     * @return A BitSet corresponding to the input fingerprint
     */
    public static BitSet decode(String enc) {
        byte[] fp = base64Decode(enc);
        if (fp.length < 4) {
            throw new IllegalArgumentException("Input is not a proper PubChem base64 encoded fingerprint");
        }

        int len = (fp[0] << 24) | (fp[1] << 16) | (fp[2] << 8) | (fp[3] & 0xff);
        if (len != FP_SIZE) {
            throw new IllegalArgumentException("Input is not a proper PubChem base64 encoded fingerprint");
        }

        // note the IChemObjectBuilder is passed as null because the SMARTSQueryTool
        // isn't needed when decoding
        PubchemFingerprinter pc = new PubchemFingerprinter(null);
        for (int i = 0; i < pc.m_bits.length; ++i) {
            pc.m_bits[i] = fp[i + 4];
        }

        BitSet ret = new BitSet(FP_SIZE);
        for (int i = 0; i < FP_SIZE; i++) {
            if (pc.isBitOn(i)) ret.set(i);
        }
        return ret;
    }

    // the first four bytes contains the length of the fingerprint
    private String encode() {
        byte[] pack = new byte[4 + m_bits.length];

        pack[0] = (byte) ((FP_SIZE & 0xffffffff) >> 24);
        pack[1] = (byte) ((FP_SIZE & 0x00ffffff) >> 16);
        pack[2] = (byte) ((FP_SIZE & 0x0000ffff) >> 8);
        pack[3] = (byte) (FP_SIZE & 0x000000ff);
        for (int i = 0; i < m_bits.length; ++i) {
            pack[i + 4] = m_bits[i];
        }
        return base64Encode(pack);
    }

    private static String BASE64_LUT = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvwxyz0123456789+/=";

    // based on NCBI C implementation
    private static String base64Encode(byte[] data) {
        char c64[] = new char[data.length * 4 / 3 + 5];
        for (int i = 0, k = 0; i < data.length; i += 3, k += 4) {
            c64[k + 0] = (char) (data[i] >> 2);
            c64[k + 1] = (char) ((data[i] & 0x03) << 4);
            c64[k + 2] = c64[k + 3] = 64;
            if ((i + i) < data.length) {
                c64[k + 1] |= data[i + 1] >> 4;
                c64[k + 2] = (char) ((data[i + 1] & 0x0f) << 2);
            }
            if ((i + 2) < data.length) {
                c64[k + 2] |= data[i + 2] >> 6;
                c64[k + 3] = (char) (data[i + 2] & 0x3f);
            }
            for (int j = 0; j < 4; ++j) {
                c64[k + j] = BASE64_LUT.charAt(c64[k + j]);
            }
        }
        return new String(c64);
    }

    // based on NCBI C implementation
    private static byte[] base64Decode(String data) {
        int len = data.length();

        byte[] b64 = new byte[len * 3 / 4];
        byte[] buf = new byte[4];
        boolean done = false;

        for (int i = 0, j, k = 0; i < len && !done;) {
            buf[0] = buf[1] = buf[2] = buf[3] = 0;
            for (j = 0; j < 4 && i < len; ++j) {
                char c = data.charAt(i);
                if (c >= 'A' && c <= 'Z') {
                    c -= 'A';
                } else if (c >= 'a' && c <= 'z') {
                    c = (char) (c - 'a' + 26);
                } else if (c >= '0' && c <= '9') {
                    c = (char) (c - '0' + 52);
                } else if (c == '+') {
                    c = 62;
                } else if (c == '/') {
                    c = 63;
                } else if (c == '=' || c == '-') {
                    done = true;
                    break;
                } else {
                    ++i;
                    --j;
                    continue;
                }
                buf[j] = (byte) c;
                ++i;
            }

            if (k < b64.length && j >= 1) {
                b64[k++] = (byte) ((buf[0] << 2) | ((buf[1] & 0x30) >> 4));
            }
            if (k < b64.length && j >= 3) {
                b64[k++] = (byte) (((buf[1] & 0x0f) << 4) | ((buf[2] & 0x3c) >> 2));
            }
            if (k < b64.length && j >= 4) {
                b64[k++] = (byte) (((buf[2] & 0x03) << 6) | (buf[3] & 0x3f));
            }
        }
        return b64;
    }

    static final int BITCOUNT[] = {0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4,
            3, 4, 4, 5, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
            1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 2, 3, 3, 4,
            3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 1, 2, 2, 3, 2, 3, 3, 4,
            2, 3, 3, 4, 3, 4, 4, 5, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5,
            4, 5, 5, 6, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
            3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 4, 5, 5, 6,
            5, 6, 6, 7, 5, 6, 6, 7, 6, 7, 7, 8};

    static final int MASK[]     = {0x80, 0x40, 0x20, 0x10, 0x08, 0x04, 0x02, 0x01};

    /*
     * Section 1: Hierarchic Element Counts - These bs test for the presence or
     * count of individual chemical atoms represented by their atomic symbol.
     */
    private static void countElements(byte[] fp, IAtomContainer mol) {
        int b;
        CountElements ce = new CountElements(mol);

        b = 0;
        if (ce.getCount("H") >= 4) fp[b >> 3] |= MASK[b % 8];
        b = 1;
        if (ce.getCount("H") >= 8) fp[b >> 3] |= MASK[b % 8];
        b = 2;
        if (ce.getCount("H") >= 16) fp[b >> 3] |= MASK[b % 8];
        b = 3;
        if (ce.getCount("H") >= 32) fp[b >> 3] |= MASK[b % 8];
        b = 4;
        if (ce.getCount("Li") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 5;
        if (ce.getCount("Li") >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 6;
        if (ce.getCount("B") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 7;
        if (ce.getCount("B") >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 8;
        if (ce.getCount("B") >= 4) fp[b >> 3] |= MASK[b % 8];
        b = 9;
        if (ce.getCount("C") >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 10;
        if (ce.getCount("C") >= 4) fp[b >> 3] |= MASK[b % 8];
        b = 11;
        if (ce.getCount("C") >= 8) fp[b >> 3] |= MASK[b % 8];
        b = 12;
        if (ce.getCount("C") >= 16) fp[b >> 3] |= MASK[b % 8];
        b = 13;
        if (ce.getCount("C") >= 32) fp[b >> 3] |= MASK[b % 8];
        b = 14;
        if (ce.getCount("N") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 15;
        if (ce.getCount("N") >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 16;
        if (ce.getCount("N") >= 4) fp[b >> 3] |= MASK[b % 8];
        b = 17;
        if (ce.getCount("N") >= 8) fp[b >> 3] |= MASK[b % 8];
        b = 18;
        if (ce.getCount("O") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 19;
        if (ce.getCount("O") >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 20;
        if (ce.getCount("O") >= 4) fp[b >> 3] |= MASK[b % 8];
        b = 21;
        if (ce.getCount("O") >= 8) fp[b >> 3] |= MASK[b % 8];
        b = 22;
        if (ce.getCount("O") >= 16) fp[b >> 3] |= MASK[b % 8];
        b = 23;
        if (ce.getCount("F") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 24;
        if (ce.getCount("F") >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 25;
        if (ce.getCount("F") >= 4) fp[b >> 3] |= MASK[b % 8];
        b = 26;
        if (ce.getCount("Na") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 27;
        if (ce.getCount("Na") >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 28;
        if (ce.getCount("Si") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 29;
        if (ce.getCount("Si") >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 30;
        if (ce.getCount("P") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 31;
        if (ce.getCount("P") >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 32;
        if (ce.getCount("P") >= 4) fp[b >> 3] |= MASK[b % 8];
        b = 33;
        if (ce.getCount("S") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 34;
        if (ce.getCount("S") >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 35;
        if (ce.getCount("S") >= 4) fp[b >> 3] |= MASK[b % 8];
        b = 36;
        if (ce.getCount("S") >= 8) fp[b >> 3] |= MASK[b % 8];
        b = 37;
        if (ce.getCount("Cl") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 38;
        if (ce.getCount("Cl") >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 39;
        if (ce.getCount("Cl") >= 4) fp[b >> 3] |= MASK[b % 8];
        b = 40;
        if (ce.getCount("Cl") >= 8) fp[b >> 3] |= MASK[b % 8];
        b = 41;
        if (ce.getCount("K") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 42;
        if (ce.getCount("K") >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 43;
        if (ce.getCount("Br") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 44;
        if (ce.getCount("Br") >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 45;
        if (ce.getCount("Br") >= 4) fp[b >> 3] |= MASK[b % 8];
        b = 46;
        if (ce.getCount("I") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 47;
        if (ce.getCount("I") >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 48;
        if (ce.getCount("I") >= 4) fp[b >> 3] |= MASK[b % 8];
        b = 49;
        if (ce.getCount("Be") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 50;
        if (ce.getCount("Mg") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 51;
        if (ce.getCount("Al") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 52;
        if (ce.getCount("Ca") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 53;
        if (ce.getCount("Sc") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 54;
        if (ce.getCount("Ti") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 55;
        if (ce.getCount("V") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 56;
        if (ce.getCount("Cr") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 57;
        if (ce.getCount("Mn") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 58;
        if (ce.getCount("Fe") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 59;
        if (ce.getCount("Co") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 60;
        if (ce.getCount("Ni") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 61;
        if (ce.getCount("Cu") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 62;
        if (ce.getCount("Zn") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 63;
        if (ce.getCount("Ga") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 64;
        if (ce.getCount("Ge") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 65;
        if (ce.getCount("As") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 66;
        if (ce.getCount("Se") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 67;
        if (ce.getCount("Kr") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 68;
        if (ce.getCount("Rb") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 69;
        if (ce.getCount("Sr") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 70;
        if (ce.getCount("Y") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 71;
        if (ce.getCount("Zr") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 72;
        if (ce.getCount("Nb") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 73;
        if (ce.getCount("Mo") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 74;
        if (ce.getCount("Ru") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 75;
        if (ce.getCount("Rh") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 76;
        if (ce.getCount("Pd") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 77;
        if (ce.getCount("Ag") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 78;
        if (ce.getCount("Cd") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 79;
        if (ce.getCount("In") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 80;
        if (ce.getCount("Sn") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 81;
        if (ce.getCount("Sb") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 82;
        if (ce.getCount("Te") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 83;
        if (ce.getCount("Xe") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 84;
        if (ce.getCount("Cs") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 85;
        if (ce.getCount("Ba") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 86;
        if (ce.getCount("Lu") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 87;
        if (ce.getCount("Hf") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 88;
        if (ce.getCount("Ta") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 89;
        if (ce.getCount("W") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 90;
        if (ce.getCount("Re") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 91;
        if (ce.getCount("Os") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 92;
        if (ce.getCount("Ir") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 93;
        if (ce.getCount("Pt") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 94;
        if (ce.getCount("Au") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 95;
        if (ce.getCount("Hg") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 96;
        if (ce.getCount("Tl") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 97;
        if (ce.getCount("Pb") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 98;
        if (ce.getCount("Bi") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 99;
        if (ce.getCount("La") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 100;
        if (ce.getCount("Ce") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 101;
        if (ce.getCount("Pr") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 102;
        if (ce.getCount("Nd") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 103;
        if (ce.getCount("Pm") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 104;
        if (ce.getCount("Sm") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 105;
        if (ce.getCount("Eu") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 106;
        if (ce.getCount("Gd") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 107;
        if (ce.getCount("Tb") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 108;
        if (ce.getCount("Dy") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 109;
        if (ce.getCount("Ho") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 110;
        if (ce.getCount("Er") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 111;
        if (ce.getCount("Tm") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 112;
        if (ce.getCount("Yb") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 113;
        if (ce.getCount("Tc") >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 114;
        if (ce.getCount("U") >= 1) fp[b >> 3] |= MASK[b % 8];
    }

    /*
     * Section 2: Rings in a canonic ESSR ring set-These bs test for the
     * presence or count of the described chemical ring system. An ESSR ring is
     * any ring which does not share three consecutive atoms with any other ring
     * in the chemical structure. For example, naphthalene has three ESSR rings
     * (two phenyl fragments and the 10-membered envelope), while biphenyl will
     * yield a count of only two ESSR rings.
     */
    private static void countRings(byte[] fp, IAtomContainer mol) {
        CountRings cr = new CountRings(mol);
        int b;

        b = 115;
        if (cr.countAnyRing(3) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 116;
        if (cr.countSaturatedOrAromaticCarbonOnlyRing(3) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 117;
        if (cr.countSaturatedOrAromaticNitrogenContainingRing(3) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 118;
        if (cr.countSaturatedOrAromaticHeteroContainingRing(3) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 119;
        if (cr.countUnsaturatedCarbonOnlyRing(3) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 120;
        if (cr.countUnsaturatedNitrogenContainingRing(3) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 121;
        if (cr.countUnsaturatedHeteroContainingRing(3) >= 1) fp[b >> 3] |= MASK[b % 8];

        b = 122;
        if (cr.countAnyRing(3) >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 123;
        if (cr.countSaturatedOrAromaticCarbonOnlyRing(3) >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 124;
        if (cr.countSaturatedOrAromaticNitrogenContainingRing(3) >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 125;
        if (cr.countSaturatedOrAromaticHeteroContainingRing(3) >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 126;
        if (cr.countUnsaturatedCarbonOnlyRing(3) >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 127;
        if (cr.countUnsaturatedNitrogenContainingRing(3) >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 128;
        if (cr.countUnsaturatedHeteroContainingRing(3) >= 2) fp[b >> 3] |= MASK[b % 8];

        b = 129;
        if (cr.countAnyRing(4) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 130;
        if (cr.countSaturatedOrAromaticCarbonOnlyRing(4) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 131;
        if (cr.countSaturatedOrAromaticNitrogenContainingRing(4) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 132;
        if (cr.countSaturatedOrAromaticHeteroContainingRing(4) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 133;
        if (cr.countUnsaturatedCarbonOnlyRing(4) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 134;
        if (cr.countUnsaturatedNitrogenContainingRing(4) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 135;
        if (cr.countUnsaturatedHeteroContainingRing(4) >= 1) fp[b >> 3] |= MASK[b % 8];

        b = 136;
        if (cr.countAnyRing(4) >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 137;
        if (cr.countSaturatedOrAromaticCarbonOnlyRing(4) >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 138;
        if (cr.countSaturatedOrAromaticNitrogenContainingRing(4) >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 139;
        if (cr.countSaturatedOrAromaticHeteroContainingRing(4) >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 140;
        if (cr.countUnsaturatedCarbonOnlyRing(4) >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 141;
        if (cr.countUnsaturatedNitrogenContainingRing(4) >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 142;
        if (cr.countUnsaturatedHeteroContainingRing(4) >= 2) fp[b >> 3] |= MASK[b % 8];

        b = 143;
        if (cr.countAnyRing(5) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 144;
        if (cr.countSaturatedOrAromaticCarbonOnlyRing(5) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 145;
        if (cr.countSaturatedOrAromaticNitrogenContainingRing(5) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 146;
        if (cr.countSaturatedOrAromaticHeteroContainingRing(5) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 147;
        if (cr.countUnsaturatedCarbonOnlyRing(5) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 148;
        if (cr.countUnsaturatedNitrogenContainingRing(5) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 149;
        if (cr.countUnsaturatedHeteroContainingRing(5) >= 1) fp[b >> 3] |= MASK[b % 8];

        b = 150;
        if (cr.countAnyRing(5) >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 151;
        if (cr.countSaturatedOrAromaticCarbonOnlyRing(5) >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 152;
        if (cr.countSaturatedOrAromaticNitrogenContainingRing(5) >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 153;
        if (cr.countSaturatedOrAromaticHeteroContainingRing(5) >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 154;
        if (cr.countUnsaturatedCarbonOnlyRing(5) >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 155;
        if (cr.countUnsaturatedNitrogenContainingRing(5) >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 156;
        if (cr.countUnsaturatedHeteroContainingRing(5) >= 2) fp[b >> 3] |= MASK[b % 8];

        b = 157;
        if (cr.countAnyRing(5) >= 3) fp[b >> 3] |= MASK[b % 8];
        b = 158;
        if (cr.countSaturatedOrAromaticCarbonOnlyRing(5) >= 3) fp[b >> 3] |= MASK[b % 8];
        b = 159;
        if (cr.countSaturatedOrAromaticNitrogenContainingRing(5) >= 3) fp[b >> 3] |= MASK[b % 8];
        b = 160;
        if (cr.countSaturatedOrAromaticHeteroContainingRing(5) >= 3) fp[b >> 3] |= MASK[b % 8];
        b = 161;
        if (cr.countUnsaturatedCarbonOnlyRing(5) >= 3) fp[b >> 3] |= MASK[b % 8];
        b = 162;
        if (cr.countUnsaturatedNitrogenContainingRing(5) >= 3) fp[b >> 3] |= MASK[b % 8];
        b = 163;
        if (cr.countUnsaturatedHeteroContainingRing(5) >= 3) fp[b >> 3] |= MASK[b % 8];

        b = 164;
        if (cr.countAnyRing(5) >= 4) fp[b >> 3] |= MASK[b % 8];
        b = 165;
        if (cr.countSaturatedOrAromaticCarbonOnlyRing(5) >= 4) fp[b >> 3] |= MASK[b % 8];
        b = 166;
        if (cr.countSaturatedOrAromaticNitrogenContainingRing(5) >= 4) fp[b >> 3] |= MASK[b % 8];
        b = 167;
        if (cr.countSaturatedOrAromaticHeteroContainingRing(5) >= 4) fp[b >> 3] |= MASK[b % 8];
        b = 168;
        if (cr.countUnsaturatedCarbonOnlyRing(5) >= 4) fp[b >> 3] |= MASK[b % 8];
        b = 169;
        if (cr.countUnsaturatedNitrogenContainingRing(5) >= 4) fp[b >> 3] |= MASK[b % 8];
        b = 170;
        if (cr.countUnsaturatedHeteroContainingRing(5) >= 4) fp[b >> 3] |= MASK[b % 8];

        b = 171;
        if (cr.countAnyRing(5) >= 5) fp[b >> 3] |= MASK[b % 8];
        b = 172;
        if (cr.countSaturatedOrAromaticCarbonOnlyRing(5) >= 5) fp[b >> 3] |= MASK[b % 8];
        b = 173;
        if (cr.countSaturatedOrAromaticNitrogenContainingRing(5) >= 5) fp[b >> 3] |= MASK[b % 8];
        b = 174;
        if (cr.countSaturatedOrAromaticHeteroContainingRing(5) >= 5) fp[b >> 3] |= MASK[b % 8];
        b = 175;
        if (cr.countUnsaturatedCarbonOnlyRing(5) >= 5) fp[b >> 3] |= MASK[b % 8];
        b = 176;
        if (cr.countUnsaturatedNitrogenContainingRing(5) >= 5) fp[b >> 3] |= MASK[b % 8];
        b = 177;
        if (cr.countUnsaturatedHeteroContainingRing(5) >= 5) fp[b >> 3] |= MASK[b % 8];

        b = 178;
        if (cr.countAnyRing(6) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 179;
        if (cr.countSaturatedOrAromaticCarbonOnlyRing(6) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 180;
        if (cr.countSaturatedOrAromaticNitrogenContainingRing(6) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 181;
        if (cr.countSaturatedOrAromaticHeteroContainingRing(6) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 182;
        if (cr.countUnsaturatedCarbonOnlyRing(6) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 183;
        if (cr.countUnsaturatedNitrogenContainingRing(6) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 184;
        if (cr.countUnsaturatedHeteroContainingRing(6) >= 1) fp[b >> 3] |= MASK[b % 8];

        b = 185;
        if (cr.countAnyRing(6) >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 186;
        if (cr.countSaturatedOrAromaticCarbonOnlyRing(6) >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 187;
        if (cr.countSaturatedOrAromaticNitrogenContainingRing(6) >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 188;
        if (cr.countSaturatedOrAromaticHeteroContainingRing(6) >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 189;
        if (cr.countUnsaturatedCarbonOnlyRing(6) >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 190;
        if (cr.countUnsaturatedNitrogenContainingRing(6) >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 191;
        if (cr.countUnsaturatedHeteroContainingRing(6) >= 2) fp[b >> 3] |= MASK[b % 8];

        b = 192;
        if (cr.countAnyRing(6) >= 3) fp[b >> 3] |= MASK[b % 8];
        b = 193;
        if (cr.countSaturatedOrAromaticCarbonOnlyRing(6) >= 3) fp[b >> 3] |= MASK[b % 8];
        b = 194;
        if (cr.countSaturatedOrAromaticNitrogenContainingRing(6) >= 3) fp[b >> 3] |= MASK[b % 8];
        b = 195;
        if (cr.countSaturatedOrAromaticHeteroContainingRing(6) >= 3) fp[b >> 3] |= MASK[b % 8];
        b = 196;
        if (cr.countUnsaturatedCarbonOnlyRing(6) >= 3) fp[b >> 3] |= MASK[b % 8];
        b = 197;
        if (cr.countUnsaturatedNitrogenContainingRing(6) >= 3) fp[b >> 3] |= MASK[b % 8];
        b = 198;
        if (cr.countUnsaturatedHeteroContainingRing(6) >= 3) fp[b >> 3] |= MASK[b % 8];

        b = 199;
        if (cr.countAnyRing(6) >= 4) fp[b >> 3] |= MASK[b % 8];
        b = 200;
        if (cr.countSaturatedOrAromaticCarbonOnlyRing(6) >= 4) fp[b >> 3] |= MASK[b % 8];
        b = 201;
        if (cr.countSaturatedOrAromaticNitrogenContainingRing(6) >= 4) fp[b >> 3] |= MASK[b % 8];
        b = 202;
        if (cr.countSaturatedOrAromaticHeteroContainingRing(6) >= 4) fp[b >> 3] |= MASK[b % 8];
        b = 203;
        if (cr.countUnsaturatedCarbonOnlyRing(6) >= 4) fp[b >> 3] |= MASK[b % 8];
        b = 204;
        if (cr.countUnsaturatedNitrogenContainingRing(6) >= 4) fp[b >> 3] |= MASK[b % 8];
        b = 205;
        if (cr.countUnsaturatedHeteroContainingRing(6) >= 4) fp[b >> 3] |= MASK[b % 8];

        b = 206;
        if (cr.countAnyRing(6) >= 5) fp[b >> 3] |= MASK[b % 8];
        b = 207;
        if (cr.countSaturatedOrAromaticCarbonOnlyRing(6) >= 5) fp[b >> 3] |= MASK[b % 8];
        b = 208;
        if (cr.countSaturatedOrAromaticNitrogenContainingRing(6) >= 5) fp[b >> 3] |= MASK[b % 8];
        b = 209;
        if (cr.countSaturatedOrAromaticHeteroContainingRing(6) >= 5) fp[b >> 3] |= MASK[b % 8];
        b = 210;
        if (cr.countUnsaturatedCarbonOnlyRing(6) >= 5) fp[b >> 3] |= MASK[b % 8];
        b = 211;
        if (cr.countUnsaturatedNitrogenContainingRing(6) >= 5) fp[b >> 3] |= MASK[b % 8];
        b = 212;
        if (cr.countUnsaturatedHeteroContainingRing(6) >= 5) fp[b >> 3] |= MASK[b % 8];

        b = 213;
        if (cr.countAnyRing(7) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 214;
        if (cr.countSaturatedOrAromaticCarbonOnlyRing(7) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 215;
        if (cr.countSaturatedOrAromaticNitrogenContainingRing(7) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 216;
        if (cr.countSaturatedOrAromaticHeteroContainingRing(7) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 217;
        if (cr.countUnsaturatedCarbonOnlyRing(7) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 218;
        if (cr.countUnsaturatedNitrogenContainingRing(7) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 219;
        if (cr.countUnsaturatedHeteroContainingRing(7) >= 1) fp[b >> 3] |= MASK[b % 8];

        b = 220;
        if (cr.countAnyRing(7) >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 221;
        if (cr.countSaturatedOrAromaticCarbonOnlyRing(7) >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 222;
        if (cr.countSaturatedOrAromaticNitrogenContainingRing(7) >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 223;
        if (cr.countSaturatedOrAromaticHeteroContainingRing(7) >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 224;
        if (cr.countUnsaturatedCarbonOnlyRing(7) >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 225;
        if (cr.countUnsaturatedNitrogenContainingRing(7) >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 226;
        if (cr.countUnsaturatedHeteroContainingRing(7) >= 2) fp[b >> 3] |= MASK[b % 8];

        b = 227;
        if (cr.countAnyRing(8) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 228;
        if (cr.countSaturatedOrAromaticCarbonOnlyRing(8) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 229;
        if (cr.countSaturatedOrAromaticNitrogenContainingRing(8) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 230;
        if (cr.countSaturatedOrAromaticHeteroContainingRing(8) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 231;
        if (cr.countUnsaturatedCarbonOnlyRing(8) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 232;
        if (cr.countUnsaturatedNitrogenContainingRing(8) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 233;
        if (cr.countUnsaturatedHeteroContainingRing(8) >= 1) fp[b >> 3] |= MASK[b % 8];

        b = 234;
        if (cr.countAnyRing(8) >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 235;
        if (cr.countSaturatedOrAromaticCarbonOnlyRing(8) >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 236;
        if (cr.countSaturatedOrAromaticNitrogenContainingRing(8) >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 237;
        if (cr.countSaturatedOrAromaticHeteroContainingRing(8) >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 238;
        if (cr.countUnsaturatedCarbonOnlyRing(8) >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 239;
        if (cr.countUnsaturatedNitrogenContainingRing(8) >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 240;
        if (cr.countUnsaturatedHeteroContainingRing(8) >= 2) fp[b >> 3] |= MASK[b % 8];

        b = 241;
        if (cr.countAnyRing(9) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 242;
        if (cr.countSaturatedOrAromaticCarbonOnlyRing(9) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 243;
        if (cr.countSaturatedOrAromaticNitrogenContainingRing(9) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 244;
        if (cr.countSaturatedOrAromaticHeteroContainingRing(9) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 245;
        if (cr.countUnsaturatedCarbonOnlyRing(9) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 246;
        if (cr.countUnsaturatedNitrogenContainingRing(9) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 247;
        if (cr.countUnsaturatedHeteroContainingRing(9) >= 1) fp[b >> 3] |= MASK[b % 8];

        b = 248;
        if (cr.countAnyRing(10) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 249;
        if (cr.countSaturatedOrAromaticCarbonOnlyRing(10) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 250;
        if (cr.countSaturatedOrAromaticNitrogenContainingRing(10) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 251;
        if (cr.countSaturatedOrAromaticHeteroContainingRing(10) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 252;
        if (cr.countUnsaturatedCarbonOnlyRing(10) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 253;
        if (cr.countUnsaturatedNitrogenContainingRing(10) >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 254;
        if (cr.countUnsaturatedHeteroContainingRing(10) >= 1) fp[b >> 3] |= MASK[b % 8];

        b = 255;
        if (cr.countAromaticRing() >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 256;
        if (cr.countHeteroAromaticRing() >= 1) fp[b >> 3] |= MASK[b % 8];
        b = 257;
        if (cr.countAromaticRing() >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 258;
        if (cr.countHeteroAromaticRing() >= 2) fp[b >> 3] |= MASK[b % 8];
        b = 259;
        if (cr.countAromaticRing() >= 3) fp[b >> 3] |= MASK[b % 8];
        b = 260;
        if (cr.countHeteroAromaticRing() >= 3) fp[b >> 3] |= MASK[b % 8];
        b = 261;
        if (cr.countAromaticRing() >= 4) fp[b >> 3] |= MASK[b % 8];
        b = 262;
        if (cr.countHeteroAromaticRing() >= 4) fp[b >> 3] |= MASK[b % 8];
    }

    private void countSubstructures(byte[] fp, IAtomContainer mol) throws CDKException {
        CountSubstructures cs = new CountSubstructures(mol);
        int b;

        /*
         * Section 3: Simple atom pairs. These bits test for the presence of
         * patterns of bonded atom pairs, regardless of bond order or count.
         */
        b = 263;
        if (cs.countSubstructure("[Li&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 264;
        if (cs.countSubstructure("[Li]~[Li]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 265;
        if (cs.countSubstructure("[Li]~[#5]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 266;
        if (cs.countSubstructure("[Li]~[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 267;
        if (cs.countSubstructure("[Li]~[#8]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 268;
        if (cs.countSubstructure("[Li]~[F]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 269;
        if (cs.countSubstructure("[Li]~[#15]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 270;
        if (cs.countSubstructure("[Li]~[#16]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 271;
        if (cs.countSubstructure("[Li]~[Cl]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 272;
        if (cs.countSubstructure("[#5&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 273;
        if (cs.countSubstructure("[#5]~[#5]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 274;
        if (cs.countSubstructure("[#5]~[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 275;
        if (cs.countSubstructure("[#5]~[#7]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 276;
        if (cs.countSubstructure("[#5]~[#8]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 277;
        if (cs.countSubstructure("[#5]~[F]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 278;
        if (cs.countSubstructure("[#5]~[#14]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 279;
        if (cs.countSubstructure("[#5]~[#15]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 280;
        if (cs.countSubstructure("[#5]~[#16]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 281;
        if (cs.countSubstructure("[#5]~[Cl]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 282;
        if (cs.countSubstructure("[#5]~[Br]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 283;
        if (cs.countSubstructure("[#6&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 284;
        if (cs.countSubstructure("[#6]~[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 285;
        if (cs.countSubstructure("[#6]~[#7]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 286;
        if (cs.countSubstructure("[#6]~[#8]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 287;
        if (cs.countSubstructure("[#6]~[F]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 288;
        if (cs.countSubstructure("[#6]~[Na]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 289;
        if (cs.countSubstructure("[#6]~[Mg]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 290;
        if (cs.countSubstructure("[#6]~[Al]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 291;
        if (cs.countSubstructure("[#6]~[#14]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 292;
        if (cs.countSubstructure("[#6]~[#15]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 293;
        if (cs.countSubstructure("[#6]~[#16]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 294;
        if (cs.countSubstructure("[#6]~[Cl]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 295;
        if (cs.countSubstructure("[#6]~[#33]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 296;
        if (cs.countSubstructure("[#6]~[#34]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 297;
        if (cs.countSubstructure("[#6]~[Br]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 298;
        if (cs.countSubstructure("[#6]~[I]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 299;
        if (cs.countSubstructure("[#7&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 300;
        if (cs.countSubstructure("[#7]~[#7]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 301;
        if (cs.countSubstructure("[#7]~[#8]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 302;
        if (cs.countSubstructure("[#7]~[F]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 303;
        if (cs.countSubstructure("[#7]~[#14]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 304;
        if (cs.countSubstructure("[#7]~[#15]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 305;
        if (cs.countSubstructure("[#7]~[#16]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 306;
        if (cs.countSubstructure("[#7]~[Cl]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 307;
        if (cs.countSubstructure("[#7]~[Br]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 308;
        if (cs.countSubstructure("[#8&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 309;
        if (cs.countSubstructure("[#8]~[#8]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 310;
        if (cs.countSubstructure("[#8]~[Mg]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 311;
        if (cs.countSubstructure("[#8]~[Na]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 312;
        if (cs.countSubstructure("[#8]~[Al]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 313;
        if (cs.countSubstructure("[#8]~[#14]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 314;
        if (cs.countSubstructure("[#8]~[#15]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 315;
        if (cs.countSubstructure("[#8]~[K]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 316;
        if (cs.countSubstructure("[F]~[#15]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 317;
        if (cs.countSubstructure("[F]~[#16]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 318;
        if (cs.countSubstructure("[Al&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 319;
        if (cs.countSubstructure("[Al]~[Cl]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 320;
        if (cs.countSubstructure("[#14&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 321;
        if (cs.countSubstructure("[#14]~[#14]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 322;
        if (cs.countSubstructure("[#14]~[Cl]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 323;
        if (cs.countSubstructure("[#15&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 324;
        if (cs.countSubstructure("[#15]~[#15]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 325;
        if (cs.countSubstructure("[#33&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 326;
        if (cs.countSubstructure("[#33]~[#33]") > 0) fp[b >> 3] |= MASK[b % 8];

        /*
         * Section 4: Simple atom nearest neighbors. These bits test for the
         * presence of atom nearest neighbor patterns, regardless of bond order
         * or count, but where bond aromaticity (denoted by "~") is significant.
         */
        b = 327;
        if (cs.countSubstructure("[#6](~Br)(~[#6])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 328;
        if (cs.countSubstructure("[#6](~Br)(~[#6])(~[#6])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 329;
        if (cs.countSubstructure("[#6&!H0]~[Br]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 330;
        if (cs.countSubstructure("[#6](~[Br])(:[c])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 331;
        if (cs.countSubstructure("[#6](~[Br])(:[n])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 332;
        if (cs.countSubstructure("[#6](~[#6])(~[#6])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 333;
        if (cs.countSubstructure("[#6](~[#6])(~[#6])(~[#6])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 334;
        if (cs.countSubstructure("[#6](~[#6])(~[#6])(~[#6])(~[#6])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 335;
        if (cs.countSubstructure("[#6H1](~[#6])(~[#6])(~[#6])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 336;
        if (cs.countSubstructure("[#6](~[#6])(~[#6])(~[#6])(~[#7])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 337;
        if (cs.countSubstructure("[#6](~[#6])(~[#6])(~[#6])(~[#8])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 338;
        if (cs.countSubstructure("[#6H1](~[#6])(~[#6])(~[#7])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 339;
        if (cs.countSubstructure("[#6H1](~[#6])(~[#6])(~[#8])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 340;
        if (cs.countSubstructure("[#6](~[#6])(~[#6])(~[#7])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 341;
        if (cs.countSubstructure("[#6](~[#6])(~[#6])(~[#8])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 342;
        if (cs.countSubstructure("[#6](~[#6])(~[Cl])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 343;
        if (cs.countSubstructure("[#6&!H0](~[#6])(~[Cl])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 344;
        if (cs.countSubstructure("[#6H,#6H2,#6H3,#6H4]~[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 345;
        if (cs.countSubstructure("[#6&!H0](~[#6])(~[#7])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 346;
        if (cs.countSubstructure("[#6&!H0](~[#6])(~[#8])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 347;
        if (cs.countSubstructure("[#6H1](~[#6])(~[#8])(~[#8])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 348;
        if (cs.countSubstructure("[#6&!H0](~[#6])(~[#15])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 349;
        if (cs.countSubstructure("[#6&!H0](~[#6])(~[#16])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 350;
        if (cs.countSubstructure("[#6](~[#6])(~[I])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 351;
        if (cs.countSubstructure("[#6](~[#6])(~[#7])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 352;
        if (cs.countSubstructure("[#6](~[#6])(~[#8])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 353;
        if (cs.countSubstructure("[#6](~[#6])(~[#16])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 354;
        if (cs.countSubstructure("[#6](~[#6])(~[#14])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 355;
        if (cs.countSubstructure("[#6](~[#6])(:c)") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 356;
        if (cs.countSubstructure("[#6](~[#6])(:c)(:c)") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 357;
        if (cs.countSubstructure("[#6](~[#6])(:c)(:n)") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 358;
        if (cs.countSubstructure("[#6](~[#6])(:n)") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 359;
        if (cs.countSubstructure("[#6](~[#6])(:n)(:n)") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 360;
        if (cs.countSubstructure("[#6](~[Cl])(~[Cl])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 361;
        if (cs.countSubstructure("[#6&!H0](~[Cl])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 362;
        if (cs.countSubstructure("[#6](~[Cl])(:c)") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 363;
        if (cs.countSubstructure("[#6](~[F])(~[F])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 364;
        if (cs.countSubstructure("[#6](~[F])(:c)") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 365;
        if (cs.countSubstructure("[#6&!H0](~[#7])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 366;
        if (cs.countSubstructure("[#6&!H0](~[#8])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 367;
        if (cs.countSubstructure("[#6&!H0](~[#8])(~[#8])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 368;
        if (cs.countSubstructure("[#6&!H0](~[#16])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 369;
        if (cs.countSubstructure("[#6&!H0](~[#14])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 370;
        if (cs.countSubstructure("[#6&!H0]:c") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 371;
        if (cs.countSubstructure("[#6&!H0](:c)(:c)") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 372;
        if (cs.countSubstructure("[#6&!H0](:c)(:n)") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 373;
        if (cs.countSubstructure("[#6&!H0](:n)") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 374;
        if (cs.countSubstructure("[#6H3]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 375;
        if (cs.countSubstructure("[#6](~[#7])(~[#7])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 376;
        if (cs.countSubstructure("[#6](~[#7])(:c)") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 377;
        if (cs.countSubstructure("[#6](~[#7])(:c)(:c)") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 378;
        if (cs.countSubstructure("[#6](~[#7])(:c)(:n)") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 379;
        if (cs.countSubstructure("[#6](~[#7])(:n)") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 380;
        if (cs.countSubstructure("[#6](~[#8])(~[#8])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 381;
        if (cs.countSubstructure("[#6](~[#8])(:c)") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 382;
        if (cs.countSubstructure("[#6](~[#8])(:c)(:c)") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 383;
        if (cs.countSubstructure("[#6](~[#16])(:c)") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 384;
        if (cs.countSubstructure("[#6](:c)(:c)") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 385;
        if (cs.countSubstructure("[#6](:c)(:c)(:c)") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 386;
        if (cs.countSubstructure("[#6](:c)(:c)(:n)") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 387;
        if (cs.countSubstructure("[#6](:c)(:n)") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 388;
        if (cs.countSubstructure("[#6](:c)(:n)(:n)") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 389;
        if (cs.countSubstructure("[#6](:n)(:n)") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 390;
        if (cs.countSubstructure("[#7](~[#6])(~[#6])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 391;
        if (cs.countSubstructure("[#7](~[#6])(~[#6])(~[#6])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 392;
        if (cs.countSubstructure("[#7&!H0](~[#6])(~[#6])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 393;
        if (cs.countSubstructure("[#7&!H0](~[#6])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 394;
        if (cs.countSubstructure("[#7&!H0](~[#6])(~[#7])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 395;
        if (cs.countSubstructure("[#7](~[#6])(~[#8])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 396;
        if (cs.countSubstructure("[#7](~[#6])(:c)") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 397;
        if (cs.countSubstructure("[#7](~[#6])(:c)(:c)") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 398;
        if (cs.countSubstructure("[#7&!H0](~[#7])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 399;
        if (cs.countSubstructure("[#7&!H0](:c)") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 400;
        if (cs.countSubstructure("[#7&!H0](:c)(:c)") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 401;
        if (cs.countSubstructure("[#7](~[#8])(~[#8])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 402;
        if (cs.countSubstructure("[#7](~[#8])(:o)") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 403;
        if (cs.countSubstructure("[#7](:c)(:c)") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 404;
        if (cs.countSubstructure("[#7](:c)(:c)(:c)") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 405;
        if (cs.countSubstructure("[#8](~[#6])(~[#6])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 406;
        if (cs.countSubstructure("[#8&!H0](~[#6])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 407;
        if (cs.countSubstructure("[#8](~[#6])(~[#15])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 408;
        if (cs.countSubstructure("[#8&!H0](~[#16])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 409;
        if (cs.countSubstructure("[#8](:c)(:c)") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 410;
        if (cs.countSubstructure("[#15](~[#6])(~[#6])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 411;
        if (cs.countSubstructure("[#15](~[#8])(~[#8])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 412;
        if (cs.countSubstructure("[#16](~[#6])(~[#6])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 413;
        if (cs.countSubstructure("[#16&!H0](~[#6])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 414;
        if (cs.countSubstructure("[#16](~[#6])(~[#8])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 415;
        if (cs.countSubstructure("[#14](~[#6])(~[#6])") > 0) fp[b >> 3] |= MASK[b % 8];

        /*
         * Section 5: Detailed atom neighborhoods - These bits test for the
         * presence of detailed atom neighborhood patterns, regardless of count,
         * but where bond orders are specific, bond aromaticity matches both
         * single and double bonds, and where "-", "=", and "#" matches a single
         * bond, double bond, and triple bond order, respectively.
         */

        b = 416;
        if (cs.countSubstructure("[#6]=,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 417;
        if (cs.countSubstructure("[#6]#[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 418;
        if (cs.countSubstructure("[#6]=,:[#7]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 419;
        if (cs.countSubstructure("[#6]#[#7]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 420;
        if (cs.countSubstructure("[#6]=,:[#8]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 421;
        if (cs.countSubstructure("[#6]=,:[#16]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 422;
        if (cs.countSubstructure("[#7]=,:[#7]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 423;
        if (cs.countSubstructure("[#7]=,:[#8]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 424;
        if (cs.countSubstructure("[#7]=,:[#15]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 425;
        if (cs.countSubstructure("[#15]=,:[#8]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 426;
        if (cs.countSubstructure("[#15]=,:[#15]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 427;
        if (cs.countSubstructure("[#6](#[#6])(-,:[#6])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 428;
        if (cs.countSubstructure("[#6&!H0](#[#6])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 429;
        if (cs.countSubstructure("[#6](#[#7])(-,:[#6])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 430;
        if (cs.countSubstructure("[#6](-,:[#6])(-,:[#6])(=,:[#6])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 431;
        if (cs.countSubstructure("[#6](-,:[#6])(-,:[#6])(=,:[#7])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 432;
        if (cs.countSubstructure("[#6](-,:[#6])(-,:[#6])(=,:[#8])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 433;
        if (cs.countSubstructure("[#6](-,:[#6])([Cl])(=,:[#8])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 434;
        if (cs.countSubstructure("[#6&!H0](-,:[#6])(=,:[#6])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 435;
        if (cs.countSubstructure("[#6&!H0](-,:[#6])(=,:[#7])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 436;
        if (cs.countSubstructure("[#6&!H0](-,:[#6])(=,:[#8])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 437;
        if (cs.countSubstructure("[#6](-,:[#6])(-,:[#7])(=,:[#6])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 438;
        if (cs.countSubstructure("[#6](-,:[#6])(-,:[#7])(=,:[#7])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 439;
        if (cs.countSubstructure("[#6](-,:[#6])(-,:[#7])(=,:[#8])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 440;
        if (cs.countSubstructure("[#6](-,:[#6])(-,:[#8])(=,:[#8])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 441;
        if (cs.countSubstructure("[#6](-,:[#6])(=,:[#6])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 442;
        if (cs.countSubstructure("[#6](-,:[#6])(=,:[#7])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 443;
        if (cs.countSubstructure("[#6](-,:[#6])(=,:[#8])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 444;
        if (cs.countSubstructure("[#6]([Cl])(=,:[#8])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 445;
        if (cs.countSubstructure("[#6&!H0](-,:[#7])(=,:[#6])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 446;
        if (cs.countSubstructure("[#6&!H0](=,:[#6])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 447;
        if (cs.countSubstructure("[#6&!H0](=,:[#7])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 448;
        if (cs.countSubstructure("[#6&!H0](=,:[#8])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 449;
        if (cs.countSubstructure("[#6](-,:[#7])(=,:[#6])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 450;
        if (cs.countSubstructure("[#6](-,:[#7])(=,:[#7])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 451;
        if (cs.countSubstructure("[#6](-,:[#7])(=,:[#8])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 452;
        if (cs.countSubstructure("[#6](-,:[#8])(=,:[#8])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 453;
        if (cs.countSubstructure("[#7](-,:[#6])(=,:[#6])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 454;
        if (cs.countSubstructure("[#7](-,:[#6])(=,:[#8])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 455;
        if (cs.countSubstructure("[#7](-,:[#8])(=,:[#8])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 456;
        if (cs.countSubstructure("[#15](-,:[#8])(=,:[#8])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 457;
        if (cs.countSubstructure("[#16](-,:[#6])(=,:[#8])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 458;
        if (cs.countSubstructure("[#16](-,:[#8])(=,:[#8])") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 459;
        if (cs.countSubstructure("[#16](=,:[#8])(=,:[#8])") > 0) fp[b >> 3] |= MASK[b % 8];

        /*
         * Section 6: Simple SMARTS patterns - These bits test for the presence
         * of simple SMARTS patterns, regardless of count, but where bond orders
         * are specific and bond aromaticity matches both single and double
         * bonds.
         */
        b = 460;
        if (cs.countSubstructure("[#6]-,:[#6]-,:[#6]#[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 461;
        if (cs.countSubstructure("[#8]-,:[#6]-,:[#6]=,:[#7]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 462;
        if (cs.countSubstructure("[#8]-,:[#6]-,:[#6]=,:[#8]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 463;
        if (cs.countSubstructure("[#7]:[#6]-,:[#16&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 464;
        if (cs.countSubstructure("[#7]-,:[#6]-,:[#6]=,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 465;
        if (cs.countSubstructure("[#8]=,:[#16]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 466;
        if (cs.countSubstructure("[#7]#[#6]-,:[#6]=,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 467;
        if (cs.countSubstructure("[#6]=,:[#7]-,:[#7]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 468;
        if (cs.countSubstructure("[#8]=,:[#16]-,:[#6]-,:[#7]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 469;
        if (cs.countSubstructure("[#16]-,:[#16]-,:[#6]:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 470;
        if (cs.countSubstructure("[#6]:[#6]-,:[#6]=,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 471;
        if (cs.countSubstructure("[#16]:[#6]:[#6]:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 472;
        if (cs.countSubstructure("[#6]:[#7]:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 473;
        if (cs.countSubstructure("[#16]-,:[#6]:[#7]:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 474;
        if (cs.countSubstructure("[#16]:[#6]:[#6]:[#7]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 475;
        if (cs.countSubstructure("[#16]-,:[#6]=,:[#7]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 476;
        if (cs.countSubstructure("[#6]-,:[#8]-,:[#6]=,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 477;
        if (cs.countSubstructure("[#7]-,:[#7]-,:[#6]:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 478;
        if (cs.countSubstructure("[#16]-,:[#6]=,:[#7&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 479;
        if (cs.countSubstructure("[#16]-,:[#6]-,:[#16]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 480;
        if (cs.countSubstructure("[#6]:[#16]:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 481;
        if (cs.countSubstructure("[#8]-,:[#16]-,:[#6]:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 482;
        if (cs.countSubstructure("[#6]:[#7]-,:[#6]:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 483;
        if (cs.countSubstructure("[#7]-,:[#16]-,:[#6]:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 484;
        if (cs.countSubstructure("[#7]-,:[#6]:[#7]:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 485;
        if (cs.countSubstructure("[#7]:[#6]:[#6]:[#7]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 486;
        if (cs.countSubstructure("[#7]-,:[#6]:[#7]:[#7]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 487;
        if (cs.countSubstructure("[#7]-,:[#6]=,:[#7]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 488;
        if (cs.countSubstructure("[#7]-,:[#6]=,:[#7&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 489;
        if (cs.countSubstructure("[#7]-,:[#6]-,:[#16]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 490;
        if (cs.countSubstructure("[#6]-,:[#6]-,:[#6]=,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 491;
        if (cs.countSubstructure("[#6]-,:[#7]:[#6&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 492;
        if (cs.countSubstructure("[#7]-,:[#6]:[#8]:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 493;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#6]:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 494;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#6]:[#7]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 495;
        if (cs.countSubstructure("[#6]-,:[#7]-,:[#6]:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 496;
        if (cs.countSubstructure("[#7]:[#7]-,:[#6&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 497;
        if (cs.countSubstructure("[#8]-,:[#6]:[#6]:[#7]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 498;
        if (cs.countSubstructure("[#8]-,:[#6]=,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 499;
        if (cs.countSubstructure("[#7]-,:[#6]:[#6]:[#7]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 500;
        if (cs.countSubstructure("[#6]-,:[#16]-,:[#6]:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 501;
        if (cs.countSubstructure("[Cl]-,:[#6]:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 502;
        if (cs.countSubstructure("[#7]-,:[#6]=,:[#6&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 503;
        if (cs.countSubstructure("[Cl]-,:[#6]:[#6&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 504;
        if (cs.countSubstructure("[#7]:[#6]:[#7]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 505;
        if (cs.countSubstructure("[Cl]-,:[#6]:[#6]-,:[#8]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 506;
        if (cs.countSubstructure("[#6]-,:[#6]:[#7]:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 507;
        if (cs.countSubstructure("[#6]-,:[#6]-,:[#16]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 508;
        if (cs.countSubstructure("[#16]=,:[#6]-,:[#7]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 509;
        if (cs.countSubstructure("[Br]-,:[#6]:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 510;
        if (cs.countSubstructure("[#7&!H0]-,:[#7&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 511;
        if (cs.countSubstructure("[#16]=,:[#6]-,:[#7&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 512;
        if (cs.countSubstructure("[#6]-,:[#33]-[#8&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 513;
        if (cs.countSubstructure("[#16]:[#6]:[#6&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 514;
        if (cs.countSubstructure("[#8]-,:[#7]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 515;
        if (cs.countSubstructure("[#7]-,:[#7]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 516;
        if (cs.countSubstructure("[#6H,#6H2,#6H3]=,:[#6H,#6H2,#6H3]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 517;
        if (cs.countSubstructure("[#7]-,:[#7]-,:[#6]-,:[#7]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 518;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#7]-,:[#7]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 519;
        if (cs.countSubstructure("[#7]=,:[#6]-,:[#7]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 520;
        if (cs.countSubstructure("[#6]=,:[#6]-,:[#6]:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 521;
        if (cs.countSubstructure("[#6]:[#7]-,:[#6&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 522;
        if (cs.countSubstructure("[#6]-,:[#7]-,:[#7&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 523;
        if (cs.countSubstructure("[#7]:[#6]:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 524;
        if (cs.countSubstructure("[#6]-,:[#6]=,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 525;
        if (cs.countSubstructure("[#33]-,:[#6]:[#6&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 526;
        if (cs.countSubstructure("[Cl]-,:[#6]:[#6]-,:[Cl]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 527;
        if (cs.countSubstructure("[#6]:[#6]:[#7&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 528;
        if (cs.countSubstructure("[#7&!H0]-,:[#6&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 529;
        if (cs.countSubstructure("[Cl]-,:[#6]-,:[#6]-,:[Cl]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 530;
        if (cs.countSubstructure("[#7]:[#6]-,:[#6]:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 531;
        if (cs.countSubstructure("[#16]-,:[#6]:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 532;
        if (cs.countSubstructure("[#16]-,:[#6]:[#6&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 533;
        if (cs.countSubstructure("[#16]-,:[#6]:[#6]-,:[#7]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 534;
        if (cs.countSubstructure("[#16]-,:[#6]:[#6]-,:[#8]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 535;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 536;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#6]-,:[#7]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 537;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#6]-,:[#8]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 538;
        if (cs.countSubstructure("[#7]=,:[#6]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 539;
        if (cs.countSubstructure("[#7]=,:[#6]-,:[#6&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 540;
        if (cs.countSubstructure("[#6]-,:[#7]-,:[#6&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 541;
        if (cs.countSubstructure("[#8]-,:[#6]:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 542;
        if (cs.countSubstructure("[#8]-,:[#6]:[#6&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 543;
        if (cs.countSubstructure("[#8]-,:[#6]:[#6]-,:[#7]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 544;
        if (cs.countSubstructure("[#8]-,:[#6]:[#6]-,:[#8]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 545;
        if (cs.countSubstructure("[#7]-,:[#6]:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 546;
        if (cs.countSubstructure("[#7]-,:[#6]:[#6&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 547;
        if (cs.countSubstructure("[#7]-,:[#6]:[#6]-,:[#7]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 548;
        if (cs.countSubstructure("[#8]-,:[#6]-,:[#6]:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 549;
        if (cs.countSubstructure("[#7]-,:[#6]-,:[#6]:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 550;
        if (cs.countSubstructure("[Cl]-,:[#6]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 551;
        if (cs.countSubstructure("[Cl]-,:[#6]-,:[#6]-,:[#8]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 552;
        if (cs.countSubstructure("[#6]:[#6]-,:[#6]:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 553;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#6]=,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 554;
        if (cs.countSubstructure("[Br]-,:[#6]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 555;
        if (cs.countSubstructure("[#7]=,:[#6]-,:[#6]=,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 556;
        if (cs.countSubstructure("[#6]=,:[#6]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 557;
        if (cs.countSubstructure("[#7]:[#6]-,:[#8&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 558;
        if (cs.countSubstructure("[#8]=,:[#7]-,:c:c") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 559;
        if (cs.countSubstructure("[#8]-,:[#6]-,:[#7&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 560;
        if (cs.countSubstructure("[#7]-,:[#6]-,:[#7]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 561;
        if (cs.countSubstructure("[Cl]-,:[#6]-,:[#6]=,:[#8]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 562;
        if (cs.countSubstructure("[Br]-,:[#6]-,:[#6]=,:[#8]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 563;
        if (cs.countSubstructure("[#8]-,:[#6]-,:[#8]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 564;
        if (cs.countSubstructure("[#6]=,:[#6]-,:[#6]=,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 565;
        if (cs.countSubstructure("[#6]:[#6]-,:[#8]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 566;
        if (cs.countSubstructure("[#8]-,:[#6]-,:[#6]-,:[#7]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 567;
        if (cs.countSubstructure("[#8]-,:[#6]-,:[#6]-,:[#8]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 568;
        if (cs.countSubstructure("N#[#6]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 569;
        if (cs.countSubstructure("[#7]-,:[#6]-,:[#6]-,:[#7]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 570;
        if (cs.countSubstructure("[#6]:[#6]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 571;
        if (cs.countSubstructure("[#6&!H0]-,:[#8&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 572;
        if (cs.countSubstructure("n:c:n:c") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 573;
        if (cs.countSubstructure("[#8]-,:[#6]-,:[#6]=,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 574;
        if (cs.countSubstructure("[#8]-,:[#6]-,:[#6]:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 575;
        if (cs.countSubstructure("[#8]-,:[#6]-,:[#6]:[#6]-,:[#8]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 576;
        if (cs.countSubstructure("[#7]=,:[#6]-,:[#6]:[#6&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 577;
        if (cs.countSubstructure("c:c-,:[#7]-,:c:c") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 578;
        if (cs.countSubstructure("[#6]-,:[#6]:[#6]-,:c:c") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 579;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#6]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 580;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#6]-,:[#6]-,:[#7]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 581;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#6]-,:[#6]-,:[#8]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 582;
        if (cs.countSubstructure("[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 583;
        if (cs.countSubstructure("[Cl]-,:[#6]:[#6]-,:[#8]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 584;
        if (cs.countSubstructure("c:c-,:[#6]=,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 585;
        if (cs.countSubstructure("[#6]-,:[#6]:[#6]-,:[#7]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 586;
        if (cs.countSubstructure("[#6]-,:[#16]-,:[#6]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 587;
        if (cs.countSubstructure("[#7]-,:[#6]:[#6]-,:[#8&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 588;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#6]-,:[#6]=,:[#8]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 589;
        if (cs.countSubstructure("[#6]-,:[#6]:[#6]-,:[#8]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 590;
        if (cs.countSubstructure("[#6]-,:[#6]:[#6]-,:[#8&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 591;
        if (cs.countSubstructure("[Cl]-,:[#6]-,:[#6]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 592;
        if (cs.countSubstructure("[#7]-,:[#6]-,:[#6]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 593;
        if (cs.countSubstructure("[#7]-,:[#6]-,:[#6]-,:[#6]-,:[#7]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 594;
        if (cs.countSubstructure("[#6]-,:[#8]-,:[#6]-,:[#6]=,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 595;
        if (cs.countSubstructure("c:c-,:[#6]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 596;
        if (cs.countSubstructure("[#7]=,:[#6]-,:[#7]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 597;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#6]-,:c:c") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 598;
        if (cs.countSubstructure("[Cl]-,:[#6]:[#6]:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 599;
        if (cs.countSubstructure("[#6H,#6H2,#6H3]-,:[#6]=,:[#6H,#6H2,#6H3]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 600;
        if (cs.countSubstructure("[#7]-,:[#6]:[#6]:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 601;
        if (cs.countSubstructure("[#7]-,:[#6]:[#6]:[#6]-,:[#7]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 602;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#6]-,:[#7]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 603;
        if (cs.countSubstructure("[#6]-,:c:c:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 604;
        if (cs.countSubstructure("[#6]-,:[#8]-,:[#6]-,:[#6]:c") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 605;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#6]-,:[#8]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 606;
        if (cs.countSubstructure("[#8]-,:[#6]:[#6]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 607;
        if (cs.countSubstructure("[#7]-,:[#6]-,:[#6]-,:[#6]:c") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 608;
        if (cs.countSubstructure("[#6]-,:[#6]-,:[#6]-,:[#6]:c") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 609;
        if (cs.countSubstructure("[Cl]-,:[#6]-,:[#6]-,:[#7]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 610;
        if (cs.countSubstructure("[#6]-,:[#8]-,:[#6]-,:[#8]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 611;
        if (cs.countSubstructure("[#7]-,:[#6]-,:[#6]-,:[#7]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 612;
        if (cs.countSubstructure("[#7]-,:[#6]-,:[#8]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 613;
        if (cs.countSubstructure("[#6]-,:[#7]-,:[#6]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 614;
        if (cs.countSubstructure("[#6]-,:[#6]-,:[#8]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 615;
        if (cs.countSubstructure("[#7]-,:[#6]-,:[#6]-,:[#8]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 616;
        if (cs.countSubstructure("c:c:n:n:c") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 617;
        if (cs.countSubstructure("[#6]-,:[#6]-,:[#6]-,:[#8&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 618;
        if (cs.countSubstructure("c:[#6]-,:[#6]-,:[#6]:c") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 619;
        if (cs.countSubstructure("[#8]-,:[#6]-,:[#6]=,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 620;
        if (cs.countSubstructure("c:c-,:[#8]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 621;
        if (cs.countSubstructure("[#7]-,:[#6]:c:c:n") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 622;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#8]-,:[#6]:c") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 623;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#6]:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 624;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#6]:[#6]-,:[#7]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 625;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#6]:[#6]-,:[#8]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 626;
        if (cs.countSubstructure("[#6]-,:[#8]-,:[#6]:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 627;
        if (cs.countSubstructure("[#8]=,:[#33]-,:[#6]:c:c") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 628;
        if (cs.countSubstructure("[#6]-,:[#7]-,:[#6]-,:[#6]:c") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 629;
        if (cs.countSubstructure("[#16]-,:[#6]:c:c-,:[#7]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 630;
        if (cs.countSubstructure("[#8]-,:[#6]:[#6]-,:[#8]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 631;
        if (cs.countSubstructure("[#8]-,:[#6]:[#6]-,:[#8&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 632;
        if (cs.countSubstructure("[#6]-,:[#6]-,:[#8]-,:[#6]:c") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 633;
        if (cs.countSubstructure("[#7]-,:[#6]-,:[#6]:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 634;
        if (cs.countSubstructure("[#6]-,:[#6]-,:[#6]:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 635;
        if (cs.countSubstructure("[#7]-,:[#7]-,:[#6]-,:[#7&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 636;
        if (cs.countSubstructure("[#6]-,:[#7]-,:[#6]-,:[#7]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 637;
        if (cs.countSubstructure("[#8]-,:[#6]-,:[#6]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 638;
        if (cs.countSubstructure("[#8]-,:[#6]-,:[#6]-,:[#6]-,:[#7]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 639;
        if (cs.countSubstructure("[#8]-,:[#6]-,:[#6]-,:[#6]-,:[#8]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 640;
        if (cs.countSubstructure("[#6]=,:[#6]-,:[#6]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 641;
        if (cs.countSubstructure("[#8]-,:[#6]-,:[#6]-,:[#6]=,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 642;
        if (cs.countSubstructure("[#8]-,:[#6]-,:[#6]-,:[#6]=,:[#8]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 643;
        if (cs.countSubstructure("[#6&!H0]-,:[#6]-,:[#7&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 644;
        if (cs.countSubstructure("[#6]-,:[#6]=,:[#7]-,:[#7]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 645;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#7]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 646;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#7]-,:[#6&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 647;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#7]-,:[#6]-,:[#7]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 648;
        if (cs.countSubstructure("[#8]=,:[#7]-,:[#6]:[#6]-,:[#7]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 649;
        if (cs.countSubstructure("[#8]=,:[#7]-,:c:c-,:[#8]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 650;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#7]-,:[#6]=,:[#8]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 651;
        if (cs.countSubstructure("[#8]-,:[#6]:[#6]:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 652;
        if (cs.countSubstructure("[#8]-,:[#6]:[#6]:[#6]-,:[#7]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 653;
        if (cs.countSubstructure("[#8]-,:[#6]:[#6]:[#6]-,:[#8]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 654;
        if (cs.countSubstructure("[#7]-,:[#6]-,:[#7]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 655;
        if (cs.countSubstructure("[#8]-,:[#6]-,:[#6]-,:[#6]:c") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 656;
        if (cs.countSubstructure("[#6]-,:[#6]-,:[#7]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 657;
        if (cs.countSubstructure("[#6]-,:[#7]-,:[#6]:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 658;
        if (cs.countSubstructure("[#6]-,:[#6]-,:[#16]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 659;
        if (cs.countSubstructure("[#8]-,:[#6]-,:[#6]-,:[#7]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 660;
        if (cs.countSubstructure("[#6]-,:[#6]=,:[#6]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 661;
        if (cs.countSubstructure("[#8]-,:[#6]-,:[#8]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 662;
        if (cs.countSubstructure("[#8]-,:[#6]-,:[#6]-,:[#8]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 663;
        if (cs.countSubstructure("[#8]-,:[#6]-,:[#6]-,:[#8&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 664;
        if (cs.countSubstructure("[#6]-,:[#6]=,:[#6]-,:[#6]=,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 665;
        if (cs.countSubstructure("[#7]-,:[#6]:[#6]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 666;
        if (cs.countSubstructure("[#6]=,:[#6]-,:[#6]-,:[#8]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 667;
        if (cs.countSubstructure("[#6]=,:[#6]-,:[#6]-,:[#8&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 668;
        if (cs.countSubstructure("[#6]-,:[#6]:[#6]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 669;
        if (cs.countSubstructure("[Cl]-,:[#6]:[#6]-,:[#6]=,:[#8]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 670;
        if (cs.countSubstructure("[Br]-,:[#6]:c:c-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 671;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#6]=,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 672;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#6]=,:[#6&!H0]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 673;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#6]=,:[#6]-,:[#7]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 674;
        if (cs.countSubstructure("[#7]-,:[#6]-,:[#7]-,:[#6]:c") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 675;
        if (cs.countSubstructure("[Br]-,:[#6]-,:[#6]-,:[#6]:c") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 676;
        if (cs.countSubstructure("[#7]#[#6]-,:[#6]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 677;
        if (cs.countSubstructure("[#6]-,:[#6]=,:[#6]-,:[#6]:c") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 678;
        if (cs.countSubstructure("[#6]-,:[#6]-,:[#6]=,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 679;
        if (cs.countSubstructure("[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 680;
        if (cs.countSubstructure("[#8]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 681;
        if (cs.countSubstructure("[#8]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#8]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 682;
        if (cs.countSubstructure("[#8]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#7]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 683;
        if (cs.countSubstructure("[#7]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 684;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 685;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#7]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 686;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#8]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 687;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#6]-,:[#6]-,:[#6]=,:[#8]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 688;
        if (cs.countSubstructure("[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 689;
        if (cs.countSubstructure("[#8]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 690;
        if (cs.countSubstructure("[#8]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#8]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 691;
        if (cs.countSubstructure("[#8]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#7]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 692;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 693;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#8]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 694;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]=,:[#8]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 695;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#7]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 696;
        if (cs.countSubstructure("[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]") > 0)
            fp[b >> 3] |= MASK[b % 8];
        b = 697;
        if (cs.countSubstructure("[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6](-,:[#6])-,:[#6]") > 0)
            fp[b >> 3] |= MASK[b % 8];
        b = 698;
        if (cs.countSubstructure("[#8]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]") > 0)
            fp[b >> 3] |= MASK[b % 8];
        b = 699;
        if (cs.countSubstructure("[#8]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6](-,:[#6])-,:[#6]") > 0)
            fp[b >> 3] |= MASK[b % 8];
        b = 700;
        if (cs.countSubstructure("[#8]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#8]-,:[#6]") > 0)
            fp[b >> 3] |= MASK[b % 8];
        b = 701;
        if (cs.countSubstructure("[#8]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6](-,:[#8])-,:[#6]") > 0)
            fp[b >> 3] |= MASK[b % 8];
        b = 702;
        if (cs.countSubstructure("[#8]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#7]-,:[#6]") > 0)
            fp[b >> 3] |= MASK[b % 8];
        b = 703;
        if (cs.countSubstructure("[#8]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6](-,:[#7])-,:[#6]") > 0)
            fp[b >> 3] |= MASK[b % 8];
        b = 704;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6]") > 0)
            fp[b >> 3] |= MASK[b % 8];
        b = 705;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6](-,:[#8])-,:[#6]") > 0)
            fp[b >> 3] |= MASK[b % 8];
        b = 706;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6](=,:[#8])-,:[#6]") > 0)
            fp[b >> 3] |= MASK[b % 8];
        b = 707;
        if (cs.countSubstructure("[#8]=,:[#6]-,:[#6]-,:[#6]-,:[#6]-,:[#6](-,:[#7])-,:[#6]") > 0)
            fp[b >> 3] |= MASK[b % 8];
        b = 708;
        if (cs.countSubstructure("[#6]-,:[#6](-,:[#6])-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 709;
        if (cs.countSubstructure("[#6]-,:[#6](-,:[#6])-,:[#6]-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 710;
        if (cs.countSubstructure("[#6]-,:[#6]-,:[#6](-,:[#6])-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 711;
        if (cs.countSubstructure("[#6]-,:[#6](-,:[#6])(-,:[#6])-,:[#6]-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 712;
        if (cs.countSubstructure("[#6]-,:[#6](-,:[#6])-,:[#6](-,:[#6])-,:[#6]") > 0) fp[b >> 3] |= MASK[b % 8];

        /*
         * Section 7: Complex SMARTS patterns - These bits test for the presence
         * of complex SMARTS patterns, regardless of count, but where bond
         * orders and bond aromaticity are specific.
         */

        b = 713;
        if (cs.countSubstructure("[#6]c1ccc([#6])cc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 714;
        if (cs.countSubstructure("[#6]c1ccc([#8])cc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 715;
        if (cs.countSubstructure("[#6]c1ccc([#16])cc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 716;
        if (cs.countSubstructure("[#6]c1ccc([#7])cc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 717;
        if (cs.countSubstructure("[#6]c1ccc(Cl)cc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 718;
        if (cs.countSubstructure("[#6]c1ccc(Br)cc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 719;
        if (cs.countSubstructure("[#8]c1ccc([#8])cc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 720;
        if (cs.countSubstructure("[#8]c1ccc([#16])cc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 721;
        if (cs.countSubstructure("[#8]c1ccc([#7])cc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 722;
        if (cs.countSubstructure("[#8]c1ccc(Cl)cc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 723;
        if (cs.countSubstructure("[#8]c1ccc(Br)cc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 724;
        if (cs.countSubstructure("[#16]c1ccc([#16])cc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 725;
        if (cs.countSubstructure("[#16]c1ccc([#7])cc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 726;
        if (cs.countSubstructure("[#16]c1ccc(Cl)cc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 727;
        if (cs.countSubstructure("[#16]c1ccc(Br)cc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 728;
        if (cs.countSubstructure("[#7]c1ccc([#7])cc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 729;
        if (cs.countSubstructure("[#7]c1ccc(Cl)cc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 730;
        if (cs.countSubstructure("[#7]c1ccc(Br)cc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 731;
        if (cs.countSubstructure("Clc1ccc(Cl)cc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 732;
        if (cs.countSubstructure("Clc1ccc(Br)cc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 733;
        if (cs.countSubstructure("Brc1ccc(Br)cc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 734;
        if (cs.countSubstructure("[#6]c1cc([#6])ccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 735;
        if (cs.countSubstructure("[#6]c1cc([#8])ccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 736;
        if (cs.countSubstructure("[#6]c1cc([#16])ccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 737;
        if (cs.countSubstructure("[#6]c1cc([#7])ccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 738;
        if (cs.countSubstructure("[#6]c1cc(Cl)ccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 739;
        if (cs.countSubstructure("[#6]c1cc(Br)ccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 740;
        if (cs.countSubstructure("[#8]c1cc([#8])ccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 741;
        if (cs.countSubstructure("[#8]c1cc([#16])ccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 742;
        if (cs.countSubstructure("[#8]c1cc([#7])ccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 743;
        if (cs.countSubstructure("[#8]c1cc(Cl)ccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 744;
        if (cs.countSubstructure("[#8]c1cc(Br)ccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 745;
        if (cs.countSubstructure("[#16]c1cc([#16])ccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 746;
        if (cs.countSubstructure("[#16]c1cc([#7])ccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 747;
        if (cs.countSubstructure("[#16]c1cc(Cl)ccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 748;
        if (cs.countSubstructure("[#16]c1cc(Br)ccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 749;
        if (cs.countSubstructure("[#7]c1cc([#7])ccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 750;
        if (cs.countSubstructure("[#7]c1cc(Cl)ccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 751;
        if (cs.countSubstructure("[#7]c1cc(Br)ccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 752;
        if (cs.countSubstructure("Clc1cc(Cl)ccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 753;
        if (cs.countSubstructure("Clc1cc(Br)ccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 754;
        if (cs.countSubstructure("Brc1cc(Br)ccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 755;
        if (cs.countSubstructure("[#6]c1c([#6])cccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 756;
        if (cs.countSubstructure("[#6]c1c([#8])cccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 757;
        if (cs.countSubstructure("[#6]c1c([#16])cccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 758;
        if (cs.countSubstructure("[#6]c1c([#7])cccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 759;
        if (cs.countSubstructure("[#6]c1c(Cl)cccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 760;
        if (cs.countSubstructure("[#6]c1c(Br)cccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 761;
        if (cs.countSubstructure("[#8]c1c([#8])cccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 762;
        if (cs.countSubstructure("[#8]c1c([#16])cccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 763;
        if (cs.countSubstructure("[#8]c1c([#7])cccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 764;
        if (cs.countSubstructure("[#8]c1c(Cl)cccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 765;
        if (cs.countSubstructure("[#8]c1c(Br)cccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 766;
        if (cs.countSubstructure("[#16]c1c([#16])cccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 767;
        if (cs.countSubstructure("[#16]c1c([#7])cccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 768;
        if (cs.countSubstructure("[#16]c1c(Cl)cccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 769;
        if (cs.countSubstructure("[#16]c1c(Br)cccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 770;
        if (cs.countSubstructure("[#7]c1c([#7])cccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 771;
        if (cs.countSubstructure("[#7]c1c(Cl)cccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 772;
        if (cs.countSubstructure("[#7]c1c(Br)cccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 773;
        if (cs.countSubstructure("Clc1c(Cl)cccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 774;
        if (cs.countSubstructure("Clc1c(Br)cccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 775;
        if (cs.countSubstructure("Brc1c(Br)cccc1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 776;
        if (cs.countSubstructure("[#6][#6]1[#6][#6][#6]([#6])[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 777;
        if (cs.countSubstructure("[#6][#6]1[#6][#6][#6]([#8])[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 778;
        if (cs.countSubstructure("[#6][#6]1[#6][#6][#6]([#16])[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 779;
        if (cs.countSubstructure("[#6][#6]1[#6][#6][#6]([#7])[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 780;
        if (cs.countSubstructure("[#6][#6]1[#6][#6][#6](Cl)[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 781;
        if (cs.countSubstructure("[#6][#6]1[#6][#6][#6](Br)[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 782;
        if (cs.countSubstructure("[#8][#6]1[#6][#6][#6]([#8])[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 783;
        if (cs.countSubstructure("[#8][#6]1[#6][#6][#6]([#16])[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 784;
        if (cs.countSubstructure("[#8][#6]1[#6][#6][#6]([#7])[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 785;
        if (cs.countSubstructure("[#8][#6]1[#6][#6][#6](Cl)[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 786;
        if (cs.countSubstructure("[#8][#6]1[#6][#6][#6](Br)[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 787;
        if (cs.countSubstructure("[#16][#6]1[#6][#6][#6]([#16])[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 788;
        if (cs.countSubstructure("[#16][#6]1[#6][#6][#6]([#7])[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 789;
        if (cs.countSubstructure("[#16][#6]1[#6][#6][#6](Cl)[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 790;
        if (cs.countSubstructure("[#16][#6]1[#6][#6][#6](Br)[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 791;
        if (cs.countSubstructure("[#7][#6]1[#6][#6][#6]([#7])[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 792;
        if (cs.countSubstructure("[#7][#6]1[#6][#6][#6](Cl)[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 793;
        if (cs.countSubstructure("[#7][#6]1[#6][#6][#6](Br)[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 794;
        if (cs.countSubstructure("Cl[#6]1[#6][#6][#6](Cl)[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 795;
        if (cs.countSubstructure("Cl[#6]1[#6][#6][#6](Br)[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 796;
        if (cs.countSubstructure("Br[#6]1[#6][#6][#6](Br)[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 797;
        if (cs.countSubstructure("[#6][#6]1[#6][#6]([#6])[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 798;
        if (cs.countSubstructure("[#6][#6]1[#6][#6]([#8])[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 799;
        if (cs.countSubstructure("[#6][#6]1[#6][#6]([#16])[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 800;
        if (cs.countSubstructure("[#6][#6]1[#6][#6]([#7])[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 801;
        if (cs.countSubstructure("[#6][#6]1[#6][#6](Cl)[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 802;
        if (cs.countSubstructure("[#6][#6]1[#6][#6](Br)[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 803;
        if (cs.countSubstructure("[#8][#6]1[#6][#6]([#8])[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 804;
        if (cs.countSubstructure("[#8][#6]1[#6][#6]([#16])[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 805;
        if (cs.countSubstructure("[#8][#6]1[#6][#6]([#7])[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 806;
        if (cs.countSubstructure("[#8][#6]1[#6][#6](Cl)[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 807;
        if (cs.countSubstructure("[#8][#6]1[#6][#6](Br)[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 808;
        if (cs.countSubstructure("[#16][#6]1[#6][#6]([#16])[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 809;
        if (cs.countSubstructure("[#16][#6]1[#6][#6]([#7])[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 810;
        if (cs.countSubstructure("[#16][#6]1[#6][#6](Cl)[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 811;
        if (cs.countSubstructure("[#16][#6]1[#6][#6](Br)[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 812;
        if (cs.countSubstructure("[#7][#6]1[#6][#6]([#7])[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 813;
        if (cs.countSubstructure("[#7][#6]1[#6][#6](Cl)[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 814;
        if (cs.countSubstructure("[#7][#6]1[#6][#6](Br)[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 815;
        if (cs.countSubstructure("Cl[#6]1[#6][#6](Cl)[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 816;
        if (cs.countSubstructure("Cl[#6]1[#6][#6](Br)[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 817;
        if (cs.countSubstructure("Br[#6]1[#6][#6](Br)[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 818;
        if (cs.countSubstructure("[#6][#6]1[#6]([#6])[#6][#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 819;
        if (cs.countSubstructure("[#6][#6]1[#6]([#8])[#6][#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 820;
        if (cs.countSubstructure("[#6][#6]1[#6]([#16])[#6][#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 821;
        if (cs.countSubstructure("[#6][#6]1[#6]([#7])[#6][#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 822;
        if (cs.countSubstructure("[#6][#6]1[#6](Cl)[#6][#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 823;
        if (cs.countSubstructure("[#6][#6]1[#6](Br)[#6][#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 824;
        if (cs.countSubstructure("[#8][#6]1[#6]([#8])[#6][#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 825;
        if (cs.countSubstructure("[#8][#6]1[#6]([#16])[#6][#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 826;
        if (cs.countSubstructure("[#8][#6]1[#6]([#7])[#6][#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 827;
        if (cs.countSubstructure("[#8][#6]1[#6](Cl)[#6][#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 828;
        if (cs.countSubstructure("[#8][#6]1[#6](Br)[#6][#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 829;
        if (cs.countSubstructure("[#16][#6]1[#6]([#16])[#6][#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 830;
        if (cs.countSubstructure("[#16][#6]1[#6]([#7])[#6][#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 831;
        if (cs.countSubstructure("[#16][#6]1[#6](Cl)[#6][#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 832;
        if (cs.countSubstructure("[#16][#6]1[#6](Br)[#6][#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 833;
        if (cs.countSubstructure("[#7][#6]1[#6]([#7])[#6][#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 834;
        if (cs.countSubstructure("[#7][#6]1[#6](Cl)[#6][#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 835;
        if (cs.countSubstructure("[#7][#6]1[#6](Br)[#6][#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 836;
        if (cs.countSubstructure("Cl[#6]1[#6](Cl)[#6][#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 837;
        if (cs.countSubstructure("Cl[#6]1[#6](Br)[#6][#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 838;
        if (cs.countSubstructure("Br[#6]1[#6](Br)[#6][#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 839;
        if (cs.countSubstructure("[#6][#6]1[#6][#6]([#6])[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 840;
        if (cs.countSubstructure("[#6][#6]1[#6][#6]([#8])[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 841;
        if (cs.countSubstructure("[#6][#6]1[#6][#6]([#16])[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 842;
        if (cs.countSubstructure("[#6][#6]1[#6][#6]([#7])[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 843;
        if (cs.countSubstructure("[#6][#6]1[#6][#6](Cl)[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 844;
        if (cs.countSubstructure("[#6][#6]1[#6][#6](Br)[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 845;
        if (cs.countSubstructure("[#8][#6]1[#6][#6]([#8])[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 846;
        if (cs.countSubstructure("[#8][#6]1[#6][#6]([#16])[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 847;
        if (cs.countSubstructure("[#8][#6]1[#6][#6]([#7])[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 848;
        if (cs.countSubstructure("[#8][#6]1[#6][#6](Cl)[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 849;
        if (cs.countSubstructure("[#8][#6]1[#6][#6](Br)[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 850;
        if (cs.countSubstructure("[#16][#6]1[#6][#6]([#16])[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 851;
        if (cs.countSubstructure("[#16][#6]1[#6][#6]([#7])[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 852;
        if (cs.countSubstructure("[#16][#6]1[#6][#6](Cl)[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 853;
        if (cs.countSubstructure("[#16][#6]1[#6][#6](Br)[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 854;
        if (cs.countSubstructure("[#7][#6]1[#6][#6]([#7])[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 855;
        if (cs.countSubstructure("[#7][#6]1[#6][#6](Cl)[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 856;
        if (cs.countSubstructure("[#7][#6]1[#6][#6](Br)[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 857;
        if (cs.countSubstructure("Cl[#6]1[#6][#6](Cl)[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 858;
        if (cs.countSubstructure("Cl[#6]1[#6][#6](Br)[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 859;
        if (cs.countSubstructure("Br[#6]1[#6][#6](Br)[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 860;
        if (cs.countSubstructure("[#6][#6]1[#6]([#6])[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 861;
        if (cs.countSubstructure("[#6][#6]1[#6]([#8])[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 862;
        if (cs.countSubstructure("[#6][#6]1[#6]([#16])[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 863;
        if (cs.countSubstructure("[#6][#6]1[#6]([#7])[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 864;
        if (cs.countSubstructure("[#6][#6]1[#6](Cl)[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 865;
        if (cs.countSubstructure("[#6][#6]1[#6](Br)[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 866;
        if (cs.countSubstructure("[#8][#6]1[#6]([#8])[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 867;
        if (cs.countSubstructure("[#8][#6]1[#6]([#16])[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 868;
        if (cs.countSubstructure("[#8][#6]1[#6]([#7])[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 869;
        if (cs.countSubstructure("[#8][#6]1[#6](Cl)[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 870;
        if (cs.countSubstructure("[#8][#6]1[#6](Br)[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 871;
        if (cs.countSubstructure("[#16][#6]1[#6]([#16])[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 872;
        if (cs.countSubstructure("[#16][#6]1[#6]([#7])[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 873;
        if (cs.countSubstructure("[#16][#6]1[#6](Cl)[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 874;
        if (cs.countSubstructure("[#16][#6]1[#6](Br)[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 875;
        if (cs.countSubstructure("[#7][#6]1[#6]([#7])[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 876;
        if (cs.countSubstructure("[#7][#6]1[#6](Cl)[#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 877;
        if (cs.countSubstructure("[#7][#6]1[#6](Br)[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 878;
        if (cs.countSubstructure("Cl[#6]1[#6](Cl)[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 879;
        if (cs.countSubstructure("Cl[#6]1[#6](Br)[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
        b = 880;
        if (cs.countSubstructure("Br[#6]1[#6](Br)[#6][#6][#6]1") > 0) fp[b >> 3] |= MASK[b % 8];
    }

    /** {@inheritDoc} */
    @Override
    public ICountFingerprint getCountFingerprint(IAtomContainer container) throws CDKException {
        throw new UnsupportedOperationException();
    }

}
