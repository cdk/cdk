/* Copyright (C) 1997-2007  Christoph Steinbeck <steinbeck@users.sourceforge.net>
 *                    2010  Egon Willighagen <egonw@users.sourceforge.net>
 *                    2014  Mark B Vine (orcid:0000-0002-7794-0426)
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io;

import com.google.common.collect.ImmutableSet;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.MDLV2000Format;
import org.openscience.cdk.io.setting.BooleanIOSetting;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.isomorphism.matchers.Expr;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryBond;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.sgroup.SgroupBracket;
import org.openscience.cdk.sgroup.SgroupKey;
import org.openscience.cdk.sgroup.SgroupType;
import org.openscience.cdk.stereo.StereoElementFactory;
import org.openscience.cdk.stereo.TetrahedralChirality;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.openscience.cdk.io.MDLV2000Writer.SPIN_MULTIPLICITY;

/**
 * Reads content from MDL molfiles and SD files. It can read a {@link
 * IAtomContainer} or {@link IChemModel} from an MDL molfile, and a {@link
 * IChemFile} from a SD file, with a {@link IChemSequence} of {@link
 * IChemModel}'s, where each IChemModel will contain one {@link IAtomContainer}.
 *
 * <p>From the Atom block it reads atomic coordinates, element types and formal
 * charges. From the Bond block it reads the bonds and the orders. Additionally,
 * it reads 'M  CHG', 'G  ', 'M  RAD' and 'M  ISO' lines from the property
 * block.
 *
 * <p>If all z coordinates are 0.0, then the xy coordinates are taken as 2D,
 * otherwise the coordinates are read as 3D.
 *
 * <p>The title of the MOL file is read and can be retrieved with:
 * <pre>
 *   molecule.getProperty(CDKConstants.TITLE);
 * </pre>
 *
 * <p>RGroups which are saved in the MDL molfile as R#, are renamed according to
 * their appearance, e.g. the first R# is named R1. With PseudAtom.getLabel()
 * "R1" is returned (instead of R#). This is introduced due to the SAR table
 * generation procedure of Scitegics PipelinePilot.
 *
 * @author steinbeck
 * @author Egon Willighagen
 * @cdk.module io
 * @cdk.githash
 * @cdk.iooptions
 * @cdk.created 2000-10-02
 * @cdk.keyword file format, MDL molfile
 * @cdk.keyword file format, SDF
 * @cdk.bug 1587283
 */
public class MDLV2000Reader extends DefaultChemObjectReader {

    BufferedReader                   input            = null;
    private static ILoggingTool      logger           = LoggingToolFactory.createLoggingTool(MDLV2000Reader.class);

    private BooleanIOSetting         forceReadAs3DCoords;
    private BooleanIOSetting         interpretHydrogenIsotopes;
    private BooleanIOSetting         addStereoElements;

    // Pattern to remove trailing space (String.trim() will remove leading space, which we don't want)
    private static final Pattern     TRAILING_SPACE   = Pattern.compile("\\s+$");

    /** Delimits Structure-Data (SD) Files. */
    private static final String      RECORD_DELIMITER = "$$$$";

    /** 
     *  @deprecated  Incorrect spelling
    */
    private static final Set<String> PSUEDO_LABELS    = ImmutableSet.<String> builder().add("*").add("A").add("Q")
                                                              .add("L").add("LP").add("R") // XXX: not in spec
                                                              .add("R#").build();

    /** Valid pseudo labels. */
    private static final Set<String> PSEUDO_LABELS    = ImmutableSet.<String> builder().add("*").add("A").add("Q")
                                                              .add("L").add("LP").add("R") // XXX: not in spec
                                                              .add("R#").build();
    
    public MDLV2000Reader() {
        this(new StringReader(""));
    }

    /**
     * Constructs a new MDLReader that can read Molecule from a given
     * InputStream.
     *
     * @param in The InputStream to read from
     */
    public MDLV2000Reader(InputStream in) {
        this(new InputStreamReader(in));
    }

    public MDLV2000Reader(InputStream in, Mode mode) {
        this(new InputStreamReader(in), mode);
    }

    /**
     * Constructs a new MDLReader that can read Molecule from a given Reader.
     *
     * @param in The Reader to read from
     */
    public MDLV2000Reader(Reader in) {
        this(in, Mode.RELAXED);
    }

    public MDLV2000Reader(Reader in, Mode mode) {
        input = new BufferedReader(in);
        initIOSettings();
        super.mode = mode;
    }

    @Override
    public IResourceFormat getFormat() {
        return MDLV2000Format.getInstance();
    }

    @Override
    public void setReader(Reader input) throws CDKException {
        if (input instanceof BufferedReader) {
            this.input = (BufferedReader) input;
        } else {
            this.input = new BufferedReader(input);
        }
    }

    @Override
    public void setReader(InputStream input) throws CDKException {
        setReader(new InputStreamReader(input));
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean accepts(Class<? extends IChemObject> classObject) {
        Class<?>[] interfaces = classObject.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            if (IChemFile.class.equals(anInterface)) return true;
            if (IChemModel.class.equals(anInterface)) return true;
            if (IAtomContainer.class.equals(anInterface)) return true;
        }
        if (IAtomContainer.class.equals(classObject)) return true;
        if (IChemFile.class.equals(classObject)) return true;
        if (IChemModel.class.equals(classObject)) return true;
        Class superClass = classObject.getSuperclass();
        return superClass != null && this.accepts(superClass);
    }

    /**
     * Takes an object which subclasses IChemObject, e.g. Molecule, and will
     * read this (from file, database, internet etc). If the specific
     * implementation does not support a specific IChemObject it will throw an
     * Exception.
     *
     * @param object The object that subclasses IChemObject
     * @return The IChemObject read
     * @throws CDKException
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends IChemObject> T read(T object) throws CDKException {
        if (object instanceof IAtomContainer) {
            return (T) readAtomContainer((IAtomContainer) object);
        } else if (object instanceof IChemFile) {
            return (T) readChemFile((IChemFile) object);
        } else if (object instanceof IChemModel) {
            return (T) readChemModel((IChemModel) object);
        } else {
            throw new CDKException("Only supported are ChemFile and Molecule.");
        }
    }

    private IChemModel readChemModel(IChemModel chemModel) throws CDKException {
        IAtomContainerSet setOfMolecules = chemModel.getMoleculeSet();
        if (setOfMolecules == null) {
            setOfMolecules = chemModel.getBuilder().newInstance(IAtomContainerSet.class);
        }
        IAtomContainer m = readAtomContainer(chemModel.getBuilder().newInstance(IAtomContainer.class));
        if (m != null) {
            setOfMolecules.addAtomContainer(m);
        }
        chemModel.setMoleculeSet(setOfMolecules);
        return chemModel;
    }

    /**
     * Read a ChemFile from a file in MDL SDF format.
     *
     * @return The ChemFile that was read from the MDL file.
     */
    private IChemFile readChemFile(IChemFile chemFile) throws CDKException {

        IChemObjectBuilder builder = chemFile.getBuilder();
        IChemSequence sequence = builder.newInstance(IChemSequence.class);

        try {
            IAtomContainer m;
            while ((m = readAtomContainer(builder.newInstance(IAtomContainer.class))) != null) {
                sequence.addChemModel(newModel(m));
            }
        } catch (CDKException e) {
            throw e;
        } catch (IllegalArgumentException exception) {
            String error = "Error while parsing SDF";
            logger.error(error);
            logger.debug(exception);
            throw new CDKException(error, exception);
        }
        try {
            input.close();
        } catch (Exception exc) {
            String error = "Error while closing file: " + exc.getMessage();
            logger.error(error);
            throw new CDKException(error, exc);
        }

        chemFile.addChemSequence(sequence);
        return chemFile;
    }

    /**
     * Create a new chem model for a single {@link IAtomContainer}.
     *
     * @param container the container to create the model for
     * @return a new {@link IChemModel}
     */
    private static IChemModel newModel(final IAtomContainer container) {

        if (container == null) throw new NullPointerException("cannot create chem model for a null container");

        final IChemObjectBuilder builder = container.getBuilder();
        final IChemModel model = builder.newInstance(IChemModel.class);
        final IAtomContainerSet containers = builder.newInstance(IAtomContainerSet.class);

        containers.addAtomContainer(container);
        model.setMoleculeSet(containers);

        return model;
    }

