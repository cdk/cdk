/* Copyright (C) 2009  Egon Willighagen <egonw@users.sf.net>
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

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.listener.PropertiesListener;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * TestCase for the writer for SMILES files.
 *
 * @cdk.module test-smiles
 * @see org.openscience.cdk.io.SMILESWriter
 */
public class SMILESWriterTest extends ChemObjectIOTest {

    @BeforeClass
    public static void setup() {
        setChemObjectIO(new SMILESWriter());
    }

    @Test
    public void testAccepts() throws Exception {
        SMILESWriter reader = new SMILESWriter();
        Assert.assertTrue(reader.accepts(AtomContainer.class));
        Assert.assertTrue(reader.accepts(AtomContainerSet.class));
    }

    @Test
    public void testWriteSMILESFile() throws Exception {
        StringWriter stringWriter = new StringWriter();
        IAtomContainer benzene = TestMoleculeFactory.makeBenzene();
        addImplicitHydrogens(benzene);
        SMILESWriter smilesWriter = new SMILESWriter(stringWriter);
        smilesWriter.write(benzene);
        smilesWriter.close();
        Assert.assertTrue(stringWriter.toString().contains("C=C"));
    }

    @Test
    public void testWriteAromatic() throws Exception {
        StringWriter stringWriter = new StringWriter();
        IAtomContainer benzene = TestMoleculeFactory.makeBenzene();
        addImplicitHydrogens(benzene);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(benzene);
        Aromaticity.cdkLegacy().apply(benzene);
        SMILESWriter smilesWriter = new SMILESWriter(stringWriter);
        Properties prop = new Properties();
        prop.setProperty("UseAromaticity", "true");
        PropertiesListener listener = new PropertiesListener(prop);
        smilesWriter.addChemObjectIOListener(listener);
        smilesWriter.customizeJob();
        smilesWriter.write(benzene);
        smilesWriter.close();
        Assert.assertFalse(stringWriter.toString().contains("C=C"));
        Assert.assertTrue(stringWriter.toString().contains("ccc"));
    }

    @Test
    public void testWriteNonCanon() throws Exception {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol1 = smipar.parseSmiles("CCO");
        IAtomContainer mol2 = smipar.parseSmiles("OCC");
        StringWriter wtr = new StringWriter();
        try (SMILESWriter smigen = new SMILESWriter(wtr)) {
            smigen.write(mol1);
            smigen.write(mol2);
        }
        String[] lines = wtr.toString().split("\n");
        assertThat(lines.length, is(2));
        assertThat(new HashSet<>(Arrays.asList(lines)).size(), is(2));
    }

    @Test
    public void testWriteCanon() throws Exception {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol1 = smipar.parseSmiles("CCO");
        IAtomContainer mol2 = smipar.parseSmiles("OCC");
        StringWriter wtr = new StringWriter();
        try (SMILESWriter smigen = new SMILESWriter(wtr)) {
            smigen.setFlavor(SmiFlavor.Canonical);
            smigen.write(mol1);
            smigen.write(mol2);
        }
        String[] lines = wtr.toString().split("\n");
        assertThat(lines.length, is(2));
        assertThat(new HashSet<>(Arrays.asList(lines)).size(), is(1));
    }

    @Test
    public void testWriteWithTitle() throws Exception {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol1 = smipar.parseSmiles("CCO mol 1");
        IAtomContainer mol2 = smipar.parseSmiles("OCC mol 2");
        StringWriter wtr = new StringWriter();
        try (SMILESWriter smigen = new SMILESWriter(wtr)) {
            smigen.setFlavor(SmiFlavor.Canonical);
            smigen.setWriteTitle(true);
            smigen.write(mol1);
            smigen.write(mol2);
        }
        assertThat(wtr.toString(), containsString("mol 1"));
        assertThat(wtr.toString(), containsString("mol 2"));
    }

    @Test
    public void testWriteWithoutTitle() throws Exception {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol1 = smipar.parseSmiles("CCO mol 1");
        IAtomContainer mol2 = smipar.parseSmiles("OCC mol 2");
        StringWriter wtr = new StringWriter();
        try (SMILESWriter smigen = new SMILESWriter(wtr)) {
            smigen.setFlavor(SmiFlavor.Canonical);
            smigen.setWriteTitle(false);
            smigen.write(mol1);
            smigen.write(mol2);
        }
        assertThat(wtr.toString(), not(containsString("mol 1")));
        assertThat(wtr.toString(), not(containsString("mol 2")));
    }

    @Test
    public void testWriteSmiFlavor() throws Exception {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol1 = smipar.parseSmiles("c1ccccc1");
        StringWriter wtr = new StringWriter();
        try (SMILESWriter smigen = new SMILESWriter(wtr)) {
            smigen.setFlavor(SmiFlavor.InChILabelling);
            smigen.write(mol1);
        }
        String[] lines = wtr.toString().split("\n");
        assertThat(wtr.toString(), containsString("C=1C=CC=CC1"));
    }
}
