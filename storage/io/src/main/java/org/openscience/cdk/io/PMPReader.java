/* Copyright (C) 2004-2008  Egon Willighagen <egonw@users.sf.net>
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
 *
 */
package org.openscience.cdk.io;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.rebond.RebondTool;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.ICrystal;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.PMPFormat;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads an frames from a PMP formated input.
 * Both compilation and use of this class requires Java 1.4.
 *
 * @cdk.module  io
 * @cdk.githash
 * @cdk.iooptions
 *
 * @cdk.keyword file format, Polymorph Predictor (tm)
 *
 * @author E.L. Willighagen
 * @cdk.require java1.4+
 */
public class PMPReader extends DefaultChemObjectReader {

    private static final String   PMP_ZORDER   = "ZOrder";
    private static final String   PMP_ID       = "Id";

    private BufferedReader        input;

    private static ILoggingTool   logger       = LoggingToolFactory.createLoggingTool(PMPReader.class);

    /* Keep a copy of the PMP model */
    private IAtomContainer        modelStructure;
    private IChemObject           chemObject;
    /* Keep an index of PMP id -> AtomCountainer id */
    private Map<Integer, Integer> atomids      = new Hashtable<>();
    private Map<Integer, Integer> atomGivenIds = new Hashtable<>();
    private Map<Integer, Integer> bondids      = new Hashtable<>();
    private Map<Integer, Integer> bondAtomOnes = new Hashtable<>();
    private Map<Integer, Integer> bondAtomTwos = new Hashtable<>();
    private Map<Integer, Double>  bondOrders   = new Hashtable<>();

    /* Often used patterns */
    Pattern                       objHeader;
    Pattern                       objCommand;
    Pattern                       atomTypePattern;

    int                           lineNumber;
    int                           bondCounter  = 0;
    private RebondTool            rebonder;

    /*
     * construct a new reader from a Reader type object
     * @param input reader from which input is read
     */
    public PMPReader(Reader input) {
        this.input = new BufferedReader(input);
        this.lineNumber = 0;

        /* compile patterns */
        objHeader = Pattern.compile(".*\\((\\d+)\\s(\\w+)$");
        objCommand = Pattern.compile(".*\\(A\\s([CFDIO])\\s(\\w+)\\s+\"?(.*?)\"?\\)$");
        atomTypePattern = Pattern.compile("^(\\d+)\\s+(\\w+)$");

        rebonder = new RebondTool(2.0, 0.5, 0.5);
    }

    public PMPReader(InputStream input) {
        this(new InputStreamReader(input));
    }

    public PMPReader() {
        this(new StringReader(""));
    }

    @Override
    public IResourceFormat getFormat() {
        return PMPFormat.getInstance();
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

    @Override
    @SuppressWarnings("unchecked")
    public boolean accepts(Class<? extends IChemObject> classObject) {
        if (IChemFile.class.equals(classObject)) return true;
        Class<?>[] interfaces = classObject.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            if (IChemFile.class.equals(anInterface)) return true;
        }
        Class superClass = classObject.getSuperclass();
        if (superClass != null) return this.accepts(superClass);
        return false;
    }

