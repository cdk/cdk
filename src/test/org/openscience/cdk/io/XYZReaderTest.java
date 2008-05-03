/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
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
 */
package org.openscience.cdk.io;

import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.io.XYZReader;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;

/**
 * TestCase for the reading XYZ files using a test file.
 *
 * @cdk.module test-io
 *
 * @see org.openscience.cdk.io.XYZReader
 */
public class XYZReaderTest extends CDKTestCase {

    private org.openscience.cdk.tools.LoggingTool logger;

    public XYZReaderTest(String name) {
        super(name);
        logger = new LoggingTool(this);
    }

    public static Test suite() {
        return new TestSuite(XYZReaderTest.class);
    }

    public void testAccepts() {
    	XYZReader reader = new XYZReader();
    	assertTrue(reader.accepts(ChemFile.class));
    }

    public void testViagra() throws Exception {
        String filename = "data/xyz/viagra.xyz";
        logger.info("Testing: ", filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        XYZReader reader = new XYZReader(ins);
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
        assertEquals(63, m.getAtomCount());
        assertEquals(0, m.getBondCount());

        assertEquals("N", m.getAtom(0).getSymbol());
        assertNotNull(m.getAtom(0).getPoint3d());
        assertEquals(-3.4932, m.getAtom(0).getPoint3d().x, 0.0001);
        assertEquals(-1.8950, m.getAtom(0).getPoint3d().y, 0.0001);
        assertEquals(0.1795, m.getAtom(0).getPoint3d().z, 0.0001);
    }

    public void testComment() throws Exception {
        String filename = "data/xyz/viagra_withComment.xyz";
        logger.info("Testing: ", filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        XYZReader reader = new XYZReader(ins);
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
        assertEquals(63, m.getAtomCount());
        assertEquals(0, m.getBondCount());

        // atom 63: H    3.1625    3.1270   -0.9362
        assertEquals("H", m.getAtom(62).getSymbol());
        assertNotNull(m.getAtom(62).getPoint3d());
        assertEquals(3.1625, m.getAtom(62).getPoint3d().x, 0.0001);
        assertEquals(3.1270, m.getAtom(62).getPoint3d().y, 0.0001);
        assertEquals(-0.9362, m.getAtom(62).getPoint3d().z, 0.0001);
    }

}
