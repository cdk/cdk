/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.geometry.cip;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.geometry.cip.CIPTool.CIP_CHIRALITY;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @cdk.module test-cip
 */
public class CIPSMILESTest extends CDKTestCase {

    static SmilesParser smiles = new SmilesParser(SilentChemObjectBuilder.getInstance());

    @Test
    public void test() throws Exception {
        IAtomContainer molecule = smiles.parseSmiles("ClC(Br)(I)[H]");
        LigancyFourChirality chirality = CIPTool.defineLigancyFourChirality(molecule, 1, 4, 0, 2, 3, Stereo.CLOCKWISE);
        Assert.assertEquals(CIP_CHIRALITY.R, CIPTool.getCIPChirality(chirality));
    }

    /**
     * Test case that tests sequence recursing of the atomic number rule.
     *
     * @cdk.inchi InChI=1S/C5H12O/c1-3-5(2)4-6/h5-6H,3-4H2,1-2H3/t5-/m1/s1
     *
     * @see #test2methylbutanol_S()
     */
    @Test
    public void test2methylbutanol_R() throws Exception {
        IAtomContainer molecule = smiles.parseSmiles("OCC([H])(C)CC");
        LigancyFourChirality chirality = CIPTool.defineLigancyFourChirality(molecule, 2, 3, 1, 4, 5, Stereo.CLOCKWISE);
        Assert.assertEquals(CIP_CHIRALITY.R, CIPTool.getCIPChirality(chirality));
    }

    /**
     * Test case that tests sequence recursing of the atomic number rule.
     *
     * @cdk.inchi InChI=1S/C5H12O/c1-3-5(2)4-6/h5-6H,3-4H2,1-2H3/t5-/m0/s1
     *
     * @see #test2methylbutanol_R()
     */
    @Test
    public void test2methylbutanol_S() throws Exception {
        IAtomContainer molecule = smiles.parseSmiles("OCC([H])(C)CC");
        LigancyFourChirality chirality = CIPTool.defineLigancyFourChirality(molecule, 2, 3, 1, 4, 5,
                Stereo.ANTI_CLOCKWISE);
        Assert.assertEquals(CIP_CHIRALITY.S, CIPTool.getCIPChirality(chirality));
    }

    @Test
    public void testTwoVersusDoubleBondedOxygen_R() throws Exception {
        IAtomContainer molecule = smiles.parseSmiles("OC(O)C([H])(C)C=O");
        LigancyFourChirality chirality = CIPTool.defineLigancyFourChirality(molecule, 3, 4, 5, 1, 6, Stereo.CLOCKWISE);
        Assert.assertEquals(CIP_CHIRALITY.R, CIPTool.getCIPChirality(chirality));
    }

    @Test
    public void testTwoVersusDoubleBondedOxygen_S() throws Exception {
        IAtomContainer molecule = smiles.parseSmiles("OC(O)C([H])(C)C=O");
        LigancyFourChirality chirality = CIPTool.defineLigancyFourChirality(molecule, 3, 4, 5, 1, 6,
                Stereo.ANTI_CLOCKWISE);
        Assert.assertEquals(CIP_CHIRALITY.S, CIPTool.getCIPChirality(chirality));
    }

    @Test
    public void testImplicitHydrogen() throws Exception {
        IAtomContainer molecule = smiles.parseSmiles("CCC(C)CCC");
        LigancyFourChirality chirality = CIPTool.defineLigancyFourChirality(molecule, 2, CIPTool.HYDROGEN, 3, 1, 4,
                Stereo.ANTI_CLOCKWISE);
        Assert.assertEquals(CIP_CHIRALITY.S, CIPTool.getCIPChirality(chirality));
    }

