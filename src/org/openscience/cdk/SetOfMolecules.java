/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2004  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 */

package org.openscience.cdk;


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
 * @cdk.module core
 *
 * @cdk.keyword reaction
 * @cdk.keyword molecule
 */
public class SetOfMolecules extends SetOfAtomContainers {

    /**
     *  Adds an molecule to this container.
     *
     * @param  molecule  The molecule to be added to this container 
     */
    public void addMolecule(Molecule molecule) {
        super.addAtomContainer(molecule);
	/* notifyChanged() called in super.addAtomContainer() */
    }
    
    /**
     *  Adds all molecules in the SetOfMolecules to this container.
     *
     * @param  moleculeSet  The SetOfMolecules 
     */
    public void add(SetOfMolecules moleculeSet) {
        Molecule[] mols = moleculeSet.getMolecules();
        for (int i=0; i< mols.length; i++) {
            addMolecule(mols[i]);
        }
	/* notifyChanged() called in super.addAtomContainer() */
    }
    
    
    /**
     *  Returns the array of Molecules of this container.
     *
     * @return    The array of Molecules of this container 
     */
    public Molecule[] getMolecules() {
        Molecule[] result = new Molecule[super.getAtomContainerCount()];
        AtomContainer[] containers = super.getAtomContainers();
        for (int i=0; i<containers.length; i++) {
            result[i] = (Molecule)containers[i];
        }
        return result;
    }
    
    
    /**
     *  
     * Returns the Molecule at position <code>number</code> in the
     * container.
     *
     * @param  number  The position of the Molecule to be returned. 
     * @return         The Molecule at position <code>number</code> . 
     */
    public Molecule getMolecule(int number)
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
    
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("SetOfMolecules(");
        buffer.append(super.toString());
        buffer.append(")");
        return buffer.toString();
    }
    
}
