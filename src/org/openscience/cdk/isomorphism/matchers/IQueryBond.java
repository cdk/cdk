/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004-2007  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
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
package org.openscience.cdk.isomorphism.matchers;

import org.openscience.cdk.interfaces.IBond;

/**
 * Defines the abililty to be matched against IBond's. Most prominent application
 * is in isomorphism and substructure matching in the UniversalIsomorphismTester.
 * 
 * @cdk.module interfaces
 * @cdk.svnrev  $Revision$
 * @see        org.openscience.cdk.isomorphism.UniversalIsomorphismTester
 */
public interface IQueryBond {

	/**
	 * Returns true of the given <code>bond</code> matches this IQueryBond.
	 * 
	 * @param bond IBond to match against
	 * @return     true, if this IQueryBond matches the given IBond
	 */
	public boolean matches(IBond bond);
    
}

