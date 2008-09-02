/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2007  Christoph Steinbeck <steinbeck@users.sf.net>
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
package org.openscience.cdk;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;

/** 
 * A set of molecules, for example those taking part in a reaction.
 *
 * To retrieve the molecules from the set, there are two options:
 *
 * <pre>
 * Molecule[] mols = setOfMolecules.getMolecules();
 * for (int i=0; i < mols.length; i++) {
 *     Molecule mol = mols[i];
 * }
 * </pre>
 *
 * and
 *
 * <pre>
 * for (int i=0; i < setOfMolecules.getMoleculeCount(); i++) {
 *    Molecule mol = setOfMolecules.getMolecule(i);
 * }
 * </pre>
 *
 * @cdk.module data
 * @cdk.svnrev  $Revision$
 *
 * @cdk.keyword reaction
 * @cdk.keyword molecule
 */
public class MoleculeSet extends AtomContainerSet implements IMoleculeSet, Cloneable {

	/**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is incompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
	 * 
	 */
	private static final long serialVersionUID = -861287315770869699L;

	public MoleculeSet() {}
	
    /**
     *  Adds an molecule to this container.
     *
     * @param  molecule  The molecule to be added to this container 
     */
    public void addMolecule(org.openscience.cdk.interfaces.IMolecule molecule) {
        super.addAtomContainer(molecule);
	/* notifyChanged() called in super.addAtomContainer() */
    }
    
    /**
     *  Adds all molecules in the MoleculeSet to this container.
     *
     * @param  moleculeSet  The MoleculeSet 
     */
    public void add(IMoleculeSet moleculeSet) {
        for (IAtomContainer mol : moleculeSet.molecules()) {
            addAtomContainer(mol);
        }
    }

    public void setMolecules(IMolecule[] molecules)
    {
	    if (atomContainerCount > 0) removeAllAtomContainers();
        for (IMolecule molecule : molecules) {
            addMolecule(molecule);
        }
    }
    
    /**
     *  Returns the array of Molecules of this container.
     *
     * @return    The array of Molecules of this container 
     * @see #setMolecules
     */
    public Iterable<IAtomContainer> molecules() {
        return super.atomContainers();
    }
    
    
    /**
     *  
     * Returns the Molecule at position <code>number</code> in the
     * container.
     *
     * @param  number  The position of the Molecule to be returned. 
     * @return         The Molecule at position <code>number</code> . 
     */
    public org.openscience.cdk.interfaces.IMolecule getMolecule(int number)
    {
        return (Molecule)super.getAtomContainer(number);
    }
    
    
    /**
     * Returns the number of Molecules in this Container.
     *
     * @return     The number of Molecules in this Container
     */
    public int getMoleculeCount() {
        return super.getAtomContainerCount();
    }
	
	
	/**
	 *  Clones this MoleculeSet and its content.
	 *
	 *@return    the cloned object
	 */
	public Object clone() throws CloneNotSupportedException {
		MoleculeSet clone = (MoleculeSet)super.clone();
    for (int i = 0; i < atomContainerCount; i++) {
        clone.replaceAtomContainer(i, (IAtomContainer)atomContainers[i].clone());
		}
		return (Object) clone;
	}
    
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("MoleculeSet(");
        buffer.append(super.toString());
        buffer.append(')');
        return buffer.toString();
    }
    
    	/**
	 *  Called by objects to which this object has
	 *  registered as a listener.
	 *
	 *@param  event  A change event pointing to the source of the change
	 */
	public void stateChanged(IChemObjectChangeEvent event)
	{
		notifyChanged(event);
	}    
}
