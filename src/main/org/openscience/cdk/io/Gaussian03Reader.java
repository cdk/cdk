/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2002-2003  Bradley A. Smith <yeldar@home.com>
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
import java.io.StreamTokenizer;
import java.io.StringReader;

import javax.vecmath.Point3d;

import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.formats.Gaussian03Format;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.tools.LoggingTool;

/**
 * A reader for Gaussian03 output.
 * Gaussian 03 is a quantum chemistry program
 * by Gaussian, Inc. (<a href="http://www.gaussian.com/">http://www.gaussian.com/</a>).
 * 
 * <p>Molecular coordinates, energies, and normal coordinates of
 * vibrations are read. Each set of coordinates is added to the
 * ChemFile in the order they are found. Energies and vibrations
 * are associated with the previously read set of coordinates.
 * 
 * <p>This reader was developed from a small set of
 * example output files, and therefore, is not guaranteed to
 * properly read all Gaussian03 output. If you have problems,
 * please contact the author of this code, not the developers
 * of Gaussian03.
 * 
 * <p>This code was adaptated by Jonathan from Gaussian98Reader written by
 * Bradley, and ported to CDK by Egon.
 *
 * @author Jonathan C. Rienstra-Kiracofe <jrienst@emory.edu>
 * @author Bradley A. Smith <yeldar@home.com>
 * @author Egon Willighagen
 * @cdk.module io
 * @cdk.svnrev  $Revision$
 */
public class Gaussian03Reader extends DefaultChemObjectReader {

    private BufferedReader input;
    private LoggingTool logger;

    public Gaussian03Reader(Reader reader) {
        input = new BufferedReader(reader);
        logger = new LoggingTool(this);
    }

    public Gaussian03Reader(InputStream input) {
        this(new InputStreamReader(input));
    }

    public Gaussian03Reader() {
        this(new StringReader(""));
    }

    @TestMethod("testGetFormat")
    public IResourceFormat getFormat() {
        return Gaussian03Format.getInstance();
    }

    @TestMethod("testSetReader_Reader")
    public void setReader(Reader reader) throws CDKException {
        this.input = new BufferedReader(input);
    }

    @TestMethod("testSetReader_InputStream")
    public void setReader(InputStream input) throws CDKException {
        setReader(new InputStreamReader(input));
    }

