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
import org.openscience.cdk.AtomType;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.result.*;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.graph.*;
import org.openscience.cdk.graph.matrix.*;

/**
 *  This class returns the number of not-Hs substituents of an atom, also defined as "atom degree".
 *
 *@author         mfe4
 *@cdk.created    2004-11-13
 *@cdk.module     qsar
 *@cdk.set        qsar-descriptors
 */
public class AtomDegreeDescriptor implements Descriptor {

	private int targetPosition = 0;
	private LoggingTool logger;
	
	/**
	 *  Constructor for the AtomDegreeDescriptor object
	 */
	public AtomDegreeDescriptor() {
		logger = new LoggingTool(this);
	}


	/**
	 *  Gets the specification attribute of the AtomDegreeDescriptor object
	 *
	 *@return    The specification value
	 */
	public DescriptorSpecification getSpecification() {
		return new DescriptorSpecification(
				"http://qsar.sourceforge.net/dicts/qsar-descriptors:atomDegree",
				this.getClass().getName(),
				"$Id$",
				"The Chemistry Development Kit");
	}


	/**
	 *  Sets the parameters attribute of the AtomDegreeDescriptor object
	 *
	 *@param  params            The parameter is the atom position
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException("AtomDegreeDescriptor only expects one parameter");
		}
		if (!(params[0] instanceof Integer)) {
			throw new CDKException("The parameter must be of type Integer");
		}
		targetPosition = ((Integer) params[0]).intValue();
	}


	/**
	 *  Gets the parameters attribute of the AtomDegreeDescriptor object
	 *
	 *@return    The parameters value
	 */
	public Object[] getParameters() {
		Object[] params = new Object[1];
		params[0] = new Integer(targetPosition);
		return params;
	}


	/**
	 *  This method calculates the number of not-Hs substituents of an atom.
	 *
	 *@param  container         Parameter is the atom container.
	 *@return                   The number of bonds on the shortest path between two atoms
	 *@exception  CDKException  Description of the Exception
	 */

	public DescriptorValue calculate(AtomContainer container) throws CDKException {
		int atomDegree = 0;
		Atom target = container.getAtomAt(targetPosition);
		Atom[] neighboors = container.getConnectedAtoms(target);
		for (int i =0; i< neighboors.length;i++) {
			if(!neighboors[i].getSymbol().equals("H")) atomDegree+=1;
		}
		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new IntegerResult(atomDegree));
	}


	/**
	 *  Gets the parameterNames attribute of the AtomDegreeDescriptor object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "The position of the target atom";
		return params;
	}


	/**
	 *  Gets the parameterType attribute of the AtomDegreeDescriptor object
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

