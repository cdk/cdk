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

import javax.vecmath.Point3d;

import org.openscience.cdk.Atom;
import org.openscience.cdk.interfaces.AtomContainer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.result.DoubleResult;

/**
 *  This class returns the 3D distance between two atoms.
 *
 * <p>This descriptor uses these parameters:
 * <table border="1">
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td>targetPosition</td>
 *     <td>0</td>
 *     <td>The position of the first atom</td>
 *   </tr>
 *   <tr>
 *     <td>focusPosition</td>
 *     <td>0</td>
 *     <td>The position of the second atom</td>
 *   </tr>
 * </table>
 *
 *@author         mfe4
 *@cdk.created    2004-11-13
 *@cdk.module     qsar
 *@cdk.set        qsar-descriptors
 * @cdk.dictref qsar-descriptors:distanceToAtom
 */
public class DistanceToAtomDescriptor implements Descriptor {

	private int targetPosition = 0;
	private int focusPosition = 0;

	/**
	 *  Constructor for the DistanceToAtomDescriptor object
	 */
	public DistanceToAtomDescriptor() {}


	/**
	 *  Gets the specification attribute of the DistanceToAtomDescriptor object
	 *
	 *@return    The specification value
	 */
	public DescriptorSpecification getSpecification() {
		return new DescriptorSpecification(
				"http://qsar.sourceforge.net/dicts/qsar-descriptors:distanceToAtom",
				this.getClass().getName(),
				"$Id$",
				"The Chemistry Development Kit");
	}


	/**
	 *  Sets the parameters attribute of the DistanceToAtomDescriptor object
	 *
	 *@param  params            The parameter is the atom position
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 2) {
			throw new CDKException("DistanceToAtomDescriptor only expects two parameters");
		}
		if (!(params[0] instanceof Integer)) {
			throw new CDKException("The parameter must be of type Integer");
		}
		if (!(params[1] instanceof Integer)) {
			throw new CDKException("The parameter must be of type Integer");
		}
		targetPosition = ((Integer) params[0]).intValue();
		focusPosition = ((Integer) params[1]).intValue();
	}


	/**
	 *  Gets the parameters attribute of the DistanceToAtomDescriptor object
	 *
	 *@return    The parameters value
	 */
	public Object[] getParameters() {
		Object[] params = new Object[2];
		params[0] = new Integer(targetPosition);
		params[1] = new Integer(focusPosition);
		return params;
	}


	/**
	 *  This method calculate the 3D distance between two atoms.
	 *
	 *@param  container         Parameter is the atom container.
	 *@return                   The number of bonds on the shortest path between two atoms
	 *@exception  CDKException  Description of the Exception
	 */

	public DescriptorValue calculate(AtomContainer container) throws CDKException {
		double distanceToAtom = 0;
		org.openscience.cdk.interfaces.Atom target = container.getAtomAt(targetPosition);
		org.openscience.cdk.interfaces.Atom focus = container.getAtomAt(focusPosition);

                if (target.getPoint3d() == null || focus.getPoint3d() == null) {
                    throw new CDKException("Target or focus atom must have 3D coordinates.");
                }
		distanceToAtom = calculateDistanceBetweenTwoAtoms(target, focus);
		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(distanceToAtom));

	}

	// generic method for calculation of distance btw 2 atoms
	private double calculateDistanceBetweenTwoAtoms(org.openscience.cdk.interfaces.Atom atom1, org.openscience.cdk.interfaces.Atom atom2) {
		double distance = 0;
		Point3d firstPoint = atom1.getPoint3d();
		Point3d secondPoint = atom2.getPoint3d();
		distance = firstPoint.distance(secondPoint);
		return distance;
	}

	/**
	 *  Gets the parameterNames attribute of the DistanceToAtomDescriptor object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[2];
		params[0] = "The position of the target atom";
		params[1] = "The position of the focus atom";
		return params;
	}


	/**
	 *  Gets the parameterType attribute of the DistanceToAtomDescriptor object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		Object[] paramTypes = new Object[2];
		paramTypes[0] = new Integer(1);
		paramTypes[1] = new Integer(1);
		return paramTypes;
	}
}

