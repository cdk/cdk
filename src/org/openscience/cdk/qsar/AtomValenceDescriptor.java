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
import java.util.Hashtable;

/**
 *  This class return the valence of an atom.
 *
 *@author         mfe4
 *@created        24 febbraio 2005
 *@cdk.created    2004-11-13
 *@cdk.module     qsar
 *@cdk.set        qsar-descriptors
 */
public class AtomValenceDescriptor implements Descriptor {

	private int targetPosition = 0;
	private LoggingTool logger;
	public Hashtable valencesTable;
	
	/**
	 *  Constructor for the AtomValenceDescriptor object
	 */
	public AtomValenceDescriptor() {
		logger = new LoggingTool(this);
		if (valencesTable == null) { 
		valencesTable = new Hashtable();
		valencesTable.put("Li", new Integer(1));
		valencesTable.put("Be", new Integer(2));
		valencesTable.put("B", new Integer(3));
		valencesTable.put("C", new Integer(4));
		valencesTable.put("N", new Integer(5));
		valencesTable.put("O", new Integer(6));
		valencesTable.put("F", new Integer(7));
		valencesTable.put("Na", new Integer(1));
		valencesTable.put("Mg", new Integer(2));
		valencesTable.put("Al", new Integer(3));
		valencesTable.put("Si", new Integer(4));
		valencesTable.put("P", new Integer(5));
		valencesTable.put("S", new Integer(6));
		valencesTable.put("Cl", new Integer(7));
		valencesTable.put("K", new Integer(1));
		valencesTable.put("Ca", new Integer(2));
		valencesTable.put("Ga", new Integer(3));
		valencesTable.put("Ge", new Integer(4));
		valencesTable.put("As", new Integer(5));
		valencesTable.put("Se", new Integer(6));
		valencesTable.put("Br", new Integer(7));
		valencesTable.put("Rb", new Integer(1));
		valencesTable.put("Sr", new Integer(2));
		valencesTable.put("In", new Integer(3));
		valencesTable.put("Sn", new Integer(4));
		valencesTable.put("Sb", new Integer(5));
		valencesTable.put("Te", new Integer(6));
		valencesTable.put("I", new Integer(7));
		valencesTable.put("Cs", new Integer(1));
		valencesTable.put("Ba", new Integer(2));
		valencesTable.put("Tl", new Integer(3));
		valencesTable.put("Pb", new Integer(4));
		valencesTable.put("Bi", new Integer(5));
		valencesTable.put("Po", new Integer(6));
		valencesTable.put("At", new Integer(7));
		valencesTable.put("Fr", new Integer(1));
		valencesTable.put("Ra", new Integer(2));	    
	    }
	}


	/**
	 *  Gets the specification attribute of the AtomValenceDescriptor object
	 *
	 *@return    The specification value
	 */
	public DescriptorSpecification getSpecification() {
		return new DescriptorSpecification(
				"http://qsar.sourceforge.net/dicts/qsar-descriptors:atomValence",
				this.getClass().getName(),
				"$Id$",
				"The Chemistry Development Kit");
	}


	/**
	 *  Sets the parameters attribute of the AtomValenceDescriptor object
	 *
	 *@param  params            The parameter is the atom position
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException("AtomValenceDescriptor only expects one parameter");
		}
		if (!(params[0] instanceof Integer)) {
			throw new CDKException("The parameter must be of type Integer");
		}
		targetPosition = ((Integer) params[0]).intValue();
	}


	/**
	 *  Gets the parameters attribute of the AtomValenceDescriptor object
	 *
	 *@return    The parameters value
	 */
	public Object[] getParameters() {
		Object[] params = new Object[1];
		params[0] = new Integer(targetPosition);
		return params;
	}


	/**
	 *  This method calculates the valence of an atom.
	 *
	 *@param  container         Parameter is the atom container.
	 *@return                   The valence of an atom
	 *@exception  CDKException  Description of the Exception
	 */

	public DescriptorValue calculate(AtomContainer container) throws CDKException {
		int atomValence = 0;
		String symbol = container.getAtomAt(targetPosition).getSymbol();
		atomValence = ((Integer)valencesTable.get(symbol)).intValue();
		return new DescriptorValue(getSpecification(), getParameters(), new IntegerResult(atomValence));
	}


	/**
	 *  Gets the parameterNames attribute of the AtomValenceDescriptor object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "The position of the target atom";
		return params;
	}


	/**
	 *  Gets the parameterType attribute of the AtomValenceDescriptor object
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

