/*
 * Copyright (c) 2013. John May <jwmay@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */
package org.openscience.cdk.io;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.io.StringWriter;

/**
 * TestCase for the writer MOL2 writer from smiles.
 *
 * @cdk.module test-smiles
 * @see org.openscience.cdk.io.Mol2Writer
 */
public class SMILES2Mol2WriterTest {

    private static IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();

    /**
     * @throws org.openscience.cdk.exception.CDKException
     * @throws java.io.IOException
     * @cdk.bug 2675188
     */
    @Test
    public void testWriter1() throws Exception {
        SmilesParser sp = new SmilesParser(builder);
        IAtomContainer molecule = sp.parseSmiles("C([H])([H])([H])([H])");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);

        StringWriter swriter = new StringWriter();
        Mol2Writer writer = new Mol2Writer(swriter);
        writer.write(molecule);
        writer.close();
        Assert.assertTrue(swriter.getBuffer().toString().indexOf("1 C1 0.000 0.000 0.000 C.3") > 0);
        Assert.assertTrue(swriter.getBuffer().toString().indexOf("1 1 2 1") > 0);
    }

    @Test
    public void testWriter2() throws Exception {
        SmilesParser sp = new SmilesParser(builder);
        IAtomContainer molecule = sp.parseSmiles("c1ccccc1C=O");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        Aromaticity.cdkLegacy().apply(molecule);

        StringWriter swriter = new StringWriter();
        Mol2Writer writer = new Mol2Writer(swriter);
        writer.write(molecule);
        writer.close();

        Assert.assertTrue("Aromatic atom not properly reported",
                swriter.getBuffer().toString().indexOf("1 C1 0.000 0.000 0.000 C.ar") > 0);
        Assert.assertTrue(swriter.getBuffer().toString().indexOf("8 O8 0.000 0.000 0.000 O.2") > 0);
        Assert.assertTrue(swriter.getBuffer().toString().indexOf("7 C7 0.000 0.000 0.000 C.2") > 0);
        Assert.assertTrue("Aromatic bond not properly reported", swriter.getBuffer().toString().indexOf("1 1 2 ar") > 0);
        Assert.assertTrue(swriter.getBuffer().toString().indexOf("8 7 8 2") > 0);
    }

    @Test
    public void testWriterForAmide() throws Exception {
        SmilesParser sp = new SmilesParser(builder);
        IAtomContainer molecule = sp.parseSmiles("CC(=O)NC");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);

        StringWriter swriter = new StringWriter();
        Mol2Writer writer = new Mol2Writer(swriter);
        writer.write(molecule);
        writer.close();

        Assert.assertTrue(swriter.getBuffer().toString().indexOf("1 C1 0.000 0.000 0.000 C.3") > 0);
        Assert.assertTrue(swriter.getBuffer().toString().indexOf("3 O3 0.000 0.000 0.000 O.") > 0);
        Assert.assertTrue(swriter.getBuffer().toString().indexOf("4 N4 0.000 0.000 0.000 N.a") > 0);
        Assert.assertTrue(swriter.getBuffer().toString().indexOf("1 1 2 1") > 0);
        Assert.assertTrue("Amide bond not properly reported", swriter.getBuffer().toString().indexOf("3 2 4 am") > 0);
        Assert.assertTrue(swriter.getBuffer().toString().indexOf("4 4 5 1") > 0);
    }
}