    /**
     * Read an IAtomContainer from a file in MDL sd format
     *
     * @return The Molecule that was read from the MDL file.
     */
    private IAtomContainer readAtomContainer(IAtomContainer molecule) throws CDKException {

        IAtomContainer outputContainer = null;
        Map<IAtom,Integer> parities = new HashMap<>();

        int linecount = 0;
        String title = null;
        String program = null;
        String remark = null;
        String line = "";

        try {

            line = input.readLine();
            linecount++;
            if (line == null) {
                return null;
            }

            if (line.startsWith("$$$$")) {
                return molecule;
            }
            if (line.length() > 0) {
                title = line;
            }
            line = input.readLine();
            linecount++;
            program = line;
            line = input.readLine();
            linecount++;
            if (line.length() > 0) {
                remark = line;
            }

            line = input.readLine();
            linecount++;

            // if the line is empty we have a problem - either a malformed
            // molecule entry or just extra new lines at the end of the file
            if (line.length() == 0) {
                handleError("Unexpected empty line", linecount, 0, 0);
                // read till the next $$$$ or EOF
                while (true) {
                    line = input.readLine();
                    linecount++;
                    if (line == null) {
                        return null;
                    }
                    if (line.startsWith("$$$$")) {
                        return molecule; // an empty molecule
                    }
                }
            }

            final CTabVersion version = CTabVersion.ofHeader(line);

            // check the CT block version
            if (version == CTabVersion.V3000) {
                handleError("This file must be read with the MDLV3000Reader.");
                // even if relaxed we can't read V3000 using the V2000 parser
                throw new CDKException("This file must be read with the MDLV3000Reader.");
            } else if (version == CTabVersion.UNSPECIFIED) {
                handleError("This file must be read with the MDLReader.");
                // okay to read in relaxed mode
            }

            int nAtoms = readMolfileInt(line, 0);
            int nBonds = readMolfileInt(line, 3);

            final IAtom[] atoms = new IAtom[nAtoms];
            final IBond[] bonds = new IBond[nBonds];

            // used for applying the MDL valence model
            int[] explicitValence = new int[nAtoms];

            boolean hasX = false, hasY = false, hasZ = false;

            for (int i = 0; i < nAtoms; i++) {
                line = input.readLine();
                linecount++;

                final IAtom atom = readAtomFast(line, molecule.getBuilder(), parities, linecount);

                atoms[i] = atom;

                Point3d p = atom.getPoint3d();
                hasX = hasX || p.x != 0d;
                hasY = hasY || p.y != 0d;
                hasZ = hasZ || p.z != 0d;
            }

            // convert to 2D, if totalZ == 0
            if (!hasX && !hasY && !hasZ) {
                if (nAtoms == 1) {
                    atoms[0].setPoint2d(new Point2d(0, 0));
                } else {
                    for (IAtom atomToUpdate : atoms) {
                        atomToUpdate.setPoint3d(null);
                    }
                }
            } else if (!hasZ) {
                //'  CDK     09251712073D'
                // 0123456789012345678901
                if (is3Dfile(program)) {
                    hasZ = true;
                } else if (!forceReadAs3DCoords.isSet()) {
                    for (IAtom atomToUpdate : atoms) {
                        Point3d p3d = atomToUpdate.getPoint3d();
                        if (p3d != null) {
                            atomToUpdate.setPoint2d(new Point2d(p3d.x, p3d.y));
                            atomToUpdate.setPoint3d(null);
                        }
                    }
                }
            }

            boolean hasQueryBonds = false;
            for (int i = 0; i < nBonds; i++) {
                line = input.readLine();
                linecount++;

                bonds[i] = readBondFast(line, molecule.getBuilder(), atoms, explicitValence, linecount);
                hasQueryBonds = hasQueryBonds
                        || (bonds[i].getOrder() == IBond.Order.UNSET && !bonds[i].getFlag(CDKConstants.ISAROMATIC));
            }

            if (!hasQueryBonds)
                outputContainer = molecule;
            else
                outputContainer = new QueryAtomContainer(molecule.getBuilder());

            if (title != null)
                outputContainer.setTitle(title);
            if (remark != null)
                outputContainer.setProperty(CDKConstants.REMARK, remark);

            // if the container is empty we can simply set the atoms/bonds
            // otherwise we add them to the end
            if (outputContainer.isEmpty()) {
                outputContainer.setAtoms(atoms);
                outputContainer.setBonds(bonds);
            } else {
                for (IAtom atom : atoms)
                    outputContainer.addAtom(atom);
                for (IBond bond : bonds)
                    outputContainer.addBond(bond);
            }

            // create 0D stereochemistry
            if (addStereoElements.isSet()) {
                Parities:
                for (Map.Entry<IAtom, Integer> e : parities.entrySet()) {
                    int parity = e.getValue();
                    if (parity != 1 && parity != 2)
                        continue; // 3=unspec
                    int idx = 0;
                    IAtom focus = e.getKey();
                    IAtom[] carriers = new IAtom[4];
                    int hidx = -1;
                    for (IAtom nbr : outputContainer.getConnectedAtomsList(focus)) {
                        if (idx == 4)
                            continue Parities; // too many neighbors
                        if (nbr.getAtomicNumber() == 1) {
                            if (hidx >= 0)
                                continue Parities;
                            hidx = idx;
                        }
                        carriers[idx++] = nbr;
                    }
                    // to few neighbors, or already have a hydrogen defined
                    if (idx < 3 || idx < 4 && hidx >= 0)
                        continue;
                    if (idx == 3)
                        carriers[idx++] = focus;

                    if (idx == 4) {
                        Stereo winding = parity == 1 ? Stereo.CLOCKWISE : Stereo.ANTI_CLOCKWISE;
                        // H is always at back, even if explicit! At least this seems to be the case.
                        // we adjust the winding as needed
                        if (hidx == 0 || hidx == 2)
                            winding = winding.invert();
                        outputContainer.addStereoElement(new TetrahedralChirality(focus, carriers, winding));
                    }
                }
            }

            // read PROPERTY block
            readPropertiesFast(input, outputContainer, nAtoms);

            // read potential SD file data between M  END and $$$$
            readNonStructuralData(input, outputContainer);

            // note: apply the valence model last so that all fixes (i.e. hydrogen
            // isotopes) are in place we need to use a offset as this atoms
            // could be added to a molecule which already had atoms present
            int offset = outputContainer.getAtomCount() - nAtoms;
            for (int i = offset; i < outputContainer.getAtomCount(); i++) {
                int valence = explicitValence[i - offset];
                if (valence < 0) {
                    hasQueryBonds = true; // also counts aromatic bond as query
                } else {
                    int unpaired = outputContainer.getConnectedSingleElectronsCount(outputContainer.getAtom(i));
                    applyMDLValenceModel(outputContainer.getAtom(i), valence + unpaired, unpaired);
                }
            }

            // sanity check that we have a decent molecule, query bonds mean we
            // don't have a hydrogen count for atoms and stereo perception isn't
            // currently possible
            if (!hasQueryBonds && addStereoElements.isSet() && hasX && hasY) {
                if (hasZ) { // has 3D coordinates
                    outputContainer.setStereoElements(StereoElementFactory.using3DCoordinates(outputContainer)
                            .createAll());
                } else if (!forceReadAs3DCoords.isSet()) { // has 2D coordinates (set as 2D coordinates)
                    outputContainer.setStereoElements(StereoElementFactory.using2DCoordinates(outputContainer)
                            .createAll());
                }
            }

        } catch (CDKException exception) {
            String error = "Error while parsing line " + linecount + ": " + line + " -> " + exception.getMessage();
            logger.error(error);
            throw exception;
        } catch (IOException exception) {
            exception.printStackTrace();
            String error = "Error while parsing line " + linecount + ": " + line + " -> " + exception.getMessage();
            logger.error(error);
            handleError("Error while parsing line: " + line, linecount, 0, 0, exception);
        }

        return outputContainer;
    }

    private boolean is3Dfile(String program) {
        return program.length() >= 22 && program.substring(20, 22).equals("3D");
    }

    /**
     * Applies the MDL valence model to atoms using the explicit valence (bond
     * order sum) and charge to determine the correct number of implicit
     * hydrogens. The model is not applied if the explicit valence is less than
     * 0 - this is the case when a query bond was read for an atom.
     *
     * @param atom            the atom to apply the model to
     * @param unpaired        unpaired electron count
     * @param explicitValence the explicit valence (bond order sum)
     */
    private void applyMDLValenceModel(IAtom atom, int explicitValence, int unpaired) {

        if (atom.getValency() != null) {
            if (atom.getValency() >= explicitValence)
                atom.setImplicitHydrogenCount(atom.getValency() - (explicitValence - unpaired));
            else
                atom.setImplicitHydrogenCount(0);
        } else {
            Integer element = atom.getAtomicNumber();
            if (element == null) element = 0;

            Integer charge = atom.getFormalCharge();
            if (charge == null) charge = 0;

            int implicitValence = MDLValence.implicitValence(element, charge, explicitValence);
            if (implicitValence < explicitValence) {
                atom.setValency(explicitValence);
                atom.setImplicitHydrogenCount(0);
            } else {
                atom.setValency(implicitValence);
                atom.setImplicitHydrogenCount(implicitValence - explicitValence);
            }
        }
    }

    private void fixHydrogenIsotopes(IAtomContainer molecule, IsotopeFactory isotopeFactory) {
        for (IAtom atom : AtomContainerManipulator.getAtomArray(molecule)) {
            if (atom instanceof IPseudoAtom) {
                IPseudoAtom pseudo = (IPseudoAtom) atom;
                if ("D".equals(pseudo.getLabel())) {
                    IAtom newAtom = molecule.getBuilder().newInstance(IAtom.class, atom);
                    newAtom.setSymbol("H");
                    newAtom.setAtomicNumber(1);
                    isotopeFactory.configure(newAtom, isotopeFactory.getIsotope("H", 2));
                    AtomContainerManipulator.replaceAtomByAtom(molecule, atom, newAtom);
                } else if ("T".equals(pseudo.getLabel())) {
                    IAtom newAtom = molecule.getBuilder().newInstance(IAtom.class, atom);
                    newAtom.setSymbol("H");
                    newAtom.setAtomicNumber(1);
                    isotopeFactory.configure(newAtom, isotopeFactory.getIsotope("H", 3));
                    AtomContainerManipulator.replaceAtomByAtom(molecule, atom, newAtom);
                }
            }
        }
    }

    @Override
    public void close() throws IOException {
        input.close();
    }

    private void initIOSettings() {
        forceReadAs3DCoords = addSetting(new BooleanIOSetting("ForceReadAs3DCoordinates", IOSetting.Importance.LOW,
                "Should coordinates always be read as 3D?", "false"));
        interpretHydrogenIsotopes = addSetting(new BooleanIOSetting("InterpretHydrogenIsotopes",
                IOSetting.Importance.LOW, "Should D and T be interpreted as hydrogen isotopes?", "true"));
        addStereoElements = addSetting(new BooleanIOSetting("AddStereoElements", IOSetting.Importance.LOW,
                "Detect and create IStereoElements for the input.", "true"));
    }

    public void customizeJob() {
        for (IOSetting setting : getSettings()) {
            fireIOSettingQuestion(setting);
        }
    }

