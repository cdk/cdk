package org.openscience.cdk.smiles.smarts.parser;

import java.io.StringReader;

import org.openscience.cdk.test.CDKTestCase;

/**
 * Junit testing routine for SmartsDumpVisitor
 *
 * @author Dazhi Jiao
 * @cdk.created 2007-05-10
 * @cdk.module test-smarts
 * @cdk.keyword SMARTS
 */
class SmartsDumpVisitorTest extends CDKTestCase {

    void dump(String smarts) throws Exception {
        SMARTSParser parser = new SMARTSParser(new StringReader(smarts));
        ASTStart start = parser.Start();
        SmartsDumpVisitor visitor = new SmartsDumpVisitor();
        visitor.visit(start, null);
    }

    void testRing() throws Exception {
        dump("(C=1CCC1).(CCC).(C1CC1CCC=12CCCC2)");
    }
}
