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
package org.openscience.cdk.tools.manipulator;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.Bond;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IRing;

/**
 *
 * @author     Kai Hartmann
 * @cdk.created    2004-02-20
 */
class AtomContainerSetManipulatorTest extends CDKTestCase {

    private IAtomContainer    mol1       = null;
    private IAtomContainer    mol2       = null;
    private IAtom             atomInMol1 = null;
    private IBond             bondInMol1 = null;
    private IAtom             atomInMol2 = null;
    private final IAtomContainerSet som        = new AtomContainerSet();

    AtomContainerSetManipulatorTest() {
        super();
    }

    @BeforeEach
    void setUp() {
        mol1 = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        atomInMol1 = new Atom("Cl");
        atomInMol1.setCharge(-1.0);
        atomInMol1.setFormalCharge(-1);
        atomInMol1.setImplicitHydrogenCount(1);
        mol1.addAtom(atomInMol1);
        mol1.addAtom(new Atom("Cl"));
        bondInMol1 = new Bond(atomInMol1, mol1.getAtom(1));
        mol1.addBond(bondInMol1);
        mol2 = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        atomInMol2 = new Atom("O");
        atomInMol2.setImplicitHydrogenCount(2);
        mol2.addAtom(atomInMol2);
        som.addAtomContainer(mol1);
        som.addAtomContainer(mol2);
    }

    @Test
    void testGetAtomCount_IAtomContainerSet() {
        int count = AtomContainerSetManipulator.getAtomCount(som);
        Assertions.assertEquals(3, count);
    }

    @Test
    void testGetBondCount_IAtomContainerSet() {
        int count = AtomContainerSetManipulator.getBondCount(som);
        Assertions.assertEquals(1, count);
    }

    @Test
    void testRemoveElectronContainer_IAtomContainerSet_IElectronContainer() {
        IAtomContainerSet ms = new AtomContainerSet();
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(new Atom("O"));
        mol.addAtom(new Atom("O"));
        mol.addBond(0, 1, IBond.Order.DOUBLE);
        IBond bond = mol.getBond(0);
        ms.addAtomContainer(mol);
        IBond otherBond = new Bond(new Atom(), new Atom());
        AtomContainerSetManipulator.removeElectronContainer(ms, otherBond);
        Assertions.assertEquals(1, AtomContainerSetManipulator.getBondCount(ms));
        AtomContainerSetManipulator.removeElectronContainer(ms, bond);
        Assertions.assertEquals(0, AtomContainerSetManipulator.getBondCount(ms));
    }

    @Test
    void testRemoveAtomAndConnectedElectronContainers_IAtomContainerSet_IAtom() {
        IAtomContainerSet ms = new AtomContainerSet();
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(new Atom("O"));
        mol.addAtom(new Atom("O"));
        mol.addBond(0, 1, IBond.Order.DOUBLE);
        IAtom atom = mol.getAtom(0);
        ms.addAtomContainer(mol);
        IAtom otherAtom = new Atom("O");
        AtomContainerSetManipulator.removeAtomAndConnectedElectronContainers(ms, otherAtom);
        Assertions.assertEquals(1, AtomContainerSetManipulator.getBondCount(ms));
        Assertions.assertEquals(2, AtomContainerSetManipulator.getAtomCount(ms));
        AtomContainerSetManipulator.removeAtomAndConnectedElectronContainers(ms, atom);
        Assertions.assertEquals(0, AtomContainerSetManipulator.getBondCount(ms));
        Assertions.assertEquals(1, AtomContainerSetManipulator.getAtomCount(ms));
    }

    @Test
    void testGetTotalCharge_IAtomContainerSet() {
        double charge = AtomContainerSetManipulator.getTotalCharge(som);
        Assertions.assertEquals(-1.0, charge, 0.000001);
    }

    @Test
    void testGetTotalFormalCharge_IAtomContainerSet() {
        double charge = AtomContainerSetManipulator.getTotalFormalCharge(som);
        Assertions.assertEquals(-1.0, charge, 0.000001);
    }

    @Test
    void testGetTotalHydrogenCount_IAtomContainerSet() {
        int hCount = AtomContainerSetManipulator.getTotalHydrogenCount(som);
        Assertions.assertEquals(3, hCount);
    }

    @Test
    void testGetAllIDs_IAtomContainerSet() {
        som.setID("som");
        mol2.setID("mol");
        atomInMol2.setID("atom");
        bondInMol1.setID("bond");
        List<String> list = AtomContainerSetManipulator.getAllIDs(som);
        Assertions.assertEquals(4, list.size());
    }

