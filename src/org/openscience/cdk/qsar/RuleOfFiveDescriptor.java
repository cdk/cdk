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

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.RingSet;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.qsar.result.*;
import java.util.Map;
import java.util.Hashtable;
import org.openscience.cdk.tools.HydrogenAdder;

/**
 * This Class contains a method that returns the number failures of the Lipinski's Rule Of 5..
 * See http://www.lifechemicals.com/eng/services/HTS/five/.
 * @author      mfe4
 * @cdk.created 2004-11-03
 * @cdk.module  qsar
 * @cdk.set     qsar-descriptors
 */
public class RuleOfFiveDescriptor implements Descriptor {
	private boolean checkAromaticity = false;


	/**
	 *  Constructor for the RuleOfFiveDescriptor object
	 */
	public RuleOfFiveDescriptor() { }


	/**
	 *  Gets the specification attribute of the RuleOfFiveDescriptor object
	 *
	 *@return    The specification value
	 */
	public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://qsar.sourceforge.net/dicts/qsar-descriptors:lipinskifailures",
		    this.getClass().getName(),
		    "$Id$",
            "The Chemistry Development Kit");
	}


	/**
	 *  Sets the parameters attribute of the RuleOfFiveDescriptor object
	 *
	 *@param  params            Parameter is only one: a boolean.
	 *@exception  CDKException  Possible Exceptions
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException("RuleOfFiveDescriptor only expects one parameter");
		}
		if (!(params[0] instanceof Boolean)) {
			throw new CDKException("The first parameter must be of type Boolean");
		}
		// ok, all should be fine
		checkAromaticity = ((Boolean) params[0]).booleanValue();
	}


	/**
	 *  Gets the parameters attribute of the RuleOfFiveDescriptor object
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
	 *  the method take a boolean checkAromaticity: if the boolean is true, it means that
	 *  aromaticity has to be checked.
	 *
	 *@param  ac                atom container
	 *@return                   number of aromatic bonds in the atom container
	 *@exception  CDKException  Possible Exceptions
	 */
	public DescriptorResult calculate(AtomContainer mol) throws CDKException {
		
		int lipinskifailures = 0;
		
		Descriptor xlogP = new XLogPDescriptor();
		Object[] xlogPparams = {new Boolean(checkAromaticity)};
		xlogP.setParameters(xlogPparams);
		double xlogPvalue = ((DoubleResult)xlogP.calculate(mol)).doubleValue();
		
		Descriptor acc = new HBondAcceptorCountDescriptor();
		acc.setParameters(xlogPparams);
		int acceptors = ((IntegerResult)acc.calculate(mol)).intValue();
		
		Descriptor don = new HBondDonorCountDescriptor();
		don.setParameters(xlogPparams);
		int donors = ((IntegerResult)don.calculate(mol)).intValue();
		
		Descriptor mw = new WeightDescriptor();
		Object[] mwparams = {new String("")};
		mw.setParameters(mwparams);
		double mwvalue = ((DoubleResult)mw.calculate(mol)).doubleValue();
		
		Descriptor rotata = new RotatableBondsCountDescriptor();
		rotata.setParameters(xlogPparams);
		int rotatablebonds = ((IntegerResult)rotata.calculate(mol)).intValue();
		
		if(xlogPvalue > 5.0) { lipinskifailures += 1; }
		if(acceptors > 10) { lipinskifailures += 1; }
		if(donors > 5) { lipinskifailures += 1; }
		if(mwvalue > 500.0) { lipinskifailures += 1; }
		if(rotatablebonds > 10.0) { lipinskifailures += 1; }
		
		return new IntegerResult(lipinskifailures);
	}


	/**
	 *  Gets the parameterNames attribute of the RuleOfFiveDescriptor
	 *  object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "True is the aromaticity has to be checked";
		return params;
	}



	/**
	 *  Gets the parameterType attribute of the RuleOfFiveDescriptor object
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

