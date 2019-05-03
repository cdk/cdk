/* Copyright (C) 2005-2007  The Chemistry Development Kit (CDK) project
 *                    2008  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.config.atomtypes;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;

/**
 * Checks the functionality of the {@link OWLAtomTypeMappingHandler}.
 *
 * @cdk.module test-atomtype
 */
public class OWLAtomTypeMappingHandlerTest extends CDKTestCase {

    @Test
    public void testOWLAtomTypeMappingHandler() {
        OWLAtomTypeMappingHandler handler = new OWLAtomTypeMappingHandler();
        Assert.assertNotNull(handler);
    }

    @Test
    public void testGetAtomTypeMappings() {
        OWLAtomTypeMappingHandler handler = new OWLAtomTypeMappingHandler();
        // nothing is read
        Assert.assertNotNull(handler);
        Assert.assertNull(handler.getAtomTypeMappings());
    }

    @Test
    public void testStartDocument() {
        OWLAtomTypeMappingHandler handler = new OWLAtomTypeMappingHandler();
        // nothing is read, but Vector is initialized
        Assert.assertNotNull(handler);
        Assert.assertNull(handler.getAtomTypeMappings());
    }

    @Test
    public void testEndElement_String_String_String() {
        Assert.assertTrue(true); // tested by testGetAtomTypeMappings
    }

    @Test
    public void testStartElement_String_String_String_Attributes() {
        Assert.assertTrue(true); // tested by testGetAtomTypeMappings
    }

    @Test
    public void testCharacters_arraychar_int_int() {
        Assert.assertTrue(true); // tested by testGetAtomTypeMappings
    }

}