    /**
     * reads the content from a PMP input. It can only return a
     * IChemObject of type ChemFile
     *
     * @param object class must be of type ChemFile
     *
     * @see IChemFile
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends IChemObject> T read(T object) throws CDKException {
        if (object instanceof IChemFile) {
            return (T) readChemFile((IChemFile) object);
        } else {
            throw new CDKException("Only supported is reading of ChemFile objects.");
        }
    }

    // private procedures

    private String readLine() throws IOException {
        String line = input.readLine();
        lineNumber = lineNumber + 1;
        logger.debug("LINE (" + lineNumber + "): ", line);
        return line;
    }

    /**
     *  Private method that actually parses the input to read a ChemFile
     *  object.
     *
     *  Each PMP frame is stored as a Crystal in a ChemModel. The PMP
     *  file is stored as a ChemSequence of ChemModels.
     *
     * @return A ChemFile containing the data parsed from input.
     */
    private IChemFile readChemFile(IChemFile chemFile) {
        IChemSequence chemSequence;
        IChemModel chemModel;
        ICrystal crystal;

        try {
            String line = readLine();
            while (input.ready() && line != null) {
                if (line.startsWith("%%Header Start")) {
                    // parse Header section
                    while (input.ready() && line != null && !(line.startsWith("%%Header End"))) {
                        if (line.startsWith("%%Version Number")) {
                            String version = readLine().trim();
                            if (!version.equals("3.00")) {
                                logger.error("The PMPReader only supports PMP files with version 3.00");
                                return null;
                            }
                        }
                        line = readLine();
                    }
                } else if (line.startsWith("%%Model Start")) {
                    // parse Model section
                    modelStructure = chemFile.getBuilder().newInstance(IAtomContainer.class);
                    while (input.ready() && line != null && !(line.startsWith("%%Model End"))) {
                        Matcher objHeaderMatcher = objHeader.matcher(line);
                        if (objHeaderMatcher.matches()) {
                            String object = objHeaderMatcher.group(2);
                            constructObject(chemFile.getBuilder(), object);
                            int id = Integer.parseInt(objHeaderMatcher.group(1));
                            // logger.debug(object + " id: " + id);
                            line = readLine();
                            while (input.ready() && line != null && !(line.trim().equals(")"))) {
                                // parse object command (or new object header)
                                Matcher objCommandMatcher = objCommand.matcher(line);
                                objHeaderMatcher = objHeader.matcher(line);
                                if (objHeaderMatcher.matches()) {
                                    // ok, forget about nesting and hope for the best
                                    object = objHeaderMatcher.group(2);
                                    id = Integer.parseInt(objHeaderMatcher.group(1));
                                    constructObject(chemFile.getBuilder(), object);
                                } else if (objCommandMatcher.matches()) {
                                    String format = objCommandMatcher.group(1);
                                    String command = objCommandMatcher.group(2);
                                    String field = objCommandMatcher.group(3);

                                    processModelCommand(object, command, format, field);
                                } else {
                                    logger.warn("Skipping line: " + line);
                                }
                                line = readLine();
                            }
                            if (chemObject instanceof IAtom) {
                                atomids.put(id, modelStructure.getAtomCount());
                                atomGivenIds.put(Integer.valueOf((String) chemObject.getProperty(PMP_ID)),
                                                 id);
                                modelStructure.addAtom((IAtom) chemObject);
                            } else if (chemObject instanceof IBond) {
                                // ignored: bonds may be defined before their
                                // atoms so their handling is deferred until the
                                // end of the model
                            } else {
                                logger.error("chemObject is not initialized or of bad class type");
                            }
                            // logger.debug(molecule.toString());
                        }
                        line = readLine();
                    }
                    assert line != null;
                    if (line.startsWith("%%Model End")) {
                        // during the Model Start, all bonds are cached as PMP files might
                        // define bonds *before* the involved atoms :(
                        // the next lines dump the cache into the atom container

                        //                  	bondids.put(new Integer(id), new Integer(molecule.getAtomCount()));
                        //                  	molecule.addBond((IBond)chemObject);
                        int bondsFound = bondids.size();
                        logger.debug("Found #bonds: ", bondsFound);
                        logger.debug("#atom ones: ", bondAtomOnes.size());
                        logger.debug("#atom twos: ", bondAtomTwos.size());
                        logger.debug("#orders: ", bondOrders.size());
                        for (Integer index : bondids.keySet()) {
                            double order = (bondOrders.get(index) != null ? bondOrders.get(index) : 1.0);
                            logger.debug("index: ", index);
                            logger.debug("ones: ", bondAtomOnes.get(index));
                            IAtom atom1 = modelStructure.getAtom(atomids.get(bondAtomOnes.get(index)));
                            IAtom atom2 = modelStructure.getAtom(atomids.get(bondAtomTwos.get(index)));
                            IBond bond = modelStructure.getBuilder().newInstance(IBond.class, atom1, atom2);
                            if (order == 1.0) {
                                bond.setOrder(IBond.Order.SINGLE);
                            } else if (order == 2.0) {
                                bond.setOrder(IBond.Order.DOUBLE);
                            } else if (order == 3.0) {
                                bond.setOrder(IBond.Order.TRIPLE);
                            } else if (order == 4.0) {
                                bond.setOrder(IBond.Order.QUADRUPLE);
                            }
                            modelStructure.addBond(bond);
                        }
                    }
                } else if (line.startsWith("%%Traj Start")) {
                    chemSequence = chemFile.getBuilder().newInstance(IChemSequence.class);
                    double energyFragment = 0.0;
                    double energyTotal = 0.0;
                    int Z = 1;
                    while (input.ready() && line != null && !(line.startsWith("%%Traj End"))) {
                        if (line.startsWith("%%Start Frame")) {
                            chemModel = chemFile.getBuilder().newInstance(IChemModel.class);
                            crystal = chemFile.getBuilder().newInstance(ICrystal.class);
                            while (input.ready() && line != null && !(line.startsWith("%%End Frame"))) {
                                // process frame data
                                if (line.startsWith("%%Atom Coords")) {
                                    // calculate Z: as it is not explicitely given, try to derive it from the
                                    // energy per fragment and the total energy
                                    if (energyFragment != 0.0 && energyTotal != 0.0) {
                                        Z = (int) Math.round(energyTotal / energyFragment);
                                        logger.debug("Z derived from energies: ", Z);
                                    }
                                    // add atomC as atoms to crystal
                                    int expatoms = modelStructure.getAtomCount();
                                    for (int molCount = 1; molCount <= Z; molCount++) {
                                        IAtomContainer clone = modelStructure.getBuilder().newInstance(
                                                IAtomContainer.class);
                                        for (int i = 0; i < expatoms; i++) {
                                            line = readLine();
                                            IAtom a = clone.getBuilder().newInstance(IAtom.class);
                                            StringTokenizer st = new StringTokenizer(line, " ");
                                            a.setPoint3d(new Point3d(Double.parseDouble(st.nextToken()), Double
                                                    .parseDouble(st.nextToken()), Double.parseDouble(st.nextToken())));
                                            a.setCovalentRadius(0.6);
                                            IAtom modelAtom = modelStructure.getAtom(atomids.get(atomGivenIds
                                                    .get(i + 1)));
                                            a.setSymbol(modelAtom.getSymbol());
                                            clone.addAtom(a);
                                        }
                                        rebonder.rebond(clone);
                                        crystal.add(clone);
                                    }
                                } else if (line.startsWith("%%E/Frag")) {
                                    line = readLine().trim();
                                    energyFragment = Double.parseDouble(line);
                                } else if (line.startsWith("%%Tot E")) {
                                    line = readLine().trim();
                                    energyTotal = Double.parseDouble(line);
                                } else if (line.startsWith("%%Lat Vects")) {
                                    StringTokenizer st;
                                    line = readLine();
                                    st = new StringTokenizer(line, " ");
                                    crystal.setA(new Vector3d(Double.parseDouble(st.nextToken()), Double.parseDouble(st
                                            .nextToken()), Double.parseDouble(st.nextToken())));
                                    line = readLine();
                                    st = new StringTokenizer(line, " ");
                                    crystal.setB(new Vector3d(Double.parseDouble(st.nextToken()), Double.parseDouble(st
                                            .nextToken()), Double.parseDouble(st.nextToken())));
                                    line = readLine();
                                    st = new StringTokenizer(line, " ");
                                    crystal.setC(new Vector3d(Double.parseDouble(st.nextToken()), Double.parseDouble(st
                                            .nextToken()), Double.parseDouble(st.nextToken())));
                                } else if (line.startsWith("%%Space Group")) {
                                    line = readLine().trim();
                                    /*
                                     * standardize space group name. See
                                     * Crystal.setSpaceGroup()
                                     */
                                    if ("P 21 21 21 (1)".equals(line)) {
                                        crystal.setSpaceGroup("P 2_1 2_1 2_1");
                                    } else {
                                        crystal.setSpaceGroup("P1");
                                    }
                                }
                                line = readLine();
                            }
                            chemModel.setCrystal(crystal);
                            chemSequence.addChemModel(chemModel);
                        }
                        line = readLine();
                    }
                    chemFile.addChemSequence(chemSequence);
                }  // else disregard line

                // read next line
                line = readLine();
            }
        } catch (IOException e) {
            logger.error("An IOException happened: ", e.getMessage());
            logger.debug(e);
            chemFile = null;
        } catch (CDKException e) {
            logger.error("An CDKException happened: ", e.getMessage());
            logger.debug(e);
            chemFile = null;
        }

        return chemFile;
    }

