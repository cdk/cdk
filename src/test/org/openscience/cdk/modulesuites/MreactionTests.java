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
import org.openscience.cdk.atomtype.ResonanceStructuresTest;
import org.openscience.cdk.coverage.ReactionCoverageTest;
import org.openscience.cdk.graph.invariant.ConjugatedPiSystemsDetectorTest;
import org.openscience.cdk.reaction.ReactionEngineTest;
import org.openscience.cdk.reaction.ReactionSpecificationTest;
import org.openscience.cdk.reaction.mechanism.AdductionLPMechanismTest;
import org.openscience.cdk.reaction.mechanism.AdductionPBMechanismTest;
import org.openscience.cdk.reaction.mechanism.HeterolyticCleavageMechanismTest;
import org.openscience.cdk.reaction.mechanism.HomolyticCleavageMechanismTest;
import org.openscience.cdk.reaction.mechanism.RadicalSiteIonizationMechanismTest;
import org.openscience.cdk.reaction.mechanism.RadicalSiteRearrangementMechanismTest;
import org.openscience.cdk.reaction.mechanism.RemovingSEofBMechanismTest;
import org.openscience.cdk.reaction.mechanism.RemovingSEofNBMechanismTest;
import org.openscience.cdk.reaction.mechanism.SharingElectronMechanismTest;
import org.openscience.cdk.reaction.mechanism.TautomerizationMechanismTest;
import org.openscience.cdk.reaction.type.AdductionProtonLPReactionTest;
import org.openscience.cdk.reaction.type.AdductionProtonPBReactionTest;
import org.openscience.cdk.reaction.type.AdductionSodiumLPReactionTest;
import org.openscience.cdk.reaction.type.CarbonylEliminationReactionTest;
import org.openscience.cdk.reaction.type.ElectronImpactNBEReactionTest;
import org.openscience.cdk.reaction.type.ElectronImpactPDBReactionTest;
import org.openscience.cdk.reaction.type.ElectronImpactSDBReactionTest;
import org.openscience.cdk.reaction.type.HeterolyticCleavagePBReactionTest;
import org.openscience.cdk.reaction.type.HeterolyticCleavageSBReactionTest;
import org.openscience.cdk.reaction.type.HomolyticCleavageReactionTest;
import org.openscience.cdk.reaction.type.HyperconjugationReactionTest;
import org.openscience.cdk.reaction.type.PiBondingMovementReactionTest;
import org.openscience.cdk.reaction.type.RadicalChargeSiteInitiationHReactionTest;
import org.openscience.cdk.reaction.type.RadicalChargeSiteInitiationReactionTest;
import org.openscience.cdk.reaction.type.RadicalSiteHrAlphaReactionTest;
import org.openscience.cdk.reaction.type.RadicalSiteHrBetaReactionTest;
import org.openscience.cdk.reaction.type.RadicalSiteHrDeltaReactionTest;
import org.openscience.cdk.reaction.type.RadicalSiteHrGammaReactionTest;
import org.openscience.cdk.reaction.type.RadicalSiteInitiationHReactionTest;
import org.openscience.cdk.reaction.type.RadicalSiteInitiationReactionTest;
import org.openscience.cdk.reaction.type.RadicalSiteRrAlphaReactionTest;
import org.openscience.cdk.reaction.type.RadicalSiteRrBetaReactionTest;
import org.openscience.cdk.reaction.type.RadicalSiteRrDeltaReactionTest;
import org.openscience.cdk.reaction.type.RadicalSiteRrGammaReactionTest;
import org.openscience.cdk.reaction.type.RearrangementAnionReactionTest;
import org.openscience.cdk.reaction.type.RearrangementCationReactionTest;
import org.openscience.cdk.reaction.type.RearrangementLonePairReactionTest;
import org.openscience.cdk.reaction.type.RearrangementRadicalReactionTest;
import org.openscience.cdk.reaction.type.SharingAnionReactionTest;
import org.openscience.cdk.reaction.type.SharingChargeDBReactionTest;
import org.openscience.cdk.reaction.type.SharingChargeSBReactionTest;
import org.openscience.cdk.reaction.type.SharingLonePairReactionTest;
import org.openscience.cdk.reaction.type.TautomerizationReactionTest;
import org.openscience.cdk.reaction.type.parameters.ParameterReactTest;
import org.openscience.cdk.reaction.type.parameters.SetReactionCenterTest;
import org.openscience.cdk.tools.StructureResonanceGeneratorTest;

/**
 * TestSuite that runs all the tests for the CDK reaction module.
 *
 * @cdk.module  test-reaction
 * @cdk.depends log4j.jar
 * @cdk.depends junit.jar
 */
@RunWith(value=Suite.class)
@SuiteClasses(value={
    ReactionCoverageTest.class, 
        
    // Individual Tests
    ReactionEngineTest.class,
    ReactionSpecificationTest.class,

    // from cdk.test.reaction.mechanism
    AdductionLPMechanismTest.class,
    AdductionPBMechanismTest.class,
    HeterolyticCleavageMechanismTest.class,
    HomolyticCleavageMechanismTest.class,
    RadicalSiteIonizationMechanismTest.class,
    RadicalSiteRearrangementMechanismTest.class,
    RemovingSEofBMechanismTest.class,
    RemovingSEofNBMechanismTest.class,
    SharingElectronMechanismTest.class,
    TautomerizationMechanismTest.class,
    ConjugatedPiSystemsDetectorTest.class,
              
    // from cdk.test.reaction.type
    AdductionProtonLPReactionTest.class,
    AdductionProtonPBReactionTest.class,
    AdductionSodiumLPReactionTest.class,
    CarbonylEliminationReactionTest.class,
    ElectronImpactPDBReactionTest.class,
    ElectronImpactNBEReactionTest.class,
    ElectronImpactSDBReactionTest.class,
    HeterolyticCleavagePBReactionTest.class,
    HeterolyticCleavageSBReactionTest.class,
    HomolyticCleavageReactionTest.class,
    HyperconjugationReactionTest.class,
    PiBondingMovementReactionTest.class,
    RadicalChargeSiteInitiationHReactionTest.class,
    RadicalChargeSiteInitiationReactionTest.class,
    RadicalSiteHrAlphaReactionTest.class,
    RadicalSiteHrBetaReactionTest.class,
    RadicalSiteHrDeltaReactionTest.class,
    RadicalSiteHrGammaReactionTest.class,
    RadicalSiteInitiationHReactionTest.class,
    RadicalSiteInitiationReactionTest.class,
    RadicalSiteRrAlphaReactionTest.class,
    RadicalSiteRrBetaReactionTest.class,
    RadicalSiteRrDeltaReactionTest.class,
    RadicalSiteRrGammaReactionTest.class,
    RearrangementAnionReactionTest.class,
    RearrangementCationReactionTest.class,
    RearrangementLonePairReactionTest.class,
    RearrangementRadicalReactionTest.class,
    SharingAnionReactionTest.class,
    SharingChargeDBReactionTest.class,
    SharingChargeSBReactionTest.class,
    SharingLonePairReactionTest.class,
    TautomerizationReactionTest.class,

    // parameters test
    ParameterReactTest.class,
    SetReactionCenterTest.class,
        
    // tools test
    StructureResonanceGeneratorTest.class,
    ResonanceStructuresTest.class
})
public class MreactionTests {}
