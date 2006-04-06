package org.openscience.cdk.test.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.GamessReader;
import org.openscience.cdk.test.CDKTestCase;

/**
 * A Test case for the "GamessReader" class.
 * 
 * <p><b>References</b>: 
 * <br>This test class complies the <a href="http://www.hacknot.info/hacknot/action/showEntry?eid=17">
 * JUnit by Contract</a> article published on Hacknot. 
 * 
 * @cdk.module test-io
 *
 * @author Nathana&euml;l "M.Le_maudit" Mazuir
 */
//TODO Update TestCase comments with appropriate information.
public class GamessReaderTest extends CDKTestCase {

	/**
	 * The "BufferedReader" object used as input parameter for the "GamessReader" object.
	 * 
	 * @see	GamessReaderTest#gamessReaderUnderTest 
	 */
	//TODO Update field comment with appropriate information.
	private BufferedReader inputReader;

	/**
	 * The "GamessReader" object used to test the "GamessReader" class.
	 */
	//TODO Update field comment with appropriate information.
	private GamessReader gamessReaderUnderTest;
		
	/**
	 * Test suite.
	 * 
	 * <p><b>Performed tests</b>:
	 * <ul>
	 * 	<li>GamessReader general tests.</li>
	 * </ul>
	 * 
	 * @return A test suite for GamessReaderTest class.
	 */
	public static Test suite() {
		return new TestSuite(GamessReaderTest.class);
	}
	
	/**
	 * Constructs a new "GamessReaderTest" object given the test case's name.
	 * 
	 * @param	name	The test case name.
	 */
	public GamessReaderTest(String name) {
		super(name);
	}
	
	/**
	 * Sets up the fixture. 
	 * <p>This method is called before a test is executed and performs the 
	 * following actions:
	 * <ul>
	 * 	<li>Constructs a new FileReader.</li>
	 * 	<li>Constructs a new BufferedReader.</li>
	 * 	<li>Constructs a new GamessReader.</li>
	 * </ul>
	 * 
	 * @throws	Exception	may be thrown by the <code>super.setUp()</code> instruction.
	 * @throws	FileNotFoundException	may be thrown by the "FileReader" object if the string given in parameter does not contain a valid file name.
	 * 
	 * @see junit.framework.TestCase#setUp()
	 * @see java.io.FileReader#FileReader(java.lang.String)
	 */
	protected void setUp() throws Exception, FileNotFoundException {
		super.setUp();

		String filename = "data/gamess/Cl2O.log";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		this.inputReader = new BufferedReader(new InputStreamReader(ins));
		this.gamessReaderUnderTest = new GamessReader(this.inputReader);
	}
	
	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		this.gamessReaderUnderTest.close(); // TODO Answer the question : Is it necessary ?
	}

	/**
	 * Tests the <code>GamessReader()</code> constructor for the "GamessReader" object.
	 * 
	 * <p><b>Contract</b>:
	 * <br><code>Preconditions</code>:
	 * <ul>
	 * 	<li><code>imputreader</code> is not null.</li>
	 * 	<li><code>inputReader</code> is an instance of "Reader".</li>
	 * </ul>
	 * <code>Invariants</code>:
	 * <ul>
	 * 	<li></li>
	 * </ul>
	 * <code>Postconditions</code>:
	 * <ul>
	 * 	<li>The "GamessReader" object is constructed.</li>
	 * 	<li>GamessReader.input is constructed with the parameter given to the constructor.</li>
	 * </ul>
	 * 
	 * <p><b>Implementation</b>:
	 * <br>The second postcondition is not yet implemented.
	 * 
	 * @see org.openscience.cdk.io.GamessReader#GamessReader(Reader)
	 */
	//TODO Update method comments with appropriate information.
	public void testGamessReader() {
		Assert.assertNotNull("TEST: The inputReader is not null.", this.inputReader);
		Assert.assertTrue("TEST: The inputReader is a Reader object.", this.inputReader instanceof Reader);
		Assert.assertNotNull("TEST: The GamessReader object is constructed.", this.gamessReaderUnderTest);
//		Assert.assertEquals("TEST: ", this.gr.input, this.inputReader);
	}

	/**
	 * Tests the <code>accepts</code> method for the "GamessReader" object.
	 * 
	 * <p><b>Contract</b>:
	 * <br><code>Preconditions</code>:
	 * <ul>
	 * 	<li>The "GamessReader" object is constructed.</li>
	 * </ul>
	 * <code>Invariants</code>:
	 * <ul>
	 * 	<li></li>
	 * </ul>
	 * <code>Postconditions</code>:
	 * <ul>
	 * 	<li>The "GamessReader" only accepts "ChemFile" objects as input parameter.</li>
	 * </ul>
	 */
	//TODO Update method comments with appropriate information.
	public void testAccepts() {
		Assert.assertNotNull("TEST: The GamessReader object is constructed", this.gamessReaderUnderTest);

		//The given object is a IChemObject object
		try {
			Assert.assertTrue("TEST: GamessReader only accepts ChemFile object.", this.gamessReaderUnderTest.accepts(new ChemObject()));
			fail("TEST: GamessReader only accepts ChemFile object RULE VIOLATION with IChemObject.");			
		} catch (AssertionFailedError e) {
		}	

		//The given object is a ChemFile object
		Assert.assertTrue("TEST: GamessReader only accepts ChemFile object.", gamessReaderUnderTest.accepts(new ChemFile()));
	}

	/**
	 * Tests the <code>read(IChemObject object)</code> method for the "GamessReader" object.
	 * 
	 * <p><b>Contract</b>:
	 * <br><code>Preconditions</code>:
	 * <ul>
	 * 	<li>The "GamessReader" object is constructed.</li>
	 * </ul>
	 * <code>Invariants</code>:
	 * <ul>
	 * 	<li></li>
	 * </ul>
	 * <code>Postconditions</code>:
	 * <ul>
	 * 	<li>The <code>read(IChemObject object)</code> method must return a "IChemObject" instance.</li>
	 * </ul>
	 * 
	 * @throws	CDKException	may be thrown by the "IChemObject" object.
	 * 
	 * @see org.openscience.cdk.ChemObject
	 */
	//TODO Update method comments with appropriate information.
	public void testRead() throws CDKException {
		Assert.assertNotNull("TEST: The GamessReader object is constructed.", this.gamessReaderUnderTest);
		Assert.assertTrue("TEST: read(IChemObject) returns a IChemObject.", this.gamessReaderUnderTest.read(new ChemFile()) instanceof ChemObject);
	}

	/**
	 * Tests the <code>close()</code> method for the "GamessReader" object.
	 * 
	 * <p><b>Contract</b>:
	 * <br><code>Preconditions</code>:
	 * <ul>
	 * 	<li>inputReader is opened.</li>
	 * </ul>
	 * Invariants:
	 * <ul>
	 * 	<li></li>
	 * </ul>
	 * <code>Postconditions</code>:
	 * <ul>
	 * 	<li>inputReader is closed.</li>
	 * </ul>
	 * 
	 * <p><b>Implementation</b>:
	 * <br>Both precondition and postcondition are not yet implemented. 
	 * 
	 * @see org.openscience.cdk.io.GamessReader
	 * @see org.openscience.cdk.io.GamessReader#close()
	 */
	//TODO Update method comments with appropriate information.
	public void testClose() {
	}
}