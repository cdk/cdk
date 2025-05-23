/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.debug;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.interfaces.AbstractChemObjectTest;
import org.openscience.cdk.interfaces.IChemObject;

/**
 * Checks the functionality of the {@link DebugChemObject}.
 *
 */
class DebugChemObjectTest extends AbstractChemObjectTest {

    @BeforeAll
    static void setUp() {
        setTestObjectBuilder(DebugChemObject::new);
    }

    @Test
    void testDebugChemObject() {
        IChemObject chemObject = new DebugChemObject();
        Assertions.assertNotNull(chemObject);
    }

    @Test
    void testDebugChemObject_IChemObject() {
        IChemObject chemObject1 = new DebugChemObject();
        IChemObject chemObject = new DebugChemObject(chemObject1);
        Assertions.assertNotNull(chemObject);
    }

    @Test
    void compare() {
        DebugChemObject co1 = new DebugChemObject();
        DebugChemObject co2 = new DebugChemObject();
        co1.setID("a1");
        co2.setID("a1");
        Assertions.assertTrue(co1.compare(co2));
    }

    @Test
    void compareDifferent() {
        DebugChemObject co1 = new DebugChemObject();
        DebugChemObject co2 = new DebugChemObject();
        co1.setID("a1");
        co2.setID("a2");
        Assertions.assertFalse(co1.compare(co2));
    }
}
