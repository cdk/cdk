/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
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
package org.openscience.cdk.smiles;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo;
import org.openscience.cdk.isomorphism.IsomorphismTester;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.stereo.ExtendedCisTrans;
import org.openscience.cdk.stereo.ExtendedTetrahedral;
import org.openscience.cdk.stereo.Octahedral;
import org.openscience.cdk.stereo.SquarePlanar;
import org.openscience.cdk.stereo.TrigonalBipyramidal;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;
import org.openscience.cdk.tools.manipulator.BondManipulator;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Please see the test.gui package for visual feedback on tests.
 *
 * @author         steinbeck
 * @cdk.created    2003-09-19
 *
 * @see org.openscience.cdk.gui.smiles.SmilesParserTest
 */
class SmilesParserTest extends CDKTestCase {

    private static final SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());

    @Test
    void testSingleOrDoubleFlag() throws Exception {
        String smiles = "c1cccn1c2cccn2";

        // need to load the exact representation - this is SMILES string is
        // invalid and cannot be correctly kekulised
        IAtomContainer mol = loadExact(smiles);

        // single or double flags now assigned separately
        AtomContainerManipulator.setSingleOrDoubleFlags(mol);

        // Let's check the atoms first...
        Assertions.assertTrue(mol.getAtom(0).getFlag(IChemObject.SINGLE_OR_DOUBLE));
        Assertions.assertTrue(mol.getAtom(1).getFlag(IChemObject.SINGLE_OR_DOUBLE));
        Assertions.assertTrue(mol.getAtom(2).getFlag(IChemObject.SINGLE_OR_DOUBLE));
        Assertions.assertTrue(mol.getAtom(3).getFlag(IChemObject.SINGLE_OR_DOUBLE));
        Assertions.assertTrue(mol.getAtom(4).getFlag(IChemObject.SINGLE_OR_DOUBLE));

        Assertions.assertTrue(mol.getAtom(5).getFlag(IChemObject.SINGLE_OR_DOUBLE));
        Assertions.assertTrue(mol.getAtom(6).getFlag(IChemObject.SINGLE_OR_DOUBLE));
        Assertions.assertTrue(mol.getAtom(7).getFlag(IChemObject.SINGLE_OR_DOUBLE));
        Assertions.assertTrue(mol.getAtom(8).getFlag(IChemObject.SINGLE_OR_DOUBLE));
        Assertions.assertTrue(mol.getAtom(9).getFlag(IChemObject.SINGLE_OR_DOUBLE));
        // ...and then the bonds...
        // ...in the first ring...
        Assertions.assertTrue(mol.getBond(mol.getAtom(0), mol.getAtom(1)).getFlag(IChemObject.SINGLE_OR_DOUBLE));
        Assertions.assertTrue(mol.getBond(mol.getAtom(1), mol.getAtom(2)).getFlag(IChemObject.SINGLE_OR_DOUBLE));
        Assertions.assertTrue(mol.getBond(mol.getAtom(2), mol.getAtom(3)).getFlag(IChemObject.SINGLE_OR_DOUBLE));
        Assertions.assertTrue(mol.getBond(mol.getAtom(3), mol.getAtom(4)).getFlag(IChemObject.SINGLE_OR_DOUBLE));
        Assertions.assertTrue(mol.getBond(mol.getAtom(4), mol.getAtom(0)).getFlag(IChemObject.SINGLE_OR_DOUBLE));
        // ...then the bond in between the rings...
        Assertions.assertFalse(mol.getBond(mol.getAtom(4), mol.getAtom(5)).getFlag(IChemObject.SINGLE_OR_DOUBLE));
        // ...and at last the bonds in the other ring.
        Assertions.assertTrue(mol.getBond(mol.getAtom(5), mol.getAtom(6)).getFlag(IChemObject.SINGLE_OR_DOUBLE));
        Assertions.assertTrue(mol.getBond(mol.getAtom(6), mol.getAtom(7)).getFlag(IChemObject.SINGLE_OR_DOUBLE));
        Assertions.assertTrue(mol.getBond(mol.getAtom(7), mol.getAtom(8)).getFlag(IChemObject.SINGLE_OR_DOUBLE));
        Assertions.assertTrue(mol.getBond(mol.getAtom(8), mol.getAtom(9)).getFlag(IChemObject.SINGLE_OR_DOUBLE));
        Assertions.assertTrue(mol.getBond(mol.getAtom(9), mol.getAtom(5)).getFlag(IChemObject.SINGLE_OR_DOUBLE));
    }

    /**
     * 1-(1H-pyrrol-2-yl)pyrrole
     * @cdk.inchi InChI=1/C8H8N2/c1-2-7-10(6-1)8-4-3-5-9-8/h1-7,9H
     */
    @Test
    void pyrrolylpyrrole_invalid() throws InvalidSmilesException {
        Assertions.assertThrows(InvalidSmilesException.class,
                                () -> {
                                    load("c1cccn1c2cccn2");
                                });
    }

                                /**
     * 1-(1H-pyrrol-2-yl)pyrrole
     * @cdk.inchi InChI=1/C8H8N2/c1-2-7-10(6-1)8-4-3-5-9-8/h1-7,9H
     */
    @Test
    void pyrrolylpyrrole_valid() throws InvalidSmilesException {
        IAtomContainer m = load("c1cccn1c2ccc[nH]2");
        Assertions.assertNotNull(m);
    }

    /** @cdk.bug 1363882 */
    @Test
    void testBug1363882() throws Exception {
        String smiles = "[H]c2c([H])c(c1c(nc(n1([H]))C(F)(F)F)c2Cl)Cl";
        IAtomContainer mol = load(smiles);
        atomtype(mol);
        assertAtomTypesPerceived(mol);
        Assertions.assertEquals(18, mol.getAtomCount());
        Assertions.assertTrue(Aromaticity.cdkLegacy().apply(mol));
    }

    /** @cdk.bug 1535587 */
    @Test
    void testBug1535587() throws Exception {
        String smiles = "COC(=O)c2ccc3n([H])c1ccccc1c3(c2)";
        IAtomContainer mol = load(smiles);
        atomtype(mol);
        assertAtomTypesPerceived(mol);
        Assertions.assertEquals(18, mol.getAtomCount());
        Assertions.assertTrue(Aromaticity.cdkLegacy().apply(mol));
        Assertions.assertEquals("N", mol.getAtom(8).getSymbol());
        Assertions.assertTrue(mol.getAtom(8).getFlag(IChemObject.AROMATIC));
    }

    /** @cdk.bug 1579235 */
    @Test
    void testBug1579235() throws Exception {
        String smiles = "c2cc1cccn1cc2";
        IAtomContainer mol = load(smiles);
        atomtype(mol);
        assertAtomTypesPerceived(mol);
        Assertions.assertEquals(9, mol.getAtomCount());
        Assertions.assertTrue(Aromaticity.cdkLegacy().apply(mol));
        Assertions.assertEquals("N", mol.getAtom(6).getSymbol());
        for (IAtom atom : mol.atoms()) {
            if (atom.getAtomicNumber() == IElement.C) {
                Assertions.assertEquals(IAtomType.Hybridization.SP2, atom.getHybridization());
            } else {
                Assertions.assertEquals(IAtomType.Hybridization.PLANAR3, atom.getHybridization());
            }
        }
    }

    @Test
    void testBug1579229() throws Exception {
        String smiles = "c1c(c23)ccc(c34)ccc4ccc2c1";
        IAtomContainer mol = sp.parseSmiles(smiles);
        atomtype(mol);
        assertAtomTypesPerceived(mol);
        Assertions.assertEquals(14, mol.getAtomCount());
        Assertions.assertTrue(Aromaticity.cdkLegacy().apply(mol));
        for (IAtom atom : mol.atoms()) {
            Assertions.assertEquals(IAtomType.Hybridization.SP2, atom.getHybridization());
        }
    }

    /** @cdk.bug 1579230 */
    @Test
    void testBug1579230() throws Exception {
        String smiles = "Cc1cccc2sc3nncn3c12";
        IAtomContainer mol = load(smiles);
        atomtype(mol);
        assertAtomTypesPerceived(mol);
        Assertions.assertEquals(13, mol.getAtomCount());
        Assertions.assertTrue(Aromaticity.cdkLegacy().apply(mol));
        for (int i = 1; i < 13; i++) { // first atom is not aromatic
            IAtom atom = mol.getAtom(i);
            if (atom.getAtomicNumber() == IElement.C)
                Assertions.assertEquals(IAtomType.Hybridization.SP2, atom.getHybridization());
            if (atom.getAtomicNumber() == IElement.N || atom.getAtomicNumber() == IElement.S) {
                Assertions.assertTrue(IAtomType.Hybridization.SP2 == atom.getHybridization()
                        || IAtomType.Hybridization.PLANAR3 == atom.getHybridization());
            }
        }
    }

    @Test
    void testPyridine_N_oxideUncharged() throws Exception {
        String smiles = "O=n1ccccc1";
        IAtomContainer mol = loadExact(smiles);
        atomtype(mol);
        assertAtomTypesPerceived(mol);
        Assertions.assertEquals(7, mol.getAtomCount());
    }

    @Test
    void testPyridine_N_oxideCharged() throws Exception {
        String smiles = "[O-][n+]1ccccc1";
        IAtomContainer mol = sp.parseSmiles(smiles);
        assertAtomTypesPerceived(mol);
        Assertions.assertEquals(7, mol.getAtomCount());
    }

    @Test
    void testPositivePhosphor() throws Exception {
        String smiles = "[Cl+3]([O-])([O-])([O-])[O-].[P+]([O-])(c1ccccc1)(c1ccccc1)c1cc([nH0+](C)c(c1)c1ccccc1)c1ccccc1";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(0, mol.getAtom(22).getImplicitHydrogenCount().intValue());
        assertAtomTypesPerceived(mol);
        Assertions.assertEquals(38, mol.getAtomCount());
        Assertions.assertEquals("P", mol.getAtom(5).getSymbol());
        Assertions.assertEquals(+1, mol.getAtom(5).getFormalCharge().intValue());
        Assertions.assertEquals("Cl", mol.getAtom(0).getSymbol());
        Assertions.assertEquals(+3, mol.getAtom(0).getFormalCharge().intValue());
    }

    /*
     * The next methods tests compounds with several conjugated rings These
     * compounds would not fail if the Aromaticity Detection was changed so that
     * a ring is aromatic if all the atoms in a ring have already been flagged
     * as aromatic from the testing of other rings in the system.
     */
    @Test
    void testUnusualConjugatedRings() throws Exception {
        //7090-41-7:
        String smiles = "c1(Cl)cc2c3cc(Cl)c(Cl)cc3c2cc1Cl";
        IAtomContainer mol = sp.parseSmiles(smiles);
        assertAtomTypesPerceived(mol);
        Assertions.assertEquals(16, mol.getAtomCount());
    }

    @Test
    void testUnusualConjugatedRings_2() throws Exception {
        //206-44-0:
        String smiles = "c(c(ccc1)ccc2)(c1c(c3ccc4)c4)c23";
        IAtomContainer mol = sp.parseSmiles(smiles);
        assertAtomTypesPerceived(mol);
        Assertions.assertEquals(16, mol.getAtomCount());
    }

    @Test
    void testUnusualConjugatedRings_3() throws Exception {
        Assumptions.assumeTrue(runSlowTests());

        //207-08-9:
        String smiles = "c2ccc1cc3c(cc1c2)c4cccc5cccc3c45";
        IAtomContainer mol = sp.parseSmiles(smiles);
        assertAtomTypesPerceived(mol);
        Assertions.assertEquals(20, mol.getAtomCount());
    }

    @Test
    void testUnusualConjugatedRings_4() throws Exception {
        //2693-46-1:
        String smiles = "Nc1c(c23)cccc3c4ccccc4c2cc1";
        IAtomContainer mol = sp.parseSmiles(smiles);
        assertAtomTypesPerceived(mol);
        Assertions.assertEquals(17, mol.getAtomCount());
    }

    @Test
    void testUnusualConjugatedRings_5() throws Exception {
        //205-99-2:
        String smiles = "c12ccccc1cc3c4ccccc4c5c3c2ccc5";
        IAtomContainer mol = sp.parseSmiles(smiles);
        assertAtomTypesPerceived(mol);
        Assertions.assertEquals(20, mol.getAtomCount());
    }

    @Test
    void test187_78_0() throws Exception {
        // are all 4 rings aromatic? Is smiles correct?
        String smiles = "c1c(c23)ccc(c34)ccc4ccc2c1";
        IAtomContainer mol = sp.parseSmiles(smiles);
        assertAtomTypesPerceived(mol);
        Assertions.assertEquals(14, mol.getAtomCount());
    }

    @Test
    void test187_78_0_PubChem() throws Exception {
        // are all 4 rings aromatic? Is smiles correct?
        String smiles = "C1=CC2=C3C(=CC=C4C3=C1C=C4)C=C2";
        IAtomContainer mol = sp.parseSmiles(smiles);
        assertAtomTypesPerceived(mol);
        Assertions.assertEquals(14, mol.getAtomCount());
    }

    @Test
    void test41814_78_2() throws Exception {
        String smiles = "Cc1cccc2sc3nncn3c12";
        IAtomContainer mol = sp.parseSmiles(smiles);
        assertAtomTypesPerceived(mol);
        Assertions.assertEquals(13, mol.getAtomCount());
    }

    @Test
    void test239_64_5() throws Exception {
        String smiles = "c1ccc4c(c1)ccc5c3ccc2ccccc2c3[nH]c45";
        IAtomContainer mol = sp.parseSmiles(smiles);
        assertAtomTypesPerceived(mol);
        Assertions.assertEquals(21, mol.getAtomCount());
    }

    @Test
    void test239_64_5_invalid() throws Exception {
        Assertions.assertThrows(InvalidSmilesException.class,
                                () -> {
                                    load("c1ccc4c(c1)ccc5c3ccc2ccccc2c3nc45");
                                });
    }

    /*
     * Compounds like Indolizine (274-40-8) with a fused nitrogen as part of a 6
     * membered ring and another ring do not parse
     */
    @Test
    void testIndolizine() throws Exception {
        String smiles = "c2cc1cccn1cc2";
        IAtomContainer mol = sp.parseSmiles(smiles);
        assertAtomTypesPerceived(mol);
        Assertions.assertEquals(9, mol.getAtomCount());
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    void testSmiles1() throws Exception {
        String smiles = "C1c2c(c3c(c(O)cnc3)cc2)CC(=O)C1";
        IAtomContainer molecule = sp.parseSmiles(smiles);
        assertAtomTypesPerceived(molecule);
        Assertions.assertEquals(16, molecule.getAtomCount());
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    void testSmiles2() throws Exception {
        String smiles = "O=C(O3)C1=COC(OC4OC(CO)C(O)C(O)C4O)C2C1C3C=C2COC(C)=O";
        IAtomContainer molecule = sp.parseSmiles(smiles);
        assertAtomTypesPerceived(molecule);
        Assertions.assertEquals(29, molecule.getAtomCount());
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    void testSmiles3() throws Exception {
        String smiles = "CN1C=NC2=C1C(N(C)C(N2C)=O)=O";
        IAtomContainer molecule = sp.parseSmiles(smiles);
        assertAtomTypesPerceived(molecule);
        Assertions.assertEquals(14, molecule.getAtomCount());
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    void testSmiles4() throws Exception {
        String smiles = "CN(C)CCC2=CNC1=CC=CC(OP(O)(O)=O)=C12";
        IAtomContainer molecule = sp.parseSmiles(smiles);
        assertAtomTypesPerceived(molecule);
        Assertions.assertEquals(19, molecule.getAtomCount());
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    void testSmiles5() throws Exception {
        String smiles = "O=C(O)C1C(OC(C3=CC=CC=C3)=O)CC2N(C)C1CC2";
        IAtomContainer molecule = sp.parseSmiles(smiles);
        assertAtomTypesPerceived(molecule);
        Assertions.assertEquals(21, molecule.getAtomCount());
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    void testSmiles6() throws Exception {
        String smiles = "C1(C2(C)(C))C(C)=CCC2C1";
        IAtomContainer molecule = sp.parseSmiles(smiles);
        assertAtomTypesPerceived(molecule);
        Assertions.assertEquals(10, molecule.getAtomCount());
    }

    @Test
    void testSmiles7() throws Exception {
        String smiles = "C1(C=C(C=C(C=C(C=C(C=CC%35=C%36)C%31=C%35C%32=C%33C%36=C%34)C%22=C%31C%23=C%32C%24=C%25C%33=C%26C%34=CC%27=CC%28=CC=C%29)C%14=C%22C%15=C%23C%16=C%24C%17=C%18C%25=C%19C%26=C%27C%20=C%28C%29=C%21)C6=C%14C7=C%15C8=C%16C9=C%17C%12=C%11C%18=C%10C%19=C%20C%21=CC%10=CC%11=CC(C=C%30)=C%12%13)=C(C6=C(C7=C(C8=C(C9=C%13C%30=C5)C5=C4)C4=C3)C3=C2)C2=CC=C1";
        IAtomContainer molecule = sp.parseSmiles(smiles);
        assertAtomTypesPerceived(molecule);
        Assertions.assertNotNull(molecule);
    }

    @Test
    void testSmiles8() throws Exception {
        String smiles = "CC1(C(=C(CC(C1)O)C)C=CC(=CC=CC(=CC=CC=C(C=CC=C(C=CC1=C(CC(CC1(C)C)O)C)C)C)C)C)C";
        IAtomContainer molecule = sp.parseSmiles(smiles);
        assertAtomTypesPerceived(molecule);
        Assertions.assertNotNull(molecule);
    }

    @Test
    void testSmiles9() throws Exception {
        String smiles = "NC(C(C)C)C(NC(C(C)O)C(NC(C(C)C)C(NC(CCC(N)=O)C(NC(CC([O-])[O-])C(NCC(NC(CC(N)=O)C(NC(Cc1ccccc1)C(NC(CO)C(NC(Cc2ccccc2)C(NC(CO)C(NC(CC(C)C)C(NC(CCC([O-])[O-])C(NC(CO)C(NC(C(C)C)C(NC(CCCC[N+])C(NC(CCCC[N+])C(NC(CC(C)C)C(NC(CCCC[N+])C(NC(CC([O-])[O-])C(NC(CC(C)C)C(NC(CCC(N)=O)C(NC(CCC([O-])[O-])C(N3CCCC3C(NC(CCC(N)=O)C(NC(CCC([O-])[O-])C(N4CCCC4C(NC(CCCNC([N+])[N+])C(NC(C(C)C)C(NCC(NC(CCCC[N+])C(NC(CC(C)C)C(NC(CCCNC([N+])[N+])C(NC(CC(N)=O)C(NC(Cc5ccccc5)C(NC(C)C(N6CCCC6C(NC(C(C)CC)C(N7CCCC7C(NCC(NC(CCC([O-])[O-])C(N8CCCC8C(NC(C(C)C)C(NC(C(C)C)C(N9CCCC9C(NC(C(C)CC)C(NC(CC(C)C)C(NC%19C[S][S]CC(C(NC(CCCC[N+])C(NC(CCC([O-])[O-])C(N%10CCCC%10C(NC(CC(N)=O)C(NC(C)C(NC(CCC(N)=O)C(NC(CCC([O-])[O-])C(NC(C(C)CC)C(NC(CC(C)C)C(NC(CCC(N)=O)C(NC(CCCNC([N+])[N+])C(NC(CC(C)C)C(NC(CCC([O-])[O-])C(NC(CCC([O-])[O-])C(NC(C(C)CC)C(NC(C)C(NC(CCC([O-])[O-])C(NC(CC([O-])[O-])C(N%11CCCC%11C(NCC(NC(C(C)O)C(NC%14C[S][S]CC%13C(NC(C(C)O)C(NCC(NC(C[S][S]CC(C(NC(C)C(NC(Cc%12ccc(O)cc%12)C(NC(C)C(NC(C)C(N%13)=O)=O)=O)=O)=O)NC(=O)C(C(C)CC)NC(=O)C(CCC([O-])[O-])NC%14=O)C(O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)NC(=O)C(CC(C)C)NC(=O)C%15CCCN%15C(=O)C(CCCC[N+])NC(=O)C(CC(C)C)NC(=O)C(CCC([O-])[O-])NC(=O)C(CCC([O-])[O-])NC(=O)C%16CCCN%16C(=O)C(Cc%17ccccc%17)NC(=O)C(CC(N)=O)NC(=O)C%18CCCN%18C(=O)C(CC(N)=O)NC(=O)C(CO)NC%19=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O";
        IAtomContainer molecule = sp.parseSmiles(smiles);
        assertAtomTypesPerceived(molecule);
        Assertions.assertNotNull(molecule);
    }

    /**
     * @cdk.bug 1296113
     */
    @Test
    void testSFBug1296113() throws Exception {
        String smiles = "S(=O)(=O)(-O)-c1c2c(c(ccc2-N-c2ccccc2)-N=N-c2c3c(c(cc2)-N=N-c2c4c(c(ccc4)-S(=O)(=O)-O)ccc2)cccc3)ccc1";
        IAtomContainer molecule = sp.parseSmiles(smiles);
        assertAtomTypesPerceived(molecule);
        Assertions.assertNotNull(molecule);
    }

    /**
     * @cdk.bug 1324105
     */
    @Test
    void testAromaticSmiles2() throws Exception {
        String smiles = "n12:n:n:n:c:2:c:c:c:c:1";
        IAtomContainer molecule = loadExact(smiles);
        assertAtomTypesPerceived(molecule);
        for (IBond iBond : molecule.bonds()) Assertions.assertTrue(iBond.getFlag(IChemObject.AROMATIC));
    }

    /**
     * A unit test for JUnit. It is currently ignored because the SMILES
     * given is invalid: the negative has an implied zero hydrogen count,
     * making it have an unfilled valency.
     */
    @Disabled
    @Test
    void testAromaticSmilesWithCharge() throws Exception {
        String smiles = "c1cc[c-]c1";
        IAtomContainer molecule = sp.parseSmiles(smiles);
        assertAtomTypesPerceived(molecule);
        Assertions.assertTrue(molecule.getAtom(0).getFlag(IChemObject.AROMATIC));
        Assertions.assertTrue(molecule.getBond(0).getFlag(IChemObject.AROMATIC));
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    void testAromaticSmiles() throws Exception {
        String smiles = "c1ccccc1";
        IAtomContainer molecule = sp.parseSmiles(smiles);
        for (IBond bond : molecule.bonds())
            Assertions.assertTrue(bond.getFlag(IChemObject.AROMATIC));
    }

    /**
     * @cdk.bug 630475
     */
    @Test
    void testSFBug630475() throws Exception {
        String smiles = "CC1(C(=C(CC(C1)O)C)C=CC(=CC=CC(=CC=CC=C(C=CC=C(C=CC1=C(CC(CC1(C)C)O)C)C)C)C)C)C";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertTrue(mol.getAtomCount() > 0);
    }

    /**
     * @cdk.bug 585811
     */
    @Test
    void testSFBug585811() throws Exception {
        String smiles = "CC(C(C8CCC(CC8)=O)C3C4C(CC5(CCC(C9=CC(C=CN%10)=C%10C=C9)CCCC5)C4)C2CCC1CCC7(CCC7)C6(CC6)C1C2C3)=O";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertTrue(mol.getAtomCount() > 0);
    }

    /**
     * @cdk.bug 593648
     */
    @Test
    void testSFBug593648() throws Exception {
        String smiles = "CC1=CCC2CC1C(C)2C";
        IAtomContainer mol = sp.parseSmiles(smiles);

        IAtomContainer apinene = mol.getBuilder().newInstance(IAtomContainer.class);
        apinene.addAtom(mol.getBuilder().newInstance(IAtom.class, "C"));
        // 1
        apinene.addAtom(mol.getBuilder().newInstance(IAtom.class, "C"));
        // 2
        apinene.addAtom(mol.getBuilder().newInstance(IAtom.class, "C"));
        // 3
        apinene.addAtom(mol.getBuilder().newInstance(IAtom.class, "C"));
        // 4
        apinene.addAtom(mol.getBuilder().newInstance(IAtom.class, "C"));
        // 5
        apinene.addAtom(mol.getBuilder().newInstance(IAtom.class, "C"));
        // 6
        apinene.addAtom(mol.getBuilder().newInstance(IAtom.class, "C"));
        // 7
        apinene.addAtom(mol.getBuilder().newInstance(IAtom.class, "C"));
        // 8
        apinene.addAtom(mol.getBuilder().newInstance(IAtom.class, "C"));
        // 9
        apinene.addAtom(mol.getBuilder().newInstance(IAtom.class, "C"));
        // 10

        apinene.addBond(0, 1, IBond.Order.DOUBLE);
        // 1
        apinene.addBond(1, 2, IBond.Order.SINGLE);
        // 2
        apinene.addBond(2, 3, IBond.Order.SINGLE);
        // 3
        apinene.addBond(3, 4, IBond.Order.SINGLE);
        // 4
        apinene.addBond(4, 5, IBond.Order.SINGLE);
        // 5
        apinene.addBond(5, 0, IBond.Order.SINGLE);
        // 6
        apinene.addBond(0, 6, IBond.Order.SINGLE);
        // 7
        apinene.addBond(3, 7, IBond.Order.SINGLE);
        // 8
        apinene.addBond(5, 7, IBond.Order.SINGLE);
        // 9
        apinene.addBond(7, 8, IBond.Order.SINGLE);
        // 10
        apinene.addBond(7, 9, IBond.Order.SINGLE);
        // 11

        IsomorphismTester it = new IsomorphismTester(apinene);
        Assertions.assertTrue(it.isIsomorphic(mol.getBuilder().newInstance(IAtomContainer.class, mol)));
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    void testReadingOfTwoCharElements() throws Exception {
        String smiles = "[Na+]";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(1, mol.getAtomCount());
        Assertions.assertEquals("Na", mol.getAtom(0).getSymbol());
    }

    @Test
    void testReadingOfOneCharElements() throws Exception {
        String smiles = "[K+]";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(1, mol.getAtomCount());
        Assertions.assertEquals("K", mol.getAtom(0).getSymbol());
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    void testOrganicSubsetUnderstanding() throws Exception {
        String smiles = "[Ni+2]";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(1, mol.getAtomCount());
        Assertions.assertEquals("Ni", mol.getAtom(0).getSymbol());

        smiles = "Co";
        mol = loadExact(smiles);
        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertEquals("C", mol.getAtom(0).getSymbol());
        Assertions.assertEquals("O", mol.getAtom(1).getSymbol());
    }

    // note we can't kekulise 'Co' (above) but we can kekulise 'Cocc'
    @Test
    void testOrganicSubsetUnderstanding2() throws Exception {
        IAtomContainer mol = load("Cocc");
        assertThat(mol.getBond(0).getOrder(), is(IBond.Order.SINGLE));
        assertThat(mol.getBond(1).getOrder(), is(IBond.Order.SINGLE));
        assertThat(mol.getBond(2).getOrder(), is(IBond.Order.DOUBLE));
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    void testMassNumberReading() throws Exception {
        String smiles = "[13C]";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(1, mol.getAtomCount());
        Assertions.assertEquals("C", mol.getAtom(0).getSymbol());
        Assertions.assertEquals(13, mol.getAtom(0).getMassNumber().intValue());
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    void testFormalChargeReading() throws Exception {
        String smiles = "[OH-]";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(1, mol.getAtomCount());
        Assertions.assertEquals("O", mol.getAtom(0).getSymbol());
        Assertions.assertEquals(-1, mol.getAtom(0).getFormalCharge().intValue());
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    void testReadingPartionedMolecules() throws Exception {
        String smiles = "[Na+].[OH-]";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertEquals(0, mol.getBondCount());
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    void testExplicitSingleBond() throws Exception {
        String smiles = "C-C";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertEquals(1, mol.getBondCount());
        Assertions.assertEquals(IBond.Order.SINGLE, mol.getBond(0).getOrder());
    }

    /**
     * @cdk.bug 1175478
     */
    @Test
    void testSFBug1175478() throws Exception {
        String smiles = "c1cc-2c(cc1)C(c3c4c2onc4c(cc3N5CCCC5)N6CCCC6)=O";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(27, mol.getAtomCount());
        Assertions.assertEquals(32, mol.getBondCount());
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    void testUnkownAtomType() throws Exception {
        String smiles = "*C";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertEquals(1, mol.getBondCount());
        Assertions.assertTrue(mol.getAtom(0) instanceof IPseudoAtom);
        Assertions.assertFalse(mol.getAtom(1) instanceof IPseudoAtom);

        smiles = "[*]C";
        mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertEquals(1, mol.getBondCount());
        Assertions.assertTrue(mol.getAtom(0) instanceof IPseudoAtom);
        Assertions.assertFalse(mol.getAtom(1) instanceof IPseudoAtom);
    }

    /**
     * @cdk.bug 2596061
     * @throws InvalidSmilesException
     */
    @Test
    void testUnknownAtomType2() throws InvalidSmilesException {
        String smiles = "[12*H2-]";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(1, mol.getAtomCount());
        Assertions.assertEquals(0, mol.getBondCount());
        Assertions.assertTrue(mol.getAtom(0) instanceof IPseudoAtom);
        Assertions.assertEquals(12, mol.getAtom(0).getMassNumber().intValue());
        Assertions.assertEquals(2, mol.getAtom(0).getImplicitHydrogenCount().intValue());
        Assertions.assertEquals(-1, mol.getAtom(0).getFormalCharge().intValue());
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    void testBondCreation() throws Exception {
        String smiles = "CC";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertEquals(1, mol.getBondCount());

        smiles = "cc";
        mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertEquals(1, mol.getBondCount());
    }

    /**
     * @cdk.bug 784433
     */
    @Test
    void testSFBug784433() throws Exception {
        String smiles = "c1cScc1";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(5, mol.getAtomCount());
        Assertions.assertEquals(5, mol.getBondCount());
    }

    /**
     * @cdk.bug 873783.
     */
    @Test
    void testProton() throws Exception {
        String smiles = "[H+]";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(1, mol.getAtomCount());
        Assertions.assertEquals(1, mol.getAtom(0).getFormalCharge().intValue());
    }

    /**
     * @cdk.bug 881330.
     */
    @Test
    void testSMILESFromXYZ() throws Exception {
        String smiles = "C.C.N.[Co].C.C.C.[H].[He].[H].[H].[H].[H].C.C.[H].[H].[H].[H].[H]";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(20, mol.getAtomCount());
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    void testSingleBracketH() throws Exception {
        String smiles = "[H]";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(1, mol.getAtomCount());
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    void testSingleH() throws Exception {
        // Beam allows bare 'H' - this is a common typo for '[H]' - there is
        // a 'strict' option which won't allow these but this isn't exposed
        // in the public API yet
        IAtomContainer mol = load("H");
        assertThat(mol.getAtom(0).getAtomicNumber(), is(1));
        assertThat(mol.getAtomCount(), is(1));
    }

    @Test
    void testSingleD() throws Exception {
        // Beam allows bare 'D' - this is a common typo for '[2H]' - there is
        // a 'strict' option which won't allow these but this isn't exposed
        // in the public API yet
        IAtomContainer mol = load("D");
        assertThat(mol.getAtomCount(), is(1));
        assertThat(mol.getAtom(0).getAtomicNumber(), is(1));
        assertThat(mol.getAtom(0).getMassNumber(), is(2));
    }

    @Test
    void testSingleT() throws Exception {
        // Beam allows bare 'T' - this is a common typo for '[3H]' - there is
        // a 'strict' option which won't allow these but this isn't exposed
        // in the public API yet
        IAtomContainer mol = load("T");
        assertThat(mol.getAtomCount(), is(1));
        assertThat(mol.getAtom(0).getAtomicNumber(), is(1));
        assertThat(mol.getAtom(0).getMassNumber(), is(3));
    }

    /**
     * @cdk.bug 862930.
     */
    @Test
    void testHydroxonium() throws Exception {
        String smiles = "[H][O+]([H])[H]";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(4, mol.getAtomCount());
    }

    /**
     * @cdk.bug 809412
     */
    @Test
    void testSFBug809412() throws Exception {
        String smiles = "Nc4cc3[n+](c2c(c1c(cccc1)cc2)nc3c5c4cccc5)c6c7c(ccc6)cccc7";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(33, mol.getAtomCount());
    }

    /**
     * A bug found with JCP.
     *
     * @cdk.bug 956926
     */
    @Test
    void testSFBug956926() throws Exception {
        String smiles = "[c+]1ccccc1";
        // C6H5+, phenyl cation
        IAtomContainer mol = sp.parseSmiles(smiles);
        assertAtomTypesPerceived(mol);
        Assertions.assertEquals(6, mol.getAtomCount());
        Assertions.assertEquals(1, mol.getAtom(0).getFormalCharge().intValue());

        // I can also check whether all carbons have exact two neighbors
        for (int i = 0; i < mol.getAtomCount(); i++) {
            Assertions.assertEquals(2, mol.getConnectedBondsCount(mol.getAtom(i)));
        }
        // and the number of implicit hydrogens
        int hCount = 0;
        for (int i = 0; i < mol.getAtomCount(); i++) {
            hCount += mol.getAtom(i).getImplicitHydrogenCount();
        }
        Assertions.assertEquals(5, hCount);
    }

    /**
     * A bug found with JCP.
     *
     * @cdk.bug   956929
     * @cdk.inchi InChI=1/C4H5N/c1-2-4-5-3-1/h1-5H
     *
     * @see #testPyrrole()
     */
    @Test
    void testPyrrole() throws Exception {
        String smiles = "c1ccc[NH]1";
        IAtomContainer mol = sp.parseSmiles(smiles);
        for (int i = 0; i < mol.getAtomCount(); i++) {
            if (mol.getAtom(i).getAtomicNumber() == IElement.N) {
                Assertions.assertEquals(IBond.Order.SINGLE, mol.getConnectedBondsList(mol.getAtom(i)).get(0).getOrder());
                Assertions.assertEquals(IBond.Order.SINGLE, mol.getConnectedBondsList(mol.getAtom(i)).get(1).getOrder());
            }
        }
    }

    /**
     * @cdk.bug 2679607
     * @throws Exception
     */
    @Test
    void testHardCodedHydrogenCount() throws Exception {
        String smiles = "c1ccc[NH]1";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(1, mol.getAtom(4).getImplicitHydrogenCount().intValue());

        smiles = "[n]1cc[nH]c1";
        mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(1, mol.getAtom(4).getImplicitHydrogenCount().intValue());
        Assertions.assertEquals(0, mol.getAtom(0).getImplicitHydrogenCount().intValue());

        smiles = "[nH]1cc[n]c1";
        mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(1, mol.getAtom(0).getImplicitHydrogenCount().intValue());
        Assertions.assertEquals(0, mol.getAtom(3).getImplicitHydrogenCount().intValue());
    }

    /**
     * @throws Exception
     * @cdk.bug 2679607
     */
    @Test
    void testHardCodedHydrogenCount2() throws Exception {
        String smiles = "[CH2]CNC";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(2, mol.getAtom(0).getImplicitHydrogenCount().intValue());
    }

    /**
     * A bug found with JCP.
     *
     * @cdk.bug 956929
     */
    @Test
    void testSFBug956929() throws Exception {
        String smiles = "Cn1cccc1";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(6, mol.getAtomCount());
        // I can also check whether the total neighbor count around the
        // nitrogen is 3, all single bonded
        org.openscience.cdk.interfaces.IAtom nitrogen = mol.getAtom(1);
        // the second atom
        Assertions.assertEquals("N", nitrogen.getSymbol());
        List<IBond> bondsList = mol.getConnectedBondsList(nitrogen);
        Assertions.assertEquals(3, bondsList.size());
        int totalBondOrder = BondManipulator.getSingleBondEquivalentSum(bondsList);
        Assertions.assertEquals(3.0, totalBondOrder, 0.001);
    }

    /**
     * A bug found with JCP.
     *
     * @cdk.bug 956921
     */
    @Test
    void testSFBug956921() throws Exception {
        String smiles = "[cH-]1cccc1";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(5, mol.getAtomCount());
        // each atom should have 1 implicit hydrogen, and two neighbors
        for (IAtom atomi : mol.atoms()) {
            Assertions.assertEquals(1, atomi.getImplicitHydrogenCount().intValue());
            Assertions.assertEquals(2, mol.getConnectedBondsCount(atomi));
        }
        // and the first atom should have a negative charge
        Assertions.assertEquals(-1, mol.getAtom(0).getFormalCharge().intValue());
    }

    /**
     * @cdk.bug 1274464
     */
    @Test
    void testSFBug1274464() throws Exception {
        IAtomContainer fromSmiles = new SmilesParser(DefaultChemObjectBuilder.getInstance()).parseSmiles("C1=CC=CC=C1");
        IAtomContainer fromFactory = TestMoleculeFactory.makeBenzene();
        CDKHydrogenAdder hAdder = CDKHydrogenAdder.getInstance(fromFactory.getBuilder());
        Iterator<IAtom> atoms = fromFactory.atoms().iterator();
        CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(fromFactory.getBuilder());
        while (atoms.hasNext()) {
            IAtom nextAtom = atoms.next();
            IAtomType type = matcher.findMatchingAtomType(fromFactory, nextAtom);
            AtomTypeManipulator.configure(nextAtom, type);
            hAdder.addImplicitHydrogens(fromFactory, nextAtom);
        }
        atomtype(fromSmiles);
        Aromaticity.cdkLegacy().apply(fromSmiles);
        Aromaticity.cdkLegacy().apply(fromFactory);
        boolean result = new UniversalIsomorphismTester().isIsomorph(fromFactory, fromSmiles);
        Assertions.assertTrue(result);
    }

    /**
     * @cdk.bug 1095696
     */
    @Test
    void testSFBug1095696() throws Exception {
        String smiles = "Nc1ncnc2[nH]cnc12";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(10, mol.getAtomCount());
        Assertions.assertEquals("N", mol.getAtom(6).getSymbol());
        Assertions.assertEquals(1, mol.getAtom(6).getImplicitHydrogenCount().intValue());
    }

    /**
     *  Example taken from 'Handbook of Chemoinformatics', Gasteiger, 2003, page 89
     *  (Part I).
     */
    @Test
    void testNonBond() throws Exception {
        String sodiumPhenoxide = "c1cc([O-].[Na+])ccc1";
        IAtomContainer mol = sp.parseSmiles(sodiumPhenoxide);
        Assertions.assertEquals(8, mol.getAtomCount());
        Assertions.assertEquals(7, mol.getBondCount());

        IAtomContainerSet fragments = ConnectivityChecker.partitionIntoMolecules(mol);
        int fragmentCount = fragments.getAtomContainerCount();
        Assertions.assertEquals(2, fragmentCount);
        IAtomContainer mol1 = fragments.getAtomContainer(0);
        IAtomContainer mol2 = fragments.getAtomContainer(1);
        // one should have one atom, the other seven atoms
        // in any order, so just test the difference
        Assertions.assertEquals(6, Math.abs(mol1.getAtomCount() - mol2.getAtomCount()));
    }

    /**
     *  Example taken from 'Handbook of Chemoinformatics', Gasteiger, 2003, page 89
     *  (Part I).
     */
    @Test
    void testConnectedByRingClosure() throws Exception {
        String sodiumPhenoxide = "C1.O2.C12";
        IAtomContainer mol = sp.parseSmiles(sodiumPhenoxide);
        Assertions.assertEquals(3, mol.getAtomCount());
        Assertions.assertEquals(2, mol.getBondCount());

        IAtomContainerSet fragments = ConnectivityChecker.partitionIntoMolecules(mol);
        int fragmentCount = fragments.getAtomContainerCount();
        Assertions.assertEquals(1, fragmentCount);
        IAtomContainer mol1 = fragments.getAtomContainer(0);
        Assertions.assertEquals(3, mol1.getAtomCount());
    }

    @Test
    void testConnectedByRingClosure_TwoAtom() throws Exception {
        String methanol = "C1.O1";
        IAtomContainer mol = sp.parseSmiles(methanol);
        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertEquals(1, mol.getBondCount());

        IAtomContainerSet fragments = ConnectivityChecker.partitionIntoMolecules(mol);
        int fragmentCount = fragments.getAtomContainerCount();
        Assertions.assertEquals(1, fragmentCount);
        IAtomContainer mol1 = fragments.getAtomContainer(0);
        Assertions.assertEquals(2, mol1.getAtomCount());
    }

    /**
     *  Example taken from 'Handbook of Chemoinformatics', Gasteiger, 2003, page 89
     *  (Part I).
     */
    @Test
    void testReaction() throws Exception {
        String reactionSmiles = "O>>[H+].[OH-]";
        IReaction reaction = sp.parseReactionSmiles(reactionSmiles);
        Assertions.assertEquals(1, reaction.getReactantCount());
        Assertions.assertEquals(2, reaction.getProductCount());
    }

    @Test
    void noReactants() throws Exception {
        IReaction reaction = sp.parseReactionSmiles(">>C");
        Assertions.assertEquals(0, reaction.getReactantCount());
        Assertions.assertEquals(1, reaction.getProductCount());
    }

    @Test
    void noProducts() throws Exception {
        IReaction reaction = sp.parseReactionSmiles("C>>");
        Assertions.assertEquals(1, reaction.getReactantCount());
        Assertions.assertEquals(0, reaction.getProductCount());
    }

    @Test
    void noReaction() throws Exception {
        IReaction reaction = sp.parseReactionSmiles(">>");
        Assertions.assertEquals(0, reaction.getReactantCount());
        Assertions.assertEquals(0, reaction.getProductCount());
    }

    @Test
    void onlyAgents() throws Exception {
        IReaction reaction = sp.parseReactionSmiles(">C>");
        Assertions.assertEquals(0, reaction.getReactantCount());
        Assertions.assertEquals(1, reaction.getAgents().getAtomContainerCount());
        Assertions.assertEquals(0, reaction.getProductCount());
    }

    /**
     *  Example taken from 'Handbook of Chemoinformatics', Gasteiger, 2003, page 90
     *  (Part I).
     */
    @Test
    void testReactionWithAgents() throws Exception {
        String reactionSmiles = "CCO.CC(=O)O>[H+]>CC(=O)OCC.O";
        IReaction reaction = sp.parseReactionSmiles(reactionSmiles);
        Assertions.assertEquals(2, reaction.getReactantCount());
        Assertions.assertEquals(2, reaction.getProductCount());
        Assertions.assertEquals(1, reaction.getAgents().getAtomContainerCount());

        Assertions.assertEquals(1, reaction.getAgents().getAtomContainer(0).getAtomCount());
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    void testImplicitHydrogenCount() throws Exception {
        String smiles = "C";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(1, mol.getAtomCount());
        Assertions.assertEquals(4, mol.getAtom(0).getImplicitHydrogenCount().intValue());
    }

    /**
     * @cdk.bug 2028780
     */
    @Test
    void testTungsten() throws Exception {
        String smiles = "[W]";
        IAtomContainer mol = sp.parseSmiles(smiles);
        assertAtomTypesPerceived(mol);
        Assertions.assertEquals(1, mol.getAtomCount());
        Assertions.assertEquals("W", mol.getAtom(0).getSymbol());
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    void testImplicitHydrogenCount2() throws Exception {
        String smiles = "CC";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertEquals(3, mol.getAtom(0).getImplicitHydrogenCount().intValue());
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    void testImplicitHydrogenCount2b() throws Exception {
        String smiles = "C=C";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertEquals(2, mol.getAtom(0).getImplicitHydrogenCount().intValue());
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    void testImplicitHydrogenCount2c() throws Exception {
        String smiles = "C#C";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertEquals(1, mol.getAtom(0).getImplicitHydrogenCount().intValue());
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    void testImplicitHydrogenCount3() throws Exception {
        String smiles = "CCC";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(3, mol.getAtomCount());
        Assertions.assertEquals(2, mol.getAtom(1).getImplicitHydrogenCount().intValue());
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    void testImplicitHydrogenCount4() throws Exception {
        String smiles = "C1CCCCC1";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(6, mol.getAtomCount());
        Assertions.assertEquals(2, mol.getAtom(0).getImplicitHydrogenCount().intValue());
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    void testImplicitHydrogenCount4a() throws Exception {
        String smiles = "c1=cc=cc=c1";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(6, mol.getAtomCount());
        Assertions.assertEquals(1, mol.getAtom(0).getImplicitHydrogenCount().intValue());
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    void testImplicitHydrogenCount4b() throws Exception {
        String smiles = "c1ccccc1";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(6, mol.getAtomCount());
        Assertions.assertEquals(1, mol.getAtom(0).getImplicitHydrogenCount().intValue());
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    void testHOSECodeProblem() throws Exception {
        String smiles = "CC=CBr";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(4, mol.getAtomCount());
        Assertions.assertEquals("Br", mol.getAtom(3).getSymbol());
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    void testPyridine() throws Exception {
        IAtomContainer mol = load("c1ccncc1");
        atomtype(mol);
        Assertions.assertEquals(6, mol.getAtomCount());
        // I can also check whether the total neighbor count around the
        // nitrogen is 3, all single bonded
        IAtom nitrogen = mol.getAtom(3);
        // the second atom
        Assertions.assertEquals("N", nitrogen.getSymbol());
        for (IAtom iAtom : mol.atoms()) {
            Assertions.assertEquals(IAtomType.Hybridization.SP2, iAtom.getHybridization());
        }
    }

    /**
     * @cdk.bug 1306780
     */
    @Test
    void testParseK() throws Exception {
        IAtomContainer mol = sp.parseSmiles("C=CCC(=NOS(=O)(=O)[O-])SC1OC(CO)C(O)C(O)C1(O).[Na+]");
        Assertions.assertNotNull(mol);
        Assertions.assertEquals(23, mol.getAtomCount());
        mol = sp.parseSmiles("C=CCC(=NOS(=O)(=O)[O-])SC1OC(CO)C(O)C(O)C1(O).[K]");
        Assertions.assertNotNull(mol);
        assertAtomTypesPerceived(mol);
        Assertions.assertEquals(23, mol.getAtomCount());
        mol = sp.parseSmiles("C=CCC(=NOS(=O)(=O)[O-])SC1OC(CO)C(O)C(O)C1(O).[K+]");
        Assertions.assertNotNull(mol);
        assertAtomTypesPerceived(mol);
        Assertions.assertEquals(23, mol.getAtomCount());
    }

    /**
     * @cdk.bug 1459299
     */
    @Test
    void testBug1459299() throws Exception {
        IAtomContainer mol = sp.parseSmiles("Cc1nn(C)cc1[C@H]2[C@H](C(=O)N)C(=O)C[C@@](C)(O)[C@@H]2C(=O)N");
        Assertions.assertNotNull(mol);
        Assertions.assertEquals(22, mol.getAtomCount());
    }

    /**
     * @cdk.bug 1365547
     */
    @Test
    void testBug1365547() throws Exception {
        IAtomContainer mol = loadExact("c2ccc1[nH]ccc1c2");
        Assertions.assertNotNull(mol);
        Assertions.assertEquals(9, mol.getAtomCount());
        Assertions.assertTrue(mol.getBond(0).getFlag(IChemObject.AROMATIC));
    }

    /**
     * @cdk.bug 1365547
     */
    @Test
    void testBug1365547_2() throws Exception {
        IAtomContainer mol = loadExact("[H]c1c([H])c(c([H])c2c([H])c([H])n([H])c12)Br");
        Assertions.assertNotNull(mol);
        Assertions.assertEquals(16, mol.getAtomCount());
        Assertions.assertEquals(17, mol.getBondCount());
        for (int i = 0; i < 17; i++) {
            IBond bond = mol.getBond(i);
            if (bond.getBegin().getAtomicNumber() == IElement.H || bond.getBegin().getAtomicNumber() == IElement.Br
                || bond.getEnd().getAtomicNumber() == IElement.H || bond.getEnd().getAtomicNumber() == IElement.Br) {
                Assertions.assertFalse(bond.getFlag(IChemObject.AROMATIC));
            } else {
                Assertions.assertTrue(bond.getFlag(IChemObject.AROMATIC));
            }
        }
    }

    /**
     * @cdk.bug 1235852
     */
    @Test
    void testBug1235852() throws Exception {
        //                                   0 1 234 56 7 890 12 3456 78
        IAtomContainer mol = sp.parseSmiles("O=C(CCS)CC(C)CCC2Cc1ccsc1CC2");
        Assertions.assertNotNull(mol);
        Assertions.assertEquals(19, mol.getAtomCount());
        Assertions.assertEquals(20, mol.getBondCount());
        // test only option for delocalized bond system
        Assertions.assertEquals(4.0, mol.getBondOrderSum(mol.getAtom(12)), 0.001);
        Assertions.assertEquals(3.0, mol.getBondOrderSum(mol.getAtom(13)), 0.001);
        Assertions.assertEquals(3.0, mol.getBondOrderSum(mol.getAtom(14)), 0.001);
        Assertions.assertEquals(2.0, mol.getBondOrderSum(mol.getAtom(15)), 0.001);
        Assertions.assertEquals(4.0, mol.getBondOrderSum(mol.getAtom(16)), 0.001);
    }

    /**
     * @cdk.bug 1519183
     */
    @Test
    void testBug1519183() throws Exception {
        //                             0    12345  6
        IAtomContainer mol = sp.parseSmiles("c%101ccccc1.O%10"); // phenol
        Assertions.assertNotNull(mol);
        Assertions.assertEquals(7, mol.getAtomCount());
        Assertions.assertEquals(7, mol.getBondCount());
    }

    /**
     * @cdk.bug 1530926
     */
    @Test
    void testBug1530926() throws Exception {
        //                               0      12345   6
        IAtomContainer mol = loadExact("[n+]%101ccccc1.[O-]%10");
        Assertions.assertNotNull(mol);
        Assertions.assertEquals(7, mol.getAtomCount());
        Assertions.assertEquals(7, mol.getBondCount());
        for (int i = 0; i < 7; i++) {
            IBond bond = mol.getBond(i);
            if (bond.getBegin().getAtomicNumber() == IElement.O || bond.getEnd().getAtomicNumber() == IElement.O) {
                Assertions.assertFalse(bond.getFlag(IChemObject.AROMATIC));
            } else {
                Assertions.assertTrue(bond.getFlag(IChemObject.AROMATIC));
            }
        }
    }

    /**
     * @cdk.bug 1541333
     */
    @Test
    void testBug1541333() throws Exception {
        //                              01  2 345  67  8 9 0 12 3 4  5 67 89  0  1 2
        IAtomContainer mol1 = sp.parseSmiles("OC(=O)CSC1=NC=2C=C(C=CC2N1C=3C=CC=CC3)N(=O)O");
        Assertions.assertNotNull(mol1);
        Assertions.assertEquals(23, mol1.getAtomCount());
        Assertions.assertEquals(25, mol1.getBondCount());
        IAtomContainer mol2 = sp.parseSmiles("OC(=O)CSc1nc2cc(ccc2n1c3ccccc3)N(=O)O");
        Assertions.assertNotNull(mol2);
        Assertions.assertEquals(23, mol2.getAtomCount());
        Assertions.assertEquals(25, mol2.getBondCount());
        // do some checking
        Assertions.assertEquals(IBond.Order.DOUBLE, mol1.getBond(mol1.getAtom(1), mol1.getAtom(2)).getOrder());
        Assertions.assertEquals(IBond.Order.DOUBLE, mol2.getBond(mol2.getAtom(1), mol2.getAtom(2)).getOrder());
        atomtype(mol1);
        atomtype(mol2);
        Aromaticity.cdkLegacy().apply(mol1);
        Aromaticity.cdkLegacy().apply(mol2);
        Assertions.assertTrue(mol1.getBond(7).getFlag(IChemObject.AROMATIC));
        Assertions.assertTrue(mol2.getBond(7).getFlag(IChemObject.AROMATIC));
    }

    /**
     * @cdk.bug 1719287
     */
    @Test
    void testBug1719287() throws Exception {
        //                              01  2  3  4  5 67 8
        IAtomContainer mol = sp
                .parseSmiles("OC(=O)[C@@H](N)CC[S+1](C)C[C@@H](O1)[C@@H](O)[C@@H](O)[C@@H]1n(c3)c(n2)c(n3)c(N)nc2");
        Assertions.assertNotNull(mol);
        Assertions.assertEquals(27, mol.getAtomCount());
        Assertions.assertEquals(29, mol.getBondCount());
        Assertions.assertEquals(1, mol.getAtom(7).getFormalCharge().intValue());
    }

    /**
     * Test for bug #1503541 "Problem with SMILES parsing". All SMILES in the test
     * should result in a benzene molecule. Sometimes only a Cyclohexa-dien was
     * created.
     * @cdk.bug 1503541
     */
    @Test
    void testBug1503541() throws Exception {
        //                              0  1 23 45
        IAtomContainer mol = sp.parseSmiles("C=1C=CC=CC=1"); // benzene #1
        Assertions.assertNotNull(mol);
        Assertions.assertEquals(6, mol.getAtomCount());
        Assertions.assertEquals(6, mol.getBondCount());
        // test only option for delocalized bond system
        Assertions.assertEquals(3.0, mol.getBondOrderSum(mol.getAtom(0)), 0.001);
        Assertions.assertEquals(3.0, mol.getBondOrderSum(mol.getAtom(1)), 0.001);
        Assertions.assertEquals(3.0, mol.getBondOrderSum(mol.getAtom(2)), 0.001);
        Assertions.assertEquals(3.0, mol.getBondOrderSum(mol.getAtom(3)), 0.001);
        Assertions.assertEquals(3.0, mol.getBondOrderSum(mol.getAtom(4)), 0.001);
        Assertions.assertEquals(3.0, mol.getBondOrderSum(mol.getAtom(5)), 0.001);

        //                              0 1 23 45
        mol = sp.parseSmiles("C1C=CC=CC=1"); // benzene #2
        Assertions.assertNotNull(mol);
        Assertions.assertEquals(6, mol.getAtomCount());
        Assertions.assertEquals(6, mol.getBondCount());
        // test only option for delocalized bond system
        Assertions.assertEquals(3.0, mol.getBondOrderSum(mol.getAtom(0)), 0.001);
        Assertions.assertEquals(3.0, mol.getBondOrderSum(mol.getAtom(1)), 0.001);
        Assertions.assertEquals(3.0, mol.getBondOrderSum(mol.getAtom(2)), 0.001);
        Assertions.assertEquals(3.0, mol.getBondOrderSum(mol.getAtom(3)), 0.001);
        Assertions.assertEquals(3.0, mol.getBondOrderSum(mol.getAtom(4)), 0.001);
        Assertions.assertEquals(3.0, mol.getBondOrderSum(mol.getAtom(5)), 0.001);

        //                              0  1 23 45
        mol = sp.parseSmiles("C=1C=CC=CC1"); // benzene #3
        Assertions.assertNotNull(mol);
        Assertions.assertEquals(6, mol.getAtomCount());
        Assertions.assertEquals(6, mol.getBondCount());
        // test only option for delocalized bond system
        Assertions.assertEquals(3.0, mol.getBondOrderSum(mol.getAtom(0)), 0.001);
        Assertions.assertEquals(3.0, mol.getBondOrderSum(mol.getAtom(1)), 0.001);
        Assertions.assertEquals(3.0, mol.getBondOrderSum(mol.getAtom(2)), 0.001);
        Assertions.assertEquals(3.0, mol.getBondOrderSum(mol.getAtom(3)), 0.001);
        Assertions.assertEquals(3.0, mol.getBondOrderSum(mol.getAtom(4)), 0.001);
        Assertions.assertEquals(3.0, mol.getBondOrderSum(mol.getAtom(5)), 0.001);

        //                              0  12 34 5
        mol = sp.parseSmiles("C1=CC=CC=C1"); // benzene #4
        Assertions.assertNotNull(mol);
        Assertions.assertEquals(6, mol.getAtomCount());
        Assertions.assertEquals(6, mol.getBondCount());
        // test only option for delocalized bond system
        Assertions.assertEquals(3.0, mol.getBondOrderSum(mol.getAtom(0)), 0.001);
        Assertions.assertEquals(3.0, mol.getBondOrderSum(mol.getAtom(1)), 0.001);
        Assertions.assertEquals(3.0, mol.getBondOrderSum(mol.getAtom(2)), 0.001);
        Assertions.assertEquals(3.0, mol.getBondOrderSum(mol.getAtom(3)), 0.001);
        Assertions.assertEquals(3.0, mol.getBondOrderSum(mol.getAtom(4)), 0.001);
        Assertions.assertEquals(3.0, mol.getBondOrderSum(mol.getAtom(5)), 0.001);
    }

    /**
     * Test case for bug #1783367 "SmilesParser incorrectly assigns double bonds".
     * "C=%10C=CC=C%02C=%10N(C)CCC%02" was parsed incorrectly whereas "C=1C=CC=C%02C=1N(C)CCC%02"
     * was parsed correctly. There was a bug with parsing "C=%10".
     * Author: Andreas Schueller <a.schueller@chemie.uni-frankfurt.de>
     *
     * @cdk.bug 1783367
     */
    @Test
    void testBug1783367() throws Exception {
        String smiles = "C=%10C=CC=C%02C=%10N(C)CCC%02";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(IBond.Order.SINGLE, mol.getBond(0).getOrder());
    }

    /**
     * @cdk.bug 1783547
     */
    @Test
    void testBug1783547() throws Exception {
        // easy case
        String smiles = "c1ccccc1C1=CC=CC=C1";
        IAtomContainer mol = loadExact(smiles);
        Assertions.assertTrue(mol.getBond(0).getFlag(IChemObject.AROMATIC));
        Assertions.assertTrue(mol.getBond(1).getFlag(IChemObject.AROMATIC));
        Assertions.assertTrue(mol.getBond(2).getFlag(IChemObject.AROMATIC));
        Assertions.assertTrue(mol.getBond(3).getFlag(IChemObject.AROMATIC));

        // harder case
        String smiles2 = "C%21=%01C=CC=C%02C=%01N(C)CCC%02.C%21c%02ccccc%02";
        IAtomContainer mol2 = loadExact(smiles2);
        Assertions.assertTrue(mol2.getBond(16).getFlag(IChemObject.AROMATIC));
        Assertions.assertTrue(mol2.getBond(17).getFlag(IChemObject.AROMATIC));
        Assertions.assertTrue(mol2.getBond(18).getFlag(IChemObject.AROMATIC));
        Assertions.assertTrue(mol2.getBond(19).getFlag(IChemObject.AROMATIC));
    }

    /**
     * Test case for bug #1783546 "Lost aromaticity in SmilesParser with Benzene".
     * SMILES like "C=1C=CC=CC=1" which end in "=1" were incorrectly parsed, the ring
     * closure double bond got lost.
     * @cdk.bug 1783546
     */
    @Test
    void testBug1783546() throws Exception {
        String smiles = "C=1C=CC=CC=1";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(IBond.Order.SINGLE, mol.getBond(mol.getAtom(0), mol.getAtom(1)).getOrder());
        Assertions.assertEquals(IBond.Order.DOUBLE, mol.getBond(mol.getAtom(1), mol.getAtom(2)).getOrder());
        Assertions.assertEquals(IBond.Order.SINGLE, mol.getBond(mol.getAtom(2), mol.getAtom(3)).getOrder());
        Assertions.assertEquals(IBond.Order.DOUBLE, mol.getBond(mol.getAtom(3), mol.getAtom(4)).getOrder());
        Assertions.assertEquals(IBond.Order.SINGLE, mol.getBond(mol.getAtom(4), mol.getAtom(5)).getOrder());
        Assertions.assertEquals(IBond.Order.DOUBLE, mol.getBond(mol.getAtom(5), mol.getAtom(0)).getOrder());
    }

    @Test
    void testChargedAtoms() throws Exception {
        String smiles = "[C-]#[O+]";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertEquals(IBond.Order.TRIPLE, mol.getBond(0).getOrder());
        Assertions.assertEquals(-1, mol.getAtom(0).getFormalCharge().intValue());
        Assertions.assertEquals(1, mol.getAtom(1).getFormalCharge().intValue());
    }

    /**
     * @cdk.bug 1872969
     */
    @Test
    void bug1872969() throws Exception {
        String smiles = "CS(=O)(=O)[O-].[Na+]";
        IAtomContainer mol = sp.parseSmiles(smiles);
        atomtype(mol);
        for (int i = 0; i < 6; i++) {
            Assertions.assertNotNull(mol.getAtom(i).getAtomTypeName());
        }
    }

    /**
     * @cdk.bug 1875949
     */
    @Test
    void testResonanceStructure() throws Exception {
        String smiles = "[F+]=C-[C-]";
        IAtomContainer mol = sp.parseSmiles(smiles);
        Assertions.assertEquals(3, mol.getAtomCount());
        Assertions.assertEquals(IBond.Order.DOUBLE, mol.getBond(0).getOrder());
        Assertions.assertEquals(+1, mol.getAtom(0).getFormalCharge().intValue());
        Assertions.assertEquals(-1, mol.getAtom(2).getFormalCharge().intValue());
    }

    /**
     * @cdk.bug 1879589
     */
    @Test
    void testSP2HybridizedSulphur() throws Exception {
        String smiles = "[s+]1c2c(nc3c1cccc3)cccc2";
        IAtomContainer mol = load(smiles);
        atomtype(mol);
        Aromaticity.cdkLegacy().apply(mol);
        assertAtomTypesPerceived(mol);
        for (IAtom atom : mol.atoms()) {
            Assertions.assertEquals(IAtomType.Hybridization.SP2, atom.getHybridization());
            Assertions.assertTrue(atom.getFlag(IChemObject.AROMATIC));
        }
    }

    @Test
    void testMercaptan() throws Exception {
        IAtomContainer mol = sp.parseSmiles("C=CCS");
        assertAtomTypesPerceived(mol);
    }

    /**
     * @cdk.bug 1957958
     */
    @Test
    void test3amino4methylpyridine() throws Exception {
        IAtomContainer mol = sp.parseSmiles("c1c(C)c(N)cnc1");
        assertAtomTypesPerceived(mol);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        boolean isaromatic = Aromaticity.cdkLegacy().apply(mol);
        Assertions.assertTrue(isaromatic);
    }

    /*
     * Tests for various aromatic hetero cycles follow:
     */

    /**
     * @cdk.bug 1959516
     */
    @Test
    void testPyrrole1() throws Exception {
        String smiles = "[nH]1cccc1";
        IAtomContainer mol = load(smiles);
        atomtype(mol);
        assertAtomTypesPerceived(mol);

        Assertions.assertEquals(5, mol.getAtomCount());

        assertAllSingleOrAromatic(mol);

        assertAtomSymbols(new String[]{"N", "C", "C", "C", "C"}, mol);

        assertHybridizations(new IAtomType.Hybridization[]{IAtomType.Hybridization.PLANAR3,
                IAtomType.Hybridization.SP2, IAtomType.Hybridization.SP2, IAtomType.Hybridization.SP2,
                IAtomType.Hybridization.SP2}, mol);

        assertHydrogenCounts(new int[]{1, 1, 1, 1, 1}, mol);
    }

    /**
     * @cdk.bug 1959516
     */
    @Test
    void testPyrrole2() throws Exception {
        String smiles = "n1([H])cccc1";
        IAtomContainer mol = load(smiles);
        atomtype(mol);
        assertAtomTypesPerceived(mol);

        Assertions.assertEquals(6, mol.getAtomCount());

        assertAllSingleOrAromatic(mol);

        assertAtomSymbols(new String[]{"N", "H", "C", "C", "C", "C"}, mol);

        assertHybridizations(new IAtomType.Hybridization[]{IAtomType.Hybridization.PLANAR3, IAtomType.Hybridization.S,
                IAtomType.Hybridization.SP2, IAtomType.Hybridization.SP2, IAtomType.Hybridization.SP2,
                IAtomType.Hybridization.SP2}, mol);

        assertHydrogenCounts(new int[]{0, 0, 1, 1, 1, 1}, mol);
    }

    /**
     * @cdk.bug 1962419
     */
    @Test
    void testPyrrole3() throws Exception {
        Assertions.assertThrows(InvalidSmilesException.class,
                                () -> {
                                    String smiles = "n1cccc1";
                                    sp.parseSmiles(smiles);
                                });
    }

    /**
     * @cdk.bug 1962398
     */
    @Test
    void testPyrroleAnion1() throws Exception {
        String smiles = "[n-]1cccc1";
        IAtomContainer mol = load(smiles);
        atomtype(mol);
        assertAtomTypesPerceived(mol);

        Assertions.assertEquals(5, mol.getAtomCount());

        assertAllSingleOrAromatic(mol);

        assertAtomSymbols(new String[]{"N", "C", "C", "C", "C"}, mol);

        assertHybridizations(new IAtomType.Hybridization[]{IAtomType.Hybridization.PLANAR3,
                IAtomType.Hybridization.SP2, IAtomType.Hybridization.SP2, IAtomType.Hybridization.SP2,
                IAtomType.Hybridization.SP2}, mol);

        assertHydrogenCounts(new int[]{0, 1, 1, 1, 1}, mol);
    }

    /**
     * @cdk.bug 1960990
     */
    @Test
    void testImidazole1() throws Exception {
        String smiles = "[nH]1cncc1";
        IAtomContainer mol = load(smiles);
        atomtype(mol);
        assertAtomTypesPerceived(mol);

        Assertions.assertEquals(5, mol.getAtomCount());

        assertAllSingleOrAromatic(mol);

        assertAtomSymbols(new String[]{"N", "C", "N", "C", "C"}, mol);

        assertHybridizations(new IAtomType.Hybridization[]{IAtomType.Hybridization.PLANAR3,
                IAtomType.Hybridization.SP2, IAtomType.Hybridization.SP2, IAtomType.Hybridization.SP2,
                IAtomType.Hybridization.SP2}, mol);

        assertHydrogenCounts(new int[]{1, 1, 0, 1, 1}, mol);
    }

    /**
     * @cdk.bug 1960990
     */
    @Test
    void testImidazole2() throws Exception {
        String smiles = "n1([H])cncc1";
        IAtomContainer mol = load(smiles);
        atomtype(mol);
        assertAtomTypesPerceived(mol);

        Assertions.assertEquals(6, mol.getAtomCount());

        assertAllSingleOrAromatic(mol);

        assertAtomSymbols(new String[]{"N", "H", "C", "N", "C", "C"}, mol);

        assertHybridizations(new IAtomType.Hybridization[]{IAtomType.Hybridization.PLANAR3, IAtomType.Hybridization.S,
                IAtomType.Hybridization.SP2, IAtomType.Hybridization.SP2, IAtomType.Hybridization.SP2,
                IAtomType.Hybridization.SP2}, mol);

        assertHydrogenCounts(new int[]{0, 0, 1, 0, 1, 1}, mol);
    }

    /**
     * @cdk.bug 1962419
     */
    @Test
    void testImidazole3() throws Exception {
        String smiles = "n1cncc1";
        Assertions.assertThrows(InvalidSmilesException.class,
                                () -> {
                                    sp.parseSmiles(smiles);
                                });
    }

    /**
     * @cdk.bug 1960990
     */
    @Test
    void testImidazole4() throws Exception {
        String smiles = "n1cc[nH]c1";
        IAtomContainer mol = load(smiles);
        atomtype(mol);
        assertAtomTypesPerceived(mol);

        Assertions.assertEquals(5, mol.getAtomCount());

        assertAllSingleOrAromatic(mol);

        assertAtomSymbols(new String[]{"N", "C", "C", "N", "C"}, mol);

        assertHybridizations(new IAtomType.Hybridization[]{IAtomType.Hybridization.SP2, IAtomType.Hybridization.SP2,
                IAtomType.Hybridization.SP2, IAtomType.Hybridization.PLANAR3, IAtomType.Hybridization.SP2}, mol);

        assertHydrogenCounts(new int[]{0, 1, 1, 1, 1}, mol);
    }

    /**
     * @cdk.bug 1959516
     */
    @Test
    void testPyridine1() throws Exception {
        String smiles = "n1ccccc1";
        IAtomContainer mol = load(smiles);
        atomtype(mol);
        assertAtomTypesPerceived(mol);

        Assertions.assertEquals(6, mol.getAtomCount());

        assertAtomSymbols(new String[]{"N", "C", "C", "C", "C", "C"}, mol);

        assertHybridizations(new IAtomType.Hybridization[]{IAtomType.Hybridization.SP2, IAtomType.Hybridization.SP2,
                IAtomType.Hybridization.SP2, IAtomType.Hybridization.SP2, IAtomType.Hybridization.SP2,
                IAtomType.Hybridization.SP2}, mol);

        assertHydrogenCounts(new int[]{0, 1, 1, 1, 1, 1}, mol);
    }

    /**
     * @cdk.bug 1959516
     */
    @Test
    void testPyrimidine1() throws Exception {
        String smiles = "n1cnccc1";
        IAtomContainer mol = load(smiles);
        atomtype(mol);
        assertAtomTypesPerceived(mol);

        Assertions.assertEquals(6, mol.getAtomCount());

        assertAllSingleOrAromatic(mol);

        assertAtomSymbols(new String[]{"N", "C", "N", "C", "C", "C"}, mol);

        assertHybridizations(new IAtomType.Hybridization[]{IAtomType.Hybridization.SP2, IAtomType.Hybridization.SP2,
                IAtomType.Hybridization.SP2, IAtomType.Hybridization.SP2, IAtomType.Hybridization.SP2,
                IAtomType.Hybridization.SP2}, mol);

        assertHydrogenCounts(new int[]{0, 1, 0, 1, 1, 1}, mol);
    }

    /**
     * @throws Exception
     * @cdk.bug 1967468
     */
    @Test
    void testIndole1() throws Exception {
        String smiles1 = "c1ccc2cc[nH]c2(c1)";
        IAtomContainer mol = loadExact(smiles1);
        assertAtomTypesPerceived(mol);
        Assertions.assertEquals(9, mol.getAtomCount());

        for (IAtom atom : mol.atoms()) {
            Assertions.assertTrue(atom.getFlag(IChemObject.AROMATIC));
        }
    }

    /**
     * @throws Exception
     * @cdk.bug 1967468
     */
    @Test
    void testIndole2() throws Exception {
        String smiles1 = "C1(NC=C2)=C2C=CC=C1";
        IAtomContainer mol = loadExact(smiles1);
        atomtype(mol);
        Aromaticity.cdkLegacy().apply(mol);
        assertAtomTypesPerceived(mol);
        Assertions.assertEquals(9, mol.getAtomCount());
        for (IAtom atom : mol.atoms()) {
            Assertions.assertTrue(atom.getFlag(IChemObject.AROMATIC));
        }
    }

    /**
     * @throws Exception
     * @cdk.bug 1963731
     */
    @Test
    void testBug1963731() throws Exception {

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer molecule = sp.parseSmiles("C(C1C(C(C(C(O1)O)N)O)O)O");
        int hcount = 0;
        for (int i = 0; i < molecule.getBondCount(); i++) {
            hcount += molecule.getAtom(i).getImplicitHydrogenCount();
        }
        Assertions.assertEquals(13, hcount);
    }

    @Test
    void testONSSolubility1() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer molecule = sp.parseSmiles("Oc1ccc(cc1OC)C=O");
        Assertions.assertEquals(11, molecule.getAtomCount());
        Assertions.assertEquals(11, molecule.getBondCount());
    }

    @Test
    void test1456139() throws Exception {
        SmilesParser p = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = p.parseSmiles("Cc1nn(C)cc1[C@H]2[C@H](C(=O)N)C(=O)C[C@@](C)(O)[C@@H]2C(=O)N");
        IAtomContainer mol2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class, mol);
        Assertions.assertNotNull(mol2);
        Assertions.assertEquals(22, mol2.getAtomCount());
    }

    @Test
    void testExplicitH() throws Exception {
        SmilesParser p = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol;

        mol = p.parseSmiles("CO[H]");
        Assertions.assertEquals(3, mol.getAtomCount());

        mol = p.parseSmiles("[CH3][OH]");
        Assertions.assertEquals(2, mol.getAtomCount());

        mol = p.parseSmiles("C([H])([H])([H])O([H])");
        Assertions.assertEquals(6, mol.getAtomCount());
    }

    /**
     * @cdk.bug 2514200
     */
    @Test
    void testno937() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C[nH0]1c[nH0]cc1"); // xlogp training set molecule no937
        Assertions.assertNotNull(mol.getAtom(1).getImplicitHydrogenCount());
        Assertions.assertEquals(0, mol.getAtom(1).getImplicitHydrogenCount().intValue());
        Assertions.assertNotNull(mol.getAtom(3).getImplicitHydrogenCount());
        Assertions.assertEquals(0, mol.getAtom(3).getImplicitHydrogenCount().intValue());
    }

    /**
     * @cdk.bug 2514200
     * @throws InvalidSmilesException
     */
    @Test
    void testHardcodedH() throws InvalidSmilesException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C[CH1]NC");
        Assertions.assertNotNull(mol.getAtom(1).getImplicitHydrogenCount());
        Assertions.assertEquals(1, mol.getAtom(1).getImplicitHydrogenCount().intValue());

        mol = sp.parseSmiles("C[CH]NC");
        Assertions.assertNotNull(mol.getAtom(1).getImplicitHydrogenCount());
        Assertions.assertEquals(1, mol.getAtom(1).getImplicitHydrogenCount().intValue());

        mol = sp.parseSmiles("C[CH0]NC");
        Assertions.assertNotNull(mol.getAtom(1).getImplicitHydrogenCount());
        Assertions.assertEquals(0, mol.getAtom(1).getImplicitHydrogenCount().intValue());
    }

    /**
     * @cdk.bug 2714283
     * @throws InvalidSmilesException
     */
    @Test
    void testBadRingClosure1() throws InvalidSmilesException {
        Assertions.assertThrows(InvalidSmilesException.class,
                                () -> {
                                    SmilesParser p = new SmilesParser(DefaultChemObjectBuilder.getInstance());
                                    p.parseSmiles("c1ccccc1Cc1ccccc");
                                });
    }

    /**
     * @cdk.bug 2714283
     * @throws InvalidSmilesException
     */
    @Test
    void testBadRingClosure2() throws InvalidSmilesException {
        Assertions.assertThrows(InvalidSmilesException.class,
                                () -> {
                                    SmilesParser p = new SmilesParser(DefaultChemObjectBuilder.getInstance());
                                    p.parseSmiles("NC1=CC=C(N)C=C");
                                });
    }

    /**
     * @cdk.inchi InChI=1/C4H5N/c1-2-4-5-3-1/h1-5H
     *
     * @see #testPyrrole()
     */
    @Test
    void testPyrrole_2() throws Exception {
        SmilesParser p = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = p.parseSmiles("c1c[nH]cc1");

        assertThat(mol.getBond(mol.getAtom(0), mol.getAtom(1)).getOrder(), is(IBond.Order.DOUBLE));
        assertThat(mol.getBond(mol.getAtom(1), mol.getAtom(2)).getOrder(), is(IBond.Order.SINGLE));
        assertThat(mol.getBond(mol.getAtom(2), mol.getAtom(3)).getOrder(), is(IBond.Order.SINGLE));
        assertThat(mol.getBond(mol.getAtom(3), mol.getAtom(4)).getOrder(), is(IBond.Order.DOUBLE));
        assertThat(mol.getBond(mol.getAtom(4), mol.getAtom(0)).getOrder(), is(IBond.Order.SINGLE));

        atomtype(mol);
        Aromaticity.cdkLegacy().apply(mol);

        for (IAtom atom : mol.atoms()) {
            Assertions.assertTrue(atom.getFlag(IChemObject.AROMATIC));
        }
    }

    /**
     * @cdk.bug 3048501
     */
    @Test
    void testAromaticSeParsing() throws InvalidSmilesException {
        SmilesParser p = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        // The CDK aromaticity model does not recognise 'se' but we can still
        // parse it from the SMILES
        p.kekulise(false);
        IAtomContainer mol = p.parseSmiles("c1cc2cccnc2[se]1");
        for (IAtom atom : mol.atoms()) {
            Assertions.assertTrue(atom.getFlag(IChemObject.AROMATIC));
        }
    }

    /**
     * @cdk.bug 3048501
     */
    @Test
    void testCeParsing() throws InvalidSmilesException {
        SmilesParser p = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = p.parseSmiles("Cl[Ce](Cl)Cl");
        Assertions.assertEquals("Ce", mol.getAtom(1).getSymbol());
    }

    /**
     * @cdk.bug 3048501
     */
    @Test
    void testErParsing() throws InvalidSmilesException {
        SmilesParser p = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = p.parseSmiles("Cl[Er](Cl)Cl");
        Assertions.assertEquals("Er", mol.getAtom(1).getSymbol());
    }

    /**
     * @cdk.bug 3048501
     */
    @Test
    void testGdParsing() throws InvalidSmilesException {
        SmilesParser p = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = p.parseSmiles("Cl[Gd](Cl)Cl");
        Assertions.assertEquals("Gd", mol.getAtom(1).getSymbol());
    }

    /**
     * @cdk.bug 3048501
     */
    @Test
    void testSmParsing() throws InvalidSmilesException {
        SmilesParser p = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = p.parseSmiles("Cl[Sm](Cl)Cl");
        Assertions.assertEquals("Sm", mol.getAtom(1).getSymbol());
    }

    /**
     * @cdk.bug 3048501
     */
    @Test
    void testLaParsing() throws InvalidSmilesException {
        SmilesParser p = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = p.parseSmiles("[Cl-].[Cl-].[Cl-].[La+3]");
        Assertions.assertEquals("La", mol.getAtom(3).getSymbol());
    }

    /**
     * @cdk.bug 3048501
     */
    @Test
    void testAcParsing() throws InvalidSmilesException {
        SmilesParser p = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = p.parseSmiles("[255Ac]");
        Assertions.assertEquals("Ac", mol.getAtom(0).getSymbol());
    }

    /**
     * @cdk.bug 3048501
     */
    @Test
    void testPuParsing() throws InvalidSmilesException {
        SmilesParser p = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = p.parseSmiles("[Pu]");
        Assertions.assertEquals("Pu", mol.getAtom(0).getSymbol());
    }

    /**
     * @cdk.bug 3048501
     */
    @Test
    void testPrParsing() throws InvalidSmilesException {
        SmilesParser p = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = p.parseSmiles("[Pr]");
        Assertions.assertEquals("Pr", mol.getAtom(0).getSymbol());
    }

    /**
     * @cdk.bug 3048501
     */
    @Test
    void testPaParsing() throws InvalidSmilesException {
        SmilesParser p = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = p.parseSmiles("[Pa]");
        Assertions.assertEquals("Pa", mol.getAtom(0).getSymbol());
    }

    /**
     * @cdk.bug 3048501
     */
    @Test
    void testTbParsing() throws InvalidSmilesException {
        SmilesParser p = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = p.parseSmiles("[Tb]");
        Assertions.assertEquals("Tb", mol.getAtom(0).getSymbol());
    }

    /**
     * @cdk.bug 3048501
     */
    @Test
    void testAmParsing() throws InvalidSmilesException {
        SmilesParser p = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = p.parseSmiles("[Am]");
        Assertions.assertEquals("Am", mol.getAtom(0).getSymbol());
    }

    /**
     * @cdk.bug 3048501
     */
    @Test
    void testPmParsing() throws InvalidSmilesException {
        SmilesParser p = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = p.parseSmiles("[Pm]");
        Assertions.assertEquals("Pm", mol.getAtom(0).getSymbol());
    }

    /**
     * @cdk.bug 3048501
     */
    @Test
    void testHoParsing() throws InvalidSmilesException {
        SmilesParser p = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = p.parseSmiles("[Ho]");
        Assertions.assertEquals("Ho", mol.getAtom(0).getSymbol());
    }

    /**
     * @cdk.bug 3048501
     */
    @Test
    void testCfParsing() throws InvalidSmilesException {
        SmilesParser p = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = p.parseSmiles("[Cf]");
        Assertions.assertEquals("Cf", mol.getAtom(0).getSymbol());
    }

    /**
     * @cdk.bug 2976054
     * @throws InvalidSmilesException
     */
    @Test
    void testAromaticity() throws InvalidSmilesException {
        IAtomContainer mol = loadExact("c1cnc2s[cH][cH]n12");
        for (IAtom atom : mol.atoms()) {
            Assertions.assertTrue(atom.getFlag(IChemObject.AROMATIC));
        }
    }

    /**
     * Tests reading stereochemistry from a SMILES with one of the four groups being an implicit hydrogen.
     */
    @Test
    void testAtAt() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("Br[C@@H](Cl)I");
        Iterator<IStereoElement> stereoElements = mol.stereoElements().iterator();
        Assertions.assertTrue(stereoElements.hasNext());
        IStereoElement stereoElement = stereoElements.next();
        Assertions.assertNotNull(stereoElement);
        Assertions.assertTrue(stereoElement instanceof ITetrahedralChirality);
        ITetrahedralChirality l4Chiral = (ITetrahedralChirality) stereoElement;
        Assertions.assertEquals("C", l4Chiral.getChiralAtom().getSymbol());
        IAtom[] ligands = l4Chiral.getLigands();
        for (IAtom atom : ligands)
            Assertions.assertNotNull(atom);
        // note: the tetrahedral centre holds atom '1' to refer to implicit
        // hydrogen
        Assertions.assertEquals(mol.getAtom(0), ligands[0]);
        Assertions.assertEquals(mol.getAtom(1), ligands[1]);
        Assertions.assertEquals(mol.getAtom(2), ligands[2]);
        Assertions.assertEquals(mol.getAtom(3), ligands[3]);
        Assertions.assertEquals(Stereo.CLOCKWISE, l4Chiral.getStereo());
    }

    /**
     * Tests reading stereochemistry from a SMILES with one of the four groups being an implicit hydrogen.
     * Per SMILES specification, this hydrogen is the atom towards the viewer, and will therefore end up
     * as first atom in the array.
     */
    @Test
    void testAt() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("Br[C@H](Cl)I");
        Iterator<IStereoElement> stereoElements = mol.stereoElements().iterator();
        Assertions.assertTrue(stereoElements.hasNext());
        IStereoElement stereoElement = stereoElements.next();
        Assertions.assertNotNull(stereoElement);
        Assertions.assertTrue(stereoElement instanceof ITetrahedralChirality);
        ITetrahedralChirality l4Chiral = (ITetrahedralChirality) stereoElement;
        Assertions.assertEquals("C", l4Chiral.getChiralAtom().getSymbol());
        IAtom[] ligands = l4Chiral.getLigands();
        for (IAtom atom : ligands)
            Assertions.assertNotNull(atom);
        // note: the tetrahedral centre holds atom '1' to refer to implicit
        // hydrogen
        Assertions.assertEquals(mol.getAtom(0), ligands[0]);
        Assertions.assertEquals(mol.getAtom(1), ligands[1]);
        Assertions.assertEquals(mol.getAtom(2), ligands[2]);
        Assertions.assertEquals(mol.getAtom(3), ligands[3]);
        Assertions.assertEquals(Stereo.ANTI_CLOCKWISE, l4Chiral.getStereo());
    }

    @Test
    void testAtAt_ExplicitHydrogen() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("Br[C@@]([H])(Cl)I");
        Iterator<IStereoElement> stereoElements = mol.stereoElements().iterator();
        Assertions.assertTrue(stereoElements.hasNext());
        IStereoElement stereoElement = stereoElements.next();
        Assertions.assertNotNull(stereoElement);
        Assertions.assertTrue(stereoElement instanceof ITetrahedralChirality);
        ITetrahedralChirality l4Chiral = (ITetrahedralChirality) stereoElement;
        Assertions.assertEquals("C", l4Chiral.getChiralAtom().getSymbol());
        IAtom[] ligands = l4Chiral.getLigands();
        for (IAtom atom : ligands)
            Assertions.assertNotNull(atom);
        Assertions.assertEquals("Br", ligands[0].getSymbol());
        Assertions.assertEquals("H", ligands[1].getSymbol());
        Assertions.assertEquals("Cl", ligands[2].getSymbol());
        Assertions.assertEquals("I", ligands[3].getSymbol());
        Assertions.assertEquals(Stereo.CLOCKWISE, l4Chiral.getStereo());
    }

    @Test
    void testAt_ExplicitHydrogen() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("Br[C@]([H])(Cl)I");
        Iterator<IStereoElement> stereoElements = mol.stereoElements().iterator();
        Assertions.assertTrue(stereoElements.hasNext());
        IStereoElement stereoElement = stereoElements.next();
        Assertions.assertNotNull(stereoElement);
        Assertions.assertTrue(stereoElement instanceof ITetrahedralChirality);
        ITetrahedralChirality l4Chiral = (ITetrahedralChirality) stereoElement;
        Assertions.assertEquals("C", l4Chiral.getChiralAtom().getSymbol());
        IAtom[] ligands = l4Chiral.getLigands();
        for (IAtom atom : ligands)
            Assertions.assertNotNull(atom);
        Assertions.assertEquals("Br", ligands[0].getSymbol());
        Assertions.assertEquals("H", ligands[1].getSymbol());
        Assertions.assertEquals("Cl", ligands[2].getSymbol());
        Assertions.assertEquals("I", ligands[3].getSymbol());
        Assertions.assertEquals(Stereo.ANTI_CLOCKWISE, l4Chiral.getStereo());
    }

    @Test
    void testRingClosure() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C12(OC1)CCC2");
        Assertions.assertEquals(6, mol.getAtomCount());
        Assertions.assertEquals("C", mol.getAtom(0).getSymbol());
        Assertions.assertEquals("O", mol.getAtom(1).getSymbol());
        Assertions.assertEquals("C", mol.getAtom(2).getSymbol());
        Assertions.assertEquals(4, mol.getConnectedBondsCount(mol.getAtom(0)));
        Assertions.assertEquals(2, mol.getConnectedBondsCount(mol.getAtom(1)));
        Assertions.assertEquals(2, mol.getConnectedBondsCount(mol.getAtom(2)));
    }

    @Test
    void testRingClosure_At() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("[C@]12(OC1)NCN2");
        Iterator<IStereoElement> stereoElements = mol.stereoElements().iterator();
        Assertions.assertTrue(stereoElements.hasNext());
        IStereoElement stereoElement = stereoElements.next();
        Assertions.assertNotNull(stereoElement);
        Assertions.assertTrue(stereoElement instanceof ITetrahedralChirality);
        ITetrahedralChirality l4Chiral = (ITetrahedralChirality) stereoElement;
        Assertions.assertEquals("C", l4Chiral.getChiralAtom().getSymbol());
        IAtom[] ligands = l4Chiral.getLigands();
        for (IAtom atom : ligands)
            Assertions.assertNotNull(atom);
        // note: ligands are given in the order they appear in (hence in this
        // case the winding (getStereo) has flipped
        Assertions.assertEquals(mol.getAtom(1), ligands[0]);
        Assertions.assertEquals(mol.getAtom(2), ligands[1]);
        Assertions.assertEquals(mol.getAtom(3), ligands[2]);
        Assertions.assertEquals(mol.getAtom(5), ligands[3]);
        Assertions.assertEquals(Stereo.CLOCKWISE, l4Chiral.getStereo());
    }

    @Test
    void testNeighboringChirality() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        final IAtomContainer mol = sp.parseSmiles("C[C@H](O)[C@H](O)C");
        List<IStereoElement<?,?>> stereoElements = new ArrayList<>();
        mol.stereoElements().forEach(stereoElements::add);

        stereoElements.sort(new Comparator<IStereoElement>() {

            @Override
            public int compare(IStereoElement o1, IStereoElement o2) {
                return Integer.compare(mol.indexOf(((ITetrahedralChirality) o1).getChiralAtom()),
                        mol.indexOf(((ITetrahedralChirality) o2).getChiralAtom()));
            }
        });

        // first chiral center
        assertThat(stereoElements.size(), is(2));
        IStereoElement stereoElement = stereoElements.get(0);
        Assertions.assertNotNull(stereoElement);
        Assertions.assertTrue(stereoElement instanceof ITetrahedralChirality);
        ITetrahedralChirality l4Chiral = (ITetrahedralChirality) stereoElement;
        Assertions.assertEquals("C", l4Chiral.getChiralAtom().getSymbol());
        IAtom[] ligands = l4Chiral.getLigands();
        for (IAtom atom : ligands)
            Assertions.assertNotNull(atom);
        Assertions.assertEquals(mol.getAtom(0), ligands[0]);
        Assertions.assertEquals(mol.getAtom(1), ligands[1]);
        Assertions.assertEquals(mol.getAtom(2), ligands[2]);
        Assertions.assertEquals(mol.getAtom(3), ligands[3]);
        Assertions.assertEquals(Stereo.ANTI_CLOCKWISE, l4Chiral.getStereo());
        // second chiral center
        stereoElement = stereoElements.get(1);
        Assertions.assertNotNull(stereoElement);
        Assertions.assertTrue(stereoElement instanceof ITetrahedralChirality);
        l4Chiral = (ITetrahedralChirality) stereoElement;
        Assertions.assertEquals("C", l4Chiral.getChiralAtom().getSymbol());
        ligands = l4Chiral.getLigands();
        for (IAtom atom : ligands)
            Assertions.assertNotNull(atom);
        Assertions.assertEquals(mol.getAtom(1), ligands[0]);
        Assertions.assertEquals(mol.getAtom(3), ligands[1]);
        Assertions.assertEquals(mol.getAtom(4), ligands[2]);
        Assertions.assertEquals(mol.getAtom(5), ligands[3]);
        Assertions.assertEquals(Stereo.ANTI_CLOCKWISE, l4Chiral.getStereo());
    }

    @Test
    void testChiralityInBranch() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("NC([C@H](O)C)Cl");
        Iterator<IStereoElement> stereoElements = mol.stereoElements().iterator();
        // first chiral center
        Assertions.assertTrue(stereoElements.hasNext());
        IStereoElement stereoElement = stereoElements.next();
        Assertions.assertNotNull(stereoElement);
        Assertions.assertTrue(stereoElement instanceof ITetrahedralChirality);
        ITetrahedralChirality l4Chiral = (ITetrahedralChirality) stereoElement;
        Assertions.assertEquals("C", l4Chiral.getChiralAtom().getSymbol());
        IAtom[] ligands = l4Chiral.getLigands();
        for (IAtom atom : ligands)
            Assertions.assertNotNull(atom);
        Assertions.assertEquals(mol.getAtom(1), ligands[0]);
        Assertions.assertEquals(mol.getAtom(2), ligands[1]); // refers to implicit hydrogen
        Assertions.assertEquals(mol.getAtom(3), ligands[2]);
        Assertions.assertEquals(mol.getAtom(4), ligands[3]);
        Assertions.assertEquals(Stereo.ANTI_CLOCKWISE, l4Chiral.getStereo());
    }

    @Test
    void testChiralityWithTonsOfDots() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("I1.Cl2.Br3.[C@]123CCC");
        Iterator<IStereoElement> stereoElements = mol.stereoElements().iterator();
        Assertions.assertTrue(stereoElements.hasNext());
        IStereoElement stereoElement = stereoElements.next();
        Assertions.assertNotNull(stereoElement);
        Assertions.assertTrue(stereoElement instanceof ITetrahedralChirality);
        ITetrahedralChirality l4Chiral = (ITetrahedralChirality) stereoElement;
        Assertions.assertEquals("C", l4Chiral.getChiralAtom().getSymbol());
        IAtom[] ligands = l4Chiral.getLigands();
        for (IAtom atom : ligands)
            Assertions.assertNotNull(atom);
        Assertions.assertEquals("I", ligands[0].getSymbol());
        Assertions.assertEquals("Cl", ligands[1].getSymbol());
        Assertions.assertEquals("Br", ligands[2].getSymbol());
        Assertions.assertEquals("C", ligands[3].getSymbol());
        Assertions.assertEquals(Stereo.ANTI_CLOCKWISE, l4Chiral.getStereo());
    }

    @Test
    void testChiralAtomWithDisconnectedLastAtom() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("Br1.[C@]1(Cl)(OC)CCC");
        Iterator<IStereoElement> stereoElements = mol.stereoElements().iterator();
        Assertions.assertTrue(stereoElements.hasNext());
        IStereoElement stereoElement = stereoElements.next();
        Assertions.assertNotNull(stereoElement);
        Assertions.assertTrue(stereoElement instanceof ITetrahedralChirality);
        ITetrahedralChirality l4Chiral = (ITetrahedralChirality) stereoElement;
        Assertions.assertEquals("C", l4Chiral.getChiralAtom().getSymbol());
        IAtom[] ligands = l4Chiral.getLigands();
        for (IAtom atom : ligands)
            Assertions.assertNotNull(atom);
        Assertions.assertEquals("Br", ligands[0].getSymbol());
        Assertions.assertEquals("Cl", ligands[1].getSymbol());
        Assertions.assertEquals("O", ligands[2].getSymbol());
        Assertions.assertEquals("C", ligands[3].getSymbol());
        Assertions.assertEquals(Stereo.ANTI_CLOCKWISE, l4Chiral.getStereo());
    }

    @Test
    void testFromBlog1() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("[C@@H]231.C2.N1.F3");
        Iterator<IStereoElement> stereoElements = mol.stereoElements().iterator();
        Assertions.assertTrue(stereoElements.hasNext());
        IStereoElement stereoElement = stereoElements.next();
        Assertions.assertNotNull(stereoElement);
        Assertions.assertTrue(stereoElement instanceof ITetrahedralChirality);
        ITetrahedralChirality l4Chiral = (ITetrahedralChirality) stereoElement;
        Assertions.assertEquals("C", l4Chiral.getChiralAtom().getSymbol());
        IAtom[] ligands = l4Chiral.getLigands();
        for (IAtom atom : ligands)
            Assertions.assertNotNull(atom);
        // note: ligands are given in the order they appear in (hence in this
        // case the winding (getStereo) has flipped (0,1,3,2) -> (0,1,2,3)
        Assertions.assertEquals(mol.getAtom(0), ligands[0]);
        Assertions.assertEquals(mol.getAtom(1), ligands[1]);
        Assertions.assertEquals(mol.getAtom(2), ligands[2]);
        Assertions.assertEquals(mol.getAtom(3), ligands[3]);
        Assertions.assertEquals(Stereo.ANTI_CLOCKWISE, l4Chiral.getStereo());
    }

    @Test
    void testFromBlog2() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("[C@@H](Cl)1[C@H](C)(F).Br1");
        Iterator<IStereoElement> stereoElements = mol.stereoElements().iterator();
        for (int i = 0; i < 2; i++) {
            Assertions.assertTrue(stereoElements.hasNext());
            IStereoElement stereoElement = stereoElements.next();
            Assertions.assertNotNull(stereoElement);
            Assertions.assertTrue(stereoElement instanceof ITetrahedralChirality);
            ITetrahedralChirality l4Chiral = (ITetrahedralChirality) stereoElement;
            Assertions.assertEquals("C", l4Chiral.getChiralAtom().getSymbol());
            if (l4Chiral.getChiralAtom().equals(mol.getAtom(0))) {
                IAtom[] ligands = l4Chiral.getLigands();
                for (IAtom atom : ligands)
                    Assertions.assertNotNull(atom);
                // note: ligands are given in the order they appear, there is
                // one inversion (0,1,5,2) -> (0,1,2,5) so winding flips
                Assertions.assertEquals(mol.getAtom(0), ligands[0]);
                Assertions.assertEquals(mol.getAtom(1), ligands[1]);
                Assertions.assertEquals(mol.getAtom(2), ligands[2]);
                Assertions.assertEquals(mol.getAtom(5), ligands[3]);
                Assertions.assertEquals(Stereo.ANTI_CLOCKWISE, l4Chiral.getStereo());
            } else {
                IAtom[] ligands = l4Chiral.getLigands();
                for (IAtom atom : ligands)
                    Assertions.assertNotNull(atom);
                Assertions.assertEquals(mol.getAtom(0), ligands[0]);
                Assertions.assertEquals(mol.getAtom(2), ligands[1]);
                Assertions.assertEquals(mol.getAtom(3), ligands[2]);
                Assertions.assertEquals(mol.getAtom(4), ligands[3]);
                Assertions.assertEquals(Stereo.ANTI_CLOCKWISE, l4Chiral.getStereo());
            }
        }
    }

    @Test
    void testPreserveAromaticity() throws InvalidSmilesException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        sp.kekulise(false);
        IAtomContainer molecule = sp.parseSmiles("Oc1ccc(Cl)c2C(=O)c3c(sc4nccn34)C(=O)c12");
        Assertions.assertEquals(14, countAromaticAtoms(molecule));
        Assertions.assertEquals(15, countAromaticBonds(molecule));

        molecule = sp.parseSmiles("COc1ccc2[nH]c3c(cnn4c(C)nnc34)c2c1");
        Assertions.assertEquals(16, countAromaticAtoms(molecule));
        Assertions.assertEquals(19, countAromaticBonds(molecule));

        molecule = sp.parseSmiles("C:1:C:C:C:C:C1"); // n.b see cyclohexaneWithAromaticBonds
        Assertions.assertEquals(6, countAromaticAtoms(molecule));
        Assertions.assertEquals(6, countAromaticBonds(molecule));

        molecule = sp.parseSmiles("c1cc[se]cc1");
        Assertions.assertEquals(6, countAromaticAtoms(molecule));
        Assertions.assertEquals(6, countAromaticBonds(molecule));

    }

    /**
     *  'C:1:C:C:C:C:C1' is actually cyclo-hexane not benzene. Beam will kekulise
     *  this correctly and leave single bonds the aromaticity flags are preserved.
     */
    @Test
    void cyclohexaneWithAromaticBonds() throws Exception {
        IAtomContainer molecule = sp.parseSmiles("C:1:C:C:C:C:C1");
        Assertions.assertEquals(6, countAromaticAtoms(molecule));
        Assertions.assertEquals(6, countAromaticBonds(molecule));
        for (IBond bond : molecule.bonds()) {
            assertThat(bond.getOrder(), is(IBond.Order.SINGLE));
            Assertions.assertTrue(bond.getFlag(IChemObject.AROMATIC));
        }
    }

    @Test
    void testPreserveAromaticityAndPerceiveAtomTypes() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        sp.kekulise(false);
        IAtomContainer molecule = sp.parseSmiles("c1ccccc1");
        atomtype(molecule);
        Assertions.assertNotNull(molecule.getAtom(0).getAtomTypeName());
    }

    /**
     * @cdk.bug 3160514
     */
    @Test
    void testAromaticBoron() throws Exception {
        IAtomContainer mol = loadExact("c1cc2c3cc1.c1cb23cc1");
        Assertions.assertNotNull(mol);
        assertAllSingleOrAromatic(mol);
    }

    /**
     * This molecule is actually invalid and there is no way to kekulise it.
     */
    @Test
    void testAromaticBoron_invalid() throws CDKException {
        Assertions.assertThrows(InvalidSmilesException.class,
                                () -> {
                                    load("c1cc2c3cc1.c1cb23cc1");
                                });
    }

    /**
     * A 'proper' aromatic boron example.
     */
    @Test
    void borinine() throws Exception {
        IAtomContainer mol = load("b1ccccc1");
        assertThat(mol.getBond(mol.getAtom(0), mol.getAtom(1)).getOrder(), is(IBond.Order.DOUBLE));
        assertThat(mol.getBond(mol.getAtom(1), mol.getAtom(2)).getOrder(), is(IBond.Order.SINGLE));
        assertThat(mol.getBond(mol.getAtom(2), mol.getAtom(3)).getOrder(), is(IBond.Order.DOUBLE));
        assertThat(mol.getBond(mol.getAtom(3), mol.getAtom(4)).getOrder(), is(IBond.Order.SINGLE));
        assertThat(mol.getBond(mol.getAtom(4), mol.getAtom(5)).getOrder(), is(IBond.Order.DOUBLE));
        assertThat(mol.getBond(mol.getAtom(5), mol.getAtom(0)).getOrder(), is(IBond.Order.SINGLE));
    }

    /** @cdk.bug 1234 */
    @Test
    void testBug1234() throws Exception {
        Assertions.assertThrows(CDKException.class,
                                () -> {
                                    sp.parseSmiles("C1C1");
                                });
    }

    @Test
    void testFormalNeighborBount() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("Oc1ccc(O)cc1");
        atomtype(mol);
        Assertions.assertEquals("O.sp3", mol.getAtom(0).getAtomTypeName());
        Assertions.assertEquals(2, mol.getAtom(0).getFormalNeighbourCount().intValue());
        Assertions.assertEquals("C.sp2", mol.getAtom(1).getAtomTypeName());
        Assertions.assertEquals(3, mol.getAtom(1).getFormalNeighbourCount().intValue());
        IAtomContainer clone = mol.clone();
        Assertions.assertEquals("O.sp3", clone.getAtom(0).getAtomTypeName());
        Assertions.assertEquals(2, clone.getAtom(0).getFormalNeighbourCount().intValue());
        Assertions.assertEquals("C.sp2", clone.getAtom(1).getAtomTypeName());
        Assertions.assertEquals(3, clone.getAtom(1).getFormalNeighbourCount().intValue());
    }

    /** @cdk.bug 549 */
    @Test
    void testDiBorane() throws Exception {
        String smiles = "[H]B1([H])HB([H]1)([H])[H]";

        IAtomContainer mol = loadExact(smiles);
        Assertions.assertEquals(8, mol.getAtomCount());
        Assertions.assertEquals(4, mol.getConnectedBondsCount(mol.getAtom(1)));
        Assertions.assertEquals(2, mol.getConnectedBondsCount(mol.getAtom(3)));
        Assertions.assertEquals(4, mol.getConnectedBondsCount(mol.getAtom(4)));
        Assertions.assertEquals(2, mol.getConnectedBondsCount(mol.getAtom(5)));
    }

    /**
     * Okay exception for a non-SMILES string.
     * @cdk.bug 1375
     */
    @Test
    void idNumber() throws Exception {
        Assertions.assertThrows(InvalidSmilesException.class,
                                () -> {
                                    load("50-00-0");
                                });
    }

    @Test
    void atomBasedDbStereo() throws Exception {
        assertThat(SmilesGenerator.isomeric().create(load("F[C@H]=[C@@H]F")),
                   is("F/C=C/F"));
        assertThat(SmilesGenerator.isomeric().create(load("F[C@H]=[C@H]F")),
                   is("F/C=C\\F"));
        assertThat(SmilesGenerator.isomeric().create(load("F[C@@H]=[C@H]F")),
                   is("F/C=C/F"));
        assertThat(SmilesGenerator.isomeric().create(load("F[C@@H]=[C@@H]F")),
                   is("F/C=C\\F"));
    }

    @Test
    void atomBasedDbStereoReversing() throws Exception {
        assertThat(SmilesGenerator.isomeric().create(load("[C@H](F)=[C@@H]F")),
                   is("C(\\F)=C\\F"));
    }

    @Test
    void azuleneHasAllBondOrdersSet() throws Exception {
        IAtomContainer mol = load("c1ccc-2cccccc12");
        for (IBond bond : mol.bonds()) {
            if (bond.getOrder() == null || bond.getOrder() == IBond.Order.UNSET)
                Assertions.fail("Unset bond order");
        }
    }

    @Test
    void cisplatin() throws Exception {
        IAtomContainer mol = load("[NH3][Pt@SP1]([NH3])(Cl)Cl");
        Iterator<IStereoElement> ses =mol.stereoElements().iterator();
        Assertions.assertTrue(ses.hasNext());
        IStereoElement se = ses.next();
        assertThat(se, instanceOf(SquarePlanar.class));
        assertThat(se.getConfigOrder(), is(1));
    }

    @Test
    void cisplatin_Z() throws Exception {
        IAtomContainer mol = load("[NH3][Pt@SP3]([NH3])(Cl)Cl");
        Iterator<IStereoElement> ses =mol.stereoElements().iterator();
        Assertions.assertTrue(ses.hasNext());
        IStereoElement se = ses.next();
        assertThat(se, instanceOf(SquarePlanar.class));
        assertThat(se.getConfigOrder(), is(3));
    }

    @Test
    void transplatin() throws Exception {
        IAtomContainer mol = load("[NH3][Pt@SP2]([NH3])(Cl)Cl");
        Iterator<IStereoElement> ses =mol.stereoElements().iterator();
        Assertions.assertTrue(ses.hasNext());
        IStereoElement se = ses.next();
        assertThat(se, instanceOf(SquarePlanar.class));
        assertThat(se.getConfigOrder(), is(2));
    }

    @Test
    void tbpy1() throws Exception {
        IAtomContainer mol = load("S[As@TB1](F)(Cl)(Br)N");
        Iterator<IStereoElement> ses =mol.stereoElements().iterator();
        Assertions.assertTrue(ses.hasNext());
        IStereoElement se = ses.next();
        assertThat(se, instanceOf(TrigonalBipyramidal.class));
        assertThat(se.getConfigOrder(), is(1));
    }

    @Test
    void tbpy2() throws Exception {
        IAtomContainer mol = load("S[As@TB2](F)(Cl)(Br)N");
        Iterator<IStereoElement> ses =mol.stereoElements().iterator();
        Assertions.assertTrue(ses.hasNext());
        IStereoElement se = ses.next();
        assertThat(se, instanceOf(TrigonalBipyramidal.class));
        assertThat(se.getConfigOrder(), is(2));
    }

    @Test
    void oh1() throws Exception {
        IAtomContainer mol = load("C[Co@](F)(Cl)(Br)(I)S");
        Iterator<IStereoElement> ses =mol.stereoElements().iterator();
        Assertions.assertTrue(ses.hasNext());
        IStereoElement se = ses.next();
        assertThat(se, instanceOf(Octahedral.class));
        assertThat(se.getConfigOrder(), is(1));
    }

    @Test
    void oh8() throws Exception {
        IAtomContainer mol = load("C[Co@OH8](F)(Br)(Cl)(I)S");
        Iterator<IStereoElement> ses =mol.stereoElements().iterator();
        Assertions.assertTrue(ses.hasNext());
        IStereoElement se = ses.next();
        assertThat(se, instanceOf(Octahedral.class));
        assertThat(se.getConfigOrder(), is(8));
    }

    @Test
    void extendedExtendedTrans3() throws Exception {
        IAtomContainer mol = load("C/C=C=C=C/C");
        for (IStereoElement se : mol.stereoElements()) {
            if (se instanceof ExtendedCisTrans) {
                ExtendedCisTrans ect = (ExtendedCisTrans) se;
                assertThat(ect.getConfigOrder(),
                           is(IStereoElement.OPPOSITE));
                assertThat(ect.getFocus(), is(mol.getBond(2)));
                assertThat(ect.getCarriers().get(0),
                           is(mol.getBond(0)));
                assertThat(ect.getCarriers().get(1),
                           is(mol.getBond(4)));
            }
        }
    }

    @Test
    void extendedExtendedCis3() throws Exception {
        IAtomContainer mol = load("C/C=C=C=C\\C");
        for (IStereoElement se : mol.stereoElements()) {
            if (se instanceof ExtendedCisTrans) {
                ExtendedCisTrans ect = (ExtendedCisTrans) se;
                assertThat(ect.getConfigOrder(),
                           is(IStereoElement.TOGETHER));
                assertThat(ect.getFocus(), is(mol.getBond(2)));
                assertThat(ect.getCarriers().get(0),
                           is(mol.getBond(0)));
                assertThat(ect.getCarriers().get(1),
                           is(mol.getBond(4)));
            }
        }
    }

    @Test
    void extendedExtendedCis5() throws Exception {
        IAtomContainer mol = load("C/C=C=C=C=C=C\\C");
        for (IStereoElement se : mol.stereoElements()) {
            if (se instanceof ExtendedCisTrans) {
                ExtendedCisTrans ect = (ExtendedCisTrans) se;
                assertThat(ect.getConfigOrder(),
                           is(IStereoElement.TOGETHER));
                assertThat(ect.getFocus(), is(mol.getBond(3)));
                assertThat(ect.getCarriers().get(0),
                           is(mol.getBond(0)));
                assertThat(ect.getCarriers().get(1),
                           is(mol.getBond(6)));
            }
        }
    }

    // an even number of double bonds is extended tetrahedral not
    // extended Cis/Trans
    @Test
    void notExtendedCis() throws Exception {
        IAtomContainer mol = load("C/C=C=C=C=C\\C");
        Assertions.assertFalse(mol.stereoElements().iterator().hasNext());
    }

    @Test
    void warnOnDirectionalBonds() throws InvalidSmilesException {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles("C/C=C/1.C/1");
    }

    @Test
    void failOnDirectionalBondsWhenStrict() throws InvalidSmilesException {
        Assertions.assertThrows(InvalidSmilesException.class,
                                () -> {
                                    SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
                                    smipar.setStrict(true);
                                    IAtomContainer mol = smipar.parseSmiles("C/C=C/1.C/1");
                                });
    }

    @Test
    void ignoreDoubleBond() throws InvalidSmilesException {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles("C/C=C(/F)/C");
        assertThat(mol.stereoElements().iterator().hasNext(),
                   is(false));
    }

    @Test
    void extendedTetrahedral7() throws InvalidSmilesException {
        IAtomContainer mol = load("CC=C=C=[C@]=C=C=CC");
        for (IStereoElement se : mol.stereoElements()) {
            if (se instanceof ExtendedTetrahedral) {
                ExtendedTetrahedral et = (ExtendedTetrahedral) se;
                assertThat(et.getConfigOrder(),
                           is(IStereoElement.LEFT));
                assertThat(et.getFocus(), is(mol.getAtom(4)));
                assertThat(et.getCarriers().toArray(new IAtom[4]),
                           is(new IAtom[]{
                               mol.getAtom(0),
                               mol.getAtom(1),
                               mol.getAtom(7),
                               mol.getAtom(8)
                           }));
            }
        }
    }


    @Test
    void testCisTransBondsOnRings() throws InvalidSmilesException {
        IAtomContainer mol = load("N\\2.C/C2=C\\C(=O)O");
        int count = 0;
        for (IStereoElement<?,?> se : mol.stereoElements()) {
            if (se.getConfigClass() == IStereoElement.CisTrans)
                count++;
        }
        Assertions.assertEquals(1, count);
    }

    @Test
    void testDirectionalBondAromaticity() throws InvalidSmilesException {
        IAtomContainer mol = load("C=c1ccccc/1=C/C");
        int count = 0;
        for (IBond bond : mol.bonds())
            count += bond.isAromatic() ? 1 : 0;
        Assertions.assertEquals(6, count);
    }

    @Test
    void testMultiStepSmiles() throws Exception {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smigen = new SmilesGenerator(SmiFlavor.Default);
        IReactionSet reactions = smipar.parseReactionSetSmiles("[Pb]>>[Ag]>>[Au]");
        Assertions.assertEquals(2, reactions.getReactionCount());
        Assertions.assertEquals("[Pb]>>[Ag]", smigen.create(reactions.getReaction(0)));
        Assertions.assertEquals("[Ag]>>[Au]", smigen.create(reactions.getReaction(1)));
    }

    @Test
    void testMultiStepSmilesEmpties() throws Exception {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smigen = new SmilesGenerator(SmiFlavor.Default);
        IReactionSet reactions = smipar.parseReactionSetSmiles(">[Pb]>>[Ag]>>[Au]>");
        Assertions.assertEquals(3, reactions.getReactionCount());
        Assertions.assertEquals(">[Pb]>", smigen.create(reactions.getReaction(0)));
        Assertions.assertEquals(">[Ag]>", smigen.create(reactions.getReaction(1)));
        Assertions.assertEquals(">[Au]>", smigen.create(reactions.getReaction(2)));
    }

    /**
     * Counts aromatic atoms in a molecule.
     * @param mol molecule for which to count aromatic atoms.
     */
    private int countAromaticAtoms(IAtomContainer mol) {
        int aromCount = 0;
        for (IAtom atom : mol.atoms()) {
            if (atom.getFlag(IChemObject.AROMATIC)) aromCount++;
        }
        return aromCount;
    }

    /**
     * Counts aromatic bonds in a molecule.
     * @param mol molecule for which to count aromatic bonds.
     */
    private int countAromaticBonds(IAtomContainer mol) {
        int aromCount = 0;
        for (IBond bond : mol.bonds()) {
            if (bond.getFlag(IChemObject.AROMATIC)) aromCount++;
        }
        return aromCount;
    }

    static void atomtype(IAtomContainer container) throws Exception {
        Set<IAtom> aromatic = new HashSet<>();
        for (IAtom atom : container.atoms()) {
            if (atom.getFlag(IChemObject.AROMATIC)) aromatic.add(atom);
        }
        // helpfully clears aromatic flags...
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(container);
        for (IAtom atom : aromatic)
            atom.setFlag(IChemObject.AROMATIC, true);
    }

    static IAtomContainer load(String smi) throws InvalidSmilesException {
        return new SmilesParser(SilentChemObjectBuilder.getInstance()).parseSmiles(smi);
    }

    static IAtomContainer loadExact(String smi) throws InvalidSmilesException {
        SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        parser.kekulise(false);
        return parser.parseSmiles(smi);
    }

    @Test
    void testNoTitle() throws InvalidSmilesException {
        SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = parser.parseSmiles("CCC");
        Assertions.assertNull(mol.getProperty("cdk:Title"));
    }

    @Test
    void testTitleMol() throws InvalidSmilesException {
        SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = parser.parseSmiles("CCC a molecule");
        Assertions.assertEquals("a molecule", mol.getTitle());
    }

    @Test
    void testTitleRxn() throws InvalidSmilesException {
        SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IReaction r = parser.parseReactionSmiles("CCC>>CCO a reaction");
        Assertions.assertEquals("a reaction", r.getProperty(CDKConstants.TITLE));
    }

    @Test
    void testTitleRxns() throws InvalidSmilesException {
        SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IReactionSet rs = parser.parseReactionSetSmiles("CCC>>CCO a reaction");
        Assertions.assertEquals("a reaction", rs.getProperty(CDKConstants.TITLE));
    }
}
