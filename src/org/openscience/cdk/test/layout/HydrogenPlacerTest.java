/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2003  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 */
package org.openscience.cdk.test.layout;

import java.io.InputStream;
import java.io.InputStreamReader;

import javax.vecmath.Point2d;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.applications.swing.MoleculeViewer2D;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.layout.HydrogenPlacer;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.LoggingTool;

/**
 * @cdkPackage test
 */
public class HydrogenPlacerTest extends TestCase {
    
    public boolean standAlone = false;
    private LoggingTool logger = null;
    
    public HydrogenPlacerTest(String name) {
        super(name);
    }

    public void setUp() 
    {
	logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
    }

    public static Test suite() {
        return new TestSuite(HydrogenPlacerTest.class);
    }

    public void testPlaceHydrogens2D() throws Exception {
	    HydrogenPlacer hydrogenPlacer = new HydrogenPlacer();
        Molecule dichloromethane = new Molecule();
        Atom carbon = new Atom("C");
        Point2d carbonPos = new Point2d(0.0,0.0);
        carbon.setPoint2D(carbonPos);
        Atom h1 = new Atom("H");
        Atom h2 = new Atom("H");
        Atom cl1 = new Atom("Cl");
        Point2d cl1Pos = new Point2d(0.0,-1.0);
        cl1.setPoint2D(cl1Pos);
        Atom cl2 = new Atom("Cl");
        Point2d cl2Pos = new Point2d(-1.0,0.0);
        cl2.setPoint2D(cl2Pos);
        dichloromethane.addAtom(carbon);
        dichloromethane.addAtom(h1);
        dichloromethane.addAtom(h2);
        dichloromethane.addAtom(cl1);
        dichloromethane.addAtom(cl2);
        dichloromethane.addBond(new Bond(carbon, h1));
        dichloromethane.addBond(new Bond(carbon, h2));
        dichloromethane.addBond(new Bond(carbon, cl1));
        dichloromethane.addBond(new Bond(carbon, cl2));

        assertNull(h1.getPoint2D());
        assertNull(h2.getPoint2D());
        
        // generate new coords
        hydrogenPlacer.placeHydrogens2D(dichloromethane, carbon);
        if (standAlone) MoleculeViewer2D.display(dichloromethane, false);
        // check that previously set coordinates are kept
        assertEquals(carbonPos, carbon.getPoint2D(), 0.01);
        assertEquals(cl1Pos, cl1.getPoint2D(), 0.01);
        assertEquals(cl2Pos, cl2.getPoint2D(), 0.01);
        assertNotNull(h1.getPoint2D());
        assertNotNull(h2.getPoint2D());
    }
    

    private void assertEquals(Point2d p1, Point2d p2, double error) throws Exception {
        assertEquals(p1.x, p2.x, error);
        assertEquals(p1.y, p2.y, error);
    }
    
    
    /* This one tests adding hydrogens to all atoms of a molecule and doing the layout for them.
    *  It is intended for visually checking the work of HydrogenPlacer, not to be run
    *  as a JUnit test. Thus the name withouth "test".
    */
	public void visualFullMolecule2DEvaluation()
	{
		HydrogenPlacer hydrogenPlacer = new HydrogenPlacer();
                String filename = "data/mdl/reserpine.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		try {
		    MDLReader reader = new MDLReader(new InputStreamReader(ins));
		    ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
		    ChemSequence seq = chemFile.getChemSequence(0);
		    ChemModel model = seq.getChemModel(0);
		    Molecule mol = model.getSetOfMolecules().getMolecule(0);
		    double bondLength = GeometryTools.getBondLengthAverage(mol);
		    HydrogenAdder ha = new HydrogenAdder();
		    logger.debug("Read Reserpine");
		    logger.debug("Starting addition of H's");
		    ha.addExplicitHydrogensToSatisfyValency(mol);
		    logger.debug("ended addition of H's");
		    hydrogenPlacer.placeHydrogens2D(mol, bondLength);
            if (standAlone) {
                MoleculeViewer2D.display(mol, false);
            }
		} catch (Exception e) {
		    e.printStackTrace();
		    fail(e.toString());
		}
	}

       	public static void main(String[] args)
	{
		try{
			HydrogenPlacerTest hpt = new HydrogenPlacerTest("HydrogenPlacerTest");
			hpt.setUp();
			hpt.standAlone = true;
			//hpt.testPlaceHydrogens2D();
			hpt.visualFullMolecule2DEvaluation();			
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
	}
}

