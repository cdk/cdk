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

import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
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
 *        
 *      </td>
 *
 *      <td>
 *        
 *      </td>
 *
 *      <td>
 *        no parameters
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
public class InductiveAtomicSoftnessDescriptor implements IAtomicDescriptor {

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
     * This descriptor does have any parameter.
     */
    public void setParameters(Object[] params) throws CDKException {
    }


	/**
	 *  Gets the parameters attribute of the InductiveAtomicSoftnessDescriptor
	 *  object
	 *
	 * @return    The parameters value
     * @see #setParameters
     */
    public Object[] getParameters() {
        return new Object[0];
    }


	/**
	 *  It is needed to call the addExplicitHydrogensToSatisfyValency method from
	 *  the class tools.HydrogenAdder, and 3D coordinates.
	 *
	 *@param  atom              The IAtom for which the DescriptorValue is requested
     *@param  ac                AtomContainer
	 *@return                   a double with polarizability of the heavy atom
	 *@exception  CDKException  if any atom in the supplied AtomContainer has no 3D coordinates
	 */
        public DescriptorValue calculate(IAtom atom, IAtomContainer ac) throws CDKException {
		if (factory == null)
            try {
                factory = AtomTypeFactory.getInstance(
                    "org/openscience/cdk/config/data/jmol_atomtypes.txt", 
                    ac.getBuilder()
                );
            } catch (Exception exception) {
                throw new CDKException("Could not instantiate AtomTypeFactory!", exception);
            }


        	IAtom[] allAtoms = ac.getAtoms();
            IAtom target = atom;
            double atomicSoftness = 0;
            double radiusTarget = 0;
            
            atomicSoftness = 0;
            double partial = 0;
            double radius = 0;
            String symbol = null;
            IAtomType type = null;
            try {
                symbol = target.getSymbol();
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
	 * @return    The parameterNames value
	 */
	public String[] getParameterNames() {
        return new String[0];
    }


	/**
	 *  Gets the parameterType attribute of the InductiveAtomicSoftnessDescriptor object.
	 *
	 * @param  name  Description of the Parameter
     * @return       An Object of class equal to that of the parameter being requested
     */
    public Object getParameterType(String name) {
        return null;
    }
}

