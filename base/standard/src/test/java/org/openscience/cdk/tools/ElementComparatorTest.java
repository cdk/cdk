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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 */
class ElementComparatorTest {

    ElementComparatorTest() {
        super();
    }

    @Test
    void testElementComparator() {
        ElementComparator comp = new ElementComparator();
        Assertions.assertNotNull(comp);
    }

    /**
     * @cdk.bug 1638375
     */
    @Test
    void testCompare_Object_Object() {
        ElementComparator comp = new ElementComparator();

        Assertions.assertTrue(comp.compare("C", "H") < 0);
        Assertions.assertTrue(comp.compare("H", "O") < 0);
        Assertions.assertTrue(comp.compare("N", "O") < 0);
        Assertions.assertEquals(0, comp.compare("Cl", "Cl"));
        Assertions.assertTrue(comp.compare("Cl", "C") > 0);
    }

}
