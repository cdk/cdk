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

package org.openscience.cdk.io;

import org.openscience.cdk.BondRef;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.MDLV3000Format;
import org.openscience.cdk.io.setting.BooleanIOSetting;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.io.setting.StringIOSetting;
import org.openscience.cdk.isomorphism.matchers.Expr;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import org.openscience.cdk.isomorphism.matchers.QueryBond;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.sgroup.SgroupBracket;
import org.openscience.cdk.sgroup.SgroupKey;
import org.openscience.cdk.sgroup.SgroupType;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static org.openscience.cdk.CDKConstants.ATOM_ATOM_MAPPING;
import static org.openscience.cdk.io.MDLV2000Writer.OptProgramName;
import static org.openscience.cdk.io.MDLV2000Writer.OptTruncateLongData;
import static org.openscience.cdk.io.MDLV2000Writer.OptWriteData;

/**
 * Ctab V3000 format output. This writer provides output to the more modern (but less widely
 * supported) V3000 format. Unlikely the V2000 format that is limited to 999 atoms or bonds
 * V3000 can write arbitrarily large molecules. Beyond this the format removes some (but not all)
 * ambiguities and simplifies output values with tagging (e.g 'CHG=-1' instead of '5').
 * <br><br>
 * Supported Features:
 * <ul>
 *     <li>Atom Block, non-query features</li>
 *     <li>Bond Block, supported are query bond types 4 aromatic, 5 single or double, 6 single or aromatic,
 *     7 double or aromatic, and 8 any as well as the query property that indicates whether a bond is
 *     located in a ring or in a chain</li>
 *     <li>Sgroup Block, partial support for all chemical Sgroups, complete support for: Abbreviations,
 *     MultipleGroup, SRUs, (Un)ordered Mixtures</li>
 * </ul>
 * The 3D block and enhanced stereochemistry is not currently supported.
 */
public final class MDLV3000Writer extends DefaultChemObjectWriter {
    private static final ILoggingTool logger = LoggingToolFactory.createLoggingTool(MDLV3000Reader.class);

    /**
     * Enum representing the different types of bonds in MDL format.
     */
    enum MDLBondType {
        SINGLE(1),
        DOUBLE(2),
        TRIPLE(3),
        AROMATIC(4),
        SINGLE_OR_DOUBLE(5),
        SINGLE_OR_AROMATIC(6),
        DOUBLE_OR_AROMATIC(7),
        ANY(8),
        COORDINATION(9), // not supported at this time
        HYDROGEN(10);    // not supported at this time

        private final int value;

        MDLBondType(int value) {
            this.value = value;
        }

        int getValue() {
            return this.value;
        }
    }

    /**
     * Enum representing MDL query property values.
     */
    enum MDLQueryProperty {
        NOT_SPECIFIED(0), // default value, not written if matched
        RING(1),
        CHAIN(2);

        private final int value;

        MDLQueryProperty(int value) {
            this.value = value;
        }

        /**
         * Returns the default value for MDLQueryProperty.
         *
         * @return the default value for MDLQueryProperty
         */
        static MDLQueryProperty getDefaultValue() {
            return NOT_SPECIFIED;
        }

        int getValue() {
            return this.value;
        }
    }

    private static final Pattern R_GRP_NUM = Pattern.compile("R(\\d+)");
    // The explicit valence of an atom is set to this value if connected to a query bond.
    private static final int ATOM_PART_OF_QUERY_BOND_EXPLICIT_VALENCE = -38271;
    private V30LineWriter writer;
    private StringIOSetting programNameOpt;
    private BooleanIOSetting writeDataOpt;
    private BooleanIOSetting truncateDataOpt;
    private Set<String> acceptedSdTags;

    /**
     * Create a new V3000 writer, output to the provided JDK writer.
     *
     * @param writer output location
     */
    public MDLV3000Writer(Writer writer) {
        this();
        this.writer = new V30LineWriter(writer);
    }

    /**
     * Create a new V3000 writer, output to the provided JDK output stream.
     *
     * @param out output location
     */
    public MDLV3000Writer(OutputStream out) throws CDKException {
        this();
        this.setWriter(out);
    }

    /**
     * Default empty constructor.
     */
    public MDLV3000Writer() {
        initIOSettings();
    }

    void setAcceptedSdTags(Set<String> acceptedSdTags) {
        this.acceptedSdTags = acceptedSdTags;
    }

    /**
     * Safely access nullable Integer fields by defaulting to zero.
     *
     * @param x value
     * @return value, or zero if null
     */
    private static int nullAsZero(Integer x) {
        return x == null ? 0 : x;
    }

    /**
     * Access the index of Obj->Int map, if the entry isn't found we return -1.
     *
     * @param idxs index map
     * @param obj the object
     * @param <T> the object type
     * @return index or -1 if not found
     */
    private static <T> Integer findIdx(Map<T, Integer> idxs, T obj) {
        final Integer idx = idxs.get(obj);
        if (idx == null)
            return -1;
        return idx;
    }

