/* This work is the product of a US Government employee as part of his/her regular duties
 * and is thus in the public domain.
 * 
 * Author: Lyle D. Burgoon, Ph.D. (lyle.d.burgoon@usace.army.mil)
 * Date: 5 FEBRUARY 2018
 * 
 */
package org.openscience.cdk.fingerprint;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import java.util.Map;

/**
 * @cdk.module test-fingerprint
 */
public class AtomPairs2DFingerprintTest extends AbstractFingerprinterTest {

    SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());

    @Test
    public void testFingerprint() throws Exception {
        /*
    	 * We are going to test hexane. Hexane is a good test b/c it has 10 carbons.
    	 * Since the max distance for this fingerprint is 10, the final C-C fingerprint slot
    	 * at distance 10 should return false, while all the other C-C fingerprint slots
    	 * should return true.
    	 */
        IFingerprinter    printer = new AtomPairs2DFingerprinter();
        IAtomContainer    mol1    = parser.parseSmiles("cccccccccc");
        BitSetFingerprint bsfp    = (BitSetFingerprint) printer.getBitFingerprint(mol1);
        Assert.assertEquals(9, bsfp.cardinality());
        Assert.assertEquals(true, bsfp.get(0));        //Distance 1
        Assert.assertEquals(true, bsfp.get(78));    //Distance 2
        Assert.assertEquals(true, bsfp.get(156));    //Distance 3
        Assert.assertEquals(true, bsfp.get(234));    //Distance 4
        Assert.assertEquals(true, bsfp.get(312));    //Distance 5
        Assert.assertEquals(true, bsfp.get(390));    //Distance 6
        Assert.assertEquals(true, bsfp.get(468));    //Distance 7
        Assert.assertEquals(true, bsfp.get(546));    //Distance 8
        Assert.assertEquals(true, bsfp.get(624));    //Distance 9
        Assert.assertEquals(false, bsfp.get(702));    //Distance 10
    }

    @Test
    public void testHalogen() throws Exception {
        IFingerprinter       printer = new AtomPairs2DFingerprinter();
        IAtomContainer       mol1    = parser.parseSmiles("Clc1ccccc1");
        Map<String, Integer> map     = printer.getRawFingerprint(mol1);
        Assert.assertTrue(map.containsKey("1_X_C"));
        Assert.assertTrue(map.containsKey("1_Cl_C"));
        Assert.assertTrue(map.containsKey("2_X_C"));
        Assert.assertTrue(map.containsKey("2_Cl_C"));
        Assert.assertTrue(map.containsKey("3_X_C"));
        Assert.assertTrue(map.containsKey("3_Cl_C"));
        Assert.assertTrue(map.containsKey("4_X_C"));
        Assert.assertTrue(map.containsKey("4_Cl_C"));
    }

    @Test
    public void ignoredAtom() throws Exception {
        IFingerprinter       printer = new AtomPairs2DFingerprinter();
        IAtomContainer       mol1    = parser.parseSmiles("[Te]1cccc1");
        Map<String, Integer> map     = printer.getRawFingerprint(mol1);
        Assert.assertTrue(map.containsKey("1_C_C"));
        Assert.assertTrue(map.containsKey("2_C_C"));
    }

    @Test
    public void testGetCountFingerprint() throws Exception {
        IFingerprinter    printer = new AtomPairs2DFingerprinter();
        IAtomContainer    mol1    = parser.parseSmiles("cccccccccc");
        ICountFingerprint icfp    = printer.getCountFingerprint(mol1);
        Assert.assertEquals(9, icfp.numOfPopulatedbins());

    }

    @Test
    public void testGetRawFingerprint() throws Exception {
        IFingerprinter printer = new AtomPairs2DFingerprinter();
    }
    
    @Test
    public void testNullPointerExceptionInGetBitFingerprint() throws Exception {
        IFingerprinter printer = new AtomPairs2DFingerprinter();
        IAtomContainer chlorobenzene;
        chlorobenzene = parser.parseSmiles("Clc1ccccc1");
        BitSetFingerprint bsfp1 = (BitSetFingerprint) printer.getBitFingerprint(chlorobenzene);
        chlorobenzene = parser.parseSmiles("c1ccccc1Cl");
        BitSetFingerprint bsfp2 = (BitSetFingerprint) printer.getBitFingerprint(chlorobenzene);
    }
}
