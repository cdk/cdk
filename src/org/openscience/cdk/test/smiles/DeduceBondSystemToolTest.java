package org.openscience.cdk.test.smiles;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.smiles.DeduceBondSystemTool;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;


/**
 *
 * @author         Rajarshi Guha
 * @cdk.created    2006-09-18
 * @cdk.module     test-smiles
 */

public class DeduceBondSystemToolTest extends CDKTestCase {

    public DeduceBondSystemToolTest(String name) {
        super(name);
    }

    /**
     * A unit test suite for JUnit
     *
     * @return The test suite
     */
    public static Test suite() {
        return new TestSuite(DeduceBondSystemToolTest.class);
    }

    public void testPyrrole() {
        String smiles = "c2ccc3n([H])c1ccccc1c3(c2)";        
        SmilesParser smilesParser = new SmilesParser();
        try {
            IMolecule molecule = smilesParser.parseSmiles(smiles);
            DeduceBondSystemTool dbst = new DeduceBondSystemTool();
            //molecule = dbst.fixAromaticBondOrders(molecule);
            assertNotNull(molecule);
            for (int i = 0; i < molecule.getBondCount(); i++) {
                IBond bond = molecule.getBond(i);
                assertTrue(bond.getOrder() == CDKConstants.BONDORDER_AROMATIC);
            }
        } catch (InvalidSmilesException e) {
            e.printStackTrace();
        }
    }
}
