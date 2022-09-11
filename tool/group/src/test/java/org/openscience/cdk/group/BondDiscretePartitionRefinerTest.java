/* Copyright (C) 2012  Gilleain Torrance <gilleain.torrance@gmail.com>
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
package org.openscience.cdk.group;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * @author maclean
 * @cdk.module test-group
 */
class BondDiscretePartitionRefinerTest extends CDKTestCase {

    private static final IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();

    @Test
    void defaultConstructorTest() {
        BondDiscretePartitionRefiner refiner = new BondDiscretePartitionRefiner();
        Assertions.assertNotNull(refiner);
    }

    @Test
    void advancedConstructorTest() {
        boolean ignoreBondOrder = true;
        BondDiscretePartitionRefiner refiner = new BondDiscretePartitionRefiner(ignoreBondOrder);
        Assertions.assertNotNull(refiner);
    }

    @Test
    void resetTest() {
        String acpString1 = "C0C1C2 0:1(1),1:2(1)";
        IAtomContainer ac1 = AtomContainerPrinter.fromString(acpString1, builder);
        BondDiscretePartitionRefiner refiner = new BondDiscretePartitionRefiner();
        refiner.refine(ac1);
        Assertions.assertEquals(refiner.getConnectivity(0, 1), 1);
        Assertions.assertEquals(refiner.getVertexCount(), 2);

        String acpString2 = "C0C1C2 0:1(1),0:2(1),1:2(1)";
        IAtomContainer ac2 = AtomContainerPrinter.fromString(acpString2, builder);
        refiner.refine(ac2);
        Assertions.assertEquals(refiner.getConnectivity(0, 2), 1);
        Assertions.assertEquals(refiner.getVertexCount(), 3);
    }

    @Test
    void refine_StartingPartitionTest() {
        Partition partition = Partition.fromString("0,1|2,3");
        String acpString = "C0C1C2C3 0:1(1),0:3(1),1:2(1),2:3(1)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        BondDiscretePartitionRefiner refiner = new BondDiscretePartitionRefiner();
        refiner.refine(ac, partition);
        PermutationGroup autG = refiner.getAutomorphismGroup();
        Assertions.assertEquals(2, autG.order());
    }

    @Test
    void refine_IgnoreBondOrderTest() {
        String acpString = "C0C1C2C3 0:1(2),0:3(1),1:2(1),2:3(2)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        boolean ignoreBondOrder = true;
        BondDiscretePartitionRefiner refiner = new BondDiscretePartitionRefiner(ignoreBondOrder);
        refiner.refine(ac);
        PermutationGroup autG = refiner.getAutomorphismGroup();
        Assertions.assertEquals(8, autG.order());
    }

    @Test
    void refineTest() {
        String acpString = "C0C1O2O3 0:1(1),0:3(1),1:2(1),2:3(1)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        BondDiscretePartitionRefiner refiner = new BondDiscretePartitionRefiner();
        refiner.refine(ac);
        PermutationGroup autG = refiner.getAutomorphismGroup();
        Assertions.assertEquals(2, autG.order());
    }

    @Test
    void isCanonical_TrueTest() {
        String acpString = "C0C1C2O3 0:1(2),0:2(1),1:3(1),2:3(1)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        BondDiscretePartitionRefiner refiner = new BondDiscretePartitionRefiner();
        Assertions.assertTrue(refiner.isCanonical(ac));
    }

    @Test
    void isCanonical_FalseTest() {
        String acpString = "C0C1C2O3 0:1(2),0:3(1),1:2(1),2:3(1)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        BondDiscretePartitionRefiner refiner = new BondDiscretePartitionRefiner();
        Assertions.assertFalse(refiner.isCanonical(ac));
    }

    @Test
    void getAutomorphismGroupTest() {
        String acpString = "C0C1C2O3 0:1(2),0:2(1),1:3(1),2:3(1)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        BondDiscretePartitionRefiner refiner = new BondDiscretePartitionRefiner();
        PermutationGroup autG = refiner.getAutomorphismGroup(ac);
        Assertions.assertNotNull(autG);
        Assertions.assertEquals(1, autG.order());
    }

    @Test
    void getAutomorphismGroup_StartingGroupTest() {
        String acpString = "C0C1C2C3 0:1(1),0:2(1),1:3(1),2:3(1)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        Permutation flip = new Permutation(1, 0, 3, 2);
        PermutationGroup autG = new PermutationGroup(4, Arrays.asList(flip));
        BondDiscretePartitionRefiner refiner = new BondDiscretePartitionRefiner();
        refiner.getAutomorphismGroup(ac, autG);
        Assertions.assertNotNull(autG);
        Assertions.assertEquals(8, autG.order());
    }

