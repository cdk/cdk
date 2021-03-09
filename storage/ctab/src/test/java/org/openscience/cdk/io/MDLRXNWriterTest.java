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

import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Mapping;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.silent.ReactionSet;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static org.hamcrest.Matchers.containsString;

/**
 * TestCase for the writer MDL rxn files using one test file.
 *
 * @cdk.module test-io
 *
 * @see org.openscience.cdk.io.MDLRXNWriter
 */
public class MDLRXNWriterTest extends ChemObjectIOTest {

    private static IChemObjectBuilder builder;

    @BeforeClass
    public static void setup() {
        builder = DefaultChemObjectBuilder.getInstance();
        setChemObjectIO(new MDLRXNWriter());
    }

    @Test
    public void testAccepts() throws Exception {
        MDLRXNWriter reader = new MDLRXNWriter();
        Assert.assertTrue(reader.accepts(Reaction.class));
    }

    @Test
    public void testRoundtrip() throws Exception {
        IReaction reaction = builder.newInstance(IReaction.class);
        IAtomContainer hydroxide = builder.newInstance(IAtomContainer.class);
        hydroxide.addAtom(builder.newInstance(IAtom.class, "O"));
        reaction.addReactant(hydroxide);
        IAtomContainer proton = builder.newInstance(IAtomContainer.class);
        proton.addAtom(builder.newInstance(IAtom.class, "H"));
        reaction.addReactant(proton);
        IAtomContainer water = builder.newInstance(IAtomContainer.class);
        water.addAtom(builder.newInstance(IAtom.class, "O"));
        reaction.addProduct(water);
        reaction.addMapping(new Mapping(hydroxide.getAtom(0), water.getAtom(0)));

        // now serialize to MDL RXN
        StringWriter writer = new StringWriter(10000);
        String file = "";
        MDLRXNWriter mdlWriter = new MDLRXNWriter(writer);
        mdlWriter.write(reaction);
        mdlWriter.close();
        file = writer.toString();

        Assert.assertTrue(file.length() > 0);

        // now deserialize the MDL RXN output
        IReaction reaction2 = builder.newInstance(IReaction.class);
        MDLRXNReader reader = new MDLRXNReader(new StringReader(file));
        reaction2 = (IReaction) reader.read(reaction2);
        reader.close();

        Assert.assertEquals(2, reaction2.getReactantCount());
        Assert.assertEquals(1, reaction2.getProductCount());
        Assert.assertEquals(1, reaction2.getMappingCount());
    }

    @Test
    public void testReactionSet_1() throws Exception {
        IReaction reaction11 = builder.newInstance(IReaction.class);
        IAtomContainer hydroxide = builder.newInstance(IAtomContainer.class);
        hydroxide.addAtom(builder.newInstance(IAtom.class, "O"));
        reaction11.addReactant(hydroxide);
        IAtomContainer proton = builder.newInstance(IAtomContainer.class);
        proton.addAtom(builder.newInstance(IAtom.class, "H"));
        reaction11.addReactant(proton);

        IAtomContainer water = builder.newInstance(IAtomContainer.class);
        water.addAtom(builder.newInstance(IAtom.class, "O"));
        reaction11.addProduct(water);

        IReactionSet reactionSet = new ReactionSet();
        reactionSet.addReaction(reaction11);

        // now serialize to MDL RXN
        StringWriter writer = new StringWriter(10000);
        String file = "";
        MDLRXNWriter mdlWriter = new MDLRXNWriter(writer);
        mdlWriter.write(reactionSet);
        mdlWriter.close();
        file = writer.toString();

        Assert.assertTrue(file.length() > 0);

        // now deserialize the MDL RXN output
        IReaction reaction2 = builder.newInstance(IReaction.class);
        MDLRXNReader reader = new MDLRXNReader(new StringReader(file));
        reaction2 = (IReaction) reader.read(reaction2);
        reader.close();

        Assert.assertEquals(2, reaction2.getReactantCount());
        Assert.assertEquals(1, reaction2.getReactants().getAtomContainer(0).getAtomCount());
        Assert.assertEquals(1, reaction2.getReactants().getAtomContainer(1).getAtomCount());
        Assert.assertEquals(1, reaction2.getProductCount());
        Assert.assertEquals(1, reaction2.getProducts().getAtomContainer(0).getAtomCount());
    }

