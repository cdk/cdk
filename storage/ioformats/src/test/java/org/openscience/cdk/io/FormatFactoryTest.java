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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.io.formats.ABINITFormat;
import org.openscience.cdk.io.formats.ADFFormat;
import org.openscience.cdk.io.formats.Aces2Format;
import org.openscience.cdk.io.formats.CMLFormat;
import org.openscience.cdk.io.formats.CTXFormat;
import org.openscience.cdk.io.formats.GamessFormat;
import org.openscience.cdk.io.formats.Gaussian92Format;
import org.openscience.cdk.io.formats.Gaussian94Format;
import org.openscience.cdk.io.formats.Gaussian98Format;
import org.openscience.cdk.io.formats.GhemicalSPMFormat;
import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.io.formats.IChemFormatMatcher;
import org.openscience.cdk.io.formats.INChIFormat;
import org.openscience.cdk.io.formats.INChIPlainTextFormat;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.JaguarFormat;
import org.openscience.cdk.io.formats.MDLFormat;
import org.openscience.cdk.io.formats.MDLV2000Format;
import org.openscience.cdk.io.formats.MDLV3000Format;
import org.openscience.cdk.io.formats.Mol2Format;
import org.openscience.cdk.io.formats.PDBFormat;
import org.openscience.cdk.io.formats.PubChemASNFormat;
import org.openscience.cdk.io.formats.PubChemCompoundXMLFormat;
import org.openscience.cdk.io.formats.PubChemCompoundsXMLFormat;
import org.openscience.cdk.io.formats.PubChemSubstanceXMLFormat;
import org.openscience.cdk.io.formats.PubChemSubstancesASNFormat;
import org.openscience.cdk.io.formats.PubChemSubstancesXMLFormat;
import org.openscience.cdk.io.formats.ShelXFormat;
import org.openscience.cdk.io.formats.VASPFormat;
import org.openscience.cdk.io.formats.XYZFormat;

/**
 * TestCase for the instantiation and functionality of the {@link FormatFactory}.
 *
 * @cdk.module test-ioformats
 */
public class FormatFactoryTest extends CDKTestCase {

    private static FormatFactory factory;

    @BeforeClass
    public static void setup() {
        factory = new FormatFactory();
    }

    @Test
    public void testGaussian94() throws Exception {
        expectFormat("data/gaussian/4-cyanophenylnitrene-Benzazirine-TS.g94.out", Gaussian94Format.getInstance());
    }

    @Test
    public void testGaussian98() throws Exception {
        expectFormat("data/gaussian/g98.out", Gaussian98Format.getInstance());
    }

    @Test
    public void testGaussian92() throws Exception {
        expectFormat("data/gaussian/phenylnitrene.g92.out", Gaussian92Format.getInstance());
    }

    @Test
    public void testGhemical() throws Exception {
        expectFormat("data/ghemical/ethene.mm1gp", GhemicalSPMFormat.getInstance());
    }

    @Test
    public void testJaguar() throws Exception {
        expectFormat("data/jaguar/ch4-opt.out", JaguarFormat.getInstance());
    }

    @Test
    public void testINChI() throws Exception {
        expectFormat("data/inchi/guanine.inchi.xml", INChIFormat.getInstance());
    }

    @Test
    public void testINChIPlainText() throws Exception {
        expectFormat("data/inchi/guanine.inchi", INChIPlainTextFormat.getInstance());
    }

    @Test
    public void testVASP() throws Exception {
        expectFormat("data/vasp/LiMoS2_optimisation_ISIF3.vasp", VASPFormat.getInstance());
    }

    @Test
    public void testAces2() throws Exception {
        expectFormat("data/aces2/ch3oh_ace.out", Aces2Format.getInstance());
    }

    @Test
    public void testADF() throws Exception {
        expectFormat("data/adf/ammonia.adf.out", ADFFormat.getInstance());
    }

    @Test
    public void testGamess() throws Exception {
        expectFormat("data/gamess/ch3oh_gam.out", GamessFormat.getInstance());
    }

    @Test
    public void testABINIT() throws Exception {
        expectFormat("data/abinit/t54.in", ABINITFormat.getInstance());
    }

    @Test
    public void testCML() throws Exception {
        expectFormat("data/cml/estron.cml", CMLFormat.getInstance());
    }

    @Test
    public void testXYZ() throws Exception {
        expectFormat("data/xyz/bf3.xyz", XYZFormat.getInstance());
    }

    @Test
    public void testShelX() throws Exception {
        expectFormat("data/shelx/frame_1.res", ShelXFormat.getInstance());
    }

    @Test
    public void testMDLMol() throws Exception {
        expectFormat("data/mdl/bug1014344-1.mol", MDLFormat.getInstance());
    }

    @Test
    public void testMDLMolV2000() throws Exception {
        expectFormat("data/mdl/methylbenzol.mol", MDLV2000Format.getInstance());
    }

    @Test
    public void testDetection() throws Exception {
        expectFormat("data/mdl/withcharges.mol", MDLV2000Format.getInstance());
    }

    @Test
    public void testMDLMolV3000() throws Exception {
        expectFormat("data/mdl/molV3000.mol", MDLV3000Format.getInstance());
    }

    @Test
    public void testPDB() throws Exception {
        expectFormat("data/pdb/coffeine.pdb", PDBFormat.getInstance());
    }

