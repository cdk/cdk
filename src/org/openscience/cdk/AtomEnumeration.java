/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
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
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk;

import java.util.Enumeration;

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
 * @cdk.module data
 *
 * @author     steinbeck
 * @cdk.created    2000-10-02
 */
public class AtomEnumeration implements java.io.Serializable, Cloneable, Enumeration {

    /**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is imcompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
	 */
	private static final long serialVersionUID = -1792810428672771080L;

	/** Counts the current element. */
    private int atomEnumerationCounter = 0;
    /** Contains the atoms to enumerate. */
    private AtomContainer container;

    /**
     *  Constructs a new AtomEnumeration.
     *
     *  @param  container  AtomContainer which contains the atoms
     */
	public AtomEnumeration(AtomContainer container)
	{
		this.container = container;
	}
    
    /**
     *  Returns true if the Enumeration still has atoms left.
     */
	public boolean hasMoreElements()
	{
		if (container.getAtomCount() > atomEnumerationCounter)
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
		return container.getAtom(atomEnumerationCounter - 1);
	}
}

