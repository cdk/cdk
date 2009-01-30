/* 
 * Copyright (C) 2006-2007  Sam Adams <sea36@users.sf.net>
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
package org.openscience.cdk.inchi;

import net.sf.jniinchi.INCHI_RET;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.*;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

/**
 * TestCase for the InChIGenerator.
 *
 * @cdk.module test-inchi
 *
 * @see org.openscience.cdk.inchi.InChIGenerator
 */
public class InChIGeneratorTest extends CDKTestCase {
    
    protected InChIGeneratorFactory factory;
    
    protected InChIGeneratorFactory getFactory() throws Exception {
        if (factory == null) {
            factory = new InChIGeneratorFactory();
        }
        return(factory);
    }

    
    /**
     * Tests element name is correctly passed to InChI.
     * 
     * @throws Exception
     */
    @Test public void testGetInchiFromChlorineAtom() throws Exception {
        IAtomContainer ac = new AtomContainer();
        ac.addAtom(new Atom("Cl"));
        InChIGenerator gen = getFactory().getInChIGenerator(ac);
        Assert.assertEquals(gen.getReturnStatus(), INCHI_RET.OKAY);
        Assert.assertEquals(gen.getInchi(), "InChI=1/Cl");
    }
    
    /**
     * Tests charge is correctly passed to InChI.
     * 
     * @throws Exception
     */
    @Test public void testGetInchiFromLithiumIon() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a = new Atom("Li");
        a.setFormalCharge(+1);
        ac.addAtom(a);
        InChIGenerator gen = getFactory().getInChIGenerator(ac);
        Assert.assertEquals(gen.getReturnStatus(), INCHI_RET.OKAY);
        Assert.assertEquals(gen.getInchi(), "InChI=1/Li/q+1");
    }
    
    /**
    * Tests isotopic mass is correctly passed to InChI.
    * 
    * @throws Exception
    */
    @Test public void testGetInchiFromChlorine37Atom() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a = new Atom("Cl");
        a.setMassNumber(37);
        ac.addAtom(a);
        InChIGenerator gen = getFactory().getInChIGenerator(ac);
        Assert.assertEquals(gen.getReturnStatus(), INCHI_RET.OKAY);
        Assert.assertEquals(gen.getInchi(), "InChI=1/Cl/i1+2");
    }
    
    /**
     * Tests implicit hydrogen count is correctly passed to InChI.
     * 
     * @throws Exception
     */
    @Test public void testGetInchiFromHydrogenChlorideImplicitH() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a = new Atom("Cl");
        a.setHydrogenCount(1);
        ac.addAtom(a);
        InChIGenerator gen = getFactory().getInChIGenerator(ac);
        Assert.assertEquals(gen.getReturnStatus(), INCHI_RET.OKAY);
        Assert.assertEquals(gen.getInchi(), "InChI=1/ClH/h1H");
    }
    
    /**
     * Tests radical state is correctly passed to InChI.
     * 
     * @throws Exception
     */
    @Test public void testGetInchiFromMethylRadical() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a = new Atom("C");
        a.setHydrogenCount(3);
        ac.addAtom(a);
        ac.addSingleElectron(new SingleElectron(a));
        InChIGenerator gen = getFactory().getInChIGenerator(ac);
        Assert.assertEquals(gen.getReturnStatus(), INCHI_RET.OKAY);
        Assert.assertEquals(gen.getInchi(), "InChI=1/CH3/h1H3");
    }
    
    /**
     * Tests single bond is correctly passed to InChI.
     * 
     * @throws Exception
     */
    @Test public void testGetInchiFromEthane() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a1 = new Atom("C");
        IAtom a2 = new Atom("C");
        a1.setHydrogenCount(3);
        a2.setHydrogenCount(3);
        ac.addAtom(a1);
        ac.addAtom(a2);
        ac.addBond(new Bond(a1, a2, CDKConstants.BONDORDER_SINGLE));
        InChIGenerator gen = getFactory().getInChIGenerator(ac);
        Assert.assertEquals(gen.getReturnStatus(), INCHI_RET.OKAY);
        Assert.assertEquals(gen.getInchi(), "InChI=1/C2H6/c1-2/h1-2H3");
    }
    
    /**
     * Tests double bond is correctly passed to InChI.
     * 
     * @throws Exception
     */
    @Test public void testGetInchiFromEthene() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a1 = new Atom("C");
        IAtom a2 = new Atom("C");
        a1.setHydrogenCount(2);
        a2.setHydrogenCount(2);
        ac.addAtom(a1);
        ac.addAtom(a2);
        ac.addBond(new Bond(a1, a2, CDKConstants.BONDORDER_DOUBLE));
        InChIGenerator gen = getFactory().getInChIGenerator(ac);
        Assert.assertEquals(gen.getReturnStatus(), INCHI_RET.OKAY);
        Assert.assertEquals(gen.getInchi(), "InChI=1/C2H4/c1-2/h1-2H2");
    }
    
    /**
     * Tests triple bond is correctly passed to InChI.
     * 
     * @throws Exception
     */
    @Test public void testGetInchiFromEthyne() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a1 = new Atom("C");
        IAtom a2 = new Atom("C");
        a1.setHydrogenCount(1);
        a2.setHydrogenCount(1);
        ac.addAtom(a1);
        ac.addAtom(a2);
        ac.addBond(new Bond(a1, a2, CDKConstants.BONDORDER_TRIPLE));
        InChIGenerator gen = getFactory().getInChIGenerator(ac);
        Assert.assertEquals(gen.getReturnStatus(), INCHI_RET.OKAY);
        Assert.assertEquals(gen.getInchi(), "InChI=1/C2H2/c1-2/h1-2H");
    }
    
    /**
     * Tests 2D coordinates are correctly passed to InChI.
     * 
     * @throws Exception
     */
    @Test public void testGetInchiEandZ12Dichloroethene2D() throws Exception {

        // (E)-1,2-dichloroethene
        IAtomContainer acE = new AtomContainer();
        IAtom a1E = new Atom("C", new Point2d(2.866, -0.250));
        IAtom a2E = new Atom("C", new Point2d(3.732, 0.250));
        IAtom a3E = new Atom("Cl", new Point2d(2.000, 2.500));
        IAtom a4E = new Atom("Cl", new Point2d(4.598, -0.250));
        a1E.setHydrogenCount(1);
        a2E.setHydrogenCount(1);
        acE.addAtom(a1E);
        acE.addAtom(a2E);
        acE.addAtom(a3E);
        acE.addAtom(a4E);

        acE.addBond(new Bond(a1E, a2E, CDKConstants.BONDORDER_DOUBLE));
        acE.addBond(new Bond(a1E, a2E, CDKConstants.BONDORDER_DOUBLE));
        acE.addBond(new Bond(a1E, a3E, CDKConstants.BONDORDER_SINGLE));
        acE.addBond(new Bond(a2E, a4E, CDKConstants.BONDORDER_SINGLE));
        
        InChIGenerator genE = getFactory().getInChIGenerator(acE);
        Assert.assertEquals(genE.getReturnStatus(), INCHI_RET.OKAY);
        Assert.assertEquals(genE.getInchi(), "InChI=1/C2H2Cl2/c3-1-2-4/h1-2H/b2-1+");
        
        
        // (Z)-1,2-dichloroethene
        IAtomContainer acZ = new AtomContainer();
        IAtom a1Z = new Atom("C", new Point2d(2.866, -0.440));
        IAtom a2Z = new Atom("C", new Point2d(3.732, 0.060));
        IAtom a3Z = new Atom("Cl", new Point2d(2.000, 0.060));
        IAtom a4Z = new Atom("Cl", new Point2d(3.732, 1.060));
        a1Z.setHydrogenCount(1);
        a2Z.setHydrogenCount(1);
        acZ.addAtom(a1Z);
        acZ.addAtom(a2Z);
        acZ.addAtom(a3Z);
        acZ.addAtom(a4Z);

        acZ.addBond(new Bond(a1Z, a2Z, CDKConstants.BONDORDER_DOUBLE));
        acZ.addBond(new Bond(a1Z, a2Z, CDKConstants.BONDORDER_DOUBLE));
        acZ.addBond(new Bond(a1Z, a3Z, CDKConstants.BONDORDER_SINGLE));
        acZ.addBond(new Bond(a2Z, a4Z, CDKConstants.BONDORDER_SINGLE));
        
        InChIGenerator genZ = getFactory().getInChIGenerator(acZ);
        Assert.assertEquals(genZ.getReturnStatus(), INCHI_RET.OKAY);
        Assert.assertEquals(genZ.getInchi(), "InChI=1/C2H2Cl2/c3-1-2-4/h1-2H/b2-1-");
    }
    
    
    /**
     * Tests 3D coordinates are correctly passed to InChI.
     * 
     * @throws Exception
     */
    @Test
    public void testGetInchiFromLandDAlanine3D() throws Exception {
        
        // L-Alanine
        IAtomContainer acL = new AtomContainer();
        IAtom a1L = new Atom("C", new Point3d(-0.358, 0.819, 20.655));
        IAtom a2L = new Atom("C", new Point3d(-1.598, -0.032, 20.905));
        IAtom a3L = new Atom("N", new Point3d(-0.275, 2.014, 21.574));
        IAtom a4L = new Atom("C", new Point3d(0.952, 0.043, 20.838));
        IAtom a5L = new Atom("O", new Point3d(-2.678, 0.479, 21.093));
        IAtom a6L = new Atom("O", new Point3d(-1.596, -1.239, 20.958));
        a1L.setHydrogenCount(1);
        a3L.setHydrogenCount(2);
        a4L.setHydrogenCount(3);
        a5L.setHydrogenCount(1);
        acL.addAtom(a1L);
        acL.addAtom(a2L);
        acL.addAtom(a3L);
        acL.addAtom(a4L);
        acL.addAtom(a5L);
        acL.addAtom(a6L);
        
        acL.addBond(new Bond(a1L, a2L, CDKConstants.BONDORDER_SINGLE));
        acL.addBond(new Bond(a1L, a3L, CDKConstants.BONDORDER_SINGLE));
        acL.addBond(new Bond(a1L, a4L, CDKConstants.BONDORDER_SINGLE));
        acL.addBond(new Bond(a2L, a5L, CDKConstants.BONDORDER_SINGLE));
        acL.addBond(new Bond(a2L, a6L, CDKConstants.BONDORDER_DOUBLE));
        
        InChIGenerator genL = getFactory().getInChIGenerator(acL);
        Assert.assertEquals(genL.getReturnStatus(), INCHI_RET.OKAY);
        Assert.assertEquals(genL.getInchi(), "InChI=1/C3H7NO2/c1-2(4)3(5)6/h2H,4H2,1H3,(H,5,6)/t2-/m0/s1");
        
        
        // D-Alanine
        IAtomContainer acD = new AtomContainer();
        IAtom a1D = new Atom("C", new Point3d(0.358, 0.819, 20.655));
        IAtom a2D = new Atom("C", new Point3d(1.598, -0.032, 20.905));
        IAtom a3D = new Atom("N", new Point3d(0.275, 2.014, 21.574));
        IAtom a4D = new Atom("C", new Point3d(-0.952, 0.043, 20.838));
        IAtom a5D = new Atom("O", new Point3d(2.678, 0.479, 21.093));
        IAtom a6D = new Atom("O", new Point3d(1.596, -1.239, 20.958));
        a1D.setHydrogenCount(1);
        a3D.setHydrogenCount(2);
        a4D.setHydrogenCount(3);
        a5D.setHydrogenCount(1);
        acD.addAtom(a1D);
        acD.addAtom(a2D);
        acD.addAtom(a3D);
        acD.addAtom(a4D);
        acD.addAtom(a5D);
        acD.addAtom(a6D);
        
        acD.addBond(new Bond(a1D, a2D, CDKConstants.BONDORDER_SINGLE));
        acD.addBond(new Bond(a1D, a3D, CDKConstants.BONDORDER_SINGLE));
        acD.addBond(new Bond(a1D, a4D, CDKConstants.BONDORDER_SINGLE));
        acD.addBond(new Bond(a2D, a5D, CDKConstants.BONDORDER_SINGLE));
        acD.addBond(new Bond(a2D, a6D, CDKConstants.BONDORDER_DOUBLE));
        
        InChIGenerator genD = getFactory().getInChIGenerator(acD);
        Assert.assertEquals(genD.getReturnStatus(), INCHI_RET.OKAY);
        Assert.assertEquals(genD.getInchi(), "InChI=1/C3H7NO2/c1-2(4)3(5)6/h2H,4H2,1H3,(H,5,6)/t2-/m1/s1");
    }
    
}