    @Test
    public void testMol2() throws Exception {
        expectFormat("data/mol2/fromWebsite.mol2", Mol2Format.getInstance());
    }

    @Test
    public void testCTX() throws Exception {
        expectFormat("data/ctx/methanol_with_descriptors.ctx", CTXFormat.getInstance());
    }

    @Test
    public void testPubChemCompoundASN() throws Exception {
        expectFormat("data/asn/pubchem/cid1.asn", PubChemASNFormat.getInstance());
    }

    @Test
    public void testPubChemSubstancesASN() throws Exception {
        expectFormat("data/asn/pubchem/list.asn", PubChemSubstancesASNFormat.getInstance());
    }

    @Test
    public void testPubChemCompoundsXML() throws Exception {
        expectFormat("data/asn/pubchem/aceticAcids38.xml", PubChemCompoundsXMLFormat.getInstance());
    }

    @Test
    public void testPubChemSubstancesXML() throws Exception {
        expectFormat("data/asn/pubchem/taxols.xml", PubChemSubstancesXMLFormat.getInstance());
    }

    @Test
    public void testPubChemSubstanceXML() throws Exception {
        expectFormat("data/asn/pubchem/sid577309.xml", PubChemSubstanceXMLFormat.getInstance());
    }

    @Test
    public void testPubChemCompoundXML() throws Exception {
        expectFormat("data/asn/pubchem/cid1145.xml", PubChemCompoundXMLFormat.getInstance());
    }

    private void expectFormat(String filename, IResourceFormat expectedFormat) throws Exception {
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        Assert.assertNotNull("Cannot find file: " + filename, ins);
        if (expectedFormat instanceof IChemFormatMatcher) {
            factory.registerFormat((IChemFormatMatcher) expectedFormat);
        }
        ins = new BufferedInputStream(ins);
        IChemFormat format = factory.guessFormat(ins);
        Assert.assertNotNull(format);
        Assert.assertEquals(expectedFormat.getFormatName(), format.getFormatName());
    }

    /**
     * @cdk.bug 2153298
     */
    @Test
    public void testGuessFormat() throws Exception {
        String filename = "data/xyz/bf3.xyz";
        InputStream input = this.getClass().getClassLoader().getResourceAsStream(filename);
        input = new BufferedInputStream(input);
        IChemFormat format = factory.guessFormat(input);
        Assert.assertNotNull(format);
        // make sure the InputStream is properly reset
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line = reader.readLine();
        Assert.assertNotNull(line);
        Assert.assertEquals("4", line);
        line = reader.readLine();
        Assert.assertNotNull(line);
        Assert.assertEquals("Bortrifluorid", line);
    }

    @Test
    public void testGuessFormat_Gz() throws Exception {
        String filename = "data/xyz/bf3.xyz.gz";
        InputStream input = new BufferedInputStream(new GZIPInputStream(this.getClass().getClassLoader()
                .getResourceAsStream(filename)));
        IChemFormat format = factory.guessFormat(input);
        Assert.assertNotNull(format);
        // make sure the InputStream is properly reset
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line = reader.readLine();
        Assert.assertNotNull(line);
        Assert.assertEquals("4", line);
        line = reader.readLine();
        Assert.assertNotNull(line);
        Assert.assertEquals("Bortrifluorid", line);
    }

    @Test
    public void testGuessFormat_Reader() throws Exception {
        String filename = "data/xyz/bf3.xyz";
        InputStream input = this.getClass().getClassLoader().getResourceAsStream(filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        IChemFormat format = factory.guessFormat(reader);
        Assert.assertNotNull(format);
        // make sure the Reader is properly reset
        String line = reader.readLine();
        Assert.assertNotNull(line);
        Assert.assertEquals("4", line);
        line = reader.readLine();
        Assert.assertNotNull(line);
        Assert.assertEquals("Bortrifluorid", line);
    }

    @Test
    public void testGetFormats() {
        List<IChemFormatMatcher> formats = factory.getFormats();
        Assert.assertNotNull(formats);
        Assert.assertNotSame(0, formats.size());
        for (IChemFormatMatcher matcher : formats) {
            Assert.assertNotNull(matcher);
        }
    }

    class DummyFormat implements IChemFormatMatcher {

        @Override
        public String getReaderClassName() {
            return null;
        }

        @Override
        public String getWriterClassName() {
            return null;
        }

        @Override
        public int getSupportedDataFeatures() {
            return 0;
        }

        @Override
        public int getRequiredDataFeatures() {
            return 0;
        }

        @Override
        public String getFormatName() {
            return "Dummy Format";
        }

        @Override
        public String getMIMEType() {
            return null;
        }

        @Override
        public boolean isXMLBased() {
            return false;
        }

        @Override
        public String getPreferredNameExtension() {
            return "dummy";
        }

        @Override
        public String[] getNameExtensions() {
            return new String[]{"dummy", "dum"};
        }

        @Override
        public MatchResult matches(List<String> lines) {
            if (lines.size() > 0 && lines.get(0).startsWith("DummyFormat:")) {
                return new MatchResult(true, this, 0);
            }
            return NO_MATCH;
        }
    }

    @Test
    public void testRegisterFormat() throws IOException {
        factory.registerFormat(new DummyFormat());
        StringReader reader = new StringReader("DummyFormat:");
        IChemFormat format = factory.guessFormat(reader);
        Assert.assertNotNull(format);
        Assert.assertTrue(format instanceof DummyFormat);
    }
}
