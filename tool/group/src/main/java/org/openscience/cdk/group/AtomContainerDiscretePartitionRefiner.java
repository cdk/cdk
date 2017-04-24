package org.openscience.cdk.group;

import org.openscience.cdk.interfaces.IAtomContainer;

public abstract class AtomContainerDiscretePartitionRefiner extends AbstractDiscretePartitionRefiner {
    

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
    public void refine(IAtomContainer atomContainer) {
        refine(atomContainer, getInitialPartition(atomContainer));
    }

    /**
     * Refine an atom partition based on the connectivity in the atom container.
     *
     * @param atomContainer the atom container to use
     * @param partition the initial partition of the atoms
     */
    public void refine(IAtomContainer atomContainer, Partition partition) {
        setup(atomContainer);
        super.refine(partition);
    }
    
    /**
     * Checks if the atom container is canonical. Note that this calls
     * {@link #refine} first.
     *
     * @param atomContainer the atom container to check
     * @return true if the atom container is canonical
     */
    public boolean isCanonical(IAtomContainer atomContainer) {
        setup(atomContainer);
        super.refine(getInitialPartition(atomContainer));
        return isCanonical();
    }
    
    /**
     * Gets the automorphism group of the atom container. By default it uses an
     * initial partition based on the element symbols (so all the carbons are in
     * one cell, all the nitrogens in another, etc). If this behaviour is not
     * desired, then use the {@link #ignoreElements} flag in the constructor.
     *
     * @param atomContainer the atom container to use
     * @return the automorphism group of the atom container
     */
    public PermutationGroup getAutomorphismGroup(IAtomContainer atomContainer) {
        setup(atomContainer);
        super.refine(getInitialPartition(atomContainer));
        return super.getAutomorphismGroup();
    }
    
    /**
     * Speed up the search for the automorphism group using the automorphisms in
     * the supplied group. Note that the behaviour of this method is unknown if
     * the group does not contain automorphisms...
     *
     * @param atomContainer the atom container to use
     * @param group the group of known automorphisms
     * @return the full automorphism group
     */
    public PermutationGroup getAutomorphismGroup(IAtomContainer atomContainer, PermutationGroup group) {
        setup(atomContainer, group);
        super.refine(getInitialPartition(atomContainer));
        return super.getAutomorphismGroup();
    }
    
    /**
     * Get the automorphism group of the molecule given an initial partition.
     *
     * @param atomContainer the atom container to use
     * @param initialPartition an initial partition of the atoms
     * @return the automorphism group starting with this partition
     */
    public PermutationGroup getAutomorphismGroup(IAtomContainer atomContainer, Partition initialPartition) {
        setup(atomContainer);
        super.refine(initialPartition);
        return super.getAutomorphismGroup();
    }
    
    /**
     * Get the automorphism partition (equivalence classes) of the atoms.
     *
     * @param atomContainer the molecule to calculate equivalence classes for
     * @return a partition of the atoms into equivalence classes
     */
    public Partition getAutomorphismPartition(IAtomContainer atomContainer) {
        setup(atomContainer);
        super.refine(getInitialPartition(atomContainer));
        return super.getAutomorphismPartition();
    }
    
    protected abstract Refinable getRefinable(IAtomContainer atomContainer);
    
    public abstract Partition getInitialPartition(IAtomContainer atomContainer);
    
    private void setup(IAtomContainer atomContainer) {
        // have to setup the connection table before making the group
        // otherwise the size may be wrong, but only setup if it doesn't exist
        Refinable refinable = getRefinable(atomContainer);
        
        int size = getVertexCount();
        PermutationGroup group = new PermutationGroup(new Permutation(size));
        super.setup(group, new EquitablePartitionRefiner(refinable));
    }

    private void setup(IAtomContainer atomContainer, PermutationGroup group) {
        super.setup(group, new EquitablePartitionRefiner(getRefinable(atomContainer)));
    }

}
