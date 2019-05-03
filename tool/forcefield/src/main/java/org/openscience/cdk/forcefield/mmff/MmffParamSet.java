/*
 * Copyright (c) 2015 John May <jwmay@users.sf.net>
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

package org.openscience.cdk.forcefield.mmff;

import com.google.common.base.Charsets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Internal class for accessing MMFF parameters.
 * 
 * @author John May
 */
enum MmffParamSet {
    
    INSTANCE;

    private static final int MAX_MMFF_ATOMTYPE = 99;

   
    /**
     * Bond charge increments.
     */
    private Map<BondKey, BigDecimal> bcis = new HashMap<>();

    /**
     * Atom type properties.
     */
    private MmffProp[] properties = new MmffProp[MAX_MMFF_ATOMTYPE + 1];

    private Map<String, Integer> typeMap = new HashMap<>();

    /**
     * Symbolic formal charges - some are varible and assigned in code.
     */
    private Map<String, BigDecimal> fCharges = new HashMap<>();

    MmffParamSet() {
        try (InputStream in = getClass().getResourceAsStream("MMFFCHG.PAR")) {
            parseMMFFCHARGE(in, bcis);
        } catch (IOException e) {
            throw new InternalError("Could not load MMFFCHG.PAR");
        }
        try (InputStream in = getClass().getResourceAsStream("MMFFFORMCHG.PAR")) {
            parseMMFFFORMCHG(in, fCharges);
        } catch (IOException e) {
            throw new InternalError("Could not load MMFFFORMCHG.PAR");
        }
        try (InputStream in = getClass().getResourceAsStream("MMFFPROP.PAR")) {
            parseMMFFPPROP(in, properties);
        } catch (IOException e) {
            throw new InternalError("Could not load MMFFPROP.PAR");
        }
        try (InputStream in = getClass().getResourceAsStream("MMFFPBCI.PAR")) {
            parseMMFFPBCI(in, properties);
        } catch (IOException e) {
            throw new InternalError("Could not load MMFFPBCI.PAR");
        }
        try (InputStream in = getClass().getResourceAsStream("mmff-symb-mapping.tsv")) {
            parseMMFFTypeMap(in, typeMap);
        } catch (IOException e) {
            throw new InternalError("Could not load mmff-symb-mapping.tsv");
        }
    }

    /**
     * Obtain the integer MMFF atom type for a given symbolic MMFF type.
     *
     * @param sym Symbolic MMFF type
     * @return integer MMFF type
     */
    int intType(final String sym) {
        Integer intType = typeMap.get(sym);
        if (intType == null) {
            return 0;
        }
        return intType;
    }

    /**
     * Access bond charge increment (bci) for a bond between two atoms (referred
     * to by MMFF integer type).
     *
     * @param cls   bond class
     * @param type1 first atom type
     * @param type2 second atom type
     * @return bci
     */
    BigDecimal getBondChargeIncrement(int cls, int type1, int type2) {
        return bcis.get(new BondKey(cls, type1, type2));
    }

    /**
     * Access Partial Bond Charge Increments (pbci).
     *
     * @param atype integer atom type
     * @return pbci
     */
    BigDecimal getPartialBondChargeIncrement(int atype) {
        return properties[checkType(atype)].pbci;
    }

    /**
     * Access Formal charge adjustment factor.
     *
     * @param atype integer atom type
     * @return adjustment factor
     */
    BigDecimal getFormalChargeAdjustment(int atype) {
        return properties[checkType(atype)].fcAdj;
    }

    /**
     * Access the CRD for an MMFF int type.
     * 
     * @param atype int atom type
     * @return CRD
     */
    int getCrd(int atype) {
        return properties[checkType(atype)].crd;
    }

    /**
     * Access the tabulated formal charge (may be fractional) for
     * a symbolic atom type. Some formal charges are variable and
     * need to be implemented in code.
     * 
     * @param symb symbolic type
     * @return formal charge
     */
    BigDecimal getFormalCharge(String symb) {
        return fCharges.get(symb);
    }

    /**
     * see. MMFF Part V - p 620, a nonstandard bond-type index of “1” is
     * assigned whenever a single bond (formal bond order 1) is found: (a)
     * between atoms i and j of types that are not both aromatic and for which
     * ”sbmb” entries of ”1” appear in Table I; or (b) between pairs of atoms
     * belonging to different aromatic rings (as in the case of the connecting
     * C-C bond in biphenyl).
     */
    int getBondCls(int type1, int type2, int bord, boolean barom) {
        MmffProp prop1 = properties[checkType(type1)];
        MmffProp prop2 = properties[checkType(type2)];
        // non-arom atoms with sbmb (single-bond-multi-bond)
        if (bord == 1 && !prop1.arom && prop1.sbmb && !prop2.arom && prop2.sbmb)
            return 1;
        // non-arom bond between arom atoms
        if (bord == 1 && !barom && prop1.arom && prop2.arom)
            return 1;
        return 0;
    }

