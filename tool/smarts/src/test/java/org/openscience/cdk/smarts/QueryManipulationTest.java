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
import org.openscience.cdk.isomorphism.matchers.Expr;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

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

}
