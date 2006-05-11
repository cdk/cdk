/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.qsar.descriptors.atomic;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.Atom;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.DistanceToAtomDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.test.CDKTestCase;

import javax.vecmath.Point3d;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsar
 */
 
public class DistanceToAtomDescriptorTest extends CDKTestCase {
	
	public  DistanceToAtomDescriptorTest() {}
    
	public static Test suite() {
		return new TestSuite(DistanceToAtomDescriptorTest.class);
	}
    
	public void testDistanceToAtomDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IMolecularDescriptor descriptor  = new DistanceToAtomDescriptor();
		Object[] params = {new Integer(0), new Integer(2)};
		descriptor.setParameters(params);
		
		Molecule mol = new Molecule();
		Atom a0 = new Atom("C"); mol.addAtom(a0); a0.setPoint3d(new Point3d(1.2492, -0.2810, 0.0000));
		Atom a1 = new Atom("C"); mol.addAtom(a1); a1.setPoint3d(new Point3d(0.0000, 0.6024, -0.0000));
		Atom a2 = new Atom("C"); mol.addAtom(a2); a2.setPoint3d(new Point3d(-1.2492,-0.2810,0.0000));
		mol.addBond(0, 1, 1); // 1
		mol.addBond(1, 2, 1); // 2
		mol.addBond(2, 3, 1); // 3
		
		assertEquals(2.46, ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue(), 0.1);
	}
}
