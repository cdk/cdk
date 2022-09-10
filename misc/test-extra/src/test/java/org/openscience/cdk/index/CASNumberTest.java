/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
 *                    2020  Egon Willighagen <egon.willighagen@gmail.com>
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
package org.openscience.cdk.index;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Tests CDK's CAS Number class.
 *
 * @cdk.module test-extra
 *
 * @author Egon Willighagen &lt;egonw@sci.kun.nl&gt;
 * @author Nathana&euml;l "M.Le_maudit" Mazuir
 *
 * @cdk.created    2003-07-01
 * @cdk.require java1.4+
 */
public class CASNumberTest extends CDKTestCase {

    @Test
    public void testValidNumbers() {
        // valid cas numbers
        Assertions.assertTrue(CASNumber.isValid("36-88-4"));
        Assertions.assertTrue(CASNumber.isValid("50-00-0")); // formaldehyde
        Assertions.assertTrue(CASNumber.isValid("548-00-5"));
        Assertions.assertTrue(CASNumber.isValid("2622-26-6"));
        Assertions.assertTrue(CASNumber.isValid("15299-99-7"));
        Assertions.assertTrue(CASNumber.isValid("673434-32-7"));
    }

    @Test
    public void testInvalidCheckDigits() {
        // invalid R value
        Assertions.assertFalse(CASNumber.isValid("50-00-1"));
        Assertions.assertFalse(CASNumber.isValid("50-00-2"));
        Assertions.assertFalse(CASNumber.isValid("50-00-3"));
        Assertions.assertFalse(CASNumber.isValid("50-00-4"));
        Assertions.assertFalse(CASNumber.isValid("50-00-5"));
        Assertions.assertFalse(CASNumber.isValid("50-00-6"));
        Assertions.assertFalse(CASNumber.isValid("50-00-7"));
        Assertions.assertFalse(CASNumber.isValid("50-00-8"));
        Assertions.assertFalse(CASNumber.isValid("50-00-9"));
    }

    @Test
    public void testWrongHyphenPositions() {
        // invalid format due to invalid hyphen positions
        Assertions.assertFalse(CASNumber.isValid("3-21-40"));
        Assertions.assertFalse(CASNumber.isValid("3-210-4"));
        Assertions.assertFalse(CASNumber.isValid("03-1-4"));
        Assertions.assertFalse(CASNumber.isValid("03-21-"));
    }

    @Test
    public void testInvalidCharacters() {
        // invalid characters
        Assertions.assertFalse(CASNumber.isValid("a-21-4"));
        Assertions.assertFalse(CASNumber.isValid("3-a1-4"));
        Assertions.assertFalse(CASNumber.isValid("3-2a-4"));
        Assertions.assertFalse(CASNumber.isValid("3-21-a"));
        Assertions.assertFalse(CASNumber.isValid("d-cb-a"));
    }

    @Test
    public void testSanity() {
        // completely stupid value
        Assertions.assertFalse(CASNumber.isValid("0&z003-!0>/-0a"));
    }

    @Test
    public void testCharacterSet() {
        // invalid value even with the '0' unicode character '\u0030'
        Assertions.assertFalse(CASNumber.isValid("\u0030-21-4"));
    }
}
