/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *   *
 *  Copyright (C) 1997-2004  The Chemistry Development Kit (CDK) project
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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.test.modeling.builder3d;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.modeling.builder3d.ModelBuilder3D;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.HydrogenAdder;
import javax.vecmath.Point3d;
/**
 *  Description of the Class
 *
 * @cdk.module test
 *
 *@author     chhoppe
 *@cdk.created    2004-11-04
 */
public class ModelBuilder3dTest extends TestCase {
	
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
	public void testModelBuilder3D(){
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
					assertTrue(c_coord.equals(mol.getAtomAt(i).getPoint3d()));
				}else if(i==1){
					assertTrue(f_coord.equals(mol.getAtomAt(i).getPoint3d()));
				}else if(i==2){
					assertTrue(h1_coord.equals(mol.getAtomAt(i).getPoint3d()));
				}else if(i==3){
					assertTrue(h2_coord.equals(mol.getAtomAt(i).getPoint3d()));
				}else if(i==4){
					assertTrue(h3_coord.equals(mol.getAtomAt(i).getPoint3d()));
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
				if (mol.getAtomAt(i).getPoint3d()!=null){
					assertTrue(true);
				}else{
					assertTrue(false);
				}
				
			}
			System.out.println("Layout molecule with SMILE:"+smile);	
		} catch (Exception exc)
		{
			System.out.println("Cannot layout molecule with SMILE:"+smile);
			if (standAlone)
			{
				exc.printStackTrace();
			}
			fail(exc.toString());
		}
		
	}
}
