/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2007  The JChemPaint project
 *
 *  Contact: jchempaint-devel@lists.sourceforge.net
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
package org.openscience.cdk.applications.jchempaint;

import java.util.Vector;

import org.openscience.cdk.tools.LoggingTool;

/**
 * A stack of undo operations
 *
 * @cdk.module jchempaint
 * @author     steinbeck
 * @cdk.created    February 4, 2004
 */
public class UndoStack
{
	/**
	 *  Description of the Field
	 */
	public int capacity = 20;
	private LoggingTool logger;
	Vector stack = null;
	int currentPosition = 0;


	/**
	 *  Constructor for the UndoStack object
	 */
	public UndoStack()
	{
		stack = new Vector();
		logger = new LoggingTool(this);
	}


	/**
	 *  Constructor for the UndoStack object
	 *
	 *@param  capacity  Description of the Parameter
	 */
	public UndoStack(int capacity)
	{
		this();
		this.capacity = capacity;
	}


	/**
	 *  Pops (removes and returns the last element) from the stack
	 *
	 *@return    The Object popped from the undo stack
	 */
	public Object pop()
	{
		Object o = null;
		if (stack.size() > 0)
		{
			o = stack.remove(stack.size() - 1);
			if (stack.size() <= currentPosition)
			{
				currentPosition = stack.size()-1;	
			}
			logger.debug("Object " + o.hashCode(), " was taken away from the stack. Current stack size: " + stack.size());
		}
		return o;
	}


	/**
	 *  Pushes an object onto the stack
	 *
	 *@param  o  The Object to be pushed onto the stack
	 */
	public void push(Object o)
	{
		logger.debug("Object " + o.hashCode(), " was pushed on the stack");
		if (currentPosition != stack.size() - 1)
		{
			logger.debug("CurrentPosition " + (currentPosition + 1 ), " < stack size " + stack.size());
			logger.debug("Thus clearing stack to prevent confusion");
			stack.removeAllElements();
		}
		if (stack.size() < capacity)
		{
			logger.debug("Current size " + stack.size(), " is smaller than capacity " + capacity);
			logger.debug("Thus the new model is just added");
			stack.addElement(o);
		} else
		{
			logger.debug("Current size " + stack.size(), " has reached capacity " + capacity);
			logger.debug("Thus removing element at position 0");
			stack.remove(0);
			logger.debug("and now adding new element");
			stack.addElement(o);
		}
		currentPosition = stack.size()-1;
		logger.debug(listObjects());
	}

	public Object getCurrent()
	{
		return stack.elementAt(currentPosition);
	}
	
	/**
	 *  right shift of undoStack (the last-but-one'th position is now the last one)
	 */

	public void undoShift()
	{
		logger.debug("Received undo shift command");
		//Object o = stack.remove(stack.size() - 1);
		//logger.debug("Removed last element Object " + o.hashCode() + ". ");
		//stack.insertElementAt(o, 0);
		if (currentPosition > 0) currentPosition --;
		logger.debug("currentPosition: " +  (currentPosition + 1), "/"+ stack.size());
		logger.debug(listObjects());
	}


	/**
	 *  left shift of undoStack (Element at position zero becomes the last one)
	 */

	public void redoShift()
	{
		logger.debug("Received redo shift command");
		//Object o = stack.remove(0);
		//logger.debug("Removed Object " + o.hashCode() + " from position 0 ");
		//stack.addElement(o);
		if (currentPosition < stack.size() - 1) currentPosition ++;
		logger.debug("currentPosition: " +  (currentPosition + 1), "/"+ stack.size());
		logger.debug(listObjects());
	}

	public int size()
	{
		return stack.size();
	}


	String listObjects()
	{
		String s = "List of Object hashcodes in the stack: \n";
		for (int i = 0; i < stack.size(); i++)
		{
			s += stack.elementAt(i).hashCode() + "\n";
		}
		return s;
	}
}