    @Test
    void getAutomorphismGroup_StartingPartitionTest() {
        Partition partition = Partition.fromString("0,1|2,3");
        String acpString = "C0C1C2C3 0:1(1),0:3(1),1:2(1),2:3(1)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        BondDiscretePartitionRefiner refiner = new BondDiscretePartitionRefiner();
        PermutationGroup autG = refiner.getAutomorphismGroup(ac, partition);
        Assertions.assertEquals(2, autG.order());
    }

    @Test
    void getVertexCountTest() {
        String acpString = "C0C1C2C3 0:1(1),0:3(1),1:2(1),2:3(1)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        BondDiscretePartitionRefiner refiner = new BondDiscretePartitionRefiner();
        refiner.refine(ac);
        Assertions.assertEquals(ac.getAtomCount(), refiner.getVertexCount());
    }

    @Test
    void getConnectivityTest() {
        String acpString = "C0C1C2C3 0:1(1),0:3(1),1:2(1),2:3(1)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        BondDiscretePartitionRefiner refiner = new BondDiscretePartitionRefiner();
        refiner.refine(ac);
        Assertions.assertEquals(1, refiner.getConnectivity(0, 1));
    }

    @Test
    void getAutomorphismPartitionTest() {
        String acpString = "C0C1C2C3C4C5C6C7C8C9 0:1(2),1:2(1),2:3(2),3:4(1),"
                + "4:5(2),5:6(1),6:7(2),7:8(1),8:9(2),5:9(1),0:9(1)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        BondDiscretePartitionRefiner refiner = new BondDiscretePartitionRefiner();
        Partition autP = refiner.getAutomorphismPartition(ac);
        Partition expected = Partition.fromString("0|1|2|3|4|5|6|7|8|9|10");
        Assertions.assertEquals(expected, autP);
    }

    // NOTE : the following tests are from bug 1250 by Luis F. de Figueiredo
    // and mostly test for aromatic bonds

    @Test
    void testAzulene() throws Exception {

        IAtomContainer mol = TestMoleculeFactory.makeAzulene();
        Assertions.assertNotNull(mol, "Created molecule was null");

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);

        BondDiscretePartitionRefiner refiner = new BondDiscretePartitionRefiner();
        refiner.refine(mol);
        Partition autP = refiner.getAutomorphismPartition();

        Assertions.assertEquals(6, autP.size(), "Wrong number of equivalent classes");
        Partition expected = Partition.fromString("0,3|1,2|4,10|5,8|6,7|9");
        Assertions.assertEquals(expected, autP, "Wrong class assignment");
    }

    /**
     * Test the equivalent classes method in pyrimidine
     * Tests if the position of the single and double bonds in an aromatic ring matter
     * to assign a class.
     *
     * @throws Exception
     */
    @Test
    void testPyrimidine() throws Exception {
        IAtomContainer mol = TestMoleculeFactory.makePyrimidine();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        Assertions.assertNotNull(mol, "Created molecule was null");

        BondDiscretePartitionRefiner refiner = new BondDiscretePartitionRefiner();
        refiner.refine(mol);
        Partition autP = refiner.getAutomorphismPartition();

        Assertions.assertEquals(3, autP.size(), "Wrong number of equivalent classes");
        Partition expected = Partition.fromString("0,3|1,2|4,5");
        Assertions.assertEquals(expected, autP, "Wrong class assignment");
    }

    /**
     * Test the equivalent classes method in biphenyl,
     * a molecule with two aromatic systems. It has 2 symmetry axis.
     *
     * @throws Exception
     */
    @Test
    void testBiphenyl() throws Exception {
        IAtomContainer mol = TestMoleculeFactory.makeBiphenyl();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        Assertions.assertNotNull(mol, "Created molecule was null");

        BondDiscretePartitionRefiner refiner = new BondDiscretePartitionRefiner();
        refiner.refine(mol);
        Partition autP = refiner.getAutomorphismPartition();

        Assertions.assertEquals(4, autP.size(), "Wrong number of equivalent classes");
        Partition expected = Partition.fromString("0,5,7,12|1,4,8,11|2,3,9,10|6");
        Assertions.assertEquals(expected, autP, "Wrong class assignment");
    }

}
