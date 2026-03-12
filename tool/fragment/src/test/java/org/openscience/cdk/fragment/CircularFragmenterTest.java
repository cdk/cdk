/*
 * Copyright (c) 2025 Jonas Schaub <jonas.schaub@uni-jena.de>
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.fragment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

//TODO: rework the tests, add more examples, remove unnecessary tests
//TODO: test for preservation of all bond and atom properties that are encoded in HOSE codes (rings, aromaticity, bond multiplicities, charges, stereochem?)
/**
 * Unit tests for {@link CircularFragmenter}.
 *
 * @author Jonas Schaub (jonas.schaub@uni-jena.de | jonas-schaub@gmx.de | <a href="https://github.com/JonasSchaub">JonasSchaub on GitHub</a>)
 * @author Claude Sonnet 4.6
 * @see CircularFragmenter
 */
class CircularFragmenterTest {

    /**
     * An empty molecule (no atoms) must yield an empty list.
     */
    @Test
    void testEmptyMolecule() {
        CircularFragmenter fragmenter = new CircularFragmenter();
        IAtomContainer empty = SilentChemObjectBuilder.getInstance().newAtomContainer();
        List<IAtomContainer> fragments = fragmenter.getCircularFragments(empty);
        Assertions.assertTrue(fragments.isEmpty(),
                "Empty molecule should produce an empty fragment list.");
    }

