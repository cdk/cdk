/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.MoleculeSetManipulator;

/**
 * @cdk.module test-standard
 */
public class IDCreatorTest extends CDKTestCase {

    public IDCreatorTest() {
        super();
    }

    @Test
    public void testCreateIDs_IChemObject() {
        IAtomContainer mol = new AtomContainer();
        Atom atom1 = new Atom("C");
        Atom atom2 = new Atom("C");
        mol.addAtom(atom1);
        mol.addAtom(atom2);
        Bond bond = new Bond(atom1, atom2);
        mol.addBond(bond);

        IDCreator.createIDs(mol);
        Assert.assertEquals("a1", atom1.getID());
        Assert.assertEquals("b1", bond.getID());
        List<String> ids = AtomContainerManipulator.getAllIDs(mol);
        Assert.assertEquals(4, ids.size());
    }

    @Test
    public void testKeepingIDs() {
        IAtomContainer mol = new AtomContainer();
        Atom atom = new Atom("C");
        atom.setID("atom1");
        mol.addAtom(atom);

        IDCreator.createIDs(mol);

        Assert.assertEquals("atom1", atom.getID());
        Assert.assertNotNull(mol.getID());
        List<String> ids = AtomContainerManipulator.getAllIDs(mol);
        Assert.assertEquals(2, ids.size());
    }

    @Test
    public void testNoDuplicateCreation() {
        IAtomContainer mol = new AtomContainer();
        Atom atom1 = new Atom("C");
        Atom atom2 = new Atom("C");
        atom1.setID("a1");
        mol.addAtom(atom2);
        mol.addAtom(atom1);

        IDCreator.createIDs(mol);
        Assert.assertEquals("a2", atom2.getID());
        List<String> ids = AtomContainerManipulator.getAllIDs(mol);
        Assert.assertEquals(3, ids.size());
    }

    /**
     * @cdk.bug 1455341
     */
    @Test
    public void testCallingTwice() {
        IAtomContainerSet molSet = new AtomContainerSet();
        IAtomContainer mol = new AtomContainer();
        Atom atom0 = new Atom("C");
        Atom atom2 = new Atom("C");
        atom0.setID("a1");
        mol.addAtom(atom2);
        mol.addAtom(atom0);
        molSet.addAtomContainer(mol);

        IDCreator.createIDs(molSet);
        List<String> ids = MoleculeSetManipulator.getAllIDs(molSet);
        Assert.assertEquals(4, ids.size());

        mol = new AtomContainer();
        Atom atom1 = new Atom("C");
        atom2 = new Atom("C");
        atom1.setID("a2");
        mol.addAtom(atom2);
        mol.addAtom(atom1);
        molSet.addAtomContainer(mol);

        IDCreator.createIDs(molSet);
        ids = MoleculeSetManipulator.getAllIDs(molSet);
        Assert.assertEquals(7, ids.size());

        mol = new AtomContainer();
        atom1 = new Atom("C");
        atom2 = new Atom("C");
        mol.addAtom(atom2);
        mol.addAtom(atom1);
        molSet.addAtomContainer(mol);

        atom0.setID("atomX");
        ids = MoleculeSetManipulator.getAllIDs(molSet);
        Assert.assertFalse(ids.contains("a1"));

        IDCreator.createIDs(molSet);
        List<String> idsAfter = MoleculeSetManipulator.getAllIDs(molSet);
        Assert.assertTrue(idsAfter.contains("a1"));
        Assert.assertEquals(10, idsAfter.size());
    }

}
