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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.MDLV3000Format;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.sgroup.SgroupType;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.BondManipulator;

/**
 * Class that implements the MDL mol V3000 format. This reader reads the
 * element symbol and 2D or 3D coordinates from the ATOM block.
 *
 * @cdk.module io
 * @cdk.githash
 * @cdk.iooptions
 *
 * @author Egon Willighagen &lt;egonw@users.sf.net&gt;
 * @cdk.created 2006
 *
 * @cdk.keyword MDL molfile V3000
 * @cdk.require java1.4+
 */
public class MDLV3000Reader extends DefaultChemObjectReader {

    BufferedReader              input  = null;
    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(MDLV3000Reader.class);

    private Pattern             keyValueTuple;
    private Pattern             keyValueTuple2;

    private int                 lineNumber;

    public MDLV3000Reader(Reader in) {
        this(in, Mode.RELAXED);
    }

    public MDLV3000Reader(Reader in, Mode mode) {
        input = new BufferedReader(in);
        initIOSettings();
        super.mode = mode;
        /* compile patterns */
        keyValueTuple = Pattern.compile("\\s*(\\w+)=([^\\s]*)(.*)"); // e.g. CHG=-1
        keyValueTuple2 = Pattern.compile("\\s*(\\w+)=\\(([^\\)]*)\\)(.*)"); // e.g. ATOMS=(1 31)
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
        for (int i = 0; i < interfaces.length; i++) {
            if (IAtomContainer.class.equals(interfaces[i])) return true;
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

    public IAtomContainer readMolecule(IChemObjectBuilder builder) throws CDKException {
        return readConnectionTable(builder);
    }

    public IAtomContainer readConnectionTable(IChemObjectBuilder builder) throws CDKException {


        logger.info("Reading CTAB block");
        IAtomContainer readData = builder.newAtomContainer();
        boolean foundEND = false;
        String lastLine = readHeader(readData);
        while (isReady() && !foundEND) {
            String command = readCommand(lastLine);
            logger.debug("command found: " + command);
            if ("END CTAB".equals(command)) {
                foundEND = true;
            } else if ("BEGIN CTAB".equals(command)) {
                // that's fine
            } else if (command.startsWith("COUNTS ")) {
                // don't think I need to parse this
            } else if ("BEGIN ATOM".equals(command)) {
                readAtomBlock(readData);
            } else if ("BEGIN BOND".equals(command)) {
                readBondBlock(readData);
            } else if ("BEGIN SGROUP".equals(command)) {
                readSGroup(readData);
            } else {
                logger.warn("Unrecognized command: " + command);
            }
            lastLine = readLine();
        }

        for (IAtom atom : readData.atoms()) {
            // XXX: slow method is slow
            int valence = 0;
            for (IBond bond : readData.getConnectedBondsList(atom)) {
                if (bond instanceof IQueryBond || bond.getOrder() == IBond.Order.UNSET) {
                    valence = -1;
                    break;
                }
                else {
                    valence += bond.getOrder().numeric();
                }
            }
            if (valence < 0) {
                logger.warn("Cannot set valence for atom with query bonds"); // also counts aromatic bond as query
            } else {
                final int unpaired = readData.getConnectedSingleElectronsCount(atom);
                applyMDLValenceModel(atom, valence + unpaired, unpaired);
            }
        }

        return readData;
    }

    /**
     * @throws CDKException when no file content is detected
     * @return Last line read
     */
    public String readHeader(IAtomContainer readData) throws CDKException {
        // read four lines
        String line1 = readLine();
        if (line1 == null) {
            throw new CDKException("Expected a header line, but found nothing.");
        }
        if (line1.length() > 0) {
            if (line1.startsWith("M  V30")) {
                // no header
                return line1;
            }
            readData.setTitle(line1);
        }
        readLine();
        String line3 = readLine();
        if (line3.length() > 0) readData.setProperty(CDKConstants.COMMENT, line3);
        String line4 = readLine();
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
    public void readAtomBlock(IAtomContainer readData) throws CDKException {
        logger.info("Reading ATOM block");
        IsotopeFactory isotopeFactory;
        try {
            isotopeFactory = Isotopes.getInstance();
        } catch (IOException exception) {
            throw new CDKException("Could not initiate the IsotopeFactory.", exception);
        }

        int RGroupCounter = 1;
        int Rnumber = 0;
        String[] rGroup = null;

        boolean foundEND = false;
        while (isReady() && !foundEND) {
            String command = readCommand(readLine());
            if ("END ATOM".equals(command)) {
                // FIXME: should check whether 3D is really 2D
                foundEND = true;
            } else {
                logger.debug("Parsing atom from: " + command);
                IAtom atom = readData.getBuilder().newAtom();
                StringTokenizer tokenizer = new StringTokenizer(command);
                // parse the index
                try {
                    atom.setID(tokenizer.nextToken());
                } catch (Exception exception) {
                    String error = "Error while parsing atom index";
                    logger.error(error);
                    logger.debug(exception);
                    throw new CDKException(error, exception);
                }
                // parse the element
                String element = tokenizer.nextToken();
                if (isotopeFactory.isElement(element)) {
                    atom.setSymbol(element);
                    isotopeFactory.configure(atom); // ?
                } else if ("A".equals(element)) {
                    atom = readData.getBuilder().newInstance(IPseudoAtom.class, element);
                } else if ("Q".equals(element)) {
                    atom = readData.getBuilder().newInstance(IPseudoAtom.class, element);
                } else if ("*".equals(element)) {
                    atom = readData.getBuilder().newInstance(IPseudoAtom.class, element);
                } else if ("LP".equals(element)) {
                    atom = readData.getBuilder().newInstance(IPseudoAtom.class, element);
                } else if ("L".equals(element)) {
                    atom = readData.getBuilder().newInstance(IPseudoAtom.class, element);
                } else if (element.length() > 0 && element.charAt(0) == 'R') {
                    logger.debug("Atom ", element, " is not an regular element. Creating a PseudoAtom.");
                    //check if the element is R
                    rGroup = element.split("^R");
                    if (rGroup.length > 1) {
                        try {
                            Rnumber = Integer.valueOf(rGroup[(rGroup.length - 1)]).intValue();
                            RGroupCounter = Rnumber;
                        } catch (Exception ex) {
                            Rnumber = RGroupCounter;
                            RGroupCounter++;
                        }
                        element = "R" + Rnumber;
                    }
                    atom = readData.getBuilder().newInstance(IPseudoAtom.class, element);
                } else {
                    if (mode == ISimpleChemObjectReader.Mode.STRICT) {
                        throw new CDKException(
                                "Invalid element type. Must be an existing element, or one in: A, Q, L, LP, *.");
                    }
                    atom = readData.getBuilder().newInstance(IPseudoAtom.class, element);
                    atom.setSymbol(element);
                }

                // parse atom coordinates (in Angstrom)
                try {
                    String xString = tokenizer.nextToken();
                    String yString = tokenizer.nextToken();
                    String zString = tokenizer.nextToken();
                    double x = Double.parseDouble(xString);
                    double y = Double.parseDouble(yString);
                    double z = Double.parseDouble(zString);
                    atom.setPoint3d(new Point3d(x, y, z));
                    atom.setPoint2d(new Point2d(x, y)); // FIXME: dirty!
                } catch (Exception exception) {
                    String error = "Error while parsing atom coordinates";
                    logger.error(error);
                    logger.debug(exception);
                    throw new CDKException(error, exception);
                }
                // atom-atom mapping
                String mapping = tokenizer.nextToken();
                if (!mapping.equals("0")) {
                    logger.warn("Skipping atom-atom mapping: " + mapping);
                } // else: default 0 is no mapping defined

                // the rest are key value things
                if (command.indexOf('=') != -1) {
                    Map<String, String> options = parseOptions(exhaustStringTokenizer(tokenizer));
                    Iterator<String> keys = options.keySet().iterator();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        String value = options.get(key);
                        try {
                            switch (key) {
                                case "CHG":
                                    int charge = Integer.parseInt(value);
                                    if (charge != 0) { // zero is no charge specified
                                        atom.setFormalCharge(charge);
                                    }
                                    break;
                                case "RAD":
                                    int numElectons = MDLV2000Writer.SPIN_MULTIPLICITY.ofValue(Integer.parseInt(value))
                                                                                      .getSingleElectrons();
                                    while (numElectons-- > 0) {
                                        readData.addSingleElectron(readData.getBuilder().newInstance(ISingleElectron.class, atom));
                                    }
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
                            String error = "Error while parsing key/value " + key + "=" + value + ": "
                                    + exception.getMessage();
                            logger.error(error);
                            logger.debug(exception);
                            throw new CDKException(error, exception);
                        }
                    }
                }

                // store atom
                readData.addAtom(atom);
                logger.debug("Added atom: " + atom);
            }
        }
    }

    /**
     * Reads the bond atoms, order and stereo configuration.
     */
    public void readBondBlock(IAtomContainer readData) throws CDKException {
        logger.info("Reading BOND block");
        boolean foundEND = false;
        while (isReady() && !foundEND) {
            String command = readCommand(readLine());
            if ("END BOND".equals(command)) {
                foundEND = true;
            } else {
                logger.debug("Parsing bond from: " + command);
                StringTokenizer tokenizer = new StringTokenizer(command);
                IBond bond = readData.getBuilder().newBond();
                // parse the index
                try {
                    String indexString = tokenizer.nextToken();
                    bond.setID(indexString);
                } catch (Exception exception) {
                    String error = "Error while parsing bond index";
                    logger.error(error);
                    logger.debug(exception);
                    throw new CDKException(error, exception);
                }
                // parse the order
                try {
                    String orderString = tokenizer.nextToken();
                    int order = Integer.parseInt(orderString);
                    if (order >= 4) {
                        logger.warn("Query order types are not supported (yet). File a bug if you need it");
                    } else {
                        bond.setOrder(BondManipulator.createBondOrder((double) order));
                    }
                } catch (Exception exception) {
                    String error = "Error while parsing bond index";
                    logger.error(error);
                    logger.debug(exception);
                    throw new CDKException(error, exception);
                }
                // parse index atom 1
                try {
                    String indexAtom1String = tokenizer.nextToken();
                    int indexAtom1 = Integer.parseInt(indexAtom1String);
                    IAtom atom1 = readData.getAtom(indexAtom1 - 1);
                    bond.setAtom(atom1, 0);
                } catch (Exception exception) {
                    String error = "Error while parsing index atom 1 in bond";
                    logger.error(error);
                    logger.debug(exception);
                    throw new CDKException(error, exception);
                }
                // parse index atom 2
                try {
                    String indexAtom2String = tokenizer.nextToken();
                    int indexAtom2 = Integer.parseInt(indexAtom2String);
                    IAtom atom2 = readData.getAtom(indexAtom2 - 1);
                    bond.setAtom(atom2, 1);
                } catch (Exception exception) {
                    String error = "Error while parsing index atom 2 in bond";
                    logger.error(error);
                    logger.debug(exception);
                    throw new CDKException(error, exception);
                }

                List<IAtom> endpts = new ArrayList<>();
                String attach = null;

                // the rest are key=value fields
                if (command.indexOf('=') != -1) {
                    Map<String, String> options = parseOptions(exhaustStringTokenizer(tokenizer));
                    for (String key : options.keySet()) {
                        String value = options.get(key);
                        try {
                            switch (key) {
                                case "CFG":
                                    int configuration = Integer.parseInt(value);
                                    if (configuration == 0) {
                                        bond.setStereo(IBond.Stereo.NONE);
                                    } else if (configuration == 1) {
                                        bond.setStereo(IBond.Stereo.UP);
                                    } else if (configuration == 2) {
                                        bond.setStereo(IBond.Stereo.UP_OR_DOWN);
                                    } else if (configuration == 3) {
                                        bond.setStereo(IBond.Stereo.DOWN);
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
                    }
                }

                // storing bond
                readData.addBond(bond);

                // storing positional variation
                if ("ANY".equals(attach)) {
                    Sgroup sgroup = new Sgroup();
                    sgroup.setType(SgroupType.ExtMulticenter);
                    sgroup.addAtom(bond.getBegin()); // could be other end?
                    sgroup.addBond(bond);
                    for (IAtom endpt : endpts)
                        sgroup.addAtom(endpt);

                    List<Sgroup> sgroups = readData.getProperty(CDKConstants.CTAB_SGROUPS);
                    if (sgroups == null)
                        readData.setProperty(CDKConstants.CTAB_SGROUPS, sgroups = new ArrayList<>(4));
                    sgroups.add(sgroup);
                }

                logger.debug("Added bond: " + bond);
            }
        }
    }

    /**
     * Reads labels.
     */
    public void readSGroup(IAtomContainer readData) throws CDKException {
        boolean foundEND = false;
        while (isReady() && !foundEND) {
            String command = readCommand(readLine());
            if ("END SGROUP".equals(command)) {
                foundEND = true;
            } else {
                logger.debug("Parsing Sgroup line: " + command);
                StringTokenizer tokenizer = new StringTokenizer(command);
                // parse the index
                String indexString = tokenizer.nextToken();
                logger.warn("Skipping external index: " + indexString);
                // parse command type
                String type = tokenizer.nextToken();
                // parse the external index
                String externalIndexString = tokenizer.nextToken();
                logger.warn("Skipping external index: " + externalIndexString);

                // the rest are key=value fields
                Map<String, String> options = new Hashtable<String, String>();
                if (command.indexOf('=') != -1) {
                    options = parseOptions(exhaustStringTokenizer(tokenizer));
                }

                // now interpret line
                if (type.startsWith("SUP")) {
                    Iterator<String> keys = options.keySet().iterator();
                    int atomID = -1;
                    String label = "";
                    while (keys.hasNext()) {
                        String key = keys.next();
                        String value = options.get(key);
                        try {
                            if (key.equals("ATOMS")) {
                                StringTokenizer atomsTokenizer = new StringTokenizer(value);
                                Integer.parseInt(atomsTokenizer.nextToken()); // should be 1, int atomCount =
                                atomID = Integer.parseInt(atomsTokenizer.nextToken());
                            } else if (key.equals("LABEL")) {
                                label = value;
                            } else {
                                logger.warn("Not parsing key: " + key);
                            }
                        } catch (Exception exception) {
                            String error = "Error while parsing key/value " + key + "=" + value + ": "
                                    + exception.getMessage();
                            logger.error(error);
                            logger.debug(exception);
                            throw new CDKException(error, exception);
                        }
                        if (atomID != -1 && label.length() > 0) {
                            IAtom original = readData.getAtom(atomID - 1);
                            IAtom replacement = original;
                            if (!(original instanceof IPseudoAtom)) {
                                replacement = readData.getBuilder().newInstance(IPseudoAtom.class,
                                                                                original);
                            }
                            ((IPseudoAtom) replacement).setLabel(label);
                            if (!replacement.equals(original))
                                AtomContainerManipulator.replaceAtomByAtom(readData, original, replacement);
                        }
                    }
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

    private Map<String, String> parseOptions(String string) throws CDKException {
        Map<String, String> keyValueTuples = new Hashtable<String, String>();
        while (string.length() >= 3) {
            logger.debug("Matching remaining option string: " + string);
            Matcher tuple1Matcher = keyValueTuple2.matcher(string);
            if (tuple1Matcher.matches()) {
                String key = tuple1Matcher.group(1);
                String value = tuple1Matcher.group(2);
                string = tuple1Matcher.group(3);
                logger.debug("Found key: " + key);
                logger.debug("Found value: " + value);
                keyValueTuples.put(key, value);
            } else {
                Matcher tuple2Matcher = keyValueTuple.matcher(string);
                if (tuple2Matcher.matches()) {
                    String key = tuple2Matcher.group(1);
                    String value = tuple2Matcher.group(2);
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

    public String exhaustStringTokenizer(StringTokenizer tokenizer) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(' ');
        while (tokenizer.hasMoreTokens()) {
            buffer.append(tokenizer.nextToken());
            buffer.append(' ');
        }
        return buffer.toString();
    }

    public String readLine() throws CDKException {
        String line = null;
        try {
            line = input.readLine();
            lineNumber++;
            logger.debug("read line " + lineNumber + ":", line);
        } catch (Exception exception) {
            String error = "Unexpected error while reading file: " + exception.getMessage();
            logger.error(error);
            logger.debug(exception);
            throw new CDKException(error, exception);
        }
        return line;
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

    private void initIOSettings() {}

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

}
