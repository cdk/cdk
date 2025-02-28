/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 *                    2009  Egon Willighagen <egonw@users.sf.net>
 *                    2010  Mark Rijnbeek <mark_rynbeek@users.sf.net>
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
package org.openscience.cdk.inchi;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * A minimal MOL file data writer, just to get input into inchi-web.wasm
 * 
 * @cdk.module inchi
 * @cdk.githash
 */
public class InChIInputMOL {


    private final static ILoggingTool logger = LoggingToolFactory.createLoggingTool(InChIInputMOL.class);

    /**
     * Enumeration of all valid radical values.
     */
    public enum SPIN_MULTIPLICITY {

        None(0, 0),
        Monovalent(2, 1),
        DivalentSinglet(1, 2),
        DivalentTriplet(3, 2);

        // the radical SDF value
        private final int value;
        // the corresponding number of single electrons
        private final int singleElectrons;

        SPIN_MULTIPLICITY(int value, int singleElectrons) {
            this.value = value;
            this.singleElectrons = singleElectrons;
        }

        /**
         * Radical value for the spin multiplicity in the properties block.
         *
         * @return the radical value
         */
        public int getValue() {
            return value;
        }

        /**
         * The number of single electrons that correspond to the spin multiplicity.
         *
         * @return the number of single electrons
         */
        public int getSingleElectrons() {
            return singleElectrons;
        }

        /**
         * Create a SPIN_MULTIPLICITY instance for the specified value.
         *
         * @param value input value (in the property block)
         * @return instance
         * @throws CDKException unknown spin multiplicity value
         */
        public static SPIN_MULTIPLICITY ofValue(int value) throws CDKException {
            switch (value) {
                case 0:
                    return None;
                case 1:
                    return DivalentSinglet;
                case 2:
                    return Monovalent;
                case 3:
                    return DivalentTriplet;
                default:
                    throw new CDKException("unknown spin multiplicity: " + value);
            }
        }
    }

    // number of entries on line; value = 1 to 8
    private static final int NN8   = 8;
    // spacing between entries on line
    private static final int WIDTH = 3;
    private static BufferedWriter writer;


