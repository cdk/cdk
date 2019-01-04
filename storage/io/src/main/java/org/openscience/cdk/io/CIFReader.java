/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.geometry.CrystalGeometryTools;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.io.formats.CIFFormat;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.io.*;
import java.util.StringTokenizer;

/**
 * This is not a reader for the CIF and mmCIF crystallographic formats.
 * It is able, however, to extract some content from such files.
 * It's very ad hoc, not written
 * using any dictionary. So please complain if something is not working.
 * In addition, the things it does read are considered experimental.
 *
 * <p>The CIF example on the IUCR website has been tested, as well as Crambin (1CRN)
 * in the PDB database.
 *
 * @cdk.module io
 * @cdk.githash
 *
 * @cdk.keyword file format, CIF
 * @cdk.keyword file format, mmCIF
 *
 * @author  E.L. Willighagen
 * @cdk.created 2003-10-12
 * @cdk.iooptions
 */
public class CIFReader extends DefaultChemObjectReader {

    private BufferedReader      input;
    private static ILoggingTool logger  = LoggingToolFactory.createLoggingTool(CIFReader.class);

    private ICrystal            crystal = null;
    // cell parameters
    private double              a       = 0.0;
    private double              b       = 0.0;
    private double              c       = 0.0;
    private double              alpha   = 0.0;
    private double              beta    = 0.0;
    private double              gamma   = 0.0;

    /**
     * Create an CIF like file reader.
     *
     * @param input source of CIF data
     */
    public CIFReader(Reader input) {
        this.input = new BufferedReader(input);
    }

    public CIFReader(InputStream input) {
        this(new InputStreamReader(input));
    }

    public CIFReader() {
        this(new StringReader(""));
    }

    @Override
    public IResourceFormat getFormat() {
        return CIFFormat.getInstance();
    }

    @Override
    public void setReader(Reader reader) throws CDKException {
        this.input = new BufferedReader(reader);
    }

    @Override
    public void setReader(InputStream input) throws CDKException {
        setReader(new InputStreamReader(input));
    }

