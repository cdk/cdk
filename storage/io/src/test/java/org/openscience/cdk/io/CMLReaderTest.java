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

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.test.io.ChemObjectReaderTest;
import org.openscience.cdk.test.io.SimpleChemObjectReaderTest;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;
import org.openscience.cdk.tools.periodictable.PeriodicTable;

/**
 * TestCase for reading CML files.
 *
 */
class CMLReaderTest extends SimpleChemObjectReaderTest {

    @BeforeAll
    static void setup() {
        setSimpleChemObjectReader(new CMLReader(), "org/openscience/cdk/io/3.cml");
    }

    @Test
    void testAccepts() {
        Assertions.assertTrue(chemObjectIO.accepts(ChemFile.class));
    }

    @Test
    @Override
    public void testSetReader_Reader() throws Exception {
        Assertions.assertThrows(CDKException.class,
                                () -> {
                                    InputStream ins = ChemObjectReaderTest.class.getClassLoader()
                                                                                .getResourceAsStream(testFile);
                                    chemObjectIO.setReader(new InputStreamReader(ins));
                                });
    }

                                /**
     * Ensure stereoBond content is read if the usual "dictRef" attribute is not
     * supplied
     *
     * @cdk.bug 1248
     */
    @Test
    void testBug1248() throws IOException, CDKException {

        InputStream in = getClass().getResourceAsStream("(1R)-1-aminoethan-1-ol.cml");
        CMLReader reader = new CMLReader(in);
        try {
            IChemFile cfile = reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IChemFile.class));

            Assertions.assertNotNull(cfile, "ChemFile was Null");

            List<IAtomContainer> containers = ChemFileManipulator.getAllAtomContainers(cfile);

            Assertions.assertEquals(1, containers.size(), "Expected a single atom container");

            IAtomContainer container = containers.get(0);

            Assertions.assertNotNull(container, "Null atom container read");

            IBond bond = container.getBond(2);

            Assertions.assertNotNull(bond, "Null bond");

//            Assertions.assertEquals(IBond.Stereo.UP, bond.getStereo(), "Expected Wedge (Up) Bond"); // deprecated
            Assertions.assertEquals(IBond.Display.Up, bond.getDisplay(), "Expected Wedge (Up) Bond");

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
    void testBug1245() throws IOException, CDKException {

        InputStream in = getClass().getResourceAsStream("(1R)-1-aminoethan-1-ol.cml");
        CMLReader reader = new CMLReader(in);
        try {
            IChemFile cfile = reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IChemFile.class));

            Assertions.assertNotNull(cfile, "ChemFile was Null");

            List<IAtomContainer> containers = ChemFileManipulator.getAllAtomContainers(cfile);

            Assertions.assertEquals(1, containers.size(), "Expected a single atom container");

            IAtomContainer container = containers.get(0);

            Assertions.assertNotNull(container, "Null atom container read");