    @Test(timeout = 5000)
    // 5 seconds should be enough
    public void testTermination() throws Exception {
        IAtomContainer mol = smiles
                .parseSmiles("[H]O[C@]([H])(C1([H])(C([H])([H])C([H])([H])C1([H])([H])))C2([H])(C([H])([H])C2([H])([H]))");
        Iterator<IStereoElement> stereoElements = mol.stereoElements().iterator();
        Assert.assertTrue(stereoElements.hasNext());
        IStereoElement stereo = stereoElements.next();
        Assert.assertNotNull(stereo);
        Assert.assertTrue(stereo instanceof ITetrahedralChirality);
        CIPTool.getCIPChirality(mol, (ITetrahedralChirality) stereo);
    }

    @Test(timeout = 5000)
    // 5 seconds should be enough
    public void testTermination2() throws Exception {
        IAtomContainer mol = smiles.parseSmiles("OC1CCC[C@](F)(CC1)Cl");
        Iterator<IStereoElement> stereoElements = mol.stereoElements().iterator();
        Assert.assertTrue(stereoElements.hasNext());
        IStereoElement stereo = stereoElements.next();
        Assert.assertNotNull(stereo);
        Assert.assertTrue(stereo instanceof ITetrahedralChirality);
        CIPTool.getCIPChirality(mol, (ITetrahedralChirality) stereo);
    }

    @Test
    public void testTetraHalogenMethane() throws Exception {
        IAtomContainer molecule = smiles.parseSmiles("FC(Br)(Cl)I");
        LigancyFourChirality chirality = CIPTool.defineLigancyFourChirality(molecule, 1, 0, 4, 2, 3,
                Stereo.ANTI_CLOCKWISE);
        Assert.assertEquals(CIP_CHIRALITY.R, CIPTool.getCIPChirality(chirality));
    }

    /**
     * @cdk.inchi InChI=1S/C20H20BrN3O3S/c1-23(2)9-10-24(20-22-14-8-7-13(21)11-18(14)28-20)19(25)17-12-26-15-5-3-4-6-16(15)27-17/h3-8,11,17H,9-10,12H2,1-2H3/p+1/t17-/m1/s1
     */
    @Test
    public void testCID42475007_R() throws Exception {
        IAtomContainer mol = smiles.parseSmiles("C[NH+](C)CCN(C1=NC2=C(S1)C=C(C=C2)Br)C(=O)[C@H]3COC4=CC=CC=C4O3");
        Iterator<IStereoElement> stereoElements = mol.stereoElements().iterator();
        Assert.assertTrue(stereoElements.hasNext());
        IStereoElement stereo = stereoElements.next();
        Assert.assertNotNull(stereo);
        Assert.assertTrue(stereo instanceof ITetrahedralChirality);
        Assert.assertEquals(CIP_CHIRALITY.R, CIPTool.getCIPChirality(mol, (ITetrahedralChirality) stereo));
    }

    /**
     * @cdk.inchi InChI=1S/C20H20BrN3O3S/c1-23(2)9-10-24(20-22-14-8-7-13(21)11-18(14)28-20)19(25)17-12-26-15-5-3-4-6-16(15)27-17/h3-8,11,17H,9-10,12H2,1-2H3/p+1/t17+/m1/s1
     */
    @Test
    public void testCID42475007_S() throws Exception {
        IAtomContainer mol = smiles.parseSmiles("C[NH+](C)CCN(C1=NC2=C(S1)C=C(C=C2)Br)C(=O)[C@@H]3COC4=CC=CC=C4O3");
        Iterator<IStereoElement> stereoElements = mol.stereoElements().iterator();
        Assert.assertTrue(stereoElements.hasNext());
        IStereoElement stereo = stereoElements.next();
        Assert.assertNotNull(stereo);
        Assert.assertTrue(stereo instanceof ITetrahedralChirality);
        Assert.assertEquals(CIP_CHIRALITY.S, CIPTool.getCIPChirality(mol, (ITetrahedralChirality) stereo));
    }

    /**
     * @cdk.inchi InChI=1/C4H10OS/c1-3-4-6(2)5/h3-4H2,1-2H3/t6+/s2
     */
    @Test
    public void r_sulfinyl() throws Exception {
        IAtomContainer mol = smiles.parseSmiles("CCC[S@@](C)=O");
        Iterator<IStereoElement> stereoElements = mol.stereoElements().iterator();
        Assert.assertTrue(stereoElements.hasNext());
        IStereoElement stereo = stereoElements.next();
        Assert.assertNotNull(stereo);
        Assert.assertTrue(stereo instanceof ITetrahedralChirality);
        Assert.assertEquals(CIP_CHIRALITY.R, CIPTool.getCIPChirality(mol, (ITetrahedralChirality) stereo));
    }

