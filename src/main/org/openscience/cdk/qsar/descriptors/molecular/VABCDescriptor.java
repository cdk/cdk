/* Copyright (C) 2011  Egon Willighagen <egon.willighagen@gmail.com>
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
package org.openscience.cdk.qsar.descriptors.molecular;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.volume.VABCVolume;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.DoubleResultType;
import org.openscience.cdk.qsar.result.IDescriptorResult;

/**
 * Volume descriptor using the method implemented in the {@link VABCVolume} class. 
 *
 * @cdk.module qsarmolecular
 * @cdk.githash
 *
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:vabc
 * @cdk.keyword volume
 * @cdk.keyword descriptor
 */
@TestClass("org.openscience.cdk.qsar.descriptors.molecular.VABCDescriptorTest")
public class VABCDescriptor implements IMolecularDescriptor {

    /**
     * {@inheritDoc}
     */
    @TestMethod("testGetSpecification")
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#vabc",
            this.getClass().getName(),
            "$Id$",
            "The Chemistry Development Kit"
        );
    }

    /** {@inheritDoc} */
    @TestMethod("testSetParameters_arrayObject")
    public void setParameters(Object[] params) throws CDKException {
        if (params.length != 0) {
            throw new CDKException("The VABCDescriptor expects zero parameters");
        }
    }

    /** {@inheritDoc} */
    @TestMethod("testGetParameters")
    public Object[] getParameters() {
        return new Object[0];

    }

    @TestMethod(value="testNamesConsistency")
    public String[] getDescriptorNames() {
        return new String[]{"VABC"};
    }

    private DescriptorValue getDummyDescriptorValue(Exception e) {
        return new DescriptorValue(getSpecification(), getParameterNames(),
            getParameters(), new DoubleResult(Double.NaN), getDescriptorNames(), e);
    }

    /**
     * Calculates the descriptor value using the {@link VABCVolume} class.
     * 
     * @param atomContainer The {@link IAtomContainer} whose volume is to be calculated
     * @return A double containing the volume
     */
    @TestMethod("testCalculate_IAtomContainer")
    public DescriptorValue calculate(IAtomContainer atomContainer) {
        if (!(atomContainer instanceof IMolecule))
            atomContainer = atomContainer.getBuilder().newInstance(
                IMolecule.class, atomContainer
            );
        IMolecule mol = (IMolecule)atomContainer;
        
        double volume;
        try {
            volume = VABCVolume.calculate(mol);
        } catch (CDKException exception) {
            return getDummyDescriptorValue(exception);
        }
        
        return new DescriptorValue(
            getSpecification(), getParameterNames(), getParameters(),
            new DoubleResult(volume), getDescriptorNames()
        );
    }

    /** {@inheritDoc} */
    @TestMethod("testGetDescriptorResultType")
    public IDescriptorResult getDescriptorResultType() {
        return new DoubleResultType();
    }

    /** {@inheritDoc} */
    @TestMethod("testGetParameterNames")
    public String[] getParameterNames() {
        return new String[0];
    }

    /** {@inheritDoc} */
    @TestMethod("testGetParameterType_String")
    public Object getParameterType(String name) {
        return null;
    }
}
