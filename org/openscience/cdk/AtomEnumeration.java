/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2001  The Chemistry Development Kit (CDK) project
 *
 *  Contact: steinbeck@ice.mpg.de, geelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk;

import java.util.*;
import javax.vecmath.*;

/**
 *  An Enumeration of the Atoms in a particular AtomContainer
 *
 * @author     steinbeck
 * @created    October 2, 2000
 */
public class AtomEnumeration implements Enumeration
{
	protected int atomEnumerationCounter = 0;
	protected AtomContainer ac;
	
	public AtomEnumeration(AtomContainer ac)
	{
		this.ac = ac;
	}
	
	public boolean hasMoreElements()
	{
		if (ac.getAtomCount() > atomEnumerationCounter)
		{
			return true;	
		}
		return false;
	}
	
	public Object nextElement()
	{
		atomEnumerationCounter ++;
		return ac.getAtomAt(atomEnumerationCounter - 1);
	}
}

