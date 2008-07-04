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
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.tools.LoggingTool;

/**
 * @cdk.module test-io
 */
public class PCSubstanceXMLReaderTest extends ChemObjectIOTest {

    private static LoggingTool logger;

    @BeforeClass public static void setup() throws Exception {
        logger = new LoggingTool(Mol2ReaderTest.class);
        setChemObjectIO(new Mol2Reader());
    }

    @Test public void testAccepts() throws Exception {
    	PCSubstanceXMLReader reader = new PCSubstanceXMLReader();
    	Assert.assertTrue(reader.accepts(Molecule.class));
    }

    @Test public void testReading() throws Exception {
        String filename = "data/asn/pubchem/sid577309.xml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        PCSubstanceXMLReader reader = new PCSubstanceXMLReader(ins);
        IMolecule molecule = (IMolecule)reader.read(new Molecule());
        Assert.assertNotNull(molecule);

        // check atom stuff
        Assert.assertEquals(19, molecule.getAtomCount());
        Assert.assertTrue(molecule.getAtom(0) instanceof IPseudoAtom);

        // check bond stuff
        Assert.assertEquals(19, molecule.getBondCount());
        Assert.assertNotNull(molecule.getBond(3));
    }
}
