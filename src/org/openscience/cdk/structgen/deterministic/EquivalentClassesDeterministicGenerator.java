/*  $Revision$ $Author$ $Date$    
 *
 *  Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.LoggingTool;

/**
 * An implementation of Faulons equivalent classes deterministic generator.
 *
 * @author      steinbeck
 * @cdk.created 2000-10-02
 */
public class EquivalentClassesDeterministicGenerator {
	
	LoggingTool logger = new LoggingTool(EquivalentClassesDeterministicGenerator.class);
	
	/* The initial, unbonded AtomContainer
	 * From this we construct a number of AtomContainers that contain the 
	 * initial heavy atoms with assigned hydrogen counts. 
	 */
	IAtomContainer baseAtomContainer = null;
	List graphs = null;
		
	public EquivalentClassesDeterministicGenerator()
	{
		Graph graph = new Graph();
		graphs = new ArrayList();
		graphs.add(graph);		
	}

	public void setAtomContainer(IAtomContainer ac) {
		baseAtomContainer  = ac;
		initGraph();
		logger.debug("Number of classes after initialization: ", ((Graph)graphs.get(0)).size());
	}	
	
	private void initGraph()
	{
		IAtomContainer ac = null;
		IAtom atom = null;
		Graph graph = (Graph)graphs.get(0);
		for (int f = 0; f < baseAtomContainer.getAtomCount(); f++)
		{
			ac = baseAtomContainer.getBuilder().newAtomContainer();
			atom = baseAtomContainer.getAtom(f);
			ac.addAtom(atom);
			ac.setProperty("class", new Integer(atom.getHydrogenCount()));
			graph.add(ac);
		}
		graph.partition();
		logger.debug("Number of initial distinct classes: ", graph.getNumberOfClasses());
	}
	
}