    @Test
    public void testReactionSet_2() throws Exception {
        IReaction reaction11 = builder.newInstance(IReaction.class);
        IAtomContainer hydroxide = builder.newInstance(IAtomContainer.class);
        hydroxide.addAtom(builder.newInstance(IAtom.class, "O"));
        reaction11.addReactant(hydroxide);
        IAtomContainer proton = builder.newInstance(IAtomContainer.class);
        proton.addAtom(builder.newInstance(IAtom.class, "H"));
        reaction11.addReactant(proton);

        IAtomContainer water = builder.newInstance(IAtomContainer.class);
        water.addAtom(builder.newInstance(IAtom.class, "O"));
        reaction11.addProduct(water);

        IReaction reaction12 = builder.newInstance(IReaction.class);
        IAtomContainer h = builder.newInstance(IAtomContainer.class);
        h.addAtom(builder.newInstance(IAtom.class, "H"));
        IAtomContainer n = builder.newInstance(IAtomContainer.class);
        n.addAtom(builder.newInstance(IAtom.class, "N"));
        reaction12.addReactant(h);
        reaction12.addReactant(n);
        IAtomContainer ammonia = builder.newInstance(IAtomContainer.class);
        ammonia.addAtom(builder.newInstance(IAtom.class, "N"));
        ammonia.addAtom(builder.newInstance(IAtom.class, "H"));
        ammonia.addBond(0, 1, IBond.Order.SINGLE);
        reaction12.addProduct(ammonia);

        IReactionSet reactionSet = builder.newInstance(IReactionSet.class);
        reactionSet.addReaction(reaction11);
        reactionSet.addReaction(reaction12);

        // now serialize to MDL RXN
        StringWriter writer = new StringWriter(10000);
        String file = "";
        MDLRXNWriter mdlWriter = new MDLRXNWriter(writer);
        mdlWriter.write(reactionSet);
        mdlWriter.close();
        file = writer.toString();

        Assert.assertTrue(file.length() > 0);

        // now deserialize the MDL RXN output
        IReactionSet reactionSetF = builder.newInstance(IReactionSet.class);
        MDLRXNReader reader = new MDLRXNReader(new StringReader(file));
        reactionSetF = (IReactionSet) reader.read(reactionSetF);
        reader.close();

        Assert.assertEquals(2, reactionSetF.getReactionCount());
        Assert.assertEquals(1, reactionSetF.getReaction(0).getReactants().getAtomContainer(0).getAtomCount());
        Assert.assertEquals(1, reactionSetF.getReaction(0).getReactants().getAtomContainer(1).getAtomCount());
        Assert.assertEquals(1, reactionSetF.getReaction(0).getProductCount());
        Assert.assertEquals(1, reactionSetF.getReaction(0).getProducts().getAtomContainer(0).getAtomCount());
    }

