/* Copyright (C) 2011  Egon Willighagen <egonw@users.sourceforge.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. All we ask is that proper credit is given for our work,
 * which includes - but is not limited to - adding the above copyright notice to
 * the beginning of your source code files, and to any copyright notice that you
 * may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.renderer;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.silent.AtomContainer;

/**
 * @cdk.module test-renderbasic
 */
public class BoundsCalculatorTest {

    /**
     * Test if we get the expected {@link IllegalArgumentException} when we pass
     * an {@link IAtomContainer} without 2D coordinates.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCalculateBounds_IAtomContainer_SingleAtom() {
        IAtomContainer container = new AtomContainer();
        container.addAtom(container.getBuilder().newInstance(IAtom.class, "C"));
        BoundsCalculator.calculateBounds(container);
    }

    /**
     * Test if we get the expected {@link IllegalArgumentException} when we pass
     * an {@link IAtomContainer} without 2D coordinates.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCalculateBounds_IAtomContainer() {
        IAtomContainer container = new AtomContainer();
        container.addAtom(container.getBuilder().newInstance(IAtom.class, "C"));
        container.addAtom(container.getBuilder().newInstance(IAtom.class, "C"));
        BoundsCalculator.calculateBounds(container);
    }

    /**
     * Test if we get the expected {@link IllegalArgumentException} when we pass
     * an {@link IAtomContainer} without 2D coordinates.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCalculateBounds_IAtomContainerSet_SingleAtom() {
        IAtomContainer container = new AtomContainer();
        container.addAtom(container.getBuilder().newInstance(IAtom.class, "C"));
        IAtomContainerSet set = container.getBuilder().newInstance(IAtomContainerSet.class);
        set.addAtomContainer(container);
        BoundsCalculator.calculateBounds(set);
    }

    /**
     * Test if we get the expected {@link IllegalArgumentException} when we pass
     * an {@link IAtomContainer} without 2D coordinates.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCalculateBounds_IAtomContainerSet() {
        IAtomContainer container = new AtomContainer();
        container.addAtom(container.getBuilder().newInstance(IAtom.class, "C"));
        container.addAtom(container.getBuilder().newInstance(IAtom.class, "C"));
        IAtomContainerSet set = container.getBuilder().newInstance(IAtomContainerSet.class);
        set.addAtomContainer(container);
        BoundsCalculator.calculateBounds(set);
    }

    /**
     * Test if we get the expected {@link IllegalArgumentException} when we pass
     * an {@link IAtomContainer} without 2D coordinates.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCalculateBounds_IReactionSet_SingleAtom() {
        IAtomContainer container = new AtomContainer();
        container.addAtom(container.getBuilder().newInstance(IAtom.class, "C"));
        IReaction reaction = container.getBuilder().newInstance(IReaction.class);
        reaction.addReactant(container.getBuilder().newInstance(IAtomContainer.class, container));
        IReactionSet set = container.getBuilder().newInstance(IReactionSet.class);
        set.addReaction(reaction);
        BoundsCalculator.calculateBounds(set);
    }

    /**
     * Test if we get the expected {@link IllegalArgumentException} when we pass
     * an {@link IAtomContainer} without 2D coordinates.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCalculateBounds_IReactionSet() {
        IAtomContainer container = new AtomContainer();
        container.addAtom(container.getBuilder().newInstance(IAtom.class, "C"));
        container.addAtom(container.getBuilder().newInstance(IAtom.class, "C"));
        IReaction reaction = container.getBuilder().newInstance(IReaction.class);
        reaction.addReactant(container.getBuilder().newInstance(IAtomContainer.class, container));
        IReactionSet set = container.getBuilder().newInstance(IReactionSet.class);
        set.addReaction(reaction);
        BoundsCalculator.calculateBounds(set);
    }

    /**
     * Test if we get the expected {@link IllegalArgumentException} when we pass
     * an {@link IAtomContainer} without 2D coordinates.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCalculateBounds_IChemModel_SingleAtom() {
        IAtomContainer container = new AtomContainer();
        container.addAtom(container.getBuilder().newInstance(IAtom.class, "C"));
        IAtomContainerSet set = container.getBuilder().newInstance(IAtomContainerSet.class);
        set.addAtomContainer(container);
        IChemModel model = container.getBuilder().newInstance(IChemModel.class);
        model.setMoleculeSet(set);
        BoundsCalculator.calculateBounds(model);
    }

    /**
     * Test if we get the expected {@link IllegalArgumentException} when we pass
     * an {@link IAtomContainer} without 2D coordinates.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCalculateBounds_IChemModel() {
        IAtomContainer container = new AtomContainer();
        container.addAtom(container.getBuilder().newInstance(IAtom.class, "C"));
        container.addAtom(container.getBuilder().newInstance(IAtom.class, "C"));
        IAtomContainerSet set = container.getBuilder().newInstance(IAtomContainerSet.class);
        set.addAtomContainer(container);
        IChemModel model = container.getBuilder().newInstance(IChemModel.class);
        model.setMoleculeSet(set);
        BoundsCalculator.calculateBounds(model);
    }

    /**
     * Test if we get the expected {@link IllegalArgumentException} when we pass
     * an {@link IAtomContainer} without 2D coordinates.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCalculateBounds_IReaction_SingleAtom() {
        IAtomContainer container = new AtomContainer();
        container.addAtom(container.getBuilder().newInstance(IAtom.class, "C"));
        IReaction reaction = container.getBuilder().newInstance(IReaction.class);
        reaction.addReactant(container.getBuilder().newInstance(IAtomContainer.class, container));
        BoundsCalculator.calculateBounds(reaction);
    }

    /**
     * Test if we get the expected {@link IllegalArgumentException} when we pass
     * an {@link IAtomContainer} without 2D coordinates.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCalculateBounds_IReaction() {
        IAtomContainer container = new AtomContainer();
        container.addAtom(container.getBuilder().newInstance(IAtom.class, "C"));
        container.addAtom(container.getBuilder().newInstance(IAtom.class, "C"));
        IReaction reaction = container.getBuilder().newInstance(IReaction.class);
        reaction.addReactant(container.getBuilder().newInstance(IAtomContainer.class, container));
        BoundsCalculator.calculateBounds(reaction);
    }
}
