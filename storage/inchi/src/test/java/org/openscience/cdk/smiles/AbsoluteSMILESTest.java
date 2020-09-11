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

import com.google.common.base.Joiner;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;

/**
 * @author John May
 * @cdk.module test-inchi
 */
public class AbsoluteSMILESTest {

    @Test
    public void myo_inositol() throws Exception {
        test("O[C@H]1[C@H](O)[C@@H](O)[C@H](O)[C@H](O)[C@@H]1O", "O[C@H]1[C@H](O)[C@@H](O)[C@H](O)[C@@H](O)[C@H]1O",
                "O[C@@H]1[C@@H](O)[C@H](O)[C@@H](O)[C@H](O)[C@@H]1O",
                "[C@@H]1(O)[C@H](O)[C@@H](O)[C@H](O)[C@@H](O)[C@@H]1O",
                "[C@@H]1([C@@H](O)[C@@H]([C@H]([C@H](O)[C@H]1O)O)O)O",
                "O[C@H]1[C@@H]([C@@H]([C@H](O)[C@H]([C@@H]1O)O)O)O", "O[C@H]1[C@H](O)[C@@H](O)[C@H](O)[C@H]([C@H]1O)O",
                "[C@H]1(O)[C@H](O)[C@@H](O)[C@@H]([C@H](O)[C@@H]1O)O",
                "O[C@@H]1[C@@H](O)[C@H]([C@H]([C@H](O)[C@H]1O)O)O",
                "[C@@H]1(O)[C@H](O)[C@@H](O)[C@@H]([C@H]([C@@H]1O)O)O",
                "[C@@H]1([C@@H]([C@@H](O)[C@@H]([C@@H](O)[C@H]1O)O)O)O",
                "[C@@H]1([C@H](O)[C@@H](O)[C@@H]([C@@H](O)[C@H]1O)O)O",
                "O[C@H]1[C@@H]([C@H](O)[C@H](O)[C@H]([C@@H]1O)O)O",
                "[C@H]1([C@H](O)[C@H](O)[C@H]([C@@H]([C@H]1O)O)O)O",
                "[C@H]1([C@H](O)[C@@H]([C@@H](O)[C@@H]([C@H]1O)O)O)O",
                "[C@@H]1(O)[C@@H](O)[C@@H]([C@@H](O)[C@H](O)[C@@H]1O)O",
                "[C@H]1(O)[C@@H]([C@H]([C@H](O)[C@@H](O)[C@H]1O)O)O",
                "O[C@H]1[C@H]([C@H](O)[C@@H](O)[C@H](O)[C@H]1O)O", "O[C@H]1[C@H](O)[C@@H](O)[C@H](O)[C@H](O)[C@@H]1O",
                "[C@H]1([C@@H]([C@@H]([C@H]([C@H](O)[C@H]1O)O)O)O)O",
                "[C@@H]1([C@H]([C@@H](O)[C@H](O)[C@H]([C@@H]1O)O)O)O");
    }

    @Test
    public void _1_3_diethylidenecyclobutane() throws Exception {
        test("C/C=C1/CC(=C/C)/C1", "C/C=C/1C\\C(=C/C)C1", "C/1(C\\C(C1)=C/C)=C\\C", "C\\1C(/CC1=C/C)=C\\C",
                "C/C=C1/CC(=C/C)/C1", "C1(=C/C)/CC(=C\\C)/C1", "C\\C=C1/CC(=C\\C)/C1", "C(\\C)=C1/CC(=C/C)/C1",
                "C1\\C(C\\C1=C\\C)=C/C", "C/1(C\\C(=C/C)C1)=C\\C", "C(/C)=C1/CC(=C\\C)/C1", "C/1(C\\C(C1)=C\\C)=C/C",
                "C\\1C(/CC1=C/C)=C\\C", "C\\C=C/1C\\C(=C\\C)C1", "C/1(C\\C(=C\\C)C1)=C/C", "C1(/CC(=C\\C)/C1)=C/C",
                "C1\\C(=C/C)C\\C1=C\\C", "C1(/CC(/C1)=C/C)=C\\C", "C/C=C1/CC(=C/C)/C1", "C(=C/1C\\C(=C\\C)C1)/C");
    }

