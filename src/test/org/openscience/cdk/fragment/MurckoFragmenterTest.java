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
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Test Murcko fragmenter.
 *
 * @cdk.module test-fragment
 */
public class MurckoFragmenterTest extends CDKTestCase {

    static MurckoFragmenter fragmenter;
    static SmilesParser smilesParser;

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
    public void testMF2() throws Exception {
        IAtomContainer mol = smilesParser.parseSmiles("C1(c2ccccc2)(CC(CC1)CCc1ccccc1)CC1C=CC=C1");
        fragmenter.generateFragments(mol);

        String[] rings = fragmenter.getRingSystems();
        Assert.assertEquals(3, rings.length);

        String[] frameworks = fragmenter.getFrameworks();
        Assert.assertEquals(7, frameworks.length);

        List<String> trueFrameworks = new ArrayList<String>();
        trueFrameworks.add("c1ccc(cc1)C2(CCCC2)CC3C=CC=C3");
        trueFrameworks.add("c1ccc(cc1)CCC4CCC(c2ccccc2)(CC3C=CC=C3)C4");
        trueFrameworks.add("C=1C=CC(C=1)CC2CCCC2");
        trueFrameworks.add("C1CCC(C1)CCc2ccccc2");
        trueFrameworks.add("c1ccc(cc1)C2CCCC2");
        trueFrameworks.add("c1ccc(cc1)C2CCC(C2)CCc3ccccc3");
        trueFrameworks.add("c1ccc(cc1)CCC2CC(CC2)CC3C=CC=C3");
        for (String s : frameworks) {
            Assert.assertTrue(s + " is not a valid framework", trueFrameworks.contains(s));
        }
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
        fragmenter.generateFragments(mol);

        String[] frameworks = fragmenter.getFrameworks();
        Assert.assertEquals(3, frameworks.length);
        List<String> trueFrameworks = new ArrayList<String>();
        trueFrameworks.add("c1ncc(o1)c2ccccc2");
        trueFrameworks.add("c1nc(co1)c2ccccc2");
        trueFrameworks.add("c2nc(c1ccccc1)c(o2)c3ccccc3");
        for (String s : frameworks) {
            Assert.assertTrue(s + " is not a valid framework", trueFrameworks.contains(s));
        }
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
        CDKHueckelAromaticityDetector.detectAromaticity(mol);
        MurckoFragmenter fragmenter = new MurckoFragmenter(true, 6);
        fragmenter.generateFragments(mol);

        String[] f = fragmenter.getFrameworks();
        IAtomContainer[] fc = fragmenter.getFrameworksAsContainers();
        Assert.assertEquals(1, f.length);
        Assert.assertEquals(f.length, fc.length);
        Assert.assertEquals("c1ccc(cc1)Cc2ncccc2", f[0]);

        SmilesGenerator sg = new SmilesGenerator(true);
        for (int i = 0; i < f.length; i++) {
            String newsmiles = sg.createSMILES(fc[i]);
            Assert.assertTrue(f[i] + " did not match the container, " + newsmiles, f[i].equals(newsmiles));
        }
    }

    /**
     * @cdk.bug 3088164
     */
    @Test
    public void testPirenperone_Bug3088164() throws Exception {
        SmilesGenerator sg = new SmilesGenerator(true);

        IAtomContainer mol = smilesParser.parseSmiles("Fc1ccc(cc1)C(=O)C4CCN(CCC\\3=C(\\N=C2\\C=C/C=C\\N2C/3=O)C)CC4");
        CDKHueckelAromaticityDetector.detectAromaticity(mol);
        MurckoFragmenter fragmenter = new MurckoFragmenter(true, 6);
        fragmenter.generateFragments(mol);

        String[] f = fragmenter.getFrameworks();
        IAtomContainer[] fc = fragmenter.getFrameworksAsContainers();

        Assert.assertEquals(1, f.length);
        Assert.assertEquals(f.length, fc.length);
        Assert.assertEquals("C=1N=C4C=CC=CN4(CC=1CCN3CCC(Cc2ccccc2)CC3)", f[0]);

        for (int i = 0; i < f.length; i++) {
            String newsmiles = sg.createSMILES(fc[i]);
            Assert.assertTrue(f[i] + " did not match the container, " + newsmiles, f[i].equals(newsmiles));
        }
    }

    /**
     * @cdk.bug 3088164
     */
    @Test
    public void testIsomoltane_Bug3088164() throws Exception {
        SmilesGenerator sg = new SmilesGenerator(true);

        IAtomContainer mol = smilesParser.parseSmiles("CC(C)NCC(O)COC1=C(C=CC=C1)N1C=CC=C1");
        CDKHueckelAromaticityDetector.detectAromaticity(mol);
        MurckoFragmenter fragmenter = new MurckoFragmenter(true, 6);
        fragmenter.generateFragments(mol);

        String[] f = fragmenter.getFrameworks();
        IAtomContainer[] fc = fragmenter.getFrameworksAsContainers();
        Assert.assertEquals(1, f.length);
        Assert.assertEquals(f.length, fc.length);
        Assert.assertEquals("c1ccccc1n2cccc2", f[0]);

        for (int i = 0; i < f.length; i++) {
            String newsmiles = sg.createSMILES(fc[i]);
            Assert.assertTrue(f[i] + " did not match the container, " + newsmiles, f[i].equals(newsmiles));
        }
    }

}