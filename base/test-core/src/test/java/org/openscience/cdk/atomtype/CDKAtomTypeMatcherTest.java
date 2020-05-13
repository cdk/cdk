/* Copyright (C) 2007-2011  Egon Willighagen <egonw@users.sf.net>
 *               2007       Rajarshi Guha
 *                    2011  Nimish Gopal <nimishg@ebi.ac.uk>
 *                    2011  Syed Asad Rahman <asad@ebi.ac.uk>
 *                    2011  Gilleain Torrance <gilleain.torrance@gmail.com>
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.Ring;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IAtomType.Hybridization;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.AtomType;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;
import org.openscience.cdk.tools.periodictable.PeriodicTable;

import static org.hamcrest.CoreMatchers.is;

/**
 * This class tests the matching of atom types defined in the
 * CDK atom type list. All tests in this class <b>must</b> use
 * explicit {@link IAtomContainer}s; test using data files
 * must be placed in {@link CDKAtomTypeMatcherFilesTest}.
 *
 * @cdk.module test-core
 */
public class CDKAtomTypeMatcherTest extends AbstractCDKAtomTypeTest {

    private static Map<String, Integer> testedAtomTypes = new HashMap<String, Integer>();

    @Test
    public void testGetInstance_IChemObjectBuilder() throws Exception {
        CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(DefaultChemObjectBuilder.getInstance());
        Assert.assertNotNull(matcher);
    }

    @Test
    public void testGetInstance_IChemObjectBuilder_int() throws Exception {
        CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(DefaultChemObjectBuilder.getInstance(),
                CDKAtomTypeMatcher.REQUIRE_EXPLICIT_HYDROGENS);
        Assert.assertNotNull(matcher);
    }

    @Test
    public void testFindMatchingAtomType_IAtomContainer_IAtom() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("C");
        final IAtomType.Hybridization thisHybridization = IAtomType.Hybridization.SP3;
        atom.setHybridization(thisHybridization);
        mol.addAtom(atom);

        String[] expectedTypes = {"C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testFindMatchingAtomType_IAtomContainer() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("C");
        final IAtomType.Hybridization thisHybridization = IAtomType.Hybridization.SP3;
        atom.setHybridization(thisHybridization);
        mol.addAtom(atom);

        // just check consistency; other methods do perception testing
        CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(DefaultChemObjectBuilder.getInstance());
        IAtomType[] types = matcher.findMatchingAtomTypes(mol);
        for (int i = 0; i < types.length; i++) {
            IAtomType type = matcher.findMatchingAtomType(mol, mol.getAtom(i));
            Assert.assertEquals(type.getAtomTypeName(), types[i].getAtomTypeName());
        }
    }

    @Test
    public void testDummy() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new PseudoAtom("R");
        mol.addAtom(atom);

        String[] expectedTypes = {"X"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * @cdk.bug 2445178
     */
    @Test
    public void testNonExistingType() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom();
        mol.addAtom(atom);
        CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(DefaultChemObjectBuilder.getInstance());
        IAtomType type = matcher.findMatchingAtomType(mol, atom);
        Assert.assertNotNull(type);
        org.hamcrest.MatcherAssert.assertThat(type.getAtomTypeName(), is("X"));
    }

