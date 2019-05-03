package org.openscience.cdk.smsd.labelling;

import org.openscience.cdk.interfaces.IReaction;

/**
 * @cdk.module  smsd
 * @cdk.githash
 * @deprecated This class is part of SMSD and either duplicates functionality elsewhere in the CDK or provides public
 *             access to internal implementation details. SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public interface ICanonicalReactionLabeller {

    /**
     * Convert a reaction into a canonical form by canonizing each of the
     * structures in the reaction in turn.
     *
     * @param  reaction the {@link IReaction} to be processed
     * @return          the canonical {@link IReaction}
     */
    public IReaction getCanonicalReaction(IReaction reaction);

}
