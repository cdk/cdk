/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004  The Chemistry Development Kit (CDK) project
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
 */
package org.openscience.cdk.qsar;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Element;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import java.util.Map;
import java.util.Hashtable;

/**
 *  The number of hydrogen bond donors is defined by Daylight:
 *  (http://www.daylight.com/dayhtml_tutorials/languages/smarts/smarts_examples.html)
 *  [!H0;#7,#8,#9].
 *
 *@author     mfe4
 *@cdk.created    2004-11-03
 * @cdk.module qsar
 */
public class HBondDonorCountDescriptor implements Descriptor {
	
	private boolean checkAromaticity = false;


	/**
	 *  Constructor for the HBondDonorCountDescriptor object
	 */
	public HBondDonorCountDescriptor() { }


	/**
	 *  Gets the specification attribute of the HBondDonorCountDescriptor
	 *  object
	 *
	 *@return    The specification value
	 */
	public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://qsar.sourceforge.net/dicts/qsar-descriptors:hBondDonors",
		    this.getClass().getName(),
		    "$Id$",
            "The Chemistry Development Kit");
	}


	/**
	 *  Sets the parameters attribute of the HBondDonorCountDescriptor object
	 *
	 *@param  params            a boolean true means that aromaticity has to be checked
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException("HBondDonorCountDescriptor only expects less than two parameters");
		}
		if (!(params[0] instanceof Boolean)) {
			throw new CDKException("The parameter must be of type Boolean");
		}
		// ok, all should be fine
		checkAromaticity = ((Boolean) params[0]).booleanValue();
	}


	/**
	 *  Gets the parameters attribute of the HBondDonorCountDescriptor object
	 *
	 *@return    The parameters value
	 */
	public Object[] getParameters() {
		// return the parameters as used for the descriptor calculation
		Object[] params = new Object[1];
		params[0] = new Boolean(checkAromaticity);
		return params;
	}


	/**
	 *  The method calculates the number of H bond donors.
	 *
	 *@param  ac                AtomContainer
	 *@return                   number of H bond donors
	 *@exception  CDKException  Possible Exceptions
	 */
	public Object calculate(AtomContainer ac) throws CDKException {
		Molecule mol = new Molecule(ac);
		if (checkAromaticity) {
			HueckelAromaticityDetector.detectAromaticity(mol);
		}
		int hBondDonors = 0;
		int hcounter = 0;
		int atomicNumber = 0;
		String symbol = null;
		Atom[] atoms = mol.getAtoms();
		Atom[] neighboors = null;
		for (int i = 0; i < atoms.length; i++) {
			hcounter = 0;
			symbol = new String(atoms[i].getSymbol());
			atomicNumber = atoms[i].getAtomicNumber();
			neighboors = mol.getConnectedAtoms(atoms[i]);
			for (int n = 0; n < neighboors.length; n++) {
				if (neighboors[n].getSymbol().equals("H")) {
					hcounter += 1;
				} else if (atoms[i].getHydrogenCount() > 0) {
					hcounter += 1;
				} else {
					hcounter += 0;
				}
			}
			if(hcounter > 0) {
				if(symbol.equals("O")) {
					hBondDonors += 1;
				}
				else if(symbol.equals("N")){
					hBondDonors += 1;
				}
				else if(symbol.equals("F")){
					hBondDonors += 1;
				}
				else {
					hBondDonors += 0;
				}
			}
			else {
				hBondDonors += 0;
			}
		}
		return new Integer(hBondDonors);
	}


	/**
	 *  Gets the parameterNames attribute of the HBondDonorCountDescriptor
	 *  object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "If true: aromaticity has to be checked";
		return params;
	}



	/**
	 *  Gets the parameterType attribute of the HBondDonorCountDescriptor
	 *  object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		Object[] paramTypes = new Object[1];
		paramTypes[0] = new Boolean(true);
		return paramTypes;
	}
}

