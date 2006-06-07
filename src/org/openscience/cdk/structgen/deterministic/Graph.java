/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.structgen.deterministic;

import java.util.Hashtable;
import java.util.Vector;

import org.openscience.cdk.AtomContainer;
/**
 *  An implementation of Faulons equivalent classes deterministic generator 
 *
 * @author     steinbeck
 * @cdk.created    2000-10-02
 */
public class Graph extends Vector
{
    private static final long serialVersionUID = 1087357022768386719L;
    
    private Hashtable classes = null;
	
	public Graph()
	{
		super();
		classes = new Hashtable();
	}
	
	public void partition()
	{
		AtomContainer ac = null;
		Integer eClass = null;
		classes.clear();
		for (int f = 0; f < size(); f++)
		{
			ac = (AtomContainer)elementAt(f);
			eClass = (Integer)ac.getProperty("class");
			if (!classes.containsKey(eClass))
			{
				/* Create a new Vector for this non-existing class */
				classes.put(eClass, new Vector());	
			}
			else
			{
				((Vector)classes.get(eClass)).addElement(ac);	
			}
		}
		
	}
	
	public int getNumberOfClasses()
	{
		return classes.size();
		
	}
	
}

