/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2006  The Jmol Development Team
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.io.formats.MDLV3000Format;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Class that implements the new MDL rxn format introduced in August 2002.
 * The overall syntax is compatible with the old format, but I consider
 * the format completely different, and thus implemented a separate Reader
 * for it.
 *
 * <p>This Reader should read all information, but it does not (yet). Please
 * report any problem with information not read as a bug. Refer to the method
 * of this class to get more insight in what is read and what is not.
 * In addition, the cdk.log will show the bits that are not interpreted.
 *
 * @cdk.module io
 *
 * @author  Egon Willighagen <egonw@sci.kun.nl>
 * @cdk.created 2003-10-05
 * 
 * @cdk.keyword MDL RXN V3000
 * @cdk.require java1.4+
 */
public class MDLV3000Reader extends DefaultChemObjectReader {

    BufferedReader input = null;
    private LoggingTool logger = null;

    private Pattern keyValueTuple;
    private Pattern keyValueTuple2;
    
    public MDLV3000Reader(Reader in) {
        logger = new LoggingTool(this);
        input = new BufferedReader(in);
        initIOSettings();
        /* compile patterns */
        keyValueTuple = Pattern.compile("\\s*(\\w+)=([^\\s]*)(.*)"); // e.g. CHG=-1
        keyValueTuple2 = Pattern.compile("\\s*(\\w+)=\\(([^\\)]*)\\)(.*)"); // e.g. ATOMS=(1 31)
    }

    public MDLV3000Reader(InputStream input) {
        this(new InputStreamReader(input));
    }
    
    public MDLV3000Reader() {
        this(new StringReader(""));
    }
    
