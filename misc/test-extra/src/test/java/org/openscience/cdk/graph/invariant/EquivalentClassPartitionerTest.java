/*
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk.graph.invariant;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.io.InputStream;

/**
 * Checks the functionality of the TopologicalEquivalentClass.
 *
 * @author      Junfeng Hao
 * @author      Luis F. de Figueiredo
 * @cdk.created 2003-09-26
 * @cdk.module test-extra
 */
public class EquivalentClassPartitionerTest extends CDKTestCase {

    AtomContainer C40C3V = null;
    AtomContainer C24D6D = null;
    AtomContainer C28TD  = null;

    @Test
    public void testEquivalent() throws Exception {
        AtomContainer C40C3V = new org.openscience.cdk.AtomContainer();
        C40C3V.addAtom(new Atom("C")); // 1
        C40C3V.addAtom(new Atom("C")); // 2
        C40C3V.addAtom(new Atom("C")); // 3
        C40C3V.addAtom(new Atom("C")); // 4
        C40C3V.addAtom(new Atom("C")); // 5
        C40C3V.addAtom(new Atom("C")); // 6
        C40C3V.addAtom(new Atom("C")); // 7
        C40C3V.addAtom(new Atom("C")); // 8
        C40C3V.addAtom(new Atom("C")); // 9
        C40C3V.addAtom(new Atom("C")); // 10
        C40C3V.addAtom(new Atom("C")); // 11
        C40C3V.addAtom(new Atom("C")); // 12
        C40C3V.addAtom(new Atom("C")); // 13
        C40C3V.addAtom(new Atom("C")); // 14
        C40C3V.addAtom(new Atom("C")); // 15
        C40C3V.addAtom(new Atom("C")); // 16
        C40C3V.addAtom(new Atom("C")); // 17
        C40C3V.addAtom(new Atom("C")); // 18
        C40C3V.addAtom(new Atom("C")); // 19
        C40C3V.addAtom(new Atom("C")); // 20
        C40C3V.addAtom(new Atom("C")); // 21
        C40C3V.addAtom(new Atom("C")); // 22
        C40C3V.addAtom(new Atom("C")); // 23
        C40C3V.addAtom(new Atom("C")); // 24
        C40C3V.addAtom(new Atom("C")); // 25
        C40C3V.addAtom(new Atom("C")); // 26
        C40C3V.addAtom(new Atom("C")); // 27
        C40C3V.addAtom(new Atom("C")); // 28
        C40C3V.addAtom(new Atom("C")); // 29
        C40C3V.addAtom(new Atom("C")); // 30
        C40C3V.addAtom(new Atom("C")); // 31
        C40C3V.addAtom(new Atom("C")); // 32
        C40C3V.addAtom(new Atom("C")); // 33
        C40C3V.addAtom(new Atom("C")); // 34
        C40C3V.addAtom(new Atom("C")); // 35
        C40C3V.addAtom(new Atom("C")); // 36
        C40C3V.addAtom(new Atom("C")); // 37
        C40C3V.addAtom(new Atom("C")); // 38
        C40C3V.addAtom(new Atom("C")); // 39
        C40C3V.addAtom(new Atom("C")); // 40

        C40C3V.addBond(0, 1, IBond.Order.SINGLE); // 1
        C40C3V.addBond(0, 5, IBond.Order.SINGLE); // 2
        C40C3V.addBond(0, 8, IBond.Order.SINGLE); // 3
        C40C3V.addBond(1, 2, IBond.Order.SINGLE); // 4
        C40C3V.addBond(1, 25, IBond.Order.SINGLE); // 5
        C40C3V.addBond(2, 3, IBond.Order.SINGLE); // 6
        C40C3V.addBond(2, 6, IBond.Order.SINGLE); // 7
        C40C3V.addBond(3, 4, IBond.Order.SINGLE); // 8
        C40C3V.addBond(3, 24, IBond.Order.SINGLE); // 9
        C40C3V.addBond(4, 7, IBond.Order.SINGLE); // 10
        C40C3V.addBond(4, 8, IBond.Order.SINGLE); // 11
        C40C3V.addBond(5, 21, IBond.Order.SINGLE); // 12
        C40C3V.addBond(5, 28, IBond.Order.SINGLE); // 13
        C40C3V.addBond(6, 22, IBond.Order.SINGLE); // 14
        C40C3V.addBond(6, 27, IBond.Order.SINGLE); // 15
        C40C3V.addBond(7, 20, IBond.Order.SINGLE); // 16
        C40C3V.addBond(7, 23, IBond.Order.SINGLE); // 17
        C40C3V.addBond(8, 26, IBond.Order.SINGLE); // 18
        C40C3V.addBond(9, 12, IBond.Order.SINGLE); // 19
        C40C3V.addBond(9, 37, IBond.Order.SINGLE); // 20
        C40C3V.addBond(9, 39, IBond.Order.SINGLE); // 21
        C40C3V.addBond(10, 14, IBond.Order.SINGLE); // 22
        C40C3V.addBond(10, 38, IBond.Order.SINGLE); // 23
        C40C3V.addBond(10, 39, IBond.Order.SINGLE); // 24
        C40C3V.addBond(11, 13, IBond.Order.SINGLE); // 25
        C40C3V.addBond(11, 36, IBond.Order.SINGLE); // 26
        C40C3V.addBond(11, 39, IBond.Order.SINGLE); // 27
        C40C3V.addBond(12, 35, IBond.Order.SINGLE); // 28
        C40C3V.addBond(12, 38, IBond.Order.SINGLE); // 29
        C40C3V.addBond(13, 34, IBond.Order.SINGLE); // 30
        C40C3V.addBond(13, 37, IBond.Order.SINGLE); // 31
        C40C3V.addBond(14, 33, IBond.Order.SINGLE); // 32
        C40C3V.addBond(14, 36, IBond.Order.SINGLE); // 33
        C40C3V.addBond(15, 29, IBond.Order.SINGLE); // 34
        C40C3V.addBond(15, 17, IBond.Order.SINGLE); // 35
        C40C3V.addBond(15, 37, IBond.Order.SINGLE); // 36
        C40C3V.addBond(16, 19, IBond.Order.SINGLE); // 37
        C40C3V.addBond(16, 30, IBond.Order.SINGLE); // 38
        C40C3V.addBond(16, 36, IBond.Order.SINGLE); // 39
        C40C3V.addBond(17, 20, IBond.Order.SINGLE); // 40
        C40C3V.addBond(17, 35, IBond.Order.SINGLE); // 41
        C40C3V.addBond(18, 22, IBond.Order.SINGLE); // 42
        C40C3V.addBond(18, 32, IBond.Order.SINGLE); // 43
        C40C3V.addBond(18, 33, IBond.Order.SINGLE); // 44
        C40C3V.addBond(19, 28, IBond.Order.SINGLE); // 45
        C40C3V.addBond(19, 34, IBond.Order.SINGLE); // 46
        C40C3V.addBond(20, 26, IBond.Order.SINGLE); // 47
        C40C3V.addBond(21, 26, IBond.Order.SINGLE); // 48
        C40C3V.addBond(21, 29, IBond.Order.SINGLE); // 49
        C40C3V.addBond(22, 24, IBond.Order.SINGLE); // 50
        C40C3V.addBond(23, 24, IBond.Order.SINGLE); // 51
        C40C3V.addBond(23, 31, IBond.Order.SINGLE); // 52
        C40C3V.addBond(25, 27, IBond.Order.SINGLE); // 53
        C40C3V.addBond(25, 28, IBond.Order.SINGLE); // 54
        C40C3V.addBond(27, 30, IBond.Order.SINGLE); // 55
        C40C3V.addBond(29, 34, IBond.Order.SINGLE); // 56
        C40C3V.addBond(30, 33, IBond.Order.SINGLE); // 57
        C40C3V.addBond(31, 32, IBond.Order.SINGLE); // 58
        C40C3V.addBond(31, 35, IBond.Order.SINGLE); // 59
        C40C3V.addBond(32, 38, IBond.Order.SINGLE); // 60
        EquivalentClassPartitioner it = new EquivalentClassPartitioner(C40C3V);
        int equivalentClass[] = it.getTopoEquivClassbyHuXu(C40C3V);
        char[] arrEquivalent = new char[39];
        for (int i = 1; i < equivalentClass.length - 1; i++)
            arrEquivalent[i - 1] = Integer.toString(equivalentClass[i]).charAt(0);
        String strEquivalent = new String(arrEquivalent);
        Assert.assertNotNull(equivalentClass);
        Assert.assertTrue(equivalentClass[0] == 10);//number of Class
        Assert.assertTrue(equivalentClass[40] == 10);
        Assert.assertEquals("111112221333444556667878222879995555444", strEquivalent);
    }

