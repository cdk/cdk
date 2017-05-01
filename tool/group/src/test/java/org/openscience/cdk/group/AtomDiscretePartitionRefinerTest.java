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

import org.junit.Assert;

import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * @author maclean
 * @cdk.module test-group
 */
public class AtomDiscretePartitionRefinerTest extends CDKTestCase {

    public static IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();

    @Test
    public void defaultConstructorTest() {
        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
        Assert.assertNotNull(refiner);
    }

    @Test
    public void advancedConstructorTest() {
        boolean ignoreElements = true;
        boolean ignoreBondOrder = true;
        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner(ignoreElements, ignoreBondOrder);
        Assert.assertNotNull(refiner);
    }

    @Test
    public void resetTest() {
        String acpString1 = "C0C1 0:1(1)";
        IAtomContainer ac1 = AtomContainerPrinter.fromString(acpString1, builder);
        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
        refiner.refine(ac1);
        Assert.assertEquals(refiner.getConnectivity(0, 1), 1);
        Assert.assertEquals(refiner.getVertexCount(), 2);

        String acpString2 = "C0C1C2 0:1(2),1:2(1)";
        IAtomContainer ac2 = AtomContainerPrinter.fromString(acpString2, builder);
        refiner.refine(ac2);
        Assert.assertEquals(refiner.getConnectivity(0, 1), 2);
        Assert.assertEquals(refiner.getVertexCount(), 3);
    }

    @Test
    public void refine_StartingPartitionTest() {
        Partition partition = Partition.fromString("0,1|2,3");
        String acpString = "C0C1C2C3 0:1(1),0:3(1),1:2(1),2:3(1)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
        refiner.refine(ac, partition);
        PermutationGroup autG = refiner.getAutomorphismGroup();
        Assert.assertEquals(2, autG.order());
    }

    @Test
    public void refine_IgnoreElementsTest() {
        String acpString = "C0C1O2O3 0:1(1),0:3(1),1:2(1),2:3(1)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        boolean ignoreElements = true;
        boolean ignoreBondOrder = false;
        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner(ignoreElements, ignoreBondOrder);
        refiner.refine(ac);
        PermutationGroup autG = refiner.getAutomorphismGroup();
        Assert.assertEquals(8, autG.order());
    }

    @Test
    public void refineTest() {
        String acpString = "C0C1O2O3 0:1(1),0:3(1),1:2(1),2:3(1)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
        refiner.refine(ac);
        PermutationGroup autG = refiner.getAutomorphismGroup();
        Assert.assertEquals(2, autG.order());
    }

    @Test
    public void isCanonical_TrueTest() {
        String acpString = "C0C1C2O3 0:2(2),0:3(1),1:2(1),1:3(1)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
        Assert.assertTrue(refiner.isCanonical(ac));
    }

    @Test
    public void isCanonical_FalseTest() {
        String acpString = "C0C1C2O3 0:1(2),0:3(1),1:2(1),2:3(1)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
        Assert.assertFalse(refiner.isCanonical(ac));
    }

    @Test
    public void getAutomorphismGroupTest() {
        String acpString = "C0C1C2O3 0:1(2),0:2(1),1:3(1),2:3(1)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
        PermutationGroup autG = refiner.getAutomorphismGroup(ac);
        Assert.assertNotNull(autG);
        Assert.assertEquals(1, autG.order());
    }

    @Test
    public void getAutomorphismGroup_StartingGroupTest() {
        String acpString = "C0C1C2C3 0:1(1),0:2(1),1:3(1),2:3(1)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        Permutation flip = new Permutation(1, 0, 3, 2);
        PermutationGroup autG = new PermutationGroup(4, Arrays.asList(flip));
        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
        refiner.getAutomorphismGroup(ac, autG);
        Assert.assertNotNull(autG);
        Assert.assertEquals(8, autG.order());
    }

    @Test
    public void getAutomorphismGroup_StartingPartitionTest() {
        Partition partition = Partition.fromString("0,1|2,3");
        String acpString = "C0C1C2C3 0:1(1),0:3(1),1:2(1),2:3(1)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
        PermutationGroup autG = refiner.getAutomorphismGroup(ac, partition);
        Assert.assertEquals(2, autG.order());
    }

    @Test
    public void getVertexCountTest() {
        String acpString = "C0C1C2C3 0:1(1),0:3(1),1:2(1),2:3(1)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
        refiner.refine(ac);
        Assert.assertEquals(ac.getAtomCount(), refiner.getVertexCount());
    }

    @Test
    public void getConnectivityTest() {
        String acpString = "C0C1C2C3 0:1(1),0:3(1),1:2(2),2:3(1)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
        refiner.refine(ac);
        IBond bond = ac.getBond(ac.getAtom(1), ac.getAtom(2));
        int orderN = bond.getOrder().numeric();
        Assert.assertEquals(orderN, refiner.getConnectivity(1, 2));
    }

    @Test
    public void getAutomorphismPartitionTest() {
        String acpString = "C0C1C2C3C4C5C6C7C8C9 0:1(2),1:2(1),2:3(2),3:4(1),"
                + "4:5(2),5:6(1),6:7(2),7:8(1),8:9(2),5:9(1),0:9(1)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
        Partition autP = refiner.getAutomorphismPartition(ac);
        Partition expected = Partition.fromString("0|1|2|3|4|5|6|7|8|9");
        Assert.assertEquals(expected, autP);
    }

    // NOTE : the following tests are from bug 1250 by Luis F. de Figueiredo
    // and mostly test for aromatic bonds

    @Test
    public void testAzulene() throws Exception {

        IAtomContainer mol = TestMoleculeFactory.makeAzulene();
        Assert.assertNotNull("Created molecule was null", mol);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);

        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
        refiner.refine(mol);
        Partition autP = refiner.getAutomorphismPartition();

        Assert.assertEquals("Wrong number of equivalent classes", 6, autP.size());
        Partition expected = Partition.fromString("0,4|1,3|2|5,9|6,8|7");
        Assert.assertEquals("Wrong class assignment", expected, autP);
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

        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
        refiner.refine(mol);
        Partition autP = refiner.getAutomorphismPartition();

        Assert.assertEquals("Wrong number of equivalent classes", 4, autP.size());
        Partition expected = Partition.fromString("0,4|1,3|2|5");
        Assert.assertEquals("Wrong class assignment", expected, autP);
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

        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
        refiner.refine(mol);
        Partition autP = refiner.getAutomorphismPartition();

        Assert.assertEquals("Wrong number of equivalent classes", 4, autP.size());
        Partition expected = Partition.fromString("0,6|1,5,7,11|2,4,8,10|3,9");
        Assert.assertEquals("Wrong class assignment", expected, autP);
    }

}
