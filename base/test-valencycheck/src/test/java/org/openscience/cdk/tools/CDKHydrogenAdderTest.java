/* Copyright (C) 2007  Egon Willighagen <egonw@sci.kun.nl>
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
package org.openscience.cdk.tools;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.vecmath.Point2d;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests CDK's hydrogen adding capabilities in terms of
 * example molecules.
 *
 * @cdk.module  test-valencycheck
 *
 * @author Egon Willighagen &lt;egonw@users.sf.net&gt;
 * @cdk.created 2007-07-28
 */
class CDKHydrogenAdderTest extends CDKTestCase {

    private final static CDKHydrogenAdder   adder   = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder
                                                            .getInstance());
    private final static CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(SilentChemObjectBuilder
                                                            .getInstance());

    @Test
    void testInstance() {
        Assertions.assertNotNull(adder);
    }

    @Test
    void testMethane() throws Exception {
        IAtomContainer molecule = SilentChemObjectBuilder.getInstance().newAtomContainer();
        IAtom newAtom = new Atom(Elements.CARBON);
        molecule.addAtom(newAtom);
        IAtomType type = matcher.findMatchingAtomType(molecule, newAtom);
        Assertions.assertNotNull(type);
        AtomTypeManipulator.configure(newAtom, type);

        Assertions.assertNull(newAtom.getImplicitHydrogenCount());
        adder.addImplicitHydrogens(molecule);
        Assertions.assertNotNull(newAtom.getImplicitHydrogenCount());
        Assertions.assertEquals(4, newAtom.getImplicitHydrogenCount().intValue());
    }

    @Test
    void testFormaldehyde() throws Exception {
        IAtomContainer molecule = SilentChemObjectBuilder.getInstance().newAtomContainer();
        IAtom newAtom = new Atom(Elements.CARBON);
        IAtom newAtom2 = new Atom(Elements.OXYGEN);
        molecule.addAtom(newAtom);
        molecule.addAtom(newAtom2);
        molecule.addBond(0, 1, Order.DOUBLE);
        IAtomType type = matcher.findMatchingAtomType(molecule, newAtom);
        Assertions.assertNotNull(type);
        AtomTypeManipulator.configure(newAtom, type);
        type = matcher.findMatchingAtomType(molecule, newAtom2);
        Assertions.assertNotNull(type);
        AtomTypeManipulator.configure(newAtom2, type);

        Assertions.assertNull(newAtom.getImplicitHydrogenCount());
        adder.addImplicitHydrogens(molecule);
        Assertions.assertNotNull(newAtom.getImplicitHydrogenCount());
        Assertions.assertNotNull(newAtom2.getImplicitHydrogenCount());
        Assertions.assertEquals(2, newAtom.getImplicitHydrogenCount().intValue());
        Assertions.assertEquals(0, newAtom2.getImplicitHydrogenCount().intValue());
    }

    @Test
    void testMethanol() throws Exception {
        IAtomContainer molecule = SilentChemObjectBuilder.getInstance().newAtomContainer();
        IAtom newAtom = new Atom(Elements.CARBON);
        IAtom newAtom2 = new Atom(Elements.OXYGEN);
        molecule.addAtom(newAtom);
        molecule.addAtom(newAtom2);
        molecule.addBond(0, 1, Order.SINGLE);
        IAtomType type = matcher.findMatchingAtomType(molecule, newAtom);
        Assertions.assertNotNull(type);
        AtomTypeManipulator.configure(newAtom, type);
        type = matcher.findMatchingAtomType(molecule, newAtom2);
        Assertions.assertNotNull(type);
        AtomTypeManipulator.configure(newAtom2, type);

        Assertions.assertNull(newAtom.getImplicitHydrogenCount());
        adder.addImplicitHydrogens(molecule);
        Assertions.assertNotNull(newAtom.getImplicitHydrogenCount());
        Assertions.assertNotNull(newAtom2.getImplicitHydrogenCount());
        Assertions.assertEquals(3, newAtom.getImplicitHydrogenCount().intValue());
        Assertions.assertEquals(1, newAtom2.getImplicitHydrogenCount().intValue());
    }

    @Test
    void testHCN() throws Exception {
        IAtomContainer molecule = SilentChemObjectBuilder.getInstance().newAtomContainer();
        IAtom newAtom = new Atom(Elements.CARBON);
        IAtom newAtom2 = new Atom(Elements.NITROGEN);
        molecule.addAtom(newAtom);
        molecule.addAtom(newAtom2);
        molecule.addBond(0, 1, Order.TRIPLE);
        IAtomType type = matcher.findMatchingAtomType(molecule, newAtom);
        Assertions.assertNotNull(type);
        AtomTypeManipulator.configure(newAtom, type);
        type = matcher.findMatchingAtomType(molecule, newAtom2);
        Assertions.assertNotNull(type);
        AtomTypeManipulator.configure(newAtom2, type);

        Assertions.assertNull(newAtom.getImplicitHydrogenCount());
        adder.addImplicitHydrogens(molecule);
        Assertions.assertNotNull(newAtom.getImplicitHydrogenCount());
        Assertions.assertNotNull(newAtom2.getImplicitHydrogenCount());
        Assertions.assertEquals(1, newAtom.getImplicitHydrogenCount().intValue());
        Assertions.assertEquals(0, newAtom2.getImplicitHydrogenCount().intValue());
    }

    @Test
    void testMethylAmine() throws Exception {
        IAtomContainer molecule = SilentChemObjectBuilder.getInstance().newAtomContainer();
        IAtom newAtom = new Atom(Elements.CARBON);
        IAtom newAtom2 = new Atom(Elements.NITROGEN);
        molecule.addAtom(newAtom);
        molecule.addAtom(newAtom2);
        molecule.addBond(0, 1, Order.SINGLE);
        IAtomType type = matcher.findMatchingAtomType(molecule, newAtom);
        Assertions.assertNotNull(type);
        AtomTypeManipulator.configure(newAtom, type);
        type = matcher.findMatchingAtomType(molecule, newAtom2);
        Assertions.assertNotNull(type);
        AtomTypeManipulator.configure(newAtom2, type);

        Assertions.assertNull(newAtom.getImplicitHydrogenCount());
        adder.addImplicitHydrogens(molecule);
        Assertions.assertNotNull(newAtom.getImplicitHydrogenCount());
        Assertions.assertNotNull(newAtom2.getImplicitHydrogenCount());
        Assertions.assertEquals(3, newAtom.getImplicitHydrogenCount().intValue());
        Assertions.assertEquals(2, newAtom2.getImplicitHydrogenCount().intValue());
    }

    @Test
    void testMethyleneImine() throws Exception {
        IAtomContainer molecule = SilentChemObjectBuilder.getInstance().newAtomContainer();
        IAtom newAtom = new Atom(Elements.CARBON);
        IAtom newAtom2 = new Atom(Elements.NITROGEN);
        molecule.addAtom(newAtom);
        molecule.addAtom(newAtom2);
        molecule.addBond(0, 1, Order.DOUBLE);
        IAtomType type = matcher.findMatchingAtomType(molecule, newAtom);
        Assertions.assertNotNull(type);
        AtomTypeManipulator.configure(newAtom, type);
        type = matcher.findMatchingAtomType(molecule, newAtom2);
        Assertions.assertNotNull(type);
        AtomTypeManipulator.configure(newAtom2, type);

        Assertions.assertNull(newAtom.getImplicitHydrogenCount());
        adder.addImplicitHydrogens(molecule);
        Assertions.assertNotNull(newAtom.getImplicitHydrogenCount());
        Assertions.assertNotNull(newAtom2.getImplicitHydrogenCount());
        Assertions.assertEquals(2, newAtom.getImplicitHydrogenCount().intValue());
        Assertions.assertEquals(1, newAtom2.getImplicitHydrogenCount().intValue());
    }

    @Test
    void testSulphur() throws Exception {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        Atom atom = new Atom("S");
        mol.addAtom(atom);
        IAtomType type = matcher.findMatchingAtomType(mol, atom);
        Assertions.assertNotNull(type);
        AtomTypeManipulator.configure(atom, type);

        Assertions.assertNotEquals((Integer) 2, atom.getImplicitHydrogenCount());
        adder.addImplicitHydrogens(mol);
        Assertions.assertEquals(1, mol.getAtomCount());
        Assertions.assertNotNull(atom.getImplicitHydrogenCount());
        Assertions.assertEquals(2, atom.getImplicitHydrogenCount().intValue());
    }

    @Test
    void testProton() throws Exception {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        Atom proton = new Atom("H");
        proton.setFormalCharge(+1);
        mol.addAtom(proton);
        IAtomType type = matcher.findMatchingAtomType(mol, proton);
        Assertions.assertNotNull(type);
        AtomTypeManipulator.configure(proton, type);

        adder.addImplicitHydrogens(mol);

        Assertions.assertEquals(1, mol.getAtomCount());
        IMolecularFormula formula = MolecularFormulaManipulator.getMolecularFormula(mol);
        Assertions.assertEquals(1, MolecularFormulaManipulator.getElementCount(formula, mol.getBuilder().newInstance(IElement.class, "H")));
        Assertions.assertEquals(0, mol.getConnectedBondsCount(proton));
        Assertions.assertNotNull(proton.getImplicitHydrogenCount());
        Assertions.assertEquals(0, proton.getImplicitHydrogenCount().intValue());
    }

    @Test
    void testHydrogen() throws Exception {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        Atom proton = new Atom("H");
        mol.addAtom(proton);
        IAtomType type = matcher.findMatchingAtomType(mol, proton);
        Assertions.assertNotNull(type);
        AtomTypeManipulator.configure(proton, type);

        adder.addImplicitHydrogens(mol);

        Assertions.assertEquals(1, mol.getAtomCount());
        IMolecularFormula formula = MolecularFormulaManipulator.getMolecularFormula(mol);
        Assertions.assertEquals(2, MolecularFormulaManipulator.getElementCount(formula, mol.getBuilder().newInstance(IElement.class, "H")));
        Assertions.assertEquals(0, mol.getConnectedBondsCount(proton));
        Assertions.assertNotNull(proton.getImplicitHydrogenCount());
        Assertions.assertEquals(1, proton.getImplicitHydrogenCount().intValue());
    }

    @Test
    void testAmmonia() throws Exception {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        Atom nitrogen = new Atom("N");
        mol.addAtom(nitrogen);
        IAtomType type = matcher.findMatchingAtomType(mol, nitrogen);
        Assertions.assertNotNull(type);
        AtomTypeManipulator.configure(nitrogen, type);

        adder.addImplicitHydrogens(mol);
        Assertions.assertNotNull(nitrogen.getImplicitHydrogenCount());
        Assertions.assertEquals(3, nitrogen.getImplicitHydrogenCount().intValue());
    }

    @Test
    void testAmmonium() throws Exception {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        Atom nitrogen = new Atom("N");
        nitrogen.setFormalCharge(+1);
        mol.addAtom(nitrogen);
        IAtomType type = matcher.findMatchingAtomType(mol, nitrogen);
        Assertions.assertNotNull(type);
        AtomTypeManipulator.configure(nitrogen, type);

        adder.addImplicitHydrogens(mol);
        Assertions.assertNotNull(nitrogen.getImplicitHydrogenCount());
        Assertions.assertEquals(4, nitrogen.getImplicitHydrogenCount().intValue());
    }

    @Test
    void testWater() throws Exception {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        Atom oxygen = new Atom("O");
        mol.addAtom(oxygen);
        IAtomType type = matcher.findMatchingAtomType(mol, oxygen);
        Assertions.assertNotNull(type);
        AtomTypeManipulator.configure(oxygen, type);

        adder.addImplicitHydrogens(mol);
        Assertions.assertNotNull(oxygen.getImplicitHydrogenCount());
        Assertions.assertEquals(2, oxygen.getImplicitHydrogenCount().intValue());
    }

    @Test
    void testHydroxonium() throws Exception {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        Atom oxygen = new Atom("O");
        oxygen.setFormalCharge(+1);
        mol.addAtom(oxygen);
        IAtomType type = matcher.findMatchingAtomType(mol, oxygen);
        Assertions.assertNotNull(type);
        AtomTypeManipulator.configure(oxygen, type);

        adder.addImplicitHydrogens(mol);
        Assertions.assertNotNull(oxygen.getImplicitHydrogenCount());
        Assertions.assertEquals(3, oxygen.getImplicitHydrogenCount().intValue());
    }

    @Test
    void testHydroxyl() throws Exception {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        Atom oxygen = new Atom("O");
        oxygen.setFormalCharge(-1);
        mol.addAtom(oxygen);
        IAtomType type = matcher.findMatchingAtomType(mol, oxygen);
        Assertions.assertNotNull(type);
        AtomTypeManipulator.configure(oxygen, type);

        adder.addImplicitHydrogens(mol);
        Assertions.assertNotNull(oxygen.getImplicitHydrogenCount());
        Assertions.assertEquals(1, oxygen.getImplicitHydrogenCount().intValue());
    }

    @Test
    void testHalogens() throws Exception {
        halogenTest("I");
        halogenTest("F");
        halogenTest("Cl");
        halogenTest("Br");
    }

    @Test
    void testHalogenAnions() throws Exception {
        negativeHalogenTest("I");
        negativeHalogenTest("F");
        negativeHalogenTest("Cl");
        negativeHalogenTest("Br");
    }

    private void halogenTest(String halogen) throws Exception {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        Atom atom = new Atom(halogen);
        mol.addAtom(atom);
        IAtomType type = matcher.findMatchingAtomType(mol, atom);
        Assertions.assertNotNull(type);
        AtomTypeManipulator.configure(atom, type);

        adder.addImplicitHydrogens(mol);
        Assertions.assertEquals(1, mol.getAtomCount());
        Assertions.assertNotNull(atom.getImplicitHydrogenCount());
        Assertions.assertEquals(1, atom.getImplicitHydrogenCount().intValue());
    }

    private void negativeHalogenTest(String halogen) throws Exception {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        Atom atom = new Atom(halogen);
        atom.setFormalCharge(-1);
        mol.addAtom(atom);
        IAtomType type = matcher.findMatchingAtomType(mol, atom);
        Assertions.assertNotNull(type);
        AtomTypeManipulator.configure(atom, type);

        adder.addImplicitHydrogens(mol);
        Assertions.assertEquals(1, mol.getAtomCount());
        Assertions.assertNotNull(atom.getImplicitHydrogenCount());
        Assertions.assertEquals(0, atom.getImplicitHydrogenCount().intValue());
    }

    @Test
    void testSulfite() throws Exception {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        Atom s = new Atom("S");
        Atom o1 = new Atom("O");
        Atom o2 = new Atom("O");
        Atom o3 = new Atom("O");
        mol.addAtom(s);
        mol.addAtom(o1);
        mol.addAtom(o2);
        mol.addAtom(o3);
        Bond b1 = new Bond(s, o1, IBond.Order.SINGLE);
        Bond b2 = new Bond(s, o2, IBond.Order.SINGLE);
        Bond b3 = new Bond(s, o3, IBond.Order.DOUBLE);
        mol.addBond(b1);
        mol.addBond(b2);
        mol.addBond(b3);
        IAtomType type = matcher.findMatchingAtomType(mol, s);
        Assertions.assertNotNull(type);
        AtomTypeManipulator.configure(s, type);
        type = matcher.findMatchingAtomType(mol, o1);
        Assertions.assertNotNull(type);
        AtomTypeManipulator.configure(o1, type);
        type = matcher.findMatchingAtomType(mol, o2);
        Assertions.assertNotNull(type);
        AtomTypeManipulator.configure(o2, type);
        type = matcher.findMatchingAtomType(mol, o3);
        Assertions.assertNotNull(type);
        AtomTypeManipulator.configure(o3, type);

        adder.addImplicitHydrogens(mol);

        Assertions.assertEquals(4, mol.getAtomCount());
        Assertions.assertEquals(3, mol.getBondCount());
        Assertions.assertNotNull(s.getImplicitHydrogenCount());
        Assertions.assertEquals(0, s.getImplicitHydrogenCount().intValue());
        Assertions.assertNotNull(o1.getImplicitHydrogenCount());
        Assertions.assertEquals(1, o1.getImplicitHydrogenCount().intValue());
        Assertions.assertNotNull(o2.getImplicitHydrogenCount());
        Assertions.assertEquals(1, o2.getImplicitHydrogenCount().intValue());
        Assertions.assertNotNull(o3.getImplicitHydrogenCount());
        Assertions.assertEquals(0, o3.getImplicitHydrogenCount().intValue());

    }

    @Test
    void testAceticAcid() throws Exception {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        Atom carbonylOxygen = new Atom("O");
        Atom hydroxylOxygen = new Atom("O");
        Atom methylCarbon = new Atom("C");
        Atom carbonylCarbon = new Atom("C");
        mol.addAtom(carbonylOxygen);
        mol.addAtom(hydroxylOxygen);
        mol.addAtom(methylCarbon);
        mol.addAtom(carbonylCarbon);
        Bond b1 = new Bond(methylCarbon, carbonylCarbon, IBond.Order.SINGLE);
        Bond b2 = new Bond(carbonylOxygen, carbonylCarbon, IBond.Order.DOUBLE);
        Bond b3 = new Bond(hydroxylOxygen, carbonylCarbon, IBond.Order.SINGLE);
        mol.addBond(b1);
        mol.addBond(b2);
        mol.addBond(b3);
        findAndConfigureAtomTypesForAllAtoms(mol);
        adder.addImplicitHydrogens(mol);

        Assertions.assertEquals(4, mol.getAtomCount());
        Assertions.assertEquals(3, mol.getBondCount());
        Assertions.assertEquals(0, carbonylOxygen.getImplicitHydrogenCount().intValue());
        Assertions.assertEquals(1, hydroxylOxygen.getImplicitHydrogenCount().intValue());
        Assertions.assertEquals(3, methylCarbon.getImplicitHydrogenCount().intValue());
        Assertions.assertEquals(0, carbonylCarbon.getImplicitHydrogenCount().intValue());
    }

    @Test
    void testEthane() throws Exception {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        Atom carbon1 = new Atom("C");
        Atom carbon2 = new Atom("C");
        Bond b = new Bond(carbon1, carbon2, IBond.Order.SINGLE);
        mol.addAtom(carbon1);
        mol.addAtom(carbon2);
        mol.addBond(b);
        findAndConfigureAtomTypesForAllAtoms(mol);
        adder.addImplicitHydrogens(mol);

        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertEquals(1, mol.getBondCount());
        Assertions.assertEquals(3, carbon1.getImplicitHydrogenCount().intValue());
        Assertions.assertEquals(3, carbon2.getImplicitHydrogenCount().intValue());
    }

    @Test
    void testEthaneWithPresetImplicitHCount() throws Exception {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        Atom carbon1 = new Atom("C");
        Atom carbon2 = new Atom("C");
        Bond b = new Bond(carbon1, carbon2, IBond.Order.SINGLE);
        mol.addAtom(carbon1);
        mol.addAtom(carbon2);
        mol.addBond(b);
        carbon1.setImplicitHydrogenCount(3);
        carbon2.setImplicitHydrogenCount(3);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);

        adder.addImplicitHydrogens(mol);

        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertEquals(1, mol.getBondCount());
        Assertions.assertEquals(3, carbon1.getImplicitHydrogenCount().intValue());
        Assertions.assertEquals(3, carbon2.getImplicitHydrogenCount().intValue());
    }

    @Test
    void testEthene() throws Exception {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        Atom carbon1 = new Atom("C");
        Atom carbon2 = new Atom("C");
        Bond b = new Bond(carbon1, carbon2, IBond.Order.DOUBLE);
        mol.addAtom(carbon1);
        mol.addAtom(carbon2);
        mol.addBond(b);
        findAndConfigureAtomTypesForAllAtoms(mol);
        adder.addImplicitHydrogens(mol);

        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertEquals(1, mol.getBondCount());
        Assertions.assertEquals(2, carbon1.getImplicitHydrogenCount().intValue());
        Assertions.assertEquals(2, carbon2.getImplicitHydrogenCount().intValue());
    }

    @Test
    void testEthyne() throws Exception {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        Atom carbon1 = new Atom("C");
        Atom carbon2 = new Atom("C");
        Bond b = new Bond(carbon1, carbon2, IBond.Order.TRIPLE);
        mol.addAtom(carbon1);
        mol.addAtom(carbon2);
        mol.addBond(b);
        findAndConfigureAtomTypesForAllAtoms(mol);
        adder.addImplicitHydrogens(mol);

        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertEquals(1, mol.getBondCount());
        Assertions.assertEquals(1, carbon1.getImplicitHydrogenCount().intValue());
        Assertions.assertEquals(1, carbon2.getImplicitHydrogenCount().intValue());
    }

    @Test
    void testAromaticSaturation() throws Exception {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5
        mol.addAtom(new Atom("C")); // 6
        mol.addAtom(new Atom("C")); // 7

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.SINGLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 5, IBond.Order.SINGLE); // 5
        mol.addBond(5, 0, IBond.Order.SINGLE); // 6
        mol.addBond(0, 6, IBond.Order.SINGLE); // 7
        mol.addBond(6, 7, IBond.Order.TRIPLE); // 8

        for (int f = 0; f < 6; f++) {
            mol.getAtom(f).setFlag(CDKConstants.ISAROMATIC, true);
            mol.getAtom(f).setHybridization(IAtomType.Hybridization.SP2);
            mol.getBond(f).setFlag(CDKConstants.ISAROMATIC, true);
        }
        findAndConfigureAtomTypesForAllAtoms(mol);
        adder.addImplicitHydrogens(mol);
        Assertions.assertEquals(6, AtomContainerManipulator.getTotalHydrogenCount(mol));
    }

    @Test
    void testaddImplicitHydrogensToSatisfyValency_OldValue() throws Exception {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(new Atom("C"));
        Atom oxygen = new Atom("O");
        mol.addAtom(oxygen);
        mol.addAtom(new Atom("C"));

        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addBond(1, 2, IBond.Order.SINGLE);

        findAndConfigureAtomTypesForAllAtoms(mol);
        adder.addImplicitHydrogens(mol);

        Assertions.assertNotNull(oxygen.getImplicitHydrogenCount());
        Assertions.assertEquals(0, oxygen.getImplicitHydrogenCount().intValue());
    }

    @Test
    void testAdenine() throws Exception {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer(); // Adenine
        IAtom a1 = mol.getBuilder().newInstance(IAtom.class, "C");
        a1.setPoint2d(new Point2d(21.0223, -17.2946));
        mol.addAtom(a1);
        IAtom a2 = mol.getBuilder().newInstance(IAtom.class, "C");
        a2.setPoint2d(new Point2d(21.0223, -18.8093));
        mol.addAtom(a2);
        IAtom a3 = mol.getBuilder().newInstance(IAtom.class, "C");
        a3.setPoint2d(new Point2d(22.1861, -16.6103));
        mol.addAtom(a3);
        IAtom a4 = mol.getBuilder().newInstance(IAtom.class, "N");
        a4.setPoint2d(new Point2d(19.8294, -16.8677));
        mol.addAtom(a4);
        IAtom a5 = mol.getBuilder().newInstance(IAtom.class, "N");
        a5.setPoint2d(new Point2d(22.2212, -19.5285));
        mol.addAtom(a5);
        IAtom a6 = mol.getBuilder().newInstance(IAtom.class, "N");
        a6.setPoint2d(new Point2d(19.8177, -19.2187));
        mol.addAtom(a6);
        IAtom a7 = mol.getBuilder().newInstance(IAtom.class, "N");
        a7.setPoint2d(new Point2d(23.4669, -17.3531));
        mol.addAtom(a7);
        IAtom a8 = mol.getBuilder().newInstance(IAtom.class, "N");
        a8.setPoint2d(new Point2d(22.1861, -15.2769));
        mol.addAtom(a8);
        IAtom a9 = mol.getBuilder().newInstance(IAtom.class, "C");
        a9.setPoint2d(new Point2d(18.9871, -18.0139));
        mol.addAtom(a9);
        IAtom a10 = mol.getBuilder().newInstance(IAtom.class, "C");
        a10.setPoint2d(new Point2d(23.4609, -18.8267));
        mol.addAtom(a10);
        IBond b1 = mol.getBuilder().newInstance(IBond.class, a1, a2, IBond.Order.DOUBLE);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newInstance(IBond.class, a1, a3, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newInstance(IBond.class, a1, a4, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = mol.getBuilder().newInstance(IBond.class, a2, a5, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = mol.getBuilder().newInstance(IBond.class, a2, a6, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = mol.getBuilder().newInstance(IBond.class, a3, a7, IBond.Order.DOUBLE);
        mol.addBond(b6);
        IBond b7 = mol.getBuilder().newInstance(IBond.class, a3, a8, IBond.Order.SINGLE);
        mol.addBond(b7);
        IBond b8 = mol.getBuilder().newInstance(IBond.class, a4, a9, IBond.Order.DOUBLE);
        mol.addBond(b8);
        IBond b9 = mol.getBuilder().newInstance(IBond.class, a5, a10, IBond.Order.DOUBLE);
        mol.addBond(b9);
        IBond b10 = mol.getBuilder().newInstance(IBond.class, a6, a9, IBond.Order.SINGLE);
        mol.addBond(b10);
        IBond b11 = mol.getBuilder().newInstance(IBond.class, a7, a10, IBond.Order.SINGLE);
        mol.addBond(b11);

        findAndConfigureAtomTypesForAllAtoms(mol);
        adder.addImplicitHydrogens(mol);
        Assertions.assertEquals(5, AtomContainerManipulator.getTotalHydrogenCount(mol));
    }

    /**
     * @cdk.bug 1727373
     *
     */
    @Test
    void testBug1727373() throws Exception {
        IAtomContainer molecule;
        String filename = "carbocations.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        molecule = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        findAndConfigureAtomTypesForAllAtoms(molecule);
        adder.addImplicitHydrogens(molecule);
        Assertions.assertEquals(2, molecule.getAtom(0).getImplicitHydrogenCount().intValue());
        Assertions.assertEquals(0, molecule.getAtom(1).getImplicitHydrogenCount().intValue());
        Assertions.assertEquals(1, molecule.getAtom(2).getImplicitHydrogenCount().intValue());
        Assertions.assertEquals(2, molecule.getAtom(3).getImplicitHydrogenCount().intValue());
    }

    /**
     * @cdk.bug 1575269
     */
    @Test
    void testBug1575269() throws Exception {
        String filename = "furan.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer molecule = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        findAndConfigureAtomTypesForAllAtoms(molecule);
        adder.addImplicitHydrogens(molecule);
        Assertions.assertEquals(1, molecule.getAtom(0).getImplicitHydrogenCount().intValue());
        Assertions.assertEquals(1, molecule.getAtom(1).getImplicitHydrogenCount().intValue());
        Assertions.assertEquals(1, molecule.getAtom(2).getImplicitHydrogenCount().intValue());
        Assertions.assertEquals(1, molecule.getAtom(3).getImplicitHydrogenCount().intValue());
    }

    @Test
    void testImpHByAtom() throws Exception {
        String filename = "furan.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer molecule = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        findAndConfigureAtomTypesForAllAtoms(molecule);
        for (IAtom atom : molecule.atoms()) {
            adder.addImplicitHydrogens(molecule, atom);
        }
        Assertions.assertEquals(1, molecule.getAtom(0).getImplicitHydrogenCount().intValue());
        Assertions.assertEquals(1, molecule.getAtom(1).getImplicitHydrogenCount().intValue());
        Assertions.assertEquals(1, molecule.getAtom(2).getImplicitHydrogenCount().intValue());
        Assertions.assertEquals(1, molecule.getAtom(3).getImplicitHydrogenCount().intValue());
    }

    @Test
    void testPseudoAtom() throws Exception {
        IAtomContainer molecule = SilentChemObjectBuilder.getInstance().newAtomContainer();
        molecule.addAtom(new PseudoAtom("Waterium"));
        findAndConfigureAtomTypesForAllAtoms(molecule);
        Assertions.assertNull(molecule.getAtom(0).getImplicitHydrogenCount());
    }

    @Test
    void testNaCl() throws Exception {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        Atom cl = new Atom("Cl");
        cl.setFormalCharge(-1);
        mol.addAtom(cl);
        Atom na = new Atom("Na");
        na.setFormalCharge(+1);
        mol.addAtom(na);
        findAndConfigureAtomTypesForAllAtoms(mol);
        adder.addImplicitHydrogens(mol);

        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertEquals(0, AtomContainerManipulator.getTotalHydrogenCount(mol));
        Assertions.assertEquals(0, mol.getConnectedBondsCount(cl));
        Assertions.assertEquals(0, cl.getImplicitHydrogenCount().intValue());
        Assertions.assertEquals(0, mol.getConnectedBondsCount(na));
        Assertions.assertEquals(0, na.getImplicitHydrogenCount().intValue());
    }

    /**
     * @cdk.bug 1244612
     */
    @Test
    void testSulfurCompound_ImplicitHydrogens() throws Exception {
        String filename = "sulfurCompound.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IChemFile chemFile = reader.read(new ChemFile());
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        Assertions.assertEquals(1, containersList.size());

        IAtomContainer atomContainer_0 = containersList.get(0);
        Assertions.assertEquals(10, atomContainer_0.getAtomCount());
        IAtom sulfur = atomContainer_0.getAtom(1);
        findAndConfigureAtomTypesForAllAtoms(atomContainer_0);
        adder.addImplicitHydrogens(atomContainer_0);
        Assertions.assertEquals("S", sulfur.getSymbol());
        Assertions.assertNotNull(sulfur.getImplicitHydrogenCount());
        Assertions.assertEquals(0, sulfur.getImplicitHydrogenCount().intValue());
        Assertions.assertEquals(3, atomContainer_0.getConnectedBondsCount(sulfur));

        Assertions.assertEquals(10, atomContainer_0.getAtomCount());

        Assertions.assertNotNull(sulfur.getImplicitHydrogenCount());
        Assertions.assertEquals(0, sulfur.getImplicitHydrogenCount().intValue());
        Assertions.assertEquals(3, atomContainer_0.getConnectedBondsCount(sulfur));
    }

    /**
     * @cdk.bug 1627763
     */
    @Test
    void testBug1627763() throws Exception {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(mol.getBuilder().newInstance(IAtom.class, "C"));
        mol.addAtom(mol.getBuilder().newInstance(IAtom.class, "O"));
        mol.addBond(mol.getBuilder().newInstance(IBond.class, mol.getAtom(0), mol.getAtom(1),
                Order.SINGLE));
        addExplicitHydrogens(mol);
        int hCount = 0;
        Iterator<IAtom> neighbors = mol.getConnectedAtomsList(mol.getAtom(0)).iterator();
        while (neighbors.hasNext()) {
            if (neighbors.next().getAtomicNumber() == IElement.H) hCount++;
        }
        Assertions.assertEquals(3, hCount);
        hCount = 0;
        neighbors = mol.getConnectedAtomsList(mol.getAtom(1)).iterator();
        while (neighbors.hasNext()) {
            if (neighbors.next().getAtomicNumber() == IElement.H) hCount++;
        }
        Assertions.assertEquals(1, hCount);
    }

    @Test
    void testMercaptan() throws Exception {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(mol.getBuilder().newInstance(IAtom.class, "C"));
        mol.addAtom(mol.getBuilder().newInstance(IAtom.class, "C"));
        mol.addAtom(mol.getBuilder().newInstance(IAtom.class, "C"));
        mol.addAtom(mol.getBuilder().newInstance(IAtom.class, "S"));
        mol.addBond(mol.getBuilder().newInstance(IBond.class, mol.getAtom(0), mol.getAtom(1),
                Order.DOUBLE));
        mol.addBond(mol.getBuilder().newInstance(IBond.class, mol.getAtom(1), mol.getAtom(2),
                Order.SINGLE));
        mol.addBond(mol.getBuilder().newInstance(IBond.class, mol.getAtom(2), mol.getAtom(3),
                Order.SINGLE));
        addExplicitHydrogens(mol);
        int hCount = 0;
        Iterator<IAtom> neighbors = mol.getConnectedAtomsList(mol.getAtom(0)).iterator();
        while (neighbors.hasNext()) {
            if (neighbors.next().getAtomicNumber() == IElement.H) hCount++;
        }
        Assertions.assertEquals(2, hCount);
        hCount = 0;
        neighbors = mol.getConnectedAtomsList(mol.getAtom(1)).iterator();
        while (neighbors.hasNext()) {
            if (neighbors.next().getAtomicNumber() == IElement.H) hCount++;
        }
        Assertions.assertEquals(1, hCount);
        hCount = 0;
        neighbors = mol.getConnectedAtomsList(mol.getAtom(2)).iterator();
        while (neighbors.hasNext()) {
            if (neighbors.next().getAtomicNumber() == IElement.H) hCount++;
        }
        Assertions.assertEquals(2, hCount);
        hCount = 0;
        neighbors = mol.getConnectedAtomsList(mol.getAtom(3)).iterator();
        while (neighbors.hasNext()) {
            if (neighbors.next().getAtomicNumber() == IElement.H) hCount++;
        }
        Assertions.assertEquals(1, hCount);
    }

    @Test
    void unknownAtomTypeLeavesHydrogenCountAlone() throws Exception {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        CDKHydrogenAdder hydrogenAdder = CDKHydrogenAdder.getInstance(bldr);
        IAtomContainer container = bldr.newInstance(IAtomContainer.class);
        IAtom atom = bldr.newInstance(IAtom.class, "C");
        atom.setImplicitHydrogenCount(3);
        atom.setAtomTypeName("X");
        container.addAtom(atom);
        hydrogenAdder.addImplicitHydrogens(container);
        assertThat(atom.getImplicitHydrogenCount(), is(3));
    }

    @Test
    void unknownAtomTypeLeavesHydrogenCountAloneUnlessNull() throws Exception {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        CDKHydrogenAdder hydrogenAdder = CDKHydrogenAdder.getInstance(bldr);
        IAtomContainer container = bldr.newInstance(IAtomContainer.class);
        IAtom atom = bldr.newInstance(IAtom.class, "C");
        atom.setImplicitHydrogenCount(null);
        atom.setAtomTypeName("X");
        container.addAtom(atom);
        hydrogenAdder.addImplicitHydrogens(container);
        assertThat(atom.getImplicitHydrogenCount(), is(0));
    }

    private void findAndConfigureAtomTypesForAllAtoms(IAtomContainer container) throws Exception {
        for (IAtom atom : container.atoms()) {
            IAtomType type = matcher.findMatchingAtomType(container, atom);
            Assertions.assertNotNull(type);
            AtomTypeManipulator.configure(atom, type);
        }
    }

}
