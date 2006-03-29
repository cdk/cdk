/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.qsar.descriptors.atomic;

import java.io.IOException;

import javax.vecmath.Point3d;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.IDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.tools.LoggingTool;
/**
 *  Inductive atomic softness of an atom in a polyatomic system can be defined
 *  as charge delocalizing ability. <p>
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
 *@cdk.created    2004-11-03
 *@cdk.module     qsar
 *@cdk.set        qsar-descriptors
 * @cdk.dictref qsar-descriptors:atomicSoftness
 */
public class InductiveAtomicSoftnessDescriptor implements IDescriptor {

	private int atomPosition = 0;
	private LoggingTool logger;
	private AtomTypeFactory factory = null;


	/**
	 *  Constructor for the InductiveAtomicSoftnessDescriptor object
	 *
	 *@exception  IOException             Description of the Exception
	 *@exception  ClassNotFoundException  Description of the Exception
	 */
	public InductiveAtomicSoftnessDescriptor() throws IOException, ClassNotFoundException {
		logger = new LoggingTool(this);
	}


	/**
	 *  Gets the specification attribute of the InductiveAtomicSoftnessDescriptor
	 *  object
	 *
	 *@return    The specification value
	 */
	public DescriptorSpecification getSpecification() {
		return new DescriptorSpecification(
				"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#atomicSoftness",
				this.getClass().getName(),
				"$Id$",
				"The Chemistry Development Kit");
	}


	/**
	 *  Sets the parameters attribute of the InductiveAtomicSoftnessDescriptor
	 *  object
	 *
	 *@param  params            The parameter is the atom position
	 *@exception  CDKException  Possible Exceptions
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException("InductiveAtomicSoftnessDescriptor only expects one parameter");
		}
		if (!(params[0] instanceof Integer)) {
			throw new CDKException("The parameter must be of type Integer");
		}
		atomPosition = ((Integer) params[0]).intValue();
	}


	/**
	 *  Gets the parameters attribute of the InductiveAtomicSoftnessDescriptor
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
	 *@exception  CDKException  if any atom in the supplied AtomContainer has no 3D coordinates
	 */
        public DescriptorValue calculate(IAtomContainer ac) throws CDKException {
		if (factory == null)
            try {
                factory = AtomTypeFactory.getInstance(
                    "org/openscience/cdk/config/data/jmol_atomtypes.txt", 
                    ac.getBuilder()
                );
            } catch (Exception exception) {
                throw new CDKException("Could not instantiate AtomTypeFactory!", exception);
            }


        	org.openscience.cdk.interfaces.IAtom[] allAtoms = null;
            org.openscience.cdk.interfaces.IAtom target = null;
            double atomicSoftness = 0;
            double radiusTarget = 0;
            target = ac.getAtomAt(atomPosition);
            allAtoms = ac.getAtoms();
            atomicSoftness = 0;
            double partial = 0;
            double radius = 0;
            String symbol = null;
            IAtomType type = null;
            try {
                symbol = ac.getAtomAt(atomPosition).getSymbol();
                type = factory.getAtomType(symbol);
                radiusTarget = type.getCovalentRadius();
            } catch (Exception ex1) {
                logger.debug(ex1);
                throw new CDKException("Problems with AtomTypeFactory due to " + ex1.toString(), ex1);
            }

            for (int i = 0; i < allAtoms.length; i++) {

                if (target.getPoint3d() == null || allAtoms[i].getPoint3d() == null) {
                    throw new CDKException("The target atom or atom "+i+" had no 3D coordinates. These are required");
                }
                if (!target.equals(allAtoms[i])) {
                    partial = 0;
                    symbol = allAtoms[i].getSymbol();
                    try {
                        type = factory.getAtomType(symbol);
                    } catch (Exception ex1) {
                        logger.debug(ex1);
                        throw new CDKException("Problems with AtomTypeFactory due to " + ex1.toString(), ex1);
                    }

                    radius = type.getCovalentRadius();
                    partial += radius * radius;
                    partial += (radiusTarget * radiusTarget);
                    partial = partial / (calculateSquareDistanceBetweenTwoAtoms(allAtoms[i], target));
                    //System.out.println("SOFT: atom "+symbol+", radius "+radius+", distance "+calculateSquareDistanceBetweenTwoAtoms(allAtoms[i], target));
                    atomicSoftness += partial;
                }
            }

            atomicSoftness = 2 * atomicSoftness;
            atomicSoftness = atomicSoftness * 0.172;
            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(atomicSoftness));
        }


	/**
	 *  Description of the Method.
	 *
	 *@param  atom1  Description of the Parameter
	 *@param  atom2  Description of the Parameter
	 *@return        Description of the Return Value
	 */
	private double calculateSquareDistanceBetweenTwoAtoms(org.openscience.cdk.interfaces.IAtom atom1, org.openscience.cdk.interfaces.IAtom atom2) {
		double distance = 0;
		double tmp = 0;
		Point3d firstPoint = atom1.getPoint3d();
		Point3d secondPoint = atom2.getPoint3d();
		tmp = firstPoint.distance(secondPoint);
		distance = tmp * tmp;
		return distance;
	}


	/**
	 *  Gets the parameterNames attribute of the InductiveAtomicSoftnessDescriptor  object.
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "atomPosition";
		return params;
	}


	/**
	 *  Gets the parameterType attribute of the InductiveAtomicSoftnessDescriptor object.
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		return new  Integer(0);
	}
}

