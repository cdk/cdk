/* Copyright (C) 2011  Egon Willighagen <egonw@users.sf.net>
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;
import org.openscience.cdk.tools.periodictable.PeriodicTable;

/**
 * TestCase for reading CML files.
 *
 * @cdk.module test-io
 */
public class CMLReaderTest extends SimpleChemObjectReaderTest {

    @BeforeClass
    public static void setup() {
        setSimpleChemObjectReader(new CMLReader(), "data/cml/3.cml");
    }

    @Test
    public void testAccepts() {
        Assert.assertTrue(chemObjectIO.accepts(ChemFile.class));
    }

    @Test(expected = CDKException.class)
    @Override
    public void testSetReader_Reader() throws Exception {
        InputStream ins = ChemObjectReaderTest.class.getClassLoader().getResourceAsStream(testFile);
        chemObjectIO.setReader(new InputStreamReader(ins));
    }

    /**
     * Ensure stereoBond content is read if the usual "dictRef" attribute is not
     * supplied
     *
     * @cdk.bug 1248
     */
    @Test
    public void testBug1248() throws IOException, CDKException {

        InputStream in = getClass().getResourceAsStream("/data/cml/(1R)-1-aminoethan-1-ol.cml");
        CMLReader reader = new CMLReader(in);
        try {
            IChemFile cfile = reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IChemFile.class));

            Assert.assertNotNull("ChemFile was Null", cfile);

            List<IAtomContainer> containers = ChemFileManipulator.getAllAtomContainers(cfile);

            Assert.assertEquals("Expected a single atom container", 1, containers.size());

            IAtomContainer container = containers.get(0);

            Assert.assertNotNull("Null atom container read", container);

            IBond bond = container.getBond(2);

            Assert.assertNotNull("Null bond", bond);

