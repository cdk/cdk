/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
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
package org.openscience.cdk.charges;


import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.charges.InductivePartialCharges;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.CDKTestCase;

import javax.vecmath.Point3d;
import java.io.IOException;

/**
 *  TestSuite that runs a test for the MMFF94PartialCharges.
 *
 * @cdk.module test-charges
 *
 *@author        mfe4
 *@cdk.created       2004-11-04
 */

public class InductivePartialChargesTest extends CDKTestCase {

    private static IAtomContainer mol;

    @BeforeClass
    public static void makeMoleucle() {
        mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newAtom("C");
        IAtom atom2 = DefaultChemObjectBuilder.getInstance().newAtom("Cl");
        IAtom atom3 = DefaultChemObjectBuilder.getInstance().newAtom("Br");
        IAtom atom4 = DefaultChemObjectBuilder.getInstance().newAtom("H");
        IAtom atom5 = DefaultChemObjectBuilder.getInstance().newAtom("O");

        IBond bond1 = DefaultChemObjectBuilder.getInstance().newBond(atom1, atom2, IBond.Order.SINGLE);
        IBond bond2 = DefaultChemObjectBuilder.getInstance().newBond(atom1, atom3, IBond.Order.SINGLE);
        IBond bond3 = DefaultChemObjectBuilder.getInstance().newBond(atom1, atom4, IBond.Order.SINGLE);
        IBond bond4 = DefaultChemObjectBuilder.getInstance().newBond(atom1, atom5, IBond.Order.SINGLE);

        mol.addAtom(atom1);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addAtom(atom5);

        mol.addBond(bond1);
        mol.addBond(bond2);
        mol.addBond(bond3);
        mol.addBond(bond4);
    }

    /**
	 *  A unit test for JUnit with beta-amino-acetic-acid
	 *
	 *@exception  ClassNotFoundException  Description of the Exception
	 *@exception  CDKException            Description of the Exception
	 *@exception  java.lang.Exception     Description of the Exception
	 */
    @Test
    public void testCalculateCharges_IAtomContainer() throws java.lang.Exception {
    	double [] testResult={0.197,-0.492,0.051,0.099,0.099};
		Point3d c_coord=new Point3d(1.392, 0.0, 0.0);
		Point3d f_coord=new Point3d(0.0, 0.0, 0.0);
		Point3d h1_coord=new Point3d(1.7439615035767404, 1.0558845107302222, 0.0);
		Point3d h2_coord=new Point3d(1.7439615035767404, -0.5279422553651107, 0.914422809754875);
		Point3d h3_coord=new Point3d(1.7439615035767402, -0.5279422553651113, -0.9144228097548747);
		
		Molecule mol = new Molecule(); // molecule is CF 
		
		Atom c = new Atom("C"); 
		mol.addAtom(c); 
		c.setPoint3d(c_coord);
		
		Atom f = new Atom("F"); 
		mol.addAtom(f); 
		f.setPoint3d(f_coord);
		
		Atom h1 = new Atom("H"); 
		mol.addAtom(h1); 
		h1.setPoint3d(h1_coord);
		
		Atom h2 = new Atom("H"); 
		mol.addAtom(h2); 
		h2.setPoint3d(h2_coord);
		
		Atom h3 = new Atom("H"); 
		mol.addAtom(h3); 
		h3.setPoint3d(h3_coord);
		
		mol.addBond(0, 1, IBond.Order.SINGLE); // 1
		mol.addBond(0, 2, IBond.Order.SINGLE); // 1
		mol.addBond(0, 3, IBond.Order.SINGLE); // 1
		mol.addBond(0, 4, IBond.Order.SINGLE); // 1
		InductivePartialCharges ipc = new InductivePartialCharges();
		ipc.assignInductivePartialCharges(mol);
		for (int i = 0; i < mol.getAtomCount(); i++) {
			Assert.assertEquals(testResult[i], ((Double)mol.getAtom(i).getProperty("InductivePartialCharge")).doubleValue(), 0.1);
			//logger.debug("CHARGE AT " + ac.getAtomAt(i).getSymbol() + " " + ac.getAtomAt(i).getProperty("MMFF94charge"));
		}
    }
    /**
	 *  A unit test for JUnit with beta-amino-acetic-acid
	 *
	 *@exception  ClassNotFoundException  Description of the Exception
	 *@exception  CDKException            Description of the Exception
	 *@exception  java.lang.Exception     Description of the Exception
	 */
    @Test
    public void testInductivePartialCharges() throws java.lang.Exception {
		double [] testResult={0.197,-0.492,0.051,0.099,0.099};
		Point3d c_coord=new Point3d(1.392, 0.0, 0.0);
		Point3d f_coord=new Point3d(0.0, 0.0, 0.0);
		Point3d h1_coord=new Point3d(1.7439615035767404, 1.0558845107302222, 0.0);
		Point3d h2_coord=new Point3d(1.7439615035767404, -0.5279422553651107, 0.914422809754875);
		Point3d h3_coord=new Point3d(1.7439615035767402, -0.5279422553651113, -0.9144228097548747);
		
		Molecule mol = new Molecule(); // molecule is CF 
		
		Atom c = new Atom("C"); 
		mol.addAtom(c); 
		c.setPoint3d(c_coord);
		
		Atom f = new Atom("F"); 
		mol.addAtom(f); 
		f.setPoint3d(f_coord);
		
		Atom h1 = new Atom("H"); 
		mol.addAtom(h1); 
		h1.setPoint3d(h1_coord);
		
		Atom h2 = new Atom("H"); 
		mol.addAtom(h2); 
		h2.setPoint3d(h2_coord);
		
		Atom h3 = new Atom("H"); 
		mol.addAtom(h3); 
		h3.setPoint3d(h3_coord);
		
		mol.addBond(0, 1, IBond.Order.SINGLE); // 1
		mol.addBond(0, 2, IBond.Order.SINGLE); // 1
		mol.addBond(0, 3, IBond.Order.SINGLE); // 1
		mol.addBond(0, 4, IBond.Order.SINGLE); // 1
		InductivePartialCharges ipc = new InductivePartialCharges();
		ipc.assignInductivePartialCharges(mol);
		for (int i = 0; i < mol.getAtomCount(); i++) {
			Assert.assertEquals(testResult[i], ((Double)mol.getAtom(i).getProperty("InductivePartialCharge")).doubleValue(), 0.1);
			//logger.debug("CHARGE AT " + ac.getAtomAt(i).getSymbol() + " " + ac.getAtomAt(i).getProperty("MMFF94charge"));
		}
	}

    @Test
    public void testGetPaulingElectronegativities() throws Exception, ClassNotFoundException {
        InductivePartialCharges ipc = new InductivePartialCharges();
        double[] eneg = ipc.getPaulingElectronegativities(mol, true);
        long[] expected = {};
        Assert.assertEquals("Error in C electronegativity", 2.20, eneg[0], 0.01);
        Assert.assertEquals("Error in Cl electronegativity", 3.28, eneg[1], 0.01);
        Assert.assertEquals("Error in Br electronegativity", 3.13, eneg[2], 0.01);
        Assert.assertEquals("Error in H electronegativity", 2.10, eneg[3], 0.01);
        Assert.assertEquals("Error in O electronegativity", 3.20, eneg[4], 0.01);
    }

    @Test
    public void testGetAtomicSoftness() throws IOException, ClassNotFoundException, CDKException {
        InductivePartialCharges ipc = new InductivePartialCharges();
        double softness = ipc.getAtomicSoftnessCore(mol, 0);
        Assert.fail("Not validated - need known values");
    }

}