    @TestMethod("testAccepts")
    public boolean accepts(Class classObject) {
        Class[] interfaces = classObject.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            if (IChemFile.class.equals(interfaces[i])) return true;
            if (IChemSequence.class.equals(interfaces[i])) return true;
        }
        return false;
    }

    public IChemObject read(IChemObject object) throws CDKException {
        if (object instanceof IChemSequence) {
            return readChemSequence((IChemSequence) object);
        } else if (object instanceof IChemFile) {
            return readChemFile((IChemFile) object);
        } else {
            throw new CDKException("Object " + object.getClass().getName() + " is not supported");
        }
    }

    @TestMethod("testClose")
    public void close() throws IOException {
        input.close();
    }

    private IChemFile readChemFile(IChemFile chemFile) throws CDKException {
        IChemSequence sequence = readChemSequence(chemFile.getBuilder().newChemSequence());
        chemFile.addChemSequence(sequence);
        return chemFile;
    }

    private IChemSequence readChemSequence(IChemSequence sequence) throws CDKException {
        IChemModel model = null;

        try {
            String line = input.readLine();
            //String levelOfTheory = null;

            // Find first set of coordinates
            while (input.ready() && (line != null)) {
                if (line.indexOf("Standard orientation:") >= 0) {

                    // Found a set of coordinates
                    model = sequence.getBuilder().newChemModel();
                    try {
                        readCoordinates(model);
                    } catch (IOException exception) {
                        throw new CDKException("Error while reading coordinates: " + exception.toString(), exception);
                    }
                    break;
                }
                line = input.readLine();
            }
            if (model != null) {
                // Read all other data
                line = input.readLine();
                while (input.ready() && (line != null)) {
                    if (line.indexOf("Standard orientation:") >= 0) {
                        // Found a set of coordinates
                        // Add current frame to file and create a new one.
                        sequence.addChemModel(model);
                        fireFrameRead();
                        model = sequence.getBuilder().newChemModel();
                        readCoordinates(model);
                    } else if (line.indexOf("SCF Done:") >= 0) {
                        // Found an energy
                        model.setProperty("org.openscience.cdk.io.Gaussian03Reaer:SCF Done", line.trim());
                    } else if (line.indexOf("Harmonic frequencies") >= 0) {
                        // Found a set of vibrations
//                        try {
//                            readFrequencies(model);
//                        } catch (IOException exception) {
//                            throw new CDKException("Error while reading frequencies: " + exception.toString(), exception);
//                        }
                    } else if (line.indexOf("Mulliken atomic charges") >= 0) {
                        readPartialCharges(model);
                    } else if (line.indexOf("Magnetic shielding") >= 0) {
                        // Found NMR data
//                        try {
//                            readNMRData(model, line);
//                        } catch (IOException exception) {
//                            throw new CDKException("Error while reading NMR data: " + exception.toString(), exception);
//                        }
                    } else if (line.indexOf("GINC") >= 0) {
                        // Found calculation level of theory
                        //levelOfTheory = parseLevelOfTheory(line);
                        // FIXME: is doing anything with it?
                    }
                    line = input.readLine();
                }

                // Add current frame to file
                sequence.addChemModel(model);
                fireFrameRead();
            }
        } catch (IOException exception) {
            throw new CDKException("Error while reading general structure: " + exception.toString(), exception);
        }
        return sequence;
    }

    /**
     * Reads a set of coordinates into ChemModel.
     *
     * @param model the destination ChemModel
     * @throws IOException if an I/O error occurs
     */
    private void readCoordinates(IChemModel model) throws CDKException, IOException {
        IAtomContainer container = model.getBuilder().newAtomContainer();
        String line = input.readLine();
        line = input.readLine();
        line = input.readLine();
        line = input.readLine();
        while (input.ready()) {
            line = input.readLine();
            if ((line == null) || (line.indexOf("-----") >= 0)) {
                break;
            }
            int atomicNumber = 0;
            StringReader sr = new StringReader(line);
            StreamTokenizer token = new StreamTokenizer(sr);
            token.nextToken();

            // ignore first token
            if (token.nextToken() == StreamTokenizer.TT_NUMBER) {
                atomicNumber = (int) token.nval;
                if (atomicNumber == 0) {

                    // Skip dummy atoms. Dummy atoms must be skipped
                    // if frequencies are to be read because Gaussian
                    // does not report dummy atoms in frequencies, and
                    // the number of atoms is used for reading frequencies.
                    continue;
                }
            } else {
                throw new IOException("Error reading coordinates");
            }
            token.nextToken();

            // ignore third token
            double x = 0.0;
            double y = 0.0;
            double z = 0.0;
            if (token.nextToken() == StreamTokenizer.TT_NUMBER) {
                x = token.nval;
            } else {
                throw new IOException("Error reading coordinates");
            }
            if (token.nextToken() == StreamTokenizer.TT_NUMBER) {
                y = token.nval;
            } else {
                throw new IOException("Error reading coordinates");
            }
            if (token.nextToken() == StreamTokenizer.TT_NUMBER) {
                z = token.nval;
            } else {
                throw new IOException("Error reading coordinates");
            }
            String symbol = "Du";
            try {
                symbol = IsotopeFactory.getInstance(model.getBuilder()).getElementSymbol(atomicNumber);
            } catch (Exception exception) {
                throw new CDKException("Could not determine element symbol!", exception);
            }
            IAtom atom = model.getBuilder().newAtom(symbol);
            atom.setPoint3d(new Point3d(x, y, z));
            container.addAtom(atom);
        }
        IMoleculeSet moleculeSet = model.getBuilder().newMoleculeSet();
        moleculeSet.addMolecule(model.getBuilder().newMolecule(container));
        model.setMoleculeSet(moleculeSet);
    }

    /**
     * Reads partial atomic charges and add the to the given ChemModel.
     */
    private void readPartialCharges(IChemModel model) throws CDKException, IOException {
        logger.info("Reading partial atomic charges");
        IMoleculeSet moleculeSet = model.getMoleculeSet();
        IMolecule molecule = moleculeSet.getMolecule(0);
        String line = input.readLine(); // skip first line after "Total atomic charges"
        while (input.ready()) {
            line = input.readLine();
            logger.debug("Read charge block line: " + line);
            if ((line == null) || (line.indexOf("Sum of Mulliken charges") >= 0)) {
                logger.debug("End of charge block found");
                break;
            }
            StringReader sr = new StringReader(line);
            StreamTokenizer tokenizer = new StreamTokenizer(sr);
            if (tokenizer.nextToken() == StreamTokenizer.TT_NUMBER) {
                int atomCounter = (int) tokenizer.nval;

                tokenizer.nextToken(); // ignore the symbol

                double charge = 0.0;
                if (tokenizer.nextToken() == StreamTokenizer.TT_NUMBER) {
                    charge = (double) tokenizer.nval;
                    logger.debug("Found charge for atom " + atomCounter +
                            ": " + charge);
                } else {
                    throw new CDKException("Error while reading charge: expected double.");
                }
                IAtom atom = molecule.getAtom(atomCounter - 1);
                atom.setCharge(charge);
            }
        }
    }

    /**
     * Reads a set of vibrations into ChemModel.
     *
     * @param model the destination ChemModel
     * @throws IOException if an I/O error occurs
     */
