/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2002-2007  Egon Willighagen <egonw@users.sf.net>
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
import java.util.StringTokenizer;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.CrystalGeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.ICrystal;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.ShelXFormat;
import org.openscience.cdk.math.FortranFormat;
import org.openscience.cdk.tools.LoggingTool;


/**
 * A reader for ShelX output (RES) files. It does not read all information.
 * The list of fields that is read: REM, END, CELL, SPGR.
 * In additions atoms are read.
 *
 * <p>A reader for ShelX files. It currently supports ShelXL.
 *
 * <p>The ShelXL format is described on the net:
 * <a href="http://www.msg.ucsf.edu/local/programs/shelxl/ch_07.html"
 * http://www.msg.ucsf.edu/local/programs/shelxl/ch_07.html</a>.
 *
 * @cdk.module io
 * @cdk.svnrev  $Revision$
 *
 * @cdk.keyword file format, ShelXL
 * @author E.L. Willighagen
 */
@TestClass("org.openscience.cdk.io.ShelXReaderTest")
public class ShelXReader extends DefaultChemObjectReader {

    private BufferedReader input;
    private LoggingTool logger;

    /**
     * Create an ShelX file reader.
     *
     * @param input source of ShelX data
     */
    public ShelXReader(Reader input) {
        this.input = new BufferedReader(input);
        this.logger = new LoggingTool(this);
    }

    public ShelXReader(InputStream input) {
        this(new InputStreamReader(input));
    }
    
    public ShelXReader() {
        this(new StringReader(""));
    }
    
    @TestMethod("testGetFormat")
    public IResourceFormat getFormat() {
        return ShelXFormat.getInstance();
    }

    @TestMethod("testSetReader_Reader")
    public void setReader(Reader input) throws CDKException {
        if (input instanceof BufferedReader) {
            this.input = (BufferedReader)input;
        } else {
            this.input = new BufferedReader(input);
        }
    }
    
    @TestMethod("testSetReader_InputStream")
    public void setReader(InputStream input) throws CDKException {
        setReader(new InputStreamReader(input));
    }

	@TestMethod("testAccepts")
    public boolean accepts(Class classObject) {
		Class[] interfaces = classObject.getInterfaces();
		for (int i=0; i<interfaces.length; i++) {
			if (ICrystal.class.equals(interfaces[i])) return true;
			if (IChemFile.class.equals(interfaces[i])) return true;
		}
    Class superClass = classObject.getSuperclass();
    if (superClass != null) return this.accepts(superClass);
		return false;
	}

