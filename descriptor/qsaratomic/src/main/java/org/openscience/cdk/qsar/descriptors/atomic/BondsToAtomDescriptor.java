/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ShortestPaths;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.AbstractAtomicDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.result.IntegerResult;

/**
 *  This class returns the number of bonds on the shortest path between two atoms.
 *
 * <p>This descriptor uses these parameters:
 * <table border="1">
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td>focusPosition</td>
 *     <td>0</td>
 *     <td>The position of the second atom</td>
 *   </tr>
 * </table>
 *
 * @author         mfe4
 * @cdk.created    2004-11-13
 * @cdk.module     qsaratomic
 * @cdk.githash
 * @cdk.set        qsar-descriptors
 * @cdk.dictref    qsar-descriptors:bondsToAtom
 */
public class BondsToAtomDescriptor extends AbstractAtomicDescriptor implements IAtomicDescriptor {

    private int focusPosition = 0;

    /**
     *  Constructor for the BondsToAtomDescriptor object
     */
    public BondsToAtomDescriptor() {}

    /**
     *  Gets the specification attribute of the BondsToAtomDescriptor object
     *
     *@return    The specification value
     */
    @Override
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#bondsToAtom", this.getClass()
                        .getName(), "The Chemistry Development Kit");
    }

    /**
     *  Sets the parameters attribute of the BondsToAtomDescriptor object
     *
     * @param  params            The parameter is the position to focus
     * @exception  CDKException  Description of the Exception
     */
    @Override
    public void setParameters(Object[] params) throws CDKException {
        if (params.length > 1) {
            throw new CDKException("BondsToAtomDescriptor only expects one parameters");
        }
        if (!(params[0] instanceof Integer)) {
            throw new CDKException("The parameter must be of type Integer");
        }
        focusPosition = (Integer) params[0];
    }

    /**
     *  Gets the parameters attribute of the BondsToAtomDescriptor object
     *
     *@return    The parameters value
     */
    @Override
    public Object[] getParameters() {
        Object[] params = new Object[1];
        params[0] = focusPosition;
        return params;
    }

    @Override
    public String[] getDescriptorNames() {
        return new String[]{"bondsToAtom"};
    }

    /**
     *  This method calculate the number of bonds on the shortest path between two atoms.
     *
     *@param  atom              The IAtom for which the DescriptorValue is requested
     *@param  container         Parameter is the atom container.
     *@return                   The number of bonds on the shortest path between two atoms
     */

    @Override
    public DescriptorValue calculate(IAtom atom, IAtomContainer container) {

        IAtom focus = container.getAtom(focusPosition);

        // could be cached
        int bondsToAtom = new ShortestPaths(container, atom).distanceTo(focus);

        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new IntegerResult(
                bondsToAtom), getDescriptorNames());

    }

    /**
     *  Gets the parameterNames attribute of the BondsToAtomDescriptor object
     *
     *@return    The parameterNames value
     */
    @Override
    public String[] getParameterNames() {
        String[] params = new String[1];
        params[0] = "focusPosition";
        return params;
    }

    /**
     *  Gets the parameterType attribute of the BondsToAtomDescriptor object
     *
     *@param  name  Description of the Parameter
     *@return       The parameterType value
     */
    @Override
    public Object getParameterType(String name) {
        return 0;
    }
}
