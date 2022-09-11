/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.test.io.ChemObjectIOTest;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

import static org.mockito.Mockito.mock;

/**
 * TestCase for the writer MOL2 writer.
 *
 * @cdk.module test-io
 * @see Mol2Writer
 * @see SMILES2Mol2WriterTest
 */
class Mol2WriterTest extends ChemObjectIOTest {

    private static IChemObjectBuilder builder;

    @BeforeAll
    static void setup() {
        builder = DefaultChemObjectBuilder.getInstance();
        setChemObjectIO(new Mol2Writer());
    }

    @Test
    void testAccepts() throws Exception {
        Mol2Writer writer = new Mol2Writer();
        Assertions.assertTrue(writer.accepts(AtomContainer.class));
    }

    /**
     * @throws CDKException
     * @throws IOException
     * @cdk.bug 2675188
     */
    @Disabled("moved to SMILES2Mol2WriterTest")
    void testWriter1() throws Exception {
        IAtomContainer molecule = mock(IAtomContainer.class);

        StringWriter swriter = new StringWriter();
        Mol2Writer writer = new Mol2Writer(swriter);
        writer.write(molecule);
        writer.close();
        Assertions.assertTrue(swriter.getBuffer().toString().indexOf("1 C1 0.000 0.000 0.000 C.3") > 0);
        Assertions.assertTrue(swriter.getBuffer().toString().indexOf("1 2 1 1") > 0);
    }

    @Disabled("moved to SMILES2Mol2WriterTest")
    void testWriter2() throws Exception {
        IAtomContainer molecule = mock(IAtomContainer.class);
        Aromaticity.cdkLegacy().apply(molecule);

        StringWriter swriter = new StringWriter();
        Mol2Writer writer = new Mol2Writer(swriter);
        writer.write(molecule);
        writer.close();

        Assertions.assertTrue(swriter.getBuffer().toString().indexOf("1 C1 0.000 0.000 0.000 C.ar") > 0, "Aromatic atom not properly reported");
        Assertions.assertTrue(swriter.getBuffer().toString().indexOf("8 O8 0.000 0.000 0.000 O.2") > 0);
        Assertions.assertTrue(swriter.getBuffer().toString().indexOf("7 C7 0.000 0.000 0.000 C.2") > 0);
        Assertions.assertTrue(swriter.getBuffer().toString().indexOf("1 2 1 ar") > 0, "Aromatic bond not properly reported");
        Assertions.assertTrue(swriter.getBuffer().toString().indexOf("8 8 7 2") > 0);
    }

    @Disabled("moved to SMILES2Mol2WriterTest")
    void testWriterForAmide() throws Exception {
        IAtomContainer molecule = mock(IAtomContainer.class);
        Aromaticity.cdkLegacy().apply(molecule);

        StringWriter swriter = new StringWriter();
        Mol2Writer writer = new Mol2Writer(swriter);
        writer.write(molecule);
        writer.close();

        Assertions.assertTrue(swriter.getBuffer().toString().indexOf("1 C1 0.000 0.000 0.000 C.3") > 0);
        Assertions.assertTrue(swriter.getBuffer().toString().indexOf("3 O3 0.000 0.000 0.000 O.") > 0);
        Assertions.assertTrue(swriter.getBuffer().toString().indexOf("4 N4 0.000 0.000 0.000 N.a") > 0);
        Assertions.assertTrue(swriter.getBuffer().toString().indexOf("1 2 1 1") > 0);
        Assertions.assertTrue(swriter.getBuffer().toString().indexOf("3 4 2 am") > 0, "Amide bond not properly reported");
        Assertions.assertTrue(swriter.getBuffer().toString().indexOf("4 5 4 1") > 0);
    }

    /**
     * This test just ensures that Mol2Writer does not throw an NPE.
     *
     * It does not test whether the output is correct or not.
     * @throws Exception
     * @cdk.bug 3315503
     */
    @Test
    void testMissingAtomType() throws Exception {
        String filename = "ligand-1a0i.sdf";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IChemFile fileContents = reader.read(new ChemFile());
        reader.close();
        List<IAtomContainer> molecules = ChemFileManipulator.getAllAtomContainers(fileContents);
        IAtomContainer mol = molecules.get(0);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);

        StringWriter writer = new StringWriter();
        Mol2Writer molwriter = new Mol2Writer(writer);
        molwriter.write(mol);
        molwriter.close();

        String mol2file = writer.getBuffer().toString();
        Assertions.assertTrue(mol2file.contains("-1.209 -18.043 49.44 X"));
    }
}
