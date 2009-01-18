/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2003  The Jmol Development Team
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.StringTokenizer;

import javax.vecmath.Point3d;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.formats.GhemicalMMFormat;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Reads Ghemical (<a href="http://www.uku.fi/~thassine/ghemical/">
 * http://www.uku.fi/~thassine/ghemical/</a>)
 * molecular mechanics (*.mm1gp) files.
 *
 * @cdk.module io
 * @cdk.svnrev  $Revision$
 *
 * @author Egon Willighagen <egonw@sci.kun.nl>
 */
@TestClass("org.openscience.cdk.io.GhemicalMMReaderTest")
public class GhemicalMMReader extends DefaultChemObjectReader {

    private LoggingTool logger = null;
    private BufferedReader input = null;

    public GhemicalMMReader(Reader input) {
        this.logger = new LoggingTool(this);
        this.input = new BufferedReader(input);
    }

    public GhemicalMMReader(InputStream input) {
        this(new InputStreamReader(input));
    }
    
    public GhemicalMMReader() {
        this(new StringReader(""));
    }

    @TestMethod("testGetFormat")
    public IResourceFormat getFormat() {
        return GhemicalMMFormat.getInstance();
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

    @TestMethod("testClose")
    public void close() {
    }
    
	@TestMethod("testAccepts")
    public boolean accepts(Class classObject) {
		Class[] interfaces = classObject.getInterfaces();
		for (int i=0; i<interfaces.length; i++) {
			if (IChemModel.class.equals(interfaces[i])) return true;
			if (IChemFile.class.equals(interfaces[i])) return true;
		}
    Class superClass = classObject.getSuperclass();
    if (superClass != null) return this.accepts(superClass);
		return false;
	}

    public IChemObject read(IChemObject object) throws CDKException {
        if (object instanceof IChemModel) {
            return (IChemObject) readChemModel((IChemModel)object);
        } else if (object instanceof IChemFile) {
        	IChemSequence sequence = object.getBuilder().newChemSequence();
        	sequence.addChemModel((IChemModel)this.read(object.getBuilder().newChemModel()));
        	((IChemFile)object).addChemSequence(sequence);
        	return object;
        } else {
            throw new CDKException("Only supported is ChemModel.");
        }
    }
    
    private IChemModel readChemModel(IChemModel model) throws CDKException {
        int[] atoms = new int[1];
        double[] atomxs = new double[1];
        double[] atomys = new double[1];
        double[] atomzs = new double[1];
        double[] atomcharges = new double[1];
        
        int[] bondatomid1 = new int[1];
        int[] bondatomid2 = new int[1];
        IBond.Order[] bondorder = new IBond.Order[1];
        
        int numberOfAtoms = 0;
        int numberOfBonds = 0;
        
        try {
            String line = input.readLine();
            while (line != null) {
                StringTokenizer st = new StringTokenizer(line);
                String command = st.nextToken();
                if ("!Header".equals(command)) {
                    logger.warn("Ignoring header");
                } else if ("!Info".equals(command)) {
                    logger.warn("Ignoring info");
                } else if ("!Atoms".equals(command)) {
                    logger.info("Reading atom block");
                    // determine number of atoms to read
                    try {
                        numberOfAtoms = Integer.parseInt(st.nextToken());
                        logger.debug("  #atoms: " + numberOfAtoms);
                        atoms = new int[numberOfAtoms];
                        atomxs = new double[numberOfAtoms];
                        atomys = new double[numberOfAtoms];
                        atomzs = new double[numberOfAtoms];
                        atomcharges = new double[numberOfAtoms];
                        
                        for (int i = 0; i < numberOfAtoms; i++) {
                            line = input.readLine();
                            StringTokenizer atomInfoFields = new StringTokenizer(line);
                            int atomID = Integer.parseInt(atomInfoFields.nextToken());
                            atoms[atomID] = Integer.parseInt(atomInfoFields.nextToken());
                            logger.debug("Set atomic number of atom (" + atomID + ") to: " + atoms[atomID]);
                        }
                    } catch (Exception exception) {
                        logger.error("Error while reading Atoms block");
                        logger.debug(exception);
                    }
                } else if ("!Bonds".equals(command)) {
                    logger.info("Reading bond block");
                    try {
                        // determine number of bonds to read
                        numberOfBonds = Integer.parseInt(st.nextToken());
                        bondatomid1 = new int[numberOfAtoms];
                        bondatomid2 = new int[numberOfAtoms];
                        bondorder = new IBond.Order[numberOfAtoms];
                        
                        for (int i = 0; i < numberOfBonds; i++) {
                            line = input.readLine();
                            StringTokenizer bondInfoFields = new StringTokenizer(line);
                            bondatomid1[i] = Integer.parseInt(bondInfoFields.nextToken());
                            bondatomid2[i] = Integer.parseInt(bondInfoFields.nextToken());
                            String order = bondInfoFields.nextToken();
                            if ("D".equals(order)) {
                                bondorder[i] = IBond.Order.DOUBLE;
                            } else if ("S".equals(order)) {
                                bondorder[i] = IBond.Order.SINGLE;
                            } else if ("T".equals(order)) {
                                bondorder[i] = IBond.Order.TRIPLE;
                            } else {
                                // ignore order, i.e. set to single
                                logger.warn("Unrecognized bond order, using single bond instead. Found: " + order);
                                bondorder[i] = IBond.Order.SINGLE;
                            }
                        }
                    } catch (Exception exception) {
                        logger.error("Error while reading Bonds block");
                        logger.debug(exception);
                    }
                } else if ("!Coord".equals(command)) {
                    logger.info("Reading coordinate block");
                    try {
                        for (int i = 0; i < numberOfAtoms; i++) {
                            line = input.readLine();
                            StringTokenizer atomInfoFields = new StringTokenizer(line);
                            int atomID = Integer.parseInt(atomInfoFields.nextToken());
                            double x = Double.valueOf(atomInfoFields.nextToken()).doubleValue();
                            double y = Double.valueOf(atomInfoFields.nextToken()).doubleValue();
                            double z = Double.valueOf(atomInfoFields.nextToken()).doubleValue();
                            atomxs[atomID] = x;
                            atomys[atomID] = y;
                            atomzs[atomID] = z;
                        }
                    } catch (Exception exception) {
                        logger.error("Error while reading Coord block");
                        logger.debug(exception);
                    }
                } else if ("!Charges".equals(command)) {
                    logger.info("Reading charges block");
                    try {
                        for (int i = 0; i < numberOfAtoms; i++) {
                            line = input.readLine();
                            StringTokenizer atomInfoFields = new StringTokenizer(line);
                            int atomID = Integer.parseInt(atomInfoFields.nextToken());
                            double charge = Double.valueOf(atomInfoFields.nextToken()).doubleValue();
                            atomcharges[atomID] = charge;
                        }
                    } catch (Exception exception) {
                        logger.error("Error while reading Charges block");
                        logger.debug(exception);
                    }
                } else if ("!End".equals(command)) {
                    logger.info("Found end of file");
                    // Store atoms
                    IAtomContainer container = model.getBuilder().newAtomContainer();
                    for (int i = 0; i < numberOfAtoms; i++) {
                        try {
                            IAtom atom = model.getBuilder().newAtom(IsotopeFactory.getInstance(container.getBuilder()).getElementSymbol(atoms[i]));
                            atom.setAtomicNumber(atoms[i]);
                            atom.setPoint3d(new Point3d(atomxs[i], atomys[i], atomzs[i]));
                            atom.setCharge(atomcharges[i]);
                            container.addAtom(atom);
                            logger.debug("Stored atom: " + atom);
                        } catch (Exception exception) {
                            logger.error("Cannot create an atom with atomic number: " + atoms[i]);
                            logger.debug(exception);
                        }
                    }
                    
                    // Store bonds
                    for (int i = 0; i < numberOfBonds; i++) {
                        container.addBond(bondatomid1[i], bondatomid2[i], bondorder[i]);
                    }
                    
                    IMoleculeSet moleculeSet = model.getBuilder().newMoleculeSet();
                    moleculeSet.addMolecule(model.getBuilder().newMolecule(container));
                    model.setMoleculeSet(moleculeSet);
                    
                    return model;
                } else {
                    logger.warn("Skipping line: " + line);
                }
                
                line = input.readLine();
            }
        } catch (Exception exception) {
            logger.error("Error while reading file");
            logger.debug(exception);
        }
        
        // this should not happen, file is lacking !End command
        return null;
        
    }
}
