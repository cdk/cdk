package org.openscience.cdk.smsd.labelling;

import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * @cdk.module smsd
 * @cdk.githash
 */

public interface ICanonicalMoleculeLabeller {
    
    public IAtomContainer getCanonicalMolecule(IAtomContainer container);

    public int[] getCanonicalPermutation(IAtomContainer container);
}
