/* $RCSfile$
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
package org.openscience.cdk.ringsearch;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;

import java.util.Vector;

/**
 * Implementation of a Queue (FIFO stack).
 *
 * @cdk.module standard
 * @cdk.svnrev  $Revision$
 */
@TestClass("org.openscience.cdk.ringsearch.QueueTest")
public class Queue extends Vector
{

    private static final long serialVersionUID = 1008167867733841614L;

    /**
	 * Constructs an empty Queue
	 *
	 */
	public Queue(){
		super();		
	}	
	
	/**
	 *  Places an Object into the queue
	 *
	 * @param   o  The object to be pushed into the queue
	 */    
    @TestMethod("testOperations")
    public void push(Object o){
		addElement(o);
	}
	
	/**
	 * Returns an Object from the queue
	 *
	 * @return The object that had been pushed first into the queue     
	 */
    @TestMethod("testOperations")
    public Object pop(){
		Object o = elementAt(0);
		removeElementAt(0);	
		return o;
	}
}
