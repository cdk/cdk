/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.geometry.cip.rules;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.geometry.cip.CIPTool;
import org.openscience.cdk.geometry.cip.ILigand;
import org.openscience.cdk.geometry.cip.ImplicitHydrogenLigand;
import org.openscience.cdk.geometry.cip.Ligand;
import org.openscience.cdk.geometry.cip.VisitedAtoms;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * @cdk.module test-cip
 */
class CIPLigandRuleTest extends CDKTestCase {

    private static final SmilesParser smiles = new SmilesParser(SilentChemObjectBuilder.getInstance());

    @Test
    void testCBrIFCl() throws Exception {
        IAtomContainer molecule = smiles.parseSmiles("FC(Br)(Cl)I");
        ILigand ligandF = new Ligand(molecule, new VisitedAtoms(), molecule.getAtom(1), molecule.getAtom(0));
        ILigand ligandBr = new Ligand(molecule, new VisitedAtoms(), molecule.getAtom(1), molecule.getAtom(2));
        ILigand ligandCl = new Ligand(molecule, new VisitedAtoms(), molecule.getAtom(1), molecule.getAtom(3));
        ILigand ligandI = new Ligand(molecule, new VisitedAtoms(), molecule.getAtom(1), molecule.getAtom(4));
        ISequenceSubRule<ILigand> rule = new CIPLigandRule();
        Assertions.assertEquals(-1, rule.compare(ligandF, ligandI));
        Assertions.assertEquals(-1, rule.compare(ligandF, ligandBr));
        Assertions.assertEquals(-1, rule.compare(ligandF, ligandCl));
        Assertions.assertEquals(-1, rule.compare(ligandCl, ligandI));
        Assertions.assertEquals(-1, rule.compare(ligandCl, ligandBr));
        Assertions.assertEquals(-1, rule.compare(ligandBr, ligandI));

        List<ILigand> ligands = new ArrayList<>();
        ligands.add(ligandI);
        ligands.add(ligandBr);
        ligands.add(ligandF);
        ligands.add(ligandCl);
        ligands.sort(new CIPLigandRule());

        Assertions.assertEquals("F", ligands.get(0).getLigandAtom().getSymbol());
        Assertions.assertEquals("Cl", ligands.get(1).getLigandAtom().getSymbol());
        Assertions.assertEquals("Br", ligands.get(2).getLigandAtom().getSymbol());
        Assertions.assertEquals("I", ligands.get(3).getLigandAtom().getSymbol());
    }

    @Test
    void testCompare_Identity() throws Exception {
        IAtomContainer molecule = smiles.parseSmiles("CC(Br)([13C])[H]");
        ILigand ligand = new Ligand(molecule, new VisitedAtoms(), molecule.getAtom(1), molecule.getAtom(0));
        ISequenceSubRule<ILigand> rule = new CIPLigandRule();
        Assertions.assertEquals(0, rule.compare(ligand, ligand));
    }

    @Test
    void testCompare() throws Exception {
        IAtomContainer molecule = smiles.parseSmiles("CC(Br)([13C])[H]");
        ILigand ligand1 = new Ligand(molecule, new VisitedAtoms(), molecule.getAtom(1), molecule.getAtom(0));
        ILigand ligand2 = new Ligand(molecule, new VisitedAtoms(), molecule.getAtom(1), molecule.getAtom(2));
        ISequenceSubRule<ILigand> rule = new CIPLigandRule();
        Assertions.assertEquals(-1, rule.compare(ligand1, ligand2));
        Assertions.assertEquals(1, rule.compare(ligand2, ligand1));
    }

    @Test
    void testOrder() throws Exception {
        IAtomContainer molecule = smiles.parseSmiles("CC(Br)([13C])[H]");
        List<ILigand> ligands = new ArrayList<>();
        VisitedAtoms visitedAtoms = new VisitedAtoms();
        ligands.add(CIPTool.defineLigand(molecule, visitedAtoms, 1, 4));
        ligands.add(CIPTool.defineLigand(molecule, visitedAtoms, 1, 3));
        ligands.add(CIPTool.defineLigand(molecule, visitedAtoms, 1, 2));
        ligands.add(CIPTool.defineLigand(molecule, visitedAtoms, 1, 0));

        ligands.sort(new CIPLigandRule());
        Assertions.assertEquals("H", ligands.get(0).getLigandAtom().getSymbol());
        Assertions.assertEquals("C", ligands.get(1).getLigandAtom().getSymbol());
        Assertions.assertEquals("C", ligands.get(2).getLigandAtom().getSymbol());
        Assertions.assertEquals(13, ligands.get(2).getLigandAtom().getMassNumber().intValue());
        Assertions.assertEquals("Br", ligands.get(3).getLigandAtom().getSymbol());
    }

