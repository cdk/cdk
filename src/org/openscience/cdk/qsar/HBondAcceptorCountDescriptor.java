/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.qsar.result.*;
import java.util.Map;
import java.util.Hashtable;

/**
 * The number of hydrogen bond acceptors. It is defined by Daylight:
 * (http://www.daylight.com/dayhtml_tutorials/languages/smarts/smarts_examples.html)
 * "A H-bond acceptor is a heteroatom with no positive charge, note that negatively 
 * charged oxygen or sulphur are included. Excluded are halogens, including F, 
 * heteroaromatic oxygen, sulphur and pyrrole N. Higher oxidation levels of N,P,S are excluded. 
 * Note P(III) is currently included. Zeneca's work would imply that (O=S=O) shoud also be excluded".
 *
 * <p>This descriptor uses these parameters:
 * <table>
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td>checkAromaticity</td>
 *     <td>false</td>
 *     <td>True is the aromaticity has to be checked</td>
 *   </tr>
 * </table>
 *
 * @author      mfe4
 * @cdk.created 2004-11-03
 * @cdk.module  qsar
 * @cdk.set     qsar-descriptors
 */
public class HBondAcceptorCountDescriptor implements Descriptor {
	
	private boolean checkAromaticity = false;


	/**
	 *  Constructor for the HBondAcceptorCountDescriptor object
	 */
	public HBondAcceptorCountDescriptor() { }


	/**
	 *  Gets the specification attribute of the HBondAcceptorCountDescriptor
	 *  object
	 *
	 *@return    The specification value
	 */
	public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://qsar.sourceforge.net/dicts/qsar-descriptors:hBondacceptors",
		    this.getClass().getName(),
		    "$Id$",
            "The Chemistry Development Kit");
	}


	/**
	 *  Sets the parameters attribute of the HBondAcceptorCountDescriptor object
	 *
	 *@param  params            a boolean true means that aromaticity has to be checked
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException("HBondAcceptorCountDescriptor only expects less than two parameters");
		}
		if (!(params[0] instanceof Boolean)) {
			throw new CDKException("The parameter must be of type Boolean");
		}
		// ok, all should be fine
		checkAromaticity = ((Boolean) params[0]).booleanValue();
	}


	/**
	 *  Gets the parameters attribute of the HBondAcceptorCountDescriptor object
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
	 *  The method calculates the number of H bond acceptors.
	 *
	 *@param  ac                AtomContainer
	 *@return                   number of H bond acceptors
	 *@exception  CDKException  Possible Exceptions
	 */
	public DescriptorValue calculate(AtomContainer ac) throws CDKException {
		int hBondacceptors = 0;
		Molecule mol = new Molecule(ac);
		if (checkAromaticity) {
			HueckelAromaticityDetector.detectAromaticity(mol);
		}
		Atom[] atoms = mol.getAtoms();
		for (int i = 0; i < atoms.length; i++) {
			String symbol = new String(atoms[i].getSymbol());
			double charge = atoms[i].getCharge();
			double valence = atoms[i].getBondOrderSum();
			if((symbol != "C") && (symbol != "F") && (symbol != "Cl") && (symbol != "Br") && (symbol != "I")) {
				if(charge < 1.0) {
					if(symbol.equals("O")){
						if(!atoms[i].getFlag(CDKConstants.ISAROMATIC)) {
							hBondacceptors += 1;
						}
					}
					else if(symbol.equals("P")){ 
						if(valence != 5) {
							hBondacceptors += 1;
						}
					}
					else if(symbol.equals("S")){
						if(!atoms[i].getFlag(CDKConstants.ISAROMATIC)) {
							if((valence != 4) && (valence != 6)) {
								hBondacceptors += 1;
							}
						}
					}
					else if(symbol.equals("N")){ 
						if(atoms[i].getFlag(CDKConstants.ISAROMATIC)) {
							if(valence != 3) {
								hBondacceptors += 1;
							}
						}
						else {
							if(valence != 5) {
								hBondacceptors += 1;
							}
						}
					}
					else {
						hBondacceptors += 0;
					}
				}
			}
		}
		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new IntegerResult(hBondacceptors));
	}


	/**
	 *  Gets the parameterNames attribute of the HBondAcceptorCountDescriptor
	 *  object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "checkAromaticity";
		return params;
	}



	/**
	 *  Gets the parameterType attribute of the HBondAcceptorCountDescriptor
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

