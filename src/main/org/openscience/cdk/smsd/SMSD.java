/* Copyright (C) 2006-2010  Syed Asad Rahman {asad@ebi.ac.uk}
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.smsd;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.smsd.factory.SubStructureSearchAlgorithms;
import org.openscience.cdk.smsd.interfaces.Algorithm;

/**
 *  <p>This class implements the SMSD- a multipurpose structure comparison tool.
 *  It allows users to, i) find the maximal common substructure(s) (MCS);
 *  ii) perform the mapping of a substructure in another structure, and;
 *  iii) map two isomorphic structures.</p>
 *
 *  <p>It also comes with various published algorithms. The user is free to
 *  choose his favorite algorithm to perform MCS or substructure search.
 *  For example 0: SMSD algorithm, 1: MCSPlus, 2: VFLibMCS, 3: CDKMCS, 4:
 *  Substructure</p>
 *
 *  <p>It also has a set of robust chemical filters (i.e. bond energy, fragment
 *  count, stereo & bond match) to sort the reported MCS solutions in a chemically
 *  relevant manner. Each comparison can be made with or without using the bond
 *  sensitive mode and with implicit or explicit hydrogens.</p>
 *  
 *  <p>If you are using <font color="#FF0000">SMSD, please cite Rahman <i>et.al. 2009</i></font>
 *  {@cdk.cite SMSD2009}. The SMSD algorithm is described in this paper.
 *  </p>
 *
 *
 * <p>An example for <b>Substructure search</b>:</p>
 *  <font color="#003366">
 *  <pre>
 *  SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
 *  // Benzene
 *  IAtomContainer A1 = sp.parseSmiles("C1=CC=CC=C1");
 *  // Napthalene
 *  IAtomContainer A2 = sp.parseSmiles("C1=CC2=C(C=C1)C=CC=C2");
 *  //Turbo mode search
 *  //Bond Sensitive is set true
 *  SMSD comparison = new SMSD(Algorithm.SubStructure, true);
 *  // set molecules and remove hydrogens
 *  comparison.init(A1, A2, true);
 *  // set chemical filter true
 *  comparison.setChemFilters(false, false, false);
 *  if (comparison.isSubgraph()) {
 *  //Get similarity score
 *   System.out.println("Tanimoto coefficient:  " + comparison.getTanimotoSimilarity());
 *   System.out.println("A1 is a subgraph of A2:  " + comparison.isSubgraph());
 *  //Get Modified AtomContainer
 *   IAtomContainer Mol1 = comparison.getReactantMolecule();
 *   IAtomContainer Mol2 = comparison.getProductMolecule();
 *  // Print the mapping between molecules
 *   System.out.println(" Mappings: ");
 *   for (Map.Entry <Integer, Integer> mapping : comparison.getFirstMapping().entrySet()) {
 *      System.out.println((mapping.getKey() + 1) + " " + (mapping.getValue() + 1));
 *
 *      IAtom eAtom = Mol1.getAtom(mapping.getKey());
 *      IAtom pAtom = Mol2.getAtom(mapping.getValue());
 *      System.out.println(eAtom.getSymbol() + " " + pAtom.getSymbol());
 *   }
 *   System.out.println("");
 *  }
 *
 *  </pre>
 *  </font>
 *
 * <p>An example for <b>MCS search</b>:</p>
 *  <font color="#003366">
 *  <pre>
 *  SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
 *  // Benzene
 *  IAtomContainer A1 = sp.parseSmiles("C1=CC=CC=C1");
 *  // Napthalene
 *  IAtomContainer A2 = sp.parseSmiles("C1=CC2=C(C=C1)C=CC=C2");
 *  //{ 0: Default SMSD Algorithm, 1: MCSPlus Algorithm, 2: VFLibMCS Algorithm, 3: CDKMCS Algorithm}
 *  //Bond Sensitive is set true
 *  SMSD comparison = new SMSD(Algorithm.DEFAULT, true);
 *  // set molecules and remove hydrogens
 *  comparison.init(A1, A2, true);
 *  // set chemical filter true
 *  comparison.setChemFilters(true, true, true);
 *
 *  //Get similarity score
 *  System.out.println("Tanimoto coefficient:  " + comparison.getTanimotoSimilarity());
 *  System.out.println("A1 is a subgraph of A2:  " + comparison.isSubgraph());
 *  //Get Modified AtomContainer
 *  IAtomContainer Mol1 = comparison.getReactantMolecule();
 *  IAtomContainer Mol2 = comparison.getProductMolecule();
 *  // Print the mapping between molecules
 *  System.out.println(" Mappings: ");
 *  for (Map.Entry <Integer, Integer> mapping : comparison.getFirstMapping().entrySet()) {
 *      System.out.println((mapping.getKey() + 1) + " " + (mapping.getValue() + 1));
 *
 *      IAtom eAtom = Mol1.getAtom(mapping.getKey());
 *      IAtom pAtom = Mol2.getAtom(mapping.getValue());
 *      System.out.println(eAtom.getSymbol() + " " + pAtom.getSymbol());
 *  }
 *  System.out.println("");
 *
 *  </pre>
 *  </font>
 * 
 * @cdk.require java1.5+
 *
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 *
 */
@TestClass("org.openscience.cdk.smsd.SMSDTest")
public class SMSD extends SubStructureSearchAlgorithms {

    /**
     * Algorithm {@link org.openscience.cdk.smsd.interfaces.Algorithm}
     * choice for find MCS or performing substructure searches
     * <OL>
     * <lI>0: default,
     * <lI>1: MCSPlus,
     * <lI>2: VFLibMCS,
     * <lI>3: CDKMCS,
     * <lI>4: SubStructure
     * </OL>
     * @param algorithmType
     * <OL>
     * <lI>0: default,
     * <lI>1: MCSPlus,
     * <lI>2: VFLibMCS,
     * <lI>3: CDKMCS,
     * <lI>4: SubStructure
     * </OL> Mode
     * @param bondSensitiveFlag true will activate bond order match else false
     */
    public SMSD(Algorithm algorithmType, boolean bondSensitiveFlag) {
        super(algorithmType, bondSensitiveFlag);
    }
}
