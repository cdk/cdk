/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import javax.vecmath.Point3d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.NoSuchAtomTypeException;
import org.openscience.cdk.graph.rebond.RebondTool;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBioPolymer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
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
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

/**
 * Reads the contents of a PDBFile.
 *
 * <p>A description can be found at <a href="http://www.rcsb.org/pdb/static.do?p=file_formats/pdb/index.html">
 * http://www.rcsb.org/pdb/static.do?p=file_formats/pdb/index.html</a>.
 *
 * @cdk.module  pdb
 * @cdk.svnrev  $Revision$
 *
 * @author      Edgar Luttmann
 * @author      Bradley Smith <bradley@baysmith.com>
 * @author      Martin Eklund <martin.eklund@farmbio.uu.se>
 * @author      Ola Spjuth <ola.spjuth@farmbio.uu.se>
 * @cdk.created 2001-08-06
 * @cdk.keyword file format, PDB
 * @cdk.bug     1714141
 * @cdk.bug     1794439
 */
@TestClass("org.openscience.cdk.io.PDBReaderTest")
public class PDBReader extends DefaultChemObjectReader {
	
	private LoggingTool logger;
	private BufferedReader _oInput; // The internal used BufferedReader
	private BooleanIOSetting useRebondTool;
	private BooleanIOSetting readConnect;
	
	private Map atomNumberMap;
	
	/*
	 * This is a temporary store for bonds from CONNECT records.
	 * As CONNECT is deliberately fully redundant (a->b and b->a)
	 * we need to use this to weed out the duplicates.
	 */
	private ArrayList bondsFromConnectRecords;
	
	private static AtomTypeFactory pdbFactory;
	
	/**
	 *
	 * Contructs a new PDBReader that can read Molecules from a given
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
	 * Contructs a new PDBReader that can read Molecules from a given
	 * Reader.
	 *
	 * @param oIn  The Reader to read from
	 *
	 */
	public PDBReader(Reader oIn) {
		logger = new LoggingTool(this.getClass());
		_oInput = new BufferedReader(oIn);
		initIOSettings();
		pdbFactory = null;
	}
	
	public PDBReader() {
		this(new StringReader(""));
	}
	
	@TestMethod("testGetFormat")
    public IResourceFormat getFormat() {
		return PDBFormat.getInstance();
	}
	
