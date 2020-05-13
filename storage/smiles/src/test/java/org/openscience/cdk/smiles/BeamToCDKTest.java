/*
 * Copyright (c) 2013 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.smiles;

import com.google.common.collect.FluentIterable;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.stereo.ExtendedTetrahedral;
import uk.ac.ebi.beam.AtomBuilder;
import uk.ac.ebi.beam.Bond;
import uk.ac.ebi.beam.Graph;
import uk.ac.ebi.beam.Element;
import uk.ac.ebi.beam.Functions;

import java.io.IOException;
import java.util.Iterator;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.openscience.cdk.CDKConstants.ATOM_ATOM_MAPPING;
import static org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo.ANTI_CLOCKWISE;
import static org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo.CLOCKWISE;

/**
 * @author John May
 * @cdk.module test-smiles
 */
public class BeamToCDKTest {

    private final IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    private final BeamToCDK          g2c     = new BeamToCDK(builder);

    @Test
    public void newUnknownAtom() {
        IAtom a = g2c.newCDKAtom(AtomBuilder.aliphatic(Element.Unknown).build());
        assertThat(a, is(instanceOf(IPseudoAtom.class)));
        assertThat(((IPseudoAtom) a).getLabel(), is("*"));
    }

    @Test
    public void newCarbonAtom() {
        IAtom a = g2c.newCDKAtom(AtomBuilder.aliphatic(Element.Carbon).build());
        assertThat(a, is(instanceOf(IAtom.class)));
        assertThat(a, is(not(instanceOf(IPseudoAtom.class))));
        assertThat(a.getSymbol(), is("C"));
    }

    @Test
    public void newNitrogenAtom() {
        IAtom a = g2c.newCDKAtom(AtomBuilder.aliphatic(Element.Nitrogen).build());
        assertThat(a, is(instanceOf(IAtom.class)));
        assertThat(a, is(not(instanceOf(IPseudoAtom.class))));
        assertThat(a.getSymbol(), is("N"));
    }

    @Test
    public void methaneAtom() {
        IAtom a = g2c.toCDKAtom(AtomBuilder.aliphatic(Element.Carbon).hydrogens(4).build(), 4);
        assertThat(a.getSymbol(), is("C"));
        assertThat(a.getImplicitHydrogenCount(), is(4));
    }

    @Test
    public void waterAtom() {
        IAtom a = g2c.toCDKAtom(AtomBuilder.aliphatic(Element.Oxygen).hydrogens(2).build(), 2);
        assertThat(a.getSymbol(), is("O"));
        assertThat(a.getImplicitHydrogenCount(), is(2));
    }

    @Test
    public void oxidanide() {
        IAtom a = g2c.toCDKAtom(AtomBuilder.aliphatic(Element.Oxygen).hydrogens(1).anion().build(), 1);
        assertThat(a.getSymbol(), is("O"));
        assertThat(a.getImplicitHydrogenCount(), is(1));
        assertThat(a.getFormalCharge(), is(-1));
    }

    @Test
    public void azaniumAtom() {
        IAtom a = g2c.toCDKAtom(AtomBuilder.aliphatic(Element.Nitrogen).hydrogens(4).cation().build(), 4);
        assertThat(a.getSymbol(), is("N"));
        assertThat(a.getImplicitHydrogenCount(), is(4));
        assertThat(a.getFormalCharge(), is(+1));
    }

    @Test
    public void unspecifiedMass() {
        IAtom a = g2c.toCDKAtom(AtomBuilder.aliphatic(Element.Carbon).hydrogens(4).build(), 4);
        assertNull(a.getMassNumber());
    }

    @Test
    public void carbon_12() {
        IAtom a = g2c.toCDKAtom(AtomBuilder.aliphatic(Element.Carbon).hydrogens(4).isotope(12).build(), 4);
        assertThat(a.getMassNumber(), is(12));
    }

