/* Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
package org.openscience.cdk.silent;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IPDBStructure;
import org.openscience.cdk.test.interfaces.AbstractPDBStructureTest;

/**
 * Checks the functionality of the {@link PDBStructure}.
 *
 */
class PDBStructureTest extends AbstractPDBStructureTest {

    @BeforeAll
    static void setUp() {
        setChemObject(new PDBStructure());
    }

    @Test
    void testPDBStructure() {
        IPDBStructure structure = new PDBStructure();
        Assertions.assertNotNull(structure);
    }

    @Test
    void testGetBuilder() {
        PDBStructure structure = new PDBStructure();
        Assertions.assertTrue(structure.getBuilder().getClass().getName().contains("SilentChemObjectBuilder"));
    }

    @Test
    void testAddListener_IChemObjectListener() {
        ChemObjectTestHelper.testAddListener_IChemObjectListener(newChemObject());
    }

}
