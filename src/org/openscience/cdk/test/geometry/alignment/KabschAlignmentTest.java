package org.openscience.cdk.test.geometry.alignment;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;
import java.io.FileReader;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.ChemObjectReader;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.geometry.alignment.KabschAlignment;

/**
 * This class defines regression tests that should ensure that the source code
 * of the org.openscience.cdk.geometry.alignment.KabschAlignment is not broken.
 *
 * @cdk.module test
 *
 * @author     Rajarshi Guha
 * @cdk.created    2004-12-11
 *
 * @see org.openscience.cdk.geometry.alignment.KabschAlignment;
 */
public class KabschAlignmentTest extends TestCase {

    public void KabschAlignmentTest(String name) {
    }
    /**
     * Defines a set of tests that can be used in automatic regression testing
     * with JUnit.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(KabschAlignmentTest.class);
        return suite;
    }
    
    public void testAlign() throws ClassNotFoundException, CDKException, java.lang.Exception{
        AtomContainer ac;
        String filename = "data/gravindex.hin";
        File input = new File(filename);
        ChemObjectReader reader = new ReaderFactory().createReader(new FileReader(input));
        ChemFile content = (ChemFile)reader.read((ChemObject)new ChemFile());
        AtomContainer[] c = ChemFileManipulator.getAllAtomContainers(content);
        ac = c[0];

        KabschAlignment ka = new KabschAlignment(ac,ac);
        ka.align();
        double rmsd = ka.getRMSD();

        assertTrue(1e-8 > rmsd);
    }
    
}

