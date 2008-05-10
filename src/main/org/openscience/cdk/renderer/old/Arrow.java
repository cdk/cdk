/* $Revision$ $Author$ $Date$
 *  
 * Copyright (C) 2006-2007  Christoph Steinbeck <steinbeck@users.sf.net>
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
package org.openscience.cdk.renderer.old;

import org.openscience.cdk.interfaces.IAtom;

/**
 * @cdk.module render
 * @cdk.svnrev  $Revision$
 */
public class Arrow 
{
	IAtom start;
	IAtom end;
	
	public Arrow(IAtom atom1, IAtom atom2)
	{
		this.start = atom1;
		this.end = atom2;
	}
	
	public IAtom getEnd() {
		return end;
	}

	public void setEnd(IAtom end) {
		this.end = end;
	}

	public IAtom getStart() {
		return start;
	}

	public void setStart(IAtom start) {
		this.start = start;
	}
		
	
}
