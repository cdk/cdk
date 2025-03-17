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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;
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
 */
class FormatFactoryTest extends CDKTestCase {

    private static FormatFactory factory;

    @BeforeAll
    static void setup() {
        factory = new FormatFactory();
    }

    @Test
    void testGaussian94() throws Exception {
        expectFormat("org/openscience/cdk/io/4-cyanophenylnitrene-Benzazirine-TS.g94.out", Gaussian94Format.getInstance());
    }

    @Test
    void testGaussian98() throws Exception {
        expectFormat("org/openscience/cdk/io/g98.out", Gaussian98Format.getInstance());
    }

    @Test
    void testGaussian92() throws Exception {
        expectFormat("org/openscience/cdk/io/phenylnitrene.g92.out", Gaussian92Format.getInstance());
    }

    @Test
    void testGhemical() throws Exception {
        expectFormat("org/openscience/cdk/io/ethene.mm1gp", GhemicalSPMFormat.getInstance());
    }

    @Test
    void testJaguar() throws Exception {
        expectFormat("org/openscience/cdk/io/ch4-opt.out", JaguarFormat.getInstance());
    }

    @Test
    void testINChI() throws Exception {
        expectFormat("org/openscience/cdk/io/guanine.inchi.xml", INChIFormat.getInstance());
    }

    @Test
    void testINChIPlainText() throws Exception {
        expectFormat("org/openscience/cdk/io/guanine.inchi", INChIPlainTextFormat.getInstance());
    }

    @Test
    void testVASP() throws Exception {
        expectFormat("org/openscience/cdk/io/LiMoS2_optimisation_ISIF3.vasp", VASPFormat.getInstance());
    }

    @Test
    void testAces2() throws Exception {
        expectFormat("org/openscience/cdk/io/ch3oh_ace.out", Aces2Format.getInstance());
    }

    @Test
    void testADF() throws Exception {
        expectFormat("org/openscience/cdk/io/ammonia.adf.out", ADFFormat.getInstance());
    }

    @Test
    void testGamess() throws Exception {
        expectFormat("org/openscience/cdk/io/ch3oh_gam.out", GamessFormat.getInstance());
    }

    @Test
    void testABINIT() throws Exception {
        expectFormat("org/openscience/cdk/io/t54.in", ABINITFormat.getInstance());
    }

    @Test
    void testCML() throws Exception {
        expectFormat("org/openscience/cdk/io/estron.cml", CMLFormat.getInstance());
    }

    @Test
    void testXYZ() throws Exception {
        expectFormat("org/openscience/cdk/io/bf3.xyz", XYZFormat.getInstance());
    }

    @Test
    void testShelX() throws Exception {
        expectFormat("org/openscience/cdk/io/frame_1.res", ShelXFormat.getInstance());
    }

    @Test
    void testMDLMol() throws Exception {
        expectFormat("org/openscience/cdk/io/bug1014344-1.mol", MDLFormat.getInstance());
    }

    @Test
    void testMDLMolV2000() throws Exception {
        expectFormat("org/openscience/cdk/io/methylbenzol.mol", MDLV2000Format.getInstance());
    }

    @Test
    void testDetection() throws Exception {
        expectFormat("org/openscience/cdk/io/withcharges.mol", MDLV2000Format.getInstance());
    }

    @Test
    void testMDLMolV3000() throws Exception {
        expectFormat("org/openscience/cdk/io/molV3000.mol", MDLV3000Format.getInstance());
    }

    @Test
    void testPDB() throws Exception {
        expectFormat("org/openscience/cdk/io/coffeine.pdb", PDBFormat.getInstance());
    }

    @Test
    void testMol2() throws Exception {
        expectFormat("org/openscience/cdk/io/fromWebsite.mol2", Mol2Format.getInstance());
    }

    @Test
    void testCTX() throws Exception {
        expectFormat("org/openscience/cdk/io/methanol_with_descriptors.ctx", CTXFormat.getInstance());
    }

    @Test
    void testPubChemCompoundASN() throws Exception {
        expectFormat("org/openscience/cdk/io/cid1.asn", PubChemASNFormat.getInstance());
    }

