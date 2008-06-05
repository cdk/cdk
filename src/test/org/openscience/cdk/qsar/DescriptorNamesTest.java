/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.qsar;


import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import java.io.InputStream;
import java.util.*;

/**
 * TestSuite that runs all tests for the DescriptorEngine.
 *
 * @cdk.module test-qsarmolecular
 */
public class DescriptorNamesTest extends NewCDKTestCase {

    public DescriptorNamesTest() {
    }

    @Test
    public void checkUniqueMolecularNames() throws CDKException {
        DescriptorEngine engine = new DescriptorEngine(DescriptorEngine.MOLECULAR);
        List<DescriptorSpecification> specs = engine.getDescriptorSpecifications();

        // we work with a simple molecule with 3D coordinates
        String filename = "data/mdl/lobtest2.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new MDLV2000Reader(ins);
        ChemFile content = (ChemFile) reader.read(new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = (IAtomContainer) cList.get(0);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(ac);

        engine.process(ac);

        int ncalc = 0;
        List<String> descNames = new ArrayList<String>();
        for (DescriptorSpecification spec : specs) {
            DescriptorValue value = (DescriptorValue) ac.getProperty(spec);
            if (value == null) continue;
            ncalc++;
            String[] names = value.getNames();
            descNames.addAll(Arrays.asList(names));
        }

        Set<String> uniqueNames = new HashSet<String>(descNames);

        Assert.assertEquals(specs.size(), ncalc);
        Assert.assertEquals(descNames.size(), uniqueNames.size());

    }
}