/* Copyright (C) 2009  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.io.rdf;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.SimpleChemObjectReaderTest;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @cdk.module test-iordf
 */
public class CDKOWLReaderTest extends SimpleChemObjectReaderTest {

    private ILoggingTool logger = LoggingToolFactory.createLoggingTool(CDKOWLReaderTest.class);

    @BeforeClass
    public static void setup() {
        setSimpleChemObjectReader(new CDKOWLReader(), "data/owl/molecule.n3");
    }

    @Test
    public void testAccepts() {
        Assert.assertTrue(chemObjectIO.accepts(AtomContainer.class));
    }

    @Test
    public void testMolecule() throws Exception {
        String filename = "data/owl/molecule.n3";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CDKOWLReader reader = new CDKOWLReader(new InputStreamReader(ins));
        IAtomContainer mol = (IAtomContainer) reader.read(new AtomContainer());
        reader.close();

        Assert.assertNotNull(mol);
        Assert.assertEquals(2, mol.getAtomCount());
        Assert.assertEquals(1, mol.getBondCount());
    }

}
