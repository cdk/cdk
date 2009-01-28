/* $Revision: 10838 $ $Author: egonw $ $Date: 2008-05-05 23:03:59 +0200 (Mon, 05 May 2008) $
 *
 * Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
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
 */
package org.openscience.cdk.io;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.tools.LoggingTool;

/**
 * @cdk.module test-io
 */
public class PCCompoundXMLReaderTest extends SimpleChemObjectReaderTest {

    private static LoggingTool logger;

    @BeforeClass public static void setup() throws Exception {
        logger = new LoggingTool(PCCompoundXMLReaderTest.class);
        setSimpleChemObjectReader(new PCCompoundXMLReader(), "data/asn/pubchem/cid1145.xml");
    }

    @Test public void testAccepts() throws Exception {
    	PCCompoundXMLReader reader = new PCCompoundXMLReader();
    	Assert.assertTrue(reader.accepts(Molecule.class));
    }

    @Test public void testReading() throws Exception {
        String filename = "data/asn/pubchem/cid1145.xml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        PCCompoundXMLReader reader = new PCCompoundXMLReader(ins);
        IMolecule molecule = (IMolecule)reader.read(new Molecule());
        Assert.assertNotNull(molecule);

        // check atom stuff
        Assert.assertEquals(14, molecule.getAtomCount());
        Assert.assertEquals("O", molecule.getAtom(0).getSymbol());
        Assert.assertEquals(Integer.valueOf(-1), molecule.getAtom(0).getFormalCharge());
        Assert.assertEquals("N", molecule.getAtom(1).getSymbol());
        Assert.assertEquals(Integer.valueOf(1), molecule.getAtom(1).getFormalCharge());

        // check bond stuff
        Assert.assertEquals(13, molecule.getBondCount());
        Assert.assertNotNull(molecule.getBond(3));
    }
}
