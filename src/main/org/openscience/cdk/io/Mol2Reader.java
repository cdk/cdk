/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2003-2007  Egon Willighagen <egonw@users.sf.net>
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
 */
package org.openscience.cdk.io;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.Mol2Format;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;
import org.openscience.cdk.tools.periodictable.PeriodicTable;

import javax.vecmath.Point3d;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.StringTokenizer;

/**
 * Reads a molecule from an Mol2 file, such as written by Sybyl.
 * See the specs <a href="http://www.tripos.com/data/support/mol2.pdf">here</a>.
 *
 * @author Egon Willighagen
 * @cdk.module io
 * @cdk.githash
 * @cdk.created 2003-08-21
 * @cdk.keyword file format, Mol2
 */
@TestClass("org.openscience.cdk.io.Mol2ReaderTest")
public class Mol2Reader extends DefaultChemObjectReader {

    boolean firstLineisMolecule = false;

    BufferedReader input = null;
    private static ILoggingTool logger =
            LoggingToolFactory.createLoggingTool(Mol2Reader.class);

    /**
     * Constructs a new MDLReader that can read Molecule from a given Reader.
     *
     * @param in The Reader to read from
     */
    public Mol2Reader(Reader in) {
        input = new BufferedReader(in);
    }

    public Mol2Reader(InputStream input) {
        this(new InputStreamReader(input));
    }

    public Mol2Reader() {
        this(new StringReader(""));
    }

    @TestMethod("testGetFormat")
    public IResourceFormat getFormat() {
        return Mol2Format.getInstance();
    }

