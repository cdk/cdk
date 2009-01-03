/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2006-03-29 10:27:08 +0200 (Wed, 29 Mar 2006) $
 *  $Revision: 5855 $
 *
 *  Copyright (C) 2006-2007  Miguel Rojas <miguelrojasch@yahoo.es>
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
package org.openscience.cdk.interfaces;

/**
 * Classes that implement this interface of a scheme. 
 * This is designed to contain a set of reactions which are linked in 
 * some way but without hard coded semantics.
 *
 * @author      miguelrojasch <miguelrojasch@yahoo.es>
 * @cdk.module  interfaces
 */
public interface IReactionScheme extends IReactionSet{
	
	/**
	 * Add a scheme of reactions.
	 * 
	 * @param reactScheme The IReactionScheme to include
	 */
	public void add(IReactionScheme reactScheme);
	
	/**
	 *  Returns an Iterable for looping over all IMolecularScheme
	 *   in this ReactionScheme.
	 *
	 * @return    An Iterable with the IMolecularScheme in this ReactionScheme
	 */
	public Iterable<IReactionScheme> reactionSchemes();
    
    /**
	 * Returns the number of ReactionScheme in this Scheme.
	 *
	 * @return     The number of ReactionScheme in this Scheme
	 */
	public int getReactionSchemeCount();
	
    /**
	 * Removes all IReactionScheme from this chemObject.
	 */
    public void removeAllReactionSchemes();

    /**
	 * Removes an IReactionScheme from this chemObject.
	 *
	 * @param  scheme  The IReactionScheme to be removed from this chemObject
	 */
    public void removeReactionScheme(IReactionScheme scheme);
    

    /**
	 * Clones this IReactionScheme object and its content.
	 *
	 * @return    The cloned object
	 */
	public Object clone() throws CloneNotSupportedException ;
}