            for (IAtom atom : container.atoms()) {
                Assertions.assertEquals(PeriodicTable.getAtomicNumber(atom.getSymbol()), atom.getAtomicNumber(), "Incorrect atomic number");
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
    void testBug1274() throws CDKException, IOException {

        InputStream in = getClass().getResourceAsStream("(1R)-1-aminoethan-1-ol-multipleBondStereo.cml");
        CMLReader reader = new CMLReader(in);
        try {
            IChemFile cfile = reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IChemFile.class));

            Assertions.assertNotNull(cfile, "ChemFile was null");

            List<IAtomContainer> containers = ChemFileManipulator.getAllAtomContainers(cfile);

            Assertions.assertEquals(1, containers.size(), "expected a single atom container");

            IAtomContainer container = containers.get(0);

            Assertions.assertNotNull(container, "null atom container read");

            // we check here that the charContent is not used and also that more then
            // one stereo isn't set
//            Assertions.assertEquals(IBond.Stereo.NONE, container.getBond(0).getStereo(), "expected non-stereo bond");
//            Assertions.assertEquals(IBond.Stereo.DOWN, container.getBond(1).getStereo(), "expected Hatch (Down) Bond");
//            Assertions.assertEquals(IBond.Stereo.NONE, container.getBond(2).getStereo(), "expected non-stereo bond");

            Assertions.assertEquals(IBond.Display.Solid, container.getBond(0).getDisplay(), "expected non-stereo bond");
            Assertions.assertEquals(IBond.Display.Down, container.getBond(1).getDisplay(), "expected Hatch (Down) Bond");
            Assertions.assertEquals(IBond.Display.Solid, container.getBond(2).getDisplay(), "expected non-stereo bond");

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
    void testBug1275() throws CDKException, IOException {

        InputStream in = getClass().getResourceAsStream("(1R)-1-aminoethan-1-ol-malformedDictRef.cml");
        CMLReader reader = new CMLReader(in);
        try {
            IChemFile cfile = reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IChemFile.class));

            Assertions.assertNotNull(cfile, "ChemFile was null");

            List<IAtomContainer> containers = ChemFileManipulator.getAllAtomContainers(cfile);

            Assertions.assertEquals(1, containers.size(), "expected a single atom container");

            IAtomContainer container = containers.get(0);

            Assertions.assertNotNull(container, "null atom container read");

            // we check here that the malformed dictRef doesn't throw an exception
            Assertions.assertEquals(IBond.Display.Solid, container.getBond(0).getDisplay(), "expected non-stereo bond");
            Assertions.assertEquals(IBond.Display.Up, container.getBond(1).getDisplay(), "expected Wedge (Up) Bond");
            Assertions.assertEquals(IBond.Display.Solid, container.getBond(2).getDisplay(), "expected non-stereo bond");

        } finally {
            reader.close();
        }

    }

    @Test
    void testWedgeBondParsing() throws CDKException, IOException {
        InputStream in = getClass().getResourceAsStream("AZD5423.xml");
        CMLReader reader = new CMLReader(in);
        try {
            IChemFile cfile = reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IChemFile.class));
            Assertions.assertNotNull(cfile, "ChemFile was null");
            List<IAtomContainer> containers = ChemFileManipulator.getAllAtomContainers(cfile);
            Assertions.assertEquals(1, containers.size(), "expected a single atom container");
            IAtomContainer container = containers.get(0);
            Assertions.assertNotNull(container, "null atom container read");

            // we check here that the malformed dictRef doesn't throw an exception
            for (int i = 0; i < 19; i++) {
                MatcherAssert.assertThat("found an unexpected wedge bond for " + i + ": " + container.getBond(i).getDisplay(),
                                         container.getBond(i).getDisplay(),
                                         CoreMatchers.is(IBond.Display.Solid));
            }
            Assertions.assertEquals(IBond.Display.Down, container.getBond(19).getDisplay(), "expected a wedge bond");
            for (int i = 20; i < 30; i++) {
                MatcherAssert.assertThat("found an unexpected wedge bond for " + i + ": " + container.getBond(i).getDisplay(),
                                         container.getBond(i).getDisplay(),
                                         CoreMatchers.is(IBond.Display.Solid));
            }
            Assertions.assertEquals(IBond.Display.Up, container.getBond(30).getDisplay(), "expected a wedge bond");
            for (int i = 31; i <= 37; i++) {
                MatcherAssert.assertThat("found an unexpected wedge bond for " + i + ": " + container.getBond(i).getDisplay(),
                                         container.getBond(i).getDisplay(),
                                         CoreMatchers.is(IBond.Display.Solid));
            }
        } finally {
            reader.close();
        }
    }

    @Test
    void testSFBug1085912_1() throws Exception {
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
            Assertions.assertNotNull(cfile, "ChemFile was null");
            List<IAtomContainer> containers = ChemFileManipulator.getAllAtomContainers(cfile);
            Assertions.assertEquals(1, containers.size(), "expected a single atom container");
            IAtomContainer container = containers.get(0);
            Assertions.assertNotNull(container, "null atom container read");

            // OK, now test that the residue identifier is properly read
            Assertions.assertEquals("ALAA116", container.getID());
        } finally {
            reader.close();
        }
    }

    @Test
    void testMixedNamespaces() throws Exception {
        InputStream in = getClass().getResourceAsStream("US06358966-20020319-C00001-enr.cml");
        CMLReader reader = new CMLReader(in);
        try {
            IChemFile cfile = reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IChemFile.class));
            Assertions.assertEquals(34, ChemFileManipulator.getAtomCount(cfile));
            Assertions.assertEquals(39, ChemFileManipulator.getBondCount(cfile));
        } finally {
            reader.close();
        }

    }
}