    @Test
    public void carbon_13() {
        IAtom a = g2c.toCDKAtom(AtomBuilder.aliphatic(Element.Carbon).hydrogens(4).isotope(13).build(), 4);
        assertThat(a.getMassNumber(), is(13));
    }

    @Test
    public void carbon_14() {
        IAtom a = g2c.toCDKAtom(AtomBuilder.aliphatic(Element.Carbon).hydrogens(4).isotope(14).build(), 4);
        assertThat(a.getMassNumber(), is(14));
    }

    @Test
    public void aromatic() {
        IAtom a = g2c.toCDKAtom(AtomBuilder.aromatic(Element.Carbon).build(), 0);
        assertTrue(a.getFlag(CDKConstants.ISAROMATIC));
    }

    @Test
    public void benzene() throws IOException {
        IAtomContainer ac = convert("c1ccccc1");
        assertThat(ac.getAtomCount(), is(6));
        assertThat(ac.getBondCount(), is(6));
        for (IAtom a : ac.atoms()) {
            assertThat(a.getSymbol(), is("C"));
            assertTrue(a.getFlag(CDKConstants.ISAROMATIC));
            assertThat(a.getImplicitHydrogenCount(), is(1));
        }
        for (IBond b : ac.bonds()) {
            assertThat(b.getOrder(), is(IBond.Order.UNSET));
            assertTrue(b.getFlag(CDKConstants.ISAROMATIC));
        }
    }

    @Test
    public void benzene_kekule() throws IOException {
        IAtomContainer ac = convert("C=1C=CC=CC1");
        assertThat(ac.getAtomCount(), is(6));
        assertThat(ac.getBondCount(), is(6));
        for (IAtom a : ac.atoms()) {
            assertThat(a.getSymbol(), is("C"));
            assertThat(a.getImplicitHydrogenCount(), is(1));
        }

        assertThat(ac.getBond(ac.getAtom(0), ac.getAtom(1)).getOrder(), is(IBond.Order.SINGLE));
        assertThat(ac.getBond(ac.getAtom(1), ac.getAtom(2)).getOrder(), is(IBond.Order.DOUBLE));
        assertThat(ac.getBond(ac.getAtom(2), ac.getAtom(3)).getOrder(), is(IBond.Order.SINGLE));
        assertThat(ac.getBond(ac.getAtom(3), ac.getAtom(4)).getOrder(), is(IBond.Order.DOUBLE));
        assertThat(ac.getBond(ac.getAtom(4), ac.getAtom(5)).getOrder(), is(IBond.Order.SINGLE));
        assertThat(ac.getBond(ac.getAtom(5), ac.getAtom(0)).getOrder(), is(IBond.Order.DOUBLE));

        assertFalse(ac.getBond(0).getFlag(CDKConstants.ISAROMATIC));
        assertFalse(ac.getBond(1).getFlag(CDKConstants.ISAROMATIC));
        assertFalse(ac.getBond(2).getFlag(CDKConstants.ISAROMATIC));
        assertFalse(ac.getBond(3).getFlag(CDKConstants.ISAROMATIC));
        assertFalse(ac.getBond(4).getFlag(CDKConstants.ISAROMATIC));
        assertFalse(ac.getBond(5).getFlag(CDKConstants.ISAROMATIC));
    }

