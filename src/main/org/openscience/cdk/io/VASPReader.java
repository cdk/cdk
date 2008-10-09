/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2007  The Jmol Development Team (v. 1.1.2.2)
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
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
import java.util.StringTokenizer;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.ICrystal;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.VASPFormat;
import org.openscience.cdk.math.FortranFormat;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Read output files generated with the VASP software.
 *
 * @cdk.module extra
 * @cdk.svnrev  $Revision$
 *
 * @author  Fabian Dortu <Fabian.Dortu@wanadoo.be>
 */
@TestClass("org.openscience.cdk.io.VSPReaderTest")
public class VASPReader extends DefaultChemObjectReader {

    private LoggingTool logger = null;

    // This variable is used to parse the input file
    protected StringTokenizer st =  new StringTokenizer("", "");;
    protected String fieldVal;
    protected int repVal = 0;
    
    protected BufferedReader inputBuffer;
    
    // VASP VARIABLES
    int natom = 1;
    int ntype = 1;
    double acell[] = new double[3];
    double[][] rprim = new double[3][3];
    String info = "";
    String line;
    String[] anames; //size is ntype. Contains the names of the atoms
    int natom_type[]; //size is natom. Contain the atomic number
    String representation; // "Direct" only so far
    
    /**
     * Creates a new <code>VASPReader</code> instance.
     *
     * @param input a <code>Reader</code> value
     */
    public VASPReader(Reader input) {
        logger = new LoggingTool(this);
        if (input instanceof BufferedReader) {
            this.inputBuffer = (BufferedReader)input;
        } else {
            this.inputBuffer = new BufferedReader(input);
        }
    }

    public VASPReader(InputStream input) {
        this(new InputStreamReader(input));
    }
    
    public VASPReader() {
        this(new StringReader(""));
    }
    
    @TestMethod("testGetFormat")
    public IResourceFormat getFormat() {
        return VASPFormat.getInstance();
    }
    
    @TestMethod("testSetReader_Reader")
    public void setReader(Reader input) throws CDKException {
        if (input instanceof BufferedReader) {
            this.inputBuffer = (BufferedReader)input;
        } else {
            this.inputBuffer = new BufferedReader(input);
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
			if (IChemFile.class.equals(interfaces[i])) return true;
		}
		return false;
	}

    public IChemObject read(IChemObject object) throws CDKException {
        if (object instanceof IChemFile) {
            IChemFile cf = (IChemFile)object;
            try {
                cf = readChemFile(cf);
            } catch (IOException exception) {
                String error = "Input/Output error while reading from input: " +
                    exception.getMessage();
                logger.error(error);
                logger.debug(exception);
                throw new CDKException(error, exception);
            }
            return cf;
        } else {
            throw new CDKException("Only supported is reading of ChemFile.");
        }
    }
    
    private IChemFile readChemFile(IChemFile file) throws CDKException, IOException {
        IChemSequence seq = readChemSequence(file.getBuilder().newChemSequence());
        file.addChemSequence(seq);
        return file;
    }
    
