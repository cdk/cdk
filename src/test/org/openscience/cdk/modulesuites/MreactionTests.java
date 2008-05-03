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

import org.openscience.cdk.ReactionCoverageTest;
import org.openscience.cdk.atomtype.ResonanceStructuresTest;
import org.openscience.cdk.reaction.type.AdductionProtonLPReactionTest;
import org.openscience.cdk.reaction.type.AdductionProtonPBReactionTest;
import org.openscience.cdk.reaction.type.AdductionSodiumLPReactionTest;
import org.openscience.cdk.reaction.type.CarbonylEliminationReactionTest;
import org.openscience.cdk.reaction.type.ElectronImpactNBEReactionTest;
import org.openscience.cdk.reaction.type.ElectronImpactPDBReactionTest;
import org.openscience.cdk.reaction.type.HeterolyticCleavagePBReactionTest;
import org.openscience.cdk.reaction.type.HeterolyticCleavageSBReactionTest;
import org.openscience.cdk.reaction.type.HomolyticCleavageReactionTest;
import org.openscience.cdk.reaction.type.HyperconjugationReactionTest;
import org.openscience.cdk.reaction.type.PiBondingMovemetReactionTest;
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
import org.openscience.cdk.tools.StructureResonanceGeneratorTest;

/**
 * TestSuite that runs all the tests for the CDK reaction module.
 *
 * @cdk.module  test-reaction
 * @cdk.depends log4j.jar
 * @cdk.depends junit.jar
 */
public class MreactionTests {
    
    public static Test suite() {
        TestSuite suite= new TestSuite("CDK standard Tests");

        suite.addTest(new JUnit4TestAdapter(ReactionCoverageTest.class));	
        
        // Individual Tests
//        suite.addTest(new JUnit4TestAdapter(ReactionEngine.class));

        // from cdk.test.reaction.mechanism
        
        // from cdk.test.reaction.type
        suite.addTest(new JUnit4TestAdapter(ResonanceStructuresTest.class));
        
        suite.addTest(new JUnit4TestAdapter(AdductionProtonLPReactionTest.class));
        suite.addTest(new JUnit4TestAdapter(AdductionProtonPBReactionTest.class));
        suite.addTest(new JUnit4TestAdapter(AdductionSodiumLPReactionTest.class));
        suite.addTest(new JUnit4TestAdapter(CarbonylEliminationReactionTest.class));
        suite.addTest(new JUnit4TestAdapter(ElectronImpactPDBReactionTest.class));
        suite.addTest(new JUnit4TestAdapter(ElectronImpactNBEReactionTest.class));
        suite.addTest(new JUnit4TestAdapter(HeterolyticCleavagePBReactionTest.class));
        suite.addTest(new JUnit4TestAdapter(HeterolyticCleavageSBReactionTest.class));
        suite.addTest(new JUnit4TestAdapter(HomolyticCleavageReactionTest.class));
        suite.addTest(new JUnit4TestAdapter(HyperconjugationReactionTest.class));
        suite.addTest(new JUnit4TestAdapter(PiBondingMovemetReactionTest.class));
        suite.addTest(new JUnit4TestAdapter(RadicalChargeSiteInitiationHReactionTest.class));
        suite.addTest(new JUnit4TestAdapter(RadicalChargeSiteInitiationReactionTest.class));
        suite.addTest(new JUnit4TestAdapter(RadicalSiteHrAlphaReactionTest.class));
        suite.addTest(new JUnit4TestAdapter(RadicalSiteHrBetaReactionTest.class));
        suite.addTest(new JUnit4TestAdapter(RadicalSiteHrDeltaReactionTest.class));
        suite.addTest(new JUnit4TestAdapter(RadicalSiteHrGammaReactionTest.class));
        suite.addTest(new JUnit4TestAdapter(RadicalSiteInitiationHReactionTest.class));
        suite.addTest(new JUnit4TestAdapter(RadicalSiteInitiationReactionTest.class));
        suite.addTest(new JUnit4TestAdapter(RadicalSiteRrAlphaReactionTest.class));
        suite.addTest(new JUnit4TestAdapter(RadicalSiteRrBetaReactionTest.class));
        suite.addTest(new JUnit4TestAdapter(RadicalSiteRrDeltaReactionTest.class));
        suite.addTest(new JUnit4TestAdapter(RadicalSiteRrGammaReactionTest.class));
        suite.addTest(new JUnit4TestAdapter(RearrangementAnionReactionTest.class));
        suite.addTest(new JUnit4TestAdapter(RearrangementCationReactionTest.class));
        suite.addTest(new JUnit4TestAdapter(RearrangementLonePairReactionTest.class));
        suite.addTest(new JUnit4TestAdapter(RearrangementRadicalReactionTest.class));
        suite.addTest(new JUnit4TestAdapter(SharingAnionReactionTest.class));
        suite.addTest(new JUnit4TestAdapter(SharingChargeDBReactionTest.class));
        suite.addTest(new JUnit4TestAdapter(SharingChargeSBReactionTest.class));
        suite.addTest(new JUnit4TestAdapter(SharingLonePairReactionTest.class));
        suite.addTest(new JUnit4TestAdapter(TautomerizationReactionTest.class));
        
        // tools test
        suite.addTest(new JUnit4TestAdapter(StructureResonanceGeneratorTest.class));
        
        return suite;
    }
    
}
