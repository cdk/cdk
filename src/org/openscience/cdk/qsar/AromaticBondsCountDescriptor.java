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
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.RingSet;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.qsar.result.*;
import java.util.Map;
import java.util.Hashtable;

/**
 * This Class contains a method that returns the number of aromatic atoms in an AtomContainer.
 *
 * @author      mfe4
 * @cdk.created 2004-11-03
 * @cdk.module  qsar
 * @cdk.set     qsar-descriptors
 */
public class AromaticBondsCountDescriptor implements Descriptor {
	private boolean checkAromaticity = false;


	/**
	 *  Constructor for the AromaticBondsCountDescriptor object
	 */
	public AromaticBondsCountDescriptor() { }


	/**
	 *  Gets the specification attribute of the AromaticBondsCountDescriptor object
	 *
	 *@return    The specification value
	 */
	public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://qsar.sourceforge.net/dicts/qsar-descriptors:aromaticBondsCount",
		    this.getClass().getName(),
		    "$Id$",
            "The Chemistry Development Kit");
	}


	/**
	 *  Sets the parameters attribute of the AromaticBondsCountDescriptor object
	 *
	 *@param  params            Parameter is only one: a boolean.
	 *@exception  CDKException  Possible Exceptions
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException("AromaticBondsCountDescriptor only expects one parameter");
		}
		if (!(params[0] instanceof Boolean)) {
			throw new CDKException("The first parameter must be of type Boolean");
		}
		// ok, all should be fine
		checkAromaticity = ((Boolean) params[0]).booleanValue();
	}


	/**
	 *  Gets the parameters attribute of the AromaticBondsCountDescriptor object
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
	public DescriptorResult calculate(AtomContainer ac) throws CDKException {
		int aromaticBondsCount = 0;
		if (checkAromaticity) {
			RingSet rs = (new AllRingsFinder()).findAllRings(ac);
			HueckelAromaticityDetector.detectAromaticity(ac, rs, true);
		}
		Bond[] bonds = ac.getBonds();
		for (int i = 0; i < bonds.length; i++) {
			if (ac.getBondAt(i).getFlag(CDKConstants.ISAROMATIC)) {
				aromaticBondsCount += 1;
			}
		}
		return new IntegerResult(aromaticBondsCount);
	}


	/**
	 *  Gets the parameterNames attribute of the AromaticBondsCountDescriptor
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
	 *  Gets the parameterType attribute of the AromaticBondsCountDescriptor object
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