            Assert.assertEquals("Expected Wedge (Up) Bond", IBond.Stereo.UP, bond.getStereo());

        } finally {
            reader.close();
        }

    }

    /**
     * Ensure correct atomic numbers are read and does not default to 1
     *
     * @cdk.bug 1245
     */
    @Test
    public void testBug1245() throws IOException, CDKException {

        InputStream in = getClass().getResourceAsStream("/data/cml/(1R)-1-aminoethan-1-ol.cml");
        CMLReader reader = new CMLReader(in);
        try {
            IChemFile cfile = reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IChemFile.class));

            Assert.assertNotNull("ChemFile was Null", cfile);

            List<IAtomContainer> containers = ChemFileManipulator.getAllAtomContainers(cfile);

            Assert.assertEquals("Expected a single atom container", 1, containers.size());

            IAtomContainer container = containers.get(0);

            Assert.assertNotNull("Null atom container read", container);

            for (IAtom atom : container.atoms()) {
                Assert.assertEquals("Incorrect atomic number", PeriodicTable.getAtomicNumber(atom.getSymbol()),
                        atom.getAtomicNumber());
            }

        } finally {
            reader.close();
        }
    }

    /**
     * Ensures that when multiple stereo is set the dictRef is favoured
     * and the charContent is not used. Here is an example of what we expect
     * to read.
     *
     * <pre>{@code
     * <bond atomRefs2="a1 a4" order="1">
     *     <bondStereo dictRef="cml:W"/> <!-- should be W -->
     * </bond>
     *
     * <bond atomRefs2="a1 a4" order="1">
     *     <bondStereo>W</bondStereo> <!-- should be W -->
     * </bond>
     *
     * <bond atomRefs2="a1 a4" order="1">
     *    <bondStereo dictRef="cml:W">W</bondStereo> <!-- should be W -->
     * </bond>
     *
     * <bond atomRefs2="a1 a4" order="1">
     *    <bondStereo dictRef="cml:W">H</bondStereo> <!-- should be W -->
     * </bond>
     * }</pre>
     *
     * @cdk.bug 1274
     * @see #testBug1248()
     */
    @Test
    public void testBug1274() throws CDKException, IOException {

        InputStream in = getClass().getResourceAsStream("/data/cml/(1R)-1-aminoethan-1-ol-multipleBondStereo.cml");
        CMLReader reader = new CMLReader(in);
        try {
            IChemFile cfile = reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IChemFile.class));

            Assert.assertNotNull("ChemFile was null", cfile);

            List<IAtomContainer> containers = ChemFileManipulator.getAllAtomContainers(cfile);

            Assert.assertEquals("expected a single atom container", 1, containers.size());

            IAtomContainer container = containers.get(0);

            Assert.assertNotNull("null atom container read", container);

            // we check here that the charContent is not used and also that more then
            // one stereo isn't set
            Assert.assertEquals("expected non-stereo bond", IBond.Stereo.NONE, container.getBond(0).getStereo());
            Assert.assertEquals("expected Hatch (Down) Bond", IBond.Stereo.DOWN, container.getBond(1).getStereo());
            Assert.assertEquals("expected non-stereo bond", IBond.Stereo.NONE, container.getBond(2).getStereo());

        } finally {
            reader.close();
        }
    }

    /**
     * Ensures that {@code <bondStereo dictRef="cml:"/>} doesn't cause an exception
     *
     * @cdk.bug 1275
     */
    @Test
    public void testBug1275() throws CDKException, IOException {

        InputStream in = getClass().getResourceAsStream("/data/cml/(1R)-1-aminoethan-1-ol-malformedDictRef.cml");
        CMLReader reader = new CMLReader(in);
        try {
            IChemFile cfile = reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IChemFile.class));

            Assert.assertNotNull("ChemFile was null", cfile);

            List<IAtomContainer> containers = ChemFileManipulator.getAllAtomContainers(cfile);

            Assert.assertEquals("expected a single atom container", 1, containers.size());

            IAtomContainer container = containers.get(0);

            Assert.assertNotNull("null atom container read", container);

            // we check here that the malformed dictRef doesn't throw an exception
            Assert.assertEquals("expected non-stereo bond", IBond.Stereo.NONE, container.getBond(0).getStereo());
            Assert.assertEquals("expected Wedge (Up) Bond", IBond.Stereo.UP, container.getBond(1).getStereo());
            Assert.assertEquals("expected non-stereo bond", IBond.Stereo.NONE, container.getBond(2).getStereo());

        } finally {
            reader.close();
        }

    }

    @Test
    public void testWedgeBondParsing() throws CDKException, IOException {
        InputStream in = getClass().getResourceAsStream("/data/cml/AZD5423.xml");
        CMLReader reader = new CMLReader(in);
        try {
            IChemFile cfile = reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IChemFile.class));
            Assert.assertNotNull("ChemFile was null", cfile);
            List<IAtomContainer> containers = ChemFileManipulator.getAllAtomContainers(cfile);
            Assert.assertEquals("expected a single atom container", 1, containers.size());
            IAtomContainer container = containers.get(0);
            Assert.assertNotNull("null atom container read", container);

            // we check here that the malformed dictRef doesn't throw an exception
            for (int i = 0; i < 19; i++) {
                Assert.assertEquals(
                        "found an unexpected wedge bond for " + i + ": " + container.getBond(i).getStereo(),
                        IBond.Stereo.NONE, container.getBond(i).getStereo());
            }
            Assert.assertEquals("expected a wedge bond", IBond.Stereo.DOWN, container.getBond(19).getStereo());
            for (int i = 20; i < 30; i++) {
                Assert.assertEquals(
                        "found an unexpected wedge bond for " + i + ": " + container.getBond(i).getStereo(),
                        IBond.Stereo.NONE, container.getBond(i).getStereo());
            }
            Assert.assertEquals("expected a wedge bond", IBond.Stereo.UP, container.getBond(30).getStereo());
            for (int i = 31; i <= 37; i++) {
                Assert.assertEquals(
                        "found an unexpected wedge bond for " + i + ": " + container.getBond(i).getStereo(),
                        IBond.Stereo.NONE, container.getBond(i).getStereo());
            }
        } finally {
            reader.close();
        }
    }

    @Test
    public void testSFBug1085912_1() throws Exception {
        String cmlContent = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>"
                + "<molecule convention=\"PDB\" dictRef=\"pdb:model\" xmlns=\"http://www.xml-cml.org/schema\">"
                + "  <molecule dictRef=\"pdb:sequence\" id=\"ALAA116\">"
                + "    <atomArray>"
                + "      <atom id=\"a9794931\" elementType=\"N\" x3=\"-10.311\" y3=\"2.77\" z3=\"-9.837\" formalCharge=\"0\">"
                + "        <scalar dictRef=\"cdk:partialCharge\" dataType=\"xsd:double\">0.0</scalar>"
                + "      </atom>"
                + "      <atom id=\"a5369354\" elementType=\"C\" x3=\"-9.75\" y3=\"4.026\" z3=\"-9.35\" formalCharge=\"0\">"
                + "        <scalar dictRef=\"cdk:partialCharge\" dataType=\"xsd:double\">0.0</scalar>"
                + "      </atom>"
                + "      <atom id=\"a14877152\" elementType=\"C\" x3=\"-10.818\" y3=\"5.095\" z3=\"-9.151\" formalCharge=\"0\">"
                + "        <scalar dictRef=\"cdk:partialCharge\" dataType=\"xsd:double\">0.0</scalar>"
                + "      </atom>"
                + "      <atom id=\"a26221736\" elementType=\"O\" x3=\"-11.558\" y3=\"5.433\" z3=\"-10.074\" formalCharge=\"0\">"
                + "        <scalar dictRef=\"cdk:partialCharge\" dataType=\"xsd:double\">0.0</scalar>"
                + "      </atom>"
                + "      <atom id=\"a4811470\" elementType=\"C\" x3=\"-8.678\" y3=\"4.536\" z3=\"-10.304\" formalCharge=\"0\">"
                + "        <scalar dictRef=\"cdk:partialCharge\" dataType=\"xsd:double\">0.0</scalar>"
                + "      </atom>"
                + "      <atom id=\"a211489\" elementType=\"H\" x3=\"-10.574\" y3=\"2.695\" z3=\"-10.778\" formalCharge=\"0\">"
                + "        <scalar dictRef=\"cdk:partialCharge\" dataType=\"xsd:double\">0.0</scalar>"
                + "      </atom>"
                + "      <atom id=\"a31287617\" elementType=\"H\" x3=\"-9.279\" y3=\"3.829\" z3=\"-8.398\" formalCharge=\"0\">"
                + "        <scalar dictRef=\"cdk:partialCharge\" dataType=\"xsd:double\">0.0</scalar>"
                + "      </atom>"
                + "      <atom id=\"a19487109\" elementType=\"H\" x3=\"-8.523\" y3=\"3.813\" z3=\"-11.09\" formalCharge=\"0\">"
                + "        <scalar dictRef=\"cdk:partialCharge\" dataType=\"xsd:double\">0.0</scalar>"
                + "      </atom>"
                + "      <atom id=\"a28589522\" elementType=\"H\" x3=\"-8.994\" y3=\"5.477\" z3=\"-10.737\" formalCharge=\"0\">"
                + "        <scalar dictRef=\"cdk:partialCharge\" dataType=\"xsd:double\">0.0</scalar>"
                + "      </atom>"
                + "      <atom id=\"a4638116\" elementType=\"H\" x3=\"-7.754\" y3=\"4.682\" z3=\"-9.763\" formalCharge=\"0\">"
                + "        <scalar dictRef=\"cdk:partialCharge\" dataType=\"xsd:double\">0.0</scalar>"
                + "      </atom>" + "    </atomArray>" + "  </molecule>" + "</molecule>";
        CMLReader reader = new CMLReader(new ByteArrayInputStream(cmlContent.getBytes()));
        try {
            IChemFile cfile = reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IChemFile.class));
            Assert.assertNotNull("ChemFile was null", cfile);
            List<IAtomContainer> containers = ChemFileManipulator.getAllAtomContainers(cfile);
            Assert.assertEquals("expected a single atom container", 1, containers.size());
            IAtomContainer container = containers.get(0);
            Assert.assertNotNull("null atom container read", container);

            // OK, now test that the residue identifier is properly read
            Assert.assertEquals("ALAA116", container.getID());
            System.out.println("" + container);
        } finally {
            reader.close();
        }
    }

    @Test
    public void testMixedNamespaces() throws Exception {
        InputStream in = getClass().getResourceAsStream("US06358966-20020319-C00001-enr.cml");
        CMLReader reader = new CMLReader(in);
        try {
            IChemFile cfile = reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IChemFile.class));
            Assert.assertEquals(34, ChemFileManipulator.getAtomCount(cfile));
            Assert.assertEquals(39, ChemFileManipulator.getBondCount(cfile));
        } finally {
            reader.close();
        }

    }
}
