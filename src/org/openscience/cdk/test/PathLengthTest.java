/* PathLengthTest.java
 * 
 * $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2004  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 */

package org.openscience.cdk.test;

import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.templates.MoleculeFactory;

/**
 * @cdk.module test
 */
public class PathLengthTest extends TestCase
{
	Molecule molecule;
	
	public PathLengthTest(String name)
	{
		super(name);
	}

	public void setUp()
	{
		molecule = MoleculeFactory.makeAlphaPinene();
	}

	public static Test suite() 
	{
		return new TestSuite(PathLengthTest.class);
	}

	public void testPathLength()
	{
		Atom atom1 = molecule.getAtomAt(0);
		Atom atom2 = molecule.getAtomAt(8);
		Vector sphere = new Vector();
		sphere.addElement(atom1);
		int length = PathTools.breadthFirstTargetSearch(molecule, sphere, atom2, 0, 3);
		//System.out.println("PathLengthTest->length: " + length);
		assertTrue(length == 3);
	}
	
}

