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

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IAtomType.Hybridization;
import org.openscience.cdk.nonotify.NNAtom;
import org.openscience.cdk.nonotify.NNMolecule;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.tools.diff.AtomContainerDiff;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * @cdk.module test-iordf
 */
public class ConvertorTest extends CDKTestCase {

    private static IChemObjectBuilder builder =
        NoNotificationChemObjectBuilder.getInstance();

    @Test public void roundtripMolecule() {
        IMolecule mol = new NNMolecule();
        Model model = Convertor.molecule2Model(mol);
        IMolecule rtMol = Convertor.model2Molecule(model, builder);
        String diff = AtomContainerDiff.diff(mol, rtMol);
        Assert.assertEquals(0, diff.length());
    }

    @Test public void roundtripAtom() {
        IMolecule mol = new NNMolecule();
        mol.addAtom(new NNAtom("C"));
        Model model = Convertor.molecule2Model(mol);
        IMolecule rtMol = Convertor.model2Molecule(model, builder);
        String diff = AtomContainerDiff.diff(mol, rtMol);
        Assert.assertEquals("Unexpected diff: " + diff, 0, diff.length());
    }

    @Test public void roundtripBond_SINGLE() {
        roundtripBond_Order(IBond.Order.SINGLE);
    }
    @Test public void roundtripBond_DOUBLE() {
        roundtripBond_Order(IBond.Order.DOUBLE);
    }
    @Test public void roundtripBond_TRIPLE() {
        roundtripBond_Order(IBond.Order.TRIPLE);
    }
    @Test public void roundtripBond_QUAD() {
        roundtripBond_Order(IBond.Order.QUADRUPLE);
    }

    private void roundtripBond_Order(IBond.Order order) {
        IMolecule mol = new NNMolecule();
        mol.addAtom(new NNAtom("C"));
        mol.addAtom(new NNAtom("C"));
        mol.addBond(0,1,order);
        Model model = Convertor.molecule2Model(mol);
        IMolecule rtMol = Convertor.model2Molecule(model, builder);
        String diff = AtomContainerDiff.diff(mol, rtMol);
        Assert.assertEquals("Unexpected diff: " + diff, 0, diff.length());
    }

    @Test public void roundtripChemObject() {
        IMolecule mol = new NNMolecule();
        IAtom object = new NNAtom("C");
        object.setID("atom1");
        mol.addAtom(object);
        Model model = Convertor.molecule2Model(mol);
        IMolecule rtMol = Convertor.model2Molecule(model, builder);
        String diff = AtomContainerDiff.diff(mol, rtMol);
        Assert.assertEquals("Unexpected diff: " + diff, 0, diff.length());
    }

    @Test public void roundtripElement() {
        IMolecule mol = new NNMolecule();
        IAtom object = new NNAtom("C");
        object.setAtomicNumber(6);
        mol.addAtom(object);
        Model model = Convertor.molecule2Model(mol);
        IMolecule rtMol = Convertor.model2Molecule(model, builder);
        String diff = AtomContainerDiff.diff(mol, rtMol);
        Assert.assertEquals("Unexpected diff: " + diff, 0, diff.length());
    }

    @Test public void roundtripAtomType_S() {
        roundtripAtomType_Hybridization(Hybridization.S);
    }
    @Test public void roundtripAtomType_SP1() {
        roundtripAtomType_Hybridization(Hybridization.SP1);
    }
    @Test public void roundtripAtomType_SP2() {
        roundtripAtomType_Hybridization(Hybridization.SP2);
    }
    @Test public void roundtripAtomType_SP3() {
        roundtripAtomType_Hybridization(Hybridization.SP3);
    }
    @Test public void roundtripAtomType_PLANAR3() {
        roundtripAtomType_Hybridization(Hybridization.PLANAR3);
    }
    @Test public void roundtripAtomType_SP3D1() {
        roundtripAtomType_Hybridization(Hybridization.SP3D1);
    }
    @Test public void roundtripAtomType_SP3D2() {
        roundtripAtomType_Hybridization(Hybridization.SP3D2);
    }
    @Test public void roundtripAtomType_SP3D3() {
        roundtripAtomType_Hybridization(Hybridization.SP3D3);
    }
    @Test public void roundtripAtomType_SP3D4() {
        roundtripAtomType_Hybridization(Hybridization.SP3D4);
    }
    @Test public void roundtripAtomType_SP3D5() {
        roundtripAtomType_Hybridization(Hybridization.SP3D5);
    }

    private void roundtripAtomType_Hybridization(Hybridization hybrid) {
        IMolecule mol = new NNMolecule();
        IAtom object = new NNAtom("C");
        object.setHybridization(hybrid);
        mol.addAtom(object);
        Model model = Convertor.molecule2Model(mol);
        IMolecule rtMol = Convertor.model2Molecule(model, builder);
        String diff = AtomContainerDiff.diff(mol, rtMol);
        Assert.assertEquals("Unexpected diff: " + diff, 0, diff.length());
    }

}
