/* This work is the product of a US Government employee as part of his/her regular duties
 * and is thus in the public domain.
 * 
 * Author: Lyle D. Burgoon, Ph.D. (lyle.d.burgoon@usace.army.mil)
 * Date: 5 FEBRUARY 2018
 * 
 */
package org.openscience.cdk.fingerprint;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.fingerprint.AbstractFingerprinterTest;

import java.util.Map;

/**
 */
class AtomPairs2DFingerprintTest extends AbstractFingerprinterTest {

    private final SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());

    @Test
    void testFingerprint() throws Exception {
        /*
    	 * We are going to test hexane. Hexane is a good test b/c it has 10 carbons.
    	 * Since the max distance for this fingerprint is 10, the final C-C fingerprint slot
    	 * at distance 10 should return false, while all the other C-C fingerprint slots
    	 * should return true.
    	 */
        IFingerprinter    printer = new AtomPairs2DFingerprinter();
        IAtomContainer    mol1    = parser.parseSmiles("cccccccccc");
        BitSetFingerprint bsfp    = (BitSetFingerprint) printer.getBitFingerprint(mol1);
        Assertions.assertEquals(9, bsfp.cardinality());
        Assertions.assertEquals(true, bsfp.get(0));        //Distance 1
        Assertions.assertEquals(true, bsfp.get(78));    //Distance 2
        Assertions.assertEquals(true, bsfp.get(156));    //Distance 3
        Assertions.assertEquals(true, bsfp.get(234));    //Distance 4
        Assertions.assertEquals(true, bsfp.get(312));    //Distance 5
        Assertions.assertEquals(true, bsfp.get(390));    //Distance 6
        Assertions.assertEquals(true, bsfp.get(468));    //Distance 7
        Assertions.assertEquals(true, bsfp.get(546));    //Distance 8
        Assertions.assertEquals(true, bsfp.get(624));    //Distance 9
        Assertions.assertEquals(false, bsfp.get(702));    //Distance 10
    }

    @Test
    void testHalogen() throws Exception {
        IFingerprinter       printer = new AtomPairs2DFingerprinter();
        IAtomContainer       mol1    = parser.parseSmiles("Clc1ccccc1");
        Map<String, Integer> map     = printer.getRawFingerprint(mol1);
        Assertions.assertTrue(map.containsKey("1_C_X"));
        Assertions.assertTrue(map.containsKey("1_C_Cl"));
        Assertions.assertTrue(map.containsKey("2_C_X"));
        Assertions.assertTrue(map.containsKey("2_C_Cl"));
        Assertions.assertTrue(map.containsKey("3_C_X"));
        Assertions.assertTrue(map.containsKey("3_C_Cl"));
        Assertions.assertTrue(map.containsKey("4_C_X"));
        Assertions.assertTrue(map.containsKey("4_C_Cl"));
    }

    @Test
    void ignoredAtom() throws Exception {
        IFingerprinter       printer = new AtomPairs2DFingerprinter();
        IAtomContainer       mol1    = parser.parseSmiles("[Te]1cccc1");
        Map<String, Integer> map     = printer.getRawFingerprint(mol1);
        Assertions.assertTrue(map.containsKey("1_C_C"));
        Assertions.assertTrue(map.containsKey("2_C_C"));
    }

    @Test
    public void testGetCountFingerprint() throws Exception {
        IFingerprinter    printer = new AtomPairs2DFingerprinter();
        IAtomContainer    mol1    = parser.parseSmiles("cccccccccc");
        ICountFingerprint icfp    = printer.getCountFingerprint(mol1);
        Assertions.assertEquals(9, icfp.numOfPopulatedbins());

    }

    @Test
    public void testGetRawFingerprint() throws Exception {
        IFingerprinter printer = new AtomPairs2DFingerprinter();
    }
    
    @Test
    void testNullPointerExceptionInGetBitFingerprint() throws Exception {
        IFingerprinter printer = new AtomPairs2DFingerprinter();
        IAtomContainer chlorobenzene;
        chlorobenzene = parser.parseSmiles("Clc1ccccc1");
        BitSetFingerprint bsfp1 = (BitSetFingerprint) printer.getBitFingerprint(chlorobenzene);
        chlorobenzene = parser.parseSmiles("c1ccccc1Cl");
        BitSetFingerprint bsfp2 = (BitSetFingerprint) printer.getBitFingerprint(chlorobenzene);
    }

    /* See: https://sourceforge.net/p/cdk/mailman/message/59396625/ */
    @Test
    public void testOnlyRetainPathsOfInterestInCountFp() throws Exception {
        IFingerprinter fingerprinter = new AtomPairs2DFingerprinter();
        IAtomContainer mol = parser.parseSmiles("c1ccccc1O");
        ICountFingerprint fp = fingerprinter.getCountFingerprint(mol);
        Assertions.assertEquals(7, fp.numOfPopulatedbins());
        Assertions.assertEquals(236, fp.getHash(0));
        Assertions.assertEquals(1, fp.getCount(0));
        Assertions.assertEquals(156, fp.getHash(1));
        Assertions.assertEquals(6, fp.getCount(1));
        Assertions.assertEquals(78, fp.getHash(2));
        Assertions.assertEquals(12, fp.getCount(2));
        Assertions.assertEquals(0, fp.getHash(3));
        Assertions.assertEquals(12, fp.getCount(3));
        Assertions.assertEquals(80, fp.getHash(4));
        Assertions.assertEquals(2, fp.getCount(4));
        Assertions.assertEquals(2, fp.getHash(5));
        Assertions.assertEquals(1, fp.getCount(5));
        Assertions.assertEquals(158, fp.getHash(6));
        Assertions.assertEquals(2, fp.getCount(6));
    }
}
