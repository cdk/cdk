/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2006  The Chemistry Development Kit (CDK) project
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.vecmath.Vector3d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.ICrystal;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.PMPFormat;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 * Reads an frames from a PMP formated input.
 * Both compilation and use of this class requires Java 1.4.
 *
 * @cdk.module io
 *
 * @cdk.keyword file format, Polymorph Predictor (tm)
 *
 * @author E.L. Willighagen
 * @cdk.require java1.4+
 */
public class PMPReader extends DefaultChemObjectReader {

    private BufferedReader input;

    private LoggingTool logger;

    /* Keep a copy of the PMP model */
    private IMoleculeSet som;
    private IChemModel modelModel;
    private IMolecule molecule;
    private IChemObject chemObject;
    /* Keep an index of PMP id -> AtomCountainer id */
    private Hashtable atomids = new Hashtable();
    private Hashtable bondids = new Hashtable();

    /* Often used patterns */
    Pattern objHeader;
    Pattern objCommand;
    Pattern atomTypePattern;

    int lineNumber = 0;
    
    /*
     * construct a new reader from a Reader type object
     *
     * @param input reader from which input is read
     */
    public PMPReader(Reader input) {
        this.input = new BufferedReader(input);
        logger = new LoggingTool(this);
        this.lineNumber = 0;
    
        /* compile patterns */
        objHeader = Pattern.compile(".*\\((\\d+)\\s(\\w+)$");
        objCommand = Pattern.compile(".*\\(A\\s(C|F|D|I|O)\\s(\\w+)\\s+\"?(.*?)\"?\\)$");
        atomTypePattern = Pattern.compile("^(\\d+)\\s+(\\w+)$");
    }

    public PMPReader(InputStream input) {
        this(new InputStreamReader(input));
    }
    
    public PMPReader() {
        this(new StringReader(""));
    }
    
    public IResourceFormat getFormat() {
        return new PMPFormat();
    }
    
    public void setReader(Reader input) throws CDKException {
        if (input instanceof BufferedReader) {
            this.input = (BufferedReader)input;
        } else {
            this.input = new BufferedReader(input);
        }
    }

    public void setReader(InputStream input) throws CDKException {
        setReader(new InputStreamReader(input));
    }

