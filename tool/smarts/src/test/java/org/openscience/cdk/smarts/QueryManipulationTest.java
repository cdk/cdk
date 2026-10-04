/*
 * Copyright (c) 2026 John Mayfield
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.isomorphism.Pattern;
import org.openscience.cdk.isomorphism.matchers.Expr;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

public class QueryManipulationTest {

    @Test
    public void testDegreeOnly() {
        String smarts = "[C,N]CCO";
        IAtomContainer query = SilentChemObjectBuilder.getInstance().newAtomContainer();
        Assertions.assertTrue(Smarts.parse(query, smarts));
        IAtomContainer queryMod = QueryAtomContainer.create(query, Expr.Type.DEGREE);
        String result = Smarts.generate(queryMod);
        Assertions.assertEquals("[D][D2][D2][D]", result);
    }

    @Test
    public void testDegreeWithElement() {
        String smarts = "[C,N]CCO";
        IAtomContainer query = SilentChemObjectBuilder.getInstance().newAtomContainer();
        Assertions.assertTrue(Smarts.parse(query, smarts));
        IAtomContainer queryMod = QueryAtomContainer.create(query, Expr.Type.ELEMENT, Expr.Type.DEGREE);
        String result = Smarts.generate(queryMod);
        Assertions.assertEquals("[D;#6,#7][#6D2][#6D2][#8D]", result);
    }

    @Test
    public void testDegreeWithAlipElement() {
        String smarts = "[C,N]CCO";
        IAtomContainer query = SilentChemObjectBuilder.getInstance().newAtomContainer();
        Assertions.assertTrue(Smarts.parse(query, smarts));
        IAtomContainer queryMod = QueryAtomContainer.create(query, Expr.Type.ELEMENT, Expr.Type.IS_ALIPHATIC, Expr.Type.DEGREE);
        String result = Smarts.generate(queryMod);
        Assertions.assertEquals("[D;C,N][CD2][CD2][OD]", result);
    }

    @Test
    public void testDegreeWithAlipElement2() {
        String smarts = "[C,N]CCO";
        IAtomContainer query = SilentChemObjectBuilder.getInstance().newAtomContainer();
        Assertions.assertTrue(Smarts.parse(query, smarts));
        IAtomContainer queryMod = QueryAtomContainer.create(query, Expr.Type.ALIPHATIC_ELEMENT, Expr.Type.DEGREE);
        String result = Smarts.generate(queryMod);
        Assertions.assertEquals("[D;C,N][CD2][CD2][OD]", result);
    }

    @Test
    public void testDegreeWithAlip3() {
        String smarts = "[C,N]CCO";
        IAtomContainer query = SilentChemObjectBuilder.getInstance().newAtomContainer();
        Assertions.assertTrue(Smarts.parse(query, smarts));
        IAtomContainer queryMod = QueryAtomContainer.create(query, Expr.Type.IS_ALIPHATIC, Expr.Type.DEGREE);
        String result = Smarts.generate(queryMod);
        Assertions.assertEquals("[D;A,A][AD2][AD2][AD]", result);
    }

    @Test
    public void testQueryNotChanged() {
        for (String smarts : new String[]{"[!#6]C", "[#7,#8]C", "[C;R]C"}) {
            IAtomContainer query = SilentChemObjectBuilder.getInstance().newAtomContainer();
            Assertions.assertTrue(Smarts.parse(query, smarts));
            String before = Smarts.generate(query);
            QueryAtomContainer.create(query, Expr.Type.ELEMENT);
            QueryAtomContainer.create(query, Expr.Type.ELEMENT, Expr.Type.IS_IN_RING);
            Assertions.assertEquals(before, Smarts.generate(query), smarts);
        }
    }

    @Test
    public void testNegatedElement() {
        String smarts = "[!#6]C";
        IAtomContainer query = SilentChemObjectBuilder.getInstance().newAtomContainer();
        Assertions.assertTrue(Smarts.parse(query, smarts));
        IAtomContainer queryMod = QueryAtomContainer.create(query, Expr.Type.ELEMENT);
        String result = Smarts.generate(queryMod);
        Assertions.assertEquals("[!#6][#6]", result);
    }

    @Test
    public void testRemovedTermsDoNotMakeQueryStricter() {
        // [!#7] and [#6,+1] both match ethane, without the element they match any atom
        Assertions.assertEquals("*", create("[!#7]", Expr.Type.FORMAL_CHARGE));
        Assertions.assertEquals("*", create("[#6,+1]", Expr.Type.FORMAL_CHARGE));
        // without the hydrogen count [N;!H0] is any nitrogen, not [#7!*]
        Assertions.assertEquals("[#7]", create("[N;!H0]", Expr.Type.ELEMENT));
        // [!N] matches aromatic n, [!#7] does not
        Assertions.assertEquals("*", create("[!N]", Expr.Type.ELEMENT));
        Assertions.assertEquals("[!#7]", create("[!#7]", Expr.Type.ELEMENT));
    }

    @Test
    public void testTetrahedralStereoFromQuery() throws Exception {
        IAtomContainer query = new QueryAtomContainer(SilentChemObjectBuilder.getInstance());
        Assertions.assertTrue(Smarts.parse(query, "F[C@H](Cl)Br"));
        IAtomContainer queryMod = QueryAtomContainer.create(query, Expr.Type.ELEMENT, Expr.Type.STEREOCHEMISTRY);
        int count = 0;
        for (IStereoElement se : queryMod.stereoElements())
            count++;
        Assertions.assertEquals(1, count);

        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol1 = smipar.parseSmiles("F[C@H](Cl)Br");
        IAtomContainer mol2 = smipar.parseSmiles("F[C@@H](Cl)Br");
        SmartsPattern.prepare(mol1);
        SmartsPattern.prepare(mol2);
        Pattern pattern = Pattern.findSubstructure(queryMod);
        Assertions.assertTrue(pattern.matches(mol1));
        Assertions.assertFalse(pattern.matches(mol2));
    }

    @Test
    public void testDegreeFromQueryContainer() {
        IAtomContainer query = new QueryAtomContainer(SilentChemObjectBuilder.getInstance());
        Assertions.assertTrue(Smarts.parse(query, "[C,N]CCO"));
        IAtomContainer queryMod = QueryAtomContainer.create(query, Expr.Type.ELEMENT, Expr.Type.DEGREE);
        Assertions.assertEquals("[D;#6,#7][#6D2][#6D2][#8D]", Smarts.generate(queryMod));
        // query atoms have no implicit hydrogen count, so there is no total degree
        queryMod = QueryAtomContainer.create(query, Expr.Type.ELEMENT, Expr.Type.TOTAL_DEGREE);
        Assertions.assertEquals("[#6,#7][#6][#6][#8]", Smarts.generate(queryMod));
    }

    @Test
    public void testNullBondOrder() throws Exception {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles("CC");
        mol.getBond(0).setOrder(null);
        IAtomContainer query = QueryAtomContainer.create(mol, Expr.Type.ELEMENT, Expr.Type.ALIPHATIC_ORDER);
        Assertions.assertEquals("[#6]~[#6]", Smarts.generate(query));
        query = QueryAtomContainer.create(mol, Expr.Type.ELEMENT, Expr.Type.ORDER);
        Assertions.assertEquals("[#6]~[#6]", Smarts.generate(query));
    }

    private static String create(String smarts, Expr.Type... opts) {
        IAtomContainer query = SilentChemObjectBuilder.getInstance().newAtomContainer();
        Assertions.assertTrue(Smarts.parse(query, smarts));
        return Smarts.generate(QueryAtomContainer.create(query, opts));
    }

}
