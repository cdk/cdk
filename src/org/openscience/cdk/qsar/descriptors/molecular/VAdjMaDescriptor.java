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
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.qsar.descriptors.molecular;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.IDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.tools.MFAnalyser;

/**
 *   Vertex adjacency information (magnitude): 
 *   1 + log2 m where m is the number of heavy-heavy bonds. If m is zero, then zero is returned.
 *   (definition from MOE tutorial on line) 
 *
 * @author      mfe4
 * @cdk.created 2004-11-03
 * @cdk.module  qsar
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:vAdjMa
 */
public class VAdjMaDescriptor implements IDescriptor {

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
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#vAdjMa",
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
	 *@param  atomContainer                AtomContainer
	 *@return                   VAdjMa
	 *@exception  CDKException  Possible Exceptions
	 */
	public DescriptorValue calculate(IAtomContainer atomContainer) throws CDKException {
		MFAnalyser formula = new MFAnalyser(atomContainer);
		int magnitude = formula.getHeavyAtoms().size();
		double vadjMa = 0;
		if (magnitude > 0) {
			vadjMa += (Math.log(magnitude) / Math.log(2)) + 1;
		}
		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(vadjMa));
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

