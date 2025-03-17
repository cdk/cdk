/* Copyright (C) 2001-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
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
package org.openscience.cdk;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.test.interfaces.AbstractChemObjectTest;

/**
 * TestCase for the IChemObject class.
 *
 * @author Edgar Luttmann &lt;edgar@uni-paderborn.de&gt;
 * @cdk.created 2001-08-09
 */
class ChemObjectTest extends AbstractChemObjectTest {

    @BeforeAll
    static void setUp() {
        setTestObjectBuilder(ChemObject::new);
    }

    @Test
    void testChemObject() {
        IChemObject chemObject = new ChemObject();
        Assertions.assertNotNull(chemObject);
    }

    @Test
    void testChemObject_IChemObject() {
        IChemObject chemObject1 = new ChemObject();
        IChemObject chemObject = new ChemObject(chemObject1);
        Assertions.assertNotNull(chemObject);
    }

    @Test
    void compare() {
        ChemObject co1 = new ChemObject();
        ChemObject co2 = new ChemObject();
        co1.setID("a1");
        co2.setID("a1");
        Assertions.assertTrue(co1.compare(co2));
    }

    @Test
    void compareDifferent() {
        ChemObject co1 = new ChemObject();
        ChemObject co2 = new ChemObject();
        co1.setID("a1");
        co2.setID("a2");
        Assertions.assertFalse(co1.compare(co2));
    }
}