    @Test
    public void testFullereneC24D6D() throws Exception {
        AtomContainer C24D6D = new org.openscience.cdk.AtomContainer();
        C24D6D.addAtom(new Atom("C")); // 1
        C24D6D.addAtom(new Atom("C")); // 2
        C24D6D.addAtom(new Atom("C")); // 3
        C24D6D.addAtom(new Atom("C")); // 4
        C24D6D.addAtom(new Atom("C")); // 5
        C24D6D.addAtom(new Atom("C")); // 6
        C24D6D.addAtom(new Atom("C")); // 7
        C24D6D.addAtom(new Atom("C")); // 8
        C24D6D.addAtom(new Atom("C")); // 9
        C24D6D.addAtom(new Atom("C")); // 10
        C24D6D.addAtom(new Atom("C")); // 11
        C24D6D.addAtom(new Atom("C")); // 12
        C24D6D.addAtom(new Atom("C")); // 13
        C24D6D.addAtom(new Atom("C")); // 14
        C24D6D.addAtom(new Atom("C")); // 15
        C24D6D.addAtom(new Atom("C")); // 16
        C24D6D.addAtom(new Atom("C")); // 17
        C24D6D.addAtom(new Atom("C")); // 18
        C24D6D.addAtom(new Atom("C")); // 19
        C24D6D.addAtom(new Atom("C")); // 20
        C24D6D.addAtom(new Atom("C")); // 21
        C24D6D.addAtom(new Atom("C")); // 22
        C24D6D.addAtom(new Atom("C")); // 23
        C24D6D.addAtom(new Atom("C")); // 24

        C24D6D.addBond(0, 1, IBond.Order.SINGLE); // 1
        C24D6D.addBond(0, 5, IBond.Order.SINGLE); // 2
        C24D6D.addBond(0, 11, IBond.Order.SINGLE); // 3
        C24D6D.addBond(1, 2, IBond.Order.SINGLE); // 4
        C24D6D.addBond(1, 10, IBond.Order.SINGLE); // 5
        C24D6D.addBond(2, 3, IBond.Order.SINGLE); // 6
        C24D6D.addBond(2, 9, IBond.Order.SINGLE); // 7
        C24D6D.addBond(3, 4, IBond.Order.SINGLE); // 8
        C24D6D.addBond(3, 8, IBond.Order.SINGLE); // 9
        C24D6D.addBond(4, 5, IBond.Order.SINGLE); // 10
        C24D6D.addBond(4, 7, IBond.Order.SINGLE); // 11
        C24D6D.addBond(5, 6, IBond.Order.SINGLE); // 12
        C24D6D.addBond(6, 16, IBond.Order.SINGLE); // 13
        C24D6D.addBond(6, 17, IBond.Order.SINGLE); // 14
        C24D6D.addBond(7, 15, IBond.Order.SINGLE); // 15
        C24D6D.addBond(7, 16, IBond.Order.SINGLE); // 16
        C24D6D.addBond(8, 14, IBond.Order.SINGLE); // 17
        C24D6D.addBond(8, 15, IBond.Order.SINGLE); // 18
        C24D6D.addBond(9, 13, IBond.Order.SINGLE); // 19
        C24D6D.addBond(9, 14, IBond.Order.SINGLE); // 20
        C24D6D.addBond(10, 12, IBond.Order.SINGLE); // 21
        C24D6D.addBond(10, 13, IBond.Order.SINGLE); // 22
        C24D6D.addBond(11, 12, IBond.Order.SINGLE); // 23
        C24D6D.addBond(11, 17, IBond.Order.SINGLE); // 24
        C24D6D.addBond(12, 19, IBond.Order.SINGLE); // 25
        C24D6D.addBond(13, 20, IBond.Order.SINGLE); // 26
        C24D6D.addBond(14, 21, IBond.Order.SINGLE); // 27
        C24D6D.addBond(15, 22, IBond.Order.SINGLE); // 28
        C24D6D.addBond(16, 23, IBond.Order.SINGLE); // 29
        C24D6D.addBond(17, 18, IBond.Order.SINGLE); // 30
        C24D6D.addBond(18, 19, IBond.Order.SINGLE); // 31
        C24D6D.addBond(18, 23, IBond.Order.SINGLE); // 32
        C24D6D.addBond(19, 20, IBond.Order.SINGLE); // 33
        C24D6D.addBond(20, 21, IBond.Order.SINGLE); // 34
        C24D6D.addBond(21, 22, IBond.Order.SINGLE); // 35
        C24D6D.addBond(22, 23, IBond.Order.SINGLE); // 36

        EquivalentClassPartitioner it = new EquivalentClassPartitioner(C24D6D);
        int equivalentClass[] = it.getTopoEquivClassbyHuXu(C24D6D);
        char[] arrEquivalent = new char[24];
        for (int i = 1; i < equivalentClass.length; i++)
            arrEquivalent[i - 1] = Integer.toString(equivalentClass[i]).charAt(0);
        String strEquivalent = new String(arrEquivalent);
        Assert.assertNotNull(equivalentClass);
        Assert.assertTrue(equivalentClass[0] == 2);//number of Class
        Assert.assertEquals("111111222222222222111111", strEquivalent);
    }