    /**
     * Retrieves the program name string with an exact length of 8 characters.
     * <p>
     * If the program name is not set, it returns a string with 8 spaces.
     * If the program name is set and longer than 8 characters, it returns the first 8 characters of the name.
     * If the program name is shorter than 8 characters, it returns the name padded with spaces to a length of 8 characters.
     * </p>
     * @return the program name for the writer
     */
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
     * Write the three line header of the MDL format: title, version/timestamp, remark.
     *
     * @param mol molecule being output
     * @throws IOException low-level IO error
     */
    private void writeHeader(IAtomContainer mol) throws IOException {
        final String title = mol.getTitle();
        if (title != null)
            writer.writeDirect(title.substring(0, Math.min(80, title.length())));
        writer.writeDirect('\n');

        /*
         * From CTX spec This line has the format:
         * IIPPPPPPPPMMDDYYHHmmddSSssssssssssEEEEEEEEEEEERRRRRR (FORTRAN:
         * A2<--A8--><---A10-->A2I2<--F10.5-><---F12.5--><-I6-> ) User's first
         * and last initials (l), program name (P), date/time (M/D/Y,H:m),
         * dimensional codes (d), scaling factors (S, s), energy (E) if modeling
         * program input, internal registry number (R) if input through MDL
         * form. A blank line can be substituted for line 2.
         */
        writer.writeDirect("  ");
        writer.writeDirect(getProgName());
        writer.writeDirect(new SimpleDateFormat("MMddyyHHmm").format(System.currentTimeMillis()));
        final int dim = getNumberOfDimensions(mol);
        if (dim != 0) {
            writer.writeDirect(Integer.toString(dim));
            writer.writeDirect('D');
        }
        writer.writeDirect('\n');

        final String comment = mol.getProperty(CDKConstants.REMARK);
        if (comment != null)
            writer.writeDirect(comment.substring(0, Math.min(80, comment.length())));
        writer.writeDirect('\n');
        writer.writeDirect("  0  0  0     0  0            999 V3000\n");
    }

    /**
     * Write the atoms of a molecule. We pass in the order of atoms since for compatibility we
     * have shifted all hydrogens to the back.
     *
     * @param mol molecule
     * @param atoms the atoms of a molecule in desired output order
     * @param idxs index lookup
     * @param atomToStereo tetrahedral stereo lookup
     * @throws IOException low-level IO error
     * @throws CDKException inconsistent state etc
     */
    private void writeAtomBlock(IAtomContainer mol, IAtom[] atoms, Map<IChemObject, Integer> idxs,
                                Map<IAtom, ITetrahedralChirality> atomToStereo) throws IOException, CDKException {
        if (mol.getAtomCount() == 0)
            return;

        final int dim = getNumberOfDimensions(mol);
        writer.write("BEGIN ATOM\n");
        int atomIdx = 0;
        for (IAtom atom : atoms) {
            final int elem = nullAsZero(atom.getAtomicNumber());
            final int chg  = nullAsZero(atom.getFormalCharge());
            final int mass = nullAsZero(atom.getMassNumber());
            final int hcnt = nullAsZero(atom.getImplicitHydrogenCount());
            final int elec = mol.getConnectedSingleElectronsCount(atom);
            int rad  = 0;
            switch (elec) {
                case 1: // 2
                    rad = MDLV2000Writer.SPIN_MULTIPLICITY.Monovalent.getValue();
                    break;
                case 2:
                    MDLV2000Writer.SPIN_MULTIPLICITY spinMultiplicity = atom.getProperty(CDKConstants.SPIN_MULTIPLICITY);
                    if (spinMultiplicity != null)
                        rad = spinMultiplicity.getValue();
                    else // 1 or 3? Information loss as to which
                        rad = MDLV2000Writer.SPIN_MULTIPLICITY.DivalentSinglet.getValue();
                    break;
            }

            int expVal = 0;
            for (IBond bond : mol.getConnectedBondsList(atom)) {
                // If atom is part of a query bond we cannot calculate its explicit valence.
                // The value ATOM_PART_OF_QUERY_BOND_EXPLICIT_VALENCE assigned to its explicit
                // valence allows for easy identification of such atoms in the code below.
                if (bond instanceof IQueryBond) {
                    expVal = ATOM_PART_OF_QUERY_BOND_EXPLICIT_VALENCE;
                    break;
                } else if (bond.getOrder() == null)
                    throw new CDKException("Unsupported bond order: " + bond.getOrder());
                expVal += bond.getOrder().numeric();
            }

            String symbol = getSymbol(atom, elem);
            int rnum = -1;
            if (symbol.charAt(0) == 'R') {
                Matcher matcher = R_GRP_NUM.matcher(symbol);
                if (matcher.matches()) {
                    symbol = "R#";
                    rnum   = Integer.parseInt(matcher.group(1));
                }
            }

            writer.write(++atomIdx)
                  .write(' ')
                  .write(symbol)
                  .write(' ');

            Point2d p2d = atom.getPoint2d();
            Point3d p3d = atom.getPoint3d();
            switch (dim) {
                case 0:
                    writer.write("0 0 0 ");
                    break;
                case 2:
                    if (p2d != null) {
                        writer.write(p2d.x).writeDirect(' ')
                              .write(p2d.y).writeDirect(' ')
                              .write("0 ");
                    } else {
                        writer.write("0 0 0 ");
                    }
                    break;
                case 3:
                    if (p3d != null) {
                        writer.write(p3d.x).writeDirect(' ')
                              .write(p3d.y).writeDirect(' ')
                              .write(p3d.z).writeDirect(' ');
                    } else {
                        writer.write("0 0 0 ");
                    }
                    break;
            }
            writer.write(nullAsZero(atom.getProperty(ATOM_ATOM_MAPPING, Integer.class)));

            if (chg != 0 && chg >= -15 && chg <= 15)
                writer.write(" CHG=").write(chg);
            if (mass != 0)
                writer.write(" MASS=").write(mass);
            if (rad > 0 && rad < 4)
                writer.write(" RAD=").write(rad);
            if (rnum >= 0)
                writer.write(" RGROUPS=(1 ").write(rnum).write(")");


            // Determine if we need to write the valence.
            // Valence is not written if atom is part of a query bond.
            if (expVal != ATOM_PART_OF_QUERY_BOND_EXPLICIT_VALENCE &&
                    MDLValence.implicitValence(elem, chg, expVal) - expVal != hcnt) {
                int val = expVal + hcnt;
                if (val <= 0 || val > 14)
                    val = -1; // -1 is 0
                writer.write(" VAL=").write(val);
            }

            int i = MDLV2000Writer.determineStereoParity(mol, atomToStereo, idxs, atom);
            if (i != 0)
                writer.write(" CFG=" + i);

            writer.write('\n');
        }
        writer.write("END ATOM\n");
    }

