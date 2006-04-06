/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2006  The Chemistry Development Kit (CKD) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
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
package org.openscience.cdk.test.tools;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.FormatStringBuffer;

/**
 * @author     egonw
 * @cdk.module test-standard
 */
public class FormatStringBufferTest extends CDKTestCase {

	private FormatStringBuffer fsb;
	
	public FormatStringBufferTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(FormatStringBufferTest.class);
	}
	
	public void setUp() {
		fsb = new FormatStringBuffer("[%s]");
	}

	public void testString() {
		fsb.reset("[%s]").format("test");
		assertEquals("[test]", fsb.toString());
		
		fsb.reset("[%5s]").format("test");
		assertEquals("[ test]", fsb.toString());
		
		fsb.reset("[%-5s]").format("test");
		assertEquals("[test ]", fsb.toString());
		
		fsb.reset("[%5.2s]").format("test");
		assertEquals("[   te]", fsb.toString());
		
		fsb.reset("[%-5.2s]").format("test");
		assertEquals("[te   ]", fsb.toString());
	}
	
	public void testChar() {
		fsb.reset("[%c]").format('A');
		assertEquals("[A]", fsb.toString());
		
		fsb.reset("[%2c]").format('A');
		assertEquals("[ A]", fsb.toString());
		
		fsb.reset("[%-2c]").format('A');
		assertEquals("[A ]", fsb.toString());
	}
	
	public void testFloat() {
		fsb.reset("[%f]").format(3.1415);
		assertEquals("[3.1415]", fsb.toString());
		
		fsb.reset("[%g]").format(3.1415);
		assertEquals("[3.1415]", fsb.toString());
		
		fsb.reset("[%+f]").format(3.1415);
		assertEquals("[+3.1415]", fsb.toString());
		
		fsb.reset("[%+10f]").format(3.1415);
		assertEquals("[   +3.1415]", fsb.toString());
		
		fsb.reset("[%-+10f]").format(3.1415);
		assertEquals("[+3.1415   ]", fsb.toString());
		
		fsb.reset("[%.3f]").format(3.1415);
		assertEquals("[3.142]", fsb.toString());
		
		fsb.reset("[%e]").format(3.1415);
		assertEquals("[3.1415e00]", fsb.toString());
		
		fsb.reset("[%+e]").format(3.1415);
		assertEquals("[+3.1415e00]", fsb.toString());
		
		fsb.reset("[%+11e]").format(3.1415);
		assertEquals("[ +3.1415e00]", fsb.toString());
		
		fsb.reset("[%-+11e]").format(3.1415);
		assertEquals("[+3.1415e00 ]", fsb.toString());
		
		fsb.reset("[%.3e]").format(3.1415);
		assertEquals("[3.142e00]", fsb.toString());
		
		fsb.reset("[%E]").format(3.1415);
		assertEquals("[3.1415E00]", fsb.toString());		
	}
	
	public void testDecimal() {
		fsb.reset("[%d]").format(600);
		assertEquals("[600]", fsb.toString());
		
		fsb.reset("[%5d]").format(600);
		assertEquals("[  600]", fsb.toString());
		
		fsb.reset("[%5d]").format(-600);
		assertEquals("[ -600]", fsb.toString());
		
		fsb.reset("[%05d]").format(600);
		assertEquals("[00600]", fsb.toString());
		
		fsb.reset("[%05d]").format(-600);
		assertEquals("[-0600]", fsb.toString());
		
		fsb.reset("[%x]").format(10);
		assertEquals("[a]", fsb.toString());
		
		fsb.reset("[%X]").format(10);
		assertEquals("[A]", fsb.toString());
		
		fsb.reset("[%o]").format(10);
		assertEquals("[12]", fsb.toString());
		
		fsb.reset("[%4X]").format(10);
		assertEquals("[   A]", fsb.toString());
		
		fsb.reset("[%#4x]").format(10);
		assertEquals("[ 0xa]", fsb.toString());
		
		fsb.reset("[%#4o]").format(10);
		assertEquals("[ 012]", fsb.toString());
		
		fsb.reset("[%#04x]").format(10);
		assertEquals("[0x0a]", fsb.toString());
		
		fsb.reset("[%#04o]").format(10);
		assertEquals("[0012]", fsb.toString());
		
		fsb.reset();
		assertEquals("[%#04o]", fsb.toString());		
	}
}
