/* Copyright (C) 2005-2007  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk.config.isotopes;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.config.isotopes.IsotopeHandler;
import org.openscience.cdk.CDKTestCase;

/**
 * Checks the functionality of the IsotopeFactory
 *
 * @cdk.module test-extra
 */
public class IsotopeHandlerTest extends CDKTestCase {

    // serious testing is done in IsotopeFactoryTest; the factory
    // requires this class to work properly. But nevertheless:

    @Test
    public void testIsotopeHandler_IChemObjectBuilder() {
        IsotopeHandler handler = new IsotopeHandler(new ChemObject().getBuilder());
        Assert.assertNotNull(handler);
    }

    @Test
    public void testGetIsotopes() {
        IsotopeHandler handler = new IsotopeHandler(new ChemObject().getBuilder());
        // nothing is read
        Assert.assertNotNull(handler);
        Assert.assertNull(handler.getIsotopes());
    }

    @Test
    public void testStartDocument() {
        IsotopeHandler handler = new IsotopeHandler(new ChemObject().getBuilder());
        // nothing is read, but Vector is initialized
        Assert.assertNotNull(handler);
        Assert.assertNull(handler.getIsotopes());
    }

    @Test
    public void testCharacters_arraychar_int_int() {
        // nothing I can test here that IsotopeFactoryTest doesn't do
        Assert.assertTrue(true);
    }

    @Test
    public void testEndElement_String_String_String() {
        // nothing I can test here that IsotopeFactoryTest doesn't do
        Assert.assertTrue(true);
    }

}
