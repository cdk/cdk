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

import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.silent.Atom;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.Bond;
import org.openscience.cdk.silent.PseudoAtom;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.stereo.DoubleBondStereochemistry;
import org.openscience.cdk.stereo.ExtendedTetrahedral;
import org.openscience.cdk.stereo.TetrahedralChirality;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import uk.ac.ebi.beam.Graph;
import uk.ac.ebi.beam.Element;

import java.util.Collections;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.openscience.cdk.interfaces.IBond.Order.DOUBLE;
import static org.openscience.cdk.interfaces.IBond.Order.SINGLE;
import static org.openscience.cdk.interfaces.IDoubleBondStereochemistry.Conformation.OPPOSITE;
import static org.openscience.cdk.interfaces.IDoubleBondStereochemistry.Conformation.TOGETHER;
import static org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo;
import static org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo.ANTI_CLOCKWISE;
import static org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo.CLOCKWISE;

/**
 * Unit tests for converting CDK IAtomContainer's to the grins object module.
 * For clarity often the SMILES output is verified if a test fails it could be
 * the Grins output changed and there was not a problem with the conversion.
 *
 * @author John May
 * @cdk.module test-smiles
 */
public class CDKToBeamTest {

    @Test(expected = NullPointerException.class)
    public void noImplicitHCount() throws Exception {
        new CDKToBeam().toBeamAtom(new Atom("C"));
    }

    @Test(expected = NullPointerException.class)
    public void noSymbol() throws Exception {
        new CDKToBeam().toBeamAtom(new Atom());
    }

    @Test
    public void unknownSymbol() throws Exception {
        IAtom a = new PseudoAtom("ALA");
        a.setImplicitHydrogenCount(0);
        assertThat(new CDKToBeam().toBeamAtom(a).element(), is(Element.Unknown));
    }

    @Test
    public void unknownSymbol_Pseudo() throws Exception {
        IAtom a = new PseudoAtom("R1");
        a.setImplicitHydrogenCount(0);
        assertThat(new CDKToBeam().toBeamAtom(a).element(), is(Element.Unknown));
    }

    @Test
    public void methane_Atom() throws Exception {
        IAtom a = new Atom("C");
        a.setImplicitHydrogenCount(4);
        assertThat(new CDKToBeam().toBeamAtom(a).element(), is(Element.Carbon));
        assertThat(new CDKToBeam().toBeamAtom(a).hydrogens(), is(4));
    }

    @Test
    public void water_Atom() throws Exception {
        IAtom a = new Atom("O");
        a.setImplicitHydrogenCount(2);
        assertThat(new CDKToBeam().toBeamAtom(a).element(), is(Element.Oxygen));
        assertThat(new CDKToBeam().toBeamAtom(a).hydrogens(), is(2));
    }

    @Test
    public void chargedAtom() throws Exception {
        IAtom a = new Atom("C");
        a.setImplicitHydrogenCount(0);
        for (int chg = -10; chg < 10; chg++) {
            a.setFormalCharge(chg);
            assertThat(new CDKToBeam().toBeamAtom(a).charge(), is(chg));
        }
    }

    @Test
    public void aliphaticAtom() throws Exception {
        IAtom a = new Atom("C");
        a.setImplicitHydrogenCount(0);
        assertFalse(new CDKToBeam().toBeamAtom(a).aromatic());
    }

    @Test
    public void aromaticAtom() throws Exception {
        IAtom a = new Atom("C");
        a.setImplicitHydrogenCount(0);
        a.setFlag(CDKConstants.ISAROMATIC, true);
        assertTrue(new CDKToBeam().toBeamAtom(a).aromatic());
    }

    @Test
    public void unspecifiedIsotope() throws Exception {
        IAtom a = new Atom("C");
        a.setImplicitHydrogenCount(0);
        assertThat(new CDKToBeam().toBeamAtom(a).isotope(), is(-1));
    }