    /**
     * Return atom symbol to write.
     *
     * @param atom atom
     * @param elem atomic number
     * @return atom symbol
     */
    private String getSymbol(IAtom atom, int elem) {
        if (atom instanceof IPseudoAtom)
            return ((IPseudoAtom) atom).getLabel();
        String symbol = Elements.ofNumber(elem).symbol();
        if (symbol.isEmpty())
            symbol = atom.getSymbol();
        if (symbol == null)
            symbol = "*";
        if (symbol.length() > 3)
            symbol = symbol.substring(0, 3);
        return symbol;
    }

    /**
     * Write the bonds of a molecule.
     *
     * @param mol molecule
     * @param idxs index lookup
     * @throws IOException low-level IO error
     * @throws CDKException inconsistent state etc
     */
    private void writeBondBlock(IAtomContainer mol,
                                Map<IChemObject, Integer> idxs) throws IOException, CDKException {
        if (mol.getBondCount() == 0)
            return;

        // collect multicenter Sgroups before output
        final List<Sgroup> sgroups = mol.getProperty(CDKConstants.CTAB_SGROUPS);
        final Map<IBond,Sgroup> multicenterSgroups = new HashMap<>();
        if (sgroups != null) {
            for (Sgroup sgroup : sgroups) {
                if (sgroup.getType() != SgroupType.ExtMulticenter)
                    continue;
                for (IBond bond : sgroup.getBonds())
                    multicenterSgroups.put(bond, sgroup);
            }
        }

        writer.write("BEGIN BOND\n");
        int bondIdx = 0;
        for (IBond bond : mol.bonds()) {
            final IAtom beg = bond.getBegin();
            final IAtom end = bond.getEnd();
            if (beg == null || end == null)
                throw new IllegalStateException("Bond " + bondIdx + " had one or more atoms.");
            int begIdx = findIdx(idxs, beg);
            int endIdx = findIdx(idxs, end);
            if (begIdx < 0 || endIdx < 0)
                throw new IllegalStateException("Bond " + bondIdx + " has atoms not present in the molecule.");

            final IBond.Stereo stereo = bond.getStereo();
            // swap beg/end if needed
            if (stereo == IBond.Stereo.UP_INVERTED ||
                stereo == IBond.Stereo.DOWN_INVERTED ||
                stereo == IBond.Stereo.UP_OR_DOWN_INVERTED) {
                int tmp = begIdx;
                begIdx = endIdx;
                endIdx = tmp;
            }

            int order = bond.getOrder() == null ? 0 : bond.getOrder().numeric();
            int bondType = -1; // initialize to satisfy compiler, this value is not expected to actually being written
            MDLQueryProperty queryProperty = MDLQueryProperty.getDefaultValue();

            // If bond is an object of type QueryBond its bond order is set to null, so the variable 'order' ends up being 0.
            // Aromatic bonds have a (1) bond order of IBond.Order.UNSET (which also yields a value of 0 for 'order') and
            // (2) the flag IChemObject.AROMATIC set to true.
            if (order == 0) {
                if (bond.getOrder() == IBond.Order.UNSET) {
                    if (bond.getFlag(IChemObject.AROMATIC)) {
                        bondType = 4;
                    } else {
                        throw new CDKException("Bond with bond order " + bond.getOrder() + " that isn't flagged as aromatic cannot be written to V3000");
                    }
                } else if (bond instanceof IQueryBond) {
                    // Bond needs to be dereferenced to assess actual implementing class of bond.
                    final IQueryBond dereferencedBond = (IQueryBond) BondRef.deref(bond);
                    // Only query bonds of the class QueryBond are supported as the actual query expression needs to be
                    // extracted from the bond.
                    if (!(dereferencedBond instanceof QueryBond)) {
                        throw new CDKException("Query bond of type " + dereferencedBond.getClass() + " cannot be written to V3000");
                    }

                    final Expr expression = ((QueryBond) dereferencedBond).getExpression();
                    final ExpressionConverter converter = new ExpressionConverter(expression);
                    // Might throw a CDKException if expression cannot be meaningfully converted to MDL bond type.
                    bondType = converter.toMDLBondType().getValue();
                    queryProperty = converter.toMDLQueryProperty();
                }
            } else if (order > 0 && order <= 3) {
                bondType = order;
            } else {
                throw new CDKException("Bond order " + bond.getOrder() + " cannot be written to V3000");
            }

            writer.write(++bondIdx)
                  .write(' ')
                  .write(bondType)
                  .write(' ')
                  .write(begIdx)
                  .write(' ')
                  .write(endIdx);


            switch (stereo) {
                case UP:
                case UP_INVERTED:
                    writer.write(" CFG=1");
                    break;
                case UP_OR_DOWN:
                case UP_OR_DOWN_INVERTED:
                    writer.write(" CFG=2");
                    break;
                case DOWN:
                case DOWN_INVERTED:
                    writer.write(" CFG=3");
                    break;
                case NONE:
                    break;
                default:
                    // warn?
                    break;
            }

            if (queryProperty != MDLQueryProperty.getDefaultValue()) {
                writer.write(" TOPO=").write(queryProperty.getValue());
            }

            final Sgroup sgroup = multicenterSgroups.get(bond);
            if (sgroup != null) {
                final List<IAtom> atoms = new ArrayList<>(sgroup.getAtoms());
                atoms.remove(bond.getBegin());
                atoms.remove(bond.getEnd());
                writer.write(" ATTACH=ANY ENDPTS=(").write(atoms, idxs).write(')');
            }

            writer.write('\n');
        }
        writer.write("END BOND\n");
    }

