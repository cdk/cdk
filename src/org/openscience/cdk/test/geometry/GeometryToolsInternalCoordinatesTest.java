/* $Revision: 7691 $ $Author: egonw $ $Date: 2007-01-11 12:47:48 +0100 (Thu, 11 Jan 2007) $
 * 
 * Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.test.geometry;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.geometry.GeometryToolsInternalCoordinates;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.nonotify.NNAtom;
import org.openscience.cdk.nonotify.NNAtomContainer;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test-standard
 */
public class GeometryToolsInternalCoordinatesTest extends CDKTestCase {
    
    public GeometryToolsInternalCoordinatesTest(String name) {
        super(name);
    }
    
	public static Test suite() {
		return new TestSuite(GeometryToolsInternalCoordinatesTest.class);
	}

	public void testTranslateAllPositive_IAtomContainer() {
		IAtomContainer container = new NNAtomContainer();
		IAtom atom = new NNAtom(Elements.CARBON);
		atom.setPoint2d(new Point2d(-3, -2));
		container.addAtom(atom);
		GeometryToolsInternalCoordinates.translateAllPositive(container);
		assertTrue(0 <= atom.getPoint2d().x);
		assertTrue(0 <= atom.getPoint2d().y);
	}
	
    public void testGetLength2D_IBond() {
        Atom o = new Atom("O", new Point2d(0.0, 0.0));
        Atom c = new Atom("C", new Point2d(1.0, 0.0));
        Bond bond = new Bond(c,o);
        
        assertEquals(1.0, GeometryToolsInternalCoordinates.getLength2D(bond), 0.001);
    }
    public void testMapAtomsOfAlignedStructures() throws Exception {
   	 
    	String filenameMolOne = "data/mdl/murckoTest6_3d_2.mol";
		String filenameMolTwo = "data/mdl/murckoTest6_3d.mol";
    	//String filenameMolTwo = "data/mdl/murckoTest6_3d_2.mol";
	    InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filenameMolOne);
	    Molecule molOne=null;
	    Molecule molTwo=null;
	    Map mappedAtoms=new HashMap();
	    MDLReader reader = new MDLReader(ins);
	    molOne = (Molecule)reader.read(new Molecule());
		
	    ins = this.getClass().getClassLoader().getResourceAsStream(filenameMolTwo);
	    reader = new MDLReader(ins);
	    molTwo = (Molecule)reader.read(new Molecule());
	   