    /**
     * @cdk.bug 3513954
     * @throws Exception
     */
    @Test
    public void testPseudoAtoms() throws Exception {
        String filename = "data/mdl/pseudoatoms.sdf";

        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        mol = reader.read(mol);
        Assert.assertNotNull(mol);

        // check that there are some pseudo-atoms
        boolean hasPseudo = false;
        for (IAtom atom : mol.atoms()) {
            if (atom instanceof PseudoAtom) hasPseudo = true;
        }
        Assert.assertTrue("The molecule should have one or more pseudo atoms", hasPseudo);

        EquivalentClassPartitioner partitioner = new EquivalentClassPartitioner(mol);
        Assert.assertNotNull(partitioner);

        int[] classes = partitioner.getTopoEquivClassbyHuXu(mol);
    }

    /**
     * Test if aromatic bonds are being considered as such.
     * Azulene has an aromatic outer ring and if bonds are considered only as a sequence of single and double bonds
     * then the atoms closing the rings will be assigned to different classes (and all other atoms as well) because
     * there will be a different number of single and double bonds on opposite sides of the symmetry axis.
     *
     * @throws Exception
     * @cdk.bug 3562476
     */
    @Test
    public void testAromaticSystem() throws Exception {

        IAtomContainer mol = TestMoleculeFactory.makeAzulene();
        Assert.assertNotNull("Created molecule was null", mol);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        EquivalentClassPartitioner it = new EquivalentClassPartitioner(mol);
        int[] equivalentClass = it.getTopoEquivClassbyHuXu(mol);
        char[] arrEquivalent = new char[mol.getAtomCount()];
        for (int i = 1; i < equivalentClass.length; i++)
            arrEquivalent[i - 1] = Integer.toString(equivalentClass[i]).charAt(0);
        String strEquivalent = new String(arrEquivalent);

        Assert.assertNotNull("Equivalent class was null", equivalentClass);
        Assert.assertEquals("Unexpected equivalent class length", mol.getAtomCount() + 1, equivalentClass.length);
        Assert.assertEquals("Wrong number of equivalent classes", 6, equivalentClass[0]);//number of Class
        Assert.assertEquals("Wrong class assignment", "1232145654", strEquivalent);
    }

