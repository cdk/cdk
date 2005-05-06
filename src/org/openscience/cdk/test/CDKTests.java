/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.openscience.cdk.applications.swing.MoleculeListViewer;
import org.openscience.cdk.test.applications.APIVersionTesterTest;
import org.openscience.cdk.test.aromaticity.HueckelAromaticityDetectorTest;
import org.openscience.cdk.test.atomtype.HybridizationMatcherTest;
import org.openscience.cdk.test.atomtype.HybridizationStateATMatcherTest;
import org.openscience.cdk.test.config.ConfigTests;
import org.openscience.cdk.test.fingerprint.FingerprinterTest;
import org.openscience.cdk.test.geometry.CrystalGeometryToolsTest;
import org.openscience.cdk.test.geometry.GeometryToolsTest;
import org.openscience.cdk.test.geometry.RDFCalculatorTest;
import org.openscience.cdk.test.geometry.alignment.KabschAlignmentTest;
import org.openscience.cdk.test.graph.GraphTests;
import org.openscience.cdk.test.index.CASNumberTest;
import org.openscience.cdk.test.io.IOTests;
import org.openscience.cdk.test.isomorphism.IsomorphismTests;
import org.openscience.cdk.test.layout.HydrogenPlacerTest;
import org.openscience.cdk.test.layout.StructureDiagramGeneratorTest;
import org.openscience.cdk.test.layout.OverlapResolverTest;
import org.openscience.cdk.test.layout.TemplateHandlerTest;
import org.openscience.cdk.test.math.MathToolsTest;
import org.openscience.cdk.test.reaction.ReactionBalancerTest;
import org.openscience.cdk.test.ringsearch.AllRingsFinderTest;
import org.openscience.cdk.test.ringsearch.RingSearchTest;
import org.openscience.cdk.test.smiles.SmilesGeneratorTest;
import org.openscience.cdk.test.smiles.SmilesParserTest;
import org.openscience.cdk.test.structgen.RandomStructureGeneratorTest;
import org.openscience.cdk.test.tools.ToolsTests;
import org.openscience.cdk.test.charges.*;
import org.openscience.cdk.test.qsar.*;
import org.openscience.cdk.test.qsar.model.*;
import org.openscience.cdk.test.modeling.builder3d.ModelBuilder3dTest;
import org.openscience.cdk.test.modeling.forcefield.ForceFieldTests;

/**
 * TestSuite that runs all the sample tests
 *
 * @cdk.module test
 * @cdk.depends log4j.jar
 * @cdk.depends junit.jar
 */
public class CDKTests
{
    
    static MoleculeListViewer moleculeListViewer = null;
    
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(suite());
    }
    public static Test suite( )
    {
        TestSuite suite= new TestSuite("All CDK Tests");
        
        // Package Test Suites
        suite.addTest(ConfigTests.suite());
        suite.addTest(DataClassesTests.suite());
        suite.addTest(CoreClassesTests.suite());
        suite.addTest(IOTests.suite());
        suite.addTest(ToolsTests.suite());
        
        // Individual Tests
        // from cdk.test.applications
        suite.addTest(APIVersionTesterTest.suite());
        // from cdk.test.aromaticity
        suite.addTest(HueckelAromaticityDetectorTest.suite());
        // from cdk.test.atomtype
        suite.addTest(HybridizationStateATMatcherTest.suite());
        suite.addTest(HybridizationMatcherTest.suite());
        // from cdk.test.charges
        suite.addTest(GasteigerMarsiliPartialChargesTest.suite());
        suite.addTest(MMFF94PartialChargesTest.suite());
        suite.addTest(InductivePartialChargesTest.suite());
        // from cdk.test.modeling
        suite.addTest(ModelBuilder3dTest.suite());
        // from cdk.test.fingerprint
        suite.addTest(FingerprinterTest.suite());
        // from cdk.test.geometry
        suite.addTest(GeometryToolsTest.suite());
        suite.addTest(CrystalGeometryToolsTest.suite());
        suite.addTest(RDFCalculatorTest.suite());
        // from cdk.test.geometry.align
        suite.addTest(KabschAlignmentTest.suite());
        // from cdk.test.graph
        suite.addTest(GraphTests.suite());
        // from cdk.test.index
        suite.addTest(CASNumberTest.suite());
        // from cdk.test.isomorphism
        suite.addTest(IsomorphismTests.suite());
        // from cdk.test.layout
        suite.addTest(StructureDiagramGeneratorTest.suite());
        suite.addTest(HydrogenPlacerTest.suite());
        suite.addTest(OverlapResolverTest.suite());
        suite.addTest(TemplateHandlerTest.suite());
        // from cdk.test.math
        suite.addTest(MathToolsTest.suite());
        // from cdk.test.qsar
        suite.addTest(QSARDescriptorTests.suite());
        // from cdk.test.reaction
        suite.addTest(ReactionBalancerTest.suite());
        // from cdk.test.ringsearch
        suite.addTest(AllRingsFinderTest.suite());
        suite.addTest(RingSearchTest.suite());
        // from cdk.test.smiles
        suite.addTest(SmilesGeneratorTest.suite());
        suite.addTest(SmilesParserTest.suite());
        // from cdk.test.structgen
        suite.addTest(RandomStructureGeneratorTest.suite());
        // from cdk.test.modeling.forcefield
        suite.addTest(ForceFieldTests.suite());

        // Below are the tests that are not always possible to execute, because
        // the class might not be compiled (depeding on Ant and Java VM versions).

        // from cdk.test.iupac
        try {
            Class testClass = ClassLoader.getSystemClassLoader().loadClass("org.openscience.cdk.test.iupac.ParserTest");
            suite.addTest(new TestSuite(testClass));
        } catch (Exception exception) {
            // ok, do without. Probably compiled with Ant < 1.6
            System.out.println("Could not load the IUPAC Parser test: " + exception.getMessage());
        }

        // from cdk.test.qsar.model
        suite.addTest(QSARRModelTests.suite());

        return suite;
    }
    
}
