/*  $RCSfile$
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

import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.graph.matrix.ConnectionMatrix;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleArrayResultType;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;


/**
 * This descriptor calculates the Wiener numbers. This includes the Wiener Path number 
 * and the Wiener Polarity Number.
 * <BR>
 * Further information is given in   
 * Wiener path number: half the sum of all the distance matrix entries; Wiener
 * polarity number: half the sum of all the distance matrix entries with a
 * value of 3. For more information see Todeschini R, Consonni V, Handbook of Molecular
 * Descriptors, In: Mannhold R, Kubinyi H, Timmermann H (Eds.), Methods and Principles in 
 * Medicinal Chemistry, Vol. 11, Wiley-VCH 2000, Weinheim, New York.
 * <p>
 * This descriptor uses no parameters.
 * <p>
 * This descriptor works properly with AtomContainers whose atoms contain <b>implicit hydrogens</b>
 * or <b>explicit hydrogens</b>.
 *
 * Returns the  following values
 * <ol>
 * <li>WPATH - weiner path number
 * <li>WPOL - weiner polarity number
 * </ol>
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
 * @author         mfe4
 * @cdk.created        December 7, 2004
 * @cdk.created    2004-11-03
 * @cdk.module     qsarmolecular
 * @cdk.svnrev  $Revision$
 * @cdk.set        qsar-descriptors
 * @cdk.dictref    qsar-descriptors:wienerNumbers
 * @cdk.keyword    Wiener number
 */
public class WienerNumbersDescriptor implements IMolecularDescriptor {

    private static final String[] names = {"WPATH", "WPOL"};

    double[][] matr = null;
    DoubleArrayResult wienerNumbers = null;
    ConnectionMatrix connectionMatrix = new ConnectionMatrix();
    AtomContainerManipulator atm =  new AtomContainerManipulator();

    /**
     *  Constructor for the WienerNumbersDescriptor object.
     */
    public WienerNumbersDescriptor() { 

    }

    /**
     * Returns a <code>Map</code> which specifies which descriptor
     * is implemented by this class. 
     *
     * These fields are used in the map:
     * <ul>
     * <li>Specification-Reference: refers to an entry in a unique dictionary
     * <li>Implementation-Title: anything
     * <li>Implementation-Identifier: a unique identifier for this version of
     *  this class
     * <li>Implementation-Vendor: CDK, JOELib, or anything else
     * </ul>
     *
     * @return An object containing the descriptor specification
     */
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#wienerNumbers",
                this.getClass().getName(),
                "$Id$",
                "The Chemistry Development Kit");
    }


    /**
     *  Sets the parameters attribute of the WienerNumbersDescriptor object.
     *
     *  This descriptor does not take any parameters
     *
     *@param  params            The new parameters value
     *@exception  CDKException  This method will not throw any exceptions
    *@see #getParameters
     */
    public void setParameters(Object[] params) throws CDKException {
        // no parameters for this descriptor
    }


    /**
     *  Gets the parameters attribute of the WienerNumbersDescriptor object.
     *
     *  This descriptor does not return any parameters
     *
     *@return    The parameters value
     *@see #setParameters
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
     * Calculate the Wiener numbers.
     *
     *@param  atomContainer   The {@link IAtomContainer} for which this descriptor is to be calculated
     *@return wiener numbers as array of 2 doubles
     */
    public DescriptorValue calculate(IAtomContainer atomContainer) {
        wienerNumbers = new DoubleArrayResult(2);
        double wienerPathNumber = 0; //wienerPath
        double wienerPolarityNumber = 0; //wienerPol


        matr = ConnectionMatrix.getMatrix(AtomContainerManipulator.removeHydrogens(atomContainer));        
        int[][] distances = PathTools.computeFloydAPSP(matr);

        int partial;
        for (int i = 0; i < distances.length; i++) {
            for (int j = 0; j < distances.length; j++) {
                partial = distances[i][j];
                wienerPathNumber += partial;
                if (partial == 3) {
                    wienerPolarityNumber += 1;
                }
            }
        }
        wienerPathNumber = wienerPathNumber / 2;
        wienerPolarityNumber = wienerPolarityNumber / 2;

        wienerNumbers.add(wienerPathNumber);
        wienerNumbers.add(wienerPolarityNumber);
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                wienerNumbers, getDescriptorNames());
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
    public IDescriptorResult getDescriptorResultType() {
        return new DoubleArrayResultType(2);
    }

    /**
     *  Gets the parameterNames attribute of the WienerNumbersDescriptor object.
     *
     * This descriptor does not return any parameters
     *
     *@return    The parameterNames value
     */
    public String[] getParameterNames() {
        // no param names to return
        return (null);
    }



    /**
     *  Gets the parameterType attribute of the WienerNumbersDescriptor object.
     *
     *@param  name  Description of the Parameter
     *@return       An Object of class equal to that of the parameter being requested
     */
    public Object getParameterType(String name) {
        return (null);
    }
}

