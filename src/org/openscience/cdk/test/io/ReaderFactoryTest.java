/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2004  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  */
package org.openscience.cdk.test.io;

import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.ChemObjectReader;
import org.openscience.cdk.io.ReaderFactory;

/**
 * TestCase for the reading CML files using a few test files
 * in data/cmltest as found in the Jmol distribution
 * (http://jmol.sf.org/).
 *
 * @cdk.module test
 */
public class ReaderFactoryTest extends TestCase {

    private ReaderFactory factory;
    
    public ReaderFactoryTest(String name) {
        super(name);
        factory = new ReaderFactory();
    }

    public static Test suite() {
        return new TestSuite(ReaderFactoryTest.class);
    }

    public void testGaussian94() {
        expectFormat("data/gaussian/4-cyanophenylnitrene-Benzazirine-TS.g94.out", 
                     "org.openscience.cdk.io.Gaussian94Reader");
    }
    public void testGaussian98() {
        expectReader("data/gaussian/g98.out", 
                     "org.openscience.cdk.io.Gaussian98Reader");
    }
    public void testGaussian92() {
        expectFormat("data/gaussian/phenylnitrene.g92.out", 
                     "org.openscience.cdk.io.Gaussian92Reader");
    }

    public void testGhemical() {
        expectReader("data/ethene.mm1gp", "org.openscience.cdk.io.GhemicalMMReader");
    }

    public void testJaguar() {
        expectFormat("data/ch4-opt.out", "org.openscience.cdk.io.JaguarReader");
    }

    public void testIChI() {
        expectReader("data/ichi/random.ichi", 
                     "org.openscience.cdk.io.IChIReader");
    }

    public void testINChI() {
        expectReader("data/ichi/guanine.inchi.xml", 
                     "org.openscience.cdk.io.INChIReader");
    }

    public void testINChIPlainText() {
        expectReader("data/ichi/guanine.inchi", 
                     "org.openscience.cdk.io.INChIPlainTextReader");
    }

    public void testVASP() {
        expectReader("data/LiMoS2_optimisation_ISIF3.vasp", 
                     "org.openscience.cdk.io.VASPReader");
    }

    public void testAces2() {
        expectFormat("data/ch3oh_ace.out", "org.openscience.cdk.io.Aces2Reader");
    }

    public void testADF() {
        expectFormat("data/ammonia.adf.out", "org.openscience.cdk.io.ADFReader");
    }

    public void testGamess() {
        expectReader("data/ch3oh_gam.out", "org.openscience.cdk.io.GamessReader");
    }

    public void testABINIT() {
        expectFormat("data/t54.in", "org.openscience.cdk.io.ABINITReader");
    }

    public void testCML() {
        expectReader("data/cmltest/estron.cml", "org.openscience.cdk.io.CMLReader");
    }

    public void testXYZ() {
        expectReader("data/bf3.xyz", "org.openscience.cdk.io.XYZReader");
    }

    public void testShelX() {
        expectReader("data/frame_1.res", "org.openscience.cdk.io.ShelXReader");
    }
    
    public void testMDLMol() {
        expectReader("data/mdl/methylbenzol.mol", "org.openscience.cdk.io.MDLReader");
    }

    public void testPDB() {
        expectReader("data/coffeine.pdb", "org.openscience.cdk.io.PDBReader");
    }
    
    public void testSMILES() {
        expectReader("data/smiles.txt", "org.openscience.cdk.io.SMILESReader");
    }
    
    private void expectFormat(String filename, String expectedFormat) {
        expectFormatAndReader(filename, expectedFormat, false);
    }
    private void expectReader(String filename, String expectedFormat) {
        expectFormatAndReader(filename, expectedFormat, true);
    }
        
    private void expectFormatAndReader(String filename, String expectedFormat, boolean andReader) {
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        if (ins == null) {
            fail("Cannot find file: " + filename);
        }
        try {
            ChemObjectReader reader = factory.createReader(new InputStreamReader(ins));
            if (andReader) {
                assertNotNull(reader);
                String format = reader.getClass().getName();
                if (format.equals(expectedFormat)) {
                    // ok
                } else {
                    fail("Wrong file format detected for " + filename + 
                    ". Expected " + expectedFormat + ", but found: " + format);
                }
                // now try reading something from it
                ChemObject[] objects = { 
                    new ChemFile(), new ChemModel(), new Molecule(),
                    new Reaction()
                };
                boolean read = false;
                for (int i=0; (i<objects.length && !read); i++) {
                    try {
                        reader.read(objects[i]);
                    } catch (CDKException exception) {
                        // was not able to read info
                    }
                    read = true;
                }
                if (read) {
                    // ok, reseting worked
                } else {
                    fail("Reading an ChemObject from the Reader did not work properly.");
                }
            } // else ok format is detected, but no reader is available
        } catch (Exception exception) {
            exception.printStackTrace();
            fail(exception.toString());
        }
    }
}
