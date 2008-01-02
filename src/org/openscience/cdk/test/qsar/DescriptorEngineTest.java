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
package org.openscience.cdk.test.qsar;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.qsar.DescriptorEngine;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.test.CDKTestCase;

/**
 * TestSuite that runs all tests for the DescriptorEngine.
 *
 * @cdk.module test-qsarmolecular
 */
public class    DescriptorEngineTest extends CDKTestCase {

    public DescriptorEngineTest() {
    }

    public static Test suite() {
        return new TestSuite(DescriptorEngineTest.class);
    }

    public void testConstructor() {
        DescriptorEngine engine = new DescriptorEngine(DescriptorEngine.MOLECULAR);
        assertNotNull(engine);
    }
        
    public void testLoadingOfMolecularDescriptors() {
    	DescriptorEngine engine = new DescriptorEngine(DescriptorEngine.MOLECULAR);
        assertNotNull(engine);
        int loadedDescriptors = engine.getDescriptorInstances().size(); 
        assertNotSame(0, loadedDescriptors);
        assertEquals(loadedDescriptors, engine.getDescriptorClassNames().size());
        assertEquals(loadedDescriptors, engine.getDescriptorSpecifications().size());
    }

    public void testLoadingOfAtomicDescriptors() {
        DescriptorEngine engine = new DescriptorEngine(DescriptorEngine.ATOMIC);
        assertNotNull(engine);
        int loadedDescriptors = engine.getDescriptorInstances().size(); 
        assertNotSame(0, loadedDescriptors);
        assertEquals(loadedDescriptors, engine.getDescriptorClassNames().size());
        assertEquals(loadedDescriptors, engine.getDescriptorSpecifications().size());
    }

    public void testLoadingOfBondDescriptors() {
        DescriptorEngine engine = new DescriptorEngine(DescriptorEngine.BOND);
        assertNotNull(engine);
        int loadedDescriptors = engine.getDescriptorInstances().size(); 
        assertNotSame(0, loadedDescriptors);
        assertEquals(loadedDescriptors, engine.getDescriptorClassNames().size());
        assertEquals(loadedDescriptors, engine.getDescriptorSpecifications().size());
    }

    public void testDictionaryType() {
        DescriptorEngine engine = new DescriptorEngine(DescriptorEngine.MOLECULAR);

        String className = "org.openscience.cdk.qsar.descriptors.molecular.ZagrebIndexDescriptor";
        DescriptorSpecification specRef = new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#zagrebIndex",
                this.getClass().getName(),
                "$Id$",
                "The Chemistry Development Kit");

        Assert.assertEquals("molecularDescriptor", engine.getDictionaryType(className));
        Assert.assertEquals("molecularDescriptor", engine.getDictionaryType(specRef));
    }

    public void testDictionaryClass() {
        DescriptorEngine engine = new DescriptorEngine(DescriptorEngine.MOLECULAR);

        String className = "org.openscience.cdk.qsar.descriptors.molecular.TPSADescriptor";
        DescriptorSpecification specRef = new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#tpsa",
                this.getClass().getName(),
                "$Id$",
                "The Chemistry Development Kit");

        String[] dictClass = engine.getDictionaryClass(className);
        Assert.assertEquals(2, dictClass.length);
        System.out.println(dictClass[0]+" "+dictClass[1]);
        Assert.assertEquals("topologicalDescriptor", dictClass[0]);
        Assert.assertEquals("electronicDescriptor", dictClass[1]);

        dictClass = engine.getDictionaryClass(specRef);
        Assert.assertEquals(2, dictClass.length);
        Assert.assertEquals("topologicalDescriptor", dictClass[0]);
        Assert.assertEquals("electronicDescriptor", dictClass[1]);
    }

    public void testAvailableClass() {
        DescriptorEngine engine = new DescriptorEngine(DescriptorEngine.MOLECULAR);
        String[] availClasses = engine.getAvailableDictionaryClasses();    
        for (int i=0; i<availClasses.length; i++) {
        	System.out.println("avail class: " + availClasses[i]);
        }
        Assert.assertEquals(6, availClasses.length);
    }
}

