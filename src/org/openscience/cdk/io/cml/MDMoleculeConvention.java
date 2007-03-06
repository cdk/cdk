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

import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.libio.md.ChargeGroup;
import org.openscience.cdk.libio.md.MDMolecule;
import org.openscience.cdk.libio.md.Residue;
import org.xml.sax.Attributes;

/**
 * 
 * Implements a Convention for parsing an MDMolecule from CML
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

		String name = raw;
		if ("molecule".equals(name)) {

			//FIXME: implement this

			for (int j = 0; j < atts.getLength(); j++) {
				logger.debug("StartElement molecule");
				DICTREF = "";

				for (int i=0; i<atts.getLength(); i++) {
					String qname = atts.getQName(i);
					if (qname.equals("dictRef")) {
						DICTREF = atts.getValue(i);
						logger.debug(name, "->DICTREF found: ", atts.getValue(i));
					} else if (qname.equals("title")) {
						elementTitle = atts.getValue(i);
						logger.debug(name, "->TITLE found: ", atts.getValue(i));
					} else {
						logger.debug("Qname: ", qname);
					}
				}

				//If convention == mdmolecule, set up a new mdmolecule
				if (atts.getQName(j).equals("convention") && atts.getValue(j).equals("mdmolecule")) {
					currentMDMolecule = new MDMolecule();
				}

				//If residue or chargeGroup, set up a new one
				if (DICTREF.equals("md:chargeGroup")){
					currentChargeGroup=new ChargeGroup();
				}else if (DICTREF.equals("md:residue")){
					currentResidue=new Residue();
				}

			}
		}
		
		//We have a scalar element. Now check who it belongs to
		if ("scalar".equals(name)) {
			for (int j = 0; j < atts.getLength(); j++) {
				logger.debug("StartElement scalar");

				DICTREF = "";

				for (int i=0; i<atts.getLength(); i++) {
					String qname = atts.getQName(i);
					if (qname.equals("dictRef")) {
						DICTREF = atts.getValue(i);
						logger.debug(name, "->DICTREF found: ", atts.getValue(i));
					} else {
						logger.debug("Qname: ", qname);
					}
				}
				
				if (DICTREF.equals("md:resNumber")){
					//TODO
				}
				else if (DICTREF.equals("md:switchingAtom")){
					//TODO
				}
				
				
			}
		}

		
		//For all else, use super implementation
		else {
			super.startElement(xpath, uri, local, raw, atts);
		}


	}

	/**
	 * Finish up parsing of elements in mdmolecule
	 */
	public void endElement(CMLStack xpath, String uri, String name, String raw) {
		if(name.equals("mdmolecule")){
			System.out.println("Ending element mdmolecule");
			//FIXME: implement this

		}else {
			//For all else, use super implementation
			super.endElement(xpath, uri, name, raw);
		}
	}

}
