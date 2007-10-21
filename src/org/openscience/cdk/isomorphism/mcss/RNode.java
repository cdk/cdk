/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2002-2007  Stephane Werner <mail@ixelis.net>
 *  
 * This code has been kindly provided by Stephane Werner 
 * and Thierry Hanser from IXELIS mail@ixelis.net.
 *  
 * IXELIS sarl - Semantic Information Systems
 *               17 rue des C?dres 67200 Strasbourg, France
 *               Tel/Fax : +33(0)3 88 27 81 39 Email: mail@ixelis.net
 *
 * CDK Contact: cdk-devel@lists.sf.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.isomorphism.mcss;

import java.util.BitSet;

/**
 *  Node of the resolution graphe (RGraph) An RNode represents an association
 *  betwwen two edges of the source graphs G1 and G2 that are compared. Two
 *  edges may be associated if they have at least one common feature. The
 *  association is defined outside this class. The node keeps tracks of the ID
 *  of the mapped edges (in an RMap), of its neighbours in the RGraph it belongs
 *  to and of the set of incompatible nodes (nodes that may not be along with
 *  this node in the same solution)
 *
 * @author      Stephane Werner from IXELIS mail@ixelis.net
 * @cdk.created 2002-07-17
 * @cdk.module  standard
 * @cdk.svnrev  $Revision$
 */
public class RNode
{
	// G1/G2 mapping
	RMap rMap = null;

	// set of neighbour nodes in the RGraph
	BitSet extension = null;

	// set of incompatible nodes in the RGraph
	BitSet forbidden = null;


	/**
	 *  Constructor for the RNode object
	 *
	 *@param  id1  number of the bond in the graphe 1
	 *@param  id2  number of the bond in the graphe 2
	 */
	public RNode(int id1, int id2)
	{
		rMap = new RMap(id1, id2);
		extension = new BitSet();
		forbidden = new BitSet();
	}


	/**
	 *  Sets the rMap attribute of the RNode object
	 *
	 *@param  rMap  The new rMap value
	 */
	public void setRMap(RMap rMap)
	{
		this.rMap = rMap;
	}


	/**
	 *  Sets the extension attribute of the RNode object
	 *
	 *@param  extension  The new extension value
	 */
	public void setExtension(BitSet extension)
	{
		this.extension = extension;
	}


	/**
	 *  Sets the forbidden attribute of the RNode object
	 *
	 *@param  forbidden  The new forbidden value
	 */
	public void setForbidden(BitSet forbidden)
	{
		this.forbidden = forbidden;
	}


	/**
	 *  Gets the rMap attribute of the RNode object
	 *
	 *@return    The rMap value
	 */
	public RMap getRMap()
	{
		return rMap;
	}


	/**
	 *  Gets the extension attribute of the RNode object
	 *
	 *@return    The extension value
	 */
	public BitSet getExtension()
	{
		return extension;
	}


	/**
	 *  Gets the forbidden attribute of the RNode object
	 *
	 *@return    The forbidden value
	 */
	public BitSet getForbidden()
	{
		return forbidden;
	}



	/**
	 *  Returns a string representation of the RNode
	 *
	 *@return    the string representation of the RNode
	 */
	public String toString()
	{
		return ("id1 : " + rMap.id1 + ", id2 : " + rMap.id2 + "\n" + "extension : " + extension + "\n"
				 + "forbiden : " + forbidden);
	}
}