	@TestMethod("testSetReader_Reader")
    public void setReader(Reader input) throws CDKException {
		if (input instanceof BufferedReader) {
			this._oInput = (BufferedReader)input;
		} else {
			this._oInput = new BufferedReader(input);
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
	public IChemObject read(IChemObject oObj) throws CDKException {
		if (oObj instanceof IChemFile) {
			if (pdbFactory == null) {
				try {
					pdbFactory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/pdb_atomtypes.xml", 
				    	  ((IChemFile)oObj).getBuilder());
				} catch (Exception exception) {
					logger.debug(exception);
					throw new CDKException("Could not setup list of PDB atom types! " + exception.getMessage(), exception);
				}
			}
			return readChemFile((IChemFile)oObj);
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
	private IChemFile readChemFile(IChemFile oFile) 	{
		// initialize all containers
		IChemSequence oSeq = oFile.getBuilder().newChemSequence();
		IChemModel oModel = oFile.getBuilder().newChemModel();
		IMoleculeSet oSet = oFile.getBuilder().newMoleculeSet();
		
		// some variables needed
		String cCol;
		PDBAtom oAtom;
		PDBPolymer oBP = new PDBPolymer();
		IMolecule molecularStructure = oFile.getBuilder().newMolecule();
		StringBuffer cResidue;
		String oObj;
		IMonomer oMonomer;
		String cRead = "";
		char chain = 'A';	// To ensure stringent name giving of monomers
		IStrand oStrand;
		int lineLength = 0;
		
		boolean isProteinStructure = false;
		
		atomNumberMap = new Hashtable();
		if (readConnect.isSet()) {
		    bondsFromConnectRecords = new ArrayList();
		}
		
		// do the reading of the Input		
		try {
			do {
				cRead = _oInput.readLine();
				logger.debug("Read line: ", cRead);
				if (cRead != null) {	
					lineLength = cRead.length();
					
					if (lineLength < 80) {
						logger.warn("Line is not of the expected length 80!");
					}
					
					// make sure the record name is 6 characters long
					if (lineLength < 6) {
						cRead = cRead + "      ";
					}
					// check the first column to decide what to do
					cCol = cRead.substring(0,6);
					if ("SEQRES".equalsIgnoreCase(cCol)) {
						isProteinStructure = true;
					} else if ("ATOM  ".equalsIgnoreCase(cCol)) {
						// read an atom record
						oAtom = readAtom(cRead, lineLength);
						
						if (isProteinStructure) {
							// construct a string describing the residue
							cResidue = new StringBuffer(8);
							oObj = oAtom.getResName();
							if (oObj != null) {
								cResidue = cResidue.append(oObj.trim());
							}
							oObj = oAtom.getChainID();
							if (oObj != null) {
								// cResidue = cResidue.append(((String)oObj).trim());
								cResidue = cResidue.append(String.valueOf(chain));
							}
							oObj = oAtom.getResSeq();
							if (oObj != null) {
								cResidue = cResidue.append(oObj.trim());
							}

							// search for an existing strand or create a new one.
							String strandName = oAtom.getChainID();
							if (strandName == null || strandName.length() == 0) {
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
							if (readConnect.isSet() && atomNumberMap.put(new Integer(oAtom.getSerial()), oAtom) != null) {
								logger.warn("Duplicate serial ID found for atom: ", oAtom);
							}
						} else {
							molecularStructure.addAtom(oAtom);
						}
						logger.debug("Added ATOM: ", oAtom);
						
						/** As HETATMs cannot be considered to either belong to a certain monomer or strand,
						 * they are dealt with seperately.*/
					} else if("HETATM".equalsIgnoreCase(cCol))	{
						// read an atom record
						oAtom = readAtom(cRead, lineLength);
						oAtom.setHetAtom(true);
						oBP.addAtom(oAtom);
						if (atomNumberMap.put(new Integer(oAtom.getSerial()), oAtom) != null) {
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
							if (useRebondTool.isSet()) {
								try {
									if(!createBondsWithRebondTool(oBP))	{
										// Get rid of all potentially created bonds.
										logger.info("Bonds could not be created using the RebondTool when PDB file was read.");								
										oBP.removeAllBonds();								
									}
								} catch (Exception exception) {
									logger.info("Bonds could not be created when PDB file was read.");
									logger.debug(exception);
								}
							}
							oSet.addMolecule(oBP);
						} else {
							oSet.addMolecule(molecularStructure);
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
								oModel = oFile.getBuilder().newChemModel();
								oSet = oFile.getBuilder().newMoleculeSet();						
							}
						} else {
							if (molecularStructure.getAtomCount() > 0) {
//								 save the model
								oSet.addAtomContainer(molecularStructure);
								oModel.setMoleculeSet(oSet);
								oSeq.addChemModel(oModel);
								// setup a new one
								molecularStructure = oFile.getBuilder().newMolecule();
								oModel = oFile.getBuilder().newChemModel();
								oSet = oFile.getBuilder().newMoleculeSet();		
							}
						}
					} else if ("REMARK".equalsIgnoreCase(cCol)) {						
						Object comment = oFile.getProperty(CDKConstants.COMMENT);
                        if (comment == null) {
                        	comment = "";
                        }
                        if (lineLength >12) {
                        	comment = comment.toString() + cRead.substring(11).trim() + System.getProperty("line.separator");
                        	oFile.setProperty(CDKConstants.COMMENT, comment);
                        } else {
                        	logger.warn("REMARK line found without any comment!");
                        }
					} else if ("COMPND".equalsIgnoreCase(cCol)) {						
                        String title = cRead.substring(10).trim();
                        oFile.setProperty(CDKConstants.TITLE, title);
					} 
					
					/*************************************************************
					 * Read connectivity information from CONECT records.
					 * Only covalent bonds are dealt with. Perhaps salt bridges
					 * should be dealt with in the same way..?
					 */
					else if (readConnect.isSet() && "CONECT".equalsIgnoreCase(cCol))	{
						cRead.trim();
						if (cRead.length() < 16) {
							logger.debug("Skipping unexpected empty CONECT line! : ", cRead);
						} else {
						
							String bondAtom = cRead.substring(7, 11).trim();
							int bondAtomNo = Integer.parseInt(bondAtom);
							String bondedAtom = cRead.substring(12, 16).trim();
							int bondedAtomNo = -1;
							
							try	{bondedAtomNo = Integer.parseInt(bondedAtom);}
							catch(Exception e)	{bondedAtomNo = -1;}
							
							if(bondedAtomNo != -1)	{
								addBond(oBP, bondAtomNo, bondedAtomNo);
								logger.warn("Bonded " + bondAtomNo + " with " + bondedAtomNo);
							}
							
							if(cRead.length() > 17)	{
								bondedAtom = cRead.substring(17, 21);
								bondedAtom = bondedAtom.trim();
								try	{bondedAtomNo = Integer.parseInt(bondedAtom);}
								catch(Exception e)	{bondedAtomNo = -1;}
								
								if(bondedAtomNo != -1)	{
									addBond(oBP, bondAtomNo, bondedAtomNo);
									logger.warn("Bonded " + bondAtomNo + " with " + bondedAtomNo);
								}
							}
							
							if(cRead.length() > 22)	{
								bondedAtom = cRead.substring(22, 26);
								bondedAtom = bondedAtom.trim();
								try	{bondedAtomNo = Integer.parseInt(bondedAtom);}
								catch(Exception e)	{bondedAtomNo = -1;}
								
								if(bondedAtomNo != -1)	{
									addBond(oBP, bondAtomNo, bondedAtomNo);
									logger.warn("Bonded " + bondAtomNo + " with " + bondedAtomNo);
								}
							}
							
							if(cRead.length() > 27)	{
								bondedAtom = cRead.substring(27, 31);
								bondedAtom = bondedAtom.trim();
								try	{bondedAtomNo = Integer.parseInt(bondedAtom);}
								catch(Exception e)	{bondedAtomNo = -1;}
								
								if(bondedAtomNo != -1)	{
									addBond(oBP, bondAtomNo, bondedAtomNo);
									logger.warn("Bonded " + bondAtomNo + " with " + bondedAtomNo);
								}
							}
						}
					}
					/*************************************************************/
					
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
		} catch (Exception e) {
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
	
	private void addBond(PDBPolymer obp, int bondAtomNo, int bondedAtomNo) {
		IAtom firstAtom = (PDBAtom)atomNumberMap.get(new Integer(bondAtomNo));
		IAtom secondAtom = (PDBAtom)atomNumberMap.get(new Integer(bondedAtomNo));
		if (firstAtom == null) {
			logger.error("Could not find bond start atom in map with serial id: ", bondAtomNo);
		}
		if (secondAtom == null) {
			logger.error("Could not find bond target atom in map with serial id: ", bondAtomNo);
		}
		IBond bond = firstAtom.getBuilder().newBond(
		                firstAtom, secondAtom, IBond.Order.SINGLE);
		for (int i = 0; i < bondsFromConnectRecords.size(); i++) {
		    IBond existingBond = 
		        (IBond) bondsFromConnectRecords.get(i);
		    IAtom a = existingBond.getAtom(0);
		    IAtom b = existingBond.getAtom(1);
		    if ((a == firstAtom && b == secondAtom) ||
		            (b == firstAtom && a == secondAtom)) {
		        // already stored
		        return;
		    }
		}
		bondsFromConnectRecords.add(bond);
		obp.addBond(bond);
	}
	
	private boolean createBondsWithRebondTool(IBioPolymer pol){
		RebondTool tool = new RebondTool(2.0, 0.5, 0.5);
		try {
//			 configure atoms
		      AtomTypeFactory factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/jmol_atomtypes.txt", 
		    	  pol.getBuilder());
		      java.util.Iterator atoms = pol.atoms().iterator();
		      while (atoms.hasNext()) {
		    	  IAtom atom = (IAtom)atoms.next();
		        try {
		        	IAtomType[] types = factory.getAtomTypes(atom.getSymbol());
		        	if (types.length > 0) {
		        		// just pick the first one
		        		AtomTypeManipulator.configure(atom, types[0]);
		        	} else {
		        		logger.warn("Could not configure atom with symbol: "+ atom.getSymbol());
		        	}
				} catch (Exception e) {
					logger.warn("Could not configure atom (but don't care): " + e.getMessage());
					logger.debug(e);
				}
		      }
			tool.rebond(pol);
		} catch (Exception e) {
			logger.error("Could not rebond the polymer: " + e.getMessage());
			logger.debug(e);
		}
		return true;
	}

	/**
	 * Creates an <code>Atom</code> and sets properties to their values from
	 * the ATOM record. If the line is shorter than 80 characters, the information
	 * past 59 characters is treated as optional. If the line is shorter than 59
	 * characters, a <code>RuntimeException</code> is thrown.
	 *
	 * @param cLine  the PDB ATOM record.
	 * @return the <code>Atom</code> created from the record.
	 * @throws RuntimeException if the line is too short (less than 59 characters).
	 */
	private PDBAtom readAtom(String cLine, int lineLength) {
		// a line looks like:
		// 01234567890123456789012345678901234567890123456789012345678901234567890123456789
		// ATOM      1  O5*   C A   1      20.662  36.632  23.475  1.00 10.00      114D  45
  		// ATOM   1186 1H   ALA     1      10.105   5.945  -6.630  1.00  0.00      1ALE1288

		if (lineLength < 59) {
			throw new RuntimeException("PDBReader error during readAtom(): line too short");
		}
		String elementSymbol = cLine.substring(12, 14).trim();
		
		if (elementSymbol.length() == 2) {
			// ensure that the second char is lower case
			elementSymbol = elementSymbol.charAt(0) + elementSymbol.substring(1).toLowerCase();
		}
		String rawAtomName = cLine.substring(12, 16).trim();
		String resName = cLine.substring(17, 20).trim();
		try {
			IAtomType type = pdbFactory.getAtomType(resName+"."+rawAtomName);
			elementSymbol = type.getSymbol();
		} catch (NoSuchAtomTypeException e) {
			logger.error("Did not recognize PDB atom type: " + resName+"."+rawAtomName);
		}
		PDBAtom oAtom = new PDBAtom(elementSymbol, 
			new Point3d(Double.parseDouble(cLine.substring(30, 38)),
			            Double.parseDouble(cLine.substring(38, 46)),
			            Double.parseDouble(cLine.substring(46, 54))
			)
		);
		
        oAtom.setRecord(cLine);
        oAtom.setSerial(Integer.parseInt(cLine.substring(6, 11).trim()));
        oAtom.setName(rawAtomName.trim());
        oAtom.setAltLoc(cLine.substring(16, 17).trim());
        oAtom.setResName(resName);
        oAtom.setChainID(cLine.substring(21, 22).trim());
        oAtom.setResSeq(cLine.substring(22, 26).trim());
        oAtom.setICode(cLine.substring(26, 27).trim());
        oAtom.setAtomTypeName(oAtom.getResName()+"."+rawAtomName);
		if (lineLength >= 59) {
            String frag = cLine.substring(54, 60).trim();
            if (frag.length() > 0) {
                oAtom.setOccupancy(Double.parseDouble(frag));
            }
		}
		if (lineLength >= 65) {
            String frag = cLine.substring(60, 66).trim();
            if (frag.length() > 0) {
                oAtom.setTempFactor(Double.parseDouble(frag));
            }
		}
		if (lineLength >= 75) {
            oAtom.setSegID(cLine.substring(72, 76).trim());
		}
//		if (lineLength >= 78) {
//            oAtom.setSymbol((new String(cLine.substring(76, 78))).trim());
//		}
		if (lineLength >= 79) {
            String frag = cLine.substring(78, 80).trim();
            if (frag.length() > 0) {
                oAtom.setCharge(Double.parseDouble(frag));
            }
		}
		
		/*************************************************************************************
		 * It sets a flag in the property content of an atom,
		 * which is used when bonds are created to check if the atom is an OXT-record => needs
		 * special treatment.
		 */
		String oxt = cLine.substring(13, 16).trim();
		
		if(oxt.equals("OXT"))	{
			oAtom.setOxt(true);
		}
		else	{
			oAtom.setOxt(false);
		}
		/*************************************************************************************/
		
		return oAtom;
	}
	
	@TestMethod("testClose")
  public void close() throws IOException {
		_oInput.close();
	}

    private void initIOSettings() {
    	useRebondTool = new BooleanIOSetting("UseRebondTool", IOSetting.LOW,
          "Should the PDBReader deduce bonding patterns?", 
          "false");
        readConnect = new BooleanIOSetting("ReadConnectSection", IOSetting.LOW,
          "Should the CONECT be read?",
          "true");
    }
    
    public void customizeJob() {
        fireIOSettingQuestion(useRebondTool);
        fireIOSettingQuestion(readConnect);
    }

    public IOSetting[] getIOSettings() {
        IOSetting[] settings = new IOSetting[2];
        settings[0] = useRebondTool;
        settings[1] = readConnect;
        return settings;
    }

}
