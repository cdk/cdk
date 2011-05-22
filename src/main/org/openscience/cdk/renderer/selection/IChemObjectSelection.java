/* Copyright (C) 2009  Gilleain Torrance <gilleain@users.sf.net>
 *               2009  Arvid Berg <goglepox@users.sf.net>
 *
 * Contact: cdk-devel@list.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.renderer.selection;

import java.util.Collection;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;

/**
 * A selection of some atoms and bonds from an {@link IAtomContainer} or
 * {@link IChemModel}.
 *
 * @author maclean
 * @cdk.module render
 * @cdk.githash
 */
public interface IChemObjectSelection {

	/**
     * Perform a selection by some method. This is used for selecting outside
     * the hub, for example:
     *
     *   IChemModel model = createModelBySomeMethod();
     *   selection.select(model);
     *   renderModel.setSelection(selection);
     *
     * @param chemModel an IChemModel to select from.
     */
    public void select(IChemModel chemModel);


    /**
     * Make an IAtomContainer where all the bonds
     * only have atoms that are in the selection.
     *
     * @return a well defined atom container.
     */
    public IAtomContainer getConnectedAtomContainer();

    /**
     * The opposite of a method like "isEmpty"
     *
     * @return true if there is anything in the selection
     */
    public boolean isFilled();

    /**
     * Determines if the {@link IChemObject} is part of the current selection.
     *
     * @param obj {@link IChemObject} which might be part of the selection
     * @return true, if the given <code>obj</code> is part of the selection
     */
    public boolean contains(IChemObject obj);

    /**
     * Returns a {@link Collection} of all selected {@link IChemObject}s of the
     * given type.
     *
     * @param clazz type of {@link IChemObject}s that should be returned.
     * @return a {@link Collection} of {@link IChemObject} of the given type
     */
    public <E extends IChemObject> Collection<E> elements(Class<E> clazz);
}
