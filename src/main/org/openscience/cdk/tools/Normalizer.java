/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.tools;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.mcss.RMap;
import org.openscience.cdk.smiles.SmilesParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Iterator;
import java.util.List;

/**
 * Adjusts parts of an AtomContainer to the congiguratin of a fragment.
 *
 * @author        shk3
 * @cdk.created   2004-03-04
 * @cdk.module    smiles
 * @cdk.svnrev  $Revision$
 */
@TestClass("org.openscience.cdk.tools.NormalizerTest")
public class Normalizer {

  /**
   *  The method takes an xml files like the following:<br>
   *  &lt;replace-set&gt;<br>
   *  &lt;replace&gt;O=N=O&lt;/replace&gt;<br>
   *  &lt;replacement&gt;[O-][N+]=O&lt;/replacement&gt;<br>
   *  &lt;/replace-set&gt;<br>
   *  All parts in ac which are the same as replace will be changed according to replacement.
   *  Currently the following changes are done: BondOrder, FormalCharge.
   *  For detection of fragments like replace, we rely on UniversalIsomorphismTester.
   *  doc may contain several replace-sets and a replace-set may contain several replace fragments, which will all be normalized according to replacement.
   *
   * @param  ac                          The atomcontainer to normalize.
   * @param  doc                         The configuration file.
   * @return                             Did a replacement take place?
   * @exception  InvalidSmilesException  doc contains an invalid smiles.
   */
  @TestMethod("testNormalize")
  public static boolean normalize(IAtomContainer ac, Document doc) throws InvalidSmilesException, CDKException {
    NodeList nl = doc.getElementsByTagName("replace-set");
    SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
    boolean change=false;
    for (int i = 0; i < nl.getLength(); i++) {
      Element child = (Element) nl.item(i);
      NodeList replaces = child.getElementsByTagName("replace");
      NodeList replacement = child.getElementsByTagName("replacement");
      String replacementstring = replacement.item(0).getFirstChild().getNodeValue();
      if (replacementstring.indexOf("\n") > -1 || replacementstring.length() < 1) {
        replacementstring = replacement.item(0).getFirstChild().getNextSibling().getNodeValue();
      }
      IAtomContainer replacementStructure = sp.parseSmiles(replacementstring);
      for (int k = 0; k < replaces.getLength(); k++) {
        Element replace = (Element) replaces.item(k);
        String replacestring = replace.getFirstChild().getNodeValue();
        if (replacestring.indexOf("\n") > -1 || replacestring.length() < 1) {
          replacestring = replace.getFirstChild().getNextSibling().getNodeValue();
        }
        IAtomContainer replaceStructure = sp.parseSmiles(replacestring);
        List l = null;
        while ((l = UniversalIsomorphismTester.getSubgraphMap(ac, replaceStructure)) != null) {
          List l2 = UniversalIsomorphismTester.makeAtomsMapOfBondsMap(l, ac, replaceStructure);
          Iterator bondit = l.iterator();
          while (bondit.hasNext()) {
            RMap rmap = (RMap) bondit.next();
            org.openscience.cdk.interfaces.IBond acbond = ac.getBond(rmap.getId1());
            org.openscience.cdk.interfaces.IBond replacebond = replacementStructure.getBond(rmap.getId2());
            acbond.setOrder(replacebond.getOrder());
            change=true;
          }
          Iterator atomit = l2.iterator();
          while (atomit.hasNext()) {
            RMap rmap = (RMap) atomit.next();
            org.openscience.cdk.interfaces.IAtom acatom = ac.getAtom(rmap.getId1());
            org.openscience.cdk.interfaces.IAtom replaceatom = replacementStructure.getAtom(rmap.getId2());
            acatom.setFormalCharge(replaceatom.getFormalCharge());
            change=true;
          }
        }
      }
    }
    return (change);
  }
}

