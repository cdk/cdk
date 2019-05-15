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
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.MDLFormat;
import org.openscience.cdk.io.setting.BooleanIOSetting;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.io.setting.StringIOSetting;
import org.openscience.cdk.isomorphism.matchers.Expr;
import org.openscience.cdk.isomorphism.matchers.QueryBond;
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
import java.util.Arrays;
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

    public static final String OptForceWriteAs2DCoordinates = "ForceWriteAs2DCoordinates";
    public static final String OptWriteMajorIsotopes        = "WriteMajorIsotopes";
    public static final String OptWriteAromaticBondTypes    = "WriteAromaticBondTypes";
    public static final String OptWriteQueryFormatValencies = "WriteQueryFormatValencies";
    public static final String OptWriteDefaultProperties    = "WriteDefaultProperties";
    public static final String OptProgramName               = "ProgramName";

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

    private BooleanIOSetting writeMajorIsotopes;

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

    private BooleanIOSetting writeDefaultProps;

    private StringIOSetting programNameOpt;

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
            if (container.getTitle() != null) {
                if (bigPile.getTitle() != null)
                    bigPile.setTitle(bigPile.getTitle() + "; " + container.getTitle());
                else
                    bigPile.setTitle(container.getTitle());
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

    private String getProgName() {
        String progname = programNameOpt.getSetting();
        if (progname == null)
            return "        ";
        else if (progname.length() > 8)
            return progname.substring(0, 8);
        else if (progname.length() < 8)
            return String.format("%-8s", progname);
        else
            return progname;
    }

    /**
     * Writes a Molecule to an OutputStream in MDL sdf format.
     *
     * @param container Molecule that is written to an OutputStream
     */
    public void writeMolecule(IAtomContainer container) throws Exception {

        final int dim = getNumberOfDimensions(container);
        StringBuilder line = new StringBuilder();
        Map<Integer, Integer> rgroups = null;
        Map<Integer, String> aliases = null;
        // write header block
        // lines get shortened to 80 chars, that's in the spec
        String title = container.getTitle();
        if (title == null) title = "";
        if (title.length() > 80) title = title.substring(0, 80);
        writer.write(title);
        writer.write('\n');

        /*
         * From CTX spec This line has the format:
         * IIPPPPPPPPMMDDYYHHmmddSSssssssssssEEEEEEEEEEEERRRRRR (FORTRAN:
         * A2<--A8--><---A10-->A2I2<--F10.5-><---F12.5--><-I6-> ) User's first
         * and last initials (l), program name (P), date/time (M/D/Y,H:m),
         * dimensional codes (d), scaling factors (S, s), energy (E) if modeling
         * program input, internal registry number (R) if input through MDL
         * form. A blank line can be substituted for line 2.
         */
        writer.write("  ");
        writer.write(getProgName());
        writer.write(new SimpleDateFormat("MMddyyHHmm").format(System.currentTimeMillis()));
        if (dim != 0) {
            writer.write(Integer.toString(dim));
            writer.write('D');
        }
        writer.write('\n');

        String comment = (String) container.getProperty(CDKConstants.REMARK);
        if (comment == null) comment = "";
        if (comment.length() > 80) comment = comment.substring(0, 80);
        writer.write(comment);
        writer.write('\n');

        // index stereo elements for setting atom parity values
        Map<IAtom,ITetrahedralChirality> atomstereo = new HashMap<>();
        Map<IAtom,Integer> atomindex = new HashMap<>();
        for (IStereoElement element : container.stereoElements())
            if (element instanceof ITetrahedralChirality)
                atomstereo.put(((ITetrahedralChirality) element).getChiralAtom(), (ITetrahedralChirality) element);
        for (IAtom atom : container.atoms())
            atomindex.put(atom, atomindex.size());

        // write Counts line
        line.append(formatMDLInt(container.getAtomCount(), 3));
        line.append(formatMDLInt(container.getBondCount(), 3));
        line.append("  0  0");
        // we mark all stereochemistry to absolute for now
        line.append(atomstereo.isEmpty() ? "  0" : "  1");
        line.append("  0  0  0  0  0999 V2000");
        writer.write(line.toString());
        writer.write('\n');

        // write Atom block
        for (int f = 0; f < container.getAtomCount(); f++) {
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
                        line.append(formatMDLFloat((float) atom.getPoint2d().x));
                        line.append(formatMDLFloat((float) atom.getPoint2d().y));
                        line.append("    0.0000 ");
                    } else {
                        line.append("    0.0000    0.0000    0.0000 ");
                    }
                    break;
                case 3:
                    if (atom.getPoint3d() != null) {
                        line.append(formatMDLFloat((float) atom.getPoint3d().x));
                        line.append(formatMDLFloat((float) atom.getPoint3d().y));
                        line.append(formatMDLFloat((float) atom.getPoint3d().z)).append(" ");
                    } else {
                        line.append("    0.0000    0.0000    0.0000 ");
                    }
                    break;
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

                    line.append("R# ");
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

                        line.append(formatMDLString(atom.getSymbol(), 3));

                    } else { // label is short enough to fit in the atom block

                        // make sure it's not empty
                        if (!label.isEmpty())
                            line.append(formatMDLString(label, 3));
                        else
                            line.append(formatMDLString(atom.getSymbol(), 3));

                    }
                }

            } else {
                line.append(formatMDLString(container.getAtom(f).getSymbol(), 3));
            }

            // atom properties
            int[] atomprops = new int[12];
            atomprops[0] = determineIsotope(atom);
            atomprops[1] = determineCharge(container, atom);
            atomprops[2] = determineStereoParity(container, atomstereo, atomindex, atom);
            atomprops[5] = determineValence(container, atom);
            atomprops[9] = determineAtomMap(atom);
            line.append(formatMDLInt(atomprops[0], 2)); // dd (mass-number)
            line.append(formatMDLInt(atomprops[1], 3)); // ccc (charge)
            int last = atomprops.length-1;
            if (!writeDefaultProps.isSet())
            {
                while (last >= 0) {
                    if (atomprops[last] != 0)
                        break;
                    last--;
                }
                // matches BIOVIA syntax
                if (last >= 2 && last < 5)
                    last = 5;
            }
            for (int i = 2; i <= last; i++)
                line.append(formatMDLInt(atomprops[i], 3));
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
                    line.append(formatMDLInt(atomindex.get(bond.getEnd()) + 1, 3));
                    line.append(formatMDLInt(atomindex.get(bond.getBegin()) + 1, 3));
                } else {
                    line.append(formatMDLInt(atomindex.get(bond.getBegin()) + 1, 3));
                    line.append(formatMDLInt(atomindex.get(bond.getEnd()) + 1, 3));
                }

                int bondType = 0;

                if (bond instanceof QueryBond) {
                    QueryBond qbond = ((QueryBond)bond);
                    Expr e = qbond.getExpression();
                    switch (e.type()) {
                        case ALIPHATIC_ORDER:
                        case ORDER:
                            bondType = e.value();
                            break;
                        case IS_AROMATIC:
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
                        case TRUE:
                            bondType = 8;
                            break;
                        case OR:
                            // SINGLE_OR_DOUBLE
                            if (e.equals(new Expr(Expr.Type.ALIPHATIC_ORDER, 1).or(new Expr(Expr.Type.ALIPHATIC_ORDER, 2))) ||
                                e.equals(new Expr(Expr.Type.ALIPHATIC_ORDER, 2).or(new Expr(Expr.Type.ALIPHATIC_ORDER, 1))))
                                bondType = 5;
                            // SINGLE_OR_AROMATIC
                            else if (e.equals(new Expr(Expr.Type.ALIPHATIC_ORDER, 1).or(new Expr(Expr.Type.IS_AROMATIC))) ||
                                e.equals(new Expr(Expr.Type.IS_AROMATIC).or(new Expr(Expr.Type.ALIPHATIC_ORDER, 1))))
                                bondType = 6;
                            // DOUBLE_OR_AROMATIC
                            else if (e.equals(new Expr(Expr.Type.ALIPHATIC_ORDER, 2).or(new Expr(Expr.Type.IS_AROMATIC))) ||
                                     e.equals(new Expr(Expr.Type.IS_AROMATIC).or(new Expr(Expr.Type.ALIPHATIC_ORDER, 2))))
                                bondType = 6;
                            break;
                        default:
                            throw new IllegalArgumentException("Unsupported bond type!");
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
                if (writeDefaultProps.isSet())
                    line.append("  0  0  0 ");
                line.append('\n');
                writer.write(line.toString());
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
                writer.write('\n');
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
                writer.write('\n');
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
                    writeRadicalPattern(iterator, 0);
                } else {
                    writer.write("M  RAD" + formatMDLInt(NN8, WIDTH));
                    writeRadicalPattern(iterator, 0);
                }
                writer.write('\n');
            }
        }

        // write formal isotope information
        for (int i = 0; i < container.getAtomCount(); i++) {
            IAtom atom = container.getAtom(i);
            if (!(atom instanceof IPseudoAtom)) {
                Integer atomicMass = atom.getMassNumber();
                if (!writeMajorIsotopes.isSet() &&
                    isMajorIsotope(atom))
                    atomicMass = null;
                if (atomicMass != null) {
                    writer.write("M  ISO  1 ");
                    writer.write(formatMDLInt(i + 1, 3));
                    writer.write(" ");
                    writer.write(formatMDLInt(atomicMass, 3));
                    writer.write('\n');
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
                    writer.write('\n');
                    rgpLine = new StringBuilder();
                    cnt = 0;
                }
            }
            if (cnt != 0) {
                rgpLine.insert(0, "M  RGP" + formatMDLInt(cnt, 3));
                writer.write(rgpLine.toString());
                writer.write('\n');
            }

        }

        // write atom aliases
        if (aliases != null) {

            for (Map.Entry<Integer, String> e : aliases.entrySet()) {

                writer.write("A" + formatMDLInt(e.getKey(), 5));
                writer.write('\n');

                String label = e.getValue();

                // fixed width file - doubtful someone would have a label > 70 but trim if they do
                if (label.length() > 70) label = label.substring(0, 70);

                writer.write(label);
                writer.write('\n');

            }
        }

        writeSgroups(container, writer, atomindex);

        // close molecule
        writer.write("M  END");
        writer.write('\n');
        writer.flush();
    }

    // 0 = uncharged or value other than these, 1 = +3, 2 = +2, 3 = +1,
    // 4 = doublet radical, 5 = -1, 6 = -2, 7 = -3
    private int determineCharge(IAtomContainer mol, IAtom atom) {
        Integer q = atom.getFormalCharge();
        if (q == null)
            q = 0;
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

    private int determineIsotope(IAtom atom) {
        Integer  mass  = atom.getMassNumber();
        IIsotope major = null;
        if (mass == null)
            return 0;
        try {
            major = Isotopes.getInstance().getMajorIsotope(atom.getSymbol());
        } catch (IOException e) {
            // ignored
        }
        if (!writeMajorIsotopes.isSet() &&
            major != null &&
            mass.equals(major.getMassNumber()))
            mass = null;
        if (mass != null) {
            mass -= major != null ? major.getMassNumber() : 0;
            return mass >= -3 && mass <= 4 ? mass : 0;
        } return 0;
    }

    private int determineAtomMap(IAtom atom) {
        Object amap   = atom.getProperty(CDKConstants.ATOM_ATOM_MAPPING);
        if (amap == null)
            return 0;
        if (amap instanceof Integer)
            return (Integer) amap;
        else {
            if (amap instanceof String) {
                try {
                    return Integer.parseInt((String) amap);
                } catch (NumberFormatException ex) {
                    //ignored
                }
            }
            logger.warn("Skipping non-integer atom map: " + amap +
                        " type:" + amap);
            return 0;
        }
    }

    private int determineValence(IAtomContainer container, IAtom atom) {
        int explicitValence = (int) AtomContainerManipulator.getBondOrderSum(container, atom);
        int charge = atom.getFormalCharge() == null ? 0 : atom.getFormalCharge();
        Integer element = atom.getAtomicNumber();
        int valence = 0;

        if (element != null) {
            int implied = MDLValence.implicitValence(element, charge, explicitValence);
            int actual;
            if (atom.getImplicitHydrogenCount() != null)
                actual = explicitValence + atom.getImplicitHydrogenCount();
            else if (atom.getValency() != null)
                actual = atom.getValency();
            else
                return 0;
            if (implied != actual) {
                if (actual == 0)
                    return 15;
                else if (actual > 0 && actual < 15)
                    return actual;
            }
        }
        return valence;
    }

    private int determineStereoParity(IAtomContainer container,
                                      Map<IAtom, ITetrahedralChirality> atomstereo,
                                      Map<IAtom, Integer> atomindex, IAtom atom) {
        final ITetrahedralChirality tc = atomstereo.get(atom);
        if (tc == null)
            return 0;
        int parity = tc.getStereo() == ITetrahedralChirality.Stereo.CLOCKWISE ? 1 : 2;
        IAtom   focus    = tc.getChiralAtom();
        IAtom[] carriers = tc.getLigands();

        int hidx = -1;
        for (int i = 0; i < 4; i++) {
            // hydrogen position
            if (carriers[i].equals(focus) || carriers[i].getAtomicNumber() == 1) {
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
        return parity;
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
            writer.write('\n');
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
                writer.write('\n');
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
                writer.write('\n');
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
                writer.write('\n');
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
                        writer.write('\n');
                        break;
                    case CtabExpansion:
                        final boolean expanded = sgroup.getValue(key);
                        if (expanded) {
                            writer.write("M  SDS EXP");
                            writer.write(formatMDLInt(1, 3));
                            writer.write(' ');
                            writer.write(formatMDLInt(id, 3));
                            writer.write('\n');
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
                            writer.write('\n');
                        }
                        break;
                    case CtabBracketStyle:
                        writer.write("M  SBT");
                        writer.write(formatMDLInt(1, 3));
                        writer.write(' ');
                        writer.write(formatMDLInt(id, 3));
                        writer.write(' ');
                        writer.write(formatMDLInt((int)sgroup.getValue(key), 3));
                        writer.write('\n');
                        break;
                    case CtabConnectivity:
                        writer.write("M  SCN");
                        writer.write(formatMDLInt(1, 3));
                        writer.write(' ');
                        writer.write(formatMDLInt(id, 3));
                        writer.write(' ');
                        writer.write(((String) sgroup.getValue(key)).toUpperCase(Locale.ROOT));
                        writer.write('\n');
                        break;
                    case CtabSubType:
                        writer.write("M  SST");
                        writer.write(formatMDLInt(1, 3));
                        writer.write(' ');
                        writer.write(formatMDLInt(id, 3));
                        writer.write(' ');
                        writer.write((String) sgroup.getValue(key));
                        writer.write('\n');
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
                            writer.write('\n');
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
                        writer.write('\n');
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

    private int getNumberOfDimensions(IAtomContainer mol) {
        for (IAtom atom : mol.atoms()) {
            if (atom.getPoint3d() != null && !forceWriteAs2DCoords.isSet())
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
        if (Double.isNaN(fl) || Double.isInfinite(fl))
            s = "0.0000";
        else
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
        forceWriteAs2DCoords = addSetting(new BooleanIOSetting(OptForceWriteAs2DCoordinates, IOSetting.Importance.LOW,
                                                               "Should coordinates always be written as 2D?", "false"));
        writeMajorIsotopes = addSetting(new BooleanIOSetting(OptWriteMajorIsotopes, IOSetting.Importance.LOW,
                                                             "Write atomic mass of any non-null atomic mass including major isotopes (e.g. [12]C)", "true"));
        writeAromaticBondTypes = addSetting(new BooleanIOSetting(OptWriteAromaticBondTypes, IOSetting.Importance.LOW,
                                                                 "Should aromatic bonds be written as bond type 4?", "false"));
        writeQueryFormatValencies = addSetting(new BooleanIOSetting(OptWriteQueryFormatValencies,
                                                                    IOSetting.Importance.LOW, "Should valencies be written in the MDL Query format? (deprecated)", "false"));
        writeDefaultProps = addSetting(new BooleanIOSetting(OptWriteDefaultProperties,
                                                            IOSetting.Importance.LOW,
                                                            "Write trailing zero's on atom/bond property blocks even if they're not used.",
                                                            "true"));
        programNameOpt = addSetting(new StringIOSetting(OptProgramName,
                                                        IOSetting.Importance.LOW,
                                                        "Program name to write at the top of the molfile header, should be exactly 8 characters long",
                                                        "CDK"));
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
