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
 *  */
package org.openscience.cdk.io;

import java.io.InputStream;
import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLV3000Reader;
import org.openscience.cdk.nonotify.NNMolecule;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;

/**
 * TestCase for the reading MDL V3000 mol files using one test file.
 *
 * @cdk.module test-io
 *
 * @see org.openscience.cdk.io.MDLReader
 * @see org.openscience.cdk.io.SDFReaderTest
 */
public class MDLV3000ReaderTest extends CDKTestCase {

    private LoggingTool logger;

    public MDLV3000ReaderTest(String name) {
        super(name);
        logger = new LoggingTool(this);
    }

    public static Test suite() {
        return new TestSuite(MDLV3000ReaderTest.class);
    }

    public void testAccepts() {
    	MDLV3000Reader reader = new MDLV3000Reader();
    	assertTrue(reader.accepts(Molecule.class));
    }
    
    /**
     * @cdk.bug 1571207
     */
    public void testBug1571207() throws Exception {
        String filename = "data/mdl/molV3000.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV3000Reader reader = new MDLV3000Reader(ins);
        IMolecule m = (IMolecule)reader.read(new NNMolecule());
        assertNotNull(m);
        assertEquals(31, m.getAtomCount());
        assertEquals(34, m.getBondCount());

        IAtom atom = m.getAtom(0);
        assertNotNull(atom);
        assertNotNull(atom.getPoint2d());
        assertEquals(10.4341, atom.getPoint2d().x, 0.0001);
        assertEquals(5.1053, atom.getPoint2d().y, 0.0001);
    }
    
    public void testEmptyString() throws Exception {
    	String emptyString = "";
    	MDLV3000Reader reader = new MDLV3000Reader(new StringReader(emptyString));
    	try {
    		reader.read(new NNMolecule());
    		fail("Should have received a CDK Exception");
    	} catch (CDKException cdkEx) {
    		assertEquals("Expected a header line, but found nothing.", cdkEx.getMessage());
    	}
    }

}
