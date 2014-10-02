/* Copyright (C) 2004-2007  Rajarshi Guha <rajarshi@users.sourceforge.net>
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

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.AbstractMolecularDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;

import java.util.List;

/**
 * Zagreb index: the sum of the squares of atom degree over all heavy atoms i.
 *
 * @author      mfe4
 * @cdk.created 2004-11-03
 * @cdk.module  qsarmolecular
 * @cdk.githash
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:zagrebIndex
 *
 * @cdk.keyword Zagreb index
 * @cdk.keyword descriptor
 */
@TestClass("org.openscience.cdk.qsar.descriptors.molecular.ZagrebIndexDescriptorTest")
public class ZagrebIndexDescriptor extends AbstractMolecularDescriptor implements IMolecularDescriptor {

    private static final String[] NAMES = {"Zagreb"};

    /**
     *  Constructor for the ZagrebIndexDescriptor object.
     */
    public ZagrebIndexDescriptor() {}

    /**
     *  Gets the specification attribute of the ZagrebIndexDescriptor object.
     *
     *@return    The specification value
     */
    @TestMethod("testGetSpecification")
    @Override
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#zagrebIndex", this.getClass()
                        .getName(), "The Chemistry Development Kit");
    }

    /**
     *  Sets the parameters attribute of the ZagrebIndexDescriptor object.
     *
     *@param  params            The new parameters value
     *@exception  CDKException  Description of the Exception
         *@see #getParameters
     */
    @TestMethod("testSetParameters_arrayObject")
    @Override
    public void setParameters(Object[] params) throws CDKException {
        // no parameters for this descriptor
    }

    /**
     *  Gets the parameters attribute of the ZagrebIndexDescriptor object.
     *
     *@return    The parameters value
         *@see #setParameters
     */
    @TestMethod("testGetParameters")
    @Override
    public Object[] getParameters() {
        return (null);
        // no parameters to return
    }

    @TestMethod(value = "testNamesConsistency")
    @Override
    public String[] getDescriptorNames() {
        return NAMES;
    }

    /**
     *  Evaluate the Zagreb Index for a molecule.
     *
     *@param  atomContainer                AtomContainer
     *@return                   zagreb index
     */
    @TestMethod("testCalculate_IAtomContainer")
    @Override
    public DescriptorValue calculate(IAtomContainer atomContainer) {
        double zagreb = 0;
        for (IAtom atom : atomContainer.atoms()) {
            if (atom.getSymbol().equals("H")) continue;
            int atomDegree = 0;
            List<IAtom> neighbours = atomContainer.getConnectedAtomsList(atom);
            for (IAtom neighbour : neighbours) {
                if (!neighbour.getSymbol().equals("H")) {
                    atomDegree += 1;
                }
            }
            zagreb += (atomDegree * atomDegree);
        }
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(zagreb),
                getDescriptorNames());
    }

    /**
     * Returns the specific type of the DescriptorResult object.
     * <p/>
     * The return value from this method really indicates what type of result will
     * be obtained from the {@link org.openscience.cdk.qsar.DescriptorValue} object. Note that the same result
     * can be achieved by interrogating the {@link org.openscience.cdk.qsar.DescriptorValue} object; this method
     * allows you to do the same thing, without actually calculating the descriptor.
     *
     * @return an object that implements the {@link org.openscience.cdk.qsar.result.IDescriptorResult} interface indicating
     *         the actual type of values returned by the descriptor in the {@link org.openscience.cdk.qsar.DescriptorValue} object
     */
    @TestMethod("testGetDescriptorResultType")
    @Override
    public IDescriptorResult getDescriptorResultType() {
        return new DoubleResult(0.0);
    }

    /**
     *  Gets the parameterNames attribute of the ZagrebIndexDescriptor object.
     *
     *@return    The parameterNames value
     */
    @TestMethod("testGetParameterNames")
    @Override
    public String[] getParameterNames() {
        // no param names to return
        return (null);
    }

    /**
     *  Gets the parameterType attribute of the ZagrebIndexDescriptor object.
     *
     *@param  name  Description of the Parameter
     *@return       The parameterType value
     */
    @TestMethod("testGetParameterType_String")
    @Override
    public Object getParameterType(String name) {
        return (null);
    }
}
