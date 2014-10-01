/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.AbstractAtomContainerSetTest;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.ITestObjectBuilder;

/**
 * Checks the functionality of the MoleculeSet class.
 *
 * @cdk.module test-data
 *
 * @see org.openscience.cdk.MoleculeSet
 */
public class AtomContainerSetTest extends AbstractAtomContainerSetTest {

    @BeforeClass
    public static void setUp() {
        setTestObjectBuilder(new ITestObjectBuilder() {

            @Override
            public IChemObject newTestObject() {
                return new AtomContainerSet();
            }
        });
    }

    @Test
    public void testAtomContainerSet() {
        IAtomContainerSet som = new AtomContainerSet();
        Assert.assertNotNull(som);
        Assert.assertEquals(0, som.getAtomContainerCount());
    }

}
