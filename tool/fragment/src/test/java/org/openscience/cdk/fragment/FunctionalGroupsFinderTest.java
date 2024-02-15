/*
 * Copyright (c) 2024 Sebastian Fritsch <>
 *                    Stefan Neumann <>
 *                    Jonas Schaub <jonas.schaub@uni-jena.de>
 *                    Christoph Steinbeck <christoph.steinbeck@uni-jena.de>
 *                    Achim Zielesny <achim.zielesny@w-hs.de>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscience.cdk.fragment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.aromaticity.ElectronDonation;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.isomorphism.Mappings;
import org.openscience.cdk.isomorphism.Pattern;
import org.openscience.cdk.isomorphism.VentoFoggia;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Test for FunctionalGroupsFinder.
 *
 * @author Sebastian Fritsch, Jonas Schaub
 * @version 1.3
 */
public class FunctionalGroupsFinderTest {
    /**
     * Constructor.
     */
    public FunctionalGroupsFinderTest() {
        super();
    }
    //
    /**
     * Example code to be used in the GitHub wiki of the project.
     *
     * @throws Exception if anything goes wrong
     * @author Jonas Schaub
     */
    @Test
    public void gitHubWikiTest() throws Exception {
        //Prepare input
        SmilesParser tmpSmiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpInputMol = tmpSmiPar.parseSmiles("C[C@@H]1CN(C[C@H](C)N1)C2=C(C(=C3C(=C2F)N(C=C(C3=O)C(=O)O)C4CC4)N)F"); //PubChem CID 5257
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(tmpInputMol);
        Aromaticity tmpAromaticity = new Aromaticity(ElectronDonation.cdk(), Cycles.cdkAromaticSet());
        tmpAromaticity.apply(tmpInputMol);
        //Identify functional groups
        FunctionalGroupsFinder tmpFGF = new FunctionalGroupsFinder(); //default: generalization turned on
        List<IAtomContainer> tmpFunctionalGroupsList = tmpFGF.find(tmpInputMol);
        SmilesGenerator tmpSmiGen = new SmilesGenerator(SmiFlavor.Canonical | SmiFlavor.UseAromaticSymbols);
        for (IAtomContainer tmpFunctionalGroup : tmpFunctionalGroupsList) {
            String tmpSmilesString = tmpSmiGen.create(tmpFunctionalGroup);
            //System.out.println(tmpSmilesString);
        }
        //non-generalized functional groups
        //System.out.println("----------------");
        tmpFGF = new FunctionalGroupsFinder(FunctionalGroupsFinder.Mode.NO_GENERALIZATION);
        tmpFunctionalGroupsList = tmpFGF.find(tmpInputMol);
        for (IAtomContainer tmpFunctionalGroup : tmpFunctionalGroupsList) {
            String tmpSmilesString = tmpSmiGen.create(tmpFunctionalGroup);
            //System.out.println(tmpSmilesString);
        }
    }
    //
    /**
     * Tests correct functional group identification on an example molecule taken from Figure 1 of the original Ertl algorithm article.
     *
     * @throws Exception if anything goes wrong
     * @author Sebastian Fritsch
     */
    @Test
    public void testFind1() throws Exception {
        String tmpMoleculeSmiles = "Cc1cc(C)nc(NS(=O)(=O)c2ccc(N)cc2)n1";
        String[] tmpExpectedFGs = new String[] {"[R]N([R])S(=O)(=O)[R]", "[c]N(H)H", "NarR3", "NarR3"};
        this.testFind(tmpMoleculeSmiles, tmpExpectedFGs);
    }
    //
    /**
     * Tests correct functional group identification on an example molecule taken from Figure 1 of the original Ertl algorithm article.
     *
     * @throws Exception if anything goes wrong
     * @author Sebastian Fritsch
     */
    @Test
    public void testFind2() throws Exception {
        String tmpMoleculeSmiles = "NC(=N)c1ccc(\\\\C=C\\\\c2ccc(cc2O)C(=N)N)cc1";
        String[] tmpExpectedFGs = new String[] {"[R]N=C-N([R])[R]", "[C]=[C]", "[c]OH", "[R]N=C-N([R])[R]"};
        this.testFind(tmpMoleculeSmiles, tmpExpectedFGs);
    }
    //
    /**
     * Tests correct functional group identification on an example molecule taken from Figure 1 of the original Ertl algorithm article.
     *
     * @throws Exception if anything goes wrong
     * @author Sebastian Fritsch
     */
    @Test
    public void testFind3() throws Exception {
        String tmpMoleculeSmiles = "CC(=O)Nc1nnc(s1)S(=O)(=O)N";
        String[] tmpExpectedFGs = new String[] {"[R]N([R])C(=O)[R]", "[R]S(=O)(=O)N([R])[R]", "NarR3", "NarR3", "SarR2"};
        this.testFind(tmpMoleculeSmiles, tmpExpectedFGs);
    }
    //
    /**
     * Tests correct functional group identification on an example molecule taken from Figure 1 of the original Ertl algorithm article.
     *
     * @throws Exception if anything goes wrong
     * @author Sebastian Fritsch
     */
    @Test
    public void testFind4() throws Exception {
        String tmpMoleculeSmiles = "NS(=O)(=O)c1cc2c(NCNS2(=O)=O)cc1Cl";
        String[] tmpExpectedFGs = new String[] {"[R]S(=O)(=O)N([R])[R]", "[R]S(=O)(=O)N([R])[C]N([R])[R]", "[R]Cl"};
        this.testFind(tmpMoleculeSmiles, tmpExpectedFGs);
    }
    //
    /**
     * Tests correct functional group identification on an example molecule taken from Figure 1 of the original Ertl algorithm article.
     *
     * @throws Exception if anything goes wrong
     * @author Sebastian Fritsch
     */
    @Test
    public void testFind5() throws Exception {
        String tmpMoleculeSmiles = "CNC1=Nc2ccc(Cl)cc2C(=N(=O)C1)c3ccccc3";
        String[] tmpExpectedFGs = new String[] {"[R]N([R])[C]=N[R]", "[R]Cl", "[R]N(=O)=[C]"};
        this.testFind(tmpMoleculeSmiles, tmpExpectedFGs);
    }
    //
    /**
     * Tests correct functional group identification on an example molecule taken from Figure 1 of the original Ertl algorithm article.
     *
     * @throws Exception if anything goes wrong
     * @author Sebastian Fritsch
     */
    @Test
    public void testFind6() throws Exception {
        String tmpMoleculeSmiles = "Cc1onc(c2ccccc2)c1C(=O)N[C@H]3[C@H]4SC(C)(C)[C@@H](N4C3=O)C(=O)O";
        String[] tmpExpectedFGs = new String[] {"O=C([R])N([R])[R]",  "O=C([R])N([R])[C]S[R]", "O=C([R])OH", "OarR2", "NarR3"};
        this.testFind(tmpMoleculeSmiles, tmpExpectedFGs);
    }
    //
    /**
     * Tests correct functional group identification on an example molecule taken from Figure 1 of the original Ertl algorithm article.
     *
     * @throws Exception if anything goes wrong
     * @author Sebastian Fritsch
     */
    @Test
    public void testFind7() throws Exception {
        String tmpMoleculeSmiles = "Clc1ccccc1C2=NCC(=O)Nc3ccc(cc23)N(=O)=O";
        String[] tmpExpectedFGs = new String[] {"[R]Cl", "[R]N=[C]", "[R]C(=O)N([R])[R]", "O=N([R])=O"};
        this.testFind(tmpMoleculeSmiles, tmpExpectedFGs);
    }
    //
    /**
     * Tests correct functional group identification on an example molecule taken from Figure 1 of the original Ertl algorithm article.
     *
     * @throws Exception if anything goes wrong
     * @author Sebastian Fritsch
     */
    @Test
    public void testFind8() throws Exception {
        String tmpMoleculeSmiles = "COc1cc(cc(C(=O)NCC2CCCN2CC=C)c1OC)S(=O)(=O)N";
        String[] tmpExpectedFGs = new String[] {"[R]O[R]", "[R]N([R])C(=O)[R]", "N([R])([R])[R]", "[C]=[C]", "[R]O[R]", "[R]S(=O)(=O)N([R])[R]"};
        this.testFind(tmpMoleculeSmiles, tmpExpectedFGs);
    }
    //
    /**
     * Tests correct functional group identification on an example molecule taken from Figure 1 of the original Ertl algorithm article.
     *
     * @throws Exception if anything goes wrong
     * @author Sebastian Fritsch
     */
    @Test
    public void testFind9() throws Exception {
        String tmpMoleculeSmiles = "Cc1ccc(Cl)c(Nc2ccccc2C(=O)O)c1Cl";
        String[] tmpExpectedFGs = new String[] {"[R]Cl", "[R]N(H)[R]", "O=C(OH)[R]", "[R]Cl"};
        this.testFind(tmpMoleculeSmiles, tmpExpectedFGs);
    }
    //
    /**
     * Tests correct functional group identification on an example molecule taken from Figure 1 of the original Ertl algorithm article.
     *
     * @throws Exception if anything goes wrong
     * @author Sebastian Fritsch
     */
    @Test
    public void testFind10() throws Exception {
        String tmpMoleculeSmiles = "Clc1ccc2Oc3ccccc3N=C(N4CCNCC4)c2c1";
        String[] tmpExpectedFGs = new String[] {"[R]Cl", "[R]O[R]", "[R]N([R])[C]=N[R]", "[R]N([H])[R]"};
        this.testFind(tmpMoleculeSmiles, tmpExpectedFGs);
    }
    //
    /**
     * Tests correct functional group identification on an example molecule taken from Figure 1 of the original Ertl algorithm article.
     *
     * @throws Exception if anything goes wrong
     * @author Sebastian Fritsch
     */
    @Test
    public void testFind11() throws Exception {
        String tmpMoleculeSmiles = "FC(F)(F)CN1C(=O)CN=C(c2ccccc2)c3cc(Cl)ccc13";
        String[] tmpExpectedFGs = new String[] {"[R]F", "[R]F", "[R]F", "O=C([R])N([R])[R]", "[R]N=[C]", "[R]Cl"};
        this.testFind(tmpMoleculeSmiles, tmpExpectedFGs);
    }
    //
    /**
     * Tests correct functional group identification on an example molecule taken from Figure 1 of the original Ertl algorithm article.
     *
     * @throws Exception if anything goes wrong
     * @author Sebastian Fritsch
     */
    @Test
    public void testFind12() throws Exception {
        String tmpMoleculeSmiles = "OC[C@H]1O[C@H](C[C@@H]1O)n2cnc3[C@H](O)CNC=Nc23";
        String[] tmpExpectedFGs = new String[] {"[C]O[H]", "[R]O[R]", "[C]OH", "[C]OH", "[R]N=CN([R])[R]", "NarR3", "NarR3"};
        this.testFind(tmpMoleculeSmiles, tmpExpectedFGs);
    }
    //
    /**
     * Tests correct functional group identification on an example molecule taken from Figure 1 of the original Ertl algorithm article.
     *
     * @throws Exception if anything goes wrong
     * @author Sebastian Fritsch
     */
    @Test
    public void testFind13() throws Exception {
        String tmpMoleculeSmiles = "CCN[C@H]1C[C@H](C)S(=O)(=O)c2sc(cc12)S(=O)(=O)N";
        String[] tmpExpectedFGs = new String[] {"[R]N([R])H", "O=S(=O)([R])[R]", "[R]S(=O)(=O)N([R])[R]", "SarR2"};
        this.testFind(tmpMoleculeSmiles, tmpExpectedFGs);
    }
    //
    /**
     * Tests correct functional group identification on an example molecule taken from Figure 1 of the original Ertl algorithm article.
     *
     * @throws Exception if anything goes wrong
     * @author Sebastian Fritsch
     */
    @Test
    public void testFind14() throws Exception {
        String tmpMoleculeSmiles = "C[C@@H](O)[C@@H]1[C@H]2[C@@H](C)C(=C(N2C1=O)C(=O)O)S[C@@H]3CN[C@@H](C3)C(=O)N(C)C";
        String[] tmpExpectedFGs = new String[] {"[C]O[H]", "O=C([R])N([R])C(C(=O)(OH))=[C]S[R]", "[R]N(H)[R]", "[R]N([R])C([R])=O"};
        this.testFind(tmpMoleculeSmiles, tmpExpectedFGs);
    }
    //
    /**
     * Tests correct functional group identification on an example molecule taken from Figure 1 of the original Ertl algorithm article.
     *
     * @throws Exception if anything goes wrong
     * @author Sebastian Fritsch
     */
    @Test
    public void testFind15() throws Exception {
        String tmpMoleculeSmiles = "C[C@@H]1CN(C[C@H](C)N1)c2c(F)c(N)c3C(=O)C(=CN(C4CC4)c3c2F)C(=O)O";
        String[] tmpExpectedFGs = new String[] {"[R]N([R])[R]", "[R]N([H])[R]", "[R]F", "[c]N(H)H", "[c]=O", "[R]F", "[R]C(=O)OH", "NarR3"};
        this.testFind(tmpMoleculeSmiles, tmpExpectedFGs);
    }
    //
    /**
     * Tests correct functional group identification on an example molecule taken from Figure 1 of the original Ertl algorithm article.
     *
     * @throws Exception if anything goes wrong
     * @author Sebastian Fritsch
     */
    @Test
    public void testFind16() throws Exception {
        String tmpMoleculeSmiles = "CC(=CCC1C(=O)N(N(C1=O)c2ccccc2)c3ccccc3)C";
        String[] tmpExpectedFGs = new String[] {"[C]=[C]", "[R]C(=O)N([R])N([R])C(=O)[R]"};
        this.testFind(tmpMoleculeSmiles, tmpExpectedFGs);
    }
    //
    /**
     * Tests correct functional group identification on an example molecule taken from Figure 1 of the original Ertl algorithm article.
     *
     * @throws Exception if anything goes wrong
     * @author Sebastian Fritsch
     */
    @Test
    public void testFind17() throws Exception {
        String tmpMoleculeSmiles = "Clc1ccc2N=C3NC(=O)CN3Cc2c1Cl";
        String[] tmpExpectedFGs = new String[] {"Cl[R]", "[R]N=C(N([R])[R])N([R])C(=O)[R]", "Cl[R]"};
        this.testFind(tmpMoleculeSmiles, tmpExpectedFGs);
    }
    //
    /**
     * Tests correct functional group identification on an example molecule taken from Figure 1 of the original Ertl algorithm article.
     *
     * @throws Exception if anything goes wrong
     * @author Sebastian Fritsch
     */
    @Test
    public void testFind18() throws Exception {
        String tmpMoleculeSmiles = "CC(=O)N[C@@H]1[C@@H](NC(=N)N)C=C(O[C@H]1[C@H](O)[C@H](O)CO)C(=O)O";
        String[] tmpExpectedFGs = new String[] {"[R]N([R])C(=O)[R]", "[R]N([R])C(=N[R])N([R])[R]", "O=C(OH)C(=[C])O[R]" , "[C]OH", "[C]OH", "[C]OH"};
        this.testFind(tmpMoleculeSmiles, tmpExpectedFGs);
    }
    //
    /**
     * Tests correct functional group identification on an example molecule taken from Figure 1 of the original Ertl algorithm article.
     *
     * @throws Exception if anything goes wrong
     * @author Sebastian Fritsch
     */
    @Test
    public void testFind19() throws Exception {
        String tmpMoleculeSmiles = "C[C@H](O)[C@H](O)[C@H]1CNc2nc(N)nc(O)c2N1";
        String[] tmpExpectedFGs = new String[] {"[C]OH", "[C]OH", "[R]N(H)[R]" , "[c]N(H)H",  "[c]OH", "[R]N(H)[R]", "NarR3", "NarR3"};
        this.testFind(tmpMoleculeSmiles, tmpExpectedFGs);
    }
    //
    /**
     * Tests correct functional group identification on an example molecule taken from Figure 1 of the original Ertl algorithm article.
     *
     * @throws Exception if anything goes wrong
     * @author Sebastian Fritsch
     */
    @Test
    public void testFind20() throws Exception {
        String tmpMoleculeSmiles = "N[C@@H]1CCCCN(C1)c2c(Cl)cc3C(=O)C(=CN(C4CC4)c3c2Cl)C(=O)O";
        String[] tmpExpectedFGs = new String[] {"[C]N([H])[H]", "[R]N([R])[R]", "[R]Cl" , "[c]=O", "[R]Cl", "[R]C(=O)OH", "NarR3"};
        this.testFind(tmpMoleculeSmiles, tmpExpectedFGs);
    }
    //
    /**
     * Tests correct functional group identification on an example molecule. Specifically, the extraction of only the marked atoms
     * in a functional group is tested. This feature was added in a later version.
     *
     * @throws Exception if anything goes wrong
     * @author Jonas Schaub
     */
    @Test
    public void testOnlyMarkedAtoms1() throws Exception {
        String tmpMoleculeSmiles = "CCO[Si](OCC)(OCC)OCC"; //Tetraethyl Orthosilicate
        String[] tmpExpectedFGs = new String[]{"[O][Si]([O])([O])[O]"};
        this.testFind(tmpMoleculeSmiles, tmpExpectedFGs, new Aromaticity(ElectronDonation.daylight(), Cycles.all()), FunctionalGroupsFinder.Mode.ONLY_MARKED_ATOMS);
    }
    //
    /**
     * Tests correct functional group identification on an example molecule. Specifically, the extraction of only the marked atoms
     * in a functional group is tested. This feature was added in a later version.
     *
     * @throws Exception if anything goes wrong
     * @author Jonas Schaub
     */
    @Test
    public void testOnlyMarkedAtoms2() throws Exception {
        String tmpMoleculeSmiles = "Cc1cc(C)nc(NS(=O)(=O)c2ccc(N)cc2)n1"; //same mol as testFind1() from the Ertl figure
        String[] tmpExpectedFGs = new String[] {"O=[S](=O)[NH]", "[NH2]", "Nar" , "Nar"};
        this.testFind(tmpMoleculeSmiles, tmpExpectedFGs, new Aromaticity(ElectronDonation.daylight(), Cycles.all()), FunctionalGroupsFinder.Mode.ONLY_MARKED_ATOMS);
    }
    //
    /**
     * Tests correct functional group identification on an example molecule. Specifically, the extraction of only the marked atoms
     * in a functional group is tested. This feature was added in a later version.
     *
     * @throws Exception if anything goes wrong
     * @author Jonas Schaub
     */
    @Test
    public void testOnlyMarkedAtoms3() throws Exception {
        String tmpMoleculeSmiles = "CO/N=C(\\C(=O)N[C@@H]1C(=O)N2C(C(=O)[O-])=C(C[N+]3(C)CCCC3)CS[C@H]12)c1csc(N)n1.Cl"; //CHEMBL1201736
        String[] tmpExpectedFGs = new String[] {"[O]N=[C]C(=O)[NH]", "[C]=C(C(=O)[O-])N([C]=O)[CH][S]", "[N+]", "[NH2]", "Cl", "Sar", "Nar"};
        this.testFind(tmpMoleculeSmiles, tmpExpectedFGs, new Aromaticity(ElectronDonation.daylight(), Cycles.all()), FunctionalGroupsFinder.Mode.ONLY_MARKED_ATOMS);
    }
    //
    /**
     * Tests correct functional group identification on an example molecule with formal charges.
     * This was not allowed in a previous version.
     *
     * @throws Exception if anything goes wrong
     * @author Jonas Schaub
     */
    @Test
    public void testChargedMolecules1() throws Exception {
        String tmpMoleculeSmiles = "CC(=O)OC1=CC=CC=C1C(=O)[O+]"; //charged ASA
        String[] tmpExpectedFGs = new String[] {"*OC(*)=O", "*C(=O)[O+]"};
        this.testFind(tmpMoleculeSmiles, tmpExpectedFGs);
    }
    //
    /**
     * Tests correct functional group identification on an example molecule with formal charges.
     * This was not allowed in a previous version.
     *
     * @throws Exception if anything goes wrong
     * @author Jonas Schaub
     */
    @Test
    public void testChargedMolecules2() throws Exception {
        String tmpMoleculeSmiles = "C1=CC(=CC=C1[N+](=O)[O-])O"; //Nitrophenol
        String[] tmpExpectedFGs = new String[] {"*[N+](=O)[O-]", "[H]O[c]"};
        this.testFind(tmpMoleculeSmiles, tmpExpectedFGs);
    }
    //
    /**
     * Tests correct functional group identification on an example molecule with formal charges.
     * This was not allowed in a previous version.
     *
     * @throws Exception if anything goes wrong
     * @author Jonas Schaub
     */
    @Test
    public void testChargedMolecules3() throws Exception {
        String tmpMoleculeSmiles = "C[N+](C)(C)C"; //Tetramethylammonium
        String[] tmpExpectedFGs = new String[] {"*[N+](*)(*)*"};
        this.testFind(tmpMoleculeSmiles, tmpExpectedFGs);
    }
    //
    /**
     * Tests correct functional group identification on an example molecule with formal charges.
     * This was not allowed in a previous version.
     *
     * @throws Exception if anything goes wrong
     * @author Jonas Schaub
     */
    @Test
    public void testChargedMolecules4() throws Exception {
        String tmpMoleculeSmiles = "c1ccccc1[CH+]C(Br)C"; //Carbenium ion in beta position to Br
        // carbenium ion is ignored since a charge is not a reason to mark carbon atom
        String[] tmpExpectedFGs = new String[] {"[C]Br"};
        this.testFind(tmpMoleculeSmiles, tmpExpectedFGs, new Aromaticity(ElectronDonation.daylight(), Cycles.all()), FunctionalGroupsFinder.Mode.NO_GENERALIZATION);

        tmpMoleculeSmiles = "c1ccccc1[CH+]C(Br)C"; //Carbenium ion in beta position to Br
        // carbenium ion is ignored since a charge is not a reason to mark carbon atom
        tmpExpectedFGs = new String[] {"[C]Br"};
        this.testFind(tmpMoleculeSmiles, tmpExpectedFGs, new Aromaticity(ElectronDonation.daylight(), Cycles.all()), FunctionalGroupsFinder.Mode.NO_GENERALIZATION);

        tmpMoleculeSmiles = "c1ccccc1[C+](Br)C"; //Carbenium ion in alpha position to Br
        // carbenium ion is extracted as environmental carbon and replaced by a new atom instance as all env carbon atoms in FGF; so it lost its charge!
        tmpExpectedFGs = new String[] {"[C]Br"};
        this.testFind(tmpMoleculeSmiles, tmpExpectedFGs, new Aromaticity(ElectronDonation.daylight(), Cycles.all()), FunctionalGroupsFinder.Mode.NO_GENERALIZATION);
    }
    //
    /**
     * Tests correct functional group identification on an example molecule with a disconnected structure.
     * This was not allowed in a previous version.
     *
     * @throws Exception if anything goes wrong
     * @author Jonas Schaub
     */
    @Test
    public void testDisconnectedMolecules1() throws Exception {
        String tmpMoleculeSmiles = "CC(=O)O.CC(=O)O.C1=CC(=CC=C1NC(=NC(=NCCCCCCN=C(N)N=C(N)NC2=CC=C(C=C2)Cl)N)N)Cl"; //Chlorhexidine Diacetate
        String[] tmpExpectedFGs = new String[] {"*C(=O)O[H]", "*C(=O)O[H]", "*N=C(N=C(N(*)*)N(*)*)N(*)*", "*N=C(N=C(N(*)*)N(*)*)N(*)*", "*Cl", "*Cl"};
        this.testFind(tmpMoleculeSmiles, tmpExpectedFGs);
    }
    //
    /**
     * Tests correct functional group identification on an example molecule with a disconnected structure.
     * This was not allowed in a previous version.
     *
     * @throws Exception if anything goes wrong
     * @author Jonas Schaub
     */
    @Test
    public void testDisconnectedMolecules2() throws Exception {
        String tmpMoleculeSmiles = "C(CN(CC(=O)[O-])CC(=O)[O-])N(CC(=O)[O-])CC(=O)[O-].[Na+].[Na+].[Na+].[Na+]"; //Sodium edetate
        String[] tmpExpectedFGs = new String[] {"*N(*)*", "*C(=O)[O-]", "*C(=O)[O-]", "*N(*)*", "*C(=O)[O-]", "*C(=O)[O-]", "[Na+]", "[Na+]", "[Na+]", "[Na+]"};
        this.testFind(tmpMoleculeSmiles, tmpExpectedFGs);
    }
    //
    /**
     * Tests correct functional group identification on an example molecule with metal/metalloid atoms.
     *
     * Note: all atoms are marked as hetero atoms by FGF that are not H or C. So, metals and metalloids get treated like
     * any other hetero atom.
     *
     * @throws Exception if anything goes wrong
     * @author Jonas Schaub
     */
    @Test
    public void testMetalsMetalloids1() throws Exception {
        String tmpMoleculeSmiles = "CCO[Si](OCC)(OCC)OCC"; //Tetraethyl Orthosilicate
        String[] tmpExpectedFGs = new String[]{"*O[Si](O*)(O*)O*"};
        this.testFind(tmpMoleculeSmiles, tmpExpectedFGs);
    }
    //
    /**
     * Tests correct functional group identification on an example molecule with metal/metalloid atoms.
     *
     * Note: all atoms are marked as hetero atoms by FGF that are not H or C. So, metals and metalloids get treated like
     * any other hetero atom.
     *
     * @throws Exception if anything goes wrong
     * @author Jonas Schaub
     */
    @Test
    public void testMetalsMetalloids2() throws Exception {
        String tmpMoleculeSmiles = "O.O.O=[Al]O[Si](=O)O[Si](=O)O[Al]=O"; //Kaolin
        String[] tmpExpectedFGs = new String[]{"*O*", "*O*", "O=[Al]O[Si](=O)O[Si](=O)O[Al]=O"};
        this.testFind(tmpMoleculeSmiles, tmpExpectedFGs);
    }
    //
    /**
     * Tests correct functional group identification on an example molecule with pseudo (R) atoms.
     *
     * Note: these pseudo (R) atoms are simply ignored by FGF.
     *
     * @throws Exception if anything goes wrong
     * @author Jonas Schaub
     */
    @Test
    public void testRAtoms1() throws Exception {
        String tmpMoleculeSmiles = "OCC(CO[*])OC([*])=O"; //CHEBI:598
        String[] tmpExpectedFGs = new String[]{"[H]O[C]", "[C][O]", "*O[C]=O"};
        this.testFind(tmpMoleculeSmiles, tmpExpectedFGs);
    }
    //
    /**
     * Tests that a former bug concerning unconnected, explicit hydrogen atoms does not occur anymore.
     *
     * @throws Exception if anything goes wrong
     * @author Jonas Schaub
     */
    @Test
    public void testHydrogenBug() throws Exception {
        String tmpMoleculeSmiles = "[H+].[H+].[O-]C(=O)\\C=C/C([O-])=O.[H][C@@]12Cc3c[nH]c4cccc(C1=C[C@@H](COC(=O)C1CCCCC1)CN2C)c34"; //CHEBI:365445
        String[] tmpExpectedFGs = new String[]{"O=C([O-])[C]=[C]C(=O)[O-]", "[C]=[C]", "*OC(*)=O", "[R]N([R])[R]", "NarR3"};
        this.testFind(tmpMoleculeSmiles, tmpExpectedFGs);

        tmpMoleculeSmiles = "[HH].O=C1N([C@H](C)C(C1=C(O)[C@]2([C@]3([C@H](C=C([C@H]2[C@@H](C(=O)O)CC)C)C[C@H](C)CC3)C)C)=O)C"; //CHEBI:223373
        tmpExpectedFGs = new String[]{"*C(=O)C(=[C]O[H])C(=O)N(*)*", "[C]=[C]", "*C(=O)O[H]"};
        this.testFind(tmpMoleculeSmiles, tmpExpectedFGs);
    }
    //
    /**
     * Applies FGF to detect functional groups in the given molecule and compares the identified FG to the given
     * expected FG, using i.a. an identity search. Note that the order of the given FG must match the order of the detected
     * FG. The expected FG can contain pseudo-SMILES code for some specific cases, where aromatic atoms are marked using
     * "-ar" and pseudo-atoms (R) can be included. Uses the electron donation model daylight and the cycle finder "all"
     * for aromaticity detection in the input molecule.
     *
     * @param aMoleculeSmiles input molecule to detect FG in
     * @param anExpectedFGPseudoSmilesArray expected FG
     * @throws Exception if anything goes wrong
     * @author Sebastian Fritsch
     */
    private void testFind(String aMoleculeSmiles, String[] anExpectedFGPseudoSmilesArray) throws Exception {
        this.testFind(aMoleculeSmiles, anExpectedFGPseudoSmilesArray, new Aromaticity(ElectronDonation.daylight(), Cycles.all()),
                FunctionalGroupsFinder.Mode.DEFAULT);
    }
    //
    /**
     * Applies FGF to detect functional groups in the given molecule and compares the identified FG to the given
     * expected FG, using i.a. an identity search. Note that the order of the given FG must match the order of the detected
     * FG. The expected FG can contain pseudo-SMILES code for some specific cases, where aromatic atoms are marked using
     * "-ar" and pseudo-atoms (R) can be included. The given aromaticity model is used for preprocessing the input molecule.
     *
     * @param aMoleculeSmiles input molecule to detect FG in
     * @param anExpectedFGPseudoSmilesArray expected FG
     * @param anAromaticityModel for aromaticity detection in preprocessing of the input molecule
     * @param aFunctionalGroupEnvironmentMode to configure the FGF used here
     * @throws Exception if anything goes wrong
     * @author Sebastian Fritsch
     */
    private void testFind(String aMoleculeSmiles, String[] anExpectedFGPseudoSmilesArray, Aromaticity anAromaticityModel,
                          FunctionalGroupsFinder.Mode aFunctionalGroupEnvironmentMode)
            throws Exception {
        // prepare input
        SmilesParser tmpSmilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer tmpMolecule = tmpSmilesParser.parseSmiles(aMoleculeSmiles);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(tmpMolecule);
        anAromaticityModel.apply(tmpMolecule);
        // find functional groups
        FunctionalGroupsFinder tmpFGFinder = new FunctionalGroupsFinder(aFunctionalGroupEnvironmentMode);
        List<IAtomContainer> tmpFunctionalgroupsList = tmpFGFinder.find(tmpMolecule);
        // get expected groups
        List<IAtomContainer> tmpExpectedFGs = new LinkedList<>();
        for (String tmpFGString : anExpectedFGPseudoSmilesArray) {
            tmpExpectedFGs.add(this.buildFunctionalGroup(tmpFGString));
        }
        // compare
        this.assertIsomorphism(tmpExpectedFGs, tmpFunctionalgroupsList);
    }
    //
    /**
     * Asserts the isomorphism between two lists of functional group atom containers. Compares their sizes, atom counts,
     * bond counts, performs an identity match using the Vento-Foggia algorithm, and checks that aromaticity annotations
     * match for the atoms and bonds.
     * NOTE: actual and expected functional groups must be in the same order!
     *
     * @param anExpectedFGsList list of expected functional groups
     * @param anActualFGsList list of actual functional groups
     * @author Sebastian Fritsch
     */
    private void assertIsomorphism(List<IAtomContainer> anExpectedFGsList, List<IAtomContainer> anActualFGsList) {
        Assertions.assertEquals(anExpectedFGsList.size(), anActualFGsList.size(),
                "Number of functional groups does not match the expected number of groups");
        for (int i = 0; i < anExpectedFGsList.size(); i++) {
            IAtomContainer tmpExpectedFG = anExpectedFGsList.get(i);
            IAtomContainer tmpActualFG = anActualFGsList.get(i);
            Assertions.assertEquals(tmpExpectedFG.getAtomCount(), tmpActualFG.getAtomCount(),
                    "Groups #" + i + ": different atom count");
            Assertions.assertEquals(tmpExpectedFG.getBondCount(),  tmpActualFG.getBondCount(),
                    "Groups #" + i + ": different bond count");
            Pattern tmpExpectedFGPattern = VentoFoggia.findIdentical(tmpExpectedFG);
            Assertions.assertTrue(tmpExpectedFGPattern.matches(tmpActualFG), "Groups #" + i + ": not isomorphic");
            Mappings tmpExpFGinActFGmappings = tmpExpectedFGPattern.matchAll(tmpActualFG);
            Map<IAtom, IAtom> tmpAtomMap = tmpExpFGinActFGmappings.toAtomMap().iterator().next();
            for (Map.Entry<IAtom, IAtom> tmpMapEntry : tmpAtomMap.entrySet()) {
                 IAtom tmpExpectedAtom = tmpMapEntry.getKey();
                 IAtom tmpActualAtom = tmpMapEntry.getValue();
                 Assertions.assertEquals(tmpExpectedAtom.isAromatic(), tmpActualAtom.isAromatic(),
                         "Groups #" + i + ": Atom aromaticity does not match ("
                                 + tmpActualAtom.getSymbol() + tmpActualAtom.isAromatic()
                                 + ":"
                                 + tmpExpectedAtom.getSymbol() + tmpExpectedAtom.isAromatic()
                                 + ")");
             }
            Map<IBond, IBond> tmpBondMap = tmpExpFGinActFGmappings.toBondMap().iterator().next();
            for (Map.Entry<IBond, IBond> tmpMapEntry : tmpBondMap.entrySet()) {
                 IBond tmpExpectedBond = tmpMapEntry.getKey();
                 IBond tmpActualBond = tmpMapEntry.getValue();
                 Assertions.assertEquals(tmpExpectedBond.isAromatic(), tmpActualBond.isAromatic(),
                         "Groups #" + i + ": Bond aromaticity does not match");
             }
        }
    }
    //
    /**
     * Constructs a functional group atom container object from a given SMILES or pseudo-SMILES code.
     * Pseudo-SMILES codes have aromatic atoms marked by "-ar", e.g. "Nar", and contain pseudo-atoms given as "R".
     * But the only available cases here are "NarR3", "SarR2", and "OarR2". There is no general treatment of any pseudo-SMILES
     * code! If the given string does not match any of the given three templates, it has to be a valid SMILES string!
     *
     * @param aFunctionalGroupPseudoSmiles SMILES code or specific pseudo-SMILES code
     * @return functional group atom container built from the given code
     * @author Sebastian Fritsch
     */
    private IAtomContainer buildFunctionalGroup(String aFunctionalGroupPseudoSmiles) {
        IAtom a1, a2, a3, a4, a5, a6, a7, a8, a9;
        IBond b1, b2, b3, b4, b5, b6, b7, b8, b9;
        IChemObjectBuilder tmpBuilder = SilentChemObjectBuilder.getInstance();
        IAtomContainer tmpFunctionalGroup;
        // custom templates:
        switch (aFunctionalGroupPseudoSmiles) {
        case "NarR3":
            a1 = tmpBuilder.newInstance(IPseudoAtom.class, "R");
            a2 = tmpBuilder.newInstance(IPseudoAtom.class, "R");
            a3 = tmpBuilder.newInstance(IPseudoAtom.class, "R");
            a4 = tmpBuilder.newInstance(IAtom.class, "N");
            a4.setIsAromatic(true);

            b1 = tmpBuilder.newInstance(IBond.class, a1, a4, Order.SINGLE);
            b2 = tmpBuilder.newInstance(IBond.class, a2, a4, Order.SINGLE);
            b3 = tmpBuilder.newInstance(IBond.class, a3, a4, Order.SINGLE);

            tmpFunctionalGroup = new AtomContainer();
            tmpFunctionalGroup.setAtoms(new IAtom[] {a1, a2, a3, a4});
            tmpFunctionalGroup.setBonds(new IBond[] {b1, b2, b3});
            return tmpFunctionalGroup;
        case "SarR2":
            a1 = tmpBuilder.newInstance(IPseudoAtom.class, "R");
            a2 = tmpBuilder.newInstance(IPseudoAtom.class, "R");
            a3 = tmpBuilder.newInstance(IAtom.class, "S");
            a3.setIsAromatic(true);

            b1 = tmpBuilder.newInstance(IBond.class, a1, a3, Order.SINGLE);
            b2 = tmpBuilder.newInstance(IBond.class, a2, a3, Order.SINGLE);

            tmpFunctionalGroup = new AtomContainer();
            tmpFunctionalGroup.setAtoms(new IAtom[] {a1, a2, a3});
            tmpFunctionalGroup.setBonds(new IBond[] {b1, b2});
            return tmpFunctionalGroup;
        case "OarR2":
            a1 = tmpBuilder.newInstance(IPseudoAtom.class, "R");
            a2 = tmpBuilder.newInstance(IPseudoAtom.class, "R");
            a3 = tmpBuilder.newInstance(IAtom.class, "O");
            a3.setIsAromatic(true);

            b1 = tmpBuilder.newInstance(IBond.class, a1, a3, Order.SINGLE);
            b2 = tmpBuilder.newInstance(IBond.class, a2, a3, Order.SINGLE);

            tmpFunctionalGroup = new AtomContainer();
            tmpFunctionalGroup.setAtoms(new IAtom[] {a1, a2, a3});
            tmpFunctionalGroup.setBonds(new IBond[] {b1, b2});
            return tmpFunctionalGroup;
        case "Nar":
            a1 = tmpBuilder.newInstance(IAtom.class, "N");
            a1.setIsAromatic(true);
            tmpFunctionalGroup = new AtomContainer();
            tmpFunctionalGroup.setAtoms(new IAtom[] {a1});
            return tmpFunctionalGroup;
        case "Sar":
            a1 = tmpBuilder.newInstance(IAtom.class, "S");
            a1.setIsAromatic(true);
            tmpFunctionalGroup = new AtomContainer();
            tmpFunctionalGroup.setAtoms(new IAtom[] {a1});
            return tmpFunctionalGroup;
        default:
            // treat as normal SMILES code
            try {
                SmilesParser tmpSmilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
                try {
                    if (aFunctionalGroupPseudoSmiles.equals("[c]=O")) {
                        tmpSmilesParser.kekulise(false);
                    }
                    tmpFunctionalGroup = tmpSmilesParser.parseSmiles(aFunctionalGroupPseudoSmiles);
                } catch(InvalidSmilesException e) {
                    tmpSmilesParser.kekulise(false);
                    tmpFunctionalGroup = tmpSmilesParser.parseSmiles(aFunctionalGroupPseudoSmiles);
                }
                for(IAtom a : tmpFunctionalGroup.atoms()) {
                    if (a instanceof PseudoAtom) {
                        a.setSymbol("R");
                    }
                }
                return tmpFunctionalGroup;
            } catch(InvalidSmilesException e) {
                throw new IllegalArgumentException("Input string '" + aFunctionalGroupPseudoSmiles + " could not be found as a template " +
                        "and is not a valid SMILES string.");
            }
        }
    }
}
