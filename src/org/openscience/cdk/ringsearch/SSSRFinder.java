/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 * 
 */
package org.openscience.cdk.ringsearch;


import java.util.*;
import org.openscience.cdk.*;

/**
 * Finds the Smallest Set of Smallest Rings. 
 * This is an implementation of the algorithm published in
 * FIG96.
 *
 * References:
 *   <a href="http://cdk.sf.net/biblio.html#FIG96">FIG96</a>
 *
 * @keyword smallest-set-of-rings
 */
public class SSSRFinder
{

	public boolean debug = false; // minimum details
	int trimCounter = 0;
	private static final int PATH = 0;

	/**
	 * Finds the Smallest Set of Smallest Rings.
	 *
	 * @param   molecule the molecule to be searched for rings 
	 * @return a RingSet containing the rings in molecule    
	 */
	public  RingSet findSSSR(Molecule mol)
	{
		Bond brokenBond = null;
		RingSet sssr = new RingSet();
		Molecule molecule = new Molecule();
		molecule.add(mol);
		Atom smallest;
		int smallestDegree, nodesToBreakCounter, degree;
		Atom[] rememberNodes;
		Ring ring;
	
		//Two Vectors - as defined in the article. One to hold the
		//full set of atoms in the structure and on to store the numbers
		//of the nodes that have been trimmed away.
		//Furhter there is a Vector nodesN2 to store the number of N2 nodes
		Vector fullSet = new Vector();
		Vector trimSet = new Vector();
		Vector nodesN2 = new Vector();
		
		initPath(molecule);
		if (debug) System.out.println("molecule.getAtomCount(): " + molecule.getAtomCount());				
		// load fullSet with the numbers of our atoms
		for (int f = 0; f < molecule.getAtomCount(); f++)
		{
			fullSet.addElement(molecule.getAtomAt(f));
		}
		if (debug) System.out.println("fullSet.size(): " + fullSet.size());						
		
		do{
			//Add nodes of degree zero to trimset.
			//Also add nodes of degree 2 to nodesN2.
			//In the same run, check, which node has the lowest degree 
			//greater than zero.	
			smallestDegree = 7;
			smallest = null;
			nodesN2.removeAllElements();
			for (int f = 0; f < molecule.getAtomCount(); f++)
			{
				Atom atom = molecule.getAtomAt(f);
				degree = molecule.getBondCount(atom);
				if (degree == 0)
				{
					if (!trimSet.contains(atom))
					{
						if (debug) System.out.println("Atom of degree 0");
						trimSet.addElement(atom);
					}
				}
				if (degree == 2)
				{
					nodesN2.addElement(atom);
				}
				if (degree < smallestDegree && degree > 0)
				{
					smallest = atom;
					smallestDegree = degree;
				}
			}
			if (smallest == null )	break;	
				
			// If there are nodes of degree 1, trim them away
			if (smallestDegree == 1)
			{
				trimCounter ++;
				trim(smallest, molecule);
				trimSet.addElement(smallest);
			}
			
			// if there are nodes of degree 2, find out of which rings
			// they are part of.
			else if (smallestDegree == 2)
			{
				rememberNodes = new Atom[nodesN2.size()];
				nodesToBreakCounter = 0;
				for (int f = 0; f < nodesN2.size(); f++)
				{
					ring = getRing((Atom)nodesN2.elementAt(f), molecule);
					if (ring != null)
					{
						// check, if this ring already is in SSSR
						if (!sssr.ringAlreadyInSet(ring))
						{
							sssr.addElement(ring);
							rememberNodes[nodesToBreakCounter] = (Atom)nodesN2.elementAt(f);
							nodesToBreakCounter++;
						}
					}
				}
				if (nodesToBreakCounter == 0)
				{
					nodesToBreakCounter = 1;
					rememberNodes[0] = (Atom)nodesN2.elementAt(0);
				}
				for (int f = 0; f < nodesToBreakCounter; f++){
					breakBond(rememberNodes[f], molecule);
				}
				if (brokenBond != null)
				{
					molecule.addBond(brokenBond);
					brokenBond = null;
				}
			}
			// if there are nodes of degree 3
			else if (smallestDegree == 3)
			{
				ring = getRing(smallest, molecule);
				if (ring != null)
				{
					
					// check, if this ring already is in SSSR
					if (!sssr.ringAlreadyInSet(ring))
					{
						sssr.addElement(ring);
					}
					brokenBond = checkEdges(ring, molecule);
					molecule.removeElectronContainer(brokenBond);
				}
			}
		}
		while(trimSet.size() < fullSet.size());
		if (debug)
		{
			System.out.println("fullSet.size(): " + fullSet.size());				
			System.out.println("trimSet.size(): " + trimSet.size());		
			System.out.println("trimCounter: " + trimCounter);
		}
	return sssr;	  
	}



