/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.ISingleElectron;

/**
 * @cdk.module test-valencycheck
 *
 * @author     steinbeck
 * @cdk.created    2003-02-20
 */
class SaturationCheckerTest {

    private SaturationChecker satcheck   = null;
    boolean           standAlone = false;

    @BeforeEach
    @Test
    void setUp() throws Exception {
        satcheck = new SaturationChecker();
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    void testAllSaturated() throws Exception {
        // test methane with explicit hydrogen
        IAtomContainer m = SilentChemObjectBuilder.getInstance().newAtomContainer();
        IAtom c = m.getBuilder().newInstance(IAtom.class, "C");
        IAtom h1 = m.getBuilder().newInstance(IAtom.class, "H");
        IAtom h2 = m.getBuilder().newInstance(IAtom.class, "H");
        IAtom h3 = m.getBuilder().newInstance(IAtom.class, "H");
        IAtom h4 = m.getBuilder().newInstance(IAtom.class, "H");
        m.addAtom(c);
        m.addAtom(h1);
        m.addAtom(h2);
        m.addAtom(h3);
        m.addAtom(h4);
        m.addBond(m.getBuilder().newInstance(IBond.class, c, h1));
        m.addBond(m.getBuilder().newInstance(IBond.class, c, h2));
        m.addBond(m.getBuilder().newInstance(IBond.class, c, h3));
        m.addBond(m.getBuilder().newInstance(IBond.class, c, h4));
        Assertions.assertTrue(satcheck.allSaturated(m));

        // test methane with implicit hydrogen
        m = SilentChemObjectBuilder.getInstance().newAtomContainer();
        c = m.getBuilder().newInstance(IAtom.class, "C");
        c.setImplicitHydrogenCount(4);
        m.addAtom(c);
        Assertions.assertTrue(satcheck.allSaturated(m));
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    void testIsSaturated() throws Exception {
        // test methane with explicit hydrogen
        IAtomContainer m = SilentChemObjectBuilder.getInstance().newAtomContainer();
        IAtom c = m.getBuilder().newInstance(IAtom.class, "C");
        IAtom h1 = m.getBuilder().newInstance(IAtom.class, "H");
        IAtom h2 = m.getBuilder().newInstance(IAtom.class, "H");
        IAtom h3 = m.getBuilder().newInstance(IAtom.class, "H");
        IAtom h4 = m.getBuilder().newInstance(IAtom.class, "H");
        m.addAtom(c);
        m.addAtom(h1);
        m.addAtom(h2);
        m.addAtom(h3);
        m.addAtom(h4);
        m.addBond(m.getBuilder().newInstance(IBond.class, c, h1));
        m.addBond(m.getBuilder().newInstance(IBond.class, c, h2));
        m.addBond(m.getBuilder().newInstance(IBond.class, c, h3));
        m.addBond(m.getBuilder().newInstance(IBond.class, c, h4));
        Assertions.assertTrue(satcheck.isSaturated(c, m));
        Assertions.assertTrue(satcheck.isSaturated(h1, m));
        Assertions.assertTrue(satcheck.isSaturated(h2, m));
        Assertions.assertTrue(satcheck.isSaturated(h3, m));
        Assertions.assertTrue(satcheck.isSaturated(h4, m));
    }

    /**
     * Tests whether the saturation checker considers negative
     * charges.
     */
    @Test
    void testIsSaturated_NegativelyChargedOxygen() throws Exception {
        // test methane with explicit hydrogen
        IAtomContainer m = SilentChemObjectBuilder.getInstance().newAtomContainer();
        IAtom c = m.getBuilder().newInstance(IAtom.class, "C");
        IAtom h1 = m.getBuilder().newInstance(IAtom.class, "H");
        IAtom h2 = m.getBuilder().newInstance(IAtom.class, "H");
        IAtom h3 = m.getBuilder().newInstance(IAtom.class, "H");
        IAtom o = m.getBuilder().newInstance(IAtom.class, "O");
        o.setFormalCharge(-1);
        m.addAtom(c);
        m.addAtom(h1);
        m.addAtom(h2);
        m.addAtom(h3);
        m.addAtom(o);
        m.addBond(m.getBuilder().newInstance(IBond.class, c, h1));
        m.addBond(m.getBuilder().newInstance(IBond.class, c, h2));
        m.addBond(m.getBuilder().newInstance(IBond.class, c, h3));
        m.addBond(m.getBuilder().newInstance(IBond.class, c, o));
        Assertions.assertTrue(satcheck.isSaturated(c, m));
        Assertions.assertTrue(satcheck.isSaturated(h1, m));
        Assertions.assertTrue(satcheck.isSaturated(h2, m));
        Assertions.assertTrue(satcheck.isSaturated(h3, m));
        Assertions.assertTrue(satcheck.isSaturated(o, m));
    }

    /**
     * Tests whether the saturation checker considers positive
     * charges.
     */
    @Test
    void testIsSaturated_PositivelyChargedNitrogen() throws Exception {
        // test methane with explicit hydrogen
        IAtomContainer m = SilentChemObjectBuilder.getInstance().newAtomContainer();
        IAtom n = m.getBuilder().newInstance(IAtom.class, "N");
        IAtom h1 = m.getBuilder().newInstance(IAtom.class, "H");
        IAtom h2 = m.getBuilder().newInstance(IAtom.class, "H");
        IAtom h3 = m.getBuilder().newInstance(IAtom.class, "H");
        IAtom h4 = m.getBuilder().newInstance(IAtom.class, "H");
        n.setFormalCharge(+1);
        m.addAtom(n);
        m.addAtom(h1);
        m.addAtom(h2);
        m.addAtom(h3);
        m.addAtom(h4);
        m.addBond(m.getBuilder().newInstance(IBond.class, n, h1));
        m.addBond(m.getBuilder().newInstance(IBond.class, n, h2));
        m.addBond(m.getBuilder().newInstance(IBond.class, n, h3));
        m.addBond(m.getBuilder().newInstance(IBond.class, n, h4));
        Assertions.assertTrue(satcheck.isSaturated(n, m));
        Assertions.assertTrue(satcheck.isSaturated(h1, m));
        Assertions.assertTrue(satcheck.isSaturated(h2, m));
        Assertions.assertTrue(satcheck.isSaturated(h3, m));
        Assertions.assertTrue(satcheck.isSaturated(h4, m));
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    void testSaturate() throws Exception {
        IAtomContainer m = SilentChemObjectBuilder.getInstance().newAtomContainer();
        // test ethene
        IAtom c1 = m.getBuilder().newInstance(IAtom.class, "C");
        c1.setImplicitHydrogenCount(2);
        IAtom c2 = m.getBuilder().newInstance(IAtom.class, "C");
        c2.setImplicitHydrogenCount(2);
        IBond b = m.getBuilder().newInstance(IBond.class, c1, c2, IBond.Order.SINGLE);
        // force single bond, saturate() must fix that
        m.addAtom(c1);
        m.addAtom(c2);
        m.addBond(b);
        satcheck.saturate(m);
        Assertions.assertEquals(IBond.Order.DOUBLE, b.getOrder());
    }

    /**
     *  A unit test for JUnit
     */
    @Test
    void testSaturate_Butene() throws Exception {
        IAtomContainer m = SilentChemObjectBuilder.getInstance().newAtomContainer();
        // test ethene
        IAtom c1 = m.getBuilder().newInstance(IAtom.class, "C");
        c1.setImplicitHydrogenCount(2);
        IAtom c2 = m.getBuilder().newInstance(IAtom.class, "C");
        c2.setImplicitHydrogenCount(1);
        IAtom c3 = m.getBuilder().newInstance(IAtom.class, "C");
        c3.setImplicitHydrogenCount(1);
        IAtom c4 = m.getBuilder().newInstance(IAtom.class, "C");
        c4.setImplicitHydrogenCount(2);
        IBond b1 = m.getBuilder().newInstance(IBond.class, c1, c2, IBond.Order.SINGLE);
        IBond b2 = m.getBuilder().newInstance(IBond.class, c3, c2, IBond.Order.SINGLE);
        IBond b3 = m.getBuilder().newInstance(IBond.class, c3, c4, IBond.Order.SINGLE);
        // force single bond, saturate() must fix that
        m.addAtom(c1);
        m.addAtom(c2);
        m.addAtom(c3);
        m.addAtom(c4);
        m.addBond(b1);
        m.addBond(b2);
        m.addBond(b3);
        satcheck.saturate(m);
        Assertions.assertEquals(IBond.Order.DOUBLE, b1.getOrder());
        Assertions.assertEquals(IBond.Order.SINGLE, b2.getOrder());
        Assertions.assertEquals(IBond.Order.DOUBLE, b3.getOrder());
    }

    @Test
    void testSaturate_ParaDiOxygenBenzene() throws Exception {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        IAtom a1 = mol.getBuilder().newInstance(IAtom.class, "C");
        mol.addAtom(a1);
        IAtom a2 = mol.getBuilder().newInstance(IAtom.class, "O");
        mol.addAtom(a2);
        IAtom a3 = mol.getBuilder().newInstance(IAtom.class, "C");
        mol.addAtom(a3);
        IAtom a4 = mol.getBuilder().newInstance(IAtom.class, "C");
        mol.addAtom(a4);
        IAtom a5 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a5);
        IAtom a6 = mol.getBuilder().newInstance(IAtom.class, "C");
        mol.addAtom(a6);
        IAtom a7 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a7);
        IAtom a8 = mol.getBuilder().newInstance(IAtom.class, "C");
        mol.addAtom(a8);
        IAtom a9 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a9);
        IAtom a10 = mol.getBuilder().newInstance(IAtom.class, "C");
        mol.addAtom(a10);
        IAtom a11 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a11);
        IAtom a12 = mol.getBuilder().newInstance(IAtom.class, "O");
        mol.addAtom(a12);
        IBond b1 = mol.getBuilder().newInstance(IBond.class, a2, a1, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = mol.getBuilder().newInstance(IBond.class, a5, a3, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = mol.getBuilder().newInstance(IBond.class, a3, a6, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = mol.getBuilder().newInstance(IBond.class, a7, a4, IBond.Order.SINGLE);
        mol.addBond(b6);
        IBond b7 = mol.getBuilder().newInstance(IBond.class, a4, a8, IBond.Order.SINGLE);
        mol.addBond(b7);
        IBond b8 = mol.getBuilder().newInstance(IBond.class, a6, a9, IBond.Order.SINGLE);
        mol.addBond(b8);
        IBond b9 = mol.getBuilder().newInstance(IBond.class, a6, a10, IBond.Order.SINGLE);
        mol.addBond(b9);
        IBond b10 = mol.getBuilder().newInstance(IBond.class, a8, a10, IBond.Order.SINGLE);
        mol.addBond(b10);
        IBond b11 = mol.getBuilder().newInstance(IBond.class, a8, a11, IBond.Order.SINGLE);
        mol.addBond(b11);
        IBond b12 = mol.getBuilder().newInstance(IBond.class, a10, a12, IBond.Order.SINGLE);
        mol.addBond(b12);
        satcheck.saturate(mol);
        Assertions.assertEquals(IBond.Order.DOUBLE, b1.getOrder());
        Assertions.assertEquals(IBond.Order.SINGLE, b2.getOrder());
        Assertions.assertEquals(IBond.Order.SINGLE, b3.getOrder());
        Assertions.assertEquals(IBond.Order.DOUBLE, b5.getOrder());
        Assertions.assertEquals(IBond.Order.DOUBLE, b7.getOrder());
        Assertions.assertEquals(IBond.Order.SINGLE, b9.getOrder());
        Assertions.assertEquals(IBond.Order.SINGLE, b10.getOrder());
        Assertions.assertEquals(IBond.Order.DOUBLE, b12.getOrder());
    }

    /**
     * Test sulfuric acid.
     */
    @Test
    void testBug772316() throws Exception {
        // test methane with explicit hydrogen
        IAtomContainer m = SilentChemObjectBuilder.getInstance().newAtomContainer();
        IAtom sulphur = m.getBuilder().newInstance(IAtom.class, "S");
        IAtom o1 = m.getBuilder().newInstance(IAtom.class, "O");
        IAtom o2 = m.getBuilder().newInstance(IAtom.class, "O");
        IAtom o3 = m.getBuilder().newInstance(IAtom.class, "O");
        IAtom o4 = m.getBuilder().newInstance(IAtom.class, "O");
        IAtom h1 = m.getBuilder().newInstance(IAtom.class, "H");
        IAtom h2 = m.getBuilder().newInstance(IAtom.class, "H");
        m.addAtom(sulphur);
        m.addAtom(o1);
        m.addAtom(o2);
        m.addAtom(o3);
        m.addAtom(o4);
        m.addAtom(h1);
        m.addAtom(h2);
        m.addBond(m.getBuilder().newInstance(IBond.class, sulphur, o1, IBond.Order.DOUBLE));
        m.addBond(m.getBuilder().newInstance(IBond.class, sulphur, o2, IBond.Order.DOUBLE));
        m.addBond(m.getBuilder().newInstance(IBond.class, sulphur, o3, IBond.Order.SINGLE));
        m.addBond(m.getBuilder().newInstance(IBond.class, sulphur, o4, IBond.Order.SINGLE));
        m.addBond(m.getBuilder().newInstance(IBond.class, h1, o3, IBond.Order.SINGLE));
        m.addBond(m.getBuilder().newInstance(IBond.class, h2, o4, IBond.Order.SINGLE));
        Assertions.assertTrue(satcheck.isSaturated(sulphur, m));
        Assertions.assertTrue(satcheck.isSaturated(o1, m));
        Assertions.assertTrue(satcheck.isSaturated(o2, m));
        Assertions.assertTrue(satcheck.isSaturated(o3, m));
        Assertions.assertTrue(satcheck.isSaturated(o4, m));
        Assertions.assertTrue(satcheck.isSaturated(h1, m));
        Assertions.assertTrue(satcheck.isSaturated(h2, m));
    }

    @Test
    void testBug777529() throws Exception {
        IAtomContainer m = SilentChemObjectBuilder.getInstance().newAtomContainer();
        m.addAtom(m.getBuilder().newInstance(IAtom.class, "C"));
        m.addAtom(m.getBuilder().newInstance(IAtom.class, "C"));
        m.addAtom(m.getBuilder().newInstance(IAtom.class, "C"));
        m.addAtom(m.getBuilder().newInstance(IAtom.class, "C"));
        m.addAtom(m.getBuilder().newInstance(IAtom.class, "C"));
        m.addAtom(m.getBuilder().newInstance(IAtom.class, "C"));
        m.addAtom(m.getBuilder().newInstance(IAtom.class, "C"));
        m.addAtom(m.getBuilder().newInstance(IAtom.class, "C"));
        m.addAtom(m.getBuilder().newInstance(IAtom.class, "C"));
        m.addAtom(m.getBuilder().newInstance(IAtom.class, "C"));
        m.addAtom(m.getBuilder().newInstance(IAtom.class, "C"));
        m.addAtom(m.getBuilder().newInstance(IAtom.class, "C"));
        m.addAtom(m.getBuilder().newInstance(IAtom.class, "C"));
        m.addAtom(m.getBuilder().newInstance(IAtom.class, "C"));
        m.addAtom(m.getBuilder().newInstance(IAtom.class, "C"));
        m.addAtom(m.getBuilder().newInstance(IAtom.class, "C"));
        m.addAtom(m.getBuilder().newInstance(IAtom.class, "C"));
        m.addAtom(m.getBuilder().newInstance(IAtom.class, "C"));
        m.addAtom(m.getBuilder().newInstance(IAtom.class, "C"));
        m.addAtom(m.getBuilder().newInstance(IAtom.class, "C"));
        m.addAtom(m.getBuilder().newInstance(IAtom.class, "O"));
        m.addAtom(m.getBuilder().newInstance(IAtom.class, "O"));
        m.addAtom(m.getBuilder().newInstance(IAtom.class, "F"));
        m.getAtom(0).setImplicitHydrogenCount(1);
        m.getAtom(2).setImplicitHydrogenCount(1);
        m.getAtom(3).setImplicitHydrogenCount(1);
        m.getAtom(6).setImplicitHydrogenCount(1);
        m.getAtom(7).setImplicitHydrogenCount(1);
        m.getAtom(8).setImplicitHydrogenCount(1);
        m.getAtom(9).setImplicitHydrogenCount(1);
        //m.getAtomAt(10).setHydrogenCount(1);
        //m.getAtomAt(12).setHydrogenCount(1);
        m.getAtom(14).setImplicitHydrogenCount(1);
        m.getAtom(15).setImplicitHydrogenCount(1);
        m.getAtom(17).setImplicitHydrogenCount(1);
        m.getAtom(18).setImplicitHydrogenCount(1);
        m.getAtom(19).setImplicitHydrogenCount(3);
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.SINGLE);
        m.addBond(2, 3, IBond.Order.SINGLE);
        m.addBond(3, 4, IBond.Order.SINGLE);
        m.addBond(4, 5, IBond.Order.SINGLE);
        m.addBond(5, 6, IBond.Order.SINGLE);
        m.addBond(6, 7, IBond.Order.SINGLE);
        m.addBond(7, 8, IBond.Order.SINGLE);
        m.addBond(8, 9, IBond.Order.SINGLE);
        m.addBond(5, 10, IBond.Order.SINGLE);
        m.addBond(9, 10, IBond.Order.SINGLE);
        m.addBond(10, 11, IBond.Order.SINGLE);
        m.addBond(0, 12, IBond.Order.SINGLE);
        m.addBond(4, 12, IBond.Order.SINGLE);
        m.addBond(11, 12, IBond.Order.SINGLE);
        m.addBond(11, 13, IBond.Order.SINGLE);
        m.addBond(13, 14, IBond.Order.SINGLE);
        m.addBond(14, 15, IBond.Order.SINGLE);
        m.addBond(15, 16, IBond.Order.SINGLE);
        m.addBond(16, 17, IBond.Order.SINGLE);
        m.addBond(13, 18, IBond.Order.SINGLE);
        m.addBond(17, 18, IBond.Order.SINGLE);
        m.addBond(20, 16, IBond.Order.SINGLE);
        m.addBond(11, 21, IBond.Order.SINGLE);
        m.addBond(22, 1, IBond.Order.SINGLE);
        m.addBond(20, 19, IBond.Order.SINGLE);
        m.getAtom(0).setFlag(IChemObject.AROMATIC, true);
        m.getAtom(1).setFlag(IChemObject.AROMATIC, true);
        m.getAtom(2).setFlag(IChemObject.AROMATIC, true);
        m.getAtom(3).setFlag(IChemObject.AROMATIC, true);
        m.getAtom(4).setFlag(IChemObject.AROMATIC, true);
        m.getAtom(12).setFlag(IChemObject.AROMATIC, true);
        m.getAtom(5).setFlag(IChemObject.AROMATIC, true);
        m.getAtom(6).setFlag(IChemObject.AROMATIC, true);
        m.getAtom(7).setFlag(IChemObject.AROMATIC, true);
        m.getAtom(8).setFlag(IChemObject.AROMATIC, true);
        m.getAtom(9).setFlag(IChemObject.AROMATIC, true);
        m.getAtom(10).setFlag(IChemObject.AROMATIC, true);
        m.getBond(0).setFlag(IChemObject.AROMATIC, true);
        m.getBond(1).setFlag(IChemObject.AROMATIC, true);
        m.getBond(2).setFlag(IChemObject.AROMATIC, true);
        m.getBond(3).setFlag(IChemObject.AROMATIC, true);
        m.getBond(5).setFlag(IChemObject.AROMATIC, true);
        m.getBond(6).setFlag(IChemObject.AROMATIC, true);
        m.getBond(7).setFlag(IChemObject.AROMATIC, true);
        m.getBond(8).setFlag(IChemObject.AROMATIC, true);
        m.getBond(9).setFlag(IChemObject.AROMATIC, true);
        m.getBond(10).setFlag(IChemObject.AROMATIC, true);
        m.getBond(12).setFlag(IChemObject.AROMATIC, true);
        m.getBond(13).setFlag(IChemObject.AROMATIC, true);
        satcheck.saturate(m);
        Assertions.assertTrue(m.getBond(4).getOrder() == IBond.Order.SINGLE);
        Assertions.assertTrue(m.getBond(9).getOrder() == IBond.Order.DOUBLE ^ m.getBond(5).getOrder() == IBond.Order.DOUBLE);
        Assertions.assertTrue(m.getBond(13).getOrder() == IBond.Order.DOUBLE
                ^ m.getBond(3).getOrder() == IBond.Order.DOUBLE);
    }

    @Test
    void testCalculateNumberOfImplicitHydrogens() throws Exception {
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();

        IAtomContainer proton = builder.newInstance(IAtomContainer.class);
        IAtom hplus = builder.newInstance(IAtom.class, "H");
        hplus.setFormalCharge(1);
        proton.addAtom(hplus);
        Assertions.assertEquals(0, satcheck.calculateNumberOfImplicitHydrogens(hplus, proton));

        IAtomContainer hydrogenRadical = builder.newInstance(IAtomContainer.class);
        IAtom hradical = builder.newInstance(IAtom.class, "H");
        hydrogenRadical.addAtom(hradical);
        hydrogenRadical.addSingleElectron(builder.newInstance(ISingleElectron.class, hradical));
        Assertions.assertEquals(0, satcheck.calculateNumberOfImplicitHydrogens(hradical, hydrogenRadical));

        IAtomContainer hydrogen = builder.newInstance(IAtomContainer.class);
        IAtom h = builder.newInstance(IAtom.class, "H");
        hydrogen.addAtom(h);
        Assertions.assertEquals(1, satcheck.calculateNumberOfImplicitHydrogens(h, hydrogen));

        IAtomContainer coRad = builder.newInstance(IAtomContainer.class);
        IAtom c = builder.newInstance(IAtom.class, "C");
        IAtom o = builder.newInstance(IAtom.class, "O");
        IBond bond = builder.newInstance(IBond.class, c, o, IBond.Order.DOUBLE);
        coRad.addAtom(c);
        coRad.addAtom(o);
        coRad.addBond(bond);
        coRad.addSingleElectron(builder.newInstance(ISingleElectron.class, c));
        Assertions.assertEquals(1, satcheck.calculateNumberOfImplicitHydrogens(c, coRad));
    }

}
