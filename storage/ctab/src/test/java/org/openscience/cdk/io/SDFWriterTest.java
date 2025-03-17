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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Properties;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.listener.PropertiesListener;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.InvPair;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.test.io.ChemObjectWriterTest;
import org.openscience.cdk.tools.LoggingToolFactory;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openscience.cdk.interfaces.IChemObject.AROMATIC;

/**
 * TestCase for the writer MDL SD file writer.
 *
 *
 * @see org.openscience.cdk.io.SDFWriter
 */
class SDFWriterTest extends ChemObjectWriterTest {

    private static IChemObjectBuilder builder;

    @BeforeAll
    static void setup() {
        builder = DefaultChemObjectBuilder.getInstance();
        setChemObjectWriter(new SDFWriter());
    }

    @Test
    void testAccepts() throws Exception {
        SDFWriter reader = new SDFWriter();
        Assertions.assertTrue(reader.accepts(ChemFile.class));
        Assertions.assertTrue(reader.accepts(ChemModel.class));
        Assertions.assertTrue(reader.accepts(AtomContainerSet.class));
    }

    @Test
    void testWrite_IAtomContainerSet_Properties_Off() throws Exception {
        StringWriter writer = new StringWriter();
        IAtomContainerSet molSet = new AtomContainerSet();
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        molecule.addAtom(new Atom("C"));
        molecule.setProperty("foo", "bar");
        molSet.addAtomContainer(molecule);

        SDFWriter sdfWriter = new SDFWriter(writer);
        Properties sdfWriterProps = new Properties();
        sdfWriterProps.put("writeProperties", "false");
        sdfWriter.addChemObjectIOListener(new PropertiesListener(sdfWriterProps));
        sdfWriter.customizeJob();
        sdfWriter.write(molSet);
        sdfWriter.close();
        String result = writer.toString();
        Assertions.assertFalse(result.contains("<foo>"));
    }

    /**
     * @cdk.bug 2827745
     */
    @Test
    void testWrite_IAtomContainerSet() throws Exception {
        StringWriter writer = new StringWriter();
        IAtomContainerSet molSet = builder.newInstance(IAtomContainerSet.class);
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molSet.addAtomContainer(molecule);

        SDFWriter sdfWriter = new SDFWriter(writer);
        sdfWriter.write(molSet);
        sdfWriter.close();
        Assertions.assertNotSame(0, writer.toString().length());
    }

    @Test
    void testWrite_IAtomContainerSet_Properties() throws Exception {
        StringWriter writer = new StringWriter();
        IAtomContainerSet molSet = new AtomContainerSet();
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        molecule.addAtom(new Atom("C"));
        molecule.setProperty("foo", "bar");
        molSet.addAtomContainer(molecule);

        SDFWriter sdfWriter = new SDFWriter(writer);
        sdfWriter.write(molSet);
        sdfWriter.close();
        Assertions.assertTrue(writer.toString().contains("<foo>"));
        Assertions.assertTrue(writer.toString().contains("bar"));
    }

    @Test
    void testWrite_IAtomContainerSet_CDKProperties() throws Exception {
        StringWriter writer = new StringWriter();
        IAtomContainerSet molSet = new AtomContainerSet();
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        molecule.addAtom(new Atom("C"));
        molecule.setProperty(InvPair.CANONICAL_LABEL, "bar");
        molSet.addAtomContainer(molecule);

        SDFWriter sdfWriter = new SDFWriter(writer);
        sdfWriter.write(molSet);
        sdfWriter.close();
        Assertions.assertTrue(!writer.toString().contains(InvPair.CANONICAL_LABEL));
    }

    @Test
    void testWrite_IAtomContainerSet_SingleMolecule() throws Exception {
        StringWriter writer = new StringWriter();
        IAtomContainerSet molSet = new AtomContainerSet();
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        molecule.addAtom(new Atom("C"));
        molSet.addAtomContainer(molecule);

        SDFWriter sdfWriter = new SDFWriter(writer);
        sdfWriter.write(molSet);
        sdfWriter.close();
        Assertions.assertTrue(writer.toString().contains("$$$$"));
    }

