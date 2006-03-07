/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *
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

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.modeling.builder3d.ModelBuilder3D;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.LoggingTool;
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
	private LoggingTool logger;
	
	/**
	 *  Constructor for the ModelBuilder3dTest
	 *@param  name  Description of the Parameter
	 */
	public  ModelBuilder3dTest(){
		logger = new LoggingTool(this);
	}


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
			IMolecule mol = sp.parseSmiles("CF");
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
			IMolecule mol = sp.parseSmiles(smile);
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
			IMolecule mol = sp.parseSmiles(smile);
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

    public void testModelBuilder3D_C1CCCCCCC1CC() throws Exception{
    	if (!this.runSlowTests()) fail("Slow tests turned of");
    	
		ModelBuilder3D mb3d=new ModelBuilder3D();
    HydrogenAdder hAdder=new HydrogenAdder();
    String smile="C1CCCCCCC1CC";
		try {
			SmilesParser sp = new SmilesParser();
			IMolecule mol = sp.parseSmiles(smile);
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

    
    /**
     * Test for SF bug #1309731.
     */
    public void testModelBuilder3D_keepChemObjectIDs(){
		ModelBuilder3D mb3d = new ModelBuilder3D();
		
		IMolecule methanol = new org.openscience.cdk.Molecule();
		IChemObjectBuilder builder = methanol.getBuilder();
		
		IAtom carbon1 = builder.newAtom("C");
		carbon1.setID("carbon1");
		methanol.addAtom(carbon1);
		for (int i=0; i<3; i++) {
			IAtom hydrogen = builder.newAtom("H");
			methanol.addAtom(hydrogen);
			methanol.addBond(builder.newBond(carbon1, hydrogen, 1.0));
		}
		IAtom oxygen1 = builder.newAtom("O");
		oxygen1.setID("oxygen1");
		methanol.addAtom(oxygen1);
		methanol.addBond(builder.newBond(carbon1, oxygen1, 1.0));
		IAtom hydrogen = builder.newAtom("H");
		methanol.addAtom(hydrogen);
		methanol.addBond(builder.newBond(hydrogen, oxygen1, 1.0));
		
		assertEquals(6, methanol.getAtomCount());
		assertEquals(5, methanol.getBondCount());

		try {
			mb3d.setMolecule(methanol,false);
			mb3d.generate3DCoordinates();
		} catch (Exception exc) {
			logger.error("Cannot layout molecule: ", exc.getMessage());
			logger.debug(exc);
			fail(exc.getMessage());
		}
		
		assertEquals("carbon1", carbon1.getID());
		assertEquals("oxygen1", oxygen1.getID());
	}
}