    /**
     * @cdk.inchi InChI=1/C4H10OS/c1-3-4-6(2)5/h3-4H2,1-2H3/t6-/s2
     */
    @Test
    public void s_sulfinyl() throws Exception {
        IAtomContainer mol = smiles.parseSmiles("CCC[S@](C)=O");
        Iterator<IStereoElement> stereoElements = mol.stereoElements().iterator();
        Assert.assertTrue(stereoElements.hasNext());
        IStereoElement stereo = stereoElements.next();
        Assert.assertNotNull(stereo);
        Assert.assertTrue(stereo instanceof ITetrahedralChirality);
        Assert.assertEquals(CIP_CHIRALITY.S, CIPTool.getCIPChirality(mol, (ITetrahedralChirality) stereo));
    }

    @Test
    public void e_butene() throws Exception {
        assertThat(label("C/C=C/C"), is(CIP_CHIRALITY.E));
        assertThat(label("C\\C=C\\C"), is(CIP_CHIRALITY.E));
    }

    @Test
    public void z_butene() throws Exception {
        assertThat(label("C/C=C\\C"), is(CIP_CHIRALITY.Z));
        assertThat(label("C\\C=C/C"), is(CIP_CHIRALITY.Z));
    }

    @Test
    public void none() throws Exception {
        assertThat(label("C/C=C(/C)C"), is(CIP_CHIRALITY.NONE));
        assertThat(label("C/C(C)=C/C"), is(CIP_CHIRALITY.NONE));
    }

    @Test
    public void e_depth2() throws Exception {
        assertThat(label("CC/C(CO)=C(/CC)CO"), is(CIP_CHIRALITY.E));
        assertThat(label("OC\\C(CC)=C(/CC)CO"), is(CIP_CHIRALITY.E));
    }

    @Test
    public void z_depth2() throws Exception {
        assertThat(label("CC\\C(CO)=C(/CC)CO"), is(CIP_CHIRALITY.Z));
        assertThat(label("OC/C(CC)=C(/CC)CO"), is(CIP_CHIRALITY.Z));
    }

    @Test
    public void one_size_depth2() throws Exception {
        assertThat(label("CC\\C(CO)=C(/C)"), is(CIP_CHIRALITY.E));
    }

    @Test
    public void none_depth2() throws Exception {
        assertThat(label("CC/C(CC)=C(/CC)CO"), is(CIP_CHIRALITY.NONE));
    }

    private final IChemObjectBuilder bldr   = SilentChemObjectBuilder.getInstance();
    private final SmilesParser       smipar = new SmilesParser(bldr);

    /**
     * Get the CIP labelling for a container with a single stereo element.
     *
     * @param smi input smiles
     * @return the labelling
     */
    CIP_CHIRALITY label(String smi) throws Exception {
        return label(smipar.parseSmiles(smi));
    }

    /**
     * Get the CIP labelling for a container with a single stereo element.
     *
     * @param container input container
     * @return the labelling
     */
    CIP_CHIRALITY label(IAtomContainer container) {

        List<IStereoElement> elements = new ArrayList<IStereoElement>();

        for (IStereoElement element : container.stereoElements()) {
            elements.add(element);
        }

        if (elements.size() != 1) Assert.fail("expected 1 stereo-element, found - " + elements.size());

        for (IStereoElement element : elements) {
            if (element instanceof ITetrahedralChirality) {
                return CIPTool.getCIPChirality(container, (ITetrahedralChirality) element);
            } else if (element instanceof IDoubleBondStereochemistry) {
                return CIPTool.getCIPChirality(container, (IDoubleBondStereochemistry) element);
            }
        }

        throw new IllegalStateException();
    }
}
