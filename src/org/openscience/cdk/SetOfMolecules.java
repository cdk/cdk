/*
 * 
 * $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2001  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, geelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
 * A set of molecules, for example those taking part in a reaction
 *
 * @keyword reaction
 * @keyword molecule
 */
public class SetOfMolecules extends ChemObject
{

	/**
	 *  Array of Molecules 
	 */
	protected Molecule[] molecules;
	
	/**
	 *  Number of Molecules contained by this container 
	 */
	protected int moleculeCount;

	/**
	 *  Amount by which the Molecules array grows when elements are added and
	 *  the array is not large enough for that. 
	 */
	protected int growArraySize = 5;


	/**
	 *  Constructs an empty SetOfMolecules
	 */
	public SetOfMolecules()   
	{
		moleculeCount = 0;
		molecules = new Molecule[growArraySize];
	}


	
	/**
	 *  Adds an molecule to this container 
	 *
	 * @param  molecule  The molecule to be added to this container 
	 */
	public void addMolecule(Molecule molecule)
	{
		if (moleculeCount + 1 >= molecules.length)
		{
			growMoleculeArray();
		}
		molecules[moleculeCount] = molecule;
		moleculeCount++;
	}


	/**
	 *  Returns the array of Molecules of this container 
	 *
	 * @return    The array of Molecules of this container 
	 */
	public Molecule[] getMolecules()
	{
		return molecules;
	}
	
	
	/**
	 *  
	 * Returns the Molecule at position <code>number</code> in the
	 * container
	 *
	 * @param  number  The position of the Molecule to be returned. 
	 * @return         The Molecule at position <code>number</code> . 
	 */
	public Molecule  getMolecule(int number)
	{
		return molecules[number];
	}
	
	
	/**
	 *  Grows the molecule array by a given size 
	 *
	 * @see    org.openscience.cdk.AtomContainer#growArraySize growArraySize 
	 */
	protected void growMoleculeArray()
	{
		growArraySize = molecules.length;
		Molecule[] newmolecules = new Molecule[molecules.length + growArraySize];
		System.arraycopy(molecules, 0, newmolecules, 0, molecules.length);
		molecules = newmolecules;
	}
	

	/**
	 * Returns the number of Molecules in this Container
	 *
	 * @return     The number of Molecules in this Container
	 */
	public int getMoleculeCount()
	{
		return this.moleculeCount;
	}

	
}