    @Test
    void testWrite_IAtomContainerSet_MultIAtomContainer() throws Exception {
        StringWriter writer = new StringWriter();
        IAtomContainerSet molSet = new AtomContainerSet();
        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        molecule.addAtom(new Atom("C"));
        molSet.addAtomContainer(molecule);
        molecule = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        molecule.addAtom(new Atom("C"));
        molSet.addAtomContainer(molecule);

        SDFWriter sdfWriter = new SDFWriter(writer);
        sdfWriter.write(molSet);
        sdfWriter.close();
        Assertions.assertTrue(writer.toString().contains("$$$$"));
    }

    @Test
    void testWrite_IAtomContainer_MultIAtomContainer() throws Exception {
        StringWriter writer = new StringWriter();
        SDFWriter sdfWriter = new SDFWriter(writer);

        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        molecule.addAtom(new Atom("C"));
        molecule.setProperty("foo", "bar");
        sdfWriter.write(molecule);

        molecule = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        molecule.addAtom(new Atom("C"));
        molecule.setProperty("toys", "r-us");
        sdfWriter.write(molecule);

        sdfWriter.close();
        Assertions.assertTrue(writer.toString().contains("foo"));
        Assertions.assertTrue(writer.toString().contains("bar"));
        Assertions.assertTrue(writer.toString().contains("toys"));
        Assertions.assertTrue(writer.toString().contains("r-us"));
        Assertions.assertTrue(writer.toString().contains("$$$$"));
    }

    @Test
    void invalidSDfileHeaderTags() throws Exception {
        StringWriter writer = new StringWriter();
        SDFWriter sdfWriter = new SDFWriter(writer);

        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        molecule.addAtom(new Atom("C"));
        molecule.setProperty("http://not-valid.com", "URL");
        sdfWriter.write(molecule);

        sdfWriter.close();
        org.hamcrest.MatcherAssert.assertThat(writer.toString(), Matchers.containsString("> <http://not_valid_com>"));
    }

    @Test
    void chooseFormatToWrite() throws Exception {
        StringWriter writer = new StringWriter();
        SDFWriter sdfWriter = new SDFWriter(writer);

        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        molecule.addAtom(new Atom("CH4"));
        sdfWriter.write(molecule);

        molecule = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        for (int i = 0; i < 1000; i++)
            molecule.addAtom(new Atom("CH4"));
        sdfWriter.write(molecule);

        molecule = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        molecule.addAtom(new Atom("CH4"));
        sdfWriter.write(molecule);

        sdfWriter.close();
        String result = writer.toString();
        assertThat(result, Matchers.containsString("V2000"));
        assertThat(result, Matchers.containsString("V3000"));
    }

    @Test
    void chooseFormatToWrite2() throws Exception {
        StringWriter writer = new StringWriter();
        SDFWriter sdfWriter = new SDFWriter(writer);
        sdfWriter.setAlwaysV3000(true);

        IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        molecule.addAtom(new Atom("CH4"));
        sdfWriter.write(molecule);

        molecule = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        for (int i = 0; i < 1000; i++)
            molecule.addAtom(new Atom("CH4"));
        sdfWriter.write(molecule);

        molecule = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        molecule.addAtom(new Atom("CH4"));
        sdfWriter.write(molecule);

        sdfWriter.close();
        String result = writer.toString();
        assertThat(result, Matchers.not(Matchers.containsString("V2000")));
        assertThat(result, Matchers.containsString("V3000"));
    }

