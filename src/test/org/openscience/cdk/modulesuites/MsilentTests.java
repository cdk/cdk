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
import org.openscience.cdk.coverage.SilentCoverageTest;
import org.openscience.cdk.silent.AminoAcidTest;
import org.openscience.cdk.silent.AtomContainerSetTest;
import org.openscience.cdk.silent.AtomContainerTest;
import org.openscience.cdk.silent.AtomParityTest;
import org.openscience.cdk.silent.AtomTest;
import org.openscience.cdk.silent.AtomTypeTest;
import org.openscience.cdk.silent.BioPolymerTest;
import org.openscience.cdk.silent.BondTest;
import org.openscience.cdk.silent.ChemFileTest;
import org.openscience.cdk.silent.ChemModelTest;
import org.openscience.cdk.silent.ChemObjectTest;
import org.openscience.cdk.silent.ChemSequenceTest;
import org.openscience.cdk.silent.CrystalTest;
import org.openscience.cdk.silent.ElectronContainerTest;
import org.openscience.cdk.silent.ElementTest;
import org.openscience.cdk.silent.FragmentAtomTest;
import org.openscience.cdk.silent.IsotopeTest;
import org.openscience.cdk.silent.LonePairTest;
import org.openscience.cdk.silent.MolecularFormulaSetTest;
import org.openscience.cdk.silent.MolecularFormulaTest;
import org.openscience.cdk.silent.MonomerTest;
import org.openscience.cdk.silent.PDBAtomTest;
import org.openscience.cdk.silent.PDBMonomerTest;
import org.openscience.cdk.silent.PDBPolymerTest;
import org.openscience.cdk.silent.PDBStructureTest;
import org.openscience.cdk.silent.PolymerTest;
import org.openscience.cdk.silent.PseudoAtomTest;
import org.openscience.cdk.silent.ReactionSchemeTest;
import org.openscience.cdk.silent.ReactionSetTest;
import org.openscience.cdk.silent.ReactionTest;
import org.openscience.cdk.silent.RingSetTest;
import org.openscience.cdk.silent.RingTest;
import org.openscience.cdk.silent.SilentChemObjectBuilderTest;
import org.openscience.cdk.silent.SingleElectronTest;
import org.openscience.cdk.silent.StrandTest;

/**
 * TestSuite that runs the tests from the silent module.
 *
 * @cdk.module  test-silent
 * @cdk.depends log4j.jar
 * @cdk.depends junit.jar
 */
@RunWith(value=Suite.class)
@SuiteClasses(value={
    SilentCoverageTest.class,
    SilentChemObjectBuilderTest.class,
    AminoAcidTest.class,
    AtomContainerTest.class,
    AtomParityTest.class,
    AtomTest.class,
    AtomTypeTest.class,
    BioPolymerTest.class,
    BondTest.class,
    ChemObjectTest.class,
    ChemFileTest.class,
    ChemModelTest.class,
    ChemSequenceTest.class,
    CrystalTest.class,
    ElectronContainerTest.class,
    ElementTest.class,
    FragmentAtomTest.class,
    IsotopeTest.class,
    LonePairTest.class,
    MonomerTest.class,
    PseudoAtomTest.class,
    PolymerTest.class,
    ReactionTest.class,
    RingTest.class,
    RingSetTest.class,
    AtomContainerSetTest.class,
    ReactionSetTest.class,
    ReactionSchemeTest.class,
    SingleElectronTest.class,
    StrandTest.class,
    MolecularFormulaTest.class,
    MolecularFormulaSetTest.class,
        
    // tests from test.protein.data
    PDBAtomTest.class,
    PDBMonomerTest.class,
    PDBPolymerTest.class,
    PDBStructureTest.class
})
public class MsilentTests {}