	/**
	 * This routine is called 'getRing() in Figueras original article
	 * finds the smallest ring of which rootNode is part of.
	 *
	 * @param   rootNode  The Atom to be searched for the smallest ring it is part of
	 * @param   molecule  The molecule that contains the rootNode
	 * @return     The smallest Ring rootnode is part of
	 */
	private Ring getRing(Atom rootNode, Molecule molecule)
	{
		Atom node, neighbor, mAtom; 
		Atom[] neighbors, mAtoms;
		/** OKatoms is Figueras nomenclature, giving the number of 
		    atoms in the structure */
		int OKatoms = molecule.getAtomCount();
		/** queue for Breadth First Search of this graph */
		Queue queue = new Queue();
		/* Initialize a path Vector for each node
		*/
		Vector pfad1,pfad2,pfad3,pfad4,pfad5;
		Vector path[] = new Vector[OKatoms];
		Vector intersection = new Vector();
		Vector ring = new Vector();
		for (int f = 0; f < OKatoms; f++)
		{
			path[f] = new Vector();		
			molecule.getAtomAt(f).pointers[PATH].removeAllElements();
		}
		// Initialize the queue with nodes attached to rootNode
		neighbors = molecule.getConnectedAtoms(rootNode);
		for (int f = 0; f < neighbors.length; f++){
			//if the degree of the f-st neighbor of rootNode is greater 
			//than zero (i.e., it has not yet been deleted from the list)
			neighbor = neighbors[f];
			// push the f-st node onto our FIFO queue	
			// after assigning rootNode as its source
			queue.push(neighbor);
			neighbor.pointers[PATH].addElement(rootNode);
			neighbor.pointers[PATH].addElement(neighbor);
		}
		while (queue.size() > 0){	
			node = (Atom)queue.pop();
			mAtoms = molecule.getConnectedAtoms(node);
			for (int f = 0; f < mAtoms.length; f++){
				mAtom = mAtoms[f];
				if (mAtom != node.pointers[PATH].elementAt(node.pointers[PATH].size() - 2)){
					if (mAtom.pointers[PATH].size() > 0){
						intersection = getIntersection(node.pointers[PATH], mAtom.pointers[PATH]);
						if (intersection.size() == 1){
							// we have found a valid ring closure
							// now let's prepare the path to
							// return in tempAtomSet
							if (debug){
								System.out.println("path1  "+node.pointers[PATH].toString());
								System.out.println("path2  "+mAtom.pointers[PATH].toString());
								System.out.println("rootNode  "+rootNode);
								System.out.println("ring   "+ ring.toString());
							}
							ring = getUnion(node.pointers[PATH], mAtom.pointers[PATH]);
							return prepareRing(ring,molecule);
						}
					}
					else 
					{   
						// if path[mNumber] is null
					    // update the path[mNumber]							
						pfad2 = node.pointers[PATH];
						mAtom.pointers[PATH] = (Vector)node.pointers[PATH].clone();
						mAtom.pointers[PATH].addElement(mAtom);
						pfad1 = mAtom.pointers[PATH];
						// now push the node m onto the queue
						queue.push(mAtom);	
					}
				}
			}
		}
		return null;
	}

									
	
	

	/**
	 * Returns the ring that is formed by the atoms in the given vector. 
	 *
	 * @param   vec  The vector that contains the atoms of the ring
	 * @param   mol  The molecule this ring is a substructure of
	 * @return     The ring formed by the given atoms
	 */
	private Ring prepareRing(Vector vec, Molecule mol)
	{
		// add the atoms in vec to the new ring
		int atomCount = vec.size();
		Ring ring = new Ring(atomCount);
		Atom[] atoms = new Atom[atomCount];
		vec.copyInto(atoms);
		ring.setAtoms(atoms);
		// add the bonds in mol to the new ring
		try {
			Bond b;
			for (int i = 0; i < atomCount - 1; i++) {
				b = mol.getBond(atoms[i], atoms[i + 1]);
				if (b != null) {
				    ring.addBond(b);
				} else {
				    System.out.println("This should not happen.")
				} 
			}
			b = mol.getBond(atoms[0], atoms[atomCount - 1]);
			if (b != null) {
				ring.addBond(b);
			} else {
				System.out.println("This should not happen either.")
			} 
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
		}
//		System.out.println("found Ring  "+ ring);
		return ring;
	}
		