    @Test
    public void bispropenyloctatriene() throws Exception {
        test("C(=C/C)/C(=C(\\C=C/C)/C=C/C)/C=C/C", "C(=C/C)/C(/C=C/C)=C(\\C=C/C)/C=C/C",
                "C\\C=C\\C(=C(/C=C/C)\\C=C/C)\\C=C/C", "C(=C/C)/C(/C=C/C)=C(/C=C/C)\\C=C/C",
                "C(/C=C/C)(=C(/C=C/C)\\C=C/C)\\C=C/C", "C\\C=C\\C(\\C=C/C)=C(/C=C/C)\\C=C/C",
                "C\\C=C\\C(\\C=C/C)=C(/C=C/C)\\C=C/C", "C(\\C=C/C)(/C=C/C)=C(/C=C/C)\\C=C/C",
                "C(/C(/C=C/C)=C(\\C=C/C)/C=C/C)=C/C", "C(/C(=C(/C=C/C)\\C=C/C)/C=C/C)=C/C",
                "C\\C=C\\C(=C(/C=C/C)\\C=C/C)\\C=C/C", "C(/C(/C=C/C)=C(\\C=C/C)/C=C/C)=C/C",
                "C(\\C(=C(\\C=C/C)/C=C/C)\\C=C/C)=C/C", "C(\\C(\\C=C/C)=C(/C=C/C)\\C=C/C)=C/C",
                "C(/C)=C\\C(\\C=C/C)=C(/C=C/C)\\C=C/C", "C(/C)=C/C(/C=C/C)=C(/C=C/C)\\C=C/C",
                "C(\\C=C/C)(=C(/C=C/C)\\C=C/C)/C=C/C", "C(=C/C)/C(=C(/C=C/C)\\C=C/C)/C=C/C",
                "C\\C=C/C(=C(/C=C/C)\\C=C/C)/C=C/C", "C(=C/C)\\C(\\C=C/C)=C(\\C=C/C)/C=C/C",
                "C\\C=C/C(/C=C/C)=C(/C=C/C)\\C=C/C");
    }

    // 2,4,6,8-tetramethyl-1,3,5,7-tetraazatricyclo[5.1.0.0³,⁵]octane
    @Test
    public void tetramethyltetraazatricyclooctane() throws Exception {
        test("C[C@H]1N2N([C@@H](C)N3[C@H](C)N13)[C@H]2C", "N12N([C@H](C)N3[C@@H](C)N3[C@@H]1C)[C@@H]2C",
                "C[C@H]1N2[C@@H](N3[C@H](N3[C@H](C)N21)C)C", "C[C@@H]1N2[C@@H](C)N3[C@H](C)N3[C@@H](C)N12",
                "N12N([C@H](N3N([C@H]1C)[C@H]3C)C)[C@H]2C", "C[C@@H]1N2N1[C@@H](N3[C@H](N3[C@@H]2C)C)C",
                "[C@H]1(N2[C@@H](C)N3N([C@H](N12)C)[C@H]3C)C", "[C@H]1(C)N2[C@@H](N3[C@@H](C)N3[C@H](C)N12)C",
                "[C@H]1(N2N1[C@H](C)N3N([C@@H]2C)[C@@H]3C)C", "[C@H]1(N2[C@H](N2[C@@H](N3N1[C@@H]3C)C)C)C",
                "C[C@@H]1N2[C@H](N2[C@@H](N3N1[C@@H]3C)C)C", "N12N([C@@H](N3[C@@H](C)N3[C@@H]1C)C)[C@@H]2C",
                "N12N([C@@H]1C)[C@@H](N3[C@@H](C)N3[C@@H]2C)C", "N12N([C@@H](C)N3[C@H](C)N3[C@H]1C)[C@H]2C",
                "[C@H]1(C)N2N([C@H]2C)[C@H](N3N1[C@H]3C)C", "N12[C@H](C)N1[C@@H](C)N3[C@H](C)N3[C@H]2C",
                "N12N([C@H](C)N3[C@H](N3[C@@H]1C)C)[C@@H]2C", "[C@H]1(N2[C@@H](C)N2[C@@H](N3[C@@H](C)N13)C)C",
                "N12[C@H](C)N3N([C@@H]3C)[C@H](C)N1[C@H]2C", "N12N([C@@H](C)N3N([C@H]3C)[C@H]1C)[C@H]2C");
    }

    @Ignore("Random failure: to be resolved https://github.com/cdk/cdk/issues/336")
    public void dbStereoCanonGeneration() throws Exception {
        String in = "Oc1ccc(cc1O)C(\\C([O-])=O)=c1/cc(O)\\c(cc1O)=C(/C([O-])=O)c1ccccc1";
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        final IAtomContainer mol = smipar.parseSmiles(in);
        final SmilesGenerator cansmi = SmilesGenerator.absolute();
        Assert.assertEquals(cansmi.create(mol),
                            cansmi.create(smipar.parseSmiles(cansmi.create(mol))));
    }

    @Test
    public void smilesWithUnknownElem() throws Exception {
        test("*CC", "CC*");
    }

    @Test
    public void rfElement() throws Exception {
        test("[Rf]");
    }

    @Test(expected = CDKException.class)
    public void problematic() throws Exception {
        test("*[Rf]");
    }

    static void test(String... inputs) throws Exception {

        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        SmilesGenerator sg = SmilesGenerator.absolute();

        Set<String> output = new HashSet<String>();

        for (String input : inputs)
            output.add(sg.create(sp.parseSmiles(input)));

        org.hamcrest.MatcherAssert.assertThat(Joiner.on(".").join(inputs) + " were not canonicalised, outputs were " + output,
                output.size(), is(1));

    }

}