    /**
     * CTfile specification is ambiguous as to how parity values should be written
     * for implicit hydrogens. Old applications (Symyx Draw) seem to push any
     * hydrogen to (implied) the last position but newer applications
     * (Accelrys/BioVia Draw) only do so for implicit hydrogens (makes more sense).
     * 
     * To avoid the ambiguity for those who read 0D stereo (bad anyways) we
     * actually do push all hydrogens atoms to the back of the atom list giving
     * them the highest value (4) when writing parity values.
     *
     * @param mol       molecule
     * @param atomToIdx mapping that will be filled with the output index
     * @return the output order of atoms
     */
    private IAtom[] pushHydrogensToBack(IAtomContainer mol, Map<IChemObject, Integer> atomToIdx) {
        if (!atomToIdx.isEmpty())
            throw new AssertionError("Map atomToIdx must be empty.");

        final IAtom[] atoms = new IAtom[mol.getAtomCount()];
        final int numberOfHydrogenAtoms = (int) StreamSupport.stream(mol.atoms().spliterator(), true)
                .filter(atom -> atom.getAtomicNumber() == 1)
                .count();
        int nonHydrogenIndex = 0;
        int hydrogenIndex = mol.getAtomCount() - numberOfHydrogenAtoms;

        for (final IAtom atom : mol.atoms()) {
            if (atom.getAtomicNumber() == 1) {
                atoms[hydrogenIndex++] = atom;
            } else {
                atoms[nonHydrogenIndex++] = atom;
            }
        }

        // Put all atoms together with their MDL indices (1-based) into map.
        IntStream.range(0, atoms.length).forEachOrdered(index -> atomToIdx.put(atoms[index], index + 1));

        return atoms;
    }

    /**
     * Safely access the Sgroups of a molecule retuning an empty list
     * if none are defined.
     *
     * @param mol molecule
     * @return the sgroups
     */
    private List<Sgroup> getSgroups(IAtomContainer mol) {
        List<Sgroup> sgroups = mol.getProperty(CDKConstants.CTAB_SGROUPS);
        if (sgroups == null)
            sgroups = new ArrayList<>(0);
        return sgroups;
    }

    /**
     * Returns the number of dimensions (2D or 3D) of a given molecule.
     *
     * @param mol the molecule to determine the number of dimensions for
     * @return the number of dimensions of the molecule (0 for no dimensions, 2 for 2D, 3 for 3D)
     */
    private int getNumberOfDimensions(IAtomContainer mol) {
        for (IAtom atom : mol.atoms()) {
            if (atom.getPoint3d() != null)
                return 3;
            else if (atom.getPoint2d() != null)
                return 2;
        }
        return 0;
    }

