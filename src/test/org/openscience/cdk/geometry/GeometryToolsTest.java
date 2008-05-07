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
package org.openscience.cdk.geometry;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.isomorphism.AtomMappingTools;
import org.openscience.cdk.nonotify.NNAtom;
import org.openscience.cdk.nonotify.NNAtomContainer;

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
public class GeometryToolsTest extends NewCDKTestCase {

    @Test public void testHas2DCoordinates_IAtomContainer() {
    	Atom atom1=new Atom("C");
    	atom1.setPoint2d(new Point2d(1,1));
    	Atom atom2=new Atom("C");
    	atom2.setPoint2d(new Point2d(1,0));
    	IAtomContainer container = new AtomContainer();
    	container.addAtom(atom1);
    	container.addAtom(atom2);
    	Assert.assertTrue(GeometryTools.has2DCoordinates(container));

    	atom1=new Atom("C");
    	atom1.setPoint3d(new Point3d(1,1,1));
    	atom2=new Atom("C");
    	atom2.setPoint3d(new Point3d(1,0,5));
    	container = new AtomContainer();
    	container.addAtom(atom1);
    	container.addAtom(atom2);
    	Assert.assertFalse(GeometryTools.has2DCoordinates(container));
    }

    @Test public void testHas2DCoordinates_EmptyAtomContainer() {
    	IAtomContainer container = new AtomContainer();
    	Assert.assertFalse(GeometryTools.has2DCoordinates(container));
    	Assert.assertFalse(GeometryTools.has2DCoordinates((IAtomContainer)null));
}

	public void testTranslateAllPositive_IAtomContainer() {
		IAtomContainer container = new NNAtomContainer();
		IAtom atom = new NNAtom(Elements.CARBON);
		atom.setPoint2d(new Point2d(-3, -2));
		container.addAtom(atom);
		GeometryTools.translateAllPositive(container);
		Assert.assertTrue(0 <= atom.getPoint2d().x);
		Assert.assertTrue(0 <= atom.getPoint2d().y);
	}
	
    public void testGetLength2D_IBond() {
        Atom o = new Atom("O", new Point2d(0.0, 0.0));
        Atom c = new Atom("C", new Point2d(1.0, 0.0));
        Bond bond = new Bond(c,o);
        
        Assert.assertEquals(1.0, GeometryTools.getLength2D(bond), 0.001);
    }
    public void testMapAtomsOfAlignedStructures() throws Exception {
   	 
    	String filenameMolOne = "data/mdl/murckoTest6_3d_2.mol";
		String filenameMolTwo = "data/mdl/murckoTest6_3d.mol";
    	//String filenameMolTwo = "data/mdl/murckoTest6_3d_2.mol";
	    InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filenameMolOne);
	    Molecule molOne=null;
	    Molecule molTwo=null;
	    Map mappedAtoms=new HashMap();
	    MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
	    molOne = (Molecule)reader.read(new Molecule());
		
	    ins = this.getClass().getClassLoader().getResourceAsStream(filenameMolTwo);
	    reader = new MDLV2000Reader(ins, Mode.STRICT);
	    molTwo = (Molecule)reader.read(new Molecule());
	   
