/*
 *  Copyright (C) 2004-2007  Rajarshi Guha <rajarshi@users.sourceforge.net>
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
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.graph.matrix.AdjacencyMatrix;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;


/**
 * A topological descriptor combining distance and adjacency information.
 * This descriptor is described by Sharma et al. {@cdk.cite SHA97} and has been shown
 * to correlate well with a number of physical properties. The descriptor is also reported to
 * have good discriminatory ability. 
 * <p>
 * The eccentric connectivity index for a hydrogen supressed molecular graph is given by the 
 * expression
 * <center>
 * \xi^{c} = \sum_{i = 1}{n} E(i) V(i)
 * </center>
 * where E(i) is the eccentricity of the i<sup>th</sup> atom (path length from the 
 * i<sup>th</sup> atom to the atom farthest from it) and V(i) is the vertex degree of the
 * i<sup>th</sup> atom.
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
 * Returns a single value with name <i>ECCEN</i>
 * @author      Rajarshi Guha
 * @cdk.created     2005-03-19
 * @cdk.module  qsarmolecular
 * @cdk.svnrev  $Revision$
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:eccentricConnectivityIndex
 */
@TestClass("org.openscience.cdk.qsar.descriptors.molecular.EccentricConnectivityIndexDescriptorTest")
public class EccentricConnectivityIndexDescriptor implements IMolecularDescriptor {
    private static final String[] names = {"ECCEN"};

    public EccentricConnectivityIndexDescriptor() {}

	public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#eccentricConnectivityIndex",
		    this.getClass().getName(),
		    "$Id$",
            "The Chemistry Development Kit");
    }

    /**
     *  Sets the parameters attribute of the EccentricConnectivityIndexDescriptor object
     *
     *@param  params            The new parameters value
     *@exception  CDKException  Description of the Exception
     */
    public void setParameters(Object[] params) throws CDKException {
        // no parameters for this descriptor
    }

    /**
     *  Gets the parameters attribute of the EccentricConnectivityIndexDescriptor object
     *
     *@return    The parameters value
     */
    public Object[] getParameters() {
        // no parameters to return
        return(null);
    }

    @TestMethod(value="testNamesConsistency")
    public String[] getDescriptorNames() {
        return names;
    }

    /**
     *  Gets the parameterNames attribute of the EccentricConnectivityIndexDescriptor object
     *
     *@return    The parameterNames value
     */
    public String[] getParameterNames() {
        // no param names to return
        return(null);
    }


    /**
     *  Gets the parameterType attribute of the EccentricConnectivityIndexDescriptor object
     *
     *@param  name  Description of the Parameter
     *@return       The parameterType value
     */
    public Object getParameterType(String name) {
         return (null);
    }

    /**
     *  Calculates the eccentric connectivity
     *
     *@param  container  Parameter is the atom container.
     *@return            An IntegerResult value representing the eccentric connectivity index
     */

    public DescriptorValue calculate(IAtomContainer container) {
        IAtomContainer local = AtomContainerManipulator.removeHydrogens(container);

        int natom = local.getAtomCount();
        int[][] admat = AdjacencyMatrix.getMatrix(local);
        int[][] distmat = PathTools.computeFloydAPSP(admat);
        
        int eccenindex = 0;
        for (int i = 0; i < natom; i++) {
            int max = -1;
            for (int j = 0; j < natom; j++) {
                if (distmat[i][j] > max) max = distmat[i][j];
            }
            int degree = local.getConnectedBondsCount(i);
            eccenindex += max * degree;
        }
        IntegerResult retval = new IntegerResult(eccenindex);
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                retval, getDescriptorNames(), null);
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
    public IDescriptorResult getDescriptorResultType() {
        return new IntegerResult(1);
    }
}
    

