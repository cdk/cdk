package org.openscience.cdk.group;

/**
 * Factory for partition refiners. Use like:
 * 
 * <pre>
 *     AtomContainerDiscretePartitionRefiner refiner = PartitionRefinement.forAtoms().create();
 * </pre>
 * 
 * The methods forAtoms and forBonds return builders with methods to allow setting the 
 * switches for ignoring atom types and/or bond orders.
 * 
 * @author maclean
 *
 */
public class PartitionRefinement {
    
    /**
     * @return a builder that makes atom refiners
     */
    public static AtomRefinerBuilder forAtoms() {
        return new AtomRefinerBuilder();
    }
    
    public static class AtomRefinerBuilder {
        private boolean ignoreAtomTypes;
        private boolean ignoreBondOrders;
        
        public AtomRefinerBuilder ignoringAtomTypes() {
            this.ignoreAtomTypes = true;
            return this;
        }
        
        public AtomRefinerBuilder ignoringBondOrders() {
            this.ignoreBondOrders = true;
            return this;
        }
        
        public AtomContainerDiscretePartitionRefiner create() {
            return new AtomDiscretePartitionRefiner(ignoreAtomTypes, ignoreBondOrders);
        }
    }
    
    /**
     * @return a builder that makes bond refiners
     */
    public BondRefinerBuilder forBonds() {
        return new BondRefinerBuilder();
    }
    
    public static class BondRefinerBuilder {
        private boolean ignoreBondOrders;
        
        public BondRefinerBuilder ignoringBondOrders() {
            this.ignoreBondOrders = true;
            return this;
        }
        
        public AtomContainerDiscretePartitionRefiner create() {
            return new BondDiscretePartitionRefiner(ignoreBondOrders);
        }
    }

}
