/* Copyright (C) 2013  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.isomorphism.matchers.smarts;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.Bond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

import static org.mockito.Mockito.mock;

/**
 * Checks the functionality of the IsomorphismTester
 *
 * @cdk.module test-smarts
 */
public class AnyOrderQueryBondTest extends CDKTestCase {

    /**
     * @cdk.bug 1305
     */
    @Test
    public void testMatches() {
        IBond testBond = null;
        AnyOrderQueryBond matcher = new AnyOrderQueryBond(mock(IChemObjectBuilder.class));
        Assert.assertFalse(matcher.matches(testBond));
    }

    @Test
    public void testAnyOrder() {
        AnyOrderQueryBond matcher = new AnyOrderQueryBond(mock(IChemObjectBuilder.class));
        IBond testBond = new Bond();
        for (IBond.Order order : IBond.Order.values()) {
            testBond.setOrder(order);
            Assert.assertTrue(matcher.matches(testBond));
        }
    }
}
