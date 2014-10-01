package org.openscience.cdk.smsd.labelling;

import org.openscience.cdk.interfaces.IReaction;

/**
 * @cdk.module smsd
 * @cdk.githash
 */

public class SmilesReactionCanoniser extends AbstractReactionLabeller implements ICanonicalReactionLabeller {

    private CanonicalLabellingAdaptor labeller = new CanonicalLabellingAdaptor();

    /**
     * {@inheritDoc}
     */
    @Override
    public IReaction getCanonicalReaction(IReaction reaction) {
        return super.labelReaction(reaction, labeller);
    }
}
