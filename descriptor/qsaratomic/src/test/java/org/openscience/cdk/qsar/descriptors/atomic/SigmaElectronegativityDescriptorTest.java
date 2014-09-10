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
package org.openscience.cdk.qsar.descriptors.atomic;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsaratomic
 */
public class SigmaElectronegativityDescriptorTest extends AtomicDescriptorTest {

    private IChemObjectBuilder      builder = SilentChemObjectBuilder.getInstance();
    private LonePairElectronChecker lpcheck = new LonePairElectronChecker();

    public SigmaElectronegativityDescriptorTest() {}

    @Before
    public void setUp() throws Exception {
        setDescriptor(SigmaElectronegativityDescriptor.class);
    }

    @Test
    public void testSigmaElectronegativityDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
        double[] testResult = {8.7177, 11.306};/*
                                                * from Petra online:
                                                * http://www2.
                                                * chemie.uni-erlangen
                                                * .de/services
                                                * /petra/smiles.phtml
                                                */
        IAtomicDescriptor descriptor = new SigmaElectronegativityDescriptor();
        Integer[] params = new Integer[1];

        SmilesParser sp = new SmilesParser(builder);
        IAtomContainer mol = sp.parseSmiles("CF");
        addExplicitHydrogens(mol);

        for (int i = 0; i < 2; i++) {
            params[0] = 6;
            descriptor.setParameters(params);
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            Assert.assertEquals(testResult[i], result, 0.01);
        }

    }

    /**
     *  A unit test for JUnit with Methyl chloride
     */
    @Test
    public void testSigmaElectronegativityDescriptor_Methyl_chloride() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] testResult = {8.3293, 10.491};/*
                                                * from Petra online:
                                                * http://www2.
                                                * chemie.uni-erlangen
                                                * .de/services
                                                * /petra/smiles.phtml
                                                */
        IAtomicDescriptor descriptor = new SigmaElectronegativityDescriptor();
        Integer[] params = new Integer[1];

        SmilesParser sp = new SmilesParser(builder);
        IAtomContainer mol = sp.parseSmiles("CCl");
        addExplicitHydrogens(mol);
        for (int i = 0; i < 2; i++) {
            params[0] = 6;
            descriptor.setParameters(params);
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            Assert.assertEquals(testResult[i], result, 0.05);
        }
    }

    /**
     *  A unit test for JUnit with Allyl bromide
     */
    @Test
    public void testSigmaElectronegativityDescriptor_Allyl_bromide() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] testResult = {7.8677, 8.1073, 8.4452, 10.154}; /*
                                                                 * from Petra
                                                                 * online:
                                                                 * http:/
                                                                 * /www2.chemie
                                                                 * .uni
                                                                 * -erlangen.
                                                                 * de/services
                                                                 * /petra
                                                                 * /smiles.phtml
                                                                 */
        IAtomicDescriptor descriptor = new SigmaElectronegativityDescriptor();
        Integer[] params = new Integer[1];

        SmilesParser sp = new SmilesParser(builder);
        IAtomContainer mol = sp.parseSmiles("C=CCBr");
        addExplicitHydrogens(mol);

        for (int i = 0; i < 4; i++) {
            params[0] = 6;
            descriptor.setParameters(params);
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            Assert.assertEquals(testResult[i], result, 0.02);
        }
    }

    /**
     *  A unit test for JUnit with Isopentyl iodide
     */
    @Test
    public void testSigmaElectronegativityDescriptor_Isopentyl_iodide() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double testResult = 9.2264; /*
                                     * from Petra online:
                                     * http://www2.chemie.uni-
                                     * erlangen.de/services/petra/smiles.phtml
                                     */
        IAtomicDescriptor descriptor = new SigmaElectronegativityDescriptor();

        SmilesParser sp = new SmilesParser(builder);
        IAtomContainer mol = sp.parseSmiles("C(C)(C)CCI");
        addExplicitHydrogens(mol);

        double result = ((DoubleResult) descriptor.calculate(mol.getAtom(5), mol).getValue()).doubleValue();
        Assert.assertEquals(testResult, result, 0.08);
    }

    /**
     *  A unit test for JUnit with Ethoxy ethane
     */
    @Test
    public void testSigmaElectronegativityDescriptor_Ethoxy_ethane() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] testResult = {7.6009, 8.3948, 9.4663, 8.3948, 7.6009}; /*
                                                                         * from
                                                                         * Petra
                                                                         * online
                                                                         * :
                                                                         * http
                                                                         * :/
                                                                         * /www2
                                                                         * .
                                                                         * chemie
                                                                         * .uni-
                                                                         * erlangen
                                                                         * .de/
                                                                         * services
                                                                         * /
                                                                         * petra
                                                                         * /
                                                                         * smiles
                                                                         * .
                                                                         * phtml
                                                                         */
        IAtomicDescriptor descriptor = new SigmaElectronegativityDescriptor();
        Integer[] params = new Integer[1];

        SmilesParser sp = new SmilesParser(builder);
        IAtomContainer mol = sp.parseSmiles("CCOCC");
        addExplicitHydrogens(mol);

        for (int i = 0; i < 5; i++) {
            params[0] = 6;
            descriptor.setParameters(params);
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            Assert.assertEquals(testResult[i], result, 0.002);
        }
    }

    /**
     *  A unit test for JUnit with Ethanolamine
     */
    @Test
    public void testSigmaElectronegativityDescriptor_Ethanolamine() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] testResult = {8.1395, 8.1321, 8.5049, 9.3081}; /*
                                                                 * from Petra
                                                                 * online:
                                                                 * http:/
                                                                 * /www2.chemie
                                                                 * .uni
                                                                 * -erlangen.
                                                                 * de/services
                                                                 * /petra
                                                                 * /smiles.phtml
                                                                 */
        IAtomicDescriptor descriptor = new SigmaElectronegativityDescriptor();
        Integer[] params = new Integer[1];

        SmilesParser sp = new SmilesParser(builder);
        IAtomContainer mol = sp.parseSmiles("NCCO");
        addExplicitHydrogens(mol);

        for (int i = 0; i < 4; i++) {
            params[0] = 6;
            descriptor.setParameters(params);
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            Assert.assertEquals(testResult[i], result, 0.002);
        }
    }

    /**
     *  A unit test for JUnit with Allyl mercaptan
     */
    @Test
    public void testSigmaElectronegativityDescriptor_Allyl_mercaptan() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] testResult = {7.8634, 8.0467, 8.061, 8.5917}; /*
                                                                * from Petra
                                                                * online:
                                                                * http://
                                                                * www2.chemie
                                                                * .uni
                                                                * -erlangen.de
                                                                * /services
                                                                * /petra
                                                                * /smiles.phtml
                                                                */
        IAtomicDescriptor descriptor = new SigmaElectronegativityDescriptor();

        SmilesParser sp = new SmilesParser(builder);
        IAtomContainer mol = sp.parseSmiles("C=CCS");
        addExplicitHydrogens(mol);

        for (int i = 0; i < 4; i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            Assert.assertEquals(testResult[i], result, 0.01);
        }
    }

    /**
     *  A unit test for JUnit with CCCCl # CCC[Cl+*]
     *
     *  @cdk.inchi InChI=1/C3H7Cl/c1-2-3-4/h2-3H2,1H3
     */
    @Test
    public void testCompareIonized() throws ClassNotFoundException, CDKException, java.lang.Exception {

        IAtomContainer molA = builder.newInstance(IAtomContainer.class);
        molA.addAtom(builder.newInstance(IAtom.class, "C"));
        molA.addAtom(builder.newInstance(IAtom.class, "C"));
        molA.addBond(0, 1, IBond.Order.SINGLE);
        molA.addAtom(builder.newInstance(IAtom.class, "C"));
        molA.addBond(1, 2, IBond.Order.SINGLE);
        molA.addAtom(builder.newInstance(IAtom.class, "Cl"));
        molA.addBond(2, 3, IBond.Order.SINGLE);

        addExplicitHydrogens(molA);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molA);
        lpcheck.saturate(molA);

        double resultA = ((DoubleResult) descriptor.calculate(molA.getAtom(3), molA).getValue()).doubleValue();

        IAtomContainer molB = builder.newInstance(IAtomContainer.class);
        molB.addAtom(builder.newInstance(IAtom.class, "C"));
        molB.addAtom(builder.newInstance(IAtom.class, "C"));
        molB.addBond(0, 1, IBond.Order.SINGLE);
        molB.addAtom(builder.newInstance(IAtom.class, "C"));
        molB.addBond(1, 2, IBond.Order.SINGLE);
        molB.addAtom(builder.newInstance(IAtom.class, "Cl"));
        molB.getAtom(3).setFormalCharge(1);
        molB.addSingleElectron(3);
        molB.addLonePair(3);
        molB.addLonePair(3);
        molB.addBond(2, 3, IBond.Order.SINGLE);

        addExplicitHydrogens(molB);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molB);
        lpcheck.saturate(molB);

        Assert.assertEquals(1, molB.getAtom(3).getFormalCharge(), 0.00001);
        Assert.assertEquals(1, molB.getSingleElectronCount(), 0.00001);
        Assert.assertEquals(2, molB.getLonePairCount(), 0.00001);

        double resultB = ((DoubleResult) descriptor.calculate(molB.getAtom(3), molB).getValue()).doubleValue();

        Assert.assertEquals(resultA, resultB, 0.00001);
    }
}