    private String removeNonDigits(String input) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char inputChar = input.charAt(i);
            if (Character.isDigit(inputChar)) sb.append(inputChar);
        }
        return sb.toString();
    }

    IAtom readAtomFast(String line, IChemObjectBuilder builder, int lineNum) throws CDKException, IOException {
        return readAtomFast(line, builder, Collections.<IAtom,Integer>emptyMap(), lineNum);
    }

    /**
     * Parse an atom line from the atom block using the format: {@code
     * xxxxx.xxxxyyyyy.yyyyzzzzz.zzzz aaaddcccssshhhbbbvvvHHHrrriiimmmnnneee}
     * where: <ul> <li>x: x coordinate</li> <li>y: y coordinate</li> <li>z: z
     * coordinate</li> <li>a: atom symbol</li> <li>d: mass difference</li>
     * <li>c: charge</li> <li>s: stereo parity</li> <li>h: hydrogen count + 1
     * (not read - query)</li> <li>b: stereo care (not read - query)</li> <li>v:
     * valence</li> <li>H: H0 designator (not read - query)</li> <li>r: not
     * used</li> <li>i: not used</li> <li>m: atom reaction mapping</li> <li>n:
     * inversion/retention flag</li> <li>e: exact change flag</li> </ul>
     *
     * The parsing is strict and does not allow extra columns (i.e. NMR shifts)
     * malformed input.
     *
     * @param line    input line
     * @param builder chem object builder to create the atom
     * @param parities map of atom parities for creation 0D stereochemistry
     * @param lineNum the line number - for printing error messages
     * @return a new atom instance
     */
    IAtom readAtomFast(String line, IChemObjectBuilder builder, Map<IAtom,Integer> parities, int lineNum) throws CDKException, IOException {

        // The line may be truncated and it's checked in reverse at the specified
        // lengths:
        //          1         2         3         4         5         6
        // 123456789012345678901234567890123456789012345678901234567890123456789
        //                                  | |  |  |  |  |  |  |  |  |  |  |  |
        // xxxxx.xxxxyyyyy.yyyyzzzzz.zzzz aaaddcccssshhhbbbvvvHHHrrriiimmmnnneee

        String symbol;
        double x, y, z;
        int massDiff = 0, charge = 0, parity = 0, valence = 0, mapping = 0;

        int length = length(line);
        if (length > 69) // excess data we should check all fields
            length = 69;

        // given the length we jump to the position and parse all fields
        // that could be present (note - fall through switch)
        switch (length) {
            case 69: // eee: exact charge flag [reaction, query]
            case 66: // nnn: inversion / retention [reaction]
            case 63: // mmm: atom-atom mapping [reaction]
                mapping = readMolfileInt(line, 60);
            case 60: // iii: not used
            case 57: // rrr: not used
            case 54: // HHH: H0 designation [redundant]
            case 51: // vvv: valence
                valence = readMolfileInt(line, 48);
            case 48: // bbb: stereo care [query]
            case 45: // hhh: hydrogen count + 1 [query]
            case 42: // sss: stereo parity
                parity = toInt(line.charAt(41));
            case 39: // ccc: charge
                charge = toCharge(line.charAt(38));
            case 36: // dd: mass difference
                massDiff = sign(line.charAt(34)) * toInt(line.charAt(35));
            case 34: // x y z and aaa: atom coordinates and symbol
            case 33: // symbol is left aligned
            case 32:
                x = readMDLCoordinate(line, 0);
                y = readMDLCoordinate(line, 10);
                z = readMDLCoordinate(line, 20);
                symbol = line.substring(31, 34).trim().intern();
                break;
            default:
                handleError("invalid line length", lineNum, 0, 0);
                throw new CDKException("invalid line length, " + length + ": " + line);
        }

        IAtom atom = createAtom(symbol, builder, lineNum);

        atom.setPoint3d(new Point3d(x, y, z));
        atom.setFormalCharge(charge);
        atom.setStereoParity(parity);
        if (parity != 0)
            parities.put(atom, parity);

        // if there was a mass difference, set the mass number
        if (massDiff != 0 && atom.getAtomicNumber() > 0) {
            IIsotope majorIsotope = Isotopes.getInstance().getMajorIsotope(atom.getAtomicNumber());
            if (majorIsotope == null)
                atom.setMassNumber(-1); // checked after M ISO is processed
            else
                atom.setMassNumber(majorIsotope.getMassNumber() + massDiff);
        }

        if (valence > 0 && valence < 16) atom.setValency(valence == 15 ? 0 : valence);

        if (mapping != 0) atom.setProperty(CDKConstants.ATOM_ATOM_MAPPING, mapping);

        return atom;
    }

    /**
     * Read a bond from a line in the MDL bond block. The bond block is
     * formatted as follows, {@code 111222tttsssxxxrrrccc}, where:
     * <ul>
     *     <li>111: first atom number</li>
     *     <li>222: second atom number</li>
     *     <li>ttt: bond type</li>
     *     <li>xxx: bond stereo</li>
     *     <li>rrr: bond topology</li>
     *     <li>ccc: reaction center</li>
     * </ul>
     *
     * @param line            the input line
     * @param builder         builder to create objects with
     * @param atoms           atoms read from the atom block
     * @param explicitValence array to fill with explicit valence
     * @param lineNum         the input line number
     * @return a new bond
     * @throws CDKException thrown if the input was malformed or didn't make
     *                      sense
     */
    IBond readBondFast(String line, IChemObjectBuilder builder, IAtom[] atoms, int[] explicitValence, int lineNum)
            throws CDKException {

        // The line may be truncated and it's checked in reverse at the specified
        // lengths. Absolutely required is atom indices, bond type and stereo.
        //          1         2
        // 123456789012345678901
        //            |  |  |  |
        // 111222tttsssxxxrrrccc

        int length = length(line);
        if (length > 21) length = 21;

        int u, v, type, stereo = 0;

        switch (length) {
            case 21: // ccc: reaction centre status
            case 18: // rrr: bond topology
            case 15: // xxx: not used
            case 12: // sss: stereo
                stereo = readUInt(line, 9, 3);
            case 9: // 111222ttt: atoms, type and stereo
                u = readMolfileInt(line, 0) - 1;
                v = readMolfileInt(line, 3) - 1;
                type = readMolfileInt(line, 6);
                break;
            default:
                throw new CDKException("invalid line length: " + length + " " + line);
        }

        IBond bond = builder.newBond();
        bond.setAtoms(new IAtom[]{atoms[u], atoms[v]});

        switch (type) {
            case 1: // single
                bond.setOrder(IBond.Order.SINGLE);
                bond.setStereo(toStereo(stereo, type));
                break;
            case 2: // double
                bond.setOrder(IBond.Order.DOUBLE);
                bond.setStereo(toStereo(stereo, type));
                break;
            case 3: // triple
                bond.setOrder(IBond.Order.TRIPLE);
                break;
            case 4: // aromatic
                bond.setOrder(IBond.Order.UNSET);
                bond.setFlag(CDKConstants.ISAROMATIC, true);
                bond.setFlag(CDKConstants.SINGLE_OR_DOUBLE, true);
                atoms[u].setFlag(CDKConstants.ISAROMATIC, true);
                atoms[v].setFlag(CDKConstants.ISAROMATIC, true);
                break;
            case 5: // single or double
                bond = new QueryBond(bond.getBegin(), bond.getEnd(), Expr.Type.SINGLE_OR_DOUBLE);
                break;
            case 6: // single or aromatic
                bond = new QueryBond(bond.getBegin(), bond.getEnd(), Expr.Type.SINGLE_OR_AROMATIC);
                break;
            case 7: // double or aromatic
                bond = new QueryBond(bond.getBegin(), bond.getEnd(), Expr.Type.DOUBLE_OR_AROMATIC);
                break;
            case 8: // any
                bond = new QueryBond(bond.getBegin(), bond.getEnd(), Expr.Type.TRUE);
                break;
            default:
                throw new CDKException("unrecognised bond type: " + type + ", " + line);
        }

        if (type < 4) {
            explicitValence[u] += type;
            explicitValence[v] += type;
        } else {
            explicitValence[u] = explicitValence[v] = Integer.MIN_VALUE;
        }

        return bond;
    }

    /**
     * Reads the property block from the {@code input} setting the values in the
     * container.
     *
     * @param input     input resource
     * @param container the structure with atoms / bonds present
     * @param nAtoms    the number of atoms in the atoms block
     * @throws IOException low-level IO error
     */
    void readPropertiesFast(final BufferedReader input, final IAtomContainer container, final int nAtoms)
            throws IOException, CDKException {
        String line;

        // first atom index in this Molfile, the container may have
        // already had atoms present before reading the file
        int offset = container.getAtomCount() - nAtoms;

        Map<Integer, Sgroup> sgroups = new LinkedHashMap<>();

        LINES:
        while ((line = input.readLine()) != null) {

            int index, count, lnOffset;
            Sgroup sgroup;
            int length = line.length();
            final PropertyKey key = PropertyKey.of(line);
            switch (key) {

                // A  aaa
                // x...
                //
                // atom alias is stored as label on a pseudo atom
                case ATOM_ALIAS:
                    index = readMolfileInt(line, 3) - 1;
                    final String label = input.readLine();
                    if (label == null) return;
                    label(container, offset + index, label);
                    break;

                // V  aaa v...
                //
                // an atom value is stored as comment on an atom
                case ATOM_VALUE:
                    index = readMolfileInt(line, 3) - 1;
                    final String comment = line.substring(7);
                    container.getAtom(offset + index).setProperty(CDKConstants.COMMENT, comment);
                    break;

                // G  aaappp
                // x...
                //
                // Abbreviation is required for compatibility with previous versions of MDL ISIS/Desktop which
                // allowed abbreviations with only one attachment. The attachment is denoted by two atom
                // numbers, aaa and ppp. All of the atoms on the aaa side of the bond formed by aaa-ppp are
                // abbreviated. The coordinates of the abbreviation are the coordinates of aaa. The text of the
                // abbreviation is on the following line (x...). In current versions of ISIS, abbreviations can have any
                // number of attachments and are written out using the Sgroup appendixes. However, any ISIS
                // abbreviations that do have one attachment are also written out in the old style, again for
                // compatibility with older ISIS versions, but this behavior might not be supported in future
                // versions.
                case GROUP_ABBREVIATION:
                    // not supported, existing parsing doesn't do what is
                    // mentioned in the specification above
                    // final int    from  = readMolfileInt(line, 3) - 1;
                    // final int    to    = readMolfileInt(line, 6) - 1;
                    final String group = input.readLine();
                    if (group == null) return;
                    break;

                // M  CHGnn8 aaa vvv ...
                //
                // vvv: -15 to +15. Default of 0 = uncharged atom. When present, this property supersedes
                //      all charge and radical values in the atom block, forcing a 0 charge on all atoms not
                //      listed in an M CHG or M RAD line.
                case M_CHG:
                    count = readUInt(line, 6, 3);
                    for (int i = 0, st = 10; i < count && st + 7 <= length; i++, st += 8) {
                        index = readMolfileInt(line, st) - 1;
                        int charge = readMolfileInt(line, st + 4);
                        container.getAtom(offset + index).setFormalCharge(charge);
                    }
                    break;

                // M  ISOnn8 aaa vvv ...
                //
                // vvv: Absolute mass of the atom isotope as a positive integer. When present, this property
                //      supersedes all isotope values in the atom block. Default (no entry) means natural
                //      abundance. The difference between this absolute mass value and the natural
                //      abundance value specified in the PTABLE.DAT file must be within the range of -18
                //      to +12.
                case M_ISO:
                    count = readUInt(line, 6, 3);
                    for (int i = 0, st = 10; i < count && st + 7 <= length; i++, st += 8) {
                        index = readMolfileInt(line, st) - 1;
                        int mass = readMolfileInt(line, st + 4);
                        if (mass < 0)
                            handleError("Absolute mass number should be >= 0, " + line);
                        else
                            container.getAtom(offset + index).setMassNumber(mass);
                      }
                    break;

                // M  RADnn8 aaa vvv ...
                //
                // vvv: Default of 0 = no radical, 1 = singlet (:), 2 = doublet ( . or ^), 3 = triplet (^^). When
                //      present, this property supersedes all charge and radical values in the atom block,
                //      forcing a 0 (zero) charge and radical on all atoms not listed in an M CHG or
                //      M RAD line.
                case M_RAD:
                    count = readUInt(line, 6, 3);
                    for (int i = 0, st = 10; i < count && st + 7 <= length; i++, st += 8) {
                        index = readMolfileInt(line, st) - 1;
                        int value = readMolfileInt(line, st + 4);
                        SPIN_MULTIPLICITY multiplicity = SPIN_MULTIPLICITY.ofValue(value);

                        for (int e = 0; e < multiplicity.getSingleElectrons(); e++)
                            container.addSingleElectron(offset + index);
                    }
                    break;

                // M  RGPnn8 aaa rrr ...
                //
                // rrr: Rgroup number, value from 1 to 32 *, labels position of Rgroup on root.
                //
                // see also, RGroupQueryReader
                case M_RGP:
                    count = readUInt(line, 6, 3);
                    for (int i = 0, st = 10; i < count && st + 7 <= length; i++, st += 8) {
                        index = readMolfileInt(line, st) - 1;
                        int number = readMolfileInt(line, st + 4);
                        label(container, offset + index, "R" + number);
                    }
                    break;

                // M  ZZC aaa c...
                // 
                // c: first character of the label, extends to EOL.
                //
                // Proprietary atom labels created by ACD/Labs ChemSketch using the Manual Numbering Tool.
                // This atom property appears to be undocumented, but experimentation leads to the following
                // specification (tested with ACD/ChemSketch version 12.00 Build 29305, 25 Nov 2008)
                //
                // It's not necessary to label any/all atoms but if a label is present, the following applies:
                //
                // The atom label(s) consist of an optional prefix, a required numeric label, and optional suffix.
                //                         
                // The numeric label is an integer in the range 0 - 999 inclusive.
                // 
                // If present, the prefix and suffix can each contain 1 - 50 characters, from the set of printable 
                // ASCII characters shown here
                //                            
                //    !"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_`abcdefghijklmnopqrstuvwxyz{|}~"
                //                    
                // In addition, both the prefix and suffix may contain leading and/or trailing and/or embedded 
                // whitespace, included within the limit of 50 characters. These should be preserved when read.
                //                    
                // Long labels in the mol/sdfile are not truncated or wrapped onto multiple lines. As a result, the
                // line could be 114 characters in length (excluding the newline).
                //
                // By stopping and restarting the Manual Numbering Tool, it's possible to create non-sequential
                // or even duplicate numbers or labels. This is reasonable for the intended purpose of the tool - 
                // labelling the structure as you wish. If unique labels are required, downstream processing will be
                // necessary to enforce this.
                //
                case M_ZZC:
                    if (mode == Mode.STRICT) {
                        throw new CDKException("Atom property ZZC is illegal in STRICT mode");
                    }
                    index = readMolfileInt(line, 7) - 1;
                    String atomLabel = line.substring(11);  // DO NOT TRIM
                    container.getAtom(offset + index).setProperty(CDKConstants.ACDLABS_LABEL, atomLabel);
                    break;

                // M STYnn8 sss ttt ...
                //  sss: Sgroup number
                //  ttt: Sgroup type: SUP = abbreviation Sgroup (formerly called superatom), MUL = multiple group,
                //                    SRU = SRU type, MON = monomer, MER = Mer type, COP = copolymer, CRO = crosslink,
                //                    MOD = modification, GRA = graft, COM = component, MIX = mixture,
                //                    FOR = formulation, DAT = data Sgroup, ANY = any polymer, GEN = generic.
                //
                // Note: For a given Sgroup, an STY line giving its type must appear before any other line that
                //       supplies information about it. For a data Sgroup, an SDT line must describe the data
                //       field before the SCD and SED lines that contain the data (see Data Sgroup Data below).
                //       When a data Sgroup is linked to another Sgroup, the Sgroup must already have been defined.
                //
                // Sgroups can be in any order on the Sgroup Type line. Brackets are drawn around Sgroups with the
                // M SDI lines defining the coordinates.
                case M_STY:
                    count = readMolfileInt(line, 6);
                    for (int i = 0; i < count; i++) {
                        lnOffset = 10 + (i * 8);
                        index = readMolfileInt(line, lnOffset);

                        if (mode == Mode.STRICT && sgroups.containsKey(index))
                            handleError("STY line must appear before any other line that supplies Sgroup information");

                        sgroup = new Sgroup();
                        sgroups.put(index, sgroup);

                        SgroupType type = SgroupType.parseCtabKey(line.substring(lnOffset + 4, lnOffset + 7));
                        if (type != null)
                            sgroup.setType(type);
                    }
                    break;

                // Sgroup Subtype [Sgroup]
                // M  SSTnn8 sss ttt ...
                // ttt: Polymer Sgroup subtypes: ALT = alternating, RAN = random, BLO = block
                case M_SST:
                    count = readMolfileInt(line, 6);
                    for (int i = 0, st = 10; i < count && st + 7 <= length; i++, st += 8) {
                        sgroup = ensureSgroup(sgroups,
                                              readMolfileInt(line, st));
                        if (mode == Mode.STRICT && sgroup.getType() != SgroupType.CtabCopolymer)
                            handleError("SST (Sgroup Subtype) specified for a non co-polymer group");

                        String sst = line.substring(st+4, st+7);

                        if (mode == Mode.STRICT && !("ALT".equals(sst) || "RAN".equals(sst) || "BLO".equals(sst)))
                            handleError("Invalid sgroup subtype: " + sst + " expected (ALT, RAN, or BLO)");

                        sgroup.putValue(SgroupKey.CtabSubType, sst);
                    }
                    break;

                // Sgroup Atom List [Sgroup]
                // M   SAL sssn15 aaa ...
                // aaa: Atoms in Sgroup sss
                case M_SAL:
                    sgroup = ensureSgroup(sgroups, readMolfileInt(line, 7));
                    count  = readMolfileInt(line, 10);
                    for (int i = 0, st = 14; i < count && st + 3 <= length; i++, st += 4) {
                        index = readMolfileInt(line, st) - 1;
                        sgroup.addAtom(container.getAtom(offset + index));
                    }
                    break;


                // Sgroup Bond List [Sgroup]
                // M  SBL sssn15 bbb ...
                // bbb: Bonds in Sgroup sss.
                // (For data Sgroups, bbb’s are the containment bonds, for all other
                //  Sgroup types, bbb’s are crossing bonds.)
                case M_SBL:
                    sgroup = ensureSgroup(sgroups, readMolfileInt(line, 7));
                    count = readMolfileInt(line, 10);
                    for (int i = 0, st = 14; i < count && st + 3 <= length; i++, st += 4) {
                        index = readMolfileInt(line, st) - 1;
                        sgroup.addBond(container.getBond(offset + index));
                    }
                    break;

                // Sgroup Hierarchy Information [Sgroup]
                // M  SPLnn8 ccc ppp ...
                //   ccc: Sgroup index of the child Sgroup
                //   ppp: Sgroup index of the parent Sgroup (ccc and ppp must already be defined via an
                //        STY line prior to encountering this line)
                case M_SPL:
                    count = readMolfileInt(line, 6);
                    for (int i = 0, st = 10; i < count && st + 6 <= length; i++, st += 8) {
                        sgroup = ensureSgroup(sgroups, readMolfileInt(line, st));
                        sgroup.addParent(ensureSgroup(sgroups, readMolfileInt(line, st+4)));
                    }
                    break;

                // Sgroup Connectivity [Sgroup]
                // M  SCNnn8 sss ttt ...
                // ttt: HH = head-to-head, HT = head-to-tail, EU = either unknown.
                // Left justified.
                case M_SCN:
                    count = readMolfileInt(line, 6);
                    for (int i = 0, st = 10; i < count && st + 6 <= length; i++, st += 8) {
                        sgroup = ensureSgroup(sgroups,
                                              readMolfileInt(line, st));
                        String con = line.substring(st + 4, Math.min(length, st + 7)).trim();
                        if (mode == Mode.STRICT && !("HH".equals(con) || "HT".equals(con) || "EU".equals(con)))
                            handleError("Unknown SCN type (expected: HH, HT, or EU) was " + con);
                        sgroup.putValue(SgroupKey.CtabConnectivity,
                                        con);
                    }
                    break;

                // Sgroup Display Information
                // M SDI sssnn4 x1 y1 x2 y2
                // x1,y1, Coordinates of bracket endpoints
                // x2,y2:
                case M_SDI:
                    sgroup = ensureSgroup(sgroups, readMolfileInt(line, 7));
                    count = readMolfileInt(line, 10);
                    assert count == 4; // fixed?
                    sgroup.addBracket(new SgroupBracket(readMDLCoordinate(line, 13),
                                                        readMDLCoordinate(line, 23),
                                                        readMDLCoordinate(line, 33),
                                                        readMDLCoordinate(line, 43)));
                    break;

                // Sgroup subscript
                // M SMT sss m...
                // m...: Text of subscript Sgroup sss.
                // (For multiple groups, m... is the text representation of the multiple group multiplier.
                //  For abbreviation Sgroups, m... is the text of the abbreviation Sgroup label.)
                case M_SMT:
                    sgroup = ensureSgroup(sgroups, readMolfileInt(line, 7));
                    sgroup.putValue(SgroupKey.CtabSubScript,
                                    line.substring(11).trim());
                    break;

                // Sgroup Bracket Style
                // The format for the Sgroup bracket style is as follows:
                // M  SBTnn8 sss ttt ...
                // where:
                //   sss: Index of Sgroup
                //   ttt: Bracket display style: 0 = default, 1 = curved (parenthetic) brackets
                // This appendix supports altering the display style of the Sgroup brackets.
                case M_SBT:
                    count = readMolfileInt(line, 6);
                    for (int i = 0, st = 10; i < count && st + 7 <= length; i++, st += 8) {
                        sgroup = ensureSgroup(sgroups,
                                              readMolfileInt(line, st));
                        sgroup.putValue(SgroupKey.CtabBracketStyle,
                                        readMolfileInt(line, st+4));
                    }
                    break;

                // Sgroup Expansion
                // M  SDS EXPn15 sss ...
                // sss: Sgroup index of expanded abbreviation Sgroups
                case M_SDS:

                    if ("EXP".equals(line.substring(7, 10))) {
                        count = readMolfileInt(line, 10);
                        for (int i = 0, st = 14; i < count && st + 3 <= length; i++, st += 4) {
                            sgroup = ensureSgroup(sgroups, readMolfileInt(line, st));
                            sgroup.putValue(SgroupKey.CtabExpansion, true);
                        }
                    } else if (mode == Mode.STRICT) {
                        handleError("Expected EXP to follow SDS tag");
                    }
                    break;

                // Multiple Group Parent Atom List [Sgroup]
                // M SPA sssn15 aaa ...
                // aaa: Atoms in paradigmatic repeating unit of multiple group sss
                // Note: To ensure that all current molfile readers consistently
                //       interpret chemical structures, multiple groups are written
                //       in their fully expanded state to the molfile. The M SPA atom
                //       list is a subset of the full atom list that is defined by the
                //       Sgroup Atom List M SAL entry.
                case M_SPA:
                    sgroup = ensureSgroup(sgroups, readMolfileInt(line, 7));
                    count  = readMolfileInt(line, 10);
                    Set<IAtom> parentAtomList = sgroup.getValue(SgroupKey.CtabParentAtomList);
                    if (parentAtomList == null) {
                        sgroup.putValue(SgroupKey.CtabParentAtomList, parentAtomList = new HashSet<IAtom>());
                    }
                    for (int i = 0, st = 14; i < count && st + 3 <= length; i++, st += 4) {
                        index = readMolfileInt(line, st) - 1;
                        parentAtomList.add(container.getAtom(offset + index));
                    }
                    break;

                // Sgroup Component Numbers [Sgroup]
                // M  SNCnn8 sss ooo ...
                // sss: Index of component Sgroup
                // ooo: Integer component order (1...256). This limit applies only to MACCS-II
                case M_SNC:
                    count = readMolfileInt(line, 6);
                    for (int i = 0, st = 10; i < count && st + 7 <= length; i++, st += 8) {
                        sgroup = ensureSgroup(sgroups,
                                              readMolfileInt(line, st));
                        sgroup.putValue(SgroupKey.CtabComponentNumber,
                                        readMolfileInt(line, st+4));
                    }
                    break;

                // M  END
                //
                // This entry goes at the end of the properties block and is required for molfiles which contain a
                // version stamp in the counts line.
                case M_END:
                    break LINES;
            }
        }

        // check of ill specified atomic mass
        for (IAtom atom : container.atoms()) {
            if (atom.getMassNumber() != null && atom.getMassNumber() < 0) {
              handleError("Unstable use of mass delta on " + atom.getSymbol() + " please use M  ISO");
              atom.setMassNumber(null);
            }
        }


        if (!sgroups.isEmpty()) {
            // load Sgroups into molecule, first we downcast
            List<Sgroup> sgroupOrgList = new ArrayList<>(sgroups.values());
            List<Sgroup> sgroupCpyList = new ArrayList<>(sgroupOrgList.size());
            for (int i = 0; i < sgroupOrgList.size(); i++) {
                Sgroup cpy = sgroupOrgList.get(i).downcast();
                sgroupCpyList.add(cpy);
            }
            // update replaced parents
            for (int i = 0; i < sgroupOrgList.size(); i++) {
                Sgroup newSgroup = sgroupCpyList.get(i);
                Set<Sgroup> oldParents = new HashSet<>(newSgroup.getParents());
                newSgroup.removeParents(oldParents);
                for (Sgroup parent : oldParents) {
                    newSgroup.addParent(sgroupCpyList.get(sgroupOrgList.indexOf(parent)));
                }
            }
            container.setProperty(CDKConstants.CTAB_SGROUPS, sgroupCpyList);
        }
    }


    private Sgroup ensureSgroup(Map<Integer, Sgroup> map, int idx) throws CDKException {
        Sgroup sgroup = map.get(idx);
        if (sgroup == null) {
            if (mode == Mode.STRICT)
                handleError("Sgroups must first be defined by a STY property");
            map.put(idx, sgroup = new Sgroup());
        }
        return sgroup;
    }

    /**
     * Convert an MDL V2000 stereo value to the CDK {@link IBond.Stereo}. The
     * method should only be invoked for single/double bonds. If strict mode is
     * enabled irrational bond stereo/types cause errors (e.g. up double bond).
     *
     * @param stereo stereo value
     * @param type   bond type
     * @return bond stereo
     * @throws CDKException the stereo value was invalid (strict mode).
     */
    private IBond.Stereo toStereo(final int stereo, final int type) throws CDKException {
        switch (stereo) {
            case 0:
                return type == 2 ? IBond.Stereo.E_Z_BY_COORDINATES : IBond.Stereo.NONE;
            case 1:
                if (mode == Mode.STRICT && type == 2)
                    throw new CDKException("stereo flag was 'up' but bond order was 2");
                return IBond.Stereo.UP;
            case 3:
                if (mode == Mode.STRICT && type == 1)
                    throw new CDKException("stereo flag was 'cis/trans' but bond order was 1");
                return IBond.Stereo.E_OR_Z;
            case 4:
                if (mode == Mode.STRICT && type == 2)
                    throw new CDKException("stereo flag was 'up/down' but bond order was 2");
                return IBond.Stereo.UP_OR_DOWN;
            case 6:
                if (mode == Mode.STRICT && type == 2)
                    throw new CDKException("stereo flag was 'down' but bond order was 2");
                return IBond.Stereo.DOWN;
        }
        if (mode == Mode.STRICT) throw new CDKException("unknown bond stereo type: " + stereo);
        return IBond.Stereo.NONE;
    }

    /**
     * Determine the length of the line excluding trailing whitespace.
     *
     * @param str a string
     * @return the length when trailing white space is removed
     */
    static int length(final String str) {
        int i = str.length() - 1;
        while (i >= 0 && str.charAt(i) == ' ') {
            i--;
        }
        return i + 1;
    }

    /**
     * Create an atom for the provided symbol. If the atom symbol is a periodic
     * element a new 'Atom' is created otherwise if the symbol is an allowed
     * query atom ('R', 'Q', 'A', '*', 'L', 'LP') a new 'PseudoAtom' is created.
     * If the symbol is invalid an exception is thrown.
     *
     * @param symbol  input symbol
     * @param builder chem object builder
     * @return a new atom
     * @throws CDKException the symbol is not allowed
     */
    private IAtom createAtom(String symbol, IChemObjectBuilder builder, int lineNum) throws CDKException {
        final Elements elem = Elements.ofString(symbol);
        if (elem != Elements.Unknown) {
            IAtom atom = builder.newAtom();
            atom.setSymbol(elem.symbol());
            atom.setAtomicNumber(elem.number());
            return atom;
        }
        if (symbol.equals("D") && interpretHydrogenIsotopes.isSet()) {
            handleError("invalid symbol: " + symbol, lineNum, 31, 33);
            IAtom atom = builder.newInstance(IAtom.class, "H");
            atom.setMassNumber(2);
            return atom;
        }
        if (symbol.equals("T") && interpretHydrogenIsotopes.isSet()) {
            handleError("invalid symbol: " + symbol, lineNum, 31, 33);
            IAtom atom = builder.newInstance(IAtom.class, "H");
            atom.setMassNumber(3);
            return atom;
        }

        if (!isPseudoElement(symbol)) {
            handleError("invalid symbol: " + symbol, lineNum, 31, 34);
            // when strict only accept labels from the specification
            if (mode == Mode.STRICT) throw new CDKException("invalid symbol: " + symbol);
        }

        // will be renumbered later by RGP if R1, R2 etc. if not renumbered then
        // 'R' is a better label than 'R#' if now RGP is specified
        if (symbol.equals("R#")) symbol = "R";

        IAtom atom = builder.newInstance(IPseudoAtom.class, symbol);
        atom.setSymbol(symbol);
        atom.setAtomicNumber(0); // avoid NPE downstream

        return atom;
    }


    /**
     * Is the atom symbol a non-periodic element (i.e. pseudo). Valid pseudo
     * atoms are 'R#', 'A', 'Q', '*', 'L' and 'LP'. We also accept 'R' but this
     * is not listed in the specification.
     *
     * @param symbol a symbol from the input
     * @return the symbol is a valid pseudo element
     */
    static boolean isPseudoElement(final String symbol) {
        return PSEUDO_LABELS.contains(symbol);
    }

    /**
     * Read a coordinate from an MDL input. The MDL V2000 input coordinate has
     * 10 characters, 4 significant figures and is prefixed with whitespace for
     * padding: 'xxxxx.xxxx'. Knowing the format allows us to use an optimised
     * parser which does not consider exponents etc.
     *
     * @param line   input line
     * @param offset first character of the coordinate
     * @return the specified value
     * @throws CDKException the coordinates specification was not valid
     */
    double readMDLCoordinate(final String line, int offset) throws CDKException {
        // to be valid the decimal should be at the fifth index (4 sig fig)
        if (line.charAt(offset + 5) != '.') {
            handleError("Bad coordinate format specified, expected 4 decimal places: " + line.substring(offset));
            int start = offset;
            while (line.charAt(start) == ' ' && start < offset + 9)
                start++;

            int dot = -1;
            int end = start;
            for (char c = line.charAt(end); c != ' ' && end < offset + 9; c = line.charAt(end), end++) {
                if(c == '.')
                    dot = end;
            }

            if(start == end) {

                return 0.0;
            } else if(dot != -1) {

                int sign = sign(line.charAt(start));
                if (sign < 0) start++;

                int integral = readUInt(line, start, dot - start - 1);
                int fraction = readUInt(line, dot, end - dot);

                return sign * (integral * 10000L + fraction) / 10000d;
            } else {

                return Double.parseDouble(line.substring(start, end));
            }
        } else {
            int start = offset;
            while (line.charAt(start) == ' ')
                start++;
            int sign = sign(line.charAt(start));
            if (sign < 0) start++;
            int integral = readUInt(line, start, (offset + 5) - start);
            int fraction = readUInt(line, offset + 6, 4);
            return sign * (integral * 10000L + fraction) / 10000d;
        }
    }

    /**
     * Convert the a character (from an MDL V2000 input) to a charge value:
     * 1 = +1, 2 = +2, 3 = +3, 4 = doublet radical, 5 = -1, 6 = -2, 7 = -3.
     *
     * @param c a character
     * @return formal charge
     */
    private static int toCharge(final char c) {
        switch (c) {
            case '1':
                return +3;
            case '2':
                return +2;
            case '3':
                return +1;
            case '4':
                return 0; // doublet radical - superseded by M  RAD
            case '5':
                return -1;
            case '6':
                return -2;
            case '7':
                return -3;
        }
        return 0;
    }

    /**
     * Obtain the sign of the character, -1 if the character is '-', +1
     * otherwise.
     *
     * @param c a character
     * @return the sign
     */
    private static int sign(final char c) {
        return c == '-' ? -1 : +1;
    }

    /**
     * Convert a character (ASCII code points) to an integer. If the character
     * was not a digit (i.e. space) the value defaults to 0.
     *
     * @param c a character
     * @return the numerical value
     */
    private static int toInt(final char c) {
        // Character.getNumericalValue allows all of unicode which we don't want
        // or need it - imagine an MDL file with roman numerals!
        return c >= '0' && c <= '9' ? c - '0' : 0;
    }

    /**
     * Read an unsigned int value from the given index with the expected number
     * of digits.
     *
     * @param line   input line
     * @param index  start index
     * @param digits number of digits (max)
     * @return an unsigned int
     */
    private static int readUInt(final String line, int index, int digits) {
        int result = 0;
        while (digits-- > 0)
            result = (result * 10) + toInt(line.charAt(index++));
        return result;
    }

    /**
     * Optimised method for reading a integer from 3 characters in a string at a
     * specified index. MDL V2000 Molfile make heavy use of the 3 character ints
     * in the atom/bond and property blocks. The integer may be signed and
     * pre/post padded with white space.
     *
     * @param line  input
     * @param index start index
     * @return the value specified in the string
     */
    private static int readMolfileInt(final String line, final int index) {
        int sign = 1;
        int result = 0;
        char c;
        switch ((c = line.charAt(index))) {
            case ' ':
                break;
            case '-':
                sign = -1;
                break;
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                result = (c - '0');
                break;
            default:
                return 0;
        }
        if (index+1 == line.length())
            return sign * result;
        switch ((c = line.charAt(index + 1))) {
            case ' ':
                if (result > 0) return sign * result;
                break;
            case '-':
                if (result > 0) return sign * result;
                sign = -1;
                break;
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                result = (result * 10) + (c - '0');
                break;
            default:
                return sign * result;
        }
        if (index+2 == line.length())
            return sign * result;
        switch ((c = line.charAt(index + 2))) {
            case ' ':
                if (result > 0) return sign * result;
                break;
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                result = (result * 10) + (c - '0');
                break;
            default:
                return sign * result;
        }
        return sign * result;
    }

    /**
     * Labels the atom at the specified index with the provide label. If the
     * atom was not already a pseudo atom then the original atom is replaced.
     *
     * @param container structure
     * @param index     atom index to replace
     * @param label     the label for the atom
     * @see IPseudoAtom#setLabel(String)
     */
    static void label(final IAtomContainer container, final int index, final String label) {
        final IAtom atom = container.getAtom(index);
        final IPseudoAtom pseudoAtom = atom instanceof IPseudoAtom ? (IPseudoAtom) atom : container.getBuilder()
                .newInstance(IPseudoAtom.class);
        if (atom.equals(pseudoAtom)) {
            pseudoAtom.setLabel(label);
        } else {
            pseudoAtom.setSymbol(label);
            pseudoAtom.setAtomicNumber(atom.getAtomicNumber());
            pseudoAtom.setPoint2d(atom.getPoint2d());
            pseudoAtom.setPoint3d(atom.getPoint3d());
            pseudoAtom.setMassNumber(atom.getMassNumber());
            pseudoAtom.setFormalCharge(atom.getFormalCharge());
            pseudoAtom.setValency(atom.getValency());
            pseudoAtom.setLabel(label);
            // XXX: would be faster to track all replacements and do it all in one
            AtomContainerManipulator.replaceAtomByAtom(container, atom, pseudoAtom);
        }
    }

    /**
     * Reads an atom from the input allowing for non-standard formatting (i.e
     * truncated lines) and chemical shifts.
     *
     * @param line      input line
     * @param builder   chem object builder
     * @param linecount the current line count
     * @return an atom to add to a container
     * @throws CDKException a CDK error occurred
     * @throws IOException  the isotopes file could not be read
     */
    private IAtom readAtomSlow(String line, IChemObjectBuilder builder, int linecount) throws CDKException, IOException {
        IAtom atom;
        Matcher trailingSpaceMatcher = TRAILING_SPACE.matcher(line);
        if (trailingSpaceMatcher.find()) {
            handleError("Trailing space found", linecount, trailingSpaceMatcher.start(), trailingSpaceMatcher.end());
            line = trailingSpaceMatcher.replaceAll("");
        }
        double x = Double.parseDouble(line.substring(0, 10).trim());
        double y = Double.parseDouble(line.substring(10, 20).trim());
        double z = Double.parseDouble(line.substring(20, 30).trim());

        String element = line.substring(31, Math.min(line.length(), 34)).trim();
        if (line.length() < 34) {
            handleError("Element atom type does not follow V2000 format type should of length three"
                    + " and padded with space if required", linecount, 31, 34);
        }

        logger.debug("Atom type: ", element);
        IsotopeFactory isotopeFactory = Isotopes.getInstance();
        if (isotopeFactory.isElement(element)) {
            atom = isotopeFactory.configure(builder.newInstance(IAtom.class, element));
        } else if ("A".equals(element)) {
            atom = builder.newInstance(IPseudoAtom.class, element);
        } else if ("Q".equals(element)) {
            atom = builder.newInstance(IPseudoAtom.class, element);
        } else if ("*".equals(element)) {
            atom = builder.newInstance(IPseudoAtom.class, element);
        } else if ("LP".equals(element)) {
            atom = builder.newInstance(IPseudoAtom.class, element);
        } else if ("L".equals(element)) {
            atom = builder.newInstance(IPseudoAtom.class, element);
        } else if (element.equals("R") || (element.length() > 0 && element.charAt(0) == 'R')) {
            logger.debug("Atom ", element, " is not an regular element. Creating a PseudoAtom.");
            //check if the element is R
            String[] rGroup = element.split("^R");
            if (rGroup.length > 1) {
                try {
                    element = "R" + Integer.valueOf(rGroup[(rGroup.length - 1)]);
                    atom = builder.newInstance(IPseudoAtom.class, element);

                } catch (Exception ex) {
                    // This happens for atoms labeled "R#".
                    // The Rnumber may be set later on, using RGP line
                    atom = builder.newInstance(IPseudoAtom.class, "R");
                }
            } else {
                atom = builder.newInstance(IPseudoAtom.class, element);
            }
        } else {
            handleError("Invalid element type. Must be an existing " + "element, or one in: A, Q, L, LP, *.",
                    linecount, 32, 35);
            atom = builder.newInstance(IPseudoAtom.class, element);
            atom.setSymbol(element);
        }

        // store as 3D for now, convert to 2D (if totalZ == 0.0) later
        atom.setPoint3d(new Point3d(x, y, z));

        // parse further fields
        if (line.length() >= 36) {
            String massDiffString = line.substring(34, 36).trim();
            logger.debug("Mass difference: ", massDiffString);
            if (!(atom instanceof IPseudoAtom)) {
                try {
                    int massDiff = Integer.parseInt(massDiffString);
                    if (massDiff != 0) {
                        IIsotope major = Isotopes.getInstance().getMajorIsotope(element);
                        atom.setMassNumber(major.getMassNumber() + massDiff);
                    }
                } catch (NumberFormatException | IOException exception) {
                    handleError("Could not parse mass difference field.", linecount, 35, 37, exception);
                }
            } else {
                logger.error("Cannot set mass difference for a non-element!");
            }
        } else {
            handleError("Mass difference is missing", linecount, 34, 36);
        }

        // set the stereo partiy
        Integer parity = line.length() > 41 ? Character.digit(line.charAt(41), 10) : 0;
        atom.setStereoParity(parity);

        if (line.length() >= 51) {
            String valenceString = removeNonDigits(line.substring(48, 51));
            logger.debug("Valence: ", valenceString);
            if (!(atom instanceof IPseudoAtom)) {
                try {
                    int valence = Integer.parseInt(valenceString);
                    if (valence != 0) {
                        //15 is defined as 0 in mol files
                        if (valence == 15)
                            atom.setValency(0);
                        else
                            atom.setValency(valence);
                    }
                } catch (Exception exception) {
                    handleError("Could not parse valence information field", linecount, 49, 52, exception);
                }
            } else {
                logger.error("Cannot set valence information for a non-element!");
            }
        }

        if (line.length() >= 39) {
            String chargeCodeString = line.substring(36, 39).trim();
            logger.debug("Atom charge code: ", chargeCodeString);
            int chargeCode = Integer.parseInt(chargeCodeString);
            if (chargeCode == 0) {
                // uncharged species
            } else if (chargeCode == 1) {
                atom.setFormalCharge(+3);
            } else if (chargeCode == 2) {
                atom.setFormalCharge(+2);
            } else if (chargeCode == 3) {
                atom.setFormalCharge(+1);
            } else if (chargeCode == 4) {
            } else if (chargeCode == 5) {
                atom.setFormalCharge(-1);
            } else if (chargeCode == 6) {
                atom.setFormalCharge(-2);
            } else if (chargeCode == 7) {
                atom.setFormalCharge(-3);
            }
        } else {
            handleError("Atom charge is missing", linecount, 36, 39);
        }

        try {
            // read the mmm field as position 61-63
            String reactionAtomIDString = line.substring(60, 63).trim();
            logger.debug("Parsing mapping id: ", reactionAtomIDString);
            try {
                int reactionAtomID = Integer.parseInt(reactionAtomIDString);
                if (reactionAtomID != 0) {
                    atom.setProperty(CDKConstants.ATOM_ATOM_MAPPING, reactionAtomID);
                }
            } catch (Exception exception) {
                logger.error("Mapping number ", reactionAtomIDString, " is not an integer.");
                logger.debug(exception);
            }
        } catch (Exception exception) {
            // older mol files don't have all these fields...
            logger.warn("A few fields are missing. Older MDL MOL file?");
        }

        //shk3: This reads shifts from after the molecule. I don't think this is an official format, but I saw it frequently 80=>78 for alk
        if (line.length() >= 78) {
            double shift = Double.parseDouble(line.substring(69, 80).trim());
            atom.setProperty("first shift", shift);
        }
        if (line.length() >= 87) {
            double shift = Double.parseDouble(line.substring(79, 87).trim());
            atom.setProperty("second shift", shift);
        }

        return atom;
    }

    /**
     * Read a bond line from an MDL V2000 molfile bond block (slow). The
     * explicit valence is also modified.
     *
     * @param line      the input from the bond block
     * @param builder   chem object builder
     * @param atoms     array of atoms
     * @param explicitValence stores the explicit valence of each atom (bond order sum)
     * @param linecount the current line count
     * @return a new bond
     * @throws CDKException the bond line could not be parsed
     */
    private IBond readBondSlow(String line, IChemObjectBuilder builder, IAtom[] atoms, int[] explicitValence,
            int linecount) throws CDKException {
        int atom1 = Integer.parseInt(line.substring(0, 3).trim());
        int atom2 = Integer.parseInt(line.substring(3, 6).trim());
        int order = Integer.parseInt(line.substring(6, 9).trim());
        IBond.Stereo stereo = null;
        if (line.length() >= 12) {
            int mdlStereo = line.length() > 12 ? Integer.parseInt(line.substring(9, 12).trim()) : Integer.parseInt(line
                    .substring(9).trim());
            if (mdlStereo == 1) {
                // MDL up bond
                stereo = IBond.Stereo.UP;
            } else if (mdlStereo == 6) {
                // MDL down bond
                stereo = IBond.Stereo.DOWN;
            } else if (mdlStereo == 0) {
                if (order == 2) {
                    // double bond stereo defined by coordinates
                    stereo = IBond.Stereo.E_Z_BY_COORDINATES;
                } else {
                    // bond has no stereochemistry
                    stereo = IBond.Stereo.NONE;
                }
            } else if (mdlStereo == 3 && order == 2) {
                // unknown E/Z stereochemistry
                stereo = IBond.Stereo.E_OR_Z;
            } else if (mdlStereo == 4) {
                //MDL bond undefined
                stereo = IBond.Stereo.UP_OR_DOWN;
            }
        } else {
            handleError("Missing expected stereo field at line: ", linecount, 10, 12);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Bond: " + atom1 + " - " + atom2 + "; order " + order);
        }
        // interpret CTfile's special bond orders
        IAtom a1 = atoms[atom1 - 1];
        IAtom a2 = atoms[atom2 - 1];
        IBond newBond;
        if (order >= 1 && order <= 3) {
            IBond.Order cdkOrder = IBond.Order.SINGLE;
            if (order == 2) cdkOrder = IBond.Order.DOUBLE;
            if (order == 3) cdkOrder = IBond.Order.TRIPLE;
            if (stereo != null) {
                newBond = builder.newInstance(IBond.class, a1, a2, cdkOrder, stereo);
            } else {
                newBond = builder.newInstance(IBond.class, a1, a2, cdkOrder);
            }
            explicitValence[atom1 - 1] += cdkOrder.numeric();
            explicitValence[atom2 - 1] += cdkOrder.numeric();
        } else if (order == 4) {
            // aromatic bond
            if (stereo != null) {
                newBond = builder.newInstance(IBond.class, a1, a2, IBond.Order.UNSET, stereo);
            } else {
                newBond = builder.newInstance(IBond.class, a1, a2, IBond.Order.UNSET);
            }
            // mark both atoms and the bond as aromatic and raise the SINGLE_OR_DOUBLE-flag
            newBond.setFlag(CDKConstants.SINGLE_OR_DOUBLE, true);
            newBond.setFlag(CDKConstants.ISAROMATIC, true);
            a1.setFlag(CDKConstants.ISAROMATIC, true);
            a2.setFlag(CDKConstants.ISAROMATIC, true);
            explicitValence[atom1 - 1] = explicitValence[atom2 - 1] = Integer.MIN_VALUE;
        } else {
            newBond = new QueryBond(builder);
            IAtom[] bondAtoms = {a1, a2};
            newBond.setAtoms(bondAtoms);
            switch (order) {
                case 5:
                    ((QueryBond) newBond).getExpression()
                                         .setPrimitive(Expr.Type.SINGLE_OR_DOUBLE);
                    break;
                case 6:
                    ((QueryBond) newBond).getExpression()
                                         .setPrimitive(Expr.Type.SINGLE_OR_AROMATIC);
                    break;
                case 7:
                    ((QueryBond) newBond).getExpression()
                                         .setPrimitive(Expr.Type.DOUBLE_OR_AROMATIC);
                    break;
                case 8:
                    ((QueryBond) newBond).getExpression()
                                         .setPrimitive(Expr.Type.TRUE);
                    break;
            }
            newBond.setStereo(stereo);
            explicitValence[atom1 - 1] = explicitValence[atom2 - 1] = Integer.MIN_VALUE;
        }
        return newBond;
    }

    /**
     * Read the properties from the V2000 block (slow).
     *
     * @param input     input source
     * @param container the container with the atoms / bonds loaded
     * @param nAtoms    the number of atoms in the atom block
     * @param linecount the line count
     * @throws IOException internal low-level error
     * @throws CDKException the properties block could not be parsed
     */
    private void readPropertiesSlow(BufferedReader input, IAtomContainer container, int nAtoms, int linecount)
            throws IOException, CDKException {
        logger.info("Reading property block");
        String line;
        while (true) {
            line = input.readLine();
            linecount++;
            if (line == null) {
                handleError("The expected property block is missing!", linecount, 0, 0);
            }
            if (line.startsWith("M  END")) break;

            boolean lineRead = false;
            if (line.startsWith("M  CHG")) {
                // FIXME: if this is encountered for the first time, all
                // atom charges should be set to zero first!
                int infoCount = Integer.parseInt(line.substring(6, 9).trim());
                StringTokenizer st = new StringTokenizer(line.substring(9));
                for (int i = 1; i <= infoCount; i++) {
                    String token = st.nextToken();
                    int atomNumber = Integer.parseInt(token.trim());
                    token = st.nextToken();
                    int charge = Integer.parseInt(token.trim());
                    container.getAtom(atomNumber - 1).setFormalCharge(charge);
                }
            } else if (line.matches("A\\s{1,4}\\d+")) {
                // Reads the pseudo atom property from the mol file

                // The atom number of the to replaced atom
                int aliasAtomNumber = Integer.parseInt(line.replaceFirst("A\\s{1,4}", ""));
                String alias = input.readLine();
                linecount++;
                IAtom aliasAtom = container.getAtom(aliasAtomNumber - 1);

                // skip if already a pseudoatom
                if (aliasAtom instanceof IPseudoAtom) {
                    ((IPseudoAtom) aliasAtom).setLabel(alias);
                    continue;
                }

                IAtom newPseudoAtom = container.getBuilder().newInstance(IPseudoAtom.class, alias);
                newPseudoAtom.setAtomicNumber(aliasAtom.getAtomicNumber());
                if (aliasAtom.getPoint2d() != null) newPseudoAtom.setPoint2d(aliasAtom.getPoint2d());
                if (aliasAtom.getPoint3d() != null) newPseudoAtom.setPoint3d(aliasAtom.getPoint3d());
                AtomContainerManipulator.replaceAtomByAtom(container, aliasAtom, newPseudoAtom);
            } else if (line.startsWith("M  ISO")) {
                try {
                    String countString = line.substring(6, 10).trim();
                    int infoCount = Integer.parseInt(countString);
                    StringTokenizer st = new StringTokenizer(line.substring(10));
                    for (int i = 1; i <= infoCount; i++) {
                        int atomNumber = Integer.parseInt(st.nextToken().trim());
                        int absMass = Integer.parseInt(st.nextToken().trim());
                        if (absMass != 0) {
                            IAtom isotope = container.getAtom(atomNumber - 1);
                            isotope.setMassNumber(absMass);
                        }
                    }
                } catch (NumberFormatException exception) {
                    String error = "Error (" + exception.getMessage() + ") while parsing line " + linecount + ": "
                            + line + " in property block.";
                    logger.error(error);
                    handleError("NumberFormatException in isotope information.", linecount, 7, 11, exception);
                }
            } else if (line.startsWith("M  RAD")) {
                try {
                    String countString = line.substring(6, 9).trim();
                    int infoCount = Integer.parseInt(countString);
                    StringTokenizer st = new StringTokenizer(line.substring(9));
                    for (int i = 1; i <= infoCount; i++) {
                        int atomNumber = Integer.parseInt(st.nextToken().trim());
                        int rad = Integer.parseInt(st.nextToken().trim());
                        MDLV2000Writer.SPIN_MULTIPLICITY spin = MDLV2000Writer.SPIN_MULTIPLICITY.None;
                        if (rad > 0) {
                            IAtom radical = container.getAtom(atomNumber - 1);
                            spin = MDLV2000Writer.SPIN_MULTIPLICITY.ofValue(rad);
                            for (int j = 0; j < spin.getSingleElectrons(); j++) {
                                container.addSingleElectron(container.getBuilder().newInstance(ISingleElectron.class,
                                        radical));
                            }
                        }
                    }
                } catch (NumberFormatException exception) {
                    String error = "Error (" + exception.getMessage() + ") while parsing line " + linecount + ": "
                            + line + " in property block.";
                    logger.error(error);
                    handleError("NumberFormatException in radical information", linecount, 7, 10, exception);
                }
            } else if (line.startsWith("G  ")) {
                try {
                    String atomNumberString = line.substring(3, 6).trim();
                    int atomNumber = Integer.parseInt(atomNumberString);
                    //String whatIsThisString = line.substring(6,9).trim();

                    String atomName = input.readLine();

                    // convert Atom into a PseudoAtom
                    IAtom prevAtom = container.getAtom(atomNumber - 1);
                    IPseudoAtom pseudoAtom = container.getBuilder().newInstance(IPseudoAtom.class, atomName);
                    if (prevAtom.getPoint2d() != null) {
                        pseudoAtom.setPoint2d(prevAtom.getPoint2d());
                    }
                    if (prevAtom.getPoint3d() != null) {
                        pseudoAtom.setPoint3d(prevAtom.getPoint3d());
                    }
                    AtomContainerManipulator.replaceAtomByAtom(container, prevAtom, pseudoAtom);
                } catch (NumberFormatException exception) {
                    String error = "Error (" + exception.toString() + ") while parsing line " + linecount + ": " + line
                            + " in property block.";
                    logger.error(error);
                    handleError("NumberFormatException in group information", linecount, 4, 7, exception);
                }
            } else if (line.startsWith("M  RGP")) {
                StringTokenizer st = new StringTokenizer(line);
                //Ignore first 3 tokens (overhead).
                st.nextToken();
                st.nextToken();
                st.nextToken();
                //Process the R group numbers as defined in RGP line.
                while (st.hasMoreTokens()) {
                    Integer position = Integer.valueOf(st.nextToken());
                    int rNumber = Integer.valueOf(st.nextToken());
                    // the container may have already had atoms before the new atoms were read
                    int index = container.getAtomCount() - nAtoms + position - 1;
                    IPseudoAtom pseudoAtom = (IPseudoAtom) container.getAtom(index);
                    if (pseudoAtom != null) {
                        pseudoAtom.setLabel("R" + rNumber);
                    }
                }
            }
            if (line.startsWith("V  ")) {
                Integer atomNumber = Integer.valueOf(line.substring(3, 6).trim());
                IAtom atomWithComment = container.getAtom(atomNumber - 1);
                atomWithComment.setProperty(CDKConstants.COMMENT, line.substring(7));
            }

            if (!lineRead) {
                logger.warn("Skipping line in property block: ", line);
            }
        }
    }

    /**
     * Read non-structural data from input and store as properties the provided
     * 'container'. Non-structural data appears in a structure data file (SDF)
     * after an Molfile and before the record deliminator ('$$$$'). The data
     * consists of one or more Data Header and Data blocks, an example is seen
     * below.
     *
     * <pre>{@code
     * > 29 <DENSITY>
     * 0.9132 - 20.0
     *
     * > 29 <BOILING.POINT>
     * 63.0 (737 MM)
     * 79.0 (42 MM)
     *
     * > 29 <ALTERNATE.NAMES>
     * SYLVAN
     *
     * > 29 <DATE>
     * 09-23-1980
     *
     * > 29 <CRC.NUMBER>
     * F-0213
     *
     * }</pre>
     *
     *
     * @param input     input source
     * @param container the container
     * @throws IOException an error occur whilst reading the input
     */
    static void readNonStructuralData(final BufferedReader input, final IAtomContainer container) throws IOException {

        String line, header = null;
        boolean wrap = false;

        final StringBuilder data = new StringBuilder(80);

        while (!endOfRecord(line = input.readLine())) {

            final String newHeader = dataHeader(line);

            if (newHeader != null) {

                if (header != null) container.setProperty(header, data.toString());

                header = newHeader;
                wrap = false;
                data.setLength(0);

            } else {

                if (data.length() > 0 || !line.equals(" ")) line = line.trim();

                if (line.isEmpty()) continue;

                if (!wrap && data.length() > 0) data.append('\n');
                data.append(line);

                wrap = line.length() == 80;
            }
        }

        if (header != null) container.setProperty(header, data.toString());
    }

    /**
     * Obtain the field name from a potential SD data header. If the header
     * does not contain a field name, then null is returned. The method does
     * not currently return field numbers (e.g. DT&lt;n&gt;).
     *
     * @param line an input line
     * @return the field name
     */
    static String dataHeader(final String line) {
        if (line.length() > 2 && line.charAt(0) != '>' && line.charAt(1) != ' ') return null;
        int i = line.indexOf('<', 2);
        if (i < 0) return null;
        int j = line.indexOf('>', i);
        if (j < 0) return null;
        return line.substring(i + 1, j);
    }

    /**
     * Is the line the end of a record. A line is the end of a record if it
     * is 'null' or is the SDF deliminator, '$$$$'.
     *
     * @param line a line from the input
     * @return the line indicates the end of a record was reached
     */
    private static boolean endOfRecord(final String line) {
        return line == null || line.equals(RECORD_DELIMITER);
    }

    /**
     * Enumeration of property keys that can be specified in the V2000 property
     * block.
     */
    enum PropertyKey {

        /** Atom Alias. */
        ATOM_ALIAS,

        /** Atom Value. */
        ATOM_VALUE,

        /** Group Abbreviation. */
        GROUP_ABBREVIATION,

        /** Skip lines. */
        SKIP,

        /** Charge [Generic]. */
        M_CHG,

        /** Radical [Generic]. */
        M_RAD,

        /** Isotope [Generic]. */
        M_ISO,

        /** Ring Bond Count [Query]. */
        M_RBC,

        /** Substitution Count [Query]. */
        M_SUB,

        /** Unsaturated Atom [Query]. */
        M_UNS,

        /** Link Atom [Query]. */
        M_LIN,

        /** Atom List [Query]. */
        M_ALS,

        /** Attachment Point [Rgroup]. */
        M_APO,

        /** Atom Attachment Order [Rgroup]. */
        M_AAL,

        /** Rgroup Label Location [Rgroup]. */
        M_RGP,

        /** Rgroup Logic, Unsatisfied Sites, Range of Occurrence [Rgroup]. */
        M_LOG,

        /** Sgroup Type [Sgroup]. */
        M_STY,

        /** Sgroup Subtype [Sgroup]. */
        M_SST,

        /** Sgroup Labels [Sgroup]. */
        M_SLB,

        /** Sgroup Connectivity [Sgroup]. */
        M_SCN,

        /** Sgroup Expansion [Sgroup]. */
        M_SDS,

        /** Sgroup Atom List [Sgroup]. */
        M_SAL,

        /** Sgroup Bond List [Sgroup]. */
        M_SBL,

        /** Multiple Group Parent Atom List [Sgroup]. */
        M_SPA,

        /** Sgroup Subscript [Sgroup]. */
        M_SMT,

        /** Sgroup Correspondence [Sgroup]. */
        M_CRS,

        /** Sgroup Display Information [Sgroup]. */
        M_SDI,

        /** Superatom Bond and Vector Information [Sgroup]. */
        M_SBV,

        /** Data Sgroup Field Description [Sgroup]. */
        M_SDT,

        /** Data Sgroup Display Information [Sgroup]. */
        M_SDD,

        /** Data Sgroup Data. */
        M_SCD,

        /** Data Sgroup Data. */
        M_SED,

        /** Sgroup Hierarchy Information. */
        M_SPL,

        /** Sgroup Component Numbers. */
        M_SNC,

        /** Sgroup Bracket Style. */
        M_SBT,

        /** 3D Feature Properties. */
        M_$3D,

        /** ACDLabs Atom Label */
        M_ZZC,
        
        /** End of Block. */
        M_END,

        /** Non-property header. */
        UNKNOWN;

        /** Index of 'M XXX' properties for quick lookup. */
        private static final Map<String, PropertyKey> mSuffix = new HashMap<String, PropertyKey>(60);

        static {
            for (PropertyKey p : values()) {
                if (p.name().charAt(0) == 'M') mSuffix.put(p.name().substring(2, 5), p);
            }
        }

        /**
         * Determine the property key of the provided line.
         *
         * @param line an property line
         * @return the key (defaults to {@link #UNKNOWN})
         */
        static PropertyKey of(final String line) {
            if (line.length() < 5) return UNKNOWN;
            switch (line.charAt(0)) {
                case 'A':
                    if (line.charAt(1) == ' ' && line.charAt(2) == ' ') return ATOM_ALIAS;
                    return UNKNOWN;
                case 'G':
                    if (line.charAt(1) == ' ' && line.charAt(2) == ' ') return GROUP_ABBREVIATION;
                    return UNKNOWN;
                case 'S':
                    if (line.charAt(1) == ' ' && line.charAt(2) == ' ') return SKIP;
                    return UNKNOWN;
                case 'V':
                    if (line.charAt(1) == ' ' && line.charAt(2) == ' ') return ATOM_VALUE;
                    return UNKNOWN;
                case 'M':
                    if (line.charAt(1) != ' ' || line.charAt(2) != ' ') return UNKNOWN;
                    PropertyKey property = mSuffix.get(line.substring(3, 6));
                    if (property != null) return property;
                    return UNKNOWN;
            }
            return UNKNOWN;
        }

    }

    /**
     * Defines the version of the CTab.
     */
    enum CTabVersion {
        V2000, V3000, UNSPECIFIED;

        /**
         * Given a CTab header, what version was specified. The version
         * is identifier in the by the presence of 'V[2|3]000'. If not
         * version tag is present the version is unspecified.
         *
         * <pre>  5  5  0  0  0  0            999 V2000</prev>
         * <pre>  0  0  0  0  0  0            999 V3000</prev>
         *
         * @param header input line (non-null)
         * @return the CTab version
         */
        static CTabVersion ofHeader(String header) {
            if (header.length() < 39) return UNSPECIFIED;
            char c = header.charAt(34);
            if (c != 'v' && c != 'V') return UNSPECIFIED;
            if (header.charAt(35) == '2') // could check for '000'
                return V2000;
            if (header.charAt(35) == '3') // could check for '000'
                return V3000;
            return UNSPECIFIED;
        }
    }

}
