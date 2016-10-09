/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.limitations.tools;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.tools.SaturationChecker;

/**
 * This class shows some limitations of algorithms in the SaturationChecker
 * class.
 *
 * @cdk.module test-extra
 *
 * @author     egonw
 * @cdk.created    2003-11-02
 *
 * @see org.openscience.cdk.tools.SaturationChecker
 */
@Ignore("Limitations actually cause failing tests")
public class SaturationCheckerTest extends CDKTestCase {

    private static SaturationChecker satcheck = null;

    @BeforeClass
    public static void setup() throws Exception {
        satcheck = new SaturationChecker();
    }

    /**
     * Tests the method saturate().
     */
    @Test
    public void testSaturate_WithNitrate() throws Exception {
        IAtomContainer mol = new AtomContainer();
        Atom a1 = new Atom("O");
        mol.addAtom(a1);
        Atom a2 = new Atom("N");
        mol.addAtom(a2);
        Atom a3 = new Atom("O");
        mol.addAtom(a3);
        Atom a4 = new Atom("C");
        mol.addAtom(a4);
        Atom a5 = new Atom("C");
        mol.addAtom(a5);
        Atom a6 = new Atom("C");
        mol.addAtom(a6);
        Atom a7 = new Atom("H");
        mol.addAtom(a7);
        Atom a8 = new Atom("C");
        mol.addAtom(a8);
        Atom a9 = new Atom("C");
        mol.addAtom(a9);
        Atom a10 = new Atom("H");
        mol.addAtom(a10);
        Atom a11 = new Atom("H");
        mol.addAtom(a11);
        Atom a12 = new Atom("C");
        mol.addAtom(a12);
        Atom a13 = new Atom("H");
        mol.addAtom(a13);
        Atom a14 = new Atom("N");
        mol.addAtom(a14);
        Atom a15 = new Atom("H");
        mol.addAtom(a15);
        Atom a16 = new Atom("H");
        mol.addAtom(a16);
        Bond b1 = new Bond(a2, a1, IBond.Order.SINGLE);
        mol.addBond(b1);
        Bond b2 = new Bond(a3, a2, IBond.Order.SINGLE);
        mol.addBond(b2);
        Bond b3 = new Bond(a2, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        Bond b4 = new Bond(a5, a4, IBond.Order.SINGLE);
        mol.addBond(b4);
        Bond b5 = new Bond(a4, a6, IBond.Order.SINGLE);
        mol.addBond(b5);
        Bond b6 = new Bond(a7, a5, IBond.Order.SINGLE);
        mol.addBond(b6);
        Bond b7 = new Bond(a8, a5, IBond.Order.SINGLE);
        mol.addBond(b7);
        Bond b8 = new Bond(a6, a9, IBond.Order.SINGLE);
        mol.addBond(b8);
        Bond b9 = new Bond(a6, a10, IBond.Order.SINGLE);
        mol.addBond(b9);
        Bond b10 = new Bond(a11, a8, IBond.Order.SINGLE);
        mol.addBond(b10);
        Bond b11 = new Bond(a8, a12, IBond.Order.SINGLE);
        mol.addBond(b11);
        Bond b12 = new Bond(a9, a13, IBond.Order.SINGLE);
        mol.addBond(b12);
        Bond b13 = new Bond(a12, a9, IBond.Order.SINGLE);
        mol.addBond(b13);
        Bond b14 = new Bond(a14, a12, IBond.Order.SINGLE);
        mol.addBond(b14);
        Bond b15 = new Bond(a15, a14, IBond.Order.SINGLE);
        mol.addBond(b15);
        Bond b16 = new Bond(a14, a16, IBond.Order.SINGLE);
        mol.addBond(b16);
        satcheck.saturate(mol);
        Assert.assertEquals(IBond.Order.DOUBLE, b1.getOrder());
        Assert.assertEquals(IBond.Order.DOUBLE, b2.getOrder());
    }

    /**
     * Tests the method saturate().
     */
    @Test
    public void testSaturation_S4AtomType() throws Exception {
        IAtomContainer mol = new AtomContainer();
        Atom a1 = new Atom("N");
        mol.addAtom(a1);
        Atom a2 = new Atom("H");
        mol.addAtom(a2);
        Atom a3 = new Atom("C");
        mol.addAtom(a3);
        Atom a4 = new Atom("S");
        mol.addAtom(a4);
        Atom a5 = new Atom("O");
        mol.addAtom(a5);
        Atom a6 = new Atom("C");
        mol.addAtom(a6);
        Atom a7 = new Atom("O");
        mol.addAtom(a7);
        Atom a8 = new Atom("O");
        mol.addAtom(a8);
        Atom a9 = new Atom("C");
        mol.addAtom(a9);
        Atom a10 = new Atom("H");
        mol.addAtom(a10);
        Atom a11 = new Atom("H");
        mol.addAtom(a11);
        Atom a12 = new Atom("H");
        mol.addAtom(a12);
        Atom a13 = new Atom("C");
        mol.addAtom(a13);
        Atom a14 = new Atom("C");
        mol.addAtom(a14);
        Atom a15 = new Atom("H");
        mol.addAtom(a15);
        Atom a16 = new Atom("C");
        mol.addAtom(a16);
        Atom a17 = new Atom("H");
        mol.addAtom(a17);
        Atom a18 = new Atom("C");
        mol.addAtom(a18);
        Atom a19 = new Atom("C");
        mol.addAtom(a19);
        Atom a20 = new Atom("H");
        mol.addAtom(a20);
        Atom a21 = new Atom("H");
        mol.addAtom(a21);
        Atom a22 = new Atom("N");
        mol.addAtom(a22);
        Atom a23 = new Atom("H");
        mol.addAtom(a23);
        Atom a24 = new Atom("H");
        mol.addAtom(a24);
        Bond b1 = new Bond(a2, a1, IBond.Order.SINGLE);
        mol.addBond(b1);
        Bond b2 = new Bond(a3, a1, IBond.Order.SINGLE);
        mol.addBond(b2);
        Bond b3 = new Bond(a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        Bond b4 = new Bond(a3, a5, IBond.Order.SINGLE);
        mol.addBond(b4);
        Bond b5 = new Bond(a6, a3, IBond.Order.SINGLE);
        mol.addBond(b5);
        Bond b6 = new Bond(a7, a4, IBond.Order.SINGLE);
        mol.addBond(b6);
        Bond b7 = new Bond(a8, a4, IBond.Order.SINGLE);
        mol.addBond(b7);
        Bond b8 = new Bond(a4, a9, IBond.Order.SINGLE);
        mol.addBond(b8);
        Bond b9 = new Bond(a10, a6, IBond.Order.SINGLE);
        mol.addBond(b9);
        Bond b10 = new Bond(a6, a11, IBond.Order.SINGLE);
        mol.addBond(b10);
        Bond b11 = new Bond(a6, a12, IBond.Order.SINGLE);
        mol.addBond(b11);
        Bond b12 = new Bond(a9, a13, IBond.Order.SINGLE);
        mol.addBond(b12);
        Bond b13 = new Bond(a9, a14, IBond.Order.SINGLE);
        mol.addBond(b13);
        Bond b14 = new Bond(a15, a13, IBond.Order.SINGLE);
        mol.addBond(b14);
        Bond b15 = new Bond(a13, a16, IBond.Order.SINGLE);
        mol.addBond(b15);
        Bond b16 = new Bond(a17, a14, IBond.Order.SINGLE);
        mol.addBond(b16);
        Bond b17 = new Bond(a14, a18, IBond.Order.SINGLE);
        mol.addBond(b17);
        Bond b18 = new Bond(a16, a19, IBond.Order.SINGLE);
        mol.addBond(b18);
        Bond b19 = new Bond(a16, a20, IBond.Order.SINGLE);
        mol.addBond(b19);
        Bond b20 = new Bond(a18, a19, IBond.Order.SINGLE);
        mol.addBond(b20);
        Bond b21 = new Bond(a18, a21, IBond.Order.SINGLE);
        mol.addBond(b21);
        Bond b22 = new Bond(a19, a22, IBond.Order.SINGLE);
        mol.addBond(b22);
        Bond b23 = new Bond(a22, a23, IBond.Order.SINGLE);
        mol.addBond(b23);
        Bond b24 = new Bond(a22, a24, IBond.Order.SINGLE);
        mol.addBond(b24);
        satcheck.saturate(mol);
        Assert.assertEquals(IBond.Order.DOUBLE, b6.getOrder());
        Assert.assertEquals(IBond.Order.DOUBLE, b7.getOrder());
    }

    /**
     * Tests the method saturate().
     */
    @Test
    public void testSaturate_NumberingProblem() throws Exception {
        IAtomContainer mol = new AtomContainer();
        Atom a1 = new Atom("C");
        mol.addAtom(a1);
        Atom a2 = new Atom("C");
        mol.addAtom(a2);
        Atom a3 = new Atom("C");
        mol.addAtom(a3);
        Atom a4 = new Atom("H");
        mol.addAtom(a4);
        Atom a5 = new Atom("C");
        mol.addAtom(a5);
        Atom a6 = new Atom("H");
        mol.addAtom(a6);
        Atom a7 = new Atom("S");
        mol.addAtom(a7);
        Atom a8 = new Atom("H");
        mol.addAtom(a8);
        Atom a9 = new Atom("H");
        mol.addAtom(a9);
        Bond b1 = new Bond(a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        Bond b2 = new Bond(a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        Bond b3 = new Bond(a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        Bond b4 = new Bond(a5, a2, IBond.Order.SINGLE);
        mol.addBond(b4);
        Bond b5 = new Bond(a2, a6, IBond.Order.SINGLE);
        mol.addBond(b5);
        Bond b6 = new Bond(a3, a7, IBond.Order.SINGLE);
        mol.addBond(b6);
        Bond b7 = new Bond(a3, a8, IBond.Order.SINGLE);
        mol.addBond(b7);
        Bond b8 = new Bond(a7, a5, IBond.Order.SINGLE);
        mol.addBond(b8);
        Bond b9 = new Bond(a5, a9, IBond.Order.SINGLE);
        mol.addBond(b9);
        satcheck.saturate(mol);
        Assert.assertEquals(IBond.Order.SINGLE, b1.getOrder());
        Assert.assertEquals(IBond.Order.DOUBLE, b2.getOrder());
        Assert.assertEquals(IBond.Order.SINGLE, b6.getOrder());
        Assert.assertEquals(IBond.Order.SINGLE, b8.getOrder());
        Assert.assertEquals(IBond.Order.DOUBLE, b4.getOrder());
    }

    /**
     * Tests whether the saturation checker gets a proton right.
     */
    @Test
    public void testIsSaturated_Proton() throws Exception {
        // test H+
        IAtomContainer m = new AtomContainer();
        Atom h = new Atom("H");
        h.setFormalCharge(+1);
        m.addAtom(h);
        Assert.assertTrue(satcheck.isSaturated(h, m));
    }

    @Test
    public void testCalculateMissingHydrogens_Aromatic() throws Exception {
        IAtomContainer pyrrole = TestMoleculeFactory.makePyrrole();
        IAtom n = pyrrole.getAtom(1);
        IRingSet rs = Cycles.sssr(pyrrole).toRingSet();
        IRing ring = (IRing) rs.getAtomContainer(0);
        for (int j = 0; j < ring.getBondCount(); j++) {
            ring.getBond(j).setFlag(CDKConstants.ISAROMATIC, true);
        }
        Assert.assertEquals(5, ring.getBondCount());
        Assert.assertEquals(1, satcheck.calculateNumberOfImplicitHydrogens(n, pyrrole));
    }

}
