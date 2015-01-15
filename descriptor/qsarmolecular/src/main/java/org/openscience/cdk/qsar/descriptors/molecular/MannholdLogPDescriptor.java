/* Copyright (C) 2009  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.qsar.descriptors.molecular;

import org.openscience.cdk.config.Elements;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.AbstractMolecularDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IDescriptor;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.DoubleResultType;
import org.openscience.cdk.qsar.result.IDescriptorResult;

/**
 * <p>Prediction of logP based on the number of carbon and hetero atoms. The
 * implemented equation was proposed in {@cdk.cite Mannhold2009}.
 *
 * @cdk.module     qsarmolecular
 * @cdk.githash
 * @cdk.set        qsar-descriptors
 * @cdk.dictref    qsar-descriptors:mannholdLogP
 *
 * @cdk.keyword LogP
 * @cdk.keyword descriptor
 */
public class MannholdLogPDescriptor extends AbstractMolecularDescriptor implements IMolecularDescriptor {

    private static final String[] NAMES = {"MLogP"};

    /**
     * Gets the specification attribute of the MannholdLogPDescriptor object.
     *
     * @return    The specification value
     */
    @Override
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification("http://www.blueobelisk.org/ontologies/"
                + "chemoinformatics-algorithms/#mannholdLogP", this.getClass().getName(),
                "The Chemistry Development Kit");
    }

    /**
     * This {@link IDescriptor} does not have any parameters. If it had, this
     * would have been the method to set them.
     *
     * @param  params            The new parameter value
     * @exception  CDKException  Exception throw when invalid parameter values
     *                           are passed
     * @see #getParameters
     */
    @Override
    public void setParameters(Object[] params) throws CDKException {
        if (params != null && params.length > 0) {
            throw new CDKException("MannholdLogPDescriptor has no parameters.");
        }
    }

    /**
     * Gets the parameters attribute of the MannholdLogPDescriptor object.
     *
     * @return    A zero-length Object array.
     * @see #setParameters
     */
    @Override
    public Object[] getParameters() {
        return new Object[0];
    }

    @Override
    public String[] getDescriptorNames() {
        return NAMES;
    }

    private DescriptorValue getDummyDescriptorValue(Exception e) {
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(
                Double.NaN), getDescriptorNames(), e);
    }

    /**
     *  Calculates the Mannhold LogP for an atom container.
     *
     * @param  atomContainer      {@link IAtomContainer} to calculate the
     *                            descriptor value for.
     * @return A descriptor value wrapping a {@link DoubleResult}.
     */
    @Override
    public DescriptorValue calculate(IAtomContainer atomContainer) {
        IAtomContainer ac = null;
        try {
            ac = (IAtomContainer) atomContainer.clone();
        } catch (CloneNotSupportedException e) {
            return getDummyDescriptorValue(e);
        }

        int carbonCount = 0;
        int heteroCount = 0;
        for (IAtom atom : ac.atoms()) {
            if (!Elements.HYDROGEN.getSymbol().equals(atom.getSymbol())) {
                if (Elements.CARBON.getSymbol().equals(atom.getSymbol())) {
                    carbonCount++;
                } else {
                    heteroCount++;
                }
            }
        }
        double mLogP = 1.46 + 0.11 * carbonCount - 0.11 * heteroCount;

        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(mLogP),
                getDescriptorNames());
    }

    /**
     * Returns a type of return value calculated by this descriptor.
     *
     * @return returns a {@link DoubleResult}.
     */
    @Override
    public IDescriptorResult getDescriptorResultType() {
        return new DoubleResultType();
    }

    /**
     * Gets the parameterNames attribute for this descriptor.
     *
     * @return    A zero-length String array.
     */
    @Override
    public String[] getParameterNames() {
        return new String[0];
    }

    /**
     * Gets the parameterType attribute for a given parameter name. It
     * always returns null, as this descriptor does not have any parameters.
     *
     * @param  name  Name of the parameter for which the type is requested.
     * @return       The parameterType of the given parameter.
     */
    @Override
    public Object getParameterType(String name) {
        return null;
    }

}
