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

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.CDKTestCase;
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
public class ElectronegativityTest extends CDKTestCase {

    private IChemObjectBuilder      builder = SilentChemObjectBuilder.getInstance();
    private LonePairElectronChecker lpcheck = new LonePairElectronChecker();

    /**
     * Constructor of the ElectronegativityTest.
     */
    public ElectronegativityTest() {
        super();
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testElectronegativity() {

        Assert.assertNotNull(new Electronegativity());
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testElectronegativity_Int_Int() {

        Assert.assertNotNull(new Electronegativity(6, 50));
    }

    /**
     * A unit test suite for JUnit.
     *
     *  @cdk.inchi InChI=1/CH3F/c1-2/h1H3
     *
     * @return    The test suite
     * @throws Exception
     */
    @Test
    public void testCalculateSigmaElectronegativity_IAtomContainer_IAtom() throws Exception {
        double[] testResult = {11.308338, 8.7184094, 7.5289848, 7.5289848, 7.5289848};
        Electronegativity pe = new Electronegativity();

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(new Atom("F"));
        molecule.addAtom(new Atom("C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);

        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        for (int i = 0; i < molecule.getAtomCount(); i++)
            Assert.assertEquals(testResult[i], pe.calculateSigmaElectronegativity(molecule, molecule.getAtom(i)), 0.001);

    }

    /**
     * A unit test suite for JUnit.
     *
     *  @cdk.inchi InChI=1/CH3F/c1-2/h1H3
     *
     * @return    The test suite
     * @throws Exception
     */
    @Test
    public void testCalculateSigmaElectronegativity_IAtomContainer_IAtom_Int_Int() throws Exception {
        double[] testResult = {11.308338, 8.7184094, 7.5289848, 7.5289848, 7.5289848};
        Electronegativity pe = new Electronegativity();

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(new Atom("F"));
        molecule.addAtom(new Atom("C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);

        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        for (int i = 0; i < molecule.getAtomCount(); i++) {
            Assert.assertEquals(testResult[i],
                    pe.calculateSigmaElectronegativity(molecule, molecule.getAtom(i), 6, 50), 0.001);

        }
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     * @throws Exception
     */
    @Test
    public void testGetMaxIterations() throws Exception {

        Electronegativity pe = new Electronegativity();
        Assert.assertSame(6, pe.getMaxIterations());
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     * @throws Exception
     */
    @Test
    public void testGetMaxResonStruc() throws Exception {

        Electronegativity pe = new Electronegativity();
        Assert.assertSame(50, pe.getMaxResonStruc());
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     * @throws Exception
     */
    @Test
    public void testSetMaxIterations_Int() throws Exception {

        Electronegativity pe = new Electronegativity();
        int maxIter = 10;
        pe.setMaxIterations(maxIter);
        Assert.assertSame(maxIter, pe.getMaxIterations());
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     * @throws Exception
     */
    @Test
    public void testSetMaxResonStruc_Int() throws Exception {

        Electronegativity pe = new Electronegativity();
        int maxRes = 10;
        pe.setMaxResonStruc(maxRes);
        Assert.assertSame(maxRes, pe.getMaxResonStruc());
    }
}
