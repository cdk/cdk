package org.openscience.cdk.fingerprint;

import junit.framework.TestCase;

import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.fingerprint.CircularFingerprinter.FP;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

public class CircularFingerprintSmartsTest extends TestCase {

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(CircularFingerprintSmartsTest.class);
    
    public void testMol1() throws Exception
    {
    	String molSmiles = "CC";
    	String expectedFPSmarts [][]  = {
    			{"C*"},
    			{"CC"}
    	};
    	checkFPSmartsForMolecule(molSmiles, expectedFPSmarts);
    }
    
    public void testMol2() throws Exception
    {
    	String molSmiles = "CCC";
    	String expectedFPSmarts [][]  = {
    			{"C*"},
    			{"C(*)*"},
    			{"CC*", "C(*)C"},
    			{"CCC"},
    	};
    	checkFPSmartsForMolecule(molSmiles, expectedFPSmarts);
    }
    
    public void testMol3() throws Exception
    {
    	String molSmiles = "CCN";
    	String expectedFPSmarts [][]  = {
    			{"C*"},
    			{"C(*)*"},
    			{"N*"},
    			{"CC*", "C(*)C"},
    			{"C(*)N", "NC*"},
    			{"CCN", "NCC", "C(C)N", "C(N)C"},
    	};
    	checkFPSmartsForMolecule(molSmiles, expectedFPSmarts);
    }
    
    public void testMol4() throws Exception
    {
    	String molSmiles = "C1CC1";
    	String expectedFPSmarts [][]  = {
    			
    			{"C(*)*"},
    			{"C1CC1", "C(C1)C1"}
    	};
    	checkFPSmartsForMolecule(molSmiles, expectedFPSmarts);
    }
    
    public void testMol5() throws Exception
    {
    	String molSmiles = "C1CCC1";
    	String expectedFPSmarts [][]  = {
    			
    			{"C(*)*"},
    			{"C(C*)C*", "C(CC*)*", "C(*)CC*" },
    			{"C1CCC1", "C(CC1)C1", "C(C1)CC1"}
    	};
    	checkFPSmartsForMolecule(molSmiles, expectedFPSmarts);
    }
    
    public void testMol6() throws Exception
    {
    	String molSmiles = "CC[C-]";
    	String expectedFPSmarts [][]  = {
    			
    			{"C*"},
    			{"C(*)*"},
    			{"[C-]*"},
    			{"CC*", "C(*)C"},
    			{"[C-]C*", "C(*)[C-]"},
    			{"CC[C-]", "C(C)[C-]", "[C-]CC", "C([C-])C" }
    	};
    	checkFPSmartsForMolecule(molSmiles, expectedFPSmarts);
    }
    
    public void testMol7() throws Exception
    {
    	String molSmiles = "c1ccccc1";
    	String expectedFPSmarts [][]  = {
    			
    			{"c(:a):a"},
    			{"c(:a)cc:a", "c(c:a)c:a", "c(cc:a):a"},
    			{"c(:a)cccc:a", "c(c:a)ccc:a", "c(cc:a)cc:a", "c(ccc:a)c:a", "c(cccc:a):a"},
    			{"c1ccccc1", "c(c1)cccc1", "c(cc1)ccc1", "c(ccc1)cc1", "c(cccc1)c1" }
    	};
    	checkFPSmartsForMolecule(molSmiles, expectedFPSmarts);
    }
    
    
    private void checkFPSmartsForMolecule(String moleculeSmiles, String expectedFPSmarts[][]) throws Exception
    {
    	//expectedFPSmarts[][] is a double array because for each smarts several equivalent variants
    	//of the smarts are given e.g. CCC C(C)C
    	SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
    	IAtomContainer mol = parser.parseSmiles(moleculeSmiles);

    	CircularFingerprinter circ = new CircularFingerprinter();
    	circ.calculate(mol);
    	int numFP =  circ.getFPCount();
    	for (int i = 0; i < numFP; i++)
    	{
    		FP fp = circ.getFP(i);
    		String smarts = circ.getFPSmarts(fp, mol);
    		int res = findSmarts(smarts, expectedFPSmarts);
    		assertEquals("serching fp smarts: " + smarts, true, res >=0);
    	}
    }
    
    private int findSmarts(String smarts, String smartsSet[][])
    {
    	for (int i = 0; i < smartsSet.length; i++)
    	{
    		String s[] = smartsSet[i];
    		for (int k = 0; k < s.length; k++)
    			if (s[k].equals(smarts))
    				return i;
    	}
    	return -1;
    }
    
    


}
