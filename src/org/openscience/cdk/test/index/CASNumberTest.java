/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2003-2006  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.test.index;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.index.CASNumber;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Tests CDK's CAS Number class.
 *
 * @cdk.module test
 *
 * @author     Egon Willighagen <egonw@sci.kun.nl>
 * @author Nathana&euml;l "M.Le_maudit" Mazuir
 *
 * @cdk.created    2003-07-01
 * @cdk.require java1.4+
 */
public class CASNumberTest extends CDKTestCase {

    public CASNumberTest(String name) {
        super(name);
    }

    /**
     * The JUnit setup method
     */
    public void setUp() {}

    /**
     * A unit test suite for JUnit
     *
     * @return    The test suite
     */
    public static Test suite() {
        return new TestSuite(CASNumberTest.class);
    }

    public void testValidNumbers() {
		// valid cas numbers
		assertTrue(CASNumber.isValid("50-00-0")); // formaldehyde
		assertTrue(CASNumber.isValid("548-00-5"));
		assertTrue(CASNumber.isValid("2622-26-6"));
		assertTrue(CASNumber.isValid("15299-99-7"));
		assertTrue(CASNumber.isValid("673434-32-7"));
    }

    public void testInvalidCheckDigits() {
		// invalid R value		
		assertFalse(CASNumber.isValid("50-00-1"));
		assertFalse(CASNumber.isValid("50-00-2"));
		assertFalse(CASNumber.isValid("50-00-3"));
		assertFalse(CASNumber.isValid("50-00-4"));
		assertFalse(CASNumber.isValid("50-00-5"));
		assertFalse(CASNumber.isValid("50-00-6"));
		assertFalse(CASNumber.isValid("50-00-7"));
		assertFalse(CASNumber.isValid("50-00-8"));
		assertFalse(CASNumber.isValid("50-00-9"));
    }
    
    public void testLargerThanFirst() {
		// valid format, but wrong number, the first is 50-00-0 
	assertFalse(CASNumber.isValid("3-21-4"));
    }

    public void testWrongHyphenPositions() {
		// invalid format due to invalid hyphen positions 
		assertFalse(CASNumber.isValid("3-21-40"));
		assertFalse(CASNumber.isValid("3-210-4"));
		assertFalse(CASNumber.isValid("03-1-4"));
		assertFalse(CASNumber.isValid("03-21-"));
    }

    public void testInvalidCharacters() {
		// invalid characters
		assertFalse(CASNumber.isValid("a-21-4"));
		assertFalse(CASNumber.isValid("3-a1-4"));
		assertFalse(CASNumber.isValid("3-2a-4"));
		assertFalse(CASNumber.isValid("3-21-a"));
		assertFalse(CASNumber.isValid("d-cb-a"));
    }

    public void testSanity() {
		// completely stupid value
		assertFalse(CASNumber.isValid("0&z003-!0>/-0a"));
    }

    public void testCharacterSet() {
		// invalid value even with the '0' unicode character '\u0030' 
		assertFalse(CASNumber.isValid("\u0030-21-4"));		
    }
}

