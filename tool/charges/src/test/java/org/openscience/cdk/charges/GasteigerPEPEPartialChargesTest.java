/* Copyright (C) 2008  Miguel Rojas <miguelrojasch@yahoo.es>
 *
 *  Contact: cdk-devel@list.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.charges;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.util.ArrayList;
import java.util.List;

/**
 * Description of the Class
 *
 * @author Miguel Rojas
 * @cdk.module test-charges
 * @cdk.created 2008-18-05
 */
class GasteigerPEPEPartialChargesTest extends CDKTestCase {

    private final IChemObjectBuilder      builder = SilentChemObjectBuilder.getInstance();
    private final LonePairElectronChecker lpcheck = new LonePairElectronChecker();

    /**
     * A unit test for JUnit with methylenfluoride
     *
     * @cdk.inchi InChI=1/CH3F/c1-2/h1H3
     */
    @Test
    void testCalculateCharges_IAtomContainer() throws Exception {
        double[] testResult = {0.0, 0.0, 0.0, 0.0, 0.0};

        GasteigerPEPEPartialCharges peoe = new GasteigerPEPEPartialCharges();

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("F"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);

        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        peoe.calculateCharges(molecule);
        for (int i = 0; i < molecule.getAtomCount(); i++) {
            //logger.debug("Charge for atom:"+i+" S:"+mol.getAtomAt(i).getSymbol()+" Charge:"+mol.getAtomAt(i).getCharge());
            Assertions.assertEquals(testResult[i], molecule.getAtom(i).getCharge(), 0.01);
        }
    }

    /**
     * @cdk.bug 2013689
     * @throws Exception
     */
    @Test
    void testAromaticBondOrders() throws Exception {
        GasteigerPEPEPartialCharges peoe = new GasteigerPEPEPartialCharges();

        String smiles1 = "c1ccccc1";
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol1 = sp.parseSmiles(smiles1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol1);
        Aromaticity.cdkLegacy().apply(mol1);
        addExplicitHydrogens(mol1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol1);
        lpcheck.saturate(mol1);

        List<Boolean> oldBondOrders = new ArrayList<>();
        for (int i = 0; i < mol1.getBondCount(); i++)
            oldBondOrders.add(mol1.getBond(i).getFlag(CDKConstants.ISAROMATIC));

        peoe.calculateCharges(mol1);

        List<Boolean> newBondOrders = new ArrayList<>();
        for (int i = 0; i < mol1.getBondCount(); i++)
            newBondOrders.add(mol1.getBond(i).getFlag(CDKConstants.ISAROMATIC));

        for (int i = 0; i < oldBondOrders.size(); i++) {
            Assertions.assertEquals(oldBondOrders.get(i), newBondOrders.get(i), "bond " + i + " does not match");
        }
    }

    @Test
    void testAromaticAndNonAromatic() throws Exception {
        GasteigerPEPEPartialCharges peoe = new GasteigerPEPEPartialCharges();

        String smiles1 = "c1ccccc1";
        String smiles2 = "C1=CC=CC=C1";

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol1 = sp.parseSmiles(smiles1);
        IAtomContainer mol2 = sp.parseSmiles(smiles2);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol2);
        Aromaticity.cdkLegacy().apply(mol1);
        Aromaticity.cdkLegacy().apply(mol2);

        addExplicitHydrogens(mol1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol1);
        lpcheck.saturate(mol1);

        addExplicitHydrogens(mol2);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol2);
        lpcheck.saturate(mol2);

        peoe.calculateCharges(mol1);
        peoe.calculateCharges(mol2);
        for (int i = 0; i < mol1.getAtomCount(); i++) {
            Assertions.assertEquals(mol1.getAtom(i).getCharge(), mol2.getAtom(i)
                                                                     .getCharge(), 0.01, "charge on atom " + i + " does not match");
        }

    }

    /**
     *
     */
    @Test
    void testAssignGasteigerPiPartialCharges_IAtomContainer_Boolean() throws Exception {
        double[] testResult = {0.0, 0.0, 0.0, 0.0, 0.0};

        GasteigerPEPEPartialCharges peoe = new GasteigerPEPEPartialCharges();

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("F"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);

        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        peoe.assignGasteigerPiPartialCharges(molecule, true);
        for (int i = 0; i < molecule.getAtomCount(); i++) {
            //logger.debug("Charge for atom:"+i+" S:"+mol.getAtomAt(i).getSymbol()+" Charge:"+mol.getAtomAt(i).getCharge());
            Assertions.assertEquals(testResult[i], molecule.getAtom(i).getCharge(), 0.01);
        }

    }

    /**
     *
     */
    @Test
    void testGetMaxGasteigerIters() throws Exception {

        GasteigerPEPEPartialCharges peoe = new GasteigerPEPEPartialCharges();

        Assertions.assertEquals(8, peoe.getMaxGasteigerIters());

    }

    /**
     *
     */
    @Test
    void testGetMaxResoStruc() throws Exception {

        GasteigerPEPEPartialCharges peoe = new GasteigerPEPEPartialCharges();

        Assertions.assertEquals(50, peoe.getMaxResoStruc());

    }

    /**
     *
     */
    @Test
    void testGetStepSize() throws Exception {

        GasteigerPEPEPartialCharges peoe = new GasteigerPEPEPartialCharges();
        Assertions.assertEquals(5, peoe.getStepSize());

    }

    /**
     *
     */
    @Test
    void testSetMaxGasteigerIters_Double() throws Exception {

        GasteigerPEPEPartialCharges peoe = new GasteigerPEPEPartialCharges();
        int MX_ITERATIONS = 10;
        peoe.setMaxGasteigerIters(MX_ITERATIONS);
        Assertions.assertEquals(MX_ITERATIONS, peoe.getMaxGasteigerIters());

    }

    /**
     *
     */
    @Test
    void testSetMaxResoStruc_Int() throws Exception {

        GasteigerPEPEPartialCharges peoe = new GasteigerPEPEPartialCharges();
        int MX_RESON = 1;
        peoe.setMaxResoStruc(MX_RESON);
        Assertions.assertEquals(MX_RESON, peoe.getMaxResoStruc());

    }

    /**
     *
     */
    @Test
    void testSetStepSize() throws Exception {

        GasteigerPEPEPartialCharges peoe = new GasteigerPEPEPartialCharges();
        int STEP_SIZE = 22;
        peoe.setStepSize(STEP_SIZE);
        Assertions.assertEquals(STEP_SIZE, peoe.getStepSize());

    }

    /**
     *
     */
    @Test
    void testAssignrPiMarsilliFactors_IAtomContainerSet() throws Exception {
        GasteigerPEPEPartialCharges peoe = new GasteigerPEPEPartialCharges();

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("F"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);

        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);
        for (IAtom iAtom : molecule.atoms()) iAtom.setCharge(0.0);

        IAtomContainerSet set = builder.newInstance(IAtomContainerSet.class);
        set.addAtomContainer(molecule);
        set.addAtomContainer(molecule);

        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        Assertions.assertNotNull(peoe.assignrPiMarsilliFactors(set));

    }
}
