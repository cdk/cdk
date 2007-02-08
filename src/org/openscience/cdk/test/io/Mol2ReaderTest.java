/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *  */
package org.openscience.cdk.test.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.zip.GZIPInputStream;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.io.Mol2Reader;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;

/**
 * TestCase for the reading SYBYL mol2 files using a test file.
 *
 * @cdk.module test-io
 *
 * @see org.openscience.cdk.io.Mol2Reader
 */
public class Mol2ReaderTest extends CDKTestCase {

    private org.openscience.cdk.tools.LoggingTool logger;

    public Mol2ReaderTest(String name) {
        super(name);
        logger = new LoggingTool(this);
    }

    public static Test suite() {
        return new TestSuite(Mol2ReaderTest.class);
    }

    public void testAccepts() {
    	Mol2Reader reader = new Mol2Reader();
    	assertTrue(reader.accepts(ChemFile.class));
    	assertTrue(reader.accepts(ChemModel.class));
    }

    /**
     * Test example from website. See
     * <a href="http://www.tripos.com/custResources/mol2Files/mol2_format3.html">Tripos example</a>.
     */
    public void testExampleFromWebsite() throws Exception {
        String filename = "data/mol2/fromWebsite.mol2";
        logger.info("Testing: ", filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        Mol2Reader reader = new Mol2Reader(ins);
        ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());

        assertNotNull(chemFile);
        assertEquals(1, chemFile.getChemSequenceCount());
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        assertNotNull(seq);
        assertEquals(1, seq.getChemModelCount());
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        assertNotNull(model);

        org.openscience.cdk.interfaces.IMoleculeSet som = model.getMoleculeSet();
        assertNotNull(som);
        assertEquals(1, som.getMoleculeCount());
        org.openscience.cdk.interfaces.IMolecule m = som.getMolecule(0);
        assertNotNull(m);
        assertEquals(12, m.getAtomCount());
        assertEquals(12, m.getBondCount());

        assertEquals("C.ar", m.getAtom(0).getAtomTypeName());
        assertEquals("C", m.getAtom(0).getSymbol());
        assertEquals("H", m.getAtom(6).getAtomTypeName());
        assertEquals("H", m.getAtom(6).getSymbol());
    }

    
    /**
     * Tests the Mol2Reader with about 30% of the NCI molecules.
     * 
     * @throws IOException if an I/O error occurs
     * @throws CDKException if an CDK error occurs
     */
    public void testNCIfeb03_2D() throws Exception {
        String filename = "data/mol2/NCI_feb03_2D.mol2.gz";
        InputStream in = new GZIPInputStream(Mol2ReaderTest.class.getClassLoader().getResourceAsStream(filename));
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder buf = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("@<TRIPOS>MOLECULE") && (buf.length() > 0)) {
                checkMol(buf);
                buf.delete(0, buf.length() - 1);
            }
            buf.append(line).append('\n');
        }
        if (buf.length() > 0) {
            checkMol(buf);
        }
    }
    
    
    private void checkMol(StringBuilder buf) throws CDKException {
        StringReader sr = new StringReader(buf.toString());
        Mol2Reader reader = new Mol2Reader(sr);
        IChemFile mol = (IChemFile)reader.read(NoNotificationChemObjectBuilder.getInstance().newChemFile());
        assertTrue(mol.getChemSequenceCount() > 0);
        assertTrue(mol.getChemSequence(0).getChemModelCount() > 0);
        assertTrue(mol.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainerCount() > 0);
        assertTrue(mol.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0).getAtomCount() > 0);        
    }
}
