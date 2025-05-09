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
package org.openscience.cdk.qsar.result;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 */
class IntegerResultTest {

    IntegerResultTest() {
        super();
    }

    @Test
    void testIntegerResult_int() {
        IntegerResult result = new IntegerResult(5);
        Assertions.assertNotNull(result);
    }

    @Test
    void testToString() {
        IntegerResult result = new IntegerResult(5);
        Assertions.assertEquals("5", result.toString());
    }

    @Test
    void testIntValue() {
        IntegerResult result = new IntegerResult(5);
        Assertions.assertEquals(5, result.intValue());
    }

    @Test
    void testLength() {
        IntegerResult result = new IntegerResult(5);
        Assertions.assertEquals(1, result.length());
    }
}
