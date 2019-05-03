/* Copyright (C) 2008  Miguel Rojas <miguelrojasch@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
package org.openscience.cdk.tools.manipulator;

import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionScheme;
import org.openscience.cdk.interfaces.IReactionSet;

/**
 * @cdk.module test-standard
 */
public class ReactionSchemeManipulatorTest extends CDKTestCase {

    private IChemObjectBuilder builder;

    public ReactionSchemeManipulatorTest() {
        super();
    }

    @Before
    public void setUp() throws Exception {
        builder = DefaultChemObjectBuilder.getInstance();

    }

    @Test
    public void testGetAllMolecules_IReactionScheme() {
        IReactionScheme reactionScheme = builder.newInstance(IReactionScheme.class);
        IReaction reaction1 = builder.newInstance(IReaction.class);
        reaction1.addProduct(builder.newInstance(IAtomContainer.class));
        IReaction reaction2 = builder.newInstance(IReaction.class);
        reaction2.addProduct(builder.newInstance(IAtomContainer.class));
        reactionScheme.addReaction(reaction1); // 1
        reactionScheme.addReaction(reaction2); // 2

        Assert.assertEquals(2, ReactionSchemeManipulator.getAllAtomContainers(reactionScheme).getAtomContainerCount());

    }

    @Test
    public void testGetAllMolecules_IReactionScheme_IAtomContainerSet() {
        IReactionScheme reactionScheme = builder.newInstance(IReactionScheme.class);
        IReaction reaction1 = builder.newInstance(IReaction.class);
        reaction1.addProduct(builder.newInstance(IAtomContainer.class));
        IReaction reaction2 = builder.newInstance(IReaction.class);
        reaction2.addProduct(builder.newInstance(IAtomContainer.class));
        reactionScheme.addReaction(reaction1); // 1
        reactionScheme.addReaction(reaction2); // 2

        Assert.assertEquals(
                2,
                ReactionSchemeManipulator.getAllAtomContainers(reactionScheme,
                        builder.newInstance(IAtomContainerSet.class)).getAtomContainerCount());

    }

    @Test
    public void testGetAllMolecules_IReactionScheme2() {
        IReactionScheme reactionScheme = builder.newInstance(IReactionScheme.class);
        IReaction reaction1 = builder.newInstance(IReaction.class);
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        reaction1.addProduct(molecule);
        reaction1.addReactant(builder.newInstance(IAtomContainer.class));
        reactionScheme.addReaction(reaction1);
        IReaction reaction2 = builder.newInstance(IReaction.class);
        reaction2.addProduct(builder.newInstance(IAtomContainer.class));
        reaction2.addReactant(molecule);
        reactionScheme.addReaction(reaction2);

        Assert.assertEquals(3, ReactionSchemeManipulator.getAllAtomContainers(reactionScheme).getAtomContainerCount());

    }

    @Test
    public void testGetAllMolecules_IReactionScheme3() {
        IReactionScheme scheme1 = builder.newInstance(IReactionScheme.class);

        IReactionScheme scheme11 = builder.newInstance(IReactionScheme.class);
        IReaction reaction1 = builder.newInstance(IReaction.class);
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        reaction1.addProduct(molecule);
        reaction1.addReactant(builder.newInstance(IAtomContainer.class));
        scheme11.addReaction(reaction1);
        IReaction reaction2 = builder.newInstance(IReaction.class);
        reaction2.addProduct(builder.newInstance(IAtomContainer.class));
        reaction2.addReactant(molecule);
        scheme11.addReaction(reaction2);
        scheme1.add(scheme11);

        IReactionScheme scheme12 = builder.newInstance(IReactionScheme.class);
        IReaction reaction3 = builder.newInstance(IReaction.class);
        reaction3.addProduct(builder.newInstance(IAtomContainer.class));
        reaction3.addReactant(molecule);
        scheme12.addReaction(reaction3);
        scheme1.add(scheme12);

        IReaction reaction11 = builder.newInstance(IReaction.class);
        reaction11.addProduct(builder.newInstance(IAtomContainer.class));
        scheme1.addReaction(reaction11);

        Assert.assertEquals(5, ReactionSchemeManipulator.getAllAtomContainers(scheme1).getAtomContainerCount());

    }

