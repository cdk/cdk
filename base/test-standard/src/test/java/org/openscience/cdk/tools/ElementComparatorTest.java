/* Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.tools;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.tools.ElementComparator;

/**
 * @cdk.module test-standard
 */
public class ElementComparatorTest extends CDKTestCase {

    public ElementComparatorTest() {
        super();
    }

    @Test
    public void testElementComparator() {
        ElementComparator comp = new ElementComparator();
        Assert.assertNotNull(comp);
    }

    /**
     * @cdk.bug 1638375
     */
    @Test
    public void testCompare_Object_Object() {
        ElementComparator comp = new ElementComparator();

        Assert.assertTrue(comp.compare("C", "H") < 0);
        Assert.assertTrue(comp.compare("H", "O") < 0);
        Assert.assertTrue(comp.compare("N", "O") < 0);
        Assert.assertEquals(0, comp.compare("Cl", "Cl"));
        Assert.assertTrue(comp.compare("Cl", "C") > 0);
    }

}
