package org.openscience.cdk.smsd.labelling;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * @cdk.module smsd
 * @cdk.githash
 * @deprecated This class is part of SMSD and either duplicates functionality elsewhere in the CDK or provides public
 *             access to internal implementation details. SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class AtomContainerAtomPermutor extends Permutor implements Iterator<IAtomContainer> {

    private final IAtomContainer original;

    public AtomContainerAtomPermutor(IAtomContainer atomContainer) {
        super(atomContainer.getAtomCount());
        original = atomContainer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAtomContainer next() {
        if (!hasNext())
            throw new NoSuchElementException();
        int[] p = super.getNextPermutation();
        return AtomContainerAtomPermutor.permute(p, original);
    }

    public static IAtomContainer permute(int[] p, IAtomContainer atomContainer) {
        return cloneAndPermute(p, atomContainer);
    }

    // an old method existed that only cloned the atoms/bonds
    private static IAtomContainer cloneAndPermute(int[] p, IAtomContainer atomContainer) {
        IAtomContainer permutedContainer = null;
        try {
            permutedContainer = atomContainer.clone();
            int n = atomContainer.getAtomCount();
            IAtom[] permutedAtoms = new IAtom[n];
            for (int originalIndex = 0; originalIndex < n; originalIndex++) {
                // get the newly cloned atom
                IAtom atom = permutedContainer.getAtom(originalIndex);

                // permute the index
                int newIndex = p[originalIndex];

                // put the atom in the new place
                permutedAtoms[newIndex] = atom;
            }
            permutedContainer.setAtoms(permutedAtoms);
        } catch (CloneNotSupportedException cne) {
            //?
            System.out.println(cne);
        }
        return permutedContainer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove() {
        // can just increase rank....
    }
}
