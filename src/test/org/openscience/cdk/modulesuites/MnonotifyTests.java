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
import org.openscience.cdk.coverage.NonotifyCoverageTest;
import org.openscience.cdk.nonotify.NNAdductFormulaTest;
import org.openscience.cdk.nonotify.NNAminoAcidTest;
import org.openscience.cdk.nonotify.NNAtomContainerSetTest;
import org.openscience.cdk.nonotify.NNAtomContainerTest;
import org.openscience.cdk.nonotify.NNAtomParityTest;
import org.openscience.cdk.nonotify.NNAtomTest;
import org.openscience.cdk.nonotify.NNAtomTypeTest;
import org.openscience.cdk.nonotify.NNBioPolymerTest;
import org.openscience.cdk.nonotify.NNBondTest;
import org.openscience.cdk.nonotify.NNChemFileTest;
import org.openscience.cdk.nonotify.NNChemModelTest;
import org.openscience.cdk.nonotify.NNChemObjectTest;
import org.openscience.cdk.nonotify.NNChemSequenceTest;
import org.openscience.cdk.nonotify.NNCrystalTest;
import org.openscience.cdk.nonotify.NNElectronContainerTest;
import org.openscience.cdk.nonotify.NNElementTest;
import org.openscience.cdk.nonotify.NNFragmentAtomTest;
import org.openscience.cdk.nonotify.NNIsotopeTest;
import org.openscience.cdk.nonotify.NNLonePairTest;
import org.openscience.cdk.nonotify.NNMappingTest;
import org.openscience.cdk.nonotify.NNMolecularFormulaSetTest;
import org.openscience.cdk.nonotify.NNMolecularFormulaTest;
import org.openscience.cdk.nonotify.NNMoleculeSetTest;
import org.openscience.cdk.nonotify.NNMoleculeTest;
import org.openscience.cdk.nonotify.NNMonomerTest;
import org.openscience.cdk.nonotify.NNPDBAtomTest;
import org.openscience.cdk.nonotify.NNPDBMonomerTest;
import org.openscience.cdk.nonotify.NNPDBPolymerTest;
import org.openscience.cdk.nonotify.NNPDBStructureTest;
import org.openscience.cdk.nonotify.NNPolymerTest;
import org.openscience.cdk.nonotify.NNPseudoAtomTest;
import org.openscience.cdk.nonotify.NNReactionSchemeTest;
import org.openscience.cdk.nonotify.NNReactionSetTest;
import org.openscience.cdk.nonotify.NNReactionTest;
import org.openscience.cdk.nonotify.NNRingSetTest;
import org.openscience.cdk.nonotify.NNRingTest;
import org.openscience.cdk.nonotify.NNSingleElectronTest;
import org.openscience.cdk.nonotify.NNStrandTest;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilderTest;

/**
 * TestSuite that runs the tests from the nonotify module.
 *
 * @cdk.module  test-nonotify
 */
@RunWith(value=Suite.class)
@SuiteClasses(value={
    NonotifyCoverageTest.class,
    NoNotificationChemObjectBuilderTest.class,
    NNAminoAcidTest.class,
    NNAtomContainerTest.class,
    NNAtomParityTest.class,
    NNAtomTest.class,
    NNAtomTypeTest.class,
    NNBioPolymerTest.class,
    NNBondTest.class,
    NNChemObjectTest.class,
    NNChemFileTest.class,
    NNChemModelTest.class,
    NNChemSequenceTest.class,
    NNCrystalTest.class,
    NNElectronContainerTest.class,
    NNElementTest.class,
    NNFragmentAtomTest.class,
    NNIsotopeTest.class,
    NNLonePairTest.class,
    NNMappingTest.class,
    NNMoleculeTest.class,
    NNMonomerTest.class,
    NNPseudoAtomTest.class,
    NNPolymerTest.class,
    NNReactionTest.class,
    NNRingTest.class,
    NNRingSetTest.class,
    NNAtomContainerSetTest.class,
    NNMoleculeSetTest.class,
    NNReactionSetTest.class,
    NNReactionSchemeTest.class,
    NNSingleElectronTest.class,
    NNStrandTest.class,
    NNAdductFormulaTest.class,
    NNMolecularFormulaTest.class,
    NNMolecularFormulaSetTest.class,
        
    // tests from test.protein.data
    NNPDBAtomTest.class,
    NNPDBMonomerTest.class,
    NNPDBPolymerTest.class,
    NNPDBStructureTest.class
})
public class MnonotifyTests {}
