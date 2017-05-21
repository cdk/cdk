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

import java.io.StringReader;
import java.io.StringWriter;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Mapping;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.silent.ReactionSet;

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

}
