/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.test.io.SimpleChemObjectReaderTest;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * TestCase for the reading MDL RXN files using one test file.
 *
 * @cdk.module test-io
 *
 * @see org.openscience.cdk.io.MDLRXNReader
 */
public class MDLRXNV3000ReaderTest extends SimpleChemObjectReaderTest {

    private static final ILoggingTool logger = LoggingToolFactory.createLoggingTool(MDLRXNV3000ReaderTest.class);

    @BeforeClass
    public static void setup() {
        setSimpleChemObjectReader(new MDLRXNV3000Reader(), "reaction_v3.rxn");
    }

    @Test
    public void testAccepts() {
        MDLRXNV3000Reader reader = new MDLRXNV3000Reader();
        Assert.assertTrue(reader.accepts(ChemModel.class));
        Assert.assertTrue(reader.accepts(Reaction.class));
    }

    /**
     * @cdk.bug 1849925
     */
    @Test
    public void testReadReactions1() throws Exception {
        String filename1 = "reaction_v3.rxn";
        logger.info("Testing: " + filename1);
        InputStream ins1 = this.getClass().getResourceAsStream(filename1);
        MDLRXNV3000Reader reader1 = new MDLRXNV3000Reader(ins1, Mode.STRICT);
        IReaction reaction1 = new Reaction();
        reaction1 = reader1.read(reaction1);
        reader1.close();

        Assert.assertNotNull(reaction1);
        Assert.assertEquals(1, reaction1.getReactantCount());
        Assert.assertEquals(1, reaction1.getProductCount());
        IAtomContainer reactant = reaction1.getReactants().getAtomContainer(0);
        Assert.assertNotNull(reactant);
        Assert.assertEquals(32, reactant.getAtomCount());
        Assert.assertEquals(29, reactant.getBondCount());
        IAtomContainer product = reaction1.getProducts().getAtomContainer(0);
        Assert.assertNotNull(product);
        Assert.assertEquals(32, product.getAtomCount());
        Assert.assertEquals(29, product.getBondCount());

    }

