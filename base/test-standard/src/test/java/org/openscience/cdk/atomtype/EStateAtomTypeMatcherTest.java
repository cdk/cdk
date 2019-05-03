/* Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.atomtype;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * @cdk.module test-standard
 */
public class EStateAtomTypeMatcherTest extends CDKTestCase {

    static EStateAtomTypeMatcher matcher;
    IAtomContainer               mol = null;

    @BeforeClass
    public static void setUp() {
        matcher = new EStateAtomTypeMatcher();
    }

    public EStateAtomTypeMatcherTest() {
        super();
    }

    @Test
    public void testEStateAtomTypeMatcher() {
        Assert.assertNotNull(matcher);
    }

    IRingSet getRings() {
        IRingSet rs = null;
        try {
            AllRingsFinder arf = new AllRingsFinder();
            rs = arf.findAllRings(mol);
        } catch (Exception e) {
            System.out.println("Could not find all rings: " + e.getMessage());
        }
        return (rs);
    }

    private boolean testAtom(String expectedAtType, IAtom atom) {
        return expectedAtType.equals(matcher.findMatchingAtomType(mol, atom).getAtomTypeName());
    }

    @Test
    public void testFindMatchingAtomType_IAtomContainer() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("C");
        final IAtomType.Hybridization thisHybridization = IAtomType.Hybridization.SP3;
        atom.setHybridization(thisHybridization);
        mol.addAtom(atom);

