/* $Revision: 12144 $ $Author: egonw $ $Date: 2008-09-02 21:53:30 +0100 (Tue, 02 Sep 2008) $
 *
 *  Copyright (C) 2005-2007  Christian Hoppe <chhoppe@users.sf.net>
 *
 *  Contact: cdk-devel@list.sourceforge.net
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
package org.openscience.cdk.modeling.builder3d;

import javax.vecmath.Point3d;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

/**
 * Tests for AtomPlacer3D
 *
 * @cdk.module test-builder3d
 * @cdk.svnrev  $Revision: 12144 $
 */
public class AtomTetrahedralLigandPlacer3DTest extends NewCDKTestCase{
	
	@Test
	public void testAdd3DCoordinatesForSinglyBondedLigands_IAtomContainer() throws CDKException{
		IAtom atom1=new Atom("C");
		atom1.setPoint3d(new Point3d(1,1,1));
		IAtom atom2=new Atom("H");
		IAtom atom3=new Atom("H");
		IAtom atom4=new Atom("H");
		IAtom atom5=new Atom("H");
		IBond bond1=new Bond(atom1,atom2);
		IBond bond2=new Bond(atom1,atom3);
		IBond bond3=new Bond(atom1,atom4);
		IBond bond4=new Bond(atom1,atom5);
		IAtomContainer ac=atom1.getBuilder().newAtomContainer();
		ac.addAtom(atom1);
		ac.addAtom(atom2);
		ac.addAtom(atom3);
		ac.addAtom(atom4);
		ac.addAtom(atom5);
		ac.addBond(bond1);
		ac.addBond(bond2);
		ac.addBond(bond3);
		ac.addBond(bond4);
		new AtomTetrahedralLigandPlacer3D().add3DCoordinatesForSinglyBondedLigands(ac);
		ModelBuilder3DTest.checkAverageBondLength(ac);
	}
	
	@Test
	public void rescaleBondLength_IAtom_IAtom_Point3d(){
		IAtom atom1=new Atom("C");
		atom1.setPoint3d(new Point3d(1,1,1));
		atom1.setCovalentRadius(0.2);
		IAtom atom2=new Atom("C");
		atom2.setPoint3d(new Point3d(2,2,2));
		atom2.setCovalentRadius(0.2);
		Point3d newpoint = new AtomTetrahedralLigandPlacer3D().rescaleBondLength(atom1,atom2,atom2.getPoint3d());
		Assert.assertEquals(0.4,newpoint.distance(atom1.getPoint3d()), 0.001);
	}

}
