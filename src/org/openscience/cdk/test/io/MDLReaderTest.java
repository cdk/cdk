/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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

    public void testBug642426() {
        String filename = "data/mdl/bug642426.mol";
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
}
