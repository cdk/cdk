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
        suite.addTest(ElectronImpactPDBReactionTest.suite());
        suite.addTest(ElectronImpactNBEReactionTest.suite());

        return suite;
    }

}