    /**
     * @cdk.bug 3392485
     */
    @Test
    void testIOPropPropagation() throws Exception {
        IAtomContainer mol = TestMoleculeFactory.makeBenzene();
        for (IAtom atom : mol.atoms()) {
            atom.setFlag(AROMATIC, true);
        }
        for (IBond bond : mol.bonds()) {
            bond.setFlag(AROMATIC, true);
        }

        StringWriter strWriter = new StringWriter();
        SDFWriter writer = new SDFWriter(strWriter);

        Properties sdfWriterProps = new Properties();
        sdfWriterProps.put("WriteAromaticBondTypes", "true");
        writer.addChemObjectIOListener(new PropertiesListener(sdfWriterProps));
        writer.customizeJob();
        writer.write(mol);
        writer.close();

        String output = strWriter.toString();
        Assertions.assertTrue(output.contains("4  0  0  0  0"));
    }

    @Test
    void testPropertyOutput_All() throws CDKException, IOException {
        IAtomContainer adenine = TestMoleculeFactory.makeAdenine();
        StringWriter sw = new StringWriter();
        SDFWriter sdf = new SDFWriter(sw);
        adenine.setProperty("one", "a");
        adenine.setProperty("two", "b");
        sdf.write(adenine);
        sdf.close();
        String out = sw.toString();
        Assertions.assertTrue(out.contains("> <one>"));
        Assertions.assertTrue(out.contains("> <two>"));
    }

    @Test
    void testPropertyOutput_one() throws CDKException, IOException {
        IAtomContainer adenine = TestMoleculeFactory.makeAdenine();
        StringWriter sw = new StringWriter();
        SDFWriter sdf = new SDFWriter(sw, Collections.singleton("one"));
        adenine.setProperty("one", "a");
        adenine.setProperty("two", "b");
        sdf.write(adenine);
        sdf.close();
        String out = sw.toString();
        Assertions.assertTrue(out.contains("> <one>"));
        Assertions.assertFalse(out.contains("> <two>"));
    }

    @Test
    void testPropertyOutput_two() throws CDKException, IOException {
        IAtomContainer adenine = TestMoleculeFactory.makeAdenine();
        StringWriter sw = new StringWriter();
        SDFWriter sdf = new SDFWriter(sw, Collections.singleton("two"));
        adenine.setProperty("one", "a");
        adenine.setProperty("two", "b");
        sdf.write(adenine);
        sdf.close();
        String out = sw.toString();
        Assertions.assertTrue(out.contains("> <two>"));
        Assertions.assertFalse(out.contains("> <one>"));
    }

    @Test
    void testPropertyOutput_none() throws CDKException, IOException {
        IAtomContainer adenine = TestMoleculeFactory.makeAdenine();
        StringWriter sw = new StringWriter();
        SDFWriter sdf = new SDFWriter(sw, Collections.emptySet());
        adenine.setProperty("one", "a");
        adenine.setProperty("two", "b");
        sdf.write(adenine);
        sdf.close();
        String out = sw.toString();
        Assertions.assertFalse(out.contains("> <two>"));
        Assertions.assertFalse(out.contains("> <one>"));
    }

    @Test
    void testPropertyOutput_noneOption() throws CDKException, IOException {
        IAtomContainer adenine = TestMoleculeFactory.makeAdenine();
        StringWriter sw = new StringWriter();
        SDFWriter sdf = new SDFWriter(sw);
        sdf.getSetting(SDFWriter.OptWriteData).setSetting("false");
        adenine.setProperty("one", "a");
        adenine.setProperty("two", "b");
        sdf.write(adenine);
        sdf.close();
        String out = sw.toString();
        Assertions.assertFalse(out.contains("> <two>"));
        Assertions.assertFalse(out.contains("> <one>"));
    }

    @Test
    void testPropertyOutput_V3000() throws CDKException, IOException {
        IAtomContainer adenine = TestMoleculeFactory.makeAdenine();
        StringWriter sw = new StringWriter();
        SDFWriter sdf = new SDFWriter(sw);
        sdf.getSetting(SDFWriter.OptAlwaysV3000).setSetting("true");
        sdf.getSetting(SDFWriter.OptWriteData).setSetting("true");
        adenine.setProperty("one", "a");
        adenine.setProperty("two", "b");
        sdf.write(adenine);
        sdf.close();
        String out = sw.toString();
        Assertions.assertTrue(out.contains("> <two>"));
        Assertions.assertTrue(out.contains("> <one>"));
    }

