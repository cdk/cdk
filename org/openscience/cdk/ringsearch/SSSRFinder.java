/* SSSRFinder.java
 *
 * $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2000  The CompChem project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
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


public class SSSRFinder
{

//	 public boolean debug = false; // minimum details
//	 public boolean debug1 = false;  // more details
//	 public boolean debug2 = false; // too many details
//	 public boolean debug3 = false; // hillarious details
//	
//	 public int ringCounter = 0;
//	 public boolean storeRings = true;

	private static final int PATH = 0;


	/**
	 *Finds the Smallest Set of Smallest Rings. 
	 *This is an implementation of the algorithm published in
	 *John Figueras, "Ring Perception Using Breadth-First Search", 
	 *J. Chem. Inf. Comput Sci. 1996, 36, 986-991.
	 *
	 * @param   molecule  
	 * @return     
	 */
	public  Ring[] findSSSR(Molecule molecule)
	{
		Ring[] sorf = new Ring[0];
		
		
		Atom smallest;
		int smallestDegree;
		Ring ring;
	
		//Two Vectors - as defined in the article. One to hold the
		//full set of atoms in the structure and on to store the numbers
		//of the nodes that have been trimmed away.
		//Furhter there is a Vector nodesN2 to store the number of N2 nodes
		Vector fullSet = new Vector();
		Vector trimSet = new Vector();
		Vector nodesN2 = new Vector();
		
		// load fullSet with the numbers of our atoms
		for (int f = 0; f < molecule.getAtomCount(); f++)
		{
			fullSet.addElement(molecule.getAtom(f));
		}
		
		
		do{
			//Add nodes of degree zero to trimset.
			//Also add nodes of degree 2 to nodesN2.
			//In the same run, check, which node has the lowest degree 
			//greater than zero.	
			smallestDegree = 7;
			smallest = null;
			for (int f = 0; f < molecule.getAtomCount(); f++)
			{
				Atom atom = molecule.getAtom(f);
				if (atom.getDegree() == 0)
				{
					if (!trimSet.contains(atom))
					{
						trimSet.addElement(atom);
					}
				}
				if (atom.getDegree() == 2)
				{
					nodesN2.addElement(atom);
				}
				if (atom.getDegree() < smallestDegree && atom.getDegree() > 0)
				{
					smallest = atom;
					smallestDegree = atom.getDegree();
				}
			}
			if (smallest == null )	break;	
				
			// If there are nodes of degree 1, trim them away
			if (smallestDegree == 1)
			{
				trim(smallest, molecule);
				trimSet.addElement(smallest);
			}
			
			// if there are nodes of degree 2, find out of which rings
			// they are part of.
			else if (smallestDegree == 2)
			{
			}
			
			// if there are nodes of degree 3
			else if (smallestDegree == 3)
			{
			}
			
		}while(trimSet.size() < fullSet.size());
	return sorf;	  
	}



	/**
	 * This routine is called 'getRing() in Figueras original article
	 * finds the smallest ring of which rootNode is part of.
	 *
	 * @param   rootNode  The Atom to be searched for the smallest ring it is part of
	 * @param   molecule  The molecule that contains the rootNode
	 * @return     The smallest Ring rootnode is part of
	 */
	public Ring findSRing(Atom rootNode, Molecule molecule)
	{
		Atom[] conAtoms;
		Atom node, neighbor, mAtom; 
		int source, mNumber, frontNode, neighborNumber; 
		/** OKatoms is Figueras nomenclature, giving the number of 
		    atoms in the structure */
		int OKatoms = molecule.getAtomCount();
		/** queue for Breadth First Search of this graph */
		Queue queue = new Queue();
		/** ringsset stores the smallest ring found and returns it */
		Ring ringset = new Ring();
		/* Initialize a path Vector for each node
		*/
		Vector path[] = new Vector[OKatoms];
		Vector intersection = new Vector();
		Vector ring = new Vector();

		for (int f = 0; f < OKatoms; f++){
//			path[f] = new Vector();		
			molecule.getAtom(f).pointers[PATH].removeAllElements();
		}
		try
		{
			// Initialize the queue with nodes attached to rootNode
			for (int f = 0; f < rootNode.getDegree(); f++){
				/* if the degree of the f-st neighbor of rootNode is greater 
				 than zero (i.e., it has not yet been deleted from the list)
				*/	
				conAtoms = molecule.getConnectedAtoms(rootNode);
				neighbor = conAtoms[f];
				if (neighbor.getDegree() > 0){
					// push the f-st node onto our FIFO queue	
					// after assigning rootNode as its source
//					neighbor.setSource(molecule.getAtomNumber(rootNode));
					queue.push(neighbor);
//					neighborNumber = molecule.getAtomNumber(neighbor);
					neighbor.pointers[PATH].addElement(rootNode);
					neighbor.pointers[PATH].addElement(neighbor);
//					path[neighborNumber].addElement(neighbor);
//					path[neighborNumber].addElement(rootNode);
				}
			}
			while (true && queue.size() > 0){	
				node = (Atom)queue.pop();
//				frontNode = molecule.getAtomNumber(node);
//				source = node.getSource();				  
				for (int f = 0; f < node.getDegree(); f++){
					conAtoms = molecule.getConnectedAtoms(node);
					mAtom = conAtoms[f];
//					mNumber = molecule.getAtomNumber(mAtom);
					if (mAtom != node.pointers[PATH].elementAt(node.pointers[PATH].size() - 2)){
						if (mAtom.pointers[PATH].size() > 0){
							intersection = getIntersection(node.pointers[PATH], mAtom.pointers[PATH]);
							if (intersection.size() == 1){
								// we have found a valid ring closure
								// now let's prepare the path to
								// return in tempAtomSet
//								if (debug){
//									System.out.println("Ring closure found at: " + mNumber);
//									System.out.println("Path of frontnode: " + path[frontNode].toString());
//									System.out.println("Path of m: " + path[mNumber].toString());
//								}
								ring = getUnion(node.pointers[PATH], mAtom.pointers[PATH]);
								return ring;
//								return prepareRing(ring, tempAtomSet);
							}
						}
						else { // if path[mNumber] is null
								   // update the path[mNumber]							
//								path[mNumber] = merge(path[mNumber], path[frontNode]);
								mAtom.pointers[PATH] = node.pointers[PATH];
								mAtom.pointers[PATH].addElement(mAtom);
								// now push the node m onto the queue
//								mAtom.setSource(frontNode);
								queue.push(mAtom);	
						}
					}
				}
			}
		}
		catch (Exception exc)
		{
			System.out.println(exc.toString());
		}
		return new Ring();
	}

									
	


		
	/**
	 * removes all bonds connected to the given atom leaving it with degree zero.
	 *
	 * @param   atom  The atom to be disconnecred
	 * @param   molecule  The molecule containing the atom
	 */
	 public void trim(Atom atom, Molecule molecule)
	 {
	 	for (int i = 0; i < molecule.getBondCount(); i++)
	 	{
			Bond bond = molecule.getBond(i);
			if (bond.contains(atom))
			{
				molecule.removeBond(i);
			}
	 	}
		// you are erased! Har, har, har.....  >8-)
	 }
	  
	private void initPath(Molecule molecule)
	{
	 	for (int i = 0; i < molecule.getAtomCount(); i++)
	 	{
			Atom atom = molecule.getAtom(i);
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
	public  Vector getIntersection(Vector vec1, Vector vec2){
		Vector is = new Vector();		
		for (int f = 0; f < vec1.size(); f++){
			if (vec2.contains((Integer)vec1.elementAt(f))) is.addElement((Integer)vec1.elementAt(f));	
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
	public  Vector getUnion(Vector vec1, Vector vec2){
		Vector is = (Vector)vec1.clone();
		for (int f = vec2.size()- 1; f > -1; f--){
			if (!vec1.contains((Integer)vec2.elementAt(f))) is.addElement((Integer)vec2.elementAt(f));	
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
	public  Vector merge(Vector vec1, Vector vec2){
		Vector result = (Vector)vec1.clone();
		for (int f = 0; f < vec2.size(); f++){
			result.addElement((Integer)vec2.elementAt(f))	;	
		}	
		return result;

	}
	

}