    @Test
    public void imidazole() throws IOException {

        IAtomContainer ac = convert("c1[nH]cnc1");
        assertThat(ac.getAtomCount(), is(5));
        assertThat(ac.getBondCount(), is(5));

        for (IAtom a : ac.atoms())
            assertTrue(a.getFlag(CDKConstants.ISAROMATIC));

        assertThat(ac.getAtom(0).getSymbol(), is("C"));
        assertThat(ac.getAtom(1).getSymbol(), is("N"));
        assertThat(ac.getAtom(2).getSymbol(), is("C"));
        assertThat(ac.getAtom(3).getSymbol(), is("N"));
        assertThat(ac.getAtom(4).getSymbol(), is("C"));

        assertThat(ac.getAtom(0).getImplicitHydrogenCount(), is(1));
        assertThat(ac.getAtom(1).getImplicitHydrogenCount(), is(1));
        assertThat(ac.getAtom(2).getImplicitHydrogenCount(), is(1));
        assertThat(ac.getAtom(3).getImplicitHydrogenCount(), is(0));
        assertThat(ac.getAtom(4).getImplicitHydrogenCount(), is(1));

        for (IAtom a : ac.atoms()) {
            assertTrue(a.getFlag(CDKConstants.ISAROMATIC));
        }

        for (IBond b : ac.bonds()) {
            assertThat(b.getOrder(), is(IBond.Order.UNSET));
            assertTrue(b.getFlag(CDKConstants.ISAROMATIC));
        }
    }

    @Test
    public void imidazole_kekule() throws IOException {

        IAtomContainer ac = convert("N1C=CN=C1");
        assertThat(ac.getAtomCount(), is(5));
        assertThat(ac.getBondCount(), is(5));

        for (IAtom a : ac.atoms())
            assertFalse(a.getFlag(CDKConstants.ISAROMATIC));

        assertThat(ac.getAtom(0).getSymbol(), is("N"));
        assertThat(ac.getAtom(1).getSymbol(), is("C"));
        assertThat(ac.getAtom(2).getSymbol(), is("C"));
        assertThat(ac.getAtom(3).getSymbol(), is("N"));
        assertThat(ac.getAtom(4).getSymbol(), is("C"));

        assertThat(ac.getAtom(0).getImplicitHydrogenCount(), is(1));
        assertThat(ac.getAtom(1).getImplicitHydrogenCount(), is(1));
        assertThat(ac.getAtom(2).getImplicitHydrogenCount(), is(1));
        assertThat(ac.getAtom(3).getImplicitHydrogenCount(), is(0));
        assertThat(ac.getAtom(4).getImplicitHydrogenCount(), is(1));

        for (IAtom a : ac.atoms()) {
            assertFalse(a.getFlag(CDKConstants.ISAROMATIC));
        }

        assertThat(ac.getBond(ac.getAtom(0), ac.getAtom(1)).getOrder(), is(IBond.Order.SINGLE));
        assertThat(ac.getBond(ac.getAtom(1), ac.getAtom(2)).getOrder(), is(IBond.Order.DOUBLE));
        assertThat(ac.getBond(ac.getAtom(2), ac.getAtom(3)).getOrder(), is(IBond.Order.SINGLE));
        assertThat(ac.getBond(ac.getAtom(3), ac.getAtom(4)).getOrder(), is(IBond.Order.DOUBLE));
        assertThat(ac.getBond(ac.getAtom(4), ac.getAtom(0)).getOrder(), is(IBond.Order.SINGLE));

        for (IBond b : ac.bonds()) {
            assertFalse(b.getFlag(CDKConstants.ISAROMATIC));
        }
    }

    /**
     * (2R)-butan-2-ol
     *
     * @cdk.inchi InChI=1/C4H10O/c1-3-4(2)5/h4-5H,3H2,1-2H3/t4-/s2
     */
    @Test
    public void _2R_butan_2_ol() throws Exception {
        IAtomContainer ac = convert("CC[C@@](C)(O)[H]");

        IStereoElement se = FluentIterable.from(ac.stereoElements()).first().get();

        assertThat(se, is(instanceOf(ITetrahedralChirality.class)));

        ITetrahedralChirality tc = (ITetrahedralChirality) se;

        assertThat(tc.getChiralAtom(), is(ac.getAtom(2)));
        assertThat(tc.getLigands(), is(new IAtom[]{ac.getAtom(1), ac.getAtom(3), ac.getAtom(4), ac.getAtom(5)}));

        assertThat(tc.getStereo(), is(ITetrahedralChirality.Stereo.CLOCKWISE));
    }

