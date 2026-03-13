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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
     * @throws CDKException if SMILES parsing or generation fails
     */
    @Test
    void testFragmentCountEqualsAtomCount() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator(SmiFlavor.Canonical | SmiFlavor.UseAromaticSymbols);
        // Ethanol (3 heavy atoms)
        IAtomContainer mol = smiPar.parseSmiles("CCO");
        CircularFragmenter fragmenter = new CircularFragmenter(2);
        List<IAtomContainer> fragments = fragmenter.getCircularFragments(mol);
        Assertions.assertEquals(mol.getAtomCount(), fragments.size());
        for (IAtomContainer frag : fragments) {
            //at radius 2, all fragments should include the whole molecule and hence be the same
            Assertions.assertEquals("OCC", smiGen.create(frag));
        }
    }

    /**
     * At radius 0, every fragment must contain exactly 1 atom (the root/center) and 0 bonds.
     *
     * @throws CDKException if SMILES parsing or generation fails
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
     * @throws CDKException if SMILES parsing or generation fails
     */
    @Test
    void testLinearChainCenterAtomRadius1and2() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator(SmiFlavor.Canonical | SmiFlavor.UseAromaticSymbols);
        // CCCCC: atoms 0–4, middle atom is index 2
        IAtomContainer pentane = smiPar.parseSmiles("CCCCC");
        CircularFragmenter fragmenter = new CircularFragmenter(1);
        List<IAtomContainer> fragments = fragmenter.getCircularFragments(pentane);
        IAtomContainer centerFrag = fragments.get(2); // C3 is index 2
        Assertions.assertEquals(3, centerFrag.getAtomCount(),
                "Radius-1 fragment from chain center must contain 3 atoms.");
        Assertions.assertEquals(2, centerFrag.getBondCount(),
                "Radius-1 fragment from chain center must contain 2 bonds.");
        Assertions.assertEquals("CCC", smiGen.create(centerFrag));

        fragmenter.setRadius(2);
        fragments = fragmenter.getCircularFragments(pentane);
        centerFrag = fragments.get(2);
        Assertions.assertEquals(5, centerFrag.getAtomCount(),
                "Radius-2 fragment from chain center must contain all 5 atoms.");
        Assertions.assertEquals(4, centerFrag.getBondCount(),
                "Radius-2 fragment from chain center must contain 4 bonds.");
        Assertions.assertEquals("CCCCC", smiGen.create(centerFrag));
    }

    /**
     * For a linear pentane chain (C1–C2–C3–C4–C5), the fragment centered/rooted on C1
     * (index 0) should only contain C1 and C2 (two atoms, one bond).
     *
     * @throws CDKException if SMILES parsing or generation fails
     */
    @Test
    void testLinearChainTerminalAtomRadius1() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator(SmiFlavor.Canonical | SmiFlavor.UseAromaticSymbols);
        IAtomContainer pentane = smiPar.parseSmiles("CCCCC");
        CircularFragmenter fragmenter = new CircularFragmenter(1);
        List<IAtomContainer> fragments = fragmenter.getCircularFragments(pentane);
        IAtomContainer terminalFrag = fragments.get(0); // C1 is index 0
        Assertions.assertEquals(2, terminalFrag.getAtomCount(),
                "Radius-1 fragment from terminal atom must contain 2 atoms.");
        Assertions.assertEquals(1, terminalFrag.getBondCount(),
                "Radius-1 fragment from terminal atom must contain 1 bond.");
        Assertions.assertEquals("CC", smiGen.create(terminalFrag));
    }

    /**
     * For benzene at radius 1, the fragment centered on any atom includes that
     * atom and its two immediate ring neighbors (3 atoms, 2 bonds). It is also tested whether
     * aromaticity flags on atoms and bonds are preserved.
     *
     * @throws CDKException if SMILES parsing or generation fails
     */
    @Test
    void testBenzeneRadius1() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator(SmiFlavor.Canonical | SmiFlavor.UseAromaticSymbols);
        IAtomContainer benzene = smiPar.parseSmiles("c1ccccc1");
        CircularFragmenter fragmenter = new CircularFragmenter(1);
        List<IAtomContainer> fragments = fragmenter.getCircularFragments(benzene);
        for (int i = 0; i < fragments.size(); i++) {
            IAtomContainer frag = fragments.get(i);
            Assertions.assertEquals(3, frag.getAtomCount(),
                    "Radius-1 benzene fragment " + i + " must contain 3 atoms.");
            Assertions.assertEquals(2, frag.getBondCount(),
                    "Radius-1 benzene fragment " + i + " must contain 2 bonds.");
            for (IAtom atom : fragments.get(i).atoms()) {
                Assertions.assertTrue(atom.isAromatic(),
                        "Aromaticity flag must be preserved on atoms in fragment " + i + ".");
            }
            for (IBond bond : fragments.get(i).bonds()) {
                Assertions.assertTrue(bond.isAromatic(),
                        "Aromaticity flag must be preserved on bonds in fragment " + i + ".");
            }
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
     * @throws CDKException if SMILES parsing or generation fails
     */
    @Test
    void testBenzeneRadius2() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator(SmiFlavor.Canonical | SmiFlavor.UseAromaticSymbols);
        IAtomContainer benzene = smiPar.parseSmiles("c1ccccc1");
        CircularFragmenter fragmenter = new CircularFragmenter(2);
        List<IAtomContainer> fragments = fragmenter.getCircularFragments(benzene);
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
     * For benzene (6-membered ring) at radius 3, the fragment centered on any
     * atom must include all 6 atoms and all 6 bonds (the entire ring is
     * reachable within 3 bonds).
     *
     * @throws CDKException if SMILES parsing or generation fails
     */
    @Test
    void testBenzeneRadius3AllAtoms() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator(SmiFlavor.Canonical | SmiFlavor.UseAromaticSymbols);
        IAtomContainer benzene = smiPar.parseSmiles("c1ccccc1");
        CircularFragmenter fragmenter = new CircularFragmenter(3);
        List<IAtomContainer> fragments = fragmenter.getCircularFragments(benzene);
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
     * @throws CDKException if SMILES parsing or generation fails
     */
    @Test
    void testEthylbenzeneTerminalRadius2AtomAndBondCount() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator(SmiFlavor.Canonical | SmiFlavor.UseAromaticSymbols);
        IAtomContainer mol = smiPar.parseSmiles("CCc1ccccc1");
        CircularFragmenter fragmenter = new CircularFragmenter(2);
        List<IAtomContainer> fragments = fragmenter.getCircularFragments(mol);
        IAtomContainer frag = fragments.get(0);
        Assertions.assertEquals(3, frag.getAtomCount(),
                "Radius-2 fragment from terminal CH3 in ethylbenzene must contain 3 atoms.");
        Assertions.assertEquals(2, frag.getBondCount(),
                "Radius-2 fragment from terminal CH3 in ethylbenzene must contain 2 bonds.");
        Assertions.assertEquals("cCC", smiGen.create(frag));
    }

    /**
     * At a large enough radius (5), the whole molecule is captured.
     * For ethylbenzene (8 atoms, 8 bonds) radius 5 must give the full molecule because of the symmetry
     * of the ring.
     *
     * @throws CDKException if SMILES parsing or generation fails
     */
    @Test
    void testEthylbenzeneRadius5FullMolecule() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator(SmiFlavor.Canonical | SmiFlavor.UseAromaticSymbols);
        IAtomContainer mol = smiPar.parseSmiles("CCc1ccccc1");
        int atomCount = mol.getAtomCount();
        int bondCount = mol.getBondCount();
        CircularFragmenter fragmenter = new CircularFragmenter(5);
        List<IAtomContainer> fragments = fragmenter.getCircularFragments(mol);
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
     * @throws CDKException if SMILES parsing or generation fails
     */
    @Test
    void testFragmentAtomsAreDeepCopies() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smiPar.parseSmiles("CCO");
        // Collect all original atom references
        Set<IAtom> originalAtoms = new HashSet<>((int) (mol.getAtomCount() * 1.4));
        for (IAtom atom : mol.atoms()) {
            originalAtoms.add(atom);
        }
        CircularFragmenter fragmenter = new CircularFragmenter(2);
        List<IAtomContainer> fragments = fragmenter.getCircularFragments(mol);
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
     * @throws CDKException if SMILES parsing or generation fails
     */
    @Test
    void testFragmentBondsAreDeepCopies() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smiPar.parseSmiles("CCO");
        Set<IBond> originalBonds = new HashSet<>((int) (mol.getBondCount() * 1.4));
        for (IBond bond : mol.bonds()) {
            originalBonds.add(bond);
        }
        CircularFragmenter fragmenter = new CircularFragmenter(2);
        List<IAtomContainer> fragments = fragmenter.getCircularFragments(mol);
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
     * @throws CDKException if SMILES parsing or generation fails
     */
    @Test
    void testMutatingFragmentDoesNotAffectOriginal() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smiPar.parseSmiles("CCO");
        CircularFragmenter fragmenter = new CircularFragmenter(2);
        List<IAtomContainer> fragments = fragmenter.getCircularFragments(mol);
        // Mutate the first atom in the first fragment
        fragments.get(0).getAtom(0).setSymbol("N");
        for (IAtom originalAtom : mol.atoms()) {
            Assertions.assertNotEquals("N", originalAtom.getSymbol(),
                    "Modifying fragment atom symbol should not affect original molecule.");
        }
    }

    /**
     * The atoms stored in a copied bond (via {@code bond.getBegin()} /
     * {@code bond.getEnd()}) must be the copied atom instances inside the
     * fragment, not the originals.
     *
     * @throws CDKException if SMILES parsing or generation fails
     */
    @Test
    void testFragmentBondsReferenceCopiedAtoms() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smiPar.parseSmiles("CCO");
        // Collect all original atom references
        Set<IAtom> originalAtoms = new HashSet<>((int) (mol.getAtomCount() * 1.4));
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
     * Bond orders of copied bonds must match those in the original molecule.
     *
     * @throws CDKException if SMILES parsing or generation fails
     */
    @Test
    void testBondOrderPreservedInFragment() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator(SmiFlavor.Canonical | SmiFlavor.UseAromaticSymbols);
        // Ethene: C=C (one double bond)
        IAtomContainer mol = smiPar.parseSmiles("C=C");
        CircularFragmenter fragmenter = new CircularFragmenter(1);
        List<IAtomContainer> fragments = fragmenter.getCircularFragments(mol);
        for (IAtomContainer frag : fragments) {
            Assertions.assertEquals(1, frag.getBondCount());
            Assertions.assertEquals(IBond.Order.DOUBLE, frag.getBond(0).getOrder(),
                    "Double bond order must be preserved in the fragment.");
            Assertions.assertEquals("C=C", smiGen.create(frag));
        }
    }

    /**
     * Tests the ring extraction from cyclopentane with radius 2. The point is that the
     * bond completing the ring must be extracted as well, even though it is not part
     * of the radius anymore, technically.
     *
     * @throws CDKException if SMILES parsing or generation fails
     */
    @Test
    void testRingExtractionCyclopentane() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator(SmiFlavor.Canonical | SmiFlavor.UseAromaticSymbols);
        IAtomContainer cyclopentane = smiPar.parseSmiles("C1CCCC1");
        CircularFragmenter fragmenter = new CircularFragmenter(2);
        List<IAtomContainer> fragments = fragmenter.getCircularFragments(cyclopentane);
        for (IAtomContainer fragment : fragments) {
            Assertions.assertEquals("C1CCCC1", smiGen.create(fragment));
        }
    }

    /**
     * Both a positive and a negative formal charge must be preserved in a
     * single fragment of the glycine zwitterion, {@code [NH3+]CC([O-])=O}).
     *
     * <p>At radius 5, every fragment captures the whole five-atom molecule,
     * so both charged atoms ({@code NH3+} and {@code O−}) must be present.</p>
     *
     * @throws CDKException if SMILES parsing or generation fails
     */
    @Test
    void testChargePreservationGlycine() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator(SmiFlavor.Canonical | SmiFlavor.UseAromaticSymbols);
        // Glycine zwitterion: [NH3+]CC([O-])=O
        IAtomContainer zwitterion = smiPar.parseSmiles("[NH3+]CC([O-])=O");
        CircularFragmenter fragmenter = new CircularFragmenter(5);
        // At radius 5, every fragment contains the whole molecule
        List<IAtomContainer> fragments = fragmenter.getCircularFragments(zwitterion);
        for (IAtomContainer fragment : fragments) {
            Assertions.assertEquals("O=C([O-])C[NH3+]", smiGen.create(fragment));
        }
    }

    /**
     * When {@link CircularFragmenter#isPreserveStereo()} is {@code true},
     * stereo elements from the original molecule must be transferred to the
     * fragment.
     *
     * <p>L-alanine ({@code [C@@H](C(O)=O)(C)N}) has one tetrahedral stereo
     * element at its alpha carbon (index 0). At radius 5, the whole molecule is
     * included in the fragment, so the stereo element can be mapped without
     * missing any ligand atom. The resulting fragment must have at least one
     * stereo element.</p>
     *
     * @throws CDKException if SMILES parsing or generation fails
     */
    @Test
    void testStereoElementsPreservedWhenFlagTrue() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator(SmiFlavor.Canonical | SmiFlavor.UseAromaticSymbols | SmiFlavor.Stereo);
        // L-alanine: [C@@H] at index 0
        IAtomContainer alanine = smiPar.parseSmiles("[C@@H](C(O)=O)(C)N");
        CircularFragmenter fragmenter = new CircularFragmenter(5, true); // preserveStereo=true
        IAtomContainer frag = fragmenter.getCircularFragment(alanine, 0);
        Assertions.assertTrue(frag.stereoElements().iterator().hasNext(),
                "Stereo elements must be present in the fragment when preserveStereo=true.");
        Assertions.assertEquals("C[C@@H](C(=O)O)N", smiGen.create(frag));
    }

    /**
     * When {@link CircularFragmenter#isPreserveStereo()} is {@code false}
     * (the default), no stereo elements must appear in the fragment.
     * Uses the same molecule and center atom as {@link #testStereoElementsPreservedWhenFlagTrue}
     * to make the two tests directly comparable.
     *
     * @throws CDKException if SMILES parsing or generation fails
     */
    @Test
    void testStereoElementsAbsentWhenFlagFalse() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator(SmiFlavor.Canonical | SmiFlavor.UseAromaticSymbols | SmiFlavor.Stereo);
        IAtomContainer alanine = smiPar.parseSmiles("[C@@H](C(O)=O)(C)N");
        CircularFragmenter fragmenter = new CircularFragmenter(5, false); // preserveStereo=false (default)
        IAtomContainer frag = fragmenter.getCircularFragment(alanine, 0); // same center as the true-stereo test
        Assertions.assertFalse(frag.stereoElements().iterator().hasNext(),
                "Stereo elements must NOT be present in the fragment when preserveStereo=false.");
        Assertions.assertEquals("CC(C(=O)O)N", smiGen.create(frag));
    }

    /**
     * When {@link CircularFragmenter#isMarkAttachments()} is {@code true}, every broken bond at
     * the fragment boundary is replaced by a bond to an {@link org.openscience.cdk.interfaces.IPseudoAtom}
     * labelled {@code "*"}.
     * This test verifies the SMILES representation of all radius-1 fragments of alanine,
     * including correct pseudo-atom bond orders (single and double) and charge/element preservation.
     *
     * @throws CDKException if SMILES parsing or generation fails
     */
    @Test
    void testPseudoAtomSaturation() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator(SmiFlavor.Canonical | SmiFlavor.UseAromaticSymbols);
        IAtomContainer alanine = smiPar.parseSmiles("[C@@H](C(O)=O)(C)N");
        CircularFragmenter fragmenter = new CircularFragmenter(1, false, true);
        List<IAtomContainer> fragments = fragmenter.getCircularFragments(alanine);
        String[] expectedFragments = new String[] {
                "*C(=*)C(N)C",            // pseudo atom is double-bonded (mirrors the C=O of the carboxyl group)
                "*C(*)C(=O)O",
                "*C(=*)O",
                "*C(*)=O",
                "*C(*)C",
                "*C(*)N"
        };
        Assertions.assertEquals(expectedFragments.length, fragments.size());
        for (IAtomContainer frag : fragments) {
            Assertions.assertTrue(Arrays.stream(expectedFragments).anyMatch(expected -> {
                        try {
                            return expected.equals(smiGen.create(frag));
                        } catch (CDKException e) {
                            throw new AssertionError("Could not generate SMILES string for fragment.");
                        }
                    }),
                    "Fragment SMILES " + smiGen.create(frag) + " does not match any expected fragment.");
        }
    }

    /**
     * Tests the correct fragmentation of the aromatic L-tryptophan zwitter ion.
     *
     * @throws CDKException if SMILES parsing or generation fails
     */
    @Test
    void testLTryptophanZwitterIonRadius1() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator(SmiFlavor.Canonical | SmiFlavor.UseAromaticSymbols | SmiFlavor.Stereo);
        IAtomContainer tryptophan = smiPar.parseSmiles("[NH3+][C@@H](Cc1c[nH]c2ccccc12)C(=O)[O-]");
        CircularFragmenter fragmenter = new CircularFragmenter(1, true, false);
        List<IAtomContainer> fragments = fragmenter.getCircularFragments(tryptophan);
        String[] expectedFragments = new String[] {
                //correct preservation of charges
                "C[NH3+]",
                //this fragment correctly carries the stereo annotation
                "C[C@@H](C)[NH3+]",
                "cCC",
                "cc(c)C",
                "cc[nH2]",
                //note that this is only correct if both aromatic Cs have a single
                // bond to nitrogen, as it is the case in tryptophan
                "c[nH]c",
                "cc(c)[nH2]",
                "ccc",
                "ccc",
                "ccc",
                "ccc",
                "cc(c)c",
                "CC(=O)[O-]",
                "C=O",
                "C[O-]"
        };
        Assertions.assertEquals(expectedFragments.length, fragments.size());
        for (IAtomContainer frag : fragments) {
            Assertions.assertTrue(Arrays.stream(expectedFragments).anyMatch(expected -> {
                        try {
                            return expected.equals(smiGen.create(frag));
                        } catch (CDKException e) {
                            throw new AssertionError("Could not generate SMILES string for fragment.");
                        }
                    }),
                    "Fragment SMILES " + smiGen.create(frag) + " does not match any expected fragment.");
        }
    }

    /**
     * Tests the correct fragmentation of the aromatic L-tryptophan zwitterion at radius 2.
     * At radius 2, fragments span two bond shells from each center atom, capturing larger
     * substructures including partial ring systems and the stereo center with its full
     * immediate chemical environment.
     *
     * @throws CDKException if SMILES parsing or generation fails
     */
    @Test
    void testLTryptophanZwitterIonRadius2() throws CDKException {
        SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator smiGen = new SmilesGenerator(SmiFlavor.Canonical | SmiFlavor.UseAromaticSymbols | SmiFlavor.Stereo);
        IAtomContainer tryptophan = smiPar.parseSmiles("[NH3+][C@@H](Cc1c[nH]c2ccccc12)C(=O)[O-]");
        CircularFragmenter fragmenter = new CircularFragmenter(2, true, false);
        List<IAtomContainer> fragments = fragmenter.getCircularFragments(tryptophan);
        String[] expectedFragments = new String[] {
                "C[C@@H](C)[NH3+]",
                "cC[C@@H](C(=O)[O-])[NH3+]",
                "cc(c)C[C@@H](C)[NH3+]",
                //note that this fragment and some other below cannot be kekulized because of the explicit H on n
                "CCc1c[nH]cc1c",
                "Cc1cc[nH]c1",
                "cc1ccc[nH]1",
                "ccc1c(c)cc[nH]1",
                "cccc(c)[nH2]",
                "ccccc",
                "ccccc",
                "cccc(c)c",
                "ccc1c(C)c[nH]c1c",
                "C[C@@H](C(=O)[O-])[NH3+]",
                "CC(=O)[O-]",
                "CC(=O)[O-]"
        };
        Assertions.assertEquals(expectedFragments.length, fragments.size());
        for (IAtomContainer frag : fragments) {
            Assertions.assertTrue(Arrays.stream(expectedFragments).anyMatch(expected -> {
                        try {
                            return expected.equals(smiGen.create(frag));
                        } catch (CDKException e) {
                            throw new AssertionError("Could not generate SMILES string for fragment.");
                        }
                    }),
                    "Fragment SMILES " + smiGen.create(frag) + " does not match any expected fragment.");
        }
    }
}
