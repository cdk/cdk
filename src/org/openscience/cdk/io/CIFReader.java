/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.io;

import org.openscience.cdk.*;
import org.openscience.cdk.math.FortranFormat;
import org.openscience.cdk.geometry.CrystalGeometryTools;
import org.openscience.cdk.exception.*;
import java.io.*;
import java.util.Vector;
import java.util.StringTokenizer;
import javax.vecmath.Point3d;


/**
 * A reader for the CIF and mmCIF crystallographic formats. It's very ad hoc, not written
 * using any dictionary. So please complain if something is not working.
 * In addition, the things it does read are considered experimental.
 *
 * <p>The CIF example on the IUCR website has been tested, as well as Crambin (1CRN)
 * in the PDB database.
 *
 * @keyword file format, CIF
 * @keyword file format, mmCIF
 *
 * @author  E.L. Willighagen
 * @created 2003-10-12
 */
public class CIFReader extends DefaultChemObjectReader {

    private BufferedReader input;
    private org.openscience.cdk.tools.LoggingTool logger;

    private Crystal crystal = null;
    // cell parameters
    private double a = 0.0;
    private double b = 0.0;
    private double c = 0.0;
    private double alpha = 0.0;
    private double beta = 0.0;
    private double gamma = 0.0;
    
    /**
     * Create an ShelX file reader.
     *
     * @param input source of ShelX data
     */
    public CIFReader(Reader input) {
        this.input = new BufferedReader(input);
        this.logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
    }

    /**
     * Read a ChemFile from input
     *
     * @return the content in a ChemFile object
     */
    public ChemObject read(ChemObject object) throws CDKException {
        if (object instanceof ChemFile) {
            ChemFile cf = null;
            try {
                cf = readChemFile();
            } catch (IOException e) {
                logger.error("Input/Output error while reading from input.");
            }
            return cf;
        } else {
            throw new CDKException("Only supported is reading of ChemFile.");
        }
    }

    /**
     * Read the ShelX from input. Each ShelX document is expected to contain
     * one crystal structure.
     *
     * @return a ChemFile with the coordinates, charges, vectors, etc.
     */
    private ChemFile readChemFile() throws IOException {
        ChemFile file = new ChemFile();
        ChemSequence seq = new ChemSequence();
        ChemModel model = new ChemModel();
        crystal = new Crystal();

        String line = input.readLine();
        boolean end_found = false;
        while (input.ready() && line != null && !end_found) {
            if (line.startsWith("#")) {
                logger.warn("Skipping comment: " + line);
                // skip comment lines
            } else if (line.length() == 0) {
                logger.debug("Skipping empty line");
                // skip empty lines
            } else if (!(line.startsWith("_") || 
                  line.startsWith("loop_"))) {
                logger.warn("Skipping unrecognized line: " + line);
                // skip line
            } else {
                
                /* determine CIF command */
                String command = "";
                int spaceIndex = line.indexOf(" ");
                if (spaceIndex != -1) {
                    // everything upto space is command
                    try {
                        command = new String(line.substring(0, spaceIndex));
                    } catch (StringIndexOutOfBoundsException sioobe) {
                        // disregard this line
                        break;
                    }
                } else {
                    // complete line is command
                    command = line;
                }
                
                logger.debug("command: " + command);
                if (command.startsWith("_cell")) {
                    processCellParameter(command, line);
                } else if (command.equals("loop_")) {
                    processLoopBlock();
                } else if (command.equals("_symmetry_space_group_name_H-M")) {
                    String value = line.substring(29).trim();
                    crystal.setSpaceGroup(value);
                } else {
                    // skip command
                    logger.warn("Skipping command: " + command);
                    line = input.readLine();
                    if (line.startsWith(";")) {
                        logger.debug("Skipping block content");
                        line = input.readLine();
                        while (!line.equals(";")) line = input.readLine();
                        line = input.readLine();
                    }
                }
            }
            line = input.readLine();
        }
        logger.info("Adding crystal to file with #atoms: " + crystal.getAtomCount());
        model.setCrystal(crystal);
        seq.addChemModel(model);
        file.addChemSequence(seq);
        return file;
    }

