/* Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.io.cml;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.libio.cml.Convertor;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.diff.AtomDiff;

/**
 * @cdk.module test-libiocml
 */
public class CDKRoundTripTest extends CDKTestCase {

    private static IChemObjectBuilder builder   = SilentChemObjectBuilder.getInstance();

    private static Convertor          convertor = new Convertor(false, "");

    @Test
    public void testIElement_Symbol() throws Exception {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom atom = builder.newInstance(IAtom.class, "C");
        atom.setID("a1");
        mol.addAtom(atom);
        IAtomContainer copy = CMLRoundTripTool.roundTripMolecule(convertor, mol);
        String difference = AtomDiff.diff(atom, copy.getAtom(0));
        Assert.assertEquals("Found non-zero diff: " + difference, 0, difference.length());
    }

    @Test
    public void testIElement_AtomicNumber() throws Exception {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom atom = builder.newInstance(IAtom.class, "C");
        atom.setID("a1");
        atom.setAtomicNumber(6);
        mol.addAtom(atom);
        IAtomContainer copy = CMLRoundTripTool.roundTripMolecule(convertor, mol);
        String difference = AtomDiff.diff(atom, copy.getAtom(0));
        Assert.assertEquals("Found non-zero diff: " + difference, 0, difference.length());
    }

    @Ignore
    @Test
    public void testIIsotope_NaturalAbundance() throws Exception {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom atom = builder.newInstance(IAtom.class, "C");
        atom.setID("a1");
        atom.setNaturalAbundance(99.);
        mol.addAtom(atom);
        IAtomContainer copy = CMLRoundTripTool.roundTripMolecule(convertor, mol);
        String difference = AtomDiff.diff(atom, copy.getAtom(0));
        Assert.assertEquals("Found non-zero diff: " + difference, 0, difference.length());
    }

    @Ignore("exact mass not currently supported in CML implmenetation")
    @Test
    public void testIIsotope_ExactMass() throws Exception {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom atom = builder.newInstance(IAtom.class, "C");
        atom.setID("a1");
        atom.setExactMass(12.);
        mol.addAtom(atom);
        IAtomContainer copy = CMLRoundTripTool.roundTripMolecule(convertor, mol);
        String difference = AtomDiff.diff(atom, copy.getAtom(0));
        Assert.assertEquals("Found non-zero diff: " + difference, 0, difference.length());
    }

    @Test
    public void testIIsotope_MassNumber() throws Exception {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom atom = builder.newInstance(IAtom.class, "C");
        atom.setID("a1");
        atom.setMassNumber(13);
        mol.addAtom(atom);
        IAtomContainer copy = CMLRoundTripTool.roundTripMolecule(convertor, mol);
        String difference = AtomDiff.diff(atom, copy.getAtom(0));
        Assert.assertEquals("Found non-zero diff: " + difference, 0, difference.length());
    }

    @Ignore
    @Test
    public void testIAtomType_Name() throws Exception {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom atom = builder.newInstance(IAtom.class, "C");
        atom.setID("a1");
        atom.setAtomTypeName("C.sp3");
        mol.addAtom(atom);
        IAtomContainer copy = CMLRoundTripTool.roundTripMolecule(convertor, mol);
        String difference = AtomDiff.diff(atom, copy.getAtom(0));
        Assert.assertEquals("Found non-zero diff: " + difference, 0, difference.length());
    }

    @Ignore
    @Test
    public void testIAtomType_MaxBondOrder() throws Exception {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom atom = builder.newInstance(IAtom.class, "C");
        atom.setID("a1");
        atom.setMaxBondOrder(IBond.Order.TRIPLE);
        mol.addAtom(atom);
        IAtomContainer copy = CMLRoundTripTool.roundTripMolecule(convertor, mol);
        String difference = AtomDiff.diff(atom, copy.getAtom(0));
        Assert.assertEquals("Found non-zero diff: " + difference, 0, difference.length());
    }

    @Ignore
    @Test
    public void testIAtomType_BondOrderSum() throws Exception {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom atom = builder.newInstance(IAtom.class, "C");
        atom.setID("a1");
        atom.setBondOrderSum(4.);
        mol.addAtom(atom);
        IAtomContainer copy = CMLRoundTripTool.roundTripMolecule(convertor, mol);
        String difference = AtomDiff.diff(atom, copy.getAtom(0));
        Assert.assertEquals("Found non-zero diff: " + difference, 0, difference.length());
    }

    @Test
    public void testIAtomType_FormalCharge() throws Exception {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom atom = builder.newInstance(IAtom.class, "C");
        atom.setID("a1");
        atom.setFormalCharge(+1);
        mol.addAtom(atom);
        IAtomContainer copy = CMLRoundTripTool.roundTripMolecule(convertor, mol);
        String difference = AtomDiff.diff(atom, copy.getAtom(0));
        Assert.assertEquals("Found non-zero diff: " + difference, 0, difference.length());
    }

    @Ignore
    @Test
    public void testIAtomType_FormalNeighborCount() throws Exception {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom atom = builder.newInstance(IAtom.class, "C");
        atom.setID("a1");
        atom.setFormalNeighbourCount(4);
        mol.addAtom(atom);
        IAtomContainer copy = CMLRoundTripTool.roundTripMolecule(convertor, mol);
        String difference = AtomDiff.diff(atom, copy.getAtom(0));
        Assert.assertEquals("Found non-zero diff: " + difference, 0, difference.length());
    }

