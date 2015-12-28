/*
 * Copyright (C) 2010 Rajarshi Guha <rajarshi.guha@gmail.com>
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
package org.openscience.cdk.fragment;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.SlowTest;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Test Murcko fragmenter.
 *
 * @cdk.module test-fragment
 */
public class MurckoFragmenterTest extends CDKTestCase {

    static MurckoFragmenter fragmenter;
    static SmilesParser     smilesParser;

    @BeforeClass
    public static void setup() {
        fragmenter = new MurckoFragmenter();
        smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
    }

    @Test
    public void testNoFramework() throws CDKException {
        IAtomContainer mol = smilesParser.parseSmiles("CCO[C@@H](C)C(=O)C(O)O");
        fragmenter.generateFragments(mol);
        String[] frameworks = fragmenter.getFrameworks();
        Assert.assertEquals(0, frameworks.length);
    }

    @Test
    public void testOnlyRingSystem() throws CDKException {
        IAtomContainer mol = smilesParser.parseSmiles("c1ccccc1CCCCC");
        fragmenter.generateFragments(mol);
        String[] frameworks = fragmenter.getFrameworks();
        Assert.assertEquals(0, frameworks.length);
        String[] rings = fragmenter.getRingSystems();
        Assert.assertEquals(1, rings.length);
    }

