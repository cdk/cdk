/*
 * Copyright (C) 2018  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package org.openscience.cdk.smarts;

import org.junit.Test;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.matchers.Expr;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MolToQueryTest {

    private final SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());

    private void test(String expected, String smi, Expr.Type... opts) throws InvalidSmilesException {
        IAtomContainer      mol   = smipar.parseSmiles(smi);
        Cycles.markRingAtomsAndBonds(mol);
        IQueryAtomContainer query = QueryAtomContainer.create(mol, opts);
        String actual = Smarts.generate(query);
        assertThat(actual, is(expected));
    }

    @Test
    public void noOptsSpecified() throws InvalidSmilesException {
        test("*1~*~*~*~*~*~1~*", "c1cccnc1C");
    }

    @Test
    public void aromaticWithBonds() throws InvalidSmilesException {
        test("a1:a:a:a:a:a:1-A", "c1cccnc1C",
             Expr.Type.IS_AROMATIC,
             Expr.Type.IS_ALIPHATIC,
             Expr.Type.SINGLE_OR_AROMATIC);
    }

    @Test
    public void aromaticElementWithBonds() throws InvalidSmilesException {
        test("c1:c:c:c:n:c:1-*", "c1cccnc1C",
             Expr.Type.AROMATIC_ELEMENT,
             Expr.Type.SINGLE_OR_AROMATIC);
        test("c1:c:c:c:n:c:1-[#6]", "c1cccnc1C",
             Expr.Type.IS_AROMATIC,
             Expr.Type.ELEMENT,
             Expr.Type.SINGLE_OR_AROMATIC);
    }

    @Test
    public void pseudoAtoms() throws InvalidSmilesException {
        test("[#6]~[#6]~*", "CC*",
             Expr.Type.ELEMENT);
    }

    @Test
    public void elementAndDegree() throws InvalidSmilesException {
        test("[#6D2]1~[#6D2]~[#6D2]~[#6D2]~[#7D2]~[#6D3]~1~[#6D]", "c1cccnc1C",
             Expr.Type.ELEMENT, Expr.Type.DEGREE);
    }

    @Test
    public void complexDocExample() throws InvalidSmilesException {
        test("[nx2+0]1:[cx2+0]:[cx2+0]:[cx2+0](=[O&x0+0]):[cx2+0]:[cx2+0]:1",
             "[nH]1ccc(=O)cc1",
             Expr.Type.ALIPHATIC_ELEMENT,
             Expr.Type.AROMATIC_ELEMENT,
             Expr.Type.SINGLE_OR_AROMATIC,
             Expr.Type.ALIPHATIC_ORDER,
             Expr.Type.ISOTOPE,
             Expr.Type.RING_BOND_COUNT,
             Expr.Type.FORMAL_CHARGE);
        test("[0n+0]1:[0c+0]:[0c+0]:[0c+0](=[O+0]):[0c+0]:[0c+0]:1",
             "[0nH]1[0cH][0cH][0cH](=O)[0cH][0cH]1",
             Expr.Type.ALIPHATIC_ELEMENT,
             Expr.Type.AROMATIC_ELEMENT,
             Expr.Type.SINGLE_OR_AROMATIC,
             Expr.Type.ALIPHATIC_ORDER,
             Expr.Type.ISOTOPE,
             Expr.Type.FORMAL_CHARGE);
    }
}
