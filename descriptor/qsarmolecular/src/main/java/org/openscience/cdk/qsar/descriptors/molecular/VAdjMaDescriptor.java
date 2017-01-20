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
package org.openscience.cdk.qsar.descriptors.molecular;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.qsar.AbstractMolecularDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;

/**
 *   Vertex adjacency information (magnitude):
 *   1 + log2 m where m is the number of heavy-heavy bonds. If m is zero, then zero is returned.
 *   (definition from MOE tutorial on line)
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
 * Returns a single value named <i>vAdjMat</i>.
 *
 * @author      mfe4
 * @cdk.created 2004-11-03
 * @cdk.module  qsarmolecular
 * @cdk.githash
 * @cdk.dictref qsar-descriptors:vAdjMa
 */
public class VAdjMaDescriptor extends AbstractMolecularDescriptor implements IMolecularDescriptor {

    private static final String[] NAMES = {"VAdjMat"};

    /**
     *  Constructor for the VAdjMaDescriptor object
     */
    public VAdjMaDescriptor() {}

    /**
     *  Gets the specification attribute of the VAdjMaDescriptor object
     *
     *@return    The specification value
     */
    @Override
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification("http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#vAdjMa",
                this.getClass().getName(), "The Chemistry Development Kit");
    }

    /**
     *  Sets the parameters attribute of the VAdjMaDescriptor object
     *
     *@param  params            The new parameters value
     *@exception  CDKException  Description of the Exception
     */
    @Override
    public void setParameters(Object[] params) throws CDKException {
        // no parameters for this descriptor
    }

    /**
     *  Gets the parameters attribute of the VAdjMaDescriptor object
     *
     *@return    The parameters value
     */
    @Override
    public Object[] getParameters() {
        // no parameters to return
        return (null);
    }

    @Override
    public String[] getDescriptorNames() {
        return NAMES;
    }

    /**
     *  calculates the VAdjMa descriptor for an atom container
     *
     *@param  atomContainer                AtomContainer
     *@return                   VAdjMa

     */
    @Override
    public DescriptorValue calculate(IAtomContainer atomContainer) {

        int n = 0; // count all heavy atom - heavy atom bonds
        for (IBond bond : atomContainer.bonds()) {
            if (bond.getAtom(0).getAtomicNumber() != 1 && bond.getAtom(1).getAtomicNumber() != 1) {
                n++;
            }
        }

        double vadjMa = 0;
        if (n > 0) {
            vadjMa += (Math.log(n) / Math.log(2)) + 1;
        }
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(vadjMa),
                getDescriptorNames());
    }

    /**
     * Returns the specific type of the DescriptorResult object.
     * 
     * The return value from this method really indicates what type of result will
     * be obtained from the {@link org.openscience.cdk.qsar.DescriptorValue} object. Note that the same result
     * can be achieved by interrogating the {@link org.openscience.cdk.qsar.DescriptorValue} object; this method
     * allows you to do the same thing, without actually calculating the descriptor.
     *
     * @return an object that implements the {@link org.openscience.cdk.qsar.result.IDescriptorResult} interface indicating
     *         the actual type of values returned by the descriptor in the {@link org.openscience.cdk.qsar.DescriptorValue} object
     */
    @Override
    public IDescriptorResult getDescriptorResultType() {
        return new DoubleResult(0.0);
    }

    /**
     *  Gets the parameterNames attribute of the VAdjMaDescriptor object
     *
     *@return    The parameterNames value
     */
    @Override
    public String[] getParameterNames() {
        // no param names to return
        return (null);
    }

    /**
     *  Gets the parameterType attribute of the VAdjMaDescriptor object
     *
     *@param  name  Description of the Parameter
     *@return       The parameterType value
     */
    @Override
    public Object getParameterType(String name) {
        return (null);
    }
}
