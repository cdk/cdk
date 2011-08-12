package org.openscience.cdk.smsd.labelling;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.signature.MoleculeSignature;

/**
 * @cdk.module smsd
 * @cdk.githash
 */

public class MoleculeSignatureLabellingAdaptor implements ICanonicalMoleculeLabeller {

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
        MoleculeSignature molSig = new MoleculeSignature(container); 
        return molSig.getCanonicalLabels();
    }
}
