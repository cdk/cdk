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

package org.openscience.cdk.graph.invariant;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.xmlcml.euclid.Int;

import java.util.Comparator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openscience.cdk.graph.GraphUtil.toAdjList;

/**
 * @author John May
 * @cdk.module test-standard
 */
public class CanonTest {

    @Test
    public void phenol_symmetry() throws Exception {
        IAtomContainer m = smi("OC1=CC=CC=C1");
        long[] symmetry = Canon.symmetry(m, GraphUtil.toAdjList(m));
        assertThat(symmetry, is(new long[]{1, 7, 5, 3, 2, 3, 5}));
    }

    @Test
    public void phenol_labelling() throws Exception {
        IAtomContainer m = smi("OC1=CC=CC=C1");
        long[] labels = Canon.label(m, GraphUtil.toAdjList(m));
        assertThat(labels, is(new long[]{1, 7, 5, 3, 2, 4, 6}));
    }

    @Test
    public void atomComparatorLabelling() throws Exception {
        IAtomContainer m = smi("c1ccccc1O");
        long[] labels = Canon.label(m, GraphUtil.toAdjList(m),
                new Comparator<IAtom>() {
                    @Override
                    public int compare(IAtom o1, IAtom o2) {
                        return Integer.compare(o1.getAtomicNumber(), o2.getAtomicNumber());
                    }
                });
        for (int i = 0; i<labels.length; i++)
            m.getAtom(i).setProperty(CDKConstants.ATOM_ATOM_MAPPING,
                                     (int)labels[i]);
        assertThat(smigen(m), is("[CH:4]1=[CH:2][CH:1]=[CH:3][CH:5]=[C:6]1[OH:7]"));
        long[] labels2 = Canon.label(m, GraphUtil.toAdjList(m),
                new Comparator<IAtom>() {
                    @Override
                    public int compare(IAtom o1, IAtom o2) {
                        return -Integer.compare(o1.getAtomicNumber(), o2.getAtomicNumber());
                    }
                });
        for (int i = 0; i<labels.length; i++)
            m.getAtom(i).setProperty(CDKConstants.ATOM_ATOM_MAPPING,
                    (int)labels2[i]);
        assertThat(smigen(m), is("[CH:5]1=[CH:3][CH:2]=[CH:4][CH:6]=[C:7]1[OH:1]"));
    }

    /**
     * Ensure we consider the previous rank when we shatter ranks. This molecule
     * has a carbons/sulphurs which experience the same environment. We must
     * consider that they are different (due to their initial label) but not
     * their environment.
     *
     * @cdk.inchi InChI=1/C2H4S5/c1-3-4-2-6-7-5-1/h1-2H2
     */
    @Test
    public void lenthionine_symmetry() throws Exception {
        IAtomContainer m = smi("C1SSCSSS1");
        long[] labels = Canon.symmetry(m, GraphUtil.toAdjList(m));
        assertThat(labels, is(new long[]{6, 4, 4, 6, 2, 1, 2}));
    }

    @Test
    public void testBasicInvariants_ethanol() throws Exception {
        IAtomContainer m = smi("CCO");
        long[] exp = new long[]{1065731, 1082114, 541697};
        long[] act = Canon.basicInvariants(m, toAdjList(m));
        assertThat(act, is(exp));
    }

    @Test
    public void testBasicInvariants_phenol() throws Exception {
        IAtomContainer m = smi("OC1=CC=CC=C1");
        long[] exp = new long[]{541697, 836352, 819969, 819969, 819969, 819969, 819969};
        long[] act = Canon.basicInvariants(m, toAdjList(m));
        assertThat(act, is(exp));
    }

    @Test
    public void terminalExplicitHydrogensAreNotIncluded() throws Exception {
        IAtomContainer m = smi("C/C=C(/C)C[H]");
        boolean[] mask = Canon.terminalHydrogens(m, GraphUtil.toAdjList(m));
        assertThat(mask, is(new boolean[]{false, false, false, false, false, true}));
    }

    @Test
    public void bridgingExplicitHydrogensAreIncluded() throws Exception {
        IAtomContainer m = smi("B1[H]B[H]1");
        boolean[] mask = Canon.terminalHydrogens(m, GraphUtil.toAdjList(m));
        assertThat(mask, is(new boolean[]{false, false, false, false}));
    }

    @Test
    public void explicitHydrogensIonsAreIncluded() throws Exception {
        IAtomContainer m = smi("[H+]");
        boolean[] mask = Canon.terminalHydrogens(m, GraphUtil.toAdjList(m));
        assertThat(mask, is(new boolean[]{false}));
    }

    @Test
    public void molecularHydrogensAreNotIncluded() throws Exception {
        IAtomContainer m = smi("[H][H]");
        boolean[] mask = Canon.terminalHydrogens(m, GraphUtil.toAdjList(m));
        assertThat(mask, is(new boolean[]{true, true}));
    }

    @Test
    public void explicitHydrogensOfEthanolHaveSymmetry() throws Exception {
        IAtomContainer m = smi("C([H])([H])C([H])([H])O");
        long[] symmetry = Canon.symmetry(m, GraphUtil.toAdjList(m));
        assertThat(symmetry, is(new long[]{6, 1, 1, 7, 3, 3, 5}));
    }

    @Test
    public void explicitHydrogensDoNotAffectHeavySymmetry() throws Exception {
        IAtomContainer m = smi("CC=C(C)C[H]");
        long[] symmetry = Canon.symmetry(m, GraphUtil.toAdjList(m));
        assertThat(symmetry, is(new long[]{4, 2, 3, 5, 5, 1}));
    }

    @Test
    public void canonicallyLabelEthaneWithInConsistentHydrogenRepresentation() throws Exception {
        IAtomContainer m = smi("CC[H]");
        long[] labels = Canon.label(m, GraphUtil.toAdjList(m));
        org.hamcrest.MatcherAssert.assertThat(labels, is(new long[]{2, 3, 1}));
    }

    @Test
    public void canonicallyLabelEthaneWithInConsistentHydrogenRepresentation2() throws Exception {
        IAtomContainer m = smi("CC([H])([H])");
        long[] labels = Canon.label(m, GraphUtil.toAdjList(m));
        org.hamcrest.MatcherAssert.assertThat(labels, is(new long[]{3, 4, 1, 2}));
    }

    @Test
    public void canonicallyLabelCaffeineWithExplicitHydrogenRepresentation() throws Exception {
        IAtomContainer m = smi("[H]C1=NC2=C(N1C([H])([H])[H])C(=O)N(C(=O)N2C([H])([H])[H])C([H])([H])[H]");
        long[] labels = Canon.label(m, GraphUtil.toAdjList(m));
        org.hamcrest.MatcherAssert.assertThat(labels, is(new long[]{1, 14, 13, 16, 18, 19, 22, 2, 3, 4, 15, 11, 20, 17, 12, 21, 24, 8, 9,
                10, 23, 5, 6, 7}));
    }

    static final SmilesParser    sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
    static final SmilesGenerator sg = new SmilesGenerator(SmiFlavor.Isomeric | SmiFlavor.AtomAtomMap);

    static IAtomContainer smi(String smi) throws Exception {
        return sp.parseSmiles(smi);
    }

    static String smigen(IAtomContainer mol) throws Exception {
        return sg.create(mol);
    }
}
