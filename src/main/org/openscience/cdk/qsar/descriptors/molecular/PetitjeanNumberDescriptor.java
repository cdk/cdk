/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;


/**
 *  According to the Petitjean definition, the eccentricity of a vertex corresponds to 
 *  the distance from that vertex to the most remote vertex in the graph. 
 *  The distance is obtained from the distance matrix as the count of edges between the two vertices. 
 *  If r(i) is the largest matrix entry in row i of the distance matrix D, then the radius is defined as the smallest of the r(i).
 *  The graph diameter D is defined as the largest vertex eccentricity in the graph.
 *  (http://www.edusoft-lc.com/molconn/manuals/400/chaptwo.html)
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
 * Returns a single value named <i>PetitjeanNumber</i>.
 *
 * @author         mfe4
 * @cdk.created    December 7, 2004
 * @cdk.created    2004-11-03
 * @cdk.module     qsarmolecular
 * @cdk.svnrev  $Revision$
 * @cdk.set        qsar-descriptors
 * @cdk.dictref    qsar-descriptors:petitjeanNumber
 * @cdk.keyword    Petit-Jean, number
 */
@TestClass("org.openscience.cdk.qsar.descriptors.molecular.PetitjeanNumberDescriptorTest")
public class PetitjeanNumberDescriptor implements IMolecularDescriptor {
    private static final String[] names = {"PetitjeanNumber"};

    /**
     *  Constructor for the PetitjeanNumberDescriptor object
     */
    public PetitjeanNumberDescriptor() { }


    /**
     *  Gets the specification attribute of the PetitjeanNumberDescriptor object
     *
     *@return    The specification value
     */
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#petitjeanNumber",
                this.getClass().getName(),
                "$Id$",
                "The Chemistry Development Kit");
    }


    /**
     *  Sets the parameters attribute of the PetitjeanNumberDescriptor object
     *
     *@param  params            The new parameters value
     *@exception  CDKException  Description of the Exception
     */
    public void setParameters(Object[] params) throws CDKException {
        // no parameters for this descriptor
    }


    /**
     *  Gets the parameters attribute of the PetitjeanNumberDescriptor object
     *
     *@return    The parameters value
     */
    public Object[] getParameters() {
        return (null);
        // no parameters to return
    }

    @TestMethod(value="testNamesConsistency")
    public String[] getDescriptorNames() {
        return names;
    }


    /**
     *  Evaluate the descriptor for the molecule.
     *
     *@param  atomContainer                AtomContainer
     *@return                   petitjean number     
     */
    public DescriptorValue calculate(IAtomContainer atomContainer) {
        double petitjeanNumber; //weinerPath
        int diameter = PathTools.getMolecularGraphDiameter(atomContainer);
        int radius = PathTools.getMolecularGraphRadius(atomContainer);

        if (diameter == 0) petitjeanNumber = 0;
        else petitjeanNumber = (diameter - radius)/(double)diameter;
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(petitjeanNumber),
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
    public IDescriptorResult getDescriptorResultType() {
        return new DoubleResult(0.0);
    }

    /**
     *  Gets the parameterNames attribute of the PetitjeanNumberDescriptor object
     *
     *@return    The parameterNames value
     */
    public String[] getParameterNames() {
        // no param names to return
        return (null);
    }



    /**
     *  Gets the parameterType attribute of the PetitjeanNumberDescriptor object
     *
     *@param  name  Description of the Parameter
     *@return       The parameterType value
     */
    public Object getParameterType(String name) {
        return (null);
    }
}

