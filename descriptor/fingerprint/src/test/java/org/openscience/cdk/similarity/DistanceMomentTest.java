package org.openscience.cdk.similarity;

import java.io.InputStream;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 * @cdk.module test-fingerprint
 */
public class DistanceMomentTest extends CDKTestCase {

    boolean standAlone = false;

    private IAtomContainer loadMolecule(String path) throws Exception {
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(path);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        reader.close();
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        return (IAtomContainer) containersList.get(0);
    }

    @Test
    public void test3DSim1() throws Exception {
        String filename = "data/mdl/sim3d1.sdf";
        IAtomContainer ac = loadMolecule(filename);
        float sim = DistanceMoment.calculate(ac, ac);
        Assert.assertEquals(1.0000, sim, 0.00001);
    }

    @Test
    public void testGenerateMoments() throws Exception {
        String filename = "data/mdl/sim3d1.sdf";
        IAtomContainer ac = loadMolecule(filename);
        float[] expected = new float[]{3.710034f, 1.780116f, 0.26535583f, 3.7945938f, 2.2801101f, 0.20164771f, 7.1209f,
                9.234152f, -0.49032924f, 6.6067924f, 8.89391f, -0.048539735f};
        float[] actual = DistanceMoment.generateMoments(ac);

        // no assertArrayEquals for junit 4.5
        for (int i = 0; i < expected.length; i++) {
            Assert.assertEquals(expected[i], actual[i], 0.000001);
        }

    }

    @Test
    public void test3DSim2() throws Exception {
        IAtomContainer ac1 = loadMolecule("data/mdl/sim3d1.sdf");
        IAtomContainer ac2 = loadMolecule("data/mdl/sim3d2.sdf");
        float sim = DistanceMoment.calculate(ac1, ac2);
        Assert.assertEquals(0.24962, sim, 0.00001);
    }

}
