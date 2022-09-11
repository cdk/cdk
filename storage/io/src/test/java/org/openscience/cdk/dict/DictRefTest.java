/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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

package org.openscience.cdk.dict;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Checks the functionality of the DictRef class.
 *
 * @cdk.module test-standard
 *
 * @see org.openscience.cdk.dict.DictRef
 */
class DictRefTest extends CDKTestCase {

    DictRefTest() {
        super();
    }

    // test constructors

    @Test
    void testDictRef_String_String() {
        DictRef dictRef = new DictRef("bar:foo", "bla");
        Assertions.assertNotNull(dictRef);
    }

    @Test
    void testGetType() {
        DictRef dictRef = new DictRef("bar:foo", "bla");
        Assertions.assertEquals("bar:foo", dictRef.getType());
    }

    @Test
    void testGetDictRef() {
        DictRef dictRef = new DictRef("bar:foo", "bla");
        Assertions.assertEquals("bla", dictRef.getReference());
    }

    /** Test for RFC #9 */
    @Test
    void testToString() {
        DictRef dictRef = new DictRef("bar:foo", "bla");
        String description = dictRef.toString();
        for (int i = 0; i < description.length(); i++) {
            Assertions.assertTrue(description.charAt(i) != '\n');
            Assertions.assertTrue(description.charAt(i) != '\r');
        }
    }
}
