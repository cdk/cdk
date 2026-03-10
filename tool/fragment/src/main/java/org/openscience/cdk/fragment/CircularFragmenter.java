/*
 * Copyright (c) 2026 Jonas Schaub <jonas.schaub@uni-jena.de>
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

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

//TODO wording: always use center atom and radius
//TODO: check SDU for other necessary funcationalities
/**
 * Extracts atom-centered circular / spherical fragments from a molecule,
 * analogous to HOSE codes, circular Morgan-type
 * fingerprints, and Molecular Signatures.
 *
 * <p>For every atom in the input molecule, the neighbourhood up to a
 * user-defined radius (number of bonds, also called "height" or "level") is
 * collected by a breadth-first expansion and returned as an independent
 * {@link IAtomContainer}. All atoms and bonds in the resulting containers are
 * <em>deep copies</em> of the originals, so modifying them does not
 * affect the source molecule.</p>
 *
 * <p><b>Usage example:</b>
 * <pre>{@code
 * IAtomContainer molecule = ...; // fully configured molecule
 * CircularFragmenter fragmenter = new CircularFragmenter(3); //radius 3
 * List<IAtomContainer> fragments = fragmenter.getCircularFragments(molecule);
 * }</pre></p>
 *
 * <p>The list index of each fragment corresponds to the index of the center atom (also called
 * "root") in the original atom container. But note that <code>fragments.get(i).contains(mol.getAtom(i))</code>
 * will produce <code>false</code> because the atoms (and bonds) are copied at fragment extraction.</p>
 *
 * <p>Note that the resulting fragments are not deduplicated! So, if you, e.g., fragment benzene
 * with a radius of 3, you will get six benzene "fragments" as a result, since a radius of three
 * includes the entire molecule, independent of which atom is taken as the center.</p>
 *
 * @author Jonas Schaub (jonas.schaub@uni-jena.de | jonas-schaub@gmx.de | <a href="https://github.com/JonasSchaub">JonasSchaub on GitHub</a>)
 * @author Claude Sonnet 4.6
 * @cdk.keyword fragment
 * @cdk.keyword circular fingerprint
 * @cdk.keyword HOSE code
 * @cdk.keyword molecular signature
 * @cdk.keyword spherical environment
 */
public class CircularFragmenter {
    /**
     * Default radius used when no explicit value is given (= 3 bonds).
     */
    public static final int DEFAULT_RADIUS = 3;

    /**
     * Radius of the circular neighbourhood to extract (number of bonds).
     */
    private int radius;

    /**
     * Creates a new {@code CircularFragmenter} with the {@link #DEFAULT_RADIUS}
     * (= {@value #DEFAULT_RADIUS}).
     */
    public CircularFragmenter() {
        this(CircularFragmenter.DEFAULT_RADIUS);
    }

    /**
     * Creates a new {@code CircularFragmenter} with the given radius.
     *
     * @param radius the number of bonds to expand from each center atom;
     *               must be >= 0; a radius of 0 produces fragments
     *               containing only the respective center atom itself
     * @throws IllegalArgumentException if {@code radius} is negative
     */
    public CircularFragmenter(int radius) {
        if (radius < 0) {
            throw new IllegalArgumentException("Radius must be >= 0, got: " + radius);
        }
        this.radius = radius;
    }

    /**
     * Returns the current radius setting for atom environment extraction.
     *
     * @return radius in nr. of bonds
     */
    public int getRadius() {
        return this.radius;
    }

    /**
     * Sets the radius for atom environment extraction.
     *
     * @param radius in nr. of bonds; must be >= 0
     * @throws IllegalArgumentException if {@code radius} is negative
     */
    public void setRadius(int radius) {
        if (radius < 0) {
            throw new IllegalArgumentException("Radius must be >= 0, got: " + radius);
        }
        this.radius = radius;
    }

