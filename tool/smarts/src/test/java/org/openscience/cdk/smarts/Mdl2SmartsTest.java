/*
 * =====================================
 *  Copyright (c) 2020 NextMove Software
 * =====================================
 */

package org.openscience.cdk.smarts;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class Mdl2SmartsTest {

  @Test
  public void atomList() throws Exception {
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
