/*  $RCSfile$
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
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.result.*;
import org.openscience.cdk.graph.*;
import org.openscience.cdk.graph.matrix.*;


/**
 *  Wiener path number: half the sum of all the distance matrix entries; Wiener
 *  polarity number: half the sum of all the distance matrix entries with a
 *  value of 3. For more informations, see
 *  http://www.chemcomp.com/Journal_of_CCG/Features/descr.htm#KH or
 *  http://www.csam.montclair.edu/~burch/bpnew.pdf.
 *
 *@author         mfe4
 *@cdk.created        December 7, 2004
 *@cdk.created    2004-11-03
 *@cdk.module     qsar
 *@cdk.set        qsar-descriptors
 */
public class WienerNumbersDescriptor implements Descriptor {

	/**
	 *  Constructor for the WienerNumbersDescriptor object
	 */
	public WienerNumbersDescriptor() { }


	/**
	 *  Gets the specification attribute of the WienerNumbersDescriptor object
	 *
	 *@return    The specification value
	 */
	public DescriptorSpecification getSpecification() {
		return new DescriptorSpecification(
				"http://qsar.sourceforge.net/dicts/qsar-descriptors:wienerNumbers",
				this.getClass().getName(),
				"$Id$",
				"The Chemistry Development Kit");
	}


	/**
	 *  Sets the parameters attribute of the WienerNumbersDescriptor object
	 *
	 *@param  params            The new parameters value
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		// no parameters for this descriptor
	}


	/**
	 *  Gets the parameters attribute of the WienerNumbersDescriptor object
	 *
	 *@return    The parameters value
	 */
	public Object[] getParameters() {
		return (null);
		// no parameters to return
	}


	/**
	 *  Description of the Method
	 *
	 *@param  atomContainer                AtomContainer
	 *@return                   wiener numbers as array of 2 doubles
	 *@exception  CDKException  Possible Exceptions
	 */
	public DescriptorResult calculate(AtomContainer atomContainer) throws CDKException {
		double wienerPathNumber = 0; //weinerPath
		double wienerPolarityNumber = 0; //weinerPol
		ConnectionMatrix connectionMatrix = new ConnectionMatrix();
		double[][] matr = connectionMatrix.getMatrix(atomContainer);
		DoubleArrayResult wienerNumbers = new DoubleArrayResult(2);
		PathTools pathTools = new PathTools();
		int[][] distances = pathTools.computeFloydAPSP(matr);
		int partial = 0;
		for (int i = 0; i < distances.length; i++) {
			wienerPolarityNumber = 0;
			for (int j = 0; j < distances.length; j++) {
				partial = distances[i][j];
				wienerPathNumber += partial;
				if (partial == 3) {
					wienerPolarityNumber += 1;
				}
			}
		}
		wienerPathNumber = wienerPathNumber / 2;

		wienerNumbers.add(wienerPathNumber);
		wienerNumbers.add(wienerPolarityNumber);
		return wienerNumbers;
	}

	/**
	 *  Gets the parameterNames attribute of the WienerNumbersDescriptor object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		// no param names to return
		return (null);
	}



	/**
	 *  Gets the parameterType attribute of the WienerNumbersDescriptor object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		return (null);
	}
}

