/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk;

import java.util.*;
import javax.vecmath.*;

/**
 *  An Enumeration of the Atoms in a particular AtomContainer.
 *  The typical use is:
 *
 *  <pre>
 *  AtomEnumeration atoms = ((Molecule)molecule).atoms();
 *  while (atoms.hasMoreElements()) {
 *      Atom a = (Atom)atoms.nextElement();
 *      // do something with atom
 *  }
 *  </pre>
 *
 *  <p>The Enumeration does not clone the AtomContainer from which
 *  it is constructed, which might lead to errors.
 *
 * @cdkPackage core
 *
 * @author     steinbeck
 * @created    2000-10-02
 */
public class AtomEnumeration implements java.io.Serializable, Cloneable, Enumeration {

    /** Counts the current element. */
    protected int atomEnumerationCounter = 0;
    /** Contains the atoms to enumerate. */
    protected AtomContainer ac;

    /**
     *  Constructs a new AtomEnumeration.
     *
     *  @param      ac      AtomContainer which contains the atoms
     */
	public AtomEnumeration(AtomContainer ac)
	{
		this.ac = ac;
	}
    
    /**
     *  Returns true if the Enumeration still has atoms left.
     */
	public boolean hasMoreElements()
	{
		if (ac.getAtomCount() > atomEnumerationCounter)
		{
			return true;	
		}
		return false;
	}
    
    /**
     *  Returns next atom in Enumeration.
     *
     *  @return Uncasted Atom class.
     */
	public Object nextElement()
	{
		atomEnumerationCounter ++;
		return ac.getAtomAt(atomEnumerationCounter - 1);
	}
}

