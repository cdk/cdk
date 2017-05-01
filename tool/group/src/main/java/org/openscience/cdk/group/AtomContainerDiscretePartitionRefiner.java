/* Copyright (C) 2017  Gilleain Torrance <gilleain.torrance@gmail.com>
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
package org.openscience.cdk.group;

import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * A tool for determining the automorphism group of the atoms in a molecule, or
 * for checking for a canonical form of a molecule.
 *
 * If two atoms are equivalent under an automorphism in the group, then
 * roughly speaking they are in symmetric positions in the molecule. For
 * example, the C atoms in two methyl groups attached to a benzene ring
 * are 'equivalent' in this sense.
 *
 * <p>There are a couple of ways to use it - firstly, get the automorphisms.</p>
 *
 * <pre>
 *     IAtomContainer ac = ... // get an atom container somehow
 *     AtomContainerDiscretePartitionRefiner refiner = 
 *          PartitionRefinement.forAtoms().create()
 *     PermutationGroup autG = refiner.getAutomorphismGroup(ac);
 *     for (Permutation automorphism : autG.all()) {
 *         ... // do something with the permutation
 *     }
 * </pre>
 *
 * <p>Another is to check an atom container to see if it is canonical:</p>
 *
 * <pre>
 *     IAtomContainer ac = ... // get an atom container somehow
 *     AtomContainerDiscretePartitionRefiner refiner = 
 *          PartitionRefinement.forAtoms().create()
 *     if (refiner.isCanonical(ac)) {
 *         ... // do something with the atom container
 *     }
 * </pre>
 *
 * Note that it is not necessary to call {@link #refine(IAtomContainer)} before
 * either of these methods. However if both the group and the canonical check
 * are required, then the code should be:
 *
 * <pre>
 *     AtomContainerDiscretePartitionRefiner refiner = 
 *          PartitionRefinement.forAtoms().create()
 *     refiner.refine(ac);
 *     boolean isCanon = refiner.isCanonical();
 *     PermutationGroup autG = refiner.getAutomorphismGroup();
 * </pre>
 *
 * This way, the refinement is not carried out multiple times.
 */
public interface AtomContainerDiscretePartitionRefiner extends DiscretePartitionRefiner {
    
    /**
     * Refine an atom container, which has the side effect of calculating
     * the automorphism group.
     *
     * If the group is needed afterwards, call {@link #getAutomorphismGroup()}
     * instead of {@link #getAutomorphismGroup(IAtomContainer)} otherwise the
     * refine method will be called twice.
     *
     * @param atomContainer the atomContainer to refine
     */
    public void refine(IAtomContainer atomContainer);

    /**
     * Refine an atom partition based on the connectivity in the atom container.
     *
     * @param atomContainer the atom container to use
     * @param partition the initial partition of the atoms
     */
    public void refine(IAtomContainer atomContainer, Partition partition);
    
    /**
     * Checks if the atom container is canonical. Note that this calls
     * {@link #refine} first.
     *
     * @param atomContainer the atom container to check
     * @return true if the atom container is canonical
     */
    public boolean isCanonical(IAtomContainer atomContainer);
    
    /**
     * Gets the automorphism group of the atom container. By default it uses an
     * initial partition based on the element symbols (so all the carbons are in
     * one cell, all the nitrogens in another, etc). If this behaviour is not
     * desired, then use the {@link #ignoreElements} flag in the constructor.
     *
     * @param atomContainer the atom container to use
     * @return the automorphism group of the atom container
     */
    public PermutationGroup getAutomorphismGroup(IAtomContainer atomContainer);
    
    /**
     * Speed up the search for the automorphism group using the automorphisms in
     * the supplied group. Note that the behaviour of this method is unknown if
     * the group does not contain automorphisms...
     *
     * @param atomContainer the atom container to use
     * @param group the group of known automorphisms
     * @return the full automorphism group
     */
    public PermutationGroup getAutomorphismGroup(IAtomContainer atomContainer, PermutationGroup group);
    
    /**
     * Get the automorphism group of the molecule given an initial partition.
     *
     * @param atomContainer the atom container to use
     * @param initialPartition an initial partition of the atoms
     * @return the automorphism group starting with this partition
     */
    public PermutationGroup getAutomorphismGroup(IAtomContainer atomContainer, Partition initialPartition);
    
    /**
     * Get the automorphism partition (equivalence classes) of the atoms.
     *
     * @param atomContainer the molecule to calculate equivalence classes for
     * @return a partition of the atoms into equivalence classes
     */
    public Partition getAutomorphismPartition(IAtomContainer atomContainer);
}