    @Test
    public void testGetAllIDs_IReactionScheme() {
        IReactionScheme scheme1 = builder.newInstance(IReactionScheme.class);
        scheme1.setID("scheme1");

        IReactionScheme scheme11 = builder.newInstance(IReactionScheme.class);
        scheme11.setID("scheme11");
        IReaction reaction1 = builder.newInstance(IReaction.class);
        reaction1.setID("reaction1");
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        reaction1.setID("molecule");
        reaction1.addProduct(molecule);
        reaction1.addReactant(builder.newInstance(IAtomContainer.class));
        scheme11.addReaction(reaction1);
        IReaction reaction2 = builder.newInstance(IReaction.class);
        reaction1.setID("reaction2");
        reaction2.addProduct(builder.newInstance(IAtomContainer.class));
        reaction2.addReactant(molecule);
        scheme11.addReaction(reaction2);
        scheme1.add(scheme11);

        IReactionScheme scheme12 = builder.newInstance(IReactionScheme.class);
        scheme12.setID("scheme12");
        IReaction reaction3 = builder.newInstance(IReaction.class);
        reaction3.setID("reaction3");
        reaction3.addProduct(builder.newInstance(IAtomContainer.class));
        reaction3.addReactant(molecule);
        scheme12.addReaction(reaction3);
        scheme1.add(scheme12);

        IReaction reaction11 = builder.newInstance(IReaction.class);
        reaction11.setID("reaction11");
        reaction11.addProduct(builder.newInstance(IAtomContainer.class));
        scheme1.addReaction(reaction11);

        Assert.assertEquals(6, ReactionSchemeManipulator.getAllIDs(scheme1).size());

    }

    @Test
    public void testCreateReactionScheme_IReactionSet() {
        IAtomContainer molA = builder.newInstance(IAtomContainer.class);
        molA.setID("A");
        IAtomContainer molB = builder.newInstance(IAtomContainer.class);
        molB.setID("B");
        IAtomContainer molC = builder.newInstance(IAtomContainer.class);
        molC.setID("C");
        IAtomContainer molD = builder.newInstance(IAtomContainer.class);
        molD.setID("D");
        IAtomContainer molE = builder.newInstance(IAtomContainer.class);
        molE.setID("E");

        IReactionSet reactionSet = builder.newInstance(IReactionSet.class);
        IReaction reaction1 = builder.newInstance(IReaction.class);
        reaction1.setID("r1");
        reaction1.addReactant(molA);
        reaction1.addProduct(molB);
        reactionSet.addReaction(reaction1);

        IReaction reaction2 = builder.newInstance(IReaction.class);
        reaction2.setID("r2");
        reaction2.addReactant(molB);
        reaction2.addProduct(molC);
        reactionSet.addReaction(reaction2);

        IReaction reaction3 = builder.newInstance(IReaction.class);
        reaction3.setID("r3");
        reaction3.addReactant(molB);
        reaction3.addProduct(molD);
        reactionSet.addReaction(reaction3);

        IReaction reaction4 = builder.newInstance(IReaction.class);
        reaction4.setID("r4");
        reaction4.addReactant(molC);
        reaction4.addProduct(molE);
        reactionSet.addReaction(reaction4);

        IReactionScheme scheme1 = ReactionSchemeManipulator.createReactionScheme(reactionSet);
        Assert.assertEquals(1, scheme1.getReactionCount());
        Assert.assertEquals("r1", scheme1.getReaction(0).getID());
        Assert.assertEquals(1, scheme1.getReactionSchemeCount());

        Iterator<IReactionScheme> iter = scheme1.reactionSchemes().iterator();
        IReactionScheme scheme2 = iter.next();
        Assert.assertEquals(2, scheme2.getReactionCount());
        Assert.assertEquals("r2", scheme2.getReaction(0).getID());
        Assert.assertEquals("r3", scheme2.getReaction(1).getID());
        Assert.assertEquals(1, scheme2.getReactionSchemeCount());

        Iterator<IReactionScheme> iter2 = scheme2.reactionSchemes().iterator();
        IReactionScheme scheme3 = iter2.next();
        Assert.assertEquals(1, scheme3.getReactionCount());
        Assert.assertEquals("r4", scheme3.getReaction(0).getID());
        Assert.assertEquals(0, scheme3.getReactionSchemeCount());

    }

    @Test
    public void testGetMoleculeSet_IAtomContainer_IAtomContainer_IReactionScheme() {
        IReactionScheme scheme1 = builder.newInstance(IReactionScheme.class);

        IReactionScheme scheme11 = builder.newInstance(IReactionScheme.class);
        IReaction reaction1 = builder.newInstance(IReaction.class);
        reaction1.setID("reaction1");
        IAtomContainer startMol = builder.newInstance(IAtomContainer.class);
        startMol.setID("startMol");
        reaction1.addReactant(startMol);
        IAtomContainer mitMol = builder.newInstance(IAtomContainer.class);
        mitMol.setID("mitMol");
        reaction1.addProduct(mitMol);
        scheme11.addReaction(reaction1);
        IReaction reaction2 = builder.newInstance(IReaction.class);
        reaction2.addProduct(builder.newInstance(IAtomContainer.class));
        reaction2.addReactant(builder.newInstance(IAtomContainer.class));
        reaction2.setID("reaction2");
        scheme11.addReaction(reaction2);
        scheme1.add(scheme11);

        IReactionScheme scheme12 = builder.newInstance(IReactionScheme.class);
        IReaction reaction3 = builder.newInstance(IReaction.class);
        IAtomContainer finalMol = builder.newInstance(IAtomContainer.class);
        finalMol.setID("finalMol");
        reaction3.addProduct(finalMol);
        reaction3.addReactant(mitMol);
        reaction3.setID("reaction3");
        scheme12.addReaction(reaction3);
        scheme1.add(scheme12);

        IReaction reaction11 = builder.newInstance(IReaction.class);
        reaction11.addProduct(builder.newInstance(IAtomContainer.class));
        reaction11.setID("reaction11");
        scheme1.addReaction(reaction11);

        ArrayList<IAtomContainerSet> listSet = ReactionSchemeManipulator.getAtomContainerSet(startMol, finalMol,
                scheme1);
        Assert.assertEquals(1, listSet.size());
        IAtomContainerSet moleculeSet = listSet.get(0);
        Assert.assertEquals("startMol", moleculeSet.getAtomContainer(0).getID());
        Assert.assertEquals("mitMol", moleculeSet.getAtomContainer(1).getID());
        Assert.assertEquals("finalMol", moleculeSet.getAtomContainer(2).getID());
    }