//    private void readFrequencies(IChemModel model) throws IOException {
        /* This is yet to be ported. Vibrations don't exist yet in CDK.
        String line = input.readLine();
        line = input.readLine();
        line = input.readLine();
        line = input.readLine();
        line = input.readLine();
        while ((line != null) && line.startsWith(" Frequencies --")) {
            Vector currentVibs = new Vector();
            StringReader vibValRead = new StringReader(line.substring(15));
            StreamTokenizer token = new StreamTokenizer(vibValRead);
            while (token.nextToken() != StreamTokenizer.TT_EOF) {
                Vibration vib = new Vibration(Double.toString(token.nval));
                currentVibs.addElement(vib);
            }
            line = input.readLine(); // skip "Red. masses"
            line = input.readLine(); // skip "Rfc consts"
            line = input.readLine(); // skip "IR Inten"
            while (!line.startsWith(" Atom AN")) {
                // skip all lines upto and including the " Atom AN" line
                line = input.readLine(); // skip
            }
            for (int i = 0; i < frame.getAtomCount(); ++i) {
                line = input.readLine();
                StringReader vectorRead = new StringReader(line);
                token = new StreamTokenizer(vectorRead);
                token.nextToken();
                
                // ignore first token
                token.nextToken();
                
                // ignore second token
                for (int j = 0; j < currentVibs.size(); ++j) {
                    double[] v = new double[3];
                    if (token.nextToken() == StreamTokenizer.TT_NUMBER) {
                        v[0] = token.nval;
                    } else {
                        throw new IOException("Error reading frequency");
                    }
                    if (token.nextToken() == StreamTokenizer.TT_NUMBER) {
                        v[1] = token.nval;
                    } else {
                        throw new IOException("Error reading frequency");
                    }
                    if (token.nextToken() == StreamTokenizer.TT_NUMBER) {
                        v[2] = token.nval;
                    } else {
                        throw new IOException("Error reading frequency");
                    }
                    ((Vibration) currentVibs.elementAt(j)).addAtomVector(v);
                }
            }
            for (int i = 0; i < currentVibs.size(); ++i) {
                frame.addVibration((Vibration) currentVibs.elementAt(i));
            }
            line = input.readLine();
            line = input.readLine();
            line = input.readLine();
        } */
//    }

    /**
     * Reads NMR nuclear shieldings.
     */
//    private void readNMRData(IChemModel model, String labelLine) throws IOException {
        /* FIXME: this is yet to be ported. CDK does not have shielding stuff.
        // Determine label for properties
        String label;
        if (labelLine.indexOf("Diamagnetic") >= 0) {
            label = "Diamagnetic Magnetic shielding (Isotropic)";
        } else if (labelLine.indexOf("Paramagnetic") >= 0) {
            label = "Paramagnetic Magnetic shielding (Isotropic)";
        } else {
            label = "Magnetic shielding (Isotropic)";
        }
        int atomIndex = 0;
        for (int i = 0; i < frame.getAtomCount(); ++i) {
            String line = input.readLine().trim();
            while (line.indexOf("Isotropic") < 0) {
                if (line == null) {
                    return;
                }
                line = input.readLine().trim();
            }
            StringTokenizer st1 = new StringTokenizer(line);
            
            // Find Isotropic label
            while (st1.hasMoreTokens()) {
                if (st1.nextToken().equals("Isotropic")) {
                    break;
                }
            }
            
            // Find Isotropic value
            while (st1.hasMoreTokens()) {
                if (st1.nextToken().equals("=")) {
                    break;
                }
            }
            double shielding = Double.valueOf(st1.nextToken()).doubleValue();
            NMRShielding ns1 = new NMRShielding(label, shielding);
            ((org.openscience.jmol.Atom)frame.getAtomAt(atomIndex)).addProperty(ns1);
            ++atomIndex;
        } */
//    }

    /**
     * Select the theory and basis set from the first archive line.
     */
    /*private String parseLevelOfTheory(String line) {
        
        StringTokenizer st1 = new StringTokenizer(line, "\\");
        
        // Must contain at least 6 tokens
        if (st1.countTokens() < 6) {
            return null;
        }
        
        // Skip first four tokens
        for (int i = 0; i < 4; ++i) {
            st1.nextToken();
        }
        return st1.nextToken() + "/" + st1.nextToken();
    }*/

}