    /**
     * Extracts one circular fragment per atom of the input molecule.
     *
     * <p>The fragment for atom {@code i} contains deep copies of all atoms
     * reachable from atom {@code i} within at most {@link #getRadius()} bonds,
     * together with all bonds between those atoms.</p>
     *
     * <p>The list index of each fragment corresponds to the index of the center atom
     * in the input atom container.</p>
     *
     * <p>Note that the resulting fragments are not deduplicated!</p>
     *
     * @param molecule the input molecule; must not be {@code null}; an empty input
     *                 molecule yields an empty return list; the method does not
     *                 modify the molecule
     * @return a list of {@link IAtomContainer} objects, one per atom in
     *         {@code molecule}, in atom-index order; never {@code null} but can be
     *         empty if the input molecule is empty
     * @throws NullPointerException if {@code molecule} is {@code null}
     */
    public List<IAtomContainer> getCircularFragments(IAtomContainer molecule) {
        Objects.requireNonNull(molecule, "Input molecule must not be null.");

        int atomCount = molecule.getAtomCount();
        List<IAtomContainer> fragments = new ArrayList<>(atomCount);

        if (atomCount == 0) {
            return fragments;
        }

        IChemObjectBuilder builder = molecule.getBuilder();
        int tmpRadius = this.radius;
        int initCollectionSize = CircularFragmenter.calculateInitCollectionSize(this.radius, molecule.getAtomCount());

        for (int centerIdx = 0; centerIdx < atomCount; centerIdx++) {
            IAtomContainer fragment = CircularFragmenter.extractFragment(molecule, centerIdx, builder, tmpRadius, initCollectionSize);
            fragments.add(centerIdx, fragment);
        }

        return fragments;
    }

    /**
     * Extracts a single circular fragment centered on the atom at
     * {@code centerIdx} in {@code molecule}.
     *
     * <p>The same algorithm as in
     * {@link #getCircularFragments(IAtomContainer)} is applied, but for only
     * one center atom.</p>
     *
     * @param molecule  the source molecule; must not be {@code null}
     * @param centerIdx zero-based index of the center atom in {@code molecule}
     * @return a deep-copied {@link IAtomContainer} of the circular environment
     * @throws NullPointerException      if {@code molecule} is {@code null}
     * @throws IndexOutOfBoundsException if {@code centerIdx} is out of range (< 0 or >= atom count of molecule)
     */
    public IAtomContainer getCircularFragment(IAtomContainer molecule, int centerIdx) {
        Objects.requireNonNull(molecule, "Input molecule must not be null.");
        if (centerIdx < 0 || centerIdx >= molecule.getAtomCount()) {
            throw new IndexOutOfBoundsException(
                    "centerIdx " + centerIdx + " is out of range [0, " + molecule.getAtomCount() + ").");
        }
        IChemObjectBuilder builder = molecule.getBuilder();
        int initCollectionSize = CircularFragmenter.calculateInitCollectionSize(this.radius, molecule.getAtomCount());
        return CircularFragmenter.extractFragment(molecule, centerIdx, builder, this.radius, initCollectionSize);
    }

    /**
     * Utility function to estimate the necessary initial collection size to possibly avoid resizing and rehashing.
     *
     * <p>No parameter checks are performed here!</p>
     *
     * @param radius in nr. of bonds
     * @return initial collection size = (1 + 4 * (3^(radius - 1) + 1)) / 2 or the atom count if it is smaller than the result
     */
    protected static int calculateInitCollectionSize(int radius, int atomCount) {
        // radius = 0, initCollectionSize = 1 (-> the center atom)
        int initCollectionSize = 1;
        if (radius == 1) {
            // radius = 1, initCollectionSize = 5 (-> the center atom, presumably carbon, plus up to 4 neighbors)
            initCollectionSize += 4;
        } else if (radius > 1) {
            // radius >= 2, initCollectionSize = 1 + 4 * (3^(radius - 1) + 1)
            // (-> presuming all are carbon atoms and each has 4 neighbors, i.e. 3 more atoms in each
            // iteration per atom in the last sphere)
            initCollectionSize +=  (4 * ((int)Math.pow(3, (double) radius - 1) + 1));
        }
        //for most small molecules, there will be rings and implicit hydrogens that minimize the required collection space
        initCollectionSize = (int) Math.ceil((double) initCollectionSize / 2);
        return Math.min(initCollectionSize, atomCount);
    }

