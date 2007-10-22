/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.graph;

import java.util.Iterator;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.tools.LoggingTool;

/**
 * The permutation code here is based on a pseudo code example 
 * on a tutorial site created and maintained by Phillip P. Fuchs:
 * <a href="http://www.geocities.com/permute_it/pseudo2.html">http://www.geocities.com/permute_it/pseudo2.html</a>.
 * 
 *@author         steinbeck
 * @cdk.svnrev  $Revision$
 *@cdk.created    2005-05-04
 *@cdk.keyword    permutation
 */
public abstract class AtomContainerPermutor implements Iterator
{
	final static boolean debug = true;
	static int debugCounter = 0;
	int N, i, j;
	int[] bookkeeping;
	Object[] objects;
	
	private static LoggingTool logger = new LoggingTool(AtomContainerPermutor.class);

	AtomContainer atomContainer;
	
	public void setAtomContainer(AtomContainer ac)
	{
		this.atomContainer = ac;
	}

	public void initBookkeeping()
	{
		bookkeeping = new int[N + 1];
		for (int f = 0; f <= N ; f++)
		{
			bookkeeping[f] = f;	
		}
		i = 1;
		
	}
	
	public Object next()
	{
		bookkeeping[i] = bookkeeping[i] - 1;
		if (isOdd(i)) j = bookkeeping[i];
		else j = 0;
		swap(i, j);
		i = 1;
		while(bookkeeping[i] == 0)
		{
			bookkeeping[i] = i;
			i++;
		}
		return makeResult();	
	}
	
	public boolean hasNext()
	{
		if (i < N) return true;
		return false;
	}
	
	public void remove()
	{
		// stupid method. not implemented.	
	}
	
	void swap(int x, int y)
	{
		logger.debug("swapping order of " + x + " and " + y);
		Object o = objects[x];
		objects[x] = objects[y];
		objects[y] = o;
	}
	
	void initObjectArray()
	{
		//
	}
	
	AtomContainer makeResult()
	{
		return null;
	}
	
	/**
        * Returns true if this number is odd.
        */
        boolean isOdd(int x) 
	{
                return (x & 1 ) == 1;
        }
}