    public String write(IAtomContainer container) throws CDKException {
        try {
            int natoms = container.getAtomCount();
            if (natoms > 999) {
                throw new CDKException(" more than 999 atoms; JavaScript cannot submit to InChI");
            }
            StringWriter sw = new StringWriter();
            writer = new BufferedWriter(sw);
            final int dim = getNumberOfDimensions(container);
            StringBuilder line = new StringBuilder();
            writer.write("inchimodel\n");
            /*
             * From CTX spec This line has the format:
             * IIPPPPPPPPMMDDYYHHmmddSSssssssssssEEEEEEEEEEEERRRRRR (FORTRAN:
             * A2<--A8--><---A10-->A2I2<--F10.5-><---F12.5--><-I6-> ) User's first and last
             * initials (l), program name (P), date/time (M/D/Y,H:m), dimensional codes (d),
             * scaling factors (S, s), energy (E) if modeling program input, internal
             * registry number (R) if input through MDL form. A blank line can be
             * substituted for line 2.
             */
            writer.write("IIPPPPPPPPMMDDYYHHmm1234567890");
            if (dim != 0) {
                writer.write(Integer.toString(dim));
                writer.write('D');
            }
            writer.write('\n');
            writer.write('\n');

            // index stereo elements for setting atom parity values
            Map<IAtom, ITetrahedralChirality> atomstereo = new HashMap<>();
            Map<IAtom, Integer> atomindex = new HashMap<>();
            for (IStereoElement<?,?> element : container.stereoElements())
                if (element instanceof ITetrahedralChirality)
                    atomstereo.put(((ITetrahedralChirality) element).getChiralAtom(), (ITetrahedralChirality) element);
            for (IAtom atom : container.atoms())
                atomindex.put(atom, Integer.valueOf(atomindex.size()));

            // write Counts line
            line.append(formatMDLInt(natoms, 3));
            line.append(formatMDLInt(container.getBondCount(), 3));

            line.append("  0  0");
            line.append(getChiralFlag(atomstereo.values()) ? "  1" : "  0");
            line.append("  0  0  0  0  0999 V2000");
            writer.write(line.toString());
            writer.write('\n');
            // write Atom block
            for (int f = 0; f < natoms; f++) {
                IAtom atom = container.getAtom(f);
                line.setLength(0);
                switch (dim) {
                case 0:
                    // if no coordinates available, then output a number
                    // of zeros
                    line.append("    0.0000    0.0000    0.0000 ");
                    break;
                case 2:
                    if (atom.getPoint2d() != null) {
                        line.append(coord10((float) atom.getPoint2d().x));
                        line.append(coord10((float) atom.getPoint2d().y));
                        line.append("    0.0000 ");
                    } else {
                        line.append("    0.0000    0.0000    0.0000 ");
                    }
                    break;
                case 3:
                    if (atom.getPoint3d() != null) {
                        line.append(coord10((float) atom.getPoint3d().x));
                        line.append(coord10((float) atom.getPoint3d().y));
                        line.append(coord10((float) atom.getPoint3d().z)).append(" ");
                    } else {
                        line.append("    0.0000    0.0000    0.0000 ");
                    }
                    break;
                }
                String s = container.getAtom(f).getSymbol() + "   ";
                line.append(s.substring(0, 3));
                // atom properties
                line.append(formatMDLInt(determineIsotope(atom), 2)); // dd (mass-number)
                line.append(formatMDLInt(determineCharge(container, atom), 3)); // ccc (charge)
                line.append('\n');
                writer.write(line.toString());
            }

            // write Bond block
            for (IBond bond : container.bonds()) {
                line.setLength(0);
                if (bond.getAtomCount() != 2) {
                    logger.warn("Skipping bond with more/less than two atoms: " + bond);
                } else {
                    if (bond.getStereo() == IBond.Stereo.UP_INVERTED || bond.getStereo() == IBond.Stereo.DOWN_INVERTED
                            || bond.getStereo() == IBond.Stereo.UP_OR_DOWN_INVERTED) {
                        // turn around atom coding to correct for inv stereo
                        line.append(formatMDLInt(atomindex.get(bond.getEnd()).intValue() + 1, 3));
                        line.append(formatMDLInt(atomindex.get(bond.getBegin()).intValue() + 1, 3));
                    } else {
                        line.append(formatMDLInt(atomindex.get(bond.getBegin()).intValue() + 1, 3));
                        line.append(formatMDLInt(atomindex.get(bond.getEnd()).intValue() + 1, 3));
                    }

                    int bondType = 0;

                    if (bond.getOrder() != null) {
                        switch (bond.getOrder()) {
                        case SINGLE:
                            bondType = 1;
                            break;
                        case DOUBLE:
                            bondType = 2;
                            break;
                        case TRIPLE:
                            bondType = 3;
                            break;
                        default:
                            break;
                        }
                    }
                    if (bondType == 0)
                        throw new CDKException("Bond at idx=" + container.indexOf(bond)
                                + " is not supported by InChI, bond=" + bond.getOrder());
                    line.append(formatMDLInt(bondType, 3));
                    line.append("  ");
                    switch (bond.getStereo()) {
                    case UP:
                        line.append("1");
                        break;
                    case UP_INVERTED:
                        line.append("1");
                        break;
                    case DOWN:
                        line.append("6");
                        break;
                    case DOWN_INVERTED:
                        line.append("6");
                        break;
                    case UP_OR_DOWN:
                        line.append("4");
                        break;
                    case UP_OR_DOWN_INVERTED:
                        line.append("4");
                        break;
                    case E_OR_Z:
                        line.append("3");
                        break;
                    default:
                        line.append("0");
                    }
                    line.append("  0  0  0");
                    line.append('\n');
                    writer.write(line.toString());
                }
            }

            // Write Atom Value
            for (int i = 0; i < natoms; i++) {
                IAtom atom = container.getAtom(i);
                if (atom.getProperty(CDKConstants.COMMENT) != null
                        && atom.getProperty(CDKConstants.COMMENT) instanceof String
                        && !((String) atom.getProperty(CDKConstants.COMMENT)).trim().equals("")) {
                    writer.write("V  ");
                    writer.write(formatMDLInt(i + 1, 3));
                    writer.write(" ");
                    writer.write((String) atom.getProperty(CDKConstants.COMMENT));
                    writer.write('\n');
                }
            }

            // write formal atomic charges
            for (int i = 0; i < natoms; i++) {
                IAtom atom = container.getAtom(i);
                int charge = getInt(atom.getFormalCharge(), 0);
                if (charge != 0) {
                    writer.write("M  CHG  1 ");
                    writer.write(formatMDLInt(i + 1, 3));
                    writer.write(" ");
                    writer.write(formatMDLInt(charge, 3));
                    writer.write('\n');
                }
            }

            // write radical information
            if (container.getSingleElectronCount() > 0) {
                Map<Integer, SPIN_MULTIPLICITY> atomIndexSpinMap = new LinkedHashMap<>();
                for (int i = 0; i < natoms; i++) {
                    IAtom atom = container.getAtom(i);
                    int eCount = container.getConnectedSingleElectronsCount(atom);
                    if (eCount == 0)
                        continue;
                    Integer ie = Integer.valueOf(eCount);
                    switch (eCount) {
                    case 1:
                        atomIndexSpinMap.put(ie, SPIN_MULTIPLICITY.Monovalent);
                        break;
                    case 2:
                        SPIN_MULTIPLICITY multiplicity = atom.getProperty(CDKConstants.SPIN_MULTIPLICITY);
                        if (multiplicity != null)
                            atomIndexSpinMap.put(ie, multiplicity);
                        else {
                            // information loss, divalent but singlet or triplet?
                            atomIndexSpinMap.put(ie, SPIN_MULTIPLICITY.DivalentSinglet);
                        }
                        break;
                    default:
                        logger.debug("Invalid number of radicals found: " + eCount);
                        break;
                    }
                }
                Iterator<Map.Entry<Integer, SPIN_MULTIPLICITY>> iterator = atomIndexSpinMap.entrySet().iterator();
                for (int i = 0; i < atomIndexSpinMap.size(); i += NN8) {
                    if (atomIndexSpinMap.size() - i <= NN8) {
                        writer.write("M  RAD" + formatMDLInt(atomIndexSpinMap.size() - i, WIDTH));
                        writeRadicalPattern(iterator, 0);
                    } else {
                        writer.write("M  RAD" + formatMDLInt(NN8, WIDTH));
                        writeRadicalPattern(iterator, 0);
                    }
                    writer.write('\n');
                }
            }

            // write formal isotope information
            for (int i = 0; i < natoms; i++) {
                IAtom atom = container.getAtom(i);
                if (!(atom instanceof IPseudoAtom)) {
                    int atomicMass = (isMajorIsotope(atom) ? 0 : getInt(atom.getMassNumber(), 0));
                    if (atomicMass != 0) {
                        writer.write("M  ISO  1 ");
                        writer.write(formatMDLInt(i + 1, 3));
                        writer.write(" ");
                        writer.write(formatMDLInt(atomicMass, 3));
                        writer.write('\n');
                    }
                }
            }

            // close molecule
            writer.write("M  END");
            writer.write('\n');
            writer.flush();
            return sw.toString();        
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Determines the chiral flag, a molecule is chiral if all it's tetrahedral stereocenters are marked as absolute.
     * This function also checks if there is enhanced stereochemistry that cannot be emitted (without information loss)
     * in V2000.
     *
     * @param stereo tetrahedral stereo
     * @return the chiral status
     */
    static boolean getChiralFlag(Iterable<? extends IStereoElement<?,?>> stereo) {
        boolean chiral = true;
        int seenGrpInfo = 0;
        int numTetrahedral = 0;
        for (IStereoElement<?,?> tc : stereo) {
            if (tc.getConfigClass() != IStereoElement.TH)
                continue;
            numTetrahedral++;
            if (tc.getGroupInfo() != IStereoElement.GRP_ABS) {
                if (seenGrpInfo == 0) {
                    seenGrpInfo = tc.getGroupInfo();
                } else if (seenGrpInfo != tc.getGroupInfo()) {
                    // we could check for racemic only but V2000 originally didn't differentiate between relative
                    // or racemic so providing they're all the same it's okay. But we should warn if there is something
                    // more complicated
                    logger.warn("Molecule has enhanced stereochemistry that cannot be represented in V2000");
                }
                chiral = false;
            }
        }
        if (numTetrahedral == 0)
            chiral = false;
        return chiral;
    }

    // 0 = uncharged or value other than these, 1 = +3, 2 = +2, 3 = +1,
    // 4 = doublet radical, 5 = -1, 6 = -2, 7 = -3
    private int determineCharge(IAtomContainer mol, IAtom atom) {
        int q = getInt(atom.getFormalCharge(), 0);
        switch (q) {
            case -3: return 7;
            case -2: return 6;
            case -1: return 5;
            case 0:
                if (mol.getConnectedSingleElectronsCount(atom) == 1)
                    return 4;
                return 0;
            case +1:  return 3;
            case +2:  return 2;
            case +3:  return 1;
        }
        return 0;
    }

    private static int getInt(Integer val, int i) {
        return (val == null ? 0 : val.intValue());
    }

    private int determineIsotope(IAtom atom) {
        int mass = getInt(atom.getMassNumber(), 0);
        if (mass == 0)
            return 0;
        IIsotope major = null;
        try {
            major = Isotopes.getInstance().getMajorIsotope(atom.getSymbol());
        } catch (IOException e) {
            // ignored
        }
        int maj = getInt(major.getMassNumber(), 0);
        if (mass == maj)
            return 0;
        mass -= (major != null ? maj : 0);
        return mass >= -3 && mass <= 4 ? mass : 0;
    }

    private boolean isMajorIsotope(IAtom atom)  {
        if (atom.getMassNumber() == null)
            return false;
        try {
            IIsotope major = Isotopes.getInstance().getMajorIsotope(atom.getSymbol());
            return major != null && major.getMassNumber().equals(atom.getMassNumber());
        } catch (IOException ex) {
            return false;
        }
    }

    private int getNumberOfDimensions(IAtomContainer mol) {
        for (IAtom atom : mol.atoms()) {
            if (atom.getPoint3d() != null)
                return 3;
            else if (atom.getPoint2d() != null)
                return 2;
        }
        return 0;
    }

    private void writeRadicalPattern(Iterator<Map.Entry<Integer, SPIN_MULTIPLICITY>> iterator, int i)
            throws IOException {

        Map.Entry<Integer, SPIN_MULTIPLICITY> entry = iterator.next();
        writer.write(" ");
        writer.write(formatMDLInt(entry.getKey().intValue() + 1, WIDTH));
        writer.write(" ");
        writer.write(formatMDLInt(entry.getValue().getValue(), WIDTH));

        i = i + 1;
        if (i < NN8 && iterator.hasNext()) writeRadicalPattern(iterator, i);
    }

    /**
     * Formats an integer to fit into the connection table and changes it
     * to a String.
     *
     * @param x The int to be formated
     * @param n Length of the String
     * @return The String to be written into the connectiontable
     */
    protected static String formatMDLInt(int x, int n) {
        char[] buf = new char[n];
        Arrays.fill(buf, ' ');
        String val = Integer.toString(x);
        if (val.length() > n)
            val = "0";
        int off = n - val.length();
        for (int i = 0; i < val.length(); i++)
            buf[off+i] = val.charAt(i);
        return new String(buf);
    }

    /**
     * yyyyy.xxxx
     * @param val The float to be formated
     * @return "yyyyy.xxxx" y left-padded with space; x right-padded with 0 
     * @throws CDKException 
     */
    protected static String coord10(float val) throws CDKException {
        if (Double.isNaN(val) || Double.isInfinite(val))
            throw new CDKException("infinite or NaN found");
        String s;
        if (val == 0) {
            s = "    0.0000";
        } else {
            s = "" + (val + (val > 0 ? 0.00000001 : -0.00000001));
            s = s.substring(0, s.indexOf(".") + 5);
            int n = s.length();
            while (s.charAt(--n) == '0') {
            }
            s = s.substring(0, n + 1);
        }
        s += "00000";
        int pt = s.indexOf(".");
        s = "          " + s.substring(0, pt + 5);
        return s.substring(s.length() - 10);
    }
    
    /**
     * Formats a String to fit into the connectiontable.
     *
     * @param s  The String to be formated
     * @param le The length of the String
     * @return The String to be written in the connectiontable
     */
    protected static String formatMDLString(String s, int le) {
        s = s.trim() + "      ";
        if (s.length() > le) return s.substring(0, le);
        int l;
        l = le - s.length();
        for (int f = 0; f < l; f++)
            s += " ";
        return s;
    }

}
