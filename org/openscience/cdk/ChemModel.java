/* ChemModel.java
 * 
 * $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2000  The CompChem project
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


import java.util.*;
import org.openscience.cdk.tools.*;


/** An object containig multiple SetOfMolecules and 
  * the other lower level concepts like rings, sequences, 
  * fragments, etc.
  */
public class ChemModel extends ChemObject
{

	/**
	 *  SetOfMolecules 
	 */
	protected SetOfMolecules setOfMolecules;
	
	protected RingSet ringSet;

	/**
	 *  Constructs an empty SetOfMolecules
	 */
	public ChemModel()   
	{
		setOfMolecules = new SetOfMolecules();
	}



	/**
	 * Puts all the Molecules of this container together in one AtomCcntainer.
	 *
	 * @return  The AtomContainer with all the Molecules of this container   
	 */
	public AtomContainer getAllInOneContainer()
	{
		AtomContainer ac;
		ac = new AtomContainer();
		for (int i = 0; i < setOfMolecules.getMoleculeCount(); i++)
		{
			ac.add(setOfMolecules.getMolecule(i));
		}
		return ac;
	}
	
	

	/**
	 * Partitions a given AtomContainer into Molecules that are not connected
	 * to each other and stores each of them as a Molecule in the SetOfMolecules
	 * of this container. 
	 *
	 * @param   ac   The AtomContainer to be partitioned
	 * @exception   Exception  
	 */
	public void partitionIntoMolecules(AtomContainer ac) throws Exception
	{
		SetOfMolecules newSet = new SetOfMolecules();
		Vector molecules = ConnectivityChecker.partitionIntoMolecules(ac);
		for (int i = 0; i < molecules.size(); i++)
		{
			newSet.addMolecule((Molecule)molecules.elementAt(i));
		}	
		setSetOfMolecules(newSet);
	}
	
	/**
	 * Returns the SetOfMolecules of this ChemModel
	 *
	 * @return   The SetOfMolecules of this ChemModel 
	 */
	public SetOfMolecules getSetOfMolecules()
	{
		return this.setOfMolecules;
	}


	/**
	 * Sets the SetOfMolecules of this ChemModel
	 *
	 * @param   setOfMolecules  
	 */
	public void setSetOfMolecules(SetOfMolecules setOfMolecules)
	{
		this.setOfMolecules = setOfMolecules;
	}

	

	/**
	 * Returns the RingSet of this ChemModel
	 *
	 * @return     
	 */
	public RingSet getRingSet()
	{
		return this.ringSet;
	}


	/**
	 * Sets the RingSet of this ChemModel
	 *
	 * @param   ringSet  
	 */
	public void setRingSet(RingSet ringSet)
	{
		this.ringSet = ringSet;
	}
}
	