    private void processCellParameter(String command, String line) {
        command = command.substring(6); // skip the "_cell." part
        if (command.equals("length_a")) {
            String value = line.substring(14).trim();
            a = parseIntoDouble(value);
            possiblySetCellParams(a,b,c,alpha,beta,gamma);
        } else if (command.equals("length_b")) {
            String value = line.substring(14).trim();
            b = parseIntoDouble(value);
            possiblySetCellParams(a,b,c,alpha,beta,gamma);
        } else if (command.equals("length_c")) {
            String value = line.substring(14).trim();
            c = parseIntoDouble(value);
            possiblySetCellParams(a,b,c,alpha,beta,gamma);
        } else if (command.equals("angle_alpha")) {
            String value = line.substring(17).trim();
            alpha = parseIntoDouble(value);
            possiblySetCellParams(a,b,c,alpha,beta,gamma);
        } else if (command.equals("angle_beta")) {
            String value = line.substring(16).trim();
            beta = parseIntoDouble(value);
            possiblySetCellParams(a,b,c,alpha,beta,gamma);
        } else if (command.equals("angle_gamma")) {
            String value = line.substring(17).trim();
            gamma = parseIntoDouble(value);
            possiblySetCellParams(a,b,c,alpha,beta,gamma);
        }        
    }
    
    private void possiblySetCellParams(double a,double b,double c,double alpha,double beta,double gamma) {
        if (a != 0.0 && b != 0.0 && c != 0.0 &&
            alpha != 0.0 && beta != 0.0 && gamma != 0.0) {
            logger.info("Found and set crystal cell parameters");
            double[][] axes = CrystalGeometryTools.notionalToCartesian(a,b,c, alpha, beta, gamma);
            
            crystal.setA(axes[0][0], axes[0][1], axes[0][2]);
            crystal.setB(axes[1][0], axes[1][1], axes[1][2]);
            crystal.setC(axes[2][0], axes[2][1], axes[2][2]);
        }
    }
    
    private void processLoopBlock() throws IOException {
        String line = input.readLine();
        if (line.startsWith("_atom")) {
            logger.info("Found atom loop block");
            processAtomLoopBlock(line);
        } else {
            logger.warn("Skipping loop block");
            skipUntilEmptyOrCommentLine(line);
        }
    }

    private void skipUntilEmptyOrCommentLine(String line) throws IOException {
        // skip everything until empty line, or comment line
        while (line != null && line.length() > 0 && line.charAt(0) != '#') {
            line = input.readLine();
        }
    }
    
