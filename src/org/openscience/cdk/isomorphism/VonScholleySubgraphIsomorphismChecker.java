/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
 *
 *  Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.isomorphism;

import org.openscience.cdk.*;
import org.openscience.cdk.exception.*;
import java.util.*;

/**
 * This class checks whether a given molecular graph is contained as a subgraph in another 
 * given graph. The algorithm used is that described in SCH84.
 *
 * References:
 *   <a href="http://cdk.sf.net/biblio.html#SCH84">SCH84</a>
 *
 * @author     steinbeck
 * @created    24. Februar 2002
 * @keyword    subgraph
 * @keyword    similarity
 */
public class VonScholleySubgraphIsomorphismChecker 
{
	AtomContainer structure = null;
	
	public VonScholleySubgraphIsomorphismChecker()
	{
		
	}

	public void setTarget(AtomContainer ac)
	{
		this.structure = ac;
	}
	
	public boolean isSubgraph(AtomContainer substructure)
	{
		Atom atom = null;
		/* If the substructure is larger than the structure is cannot be
		 * contained in it 
		 */
		if (substructure.getAtomCount() > structure.getAtomCount()) return false;
		labelStructure(substructure);
		return false;
	}
	
	private void labelStructure(AtomContainer substructure)
	{
		Atom atom = null;
		Atom anotherAtom = null;
		for (int f = 0; f < structure.getAtomCount(); f++)
		{
			atom = structure.getAtomAt(f);
			atom.pointers = new Vector[0];
			atom.pointers[0] = new Vector();
			for (int g = 0; g < substructure.getAtomCount(); g++)
			{
				anotherAtom = substructure.getAtomAt(g); 
				if (anotherAtom.getSymbol().equals(atom.getSymbol()))
				{
					if (anotherAtom.getHydrogenCount() <= atom.getHydrogenCount())
					{
						atom.pointers[0].addElement(anotherAtom);
					}
				}
			}
		}
	}
}

