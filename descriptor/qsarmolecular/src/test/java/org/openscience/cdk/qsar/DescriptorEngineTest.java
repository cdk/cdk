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

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;

/**
 * TestSuite that runs all tests for the DescriptorEngine.
 *
 * @cdk.module test-qsarmolecular
 */
public class DescriptorEngineTest extends CDKTestCase {

    public DescriptorEngineTest() {}

    @Test
    public void testConstructor() {
        DescriptorEngine engine = new DescriptorEngine(IMolecularDescriptor.class,
                DefaultChemObjectBuilder.getInstance());
        Assert.assertNotNull(engine);
    }

    @Test
    public void testLoadingOfMolecularDescriptors() {
        DescriptorEngine engine = new DescriptorEngine(IMolecularDescriptor.class,
                DefaultChemObjectBuilder.getInstance());
        Assert.assertNotNull(engine);
        int loadedDescriptors = engine.getDescriptorInstances().size();
        Assert.assertTrue("Could not load any descriptors", 0 != loadedDescriptors);
        Assert.assertEquals(loadedDescriptors, engine.getDescriptorClassNames().size());
        Assert.assertEquals(loadedDescriptors, engine.getDescriptorSpecifications().size());
    }

    @Test
    public void testLoadingOfAtomicDescriptors() {
        DescriptorEngine engine = new DescriptorEngine(IAtomicDescriptor.class, DefaultChemObjectBuilder.getInstance());
        Assert.assertNotNull(engine);
        int loadedDescriptors = engine.getDescriptorInstances().size();
        Assert.assertNotSame(0, loadedDescriptors);
        Assert.assertEquals(loadedDescriptors, engine.getDescriptorClassNames().size());
        Assert.assertEquals(loadedDescriptors, engine.getDescriptorSpecifications().size());
    }

    @Test
    public void testLoadingOfBondDescriptors() {
        DescriptorEngine engine = new DescriptorEngine(IBondDescriptor.class, DefaultChemObjectBuilder.getInstance());
        Assert.assertNotNull(engine);
        int loadedDescriptors = engine.getDescriptorInstances().size();
        Assert.assertNotSame(0, loadedDescriptors);
        Assert.assertEquals(loadedDescriptors, engine.getDescriptorClassNames().size());
        Assert.assertEquals(loadedDescriptors, engine.getDescriptorSpecifications().size());
    }

    @Test
    public void testDictionaryType() {
        DescriptorEngine engine = new DescriptorEngine(IMolecularDescriptor.class,
                DefaultChemObjectBuilder.getInstance());

        String className = "org.openscience.cdk.qsar.descriptors.molecular.ZagrebIndexDescriptor";
        DescriptorSpecification specRef = new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#zagrebIndex", this.getClass()
                        .getName(), "The Chemistry Development Kit");

        Assert.assertEquals("molecularDescriptor", engine.getDictionaryType(className));
        Assert.assertEquals("molecularDescriptor", engine.getDictionaryType(specRef));
    }

    @Test
    public void testDictionaryClass() {
        DescriptorEngine engine = new DescriptorEngine(IMolecularDescriptor.class,
                DefaultChemObjectBuilder.getInstance());

        String className = "org.openscience.cdk.qsar.descriptors.molecular.TPSADescriptor";
        DescriptorSpecification specRef = new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#tpsa", this.getClass().getName(),
                "The Chemistry Development Kit");

        String[] dictClass = engine.getDictionaryClass(className);
        Assert.assertEquals(2, dictClass.length);
        Assert.assertEquals("topologicalDescriptor", dictClass[0]);
        Assert.assertEquals("electronicDescriptor", dictClass[1]);

        dictClass = engine.getDictionaryClass(specRef);
        Assert.assertEquals(2, dictClass.length);
        Assert.assertEquals("topologicalDescriptor", dictClass[0]);
        Assert.assertEquals("electronicDescriptor", dictClass[1]);
    }

    @Test
    public void testAvailableClass() {
        DescriptorEngine engine = new DescriptorEngine(IMolecularDescriptor.class,
                DefaultChemObjectBuilder.getInstance());
        String[] availClasses = engine.getAvailableDictionaryClasses();
        Assert.assertEquals(5, availClasses.length);
    }

    @Test
    public void testLoadingOfAtomPairDescriptors() {
        DescriptorEngine engine = new DescriptorEngine(IAtomicDescriptor.class, DefaultChemObjectBuilder.getInstance());
        Assert.assertNotNull(engine);
        int loadedDescriptors = engine.getDescriptorInstances().size();
        Assert.assertNotSame(0, loadedDescriptors);
        Assert.assertEquals(loadedDescriptors, engine.getDescriptorClassNames().size());
        Assert.assertEquals(loadedDescriptors, engine.getDescriptorSpecifications().size());
    }
}