    @TestMethod("testSetReader_Reader")
    public void setReader(Reader input) throws CDKException {
        if (input instanceof BufferedReader) {
            this.input = (BufferedReader) input;
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
        for (Class anInterface : interfaces) {
            if (IChemModel.class.equals(anInterface)) return true;
            if (IChemFile.class.equals(anInterface)) return true;
            if (IMolecule.class.equals(anInterface)) return true;
        }
        Class superClass = classObject.getSuperclass();
        return superClass != null && this.accepts(superClass);
    }

    public <T extends IChemObject> T read(T object) throws CDKException {
        if (object instanceof IChemFile) {
            return (T) readChemFile((IChemFile) object);
        } else if (object instanceof IChemModel) {
            return (T) readChemModel((IChemModel) object);
        } else if (object instanceof IMolecule) {
            return (T) readMolecule((IMolecule) object);
        } else {
            throw new CDKException("Only supported are ChemFile and Molecule.");
        }
    }

    private IChemModel readChemModel(IChemModel chemModel) throws CDKException {
        IMoleculeSet setOfMolecules = chemModel.getMoleculeSet();
        if (setOfMolecules == null) {
            setOfMolecules = chemModel.getBuilder().newInstance(IMoleculeSet.class);
        }
        IMolecule m = readMolecule(chemModel.getBuilder().newInstance(IMolecule.class));
        if (m != null) {
            setOfMolecules.addMolecule(m);
        }
        chemModel.setMoleculeSet(setOfMolecules);
        return chemModel;
    }

    private IChemFile readChemFile(IChemFile chemFile) throws CDKException {
        IChemSequence chemSequence = chemFile.getBuilder().newInstance(IChemSequence.class);

        IChemModel chemModel = chemFile.getBuilder().newInstance(IChemModel.class);
        IMoleculeSet setOfMolecules = chemFile.getBuilder().newInstance(IMoleculeSet.class);
        IMolecule m = readMolecule(chemFile.getBuilder().newInstance(IMolecule.class));
        if (m != null) setOfMolecules.addMolecule(m);
        chemModel.setMoleculeSet(setOfMolecules);
        chemSequence.addChemModel(chemModel);
        setOfMolecules = chemFile.getBuilder().newInstance(IMoleculeSet.class);
        chemModel = chemFile.getBuilder().newInstance(IChemModel.class);
        try {
            firstLineisMolecule = true;
            while (m != null) {
                m = readMolecule(chemFile.getBuilder().newInstance(IMolecule.class));
                if (m != null) {
                    setOfMolecules.addMolecule(m);
                    chemModel.setMoleculeSet(setOfMolecules);
                    chemSequence.addChemModel(chemModel);
                    setOfMolecules = chemFile.getBuilder().newInstance(IMoleculeSet.class);
                    chemModel = chemFile.getBuilder().newInstance(IChemModel.class);
                }
            }
        } catch (CDKException cdkexc) {
            throw cdkexc;
        } catch (Exception exception) {
            String error = "Error while parsing MOL2";
            logger.error(error);
            logger.debug(exception);
            throw new CDKException(error, exception);
        }
        try {
            input.close();
        } catch (Exception exc) {
            String error = "Error while closing file: " + exc.getMessage();
            logger.error(error);
            throw new CDKException(error, exc);
        }

        chemFile.addChemSequence(chemSequence);

        // reset it to false so that other read methods called later do not get confused
        firstLineisMolecule = false;
        
        return chemFile;
    }


    @TestMethod("testAccepts")
    public boolean accepts(IChemObject object) {
        if (object instanceof IChemFile) {
            return true;
        } else if (object instanceof IChemModel) {
            return true;
        } else if (object instanceof IMolecule) {
            return true;
        }
        return false;
    }


    /**
     * Read a Reaction from a file in MDL RXN format
     *
     * @return The Reaction that was read from the MDL file.
     */
    private IMolecule readMolecule(IMolecule molecule) throws CDKException {
        AtomTypeFactory atFactory = null;
        try {
            atFactory = AtomTypeFactory.getInstance(
                    "org/openscience/cdk/config/data/mol2_atomtypes.xml", molecule.getBuilder()
            );
        } catch (Exception exception) {
            String error = "Could not instantiate an AtomTypeFactory";
            logger.error(error);
            logger.debug(exception);
            throw new CDKException(error, exception);
        }
        try {
            int atomCount = 0;
            int bondCount = 0;

            String line;
            while (true) {
                line = input.readLine();
                if (line == null) return null;
                if (line.startsWith("@<TRIPOS>MOLECULE")) break;
                if (!line.startsWith("#") && line.trim().length() > 0) break;
            }

            // ok, if we're coming from the chemfile functoion, we've alreay read the molecule RTI
            if (firstLineisMolecule) molecule.setProperty(CDKConstants.TITLE, line);
            else {
                line = input.readLine();
                molecule.setProperty(CDKConstants.TITLE, line);
            }

            // get atom and bond counts
            String counts = input.readLine();
            StringTokenizer tokenizer = new StringTokenizer(counts);
            try {
                atomCount = Integer.parseInt(tokenizer.nextToken());
            } catch (NumberFormatException nfExc) {
                String error = "Error while reading atom count from MOLECULE block";
                logger.error(error);
                logger.debug(nfExc);
                throw new CDKException(error, nfExc);
            }
            if (tokenizer.hasMoreTokens()) {
                try {
                    bondCount = Integer.parseInt(tokenizer.nextToken());
                } catch (NumberFormatException nfExc) {
                    String error = "Error while reading atom and bond counts";
                    logger.error(error);
                    logger.debug(nfExc);
                    throw new CDKException(error, nfExc);
                }
            } else {
                bondCount = 0;
            }
            logger.info("Reading #atoms: ", atomCount);
            logger.info("Reading #bonds: ", bondCount);

            // we skip mol type, charge type and status bit lines
            logger.warn("Not reading molecule qualifiers");

            line = input.readLine();
            boolean molend = false;
            while (line != null) {
                if (line.startsWith("@<TRIPOS>MOLECULE")) {
                    molend = true;
                    break;
                } else if (line.startsWith("@<TRIPOS>ATOM")) {
                    logger.info("Reading atom block");
                    for (int i = 0; i < atomCount; i++) {
                        line = input.readLine().trim();
                        if (line.startsWith("@<TRIPOS>MOLECULE")) {
                            molend = true;
                            break;
                        }
                        tokenizer = new StringTokenizer(line);
                        tokenizer.nextToken(); // disregard the id token
                        String nameStr = tokenizer.nextToken();
                        String xStr = tokenizer.nextToken();
                        String yStr = tokenizer.nextToken();
                        String zStr = tokenizer.nextToken();
                        String atomTypeStr = tokenizer.nextToken();

                        // fix OpenBabel atom type codes to SYBYL specification
                        // this addresses wrong types that were output by OB (but is now fixed)
                        // see https://sourceforge.net/tracker/index.php?func=detail&aid=1650239&group_id=40728&atid=428740
                        if ("S.o2".equals(atomTypeStr)) atomTypeStr = "S.O2";
                        if ("S.o".equals(atomTypeStr)) atomTypeStr = "S.O";

                        IAtom atom = molecule.getBuilder().newInstance(IAtom.class, "X");
                        IAtomType atomType;
                        try {
                            atomType = atFactory.getAtomType(atomTypeStr);
                        } catch (Exception exception) {
                            // ok, *not* an mol2 atom type
                            atomType = null;
                        }
                        // Maybe it is just an element
                        if (atomType == null && isElementSymbol(atomTypeStr)) {
                            atom.setSymbol(atomTypeStr);
                        } else {
                            if (atomType == null) {
                                atomType = atFactory.getAtomType("X");
                                logger.error("Could not find specified atom type: ", atomTypeStr);
                            }
                            AtomTypeManipulator.configure(atom, atomType);
                        }

                        atom.setID(nameStr);
                        atom.setAtomTypeName(atomTypeStr);
                        try {
                            double x = Double.parseDouble(xStr);
                            double y = Double.parseDouble(yStr);
                            double z = Double.parseDouble(zStr);
                            atom.setPoint3d(new Point3d(x, y, z));
                        } catch (NumberFormatException nfExc) {
                            String error = "Error while reading atom coordinates";
                            logger.error(error);
                            logger.debug(nfExc);
                            throw new CDKException(error, nfExc);
                        }
                        molecule.addAtom(atom);
                    }
                } else if (line.startsWith("@<TRIPOS>BOND")) {
                    logger.info("Reading bond block");
                    for (int i = 0; i < bondCount; i++) {
                        line = input.readLine();
                        if (line.startsWith("@<TRIPOS>MOLECULE")) {
                            molend = true;
                            break;
                        }
                        tokenizer = new StringTokenizer(line);
                        tokenizer.nextToken(); // disregard the id token
                        String atom1Str = tokenizer.nextToken();
                        String atom2Str = tokenizer.nextToken();
                        String orderStr = tokenizer.nextToken();
                        try {
                            int atom1 = Integer.parseInt(atom1Str);
                            int atom2 = Integer.parseInt(atom2Str);
                            if ("nc".equals(orderStr)) {
                                // do not connect the atoms
                            } else {
                                IBond bond = molecule.getBuilder().newInstance(IBond.class,
                                        molecule.getAtom(atom1 - 1),
                                        molecule.getAtom(atom2 - 1)
                                );
                                if ("1".equals(orderStr)) {
                                    bond.setOrder(CDKConstants.BONDORDER_SINGLE);
                                } else if ("2".equals(orderStr)) {
                                    bond.setOrder(CDKConstants.BONDORDER_DOUBLE);
                                } else if ("3".equals(orderStr)) {
                                    bond.setOrder(CDKConstants.BONDORDER_TRIPLE);
                                } else if ("am".equals(orderStr) || "ar".equals(orderStr)) {
                                    bond.setOrder(CDKConstants.BONDORDER_SINGLE);
                                    bond.setFlag(CDKConstants.ISAROMATIC, true);
                                    bond.getAtom(0).setFlag(CDKConstants.ISAROMATIC, true);
                                    bond.getAtom(1).setFlag(CDKConstants.ISAROMATIC, true);
                                } else if ("du".equals(orderStr)) {
                                    bond.setOrder(CDKConstants.BONDORDER_SINGLE);
                                } else if ("un".equals(orderStr)) {
                                    bond.setOrder(CDKConstants.BONDORDER_SINGLE);
                                }
                                molecule.addBond(bond);
                            }
                        } catch (NumberFormatException nfExc) {
                            String error = "Error while reading bond information";
                            logger.error(error);
                            logger.debug(nfExc);
                            throw new CDKException(error, nfExc);
                        }
                    }
                }
                if (molend) return molecule;
                line = input.readLine();
            }
        } catch (IOException exception) {
            String error = "Error while reading general structure";
            logger.error(error);
            logger.debug(exception);
            throw new CDKException(error, exception);
        }
        return molecule;
    }

    private boolean isElementSymbol(String atomTypeStr) {
        for (int i = 1; i < PeriodicTable.getElementCount(); i++) {
            if (PeriodicTable.getSymbol(i).equals(atomTypeStr)) return true;
        }
        return false;
    }

    @TestMethod("testClose")
    public void close() throws IOException {
        input.close();
    }
}

