/* ElementFormula.java
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

package org.openscience.cdk.tools;

import org.openscience.cdk.*;



public class ElementFormula
{


	/**
	 * Gets a Molecule and an array of element symbols. Counts how many of each of these 
	 * elements the molecule contains. Than it returns the elements followed by their 
	 * number as a string, i.e. C15H8N3.
	 *
	 * @param   mol   The Molecule to be searched
	 * @param   element  The array of element symbols
	 * @return     The element formula as a string
	 */
	public static String getElementFormula(Molecule mol, String[] elements)
	{
		int num = elements.length;
		StringBuffer formula = new StringBuffer();
		int[] elementCount = new int[num];
		for (int i = 0; i < mol.getAtomCount(); i++)
		{
			String symbol = mol.getAtomAt(i).getElement().getSymbol();
			for (int j = 0; j < num; j++)
			{
				if (elements[j].equals(mol.getAtomAt(i).getElement().getSymbol()))
				{
					elementCount[j] ++;
				}
			}
		}
		for (int i = 0; i < num; i++)
		{
			formula.append(elements[i] + elementCount[i]);
		}
		return formula.toString();
	}
}