    @Test
    void testPropertyOutput_V3000_acceptTags() throws CDKException, IOException {
        IAtomContainer adenine = TestMoleculeFactory.makeAdenine();
        StringWriter sw = new StringWriter();
        SDFWriter sdf = new SDFWriter(sw, Collections.emptySet());
        sdf.getSetting(SDFWriter.OptAlwaysV3000).setSetting("true");
        sdf.getSetting(SDFWriter.OptWriteData).setSetting("true");
        adenine.setProperty("one", "a");
        adenine.setProperty("two", "b");
        sdf.write(adenine);
        sdf.close();
        String out = sw.toString();
        Assertions.assertFalse(out.contains("> <two>"));
        Assertions.assertFalse(out.contains("> <one>"));
    }

    @Test
    void testPropertyOutput_V3000_acceptTag() throws CDKException, IOException {
        IAtomContainer adenine = TestMoleculeFactory.makeAdenine();
        StringWriter sw = new StringWriter();
        SDFWriter sdf = new SDFWriter(sw, Collections.singleton("one"));
        sdf.getSetting(SDFWriter.OptAlwaysV3000).setSetting("true");
        sdf.getSetting(SDFWriter.OptWriteData).setSetting("true");
        adenine.setProperty("one", "a");
        adenine.setProperty("two", "b");
        sdf.write(adenine);
        sdf.close();
        String out = sw.toString();
        Assertions.assertFalse(out.contains("> <two>"));
        Assertions.assertTrue(out.contains("> <one>"));
    }

    @Test
    void setProgramName() {
        StringWriter sw = new StringWriter();
        try (SDFWriter sdfw = new SDFWriter(sw)) {
            sdfw.getSetting(MDLV2000Writer.OptWriteDefaultProperties)
                .setSetting("false");
            sdfw.getSetting(MDLV2000Writer.OptProgramName)
                .setSetting("Bioclipse");

            sdfw.write(TestMoleculeFactory.make123Triazole());

            sdfw.getSetting(SDFWriter.OptAlwaysV3000)
                .setSetting("true");

            sdfw.write(TestMoleculeFactory.make123Triazole());
        } catch (IOException | CDKException e) {
            LoggingToolFactory.createLoggingTool(SDFWriterTest.class)
                              .warn("Unexpected Error:", e);
        }
        String sdf = sw.toString();
        for (String mol : sdf.split("\\$\\$\\$\\$", 2)) {
            assertThat(mol, CoreMatchers.containsString("Bioclip"));
        }
    }

    @Test
    void optionallyTruncateLongProperties() {
        StringWriter sw = new StringWriter();
        try (SDFWriter sdfw = new SDFWriter(sw)) {
            sdfw.getSetting(MDLV2000Writer.OptWriteDefaultProperties)
                .setSetting("false");
            sdfw.getSetting(SDFWriter.OptTruncateLongData)
                .setSetting("true");
            IAtomContainer mol = TestMoleculeFactory.make123Triazole();
            mol.setProperty("MyLongField",
                            "ThisIsAVeryLongFieldThatShouldBeWrapped" +
                            "ThisIsAVeryLongFieldThatShouldBeWrapped" +
                            "ThisIsAVeryLongFieldThatShouldBeWrapped" +
                            "ThisIsAVeryLongFieldThatShouldBeWrapped" +
                            "ThisIsAVeryLongFieldThatShouldBeWrapped" +
                            "ThisIsAVeryLongFieldThatShouldBeWrapped" +
                            "ThisIsAVeryLongFieldThatShouldBeWrapped" +
                            "ThisIsAVeryLongFieldThatShouldBeWrapped" +
                            "ThisIsAVeryLongFieldThatShouldBeWrapped" +
                            "ThisIsAVeryLongFieldThatShouldBeWrapped");
            sdfw.write(mol);
        } catch (IOException | CDKException e) {
            LoggingToolFactory.createLoggingTool(SDFWriterTest.class)
                              .warn("Unexpected Error:", e);
        }
        String sdf = sw.toString();
        assertThat(sdf,
                   CoreMatchers.containsString("ThisIsAVeryLongFieldThatShouldBeWrappedThisIsAVeryLongFieldThatShouldBeWrappedThisIsAVeryLongFieldThatShouldBeWrappedThisIsAVeryLongFieldThatShouldBeWrappedThisIsAVeryLongFieldThatShouldBeWrappedThisI\n"));
    }


