/*  Copyright (C)      2005  Matteo Floris <mfe4@users.sf.net>
 *                     2006  Kai Hartmann <kaihartmann@users.sf.net>
 *                     2006  Miguel Rojas-Cherto <miguelrojasch@users.sf.net>
 *                2005-2008  Egon Willighagen <egonw@users.sf.net>
 *                2008-2009  Rajarshi Guha <rajarshi@users.sf.net>
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
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.AbstractAtomicDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.result.IntegerResult;

import java.util.List;

/**
 * This class returns the number of not-Hs substituents of an atom, also defined as "atom degree".
 *
 * <p>This descriptor uses these parameters:
 * <table border="1">
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td></td>
 *     <td></td>
 *     <td>no parameters</td>
 *   </tr>
 * </table>
 *
 * @author      mfe4
 * @cdk.created 2004-11-13
 * @cdk.module  qsaratomic
 * @cdk.githash
 * @cdk.dictref qsar-descriptors:atomDegree
 */
public class AtomDegreeDescriptor extends AbstractAtomicDescriptor implements IAtomicDescriptor {

    @Override
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#atomDegree", this.getClass()
                        .getName(), "The Chemistry Development Kit");
    }

    /**
     * This descriptor does not have any parameter to be set.
     */
    @Override
    public void setParameters(Object[] params) throws CDKException {
        // no parameters
    }

    /**
     *  Gets the parameters attribute of the AtomDegreeDescriptor object.
     *
     *@return    The parameters value
     *@see #setParameters
     */
    @Override
    public Object[] getParameters() {
        return null;
    }

    @Override
    public String[] getDescriptorNames() {
        return new String[]{"aNeg"};
    }

    /**
     * This method calculates the number of not-H substituents of an atom.
     *
     * @param  atom              The IAtom for which the DescriptorValue is requested
     * @param  container         The {@link IAtomContainer} for which this descriptor is to be calculated for
     * @return   The number of bonds on the shortest path between two atoms
     */
    @Override
    public DescriptorValue calculate(IAtom atom, IAtomContainer container) {
        int atomDegree = 0;
        List<IAtom> neighboors = container.getConnectedAtomsList(atom);
        for (IAtom neighboor : neighboors) {
            if (!neighboor.getSymbol().equals("H")) atomDegree += 1;
        }
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new IntegerResult(
                atomDegree), getDescriptorNames());
    }

    /**
     * Gets the parameterNames attribute of the AtomDegreeDescriptor object.
     *
     * @return    The parameterNames value
     */
    @Override
    public String[] getParameterNames() {
        return new String[0];
    }

    /**
     * Gets the parameterType attribute of the AtomDegreeDescriptor object.
     *
     * @param  name  Description of the Parameter
     * @return       An Object of class equal to that of the parameter being requested
     */
    @Override
    public Object getParameterType(String name) {
        return null;
    }
}