    @Override
    public boolean accepts(Class<? extends IChemObject> testClass) {
        if (IChemFile.class.equals(testClass)) return true;
        Class<?>[] interfaces = testClass.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            if (IChemFile.class.equals(interfaces[i])) return true;
        }
        Class superClass = testClass.getSuperclass();
        if (superClass != null) return this.accepts(superClass);
        return false;
    }

    /**
     * Read a ChemFile from input.
     *
     * @return the content in a ChemFile object
     */
    @Override
    public <T extends IChemObject> T read(T object) throws CDKException {
        if (object instanceof IChemFile) {
            IChemFile cf = (IChemFile) object;
            try {
                cf = readChemFile(cf);
            } catch (IOException e) {
                logger.error("Input/Output error while reading from input.");
            }
            return (T) cf;
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
    private IChemFile readChemFile(IChemFile file) throws IOException {
        IChemSequence seq = file.getBuilder().newInstance(IChemSequence.class);
        IChemModel model = file.getBuilder().newInstance(IChemModel.class);
        crystal = file.getBuilder().newInstance(ICrystal.class);

        String line = input.readLine();
        boolean end_found = false;
        while (input.ready() && line != null && !end_found) {
            if (line.length() == 0) {
                logger.debug("Skipping empty line");
                // skip empty lines
            } else if (line.charAt(0) == '#') {
                logger.warn("Skipping comment: ", line);
                // skip comment lines
            } else if (!(line.charAt(0) == '_' || line.startsWith("loop_"))) {
                logger.warn("Skipping unrecognized line: ", line);
                // skip line
            } else {

                /* determine CIF command */
                String command = "";
                int spaceIndex = line.indexOf(' ');
                if (spaceIndex != -1) {
                    // everything upto space is command
                    try {
                        command = line.substring(0, spaceIndex);
                    } catch (StringIndexOutOfBoundsException sioobe) {
                        // disregard this line
                        break;
                    }
                } else {
                    // complete line is command
                    command = line;
                }

                logger.debug("command: ", command);
                if (command.startsWith("_cell")) {
                    processCellParameter(command, line);
                } else if (command.equals("loop_")) {
                    line = processLoopBlock();
                    continue;
                } else if (command.equals("_symmetry_space_group_name_H-M")) {
                    String value = line.substring(29).trim();
                    crystal.setSpaceGroup(value);
                } else {
                    // skip command
                    logger.warn("Skipping command: ", command);
                    line = input.readLine();
                    if (line != null && line.startsWith(";")) {
                        logger.debug("Skipping block content");
                        while ((line = input.readLine()) != null &&
                               !line.startsWith(";")) {
                            logger.debug("Skipping block line: ", line);
                        }
                    }
                }
            }
            line = input.readLine();
        }
        logger.info("Adding crystal to file with #atoms: ", crystal.getAtomCount());
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
            possiblySetCellParams(a, b, c, alpha, beta, gamma);
        } else if (command.equals("length_b")) {
            String value = line.substring(14).trim();
            b = parseIntoDouble(value);
            possiblySetCellParams(a, b, c, alpha, beta, gamma);
        } else if (command.equals("length_c")) {
            String value = line.substring(14).trim();
            c = parseIntoDouble(value);
            possiblySetCellParams(a, b, c, alpha, beta, gamma);
        } else if (command.equals("angle_alpha")) {
            String value = line.substring(17).trim();
            alpha = parseIntoDouble(value);
            possiblySetCellParams(a, b, c, alpha, beta, gamma);
        } else if (command.equals("angle_beta")) {
            String value = line.substring(16).trim();
            beta = parseIntoDouble(value);
            possiblySetCellParams(a, b, c, alpha, beta, gamma);
        } else if (command.equals("angle_gamma")) {
            String value = line.substring(17).trim();
            gamma = parseIntoDouble(value);
            possiblySetCellParams(a, b, c, alpha, beta, gamma);
        }
    }

    private void possiblySetCellParams(double a, double b, double c, double alpha, double beta, double gamma) {
        if (a != 0.0 && b != 0.0 && c != 0.0 && alpha != 0.0 && beta != 0.0 && gamma != 0.0) {
            logger.info("Found and set crystal cell parameters");
            Vector3d[] axes = CrystalGeometryTools.notionalToCartesian(a, b, c, alpha, beta, gamma);

            crystal.setA(axes[0]);
            crystal.setB(axes[1]);
            crystal.setC(axes[2]);
        }
    }

    private String processLoopBlock() throws IOException {
        String line = input.readLine().trim();
        if (line.startsWith("_atom")) {
            logger.info("Found atom loop block");
            return processAtomLoopBlock(line);
        } else {
            logger.warn("Skipping loop block");
            return skipLoop(line);
        }
    }

    private String skipLoop(String line) throws IOException {
        // skip everything until the end of the loop body
        if (line != null) line = line.trim();
        // First, skip the loop_ data name list:
        while (line != null && line.length() > 0 && line.charAt(0) == '_') {
            line = input.readLine();
            if (line != null) line = line.trim();
        }
        return skipLoopBody(line);
    }

    private String skipLoopBody(String line) throws IOException {
        // Then, skip every line that looks like starting with a CIF value:
        while (line != null && line.length() > 0 &&
        		line.charAt(0) != '#' &&
        		line.charAt(0) != '_' &&
        		!line.startsWith("loop_") &&
        		!line.startsWith("data_")) {
            line = input.readLine();
            if (line != null) line = line.trim();
        }
        return line;
    }

    private String processAtomLoopBlock(String firstLine) throws IOException {
        int atomLabel = -1; // -1 means not found in this block
        int atomSymbol = -1;
        int atomFractX = -1;
        int atomFractY = -1;
        int atomFractZ = -1;
        int atomRealX = -1;
        int atomRealY = -1;
        int atomRealZ = -1;
        String line = firstLine.trim();
        int headerCount = 0;
        boolean hasParsableInformation = false;
        while (line != null && line.charAt(0) == '_') {
            headerCount++;
            if (line.equals("_atom_site_label") || line.equals("_atom_site_label_atom_id")) {
                atomLabel = headerCount;
                hasParsableInformation = true;
                logger.info("label found in col: ", atomLabel);
            } else if (line.startsWith("_atom_site_fract_x")) {
                atomFractX = headerCount;
                hasParsableInformation = true;
                logger.info("frac x found in col: ", atomFractX);
            } else if (line.startsWith("_atom_site_fract_y")) {
                atomFractY = headerCount;
                hasParsableInformation = true;
                logger.info("frac y found in col: ", atomFractY);
            } else if (line.startsWith("_atom_site_fract_z")) {
                atomFractZ = headerCount;
                hasParsableInformation = true;
                logger.info("frac z found in col: ", atomFractZ);
            } else if (line.equals("_atom_site.Cartn_x")) {
                atomRealX = headerCount;
                hasParsableInformation = true;
                logger.info("cart x found in col: ", atomRealX);
            } else if (line.equals("_atom_site.Cartn_y")) {
                atomRealY = headerCount;
                hasParsableInformation = true;
                logger.info("cart y found in col: ", atomRealY);
            } else if (line.equals("_atom_site.Cartn_z")) {
                atomRealZ = headerCount;
                hasParsableInformation = true;
                logger.info("cart z found in col: ", atomRealZ);
            } else if (line.equals("_atom_site.type_symbol")) {
                atomSymbol = headerCount;
                hasParsableInformation = true;
                logger.info("type_symbol found in col: ", atomSymbol);
            } else {
                logger.warn("Ignoring atom loop block field: ", line);
            }
            line = input.readLine().trim();
        }
        if (!hasParsableInformation) {
            logger.info("No parsable info found");
            return skipLoopBody(line);
        } else {
            // now that headers are parsed, read the data
            while (line != null && line.length() > 0 &&
                    line.charAt(0) != '#' &&
                    line.charAt(0) != '_' &&
                    !line.startsWith("loop_") &&
                    !line.startsWith("data_")) {
                logger.debug("new row");
                StringTokenizer tokenizer = new StringTokenizer(line);
                if (tokenizer.countTokens() < headerCount) {
                    logger.warn("Column count mismatch; assuming continued on next line");
                    logger.debug("Found #expected, #found: ", headerCount, ", ", tokenizer.countTokens());
                    tokenizer = new StringTokenizer(line + input.readLine());
                }
                int colIndex = 0;
                // process one row
                IAtom atom = crystal.getBuilder().newInstance(IAtom.class, "C");
                Point3d frac = new Point3d();
                Point3d real = new Point3d();
                boolean hasFractional = false;
                boolean hasCartesian = false;
                while (tokenizer.hasMoreTokens()) {
                    colIndex++;
                    String field = tokenizer.nextToken();
                    logger.debug("Parsing col,token: ", colIndex, "=", field);
                    if (colIndex == atomLabel) {
                        if (atomSymbol == -1) {
                            // no atom symbol found, use label
                            String element = extractFirstLetters(field);
                            atom.setSymbol(element);
                        }
                        atom.setID(field);
                    } else if (colIndex == atomFractX) {
                        hasFractional = true;
                        frac.x = parseIntoDouble(field);
                    } else if (colIndex == atomFractY) {
                        hasFractional = true;
                        frac.y = parseIntoDouble(field);
                    } else if (colIndex == atomFractZ) {
                        hasFractional = true;
                        frac.z = parseIntoDouble(field);
                    } else if (colIndex == atomSymbol) {
                        atom.setSymbol(field);
                    } else if (colIndex == atomRealX) {
                        hasCartesian = true;
                        logger.debug("Adding x3: ", parseIntoDouble(field));
                        real.x = parseIntoDouble(field);
                    } else if (colIndex == atomRealY) {
                        hasCartesian = true;
                        logger.debug("Adding y3: ", parseIntoDouble(field));
                        real.y = parseIntoDouble(field);
                    } else if (colIndex == atomRealZ) {
                        hasCartesian = true;
                        logger.debug("Adding x3: ", parseIntoDouble(field));
                        real.z = parseIntoDouble(field);
                    }
                }
                if (hasCartesian) {
                    Vector3d a = crystal.getA();
                    Vector3d b = crystal.getB();
                    Vector3d c = crystal.getC();
                    frac = CrystalGeometryTools.cartesianToFractional(a, b, c, real);
                    atom.setFractionalPoint3d(frac);
                }
                if (hasFractional) {
                    atom.setFractionalPoint3d(frac);
                }
                logger.debug("Adding atom: ", atom);
                crystal.addAtom(atom);

                // look up next row
                line = input.readLine();
                if (line != null) line = line.trim();
            }
        }
        return line;
    }

    /**
     * Process double in the format: '.071(1)'.
     */
    private double parseIntoDouble(String value) {
        double returnVal = 0.0;
        if (value.charAt(0) == '.') value = "0" + value;
        int bracketIndex = value.indexOf('(');
        if (bracketIndex != -1) {
            value = value.substring(0, bracketIndex);
        }
        try {
            returnVal = Double.parseDouble(value);
        } catch (Exception exception) {
            logger.error("Could not parse double string: ", value);
        }
        return returnVal;
    }

    private String extractFirstLetters(String value) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < value.length(); i++) {
            if (Character.isDigit(value.charAt(i))) {
                break;
            } else {
                result.append(value.charAt(i));
            }
        }
        return result.toString();
    }

    @Override
    public void close() throws IOException {
        input.close();
    }
}
