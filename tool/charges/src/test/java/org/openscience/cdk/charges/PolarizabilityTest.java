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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.graph.matrix.AdjacencyMatrix;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 *  Description of the Class
 *
 *
 *@author     chhoppe
 *@cdk.created    2004-11-04
 */
class PolarizabilityTest {

    /**
     *  A unit test for JUnit
     */
    @Test
    void testGetPolarizabilitiyFactorForAtom_IAtomContainer_IAtom() throws Exception {
        Polarizability pol = new Polarizability();
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("NCCN(C)(C)");
        double result = pol.getPolarizabilitiyFactorForAtom(mol, mol.getAtom(0));
        Assertions.assertFalse(Double.isNaN(result));
        result = pol.getPolarizabilitiyFactorForAtom(mol, mol.getAtom(3));
        Assertions.assertFalse(Double.isNaN(result));
    }

    /**
     *  A unit test for JUnit with n,n-dimethyl ethylendiamine
     */
    @Test
    void testCalculateGHEffectiveAtomPolarizability_IAtomContainer_IAtom_Int_Boolean() throws Exception {
        double[] testResult = {4.73, 6.92};
        Polarizability pol = new Polarizability();
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("NCCN(C)(C)");
        double result = pol.calculateGHEffectiveAtomPolarizability(mol, mol.getAtom(0), 100, true);
        Assertions.assertEquals(testResult[0], result, 0.01);
        result = pol.calculateGHEffectiveAtomPolarizability(mol, mol.getAtom(3), 100, true);
        Assertions.assertEquals(testResult[1], result, 0.01);
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    void testCalculateGHEffectiveAtomPolarizability_IAtomContainer_IAtom_Boolean_IntInt() throws Exception {
        Polarizability pol = new Polarizability();
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCO");
        IAtomContainer before = mol.clone();
        IAtomContainer explicit = mol.clone();
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(explicit);
        int[][] distanceMatrix = PathTools.computeFloydAPSP(AdjacencyMatrix.getMatrix(explicit));

        for (int i = 0; i < mol.getAtomCount(); i++) {
            double expected = pol.calculateGHEffectiveAtomPolarizability(explicit, explicit.getAtom(i), 100, false);
            double result = pol.calculateGHEffectiveAtomPolarizability(mol, mol.getAtom(i), true, distanceMatrix);
            assertUnchanged(before, mol);
            Assertions.assertEquals(expected, result, 0.000001);
        }
    }

    @Test
    void testGetPolarizabilityFactorDoesNotModifyInput() throws Exception {
        Polarizability pol = new Polarizability();
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCO");
        IAtomContainer before = mol.clone();
        IAtomContainer explicit = mol.clone();
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(explicit);

        double expected = pol.getPolarizabilitiyFactorForAtom(explicit, explicit.getAtom(2));
        double result = pol.getPolarizabilitiyFactorForAtom(mol, mol.getAtom(2));
        assertUnchanged(before, mol);
        Assertions.assertEquals(expected, result, 0.000001);
    }

    @Test
    void testCalculateMeanPolarizabilityDoesNotModifyInput() throws Exception {
        Polarizability pol = new Polarizability();
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCO");
        IAtomContainer before = mol.clone();
        IAtomContainer explicit = mol.clone();
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(explicit);

        double expected = pol.calculateKJMeanMolecularPolarizability(explicit);
        double result = pol.calculateKJMeanMolecularPolarizability(mol);
        assertUnchanged(before, mol);
        Assertions.assertEquals(expected, result, 0.000001);
    }

    @Test
    void testCalculateEffectivePolarizabilityDoesNotModifyInput() throws Exception {
        Polarizability pol = new Polarizability();
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCO");
        IAtomContainer before = mol.clone();
        IAtomContainer explicit = mol.clone();
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(explicit);

        double expected = pol.calculateGHEffectiveAtomPolarizability(explicit, explicit.getAtom(0), 100, false);
        double result = pol.calculateGHEffectiveAtomPolarizability(mol, mol.getAtom(0), 100, true);
        assertUnchanged(before, mol);
        Assertions.assertEquals(expected, result, 0.000001);
        Assertions.assertEquals(4.10475, result, 0.000001);
    }

    @Test
    void testCalculateBondPolarizabilityDoesNotModifyInput() throws Exception {
        Polarizability pol = new Polarizability();
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCO");
        IAtomContainer before = mol.clone();
        IAtomContainer explicit = mol.clone();
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(explicit);

        double expected = pol.calculateBondPolarizability(explicit, explicit.getBond(1));
        double result = pol.calculateBondPolarizability(mol, mol.getBond(1));
        assertUnchanged(before, mol);
        Assertions.assertEquals(expected, result, 0.000001);
    }

    private void assertUnchanged(IAtomContainer before, IAtomContainer after) {
        Assertions.assertEquals(before.getAtomCount(), after.getAtomCount());
        for (int i = 0; i < before.getAtomCount(); i++) {
            Assertions.assertEquals(before.getAtom(i).getImplicitHydrogenCount(),
                    after.getAtom(i).getImplicitHydrogenCount(), "Implicit hydrogen count changed for atom " + i);
        }
    }

    /**
     *  A unit test for JUnit with n,n-dimethyl ethylendiamine
     */
    @Test
    void testCalculateBondPolarizability_IAtomContainer_IBond() throws Exception {
        Polarizability pol = new Polarizability();
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("NCCN(C)(C)");
        double result = pol.calculateBondPolarizability(mol, mol.getBond(0));
        Assertions.assertFalse(Double.isNaN(result));
    }

    /**
     *  A unit test for JUnit with methane
     */
    @Test
    void testCalculateKJMeanMolecularPolarizability() throws Exception {
        double testResult = 2.61;
        Polarizability pol = new Polarizability();
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C");
        double result = pol.calculateKJMeanMolecularPolarizability(mol);
        Assertions.assertEquals(testResult, result, 0.01);
    }

    /**
     *  A unit test for JUnit with Ethyl chloride
     */
    @Test
    void testcalculateGHEffectiveAtomPolarizability_Ethyl_chloride() throws Exception {
        double testResult = 4.62; /* from thesis Wolfgang Hanebeck, TUM */
        Polarizability pol = new Polarizability();
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCCl");
        double result = pol.calculateGHEffectiveAtomPolarizability(mol, mol.getAtom(2), 100, true);
        Assertions.assertEquals(testResult, result, 0.01);
    }

    /**
     *  A unit test for JUnit with Allyl bromide
     */
    @Test
    void testcalculateGHEffectiveAtomPolarizability_Allyl_bromide() throws Exception {
        double testResult = 6.17; /* from thesis Wolfgang Hanebeck, TUM */
        Polarizability pol = new Polarizability();
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C=CCBr");
        double result = pol.calculateGHEffectiveAtomPolarizability(mol, mol.getAtom(3), 100, true);
        Assertions.assertEquals(testResult, result, 0.01);
    }

    /**
     *  A unit test for JUnit with Isopentyl iodide
     */
    @Test
    void testcalculateGHEffectiveAtomPolarizability_Isopentyl_iodide() throws Exception {
        double testResult = 8.69; /* from thesis Wolfgang Hanebeck, TUM */
        Polarizability pol = new Polarizability();
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C(C)(C)CCI");
        double result = pol.calculateGHEffectiveAtomPolarizability(mol, mol.getAtom(5), 100, true);
        Assertions.assertEquals(testResult, result, 0.01);
    }

    /**
     *  A unit test for JUnit with Ethoxy ethane
     */
    @Test
    void testcalculateGHEffectiveAtomPolarizability_Ethoxy_ethane() throws Exception {
        double testResult = 5.21; /* from thesis Wolfgang Hanebeck, TUM */
        Polarizability pol = new Polarizability();
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCOCC");
        double result = pol.calculateGHEffectiveAtomPolarizability(mol, mol.getAtom(2), 100, true);
        Assertions.assertEquals(testResult, result, 0.01);
    }

    /**
     *  A unit test for JUnit with Ethanolamine
     */
    @Test
    void testcalculateGHEffectiveAtomPolarizability_Ethanolamine() throws Exception {
        double[] testResult = {4.26, 3.60}; /*
                                             * from thesis Wolfgang Hanebeck,
                                             * TUM
                                             */
        Polarizability pol = new Polarizability();
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("NCCO");
        double result = pol.calculateGHEffectiveAtomPolarizability(mol, mol.getAtom(3), 100, true);
        Assertions.assertEquals(testResult[1], result, 0.01);
        result = pol.calculateGHEffectiveAtomPolarizability(mol, mol.getAtom(0), 100, true);
        Assertions.assertEquals(testResult[0], result, 0.01);
    }

    /**
     *  A unit test for JUnit with Allyl mercaptan
     */
    @Test
    void testcalculateGHEffectiveAtomPolarizability_Allyl_mercaptan() throws Exception {
        double testResult = 6.25; /* from thesis Wolfgang Hanebeck, TUM */
        Polarizability pol = new Polarizability();
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C=CCS");
        double result = pol.calculateGHEffectiveAtomPolarizability(mol, mol.getAtom(3), 100, true);
        Assertions.assertEquals(testResult, result, 0.01);
    }
}
