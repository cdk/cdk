/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004  The Chemistry Development Kit (CDK) project
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

import java.util.ArrayList;

import javax.vecmath.Point3d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.RDFProtonDescriptor;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.test.CDKTestCase;


/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsar
 */
 
public class RDFProtonDescriptorTest extends CDKTestCase {
	
	public  RDFProtonDescriptorTest() {}
    
	public static Test suite() {
		return new TestSuite(RDFProtonDescriptorTest.class);
	}
	
	public void testRDFProtonDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IAtomicDescriptor descriptor  = new RDFProtonDescriptor();
		Object[] params = {new Boolean(true)};
		descriptor.setParameters(params);
		
		Molecule mol = new Molecule();
		Atom a0 = new Atom("C"); mol.addAtom(a0); a0.setPoint3d(new Point3d(-1.2360,0.6585,0.5216));
		Atom a1 = new Atom("C"); mol.addAtom(a1); a1.setPoint3d(new Point3d(-1.2357,-0.6594,0.5213));
		Atom a2 = new Atom("C"); mol.addAtom(a2); a2.setPoint3d(new Point3d(-0.0554,-1.1436,-0.3298));
		Atom a3 = new Atom("C"); mol.addAtom(a3); a3.setPoint3d(new Point3d(1.2023,-0.7740,0.5195));
		Atom a4 = new Atom("C"); mol.addAtom(a4); a4.setPoint3d(new Point3d(1.2019,0.7743,0.5199));
		Atom a5 = new Atom("C"); mol.addAtom(a5); a5.setPoint3d(new Point3d(-0.0560,1.1438,-0.3292));
		Atom a6 = new Atom("H"); mol.addAtom(a6); a6.setPoint3d(new Point3d(-0.0159,0.0004,-1.4081));
		Atom a7 = new Atom("H"); mol.addAtom(a7); a7.setPoint3d(new Point3d(-1.9492,1.2898,1.0309));
		Atom a8 = new Atom("H"); mol.addAtom(a8); a8.setPoint3d(new Point3d(-1.9460,-1.2913,1.0338));
		Atom a9 = new Atom("H"); mol.addAtom(a9); a9.setPoint3d(new Point3d(-0.1091,-2.1705,-0.6913));
		Atom a10 = new Atom("H"); mol.addAtom(a10); a10.setPoint3d(new Point3d(1.1050,-1.1575,1.5351));
		Atom a11 = new Atom("H"); mol.addAtom(a11); a11.setPoint3d(new Point3d(2.1081,-1.1568,0.0494));
		Atom a12 = new Atom("H"); mol.addAtom(a12); a12.setPoint3d(new Point3d(1.1044,1.1572,1.5357));
		Atom a13 = new Atom("H"); mol.addAtom(a13); a13.setPoint3d(new Point3d(2.1075,1.1579,0.0500));
		Atom a14 = new Atom("H"); mol.addAtom(a14); a14.setPoint3d(new Point3d(-0.1102,2.1708,-0.6902));
		Atom a15 = new Atom("H"); mol.addAtom(a15); a15.setPoint3d(new Point3d(0.9103,0.0008,-1.9828));
		Atom a16 = new Atom("H"); mol.addAtom(a16); a16.setPoint3d(new Point3d(-0.8974,0.0003,-2.0494));
				
		mol.addBond(0, 1, 2); // 1
		mol.addBond(1, 2, 1); // 2
		mol.addBond(2, 3, 1); // 3
		mol.addBond(3, 4, 1); // 4
		mol.addBond(4, 5, 1); // 5
		mol.addBond(0, 5, 1); // 6
		mol.addBond(5, 6, 1); // 7
		mol.addBond(2, 6, 1); // 8
		mol.addBond(0, 7, 1); // 9
		mol.addBond(1, 8, 1); // 10
		mol.addBond(2, 9, 1); // 11
		mol.addBond(3, 10, 1); // 12
		mol.addBond(3, 11, 1); // 13
		mol.addBond(4, 12, 1); // 14
		mol.addBond(4, 13, 1); // 15
		mol.addBond(5, 14, 1); // 16
		mol.addBond(6, 15, 1); // 17
		mol.addBond(6, 16, 1); // 18
		
		IAtom target = mol.getAtomAt(16);
		
		IntegerArrayResult retval = (IntegerArrayResult)descriptor.calculate(target,mol).getValue();
		assertEquals(5, retval.size());
		
		Double thisValue = (Double)((ArrayList)target.getProperty("gasteigerGHR")).get(13);
		assertEquals(0.068, thisValue.doubleValue(), 0.01);
		
		thisValue = (Double)((ArrayList)target.getProperty("gasteigerGDR")).get(1);
		assertEquals(0.032, thisValue.doubleValue(), 0.01);
		
		thisValue = (Double)((ArrayList)target.getProperty("gasteigerGSR")).get(2);
		assertEquals(2.25, thisValue.doubleValue(), 0.01);
		
		thisValue = (Double)((ArrayList)target.getProperty("gasteigerG3R")).get(2);
		assertEquals(1.69, thisValue.doubleValue(), 0.01);
	}
}

