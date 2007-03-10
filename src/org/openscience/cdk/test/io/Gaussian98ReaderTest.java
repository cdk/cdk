package org.openscience.cdk.test.io;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

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

    public void testNMRReading() throws Exception
	{
		IAtomContainer atomContainer = null;
		//boolean foundOneShieldingEntry = false;
		//Double shielding = null;
		Object object = null;
		int shieldingCounter = 0;
		String filename = "data/gaussian/g98ReaderNMRTest.log";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		BufferedReader inputReader = new BufferedReader(new InputStreamReader(ins));
		Gaussian98Reader g98Reader = new Gaussian98Reader(inputReader);
		ChemFile chemFile = (ChemFile)g98Reader.read(new ChemFile());
		List atomContainersList = ChemFileManipulator.getAllAtomContainers(chemFile);
		assertNotNull(atomContainersList);
		assertTrue(atomContainersList.size() == 54);
		//logger.debug("Found " + atomContainers.length + " atomContainers");
		Iterator iterator = atomContainersList.iterator();
		int counter = 0;
		while(iterator.hasNext())
		{
			IAtomContainer ac = (IAtomContainer) iterator.next();
			shieldingCounter = 0;
			atomContainer = ac;
			for (int g = 0; g <  atomContainer.getAtomCount(); g++)
			{
				object = atomContainer.getAtom(g).getProperty(CDKConstants.ISOTROPIC_SHIELDING);
				if (object != null)
				{
					//shielding = (Double)object;
					shieldingCounter ++;
				}
			}
			if (counter < 53) assertTrue(shieldingCounter == 0);
			else assertTrue(shieldingCounter == ac.getAtomCount());
			//logger.debug("AtomContainer " + (f + 1) + " has " + atomContainers[f].getAtomCount() + " atoms and " + shieldingCounter + " shielding entries");
			counter++;
		}		
	}
	
}