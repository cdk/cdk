/* SwissArmyKnife.java
 *
 * $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2000  The JChemPaint project
 * 
 * Contact: steinbeck@ice.mpg.de
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

import java.util.*;
import java.io.*;
import org.openscience.cdk.*;


/** 
 * A set of utilities which did not really fit into any other category
 */
public class SwissArmyKnife
{

    public static boolean  debug = false; // minimum details

	public static int INFINITY = 1000000;


	/**
	 * Returns a string representation of a 2D int matrix
	 * for printing or listing to the console
	 *
	 * @param contab The 2D int matrix for which a string representation is to be generatred  
	 */
	public static String printInt2D(int[][] contab)
	{
		String line = "";
		for (int f = 0; f < contab.length; f++)
		{
			for (int g = 0; g < contab.length; g++)
			{
				line += contab[f][g] + " ";
			}
			line += "\n";
		}
		return line;
	}



	/**
	 * Calculates the faculty for a given integer
	 *
	 * @param   i The int value for which the faculty is to be returned  
	 * @return The faculty of i    
	 */
	public static int faculty(int i)
	{
		if (i > 1) return i * faculty(i - 1);
		return 1;
	}
	

	/**
	 * Returns a string reporting the time passed between startTime and endTime,
	 * both given in milliseconds, in hours, minutes, seconds and milliseconds
	 *
	 * @param   startTime  The start time in milliseconds
	 * @param   endTime  The end time in milliseconds
	 * @return  A human readable representation of a timespan given in milliseconds   
	 */
	public static String getDuration(long startTime, long endTime)
	{
		long diff = endTime - startTime;
		return getDuration(diff);		
	}
	

	/**
	 * Returns a String reporting the time passed during a given number of milliseconds.
	 *
	 * @param   diff A time span in milliseconds 
 	 * @return  A human readable representation of a timespan given in milliseconds    
	 */
	public static String getDuration(long diff)
	{
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(new Date(diff));
		StringBuffer s = new StringBuffer();
		if (calendar.get(Calendar.HOUR) > 1) s.append("hours: " + (calendar.get(Calendar.HOUR) - 1) + ", ");		
		if (calendar.get(Calendar.MINUTE) > 0) s.append("minutes: " + (calendar.get(Calendar.MINUTE)) + ", ");				
		if (calendar.get(Calendar.SECOND) > 0) s.append("seconds: " + (calendar.get(Calendar.SECOND)) + ", ");				
		if (calendar.get(Calendar.MILLISECOND) > 1) s.append("milliseconds: " + (calendar.get(Calendar.MILLISECOND)) + ", ");						
		s.append("total milliseconds: " + diff);
		return s.toString();		
	}

	/**
	 * Gets a Molecule and an array of element symbols. Counts how many of each of these 
	 * elements the molecule contains. Than it returns the elements followed by their 
	 * number as a string, i.e. C15H8N3.
	 *
	 * @param   mol   The Molecule to be searched
	 * @param   element  The array of element symbols
	 * @return     The element formula as a string
	 */
	public static String generateElementFormula(Molecule mol, String[] elements)
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

