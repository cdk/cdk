package org.openscience.cdk.test.reaction.type;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.applications.swing.MoleculeListViewer;

/**
 * TestSuite that runs all reactions tests.
 *
 * @cdk.module test-extra
 */

public class ReactionTypeTest {

    static MoleculeListViewer moleculeListViewer = null;

    public static Test suite() {
        TestSuite suite = new TestSuite("All Reaction Type Tests");
        suite.addTest(ElectronImpactPDBReactionTest.suite());
        suite.addTest(ElectronImpactNBEReactionTest.suite());

        return suite;
    }

}

