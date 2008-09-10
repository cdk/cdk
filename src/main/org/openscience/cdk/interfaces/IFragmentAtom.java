/* $Revision$ $Author$$Date$
 *
 * Copyright (C) 2007  Egon Willighagen <ewilligh@users.sf.net>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.interfaces;

/**
 * Class to represent an IPseudoAtom which embeds an IAtomContainer. Very much
 * like the MDL molfile <code>Group</code> concept.
 * 
 * @cdk.module interfaces
 * @cdk.svnrev $Revision$
 * @cdk.bug    1872765
 * 
 * @author egonw
 */
public interface IFragmentAtom extends IPseudoAtom {

	/**
	 * Helper method to indicate that the method should be drawn fully, and not
	 * just the abbreviated form.
	 * 
	 * @return true, if it should be considered in expended form
	 */
	public boolean isExpanded();

	public void setExpanded(boolean bool);

	/**
	 * Returns the fully expended form of the IFragmentAtom.
	 * 
	 * @return the fully expanded form as an IAtomContainer object
	 */
	public IAtomContainer getFragment();

	/**
	 * Sets the fully expended form of the IFragmentAtom.
	 * 
	 * @param fragment The fragment
	 */
	public void setFragment(IAtomContainer fragment);

}