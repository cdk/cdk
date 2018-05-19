/* Copyright (C) 1997-2007  Christoph Steinbeck <steinbeck@users.sourceforge.net>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.MDLFormat;
import org.openscience.cdk.io.setting.BooleanIOSetting;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Reads a molecule from the original MDL MOL or SDF file {@cdk.cite DAL92}. An SD files
 * is read into a {@link IChemSequence} of {@link IChemModel}'s. Each ChemModel will contain one
 * Molecule. If the MDL molfile contains a property block, the {@link MDLV2000Reader} should be
 * used.
 *
 * <p>If all z coordinates are 0.0, then the xy coordinates are taken as
 * 2D, otherwise the coordinates are read as 3D.
 *
 * <p>The title of the MOL file is read and can be retrieved with:
 * <pre>
 *   molecule.getProperty(CDKConstants.TITLE);
 * </pre>
 *
 * @cdk.module io
 * @cdk.githash
 * @cdk.iooptions
 *
 * @author     steinbeck
 * @author     Egon Willighagen
 * @cdk.created    2000-10-02
 * @cdk.keyword    file format, MDL molfile
 * @cdk.keyword    file format, SDF
 *
 * @see        org.openscience.cdk.io.MDLV2000Reader
 * @deprecated This reader is only for molfiles without a version tag, typically the most
 *             common molfile now encountered is V2000 and the {@link MDLV2000Reader} should be used
 *             instead. The V2000 reader can actually read files missing the version tag when
 *             in relaxed mode.
 */
@Deprecated
public class MDLReader extends DefaultChemObjectReader {

    BufferedReader               input          = null;
    private static ILoggingTool  logger         = LoggingToolFactory.createLoggingTool(MDLReader.class);

    private BooleanIOSetting     forceReadAs3DCoords;
    private static final Pattern TRAILING_SPACE = Pattern.compile("\\s+$");

    public MDLReader() {
        this(new StringReader(""));
    }

    /**
     *  Constructs a new MDLReader that can read Molecule from a given InputStream.
     *
     *@param  in  The InputStream to read from
     */
    public MDLReader(InputStream in) {
        this(in, Mode.RELAXED);
    }

    public MDLReader(InputStream in, Mode mode) {
        this(new InputStreamReader(in));
        super.mode = mode;
    }

    /**
     * Constructs a new MDLReader that can read Molecule from a given Reader.
     *
     * @param  in  The Reader to read from
     */
    public MDLReader(Reader in) {
        this(in, Mode.RELAXED);
    }

    public MDLReader(Reader in, Mode mode) {
        super.mode = mode;
        input = new BufferedReader(in);
        initIOSettings();
    }

    @Override
    public IResourceFormat getFormat() {
        return MDLFormat.getInstance();
    }

    @Override
    public void setReader(Reader input) throws CDKException {
        if (input instanceof BufferedReader) {
            this.input = (BufferedReader) input;
        } else {
            this.input = new BufferedReader(input);
        }
    }

    @Override
    public void setReader(InputStream input) throws CDKException {
        setReader(new InputStreamReader(input));
    }

