/*
 * Copyright (c) 2014 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscience.cdk.renderer.generators.standard;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.SymbolVisibility;

import java.util.List;

/**
 * Extended existing symbol visibility options to account for selection of atoms in the standard
 * generator. 
 *
 * The selection viability displays an atom symbol regardless as to whether it is normally 'shown'.
 * By default, the symbol is shown if the atom is selected an not next to any selected bonds
 * (disconnected). Alternatively, all select atoms can be displayed. 
 *
 * An atom or bond is selected if the {@link StandardGenerator#HIGHLIGHT_COLOR} is non-null.
 *
 * @author John May
 */
public final class SelectionVisibility extends SymbolVisibility {

    private final SymbolVisibility delegate;
    private final boolean          showAll;

    /**
     * Internal constructor.
     *
     * @param delegate default viability
     * @param showAll      all select atoms are displayed
     */
    private SelectionVisibility(SymbolVisibility delegate, boolean showAll) {
        this.delegate = delegate;
        this.showAll = showAll;
    }

    /**
     * Display the atom symbol if is disconnected from any other selected atoms or bonds. The
     * provided visibility is used when the atom is not selected.
     *
     * @param visibility visibility when not selected
     * @return visibility instance
     */
    public static SymbolVisibility disconnected(SymbolVisibility visibility) {
        return new SelectionVisibility(visibility, false);
    }

    /**
     * Display the atom symbol if is selected, otherwise use the provided visibility.
     *
     * @param visibility visibility when not selected
     * @return visibility instance
     */
    public static SymbolVisibility all(SymbolVisibility visibility) {
        return new SelectionVisibility(visibility, true);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public boolean visible(IAtom atom, List<IBond> neighbors, RendererModel model) {
        if (isSelected(atom, model) && (showAll || !hasSelectedBond(neighbors, model))) return true;
        return delegate.visible(atom, neighbors, model);
    }

    /**
     * Determine if an object is selected.
     *
     * @param object the object
     * @return object is selected
     */
    static boolean isSelected(IChemObject object, RendererModel model) {
        if (object.getProperty(StandardGenerator.HIGHLIGHT_COLOR) != null) return true;
        if (model.getSelection() != null) return model.getSelection().contains(object);
        return false;
    }

    /**
     * Determines if any bond in the list is selected
     *
     * @param bonds list of bonds
     * @return at least bond bond is selected
     */
    static boolean hasSelectedBond(List<IBond> bonds, RendererModel model) {
        for (IBond bond : bonds) {
            if (isSelected(bond, model)) return true;
        }
        return false;
    }
}