	    mappedAtoms=AtomMappingTools.mapAtomsOfAlignedStructures(molOne, molTwo, mappedAtoms);
	    //logger.debug("mappedAtoms:"+mappedAtoms.toString());
	    //logger.debug("***** ANGLE VARIATIONS *****");
	    double AngleRMSD=GeometryTools.getAngleRMSD(molOne,molTwo,mappedAtoms);
	    //logger.debug("The Angle RMSD between the first and the second structure is :"+AngleRMSD);
	    //logger.debug("***** ALL ATOMS RMSD *****");
	    Assert.assertEquals(0.2, AngleRMSD, 0.1);
	    double AllRMSD=GeometryTools.getAllAtomRMSD(molOne,molTwo,mappedAtoms,true);
	    //logger.debug("The RMSD between the first and the second structure is :"+AllRMSD);
	    Assert.assertEquals(0.242, AllRMSD, 0.001);
	    //logger.debug("***** BOND LENGTH RMSD *****");
	    double BondLengthRMSD=GeometryTools.getBondLengthRMSD(molOne,molTwo,mappedAtoms,true);
	    //logger.debug("The Bond length RMSD between the first and the second structure is :"+BondLengthRMSD);
	    Assert.assertEquals(0.2, BondLengthRMSD, 0.1);
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
    	GeometryTools.rotate(ac, new Point2d(0,0),Math.PI/2);
    	Assert.assertEquals(atom1.getPoint2d().x,-1,.2);
    	Assert.assertEquals(atom1.getPoint2d().y,1,.2);
    	Assert.assertEquals(atom2.getPoint2d().x,0,.2);
    	Assert.assertEquals(atom2.getPoint2d().y,1,.2);
    	atom2.setPoint2d(new Point2d(0,0));
    	GeometryTools.rotate(ac, new Point2d(0,0),Math.PI);
    	Assert.assertFalse(Double.isNaN(atom2.getPoint2d().x));
    	Assert.assertFalse(Double.isNaN(atom2.getPoint2d().y));
    }
    
    
    public void testGetMinMax_IAtomContainer(){
    	Atom atom1=new Atom("C");
    	atom1.setPoint2d(new Point2d(1,1));
    	Atom atom2=new Atom("C");
    	atom2.setPoint2d(new Point2d(1,0));
    	IAtomContainer ac=DefaultChemObjectBuilder.getInstance().newAtomContainer();
    	ac.addAtom(atom1);
    	ac.addAtom(atom2);
    	double [] minmax=GeometryTools.getMinMax(ac);
    	Assert.assertEquals(minmax[0],1d,.1);
    	Assert.assertEquals(minmax[1],0d,.1);
    	Assert.assertEquals(minmax[2],1d,.1);
    	Assert.assertEquals(minmax[3],1d,.1);
    }
    
    
    public void testRotate_IAtom_Point3d_Point3d_double(){
    	Atom atom1=new Atom("C");
    	atom1.setPoint3d(new Point3d(1,1,0));
    	GeometryTools.rotate(atom1, new Point3d(2,0,0), new Point3d(2,2,0), 90);
    	assertEquals(new Point3d(2.0, 1.0, 1.0), atom1.getPoint3d(),0.2);
    }
    
    public void testNormalize_Point3d(){
    	Point3d p=new Point3d(1,1,0);
    	GeometryTools.normalize(p);
    	Assert.assertEquals(p.x,0.7,.1);
    	Assert.assertEquals(p.y,0.7,.1);
    	Assert.assertEquals(p.z,0.0,.1);
    }
    
    public void testGet2DCenter_IAtomContainer(){
    	Atom atom1=new Atom("C");
    	atom1.setPoint2d(new Point2d(1,1));
    	Atom atom2=new Atom("C");
    	atom2.setPoint2d(new Point2d(1,0));
    	IAtomContainer ac=DefaultChemObjectBuilder.getInstance().newAtomContainer();
    	ac.addAtom(atom1);
    	ac.addAtom(atom2);
    	Point2d p=GeometryTools.get2DCenter(ac);
    	Assert.assertEquals(p.x,1.0,.1);
    	Assert.assertEquals(p.y,0.5,.1);
    }

    public void testGet2DCenterOfMass_IAtomContainer(){
    	Atom atom1=new Atom("C");
    	atom1.setPoint2d(new Point2d(1,1));
    	Atom atom2=new Atom("C");
    	atom2.setPoint2d(new Point2d(1,0));
    	IAtomContainer ac=DefaultChemObjectBuilder.getInstance().newAtomContainer();
    	ac.addAtom(atom1);
    	ac.addAtom(atom2);
    	Point2d p=GeometryTools.get2DCentreOfMass(ac);
    	Assert.assertEquals(p.x,1.0,.1);
    	Assert.assertEquals(p.y,0.5,.1);
    }

    public void testGet2DCenter_arrayIAtom(){
    	Atom atom1=new Atom("C");
    	atom1.setPoint2d(new Point2d(1,1));
    	Atom atom2=new Atom("C");
    	atom2.setPoint2d(new Point2d(1,0));
    	IAtom[] array=new IAtom[2];
    	array[0]=atom1;
    	array[1]=atom2;
    	Point2d p=GeometryTools.get2DCenter(array);
    	Assert.assertEquals(p.x,1.0,.1);
    	Assert.assertEquals(p.y,0.5,.1);
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
    	Point2d p=GeometryTools.get2DCenter(ac);
    	Assert.assertEquals(p.x,1.0,.1);
    	Assert.assertEquals(p.y,0.5,.1);
    }
    
    
    public void testGet2DCenter_Iterator(){
    	Atom atom1=new Atom("C");
    	atom1.setPoint2d(new Point2d(1,1));
    	Atom atom2=new Atom("C");
    	atom2.setPoint2d(new Point2d(1,0));
    	IAtomContainer ac=DefaultChemObjectBuilder.getInstance().newAtomContainer();
    	ac.addAtom(atom1);
    	ac.addAtom(atom2);
    	Point2d p=GeometryTools.get2DCenter(ac.atoms());
    	Assert.assertEquals(p.x,1.0,.1);
    	Assert.assertEquals(p.y,0.5,.1);
    }

    public void testHas2DCoordinates_IAtom() {
    	Atom atom1=new Atom("C");
    	atom1.setPoint2d(new Point2d(1,1));
    	Assert.assertTrue(GeometryTools.has2DCoordinates(atom1));

    	atom1=new Atom("C");
    	atom1.setPoint3d(new Point3d(1,1,1));
    	Assert.assertFalse(GeometryTools.has2DCoordinates(atom1));
    }

    public void testHas2DCoordinates_IBond() {
    	Atom atom1=new Atom("C");
    	atom1.setPoint2d(new Point2d(1,1));
    	Atom atom2=new Atom("C");
    	atom2.setPoint2d(new Point2d(1,0));
    	IBond bond = new Bond(atom1, atom2);
    	Assert.assertTrue(GeometryTools.has2DCoordinates(bond));

    	atom1=new Atom("C");
    	atom1.setPoint3d(new Point3d(1,1,1));
    	atom2=new Atom("C");
    	atom2.setPoint3d(new Point3d(1,0,5));
    	bond = new Bond(atom1, atom2);
    	Assert.assertFalse(GeometryTools.has2DCoordinates(bond));
    }

    public void testHas2DCoordinatesNew_IAtomContainer() {
    	Atom atom1=new Atom("C");
    	atom1.setPoint2d(new Point2d(1,1));
    	Atom atom2=new Atom("C");
    	atom2.setPoint2d(new Point2d(1,0));
    	IAtomContainer container = new AtomContainer();
    	container.addAtom(atom1);
    	container.addAtom(atom2);
    	Assert.assertEquals(2, GeometryTools.has2DCoordinatesNew(container));

    	atom1=new Atom("C");
    	atom1.setPoint2d(new Point2d(1,1));
    	atom2=new Atom("C");
    	atom2.setPoint3d(new Point3d(1,0,1));
    	container = new AtomContainer();
    	container.addAtom(atom1);
    	container.addAtom(atom2);
    	Assert.assertEquals(1, GeometryTools.has2DCoordinatesNew(container));

    	atom1=new Atom("C");
    	atom1.setPoint3d(new Point3d(1,1,1));
    	atom2=new Atom("C");
    	atom2.setPoint3d(new Point3d(1,0,5));
    	container = new AtomContainer();
    	container.addAtom(atom1);
    	container.addAtom(atom2);
    	Assert.assertEquals(0, GeometryTools.has2DCoordinatesNew(container));
    }

    public void testHas3DCoordinates_IAtomContainer() {
    	Atom atom1=new Atom("C");
    	atom1.setPoint2d(new Point2d(1,1));
    	Atom atom2=new Atom("C");
    	atom2.setPoint2d(new Point2d(1,0));
    	IAtomContainer container = new AtomContainer();
    	container.addAtom(atom1);
    	container.addAtom(atom2);
    	Assert.assertFalse(GeometryTools.has3DCoordinates(container));

    	atom1=new Atom("C");
    	atom1.setPoint3d(new Point3d(1,1,1));
    	atom2=new Atom("C");
    	atom2.setPoint3d(new Point3d(1,0,5));
    	container = new AtomContainer();
    	container.addAtom(atom1);
    	container.addAtom(atom2);
    	Assert.assertTrue(GeometryTools.has3DCoordinates(container));
    }
    
    public void testTranslateAllPositive_IAtomContainer_HashMap(){
    	Atom atom1=new Atom("C");
    	atom1.setPoint2d(new Point2d(-1,-1));
    	Atom atom2=new Atom("C");
    	atom2.setPoint2d(new Point2d(1,0));
    	IAtomContainer ac=DefaultChemObjectBuilder.getInstance().newAtomContainer();
    	ac.addAtom(atom1);
    	ac.addAtom(atom2);
    	GeometryTools.translateAllPositive(ac);
    	Assert.assertEquals(atom1.getPoint2d().x,0.0, 0.01);
    	Assert.assertEquals(atom1.getPoint2d().y,0.0, 0.01);
    	Assert.assertEquals(atom2.getPoint2d().x,2.0, 0.01);
    	Assert.assertEquals(atom2.getPoint2d().y,1.0, 0.01);
    }
    
    
    public void testGetLength2D_IBond_HashMap(){
    	Atom atom1=new Atom("C");
    	atom1.setPoint2d(new Point2d(-1,-1));
    	Atom atom2=new Atom("C");
    	atom2.setPoint2d(new Point2d(1,0));
    	IBond bond=new Bond(atom1,atom2);
    	IAtomContainer ac=DefaultChemObjectBuilder.getInstance().newAtomContainer();
    	ac.addAtom(atom1);
    	ac.addAtom(atom2);
    	Assert.assertEquals(GeometryTools.getLength2D(bond),2.23,0.01);
    }

}

