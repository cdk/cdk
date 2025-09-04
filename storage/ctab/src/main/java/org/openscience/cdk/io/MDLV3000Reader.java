/* Copyright (C) 2006-2008  Egon Willighagen <egonw@sci.kun.nl>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.MDLV3000Format;
import org.openscience.cdk.io.setting.BooleanIOSetting;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.isomorphism.matchers.*;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.sgroup.SgroupType;
import org.openscience.cdk.stereo.StereoElementFactory;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.BondManipulator;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that implements the MDL mol V3000 format. This reader reads the
 * element symbol and 2D or 3D coordinates from the ATOM block.
 * <br><br>
 * This reader is capable of reading <b>query bonds</b>, i.e., bond types
 * <ul>
 * <li>4 aromatic</li>
 * <li>5 single or double</li>
 * <li>6 single or aromatic</li>
 * <li>7 double or aromatic</li>
 * <li>8 any</li>
 * </ul>
 * <b>Limitations:</b>
 * <br>
 * Atoms: The only properties read in the atom block are atom index, atom type,
 * atom coordinates, atom-atom mapping, atom charge ({@code CHG}), atom radical
 * ({@code RAD}}, stereo configuration ({@code CFG}), atomic weight ({@code MASS}),
 * and valence ({@code VAL}).
 * <br>
 * Bonds: The reader throws a <code>CDKException</code> if bond types
 * 9 (coordination) or 10 (hydrogen) are encountered.
 *
 * @author Egon Willighagen &lt;egonw@users.sf.net&gt;
 * @cdk.iooptions
 * @cdk.created 2006
 * @cdk.keyword MDL molfile V3000
 * @cdk.require java1.4+
 */
public class MDLV3000Reader extends DefaultChemObjectReader {

    private static final ILoggingTool logger = LoggingToolFactory.createLoggingTool(MDLV3000Reader.class);
    // e.g. CHG=-1
    private static final Pattern keyValueTuple = Pattern.compile("\\s*(\\w+)=([^\\s]*)(.*)");
    // e.g. ATOMS=(1 31)
    private static final Pattern keyValueTuple2 = Pattern.compile("\\s*(\\w+)=\\(([^\\)]*)\\)(.*)");
    public static final String M_END = "M  END";

    private BooleanIOSetting optForce3d;
    private BooleanIOSetting optHydIso;
    private BooleanIOSetting optStereoPerc;
    private BooleanIOSetting optStereo0d;
    private BufferedReader input;
    private int lineNumber;

    public MDLV3000Reader(Reader in) {
        this(in, Mode.RELAXED);
    }

    public MDLV3000Reader(Reader in, Mode mode) {
        input = new BufferedReader(in);
        initIOSettings();
        super.mode = mode;
        lineNumber = 0;
    }

    public MDLV3000Reader(InputStream input) {
        this(input, Mode.RELAXED);
    }

    public MDLV3000Reader(InputStream input, Mode mode) {
        this(new InputStreamReader(input), mode);
    }

    public MDLV3000Reader() {
        this(new StringReader(""));
    }

    @Override
    public IResourceFormat getFormat() {
        return MDLV3000Format.getInstance();
    }

    @Override
    public void setReader(Reader input) throws CDKException {
        if (input instanceof BufferedReader) {
            this.input = (BufferedReader) input;
        } else {
            this.input = new BufferedReader(input);
        }
        lineNumber = 0;
    }

    @Override
    public void setReader(InputStream input) throws CDKException {
        setReader(new InputStreamReader(input));
    }