    /**
     * (2S)-butan-2-ol
     *
     * @cdk.inchi InChI=1/C4H10O/c1-3-4(2)5/h4-5H,3H2,1-2H3/t4-/s2
     */
    @Test
    public void _2S_butan_2_ol() throws Exception {
        IAtomContainer ac = convert("CC[C@](C)(O)[H]");

        IStereoElement se = FluentIterable.from(ac.stereoElements()).first().get();

        assertThat(se, is(instanceOf(ITetrahedralChirality.class)));

        ITetrahedralChirality tc = (ITetrahedralChirality) se;

        assertThat(tc.getChiralAtom(), is(ac.getAtom(2)));
        assertThat(tc.getLigands(), is(new IAtom[]{ac.getAtom(1), ac.getAtom(3), ac.getAtom(4), ac.getAtom(5)}));
        assertThat(tc.getStereo(), is(ANTI_CLOCKWISE));
    }

    /**
     * (4as,8as)-decahydronaphthalene-4a,8a-diol
     *
     * @cdk.inchi InChI=1/C10H18O2/c11-9-5-1-2-6-10(9,12)8-4-3-7-9/h11-12H,1-8H2/t9-,10+
     */
    @Test
    public void tetrahedralRingClosure() throws Exception {
        IAtomContainer ac = convert("O[C@]12CCCC[C@@]1(O)CCCC2");

        IStereoElement[] ses = FluentIterable.from(ac.stereoElements()).toArray(IStereoElement.class);

        assertThat(ses.length, is(2));
        assertThat(ses[0], is(instanceOf(ITetrahedralChirality.class)));
        assertThat(ses[1], is(instanceOf(ITetrahedralChirality.class)));

        ITetrahedralChirality tc1 = (ITetrahedralChirality) ses[0];
        ITetrahedralChirality tc2 = (ITetrahedralChirality) ses[1];

        // we want the second atom stereo as tc1
        if (ac.indexOf(tc1.getChiralAtom()) > ac.indexOf(tc2.getChiralAtom())) {
            ITetrahedralChirality swap = tc1;
            tc1 = tc2;
            tc2 = swap;
        }

        assertThat(tc1.getChiralAtom(), is(ac.getAtom(1)));
        assertThat(tc1.getLigands(), is(new IAtom[]{ac.getAtom(0), ac.getAtom(2), ac.getAtom(6), ac.getAtom(11)}));
        assertThat(tc1.getStereo(), is(ANTI_CLOCKWISE));

        // the configuration around atom 6 flips as the ring closure '[C@@]1'
        // is the first atom (when ordered) but not in the configuration - when
        // we order the atoms by their index the tetrahedral configuration goes
        // from clockwise in the SMILES to anti-clockwise ('@'). Writing out the
        // SMILES again one can see it will flip back clockwise ('@@').

        assertThat(tc2.getChiralAtom(), is(ac.getAtom(6)));
        assertThat(tc2.getLigands(), is(new IAtom[]{ac.getAtom(1), ac.getAtom(5), ac.getAtom(7), ac.getAtom(8)}));
        assertThat(tc2.getStereo(), is(ANTI_CLOCKWISE));
    }

    /**
     * (E)-1,2-difluoroethene
     *
     * @cdk.inchi InChI=1/C2H2F2/c3-1-2-4/h1-2H/b2-1+
     */
    @Test
    public void e_1_2_difluroethene() throws Exception {

        IAtomContainer ac = convert("F/C=C/F");

        IStereoElement se = FluentIterable.from(ac.stereoElements()).first().get();

        assertThat(se, is(instanceOf(IDoubleBondStereochemistry.class)));

        IDoubleBondStereochemistry dbs = (IDoubleBondStereochemistry) se;
        assertThat(dbs.getStereoBond(), is(ac.getBond(ac.getAtom(1), ac.getAtom(2))));
        assertThat(dbs.getBonds(),
                is(new IBond[]{ac.getBond(ac.getAtom(0), ac.getAtom(1)), ac.getBond(ac.getAtom(2), ac.getAtom(3))}));
        assertThat(dbs.getStereo(), is(IDoubleBondStereochemistry.Conformation.OPPOSITE));
    }

