/* Copyright (C) 2009  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.libio.jena;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType.Hybridization;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.silent.Atom;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.diff.AtomContainerDiff;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * @cdk.module test-iordf
 */
public class ConvertorTest extends CDKTestCase {

    private static IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();

    @Test
    public void roundtripMolecule() {
        IAtomContainer mol = new AtomContainer();
        Model model = Convertor.molecule2Model(mol);
        IAtomContainer rtMol = Convertor.model2Molecule(model, builder);
        String diff = AtomContainerDiff.diff(mol, rtMol);
        Assert.assertEquals(0, diff.length());
    }

    @Test
    public void roundtripAtom() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C"));
        Model model = Convertor.molecule2Model(mol);
        IAtomContainer rtMol = Convertor.model2Molecule(model, builder);
        String diff = AtomContainerDiff.diff(mol, rtMol);
        Assert.assertEquals("Unexpected diff: " + diff, 0, diff.length());
    }

    @Test
    public void roundtripBond_SINGLE() {
        roundtripBond_Order(IBond.Order.SINGLE);
    }

    @Test
    public void roundtripBond_DOUBLE() {
        roundtripBond_Order(IBond.Order.DOUBLE);
    }

    @Test
    public void roundtripBond_TRIPLE() {
        roundtripBond_Order(IBond.Order.TRIPLE);
    }

    @Test
    public void roundtripBond_QUAD() {
        roundtripBond_Order(IBond.Order.QUADRUPLE);
    }

    private void roundtripBond_Order(IBond.Order order) {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addBond(0, 1, order);
        Model model = Convertor.molecule2Model(mol);
        IAtomContainer rtMol = Convertor.model2Molecule(model, builder);
        String diff = AtomContainerDiff.diff(mol, rtMol);
        Assert.assertEquals("Unexpected diff: " + diff, 0, diff.length());
    }

    @Test
    public void roundtripElectronContainer_ElectronCount() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.getBond(0).setElectronCount(1);
        Model model = Convertor.molecule2Model(mol);
        IAtomContainer rtMol = Convertor.model2Molecule(model, builder);
        String diff = AtomContainerDiff.diff(mol, rtMol);
        Assert.assertEquals("Unexpected diff: " + diff, 0, diff.length());
    }

    @Test
    public void roundtripChemObject() {
        IAtomContainer mol = new AtomContainer();
        IAtom object = new Atom("C");
        object.setID("atom1");
        mol.addAtom(object);
        Model model = Convertor.molecule2Model(mol);
        IAtomContainer rtMol = Convertor.model2Molecule(model, builder);
        String diff = AtomContainerDiff.diff(mol, rtMol);
        Assert.assertEquals("Unexpected diff: " + diff, 0, diff.length());
    }

    @Test
    public void roundtripElement() {
        IAtomContainer mol = new AtomContainer();
        IAtom object = new Atom("C");
        object.setAtomicNumber(6);
        mol.addAtom(object);
        Model model = Convertor.molecule2Model(mol);
        IAtomContainer rtMol = Convertor.model2Molecule(model, builder);
        String diff = AtomContainerDiff.diff(mol, rtMol);
        Assert.assertEquals("Unexpected diff: " + diff, 0, diff.length());
    }

    @Test
    public void roundtripPseudoAtom() {
        IAtomContainer mol = new AtomContainer();
        IPseudoAtom object = new PseudoAtom("FunnyAtom");
        mol.addAtom(object);
        Model model = Convertor.molecule2Model(mol);
        IAtomContainer rtMol = Convertor.model2Molecule(model, builder);
        String diff = AtomContainerDiff.diff(mol, rtMol);
        Assert.assertEquals("Unexpected diff: " + diff, 0, diff.length());
    }

    @Test
    public void roundtripAtomType() {
        IAtomContainer mol = new AtomContainer();
        IAtom object = new Atom("C");
        object.setAtomTypeName("C.sp3");
        object.setFormalCharge(+1);
        mol.addAtom(object);
        Model model = Convertor.molecule2Model(mol);
        IAtomContainer rtMol = Convertor.model2Molecule(model, builder);
        String diff = AtomContainerDiff.diff(mol, rtMol);
        Assert.assertEquals("Unexpected diff: " + diff, 0, diff.length());
    }

    @Test
    public void roundtripIsotope_ExactMass() {
        IAtomContainer mol = new AtomContainer();
        IAtom object = new Atom("C");
        object.setExactMass(0.3);
        mol.addAtom(object);
        Model model = Convertor.molecule2Model(mol);
        IAtomContainer rtMol = Convertor.model2Molecule(model, builder);
        String diff = AtomContainerDiff.diff(mol, rtMol);
        Assert.assertEquals("Unexpected diff: " + diff, 0, diff.length());
    }

    @Test
    public void roundtripIsotope_MassNumber() {
        IAtomContainer mol = new AtomContainer();
        IAtom object = new Atom("C");
        object.setMassNumber(13);
        mol.addAtom(object);
        Model model = Convertor.molecule2Model(mol);
        IAtomContainer rtMol = Convertor.model2Molecule(model, builder);
        String diff = AtomContainerDiff.diff(mol, rtMol);
        Assert.assertEquals("Unexpected diff: " + diff, 0, diff.length());
    }

    @Test
    public void roundtripIsotope_NaturalAbundance() {
        IAtomContainer mol = new AtomContainer();
        IAtom object = new Atom("C");
        object.setNaturalAbundance(0.95);
        mol.addAtom(object);
        Model model = Convertor.molecule2Model(mol);
        IAtomContainer rtMol = Convertor.model2Molecule(model, builder);
        String diff = AtomContainerDiff.diff(mol, rtMol);
        Assert.assertEquals("Unexpected diff: " + diff, 0, diff.length());
    }

    @Test
    public void roundtripAtomType_S() {
        roundtripAtomType_Hybridization(Hybridization.S);
    }

    @Test
    public void roundtripAtomType_SP1() {
        roundtripAtomType_Hybridization(Hybridization.SP1);
    }

    @Test
    public void roundtripAtomType_SP2() {
        roundtripAtomType_Hybridization(Hybridization.SP2);
    }

    @Test
    public void roundtripAtomType_SP3() {
        roundtripAtomType_Hybridization(Hybridization.SP3);
    }

    @Test
    public void roundtripAtomType_PLANAR3() {
        roundtripAtomType_Hybridization(Hybridization.PLANAR3);
    }

    @Test
    public void roundtripAtomType_SP3D1() {
        roundtripAtomType_Hybridization(Hybridization.SP3D1);
    }

    @Test
    public void roundtripAtomType_SP3D2() {
        roundtripAtomType_Hybridization(Hybridization.SP3D2);
    }

    @Test
    public void roundtripAtomType_SP3D3() {
        roundtripAtomType_Hybridization(Hybridization.SP3D3);
    }

    @Test
    public void roundtripAtomType_SP3D4() {
        roundtripAtomType_Hybridization(Hybridization.SP3D4);
    }

    @Test
    public void roundtripAtomType_SP3D5() {
        roundtripAtomType_Hybridization(Hybridization.SP3D5);
    }

    private void roundtripAtomType_Hybridization(Hybridization hybrid) {
        IAtomContainer mol = new AtomContainer();
        IAtom object = new Atom("C");
        object.setHybridization(hybrid);
        mol.addAtom(object);
        Model model = Convertor.molecule2Model(mol);
        IAtomContainer rtMol = Convertor.model2Molecule(model, builder);
        String diff = AtomContainerDiff.diff(mol, rtMol);
        Assert.assertEquals("Unexpected diff: " + diff, 0, diff.length());
    }

    @Test
    public void testAtomType_MaxBondOrder_SINGLE() {
        roundtripAtomType_MaxBondOrder(Order.SINGLE);
    }

    @Test
    public void testAtomType_MaxBondOrder_DOUBLE() {
        roundtripAtomType_MaxBondOrder(Order.DOUBLE);
    }

    @Test
    public void testAtomType_MaxBondOrder_TRIPLE() {
        roundtripAtomType_MaxBondOrder(Order.TRIPLE);
    }

    @Test
    public void testAtomType_MaxBondOrder_QUAD() {
        roundtripAtomType_MaxBondOrder(Order.QUADRUPLE);
    }

    private void roundtripAtomType_MaxBondOrder(Order order) {
        IAtomContainer mol = new AtomContainer();
        IAtom object = new Atom("C");
        object.setMaxBondOrder(order);
        mol.addAtom(object);
        Model model = Convertor.molecule2Model(mol);
        IAtomContainer rtMol = Convertor.model2Molecule(model, builder);
        String diff = AtomContainerDiff.diff(mol, rtMol);
        Assert.assertEquals("Unexpected diff: " + diff, 0, diff.length());
    }
}
