/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
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

package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.applications.swing.MoleculeListViewer;
import org.openscience.cdk.test.applications.APIVersionTesterTest;
import org.openscience.cdk.test.applications.undoredo.AddAtomsAndBondsEditTest;
import org.openscience.cdk.test.applications.undoredo.AddHydrogenEditTest;
import org.openscience.cdk.test.applications.undoredo.AdjustBondOrdersEditTest;
import org.openscience.cdk.test.applications.undoredo.ChangeAtomSymbolEditTest;
import org.openscience.cdk.test.applications.undoredo.ChangeChargeEditTest;
import org.openscience.cdk.test.applications.undoredo.ChangeIsotopeEditTest;
import org.openscience.cdk.test.applications.undoredo.CleanUpEditTest;
import org.openscience.cdk.test.applications.undoredo.ConvertToPseudoAtomEditTest;
import org.openscience.cdk.test.applications.undoredo.ConvertToRadicalEditTest;
import org.openscience.cdk.test.applications.undoredo.FlipEditTest;
import org.openscience.cdk.test.applications.undoredo.RemoveAtomsAndBondsEditTest;
import org.openscience.cdk.test.aromaticity.HueckelAromaticityDetectorTest;
import org.openscience.cdk.test.atomtype.CDKChemicalRingConstantsTest;
import org.openscience.cdk.test.atomtype.HybridizationMatcherTest;
import org.openscience.cdk.test.atomtype.HybridizationStateATMatcherTest;
import org.openscience.cdk.test.atomtype.MM2AtomTypeMatcherTest;
import org.openscience.cdk.test.atomtype.MMFF94AtomTypeMatcherTest;
import org.openscience.cdk.test.charges.GasteigerMarsiliPartialChargesTest;
import org.openscience.cdk.test.charges.InductivePartialChargesTest;
import org.openscience.cdk.test.charges.MMFF94PartialChargesTest;
import org.openscience.cdk.test.config.ConfigTests;
import org.openscience.cdk.test.debug.DebugDataClassesTests;
import org.openscience.cdk.test.dict.DictDBTest;
import org.openscience.cdk.test.dict.DictRefTest;
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
import org.openscience.cdk.test.layout.OverlapResolverTest;
import org.openscience.cdk.test.layout.StructureDiagramGeneratorTest;
import org.openscience.cdk.test.layout.TemplateHandlerTest;
import org.openscience.cdk.test.libio.openbabel.OpenBabelConvertTest;
import org.openscience.cdk.test.math.MathToolsTest;
import org.openscience.cdk.test.modeling.builder3d.ModelBuilder3dTest;
import org.openscience.cdk.test.modeling.forcefield.ForceFieldTests;
import org.openscience.cdk.test.qsar.descriptors.QSARDescriptorTests;
import org.openscience.cdk.test.qsar.model.QSARRModelTests;
import org.openscience.cdk.test.reaction.type.ReactionTypeTest;
import org.openscience.cdk.test.ringsearch.AllRingsFinderTest;
import org.openscience.cdk.test.ringsearch.RingPartitionerTest;
import org.openscience.cdk.test.ringsearch.RingSearchTest;
import org.openscience.cdk.test.similarity.TanimotoTest;
import org.openscience.cdk.test.smiles.SmilesGeneratorTest;
import org.openscience.cdk.test.smiles.SmilesParserTest;
import org.openscience.cdk.test.structgen.RandomStructureGeneratorTest;
import org.openscience.cdk.test.templates.AminoAcidsTest;
import org.openscience.cdk.test.tools.ToolsTests;

/**
 * TestSuite that runs all the sample tests.
 *
 * @cdk.module  test-extra
 * @cdk.depends log4j.jar
 * @cdk.depends junit.jar
 */
public class MextraTests {
    
    static MoleculeListViewer moleculeListViewer = null;
    
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(suite());
    }
    public static Test suite( )
    {
        TestSuite suite= new TestSuite("All CDK Tests");

        // Tests for data classes
        suite.addTest(DataClassesTests.suite());
        suite.addTest(DebugDataClassesTests.suite());
        
        // Package Test Suites
        suite.addTest(ConfigTests.suite());
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
        suite.addTest(MMFF94AtomTypeMatcherTest.suite());
        suite.addTest(CDKChemicalRingConstantsTest.suite());
        suite.addTest(MM2AtomTypeMatcherTest.suite());
        // from cdk.test.dict
        suite.addTest(DictRefTest.suite());
        suite.addTest(DictDBTest.suite());
        // from cdk.test.charges
        suite.addTest(GasteigerMarsiliPartialChargesTest.suite());
        suite.addTest(MMFF94PartialChargesTest.suite());
        suite.addTest(InductivePartialChargesTest.suite());
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
        // from cdk.test.libio.openbabel
        suite.addTest(OpenBabelConvertTest.suite());
        // from cdk.test.math
        suite.addTest(MathToolsTest.suite());
        // from cdk.test.modeling
        suite.addTest(ModelBuilder3dTest.suite());
        // from cdk.test.modeling.forcefield
        suite.addTest(ForceFieldTests.suite());
        // from cdk.test.qsar
        suite.addTest(QSARDescriptorTests.suite());
        // from cdk.test.reaction
        suite.addTest(ReactionTypeTest.suite());
        // from cdk.test.ringsearch
        suite.addTest(AllRingsFinderTest.suite());
        suite.addTest(RingSearchTest.suite());
        suite.addTest(RingPartitionerTest.suite());
        // from cdk.test.similarity
        suite.addTest(TanimotoTest.suite());
        // from cdk.test.smiles
        suite.addTest(SmilesGeneratorTest.suite());
        suite.addTest(SmilesParserTest.suite());
        // from cdk.test.structgen
        suite.addTest(RandomStructureGeneratorTest.suite());
        // from cdk.test.templates
        suite.addTest(AminoAcidsTest.suite());

        // Below are the tests that are not always possible to execute, because
        // the class might not be compiled (depeding on Ant and Java VM versions).

        // from cdk.test.iupac
        try {
            Class testClass = ClassLoader.getSystemClassLoader().loadClass("org.openscience.cdk.test.iupac.ParserTest");
            suite.addTest(new TestSuite(testClass));
            System.out.println("Found IUPAC Parser test.");
        } catch (Exception exception) {
            // ok, do without. Probably compiled with Ant < 1.6
            System.out.println("Could not load the IUPAC Parser test: " + exception.getMessage());
        }

        // from cdk.test.qsar.model
        suite.addTest(QSARRModelTests.suite());
        
        //from cdk.test.applications.jchempaint.undoredo
        suite.addTest(ConvertToPseudoAtomEditTest.suite());
        suite.addTest(ConvertToRadicalEditTest.suite());
        suite.addTest(ChangeIsotopeEditTest.suite());
        suite.addTest(ChangeAtomSymbolEditTest.suite());
        suite.addTest(CleanUpEditTest.suite());
        suite.addTest(AddHydrogenEditTest.suite());
        suite.addTest(AdjustBondOrdersEditTest.suite());
        suite.addTest(FlipEditTest.suite());
        suite.addTest(AddAtomsAndBondsEditTest.suite());
        suite.addTest(RemoveAtomsAndBondsEditTest.suite());
        suite.addTest(ChangeChargeEditTest.suite());
        return suite;
    }
    
}
