package org.openscience.cdk.smsd.labelling;

import org.openscience.cdk.graph.invariant.CanonicalLabeler;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.InvPair;

/**
 * @cdk.module smsd
 * @cdk.githash
 */

public class CanonicalLabellingAdaptor implements ICanonicalMoleculeLabeller {

	/**
	 * {@inheritDoc}
	 */
    public IAtomContainer getCanonicalMolecule(IAtomContainer container) {
        return AtomContainerAtomPermutor.permute(
                getCanonicalPermutation(container), container);
    }

	/**
	 * {@inheritDoc}
	 */
    public int[] getCanonicalPermutation(IAtomContainer container) {
        CanonicalLabeler labeler = new CanonicalLabeler();
        labeler.canonLabel(container);
        int n = container.getAtomCount();
        int[] perm = new int[n];
        for (int i = 0; i < n; i++) {
            IAtom a = container.getAtom(i);
            int x = ((Long) a.getProperty(InvPair.CANONICAL_LABEL)).intValue(); 
            perm[i] = x - 1;
        }
        return perm;
    }
}