    @Test
    public void testGetAllReactions_IReactionScheme() {
        IReactionScheme scheme1 = builder.newInstance(IReactionScheme.class);

        IReactionScheme scheme11 = builder.newInstance(IReactionScheme.class);
        IReaction reaction1 = builder.newInstance(IReaction.class);
        IAtomContainer startMol = builder.newInstance(IAtomContainer.class);
        startMol.setID("startMol");
        reaction1.addReactant(startMol);
        IAtomContainer mitMol = builder.newInstance(IAtomContainer.class);
        mitMol.setID("mitMol");
        reaction1.addProduct(mitMol);
        scheme11.addReaction(reaction1);
        IReaction reaction2 = builder.newInstance(IReaction.class);
        reaction2.addProduct(builder.newInstance(IAtomContainer.class));
        reaction2.addReactant(builder.newInstance(IAtomContainer.class));
        scheme11.addReaction(reaction2);
        scheme1.add(scheme11);

        IReactionScheme scheme12 = builder.newInstance(IReactionScheme.class);
        IReaction reaction3 = builder.newInstance(IReaction.class);
        IAtomContainer finalMol = builder.newInstance(IAtomContainer.class);
        finalMol.setID("finalMol");
        reaction3.addProduct(finalMol);
        reaction3.addReactant(startMol);
        scheme12.addReaction(reaction3);
        scheme1.add(scheme12);

        IReaction reaction11 = builder.newInstance(IReaction.class);
        reaction11.addProduct(builder.newInstance(IAtomContainer.class));
        scheme1.addReaction(reaction11);

        IReactionSet reactionSet = ReactionSchemeManipulator.getAllReactions(scheme1);
        Assert.assertEquals(4, reactionSet.getReactionCount());
        Assert.assertEquals(reaction1, reactionSet.getReaction(0));
        Assert.assertEquals(reaction2, reactionSet.getReaction(1));
        Assert.assertEquals(reaction3, reactionSet.getReaction(2));
        Assert.assertEquals(reaction11, reactionSet.getReaction(3));
    }

    @Test
    public void testExtractTopReactions_IReactionScheme() {
        IReactionScheme scheme1 = builder.newInstance(IReactionScheme.class);
        IReaction reaction1 = builder.newInstance(IReaction.class);
        IAtomContainer molA = builder.newInstance(IAtomContainer.class);
        reaction1.addReactant(molA);
        IAtomContainer molB = builder.newInstance(IAtomContainer.class);
        reaction1.addProduct(molB);
        scheme1.addReaction(reaction1);

        IReactionScheme scheme2 = builder.newInstance(IReactionScheme.class);
        IReaction reaction2 = builder.newInstance(IReaction.class);
        reaction2.addReactant(molB);
        IAtomContainer molC = builder.newInstance(IAtomContainer.class);
        reaction2.addProduct(molC);
        scheme2.addReaction(reaction2);

        IReaction reaction3 = builder.newInstance(IReaction.class);
        reaction3.addReactant(molB);
        IAtomContainer molD = builder.newInstance(IAtomContainer.class);
        reaction3.addProduct(molD);
        scheme2.addReaction(reaction3);

        IReaction reaction4 = builder.newInstance(IReaction.class);
        IAtomContainer molE = builder.newInstance(IAtomContainer.class);
        reaction4.addReactant(molE);
        IAtomContainer molF = builder.newInstance(IAtomContainer.class);
        reaction4.addProduct(molF);
        scheme1.addReaction(reaction4);

        IReactionSet reactionSet = ReactionSchemeManipulator.extractTopReactions(scheme1);
        Assert.assertEquals(2, reactionSet.getReactionCount());
        Assert.assertEquals(reaction1, reactionSet.getReaction(0));
        Assert.assertEquals(reaction4, reactionSet.getReaction(1));
    }
}