    /**
     * Read a ChemFile from input
     *
     * @return the content in a ChemFile object
     */
    public IChemObject read(IChemObject object) throws CDKException {
        if (object instanceof IChemFile) {
            try {
                return readChemFile((IChemFile)object);
            } catch (IOException e) {
                logger.error("Input/Output error while reading from input: " + e.getMessage());
                throw new CDKException(e.getMessage());
            }
        } else if (object instanceof ICrystal) {
            try {
                return readCrystal((ICrystal)object);
            } catch (IOException e) {
                logger.error("Input/Output error while reading from input: " + e.getMessage());
                throw new CDKException(e.getMessage(), e);
            }
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
        IChemSequence seq = file.getBuilder().newChemSequence();
        IChemModel model = file.getBuilder().newChemModel();
        ICrystal crystal = readCrystal(file.getBuilder().newCrystal());
        model.setCrystal(crystal);
        seq.addChemModel(model);
        file.addChemSequence(seq);
        return file;
    }

    private ICrystal readCrystal(ICrystal crystal) throws IOException {
        String line = input.readLine();
        boolean end_found = false;
        while (input.ready() && line != null && !end_found) {
            /* is line continued? */
            if (line.length() > 0 &&
                line.substring(line.length()-1).equals("=")) {
                /* yes, line is continued */
                line = line + input.readLine();
            }

            /* determine ShelX command */
            String command;
            try {
                command = new String(line.substring(0, 4));
            } catch (StringIndexOutOfBoundsException sioobe) {
                // disregard this line
                break;
            }

            logger.debug("command: " + command);
            if (command.substring(0,3).equalsIgnoreCase("REM")) {
                /* line is comment, disregard */

            /* 7.1 Crystal data and general instructions */
            } else if (command.substring(0,3).equalsIgnoreCase("END")) {
                end_found = true;
            } else if (command.equalsIgnoreCase("TITL")) {
            } else if (command.equalsIgnoreCase("CELL")) {
                /* example:
                 * CELL  1.54184   23.56421  7.13203 18.68928  90.0000 109.3799  90.0000
                 * CELL   1.54184   7.11174  21.71704  30.95857  90.000  90.000  90.000
                 */
                StringTokenizer st = new StringTokenizer(line);
                st.nextToken(); // String command_again
                st.nextToken(); // String wavelength
                String sa = st.nextToken();
                String sb = st.nextToken();
                String sc = st.nextToken();
                String salpha = st.nextToken();
                String sbeta  = st.nextToken();
                String sgamma = st.nextToken();
                logger.debug("a: " + sa);
                logger.debug("b: " + sb);
                logger.debug("c: " + sc);
                logger.debug("alpha: " + salpha);
                logger.debug("beta : " + sbeta);
                logger.debug("gamma: " + sgamma);

                double a = FortranFormat.atof(sa);
                double b = FortranFormat.atof(sb);
                double c = FortranFormat.atof(sc);
                double alpha = FortranFormat.atof(salpha);
                double beta  = FortranFormat.atof(sbeta);
                double gamma = FortranFormat.atof(sgamma);
                
                Vector3d[] axes = CrystalGeometryTools.notionalToCartesian(a,b,c, alpha, beta, gamma);

                crystal.setA(axes[0]);
                crystal.setB(axes[1]);
                crystal.setC(axes[2]);
            } else if (command.equalsIgnoreCase("ZERR")) {
            } else if (command.equalsIgnoreCase("LATT")) {
            } else if (command.equalsIgnoreCase("SYMM")) {
            } else if (command.equalsIgnoreCase("SFAC")) {
            } else if (command.equalsIgnoreCase("DISP")) {
            } else if (command.equalsIgnoreCase("UNIT")) {
            } else if (command.equalsIgnoreCase("LAUE")) {
            } else if (command.equalsIgnoreCase("REM ")) {
            } else if (command.equalsIgnoreCase("MORE")) {
            } else if (command.equalsIgnoreCase("TIME")) {

            /* 7.2 Reflection data input */
            } else if (command.equalsIgnoreCase("HKLF")) {
            } else if (command.equalsIgnoreCase("OMIT")) {
            } else if (command.equalsIgnoreCase("SHEL")) {
            } else if (command.equalsIgnoreCase("BASF")) {
            } else if (command.equalsIgnoreCase("TWIN")) {
            } else if (command.equalsIgnoreCase("EXTI")) {
            } else if (command.equalsIgnoreCase("SWAT")) {
            } else if (command.equalsIgnoreCase("HOPE")) {
            } else if (command.equalsIgnoreCase("MERG")) {

            /* 7.3 Atom list and least-squares constraints */
            } else if (command.equalsIgnoreCase("SPEC")) {
            } else if (command.equalsIgnoreCase("RESI")) {
            } else if (command.equalsIgnoreCase("MOVE")) {
            } else if (command.equalsIgnoreCase("ANIS")) {
            } else if (command.equalsIgnoreCase("AFIX")) {
            } else if (command.equalsIgnoreCase("HFIX")) {
            } else if (command.equalsIgnoreCase("FRAG")) {
            } else if (command.equalsIgnoreCase("FEND")) {
            } else if (command.equalsIgnoreCase("EXYZ")) {
            } else if (command.equalsIgnoreCase("EXTI")) {
            } else if (command.equalsIgnoreCase("EADP")) {
            } else if (command.equalsIgnoreCase("EQIV")) {

            /* 7.4 The connectivity list */
            } else if (command.equalsIgnoreCase("CONN")) {
            } else if (command.equalsIgnoreCase("PART")) {
            } else if (command.equalsIgnoreCase("BIND")) {
            } else if (command.equalsIgnoreCase("FREE")) {

            /* 7.5 Least-squares restraints */
            } else if (command.equalsIgnoreCase("DFIX")) {
            } else if (command.equalsIgnoreCase("DANG")) {
            } else if (command.equalsIgnoreCase("BUMP")) {
            } else if (command.equalsIgnoreCase("SAME")) {
            } else if (command.equalsIgnoreCase("SADI")) {
            } else if (command.equalsIgnoreCase("CHIV")) {
            } else if (command.equalsIgnoreCase("FLAT")) {
            } else if (command.equalsIgnoreCase("DELU")) {
            } else if (command.equalsIgnoreCase("SIMU")) {
            } else if (command.equalsIgnoreCase("DEFS")) {
            } else if (command.equalsIgnoreCase("ISOR")) {
            } else if (command.equalsIgnoreCase("NCSY")) {
            } else if (command.equalsIgnoreCase("SUMP")) {

            /* 7.6 Least-squares organization */
            } else if (command.equalsIgnoreCase("L.S.")) {
            } else if (command.equalsIgnoreCase("CGLS")) {
            } else if (command.equalsIgnoreCase("BLOC")) {
            } else if (command.equalsIgnoreCase("DAMP")) {
            } else if (command.equalsIgnoreCase("STIR")) {
            } else if (command.equalsIgnoreCase("WGHT")) {
            } else if (command.equalsIgnoreCase("FVAR")) {

            /* 7.7 Lists and tables */
            } else if (command.equalsIgnoreCase("BOND")) {
            } else if (command.equalsIgnoreCase("CONF")) {
            } else if (command.equalsIgnoreCase("MPLA")) {
            } else if (command.equalsIgnoreCase("RTAB")) {
            } else if (command.equalsIgnoreCase("HTAB")) {
            } else if (command.equalsIgnoreCase("LIST")) {
            } else if (command.equalsIgnoreCase("ACTA")) {
            } else if (command.equalsIgnoreCase("SIZE")) {
            } else if (command.equalsIgnoreCase("TEMP")) {
            } else if (command.equalsIgnoreCase("WPDB")) {

            /* 7.8 Fouriers, peak search and lineprinter plots */
            } else if (command.equalsIgnoreCase("FMAP")) {
            } else if (command.equalsIgnoreCase("GRID")) {
            } else if (command.equalsIgnoreCase("PLAN")) {
            } else if (command.equalsIgnoreCase("MOLE")) {

            /* NOT DOCUMENTED BUT USED BY PLATON */
            } else if (command.equalsIgnoreCase("SPGR")) {
                // Line added by PLATON stating the spacegroup
                StringTokenizer st = new StringTokenizer(line);
                st.nextToken(); // String command_again
                String spacegroup = st.nextToken();
                crystal.setSpaceGroup(spacegroup);
           } else if (command.equalsIgnoreCase("    ")) {
                logger.debug("Disrgarding line assumed to be added by PLATON: " + line);

            /* All other is atom */
            } else {
                //logger.debug("Assumed to contain an atom: " + line);
                /* this line gives an atom, because all lines not starting with
                   a ShelX command is an atom (that sucks!) */
                StringTokenizer st = new StringTokenizer(line);
                String atype = st.nextToken();
                st.nextToken(); // String scatt_factor
                String sa = st.nextToken();
                String sb = st.nextToken();
                String sc = st.nextToken();
                // skip the rest

                if (Character.isDigit(atype.charAt(1))) {
                    // atom type has a one letter code
                    atype = atype.substring(0,1);
                } else {
                    StringBuffer sb2 = new StringBuffer();
                    sb2.append(atype.charAt(1));
                    atype = atype.substring(0,1) + sb2.toString().toLowerCase();
                }

                double[] frac = new double[3];
                frac[0] = FortranFormat.atof(sa); // fractional coordinates
                frac[1] = FortranFormat.atof(sb);
                frac[2] = FortranFormat.atof(sc);
                logger.debug("fa,fb,fc: " + frac[0] + ", " + frac[1] + ", " + frac[2]);

                if (atype.equalsIgnoreCase("Q")) {
                    // ingore atoms named Q
                } else {
                    logger.info("Adding atom: " + atype + ", " + frac[0]
                                                        + ", " + frac[1]
                                                        + ", " + frac[2]);
                    IAtom atom = crystal.getBuilder().newAtom(atype);
                    atom.setFractionalPoint3d(new Point3d(frac[0], frac[1], frac[2]));
                    crystal.addAtom(atom);
                    logger.debug("Atom added: ", atom);
                }
            }
            line = input.readLine();
        }
        return crystal;
    }
    
    @TestMethod("testClose")
    public void close() throws IOException {
        input.close();
    }
}
