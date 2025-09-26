/* Copyright (C) 2004-2007  Matteo Floris <mfe4@users.sf.net>
 *                    2010  Egon Willighagen <egonw@users.sf.net>
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
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.graph.matrix.ConnectionMatrix;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.AbstractMolecularDescriptor;
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
 * <br>Further information is given in
 * <a href="https://doi.org/10.1021/ja01193a005">Wiener, H. (1947) "Structural Determination of Paraffin Boiling Points"</a>.
 * <br>Wiener path number (WPATH): half the sum of all the distance matrix entries.
 * <br>Wiener polarity number (WPOL): half the sum of all the distance matrix entries with a
 * value of 3.
 * <p>
 * This descriptor uses no parameters.
 * <p>
 * This descriptor works properly with AtomContainers whose atoms contain <b>implicit hydrogens</b>
 * or <b>explicit hydrogens</b>.
 *
 * @author         mfe4
 * @cdk.created    December 7, 2004
 * @cdk.created    2004-11-03
 * @cdk.dictref    qsar-descriptors:wienerNumbers
 * @cdk.keyword    Wiener number
 */
public class WienerNumbersDescriptor extends AbstractMolecularDescriptor implements IMolecularDescriptor {

    /**
     * Names of calculated descriptor values.
     */
    private static final String[] NAMES            = {"WPATH", "WPOL"};

    /**
     * Constructor for the WienerNumbersDescriptor object (does nothing since there are no parameters).
     */
    public WienerNumbersDescriptor() {
        //nothing to do
    }

    @Override
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#wienerNumbers", this.getClass()
                        .getName(), "The Chemistry Development Kit");
    }

    /**
     * Sets the parameters attribute of the WienerNumbersDescriptor object.
     * <p></p>
     * This descriptor does not take any parameters.
     *
     * @param  params            The new parameters value
     * @exception  CDKException  This method will not throw any exceptions
     * @see #getParameters
     */
    @Override
    public void setParameters(Object[] params) throws CDKException {
        // no parameters for this descriptor
    }

    /**
     * Get parameters: returns empty array, since there are none.
     */
    @Override
    public Object[] getParameters() {
        return new Object[0];
    }

    @Override
    public String[] getDescriptorNames() {
        return NAMES;
    }

    /**
     * Calculate the Wiener numbers.
     *
     * @param atomContainer The {@link IAtomContainer} for which this descriptor is to be calculated
     * @return Wiener numbers (path number and polarity number) as array of 2 doubles
     */
    @Override
    public DescriptorValue calculate(IAtomContainer atomContainer) {
        DoubleArrayResult wienerNumbers = new DoubleArrayResult(2);
        double wienerPathNumber = 0; //wienerPath
        double wienerPolarityNumber = 0; //wienerPol

        double[][] matr = ConnectionMatrix.getMatrix(AtomContainerManipulator.removeHydrogens(atomContainer));
        int[][] distances = PathTools.computeFloydAPSP(matr);

        int partial;
        for (int[] distance : distances) {
            for (int j = 0; j < distances.length; j++) {
                partial = distance[j];
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
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), wienerNumbers,
                getDescriptorNames());
    }

    @Override
    public IDescriptorResult getDescriptorResultType() {
        return new DoubleArrayResultType(2);
    }

    /**
     * Get parameters: empty, since there are no parameters.
     */
    @Override
    public String[] getParameterNames() {
        // no param names to return
        return new String[0];
    }

    /**
     * Gets the parameterType attribute of the WienerNumbersDescriptor object.
     *
     * @param  name  Description of the Parameter
     * @return       An Object of class equal to that of the parameter being requested (always null here!)
     */
    @Override
    public Object getParameterType(String name) {
        return (null);
    }
}
