/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003  The Chemistry Development Kit (CDK) project
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
 * TestCase for the reading CML 2 files using a few test files
 * in data/cmltest.
 */
public class CML2Test extends TestCase {

    private org.openscience.cdk.tools.LoggingTool logger;

    public CML2Test(String name) {
        super(name);
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
    }

    public static Test suite() {
        return new TestSuite(CML2Test.class);
    }

    public void testCOONa() {
        String filename = "data/cmltest/COONa.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            CMLReader reader = new CMLReader(new InputStreamReader(ins));
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());

            // test the resulting ChemFile content
            assertNotNull(chemFile);
            assertEquals(chemFile.getChemSequenceCount(), 1);
            ChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(seq.getChemModelCount(), 1);
            ChemModel model = seq.getChemModel(0);
            assertNotNull(model);
            assertEquals(model.getSetOfMolecules().getMoleculeCount(), 1);

            // test the molecule
            Molecule mol = model.getSetOfMolecules().getMolecule(0);
            assertNotNull(mol);
            assertEquals(4, mol.getAtomCount());
            assertEquals(2, mol.getBondCount());
            assertTrue(GeometryTools.has3DCoordinates(mol));
            assertTrue(!GeometryTools.has2DCoordinates(mol));
            
            Atom[] atoms = mol.getAtoms();
            for (int i=0; i<atoms.length; i++) {
                Atom atom = atoms[i];
                if (atom.getSymbol().equals("Na")) 
                    assertEquals(+1, atom.getFormalCharge()); 
            }
        } catch (Exception e) {
            fail(e.toString());
            e.printStackTrace();
        }
    }

    public void testNitrate() {
        String filename = "data/cmltest/nitrate.cml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            CMLReader reader = new CMLReader(new InputStreamReader(ins));
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());

            // test the resulting ChemFile content
            assertNotNull(chemFile);
            assertEquals(chemFile.getChemSequenceCount(), 1);
            ChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(seq.getChemModelCount(), 1);
            ChemModel model = seq.getChemModel(0);
            assertNotNull(model);
            assertEquals(model.getSetOfMolecules().getMoleculeCount(), 1);

            // test the molecule
            Molecule mol = model.getSetOfMolecules().getMolecule(0);
            assertNotNull(mol);
            assertEquals(4, mol.getAtomCount());
            assertEquals(3, mol.getBondCount());
            assertTrue(GeometryTools.has3DCoordinates(mol));
            assertTrue(!GeometryTools.has2DCoordinates(mol));
            
            Atom[] atoms = mol.getAtoms();
            for (int i=0; i<atoms.length; i++) {
                Atom atom = atoms[i];
                if (atom.getSymbol().equals("N")) 
                    assertEquals(+1, atom.getFormalCharge()); 
            }
        } catch (Exception e) {
            fail(e.toString());
            e.printStackTrace();
        }
    }

}
