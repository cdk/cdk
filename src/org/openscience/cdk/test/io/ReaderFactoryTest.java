/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2006  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *  */
package org.openscience.cdk.test.io;

import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.io.formats.ABINITFormat;
import org.openscience.cdk.io.formats.ADFFormat;
import org.openscience.cdk.io.formats.Aces2Format;
import org.openscience.cdk.io.formats.CMLFormat;
import org.openscience.cdk.io.formats.GamessFormat;
import org.openscience.cdk.io.formats.Gaussian92Format;
import org.openscience.cdk.io.formats.Gaussian94Format;
import org.openscience.cdk.io.formats.Gaussian98Format;
import org.openscience.cdk.io.formats.GhemicalSPMFormat;
import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.io.formats.INChIFormat;
import org.openscience.cdk.io.formats.INChIPlainTextFormat;
import org.openscience.cdk.io.formats.JaguarFormat;
import org.openscience.cdk.io.formats.MDLFormat;
import org.openscience.cdk.io.formats.PDBFormat;
import org.openscience.cdk.io.formats.ShelXFormat;
import org.openscience.cdk.io.formats.VASPFormat;
import org.openscience.cdk.io.formats.XYZFormat;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;

/**
 * TestCase for the reading CML files using a few test files
 * in data/cmltest as found in the Jmol distribution
 * (http://jmol.sf.org/).
 *
 * @cdk.module test-io
 */
public class ReaderFactoryTest extends CDKTestCase {

    private ReaderFactory factory;
    private LoggingTool logger;
    
    public ReaderFactoryTest(String name) {
        super(name);
        logger = new LoggingTool(this);
        factory = new ReaderFactory();
    }

    public static Test suite() {
        return new TestSuite(ReaderFactoryTest.class);
    }

    public void testGaussian94() {
        expectFormat("data/gaussian/4-cyanophenylnitrene-Benzazirine-TS.g94.out", 
                     new Gaussian94Format());
    }
    public void testGaussian98() {
        expectReader("data/gaussian/g98.out", new Gaussian98Format());
    }
    public void testGaussian92() {
        expectFormat("data/gaussian/phenylnitrene.g92.out", new Gaussian92Format());
    }

    public void testGhemical() {
        expectReader("data/ghemical/ethene.mm1gp", new GhemicalSPMFormat());
    }

    public void testJaguar() {
        expectFormat("data/jaguar/ch4-opt.out", new JaguarFormat());
    }

    public void testINChI() {
        expectReader("data/inchi/guanine.inchi.xml", new INChIFormat());
    }

    public void testINChIPlainText() {
        expectReader("data/inchi/guanine.inchi", new INChIPlainTextFormat());
    }

    public void testVASP() {
        expectReader("data/vasp/LiMoS2_optimisation_ISIF3.vasp", new VASPFormat());
    }

    public void testAces2() {
        expectFormat("data/aces2/ch3oh_ace.out", new Aces2Format());
    }

    public void testADF() {
        expectFormat("data/adf/ammonia.adf.out", new ADFFormat());
    }

    public void testGamess() {
        expectReader("data/gamess/ch3oh_gam.out", new GamessFormat());
    }

    public void testABINIT() {
        expectFormat("data/abinit/t54.in", new ABINITFormat());
    }

    public void testCML() {
        expectReader("data/cml/estron.cml", new CMLFormat());
    }

    public void testXYZ() {
        expectReader("data/xyz/bf3.xyz", new XYZFormat());
    }

    public void testShelX() {
        expectReader("data/shelx/frame_1.res", new ShelXFormat());
    }
    
    public void testMDLMol() {
        expectReader("data/mdl/methylbenzol.mol", new MDLFormat());
    }

    public void testPDB() {
        expectReader("data/pdb/coffeine.pdb", new PDBFormat());
    }
    
    private void expectFormat(String filename, IChemFormat expectedFormat) {
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        if (ins == null) {
            fail("Cannot find file: " + filename);
        }
        IChemFormat format = null;
        try {
            format = factory.guessFormat(ins);
        } catch (Exception exception) {
            logger.error("Could not guess format: ", exception.getMessage());
            logger.debug(exception);
            fail(exception.getMessage());
        }
        assertNotNull(format);
        assertEquals(expectedFormat.getFormatName(), format.getFormatName());
    }
    private void expectReader(String filename, IChemFormat expectedFormat) {
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        if (ins == null) {
            fail("Cannot find file: " + filename);
        }
        try {
            IChemFormat format = factory.guessFormat(ins);
            assertNotNull(format);
            assertEquals(expectedFormat.getFormatName(), format.getFormatName());
            // ok, if format ok, try instantiating a reader
            ins = this.getClass().getClassLoader().getResourceAsStream(filename);
            IChemObjectReader reader = factory.createReader(ins);
            assertNotNull(reader);
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
                    logger.error("Could not read information from file: ", exception.getMessage());
                    logger.debug(exception);
                }
                read = true;
            }
            if (read) {
                // ok, reseting worked
            } else {
                fail("Reading an IChemObject from the Reader did not work properly.");
            }
        } catch (junit.framework.AssertionFailedError exception) {
            throw exception;
        } catch (Exception exception) {
            logger.error("Could not guess format or read file: ", exception.getMessage());
            logger.debug(exception);
            fail(exception.getMessage());
        }
    }
}
