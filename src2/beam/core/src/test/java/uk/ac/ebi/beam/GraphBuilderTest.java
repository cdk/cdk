/*
 * Copyright (c) 2013, European Bioinformatics Institute (EMBL-EBI)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */

package uk.ac.ebi.beam;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static uk.ac.ebi.beam.Configuration.DoubleBond.OPPOSITE;

/**
 * @author John May
 */
public class GraphBuilderTest {

    @Test
    public void clockwise_parity() throws IOException {

        GraphBuilder gb = GraphBuilder.create(5);
        Graph g = gb.add(AtomBuilder.aliphatic("C").build())
                    .add(AtomImpl.AliphaticSubset.Nitrogen)
                    .add(AtomImpl.AliphaticSubset.Oxygen)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomBuilder.explicitHydrogen())
                    .add(0, 1)
                    .add(0, 2)
                    .add(0, 3)
                    .add(0, 4)
                    .tetrahedral(0).lookingFrom(1)
                    .neighbors(2, 3, 4)
                    .parity(1)
                    .build()
                    .build();

        assertThat(g.toSmiles(), is("[C@@](N)(O)(C)[H]"));
    }

    @Test
    public void anticlockwise_parity() throws IOException {

        GraphBuilder gb = GraphBuilder.create(5);
        Graph g = gb.add(AtomBuilder.aliphatic("C").build())
                    .add(AtomImpl.AliphaticSubset.Nitrogen)
                    .add(AtomImpl.AliphaticSubset.Oxygen)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomBuilder.explicitHydrogen())
                    .add(0, 1)
                    .add(0, 2)
                    .add(0, 3)
                    .add(0, 4)
                    .tetrahedral(0).lookingFrom(1)
                    .neighbors(2, 3, 4)
                    .parity(-1)
                    .build()
                    .build();

        assertThat(g.toSmiles(), is("[C@](N)(O)(C)[H]"));
    }

    @Test
    public void e_1_2_difluroethene() throws IOException {
        GraphBuilder gb = GraphBuilder.create(5);
        Graph g = gb.add(AtomImpl.AliphaticSubset.Fluorine)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Fluorine)
                    .add(0, 1)
                    .doubleBond(1, 2)
                    .add(2, 3)
                    .geometric(1, 2).opposite(0, 3)
                    .build();
        assertThat(g.toSmiles(), is("F/C=C/F"));
    }

    @Test
    public void z_1_2_difluroethene() throws IOException {
        GraphBuilder gb = GraphBuilder.create(5);
        Graph g = gb.add(AtomImpl.AliphaticSubset.Fluorine)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Fluorine)
                    .add(0, 1)
                    .doubleBond(1, 2)
                    .add(2, 3)
                    .geometric(1, 2).together(0, 3)
                    .build();
        assertThat(g.toSmiles(), is("F/C=C\\F"));
    }


    @Test
    public void conjugated_consider_existing() throws IOException {
        // the second configuration considers the existing configuration
        GraphBuilder gb = GraphBuilder.create(5);
        Graph g = gb.add(AtomImpl.AliphaticSubset.Fluorine)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Fluorine)
                    .add(0, 1)
                    .doubleBond(1, 2)
                    .add(2, 3)
                    .doubleBond(3, 4)
                    .add(4, 5)
                    .geometric(1, 2).together(0, 3)
                    .geometric(3, 4).together(2, 5)
                    .build();
        assertThat(g.toSmiles(), is("F/C=C\\C=C/F"));
    }

    @Test
    public void conjugated_resolve_conflict() throws IOException {
        // assigning the second one first means we have to consider this
        // on the first one
        GraphBuilder gb = GraphBuilder.create(5);
        Graph g = gb.add(AtomImpl.AliphaticSubset.Fluorine)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Fluorine)
                    .add(0, 1)
                    .doubleBond(1, 2)
                    .add(2, 3)
                    .doubleBond(3, 4)
                    .add(4, 5)
                    .geometric(3, 4).together(2, 5)
                    .geometric(1, 2).together(0, 3)
                    .build();
        assertThat(g.toSmiles(), is("F\\C=C/C=C\\F"));
    }

    @Test
    public void conjugated_resolve_conflict2() throws IOException {
        // we assign the first, third then second - the second one cause
        // a conflict and we must flip one of the others
        GraphBuilder gb = GraphBuilder.create(5);
        Graph g = gb.add(AtomImpl.AliphaticSubset.Fluorine)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Fluorine)
                    .add(0, 1)
                    .doubleBond(1, 2)
                    .add(2, 3)
                    .doubleBond(3, 4)
                    .add(4, 5)
                    .doubleBond(5, 6)
                    .add(6, 7)
                    .geometric(1, 2).opposite(0, 3)
                    .geometric(5, 6).together(4, 7)
                    .geometric(3, 4).together(2, 5)
                    .build();
        assertThat(g.toSmiles(), is("F/C=C/C=C\\C=C/F"));
    }

    @Test
    public void resolveConflict3() throws Exception {
        GraphBuilder gb = GraphBuilder.create(5);
        Graph g = gb.add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(0, 1)
                    .doubleBond(1, 2)
                    .add(2, 3)
                    .doubleBond(3, 4)
                    .add(4, 5)
                    .add(3, 6)
                    .doubleBond(6, 7)
                    .add(7, 8)
                    .geometric(1, 2).configure(0, 3, OPPOSITE)
                    .geometric(7, 6).configure(8, 3, OPPOSITE)
                    .geometric(3, 4).configure(2, 5, OPPOSITE)
                    .build();
        assertNotNull(g); // g builds okay
    }

    @Test
    public void all_trans_octatetraene() throws IOException {
        GraphBuilder gb = GraphBuilder.create(5);
        Graph g = gb.add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(0, 1)
                    .doubleBond(1, 2)
                    .add(2, 3)
                    .doubleBond(3, 4)
                    .add(4, 5)
                    .doubleBond(5, 6)
                    .add(6, 7)
                    .doubleBond(7, 0)
                    .geometric(1, 2).together(0, 3)
                    .geometric(3, 4).together(2, 5)
                    .geometric(5, 6).together(4, 7)
                    .geometric(7, 0).together(6, 1)
                    .build();
        assertThat(g.toSmiles(), is("C=1/C=C\\C=C/C=C\\C1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void impossible_octatetraene() throws IOException {
        GraphBuilder gb = GraphBuilder.create(5);
        Graph g = gb.add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(0, 1)
                    .doubleBond(1, 2)
                    .add(2, 3)
                    .doubleBond(3, 4)
                    .add(4, 5)
                    .doubleBond(5, 6)
                    .add(6, 7)
                    .doubleBond(7, 0)
                    .geometric(1, 2).together(0, 3)
                    .geometric(3, 4).opposite(2, 5)
                    .geometric(5, 6).together(4, 7)
                    .geometric(7, 0).together(6, 1)
                    .build();
        assertThat(g.toSmiles(), is("C=1/C=C\\C=C/C=C\\C1"));
    }

    // example from: CHEBI:27711
    // C[C@]1(CC(O)=O)[C@H](CCC(O)=O)C2=C/c3[nH]c(Cc4[nH]c(c(CC(O)=O)c4CCC(O)=O)[C@](C)(O)[C@@]45N/C(=C\C1=N\2)[C@@H](CCC(O)=O)[C@]4(C)CC(=O)O5)c(CCC(O)=O)c3CC(O)=O
    @Test
    public void correctCyclicDb() {
        // C\C=C/C1=C(CCC1)\C=C/C
        // 0 1 2 3  4 567   8 9 0
        GraphBuilder gb = GraphBuilder.create(5);
        Graph g = gb.add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(AtomImpl.AliphaticSubset.Carbon)
                    .add(0, 1)
                    .doubleBond(1, 2)
                    .add(2, 3)
                    .doubleBond(3, 4)
                    .add(4, 5)
                    .add(5, 6)
                    .add(6, 7)
                    .add(7, 3)
                    .add(4, 8)
                    .doubleBond(8, 9)
                    .add(9, 10)
                    .geometric(1, 2).opposite(0, 3)
                    .geometric(8, 9).opposite(4, 10)
                    .geometric(3, 4).opposite(2, 8)
                    .build();
        assertNotNull(g); // builds okay
    }

    @Test
    public void suppress_benzene() throws IOException {
        GraphBuilder gb = GraphBuilder.create(5);
        assertThat(gb.add(Element.Carbon, 1)
                            .add(Element.Carbon, 1)
                            .add(Element.Carbon, 1)
                            .add(Element.Carbon, 1)
                            .add(Element.Carbon, 1)
                            .add(Element.Carbon, 1)
                            .add(0, 1)
                            .add(1, 2, Bond.DOUBLE)
                            .add(2, 3)
                            .add(3, 4, Bond.DOUBLE)
                            .add(4, 5)
                            .add(5, 0, Bond.DOUBLE).build().toSmiles(),
                          is("C=1C=CC=CC1"));
    }

    @Test
    public void buildExtendedTetrahedral() throws IOException {
        GraphBuilder gb = GraphBuilder.create(4);
        gb = gb.add(Element.Carbon, 3)
               .add(Element.Carbon, 1)
               .add(Element.Carbon, 0)
               .add(Element.Carbon, 1)
               .add(Element.Carbon, 3)
               .singleBond(0, 1)
               .doubleBond(1, 2)
               .doubleBond(2, 3)
               .singleBond(3, 4)
               .extendedTetrahedral(2).lookingFrom(1)
               .neighbors(2, 3, 4)
               .winding(Configuration.AL1)
               .build();
        assertNotNull(gb.build()); // builds okay
    }

    @Test
    public void buildCHEMBL1204342() throws Exception {
        GraphBuilder gb = GraphBuilder.create(50);
        gb.add(Element.Carbon,3)
          .add(Element.Carbon,3)
          .add(Element.Carbon,3)
          .add(Element.Carbon,3)
          .add(Element.Carbon,1)
          .add(Element.Carbon,1)
          .add(Element.Carbon,1)
          .add(Element.Carbon,1)
          .add(Element.Carbon,1)
          .add(Element.Carbon,1)
          .add(Element.Carbon,1)
          .add(Element.Carbon,1)
          .add(Element.Carbon,1)
          .add(Element.Carbon,1)
          .add(Element.Carbon,1)
          .add(Element.Carbon,1)
          .add(Element.Carbon,1)
          .add(Element.Carbon,1)
          .add(Element.Carbon,1)
          .add(Element.Carbon,1)
          .add(Element.Carbon,1)
          .add(Element.Carbon,1)
          .add(Element.Carbon,0)
          .add(Element.Carbon,0)
          .add(Element.Carbon,0)
          .add(Element.Carbon,0)
          .add(Element.Carbon,0)
          .add(Element.Carbon,0)
          .add(Element.Carbon,0)
          .add(Element.Carbon,0)
          .add(Element.Carbon,0)
          .add(Element.Carbon,0)
          .add(Element.Carbon,0)
          .add(Element.Carbon,0)
          .add(Element.Carbon,0)
          .add(Element.Carbon,0)
          .add(Element.Carbon,0)
          .add(Element.Carbon,0)
          .add(Element.Nitrogen,1)
          .add(Element.Nitrogen,1)
          .add(Element.Nitrogen,1)
          .add(Element.Nitrogen,1)
          .add(Element.Nitrogen,0)
          .add(Element.Nitrogen,0)
          .add(Element.Nitrogen,0)
          .add(Element.Nitrogen,0)
          .add(Element.Oxygen,0)
          .add(Element.Chlorine,1)
          .add(0,20, Bond.IMPLICIT)
          .add(1,20, Bond.IMPLICIT)
          .add(2,21, Bond.IMPLICIT)
          .add(3,21, Bond.IMPLICIT)
          .add(4,8, Bond.DOUBLE)
          .add(4,22, Bond.IMPLICIT)
          .add(5,9, Bond.DOUBLE)
          .add(5,22, Bond.IMPLICIT)
          .add(6,10, Bond.DOUBLE)
          .add(6,23, Bond.IMPLICIT)
          .add(7,11, Bond.DOUBLE)
          .add(7,23, Bond.IMPLICIT)
          .add(8,24, Bond.IMPLICIT)
          .add(9,24, Bond.IMPLICIT)
          .add(10,25, Bond.IMPLICIT)
          .add(11,25, Bond.IMPLICIT)
          .add(12,14, Bond.DOUBLE)
          .add(12,26, Bond.IMPLICIT)
          .add(13,15, Bond.DOUBLE)
          .add(13,27, Bond.IMPLICIT)
          .add(14,28, Bond.IMPLICIT)
          .add(15,29, Bond.IMPLICIT)
          .add(16,17, Bond.DOUBLE)
          .add(16,32, Bond.IMPLICIT)
          .add(17,33, Bond.IMPLICIT)
          .add(18,26, Bond.DOUBLE)
          .add(18,30, Bond.IMPLICIT)
          .add(19,27, Bond.DOUBLE)
          .add(19,31, Bond.IMPLICIT)
          .add(20,40, Bond.IMPLICIT)
          .add(21,41, Bond.IMPLICIT)
          .add(22,32, Bond.DOUBLE)
          .add(23,33, Bond.DOUBLE)
          .add(24,36, Bond.DOUBLE)
          .add(25,37, Bond.DOUBLE)
          .add(26,34, Bond.IMPLICIT)
          .add(27,35, Bond.IMPLICIT)
          .add(28,30, Bond.IMPLICIT)
          .add(28,42, Bond.DOUBLE)
          .add(29,31, Bond.IMPLICIT)
          .add(29,43, Bond.DOUBLE)
          .add(30,44, Bond.DOUBLE)
          .add(31,45, Bond.DOUBLE)
          .add(32,46, Bond.IMPLICIT)
          .add(33,46, Bond.IMPLICIT)
          .add(34,38, Bond.DOUBLE)
          .add(34,40, Bond.IMPLICIT)
          .add(35,39, Bond.DOUBLE)
          .add(35,41, Bond.IMPLICIT)
          .add(36,42, Bond.IMPLICIT)
          .add(36,44, Bond.IMPLICIT)
          .add(37,43, Bond.IMPLICIT)
          .add(37,45, Bond.IMPLICIT);
        gb.geometric(23, 33).together(6, 17)
          .geometric(25, 37).together(10, 43)
          .geometric(24, 36).together(8, 42)
          .geometric(22, 32).opposite(4, 16);
        assertThat(gb.build().toSmiles(),
                   is("CC(C)NC(C=1C=CC=2C(C1)=N\\C(=C3\\C=CC(/C=C3)=C\\4C=C/C(=C/5C=C/C(C=C5)=C/6\\N=C7C=CC(=CC7=N6)C(=N)NC(C)C)/O4)\\N2)=N.Cl"));
    }

    @Test
    public void extendedCisTrans() throws IOException {
        GraphBuilder gb = GraphBuilder.create(6)
                                      .add(AtomImpl.AliphaticSubset.Carbon)
                                      .add(AtomImpl.AliphaticSubset.Carbon)
                                      .add(AtomImpl.AliphaticSubset.Carbon)
                                      .add(AtomImpl.AliphaticSubset.Carbon)
                                      .add(AtomImpl.AliphaticSubset.Carbon)
                                      .add(AtomImpl.AliphaticSubset.Carbon)
                                      .singleBond(0,1)
                                      .doubleBond(1,2)
                                      .doubleBond(2,3)
                                      .doubleBond(3,4)
                                      .singleBond(4,5)
                                      .extendedGeometric(1,4)
                                      .configure(0, 5, Configuration.DoubleBond.TOGETHER);
        assertThat(gb.build().toSmiles(), is("C/C=C=C=C\\C"));
    }

}
