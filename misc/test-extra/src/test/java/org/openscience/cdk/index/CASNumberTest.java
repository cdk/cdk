/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;

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
        Assert.assertTrue(CASNumber.isValid("50-00-0")); // formaldehyde
        Assert.assertTrue(CASNumber.isValid("548-00-5"));
        Assert.assertTrue(CASNumber.isValid("2622-26-6"));
        Assert.assertTrue(CASNumber.isValid("15299-99-7"));
        Assert.assertTrue(CASNumber.isValid("673434-32-7"));
    }

    @Test
    public void testInvalidCheckDigits() {
        // invalid R value
        Assert.assertFalse(CASNumber.isValid("50-00-1"));
        Assert.assertFalse(CASNumber.isValid("50-00-2"));
        Assert.assertFalse(CASNumber.isValid("50-00-3"));
        Assert.assertFalse(CASNumber.isValid("50-00-4"));
        Assert.assertFalse(CASNumber.isValid("50-00-5"));
        Assert.assertFalse(CASNumber.isValid("50-00-6"));
        Assert.assertFalse(CASNumber.isValid("50-00-7"));
        Assert.assertFalse(CASNumber.isValid("50-00-8"));
        Assert.assertFalse(CASNumber.isValid("50-00-9"));
    }

    @Test
    public void testLargerThanFirst() {
        // valid format, but wrong number, the first is 50-00-0
        Assert.assertFalse(CASNumber.isValid("3-21-4"));
    }

    @Test
    public void testWrongHyphenPositions() {
        // invalid format due to invalid hyphen positions
        Assert.assertFalse(CASNumber.isValid("3-21-40"));
        Assert.assertFalse(CASNumber.isValid("3-210-4"));
        Assert.assertFalse(CASNumber.isValid("03-1-4"));
        Assert.assertFalse(CASNumber.isValid("03-21-"));
    }

    @Test
    public void testInvalidCharacters() {
        // invalid characters
        Assert.assertFalse(CASNumber.isValid("a-21-4"));
        Assert.assertFalse(CASNumber.isValid("3-a1-4"));
        Assert.assertFalse(CASNumber.isValid("3-2a-4"));
        Assert.assertFalse(CASNumber.isValid("3-21-a"));
        Assert.assertFalse(CASNumber.isValid("d-cb-a"));
    }

    @Test
    public void testSanity() {
        // completely stupid value
        Assert.assertFalse(CASNumber.isValid("0&z003-!0>/-0a"));
    }

    @Test
    public void testCharacterSet() {
        // invalid value even with the '0' unicode character '\u0030'
        Assert.assertFalse(CASNumber.isValid("\u0030-21-4"));
    }
}
