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
import org.openscience.cdk.tools.*;
import org.openscience.cdk.exception.CDKException;
import java.lang.Math;
import java.util.Map;
import java.util.Hashtable;

/**
 *   Vertex adjacency information (magnitude): 
 *   1 + log2 m where m is the number of heavy-heavy bonds. If m is zero, then zero is returned.
 *   (definition from MOE tutorial on line) 
 *
 * @author      mfe4
 * @cdk.created 2004-11-03
 * @cdk.module  qsar
 * @cdk.set     qsar-descriptors
 */
public class VAdjMaDescriptor implements Descriptor {

	/**
	 *  Constructor for the VAdjMaDescriptor object
	 */
	public VAdjMaDescriptor() { }


	/**
	 *  Gets the specification attribute of the VAdjMaDescriptor object
	 *
	 *@return    The specification value
	 */
	public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://qsar.sourceforge.net/dicts/qsar-descriptors:vAdjMa",
		    this.getClass().getName(),
		    "$Id$",
            "The Chemistry Development Kit");
	}


	/**
	 *  Sets the parameters attribute of the VAdjMaDescriptor object
	 *
	 *@param  params            The new parameters value
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		// no parameters for this descriptor
	}


	/**
	 *  Gets the parameters attribute of the VAdjMaDescriptor object
	 *
	 *@return    The parameters value
	 */
	public Object[] getParameters() {
		// no parameters to return
		return (null);
	}


	/**
	 *  calculates the VAdjMa descriptor for an atom container
	 *
	 *@param  ac                AtomContainer
	 *@return                   VAdjMa
	 *@exception  CDKException  Possible Exceptions
	 */
	public Object calculate(AtomContainer ac) throws CDKException {
		MFAnalyser formula = new MFAnalyser(ac);
		int magnitude = formula.getHeavyAtoms().size();
		double vadjMa = 0;
		if (magnitude > 0) {
			vadjMa += (Math.log(magnitude) / Math.log(2)) + 1;
		}
		return new Double(vadjMa);
	}


	/**
	 *  Gets the parameterNames attribute of the VAdjMaDescriptor object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		// no param names to return
		return (null);
	}



	/**
	 *  Gets the parameterType attribute of the VAdjMaDescriptor object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		return (null);
	}
}

