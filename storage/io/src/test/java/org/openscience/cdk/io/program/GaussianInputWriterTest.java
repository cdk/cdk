/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.io.program;

import java.io.StringWriter;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.ChemObjectIOTest;
import org.openscience.cdk.templates.TestMoleculeFactory;

/**
 * TestCase for the reading MDL mol files using one test file.
 * A test case for SDF files is available as separate Class.
 *
 * @cdk.module test-io
 *
 * @see org.openscience.cdk.io.GaussianInputWriter
 */
public class GaussianInputWriterTest extends ChemObjectIOTest {

    @BeforeClass
    public static void setup() {
        setChemObjectIO(new GaussianInputWriter());
    }

    @Test
    public void testAccepts() {
        GaussianInputWriter reader = new GaussianInputWriter();
        Assert.assertTrue(reader.accepts(IAtomContainer.class));
    }

    /**
     * @cdk.bug 2501715
     */
    @Test
    public void testWrite() throws Exception {
        IAtomContainer molecule = TestMoleculeFactory.makeAlphaPinene();
        StringWriter writer = new StringWriter();
        GaussianInputWriter gaussianWriter = new GaussianInputWriter(writer);
        gaussianWriter.write(molecule);
        gaussianWriter.close();
        String output = writer.toString();
        Assert.assertNotSame(0, output.length());
    }
}