    private int checkType(int atype) {
        if (atype < 0 || atype > MAX_MMFF_ATOMTYPE)
            throw new IllegalArgumentException("Invalid MMFF atom type:" + atype);
        return atype;
    }

    private static void parseMMFFCHARGE(InputStream in, Map<BondKey, BigDecimal> map) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, Charsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty() || line.charAt(0) == '*')
                    continue;
                final String[] cols = line.split("\\s+");
                if (cols.length != 5)
                    throw new IOException("Malformed MMFFBOND.PAR file.");
                final BondKey key = new BondKey(Integer.parseInt(cols[0]),
                                                Integer.parseInt(cols[1]),
                                                Integer.parseInt(cols[2]));
                final BigDecimal bci = new BigDecimal(cols[3]);
                map.put(key, bci);
                map.put(key.inv(), bci.negate());
            }
        }
    }

    private static void parseMMFFPBCI(InputStream in, MmffProp[] props) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, Charsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty() || line.charAt(0) == '*')
                    continue;
                final String[] cols = line.split("\\s+");
                if (cols.length < 5)
                    throw new IOException("Malformed MMFFPCBI.PAR file.");
                final int type = Integer.parseInt(cols[1]);
                props[type].pbci  = new BigDecimal(cols[2]);
                props[type].fcAdj = new BigDecimal(cols[3]);
            }
        }
    }

    private static void parseMMFFPPROP(InputStream in, MmffProp[] props) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, Charsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty() || line.charAt(0) == '*')
                    continue;
                final String[] cols = line.split("\\s+");
                if (cols.length != 10)
                    throw new IOException("Malformed MMFFPROP.PAR file.");
                final int type = Integer.parseInt(cols[1]);
                props[type] = new MmffProp(Integer.parseInt(cols[2]),
                                           Integer.parseInt(cols[3]),
                                           Integer.parseInt(cols[4]),
                                           Integer.parseInt(cols[5]),
                                           Integer.parseInt(cols[6]),
                                           Integer.parseInt(cols[7]),
                                           Integer.parseInt(cols[8]),
                                           Integer.parseInt(cols[9]));
            }
        }
    }

    private static void parseMMFFTypeMap(InputStream in, Map<String, Integer> types) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, Charsets.UTF_8))) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                if (line.isEmpty() || line.charAt(0) == '*')
                    continue;
                final String[] cols = line.split("\t");
                int intType = Integer.parseInt(cols[1]);
                types.put(cols[0], intType);
                types.put(cols[2], intType);
            }
        }
    }

    private static void parseMMFFFORMCHG(InputStream in, Map<String, BigDecimal> fcharges) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, Charsets.UTF_8))) {
            String line = br.readLine(); // header
            while ((line = br.readLine()) != null) {
                if (line.isEmpty() || line.charAt(0) == '*')
                    continue;
                final String[] cols = line.split("\\s+");
                fcharges.put(cols[0], new BigDecimal(cols[1]));
            }
        }
    }

    /**
     * Key for indexing bond parameters by
     */
    static final class BondKey {

        /** Bond class. */
        private final int cls;

        /**
         * MMFF atom types for the bond.
         */
        private final int type1, type2;


        public BondKey(int cls, int type1, int type2) {
            this.cls = cls;
            this.type1 = type1;
            this.type2 = type2;
        }

        public BondKey inv() {
            return new BondKey(cls, type2, type1);
        }

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            BondKey bondKey = (BondKey) o;

            if (cls != bondKey.cls) return false;
            if (type1 != bondKey.type1) return false;
            return type2 == bondKey.type2;

        }

        @Override public int hashCode() {
            int result = cls;
            result = 31 * result + type1;
            result = 31 * result + type2;
            return result;
        }
    }

    /**
     * Properties of an MMFF atom type.
     */
    private static final class MmffProp {
        private final int        aspec;
        private final int        crd;
        private final int        val;
        private final int        pilp;
        private final int        mltb;
        private final boolean    arom;
        private final boolean    lin;
        private final boolean    sbmb;
        private       BigDecimal pbci;
        private       BigDecimal fcAdj;

        public MmffProp(int aspec, int crd, int val, int pilp, int mltb, int arom, int lin, int sbmb) {
            this.aspec = aspec;
            this.crd = crd;
            this.val = val;
            this.pilp = pilp;
            this.mltb = mltb;
            this.arom = arom != 0;
            this.lin = lin != 0;
            this.sbmb = sbmb != 0;
        }
    }

}
