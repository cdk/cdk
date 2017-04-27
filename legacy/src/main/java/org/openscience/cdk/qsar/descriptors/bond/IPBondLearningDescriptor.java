/* Copyright (C) 2006-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.qsar.descriptors.bond;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.qsar.AbstractBondDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.tools.IonizationPotentialTool;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 *
 *  This class returns the ionization potential of a Bond. It is
 *  based on a function which is extracted from Weka(J48) from
 *  experimental values (NIST data).
 *
 * <table border="1"><caption>Parameters for this descriptor:</caption>
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
 * @author      Miguel Rojas
 * @cdk.created 2006-05-26
 * @cdk.module  qsarionpot
 * @cdk.githash
 * @cdk.dictref qsar-descriptors:ionizationPotential
 */
@Deprecated
public class IPBondLearningDescriptor extends AbstractBondDescriptor {

    private static final String[] DESCRIPTOR_NAMES = {"ipBondLearning"};

    /**
     *  Constructor for the IPBondLearningDescriptor object
     */
    public IPBondLearningDescriptor() {}

    /**
     *  Gets the specification attribute of the IPBondLearningDescriptor object
     *
     *@return    The specification value
     */
    @Override
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#ionizationPotential", this
                        .getClass().getName(), "The Chemistry Development Kit");
    }

    /**
     * This descriptor does have any parameter.
     */
    @Override
    public void setParameters(Object[] params) throws CDKException {}

    /**
     *  Gets the parameters attribute of the IPBondLearningDescriptor object.
     *
     *@return    The parameters value
     * @see #setParameters
     */
    @Override
    public Object[] getParameters() {
        return null;
    }

    @Override
    public String[] getDescriptorNames() {
        return DESCRIPTOR_NAMES;
    }

    private DescriptorValue getDummyDescriptorValue(Exception e) {
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(
                Double.NaN), DESCRIPTOR_NAMES, e);
    }

    /**
     *  This method calculates the ionization potential of a bond.
     *
     *@param  atomContainer         Parameter is the IAtomContainer.
     *@return                   The ionization potential
     */
    @Override
    public DescriptorValue calculate(IBond bond, IAtomContainer atomContainer) {
        double value = 0;
        // FIXME: for now I'll cache a few modified atomic properties, and restore them at the end of this method
        String originalAtomtypeName1 = bond.getBeg().getAtomTypeName();
        Integer originalNeighborCount1 = bond.getBeg().getFormalNeighbourCount();
        IAtomType.Hybridization originalHybridization1 = bond.getBeg().getHybridization();
        Integer originalValency1 = bond.getBeg().getValency();
        String originalAtomtypeName2 = bond.getEnd().getAtomTypeName();
        Integer originalNeighborCount2 = bond.getEnd().getFormalNeighbourCount();
        IAtomType.Hybridization originalHybridization2 = bond.getEnd().getHybridization();
        Integer originalValency2 = bond.getEnd().getValency();
        Double originalBondOrderSum1 = bond.getBeg().getBondOrderSum();
        Order originalMaxBondOrder1 = bond.getBeg().getMaxBondOrder();
        Double originalBondOrderSum2 = bond.getEnd().getBondOrderSum();
        Order originalMaxBondOrder2 = bond.getEnd().getMaxBondOrder();

        if (!isCachedAtomContainer(atomContainer)) {
            try {
                AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(atomContainer);
                LonePairElectronChecker lpcheck = new LonePairElectronChecker();
                lpcheck.saturate(atomContainer);
            } catch (CDKException e) {
                return getDummyDescriptorValue(e);
            }

        }
        if (!bond.getOrder().equals(IBond.Order.SINGLE)) {
            try {
                value = IonizationPotentialTool.predictIP(atomContainer, bond);
            } catch (CDKException e) {
                return getDummyDescriptorValue(e);
            }
        }
        bond.getBeg().setAtomTypeName(originalAtomtypeName1);
        bond.getBeg().setHybridization(originalHybridization1);
        bond.getBeg().setValency(originalValency1);
        bond.getBeg().setFormalNeighbourCount(originalNeighborCount1);
        bond.getEnd().setAtomTypeName(originalAtomtypeName2);
        bond.getEnd().setHybridization(originalHybridization2);
        bond.getEnd().setValency(originalValency2);
        bond.getEnd().setFormalNeighbourCount(originalNeighborCount2);
        bond.getBeg().setMaxBondOrder(originalMaxBondOrder1);
        bond.getBeg().setBondOrderSum(originalBondOrderSum1);
        bond.getEnd().setMaxBondOrder(originalMaxBondOrder2);
        bond.getEnd().setBondOrderSum(originalBondOrderSum2);

        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(value),
                DESCRIPTOR_NAMES);
    }

    /**
    * Gets the parameterNames attribute of the IPBondLearningDescriptor object.
    *
    * @return    The parameterNames value
    */
    @Override
    public String[] getParameterNames() {
        return new String[0];
    }

    /**
     * Gets the parameterType attribute of the IPBondLearningDescriptor object.
     *
     * @param  name  Description of the Parameter
     * @return       An Object of class equal to that of the parameter being requested
     */
    @Override
    public Object getParameterType(String name) {
        return null;
    }
}