    private void processModelCommand(String object, String command, String format, String field) {
        logger.debug(object + "->" + command + " (" + format + "): " + field);
        if ("Model".equals(object)) {
            logger.warn("Unkown PMP Model command: " + command);
        } else if ("Atom".equals(object)) {
            if ("ACL".equals(command)) {
                Matcher atomTypeMatcher = atomTypePattern.matcher(field);
                if (atomTypeMatcher.matches()) {
                    int atomicnum = Integer.parseInt(atomTypeMatcher.group(1));
                    String type = atomTypeMatcher.group(2);
                    ((IAtom) chemObject).setAtomicNumber(atomicnum);
                    ((IAtom) chemObject).setSymbol(type);
                } else {
                    logger.error("Incorrectly formated field value: " + field + ".");
                }
            } else if ("Charge".equals(command)) {
                try {
                    double charge = Double.parseDouble(field);
                    ((IAtom) chemObject).setCharge(charge);
                } catch (NumberFormatException e) {
                    logger.error("Incorrectly formated float field: " + field + ".");
                }
            } else if ("CMAPPINGS".equals(command)) {
            } else if ("FFType".equals(command)) {
            } else if ("Id".equals(command)) {
                // ok, should take this into account too
                chemObject.setProperty(PMP_ID, field);
            } else if ("Mass".equals(command)) {
            } else if ("XYZ".equals(command)) {
            } else if ("ZOrder".equals(command)) {
                // ok, should take this into account too
                chemObject.setProperty(PMP_ZORDER, field);
            } else {
                logger.warn("Unkown PMP Atom command: " + command);
            }
        } else if ("Bond".equals(object)) {
            if ("Atom1".equals(command)) {
                int atomid = Integer.parseInt(field);
                // this assumes that the atoms involved in this bond are
                // already added, which seems the case in the PMP files
                bondAtomOnes.put(bondCounter, atomid);
                //                IAtom a = molecule.getAtom(realatomid);
                //                ((IBond)chemObject).setAtomAt(a, 0);
            } else if ("Atom2".equals(command)) {
                int atomid = Integer.parseInt(field);
                // this assumes that the atoms involved in this bond are
                // already added, which seems the case in the PMP files
                logger.debug("atomids: " + atomids);
                logger.debug("atomid: " + atomid);
                bondAtomTwos.put(bondCounter, atomid);
                //                IAtom a = molecule.getAtom(realatomid);
                //                ((IBond)chemObject).setAtomAt(a, 1);
            } else if ("Order".equals(command)) {
                double order = Double.parseDouble(field);
                bondOrders.put(bondCounter, order);
                //                ((IBond)chemObject).setOrder(order);
            } else if ("Id".equals(command)) {
                int bondid = Integer.parseInt(field);
                bondids.put(bondCounter, bondid);
            } else if ("Label".equals(command)) {
            } else if ("3DGridOrigin".equals(command)) {
            } else if ("3DGridMatrix".equals(command)) {
            } else if ("3DGridDivision".equals(command)) {
            } else {
                logger.warn("Unkown PMP Bond command: " + command);
            }
        } else {
            logger.warn("Unkown PMP object: " + object);
        }
    }

    private void constructObject(IChemObjectBuilder builder, String object) {
        if ("Atom".equals(object)) {
            chemObject = builder.newInstance(IAtom.class, "C");
        } else if ("Bond".equals(object)) {
            bondCounter++;
            chemObject = builder.newInstance(IBond.class);
        } else if ("Model".equals(object)) {
            modelStructure = builder.newInstance(IAtomContainer.class);
        } else {
            logger.error("Cannot construct PMP object type: " + object);
        }
    }

    @Override
    public void close() throws IOException {
        input.close();
    }
}
