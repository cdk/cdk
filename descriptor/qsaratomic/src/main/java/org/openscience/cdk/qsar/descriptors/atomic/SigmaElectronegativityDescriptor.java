/*  Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.charges.Electronegativity;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.AbstractAtomicDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * Atomic descriptor that reflects that Gasteiger-Marsili sigma electronegativity.
 * The used approach is given by <pre>X = a + bq + c(q*q)</pre> where a, b, and c are
 * the Gasteiger-Marsili parameters and q is the sigma charge. For the actual
 * calculation it uses the {@link Electronegativity} class.
 *
 * <p>This descriptor uses these parameters:
 * <table border="1">
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td>maxIterations</td>
 *     <td>0</td>
 *     <td>Number of maximum iterations</td>
 *   </tr>
 * </table>
 *
 * @author      mfe4
 * @cdk.created 2004-11-03
 * @cdk.module  qsaratomic
 * @cdk.githash
 * @cdk.dictref qsar-descriptors:sigmaElectronegativity
 * @see Electronegativity
 */
public class SigmaElectronegativityDescriptor extends AbstractAtomicDescriptor implements IAtomicDescriptor {

    /**Number of maximum iterations*/
    private int                   maxIterations   = 0;

    private static final String[] NAMES = {"elecSigmA"};

    private Electronegativity     electronegativity;

    /**
     *  Constructor for the SigmaElectronegativityDescriptor object
     */
    public SigmaElectronegativityDescriptor() {
        electronegativity = new Electronegativity();
    }

    /**
     *  Gets the specification attribute of the SigmaElectronegativityDescriptor
     *  object
     *
     *@return    The specification value
     */
    @Override
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#sigmaElectronegativity", this
                        .getClass().getName(), "The Chemistry Development Kit");
    }

    /**
     *  Sets the parameters attribute of the SigmaElectronegativityDescriptor
     *  object
     *
     *@param  params            1: max iterations (optional, defaults to 20)
     *@exception  CDKException  Description of the Exception
     */
    @Override
    public void setParameters(Object[] params) throws CDKException {
        if (params.length > 1) {
            throw new CDKException("SigmaElectronegativityDescriptor only expects one parameter");
        }
        if (!(params[0] instanceof Integer)) {
            throw new CDKException("The parameter must be of type Integer");
        }
        if (params.length == 0) return;
        maxIterations = (Integer) params[0];
    }

    /**
     *  Gets the parameters attribute of the SigmaElectronegativityDescriptor
     *  object
     *
     *@return    The parameters value
     */
    @Override
    public Object[] getParameters() {
        // return the parameters as used for the descriptor calculation
        Object[] params = new Object[1];
        params[0] = maxIterations;
        return params;
    }

    @Override
    public String[] getDescriptorNames() {
        return NAMES;
    }

    /**
     *  The method calculates the sigma electronegativity of a given atom
     *  It is needed to call the addExplicitHydrogensToSatisfyValency method from the class tools.HydrogenAdder.
     *
     *@param  atom              The IAtom for which the DescriptorValue is requested
     *@param  ac                AtomContainer
     *@return                   return the sigma electronegativity
     */
    @Override
    public DescriptorValue calculate(IAtom atom, IAtomContainer ac) {

        IAtomContainer clone;
        IAtom localAtom;
        try {
            clone = (IAtomContainer) ac.clone();
            localAtom = clone.getAtom(ac.getAtomNumber(atom));
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(clone);
        } catch (CDKException e) {
            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(
                    Double.NaN), NAMES, e);
        } catch (CloneNotSupportedException e) {
            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(
                    Double.NaN), NAMES, e);
        }

        if (maxIterations != -1 && maxIterations != 0) electronegativity.setMaxIterations(maxIterations);

        double result = electronegativity.calculateSigmaElectronegativity(clone, localAtom);

        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(result),
                                   NAMES);
    }

    /**
     *  Gets the parameterNames attribute of the SigmaElectronegativityDescriptor
     *  object
     *
     *@return    The parameterNames value
     */
    @Override
    public String[] getParameterNames() {
        String[] params = new String[1];
        params[0] = "maxIterations";
        return params;
    }

    /**
     *  Gets the parameterType attribute of the SigmaElectronegativityDescriptor
     *  object
     *
     * @param  name  Description of the Parameter
     * @return       An Object of class equal to that of the parameter being requested
     */
    @Override
    public Object getParameterType(String name) {
        return 0;
    }
}
