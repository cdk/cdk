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
package org.openscience.cdk.io;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.MDLFormat;
import org.openscience.cdk.io.setting.BooleanIOSetting;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.isomorphism.matchers.CTFileQueryBond;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.sgroup.SgroupBracket;
import org.openscience.cdk.sgroup.SgroupKey;
import org.openscience.cdk.sgroup.SgroupType;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Writes MDL molfiles, which contains a single molecule (see {@cdk.cite DAL92}).
 * For writing a MDL molfile you can this code:
 * <pre>
 * MDLV2000Writer writer = new MDLV2000Writer(
 *   new FileWriter(new File("output.mol"))
 * );
 * writer.write((IAtomContainer)molecule);
 * writer.close();
 * </pre>
 * 
 * <p>The writer has two IO settings: one for writing 2D coordinates, even if
 * 3D coordinates are given for the written data; the second writes aromatic
 * bonds as bond type 4, which is, strictly speaking, a query bond type, but
 * my many tools used to reflect aromaticity. The full IO setting API is
 * explained in CDK News {@cdk.cite WILLIGHAGEN2004}. One programmatic option
 * to set the option for writing 2D coordinates looks like:
 * <pre>
 * Properties customSettings = new Properties();
 * customSettings.setProperty(
 *  "ForceWriteAs2DCoordinates", "true"
 * );
 * PropertiesListener listener =
 *   new PropertiesListener(customSettings);
 * writer.addChemObjectIOListener(listener);
 * </pre>
 *
 * @cdk.module io
 * @cdk.githash
 * @cdk.iooptions
 * @cdk.keyword file format, MDL molfile
 */
public class MDLV2000Writer extends DefaultChemObjectWriter {

    private final static ILoggingTool logger = LoggingToolFactory.createLoggingTool(MDLV2000Writer.class);

    // regular expression to capture R groups with attached numbers
    private Pattern NUMERED_R_GROUP = Pattern.compile("R(\\d+)");

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

        private SPIN_MULTIPLICITY(int value, int singleElectrons) {
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

    private BooleanIOSetting forceWriteAs2DCoords;

    // The next two options are MDL Query format options, not really
    // belonging to the MDLV2000 format, and will be removed when
    // a MDLV2000QueryWriter is written.

    /*
     * Should aromatic bonds be written as bond type 4? If true, this makes the
     * output a query file.
     */
    private BooleanIOSetting writeAromaticBondTypes;

    /* Should atomic valencies be written in the Query format. */
    @Deprecated
    private BooleanIOSetting writeQueryFormatValencies;

    private BufferedWriter writer;

    /**
     * Constructs a new MDLWriter that can write an {@link IAtomContainer}
     * to the MDL molfile format.
     *
     * @param out The Writer to write to
     */
    public MDLV2000Writer(Writer out) {
        if (out instanceof BufferedWriter) {
            writer = (BufferedWriter) out;
        } else {
            writer = new BufferedWriter(out);
        }
        initIOSettings();
    }

    /**
     * Constructs a new MDLWriter that can write an {@link IAtomContainer}
     * to a given OutputStream.
     *
     * @param output The OutputStream to write to
     */
    public MDLV2000Writer(OutputStream output) {
        this(new OutputStreamWriter(output, StandardCharsets.UTF_8));
    }

    public MDLV2000Writer() {
        this(new StringWriter());
    }

    @Override
    public IResourceFormat getFormat() {
        return MDLFormat.getInstance();
    }

    @Override
    public void setWriter(Writer out) throws CDKException {
        if (out instanceof BufferedWriter) {
            writer = (BufferedWriter) out;
        } else {
            writer = new BufferedWriter(out);
        }
    }

    @Override
    public void setWriter(OutputStream output) throws CDKException {
        setWriter(new OutputStreamWriter(output));
    }

    /**
     * Flushes the output and closes this object.
     */
    @Override
    public void close() throws IOException {
        writer.close();
    }

    @Override
    public boolean accepts(Class<? extends IChemObject> classObject) {
        Class<?>[] interfaces = classObject.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            if (IAtomContainer.class.equals(interfaces[i])) return true;
            if (IChemFile.class.equals(interfaces[i])) return true;
            if (IChemModel.class.equals(interfaces[i])) return true;
        }
        if (IAtomContainer.class.equals(classObject)) return true;
        if (IChemFile.class.equals(classObject)) return true;
        if (IChemModel.class.equals(classObject)) return true;
        Class superClass = classObject.getSuperclass();
        if (superClass != null) return this.accepts(superClass);
        return false;
    }

