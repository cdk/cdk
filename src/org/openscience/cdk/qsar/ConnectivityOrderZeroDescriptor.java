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
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.exception.CDKException;
import java.util.Map;
import java.util.Hashtable;
import java.util.ArrayList;

/**
 *  Connectivity index (order 0):
 *  http://www.edusoft-lc.com/molconn/manuals/400/chaptwo.html
 *  http://www.chemcomp.com/Journal_of_CCG/Features/descr.htm#KH
 *  returned values are:
 *  chi0 is the Atomic connectivity index (order 0),
 *  chi0_C is the Carbon connectivity index (order 0);
 *
 *@author         mfe4
 *@cdk.created    2004-11-03
 * @cdk.module	qsar
 */
public class ConnectivityOrderZeroDescriptor implements Descriptor {

	/**
	 *  Constructor for the ConnectivityOrderZeroDescriptor object
	 */
	public ConnectivityOrderZeroDescriptor() { }


	/**
	 *  Gets the specification attribute of the ConnectivityOrderZeroDescriptor object
	 *
	 *@return    The specification value
	 */
	public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://qsar.sourceforge.net/dicts/qsar-descriptors:chiValuesCOZ",
		    this.getClass().getName(),
		    "$Id$",
            "The Chemistry Development Kit");
	}


	/**
	 *  Sets the parameters attribute of the ConnectivityOrderZeroDescriptor object
	 *
	 *@param  params            The new parameters value
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		// no parameters for this descriptor
	}


	/**
	 *  Gets the parameters attribute of the ConnectivityOrderZeroDescriptor object
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
	 *@param  ac                AtomContainer
	 *@return                   chiValuesCOZ is an arrayList that contains chi0 and chi0_C
	 *@exception  CDKException  Possible Exceptions
	 */
	public Object calculate(AtomContainer ac) throws CDKException {
		ArrayList chiValuesCOZ = new ArrayList(2);
		double chi0 = 0;
		double chi0_C = 0;
		Atom[] atoms = ac.getAtoms();
		for (int i = 0; i < atoms.length; i++) {
			int atomDegree = 0;
			Atom[] neighboors = ac.getConnectedAtoms(atoms[i]);
			for (int a = 0; a < neighboors.length; a++) {
				if (!neighboors[a].getSymbol().equals("H")) {
					atomDegree += 1;
				}
			}
			if(atomDegree > 0) {
				if(atoms[i].getSymbol().equals("C")) {
					chi0_C += 1/(Math.sqrt(atomDegree));
				}
				chi0 += 1/(Math.sqrt(atomDegree));
			}
		}
		chiValuesCOZ.add(new Double(chi0));
		chiValuesCOZ.add(new Double(chi0_C));		
		return new ArrayList(chiValuesCOZ);
	}


	/**
	 *  Gets the parameterNames attribute of the ConnectivityOrderZeroDescriptor object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		// no param names to return
		return (null);
	}



	/**
	 *  Gets the parameterType attribute of the ConnectivityOrderZeroDescriptor object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		return (null);
	}
}

