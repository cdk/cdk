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
import org.openscience.cdk.tools.LoggingTool;

/**
 * TestCase for the reading CML files using a few test files
 * in data/cmltest as found in the original Jumbo3 release
 * (http://www.xml-cml.org/).
 *
 * @cdk.module test
 */
public class JumboTest extends CDKTestCase {

    private LoggingTool logger;

    public JumboTest(String name) {
        super(name);
        logger = new LoggingTool(this);
    }

    public static Test suite() {
        return new TestSuite(JumboTest.class);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(new TestSuite(JumboTest.class));
    }

    /**
     * Now come the actual tests...
     */

    /**
     * Special CML characteristics:
     * - <atomArray><atom/><atom/></atomArray>
     * - X2D only
     */
    public void testCuran() {
        String filename = "data/cml/curan.xml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            CMLReader reader = new CMLReader(new InputStreamReader(ins));
            IChemFile chemFile = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());

            // test the resulting ChemFile content
            assertNotNull(chemFile);
            assertEquals(chemFile.getChemSequenceCount(), 1);
            org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(seq.getChemModelCount(), 1);
            org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
            assertNotNull(model);
            assertEquals(model.getSetOfMolecules().getMoleculeCount(), 1);

            // test the molecule
            org.openscience.cdk.interfaces.IMolecule mol = model.getSetOfMolecules().getMolecule(0);
            assertNotNull(mol);
            assertEquals(mol.getAtomCount(), 24);
            assertEquals(mol.getBondCount(), 28);
            assertTrue(!GeometryTools.has3DCoordinates(mol));
            assertTrue(GeometryTools.has2DCoordinates(mol));
        } catch (Exception e) {
            fail(e.toString());
            e.printStackTrace();
        }
    }

    /**
     * Special CML characteristics:
     * - use of cml: namespace
     * - X2D only
     */
    public void testCephNS() {
        String filename = "data/cml/ceph-ns.xml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            CMLReader reader = new CMLReader(new InputStreamReader(ins));
            IChemFile chemFile = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());

            // test the resulting ChemFile content
            assertNotNull(chemFile);
            assertEquals(chemFile.getChemSequenceCount(), 1);
            org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(seq.getChemModelCount(), 1);
            org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
            assertNotNull(model);
            assertEquals(model.getSetOfMolecules().getMoleculeCount(), 1);

            // test the molecule
            org.openscience.cdk.interfaces.IMolecule mol = model.getSetOfMolecules().getMolecule(0);
            assertNotNull(mol);
            assertEquals(mol.getAtomCount(), 15);
            assertEquals(mol.getBondCount(), 16);
            assertTrue(!GeometryTools.has3DCoordinates(mol));
            assertTrue(GeometryTools.has2DCoordinates(mol));
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    /**
     * Special CML characteristics:
     * - <atomArray><stringArray builtin="atomId"/></atomArray>
     * - <bondArray><stringArray builtin="atomRef"/></atomArray>
     * - no coords
     */
    public void testNucleustest() {
        String filename = "data/cml/nucleustest.xml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            CMLReader reader = new CMLReader(new InputStreamReader(ins));
            IChemFile chemFile = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());

            // test the resulting ChemFile content
            assertNotNull(chemFile);
            assertEquals(chemFile.getChemSequenceCount(), 1);
            org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(seq.getChemModelCount(), 1);
            org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
            assertNotNull(model);
            assertEquals(model.getSetOfMolecules().getMoleculeCount(), 1);

            // test the molecule
            org.openscience.cdk.interfaces.IMolecule mol = model.getSetOfMolecules().getMolecule(0);
            assertNotNull(mol);
            assertEquals("Incorrect number of atoms", 11, mol.getAtomCount());
            assertEquals("Incorrect number of bonds", 12, mol.getBondCount());
            assertTrue("File does not have 3D coordinates", !GeometryTools.has3DCoordinates(mol));
            assertTrue("File does not have 2D coordinates", !GeometryTools.has2DCoordinates(mol));
        } catch (Exception e) {
            fail(e.toString());
        }
    }

}
