/* $Revision$ $Author$$Date$
 *
 * Copyright (C) 2006-2007  Egon Willighagen <ewilligh@uni-koeln.de>
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
package org.openscience.cdk;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IFragmentAtom;

/**
 * Class to represent an IPseudoAtom which embeds an IAtomContainer. Very much
 * like the MDL Molfile <code>Group</code> concept.
 * 
 * @cdk.module data
 * @cdk.svnrev $Revision$
 * 
 * @author egonw
 */
public class FragmentAtom extends PseudoAtom implements IFragmentAtom {

	private static final long serialVersionUID = -6144605920605752463L;

	private IAtomContainer fragment;
	private boolean isExpanded;

	public FragmentAtom() {
		fragment = this.getBuilder().newAtomContainer();
		isExpanded = false;
	}
	
	public boolean isExpanded() {
		return isExpanded;
	}

	public void setExpanded(boolean bool) {
		this.isExpanded = bool;
	}

	public IAtomContainer getFragment() {
		return fragment;
	}

	public void setFragment(IAtomContainer fragment) {
		this.fragment = fragment;
	}

	/**
	 * The exact mass of an FragmentAtom is defined as the sum of exact masses
	 * of the IAtom's in the fragment.
	 */
	public Double getExactMass() {
		double totalMass = 0.0;
        for (IAtom atom : fragment.atoms()) {
            totalMass += atom.getExactMass();
        }
		return totalMass;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("FragmentAtom{").append(hashCode());
		buffer.append(", A=").append(super.toString());
		if (fragment != null) {
			buffer.append(", F=").append(fragment.toString());
		}
		buffer.append('}');
		return buffer.toString();
	}
	
}
