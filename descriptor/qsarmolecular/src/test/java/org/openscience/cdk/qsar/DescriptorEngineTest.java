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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;

/**
 * TestSuite that runs all tests for the DescriptorEngine.
 *
 * @cdk.module test-qsarmolecular
 */
class DescriptorEngineTest extends CDKTestCase {

    DescriptorEngineTest() {}

    @Test
    void testConstructor() {
        DescriptorEngine engine = new DescriptorEngine(IMolecularDescriptor.class,
                DefaultChemObjectBuilder.getInstance());
        Assertions.assertNotNull(engine);
    }

    @Test
    void testLoadingOfMolecularDescriptors() {
        DescriptorEngine engine = new DescriptorEngine(IMolecularDescriptor.class,
                DefaultChemObjectBuilder.getInstance());
        Assertions.assertNotNull(engine);
        int loadedDescriptors = engine.getDescriptorInstances().size();
        Assertions.assertTrue(0 != loadedDescriptors, "Could not load any descriptors");
        Assertions.assertEquals(loadedDescriptors, engine.getDescriptorClassNames().size());
        Assertions.assertEquals(loadedDescriptors, engine.getDescriptorSpecifications().size());
    }

    @Test
    void testLoadingOfAtomicDescriptors() {
        DescriptorEngine engine = new DescriptorEngine(IAtomicDescriptor.class, DefaultChemObjectBuilder.getInstance());
        Assertions.assertNotNull(engine);
        int loadedDescriptors = engine.getDescriptorInstances().size();
        Assertions.assertNotSame(0, loadedDescriptors);
        Assertions.assertEquals(loadedDescriptors, engine.getDescriptorClassNames().size());
        Assertions.assertEquals(loadedDescriptors, engine.getDescriptorSpecifications().size());
    }

    @Test
    void testLoadingOfBondDescriptors() {
        DescriptorEngine engine = new DescriptorEngine(IBondDescriptor.class, DefaultChemObjectBuilder.getInstance());
        Assertions.assertNotNull(engine);
        int loadedDescriptors = engine.getDescriptorInstances().size();
        Assertions.assertNotSame(0, loadedDescriptors);
        Assertions.assertEquals(loadedDescriptors, engine.getDescriptorClassNames().size());
        Assertions.assertEquals(loadedDescriptors, engine.getDescriptorSpecifications().size());
    }

    @Test
    void testDictionaryType() {
        DescriptorEngine engine = new DescriptorEngine(IMolecularDescriptor.class,
                DefaultChemObjectBuilder.getInstance());

        String className = "org.openscience.cdk.qsar.descriptors.molecular.ZagrebIndexDescriptor";
        DescriptorSpecification specRef = new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#zagrebIndex", this.getClass()
                        .getName(), "The Chemistry Development Kit");

        Assertions.assertEquals("molecularDescriptor", engine.getDictionaryType(className));
        Assertions.assertEquals("molecularDescriptor", engine.getDictionaryType(specRef));
    }

    @Test
    void testDictionaryClass() {
        DescriptorEngine engine = new DescriptorEngine(IMolecularDescriptor.class,
                DefaultChemObjectBuilder.getInstance());

        String className = "org.openscience.cdk.qsar.descriptors.molecular.TPSADescriptor";
        DescriptorSpecification specRef = new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#tpsa", this.getClass().getName(),
                "The Chemistry Development Kit");

        String[] dictClass = engine.getDictionaryClass(className);
        Assertions.assertEquals(2, dictClass.length);
        Assertions.assertEquals("topologicalDescriptor", dictClass[0]);
        Assertions.assertEquals("electronicDescriptor", dictClass[1]);

        dictClass = engine.getDictionaryClass(specRef);
        Assertions.assertEquals(2, dictClass.length);
        Assertions.assertEquals("topologicalDescriptor", dictClass[0]);
        Assertions.assertEquals("electronicDescriptor", dictClass[1]);
    }

    @Test
    void testAvailableClass() {
        DescriptorEngine engine = new DescriptorEngine(IMolecularDescriptor.class,
                DefaultChemObjectBuilder.getInstance());
        String[] availClasses = engine.getAvailableDictionaryClasses();
        Assertions.assertEquals(5, availClasses.length);
    }

    @Test
    void testLoadingOfAtomPairDescriptors() {
        DescriptorEngine engine = new DescriptorEngine(IAtomicDescriptor.class, DefaultChemObjectBuilder.getInstance());
        Assertions.assertNotNull(engine);
        int loadedDescriptors = engine.getDescriptorInstances().size();
        Assertions.assertNotSame(0, loadedDescriptors);
        Assertions.assertEquals(loadedDescriptors, engine.getDescriptorClassNames().size());
        Assertions.assertEquals(loadedDescriptors, engine.getDescriptorSpecifications().size());
    }
}
