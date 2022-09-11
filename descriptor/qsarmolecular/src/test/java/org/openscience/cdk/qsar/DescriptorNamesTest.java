/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.IImplementationSpecification;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 * TestSuite that runs all tests for the DescriptorEngine.
 *
 * @cdk.module test-qsarmolecular
 */
class DescriptorNamesTest extends CDKTestCase {

    DescriptorNamesTest() {}

    @Test
    void checkUniqueMolecularDescriptorNames() throws Exception {
        DescriptorEngine engine = new DescriptorEngine(IMolecularDescriptor.class,
                DefaultChemObjectBuilder.getInstance());
        List<IImplementationSpecification> specs = engine.getDescriptorSpecifications();

        // we work with a simple molecule with 3D coordinates
        String filename = "descriptors/molecular/lobtest2.sdf";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new MDLV2000Reader(ins);
        ChemFile content = reader.read(new ChemFile());
        List cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = (IAtomContainer) cList.get(0);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(ac);
        Isotopes.getInstance().configureAtoms(ac);
        engine.process(ac);

        int ncalc = 0;
        List<String> descNames = new ArrayList<>();
        for (IImplementationSpecification spec : specs) {
            DescriptorValue value = ac.getProperty(spec);
            if (value == null) Assertions.fail(spec.getImplementationTitle() + " was not calculated.");
            ncalc++;
            String[] names = value.getNames();
            descNames.addAll(Arrays.asList(names));
        }

        List<String> dups = new ArrayList<>();
        Set<String> uniqueNames = new HashSet<>();
        for (String name : descNames) {
            if (!uniqueNames.add(name)) dups.add(name);
        }
        Assertions.assertEquals(specs.size(), ncalc);
        Assertions.assertEquals(descNames.size(), uniqueNames.size());
        if (dups.size() != 0) {
            System.out.println("Following names were duplicated");
            for (String dup : dups) {
                System.out.println("dup = " + dup);
            }
        }

    }
}
