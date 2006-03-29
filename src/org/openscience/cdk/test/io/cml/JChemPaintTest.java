/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.io.cml;

import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.test.CDKTestCase;

/**
 * TestCase for the reading CML files using a few test files
 * in data/cmltest as found in the JChemPaint distribution
 * (http://jchempaint.sf.org/).
 *
 * @cdk.module test
 */
public class JChemPaintTest extends CDKTestCase {

    private org.openscience.cdk.tools.LoggingTool logger;

    public JChemPaintTest(String name) {
        super(name);
        logger = new org.openscience.cdk.tools.LoggingTool(this);
    }

    public static Test suite() {
        return new TestSuite(JChemPaintTest.class);
    }

    /**
     * This one tests a CML2 file.
     */
    public void testSalt() {
        String filename = "data/cml/COONa.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            CMLReader reader = new CMLReader(new InputStreamReader(ins));
            IChemFile chemFile = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());

            // test the resulting ChemFile content
            assertNotNull(chemFile);
            assertEquals(1, chemFile.getChemSequenceCount());
            //System.out.println("NO sequences: " + chemFile.getChemSequenceCount());
            org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(1, seq.getChemModelCount());
            //System.out.println("NO models: " + seq.getChemModelCount());
            org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
            assertNotNull(model);
            assertEquals(1, model.getSetOfMolecules().getMoleculeCount());

            // test the molecule
            org.openscience.cdk.interfaces.IMolecule mol = model.getSetOfMolecules().getMolecule(0);
            assertNotNull(mol);
            assertEquals(4, mol.getAtomCount());
            assertTrue(GeometryTools.has3DCoordinates(mol));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    /**
     * This one tests reading of output from the WWMM matrix (KEGG collection).
     */
    public void testWWMMOutput() {
        String filename = "data/cml/keggtest.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            CMLReader reader = new CMLReader(new InputStreamReader(ins));
            IChemFile chemFile = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());

            // test the resulting ChemFile content
            assertNotNull(chemFile);
            assertEquals(1, chemFile.getChemSequenceCount());
            org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(1, seq.getChemModelCount());
            org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
            assertNotNull(model);
            org.openscience.cdk.interfaces.ISetOfMolecules moleculeSet = model.getSetOfMolecules();
            assertNotNull(moleculeSet);
            assertEquals(1, moleculeSet.getMoleculeCount());

            // test the molecule
            org.openscience.cdk.interfaces.IMolecule mol = moleculeSet.getMolecule(0);
            assertNotNull(mol);
            assertEquals(2, mol.getAtomCount());
            assertTrue(GeometryTools.has3DCoordinates(mol));
        } catch (Exception exception) {
            exception.printStackTrace();
            fail(exception.toString());
        }
    }
}
