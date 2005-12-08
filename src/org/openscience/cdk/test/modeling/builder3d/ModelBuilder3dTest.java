/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *   *
 *  Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.modeling.builder3d;

import javax.vecmath.Point3d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.interfaces.Molecule;
import org.openscience.cdk.modeling.builder3d.ModelBuilder3D;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;
/**
 *  Description of the Class
 *
 * @cdk.module test
 *
 *@author     chhoppe
 *@cdk.created    2004-11-04
 */
public class ModelBuilder3dTest extends CDKTestCase {
	
	boolean standAlone = false;
	
	/**
	 *  Constructor for the ModelBuilder3dTest
	 *@param  name  Description of the Parameter
	 */
	public  ModelBuilder3dTest(){}


	/**
	 *  A unit test suite for JUnit
	 *
	 *@return    The test suite
	 */
	public static Test suite()
	{
		return new TestSuite(ModelBuilder3dTest.class);
	}
	
	/**
	 *  Sets the standAlone attribute 
	 *
	 *@param  standAlone  The new standAlone value
	 */
	public void setStandAlone(boolean standAlone)
	{
		this.standAlone = standAlone;
	}
	
	
	/**
	 *  A unit test for JUnit with methylenfluoride
	 */
	public void testModelBuilder3D_CF(){
		ModelBuilder3D mb3d=new ModelBuilder3D();
		HydrogenAdder hAdder=new HydrogenAdder();
		Point3d c_coord=new Point3d(1.392, 0.0, 0.0);
		Point3d f_coord=new Point3d(0.0, 0.0, 0.0);
		Point3d h1_coord=new Point3d(1.7439615035767404, 1.0558845107302222, 0.0);
		Point3d h2_coord=new Point3d(1.7439615035767404, -0.5279422553651107, 0.914422809754875);
		Point3d h3_coord=new Point3d(1.7439615035767402, -0.5279422553651113, -0.9144228097548747);
		try{
			SmilesParser sp = new SmilesParser();
			Molecule mol = sp.parseSmiles("CF");
			hAdder.addExplicitHydrogensToSatisfyValency(mol);
			//mb3d.setTemplateHandler();
			mb3d.setForceField("mm2");
			mb3d.setMolecule(mol,false);
			mb3d.generate3DCoordinates();
			mol = mb3d.getMolecule();
			for (int i=0;i<mol.getAtomCount();i++){
				if (i==0){
					assertEquals(c_coord.x, mol.getAtomAt(i).getPoint3d().x, 0.0001);
					assertEquals(c_coord.y, mol.getAtomAt(i).getPoint3d().y, 0.0001);
					assertEquals(c_coord.z, mol.getAtomAt(i).getPoint3d().z, 0.0001);
				}else if(i==1){
					assertEquals(f_coord.x, mol.getAtomAt(i).getPoint3d().x, 0.0001);
					assertEquals(f_coord.y, mol.getAtomAt(i).getPoint3d().y, 0.0001);
					assertEquals(f_coord.z, mol.getAtomAt(i).getPoint3d().z, 0.0001);
				}else if(i==2){
					assertEquals(h1_coord.x, mol.getAtomAt(i).getPoint3d().x, 0.0001);
					assertEquals(h1_coord.y, mol.getAtomAt(i).getPoint3d().y, 0.0001);
					assertEquals(h1_coord.z, mol.getAtomAt(i).getPoint3d().z, 0.0001);
				}else if(i==3){
					assertEquals(h2_coord.x, mol.getAtomAt(i).getPoint3d().x, 0.0001);
					assertEquals(h2_coord.y, mol.getAtomAt(i).getPoint3d().y, 0.0001);
					assertEquals(h2_coord.z, mol.getAtomAt(i).getPoint3d().z, 0.0001);
				}else if(i==4){
					assertEquals(h3_coord.x, mol.getAtomAt(i).getPoint3d().x, 0.0001);
					assertEquals(h3_coord.y, mol.getAtomAt(i).getPoint3d().y, 0.0001);
					assertEquals(h3_coord.z, mol.getAtomAt(i).getPoint3d().z, 0.0001);
				}
			}
			
		} catch (Exception exc)
		{
			if (standAlone)
			{
				exc.printStackTrace();
			}
			fail(exc.toString());
		}
    }
    
    public void testModelBuilder3D_CccccC(){
		ModelBuilder3D mb3d=new ModelBuilder3D();
		HydrogenAdder hAdder=new HydrogenAdder();
		String smile="CccccC";
		try{
			SmilesParser sp = new SmilesParser();
			Molecule mol = sp.parseSmiles(smile);
			hAdder.addExplicitHydrogensToSatisfyValency(mol);
			//mb3d.setTemplateHandler();
			mb3d.setForceField("mm2");
			mb3d.setMolecule(mol,false);
			mb3d.generate3DCoordinates();
			mol = mb3d.getMolecule();
			for (int i=0;i<mol.getAtomCount();i++){
				assertNotNull(mol.getAtomAt(i).getPoint3d());
			}
			//System.out.println("Layout molecule with SMILE: "+smile);
		} catch (Exception exc) {
			System.out.println("Cannot layout molecule with SMILES: "+smile);
			if (standAlone)
			{
				exc.printStackTrace();
			}
			fail(exc.toString());
        }
    }
    
    public void testModelBuilder3D_c1ccccc1C0(){
    	if (!this.runSlowTests()) fail("Slow tests turned of");
    	
		ModelBuilder3D mb3d=new ModelBuilder3D();
    HydrogenAdder hAdder=new HydrogenAdder();
    String smile="c1ccccc1C=0";
		try {
			SmilesParser sp = new SmilesParser();
			Molecule mol = sp.parseSmiles(smile);
			hAdder.addExplicitHydrogensToSatisfyValency(mol);
			mb3d.setTemplateHandler();
			mb3d.setMolecule(mol,false);
			mb3d.generate3DCoordinates();
		} catch (Exception exc) {
			System.out.println("Cannot layout molecule with SMILE: "+smile);
			if (standAlone)
			{
				exc.printStackTrace();
			}
    	fail(exc.toString());
		}
	}
}