    /**
     * Write the Sgroup block to the output.
     *
     * @param sgroups the sgroups, non-null
     * @param idxs index map for looking up atom and bond indexes
     * @throws IOException low-level IO error
     * @throws CDKException unsupported format feature or invalid state
     */
    private void writeSgroupBlock(final List<Sgroup> sgroups, Map<IChemObject, Integer> idxs) throws IOException, CDKException {
        // going to reorder but keep the originals untouched
        List<Sgroup> copyOfSgroups = new ArrayList<>(sgroups);

        // remove non-ctab Sgroups
        copyOfSgroups.removeIf(sgroup -> !sgroup.getType().isCtabStandard());

        if (copyOfSgroups.isEmpty())
            return;

        writer.write("BEGIN SGROUP\n");

        // Short of building a full dependency graph we write the parents
        // first, this sort is good for three levels of nesting. Not perfect
        // but really tools should be able to handle output of order parents
        // when reading (we do).
        copyOfSgroups.sort((o1, o2) -> {
            // empty parents come first
            int cmp = -Boolean.compare(o1.getParents().isEmpty(), o2.getParents().isEmpty());
            if (cmp != 0 || o1.getParents().isEmpty()) return cmp;
            // non-empty parents, if one contains the other we have an ordering
            if (o1.getParents().contains(o2))
                return +1;
            else if (o2.getParents().contains(o1))
                return -1;
            else
                return 0;
        });

        int sgroupIdx = 0;
        for (Sgroup sgroup : copyOfSgroups) {
            final SgroupType type = sgroup.getType();
            writer.write(++sgroupIdx).write(' ').write(type.getKey()).write(" 0");

            if (!sgroup.getAtoms().isEmpty()) {
                writer.write(" ATOMS=(")
                      .write(sgroup.getAtoms(), idxs)
                      .write(")");
            }

            if (!sgroup.getBonds().isEmpty()) {
                if (type == SgroupType.CtabData) {
                    writer.write(" CBONDS=("); // containment bonds
                } else {
                    writer.write(" XBONDS=("); // crossing bonds
                }
                writer.write(sgroup.getBonds(), idxs);
                writer.write(")");
            }

            if (!sgroup.getParents().isEmpty()) {
                Set<Sgroup> parents = sgroup.getParents();
                if (parents.size() > 1)
                    throw new CDKException("Cannot write Sgroup with multiple parents");
                writer.write(" PARENT=").write(1+copyOfSgroups.indexOf(parents.iterator().next()));
            }

            for (SgroupKey key : sgroup.getAttributeKeys()) {
                switch (key) {
                    case CtabSubType:
                        writer.write(" SUBTYPE=").write(sgroup.getValue(key).toString());
                        break;
                    case CtabConnectivity:
                        writer.write(" CONNECT=").write(sgroup.getValue(key).toString().toUpperCase(Locale.ROOT));
                        break;
                    case CtabSubScript:
                        if (type == SgroupType.CtabMultipleGroup)
                            writer.write(" MULT=").write(sgroup.getValue(key).toString());
                        else
                            writer.write(" LABEL=").write(sgroup.getValue(key).toString());
                        break;
                    case CtabBracketStyle:
                        Integer btype = sgroup.getValue(key);
                        if (btype.equals(1))
                            writer.write(" BRKTYP=PAREN");
                        break;
                    case CtabParentAtomList:
                        Collection<? extends IChemObject> parentAtoms = sgroup.getValue(key);
                        writer.write(" PATOMS=(")
                              .write(parentAtoms, idxs)
                              .write(')');
                        break;
                    case CtabComponentNumber:
                        Integer number = sgroup.getValue(key);
                        if (number > 0)
                            writer.write(" COMPNO=").write(number);
                        break;
                    case CtabExpansion:
                        boolean expanded = sgroup.getValue(key);
                        if (expanded)
                            writer.write(" ESTATE=E");
                        break;
                    case CtabBracket:
                        Collection<? extends SgroupBracket> brackets = sgroup.getValue(key);
                        for (SgroupBracket bracket : brackets) {
                            writer.write(" BRKXYZ=(");
                            final Point2d p1 = bracket.getFirstPoint();
                            final Point2d p2 = bracket.getSecondPoint();
                            writer.write("9");
                            writer.write(' ').write(p1.x).write(' ').write(p1.y).write(" 0");
                            writer.write(' ').write(p2.x).write(' ').write(p2.y).write(" 0");
                            writer.write(" 0 0 0");
                            writer.write(")");
                        }
                        //writer.write(" BRKTYP=").write(sgroup.getValue(key).toString());
                        break;
                }
            }
            writer.write('\n');
        }
        writer.write("END SGROUP\n");
    }

    /**
     * Writes a molecule to the V3000 format.
     *
     * @param mol molecule
     * @throws IOException low-level IO error
     * @throws CDKException state exception (e.g undef bonds), unsupported format feature etc
     */
    private void writeMol(IAtomContainer mol) throws IOException, CDKException {
        writeHeader(mol);

        final List<Sgroup> sgroups = getSgroups(mol);

        int numSgroups = 0;
        for (Sgroup sgroup : sgroups)
            if (sgroup.getType().isCtabStandard())
                numSgroups++;

        final int chiralFlag = getChiralFlag(mol.stereoElements());

        writer.write("BEGIN CTAB\n");
        writer.write("COUNTS ")
              .write(mol.getAtomCount())
              .write(' ')
              .write(mol.getBondCount())
              .write(' ')
              .write(numSgroups)
              .write(" 0")
              .write(chiralFlag == 1 ? " 1" : " 0")
              .write("\n");

        // fast lookup atom indexes, MDL indexing starts at 1
        final Map<IChemObject, Integer> idxs = new HashMap<>();
        final Map<IAtom, ITetrahedralChirality> atomToStereo = new HashMap<>();

        // work around specification ambiguities but reordering atom output
        // order, we also insert the index into a map for lookup
        final IAtom[] atoms = pushHydrogensToBack(mol, idxs);

        // bonds are in molecule order
        for (IBond bond : mol.bonds())
            idxs.put(bond, 1 + idxs.size() - mol.getAtomCount());

        // index stereo elements for lookup
        for (IStereoElement se : mol.stereoElements()) {
            if (se instanceof ITetrahedralChirality)
                atomToStereo.put(((ITetrahedralChirality) se).getChiralAtom(), (ITetrahedralChirality) se);
        }

        writeAtomBlock(mol, atoms, idxs, atomToStereo);
        writeBondBlock(mol, idxs);
        writeSgroupBlock(sgroups, idxs);
        if (chiralFlag > 1)
            writeEnhancedStereo(mol, idxs);

        writer.write("END CTAB\n");
        writer.writeDirect("M  END\n");
        // write non-structural data (mol properties in our case)
        if (writeDataOpt.isSet()) {
            MDLV2000Writer.writeNonStructuralData(writer.writer,
                                                  mol,
                                                  MDLV2000Writer.SD_TAGS_TO_IGNORE,
                                                  acceptedSdTags,
                                                  truncateDataOpt.isSet());
        }
        writer.writer.flush();
    }