    @Test public void readAgents() throws IOException, CDKException {
        String rxnfile = "$RXN V3000\n" +
                "\n" +
                "  Mrv1810      020601212219\n" +
                "\n" +
                "M  V30 COUNTS 2 1 2\n" +
                "M  V30 BEGIN REACTANT\n" +
                "M  V30 BEGIN CTAB\n" +
                "M  V30 COUNTS 9 9 0 0 0\n" +
                "M  V30 BEGIN ATOM\n" +
                "M  V30 1 C -24.3094 2.695 0 0\n" +
                "M  V30 2 C -22.9758 1.925 0 0\n" +
                "M  V30 3 C -21.6421 2.695 0 0\n" +
                "M  V30 4 C -22.9758 0.385 0 0\n" +
                "M  V30 5 C -21.6421 -0.385 0 0\n" +
                "M  V30 6 C -21.6421 -1.925 0 0\n" +
                "M  V30 7 C -22.9758 -2.695 0 0\n" +
                "M  V30 8 C -24.3094 -1.925 0 0\n" +
                "M  V30 9 C -24.3094 -0.385 0 0\n" +
                "M  V30 END ATOM\n" +
                "M  V30 BEGIN BOND\n" +
                "M  V30 1 1 1 2\n" +
                "M  V30 2 1 2 3\n" +
                "M  V30 3 1 2 4\n" +
                "M  V30 4 4 4 5\n" +
                "M  V30 5 4 5 6\n" +
                "M  V30 6 4 6 7\n" +
                "M  V30 7 4 7 8\n" +
                "M  V30 8 4 8 9\n" +
                "M  V30 9 4 4 9\n" +
                "M  V30 END BOND\n" +
                "M  V30 END CTAB\n" +
                "M  V30 BEGIN CTAB\n" +
                "M  V30 COUNTS 7 6 0 0 0\n" +
                "M  V30 BEGIN ATOM\n" +
                "M  V30 1 C -14.341 0.1528 0 0\n" +
                "M  V30 2 C -15.6747 -0.6172 0 0\n" +
                "M  V30 3 C -17.0084 0.1528 0 0\n" +
                "M  V30 4 O -18.3421 -0.6172 0 0\n" +
                "M  V30 5 Cl -17.0084 1.6928 0 0\n" +
                "M  V30 6 C -13.0074 -0.6172 0 0\n" +
                "M  V30 7 Cl -11.6737 0.1528 0 0\n" +
                "M  V30 END ATOM\n" +
                "M  V30 BEGIN BOND\n" +
                "M  V30 1 1 1 2\n" +
                "M  V30 2 1 2 3\n" +
                "M  V30 3 2 3 4\n" +
                "M  V30 4 1 3 5\n" +
                "M  V30 5 1 1 6\n" +
                "M  V30 6 1 6 7\n" +
                "M  V30 END BOND\n" +
                "M  V30 END CTAB\n" +
                "M  V30 END REACTANT\n" +
                "M  V30 BEGIN PRODUCT\n" +
                "M  V30 BEGIN CTAB\n" +
                "M  V30 COUNTS 15 15 0 0 0\n" +
                "M  V30 BEGIN ATOM\n" +
                "M  V30 1 C 18.9747 -3.08 0 0\n" +
                "M  V30 2 C 18.9747 -1.54 0 0\n" +
                "M  V30 3 C 17.641 -0.77 0 0\n" +
                "M  V30 4 C 20.3084 -0.77 0 0\n" +
                "M  V30 5 C 20.3084 0.77 0 0\n" +
                "M  V30 6 C 21.6421 1.54 0 0\n" +
                "M  V30 7 C 22.9758 0.77 0 0\n" +
                "M  V30 8 C 22.9758 -0.77 0 0\n" +
                "M  V30 9 C 21.6421 -1.54 0 0\n" +
                "M  V30 10 C 24.3094 1.54 0 0\n" +
                "M  V30 11 O 24.3094 3.08 0 0\n" +
                "M  V30 12 C 25.6431 0.77 0 0\n" +
                "M  V30 13 C 26.9768 1.54 0 0\n" +
                "M  V30 14 C 28.3105 0.77 0 0\n" +
                "M  V30 15 Cl 29.6441 1.54 0 0\n" +
                "M  V30 END ATOM\n" +
                "M  V30 BEGIN BOND\n" +
                "M  V30 1 1 1 2\n" +
                "M  V30 2 1 2 3\n" +
                "M  V30 3 1 2 4\n" +
                "M  V30 4 4 4 5\n" +
                "M  V30 5 4 5 6\n" +
                "M  V30 6 4 6 7\n" +
                "M  V30 7 4 7 8\n" +
                "M  V30 8 4 8 9\n" +
                "M  V30 9 4 4 9\n" +
                "M  V30 10 1 7 10\n" +
                "M  V30 11 2 10 11\n" +
                "M  V30 12 1 10 12\n" +
                "M  V30 13 1 12 13\n" +
                "M  V30 14 1 13 14\n" +
                "M  V30 15 1 14 15\n" +
                "M  V30 END BOND\n" +
                "M  V30 END CTAB\n" +
                "M  V30 END PRODUCT\n" +
                "M  V30 BEGIN AGENT\n" +
                "M  V30 BEGIN CTAB\n" +
                "M  V30 COUNTS 4 0 0 0 0\n" +
                "M  V30 BEGIN ATOM\n" +
                "M  V30 1 Al -3.135 3.3128 0 0 CHG=3\n" +
                "M  V30 2 Cl -1.045 3.3128 0 0 CHG=-1\n" +
                "M  V30 3 Cl 1.045 3.3128 0 0 CHG=-1\n" +
                "M  V30 4 Cl 3.135 3.3128 0 0 CHG=-1\n" +
                "M  V30 END ATOM\n" +
                "M  V30 END CTAB\n" +
                "M  V30 BEGIN CTAB\n" +
                "M  V30 COUNTS 3 2 0 0 0\n" +
                "M  V30 BEGIN ATOM\n" +
                "M  V30 1 C 7.9887 2.7995 0 0\n" +
                "M  V30 2 Cl 6.655 3.5695 0 0\n" +
                "M  V30 3 Cl 9.3224 3.5695 0 0\n" +
                "M  V30 END ATOM\n" +
                "M  V30 BEGIN BOND\n" +
                "M  V30 1 1 1 2\n" +
                "M  V30 2 1 1 3\n" +
                "M  V30 END BOND\n" +
                "M  V30 END CTAB\n" +
                "M  V30 END AGENT\n" +
                "M  END\n";
        try (MDLRXNV3000Reader mdlr = new MDLRXNV3000Reader(new StringReader(rxnfile))) {
            IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
            IReaction reaction = mdlr.read(bldr.newInstance(IReaction.class));
            Assert.assertEquals(2, reaction.getReactantCount());
            Assert.assertEquals(1, reaction.getProductCount());
            Assert.assertEquals(2, reaction.getAgents().getAtomContainerCount());
        }
    }
}
