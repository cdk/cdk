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

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;

import static org.hamcrest.CoreMatchers.is;

/**
 * TestCase for the CMLStack class.
 *
 * @cdk.module test-io
 */
public class CMLStackTest extends CDKTestCase {

    @Test
    public void testPush_String() {
        // the class has a hardcoded default length. Test going beyond this.
        CMLStack stack = new CMLStack();
        for (int i = 0; i < 100; i++) {
            stack.push("element");
        }
    }

    @Test
    public void testPop() {
        CMLStack stack = new CMLStack();
        stack.push("first");
        stack.push("second");
        stack.push("third");
        Assert.assertEquals("third", stack.pop());
        Assert.assertEquals("second", stack.pop());
        Assert.assertEquals("first", stack.pop());
        try {
            Assert.assertEquals("doesNotExist", stack.pop());
            Assert.fail("Should have received an ArrayIndexOutOfBoundsException");
        } catch (Exception exception) {
            // OK, should happen
        }
    }

    @Test
    public void testCurrent() {
        CMLStack stack = new CMLStack();
        stack.push("first");
        Assert.assertEquals("first", stack.current());
        stack.push("second");
        Assert.assertEquals("second", stack.current());
        stack.push("third");
        Assert.assertEquals("third", stack.current());
        stack.pop();
        Assert.assertEquals("second", stack.current());
        stack.pop();
        Assert.assertEquals("first", stack.current());
    }

    @Test
    public void testEndsWith_String() {
        CMLStack stack = new CMLStack();
        stack.push("first");
        Assert.assertTrue(stack.endsWith("first"));
        stack.push("second");
        Assert.assertFalse(stack.endsWith("first"));
        Assert.assertTrue(stack.endsWith("second"));
        stack.push("third");
        Assert.assertTrue(stack.endsWith("third"));
    }

    @Test
    public void testEndsWith_String_String() {
        CMLStack stack = new CMLStack();
        stack.push("first");
        stack.push("second");
        Assert.assertFalse(stack.endsWith("second", "first"));
        Assert.assertTrue(stack.endsWith("first", "second"));
        stack.push("third");
        Assert.assertTrue(stack.endsWith("second", "third"));
    }

    @Test
    public void testEndsWith_String_String_String() {
        CMLStack stack = new CMLStack();
        stack.push("first");
        stack.push("second");
        stack.push("third");
        Assert.assertTrue(stack.endsWith("first", "second", "third"));
    }

    @Test
    public void testSize() {
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
