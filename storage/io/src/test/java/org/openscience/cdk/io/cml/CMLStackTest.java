/* Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.io.cml;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;

import static org.hamcrest.CoreMatchers.is;

/**
 * TestCase for the CMLStack class.
 *
 */
class CMLStackTest extends CDKTestCase {

    @Test
    void testPush_String() {
        // the class has a hardcoded default length. Test going beyond this.
        CMLStack stack = new CMLStack();
        for (int i = 0; i < 100; i++) {
            stack.push("element");
        }
    }

    @Test
    void testPop() {
        CMLStack stack = new CMLStack();
        stack.push("first");
        stack.push("second");
        stack.push("third");
        Assertions.assertEquals("third", stack.pop());
        Assertions.assertEquals("second", stack.pop());
        Assertions.assertEquals("first", stack.pop());
        try {
            Assertions.assertEquals("doesNotExist", stack.pop());
            Assertions.fail("Should have received an ArrayIndexOutOfBoundsException");
        } catch (Exception exception) {
            // OK, should happen
        }
    }

    @Test
    void testCurrent() {
        CMLStack stack = new CMLStack();
        stack.push("first");
        Assertions.assertEquals("first", stack.current());
        stack.push("second");
        Assertions.assertEquals("second", stack.current());
        stack.push("third");
        Assertions.assertEquals("third", stack.current());
        stack.pop();
        Assertions.assertEquals("second", stack.current());
        stack.pop();
        Assertions.assertEquals("first", stack.current());
    }

    @Test
    void testEndsWith_String() {
        CMLStack stack = new CMLStack();
        stack.push("first");
        Assertions.assertTrue(stack.endsWith("first"));
        stack.push("second");
        Assertions.assertFalse(stack.endsWith("first"));
        Assertions.assertTrue(stack.endsWith("second"));
        stack.push("third");
        Assertions.assertTrue(stack.endsWith("third"));
    }

    @Test
    void testEndsWith_String_String() {
        CMLStack stack = new CMLStack();
        stack.push("first");
        stack.push("second");
        Assertions.assertFalse(stack.endsWith("second", "first"));
        Assertions.assertTrue(stack.endsWith("first", "second"));
        stack.push("third");
        Assertions.assertTrue(stack.endsWith("second", "third"));
    }

    @Test
    void testEndsWith_String_String_String() {
        CMLStack stack = new CMLStack();
        stack.push("first");
        stack.push("second");
        stack.push("third");
        Assertions.assertTrue(stack.endsWith("first", "second", "third"));
    }

    @Test
    void testSize() {
        CMLStack stack = new CMLStack();
        org.hamcrest.MatcherAssert.assertThat(stack.size(), is(0));
        stack.push("first");
        org.hamcrest.MatcherAssert.assertThat(stack.size(), is(1));
        stack.push("second");
        org.hamcrest.MatcherAssert.assertThat(stack.size(), is(2));
        stack.push("third");
        org.hamcrest.MatcherAssert.assertThat(stack.size(), is(3));
    }
}
