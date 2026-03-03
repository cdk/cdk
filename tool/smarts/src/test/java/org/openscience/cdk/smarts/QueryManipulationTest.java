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