    @Test
    void testNoChiralFlag() throws Exception {
        final String input = "\n" +
                "  Mrv1810 02052112362D          \n" +
                "\n" +
                "  0  0  0     0  0            999 V3000\n" +
                "M  V30 BEGIN CTAB\n" +
                "M  V30 COUNTS 7 7 0 0 0\n" +
                "M  V30 BEGIN ATOM\n" +
                "M  V30 1 C -2.1407 12.3148 0 0 CFG=2\n" +
                "M  V30 2 C -3.4743 11.5447 0 0\n" +
                "M  V30 3 C -3.4743 10.0047 0 0\n" +
                "M  V30 4 C -2.1407 9.2347 0 0\n" +
                "M  V30 5 C -0.807 10.0047 0 0\n" +
                "M  V30 6 N -0.807 11.5447 0 0\n" +
                "M  V30 7 O -2.1407 13.8548 0 0\n" +
                "M  V30 END ATOM\n" +
                "M  V30 BEGIN BOND\n" +
                "M  V30 1 1 1 2\n" +
                "M  V30 2 1 2 3\n" +
                "M  V30 3 1 3 4\n" +
                "M  V30 4 1 4 5\n" +
                "M  V30 5 1 5 6\n" +
                "M  V30 6 1 1 6\n" +
                "M  V30 7 1 1 7 CFG=1\n" +
                "M  V30 END BOND\n" +
                "M  V30 END CTAB\n" +
                "M  END\n";
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        StringWriter sw = new StringWriter();
        try (MDLV3000Reader mdlr = new MDLV3000Reader(new StringReader(input));
             SDFWriter mdlw = new SDFWriter(sw)) {
            mdlw.write(mdlr.read(bldr.newAtomContainer()));
        }
        assertThat(sw.toString(), containsString("  7  7  0  0  0  0  0  0  0  0999 V2000"));
        assertThat(sw.toString(), not(containsString("BEGIN COLLECTION\n" +
                "M  V30 MDLV30/STERAC1 ATOMS=(1)\n" +
                "END COLLECTION")));
    }

    @Test
    void testChiralFlag() throws Exception {
        final String input = "\n" +
                "  Mrv1810 02052112362D          \n" +
                "\n" +
                "  0  0  0     0  0            999 V3000\n" +
                "M  V30 BEGIN CTAB\n" +
                "M  V30 COUNTS 7 7 0 0 1\n" +
                "M  V30 BEGIN ATOM\n" +
                "M  V30 1 C -2.1407 12.3148 0 0 CFG=2\n" +
                "M  V30 2 C -3.4743 11.5447 0 0\n" +
                "M  V30 3 C -3.4743 10.0047 0 0\n" +
                "M  V30 4 C -2.1407 9.2347 0 0\n" +
                "M  V30 5 C -0.807 10.0047 0 0\n" +
                "M  V30 6 N -0.807 11.5447 0 0\n" +
                "M  V30 7 O -2.1407 13.8548 0 0\n" +
                "M  V30 END ATOM\n" +
                "M  V30 BEGIN BOND\n" +
                "M  V30 1 1 1 2\n" +
                "M  V30 2 1 2 3\n" +
                "M  V30 3 1 3 4\n" +
                "M  V30 4 1 4 5\n" +
                "M  V30 5 1 5 6\n" +
                "M  V30 6 1 1 6\n" +
                "M  V30 7 1 1 7 CFG=1\n" +
                "M  V30 END BOND\n" +
                "M  V30 END CTAB\n" +
                "M  END\n";
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        StringWriter sw = new StringWriter();
        try (MDLV3000Reader mdlr = new MDLV3000Reader(new StringReader(input));
             SDFWriter mdlw = new SDFWriter(sw)) {
            mdlw.write(mdlr.read(bldr.newAtomContainer()));
        }
        assertThat(sw.toString(), containsString("7  7  0  0  1  0  0  0  0  0999 V2000"));
    }

