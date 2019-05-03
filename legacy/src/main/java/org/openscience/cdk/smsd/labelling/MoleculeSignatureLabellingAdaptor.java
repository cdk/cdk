package org.openscience.cdk.smsd.labelling;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.signature.MoleculeSignature;

/**
 * @cdk.module smsd
 * @cdk.githash
 * @deprecated This class is part of SMSD and either duplicates functionality elsewhere in the CDK or provides public
 *             access to internal implementation details. SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class MoleculeSignatureLabellingAdaptor implements ICanonicalMoleculeLabeller {

    /**
     * {@inheritDoc}
     */
    @Override
    public IAtomContainer getCanonicalMolecule(IAtomContainer container) {
        return AtomContainerAtomPermutor.permute(getCanonicalPermutation(container), container);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getCanonicalPermutation(IAtomContainer container) {
        MoleculeSignature molSig = new MoleculeSignature(container);
        return molSig.getCanonicalLabels();
    }
}