    //TODO: can this be optimized? ask Gemini
    /**
     * Core BFS-based fragment extraction.
     *
     * <p>Starting from the center atom, the algorithm performs a
     * breadth-first expansion up to {@link #radius} bonds. It collects the
     * set of atoms and all bonds between collected atoms, then builds
     * a new {@link IAtomContainer} from deep copies of those atoms and bonds.</p>
     *
     * <p>No parameter checks are performed, they must be conducted by the calling code!</p>
     *
     * @param molecule  source molecule
     * @param centerIdx index of the center atom
     * @param builder   {@link IChemObjectBuilder} obtained from the molecule
     *                  or the builder that should be used to construct the fragment
     * @param radius    in nr. of bonds; must be >= 0
     * @return fragment container (deep copy)
     */
    protected static IAtomContainer extractFragment(
            IAtomContainer molecule,
            int centerIdx,
            IChemObjectBuilder builder,
            int radius,
            int initCollectionSize) {

        // --- 1. BFS to collect atoms within radius bonds ---

        // Tracks which atom indices have been visited
        Set<Integer> visitedIndices = new HashSet<>((int) Math.ceil(initCollectionSize * 1.4));
        // Stores collected atoms in BFS order (first entry is always the center atom)
        List<IAtom> collectedAtoms = new ArrayList<>(initCollectionSize);
        //note visitedIndices and collectedAtoms will contain the same atoms but the lookup is faster in the hash set;
        // that is why both exist in parallel

        // BFS queue entries, [atomIndex, depth]
        Deque<int[]> queue = new ArrayDeque<>(initCollectionSize);

        IAtom centerAtom = molecule.getAtom(centerIdx);
        visitedIndices.add(centerIdx);
        collectedAtoms.add(centerAtom);
        queue.add(new int[]{centerIdx, 0});

        while (!queue.isEmpty()) {
            int[] entry = queue.poll();
            int currentIdx = entry[0];
            int currentDepth = entry[1];

            if (currentDepth >= radius) {
                // Do not expand further, but the atom itself is already collected
                continue;
            }

            IAtom currentAtom = molecule.getAtom(currentIdx);

            // Iterate over all bonds of the current atom
            for (IBond bond : currentAtom.bonds()) {
                IAtom neighbor = bond.getOther(currentAtom);
                int neighborIdx = molecule.indexOf(neighbor);
                if (neighborIdx < 0) {
                    // Safety guard: atom not in this molecule
                    continue;
                }
                if (!visitedIndices.contains(neighborIdx)) {
                    visitedIndices.add(neighborIdx);
                    collectedAtoms.add(neighbor);
                    queue.add(new int[]{neighborIdx, currentDepth + 1});
                }
            }
        }

        // --- 2. Collect all bonds whose both endpoints are in the fragment ---

        // Map from original atom -> copied atom (built in step 3)
        // We collect the bonds first by checking the index set.
        List<IBond> collectedBonds = new ArrayList<>(initCollectionSize);
        for (IBond bond : molecule.bonds()) {
            int idx0 = molecule.indexOf(bond.getBegin());
            int idx1 = molecule.indexOf(bond.getEnd());
            if (visitedIndices.contains(idx0) && visitedIndices.contains(idx1)) {
                collectedBonds.add(bond);
            }
        }

        // --- 3. Deep-copy atoms and bonds into a new container ---

        IAtomContainer fragment = builder.newAtomContainer();

        // Map: original IAtom reference -> copied IAtom in the fragment
        Map<IAtom, IAtom> originalAtomToCopyAtomMap = new HashMap<>((int) Math.ceil(collectedAtoms.size() * 1.4));

        for (IAtom origAtom : collectedAtoms) {
            try {
                IAtom copiedAtom = origAtom.clone();
                originalAtomToCopyAtomMap.put(origAtom, copiedAtom);
                fragment.addAtom(copiedAtom);
            } catch (CloneNotSupportedException e) {
                throw new IllegalArgumentException(
                        "Atom at index " + molecule.indexOf(origAtom) + " does not support cloning.", e);
            }
        }

        for (IBond origBond : collectedBonds) {
            try {
                IBond copiedBond = origBond.clone();
                // Re-wire the copied bond to the copied atom instances
                IAtom copiedBegin = originalAtomToCopyAtomMap.get(origBond.getBegin());
                IAtom copiedEnd = originalAtomToCopyAtomMap.get(origBond.getEnd());
                copiedBond.setAtoms(new IAtom[]{copiedBegin, copiedEnd});
                fragment.addBond(copiedBond);
            } catch (CloneNotSupportedException e) {
                throw new IllegalArgumentException(
                        "A bond in the molecule does not support cloning.", e);
            }
        }

        return fragment;
    }
}