    @Test
    void testStereoRac1() throws Exception {
        final String input = "\n" +
                "  Mrv1810 02052113162D          \n" +
                "\n" +
                "  0  0  0     0  0            999 V3000\n" +
                "M  V30 BEGIN CTAB\n" +
                "M  V30 COUNTS 7 7 0 0 0\n" +
                "M  V30 BEGIN ATOM\n" +
                "M  V30 1 C -2.1407 12.3148 0 0 CFG=2\n" +
                "M  V30 2 C -3.4743 11.5447 0 0\n" +
                "M  V30 3 C -3.4743 10.0047 0 0\n" +
                "M  V30 4 C -2.1407 9.2347 0 0\n" +
                "M  V30 5 C -0.807 10.0047 0 0\n" +
                "M  V30 6 N -0.807 11.5447 0 0\n" +
                "M  V30 7 O -2.1407 13.8548 0 0\n" +
                "M  V30 END ATOM\n" +
                "M  V30 BEGIN BOND\n" +
                "M  V30 1 1 1 2\n" +
                "M  V30 2 1 2 3\n" +
                "M  V30 3 1 3 4\n" +
                "M  V30 4 1 4 5\n" +
                "M  V30 5 1 5 6\n" +
                "M  V30 6 1 1 6\n" +
                "M  V30 7 1 1 7 CFG=1\n" +
                "M  V30 END BOND\n" +
                "M  V30 BEGIN COLLECTION\n" +
                "M  V30 MDLV30/STERAC1 ATOMS=(1 1)\n" +
                "M  V30 END COLLECTION\n" +
                "M  V30 END CTAB\n" +
                "M  END";
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        StringWriter sw = new StringWriter();
        try (MDLV3000Reader mdlr = new MDLV3000Reader(new StringReader(input));
             SDFWriter mdlw = new SDFWriter(sw)) {
            mdlw.write(mdlr.read(bldr.newAtomContainer()));
        }
        assertThat(sw.toString(), containsString("  7  7  0  0  0  0  0  0  0  0999 V2000"));
    }

    @Test
    void testStereoRel1() throws Exception {
        final String input = "\n" +
                "  Mrv1810 02052113162D          \n" +
                "\n" +
                "  0  0  0     0  0            999 V3000\n" +
                "M  V30 BEGIN CTAB\n" +
                "M  V30 COUNTS 7 7 0 0 0\n" +
                "M  V30 BEGIN ATOM\n" +
                "M  V30 1 C -2.1407 12.3148 0 0 CFG=2\n" +
                "M  V30 2 C -3.4743 11.5447 0 0\n" +
                "M  V30 3 C -3.4743 10.0047 0 0\n" +
                "M  V30 4 C -2.1407 9.2347 0 0\n" +
                "M  V30 5 C -0.807 10.0047 0 0\n" +
                "M  V30 6 N -0.807 11.5447 0 0\n" +
                "M  V30 7 O -2.1407 13.8548 0 0\n" +
                "M  V30 END ATOM\n" +
                "M  V30 BEGIN BOND\n" +
                "M  V30 1 1 1 2\n" +
                "M  V30 2 1 2 3\n" +
                "M  V30 3 1 3 4\n" +
                "M  V30 4 1 4 5\n" +
                "M  V30 5 1 5 6\n" +
                "M  V30 6 1 1 6\n" +
                "M  V30 7 1 1 7 CFG=1\n" +
                "M  V30 END BOND\n" +
                "M  V30 BEGIN COLLECTION\n" +
                "M  V30 MDLV30/STEREL5 ATOMS=(1 1)\n" +
                "M  V30 END COLLECTION\n" +
                "M  V30 END CTAB\n" +
                "M  END";
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        StringWriter sw = new StringWriter();
        try (MDLV3000Reader mdlr = new MDLV3000Reader(new StringReader(input));
             SDFWriter mdlw = new SDFWriter(sw)) {
            mdlw.write(mdlr.read(bldr.newAtomContainer()));
        }
        assertThat(sw.toString(), containsString("M  V30 BEGIN COLLECTION\n" +
                "M  V30 MDLV30/STEREL1 ATOMS=(1)\n" +
                "M  V30 END COLLECTION"));
    }