	/**
	 * removes all bonds connected to the given atom leaving it with degree zero.
	 *
	 * @param   atom  The atom to be disconnecred
	 * @param   molecule  The molecule containing the atom
	 */
	 private void trim(Atom atom, Molecule molecule) {
        Bond[] bonds = molecule.getConnectedBonds(atom);
	 	for (int i = 0; i < bonds.length; i++) {
            molecule.removeElectronContainer(bonds[i]);
	 	}
		// you are erased! Har, har, har.....  >8-)
	 }
	  

	/**
	 * initializes a path vector in every Atom of the given molecule
	 *
	 * @param   molecule  The given molecule
	 */
	private void initPath(Molecule molecule)
	{
	 	for (int i = 0; i < molecule.getAtomCount(); i++)
	 	{
			Atom atom = molecule.getAtomAt(i);
			atom.pointers = new Vector[1];
			atom.pointers[PATH] = new Vector();
	 	}		
	}

	/**
	 * Returns a Vector that contains the intersection of Vectors vec1 and vec2
	 *
	 * @param   vec1   The first vector
	 * @param   vec2   The second vector
	 * @return     
	 */
	private  Vector getIntersection(Vector vec1, Vector vec2){
		Vector is = new Vector();		
		for (int f = 0; f < vec1.size(); f++){
			if (vec2.contains((Atom)vec1.elementAt(f))) is.addElement((Atom)vec1.elementAt(f));	
		}	
		return is;
	}	

	/**
	 * Returns a Vector that contains the union of Vectors vec1 and vec2
	 *
	 * @param   vec1  The first vector
	 * @param   vec2  The second vector
	 * @return     
	 */
	private  Vector getUnion(Vector vec1, Vector vec2){
		Vector is = (Vector)vec1.clone();
		for (int f = vec2.size()- 1; f > -1; f--){
			if (!vec1.contains((Atom)vec2.elementAt(f))) is.addElement((Atom)vec2.elementAt(f));	
		}	
		return is;
	}	

	/**
	 * merges two vectors into one
	 *
	 * @param   vec1  The first vector
	 * @param   vec2  The second vector
	 * @return     
	 */
	private  Vector merge(Vector vec1, Vector vec2){
		Vector result = (Vector)vec1.clone();
		for (int f = 0; f < vec2.size(); f++){
			result.addElement((Atom)vec2.elementAt(f))	;	
		}	
		return result;

	}
	
	/**
	 * Eliminates one bond of this atom from the molecule
	 *
	 * @param   atom  The atom one bond is eliminated of
	 * @param   molecule  The molecule that contains the atom
	 */
	private void breakBond(Atom atom, Molecule molecule)
	{
        Bond[] bonds = molecule.getBonds();
		for (int i = 0; i < bonds.length; i++)
		{
			if (bonds[i].contains(atom))
			{
				molecule.removeElectronContainer(bonds[i]);
				break;
			}
		}
	}


	/**
	 * Selects an optimum edge for elimination in structures without N2 nodes.
	 *
     * <p>This might be severely broken! Would have helped if there was an
     * explanation of how this algorithm worked.
     *
	 * @param   ring  
	 * @param   mol  
	 * @return     
	 */
	private Bond checkEdges(Ring ring, Molecule molecule)
	{
		Ring r1, r2;
		RingSet ringSet = new RingSet();
		Bond bond;
		int minMaxSize = Integer.MAX_VALUE;
		int minMax = 0;
		System.out.println(molecule);
        Bond[] bonds = ring.getBonds();
		for (int i = 0; i < bonds.length; i++)
		{
			bond = bonds[i];
			molecule.removeElectronContainer(bond);
			r1 = getRing(bond.getAtomAt(0),molecule);
			r2 = getRing(bond.getAtomAt(1),molecule);
			System.out.println("checkEdges: " + bond);
			if (r1.getAtomCount() > r2.getAtomCount())
			{
				ringSet.addElement(r1);
			}
			else
			{
				ringSet.addElement(r2);
			}
			molecule.addBond(bond);
		}
		for (int i = 0; i < ringSet.size(); i++)
		{
			if (((Ring)ringSet.elementAt(i)).getBondCount() < minMaxSize)
			{
				minMaxSize = ((Ring)ringSet.elementAt(i)).getBondCount();
				minMax = i;
			}
		}
		return (Bond)ring.getElectronContainerAt(minMax);
	}
	
	
}








