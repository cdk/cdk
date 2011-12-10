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
import org.openscience.cdk.coverage.DatadebugCoverageTest;
import org.openscience.cdk.debug.DebugAdductFormulaTest;
import org.openscience.cdk.debug.DebugAminoAcidTest;
import org.openscience.cdk.debug.DebugAtomContainerSetTest;
import org.openscience.cdk.debug.DebugAtomContainerTest;
import org.openscience.cdk.debug.DebugAtomParityTest;
import org.openscience.cdk.debug.DebugAtomTest;
import org.openscience.cdk.debug.DebugAtomTypeTest;
import org.openscience.cdk.debug.DebugBioPolymerTest;
import org.openscience.cdk.debug.DebugBondTest;
import org.openscience.cdk.debug.DebugChemFileTest;
import org.openscience.cdk.debug.DebugChemModelTest;
import org.openscience.cdk.debug.DebugChemObjectBuilderTest;
import org.openscience.cdk.debug.DebugChemObjectTest;
import org.openscience.cdk.debug.DebugChemSequenceTest;
import org.openscience.cdk.debug.DebugCrystalTest;
import org.openscience.cdk.debug.DebugElectronContainerTest;
import org.openscience.cdk.debug.DebugElementTest;
import org.openscience.cdk.debug.DebugFragmentAtomTest;
import org.openscience.cdk.debug.DebugIsotopeTest;
import org.openscience.cdk.debug.DebugLonePairTest;
import org.openscience.cdk.debug.DebugMappingTest;
import org.openscience.cdk.debug.DebugMolecularFormulaSetTest;
import org.openscience.cdk.debug.DebugMolecularFormulaTest;
import org.openscience.cdk.debug.DebugMonomerTest;
import org.openscience.cdk.debug.DebugPDBAtomTest;
import org.openscience.cdk.debug.DebugPDBMonomerTest;
import org.openscience.cdk.debug.DebugPDBPolymerTest;
import org.openscience.cdk.debug.DebugPDBStructureTest;
import org.openscience.cdk.debug.DebugPolymerTest;
import org.openscience.cdk.debug.DebugPseudoAtomTest;
import org.openscience.cdk.debug.DebugReactionSchemeTest;
import org.openscience.cdk.debug.DebugReactionSetTest;
import org.openscience.cdk.debug.DebugReactionTest;
import org.openscience.cdk.debug.DebugRingSetTest;
import org.openscience.cdk.debug.DebugRingTest;
import org.openscience.cdk.debug.DebugSingleElectronTest;
import org.openscience.cdk.debug.DebugStrandTest;

/**
 * TestSuite that runs the datadebug tests.
 *
 * @cdk.module  test-datadebug
 * @cdk.depends log4j.jar
 * @cdk.depends junit.jar
 */
@RunWith(value=Suite.class)
@SuiteClasses(value={
    DatadebugCoverageTest.class,
    DebugChemObjectBuilderTest.class,
    DebugAminoAcidTest.class,
    DebugAtomContainerTest.class,
    DebugAtomParityTest.class,
    DebugAtomTest.class,
    DebugAtomTypeTest.class,
    DebugBioPolymerTest.class,
    DebugBondTest.class,
    DebugChemObjectTest.class,
    DebugChemFileTest.class,
    DebugChemModelTest.class,
    DebugChemSequenceTest.class,
    DebugCrystalTest.class,
    DebugElectronContainerTest.class,
    DebugElementTest.class,
    DebugFragmentAtomTest.class,
    DebugIsotopeTest.class,
    DebugLonePairTest.class,
    DebugMappingTest.class,
    DebugMonomerTest.class,
    DebugPseudoAtomTest.class,
    DebugPolymerTest.class,
    DebugReactionTest.class,
    DebugReactionSchemeTest.class,
    DebugRingTest.class,
    DebugRingSetTest.class,
    DebugAtomContainerSetTest.class,
    DebugReactionSetTest.class,
    DebugSingleElectronTest.class,
    DebugStrandTest.class,
    DebugAdductFormulaTest.class,
    DebugMolecularFormulaTest.class,
    DebugMolecularFormulaSetTest.class,
        
    // tests from test.protein.data
    DebugPDBAtomTest.class,
    DebugPDBMonomerTest.class,
    DebugPDBPolymerTest.class,
    DebugPDBStructureTest.class
})
public class MdatadebugTests {}
