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
import org.openscience.cdk.Bond;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.exception.CDKException;
import java.util.Map;
import java.util.Hashtable;
import java.util.ArrayList;

/**
 *  Connectivity index (order 1):
 *  http://www.edusoft-lc.com/molconn/manuals/400/chaptwo.html
 *  http://www.chemcomp.com/Journal_of_CCG/Features/descr.htm#KH
 *  returned values are:
 *  chi1 is the Atomic connectivity index (order 1),
 *  chi1_C is the Carbon connectivity index (order 1);
 *
 * @author      mfe4
 * @cdk.created 2004-11-03
 * @cdk.module	qsar
 * @cdk.set     qsar-descriptors
 */
public class ConnectivityOrderOneDescriptor implements Descriptor {

	/**
	 *  Constructor for the ConnectivityOrderOneDescriptor object
	 */
	public ConnectivityOrderOneDescriptor() { }


	/**
	 *  Gets the specification attribute of the ConnectivityOrderOneDescriptor object
	 *
	 *@return    The specification value
	 */
	public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://qsar.sourceforge.net/dicts/qsar-descriptors:chiValuesCOO",
		    this.getClass().getName(),
		    "$Id$",
            "The Chemistry Development Kit");
	}


	/**
	 *  Sets the parameters attribute of the ConnectivityOrderOneDescriptor object
	 *
	 *@param  params            The new parameters value
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		// no parameters for this descriptor
	}


	/**
	 *  Gets the parameters attribute of the ConnectivityOrderOneDescriptor object
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
	 *@return                   chiValuesCOO is an arrayList that contains chi1 and chi1_C
	 *@exception  CDKException  Possible Exceptions
	 */
	public Object calculate(AtomContainer ac) throws CDKException {
		ArrayList chiValuesCOO = new ArrayList(2);
		ArrayList degrees = new ArrayList(2);
		double chi1 = 0;
		double chi1_C = 0;
		double val0 = 0;
		double val1 = 0;
		int atomDegree = 0;
		Bond[] bonds = ac.getBonds();
		Atom[] atoms = null;
		for (int b = 0; b < bonds.length; b++) {
			atoms = bonds[b].getAtoms();
			if ((!atoms[0].getSymbol().equals("H")) || (!atoms[1].getSymbol().equals("H"))) {
				degrees.clear();
				for (int a = 0; a < atoms.length; a++) {
					atomDegree = 0;
					Atom[] neighboors = ac.getConnectedAtoms(atoms[a]);
					for (int n = 0; n < neighboors.length; n++) {
						if (!neighboors[n].getSymbol().equals("H")) {
							atomDegree += 1;
						}
					}
					if(atomDegree > 0) {
						degrees.add(new Double(atomDegree));
					}
				}
				val0 = ( (Double)degrees.get(0) ).doubleValue();
				val1 = ( (Double)degrees.get(1) ).doubleValue();
				if((atoms[0].getSymbol().equals("C")) && (atoms[1].getSymbol().equals("C"))) {
					chi1_C += 1/(Math.sqrt(val0 * val1));
				}
				chi1 += 1/(Math.sqrt(val0 * val1));
			}
		}
		chiValuesCOO.add(new Double(chi1));
		chiValuesCOO.add(new Double(chi1_C));		
		return new ArrayList(chiValuesCOO);
	}


	/**
	 *  Gets the parameterNames attribute of the ConnectivityOrderOneDescriptor object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		// no param names to return
		return (null);
	}



	/**
	 *  Gets the parameterType attribute of the ConnectivityOrderOneDescriptor object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		return (null);
	}
}

