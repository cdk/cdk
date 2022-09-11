/* Copyright (C) 2008  Miguel Rojas <miguelrojasch@yahoo.es>
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
class PiElectronegativityTest extends CDKTestCase {

    private final IChemObjectBuilder      builder = SilentChemObjectBuilder.getInstance();
    private final LonePairElectronChecker lpcheck = new LonePairElectronChecker();

    /**
     * Constructor of the PiElectronegativityTest.
     */
    PiElectronegativityTest() {
        super();
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    void testPiElectronegativity() {

        Assertions.assertNotNull(new PiElectronegativity());
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    void testPiElectronegativity_Int_Int() {

        Assertions.assertNotNull(new PiElectronegativity(6, 50));
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
    void testCalculatePiElectronegativity_IAtomContainer_IAtom() throws Exception {

        PiElectronegativity pe = new PiElectronegativity();

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(new Atom("F"));
        molecule.addAtom(new Atom("C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);

        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        for (int i = 0; i < molecule.getAtomCount(); i++) {
            if (i == 0)
                Assertions.assertNotSame(0.0, pe.calculatePiElectronegativity(molecule, molecule.getAtom(i)));
            else
                Assertions.assertEquals(0.0, pe.calculatePiElectronegativity(molecule, molecule.getAtom(i)), 0.001);

        }
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
    void testCalculatePiElectronegativity_IAtomContainer_IAtom_Int_Int() throws Exception {

        PiElectronegativity pe = new PiElectronegativity();

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(new Atom("F"));
        molecule.addAtom(new Atom("C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);

        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        for (int i = 0; i < molecule.getAtomCount(); i++) {
            if (i == 0)
                Assertions.assertNotSame(0.0, pe.calculatePiElectronegativity(molecule, molecule.getAtom(i), 6, 50));
            else
                Assertions.assertEquals(0.0, pe.calculatePiElectronegativity(molecule, molecule.getAtom(i), 6, 50), 0.001);

        }
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     * @throws Exception
     */
    @Test
    void testGetMaxIterations() throws Exception {

        PiElectronegativity pe = new PiElectronegativity();
        Assertions.assertSame(6, pe.getMaxIterations());
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     * @throws Exception
     */
    @Test
    void testGetMaxResonStruc() throws Exception {

        PiElectronegativity pe = new PiElectronegativity();
        Assertions.assertSame(50, pe.getMaxResonStruc());
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     * @throws Exception
     */
    @Test
    void testSetMaxIterations_Int() throws Exception {

        PiElectronegativity pe = new PiElectronegativity();
        int maxIter = 10;
        pe.setMaxIterations(maxIter);
        Assertions.assertSame(maxIter, pe.getMaxIterations());
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     * @throws Exception
     */
    @Test
    void testSetMaxResonStruc_Int() throws Exception {

        PiElectronegativity pe = new PiElectronegativity();
        int maxRes = 10;
        pe.setMaxResonStruc(maxRes);
        Assertions.assertSame(maxRes, pe.getMaxResonStruc());
    }
}
