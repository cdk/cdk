package org.openscience.cdk.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;

/**
 * A Test case for the "GamessReader" class.
 *
 * <p><b>References</b>:
 * <br>This test class complies the <a href="http://www.hacknot.info/hacknot/action/showEntry?eid=17">
 * JUnit by Contract</a> article published on Hacknot.
 *
 * @cdk.module test-extra
 *
 * @author Nathana&euml;l "M.Le_maudit" Mazuir
 */
//TODO Update TestCase comments with appropriate information.
public class GamessReaderTest extends SimpleChemObjectReaderTest {

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
    private GamessReader   gamessReaderUnderTest;

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
     * @see java.io.FileReader#FileReader(java.lang.String)
     */
    @Before
    public void setUp() throws Exception {
        String filename = "data/gamess/Cl2O.log";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        this.inputReader = new BufferedReader(new InputStreamReader(ins));
        this.gamessReaderUnderTest = new GamessReader(this.inputReader);
        setSimpleChemObjectReader(this.gamessReaderUnderTest, filename);
    }

    /*
     * @see TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        this.inputReader.close();
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
    @Test
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
    @Test
    public void testAccepts() {
        Assert.assertNotNull("The GamessReader object is not constructed", this.gamessReaderUnderTest);
        Assert.assertTrue("GamessReader should accept an IChemFile object.",
                gamessReaderUnderTest.accepts(ChemFile.class));
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
     * @throws	Exception	may be thrown by the "IChemObject" object.
     *
     * @see org.openscience.cdk.ChemObject
     */
    //TODO Update method comments with appropriate information.
    @Test
    public void testRead() throws Exception {
        Assert.assertNotNull("TEST: The GamessReader object is constructed.", this.gamessReaderUnderTest);
        Assert.assertTrue("TEST: read(IChemObject) returns a IChemObject.",
                this.gamessReaderUnderTest.read(new ChemFile()) instanceof ChemObject);
    }

}
