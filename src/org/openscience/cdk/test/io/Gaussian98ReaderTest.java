package org.openscience.cdk.test.io;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.Gaussian98Reader;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 * A Test case for the gaussian 98 (G98Reader) class.
 * 
 * @cdk.module test-io
 *
 * @author Christoph Steinbeck
 */

public class Gaussian98ReaderTest extends CDKTestCase {
		
	static boolean standAlone = false;
	/**
	 * Test suite.
	 * 
	 * <p><b>Performed tests</b>:
	 * <ul>
	 * 	<li>Gaussian98Reader general tests.</li>
	 * </ul>
	 * 
	 * @return A test suite for Gaussian98Reader class.
	 */
	public static Test suite() {
		return new TestSuite(Gaussian98ReaderTest.class);
	}
	
	/**
	 * Constructs a new "Gaussian98ReaderTest" object given the test case's name.
	 * 
	 * @param	name	The test case name.
	 */
	public Gaussian98ReaderTest(String name) {
		super(name);
	}
	
	
    public void testAccepts() {
    	Gaussian98Reader reader = new Gaussian98Reader();
    	assertTrue(reader.accepts(ChemFile.class));
    }

    public void testNMRReading()
	{
		IAtomContainer atomContainer = null;
		boolean foundOneShieldingEntry = false;
		Double shielding = null;
		Object object = null;
		int shieldingCounter = 0;
		try{
			String filename = "data/gaussian/g98ReaderNMRTest.log";
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(ins));
			Gaussian98Reader g98Reader = new Gaussian98Reader(inputReader);
			ChemFile chemFile = (ChemFile)g98Reader.read(new ChemFile());
			IAtomContainer[] atomContainers = ChemFileManipulator.getAllAtomContainers(chemFile);
			assertNotNull(atomContainers);
			assertTrue(atomContainers.length == 54);
			//System.out.println("Found " + atomContainers.length + " atomContainers");
			for (int f = 0; f < atomContainers.length; f++)
			{	
				shieldingCounter = 0;
				atomContainer = atomContainers[f];
				for (int g = 0; g <  atomContainer.getAtomCount(); g++)
				{
					object = atomContainer.getAtomAt(g).getProperty(CDKConstants.ISOTROPIC_SHIELDING);
					if (object != null)
					{
						shielding = (Double)object;
						shieldingCounter ++;
					}
				}
				if (f < 53) assertTrue(shieldingCounter == 0);
				else assertTrue(shieldingCounter == atomContainers[f].getAtomCount());
				//System.out.println("AtomContainer " + (f + 1) + " has " + atomContainers[f].getAtomCount() + " atoms and " + shieldingCounter + " shielding entries");
			}
		}catch(Exception exc)
		{
			exc.printStackTrace();
			fail();	
		}
		
	}
	
	/**
	 *  The main program for this class
	 *
	 *@param  args  The command line arguments
	 */
	public static void main(String[] args)
	{
		Gaussian98ReaderTest g98rt = new Gaussian98ReaderTest("Gaussian98ReaderTest");
		standAlone = true;
		g98rt.testNMRReading();
	}
}