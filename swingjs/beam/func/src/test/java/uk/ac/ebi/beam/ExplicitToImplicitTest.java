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

/** @author John May */
public class ExplicitToImplicitTest {

    // function (f) to convert explicit bond types to implicit types
    ExplicitToImplicit f = new ExplicitToImplicit();

    @Test
    public void phenylbenzene() throws Exception {
        Graph g = Parser.parse("c:1:c:c:c(c:c1)-c:2:c:c:c:c:c2");
        Assert.assertThat(Generator.generate(g),
                          CoreMatchers.is("c:1:c:c:c(c:c1)-c:2:c:c:c:c:c2"));
        Assert.assertThat(Generator.generate(f.apply(g)),
                          CoreMatchers.is("c1ccc(cc1)-c2ccccc2"));
    }

    @Test
    public void benzene() throws Exception {
        Graph g = Parser.parse("c:1:c:c:c:c:c1");
        Assert.assertThat(Generator.generate(g),
                          CoreMatchers.is("c:1:c:c:c:c:c1"));
        Assert.assertThat(Generator.generate(f.apply(g)),
                          CoreMatchers.is("c1ccccc1"));
    }

    @Test
    public void benzeneMixed() throws Exception {
        Graph g = Parser.parse("C:1:C:C:C:C:C1");
        Assert.assertThat(Generator.generate(g),
                          CoreMatchers.is("C:1:C:C:C:C:C1"));
        Assert.assertThat(Generator.generate(f.apply(g)),
                          CoreMatchers.is("C:1:C:C:C:C:C1"));
    }

}