    @Test public void writeAgentsFromV3000() throws IOException, CDKException {
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
                "M  V30 4 1 4 5\n" +
                "M  V30 5 2 5 6\n" +
                "M  V30 6 1 6 7\n" +
                "M  V30 7 2 7 8\n" +
                "M  V30 8 1 8 9\n" +
                "M  V30 9 2 4 9\n" +
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
                "M  V30 4 1 4 5\n" +
                "M  V30 5 2 5 6\n" +
                "M  V30 6 1 6 7\n" +
                "M  V30 7 2 7 8\n" +
                "M  V30 8 1 8 9\n" +
                "M  V30 9 2 4 9\n" +
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
        StringWriter sw = new StringWriter();
        try (MDLRXNV3000Reader mdlr = new MDLRXNV3000Reader(new StringReader(rxnfile));
             MDLRXNWriter mdlw = new MDLRXNWriter(sw)) {
            IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
            mdlw.write(mdlr.read(bldr.newInstance(IReaction.class)));
        }
        String output = sw.toString();
        MatcherAssert.assertThat(output, containsString(
                "$RXN\n" +
                "\n" +
                "\n" +
                "\n" +
                "  2  1  2"));
        MatcherAssert.assertThat(output, containsString(
                "\n" +
                "  4  0  0  0  0  0  0  0  0  0999 V2000\n" +
                "   -3.1350    3.3128    0.0000 Al  0  1  0  0  0  0  0  0  0  0  0  0\n" +
                "   -1.0450    3.3128    0.0000 Cl  0  5  0  0  0  0  0  0  0  0  0  0\n" +
                "    1.0450    3.3128    0.0000 Cl  0  5  0  0  0  0  0  0  0  0  0  0\n" +
                "    3.1350    3.3128    0.0000 Cl  0  5  0  0  0  0  0  0  0  0  0  0\n" +
                "M  CHG  1   1   3\n" +
                "M  CHG  1   2  -1\n" +
                "M  CHG  1   3  -1\n" +
                "M  CHG  1   4  -1\n" +
                "M  END"));
    }