    @Test
    void testPubChemSubstancesASN() throws Exception {
        expectFormat("org/openscience/cdk/io/list.asn", PubChemSubstancesASNFormat.getInstance());
    }

    @Test
    void testPubChemCompoundsXML() throws Exception {
        expectFormat("org/openscience/cdk/io/aceticAcids38.xml", PubChemCompoundsXMLFormat.getInstance());
    }

    @Test
    void testPubChemSubstancesXML() throws Exception {
        expectFormat("org/openscience/cdk/io/taxols.xml", PubChemSubstancesXMLFormat.getInstance());
    }

    @Test
    void testPubChemSubstanceXML() throws Exception {
        expectFormat("org/openscience/cdk/io/sid577309.xml", PubChemSubstanceXMLFormat.getInstance());
    }

    @Test
    void testPubChemCompoundXML() throws Exception {
        expectFormat("org/openscience/cdk/io/cid1145.xml", PubChemCompoundXMLFormat.getInstance());
    }

    private void expectFormat(String filename, IResourceFormat expectedFormat) throws Exception {
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        Assertions.assertNotNull(ins, "Cannot find file: " + filename);
        if (expectedFormat instanceof IChemFormatMatcher) {
            factory.registerFormat((IChemFormatMatcher) expectedFormat);
        }
        ins = new BufferedInputStream(ins);
        IChemFormat format = factory.guessFormat(ins);
        Assertions.assertNotNull(format);
        Assertions.assertEquals(expectedFormat.getFormatName(), format.getFormatName());
    }

    /**
     * @cdk.bug 2153298
     */
    @Test
    void testGuessFormat() throws Exception {
        String filename = "org/openscience/cdk/io/bf3.xyz";
        InputStream input = this.getClass().getClassLoader().getResourceAsStream(filename);
        input = new BufferedInputStream(input);
        IChemFormat format = factory.guessFormat(input);
        Assertions.assertNotNull(format);
        // make sure the InputStream is properly reset
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line = reader.readLine();
        Assertions.assertNotNull(line);
        Assertions.assertEquals("4", line);
        line = reader.readLine();
        Assertions.assertNotNull(line);
        Assertions.assertEquals("Bortrifluorid", line);
    }

    @Test
    void testGuessFormat_Gz() throws Exception {
        String filename = "org/openscience/cdk/io/bf3.xyz.gz";
        InputStream input = new BufferedInputStream(new GZIPInputStream(this.getClass().getClassLoader()
                .getResourceAsStream(filename)));
        IChemFormat format = factory.guessFormat(input);
        Assertions.assertNotNull(format);
        // make sure the InputStream is properly reset
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line = reader.readLine();
        Assertions.assertNotNull(line);
        Assertions.assertEquals("4", line);
        line = reader.readLine();
        Assertions.assertNotNull(line);
        Assertions.assertEquals("Bortrifluorid", line);
    }

    @Test
    void testGuessFormat_Reader() throws Exception {
        String filename = "org/openscience/cdk/io/bf3.xyz";
        InputStream input = this.getClass().getClassLoader().getResourceAsStream(filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        IChemFormat format = factory.guessFormat(reader);
        Assertions.assertNotNull(format);
        // make sure the Reader is properly reset
        String line = reader.readLine();
        Assertions.assertNotNull(line);
        Assertions.assertEquals("4", line);
        line = reader.readLine();
        Assertions.assertNotNull(line);
        Assertions.assertEquals("Bortrifluorid", line);
    }

    @Test
    void testGetFormats() {
        List<IChemFormatMatcher> formats = factory.getFormats();
        Assertions.assertNotNull(formats);
        Assertions.assertNotSame(0, formats.size());
        for (IChemFormatMatcher matcher : formats) {
            Assertions.assertNotNull(matcher);
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
    void testRegisterFormat() throws IOException {
        factory.registerFormat(new DummyFormat());
        StringReader reader = new StringReader("DummyFormat:");
        IChemFormat format = factory.guessFormat(reader);
        Assertions.assertNotNull(format);
        Assertions.assertTrue(format instanceof DummyFormat);
    }
}
