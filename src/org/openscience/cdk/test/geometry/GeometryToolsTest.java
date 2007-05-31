/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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

import java.util.HashMap;
import java.util.Iterator;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.nonotify.NNAtom;
import org.openscience.cdk.nonotify.NNAtomContainer;
import org.openscience.cdk.test.CDKTestCase;

/**
 * This class defines regression tests that should ensure that the source code
 * of the org.openscience.cdk.geometry.GeometryTools is not broken.
 *
 * @cdk.module test-standard
 *
 * @author     Egon Willighagen
 * @cdk.created    2004-01-30
 *
 * @see org.openscience.cdk.geometry.GeometryTools
 */
public class GeometryToolsTest extends CDKTestCase {

    public GeometryToolsTest(String name) {
        super(name);
    }
    
    public void setUp() {}
    
    /**
     * Defines a set of tests that can be used in automatic regression testing
     * with JUnit.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(GeometryToolsTest.class);
        return suite;
    }
    
	public void testTranslateAllPositive_IAtomContainer_HashMap() {
		IAtomContainer container = new NNAtomContainer();
		IAtom atom = new NNAtom(Elements.CARBON);
		atom.setPoint2d(new Point2d(-3, -2));
		container.addAtom(atom);
		HashMap map=this.makeCoordsMap(container);
		GeometryTools.translateAllPositive(container,map);
		assertTrue(0 <= ((Point2d)map.get(atom)).x);
		assertTrue(0 <= ((Point2d)map.get(atom)).y);
	}
	
    public void testGetLength2D_IBond_HashMap() {
        Atom o = new Atom("O", new Point2d(0.0, 0.0));
        Atom c = new Atom("C", new Point2d(1.0, 0.0));
        Bond bond = new Bond(c,o);
        IAtomContainer container=DefaultChemObjectBuilder.getInstance().newAtomContainer();
        container.addAtom(o);
        container.addAtom(c);
        container.addBond(bond);
        HashMap map=this.makeCoordsMap(container);
        assertEquals(1.0, GeometryTools.getLength2D(bond,map), 0.001);
    }

    
    private HashMap makeCoordsMap(IAtomContainer container){
    	HashMap map=new HashMap();
    	Iterator it=container.atoms();
    	while(it.hasNext()){
    		IAtom atom=(IAtom)it.next();
    		map.put(atom, atom.getPoint2d());
    	}
    	return map;
    }
    
    public void testGetMinMax_IAtomContainer_HashMap(){
    	Atom atom1=new Atom("C");
    	atom1.setPoint2d(new Point2d(1,1));
    	Atom atom2=new Atom("C");
    	atom2.setPoint2d(new Point2d(1,0));
    	IAtomContainer ac=DefaultChemObjectBuilder.getInstance().newAtomContainer();
    	ac.addAtom(atom1);
    	ac.addAtom(atom2);
    	HashMap map=makeCoordsMap(ac);
    	double [] minmax=GeometryTools.getMinMax(ac,map);
    	assertEquals(minmax[0],1d,.1);
    	assertEquals(minmax[1],0d,.1);
    	assertEquals(minmax[2],1d,.1);
    	assertEquals(minmax[3],1d,.1);
    }
    
    public void testGetMinMax_IMoleculeSet_HashMap(){
    	Atom atom1=new Atom("C");
    	atom1.setPoint2d(new Point2d(1,1));
    	Atom atom2=new Atom("C");
    	atom2.setPoint2d(new Point2d(1,0));
    	IAtomContainer ac=DefaultChemObjectBuilder.getInstance().newAtomContainer();
    	ac.addAtom(atom1);
    	ac.addAtom(atom2);
    	IMoleculeSet molSet = new MoleculeSet();
    	molSet.addAtomContainer(ac);
    	HashMap map=makeCoordsMap(ac);
    	double [] minmax=GeometryTools.getMinMax(molSet,map);
    	assertEquals(minmax[0],1d,.1);
    	assertEquals(minmax[1],0d,.1);
    	assertEquals(minmax[2],1d,.1);
    	assertEquals(minmax[3],1d,.1);
    }
    
    public void testGet2DCenter_IAtomContainer_HashMap(){
    	Atom atom1=new Atom("C");
    	atom1.setPoint2d(new Point2d(1,1));
    	Atom atom2=new Atom("C");
    	atom2.setPoint2d(new Point2d(1,0));
    	IAtomContainer ac=DefaultChemObjectBuilder.getInstance().newAtomContainer();
    	ac.addAtom(atom1);
    	ac.addAtom(atom2);
    	Point2d p=GeometryTools.get2DCenter(ac, this.makeCoordsMap(ac));
    	assertEquals(p.x,1.0,.1);
    	assertEquals(p.y,0.5,.1);
    }

    
    public void testGet2DCenter_Iterator_HashMap(){
    	Atom atom1=new Atom("C");
    	atom1.setPoint2d(new Point2d(1,1));
    	Atom atom2=new Atom("C");
    	atom2.setPoint2d(new Point2d(1,0));
    	IAtomContainer ac=DefaultChemObjectBuilder.getInstance().newAtomContainer();
    	ac.addAtom(atom1);
    	ac.addAtom(atom2);
    	Point2d p=GeometryTools.get2DCenter(ac.atoms(),this.makeCoordsMap(ac));
    	assertEquals(p.x,1.0,.1);
    	assertEquals(p.y,0.5,.1);
    }

    
    public void testGet2DCenter_arrayIAtom_HashMap(){
    	Atom atom1=new Atom("C");
    	atom1.setPoint2d(new Point2d(1,1));
    	Atom atom2=new Atom("C");
    	atom2.setPoint2d(new Point2d(1,0));
    	IAtom[] array=new IAtom[2];
    	array[0]=atom1;
    	array[1]=atom2;
    	HashMap hm=new HashMap();
    	hm.put(atom1, atom1.getPoint2d());
    	hm.put(atom2, atom2.getPoint2d());
    	Point2d p=GeometryTools.get2DCenter(array,hm);
    	assertEquals(p.x,1.0,.1);
    	assertEquals(p.y,0.5,.1);
    }
    
    public void testHas2DCoordinates_IAtomContainer() {
    	Atom atom1=new Atom("C");
    	atom1.setPoint2d(new Point2d(1,1));
    	Atom atom2=new Atom("C");
    	atom2.setPoint2d(new Point2d(1,0));
    	IAtomContainer container = new AtomContainer();
    	container.addAtom(atom1);
    	container.addAtom(atom2);
    	assertTrue(GeometryTools.has2DCoordinates(container));

    	atom1=new Atom("C");
    	atom1.setPoint3d(new Point3d(1,1,1));
    	atom2=new Atom("C");
    	atom2.setPoint3d(new Point3d(1,0,5));
    	container = new AtomContainer();
    	container.addAtom(atom1);
    	container.addAtom(atom2);
    	assertFalse(GeometryTools.has2DCoordinates(container));
    }

    public void testHas2DCoordinates_EmptyAtomContainer() {
    	IAtomContainer container = new AtomContainer();
    	assertFalse(GeometryTools.has2DCoordinates(container));
    	assertFalse(GeometryTools.has2DCoordinates(null));
}

    public void testHas2DCoordinatesNew_IAtomContainer() {
    	Atom atom1=new Atom("C");
    	atom1.setPoint2d(new Point2d(1,1));
    	Atom atom2=new Atom("C");
    	atom2.setPoint2d(new Point2d(1,0));
    	IAtomContainer container = new AtomContainer();
    	container.addAtom(atom1);
    	container.addAtom(atom2);
    	assertEquals(2, GeometryTools.has2DCoordinatesNew(container));

    	atom1=new Atom("C");
    	atom1.setPoint2d(new Point2d(1,1));
    	atom2=new Atom("C");
    	atom2.setPoint3d(new Point3d(1,0,1));
    	container = new AtomContainer();
    	container.addAtom(atom1);
    	container.addAtom(atom2);
    	assertEquals(1, GeometryTools.has2DCoordinatesNew(container));

    	atom1=new Atom("C");
    	atom1.setPoint3d(new Point3d(1,1,1));
    	atom2=new Atom("C");
    	atom2.setPoint3d(new Point3d(1,0,5));
    	container = new AtomContainer();
    	container.addAtom(atom1);
    	container.addAtom(atom2);
    	assertEquals(0, GeometryTools.has2DCoordinatesNew(container));
    }

    public void testHas3DCoordinates_IAtomContainer() {
    	Atom atom1=new Atom("C");
    	atom1.setPoint2d(new Point2d(1,1));
    	Atom atom2=new Atom("C");
    	atom2.setPoint2d(new Point2d(1,0));
    	IAtomContainer container = new AtomContainer();
    	container.addAtom(atom1);
    	container.addAtom(atom2);
    	assertFalse(GeometryTools.has3DCoordinates(container));

    	atom1=new Atom("C");
    	atom1.setPoint3d(new Point3d(1,1,1));
    	atom2=new Atom("C");
    	atom2.setPoint3d(new Point3d(1,0,5));
    	container = new AtomContainer();
    	container.addAtom(atom1);
    	container.addAtom(atom2);
    	assertTrue(GeometryTools.has3DCoordinates(container));
    }

    public void testTranslateAllPositive_IAtomContainer_HashMap2(){
    	Atom atom1=new Atom("C");
    	atom1.setPoint2d(new Point2d(-1,-1));
    	Atom atom2=new Atom("C");
    	atom2.setPoint2d(new Point2d(1,0));
    	IAtomContainer ac=DefaultChemObjectBuilder.getInstance().newAtomContainer();
    	ac.addAtom(atom1);
    	ac.addAtom(atom2);
    	HashMap hm=this.makeCoordsMap(ac);
    	GeometryTools.translateAllPositive(ac,hm);
    	assertEquals(((Point2d)hm.get(atom1)).x,0.0, 0.01);
    	assertEquals(((Point2d)hm.get(atom1)).y,0.0, 0.01);
    	assertEquals(((Point2d)hm.get(atom2)).x,2.0, 0.01);
    	assertEquals(((Point2d)hm.get(atom2)).y,1.0, 0.01);
    }
    
    public void testGetLength2D_IBond_HashMap2(){
    	Atom atom1=new Atom("C");
    	atom1.setPoint2d(new Point2d(-1,-1));
    	Atom atom2=new Atom("C");
    	atom2.setPoint2d(new Point2d(1,0));
    	IBond bond=new Bond(atom1,atom2);
    	IAtomContainer ac=DefaultChemObjectBuilder.getInstance().newAtomContainer();
    	ac.addAtom(atom1);
    	ac.addAtom(atom2);
    	HashMap hm=this.makeCoordsMap(ac);
    	assertEquals(GeometryTools.getLength2D(bond,hm),2.23,0.01);
    }
}

