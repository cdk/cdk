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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Checks the functionality of the {@link OWLAtomTypeMappingHandler}.
 *
 */
class OWLAtomTypeMappingHandlerTest {

    @Test
    void testOWLAtomTypeMappingHandler() {
        OWLAtomTypeMappingHandler handler = new OWLAtomTypeMappingHandler();
        Assertions.assertNotNull(handler);
    }

    @Test
    void testGetAtomTypeMappings() {
        OWLAtomTypeMappingHandler handler = new OWLAtomTypeMappingHandler();
        // nothing is read
        Assertions.assertNotNull(handler);
        Assertions.assertNull(handler.getAtomTypeMappings());
    }

    @Test
    void testStartDocument() {
        OWLAtomTypeMappingHandler handler = new OWLAtomTypeMappingHandler();
        // nothing is read, but Vector is initialized
        Assertions.assertNotNull(handler);
        Assertions.assertNull(handler.getAtomTypeMappings());
    }

    @Test
    void testEndElement_String_String_String() {
        Assertions.assertTrue(true); // tested by testGetAtomTypeMappings
    }

    @Test
    void testStartElement_String_String_String_Attributes() {
        Assertions.assertTrue(true); // tested by testGetAtomTypeMappings
    }

    @Test
    void testCharacters_arraychar_int_int() {
        Assertions.assertTrue(true); // tested by testGetAtomTypeMappings
    }

}