    /**
     * The number of returned fragments must equal the number of atoms in the
     * input molecule (one fragment per atom).
     *
     * @throws CDKException if SMILES parsing fails
     */
    @Test
    void testFragmentCountEqualsAtomCount() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        // Ethanol (3 heavy atoms)
        IAtomContainer mol = smiPar.parseSmiles("CCO");
        CircularFragmenter fragmenter = new CircularFragmenter(2);
        List<IAtomContainer> fragments = fragmenter.getCircularFragments(mol);
        Assertions.assertEquals(mol.getAtomCount(), fragments.size());
        SmilesGenerator smiGen = new SmilesGenerator(SmiFlavor.Canonical | SmiFlavor.UseAromaticSymbols);
        for (IAtomContainer frag : fragments) {
            //at radius 2, all fragments should include the whole molecule and hence be the same
            Assertions.assertEquals("OCC", smiGen.create(frag));
        }
    }

    /**
     * At radius 0, every fragment must contain exactly 1 atom (the root/center) and 0 bonds.
     *
     * @throws CDKException if SMILES parsing fails
     */
    @Test
    void testRadius0SingleAtomFragments() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smiPar.parseSmiles("CCCCC"); // pentane
        CircularFragmenter fragmenter = new CircularFragmenter(0);
        List<IAtomContainer> fragments = fragmenter.getCircularFragments(mol);
        for (IAtomContainer frag : fragments) {
            Assertions.assertEquals(1, frag.getAtomCount(),
                    "Radius-0 fragment must contain only the center atom.");
            Assertions.assertEquals(0, frag.getBondCount(),
                    "Radius-0 fragment must contain no bonds.");
        }
    }

    /**
     * For a linear pentane chain (C1–C2–C3–C4–C5), the fragment centered/rooted on C3 (index 2) should look like this:
     * <ul>
     *   <li>radius 1: C2, C3, C4 (3 atoms, 2 bonds)</li>
     *   <li>radius 2: C1, C2, C3, C4, C5 (5 atoms, 4 bonds, the whole molecule)</li>
     * </ul>
     *
     * @throws CDKException if SMILES parsing fails
     */
    @Test
    void testLinearChainCenterAtomRadius1and2() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        // CCCCC: atoms 0–4, middle atom is index 2
        IAtomContainer pentane = smiPar.parseSmiles("CCCCC");
        CircularFragmenter fragmenter = new CircularFragmenter(1);
        List<IAtomContainer> fragments = fragmenter.getCircularFragments(pentane);

        IAtomContainer centerFrag = fragments.get(2); // C3 is index 2
        Assertions.assertEquals(3, centerFrag.getAtomCount(),
                "Radius-1 fragment from chain center must contain 3 atoms.");
        Assertions.assertEquals(2, centerFrag.getBondCount(),
                "Radius-1 fragment from chain center must contain 2 bonds.");

        fragmenter.setRadius(2);
        fragments = fragmenter.getCircularFragments(pentane);

        centerFrag = fragments.get(2);
        Assertions.assertEquals(5, centerFrag.getAtomCount(),
                "Radius-2 fragment from chain center must contain all 5 atoms.");
        Assertions.assertEquals(4, centerFrag.getBondCount(),
                "Radius-2 fragment from chain center must contain 4 bonds.");
    }

    /**
     * For a linear pentane chain (C1–C2–C3–C4–C5), the fragment centered/rooted on C1
     * (index 0) should only contain C1 and C2 (two atoms, one bond).
     *
     * @throws CDKException if SMILES parsing fails
     */
    @Test
    void testLinearChainTerminalAtomRadius1() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer pentane = smiPar.parseSmiles("CCCCC");
        CircularFragmenter fragmenter = new CircularFragmenter(1);
        List<IAtomContainer> fragments = fragmenter.getCircularFragments(pentane);

        IAtomContainer terminalFrag = fragments.get(0); // C1 is index 0
        Assertions.assertEquals(2, terminalFrag.getAtomCount(),
                "Radius-1 fragment from terminal atom must contain 2 atoms.");
        Assertions.assertEquals(1, terminalFrag.getBondCount(),
                "Radius-1 fragment from terminal atom must contain 1 bond.");
    }

    /**
     * For benzene (6-membered ring) at radius 3, the fragment centered on any
     * atom must include all 6 atoms and all 6 bonds (the entire ring is
     * reachable within 3 bonds).
     *
     * @throws CDKException if SMILES parsing fails
     */
    @Test
    void testBenzeneRadius3AllAtoms() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer benzene = smiPar.parseSmiles("c1ccccc1");
        CircularFragmenter fragmenter = new CircularFragmenter(3);
        List<IAtomContainer> fragments = fragmenter.getCircularFragments(benzene);
        SmilesGenerator smiGen = new SmilesGenerator(SmiFlavor.Canonical | SmiFlavor.UseAromaticSymbols);
        for (int i = 0; i < fragments.size(); i++) {
            IAtomContainer frag = fragments.get(i);
            Assertions.assertEquals(6, frag.getAtomCount(),
                    "Radius-3 fragment from benzene atom " + i + " must contain all 6 atoms.");
            Assertions.assertEquals(6, frag.getBondCount(),
                    "Radius-3 fragment from benzene atom " + i + " must contain all 6 bonds.");
            Assertions.assertEquals("c1ccccc1", smiGen.create(frag));
        }
    }

    /**
     * For benzene at radius 1, the fragment centered on any atom includes that
     * atom and its two immediate ring neighbors (3 atoms, 2 bonds).
     *
     * @throws CDKException if SMILES parsing fails
     */
    @Test
    void testBenzeneRadius1() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer benzene = smiPar.parseSmiles("c1ccccc1");
        CircularFragmenter fragmenter = new CircularFragmenter(1);
        List<IAtomContainer> fragments = fragmenter.getCircularFragments(benzene);
        SmilesGenerator smiGen = new SmilesGenerator(SmiFlavor.Canonical | SmiFlavor.UseAromaticSymbols);
        for (int i = 0; i < fragments.size(); i++) {
            IAtomContainer frag = fragments.get(i);
            Assertions.assertEquals(3, frag.getAtomCount(),
                    "Radius-1 benzene fragment " + i + " must contain 3 atoms.");
            Assertions.assertEquals(2, frag.getBondCount(),
                    "Radius-1 benzene fragment " + i + " must contain 2 bonds.");
            Assertions.assertEquals("ccc", smiGen.create(frag));
        }
    }

    /**
     * For benzene at radius 2, each fragment must contain 5 atoms and 4 bonds.
     *
     * <p>From any center atom, BFS at depth 1 reaches its 2 direct neighbors,
     * and at depth 2 their respective other neighbors — giving 5 atoms in
     * total. The atom diametrically opposite across the 6-ring is at
     * shortest-path distance 3 and is therefore excluded. The 5 collected
     * atoms form an open chain (4 bonds); the bond that would close the ring
     * is absent because one of its endpoints was not collected.</p>
     *
     * @throws CDKException if SMILES parsing fails
     */
    @Test
    void testBenzeneRadius2() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer benzene = smiPar.parseSmiles("c1ccccc1");
        CircularFragmenter fragmenter = new CircularFragmenter(2);
        List<IAtomContainer> fragments = fragmenter.getCircularFragments(benzene);
        SmilesGenerator smiGen = new SmilesGenerator(SmiFlavor.Canonical | SmiFlavor.UseAromaticSymbols);
        for (int i = 0; i < fragments.size(); i++) {
            IAtomContainer frag = fragments.get(i);
            Assertions.assertEquals(5, frag.getAtomCount(),
                    "Radius-2 benzene fragment " + i + " must contain 5 atoms.");
            Assertions.assertEquals(4, frag.getBondCount(),
                    "Radius-2 benzene fragment " + i + " must contain 4 bonds.");
            Assertions.assertEquals("ccccc", smiGen.create(frag));
        }
    }

    /**
     * Ethylbenzene (CCc1ccccc1): at radius 2, the fragment centered on the
     * terminal methyl carbon (index 0) must contain 3 atoms and 2 bonds.
     *
     * <p>The BFS expands as follows:
     * <ul>
     *   <li>depth 0 — atom 0 (CH3)</li>
     *   <li>depth 1 — atom 1 (CH2)</li>
     *   <li>depth 2 — atom 2 (aromatic C, ring carbon)</li>
     * </ul>
     * The ring carbons adjacent to atom 2 are at depth 3 and are therefore
     * excluded. The resulting fragment is the propyl stub C–C–c with
     * 3 atoms and 2 bonds.</p>
     *
     * @throws CDKException if SMILES parsing fails
     */
    @Test
    void testEthylbenzeneTerminalRadius2AtomAndBondCount() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smiPar.parseSmiles("CCc1ccccc1");
        CircularFragmenter fragmenter = new CircularFragmenter(2);
        List<IAtomContainer> fragments = fragmenter.getCircularFragments(mol);
        SmilesGenerator smiGen = new SmilesGenerator(SmiFlavor.Canonical | SmiFlavor.UseAromaticSymbols);

        IAtomContainer frag = fragments.get(0);
        Assertions.assertEquals(3, frag.getAtomCount(),
                "Radius-2 fragment from terminal CH3 in ethylbenzene must contain 3 atoms.");
        Assertions.assertEquals(2, frag.getBondCount(),
                "Radius-2 fragment from terminal CH3 in ethylbenzene must contain 2 bonds.");
        Assertions.assertEquals("cCC", smiGen.create(frag));
    }

    /**
     * At a large enough radius the whole molecule is captured.
     * For ethylbenzene (8 atoms, 8 bonds) radius 5 must give the full molecule because of the symmetry of the ring.
     *
     * @throws CDKException if SMILES parsing fails
     */
    @Test
    void testEthylbenzeneLargeRadiusFullMolecule() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smiPar.parseSmiles("CCc1ccccc1");
        int atomCount = mol.getAtomCount();
        int bondCount = mol.getBondCount();

        CircularFragmenter fragmenter = new CircularFragmenter(5);
        List<IAtomContainer> fragments = fragmenter.getCircularFragments(mol);
        SmilesGenerator smiGen = new SmilesGenerator(SmiFlavor.Canonical | SmiFlavor.UseAromaticSymbols);

        for (int i = 0; i < fragments.size(); i++) {
            IAtomContainer frag = fragments.get(i);
            Assertions.assertEquals(atomCount, frag.getAtomCount(),
                    "Large-radius fragment " + i + " must contain all atoms.");
            Assertions.assertEquals(bondCount, frag.getBondCount(),
                    "Large-radius fragment " + i + " must contain all bonds.");
            Assertions.assertEquals("c1ccc(cc1)CC", smiGen.create(frag));
        }
    }

    /**
     * Atoms in the returned fragments must be distinct objects from the
     * original molecule (deep copies, not the same references).
     *
     * @throws CDKException if SMILES parsing fails
     */
    @Test
    void testFragmentAtomsAreDeepCopies() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smiPar.parseSmiles("CCO");
        CircularFragmenter fragmenter = new CircularFragmenter(2);
        List<IAtomContainer> fragments = fragmenter.getCircularFragments(mol);

        // Collect all original atom references
        Set<IAtom> originalAtoms = new HashSet<>();
        for (IAtom atom : mol.atoms()) {
            originalAtoms.add(atom);
        }

        for (IAtomContainer frag : fragments) {
            for (IAtom fragAtom : frag.atoms()) {
                Assertions.assertFalse(originalAtoms.contains(fragAtom),
                        "Fragment atom must not be the same object as the original atom.");
            }
        }
    }

    /**
     * Bonds in the returned fragments must be distinct objects from the
     * original molecule (deep copies, not the same references).
     *
     * @throws CDKException if SMILES parsing fails
     */
    @Test
    void testFragmentBondsAreDeepCopies() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smiPar.parseSmiles("CCO");
        CircularFragmenter fragmenter = new CircularFragmenter(2);
        List<IAtomContainer> fragments = fragmenter.getCircularFragments(mol);

        Set<IBond> originalBonds = new HashSet<>();
        for (IBond bond : mol.bonds()) {
            originalBonds.add(bond);
        }

        for (IAtomContainer frag : fragments) {
            for (IBond fragBond : frag.bonds()) {
                Assertions.assertFalse(originalBonds.contains(fragBond),
                        "Fragment bond must not be the same object as the original bond.");
            }
        }
    }

    /**
     * Modifying an atom symbol in a copied fragment must not affect the
     * corresponding atom in the original molecule.
     *
     * @throws CDKException if SMILES parsing fails
     */
    @Test
    void testMutatingFragmentDoesNotAffectOriginal() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smiPar.parseSmiles("CCO");
        String originalSymbol = mol.getAtom(0).getSymbol();

        CircularFragmenter fragmenter = new CircularFragmenter(2);
        List<IAtomContainer> fragments = fragmenter.getCircularFragments(mol);

        // Mutate the first atom in the first fragment
        fragments.get(0).getAtom(0).setSymbol("N");

        // The original molecule must be unchanged
        Assertions.assertEquals(originalSymbol, mol.getAtom(0).getSymbol(),
                "Mutating a copied fragment atom must not affect the original molecule.");
    }

    /**
     * The atoms stored in a copied bond (via {@code bond.getBegin()} /
     * {@code bond.getEnd()}) must be the copied atom instances inside the
     * fragment, not the originals.
     *
     * @throws CDKException if SMILES parsing fails
     */
    @Test
    void testFragmentBondsReferenceCopiedAtoms() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smiPar.parseSmiles("CCO");

        // Collect all original atom references
        Set<IAtom> originalAtoms = new HashSet<>();
        for (IAtom atom : mol.atoms()) {
            originalAtoms.add(atom);
        }

        CircularFragmenter fragmenter = new CircularFragmenter(2);
        List<IAtomContainer> fragments = fragmenter.getCircularFragments(mol);

        for (IAtomContainer frag : fragments) {
            for (IBond bond : frag.bonds()) {
                Assertions.assertFalse(originalAtoms.contains(bond.getBegin()),
                        "Bond.getBegin() in fragment must not reference an original atom.");
                Assertions.assertFalse(originalAtoms.contains(bond.getEnd()),
                        "Bond.getEnd() in fragment must not reference an original atom.");
            }
        }
    }

    /**
     * A molecule with a single atom: at any radius the fragment must contain
     * exactly 1 atom and 0 bonds.
     *
     * @throws CDKException if SMILES parsing fails
     */
    @Test
    void testSingleAtomMolecule() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smiPar.parseSmiles("[CH4]");
        CircularFragmenter fragmenter = new CircularFragmenter(2);
        List<IAtomContainer> fragments = fragmenter.getCircularFragments(mol);
        Assertions.assertEquals(1, fragments.size());
        Assertions.assertEquals(1, fragments.get(0).getAtomCount());
        Assertions.assertEquals(0, fragments.get(0).getBondCount());
    }

    /**
     * A diatomic molecule at radius 1: each fragment must contain both atoms
     * and the single bond between them.
     *
     * @throws CDKException if SMILES parsing fails
     */
    @Test
    void testDiatomicMolecule() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smiPar.parseSmiles("CC"); // ethane
        CircularFragmenter fragmenter = new CircularFragmenter(1);
        List<IAtomContainer> fragments = fragmenter.getCircularFragments(mol);
        Assertions.assertEquals(2, fragments.size());
        for (IAtomContainer frag : fragments) {
            Assertions.assertEquals(2, frag.getAtomCount());
            Assertions.assertEquals(1, frag.getBondCount());
        }
    }

    /**
     * {@link CircularFragmenter#getCircularFragment(IAtomContainer, int)} must
     * return the same result (atom count, bond count, property values) as the
     * corresponding entry in the list returned by
     * {@link CircularFragmenter#getCircularFragments(IAtomContainer)}.
     *
     * @throws CDKException if SMILES parsing fails
     */
    @Test
    void testGetCircularFragmentMatchesList() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smiPar.parseSmiles("CCCCC");
        CircularFragmenter fragmenter = new CircularFragmenter(2);
        List<IAtomContainer> allFragments = fragmenter.getCircularFragments(mol);

        for (int i = 0; i < mol.getAtomCount(); i++) {
            IAtomContainer single = fragmenter.getCircularFragment(mol, i);
            Assertions.assertEquals(allFragments.get(i).getAtomCount(), single.getAtomCount(),
                    "Atom count mismatch at index " + i);
            Assertions.assertEquals(allFragments.get(i).getBondCount(), single.getBondCount(),
                    "Bond count mismatch at index " + i);
        }
    }

    /**
     * The element symbols of atoms in the fragment must match those of the
     * corresponding atoms in the original molecule.
     *
     * @throws CDKException if SMILES parsing fails
     */
    @Test
    void testAtomSymbolsPreservedInFragment() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        // Ethanol: C, C, O
        IAtomContainer mol = smiPar.parseSmiles("CCO");
        CircularFragmenter fragmenter = new CircularFragmenter(2);

        // Fragment centered on the oxygen (last atom, index 2) at radius 2
        // must include all three atoms
        IAtomContainer fragFromO = fragmenter.getCircularFragment(mol, 2);
        Assertions.assertEquals(3, fragFromO.getAtomCount());

        Set<String> symbols = new HashSet<>();
        for (IAtom atom : fragFromO.atoms()) {
            symbols.add(atom.getSymbol());
        }
        Assertions.assertTrue(symbols.contains("C"), "Fragment must contain carbon atoms.");
        Assertions.assertTrue(symbols.contains("O"), "Fragment must contain an oxygen atom.");
    }

    /**
     * Bond orders of copied bonds must match those in the original molecule.
     *
     * @throws CDKException if SMILES parsing fails
     */
    @Test
    void testBondOrderPreservedInFragment() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        // Ethene: C=C (one double bond)
        IAtomContainer mol = smiPar.parseSmiles("C=C");
        CircularFragmenter fragmenter = new CircularFragmenter(1);
        List<IAtomContainer> fragments = fragmenter.getCircularFragments(mol);

        for (IAtomContainer frag : fragments) {
            Assertions.assertEquals(1, frag.getBondCount());
            Assertions.assertEquals(IBond.Order.DOUBLE, frag.getBond(0).getOrder(),
                    "Double bond order must be preserved in the fragment.");
        }
    }
}
