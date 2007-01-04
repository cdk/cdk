/* $Revision: 6720 $ $Author: egonw $ $Date: 2006-08-01 21:49:30 +0200 (Tue, 01 Aug 2006) $
 *
 * Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.test.io.cml;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.io.cml.CMLStack;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;

/**
 * TestCase for the CMLStack class.
 *
 * @cdk.module test-io
 */
public class CMLStackTest extends CDKTestCase {

    private LoggingTool logger = new LoggingTool(this);

    public CMLStackTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(CMLStackTest.class);
    }

    public void testPush_String() {
    	// the class has a hardcoded default length. Test going beyond this.
    	CMLStack stack = new CMLStack(); 
    	for (int i=0; i<100; i++) {
    		stack.push("element");
    	}
    }
    
    public void testPop() {
    	CMLStack stack = new CMLStack();
    	stack.push("first");
    	stack.push("second");
    	stack.push("third");
    	assertEquals("third", stack.pop());
    	assertEquals("second", stack.pop());
    	assertEquals("first", stack.pop());
    	try {
    		assertEquals("doesNotExist", stack.pop());
    		fail("Should have received an ArrayIndexOutOfBoundsException");
    	} catch (Exception exception) {
    		// OK, should happen
    	}
    }
    
    public void testCurrent() {
    	CMLStack stack = new CMLStack();
    	stack.push("first");
    	assertEquals("first", stack.current());
    	stack.push("second");
    	assertEquals("second", stack.current());
    	stack.push("third");
    	assertEquals("third", stack.current());
    	stack.pop();
    	assertEquals("second", stack.current());
    	stack.pop();
    	assertEquals("first", stack.current());
    }

    public void testEndsWith_String() {
    	CMLStack stack = new CMLStack();
    	stack.push("first");
    	assertTrue(stack.endsWith("first"));
    	stack.push("second");
    	assertFalse(stack.endsWith("first"));
    	assertTrue(stack.endsWith("second"));
    	stack.push("third");
    	assertTrue(stack.endsWith("third"));
    }
    
    public void testEndsWith_String_String() {
    	CMLStack stack = new CMLStack();
    	stack.push("first");
    	stack.push("second");
    	assertFalse(stack.endsWith("second", "first"));
    	assertTrue(stack.endsWith("first", "second"));
    	stack.push("third");
    	assertTrue(stack.endsWith("second", "third"));
    }

    public void testEndsWith_String_String_String() {
    	CMLStack stack = new CMLStack();
    	stack.push("first");
    	stack.push("second");
    	stack.push("third");
    	assertTrue(stack.endsWith("first", "second", "third"));
    }
}