        // just check consistency; other methods do perception testing
        IAtomType[] types = matcher.findMatchingAtomTypes(mol);
        for (int i = 0; i < types.length; i++) {
            IAtomType type = matcher.findMatchingAtomType(mol, mol.getAtom(i));
            Assert.assertEquals(type.getAtomTypeName(), types[i].getAtomTypeName());
        }
    }

    @Test
    public void testSP3Atoms() {
        //Testing with CC(C)(C)CC
        mol = new AtomContainer();
        IAtom a1 = mol.getBuilder().newInstance(IAtom.class, "C");
        mol.addAtom(a1);
        IAtom a2 = mol.getBuilder().newInstance(IAtom.class, "C");
        mol.addAtom(a2);
        IAtom a3 = mol.getBuilder().newInstance(IAtom.class, "C");
        mol.addAtom(a3);
        IAtom a4 = mol.getBuilder().newInstance(IAtom.class, "C");
        mol.addAtom(a4);
        IAtom a5 = mol.getBuilder().newInstance(IAtom.class, "C");
        mol.addAtom(a5);
        IAtom a6 = mol.getBuilder().newInstance(IAtom.class, "C");
        mol.addAtom(a6);
        IAtom a7 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a7);
        IAtom a8 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a8);
        IAtom a9 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a9);
        IAtom a10 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a10);
        IAtom a11 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a11);
        IAtom a12 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a12);
        IAtom a13 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a13);
        IAtom a14 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a14);
        IAtom a15 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a15);
        IAtom a16 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a16);
        IAtom a17 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a17);
        IAtom a18 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a18);
        IAtom a19 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a19);
        IAtom a20 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a20);
        IBond b1 = mol.getBuilder().newInstance(IBond.class, a2, a1, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newInstance(IBond.class, a3, a2, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newInstance(IBond.class, a4, a2, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = mol.getBuilder().newInstance(IBond.class, a5, a2, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = mol.getBuilder().newInstance(IBond.class, a6, a5, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = mol.getBuilder().newInstance(IBond.class, a1, a7, IBond.Order.SINGLE);
        mol.addBond(b6);
        IBond b7 = mol.getBuilder().newInstance(IBond.class, a1, a8, IBond.Order.SINGLE);
        mol.addBond(b7);
        IBond b8 = mol.getBuilder().newInstance(IBond.class, a1, a9, IBond.Order.SINGLE);
        mol.addBond(b8);
        IBond b9 = mol.getBuilder().newInstance(IBond.class, a3, a10, IBond.Order.SINGLE);
        mol.addBond(b9);
        IBond b10 = mol.getBuilder().newInstance(IBond.class, a3, a11, IBond.Order.SINGLE);
        mol.addBond(b10);
        IBond b11 = mol.getBuilder().newInstance(IBond.class, a3, a12, IBond.Order.SINGLE);
        mol.addBond(b11);
        IBond b12 = mol.getBuilder().newInstance(IBond.class, a4, a13, IBond.Order.SINGLE);
        mol.addBond(b12);
        IBond b13 = mol.getBuilder().newInstance(IBond.class, a4, a14, IBond.Order.SINGLE);
        mol.addBond(b13);
        IBond b14 = mol.getBuilder().newInstance(IBond.class, a4, a15, IBond.Order.SINGLE);
        mol.addBond(b14);
        IBond b15 = mol.getBuilder().newInstance(IBond.class, a5, a16, IBond.Order.SINGLE);
        mol.addBond(b15);
        IBond b16 = mol.getBuilder().newInstance(IBond.class, a5, a17, IBond.Order.SINGLE);
        mol.addBond(b16);
        IBond b17 = mol.getBuilder().newInstance(IBond.class, a6, a18, IBond.Order.SINGLE);
        mol.addBond(b17);
        IBond b18 = mol.getBuilder().newInstance(IBond.class, a6, a19, IBond.Order.SINGLE);
        mol.addBond(b18);
        IBond b19 = mol.getBuilder().newInstance(IBond.class, a6, a20, IBond.Order.SINGLE);
        mol.addBond(b19);

        matcher.setRingSet(getRings());
        Assert.assertTrue(testAtom("SsCH3", a1));
        Assert.assertTrue(testAtom("SssssC", a2));
        Assert.assertTrue(testAtom("SsCH3", a3));
        Assert.assertTrue(testAtom("SsCH3", a4));
        Assert.assertTrue(testAtom("SssCH2", a5));
        Assert.assertTrue(testAtom("SsCH3", a6));
        Assert.assertTrue(testAtom("SsH", a7));
        Assert.assertTrue(testAtom("SsH", a8));
    }

    @Test
    public void testSP2Atoms() {
        //Test with C=CC=N
        mol = new AtomContainer();
        IAtom a1 = mol.getBuilder().newInstance(IAtom.class, "C");
        mol.addAtom(a1);
        IAtom a2 = mol.getBuilder().newInstance(IAtom.class, "C");
        mol.addAtom(a2);
        IAtom a3 = mol.getBuilder().newInstance(IAtom.class, "C");
        mol.addAtom(a3);
        IAtom a4 = mol.getBuilder().newInstance(IAtom.class, "N");
        mol.addAtom(a4);
        IAtom a5 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a5);
        IAtom a6 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a6);
        IAtom a7 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a7);
        IAtom a8 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a8);
        IAtom a9 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a9);
        IBond b1 = mol.getBuilder().newInstance(IBond.class, a2, a1, IBond.Order.DOUBLE);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newInstance(IBond.class, a3, a2, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newInstance(IBond.class, a4, a3, IBond.Order.DOUBLE);
        mol.addBond(b3);
        IBond b4 = mol.getBuilder().newInstance(IBond.class, a1, a5, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = mol.getBuilder().newInstance(IBond.class, a1, a6, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = mol.getBuilder().newInstance(IBond.class, a2, a7, IBond.Order.SINGLE);
        mol.addBond(b6);
        IBond b7 = mol.getBuilder().newInstance(IBond.class, a3, a8, IBond.Order.SINGLE);
        mol.addBond(b7);
        IBond b8 = mol.getBuilder().newInstance(IBond.class, a4, a9, IBond.Order.SINGLE);
        mol.addBond(b8);

        matcher.setRingSet(getRings());
        Assert.assertTrue(testAtom("SdCH2", a1));
        Assert.assertTrue(testAtom("SdsCH", a2));
        Assert.assertTrue(testAtom("SdsCH", a3));
        Assert.assertTrue(testAtom("SdNH", a4));
        Assert.assertTrue(testAtom("SsH", a9));
    }

    @Test
    public void testSPAtoms() {
        //Testing with  C#CCC#N
        mol = new AtomContainer();
        IAtom a1 = mol.getBuilder().newInstance(IAtom.class, "C");
        mol.addAtom(a1);
        IAtom a2 = mol.getBuilder().newInstance(IAtom.class, "C");
        mol.addAtom(a2);
        IAtom a3 = mol.getBuilder().newInstance(IAtom.class, "C");
        mol.addAtom(a3);
        IAtom a4 = mol.getBuilder().newInstance(IAtom.class, "C");
        mol.addAtom(a4);
        IAtom a5 = mol.getBuilder().newInstance(IAtom.class, "N");
        mol.addAtom(a5);
        IAtom a6 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a6);
        IAtom a7 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a7);
        IAtom a8 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a8);
        IBond b1 = mol.getBuilder().newInstance(IBond.class, a2, a1, IBond.Order.TRIPLE);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newInstance(IBond.class, a3, a2, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newInstance(IBond.class, a4, a3, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = mol.getBuilder().newInstance(IBond.class, a5, a4, IBond.Order.TRIPLE);
        mol.addBond(b4);
        IBond b5 = mol.getBuilder().newInstance(IBond.class, a1, a6, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = mol.getBuilder().newInstance(IBond.class, a3, a7, IBond.Order.SINGLE);
        mol.addBond(b6);
        IBond b7 = mol.getBuilder().newInstance(IBond.class, a3, a8, IBond.Order.SINGLE);
        mol.addBond(b7);

        matcher.setRingSet(getRings());
        Assert.assertTrue(testAtom("StCH", a1));
        Assert.assertTrue(testAtom("StsC", a2));
        Assert.assertTrue(testAtom("SssCH2", a3));
        Assert.assertTrue(testAtom("StsC", a4));
        Assert.assertTrue(testAtom("StN", a5));
    }

    @Test
    public void testAromaticAtoms() {
        //Testing with C1=CN=CC=C1C
        mol = new AtomContainer();
        IAtom a1 = mol.getBuilder().newInstance(IAtom.class, "C");
        a1.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a1);
        IAtom a2 = mol.getBuilder().newInstance(IAtom.class, "C");
        a2.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a2);
        IAtom a3 = mol.getBuilder().newInstance(IAtom.class, "N");
        a3.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a3);
        IAtom a4 = mol.getBuilder().newInstance(IAtom.class, "C");
        a4.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a4);
        IAtom a5 = mol.getBuilder().newInstance(IAtom.class, "C");
        a5.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a5);
        IAtom a6 = mol.getBuilder().newInstance(IAtom.class, "C");
        a6.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a6);
        IAtom a7 = mol.getBuilder().newInstance(IAtom.class, "C");
        mol.addAtom(a7);
        IAtom a8 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a8);
        IAtom a9 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a9);
        IAtom a10 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a10);
        IAtom a11 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a11);
        IAtom a12 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a12);
        IAtom a13 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a13);
        IAtom a14 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a14);
        IBond b1 = mol.getBuilder().newInstance(IBond.class, a2, a1, IBond.Order.DOUBLE);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newInstance(IBond.class, a3, a2, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newInstance(IBond.class, a4, a3, IBond.Order.DOUBLE);
        mol.addBond(b3);
        IBond b4 = mol.getBuilder().newInstance(IBond.class, a5, a4, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = mol.getBuilder().newInstance(IBond.class, a6, a5, IBond.Order.DOUBLE);
        mol.addBond(b5);
        IBond b6 = mol.getBuilder().newInstance(IBond.class, a6, a1, IBond.Order.SINGLE);
        mol.addBond(b6);
        IBond b7 = mol.getBuilder().newInstance(IBond.class, a7, a6, IBond.Order.SINGLE);
        mol.addBond(b7);
        IBond b8 = mol.getBuilder().newInstance(IBond.class, a1, a8, IBond.Order.SINGLE);
        mol.addBond(b8);
        IBond b9 = mol.getBuilder().newInstance(IBond.class, a2, a9, IBond.Order.SINGLE);
        mol.addBond(b9);
        IBond b10 = mol.getBuilder().newInstance(IBond.class, a5, a10, IBond.Order.SINGLE);
        mol.addBond(b10);
        IBond b11 = mol.getBuilder().newInstance(IBond.class, a7, a11, IBond.Order.SINGLE);
        mol.addBond(b11);
        IBond b12 = mol.getBuilder().newInstance(IBond.class, a7, a12, IBond.Order.SINGLE);
        mol.addBond(b12);
        IBond b13 = mol.getBuilder().newInstance(IBond.class, a7, a13, IBond.Order.SINGLE);
        mol.addBond(b13);
        IBond b14 = mol.getBuilder().newInstance(IBond.class, a4, a14, IBond.Order.SINGLE);
        mol.addBond(b14);

        matcher.setRingSet(getRings());
        Assert.assertTrue(testAtom("SaaCH", a1));
        Assert.assertTrue(testAtom("SaaCH", a2));
        Assert.assertTrue(testAtom("SaaN", a3));
        Assert.assertTrue(testAtom("SaaCH", a4));
        Assert.assertTrue(testAtom("SaaCH", a5));
        Assert.assertTrue(testAtom("SsaaC", a6));
        Assert.assertTrue(testAtom("SsCH3", a7));
    }

    @Test
    public void testBenzeneFromSmiles() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        mol = sp.parseSmiles("C1=CC=CC=C1");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);

        matcher.setRingSet(getRings());
        Iterator<IAtom> atoms = mol.atoms().iterator();
        while (atoms.hasNext()) {
            IAtom atom = atoms.next();
            if (atom.getSymbol().equals("C")) {
                Assert.assertTrue(testAtom("SaaCH", atom));
            }
        }
    }

    @Test
    public void testNaphthalene() {
        //Testing with C1=CC2C=CC=CC=2C=C1
        mol = new AtomContainer();
        IAtom a1 = mol.getBuilder().newInstance(IAtom.class, "C");
        a1.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a1);
        IAtom a2 = mol.getBuilder().newInstance(IAtom.class, "C");
        a2.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a2);
        IAtom a3 = mol.getBuilder().newInstance(IAtom.class, "C");
        a3.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a3);
        IAtom a4 = mol.getBuilder().newInstance(IAtom.class, "C");
        a4.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a4);
        IAtom a5 = mol.getBuilder().newInstance(IAtom.class, "C");
        a5.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a5);
        IAtom a6 = mol.getBuilder().newInstance(IAtom.class, "C");
        a6.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a6);
        IAtom a7 = mol.getBuilder().newInstance(IAtom.class, "C");
        a7.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a7);
        IAtom a8 = mol.getBuilder().newInstance(IAtom.class, "C");
        a8.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a8);
        IAtom a9 = mol.getBuilder().newInstance(IAtom.class, "C");
        a9.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a9);
        IAtom a10 = mol.getBuilder().newInstance(IAtom.class, "C");
        a10.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addAtom(a10);
        IAtom a11 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a11);
        IAtom a12 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a12);
        IAtom a13 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a13);
        IAtom a14 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a14);
        IAtom a15 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a15);
        IAtom a16 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a16);
        IAtom a17 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a17);
        IAtom a18 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a18);
        IBond b1 = mol.getBuilder().newInstance(IBond.class, a2, a1, IBond.Order.DOUBLE);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newInstance(IBond.class, a3, a2, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newInstance(IBond.class, a4, a3, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = mol.getBuilder().newInstance(IBond.class, a5, a4, IBond.Order.DOUBLE);
        mol.addBond(b4);
        IBond b5 = mol.getBuilder().newInstance(IBond.class, a6, a5, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = mol.getBuilder().newInstance(IBond.class, a7, a6, IBond.Order.DOUBLE);
        mol.addBond(b6);
        IBond b7 = mol.getBuilder().newInstance(IBond.class, a8, a7, IBond.Order.SINGLE);
        mol.addBond(b7);
        IBond b8 = mol.getBuilder().newInstance(IBond.class, a8, a3, IBond.Order.DOUBLE);
        mol.addBond(b8);
        IBond b9 = mol.getBuilder().newInstance(IBond.class, a9, a8, IBond.Order.SINGLE);
        mol.addBond(b9);
        IBond b10 = mol.getBuilder().newInstance(IBond.class, a10, a9, IBond.Order.DOUBLE);
        mol.addBond(b10);
        IBond b11 = mol.getBuilder().newInstance(IBond.class, a10, a1, IBond.Order.SINGLE);
        mol.addBond(b11);
        IBond b12 = mol.getBuilder().newInstance(IBond.class, a1, a11, IBond.Order.SINGLE);
        mol.addBond(b12);
        IBond b13 = mol.getBuilder().newInstance(IBond.class, a2, a12, IBond.Order.SINGLE);
        mol.addBond(b13);
        IBond b14 = mol.getBuilder().newInstance(IBond.class, a10, a13, IBond.Order.SINGLE);
        mol.addBond(b14);
        IBond b15 = mol.getBuilder().newInstance(IBond.class, a9, a14, IBond.Order.SINGLE);
        mol.addBond(b15);
        IBond b16 = mol.getBuilder().newInstance(IBond.class, a4, a15, IBond.Order.SINGLE);
        mol.addBond(b16);
        IBond b17 = mol.getBuilder().newInstance(IBond.class, a5, a16, IBond.Order.SINGLE);
        mol.addBond(b17);
        IBond b18 = mol.getBuilder().newInstance(IBond.class, a7, a17, IBond.Order.SINGLE);
        mol.addBond(b18);
        IBond b19 = mol.getBuilder().newInstance(IBond.class, a6, a18, IBond.Order.SINGLE);
        mol.addBond(b19);

        matcher.setRingSet(getRings());
        Assert.assertTrue(testAtom("SaaCH", a1));
        Assert.assertTrue(testAtom("SaaCH", a2));
        Assert.assertTrue(testAtom("SaaaC", a3));
        Assert.assertTrue(testAtom("SaaCH", a4));
        Assert.assertTrue(testAtom("SaaCH", a5));
        Assert.assertTrue(testAtom("SaaCH", a6));
        Assert.assertTrue(testAtom("SaaCH", a7));
        Assert.assertTrue(testAtom("SaaaC", a8));
        Assert.assertTrue(testAtom("SaaCH", a9));
        Assert.assertTrue(testAtom("SaaCH", a10));
    }

    @Test
    public void testChargedAtoms() {
        //Testing with C[N+]
        mol = new AtomContainer();
        IAtom a1 = mol.getBuilder().newInstance(IAtom.class, "C");
        mol.addAtom(a1);
        IAtom a2 = mol.getBuilder().newInstance(IAtom.class, "N");
        a2.setFormalCharge(+1);
        mol.addAtom(a2);
        IAtom a3 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a3);
        IAtom a4 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a4);
        IAtom a5 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a5);
        IAtom a6 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a6);
        IAtom a7 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a7);
        IAtom a8 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a8);
        IBond b1 = mol.getBuilder().newInstance(IBond.class, a2, a1, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = mol.getBuilder().newInstance(IBond.class, a1, a5, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = mol.getBuilder().newInstance(IBond.class, a2, a6, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = mol.getBuilder().newInstance(IBond.class, a2, a7, IBond.Order.SINGLE);
        mol.addBond(b6);
        IBond b7 = mol.getBuilder().newInstance(IBond.class, a2, a8, IBond.Order.SINGLE);
        mol.addBond(b7);

        matcher.setRingSet(getRings());
        Assert.assertTrue(testAtom("SsCH3", a1));
        Assert.assertTrue(testAtom("SsNpH3", a2));
    }

    @Test
    public void testNaCl() {
        //Testing with [Na+].[Cl-]
        mol = new AtomContainer();
        IAtom a1 = mol.getBuilder().newInstance(IAtom.class, "Na");
        a1.setFormalCharge(+1);
        mol.addAtom(a1);
        IAtom a2 = mol.getBuilder().newInstance(IAtom.class, "Cl");
        a2.setFormalCharge(-1);
        mol.addAtom(a2);

        matcher.setRingSet(getRings());
        Assert.assertTrue(testAtom("SNap", a1));
        Assert.assertTrue(testAtom("SClm", a2));

        //Testing with different presentation - [Na]Cl
        mol = new AtomContainer();
        a1 = mol.getBuilder().newInstance(IAtom.class, "Na");
        mol.addAtom(a1);
        a2 = mol.getBuilder().newInstance(IAtom.class, "Cl");
        mol.addAtom(a2);
        IBond b1 = mol.getBuilder().newInstance(IBond.class, a2, a1, IBond.Order.SINGLE);
        mol.addBond(b1);

        matcher.setRingSet(getRings());
        Assert.assertTrue(testAtom("SsNa", a1));
        Assert.assertTrue(testAtom("SsCl", a2));
    }

}
