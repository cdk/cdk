/* Copyright (C) 1997-2007  Christoph Steinbeck <steinbeck@users.sf.net>
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

package org.openscience.cdk.silent;

import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.ICrystal;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.interfaces.IRingSet;

import java.io.Serializable;

/**
 * An object containing multiple MoleculeSet and
 * the other lower level concepts like rings, sequences,
 * fragments, etc.
 *
 * @cdk.module  silent
 * @cdk.githash
 */
public class ChemModel extends ChemObject implements Serializable, IChemModel, IChemObjectListener, Cloneable {

    /**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is incompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
     */
    private static final long   serialVersionUID = -5213425310451366185L;

    /**
     *  A MoleculeSet.
     */
    protected IAtomContainerSet setOfMolecules   = null;

    /**
     *  A ReactionSet.
     */
    protected IReactionSet      setOfReactions   = null;

    /**
     *  A RingSet.
     */
    protected IRingSet          ringSet          = null;

    /**
     *  A Crystal.
     */
    protected ICrystal          crystal          = null;

    /**
     *  Constructs an new ChemModel with a null setOfMolecules.
     */
    public ChemModel() {}

    /**
     * Returns the MoleculeSet of this ChemModel.
     *
     * @return   The MoleculeSet of this ChemModel
     *
     * @see      #setMoleculeSet
     */
    @Override
    public IAtomContainerSet getMoleculeSet() {
        return this.setOfMolecules;
    }

    /**
     * Sets the MoleculeSet of this ChemModel.
     *
     * @param   setOfMolecules  the content of this model
     *
     * @see      #getMoleculeSet
     */
    @Override
    public void setMoleculeSet(IAtomContainerSet setOfMolecules) {
        this.setOfMolecules = setOfMolecules;
    }

    /**
     * Returns the RingSet of this ChemModel.
     *
     * @return the ringset of this model
     *
     * @see      #setRingSet
     */
    @Override
    public IRingSet getRingSet() {
        return this.ringSet;
    }

    /**
     * Sets the RingSet of this ChemModel.
     *
     * @param   ringSet         the content of this model
     *
     * @see      #getRingSet
     */
    @Override
    public void setRingSet(IRingSet ringSet) {
        this.ringSet = ringSet;
    }

    /**
     * Gets the Crystal contained in this ChemModel.
     *
     * @return The crystal in this model
     *
     * @see      #setCrystal
     */
    @Override
    public ICrystal getCrystal() {
        return this.crystal;
    }

    /**
     * Sets the Crystal contained in this ChemModel.
     *
     * @param   crystal  the Crystal to store in this model
     *
     * @see      #getCrystal
     */
    @Override
    public void setCrystal(ICrystal crystal) {
        this.crystal = crystal;
    }

    /**
     * Gets the ReactionSet contained in this ChemModel.
     *
     * @return The ReactionSet in this model
     *
     * @see      #setReactionSet
     */
    @Override
    public IReactionSet getReactionSet() {
        return this.setOfReactions;
    }

    /**
     * Sets the ReactionSet contained in this ChemModel.
     *
     * @param sor the ReactionSet to store in this model
     *
     * @see       #getReactionSet
     */
    @Override
    public void setReactionSet(IReactionSet sor) {
        this.setOfReactions = sor;
    }

    /**
     * Returns a String representation of the contents of this
     * IChemObject.
     *
     * @return String representation of content
     */
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer(64);
        buffer.append("ChemModel(");
        buffer.append(hashCode());
        if (getMoleculeSet() != null) {
            buffer.append(", ");
            buffer.append(getMoleculeSet().toString());
        }
        if (getCrystal() != null) {
            buffer.append(", ");
            buffer.append(getCrystal().toString());
        }
        if (getReactionSet() != null) {
            buffer.append(", ");
            buffer.append(getReactionSet().toString());
        }
        buffer.append(')');
        return buffer.toString();
    }

    /**
     * Clones this <code>ChemModel</code> and its content.
     *
     * @return  The cloned object
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        ChemModel clone = (ChemModel) super.clone();
        // clone the content
        if (setOfMolecules != null) {
            clone.setOfMolecules = (AtomContainerSet) setOfMolecules.clone();
        } else {
            clone.setOfMolecules = null;
        }
        if (setOfReactions != null) {
            clone.setOfReactions = (IReactionSet) setOfReactions.clone();
        } else {
            clone.setOfReactions = null;
        }
        if (crystal != null) {
            clone.crystal = (Crystal) crystal.clone();
        } else {
            clone.crystal = null;
        }
        if (ringSet != null) {
            clone.ringSet = (RingSet) ringSet.clone();
        } else {
            clone.ringSet = null;
        }
        return clone;
    }

    /**
     *  Called by objects to which this object has
     *  registered as a listener.
     *
     *@param  event  A change event pointing to the source of the change
     */
    @Override
    public void stateChanged(IChemObjectChangeEvent event) {}

    /**
     *{@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        if (setOfMolecules != null && !setOfMolecules.isEmpty()) return false;
        if (setOfReactions != null && !setOfReactions.isEmpty()) return false;
        if (ringSet != null && !ringSet.isEmpty()) return false;
        if (crystal != null && !crystal.isEmpty()) return false;
        return true;
    }
}
