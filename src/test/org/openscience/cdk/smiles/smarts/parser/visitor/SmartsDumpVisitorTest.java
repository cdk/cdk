package org.openscience.cdk.smiles.smarts.parser.visitor;

import java.io.StringReader;

import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.smiles.smarts.parser.ASTStart;
import org.openscience.cdk.smiles.smarts.parser.SMARTSParser;

/**
 * Junit testing routine for SmartsDumpVisitor
 *
 * @author Dazhi Jiao
 * @cdk.created 2007-05-10
 * @cdk.module test-smarts
 * @cdk.keyword SMARTS 
 */
public class SmartsDumpVisitorTest extends CDKTestCase {

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