    /**
     * (Z)-1,2-difluoroethene
     *
     * @cdk.inchi InChI=1/C2H2F2/c3-1-2-4/h1-2H/b2-1+
     */
    @Test
    public void z_1_2_difluroethene() throws Exception {

        IAtomContainer ac = convert("F/C=C\\F");

        IStereoElement se = FluentIterable.from(ac.stereoElements()).first().get();

        assertThat(se, is(instanceOf(IDoubleBondStereochemistry.class)));

        IDoubleBondStereochemistry dbs = (IDoubleBondStereochemistry) se;
        assertThat(dbs.getStereoBond(), is(ac.getBond(ac.getAtom(1), ac.getAtom(2))));
        assertThat(dbs.getBonds(),
                is(new IBond[]{ac.getBond(ac.getAtom(0), ac.getAtom(1)), ac.getBond(ac.getAtom(2), ac.getAtom(3))}));
        assertThat(dbs.getStereo(), is(IDoubleBondStereochemistry.Conformation.TOGETHER));
    }

    /**
     * (E)-1,2-difluoroethene
     *
     * @cdk.inchi InChI=1/C2H2F2/c3-1-2-4/h1-2H/b2-1+
     */
    @Test
    public void e_1_2_difluroethene_explicit() throws Exception {

        IAtomContainer ac = convert("F/C([H])=C(\\[H])F");

        IStereoElement se = FluentIterable.from(ac.stereoElements()).first().get();

        assertThat(se, is(instanceOf(IDoubleBondStereochemistry.class)));

        IDoubleBondStereochemistry dbs = (IDoubleBondStereochemistry) se;
        assertThat(dbs.getStereoBond(), is(ac.getBond(ac.getAtom(1), ac.getAtom(3))));
        assertThat(dbs.getBonds(),
                is(new IBond[]{ac.getBond(ac.getAtom(0), ac.getAtom(1)), ac.getBond(ac.getAtom(3), ac.getAtom(4))}));
        // the two 'F' are opposite but we use a H so they are 'together'
        assertThat(dbs.getStereo(), is(IDoubleBondStereochemistry.Conformation.TOGETHER));
    }

    /**
     * (Z)-1,2-difluoroethene
     *
     * @cdk.inchi InChI=1/C2H2F2/c3-1-2-4/h1-2H/b2-1-
     */
    @Test
    public void z_1_2_difluroethene_explicit() throws Exception {

        IAtomContainer ac = convert("FC(\\[H])=C([H])/F");

        IStereoElement se = FluentIterable.from(ac.stereoElements()).first().get();

        assertThat(se, is(instanceOf(IDoubleBondStereochemistry.class)));

        IDoubleBondStereochemistry dbs = (IDoubleBondStereochemistry) se;
        assertThat(dbs.getStereoBond(), is(ac.getBond(ac.getAtom(1), ac.getAtom(3))));
        assertThat(dbs.getBonds(),
                is(new IBond[]{ac.getBond(ac.getAtom(1), ac.getAtom(2)), ac.getBond(ac.getAtom(3), ac.getAtom(5))}));
        // the two 'F' are together but we use a H so they are 'opposite'
        assertThat(dbs.getStereo(), is(IDoubleBondStereochemistry.Conformation.OPPOSITE));
    }

    @Test
    public void readAtomClass() throws Exception {
        IAtomContainer ac = convert("CC[C:2]C");
        assertNotNull(ac.getAtom(2).getProperty(ATOM_ATOM_MAPPING));
        assertThat(ac.getAtom(2).getProperty(ATOM_ATOM_MAPPING, Integer.class), is(2));
    }

