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

package org.openscience.cdk.tools;

import java.util.*;
import java.io.*;

public class SwissArmyKnife
{

	 public static boolean  debug = false; // minimum details
	 public static boolean  debug1 = false;  // more details
	 public static boolean  debug2 = false; // too many details
	 public static boolean  debug3 = false; // ridiculous details

	public static int INFINITY = 1000000;

	/** Lists a 2D array of int values to the System console */
	public static void printInt2D(int[][] contab)
	{
		String line;
		for (int f = 0; f < contab.length; f++)
		{
			line  = "";
			for (int g = 0; g < contab.length; g++)
			{
				line += contab[f][g] + " ";
			}
		}
	}


	public static int faculty(int i)
	{
		int faculty = 1;
		for (int f = 2; f <= i; f++)
		{
			faculty *= f;
		}
		return faculty;
	}
	
	/* Returns a String reporting the time passed between startTime and endTime, both given in 
	   milliseconds */

	public static String getDuration(long startTime, long endTime)
	{
		long diff = endTime - startTime;
		return getDuration(diff);		
	}
	
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
}
