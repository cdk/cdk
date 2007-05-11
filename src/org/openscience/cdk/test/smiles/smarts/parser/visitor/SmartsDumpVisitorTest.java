package org.openscience.cdk.test.smiles.smarts.parser.visitor;

import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.smiles.smarts.parser.ASTStart;
import org.openscience.cdk.smiles.smarts.parser.SMARTSParser;
import org.openscience.cdk.smiles.smarts.parser.visitor.SmartsDumpVisitor;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Junit testing routine for SmartsDumpVisitor
 *
 * @author Dazhi Jiao
 * @cdk.created 2007-05-10
 * @cdk.module test-smarts
 * @cdk.keyword SMARTS 
 */
public class SmartsDumpVisitorTest extends CDKTestCase {
    public SmartsDumpVisitorTest() {}

    public SmartsDumpVisitorTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        return new TestSuite(SmartsDumpVisitorTest.class);
    }
    
    public void dump(String smarts) throws Exception {
    	SMARTSParser parser = new SMARTSParser(new StringReader(smarts));
    	ASTStart start = parser.Start();
    	SmartsDumpVisitor visitor = new SmartsDumpVisitor();
    	visitor.visit(start, null);
    }
    
    public void testRing() throws Exception {
    	dump("(C=1CCC1).(CCC).(C1CC1CCC=12CCCC2)");
    }
}