    @Test
    public void testMF3() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("C(CC1=C2C=CC=CC2=CC2=C1C=CC=C2)C1CCCCC1");
        fragmenter.generateFragments(mol);
        String[] frameworks = fragmenter.getFrameworks();
        Assert.assertEquals(1, frameworks.length);
    }

    @Test
    public void testMF3_Container() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("C(CC1=C2C=CC=CC2=CC2=C1C=CC=C2)C1CCCCC1");
        fragmenter.generateFragments(mol);
        IAtomContainer[] frameworks = fragmenter.getFrameworksAsContainers();
        Assert.assertEquals(1, frameworks.length);
    }

    @Test
    public void testMF1() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("c1ccccc1PP(B)c1cccc(N(N)N)c1SC1CCC1");
        MurckoFragmenter fragmenter = new MurckoFragmenter(false, 2);

        fragmenter.generateFragments(mol);
        String[] frameworks = fragmenter.getFrameworks();
        Assert.assertEquals(3, frameworks.length);

        String[] rings = fragmenter.getRingSystems();
        Assert.assertEquals(2, rings.length);
    }

    @Test
    public void testMF1_Container() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("c1ccccc1PP(B)c1cccc(N(N)N)c1SC1CCC1");
        MurckoFragmenter fragmenter = new MurckoFragmenter(false, 2);

        fragmenter.generateFragments(mol);
        IAtomContainer[] frameworks = fragmenter.getFrameworksAsContainers();
        Assert.assertEquals(3, frameworks.length);

        IAtomContainer[] rings = fragmenter.getRingSystemsAsContainers();
        Assert.assertEquals(2, rings.length);
    }

    @Test
    public void testMF2() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("C1(c2ccccc2)(CC(CC1)CCc1ccccc1)CC1C=CC=C1");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        fragmenter.generateFragments(mol);

        String[] rings = fragmenter.getRingSystems();
        Assert.assertEquals(3, rings.length);

        List<String> frameworks = Arrays.asList(fragmenter.getFrameworks());
        Assert.assertEquals(7, frameworks.size());

        assertThat(
                frameworks,
                hasItems("c1ccc(cc1)CCC2CCC(CC3C=CC=C3)C2", "c1ccc(cc1)CCC2CCCC2", "C=1C=CC(C1)CC2CCCC2",
                        "c1ccc(cc1)C2(CCCC2)CC3C=CC=C3", "c1ccc(cc1)CCC2CCC(c3ccccc3)(CC4C=CC=C4)C2",
                        "c1ccc(cc1)C2CCCC2", "c1ccc(cc1)CCC2CCC(c3ccccc3)C2"));
    }

    @Test
    public void testSingleFramework() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("C1(c2ccccc2)(CC(CC1)CCc1ccccc1)CC1C=CC=C1");
        MurckoFragmenter fragmenter = new MurckoFragmenter(true, 6);
        fragmenter.generateFragments(mol);

        String[] frameworks = fragmenter.getFrameworks();
        Assert.assertEquals(1, frameworks.length);

    }

    @Test
    public void testMF4() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("c1ccc(cc1)c2c(oc(n2)N(CCO)CCO)c3ccccc3");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        fragmenter.generateFragments(mol);

        List<String> frameworks = Arrays.asList(fragmenter.getFrameworks());
        Assert.assertEquals(3, frameworks.size());
        assertThat(frameworks, hasItems("n1coc(c1)-c2ccccc2", "n1coc(-c2ccccc2)c1-c3ccccc3", "n1cocc1-c2ccccc2"));
    }

    @Test
    public void testMF5() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("c1cc(ccc1C(=O)Nc2ccc3c(c2)nc(o3)c4ccncc4)F");
        fragmenter.generateFragments(mol);
        String[] frameworks = fragmenter.getFrameworks();
        Assert.assertEquals(3, frameworks.length);

        String[] rings = fragmenter.getRingSystems();
        Assert.assertEquals(3, rings.length);
    }

    @Test
    public void testMF6() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("COc1ccc(cc1OCc2ccccc2)C(=S)N3CCOCC3");
        fragmenter.generateFragments(mol);

        String[] frameworks = fragmenter.getFrameworks();
        Assert.assertEquals(3, frameworks.length);

        String[] rings = fragmenter.getRingSystems();
        Assert.assertEquals(2, rings.length);
    }

    @Test
    public void testMF7() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("Cc1nnc(s1)N[C@H](C(=O)c2ccccc2)NC(=O)c3ccco3");
        fragmenter.generateFragments(mol);

        String[] frameworks = fragmenter.getFrameworks();
        Assert.assertEquals(4, frameworks.length);

        String[] rings = fragmenter.getRingSystems();
        Assert.assertEquals(3, rings.length);
    }

    /**
     * @throws Exception
     * @cdk.bug 1848591
     */
    @Test
    public void testBug1848591() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("c1(ccc(cc1C)CCC(C(CCC)C2C(C2)CC)C3C=C(C=C3)CC)C");
        MurckoFragmenter fragmenter = new MurckoFragmenter(true, 6);
        fragmenter.generateFragments(mol);

        String[] frameworks = fragmenter.getFrameworks();
        Assert.assertEquals(1, frameworks.length);
        Assert.assertEquals("c1ccc(cc1)CCC(CC2CC2)C3C=CC=C3", frameworks[0]);
    }

    /**
     * @cdk.bug 3088164
     */
    @Test
    public void testCarbinoxamine_Bug3088164() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("CN(C)CCOC(C1=CC=C(Cl)C=C1)C1=CC=CC=N1");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        MurckoFragmenter fragmenter = new MurckoFragmenter(true, 6);
        fragmenter.generateFragments(mol);

        String[] f = fragmenter.getFrameworks();
        IAtomContainer[] fc = fragmenter.getFrameworksAsContainers();
        Assert.assertEquals(1, f.length);
        Assert.assertEquals(f.length, fc.length);
        Assert.assertEquals("n1ccccc1Cc2ccccc2", f[0]);

        SmilesGenerator sg = SmilesGenerator.unique().aromatic();
        for (int i = 0; i < f.length; i++) {
            Aromaticity.cdkLegacy().apply(fc[i]);
            String newsmiles = sg.create(fc[i]);
            Assert.assertTrue(f[i] + " did not match the container, " + newsmiles, f[i].equals(newsmiles));
        }
    }

    /**
     * @cdk.bug 3088164
     */
    @Test
    public void testPirenperone_Bug3088164() throws Exception {
        SmilesGenerator sg = SmilesGenerator.unique().aromatic();

        IAtomContainer mol = smilesParser.parseSmiles("Fc1ccc(cc1)C(=O)C4CCN(CCC\\3=C(\\N=C2\\C=C/C=C\\N2C/3=O)C)CC4");
        AtomContainerManipulator.clearAtomConfigurations(mol);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        MurckoFragmenter fragmenter = new MurckoFragmenter(true, 6);
        fragmenter.generateFragments(mol);

        String[] f = fragmenter.getFrameworks();
        IAtomContainer[] fc = fragmenter.getFrameworksAsContainers();

        Assert.assertEquals(1, f.length);
        Assert.assertEquals(f.length, fc.length);

        AtomContainerManipulator.clearAtomConfigurations(mol);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        CDKHydrogenAdder.getInstance(mol.getBuilder()).addImplicitHydrogens(mol);
        Aromaticity.cdkLegacy().apply(mol);

        Assert.assertEquals("N=1C=C(CN2C=CC=CC12)CCN3CCC(Cc4ccccc4)CC3", f[0]);

        for (int i = 0; i < f.length; i++) {
            String newsmiles = sg.create(fc[i]);
            Assert.assertTrue(f[i] + " did not match the container, " + newsmiles, f[i].equals(newsmiles));
        }
    }

    /**
     * @cdk.bug 3088164
     */
    @Test
    public void testIsomoltane_Bug3088164() throws Exception {
        SmilesGenerator sg = SmilesGenerator.unique().aromatic();

        IAtomContainer mol = smilesParser.parseSmiles("CC(C)NCC(O)COC1=C(C=CC=C1)N1C=CC=C1");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        MurckoFragmenter fragmenter = new MurckoFragmenter(true, 6);
        fragmenter.generateFragments(mol);

        String[] f = fragmenter.getFrameworks();
        IAtomContainer[] fc = fragmenter.getFrameworksAsContainers();
        Assert.assertEquals(1, f.length);
        Assert.assertEquals(f.length, fc.length);
        Assert.assertEquals("c1ccc(cc1)-n2cccc2", f[0]);

        for (int i = 0; i < f.length; i++) {
            Aromaticity.cdkLegacy().apply(fc[i]);
            String newsmiles = sg.create(fc[i]);
            Assert.assertTrue(f[i] + " did not match the container, " + newsmiles, f[i].equals(newsmiles));
        }
    }

    @Test
    public void testGetFragmentsAsContainers() throws Exception {

        IAtomContainer biphenyl = MoleculeFactory.makeBiphenyl(SilentChemObjectBuilder.getInstance());
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(biphenyl);
        Aromaticity.cdkLegacy().apply(biphenyl);

        MurckoFragmenter fragmenter = new MurckoFragmenter(true, 6);
        fragmenter.generateFragments(biphenyl);
        IAtomContainer[] fragments = fragmenter.getFragmentsAsContainers();

        assertThat(fragments.length, is(2));
        assertThat(fragments[0].getAtomCount(), is(12));
        assertThat(fragments[1].getAtomCount(), is(6));
    }

    /**
     * Test for large branched, symmetric molecule.
     *
     * @cdk.inchi InChI=1S/C76H52O46/c77-32-1-22(2-33(78)53(32)92)67(103)113-47-16-27(11-42(87)58(47)97)66(102)112-21-52-63(119-72(108)28-12-43(88)59(98)48(17-28)114-68(104)23-3-34(79)54(93)35(80)4-23)64(120-73(109)29-13-44(89)60(99)49(18-29)115-69(105)24-5-36(81)55(94)37(82)6-24)65(121-74(110)30-14-45(90)61(100)50(19-30)116-70(106)25-7-38(83)56(95)39(84)8-25)76(118-52)122-75(111)31-15-46(91)62(101)51(20-31)117-71(107)26-9-40(85)57(96)41(86)10-26/h1-20,52,63-65,76-101H,21H2
     * @throws Exception
     */
    @Test
    @Category(SlowTest.class)
    public void testMacrocycle() throws Exception {
        IAtomContainer mol = smilesParser
                .parseSmiles("C1=C(C=C(C(=C1O)O)O)C(=O)OC2=CC(=CC(=C2O)O)C(=O)OCC3C(C(C(C(O3)OC(=O)C4=CC(=C(C(=C4)OC(=O)C5=CC(=C(C(=C5)O)O)O)O)O)OC(=O)C6=CC(=C(C(=C6)OC(=O)C7=CC(=C(C(=C7)O)O)O)O)O)OC(=O)C8=CC(=C(C(=C8)OC(=O)C9=CC(=C(C(=C9)O)O)O)O)O)OC(=O)C1=CC(=C(C(=C1)OC(=O)C1=CC(=C(C(=C1)O)O)O)O)O");
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        Aromaticity.cdkLegacy().apply(mol);
        MurckoFragmenter fragmenter = new MurckoFragmenter(true, 6);
        fragmenter.generateFragments(mol);

        String[] f = fragmenter.getFrameworks();
        assertThat(f.length, is(1));
        String[] rs = fragmenter.getRingSystems();
        assertThat(rs.length, is(2));
        String[] fs = fragmenter.getFragments();
        assertThat(fs.length, is(3));
    }
}
