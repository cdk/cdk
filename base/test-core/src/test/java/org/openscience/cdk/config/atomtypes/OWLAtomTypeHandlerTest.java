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
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.CDKTestCase;

/**
 * Checks the functionality of the {@link OWLAtomTypeHandler}.
 *
 * @cdk.module test-core
 */
public class OWLAtomTypeHandlerTest extends CDKTestCase {

    // serious testing is done in OWLAtomTypeFactoryTest; the factory
    // requires this class to work properly. But nevertheless:

    @Test
    public void testAtomTypeHandler_IChemObjectBuilder() {
        OWLAtomTypeHandler handler = new OWLAtomTypeHandler(new ChemObject().getBuilder());
        Assert.assertNotNull(handler);
    }

    @Test
    public void testGetAtomTypes() {
        OWLAtomTypeHandler handler = new OWLAtomTypeHandler(new ChemObject().getBuilder());
        // nothing is read
        Assert.assertNotNull(handler);
        Assert.assertNull(handler.getAtomTypes());
    }

    @Test
    public void testStartDocument() {
        OWLAtomTypeHandler handler = new OWLAtomTypeHandler(new ChemObject().getBuilder());
        // nothing is read, but Vector is initialized
        Assert.assertNotNull(handler);
        Assert.assertNull(handler.getAtomTypes());
    }

}
