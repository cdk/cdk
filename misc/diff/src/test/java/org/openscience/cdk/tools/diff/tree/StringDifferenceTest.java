/* Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.tools.diff.tree;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 */
class StringDifferenceTest {

    public static void assertOneLiner(String testString) {
        Assertions.assertNotNull(testString, "Expected a non-null String.");
        for (int i = 0; i < testString.length(); i++) {
            char c = testString.charAt(i);
            Assertions.assertNotSame('\n', c, "The String must not contain newline characters");
            Assertions.assertNotSame('\r', c, "The String must not contain newline characters");
        }
    }

    @Test
    void testDiff() {
        IDifference result = StringDifference.construct("Foo", "foo", "bar");
        Assertions.assertNotNull(result);
    }

    @Test
    void testSame() {
        IDifference result = StringDifference.construct("Foo", "foo", "foo");
        Assertions.assertNull(result);
    }

    @Test
    void testTwoNull() {
        IDifference result = StringDifference.construct("Foo", null, null);
        Assertions.assertNull(result);
    }

    @Test
    void testOneNull() {
        IDifference result = StringDifference.construct("Foo", null, "bar");
        Assertions.assertNotNull(result);

        result = StringDifference.construct("Foo", "bar", null);
        Assertions.assertNotNull(result);
    }

    @Test
    void testToString() {
        IDifference result = StringDifference.construct("Foo", null, "bar");
        String diffString = result.toString();
        Assertions.assertNotNull(diffString);
        assertOneLiner(diffString);
    }
}
