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

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryToolsInternalCoordinates;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
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
    public void testMapAtomsOfAlignedStructures(){
   	 
    	String filenameMolOne = "data/mdl/murckoTest6_3d_2.mol";
		String filenameMolTwo = "data/mdl/murckoTest6_3d.mol";
    	//String filenameMolTwo = "data/mdl/murckoTest6_3d_2.mol";
	    InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filenameMolOne);
	    Molecule molOne=null;
	    Molecule molTwo=null;
	    Map mappedAtoms=new HashMap();
	    try {
	    	MDLReader reader = new MDLReader(ins);
	        molOne = (Molecule)reader.read(new Molecule());
	    } catch (Exception exception) {
	        System.out.println("Error: Cannot read in mol1 due to "+exception.toString());
	    }
		
	    ins = this.getClass().getClassLoader().getResourceAsStream(filenameMolTwo);
	    try {
	    	MDLReader reader = new MDLReader(ins);
	        molTwo = (Molecule)reader.read(new Molecule());
	    } catch (Exception exception) {
	        System.out.println("Error: Cannot read in mol2 due to "+exception.toString());
	    }
	   
	    try {
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
	    } catch (CDKException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	    }
   }
}


