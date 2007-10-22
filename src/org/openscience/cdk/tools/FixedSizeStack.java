/*
 * $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
 *
 */

package org.openscience.cdk.tools;

/**
 *  A LIFO queue for result structures. The Size is fixed so that one can use
 *  this to hold the 10 best structures, e. g.
 *
 *@author     steinbeck
 * @cdk.svnrev  $Revision$
 *@cdk.created    2001-06-05
 */
public class FixedSizeStack extends java.util.Vector implements java.io.Serializable {

    private static final long serialVersionUID = 7820949609007415936L;
    
    private int size;

	/**
	 *  Creates a fixed size stack
	 *
	 *@param  size  The size of this stack
	 */
	public FixedSizeStack(int size) {
		super();
		this.size = size;
	}


	/**
	 * Pushes an object onto the stack. If the new size then exceeds the 
	 * standard size of this stack, the oldest elements in the stack are discarded. 
	 *
	 *@param  O  The object to be pushed onto the stack
	 */
	public void push(Object O) {
		insertElementAt(O, 0);
		if (size() > size) {
			setSize(size);
		}
	}


	/**
	 *  Returns the last object that was pushed onto the stack
	 *
	 *@return    the last object that was pushed onto the stack
	 */
	public Object pop() {
		Object O = elementAt(0);
		removeElementAt(0);
		return O;
	}
}