    @Test
    void testStereoRac1And() throws Exception {
        final String input = "\n" +
                "  Mrv1810 02062121432D          \n" +
                "\n" +
                "  0  0  0     0  0            999 V3000\n" +
                "M  V30 BEGIN CTAB\n" +
                "M  V30 COUNTS 11 11 0 0 1\n" +
                "M  V30 BEGIN ATOM\n" +
                "M  V30 1 C 0 6.16 0 0\n" +
                "M  V30 2 C 0 4.62 0 0 CFG=2\n" +
                "M  V30 3 O -1.3337 3.85 0 0\n" +
                "M  V30 4 C 1.3337 3.85 0 0 CFG=2\n" +
                "M  V30 5 O 2.6674 4.62 0 0\n" +
                "M  V30 6 C 1.3337 2.31 0 0\n" +
                "M  V30 7 C 2.6674 1.54 0 0\n" +
                "M  V30 8 C 2.6674 -0 0 0\n" +
                "M  V30 9 C 1.3337 -0.77 0 0\n" +
                "M  V30 10 C 0 0 0 0\n" +
                "M  V30 11 C 0 1.54 0 0\n" +
                "M  V30 END ATOM\n" +
                "M  V30 BEGIN BOND\n" +
                "M  V30 1 1 2 1\n" +
                "M  V30 2 1 2 3 CFG=1\n" +
                "M  V30 3 1 2 4\n" +
                "M  V30 4 1 4 5 CFG=3\n" +
                "M  V30 5 1 4 6\n" +
                "M  V30 6 1 6 7\n" +
                "M  V30 7 1 7 8\n" +
                "M  V30 8 1 8 9\n" +
                "M  V30 9 1 9 10\n" +
                "M  V30 10 1 10 11\n" +
                "M  V30 11 1 6 11\n" +
                "M  V30 END BOND\n" +
                "M  V30 BEGIN COLLECTION\n" +
                "M  V30 MDLV30/STEABS ATOMS=(1 4)\n" +
                "M  V30 MDLV30/STERAC1 ATOMS=(1 2)\n" +
                "M  V30 END COLLECTION\n" +
                "M  V30 END CTAB\n" +
                "M  END\n";
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        StringWriter sw = new StringWriter();
        try (MDLV3000Reader mdlr = new MDLV3000Reader(new StringReader(input));
             SDFWriter mdlw = new SDFWriter(sw)) {
            mdlw.write(mdlr.read(bldr.newAtomContainer()));
        }
        assertThat(sw.toString(), containsString("M  V30 COUNTS 11 11 0 0 0"));
        assertThat(sw.toString(), containsString("M  V30 BEGIN COLLECTION\n" +
                "M  V30 MDLV30/STEABS ATOMS=(4)\n" +
                "M  V30 MDLV30/STERAC1 ATOMS=(2)\n" +
                "M  V30 END COLLECTION"));
    }
}
