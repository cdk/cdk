/* RandomGenerator.java
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

package org.openscience.cdk.structgen;

import org.openscience.cdk.*;
import java.util.Vector;

/**
 * RandomGenerator is a generator of Constitutional Isomers. It needs to be 
 * provided with a starting constitution and it makes random moves in 
 * constitutional space from there. 
 * This generator was first suggested by
 * Faulon, J.-L. Journal of Chemical Information and Computer Sciences 1996, 36, 731-740. 
 * 
 **/
 
public class RandomGenerator
{

	Molecule proposedMolecule;
	Molecule molecule;

	Vector oldBonds;
	Vector newBonds;

	/**
	 *
	 *
	 */
	public RandomGenerator()
	{
		oldBonds = new Vector();
		newBonds = new Vector();
	}


	/**
	 *
	 *
	 * @param   molecule  
	 */
	public RandomGenerator(Molecule molecule)
	{
		this ();
		setMolecule(molecule);
	}




	/**
	 *
	 *
	 * @return     
	 */
	public Molecule proposeStructure()
	{
		return proposedMolecule;
	}


	/**
	 *
	 *
	 * @return     
	 */
	public Molecule getNextStructure()
	{
		return new Molecule();
	}
	
	/**
	 * Randomly breaks a bond and forms another to mutate the structure
	 * The rules for this method are described in "Faulon, JCICS 1996, 36, 731"
	 *
	 */
	protected void mutate()
	{
		newBonds.removeAllElements();
		oldBonds.removeAllElements();
		int nrOfAtoms = molecule.getAtomCount();
		int x1, x2, y1, y2, a11, a12, a22, a21, b11, min, max;

		Atom ax1 = null, ax2 = null, ay1 = null, ay2  = null;
		Bond b1 = null, b2 = null, b3 = null, b4 = null;

		do
		{
			/* Randomly choose four distinct atoms */
			do
			{
				x1 = (int)(Math.random() * nrOfAtoms);
				x2 = (int)(Math.random() * nrOfAtoms);
				y1 = (int)(Math.random() * nrOfAtoms);
				y2 = (int)(Math.random() * nrOfAtoms);
			}
			while (!(x1 != x2 && x1 != y1 && x1 != y2 && x2 != y1 &&
				x2 != y2 && y1 != y2));
			ax1 = molecule.getAtomAt(x1);
			ay1 = molecule.getAtomAt(y1);
			ax2 = molecule.getAtomAt(x2);
			ay2 = molecule.getAtomAt(y2);
			/* Get four bonds for these four atoms */
			try
			{
				b1 = molecule.getBond(ax1, ay1);
				a11 = b1.getOrder();
			}
			catch(Exception exc)
			{
				a11 = 0;
			}
			try
			{
				b2 = molecule.getBond(ax1, ay2);
				a12 = b2.getOrder();
			}
			catch(Exception exc)
			{
				a12 = 0;
			}
			try
			{
				b3 = molecule.getBond(ax2, ay2);
				a22 = b3.getOrder();
			}
			catch(Exception exc)
			{
				a22 = 0;
			}
			try
			{
				b4 = molecule.getBond(ax2, ay1);									
				a21 = b4.getOrder();
			}
			catch(Exception exc)
			{
				a21 = 0;
			}
			
			/* Compute the range for b11 (see Faulons formulae for details) */
			int[] cmax = {0, a11 - a22, a11 + a12 - 3, a11 + a21 - 3};
			int[] cmin = {3, a11 + a12, a11 + a21, a11 - a22 + 3};
			min = max(cmax);
			max = min(cmin);
			/* Randomly choose b11 != a11 in the range max > r > min */
			b11 = ((int)(Math.random() * (max - min))) + min;
		}
		while (b11 == a11);
		if (b11 == 0)
		{
			molecule.removeBond(b1);
		}
		else
		{
			b1.setOrder(b11);
		}
		
		if (a11 + a12 - b11 == 0) 
		{
			molecule.removeBond(b2);			
		}
		else
		{
			b2.setOrder(a11 + a12 - b11);
		}

		if (a22 - a11 + b11 == 0) 
		{
			molecule.removeBond(b3);			
		}
		else
		{
			b3.setOrder(a22 - a11 + b11);
		}

		if (a11 + a21 - b11 == 0) 
		{
			molecule.removeBond(b4);			
		}
		else
		{
			b4.setOrder(a11 + a21 - b11);
		}		
	}

	/**
	* Analog of Math.max that returns the largest int value in an array of ints
	**/

	/**
	 *
	 *
	 * @param   values  
	 * @return     
	 */
	protected int max(int[] values)
	{
		int max = values[0];
		for (int f = 0; f < values.length; f++)
			if (values[f] > max)
				max = values[f];
			return max;
	}

	/**
	* Analog of Math.max that returns the largest int value in an array of ints
	**/

	/**
	 *
	 *
	 * @param   values  
	 * @return     
	 */
	protected int min(int[] values)
	{
		int min = values[0];
		for (int f = 0; f < values.length; f++)
			if (values[f] < min)
				min = values[f];
			return min;
	}

	
	/** Assigns the set of Nodes based on which the structure generation is performed */

	/**
	 *
	 *
	 * @param   molecule  
	 */
	public void setMolecule(Molecule molecule)
	{
		this.molecule = molecule;	
	}
	

	/**
	 *
	 *
	 * @param   bm  
	 * @return     
	 */
	protected boolean structureConnected(int[][] bm)
	{
		boolean[] visited = new boolean[bm.length];
		visited[0] = true;
		for (int i = 0; i < bm.length; i++)
		{
			if (!visited[i])
			{
				for (int j = 0; j < bm.length; j++)
				{
					 
				}
			}
		}
		return true;
	}	
}
