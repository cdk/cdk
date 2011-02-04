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
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.qsar.result.IntegerResultType;
import org.openscience.cdk.smiles.smarts.SMARTSQueryTool;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * Returns the number of acidic groups. The list of acidic groups is defined
 * by these SMARTS "$([O;H1]-[C,S,P]=O)", "$([*;-;!$(*~[*;+])])", 
 * "$([NH](S(=O)=O)C(F)(F)F)", and "$(n1nnnc1)" originally presented in
 * JOELib {@cdk.cite WEGNER2006}.
 *
 * @author      egonw
 * @cdk.module  qsarmolecular
 * @cdk.githash
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:acidicGroupCount
 */
@TestClass("org.openscience.cdk.qsar.descriptors.molecular.AcidicGroupCountDescriptorTest")
public class AcidicGroupCountDescriptor implements IMolecularDescriptor {

    private final static String[] SMARTS_STRINGS = {
        "[$([O;H1]-[C,S,P]=O)]",
        "[$([*;-;!$(*~[*;+])])]",
        "[$([NH](S(=O)=O)C(F)(F)F)]",
        "[$(n1nnnc1)]"
    };
    private final static String[] names = {"nAcid"};

    private List<SMARTSQueryTool> tools = new ArrayList<SMARTSQueryTool>();
    private boolean checkAromaticity;

    /**
     * Creates a new {@link AcidicGroupCountDescriptor}.
     */
    @TestMethod("testConstructor")
    public AcidicGroupCountDescriptor() throws CDKException {
        for (String smarts : SMARTS_STRINGS) {
            tools.add(new SMARTSQueryTool(smarts));
        }
        this.checkAromaticity = true;
    }

    /** {@inheritDoc} */
    @TestMethod("testGetSpecification")
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#acidGroupCount",
            this.getClass().getName(),
            "$Id$",
            "The Chemistry Development Kit"
        );
    }

    /** {@inheritDoc} */
    @TestMethod("testSetParameters_arrayObject")
    public void setParameters(Object[] params) throws CDKException {
        if (params.length != 1) {
            throw new CDKException("AcidicGroupCountDescriptor requires 1 parameter.");
        }
        if (!(params[0] instanceof Boolean)) {
            throw new CDKException("The parameter must be of type Boolean");
        }

        // ok, all should be fine
        this.checkAromaticity = (Boolean) params[0];

    }

    /** {@inheritDoc} */
    @TestMethod("testGetParameters")
    public Object[] getParameters() {
        Object params[] = new Object[1];
        params[0] = this.checkAromaticity;
        return (params);
    }

    /** {@inheritDoc} */
    @TestMethod(value="testNamesConsistency")
    public String[] getDescriptorNames() {
        return names;
    }

    /** {@inheritDoc} */
    @TestMethod("testCalculate_IAtomContainer")
    public DescriptorValue calculate(IAtomContainer atomContainer) {
        // do aromaticity detection
        if (this.checkAromaticity) {
            try {
                AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(
                    atomContainer
                );
                CDKHueckelAromaticityDetector.detectAromaticity(
                    atomContainer
                );
            } catch (CDKException exception) {
                return getDummyDescriptorValue(exception);
            }
        }

        try {
            int count = 0;
            for (SMARTSQueryTool tool : tools) {
                if (tool.matches(atomContainer))
                    count += tool.countMatches();
            }
            return new DescriptorValue(getSpecification(), getParameterNames(),
                getParameters(),
                new IntegerResult(count),
                getDescriptorNames()
            );
        } catch (CDKException exception) {
            return getDummyDescriptorValue(exception);
        }
    }

    /** {@inheritDoc} */
    @TestMethod("testGetDescriptorResultType")
    public IDescriptorResult getDescriptorResultType() {
        return new IntegerResultType();
    }

    /** {@inheritDoc} */
    @TestMethod("testGetParameterNames")
    public String[] getParameterNames() {
        String[] params = new String[1];
        params[0] = "checkAromaticity";
        return (params);

    }

    /** {@inheritDoc} */
    @TestMethod("testGetParameterType_String")
    public Object getParameterType(String name) {
        Object object = null;
        if (name.equals("checkAromaticity")) object = true;
        return (object);
    }

    private DescriptorValue getDummyDescriptorValue(Exception exception) {
        return new DescriptorValue(getSpecification(), getParameterNames(),
            getParameters(), new IntegerResult(-1), getDescriptorNames(),
            exception
        );
    }
}

