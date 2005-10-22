/* $RCSfile$ 
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openscience.cdk.test.graph;

import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test
 */
public class PathToolsTest extends CDKTestCase
{
	private Molecule molecule;
	
	public PathToolsTest(String name) {
		super(name);
	}

	public void setUp() {
		molecule = MoleculeFactory.makeAlphaPinene();
	}

	public static Test suite() {
		return new TestSuite(PathToolsTest.class);
	}

	public void testBreadthFirstTargetSearch() {
		org.openscience.cdk.interfaces.Atom atom1 = molecule.getAtomAt(0);
		org.openscience.cdk.interfaces.Atom atom2 = molecule.getAtomAt(8);
		Vector sphere = new Vector();
		sphere.addElement(atom1);
		int length = PathTools.breadthFirstTargetSearch(molecule, sphere, atom2, 0, 3);
		//System.out.println("PathLengthTest->length: " + length);
		assertEquals(3, length);
	}
	
}