    private void writeEnhancedStereo(IAtomContainer mol, Map<IChemObject, Integer> idxs) throws IOException {
        // group together
        final Map<Integer,List<IAtom>> groups = new TreeMap<>();
        for (IStereoElement<?,?> se : mol.stereoElements()) {
            if (se.getConfigClass() == IStereoElement.TH) {
               groups.computeIfAbsent(se.getGroupInfo(), e -> new ArrayList<>())
                       .add((IAtom)se.getFocus());
            }
        }
        writer.write("BEGIN COLLECTION\n");
        int numRel = 0;
        int numRac = 0;
        for (Map.Entry<Integer,List<IAtom>> e : groups.entrySet()) {
            final int grpInfo = e.getKey();
            final List<IAtom> atoms = e.getValue();
            writer.write("MDLV30/STE");
            switch (grpInfo & IStereoElement.GRP_TYPE_MASK) {
                case IStereoElement.GRP_ABS:
                    writer.write("ABS");
                    break;
                case IStereoElement.GRP_RAC:
                    writer.write("RAC");
                    writer.write(++numRac);
                    break;
                case IStereoElement.GRP_REL:
                    writer.write("REL");
                    writer.write(++numRel);
                    break;
                default:
                    throw new IllegalStateException("Unexpected ");
            }
            writer.write(" ATOMS=(");
            writer.write(idxs.get(atoms.get(0)));
            for (int i=1; i<atoms.size(); i++) {
                writer.write(' ');
                writer.write(idxs.get(atoms.get(i)));
            }
            writer.write(")\n");
        }
        writer.write("END COLLECTION\n");
    }

