/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@slists.sourceforge.net
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

import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.geometry.*;
import java.io.*;
import junit.framework.*;
import com.baysmith.io.FileUtilities;
import java.util.Iterator;

/**
 * TestCase for the reading MDL mol files using one test file.
 *
 * @see org.openscience.cdk.io.MDLReader
 */
public class MDLReaderTest extends TestCase {

    private org.openscience.cdk.tools.LoggingTool logger;

    public MDLReaderTest(String name) {
        super(name);
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
    }

    public static Test suite() {
        return new TestSuite(MDLReaderTest.class);
    }

    public void testBug682233() {
        String filename = "data/mdl/bug682233.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            MDLReader reader = new MDLReader(new InputStreamReader(ins));
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
            
            assertNotNull(chemFile);
            assertEquals(1, chemFile.getChemSequenceCount());
            ChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(1, seq.getChemModelCount());
            ChemModel model = seq.getChemModel(0);
            assertNotNull(model);
            
            SetOfMolecules som = model.getSetOfMolecules();
            assertNotNull(som);
            assertEquals(1, som.getMoleculeCount());
            Molecule m = som.getMolecule(0);
            assertNotNull(m);
            assertEquals(4, m.getAtomCount());
            assertEquals(2, m.getBondCount());
            
            // test reading of formal charges
            Atom a = m.getAtomAt(0);
            assertNotNull(a);
            assertEquals("Na", a.getSymbol());
            assertEquals(1, a.getFormalCharge());
            a = m.getAtomAt(2); 
            assertNotNull(a);
            assertEquals("O", a.getSymbol());
            assertEquals(-1, a.getFormalCharge());
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    public void testAPinene() {
        String filename = "data/mdl/a-pinene.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            MDLReader reader = new MDLReader(new InputStreamReader(ins));
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    public void testFourRing() {
        String filename = "data/mdl/four-ring-5x10.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            MDLReader reader = new MDLReader(new InputStreamReader(ins));
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
        } catch (Exception e) {
            fail(e.toString());
        }
    }


    public void testHydrozyamino() {
        String filename = "data/mdl/hydroxyamino.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            MDLReader reader = new MDLReader(new InputStreamReader(ins));
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
        } catch (Exception e) {
            fail(e.toString());
        }
    }


    public void testMethylBenzol() {
        String filename = "data/mdl/methylbenzol.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            MDLReader reader = new MDLReader(new InputStreamReader(ins));
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
        } catch (Exception e) {
            fail(e.toString());
        }
    }
    

    public void testPolycarpol() {
        String filename = "data/mdl/polycarpol.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            MDLReader reader = new MDLReader(new InputStreamReader(ins));
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
        } catch (Exception e) {
            fail(e.toString());
        }
    }
    
    public void testReserpine() {
        String filename = "data/mdl/reserpine.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            MDLReader reader = new MDLReader(new InputStreamReader(ins));
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
        } catch (Exception e) {
            fail(e.toString());
        }
    }    


    public void testSixRing() {
        String filename = "data/mdl/six-ring-4x4.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            MDLReader reader = new MDLReader(new InputStreamReader(ins));
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
        } catch (Exception e) {
            fail(e.toString());
        }
    }


    public void testSuperspiro() {
        String filename = "data/mdl/superspiro.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            MDLReader reader = new MDLReader(new InputStreamReader(ins));
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    /** 
     * Problem was filed as bug #835571
     */
    public void testReadFromStringReader () {
        String mdl =
                "cyclopropane.mol\n" +
                "\n" +
                "\n" +
                "  9  9  0  0  0                 1 V2000\n" +
                "   -0.0073   -0.5272    0.9655 C   0  0  0  0  0\n" +
                "   -0.6776   -0.7930   -0.3498 C   0  0  0  0  0\n" +
                "    0.2103    0.4053   -0.1891 C   0  0  0  0  0\n" +
                "    0.8019   -1.1711    1.2970 H   0  0  0  0  0\n" +
                "   -0.6000   -0.2021    1.8155 H   0  0  0  0  0\n" +
                "   -1.7511   -0.6586   -0.4435 H   0  0  0  0  0\n" +
                "   -0.3492   -1.6277   -0.9620 H   0  0  0  0  0\n" +
                "    1.1755    0.4303   -0.6860 H   0  0  0  0  0\n" +
                "   -0.2264    1.3994   -0.1675 H   0  0  0  0  0\n" +
                "  1  2  1  6  0  0\n" +
                "  1  3  1  6  0  0\n" +
                "  1  4  1  0  0  0\n" +
                "  1  5  1  1  0  0\n" +
                "  2  3  1  0  0  0\n" +
                "  2  6  1  0  0  0\n" +
                "  2  7  1  6  0  0\n" +
                "  3  8  1  6  0  0\n" +
                "  3  9  1  0  0  0\n" +
                "M  END\n" +
                "$$$$\n";
        try {
            ChemFile cf = (ChemFile) new MDLReader(new StringReader(mdl)).read(new ChemFile());
            assertNotNull(cf);
        } catch (Exception exception) {
            fail();
        }
    }

}
