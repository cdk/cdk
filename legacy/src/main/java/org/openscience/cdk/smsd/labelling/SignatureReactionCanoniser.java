package org.openscience.cdk.smsd.labelling;

import org.openscience.cdk.interfaces.IReaction;

/**
 * @cdk.module smsd
 * @cdk.githash
 * @deprecated This class is part of SMSD and either duplicates functionality elsewhere in the CDK or provides public
 *             access to internal implementation details. SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
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