    /**
     * Test the equivalent classes method in alpha-pinene
     *
     * @throws Exception
     */
    @Test
    public void testAlphaPinene() throws Exception {
        IAtomContainer mol = TestMoleculeFactory.makeAlphaPinene();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        Assert.assertNotNull("Created molecule was null", mol);
        EquivalentClassPartitioner it = new EquivalentClassPartitioner(mol);
        int[] equivalentClass = it.getTopoEquivClassbyHuXu(mol);
        char[] arrEquivalent = new char[mol.getAtomCount()];
        for (int i = 1; i < equivalentClass.length; i++)
            arrEquivalent[i - 1] = Integer.toString(equivalentClass[i]).charAt(0);
        String strEquivalent = new String(arrEquivalent);
        Assert.assertNotNull("Equivalent class was null", equivalentClass);
        Assert.assertEquals("Wrong number of equivalent classes", 9, equivalentClass[0]);
        Assert.assertEquals("Wrong class assignment", "1234567899", strEquivalent);
    }

    /**
     * Test the equivalent classes method in pyrimidine
     * Tests if the position of the single and double bonds in an aromatic ring matter
     * to assign a class.
     *
     * @throws Exception
     */
    @Test
    public void testPyrimidine() throws Exception {
        IAtomContainer mol = TestMoleculeFactory.makePyrimidine();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        Assert.assertNotNull("Created molecule was null", mol);
        EquivalentClassPartitioner it = new EquivalentClassPartitioner(mol);
        int[] equivalentClass = it.getTopoEquivClassbyHuXu(mol);
        char[] arrEquivalent = new char[mol.getAtomCount()];
        for (int i = 1; i < equivalentClass.length; i++)
            arrEquivalent[i - 1] = Integer.toString(equivalentClass[i]).charAt(0);
        String strEquivalent = new String(arrEquivalent);
        Assert.assertNotNull("Equivalent class was null", equivalentClass);
        Assert.assertEquals("Wrong number of equivalent classes", 4, equivalentClass[0]);
        Assert.assertEquals("Wrong class assignment", "123214", strEquivalent);
    }

