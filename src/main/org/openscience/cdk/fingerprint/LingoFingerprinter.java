package org.openscience.cdk.fingerprint;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmilesGenerator;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An implementation of the LINGO fingerprint {@cdk.cite Vidal2005}.
 *
 * @author Rajarshi Guha
 * @cdk.module standard
 * @cdk.keyword fingerprint
 * @cdk.keyword hologram
 */
@TestClass("org.openscience.cdk.fingerprint.LingoFingerprinterTest")
public class LingoFingerprinter implements IFingerprinter {

    int q = 4;
    SmilesGenerator gen = new SmilesGenerator(true);
    Pattern ringClosurePattern = Pattern.compile("[0-9]+");

    /**
     * Initialize the fingerprinter with a defult substring length of 4.
     */
    @TestMethod("testFingerprint")
    public LingoFingerprinter() {
    }

    /**
     * Initialize the fingerprinter.
     *
     * @param q The length of substrings to consider
     */
    @TestMethod("testFingerprint")
    public LingoFingerprinter(int q) {
        this.q = q;
    }

    public BitSet getFingerprint(IAtomContainer iAtomContainer) throws CDKException {
        throw new NotImplementedException();
    }

    @TestMethod("testFingerprint")
    public Map<String, Integer> getRawFingerprint(IAtomContainer atomContainer) throws CDKException {
        String smiles = refactorSmnile(gen.createSMILES(atomContainer));
        System.out.println("smiels = " + smiles);
        Map<String, Integer> map = new HashMap<String,Integer>();
        for (int i = 0; i < smiles.length()-q+1; i++) {
          String subsmi = smiles.substring(i, i+q);
            System.out.println(subsmi);
            if (map.containsKey(subsmi)) map.put(subsmi, map.get(subsmi)+1);
            else map.put(subsmi, 1);
        }
        System.out.println("");
        return map;
    }

    @TestMethod("testGetSize")
    public int getSize() {
        return -1;
    }

    private String refactorSmnile(String smiles) {
        Matcher matcher = ringClosurePattern.matcher(smiles);
        return matcher.replaceAll("0");        
    }

}
