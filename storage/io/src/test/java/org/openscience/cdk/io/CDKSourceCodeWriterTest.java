/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 *                    2010  Egon Willighagen <egonw@users.sf.net>
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

import java.io.StringWriter;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.interfaces.IAtomContainer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * TestCase for the writer CDK source code files using one test file.
 *
 * @cdk.module test-io
 * @see org.openscience.cdk.io.CDKSourceCodeWriterTest
 */
public class CDKSourceCodeWriterTest extends ChemObjectIOTest {

    @BeforeClass
    public static void setup() {
        setChemObjectIO(new CDKSourceCodeWriter());
    }

    @Test
    public void testAccepts() throws Exception {
        Assert.assertTrue(chemObjectIO.accepts(AtomContainer.class));
    }

    @Test
    public void testOutput() throws Exception {
        StringWriter writer = new StringWriter();
        IAtomContainer molecule = new AtomContainer();
        Atom atom = new Atom("C");
        atom.setMassNumber(14);
        molecule.addAtom(atom);

        CDKSourceCodeWriter sourceWriter = new CDKSourceCodeWriter(writer);
        sourceWriter.write(molecule);
        sourceWriter.close();
        String output = writer.toString();
        String newline = System.lineSeparator();
        assertThat(output, is("{" + newline +
                                      "  IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();" + newline +
                                      "  IAtomContainer mol = builder.newInstance(IAtomContainer.class);" + newline +
                                      "  IAtom a1 = builder.newInstance(IAtom.class,\"C\");" + newline +
                                      "  a1.setFormalCharge(0);" + newline +
                                      "  mol.addAtom(a1);" + newline +
                                      "}" + newline));
    }
}