    /**
     * Writes a molecule to the V3000 format. {@inheritDoc}
     *
     * @throws CDKException state exception (e.g undef bonds), unsupported format feature,
     *                      object not supported etc
     */
    @Override
    public void write(IChemObject object) throws CDKException {
        try {
            if (object instanceof IAtomContainer)
                writeMol((IAtomContainer) object);
            else
                throw new CDKException("Unsupported ChemObject " + object.getClass());
        } catch (IOException ex) {
            throw new CDKException("Could not write V3000 format", ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWriter(Writer writer) throws CDKException {
        this.writer = new V30LineWriter(writer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWriter(OutputStream writer) throws CDKException {
        setWriter(new OutputStreamWriter(writer, StandardCharsets.UTF_8));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IResourceFormat getFormat() {
        return MDLV3000Format.getInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accepts(Class<? extends IChemObject> c) {
        return c.isInstance(IAtomContainer.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        if (writer != null)
            writer.close();
    }

    /**
     * A convenience class for writing V3000 lines that auto
     * wrap when >80 characters. We actually wrap at 78 since
     * the '-\n' takes the final two. We normally only need to wrap
     * for Sgroups but all lines are handled.
     */
    private static final class V30LineWriter implements Closeable {
        // note: non-static
        private final DecimalFormat decimalFmt = new DecimalFormat("#.#####", DecimalFormatSymbols.getInstance(Locale.ROOT));
        private static final String PREFIX = "M  V30 ";
        private static final int LIMIT = 78; // '-\n' takes last two chars (80 total)

        // the base writer instance
        private final BufferedWriter writer;

        // tracks the current line length
        private int currLength = 0;

        V30LineWriter(Writer writer) {
            if (writer instanceof BufferedWriter) {
                this.writer = (BufferedWriter)writer;
            } else {
                this.writer = new BufferedWriter(writer);
            }
        }

        /**
         * Write the string to the output directly without any prefix or wrapping.
         *
         * @param str the string
         * @return self-reference for chaining
         * @throws IOException low-level IO error
         */
        V30LineWriter writeDirect(String str) throws IOException {
            this.writer.write(str);
            return this;
        }

        /**
         * Write the char to the output directly without any prefix or wrapping.
         *
         * @param c the character
         * @return self-reference for chaining
         * @throws IOException low-level IO error
         */
        V30LineWriter writeDirect(char c) throws IOException {
            this.writer.write(c);
            return this;
        }

        private void writeUnbroken(String str) throws IOException {
            newLineIfNeeded();
            writePrefixIfNeeded();
            final int len = str.length();
            if (currLength + len < LIMIT) {
                this.writer.write(str);
                currLength += len;
            } else {
                // could be more efficient but sufficient
                for (int i = 0; i < len; i++)
                    write(str.charAt(i));
            }
        }

        private void newLineIfNeeded() throws IOException {
            if (currLength == LIMIT) {
                this.writer.write('-');
                this.writer.write('\n');
                currLength = 0;
            }
        }

        private void writePrefixIfNeeded() throws IOException {
            if (currLength == 0) {
                this.writer.write(PREFIX);
                currLength = PREFIX.length();
            }
        }

        /**
         * Write a floating point number to the output, wrapping
         * if needed.
         *
         * @param num value
         * @return self-reference for chaining.
         * @throws IOException low-level IO error
         */
        V30LineWriter write(double num) throws IOException {
            return write(decimalFmt.format(num));
        }

        /**
         * Write a int number to the output, wrapping if needed.
         *
         * @param num value
         * @return self-reference for chaining.
         * @throws IOException low-level IO error
         */
        V30LineWriter write(int num) throws IOException {
            return write(Integer.toString(num));
        }

        /**
         * Write a string to the output, wrapping if needed.
         *
         * @param str value
         * @return self-reference for chaining.
         * @throws IOException low-level IO error
         */
        V30LineWriter write(String str) throws IOException {
            int i = str.indexOf('\n');
            if (i < 0) {
                writeUnbroken(str);
            } else if (i == str.length() - 1) {
                writeUnbroken(str);
                currLength = 0;
            } else {
                throw new UnsupportedOperationException();
            }
            return this;
        }

        /**
         * Write a char number to the output, wrapping if needed.
         *
         * @param c char
         * @return self-reference for chaining.
         * @throws IOException low-level IO error
         */
        V30LineWriter write(char c) throws IOException {
            if (c == '\n' && currLength == PREFIX.length())
                return this;
            if (c != '\n') newLineIfNeeded();
            writePrefixIfNeeded();
            this.writer.write(c);
            currLength++;
            if (c == '\n')
                currLength = 0;
            return this;
        }

        /**
         * Write chemobject index list, mainly useful for Sgroup output.
         *
         * @param chemObjects collection of chemobjects
         * @param idxs index map
         * @return self-reference for chaining.
         * @throws IOException low-level IO error
         */
        V30LineWriter write(Collection<? extends IChemObject> chemObjects,
                            Map<IChemObject, Integer> idxs) throws IOException {
            this.write(chemObjects.size());
            List<Integer> integers = new ArrayList<>();
            for (IChemObject chemObject : chemObjects)
                integers.add(idxs.get(chemObject));
            Collections.sort(integers);
            for (Integer integer : integers)
                this.write(' ').write(integer);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void close() throws IOException {
            writer.close();
        }
    }

    /**
     * Initializes IO settings.
     */
    private void initIOSettings() {
        programNameOpt = addSetting(new StringIOSetting(OptProgramName,
                                                        IOSetting.Importance.LOW,
                                                        "Program name to write at the top of the molfile header, should be exactly 8 characters long",
                                                        "CDK"));
        writeDataOpt = addSetting(new BooleanIOSetting(OptWriteData,
                                                       IOSetting.Importance.LOW,
                                                       "Should molecule properties be written as non-structural data", "false"));
        truncateDataOpt = addSetting(new BooleanIOSetting(OptTruncateLongData,
                                                          IOSetting.Importance.LOW,
                                                          "Truncate long data files >200 characters", "false"));
    }

    public void customizeJob() {
        for (IOSetting setting : getSettings()) {
            fireIOSettingQuestion(setting);
        }
    }

    /**
     * Determines the chiral flag, a molecule is chiral if all it's tetrahedral stereocenters are marked as absolute.
     *
     * @param stereo tetrahedral stereo
     * @return the chiral status, 0=not chiral, 1=chiral (all abs), 2=enhanced
     */
    static int getChiralFlag(Iterable<? extends IStereoElement> stereo) {
        boolean init = false;
        int grp = 0;
        for (IStereoElement<?,?> se : stereo) {
            if (se.getConfigClass() == IStereoElement.TH) {
                if (!init) {
                    init = true;
                    grp = se.getGroupInfo();
                } else if (grp != se.getGroupInfo()) {
                    return 2; // mixed
                }
            }
        }
        if (!init)
            return 0;
        if (grp == IStereoElement.GRP_ABS)
            return 1;
        return 2;
    }

    /**
     * Responsible for converting bond {@link Expr expressions} to {@link MDLBondType MDL bond types}
     * and {@link MDLQueryProperty MDL query properties}.
     */
    static final class ExpressionConverter {
        
        private static final int UNSPEC_SING  = 0x0001; // 1 << 0  ≅ 0b000000000000001
        private static final int UNSPEC_DOUB  = 0x0002; // 1 << 1  ≅ 0b000000000000010
        private static final int UNSPEC_TRIP  = 0x0004; // 1 << 2  ≅ 0b000000000000100
        private static final int UNSPEC_QUAD  = 0x0008; // 1 << 3  ≅ 0b000000000001000
        private static final int UNSPEC_AROM  = 0x0010; // 1 << 4  ≅ 0b000000000010000
        private static final int CHAIN_SING   = 0x0020; // 1 << 5  ≅ 0b000000000100000
        private static final int CHAIN_DOUB   = 0x0040; // 1 << 6  ≅ 0b000000001000000
        private static final int CHAIN_TRIP   = 0x0080; // 1 << 7  ≅ 0b000000010000000
        private static final int CHAIN_QUAD   = 0x0100; // 1 << 8  ≅ 0b000000100000000
        private static final int CHAIN_AROM   = 0x0200; // 1 << 9  ≅ 0b000001000000000
        private static final int RING_SING    = 0x0400; // 1 << 10 ≅ 0b000010000000000
        private static final int RING_DOUB    = 0x0800; // 1 << 11 ≅ 0b000100000000000
        private static final int RING_TRIP    = 0x1000; // 1 << 12 ≅ 0b001000000000000
        private static final int RING_QUAD    = 0x2000; // 1 << 13 ≅ 0b010000000000000
        private static final int RING_AROM    = 0x4000; // 1 << 14 ≅ 0b100000000000000

        private static final int SINGLE_MASK        = 0x0421; // 0b000010000100001
        private static final int DOUBLE_MASK        = 0x0842; // 0b000100001000010
        private static final int TRIPLE_MASK        = 0x1084; // 0b001000010000100
        private static final int QUADRUPLE_MASK     = 0x2108; // 0b010000100001000
        private static final int UNSPECIFIED_MASK   = 0x001f; // 0b000000000011111
        private static final int CHAIN_MASK         = 0x03e0; // 0b000001111100000
        private static final int RING_MASK          = 0x7c00; // 0b111110000000000
        private static final int AROMATIC_MASK      = 0x4210; // 0b100001000010000
        private static final int ALIPHATIC_MASK     = 0x3def; // 0b011110111101111
        private static final int ALL_MASK           = 0x7fff; // 0b111111111111111

        private final Expr expression;
        private final int bondCode;

        /**
         * Initializes an instance of the ExpressionConverter class.
         *
         * @param expression the expression to be converted
         */
        ExpressionConverter(final Expr expression) {
            this.expression = expression;
            this.bondCode = getBondCode(expression);
        }

        /**
         * Converts the bond code to an {@link MDLBondType} enum.
         *
         * @return the MDLBondType enum representing the bond code
         * @throws CDKException if the expression of the query bond cannot be written to V3000
         */
        MDLBondType toMDLBondType() throws CDKException {
            if ((this.bondCode & ALL_MASK) == ALL_MASK)
                return MDLBondType.ANY;
            if ((this.bondCode & SINGLE_MASK) > 0) {
                if ((this.bondCode & DOUBLE_MASK) > 0) {
                    return MDLBondType.SINGLE_OR_DOUBLE;
                }
                if ((this.bondCode & AROMATIC_MASK) > 0) {
                    return MDLBondType.SINGLE_OR_AROMATIC;
                }
                if ((this.bondCode & TRIPLE_MASK) == 0 && (this.bondCode & QUADRUPLE_MASK) == 0) {
                    return MDLBondType.SINGLE;
                }
            }
            if ((this.bondCode & DOUBLE_MASK) > 0) {
                if ((this.bondCode & AROMATIC_MASK) > 0) {
                    return MDLBondType.DOUBLE_OR_AROMATIC;
                }
                if (
                        (this.bondCode & SINGLE_MASK) == 0 &&
                        (this.bondCode & TRIPLE_MASK) == 0 &&
                        (this.bondCode & QUADRUPLE_MASK) == 0
                ) {
                    return MDLBondType.DOUBLE;
                }
            }
            if (
                    (this.bondCode & TRIPLE_MASK) > 0 &&
                    (this.bondCode & SINGLE_MASK) == 0 &&
                    (this.bondCode & DOUBLE_MASK) == 0 &&
                    (this.bondCode & QUADRUPLE_MASK) == 0
            ) {
                return MDLBondType.TRIPLE;
            }
            if (
                    (this.bondCode & AROMATIC_MASK) > 0 &&
                    (this.bondCode & SINGLE_MASK) == 0 &&
                    (this.bondCode & DOUBLE_MASK) == 0 &&
                    (this.bondCode & TRIPLE_MASK) == 0 &&
                    (this.bondCode & QUADRUPLE_MASK) == 0
            ) {
                return MDLBondType.AROMATIC;
            }

            throw new CDKException("Query bond with expression " + this.expression + " cannot be written to V3000");
        }

        /**
         * Converts this bondCode to an {@link MDLQueryProperty} enum.
         *
         * @return MDLQueryProperty enum representing the bondCode
         */
        MDLQueryProperty toMDLQueryProperty() {
            if ((this.bondCode & UNSPECIFIED_MASK) > 0)
                return MDLQueryProperty.NOT_SPECIFIED;
            if ((this.bondCode & CHAIN_MASK) > 0)
                return MDLQueryProperty.CHAIN;
            if ((this.bondCode & RING_MASK) > 0)
                return MDLQueryProperty.RING;

            // This statement can be reached with e.g. and(IS_IN_RING, IS_IN_CHAIN).
            return MDLQueryProperty.NOT_SPECIFIED;
        }

        /**
         * Calculates a bond code based on the given expression.
         * <br>
         * This method is package-private instead of private on purpose to allow for testing.
         *
         * @param expression the expression to get a bond code for
         * @return the calculated bond code
         */
        static int getBondCode(final Expr expression) {
            switch (expression.type()) {
                case NOT:
                    return ~getBondCode(expression.left());
                case OR:
                    return getBondCode(expression.left()) | getBondCode(expression.right());
                case AND:
                    return getBondCode(expression.left()) & getBondCode(expression.right());
                case TRUE:
                case STEREOCHEMISTRY:
                    return ALL_MASK;
                case SINGLE_OR_AROMATIC:
                    return SINGLE_MASK | AROMATIC_MASK;
                case DOUBLE_OR_AROMATIC:
                    return DOUBLE_MASK | AROMATIC_MASK;
                case SINGLE_OR_DOUBLE:
                    return SINGLE_MASK | DOUBLE_MASK;
                case IS_AROMATIC:
                    return AROMATIC_MASK;
                case IS_ALIPHATIC:
                    return ALIPHATIC_MASK;
                case IS_IN_RING:
                    return RING_MASK;
                case IS_IN_CHAIN:
                    return CHAIN_MASK;
                case ORDER:
                    switch (expression.value()) {
                        case 1:
                            return SINGLE_MASK;
                        case 2:
                            return DOUBLE_MASK;
                        case 3:
                            return TRIPLE_MASK;
                        case 4:
                            return QUADRUPLE_MASK;
                        default:
                            logger.warn("Unexpected bond order expression value: " + expression.value());
                            return 0; // error
                    }
                case ALIPHATIC_ORDER:
                    switch (expression.value()) {
                        case 1:
                            return SINGLE_MASK & ALIPHATIC_MASK;
                        case 2:
                            return DOUBLE_MASK & ALIPHATIC_MASK;
                        case 3:
                            return TRIPLE_MASK & ALIPHATIC_MASK;
                        case 4:
                            return QUADRUPLE_MASK & ALIPHATIC_MASK;
                        default:
                            logger.warn("Unexpected aliphatic bond order expression value: " + expression.value());
                            return 0; // error
                    }
                case FALSE:
                    return 0;
                default:
                    logger.warn("Unexpected expression: " + expression);
                    return 0; // error
            }
        }
    }
}
