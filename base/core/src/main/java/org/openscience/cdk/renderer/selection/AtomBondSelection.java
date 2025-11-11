package org.openscience.cdk.renderer.selection;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A simple implementation of an {@link IChemObjectSelection} which wraps
 * a HashSet. This selection only allows select
 */
public class AtomBondSelection implements IChemObjectSelection {

    private final Set<IChemObject> selected = new HashSet<>();

    /**
     * Select the chemobject in this selection.
     * @param obj the chemobject
     */
    public void select(IChemObject obj) {
        if (obj instanceof IAtom || obj instanceof IBond)
            selected.add(obj);
    }

    /**
     * Deselect the chemobject in this selection.
     * @param obj the chemobject
     */
    public void remove(IChemObject obj) {
        selected.remove(obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void select(IChemModel chemModel) {
        for (IAtomContainer mol : chemModel.getMoleculeSet()) {
            for (IAtom atom : mol.atoms())
                this.selected.add(atom);
            for (IBond bond : mol.bonds())
                this.selected.add(bond);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAtomContainer getConnectedAtomContainer() {
        IAtomContainer result = null;
        for (IAtom atom : elements(IAtom.class)) {
            if (result == null) result = atom.getBuilder().newAtomContainer();
            result.addAtom(atom);
        }

        if (result != null) {
            for (IBond bond : elements(IBond.class)) {
                if (result.contains(bond.getBegin()) &&
                    result.contains(bond.getEnd()))
                    result.addBond(bond);
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFilled() {
        return !selected.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(IChemObject obj) {
        return selected.contains(obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <E extends IChemObject> Collection<E> elements(Class<E> cls) {
        Collection<E> result = new ArrayList<>();

        if (IAtom.class.isAssignableFrom(cls)) {
            for (IChemObject chemObj : selected) {
                if (chemObj instanceof IAtom)
                    result.add((E) chemObj);
            }
        } else if (IBond.class.isAssignableFrom(cls)) {
            for (IChemObject chemObj : selected) {
                if (chemObj instanceof IBond)
                    result.add((E) chemObj);
            }
        } else if (IChemObject.class == cls) {
            for (IChemObject chemObj : selected) {
                result.add((E) chemObj);
            }
        }
        return result;
    }
}
