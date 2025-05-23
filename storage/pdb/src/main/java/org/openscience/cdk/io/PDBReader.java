/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 *                    2014  Mark B Vine (orcid:0000-0002-7794-0426)
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
 *  */
package org.openscience.cdk.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Point3d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.rebond.RebondTool;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMonomer;
import org.openscience.cdk.interfaces.IStrand;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.PDBFormat;
import org.openscience.cdk.io.setting.BooleanIOSetting;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.protein.data.PDBAtom;
import org.openscience.cdk.protein.data.PDBMonomer;
import org.openscience.cdk.protein.data.PDBPolymer;
import org.openscience.cdk.protein.data.PDBStrand;
import org.openscience.cdk.protein.data.PDBStructure;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

/**
 * Reads the contents of a PDBFile.
 *
 * <p>A description can be found at <a href="http://www.rcsb.org/pdb/static.do?p=file_formats/pdb/index.html">
 * http://www.rcsb.org/pdb/static.do?p=file_formats/pdb/index.html</a>.
 *
 * @cdk.iooptions
 *
 * @author      Edgar Luttmann
 * @author Bradley Smith &lt;bradley@baysmith.com&gt;
 * @author Martin Eklund &lt;martin.eklund@farmbio.uu.se&gt;
 * @author Ola Spjuth &lt;ola.spjuth@farmbio.uu.se&gt;
 * @author Gilleain Torrance &lt;gilleain.torrance@gmail.com&gt;
 * @cdk.created 2001-08-06
 * @cdk.keyword file format, PDB
 * @cdk.bug     1714141
 * @cdk.bug     1794439
 */
public class PDBReader extends DefaultChemObjectReader {

    private static final ILoggingTool    logger            = LoggingToolFactory.createLoggingTool(PDBReader.class);
    private BufferedReader         _oInput;                                                                  // The internal used BufferedReader
    private BooleanIOSetting       useRebondTool;
    private BooleanIOSetting       readConnect;
    private BooleanIOSetting       useHetDictionary;

    private Map<Integer, IAtom>    atomNumberMap;

    /*
     * This is a temporary store for bonds from CONNECT records. As CONNECT is
     * deliberately fully redundant (a->b and b->a) we need to use this to weed
     * out the duplicates.
     */
    private List<IBond>            bondsFromConnectRecords;

    /**
     * A mapping between HETATM 3-letter codes + atomNames to CDK atom type
     * names; for example "RFB.N13" maps to "N.planar3".
     */
    private Map<String, String>    hetDictionary;
    private Set<String>            hetResidues;

    private AtomTypeFactory        cdkAtomTypeFactory;

    private static final String    hetDictionaryPath = "type_map.txt";
    private static final String    resDictionaryPath = "type_res.txt";

    /**
     *
     * Constructs a new PDBReader that can read Molecules from a given
     * InputStream.
     *
     * @param oIn  The InputStream to read from
     *
     */
    public PDBReader(InputStream oIn) {
        this(new InputStreamReader(oIn));
    }

    /**
     *
     * Constructs a new PDBReader that can read Molecules from a given
     * Reader.
     *
     * @param oIn  The Reader to read from
     *
     */
    public PDBReader(Reader oIn) {
        _oInput = new BufferedReader(oIn);
        initIOSettings();
        hetDictionary = null;
        cdkAtomTypeFactory = null;
    }

    public PDBReader() {
        this(new StringReader(""));
    }

    @Override
    public IResourceFormat getFormat() {
        return PDBFormat.getInstance();
    }

    @Override
    public void setReader(Reader input) {
        if (input instanceof BufferedReader) {
            this._oInput = (BufferedReader) input;
        } else {
            this._oInput = new BufferedReader(input);
        }
    }

    @Override
    public void setReader(InputStream input) {
        setReader(new InputStreamReader(input));
    }

