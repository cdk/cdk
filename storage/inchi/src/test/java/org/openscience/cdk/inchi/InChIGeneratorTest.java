/* Copyright (C) 2006-2007  Sam Adams <sea36@users.sf.net>
 *               2010,2012  Egon Willighagen <egonw@users.sf.net>
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.SingleElectron;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.stereo.DoubleBondStereochemistry;
import org.openscience.cdk.stereo.ExtendedTetrahedral;
import org.openscience.cdk.stereo.TetrahedralChirality;

import net.sf.jniinchi.INCHI_RET;
import org.junit.Assert;
import org.junit.Test;

/**
 * TestCase for the InChIGenerator.
 *
 * @cdk.module test-inchi
 *
 * @see org.openscience.cdk.inchi.InChIGenerator
 */
public class InChIGeneratorTest extends CDKTestCase {

    protected static InChIGeneratorFactory factory;

    protected InChIGeneratorFactory getFactory() throws Exception {
        if (factory == null) {
            factory = InChIGeneratorFactory.getInstance();
        }
        return (factory);
    }

    /**
     * Tests element name is correctly passed to InChI.
     *
     * @throws Exception
     */
    @Test
    public void testGetInchiFromChlorineAtom() throws Exception {
        IAtomContainer ac = new AtomContainer();
        ac.addAtom(new Atom("Cl"));
        InChIGenerator gen = getFactory().getInChIGenerator(ac, "FixedH");
        Assert.assertEquals(gen.getReturnStatus(), INCHI_RET.OKAY);
        Assert.assertEquals("InChI=1/ClH/h1H", gen.getInchi());
    }

    @Test
    public void testGetLog() throws Exception {
        IAtomContainer ac = new AtomContainer();
        ac.addAtom(new Atom("Cl"));
        InChIGenerator gen = getFactory().getInChIGenerator(ac, "FixedH");
        Assert.assertNotNull(gen.getLog());
    }

