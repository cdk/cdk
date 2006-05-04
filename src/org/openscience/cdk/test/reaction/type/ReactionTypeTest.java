package org.openscience.cdk.test.reaction.type;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * TestSuite that runs all reactions tests.
 *
 * @cdk.module test-reaction
 */

public class ReactionTypeTest {

    public static Test suite() {
        TestSuite suite = new TestSuite("All Reaction Type Tests");
        suite.addTest(DisplacementChargeReactionTest.suite());
        suite.addTest(ElectronImpactPDBReactionTest.suite());
        suite.addTest(ElectronImpactNBEReactionTest.suite());
        suite.addTest(RearrangementAnion1ReactionTest.suite());
        suite.addTest(RearrangementAnion2ReactionTest.suite());
        suite.addTest(RearrangementAnion3ReactionTest.suite());
        suite.addTest(RearrangementCation1ReactionTest.suite());
        suite.addTest(RearrangementCation2ReactionTest.suite());
        suite.addTest(RearrangementCation3ReactionTest.suite());
        suite.addTest(RearrangementRadical1ReactionTest.suite());
        suite.addTest(RearrangementRadical2ReactionTest.suite());
        suite.addTest(RearrangementRadical3ReactionTest.suite());
        suite.addTest(ReactionBalancerTest.suite());

        return suite;
    }

}