    private void processAtomLoopBlock(String firstLine) throws IOException {
        int atomLabel = -1; // -1 means not found in this block
        int atomSymbol = -1;
        int atomFractX = -1;
        int atomFractY = -1;
        int atomFractZ = -1;
        int atomRealX = -1;
        int atomRealY = -1;
        int atomRealZ = -1;
        String line = firstLine;
        int headerCount = 0;
        boolean hasParsableInformation = false;
        while (line != null && line.charAt(0) == '_') {
            headerCount++;
            if (line.equals("_atom_site_label")) {
                atomLabel = headerCount;
                hasParsableInformation = true;
                logger.info("label found in col: " + atomLabel);
            } else if (line.startsWith("_atom_site_fract_x")) {
                atomFractX = headerCount;
                hasParsableInformation = true;
                logger.info("frac x found in col: " + atomFractX);
            } else if (line.startsWith("_atom_site_fract_y")) {
                atomFractY = headerCount;
                hasParsableInformation = true;
                logger.info("frac x found in col: " + atomFractY);
            } else if (line.startsWith("_atom_site_fract_z")) {
                atomFractZ = headerCount;
                hasParsableInformation = true;
                logger.info("frac x found in col: " + atomFractZ);
            } else if (line.equals("_atom_site.Cartn_x ")) {
                atomRealX = headerCount;
                hasParsableInformation = true;
                logger.info("cart x found in col: " + atomRealX);
            } else if (line.equals("_atom_site.Cartn_y ")) {
                atomRealY = headerCount;
                hasParsableInformation = true;
                logger.info("cart y found in col: " + atomRealY);
            } else if (line.equals("_atom_site.Cartn_z ")) {
                atomRealZ = headerCount;
                hasParsableInformation = true;
                logger.info("cart z found in col: " + atomRealZ);
            } else if (line.equals("_atom_site.type_symbol ")) {
                atomSymbol = headerCount;
                hasParsableInformation = true;
                logger.info("type_symbol found in col: " + atomSymbol);
            } else {
                logger.warn("Ignoring atom loop block field: " + line);
            }
            line = input.readLine();
        }
        if (hasParsableInformation == false ) {
            logger.info("No parsable info found");
            skipUntilEmptyOrCommentLine(line);
        } else {
            // now that headers are parsed, read the data
            while( line != null && line.length() > 0 && line.charAt(0) != '#') {
                logger.debug("new row");
                StringTokenizer tokenizer = new StringTokenizer(line);
                if (tokenizer.countTokens() < headerCount) {
                    logger.warn("Column count mismatch; assuming continued on next line");
                    logger.debug("Found #expected, #found: " + headerCount + ", " + tokenizer.countTokens());
                    tokenizer = new StringTokenizer(line + input.readLine());
                }
                int colIndex = 0;
                // process one row
                Atom atom = new Atom("C");
                double[] frac = new double[3];
                double[] real = new double[3];
                boolean hasFractional = false;
                boolean hasCartesian = false;
                while (tokenizer.hasMoreTokens()) {
                    colIndex++;
                    String field = tokenizer.nextToken();
                    logger.debug("Parsing col,token: " + colIndex + "=" + field);
                    if (colIndex == atomLabel) {
                        String element = extractFirstLetters(field);
                        atom.setSymbol(element);
                        atom.setID(field);
                    } else if (colIndex == atomFractX) {
                        hasFractional = true;
                        frac[0] = parseIntoDouble(field);
                    } else if (colIndex == atomFractY) {
                        hasFractional = true;
                        frac[1] = parseIntoDouble(field);
                    } else if (colIndex == atomFractZ) {
                        hasFractional = true;
                        frac[2] = parseIntoDouble(field);
                    } else if (colIndex == atomSymbol) {
                        atom.setSymbol(field);
                    } else if (colIndex == atomRealX) {
                        hasCartesian = true;
                        logger.debug("Adding x3: " + parseIntoDouble(field));
                        real[0] = parseIntoDouble(field);
                    } else if (colIndex == atomRealY) {
                        hasCartesian = true;
                        logger.debug("Adding y3: " + parseIntoDouble(field));
                        real[1] = parseIntoDouble(field);
                    } else if (colIndex == atomRealZ) {
                        hasCartesian = true;
                        logger.debug("Adding x3: " + parseIntoDouble(field));
                        real[2] = parseIntoDouble(field);
                    }
                }
                if (hasCartesian) {
                    double[] a = crystal.getA();
                    double[] b = crystal.getB();
                    double[] c = crystal.getC();
                    frac = CrystalGeometryTools.cartesianToFractional(a, b, c, real);
                    atom.setFractionalPoint3D(new Point3d(frac[0], frac[1], frac[2]));
                }
                if (hasFractional) {
                    atom.setFractionalPoint3D(new Point3d(frac[0], frac[1], frac[2]));
                }
                logger.debug("Adding atom: " + atom);
                crystal.addAtom(atom);
                
                // look up next row
                line = input.readLine();
            }
        }
    }
    
    /**
     * Process double in the format: '.071(1)'.
     */
    private double parseIntoDouble(String value) {
        double returnVal = 0.0;
        if (value.charAt(0) == '.') value = "0" + value;
        int bracketIndex = value.indexOf("(");
        if (bracketIndex != -1) {
            value = value.substring(0, bracketIndex);
        }
        try {
            returnVal = Double.parseDouble(value);
        } catch (Exception exception) {
            logger.error("Could not parse double string: " + value);
        }
        return returnVal;
    }
    
    private String extractFirstLetters(String value) {
        StringBuffer result = new StringBuffer();
        for (int i=0; i<value.length(); i++) {
            if (Character.isDigit(value.charAt(i))) {
                break;
            } else {
                result.append(value.charAt(i));
            }
        }
        return result.toString();
    }
    
    public void close() throws IOException {
        input.close();
    }
}
