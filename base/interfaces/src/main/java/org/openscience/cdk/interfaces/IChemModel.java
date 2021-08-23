/* Copyright (C) 2006-2007,2011  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
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
package org.openscience.cdk.interfaces;

/**
 * An object containing multiple MoleculeSet and the other lower level concepts like rings,
 * sequences, fragments, etc.
 *
 * @cdk.module interfaces
 * @cdk.githash
 */
public interface IChemModel extends IChemObject {

    /**
     * Returns the IAtomContainerSet of this ChemModel.
     *
     * @return The {@link IAtomContainerSet} of this ChemModel
     * @see #setMoleculeSet
     */
    public IAtomContainerSet getMoleculeSet();

    /**
     * Sets the IAtomContainerSet of this ChemModel.
     *
     * @param setOfMolecules the content of this model
     * @see #getMoleculeSet
     */
    public void setMoleculeSet(IAtomContainerSet setOfMolecules);

    /**
     * Returns the RingSet of this ChemModel.
     *
     * @return the ringset of this model
     * @see #setRingSet
     */
    public IRingSet getRingSet();

    /**
     * Sets the RingSet of this ChemModel.
     *
     * @param ringSet the content of this model
     * @see #getRingSet
     */
    public void setRingSet(IRingSet ringSet);

    /**
     * Gets the Crystal contained in this ChemModel.
     *
     * @return The crystal in this model
     * @see #setCrystal
     */
    public ICrystal getCrystal();

    /**
     * Sets the Crystal contained in this ChemModel.
     *
     * @param crystal the Crystal to store in this model
     * @see #getCrystal
     */
    public void setCrystal(ICrystal crystal);

    /**
     * Gets the ReactionSet contained in this ChemModel.
     *
     * @return The ReactionSet in this model
     * @see #setReactionSet
     */
    public IReactionSet getReactionSet();

    /**
     * Sets the ReactionSet contained in this ChemModel.
     *
     * @param sor the ReactionSet to store in this model
     * @see #getReactionSet
     */
    public void setReactionSet(IReactionSet sor);

    /**
     * Returns true if this ChemModel is empty.
     *
     * @return a boolean indicating if this model has no content
     */
    public boolean isEmpty();
}
