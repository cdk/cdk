package org.openscience.cdk.hash;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IPseudoAtom;

import java.util.BitSet;

/**
 * Defines a method of suppressing certain atoms from an {@link IAtomContainer}
 * when computing the hash codes for the molecule or it's atoms.
 *
 * @author John May
 * @cdk.module hash
 */
@TestClass("org.openscience.cdk.hash.AtomSuppressionTest")
abstract class AtomSuppression {

    /**
     * Returns a new instance indicated which atoms are suppressed for this
     * suppression method.
     *
     * @param container molecule with 0 or more atoms
     * @return the vertices (atom index) which should be suppressed
     */
    abstract Suppressed suppress(IAtomContainer container);

    /** Default implementation - don't suppress anything. */
    private static final class Unsuppressed extends AtomSuppression {
        @Override Suppressed suppress(IAtomContainer container) {
            return Suppressed.none();
        }
    }

    /**
     * Suppresses any explicit hydrogen regardless of whether the atom is a
     * hydrogen ion or isotope.
     */
    private static final class AnyHydrogens extends AtomSuppression {
        /** @inheritDoc */
        @Override Suppressed suppress(IAtomContainer container) {
            BitSet hydrogens = new BitSet();
            for (int i = 0; i < container.getAtomCount(); i++) {
                IAtom atom = container.getAtom(i);
                hydrogens.set(i, "H".equals(atom.getSymbol()));
            }
            return Suppressed.fromBitSet(hydrogens);
        }
    }

    /** Suppresses any pseudo atom. */
    private static final class AnyPseudos extends AtomSuppression {
        /** @inheritDoc */
        @Override Suppressed suppress(IAtomContainer container) {
            BitSet hydrogens = new BitSet();
            for (int i = 0; i < container.getAtomCount(); i++) {
                IAtom atom = container.getAtom(i);
                hydrogens.set(i, atom instanceof IPseudoAtom);
            }
            return Suppressed.fromBitSet(hydrogens);
        }
    }

    /** internal reference for factory. */
    private static final AtomSuppression unsuppressed = new Unsuppressed();
    /** internal reference for factory. */
    private static final AtomSuppression anyHydrogens = new AnyHydrogens();
    /** internal reference for factory. */
    private static final AtomSuppression anyPseudos   = new AnyPseudos();

    /**
     * Do not suppress any atoms.
     *
     * @return a suppression which wont' suppress anything.
     */
    @TestMethod("unsuppressed")
    static AtomSuppression unsuppressed() {
        return unsuppressed;
    }


    /**
     * Suppress all hydrogens even if they are charged or an isotope.
     *
     * @return a suppression which will mark 'all' explicit hydrogens
     */
    @TestMethod("anyHydrogens")
    static AtomSuppression anyHydrogens() {
        return anyHydrogens;
    }

    /**
     * Suppress all pseudo atoms regardless of what their label is.
     *
     * @return a suppression which will mark 'all' pseudo atoms
     */
    @TestMethod("anyPseudos")
    static AtomSuppression anyPseudos() {
        return anyPseudos;
    }
}
