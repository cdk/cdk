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
public class RemoveUpDownBondsTest {

    @Test public void e_butene_expH() throws Exception {
        transform("C\\C(\\[H])=C\\C",
                  "C\\C([H])=C\\C");
        transform("C/C(/[H])=C/C",
                  "C/C([H])=C/C");
        transform("C\\C(\\[H])=C(/[H])\\C",
                  "C\\C([H])=C(/[H])C");
        transform("C/C(/[H])=C(\\[H])/C",
                  "C/C([H])=C(\\[H])C");
    }

    @Test public void z_butene_expH() throws Exception {
        transform("C\\C(\\[H])=C/C",
                  "C\\C([H])=C/C");
        transform("C/C(/[H])=C\\C",
                  "C/C([H])=C\\C");
        transform("C\\C(\\[H])=C(\\[H])/C",
                  "C\\C([H])=C(\\[H])C");
        transform("C/C(/[H])=C(/[H])\\C",
                  "C/C([H])=C(/[H])C");
    }

    @Test public void e_e_hexadiene_expH() throws InvalidSmilesException {
        transform("C\\C(\\[H])=C(/[H])\\C(\\[H])=C(/[H])\\C",
                  "C\\C([H])=C(/[H])\\C([H])=C(/[H])C");
    }

    @Test public void e_e_hexadiene_expH2() throws Exception {
        transform("[H]\\C(\\C(\\[H])=C(\\C)/[H])=C(/C)\\[H]",
                  "[H]\\C(\\C([H])=C(\\C)[H])=C(/C)[H]");
    }

    @Test public void e_e_hexadiene_expH3() throws Exception {
        transform("[H]/C(/C(=C(/C)\\[H])[H])=C(/[H])\\C",
                  "[H]/C(/C(=C(/C)[H])[H])=C(/[H])C");
    }

    @Test public void e_e_hexadiene_expH4() throws Exception {
        transform("C\\C(\\[H])=C(/[H])\\C(=C(/[H])\\C)\\[H]",
                  "C\\C([H])=C(/[H])\\C(=C(/[H])C)[H]");
    }

    @Test public void e_e_hexadiene_expH5() throws Exception {
        transform("[H]/C(/C)=C(/[H])\\C(\\[H])=C(/[H])\\C",
                  "[H]/C(C)=C(/[H])\\C([H])=C(/[H])C");
    }

    @Test public void e_e_hexadiene_permute() throws Exception {
        String input = "C\\C(=C(\\C(=C(/[H])\\C)\\[H])/[H])\\[H]";
        int[] p = new int[]{7, 2, 4, 1, 3, 6, 8, 9, 0, 5};
        Graph g = Parser.parse(input);
        Assert.assertThat(Generator.generate(g.permute(p)),
                          CoreMatchers
                                  .is("[H]\\C(\\C(=C(/[H])\\C)\\[H])=C(\\[H])/C"));
        Assert.assertThat(Generator.generate(new RemoveUpDownBonds().apply(g.permute(p))),
                          CoreMatchers.is("[H]\\C(\\C(=C(/[H])C)[H])=C(\\[H])C"));
    }

    static void transform(String smi, String exp) throws
                                                  InvalidSmilesException {
        Assert.assertThat(Generator.generate(new RemoveUpDownBonds()
                                                     .apply(Parser.parse(smi))),
                          CoreMatchers.is(exp));
    }
}
