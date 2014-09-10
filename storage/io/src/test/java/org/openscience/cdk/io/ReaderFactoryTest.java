/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
 *  */
package org.openscience.cdk.io;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.io.formats.CMLFormat;
import org.openscience.cdk.io.formats.CTXFormat;
import org.openscience.cdk.io.formats.Gaussian98Format;
import org.openscience.cdk.io.formats.GhemicalSPMFormat;
import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.io.formats.IChemFormatMatcher;
import org.openscience.cdk.io.formats.MDLFormat;
import org.openscience.cdk.io.formats.MDLV2000Format;
import org.openscience.cdk.io.formats.MDLV3000Format;
import org.openscience.cdk.io.formats.Mol2Format;
import org.openscience.cdk.io.formats.PDBFormat;
import org.openscience.cdk.io.formats.PubChemASNFormat;
import org.openscience.cdk.io.formats.PubChemCompoundXMLFormat;
import org.openscience.cdk.io.formats.PubChemSubstanceXMLFormat;
import org.openscience.cdk.io.formats.ShelXFormat;
import org.openscience.cdk.io.formats.XYZFormat;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 * TestCase for the instantiation and functionality of the {@link ReaderFactory}.
 *
 * @cdk.module test-io
 */
public class ReaderFactoryTest extends AbstractReaderFactoryTest {

    private ReaderFactory factory = new ReaderFactory();

    @Test
    public void testCreateReader_IChemFormat() {
        IChemFormat format = (IChemFormat) XYZFormat.getInstance();
        ISimpleChemObjectReader reader = factory.createReader(format);
        Assert.assertNotNull(reader);
        Assert.assertEquals(format.getFormatName(), reader.getFormat().getFormatName());
    }

    @Test
    public void testGaussian98() throws Exception {
        expectReader("data/gaussian/g98.out", Gaussian98Format.getInstance(), -1, -1);
    }

    @Test
    public void testGhemical() throws Exception {
        expectReader("data/ghemical/ethene.mm1gp", GhemicalSPMFormat.getInstance(), 6, 5);
    }

    @Test
    public void testCML() throws Exception {
        expectReader("data/cml/estron.cml", CMLFormat.getInstance(), -1, -1);
    }

    @Test
    public void testXYZ() throws Exception {
        expectReader("data/xyz/bf3.xyz", XYZFormat.getInstance(), -1, -1);
    }

    @Test
    public void testShelX() throws Exception {
        expectReader("data/shelx/frame_1.res", ShelXFormat.getInstance(), -1, -1);
    }

    @Test
    public void testMDLMol() throws Exception {
        expectReader("data/mdl/bug1014344-1.mol", MDLFormat.getInstance(), 21, 21);
    }

    @Test
    public void testMDLMolV2000() throws Exception {
        expectReader("data/mdl/methylbenzol.mol", MDLV2000Format.getInstance(), 15, 15);
    }

    @Test
    public void testDetection() throws Exception {
        expectReader("data/mdl/withcharges.mol", MDLV2000Format.getInstance(), 9, 9);
    }

    @Test
    public void testMDLMolV3000() throws Exception {
        expectReader("data/mdl/molV3000.mol", MDLV3000Format.getInstance(), -1, -1);
    }

    @Ignore("test moved to cdk-test-pdb/PDBReaderFactoryTest")
    public void testPDB() throws Exception {
        expectReader("data/pdb/coffeine.pdb", PDBFormat.getInstance(), -1, -1);
    }

    @Test
    public void testMol2() throws Exception {
        expectReader("data/mol2/fromWebsite.mol2", Mol2Format.getInstance(), -1, -1);
    }

    @Test
    public void testCTX() throws Exception {
        expectReader("data/ctx/methanol_with_descriptors.ctx", CTXFormat.getInstance(), -1, -1);
    }

    @Test
    public void testPubChemCompoundASN() throws Exception {
        expectReader("data/asn/pubchem/cid1.asn", PubChemASNFormat.getInstance(), -1, -1);
    }

    @Test
    public void testPubChemSubstanceXML() throws Exception {
        expectReader("data/asn/pubchem/sid577309.xml", PubChemSubstanceXMLFormat.getInstance(), -1, -1);
    }

    @Test
    public void testPubChemCompoundXML() throws Exception {
        expectReader("data/asn/pubchem/cid1145.xml", PubChemCompoundXMLFormat.getInstance(), -1, -1);
    }

    @Test
    public void testSmiles() throws Exception {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("data/smiles/drugs.smi");
        Object reader = factory.createReader(is);
        Assert.assertNull(reader);
    }

    /**
     * @cdk.bug 2153298
     */
    @Test
    public void testBug2153298() throws Exception {
        String filename = "data/asn/pubchem/cid1145.xml";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        Assert.assertNotNull("Cannot find file: " + filename, ins);
        IChemFormatMatcher realFormat = (IChemFormatMatcher) PubChemCompoundXMLFormat.getInstance();
        factory.registerFormat(realFormat);
        // ok, if format ok, try instantiating a reader
        ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = factory.createReader(ins);
        Assert.assertNotNull(reader);
        Assert.assertEquals(((IChemFormat) PubChemCompoundXMLFormat.getInstance()).getReaderClassName(), reader
                .getClass().getName());
        // now try reading something from it
        IAtomContainer molecule = (IAtomContainer) reader.read(new AtomContainer());
        Assert.assertNotNull(molecule);
        Assert.assertNotSame(0, molecule.getAtomCount());
        Assert.assertNotSame(0, molecule.getBondCount());
    }

    @Test
    public void testReadGz() throws Exception {
        String filename = "data/xyz/bf3.xyz.gz";
        InputStream input = new BufferedInputStream(new GZIPInputStream(this.getClass().getClassLoader()
                .getResourceAsStream(filename)));
        // ok, if format ok, try instantiating a reader
        ISimpleChemObjectReader reader = factory.createReader(input);
        Assert.assertNotNull(reader);
        Assert.assertEquals(((IChemFormat) XYZFormat.getInstance()).getReaderClassName(), reader.getClass().getName());
        // now try reading something from it
        IChemFile chemFile = (IChemFile) reader.read(new ChemFile());
        IAtomContainer molecule = new AtomContainer();
        for (IAtomContainer container : ChemFileManipulator.getAllAtomContainers(chemFile)) {
            molecule.add(container);
        }
        Assert.assertNotNull(molecule);
        Assert.assertEquals(4, molecule.getAtomCount());
    }

    @Test
    public void testReadGzWithGzipDetection() throws Exception {
        String filename = "data/xyz/bf3.xyz.gz";
        InputStream input = this.getClass().getClassLoader().getResourceAsStream(filename);
        // ok, if format ok, try instantiating a reader
        ISimpleChemObjectReader reader = factory.createReader(input);
        Assert.assertNotNull(reader);
        Assert.assertEquals(((IChemFormat) XYZFormat.getInstance()).getReaderClassName(), reader.getClass().getName());
        // now try reading something from it
        IChemFile chemFile = (IChemFile) reader.read(new ChemFile());
        IAtomContainer molecule = new AtomContainer();
        for (IAtomContainer container : ChemFileManipulator.getAllAtomContainers(chemFile)) {
            molecule.add(container);
        }
        Assert.assertNotNull(molecule);
        Assert.assertEquals(4, molecule.getAtomCount());
    }

}