    private IChemSequence readChemSequence(IChemSequence sequence) throws CDKException, IOException {
        IChemModel chemModel = sequence.getBuilder().newChemModel();
        ICrystal crystal = null;
        
        // Get the info line (first token of the first line)
        inputBuffer.mark(255);
        info = nextVASPToken(false);
        //logger.debug(info);
        inputBuffer.reset(); 
        
        // Get the number of different atom "NCLASS=X"
        inputBuffer.mark(255);
        nextVASPTokenFollowing("NCLASS");
        ntype = Integer.parseInt(fieldVal);
        //logger.debug("NCLASS= " + ntype);
        inputBuffer.reset(); 
        
        // Get the different atom names
        anames = new String[ntype];
        
        nextVASPTokenFollowing("ATOM");
        for(int i = 0; i < ntype; i++) {
            anames[i] = fieldVal;
            nextVASPToken(false);
        }
        
        // Get the number of atom of each type
        int[] natom_type = new int[ntype];     
        natom = 0;
        for(int i = 0; i < ntype; i++) {
            natom_type[i] = Integer.parseInt(fieldVal);
            nextVASPToken(false);
            natom = natom + natom_type[i];
        }
        
        // Get the representation type of the primitive vectors
        // only "Direct" is recognize now.
        representation = fieldVal;
        if(representation.equals("Direct")) {
            logger.info("Direct representation");
            // DO NOTHING
        } else {
            throw new CDKException("This VASP file is not supported. Please contact the Jmol developpers");
        }
        
        while(nextVASPToken(false) != null) {
            
            logger.debug("New crystal started...");
            
            crystal = sequence.getBuilder().newCrystal();
            chemModel = sequence.getBuilder().newChemModel();
            
            // Get acell
            for(int i=0; i<3; i++) {
                acell[i] = FortranFormat.atof(fieldVal); // all the same FIX?
            }
            
            // Get primitive vectors
            for(int i = 0; i < 3; i++) {
                for(int j = 0; j < 3; j++) {
                    nextVASPToken(false);
                    rprim[i][j] = FortranFormat.atof(fieldVal);
                }
            }
            
            // Get atomic position
            int[] atomType = new int[natom];
            double[][] xred = new double[natom][3];
            int atomIndex=0;
            
            for(int i = 0; i < ntype; i++) {
                for(int j = 0; j < natom_type[i] ; j++) {
                    try {
                        atomType[atomIndex] = IsotopeFactory.getInstance(sequence.getBuilder()).
                            getElement(anames[i]).getAtomicNumber();
                    } catch (Exception exception) {
                        throw new CDKException("Could not determine atomic number!", exception);
                    }
                    logger.debug("aname: " + anames[i]);
                    logger.debug("atomType: " + atomType[atomIndex]);
                    
                    nextVASPToken(false);
                    xred[atomIndex][0] = FortranFormat.atof(fieldVal);
                    nextVASPToken(false);
                    xred[atomIndex][1] = FortranFormat.atof(fieldVal);
                    nextVASPToken(false);
                    xred[atomIndex][2] = FortranFormat.atof(fieldVal);
                    
                    atomIndex = atomIndex+1;
                    // FIXME: store atom
                }
            }
            
            crystal.setA(new Vector3d(rprim[0][0]*acell[0],
                                      rprim[0][1]*acell[0],
                                      rprim[0][2]*acell[0]));
            crystal.setB(new Vector3d(rprim[1][0]*acell[1],
                                      rprim[1][1]*acell[1],
                                      rprim[1][2]*acell[1]));
            crystal.setC(new Vector3d(rprim[2][0]*acell[2],
                                      rprim[2][1]*acell[2],
                                      rprim[2][2]*acell[2]));
            for (int i=0; i<atomType.length; i++) {
                String symbol = "Du";
                try {
                    symbol = IsotopeFactory.getInstance(sequence.getBuilder()).
                        getElement(atomType[i]).getSymbol();
                } catch (Exception exception) {
                    throw new CDKException("Could not determine element symbol!", exception);
                }
                IAtom atom = sequence.getBuilder().newAtom(symbol);
                atom.setAtomicNumber(atomType[i]);
                // convert fractional to cartesian
                double[] frac = new double[3];
                frac[0] = xred[i][0];
                frac[1] = xred[i][1];
                frac[2] = xred[i][2];
                atom.setFractionalPoint3d(new Point3d(frac[0], frac[1], frac[2]));
                crystal.addAtom(atom);
            }
            crystal.setProperty(CDKConstants.REMARK, info);
            chemModel.setCrystal(crystal);
            
            logger.info("New Frame set!");
            
            sequence.addChemModel(chemModel);
            
        } //end while
        
        return sequence;
    }
    
    
    
    /**
    * Find the next token of an VASP file.
    * ABINIT tokens are words separated by space(s). Characters
    * following a "#" are ignored till the end of the line.
    *
    * @return a <code>String</code> value
    * @exception IOException if an error occurs
    */
    public String nextVASPToken(boolean newLine) throws IOException {
        
        String line;
        
        if (newLine) { // We ignore the end of the line and go to the following line
            if (inputBuffer.ready()) {
                line = inputBuffer.readLine();
                st = new StringTokenizer(line, " =\t");
            }
        }
        
        while (!st.hasMoreTokens() && inputBuffer.ready()) {
            line = inputBuffer.readLine();
            st = new StringTokenizer(line, " =\t");
        }
        if (st.hasMoreTokens()) {
            fieldVal = st.nextToken();
            if (fieldVal.startsWith("#")) {
                nextVASPToken(true);
            }
        } else {
            fieldVal = null;
        }
        return this.fieldVal;
    } //end nextVASPToken(boolean newLine)
    
    
    /**
     * Find the next token of a VASP file begining
     * with the *next* line.
     */
    public String nextVASPTokenFollowing(String string) throws IOException {
        int index;
        String line;
        while (inputBuffer.ready()) {
            line = inputBuffer.readLine();
            index = line.indexOf(string);
            if (index > 0) {
                index = index + string.length();
                line = line.substring(index);
                st = new StringTokenizer(line, " =\t");
                while(!st.hasMoreTokens() && inputBuffer.ready()) {
                    line = inputBuffer.readLine();
                    st = new StringTokenizer(line, " =\t");
                } 
                if (st.hasMoreTokens()) {
                    fieldVal = st.nextToken();
                } else {
                    fieldVal = null;
                }
                break;
            }
        }
        return fieldVal;
    } //end nextVASPTokenFollowing(String string) 
        
    @TestMethod("testClose")
    public void close() throws IOException {
        inputBuffer.close();
    }
}
