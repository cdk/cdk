/* Copyright (C) 2008  Miguel Rojas <miguelrojasch@yahoo.es>
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
package org.openscience.cdk.reaction.mechanism;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.reaction.IReactionMechanism;
import org.openscience.cdk.reaction.ReactionMechanismTest;

/**
 * Tests for AdductionLPMechanism implementations.
 *
 * @cdk.module test-reaction
 */
public class AdductionLPMechanismTest extends ReactionMechanismTest {

    /**
     *  The JUnit setup method
     */
    @BeforeClass
    public static void setUp() throws Exception {
        setMechanism(AdductionLPMechanism.class);
    }

    /**
     *  Constructor for the AdductionLPMechanismTest object.
     */
    public AdductionLPMechanismTest() {
        super();
    }

    /**
     * Junit test.
     *
     * @throws Exception
     */
    @Test
    public void testAdductionLPMechanism() {
        IReactionMechanism mechanism = new AdductionLPMechanism();
        Assert.assertNotNull(mechanism);
    }

    /**
     * Junit test.
     * TODDO: REACT: add an example
     *
     * @throws Exception
     */
    @Test
    public void testInitiate_IAtomContainerSet_ArrayList_ArrayList() {
        IReactionMechanism mechanism = new AdductionLPMechanism();

        Assert.assertNotNull(mechanism);
    }
}
