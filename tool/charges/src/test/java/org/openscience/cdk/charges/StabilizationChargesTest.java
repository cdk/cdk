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
 */
package org.openscience.cdk.charges;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
* TestSuite that runs all tests.
*
* @cdk.module test-charges
*/
class StabilizationChargesTest extends CDKTestCase {

    private final IChemObjectBuilder      builder = SilentChemObjectBuilder.getInstance();
    private final LonePairElectronChecker lpcheck = new LonePairElectronChecker();

    /**
     * Constructor of the StabilizationChargesTest.
     */
    StabilizationChargesTest() {
        super();
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    void testStabilizationCharges() {

        Assertions.assertNotNull(new StabilizationCharges());
    }

    /**
     * A unit test suite for JUnit.
     *
     *  @cdk.inchi InChI=1/C4H8/c1-3-4-2/h3H,1,4H2,2H3
     *
     *
     * @throws Exception
     */
    @Test
    @Tag("SlowTest")
    void testCalculatePositive_IAtomContainer_IAtom() throws Exception {

        StabilizationCharges sc = new StabilizationCharges();

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.getAtom(1).setFormalCharge(+1);
        molecule.addAtom(new Atom("C"));
        molecule.addBond(1, 2, IBond.Order.SINGLE);
        molecule.addAtom(new Atom("C"));
        molecule.addBond(2, 3, IBond.Order.DOUBLE);

        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        for (int i = 0; i < molecule.getAtomCount(); i++) {
            if (i == 1)
                Assertions.assertNotSame(0.0, sc.calculatePositive(molecule, molecule.getAtom(i)));
            else
                Assertions.assertEquals(0.0, sc.calculatePositive(molecule, molecule.getAtom(i)), 0.001);

        }
    }
}
