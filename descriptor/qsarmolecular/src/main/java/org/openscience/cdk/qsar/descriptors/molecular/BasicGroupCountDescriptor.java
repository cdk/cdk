/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
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

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.qsar.AbstractMolecularDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.qsar.result.IntegerResultType;
import org.openscience.cdk.smiles.smarts.SMARTSQueryTool;

/**
 * Returns the number of basic groups. The list of basic groups is defined
 * by this SMARTS "[$([NH2]-[CX4])]", "[$([NH](-[CX4])-[CX4])]",
 * "[$(N(-[CX4])(-[CX4])-[CX4])]", "[$([*;+;!$(*~[*;-])])]",
 * "[$(N=C-N)]", and "[$(N-C=N)]" originally presented in
 * JOELib {@cdk.cite WEGNER2006}.
 *
 * @author      egonw
 * @cdk.module  qsarmolecular
 * @cdk.githash
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:acidicGroupCount
 */
@TestClass("org.openscience.cdk.qsar.descriptors.molecular.BasicGroupCountDescriptorTest")
public class BasicGroupCountDescriptor extends AbstractMolecularDescriptor implements IMolecularDescriptor {

    private final static String[] SMARTS_STRINGS = {"[$([NH2]-[CX4])]", "[$([NH](-[CX4])-[CX4])]",
            "[$(N(-[CX4])(-[CX4])-[CX4])]", "[$([*;+;!$(*~[*;-])])]", "[$(N=C-N)]", "[$(N-C=N)]"};
    private final static String[] NAMES          = {"nBase"};

    private List<SMARTSQueryTool> tools          = new ArrayList<SMARTSQueryTool>();

    /**
     * Creates a new {@link BasicGroupCountDescriptor}.
     */
    @TestMethod("testConstructor")
    public BasicGroupCountDescriptor() {}

    @Override
    public void initialise(IChemObjectBuilder builder) {
        for (String smarts : SMARTS_STRINGS) {
            tools.add(new SMARTSQueryTool(smarts, builder));
        }
    }

    /** {@inheritDoc} */
    @TestMethod("testGetSpecification")
    @Override
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#basicGroupCount", this.getClass()
                        .getName(), "The Chemistry Development Kit");
    }

    /** {@inheritDoc} */
    @TestMethod("testSetParameters_arrayObject")
    @Override
    public void setParameters(Object[] params) throws CDKException {}

    /** {@inheritDoc} */
    @TestMethod("testGetParameters")
    @Override
    public Object[] getParameters() {
        return null;
    }

    /** {@inheritDoc} */
    @TestMethod(value = "testNamesConsistency")
    @Override
    public String[] getDescriptorNames() {
        return NAMES;
    }

    /** {@inheritDoc} */
    @TestMethod("testCalculate_IAtomContainer")
    @Override
    public DescriptorValue calculate(IAtomContainer atomContainer) {

        if (tools.isEmpty()) {
            throw new IllegalStateException("descriptor is not initalised, invoke 'initalise' first");
        }

        try {
            int count = 0;
            for (SMARTSQueryTool tool : tools) {
                if (tool.matches(atomContainer)) count += tool.countMatches();
            }
            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new IntegerResult(
                    count), getDescriptorNames());
        } catch (CDKException exception) {
            return getDummyDescriptorValue(exception);
        }
    }

    /** {@inheritDoc} */
    @TestMethod("testGetDescriptorResultType")
    @Override
    public IDescriptorResult getDescriptorResultType() {
        return new IntegerResultType();
    }

    /** {@inheritDoc} */
    @TestMethod("testGetParameterNames")
    @Override
    public String[] getParameterNames() {
        return new String[0];

    }

    /** {@inheritDoc} */
    @TestMethod("testGetParameterType_String")
    @Override
    public Object getParameterType(String name) {
        return null;
    }

    private DescriptorValue getDummyDescriptorValue(Exception exception) {
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new IntegerResult(-1),
                getDescriptorNames(), exception);
    }
}
