/* 
 * $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2001  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
 * 
 */

package org.openscience.cdk.tools;

import org.openscience.cdk.*;
import org.openscience.cdk.exception.*;
import java.util.*;


public class IsomorphismTester implements java.io.Serializable{

	int[] baseTable;		
	int[] sortedBaseTable;
	int[] compareTable;
	int[] sortedCompareTable;
	Molecule base = null;
	Molecule compare = null;

   public IsomorphismTester(){
   }
	
   
	public IsomorphismTester(Molecule mol) throws NoSuchAtomException
	{
		setBaseTable(mol);
	}
	
	
	public boolean isIsomorphic(Molecule mol1, Molecule mol2)  throws NoSuchAtomException
	{
		setBaseTable(mol1);
		return isIsomorphic(mol2);
	}

	public boolean isIsomorphic(Molecule mol2)  throws NoSuchAtomException
	{
		boolean found;
		Atom atom1 = null, atom2 = null;
		setCompareTable(mol2);
		for (int f = 0; f < sortedBaseTable.length; f++)
		{
			if (sortedBaseTable[f] != sortedCompareTable[f])
			{
//				System.out.println("Morgannumbers differ -> not isomorphic");
//				report();
				return false;
			}
		}

		for (int f = 0; f < baseTable.length; f++){
			found = false;
			for (int g = 0; g < compareTable.length; g++){
			 if (baseTable[f] == compareTable[g]){
				atom1 = base.getAtomAt(f);
				atom2 = compare.getAtomAt(g);
			 	if(!(atom1.getElementSymbol().equals(atom2.getElementSymbol())) && atom1.getHydrogenCount() == atom2.getHydrogenCount()) return false;
				found = true;
			 }
		 	}
		 	if (!found)
			{
//				System.out.println("Structures are not isomorphic");
//				report();
				return false;
			}
		}
//		System.out.println("Structures are isomorphic");
//		report();
		return true;
	}
	
	
	private void setBaseTable(Molecule mol) throws NoSuchAtomException
	{
		this.base = mol;
		this.baseTable = MorganNumbersTools.getMorganNumbers(base);
		sortedBaseTable = new int[baseTable.length]; 
		System.arraycopy(baseTable, 0, sortedBaseTable, 0, baseTable.length);
		Arrays.sort(sortedBaseTable);
	}
	

	private void setCompareTable(Molecule mol)  throws NoSuchAtomException
	{
		this.compare = mol;
		this.compareTable = MorganNumbersTools.getMorganNumbers(compare);
		sortedCompareTable = new int[compareTable.length]; 
		System.arraycopy(compareTable, 0, sortedCompareTable, 0, compareTable.length);
		Arrays.sort(sortedCompareTable);
		
	}
	
	
	public void report()
	{
		String s = "";
		for(int f = 0; f < sortedBaseTable.length; f++)
		{
			s += sortedBaseTable[f] + " ";
		}
		System.out.println(s);
		s = "";
		for(int f = 0; f < sortedCompareTable.length; f++)
		{
			s += sortedCompareTable[f] + " ";
		}
		System.out.println(s);
		
	}
}
