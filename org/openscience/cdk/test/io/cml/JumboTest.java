/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2001  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.io.cml;

import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.geometry.*;
import java.io.*;
import junit.framework.*;
import com.baysmith.io.FileUtilities;
import java.util.Iterator;

/**
 * TestCase for the reading CML files.
 *
 */
public class JumboTest extends TestCase {

    public JumboTest(String name) {
        super(name);
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

    public void testCuran() {
        try {
            File f = new File("data/cmltest/curan.xml");
            if (f.canRead()) {
                // read the file
                CMLReader reader = new CMLReader(new FileReader(f));
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
                assertEquals(mol.getAtomCount(), 24);
                assertEquals(mol.getBondCount(), 28);
                assertTrue(!GeometryTools.has3DCoordinates(mol));
                assertTrue(GeometryTools.has2DCoordinates(mol));
            } else {
                System.out.println("The CMLReader was not tested with a CML file.");
                System.out.println("Due to missing file: data/cmltest/curan.xml");
            }
        } catch (Exception e) {
            fail(e.toString());
        }
    }

}