    public IChemFormat getFormat() {
        return new MDLV3000Format();
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

    public IChemObject read(IChemObject object) throws CDKException {
        if (object instanceof IMolecule) {
            return readMolecule(object.getBuilder());
        }
        return null;
    }
    
    public IMolecule readMolecule(IChemObjectBuilder builder) throws CDKException {
        return builder.newMolecule(readConnectionTable(builder));
    }
    
    public IAtomContainer readConnectionTable(IChemObjectBuilder builder) throws CDKException {
        IAtomContainer readData = builder.newAtomContainer();
        boolean foundEND = false;
        while (isReady() && !foundEND) {
            String command = readCommand();
            if ("END CTAB".equals(command)) {
                foundEND = true;
            } else if ("BEGIN CTAB".equals(command)) {
                // that's fine
            } else if ("COUNTS".equals(command)) {
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
        }
        return readData;
    }
    
    /**
     * Reads the atoms, coordinates and charges.
     *
     * <p>IMPORTANT: it does not support the atom list and its negation!
     */
    public void readAtomBlock(IAtomContainer readData) throws CDKException {
        boolean foundEND = false;
        while (isReady() && !foundEND) {
            String command = readCommand();
            if ("END ATOM".equals(command)) {
                // FIXME: should check wether 3D is really 2D
                foundEND = true;
            } else {
                logger.debug("Parsing atom from: " + command);
                StringTokenizer tokenizer = new StringTokenizer(command);
                IAtom atom = readData.getBuilder().newAtom("C");
                // parse the index
                try {
                    String indexString = tokenizer.nextToken();
                    atom.setID(indexString);
                } catch (Exception exception) {
                    String error = "Error while parsing atom index";
                    logger.error(error);
                    logger.debug(exception);
                    throw new CDKException(error, exception);
                }
                // parse the element
                String element = tokenizer.nextToken();
                boolean isElement = false;
                try {
                    isElement = IsotopeFactory.getInstance(atom.getBuilder()).isElement(element);
                } catch (Exception exception) {
                    throw new CDKException("Could not determine if element exists!", exception);
                }
                if (isPseudoAtom(element)) {
                    atom = readData.getBuilder().newPseudoAtom(atom);
                } else if (isElement) {
                    atom.setSymbol(element);
                } else {
                    String error = "Cannot parse element of type: " + element;
                    logger.error(error);
                    throw new CDKException("(Possible CDK bug) " + error);
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
                if (command.indexOf("=") != -1) {
                    Hashtable options = parseOptions(exhaustStringTokenizer(tokenizer));
                    Enumeration keys = options.keys();
                    while (keys.hasMoreElements()) {
                        String key = (String)keys.nextElement();
                        String value = (String)options.get(key);
                        try {
                            if (key.equals("CHG")) {
                                int charge = Integer.parseInt(value);
                                if (charge != 0) { // zero is no charge specified
                                    atom.setFormalCharge(charge);
                                }
                            } else {
                                logger.warn("Not parsing key: " + key);
                            }
                        } catch (Exception exception) {
                            String error = "Error while parsing key/value " + key + "=" +
                            value + ": " + exception.getMessage();
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
        boolean foundEND = false;
        while (isReady() && !foundEND) {
            String command = readCommand();
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
                        bond.setOrder((double)order);
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
                    IAtom atom1 = readData.getAtomAt(indexAtom1 -1);
                    bond.setAtomAt(atom1, 0);
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
                    IAtom atom2 = readData.getAtomAt(indexAtom2 -1);
                    bond.setAtomAt(atom2, 1);
                } catch (Exception exception) {
                    String error = "Error while parsing index atom 2 in bond";
                    logger.error(error);
                    logger.debug(exception);
                    throw new CDKException(error, exception);
                }
                // the rest are key=value fields
                if (command.indexOf("=") != -1) {
                    Hashtable options = parseOptions(exhaustStringTokenizer(tokenizer));
                    Enumeration keys = options.keys();
                    while (keys.hasMoreElements()) {
                        String key = (String)keys.nextElement();
                        String value = (String)options.get(key);
                        try {
                            if (key.equals("CFG")) {
                                int configuration = Integer.parseInt(value);
                                if (configuration == 0) {
                                    bond.setStereo(CDKConstants.STEREO_BOND_NONE);
                                } else if (configuration == 1) {
                                    bond.setStereo(CDKConstants.STEREO_BOND_UP);
                                } else if (configuration == 2) {
                                    bond.setStereo(CDKConstants.STEREO_BOND_UNDEFINED);
                                } else if (configuration == 3) {
                                    bond.setStereo(CDKConstants.STEREO_BOND_DOWN);
                                }
                            } else {
                                logger.warn("Not parsing key: " + key);
                            }
                        } catch (Exception exception) {
                            String error = "Error while parsing key/value " + key + "=" +
                            value + ": " + exception.getMessage();
                            logger.error(error);
                            logger.debug(exception);
                            throw new CDKException(error, exception);
                        }
                    }
                }
                
                // storing bond
                readData.addBond(bond);
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
            String command = readCommand();
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
                Hashtable options = new Hashtable();
                if (command.indexOf("=") != -1) {
                    options = parseOptions(exhaustStringTokenizer(tokenizer));
                }

                // now interpret line
                if (type.startsWith("SUP")) {
                    Enumeration keys = options.keys();
                    int atomID = -1;
                    String label = "";
                    while (keys.hasMoreElements()) {
                        String key = (String)keys.nextElement();
                        String value = (String)options.get(key);
                        try {
                            if (key.equals("ATOMS")) {
                                StringTokenizer atomsTokenizer = new StringTokenizer(value);
                                int atomCount = Integer.parseInt(atomsTokenizer.nextToken()); // should be 1
                                atomID = Integer.parseInt(atomsTokenizer.nextToken());
                            } else if (key.equals("LABEL")) {
                                label = value;
                            } else {
                                logger.warn("Not parsing key: " + key);
                            }
                        } catch (Exception exception) {
                            String error = "Error while parsing key/value " + key + "=" +
                            value + ": " + exception.getMessage();
                            logger.error(error);
                            logger.debug(exception);
                            throw new CDKException(error, exception);
                        }
                        if (atomID != -1 && label.length() > 0) {
                        	IAtom atom = readData.getAtomAt(atomID-1);
                            if (!(atom instanceof IPseudoAtom)) {
                                atom = readData.getBuilder().newPseudoAtom(atom);
                            }
                            ((IPseudoAtom)atom).setLabel(label);
                            readData.setAtomAt(atomID-1, atom);
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
    private String readCommand() throws CDKException {
        String line = readLine();
        if (line.startsWith("M  V30 ")) {
            String command =  line.substring(7);
            if (command.endsWith("-")) {
                command = command.substring(0, command.length()-1);
                command += readCommand();
            }
            return command;
        } else {
            throw new CDKException("Could not read MDL file: unexpected line: " + line);
        }
    }
    
    private Hashtable parseOptions(String string) throws CDKException {
        Hashtable keyValueTuples = new Hashtable();
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
        buffer.append(" ");
        while (tokenizer.hasMoreTokens()) {
            buffer.append(tokenizer.nextToken());
            buffer.append(" ");
        }
        return buffer.toString();
    }
    
    public String readLine() throws CDKException {
        String line = null;
        try {
            line = input.readLine();
            logger.debug("read line: " + line);
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

    private boolean isPseudoAtom(String element) {
        if (element.equals("R#") || // a Rgroup
            element.equals("Q") ||  // any atom but C and H
            element.equals("A") ||  // any atom
            element.equals("*")) {  // 'star' atom
            return true;
        }
        return false;
    }
    
    public boolean accepts(IChemObject object) {
        if (object instanceof IMolecule) {
            return true;
        }
        return false;
    }

    public void close() throws IOException {
        input.close();
    }
    
    private void initIOSettings() {
    }
    
    public IOSetting[] getIOSettings() {
        return new IOSetting[0];
    }
    
}