    @Test
    public void erroneousLabels_tRNA() throws Exception {
        IAtomContainer ac = convert("[tRNA]CC");
        assertThat(ac.getAtom(0).getSymbol(), is("R"));
        assertThat(ac.getAtom(0), is(instanceOf(IPseudoAtom.class)));
        assertThat(((IPseudoAtom) ac.getAtom(0)).getLabel(), is("tRNA"));
    }

    // believe it or not there are cases of this in the wild -checkout some
    // acyl-carrier-protein SMILES in MetaCyc
    @Test
    public void erroneousLabels_nested() throws Exception {
        IAtomContainer ac = convert("[now-[this]-is-mean]CC");
        assertThat(ac.getAtom(0).getSymbol(), is("R"));
        assertThat(ac.getAtom(0), is(instanceOf(IPseudoAtom.class)));
        assertThat(((IPseudoAtom) ac.getAtom(0)).getLabel(), is("now-[this]-is-mean"));
    }

    @Test(expected = IOException.class)
    public void erroneousLabels_bad1() throws Exception {
        convert("[this]-is-not-okay]CC");
    }

    @Test(expected = IOException.class)
    public void erroneousLabels_bad2() throws Exception {
        convert("[this-[is-not-okay]CC");
    }

    @Test(expected = IOException.class)
    public void erroneousLabels_bad3() throws Exception {
        convert("[this-[is]-not]-okay]CC");
    }

    @Test
    public void extendedTetrahedral_ccw() throws Exception {
        IAtomContainer ac = convert("CC=[C@]=CC");
        Iterator<IStereoElement> elements = ac.stereoElements().iterator();
        assertTrue(elements.hasNext());
        IStereoElement element = elements.next();
        assertThat(element, is(instanceOf(ExtendedTetrahedral.class)));
        ExtendedTetrahedral extendedTetrahedral = (ExtendedTetrahedral) element;
        assertThat(extendedTetrahedral.winding(), is(ANTI_CLOCKWISE));
        assertThat(extendedTetrahedral.focus(), is(ac.getAtom(2)));
        assertThat(extendedTetrahedral.peripherals(),
                is(new IAtom[]{ac.getAtom(0), ac.getAtom(1), ac.getAtom(3), ac.getAtom(4)}));
    }

    @Test
    public void extendedTetrahedral_cw() throws Exception {
        IAtomContainer ac = convert("CC=[C@@]=CC");
        Iterator<IStereoElement> elements = ac.stereoElements().iterator();
        assertTrue(elements.hasNext());
        IStereoElement element = elements.next();
        assertThat(element, is(instanceOf(ExtendedTetrahedral.class)));
        ExtendedTetrahedral extendedTetrahedral = (ExtendedTetrahedral) element;
        assertThat(extendedTetrahedral.winding(), is(CLOCKWISE));
        assertThat(extendedTetrahedral.focus(), is(ac.getAtom(2)));
        assertThat(extendedTetrahedral.peripherals(),
                is(new IAtom[]{ac.getAtom(0), ac.getAtom(1), ac.getAtom(3), ac.getAtom(4)}));
    }

    @Test public void titleWithTab() throws Exception {
        assertEquals(convert("CN1C=NC2=C1C(=O)N(C(=O)N2C)C\tcaffeine").getTitle(),
                     "caffeine");
    }

    @Test public void titleWithSpace() throws Exception {
        assertEquals(convert("CN1C=NC2=C1C(=O)N(C(=O)N2C)C caffeine").getTitle(),
                     "caffeine");
    }

    @Test public void titleWithMultipleSpace() throws Exception {
        assertEquals(convert("CN1C=NC2=C1C(=O)N(C(=O)N2C)C caffeine compound").getTitle(),
                     "caffeine compound");
    }

    IAtomContainer convert(String smi) throws IOException {
        BeamToCDK g2c = new BeamToCDK(SilentChemObjectBuilder.getInstance());
        Graph g = Graph.fromSmiles(smi);
        return g2c.toAtomContainer(g, false);
    }

}
