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

import java.util.Vector;

import org.openscience.cdk.AtomContainer;
/**
 *  An implementation of Faulons equivalent classes deterministic generator 
 *
 * @author     steinbeck
 * @cdk.created    2000-10-02
 */
public class EquivalentClassesDeterministicGenerator
{
	/* The initial, unbonded AtomContainer
	 * From this we construct a number of AtomContainers that contain the 
	 * initial heavy atoms with assigned hydrogen counts. 
	 */
	AtomContainer baseAtomContainer = null;
	Vector graphs = null;
		
	public EquivalentClassesDeterministicGenerator()
	{
		Graph graph = new Graph();
		graphs = new Vector();
		graphs.addElement(graph);		
	}

	public void setAtomContainer(AtomContainer ac)
	{
		baseAtomContainer  = ac;
		initGraph();
		System.out.println("Number of classes after initialization: " + ((Graph)graphs.elementAt(0)).size());
	}	
	
	private void initGraph()
	{
		AtomContainer ac = null;
		org.openscience.cdk.interfaces.IAtom atom = null;
		Graph graph = (Graph)graphs.elementAt(0);
		for (int f = 0; f < baseAtomContainer.getAtomCount(); f++)
		{
			ac = new org.openscience.cdk.AtomContainer();
			atom = baseAtomContainer.getAtomAt(f);
			ac.addAtom(atom);
			ac.setProperty("class", new Integer(atom.getHydrogenCount()));
			graph.addElement(ac);
		}
		graph.partition();
		System.out.println("Number of initial distinct classes: " + graph.getNumberOfClasses());
	}
	
	 
}