    /**
     * Test the equivalent classes method in biphenyl,
     * a molecule with two aromatic systems. It has 2 symmetry axis.
     *
     * @throws Exception
     */
    @Test
    public void testBiphenyl() throws Exception {
        IAtomContainer mol = TestMoleculeFactory.makeBiphenyl();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        Assert.assertNotNull("Created molecule was null", mol);
        EquivalentClassPartitioner it = new EquivalentClassPartitioner(mol);
        int[] equivalentClass = it.getTopoEquivClassbyHuXu(mol);
        char[] arrEquivalent = new char[mol.getAtomCount()];
        for (int i = 1; i < equivalentClass.length; i++)
            arrEquivalent[i - 1] = Integer.toString(equivalentClass[i]).charAt(0);
        String strEquivalent = new String(arrEquivalent);
        Assert.assertNotNull("Equivalent class was null", equivalentClass);
        Assert.assertEquals("Wrong number of equivalent classes", 4, equivalentClass[0]);
        Assert.assertEquals("Wrong class assignment", "123432123432", strEquivalent);
    }

    /**
     * Test the equivalent classes method in imidazole,
     * an aromatic molecule with a proton that can be exchanged between two aromatic nitrogens.
     * The method should have failed because only one tautomer is considered,
     * but there is no priority class for nodes of type ArNH to distinguish the nitrogens.
     *
     * @throws Exception
     */
    @Test
    public void testImidazole() throws Exception {
        IAtomContainer mol = TestMoleculeFactory.makeImidazole();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        Assert.assertNotNull("Created molecule was null", mol);
        EquivalentClassPartitioner it = new EquivalentClassPartitioner(mol);
        int[] equivalentClass = it.getTopoEquivClassbyHuXu(mol);
        char[] arrEquivalent = new char[mol.getAtomCount()];
        for (int i = 1; i < equivalentClass.length; i++)
            arrEquivalent[i - 1] = Integer.toString(equivalentClass[i]).charAt(0);
        String strEquivalent = new String(arrEquivalent);
        Assert.assertNotNull("Equivalent class was null", equivalentClass);
        Assert.assertEquals("Wrong number of equivalent classes", 3, equivalentClass[0]);
        Assert.assertEquals("Wrong class assignment", "12321", strEquivalent);
    }

}
