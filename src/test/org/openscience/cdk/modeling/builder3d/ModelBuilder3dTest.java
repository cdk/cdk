/*  $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.geometry.GeometryToolsInternalCoordinates;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.modeling.builder3d.ModelBuilder3D;
import org.openscience.cdk.nonotify.NNMolecule;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 *  Description of the Class
 *
 * @cdk.module test-builder3d
 *
 *@author     chhoppe
 *@cdk.created    2004-11-04
 */
public class ModelBuilder3dTest extends NewCDKTestCase {
	
	boolean standAlone = false;


    /**
	 *  Sets the standAlone attribute 
	 *
	 *@param  standAlone  The new standAlone value
	 */
	public void setStandAlone(boolean standAlone)
	{
		this.standAlone = standAlone;
	}
	
	

	 //  A unit test for JUnit with methylenfluoride\
    @Test
    public void testModelBuilder3D_CF() throws Exception {
		ModelBuilder3D mb3d = ModelBuilder3D.getInstance();
		Point3d c_coord=new Point3d(1.392, 0.0, 0.0);
		Point3d f_coord=new Point3d(0.0, 0.0, 0.0);
		Point3d h1_coord=new Point3d(1.7439615035767404, 1.0558845107302222, 0.0);
		Point3d h2_coord=new Point3d(1.7439615035767404, -0.5279422553651107, 0.914422809754875);
		Point3d h3_coord=new Point3d(1.7439615035767402, -0.5279422553651113, -0.9144228097548747);

		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("CF");
		addExplicitHydrogens(mol);
		//mb3d.setTemplateHandler();
		mol = mb3d.generate3DCoordinates(mol, false);
		for (int i=0;i<mol.getAtomCount();i++){
			if (i==0){
				Assert.assertEquals(c_coord.x, mol.getAtom(i).getPoint3d().x, 0.0001);
				Assert.assertEquals(c_coord.y, mol.getAtom(i).getPoint3d().y, 0.0001);
				Assert.assertEquals(c_coord.z, mol.getAtom(i).getPoint3d().z, 0.0001);
			}else if(i==1){
				Assert.assertEquals(f_coord.x, mol.getAtom(i).getPoint3d().x, 0.0001);
				Assert.assertEquals(f_coord.y, mol.getAtom(i).getPoint3d().y, 0.0001);
				Assert.assertEquals(f_coord.z, mol.getAtom(i).getPoint3d().z, 0.0001);
			}else if(i==2){
				Assert.assertEquals(h1_coord.x, mol.getAtom(i).getPoint3d().x, 0.0001);
				Assert.assertEquals(h1_coord.y, mol.getAtom(i).getPoint3d().y, 0.0001);
				Assert.assertEquals(h1_coord.z, mol.getAtom(i).getPoint3d().z, 0.0001);
			}else if(i==3){
				Assert.assertEquals(h2_coord.x, mol.getAtom(i).getPoint3d().x, 0.0001);
				Assert.assertEquals(h2_coord.y, mol.getAtom(i).getPoint3d().y, 0.0001);
				Assert.assertEquals(h2_coord.z, mol.getAtom(i).getPoint3d().z, 0.0001);
			}else if(i==4){
				Assert.assertEquals(h3_coord.x, mol.getAtom(i).getPoint3d().x, 0.0001);
				Assert.assertEquals(h3_coord.y, mol.getAtom(i).getPoint3d().y, 0.0001);
				Assert.assertEquals(h3_coord.z, mol.getAtom(i).getPoint3d().z, 0.0001);
			}
		}

    }

    @Test
    public void testModelBuilder3D_CccccC() throws Exception {
		ModelBuilder3D mb3d=ModelBuilder3D.getInstance();
		String smile="CccccC";
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles(smile);
		addExplicitHydrogens(mol);
		mol = mb3d.generate3DCoordinates(mol, false);
		for (int i=0;i<mol.getAtomCount();i++){
			Assert.assertNotNull(mol.getAtom(i).getPoint3d());
		}
		//logger.debug("Layout molecule with SMILE: "+smile);
    }

    @Test
    public void testModelBuilder3D_c1ccccc1C0() throws Exception {

    	if (!this.runSlowTests()) Assert.fail("Slow tests turned of");

    	ModelBuilder3D mb3d=ModelBuilder3D.getInstance();
    	String smile="c1ccccc1C=0";
    	SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
    	IMolecule mol = sp.parseSmiles(smile);
    	addExplicitHydrogens(mol);
    	mb3d.generate3DCoordinates(mol, false);
    	for(int i=0;i<mol.getAtomCount();i++){
    		Assert.assertNotNull(mol.getAtom(i).getPoint3d());
    	}
    }

