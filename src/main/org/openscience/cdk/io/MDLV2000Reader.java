/* Copyright (C) 1997-2007  Christoph Steinbeck <steinbeck@users.sourceforge.net>
 *                    2010  Egon Willighagen <egonw@users.sourceforge.net>
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

import com.google.common.collect.ImmutableSet;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.MDLV2000Format;
import org.openscience.cdk.io.setting.BooleanIOSetting;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.isomorphism.matchers.CTFileQueryBond;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.stereo.StereoElementFactory;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.periodictable.PeriodicTable;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads content from MDL molfiles and SD files. It can read a {@link
 * IAtomContainer} or {@link IChemModel} from an MDL molfile, and a {@link
 * IChemFile} from a SD file, with a {@link IChemSequence} of {@link
 * IChemModel}'s, where each IChemModel will contain one IMolecule.
 *
 * <p>From the Atom block it reads atomic coordinates, element types and formal
 * charges. From the Bond block it reads the bonds and the orders. Additionally,
 * it reads 'M  CHG', 'G  ', 'M  RAD' and 'M  ISO' lines from the property
 * block.
 *
 * <p>If all z coordinates are 0.0, then the xy coordinates are taken as 2D,
 * otherwise the coordinates are read as 3D.
 *
 * <p>The title of the MOL file is read and can be retrieved with:
 * <pre>
 *   molecule.getProperty(CDKConstants.TITLE);
 * </pre>
 *
 * <p>RGroups which are saved in the MDL molfile as R#, are renamed according to
 * their appearance, e.g. the first R# is named R1. With PseudAtom.getLabel()
 * "R1" is returned (instead of R#). This is introduced due to the SAR table
 * generation procedure of Scitegics PipelinePilot.
 *
 * @author steinbeck
 * @author Egon Willighagen
 * @cdk.module io
 * @cdk.githash
 * @cdk.iooptions
 * @cdk.created 2000-10-02
 * @cdk.keyword file format, MDL molfile
 * @cdk.keyword file format, SDF
 * @cdk.bug 1587283
 */
@TestClass("org.openscience.cdk.io.MDLV2000ReaderTest")
public class MDLV2000Reader extends DefaultChemObjectReader {

    BufferedReader input = null;
    private static ILoggingTool logger =
            LoggingToolFactory.createLoggingTool(MDLV2000Reader.class);

    private BooleanIOSetting forceReadAs3DCoords;
    private BooleanIOSetting interpretHydrogenIsotopes;
    private BooleanIOSetting addStereoElements;

    // Pattern to remove trailing space (String.trim() will remove leading space, which we don't want)
    private static final Pattern TRAILING_SPACE = Pattern.compile("\\s+$");

    /** Delimits Structure-Data (SD) Files. */
    private static final String RECORD_DELIMITER = "$$$$";

    /** Valid pseudo labels. */
    private static final Set<String> PSUEDO_LABELS = ImmutableSet.<String>builder()
                                                                 .add("*")
                                                                 .add("A")
                                                                 .add("Q")
                                                                 .add("L")
                                                                 .add("LP")
                                                                 .add("R")  // XXX: not in spec
                                                                 .add("R#")
                                                                 .build();

    public MDLV2000Reader() {
        this(new StringReader(""));
    }

    /**
     * Constructs a new MDLReader that can read Molecule from a given
     * InputStream.
     *
     * @param in The InputStream to read from
     */
    public MDLV2000Reader(InputStream in) {
        this(new InputStreamReader(in));
    }

    public MDLV2000Reader(InputStream in, Mode mode) {
        this(new InputStreamReader(in), mode);
    }

    /**
     * Constructs a new MDLReader that can read Molecule from a given Reader.
     *
     * @param in The Reader to read from
     */
    public MDLV2000Reader(Reader in) {
        this(in, Mode.RELAXED);
    }

    public MDLV2000Reader(Reader in, Mode mode) {
        input = new BufferedReader(in);
        initIOSettings();
        super.mode = mode;
    }

    @TestMethod("testGetFormat")
    public IResourceFormat getFormat() {
        return MDLV2000Format.getInstance();
    }

    @TestMethod("testSetReader_Reader")
    public void setReader(Reader input) throws CDKException {
        if (input instanceof BufferedReader) {
            this.input = (BufferedReader) input;
        }
        else {
            this.input = new BufferedReader(input);
        }
    }

    @TestMethod("testSetReader_InputStream")
    public void setReader(InputStream input) throws CDKException {
        setReader(new InputStreamReader(input));
    }

