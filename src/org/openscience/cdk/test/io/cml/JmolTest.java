/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.io.cml;

import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.geometry.*;
import java.io.*;
import junit.framework.*;
import com.baysmith.io.FileUtilities;
import java.util.Iterator;

/**
 * TestCase for the reading CML files using a few test files
 * in data/cmltest as found in the Jmol distribution
 * (http://jmol.sf.org/).
 */
public class JmolTest extends TestCase {

    private org.openscience.cdk.tools.LoggingTool logger;

    public JmolTest(String name) {
        super(name);
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
    }

    public static Test suite() {
        return new TestSuite(JmolTest.class);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(new TestSuite(JmolTest.class));
    }

    /**
     * Now come the actual tests...
     */


    /**
     * Special CML characteristics:
     * - <crystal>
     */
    public void testEstron() {
        String filename = "data/cmltest/estron.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            CMLReader reader = new CMLReader(new InputStreamReader(ins));
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());

            // test the resulting ChemFile content
            assertNotNull(chemFile);
            assertEquals(1, chemFile.getChemSequenceCount());
            ChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(1, seq.getChemModelCount());
            ChemModel model = seq.getChemModel(0);
            assertNotNull(model);

            // test the molecule
            Crystal crystal = model.getCrystal();
            assertNotNull(crystal);
            assertEquals(4*42, crystal.getAtomCount());
            assertTrue(GeometryTools.has3DCoordinates(crystal));
            // test the cell axes
            double[] a = crystal.getA();
            assertTrue(a[0] != 0.0);
            double[] b = crystal.getB();
            assertTrue(b[1] != 0.0);
            double[] c = crystal.getC();
            assertTrue(c[2] != 0.0);
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    /**
     * Special CML characteristics:
     * - Jmol Animation
     */
    public void testAnimation() {
        String filename = "data/cmltest/SN1_reaction.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            CMLReader reader = new CMLReader(new InputStreamReader(ins));
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());

            // test the resulting ChemFile content
            assertNotNull(chemFile);
            assertEquals(1, chemFile.getChemSequenceCount());
            ChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(34, seq.getChemModelCount());
            ChemModel model = seq.getChemModel(0);
            assertNotNull(model);
            SetOfMolecules som = model.getSetOfMolecules();
            assertNotNull(som);
            assertEquals(1, som.getMoleculeCount());

            // test the molecule
            Molecule mol = som.getMolecule(0);
            assertNotNull(mol);
            assertEquals(mol.getAtomCount(), 25);
            assertTrue(GeometryTools.has3DCoordinates(mol));
        } catch (Exception e) {
            fail(e.toString());
        }
    }


    /**
     * No special CML code, just regression test for Jmol releases
     */
    public void testMethanolTwo() {
        String filename = "data/cmltest/methanol2.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            CMLReader reader = new CMLReader(new InputStreamReader(ins));
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());

            // test the resulting ChemFile content
            assertNotNull(chemFile);
            assertEquals(1, chemFile.getChemSequenceCount());
            //System.out.println("NO sequences: " + chemFile.getChemSequenceCount());
            ChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(1, seq.getChemModelCount());
            //System.out.println("NO models: " + seq.getChemModelCount());
            ChemModel model = seq.getChemModel(0);
            assertNotNull(model);
            assertEquals(1, model.getSetOfMolecules().getMoleculeCount());

            // test the molecule
            Molecule mol = model.getSetOfMolecules().getMolecule(0);
            assertNotNull(mol);
            assertEquals(mol.getAtomCount(), 6);
            assertTrue(GeometryTools.has3DCoordinates(mol));
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    /**
     * No special CML code, just regression test for Jmol releases
     */
    public void testMethanolOne() {
        String filename = "data/cmltest/methanol1.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            CMLReader reader = new CMLReader(new InputStreamReader(ins));
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());

            // test the resulting ChemFile content
            assertNotNull(chemFile);
            assertEquals(1, chemFile.getChemSequenceCount());
            //System.out.println("NO sequences: " + chemFile.getChemSequenceCount());
            ChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(1, seq.getChemModelCount());
            //System.out.println("NO models: " + seq.getChemModelCount());
            ChemModel model = seq.getChemModel(0);
            assertNotNull(model);
            SetOfMolecules som = model.getSetOfMolecules();
            assertEquals(1, som.getMoleculeCount());

            // test the molecule
            Molecule mol = som.getMolecule(0);
            assertNotNull(mol);
            assertEquals(mol.getAtomCount(), 6);
            assertTrue(GeometryTools.has3DCoordinates(mol));
        } catch (Exception e) {
            fail(e.toString());
        }
    }

}
