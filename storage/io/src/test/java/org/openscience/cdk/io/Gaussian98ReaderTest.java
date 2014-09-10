package org.openscience.cdk.io;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

/**
 * A Test case for the gaussian 98 (G98Reader) class.
 *
 * @cdk.module test-io
 *
 * @author Christoph Steinbeck
 */
public class Gaussian98ReaderTest extends SimpleChemObjectReaderTest {

    @BeforeClass
    public static void setup() {
        setSimpleChemObjectReader(new Gaussian98Reader(), "data/gaussian/g98ReaderNMRTest.log");
    }

    @Test
    public void testAccepts() {
        Assert.assertTrue(chemObjectIO.accepts(ChemFile.class));
    }

    @Test
    public void testNMRReading() throws Exception {
        IAtomContainer atomContainer = null;
        //boolean foundOneShieldingEntry = false;
        //Double shielding = null;
        Object object = null;
        int shieldingCounter = 0;
        String filename = "data/gaussian/g98ReaderNMRTest.log";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(ins));
        Gaussian98Reader g98Reader = new Gaussian98Reader(inputReader);
        ChemFile chemFile = (ChemFile) g98Reader.read(new ChemFile());
        g98Reader.close();
        List<IAtomContainer> atomContainersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assert.assertNotNull(atomContainersList);
        Assert.assertTrue(atomContainersList.size() == 54);
        //logger.debug("Found " + atomContainers.length + " atomContainers");
        Iterator<IAtomContainer> iterator = atomContainersList.iterator();
        int counter = 0;
        while (iterator.hasNext()) {
            IAtomContainer ac = iterator.next();
            shieldingCounter = 0;
            atomContainer = ac;
            for (int g = 0; g < atomContainer.getAtomCount(); g++) {
                object = atomContainer.getAtom(g).getProperty(CDKConstants.ISOTROPIC_SHIELDING);
                if (object != null) {
                    //shielding = (Double)object;
                    shieldingCounter++;
                }
            }
            if (counter < 53)
                Assert.assertTrue(shieldingCounter == 0);
            else
                Assert.assertTrue(shieldingCounter == ac.getAtomCount());
            //logger.debug("AtomContainer " + (f + 1) + " has " + atomContainers[f].getAtomCount() + " atoms and " + shieldingCounter + " shielding entries");
            counter++;
        }
    }

}
