/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import java.util.Iterator;
import java.util.List;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

/**
 *  Description of the Class
 *
 * @cdk.module test-charges
 *
 *@author     chhoppe
 *@cdk.created    2004-11-04
 */
public class GasteigerMarsiliPartialChargesTest extends CDKTestCase {

    private IChemObjectBuilder      builder = SilentChemObjectBuilder.getInstance();
    private LonePairElectronChecker lpcheck = new LonePairElectronChecker();

    /**
     *  A unit test for JUnit with methylenfluoride
     *
     *  @cdk.inchi InChI=1/CH3F/c1-2/h1H3
     */
    @Test
    public void testCalculateCharges_IAtomContainer() throws Exception {
        double[] testResult = {0.07915, -0.25264, 0.05783, 0.05783, 0.05783};

        GasteigerMarsiliPartialCharges peoe = new GasteigerMarsiliPartialCharges();

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
            Assert.assertEquals(testResult[i], molecule.getAtom(i).getCharge(), 0.01);
        }
    }

    /**
     *
     */
    @Test
    public void testAssignGasteigerMarsiliSigmaPartialCharges_IAtomContainer_Boolean() throws Exception {
        double[] testResult = {0.07915, -0.25264, 0.05783, 0.05783, 0.05783};

        GasteigerMarsiliPartialCharges peoe = new GasteigerMarsiliPartialCharges();

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("F"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);

        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        peoe.assignGasteigerMarsiliSigmaPartialCharges(molecule, true);
        for (int i = 0; i < molecule.getAtomCount(); i++) {
            //logger.debug("Charge for atom:"+i+" S:"+mol.getAtomAt(i).getSymbol()+" Charge:"+mol.getAtomAt(i).getCharge());
            Assert.assertEquals(testResult[i], molecule.getAtom(i).getCharge(), 0.01);
        }

    }

    /**
     *
     */
    @Test
    public void testAssignGasteigerSigmaMarsiliFactors_IAtomContainer() throws Exception {
        GasteigerMarsiliPartialCharges peoe = new GasteigerMarsiliPartialCharges();

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(new Atom("C"));
        molecule.getAtom(0).setCharge(0.0);
        molecule.addAtom(new Atom("F"));
        molecule.getAtom(1).setCharge(0.0);
        molecule.addBond(0, 1, IBond.Order.SINGLE);

        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);
        for (IAtom atom : molecule.atoms())
            atom.setCharge(0.0);

        org.hamcrest.MatcherAssert.assertThat(peoe.assignGasteigerSigmaMarsiliFactors(molecule).length,
                          is(not(0)));

    }

    /**
     *
     */
    @Test
    public void testGetMaxGasteigerIters() throws Exception {

        GasteigerMarsiliPartialCharges peoe = new GasteigerMarsiliPartialCharges();

        Assert.assertEquals(20, peoe.getMaxGasteigerIters(), 0.01);

    }

    /**
     *
     */
    @Test
    public void testGetMaxGasteigerDamp() throws Exception {

        GasteigerMarsiliPartialCharges peoe = new GasteigerMarsiliPartialCharges();

        Assert.assertEquals(20, peoe.getMaxGasteigerIters(), 0.01);

    }

    /**
     *
     */
    @Test
    public void testGetChiCatHydrogen() throws Exception {

        GasteigerMarsiliPartialCharges peoe = new GasteigerMarsiliPartialCharges();

        Assert.assertEquals(20, peoe.getMaxGasteigerIters(), 0.01);

    }

    /**
     *
     */
    @Test
    public void testGetStepSize() throws Exception {

        GasteigerMarsiliPartialCharges peoe = new GasteigerMarsiliPartialCharges();
        Assert.assertEquals(5, peoe.getStepSize());

    }

    /**
     *
     */
    @Test
    public void testSetMaxGasteigerIters_Double() throws Exception {

        GasteigerMarsiliPartialCharges peoe = new GasteigerMarsiliPartialCharges();
        double MX_ITERATIONS = 10;
        peoe.setMaxGasteigerIters(MX_ITERATIONS);
        Assert.assertEquals(MX_ITERATIONS, peoe.getMaxGasteigerIters(), 0.01);

    }

    /**
     *
     */
    @Test
    public void testSetMaxGasteigerDamp_Double() throws Exception {

        GasteigerMarsiliPartialCharges peoe = new GasteigerMarsiliPartialCharges();
        double MX_DAMP = 1;
        peoe.setMaxGasteigerDamp(MX_DAMP);
        Assert.assertEquals(MX_DAMP, peoe.getMaxGasteigerDamp(), 0.01);

    }

    /**
     *
     */
    @Test
    public void testSetChiCatHydrogen_Double() throws Exception {

        GasteigerMarsiliPartialCharges peoe = new GasteigerMarsiliPartialCharges();
        double DEOC_HYDROGEN = 22;
        peoe.setChiCatHydrogen(DEOC_HYDROGEN);
        Assert.assertEquals(DEOC_HYDROGEN, peoe.getChiCatHydrogen(), 0.01);

    }

    /**
     *
     */
    @Test
    public void testSetStepSize() throws Exception {
        GasteigerMarsiliPartialCharges peoe = new GasteigerMarsiliPartialCharges();
        int STEP_SIZE = 22;
        peoe.setStepSize(STEP_SIZE);
        Assert.assertEquals(STEP_SIZE, peoe.getStepSize());

    }

    @Test(expected = CDKException.class)
    public void testUndefinedPartialCharge() throws Exception {
        String filename = "data/mdl/burden_undefined.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new MDLV2000Reader(ins);
        ChemFile content = reader.read(new ChemFile());
        reader.close();
        List<IAtomContainer> cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = cList.get(0);

        Assert.assertNotNull(ac);
        addExplicitHydrogens(ac);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(ac);
        Aromaticity.cdkLegacy().apply(ac);

        addExplicitHydrogens(ac);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(ac);
        lpcheck.saturate(ac);

        GasteigerMarsiliPartialCharges peoe = new GasteigerMarsiliPartialCharges();
        peoe.calculateCharges(ac);
    }
}