    @Override
    public boolean accepts(Class<? extends IChemObject> classObject) {
        Class<?>[] interfaces = classObject.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            if (IAtomContainer.class.equals(anInterface)) return true;
        }
        if (IAtomContainer.class.equals(classObject)) return true;
        Class superClass = classObject.getSuperclass();
        if (superClass != null) return this.accepts(superClass);
        return false;
    }

    @Override
    public <T extends IChemObject> T read(T object) throws CDKException {
        if (object instanceof IAtomContainer) {
            return (T) readMolecule(object.getBuilder());
        }
        throw new CDKException("Only supports AtomContainer objects.");
    }

    /**
     * Represents the state of reading a molecule in the MDLV3000Reader class.
     * <p>
     * This class stores the molecule, its dimensions (0D, 2D, or 3D), whether it is chiral,
     * stereo flags, and maps for atom and bond IDs. It also provides methods to add atoms
     * and bonds to the respective ID maps, and to retrieve atoms and bonds by their IDs.
     * </p>
     */
    private static final class ReadState {
        IAtomContainer mol;
        // 0D (undef/no coordinates), 2D, 3D
        int dimensions = 0;
        boolean chiral = false;
        // true if the molecule has query features, false otherwise
        boolean isQuery = false;
        Map<Integer,Integer> stereoflags = null;
        final Map<IAtom,Integer> stereo0d = new HashMap<>();

        // atom/bond ids need not be sequential, we could use map but more
        // common is the ids will be sequential
        IAtom[] atomById = new IAtom[64];
        IBond[] bondById = new IBond[64];

        <T> T[] grow(T[] arr, int req)
        {
            int cap = arr.length;
            return Arrays.copyOf(arr, Math.max(cap + cap >> 1,
                                               req + 1));
        }

        void addAtom(int id, IAtom atom) {
            if (id >= atomById.length)
                atomById = grow(atomById, id);
            atomById[id] = atom;
        }

        void addBond(int id, IBond bond) {
            if (id >= bondById.length)
                bondById = grow(bondById, id);
            bondById[id] = bond;
        }

        public IAtom getAtom(int i) {
            return atomById[i];
        }

        public IBond getBond(int i) {
            return bondById[i];
        }
    }

    /**
     * Reads a molecule and returns the corresponding AtomContainer.
     *
     * @param builder the builder object used to create the AtomContainer
     * @return the AtomContainer representing the CTAB block
     * @throws CDKException if there is an error while reading the CTAB block
     */
    public IAtomContainer readMolecule(IChemObjectBuilder builder) throws CDKException {
        return readConnectionTable(builder);
    }

    /**
     * Reads a Connection Table (CTAB) block and returns the corresponding AtomContainer.
     *
     * @param builder the builder object used to create the AtomContainer
     * @return the AtomContainer representing the CTAB block
     * @throws CDKException if there is an error while reading the CTAB block
     */
    public IAtomContainer readConnectionTable(IChemObjectBuilder builder) throws CDKException {

        logger.info("Reading CTAB block");
        final ReadState state = new ReadState();
        IAtomContainer readData = builder.newAtomContainer();
        state.mol = readData;

        boolean foundEND = false;
        String lastLine = readHeader(state);
        while (isReady() && !foundEND) {
            String command = readCommand(lastLine);
            logger.debug("command found: " + command);
            if ("END CTAB".equals(command)) {
                foundEND = true;
            } else if ("BEGIN CTAB".equals(command)) {
                // that's fine
            } else if (command.startsWith("COUNTS ")) {
                // COUNTS <natom> <nbond> <nsgroup> <n3dquery> <chiral> [REGNO=<regno>]
                String[] counts = command.split(" ");
                state.chiral = counts.length >= 6 && counts[5].equals("1");
            } else if ("BEGIN ATOM".equals(command)) {
                readAtomBlock(state);
            } else if ("BEGIN BOND".equals(command)) {
                readBondBlock(state);
            } else if ("BEGIN SGROUP".equals(command)) {
                readSGroup(state);
            } else if ("BEGIN COLLECTION".equals(command)) {
                readCollection(state);
            } else {
                logger.warn("Unrecognized command: " + command);
            }
            lastLine = readLine();
        }

        // read in any SDF fields
        if (lastLine != null && lastLine.startsWith(M_END)) {
            try {
                MDLV2000Reader.readNonStructuralData(input, state.mol);
            } catch (IOException ex) {
                throw new CDKException("IO Error", ex);
            }
        }

        // carry out final processing steps
        readData = finalizeMol(state);

        return readData;
    }

    /**
     * Finalizes the molecule. This includes finalizing dimensions, query features, valence, and stereochemistry.
     *
     * @param state the ReadState object containing the molecule and other relevant information
     * @return the processed Molecule
     */
    private IAtomContainer finalizeMol(ReadState state) {
        finalizeDimensions(state);

        // finalize query features
        IAtomContainer readAtomContainer = state.mol;
        // migrate atom container to IQueryAtomContainer implementation if there are any objects with query features
        if (state.isQuery) {
            // shallow copy of the original atom container, i.e. same atoms and electron containers as original
            final IQueryAtomContainer queryAtomContainer = new QueryAtomContainer(readAtomContainer, readAtomContainer.getBuilder());
            readAtomContainer.stereoElements().forEach(queryAtomContainer::addStereoElement);
            queryAtomContainer.setTitle(readAtomContainer.getTitle());
            queryAtomContainer.setProperties(readAtomContainer.getProperties());
            readAtomContainer = queryAtomContainer;
        }

        // initialize with value from ReadState that only considers if atom container has any query features
        boolean isQueryOrAromaticBond = state.isQuery;

        for (IAtom atom : readAtomContainer.atoms()) {
            int valence = 0;
            for (IBond bond : readAtomContainer.getConnectedBondsList(atom)) {
                if (bond instanceof IQueryBond || bond.getOrder() == IBond.Order.UNSET) {
                    valence = -1;
                    break;
                } else {
                    valence += bond.getOrder().numeric();
                }
            }
            if (valence < 0) {
                // update variable so that it now considers both (1) presence of a query feature in atom container
                // and (2) presence of an aromatic bond
                isQueryOrAromaticBond = true;
                logger.warn("Cannot set valence for atom with query bonds (this includes aromatic bonds)");
            } else {
                final int unpaired = readAtomContainer.getConnectedSingleElectronsCount(atom);
                applyMDLValenceModel(atom, valence + unpaired, unpaired);
            }
        }

        // skip infering and setting stereo chemistry if atom container has query features or aromatic bond
        if (!isQueryOrAromaticBond)
            finalizeStereochemistry(state, readAtomContainer);

        return readAtomContainer;
    }

    /**
     * Finalizes the stereochemistry of the molecule based on the ReadState object.
     *
     * @param state the ReadState object containing the molecule and other relevant information
     * @param readData the IAtomContainer representing the molecule to finalize the stereochemistry
     */
    private void finalizeStereochemistry(ReadState state, IAtomContainer readData) {
        if (optStereoPerc.isSet()) {

            if (state.dimensions == 3) { // has 3D coordinates
                readData.setStereoElements(StereoElementFactory.using3DCoordinates(readData)
                                                               .createAll());
            } else if (state.dimensions == 2) { // has 2D coordinates (set as 2D coordinates)
                readData.setStereoElements(StereoElementFactory.using2DCoordinates(readData)
                                                               .createAll());
            } else if (state.dimensions == 0 && optStereo0d.isSet()) {
                // technically if a molecule is 2D/3D and has the CFG=1 or CFG=2
                // specified this gives us hints information but it's safer to
                // just use the coordinates or wedge bonds
                for (Map.Entry<IAtom, Integer> e : state.stereo0d.entrySet()) {
                    final IStereoElement<IAtom,IAtom> stereoElement
                            = MDLV2000Reader.createStereo0d(state.mol, e.getKey(), e.getValue());
                    if (stereoElement != null)
                        state.mol.addStereoElement(stereoElement);
                }
            }

            if (state.stereoflags != null && !state.stereoflags.isEmpty()) {

                // work out the next available group, if we have &1, &2, etc then we choose &3
                // this is only needed if
                int defaultRacGrp = 0;
                if (!state.chiral) {
                    int max = 0;
                    for (Integer val : state.stereoflags.values()) {
                        if ((val & IStereoElement.GRP_TYPE_MASK) == IStereoElement.GRP_RAC) {
                            int num = val >>> IStereoElement.GRP_NUM_SHIFT;
                            if (num > max)
                                max = num;
                        }
                    }
                    defaultRacGrp = IStereoElement.GRP_RAC | (((max + 1) << IStereoElement.GRP_NUM_SHIFT));
                }

                for (IStereoElement<?, ?> se : readData.stereoElements()) {
                    if (se.getConfigClass() != IStereoElement.TH)
                        continue;
                    IAtom focus = (IAtom) se.getFocus();
                    if (focus.getID() == null)
                        continue;
                    int idx = Integer.parseInt(focus.getID());
                    Integer grpinfo = state.stereoflags.get(idx);
                    if (grpinfo != null)
                        se.setGroupInfo(grpinfo);
                    else if (!state.chiral)
                        se.setGroupInfo(defaultRacGrp);
                }
            } else if (!state.chiral) {
                // chiral flag not set which means this molecule is this stereoisomer "and" the enantiomer, mark all
                // Tetrahedral stereo as AND1 (&1)
                for (IStereoElement<?, ?> se : readData.stereoElements()) {
                    if (se.getConfigClass() == IStereoElement.TH) {
                        se.setGroupInfo(IStereoElement.GRP_RAC1);
                    }
                }
            }
        }
    }


    /**
     * Finalizes the dimensions of the molecule based on the ReadState object.
     * <br>
     * The parser will read all coords as 3D, then given information in the header and the
     * x,y,z values of each atom we work out whether we are 0D, 2D (Point2D) or 3D (Point3D).
     *
     * @param state the ReadState object containing the molecule and other relevant information
     */
    private void finalizeDimensions(ReadState state) {
        if (state.dimensions == 3 || optForce3d.isSet())
            return;
        int dimensions = 0;
        for (IAtom atom : state.mol.atoms()) {
            Point3d p3d = atom.getPoint3d();
            if (p3d.z != 0d) {
                dimensions = 3; // 3D
                break;
            } else if (dimensions == 0 && p3d.x != 0 && p3d.y != 0) {
                dimensions = 2; // 2D (if not 3D)
            }
        }
        // check the global header
        if (dimensions == 0)
            dimensions = state.dimensions;
        state.dimensions = dimensions;

        if (dimensions == 0) {
            // remove all coords we set
            for (IAtom atom : state.mol.atoms())
                atom.setPoint3d(null);
        } else if (dimensions == 2) {
            // convert 3d to 2d
            for (IAtom atom : state.mol.atoms()) {
                Point3d p3d = atom.getPoint3d();
                atom.setPoint2d(new Point2d(p3d.x, p3d.y));
                atom.setPoint3d(null);
            }
        }
    }

    /**
     * Checks if a given character is an ASCII digit. Do NOT replace
     * with Character.isDigit() which check the entire Unicode table/code
     * spaces.
     *
     * @param ch the character to check
     * @return true if the character is a digit, false otherwise
     */
    private boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    /**
     * Parses and extracts stereo group information from a given string.
     *
     * @param flags a map to store the stereo group flags
     * @param str the string to parse for stereo group information
     * @param type the type of stereo element to associate with the flags
     * @throws CDKException if there is an error while parsing the stereo group
     */
    private void parseStereoGroup(Map<Integer,Integer> flags, String str, int type) throws CDKException {
        int i   = "MDLV30/STE???".length();
        final int len = str.length();
        int num = 0;
        char ch;

        while (i < len && isDigit(ch = str.charAt(i))) {
            num = 10 * num + (ch - '0');
            i++;
        }
        type |= num << IStereoElement.GRP_NUM_SHIFT;

        // skip space
        while (i < len && str.charAt(i) == ' ')
            i++;
        // start of atom list
        if (str.startsWith("ATOMS=(", i))
            i += "ATOMS=(".length();
        else {
            handleError("Error while parsing stereo group: Expected an atom collection.");
            return;
        }

        // skip the count since we're storing in map
        while (i < len && isDigit(str.charAt(i)))
            i++;
        while (i < len && str.charAt(i) == ' ')
            i++;

        // parse the atoms
        while (i < len) {
            int val = 0;
            while (i < len && isDigit(ch = str.charAt(i))) {
                val = 10 * val + (ch - '0');
                i++;
            }
            // val-1 since we store atom index instead of atom number
            if (val > 0)
                flags.put(val, type);
            while (i < len && str.charAt(i) == ' ')
                i++;
            if (i < len && str.charAt(i) == ')')
                break;
        }
    }

    /**
     * Read collection information: highlights (currently not supported) and abs, rac, rel stereo groups.
     *
     * @param state the read state
     */
    private void readCollection(ReadState state) throws CDKException {
        if (state.stereoflags == null)
            state.stereoflags = new HashMap<>();
        String line;
        while ((line = readLine()) != null) {
            final String command = readCommand(line);
            if (command.startsWith("END COLLECTION"))
                break;
            else if (command.startsWith("MDLV30/STERAC")) {
                parseStereoGroup(state.stereoflags, command, IStereoElement.GRP_RAC);
            } else if (command.startsWith("MDLV30/STEREL")) {
                parseStereoGroup(state.stereoflags, command, IStereoElement.GRP_REL);
            } else if (command.startsWith("MDLV30/STEABS")) {
                parseStereoGroup(state.stereoflags, command, IStereoElement.GRP_ABS);
            }
        }
    }

    /**
     * Parses the dimensions information from a string.
     * <pre>
     * '  CDK     09251712073D'
     *  0123456789012345678901
     *  </pre>
     *
     * @param info string containing the dimensions information
     * @return the parsed dimensions value: 2 for "2D", 3 for "3D", and 0 otherwise
     */
    private static int parseDimensions(String info) {
        if (info.startsWith("2D", 20))
            return 2;
        if (info.startsWith("3D", 20))
            return 3;
        return 0;
    }

    /**
     * @return last line read
     * @throws CDKException when no file content is detected
     */
    private String readHeader(ReadState state) throws CDKException {
        // read four lines
        final String line1 = readLine();
        if (line1 == null) {
            throw new CDKException("Expected a header line, but found nothing.");
        }
        if (!line1.isEmpty()) {
            if (line1.startsWith("M  V30")) {
                // no header
                return line1;
            }
            state.mol.setTitle(line1);
        }
        final String infoLine = readLine();
        state.dimensions = parseDimensions(infoLine);
        final String line3 = readLine();
        if (!line3.isEmpty())
            state.mol.setProperty(CDKConstants.COMMENT, line3);
        final String line4 = readLine();
        if (!line4.contains("3000")) {
            throw new CDKException("This file is not a MDL V3000 molfile.");
        }
        return readLine();
    }

    /**
     * Reads the atoms, coordinates and charges.
     *
     * <p>IMPORTANT: it does not support the atom list and its negation!
     */
    private void readAtomBlock(ReadState state) throws CDKException {
        final IAtomContainer readData = state.mol;
        logger.info("Reading ATOM block");

        int RGroupCounter = 1;
        int Rnumber;
        String id;
        String[] rGroup;

        boolean foundEND = false;
        while (isReady() && !foundEND) {
            final String command = readCommand(readLine());
            if ("END ATOM".equals(command)) {
                // FIXME: should check whether 3D is really 2D
                foundEND = true;
            } else {
                logger.debug("Parsing atom from: " + command);
                IAtom atom = readData.getBuilder().newAtom();
                final StringTokenizer tokenizer = new StringTokenizer(command);
                // parse the index
                try {
                    id = tokenizer.nextToken();
                } catch (Exception exception) {
                    String errorMessage = "Error while parsing atom index";
                    logger.error(errorMessage);
                    logger.debug(exception);
                    throw new CDKException(errorMessage, exception);
                }
                // parse the element
                String elementString = tokenizer.nextToken();
                final Elements element = Elements.ofString(elementString);
                if (element != Elements.Unknown) {
                    atom.setAtomicNumber(element.number());
                } else if ("D".equals(elementString) && optHydIso.isSet()) {
                    atom.setMassNumber(2);
                    atom.setAtomicNumber(IElement.H);
                } else if ("T".equals(elementString) && optHydIso.isSet()) {
                    atom.setMassNumber(3);
                    atom.setAtomicNumber(IElement.H);
                } else if ("A".equals(elementString)) {
                    atom = readData.getBuilder().newInstance(IPseudoAtom.class, elementString);
                } else if ("Q".equals(elementString)) {
                    atom = readData.getBuilder().newInstance(IPseudoAtom.class, elementString);
                } else if ("*".equals(elementString)) {
                    atom = readData.getBuilder().newInstance(IPseudoAtom.class, elementString);
                } else if ("LP".equals(elementString)) {
                    atom = readData.getBuilder().newInstance(IPseudoAtom.class, elementString);
                } else if ("L".equals(elementString)) {
                    atom = readData.getBuilder().newInstance(IPseudoAtom.class, elementString);
                } else if (!elementString.isEmpty() && elementString.charAt(0) == 'R') {
                    logger.debug("Atom ", elementString, " is not an regular element. Creating a PseudoAtom.");
                    //check if the element is R
                    rGroup = elementString.split("^R");
                    if (rGroup.length > 1) {
                        try {
                            Rnumber = Integer.parseInt(rGroup[(rGroup.length - 1)]);
                            RGroupCounter = Rnumber;
                        } catch (Exception ex) {
                            Rnumber = RGroupCounter;
                            RGroupCounter++;
                        }
                        elementString = "R" + Rnumber;
                    }
                    atom = readData.getBuilder().newInstance(IPseudoAtom.class, elementString);
                } else {
                    if (mode == ISimpleChemObjectReader.Mode.STRICT) {
                        throw new CDKException(
                                "Invalid element type. Must be an existing element, or one in: A, Q, L, LP, *.");
                    }
                    atom = readData.getBuilder().newInstance(IPseudoAtom.class, elementString);
                    atom.setSymbol(elementString);
                }

                // parse atom coordinates (in Angstrom)
                try {
                    final double x = Double.parseDouble(tokenizer.nextToken());
                    final double y = Double.parseDouble(tokenizer.nextToken());
                    final double z = Double.parseDouble(tokenizer.nextToken());
                    atom.setPoint3d(new Point3d(x, y, z));
                } catch (Exception exception) {
                    String errorMessage = "Error while parsing atom coordinates";
                    logger.error(errorMessage);
                    logger.debug(exception);
                    throw new CDKException(errorMessage, exception);
                }
                // atom-atom mapping
                final String mapping = tokenizer.nextToken();
                if (!mapping.equals("0")) {
                    atom.setMapIdx(Integer.parseInt(mapping));
                } // else: default 0 is no mapping defined

                // the rest are key value things
                if (command.indexOf('=') != -1) {
                    final Map<String, String> options = parseOptions(exhaustStringTokenizer(tokenizer));
                    for (String key : options.keySet()) {
                        final String value = options.get(key);
                        try {
                            switch (key) {
                                case "CFG":
                                    final int cfg = Integer.parseInt(value);
                                    if (cfg != 0) {
                                        atom.setStereoParity(cfg);
                                        state.stereo0d.put(atom, cfg);
                                    }
                                    break;
                                case "CHG":
                                    final int charge = Integer.parseInt(value);
                                    if (charge != 0) { // zero is no charge specified
                                        atom.setFormalCharge(charge);
                                    }
                                    break;
                                case "RAD":
                                    final MDLV2000Writer.SPIN_MULTIPLICITY spinMultiplicity = MDLV2000Writer.SPIN_MULTIPLICITY.ofValue(Integer.parseInt(value));
                                    int numElectons = spinMultiplicity.getSingleElectrons();
                                    atom.setProperty(CDKConstants.SPIN_MULTIPLICITY, spinMultiplicity);
                                    while (numElectons-- > 0) {
                                        readData.addSingleElectron(readData.getBuilder()
                                                                           .newInstance(ISingleElectron.class, atom));
                                    }
                                    break;
                                case "MASS":
                                    atom.setMassNumber(Integer.parseInt(value));
                                    break;
                                case "VAL":
                                    if (!(atom instanceof IPseudoAtom)) {
                                        try {
                                            int valence = Integer.parseInt(value);
                                            if (valence != 0) {
                                                //15 is defined as 0 in mol files
                                                if (valence == 15)
                                                    atom.setValency(0);
                                                else
                                                    atom.setValency(valence);
                                            }
                                        } catch (Exception exception) {
                                            handleError("Could not parse valence information field", lineNumber, 0, 0, exception);
                                        }
                                    } else {
                                        logger.error("Cannot set valence information for a non-element!");
                                    }
                                    break;
                                default:
                                    logger.warn("Not parsing key: " + key);
                                    break;
                            }
                        } catch (Exception exception) {
                            String errorMessage = "Error while parsing key/value " + key + "=" + value + ": "
                                    + exception.getMessage();
                            logger.error(errorMessage);
                            logger.debug(exception);
                            throw new CDKException(errorMessage, exception);
                        }
                    }
                }

                // store atom
                atom.setID(id);
                readData.addAtom(atom);
                state.addAtom(Integer.parseInt(id), readData.getAtom(readData.getAtomCount()-1));
                logger.debug("Added atom: " + atom);
            }
        }
    }

    /**
     * Reads the bond atoms, order and stereo configuration.
     */
    private void readBondBlock(ReadState state) throws CDKException {
        IAtomContainer readData = state.mol;
        logger.info("Reading BOND block");
        boolean foundEND = false;
        while (isReady() && !foundEND) {
            final String line = readLine();
            final String command = readCommand(line);
            if ("END BOND".equals(command)) {
                foundEND = true;
            } else {
                logger.debug("Parsing bond from: " + command);
                final StringTokenizer tokenizer = new StringTokenizer(command);
                IBond bond = readData.getBuilder().newBond();
                // parse the index
                try {
                    final String indexString = tokenizer.nextToken();
                    bond.setID(indexString);
                } catch (Exception exception) {
                    final String errorMessage = "Error while parsing bond index: "
                            + exception.getMessage() + ", line='" + line + "'";
                    logger.error(errorMessage);
                    logger.debug(exception);
                    throw new CDKException(errorMessage, exception);
                }
                // parse the order
                try {
                    final String bondTypeString = tokenizer.nextToken();
                    final int bondType = Integer.parseInt(bondTypeString);
                    Expr.Type queryBondExpressionType = null;
                    switch (bondType) {
                        case 1: // single
                        case 2: // double
                        case 3: // triple
                            bond.setOrder(BondManipulator.createBondOrder(bondType));
                            break;
                        case 4: // aromatic
                            bond.setOrder(IBond.Order.UNSET);
                            bond.setFlag(IChemObject.AROMATIC, true);
                            bond.setFlag(IChemObject.SINGLE_OR_DOUBLE, true);
                            break;
                        case 5: // single or double
                            queryBondExpressionType = Expr.Type.SINGLE_OR_DOUBLE;
                            break;
                        case 6: // single or aromatic
                            queryBondExpressionType = Expr.Type.SINGLE_OR_AROMATIC;
                            break;
                        case 7: // double or aromatic
                            queryBondExpressionType = Expr.Type.DOUBLE_OR_AROMATIC;
                            break;
                        case 8: // any
                            queryBondExpressionType = Expr.Type.TRUE;
                            break;
                        case 9:
                        case 10:
                            throw new CDKException("Unsupported bond type: " + bondType);
                        default:
                            throw new CDKException("Invalid bond type: " + bondType);
                    }
                    // set up the QueryBond object if this is required given the bond type
                    if (queryBondExpressionType != null) {
                        final IQueryBond queryBond = new QueryBond(queryBondExpressionType, readData.getBuilder());
                        queryBond.setID(bond.getID());
                        bond = queryBond;
                    }
                } catch (Exception exception) {
                    final String errorMessage = "Error while parsing bond type: "
                            + exception.getMessage() + ", line='" + line + "'";
                    logger.error(errorMessage);
                    logger.debug(exception);
                    throw new CDKException(errorMessage, exception);
                }
                // parse index atom 1
                try {
                    final String indexAtom1String = tokenizer.nextToken();
                    final int indexAtom1 = Integer.parseInt(indexAtom1String);
                    final IAtom atom1 = state.getAtom(indexAtom1);
                    bond.setAtom(atom1, 0);
                } catch (Exception exception) {
                    final String errorMessage = "Error while parsing index atom 1 in bond"
                            + exception.getMessage() + ", line='" + line + "'";
                    logger.error(errorMessage);
                    logger.debug(exception);
                    throw new CDKException(errorMessage, exception);
                }
                // parse index atom 2
                try {
                    final String indexAtom2String = tokenizer.nextToken();
                    final int indexAtom2 = Integer.parseInt(indexAtom2String);
                    final IAtom atom2 = state.getAtom(indexAtom2);
                    bond.setAtom(atom2, 1);
                } catch (Exception exception) {
                    final String errorMessage = "Error while parsing index atom 2 in bond"
                            + exception.getMessage() + ", line='" + line + "'";
                    logger.error(errorMessage);
                    logger.debug(exception);
                    throw new CDKException(errorMessage, exception);
                }

                final List<IAtom> endpts = new ArrayList<>();
                String attach = null;
                // the rest are key=value fields
                if (command.indexOf('=') != -1) {
                    final Map<String, String> options = parseOptions(exhaustStringTokenizer(tokenizer));
                    for (String key : options.keySet()) {
                        String value = options.get(key);
                        try {
                            switch (key) {
                                case "CFG":
                                    int configuration = Integer.parseInt(value);
                                    if (configuration == 0) {
                                        bond.setDisplay(IBond.Display.Solid);
                                    } else if (configuration == 1) {
                                        bond.setDisplay(IBond.Display.WedgeBegin);
                                    } else if (configuration == 2) {
                                        if (bond.getOrder() == IBond.Order.DOUBLE)
                                            bond.setDisplay(IBond.Display.Crossed);
                                        else
                                            bond.setDisplay(IBond.Display.Wavy);
                                    } else if (configuration == 3) {
                                        bond.setDisplay(IBond.Display.WedgedHashBegin);
                                    }
                                    break;
                                case "ENDPTS":
                                    String[] endptStr = value.split(" ");
                                    // skip first value that is count
                                    for (int i = 1; i < endptStr.length; i++) {
                                        endpts.add(readData.getAtom(Integer.parseInt(endptStr[i]) - 1));
                                    }
                                    break;
                                case "ATTACH":
                                    attach = value;
                                    break;
                                // query property, bond topology
                                case "TOPO":
                                    // key is only valid if bond is a query bond
                                    if (bond instanceof IQueryBond) {
                                        final int topology = Integer.parseInt(value);
                                        final Expr bondTypeExpression = ((QueryBond) bond).getExpression();
                                        switch (topology) {
                                            // not specified, default value
                                            case 0:
                                                break;
                                            // ring
                                            case 1:
                                                final Expr expressionBondTypeAndInRing = bondTypeExpression.and(new Expr(Expr.Type.IS_IN_RING));
                                                ((QueryBond) bond).setExpression(expressionBondTypeAndInRing);
                                                break;
                                            // chain
                                            case 2:
                                                final Expr expressionBondTypeAndInChain = bondTypeExpression.and(new Expr(Expr.Type.IS_IN_CHAIN));
                                                ((QueryBond) bond).setExpression(expressionBondTypeAndInChain);
                                                break;
                                            default:
                                                handleError("Invalid value " + topology + " for key " + key, lineNumber, 0, 0);
                                                break;
                                        }
                                    } else {
                                        handleError("Key " + key + " is only defined for query bonds", lineNumber, 0, 0);
                                    }
                                    break;
                                default:
                                    logger.warn("Not parsing key: " + key);
                                    break;
                            }
                        } catch (Exception exception) {
                            final String errorMessage = "Error while parsing key/value " + key + "=" + value + ": "
                                    + exception.getMessage() + ", line='" + line + "'";
                            logger.error(errorMessage);
                            logger.debug(exception);
                            throw new CDKException(errorMessage, exception);
                        }
                    }
                }

                // update whether this is a molecule with query features
                state.isQuery = state.isQuery || bond instanceof IQueryBond;

                // alter this bond to a QueryBond
                if (state.isQuery && bond.getClass() != QueryBond.class) {
                    Expr expr;
                    if (bond.isAromatic()) {
                        expr = new Expr(Expr.Type.IS_AROMATIC);
                    } else {
                        expr = new Expr(Expr.Type.ORDER, bond.getOrder().numeric());
                    }
                    final IQueryBond queryBond = new QueryBond(bond.getBegin(), bond.getEnd(), expr);
                    queryBond.setID(bond.getID());
                    bond = queryBond;
                }

                // storing bond
                readData.addBond(bond);
                state.addBond(Integer.parseInt(bond.getID()),
                              readData.getBond(readData.getBondCount()-1));

                // storing positional variation
                if ("ANY".equals(attach)) {
                    final Sgroup sgroup = new Sgroup();
                    sgroup.setType(SgroupType.ExtMulticenter);
                    sgroup.addAtom(bond.getBegin()); // could be other end?
                    sgroup.addBond(bond);
                    for (IAtom endpt : endpts)
                        sgroup.addAtom(endpt);

                    List<Sgroup> sgroups = readData.getProperty(CDKConstants.CTAB_SGROUPS);
                    if (sgroups == null)
                        readData.setProperty(CDKConstants.CTAB_SGROUPS, sgroups = new ArrayList<>(4));
                    if (MDLV2000Reader.fixCrossingBonds(sgroup))
                        handleError("Fixed incorrect SBL list on SGroup " + sgroup.getSubscript());
                    sgroups.add(sgroup);
                }

                // set flags of atoms participating in bond to aromatic if bond has flag set to aromatic
                if (bond.isAromatic()) {
                    bond.getBegin().setFlag(IChemObject.AROMATIC, true);
                    bond.getEnd().setFlag(IChemObject.AROMATIC, true);
                }

                if (logger.isDebugEnabled())
                    logger.debug("Added " + (bond.getClass().getSimpleName().toLowerCase(Locale.ROOT).contains("query") ? "query" : "") + " bond: " + bond);
            }
        }
    }

    /**
     * Reads labels.
     */
    private void readSGroup(ReadState state) throws CDKException {
        IAtomContainer readData = state.mol;
        boolean foundEND = false;
        while (isReady() && !foundEND) {
            final String command = readCommand(readLine());
            if ("END SGROUP".equals(command)) {
                foundEND = true;
            } else {
                logger.debug("Parsing Sgroup line: " + command);
                final StringTokenizer tokenizer = new StringTokenizer(command);
                // parse the index
                final String indexString = tokenizer.nextToken();
                logger.warn("Skipping external index: " + indexString);
                // parse command type
                final String type = tokenizer.nextToken();
                // parse the external index
                final String externalIndexString = tokenizer.nextToken();
                logger.warn("Skipping external index: " + externalIndexString);

                // the rest are key=value fields
                Map<String, String> options = new HashMap<>();
                if (command.indexOf('=') != -1) {
                    options = parseOptions(exhaustStringTokenizer(tokenizer));
                }

                Sgroup sgroup = new Sgroup();
                // now interpret line
                if (type.startsWith("SUP")) {
                    sgroup.setType(SgroupType.CtabAbbreviation);
                    final Iterator<String> keys = options.keySet().iterator();
                    String label = "";
                    while (keys.hasNext()) {
                        final String key = keys.next();
                        final String value = options.get(key);
                        try {
                            switch (key) {
                                case "ATOMS": {
                                    final StringTokenizer atomsTokenizer = new StringTokenizer(value);
                                    int nExpected = Integer.parseInt(atomsTokenizer.nextToken());
                                    while (atomsTokenizer.hasMoreTokens()) {
                                        sgroup.addAtom(state.getAtom(Integer.parseInt(atomsTokenizer.nextToken())));
                                    }
                                    break;
                                }
                                case "XBONDS": {
                                    final StringTokenizer xbonds = new StringTokenizer(value);
                                    int nExpected = Integer.parseInt(xbonds.nextToken());
                                    while (xbonds.hasMoreTokens()) {
                                        sgroup.addBond(state.getBond(Integer.parseInt(xbonds.nextToken())));
                                    }
                                    break;
                                }
                                case "LABEL":
                                    label = value;
                                    break;
                                default:
                                    logger.warn("Not parsing key: " + key);
                                    break;
                            }
                        } catch (Exception exception) {
                            String error = "Error while parsing key/value " + key + "=" + value + ": "
                                    + exception.getMessage();
                            logger.error(error);
                            logger.debug(exception);
                            throw new CDKException(error, exception);
                        }
                        if (!sgroup.getAtoms().isEmpty() && !label.isEmpty()) {
                            sgroup.setSubscript(label);
                        }
                    }
                    List<Sgroup> sgroups = readData.getProperty(CDKConstants.CTAB_SGROUPS);
                    if (sgroups == null)
                        sgroups = new ArrayList<>();
                    sgroups.add(sgroup);
                    readData.setProperty(CDKConstants.CTAB_SGROUPS, sgroups);
                } else {
                    logger.warn("Skipping unrecognized SGROUP type: " + type);
                }
            }
        }
    }

    /**
     * Reads the command on this line. If the line is continued on the next, that
     * part is added.
     *
     * @return Returns the command on this line.
     */
    private String readCommand(String line) throws CDKException {
        if (line.startsWith("M  V30 ")) {
            String command = line.substring(7);
            if (command.endsWith("-")) {
                command = command.substring(0, command.length() - 1);
                command += readCommand(readLine());
            }
            return command;
        } else {
            throw new CDKException("Could not read MDL file: unexpected line: " + line);
        }
    }

    private Map<String, String> parseOptions(String string) {
        final Map<String, String> keyValueTuples = new HashMap<>();
        while (string.length() >= 3) {
            logger.debug("Matching remaining option string: " + string);
            final Matcher tuple1Matcher = keyValueTuple2.matcher(string);
            if (tuple1Matcher.matches()) {
                final String key = tuple1Matcher.group(1);
                final String value = tuple1Matcher.group(2);
                string = tuple1Matcher.group(3);
                logger.debug("Found key: " + key);
                logger.debug("Found value: " + value);
                keyValueTuples.put(key, value);
            } else {
                final Matcher tuple2Matcher = keyValueTuple.matcher(string);
                if (tuple2Matcher.matches()) {
                    final String key = tuple2Matcher.group(1);
                    final String value = tuple2Matcher.group(2);
                    string = tuple2Matcher.group(3);
                    logger.debug("Found key: " + key);
                    logger.debug("Found value: " + value);
                    keyValueTuples.put(key, value);
                } else {
                    logger.warn("Quiting; could not parse: " + string + ".");
                    string = "";
                }
            }
        }
        return keyValueTuples;
    }

    private String exhaustStringTokenizer(StringTokenizer tokenizer) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(' ');
        while (tokenizer.hasMoreTokens()) {
            stringBuilder.append(tokenizer.nextToken());
            stringBuilder.append(' ');
        }
        return stringBuilder.toString();
    }

    private String readLine() throws CDKException {
        try {
            final String line = input.readLine();
            lineNumber++;
            logger.debug("read line " + lineNumber + ":", line);
            return line;
        } catch (Exception exception) {
            String error = "Unexpected error while reading file: " + exception.getMessage();
            logger.error(error);
            logger.debug(exception);
            throw new CDKException(error, exception);
        }
    }

    public boolean isReady() throws CDKException {
        try {
            return input.ready();
        } catch (Exception exception) {
            String error = "Unexpected error while reading file: " + exception.getMessage();
            logger.error(error);
            logger.debug(exception);
            throw new CDKException(error, exception);
        }
    }

    @Override
    public void close() throws IOException {
        input.close();
    }

    private void initIOSettings() {
        optForce3d = addSetting(new BooleanIOSetting("ForceReadAs3DCoordinates", IOSetting.Importance.LOW,
                "Should coordinates always be read as 3D?", "false"));
        optHydIso = addSetting(new BooleanIOSetting("InterpretHydrogenIsotopes",
                IOSetting.Importance.LOW, "Should D and T be interpreted as hydrogen isotopes?", "true"));
        optStereoPerc = addSetting(new BooleanIOSetting("AddStereoElements", IOSetting.Importance.LOW,
                "Detect and create IStereoElements for the input.", "true"));
        optStereo0d = addSetting(new BooleanIOSetting("AddStereo0d", IOSetting.Importance.LOW,
                "Allow stereo created from parity value when no coordinates", "true"));

    }

    /**
     * Applies the MDL valence model to atoms using the explicit valence (bond
     * order sum) and charge to determine the correct number of implicit
     * hydrogens. The model is not applied if the explicit valence is less than
     * 0 - this is the case when a query bond was read for an atom.
     *
     * @param atom            the atom to apply the model to
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
            if (element == null)
                element = 0;

            Integer charge = atom.getFormalCharge();
            if (charge == null)
                charge = 0;

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

}