    @TestMethod("testAccepts")
    @SuppressWarnings("unchecked")
    public boolean accepts(Class<? extends IChemObject> classObject) {
        Class<?>[] interfaces = classObject.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            if (IChemFile.class.equals(anInterface)) return true;
            if (IChemModel.class.equals(anInterface)) return true;
            if (IAtomContainer.class.equals(anInterface)) return true;
        }
        if (IAtomContainer.class.equals(classObject)) return true;
        if (IChemFile.class.equals(classObject)) return true;
        if (IChemModel.class.equals(classObject)) return true;
        Class superClass = classObject.getSuperclass();
        return superClass != null && this.accepts(superClass);
    }

    /**
     * Takes an object which subclasses IChemObject, e.g. Molecule, and will
     * read this (from file, database, internet etc). If the specific
     * implementation does not support a specific IChemObject it will throw an
     * Exception.
     *
     * @param object The object that subclasses IChemObject
     * @return The IChemObject read
     * @throws CDKException
     */
    @SuppressWarnings("unchecked")
    public <T extends IChemObject> T read(T object) throws CDKException {
        if (object instanceof IChemFile) {
            return (T) readChemFile((IChemFile) object);
        }
        else if (object instanceof IChemModel) {
            return (T) readChemModel((IChemModel) object);
        }
        else if (object instanceof IAtomContainer) {
            return (T) readAtomContainer((IAtomContainer) object);
        }
        else {
            throw new CDKException("Only supported are ChemFile and Molecule.");
        }
    }

    private IChemModel readChemModel(IChemModel chemModel) throws CDKException {
        IAtomContainerSet setOfMolecules = chemModel.getMoleculeSet();
        if (setOfMolecules == null) {
            setOfMolecules = chemModel.getBuilder().newInstance(IAtomContainerSet.class);
        }
        IAtomContainer m = readAtomContainer(chemModel.getBuilder().newInstance(IAtomContainer.class));
        if (m != null) {
            setOfMolecules.addAtomContainer(m);
        }
        chemModel.setMoleculeSet(setOfMolecules);
        return chemModel;
    }

    /**
     * Read a ChemFile from a file in MDL SDF format.
     *
     * @return The ChemFile that was read from the MDL file.
     */
    private IChemFile readChemFile(IChemFile chemFile) throws CDKException {
        
        IChemObjectBuilder builder  = chemFile.getBuilder();
        IChemSequence      sequence = builder.newInstance(IChemSequence.class);

        try {
            IAtomContainer m;
            while ((m = readAtomContainer(builder.newInstance(IAtomContainer.class))) != null) {
                sequence.addChemModel(newModel(m));
            }
        } catch (CDKException e) {
            throw e;
        } catch (Exception exception) {
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

        chemFile.addChemSequence(sequence);
        return chemFile;
    }

    /**
     * Create a new chem model for a single {@link IAtomContainer}.
     * 
     * @param container the container to create the model for
     * @return a new {@link IChemModel}
     */
    private static IChemModel newModel(final IAtomContainer container) {
        
        if (container == null)
            throw new NullPointerException("cannot create chem model for a null container");
        
        final IChemObjectBuilder builder    = container.getBuilder();
        final IChemModel         model      = builder.newInstance(IChemModel.class);
        final IAtomContainerSet  containers = builder.newInstance(IAtomContainerSet.class);
        
        containers.addAtomContainer(container);
        model.setMoleculeSet(containers);
        
        return model;
    }

    /**
     * Read an IAtomContainer from a file in MDL sd format
     *
     * @return The Molecule that was read from the MDL file.
     */
    private IAtomContainer readAtomContainer(IAtomContainer molecule) throws CDKException {

        // flags for determining stereo config
        boolean has2D = false, has3D = true;

        logger.debug("Reading new molecule");
        IAtomContainer outputContainer = null;
        int linecount = 0;
        int nAtoms;
        int nBonds;
        int atom1;
        int atom2;
        int order;
        IBond.Stereo stereo = (IBond.Stereo) CDKConstants.UNSET;
        String title = null;
        String remark = null;
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
                title = line;
            }
            line = input.readLine();
            linecount++;
            logger.debug("Line " + linecount + ": " + line);
            line = input.readLine();
            linecount++;
            logger.debug("Line " + linecount + ": " + line);
            if (line.length() > 0) {
                remark = line;
            }

            logger.info("Reading rest of file");
            line = input.readLine();
            linecount++;
            logger.debug("Line " + linecount + ": " + line);

            // if the line is empty we hav a problem - either a malformed
            // molecule entry or just extra new lines at the end of the file
            if (line.length() == 0) {
                // read till the next $$$$ or EOF
                while (true) {
                    line = input.readLine();
                    linecount++;
                    if (line == null) {
                        return null;
                    }
                    if (line.startsWith("$$$$")) {
                        return molecule; // an empty molecule
                    }
                }
            }

            // check the CT block version
            if (line.contains("V3000") || line.contains("v3000")) {
                handleError("This file must be read with the MDLV3000Reader.");
            }
            else if (!line.contains("V2000") && !line.contains("v2000")) {
                handleError("This file must be read with the MDLReader.");
            }

            nAtoms = Integer.parseInt(line.substring(0, 3).trim());
            
            logger.debug("Atomcount: " + nAtoms);
            nBonds = Integer.parseInt(line.substring(3, 6).trim());
            logger.debug("Bondcount: " + nBonds);

            final IAtom[] atoms = new IAtom[nAtoms];
            final IBond[] bonds = new IBond[nBonds];

            // used for applying the MDL valence model
            int[] explicitValence = new int[nAtoms];

            // read ATOM block
            logger.info("Reading atom block");

            boolean hasX = false, hasY = false, hasZ = false;
            
            for (int i = 0; i < nAtoms; i++) {
                line = input.readLine();
                linecount++;
                
                final IAtom atom = readAtomSlow(line, molecule.getBuilder(), linecount);
                
                atoms[i] = atom;

                Point3d p = atom.getPoint3d();
                hasX = hasX || p.x != 0d;
                hasY = hasY || p.y != 0d;
                hasZ = hasZ || p.z != 0d;
            }
            
            // convert to 2D, if totalZ == 0
            if (!hasX && !hasY && !hasZ) {
                has3D = false;
                logger.info("All coordinates are 0.0");
                if (nAtoms == 1) {
                    atoms[0].setPoint2d(new Point2d(0, 0));
                }
                else {
                    for (IAtom atomToUpdate : atoms) {
                        atomToUpdate.setPoint3d(null);
                    }
                }
            }
            else if (!hasZ) {

                if (!forceReadAs3DCoords.isSet()) {
                    logger.info("Total 3D Z is 0.0, interpreting it as a 2D structure");
                    for (IAtom atomToUpdate : atoms) {
                        Point3d p3d = atomToUpdate.getPoint3d();
                        if (p3d != null) {
                            atomToUpdate.setPoint2d(new Point2d(p3d.x, p3d.y));
                            atomToUpdate.setPoint3d(null);
                        }
                    }
                    has2D = true;
                    has3D = false;
                }
                else {
                    // we do have 2D but they're stored as 3D and so not very
                    // useful for stereo perception (E/Z would still be okay)
                    has2D = false;
                    has3D = false;
                }
            }

            // read BOND block
            logger.info("Reading bond block");
            int queryBondCount = 0;
            for (int i = 0; i < nBonds; i++) {
                line = input.readLine();
                linecount++;
                
                IBond newBond = readBondSlow(line, molecule.getBuilder(), atoms, linecount);
                
                bonds[i] = newBond;
                
                // FIXME: set explicit valence in readBond method
                atom1 = -1; atom2 = -1;
                for (int idx = 0; idx < atoms.length; idx++) {
                    if (atom1 >= 0 && atom2 >= 0)
                        break;
                    if (atom1 < 0 && atoms[idx] == newBond.getAtom(0)) {
                        atom1 = idx + 1;
                    }
                    if (atom2 < 0 && atoms[idx] == newBond.getAtom(1)) {
                        atom2 = idx + 1;
                    }
                }

                // add the bond order to the explicit valence for each atom
                if (newBond.getOrder() != null && newBond.getOrder() != IBond.Order.UNSET) {
                    explicitValence[atom1 - 1] += newBond.getOrder().numeric();
                    explicitValence[atom2 - 1] += newBond.getOrder().numeric();
                }
                else {
                    if (!newBond.getFlag(CDKConstants.ISAROMATIC))
                        queryBondCount++;
                    explicitValence[atom1 - 1] = Integer.MIN_VALUE;
                    explicitValence[atom2 - 1] = Integer.MIN_VALUE;
                }
            }

            if (queryBondCount == 0)
                outputContainer = molecule;
            else {
                outputContainer = new QueryAtomContainer(molecule.getBuilder());
            }

            outputContainer.setProperty(CDKConstants.TITLE, title);
            outputContainer.setProperty(CDKConstants.REMARK, remark);
            outputContainer.setAtoms(atoms);
            outputContainer.setBonds(bonds);

            // read PROPERTY block
            logger.info("Reading property block");
            while (true) {
                line = input.readLine();
                linecount++;
                if (line == null) {
                    handleError(
                            "The expected property block is missing!",
                            linecount, 0, 0
                               );
                }
                if (line.startsWith("M  END")) break;

                boolean lineRead = false;
                if (line.startsWith("M  CHG")) {
                    // FIXME: if this is encountered for the first time, all
                    // atom charges should be set to zero first!
                    int infoCount = Integer.parseInt(line.substring(6, 9).trim());
                    StringTokenizer st = new StringTokenizer(line.substring(9));
                    for (int i = 1; i <= infoCount; i++) {
                        String token = st.nextToken();
                        int atomNumber = Integer.parseInt(token.trim());
                        token = st.nextToken();
                        int charge = Integer.parseInt(token.trim());
                        outputContainer.getAtom(atomNumber - 1).setFormalCharge(charge);
                    }
                }
                else if (line.matches("A\\s{1,4}\\d+")) {
                    // Reads the pseudo atom property from the mol file

                    // The atom number of the to replaced atom
                    int aliasAtomNumber = Integer.parseInt(line.replaceFirst("A\\s{1,4}", ""));
                    line = input.readLine();
                    linecount++;
                    String[] aliasArray = line.split("\\\\");
                    // name of the alias atom like R1 or R2 etc. 
                    String alias = "";
                    for (String anAliasArray : aliasArray) {
                        alias += anAliasArray;
                    }
                    IAtom aliasAtom = outputContainer.getAtom(aliasAtomNumber - 1);

                    // skip if already a pseudoatom
                    if (aliasAtom instanceof IPseudoAtom) {
                        ((IPseudoAtom) aliasAtom).setLabel(alias);
                        continue;
                    }

                    IAtom newPseudoAtom = molecule.getBuilder().newInstance(IPseudoAtom.class, alias);
                    if (aliasAtom.getPoint2d() != null)
                        newPseudoAtom.setPoint2d(aliasAtom.getPoint2d());
                    if (aliasAtom.getPoint3d() != null)
                        newPseudoAtom.setPoint3d(aliasAtom.getPoint3d());
                    AtomContainerManipulator.replaceAtomByAtom(outputContainer, aliasAtom, newPseudoAtom);
                }
                else if (line.startsWith("M  ISO")) {
                    try {
                        String countString = line.substring(6, 10).trim();
                        int infoCount = Integer.parseInt(countString);
                        StringTokenizer st = new StringTokenizer(line.substring(10));
                        for (int i = 1; i <= infoCount; i++) {
                            int atomNumber = Integer.parseInt(st.nextToken().trim());
                            int absMass = Integer.parseInt(st.nextToken().trim());
                            if (absMass != 0) {
                                IAtom isotope = outputContainer.getAtom(atomNumber - 1);
                                isotope.setMassNumber(absMass);
                            }
                        }
                    } catch (NumberFormatException exception) {
                        String error = "Error (" + exception.getMessage() + ") while parsing line "
                                + linecount + ": " + line + " in property block.";
                        logger.error(error);
                        handleError(
                                "NumberFormatException in isotope information.",
                                linecount, 7, 11,
                                exception
                                   );
                    }
                }
                else if (line.startsWith("M  RAD")) {
                    try {
                        String countString = line.substring(6, 9).trim();
                        int infoCount = Integer.parseInt(countString);
                        StringTokenizer st = new StringTokenizer(line.substring(9));
                        for (int i = 1; i <= infoCount; i++) {
                            int atomNumber = Integer.parseInt(st.nextToken().trim());
                            int spinMultiplicity = Integer.parseInt(st.nextToken().trim());
                            MDLV2000Writer.SPIN_MULTIPLICITY spin = MDLV2000Writer.SPIN_MULTIPLICITY.NONE;
                            if (spinMultiplicity > 0) {
                                IAtom radical = outputContainer.getAtom(atomNumber - 1);
                                switch (spinMultiplicity) {
                                    case 1:
                                        spin = MDLV2000Writer.SPIN_MULTIPLICITY.DOUBLET;
                                        break;
                                    case 2:
                                        spin = MDLV2000Writer.SPIN_MULTIPLICITY.SINGLET;
                                        break;
                                    case 3:
                                        spin = MDLV2000Writer.SPIN_MULTIPLICITY.TRIPLET;
                                        break;
                                    default:
                                        logger.debug("Invalid spin multiplicity found: " + spinMultiplicity);
                                        break;
                                }
                                for (int j = 0; j < spin.getSingleElectrons(); j++) {
                                    outputContainer.addSingleElectron(
                                            molecule.getBuilder().newInstance(ISingleElectron.class, radical));
                                }
                            }
                        }
                    } catch (NumberFormatException exception) {
                        String error = "Error (" + exception.getMessage() + ") while parsing line "
                                + linecount + ": " + line + " in property block.";
                        logger.error(error);
                        handleError(
                                "NumberFormatException in radical information",
                                linecount, 7, 10,
                                exception
                                   );
                    }
                }
                else if (line.startsWith("G  ")) {
                    try {
                        String atomNumberString = line.substring(3, 6).trim();
                        int atomNumber = Integer.parseInt(atomNumberString);
                        //String whatIsThisString = line.substring(6,9).trim();

                        String atomName = input.readLine();

                        // convert Atom into a PseudoAtom
                        IAtom prevAtom = outputContainer.getAtom(atomNumber - 1);
                        IPseudoAtom pseudoAtom = molecule.getBuilder().newInstance(IPseudoAtom.class, atomName);
                        if (prevAtom.getPoint2d() != null) {
                            pseudoAtom.setPoint2d(prevAtom.getPoint2d());
                        }
                        if (prevAtom.getPoint3d() != null) {
                            pseudoAtom.setPoint3d(prevAtom.getPoint3d());
                        }
                        AtomContainerManipulator.replaceAtomByAtom(molecule, prevAtom, pseudoAtom);
                    } catch (NumberFormatException exception) {
                        String error = "Error (" + exception.toString() + ") while parsing line "
                                + linecount + ": " + line + " in property block.";
                        logger.error(error);
                        handleError(
                                "NumberFormatException in group information",
                                linecount, 4, 7,
                                exception
                                   );
                    }
                }
                else if (line.startsWith("M  RGP")) {
                    StringTokenizer st = new StringTokenizer(line);
                    //Ignore first 3 tokens (overhead).
                    st.nextToken();
                    st.nextToken();
                    st.nextToken();
                    //Process the R group numbers as defined in RGP line.
                    while (st.hasMoreTokens()) {
                        Integer position = new Integer(st.nextToken());
                        int rNumber = new Integer(st.nextToken());
                        // the container may have already had atoms before the new atoms were read
                        int index   = outputContainer.getAtomCount() - nAtoms + position - 1;
                        IPseudoAtom pseudoAtom = (IPseudoAtom) outputContainer.getAtom(index);
                        if (pseudoAtom != null) {
                            pseudoAtom.setLabel("R" + rNumber);
                        }
                    }
                }
                if (line.startsWith("V  ")) {
                    Integer atomNumber = new Integer(line.substring(3, 6).trim());
                    IAtom atomWithComment = outputContainer.getAtom(atomNumber - 1);
                    atomWithComment.setProperty(CDKConstants.COMMENT, line.substring(7));
                }

                if (!lineRead) {
                    logger.warn("Skipping line in property block: ", line);
                }
            }

            // read potential SD file data between M  END and $$$$
            readNonStructuralData(input, outputContainer);

            if (interpretHydrogenIsotopes.isSet()) {
                fixHydrogenIsotopes(molecule, isotopeFactory);
            }

            // note: apply the valence model last so that all fixes (i.e. hydrogen
            // isotopes) are in place we need to use a offset as this atoms
            // could be added to a molecule which already had atoms present
            int offset = outputContainer.getAtomCount() - nAtoms;
            for (int i = offset; i < outputContainer.getAtomCount(); i++) {
                applyMDLValenceModel(outputContainer.getAtom(i), explicitValence[i - offset]);
            }

        } catch (CDKException exception) {
            String error = "Error while parsing line " + linecount + ": " + line + " -> " + exception.getMessage();
            logger.error(error);
            logger.debug(exception);
            throw exception;
        } catch (Exception exception) {
            exception.printStackTrace();
            String error = "Error while parsing line " + linecount + ": " + line + " -> " + exception.getMessage();
            logger.error(error);
            logger.debug(exception);
            handleError(
                    "Error while parsing line: " + line,
                    linecount, 0, 0,
                    exception
                       );
        }

        // TODO: query bonds and atoms with unset values leave nulls hanging 
        // TODO: around. making these classes not have unset, atomic number,
        // TODO: hydrogen count and bond order would allow us to automatically
        // TODO: add stereo.

        // sanity check that we have a decent molecule, query bonds mean we 
        // don't have a hydrogen count for atoms
        for (IAtom a : outputContainer.atoms())
            if (a.getImplicitHydrogenCount() == null)
                return outputContainer;

        if (addStereoElements.isSet() && has2D) {
            outputContainer.setStereoElements(StereoElementFactory.using2DCoordinates(outputContainer)
                                                                  .createAll());
        }
        else if (addStereoElements.isSet() && has3D) {
            outputContainer.setStereoElements(StereoElementFactory.using3DCoordinates(outputContainer)
                                                                  .createAll());
        }

        return outputContainer;
    }

    /**
     * Applies the MDL valence model to atoms using the explicit valence (bond
     * order sum) and charge to determine the correct number of implicit
     * hydrogens. The model is not applied if the explicit valence is less than
     * 0 - this is the case when a query bond was read for an atom.
     *
     * @param atom            the atom to apply the model to
     * @param explicitValence the explicit valence (bond order sum)
     */
    private void applyMDLValenceModel(IAtom atom, int explicitValence) {

        if (explicitValence < 0)
            return;

        if (atom.getValency() != null) {
            atom.setImplicitHydrogenCount(atom.getValency() - explicitValence);
        }
        else {
            Integer element = atom.getAtomicNumber();
            if (element == null)
                return;

            Integer charge = atom.getFormalCharge();
            if (charge == null)
                charge = 0;

            int implicitValence = MDLValence.implicitValence(element, charge, explicitValence);
            atom.setValency(implicitValence);
            atom.setImplicitHydrogenCount(implicitValence - explicitValence);
        }
    }

    private void fixHydrogenIsotopes(IAtomContainer molecule, IsotopeFactory isotopeFactory) {
        for (IAtom atom : AtomContainerManipulator.getAtomArray(molecule)) {
            if (atom instanceof IPseudoAtom) {
                IPseudoAtom pseudo = (IPseudoAtom) atom;
                if ("D".equals(pseudo.getLabel())) {
                    IAtom newAtom = molecule.getBuilder().newInstance(IAtom.class, atom);
                    newAtom.setSymbol("H");
                    newAtom.setAtomicNumber(1);
                    isotopeFactory.configure(newAtom, isotopeFactory.getIsotope("H", 2));
                    AtomContainerManipulator.replaceAtomByAtom(molecule, atom, newAtom);
                }
                else if ("T".equals(pseudo.getLabel())) {
                    IAtom newAtom = molecule.getBuilder().newInstance(IAtom.class, atom);
                    newAtom.setSymbol("H");
                    newAtom.setAtomicNumber(1);
                    isotopeFactory.configure(newAtom, isotopeFactory.getIsotope("H", 3));
                    AtomContainerManipulator.replaceAtomByAtom(molecule, atom, newAtom);
                }
            }
        }
    }

    @TestMethod("testClose")
    public void close() throws IOException {
        input.close();
    }

    private void initIOSettings() {
        forceReadAs3DCoords = addSetting(new BooleanIOSetting("ForceReadAs3DCoordinates", IOSetting.Importance.LOW,
                                                              "Should coordinates always be read as 3D?",
                                                              "false"));
        interpretHydrogenIsotopes = addSetting(new BooleanIOSetting("InterpretHydrogenIsotopes", IOSetting.Importance.LOW,
                                                                    "Should D and T be interpreted as hydrogen isotopes?",
                                                                    "true"));
        addStereoElements = addSetting(new BooleanIOSetting("AddStereoElements", IOSetting.Importance.LOW,
                                                            "Assign stereo configurations to stereocenters utilising 2D/3D coordinates.",
                                                            "true"));
    }

    public void customizeJob() {
        for (IOSetting setting : getSettings()) {
            fireIOSettingQuestion(setting);
        }
    }

    private String removeNonDigits(String input) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char inputChar = input.charAt(i);
            if (Character.isDigit(inputChar))
                sb.append(inputChar);
        }
        return sb.toString();
    }

    /**
     * Parse an atom line from the atom block using the format: {@code
     * xxxxx.xxxxyyyyy.yyyyzzzzz.zzzz aaaddcccssshhhbbbvvvHHHrrriiimmmnnneee}
     * where: <ul> <li>x: x coordinate</li> <li>y: y coordinate</li> <li>z: z
     * coordinate</li> <li>a: atom symbol</li> <li>d: mass difference</li>
     * <li>c: charge</li> <li>s: stereo parity</li> <li>h: hydrogen count + 1
     * (not read - query)</li> <li>b: stereo care (not read - query)</li> <li>v:
     * valence</li> <li>H: H0 designator (not read - query)</li> <li>r: not
     * used</li> <li>i: not used</li> <li>m: atom reaction mapping</li> <li>n:
     * inversion/retention flag</li> <li>e: exact change flag</li> </ul>
     *
     * The parsing is strict and does not allow extra columns (i.e. NMR shifts)
     * malformed input.
     *
     * @param line    input line
     * @param builder chem object builder to create the atom
     * @param lineNum the line number - for printing error messages 
     * @return a new atom instance
     */
    IAtom readAtomFast(String             line,
                       IChemObjectBuilder builder,
                       int                lineNum) throws CDKException, IOException {

        // The line may be truncated and it's checked in reverse at the specified
        // lengths:
        //          1         2         3         4         5         6
        // 123456789012345678901234567890123456789012345678901234567890123456789
        //                                  | |  |  |  |  |  |  |  |  |  |  |  | 
        // xxxxx.xxxxyyyyy.yyyyzzzzz.zzzz aaaddcccssshhhbbbvvvHHHrrriiimmmnnneee

        String symbol;
        double x, y, z;
        int massDiff = 0, charge = 0, parity = 0, valence = 0, mapping = 0;
        
        int length = length(line);
        if (length > 69) // excess data we should check all fields 
            length = 69;
        
        // given the length we jump to the position and parse all fields
        // that could be present (note - fall through switch)
        switch (length) {
            case 69: // eee: exact charge flag [reaction, query]  
            case 66: // nnn: inversion / retention [reaction]
            case 63: // mmm: atom-atom mapping [reaction]
                mapping = readUInt(line, 60, 3);
            case 60: // iii: not used
            case 57: // rrr: not used
            case 54: // HHH: H0 designation [redundant] 
            case 51: // vvv: valence
                valence = readUInt(line, 49, 2);
            case 48: // bbb: stereo care [query]       
            case 45: // hhh: hydrogen count + 1 [query] 
            case 42: // sss: stereo parity
                parity = toInt(line.charAt(41));
            case 39: // ccc: charge
                charge = toCharge(line.charAt(38));
            case 36: // dd: mass difference
                massDiff = sign(line.charAt(34)) * toInt(line.charAt(35));
            case 34: // x y z and aaa: atom coordinates and symbol 
            case 33: // symbol is left aligned  
            case 32:   
                x      = readMDLCoordinate(line, 0);
                y      = readMDLCoordinate(line, 10);
                z      = readMDLCoordinate(line, 20);
                symbol = line.substring(31, 34).trim().intern();
                break;
             default:
                 throw new CDKException("invalid line length, " + length + ": " + line);
        }

        IAtom atom = createAtom(symbol, builder);

        atom.setPoint3d(new Point3d(x, y, z));
        atom.setFormalCharge(charge);
        atom.setStereoParity(parity);

        // if there was a mass difference, set the mass number
        if (massDiff != 0 && atom.getAtomicNumber() > 0)
            atom.setMassNumber(Isotopes.getInstance()
                                       .getMajorIsotope(atom.getAtomicNumber())
                                       .getMassNumber()
                                       + massDiff);
        
        if (valence > 0 && valence < 16)
            atom.setValency(valence == 15 ? 0 : valence);

        if (mapping != 0)
            atom.setProperty(CDKConstants.ATOM_ATOM_MAPPING, mapping);

        return atom;
    }

    /**
     * Determine the length of the line excluding trailing whitespace.
     * 
     * @param str a string
     * @return the length when trailing white space is removed
     */
    static int length(final String str) {
        int i = str.length() - 1;
        while (i >= 0 && str.charAt(i) == ' ') {
            i--;
        }
        return i + 1;
    }

    /**
     * Create an atom for the provided symbol. If the atom symbol is a periodic
     * element a new 'Atom' is created otherwise if the symbol is an allowed
     * query atom ('R', 'Q', 'A', '*', 'L', 'LP') a new 'PseudoAtom' is created.
     * If the symbol is invalid an exception is thrown.
     *
     * @param symbol  input symbol
     * @param builder chem object builder
     * @return a new atom
     * @throws CDKException the symbol is not allowed
     */
    private IAtom createAtom(String symbol,
                             IChemObjectBuilder builder) throws CDKException {
        if (isPeriodicElement(symbol))
            return builder.newInstance(IAtom.class, symbol);
        
        // when strict only accept labels from the specification
        if (mode == Mode.STRICT && !isPseudoElement(symbol))
            throw new CDKException("invald symbol: " + symbol);        
        
        // will be renumbered later by RGP if R1, R2 etc. if not renumbered then
        // 'R' is a better label than 'R#' if now RGP is specified
        if (symbol.equals("R#"))
            symbol = "R";
        
        IAtom atom = builder.newInstance(IPseudoAtom.class, symbol);
        atom.setSymbol(symbol);
        atom.setAtomicNumber(0); // avoid NPE downstream
        
        return atom;
    }

    /**
     * Is the symbol a periodic element.
     * 
     * @param symbol a symbol from the input
     * @return the symbol is a pseudo atom
     */
    private static boolean isPeriodicElement(final String symbol) {
        // XXX: PeriodicTable is slow - switch without file IO would be optimal
        Integer elem = PeriodicTable.getAtomicNumber(symbol);
        return elem != null && elem > 0;
    }

    /**
     * Is the atom symbol a non-periodic element (i.e. pseudo). Valid pseudo
     * atoms are 'R#', 'A', 'Q', '*', 'L' and 'LP'. We also accept 'R' but this
     * is not listed in the specification.
     * 
     * @param symbol a symbol from the input
     * @return the symbol is a valid pseudo element
     */
    static boolean isPseudoElement(final String symbol) {
        return PSUEDO_LABELS.contains(symbol);
    }

    /**
     * Read a coordinate from an MDL input. The MDL V2000 input coordinate has
     * 10 characters, 4 significant figures and is prefixed with whitespace for
     * padding: 'xxxxx.xxxx'. Knowing the format allows us to use an optimised
     * parser which does not consider exponents etc.
     *
     * @param line   input line
     * @param offset first character of the coordinate
     * @return the specified value
     * @throws CDKException the coordinates specification was not valid
     */
    static double readMDLCoordinate(final String line, int offset) throws CDKException {
        // to be valid the decimal should be at the fifth index (4 sig fig)
        if (line.charAt(offset + 5) != '.')
            throw new CDKException("invalid coordinate specification");
        
        int start = offset;
        while (line.charAt(start) == ' ')
            start++;
        
        int sign = sign(line.charAt(start));
        if (sign < 0)
            start++;
        
        int integral = readUInt(line, start, (offset + 5) - start);
        int fraction = readUInt(line, offset + 6, 4);
                
        return sign * (integral * 10000l + fraction) / 10000d;
    }

    /**
     * Convert the a character (from an MDL V2000 input) to a charge value:
     * 1 = +1, 2 = +2, 3 = +3, 4 = doublet radical, 5 = -1, 6 = -2, 7 = -3.
     * 
     * @param c a character
     * @return formal charge 
     */
    private static int toCharge(final char c) {
        switch (c) {
            case '1': return +3;
            case '2': return +2;
            case '3': return +1;
            case '4': return 0; // doublet radical - superseded by M  RAD
            case '5': return -1;
            case '6': return -2;
            case '7': return -3;
        }
        return 0;
    }

    /**
     * Obtain the sign of the character, -1 if the character is '-', +1
     * otherwise.
     *
     * @param c a character
     * @return the sign
     */
    private static int sign(final char c) {
        return c == '-' ? -1 : +1;
    }
    
    /**
     * Convert a character (ASCII code points) to an integer. If the character
     * was not a digit (i.e. space) the value defaults to 0.
     *
     * @param c a character
     * @return the numerical value
     */
    private static int toInt(final char c) {
        // Character.getNumericalValue allows all of unicode which we don't want
        // or need it - imagine an MDL file with roman numerals!
        return c >= '0' && c <= '9' ? c - '0' : 0;
    }

    /**
     * Read an unsigned int value from the given index with the expected number
     * of digits.
     *
     * @param line   input line
     * @param index  start index
     * @param digits number of digits (max)
     * @return an unsigned int
     */
    private static int readUInt(final String line, int index, int digits) {
        int result = 0;
        while (digits-- > 0)
            result = (result * 10) + toInt(line.charAt(index++));
        return result;
    }

    /**
     * Reads an atom from the input allowing for non-standard formatting (i.e
     * truncated lines) and chemical shifts.
     *
     * @param line      input line
     * @param builder   chem object builder
     * @param linecount the current line count
     * @return an atom to add to a container
     * @throws CDKException a CDK error occurred
     * @throws IOException  the isotopes file could not be read
     */
    private IAtom readAtomSlow(String line,
                               IChemObjectBuilder builder,
                               int linecount) throws CDKException, IOException {
        IAtom atom;        
        Matcher trailingSpaceMatcher = TRAILING_SPACE.matcher(line);
        if (trailingSpaceMatcher.find()) {
            handleError("Trailing space found",
                        linecount,
                        trailingSpaceMatcher.start(), trailingSpaceMatcher.end());
            line = trailingSpaceMatcher.replaceAll("");
        }
        double x = Double.parseDouble(line.substring(0, 10).trim());
        double y = Double.parseDouble(line.substring(10, 20).trim());
        double z = Double.parseDouble(line.substring(20, 30).trim());
        
        
        String element = line.substring(31, Math.min(line.length(), 34)).trim();
        if (line.length() < 34) {
            handleError("Element atom type does not follow V2000 format type should of length three" +
                                " and padded with space if required",
                        linecount, 31, 34);
        }

        logger.debug("Atom type: ", element);
        IsotopeFactory isotopeFactory = Isotopes.getInstance();
        if (isotopeFactory.isElement(element)) {
            atom = isotopeFactory.configure(builder.newInstance(IAtom.class, element));
        }
        else if ("A".equals(element)) {
            atom = builder.newInstance(IPseudoAtom.class, element);
        }
        else if ("Q".equals(element)) {
            atom = builder.newInstance(IPseudoAtom.class, element);
        }
        else if ("*".equals(element)) {
            atom = builder.newInstance(IPseudoAtom.class, element);
        }
        else if ("LP".equals(element)) {
            atom = builder.newInstance(IPseudoAtom.class, element);
        }
        else if ("L".equals(element)) {
            atom = builder.newInstance(IPseudoAtom.class, element);
        }
        else if (element.equals("R") ||
                (element.length() > 0 && element.charAt(0) == 'R')) {
            logger.debug("Atom ", element, " is not an regular element. Creating a PseudoAtom.");
            //check if the element is R
            String[] rGroup = element.split("^R");
            if (rGroup.length > 1) {
                try {
                    element = "R" + Integer.valueOf(rGroup[(rGroup.length - 1)]);
                    atom = builder.newInstance(IPseudoAtom.class, element);

                } catch (Exception ex) {
                    // This happens for atoms labeled "R#".
                    // The Rnumber may be set later on, using RGP line
                    atom = builder.newInstance(IPseudoAtom.class, "R");
                }
            }
            else {
                atom = builder.newInstance(IPseudoAtom.class, element);
            }
        }
        else {
            handleError(
                    "Invalid element type. Must be an existing " +
                            "element, or one in: A, Q, L, LP, *.",
                    linecount, 32, 35
                       );
            atom = builder.newInstance(IPseudoAtom.class, element);
            atom.setSymbol(element);
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
                        atom.setMassNumber(major.getMassNumber() + massDiff);
                    }
                } catch (Exception exception) {
                    handleError(
                            "Could not parse mass difference field.",
                            linecount, 35, 37,
                            exception
                               );
                }
            }
            else {
                logger.error("Cannot set mass difference for a non-element!");
            }
        }
        else {
            handleError("Mass difference is missing", linecount, 34, 36);
        }


        // set the stereo partiy
        Integer parity = line.length() > 41 ? Character.digit(line.charAt(41), 10) : 0;
        atom.setStereoParity(parity);

        if (line.length() >= 51) {
            String valenceString = removeNonDigits(line.substring(48, 51));
            logger.debug("Valence: ", valenceString);
            if (!(atom instanceof IPseudoAtom)) {
                try {
                    int valence = Integer.parseInt(valenceString);
                    if (valence != 0) {
                        //15 is defined as 0 in mol files
                        if (valence == 15)
                            atom.setValency(0);
                        else
                            atom.setValency(valence);
                    }
                } catch (Exception exception) {
                    handleError(
                            "Could not parse valence information field",
                            linecount, 49, 52,
                            exception
                               );
                }
            }
            else {
                logger.error("Cannot set valence information for a non-element!");
            }
        }

        if (line.length() >= 39) {
            String chargeCodeString = line.substring(36, 39).trim();
            logger.debug("Atom charge code: ", chargeCodeString);
            int chargeCode = Integer.parseInt(chargeCodeString);
            if (chargeCode == 0) {
                // uncharged species
            }
            else if (chargeCode == 1) {
                atom.setFormalCharge(+3);
            }
            else if (chargeCode == 2) {
                atom.setFormalCharge(+2);
            }
            else if (chargeCode == 3) {
                atom.setFormalCharge(+1);
            }
            else if (chargeCode == 4) {
            }
            else if (chargeCode == 5) {
                atom.setFormalCharge(-1);
            }
            else if (chargeCode == 6) {
                atom.setFormalCharge(-2);
            }
            else if (chargeCode == 7) {
                atom.setFormalCharge(-3);
            }
        }
        else {
            handleError("Atom charge is missing", linecount, 36, 39);
        }

        try {
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
        } catch (Exception exception) {
            // older mol files don't have all these fields...
            logger.warn("A few fields are missing. Older MDL MOL file?");
        }

        //shk3: This reads shifts from after the molecule. I don't think this is an official format, but I saw it frequently 80=>78 for alk
        if (line.length() >= 78) {
            double shift = Double.parseDouble(line.substring(69, 80).trim());
            atom.setProperty("first shift", shift);
        }
        if (line.length() >= 87) {
            double shift = Double.parseDouble(line.substring(79, 87).trim());
            atom.setProperty("second shift", shift);
        }
        
        return atom;
    }

    /**
     * Read a bond line from an MDL V2000 molfile bond block (slow).
     *
     * @param line      the input from the bond block
     * @param builder   chem object builder
     * @param atoms     array of atoms
     * @param linecount the current line count
     * @return a new bond
     * @throws CDKException the bond line could not be parsed
     */
    private IBond readBondSlow(String line,
                               IChemObjectBuilder builder,
                               IAtom[] atoms,
                               int linecount) throws CDKException {
        int atom1 = Integer.parseInt(line.substring(0, 3).trim());
        int atom2 = Integer.parseInt(line.substring(3, 6).trim());
        int order = Integer.parseInt(line.substring(6, 9).trim());
        IBond.Stereo stereo = null;
        if (line.length() >= 12) {
            int mdlStereo = line.length() > 12
                            ? Integer.parseInt(line.substring(9, 12).trim())
                            : Integer.parseInt(line.substring(9).trim());
            if (mdlStereo == 1) {
                // MDL up bond
                stereo = IBond.Stereo.UP;
            }
            else if (mdlStereo == 6) {
                // MDL down bond
                stereo = IBond.Stereo.DOWN;
            }
            else if (mdlStereo == 0) {
                if (order == 2) {
                    // double bond stereo defined by coordinates
                    stereo = IBond.Stereo.E_Z_BY_COORDINATES;
                }
                else {
                    // bond has no stereochemistry
                    stereo = IBond.Stereo.NONE;
                }
            }
            else if (mdlStereo == 3 && order == 2) {
                // unknown E/Z stereochemistry
                stereo = IBond.Stereo.E_OR_Z;
            }
            else if (mdlStereo == 4) {
                //MDL bond undefined
                stereo = IBond.Stereo.UP_OR_DOWN;
            }
        }
        else {
            handleError(
                    "Missing expected stereo field at line: ",
                    linecount, 10, 12
                       );
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Bond: " + atom1 + " - " + atom2 + "; order " + order);
        }
        // interpret CTfile's special bond orders
        IAtom a1 = atoms[atom1 - 1];
        IAtom a2 = atoms[atom2 - 1];
        IBond newBond;
        if (order >= 1 && order <= 3) {
            IBond.Order cdkOrder = IBond.Order.SINGLE;
            if (order == 2) cdkOrder = IBond.Order.DOUBLE;
            if (order == 3) cdkOrder = IBond.Order.TRIPLE;
            if (stereo != null) {
                newBond = builder.newInstance(IBond.class, a1, a2, cdkOrder, stereo);
            }
            else {
                newBond = builder.newInstance(IBond.class, a1, a2, cdkOrder);
            }
        }
        else if (order == 4) {
            // aromatic bond                	
            if (stereo != null) {
                newBond = builder.newInstance(IBond.class, a1, a2, IBond.Order.UNSET, stereo);
            }
            else {
                newBond = builder.newInstance(IBond.class, a1, a2, IBond.Order.UNSET);
            }
            // mark both atoms and the bond as aromatic and raise the SINGLE_OR_DOUBLE-flag
            newBond.setFlag(CDKConstants.SINGLE_OR_DOUBLE, true);
            newBond.setFlag(CDKConstants.ISAROMATIC, true);
            a1.setFlag(CDKConstants.ISAROMATIC, true);
            a2.setFlag(CDKConstants.ISAROMATIC, true);
        }
        else {
            newBond = new CTFileQueryBond(builder);
            IAtom[] bondAtoms = {a1, a2};
            newBond.setAtoms(bondAtoms);
            newBond.setOrder(IBond.Order.UNSET);
            CTFileQueryBond.Type queryBondType = null;
            switch (order) {
                case 5:
                    queryBondType = CTFileQueryBond.Type.SINGLE_OR_DOUBLE;
                    break;
                case 6:
                    queryBondType = CTFileQueryBond.Type.SINGLE_OR_AROMATIC;
                    break;
                case 7:
                    queryBondType = CTFileQueryBond.Type.DOUBLE_OR_AROMATIC;
                    break;
                case 8:
                    queryBondType = CTFileQueryBond.Type.ANY;
                    break;
            }
            ((CTFileQueryBond) newBond).setType(queryBondType);
            newBond.setStereo(stereo);
        }
        return newBond;
    }
    

    /**
     * Read non-structural data from input and store as properties the provided
     * 'container'. Non-structural data appears in a structure data file (SDF)
     * after an Molfile and before the record deliminator ('$$$$'). The data
     * consists of one or more Data Header and Data blocks, an example is seen
     * below.
     * 
     * <pre>{@code
     * > 29 <DENSITY> 
     * 0.9132 - 20.0
     * 
     * > 29 <BOILING.POINT> 
     * 63.0 (737 MM) 
     * 79.0 (42 MM)
     * 
     * > 29 <ALTERNATE.NAMES> 
     * SYLVAN
     * 
     * > 29 <DATE> 
     * 09-23-1980
     * 
     * > 29 <CRC.NUMBER> 
     * F-0213
     * 
     * }</pre>
     * 
     *
     * @param input     input source
     * @param container the container
     * @throws IOException an error occur whilst reading the input
     */
    @TestMethod("readNonStructuralData")
    static void readNonStructuralData(final BufferedReader input,
                                      final IAtomContainer container) throws IOException {

        final String newline = System.getProperty("line.separator");

        String line, header = null;
        boolean wrap = false;

        final StringBuilder data = new StringBuilder(80);

        while (!endOfRecord(line = input.readLine())) {

            final String newHeader = dataHeader(line);

            if (newHeader != null) {

                if (header != null)
                    container.setProperty(header, data.toString());

                header = newHeader;
                wrap = false;
                data.setLength(0);

            }
            else {

                if (data.length() > 0 || !line.equals(" "))
                    line = line.trim();

                if (line.isEmpty())
                    continue;

                if (!wrap && data.length() > 0)
                    data.append(newline);
                data.append(line);

                wrap = line.length() == 80;
            }
        }

        if (header != null)
            container.setProperty(header, data.toString());
    }

    /**
     * Obtain the field name from a potential SD data header. If the header
     * does not contain a field name, then null is returned. The method does
     * not currently return field numbers (e.g. DT&lt;n&gt;).
     * 
     * @param line an input line
     * @return the field name
     */
    @TestMethod("dataHeader_1")
    static String dataHeader(final String line) {
        if (line.length() > 2 
                && line.charAt(0) != '>'
                && line.charAt(1) != ' ')
            return null;
        int i = line.indexOf('<', 2);
        if (i < 0)
            return null;
        int j = line.indexOf('>', i);
        if (j < 0)
            return null;
        return line.substring(i + 1, j);
    }

    /**
     * Is the line the end of a record. A line is the end of a record if it
     * is 'null' or is the SDF deliminator, '$$$$'. 
     * 
     * @param line a line from the input 
     * @return the line indicates the end of a record was reached
     */
    private static boolean endOfRecord(final String line) {
        return line == null || line.equals(RECORD_DELIMITER);
    }
}