    @Override
    public boolean accepts(Class<? extends IChemObject> classObject) {
        Class<?>[] interfaces = classObject.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            if (IChemFile.class.equals(anInterface)) return true;
        }
        if (IChemFile.class.equals(classObject)) return true;
        Class superClass = classObject.getSuperclass();
        if (superClass != null) return this.accepts(superClass);
        return false;
    }

    /**
     *
     * Takes an object which subclasses IChemObject, e.g. Molecule, and will
     * read this (from file, database, internet etc). If the specific
     * implementation does not support a specific IChemObject it will throw
     * an Exception.
     *
     * @param oObj  The object that subclasses IChemObject
     * @return      The IChemObject read
     * @exception   CDKException
     *
     */
    @Override
    public <T extends IChemObject> T read(T oObj) throws CDKException {
        if (oObj instanceof IChemFile) {
            return (T) readChemFile((IChemFile) oObj);
        } else {
            throw new CDKException("Only supported is reading of ChemFile objects.");
        }
    }

    /**
     * Read a <code>ChemFile</code> from a file in PDB format. The molecules
     * in the file are stored as <code>BioPolymer</code>s in the
     * <code>ChemFile</code>. The residues are the monomers of the
     * <code>BioPolymer</code>, and their names are the concatenation of the
     * residue, chain id, and the sequence number. Separate chains (denoted by
     * TER records) are stored as separate <code>BioPolymer</code> molecules.
     *
     * <p>Connectivity information is not currently read.
     *
     * @return The ChemFile that was read from the PDB file.
     */
    private IChemFile readChemFile(IChemFile oFile) {
        // initialize all containers
        IChemSequence oSeq = oFile.getBuilder().newInstance(IChemSequence.class);
        IChemModel oModel = oFile.getBuilder().newInstance(IChemModel.class);
        IAtomContainerSet oSet = oFile.getBuilder().newInstance(IAtomContainerSet.class);

        // some variables needed
        String cCol;
        PDBAtom oAtom;
        PDBPolymer oBP = new PDBPolymer();
        IAtomContainer molecularStructure = oFile.getBuilder().newInstance(IAtomContainer.class);
        StringBuilder cResidue;
        String oObj;
        IMonomer oMonomer;
        String cRead = "";
        char chain = 'A'; // To ensure stringent name giving of monomers
        IStrand oStrand;
        int lineLength;

        boolean isProteinStructure = false;

        atomNumberMap = new Hashtable<>();
        if (readConnect.isSet()) {
            bondsFromConnectRecords = new ArrayList<>();
        }

        // do the reading of the Input
        try {
            do {
                cRead = _oInput.readLine();
                logger.debug("Read line: ", cRead);
                if (cRead != null) {
                    lineLength = cRead.length();
                    
                    // make sure the record name is 6 characters long
                    if (lineLength < 6) {
                        cRead = cRead + "      ";
                    }
                    // check the first column to decide what to do
                    cCol = cRead.substring(0, 6);
                    if ("SEQRES".equalsIgnoreCase(cCol)) {
                        isProteinStructure = true;
                    } else if ("ATOM  ".equalsIgnoreCase(cCol)) {
                        // read an atom record
                        oAtom = readAtom(cRead, lineLength);

                        if (isProteinStructure) {
                            // construct a string describing the residue
                            cResidue = new StringBuilder(8);
                            oObj = oAtom.getResName();
                            if (oObj != null) {
                                cResidue = cResidue.append(oObj.trim());
                            }
                            oObj = oAtom.getChainID();
                            if (oObj != null) {
                                // cResidue = cResidue.append(((String)oObj).trim());
                                cResidue = cResidue.append(chain);
                            }
                            oObj = oAtom.getResSeq();
                            if (oObj != null) {
                                cResidue = cResidue.append(oObj.trim());
                            }

                            // search for an existing strand or create a new one.
                            String strandName = oAtom.getChainID();
                            if (strandName == null || strandName.isEmpty()) {
                                strandName = String.valueOf(chain);
                            }
                            oStrand = oBP.getStrand(strandName);
                            if (oStrand == null) {
                                oStrand = new PDBStrand();
                                oStrand.setStrandName(strandName);
                                oStrand.setID(String.valueOf(chain));
                            }

                            // search for an existing monomer or create a new one.
                            oMonomer = oBP.getMonomer(cResidue.toString(), String.valueOf(chain));
                            if (oMonomer == null) {
                                PDBMonomer monomer = new PDBMonomer();
                                monomer.setMonomerName(cResidue.toString());
                                monomer.setMonomerType(oAtom.getResName());
                                monomer.setChainID(oAtom.getChainID());
                                monomer.setICode(oAtom.getICode());
                                monomer.setResSeq(oAtom.getResSeq());
                                oMonomer = monomer;
                            }

                            // add the atom
                            oBP.addAtom(oAtom, oMonomer, oStrand);
                        } else {
                            molecularStructure.addAtom(oAtom);
                        }

                        if (readConnect.isSet() && atomNumberMap.put(oAtom.getSerial(), oAtom) != null) {
                            logger.warn("Duplicate serial ID found for atom: ", oAtom);
                        }
                        logger.debug("Added ATOM: ", oAtom);

                        /* As HETATMs cannot be considered to either belong to a certain monomer or strand, they are dealt with seperately. */
                    } else if ("HETATM".equalsIgnoreCase(cCol)) {
                        // read an atom record
                        oAtom = readAtom(cRead, lineLength);
                        oAtom.setHetAtom(true);
                        if (isProteinStructure) {
                            oBP.addAtom(oAtom);
                        } else {
                            molecularStructure.addAtom(oAtom);
                        }
                        if (atomNumberMap.put(oAtom.getSerial(), oAtom) != null) {
                            logger.warn("Duplicate serial ID found for atom: ", oAtom);
                        }
                        logger.debug("Added HETATM: ", oAtom);
                    } else if ("TER   ".equalsIgnoreCase(cCol)) {
                        // start new strand
                        chain++;
                        oStrand = new PDBStrand();
                        oStrand.setStrandName(String.valueOf(chain));
                        logger.debug("Added new STRAND");
                    } else if ("END   ".equalsIgnoreCase(cCol)) {
                        atomNumberMap.clear();
                        if (isProteinStructure) {
                            // create bonds and finish the molecule
                            oSet.addAtomContainer(oBP);
                            if (useRebondTool.isSet()) {
                                try {
                                    if (!createBondsWithRebondTool(oBP)) {
                                        // Get rid of all potentially created bonds.
                                        logger.info("Bonds could not be created using the RebondTool when PDB file was read.");
                                        oBP.removeAllBonds();
                                    }
                                } catch (Exception exception) {
                                    logger.info("Bonds could not be created when PDB file was read.");
                                    logger.debug(exception);
                                }
                            }
                        } else {
                            if (useRebondTool.isSet()) createBondsWithRebondTool(molecularStructure);
                            oSet.addAtomContainer(molecularStructure);
                        }

                    } else if (cCol.equals("MODEL ")) {
                        // OK, start a new model and save the current one first *if* it contains atoms
                        if (isProteinStructure) {
                            if (oBP.getAtomCount() > 0) {
                                // save the model
                                oSet.addAtomContainer(oBP);
                                oModel.setMoleculeSet(oSet);
                                oSeq.addChemModel(oModel);
                                // setup a new one
                                oBP = new PDBPolymer();
                                oModel = oFile.getBuilder().newInstance(IChemModel.class);
                                oSet = oFile.getBuilder().newInstance(IAtomContainerSet.class);
                                // avoid duplicate atom warnings
                                atomNumberMap.clear();
                            }
                        } else {
                            if (molecularStructure.getAtomCount() > 0) {
                                //								 save the model
                                oSet.addAtomContainer(molecularStructure);
                                oModel.setMoleculeSet(oSet);
                                oSeq.addChemModel(oModel);
                                // setup a new one
                                molecularStructure = oFile.getBuilder().newInstance(IAtomContainer.class);
                                oModel = oFile.getBuilder().newInstance(IChemModel.class);
                                oSet = oFile.getBuilder().newInstance(IAtomContainerSet.class);
                            }
                        }
                    } else if ("REMARK".equalsIgnoreCase(cCol)) {
                        Object comment = oFile.getProperty(CDKConstants.COMMENT);
                        if (comment == null) {
                            comment = "";
                        }
                        if (lineLength > 12) {
                            comment = comment + cRead.substring(11).trim()
                                    + "\n";
                            oFile.setProperty(CDKConstants.COMMENT, comment);
                        } else {
                            logger.warn("REMARK line found without any comment!");
                        }
                    } else if ("COMPND".equalsIgnoreCase(cCol)) {
                        if (cRead.length() > 10) {
                            String title = cRead.substring(10).trim();
                            if (!title.isEmpty())
                                oFile.setProperty(CDKConstants.TITLE, title);
                        }
                    }

                    /* ***********************************************************
                     * Read connectivity information from CONECT records. Only
                     * covalent bonds are dealt with. Perhaps salt bridges
                     * should be dealt with in the same way..?
                     */
                    else if (readConnect.isSet() && "CONECT".equalsIgnoreCase(cCol)) {
                        cRead = cRead.trim();
                        if (cRead.length() < 16) {
                            logger.debug("Skipping unexpected empty CONECT line! : ", cRead);
                        } else {
                            int lineIndex = 6;
                            int atomFromNumber = -1;
                            int atomToNumber;
                            IAtomContainer molecule = (isProteinStructure) ? oBP : molecularStructure;
                            while (lineIndex + 5 <= cRead.length()) {
                                String part = cRead.substring(lineIndex, lineIndex + 5).trim();
                                if (atomFromNumber == -1) {
                                    try {
                                        atomFromNumber = Integer.parseInt(part);
                                    } catch (NumberFormatException nfe) {
                                    }
                                } else {
                                    try {
                                        atomToNumber = Integer.parseInt(part);
                                    } catch (NumberFormatException nfe) {
                                        atomToNumber = -1;
                                    }
                                    if (atomToNumber != -1) {
                                        addBond(molecule, atomFromNumber, atomToNumber);
                                        logger.debug("Bonded " + atomFromNumber + " with " + atomToNumber);
                                    }
                                }
                                lineIndex += 5;
                            }
                        }
                    }
                    /* ********************************************************** */

                    else if ("HELIX ".equalsIgnoreCase(cCol)) {
                        //						HELIX    1 H1A CYS A   11  LYS A   18  1 RESIDUE 18 HAS POSITIVE PHI    1D66  72
                        //						          1         2         3         4         5         6         7
                        //						01234567890123456789012345678901234567890123456789012345678901234567890123456789
                        PDBStructure structure = new PDBStructure();
                        structure.setStructureType(PDBStructure.HELIX);
                        structure.setStartChainID(cRead.charAt(19));
                        structure.setStartSequenceNumber(Integer.parseInt(cRead.substring(21, 25).trim()));
                        structure.setStartInsertionCode(cRead.charAt(25));
                        structure.setEndChainID(cRead.charAt(31));
                        structure.setEndSequenceNumber(Integer.parseInt(cRead.substring(33, 37).trim()));
                        structure.setEndInsertionCode(cRead.charAt(37));
                        oBP.addStructure(structure);
                    } else if ("SHEET ".equalsIgnoreCase(cCol)) {
                        PDBStructure structure = new PDBStructure();
                        structure.setStructureType(PDBStructure.SHEET);
                        structure.setStartChainID(cRead.charAt(21));
                        structure.setStartSequenceNumber(Integer.parseInt(cRead.substring(22, 26).trim()));
                        structure.setStartInsertionCode(cRead.charAt(26));
                        structure.setEndChainID(cRead.charAt(32));
                        structure.setEndSequenceNumber(Integer.parseInt(cRead.substring(33, 37).trim()));
                        structure.setEndInsertionCode(cRead.charAt(37));
                        oBP.addStructure(structure);
                    } else if ("TURN  ".equalsIgnoreCase(cCol)) {
                        PDBStructure structure = new PDBStructure();
                        structure.setStructureType(PDBStructure.TURN);
                        structure.setStartChainID(cRead.charAt(19));
                        structure.setStartSequenceNumber(Integer.parseInt(cRead.substring(20, 24).trim()));
                        structure.setStartInsertionCode(cRead.charAt(24));
                        structure.setEndChainID(cRead.charAt(30));
                        structure.setEndSequenceNumber(Integer.parseInt(cRead.substring(31, 35).trim()));
                        structure.setEndInsertionCode(cRead.charAt(35));
                        oBP.addStructure(structure);
                    } // ignore all other commands
                }
            } while (_oInput.ready() && (cRead != null));
        } catch (IOException | IllegalArgumentException | CDKException e) {
            logger.error("Found a problem at line:");
            logger.error(cRead);
            logger.error("01234567890123456789012345678901234567890123456789012345678901234567890123456789");
            logger.error("          1         2         3         4         5         6         7         ");
            logger.error("  error: " + e.getMessage());
            logger.debug(e);
        }

        // try to close the Input
        try {
            _oInput.close();
        } catch (Exception e) {
            logger.debug(e);
        }

        // Set all the dependencies
        oModel.setMoleculeSet(oSet);
        oSeq.addChemModel(oModel);
        oFile.addChemSequence(oSeq);

        return oFile;
    }

    private void addBond(IAtomContainer molecule, int bondAtomNo, int bondedAtomNo) {
        IAtom firstAtom = atomNumberMap.get(bondAtomNo);
        IAtom secondAtom = atomNumberMap.get(bondedAtomNo);
        if (firstAtom == null) {
            logger.error("Could not find bond start atom in map with serial id: ", bondAtomNo);
        } else if (secondAtom == null) {
            logger.error("Could not find bond target atom in map with serial id: ", bondAtomNo);
        } else {
            IBond bond = firstAtom.getBuilder().newInstance(IBond.class, firstAtom, secondAtom, IBond.Order.SINGLE);
            for (IBond bondsFromConnectRecord : bondsFromConnectRecords) {
                IAtom a = bondsFromConnectRecord.getBegin();
                IAtom b = bondsFromConnectRecord.getEnd();
                if ((a.equals(firstAtom) && b.equals(secondAtom)) || (b.equals(firstAtom) && a.equals(secondAtom))) {
                    // already stored
                    return;
                }
            }
            bondsFromConnectRecords.add(bond);
            molecule.addBond(bond);
        }
    }

    private boolean createBondsWithRebondTool(IAtomContainer molecule) {
        RebondTool tool = new RebondTool(2.0, 0.5, 0.5);
        try {
            //			 configure atoms
            AtomTypeFactory factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/jmol_atomtypes.txt",
                    molecule.getBuilder());
            for (IAtom atom : molecule.atoms()) {
                try {
                    IAtomType[] types = factory.getAtomTypes(atom.getSymbol());
                    if (types.length > 0) {
                        // just pick the first one
                        AtomTypeManipulator.configure(atom, types[0]);
                    } else {
                        logger.warn("Could not configure atom with symbol: " + atom.getSymbol());
                    }
                } catch (Exception e) {
                    logger.warn("Could not configure atom (but don't care): " + e.getMessage());
                    logger.debug(e);
                }
            }
            tool.rebond(molecule);
        } catch (Exception e) {
            logger.error("Could not rebond the polymer: " + e.getMessage());
            logger.debug(e);
        }
        return true;
    }

    private static boolean isUpper(char c) {
        return c >= 'A' && c <= 'Z';
    }
    private static boolean isLower(char c) {
        return c >= 'a' && c <= 'z';
    }
    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private String parseAtomSymbol(String str) {

        if (str == null || str.isEmpty())
            return null;

        final int len = str.length();

        StringBuilder sym = new StringBuilder();

        // try grabbing from end of line

        if (len > 76 && isUpper(str.charAt(76))) {
            sym.append(str.charAt(76));
            if (len > 77 && isUpper(str.charAt(77)))
                sym.append(Character.toLowerCase(str.charAt(77)));
            else if (len > 77 && isLower(str.charAt(77)))
                sym.append(Character.toLowerCase(str.charAt(77)));
        } else if (len > 76 &&str.charAt(76) == ' ') {
            if (len > 77 && isUpper(str.charAt(77)))
                sym.append(str.charAt(77));
        }

        if (sym.length() > 0)
            return sym.toString();

        // try getting from PDB atom name
        if (len > 13 && isUpper(str.charAt(13))) {
            if (str.charAt(12) == ' ') {
                sym.append(str.charAt(13));
                if (isLower(str.charAt(14)))
                    sym.append(str.charAt(14));
            } else if (isUpper(str.charAt(12))) {
                if (str.charAt(0) == 'A' && str.charAt(12) == 'H') {
                    sym.append('H'); // ATOM record H is always H
                } else {
                    sym.append(str.charAt(12));
                    sym.append(Character.toLowerCase(str.charAt(13)));
                }
            } else if (isDigit(str.charAt(12))) {
                sym.append(str.charAt(13));
            }
        }

        if (sym.length() > 0)
            return sym.toString();

        return null;
    }

    /**
     * Creates an <code>Atom</code> and sets properties to their values from
     * the ATOM or HETATM record. If the line is shorter than 80 characters, the
     * information past 59 characters is treated as optional. If the line is
     * shorter than 59 characters, a <code>RuntimeException</code> is thrown.
     *
     * @param cLine  the PDB ATOM or HEATATM record.
     * @return the <code>Atom</code> created from the record.
     * @throws RuntimeException if the line is too short (less than 59 characters).
     */
    private PDBAtom readAtom(String cLine, int lineLength) throws CDKException {
        // a line can look like (two in old format, then two in new format):
        //
        //           1         2         3         4         5         6         7
        // 01234567890123456789012345678901234567890123456789012345678901234567890123456789
        // ATOM      1  O5*   C A   1      20.662  36.632  23.475  1.00 10.00      114D  45
        // ATOM   1186 1H   ALA     1      10.105   5.945  -6.630  1.00  0.00      1ALE1288
        // ATOM     31  CA  SER A   3      -0.891  17.523  51.925  1.00 28.64           C
        // HETATM 3486 MG    MG A 302      24.885  14.008  59.194  1.00 29.42          MG+2
        //
        // note: the first two examples have the PDBID in col 72-75
        // note: the last two examples have the element symbol in col 76-77
        // note: the last (Mg hetatm) has a charge in col 78-79

        if (lineLength < 59) {
            throw new RuntimeException("PDBReader error during readAtom(): line too short");
        }

        boolean isHetatm = cLine.startsWith("HETATM");
        String  atomName = cLine.substring(12, 16).trim();
        String  resName  = cLine.substring(17, 20).trim();
        String  symbol   = parseAtomSymbol(cLine);

        if (symbol == null)
            handleError("Cannot parse symbol from " + atomName);

        PDBAtom oAtom = new PDBAtom(symbol, new Point3d(Double.parseDouble(cLine.substring(30, 38)),
                Double.parseDouble(cLine.substring(38, 46)), Double.parseDouble(cLine.substring(46, 54))));
        if (useHetDictionary.isSet() && isHetatm) {
            String cdkType = typeHetatm(resName, atomName);
            oAtom.setAtomTypeName(cdkType);
            if (cdkType != null) {
                try {
                    cdkAtomTypeFactory.configure(oAtom);
                } catch (CDKException cdke) {
                    logger.warn("Could not configure", resName, " ", atomName);
                }
            }
        }

        oAtom.setRecord(cLine);
        oAtom.setSerial(Integer.parseInt(cLine.substring(6, 11).trim()));
        oAtom.setName(atomName.trim());
        oAtom.setAltLoc(cLine.substring(16, 17).trim());
        oAtom.setResName(resName);
        oAtom.setChainID(cLine.substring(21, 22).trim());
        oAtom.setResSeq(cLine.substring(22, 26).trim());
        oAtom.setICode(cLine.substring(26, 27).trim());
        if (useHetDictionary.isSet() && isHetatm) {
            oAtom.setID(oAtom.getResName() + "." + atomName);
        } else {
            oAtom.setAtomTypeName(oAtom.getResName() + "." + atomName);
        }
        if (lineLength >= 59) {
            String frag = cLine.substring(54, Math.min(lineLength, 60)).trim();
            if (!frag.isEmpty()) {
                oAtom.setOccupancy(Double.parseDouble(frag));
            }
        }
        if (lineLength >= 65) {
            String frag = cLine.substring(60, Math.min(lineLength, 66)).trim();
            if (!frag.isEmpty()) {
                oAtom.setTempFactor(Double.parseDouble(frag));
            }
        }
        if (lineLength >= 75) {
            oAtom.setSegID(cLine.substring(72, Math.min(lineLength, 76)).trim());
        }
        //		if (lineLength >= 78) {
        //            oAtom.setSymbol((new String(cLine.substring(76, 78))).trim());
        //		}
        if (lineLength >= 79) {
            String frag;
            if (lineLength >= 80) {
                frag = cLine.substring(78, 80).trim();
            } else {
                frag = cLine.substring(78);
            }
            if (frag.length() > 0) {
                // see Format_v33_A4.pdf, p. 178
                if (frag.endsWith("-") || frag.endsWith("+")) {
                    oAtom.setCharge(Double.parseDouble(new StringBuilder(frag).reverse().toString()));
                } else {
                    oAtom.setCharge(Double.parseDouble(frag));
                }
            }
        }

        /* ***********************************************************************************
         * It sets a flag in the property content of an atom, which is used when
         * bonds are created to check if the atom is an OXT-record => needs
         * special treatment.
         */
        String oxt = cLine.substring(13, 16).trim();

        oAtom.setOxt(oxt.equals("OXT"));
        /* ********************************************************************************** */

        return oAtom;
    }

    private String typeHetatm(String resName, String atomName) {
        if (hetDictionary == null) {
            readHetDictionary();
            cdkAtomTypeFactory = AtomTypeFactory.getInstance("org/openscience/cdk/dict/data/cdk-atom-types.owl",
                    DefaultChemObjectBuilder.getInstance());
        }

        // lookup the atom type using the residue and name, if the atom is a hydrogen
        // or carbon and is a known residue we default to the common H and C.sp2 cases
        String key = resName + "." + atomName;
        String type = hetDictionary.get(key);
        if (type != null)
            return type;
        else if (atomName.startsWith("H"))
            return hetResidues.contains(resName) ? "H" : null;
        else if (hetResidues.contains(resName) && atomName.startsWith("C"))
            return hetResidues.contains(resName) ? "C.sp2" : null;

        return null;
    }

    private void readHetDictionary() {
        hetDictionary = new HashMap<>();
        hetResidues = new HashSet<>();
        try (InputStream ins = getClass().getResourceAsStream(hetDictionaryPath);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ins, StandardCharsets.UTF_8))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                int colonIndex = line.indexOf(':');
                if (colonIndex == -1) continue;
                if (line.startsWith("#"))
                    continue; // comment
                String typeKey = line.substring(0, colonIndex);
                String typeValue = line.substring(colonIndex + 1);
                if (typeValue.equals("null")) {
                    hetDictionary.put(typeKey, null);
                } else {
                    hetDictionary.put(typeKey, typeValue);
                }
                hetResidues.add(typeKey.split("\\.")[0]);
            }
        } catch (IOException ioe) {
            logger.error(ioe.getMessage());
        }

        // additional residue names where all atom types are C.sp2/H
        try (InputStream ins = getClass().getResourceAsStream(resDictionaryPath);
             BufferedReader brdr = new BufferedReader(new InputStreamReader(ins, StandardCharsets.UTF_8))) {
            brdr.lines().filter(l -> !l.startsWith("#"))
                .forEach(hetResidues::add);
        } catch (IOException ioe) {
            logger.error(ioe.getMessage());
        }
    }

    @Override
    public void close() throws IOException {
        _oInput.close();
    }

    private void initIOSettings() {
        useRebondTool = addSetting(new BooleanIOSetting("UseRebondTool", IOSetting.Importance.LOW,
                "Should the PDBReader deduce bonding patterns?", "false"));
        readConnect = addSetting(new BooleanIOSetting("ReadConnectSection", IOSetting.Importance.LOW,
                "Should the CONECT be read?", "true"));
        useHetDictionary = addSetting(new BooleanIOSetting("UseHetDictionary", IOSetting.Importance.LOW,
                "Should the PDBReader use the HETATM dictionary for atom types?", "false"));
    }

    public void customizeJob() {
        for (IOSetting setting : getSettings()) {
            fireIOSettingQuestion(setting);
        }
    }

}
