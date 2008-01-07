/* $RCSfile$    
 * $Author: egonw $    
 * $Date: 2006-03-30 00:42:34 +0200 (Thu, 30 Mar 2006) $    
 * $Revision: 5865 $
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

package org.openscience.cdk.test.modulesuites;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.test.AminoAcidTest;
import org.openscience.cdk.test.AtomContainerSetTest;
import org.openscience.cdk.test.AtomContainerTest;
import org.openscience.cdk.test.AtomParityTest;
import org.openscience.cdk.test.AtomTest;
import org.openscience.cdk.test.AtomTypeTest;
import org.openscience.cdk.test.BioPolymerTest;
import org.openscience.cdk.test.BondTest;
import org.openscience.cdk.test.ChangeEventPropagationTest;
import org.openscience.cdk.test.ChemFileTest;
import org.openscience.cdk.test.ChemModelTest;
import org.openscience.cdk.test.ChemObjectTest;
import org.openscience.cdk.test.ChemSequenceTest;
import org.openscience.cdk.test.ConformerContainerTest;
import org.openscience.cdk.test.CrystalTest;
import org.openscience.cdk.test.DataCoverageTest;
import org.openscience.cdk.test.DefaultChemObjectBuilderTest;
import org.openscience.cdk.test.ElectronContainerTest;
import org.openscience.cdk.test.ElementTest;
import org.openscience.cdk.test.FragmentAtomTest;
import org.openscience.cdk.test.IsotopeTest;
import org.openscience.cdk.test.LonePairTest;
import org.openscience.cdk.test.MappingTest;
import org.openscience.cdk.test.MoleculeSetTest;
import org.openscience.cdk.test.MoleculeTest;
import org.openscience.cdk.test.MonomerTest;
import org.openscience.cdk.test.PolymerTest;
import org.openscience.cdk.test.PseudoAtomTest;
import org.openscience.cdk.test.ReactionSetTest;
import org.openscience.cdk.test.ReactionTest;
import org.openscience.cdk.test.RingSetTest;
import org.openscience.cdk.test.RingTest;
import org.openscience.cdk.test.SingleElectronTest;
import org.openscience.cdk.test.StrandTest;
import org.openscience.cdk.test.event.ChemObjectChangeEventTest;
import org.openscience.cdk.test.protein.data.PDBAtomTest;
import org.openscience.cdk.test.protein.data.PDBMonomerTest;
import org.openscience.cdk.test.protein.data.PDBPolymerTest;
import org.openscience.cdk.test.protein.data.PDBStructureTest;

/**
 * TestSuite that runs all the sample tests.
 *
 * @cdk.module test-data
 */
public class MdataTests {

    public static Test suite () {
        TestSuite suite= new TestSuite("The CDK data module Tests");
        
        suite.addTest(DataCoverageTest.suite());
        
        suite.addTest(new JUnit4TestAdapter(AminoAcidTest.class));
        suite.addTest(new JUnit4TestAdapter(AtomContainerTest.class));
        //suite.addTest(AtomEnumerationTest.suite());
        suite.addTest(new JUnit4TestAdapter(AtomParityTest.class));
        suite.addTest(new JUnit4TestAdapter(AtomTest.class));
        suite.addTest(new JUnit4TestAdapter(AtomTypeTest.class));
        suite.addTest(new JUnit4TestAdapter(BioPolymerTest.class));
        suite.addTest(new JUnit4TestAdapter(BondTest.class));
        suite.addTest(new JUnit4TestAdapter(ChemFileTest.class));
        suite.addTest(ChemModelTest.suite());
        suite.addTest(ChemObjectTest.suite());
        suite.addTest(ChemSequenceTest.suite());
        suite.addTest(new JUnit4TestAdapter(ConformerContainerTest.class));
        suite.addTest(CrystalTest.suite());
        suite.addTest(new JUnit4TestAdapter(DefaultChemObjectBuilderTest.class));
        suite.addTest(ElectronContainerTest.suite());
        suite.addTest(ElementTest.suite());
        suite.addTest(IsotopeTest.suite());
        suite.addTest(LonePairTest.suite());
        suite.addTest(MappingTest.suite());
        suite.addTest(MoleculeTest.suite());
        suite.addTest(MonomerTest.suite());
        suite.addTest(PolymerTest.suite());
        suite.addTest(PseudoAtomTest.suite());
        suite.addTest(ReactionTest.suite());
        suite.addTest(RingTest.suite());
        suite.addTest(RingSetTest.suite());
        suite.addTest(new JUnit4TestAdapter(AtomContainerSetTest.class));
        suite.addTest(MoleculeSetTest.suite());
        suite.addTest(ReactionSetTest.suite());
        suite.addTest(SingleElectronTest.suite());
        suite.addTest(StrandTest.suite());
        suite.addTest(ChangeEventPropagationTest.suite());

        suite.addTest(FragmentAtomTest.suite());
        
        // test from test.event
        suite.addTest(ChemObjectChangeEventTest.suite());
        
        // tests from test.protein.data
        suite.addTest(PDBAtomTest.suite());
        suite.addTest(PDBMonomerTest.suite());
        suite.addTest(PDBPolymerTest.suite());
        suite.addTest(PDBStructureTest.suite());
        
        return suite;
    }

}