	public boolean accepts(Class classObject) {
		Class[] interfaces = classObject.getInterfaces();
		for (int i=0; i<interfaces.length; i++) {
			if (IChemFile.class.equals(interfaces[i])) return true;
		}
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
    public IChemObject read(IChemObject object) throws CDKException {
        if (object instanceof IChemFile) {
            return (IChemObject)readChemFile((IChemFile)object);
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
        IChemSequence chemSequence = chemFile.getBuilder().newChemSequence();
        IChemModel chemModel = chemFile.getBuilder().newChemModel();
        ICrystal crystal = chemFile.getBuilder().newCrystal();

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
                    while (input.ready() && line != null && !(line.startsWith("%%Model End"))) {
                        Matcher objHeaderMatcher = objHeader.matcher(line);
                        if (objHeaderMatcher.matches()) {
                            String object = objHeaderMatcher.group(2);
                            constructObject(chemFile.getBuilder(), object);
                            int id = Integer.parseInt(objHeaderMatcher.group(1));
                            // System.out.println(object + " id: " + id);
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
                                atomids.put(new Integer(id), new Integer(molecule.getAtomCount()));
                                molecule.addAtom((IAtom)chemObject);
                            } else if (chemObject instanceof IBond) {
                                bondids.put(new Integer(id), new Integer(molecule.getAtomCount()));
                                molecule.addBond((IBond)chemObject);
                            } else {
                                logger.error("chemObject is not initialized or of bad class type");
                            }
                            // System.out.println(molecule.toString());
                        }
                        line = readLine();
                    }
                    som.addMolecule(molecule);
                    modelModel.setSetOfMolecules(som);
                } else if (line.startsWith("%%Traj Start")) {
                    chemSequence = chemFile.getBuilder().newChemSequence();
                    while (input.ready() && line != null && !(line.startsWith("%%Traj End"))) {
                        if (line.startsWith("%%Start Frame")) {
                            chemModel = chemFile.getBuilder().newChemModel();
                            crystal = chemFile.getBuilder().newCrystal();
                            IAtomContainer atomC = ChemModelManipulator.getAllInOneContainer(modelModel);
                            while (input.ready() && line != null && !(line.startsWith("%%End Frame"))) {
                                // process frame data
                                if (line.startsWith("%%Atom Coords")) {
                                    // add atomC as atoms to crystal
                                    crystal.add((IAtomContainer)atomC.clone());
                                    int expatoms = atomC.getAtomCount();
                                    // exception
                                    for (int i=0; i < expatoms; i++) {
                                        line = readLine();
                                        IAtom a = crystal.getAtom(i);
                                        StringTokenizer st = new StringTokenizer(line, " ");
                                        a.setX3d(Double.parseDouble(st.nextToken()));
                                        a.setY3d(Double.parseDouble(st.nextToken()));
                                        a.setZ3d(Double.parseDouble(st.nextToken()));
                                    }
                                } else if (line.startsWith("%%Lat Vects")) {
                                    StringTokenizer st;
                                    line = readLine();
                                    st = new StringTokenizer(line, " ");
                                    crystal.setA(new Vector3d(
                                        Double.parseDouble(st.nextToken()),
                                        Double.parseDouble(st.nextToken()),
                                        Double.parseDouble(st.nextToken())
                                    ));
                                    line = readLine();
                                    st = new StringTokenizer(line, " ");
                                    crystal.setB(new Vector3d(
                                        Double.parseDouble(st.nextToken()),
                                        Double.parseDouble(st.nextToken()),
                                        Double.parseDouble(st.nextToken())
                                    ));
                                    line = readLine();
                                    st = new StringTokenizer(line, " ");
                                    crystal.setC(new Vector3d(
                                        Double.parseDouble(st.nextToken()),
                                        Double.parseDouble(st.nextToken()),
                                        Double.parseDouble(st.nextToken())
                                    ));
                                } else if (line.startsWith("%%Space Group")) {
                                    line = readLine().trim();
                                    /* standardize space group name.
                                       See Crystal.setSpaceGroup() */
                                    if ("P 21 21 21 (1)".equals(line)) {
                                        crystal.setSpaceGroup("P 2_1 2_1 2_1");
                                    } else {
                                        crystal.setSpaceGroup("P1");
                                    }
                                } else {
                                }
                                line = readLine();
                            }
                            chemModel.setCrystal(crystal);
                            chemSequence.addChemModel(chemModel);
                        }
                        line = readLine();
                    }
                    chemFile.addChemSequence(chemSequence);
                } else {
                    // disregard line
                }
                // read next line
                line = readLine();
            }
        } catch (IOException e) {
            logger.error("An IOException happened: ", e.getMessage());
            logger.debug(e);
            chemFile = null;
        } catch (CloneNotSupportedException e) {
            logger.error("An CloneNotSupportedException happened: ", e.getMessage());
            logger.debug(e);
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
                    ((IAtom)chemObject).setAtomicNumber(atomicnum);
                    ((IAtom)chemObject).setSymbol(type);
                } else {
                    logger.error("Incorrectly formated field value: " + field + ".");
                }
            } else if ("Charge".equals(command)) {
                try {
                    double charge = Double.parseDouble(field);
                    ((IAtom)chemObject).setCharge(charge);
                } catch (NumberFormatException e) {
                    logger.error("Incorrectly formated float field: " + field + ".");
                }
            } else if ("CMAPPINGS".equals(command)) {
            } else if ("FFType".equals(command)) {
            } else if ("Id".equals(command)) {
            } else if ("Mass".equals(command)) {
            } else if ("XYZ".equals(command)) {
            } else if ("ZOrder".equals(command)) {
            } else {
                logger.warn("Unkown PMP Atom command: " + command);
            }
        } else if ("Bond".equals(object)) {
            if ("Atom1".equals(command)) {
                int atomid = Integer.parseInt(field);
                // this assumes that the atoms involved in this bond are
                // already added, which seems the case in the PMP files
                int realatomid = ((Integer)atomids.get(new Integer(atomid))).intValue();
                IAtom a = molecule.getAtom(realatomid);
                ((IBond)chemObject).setAtomAt(a, 0);
            } else if ("Atom2".equals(command)) {
                int atomid = Integer.parseInt(field);
                // this assumes that the atoms involved in this bond are
                // already added, which seems the case in the PMP files
                logger.debug("atomids: " + atomids);
                logger.debug("atomid: " + atomid);
                int realatomid = ((Integer)atomids.get(new Integer(atomid))).intValue();
                IAtom a = molecule.getAtom(realatomid);
                ((IBond)chemObject).setAtomAt(a, 1);
            } else if ("Order".equals(command)) {
                double order = Double.parseDouble(field);
                ((IBond)chemObject).setOrder(order);
            } else if ("Id".equals(command)) {
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
            chemObject = builder.newAtom("C");
        } else if ("Bond".equals(object)) {
            chemObject = builder.newBond();
        } else if ("Model".equals(object)) {
            modelModel = builder.newChemModel();
            som = builder.newMoleculeSet();
            molecule = builder.newMolecule();
        } else {
            logger.error("Cannot construct PMP object type: " + object);
        }
    };

    public void close() throws IOException {
        input.close();
    }
}
