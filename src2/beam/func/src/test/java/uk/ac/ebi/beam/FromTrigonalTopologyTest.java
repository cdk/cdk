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
public class FromTrigonalTopologyTest {

    @Test public void z_dichloroethene() throws Exception {
        transform("F[C@H]=[C@H]F", "F/C=C\\F");
    }

    @Test public void z_dichloroethene_alt() throws Exception {
        transform("F[C@@H]=[C@@H]F", "F\\C=C/F");
    }

    @Test public void z_dichloroethene_explicitH() throws Exception {
        transform("F[C@]([H])=[C@](F)[H]", "F/C(/[H])=C(\\F)/[H]");
    }

    @Test public void z_dichloroethene_alt_explicitH() throws Exception {
        transform("F[C@@]([H])=[C@@](F)[H]", "F\\C(\\[H])=C(/F)\\[H]");
    }

    @Test public void e_dichloroethene() throws Exception {
        transform("F[C@H]=[C@@H]F", "F/C=C/F");
    }

    @Test public void e_dichloroethene_alt() throws Exception {
        transform("F[C@@H]=[C@H]F", "F\\C=C\\F");
    }

    @Test public void e_dichloroethene_explicitH() throws Exception {
        transform("F[C@]([H])=[C@@](F)[H]", "F/C(/[H])=C(/F)\\[H]");
    }

    @Test public void e_dichloroethene_alt_explicitH() throws Exception {
        transform("F[C@@]([H])=[C@](F)[H]", "F\\C(\\[H])=C(\\F)/[H]");
    }

    @Test public void z_dichloroethene_permuted_1() throws Exception {
        transform("F[C@H]=[C@H]F", new int[]{1, 0, 2, 3}, "C(\\F)=C\\F");
    }

    @Test public void z_dichloroethene_permuted_2() throws Exception {
        transform("F[C@H]=[C@H]F", new int[]{3, 2, 1, 0}, "F\\C=C/F");
    }

    @Test public void z_dichloroethene_alt_permuted_1() throws Exception {
        transform("F[C@@H]=[C@@H]F", new int[]{1, 0, 2, 3}, "C(/F)=C/F");
    }

    @Test public void z_dichloroethene_alt_permuted_2() throws Exception {
        transform("F[C@@H]=[C@@H]F", new int[]{3, 2, 1, 0}, "F/C=C\\F");
    }

    @Test public void e_dichloroethene_permuted_1() throws Exception {
        transform("F[C@H]=[C@@H]F", new int[]{1, 0, 2, 3}, "C(\\F)=C/F");
    }

    @Test public void e_dichloroethene_permuted_2() throws Exception {
        transform("F[C@@H]=[C@H]F", new int[]{3, 2, 1, 0}, "F\\C=C\\F");
    }

    @Test public void conjugated() throws InvalidSmilesException {
        transform("F[C@H]=[C@@H][C@H]=[C@@H]F", "F/C=C/C=C/F");
    }

    /** Ensures that conflicting directional assignments are resolved. */
    @Test public void conjugated_conflict() throws InvalidSmilesException {
        transform("F[C@H]=[C@@H][C@@H]=[C@H]F", "F/C=C/C=C/F");
    }



    @Test public void cyclooctatetraene_1() throws InvalidSmilesException {
        transform("[C@H]1=[C@@H][C@@H]=[C@@H][C@@H]=[C@@H][C@@H]=[C@@H]1",
                  "C\\1=C\\C=C/C=C\\C=C1");
    }

    @Test public void cyclooctatetraene_2() throws InvalidSmilesException {
        transform("[C@@H]1=[C@H][C@H]=[C@H][C@H]=[C@H][C@H]=[C@H]1",
                  "C/1=C/C=C\\C=C/C=C1");
    }

//    @Test public void cyclooctatetraene_3() throws InvalidSmilesException {
//        apply("[C@H]1=[C@@H][C@H]=[C@H][C@@H]=[C@@H][C@H]=[C@@H]1",
//                  "C\\1=C/C=C\\C=C/C=C1");
//    }


    static void transform(String smi, String exp) throws
                                                  InvalidSmilesException {
        ImplicitToExplicit ite = new ImplicitToExplicit();
        FromTrigonalTopology ftt = new FromTrigonalTopology();
        ExplicitToImplicit eti = new ExplicitToImplicit();
        Assert.assertThat(Generator
                                  .generate(eti.apply(ftt.apply(ite.apply(Parser.parse(smi))))),
                          CoreMatchers.is(exp));
    }

    static void transform(String smi, int[] p, String exp) throws
                                                           InvalidSmilesException {
        ImplicitToExplicit ite = new ImplicitToExplicit();
        FromTrigonalTopology ftt = new FromTrigonalTopology();
        ExplicitToImplicit eti = new ExplicitToImplicit();
        Assert.assertThat(Generator
                                  .generate(eti.apply(ftt.apply(ite.apply(Parser.parse(smi)
                                                                                .permute(p))))),
                          CoreMatchers.is(exp));
    }
}
