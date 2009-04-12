/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2007  Ola Spjuth <ola.spjuth@farmbio.uu.se>
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
 *
 */

package org.openscience.cdk.io.cml;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.libio.md.ChargeGroup;
import org.openscience.cdk.libio.md.MDMolecule;
import org.openscience.cdk.libio.md.Residue;
import org.xml.sax.Attributes;

/**
 * 
 * Implements a Convention for parsing an MDMolecule from CML.
 * 
 * @cdk.module libiomd
 * @cdk.svnrev  $Revision$
 * 
 * @author Ola Spjuth <ola.spjuth@farmbio.uu.se>
 *
 */
public class MDMoleculeConvention extends CMLCoreModule {

	private Residue currentResidue;
	private ChargeGroup currentChargeGroup;
	
	public MDMoleculeConvention(IChemFile chemFile) {
		super(chemFile);
	}

	public MDMoleculeConvention(ICMLModule conv) {
		super(conv);
	}

	/**
	 * Add parsing of elements in mdmolecule:
	 * 
	 * mdmolecule
	 * 		chargeGroup
	 * 			id
	 * 			cgNumber
	 * 			atomArray
	 * 			switchingAtom
	 * 		residue
	 * 			id
	 * 			title
	 * 			resNumber
	 * 			atomArray
	 * 
	 * @cdk.todo The JavaDoc of this class needs to be converted into HTML
	 */
	public void startElement(CMLStack xpath, String uri, String local, String raw, Attributes atts) {
//		<molecule convention="md:mdMolecule"
//	          xmlns="http://www.xml-cml.org/schema"
//	          xmlns:md="http://www.bioclipse.org/mdmolecule">
//	  <atomArray>
//	    <atom id="a1" elementType="C"/>
//	    <atom id="a2" elementType="C"/>
//	  </atomArray>
//	  <molecule dictRef="md:chargeGroup" id="cg1">
//	    <scalar dictRef="md:cgNumber">5</scalar>
//	    <atomArray>
//	      <atom ref="a1"/>
//	      <atom ref="a2"><scalar dictRef="md:switchingAtom"/></atom>
//	    </atomArray>
//	  </molecule>
//	  <molecule dictRef="md:residue" id="r1" title="resName">
//	    <scalar dictRef="md:resNumber">3</scalar>
//	    <atomArray>
//	      <atom ref="a1"/>
//	      <atom ref="a2"/>
//	    </atomArray>
//	  </molecule>
//	</molecule>

		// let the CMLCore convention deal with things first

		if ("molecule".equals(local)) {

			// the copy the parsed content into a new MDMolecule
			if (atts.getValue("convention") != null &&
				atts.getValue("convention").equals("md:mdMolecule")) {
//				System.out.println("creating a MDMolecule");
				super.startElement(xpath, uri, local, raw, atts);
				currentMolecule = new MDMolecule(currentMolecule);
			} else {
				DICTREF = atts.getValue("dictRef") != null ? atts.getValue("dictRef") : "";
				//If residue or chargeGroup, set up a new one
				if (DICTREF.equals("md:chargeGroup")){
//					System.out.println("Creating a new charge group...");
					currentChargeGroup = new ChargeGroup();
				} else if (DICTREF.equals("md:residue")){
//					System.out.println("Creating a new residue group...");
					currentResidue = new Residue();
					if (atts.getValue("title")!=null)
						currentResidue.setName(atts.getValue("title"));
				}
			}
		} else 
		
		//We have a scalar element. Now check who it belongs to
		if ("scalar".equals(local)) {
			DICTREF = atts.getValue("dictRef");
			//Switching Atom
			if ("md:switchingAtom".equals(DICTREF)){
				//Set current atom as switching atom
				System.out.println("Adding Switching atom: " + currentAtom);
				currentChargeGroup.setSwitchingAtom(currentAtom);
			} else {
				super.startElement(xpath, uri, local, raw, atts);
			}
		}
		
		else if ("atom".equals(local)) {
			if (currentChargeGroup != null) {
				String id = atts.getValue("ref");
				if (id != null) {
					// ok, an atom is referenced; look it up
					currentAtom = null;
//					System.out.println("#atoms: " + currentMolecule.getAtomCount());
					for (IAtom nextAtom : currentMolecule.atoms()) {
						if (nextAtom.getID().equals(id)) {
							currentAtom = nextAtom; 
						}
					}
					if (currentAtom == null) {
						logger.error("Could not found the referenced atom '" + id + "' for this charge group!");
					} else {
						currentChargeGroup.addAtom(currentAtom);
					}
				}
			} else if (currentResidue != null) {
				String id = atts.getValue("ref");
				if (id != null) {
					// ok, an atom is referenced; look it up
					IAtom referencedAtom = null;
//					System.out.println("#atoms: " + currentMolecule.getAtomCount());
                    for (IAtom nextAtom : currentMolecule.atoms()) {
						if (nextAtom.getID().equals(id)) {
							referencedAtom = nextAtom; 
						}
					}
					if (referencedAtom == null) {
						logger.error("Could not found the referenced atom '" + id + "' for this residue!");
					} else {
						currentResidue.addAtom(referencedAtom);
					}
				}
			} else {
				// ok, fine, just add it to the currentMolecule
				super.startElement(xpath, uri, local, raw, atts);
			}
		}
		
		else {
			super.startElement(xpath, uri, local, raw, atts);
		}

	}

	/**
	 * Finish up parsing of elements in mdmolecule
	 */
	public void endElement(CMLStack xpath, String uri, String name, String raw) {
		if (name.equals("molecule")){
//			System.out.println("Ending element mdmolecule");
			// add chargeGroup, and then delete them
			if (currentChargeGroup != null) {
				if (currentMolecule instanceof MDMolecule) {
					((MDMolecule)currentMolecule).addChargeGroup(currentChargeGroup);
				} else {
					logger.error("Need to store a charge group, but the current molecule is not a MDMolecule!");
				}
				currentChargeGroup = null;
			} else 
			
			// add chargeGroup, and then delete them
			if (currentResidue != null) {
				if (currentMolecule instanceof MDMolecule) {
					((MDMolecule)currentMolecule).addResidue(currentResidue);
				} else {
					logger.error("Need to store a residue group, but the current molecule is not a MDMolecule!");
				}
				currentResidue = null;
			} else {
//				System.out.println("OK, that was the last end mdmolecule");
				super.endElement(xpath, uri, name, raw);
			}
		} else if ("atomArray".equals(name)) {
			if (xpath.length() == 2 && xpath.endsWith("molecule", "atomArray")) {
				storeAtomData();
			} else if (xpath.length() > 2 && xpath.endsWith("cml", "molecule", "atomArray")) {
				storeAtomData();
			}
		} else if ("bondArray".equals(name)) {
			if (xpath.length() == 2 && xpath.endsWith("molecule", "bondArray")) {
				storeBondData();
			} else if (xpath.length() > 2 && xpath.endsWith("cml", "molecule", "bondArray")) {
				storeBondData();
			}
		} else if ("scalar".equals(name)) {
			//Residue number
			if ("md:resNumber".equals(DICTREF)){
				int myInt=Integer.parseInt(currentChars);
				currentResidue.setNumber(myInt);
			}
			//ChargeGroup number
			else if ("md:cgNumber".equals(DICTREF)){
				int myInt=Integer.parseInt(currentChars);
				currentChargeGroup.setNumber(myInt);
			}
		} else {
			super.endElement(xpath, uri, name, raw);
		}
	}

}
