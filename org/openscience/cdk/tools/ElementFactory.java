/*
 * ElementFactory.java
 *
 * $RCSfile$    $Author$    $Date$    $Revision$
 *
 * Copyright (C) 1997-2001  The Chemistry Development Kit Project
 *
 * Contact: steinbeck@ice.mpg.de
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package org.openscience.cdk.tools;

import java.util.*;
import java.io.*;
import org.openscience.cdk.*;

/**
 *  Used to store and return data of a particular element
 *
 * @author     steinbeck
 * @created    July 23, 2001
 */

public class ElementFactory {

	Element[] elements = {};


	/**
	 *  Constructor for the ElementFactory object
	 */
	public ElementFactory() {
	}


	/**
	 *  Gets the Size attribute of the ElementFactory object
	 *
	 * @return    The Size value
	 */
	public int getSize() {
		return elements.length;
	}



	/**
	 *  Description of the Method
	 *
	 * @param  pos  Description of Parameter
	 * @return      Description of the Returned Value
	 */
	public Element elementAt(int pos) {
		if (pos >= 0 && pos < getSize()) {
			return (Element) elements[pos].clone();
		}
		return null;
	}


	/**
	 *  Writes an xml description of the elements to a given file
	 *
	 * @param  writer  Description of Parameter
	 */
	public void writeElements(Writer writer) throws IOException 
	{
		Element e = null;
		writer.write("<elements>\n");
		for (int f = 0; f < elements.length; f++)
		{
			e = elements[f];
			writer.write("<element>\n");
			writer.write("</element>\n");				
		}
		writer.write("</elements>\n");
	}


	/**
	 *  Description of the Method
	 */
	private void enlargeElementArray() {
		int newSize = (int) (elements.length * 1.1);
		Element[] newElements = new Element[newSize];
		System.arraycopy(elements, 0, newElements, 0, elements.length);
		elements = newElements;
	}

}

