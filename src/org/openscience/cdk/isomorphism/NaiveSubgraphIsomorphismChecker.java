/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2001  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.aromaticity.*;
import org.openscience.cdk.exception.*;
import java.util.*;

/**
 *  Checks is a given Atomcontainer is a subgraph of another given Atomcontainer
 *  by doing a naive atom-to-atom mapping
 *
 *@author     steinbeck
 *@created    24. Februar 2002
 *@keyword    subgraph
 *@keyword    isomorphism
 */
public class NaiveSubgraphIsomorphismChecker implements CDKConstants
{

	protected AtomContainer query = null;
	protected AtomContainer target = null;
	protected Hashtable mappedAtoms = null;

	protected static String queryIsNullError = "The query structure is not defined";
	protected static String targetIsNullError = "The target structure is not defined";
	
	/**
	 *  Constructor for the NaiveSubgraphIsomorphismChecker object
	 */
	public NaiveSubgraphIsomorphismChecker() 
	{
		mappedAtoms = new Hashtable();
	}


	/**
	 *  Gets the substructure attribute of the NaiveSubgraphIsomorphismChecker
	 *  object
	 *
	 *@return    The substructure value
	 */
	public boolean isSubstructure() throws java.lang.Exception
	{
		Atom qAtom = null;
		Atom tAtom = null;
		
		if (query == null) throw new Exception(queryIsNullError);
		if (target == null) throw new Exception(targetIsNullError);
		if (query.getAtomCount() > target.getAtomCount()) return false;
		
		/*
		* Now we know that we have a real problem here :-)
		* We do no further checking because we assume that we only get 
		* fingerprint filtered problems
		*/
		
		
		for (int f = 0; f < query.getAtomCount(); f++)
		{
			qAtom = query.getAtomAt(f);
			resetMappedFlags(query);
			resetMappedFlags(target);
			for (int g = 0; g < target.getAtomCount(); g++)
			{
				tAtom = target.getAtomAt(g);
				if(tAtom.getSymbol().equals(qAtom.getSymbol()))
				{
					if (qAtom.getHydrogenCount() <= tAtom.getHydrogenCount())
					{
						qAtom.flags[MAPPED] = true;
						tAtom.flags[MAPPED] = true;
						mappedAtoms.put(qAtom, tAtom);
						if (mapNeigbors(qAtom)) return true;
					}
				}
			}
		}
		return false;
		
	}
	
	protected boolean mapNeigbors(Atom qAtom)
	{
		Atom[] qNeighbors = query.getConnectedAtoms(qAtom);
		Atom tAtom = (Atom)mappedAtoms.get(qAtom);
		Atom[] tNeighbors = target.getConnectedAtoms(tAtom);
		return false;
	}

	protected void resetMappedFlags(AtomContainer ac)
	{
		for (int f = 0; f < ac.getAtomCount(); f++)
		{
			ac.getAtomAt(f).flags[MAPPED] = false;
		}
	}
	

	/**
	 *  Sets the query attribute of the NaiveSubgraphIsomorphismChecker object
	 *
	 *@param  query  The new query value
	 */
	public void setQuery(AtomContainer query)
	{
		this.query = query;
	}


	/**
	 *  Sets the target attribute of the NaiveSubgraphIsomorphismChecker object
	 *
	 *@param  target  The new target value
	 */
	public void setTarget(AtomContainer target)
	{
		this.target = target;
	}


	/**
	 *  Gets the query attribute of the NaiveSubgraphIsomorphismChecker object
	 *
	 *@return    The query value
	 */
	public AtomContainer getQuery()
	{
		return query;
	}


	/**
	 *  Gets the target attribute of the NaiveSubgraphIsomorphismChecker object
	 *
	 *@return    The target value
	 */
	public AtomContainer getTarget()
	{
		return target;
	}

}

