/*
 * Copyright (c) 2020 John Mayfield
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscience.cdk.smarts;

import org.junit.jupiter.api.Test;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


class Mdl2SmartsTest {

  @Test
  void atomList() throws Exception {
    try (InputStream in = getClass().getResourceAsStream("mdlquery.mol");
         MDLV2000Reader mdlr = new MDLV2000Reader(in)) {
      IQueryAtomContainer mol = mdlr.read(new QueryAtomContainer(SilentChemObjectBuilder.getInstance()));
      // Important! MDL => SMARTS is not exact since SMARTS has no was of
      //            expressing double bond, = means "double aliphatic" and will
      //            not match benzene (for example) where as the MDL query would
      assertThat(Smarts.generate(mol),
                 is("[F,#7,#8]-[#6]1-[#6h0]=[#6h1]-[#6]=[#6]-[#6]=1"));
    }
  }
}
