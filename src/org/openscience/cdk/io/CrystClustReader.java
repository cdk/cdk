/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2004  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307  USA.
 */
package org.openscience.cdk.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import javax.vecmath.Point3d;
import org.openscience.cdk.Atom;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.Crystal;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.CrystalGeometryTools;
import org.openscience.cdk.math.FortranFormat;
import org.openscience.cdk.tools.LoggingTool;

/**
 * @cdk.module extra
 */
public class CrystClustReader extends DefaultChemObjectReader {

    private BufferedReader input;
    private LoggingTool logger;

    public CrystClustReader() {
        logger = new LoggingTool(this);
    }

    public CrystClustReader(Reader input) {
        this();
        if (input instanceof BufferedReader) {
            this.input = (BufferedReader)input;
        } else {
            this.input = new BufferedReader(input);
        }
    }

    public String getFormatName() {
        return "CrystClust";
    }
    
    public void setReader(Reader reader) throws CDKException {
        if (input instanceof BufferedReader) {
            this.input = (BufferedReader)reader;
        } else {
            this.input = new BufferedReader(reader);
        }
    }

    public boolean matches(int lineNumber, String line) {
        if (lineNumber == 1 && line.startsWith("frame: ")) {
            return true;
        }
        return false;
    }

    public ChemObject read(ChemObject object) throws CDKException {
        if (object instanceof ChemFile) {
            ChemFile cf = readChemFile();
            return cf;
        } else {
            throw new CDKException("Only supported is reading of ChemFile.");
        }
    }

    private ChemFile readChemFile() throws CDKException {
        ChemFile file = new ChemFile();
        ChemSequence seq = new ChemSequence();
        ChemModel model = new ChemModel();
        Crystal crystal = null;
        
        int lineNumber = 0;
        int frames = 0;
        
        double[] a, b, c;
        
        try {
            String line = input.readLine();
            while (input.ready() && line != null) {
                logger.debug((lineNumber++) + ": ", line);
                if (line.startsWith("frame:")) {
                    logger.debug("found new frame");
                    frames++;
                    model = new ChemModel();
                    crystal = new Crystal();
                    
                    // assume the file format is correct
                    
                    logger.debug("reading spacegroup");
                    line = input.readLine();
                    logger.debug((lineNumber++) + ": ", line);
                    crystal.setSpaceGroup(line);
                    
                    logger.debug("reading unit cell axes");
                    double fractx, fracty, fractz;
                    logger.debug("parsing A: ");
                    line = input.readLine();
                    logger.debug((lineNumber++) + ": ", line);
                    fractx = FortranFormat.atof(line);
                    line = input.readLine();
                    logger.debug((lineNumber++) + ": ", line);
                    fracty = FortranFormat.atof(line);
                    line = input.readLine();
                    logger.debug((lineNumber++) + ": ", line);
                    fractz = FortranFormat.atof(line);
                    crystal.setA(fractx, fracty, fractz);
                    logger.debug("parsing B: ");
                    line = input.readLine();
                    logger.debug((lineNumber++) + ": ", line);
                    fractx = FortranFormat.atof(line);
                    line = input.readLine();
                    logger.debug((lineNumber++) + ": ", line);
                    fracty = FortranFormat.atof(line);
                    line = input.readLine();
                    logger.debug((lineNumber++) + ": ", line);
                    fractz = FortranFormat.atof(line);
                    crystal.setB(fractx, fracty, fractz);
                    logger.debug("parsing C: ");
                    line = input.readLine();
                    logger.debug((lineNumber++) + ": ", line);
                    fractx = FortranFormat.atof(line);
                    line = input.readLine();
                    logger.debug((lineNumber++) + ": ", line);
                    fracty = FortranFormat.atof(line);
                    line = input.readLine();
                    logger.debug((lineNumber++) + ": ", line);
                    fractz = FortranFormat.atof(line);
                    crystal.setC(fractx, fracty, fractz);
                    a = crystal.getA();
                    b = crystal.getB();
                    c = crystal.getC();
                    
                    logger.debug("Reading number of atoms");
                    line = input.readLine();
                    logger.debug((lineNumber++) + ": ", line);
                    int atomsToRead = Integer.parseInt(line);
                    
                    logger.debug("Reading no molecules in assym unit cell");
                    line = input.readLine();
                    logger.debug((lineNumber++) + ": ", line);
                    int Z = Integer.parseInt(line);
                    
                    String symbol;
                    double charge;
                    double[] cart;
                    for (int i=1; i<=atomsToRead; i++) {
                        cart = new double[3];
                        line = input.readLine();
                        logger.debug((lineNumber++) + ": ", line);
                        symbol = line.substring(0, line.indexOf(":"));
                        charge = Double.parseDouble(line.substring(line.indexOf(":")+1));
                        line = input.readLine();
                        logger.debug((lineNumber++) + ": ", line);
                        cart[0] = Double.parseDouble(line); // x
                        line = input.readLine();
                        logger.debug((lineNumber++) + ": ", line);
                        cart[1] = Double.parseDouble(line); // y
                        line = input.readLine();
                        logger.debug((lineNumber++) + ": ", line);
                        cart[2] = Double.parseDouble(line); // z
                        Atom atom = new Atom(symbol);
                        atom.setCharge(charge);
                        // convert cartesian coords to fractional
                        double[] frac = CrystalGeometryTools.cartesianToFractional(a, b, c, cart);
                        atom.setFractionalPoint3D(new Point3d(frac[0], frac[1], frac[2]));
                        crystal.addAtom(atom);
                        logger.debug("Added atom: ", atom);
                    }
                    
                    model.setCrystal(crystal);
                    seq.addChemModel(model);
                } else {
                    logger.debug("Format seems broken. Skipping these lines:");
                    while (!line.startsWith("frame:") && 
                    input.ready() && line != null) {
                        line = input.readLine();
                        logger.debug(lineNumber++ + ": ", line);
                    }
                    logger.debug("Ok, resynched: found new frame");
                }
            }
            file.addChemSequence(seq);
        } catch (Exception exception) {
            String message = "Error while parsing CrystClust file: " + exception.getMessage();
            logger.error(message);
            logger.debug(exception);
            throw new CDKException(message);
        }
        return file;
    }
    
    public void close() throws IOException {
        input.close();
    }
}
