/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
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

package org.openscience.cdk.modulesuites;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.AminoAcidTest;
import org.openscience.cdk.AtomContainerSetTest;
import org.openscience.cdk.AtomContainerTest;
import org.openscience.cdk.AtomParityTest;
import org.openscience.cdk.AtomTest;
import org.openscience.cdk.AtomTypeTest;
import org.openscience.cdk.BioPolymerTest;
import org.openscience.cdk.BondTest;
import org.openscience.cdk.ChangeEventPropagationTest;
import org.openscience.cdk.ChemFileTest;
import org.openscience.cdk.ChemModelTest;
import org.openscience.cdk.ChemObjectTest;
import org.openscience.cdk.ChemSequenceTest;
import org.openscience.cdk.ConformerContainerTest;
import org.openscience.cdk.CrystalTest;
import org.openscience.cdk.DataCoverageTest;
import org.openscience.cdk.DefaultChemObjectBuilderTest;
import org.openscience.cdk.ElectronContainerTest;
import org.openscience.cdk.ElementTest;
import org.openscience.cdk.FragmentAtomTest;
import org.openscience.cdk.IsotopeTest;
import org.openscience.cdk.LonePairTest;
import org.openscience.cdk.MappingTest;
import org.openscience.cdk.MoleculeSetTest;
import org.openscience.cdk.MoleculeTest;
import org.openscience.cdk.MonomerTest;
import org.openscience.cdk.PolymerTest;
import org.openscience.cdk.PseudoAtomTest;
import org.openscience.cdk.ReactionSetTest;
import org.openscience.cdk.ReactionTest;
import org.openscience.cdk.RingSetTest;
import org.openscience.cdk.RingTest;
import org.openscience.cdk.SingleElectronTest;
import org.openscience.cdk.StrandTest;
import org.openscience.cdk.event.ChemObjectChangeEventTest;
import org.openscience.cdk.formula.AdductFormulaTest;
import org.openscience.cdk.formula.MolecularFormulaSetTest;
import org.openscience.cdk.formula.MolecularFormulaTest;
import org.openscience.cdk.protein.data.PDBAtomTest;
import org.openscience.cdk.protein.data.PDBMonomerTest;
import org.openscience.cdk.protein.data.PDBPolymerTest;
import org.openscience.cdk.protein.data.PDBStructureTest;

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
        suite.addTest(new JUnit4TestAdapter(ChemModelTest.class));
        suite.addTest(new JUnit4TestAdapter(ChemObjectTest.class));
        suite.addTest(new JUnit4TestAdapter(ChemSequenceTest.class));
        suite.addTest(new JUnit4TestAdapter(ConformerContainerTest.class));
        suite.addTest(new JUnit4TestAdapter(CrystalTest.class));
        suite.addTest(new JUnit4TestAdapter(DefaultChemObjectBuilderTest.class));
        suite.addTest(new JUnit4TestAdapter(ElectronContainerTest.class));
        suite.addTest(new JUnit4TestAdapter(ElementTest.class));
        suite.addTest(new JUnit4TestAdapter(IsotopeTest.class));
        suite.addTest(new JUnit4TestAdapter(LonePairTest.class));
        suite.addTest(new JUnit4TestAdapter(MappingTest.class));
        suite.addTest(new JUnit4TestAdapter(MoleculeTest.class));
        suite.addTest(new JUnit4TestAdapter(MonomerTest.class));
        suite.addTest(new JUnit4TestAdapter(PolymerTest.class));
        suite.addTest(new JUnit4TestAdapter(PseudoAtomTest.class));
        suite.addTest(new JUnit4TestAdapter(ReactionTest.class));
        suite.addTest(new JUnit4TestAdapter(RingTest.class));
        suite.addTest(new JUnit4TestAdapter(RingSetTest.class));
        suite.addTest(new JUnit4TestAdapter(AtomContainerSetTest.class));
        suite.addTest(new JUnit4TestAdapter(MoleculeSetTest.class));
        suite.addTest(new JUnit4TestAdapter(ReactionSetTest.class));
        suite.addTest(new JUnit4TestAdapter(SingleElectronTest.class));
        suite.addTest(new JUnit4TestAdapter(StrandTest.class));
        suite.addTest(new JUnit4TestAdapter(ChangeEventPropagationTest.class));

        suite.addTest(new JUnit4TestAdapter(FragmentAtomTest.class));

        suite.addTest(new JUnit4TestAdapter(AdductFormulaTest.class));
        suite.addTest(new JUnit4TestAdapter(MolecularFormulaSetTest.class));
        suite.addTest(new JUnit4TestAdapter(MolecularFormulaTest.class));

        // test from test.event
        suite.addTest(new JUnit4TestAdapter(ChemObjectChangeEventTest.class));
        
        // tests from test.protein.data
        suite.addTest(new JUnit4TestAdapter(PDBAtomTest.class));
        suite.addTest(new JUnit4TestAdapter(PDBMonomerTest.class));
        suite.addTest(new JUnit4TestAdapter(PDBPolymerTest.class));
        suite.addTest(new JUnit4TestAdapter(PDBStructureTest.class));
        
        return suite;
    }

}