    @Test
    public void testModelBuilder3D_Konstanz() throws Exception {
    	if (!this.runSlowTests()) Assert.fail("Slow tests turned of");
    	
		ModelBuilder3D mb3d=ModelBuilder3D.getInstance();
		String smile="C12(-[H])-C3(-C(-[H])(-[H])-C(-C4(-C5(-C(-Cl)(-Cl)-C(-C-3-4-[H])(-Cl)-C(-Cl)(-[H])-C-5(-Cl)-[H])-Cl)-[H])(-[H])-C-2(-O-1)-[H])-[H]";
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles(smile);
		addExplicitHydrogens(mol);
		mol = mb3d.generate3DCoordinates(mol, false);
    	for(int i=0;i<mol.getAtomCount();i++){
    		Assert.assertNotNull(mol.getAtom(i).getPoint3d());
    	}
    }

    @Test
    public void xtestModelBuilder3D_Konstanz2() throws Exception {
    	if (!this.runSlowTests()) Assert.fail("Slow tests turned of");
    	
		ModelBuilder3D mb3d=ModelBuilder3D.getInstance();
		String smile="c1(:c(:c(:c(-[H]):c(-Cl):c:1-[H])-[H])-[H])-[H]";
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles(smile);
		addExplicitHydrogens(mol);
		mol = mb3d.generate3DCoordinates(mol, false);
    	for(int i=0;i<mol.getAtomCount();i++){
    		Assert.assertNotNull(mol.getAtom(i).getPoint3d());
    	}
    }

    @Test
    public void testModelBuilder3D_C1CCCCCCC1CC() throws Exception{
    	if (!this.runSlowTests()) Assert.fail("Slow tests turned of");

    	ModelBuilder3D mb3d=ModelBuilder3D.getInstance();
    	String smile="C1CCCCCCC1CC";
    	SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
    	IMolecule mol = sp.parseSmiles(smile);
    	addExplicitHydrogens(mol);
    	mol = mb3d.generate3DCoordinates(mol, false);
    	for(int i=0;i<mol.getAtomCount();i++){
    		Assert.assertNotNull(mol.getAtom(i).getPoint3d());
    	}
    }

    /**
     * Bug #1610997 says the modelbulder does not work if 2d coordinates exist before - we test this here
     * 
     * @cdk.bug 1610997
     */
    @Test
    public void testModelBuilder3D_CCCCCCCCCC_with2d() throws Exception{
    	if (!this.runSlowTests()) Assert.fail("Slow tests turned of");
    	
		ModelBuilder3D mb3d=ModelBuilder3D.getInstance();
		String smile="CCCCCCCCCC";
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles(smile);
		for(int i=0;i<mol.getAtomCount();i++){
			mol.getAtom(i).setPoint2d(new Point2d(1,1));
		}
		addExplicitHydrogens(mol);
		mol = mb3d.generate3DCoordinates(mol, false);
    	for(int i=0;i<mol.getAtomCount();i++){
    		Assert.assertNotNull(mol.getAtom(i).getPoint3d());
    	}
	}
    
    /**
     * @cdk.bug 1315823
     */
    @Test
    public void testModelBuilder3D_232() throws Exception{
    	if (!this.runSlowTests()) Assert.fail("Slow tests turned of");

    	ModelBuilder3D mb3d=ModelBuilder3D.getInstance();
    	String filename = "data/mdl/allmol232.mol";
    	InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
    	MDLV2000Reader reader = new MDLV2000Reader(ins);
    	ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
    	List containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
    	IMolecule ac = new NNMolecule((IAtomContainer)containersList.get(0));
    	addExplicitHydrogens(ac);
    	ac = mb3d.generate3DCoordinates(ac, false);
    	Assert.assertNotNull(ac.getAtom(0).getPoint3d());
    	double avlength=GeometryToolsInternalCoordinates.getBondLengthAverage3D(ac);
    	for(int i=0;i<ac.getBondCount();i++){
    		double distance=ac.getBond(i).getAtom(0).getPoint3d().distance(ac.getBond(i).getAtom(1).getPoint3d());
    		Assert.assertTrue("Unreasonable bond length (" + distance + ") for bond " + i,
    			distance >= avlength/2 && distance <= avlength*2);
    	}
    }

