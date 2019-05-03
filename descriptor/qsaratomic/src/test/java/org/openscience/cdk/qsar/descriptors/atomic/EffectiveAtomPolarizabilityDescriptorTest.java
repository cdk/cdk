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
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsaratomic
 */
public class EffectiveAtomPolarizabilityDescriptorTest extends AtomicDescriptorTest {

    public EffectiveAtomPolarizabilityDescriptorTest() {}

    @Before
    public void setUp() throws Exception {
        setDescriptor(EffectiveAtomPolarizabilityDescriptor.class);
    }

    /**
     *  A unit test for JUnit with 2-(dimethylamino)ethyl)amino
     */
    @Test
    public void testEffectivePolarizabilityDescriptor() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] testResult = {4.7253, 6.1345, 6.763, 6.925, 5.41, 5.41};
        IAtomicDescriptor descriptor = new EffectiveAtomPolarizabilityDescriptor();

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("NCCN(C)(C)");
        addExplicitHydrogens(mol);

        for (int i = 0; i < 6; i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            Assert.assertEquals(testResult[i], result, 0.01);
        }
    }

    /**
     *  A unit test for JUnit with Ethyl chloride
     */
    @Test
    public void testPolarizabilityDescriptor_Ethyl_chloride() throws Exception {
        double[] testResult = {4.8445, 5.824, 4.6165};/*
                                                       * from Petra online:
                                                       * http:
                                                       * //www2.chemie.uni-erlangen
                                                       * .
                                                       * de/services/petra/smiles
                                                       * .phtml
                                                       */
        EffectiveAtomPolarizabilityDescriptor descriptor = new EffectiveAtomPolarizabilityDescriptor();

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCCl");
        addExplicitHydrogens(mol);
        for (int i = 0; i < 3; i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            Assert.assertEquals(testResult[i], result, 0.01);
        }
    }

    /**
     *  A unit test for JUnit with Allyl bromide
     */
    @Test
    public void testPolarizabilityDescriptor_Allyl_bromide() throws Exception {
        double testResult = 6.1745; /*
                                     * from Petra online:
                                     * http://www2.chemie.uni-
                                     * erlangen.de/services/petra/smiles.phtml
                                     */
        EffectiveAtomPolarizabilityDescriptor descriptor = new EffectiveAtomPolarizabilityDescriptor();

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C=CCBr");
        addExplicitHydrogens(mol);

        double result = ((DoubleResult) descriptor.calculate(mol.getAtom(3), mol).getValue()).doubleValue();
        Assert.assertEquals(testResult, result, 0.01);
    }

    /**
     *  A unit test for JUnit with Isopentyl iodide
     */
    @Test
    public void testPolarizabilityDescriptor_Isopentyl_iodide() throws Exception {
        double[] testResult = {8.3585, 6.1118, 6.1118, 9.081, 10.526, 8.69}; /*
                                                                              * from
                                                                              * Petra
                                                                              * online
                                                                              * :
                                                                              * http
                                                                              * :
                                                                              * /
                                                                              * /
                                                                              * www2
                                                                              * .
                                                                              * chemie
                                                                              * .
                                                                              * uni
                                                                              * -
                                                                              * erlangen
                                                                              * .
                                                                              * de
                                                                              * /
                                                                              * services
                                                                              * /
                                                                              * petra
                                                                              * /
                                                                              * smiles
                                                                              * .
                                                                              * phtml
                                                                              */
        EffectiveAtomPolarizabilityDescriptor descriptor = new EffectiveAtomPolarizabilityDescriptor();

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C(C)(C)CCI");
        addExplicitHydrogens(mol);

        for (int i = 0; i < 6; i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            Assert.assertEquals(testResult[i], result, 0.01);
        }
    }

    /**
     *  A unit test for JUnit with Ethoxy ethane
     */
    @Test
    public void testPolarizabilityDescriptor_Ethoxy_ethane() throws Exception {
        double testResult = 5.207; /*
                                    * from Petra online:
                                    * http://www2.chemie.uni-erlangen
                                    * .de/services/petra/smiles.phtml
                                    */
        EffectiveAtomPolarizabilityDescriptor descriptor = new EffectiveAtomPolarizabilityDescriptor();

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCOCC");
        addExplicitHydrogens(mol);

        double result = ((DoubleResult) descriptor.calculate(mol.getAtom(2), mol).getValue()).doubleValue();
        Assert.assertEquals(testResult, result, 0.01);
    }

    /**
     *  A unit test for JUnit with Ethanolamine
     */
    @Test
    public void testPolarizabilityDescriptor_Ethanolamine() throws Exception {
        double[] testResult = {4.2552, 5.1945, 4.883, 3.595}; /*
                                                               * from Petra
                                                               * online:
                                                               * http://www2
                                                               * .chemie
                                                               * .uni-erlangen
                                                               * .de
                                                               * /services/petra
                                                               * /smiles.phtml
                                                               */
        EffectiveAtomPolarizabilityDescriptor descriptor = new EffectiveAtomPolarizabilityDescriptor();

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("NCCO");
        addExplicitHydrogens(mol);
        for (int i = 0; i < 4; i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            Assert.assertEquals(testResult[i], result, 0.01);
        }
    }

    /**
     *  A unit test for JUnit with Allyl mercaptan
     */
    @Test
    public void testPolarizabilityDescriptor_Allyl_mercaptan() throws Exception {
        double[] testResult = {5.2995, 6.677, 7.677, 6.2545}; /*
                                                               * from Petra
                                                               * online:
                                                               * http://www2
                                                               * .chemie
                                                               * .uni-erlangen
                                                               * .de
                                                               * /services/petra
                                                               * /smiles.phtml
                                                               */
        EffectiveAtomPolarizabilityDescriptor descriptor = new EffectiveAtomPolarizabilityDescriptor();

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C=CCS");
        addExplicitHydrogens(mol);
        for (int i = 0; i < 4; i++) {
            double result = ((DoubleResult) descriptor.calculate(mol.getAtom(i), mol).getValue()).doubleValue();
            Assert.assertEquals(testResult[i], result, 0.02);
        }
    }
}