    /**
     * Test that verifies the branching of the side chains determines precedence for ties.
     */
    @Test
    void testSideChains() throws Exception {
        IAtomContainer molecule = smiles.parseSmiles("CC(C)C([H])(C)CC");
        ILigand ligand1 = CIPTool.defineLigand(molecule, new VisitedAtoms(), 3, 6);
        ILigand ligand2 = CIPTool.defineLigand(molecule, new VisitedAtoms(), 3, 1);
        ISequenceSubRule<ILigand> rule = new CIPLigandRule();
        Assertions.assertEquals(-1, rule.compare(ligand1, ligand2));
        Assertions.assertEquals(1, rule.compare(ligand2, ligand1));
    }

    /**
     * Test that verifies the branching of the side chains determines precedence for ties,
     * but unlike {@link #testSideChains()}, the tie only gets resolved after recursion.
     */
    @Test
    void testSideChains_Recursive() throws Exception {
        IAtomContainer molecule = smiles.parseSmiles("CCCC([H])(C)CC");
        ILigand ligand1 = CIPTool.defineLigand(molecule, new VisitedAtoms(), 3, 6);
        ILigand ligand2 = CIPTool.defineLigand(molecule, new VisitedAtoms(), 3, 1);
        ISequenceSubRule<ILigand> rule = new CIPLigandRule();
        Assertions.assertEquals(-1, rule.compare(ligand1, ligand2));
        Assertions.assertEquals(1, rule.compare(ligand2, ligand1));
    }

    /**
     * The CIP sequence rule prescribes that double bonded side chains of a ligand
     * are counted twice. This alone, is not enough to distinguish between a
     * hypothetical dialcohol and a aldehyde.
     */
    @Test
    void testTwoVersusDoubleBondedOxygen() throws Exception {
        IAtomContainer molecule = smiles.parseSmiles("OC(O)C([H])(C)C=O");
        ILigand ligand1 = CIPTool.defineLigand(molecule, new VisitedAtoms(), 3, 1);
        ILigand ligand2 = CIPTool.defineLigand(molecule, new VisitedAtoms(), 3, 6);
        ISequenceSubRule<ILigand> rule = new CIPLigandRule();
        Assertions.assertEquals(-1, rule.compare(ligand1, ligand2));
        Assertions.assertEquals(1, rule.compare(ligand2, ligand1));
    }

    /**
     * Tests deep recursion.
     */
    @Test
    void testDeepRecursion() throws Exception {
        IAtomContainer molecule = smiles.parseSmiles("CC([H])(CCCCCCCCCC)CCCCCCCCC");
        ILigand ligand1 = CIPTool.defineLigand(molecule, new VisitedAtoms(), 1, 3);
        ILigand ligand2 = CIPTool.defineLigand(molecule, new VisitedAtoms(), 1, 13);
        ISequenceSubRule<ILigand> rule = new CIPLigandRule();
        Assertions.assertEquals(1, rule.compare(ligand1, ligand2));
        Assertions.assertEquals(-1, rule.compare(ligand2, ligand1));
    }

    @Test
    void testImplicitHydrogen_Same() throws Exception {
        IAtomContainer molecule = smiles.parseSmiles("CC(Br)([13C])[H]");
        ILigand ligand1 = new ImplicitHydrogenLigand(molecule, new VisitedAtoms(), molecule.getAtom(1));
        ILigand ligand2 = new Ligand(molecule, new VisitedAtoms(), molecule.getAtom(1), molecule.getAtom(4));
        ISequenceSubRule<ILigand> rule = new CIPLigandRule();
        Assertions.assertEquals(0, rule.compare(ligand1, ligand2));
        Assertions.assertEquals(0, rule.compare(ligand2, ligand1));
    }

    @Test
    void testImplicitHydrogen() throws Exception {
        IAtomContainer molecule = smiles.parseSmiles("CC(Br)([2H])[H]");
        ILigand ligand1 = new ImplicitHydrogenLigand(molecule, new VisitedAtoms(), molecule.getAtom(1));
        ILigand ligand2 = new Ligand(molecule, new VisitedAtoms(), molecule.getAtom(1), molecule.getAtom(3));
        ISequenceSubRule<ILigand> rule = new CIPLigandRule();
        Assertions.assertEquals(-1, rule.compare(ligand1, ligand2));
        Assertions.assertEquals(1, rule.compare(ligand2, ligand1));

        ligand2 = new Ligand(molecule, new VisitedAtoms(), molecule.getAtom(1), molecule.getAtom(2));
        Assertions.assertEquals(-1, rule.compare(ligand1, ligand2));
        Assertions.assertEquals(1, rule.compare(ligand2, ligand1));
    }
}
