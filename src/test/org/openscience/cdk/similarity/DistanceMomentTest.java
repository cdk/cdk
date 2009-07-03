package org.openscience.cdk.similarity;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.exception.CDKException;
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
        List containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
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
    public void test3DSim2() throws Exception {
        IAtomContainer ac1 = loadMolecule("data/mdl/sim3d1.sdf");
        IAtomContainer ac2 = loadMolecule("data/mdl/sim3d2.sdf");
        float sim = DistanceMoment.calculate(ac1, ac2);
        Assert.assertEquals(0.24962, sim, 0.00001);
    }


}
