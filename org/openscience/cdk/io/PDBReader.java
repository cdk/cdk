/* PDBReader.java
 * 
 * $ author: 	Edgar Luttmann 			$ 
 * $ contact: 	edgar@uni-paderborn.de 	$
 * $ date: 		2001-08-06 				$
 * 
 * Copyright (C) 1997-2001  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  */
package org.openscience.cdk.io;

import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.exception.*;
import java.io.*;
import java.util.*;
import javax.vecmath.*;

/**
 *
 * Reads the contents of a PDBFile.
 *
 * @author     Edgar Luttmann
 * @created    2001-08-06 
 *
 */
public class PDBReader implements CDKConstants, ChemObjectReader {

	private BufferedReader _oInput; // The internal used BufferedReader

	/**
	 *
	 * Contructs a new PDBReader that can read Molecules from a given 
	 * InputStream.
	 *
	 * @param oIn  The InputStream to read from
	 *
	 */
	public PDBReader(InputStream oIn) {
		_oInput = new BufferedReader(new InputStreamReader(oIn));
	}

	/**
	 *
	 * Takes an object which subclasses ChemObject, e.g. Molecule, and will 
	 * read this (from file, database, internet etc). If the specific 
	 * implementation does not support a specific ChemObject it will throw 
	 * an Exception.
	 *
	 * @param oObj  The object that subclasses ChemObject
	 * @return   	The ChemObject read  
	 * @exception   UnsupportedChemObjectException  
	 *
	 */
   public ChemObject read(ChemObject oObj) throws UnsupportedChemObjectException {
		if (oObj instanceof ChemFile) {
			return (ChemObject)readChemFile();
		} else {
			throw new UnsupportedChemObjectException("Only supported are ChemFile.");
		}
	}

	/**
	 *
	 * Read a ChemFile from a file in PDB format.
	 *
	 * @return The ChemFile that was read from the PDB file.    
	 *
	 */
	private ChemFile readChemFile() 	{
		// initialize all containers
		ChemFile oFile = new ChemFile();
		ChemSequence oSeq = new ChemSequence();
		ChemModel oModel = new ChemModel();
		SetOfMolecules oSet = new SetOfMolecules();
		
		// some variables needed
		StringBuffer cLine;
		String cCol;
		Atom oAtom;
		BioPolymer oBP = new BioPolymer();
		StringBuffer cResidue;
		Object oObj;
		Monomer oMonomer;
		String cRead;
		
		// do the reading of the Input		
		try {
			do {
				cRead = _oInput.readLine();
				if (cRead != null) {
					cLine = new StringBuffer(cRead);
					// make sure the record name is 6 characters long
					while (cLine.length() < 6) {
						cLine.append(" ");
					}
					// check the first column to decide what to do
					cCol = cLine.substring(0,6).toUpperCase();
					if (cCol.equals("ATOM  ") || cCol.equals("HETATM")) {
						// read an atom record
						oAtom = readAtom(cLine.toString());
						
						// construct a string describing the residue
						cResidue = new StringBuffer(8);
						oObj = oAtom.getPhysicalProperty("pdb.resName");
						if (oObj != null) {
							cResidue = cResidue.append(((String)oObj).trim());
						}
						oObj = oAtom.getPhysicalProperty("pdb.chainID");
						if (oObj != null) {
							cResidue = cResidue.append(((String)oObj).trim());
						}
						oObj = oAtom.getPhysicalProperty("pdb.resSeq");
						if (oObj != null) {
							cResidue = cResidue.append(((String)oObj).trim());
						}
						
						// search for the existing monomer or create a new one
						oMonomer = oBP.getMonomer(cResidue.toString());
						if (oMonomer == null) {
							oMonomer = new Monomer();
							oMonomer.setMonomerName(cResidue.toString());
							oMonomer.setMonomerType((String)oAtom.getPhysicalProperty("resName"));
						}
						
						// add the atom
						oBP.addAtom(oAtom, oMonomer);
					} else if (cCol.equals("TER   ")) {
						// finish the molecule and construct a new one
						oSet.addMolecule(oBP);
						oBP = new BioPolymer();
					} else if (cCol.equals("END   ")) {
						// finish the molecule and construct a new one
						oSet.addMolecule(oBP);
						oBP = new BioPolymer();					
	//				} else if (cCol.equals("USER  ")) {
	//						System.out.println(cLine);
	//				} else if (cCol.equals("MODEL ")) {
	//					System.out.println(cLine);
	//				} else if (cCol.equals("ENDMDL")) {
	//					System.out.println(cLine);
					}
				}
			} while (_oInput.ready());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// try to close the Input
		try {
			_oInput.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		// Set all the dependencies
		oModel.setSetOfMolecules(oSet);
		oSeq.addChemModel(oModel);
		oFile.addChemSequence(oSeq);
		
		return oFile;
	}

	/**
	 *
	 * Constructs a Atom object and sets properties due to their values from
	 * the PDB record.
	 *
	 * @param cLine  The actual read PDB record
	 * @return The Atom object constructed from the informations within the actual line.
	 *
	 */
	private Atom readAtom(String cLine) {
		Atom oAtom = new Atom(new Element(cLine.substring(12, 14).trim()), 
									 new Point3d(new Double(cLine.substring(30, 38)).doubleValue(),
									 				 new Double(cLine.substring(38, 46)).doubleValue(),
									 				 new Double(cLine.substring(46, 54)).doubleValue()));
		oAtom.setPhysicalProperty("pdb.name", (new String(cLine.substring(12, 16))).trim());
		oAtom.setPhysicalProperty("pdb.altLoc", (new String(cLine.substring(16, 17))).trim());
		oAtom.setPhysicalProperty("pdb.resName", (new String(cLine.substring(17, 20))).trim());
		oAtom.setPhysicalProperty("pdb.chainID", (new String(cLine.substring(21, 22))).trim());
		oAtom.setPhysicalProperty("pdb.resSeq", (new String(cLine.substring(22, 26))).trim());
		return oAtom;
		}
}
