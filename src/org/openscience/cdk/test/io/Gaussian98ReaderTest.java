package org.openscience.cdk.test.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.Gaussian98Reader;

/**
 * A Test case for the gaussian 98 (G98Reader) class.
 * 
 * @cdk.module test
 *
 * @author Christoph Steinbeck
 */

public class Gaussian98ReaderTest extends TestCase {
		
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
	
	
	public void testNMRReading()
	{
		try{
			String filename = "data/gaussian/g98ReaderNMRTest.log";
			BufferedReader inputReader = new BufferedReader(new FileReader(filename));
			Gaussian98Reader g98Reader = new Gaussian98Reader(inputReader);
			g98Reader.read(new ChemFile());
		}catch(Exception exc)
		{
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