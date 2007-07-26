/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.test.applications.undoredo.*;
import org.openscience.cdk.test.charges.GasteigerMarsiliPartialChargesTest;
import org.openscience.cdk.test.charges.InductivePartialChargesTest;
import org.openscience.cdk.test.charges.MMFF94PartialChargesTest;
import org.openscience.cdk.test.dict.DictDBTest;
import org.openscience.cdk.test.geometry.RDFCalculatorTest;
import org.openscience.cdk.test.geometry.alignment.KabschAlignmentTest;
import org.openscience.cdk.test.index.CASNumberTest;
import org.openscience.cdk.test.layout.HydrogenPlacerTest;
import org.openscience.cdk.test.layout.OverlapResolverTest;
import org.openscience.cdk.test.layout.StructureDiagramGeneratorTest;
import org.openscience.cdk.test.layout.TemplateHandlerTest;
import org.openscience.cdk.test.libio.openbabel.OpenBabelConvertTest;
import org.openscience.cdk.test.similarity.DistanceMomentTest;
import org.openscience.cdk.test.similarity.TanimotoTest;
import org.openscience.cdk.test.tools.HOSECodeAnalyserTest;
import org.openscience.cdk.test.tools.HOSECodeGeneratorTest;

/**
 * TestSuite that runs all the sample tests.
 *
 * @cdk.module  test-extra
 * @cdk.depends log4j.jar
 * @cdk.depends junit.jar
 */
public class MextraTests {
    
    static MoleculeListViewer moleculeListViewer = null;
    
    public static Test suite( )
    {
        TestSuite suite= new TestSuite("All CDK Tests");

        // Individual Tests
        // from cdk.test.applications
        // from cdk.test.aromaticity
        // from cdk.test.dict
        suite.addTest(DictDBTest.suite());
        // from cdk.test.charges
        suite.addTest(GasteigerMarsiliPartialChargesTest.suite());
        suite.addTest(MMFF94PartialChargesTest.suite());
        suite.addTest(InductivePartialChargesTest.suite());
        // from cdk.test.geometry
        suite.addTest(RDFCalculatorTest.suite());
        // from cdk.test.geometry.align
        suite.addTest(KabschAlignmentTest.suite());
        // from cdk.test.index
        suite.addTest(CASNumberTest.suite());
        // from cdk.test.isomorphism
        // from cdk.test.layout
        suite.addTest(StructureDiagramGeneratorTest.suite());
        suite.addTest(HydrogenPlacerTest.suite());
        suite.addTest(OverlapResolverTest.suite());
        suite.addTest(TemplateHandlerTest.suite());
        // from cdk.test.libio.openbabel
        suite.addTest(OpenBabelConvertTest.suite());
        // from cdk.test.math
        // from cdk.test.similarity
        suite.addTest(TanimotoTest.suite());
        suite.addTest(DistanceMomentTest.suite());
        suite.addTest(HOSECodeGeneratorTest.suite());
        suite.addTest(HOSECodeAnalyserTest.suite());
        
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
        
        // other
        suite.addTest(VariousTests.suite());        
        
        return suite;
    }
    
}
