/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2006  The Chemistry Development Kit (CDK) project
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
import java.io.InputStreamReader;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.io.INChIReader;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;

/**
 * TestCase for the reading INChI files using one test file.
 *
 * @cdk.module test
 *
 * @see org.openscience.cdk.io.INChIReader
 * @cdk.require java1.4+
 */
public class INChIReaderTest extends CDKTestCase {

    private LoggingTool logger;

    public INChIReaderTest(String name) {
        super(name);
        logger = new LoggingTool(this);
    }

    public static Test suite() {
        return new TestSuite(INChIReaderTest.class);
    }

    /**
     * Test a INChI 1.1Beta file containing the two tautomers
     * of guanine.
     */
    public void testGuanine() {
        String filename = "data/inchi/guanine.inchi.xml";
        logger.info("Testing: ", filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            INChIReader reader = new INChIReader(new InputStreamReader(ins));
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
            
            assertNotNull(chemFile);
            assertEquals(1, chemFile.getChemSequenceCount());
            org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(1, seq.getChemModelCount());
            org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
            assertNotNull(model);
            org.openscience.cdk.interfaces.ISetOfMolecules moleculeSet = model.getSetOfMolecules();
            assertNotNull(moleculeSet);
            org.openscience.cdk.interfaces.IMolecule molecule = moleculeSet.getMolecule(0);
            assertNotNull(molecule);
            
            assertEquals(11, molecule.getAtomCount());
            assertEquals(12, molecule.getBondCount());

        } catch (Exception e) {
            logger.error("Exception during test: ", e.getMessage());
            logger.debug(e);
            fail(e.toString());
        }
    }
    
}
