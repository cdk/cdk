/*  $RCSfile$    
 *  $Author$    
 *  $Date$    
 *  $Revision$
 *
 *  Copyright (C) 2002-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.ringsearch;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import java.util.Vector;

/**
* Implementation of a Path as needed by {@cdk.cite HAN96}.
 *
 * @cdk.module standard
 * @cdk.svnrev  $Revision$
 *
 * @cdk.keyword graph, path
 *
 * @author     steinbeck
 * @cdk.created    2002-02-28
 */
@TestClass("org.openscience.cdk.ringsearch.PathTest")
public class Path extends Vector
{

    private static final long serialVersionUID = -1086494171897189629L;

    /**
	 *  Constructs an empty path
	 */
	public Path()
	{
		super();
	}


	/**
	 *  Constructs a new Path with two Atoms
	 *
	 * @param  atom1  first atom in the new path
	 * @param  atom2  second atom in the new path
	 */
	public Path(IAtom atom1, IAtom atom2)
	{
		super();
		add(atom1);
		add(atom2);
	}


	/**
	 *  Joins two paths. The joint point is given by an atom
	 *  which is shared by the two pathes.
	 *
	 * @param  path1  First path to join
	 * @param  path2  Second path to join
	 * @param  atom   The atom which is the joint point
	 * @return        The newly formed longer path
	 */
    @TestMethod("testJoin")
    public static Path join(Path path1, Path path2, IAtom atom)
	{
		Path newPath = new Path();
		Path tempPath = new Path();
		if (path1.firstElement() == atom)
		{
			path1.revert();
		}
		newPath.addAll(path1);
		if (path2.lastElement() == atom)
		{
			path2.revert();
		}
		tempPath.addAll(path2);
		tempPath.remove(atom);
		newPath.addAll(tempPath);
		return newPath;
	}

    @TestMethod("testGetIntersectionSize")
    public int getIntersectionSize(Path other)
	{
		IAtom a1, a2;
		int iSize = 0;
		for (int i = 0; i < size(); i++)
		{
			a1 = (IAtom)elementAt(i);
			for (int j = 0; j < other.size(); j++)
			{
				a2 = (IAtom)other.elementAt(j);
				if (a1 == a2) iSize++;
			}
		}
		return iSize;
	}
	
	private void revert()
	{
		Object o = null;
		int size = size();
		int i = (int)(size / 2);
		for (int f = 0; f < i; f++)
		{  
			o = elementAt(f);
			setElementAt(elementAt(size - f -1), f);
			setElementAt(o, size - f - 1);
		}
	}
	
	public String toString(IAtomContainer ac)
	{
		String s = "Path of length " + size() + ": ";
		try
		{
			for (int f = 0; f < size(); f++)
			{
				s += ac.getAtomNumber((IAtom)elementAt(f)) + " ";
			}
		}
		catch(Exception exc)
		{
//			logger.error(exc);
			s += "Could not create a string representaion of this path";	
		}
		return s;
	}
}

