package org.openscience.cdk.test.similarity;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.similarity.DistanceMoment;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @cdk.module test-extra
 */
public class DistanceMomentTest extends CDKTestCase {

    boolean standAlone = false;
    //private static LoggingTool logger = new LoggingTool(TanimotoTest.class);

    public DistanceMomentTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(DistanceMomentTest.class);
    }

    private IAtomContainer loadMolecule(String path) throws CDKException {
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(path);
        MDLReader reader = new MDLReader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        List containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        return (IAtomContainer) containersList.get(0);
    }

    public void test3DSim1() throws IOException, CDKException {
        String filename = "data/mdl/sim3d1.sdf";
        IAtomContainer ac = loadMolecule(filename);
        float sim = DistanceMoment.calculate(ac, ac);
        assertEquals(1.0000, sim, 0.00001);
    }

    public void test3DSim2() throws IOException, CDKException {
        IAtomContainer ac1 = loadMolecule("data/mdl/sim3d1.sdf");
        IAtomContainer ac2 = loadMolecule("data/mdl/sim3d2.sdf");
        float sim = DistanceMoment.calculate(ac1, ac2);
        assertEquals(0.24962, sim, 0.00001);
    }


}
