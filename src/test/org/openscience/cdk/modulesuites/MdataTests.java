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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
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
import org.openscience.cdk.ElectronContainerTest;
import org.openscience.cdk.ElementTest;
import org.openscience.cdk.FragmentAtomTest;
import org.openscience.cdk.IsotopeTest;
import org.openscience.cdk.LonePairTest;
import org.openscience.cdk.MappingTest;
import org.openscience.cdk.MoleculeSetTest;
import org.openscience.cdk.MoleculeTest;
import org.openscience.cdk.MonomerTest;
import org.openscience.cdk.DefaultChemObjectBuilderTest;
import org.openscience.cdk.PolymerTest;
import org.openscience.cdk.PseudoAtomTest;
import org.openscience.cdk.ReactionSetTest;
import org.openscience.cdk.ReactionTest;
import org.openscience.cdk.RingSetTest;
import org.openscience.cdk.RingTest;
import org.openscience.cdk.SingleElectronTest;
import org.openscience.cdk.StrandTest;
import org.openscience.cdk.coverage.DataCoverageTest;
import org.openscience.cdk.event.ChemObjectChangeEventTest;
import org.openscience.cdk.formula.AdductFormulaTest;
import org.openscience.cdk.formula.MolecularFormulaSetTest;
import org.openscience.cdk.formula.MolecularFormulaTest;
import org.openscience.cdk.protein.data.PDBAtomTest;
import org.openscience.cdk.protein.data.PDBMonomerTest;
import org.openscience.cdk.protein.data.PDBPolymerTest;
import org.openscience.cdk.protein.data.PDBStructureTest;
import org.openscience.cdk.stereo.TetrahedralChiralityTest;

/**
 * TestSuite that runs all the sample tests.
 *
 * @cdk.module test-data
 */
@RunWith(value=Suite.class)
@SuiteClasses(value={
    DataCoverageTest.class,
        
    AminoAcidTest.class,
    AtomContainerTest.class,
    AtomParityTest.class,
    AtomTest.class,
    AtomTypeTest.class,
    BioPolymerTest.class,
    BondTest.class,
    ChemFileTest.class,
    ChemModelTest.class,
    ChemObjectTest.class,
    ChemSequenceTest.class,
    ConformerContainerTest.class,
    CrystalTest.class,
    DefaultChemObjectBuilderTest.class,
    ElectronContainerTest.class,
    ElementTest.class,
    IsotopeTest.class,
    LonePairTest.class,
    MappingTest.class,
    MoleculeTest.class,
    MonomerTest.class,
    PolymerTest.class,
    PseudoAtomTest.class,
    ReactionTest.class,
    RingTest.class,
    RingSetTest.class,
    AtomContainerSetTest.class,
    MoleculeSetTest.class,
    ReactionSetTest.class,
    SingleElectronTest.class,
    StrandTest.class,
    ChangeEventPropagationTest.class,

    FragmentAtomTest.class,

    AdductFormulaTest.class,
    MolecularFormulaSetTest.class,
    MolecularFormulaTest.class,
    
    TetrahedralChiralityTest.class,

    // test from test.event
    ChemObjectChangeEventTest.class,
        
    // tests from test.protein.data
    PDBAtomTest.class,
    PDBMonomerTest.class,
    PDBPolymerTest.class,
    PDBStructureTest.class
})
public class MdataTests {}