    @Test
    void testGetAllAtomContainers_IAtomContainerSet() {
        List<IAtomContainer> list = AtomContainerSetManipulator.getAllAtomContainers(som);
        Assertions.assertEquals(2, list.size());
    }

    @Test
    void testSetAtomProperties_IAtomContainerSet_Object_Object() {
        String key = "key";
        String value = "value";
        AtomContainerSetManipulator.setAtomProperties(som, key, value);
        Assertions.assertEquals(value, atomInMol1.getProperty(key));
        Assertions.assertEquals(value, atomInMol2.getProperty(key));
    }

    @Test
    void testGetRelevantAtomContainer_IAtomContainerSet_IAtom() {
        IAtomContainer ac1 = AtomContainerSetManipulator.getRelevantAtomContainer(som, atomInMol1);
        Assertions.assertEquals(mol1, ac1);
        IAtomContainer ac2 = AtomContainerSetManipulator.getRelevantAtomContainer(som, atomInMol2);
        Assertions.assertEquals(mol2, ac2);
    }

    @Test
    void testGetRelevantAtomContainer_IAtomContainerSet_IBond() {
        IAtomContainer ac1 = AtomContainerSetManipulator.getRelevantAtomContainer(som, bondInMol1);
        Assertions.assertEquals(mol1, ac1);
    }

    @Test
    void testGetAllChemObjects_IAtomContainerSet() {
        List<IChemObject> list = AtomContainerSetManipulator.getAllChemObjects(som);
        Assertions.assertEquals(3, list.size()); // only AtomContainerSets and AtomContainers at the moment (see source code comment)
    }

    @Test
    void testSort_IAtomContainerSet() {
        // Create some IAtomContainers
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IRing cycloPentane = builder.newInstance(IRing.class, 5, "C");
        IRing cycloHexane = builder.newInstance(IRing.class, 6, "C");
        IAtomContainer hexaneNitrogen = builder.newInstance(IRing.class, 6, "N");
        hexaneNitrogen.removeBond(0);
        IRing cycloHexaneNitrogen = builder.newInstance(IRing.class, 6, "N");
        IRing cycloHexeneNitrogen = builder.newInstance(IRing.class, 6, "N");
        cycloHexeneNitrogen.getBond(0).setOrder(Order.DOUBLE);

        // Add them to a IAtomContainerSet
        IAtomContainerSet atomContainerSet = builder.newInstance(IAtomContainerSet.class);
        atomContainerSet.addAtomContainer(cycloHexane);
        atomContainerSet.addAtomContainer(cycloHexeneNitrogen);
        atomContainerSet.addAtomContainer(cycloPentane);
        atomContainerSet.addAtomContainer(hexaneNitrogen);
        atomContainerSet.addAtomContainer(cycloHexaneNitrogen);

        // Sort the IAtomContainerSet
        AtomContainerSetManipulator.sort(atomContainerSet);

        // Assert.assert the correct order
        Assertions.assertSame(cycloPentane, atomContainerSet.getAtomContainer(0), "first order: cycloPentane");
        Assertions.assertSame(cycloHexane, atomContainerSet.getAtomContainer(1), "second order: cycloHexane");
        Assertions.assertSame(hexaneNitrogen, atomContainerSet.getAtomContainer(2), "third order: hexaneNitrogen");
        Assertions.assertSame(cycloHexaneNitrogen, atomContainerSet.getAtomContainer(3), "forth order: cycloHexaneNitrogen");
        Assertions.assertSame(cycloHexeneNitrogen, atomContainerSet.getAtomContainer(4), "firth order: cycloHexeneNitrogen");
    }

    @Test
    void testContainsByID_IAtomContainerSet_IAtomContainer() {
        IAtomContainer relevantAtomContainer = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtomContainerSet atomContainerSet = DefaultChemObjectBuilder.getInstance()
                .newInstance(IAtomContainerSet.class);
        atomContainerSet.addAtomContainer(relevantAtomContainer);
        Assertions.assertFalse(AtomContainerSetManipulator.containsByID(atomContainerSet, relevantAtomContainer.getID()));
        relevantAtomContainer.setID("1");
        Assertions.assertTrue(AtomContainerSetManipulator.containsByID(atomContainerSet, relevantAtomContainer.getID()));
    }
}
