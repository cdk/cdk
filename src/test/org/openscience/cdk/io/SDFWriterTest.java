/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
import java.util.Properties;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.listener.PropertiesListener;
import org.openscience.cdk.smiles.InvPair;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * TestCase for the writer MDL SD file writer.
 *
 * @cdk.module test-io
 *
 * @see org.openscience.cdk.io.SDFWriter
 */
public class SDFWriterTest extends ChemObjectWriterTest {

    private static IChemObjectBuilder builder;

    @BeforeClass public static void setup() {
        builder = DefaultChemObjectBuilder.getInstance();
        setChemObjectWriter(new SDFWriter());
    }

    @Test public void testAccepts() throws Exception {
    	SDFWriter reader = new SDFWriter();
    	Assert.assertTrue(reader.accepts(ChemFile.class));
    	Assert.assertTrue(reader.accepts(ChemModel.class));
        Assert.assertTrue(reader.accepts(AtomContainerSet.class));
    }

    @Test public void testWrite_IAtomContainerSet_Properties_Off() throws Exception {
        StringWriter writer = new StringWriter();
        IAtomContainerSet molSet = new AtomContainerSet();
        IAtomContainer molecule = new AtomContainer();
        molecule.addAtom(new Atom("C"));
        molecule.setProperty("foo", "bar");
        molSet.addAtomContainer(molecule);
        
        SDFWriter sdfWriter = new SDFWriter(writer);
        Properties sdfWriterProps = new Properties();
        sdfWriterProps.put("writeProperties", "false");
        sdfWriter.addChemObjectIOListener(
            new PropertiesListener(sdfWriterProps)
        );
        sdfWriter.customizeJob();
        sdfWriter.write(molSet);
        sdfWriter.close();
        Assert.assertTrue(writer.toString().indexOf("<foo>") != -1);
        Assert.assertTrue(writer.toString().indexOf("bar") != -1);
    }

    /**
     * @cdk.bug 2827745
     */
    @Test public void testWrite_IAtomContainerSet() throws Exception {
        StringWriter writer = new StringWriter();
        IAtomContainerSet molSet = builder.newInstance(IAtomContainerSet.class);
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class,"C"));
        molSet.addAtomContainer(molecule);

        SDFWriter sdfWriter = new SDFWriter(writer);
        sdfWriter.write(molSet);
        sdfWriter.close();
        Assert.assertNotSame(0, writer.toString().length());
    }

    @Test public void testWrite_IAtomContainerSet_Properties() throws Exception {
        StringWriter writer = new StringWriter();
        IAtomContainerSet molSet = new AtomContainerSet();
        IAtomContainer molecule = new AtomContainer();
        molecule.addAtom(new Atom("C"));
        molecule.setProperty("foo", "bar");
        molSet.addAtomContainer(molecule);
        
        SDFWriter sdfWriter = new SDFWriter(writer);
        sdfWriter.write(molSet);
        sdfWriter.close();
        Assert.assertTrue(writer.toString().indexOf("<foo>") != -1);
        Assert.assertTrue(writer.toString().indexOf("bar") != -1);
    }

    @Test public void testWrite_IAtomContainerSet_CDKProperties() throws Exception {
        StringWriter writer = new StringWriter();
        IAtomContainerSet molSet = new AtomContainerSet();
        IAtomContainer molecule = new AtomContainer();
        molecule.addAtom(new Atom("C"));
        molecule.setProperty(InvPair.CANONICAL_LABEL, "bar");
        molSet.addAtomContainer(molecule);
        
        SDFWriter sdfWriter = new SDFWriter(writer);
        sdfWriter.write(molSet);
        sdfWriter.close();
        Assert.assertTrue(
            writer.toString().indexOf(InvPair.CANONICAL_LABEL) == -1
        );
    }

    @Test public void testWrite_IAtomContainerSet_SingleMolecule() throws Exception {
        StringWriter writer = new StringWriter();
        IAtomContainerSet molSet = new AtomContainerSet();
        IAtomContainer molecule = new AtomContainer();
        molecule.addAtom(new Atom("C"));
        molSet.addAtomContainer(molecule);
        
        SDFWriter sdfWriter = new SDFWriter(writer);
        sdfWriter.write(molSet);
        sdfWriter.close();
        Assert.assertTrue(writer.toString().indexOf("$$$$") != -1);
    }

    @Test public void testWrite_IAtomContainerSet_MultIAtomContainer() throws Exception {
        StringWriter writer = new StringWriter();
        IAtomContainerSet molSet = new AtomContainerSet();
        IAtomContainer molecule = new AtomContainer();
        molecule.addAtom(new Atom("C"));
        molSet.addAtomContainer(molecule);
        molecule = new AtomContainer();
        molecule.addAtom(new Atom("C"));
        molSet.addAtomContainer(molecule);
        
        SDFWriter sdfWriter = new SDFWriter(writer);
        sdfWriter.write(molSet);
        sdfWriter.close();
        Assert.assertTrue(writer.toString().indexOf("$$$$") != -1);
    }

    @Test public void testWrite_IAtomContainer_MultIAtomContainer() throws Exception {
        StringWriter writer = new StringWriter();
        SDFWriter sdfWriter = new SDFWriter(writer);

        IAtomContainer molecule = new AtomContainer();
        molecule.addAtom(new Atom("C"));
        molecule.setProperty("foo", "bar");
        sdfWriter.write(molecule);

        molecule = new AtomContainer();
        molecule.addAtom(new Atom("C"));
        molecule.setProperty("toys", "r-us");
        sdfWriter.write(molecule);
        
        sdfWriter.close();
        System.out.println(writer.toString());
        Assert.assertTrue(writer.toString().indexOf("foo") != -1);
        Assert.assertTrue(writer.toString().indexOf("bar") != -1);
        Assert.assertTrue(writer.toString().indexOf("toys") != -1);
        Assert.assertTrue(writer.toString().indexOf("r-us") != -1);
        Assert.assertTrue(writer.toString().indexOf("$$$$") != -1);
    }

    /**
     * @cdk.bug 3392485
     */
    @Test
    public void testIOPropPropagation() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("c1ccccc1CC");
        CDKHueckelAromaticityDetector.detectAromaticity(mol);

        StringWriter strWriter = new StringWriter();
        SDFWriter writer = new SDFWriter(strWriter);

        Properties sdfWriterProps = new Properties();
        sdfWriterProps.put("WriteAromaticBondTypes", "true");
        writer.addChemObjectIOListener(
            new PropertiesListener(sdfWriterProps)
        );
        writer.customizeJob();
        writer.write(mol);
        writer.close();

        String output = strWriter.toString();
        Assert.assertTrue(output.contains("4  0  0  0  0"));
    }
}