    @Test
    public void specifiedIsotope() throws Exception {
        IAtom a = new Atom("C");
        a.setImplicitHydrogenCount(0);
        a.setMassNumber(13);
        assertThat(new CDKToBeam().toBeamAtom(a).isotope(), is(13));
    }

    @Test
    public void noDefaultIsotope() throws Exception {
        IAtom a = new Atom("C");
        a.setImplicitHydrogenCount(0);
        a.setMassNumber(12);
        assertThat(new CDKToBeam().toBeamAtom(a).isotope(), is(12));
    }

    // special check that a CDK pseudo atom will default to 0 hydrogens if
    // the hydrogens are set to null
    @Test
    public void pseudoAtom_nullH() throws Exception {
        assertThat(new CDKToBeam().toBeamAtom(new PseudoAtom("R")).hydrogens(), is(0));
        assertThat(new CDKToBeam().toBeamAtom(new PseudoAtom("*")).hydrogens(), is(0));
        assertThat(new CDKToBeam().toBeamAtom(new PseudoAtom("R1")).hydrogens(), is(0));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = CDKException.class)
    public void unsetBondOrder() throws Exception {
        IAtom u = mock(IAtom.class);
        IAtom v = mock(IAtom.class);
        IBond b = new Bond(u, v, IBond.Order.UNSET);
        Map<IAtom, Integer> mock = mock(Map.class);
        when(mock.get(u)).thenReturn(0);
        when(mock.get(v)).thenReturn(1);
        new CDKToBeam().toBeamEdge(b, mock);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = CDKException.class)
    public void undefBondOrder() throws Exception {
        IAtom u = mock(IAtom.class);
        IAtom v = mock(IAtom.class);
        IBond b = new Bond(u, v, null);
        Map<IAtom, Integer> mock = mock(Map.class);
        when(mock.get(u)).thenReturn(0);
        when(mock.get(v)).thenReturn(1);
        new CDKToBeam().toBeamEdge(b, mock);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = IllegalArgumentException.class)
    public void tooFewAtoms() throws Exception {
        IBond b = new Bond(new IAtom[]{mock(IAtom.class)});
        new CDKToBeam().toBeamEdge(b, mock(Map.class));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = IllegalArgumentException.class)
    public void tooManyAtoms() throws Exception {
        IBond b = new Bond(new IAtom[]{mock(IAtom.class), mock(IAtom.class), mock(IAtom.class)});
        new CDKToBeam().toBeamEdge(b, mock(Map.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void singleBond() throws Exception {
        IAtom u = mock(IAtom.class);
        IAtom v = mock(IAtom.class);
        IBond b = new Bond(u, v);
        Map<IAtom, Integer> mock = mock(Map.class);
        when(mock.get(u)).thenReturn(0);
        when(mock.get(v)).thenReturn(1);
        CDKToBeam c2g = new CDKToBeam();
        assertThat(c2g.toBeamEdge(b, mock), is(uk.ac.ebi.beam.Bond.SINGLE.edge(0, 1)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void aromaticBond() throws Exception {
        IAtom u = mock(IAtom.class);
        IAtom v = mock(IAtom.class);
        IBond b = new Bond(u, v);
        b.setFlag(CDKConstants.ISAROMATIC, true);
        Map<IAtom, Integer> mock = mock(Map.class);
        when(mock.get(u)).thenReturn(0);
        when(mock.get(v)).thenReturn(1);
        when(u.isAromatic()).thenReturn(true);
        when(v.isAromatic()).thenReturn(true);
        CDKToBeam c2g = new CDKToBeam();
        assertThat(c2g.toBeamEdge(b, mock), is(uk.ac.ebi.beam.Bond.AROMATIC.edge(0, 1)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void doubleBond() throws Exception {
        IAtom u = mock(IAtom.class);
        IAtom v = mock(IAtom.class);
        IBond b = new Bond(u, v, IBond.Order.DOUBLE);
        Map<IAtom, Integer> mock = mock(Map.class);
        when(mock.get(u)).thenReturn(0);
        when(mock.get(v)).thenReturn(1);
        CDKToBeam c2g = new CDKToBeam();
        assertThat(c2g.toBeamEdge(b, mock), is(uk.ac.ebi.beam.Bond.DOUBLE.edge(0, 1)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void tripleBond() throws Exception {
        IAtom u = mock(IAtom.class);
        IAtom v = mock(IAtom.class);
        IBond b = new Bond(u, v, IBond.Order.TRIPLE);
        Map<IAtom, Integer> mock = mock(Map.class);
        when(mock.get(u)).thenReturn(0);
        when(mock.get(v)).thenReturn(1);
        CDKToBeam c2g = new CDKToBeam();
        assertThat(c2g.toBeamEdge(b, mock), is(uk.ac.ebi.beam.Bond.TRIPLE.edge(0, 1)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void quadrupleBond() throws Exception {
        IAtom u = mock(IAtom.class);
        IAtom v = mock(IAtom.class);
        IBond b = new Bond(u, v, IBond.Order.QUADRUPLE);
        Map<IAtom, Integer> mock = mock(Map.class);
        when(mock.get(u)).thenReturn(0);
        when(mock.get(v)).thenReturn(1);
        CDKToBeam c2g = new CDKToBeam();
        assertThat(c2g.toBeamEdge(b, mock), is(uk.ac.ebi.beam.Bond.QUADRUPLE.edge(0, 1)));
    }

    @Test
    public void adeneine() throws Exception {
        Graph g = convert(TestMoleculeFactory.makeAdenine(), 0);
        assertThat(g.toSmiles(), is("C12=C(N=CN=C1N)NC=N2"));
    }

    @Test
    public void benzene_kekule() throws Exception {
        Graph g = convert(TestMoleculeFactory.makeBenzene(), 0);
        assertThat(g.toSmiles(), is("C=1C=CC=CC1"));
    }

    @Test
    public void benzene() throws Exception {
        IAtomContainer ac = TestMoleculeFactory.makeBenzene();
        Graph g = convert(ac, true, SmiFlavor.UseAromaticSymbols);
        assertThat(g.toSmiles(), is("c1ccccc1"));
    }

    @Test
    public void imidazole_kekule() throws Exception {
        Graph g = convert(TestMoleculeFactory.makeImidazole(), false, 0);
        assertThat(g.toSmiles(), is("C=1NC=NC1"));
    }

    @Test
    public void imidazole() throws Exception {
        Graph g = convert(TestMoleculeFactory.makeImidazole(), true, SmiFlavor.UseAromaticSymbols);
        assertThat(g.toSmiles(), is("c1[nH]cnc1"));
    }

    @Test
    public void imidazole_ignoreAromatic() throws Exception {
        Graph g = convert(TestMoleculeFactory.makeImidazole(), true, 0);
        assertThat(g.toSmiles(), is("C=1NC=NC1"));
    }

    @Test
    public void C13_isomeric() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a = new Atom("C");
        a.setMassNumber(13);
        ac.addAtom(a);
        Graph g = convert(ac, SmiFlavor.AtomicMass);
        assertThat(g.atom(0).isotope(), is(13));
        assertThat(g.toSmiles(), is("[13CH4]"));
    }

    @Test
    public void C13_nonIsomeric() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a = new Atom("C");
        a.setMassNumber(13);
        ac.addAtom(a);
        Graph g = convert(ac, false, 0); // non-isomeric
        assertThat(g.atom(0).isotope(), is(-1));
        assertThat(g.toSmiles(), is("C"));
    }

    @Test
    public void azanium() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a = new Atom("N");
        a.setFormalCharge(+1);
        ac.addAtom(a);
        Graph g = convert(ac, 0);
        assertThat(g.atom(0).charge(), is(+1));
        assertThat(g.toSmiles(), is("[NH4+]"));
    }

    @Test
    public void oxidanide() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a = new Atom("O");
        a.setFormalCharge(-1);
        ac.addAtom(a);
        Graph g = convert(ac, 0);
        assertThat(g.atom(0).charge(), is(-1));
        assertThat(g.toSmiles(), is("[OH-]"));
    }

    @Test
    public void oxidandiide() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a = new Atom("O");
        a.setFormalCharge(-2);
        ac.addAtom(a);
        Graph g = convert(ac, 0);
        assertThat(g.atom(0).charge(), is(-2));
        assertThat(g.toSmiles(), is("[O-2]"));
    }

    /**
     * (E)-1,2-difluoroethene
     *
     * @cdk.inchi InChI=1/C2H2F2/c3-1-2-4/h1-2H/b2-1+
     */
    @Test
    public void e_1_2_difluoroethene() throws Exception {

        IAtomContainer ac = new AtomContainer();
        ac.addAtom(new Atom("F"));
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("F"));
        ac.addBond(0, 1, SINGLE);
        ac.addBond(1, 2, DOUBLE);
        ac.addBond(2, 3, SINGLE);

        ac.addStereoElement(new DoubleBondStereochemistry(ac.getBond(1), new IBond[]{ac.getBond(0), ac.getBond(2)},
                OPPOSITE));
        Graph g = convert(ac, SmiFlavor.StereoCisTrans);
        assertThat(g.toSmiles(), is("F/C=C/F"));
    }

    /**
     * (Z)-1,2-difluoroethene
     *
     * @cdk.inchi InChI=1/C2H2F2/c3-1-2-4/h1-2H/b2-1-
     */
    @Test
    public void z_1_2_difluoroethene() throws Exception {

        IAtomContainer ac = new AtomContainer();
        ac.addAtom(new Atom("F"));
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("F"));
        ac.addBond(0, 1, SINGLE);
        ac.addBond(1, 2, DOUBLE);
        ac.addBond(2, 3, SINGLE);

        ac.addStereoElement(new DoubleBondStereochemistry(ac.getBond(1), new IBond[]{ac.getBond(0), ac.getBond(2)},
                TOGETHER));
        Graph g = convert(ac, SmiFlavor.StereoCisTrans);
        assertThat(g.toSmiles(), is("F/C=C\\F"));
    }

    /**
     * (2R)-butan-2-ol
     *
     * @cdk.inchi InChI=1/C4H10O/c1-3-4(2)5/h4-5H,3H2,1-2H3/t4-/s2
     */
    @Test
    public void _2R_butan_2_ol() throws Exception {

        IAtomContainer ac = new AtomContainer();
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("O"));
        ac.addAtom(new Atom("H"));
        ac.addBond(0, 1, SINGLE);
        ac.addBond(1, 2, SINGLE);
        ac.addBond(2, 3, SINGLE);
        ac.addBond(2, 4, SINGLE);
        ac.addBond(2, 5, SINGLE);

        ac.addStereoElement(new TetrahedralChirality(ac.getAtom(2), new IAtom[]{ac.getAtom(1), // C-C
                ac.getAtom(3), // C
                ac.getAtom(4), // O
                ac.getAtom(5), // H
        }, CLOCKWISE));

        Graph g = convert(ac, SmiFlavor.StereoTetrahedral);
        assertThat(g.toSmiles(), is("CC[C@@](C)(O)[H]"));
    }

    /**
     * (2S)-butan-2-ol
     *
     * @cdk.inchi InChI=1/C4H10O/c1-3-4(2)5/h4-5H,3H2,1-2H3/t4-/s2
     */
    @Test
    public void _2S_butan_2_ol() throws Exception {

        IAtomContainer ac = new AtomContainer();
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("O"));
        ac.addAtom(new Atom("H"));
        ac.addBond(0, 1, SINGLE);
        ac.addBond(1, 2, SINGLE);
        ac.addBond(2, 3, SINGLE);
        ac.addBond(2, 4, SINGLE);
        ac.addBond(2, 5, SINGLE);

        ac.addStereoElement(new TetrahedralChirality(ac.getAtom(2), new IAtom[]{ac.getAtom(1), // C-C
                ac.getAtom(3), // C
                ac.getAtom(4), // O
                ac.getAtom(5), // H
        }, ANTI_CLOCKWISE));

        Graph g = convert(ac, SmiFlavor.StereoTetrahedral);
        assertThat(g.toSmiles(), is("CC[C@](C)(O)[H]"));
    }

    /**
     * This is a mock test where we don't want aromatic bonds to have a
     * configuration. (Z)-1,2-difluoroethene is not aromatic but a 'real'
     * example would be porphyrins.
     *
     * @cdk.inchi InChI=1/C2H2F2/c3-1-2-4/h1-2H/b2-1-
     */
    @Test
    public void z_1_2_difluoroethene_aromatic() throws Exception {

        IAtomContainer ac = new AtomContainer();
        ac.addAtom(new Atom("F"));
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("F"));
        ac.addBond(0, 1, SINGLE);
        ac.addBond(1, 2, DOUBLE);
        ac.addBond(2, 3, SINGLE);
        ac.getAtom(1).setIsAromatic(true);
        ac.getAtom(2).setIsAromatic(true);

        ac.getBond(1).setFlag(CDKConstants.ISAROMATIC, true);

        ac.addStereoElement(new DoubleBondStereochemistry(ac.getBond(1), new IBond[]{ac.getBond(0), ac.getBond(2)},
                TOGETHER));
        Graph g = convert(ac, SmiFlavor.UseAromaticSymbols);
        assertThat(g.toSmiles(), is("FccF"));
    }

    @Test
    public void propadiene() throws Exception {

    }

    @Test
    public void writeAtomClass() throws Exception {
        IAtomContainer ac = new AtomContainer();
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("C"));
        ac.addAtom(new Atom("O"));
        ac.addBond(0, 1, SINGLE);
        ac.addBond(1, 2, SINGLE);
        ac.getAtom(0).setProperty(CDKConstants.ATOM_ATOM_MAPPING, 3);
        ac.getAtom(1).setProperty(CDKConstants.ATOM_ATOM_MAPPING, 1);
        ac.getAtom(2).setProperty(CDKConstants.ATOM_ATOM_MAPPING, 2);
        assertThat(convert(ac, SmiFlavor.AtomAtomMap).toSmiles(), is("[CH3:3][CH2:1][OH:2]"));
    }

    @Test
    public void r_penta_2_3_diene_impl_h() throws Exception {
        IAtomContainer m = new AtomContainer(5, 4, 0, 0);
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.DOUBLE);
        m.addBond(2, 3, IBond.Order.DOUBLE);
        m.addBond(3, 4, IBond.Order.SINGLE);

        IStereoElement element = new ExtendedTetrahedral(m.getAtom(2), new IAtom[]{m.getAtom(0), m.getAtom(1),
                m.getAtom(3), m.getAtom(4)}, ANTI_CLOCKWISE);
        m.setStereoElements(Collections.singletonList(element));

        assertThat(convert(m, SmiFlavor.Stereo).toSmiles(), is("CC=[C@]=CC"));
    }

    @Test
    public void s_penta_2_3_diene_impl_h() throws Exception {
        IAtomContainer m = new AtomContainer(5, 4, 0, 0);
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.DOUBLE);
        m.addBond(2, 3, IBond.Order.DOUBLE);
        m.addBond(3, 4, IBond.Order.SINGLE);

        IStereoElement element = new ExtendedTetrahedral(m.getAtom(2), new IAtom[]{m.getAtom(0), m.getAtom(1),
                m.getAtom(3), m.getAtom(4)}, CLOCKWISE);
        m.setStereoElements(Collections.singletonList(element));

        assertThat(convert(m, SmiFlavor.Stereo).toSmiles(), is("CC=[C@@]=CC"));
    }

    @Test
    public void r_penta_2_3_diene_expl_h() throws Exception {
        IAtomContainer m = new AtomContainer(5, 4, 0, 0);
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("H"));
        m.addAtom(new Atom("H"));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.DOUBLE);
        m.addBond(2, 3, IBond.Order.DOUBLE);
        m.addBond(3, 4, IBond.Order.SINGLE);
        m.addBond(1, 5, IBond.Order.SINGLE);
        m.addBond(3, 6, IBond.Order.SINGLE);

        int[][] atoms = new int[][]{{0, 5, 6, 4}, {5, 0, 6, 4}, {5, 0, 4, 6}, {0, 5, 4, 6}, {4, 6, 5, 0}, {4, 6, 0, 5},
                {6, 4, 0, 5}, {6, 4, 5, 0},};
        Stereo[] stereos = new Stereo[]{Stereo.ANTI_CLOCKWISE, Stereo.CLOCKWISE, Stereo.ANTI_CLOCKWISE,
                Stereo.CLOCKWISE, Stereo.ANTI_CLOCKWISE, Stereo.CLOCKWISE, Stereo.ANTI_CLOCKWISE, Stereo.CLOCKWISE};

        for (int i = 0; i < atoms.length; i++) {

            IStereoElement element = new ExtendedTetrahedral(m.getAtom(2), new IAtom[]{m.getAtom(atoms[i][0]),
                    m.getAtom(atoms[i][1]), m.getAtom(atoms[i][2]), m.getAtom(atoms[i][3])}, stereos[i]);
            m.setStereoElements(Collections.singletonList(element));

            assertThat(convert(m, SmiFlavor.Stereo).toSmiles(), is("CC(=[C@@]=C(C)[H])[H]"));

        }
    }

