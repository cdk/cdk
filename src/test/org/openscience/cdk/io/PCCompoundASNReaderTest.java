/* $Revision$ $Author$ $Date$
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
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 * @cdk.module test-io
 */
public class PCCompoundASNReaderTest extends ChemObjectIOTest {

    private static LoggingTool logger;

    @BeforeClass public static void setup() throws Exception {
        logger = new LoggingTool(PCCompoundASNReaderTest.class);
        setChemObjectIO(new PCCompoundASNReader());
    }

    @Test public void testAccepts() {
    	PCCompoundASNReader reader = new PCCompoundASNReader();
    	Assert.assertTrue(reader.accepts(ChemFile.class));
    }

    @Test public void testReading() throws Exception {
        String filename = "data/asn/pubchem/cid1.asn";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        PCCompoundASNReader reader = new PCCompoundASNReader(ins);
        IChemFile cFile = (IChemFile)reader.read(new ChemFile());
        List containers = ChemFileManipulator.getAllAtomContainers(cFile);
        Assert.assertEquals(1, containers.size());
        Assert.assertTrue(containers.get(0) instanceof IMolecule);
        IMolecule molecule = (IMolecule)containers.get(0);
        Assert.assertNotNull(molecule);

        // check atom stuff
        Assert.assertEquals(31, molecule.getAtomCount());
        Assert.assertNotNull(molecule.getAtom(3));
        Assert.assertEquals("O", molecule.getAtom(3).getSymbol());
        Assert.assertNotNull(molecule.getAtom(4));
        Assert.assertEquals("N", molecule.getAtom(4).getSymbol());

        // check bond stuff
        Assert.assertEquals(30, molecule.getBondCount());
        Assert.assertNotNull(molecule.getBond(3));
        Assert.assertEquals(molecule.getAtom(2), molecule.getBond(3).getAtom(0));
        Assert.assertEquals(molecule.getAtom(11), molecule.getBond(3).getAtom(1));

        // some extracted props
        Assert.assertEquals("InChI=1/C9H17NO4/c1-7(11)14-8(5-9(12)13)6-10(2,3)4/h8H,5-6H2,1-4H3",
        		molecule.getProperty(CDKConstants.INCHI));
        Assert.assertEquals("CC(=O)OC(CC(=O)[O-])C[N+](C)(C)C",
        		molecule.getProperty(CDKConstants.SMILES));
    }
}
