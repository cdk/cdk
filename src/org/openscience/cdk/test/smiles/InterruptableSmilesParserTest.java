/* $Revision: 7636 $ $Author: egonw $ $Date: 2007-01-04 18:46:10 +0100 (Thu, 04 Jan 2007) $
 *
 * Copyright (C) 2007  Nina Jeliazkova <nina@acad.bg>
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
package org.openscience.cdk.test.smiles;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.smiles.InterruptableSmilesParser;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @author       Nina Jeliazkova
 * @cdk.module   test-smiles
 * @cdk.created  2007-02-02
 * 
 * @see org.openscience.cdk.test.gui.SmilesParserTest
 */
public class InterruptableSmilesParserTest extends CDKTestCase {
	
	private InterruptableSmilesParser sp;

	public InterruptableSmilesParserTest(String name) {
		super(name);
	}

	public void setUp() {
		sp = new InterruptableSmilesParser(NoNotificationChemObjectBuilder.getInstance());
	}

	public static Test suite() {
		return new TestSuite(InterruptableSmilesParserTest.class);
	}
	
	public void testParseSmiles_String_long() {
		try {
			sp.parseSmiles("CCCC",10000);
		} catch (CDKException x) {
			fail("Time out should not occur for this SMILES!");
		}
	}
	
	public void testSetInterrupted_boolean() {
		//This should throw exception
		try {
			sp.parseSmiles("n(c(c(c(Nc(c(c(nc(c(c(Nc(c1c(cccc2)c2)c3)c3)c(cccc4)c4)c5)c5)c(cccc6)c6)c7)c7)c(cccc8)c8)cc9)c19",3000);
			fail("This SMILES should have given an time out");
		} catch (CDKException x) {
		  x.printStackTrace();
		}
	}
	
}

