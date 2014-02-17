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

import org.junit.Test;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openscience.cdk.graph.GraphUtil.toAdjList;

/**
 * @author John May
 * @cdk.module test-standard
 */
public class CanonTest {
    
    @Test public void phenol_symmetry() throws Exception {
        IAtomContainer m = smi("OC1=CC=CC=C1");
        long[] symmetry = Canon.symmetry(m, GraphUtil.toAdjList(m));
        assertThat(symmetry, is(new long[]{1, 7, 5, 3, 2, 3, 5}));
    }

    @Test public void phenol_labelling() throws Exception {
        IAtomContainer m = smi("OC1=CC=CC=C1");
        long[] labels = Canon.label(m, GraphUtil.toAdjList(m));
        assertThat(labels, is(new long[]{1, 7, 5, 3, 2, 4, 6}));
    }

    /**
     * Ensure we consider the previous rank when we shatter ranks. This molecule
     * has a carbons/sulphurs which experience the same environment. We must
     * consider that they are different (due to their initial label) but not 
     * their environment.
     * 
     * @cdk.inchi InChI=1/C2H4S5/c1-3-4-2-6-7-5-1/h1-2H2
     */
    @Test public void lenthionine_symmetry() throws Exception {
        IAtomContainer m = smi("C1SSCSSS1");
        long[] labels = Canon.symmetry(m, GraphUtil.toAdjList(m));
        assertThat(labels, is(new long[]{6, 4, 4, 6, 2, 1, 2}));
    }

    @Test public void testBasicInvariants_ethanol() throws Exception {
        IAtomContainer m = smi("CCO");
        long[] exp = new long[]{1065731, 1082114, 541697};
        long[] act = Canon.basicInvariants(m, toAdjList(m));
        assertThat(act, is(exp));
    }

    @Test public void testBasicInvariants_phenol() throws Exception {
        IAtomContainer m = smi("OC1=CC=CC=C1");
        long[] exp = new long[]{541697, 836352, 819969, 819969, 819969, 819969, 819969};
        long[] act = Canon.basicInvariants(m, toAdjList(m));
        assertThat(act, is(exp));
    }

    static final SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());

    static IAtomContainer smi(String smi) throws Exception {
        return sp.parseSmiles(smi);
    }
}
