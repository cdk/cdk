/* Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
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
 *  */
package org.openscience.cdk.io;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.sgroup.SgroupType;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * TestCase for the reading MDL V3000 mol files using one test file.
 *
 * @cdk.module test-io
 *
 * @see org.openscience.cdk.io.MDLReader
 * @see org.openscience.cdk.io.SDFReaderTest
 */
public class MDLV3000ReaderTest extends SimpleChemObjectReaderTest {

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(MDLV3000ReaderTest.class);

    @BeforeClass
    public static void setup() throws Exception {
        setSimpleChemObjectReader(new MDLV3000Reader(), "data/mdl/molV3000.mol");
    }

    @Test
    public void testAccepts() {
        MDLV3000Reader reader = new MDLV3000Reader();
        Assert.assertTrue(reader.accepts(AtomContainer.class));
    }

    /**
     * @cdk.bug 1571207
     */
    @Test
    public void testBug1571207() throws Exception {
        String filename = "data/mdl/molV3000.mol";
        logger.info("Testing: " + filename);
        try (InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
             MDLV3000Reader reader = new MDLV3000Reader(ins);) {
            IAtomContainer m = reader.read(new AtomContainer());
            reader.close();
            Assert.assertNotNull(m);
            Assert.assertEquals(31, m.getAtomCount());
            Assert.assertEquals(34, m.getBondCount());

            IAtom atom = m.getAtom(0);
            Assert.assertNotNull(atom);
            Assert.assertNotNull(atom.getPoint2d());
            Assert.assertEquals(10.4341, atom.getPoint2d().x, 0.0001);
            Assert.assertEquals(5.1053, atom.getPoint2d().y, 0.0001);
        }
    }

    @Test
    public void testEmptyString() throws Exception {
        String emptyString = "";
        try (MDLV3000Reader reader = new MDLV3000Reader(new StringReader(emptyString))) {
            reader.read(new AtomContainer());
            reader.close();
            Assert.fail("Should have received a CDK Exception");
        } catch (CDKException cdkEx) {
            Assert.assertEquals("Expected a header line, but found nothing.", cdkEx.getMessage());
        }
    }

    @Test
    public void testPseudoAtomLabels() throws Exception {
        try (InputStream in = ClassLoader.getSystemResourceAsStream("data/mdl/pseudoatomsv3000.mol");
        MDLV3000Reader reader = new MDLV3000Reader(in);) {
            IAtomContainer molecule = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
            molecule = reader.read(molecule);
            reader.close();
            Assert.assertTrue(molecule.getAtom(9) instanceof IPseudoAtom);
            Assert.assertEquals("R", molecule.getAtom(9).getSymbol());
            IPseudoAtom pa = (IPseudoAtom) molecule.getAtom(9);
            Assert.assertEquals("Leu", pa.getLabel());
        }
    }
    
    @Test public void pseudoAtomReplacement() throws Exception {
        try (MDLV3000Reader reader = new MDLV3000Reader(getClass().getResourceAsStream("pseudoAtomReplacement.mol"))) {
            IAtomContainer container = reader.read(new org.openscience.cdk.AtomContainer(0, 0, 0, 0));
            for (IAtom atom : container.getBond(9).atoms()) {
                Assert.assertTrue(container.contains(atom));
            }
        }
    }

    @Test public void positionalVariation() throws Exception {
        try (MDLV3000Reader reader = new MDLV3000Reader(getClass().getResourceAsStream("multicenterBond.mol"))) {
            IAtomContainer container = reader.read(new org.openscience.cdk.AtomContainer(0, 0, 0, 0));
            assertThat(container.getBondCount(), is(8));
            List<Sgroup> sgroups = container.getProperty(CDKConstants.CTAB_SGROUPS);
            assertNotNull(sgroups);
            assertThat(sgroups.size(), is(1));
            assertThat(sgroups.get(0).getType(), is(SgroupType.ExtMulticenter));
        }
    }

    @Test public void radicalsInCH3() throws Exception {
        try (MDLV3000Reader reader = new MDLV3000Reader(getClass().getResourceAsStream("CH3.mol"))) {
            IAtomContainer container = reader.read(new org.openscience.cdk.AtomContainer(0, 0, 0, 0));
            assertThat(container.getSingleElectronCount(), is(1));
            assertThat(container.getAtom(0).getImplicitHydrogenCount(), is(3));
        }
    }

    @Test public void issue602() throws Exception {
        try (MDLV3000Reader reader = new MDLV3000Reader(getClass().getResourceAsStream("issue602.mol"))) {
            IAtomContainer mol = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
            assertThat(mol.getAtomCount(), CoreMatchers.is(31));
        }
    }
}
