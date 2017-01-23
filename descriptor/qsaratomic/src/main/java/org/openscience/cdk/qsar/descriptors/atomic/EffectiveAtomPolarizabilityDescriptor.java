/* Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.charges.Polarizability;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.qsar.AbstractAtomicDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;

/**
 * Effective polarizability of a heavy atom
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
 * @cdk.created 2006-05-03
 * @cdk.module  qsaratomic
 * @cdk.githash
 * @cdk.dictref qsar-descriptors:effectivePolarizability
 * @see Polarizability
 */
public class EffectiveAtomPolarizabilityDescriptor extends AbstractAtomicDescriptor implements IAtomicDescriptor {

    private Polarizability pol;

    /**
     *  Constructor for the EffectiveAtomPolarizabilityDescriptor object
     */
    public EffectiveAtomPolarizabilityDescriptor() {
        pol = new Polarizability();
    }

    /**
     *  Gets the specification attribute of the EffectiveAtomPolarizabilityDescriptor
     *  object
     *
     *@return    The specification value
     */
    @Override
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#effectivePolarizability", this
                        .getClass().getName(), "The Chemistry Development Kit");
    }

    /**
     * This descriptor does have any parameter.
     */
    @Override
    public void setParameters(Object[] params) throws CDKException {}

    /**
     *  Gets the parameters attribute of the EffectiveAtomPolarizabilityDescriptor
     *  object
     *
     * @return    The parameters value
     * @see #setParameters
     */
    @Override
    public Object[] getParameters() {
        return null;
    }

    @Override
    public String[] getDescriptorNames() {
        return new String[]{"effAtomPol"};
    }

    /**
     *  The method calculates the Effective Atom Polarizability of a given atom
     *  It is needed to call the addExplicitHydrogensToSatisfyValency method from the class tools.HydrogenAdder.
     *
     *@param  atom              The IAtom for which the DescriptorValue is requested
     *@param  ac                AtomContainer
     *@return                   return the effective polarizability
     */
    @Override
    public DescriptorValue calculate(IAtom atom, IAtomContainer ac) {
        double polarizability;
        try {
            // FIXME: for now I'll cache a few modified atomic properties, and restore them at the end of this method
            String originalAtomtypeName = atom.getAtomTypeName();
            Integer originalNeighborCount = atom.getFormalNeighbourCount();
            Integer originalHCount = atom.getImplicitHydrogenCount();
            Integer originalValency = atom.getValency();
            IAtomType.Hybridization originalHybridization = atom.getHybridization();
            boolean originalFlag = atom.getFlag(CDKConstants.VISITED);
            Double originalBondOrderSum = atom.getBondOrderSum();
            Order originalMaxBondOrder = atom.getMaxBondOrder();
            polarizability = pol.calculateGHEffectiveAtomPolarizability(ac, atom, 100, true);
            // restore original props
            atom.setAtomTypeName(originalAtomtypeName);
            atom.setFormalNeighbourCount(originalNeighborCount);
            atom.setValency(originalValency);
            atom.setImplicitHydrogenCount(originalHCount);
            atom.setFlag(CDKConstants.VISITED, originalFlag);
            atom.setHybridization(originalHybridization);
            atom.setMaxBondOrder(originalMaxBondOrder);
            atom.setBondOrderSum(originalBondOrderSum);
            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(
                    polarizability), getDescriptorNames());
        } catch (Exception ex1) {
            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(
                    Double.NaN), getDescriptorNames(), ex1);
        }
    }

    /**
     *  Gets the parameterNames attribute of the EffectiveAtomPolarizabilityDescriptor
     *  object
     *
     *@return    The parameterNames value
     */
    @Override
    public String[] getParameterNames() {
        return new String[0];
    }

    /**
     *  Gets the parameterType attribute of the EffectiveAtomPolarizabilityDescriptor
     *  object
     *
     *@param  name  Description of the Parameter
     *@return       The parameterType value
     */
    @Override
    public Object getParameterType(String name) {
        return null;
    }
}