    @Test
    public void testGetAuxInfo() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a1 = new Atom("C");
        IAtom a2 = new Atom("C");
        a1.setImplicitHydrogenCount(3);
        a2.setImplicitHydrogenCount(3);
        ac.addAtom(a1);
        ac.addAtom(a2);
        ac.addBond(new Bond(a1, a2, CDKConstants.BONDORDER_SINGLE));
        InChIGenerator gen = getFactory().getInChIGenerator(ac);
        Assert.assertNotNull(gen.getAuxInfo());
        Assert.assertTrue(gen.getAuxInfo().startsWith("AuxInfo="));
    }

    @Test
    public void testGetMessage() throws Exception {
        IAtomContainer ac = new AtomContainer();
        ac.addAtom(new Atom("Cl"));
        InChIGenerator gen = getFactory().getInChIGenerator(ac, "FixedH");
        Assert.assertNull("Because this generation should work, I expected a null message String.", gen.getMessage());
    }

    @Test
    public void testGetWarningMessage() throws Exception {
        IAtomContainer ac = new AtomContainer();
        ac.addAtom(new Atom("Cl"));
        ac.addAtom(new Atom("H"));
        ac.addBond(0, 1, Order.TRIPLE);
        InChIGenerator gen = getFactory().getInChIGenerator(ac);
        Assert.assertNotNull(gen.getMessage());
        Assert.assertTrue(gen.getMessage().contains("Accepted unusual valence"));
    }

    /**
     * Tests charge is correctly passed to InChI.
     *
     * @throws Exception
     */
    @Test
    public void testGetInchiFromLithiumIon() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a = new Atom("Li");
        a.setFormalCharge(+1);
        ac.addAtom(a);
        InChIGenerator gen = getFactory().getInChIGenerator(ac, "FixedH");
        Assert.assertEquals(gen.getReturnStatus(), INCHI_RET.OKAY);
        Assert.assertEquals("InChI=1/Li/q+1", gen.getInchi());
    }

    /**
    * Tests isotopic mass is correctly passed to InChI.
    *
    * @throws Exception
    */
    @Test
    public void testGetInchiFromChlorine37Atom() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a = new Atom("Cl");
        a.setMassNumber(37);
        ac.addAtom(a);
        InChIGenerator gen = getFactory().getInChIGenerator(ac, "FixedH");
        Assert.assertEquals(gen.getReturnStatus(), INCHI_RET.OKAY);
        Assert.assertEquals("InChI=1/ClH/h1H/i1+2", gen.getInchi());
    }

    /**
     * Tests implicit hydrogen count is correctly passed to InChI.
     *
     * @throws Exception
     */
    @Test
    public void testGetInchiFromHydrogenChlorideImplicitH() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a = new Atom("Cl");
        a.setImplicitHydrogenCount(1);
        ac.addAtom(a);
        InChIGenerator gen = getFactory().getInChIGenerator(ac, "FixedH");
        Assert.assertEquals(gen.getReturnStatus(), INCHI_RET.OKAY);
        Assert.assertEquals("InChI=1/ClH/h1H", gen.getInchi());
    }

    /**
     * Tests radical state is correctly passed to InChI.
     *
     * @throws Exception
     */
    @Test
    public void testGetInchiFromMethylRadical() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a = new Atom("C");
        a.setImplicitHydrogenCount(3);
        ac.addAtom(a);
        ac.addSingleElectron(new SingleElectron(a));
        InChIGenerator gen = getFactory().getInChIGenerator(ac, "FixedH");
        Assert.assertEquals(gen.getReturnStatus(), INCHI_RET.OKAY);
        Assert.assertEquals("InChI=1/CH3/h1H3", gen.getInchi());
    }

    /**
     * Tests single bond is correctly passed to InChI.
     *
     * @throws Exception
     */
    @Test
    public void testGetInchiFromEthane() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a1 = new Atom("C");
        IAtom a2 = new Atom("C");
        a1.setImplicitHydrogenCount(3);
        a2.setImplicitHydrogenCount(3);
        ac.addAtom(a1);
        ac.addAtom(a2);
        ac.addBond(new Bond(a1, a2, CDKConstants.BONDORDER_SINGLE));
        InChIGenerator gen = getFactory().getInChIGenerator(ac, "FixedH");
        Assert.assertEquals(gen.getReturnStatus(), INCHI_RET.OKAY);
        Assert.assertEquals("InChI=1/C2H6/c1-2/h1-2H3", gen.getInchi());
        Assert.assertEquals("OTMSDBZUPAUEDD-UHFFFAOYNA-N", gen.getInchiKey());
    }

    /**
     * Tests double bond is correctly passed to InChI.
     *
     * @throws Exception
     */
    @Test
    public void testGetInchiFromEthene() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a1 = new Atom("C");
        IAtom a2 = new Atom("C");
        a1.setImplicitHydrogenCount(2);
        a2.setImplicitHydrogenCount(2);
        ac.addAtom(a1);
        ac.addAtom(a2);
        ac.addBond(new Bond(a1, a2, CDKConstants.BONDORDER_DOUBLE));
        InChIGenerator gen = getFactory().getInChIGenerator(ac, "FixedH");
        Assert.assertEquals(gen.getReturnStatus(), INCHI_RET.OKAY);
        Assert.assertEquals("InChI=1/C2H4/c1-2/h1-2H2", gen.getInchi());
    }

    /**
     * Tests triple bond is correctly passed to InChI.
     *
     * @throws Exception
     */
    @Test
    public void testGetInchiFromEthyne() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a1 = new Atom("C");
        IAtom a2 = new Atom("C");
        a1.setImplicitHydrogenCount(1);
        a2.setImplicitHydrogenCount(1);
        ac.addAtom(a1);
        ac.addAtom(a2);
        ac.addBond(new Bond(a1, a2, CDKConstants.BONDORDER_TRIPLE));
        InChIGenerator gen = getFactory().getInChIGenerator(ac, "FixedH");
        Assert.assertEquals(gen.getReturnStatus(), INCHI_RET.OKAY);
        Assert.assertEquals("InChI=1/C2H2/c1-2/h1-2H", gen.getInchi());
    }

    /**
     * Tests 2D coordinates are correctly passed to InChI.
     *
     * @throws Exception
     */
    @Test
    public void testGetInchiEandZ12Dichloroethene2D() throws Exception {

        // (E)-1,2-dichloroethene
        IAtomContainer acE = new AtomContainer();
        IAtom a1E = new Atom("C", new Point2d(2.866, -0.250));
        IAtom a2E = new Atom("C", new Point2d(3.732, 0.250));
        IAtom a3E = new Atom("Cl", new Point2d(2.000, 2.500));
        IAtom a4E = new Atom("Cl", new Point2d(4.598, -0.250));
        a1E.setImplicitHydrogenCount(1);
        a2E.setImplicitHydrogenCount(1);
        acE.addAtom(a1E);
        acE.addAtom(a2E);
        acE.addAtom(a3E);
        acE.addAtom(a4E);

        acE.addBond(new Bond(a1E, a2E, CDKConstants.BONDORDER_DOUBLE));
        acE.addBond(new Bond(a1E, a2E, CDKConstants.BONDORDER_DOUBLE));
        acE.addBond(new Bond(a1E, a3E, CDKConstants.BONDORDER_SINGLE));
        acE.addBond(new Bond(a2E, a4E, CDKConstants.BONDORDER_SINGLE));

        InChIGenerator genE = getFactory().getInChIGenerator(acE, "FixedH");
        Assert.assertEquals(genE.getReturnStatus(), INCHI_RET.OKAY);
        Assert.assertEquals("InChI=1/C2H2Cl2/c3-1-2-4/h1-2H/b2-1+", genE.getInchi());

        // (Z)-1,2-dichloroethene
        IAtomContainer acZ = new AtomContainer();
        IAtom a1Z = new Atom("C", new Point2d(2.866, -0.440));
        IAtom a2Z = new Atom("C", new Point2d(3.732, 0.060));
        IAtom a3Z = new Atom("Cl", new Point2d(2.000, 0.060));
        IAtom a4Z = new Atom("Cl", new Point2d(3.732, 1.060));
        a1Z.setImplicitHydrogenCount(1);
        a2Z.setImplicitHydrogenCount(1);
        acZ.addAtom(a1Z);
        acZ.addAtom(a2Z);
        acZ.addAtom(a3Z);
        acZ.addAtom(a4Z);

        acZ.addBond(new Bond(a1Z, a2Z, CDKConstants.BONDORDER_DOUBLE));
        acZ.addBond(new Bond(a1Z, a2Z, CDKConstants.BONDORDER_DOUBLE));
        acZ.addBond(new Bond(a1Z, a3Z, CDKConstants.BONDORDER_SINGLE));
        acZ.addBond(new Bond(a2Z, a4Z, CDKConstants.BONDORDER_SINGLE));

        InChIGenerator genZ = getFactory().getInChIGenerator(acZ, "FixedH");
        Assert.assertEquals(genZ.getReturnStatus(), INCHI_RET.OKAY);
        Assert.assertEquals("InChI=1/C2H2Cl2/c3-1-2-4/h1-2H/b2-1-", genZ.getInchi());
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
        a1L.setImplicitHydrogenCount(1);
        a3L.setImplicitHydrogenCount(2);
        a4L.setImplicitHydrogenCount(3);
        a5L.setImplicitHydrogenCount(1);
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

        InChIGenerator genL = getFactory().getInChIGenerator(acL, "FixedH");
        Assert.assertEquals(genL.getReturnStatus(), INCHI_RET.OKAY);
        Assert.assertEquals("InChI=1/C3H7NO2/c1-2(4)3(5)6/h2H,4H2,1H3,(H,5,6)/t2-/m0/s1/f/h5H", genL.getInchi());

        // D-Alanine
        IAtomContainer acD = new AtomContainer();
        IAtom a1D = new Atom("C", new Point3d(0.358, 0.819, 20.655));
        IAtom a2D = new Atom("C", new Point3d(1.598, -0.032, 20.905));
        IAtom a3D = new Atom("N", new Point3d(0.275, 2.014, 21.574));
        IAtom a4D = new Atom("C", new Point3d(-0.952, 0.043, 20.838));
        IAtom a5D = new Atom("O", new Point3d(2.678, 0.479, 21.093));
        IAtom a6D = new Atom("O", new Point3d(1.596, -1.239, 20.958));
        a1D.setImplicitHydrogenCount(1);
        a3D.setImplicitHydrogenCount(2);
        a4D.setImplicitHydrogenCount(3);
        a5D.setImplicitHydrogenCount(1);
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

        InChIGenerator genD = getFactory().getInChIGenerator(acD, "FixedH");
        Assert.assertEquals(genD.getReturnStatus(), INCHI_RET.OKAY);
        Assert.assertEquals("InChI=1/C3H7NO2/c1-2(4)3(5)6/h2H,4H2,1H3,(H,5,6)/t2-/m1/s1/f/h5H", genD.getInchi());
    }

    // ensure only
    @Test
    public void zeroHydrogenCount() throws Exception {
        IAtomContainer ac = new AtomContainer();
        ac.addAtom(new Atom("O"));
        ac.getAtom(0).setImplicitHydrogenCount(0);
        InChIGenerator gen = getFactory().getInChIGenerator(ac);
        Assert.assertEquals(INCHI_RET.OKAY, gen.getReturnStatus());
        Assert.assertEquals("InChI=1S/O", gen.getInchi());
    }

    /**
     * Tests element name is correctly passed to InChI.
     *
     * @throws Exception
     */
    @Test
    public void testGetStandardInchiFromChlorineAtom() throws Exception {
        IAtomContainer ac = new AtomContainer();
        ac.addAtom(new Atom("Cl"));
        InChIGenerator gen = getFactory().getInChIGenerator(ac);
        Assert.assertEquals(INCHI_RET.OKAY, gen.getReturnStatus());
        Assert.assertEquals("InChI=1S/ClH/h1H", gen.getInchi());
    }

    /**
     * Tests charge is correctly passed to InChI.
     *
     * @throws Exception
     */
    @Test
    public void testGetStandardInchiFromLithiumIon() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a = new Atom("Li");
        a.setFormalCharge(+1);
        ac.addAtom(a);
        InChIGenerator gen = getFactory().getInChIGenerator(ac);
        Assert.assertEquals(INCHI_RET.OKAY, gen.getReturnStatus());
        Assert.assertEquals("InChI=1S/Li/q+1", gen.getInchi());
    }

    /**
    * Tests isotopic mass is correctly passed to InChI.
    *
    * @throws Exception
    */
    @Test
    public void testGetStandardInchiFromChlorine37Atom() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a = new Atom("Cl");
        a.setMassNumber(37);
        ac.addAtom(a);
        InChIGenerator gen = getFactory().getInChIGenerator(ac);
        Assert.assertEquals(INCHI_RET.OKAY, gen.getReturnStatus());
        Assert.assertEquals("InChI=1S/ClH/h1H/i1+2", gen.getInchi());
    }

    /**
     * Tests implicit hydrogen count is correctly passed to InChI.
     *
     * @throws Exception
     */
    @Test
    public void testGetStandardInchiFromHydrogenChlorideImplicitH() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a = new Atom("Cl");
        a.setImplicitHydrogenCount(1);
        ac.addAtom(a);
        InChIGenerator gen = getFactory().getInChIGenerator(ac);
        Assert.assertEquals(gen.getReturnStatus(), INCHI_RET.OKAY);
        Assert.assertEquals("InChI=1S/ClH/h1H", gen.getInchi());
    }

    /**
     * Tests radical state is correctly passed to InChI.
     *
     * @throws Exception
     */
    @Test
    public void testGetStandardInchiFromMethylRadical() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a = new Atom("C");
        a.setImplicitHydrogenCount(3);
        ac.addAtom(a);
        ac.addSingleElectron(new SingleElectron(a));
        InChIGenerator gen = getFactory().getInChIGenerator(ac);
        Assert.assertEquals(INCHI_RET.OKAY, gen.getReturnStatus());
        Assert.assertEquals("InChI=1S/CH3/h1H3", gen.getInchi());
    }

    /**
     * Tests single bond is correctly passed to InChI.
     *
     * @throws Exception
     */
    @Test
    public void testGetStandardInchiFromEthane() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a1 = new Atom("C");
        IAtom a2 = new Atom("C");
        a1.setImplicitHydrogenCount(3);
        a2.setImplicitHydrogenCount(3);
        ac.addAtom(a1);
        ac.addAtom(a2);
        ac.addBond(new Bond(a1, a2, CDKConstants.BONDORDER_SINGLE));
        InChIGenerator gen = getFactory().getInChIGenerator(ac);
        Assert.assertEquals(INCHI_RET.OKAY, gen.getReturnStatus());
        Assert.assertEquals("InChI=1S/C2H6/c1-2/h1-2H3", gen.getInchi());
        Assert.assertEquals("OTMSDBZUPAUEDD-UHFFFAOYSA-N", gen.getInchiKey());
    }

    /**
     * Tests double bond is correctly passed to InChI.
     *
     * @throws Exception
     */
    @Test
    public void testGetStandardInchiFromEthene() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a1 = new Atom("C");
        IAtom a2 = new Atom("C");
        a1.setImplicitHydrogenCount(2);
        a2.setImplicitHydrogenCount(2);
        ac.addAtom(a1);
        ac.addAtom(a2);
        ac.addBond(new Bond(a1, a2, CDKConstants.BONDORDER_DOUBLE));
        InChIGenerator gen = getFactory().getInChIGenerator(ac);
        Assert.assertEquals(INCHI_RET.OKAY, gen.getReturnStatus());
        Assert.assertEquals("InChI=1S/C2H4/c1-2/h1-2H2", gen.getInchi());
    }

    /**
     * Tests triple bond is correctly passed to InChI.
     *
     * @throws Exception
     */
    @Test
    public void testGetStandardInchiFromEthyne() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a1 = new Atom("C");
        IAtom a2 = new Atom("C");
        a1.setImplicitHydrogenCount(1);
        a2.setImplicitHydrogenCount(1);
        ac.addAtom(a1);
        ac.addAtom(a2);
        ac.addBond(new Bond(a1, a2, CDKConstants.BONDORDER_TRIPLE));
        InChIGenerator gen = getFactory().getInChIGenerator(ac);
        Assert.assertEquals(INCHI_RET.OKAY, gen.getReturnStatus());
        Assert.assertEquals("InChI=1S/C2H2/c1-2/h1-2H", gen.getInchi());
    }

    /**
     * Tests 2D coordinates are correctly passed to InChI.
     *
     * @throws Exception
     */
    @Test
    public void testGetStandardInchiEandZ12Dichloroethene2D() throws Exception {

        // (E)-1,2-dichloroethene
        IAtomContainer acE = new AtomContainer();
        IAtom a1E = new Atom("C", new Point2d(2.866, -0.250));
        IAtom a2E = new Atom("C", new Point2d(3.732, 0.250));
        IAtom a3E = new Atom("Cl", new Point2d(2.000, 2.500));
        IAtom a4E = new Atom("Cl", new Point2d(4.598, -0.250));
        a1E.setImplicitHydrogenCount(1);
        a2E.setImplicitHydrogenCount(1);
        acE.addAtom(a1E);
        acE.addAtom(a2E);
        acE.addAtom(a3E);
        acE.addAtom(a4E);

        acE.addBond(new Bond(a1E, a2E, CDKConstants.BONDORDER_DOUBLE));
        acE.addBond(new Bond(a1E, a2E, CDKConstants.BONDORDER_DOUBLE));
        acE.addBond(new Bond(a1E, a3E, CDKConstants.BONDORDER_SINGLE));
        acE.addBond(new Bond(a2E, a4E, CDKConstants.BONDORDER_SINGLE));

        InChIGenerator genE = getFactory().getInChIGenerator(acE);
        Assert.assertEquals(INCHI_RET.OKAY, genE.getReturnStatus());
        Assert.assertEquals("InChI=1S/C2H2Cl2/c3-1-2-4/h1-2H/b2-1+", genE.getInchi());

        // (Z)-1,2-dichloroethene
        IAtomContainer acZ = new AtomContainer();
        IAtom a1Z = new Atom("C", new Point2d(2.866, -0.440));
        IAtom a2Z = new Atom("C", new Point2d(3.732, 0.060));
        IAtom a3Z = new Atom("Cl", new Point2d(2.000, 0.060));
        IAtom a4Z = new Atom("Cl", new Point2d(3.732, 1.060));
        a1Z.setImplicitHydrogenCount(1);
        a2Z.setImplicitHydrogenCount(1);
        acZ.addAtom(a1Z);
        acZ.addAtom(a2Z);
        acZ.addAtom(a3Z);
        acZ.addAtom(a4Z);

        acZ.addBond(new Bond(a1Z, a2Z, CDKConstants.BONDORDER_DOUBLE));
        acZ.addBond(new Bond(a1Z, a2Z, CDKConstants.BONDORDER_DOUBLE));
        acZ.addBond(new Bond(a1Z, a3Z, CDKConstants.BONDORDER_SINGLE));
        acZ.addBond(new Bond(a2Z, a4Z, CDKConstants.BONDORDER_SINGLE));

        InChIGenerator genZ = getFactory().getInChIGenerator(acZ);
        Assert.assertEquals(INCHI_RET.OKAY, genZ.getReturnStatus());
        Assert.assertEquals("InChI=1S/C2H2Cl2/c3-1-2-4/h1-2H/b2-1-", genZ.getInchi());
    }

    /**
     * Tests 3D coordinates are correctly passed to InChI.
     *
     * @throws Exception
     */
    @Test
    public void testGetStandardInchiFromLandDAlanine3D() throws Exception {

        // L-Alanine
        IAtomContainer acL = new AtomContainer();
        IAtom a1L = new Atom("C", new Point3d(-0.358, 0.819, 20.655));
        IAtom a2L = new Atom("C", new Point3d(-1.598, -0.032, 20.905));
        IAtom a3L = new Atom("N", new Point3d(-0.275, 2.014, 21.574));
        IAtom a4L = new Atom("C", new Point3d(0.952, 0.043, 20.838));
        IAtom a5L = new Atom("O", new Point3d(-2.678, 0.479, 21.093));
        IAtom a6L = new Atom("O", new Point3d(-1.596, -1.239, 20.958));
        a1L.setImplicitHydrogenCount(1);
        a3L.setImplicitHydrogenCount(2);
        a4L.setImplicitHydrogenCount(3);
        a5L.setImplicitHydrogenCount(1);
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
        Assert.assertEquals(INCHI_RET.OKAY, genL.getReturnStatus());
        Assert.assertEquals("InChI=1S/C3H7NO2/c1-2(4)3(5)6/h2H,4H2,1H3,(H,5,6)/t2-/m0/s1", genL.getInchi());

        // D-Alanine
        IAtomContainer acD = new AtomContainer();
        IAtom a1D = new Atom("C", new Point3d(0.358, 0.819, 20.655));
        IAtom a2D = new Atom("C", new Point3d(1.598, -0.032, 20.905));
        IAtom a3D = new Atom("N", new Point3d(0.275, 2.014, 21.574));
        IAtom a4D = new Atom("C", new Point3d(-0.952, 0.043, 20.838));
        IAtom a5D = new Atom("O", new Point3d(2.678, 0.479, 21.093));
        IAtom a6D = new Atom("O", new Point3d(1.596, -1.239, 20.958));
        a1D.setImplicitHydrogenCount(1);
        a3D.setImplicitHydrogenCount(2);
        a4D.setImplicitHydrogenCount(3);
        a5D.setImplicitHydrogenCount(1);
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
        Assert.assertEquals(INCHI_RET.OKAY, genD.getReturnStatus());
        Assert.assertEquals("InChI=1S/C3H7NO2/c1-2(4)3(5)6/h2H,4H2,1H3,(H,5,6)/t2-/m1/s1", genD.getInchi());
    }

    @Test
    public void testTetrahedralStereo() throws Exception {
        // L-Alanine
        IAtomContainer acL = new AtomContainer();
        IAtom[] ligandAtoms = new IAtom[4];
        IAtom a1 = new Atom("C");
        IAtom a1H = new Atom("H");
        ligandAtoms[0] = a1H;
        IAtom a2 = new Atom("C");
        ligandAtoms[1] = a2;
        IAtom a3 = new Atom("N");
        ligandAtoms[2] = a3;
        IAtom a4 = new Atom("C");
        ligandAtoms[3] = a4;
        IAtom a5 = new Atom("O");
        IAtom a6 = new Atom("O");
        a1.setImplicitHydrogenCount(0);
        a3.setImplicitHydrogenCount(2);
        a4.setImplicitHydrogenCount(3);
        a5.setImplicitHydrogenCount(1);
        acL.addAtom(a1);
        acL.addAtom(a1H);
        acL.addAtom(a2);
        acL.addAtom(a3);
        acL.addAtom(a4);
        acL.addAtom(a5);
        acL.addAtom(a6);

        acL.addBond(new Bond(a1, a1H, CDKConstants.BONDORDER_SINGLE));
        acL.addBond(new Bond(a1, a2, CDKConstants.BONDORDER_SINGLE));
        acL.addBond(new Bond(a1, a3, CDKConstants.BONDORDER_SINGLE));
        acL.addBond(new Bond(a1, a4, CDKConstants.BONDORDER_SINGLE));
        acL.addBond(new Bond(a2, a5, CDKConstants.BONDORDER_SINGLE));
        acL.addBond(new Bond(a2, a6, CDKConstants.BONDORDER_DOUBLE));

        ITetrahedralChirality chirality = new TetrahedralChirality(a1, ligandAtoms, Stereo.ANTI_CLOCKWISE);
        acL.addStereoElement(chirality);

        InChIGenerator genL = getFactory().getInChIGenerator(acL);
        Assert.assertEquals(INCHI_RET.OKAY, genL.getReturnStatus());
        Assert.assertEquals("InChI=1S/C3H7NO2/c1-2(4)3(5)6/h2H,4H2,1H3,(H,5,6)/t2-/m0/s1", genL.getInchi());
    }

    @Test
    public void testDoubleBondStereochemistry() throws Exception {
        // (E)-1,2-dichloroethene
        IAtomContainer acE = new AtomContainer();
        IAtom a1E = new Atom("C");
        IAtom a2E = new Atom("C");
        IAtom a3E = new Atom("Cl");
        IAtom a4E = new Atom("Cl");
        a1E.setImplicitHydrogenCount(1);
        a2E.setImplicitHydrogenCount(1);
        acE.addAtom(a1E);
        acE.addAtom(a2E);
        acE.addAtom(a3E);
        acE.addAtom(a4E);

        acE.addBond(new Bond(a1E, a2E, CDKConstants.BONDORDER_DOUBLE));
        acE.addBond(new Bond(a1E, a3E, CDKConstants.BONDORDER_SINGLE));
        acE.addBond(new Bond(a2E, a4E, CDKConstants.BONDORDER_SINGLE));

        IBond[] ligands = new IBond[2];
        ligands[0] = acE.getBond(1);
        ligands[1] = acE.getBond(2);
        IDoubleBondStereochemistry stereo = new DoubleBondStereochemistry(acE.getBond(0), ligands,
                org.openscience.cdk.interfaces.IDoubleBondStereochemistry.Conformation.OPPOSITE);
        acE.addStereoElement(stereo);

        InChIGenerator genE = getFactory().getInChIGenerator(acE);
        Assert.assertEquals(INCHI_RET.OKAY, genE.getReturnStatus());
        System.out.println(genE.getMessage());
        Assert.assertEquals("InChI=1S/C2H2Cl2/c3-1-2-4/h1-2H/b2-1+", genE.getInchi());
    }

    /**
     * @cdk.bug 1295
     */
    @Test
    public void bug1295() throws Exception {
        MDLV2000Reader reader = new MDLV2000Reader(getClass().getResourceAsStream("/data/mdl/bug1295.mol"));
        try {
            IAtomContainer container = reader.read(new AtomContainer());
            InChIGenerator generator = getFactory().getInChIGenerator(container);
            Assert.assertEquals("InChI=1S/C7H15NO/c1-4-7(3)6-8-9-5-2/h6-7H,4-5H2,1-3H3", generator.getInchi());
        } finally {
            reader.close();
        }
    }

    @Test
    public void r_penta_2_3_diene_impl_h() throws Exception {
        IAtomContainer m = new AtomContainer(5, 4, 0, 0);
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.DOUBLE);
        m.addBond(2, 3, IBond.Order.DOUBLE);
        m.addBond(3, 4, IBond.Order.SINGLE);

        int[][] atoms = new int[][]{{0, 1, 3, 4}, {1, 0, 3, 4}, {1, 0, 4, 3}, {0, 1, 4, 3}, {4, 3, 1, 0}, {4, 3, 0, 1},
                {3, 4, 0, 1}, {3, 4, 1, 0},};
        Stereo[] stereos = new Stereo[]{Stereo.ANTI_CLOCKWISE, Stereo.CLOCKWISE, Stereo.ANTI_CLOCKWISE,
                Stereo.CLOCKWISE, Stereo.ANTI_CLOCKWISE, Stereo.CLOCKWISE, Stereo.ANTI_CLOCKWISE, Stereo.CLOCKWISE};

        for (int i = 0; i < atoms.length; i++) {
            IStereoElement element = new ExtendedTetrahedral(m.getAtom(2), new IAtom[]{m.getAtom(atoms[i][0]),
                    m.getAtom(atoms[i][1]), m.getAtom(atoms[i][2]), m.getAtom(atoms[i][3])}, stereos[i]);
            m.setStereoElements(Collections.singletonList(element));

            InChIGenerator generator = getFactory().getInChIGenerator(m);
            assertThat(generator.getInchi(), is("InChI=1S/C5H8/c1-3-5-4-2/h3-4H,1-2H3/t5-/m0/s1"));

        }
    }

    @Test
    public void s_penta_2_3_diene_impl_h() throws Exception {
        IAtomContainer m = new AtomContainer(5, 4, 0, 0);
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.DOUBLE);
        m.addBond(2, 3, IBond.Order.DOUBLE);
        m.addBond(3, 4, IBond.Order.SINGLE);

        int[][] atoms = new int[][]{{0, 1, 3, 4}, {1, 0, 3, 4}, {1, 0, 4, 3}, {0, 1, 4, 3}, {4, 3, 1, 0}, {4, 3, 0, 1},
                {3, 4, 0, 1}, {3, 4, 1, 0},};
        Stereo[] stereos = new Stereo[]{Stereo.CLOCKWISE, Stereo.ANTI_CLOCKWISE, Stereo.CLOCKWISE,
                Stereo.ANTI_CLOCKWISE, Stereo.CLOCKWISE, Stereo.ANTI_CLOCKWISE, Stereo.CLOCKWISE, Stereo.ANTI_CLOCKWISE};

        for (int i = 0; i < atoms.length; i++) {

            IStereoElement element = new ExtendedTetrahedral(m.getAtom(2), new IAtom[]{m.getAtom(atoms[i][0]),
                    m.getAtom(atoms[i][1]), m.getAtom(atoms[i][2]), m.getAtom(atoms[i][3])}, stereos[i]);
            m.setStereoElements(Collections.singletonList(element));

            InChIGenerator generator = getFactory().getInChIGenerator(m);
            assertThat(generator.getInchi(), is("InChI=1S/C5H8/c1-3-5-4-2/h3-4H,1-2H3/t5-/m1/s1"));

        }
    }

    @Test
    public void r_penta_2_3_diene_expl_h() throws Exception {
        IAtomContainer m = new AtomContainer(5, 4, 0, 0);
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("H"));
        m.addAtom(new Atom("H"));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.DOUBLE);
        m.addBond(2, 3, IBond.Order.DOUBLE);
        m.addBond(3, 4, IBond.Order.SINGLE);
        m.addBond(1, 5, IBond.Order.SINGLE);
        m.addBond(3, 6, IBond.Order.SINGLE);

        int[][] atoms = new int[][]{{0, 5, 6, 4}, {5, 0, 6, 4}, {5, 0, 4, 6}, {0, 5, 4, 6}, {4, 6, 5, 0}, {4, 6, 0, 5},
                {6, 4, 0, 5}, {6, 4, 5, 0},};
        Stereo[] stereos = new Stereo[]{Stereo.ANTI_CLOCKWISE, Stereo.CLOCKWISE, Stereo.ANTI_CLOCKWISE,
                Stereo.CLOCKWISE, Stereo.ANTI_CLOCKWISE, Stereo.CLOCKWISE, Stereo.ANTI_CLOCKWISE, Stereo.CLOCKWISE};

        for (int i = 0; i < atoms.length; i++) {

            IStereoElement element = new ExtendedTetrahedral(m.getAtom(2), new IAtom[]{m.getAtom(atoms[i][0]),
                    m.getAtom(atoms[i][1]), m.getAtom(atoms[i][2]), m.getAtom(atoms[i][3])}, stereos[i]);
            m.setStereoElements(Collections.singletonList(element));

            InChIGenerator generator = getFactory().getInChIGenerator(m);
            assertThat(generator.getInchi(), is("InChI=1S/C5H8/c1-3-5-4-2/h3-4H,1-2H3/t5-/m0/s1"));

        }
    }

    @Test
    public void s_penta_2_3_diene_expl_h() throws Exception {
        IAtomContainer m = new AtomContainer(5, 4, 0, 0);
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("H"));
        m.addAtom(new Atom("H"));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.DOUBLE);
        m.addBond(2, 3, IBond.Order.DOUBLE);
        m.addBond(3, 4, IBond.Order.SINGLE);
        m.addBond(1, 5, IBond.Order.SINGLE);
        m.addBond(3, 6, IBond.Order.SINGLE);

        int[][] atoms = new int[][]{{0, 5, 6, 4}, {5, 0, 6, 4}, {5, 0, 4, 6}, {0, 5, 4, 6}, {4, 6, 5, 0}, {4, 6, 0, 5},
                {6, 4, 0, 5}, {6, 4, 5, 0},};
        Stereo[] stereos = new Stereo[]{Stereo.CLOCKWISE, Stereo.ANTI_CLOCKWISE, Stereo.CLOCKWISE,
                Stereo.ANTI_CLOCKWISE, Stereo.CLOCKWISE, Stereo.ANTI_CLOCKWISE, Stereo.CLOCKWISE, Stereo.ANTI_CLOCKWISE};

        for (int i = 0; i < atoms.length; i++) {

            IStereoElement element = new ExtendedTetrahedral(m.getAtom(2), new IAtom[]{m.getAtom(atoms[i][0]),
                    m.getAtom(atoms[i][1]), m.getAtom(atoms[i][2]), m.getAtom(atoms[i][3])}, stereos[i]);
            m.setStereoElements(Collections.singletonList(element));

            InChIGenerator generator = getFactory().getInChIGenerator(m);
            assertThat(generator.getInchi(), is("InChI=1S/C5H8/c1-3-5-4-2/h3-4H,1-2H3/t5-/m1/s1"));

        }
    }
}
