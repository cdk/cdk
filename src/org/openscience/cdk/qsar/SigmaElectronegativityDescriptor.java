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

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.charges.GasteigerMarsiliPartialCharges;
import org.openscience.cdk.qsar.result.*;
import java.util.Map;
import java.util.Hashtable;

/**
 *  Sigma electronegativity is given by X = a + bq + c(q*q)
 *
 * @author      mfe4
 * @cdk.created 2004-11-03
 * @cdk.module  qsar
 * @cdk.set     qsar-descriptors
 */
public class SigmaElectronegativityDescriptor implements Descriptor {

	private int atomPosition = 0;


	/**
	 *  Constructor for the SigmaElectronegativityDescriptor object
	 */
	public SigmaElectronegativityDescriptor() { }


	/**
	 *  Gets the specification attribute of the SigmaElectronegativityDescriptor
	 *  object
	 *
	 *@return    The specification value
	 */
	public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://qsar.sourceforge.net/dicts/qsar-descriptors:sigmaElectronegativity",
		    this.getClass().getName(),
		    "$Id$",
            "The Chemistry Development Kit");
	}


	/**
	 *  Sets the parameters attribute of the SigmaElectronegativityDescriptor
	 *  object
	 *
	 *@param  params            Atom position
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException("SigmaElectronegativityDescriptor only expects one parameter");
		}
		if (!(params[0] instanceof Integer)) {
			throw new CDKException("The parameter must be of type Integer");
		}
		atomPosition = ((Integer) params[0]).intValue();
	}


	/**
	 *  Gets the parameters attribute of the SigmaElectronegativityDescriptor
	 *  object
	 *
	 *@return    The parameters value
	 */
	public Object[] getParameters() {
		// return the parameters as used for the descriptor calculation
		Object[] params = new Object[1];
		params[0] = new Integer(atomPosition);
		return params;
	}


	/**
	 *  The method calculates the sigma electronegativity of a given atom
	 *  It is needed to call the addExplicitHydrogensToSatisfyValency method from the class tools.HydrogenAdder.
	 *
	 *@param  ac                AtomContainer
	 *@return                   return the sigma electronegativity
	 *@exception  CDKException  Possible Exceptions
	 */
	public DescriptorValue calculate(AtomContainer ac) throws CDKException {
		double sigmaElectronegativity = 0;
		Molecule mol = new Molecule(ac);
		GasteigerMarsiliPartialCharges peoe = new GasteigerMarsiliPartialCharges();
		try {
			peoe.assignGasteigerMarsiliPartialCharges(mol, true);
			double[] gasteigerFactors = peoe.assignGasteigerMarsiliFactors(mol);
			int stepSize = peoe.getStepSize();
			int start = (stepSize * (atomPosition) + atomPosition);
			sigmaElectronegativity = ((gasteigerFactors[start]) + (mol.getAtomAt(atomPosition).getCharge() * gasteigerFactors[start + 1]) + (gasteigerFactors[start + 2] * ((mol.getAtomAt(atomPosition).getCharge() * mol.getAtomAt(atomPosition).getCharge()))));
			return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(sigmaElectronegativity));
		} catch (Exception ex1) {
			throw new CDKException("Problems with GasteigerMarsiliPartialCharges due to " + ex1.toString());
		}
	}


	/**
	 *  Gets the parameterNames attribute of the SigmaElectronegativityDescriptor
	 *  object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "The position of the atom whose calculate sigma electronegativity";
		return params;
	}


	/**
	 *  Gets the parameterType attribute of the SigmaElectronegativityDescriptor
	 *  object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		Object[] paramTypes = new Object[1];
		paramTypes[0] = new Integer(1);
		return paramTypes;
	}
}

