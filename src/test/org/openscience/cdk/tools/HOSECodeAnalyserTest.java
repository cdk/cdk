/* $Revision: 7997 $ $Author: egonw $ $Date: 2007-02-27 16:00:10 +0100 (Tue, 27 Feb 2007) $
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.tools;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HOSECodeAnalyser;

/**
 * @cdk.module test-extra
 */
public class HOSECodeAnalyserTest extends CDKTestCase {
	
	public HOSECodeAnalyserTest(String name) {
		super(name);
	}

	public void setUp() {}

	public static Test suite() {
		return new TestSuite(HOSECodeAnalyserTest.class);
	}

	public void testGetElements_String()	{
		List elements = HOSECodeAnalyser.getElements("CCY(CF,C,/C,,&/&)//");
		assertEquals(3, elements.size());
		assertTrue(elements.contains("C"));
		assertTrue(elements.contains("F"));
		assertTrue(elements.contains("Br"));
	}
    
	public void testCode1()	{
		List elements = HOSECodeAnalyser.getElements("*C*CC(*C,*C,=C/*C,*&,CC/*&O,=OO,%N),C,,C,/");
		assertEquals(3, elements.size());
		assertTrue(elements.contains("C"));
		assertTrue(elements.contains("O"));
		assertTrue(elements.contains("N"));
	}
    
}

