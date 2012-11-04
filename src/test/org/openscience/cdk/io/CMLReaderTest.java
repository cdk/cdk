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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

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
    public void testBug3557907() throws FileNotFoundException, CDKException {

        InputStream in = getClass().getResourceAsStream("/data/cml/(1R)-1-aminoethan-1-ol.cml");
        CMLReader reader = new CMLReader(in);
        IChemFile cfile = reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IChemFile.class));

        Assert.assertNotNull("ChemFile was Null", cfile);

        List<IAtomContainer> containers = ChemFileManipulator.getAllAtomContainers(cfile);

        Assert.assertEquals("Expected a single atom container", 1, containers.size());

        IAtomContainer container = containers.get(0);

        Assert.assertNotNull("Null atom container read", container);

        IBond bond = container.getBond(2);

        Assert.assertNotNull("Null bond", bond);

        Assert.assertEquals("Expected Wedge (Up) Bond",
                            IBond.Stereo.UP, bond.getStereo());

    }


    /**
     * Ensure correct atomic numbers are read and does not default to 1
     *
     * @cdk.bug 1245
     */
    @Test
    public void testBug3553328() throws FileNotFoundException, CDKException {

        InputStream in = getClass().getResourceAsStream("/data/cml/(1R)-1-aminoethan-1-ol.cml");
        CMLReader reader = new CMLReader(in);
        IChemFile cfile = reader.read(DefaultChemObjectBuilder.getInstance().newInstance(IChemFile.class));

        Assert.assertNotNull("ChemFile was Null", cfile);

        List<IAtomContainer> containers = ChemFileManipulator.getAllAtomContainers(cfile);

        Assert.assertEquals("Expected a single atom container", 1, containers.size());

        IAtomContainer container = containers.get(0);

        Assert.assertNotNull("Null atom container read", container);

        for(IAtom atom : container.atoms()){
            Assert.assertEquals("Incorrect atomic number",
                                PeriodicTable.getAtomicNumber(atom.getSymbol()),
                                atom.getAtomicNumber());
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
            Assert.assertEquals("expected non-stereo bond",
                                IBond.Stereo.NONE, container.getBond(0).getStereo());
            Assert.assertEquals("expected Hatch (Down) Bond",
                                IBond.Stereo.DOWN, container.getBond(1).getStereo());
            Assert.assertEquals("expected non-stereo bond",
                                IBond.Stereo.NONE, container.getBond(2).getStereo());

        } finally {
            reader.close();
        }
    }

}
