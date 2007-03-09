/* $Revision: 7981 $ $Author: egonw $ $Date: 2007-02-20 17:05:37 +0000 (Tue, 20 Feb 2007) $
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
 * 
 * @author Ola Spjuth <ola.spjuth@farmbio.uu.se>
 *
 */
public class MDMoleculeConvention extends CMLCoreModule {

	private MDMolecule currentMDMolecule;
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
		
		super.startElement(xpath, uri, local, raw, atts);

		if ("molecule".equals(local)) {

			DICTREF = atts.getValue("dictRef") != null ? atts.getValue("dictRef") : "";
			// the copy the parsed content into a new MDMolecule
			if (atts.getValue("convention") != null &&
				atts.getValue("convention").equals("md:mdMolecule")) {
				System.out.println("creating a MDMolecule");
//				super.startElement(xpath, uri, local, raw, atts);
				currentMolecule = new MDMolecule(currentMolecule);
				currentMDMolecule = (MDMolecule)currentMolecule;
			} else {
				//If residue or chargeGroup, set up a new one
				if (DICTREF.equals("md:chargeGroup")){
					System.out.println("CG ** Creating a new charge group...");
					currentMolecule = new ChargeGroup();
					currentChargeGroup = (ChargeGroup)currentMolecule;
				} else if (DICTREF.equals("md:residue")){
					System.out.println("RES ** Creating a new residue: " + atts.getValue("title"));
					currentMolecule = new Residue();
					currentResidue = (Residue)currentMolecule;
					currentResidue.setName(atts.getValue("title"));
				}
			}
		} 

	}

	/**
	 * Finish up parsing of elements in mdmolecule
	 */
	public void endElement(CMLStack xpath, String uri, String name, String raw) {
		if (name.equals("molecule")){

			//FIXME: Last element should be an MDMolecule but is a ChargeGroup!
			//Very strange, since I save a MDMolecule (confirmed)
			
			//End CG
			if (currentMolecule instanceof ChargeGroup) {
				ChargeGroup cg=(ChargeGroup)currentMolecule;
				System.out.println("CG ** Ending element charge group: " + cg.getNumber());
				((MDMolecule)currentMDMolecule).addChargeGroup(cg);
			}
			
			//End residue
			if (currentMolecule instanceof Residue) {
				Residue res=(Residue)currentMolecule;
				System.out.println("RES ** Ending element residue: " + res.getName());
				((MDMolecule)currentMDMolecule).addResidue(res);
			}
			
			//End the MDMolecule
			else if (currentMolecule instanceof MDMolecule){
				System.out.println("Ending MDMolecule");
				//Convert currentMolecule into an MDMolecule
				currentMolecule=new MDMolecule(currentMolecule);
				((MDMolecule)currentMolecule).setResidues(currentMDMolecule.getResidues());
				((MDMolecule)currentMolecule).setChargeGroups(currentMDMolecule.getChargeGroups());
			}
		}
		
		//We have a scalar element. Now check who it belongs to
		else if ("scalar".equals(name)) {			
			//Residue number
			if (DICTREF.equals("md:resNumber")){
				int myInt=Integer.parseInt(currentChars);
				currentResidue.setNumber(myInt);
				System.out.println("Ending scalar resNumber: " + currentChars );
			}
			//ChargeGroup number
			else if (DICTREF.equals("md:cgNumber")){
				int myInt=Integer.parseInt(currentChars);
				currentChargeGroup.setNumber(myInt);
				System.out.println("Ending scalar cgNumber" + currentChars);
			}
		}

		//Check atoms for md dictref
		else if ("atom".equals(name)) {
			//Switching Atom
			if (DICTREF.equals("md:switchingAtom")){
				//Set current atom as switching atom
				currentChargeGroup.setSwitchingAtom(currentAtom);
				System.out.println("Setting switching atom to " + currentAtom.toString());
			}
		}

		//We always want to endElement for super object
		super.endElement(xpath, uri, name, raw);
	}

}
