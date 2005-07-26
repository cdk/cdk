/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2005  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.tools;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Ring;
import org.openscience.cdk.RingSet;
import org.openscience.cdk.CDKChemicalRingConstants;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.HOSECodeGenerator;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;


/**
* AtomTypeTools is a helper class for assigning atom types to an atom
*
* @author         cho
* @cdk.created    2005-18-07
* @cdk.module     experimental
*/

public class AtomTypeTools {
	
	private LoggingTool logger;
	HOSECodeGenerator hcg=null;
	SmilesGenerator sg=null;
	
	/**
	 * Constructor for the MMFF94AtomTypeMatcher object.
	 */
	public AtomTypeTools() {
		logger = new LoggingTool(this);
		hcg = new HOSECodeGenerator();
		sg=new SmilesGenerator();
	}
	
	/**
	 *  Method assigns certain properties to an atom. Necessary for the atom type matching 
	 *  Properties:
	 *  	- aromaticity)
	 *		- ChemicalGroup (CDKChemicalRingGroupConstant)
	 *			- sssr
	 *			- Ring/Group, ringSize, aromaticity
	 *			- SphericalMatcher (HoSe Code)
	 *
	 *@return                molecule
	 *@exception  Exception  Description of the Exception
	 */
	public Molecule assignAtomTypePropertiesToAtom(Molecule molecule) throws Exception{
		//System.out.println("assignAtomTypePropertiesToAtom Start ...");
		logger.debug("assignAtomTypePropertiesToAtom Start ...");
		Atom atom = null;
		String hoseCode = "";
		RingSet ringSetA = null;
		RingSet ringSetMolecule = new SSSRFinder(molecule).findSSSR();
		
		try {
			HueckelAromaticityDetector.detectAromaticity(molecule);
		} catch (Exception cdk1) {
			//System.out.println("AROMATICITYError: Cannot determine aromaticity due to: " + cdk1.toString());
			logger.error("AROMATICITYError: Cannot determine aromaticity due to: " + cdk1.toString());
		}

		for (int i = 0; i < molecule.getAtomCount(); i++) {
			atom = molecule.getAtomAt(i);
			//Atom aromatic is set by HueckelAromaticityDetector
			//Atom in ring?
			if (ringSetMolecule.contains(atom)) {
				ringSetA = ringSetMolecule.getRings(atom);
				RingSetManipulator.sort(ringSetA);
				Ring sring = (Ring) ringSetA.lastElement();
				atom.setRingSize(sring.getRingSize());
				atom.setChemicalGroupConstant(ringSystemClassifier(sring, sg.createSMILES(new Molecule(sring))));
				
			}else{
				atom.setChemicalGroupConstant(CDKChemicalRingConstants.ISNOT_IN_RING);
			}
			try {
				hoseCode = hcg.getHOSECode(molecule, atom, 3);
				hoseCode=removeAromaticityFlagsFromHoseCode(hoseCode);
				atom.setSphericalMatcher(hoseCode);
			} catch (CDKException ex1) {
				throw new CDKException("Could not build HOSECode from atom "+ i + " due to " + ex1.toString());
			}
		}
		return molecule;
	}
	

	/**
	 *  Identifies ringSystem and returns a number which corresponds to 
	 *  CDKChemicalRingConstant
	 *
	 *@param  ring	Ring class with the ring system  
	 *@param  smile  smile of the ring system
	 *@return     chemicalRingConstant
	 */
	private int ringSystemClassifier(Ring ring, String smile) {
		
		if (smile.equals("c1ccnc1"))return 4;
		else if (smile.equals("c1ccoc1"))return 6;
		else if (smile.equals("c1ccsc1"))return 8;
		else if (smile.equals("c1ccncc1"))return 10;
		else if (smile.equals("c1cncnc1"))return 12;
		else if (smile.equals("cccccc"))return 5;
								
		for (int i=0; i<ring.getAtomCount();i++){
			if (ring.getAtomAt(i).getSymbol()!="C"){
				return 2;
			}
		}
		return 3;
	}
	
	public String removeAromaticityFlagsFromHoseCode(String hoseCode){
		String hosecode="";
		for (int i=0;i<hoseCode.length();i++){
			if (hoseCode.charAt(i)!= '*'){
				hosecode=hosecode+hoseCode.charAt(i);
			}
		}
		return hosecode;
	}
}