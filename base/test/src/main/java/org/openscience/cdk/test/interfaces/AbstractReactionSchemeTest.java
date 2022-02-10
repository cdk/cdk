/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.interfaces;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionScheme;

/**
 * Checks the functionality of {@link org.openscience.cdk.interfaces.IReactionScheme} implementations.
 *
 * @cdk.module test-interfaces
 */
public abstract class AbstractReactionSchemeTest extends AbstractReactionSetTest {

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testGetReactionSchemeCount() {
        IReactionScheme scheme = (IReactionScheme) newChemObject();
        scheme.add(scheme.getBuilder().newInstance(IReactionScheme.class));
        Assert.assertEquals(1, scheme.getReactionSchemeCount());
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    @Override
    public void testGetReactionCount() {
        IReactionScheme scheme = (IReactionScheme) newChemObject();
        scheme.addReaction(scheme.getBuilder().newInstance(IReaction.class));
        scheme.addReaction(scheme.getBuilder().newInstance(IReaction.class));
        Assert.assertEquals(2, scheme.getReactionCount());
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testReactionSchemes() {
        IReactionScheme scheme = (IReactionScheme) newChemObject();
        scheme.add(scheme.getBuilder().newInstance(IReactionScheme.class));
        scheme.add(scheme.getBuilder().newInstance(IReactionScheme.class));
        scheme.add(scheme.getBuilder().newInstance(IReactionScheme.class));

        Assert.assertEquals(3, scheme.getReactionSchemeCount());
        int count = 0;
        for (IReactionScheme sch : scheme.reactionSchemes()) {
            sch.getClass();
            ++count;
        }
        Assert.assertEquals(3, count);
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    @Override
    public void testReactions() {
        IReactionScheme scheme = (IReactionScheme) newChemObject();
        scheme.addReaction(scheme.getBuilder().newInstance(IReaction.class));
        scheme.addReaction(scheme.getBuilder().newInstance(IReaction.class));
        scheme.addReaction(scheme.getBuilder().newInstance(IReaction.class));

        Assert.assertEquals(3, scheme.getReactionCount());
        int count = 0;
        for (IReaction iReaction : scheme.reactions()) {
            ++count;
        }
        Assert.assertEquals(3, count);
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testAdd_IReactionScheme() {
        IReactionScheme scheme = (IReactionScheme) newChemObject();
        scheme.add(scheme.getBuilder().newInstance(IReactionScheme.class));
        scheme.add(scheme.getBuilder().newInstance(IReactionScheme.class));
        scheme.add(scheme.getBuilder().newInstance(IReactionScheme.class));

        IReactionScheme tested = scheme.getBuilder().newInstance(IReactionScheme.class);
        Assert.assertEquals(0, tested.getReactionSchemeCount());
        tested.add(scheme);
        Assert.assertEquals(1, tested.getReactionSchemeCount());
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testAdd_IReaction() {
        IReactionScheme scheme = (IReactionScheme) newChemObject();
        scheme.add(scheme.getBuilder().newInstance(IReactionScheme.class));
        scheme.add(scheme.getBuilder().newInstance(IReactionScheme.class));
        scheme.add(scheme.getBuilder().newInstance(IReactionScheme.class));

        IReactionScheme tested = scheme.getBuilder().newInstance(IReactionScheme.class);
        Assert.assertEquals(0, tested.getReactionSchemeCount());
        tested.add(scheme);
        Assert.assertEquals(1, tested.getReactionSchemeCount());
        Assert.assertEquals(0, tested.getReactionCount());
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    @Override
    public void testClone() throws Exception {
        IReactionScheme scheme = (IReactionScheme) newChemObject();
        Object clone = scheme.clone();
        Assert.assertTrue(clone instanceof IReactionScheme);
        Assert.assertNotSame(scheme, clone);
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testRemoveReactionScheme_IReactionScheme() {
        IReactionScheme scheme = (IReactionScheme) newChemObject();
        IReactionScheme scheme1 = (IReactionScheme) newChemObject();
        IReactionScheme scheme2 = (IReactionScheme) newChemObject();
        scheme.add(scheme1);
        scheme.add(scheme2);
        scheme.removeReactionScheme(scheme1);
        Assert.assertEquals(1, scheme.getReactionSchemeCount());
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    public void testRemoveAllReactionSchemes() {
        IReactionScheme scheme = (IReactionScheme) newChemObject();
        IReactionScheme scheme1 = (IReactionScheme) newChemObject();
        IReactionScheme scheme2 = (IReactionScheme) newChemObject();
        scheme.add(scheme1);
        scheme.add(scheme2);

        Assert.assertEquals(2, scheme.getReactionSchemeCount());
        scheme.removeAllReactionSchemes();
        Assert.assertEquals(0, scheme.getReactionSchemeCount());
    }
}
