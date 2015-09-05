/* Copyright (C) 2006-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.qsar.descriptors.atomic;

import org.openscience.cdk.charges.MMFF94PartialCharges;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.forcefield.mmff.Mmff;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.AbstractAtomicDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * The calculation of total partial charges of an heavy atom is based on MMFF94
 * model.
 *
 * <p>This descriptor uses these parameters: <table border="1"> <tr>
 * <td>Name</td> <td>Default</td> <td>Description</td> </tr> <tr> <td></td>
 * <td></td> <td>no parameters</td> </tr> </table>
 *
 * @author Miguel Rojas
 * @cdk.created 2006-04-11
 * @cdk.module qsaratomic
 * @cdk.githash
 * @cdk.set qsar-descriptors
 * @cdk.dictref qsar-descriptors:partialTChargeMMFF94
 * @cdk.bug 1628461
 * @see MMFF94PartialCharges
 */
public class PartialTChargeMMFF94Descriptor extends AbstractAtomicDescriptor {

    private static final String[] NAMES = {"partialTCMMFF94"};

    private static final String CHARGE_CACHE = "mmff.qsar.charge.cache";

    private Mmff mmff;

    private ILoggingTool logger = LoggingToolFactory.createLoggingTool(getClass());

    /**
     * Constructor for the PartialTChargeMMFF94Descriptor object
     */
    public PartialTChargeMMFF94Descriptor() {
        mmff = new Mmff();
    }

    /**
     * Gets the specification attribute of the PartialTChargeMMFF94Descriptor
     * object
     *
     * @return The specification value
     */
    @Override
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#partialTChargeMMFF94", this
                .getClass().getName(), "The Chemistry Development Kit");
    }

    /**
     * This descriptor does not have any parameter to be set.
     */
    @Override
    public void setParameters(Object[] params) throws CDKException {
        // no parameters
    }

    /**
     * Gets the parameters attribute of the PartialTChargeMMFF94Descriptor
     * object
     *
     * @return The parameters value
     * @see #setParameters
     */
    @Override
    public Object[] getParameters() {
        return null;
    }

    @Override
    public String[] getDescriptorNames() {
        return NAMES;
    }

    /**
     * The method returns partial charges assigned to an heavy atom through
     * MMFF94 method. It is needed to call the addExplicitHydrogensToSatisfyValency
     * method from the class tools.HydrogenAdder.
     *
     * @param atom The IAtom for which the DescriptorValue is requested
     * @param org  AtomContainer
     * @return partial charge of parameter atom
     */
    @Override
    public DescriptorValue calculate(IAtom atom, IAtomContainer org) {

        if (atom.getProperty(CHARGE_CACHE) == null) {

            IAtomContainer copy;
            try {
                copy = org.clone();
            } catch (CloneNotSupportedException e) {
                return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(
                        Double.NaN), NAMES);
            }

            for (IAtom a : org.atoms()) {
                if (a.getImplicitHydrogenCount() == null || a.getImplicitHydrogenCount() != 0) {
                    logger.error("Hydrogens must be explict for MMFF charge calculation");
                    return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                                               new DoubleResult(Double.NaN), NAMES);
                }
            }

            if (!mmff.assignAtomTypes(copy))
                logger.warn("One or more atoms could not be assigned an MMFF atom type");
            mmff.partialCharges(copy);
            mmff.clearProps(copy);

            // cache charges
            for (int i = 0; i < org.getAtomCount(); i++) {
                org.getAtom(i).setProperty(CHARGE_CACHE,
                                           copy.getAtom(i).getCharge());
            }
        }

        return new DescriptorValue(getSpecification(),
                                   getParameterNames(),
                                   getParameters(),
                                   new DoubleResult(atom.getProperty(CHARGE_CACHE, Double.class)),
                                   NAMES);
    }

    /**
     * Gets the parameterNames attribute of the PartialTChargeMMFF94Descriptor
     * object
     *
     * @return The parameterNames value
     */
    @Override
    public String[] getParameterNames() {
        return new String[0];
    }

    /**
     * Gets the parameterType attribute of the PartialTChargeMMFF94Descriptor
     * object
     *
     * @param name Description of the Parameter
     * @return The parameterType value
     */
    @Override
    public Object getParameterType(String name) {
        return null;
    }
}
