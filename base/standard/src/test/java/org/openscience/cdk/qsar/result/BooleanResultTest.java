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
class BooleanResultTest {

    BooleanResultTest() {
        super();
    }

    // well, these tests are not shocking...

    @Test
    void testBooleanResult_boolean() {
        BooleanResult result = new BooleanResult(true);
        Assertions.assertNotNull(result);
    }

    @Test
    void testBooleanValue() {
        Assertions.assertTrue(new BooleanResult(true).booleanValue());
        Assertions.assertFalse(new BooleanResult(false).booleanValue());
    }

    @Test
    void testToString() {
        Assertions.assertEquals("true", new BooleanResult(true).toString());
        Assertions.assertEquals("false", new BooleanResult(false).toString());
    }

}