	    mappedAtoms=GeometryToolsInternalCoordinates.mapAtomsOfAlignedStructures(molOne, molTwo, mappedAtoms);
	    //logger.debug("mappedAtoms:"+mappedAtoms.toString());
	    //logger.debug("***** ANGLE VARIATIONS *****");
	    double AngleRMSD=GeometryToolsInternalCoordinates.getAngleRMSD(molOne,molTwo,mappedAtoms);
	    //logger.debug("The Angle RMSD between the first and the second structure is :"+AngleRMSD);
	    //logger.debug("***** ALL ATOMS RMSD *****");
	    assertEquals(0.2, AngleRMSD, 0.1);
	    double AllRMSD=GeometryToolsInternalCoordinates.getAllAtomRMSD(molOne,molTwo,mappedAtoms,true);
	    //logger.debug("The RMSD between the first and the second structure is :"+AllRMSD);
	    assertEquals(0.242, AllRMSD, 0.001);
	    //logger.debug("***** BOND LENGTH RMSD *****");
	    double BondLengthRMSD=GeometryToolsInternalCoordinates.getBondLengthRMSD(molOne,molTwo,mappedAtoms,true);
	    //logger.debug("The Bond length RMSD between the first and the second structure is :"+BondLengthRMSD);
	    assertEquals(0.2, BondLengthRMSD, 0.1);
   }

    /*
     * @cdk.bug        1649007
     */
    public void testRotate_IAtomContainer_Point2d_double(){
    	Atom atom1=new Atom("C");
    	atom1.setPoint2d(new Point2d(1,1));
    	Atom atom2=new Atom("C");
    	atom2.setPoint2d(new Point2d(1,0));
    	IAtomContainer ac=DefaultChemObjectBuilder.getInstance().newAtomContainer();
    	ac.addAtom(atom1);
    	ac.addAtom(atom2);
    	GeometryToolsInternalCoordinates.rotate(ac, new Point2d(0,0),Math.PI);
    	assertEquals(atom1.getPoint2d().x,-1,.2);
    	assertEquals(atom1.getPoint2d().y,1,.2);
    	assertEquals(atom2.getPoint2d().x,0,.2);
    	assertEquals(atom2.getPoint2d().y,1,.2);
    	atom2.setPoint2d(new Point2d(0,0));
    	GeometryToolsInternalCoordinates.rotate(ac, new Point2d(0,0),Math.PI);
    	assertFalse(Double.isNaN(atom2.getPoint2d().x));
    	assertFalse(Double.isNaN(atom2.getPoint2d().y));
    }
    
    
    public void testGetMinMax_IAtomContainer(){
    	Atom atom1=new Atom("C");
    	atom1.setPoint2d(new Point2d(1,1));
    	Atom atom2=new Atom("C");
    	atom2.setPoint2d(new Point2d(1,0));
    	IAtomContainer ac=DefaultChemObjectBuilder.getInstance().newAtomContainer();
    	ac.addAtom(atom1);
    	ac.addAtom(atom2);
    	double [] minmax=GeometryToolsInternalCoordinates.getMinMax(ac);
    	assertEquals(minmax[0],1d,.1);
    	assertEquals(minmax[1],0d,.1);
    	assertEquals(minmax[2],1d,.1);
    	assertEquals(minmax[3],1d,.1);
    }
    
    
    public void testRotate_IAtom_Point3d_Point3d_double(){
    	Atom atom1=new Atom("C");
    	atom1.setPoint3d(new Point3d(1,1,0));
    	GeometryToolsInternalCoordinates.rotate(atom1, new Point3d(2,0,0), new Point3d(2,2,0), 90);
    	assertEquals(atom1.getPoint3d().x,2.0);
    	assertEquals(atom1.getPoint3d().y,1.0);
    	assertEquals(atom1.getPoint3d().z,1.0);
    }
    
    public void testNormalize_Point3d(){
    	Point3d p=new Point3d(1,1,0);
    	GeometryToolsInternalCoordinates.normalize(p);
    	assertEquals(p.x,0.7,.1);
    	assertEquals(p.y,0.7,.1);
    	assertEquals(p.z,0.0,.1);
    }
    
    public void testGet2DCenter_IAtomContainer(){
    	Atom atom1=new Atom("C");
    	atom1.setPoint2d(new Point2d(1,1));
    	Atom atom2=new Atom("C");
    	atom2.setPoint2d(new Point2d(1,0));
    	IAtomContainer ac=DefaultChemObjectBuilder.getInstance().newAtomContainer();
    	ac.addAtom(atom1);
    	ac.addAtom(atom2);
    	Point2d p=GeometryToolsInternalCoordinates.get2DCenter(ac);
    	assertEquals(p.x,1.0,.1);
    	assertEquals(p.y,0.5,.1);
    }

    public void testGet2DCenter_arrayIAtom(){
    	Atom atom1=new Atom("C");
    	atom1.setPoint2d(new Point2d(1,1));
    	Atom atom2=new Atom("C");
    	atom2.setPoint2d(new Point2d(1,0));
    	IAtom[] array=new IAtom[2];
    	array[0]=atom1;
    	array[1]=atom2;
    	Point2d p=GeometryToolsInternalCoordinates.get2DCenter(array);
    	assertEquals(p.x,1.0,.1);
    	assertEquals(p.y,0.5,.1);
    }
    
    public void testGet2DCenter_IRingSet(){
    	Atom atom1=new Atom("C");
    	atom1.setPoint2d(new Point2d(1,1));
    	Atom atom2=new Atom("C");
    	atom2.setPoint2d(new Point2d(1,0));
    	IRing ac=DefaultChemObjectBuilder.getInstance().newRing();
    	ac.addAtom(atom1);
    	ac.addAtom(atom2);
    	IRingSet ringset=DefaultChemObjectBuilder.getInstance().newRingSet();
    	ringset.addAtomContainer(ac);
    	Point2d p=GeometryToolsInternalCoordinates.get2DCenter(ac);
    	assertEquals(p.x,1.0,.1);
    	assertEquals(p.y,0.5,.1);
    }
    
    
    public void testGet2DCenter_Iterator(){
    	Atom atom1=new Atom("C");
    	atom1.setPoint2d(new Point2d(1,1));
    	Atom atom2=new Atom("C");
    	atom2.setPoint2d(new Point2d(1,0));
    	IAtomContainer ac=DefaultChemObjectBuilder.getInstance().newAtomContainer();
    	ac.addAtom(atom1);
    	ac.addAtom(atom2);
    	Point2d p=GeometryToolsInternalCoordinates.get2DCenter(ac.atoms());
    	assertEquals(p.x,1.0,.1);
    	assertEquals(p.y,0.5,.1);
    }
}