    @Test
    public void testEthene() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0, 1, Order.DOUBLE);

        String[] expectedTypes = {"C.sp2", "C.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testEthyneKation() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("C");
        atom2.setFormalCharge(+1);
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0, 1, Order.TRIPLE);

        String[] expectedTypes = {"C.sp", "C.plus.sp1"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testEthyneRadical() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addSingleElectron(1);
        mol.addBond(0, 1, Order.TRIPLE);

        String[] expectedTypes = {"C.sp", "C.radical.sp1"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testImine() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("N");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0, 1, Order.DOUBLE);

        String[] expectedTypes = {"C.sp2", "N.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testImineRadical() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("N");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addSingleElectron(1);
        mol.addBond(0, 1, Order.DOUBLE);

        String[] expectedTypes = {"C.sp2", "N.sp2.radical"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testEtheneRadical() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addSingleElectron(1);
        mol.addBond(0, 1, Order.DOUBLE);

        String[] expectedTypes = {"C.sp2", "C.radical.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testGuanineMethyl() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom1 = new Atom("C");
        IAtom atom2 = new Atom("N");
        IAtom atom3 = new Atom("C");
        IAtom atom4 = new Atom("N");
        IAtom atom5 = new Atom("C");
        IAtom atom6 = new Atom("C");
        IAtom atom7 = new Atom("N");
        IAtom atom8 = new Atom("C");
        IAtom atom9 = new Atom("C");
        IAtom atom10 = new Atom("N");
        IAtom atom11 = new Atom("O");
        IAtom atom12 = new Atom("N");
        mol.addAtom(atom1);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addAtom(atom5);
        mol.addAtom(atom6);
        mol.addAtom(atom7);
        mol.addAtom(atom8);
        mol.addAtom(atom9);
        mol.addAtom(atom10);
        mol.addAtom(atom11);
        mol.addAtom(atom12);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(0, 2, Order.DOUBLE);
        mol.addBond(0, 3, Order.SINGLE);
        mol.addBond(4, 1, Order.SINGLE);
        mol.addBond(1, 5, Order.SINGLE);
        mol.addBond(2, 6, Order.SINGLE);
        mol.addBond(2, 7, Order.SINGLE);
        mol.addBond(3, 8, Order.DOUBLE);
        mol.addBond(5, 6, Order.DOUBLE);
        mol.addBond(7, 9, Order.SINGLE);
        mol.addBond(7, 10, Order.DOUBLE);
        mol.addBond(8, 11, Order.SINGLE);
        mol.addBond(8, 9, Order.SINGLE);

        String[] expectedTypes = {"C.sp2", "N.planar3", "C.sp2", "N.sp2", "C.sp3", "C.sp2", "N.sp2", "C.sp2", "C.sp2",
                "N.amide", "O.sp2", "N.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testPropyne() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0, 1, Order.TRIPLE);
        mol.addBond(2, 1, Order.SINGLE);

        String[] expectedTypes = {"C.sp", "C.sp", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testFormaldehyde() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0, 1, Order.DOUBLE);

        String[] expectedTypes = {"O.sp2", "C.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testCarboxylate() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("O");
        IAtom atom1 = new Atom("C");
        IAtom atom2 = new Atom("O");
        atom2.setFormalCharge(-1);
        IAtom atom3 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom1);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0, 1, Order.DOUBLE);
        mol.addBond(1, 2, Order.SINGLE);
        mol.addBond(1, 3, Order.SINGLE);

        String[] expectedTypes = {"O.sp2.co2", "C.sp2", "O.minus.co2", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testFormaldehydeRadicalKation() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("O");
        atom.setFormalCharge(+1);
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0, 1, Order.DOUBLE);
        mol.addSingleElectron(0);

        String[] expectedTypes = {"O.plus.sp2.radical", "C.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * Aim of this test is to see if the atom type matcher is OK with
     * partial filled implicit hydrogen counts.
     */
    @Test
    public void testPartialMethane() throws Exception {
        IAtomContainer methane = new AtomContainer();
        IAtom carbon = new Atom("C");
        methane.addAtom(carbon);

        String[] expectedTypes = {"C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, methane);

        carbon.setImplicitHydrogenCount(1);
        assertAtomTypes(testedAtomTypes, expectedTypes, methane);

        carbon.setImplicitHydrogenCount(2);
        assertAtomTypes(testedAtomTypes, expectedTypes, methane);

        carbon.setImplicitHydrogenCount(3);
        assertAtomTypes(testedAtomTypes, expectedTypes, methane);

        carbon.setImplicitHydrogenCount(4);
        assertAtomTypes(testedAtomTypes, expectedTypes, methane);
    }

    @Test
    public void testMethanol() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0, 1, Order.SINGLE);

        String[] expectedTypes = {"O.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testLithiumMethanoxide() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("Li");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(0, 2, Order.SINGLE);

        String[] expectedTypes = {"O.sp3", "C.sp3", "Li"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testHCN() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("N");
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0, 1, Order.TRIPLE);

        String[] expectedTypes = {"N.sp1", "C.sp"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testHNO2() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("N");
        IAtom atom2 = new Atom("O");
        IAtom atom3 = new Atom("O");
        IAtom atom4 = new Atom("H");
        mol.addAtom(atom);
        atom.setFormalCharge(+1);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        atom3.setFormalCharge(-1);
        mol.addAtom(atom4);
        mol.addBond(0, 1, Order.DOUBLE);
        mol.addBond(0, 2, Order.SINGLE);
        mol.addBond(0, 3, Order.SINGLE);

        String[] expectedTypes = {"N.plus.sp2", "O.sp2", "O.minus", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testNitromethane() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("N");
        IAtom atom2 = new Atom("O");
        IAtom atom3 = new Atom("O");
        IAtom atom4 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addBond(0, 1, Order.DOUBLE);
        mol.addBond(0, 2, Order.DOUBLE);
        mol.addBond(0, 3, Order.SINGLE);

        String[] expectedTypes = {"N.nitro", "O.sp2", "O.sp2", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testMethylAmine() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("N");
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0, 1, Order.SINGLE);

        String[] expectedTypes = {"N.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testMethylAmineRadical() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("N");
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addSingleElectron(0);
        atom.setFormalCharge(+1);
        mol.addBond(0, 1, Order.SINGLE);

        String[] expectedTypes = {"N.plus.sp3.radical", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testMethyleneImine() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("N");
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0, 1, Order.DOUBLE);

        String[] expectedTypes = {"N.sp2", "C.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testEthene_withHybridInfo() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("C");
        final IAtomType.Hybridization thisHybridization = IAtomType.Hybridization.SP2;
        atom.setHybridization(thisHybridization);
        atom2.setHybridization(thisHybridization);
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0, 1, Order.DOUBLE);

        String[] expectedTypes = {"C.sp2", "C.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testPiperidine() throws Exception {
        IAtomContainer molecule = TestMoleculeFactory.makePiperidine();
        String[] expectedTypes = {"N.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
    }

    @Test
    public void testTetrahydropyran() throws Exception {
        IAtomContainer molecule = TestMoleculeFactory.makeTetrahydropyran();
        String[] expectedTypes = {"O.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
    }

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

        String[] expectedTypes = {"S.oxide", "O.sp2", "O.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testH2S() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom s = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "S");
        IAtom h1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "H");
        IAtom h2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "H");

        IBond b1 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, s, h1, IBond.Order.SINGLE);
        IBond b2 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, s, h2, IBond.Order.SINGLE);

        mol.addAtom(s);
        mol.addAtom(h1);
        mol.addAtom(h2);

        mol.addBond(b1);
        mol.addBond(b2);

        String[] expectedTypes = {"S.3", "H", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * @cdk.inchi InChI=1/H2Se/h1H2
     */
    @Test
    public void testH2Se() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom se = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "Se");
        IAtom h1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "H");
        IAtom h2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "H");

        IBond b1 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, se, h1, IBond.Order.SINGLE);
        IBond b2 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, se, h2, IBond.Order.SINGLE);

        mol.addAtom(se);
        mol.addAtom(h1);
        mol.addAtom(h2);

        mol.addBond(b1);
        mol.addBond(b2);

        String[] expectedTypes = {"Se.3", "H", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * @cdk.inchi InChI=1/H2Se/h1H2
     */
    @Test
    public void testH2Se_oneImplH() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom se = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "Se");
        se.setImplicitHydrogenCount(1);
        IAtom h1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "H");

        IBond b1 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, se, h1, IBond.Order.SINGLE);

        mol.addAtom(se);
        mol.addAtom(h1);
        mol.addBond(b1);

        String[] expectedTypes = {"Se.3", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * @cdk.inchi InChI=1/H2Se/h1H2
     */
    @Test
    public void testH2Se_twoImplH() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom se = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "Se");
        se.setImplicitHydrogenCount(2);
        mol.addAtom(se);

        String[] expectedTypes = {"Se.3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testSelenide() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom se = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "Se");
        se.setImplicitHydrogenCount(0);
        se.setFormalCharge(-2);
        mol.addAtom(se);

        String[] expectedTypes = {"Se.2minus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testH2S_Hybridization() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom s = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "S");
        s.setHybridization(Hybridization.SP3);
        mol.addAtom(s);
        String[] expectedTypes = {"S.3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testHS() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom s = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "S");
        s.setFormalCharge(-1);
        IAtom h1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "H");

        IBond b1 = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, s, h1, IBond.Order.SINGLE);

        mol.addAtom(s);
        mol.addAtom(h1);
        mol.addBond(b1);

        String[] expectedTypes = {"S.minus", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testDMSOCharged() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("O");
        atom.setFormalCharge(-1);
        IAtom atom2 = new Atom("S");
        atom2.setFormalCharge(1);
        IAtom atom3 = new Atom("C");
        IAtom atom4 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(1, 2, Order.SINGLE);
        mol.addBond(1, 3, Order.SINGLE);

        String[] expectedTypes = {"O.minus", "S.inyl.charged", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testDMSO() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("S");
        IAtom atom3 = new Atom("C");
        IAtom atom4 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addBond(0, 1, Order.DOUBLE);
        mol.addBond(1, 2, Order.SINGLE);
        mol.addBond(1, 3, Order.SINGLE);

        String[] expectedTypes = {"O.sp2", "S.inyl", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testDMSOO() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("O");
        IAtom atom1 = new Atom("O");
        IAtom atom2 = new Atom("S");
        IAtom atom3 = new Atom("C");
        IAtom atom4 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom1);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addBond(0, 2, Order.DOUBLE);
        mol.addBond(1, 2, Order.DOUBLE);
        mol.addBond(2, 3, Order.SINGLE);
        mol.addBond(2, 4, Order.SINGLE);

        String[] expectedTypes = {"O.sp2", "O.sp2", "S.onyl", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testStrioxide() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("O");
        IAtom atom1 = new Atom("O");
        IAtom atom2 = new Atom("S");
        IAtom atom3 = new Atom("O");
        mol.addAtom(atom);
        mol.addAtom(atom1);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0, 2, Order.DOUBLE);
        mol.addBond(1, 2, Order.DOUBLE);
        mol.addBond(2, 3, Order.DOUBLE);

        String[] expectedTypes = {"O.sp2", "O.sp2", "S.trioxide", "O.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testAmide() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("N");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0, 1, Order.DOUBLE);
        mol.addBond(1, 2, Order.SINGLE);

        String[] expectedTypes = {"O.sp2", "C.sp2", "N.amide"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testAmineOxide() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("N");
        IAtom atom3 = new Atom("C");
        IAtom atom4 = new Atom("C");
        IAtom atom5 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addAtom(atom5);
        mol.addBond(0, 1, Order.DOUBLE);
        mol.addBond(1, 2, Order.SINGLE);
        mol.addBond(1, 3, Order.SINGLE);
        mol.addBond(1, 4, Order.SINGLE);

        String[] expectedTypes = {"O.sp2", "N.oxide", "C.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testThioAmide() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("S");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("N");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0, 1, Order.DOUBLE);
        mol.addBond(1, 2, Order.SINGLE);

        String[] expectedTypes = {"S.2", "C.sp2", "N.thioamide"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testAdenine() throws Exception {
        IAtomContainer mol = TestMoleculeFactory.makeAdenine();
        String[] expectedTypes = {"C.sp2", "C.sp2", "C.sp2", "N.sp2", "N.sp2", "N.planar3", "N.sp2", "N.sp3", "C.sp2",
                "C.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testAmide2() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("O");
        IAtom atom1 = new Atom("C");
        IAtom atom2 = new Atom("N");
        IAtom atom3 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom1);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0, 1, Order.DOUBLE);
        mol.addBond(1, 2, Order.SINGLE);
        mol.addBond(2, 3, Order.SINGLE);

        String[] expectedTypes = {"O.sp2", "C.sp2", "N.amide", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testAmide3() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("O");
        IAtom atom1 = new Atom("C");
        IAtom atom2 = new Atom("N");
        IAtom atom3 = new Atom("C");
        IAtom atom4 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom1);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addBond(0, 1, Order.DOUBLE);
        mol.addBond(1, 2, Order.SINGLE);
        mol.addBond(2, 3, Order.SINGLE);
        mol.addBond(3, 4, Order.SINGLE);

        String[] expectedTypes = {"O.sp2", "C.sp2", "N.amide", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testLactam() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("O");
        IAtom atom1 = new Atom("C");
        IAtom atom2 = new Atom("N");
        IAtom atom3 = new Atom("C");
        IAtom atom4 = new Atom("C");
        IAtom atom5 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom1);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addAtom(atom5);
        mol.addBond(0, 1, Order.DOUBLE);
        mol.addBond(1, 2, Order.SINGLE);
        mol.addBond(2, 3, Order.SINGLE);
        mol.addBond(3, 4, Order.SINGLE);
        mol.addBond(4, 5, Order.SINGLE);
        mol.addBond(5, 1, Order.SINGLE);

        String[] expectedTypes = {"O.sp2", "C.sp2", "N.amide", "C.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testThioAcetone() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("S");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("C");
        IAtom atom4 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addBond(0, 1, Order.DOUBLE);
        mol.addBond(1, 2, Order.SINGLE);
        mol.addBond(1, 3, Order.SINGLE);

        String[] expectedTypes = {"S.2", "C.sp2", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testSulphuricAcid() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("S");
        IAtom atom3 = new Atom("O");
        IAtom atom4 = new Atom("O");
        IAtom atom5 = new Atom("O");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addAtom(atom5);
        mol.addBond(0, 1, Order.DOUBLE);
        mol.addBond(1, 2, Order.DOUBLE);
        mol.addBond(1, 3, Order.SINGLE);
        mol.addBond(1, 4, Order.SINGLE);

        String[] expectedTypes = {"O.sp2", "S.onyl", "O.sp2", "O.sp3", "O.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * @cdk.inchi InChI=1S/CH4O2S2/c1-5(2,3)4/h1H3,(H,2,3,4)
     */
    @Test
    public void testThioSulphonate() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "S");
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "S");
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "O");
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "O");
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a5);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.DOUBLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a4, IBond.Order.DOUBLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a1, a5, IBond.Order.SINGLE);
        mol.addBond(b4);

        String[] expectedTypes = {"S.thionyl", "S.2", "O.sp3", "O.sp2", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testSulphuricAcid_Charged() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("S");
        IAtom atom3 = new Atom("O");
        IAtom atom4 = new Atom("O");
        IAtom atom5 = new Atom("O");
        mol.addAtom(atom);
        atom.setFormalCharge(-1);
        mol.addAtom(atom2);
        atom2.setFormalCharge(+2);
        mol.addAtom(atom3);
        atom3.setFormalCharge(-1);
        mol.addAtom(atom4);
        mol.addAtom(atom5);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(1, 2, Order.SINGLE);
        mol.addBond(1, 3, Order.SINGLE);
        mol.addBond(1, 4, Order.SINGLE);

        String[] expectedTypes = {"O.minus", "S.onyl.charged", "O.minus", "O.sp3", "O.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testSF6() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("F");
        IAtom atom2 = new Atom("S");
        IAtom atom3 = new Atom("F");
        IAtom atom4 = new Atom("F");
        IAtom atom5 = new Atom("F");
        IAtom atom6 = new Atom("F");
        IAtom atom7 = new Atom("F");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addAtom(atom5);
        mol.addAtom(atom6);
        mol.addAtom(atom7);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(1, 2, Order.SINGLE);
        mol.addBond(1, 3, Order.SINGLE);
        mol.addBond(1, 4, Order.SINGLE);
        mol.addBond(1, 5, Order.SINGLE);
        mol.addBond(1, 6, Order.SINGLE);

        String[] expectedTypes = {"F", "S.octahedral", "F", "F", "F", "F", "F"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testMnF4() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("F");
        IAtom atom2 = new Atom("Mn");
        IAtom atom3 = new Atom("F");
        IAtom atom4 = new Atom("F");
        IAtom atom5 = new Atom("F");
        mol.addAtom(atom);
        atom.setFormalCharge(-1);
        mol.addAtom(atom2);
        atom2.setFormalCharge(+2);
        mol.addAtom(atom3);
        atom3.setFormalCharge(-1);
        mol.addAtom(atom4);
        atom4.setFormalCharge(-1);
        mol.addAtom(atom5);
        atom5.setFormalCharge(-1);

        String[] expectedTypes = {"F.minus", "Mn.2plus", "F.minus", "F.minus", "F.minus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testCrF6() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("F");
        IAtom atom2 = new Atom("Cr");
        IAtom atom3 = new Atom("F");
        IAtom atom4 = new Atom("F");
        IAtom atom5 = new Atom("F");
        IAtom atom6 = new Atom("F");
        IAtom atom7 = new Atom("F");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addAtom(atom5);
        mol.addAtom(atom6);
        mol.addAtom(atom7);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(1, 2, Order.SINGLE);
        mol.addBond(1, 3, Order.SINGLE);
        mol.addBond(1, 4, Order.SINGLE);
        mol.addBond(1, 5, Order.SINGLE);
        mol.addBond(1, 6, Order.SINGLE);

        String[] expectedTypes = {"F", "Cr", "F", "F", "F", "F", "F"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testXeF4() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("F");
        IAtom atom2 = new Atom("Xe");
        IAtom atom3 = new Atom("F");
        IAtom atom4 = new Atom("F");
        IAtom atom5 = new Atom("F");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addAtom(atom5);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(1, 2, Order.SINGLE);
        mol.addBond(1, 3, Order.SINGLE);
        mol.addBond(1, 4, Order.SINGLE);

        String[] expectedTypes = {"F", "Xe.3", "F", "F", "F"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testPhosphate() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("P");
        IAtom atom3 = new Atom("O");
        IAtom atom4 = new Atom("O");
        IAtom atom5 = new Atom("O");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addAtom(atom5);
        mol.addBond(0, 1, Order.DOUBLE);
        mol.addBond(1, 2, Order.SINGLE);
        mol.addBond(1, 3, Order.SINGLE);
        mol.addBond(1, 4, Order.SINGLE);

        String[] expectedTypes = {"O.sp2", "P.ate", "O.sp3", "O.sp3", "O.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * @cdk.inchi InChI=1/C3H10OP/c1-5(2,3)4/h4H,1-3H3/q+1
     */
    @Test
    public void testHydroxyTriMethylPhophanium() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("P");
        atom2.setFormalCharge(+1);
        IAtom atom3 = new Atom("C");
        IAtom atom4 = new Atom("C");
        IAtom atom5 = new Atom("O");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addAtom(atom5);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(1, 2, Order.SINGLE);
        mol.addBond(1, 3, Order.SINGLE);
        mol.addBond(1, 4, Order.SINGLE);

        String[] expectedTypes = {"C.sp3", "P.ate.charged", "C.sp3", "C.sp3", "O.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testPhosphateCharged() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("O");
        atom.setFormalCharge(-1);
        IAtom atom2 = new Atom("P");
        atom2.setFormalCharge(1);
        IAtom atom3 = new Atom("O");
        IAtom atom4 = new Atom("O");
        IAtom atom5 = new Atom("O");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addAtom(atom5);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(1, 2, Order.SINGLE);
        mol.addBond(1, 3, Order.SINGLE);
        mol.addBond(1, 4, Order.SINGLE);

        String[] expectedTypes = {"O.minus", "P.ate.charged", "O.sp3", "O.sp3", "O.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testPhosphorusTriradical() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("P");
        mol.addAtom(atom);
        mol.addSingleElectron(0);
        mol.addSingleElectron(0);
        mol.addSingleElectron(0);

        String[] expectedTypes = {"P.se.3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testAmmonia() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("H");
        IAtom atom2 = new Atom("N");
        IAtom atom3 = new Atom("H");
        IAtom atom4 = new Atom("H");
        IAtom atom5 = new Atom("H");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        atom2.setFormalCharge(+1);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addAtom(atom5);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(1, 2, Order.SINGLE);
        mol.addBond(1, 3, Order.SINGLE);
        mol.addBond(1, 4, Order.SINGLE);

        String[] expectedTypes = {"H", "N.plus", "H", "H", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testNitrogenRadical() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("H");
        IAtom atom2 = new Atom("N");
        IAtom atom3 = new Atom("H");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addSingleElectron(1);
        mol.addAtom(atom3);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(1, 2, Order.SINGLE);

        String[] expectedTypes = {"H", "N.sp3.radical", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testTMS() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("Si");
        IAtom atom3 = new Atom("C");
        IAtom atom4 = new Atom("C");
        IAtom atom5 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addAtom(atom5);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(1, 2, Order.SINGLE);
        mol.addBond(1, 3, Order.SINGLE);
        mol.addBond(1, 4, Order.SINGLE);

        String[] expectedTypes = {"C.sp3", "Si.sp3", "C.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testTinCompound() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("Sn");
        IAtom atom3 = new Atom("C");
        IAtom atom4 = new Atom("C");
        IAtom atom5 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addAtom(atom5);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(1, 2, Order.SINGLE);
        mol.addBond(1, 3, Order.SINGLE);
        mol.addBond(1, 4, Order.SINGLE);

        String[] expectedTypes = {"C.sp3", "Sn.sp3", "C.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testArsenicPlus() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("As");
        atom2.setFormalCharge(+1);
        IAtom atom3 = new Atom("C");
        IAtom atom4 = new Atom("C");
        IAtom atom5 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addAtom(atom5);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(1, 2, Order.SINGLE);
        mol.addBond(1, 3, Order.SINGLE);
        mol.addBond(1, 4, Order.SINGLE);

        String[] expectedTypes = {"C.sp3", "As.plus", "C.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testPhosphine() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("H");
        IAtom atom2 = new Atom("P");
        IAtom atom3 = new Atom("H");
        IAtom atom4 = new Atom("H");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(1, 2, Order.SINGLE);
        mol.addBond(1, 3, Order.SINGLE);

        String[] expectedTypes = {"H", "P.ine", "H", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * @cdk.inchi InChI=1S/HO3P/c1-4(2)3/h(H-,1,2,3)/p+1
     */
    @Test
    public void testPhosphorousAcid() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IChemObjectBuilder builder = mol.getBuilder();
        IAtom a1 = builder.newInstance(IAtom.class, "P");
        a1.setFormalCharge(1);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "O");
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "O");
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "O");
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "H");
        mol.addAtom(a5);
        IAtom a6 = builder.newInstance(IAtom.class, "H");
        mol.addAtom(a6);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a4, IBond.Order.DOUBLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a2, a5, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = builder.newInstance(IBond.class, a3, a6, IBond.Order.SINGLE);
        mol.addBond(b5);

        String[] expectedTypes = {"P.anium", "O.sp3", "O.sp3", "O.sp2", "H", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testDiethylPhosphine() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("P");
        IAtom atom3 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(1, 2, Order.SINGLE);

        String[] expectedTypes = {"C.sp3", "P.ine", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * @cdk.inchi InChI=1/C2H5P/c1-3-2/h1H2,2H3
     */
    @Test
    public void testPhosphorCompound() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("P");
        IAtom atom3 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0, 1, Order.DOUBLE);
        mol.addBond(1, 2, Order.SINGLE);

        String[] expectedTypes = {"C.sp2", "P.irane", "C.sp3"}; // FIXME: compare with previous test... can't both be P.ine...
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testCarbokation() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("H");
        IAtom atom2 = new Atom("C");
        atom2.setFormalCharge(+1);
        IAtom atom3 = new Atom("H");
        IAtom atom4 = new Atom("H");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(1, 2, Order.SINGLE);
        mol.addBond(1, 3, Order.SINGLE);

        String[] expectedTypes = {"H", "C.plus.planar", "H", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testCarbokation_implicitHydrogen() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom2 = new Atom("C");
        atom2.setFormalCharge(+1);
        mol.addAtom(atom2);

        String[] expectedTypes = {"C.plus.sp2"}; // FIXME: compare with previous test... same compound!
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testHydrogen() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("H");
        mol.addAtom(atom);

        String[] expectedTypes = {"H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testHydroxyl() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("H");
        IAtom oxygen = new Atom("O");
        oxygen.setFormalCharge(-1);
        mol.addAtom(atom);
        mol.addAtom(oxygen);
        mol.addBond(0, 1, Order.SINGLE);

        String[] expectedTypes = {"H", "O.minus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testHydroxyl2() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom oxygen = new Atom("O");
        oxygen.setFormalCharge(-1);
        mol.addAtom(oxygen);

        String[] expectedTypes = {"O.minus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testHydroxonium() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("H");
        IAtom atom1 = new Atom("H");
        IAtom atom2 = new Atom("H");
        IAtom oxygen = new Atom("O");
        oxygen.setFormalCharge(+1);
        mol.addAtom(atom);
        mol.addAtom(atom1);
        mol.addAtom(atom2);
        mol.addAtom(oxygen);
        mol.addBond(0, 3, Order.SINGLE);
        mol.addBond(1, 3, Order.SINGLE);
        mol.addBond(2, 3, Order.SINGLE);

        String[] expectedTypes = {"H", "H", "H", "O.plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testPositiveCarbonyl() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("H");
        IAtom atom1 = new Atom("H");
        IAtom atom2 = new Atom("H");
        IAtom oxygen = new Atom("O");
        IAtom carbon = new Atom("C");
        oxygen.setFormalCharge(+1);
        mol.addAtom(atom);
        mol.addAtom(atom1);
        mol.addAtom(atom2);
        mol.addAtom(oxygen);
        mol.addAtom(carbon);
        mol.addBond(0, 3, Order.SINGLE);
        mol.addBond(1, 4, Order.SINGLE);
        mol.addBond(2, 4, Order.SINGLE);
        mol.addBond(3, 4, Order.DOUBLE);

        String[] expectedTypes = {"H", "H", "H", "O.plus.sp2", "C.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testProton() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("H");
        atom.setFormalCharge(1);
        mol.addAtom(atom);

        String[] expectedTypes = {"H.plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testHalides() throws Exception {
        IAtomContainer mol = new AtomContainer();

        IAtom atom = new Atom("Cl");
        atom.setFormalCharge(-1);
        mol.addAtom(atom);
        String[] expectedTypes = {"Cl.minus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);

        mol = new AtomContainer();
        atom = new Atom("F");
        atom.setFormalCharge(-1);
        mol.addAtom(atom);
        expectedTypes = new String[]{"F.minus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);

        mol = new AtomContainer();
        atom = new Atom("Br");
        atom.setFormalCharge(-1);
        mol.addAtom(atom);
        expectedTypes = new String[]{"Br.minus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);

        mol = new AtomContainer();
        atom = new Atom("I");
        atom.setFormalCharge(-1);
        mol.addAtom(atom);
        expectedTypes = new String[]{"I.minus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testHalogens() throws Exception {
        IAtomContainer mol = new AtomContainer();

        IAtom atom = new Atom("Cl");
        IAtom hydrogen = new Atom("H");
        mol.addAtom(atom);
        mol.addAtom(hydrogen);
        mol.addBond(0, 1, Order.SINGLE);
        String[] expectedTypes = new String[]{"Cl", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);

        mol = new AtomContainer();
        atom = new Atom("I");
        mol.addAtom(atom);
        mol.addAtom(hydrogen);
        mol.addBond(0, 1, Order.SINGLE);
        expectedTypes = new String[]{"I", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);

        mol = new AtomContainer();
        atom = new Atom("Br");
        mol.addAtom(atom);
        mol.addAtom(hydrogen);
        mol.addBond(0, 1, Order.SINGLE);
        expectedTypes = new String[]{"Br", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);

        mol = new AtomContainer();
        atom = new Atom("F");
        mol.addAtom(atom);
        mol.addAtom(hydrogen);
        mol.addBond(0, 1, Order.SINGLE);
        expectedTypes = new String[]{"F", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testFluorRadical() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("F");
        mol.addAtom(atom);
        mol.addSingleElectron(0);

        String[] expectedTypes = {"F.radical"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testChlorRadical() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("Cl");
        mol.addAtom(atom);
        mol.addSingleElectron(0);

        String[] expectedTypes = {"Cl.radical"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testBromRadical() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("Br");
        mol.addAtom(atom);
        mol.addSingleElectron(0);

        String[] expectedTypes = {"Br.radical"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testIodRadical() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("I");
        mol.addAtom(atom);
        mol.addSingleElectron(0);

        String[] expectedTypes = {"I.radical"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testIMinusF2() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("F");
        IAtom atom2 = new Atom("I");
        IAtom atom3 = new Atom("F");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        atom2.setFormalCharge(-1);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(1, 2, Order.SINGLE);

        String[] expectedTypes = {"F", "I.minus.5", "F"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testHydride() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("H");
        atom.setFormalCharge(-1);
        mol.addAtom(atom);

        String[] expectedTypes = new String[]{"H.minus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testHydrogenRadical() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("H");
        mol.addAtom(atom);
        mol.addSingleElectron(0);

        String[] expectedTypes = new String[]{"H.radical"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testAzide() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("N");
        atom2.setFormalCharge(-1);
        IAtom atom3 = new Atom("N");
        atom3.setFormalCharge(+1);
        IAtom atom4 = new Atom("N");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(1, 2, Order.SINGLE);
        mol.addBond(2, 3, Order.TRIPLE);

        String[] expectedTypes = new String[]{"C.sp3", "N.minus.sp3", "N.plus.sp1", "N.sp1"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testAllene() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom1 = new Atom("C");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("C");
        mol.addAtom(atom1);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0, 1, Order.DOUBLE);
        mol.addBond(1, 2, Order.DOUBLE);

        String[] expectedTypes = new String[]{"C.sp2", "C.allene", "C.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testAzide2() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("N");
        IAtom atom3 = new Atom("N");
        atom3.setFormalCharge(+1);
        IAtom atom4 = new Atom("N");
        atom4.setFormalCharge(-1);
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(1, 2, Order.DOUBLE);
        mol.addBond(2, 3, Order.DOUBLE);

        String[] expectedTypes = new String[]{"C.sp3", "N.sp2", "N.plus.sp1", "N.minus.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testMercuryComplex() throws Exception {
        IAtomContainer mol = new AtomContainer();

        IAtom atom = new Atom("Hg");
        atom.setFormalCharge(-1);
        IAtom atom1 = new Atom("O");
        atom1.setFormalCharge(+1);
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("C");
        IAtom atom4 = new Atom("N");
        mol.addAtom(atom);
        mol.addAtom(atom1);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(1, 2, Order.DOUBLE);
        mol.addBond(2, 3, Order.SINGLE);
        mol.addBond(3, 4, Order.DOUBLE);
        mol.addBond(4, 0, Order.SINGLE);
        String[] expectedTypes = new String[]{"Hg.minus", "O.plus.sp2", "C.sp2", "C.sp2", "N.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Hg_2plus() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Hg");
        a1.setFormalCharge(2);
        mol.addAtom(a1);

        String[] expectedTypes = {"Hg.2plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Hg_plus() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Hg");
        a1.setFormalCharge(1);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);

        String[] expectedTypes = {"Hg.plus", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Hg_metallic() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Hg");
        a1.setFormalCharge(0);
        mol.addAtom(a1);

        String[] expectedTypes = {"Hg.metallic"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Hg_1() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Hg");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.DOUBLE);
        mol.addBond(b1);

        String[] expectedTypes = {"Hg.1", "C.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Hg_2() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Hg");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);

        String[] expectedTypes = {"Hg.2", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testPoloniumComplex() throws Exception {
        IAtomContainer mol = new AtomContainer();

        IAtom atom = new Atom("O");
        IAtom atom1 = new Atom("Po");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom1);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(1, 2, Order.SINGLE);
        mol.addBond(2, 3, Order.SINGLE);
        String[] expectedTypes = new String[]{"O.sp3", "Po", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testStronglyBoundKations() throws Exception {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("O"));
        mol.getAtom(1).setFormalCharge(+1);
        mol.addBond(0, 1, IBond.Order.DOUBLE);
        IAtom atom = new Atom("Na");
        mol.addAtom(atom);
        mol.addBond(1, 2, IBond.Order.SINGLE);

        String[] expectedTypes = new String[]{"C.sp2", "O.plus.sp2", "Na"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testMetallics() throws Exception {
        IAtomContainer mol = new AtomContainer();

        IAtom atom = new Atom("W");
        mol.addAtom(atom);
        String[] expectedTypes = new String[]{"W.metallic"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);

        mol = new AtomContainer();
        atom = new Atom("K");
        mol.addAtom(atom);
        expectedTypes = new String[]{"K.metallic"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);

        mol = new AtomContainer();
        atom = new Atom("Co");
        mol.addAtom(atom);
        expectedTypes = new String[]{"Co.metallic"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testSalts() throws Exception {
        IAtomContainer mol = new AtomContainer();

        IAtom atom = new Atom("Na");
        atom.setFormalCharge(+1);
        mol.addAtom(atom);
        String[] expectedTypes = new String[]{"Na.plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);

        mol = new AtomContainer();
        atom = new Atom("K");
        atom.setFormalCharge(+1);
        mol.addAtom(atom);
        expectedTypes = new String[]{"K.plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);

        mol = new AtomContainer();
        atom = new Atom("Ca");
        atom.setFormalCharge(+2);
        mol.addAtom(atom);
        expectedTypes = new String[]{"Ca.2plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);

        mol = new AtomContainer();
        atom = new Atom("Mg");
        atom.setFormalCharge(+2);
        mol.addAtom(atom);
        expectedTypes = new String[]{"Mg.2plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);

        mol = new AtomContainer();
        atom = new Atom("Ni");
        atom.setFormalCharge(+2);
        mol.addAtom(atom);
        expectedTypes = new String[]{"Ni.2plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);

        mol = new AtomContainer();
        atom = new Atom("Pt");
        atom.setFormalCharge(+2);
        mol.addAtom(atom);
        expectedTypes = new String[]{"Pt.2plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);

        mol = new AtomContainer();
        atom = new Atom("Co");
        atom.setFormalCharge(+2);
        mol.addAtom(atom);
        expectedTypes = new String[]{"Co.2plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);

        mol = new AtomContainer();
        atom = new Atom("Co");
        atom.setFormalCharge(+3);
        mol.addAtom(atom);
        expectedTypes = new String[]{"Co.3plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);

        mol = new AtomContainer();
        atom = new Atom("Cu");
        atom.setFormalCharge(+2);
        mol.addAtom(atom);
        expectedTypes = new String[]{"Cu.2plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);

        mol = new AtomContainer();
        atom = new Atom("Al");
        atom.setFormalCharge(+3);
        mol.addAtom(atom);
        expectedTypes = new String[]{"Al.3plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void fix_Ca_2() throws Exception {
        String molName = "Ca_2";
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Ca");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);

        String[] expectedTypes = {"Ca.2", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void fix_Ca_1() throws Exception {
        String molName1 = "Ca_1";
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Ca");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.DOUBLE);
        mol.addBond(b1);

        String[] expectedTypes1 = {"Ca.1", "C.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes1, mol);
    }

    @Test
    public void testCyclopentadienyl() throws Exception {
        IAtomContainer cp = new AtomContainer();
        cp.addAtom(new Atom("C"));
        cp.getAtom(0).setHybridization(IAtomType.Hybridization.SP2);
        cp.getAtom(0).setImplicitHydrogenCount(1);
        cp.addAtom(new Atom("C"));
        cp.getAtom(1).setHybridization(IAtomType.Hybridization.SP2);
        cp.getAtom(1).setImplicitHydrogenCount(1);
        cp.addAtom(new Atom("C"));
        cp.getAtom(2).setHybridization(IAtomType.Hybridization.SP2);
        cp.getAtom(2).setImplicitHydrogenCount(1);
        cp.addAtom(new Atom("C"));
        cp.getAtom(3).setHybridization(IAtomType.Hybridization.SP2);
        cp.getAtom(3).setImplicitHydrogenCount(1);
        cp.addAtom(new Atom("C"));
        cp.getAtom(4).setFormalCharge(-1);
        cp.getAtom(4).setHybridization(IAtomType.Hybridization.PLANAR3);
        cp.addAtom(new Atom("H"));
        cp.addBond(0, 1, Order.DOUBLE);
        cp.addBond(1, 2, Order.SINGLE);
        cp.addBond(2, 3, Order.DOUBLE);
        cp.addBond(3, 4, Order.SINGLE);
        cp.addBond(4, 0, Order.SINGLE);
        cp.addBond(4, 5, Order.SINGLE);

        String[] expectedTypes = new String[]{"C.sp2", "C.sp2", "C.sp2", "C.sp2", "C.minus.planar", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, cp);
    }

    @Test
    public void testFerrocene() throws Exception {
        IAtomContainer ferrocene = new AtomContainer();
        ferrocene.addAtom(new Atom("C"));
        ferrocene.addAtom(new Atom("C"));
        ferrocene.addAtom(new Atom("C"));
        ferrocene.addAtom(new Atom("C"));
        ferrocene.addAtom(new Atom("C"));
        ferrocene.getAtom(4).setFormalCharge(-1);
        ferrocene.addAtom(new Atom("C"));
        ferrocene.addAtom(new Atom("C"));
        ferrocene.addAtom(new Atom("C"));
        ferrocene.addAtom(new Atom("C"));
        ferrocene.addAtom(new Atom("C"));
        ferrocene.getAtom(9).setFormalCharge(-1);
        ferrocene.addAtom(new Atom("Fe"));
        ferrocene.getAtom(10).setFormalCharge(+2);
        ferrocene.addBond(0, 1, Order.DOUBLE);
        ferrocene.addBond(1, 2, Order.SINGLE);
        ferrocene.addBond(2, 3, Order.DOUBLE);
        ferrocene.addBond(3, 4, Order.SINGLE);
        ferrocene.addBond(4, 0, Order.SINGLE);
        ferrocene.addBond(5, 6, Order.DOUBLE);
        ferrocene.addBond(6, 7, Order.SINGLE);
        ferrocene.addBond(7, 8, Order.DOUBLE);
        ferrocene.addBond(8, 9, Order.SINGLE);
        ferrocene.addBond(9, 5, Order.SINGLE);

        String[] expectedTypes = new String[]{"C.sp2", "C.sp2", "C.sp2", "C.sp2", "C.minus.planar", "C.sp2", "C.sp2",
                "C.sp2", "C.sp2", "C.minus.planar", "Fe.2plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, ferrocene);
    }

    @Test
    public void testFuran() throws Exception {
        IAtomContainer furan = new AtomContainer();
        furan.addAtom(new Atom("C"));
        furan.addAtom(new Atom("C"));
        furan.addAtom(new Atom("C"));
        furan.addAtom(new Atom("C"));
        furan.addAtom(new Atom("O"));
        furan.addBond(0, 1, Order.DOUBLE);
        furan.addBond(1, 2, Order.SINGLE);
        furan.addBond(2, 3, Order.DOUBLE);
        furan.addBond(3, 4, Order.SINGLE);
        furan.addBond(4, 0, Order.SINGLE);
        String[] expectedTypes = new String[]{"C.sp2", "C.sp2", "C.sp2", "C.sp2", "O.planar3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, furan);
    }

    @Test
    public void testPerchlorate() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("Cl");
        IAtom atom3 = new Atom("O");
        IAtom atom4 = new Atom("O");
        IAtom atom5 = new Atom("O");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addAtom(atom5);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(1, 2, Order.DOUBLE);
        mol.addBond(1, 3, Order.DOUBLE);
        mol.addBond(1, 4, Order.DOUBLE);

        String[] expectedTypes = new String[]{"O.sp3", "Cl.perchlorate", "O.sp2", "O.sp2", "O.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * Gallium tetrahydroxide.
     */
    @Test
    public void testGallate() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("O");
        atom.setFormalCharge(-1);
        IAtom atom2 = new Atom("Ga");
        atom2.setFormalCharge(+3);
        IAtom atom3 = new Atom("O");
        atom3.setFormalCharge(-1);
        IAtom atom4 = new Atom("O");
        atom4.setFormalCharge(-1);
        IAtom atom5 = new Atom("O");
        atom5.setFormalCharge(-1);
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addAtom(atom5);

        String[] expectedTypes = new String[]{"O.minus", "Ga.3plus", "O.minus", "O.minus", "O.minus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * Gallium trihydroxide.
     */
    @Test
    public void testGallateCovalent() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("Ga");
        IAtom atom3 = new Atom("O");
        IAtom atom4 = new Atom("O");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addBond(1,0,Order.SINGLE);
        mol.addBond(1,2,Order.SINGLE);
        mol.addBond(1,3,Order.SINGLE);

        String[] expectedTypes = new String[]{"O.sp3", "Ga", "O.sp3", "O.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testPerchlorate_ChargedBonds() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("Cl");
        IAtom atom3 = new Atom("O");
        IAtom atom4 = new Atom("O");
        IAtom atom5 = new Atom("O");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        atom2.setFormalCharge(+3);
        mol.addAtom(atom3);
        atom3.setFormalCharge(-1);
        mol.addAtom(atom4);
        atom4.setFormalCharge(-1);
        mol.addAtom(atom5);
        atom5.setFormalCharge(-1);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(1, 2, Order.SINGLE);
        mol.addBond(1, 3, Order.SINGLE);
        mol.addBond(1, 4, Order.SINGLE);

        String[] expectedTypes = new String[]{"O.sp3", "Cl.perchlorate.charged", "O.minus", "O.minus", "O.minus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testChlorate() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("Cl");
        IAtom atom3 = new Atom("O");
        IAtom atom4 = new Atom("O");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(1, 2, Order.DOUBLE);
        mol.addBond(1, 3, Order.DOUBLE);

        String[] expectedTypes = new String[]{"O.sp3", "Cl.chlorate", "O.sp2", "O.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testOxide() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("O");
        atom.setFormalCharge(-2);
        mol.addAtom(atom);

        String[] expectedTypes = new String[]{"O.minus2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testAzulene() throws Exception {
        IAtomContainer molecule = TestMoleculeFactory.makeAzulene();
        String[] expectedTypes = new String[]{"C.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp2",
                "C.sp2", "C.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
    }

    @Test
    public void testIndole() throws Exception {
        String[] expectedTypes = {"C.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp2", "N.planar3"};
        IAtomContainer molecule = TestMoleculeFactory.makeIndole();
        assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
    }

    /**
     * Test for the structure in XLogPDescriptorTest.testno937().
     */
    @Test
    public void testno937() throws Exception {
        String[] expectedTypes = {"C.sp2", "N.planar3", "C.sp2", "N.sp2", "C.sp2", "C.sp3"};
        IAtomContainer molecule = TestMoleculeFactory.makePyrrole();
        molecule.getAtom(3).setSymbol("N");
        molecule.addAtom(molecule.getBuilder().newInstance(IAtom.class, "C"));
        molecule.addBond(1, 5, IBond.Order.SINGLE);
        assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
    }

    @Test
    public void testBenzene() throws Exception {
        String[] expectedTypes = {"C.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp2"};
        IAtomContainer molecule = new AtomContainer();
        molecule.add(new Ring(6, "C"));
        for (IBond bond : molecule.bonds()) {
            bond.setFlag(CDKConstants.ISAROMATIC, true);
        }
        for (IAtom atom : molecule.atoms()) {
            atom.setImplicitHydrogenCount(1);
        }
        assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
    }

    @Test
    public void testBenzene_SingleOrDouble() throws Exception {
        String[] expectedTypes = {"C.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp2"};
        IAtomContainer molecule = new AtomContainer();
        molecule.add(new Ring(6, "C"));
        for (IBond bond : molecule.bonds()) {
            bond.setOrder(IBond.Order.UNSET);
            bond.setFlag(CDKConstants.SINGLE_OR_DOUBLE, true);
        }
        for (IAtom atom : molecule.atoms()) {
            atom.setImplicitHydrogenCount(1);
        }
        assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
    }

    @Test
    public void testPyrrole() throws Exception {
        String[] expectedTypes = {"C.sp2", "N.planar3", "C.sp2", "C.sp2", "C.sp2"};
        IAtomContainer molecule = TestMoleculeFactory.makePyrrole();
        assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
    }

    @Test
    public void testPyrrole_SingleOrDouble() throws Exception {
        String[] expectedTypes = {"C.sp2", "N.planar3", "C.sp2", "C.sp2", "C.sp2"};
        IAtomContainer molecule = TestMoleculeFactory.makePyrrole();
        for (IBond bond : molecule.bonds()) {
            bond.setOrder(IBond.Order.UNSET);
            bond.setFlag(CDKConstants.SINGLE_OR_DOUBLE, true);
        }
        for (IAtom atom : molecule.atoms()) {
            atom.setImplicitHydrogenCount(1);
        }
        assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
    }

    @Test
    public void testPyrroleAnion() throws Exception {
        String[] expectedTypes = {"C.sp2", "N.minus.planar3", "C.sp2", "C.sp2", "C.sp2"};
        IAtomContainer molecule = TestMoleculeFactory.makePyrroleAnion();
        assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
    }

    @Test
    public void testImidazole() throws Exception {
        String[] expectedTypes = {"C.sp2", "N.planar3", "C.sp2", "N.sp2", "C.sp2"};
        IAtomContainer molecule = TestMoleculeFactory.makeImidazole();
        assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
    }

    @Test
    public void testPyrazole() throws Exception {
        String[] expectedTypes = {"C.sp2", "N.planar3", "N.sp2", "C.sp2", "C.sp2"};
        IAtomContainer molecule = TestMoleculeFactory.makePyrazole();
        assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
    }

    @Test
    public void test124Triazole() throws Exception {
        String[] expectedTypes = {"C.sp2", "N.planar3", "N.sp2", "C.sp2", "N.sp2"};
        IAtomContainer molecule = TestMoleculeFactory.make124Triazole();
        assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
    }

    @Test
    public void test123Triazole() throws Exception {
        String[] expectedTypes = {"C.sp2", "N.planar3", "N.sp2", "N.sp2", "C.sp2"};
        IAtomContainer molecule = TestMoleculeFactory.make123Triazole();
        assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
    }

    @Test
    public void testTetrazole() throws Exception {
        String[] expectedTypes = {"N.sp2", "N.planar3", "N.sp2", "N.sp2", "C.sp2"};
        IAtomContainer molecule = TestMoleculeFactory.makeTetrazole();
        assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
    }

    @Test
    public void testOxazole() throws Exception {
        String[] expectedTypes = {"C.sp2", "O.planar3", "C.sp2", "N.sp2", "C.sp2"};
        IAtomContainer molecule = TestMoleculeFactory.makeOxazole();
        assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
    }

    @Test
    public void testIsoxazole() throws Exception {
        String[] expectedTypes = {"C.sp2", "O.planar3", "N.sp2", "C.sp2", "C.sp2"};
        IAtomContainer molecule = TestMoleculeFactory.makeIsoxazole();
        assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
    }

    // testThiazole can be found below...

    @Test
    public void testIsothiazole() throws Exception {
        String[] expectedTypes = {"C.sp2", "S.planar3", "N.sp2", "C.sp2", "C.sp2"};
        IAtomContainer molecule = TestMoleculeFactory.makeIsothiazole();
        assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
    }

    @Test
    public void testThiadiazole() throws Exception {
        String[] expectedTypes = {"C.sp2", "S.planar3", "C.sp2", "N.sp2", "N.sp2"};
        IAtomContainer molecule = TestMoleculeFactory.makeThiadiazole();
        assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
    }

    @Test
    public void testOxadiazole() throws Exception {
        String[] expectedTypes = {"C.sp2", "O.planar3", "C.sp2", "N.sp2", "N.sp2"};
        IAtomContainer molecule = TestMoleculeFactory.makeOxadiazole();
        assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
    }

    @Test
    public void testPyridine() throws Exception {
        String[] expectedTypes = {"C.sp2", "N.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp2"};
        IAtomContainer molecule = TestMoleculeFactory.makePyridine();
        assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
    }

    @Test
    public void testPyridine_SingleOrDouble() throws Exception {
        String[] expectedTypes = {"C.sp2", "N.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp2"};
        IAtomContainer molecule = TestMoleculeFactory.makePyridine();
        for (IBond bond : molecule.bonds()) {
            bond.setOrder(IBond.Order.UNSET);
            bond.setFlag(CDKConstants.SINGLE_OR_DOUBLE, true);
        }
        assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
    }

    @Test
    public void testPyridineDirect() throws Exception {
        String[] expectedTypes = {"N.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp2"};
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("N"));
        mol.addAtom(new Atom("C"));
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addAtom(new Atom("C"));
        mol.addBond(1, 2, IBond.Order.DOUBLE);
        mol.addAtom(new Atom("C"));
        mol.addBond(2, 3, IBond.Order.SINGLE);
        mol.addAtom(new Atom("C"));
        mol.addBond(3, 4, IBond.Order.DOUBLE);
        mol.addAtom(new Atom("C"));
        mol.addBond(4, 5, IBond.Order.SINGLE);
        mol.addBond(0, 5, IBond.Order.DOUBLE);
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * @cdk.bug 1957958
     */
    @Test
    public void testPyridineWithSP2() throws Exception {
        String[] expectedTypes = {"N.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp2"};
        IAtomContainer mol = new AtomContainer();
        IAtom a1 = mol.getBuilder().newInstance(IAtom.class, "N");
        IAtom a2 = mol.getBuilder().newInstance(IAtom.class, "C");
        IAtom a3 = mol.getBuilder().newInstance(IAtom.class, "C");
        IAtom a4 = mol.getBuilder().newInstance(IAtom.class, "C");
        IAtom a5 = mol.getBuilder().newInstance(IAtom.class, "C");
        IAtom a6 = mol.getBuilder().newInstance(IAtom.class, "C");

        a1.setHybridization(Hybridization.SP2);
        a2.setHybridization(Hybridization.SP2);
        a3.setHybridization(Hybridization.SP2);
        a4.setHybridization(Hybridization.SP2);
        a5.setHybridization(Hybridization.SP2);
        a6.setHybridization(Hybridization.SP2);

        mol.addAtom(a1);
        mol.addAtom(a2);
        mol.addAtom(a3);
        mol.addAtom(a4);
        mol.addAtom(a5);
        mol.addAtom(a6);

        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addBond(1, 2, IBond.Order.SINGLE);
        mol.addBond(2, 3, IBond.Order.SINGLE);
        mol.addBond(3, 4, IBond.Order.SINGLE);
        mol.addBond(4, 5, IBond.Order.SINGLE);
        mol.addBond(0, 5, IBond.Order.SINGLE);
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * @cdk.bug 1879589
     */
    @Test
    public void testChargedSulphurSpecies() throws Exception {
        String[] expectedTypes = {"C.sp2", "N.sp2", "C.sp2", "C.sp2", "S.plus", "C.sp2"};
        IAtomContainer molecule = TestMoleculeFactory.makePyridine();
        molecule.getAtom(4).setSymbol("S");
        molecule.getAtom(4).setFormalCharge(+1);
        assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
    }

    @Test
    public void testPyridineOxide_Charged() throws Exception {
        String[] expectedTypes = {"C.sp2", "N.plus.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp2", "O.minus"};
        IAtomContainer molecule = TestMoleculeFactory.makePyridineOxide();
        assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
    }

    @Test
    public void testPyridineOxide() throws Exception {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("N")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5
        mol.addAtom(new Atom("O")); // 6

        mol.addBond(0, 1, IBond.Order.DOUBLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.DOUBLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 5, IBond.Order.DOUBLE); // 5
        mol.addBond(5, 0, IBond.Order.SINGLE); // 6
        mol.addBond(1, 6, IBond.Order.DOUBLE); // 7

        String[] expectedTypes = {"C.sp2", "N.sp2.3", "C.sp2", "C.sp2", "C.sp2", "C.sp2", "O.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testPyridineOxide_SP2() throws Exception {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.getAtom(0).setHybridization(Hybridization.SP2);
        mol.addAtom(new Atom("N")); // 1
        mol.getAtom(1).setHybridization(Hybridization.SP2);
        mol.addAtom(new Atom("C")); // 2
        mol.getAtom(2).setHybridization(Hybridization.SP2);
        mol.addAtom(new Atom("C")); // 3
        mol.getAtom(3).setHybridization(Hybridization.SP2);
        mol.addAtom(new Atom("C")); // 4
        mol.getAtom(4).setHybridization(Hybridization.SP2);
        mol.addAtom(new Atom("C")); // 5
        mol.getAtom(5).setHybridization(Hybridization.SP2);
        mol.addAtom(new Atom("O")); // 6
        mol.getAtom(6).setHybridization(Hybridization.SP2);

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.SINGLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 5, IBond.Order.SINGLE); // 5
        mol.addBond(5, 0, IBond.Order.SINGLE); // 6
        mol.addBond(1, 6, IBond.Order.DOUBLE); // 7

        String[] expectedTypes = {"C.sp2", "N.sp2.3", "C.sp2", "C.sp2", "C.sp2", "C.sp2", "O.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testPyridineOxideCharged_SP2() throws Exception {
        String[] expectedTypes = {"C.sp2", "N.plus.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp2", "O.minus"};
        IAtomContainer molecule = TestMoleculeFactory.makePyridineOxide();
        Iterator<IBond> bonds = molecule.bonds().iterator();
        while (bonds.hasNext())
            bonds.next().setOrder(Order.SINGLE);
        for (int i = 0; i < 6; i++) {
            molecule.getAtom(i).setHybridization(IAtomType.Hybridization.SP2);
        }
        assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
    }

    @Test
    public void testPyrimidine() throws Exception {
        String[] expectedTypes = {"C.sp2", "N.sp2", "C.sp2", "N.sp2", "C.sp2", "C.sp2"};
        IAtomContainer molecule = TestMoleculeFactory.makePyrimidine();
        assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
    }

    @Test
    public void testPyridazine() throws Exception {
        String[] expectedTypes = {"C.sp2", "N.sp2", "N.sp2", "C.sp2", "C.sp2", "C.sp2"};
        IAtomContainer molecule = TestMoleculeFactory.makePyridazine();
        assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
    }

    @Test
    public void testTriazine() throws Exception {
        String[] expectedTypes = {"C.sp2", "N.sp2", "C.sp2", "N.sp2", "C.sp2", "N.sp2"};
        IAtomContainer molecule = TestMoleculeFactory.makeTriazine();
        assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
    }

    @Test
    public void testThiazole() throws Exception {
        String[] expectedTypes = {"C.sp2", "N.sp2", "C.sp2", "S.planar3", "C.sp2"};
        IAtomContainer molecule = TestMoleculeFactory.makeThiazole();
        assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
    }

    /**
     * SDF version of the PubChem entry for the given InChI uses uncharged Ni.
     *
     * @cdk.inchi InChI=1/C2H6S2.Ni/c3-1-2-4;/h3-4H,1-2H2;/q;+2/p-2/fC2H4S2.Ni/h3-4h;/q-2;m
     */
    @Test
    public void testNiCovalentlyBound() throws Exception {
        String[] expectedTypes = {"C.sp3", "C.sp3", "S.3", "Ni", "S.3"};
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addAtom(new Atom("S"));
        mol.addBond(1, 2, IBond.Order.SINGLE);
        mol.addAtom(new Atom("Ni"));
        mol.addBond(2, 3, IBond.Order.SINGLE);
        mol.addAtom(new Atom("S"));
        mol.addBond(3, 4, IBond.Order.SINGLE);
        mol.addBond(0, 4, IBond.Order.SINGLE);
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testHaloniumsF() throws Exception {
        IAtomContainer mol = new AtomContainer();

        IAtom carbon1 = new Atom("C");
        IAtom carbon2 = new Atom("C");

        IAtom atom = new Atom("F");
        atom.setFormalCharge(+1);
        mol.addAtom(atom);
        mol.addAtom(carbon1);
        mol.addAtom(carbon2);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(0, 2, Order.SINGLE);

        String[] expectedTypes = {"F.plus.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testHaloniumsCl() throws Exception {
        IAtomContainer mol = new AtomContainer();

        IAtom carbon1 = new Atom("C");
        IAtom carbon2 = new Atom("C");

        IAtom atom = new Atom("Cl");
        atom.setFormalCharge(+1);
        mol.addAtom(atom);
        mol.addAtom(carbon1);
        mol.addAtom(carbon2);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(0, 2, Order.SINGLE);

        String[] expectedTypes = {"Cl.plus.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testHaloniumsBr() throws Exception {
        IAtomContainer mol = new AtomContainer();

        IAtom carbon1 = new Atom("C");
        IAtom carbon2 = new Atom("C");

        IAtom atom = new Atom("Br");
        atom.setFormalCharge(+1);
        mol.addAtom(atom);
        mol.addAtom(carbon1);
        mol.addAtom(carbon2);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(0, 2, Order.SINGLE);

        String[] expectedTypes = {"Br.plus.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testHaloniumsI() throws Exception {
        IAtomContainer mol = new AtomContainer();

        IAtom carbon1 = new Atom("C");
        IAtom carbon2 = new Atom("C");

        IAtom atom = new Atom("I");
        atom.setFormalCharge(+1);
        mol.addAtom(atom);
        mol.addAtom(carbon1);
        mol.addAtom(carbon2);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(0, 2, Order.SINGLE);

        String[] expectedTypes = {"I.plus.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testRearrangementCarbokation() throws Exception {
        IAtomContainer mol = new AtomContainer();

        IAtom carbon1 = new Atom("C");
        carbon1.setFormalCharge(+1);
        IAtom carbon2 = new Atom("C");
        IAtom carbon3 = new Atom("C");

        mol.addAtom(carbon1);
        mol.addAtom(carbon2);
        mol.addAtom(carbon3);
        mol.addBond(0, 1, Order.DOUBLE);
        mol.addBond(1, 2, Order.SINGLE);

        String[] expectedTypes = {"C.plus.sp2", "C.sp2", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testChargedSpecies() throws Exception {
        IAtomContainer mol = new AtomContainer();

        IAtom atom1 = new Atom("C");
        atom1.setFormalCharge(-1);
        IAtom atom2 = new Atom("O");
        atom2.setFormalCharge(+1);

        mol.addAtom(atom1);
        mol.addAtom(atom2);
        mol.addBond(0, 1, Order.TRIPLE);

        String[] expectedTypes = {"C.minus.sp1", "O.plus.sp1"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    //    [O+]=C-[C-]
    @Test
    public void testChargedSpecies2() throws Exception {
        IAtomContainer mol = new AtomContainer();

        IAtom atom1 = new Atom("O");
        atom1.setFormalCharge(1);
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("C");
        atom3.setFormalCharge(-1);

        mol.addAtom(atom1);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0, 1, Order.DOUBLE);
        mol.addBond(0, 2, Order.SINGLE);

        String[] expectedTypes = {"O.plus.sp2", "C.sp2", "C.minus.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    //    [C-]=C-C
    @Test
    public void testChargedSpecies3() throws Exception {
        IAtomContainer mol = new AtomContainer();

        IAtom atom1 = new Atom("C");
        atom1.setFormalCharge(-1);
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("C");

        mol.addAtom(atom1);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0, 1, Order.DOUBLE);
        mol.addBond(0, 2, Order.SINGLE);

        String[] expectedTypes = {"C.minus.sp2", "C.sp2", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    // [C-]#[N+]C
    @Test
    public void testIsonitrile() throws Exception {
        IAtomContainer mol = new AtomContainer();

        IAtom atom1 = new Atom("C");
        IAtom atom2 = new Atom("N");
        atom2.setFormalCharge(1);
        IAtom atom3 = new Atom("C");
        atom3.setFormalCharge(-1);

        mol.addAtom(atom1);
        mol.addAtom(atom2);
        mol.addAtom(atom3);

        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(1, 2, Order.TRIPLE);

        String[] expectedTypes = {"C.sp3", "N.plus.sp1", "C.minus.sp1"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testNobleGases() throws Exception {
        IAtomContainer mol = new AtomContainer();

        mol.addAtom(new Atom("He"));
        mol.addAtom(new Atom("Ne"));
        mol.addAtom(new Atom("Ar"));
        mol.addAtom(new Atom("Kr"));
        mol.addAtom(new Atom("Xe"));
        mol.addAtom(new Atom("Rn"));

        String[] expectedTypes = {"He", "Ne", "Ar", "Kr", "Xe", "Rn"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testZincChloride() throws Exception {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("Zn"));
        mol.addAtom(new Atom("Cl"));
        mol.addAtom(new Atom("Cl"));
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(0, 2, Order.SINGLE);

        String[] expectedTypes = {"Zn", "Cl", "Cl"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testZinc() throws Exception {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("Zn"));
        mol.getAtom(0).setFormalCharge(+2);

        String[] expectedTypes = {"Zn.2plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testSilicon() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom a1 = mol.getBuilder().newInstance(IAtom.class, "Si");
        mol.addAtom(a1);
        IAtom a2 = mol.getBuilder().newInstance(IAtom.class, "O");
        mol.addAtom(a2);
        IAtom a3 = mol.getBuilder().newInstance(IAtom.class, "O");
        mol.addAtom(a3);
        IAtom a4 = mol.getBuilder().newInstance(IAtom.class, "O");
        mol.addAtom(a4);
        IAtom a5 = mol.getBuilder().newInstance(IAtom.class, "C");
        mol.addAtom(a5);
        IAtom a6 = mol.getBuilder().newInstance(IAtom.class, "C");
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
        IAtom a15 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a15);
        IAtom a16 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a16);
        IAtom a17 = mol.getBuilder().newInstance(IAtom.class, "H");
        mol.addAtom(a17);
        IBond b1 = mol.getBuilder().newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = mol.getBuilder().newInstance(IBond.class, a2, a5, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = mol.getBuilder().newInstance(IBond.class, a3, a6, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = mol.getBuilder().newInstance(IBond.class, a4, a7, IBond.Order.SINGLE);
        mol.addBond(b6);
        IBond b7 = mol.getBuilder().newInstance(IBond.class, a5, a8, IBond.Order.SINGLE);
        mol.addBond(b7);
        IBond b8 = mol.getBuilder().newInstance(IBond.class, a5, a9, IBond.Order.SINGLE);
        mol.addBond(b8);
        IBond b9 = mol.getBuilder().newInstance(IBond.class, a5, a10, IBond.Order.SINGLE);
        mol.addBond(b9);
        IBond b10 = mol.getBuilder().newInstance(IBond.class, a6, a11, IBond.Order.SINGLE);
        mol.addBond(b10);
        IBond b11 = mol.getBuilder().newInstance(IBond.class, a6, a12, IBond.Order.SINGLE);
        mol.addBond(b11);
        IBond b12 = mol.getBuilder().newInstance(IBond.class, a6, a13, IBond.Order.SINGLE);
        mol.addBond(b12);
        IBond b13 = mol.getBuilder().newInstance(IBond.class, a7, a14, IBond.Order.SINGLE);
        mol.addBond(b13);
        IBond b14 = mol.getBuilder().newInstance(IBond.class, a7, a15, IBond.Order.SINGLE);
        mol.addBond(b14);
        IBond b15 = mol.getBuilder().newInstance(IBond.class, a7, a16, IBond.Order.SINGLE);
        mol.addBond(b15);
        IBond b16 = mol.getBuilder().newInstance(IBond.class, a1, a17, IBond.Order.SINGLE);
        mol.addBond(b16);

        String[] expectedTypes = {"Si.sp3", "O.sp3", "O.sp3", "O.sp3", "C.sp3", "C.sp3", "C.sp3", "H", "H", "H", "H",
                "H", "H", "H", "H", "H", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testScandium() throws Exception {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("Sc"));
        mol.getAtom(0).setFormalCharge(-3);
        mol.addAtom(new Atom("O"));
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addAtom(new Atom("H"));
        mol.addBond(1, 2, IBond.Order.SINGLE);
        mol.addAtom(new Atom("O"));
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addAtom(new Atom("H"));
        mol.addBond(3, 4, IBond.Order.SINGLE);
        mol.addAtom(new Atom("O"));
        mol.addBond(0, 5, IBond.Order.SINGLE);
        mol.addAtom(new Atom("H"));
        mol.addBond(5, 6, IBond.Order.SINGLE);
        mol.addAtom(new Atom("O"));
        mol.addBond(0, 7, IBond.Order.SINGLE);
        mol.addAtom(new Atom("H"));
        mol.addBond(7, 8, IBond.Order.SINGLE);
        mol.addAtom(new Atom("O"));
        mol.addBond(0, 9, IBond.Order.SINGLE);
        mol.addAtom(new Atom("H"));
        mol.addBond(9, 10, IBond.Order.SINGLE);
        mol.addAtom(new Atom("O"));
        mol.addBond(0, 11, IBond.Order.SINGLE);
        mol.addAtom(new Atom("H"));
        mol.addBond(11, 12, IBond.Order.SINGLE);

        String[] expectedTypes = {"Sc.3minus", "O.sp3", "H", "O.sp3", "H", "O.sp3", "H", "O.sp3", "H", "O.sp3", "H",
                "O.sp3", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testVanadium() throws Exception {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("V"));
        mol.getAtom(0).setFormalCharge(-3);
        mol.addAtom(new Atom("C"));
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addAtom(new Atom("N"));
        mol.addBond(1, 2, IBond.Order.TRIPLE);
        mol.addAtom(new Atom("C"));
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addAtom(new Atom("N"));
        mol.addBond(3, 4, IBond.Order.TRIPLE);
        mol.addAtom(new Atom("C"));
        mol.addBond(0, 5, IBond.Order.SINGLE);
        mol.addAtom(new Atom("N"));
        mol.addBond(5, 6, IBond.Order.TRIPLE);
        mol.addAtom(new Atom("C"));
        mol.addBond(0, 7, IBond.Order.SINGLE);
        mol.addAtom(new Atom("N"));
        mol.addBond(7, 8, IBond.Order.TRIPLE);
        mol.addAtom(new Atom("C"));
        mol.addBond(0, 9, IBond.Order.SINGLE);
        mol.addAtom(new Atom("N"));
        mol.addBond(9, 10, IBond.Order.TRIPLE);
        mol.addAtom(new Atom("C"));
        mol.addBond(0, 11, IBond.Order.SINGLE);
        mol.addAtom(new Atom("N"));
        mol.addBond(11, 12, IBond.Order.TRIPLE);

        String[] expectedTypes = {"V.3minus", "C.sp", "N.sp1", "C.sp", "N.sp1", "C.sp", "N.sp1", "C.sp", "N.sp1",
                "C.sp", "N.sp1", "C.sp", "N.sp1"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testTitanium() throws Exception {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("Ti"));
        mol.getAtom(0).setFormalCharge(-3);
        mol.addAtom(new Atom("C"));
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addAtom(new Atom("N"));
        mol.addBond(1, 2, IBond.Order.TRIPLE);
        mol.addAtom(new Atom("C"));
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addAtom(new Atom("N"));
        mol.addBond(3, 4, IBond.Order.TRIPLE);
        mol.addAtom(new Atom("C"));
        mol.addBond(0, 5, IBond.Order.SINGLE);
        mol.addAtom(new Atom("N"));
        mol.addBond(5, 6, IBond.Order.TRIPLE);
        mol.addAtom(new Atom("C"));
        mol.addBond(0, 7, IBond.Order.SINGLE);
        mol.addAtom(new Atom("N"));
        mol.addBond(7, 8, IBond.Order.TRIPLE);
        mol.addAtom(new Atom("C"));
        mol.addBond(0, 9, IBond.Order.SINGLE);
        mol.addAtom(new Atom("N"));
        mol.addBond(9, 10, IBond.Order.TRIPLE);
        mol.addAtom(new Atom("C"));
        mol.addBond(0, 11, IBond.Order.SINGLE);
        mol.addAtom(new Atom("N"));
        mol.addBond(11, 12, IBond.Order.TRIPLE);

        String[] expectedTypes = {"Ti.3minus", "C.sp", "N.sp1", "C.sp", "N.sp1", "C.sp", "N.sp1", "C.sp", "N.sp1",
                "C.sp", "N.sp1", "C.sp", "N.sp1"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testBoronTetraFluoride() throws Exception {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("B"));
        mol.getAtom(0).setFormalCharge(-1);
        mol.addAtom(new Atom("F"));
        mol.addAtom(new Atom("F"));
        mol.addAtom(new Atom("F"));
        mol.addAtom(new Atom("F"));
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addBond(0, 2, IBond.Order.SINGLE);
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addBond(0, 4, IBond.Order.SINGLE);

        String[] expectedTypes = {"B.minus", "F", "F", "F", "F"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testBerylliumTetraFluoride() throws Exception {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("Be"));
        mol.getAtom(0).setFormalCharge(-2);
        mol.addAtom(new Atom("F"));
        mol.addAtom(new Atom("F"));
        mol.addAtom(new Atom("F"));
        mol.addAtom(new Atom("F"));
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addBond(0, 2, IBond.Order.SINGLE);
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addBond(0, 4, IBond.Order.SINGLE);

        String[] expectedTypes = {"Be.2minus", "F", "F", "F", "F"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testArsine() throws Exception {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("As"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addBond(0, 2, IBond.Order.SINGLE);
        mol.addBond(0, 3, IBond.Order.SINGLE);

        String[] expectedTypes = {"As", "H", "H", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testBoron() throws Exception {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("B"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addBond(0, 2, IBond.Order.SINGLE);
        mol.addBond(0, 3, IBond.Order.SINGLE);

        String[] expectedTypes = {"B", "H", "H", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testCarbonMonoxide() throws Exception {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C"));
        mol.getAtom(0).setFormalCharge(-1);
        mol.addAtom(new Atom("O"));
        mol.getAtom(1).setFormalCharge(1);
        mol.addBond(0, 1, IBond.Order.TRIPLE);

        String[] expectedTypes = {"C.minus.sp1", "O.plus.sp1"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testTitaniumFourCoordinate() throws Exception {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("Ti"));
        mol.addAtom(new Atom("Cl"));
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addAtom(new Atom("Cl"));
        mol.addBond(0, 2, IBond.Order.SINGLE);
        mol.addAtom(new Atom("Cl"));
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addAtom(new Atom("Cl"));
        mol.addBond(0, 4, IBond.Order.SINGLE);

        String[] expectedTypes = {"Ti.sp3", "Cl", "Cl", "Cl", "Cl"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * @cdk.bug 1872969
     */
    @Test
    public void bug1872969() throws Exception {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("S"));
        mol.addAtom(new Atom("O"));
        mol.addAtom(new Atom("O"));
        mol.addAtom(new Atom("O"));
        mol.getAtom(4).setFormalCharge(-1);
        mol.addAtom(new Atom("Na"));
        mol.getAtom(5).setFormalCharge(+1);
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addBond(1, 2, IBond.Order.DOUBLE);
        mol.addBond(1, 3, IBond.Order.DOUBLE);
        mol.addBond(1, 4, IBond.Order.SINGLE);

        String[] expectedTypes = {"C.sp3", "S.onyl", "O.sp2", "O.sp2", "O.minus", "Na.plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * Test if all elements up to and including Uranium have atom types.
     *
     * @throws Exception
     */
    @Test
    public void testAllElementsRepresented() throws Exception {
        AtomTypeFactory factory = AtomTypeFactory.getInstance("org/openscience/cdk/dict/data/cdk-atom-types.owl",
                SilentChemObjectBuilder.getInstance());
        Assert.assertTrue("Could not read the atom types", factory.getSize() != 0);
        String errorMessage = "Elements without atom type(s) defined in the XML:";
        final int testUptoAtomicNumber = 36; // TODO: 92 ?
        int elementsMissingTypes = 0;
        for (int i = 1; i < testUptoAtomicNumber; i++) {
            String symbol = PeriodicTable.getSymbol(i);
            IAtomType[] expectedTypes = factory.getAtomTypes(symbol);
            if (expectedTypes.length == 0) {
                errorMessage += " " + symbol;
                elementsMissingTypes++;
            }
        }
        Assert.assertEquals(errorMessage, 0, elementsMissingTypes);
    }

    @Test
    public void testAssumeExplicitHydrogens() throws Exception {
        IAtomContainer mol = new AtomContainer();
        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder(),
                CDKAtomTypeMatcher.REQUIRE_EXPLICIT_HYDROGENS);

        mol.addAtom(new Atom("O"));
        mol.getAtom(0).setFormalCharge(+1);
        IAtomType type = atm.findMatchingAtomType(mol, mol.getAtom(0));
        Assert.assertNotNull(type);
        org.hamcrest.MatcherAssert.assertThat(type.getAtomTypeName(), is("X"));

        for (int i = 0; i < 3; i++) {
            mol.addAtom(new Atom("H"));
            mol.addBond(new Bond(mol.getAtom(i + 1), mol.getAtom(0), IBond.Order.SINGLE));
        }
        assertAtomType(testedAtomTypes, "O.plus", atm.findMatchingAtomType(mol, mol.getAtom(0)));
    }

    @Test
    public void testStructGenMatcher() throws Exception {
        CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(DefaultChemObjectBuilder.getInstance());
        Assert.assertNotNull(matcher);
    }

    @Test
    public void testCarbonRadical() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("C");
        IAtom atom4 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addSingleElectron(1);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(1, 2, Order.SINGLE);
        mol.addBond(1, 3, Order.SINGLE);

        String[] expectedTypes = {"C.sp3", "C.radical.planar", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * @cdk.bug 1382
     */
    @Test
    public void testCarbonDiradical() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("C");
        mol.addAtom(atom);
        mol.addSingleElectron(0);
        mol.addSingleElectron(0);

        IAtomTypeMatcher atm = getAtomTypeMatcher(mol.getBuilder());
        IAtomType foundType = atm.findMatchingAtomType(mol, atom);
        Assert.assertEquals("X", foundType.getAtomTypeName());
    }

    @Test
    public void testEthoxyEthaneRadical() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("C");
        mol.addAtom(atom);
        atom.setFormalCharge(+1);
        mol.addSingleElectron(0);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(2, 1, Order.SINGLE);

        String[] expectedTypes = {"O.plus.radical", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testMethylFluorRadical() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("F");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        atom2.setFormalCharge(+1);
        mol.addSingleElectron(1);
        mol.addBond(0, 1, Order.SINGLE);

        String[] expectedTypes = {"C.sp3", "F.plus.radical"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testMethylChloroRadical() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("Cl");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        atom2.setFormalCharge(+1);
        mol.addSingleElectron(1);
        mol.addBond(0, 1, Order.SINGLE);

        String[] expectedTypes = {"C.sp3", "Cl.plus.radical"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testMethylBromoRadical() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("Br");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        atom2.setFormalCharge(+1);
        mol.addSingleElectron(1);
        mol.addBond(0, 1, Order.SINGLE);

        String[] expectedTypes = {"C.sp3", "Br.plus.radical"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testMethylIodoRadical() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("I");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        atom2.setFormalCharge(+1);
        mol.addSingleElectron(1);
        mol.addBond(0, 1, Order.SINGLE);

        String[] expectedTypes = {"C.sp3", "I.plus.radical"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testMethyleneFluorKation() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("F");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        atom2.setFormalCharge(+1);
        mol.addBond(0, 1, Order.DOUBLE);

        String[] expectedTypes = {"C.sp2", "F.plus.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testMethyleneChlorKation() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("Cl");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        atom2.setFormalCharge(+1);
        mol.addBond(0, 1, Order.DOUBLE);

        String[] expectedTypes = {"C.sp2", "Cl.plus.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testMethyleneBromKation() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("Br");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        atom2.setFormalCharge(+1);
        mol.addBond(0, 1, Order.DOUBLE);

        String[] expectedTypes = {"C.sp2", "Br.plus.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testMethyleneIodKation() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("I");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        atom2.setFormalCharge(+1);
        mol.addBond(0, 1, Order.DOUBLE);

        String[] expectedTypes = {"C.sp2", "I.plus.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testMethanolRadical() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("O");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addSingleElectron(1);
        mol.addBond(0, 1, IBond.Order.SINGLE);

        String[] expectedTypes = {"C.sp3", "O.sp3.radical"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testMethylMethylimineRadical() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("N");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("C");
        mol.addAtom(atom);
        atom.setFormalCharge(+1);
        mol.addSingleElectron(0);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0, 1, Order.DOUBLE);
        mol.addBond(2, 1, Order.SINGLE);

        String[] expectedTypes = {"N.plus.sp2.radical", "C.sp2", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testChargeSeparatedFluoroEthane() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("F");
        IAtom atom2 = new Atom("C");
        atom2.setFormalCharge(+1);
        IAtom atom3 = new Atom("C");
        atom3.setFormalCharge(-1);
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(2, 1, Order.SINGLE);

        String[] expectedTypes = {"F", "C.plus.planar", "C.minus.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * @cdk.inchi InChI=1/C2H7NS/c1-4(2)3/h3H,1-2H3
     */
    @Test
    public void testSulphurCompound() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom a1 = mol.getBuilder().newInstance(IAtom.class, "S");
        mol.addAtom(a1);
        IAtom a2 = mol.getBuilder().newInstance(IAtom.class, "N");
        mol.addAtom(a2);
        IAtom a3 = mol.getBuilder().newInstance(IAtom.class, "C");
        mol.addAtom(a3);
        IAtom a4 = mol.getBuilder().newInstance(IAtom.class, "C");
        mol.addAtom(a4);
        IBond b1 = mol.getBuilder().newInstance(IBond.class, a1, a2, IBond.Order.DOUBLE);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);

        String[] expectedTypes = {"S.inyl", "N.sp2", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testAluminumChloride() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom a1 = mol.getBuilder().newInstance(IAtom.class, "Cl");
        mol.addAtom(a1);
        IAtom a2 = mol.getBuilder().newInstance(IAtom.class, "Cl");
        mol.addAtom(a2);
        IAtom a3 = mol.getBuilder().newInstance(IAtom.class, "Cl");
        mol.addAtom(a3);
        IAtom a4 = mol.getBuilder().newInstance(IAtom.class, "Al");
        mol.addAtom(a4);
        IBond b1 = mol.getBuilder().newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newInstance(IBond.class, a2, a4, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newInstance(IBond.class, a3, a4, IBond.Order.SINGLE);
        mol.addBond(b3);

        String[] expectedTypes = {"Cl", "Cl", "Cl", "Al"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * @cdk.inchi InChI=1/C3H9NO/c1-4(2,3)5/h1-3H3
     */
    @Test
    public void cid1145() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom a1 = mol.getBuilder().newInstance(IAtom.class, "O");
        mol.addAtom(a1);
        a1.setFormalCharge(-1);
        IAtom a2 = mol.getBuilder().newInstance(IAtom.class, "N");
        mol.addAtom(a2);
        a2.setFormalCharge(+1);
        IAtom a3 = mol.getBuilder().newInstance(IAtom.class, "C");
        mol.addAtom(a3);
        IAtom a4 = mol.getBuilder().newInstance(IAtom.class, "C");
        mol.addAtom(a4);
        IAtom a5 = mol.getBuilder().newInstance(IAtom.class, "C");
        mol.addAtom(a5);
        IAtom a6 = mol.getBuilder().newInstance(IAtom.class, "H");
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
        IBond b1 = mol.getBuilder().newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newInstance(IBond.class, a2, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newInstance(IBond.class, a2, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = mol.getBuilder().newInstance(IBond.class, a2, a5, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = mol.getBuilder().newInstance(IBond.class, a3, a6, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = mol.getBuilder().newInstance(IBond.class, a3, a7, IBond.Order.SINGLE);
        mol.addBond(b6);
        IBond b7 = mol.getBuilder().newInstance(IBond.class, a3, a8, IBond.Order.SINGLE);
        mol.addBond(b7);
        IBond b8 = mol.getBuilder().newInstance(IBond.class, a4, a9, IBond.Order.SINGLE);
        mol.addBond(b8);
        IBond b9 = mol.getBuilder().newInstance(IBond.class, a4, a10, IBond.Order.SINGLE);
        mol.addBond(b9);
        IBond b10 = mol.getBuilder().newInstance(IBond.class, a4, a11, IBond.Order.SINGLE);
        mol.addBond(b10);
        IBond b11 = mol.getBuilder().newInstance(IBond.class, a5, a12, IBond.Order.SINGLE);
        mol.addBond(b11);
        IBond b12 = mol.getBuilder().newInstance(IBond.class, a5, a13, IBond.Order.SINGLE);
        mol.addBond(b12);
        IBond b13 = mol.getBuilder().newInstance(IBond.class, a5, a14, IBond.Order.SINGLE);
        mol.addBond(b13);

        String[] expectedTypes = {"O.minus", "N.plus", "C.sp3", "C.sp3", "C.sp3", "H", "H", "H", "H", "H", "H", "H",
                "H", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testChiPathFail() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom a1 = mol.getBuilder().newInstance(IAtom.class, "C");
        mol.addAtom(a1);
        IAtom a2 = mol.getBuilder().newInstance(IAtom.class, "C");
        mol.addAtom(a2);
        IAtom a3 = mol.getBuilder().newInstance(IAtom.class, "C");
        mol.addAtom(a3);
        IAtom a4 = mol.getBuilder().newInstance(IAtom.class, "O");
        mol.addAtom(a4);
        IBond b1 = mol.getBuilder().newInstance(IBond.class, a2, a1, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newInstance(IBond.class, a3, a2, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newInstance(IBond.class, a4, a3, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = mol.getBuilder().newInstance(IBond.class, a4, a2, IBond.Order.SINGLE);
        mol.addBond(b4);

        String[] expectedTypes = {"C.sp3", "C.sp3", "C.sp3", "O.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * @cdk.inchi InChI=1S/C6H5IO/c8-7-6-4-2-1-3-5-6/h1-5H
     */
    @Test
    public void testIodosobenzene() throws Exception {
        IAtomContainer mol = TestMoleculeFactory.makeBenzene();
        IAtom iodine = mol.getBuilder().newInstance(IAtom.class, "I");
        IAtom oxygen = mol.getBuilder().newInstance(IAtom.class, "O");
        mol.addAtom(iodine);
        mol.addAtom(oxygen);
        mol.addBond(0, 6, Order.SINGLE);
        mol.addBond(6, 7, Order.DOUBLE);

        String[] expectedTypes = {"C.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp2", "I.3", "O.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * @cdk.inchi InChI=1S/C6H5IO2/c8-7(9)6-4-2-1-3-5-6/h1-5H
     */
    @Test
    public void testIodoxybenzene() throws Exception {
        IAtomContainer mol = TestMoleculeFactory.makeBenzene();
        IAtom iodine = mol.getBuilder().newInstance(IAtom.class, "I");
        IAtom oxygen1 = mol.getBuilder().newInstance(IAtom.class, "O");
        IAtom oxygen2 = mol.getBuilder().newInstance(IAtom.class, "O");
        mol.addAtom(iodine);
        mol.addAtom(oxygen1);
        mol.addAtom(oxygen2);
        mol.addBond(0, 6, Order.SINGLE);
        mol.addBond(6, 7, Order.DOUBLE);
        mol.addBond(6, 8, Order.DOUBLE);

        String[] expectedTypes = {"C.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp2", "I.5", "O.sp2", "O.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * @cdk.inchi InChI=1S/C7H7NOS/c8-7(10-9)6-4-2-1-3-5-6/h1-5H,8H2
     */
    @Test
    public void testThiobenzamideSOxide() throws Exception {
        IAtomContainer mol = TestMoleculeFactory.makeBenzene();
        IAtom carbon = mol.getBuilder().newInstance(IAtom.class, "C");
        IAtom sulphur = mol.getBuilder().newInstance(IAtom.class, "S");
        IAtom oxygen = mol.getBuilder().newInstance(IAtom.class, "O");
        IAtom nitrogen = mol.getBuilder().newInstance(IAtom.class, "N");
        mol.addAtom(carbon);
        mol.addAtom(sulphur);
        mol.addAtom(oxygen);
        mol.addAtom(nitrogen);
        mol.addBond(0, 6, Order.SINGLE);
        mol.addBond(6, 7, Order.DOUBLE);
        mol.addBond(7, 8, Order.DOUBLE);
        mol.addBond(6, 9, Order.SINGLE);

        String[] expectedTypes = {"C.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp2", "S.inyl.2", "O.sp2",
                "N.thioamide"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * @cdk.inchi InChI=1S/C4H10S/c1-5(2)3-4-5/h3-4H2,1-2H3
     */
    @Test
    public void testDimethylThiirane() throws Exception {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(mol.getBuilder().newInstance(IAtom.class, "C"));
        mol.addAtom(mol.getBuilder().newInstance(IAtom.class, "C"));
        mol.addAtom(mol.getBuilder().newInstance(IAtom.class, "C"));
        mol.addAtom(mol.getBuilder().newInstance(IAtom.class, "C"));
        mol.addAtom(mol.getBuilder().newInstance(IAtom.class, "S"));
        mol.addBond(0, 4, Order.SINGLE);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(1, 4, Order.SINGLE);
        mol.addBond(4, 2, Order.SINGLE);
        mol.addBond(4, 3, Order.SINGLE);

        String[] expectedTypes = {"C.sp3", "C.sp3", "C.sp3", "C.sp3", "S.anyl"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * @cdk.inchi     InChI=1/C3H8S/c1-4(2)3/h1H2,2-3H3
     */
    @Test
    public void testSulphonylLookalike() throws Exception {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(mol.getBuilder().newInstance(IAtom.class, "C"));
        mol.addAtom(mol.getBuilder().newInstance(IAtom.class, "C"));
        mol.addAtom(mol.getBuilder().newInstance(IAtom.class, "C"));
        mol.addAtom(mol.getBuilder().newInstance(IAtom.class, "S"));
        mol.addBond(0, 3, Order.SINGLE);
        mol.addBond(1, 3, Order.SINGLE);
        mol.addBond(2, 3, Order.DOUBLE);

        String[] expectedTypes = {"C.sp3", "C.sp3", "C.sp2", "S.inyl"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testNOxide() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom a1 = mol.getBuilder().newInstance(IAtom.class, "C");
        IAtom a2 = mol.getBuilder().newInstance(IAtom.class, "C");
        IAtom a3 = mol.getBuilder().newInstance(IAtom.class, "N");
        IAtom a4 = mol.getBuilder().newInstance(IAtom.class, "O");
        IAtom a5 = mol.getBuilder().newInstance(IAtom.class, "O");

        mol.addAtom(a1);
        mol.addAtom(a2);
        mol.addAtom(a3);
        mol.addAtom(a4);
        mol.addAtom(a5);

        mol.addBond(mol.getBuilder().newInstance(IBond.class, a1, a2, IBond.Order.SINGLE));
        mol.addBond(mol.getBuilder().newInstance(IBond.class, a2, a3, IBond.Order.SINGLE));
        mol.addBond(mol.getBuilder().newInstance(IBond.class, a3, a4, IBond.Order.DOUBLE));
        mol.addBond(mol.getBuilder().newInstance(IBond.class, a3, a5, IBond.Order.DOUBLE));

        String[] expectedTypes = {"C.sp3", "C.sp3", "N.nitro", "O.sp2", "O.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testGermaniumFourCoordinate() throws Exception {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("Ge"));
        mol.addAtom(new Atom("Cl"));
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addAtom(new Atom("Cl"));
        mol.addBond(0, 2, IBond.Order.SINGLE);
        mol.addAtom(new Atom("Cl"));
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addAtom(new Atom("Cl"));
        mol.addBond(0, 4, IBond.Order.SINGLE);

        String[] expectedTypes = {"Ge", "Cl", "Cl", "Cl", "Cl"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testPlatinumFourCoordinate() throws Exception {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("Pt"));
        mol.addAtom(new Atom("Cl"));
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addAtom(new Atom("Cl"));
        mol.addBond(0, 2, IBond.Order.SINGLE);
        mol.addAtom(new Atom("Cl"));
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addAtom(new Atom("Cl"));
        mol.addBond(0, 4, IBond.Order.SINGLE);

        String[] expectedTypes = {"Pt.4", "Cl", "Cl", "Cl", "Cl"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testPlatinumSixCoordinate() throws Exception {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("Pt"));
        mol.addAtom(new Atom("Cl"));
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addAtom(new Atom("Cl"));
        mol.addBond(0, 2, IBond.Order.SINGLE);
        mol.addAtom(new Atom("Cl"));
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addAtom(new Atom("Cl"));
        mol.addBond(0, 4, IBond.Order.SINGLE);
        mol.addAtom(new Atom("O"));
        mol.addBond(0, 5, IBond.Order.SINGLE);
        mol.addAtom(new Atom("O"));
        mol.addBond(0, 6, IBond.Order.SINGLE);

        String[] expectedTypes = {"Pt.6", "Cl", "Cl", "Cl", "Cl", "O.sp3", "O.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * @cdk.bug 2424511
     */
    @Test
    public void testWeirdNitrogen() throws Exception {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("N"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));

        mol.addBond(0, 1, IBond.Order.TRIPLE);
        mol.addBond(1, 2, IBond.Order.DOUBLE);
        mol.addBond(2, 3, IBond.Order.SINGLE);

        String[] expectedTypes = {"C.sp", "N.sp1.2", "C.sp2", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * Testing a nitrogen as found in this SMILES input: c1c2cc[nH]cc2nc1.
     */
    @Test
    public void testAnotherNitrogen() throws Exception {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C"));
        mol.getAtom(0).setHybridization(Hybridization.SP2);
        mol.addAtom(new Atom("C"));
        mol.getAtom(1).setHybridization(Hybridization.SP2);
        mol.addAtom(new Atom("C"));
        mol.getAtom(2).setHybridization(Hybridization.SP2);
        mol.addAtom(new Atom("C"));
        mol.getAtom(3).setHybridization(Hybridization.SP2);
        mol.addAtom(new Atom("N"));
        mol.getAtom(4).setHybridization(Hybridization.PLANAR3);
        mol.getAtom(4).setImplicitHydrogenCount(1);
        mol.addAtom(new Atom("C"));
        mol.getAtom(5).setHybridization(Hybridization.SP2);
        mol.addAtom(new Atom("C"));
        mol.getAtom(6).setHybridization(Hybridization.SP2);
        mol.addAtom(new Atom("N"));
        mol.getAtom(7).setHybridization(Hybridization.SP2);
        mol.addAtom(new Atom("C"));
        mol.getAtom(8).setHybridization(Hybridization.SP2);

        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addBond(0, 8, IBond.Order.SINGLE);
        mol.addBond(1, 2, IBond.Order.SINGLE);
        mol.addBond(1, 6, IBond.Order.SINGLE);
        mol.addBond(2, 3, IBond.Order.SINGLE);
        mol.addBond(3, 4, IBond.Order.SINGLE);
        mol.addBond(4, 5, IBond.Order.SINGLE);
        mol.addBond(5, 6, IBond.Order.SINGLE);
        mol.addBond(6, 7, IBond.Order.SINGLE);
        mol.addBond(7, 8, IBond.Order.SINGLE);

        String[] expectedTypes = {"C.sp2", "C.sp2", "C.sp2", "C.sp2", "N.planar3", "C.sp2", "C.sp2", "N.sp2", "C.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * @cdk.bug 3061263
     */
    @Test
    public void testFormalChargeRepresentation() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("O");
        final IAtomType.Hybridization thisHybridization = IAtomType.Hybridization.SP3;
        atom.setHybridization(thisHybridization);
        mol.addAtom(atom);
        String[] expectedTypes = {"O.minus"};

        // option one: Integer.valueOf()
        atom.setFormalCharge(Integer.valueOf(-1));
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);

        // option one: autoboxing
        atom.setFormalCharge(-1);
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);

        // option one: new Integer()
        atom.setFormalCharge(new Integer(-1));
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * @cdk.bug 3190151
     */
    @Test
    public void testP() throws Exception {
        IAtom atomP = new Atom("P");
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(atomP);
        String[] expectedTypes = {"P.ine"};

        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * @cdk.bug 3190151
     */
    @Test
    public void testPine() throws Exception {
        IAtom atomP = new Atom(Elements.PHOSPHORUS);
        IAtomType atomTypeP = new AtomType(Elements.PHOSPHORUS);
        AtomTypeManipulator.configure(atomP, atomTypeP);

        IAtomContainer ac = atomP.getBuilder().newInstance(IAtomContainer.class);
        ac.addAtom(atomP);
        IAtomType type = null;
        for (IAtom atom : ac.atoms()) {
            type = CDKAtomTypeMatcher.getInstance(ac.getBuilder()).findMatchingAtomType(ac, atom);
            Assert.assertNotNull(type);
        }
    }

    @Test
    public void test_S_sp3d1() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "S");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IAtom a6 = builder.newInstance(IAtom.class, "C");
        a6.setFormalCharge(0);
        mol.addAtom(a6);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a4, IBond.Order.DOUBLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a1, a5, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = builder.newInstance(IBond.class, a1, a6, IBond.Order.SINGLE);
        mol.addBond(b5);

        String[] expectedTypes = {"S.sp3d1", "C.sp3", "C.sp3", "C.sp2", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_S_inyl_2() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "S");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.DOUBLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.DOUBLE);
        mol.addBond(b2);

        String[] expectedTypes = {"S.inyl.2", "C.sp2", "C.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_S_2minus() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "S");
        a1.setFormalCharge(-2);
        mol.addAtom(a1);

        String[] expectedTypes = {"S.2minus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_S_sp3() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "S");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);

        String[] expectedTypes = {"S.3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_S_sp3_4() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "S");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.DOUBLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.DOUBLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a1, a5, IBond.Order.SINGLE);
        mol.addBond(b4);

        String[] expectedTypes = {"S.sp3.4", "C.sp2", "C.sp2", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Co_3plus() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Co");
        a1.setFormalCharge(3);
        mol.addAtom(a1);

        String[] expectedTypes = {"Co.3plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Co_metallic() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Co");
        a1.setFormalCharge(0);
        mol.addAtom(a1);

        String[] expectedTypes = {"Co.metallic"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Co_plus_6() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Co");
        a1.setFormalCharge(1);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IAtom a6 = builder.newInstance(IAtom.class, "C");
        a6.setFormalCharge(0);
        mol.addAtom(a6);
        IAtom a7 = builder.newInstance(IAtom.class, "C");
        a7.setFormalCharge(0);
        mol.addAtom(a7);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a1, a5, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = builder.newInstance(IBond.class, a1, a6, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = builder.newInstance(IBond.class, a1, a7, IBond.Order.SINGLE);
        mol.addBond(b6);

        String[] expectedTypes = {"Co.plus.6", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Co_2plus() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Co");
        a1.setFormalCharge(2);
        mol.addAtom(a1);

        String[] expectedTypes = {"Co.2plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Co_plus_2() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Co");
        a1.setFormalCharge(1);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);

        String[] expectedTypes = {"Co.plus.2", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Co_2() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "C");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "Co");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IBond b1 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a2, a3, IBond.Order.SINGLE);
        mol.addBond(b2);

        String[] expectedTypes = {"C.sp3", "C.sp3", "Co.2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Co_6() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Co");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IAtom a6 = builder.newInstance(IAtom.class, "C");
        a6.setFormalCharge(0);
        mol.addAtom(a6);
        IAtom a7 = builder.newInstance(IAtom.class, "C");
        a7.setFormalCharge(0);
        mol.addAtom(a7);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a1, a5, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = builder.newInstance(IBond.class, a1, a6, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = builder.newInstance(IBond.class, a1, a7, IBond.Order.SINGLE);
        mol.addBond(b6);

        String[] expectedTypes = {"Co.6", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Co_plus_4() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Co");
        a1.setFormalCharge(1);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a1, a5, IBond.Order.SINGLE);
        mol.addBond(b4);

        String[] expectedTypes = {"Co.plus.4", "C.sp3", "C.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Co_4() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Co");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a1, a5, IBond.Order.SINGLE);
        mol.addBond(b4);

        String[] expectedTypes = {"Co.4", "C.sp3", "C.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Co_plus_5() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Co");
        a1.setFormalCharge(1);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IAtom a6 = builder.newInstance(IAtom.class, "C");
        a6.setFormalCharge(0);
        mol.addAtom(a6);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a1, a5, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = builder.newInstance(IBond.class, a1, a6, IBond.Order.SINGLE);
        mol.addBond(b5);

        String[] expectedTypes = {"Co.plus.5", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * @cdk.bug 3529082
     */
    @Test
    public void test_Co_plus() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a2 = builder.newInstance(IAtom.class, "Co");
        a2.setFormalCharge(1);
        mol.addAtom(a2);

        String[] expectedTypes = {"Co.plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Co_plus_1() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "C");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "Co");
        a2.setFormalCharge(1);
        mol.addAtom(a2);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);

        String[] expectedTypes = {"C.sp3", "Co.plus.1"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Co_1() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Co");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);

        String[] expectedTypes = {"Co.1", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * Bromic acid (CHEBI:49382).
     *
     * @cdk.inchi InChI=1S/BrHO3/c2-1(3)4/h(H,2,3,4)
     */
    @Test
    public void test_Br_3() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Br");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "O");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "O");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "O");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.DOUBLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.DOUBLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);

        String[] expectedTypes = {"Br.3", "O.sp2", "O.sp2", "O.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Zn_metallic() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Zn");
        a1.setFormalCharge(0);
        mol.addAtom(a1);

        String[] expectedTypes = {"Zn.metallic"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Zn_1() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Zn");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.DOUBLE);
        mol.addBond(b1);

        String[] expectedTypes = {"Zn.1", "C.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * Vanadate. PDB HET ID : VO4.
     *
     * @cdk.inchi InChI=1S/4O.V/q;3*-1;
     */
    @Test
    public void test_V_3minus_4() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "V");
        a1.setFormalCharge(-3);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "O");
        a2.setFormalCharge(-1);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "O");
        a3.setFormalCharge(-1);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "O");
        a4.setFormalCharge(-1);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "O");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IBond b1 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a1, a5, IBond.Order.DOUBLE);
        mol.addBond(b4);

        String[] expectedTypes = {"V.3minus.4", "O.minus", "O.minus", "O.minus", "O.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * Hexafluoroaluminate
     * @cdk.inchi InChI=1S/Al.6FH.3Na/h;6*1H;;;/q+3;;;;;;;3*+1/p-6
     */
    @Test
    public void test_Al_3minus() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Al");
        a1.setFormalCharge(-3);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IAtom a6 = builder.newInstance(IAtom.class, "C");
        a6.setFormalCharge(0);
        mol.addAtom(a6);
        IAtom a7 = builder.newInstance(IAtom.class, "C");
        a7.setFormalCharge(0);
        mol.addAtom(a7);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a1, a5, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = builder.newInstance(IBond.class, a1, a6, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = builder.newInstance(IBond.class, a1, a7, IBond.Order.SINGLE);
        mol.addBond(b6);

        String[] expectedTypes = {"Al.3minus", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testSe_sp3d1_4() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Se");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a1, a5, IBond.Order.SINGLE);
        mol.addBond(b4);

        String[] expectedTypes1 = {"Se.sp3d1.4", "C.sp3", "C.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes1, mol);
    }

    @Test
    public void testSe_sp3_4() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Se");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a4, IBond.Order.DOUBLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a1, a5, IBond.Order.DOUBLE);
        mol.addBond(b4);

        String[] expectedTypes = {"Se.sp3.4", "C.sp3", "C.sp3", "C.sp2", "C.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testSe_sp2_2() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Se");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.DOUBLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.DOUBLE);
        mol.addBond(b2);

        String[] expectedTypes2 = {"Se.sp2.2", "C.sp2", "C.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes2, mol);
    }

    @Test
    public void testSe_1() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "C");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "Se");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.DOUBLE);
        mol.addBond(b1);

        String[] expectedTypes3 = {"C.sp2", "Se.1"};
        assertAtomTypes(testedAtomTypes, expectedTypes3, mol);
    }

    @Test
    public void testSe_3() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Se");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);

        String[] expectedTypes4 = {"Se.3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes4, mol);
    }

    @Test
    public void testSe_sp3_3() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "C");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "Se");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a2, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a2, a4, IBond.Order.DOUBLE);
        mol.addBond(b3);

        String[] expectedTypes5 = {"C.sp3", "Se.sp3.3", "C.sp3", "C.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes5, mol);
    }

    @Test
    public void testSe_4plus() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Se");
        a1.setFormalCharge(4);
        mol.addAtom(a1);

        String[] expectedTypes6 = {"Se.4plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes6, mol);
    }

    @Test
    public void testSe_plus_3() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "C");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "Se");
        a2.setFormalCharge(1);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a2, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a2, a4, IBond.Order.SINGLE);
        mol.addBond(b3);

        String[] expectedTypes7 = {"C.sp3", "Se.plus.3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes7, mol);
    }

    @Test
    public void testSe_5() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Se");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IAtom a6 = builder.newInstance(IAtom.class, "C");
        a6.setFormalCharge(0);
        mol.addAtom(a6);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.DOUBLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a1, a5, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = builder.newInstance(IBond.class, a1, a6, IBond.Order.SINGLE);
        mol.addBond(b5);

        String[] expectedTypes8 = {"Se.5", "C.sp2", "C.sp3", "C.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes8, mol);
    }

    @Test
    public void test_Se_2() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Se");
        a1.setImplicitHydrogenCount(0);
        a1.setFormalCharge(0);
        mol.addAtom(a1);

        String[] expectedTypes = {"Se.2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * @cdk.inchi InChI=1S/H2Te/h1H2
     */
    @Test
    public void testTellane() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Te");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "H");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "H");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);

        String[] expectedTypes = {"Te.3", "H", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * @cdk.inchi InChI=1S/C3H6P/c1-3-4-2/h3H,2H2,1H3/q+1
     */
    @Test
    public void testPhosphanium() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "P");
        a1.setFormalCharge(1);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IBond b1 = builder.newInstance(IBond.class, a1, a3, IBond.Order.DOUBLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a4, IBond.Order.DOUBLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a2, a3, IBond.Order.SINGLE);
        mol.addBond(b3);

        String[] expectedTypes = {"P.sp1.plus", "C.sp3", "C.sp2", "C.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * @cdk.inchi InChI=1S/CHP/c1-2/h1H
     */
    @Test
    public void testPhosphide() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "P");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "H");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.TRIPLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a2, a3, IBond.Order.SINGLE);
        mol.addBond(b2);

        String[] expectedTypes = {"P.ide", "C.sp", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testPentaMethylPhosphane() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "P");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IAtom a6 = builder.newInstance(IAtom.class, "C");
        a6.setFormalCharge(0);
        mol.addAtom(a6);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a1, a5, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = builder.newInstance(IBond.class, a1, a6, IBond.Order.SINGLE);
        mol.addBond(b5);

        String[] expectedTypes = {"P.ane", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Sb_4() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "C");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "Sb");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a2, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a2, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a2, a5, IBond.Order.DOUBLE);
        mol.addBond(b4);

        String[] expectedTypes = {"C.sp3", "Sb.4", "C.sp3", "C.sp3", "C.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Sb_3() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Sb");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);

        String[] expectedTypes = {"Sb.3", "C.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_B_3plus() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "B");
        a1.setFormalCharge(3);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a1, a5, IBond.Order.SINGLE);
        mol.addBond(b4);

        String[] expectedTypes = {"B.3plus", "C.sp3", "C.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Sr_2plus() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Sr");
        a1.setFormalCharge(2);
        mol.addAtom(a1);

        String[] expectedTypes = {"Sr.2plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Te_4plus() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Te");
        a1.setFormalCharge(4);
        mol.addAtom(a1);

        String[] expectedTypes = {"Te.4plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Be_neutral() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Be");
        a1.setFormalCharge(0);
        mol.addAtom(a1);

        String[] expectedTypes = {"Be.neutral"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Cl_2() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Cl");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.DOUBLE);
        mol.addBond(b2);

        String[] expectedTypes = {"Cl.2", "C.sp3", "C.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_K_neutral() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "K");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);

        String[] expectedTypes = {"K.neutral", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Li_neutral() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Li");
        a1.setFormalCharge(0);
        mol.addAtom(a1);

        String[] expectedTypes = {"Li.neutral"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Li_plus() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Li");
        a1.setFormalCharge(1);
        mol.addAtom(a1);

        String[] expectedTypes = {"Li.plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_I_sp3d2_3() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "I");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);

        String[] expectedTypes = {"I.sp3d2.3", "C.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    @Override
    public void testForDuplicateDefinitions() {
        super.testForDuplicateDefinitions();
    }

    /**
     * @cdk.inchi InChI=1S/CH2N2/c1-3-2/h1H2
     */
    @Test
    public void testAzoCompound() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom a1 = mol.getBuilder().newInstance(IAtom.class, "N");
        a1.setFormalCharge(1);
        mol.addAtom(a1);
        IAtom a2 = mol.getBuilder().newInstance(IAtom.class, "N");
        a2.setFormalCharge(-1);
        mol.addAtom(a2);
        IAtom a3 = mol.getBuilder().newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = mol.getBuilder().newInstance(IAtom.class, "H");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = mol.getBuilder().newInstance(IAtom.class, "H");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IBond b1 = mol.getBuilder().newInstance(IBond.class, a1, a2, IBond.Order.DOUBLE);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newInstance(IBond.class, a1, a3, IBond.Order.DOUBLE);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newInstance(IBond.class, a3, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = mol.getBuilder().newInstance(IBond.class, a3, a5, IBond.Order.SINGLE);
        mol.addBond(b4);

        String[] expectedTypes = {"N.plus.sp1", "N.minus.sp2", "C.sp2", "H", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * @cdk.bug   3141611
     * @cdk.inchi InChI=1S/CH5O2P/c1-4(2)3/h4H,1H3,(H,2,3)
     */
    @Test
    public void testMethylphosphinicAcid() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "P");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "O");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "O");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "H");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IAtom a6 = builder.newInstance(IAtom.class, "H");
        a6.setFormalCharge(0);
        mol.addAtom(a6);
        IAtom a7 = builder.newInstance(IAtom.class, "H");
        a7.setFormalCharge(0);
        mol.addAtom(a7);
        IAtom a8 = builder.newInstance(IAtom.class, "H");
        a8.setFormalCharge(0);
        mol.addAtom(a8);
        IAtom a9 = builder.newInstance(IAtom.class, "H");
        a9.setFormalCharge(0);
        mol.addAtom(a9);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.DOUBLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a1, a5, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = builder.newInstance(IBond.class, a2, a9, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = builder.newInstance(IBond.class, a4, a6, IBond.Order.SINGLE);
        mol.addBond(b6);
        IBond b7 = builder.newInstance(IBond.class, a4, a7, IBond.Order.SINGLE);
        mol.addBond(b7);
        IBond b8 = builder.newInstance(IBond.class, a4, a8, IBond.Order.SINGLE);
        mol.addBond(b8);

        String[] expectedTypes = {"P.ate", "O.sp3", "O.sp2", "C.sp3", "H", "H", "H", "H", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Ti_2() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Ti");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.DOUBLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.DOUBLE);
        mol.addBond(b2);

        String[] expectedTypes = {"Ti.2", "C.sp2", "C.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Ni_metallic() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Ni");
        a1.setFormalCharge(0);
        mol.addAtom(a1);

        String[] expectedTypes = {"Ni.metallic"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Ni_plus() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "C");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "Ni");
        a2.setFormalCharge(1);
        mol.addAtom(a2);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);

        String[] expectedTypes = {"C.sp3", "Ni.plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Pb_1() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Pb");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.DOUBLE);
        mol.addBond(b1);

        String[] expectedTypes = {"Pb.1", "C.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Pb_2plus() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Pb");
        a1.setFormalCharge(2);
        mol.addAtom(a1);

        String[] expectedTypes = {"Pb.2plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Pb_neutral() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Pb");
        a1.setFormalCharge(0);
        mol.addAtom(a1);

        String[] expectedTypes = {"Pb.neutral"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Tl_neutral() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Tl");
        a1.setFormalCharge(0);
        mol.addAtom(a1);

        String[] expectedTypes = {"Tl"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Tl_1() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "C");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "Tl");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);

        String[] expectedTypes = {"C.sp3", "Tl.1"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Tl_plus() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Tl");
        a1.setFormalCharge(1);
        mol.addAtom(a1);

        String[] expectedTypes = {"Tl.plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Mg_neutral_2() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "C");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "Mg");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a2, a3, IBond.Order.SINGLE);
        mol.addBond(b2);

        String[] expectedTypes = {"C.sp3", "Mg.neutral.2", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Mg_neutral_4() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "C");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "Mg");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IBond b1 = builder.newInstance(IBond.class, a2, a3, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a3, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a3, a5, IBond.Order.SINGLE);
        mol.addBond(b4);

        String[] expectedTypes = {"C.sp3", "C.sp3", "Mg.neutral", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Mg_neutral_1() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Mg");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.DOUBLE);
        mol.addBond(b1);

        String[] expectedTypes = {"Mg.neutral.1", "C.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Gd_3plus() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Gd");
        a1.setFormalCharge(3);
        mol.addAtom(a1);

        String[] expectedTypes = {"Gd.3plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Mo_4() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Mo");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.DOUBLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.DOUBLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a1, a5, IBond.Order.SINGLE);
        mol.addBond(b4);

        String[] expectedTypes = {"Mo.4", "C.sp2", "C.sp2", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Mo_metallic() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Mo");
        a1.setFormalCharge(0);
        mol.addAtom(a1);

        String[] expectedTypes = {"Mo.metallic"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Pt_2() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Pt");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);

        String[] expectedTypes = {"Pt.2", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Pt_2plus_4() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Pt");
        a1.setFormalCharge(2);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a1, a5, IBond.Order.SINGLE);
        mol.addBond(b4);

        String[] expectedTypes = {"Pt.2plus.4", "C.sp3", "C.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Cu_metallic() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Cu");
        a1.setFormalCharge(0);
        mol.addAtom(a1);

        String[] expectedTypes = {"Cu.metallic"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Cu_plus() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Cu");
        a1.setFormalCharge(1);
        mol.addAtom(a1);

        String[] expectedTypes = {"Cu.plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Cu_1() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Cu");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);

        String[] expectedTypes = {"Cu.1", "C.sp3",};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Ra() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Ra");
        a1.setFormalCharge(0);
        mol.addAtom(a1);

        String[] expectedTypes = {"Ra.neutral"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Cr_neutral() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Cr");
        a1.setFormalCharge(0);
        mol.addAtom(a1);

        String[] expectedTypes = {"Cr.neutral"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Rb_neutral() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Rb");
        a1.setFormalCharge(0);
        mol.addAtom(a1);

        String[] expectedTypes = {"Rb.neutral"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Rb_plus() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Rb");
        a1.setFormalCharge(1);
        mol.addAtom(a1);

        String[] expectedTypes = {"Rb.plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Cr_4() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Cr");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.DOUBLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.DOUBLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a1, a5, IBond.Order.SINGLE);
        mol.addBond(b4);

        String[] expectedTypes = {"Cr.4", "C.sp2", "C.sp2", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Cr_3plus() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Cr");
        a1.setFormalCharge(3);
        mol.addAtom(a1);

        String[] expectedTypes = {"Cr.3plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Cr_6plus() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Cr");
        a1.setFormalCharge(6);
        mol.addAtom(a1);

        String[] expectedTypes = {"Cr.6plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Ba_2plus() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Ba");
        a1.setFormalCharge(2);
        mol.addAtom(a1);

        String[] expectedTypes = {"Ba.2plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Au_1() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "C");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "Au");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);

        String[] expectedTypes = {"C.sp3", "Au.1"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Ag_neutral() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Ag");
        a1.setFormalCharge(0);
        mol.addAtom(a1);

        String[] expectedTypes = {"Ag.neutral"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * For example PubChem CID 3808730.
     */
    @Test
    public void test_Ag_plus() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Ag");
        a1.setFormalCharge(1);
        mol.addAtom(a1);

        String[] expectedTypes = {"Ag.plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * For example PubChem CID 139654.
     */
    @Test
    public void test_Ag_covalent() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Ag");
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "Cl");
        mol.addAtom(a2);
        mol.addBond(0, 1, IBond.Order.SINGLE);

        String[] expectedTypes = {"Ag.1", "Cl"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_In_3plus() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "In");
        a1.setFormalCharge(3);
        mol.addAtom(a1);

        String[] expectedTypes = {"In.3plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_In_3() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "In");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);

        String[] expectedTypes = {"In.3", "C.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_In_1() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "In");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.TRIPLE);
        mol.addBond(b1);

        String[] expectedTypes = {"In.1", "C.sp"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_In() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "In");
        a1.setFormalCharge(0);
        mol.addAtom(a1);

        String[] expectedTypes = {"In"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Cd_2plus() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Cd");
        a1.setFormalCharge(2);
        mol.addAtom(a1);

        String[] expectedTypes = {"Cd.2plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Cd_2() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Cd");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);

        String[] expectedTypes = {"Cd.2", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Cd_metallic() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Cd");
        a1.setFormalCharge(0);
        mol.addAtom(a1);

        String[] expectedTypes = {"Cd.metallic"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Pu() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Pu");
        a1.setFormalCharge(0);
        mol.addAtom(a1);

        String[] expectedTypes = {"Pu"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Th() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Th");
        a1.setFormalCharge(0);
        mol.addAtom(a1);

        String[] expectedTypes = {"Th"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Ge_3() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "C");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "Ge");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a2, a3, IBond.Order.DOUBLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a2, a4, IBond.Order.SINGLE);
        mol.addBond(b3);

        String[] expectedTypes = {"C.sp3", "Ge.3", "C.sp2", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Na_neutral() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Na");
        a1.setFormalCharge(0);
        mol.addAtom(a1);

        String[] expectedTypes = {"Na.neutral"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Mn_3plus() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Mn");
        a1.setFormalCharge(3);
        mol.addAtom(a1);

        String[] expectedTypes = {"Mn.3plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Mn_2() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Mn");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);

        String[] expectedTypes = {"Mn.2", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Mn_metallic() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Mn");
        a1.setFormalCharge(0);
        mol.addAtom(a1);

        String[] expectedTypes = {"Mn.metallic"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Si_2minus_6() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Si");
        a1.setFormalCharge(-2);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IAtom a6 = builder.newInstance(IAtom.class, "C");
        a6.setFormalCharge(0);
        mol.addAtom(a6);
        IAtom a7 = builder.newInstance(IAtom.class, "C");
        a7.setFormalCharge(0);
        mol.addAtom(a7);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a1, a5, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = builder.newInstance(IBond.class, a1, a6, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = builder.newInstance(IBond.class, a1, a7, IBond.Order.SINGLE);
        mol.addBond(b6);

        String[] expectedTypes = {"Si.2minus.6", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Si_3() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Si");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.DOUBLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);

        String[] expectedTypes = {"Si.3", "C.sp2", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Si_2() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Si");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.DOUBLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.DOUBLE);
        mol.addBond(b2);

        String[] expectedTypes = {"Si.2", "C.sp2", "C.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_As_minus() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "As");
        a1.setFormalCharge(-1);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IAtom a6 = builder.newInstance(IAtom.class, "C");
        a6.setFormalCharge(0);
        mol.addAtom(a6);
        IAtom a7 = builder.newInstance(IAtom.class, "C");
        a7.setFormalCharge(0);
        mol.addAtom(a7);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a1, a5, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = builder.newInstance(IBond.class, a1, a6, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = builder.newInstance(IBond.class, a1, a7, IBond.Order.SINGLE);
        mol.addBond(b6);

        String[] expectedTypes = {"As.minus", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_As_3plus() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "As");
        a1.setFormalCharge(3);
        mol.addAtom(a1);

        String[] expectedTypes = {"As.3plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_As_2() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "C");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "As");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a2, a3, IBond.Order.DOUBLE);
        mol.addBond(b2);

        String[] expectedTypes = {"C.sp3", "As.2", "C.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_As_5() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "As");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a1, a5, IBond.Order.DOUBLE);
        mol.addBond(b4);

        String[] expectedTypes = {"As.5", "C.sp3", "C.sp3", "C.sp3", "C.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Fe_metallic() throws Exception {
        String molName = "Fe_metallic";
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Fe");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        String[] expectedTypes = {"Fe.metallic"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Fe_plus() throws Exception {
        String molName1 = "Fe_plus";
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "C");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "Fe");
        a3.setFormalCharge(1);
        mol.addAtom(a3);
        IBond b1 = builder.newInstance(IBond.class, a2, a3, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);

        String[] expectedTypes1 = {"C.sp3", "C.sp3", "Fe.plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes1, mol);
    }

    @Test
    public void test_Fe_4() throws Exception {
        String molName2 = "Fe_4";
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "C");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "Fe");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IBond b1 = builder.newInstance(IBond.class, a2, a3, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a3, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a3, a5, IBond.Order.SINGLE);
        mol.addBond(b4);
        String[] expectedTypes2 = {"C.sp3", "C.sp3", "Fe.4", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes2, mol);
    }

    @Test
    public void test_Fe_3minus() throws Exception {
        String molName3 = "Fe_3minus";
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Fe");
        a1.setFormalCharge(-3);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IAtom a6 = builder.newInstance(IAtom.class, "C");
        a6.setFormalCharge(0);
        mol.addAtom(a6);
        IAtom a7 = builder.newInstance(IAtom.class, "C");
        a7.setFormalCharge(0);
        mol.addAtom(a7);
        IBond b1 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a5, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a6, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = builder.newInstance(IBond.class, a1, a7, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b6);
        String[] expectedTypes3 = {"Fe.3minus", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes3, mol);
    }

    @Test
    public void test_Fe_2plus() throws Exception {
        String molName4 = "Fe_2plus";
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Fe");
        a1.setFormalCharge(2);
        mol.addAtom(a1);
        String[] expectedTypes4 = {"Fe.2plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes4, mol);
    }

    @Test
    public void test_Fe_4minus() throws Exception {
        String molName5 = "Fe_4minus";
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Fe");
        a1.setFormalCharge(-4);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IAtom a6 = builder.newInstance(IAtom.class, "C");
        a6.setFormalCharge(0);
        mol.addAtom(a6);
        IAtom a7 = builder.newInstance(IAtom.class, "C");
        a7.setFormalCharge(0);
        mol.addAtom(a7);
        IBond b1 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a5, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a6, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = builder.newInstance(IBond.class, a1, a7, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b6);

        String[] expectedTypes5 = {"Fe.4minus", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes5, mol);
    }

    @Test
    public void test_Fe_5() throws Exception {
        String molNameFe5 = "Fe_5";
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Fe");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IAtom a6 = builder.newInstance(IAtom.class, "C");
        a6.setFormalCharge(0);
        mol.addAtom(a6);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a1, a5, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = builder.newInstance(IBond.class, a1, a6, IBond.Order.SINGLE);
        mol.addBond(b5);
        String[] expectedTypesFe5 = {"Fe.5", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypesFe5, mol);
    }

    @Test
    public void test_Fe_6() throws Exception {
        String molName7 = "Fe_6";
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Fe");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IAtom a6 = builder.newInstance(IAtom.class, "C");
        a6.setFormalCharge(0);
        mol.addAtom(a6);
        IAtom a7 = builder.newInstance(IAtom.class, "C");
        a7.setFormalCharge(0);
        mol.addAtom(a7);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a1, a5, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = builder.newInstance(IBond.class, a1, a6, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = builder.newInstance(IBond.class, a1, a7, IBond.Order.SINGLE);
        mol.addBond(b6);
        String[] expectedTypes7 = {"Fe.6", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes7, mol);
    }

    @Test
    public void test_Fe_2minus() throws Exception {
        String molName8 = "Fe_2minus";
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Fe");
        a1.setFormalCharge(-2);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IAtom a6 = builder.newInstance(IAtom.class, "C");
        a6.setFormalCharge(0);
        mol.addAtom(a6);
        IAtom a7 = builder.newInstance(IAtom.class, "C");
        a7.setFormalCharge(0);
        mol.addAtom(a7);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a1, a5, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = builder.newInstance(IBond.class, a1, a6, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = builder.newInstance(IBond.class, a1, a7, IBond.Order.SINGLE);
        mol.addBond(b6);
        String[] expectedTypes8 = {"Fe.2minus", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes8, mol);
    }

    @Test
    public void test_Fe_3plus() throws Exception {
        String molName9 = "Fe_3plus";
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Fe");
        a1.setFormalCharge(3);
        mol.addAtom(a1);
        String[] expectedTypes9 = {"Fe.3plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes9, mol);
    }

    @Test
    public void test_Fe_2() throws Exception {
        String molNameA = "Fe_2";
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "C");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "Fe");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a2, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        String[] expectedTypesA = {"C.sp3", "Fe.2", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypesA, mol);
    }

    @Test
    public void test_Fe_3() throws Exception {
        String molNameB = "Fe_3";
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Fe");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        String[] expectedTypesB = {"Fe.3", "C.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypesB, mol);
    }

    /**
     * @cdk.inchi InChI=1S/C8H16S/c1-6-3-8-4-7(6)5-9(8)2/h6-9H,3-5H2,1-2H3/t6-,7-,8+/m0/s1
     */
    @Test
    public void testSulphur4() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "S");
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a5);
        IAtom a6 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a6);
        IAtom a7 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a7);
        IAtom a8 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a8);
        IAtom a9 = builder.newInstance(IAtom.class, "C");
        mol.addAtom(a9);
        IBond b1 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a6, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a8, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b5 = builder.newInstance(IBond.class, a2, a4, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = builder.newInstance(IBond.class, a2, a7, IBond.Order.SINGLE);
        mol.addBond(b6);
        IBond b8 = builder.newInstance(IBond.class, a3, a4, IBond.Order.SINGLE);
        mol.addBond(b8);
        IBond b9 = builder.newInstance(IBond.class, a3, a5, IBond.Order.SINGLE);
        mol.addBond(b9);
        IBond b10 = builder.newInstance(IBond.class, a3, a6, IBond.Order.SINGLE);
        mol.addBond(b10);
        IBond b14 = builder.newInstance(IBond.class, a5, a7, IBond.Order.SINGLE);
        mol.addBond(b14);
        IBond b15 = builder.newInstance(IBond.class, a5, a9, IBond.Order.SINGLE);
        mol.addBond(b15);

        String[] expectedTypes = {"S.anyl", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * One of the ruthenium atom types in ruthenium red (CHEBI:34956).
     */
    @Test
    public void test_Ru_3minus_6() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Ru");
        a1.setFormalCharge(-3);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "N");
        a2.setFormalCharge(+1);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "N");
        a3.setFormalCharge(+1);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "N");
        a4.setFormalCharge(+1);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "N");
        a5.setFormalCharge(+1);
        mol.addAtom(a5);
        IAtom a6 = builder.newInstance(IAtom.class, "N");
        a6.setFormalCharge(+1);
        mol.addAtom(a6);
        IAtom a7 = builder.newInstance(IAtom.class, "O");
        a7.setFormalCharge(0);
        mol.addAtom(a7);
        IBond b1 = builder.newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a6, a1, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a1, a7, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = builder.newInstance(IBond.class, a1, a5, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b6);

        String[] expectedTypes = {"Ru.3minus.6", "N.plus", "N.plus", "N.plus", "N.plus", "N.plus", "O.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * One of the ruthenium atom types in ruthenium red (CHEBI:34956).
     */
    @Test
    public void test_Ru_2minus_6() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Ru");
        a1.setFormalCharge(-2);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "N");
        a2.setFormalCharge(+1);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "N");
        a3.setFormalCharge(+1);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "N");
        a4.setFormalCharge(+1);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "N");
        a5.setFormalCharge(+1);
        mol.addAtom(a5);
        IAtom a6 = builder.newInstance(IAtom.class, "O");
        a6.setFormalCharge(0);
        mol.addAtom(a6);
        IAtom a7 = builder.newInstance(IAtom.class, "O");
        a7.setFormalCharge(0);
        mol.addAtom(a7);
        IBond b1 = builder.newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a6, a1, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a1, a7, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = builder.newInstance(IBond.class, a1, a5, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b6);

        String[] expectedTypes = {"Ru.2minus.6", "N.plus", "N.plus", "N.plus", "N.plus", "O.sp3", "O.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Ru_10plus_6() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Ru");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IAtom a6 = builder.newInstance(IAtom.class, "C");
        a6.setFormalCharge(0);
        mol.addAtom(a6);
        IAtom a7 = builder.newInstance(IAtom.class, "C");
        a7.setFormalCharge(0);
        mol.addAtom(a7);
        IBond b1 = builder.newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a6, a1, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a1, a7, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = builder.newInstance(IBond.class, a1, a5, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b6);

        String[] expectedTypes = {"Ru.6", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void test_Ru_6() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "Ru");
        a1.setFormalCharge(0);
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "C");
        a2.setFormalCharge(0);
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "C");
        a3.setFormalCharge(0);
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "C");
        a4.setFormalCharge(0);
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "C");
        a5.setFormalCharge(0);
        mol.addAtom(a5);
        IAtom a6 = builder.newInstance(IAtom.class, "C");
        a6.setFormalCharge(0);
        mol.addAtom(a6);
        IAtom a7 = builder.newInstance(IAtom.class, "C");
        a7.setFormalCharge(0);
        mol.addAtom(a7);
        IBond b1 = builder.newInstance(IBond.class, a1, a5, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a1, a6, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a7, a1, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a1, a2, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = builder.newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = builder.newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b6);

        String[] expectedTypes = {"Ru.6", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * @cdk.inchi InChI=1S/C4H5N/c1-2-4-5-3-1/h1-5H
     * @throws Exception
     */
    @Test
    public void test_n_planar3_sp2_aromaticity() throws Exception {

        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();

        // simulate an IAtomContainer returned from a SDFile with bond order 4 to indicate aromaticity
        IAtomContainer pyrrole = builder.newInstance(IAtomContainer.class);

        IAtom n1 = builder.newInstance(IAtom.class, "N");
        IAtom c2 = builder.newInstance(IAtom.class, "C");
        IAtom c3 = builder.newInstance(IAtom.class, "C");
        IAtom c4 = builder.newInstance(IAtom.class, "C");
        IAtom c5 = builder.newInstance(IAtom.class, "C");

        IBond b1 = builder.newInstance(IBond.class, n1, c2, IBond.Order.SINGLE);
        b1.setFlag(CDKConstants.ISAROMATIC, true);
        IBond b2 = builder.newInstance(IBond.class, c2, c3, IBond.Order.SINGLE);
        b2.setFlag(CDKConstants.ISAROMATIC, true);
        IBond b3 = builder.newInstance(IBond.class, c3, c4, IBond.Order.SINGLE);
        b3.setFlag(CDKConstants.ISAROMATIC, true);
        IBond b4 = builder.newInstance(IBond.class, c4, c5, IBond.Order.SINGLE);
        b4.setFlag(CDKConstants.ISAROMATIC, true);
        IBond b5 = builder.newInstance(IBond.class, c5, n1, IBond.Order.SINGLE);
        b5.setFlag(CDKConstants.ISAROMATIC, true);

        pyrrole.addAtom(n1);
        pyrrole.addAtom(c2);
        pyrrole.addAtom(c3);
        pyrrole.addAtom(c4);
        pyrrole.addAtom(c5);
        pyrrole.addBond(b1);
        pyrrole.addBond(b2);
        pyrrole.addBond(b3);
        pyrrole.addBond(b4);
        pyrrole.addBond(b5);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(pyrrole);

        Assert.assertEquals(pyrrole.getAtom(0).getHybridization().name(), "PLANAR3");
    }

    /**
     * @cdk.inchi InChI=1S/C4H5N/c1-2-4-5-3-1/h1-5H
     * @throws Exception
     */
    @Test
    public void test_n_planar3_sp2_aromaticity_explicitH() throws Exception {

        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();

        // simulate an IAtomContainer returned from a SDFile with bond order 4 to indicate aromaticity
        IAtomContainer pyrrole = builder.newInstance(IAtomContainer.class);

        IAtom n1 = builder.newInstance(IAtom.class, "N");
        IAtom c2 = builder.newInstance(IAtom.class, "C");
        IAtom c3 = builder.newInstance(IAtom.class, "C");
        IAtom c4 = builder.newInstance(IAtom.class, "C");
        IAtom c5 = builder.newInstance(IAtom.class, "C");

        IBond b1 = builder.newInstance(IBond.class, n1, c2, IBond.Order.SINGLE);
        b1.setFlag(CDKConstants.ISAROMATIC, true);
        IBond b2 = builder.newInstance(IBond.class, c2, c3, IBond.Order.SINGLE);
        b2.setFlag(CDKConstants.ISAROMATIC, true);
        IBond b3 = builder.newInstance(IBond.class, c3, c4, IBond.Order.SINGLE);
        b3.setFlag(CDKConstants.ISAROMATIC, true);
        IBond b4 = builder.newInstance(IBond.class, c4, c5, IBond.Order.SINGLE);
        b4.setFlag(CDKConstants.ISAROMATIC, true);
        IBond b5 = builder.newInstance(IBond.class, c5, n1, IBond.Order.SINGLE);
        b5.setFlag(CDKConstants.ISAROMATIC, true);

        pyrrole.addAtom(n1);
        pyrrole.addAtom(c2);
        pyrrole.addAtom(c3);
        pyrrole.addAtom(c4);
        pyrrole.addAtom(c5);
        pyrrole.addBond(b1);
        pyrrole.addBond(b2);
        pyrrole.addBond(b3);
        pyrrole.addBond(b4);
        pyrrole.addBond(b5);

        // add explicit hydrogens
        IAtom h1 = builder.newInstance(IAtom.class, "H");
        IAtom h2 = builder.newInstance(IAtom.class, "H");
        IAtom h3 = builder.newInstance(IAtom.class, "H");
        IAtom h4 = builder.newInstance(IAtom.class, "H");
        IAtom h5 = builder.newInstance(IAtom.class, "H");
        pyrrole.addAtom(h1);
        pyrrole.addAtom(h2);
        pyrrole.addAtom(h3);
        pyrrole.addAtom(h4);
        pyrrole.addAtom(h5);
        pyrrole.addBond(builder.newInstance(IBond.class, n1, h1));
        pyrrole.addBond(builder.newInstance(IBond.class, c2, h2));
        pyrrole.addBond(builder.newInstance(IBond.class, c3, h3));
        pyrrole.addBond(builder.newInstance(IBond.class, c4, h4));
        pyrrole.addBond(builder.newInstance(IBond.class, c5, h5));

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(pyrrole);

        Assert.assertEquals(pyrrole.getAtom(0).getHybridization().name(), "PLANAR3");
    }

    @AfterClass
    public static void testTestedAtomTypes() throws Exception {
        countTestedAtomTypes(testedAtomTypes, factory);
    }

}
