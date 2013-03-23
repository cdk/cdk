/*
 *  Copyright (C) 1997-2012  The Chemistry Development Kit (CDK) project
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
 */
package org.openscience.cdk.graph;

import java.util.Iterator;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * This class iterates through all possible permutations of the atom order in a
 * given atom container. These permutations ensure that the results of an
 * algorithm are independent of atom order.
 * <p/>
 * <pre>{@code
 *
 * IAtomContainer        container = ...;
 * AtomContainerPermutor permutor  = new AtomContainerAtomPermutor(container);
 *
 * while (permutor.hasNext()) {
 *     IAtomContainer permutation = permutor.next();
 * }
 *
 * }</pre>
 * <p/>
 * The permutation code here is based on a tutorial by Phillip Paul Fuchs
 * {@cdk.cite pfuchs}.
 *
 * @author         steinbeck
 * @see            <a href="http://www.oocities.org/permute_it/pseudo2.html">
 *                 Phillip P. Fuchs Tutorial</a>
 * @cdk.githash
 * @cdk.created    2005-05-04
 * @cdk.keyword    permutation
 */
public class AtomContainerAtomPermutor extends AtomContainerPermutor
{
	
	public AtomContainerAtomPermutor(IAtomContainer ac)
	{
		setAtomContainer(ac);
		N = atomContainer.getAtomCount();
		initBookkeeping();
		initObjectArray();
	}
	
	public void initObjectArray()
	{
		Iterator<IAtom> atoms = atomContainer.atoms().iterator();
		objects = new Object[atomContainer.getAtomCount()];
		int count = -1;
		while (atoms.hasNext())
		{
			objects[++count] = atoms.next();	
		}
	}
	
	IAtomContainer makeResult()
	{
		IAtom[] atoms = new IAtom[objects.length];
		for (int f = 0; f < objects.length; f++)
		{
			atoms[f] = (IAtom) objects[f];
		}
		IAtomContainer ac = atomContainer.getBuilder().newInstance(IAtomContainer.class,atomContainer);
		ac.setAtoms(atoms);
		IAtomContainer clone = null;
		try {
			clone = (IAtomContainer)ac.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return clone;
	}
	
}

