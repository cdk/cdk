/* Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
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

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * This class tests the matching of atom types defined in the
 * structgen atom type list.
 *
 * @cdk.module test-structgen
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StructGenMatcherTest extends AbstractAtomTypeTest {

    private final static String          ATOMTYPE_LIST = "structgen_atomtypes.owl";

    private final static AtomTypeFactory factory       = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/"
                                                               + ATOMTYPE_LIST, SilentChemObjectBuilder.getInstance());

    @Override
    public String getAtomTypeListName() {
        return ATOMTYPE_LIST;
    };

    @Override
    public AtomTypeFactory getFactory() {
        return factory;
    }

    @Override
    public IAtomTypeMatcher getAtomTypeMatcher(IChemObjectBuilder builder) {
        return new StructGenMatcher();
    }

    private static Map<String, Integer> testedAtomTypes = new HashMap<String, Integer>();

    @Test
    public void testStructGenMatcher() throws Exception {
        StructGenMatcher matcher = new StructGenMatcher();
        Assert.assertNotNull(matcher);
    }

    @Test
    public void testFindMatchingAtomType_IAtomContainer_IAtom() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom atom = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        atom.setImplicitHydrogenCount(4);
        mol.addAtom(atom);

        StructGenMatcher atm = new StructGenMatcher();
        IAtomType matched = atm.findMatchingAtomType(mol, atom);
        Assert.assertNotNull(matched);

        Assert.assertEquals("C", matched.getSymbol());
    }

    @Test
    public void testN3() throws Exception {
        IAtomContainer mol = new AtomContainer();
        Atom atom = new Atom("N");
        atom.setImplicitHydrogenCount(3);
        mol.addAtom(atom);

        StructGenMatcher atm = new StructGenMatcher();
        IAtomType matched = atm.findMatchingAtomType(mol, atom);
        Assert.assertNotNull(matched);

        Assert.assertEquals("N", matched.getSymbol());
    }

    @Test
    public void testFlourine() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        atom1.setImplicitHydrogenCount(0);
        mol.addAtom(atom1);
        for (int i = 0; i < 4; i++) {
            IAtom floruineAtom = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "F");
            mol.addAtom(floruineAtom);
            IBond bond = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, floruineAtom, atom1);
            mol.addBond(bond);
        }

        StructGenMatcher matcher = new StructGenMatcher();
        IAtomType matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "C4", matched);

        for (int i = 1; i < mol.getAtomCount(); i++) {
            IAtom atom = mol.getAtom(i);
            matched = matcher.findMatchingAtomType(mol, atom);
            assertAtomType(testedAtomTypes, "atom " + i + " failed to match", "F1", matched);
        }
    }

    @Test
    public void testChlorine() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        atom1.setImplicitHydrogenCount(0);
        mol.addAtom(atom1);
        for (int i = 0; i < 4; i++) {
            IAtom floruineAtom = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "Cl");
            mol.addAtom(floruineAtom);
            IBond bond = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, floruineAtom, atom1);
            mol.addBond(bond);
        }

        StructGenMatcher matcher = new StructGenMatcher();
        IAtomType matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "C4", matched);

        for (int i = 1; i < mol.getAtomCount(); i++) {
            IAtom atom = mol.getAtom(i);
            matched = matcher.findMatchingAtomType(mol, atom);
            assertAtomType(testedAtomTypes, "atom " + i + " failed to match", "Cl1", matched);
        }
    }

    @Test
    public void testBromine() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        atom1.setImplicitHydrogenCount(0);
        mol.addAtom(atom1);
        for (int i = 0; i < 4; i++) {
            IAtom floruineAtom = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "Br");
            mol.addAtom(floruineAtom);
            IBond bond = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, floruineAtom, atom1);
            mol.addBond(bond);
        }

        StructGenMatcher matcher = new StructGenMatcher();
        IAtomType matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "C4", matched);

        for (int i = 1; i < mol.getAtomCount(); i++) {
            IAtom atom = mol.getAtom(i);
            matched = matcher.findMatchingAtomType(mol, atom);
            assertAtomType(testedAtomTypes, "atom " + i + " failed to match", "Br1", matched);
        }
    }

    @Test
    public void testIodine() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        atom1.setImplicitHydrogenCount(0);
        mol.addAtom(atom1);
        for (int i = 0; i < 4; i++) {
            IAtom floruineAtom = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "I");
            mol.addAtom(floruineAtom);
            IBond bond = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, floruineAtom, atom1);
            mol.addBond(bond);
        }

        StructGenMatcher matcher = new StructGenMatcher();
        IAtomType matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "C4", matched);

        for (int i = 1; i < mol.getAtomCount(); i++) {
            IAtom atom = mol.getAtom(i);
            matched = matcher.findMatchingAtomType(mol, atom);
            assertAtomType(testedAtomTypes, "atom " + i + " failed to match", "I1", matched);
        }
    }

    @Test
    public void testLithium() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "Li");
        IAtom atom2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "F");
        IBond bond = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, atom1, atom2);
        mol.addAtom(atom1);
        mol.addAtom(atom2);
        mol.addBond(bond);

        StructGenMatcher matcher = new StructGenMatcher();
        IAtomType matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "Li1", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(1));
        assertAtomType(testedAtomTypes, "F1", matched);
    }

    /*
     * Tests As3, Cl1
     */
    @Test
    public void testArsenic() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "As");
        atom1.setImplicitHydrogenCount(0);
        mol.addAtom(atom1);
        for (int i = 0; i < 3; i++) {
            IAtom atom = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "Cl");
            mol.addAtom(atom);
            IBond bond = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, atom, atom1,
                    IBond.Order.SINGLE);
            mol.addBond(bond);
        }

        StructGenMatcher matcher = new StructGenMatcher();
        IAtomType matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "As3", matched);

        for (int i = 1; i < mol.getAtomCount(); i++) {
            IAtom atom = mol.getAtom(i);
            matched = matcher.findMatchingAtomType(mol, atom);
            assertAtomType(testedAtomTypes, "atom " + i + " failed to match", "Cl1", matched);
        }
    }

    /*
     * Tests C4, O2
     */
    @Test
    public void testOxygen1() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom carbon = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        IAtom o1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "O");
        IAtom o2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "O");

        carbon.setImplicitHydrogenCount(1);
        o1.setImplicitHydrogenCount(1);
        o2.setImplicitHydrogenCount(0);

        IBond bond1 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, carbon, o1, IBond.Order.SINGLE);
        IBond bond2 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, carbon, o2, IBond.Order.DOUBLE);

        mol.addAtom(carbon);
        mol.addAtom(o1);
        mol.addAtom(o2);
        mol.addBond(bond1);
        mol.addBond(bond2);

        StructGenMatcher matcher = new StructGenMatcher();

        // look at the sp2 O first
        IAtomType matched = matcher.findMatchingAtomType(mol, mol.getAtom(2));
        assertAtomType(testedAtomTypes, "O2", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "C4", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(1));
        assertAtomType(testedAtomTypes, "O2", matched);
    }

    /*
     * Tests O2, H1
     */
    @Test
    public void testOxygen2() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom o1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "O");
        IAtom o2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "O");
        IAtom h1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "H");
        IAtom h2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "H");

        IBond bond1 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, h1, o1, IBond.Order.SINGLE);
        IBond bond2 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, o1, o2, IBond.Order.SINGLE);
        IBond bond3 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, o2, h2, IBond.Order.SINGLE);

        mol.addAtom(o1);
        mol.addAtom(o2);
        mol.addAtom(h1);
        mol.addAtom(h2);

        mol.addBond(bond1);
        mol.addBond(bond2);
        mol.addBond(bond3);

        StructGenMatcher matcher = new StructGenMatcher();
        IAtomType matched;

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "O2", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(1));
        assertAtomType(testedAtomTypes, "O2", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(2));
        assertAtomType(testedAtomTypes, "H1", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(3));
        assertAtomType(testedAtomTypes, "H1", matched);
    }

    /*
     * Tests P4, S2, Cl1
     */
    @Test
    public void testP4() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom p = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "P");
        IAtom cl1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "Cl");
        IAtom cl2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "Cl");
        IAtom cl3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "Cl");
        IAtom s = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "S");

        IBond bond1 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, p, cl1, IBond.Order.SINGLE);
        IBond bond2 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, p, cl2, IBond.Order.SINGLE);
        IBond bond3 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, p, cl3, IBond.Order.SINGLE);
        IBond bond4 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, p, s, IBond.Order.DOUBLE);

        mol.addAtom(p);
        mol.addAtom(cl1);
        mol.addAtom(cl2);
        mol.addAtom(cl3);
        mol.addAtom(s);

        mol.addBond(bond1);
        mol.addBond(bond2);
        mol.addBond(bond3);
        mol.addBond(bond4);

        StructGenMatcher matcher = new StructGenMatcher();
        IAtomType matched;

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "P4", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(4));
        assertAtomType(testedAtomTypes, "S2", matched);

        for (int i = 1; i < 4; i++) {
            matched = matcher.findMatchingAtomType(mol, mol.getAtom(i));
            assertAtomType(testedAtomTypes, "atom " + i + " failed to match", "Cl1", matched);
        }
    }

    /*
     * Tests P3, O2, C4
     */
    @Test
    public void testP3() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom p = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "P");
        IAtom o1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "O");
        IAtom o2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "O");
        IAtom o3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "O");
        IAtom c1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        IAtom c2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        IAtom c3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");

        c1.setImplicitHydrogenCount(3);
        c2.setImplicitHydrogenCount(3);
        c3.setImplicitHydrogenCount(3);

        IBond bond1 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, p, o1, IBond.Order.SINGLE);
        IBond bond2 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, p, o2, IBond.Order.SINGLE);
        IBond bond3 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, p, o3, IBond.Order.SINGLE);
        IBond bond4 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, c1, o1, IBond.Order.SINGLE);
        IBond bond5 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, c2, o2, IBond.Order.SINGLE);
        IBond bond6 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, c3, o3, IBond.Order.SINGLE);

        mol.addAtom(p);
        mol.addAtom(o1);
        mol.addAtom(o2);
        mol.addAtom(o3);
        mol.addAtom(c1);
        mol.addAtom(c2);
        mol.addAtom(c3);

        mol.addBond(bond1);
        mol.addBond(bond2);
        mol.addBond(bond3);
        mol.addBond(bond4);
        mol.addBond(bond5);
        mol.addBond(bond6);

        StructGenMatcher matcher = new StructGenMatcher();
        IAtomType matched;

        String[] atomTypes = {"P3", "O2", "O2", "O2", "C4", "C4", "C4"};
        for (int i = 0; i < mol.getAtomCount(); i++) {
            matched = matcher.findMatchingAtomType(mol, mol.getAtom(i));
            assertAtomType(testedAtomTypes, atomTypes[i], matched);
        }
    }

    /* Test Na1, Cl1 */
    @Test
    public void testNa1() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom na = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "Na");
        IAtom cl = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "Cl");
        IBond bond = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, na, cl, IBond.Order.SINGLE);
        mol.addAtom(na);
        mol.addAtom(cl);
        mol.addBond(bond);

        StructGenMatcher matcher = new StructGenMatcher();
        IAtomType matched;

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "Na1", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(1));
        assertAtomType(testedAtomTypes, "Cl1", matched);
    }

    /* Test Si4, C4, Cl1 */
    @Test
    public void testSi4() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom si = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "Si");
        IAtom c1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        IAtom cl1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "Cl");
        IAtom cl2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "Cl");
        IAtom cl3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "Cl");

        c1.setImplicitHydrogenCount(3);

        IBond bond1 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, si, c1, IBond.Order.SINGLE);
        IBond bond2 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, si, cl1, IBond.Order.SINGLE);
        IBond bond3 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, si, cl2, IBond.Order.SINGLE);
        IBond bond4 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, si, cl3, IBond.Order.SINGLE);

        mol.addAtom(si);
        mol.addAtom(c1);
        mol.addAtom(cl1);
        mol.addAtom(cl2);
        mol.addAtom(cl3);

        mol.addBond(bond1);
        mol.addBond(bond2);
        mol.addBond(bond3);
        mol.addBond(bond4);

        StructGenMatcher matcher = new StructGenMatcher();
        IAtomType matched;

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "Si4", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(1));
        assertAtomType(testedAtomTypes, "C4", matched);

        for (int i = 3; i < mol.getAtomCount(); i++) {
            matched = matcher.findMatchingAtomType(mol, mol.getAtom(i));
            assertAtomType(testedAtomTypes, "atom " + i + " failed to match", "Cl1", matched);
        }
    }

    /* Tests S2, H1 */
    @Test
    public void testS2() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom s = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "S");
        s.setImplicitHydrogenCount(2);

        mol.addAtom(s);

        StructGenMatcher matcher = new StructGenMatcher();
        IAtomType matched;

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "S2", matched);

        mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        s = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "S");
        IAtom h1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "H");
        IAtom h2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "H");
        IBond b1 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, s, h1, IBond.Order.SINGLE);
        IBond b2 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, s, h2, IBond.Order.SINGLE);

        mol.addAtom(s);
        mol.addAtom(h1);
        mol.addAtom(h2);

        mol.addBond(b1);
        mol.addBond(b2);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "S2", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(1));
        assertAtomType(testedAtomTypes, "H1", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(2));
        assertAtomType(testedAtomTypes, "H1", matched);
    }

    /* Tests S3, O2 */
    @Test
    public void testS3() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom s = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "S");
        IAtom o1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "O");
        IAtom o2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "O");

        IBond b1 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, s, o1, IBond.Order.DOUBLE);
        IBond b2 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, s, o2, IBond.Order.DOUBLE);

        mol.addAtom(s);
        mol.addAtom(o1);
        mol.addAtom(o2);

        mol.addBond(b1);
        mol.addBond(b2);

        StructGenMatcher matcher = new StructGenMatcher();
        IAtomType matched;

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "S3", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(1));
        assertAtomType(testedAtomTypes, "O2", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(2));
        assertAtomType(testedAtomTypes, "O2", matched);
    }

    /* Tests S4, Cl1 */
    @Test
    public void testS4() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom s = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "S");
        mol.addAtom(s);
        for (int i = 0; i < 6; i++) {
            IAtom f = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "F");
            mol.addAtom(f);
            IBond bond = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, s, f, IBond.Order.SINGLE);
            mol.addBond(bond);
        }

        StructGenMatcher matcher = new StructGenMatcher();
        IAtomType matched;

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "S4", matched);

        for (int i = 1; i < mol.getAtomCount(); i++) {
            matched = matcher.findMatchingAtomType(mol, mol.getAtom(i));
            assertAtomType(testedAtomTypes, "atom " + i + " failed to match", "F1", matched);
        }
    }

    /* Tests S4, O2 */
    @Test
    public void testS4oxide() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom s = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "S");
        IAtom o1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "O");
        IAtom o2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "O");
        IAtom o3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "O");

        IBond b1 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, s, o1, IBond.Order.DOUBLE);
        IBond b2 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, s, o2, IBond.Order.DOUBLE);
        IBond b3 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, s, o3, IBond.Order.DOUBLE);

        mol.addAtom(s);
        mol.addAtom(o1);
        mol.addAtom(o2);
        mol.addAtom(o3);

        mol.addBond(b1);
        mol.addBond(b2);
        mol.addBond(b3);

        StructGenMatcher matcher = new StructGenMatcher();
        IAtomType matched;

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "S4", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(1));
        assertAtomType(testedAtomTypes, "O2", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(2));
        assertAtomType(testedAtomTypes, "O2", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(3));
        assertAtomType(testedAtomTypes, "O2", matched);
    }

    /* Tests N3, O2 */
    @Test
    public void testN3acid() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom n = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "N");
        IAtom o = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "O");
        IAtom h = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "H");

        IBond b1 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, n, o, IBond.Order.DOUBLE);
        IBond b2 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, n, h, IBond.Order.SINGLE);

        mol.addAtom(n);
        mol.addAtom(o);
        mol.addAtom(h);

        mol.addBond(b1);
        mol.addBond(b2);

        StructGenMatcher matcher = new StructGenMatcher();
        IAtomType matched;

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "N3", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(1));
        assertAtomType(testedAtomTypes, "O2", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(2));
        assertAtomType(testedAtomTypes, "H1", matched);
    }

    @Test
    public void testN3cyanide() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom n = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "N");
        IAtom c1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        IAtom c2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");

        c1.setImplicitHydrogenCount(0);
        c2.setImplicitHydrogenCount(3);

        IBond b1 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, n, c1, IBond.Order.TRIPLE);
        IBond b2 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, c1, c2, IBond.Order.SINGLE);

        mol.addAtom(n);
        mol.addAtom(c1);
        mol.addAtom(c2);

        mol.addBond(b1);
        mol.addBond(b2);

        StructGenMatcher matcher = new StructGenMatcher();
        IAtomType matched;

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "N3", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(1));
        assertAtomType(testedAtomTypes, "C4", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(2));
        assertAtomType(testedAtomTypes, "C4", matched);
    }

    /* Tests N5, O2, C4 */
    @Test
    public void testN5() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom n = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "N");
        IAtom o1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "O");
        IAtom o2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "O");
        IAtom c = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");

        c.setImplicitHydrogenCount(3);

        IBond b1 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, n, o1, IBond.Order.DOUBLE);
        IBond b2 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, n, o2, IBond.Order.DOUBLE);
        IBond b3 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, n, c, IBond.Order.SINGLE);

        mol.addAtom(n);
        mol.addAtom(o1);
        mol.addAtom(o2);
        mol.addAtom(c);

        mol.addBond(b1);
        mol.addBond(b2);
        mol.addBond(b3);

        StructGenMatcher matcher = new StructGenMatcher();
        IAtomType matched;

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "N5", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(1));
        assertAtomType(testedAtomTypes, "O2", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(2));
        assertAtomType(testedAtomTypes, "O2", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(3));
        assertAtomType(testedAtomTypes, "C4", matched);
    }

    /* Test B3, F1 */
    @Test
    public void testB3() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom b = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "B");
        IAtom f1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "F");
        IAtom f2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "F");
        IAtom f3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "F");

        IBond b1 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, b, f1, IBond.Order.SINGLE);
        IBond b2 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, b, f2, IBond.Order.SINGLE);
        IBond b3 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, b, f3, IBond.Order.SINGLE);

        mol.addAtom(b);
        mol.addAtom(f1);
        mol.addAtom(f2);
        mol.addAtom(f3);
        mol.addBond(b1);
        mol.addBond(b2);
        mol.addBond(b3);

        StructGenMatcher matcher = new StructGenMatcher();
        IAtomType matched;

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "B3", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(1));
        assertAtomType(testedAtomTypes, "F1", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(2));
        assertAtomType(testedAtomTypes, "F1", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(3));
        assertAtomType(testedAtomTypes, "F1", matched);
    }

    @Test
    public void testSe2() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom se = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "Se");
        IAtom o = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "O");
        IBond b1 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, se, o, IBond.Order.DOUBLE);
        mol.addAtom(se);
        mol.addAtom(o);
        mol.addBond(b1);

        StructGenMatcher matcher = new StructGenMatcher();
        IAtomType matched;

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "Se2", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(1));
        assertAtomType(testedAtomTypes, "O2", matched);

    }

    /**
     * The test seems to be run by JUnit in order in which they found
     * in the source. Ugly, but @AfterClass does not work because that
     * methods does cannot assert anything.
     *
     * ...not anymore. Bad idea to do have such a test in the first place
     * but we can hack it by sorting by test name (see fix method order
     * annotation).
     */
    @Test
    public void utestCountTestedAtomTypes() {
        AtomTypeFactory factory = AtomTypeFactory.getInstance(
                "org/openscience/cdk/config/data/structgen_atomtypes.xml", SilentChemObjectBuilder.getInstance());

        IAtomType[] expectedTypes = factory.getAllAtomTypes();
        if (expectedTypes.length != testedAtomTypes.size()) {
            String errorMessage = "Atom types not tested:";
            for (IAtomType expectedType : expectedTypes) {
                if (!testedAtomTypes.containsKey(expectedType.getAtomTypeName()))
                    errorMessage += " " + expectedType.getAtomTypeName();
            }
            Assert.assertEquals(errorMessage, factory.getAllAtomTypes().length, testedAtomTypes.size());
        }
    }

}