    /**
     * Writes a {@link IChemObject} to the MDL molfile formated output.
     * It can only output ChemObjects of type {@link IChemFile},
     * {@link IChemObject} and {@link IAtomContainer}.
     *
     * @param object {@link IChemObject} to write
     * @see #accepts(Class)
     */
    @Override
    public void write(IChemObject object) throws CDKException {
        customizeJob();
        try {
            if (object instanceof IChemFile) {
                writeChemFile((IChemFile) object);
                return;
            } else if (object instanceof IChemModel) {
                IChemFile file = object.getBuilder().newInstance(IChemFile.class);
                IChemSequence sequence = object.getBuilder().newInstance(IChemSequence.class);
                sequence.addChemModel((IChemModel) object);
                file.addChemSequence(sequence);
                writeChemFile((IChemFile) file);
                return;
            } else if (object instanceof IAtomContainer) {
                writeMolecule((IAtomContainer) object);
                return;
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.debug(ex);
            throw new CDKException("Exception while writing MDL file: " + ex.getMessage(), ex);
        }
        throw new CDKException("Only supported is writing of IChemFile, " + "IChemModel, and IAtomContainer objects.");
    }

    private void writeChemFile(IChemFile file) throws Exception {
        IAtomContainer bigPile = file.getBuilder().newInstance(IAtomContainer.class);
        for (IAtomContainer container : ChemFileManipulator.getAllAtomContainers(file)) {
            bigPile.add(container);
            if (container.getProperty(CDKConstants.TITLE) != null) {
                if (bigPile.getProperty(CDKConstants.TITLE) != null)
                    bigPile.setProperty(CDKConstants.TITLE,
                                        bigPile.getProperty(CDKConstants.TITLE) + "; " + container.getProperty(CDKConstants.TITLE));
                else
                    bigPile.setProperty(CDKConstants.TITLE, container.getProperty(CDKConstants.TITLE));
            }
            if (container.getProperty(CDKConstants.REMARK) != null) {
                if (bigPile.getProperty(CDKConstants.REMARK) != null)
                    bigPile.setProperty(CDKConstants.REMARK, bigPile.getProperty(CDKConstants.REMARK) + "; "
                                                             + container.getProperty(CDKConstants.REMARK));
                else
                    bigPile.setProperty(CDKConstants.REMARK, container.getProperty(CDKConstants.REMARK));
            }
        }
        writeMolecule(bigPile);
    }

    /**
     * Writes a Molecule to an OutputStream in MDL sdf format.
     *
     * @param container Molecule that is written to an OutputStream
     */
    public void writeMolecule(IAtomContainer container) throws Exception {
        String line = "";
        Map<Integer, Integer> rgroups = null;
        Map<Integer, String> aliases = null;
        // write header block
        // lines get shortened to 80 chars, that's in the spec
        String title = (String) container.getProperty(CDKConstants.TITLE);
        if (title == null) title = "";
        if (title.length() > 80) title = title.substring(0, 80);
        writer.write(title);
        writer.newLine();

        /*
         * From CTX spec This line has the format:
         * IIPPPPPPPPMMDDYYHHmmddSSssssssssssEEEEEEEEEEEERRRRRR (FORTRAN:
         * A2<--A8--><---A10-->A2I2<--F10.5-><---F12.5--><-I6-> ) User's first
         * and last initials (l), program name (P), date/time (M/D/Y,H:m),
         * dimensional codes (d), scaling factors (S, s), energy (E) if modeling
         * program input, internal registry number (R) if input through MDL
         * form. A blank line can be substituted for line 2.
         */
        writer.write("  CDK     ");
        writer.write(new SimpleDateFormat("MMddyyHHmm").format(System.currentTimeMillis()));
        writer.newLine();

        String comment = (String) container.getProperty(CDKConstants.REMARK);
        if (comment == null) comment = "";
        if (comment.length() > 80) comment = comment.substring(0, 80);
        writer.write(comment);
        writer.newLine();

        // write Counts line
        line += formatMDLInt(container.getAtomCount(), 3);
        line += formatMDLInt(container.getBondCount(), 3);
        line += "  0  0  0  0  0  0  0  0999 V2000";
        writer.write(line);
        writer.newLine();

        // index stereo elements for setting atom parity values
        Map<IAtom,ITetrahedralChirality> atomstereo = new HashMap<>();
        Map<IAtom,Integer> atomindex = new HashMap<>();
        for (IStereoElement element : container.stereoElements())
                if (element instanceof ITetrahedralChirality)
                    atomstereo.put(((ITetrahedralChirality) element).getChiralAtom(), (ITetrahedralChirality) element);
        for (IAtom atom : container.atoms())
            atomindex.put(atom, atomindex.size());

        // write Atom block
        for (int f = 0; f < container.getAtomCount(); f++) {
            IAtom atom = container.getAtom(f);
            line = "";
            if (atom.getPoint3d() != null && !forceWriteAs2DCoords.isSet()) {
                line += formatMDLFloat((float) atom.getPoint3d().x);
                line += formatMDLFloat((float) atom.getPoint3d().y);
                line += formatMDLFloat((float) atom.getPoint3d().z) + " ";
            } else if (atom.getPoint2d() != null) {
                line += formatMDLFloat((float) atom.getPoint2d().x);
                line += formatMDLFloat((float) atom.getPoint2d().y);
                line += "    0.0000 ";
            } else {
                // if no coordinates available, then output a number
                // of zeros
                line += formatMDLFloat((float) 0.0);
                line += formatMDLFloat((float) 0.0);
                line += formatMDLFloat((float) 0.0) + " ";
            }
            if (container.getAtom(f) instanceof IPseudoAtom) {
                //according to http://www.google.co.uk/url?sa=t&ct=res&cd=2&url=http%3A%2F%2Fwww.mdl.com%2Fdownloads%2Fpublic%2Fctfile%2Fctfile.pdf&ei=MsJjSMbjAoyq1gbmj7zCDQ&usg=AFQjCNGaJSvH4wYy4FTXIaQ5f7hjoTdBAw&sig2=eSfruNOSsdMFdlrn7nhdAw an R group is written as R#
                IPseudoAtom pseudoAtom = (IPseudoAtom) container.getAtom(f);
                String label = pseudoAtom.getLabel();
                if (label == null) // set to empty string if null
                    label = "";

                // firstly check if it's a numbered R group
                Matcher matcher = NUMERED_R_GROUP.matcher(label);
                if (pseudoAtom.getSymbol().equals("R") && !label.isEmpty() && matcher.matches()) {

                    line += "R# ";
                    if (rgroups == null) {
                        // we use a tree map to ensure the output order is always the same
                        rgroups = new TreeMap<Integer, Integer>();
                    }
                    rgroups.put(f + 1, Integer.parseInt(matcher.group(1)));

                }
                // not a numbered R group - note the symbol may still be R
                else {

                    // note: no distinction made between alias and pseudo atoms - normally
                    //       aliases maintain their original symbol while pseudo atoms are
                    //       written with a 'A' in the atom block

                    // if the label is longer then 3 characters we need
                    // to use an alias.
                    if (label.length() > 3) {

                        if (aliases == null) aliases = new TreeMap<Integer, String>();

                        aliases.put(f + 1, label); // atom index to alias

                        line += formatMDLString(atom.getSymbol(), 3);

                    } else { // label is short enough to fit in the atom block

                        // make sure it's not empty
                        if (!label.isEmpty())
                            line += formatMDLString(label, 3);
                        else
                            line += formatMDLString(atom.getSymbol(), 3);

                    }
                }

            } else {
                line += formatMDLString(container.getAtom(f).getSymbol(), 3);
            }

            final ITetrahedralChirality tc = atomstereo.get(atom);
            if (tc == null) {
                line += " 0  0  0  0  0";
            } else {
                int parity = tc.getStereo() == ITetrahedralChirality.Stereo.CLOCKWISE ? 1 : 2;
                IAtom   focus    = tc.getChiralAtom();
                IAtom[] carriers = tc.getLigands();

                int hidx = -1;
                for (int i = 0; i < 4; i++) {
                    // hydrogen position
                    if (carriers[i] == focus || carriers[i].getAtomicNumber() == 1) {
                        if (hidx >= 0) parity = 0;
                        hidx = i;
                    }
                }

                if (parity != 0) {
                    for (int i = 0; i < 4; i++) {
                        for (int j = i + 1; j < 4; j++) {
                            int a = atomindex.get(carriers[i]);
                            int b = atomindex.get(carriers[j]);
                            if (i == hidx)
                                a = container.getAtomCount();
                            if (j == hidx)
                                b = container.getAtomCount();
                            if (a > b)
                                parity ^= 0x3;
                        }
                    }
                }

                line += String.format(" 0  0  %d  0  0", parity);
            }

            // write valence - this is a bit of pain as the CDK has both
            // valence and implied hydrogen counts making life a lot more
            // difficult than it needs to be - we also have formal
            // neighbor count but to avoid more verbosity that check has been
            // omitted
            {
                try {
                    // slow but neat
                    int explicitValence = (int) AtomContainerManipulator.getBondOrderSum(container, atom);
                    int charge = atom.getFormalCharge() == null ? 0 : atom.getFormalCharge();
                    Integer element = atom.getAtomicNumber();

                    if (element == null) {
                        line += formatMDLInt(0, 3);
                    } else {

                        int implied = MDLValence.implicitValence(element, charge, explicitValence);

                        if (atom.getValency() != null && atom.getImplicitHydrogenCount() != null) {

                            int valence = atom.getValency();
                            int actual = explicitValence + atom.getImplicitHydrogenCount();

                            // valence from h count differs from field? we still
                            // set to default - which one has more merit?
                            if (valence != actual || implied == atom.getValency())
                                line += formatMDLInt(0, 3);
                            else if (valence == 0)
                                line += formatMDLInt(15, 3);
                            else if (valence > 0 && valence < 15)
                                line += formatMDLInt(valence, 3);
                            else
                                line += formatMDLInt(0, 3);
                        } else if (atom.getImplicitHydrogenCount() != null) {

                            int actual = explicitValence + atom.getImplicitHydrogenCount();

                            if (implied == actual) {
                                line += formatMDLInt(0, 3);
                            } else {
                                if (actual == 0)
                                    line += formatMDLInt(15, 3);
                                else if (actual > 0 && actual < 15)
                                    line += formatMDLInt(actual, 3);
                                else
                                    line += formatMDLInt(0, 3);
                            }
                        } else {
                            int valence = atom.getValency();

                            // valence from h count differs from field? we still
                            // set to default - which one has more merit?
                            if (implied == valence)
                                line += formatMDLInt(0, 3);
                            else if (valence == 0)
                                line += formatMDLInt(15, 3);
                            else if (valence > 0 && valence < 15)
                                line += formatMDLInt(valence, 3);
                            else
                                line += formatMDLInt(0, 3);
                        }
                    }

                } catch (RuntimeException e) {
                    // null bond order, query bond order - who knows.. but
                    line += formatMDLInt(0, 3);
                }
            }
            line += "  0  0  0";

            if (container.getAtom(f).getProperty(CDKConstants.ATOM_ATOM_MAPPING) != null) {
                Object atomAtomMapping = container.getAtom(f).getProperty(CDKConstants.ATOM_ATOM_MAPPING);
                if (atomAtomMapping instanceof String) {
                    try {
                        int value = Integer.parseInt((String) atomAtomMapping);
                        line += formatMDLInt(value, 3);
                    } catch (NumberFormatException exception) {
                        line += formatMDLInt(0, 3);
                        logger.warn("Skipping atom-atom mapping, invalid value: " + atomAtomMapping);
                    }
                } else if (atomAtomMapping instanceof Integer) {
                    int value = (Integer) atomAtomMapping;
                    line += formatMDLInt(value, 3);
                } else {
                    line += formatMDLInt(0, 3);
                }
            } else {
                line += formatMDLInt(0, 3);
            }
            line += "  0  0";
            writer.write(line);
            writer.newLine();
        }

        // write Bond block
        for (IBond bond : container.bonds()) {
            if (bond.getAtomCount() != 2) {
                logger.warn("Skipping bond with more/less than two atoms: " + bond);
            } else {
                if (bond.getStereo() == IBond.Stereo.UP_INVERTED || bond.getStereo() == IBond.Stereo.DOWN_INVERTED
                    || bond.getStereo() == IBond.Stereo.UP_OR_DOWN_INVERTED) {
                    // turn around atom coding to correct for inv stereo
                    line = formatMDLInt(atomindex.get(bond.getEnd()) + 1, 3);
                    line += formatMDLInt(atomindex.get(bond.getBeg()) + 1, 3);
                } else {
                    line = formatMDLInt(atomindex.get(bond.getBeg()) + 1, 3);
                    line += formatMDLInt(atomindex.get(bond.getEnd()) + 1, 3);
                }

                int bondType = 0;

                if (bond instanceof CTFileQueryBond) {
                    // Could do ordinal()-1 but this is clearer
                    switch (((CTFileQueryBond) bond).getType()) {
                        case SINGLE:
                            bondType = 1;
                            break;
                        case DOUBLE:
                            bondType = 2;
                            break;
                        case TRIPLE:
                            bondType = 3;
                            break;
                        case AROMATIC:
                            bondType = 4;
                            break;
                        case SINGLE_OR_DOUBLE:
                            bondType = 5;
                            break;
                        case SINGLE_OR_AROMATIC:
                            bondType = 6;
                            break;
                        case DOUBLE_OR_AROMATIC:
                            bondType = 7;
                            break;
                        case ANY:
                            bondType = 8;
                            break;
                    }
                } else {
                    if (bond.getOrder() != null) {
                        switch (bond.getOrder()) {
                            case SINGLE:
                            case DOUBLE:
                            case TRIPLE:
                                if (writeAromaticBondTypes.isSet() && bond.isAromatic())
                                    bondType = 4;
                                else
                                    bondType = bond.getOrder().numeric();
                                break;
                            case UNSET:
                                if (bond.isAromatic()) {
                                    if (!writeAromaticBondTypes.isSet())
                                        throw new CDKException("Bond at idx " + container.indexOf(bond) + " was an unspecific aromatic bond which should only be used for querie in Molfiles. These can be written if desired by enabling the option 'WriteAromaticBondTypes'.");
                                    bondType = 4;
                                }
                                break;
                        }
                    }
                }

                if (bondType == 0)
                    throw new CDKException("Bond at idx=" + container.indexOf(bond) + " is not supported by Molfile, bond=" + bond.getOrder());

                line += formatMDLInt(bondType, 3);
                line += "  ";
                switch (bond.getStereo()) {
                    case UP:
                        line += "1";
                        break;
                    case UP_INVERTED:
                        line += "1";
                        break;
                    case DOWN:
                        line += "6";
                        break;
                    case DOWN_INVERTED:
                        line += "6";
                        break;
                    case UP_OR_DOWN:
                        line += "4";
                        break;
                    case UP_OR_DOWN_INVERTED:
                        line += "4";
                        break;
                    case E_OR_Z:
                        line += "3";
                        break;
                    default:
                        line += "0";
                }
                line += "  0  0  0 ";
                writer.write(line);
                writer.newLine();
            }
        }

        // Write Atom Value
        for (int i = 0; i < container.getAtomCount(); i++) {
            IAtom atom = container.getAtom(i);
            if (atom.getProperty(CDKConstants.COMMENT) != null
                && atom.getProperty(CDKConstants.COMMENT) instanceof String
                && !((String) atom.getProperty(CDKConstants.COMMENT)).trim().equals("")) {
                writer.write("V  ");
                writer.write(formatMDLInt(i + 1, 3));
                writer.write(" ");
                writer.write((String) atom.getProperty(CDKConstants.COMMENT));
                writer.newLine();
            }
        }

        // write formal atomic charges
        for (int i = 0; i < container.getAtomCount(); i++) {
            IAtom atom = container.getAtom(i);
            Integer charge = atom.getFormalCharge();
            if (charge != null && charge != 0) {
                writer.write("M  CHG  1 ");
                writer.write(formatMDLInt(i + 1, 3));
                writer.write(" ");
                writer.write(formatMDLInt(charge, 3));
                writer.newLine();
            }
        }

        // write radical information
        if (container.getSingleElectronCount() > 0) {
            Map<Integer, SPIN_MULTIPLICITY> atomIndexSpinMap = new LinkedHashMap<Integer, SPIN_MULTIPLICITY>();
            for (int i = 0; i < container.getAtomCount(); i++) {
                int eCount = container.getConnectedSingleElectronsCount(container.getAtom(i));
                switch (eCount) {
                    case 0:
                        continue;
                    case 1:
                        atomIndexSpinMap.put(i, SPIN_MULTIPLICITY.Monovalent);
                        break;
                    case 2:
                        // information loss, divalent but singlet or triplet?
                        atomIndexSpinMap.put(i, SPIN_MULTIPLICITY.DivalentSinglet);
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
                    writeRadicalPattern(iterator, i);
                } else {
                    writer.write("M  RAD" + formatMDLInt(NN8, WIDTH));
                    writeRadicalPattern(iterator, i);
                }
                writer.newLine();
            }
        }

        // write formal isotope information
        for (int i = 0; i < container.getAtomCount(); i++) {
            IAtom atom = container.getAtom(i);
            if (!(atom instanceof IPseudoAtom)) {
                Integer atomicMass = atom.getMassNumber();
                if (atomicMass != null) {
                    int majorMass = Isotopes.getInstance().getMajorIsotope(atom.getSymbol()).getMassNumber();
                    if (atomicMass != majorMass) {
                        writer.write("M  ISO  1 ");
                        writer.write(formatMDLInt(i + 1, 3));
                        writer.write(" ");
                        writer.write(formatMDLInt(atomicMass, 3));
                        writer.newLine();
                    }
                }
            }
        }

        //write RGP line (max occurrence is 16 data points per line)
        if (rgroups != null) {
            StringBuilder rgpLine = new StringBuilder();
            int cnt = 0;

            // the order isn't guarantied but as we index with the atom
            // number this isn't an issue
            for (Map.Entry<Integer, Integer> e : rgroups.entrySet()) {
                rgpLine.append(formatMDLInt(e.getKey(), 4));
                rgpLine.append(formatMDLInt(e.getValue(), 4));
                cnt++;
                if (cnt == 8) {
                    rgpLine.insert(0, "M  RGP" + formatMDLInt(cnt, 3));
                    writer.write(rgpLine.toString());
                    writer.newLine();
                    rgpLine = new StringBuilder();
                    cnt = 0;
                }
            }
            if (cnt != 0) {
                rgpLine.insert(0, "M  RGP" + formatMDLInt(cnt, 3));
                writer.write(rgpLine.toString());
                writer.newLine();
            }

        }

        // write atom aliases
        if (aliases != null) {

            for (Map.Entry<Integer, String> e : aliases.entrySet()) {

                writer.write("A" + formatMDLInt(e.getKey(), 5));
                writer.newLine();

                String label = e.getValue();

                // fixed width file - doubtful someone would have a label > 70 but trim if they do
                if (label.length() > 70) label = label.substring(0, 70);

                writer.write(label);
                writer.newLine();

            }
        }

        writeSgroups(container, writer, atomindex);

        // close molecule
        writer.write("M  END");
        writer.newLine();
        writer.flush();
    }

    private void writeSgroups(IAtomContainer container, BufferedWriter writer, Map<IAtom,Integer> atomidxs) throws IOException {
        List<Sgroup> sgroups = container.getProperty(CDKConstants.CTAB_SGROUPS);
        if (sgroups == null)
            return;

        // going to modify
        sgroups = new ArrayList<>(sgroups);

        // remove non-ctab Sgroups
        Iterator<Sgroup> iter = sgroups.iterator();
        while (iter.hasNext()) {
            if (iter.next().getType() == SgroupType.ExtMulticenter)
                iter.remove();
        }

        for (List<Sgroup> wrapSgroups : wrap(sgroups, 8)) {
            // Declare the SGroup type
            writer.write("M  STY");
            writer.write(formatMDLInt(wrapSgroups.size(), 3));
            for (Sgroup sgroup : wrapSgroups) {
                writer.write(' ');
                writer.write(formatMDLInt(1 + sgroups.indexOf(sgroup), 3));
                writer.write(' ');
                writer.write(sgroup.getType().getKey());
            }
            writer.newLine();
        }

        // Sgroup output is non-compact for now - but valid
        for (int id = 1; id <= sgroups.size(); id++) {
            Sgroup sgroup = sgroups.get(id - 1);

            // Sgroup Atom List
            for (List<IAtom> atoms : wrap(sgroup.getAtoms(), 15)) {
                writer.write("M  SAL ");
                writer.write(formatMDLInt(id, 3));
                writer.write(formatMDLInt(atoms.size(), 3));
                for (IAtom atom : atoms) {
                    writer.write(' ');
                    writer.write(formatMDLInt(1+atomidxs.get(atom), 3));
                }
                writer.newLine();
            }

            // Sgroup Bond List
            for (List<IBond> bonds : wrap(sgroup.getBonds(), 15)) {
                writer.write("M  SBL ");
                writer.write(formatMDLInt(id, 3));
                writer.write(formatMDLInt(bonds.size(), 3));
                for (IBond bond : bonds) {
                    writer.write(' ');
                    writer.write(formatMDLInt(1+container.indexOf(bond), 3));
                }
                writer.newLine();
            }

            // Sgroup Parent List
            for (List<Sgroup> parents : wrap(sgroup.getParents(), 8)) {
                writer.write("M  SPL");
                writer.write(formatMDLInt(parents.size(), 3));
                for (Sgroup parent : parents) {
                    writer.write(' ');
                    writer.write(formatMDLInt(id, 3));
                    writer.write(' ');
                    writer.write(formatMDLInt(1 + sgroups.indexOf(parent), 3));
                }
                writer.newLine();
            }

            Set<SgroupKey> attributeKeys = sgroup.getAttributeKeys();
            // TODO order and aggregate attribute keys
            for (SgroupKey key : attributeKeys) {
                switch (key) {
                    case CtabSubScript:
                        writer.write("M  SMT ");
                        writer.write(formatMDLInt(id, 3));
                        writer.write(' ');
                        writer.write((String) sgroup.getValue(key));
                        writer.newLine();
                        break;
                    case CtabExpansion:
                        final boolean expanded = sgroup.getValue(key);
                        if (expanded) {
                            writer.write("M  SDS EXP");
                            writer.write(formatMDLInt(1, 3));
                            writer.write(' ');
                            writer.write(formatMDLInt(id, 3));
                            writer.newLine();
                        }
                        break;
                    case CtabBracket:
                        final List<SgroupBracket> brackets = sgroup.getValue(key);
                        for (SgroupBracket bracket : brackets) {
                            writer.write("M  SDI ");
                            writer.write(formatMDLInt(id, 3));
                            writer.write(formatMDLInt(4, 3));
                            writer.write(formatMDLFloat((float) bracket.getFirstPoint().x));
                            writer.write(formatMDLFloat((float) bracket.getFirstPoint().y));
                            writer.write(formatMDLFloat((float) bracket.getSecondPoint().x));
                            writer.write(formatMDLFloat((float) bracket.getSecondPoint().y));
                            writer.newLine();
                        }
                        break;
                    case CtabBracketStyle:
                        writer.write("M  SBT");
                        writer.write(formatMDLInt(1, 3));
                        writer.write(' ');
                        writer.write(formatMDLInt(id, 3));
                        writer.write(' ');
                        writer.write(formatMDLInt((int)sgroup.getValue(key), 3));
                        writer.newLine();
                        break;
                    case CtabConnectivity:
                        writer.write("M  SCN");
                        writer.write(formatMDLInt(1, 3));
                        writer.write(' ');
                        writer.write(formatMDLInt(id, 3));
                        writer.write(' ');
                        writer.write(((String) sgroup.getValue(key)).toUpperCase(Locale.ROOT));
                        writer.newLine();
                        break;
                    case CtabSubType:
                        writer.write("M  SST");
                        writer.write(formatMDLInt(1, 3));
                        writer.write(' ');
                        writer.write(formatMDLInt(id, 3));
                        writer.write(' ');
                        writer.write((String) sgroup.getValue(key));
                        writer.newLine();
                        break;
                    case CtabParentAtomList:
                        Set<IAtom> parentAtomList = sgroup.getValue(key);
                        for (List<IAtom> atoms : wrap(parentAtomList, 15)) {
                            writer.write("M  SPA ");
                            writer.write(formatMDLInt(id, 3));
                            writer.write(formatMDLInt(atoms.size(), 3));
                            for (IAtom atom : atoms) {
                                writer.write(' ');
                                writer.write(formatMDLInt(1+atomidxs.get(atom), 3));
                            }
                            writer.newLine();
                        }
                        break;
                    case CtabComponentNumber:
                        Integer compNumber = sgroup.getValue(key);
                        writer.write("M  SNC");
                        writer.write(formatMDLInt(1, 3));
                        writer.write(' ');
                        writer.write(formatMDLInt(id, 3));
                        writer.write(' ');
                        writer.write(formatMDLInt(compNumber, 3));
                        writer.newLine();
                        break;
                }
            }

        }
    }

    private <T> List<List<T>> wrap(Collection<T> set, int lim) {
        List<List<T>> wrapped = new ArrayList<>();
        List<T> list = new ArrayList<T>(set);
        if (set.size() <= lim) {
            if (!list.isEmpty())
             wrapped.add(list);
        } else {
            int i = 0;
            for (; (i + lim) < set.size(); i += lim) {
                wrapped.add(list.subList(i, i + lim));
            }
            wrapped.add(list.subList(i, list.size()));
        }
        return wrapped;
    }

    private void writeRadicalPattern(Iterator<Map.Entry<Integer, SPIN_MULTIPLICITY>> iterator, int i)
            throws IOException {

        Map.Entry<Integer, SPIN_MULTIPLICITY> entry = iterator.next();
        writer.write(" ");
        writer.write(formatMDLInt(entry.getKey() + 1, WIDTH));
        writer.write(" ");
        writer.write(formatMDLInt(entry.getValue().getValue(), WIDTH));

        i = i + 1;
        if (i < NN8 && iterator.hasNext()) writeRadicalPattern(iterator, i);
    }

    /**
     * Formats an integer to fit into the connection table and changes it
     * to a String.
     *
     * @param i The int to be formated
     * @param l Length of the String
     * @return The String to be written into the connectiontable
     */
    protected static String formatMDLInt(int i, int l) {
        String s = "", fs = "";
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
        nf.setParseIntegerOnly(true);
        nf.setMinimumIntegerDigits(1);
        nf.setMaximumIntegerDigits(l);
        nf.setGroupingUsed(false);
        s = nf.format(i);
        l = l - s.length();
        for (int f = 0; f < l; f++)
            fs += " ";
        fs += s;
        return fs;
    }

    /**
     * Formats a float to fit into the connectiontable and changes it
     * to a String.
     *
     * @param fl The float to be formated
     * @return The String to be written into the connectiontable
     */
    protected static String formatMDLFloat(float fl) {
        String s = "", fs = "";
        int l;
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
        nf.setMinimumIntegerDigits(1);
        nf.setMaximumIntegerDigits(4);
        nf.setMinimumFractionDigits(4);
        nf.setMaximumFractionDigits(4);
        nf.setGroupingUsed(false);
        s = nf.format(fl);
        l = 10 - s.length();
        for (int f = 0; f < l; f++)
            fs += " ";
        fs += s;
        return fs;
    }

    /**
     * Formats a String to fit into the connectiontable.
     *
     * @param s  The String to be formated
     * @param le The length of the String
     * @return The String to be written in the connectiontable
     */
    protected static String formatMDLString(String s, int le) {
        s = s.trim();
        if (s.length() > le) return s.substring(0, le);
        int l;
        l = le - s.length();
        for (int f = 0; f < l; f++)
            s += " ";
        return s;
    }

    /**
     * Initializes IO settings.<br>
     * Please note with regards to "writeAromaticBondTypes": bond type values 4 through 8 are for SSS queries only,
     * so a 'query file' is created if the container has aromatic bonds and this settings is true.
     */
    private void initIOSettings() {
        forceWriteAs2DCoords = addSetting(new BooleanIOSetting("ForceWriteAs2DCoordinates", IOSetting.Importance.LOW,
                                                               "Should coordinates always be written as 2D?", "false"));
        writeAromaticBondTypes = addSetting(new BooleanIOSetting("WriteAromaticBondTypes", IOSetting.Importance.LOW,
                                                                 "Should aromatic bonds be written as bond type 4?", "false"));
        writeQueryFormatValencies = addSetting(new BooleanIOSetting("WriteQueryFormatValencies",
                                                                    IOSetting.Importance.LOW, "Should valencies be written in the MDL Query format? (deprecated)", "false"));
    }

    /**
     * Convenience method to set the option for writing aromatic bond types.
     *
     * @param val the value.
     */
    public void setWriteAromaticBondTypes(boolean val) {
        try {
            writeAromaticBondTypes.setSetting(Boolean.toString(val));
        } catch (CDKException e) {
            // ignored can't happen since we are statically typed here
        }
    }

    public void customizeJob() {
        for (IOSetting setting : getSettings()) {
            fireIOSettingQuestion(setting);
        }
    }

}