    @Test
    public void testModelBuilder3D_231() throws Exception{
    	if (!this.runSlowTests()) Assert.fail("Slow tests turned of");

    	ModelBuilder3D mb3d=ModelBuilder3D.getInstance();
    	String filename = "data/mdl/allmol231.mol";
    	InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
    	MDLV2000Reader reader = new MDLV2000Reader(ins);
    	ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
    	List containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
    	IMolecule ac= new NNMolecule((IAtomContainer)containersList.get(0));
    	addExplicitHydrogens(ac);
    	ac = mb3d.generate3DCoordinates(ac, false);
    	for(int i=0;i<ac.getAtomCount();i++){
    		Assert.assertNotNull(ac.getAtom(i).getPoint3d());
    	}
    }

    
    /**
     * Test for SF bug #1309731.
     * @cdk.bug 1309731
     */
    @Test
    public void testModelBuilder3D_keepChemObjectIDs() throws Exception {
    	ModelBuilder3D mb3d = ModelBuilder3D.getInstance();

    	IMolecule methanol = new org.openscience.cdk.Molecule();
    	IChemObjectBuilder builder = methanol.getBuilder();

    	IAtom carbon1 = builder.newAtom("C");
    	carbon1.setID("carbon1");
    	methanol.addAtom(carbon1);
    	for (int i=0; i<3; i++) {
    		IAtom hydrogen = builder.newAtom("H");
    		methanol.addAtom(hydrogen);
    		methanol.addBond(builder.newBond(carbon1, hydrogen, IBond.Order.SINGLE));
    	}
    	IAtom oxygen1 = builder.newAtom("O");
    	oxygen1.setID("oxygen1");
    	methanol.addAtom(oxygen1);
    	methanol.addBond(builder.newBond(carbon1, oxygen1, IBond.Order.SINGLE));
    	IAtom hydrogen = builder.newAtom("H");
    	methanol.addAtom(hydrogen);
    	methanol.addBond(builder.newBond(hydrogen, oxygen1, IBond.Order.SINGLE));

    	Assert.assertEquals(6, methanol.getAtomCount());
    	Assert.assertEquals(5, methanol.getBondCount());

    	mb3d.generate3DCoordinates(methanol, false);

    	Assert.assertEquals("carbon1", carbon1.getID());
    	Assert.assertEquals("oxygen1", oxygen1.getID());
    }

    /*
	 * this is a test contributed by mario baseda / see bug #1610997
	 *  @cdk.bug 1610997
	 */
    @Test
    public void test_LocalWorkerModel3DBuildersWithMM2ForceField() throws Exception{
		if (!this.runSlowTests()) Assert.fail("Slow tests turned of");

		boolean notCalculatedResults = false;
        List<IAtomContainer> inputList = new ArrayList<IAtomContainer>();

		////////////////////////////////////////////////////////////////////////////////////////////
		//generate the input molecules. This are molecules without x, y, z coordinats 

		String[] smiles = new String[] {"CC", "OCC", "O(C)CCC", "c1ccccc1", "C(=C)=C","OCC=CCc1ccccc1(C=C)", "O(CC=C)CCN", "CCCCCCCCCCCCCCC", "OCC=CCO", "NCCCCN"};
		SmilesParser sp = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());
		IAtomContainer[] atomContainer = new IAtomContainer[smiles.length];
		for (int i = 0; i < smiles.length; i++) {
			atomContainer[i] = sp.parseSmiles(smiles[i]);

			inputList.add(atomContainer[i]);
		}
		System.out.println(inputList.size());
		///////////////////////////////////////////////////////////////////////////////////////////
		// Generate 2D coordinats for the input molecules with the Structure Diagram Generator

		StructureDiagramGenerator str;
		List<IAtomContainer> resultList = new ArrayList<IAtomContainer>();
        for (IAtomContainer molecules : inputList) {
            str = new StructureDiagramGenerator();
            str.setMolecule((IMolecule) molecules);
            str.generateCoordinates();
            resultList.add(str.getMolecule());
        }
		inputList = resultList;

		/////////////////////////////////////////////////////////////////////////////////////////////
		// Delete x and y coordinats

        for (IAtomContainer molecules : inputList) {
            for (Iterator atom = molecules.atoms(); atom.hasNext();) {
                Atom last = (Atom) atom.next();
                last.setPoint2d(null);
            }
        }

		////////////////////////////////////////////////////////////////////////////////////////////////////
		// Test for the method Model3DBuildersWithMM2ForceField 

		ModelBuilder3D mb3d=ModelBuilder3D.getInstance();
        for (IAtomContainer molecules : inputList) {
            IMolecule mol = molecules.getBuilder().newMolecule(molecules);
            /*for(int i=0;i<mol.getAtomCount();i++){
                     mol.getAtom(i).setFlag(CDKConstants.ISPLACED,false);
                     mol.getAtom(i).setFlag(CDKConstants.VISITED,false);
                   } */
            mol = mb3d.generate3DCoordinates(mol, false);
            System.out.println("Calculation done");
        }

        for (IAtomContainer molecules : inputList) {            
            for (Iterator atom = molecules.atoms(); atom.hasNext();) {
                Atom last = (Atom) atom.next();
                if (last.getPoint3d() == null) notCalculatedResults = true;
            }
        }
		Assert.assertEquals(false, notCalculatedResults);
	}
}
