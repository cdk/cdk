package org.openscience.cdk.group;

/**
 * A mechanism for refining partitions of graph-like objects.
 * 
 * @author maclean
 *
 */
public interface DiscretePartitionRefiner {
    
    /**
     * Get the best permutation found.
     *
     * @return the permutation that gives the maximal half-matrix string
     */
    Permutation getBest();
    
    /**
     * The automorphism partition is a partition of the elements of the group.
     *
     * @return a partition of the elements of group
     */
    Partition getAutomorphismPartition();
    
    /**
     * Get the automorphism group used to prune the search.
     *
     * @return the automorphism group
     */
    PermutationGroup getAutomorphismGroup();
    
    /**
     * Get the first permutation reached by the search.
     *
     * @return the first permutation reached
     */
    Permutation getFirst();
    
    /**
     * Check that the first refined partition is the identity.
     *
     * @return true if the first is the identity permutation
     */
    boolean firstIsIdentity();

}
