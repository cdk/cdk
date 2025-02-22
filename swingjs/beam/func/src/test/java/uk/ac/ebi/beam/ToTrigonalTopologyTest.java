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

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

/** @author John May */
public class ToTrigonalTopologyTest {


    @Test public void e_difluoroethene_impl1() throws InvalidSmilesException {
        transform("F/C=C/F", "F[C@H]=[C@@H]F");
    }

    @Test public void z_difluoroethene_impl2() throws InvalidSmilesException {
        transform("F/C=C\\F", "F[C@H]=[C@H]F");
    }

    @Test public void e_difluoroethene_impl3() throws InvalidSmilesException {
        transform("F\\C=C\\F", "F[C@@H]=[C@H]F");
    }

    @Test public void z_difluoroethene_impl4() throws InvalidSmilesException {
        transform("F\\C=C/F", "F[C@@H]=[C@@H]F");
    }

    @Test public void e_difluoroethene_exp1() throws InvalidSmilesException {
        transform("F/C([H])=C([H])/F", "F[C@]([H])=[C@@]([H])F");
    }

    @Test public void z_difluoroethene_exp2() throws InvalidSmilesException {
        transform("F/C([H])=C([H])\\F", "F[C@]([H])=[C@]([H])F");
    }

    @Test public void e_difluoroethene_exp3() throws InvalidSmilesException {
        transform("F\\C([H])=C([H])\\F", "F[C@@]([H])=[C@]([H])F");
    }

    @Test public void z_difluoroethene_exp4() throws InvalidSmilesException {
        transform("F\\C([H])=C([H])/F", "F[C@@]([H])=[C@@]([H])F");
    }

    @Test public void z_difluoroethene_exp5() throws InvalidSmilesException {
        transform("FC(\\[H])=C([H])/F", "F[C@@]([H])=[C@@]([H])F");
        transform("FC(\\[H])=C(\\[H])F", "F[C@@]([H])=[C@@]([H])F");
        transform("F\\C([H])=C(\\[H])F", "F[C@@]([H])=[C@@]([H])F");
    }

    @Test public void e_difluoroethene_exp6() throws InvalidSmilesException {
        transform("FC(\\[H])=C([H])\\F", "F[C@@]([H])=[C@]([H])F");
        transform("FC(\\[H])=C(/[H])F", "F[C@@]([H])=[C@]([H])F");
        transform("F\\C([H])=C(/[H])F", "F[C@@]([H])=[C@]([H])F");
    }

    @Test public void z_difluoroethene_exp7() throws InvalidSmilesException {
        transform("FC(/[H])=C([H])\\F", "F[C@]([H])=[C@]([H])F");
        transform("FC(/[H])=C(/[H])F", "F[C@]([H])=[C@]([H])F");
        transform("F/C([H])=C(/[H])F", "F[C@]([H])=[C@]([H])F");
    }

    @Test public void e_difluoroethene_exp8() throws InvalidSmilesException {
        transform("FC(/[H])=C([H])/F", "F[C@]([H])=[C@@]([H])F");
        transform("FC(/[H])=C(\\[H])F", "F[C@]([H])=[C@@]([H])F");
        transform("F/C([H])=C(\\[H])F", "F[C@]([H])=[C@@]([H])F");
    }

    @Test public void e_difluoroethene_explicitH_9() throws InvalidSmilesException {
        transform("C(\\F)([H])=C([H])/F",
                  "[C@](F)([H])=[C@@]([H])F");
    }

    @Test public void e_difluoroethene_permuted() throws InvalidSmilesException {
        transform("F/C=C/F",
                  new int[]{1, 0, 2, 3},
                  "[C@@H](F)=[C@@H]F");
    }

    @Test public void e_difluoroethene_explicitH_permutation_1() throws InvalidSmilesException {
        transform("F/C([H])=C([H])/F",
                  new int[]{1, 0, 2, 3, 4, 5},
                  "[C@](F)([H])=[C@@]([H])F");
    }

    @Test public void e_difluoroethene_explicitH_permutation_2() throws InvalidSmilesException {
        transform("F/C([H])=C([H])/F",
                  new int[]{2, 0, 1, 3, 4, 5},
                  "[C@@]([H])(F)=[C@@]([H])F");
    }

    @Test public void e_difluoroethene_explicitH_permutation_3() throws InvalidSmilesException {
        transform("F/C([H])=C([H])/F",
                  new int[]{2, 0, 1, 3, 5, 4},
                  "[C@@]([H])(F)=[C@](F)[H]");
    }

    @Test public void cyclooctatetraene() throws InvalidSmilesException {
        transform("C/1=C/C=C\\C=C/C=C1",
                  "[C@H]1=[C@@H][C@H]=[C@H][C@@H]=[C@@H][C@H]=[C@H]1");
    }

    @Test public void unspecified() throws InvalidSmilesException {
        transform("FC=CF",
                  "FC=CF");
    }


    static void transform(String smi, String exp) throws
                                                  InvalidSmilesException {
        ImplicitToExplicit ite = new ImplicitToExplicit();
        ToTrigonalTopology ttt = new ToTrigonalTopology();
        ExplicitToImplicit eti = new ExplicitToImplicit();
        Assert.assertThat(Generator
                                  .generate(eti.apply(ttt.apply(ite.apply(Parser.parse(smi))))),
                          CoreMatchers.is(exp));
    }

    static void transform(String smi, int[] p, String exp) throws
                                                           InvalidSmilesException {
        ImplicitToExplicit ite = new ImplicitToExplicit();
        ToTrigonalTopology ttt = new ToTrigonalTopology();
        ExplicitToImplicit eti = new ExplicitToImplicit();
        Assert.assertThat(Generator
                                  .generate(eti.apply(ttt.apply(ite.apply(Parser.parse(smi)
                                                                                .permute(p))))),
                          CoreMatchers.is(exp));
    }

}
