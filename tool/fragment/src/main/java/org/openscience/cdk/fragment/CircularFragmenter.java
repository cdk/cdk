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
 * TODO: remove the two properties?
 * <p>The property key {@link #FRAGMENT_CENTER_IDX_PROPERTY} is set on each
 * fragment container and holds the zero-based index of the atom that was used
 * as center of that fragment in the original molecule. Similarly, the
 * property key {@link #FRAGMENT_RADIUS_PROPERTY} records the radius with which
 * the fragment was generated.</p>
 *
 * <h2>Usage example</h2>
 * <pre>{@code
 * IAtomContainer molecule = ...; // fully configured molecule
 * CircularFragmenter fragmenter = new CircularFragmenter(3); //radius 3
 * List<IAtomContainer> fragments = fragmenter.getCircularFragments(molecule);
 * }</pre>
 *
 * <p>Note that the resulting fragments are not deduplicated!</p>
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
     * Property key set on every generated fragment container.
     * The value is the zero-based index of the center atom in the original
     * molecule.
     */
    public static final String FRAGMENT_CENTER_IDX_PROPERTY = "CircularFragmenter.centerAtomIdx";

    /**
     * Property key set on every generated fragment container.
     * The value is the radius (in bonds) used to generate the fragment.
     */
    public static final String FRAGMENT_RADIUS_PROPERTY = "CircularFragmenter.radius";

    /** Default radius used when no explicit value is given. */
    public static final int DEFAULT_RADIUS = 3;

    /** Radius of the circular neighbourhood to extract (number of bonds). */
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
     *               must be >= 0. A radius of 0 produces a fragment
     *               containing only the center atom itself.
     * @throws IllegalArgumentException if {@code radius} is negative
     */
    public CircularFragmenter(int radius) {
        if (radius < 0)
            throw new IllegalArgumentException("Radius must be >= 0, got: " + radius);
        this.radius = radius;
    }

    /**
     * Returns the current radius setting.
     *
     * @return radius in bonds
     */
    public int getRadius() {
        return this.radius;
    }

    /**
     * Sets the radius to use for subsequent calls to
     * {@link #getCircularFragments(IAtomContainer)}.
     *
     * @param radius must be >= 0
     * @throws IllegalArgumentException if {@code radius} is negative
     */
    public void setRadius(int radius) {
        if (radius < 0)
            throw new IllegalArgumentException("Radius must be >= 0, got: " + radius);
        this.radius = radius;
    }

    /**
     * Extracts one circular fragment per atom of the input molecule.
     *
     * <p>The fragment for atom {@code i} contains deep copies of all atoms
     * reachable from atom {@code i} within at most {@link #getRadius()} bonds,
     * together with all bonds between those atoms.</p>
     *
     * <p>Each returned container carries two properties:</p>
     * <ul>
     *   <li>{@link #FRAGMENT_CENTER_IDX_PROPERTY} – the index of the center
     *       atom in the original molecule</li>
     *   <li>{@link #FRAGMENT_RADIUS_PROPERTY} – the radius used</li>
     * </ul>
     *
     * <p>Note that the resulting fragments are not deduplicated!</p>
     *
     * @param molecule the input molecule; must not be {@code null} and should
     *                 have atoms already added to it. The method does not
     *                 modify the molecule.
     * @return a list of {@link IAtomContainer} objects, one per atom in
     *         {@code molecule}, in atom-index order; never {@code null}
     * @throws NullPointerException if {@code molecule} is {@code null}
     * @throws IllegalArgumentException if the molecule's atoms or bonds cannot
     *                 be cloned
     */
    public List<IAtomContainer> getCircularFragments(IAtomContainer molecule) {
        Objects.requireNonNull(molecule, "Input molecule must not be null.");

        int atomCount = molecule.getAtomCount();
        List<IAtomContainer> fragments = new ArrayList<>(atomCount);

        if (atomCount == 0) {
            return fragments;
        }

        IChemObjectBuilder builder = molecule.getBuilder();

        for (int centerIdx = 0; centerIdx < atomCount; centerIdx++) {
            IAtomContainer fragment = this.extractFragment(molecule, centerIdx, builder);
            fragments.add(fragment);
        }

        return fragments;
    }

    /**
     * Extracts a single circular fragment centered on the atom at
     * {@code centerIdx} in {@code molecule}.
     *
     * <p>The same algorithm as in
     * {@link #getCircularFragments(IAtomContainer)} is applied, but for only
     * one center atom. This is useful when only a subset of fragments is
     * needed.</p>
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
        return this.extractFragment(molecule, centerIdx, builder);
    }

    /**
     * Core BFS-based fragment extraction.
     *
     * <p>Starting from the center atom, the algorithm performs a
     * breadth-first expansion up to {@link #radius} bonds. It collects the
     * set of atoms and all bonds <em>between collected atoms</em>, then builds
     * a new {@link IAtomContainer} from deep copies of those atoms and bonds.</p>
     *
     * @param molecule  source molecule
     * @param centerIdx index of the center atom
     * @param builder   {@link IChemObjectBuilder} obtained from the molecule
     * @return fragment container (deep copy)
     */
    private IAtomContainer extractFragment(IAtomContainer molecule, int centerIdx, IChemObjectBuilder builder) {

        // --- 1. BFS to collect atoms within radius bonds ---

        // Tracks which atom indices have been visited
        Set<Integer> visitedIndices = new HashSet<>();
        // Stores collected atoms in BFS order (first entry is always the center)
        List<IAtom> collectedAtoms = new ArrayList<>();

        // BFS queue entries: (atom, current depth)
        Deque<int[]> queue = new ArrayDeque<>(); // [atomIndex, depth]

        IAtom centerAtom = molecule.getAtom(centerIdx);
        visitedIndices.add(centerIdx);
        collectedAtoms.add(centerAtom);
        queue.add(new int[]{centerIdx, 0});

        while (!queue.isEmpty()) {
            int[] entry = queue.poll();
            int currentIdx = entry[0];
            int currentDepth = entry[1];

            if (currentDepth >= this.radius) {
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

        // Map from original atom -> cloned atom (built in step 3)
        // We collect the bonds first by checking the index set.
        List<IBond> collectedBonds = new ArrayList<>();
        for (IBond bond : molecule.bonds()) {
            int idx0 = molecule.indexOf(bond.getBegin());
            int idx1 = molecule.indexOf(bond.getEnd());
            if (visitedIndices.contains(idx0) && visitedIndices.contains(idx1)) {
                collectedBonds.add(bond);
            }
        }

        // --- 3. Deep-copy atoms and bonds into a new container ---

        IAtomContainer fragment = builder.newAtomContainer();

        // Map: original IAtom reference -> cloned IAtom in the fragment
        Map<IAtom, IAtom> atomMap = new HashMap<>(collectedAtoms.size() * 2);

        for (IAtom origAtom : collectedAtoms) {
            try {
                IAtom clonedAtom = origAtom.clone();
                atomMap.put(origAtom, clonedAtom);
                fragment.addAtom(clonedAtom);
            } catch (CloneNotSupportedException e) {
                throw new IllegalArgumentException(
                        "Atom at index " + molecule.indexOf(origAtom) + " does not support cloning.", e);
            }
        }

        for (IBond origBond : collectedBonds) {
            try {
                IBond clonedBond = origBond.clone();
                // Re-wire the cloned bond to the cloned atom instances
                IAtom clonedBegin = atomMap.get(origBond.getBegin());
                IAtom clonedEnd = atomMap.get(origBond.getEnd());
                clonedBond.setAtoms(new IAtom[]{clonedBegin, clonedEnd});
                fragment.addBond(clonedBond);
            } catch (CloneNotSupportedException e) {
                throw new IllegalArgumentException(
                        "A bond in the molecule does not support cloning.", e);
            }
        }

        // --- 4. Annotate the fragment with provenance properties ---

        fragment.setProperty(FRAGMENT_CENTER_IDX_PROPERTY, centerIdx);
        fragment.setProperty(FRAGMENT_RADIUS_PROPERTY, this.radius);

        return fragment;
    }
}