    @Override
    public boolean accepts(Class<? extends IChemObject> classObject) {
        if (IChemFile.class.equals(classObject)) return true;
        if (IChemModel.class.equals(classObject)) return true;
        if (IAtomContainer.class.equals(classObject)) return true;
        Class<?>[] interfaces = classObject.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            if (IChemFile.class.equals(interfaces[i])) return true;
            if (IChemModel.class.equals(interfaces[i])) return true;
            if (IAtomContainer.class.equals(interfaces[i])) return true;
        }
        Class superClass = classObject.getSuperclass();
        if (superClass != null) return this.accepts(superClass);
        return false;
    }

    /**
     *  Takes an object which subclasses IChemObject, e.g. Molecule, and will read
     *  this (from file, database, internet etc). If the specific implementation
     *  does not support a specific IChemObject it will throw an Exception.
     *
     *@param  object                              The object that subclasses
     *      IChemObject
     *@return                                     The IChemObject read
     *@exception  CDKException
     */
    @Override
    public <T extends IChemObject> T read(T object) throws CDKException {
        if (object instanceof IChemFile) {
            return (T) readChemFile((IChemFile) object);
        } else if (object instanceof IChemModel) {
            return (T) readChemModel((IChemModel) object);
        } else if (object instanceof IAtomContainer) {
            return (T) readMolecule((IAtomContainer) object);
        } else {
            throw new CDKException("Only supported are ChemFile and Molecule.");
        }
    }

    private IChemModel readChemModel(IChemModel chemModel) throws CDKException {
        IAtomContainerSet setOfMolecules = chemModel.getMoleculeSet();
        if (setOfMolecules == null) {
            setOfMolecules = chemModel.getBuilder().newInstance(IAtomContainerSet.class);
        }
        IAtomContainer m = readMolecule(chemModel.getBuilder().newInstance(IAtomContainer.class));
        if (m != null) {
            setOfMolecules.addAtomContainer(m);
        }
        chemModel.setMoleculeSet(setOfMolecules);
        return chemModel;
    }

    /**
     * Read a ChemFile from a file in MDL SDF format.
     *
     * @return    The ChemFile that was read from the MDL file.
     */
    private IChemFile readChemFile(IChemFile chemFile) throws CDKException {
        IChemSequence chemSequence = chemFile.getBuilder().newInstance(IChemSequence.class);

        IChemModel chemModel = chemFile.getBuilder().newInstance(IChemModel.class);
        IAtomContainerSet setOfMolecules = chemFile.getBuilder().newInstance(IAtomContainerSet.class);
        IAtomContainer m = readMolecule(chemFile.getBuilder().newInstance(IAtomContainer.class));
        if (m != null) {
            setOfMolecules.addAtomContainer(m);
        }
        chemModel.setMoleculeSet(setOfMolecules);
        chemSequence.addChemModel(chemModel);

        setOfMolecules = chemFile.getBuilder().newInstance(IAtomContainerSet.class);
        chemModel = chemFile.getBuilder().newInstance(IChemModel.class);
        String str;
        try {
            String line;
            while ((line = input.readLine()) != null) {
                logger.debug("line: ", line);
                // apparently, this is a SDF file, continue with
                // reading mol files
                str = line;
                if (line.equals("M  END"))
                    continue;
                if (str.equals("$$$$")) {
                    m = readMolecule(chemFile.getBuilder().newInstance(IAtomContainer.class));

                    if (m != null) {
                        setOfMolecules.addAtomContainer(m);

                        chemModel.setMoleculeSet(setOfMolecules);
                        chemSequence.addChemModel(chemModel);

                        setOfMolecules = chemFile.getBuilder().newInstance(IAtomContainerSet.class);
                        chemModel = chemFile.getBuilder().newInstance(IChemModel.class);

                    }
                } else {
                    // here the stuff between 'M  END' and '$$$$'
                    if (m != null) {
                        // ok, the first lines should start with '>'
                        String fieldName = null;
                        if (str.startsWith("> ")) {
                            // ok, should extract the field name
                            str.substring(2); // String content =
                            int index = str.indexOf('<');
                            if (index != -1) {
                                int index2 = str.substring(index).indexOf('>');
                                if (index2 != -1) {
                                    fieldName = str.substring(index + 1, index + index2);
                                }
                            }
                            // end skip all other lines
                            while ((line = input.readLine()) != null && line.startsWith(">")) {
                                logger.debug("data header line: ", line);
                            }
                        }
                        if (line == null) {
                            throw new CDKException("Expecting data line here, but found null!");
                        }
                        String data = line;
                        while ((line = input.readLine()) != null && line.trim().length() > 0) {
                            if (line.equals("$$$$")) {
                                logger.error("Expecting data line here, but found end of molecule: ", line);
                                break;
                            }
                            logger.debug("data line: ", line);
                            data += line;
                            // preserve newlines, unless the line is exactly 80 chars; in that case it
                            // is assumed to continue on the next line. See MDL documentation.
                            if (line.length() < 80) data += "\n";
                        }
                        if (fieldName != null) {
                            logger.info("fieldName, data: ", fieldName, ", ", data);
                            m.setProperty(fieldName, data);
                        }
                    }
                }
            }
        } catch (CDKException cdkexc) {
            throw cdkexc;
        } catch (IOException | IllegalArgumentException exception) {
            String error = "Error while parsing SDF";
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
        return chemFile;
    }

    /**
     *  Read a Molecule from a file in MDL sd format
     *
     *@return    The Molecule that was read from the MDL file.
     */
    private IAtomContainer readMolecule(IAtomContainer molecule) throws CDKException {
        logger.debug("Reading new molecule");
        int linecount = 0;
        int atoms = 0;
        int bonds = 0;
        int atom1 = 0;
        int atom2 = 0;
        int order = 0;
        IBond.Stereo stereo = (IBond.Stereo) CDKConstants.UNSET;
        int RGroupCounter = 1;
        int Rnumber = 0;
        String[] rGroup = null;
        double x = 0.0;
        double y = 0.0;
        double z = 0.0;
        double totalX = 0.0;
        double totalY = 0.0;
        double totalZ = 0.0;
        //int[][] conMat = new int[0][0];
        //String help;
        IAtom atom;
        String line = "";

        try {
            IsotopeFactory isotopeFactory = Isotopes.getInstance();

            logger.info("Reading header");
            line = input.readLine();
            linecount++;
            if (line == null) {
                return null;
            }
            logger.debug("Line " + linecount + ": " + line);

            if (line.startsWith("$$$$")) {
                logger.debug("File is empty, returning empty molecule");
                return molecule;
            }
            if (line.length() > 0) {
                molecule.setTitle(line);
            }
            line = input.readLine();
            linecount++;
            logger.debug("Line " + linecount + ": " + line);
            line = input.readLine();
            linecount++;
            logger.debug("Line " + linecount + ": " + line);
            if (line.length() > 0) {
                molecule.setProperty(CDKConstants.REMARK, line);
            }

            logger.info("Reading rest of file");
            line = input.readLine();
            linecount++;
            logger.debug("Line " + linecount + ": " + line);
            if (mode == Mode.STRICT) {
                if (line.contains("V2000") || line.contains("v2000")) {
                    throw new CDKException("This file must be read with the MDLV2000Reader.");
                }
                if (line.contains("V3000") || line.contains("v3000")) {
                    throw new CDKException("This file must be read with the MDLV3000Reader.");
                }
            }
            atoms = Integer.valueOf(line.substring(0, 3).trim()).intValue();
            logger.debug("Atomcount: " + atoms);
            bonds = Integer.valueOf(line.substring(3, 6).trim()).intValue();
            logger.debug("Bondcount: " + bonds);

            // read ATOM block
            logger.info("Reading atom block");
            for (int f = 0; f < atoms; f++) {
                line = input.readLine();
                linecount++;
                Matcher trailingSpaceMatcher = TRAILING_SPACE.matcher(line);
                if (trailingSpaceMatcher.find()) {
                    handleError("Trailing space found", linecount, trailingSpaceMatcher.start(),
                            trailingSpaceMatcher.end());
                    line = trailingSpaceMatcher.replaceAll("");
                }
                x = new Double(line.substring(0, 10).trim()).doubleValue();
                y = new Double(line.substring(10, 20).trim()).doubleValue();
                z = new Double(line.substring(20, 30).trim()).doubleValue();
                // *all* values should be zero, not just the sum
                totalX += Math.abs(x);
                totalY += Math.abs(y);
                totalZ += Math.abs(z);
                logger.debug("Coordinates: " + x + "; " + y + "; " + z);
                String element = line.substring(31, Math.min(34, line.length())).trim();
                if (line.length() < 34) {
                    handleError("Element atom type does not follow V2000 format type should of length three"
                            + " and padded with space if required", linecount, 31, 34);
                }

                logger.debug("Atom type: ", element);
                if (isotopeFactory.isElement(element)) {
                    atom = isotopeFactory.configure(molecule.getBuilder().newInstance(IAtom.class, element));
                } else if ("A".equals(element)) {
                    atom = molecule.getBuilder().newInstance(IPseudoAtom.class, element);
                } else if ("Q".equals(element)) {
                    atom = molecule.getBuilder().newInstance(IPseudoAtom.class, element);
                } else if ("*".equals(element)) {
                    atom = molecule.getBuilder().newInstance(IPseudoAtom.class, element);
                } else if ("LP".equals(element)) {
                    atom = molecule.getBuilder().newInstance(IPseudoAtom.class, element);
                } else if ("L".equals(element)) {
                    atom = molecule.getBuilder().newInstance(IPseudoAtom.class, element);
                } else if (element.length() > 0 && element.charAt(0) == 'R') {
                    logger.debug("Atom ", element, " is not an regular element. Creating a PseudoAtom.");
                    //check if the element is R
                    rGroup = element.split("^R");
                    if (rGroup.length > 1) {
                        try {
                            Rnumber = Integer.parseInt(rGroup[(rGroup.length - 1)]);
                            RGroupCounter = Rnumber;
                        } catch (Exception ex) {
                            Rnumber = RGroupCounter;
                            RGroupCounter++;
                        }
                        element = "R" + Rnumber;
                    }
                    atom = molecule.getBuilder().newInstance(IPseudoAtom.class, element);
                } else {
                    if (mode == ISimpleChemObjectReader.Mode.STRICT) {
                        throw new CDKException(
                                "Invalid element type. Must be an existing element, or one in: A, Q, L, LP, *.");
                    }
                    atom = molecule.getBuilder().newInstance(IPseudoAtom.class, element);
                }

                // store as 3D for now, convert to 2D (if totalZ == 0.0) later
                atom.setPoint3d(new Point3d(x, y, z));

                // parse further fields
                if (line.length() >= 36) {
                    String massDiffString = line.substring(34, 36).trim();
                    logger.debug("Mass difference: ", massDiffString);
                    if (!(atom instanceof IPseudoAtom)) {
                        try {
                            int massDiff = Integer.parseInt(massDiffString);
                            if (massDiff != 0) {
                                IIsotope major = Isotopes.getInstance().getMajorIsotope(element);
                                atom.setAtomicNumber(major.getAtomicNumber() + massDiff);
                            }
                        } catch (NumberFormatException | IOException exception) {
                            logger.error("Could not parse mass difference field");
                        }
                    } else {
                        logger.error("Cannot set mass difference for a non-element!");
                    }
                } else {
                    handleError("Mass difference is missing", linecount, 34, 36);
                }

                if (line.length() >= 39) {
                    String chargeCodeString = line.substring(36, 39).trim();
                    logger.debug("Atom charge code: ", chargeCodeString);
                    int chargeCode = Integer.parseInt(chargeCodeString);
                    if (chargeCode == 0) {
                        // uncharged species
                    } else if (chargeCode == 1) {
                        atom.setFormalCharge(+3);
                    } else if (chargeCode == 2) {
                        atom.setFormalCharge(+2);
                    } else if (chargeCode == 3) {
                        atom.setFormalCharge(+1);
                    } else if (chargeCode == 4) {
                    } else if (chargeCode == 5) {
                        atom.setFormalCharge(-1);
                    } else if (chargeCode == 6) {
                        atom.setFormalCharge(-2);
                    } else if (chargeCode == 7) {
                        atom.setFormalCharge(-3);
                    }
                } else {
                    handleError("Atom charge count is empty", linecount, 35, 39);
                }

                if (line.length() >= 64) {
                    // read the mmm field as position 61-63
                    String reactionAtomIDString = line.substring(60, 63).trim();
                    logger.debug("Parsing mapping id: ", reactionAtomIDString);
                    try {
                        int reactionAtomID = Integer.parseInt(reactionAtomIDString);
                        if (reactionAtomID != 0) {
                            atom.setProperty(CDKConstants.ATOM_ATOM_MAPPING, reactionAtomID);
                        }
                    } catch (Exception exception) {
                        logger.error("Mapping number ", reactionAtomIDString, " is not an integer.");
                        logger.debug(exception);
                    }
                }

                //shk3: This reads shifts from after the molecule. I don't think this is an official format, but I saw it frequently 80=>78 for alk
                if (line.length() >= 78) {
                    double shift = Double.parseDouble(line.substring(69, 80).trim());
                    atom.setProperty("first shift", new Double(shift));
                }
                if (line.length() >= 87) {
                    double shift = Double.parseDouble(line.substring(79, 87).trim());
                    atom.setProperty("second shift", new Double(shift));
                }

                molecule.addAtom(atom);
            }

            // convert to 2D, if totalZ == 0
            if (totalX == 0.0 && totalY == 0.0 && totalZ == 0.0) {
                logger.info("All coordinates are 0.0");
                for (IAtom atomToUpdate : molecule.atoms()) {
                    atomToUpdate.setPoint3d(null);
                }
            } else if (totalZ == 0.0 && !forceReadAs3DCoords.isSet()) {
                logger.info("Total 3D Z is 0.0, interpreting it as a 2D structure");
                Iterator<IAtom> atomsToUpdate = molecule.atoms().iterator();
                while (atomsToUpdate.hasNext()) {
                    IAtom atomToUpdate = (IAtom) atomsToUpdate.next();
                    Point3d p3d = atomToUpdate.getPoint3d();
                    atomToUpdate.setPoint2d(new Point2d(p3d.x, p3d.y));
                    atomToUpdate.setPoint3d(null);
                }
            }

            // read BOND block
            logger.info("Reading bond block");
            for (int f = 0; f < bonds; f++) {
                line = input.readLine();
                linecount++;
                atom1 = java.lang.Integer.valueOf(line.substring(0, 3).trim()).intValue();
                atom2 = java.lang.Integer.valueOf(line.substring(3, 6).trim()).intValue();
                order = java.lang.Integer.valueOf(line.substring(6, 9).trim()).intValue();
                if (line.length() > 12) {
                    int mdlStereo = Integer.valueOf(line.substring(9, 12).trim());
                    if (mdlStereo == 1) {
                        // MDL up bond
                        stereo = IBond.Stereo.UP;
                    } else if (mdlStereo == 6) {
                        // MDL down bond
                        stereo = IBond.Stereo.DOWN;
                    } else if (mdlStereo == 0) {
                        // bond has no stereochemistry
                        stereo = IBond.Stereo.NONE;
                    } else if (mdlStereo == 4) {
                        //MDL up or down bond
                        stereo = IBond.Stereo.UP_OR_DOWN;
                    } else if (mdlStereo == 3) {
                        //MDL e or z undefined
                        stereo = IBond.Stereo.E_OR_Z;
                    }
                } else {
                    logger.warn("Missing expected stereo field at line: " + line);
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Bond: " + atom1 + " - " + atom2 + "; order " + order);
                }
                // interpret CTfile's special bond orders
                IAtom a1 = molecule.getAtom(atom1 - 1);
                IAtom a2 = molecule.getAtom(atom2 - 1);
                IBond newBond = null;
                if (order >= 1 && order <= 3) {
                    IBond.Order cdkOrder = IBond.Order.SINGLE;
                    if (order == 2) cdkOrder = IBond.Order.DOUBLE;
                    if (order == 3) cdkOrder = IBond.Order.TRIPLE;
                    if (stereo != null) {
                        newBond = molecule.getBuilder().newInstance(IBond.class, a1, a2, cdkOrder, stereo);
                    } else {
                        newBond = molecule.getBuilder().newInstance(IBond.class, a1, a2, cdkOrder);
                    }
                } else if (order == 4) {
                    // aromatic bond
                    if (stereo != null) {
                        newBond = molecule.getBuilder().newInstance(IBond.class, a1, a2, IBond.Order.SINGLE, stereo);
                    } else {
                        newBond = molecule.getBuilder().newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
                    }
                    // mark both atoms and the bond as aromatic
                    newBond.setFlag(CDKConstants.ISAROMATIC, true);
                    a1.setFlag(CDKConstants.ISAROMATIC, true);
                    a2.setFlag(CDKConstants.ISAROMATIC, true);
                }
                molecule.addBond(newBond);
            }

        } catch (IOException | CDKException | IllegalArgumentException exception) {
            String error = "Error while parsing line " + linecount + ": " + line + " -> " + exception.getMessage();
            logger.error(error);
            logger.debug(exception);
            throw new CDKException(error, exception);
        }
        return molecule;
    }

    @Override
    public void close() throws IOException {
        input.close();
    }

    private void initIOSettings() {
        forceReadAs3DCoords = addSetting(new BooleanIOSetting("ForceReadAs3DCoordinates", IOSetting.Importance.LOW,
                "Should coordinates always be read as 3D?", "false"));
    }

    public void customizeJob() {
        fireIOSettingQuestion(forceReadAs3DCoords);
    }

}
