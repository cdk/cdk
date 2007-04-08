package org.openscience.cdk.test.smiles.smarts;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.smiles.smarts.SMARTSQueryTool;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * JUnit test routines for the SMARTS substructure search.
 *
 * @author Egon Willighagen
 * @cdk.module test-experimental
 * @cdk.require ant1.6
 */
public class SMARTSQueryToolTest extends CDKTestCase {

    private LoggingTool logger;

    public SMARTSQueryToolTest(String testName) {
        super(testName);
        logger = new LoggingTool(this);
    }

    public static Test suite() {
        return new TestSuite(SMARTSQueryToolTest.class);
    }

    public void testQueryTool() throws CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = sp.parseSmiles("CC(=O)OC(=O)C");
        SMARTSQueryTool querytool = new SMARTSQueryTool("O=CO");

        boolean status = querytool.matches(atomContainer);
        assertTrue(status);

        int nmatch = querytool.countMatches();
        assertEquals(2, nmatch);

        List map1 = new ArrayList();
        map1.add(new Integer(1));
        map1.add(new Integer(2));
        map1.add(new Integer(3));

        List map2 = new ArrayList();
        map2.add(new Integer(3));
        map2.add(new Integer(4));
        map2.add(new Integer(5));

        List mappings = querytool.getMatchingAtoms();
        List ret1 = (List) mappings.get(0);
        Collections.sort(ret1);
        for (int i = 0; i < 3; i++) {
            assertEquals(map1.get(i), ret1.get(i));
        }

        List ret2 = (List) mappings.get(1);
        Collections.sort(ret2);
        for (int i = 0; i < 3; i++) {
            assertEquals(map2.get(i), ret2.get(i));
        }

    }
}