    @Test public void writeAgentsFromV2000() throws IOException, CDKException {
        String rxnfile = "$RXN\n" +
                "\n" +
                "\n" +
                "\n" +
                "  2  1  2\n" +
                "$MOL\n" +
                "\n" +
                "  CDK     02062122413D\n" +
                "\n" +
                "  9  9  0  0  0  0  0  0  0  0999 V2000\n" +
                "  -24.3094    2.6950    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "  -22.9758    1.9250    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "  -21.6421    2.6950    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "  -22.9758    0.3850    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "  -21.6421   -0.3850    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "  -21.6421   -1.9250    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "  -22.9758   -2.6950    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "  -24.3094   -1.9250    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "  -24.3094   -0.3850    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "  1  2  1  0  0  0  0\n" +
                "  2  3  1  0  0  0  0\n" +
                "  2  4  1  0  0  0  0\n" +
                "  4  5  1  0  0  0  0\n" +
                "  5  6  2  0  0  0  0\n" +
                "  6  7  1  0  0  0  0\n" +
                "  7  8  2  0  0  0  0\n" +
                "  8  9  1  0  0  0  0\n" +
                "  4  9  2  0  0  0  0\n" +
                "M  END\n" +
                "$MOL\n" +
                "\n" +
                "  CDK     02062122413D\n" +
                "\n" +
                "  7  6  0  0  0  0  0  0  0  0999 V2000\n" +
                "  -14.3410    0.1528    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "  -15.6747   -0.6172    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "  -17.0084    0.1528    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "  -18.3421   -0.6172    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "  -17.0084    1.6928    0.0000 Cl  0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "  -13.0074   -0.6172    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "  -11.6737    0.1528    0.0000 Cl  0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "  1  2  1  0  0  0  0\n" +
                "  2  3  1  0  0  0  0\n" +
                "  3  4  2  0  0  0  0\n" +
                "  3  5  1  0  0  0  0\n" +
                "  1  6  1  0  0  0  0\n" +
                "  6  7  1  0  0  0  0\n" +
                "M  END\n" +
                "$MOL\n" +
                "\n" +
                "  CDK     02062122413D\n" +
                "\n" +
                " 15 15  0  0  0  0  0  0  0  0999 V2000\n" +
                "   18.9747   -3.0800    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   18.9747   -1.5400    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   17.6410   -0.7700    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   20.3084   -0.7700    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   20.3084    0.7700    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   21.6421    1.5400    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   22.9758    0.7700    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   22.9758   -0.7700    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   21.6421   -1.5400    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   24.3094    1.5400    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   24.3094    3.0800    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   25.6431    0.7700    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   26.9768    1.5400    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   28.3105    0.7700    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   29.6441    1.5400    0.0000 Cl  0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "  1  2  1  0  0  0  0\n" +
                "  2  3  1  0  0  0  0\n" +
                "  2  4  1  0  0  0  0\n" +
                "  4  5  1  0  0  0  0\n" +
                "  5  6  2  0  0  0  0\n" +
                "  6  7  1  0  0  0  0\n" +
                "  7  8  2  0  0  0  0\n" +
                "  8  9  1  0  0  0  0\n" +
                "  4  9  2  0  0  0  0\n" +
                "  7 10  1  0  0  0  0\n" +
                " 10 11  2  0  0  0  0\n" +
                " 10 12  1  0  0  0  0\n" +
                " 12 13  1  0  0  0  0\n" +
                " 13 14  1  0  0  0  0\n" +
                " 14 15  1  0  0  0  0\n" +
                "M  END\n" +
                "$MOL\n" +
                "\n" +
                "  CDK     02062122413D\n" +
                "\n" +
                "  4  0  0  0  0  0  0  0  0  0999 V2000\n" +
                "   -3.1350    3.3128    0.0000 Al  0  1  0  0  0  0  0  0  0  0  0  0\n" +
                "   -1.0450    3.3128    0.0000 Cl  0  5  0  0  0  0  0  0  0  0  0  0\n" +
                "    1.0450    3.3128    0.0000 Cl  0  5  0  0  0  0  0  0  0  0  0  0\n" +
                "    3.1350    3.3128    0.0000 Cl  0  5  0  0  0  0  0  0  0  0  0  0\n" +
                "M  CHG  1   1   3\n" +
                "M  CHG  1   2  -1\n" +
                "M  CHG  1   3  -1\n" +
                "M  CHG  1   4  -1\n" +
                "M  END\n" +
                "$MOL\n" +
                "\n" +
                "  CDK     02062122413D\n" +
                "\n" +
                "  3  2  0  0  0  0  0  0  0  0999 V2000\n" +
                "    7.9887    2.7995    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "    6.6550    3.5695    0.0000 Cl  0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "    9.3224    3.5695    0.0000 Cl  0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "  1  2  1  0  0  0  0\n" +
                "  1  3  1  0  0  0  0\n" +
                "M  END";
        StringWriter sw = new StringWriter();
        try (MDLRXNV2000Reader mdlr = new MDLRXNV2000Reader(new StringReader(rxnfile));
             MDLRXNWriter mdlw = new MDLRXNWriter(sw)) {
            IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
            mdlw.write(mdlr.read(bldr.newInstance(IReaction.class)));
        }
        String output = sw.toString();
        MatcherAssert.assertThat(output, containsString(
                "$RXN\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "  2  1  2"));
        MatcherAssert.assertThat(output, containsString(
                "\n" +
                        "  4  0  0  0  0  0  0  0  0  0999 V2000\n" +
                        "   -3.1350    3.3128    0.0000 Al  0  1  0  0  0  0  0  0  0  0  0  0\n" +
                        "   -1.0450    3.3128    0.0000 Cl  0  5  0  0  0  0  0  0  0  0  0  0\n" +
                        "    1.0450    3.3128    0.0000 Cl  0  5  0  0  0  0  0  0  0  0  0  0\n" +
                        "    3.1350    3.3128    0.0000 Cl  0  5  0  0  0  0  0  0  0  0  0  0\n" +
                        "M  CHG  1   1   3\n" +
                        "M  CHG  1   2  -1\n" +
                        "M  CHG  1   3  -1\n" +
                        "M  CHG  1   4  -1\n" +
                        "M  END"));
    }

}