    @Test
    public void s_penta_2_3_diene_expl_h() throws Exception {
        IAtomContainer m = new AtomContainer(5, 4, 0, 0);
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("C"));
        m.addAtom(new Atom("H"));
        m.addAtom(new Atom("H"));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.DOUBLE);
        m.addBond(2, 3, IBond.Order.DOUBLE);
        m.addBond(3, 4, IBond.Order.SINGLE);
        m.addBond(1, 5, IBond.Order.SINGLE);
        m.addBond(3, 6, IBond.Order.SINGLE);

        int[][] atoms = new int[][]{{0, 5, 6, 4}, {5, 0, 6, 4}, {5, 0, 4, 6}, {0, 5, 4, 6}, {4, 6, 5, 0}, {4, 6, 0, 5},
                {6, 4, 0, 5}, {6, 4, 5, 0},};
        Stereo[] stereos = new Stereo[]{Stereo.CLOCKWISE, Stereo.ANTI_CLOCKWISE, Stereo.CLOCKWISE,
                Stereo.ANTI_CLOCKWISE, Stereo.CLOCKWISE, Stereo.ANTI_CLOCKWISE, Stereo.CLOCKWISE, Stereo.ANTI_CLOCKWISE};

        for (int i = 0; i < atoms.length; i++) {

            IStereoElement element = new ExtendedTetrahedral(m.getAtom(2), new IAtom[]{m.getAtom(atoms[i][0]),
                    m.getAtom(atoms[i][1]), m.getAtom(atoms[i][2]), m.getAtom(atoms[i][3])}, stereos[i]);
            m.setStereoElements(Collections.singletonList(element));

            assertThat(convert(m, SmiFlavor.Stereo).toSmiles(), is("CC(=[C@]=C(C)[H])[H]"));

        }
    }

    static Graph convert(IAtomContainer ac, int options) throws Exception {
        return convert(ac, false, options);
    }

    static Graph convert(IAtomContainer ac, boolean perceiveAromaticity, int options) throws Exception {
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(ac);
        CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance()).addImplicitHydrogens(ac);
        if (perceiveAromaticity) Aromaticity.cdkLegacy().apply(ac);
        return new CDKToBeam(options).toBeamGraph(ac);
    }
}
