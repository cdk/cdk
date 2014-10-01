package org.openscience.cdk.smsd.labelling;

import org.openscience.cdk.interfaces.IReaction;

/**
 * @cdk.module smsd
 * @cdk.githash
 */

public class SignatureReactionCanoniser extends AbstractReactionLabeller implements ICanonicalReactionLabeller {

    private MoleculeSignatureLabellingAdaptor labeller = new MoleculeSignatureLabellingAdaptor();

    /**
     * {@inheritDoc}
     */
    @Override
    public IReaction getCanonicalReaction(IReaction reaction) {
        return super.labelReaction(reaction, labeller);
    }
}
