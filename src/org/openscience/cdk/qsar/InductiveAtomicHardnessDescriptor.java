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
 *  as published by the Free Hardware Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Hardware
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.qsar;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomType;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.qsar.result.*;
import org.openscience.cdk.tools.LoggingTool;
import javax.vecmath.*;
import java.io.*;
/**
 *  Inductive atomic hardness of an atom in a polyatomic system can be defined
 *  as the "resistance" to a change of the atomic charge. <p>
 *
 *  This descriptor uses these parameters:
 *  <tableborder="1">
 *
 *    <tr>
 *
 *      <td>
 *        Name
 *      </td>
 *
 *      <td>
 *        Default
 *      </td>
 *
 *      <td>
 *        Description
 *      </td>
 *
 *    </tr>
 *
 *    <tr>
 *
 *      <td>
 *        atomPosition
 *      </td>
 *
 *      <td>
 *        0
 *      </td>
 *
 *      <td>
 *        The position of the target atom
 *      </td>
 *
 *    </tr>
 *
 *  </table>
 *
 *
 *@author         mfe4
 *@created        18 aprile 2005
 *@cdk.created    2004-11-03
 *@cdk.module     qsar
 *@cdk.set        qsar-descriptors
 */
public class InductiveAtomicHardnessDescriptor implements Descriptor {

	private int atomPosition = 0;
	private LoggingTool logger;
	private AtomTypeFactory factory = null;


	/**
	 *  Constructor for the InductiveAtomicHardnessDescriptor object
	 *
	 *@exception  IOException             Description of the Exception
	 *@exception  ClassNotFoundException  Description of the Exception
	 */
	public InductiveAtomicHardnessDescriptor() throws IOException, ClassNotFoundException {
		logger = new LoggingTool(this);
		factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/jmol_atomtypes.txt");
	}


	/**
	 *  Gets the specification attribute of the InductiveAtomicHardnessDescriptor
	 *  object
	 *
	 *@return    The specification value
	 */
	public DescriptorSpecification getSpecification() {
		return new DescriptorSpecification(
				"http://qsar.sourceforge.net/dicts/qsar-descriptors:atomicHardness",
				this.getClass().getName(),
				"$Id$",
				"The Chemistry Development Kit");
	}


	/**
	 *  Sets the parameters attribute of the InductiveAtomicHardnessDescriptor
	 *  object
	 *
	 *@param  params            The parameter is the atom position
	 *@exception  CDKException  Possible Exceptions
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException("InductiveAtomicHardnessDescriptor only expects one parameter");
		}
		if (!(params[0] instanceof Integer)) {
			throw new CDKException("The parameter must be of type Integer");
		}
		atomPosition = ((Integer) params[0]).intValue();
	}


	/**
	 *  Gets the parameters attribute of the InductiveAtomicHardnessDescriptor
	 *  object
	 *
	 *@return    the position if the target atom
	 */
	public Object[] getParameters() {
		// return the parameters as used for the descriptor calculation
		Object[] params = new Object[1];
		params[0] = new Integer(atomPosition);
		return params;
	}


	/**
	 *  It is needed to call the addExplicitHydrogensToSatisfyValency method from
	 *  the class tools.HydrogenAdder, and 3D coordinates.
	 *
	 *@param  ac                AtomContainer
	 *@return                   a double with polarizability of the heavy atom
	 *@exception  CDKException  Possible Exceptions
	 */
	public DescriptorValue calculate(AtomContainer ac) throws CDKException {
		Atom[] allAtoms = null;
		Atom target = null;
		double atomicHardness = 0;

		double radiusTarget = 0;
		target = ac.getAtomAt(atomPosition);
		allAtoms = ac.getAtoms();
		atomicHardness = 0;
		double partial = 0;
		double radius = 0;
		String symbol = null;
		AtomType type = null;
		try {
			symbol = ac.getAtomAt(atomPosition).getSymbol();
			type = factory.getAtomType(symbol);
			radiusTarget = type.getCovalentRadius();
		} catch (Exception ex1) {
			logger.debug(ex1);
			throw new CDKException("Problems with AtomTypeFactory due to " + ex1.toString());
		}

		for (int i = 0; i < allAtoms.length; i++) {
   			if (!target.equals(allAtoms[i])) {
				partial = 0;
				symbol = allAtoms[i].getSymbol();
				
				try {
					type = factory.getAtomType(symbol);
				} catch (Exception ex1) {
					logger.debug(ex1);
					throw new CDKException("Problems with AtomTypeFactory due to " + ex1.toString());
				}
				radius = type.getCovalentRadius();
				partial += radius * radius;
				partial += (radiusTarget * radiusTarget);
				partial = partial / (calculateSquareDistanceBetweenTwoAtoms(target, allAtoms[i]));
				atomicHardness += partial;
			}
		}

		atomicHardness = 2 * atomicHardness;
		atomicHardness = atomicHardness * 0.172;
		atomicHardness = 1 / atomicHardness;
		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(atomicHardness));
	}


	/**
	 *  Description of the Method
	 *
	 *@param  atom1  Description of the Parameter
	 *@param  atom2  Description of the Parameter
	 *@return        Description of the Return Value
	 */
	private double calculateSquareDistanceBetweenTwoAtoms(Atom atom1, Atom atom2) {
		double distance = 0;
		double tmp = 0;
		Point3d firstPoint = atom1.getPoint3d();
		Point3d secondPoint = atom2.getPoint3d();
		tmp = firstPoint.distance(secondPoint);
		distance = tmp * tmp;
		return distance;
	}

	/**
	 *  Gets the parameterNames attribute of the InductiveAtomicHardnessDescriptor
	 *  object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "atomPosition";
		return params;
	}


	/**
	 *  Gets the parameterType attribute of the InductiveAtomicHardnessDescriptor
	 *  object
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

