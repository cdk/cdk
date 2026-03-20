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

import org.openscience.cdk.Bond;
import org.openscience.cdk.exception.NoSuchAtomException;
import org.openscience.cdk.exception.NoSuchBondException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.interfaces.IStereoElement;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

//TODO: add note on prior aromaticity detection
//TODO: add test for previous CDK HOSE code generator bug
/**
 * Extracts atom-centered circular / spherical fragments from a molecule,
 * analogous to HOSE codes, circular Morgan-type
 * fingerprints, and Molecular Signatures.
 *
 * <p>For every atom in the input molecule, the neighborhood up to a
 * user-defined radius (number of bonds, also called "height" or "level") is
 * collected by a breadth-first expansion and returned as an independent
 * {@link IAtomContainer}. All atoms and bonds in the resulting containers are
 * <em>deep copies</em> of the originals, so modifying them does not
 * affect the source molecule.</p>
 *
 * <p><b>Usage example:</b></p>
 * <pre>{@code
 * IAtomContainer molecule = ...; // fully configured molecule
 * CircularFragmenter fragmenter = new CircularFragmenter(3); //radius 3, also the default
 * List<IAtomContainer> fragments = fragmenter.getCircularFragments(molecule);
 * }</pre>
 *
 * <p>The list index of each fragment corresponds to the index of the center atom (also called
 * "root") in the original atom container. But note that <code>fragments.get(i).contains(mol.getAtom(i))</code>
 * will produce <code>false</code> because the atoms (and bonds) are copied at fragment extraction.</p>
 *
 * <p>Each atom in a fragment has the property with key {@link CircularFragmenter#FRAGMENT_ATOM_DEPTH_PROPERTY_KEY}
 * set, which contains the depth (sphere nr. / level / height) of the respective atom in the respective fragment.
 * 0 stands for the center atom. These can, e.g., be used to generate SMILES strings of a fragment with atom-atom-mappings
 * corresponding to the respective depth of each atom.</p>
 *
 * <p>Additional configuration options (to the radius) include the saturation of the fragments where bonds were broken
 * (either, per default, with implicit hydrogen atoms or, alternatively, with pseudo atoms to mark the attachment points)
 * and whether stereochemistry annotations should be preserved in the fragments (default: false).</p>
 *
 * <p>Note that the resulting fragments are not deduplicated! So, if you, e.g., fragment benzene
 * with a radius of 3, you will get six benzene "fragments" as a result, since a radius of three
 * includes the entire molecule, independent of which atom is taken as the center.</p>
 *
 * <p>Scaling: Let <em>n</em> be the number of atoms and <em>r</em> the radius.
 * A single fragment extraction scales with the number of atoms <em>k</em> in the fragment
 * (approx. min(<em>n</em>, 3<sup><em>r</em></sup>)).
 * Extracting all fragments takes <em>O</em>(<em>n</em>&middot;<em>k</em>).
 * For typical small radii, this is effectively linear <em>O</em>(<em>n</em>);
 * for large radii covering the whole molecule, it is <em>O</em>(<em>n</em><sup>2</sup>).</p>
 *
 * @author Jonas Schaub (jonas.schaub@uni-jena.de | jonas-schaub@gmx.de | <a href="https://github.com/JonasSchaub">JonasSchaub on GitHub</a>)
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
    private static final int DEFAULT_RADIUS = 3;

    /**
     * Default value for whether to preserve stereochemistry annotations during fragmentation (= false).
     */
    private static final boolean DEFAULT_PRESERVE_STEREO = false;

    /**
     * Default value for whether to mark attachment points of broken bonds with pseudo atoms (= false).
     */
    private static final boolean DEFAULT_MARK_ATTACHMENTS = false;

    /**
     * Property key to retrieve the depth (sphere nr. / level / height) of a fragment atom in the
     * respective circular fragment. 0 stands for the center atom.
     */
    public static final String FRAGMENT_ATOM_DEPTH_PROPERTY_KEY = "CircularFragmenter:atomDepth";

    /**
     * Radius of the circular neighborhood to extract (number of bonds).
     */
    private int radius;

    /**
     * Whether to preserve stereochemistry annotations during fragmentation.
     */
    private boolean preserveStereo;

    /**
     * Whether to mark attachment points of broken bonds with pseudo atoms.
     */
    private boolean markAttachments;

    /**
     * Creates a new {@code CircularFragmenter} with the default radius
     * (= 3 bonds), the default stereochemistry setting (= false, no preservation of stereochemistry),
     * and the default attachment point marking setting (= false, no marking of attachment points;
     * saturation with implicit hydrogen atoms instead).
     */
    public CircularFragmenter() {
        this(CircularFragmenter.DEFAULT_RADIUS, CircularFragmenter.DEFAULT_PRESERVE_STEREO, CircularFragmenter.DEFAULT_MARK_ATTACHMENTS);
    }

    /**
     * Creates a new {@code CircularFragmenter} with the given radius,
     * the default stereochemistry setting (= false, no preservation of stereochemistry),
     * and the default attachment point marking setting (= false, no marking of attachment points;
     * saturation with implicit hydrogen atoms instead).
     *
     * @param radius the number of bonds to expand from each center atom;
     *               must be >= 0; a radius of 0 produces fragments
     *               containing only the respective center atom itself
     * @throws IllegalArgumentException if {@code radius} is negative
     */
    public CircularFragmenter(int radius) {
        this(radius, CircularFragmenter.DEFAULT_PRESERVE_STEREO, CircularFragmenter.DEFAULT_MARK_ATTACHMENTS);
    }

    /**
     * Creates a new {@code CircularFragmenter} with the given radius and stereochemistry setting
     * and the default attachment point marking setting (= false, no marking of attachment points;
     * saturation with implicit hydrogen atoms instead).
     *
     * @param radius the number of bonds to expand from each center atom;
     *               must be >= 0; a radius of 0 produces fragments
     *               containing only the respective center atom itself
     * @param preserveStereo whether to preserve stereochemistry annotations during fragmentation
     * @throws IllegalArgumentException if {@code radius} is negative
     */
    public CircularFragmenter(int radius, boolean preserveStereo) {
        this(radius, preserveStereo, CircularFragmenter.DEFAULT_MARK_ATTACHMENTS);
    }

    /**
     * Creates a new {@code CircularFragmenter} with the given radius, stereochemistry,
     * and attachment point marking setting.
     *
     * @param radius the number of bonds to expand from each center atom;
     *               must be >= 0; a radius of 0 produces fragments
     *               containing only the respective center atom itself
     * @param preserveStereo whether to preserve stereochemistry annotations during fragmentation
     * @param markAttachments whether to mark attachment points of broken bonds with pseudo atoms
     * @throws IllegalArgumentException if {@code radius} is negative
     */
    public CircularFragmenter(int radius, boolean preserveStereo, boolean markAttachments) {
        if (radius < 0) {
            throw new IllegalArgumentException("Radius must be >= 0, got: " + radius);
        }
        this.radius = radius;
        this.preserveStereo = preserveStereo;
        this.markAttachments = markAttachments;
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
     * Returns the current stereochemistry setting.
     *
     * @return whether to preserve stereochemistry annotations during fragmentation
     */
    public boolean isPreserveStereo() {
        return this.preserveStereo;
    }

    /**
     * Sets the stereochemistry setting.
     *
     * @param preserveStereo whether to preserve stereochemistry annotations during fragmentation
     */
    public void setPreserveStereo(boolean preserveStereo) {
        this.preserveStereo = preserveStereo;
    }

    /**
     * Returns the current attachment point marking setting.
     *
     * @return whether to mark attachment points of broken bonds with pseudo atoms
     */
    public boolean isMarkAttachments() {
        return this.markAttachments;
    }

    /**
     * Sets the attachment point marking setting.
     *
     * @param markAttachments whether to mark attachment points of broken bonds with pseudo atoms
     */
    public void setMarkAttachments(boolean markAttachments) {
        this.markAttachments = markAttachments;
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

        int tmpRadius = this.radius;
        boolean tmpPreserveStereo = this.preserveStereo;
        boolean tmpMarkAttachments = this.markAttachments;
        int initCollectionSize = CircularFragmenter.calculateInitCollectionSize(this.radius, atomCount);

        for (int centerIdx = 0; centerIdx < atomCount; centerIdx++) {
            IAtomContainer fragment = this.extractFragment(molecule, centerIdx, tmpRadius, initCollectionSize, tmpPreserveStereo, tmpMarkAttachments);
            fragments.add(centerIdx, fragment);
        }

        return fragments;
    }

    /**
     * Extracts a single circular fragment centered on the given atom from the molecule the atom is a part of.
     *
     * <p>The same algorithm as in
     * {@link #getCircularFragments(IAtomContainer)} is applied, but for only
     * one center atom.</p>
     *
     * <p>This method will only work if the atom has been accessed
     * in the context of an {@link IAtomContainer}, for example:
     *
     * <pre>{@code
     * IAtomContainer mol  = new AtomContainer();
     * IAtom          atom = new Atom(6);
     *
     * atom.getContainer(); // null
     * mol.add(atom);
     * atom.getContainer(); // still null
     * mol.getAtom(0).getContainer(); // not-null, returns 'mol'
     * }</pre></p>
     *
     * @param atom the center atom; must not be {@code null} and must be accessed in the context of an {@link IAtomContainer}
     * @return a deep-copied {@link IAtomContainer} of the circular environment
     * @throws NullPointerException      if {@code atom} is {@code null}
     * @throws IllegalArgumentException  if {@code atom} is not accessed in the context of an {@link IAtomContainer}
     */
    public IAtomContainer getCircularFragment(IAtom atom) {
        IAtomContainer molecule = atom.getContainer();
        if (molecule == null) {
            throw new IllegalArgumentException(
                    "Given atom is not part of a molecule or was not accessed correctly!");
        }
        return this.getCircularFragment(molecule, atom);
    }

    /**
     * Extracts a single circular fragment centered on the given atom from the given molecule.
     *
     * <p>The same algorithm as in
     * {@link #getCircularFragments(IAtomContainer)} is applied, but for only
     * one center atom.</p>
     *
     * @param molecule  the source molecule; must not be {@code null}
     * @param atom      the center atom; must not be {@code null} and must be part of {@code molecule}
     * @return a deep-copied {@link IAtomContainer} of the circular environment
     * @throws NullPointerException      if {@code molecule} or {@code atom} is {@code null}
     * @throws IllegalArgumentException  if {@code atom } is not part of {@code molecule}
     */
    public IAtomContainer getCircularFragment(IAtomContainer molecule, IAtom atom) {
        Objects.requireNonNull(molecule, "Input molecule must not be null.");
        Objects.requireNonNull(atom, "Input atom must not be null.");

        if (!molecule.contains(atom)) {
            throw new IllegalArgumentException(
                    "Given atom is not part of the given molecule!");
        }
        
        int initCollectionSize = CircularFragmenter.calculateInitCollectionSize(this.radius, molecule.getAtomCount());
        int tmpRadius = this.radius;
        boolean tmpPreserveStereo = this.preserveStereo;
        boolean tmpMarkAttachments = this.markAttachments;
        return this.extractFragment(molecule, atom.getIndex(), tmpRadius, initCollectionSize, tmpPreserveStereo, tmpMarkAttachments);
    }

    /**
     * Utility function to estimate the necessary initial collection size to possibly avoid resizing and rehashing.
     *
     * <p>No parameter checks are performed here!</p>
     *
     * @param radius in nr. of bonds
     * @param atomCount total number of atoms in the molecule
     * @return initial collection size = (1 + 4 * (3^(radius - 1) + 1)) / 2 or the atom count if it is smaller than the result
     */
    private static int calculateInitCollectionSize(int radius, int atomCount) {
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
     * @param radius    in nr. of bonds; must be >= 0
     * @param initCollectionSize estimated number of atoms in the fragment for collection sizing
     * @param preserveStereo whether to preserve stereochemistry annotations during fragmentation
     * @param markAttachments whether to mark attachment points of broken bonds with pseudo atoms
     * @return fragment container (deep copy)
     */
    private IAtomContainer extractFragment(
            IAtomContainer molecule,
            int centerIdx,
            int radius,
            int initCollectionSize,
            boolean preserveStereo,
            boolean markAttachments) {

        // --- 1. BFS to collect atoms within radius bonds ---

        // Tracks the depth (sphere nr. / level / height) of each atom and whether it has been visited already
        int[] depths = new int[molecule.getAtomCount()];
        Arrays.fill(depths, -1);
        // Stores collected atoms in BFS order; first entry is always the center atom.
        // Both collections are kept in parallel: depths for fast lookup, collectedAtoms for ordered iteration.
        List<IAtom> collectedAtoms = new ArrayList<>(initCollectionSize);

        // BFS queue entries, [atomIndex, depth]
        Deque<Integer> queue = new ArrayDeque<>(initCollectionSize);

        IAtom centerAtom = molecule.getAtom(centerIdx);
        depths[centerIdx] = 0;
        collectedAtoms.add(centerAtom);
        queue.add(centerIdx);

        while (!queue.isEmpty()) {
            int currentIdx = queue.poll();
            int currentDepth = depths[currentIdx];

            if (currentDepth >= radius) {
                // Do not expand further, but the atom itself is already collected
                continue;
            }

            IAtom currentAtom = molecule.getAtom(currentIdx);

            for (IBond bond : currentAtom.bonds()) {
                IAtom neighbor = bond.getOther(currentAtom);
                Integer neighborIdx = neighbor.getIndex();
                if (depths[neighborIdx] == -1) {
                    depths[neighborIdx] = currentDepth + 1;
                    collectedAtoms.add(neighbor);
                    queue.add(neighborIdx);
                }
            }
        }

        // --- 2. Collect all bonds whose both endpoints are in the fragment ---
        
        List<IBond> collectedBonds = new ArrayList<>(initCollectionSize);
        //set for faster look-up whether bond was already collected
        Set<IBond> bondsInFragment = new HashSet<>(initCollectionSize);

        for (IAtom atom : collectedAtoms) {
            for (IBond bond : atom.bonds()) {
                if (bondsInFragment.contains(bond)) {
                    continue;
                }
                IAtom other = bond.getOther(atom);
                Integer otherIdx = other.getIndex();
                if (otherIdx != null && depths[otherIdx] != -1) {
                    collectedBonds.add(bond);
                    bondsInFragment.add(bond);
                }
            }
        }

        // --- 3. Deep-copy atoms and bonds into a new container ---

        IAtomContainer fragment = molecule.getBuilder().newAtomContainer();

        // Map: original IAtom reference -> copied IAtom in the fragment
        Map<IAtom, IAtom> originalAtomToCopyAtomMap = new HashMap<>((int) Math.ceil(collectedAtoms.size() * 1.4));
        // Map: original IBond reference -> copied IBond in the fragment
        Map<IBond, IBond> originalBondToCopyBondMap = new HashMap<>((int) Math.ceil(collectedBonds.size() * 1.4));

        for (IAtom origAtom : collectedAtoms) {
            IAtom copiedAtom = this.deeperCopy(origAtom, fragment);
            copiedAtom.setProperty(CircularFragmenter.FRAGMENT_ATOM_DEPTH_PROPERTY_KEY, depths[origAtom.getIndex()]);
            originalAtomToCopyAtomMap.put(origAtom, copiedAtom);
            fragment.addAtom(copiedAtom);
        }

        for (IBond origBond : collectedBonds) {
            // Re-wire the copied bond to the copied atom instances
            IAtom copiedBegin = originalAtomToCopyAtomMap.get(origBond.getBegin());
            IAtom copiedEnd = originalAtomToCopyAtomMap.get(origBond.getEnd());
            if (copiedBegin == null || copiedEnd == null || copiedBegin.getContainer() != copiedEnd.getContainer()) {
                continue;
            }
            IBond copiedBond = this.deeperCopy(origBond, copiedBegin, copiedEnd);
            originalBondToCopyBondMap.put(origBond, copiedBond);
            fragment.addBond(copiedBond);
        }

        // single electrons
        for (ISingleElectron se : molecule.singleElectrons()) {
            IAtom atom = originalAtomToCopyAtomMap.get(se.getAtom());
            if (!Objects.isNull(atom)) {
                atom.getContainer().addSingleElectron(atom.getIndex());
            }
        }
        // lone pairs
        for (ILonePair lp : molecule.lonePairs()) {
            IAtom atom = originalAtomToCopyAtomMap.get(lp.getAtom());
            if (!Objects.isNull(atom)) {
                atom.getContainer().addLonePair(atom.getIndex());
            }
        }
        // stereo elements
        if (preserveStereo) {
            for (IStereoElement elem : molecule.stereoElements()) {
                try {
                    fragment.addStereoElement(elem.map(originalAtomToCopyAtomMap, originalBondToCopyBondMap));
                } catch (NoSuchAtomException | NoSuchBondException exception) {
                    //catch those because they appear if not all stereo carriers are present in the fragment
                }
            }
        }

        //saturation
        for (Map.Entry<IAtom, IAtom> entry : originalAtomToCopyAtomMap.entrySet()) {
            for (IBond originalBond : molecule.getConnectedBondsList(entry.getKey())) {
                if (!originalBondToCopyBondMap.containsKey(originalBond)) {
                    this.saturate(entry.getValue(), fragment, markAttachments, entry.getKey(), originalBond);
                }
            }
        }

        //note: properties of the original atom container are not copied.

        return fragment;
    }

    /**
     *  Creates a relatively deep ("deeper" than cloning but not as extensive) copy of the given atom and adds it to the given container.
     *  Copies:
     *  <br>- atomic number
     *  <br>- implicit hydrogen count
     *  <br>- aromaticity flag
     *  <br>- valency
     *  <br>- atom type name
     *  <br>- formal charge
     *  <br>- point 2D and 3D coordinates
     *  <br>- flags
     *  <br>- some primitive-based properties (String, Integer, Boolean)
     * <br>Note: atom types and isotopes of the original atoms are not copied and hence, some properties will be unset in the copies.
     * If you need atom types and their defining properties, you need to re-perceive them after copying.
     *
     * @param atom the atom to copy
     * @param container the container to add the copied atom to
     * @return the copied atom
     */
    private IAtom deeperCopy(IAtom atom, IAtomContainer container) {
        IAtom cpyAtom = container.newAtom(atom.getAtomicNumber(),
                atom.getImplicitHydrogenCount());
        cpyAtom.setIsAromatic(atom.isAromatic());
        cpyAtom.setValency(atom.getValency());
        cpyAtom.setAtomTypeName(atom.getAtomTypeName());
        //setting the formal charge also sets the (partial) charge, see https://github.com/cdk/cdk/pull/1151
        cpyAtom.setFormalCharge(atom.getFormalCharge());
        if (atom.getPoint2d() != null) {
            cpyAtom.setPoint2d(new Point2d(atom.getPoint2d().x, atom.getPoint2d().y));
        }
        if (atom.getPoint3d() != null) {
            cpyAtom.setPoint3d(new Point3d(atom.getPoint3d().x, atom.getPoint3d().y, atom.getPoint3d().z));
        }
        cpyAtom.setFlags(atom.getFlags());
        //fractional point 3D (location in a crystal unit cell) is deliberately not copied; add if needed
        //fields related to atom type (max bond order, bond order sum, covalent radius, hybridization, formal neighbor count) are deliberately not copied; add if needed
        //fields related to isotope (exact mass, natural abundance, mass number) are deliberately not copied; add if needed
        //properties:
        for (Map.Entry<Object, Object> entry : atom.getProperties().entrySet()) {
            if ((entry.getKey() instanceof String || entry.getKey() instanceof Integer || entry.getKey() instanceof Boolean)
                    && (entry.getValue() instanceof String || entry.getValue() instanceof Integer || entry.getValue() instanceof Boolean || entry.getValue() == null)) {
                cpyAtom.setProperty(entry.getKey(), entry.getValue());
            }
        }
        return cpyAtom;
    }

    /**
     * Creates a relatively deep ("deeper" than cloning but not as extensive) copy of the given bond between the given begin and end atoms.
     * Copies:
     * <br>- order
     * <br>- aromaticity flag
     * <br>- display
     * <br>- in ring flag
     * <br>- flags
     * <br>- electron count
     * <br>- some primitive-based properties (String, Integer, Boolean)
     * <br>Note: The begin and end atoms are not copied, but the given ones are used in the copy.
     * <br>Note also: the created bond must be added to the copy atom container by the calling code!
     *
     * @param bond the bond to copy
     * @param begin the begin atom of the bond in the copy(!)
     * @param end the end atom of the bond in the copy(!)
     * @return the copied bond
     */
    private IBond deeperCopy(IBond bond, IAtom begin, IAtom end) {
        //using begin.getContainer().newBond() here caused weird issues sometimes
        IBond newBond = new Bond(begin, end, bond.getOrder());
        newBond.setIsAromatic(bond.isAromatic());
        newBond.setDisplay(bond.getDisplay());
        newBond.setIsInRing(bond.isInRing());
        newBond.setFlags(bond.getFlags());
        newBond.setElectronCount(bond.getElectronCount());
        //properties:
        for (Map.Entry<Object, Object> entry : bond.getProperties().entrySet()) {
            if ((entry.getKey() instanceof String || entry.getKey() instanceof Integer || entry.getKey() instanceof Boolean)
                    && (entry.getValue() instanceof String || entry.getValue() instanceof Integer || entry.getValue() instanceof Boolean || entry.getValue() == null)) {
                newBond.setProperty(entry.getKey(), entry.getValue());
            }
        }
        return newBond;
    }

    /**
     * Saturates a broken bond at an attachment point by either adding a pseudo atom or increasing the implicit hydrogen count.
     * <p>
     * No checks are performed!
     *
     * @param copyAtomToSaturate The atom in the copy container that needs to be saturated due to a broken bond
     * @param copyContainer The atom container containing the atom to be saturated
     * @param markAttachments If true, a pseudo atom is added to mark the attachment point;
     *                        if false, the implicit hydrogen count is increased.
     * @param originalAtom The original atom from the input molecule corresponding to the atom to be saturated.
     *                     Used for determining bond properties
     * @param originalBond The original bond that was broken during the extraction process.
     *                     Used for determining the bond order of the new pseudo atom bond
     */
    private void saturate(
            IAtom copyAtomToSaturate,
            IAtomContainer copyContainer,
            boolean markAttachments,
            IAtom originalAtom,
            IBond originalBond
    ) {
        //correction for unset (aromatic) bonds and aromatic double bonds
        IBond.Order effectiveOrder;
        if (originalBond.getOrder() == null || originalBond.getOrder() == IBond.Order.UNSET) {
            effectiveOrder = IBond.Order.SINGLE;
        } else if (originalBond.isAromatic() && originalBond.getOrder() == IBond.Order.DOUBLE) {
            effectiveOrder = IBond.Order.SINGLE;
        } else {
            effectiveOrder = originalBond.getOrder();
        }

        if (markAttachments) {
            IPseudoAtom pseudoAtom = originalAtom.getBuilder().newInstance(IPseudoAtom.class, "R");
            pseudoAtom.setAttachPointNum(1);
            pseudoAtom.setImplicitHydrogenCount(0);
            IBond bondToPseudoAtom;
            if (originalBond.getBegin().equals(originalAtom)) {
                bondToPseudoAtom = originalAtom.getBuilder().newInstance(
                        IBond.class, copyAtomToSaturate, pseudoAtom, effectiveOrder);
            } else {
                bondToPseudoAtom = originalAtom.getBuilder().newInstance(
                        IBond.class, pseudoAtom, copyAtomToSaturate, effectiveOrder);
            }
            copyContainer.addAtom(pseudoAtom);
            copyContainer.addBond(bondToPseudoAtom);
        } else {
            copyAtomToSaturate.setImplicitHydrogenCount(
                    copyAtomToSaturate.getImplicitHydrogenCount()
                            + effectiveOrder.numeric());
        }
    }
}