    @Ignore
    @Test
    public void testIAtomType_Hybridization() throws Exception {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom atom = builder.newInstance(IAtom.class, "C");
        atom.setID("a1");
        atom.setHybridization(IAtomType.Hybridization.SP3);
        mol.addAtom(atom);
        IAtomContainer copy = CMLRoundTripTool.roundTripMolecule(convertor, mol);
        String difference = AtomDiff.diff(atom, copy.getAtom(0));
        Assert.assertEquals("Found non-zero diff: " + difference, 0, difference.length());
    }

    @Ignore
    @Test
    public void testIAtomType_CovalentRadius() throws Exception {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom atom = builder.newInstance(IAtom.class, "C");
        atom.setID("a1");
        atom.setCovalentRadius(1.5d);
        mol.addAtom(atom);
        IAtomContainer copy = CMLRoundTripTool.roundTripMolecule(convertor, mol);
        String difference = AtomDiff.diff(atom, copy.getAtom(0));
        Assert.assertEquals("Found non-zero diff: " + difference, 0, difference.length());
    }

    @Ignore
    @Test
    public void testIAtomType_Valency() throws Exception {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom atom = builder.newInstance(IAtom.class, "C");
        atom.setID("a1");
        atom.setValency(4);
        mol.addAtom(atom);
        IAtomContainer copy = CMLRoundTripTool.roundTripMolecule(convertor, mol);
        String difference = AtomDiff.diff(atom, copy.getAtom(0));
        Assert.assertEquals("Found non-zero diff: " + difference, 0, difference.length());
    }

    @Test
    public void testIAtom_Charge() throws Exception {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom atom = builder.newInstance(IAtom.class, "C");
        atom.setID("a1");
        atom.setCharge(0.3);
        mol.addAtom(atom);
        IAtomContainer copy = CMLRoundTripTool.roundTripMolecule(convertor, mol);
        String difference = AtomDiff.diff(atom, copy.getAtom(0));
        Assert.assertEquals("Found non-zero diff: " + difference, 0, difference.length());
    }

    @Test
    public void testIAtom_HydrogenCount() throws Exception {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom atom = builder.newInstance(IAtom.class, "C");
        atom.setID("a1");
        atom.setImplicitHydrogenCount(4);
        mol.addAtom(atom);
        IAtomContainer copy = CMLRoundTripTool.roundTripMolecule(convertor, mol);
        String difference = AtomDiff.diff(atom, copy.getAtom(0));
        Assert.assertEquals("Found non-zero diff: " + difference, 0, difference.length());
    }

    @Test
    public void testIAtom_Point2d() throws Exception {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom atom = builder.newInstance(IAtom.class, "C");
        atom.setID("a1");
        atom.setPoint2d(new Point2d(1.0, 2.0));
        mol.addAtom(atom);
        IAtomContainer copy = CMLRoundTripTool.roundTripMolecule(convertor, mol);
        String difference = AtomDiff.diff(atom, copy.getAtom(0));
        Assert.assertEquals("Found non-zero diff: " + difference, 0, difference.length());
    }

    @Test
    public void testIAtom_Point3d() throws Exception {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom atom = builder.newInstance(IAtom.class, "C");
        atom.setID("a1");
        atom.setPoint3d(new Point3d(1.0, 2.0, 3.0));
        mol.addAtom(atom);
        IAtomContainer copy = CMLRoundTripTool.roundTripMolecule(convertor, mol);
        String difference = AtomDiff.diff(atom, copy.getAtom(0));
        Assert.assertEquals("Found non-zero diff: " + difference, 0, difference.length());
    }

    @Test
    public void testIAtom_FractionalPoint3d() throws Exception {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom atom = builder.newInstance(IAtom.class, "C");
        atom.setID("a1");
        atom.setFractionalPoint3d(new Point3d(1.0, 2.0, 3.0));
        mol.addAtom(atom);
        IAtomContainer copy = CMLRoundTripTool.roundTripMolecule(convertor, mol);
        String difference = AtomDiff.diff(atom, copy.getAtom(0));
        Assert.assertEquals("Found non-zero diff: " + difference, 0, difference.length());
    }

    @Test
    public void testIAtom_Point8d() throws Exception {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom atom = builder.newInstance(IAtom.class, "C");
        atom.setID("a1");
        atom.setPoint2d(new Point2d(0.0, 0.0));
        atom.setPoint3d(new Point3d(-1.0, -2.0, -3.0));
        atom.setFractionalPoint3d(new Point3d(1.0, 2.0, 3.0));
        mol.addAtom(atom);
        IAtomContainer copy = CMLRoundTripTool.roundTripMolecule(convertor, mol);
        String difference = AtomDiff.diff(atom, copy.getAtom(0));
        Assert.assertEquals("Found non-zero diff: " + difference, 0, difference.length());
    }

    @Ignore
    @Test
    public void testIAtom_StereoParity() throws Exception {
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom atom = builder.newInstance(IAtom.class, "C");
        atom.setID("a1");
        atom.setStereoParity(-1);
        mol.addAtom(atom);
        IAtomContainer copy = CMLRoundTripTool.roundTripMolecule(convertor, mol);
        String difference = AtomDiff.diff(atom, copy.getAtom(0));
        Assert.assertEquals("Found non-zero diff: " + difference, 0, difference.length());
    }

}
