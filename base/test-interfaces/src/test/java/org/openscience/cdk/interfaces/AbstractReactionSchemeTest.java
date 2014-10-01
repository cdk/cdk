/* Copyright (C) 2007  Miguel Rojasch <miguelrojasch@users.sf.net>
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
 *
 */
package org.openscience.cdk.interfaces;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

/**
 * Checks the functionality of {@link IReactionScheme} implementations.
 *
 * @cdk.module test-interfaces
 */
public abstract class AbstractReactionSchemeTest extends AbstractReactionSetTest {

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
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
     * @return    The test suite
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
     * @return    The test suite
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
     * @return    The test suite
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
        for (Iterator<IReaction> it = scheme.reactions().iterator(); it.hasNext();) {
            it.next();
            ++count;
        }
        Assert.assertEquals(3, count);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
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
     * @return    The test suite
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
     * @return    The test suite
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
     * @return    The test suite
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
     * @return    The test suite
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
